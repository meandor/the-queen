(ns com.github.meandor.authentication.authentication-server-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.authentication-server :as as]))

(deftest sha-256-test
  (testing "Should calculate correct hash"
    (is (= "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"
           (as/sha-256 "foobar")))

    (is (= "688787d8ff144c502c7f5cffaafe2cc588d86079f9de88304c26b0cb99ce91c6"
           (as/sha-256 "asd")))))
