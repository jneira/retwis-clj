(ns retwis-clj.model.db
  (:refer-clojure :exclude [cons get type]
                  :rename {set core-set})
  (:require [taoensso.carmine :as redis]
            [retwis-clj.model.db-config :as cfg]))

(def pool (redis/make-conn-pool)) 

(def spec-server1
  (if cfg/redis
    (apply redis/make-conn-spec cfg/redis)
    (redis/make-conn-spec)))

(defmacro wcar [& body]
  `(redis/with-conn pool spec-server1 ~@body))

(defn type [ent]
  (.getSimpleName (class ent)))

(defn key [root & subs]
  (apply str root
         (map #(if (keyword? %) % (keyword (str %))) subs)))

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
  ([{id :id :as ent} ks-select]
     (when ent (let [ks (remove #{:id} ks-select)]
                 (merge ent (read (type ent) id ks)))))
  ([ent] (read ent (keys ent))))

(defn find-by-id
  ([id->ent id] (find-by-id id->ent id []))
  ([id->ent id ks]
     (let [ent (id->ent id)]
       (when (member? (key (type ent) :ids) id)
         (if (seq ks)
           (read ent ks) ent)))))

(defn find-by-index
  ([id->ent index i ks]
     (let [type (type (id->ent nil))]
      (when-let [id (get (key type index i))]
        (find-by-id id->ent id ks))))
  ([id->ent index i]
     (find-by-index id->ent index i [])))

(defn add-to-index
  ([root index i id]
     (set [(key root index i) id]))
  ([ent index]
     (add-to-index (type ent) index
                   (index ent) (:id ent))))

(defn set
  ([[k v]] (wcar (redis/set k v)))
  ([[key val] & kvs]
     (wcar (doseq [[k v] (conj kvs [key val])]
             (redis/set k v)))))

(defn writable? [[k v]]
  (and ((comp not nil?) v) (not= :id k)))

(defn write
  ([type id kvs]
     (apply set (map (fn [[k v]] [(key type :id id k) v]) kvs))
     kvs)
  ([{id :id :as ent}]
     (let [kvs (filter writable? ent)]
       (write (type ent) id kvs) ent)))

(defn new-uid [type]
  (wcar (redis/incr (key type :uid))))

(defn create [ent]
  (let [id (or (:id ent) (new-uid (type ent)))
        ent-id (assoc ent :id id)]
    (add (key (type ent) :ids) id)
    (write ent-id)))

(defn cons [val lst-key]
  (wcar (redis/lpush lst-key val)))

(defn sublist [lst-key from to]
  (wcar (redis/lrange lst-key from to)))

(defn add [set-key val]
  (wcar (redis/sadd set-key val)))

(defn remove [set-key val]
  (wcar (redis/srem set-key val)))

(defn member? [set-key val]
  (redis/as-bool (wcar (redis/sismember set-key val))))

(defn members [set-key]
  (wcar (redis/smembers set-key)))
