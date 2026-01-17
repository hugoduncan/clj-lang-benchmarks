(ns vec-construction
  {:clay {:title "Vec Construction Benchmarks"}}
  (:require
   [criterium.bench :as bench]
   [criterium.domain :as domain]
   [criterium.domain-plans :as domain-plans]
   [criterium.jvm :as jvm]
   [scicloj.kindly.v4.kind :as kind]))

(kind/hidden
 (bench/set-default-viewer! :kindly))

;; # Vec Construction Benchmarks
;;
;; Measuring `(vec realized-seq)` performance across collection sizes to analyze
;; Clojure's PersistentVector implementation characteristics.

;; 1. [Environment](#environment)
;; 2. [Up to 32 elements](#up-to-32)
;; 3. [32 to 128 elements](#up-to-128)
;; 4. [More than 128 elements](#more-than-28)

^:kindly/hide-code
(kind/hiccup [:a {:id "environment"}])
;; ## Environment

*clojure-version*

(jvm/runtime-details)

(jvm/os-details)

^:kindly/hide-code
(kind/hiccup [:a {:id "up-to-32"}])
;; ## Up to 32 elements

(domain/bench
 (domain/domain-expr
  [n [2 4 8 16 32]]
  {:vec (vec (doall (range n)))})
 :bench-options {:metric-ids [:elapsed-time-only :thread-allocation]}
 :domain-plan domain-plans/complexity-analysis)

^:kindly/hide-code
(kind/hiccup [:a {:id "up-to-128"}])
;; ## 33-128 elements

(domain/bench
 (domain/domain-expr
  [n [33 48 64 96 128]]
  {:vec (vec (doall (range n)))})
 :bench-options {:metric-ids [:elapsed-time-only :thread-allocation]}
 :domain-plan domain-plans/complexity-analysis)

^:kindly/hide-code
(kind/hiccup [:a {:id "more-than-128"}])
;; ## More than 128 elements

(domain/bench
 (domain/domain-expr
  [n [129 256 512 1024 2048]]
  {:vec (vec (doall (range n)))})
 :bench-options {:metric-ids [:elapsed-time-only :thread-allocation]}
 :domain-plan domain-plans/complexity-analysis)

(kind/hidden
 (bench/set-default-viewer! :print))
