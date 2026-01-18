(ns vector-accessors
  {:clay {:title "Vector Accessor Benchmarks"}}
  (:require
   [criterium.bench :as bench]
   [criterium.domain :as domain]
   [criterium.domain-plans :as domain-plans]
   [scicloj.kindly.v4.kind :as kind]))

(kind/hidden
 (bench/set-default-viewer! :kindly))

;; # Vector Accessor Benchmarks
;;
;; Comparing performance of different vector element access patterns in Clojure.

;; ## Why Benchmark Vector Access?
;;
;; Clojure vectors (PersistentVector) provide O(log32 n) indexed access via
;; a trie structure. Several access methods exist with varying overhead:
;;
;; - **IFn invoke**: Vectors implement IFn, allowing `(v i)` syntax
;; - **clojure.core/get**: Generic associative lookup
;; - **clojure.core/nth**: Sequence/indexed access
;; - **Direct method call**: Bypasses var lookup and dispatch
;;
;; Understanding the relative costs helps choose the right access pattern
;; for performance-critical code.

;; ## Test Data

(def v [0 1 2 3 4 5 6 7])
(def i 4)

;; ## Basic Access (No Default)
;;
;; Four ways to access a vector element without providing a default value.
;; The direct `.nth` call requires a type hint to avoid reflection.

(domain/bench
 (domain/domain-expr
  [test-vec [v]
   idx [i]]
  {:invoke     (test-vec idx)
   :get        (get test-vec idx)
   :nth        (nth test-vec idx)
   :direct-nth (.nth ^clojure.lang.Indexed test-vec idx)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Access with Default Value
;;
;; When accessing indices that might be out of bounds, providing a default
;; avoids exceptions. The direct `.valAt` call uses IPersistentVector's
;; two-argument form.

(def default :not-found)

(domain/bench
 (domain/domain-expr
  [test-vec [v]
   idx [i]
   dflt [default]]
  {:get          (get test-vec idx dflt)
   :nth          (nth test-vec idx dflt)
   :direct-valAt (.valAt ^clojure.lang.IPersistentVector test-vec idx dflt)})
 :domain-plan domain-plans/implementation-comparison)

(kind/hidden
 (bench/set-default-viewer! :print))
