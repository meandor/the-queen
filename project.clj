(defproject com.github.meandor/the-queen "0.1.0-SNAPSHOT"
  :description "OAuth2 SSO user registry"
  :url "https://github.com/meandor/the-queen"
  :license {:name "Apache Version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:name "git"
        :url  "https://github.com/meandor/the-queen"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.taoensso/carmine "2.17.0"]
                 [de.otto/tesla-microservice "0.11.22"]]

  :lein-release {:deploy-via :clojars}
  :profiles {:dev {:plugins      [[lein-release/lein-release "1.0.9"]]
                   :dependencies [[com.github.kstyrc/embedded-redis "0.6"]]}})
