(ns com.github.meandor.authentication.ticket-granting-service-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.ticket-granting-service :as tgs]
            [com.github.meandor.crypto :as crypto]))

(deftest ticket-granting-ticket-structure
  (testing "Should create a ticket granting ticket data structure"
    (with-redefs [tgs/current-time (constantly 1337)]
      (is (= {:client-principal  "foobar"
              :service-principal "fooTGS"
              :datetime          1337
              :lifetime          (+ 1337 (* 12 1000 60 60))
              :session-key       "foobaz"}
             (tgs/ticket-granting-ticket "foobar" "fooTGS" "foobaz")))

      (is (= {:client-principal  "foobar2"
              :service-principal "fooTGS2"
              :datetime          1337
              :lifetime          (+ 1337 (* 12 1000 60 60))
              :session-key       "asd"}
             (tgs/ticket-granting-ticket "foobar2" "fooTGS2" "asd"))))))

(deftest ticket-structure
  (testing "Should create a ticket data structure"
    (is (= {:session-key            "session-key"
            :nonce                  42
            :ticket-granting-ticket "encrypted ticket granting ticket"}
           (tgs/ticket "session-key" 42 "encrypted ticket granting ticket")))

    (is (= {:session-key            "session-key2"
            :nonce                  1337
            :ticket-granting-ticket "encrypted"}
           (tgs/ticket "session-key2" 1337 "encrypted")))))

(deftest encrypt-ticket-structure
  (testing "Should turn a ticket structure into a encrypted string"
    (let [iv-example (crypto/iv 16)]
      (with-redefs [crypto/encrypt-aes-256 (fn [secret iv plaintext-bytes]
                                             (is (= "safe-password" secret))
                                             (is (= iv-example iv))
                                             (is (= [123 58 102 111 111 32 34 98 97 114 34 125]
                                                    plaintext-bytes))
                                             "foo")]
        (is (= "foo"
               (tgs/encrypt-ticket {:foo "bar"} "safe-password" iv-example)))))))
