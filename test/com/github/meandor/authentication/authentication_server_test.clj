(ns com.github.meandor.authentication.authentication-server-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.authentication-server :as as]))

(deftest ticket-structure
  (testing "Should create a ticket granting ticket data structure"
    (with-redefs [as/session-key (constantly "foobaz")
                  as/current-time (constantly 1337)]
      (is (= {:client-principal  "foobar"
              :service-principal "fooTGS"
              :datetime          1337
              :lifetime          (+ 1337 (* 12 1000 60 60))
              :session-key       "foobaz"}
             (as/ticket-granting-ticket "foobar" "fooTGS")))

      (is (= {:client-principal  "foobar2"
              :service-principal "fooTGS2"
              :datetime          1337
              :lifetime          (+ 1337 (* 12 1000 60 60))
              :session-key       "foobaz"}
             (as/ticket-granting-ticket "foobar2" "fooTGS2"))))))
