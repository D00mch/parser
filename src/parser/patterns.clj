(ns parser.patterns
  (:require
   [parser.colors :as c])
  (:import [java.awt Robot Color Dimension MouseInfo]
           [java.awt.event KeyEvent WindowEvent InputEvent]
           [javax.swing JFrame SwingUtilities]))

(def bottom-pattern #"[x]{90}")
(def next-page-pattern #"[b][x][b]{31,34}[x][b]")
(def contact-pattern #"[g|o]{1,3}.{40,140}[b]{15,18}")
(def contact-end-pattern #"[x][g]{2}[x]")
(def contact-button-pattern #"[x][b]{15,18}[x]+")
#_(def contact-end-add-pattern #"[b]{45}")

(defrecord YCoords [start end])

(defn re-seq-pos [pattern string]
  (let [m (re-matcher pattern string)]
    ((fn step []
       (when (. m find)
         (cons (->YCoords (. m start) (. m end))
               (lazy-seq (step))))))))


(defn coords
  "takes regex, string sample, and sample start y;
  returns y's start and end map, or nil if there is no pattern match"
  [regex sample y]
  (if-let [result (re-seq-pos regex sample)]
    (-> result
        first
        (update :start + y)
        (update :end   + y))
    nil))
