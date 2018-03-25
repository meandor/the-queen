(ns com.github.meandor.crypot-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.crypto :as crypto]))

(def plaintext "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern")

(deftest sha-256-test
  (testing "Should calculate correct hash"
    (is (= "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"
           (crypto/byte-array->unsigned-hex-string (crypto/sha-256 "foobar"))))
    (is (= "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
           (crypto/byte-array->unsigned-hex-string (crypto/sha-256 plaintext))))))

(deftest random-bytes-test
  (testing "Should yield a random byte array with given length"
    (is (= 16
           (count (crypto/random-bytes 16))))
    (is (not= (into [] (crypto/random-bytes 16))
              (into [] (crypto/random-bytes 16))))))

(defn- round [number]
  (Math/round (double number)))

(deftest aes-encryption-decryption-test
  (testing "Should encrypt and decrypt something"
    (let [iv (crypto/iv 16)                                 ; aes-256 block length is 128 bits
          cipher (crypto/encrypt-aes-256 "secret" iv (.getBytes plaintext))]
      (is (= (* 16 (round (/ (count (.getBytes plaintext)) 16)))
             (count cipher)))
      (is (= plaintext
             (apply str (map char (crypto/decrypt-aes-256 "secret" iv cipher))))))))
