(ns retwis-clj.model.entities)

(defrecord User [id username hashed-password salt])

(defn id->User [id] (map->User {:id id}))

(defrecord Post [id content user-id created-at])

(defn id->Post [id] (map->Post {:id id}))
