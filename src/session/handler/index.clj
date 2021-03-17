(ns session.handler.index
  (:require [session.views.index :as views.index]
            [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(def authdata
  {:admin "qwerty"
   :test "password"
   :a "a"})

(defn unauthorized-handler
  [request metadata]
  (if (authenticated? request)
    [::response/ok (views.index/error)]
    (let [current-url (:uri request)]
      {:status 302, :headers {"Location" (format "/login?next=%s" current-url)}, :body nil})))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(defmethod ig/init-key :session.handler.index/session-auth [_ _]
  (fn [handler]
    (-> handler
        (wrap-authorization auth-backend)
        (wrap-authentication auth-backend))))

;; ハンドラ
(defmethod ig/init-key :session.handler.index/home [_ _]
  (fn [handler]
    (if-not (authenticated? handler)
      (throw-unauthorized)
      [::response/ok (views.index/home)])))

(defmethod ig/init-key :session.handler.index/login [_ _]
  (fn [_]
    [::response/ok (views.index/login)]))

(defmethod ig/init-key :session.handler.index/login-authenticate [_ _]
  (fn [{:keys [params session query-params]}]
    (let [name (get params :name "")
          password (get params :password "")
          found-password (get authdata (keyword name))]
      (if (and found-password (= found-password password))
        (let [next-url (get query-params :next "/")
              updated-session (assoc session :identity (keyword name))]
          (-> {:status 302, :headers {"Location" next-url}, :body nil}
              (assoc :session updated-session)))
        [::response/ok (views.index/login)]))))

(defmethod ig/init-key :session.handler.index/logout [_ _]
  (fn [_]
    (-> {:status 302, :headers {"Location" "/login"}, :body nil}
        (assoc :session {}))))
