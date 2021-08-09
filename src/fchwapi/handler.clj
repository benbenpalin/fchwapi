(ns fchwapi.handler
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [fchwapi.config :refer [api-key]]))


(defn call-enrich []
  (:body (client/post "https://api.fullcontact.com/v3/person.enrich"
                    {:headers {:Authorization api-key}
                     :content-type :json
                     :form-params  {:phone "+13035551234"}})))

(defroutes app-routes
  (POST "/enrich" []
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body (call-enrich)})
  (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
