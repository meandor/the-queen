(ns com.github.meandor.authentication.authentication-server-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.authentication-server :as as]))

(deftest ticket-granting-ticket-test
  (testing "Should create a ticket granting ticket"
    (with-redefs [as/session-key (constantly "foobaz")
                  as/current-time (constantly 1337)]
      (is (= {:client-principal  "foobar"
              :service-principal "fooTGS"
              :datetime          1337
              :lifetime          42
              :session-key       "foobaz"}
             (as/ticket-granting-ticket "foobar" "fooTGS" 42)))

      (is (= {:client-principal  "foobar2"
              :service-principal "fooTGS2"
              :datetime          1337
              :lifetime          12
              :session-key       "foobaz"}
             (as/ticket-granting-ticket "foobar2" "fooTGS2" 12))))))
