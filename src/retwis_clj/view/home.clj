(ns retwis-clj.view.home
  (:require [compojure.core :refer [defroutes GET]]
            [stencil.core :as stencil]
            [retwis-clj.view.common :as common]))

(defn- page-body []
  (stencil/render-file
   "retwis_clj/view/templates/home"
   {}))

(defn- render-page [request]
  (common/wrap-layout "Home"
                      (page-body)))

(defroutes home-routes
  (GET "/" request (render-page request)))
