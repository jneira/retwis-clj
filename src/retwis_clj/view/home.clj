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
   {:root (get-context-root)
    :timeline (post/all)}))

(defn- render-page [request]
  (wrap-layout "Home"
    (if (authenticated?)
      (user-page-body)
      (guess-page-body))
    (:messages request)))

(defn- post [{ps :params :as request}]
  (if-let [errors (seq (post/validate ps))]
    (do (-> request
            (assoc :messages (messages/from-validation errors))
            (render-page)))
    (let [user (session/current-user)]
      (post/create user (:content ps))
      (response/redirect (wrap-context-root "/")))))

(defroutes home-routes
  (GET "/" request (render-page request))
  (POST "/post" request (post request)))
