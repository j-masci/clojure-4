(ns temp)

(defn rotation-matrix
  "Rotates a point counter clockwise in radians, when you matrix multiply a point by this."
  [theta]
  ([[(Math/cos theta) (- (Math/sin theta))]
    [(Math/sin theta) (Math/cos theta)]]))

(defn rotation-matrix-clockwise [theta]
  ([[(Math/cos theta) (Math/sin theta)]
    [(- (Math/sin theta)) (Math/cos theta)]]))