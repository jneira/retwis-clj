(ns retwis-clj.view.profile
  (:require [compojure.core :refer [defroutes GET PUT]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.view.common :refer
             [restricted authenticated? wrap-layout]]
            [retwis-clj.model.user :as user]))

(defn- profile-body [{{name :username} :params}]
  (stencil/render-file
   "retwis_clj/view/templates/profile"
   (let [current (session/current-user)
         user (user/find-by-username name)]
     {:posts (user/posts user)
      :followers (user/followers user)
      :followees (user/followees user)
      :self (= name (:username current))
      :other name
      :current current})))

(defn- connect-body []
  (stencil/render-file
   "retwis_clj/view/templates/connect"
   (let [current (session/current-user)]
     {:mentions (user/mentions current)})))

(defn- profile-page [request]
  (wrap-layout "Profile" (profile-body request)))

(defn- connect-page [request]
  (wrap-layout "Connect" (connect-body)))

(defn- follow [request])

(defroutes profile-routes
  (GET "/user/:username" request
       (profile-page request))
  (GET "/connect" request
       (connect-page request))
  (PUT "/user/:self-user/followee/:user-to-follow" request
       (follow request)))
