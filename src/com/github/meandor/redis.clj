(ns com.github.meandor.redis
  (:require [taoensso.carmine :as car]))

(def server1-conn {:pool {} :spec {}})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))