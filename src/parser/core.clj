(ns parser.core
  (:require
   [parser.colors :as c]
   [parser.patterns :as p]
   [clojure.string :as str])
  (:import [java.awt Robot Color Dimension MouseInfo Toolkit]
           [java.awt.event KeyEvent WindowEvent InputEvent]
           [javax.swing JFrame SwingUtilities]
           [java.awt.datatransfer Clipboard StringSelection DataFlavor]
           java.util.concurrent.Executors)
  (:gen-class))

(defn f! []
  (set! *warn-on-reflection* true))

(def thread-pool
  (Executors/newFixedThreadPool
   (+ 2 (. (Runtime/getRuntime) availableProcessors))))

(def robot (doto (Robot.)
             (.setAutoDelay 5)
             (.setAutoWaitForIdle true)))

;; ****************** CLIPBOARD

(defn ->clipboard [txt]
  (doto (.. Toolkit getDefaultToolkit getSystemClipboard)
    (.setContents (StringSelection. txt) nil)))

(defn <-clipboard []
  (.. Toolkit getDefaultToolkit getSystemClipboard (getData DataFlavor/stringFlavor)))

;; ****************** SCREEN UTILS
(defrecord MousePos [x y])

(defn mouse-pos []
  (let [mouse-info (.. MouseInfo getPointerInfo getLocation)]
    (->MousePos (. mouse-info x) (. mouse-info y))))

(defn move!
  ([[x y]] (move! x y))
  ([x y] (.mouseMove ^Robot robot x y)))

(defn scroll! [i] (.mouseWheel ^Robot robot i))

(def press-release-delay 30)

(defn click! []
  (.mousePress ^Robot robot InputEvent/BUTTON1_DOWN_MASK)
  (.delay robot press-release-delay)
  (.mouseRelease ^Robot robot InputEvent/BUTTON1_DOWN_MASK))

(defn press! [key]
  (.keyPress ^Robot robot key)
  (.delay robot press-release-delay)
  (.keyRelease ^Robot robot key))

;; ****************** COLOR INFO
(defn color
  ([{:keys [x y]}] (color x y))
  ([x y]   (.getPixelColor ^Robot robot x y)))

(defn show-color [color]
  (SwingUtilities/invokeLater
   #(let [f (doto (JFrame.)
              (-> (.getContentPane) (.setBackground color))
              (.setSize (Dimension. 100 100))
              (.setVisible true))]
      (future
        (Thread/sleep 2000)
        (.dispatchEvent f (WindowEvent. f WindowEvent/WINDOW_CLOSING))))))

(defn color-at-cursor [] (color (mouse-pos)))

(defn color->txt [c]
  (str "(Color. "(.getRed ^Color c)" "(.getGreen ^Color c)" "(.getBlue ^Color c)")"))

(defn color-at-cursor! []
  (let [{:keys [x y]}   (mouse-pos)
        clr     (color x y)
        clr-txt (color->txt clr)]
    (show-color clr)
    (println "x" x "y" y clr-txt)
    (->clipboard clr-txt)
    color))

(defn color-vert-line [x y1 y2]
  (for [y (range y1 y2)] (color x y)))

;; ****************** PATTERNS

(defn line-aliases [x y1 y2]
  (str/join (map c/->alias (color-vert-line x y1 y2))))

(defn find-pattern [pattern x y1 y2]
  (p/coords pattern (line-aliases x y1 y2) y1))

;; ****************** END CONDITION

(defn bottom? []
  (find-pattern p/bottom-pattern 204 884 983))

(defn end? []
  (= (c/rgb :shitty) (color 247 626)))

;; ****************** PROCESS CONTACT
(def x-cont 408)

(defn get-contact-pos []
  (find-pattern p/contact-pattern x-cont 200 360))

(defn open-full-contact [ys]
  (move! (+ 20 x-cont) (+ 42 (:start ys)))
  (.delay ^Robot robot 20)
  (click!)
  (.delay ^Robot robot 1000)
  (scroll! 4))

(defn save-cursor! []
  (when (< (:x (mouse-pos)) 956)
    (move! 956 409)))

(defn move-click! [x y]
  (move! x y)
  (.delay ^Robot robot 40)
  (click!)
  (.delay ^Robot robot 40)
  (save-cursor!))

(defn stop? [] (> (:x (mouse-pos)) 1000))

(def dsw 0)
(defn download-contact! []
  (loop [n    80
         wait dsw]
    (let [pos    (find-pattern p/contact-button-pattern 767 180 340)]
      (cond (stop?)          :stopped
            pos              (move-click! 678 (+ 10 (:start pos)))
            (< n 1)          :end-reached
            :else            (do (press! KeyEvent/VK_J)
                                 (.delay ^Robot robot wait)
                                 (recur (dec n) wait))))))

(defn slow-search-contact! []
  (loop [n 4]
    (.delay ^Robot robot 300)
    (let [pos (find-pattern p/contact-pattern x-cont 160 320)]
      (if pos
        pos
        (do (press! KeyEvent/VK_J) (recur (dec n)))))))

(defn process-contact! []
  (save-cursor!)
  (when-let [ys (get-contact-pos)]
    (open-full-contact ys)
    (download-contact!)))

;; ****************** MAIN WORKFLOW
(defn next-button-pos! []
  (find-pattern p/next-page-pattern 403 554 654))

(defn click-more! [ys]
  (move! 695 (+ 61 (:start ys)))
  (click!)
  (save-cursor!))

(defn work! []
  (def robot (doto (Robot.)
               (.setAutoDelay 5)
               (.setAutoWaitForIdle true)))
  (.submit thread-pool
           (fn []
             (loop [n 100]
               (save-cursor!)
               (cond (stop?)   :stopped
                     (= n 0)   (work!)
                     (bottom?) (do (press! KeyEvent/VK_J)
                                   (Thread/sleep 50)
                                   (click-more! (next-button-pos!))
                                   (Thread/sleep 3500)
                                   (recur (dec n)))
                     :else     (do (press! KeyEvent/VK_J)
                                   (process-contact!)
                                   (recur (dec n))))
             ))))

(defn -main []
  (println "started")
  (scroll! 1)
  (try
    (work!)
    (catch Exception e
      (.printStackTrace e)))
  (.waitForIdle ^Robot robot)
  (Thread/sleep 100))
