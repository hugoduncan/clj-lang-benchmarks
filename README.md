# clj-lang-benchmarks

Clojure benchmarking project using [criterium](https://github.com/hugoduncan/criterium) for accurate performance measurements. Results are presented as [Clay](https://scicloj.github.io/clay/) notebooks published via GitHub Pages.

## Project Structure

```
├── notebooks/         Clay benchmark notebooks
├── dev/user.clj       Development namespace
├── build.clj          Build tasks
├── clay.edn           Clay configuration
└── docs/              Generated HTML output (gitignored)
```

## Running Benchmarks

Start a REPL with the dev alias:

```bash
clj -M:dev
```

Reflection warnings and boxed math warnings are enabled by default in the dev namespace.

For an nREPL server:

```bash
clj -M:nrepl
```

## Building Notebooks

Build all notebooks to `docs/`:

```bash
clj -T:build notebooks
```

## Publishing

There are two publishing methods, both deploying to the `gh-pages` branch:

### Automatic (GitHub Actions)

Pushing to `master` triggers the GitHub Actions workflow which builds
notebooks and deploys to GitHub Pages automatically.

### Manual

The `publish` task builds notebooks locally and pushes to `gh-pages`:

```bash
clj -T:build publish
```

This uses git subtree to push `docs/` content to the `gh-pages` branch
without committing to `master`.

## Viewing Results

Published benchmarks are available at:
https://hugoduncan.github.io/clj-lang-benchmarks/
