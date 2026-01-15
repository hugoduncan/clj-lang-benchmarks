(ns switch
  {:clay {:title "Case/Cond/Condp Benchmarks"}}
  (:require
   [criterium.bench :as bench]
   [criterium.domain :as domain]
   [criterium.domain-plans :as domain-plans]
   [criterium.jvm :as jvm]
   [scicloj.kindly.v4.kind :as kind]))

(kind/hidden
 (bench/set-default-viewer! :kindly))

;; # Switch/Case Benchmarks
;;
;; Comparing performance of value matching approaches in Clojure.

;; ## Environment

*clojure-version*

(jvm/runtime-details)

(jvm/os-details)

;; ## Semantics
;;
;; Clojure provides several ways to dispatch on a value:
;;
;; - **case**: Compile-time hash table lookup. O(1) constant time regardless of
;;   which clause matches. Values must be compile-time literals.
;;
;; - **cond**: Evaluates test expressions sequentially until one returns true.
;;   O(n) linear time - earlier clauses are faster.
;;
;; - **condp**: Like cond but with a shared predicate. Evaluates sequentially.
;;   O(n) linear time - earlier clauses are faster.
;;
;; - **if** (nested): Chain of if expressions. Evaluates sequentially.
;;   O(n) linear time - earlier clauses are faster.

;; ## Implementations
;;
;; Each function matches on 8 long values 1 through 8.

(defn switch-case [^long x]
  (case x 1 :a 2 :b 3 :c 4 :d 5 :e 6 :f 7 :g 8 :h nil))

(defn switch-cond [^long x]
  (cond
    (= x 1) :a
    (= x 2) :b
    (= x 3) :c
    (= x 4) :d
    (= x 5) :e
    (= x 6) :f
    (= x 7) :g
    (= x 8) :h))

(defn switch-condp [^long x]
  (condp = x 1 :a 2 :b 3 :c 4 :d 5 :e 6 :f 7 :g 8 :h nil))

(defn switch-if [^long x]
  (if (= x 1) :a
      (if (= x 2) :b
          (if (= x 3) :c
              (if (= x 4) :d
                  (if (= x 5) :e
                      (if (= x 6) :f
                          (if (= x 7) :g
                              (if (= x 8) :h nil)))))))))

;; ## Benchmarks
;;
;; Testing each approach across lookup values 1-8 (matching clauses) and 0
;; (missing). `case` should show constant time while others show increasing
;; time for later positions.

(domain/bench
 (domain/domain-expr
  [lookup-val [1 2 3 4 5 6 7 8 0]]
  {:case  (switch-case lookup-val)
   :cond  (switch-cond lookup-val)
   :condp (switch-condp lookup-val)
   :if    (switch-if lookup-val)})
 :domain-plan domain-plans/implementation-comparison)

(kind/hidden
 (bench/set-default-viewer! :print))
