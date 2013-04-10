(ns retwis-clj.model.user
  (:use retwis-clj.model.entities)
  (:require [retwis-clj.model.db :as db]
            digest))

(def key-user (partial db/key 'User))
(def key (partial key-user :id))

(defn- new-salt
  ([] (new-salt 5))
  ([n] (apply str (repeatedly n #(rand-nth "etaoin")))))

(defn- hash-pw [salt pw]
  (digest/md5 (str salt pw)))

(defn find-by-id [id]
  (let [name (key id :username)]
    (when (db/exists? name)
      (map->User {:id id}))))

(defn find-by-username [name]
  (when-let [id (db/get (key-user :username  name))]
    (id->User id)))

(defn create [name password]
  (let [id (db/new-uid 'user) salt (new-salt)
        pwd (hash-pw salt password)]
    (apply db/set [(key id :username) name
                   (key-user :username name) id
                   (key id :salt) salt
                   (key id :hashed-password) pwd])
    (db/cons id (db/key 'users))
    (->User id name pwd salt)))

(defn tweets
  ([type user] (tweets type user 1))
  ([type {:keys [id]} page]
     (let [from (* (dec page) 10) to (* page 10)]
       (map #(map->Post {:id %})
            (db/sublist (key id type) from to)))))

(defn add-follower [{idx :id} {idy :id}]
  (db/add (key idx :followers) idy))

(defn remove-follower [{idx :id} {idy :id}]
  (db/remove (key idx :followers) idy))

(defn follow [{idx :id} {idy :id}]
  (when (not= idx idy)
    (db/add (key idx :followees) idy)
    (add-follower idy idx)))

(defn unfollow [{idx :id} {idy :id}]
  (db/remove (key idx :followees) idy)
  (remove-follower idy idx))

(defn following? [{idx :id} {idy :id}]
  (db/member? (key idx :followees) idy))

(defn followers [{id :id}]
  (map id->User
       (db/members (key id :followers))))

(defn followees [{id :id}]
  (map id->User
       (db/members (key id :followees))))

(def posts (partial tweets :posts))
(def timeline (partial tweets :timeline))
(def mentions (partial tweets :mentions))

(defn add-tweet [lists {id :id} {post-id :id}]
  (doseq [lst lists]
    (db/cons post-id (key id lst))))

(def add-to-timeline (partial add-tweet [:timeline]))
(def add-mention (partial add-tweet [:mentions]))

(defn add-post [user post]
  (add-tweet [:posts :timeline] user post)
  (doseq [f (followers user)] (add-to-timeline f post)))
