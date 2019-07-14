(ns guestbook.core
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]
            [re-frame.core :as rf]
            [cognitect.transit :as t]
            [guestbook.validation :refer [validate-message]]))
(rf/reg-sub
 :messages/loading?
 (fn [db _]
   (:messages/loading? db)))

(rf/reg-event-fx
 :app/initialize
 (fn [_ _]
   {:db {:messages/loading? true}}))

(rf/reg-event-db
 :messages/set
 (fn [db [_ messages]]
   (-> db
       (assoc :messages/loading? false
              :messages/list messages))))
(rf/reg-event-db
 :message/add
 (fn [db [_ message]]
   (update db :messages/list conj message)))

(rf/reg-sub
 :messages/list
 (fn [db _]
   (:messages/list db [])))

(defn message-list [messages]
  [:ul.messages
   (let [messages (reverse @messages)]
     (for [{:keys [timestamp message name]} messages]
       ^{:key timestamp}
       [:li
        [:time (.toLocaleString timestamp )]
        [:p message]
        [:p " - " name]]))])

(defn get-messages []
  (GET "api/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(rf/dispatch [:messages/set (:messages %)])}))

(defn send-message! [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "api/message"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       (rf/dispatch  [:message/add (assoc @fields :timestamp (js/Date.))])
                       (reset! fields nil)
                       (reset! errors nil))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         :value "comment"
         :on-click #(send-message! fields errors )}]])))

(defn home []
  (let [messages (rf/subscribe [:messages/list])]
    (rf/dispatch [:app/initialize])
    (get-messages)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       (if @(rf/subscribe [:messages/loading?])
         [:h3
          "Loading Messages..."]
         [:div ;;because of else in this if we use this div
         [:div.columns>div.column
          [message-form messages]]
         [:div.columns>div.column
          [:h3 "Messages"]
          [message-list messages]]])])))

(r/render
 (home)
 (.getElementById js/document "content"))
