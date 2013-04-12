(defproject retwis-clj "0.1.0-SNAPSHOT"
  :description "A clojure version of twitter clone on redis"
  :url "http://redis.io/topics/twitter-clone"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [stencil "0.3.2"]
                 [com.taoensso/carmine "1.6.0"]
                 [com.taoensso/tower "1.5.1"]
                 [digest "1.3.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler retwis-clj.app/site-handler}
  :war-resources-path "resources/public"
  :main retwis-clj.server)
