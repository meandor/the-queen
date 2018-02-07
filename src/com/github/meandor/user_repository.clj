(ns com.github.meandor.user-repository
  (:require [com.github.meandor.redis-component :as rc]
            [taoensso.carmine :as car]
            [clojure.walk :as w]
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
    (str/replace user-id "user:" "")))

(defn redis-user-vector->user-map [vector]
  (if (= [] vector)
    nil
    (-> (w/keywordize-keys (apply hash-map vector))
        (update :groups (fn [old] (str/split old #":"))))))

(defn find-user [redis-component user-id]
  (redis-user-vector->user-map (rc/wcar* redis-component (car/hgetall (str "user:" user-id)))))

(defn all-user-ids [redis-component]
  (map #(str/replace % "user:" "") (rc/wcar* redis-component (car/keys "user:*"))))

(defn- delete-key [redis-component key]
  (rc/wcar* redis-component (car/del key)))

(defn find-user-groups [redis-component user-id]
  (->> (str/split (rc/wcar* redis-component (car/hget (str "user:" user-id) "groups")) #":")
       (map #(str "groups:" %))))

(defn delete-user [redis-component user-id]
  (let [group-ids (find-user-groups redis-component user-id)]
    (rc/wcar* redis-component (doseq [group-id group-ids]
                                (car/srem group-id (str "user:" user-id)))))
  (delete-key redis-component (str "user:" user-id)))

(defn find-group-member [redis-component group-id]
  (map #(str/replace % "user:" "") (rc/wcar* redis-component (car/smembers (str "groups:" group-id)))))

(defn all-group-ids [redis-component]
  (->> (rc/wcar* redis-component (car/keys "groups:*"))
       (map #(str/replace % "groups:" ""))))

(defn delete-group [redis-component group-id]
  (delete-key redis-component (str "groups:" group-id)))

(defn update-user [redis-component user-id user]
  (let [groups (map #(str "groups:" %) (:groups user))
        internal-user-id (str "user:" user-id)
        old-groups (find-user-groups redis-component user-id)]
    (rc/wcar* redis-component
              (doseq [[k v] (update user :groups (fn [old] (str/join ":" old)))]
                (car/hset internal-user-id k v))
              (doseq [group groups]
                (car/sadd group internal-user-id))
              (doseq [group old-groups]
                (car/srem group internal-user-id))
              (car/incr "user-id"))
    user-id))
