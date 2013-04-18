(ns retwis-clj.view.auth
  (:use validateur.validation)
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
   (merge
    {:context-root (get-context-root)}
    (let [keys [:title :username :password :remember-me
                 :submit-log-in :link-register
                 :link-recover-password]
          vals (i18n/with-scope :auth (vec (map i18n/t keys)))]
         (zipmap keys vals)))))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(def validate-user
  (apply validation-set
   (length-of :username :within (range 3 8))
   (length-of :password :within (range 8 100))
   (map #(format-of :password :format %)
        [#"\d+" #"\D+" #"[A-Za-z]+"])))

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
