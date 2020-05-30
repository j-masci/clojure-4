(ns utils
  (:require [seesaw.graphics :as gr]
            [clojure.data.json :as json]
            [seesaw.color :as color]))

(defn timestamp-ms-float []
  "Timestamp in mili-seconds with nano-second precision."
  (/ (System/nanoTime) 1000000))

(defn parse-ms-float [ms]
  "Provided mili-seconds as a float, returns a vector
  of mili-seconds as an int and nano-seconds as an int."
  (let [int-ms (Math/floor ms)
        int-ns (Math/floor (* 1000000 (- ms int-ms)))]
    [int-ms int-ns]))

(defn sleep-ms-float [ms]
  "Sleep for mili-seconds. Pass in float or int."
  (let [[int-ms int-ns] (parse-ms-float ms)]
    (Thread/sleep int-ms int-ns)))

(defn time-fn-ms [f]
  "Executes and times a function (in mili-seconds, float)."
  (let [time-before (timestamp-ms-float)
        ret (f)
        time-taken (- (timestamp-ms-float) time-before)]
    [ret time-taken]))

(defn do-fn-and-maybe-sleep [f allowed-ms]
  "Executes a function and sleeps if the function took less time
  than allowed-ms to complete. Returns the functions return value."
  (let [[ret time-taken] (time-fn-ms f)
        time-remaining (- allowed-ms time-taken)]
    (when (> time-remaining 0) (sleep-ms-float time-remaining))
    ret))

(defn nano-seconds-per-frame [ups]
  (if (> ups 0) (int (* 1000000000 (float (/ 1 ups)))) 0))

(defn log-debug!
  "Writes to a file in the /debug directory."
  ([thing] (log-debug! thing "debug"))
  ([thing filename-prefix]
   (let [timestamp (int (/ (System/currentTimeMillis) 1000))
         filename (str filename-prefix "-" timestamp ".txt")
         content (if (string? thing) thing (with-out-str (json/pprint thing)))]
     (spit filename content))))

(defn draw-string
  "Draw some text on the canvas."
  [g2d x y str]
  (.drawString g2d str x y))