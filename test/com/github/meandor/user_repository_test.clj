(ns com.github.meandor.user-repository-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.user-repository :as ur]
            [com.github.meandor.redis-component :as rc]
            [taoensso.carmine :as car])
  (:import (redis.embedded RedisServer)))

(def redis-component {:connection {:pool {} :spec {}}})

(defn- with-db-fixture [f]
  (let [db (RedisServer.)]
    (.start db)
    (f)
    (.stop db)))

(defn- with-cleared-db [f]
  (rc/wcar* redis-component (car/flushall))
  (f))

(use-fixtures :once with-db-fixture)
(use-fixtures :each with-cleared-db)

(deftest new-user-id-test
  (testing "get next higher number for user-id"
    (is (= "user:1"
           (ur/new-user-id redis-component)))

    (rc/wcar* redis-component (car/set "user-id" 3))
    (is (= "user:4"
           (ur/new-user-id redis-component)))))

(deftest register-user-test
  (testing "insert new user to db and add all other relations for user"
    (let [new-user-id (ur/register-user redis-component {:email     "foo@bar.com"
                                                         :name      "foobar"
                                                         :firstname "foo"
                                                         :lastname  "bar"
                                                         :password  "baz"
                                                         :groups    ["bak" "42"]})]
      (is (= "user:1"
             new-user-id))

      (is (= ["email" "foo@bar.com"
              "name" "foobar"
              "firstname" "foo"
              "lastname" "bar"
              "password" "baz"
              "groups" "bak:42"]
             (rc/wcar* redis-component (car/hgetall new-user-id))))

      (is (= ["user:1"]
             (rc/wcar* redis-component (car/smembers "groups:bak"))))

      (is (= ["user:1"]
             (rc/wcar* redis-component (car/smembers "groups:42"))))))

  (testing "insert another new user to db with existing user"
    (let [new-user-id (ur/register-user redis-component {:email     "foo@bar2.com"
                                                         :name      "foobar2"
                                                         :firstname "foo"
                                                         :lastname  "bar2"
                                                         :password  "baz2"
                                                         :groups    ["42"]})]
      (is (= "user:2"
             new-user-id))

      (is (= ["email" "foo@bar2.com"
              "name" "foobar2"
              "firstname" "foo"
              "lastname" "bar2"
              "password" "baz2"
              "groups" "42"]
             (rc/wcar* redis-component (car/hgetall new-user-id))))

      (is (= ["user:1"]
             (rc/wcar* redis-component (car/smembers "groups:bak"))))

      (is (= (set ["user:1" "user:2"])
             (set (rc/wcar* redis-component (car/smembers "groups:42"))))))))

(deftest redis-vector->user-map
  (testing "transformation of vector into map with keywordizing"
    (is (= {:a "foo" :bar "baz" :groups ["a" "b"]}
           (ur/redis-user-vector->user-map ["a" "foo" "bar" "baz" "groups" "a:b"])))
    (is (= {:a "foo" :bar "baz" :groups ["a"]}
           (ur/redis-user-vector->user-map ["a" "foo" "bar" "baz" "groups" "a"])))))

(deftest find-user-by-id-test
  (testing "return nil while looking for a non existent user"
    (is (= nil
           (ur/find-user redis-component "foobar"))))

  (testing "find an existing user"
    (rc/wcar* redis-component (car/hmset "user:1" "email" "foo@bar2.com" "name" "foobar2"
                                         "firstname" "foo" "lastname" "bar2"
                                         "password" "baz2" "groups" "42"))
    (is (= {:email     "foo@bar2.com"
            :name      "foobar2"
            :firstname "foo"
            :lastname  "bar2"
            :password  "baz2"
            :groups    ["42"]}
           (ur/find-user redis-component "1")))))

(deftest all-user-ids-test
  (testing "get all user-ids"
    (is (= []
           (ur/all-user-ids redis-component)))

    (rc/wcar* redis-component
              (car/hmset "user:1" "email" "foo@bar2.com" "name" "foobar2"
                         "firstname" "foo" "lastname" "bar2"
                         "password" "baz2" "groups" "42")
              (car/hmset "user:2" "email" "foo@bar2.com" "name" "foobar2"
                         "firstname" "foo" "lastname" "bar2"
                         "password" "baz2" "groups" "42"))

    (is (= (set ["1" "2"])
           (set (ur/all-user-ids redis-component))))))

(deftest find-user-groups-test
  (testing "find all groups for user"
    (ur/register-user redis-component {:email     "foo@bar.com"
                                       :name      "foobar"
                                       :firstname "foo"
                                       :lastname  "bar"
                                       :password  "baz"
                                       :groups    ["bak" "42"]})

    (is (= ["groups:bak" "groups:42"]
           (ur/find-user-groups redis-component "1")))))

(deftest remove-user-test
  (testing "removal of a user"
    (ur/register-user redis-component {:email     "foo@bar.com"
                                       :name      "foobar"
                                       :firstname "foo"
                                       :lastname  "bar"
                                       :password  "baz"
                                       :groups    ["bak" "42"]})

    (is (= ["user:1"]
           (rc/wcar* redis-component (car/smembers "groups:bak"))))

    (is (= ["user:1"]
           (rc/wcar* redis-component (car/smembers "groups:42"))))

    (is (= 1
           (ur/delete-user redis-component "1")))

    (is (= []
           (rc/wcar* redis-component (car/hgetall "user:1"))))

    (is (= []
           (rc/wcar* redis-component (car/smembers "groups:bak"))))

    (is (= []
           (rc/wcar* redis-component (car/smembers "groups:42"))))))

(deftest all-group-ids-test
  (testing "get all group-ids"
    (is (= []
           (ur/all-group-ids redis-component)))

    (rc/wcar* redis-component
              (car/sadd "groups:1" "foobar" "42")
              (car/sadd "groups:42" "bar" "foo"))

    (is (= (set ["groups:1" "groups:42"])
           (set (ur/all-group-ids redis-component))))))