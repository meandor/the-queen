(ns com.github.meandor.authentication.authentication-server
  (:import (java.security MessageDigest)
           (java.nio.charset StandardCharsets)))

(defn- byte->hex-string [byte]
  (Integer/toHexString (int (bit-and byte 0xff))))

(defn- byte-array->hex-string [bytes]
  (reduce (fn [acc byte] (str acc (byte->hex-string byte))) "" bytes))

(defn sha-256 [^String clear]
  (-> (MessageDigest/getInstance "SHA-256")
      (.digest (.getBytes clear StandardCharsets/UTF_8))
      (byte-array->hex-string)))
