(ns com.github.meandor.authentication.crypto-utils
  (:import (java.nio.charset StandardCharsets)
           (java.security MessageDigest SecureRandom)
           (javax.crypto SecretKeyFactory)
           (javax.crypto.spec PBEKeySpec SecretKeySpec)))

(defn byte-array->unsigned-hex-string [bytes-values]
  (->> (map #(format "%02x" (int (bit-and % 0xff))) bytes-values)
       (apply str)))

(defn sha-256 [^String plaintext]
  (-> (MessageDigest/getInstance "SHA-256")
      (.digest (.getBytes plaintext StandardCharsets/UTF_8))))

(defn salt [length]
  (let [salt-byte-array (byte-array length)]                ;TODO: (doto)
    (.nextBytes (new SecureRandom) salt-byte-array)
    salt-byte-array))

(def PBKDF2_ITERATIONS 15789)
(defn aes-secret-key [plain size]
  (let [random-salt (salt (/ size 8))                       ;salt should be >= hash length in bytes
        spec (new PBEKeySpec (char-array plain) random-salt PBKDF2_ITERATIONS size)]
    (-> (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA256")
        (.generateSecret spec)
        (.getEncoded)
        (SecretKeySpec. "AES"))))

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