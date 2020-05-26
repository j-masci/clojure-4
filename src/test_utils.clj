(ns test-utils)

(defn numbers-almost-equal [tol n1 n2]
  (< (Math/abs (- n1 n2)) tol))

(defn vectors-almost-equal [tol v1 v2]
  "Allows for some rounding errors in calculations."
  (assert (number? tol))
  (assert (vector? v1))
  (assert (vector? v2))
  (when (= (count v1) (count v2))
    (let [diffs (mapv #(Math/abs (- %1 %2)) v1 v2)]
      (every? #(< % tol) diffs))))

