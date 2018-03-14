(ns com.github.meandor.authentication.crypto-utils
  (:import (java.nio.charset StandardCharsets)
           (java.security MessageDigest SecureRandom)
           (javax.crypto Cipher)
           (javax.crypto.spec SecretKeySpec IvParameterSpec)))

(defn byte-array->unsigned-hex-string [bytes-values]
  (->> (map #(format "%02x" (int (bit-and % 0xff))) bytes-values)
       (apply str)))

(defn sha-256 [^String plaintext]
  (-> (MessageDigest/getInstance "SHA-256")
      (.digest (.getBytes plaintext StandardCharsets/UTF_8))))

(defn random-bytes [length]
  (let [random-byte-array (byte-array length)]
    (.nextBytes (new SecureRandom) random-byte-array)
    random-byte-array))

(defn iv [length]
  (IvParameterSpec. (random-bytes length)))

(defn encrypt-aes-256 [secret iv plaintext-bytes]
  (let [^Cipher cipher (Cipher/getInstance "AES/CBC/PKCS5Padding")
        hashed-secret-key (SecretKeySpec. (sha-256 secret) "AES")]
    (.init cipher Cipher/ENCRYPT_MODE hashed-secret-key iv)
    (.doFinal cipher plaintext-bytes)))

(defn decrypt-aes-256 [secret iv encrypted-bytes]
  (let [cipher (Cipher/getInstance "AES/CBC/PKCS5Padding")
        hashed-secret-key (SecretKeySpec. (sha-256 secret) "AES")]
    (.init cipher Cipher/DECRYPT_MODE hashed-secret-key iv)
    (.doFinal cipher encrypted-bytes)))
