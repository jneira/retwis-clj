(ns retwis-clj.view.common
  (:require [ring.util.response :as response]
            [stencil.core :as stencil]
            [taoensso.tower :as i18n]
            [retwis-clj.middleware.context :as context]
            [retwis-clj.util.session :as session]))

;;; Context utils
(defn get-context-root
  []
  (context/get-context-root))

(defn wrap-context-root
  "Add web context to the path of URI"
  [path]
  (str (get-context-root) path))

;;; i18n utils

(defn translate
  ([keys] (zipmap keys (map i18n/t keys)))
  ([scope keys] (i18n/with-scope scope (translate keys))))


;;; User utils
(defn restricted
  "Function for restricted part of the Web site. 
   Takes a predicate function and the handler to execute if predicate is true."
  [predicate handler & args]
  (if (predicate)
    (apply handler args)
    (response/redirect (wrap-context-root "/"))))

(defn authenticated?
  "Sample authentication function. Test if current user is not null."
  []
  (not (nil? (session/current-user))))

(defn admin?
  "Sample authorization function. Test if current user it admin."
  []
  (if-let [user (session/current-user)]
    (= :admin (:type user))))

;;; Layout
(defn- base-content [title body]
  {:context-root (context/get-context-root)
   :title title
   :body body})

(defn- user-nav-links [user]
  (when (admin?) 
    [{:link (wrap-context-root "/admin") :label "Administration"}
     {:link (wrap-context-root "/") :label "Foo"}]))

(defn wrap-layout
  "Define pages layout"
  [title body]
  (stencil/render-file
   "retwis_clj/view/templates/layout"
   (let [content (base-content title body)
         user (session/current-user)]
     (if (authenticated?)
       (assoc content 
         :authenticated? 
         {:user (:username user)
          :nav-links (user-nav-links user)})
       (assoc content :not-authenticated? {})))))
