(ns retwis-clj.model.db
  (:refer-clojure :exclude [cons set get])
  (:require [taoensso.carmine :as redis]
            [retwis-clj.model.db-config :as cfg]))

(def pool (redis/make-conn-pool)) 

(def spec-server1
  (if cfg/redis
    (apply redis/make-conn-spec cfg/redis)
    (redis/make-conn-spec)))

(defmacro wcar [& body]
  `(redis/with-conn pool spec-server1 ~@body))

(defn get-type [ent]
  (.getSimpleName (class ent)))

(defn key [root & subs]
  (apply str root
         (map #(if (keyword? %) % (keyword (str %))) subs)))

(defn key-with-id [{id :id :as ent}]
  (partial key (get-type ent) :id id))

(defn exists? [key]
  (redis/as-bool (wcar (redis/exists key))))

(defn get
  ([key] (wcar (redis/get key)))
  ([key & keys]
     (wcar (doseq [k (conj keys key)]
             (redis/get k)))))

(defn read
  ([type id ks]
     (let [dbks (map #(key type :id id %) ks)
           vals (apply get dbks)
           vals (if (coll? vals) vals [vals])]
       (zipmap ks vals)))
  ([{id :id :as ent}]
     (let [ks (remove #{:id} (keys ent))]
       (merge ent (read (get-type ent) id ks)))))

(defn set
  ([[k v]] (wcar (redis/set k v)))
  ([[key val] & kvs]
     (wcar (doseq [[k v] (conj kvs [key val])]
             (redis/set k v)))))

(defn writable? [ent k v]
  (and ((comp not nil?) v) (not= :id k)))

(defn write [ent]
  (let [db-key (key-with-id ent)
        kvs (for [[k v] ent :when (writable? ent k v)]
              [(db-key k) v])]
    (apply set kvs)))

(defn create [ent]
  (let [with-gen-id #(assoc ent :id (new-uid ent))]
   (write (if (:id ent) ent (with-gen-id)))))

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
