(ns wddump.core
  (:import (java.io BufferedReader FileReader))
  (:require [clojure.data.json :as json])
  (:gen-class))

(use 'clojure.java.io)

(defn get-simple-facts 
  "Get the simple item-valued facts from a claim" [mainsnaks]
  (remove nil? 
          (map 
            #(let [v (get-in % [:mainsnak :datavalue :value])]
               (if (= (get v :entity-type) "item")
                 (get v :numeric-id)))                
            mainsnaks)))

(defn format-simple-fact [s p o]
  (str "fact, " s ", " (subs (str p) 1) ", Q" o))

(defn print-simple-facts [entity]
  (doseq [claim (get entity :claims)]           
    (doseq [fact (get-simple-facts (val claim))]
      (println (format-simple-fact (get entity :id) (key claim) fact)))))

(defn print-label [entity]
  (println "label, "(get entity :id) ", " (.replaceAll (get-in entity [:labels :en :value])  "," "" )))

;; or: (FileReader. "resources/wdsnip.json")
(defn -main  [& args]
  (with-open [rdr (BufferedReader. *in*)]
    (doseq [line (drop-last (drop 1 (line-seq rdr)))]
      (let [entity (json/read-str line :key-fn keyword)] 
        (print-label entity)        
        (print-simple-facts entity)))))
