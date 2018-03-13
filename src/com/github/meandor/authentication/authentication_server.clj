(ns com.github.meandor.authentication.authentication-server)

(defn session-key []
  (* 100000000 (Math/random)))

(defn current-time []
  (System/currentTimeMillis))

(defn ticket-granting-ticket [client-principal service-principal lifetime]
  {:client-principal  client-principal
   :service-principal service-principal
   :datetime          (current-time)
   :lifetime          lifetime
   :session-key       (session-key)})
