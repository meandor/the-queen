(ns com.github.meandor.authentication.utils
  (:require [com.github.meandor.crypto :as crypto]))

(defn current-time []
  (System/currentTimeMillis))

(defn encrypt-data-structure [ticket secret iv]
  (crypto/encrypt-aes-256 secret iv (byte-array (map byte (str ticket)))))
