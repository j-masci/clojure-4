(ns ents
  (:require [clojure.core.matrix :as m]))

(def player {:type :player
             :pos  [400 400]
             :vel  [1 1]
             :acc  [1 1]})

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))