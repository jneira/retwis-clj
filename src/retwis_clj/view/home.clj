(ns retwis-clj.view.home
  (:require [compojure.core :refer [defroutes GET]]
            [stencil.core :as stencil]
            [retwis-clj.view.common :as common]))

(defn- user-page-body []
  (stencil/render-file
   "retwis_clj/view/templates/home"
   {}))

(defn- guess-page-body []
  (stencil/render-file
   "retwis_clj/view/templates/guess"
   {}))

(defn- render-page [request]
  (common/wrap-layout "Home"
   (if (common/authenticated?)
     (user-page-body)
     (guess-page-body))))

(defroutes home-routes
  (GET "/" request (render-page request)))
