(ns scratch)


(def my-functions (atom {}))

(swap! my-functions assoc :fn1 #(println "fn1..."))
(swap! my-functions assoc :fn2 #(println "fn2..."))
(swap! my-functions assoc :fn3 #(println "fn3..."))

(defn my-app [the-fns] (println "the app..."))