(ns com.github.meandor.authentication.client)

(defn nonce []
  (* 100000000 (Math/random)))