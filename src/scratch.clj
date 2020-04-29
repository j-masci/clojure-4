(ns scratch)

(def games (atom [{}]))

(defn update-game
  ([f] (swap! games (fn [prev] (conj @games (f (last @games)))))))

(update-game (fn [p] (assoc p :fuck 12)))

(update-game (fn [p] (assoc p :fuck 13)))

(update-game (fn [p] (assoc p :asd 133)))

(println @games)
