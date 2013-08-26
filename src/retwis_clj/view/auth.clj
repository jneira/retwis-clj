(ns retwis-clj.view.auth
  (:use validateur.validation)
  (:require [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.view.common :refer
             [wrap-context-root get-context-root
              wrap-layout translate-keys translate-error]]
            [retwis-clj.util.messages :as msgs]
            [retwis-clj.model.user :as user]))

(def labels [:username-label :password-label
                :password-check-label])

(defn- login-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/auth"
   (merge
    {:context-root (get-context-root)}
    (:params request)
    (translate-keys :auth
                    (into labels [:remember-me :login-submit
                                  :signup-link :recover-password-link])))))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)
               (:messages request)))

(defn- login [request]
  (let [{:keys [username password] :as user} (:params request)
        res (user/validate-login username password)]
    (if (= res ::user/valid-login)
      (do (session/set-user!
           (user/find-by-username username [:username]))
          (response/redirect (wrap-context-root "/")))
      (let [msg (translate-error res)]
        (login-page (assoc request :messages
                           (msgs/single :error msg)))))))

(defn- logout [request]
  (session/logout)
  (response/redirect (wrap-context-root "/")))

(defn- signup-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/signup"
   (merge
    {:context-root (get-context-root)}
    (:params request)
    (translate-keys :auth (into labels [:signup-title
                                        :signup-submit])))))

(defn- signup-page [request]
  (wrap-layout "Sign up"
               (signup-page-body request)
               (:messages request)))

(defn- signup [{user :params :as request}]
  (if-let [errors (seq (user/validate-new user translate-error))]
    (-> request
        (assoc :messages (msgs/from-validation errors))
        (signup-page))
    (let [new-user (user/create user)]
      (session/set-user! new-user)
      (response/redirect (wrap-context-root "/")))))

(defroutes auth-routes
  (GET "/login" request (login-page request))
  (POST "/login" request (login request))
  (GET "/logout" request (logout request))
  (GET "/signup" request (signup-page request))
  (POST "/signup" request (signup request)))
