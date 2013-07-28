(ns retwis-clj.view.home
  (:use retwis-clj.view.common)
  (:require [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.model.post :as post]
            [retwis-clj.model.user :as user]
            [retwis-clj.util.session :as session]
            [retwis-clj.util.messages :as messages]))

(defn- user-page-body []
  (let [user (session/current-user)]
    (stencil/render-file
     "retwis_clj/view/templates/home"
     {:root (get-context-root)
      :timeline (user/timeline user)})))

(defn- guess-page-body []
  (stencil/render-file
   "retwis_clj/view/templates/guess"
   {}))

(defn- render-page [request]
  (wrap-layout "Home"
   (if (authenticated?) (user-page-body)
        (guess-page-body))
    (:messages request)))

(defn- post [{post :params :as request}]
  (if-let [errors (seq (post/validate post))]
    (do (-> request
            (assoc :messages (messages/from-validation errors))
            (render-page)))
    (let [user (session/current-user)]
      (post/create user (:content post))
      (response/redirect (wrap-context-root "/")))))

(defroutes home-routes
  (GET "/" request (render-page request))
  (POST "/post" request (post request)))
