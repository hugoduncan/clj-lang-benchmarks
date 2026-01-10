(ns index
  {:clay             {:title "Clojure Language Benchmarks"}
   :kindly/hide-code true}
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [scicloj.kindly.v4.kind :as kind]))

;; # Clojure Language Benchmarks
;;
;; This project benchmarks Clojure implementations using criterium,
;; with results published as Clay notebooks.
;;
;; [Source on GitHub](https://github.com/hugoduncan/clj-lang-benchmarks)

;; ## Benchmarks

^:kindly/hide-code
(do
  (defn- notebook-files
    "Returns .clj files in notebooks/ excluding index.clj."
    []
    (->> (io/file "notebooks")
         (.listFiles)
         (filter #(and (.isFile %)
                       (str/ends-with? (.getName %) ".clj")
                       (not= "index.clj" (.getName %))))
         (sort-by #(.getName %))))

  (defn- read-ns-form
    "Reads the first form from a file, expected to be an ns form."
    [file]
    (with-open [rdr (java.io.PushbackReader. (io/reader file))]
      (edn/read rdr)))

  (defn- extract-title
    "Extracts :clay {:title ...} from ns form metadata, or derives from filename."
    [file ns-form]
    (let [ns-meta (when (and (sequential? ns-form)
                             (>= (count ns-form) 2)
                             (= 'ns (first ns-form)))
                    (let [second-elem (nth ns-form 2 nil)]
                      (when (map? second-elem) second-elem)))]
      (or (get-in ns-meta [:clay :title])
          (-> (.getName file)
              (str/replace #"\.clj$" "")
              (str/replace "_" " ")
              str/capitalize))))

  (defn- notebook-link
    "Generates hiccup for a notebook link."
    [file]
    (let [ns-form   (read-ns-form file)
          title     (extract-title file ns-form)
          html-name (-> (.getName file)
                        (str/replace #"\.clj$" ".html"))]
      [:li [:a {:href html-name} title]]))
  "")

^:kindly/hide-code
(kind/hiccup
 (into [:ul]
       (map notebook-link)
       (notebook-files)))
