(ns clj-bench.index
  {:clay {:title "Clojure Language Benchmarks"}}
  (:require [scicloj.kindly.v4.kind :as kind]))

;; # Clojure Language Benchmarks
;;
;; This project benchmarks Clojure implementations using criterium,
;; with results published as Clay notebooks.

;; ## Benchmarks
;;
;; Benchmark notebooks will be added here as they are created.

(kind/hiccup
 [:ul
  [:li [:em "No benchmarks yet - check back soon!"]]])

;; ## Running Benchmarks
;;
;; To run benchmarks locally:
;;
;; ```bash
;; clj -M:dev
;; ```
;;
;; Then evaluate the benchmark namespace in your REPL.

;; ## Building Notebooks
;;
;; ```bash
;; clj -T:build notebooks
;; ```
;;
;; Output is written to `docs/` for GitHub Pages.
