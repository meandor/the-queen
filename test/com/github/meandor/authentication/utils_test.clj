(ns com.github.meandor.authentication.utils-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.crypto :as crypto]
            [com.github.meandor.authentication.utils :as utils]))

(def foo-map-string-bytes [123 58 102 111 111 32 34 98 97 114 34 125])

(defn assumme-simple-aes256-encryption [secret iv plaintext-bytes]
  (is (= "safe-password" secret))
  (is (bytes? (.getIV iv)))
  (is (= foo-map-string-bytes
         (into [] plaintext-bytes)))
  (byte-array foo-map-string-bytes))

(defn assumme-simple-aes256-decryption [secret iv cipher]
  (is (= "safe-password" secret))
  (is (bytes? (.getIV iv)))
  (is (= foo-map-string-bytes
         (into [] cipher)))
  (byte-array foo-map-string-bytes))

(deftest encrypt-decrypt-datas-structure
  (testing "Should turn a ticket structure into a encrypted string"
    (with-redefs [crypto/encrypt-aes-256 assumme-simple-aes256-encryption
                  crypto/decrypt-aes-256 assumme-simple-aes256-decryption]
      (is (= {:foo "bar"}
             (utils/decrypt-data-structure (utils/encrypt-data-structure {:foo "bar"} "safe-password") "safe-password"))))))
