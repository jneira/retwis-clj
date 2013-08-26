(ns retwis-clj.view.profile
  (:require [compojure.core :refer [defroutes GET PUT DELETE ANY]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.util.messages :as msgs]
            [retwis-clj.view.common :refer
             [restricted authenticated? wrap-layout
              translate translate-keys translate-error]]
            [retwis-clj.model.user :as user]))

(def trans (partial translate :profile))

(defn- profile-body [{{name :username} :params}]
  (stencil/render-file
   "retwis_clj/view/templates/profile"
   (let [current (session/current-user)
         user (user/find-by-username name)
         fwee? (user/following? current user)]
     (merge
      {:posts (user/posts user)
       :followers (user/followers user)
       :followees (user/followees user)
       :self (= name (:username current))
       :other name :current current
       :other-user-method (if fwee? "delete" "put")
       :other-user-submit (trans (if fwee? :unfollow-submit
                                           :follow-submit))}
      (translate-keys :profile
       [:tweets-title :no-tweets-msg
        :followers-title :no-followers-msg
        :followees-title :no-followees-msg])))))

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

(defn- follow-or-unfollow-by-method [follower followee method]
  (case (.toUpperCase method)
    "PUT" (user/follow follower followee)
    "DELETE" (user/unfollow follower followee)))

(defn- follow-or-unfollow [{{:keys [username followee _method]}
                            :params :as req}]
  (let [curr (session/current-user)
        [follower fwee] (map #(user/find-by-username % [:username])
                             [username followee])
        res (user/validate-follow curr follower fwee)
        method (or _method (name (:request-method req)))]
    (if (= res ::user/valid)
      (do (follow-or-unfollow-by-method follower fwee method)
          (profile-page req))
      (let [msgs (msgs/single :error (translate-error res))]
          (profile-page (assoc req :messages msgs))))))

(defroutes profile-routes
  (ANY "/user/:username/followee/:followee" request
        (follow-or-unfollow request))
  (GET "/user/:username" request
       (profile-page request))
  (GET "/connect" request
       (connect-page request)))
