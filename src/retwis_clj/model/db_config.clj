(ns retwis-clj.model.db-config)

(def redis
  (when-let [uri (System/getenv "REDISCLOUD_URL")] {:uri uri}))

