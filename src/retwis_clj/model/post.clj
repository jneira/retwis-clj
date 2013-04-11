(ns retwis-clj.model.post
  (:refer-clojure exclude [key])
  (:use retwis-clj.model.entities)
  (:require [retwis-clj.model.user :as user]
            [retwis-clj.model.db :as db]))

(def key (partial db/key 'Post))

(defn find-by-id
  (partial db/find-by-id id->Post))

(defn add-mentions [{c :content :as post}]
  (doseq [r (re-seq #"@\w+" c)
          :let [name (.substring r 1)
                user (user/find-by-username name)]
          :when user]
    (user/add-mention user post)))

(defn create [user-id content]
  (let [now (System/currentTimeMillis)
        post (db/create
              (->Post nil content user-id now))]
    (user/add-post {:id user-id} post)
    (add-mentions post) post))
