(ns com.github.meandor.authentication.utils
  (:require [com.github.meandor.crypto :as crypto]
            [clojure.edn :as edn]))

(defn current-time []
  (System/currentTimeMillis))

(def IV_BYTE_SIZE 16)

(defn encrypt-data-structure [data-structure-plain secret]
  (let [iv (crypto/random-iv IV_BYTE_SIZE)]
    (->> (str data-structure-plain)
         (map byte)
         (byte-array)
         (crypto/encrypt-aes-256 secret iv)
         (concat (.getIV iv))
         (byte-array))))

(defn decrypt-data-structure [data-structure-cipher secret]
  (let [[iv cipher] (split-at IV_BYTE_SIZE data-structure-cipher)]
    (->> (byte-array cipher)
         (crypto/decrypt-aes-256 secret (crypto/iv (byte-array iv)))
         (new String)
         (edn/read-string))))
