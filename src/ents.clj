(ns ents
  (:require [clojure.core.matrix :as m]
            [clojure.spec.alpha :as s]
            [seesaw.graphics :as gr])
  (:use globals))

(defn ent [type & keyvals]
  (merge {:type type :shapes []} (apply hash-map keyvals)))

(def player (ent :player
                 :dir 0.0
                 :pos [500.0 500.0]
                 :vel [0.0 0.0]
                 :acc [0.0 0.0]))

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

