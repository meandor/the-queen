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

      (is (= ["user:2" "user:1"]
             (rc/wcar* redis-component (car/smembers "groups:42")))))))