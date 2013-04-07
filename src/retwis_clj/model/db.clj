(ns retwis-clj.model.db
  (:refer-clojure :exclude [cons])
  (:require [taoensso.carmine :as redis]
            [retwis-clj.model.db-config :as cfg]))

(def pool (redis/make-conn-pool)) 

(def spec-server1
  (if cfg/redis
    (apply redis/make-conn-spec cfg/redis)
    (redis/make-conn-spec)))

(defmacro wcar [& body]
  `(redis/with-conn pool spec-server1 ~@body))

(defn key [ent & segs]
  (apply str ent
         (map #(if (keyword? %) % (keyword (str %))) segs)))

(defn exists? [key]
  (redis/as-bool (wcar (redis/exists key))))

(defn get-type [ent]
  (.getSimpleName (class ent)))

(defn get
  ([key] (wcar (redis/get key)))
  ([key & keys]
     (wcar (doseq [k (conj keys key)]
             (redis/get k)))))

(defn set
  ([key val] (wcar (redis/set key val)))
  ([key val & kvs]
     (wcar (doseq [[k v] (conj (partition 2 kvs)
                               [key val])]
             (redis/set k v)))))

(defn new-uid [ent]
  (wcar (redis/incr (str ent :uid))))

(defn cons [val lst-key]
  (wcar (redis/lpush lst-key val)))

(defn sublist [lst-key from to]
  (wcar (redis/lrange lst-key from to)))

(defn add [set-key val]
  (wcar (redis/sadd set-key val)))

(defn remove [set-key val]
  (wcar (redis/srem set-key val)))

(defn member? [set-key val]
  (wcar (redis/sismember set-key val)))

(defn members [set-key]
  (wcar (redis/smembers set-key val)))
