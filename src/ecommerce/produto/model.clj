(ns ecommerce.produto.model
  (:import (java.util UUID)))

(def produto-schema [{:db/ident       :produto/nome
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "O nome do produto"}
                     {:db/ident       :produto/slug
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "atalho de acesso"}
                     {:db/ident       :produto/preco
                      :db/valueType   :db.type/bigdec
                      :db/cardinality :db.cardinality/one
                      :db/doc         "Valor unitário do produto"}
                     {:db/ident       :produto/tags
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/many}
                     {:db/ident       :produto/id
                      :db/valueType   :db.type/uuid
                      :db/cardinality :db.cardinality/one
                      :db/unique      :db.unique/identity}])
(defn uuid []
  (UUID/randomUUID))

(defn novo-produto
  ([nome slug preco]
   (novo-produto (uuid) nome slug preco))
  ([uuid nome slug preco]
   {:produto/id    uuid
    :produto/nome  nome
    :produto/slug  slug
    :produto/preco preco}))
