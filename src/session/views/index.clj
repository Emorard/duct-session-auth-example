(ns session.views.index
  (:require [hiccup.form :refer [form-to label text-field submit-button password-field]]
            [hiccup.page :refer [html5]]
            [hiccup.element :refer [link-to]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn home []
  (html5
   [:body
    [:p "こんちわ"]
    [:p (link-to "/logout" "ログアウト")]]))

(defn login []
  (html5
   [:body
    [:h2 "ログイン"]
    [:div
     (form-to [:post "/login"]
              (anti-forgery-field)
              (label :name "Name:")
              (text-field {:placeholder "Username"} :name)
              (label :password "Password:")
              (password-field {:placeholder "Password"} :password)
              (submit-button "Submit"))]]))

(defn error []
  (html5
   [:h1 "エラー！"]))