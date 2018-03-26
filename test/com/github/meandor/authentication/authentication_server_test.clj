(ns com.github.meandor.authentication.authentication-server-test
  (:require [clojure.test :refer :all]
            [com.github.meandor.authentication.authentication-service :as as]
            [com.github.meandor.user.repository :as ur]
            [com.github.meandor.authentication.utils :as utils]
            [com.github.meandor.authentication.ticket-granting-service :as tgs]))

(def sample-config
  {:tgs {:principal "TGSName"
         :secret    "keep-quiet"}})

(deftest initial-authentication-request
  (testing "Should return a authentication reply on valid initial authentication request"
    (with-redefs [utils/current-time (constantly 42)
                  tgs/session-key (constantly 1337)
                  ur/find-user (fn [redis user-id]
                                 (is (= "redis-db" redis))
                                 (is (= "foo-user" user-id))
                                 {:username "foo-user" :password "foobar"})]
      (let [authentication-reply-cipher (as/initial-authentication-request {:user-db "redis-db" :config sample-config} "foo-user" "TGSName" 300)
            authentication-reply-plain (utils/decrypt-data-structure authentication-reply-cipher "foobar")]

        (is (bytes? authentication-reply-cipher))

        (is (< 100
               (count authentication-reply-cipher)))

        (is (= {:lifetime          342
                :service-principal "TGSName"
                :session-key       1337
                :timestamp         42}
               (dissoc authentication-reply-plain :ticket-granting-ticket)))

        (is (= {:client-principal  "foo-user"
                :lifetime          (+ 42 (* 12 1000 60 60))
                :service-principal "TGSName"
                :session-key       1337
                :timestamp         42}
               (utils/decrypt-data-structure (:ticket-granting-ticket authentication-reply-plain) "keep-quiet")))))))
