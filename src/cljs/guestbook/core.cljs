(ns guestbook.core
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [clojure.string :as string]))

(defn send-message! [fields errors]
  (POST "/message"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(do
                     (.log js/console (str "response:" %))
                     (reset! errors nil))
         :error-handler #(do
                           (.log js/console (str %))
                           (reset! errors (get-in % [:response :errors])))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form []
  (let [fields (r/atom {})
        errors (r/atom {})]
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
         :on-click #(send-message! fields errors)}]])))

(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])

(r/render
 (home)
 (.getElementById js/document "content"))
;;(-> (.getElementById js/document "content") (.-innerHTML) (set! "hello, Justin."))




 ;;  <div class="columns is-centered">
 ;;    <div class="column is-two-thirds">
 ;;      <div class="columns">
 ;;        <div class="column">
 ;;          <h3>Messages</h3>
 ;;          <ul class="messages">
 ;;            {% for item in messages %}
 ;;            <li>
 ;;              <time>
 ;;                {{item.timestamp|date:"yyyy-MM-dd HH:mm"}}
 ;;              </time>
 ;;              <p>{{item.message}}</p>
 ;;              <p> - {{item.name}}</p>
 ;;            </li> {% endfor %} </ul>
 ;;        </div>
 ;;      </div>
 ;;      <div class="columns">
 ;;        <div class="column">
 ;;          <form method="POST" action="/message">
 ;;            {% csrf-field %}
 ;;            <div class="field">
 ;;              <label class="label" for="name"> Name </label>
 ;;              <input class="input" type="text" name="name" value="{{name}}" />
 ;;            </div>
 ;;            <div class="field">
 ;;              <label class="label" for="message"> Message
 ;;              </label>
 ;;              <textarea class="textarea" name="message">
 ;; {{message}}
 ;;              </textarea>
 ;;            </div>
 ;;            <input type="submit" class="button is-primary" value="comment" />
 ;;          </form>
 ;;        </div>
 ;;      </div>
 ;;    </div>
 ;;  </div>
