(ns ents
  (:require [clojure.core.matrix :as m]))

(def player {:type :player
             :pos  [500 500]
             :vel  [2 1]
             :acc  [0 0]})

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

(defmulti draw (fn [ent g] (:type ent)))

(defmacro g [coll & props] (list get-in coll (into [] props)))

(defmethod draw
  :player
  [ent g2d]
  (doto g2d
    (.drawString (str (:pos ent)) (get-in ent [:pos 0]) (get-in ent [:pos 1]))))