(ns retwis-clj.model.user
  (:refer-clojure :exclude [key])
  (:use retwis-clj.model.entities
        validateur.validation)
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

(def exists? (comp boolean find-by-username))

(defn validate-login [username password]
  (let [{:keys [hashed-password salt] :as user}
        (find-by-username username [:salt :hashed-password])]
    (if user
      (if (= hashed-password (hash-pw salt password))
        ::valid-login ::incorrect-password)
      ::user-not-found)))

(defn constraints [{pw :password :as user} msg-fn]
  (list*
   (length-of :username :within (range 3 8) :message-fn msg-fn)
   (length-of :password :within (range 8 257) :message-fn msg-fn)
   (inclusion-of :password-check :in (set [pw]) :message-fn msg-fn)
   (map #(format-of :password :format % :message-fn msg-fn)
        [#"\d+" #"\W+" #"[A-Za-z]+"])))

(defn validate [user msg-fn]
  ((apply validation-set (constraints user msg-fn)) user))

(defn validate-new [user msg-fn]
  (if (exists? (:username user))
    {:username #{(msg-fn :username-in-use user :username)}}
    (validate user msg-fn)))

(defn create
  ([{:keys [username password]}]
     (create username password))
  ([name password]
     (let [salt (new-salt) pwd (hash-pw salt password)
           user (db/create (->User nil name pwd salt))]
       (db/add-to-index user :username) user)))

(defn tweet-by-id [tweet-id]
  (let [t (db/read (id->Post tweet-id))
        u (find-by-id (:user-id t) [:username])]
    (assoc t :user u)))

(defn tweets
  ([type user] (tweets type user 1))
  ([type {id :id} page]
     (let [ids (db/paged-sublist (key-id id type) 10 page)]
       (map tweet-by-id ids))))

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

(defn validate-follow [current follower followee]
  (let [[curr fwer fwee]
        (map :username [current follower followee])]
    (cond (not (exists? fwer)) ::unknown-follower
          (not (exists? fwee)) ::unknown-followee
          (not= curr fwer) ::current-is-not-follower
          (= fwer fwee) ::follower-is-followee
          :else ::valid)))

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

(defn add-tweet [lists {user-id :id} {post-id :id :as post}]
  (doseq [lst lists]
    (db/cons post-id (key-id user-id lst)))
  post)

(def add-to-timeline (partial add-tweet [:timeline]))
(def add-mention (partial add-tweet [:mentions]))

(defn add-post [user post]
  (add-tweet [:posts :timeline] user post)
  (doall (for [f (followers user)] (add-to-timeline f post))))
