(ns retwis-clj.view.auth
  (:use validateur.validation)
  (:require [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.view.common :refer
             [wrap-context-root get-context-root wrap-layout translate]]
            [retwis-clj.model.user :as user]))


(defn- login-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/auth"
   (merge
    {:context-root (get-context-root)}
    (translate :auth [:login-title :username :password :remember-me
                      :login-submit :signup-link
                      :recover-password-link]))))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(defn- login [request]
  (let [{:keys [username password] :as user} (:params request)
        res (user/validate-login username password)]
    (if (= res :user/correct-password)
      (do (session/set-user! user)
          (response/redirect (wrap-context-root "/")))
      (login-page request))))

(defn- logout [request]
  (session/logout)
  (response/redirect (wrap-context-root "/")))

(defn- signup-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/signup"
   (merge
    {:context-root (get-context-root)}
    (translate :auth [:signup-title :username :password
                      :password-check  :signup-submit]))))

(defn- signup-page [request]
  (wrap-layout "Sign up"
               (signup-page-body request)
               (:messages request)))

(defn- signup [{user :params :as request}]
  (let [msgs (user/validate-new user)]
    (if (empty? msgs)
      (do (session/set-user!
           (select-keys (user/create user) [:username]))
          (response/redirect (wrap-context-root "/")))
      (-> request
          (assoc :messages {:error (vals msgs)})
          (signup-page)))))

(defroutes auth-routes
  (GET "/login" request (login-page request))
  (POST "/login" request (login request))
  (GET "/logout" request (logout request))
  (GET "/signup" request (signup-page request))
  (POST "/signup" request (signup request)))
