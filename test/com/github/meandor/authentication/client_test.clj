(ns com.github.meandor.authentication.client-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.client :as cl]))

(deftest nonce-test
  (testing "Should create a nonce"
    (is (number? (cl/nonce)))
    (is (not= (cl/nonce)
              (cl/nonce)))
    (is (not= (cl/nonce)
              (cl/nonce)))))
