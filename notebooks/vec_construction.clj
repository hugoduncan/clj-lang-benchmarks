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

;; ## Vec Internals
;;
;; Clojure's PersistentVector uses two different internal representations:
;;
;; - **Array-based (≤32 elements)**: Small vectors store elements directly in a
;;   Java array. Construction is O(n) with minimal overhead.
;;
;; - **Tree-based (>32 elements)**: Larger vectors use a 32-way branching trie
;;   (HAMT). The tree depth grows logarithmically: depth 1 holds up to 1024
;;   elements, depth 2 up to 32768, etc.
;;
;; The transition at 32 elements is a key inflection point where allocation
;; patterns and construction costs change.

;; ## Environment

*clojure-version*

(jvm/runtime-details)

(jvm/os-details)

;; ## Small Range Benchmarks (1-32 elements)
;;
;; These sizes fit in the array-based representation. We expect linear scaling
;; with low per-element overhead.

(domain/bench
 (domain/domain-expr
  [n [1 2 4 8 16 32]]
  {:vec (vec (doall (range n)))})
 :bench-options {:metric-ids [:elapsed-time :thread-allocation]}
 :domain-plan domain-plans/complexity-analysis)

;; ## Tree Range Benchmarks (32-10240 elements)
;;
;; These sizes require tree-based storage. Construction involves building the
;; trie structure, which adds overhead compared to the array-based path.

(domain/bench
 (domain/domain-expr
  [n [32 64 128 256 512 1024 2048 4096 10240]]
  {:vec (vec (doall (range n)))})
 :bench-options {:metric-ids [:elapsed-time :thread-allocation]}
 :domain-plan domain-plans/complexity-analysis)

;; ## Analysis
;;
;; Key observations to look for:
;;
;; - **Linear scaling in small range**: Construction time should scale linearly
;;   with size for array-based vectors.
;;
;; - **Transition overhead at 32**: The first tree-based size (64) may show
;;   increased per-element cost due to trie node allocation.
;;
;; - **Allocation patterns**: Thread allocation metrics reveal memory pressure
;;   differences between array and tree representations.
;;
;; - **Logarithmic factors in large range**: Tree construction adds O(log32 n)
;;   depth traversal, though this is typically dominated by the O(n) element
;;   copying.

(kind/hidden
 (bench/set-default-viewer! :print))
