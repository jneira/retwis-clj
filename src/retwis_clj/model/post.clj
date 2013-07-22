(ns retwis-clj.model.post
  (:refer-clojure exclude [key])
  (:use retwis-clj.model.entities
        validateur.validation)
  (:require [retwis-clj.model.user :as user]
            [retwis-clj.model.db :as db]))

(def key (partial db/key 'Post))

(def find-by-id
  (partial db/find-by-id id->Post))

(defn add-mentions [{c :content :as post}]
  (doseq [r (re-seq #"@\w+" c)
          :let [name (.substring r 1)
                user (user/find-by-username name)]
          :when user]
    (user/add-mention user post)))

(defn constraints [post]
  [(length-of :content :within (range 1 141))])

(defn validate [post]
  ((apply validation-set (constraints post)) post))

(defn create [user-id content]
  (let [now (System/currentTimeMillis)
        post (db/create
              (->Post nil content user-id now))]
    (user/add-post {:id user-id} post)
    (add-mentions post) post))
