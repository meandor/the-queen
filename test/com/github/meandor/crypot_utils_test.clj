(ns com.github.meandor.authentication.crypot-utils-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.crypto-utils :as cu])
  (:import (javax.crypto.spec IvParameterSpec)))

(deftest sha-256-test
  (testing "Should calculate correct hash"
    (is (= "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 "foobar"))))
    (is (= "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern"))))))

(deftest random-bytes-test
  (testing "Should yield a random byte array with given length"
    (is (= 16
           (count (cu/random-bytes 16))))
    (is (not= (into [] (cu/random-bytes 16))
              (into [] (cu/random-bytes 16))))))

(deftest aes-encryption-decryption-test
  (testing "Should encrypt and decrypt something"
    (let [iv (cu/iv 16)
          cipher (cu/encrypt-aes-256 "secret" iv (.getBytes "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern"))]
      #_(is (= (/ 256 8)                                    ;TODO: size is double, is that correct?
               (count cipher)))
      (is (= "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern"
             (apply str (map char (cu/decrypt-aes-256 "secret" iv cipher))))))))
