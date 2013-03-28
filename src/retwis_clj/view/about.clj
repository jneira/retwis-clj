(ns retwis-clj.view.about
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [retwis-clj.view.common :refer [wrap-layout]]))

(defn- page-body [request]
  (slurp (io/resource "retwis_clj/view/templates/about.html")))

(defn- render-page [request]
  (wrap-layout "About"
               (page-body request)))

(defroutes about-routes
  (GET "/about" request (render-page request)))

