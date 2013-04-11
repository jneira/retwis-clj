(ns retwis-clj.model.user
  (:refer-clojure exclude [key])
  (:use retwis-clj.model.entities)
  (:require [retwis-clj.model.db :as db]
            digest))

(def key (partial db/key 'User))
(def key-id (partial key :id))

(defn- new-salt
  ([] (new-salt 5))
  ([n] (apply str (repeatedly n #(rand-nth "etaoin")))))

(defn- hash-pw [salt pw]
  (digest/md5 (str salt pw)))

(def find-by-id
  (partial db/find-by-id id->User))

(def find-by-username
  (partial db/find-by-index id->User :username))

(defn create [name password]
  (let [salt (new-salt) pwd (hash-pw salt password)
        user (db/create (->User nil name pwd salt))]
    (db/add-to-index user :username) user))

(defn tweets
  ([type user] (tweets type user 1))
  ([type {id :id} page]
     (let [from (* (dec page) 10) to (* page 10)]
       (map #(db/read (id->Post %))
            (db/sublist (key-id id type) from to)))))

(def posts (partial tweets :posts))
(def timeline (partial tweets :timeline))
(def mentions (partial tweets :mentions))

(defn add-follower [{idx :id}
                    {idy :id :as follower}]
  (db/add (key-id idx :followers) idy)
   follower)

(defn remove-follower [{idx :id}
                       {idy :id :as follower}]
  (db/remove (key-id idx :followers) idy)
   follower)

(defn follow [{idx :id :as follower}
              {idy :id :as following}]
  (when (not= idx idy)
    (db/add (key-id idx :followees) idy)
    (add-follower following follower)))

(defn unfollow [{idx :id :as follower}
                {idy :id :as following}]
  (db/remove (key-id idx :followees) idy)
  (remove-follower following follower))

(defn following? [{idx :id :as follower}
                  {idy :id}]
  (db/member? (key-id idx :followees) idy))

(defn followers [{id :id}]
  (map #(find-by-id % [:username])
       (db/members (key-id id :followers))))

(defn followees [{id :id}]
  (map #(find-by-id % [:username])
       (db/members (key-id id :followees))))

(defn add-tweet [lists {id :id} {post-id :id :as post}]
  (doseq [lst lists]
    (db/cons post-id (key-id id lst)) post))

(def add-to-timeline (partial add-tweet [:timeline]))
(def add-mention (partial add-tweet [:mentions]))

(defn add-post [user post]
  (add-tweet [:posts :timeline] user post)
  (doall (for [f (followers user)] (add-to-timeline f post))))
