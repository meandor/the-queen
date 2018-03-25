(ns com.github.meandor.authentication.ticket-granting-service-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.ticket-granting-service :as tgs]))

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
