(ns com.github.meandor.authentication.utils-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.crypto :as crypto]
            [com.github.meandor.authentication.utils :as utils]))

(deftest encrypt-datas-structure
  (testing "Should turn a ticket structure into a encrypted string"
    (let [iv-example (crypto/random-iv 16)]
      (with-redefs [crypto/encrypt-aes-256 (fn [secret iv plaintext-bytes]
                                             (is (= "safe-password" secret))
                                             (is (= iv-example iv))
                                             (is (= [123 58 102 111 111 32 34 98 97 114 34 125]
                                                    (into [] plaintext-bytes)))
                                             "foo")]
        (is (= "foo"
               (utils/encrypt-data-structure {:foo "bar"} "safe-password" iv-example)))))))
