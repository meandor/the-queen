(ns com.github.meandor.redis-test
  (:require [clojure.test :refer :all]
            [taoensso.carmine :as car]
            [com.github.meandor.redis :as redis])
  (:import (redis.embedded RedisServer)))

(defn with-db-fixture [f]
  (let [db (RedisServer.)]
    (.start db)
    (f)
    (.stop db)))

(defn with-flushed-db [f]
  (redis/wcar* (car/flushall))
  (f))

(use-fixtures :once with-db-fixture)
(use-fixtures :each with-flushed-db)

(deftest register-user-test
  (testing "connect to redis and add test key"
    (is (nil? (redis/wcar* (car/get "foo"))))

    (redis/wcar* (car/set "foo" "bar"))
    (is (= "bar"
           (redis/wcar* (car/get "foo"))))))