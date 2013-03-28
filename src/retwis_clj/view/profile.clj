(ns retwis-clj.view.profile
  (:require [compojure.core :refer [defroutes GET]]
            [stencil.core :as stencil]
            [retwis-clj.view.common :refer [restricted authenticated? wrap-layout]]))

(defn- page-body []
  (stencil/render-file
   "retwis_clj/view/templates/profile"
   {}))

(defn- render-page [request]
  (wrap-layout "Profile"
               (page-body)))

(defroutes profile-routes
  (GET "/profile" request (restricted authenticated? render-page request)))
