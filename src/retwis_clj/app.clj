(ns retwis-clj.app
  (:require [clojure.core.cache :as cache]
            [compojure.core :refer [defroutes routes]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [stencil.loader :as stencil]
            [clojure.core.cache :as cache]
            [retwis-clj.middleware.session :as session-manager]
            [retwis-clj.middleware.context :as context-manager]))

;; Initialization
; Add required code here (database, etc.)
(stencil/set-cache (cache/ttl-cache-factory {}))
;(stencil/set-cache (cache/lru-cache-factory {}))


;; Load public routes
(require '[retwis-clj.view.home :refer [home-routes]]
         '[retwis-clj.view.about :refer [about-routes]])

;; Load authentication routes
(require '[retwis-clj.view.auth :refer [auth-routes]])

;; Load private routes
(require '[retwis-clj.view.profile :refer [profile-routes]]
         '[retwis-clj.view.admin :refer [admin-routes]])


;; Ring handler definition
(defroutes site-handler
  (-> (routes home-routes
              about-routes
              auth-routes
              profile-routes
              admin-routes
              (route/resources "/")
              (route/not-found "<h1>Page not found.</h1>"))
      (session-manager/wrap-session)
      (context-manager/wrap-context-root)
      (handler/site)))
