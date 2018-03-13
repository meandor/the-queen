(ns com.github.meandor.core
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as c]
            [de.otto.goo.goo :as goo]
            [iapetos.collector.jvm :as jvm]
            [de.otto.tesla.serving-with-httpkit :as httpkit]
            [de.otto.tesla.system :as system])
  (:gen-class))

(defn the-queen-system [runtime-config]
  (-> (system/base-system runtime-config)
      ; (assoc)
      (httpkit/add-server)))

(defonce _ (jvm/initialize (goo/snapshot)))
(defonce _ (Thread/setDefaultUncaughtExceptionHandler
             (reify Thread$UncaughtExceptionHandler
               (uncaughtException [_ thread ex]
                 (log/error ex "Uncaught exception on " (.getName thread))))))

(defn -main [& _]
  (system/start (the-queen-system {})))
