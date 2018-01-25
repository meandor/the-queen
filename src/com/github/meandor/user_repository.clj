(ns com.github.meandor.user-repository
  (:require [com.github.meandor.redis-component :as rc]
            [taoensso.carmine :as car]
            [clojure.string :as str]))

(defn new-user-id [redis-component]
  (if-let [user-id (rc/wcar* redis-component (car/get "user-id"))]
    (str "user:" (inc (read-string user-id)))
    "user:1"))

(defn register-user [redis-component user]
  (let [user-id (new-user-id redis-component)
        groups (map #(str "groups:" %) (:groups user))]
    (rc/wcar* redis-component
              (doseq [[k v] (update user :groups (fn [old] (str/join ":" old)))]
                (car/hset user-id k v))
              (doseq [group groups]
                (car/sadd group user-id))
              (car/incr "user-id"))
    user-id))

