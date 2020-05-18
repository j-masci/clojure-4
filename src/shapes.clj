(ns shapes)

(defn shape [type & keyvals]
  "A shape is a map with a :type, and other data depending on the type."
  (merge {:type type} (apply hash-map keyvals)))

(defn line [p1 p2]
  (shape :line :p1 p1 :p2 p2))

(defn circle [center radius]
  (shape :circle :center center :radius radius))

(defn points->lines [points]
  "ie. [[x1 y2] [x2 y2] [x3 y3]]"
  (mapv line points (rest (conj points (first points)))))

;maybe later
;(defn rect->shapes [points]
;  ())

(defn player->shapes [ent]
  "generate the shapes vector for the player"
  (into []
        (concat [(-> (circle [0 0] 10)
                     (assoc :color :blue))]
                (points->lines [[-10 -10]
                                [-10 10]
                                [10 10]
                                [10 -10]]))))

(defmulti rotate (fn [shape amt]
                   (:type shape)))

(defmethod rotate :circle [shape deg] shape)

(defmethod rotate :line [shape deg]
  ())

(defmulti transform (fn [shape offset rotation]
                      (:type shape)))

(defmethod transform :line [line offset rotation]
  ())