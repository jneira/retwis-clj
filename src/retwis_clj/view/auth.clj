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
    (translate :auth [:title :username :password :remember-me
                      :submit-log-in :link-register
                      :link-recover-password]))))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(defn- login [request]
  (let [{:keys [username password]} (:params request)
        res (user/validate-login username password)]
    (println res)
    (session/set-user! {:login username :type :admin}))
  (response/redirect (wrap-context-root "/")))

(defn- logout [request]
  (session/logout)
  (response/redirect (wrap-context-root "/")))

(defn- signup-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(defn- signup [request]
  (init-test-data)
  (println request)
  (response/redirect (wrap-context-root "/")))

(defroutes auth-routes
  (GET "/login" request (login-page request))
  (POST "/login" request (login request))
  (GET "/logout" request (logout request))
  (GET "/signup" request (signup-page request))
  (POST "/signup" request (signup request)))
