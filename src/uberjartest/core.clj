(ns uberjartest.core
  (:import (java.io BufferedReader FileReader))
  (:require [clojure.data.json :as json])
  (:gen-class))

(use 'clojure.java.io)

(defn format-fact [s p o]
  (str s ", " (subs (str p) 1) ", Q" o))

(defn get-simple-facts 
  "Get the simple item-valued facts from a claim" [mainsnaks]
  (remove nil? 
          (map 
            #(let [v (get-in % [:mainsnak :datavalue :value])]
               (if (= (get v :entity-type) "item")
                 (get v :numeric-id)))                
            mainsnaks)))

(defn dump-line [line]
  (let [entity (json/read-str line :key-fn keyword)]
    (doseq [claim (get entity :claims)]           
      (doseq [fact (get-simple-facts (val claim))]
        (println (format-fact (get entity :id) (key claim) fact))))))

;; or: (FileReader. "resources/wdsnip.json")
(defn -main  [& args]
  (with-open [rdr (BufferedReader. *in*)]
    (doseq [line (drop-last (drop 1 (line-seq rdr)))]
      (dump-line line))))
