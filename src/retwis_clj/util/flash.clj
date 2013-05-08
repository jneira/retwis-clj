(ns retwis-clj.util.flash
  (:refer-clojure :exclude [get])
  (:require [retwis-clj.middleware.session :as session-manager]))

(defn put!
  "Put key/value in flash"
  [k v]
  (session-manager/flash-put! k v))

(defn get!
  "Retrieve a flash value and remove the key"
  [k]
  (session-manager/flash-get k))

(defn get
  "Retrieve a flash value"
  [k]
  (@session-manager/*flash* k))
