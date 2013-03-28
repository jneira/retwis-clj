(ns retwis-clj.view.admin
  (:require [compojure.core :refer [defroutes GET]]
            [stencil.core :as stencil]
            [retwis-clj.view.common :refer [restricted admin? wrap-layout]]))

(defn- page-body []
  (stencil/render-file
   "retwis_clj/view/templates/admin"
   {}))

(defn- render-page [request]
  (wrap-layout "Admin"
               (page-body)))

(defroutes admin-routes
  (GET "/admin" request (restricted admin? render-page request)))
