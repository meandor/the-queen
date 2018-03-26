(ns com.github.meandor.authentication.utils
  (:require [com.github.meandor.crypto :as crypto]
            [clojure.edn :as edn]))

(defn current-time []
  (System/currentTimeMillis))

(defn encrypt-data-structure [data-structure-plain secret]
  (let [iv (crypto/random-iv 16)
        iv-bytes (.getIV iv)]
    (byte-array (concat iv-bytes (crypto/encrypt-aes-256 secret iv (byte-array (map byte (str data-structure-plain))))))))

(defn decrypt-data-structure [data-structure-cipher secret]
  (let [[iv cipher] (split-at 16 data-structure-cipher)]
    (edn/read-string (new String (crypto/decrypt-aes-256 secret (crypto/iv (byte-array iv)) (byte-array cipher))))))
