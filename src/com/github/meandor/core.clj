(ns com.github.meandor.core
  (:require [de.otto.tesla.system :as system]
            [de.otto.tesla.serving-with-httpkit :as httpkit]
            [clojure.tools.logging :as log]))

(defn the-queen-system [runtime-config]
  (-> (system/base-system (merge {:name "the-queen"} runtime-config))
      (httpkit/add-server)))

(defonce _ (Thread/setDefaultUncaughtExceptionHandler
             (reify Thread$UncaughtExceptionHandler
               (uncaughtException [_ thread ex]
                 (log/error ex "Uncaught exception on " (.getName thread))))))

(defn -main [& _]
  (system/start (the-queen-system {})))
