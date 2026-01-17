# clj-lang-benchmarks

Clojure performance benchmarks using [criterium](https://github.com/hugoduncan/criterium). Results are published as interactive notebooks.

## View Results

**https://hugoduncan.github.io/clj-lang-benchmarks/**

## Available Benchmarks

- **Map Accessor Benchmarks** - Comparing performance of different map and record access patterns (keyword lookup, destructuring, field access)

- **Not-Empty Predicate Benchmarks** - Comparing `(seq x)` vs `(not (empty? x))` for checking if collections are non-empty
