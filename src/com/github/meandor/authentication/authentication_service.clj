(ns com.github.meandor.authentication.authentication-service
  (:require [com.github.meandor.user.repository :as ur]
            [com.github.meandor.authentication.ticket-granting-service :as tgs]
            [com.github.meandor.authentication.utils :as utils]))

(defn authentication-reply [service-principal lifetime session-key client-principal tgs-secret]
  (let [now (utils/current-time)]
    {:service-principal      service-principal
     :timestamp              now
     :lifetime               (+ now lifetime)
     :session-key            session-key
     :ticket-granting-ticket (into [] (utils/encrypt-data-structure
                                        (tgs/ticket-granting-ticket client-principal service-principal session-key)
                                        tgs-secret))}))

(defn initial-authentication-request [{:keys [config user-db]} client-principal service-principal lifetime]
  (when (and (ur/find-user user-db client-principal)
             (= service-principal (get-in config [:tgs :principal])))
    (let [session-key (tgs/session-key)]
      (utils/encrypt-data-structure
        (authentication-reply service-principal lifetime session-key client-principal (get-in config [:tgs :secret]))
        (:password (ur/find-user user-db client-principal))))))
