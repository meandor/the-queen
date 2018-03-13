(ns com.github.meandor.user-repository
  (:require [com.github.meandor.redis-component :as rc]
            [taoensso.carmine :as car]
            [clojure.walk :as w]
            [clojure.string :as str]))

(defn user-id->user-key [user-id]
  (str "user:" user-id))

(defn user-key->user-id [user-key]
  (str/replace user-key "user:" ""))

(defn group-id->group-key [group-id]
  (str "groups:" group-id))

(defn group-key->group-id [group-key]
  (str/replace group-key "groups:" ""))

(def CURRENT_FREE_USER_ID_KEY "user-id")

(defn new-user-key [redis-component]
  (if-let [user-id (rc/wcar* redis-component (car/get CURRENT_FREE_USER_ID_KEY))]
    (user-id->user-key (inc (read-string user-id)))
    (user-id->user-key 1)))

(def LIST_SEPARATOR "/")

(defn combined-group-keys [group-ids]
  (->> group-ids
       (map group-id->group-key)
       (str/join LIST_SEPARATOR)))

(defn- add-user-to-db [redis-component user-key user]
  (rc/wcar* redis-component
            (doseq [[k v] (update user :groups combined-group-keys)]
              (car/hset user-key k v))
            (doseq [group-key (map group-id->group-key (:groups user))]
              (car/sadd group-key user-key))))

(defn register-user [redis-component user]
  (let [user-key (new-user-key redis-component)]
    (add-user-to-db redis-component user-key user)
    (rc/wcar* redis-component (car/incr CURRENT_FREE_USER_ID_KEY))
    (user-key->user-id user-key)))

(defn redis-user-vector->user-map [vector]
  (when (not (empty? vector))
    (-> (w/keywordize-keys (apply hash-map vector))
        (update :groups (fn [old] (str/split old #"/"))))))

(defn find-user [redis-component user-id]
  (redis-user-vector->user-map (rc/wcar* redis-component (car/hgetall (user-id->user-key user-id)))))

(defn- user-groups-with-keys [redis-component user-key]
  (-> redis-component
      (rc/wcar* (car/hget user-key "groups"))
      (str/split #"/")))

(defn find-user-groups [redis-component user-id]
  (->> (user-groups-with-keys redis-component (user-id->user-key user-id))
       (map group-key->group-id)))

(defn- delete-key [redis-component key]
  (rc/wcar* redis-component (car/del key)))

(defn delete-user [redis-component user-id]
  (let [user-key (user-id->user-key user-id)]
    (rc/wcar* redis-component (doseq [group-key (user-groups-with-keys redis-component user-key)]
                                (car/srem group-key user-key)))
    (delete-key redis-component user-key)))

(defn update-user [redis-component user-id user]
  (delete-user redis-component user-id)
  (add-user-to-db redis-component (user-id->user-key user-id) user)
  user-id)

(defn find-group-member [redis-component group-id]
  (map #(str/replace % "user:" "") (rc/wcar* redis-component (car/smembers (str "groups:" group-id)))))

(defn all-group-ids [redis-component]
  (->> (rc/wcar* redis-component (car/keys "groups:*"))
       (map #(str/replace % "groups:" ""))))

(defn delete-group [redis-component group-id]
  (delete-key redis-component (str "groups:" group-id)))
