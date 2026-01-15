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
;; Each function matches on 8 keyword values `:a` through `:h`.

(defn switch-case [x]
  (case x :a 1 :b 2 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 nil))

(defn switch-cond [x]
  (cond
    (= x :a) 1
    (= x :b) 2
    (= x :c) 3
    (= x :d) 4
    (= x :e) 5
    (= x :f) 6
    (= x :g) 7
    (= x :h) 8))

(defn switch-condp [x]
  (condp = x :a 1 :b 2 :c 3 :d 4 :e 5 :f 6 :g 7 :h 8 nil))

(defn switch-if [x]
  (if (= x :a) 1
      (if (= x :b) 2
          (if (= x :c) 3
              (if (= x :d) 4
                  (if (= x :e) 5
                      (if (= x :f) 6
                          (if (= x :g) 7
                              (if (= x :h) 8 nil)))))))))

;; ## Benchmarks
;;
;; Testing each approach across clause positions 0-7 (matching) and 8 (missing).
;; The numeric index is used to look up the keyword from a vector, providing
;; indirection so `case` uses its hash-based dispatch on the keyword value.

(let [lookup-keys [:a :b :c :d :e :f :g :h :missing]]
  (domain/bench
   (domain/domain-expr
    [idx [0 1 2 3 4 5 6 7 8]]
    {:case  (switch-case (lookup-keys idx))
     :cond  (switch-cond (lookup-keys idx))
     :condp (switch-condp (lookup-keys idx))
     :if    (switch-if (lookup-keys idx))})
   :domain-plan domain-plans/implementation-comparison))

(kind/hidden
 (bench/set-default-viewer! :print))
