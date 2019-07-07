(ns guestbook.core)

(-> (.getElementById js/document "content") (.-innerHTML) (set! "hello, Justin."))
