(ns retwis-clj.model.db-config)

(def redis {:uri (System/getenv "REDISCLOUD_URL")})

