(ns parser.colors
  (:import [java.awt Color]))

;; black theme colors
#_(def clrs {:black-bg     {:color (Color. 26 26 25)    :alias "b"} ;; contact, main bg
           :shitty       {:color (Color. 76 55 34)    :alias "s"} ;; wage
           :orange       {:color (Color. 151 92 45)   :alias "o"} ;; vip title, divider
           :water-text   {:color (Color. 101 133 171) :alias "w"}
           :gray-divider {:color (Color. 73 72 71)    :alias "g"}
           :light-gray   {:color (Color. 151 149 145) :alias "l"} ;; top-panel
           :contact-str  {:color (Color. 59 57 45)    :alias "c"} ;; contact info stroke
           })

(def clrs {:black-bg     {:color (Color. 255 255 255) :alias "b"} ;; contact, main bg
           :shitty       {:color (Color. 76 55 34)    :alias "s"} ;; wage
           :orange       {:color (Color. 248 124 7)   :alias "o"} ;; vip title, divider
           :water-text   {:color (Color. 101 133 171) :alias "w"}
           :gray-divider {:color (Color. 183 184 186) :alias "g"}
           :light-gray   {:color (Color. 151 149 145) :alias "l"} ;; top-panel
           :contact-str  {:color (Color. 59 57 45)    :alias "c"} ;; contact info stroke
           })

(defn rgb [key] (get-in clrs [key :color]))

(defn ->alias [c]
  (or (->> (filter (fn [[_ {v :color}]] (= v c)) clrs)
           (map #(-> % second :alias))
           first)
      "x"))

(defn ->name [c]
  (or (->> (filter (fn [[_ {v :color}]] (= v c)) clrs)
           (map first)
           first)
      "x"))

(defn black-bg? [c] (= c (:black-bg clrs)))
(defn divider? [c] (or (= c (:gray-divider clrs)) (= c (:orange clrs))))
(defn brown? [c] (= c (:brown clrs)))
(defn contact-stroke? [c] (= c (:light-brown-gray clrs)))
