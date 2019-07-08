(ns guestbook.validation
  (:require
   [struct.core :as st]))

(def message-schema
  [[:name
    st/required
    st/string]
   [:message
    st/required
    st/string
    {:message "message must contain at least 5 characters"
     :validate (fn [msg] (>= (count msg) 5))}]])

(defn validate-message [params]
  (first (st/validate params message-schema)))

