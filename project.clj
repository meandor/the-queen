(defproject com.github.meandor/the-queen "0.1.0-SNAPSHOT"
  :description "OAuth2 SSO user registry"
  :url "https://github.com/meandor/the-queen"
  :license {:name "Apache Version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:name "git"
        :url  "https://github.com/meandor/the-queen.git"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.github.meandor/gimme "0.1.1"]
                 [de.otto/tesla-microservice "0.11.25"]
                 [de.otto/tesla-httpkit "1.0.1"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]]

  :main ^:skip-aot com.github.meandor.core

  :target-path "target/%s"

  :lein-release {:deploy-via :clojars}
  :profiles {:uberjar {:aot :all}
             :dev     {:plugins      [[lein-release/lein-release "1.0.9"]]
                       :dependencies [[com.github.kstyrc/embedded-redis "0.6"]]}})
