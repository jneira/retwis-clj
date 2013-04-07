(ns retwis-clj.model.post
  (:use retwis-clj.model.entities)
  (:require [retwis-clj.model.user :as user]))

(def key (partial db/key 'post))

(defn add-mentions [{c :content :as post}]
  (doseq [r (re-seq #"@\w+" c)
          :let [name (.substring r 1)
                user (user/find-by-username name)]
          :when user]
    (user/add-mention user post)))

(defn create [user-id content]
  (let [id (db/new-uid 'post)
        now (System/currentTimeMillis)
        post (->Post id content user-id now)]
    (apply db/set [(key id :content) content
                   (key :user_id) user-id
                   (key id :created-at) now])
    (db/cons id (db/key 'timeline))
    (user/add-post {:id user-id} post)
    (add-mentions post)))



