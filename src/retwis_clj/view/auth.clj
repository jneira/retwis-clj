(ns retwis-clj.view.auth
  (:require [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.view.common :refer
             [wrap-context-root get-context-root wrap-layout]]
            [taoensso.tower :as i18n]
            [retwis-clj.model.user :as user]))

(defn init-test-data
  "Initialise session with dummy data"
  []
  (session/set-user! {:login "admin"
                      :type :admin}))

(defn- login-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/auth"
   {:context-root (get-context-root)
    :title (i18n/t :auth/title)
    :username (i18n/t :auth/username)
    :password (i18n/t :auth/password)
    :remember-me (i18n/t :auth/remember-me)
    :submit-log-in (i18n/t :auth/submit-log-in)
    :link-register (i18n/t :auth/link-register)
    :link-recover-password (i18n/t :auth/link-recover-password)}))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(defn- login [request]
  (init-test-data)
  (let [{:keys [login password]} (:params request)
        res (user/test-password login password)]
    (println res))
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
