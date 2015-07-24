(ns clojure-digits.core
  (:require [flambo.api :as f]
            [flambo.conf :as cf]
            [flambo.tuple :as ft]
            [clojure.string :as s])
  (:import [org.apache.spark.mllib.linalg Vectors]
           [org.apache.spark.mllib.regression LabeledPoint]
           [org.apache.spark.mllib.classification LogisticRegressionWithLBFGS]
           [org.apache.spark.mllib.evaluation BinaryClassificationMetrics]
           [org.apache.spark.mllib.tree RandomForest]
           [org.apache.spark.mllib.tree.model RandomForestModel])
  (:gen-class))

; Create a Spark Context
(def spark
  (let [cfg (-> (cf/spark-conf)
    (cf/master "local[2]")
    (cf/app-name "clojure-digits")
    (cf/set "spark.akka.timeout" "300"))]
    (f/spark-context cfg)))

; Load data
(def data
  (-> (f/text-file spark "train.csv")
      (.zipWithIndex)
      (f/map f/untuple)
      (f/filter (f/fn [[line idx]] (< 0 idx)))
      (f/map (f/fn [[line _]]
      (->> (s/split line #",")
           (map #(Integer/parseInt %)))))))

; Convert to a Labeled Dataset
(def dataset
  (f/map data (f/fn [x] (LabeledPoint. (first x) (Vectors/dense (double-array (rest x)))))))
(f/cache dataset)

; Load test data
(def test-data
  (-> (f/text-file spark "test.csv")
    (.zipWithIndex)
    (f/map f/untuple)
    (f/filter (f/fn [[line idx]] (< 0 idx)))
    (f/map (f/fn [[line _]]
      (->> (s/split line #",")
      (map #(Integer/parseInt %)))))))

; Run model
(def rf-model (RandomForest/trainClassifier dataset 10 (new java.util.HashMap) 100 "auto" "gini" 14 32 12345))

; Run over test set
(def test-predictions (map #(.predict rf-model (Vectors/dense (double-array %))) (f/collect test-data)))

(defn -main
  [& args]
  (println (clojure.string/join "\n" (map #(str (int %)) test-predictions))))
