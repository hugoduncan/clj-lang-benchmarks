# clj-lang-benchmarks Project Conventions

## Project Purpose

Benchmarking Clojure implementations using criterium, with results published as Clay notebooks.

## Clojure Style

- Enable `*warn-on-reflection*` and `*unchecked-math* :boxed` when developing
- Type hint where necessary to avoid reflection
- Run benchmarks with criterium's `bench` or `quick-bench`

## Benchmark Structure

Each benchmark should be a Clay notebook in `notebooks/` that:

1. Defines the code being benchmarked
2. Explains what is being measured and why
3. Runs criterium benchmarks
4. Displays results

Example structure:

```clojure
(ns notebooks.example-benchmark
  (:require [criterium.core :as criterium]))

;; ## What we're benchmarking
;; Explanation of the benchmark purpose

;; ## Implementation
(defn implementation-1 [x] ...)
(defn implementation-2 [x] ...)

;; ## Benchmarks
(criterium/bench (implementation-1 test-data))
(criterium/bench (implementation-2 test-data))

;; ## Analysis
;; Discussion of results
```

## Clay Notebook Conventions

- Notebooks live in `notebooks/`
- Index notebook (`notebooks/index.clj`) links to all benchmarks
- Build with `clj -T:build notebooks`
- Output goes to `docs/` for GitHub Pages

## Code Quality

Pre-commit hook runs:
- cljfmt check
- clj-kondo lint

Run `clj -M:dev` for a REPL with warnings enabled.
