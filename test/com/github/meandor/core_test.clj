(ns com.github.meandor.core-test
  (:require [clojure.test :refer :all]
            [de.otto.tesla.util.test-utils :as utils]
            [com.github.meandor.core :as core]))

(deftest system-startup-test
  (testing "starting the system"
    (utils/with-started [system (core/the-queen-system {})]
                        (is (not (nil? system))))
    ))
