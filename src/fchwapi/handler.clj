(ns fchwapi.handler
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [fchwapi.config :refer [api-key]]))

(defn pull-basic-profile [profile]
  (let [profile-map (json/read-str profile)]
    (merge
      (select-keys profile-map ["fullName" "avatar" "title" "organization"])
      (select-keys (get profile-map "details") ["phones" "emails" "profiles"]))))

(defn call-enrich [customer-info-type customer-info]
  (pull-basic-profile
    (:body
      (client/post "https://api.fullcontact.com/v3/person.enrich"
        {:headers      {:Authorization api-key}
         :content-type :json
         :form-params  {customer-info-type customer-info}}))))

(defroutes app-routes
  (POST "/enrich" req
    (let [type (get-in req [:params :customerInfoType])
          info (get-in req [:params :customerInfo])]
      {:status   200
       :headers {"Content-Type" "application/json"}
       :body    (json/write-str (call-enrich type info))}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
