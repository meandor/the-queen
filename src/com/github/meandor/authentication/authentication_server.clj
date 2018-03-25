(ns com.github.meandor.authentication.authentication-server)

(defn session-key []
  (* 100000000 (Math/random)))

(defn current-time []
  (System/currentTimeMillis))

(def TWELVE_HOURS_IN_MILLIS 43200000)

(defn ticket-granting-ticket [client-principal service-principal]
  (let [now (current-time)]
    {:client-principal  client-principal
     :service-principal service-principal
     :datetime          now
     :lifetime          (+ now TWELVE_HOURS_IN_MILLIS)
     :session-key       (session-key)}))
