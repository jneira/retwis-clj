(ns retwis-clj.view.home
  (:require [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.view.common :as common]
            [retwis-clj.model.post :as post]
            [retwis-clj.util.session :as session]
            [retwis-clj.util.messages :as messages]))

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

(defn- post [{post :params :as request}]
  (if-let [errors (seq (post/validate post))]
    (assoc request :messages
           (messages/from-validation errors))
    (let [user (session/current-user)]
      (println "Usuario" user)
      (post/create (:contents post) (:username user))))
  (render-page request))

(defroutes home-routes
  (GET "/" request (render-page request))
  (POST "/post" request (post request)))
