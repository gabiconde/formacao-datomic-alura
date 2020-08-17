(ns ecommerce.categoria.model
  (:import (java.util UUID)))

(def categoria-schema [{:db/ident       :categoria/nome
                        :db/valueType   :db.type/string
                        :db/cardinality :db.cardinality/one}
                       {:db/ident       :categoria/id
                        :db/valueType   :db.type/uuid
                        :db/cardinality :db.cardinality/one
                        :db/unique      :db.unique/identity}])

(defn uuid []
  (UUID/randomUUID))

(defn nova-categoria
  ([nome] (nova-categoria nome (uuid)))
  ([nome id]
   {:categoria/nome nome
    :categoria/id id}))