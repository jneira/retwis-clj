(ns retwis-clj.view.auth
  (:require [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [stencil.core :as stencil]
            [retwis-clj.util.session :as session]
            [retwis-clj.view.common :refer [wrap-context-root get-context-root wrap-layout]]))

(defn init-test-data
  "Initialise session with dummy data"
  []
  (session/set-user! {:login "admin"
                      :type :admin}))

(defn- login-page-body [request]
  (stencil/render-file
   "retwis_clj/view/templates/auth"
   {:context-root (get-context-root)}))

(defn- login-page [request]
  (wrap-layout "Log in"
               (login-page-body request)))

(defn- login [request]
  (init-test-data)
  (response/redirect (wrap-context-root "/")))

(defn- logout [request]
  (session/logout)
  (response/redirect (wrap-context-root "/")))

(defroutes auth-routes
  (GET "/login" request (login-page request))
  (POST "/login" request (login request))
  (GET "/logout" request (logout request)))
