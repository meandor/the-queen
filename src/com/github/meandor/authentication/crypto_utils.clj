(ns com.github.meandor.authentication.crypto-utils
  (:import (java.nio.charset StandardCharsets)
           (java.security MessageDigest SecureRandom)))

(defn- byte->hex-string [byte]
  (Integer/toHexString (int (bit-and byte 0xff))))

(defn- byte-array->hex-string [bytes]
  (reduce (fn [acc byte] (str acc (byte->hex-string byte))) "" bytes))

(defn sha-256 [^String clear]
  (-> (MessageDigest/getInstance "SHA-256")
      (.digest (.getBytes clear StandardCharsets/UTF_8))
      (byte-array->hex-string)))

(defn salt [length]
  (let [salt-byte-array (byte-array length)]
    (.nextBytes (new SecureRandom) salt-byte-array)
    salt-byte-array))

(defn aes-256 [secret-key plaintext]
  ;byte[] key = null; // TODO
  ;byte[] input = null; // TODO
  ;byte[] output = null;
  ;SecretKeySpec keySpec = null;
  ;keySpec = new SecretKeySpec(key, "AES");
  ;Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
  ;cipher.init(Cipher.ENCRYPT_MODE, keySpec);
  ;output = cipher.doFinal(input)
  )