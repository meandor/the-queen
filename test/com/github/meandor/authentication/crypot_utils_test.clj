(ns com.github.meandor.authentication.crypot-utils-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.crypto-utils :as cu]))

(deftest sha-256-test
  (testing "Should calculate correct hash"
    (is (= "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 "foobar"))))
    (is (= "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
           (cu/byte-array->unsigned-hex-string (cu/sha-256 "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern"))))))

(deftest salt-test
  (testing "Should yield a random salt with given length"
    (is (= 16
           (count (cu/salt 16))))
    (is (not= (into [] (cu/salt 16))
              (into [] (cu/salt 16))))))

(deftest aes-secret-key-test
  (testing "Should generate a salted cipher secret key for aes with 128 bits"
    (with-redefs [cu/salt (fn [length]
                            (is (= 32 length))
                            (byte-array length))]
      (is (= "b4b839071a9e2a9ca7730410c1d680ef3dceda331811ddf4e9a094c3e477c000"
             (cu/byte-array->unsigned-hex-string (.getEncoded (cu/aes-secret-key "foobar" 256))))))))

#_(deftest aes-encryption-keys-test
    (testing "Should create encryption keys"
      (is (= []
             (cu/aes-encryption-keys 128 "foobar" (byte-array 16))))
      ))
