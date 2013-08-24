(ns retwis-clj.util.messages
  (:refer-clojure :exclude [empty get set])
  (:require [retwis-clj.util.flash :as flash]))

(def empty {:error [] :info []})

(defn get
  ([] (or (flash/get :messages) empty))
  ([type] (type (get))))

(defn get!
  ([] (or (flash/get! :messages) empty))
  ([type] (type (get!))))

(defn set
  ([msgs] (flash/put! :messages msgs))
  ([type msgs]
     (set (assoc (get) type msgs))))    

(defn add [type msg]
  (set (update-in (get) [type] conj msg)))

(defn single [type str]
  {type [{:content str}]})

(defn from-validation [fields-with-msgs]
  {:error
   (flatten (for [[field msgs] fields-with-msgs]
              (for [msg msgs] {:content msg})))})
