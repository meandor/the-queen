(ns com.github.meandor.authentication.ticket-granting-service
  (:require [com.github.meandor.crypto :as crypto]))

(defn session-key []
  (* 100000000 (Math/random)))

(defn current-time []
  (System/currentTimeMillis))

(def TWELVE_HOURS_IN_MILLIS 43200000)

(defn ticket-granting-ticket [client-principal service-principal session-key]
  (let [now (current-time)]
    {:client-principal  client-principal
     :service-principal service-principal
     :datetime          now
     :lifetime          (+ now TWELVE_HOURS_IN_MILLIS)
     :session-key       session-key}))

(defn ticket [session-key nonce ticket-granting-ticket]
  {:session-key            session-key
   :nonce                  nonce
   :ticket-granting-ticket ticket-granting-ticket})

(defn encrypt-ticket [ticket secret iv]
  (crypto/encrypt-aes-256 secret iv (map byte (str ticket))))
