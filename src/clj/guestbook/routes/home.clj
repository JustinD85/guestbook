(ns guestbook.routes.home
  (:require
    [guestbook.layout :as layout]
    [guestbook.middleware :as middleware]))

;; (defn message-list [_]
;;   (response/ok (msg/message-list)))

;; (defn save-message! [{:keys [params]}]
;;   (if-let [errors (validate-message params)]
;;     (response/bad-request {:errors errors})
;;     (try
;;       (msg/save-message! params)
;;       (response/ok {:status :ok})
;;       (catch Exception e
;;         (let [{id :guestbook/error-id
;;                errors :errors} (ex-data e)]
;;           (case id
;;             :validation
;;             (response/bad-request {:errors errors})
;;             (response/internal-server-error
;;              {:errors {:server-error ["Failed to save message!"]}})))))))

(defn home-page [{:keys [flash] :as request}]
  (layout/render
   request
   "home.html"))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])

