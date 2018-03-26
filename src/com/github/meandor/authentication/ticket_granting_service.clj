(ns com.github.meandor.authentication.ticket-granting-service
  (:require [com.github.meandor.authentication.utils :as utils]))

(defn session-key []
  (* 100000000 (Math/random)))

(def TWELVE_HOURS_IN_MILLIS 43200000)

(defn ticket-granting-ticket [client-principal service-principal session-key]
  (let [now (utils/current-time)]
    {:client-principal  client-principal
     :service-principal service-principal
     :timestamp         now
     :lifetime          (+ now TWELVE_HOURS_IN_MILLIS)
     :session-key       session-key}))
