(ns ents
  (:require shapes
            [clojure.core.matrix :as m]
            [clojure.spec.alpha :as s]
            [seesaw.graphics :as gr])
  (:use globals))

(defn ent [type & keyvals]
  (merge {:type   type
          :shapes []
          :pos    [0 0]
          :vel    [0 0]
          :acc    [0 0]
          :dir    0} (apply hash-map keyvals)))

(def player (ent :player
                 :dir 90
                 :pos [400.0 400.0]
                 :vel [0.0 0.0]
                 :acc [0.0 0.0]
                 :shapes (concat [(shapes/circle [0 8] 4)
                                  (shapes/circle [0 -8] 4)
                                  (shapes/circle [-10 -16] 4)
                                  (shapes/circle [-10 16] 4)
                                  (shapes/circle [10 16] 4)
                                  (shapes/circle [10 -16] 4)
                                  (shapes/line [-4 14] [0 22])
                                  (shapes/line [4 14] [0 22])]
                                 (shapes/points->lines [[-10 -16]
                                                        [-10 16]
                                                        [10 16]
                                                        [10 -16]]))))
(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))


