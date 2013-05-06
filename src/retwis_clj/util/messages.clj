(ns messages
  (:require [retwis-clj.middleware.session :as session-manager]
            [retwis-clj.util.flash :as flash])

(def empty-messages)

(def get-messages
  ([] (get :messages))
  ([type] (when-let [msgs (get-messages)] (type mgs))))

(def add-message [type msg]
  (let [msgs (or (get-messages) {:error [] :info []})]
    (put! :messages (update-in msgs [type] conj msg))))
