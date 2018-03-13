(ns com.github.meandor.core
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as c]
            [de.otto.goo.goo :as goo]
            [iapetos.collector.jvm :as jvm]
            [de.otto.tesla.serving-with-httpkit :as httpkit]
            [de.otto.tesla.system :as system]
            [de.otto.tesla.stateful.app-status :as app-status]
            [de.otto.tesla.stateful.metering :as metering])
  (:gen-class))

(defn- authenticated? [config user password]
  (and (= user (get-in config [:authentication :username]))
       (= password (get-in config [:authentication :password]))))

(defn the-queen-system [runtime-config]
  (-> (system/base-system runtime-config)
      (assoc
        :app-status (c/using (app-status/new-app-status authenticated?) [:config :handler])
        :metering (c/using (metering/new-metering authenticated?) [:config :handler]))
      (httpkit/add-server :app-status :metering)))

(defonce _ (jvm/initialize (goo/snapshot)))
(defonce _ (Thread/setDefaultUncaughtExceptionHandler
             (reify Thread$UncaughtExceptionHandler
               (uncaughtException [_ thread ex]
                 (log/error ex "Uncaught exception on " (.getName thread))))))

(defn -main [& _]
  (system/start (the-queen-system {})))
