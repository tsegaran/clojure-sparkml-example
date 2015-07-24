(defproject clojure-digits "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [yieldbot/flambo "0.6.0-SNAPSHOT"]
                 [org.apache.spark/spark-mllib_2.10 "1.3.0"]]
  :main ^:skip-aot clojure-digits.core
  :target-path "target/%s"
  :profiles {:dev {:aot [flambo.function]}
            :provided {:dependencies
                         [[org.apache.spark/spark-core_2.10 "1.3.0"]
                          [org.apache.spark/spark-streaming_2.10 "1.3.0"]
                          [org.apache.spark/spark-streaming-kafka_2.10 "1.3.0"]
                          [org.apache.spark/spark-streaming-flume_2.10 "1.3.0"]
                          [org.apache.spark/spark-sql_2.10 "1.3.0"]]}
            :uberjar {:aot :all}}
  :jvm-opts ^:replace ["-server" "-Xmx2g"])
