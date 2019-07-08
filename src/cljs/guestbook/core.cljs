(ns guestbook.core
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]))

(defn send-message! [fields]
  (POST "/message"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(.log js/console (str "response:" %))
         :error-handler #(.error js/console (str "error:" %))}))

(defn message-form []
  (let [fields (r/atom {})]
    (fn []
      [:div
       [:div.field
        [:label.label {:for :name} "Name"]
        [:input.input
         {:type :text
          :name :name
          :on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         :value "comment"
         :on-click #(send-message! fields)}]])))

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
