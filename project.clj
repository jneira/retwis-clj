(defproject retwis-clj "0.1.0-SNAPSHOT"
  :description "A clojure version of twitter clone on redis"
  :url "http://redis.io/topics/twitter-clone"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [stencil "0.3.2"]
                 [com.taoensso/carmine "2.0.0-RC1"]
                 [com.taoensso/tower "1.7.1"]
                 [digest "1.3.0"]
                 [com.novemberain/validateur "1.5.0"]]
  :plugins [[lein-ring "0.8.3"]
            [lein-cloudbees "1.0.4"]]
  :ring {:handler retwis-clj.app/site-handler}
  :war-resources-path "resources/public"
  :main retwis-clj.server
  :uberjar-name "retwis-clj-standalone.jar"
  :min-lein-version "2.0.0")
