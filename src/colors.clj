(ns colors)

(def colors
  {:blue [0 0 255]
   :red [255 0 0]
   :green [0 255 0]})

(def rgb colors)

(defn rgba
  ([color] (rgba color 255))
  ([color op] (conj (colors color) op)))
