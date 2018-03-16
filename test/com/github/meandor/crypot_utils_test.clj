(ns com.github.meandor.crypot-utils-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.crypto-utils :as cu]))

(def plaintext "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern")

(deftest sha-256-test
  (testing "Should calculate correct hash"
    (is (= "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 "foobar"))))
    (is (= "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 plaintext))))))

(deftest random-bytes-test
  (testing "Should yield a random byte array with given length"
    (is (= 16
           (count (cu/random-bytes 16))))
    (is (not= (into [] (cu/random-bytes 16))
              (into [] (cu/random-bytes 16))))))

(defn- round [number]
  (Math/round (double number)))

(deftest aes-encryption-decryption-test
  (testing "Should encrypt and decrypt something"
    (let [iv (cu/iv 16)                                     ; aes-256 block length is 128 bits
          cipher (cu/encrypt-aes-256 "secret" iv (.getBytes plaintext))]
      (is (= (* 16 (round (/ (count (.getBytes plaintext)) 16)))
             (count cipher)))
      (is (= plaintext
             (apply str (map char (cu/decrypt-aes-256 "secret" iv cipher))))))))
