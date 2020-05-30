(ns _core
  (:require
    colors
    ents
    game
    globals
    graphics
    input
    mock
    shapes
    utils
    vec
    [seesaw.core :as ss]))

(def *window-has-been-initialized*
  "Will check if this is false to lazy-init the window when opening it.

  init-window! does not first check this, since we may want to re-init the window."
  (atom false))

(defn init-window! []
  "Initializes the jframe, jpanel, etc. Intentionally not run every time
  we open the REPL."
  (reset! *window-has-been-initialized* true)
  (let [instances (graphics/create-window-instances
                    (fn [canvas g2d]
                      (let [state (deref globals/*state-to-paint*)]
                        (game/paint! state canvas g2d))))
        ; takes a AWTEvent instance, converts it to a map, and then registers it
        queue-input! (fn [e]
                       (globals/queue-input!
                         (input/awt-event-obj->map e)))]
    (ss/listen (:canvas instances)
               :mouse-entered queue-input!
               :mouse-motion queue-input!)
    (ss/listen (:frame instances)
               :focus-gained queue-input!
               :mouse-wheel-moved queue-input!
               :key queue-input!)
    (reset! globals/*window-instances* instances)))

(defn open-window! []
  (if-not @*window-has-been-initialized* (init-window!))
  (ss/invoke-later (ss/show! (globals/get-frame))))

(defn close-window! []
  (ss/invoke-later (ss/hide! (globals/get-frame))))

(def *loop-next-state-callback*
  "A placeholder no-op. You can re-def this in the REPL to test certain things.
  Accepts state and must return it."
  identity)

(defn loop!
  "Very side-effecty. Gets the next state, sleeps a bit, resets the
  input queue, tracks the new state for debugging, optionally paints,
  then recurs unless we're paused. Optionally paints so that we can
  test this without a window open if we want to."
  ([state]
   (loop! state true))
  ([state bool-repaint]
   (let [next-state (utils/do-fn-and-maybe-sleep
                      ; the function computes and returns next state, and maybe paints
                      #(let [next-state (game/update* state (globals/get-and-reset-input-queue!))]
                         (when bool-repaint (globals/paint-given-state! next-state))
                         (*loop-next-state-callback* next-state))
                      (globals/time-per-update-ms))]
     (swap! globals/*all-states* conj next-state)
     (if (game/is-paused next-state) (println "Paused.") (recur next-state bool-repaint)))))

(defn resume! []
  "Resume game after its been paused.

  There is no pause function currently. To pause the game, press escape
  while its running (if that doesn't work, check input handlers in game.clj)"
  (loop! (last (deref globals/*all-states*)) true))

(defn start! []
  "When in the REPL, only call this if you manually called open-window! first.

  However, you normally don't need to call this function, just call -main instead.

  To start the game after it has been paused, use resume! instead."
  (loop! (game/get-initial-state) true))

(defn -main [& args]
  "Open the window and start the game."
  (init-window!)
  (open-window!)
  (start!))

