(ns retwis-clj.view.profile
  (:require [compojure.core :refer [defroutes GET PUT]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.util.messages :as msgs]
            [retwis-clj.view.common :refer
             [restricted authenticated? wrap-layout
              translate translate-error]]
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
  (wrap-layout "Profile" (profile-body request)
               (:messages request)))

(defn- connect-page [request]
  (wrap-layout "Connect" (connect-body)
               (:messages request)))

(defn- follow [{{:keys [username followee]} :params :as req}]
  (let [curr (session/current-user)
        [follower fwee] (map #(user/find-by-username % [:username])
                             [username followee])
        res (user/validate-follow curr follower fwee)]
    (println res)
    (if (= res ::user/valid-follow)
      (do (user/follow follower fwee)
          (profile-page req))
      (let [msgs (msgs/single :error (translate-error res))]
        (profile-page (assoc req :messages msgs))))))

(defroutes profile-routes
  (GET "/user/:username/followee/:followee" request
       (follow request))
  (GET "/user/:username" request
       (profile-page request))
  (GET "/connect" request
       (connect-page request)))
