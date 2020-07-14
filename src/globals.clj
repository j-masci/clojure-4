(ns globals
  "Most/all global mutable state that lives in the outer layer of our game."
  (:require graphics))

; todo: can't we just have this live inside state?
(def *updates-per-second* 20)

(defn time-per-update-ms []
  "The time in miliseconds (float) that is given to each frame
  update and/or frame rendering."
  (/ 1000 *updates-per-second*))

(def *window-instances*
  "A map of lazy-loaded AWT objects, ie. jframe, jpanel, and a canvas."
  (atom {}))

(defn get-canvas []
  (:canvas (deref *window-instances*)))

(defn get-panel []
  (:panel (deref *window-instances*)))

(defn get-frame []
  (:frame (deref *window-instances*)))

(def *state-to-paint*
  "A hard to avoid mutable global for reasons I don't want to go into."
  (atom {}))

(defn paint-given-state! [state]
  "Repaint the canvas with the given state. This is a mess but we can't really
  avoid some amount of mess for reasons I don't want to get into.

  When we initialize the window instances, we provide a paint callback
  that accesses globals/*state-to-paint*. When we call graphics/repaint!,
  that callback is invoked. So you set the global to what is passed
  to this function, and it will be accessed a few function calls later."
  (reset! *state-to-paint* state)
  (graphics/repaint! (deref *window-instances*)))

(def *all-states*
  "Used for debugging."
  (atom []))

(def ^:dynamic *input-queue*
  "A vector of maps (see input/awt-event-obj->map)."
  (atom []))

(defn queue-input! [e]
  "Queue an input event which can be handled on next state update.

  An event is just a map. See input/awt-event-obj->map to get
  the map which you need to pass in."
  (assert (map? e))
  (swap! *input-queue* conj e))

(defn reset-input-queue! []
  (reset! *input-queue* []))

(defn get-and-reset-input-queue! []
  "Returns the events that occurred since we last flushed the queue."
  (let [ret (deref globals/*input-queue*)]
    (reset-input-queue!)
    ret))
