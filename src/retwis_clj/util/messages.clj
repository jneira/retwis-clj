(ns retwis-clj.util.messages
  (:refer-clojure :exclude [empty get set])
  (:require [retwis-clj.util.flash :as flash]))

(def empty {:error [] :info []})

(defn get
  ([] (flash/get :messages))
  ([type] (type (get))))

(defn set
  ([msgs] (flash/put! :messages msgs))
  ([type msgs]
     (let [all (or (get) empty)]
       (set (assoc all type msgs)))))    

(defn add [type msg]
  (let [msgs (or (get) empty)]
    (set (update-in msgs [type] conj msg))))

