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
                      :db/unique      :db.unique/identity}
                     {:db/ident       :produto/categoria
                      :db/valueType   :db.type/ref
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :produto/estoque
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :produto/digital
                      :db/valueType   :db.type/boolean
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :produto/variacao
                      :db/valueType   :db.type/ref
                      :db/isComponent true
                      :db/cardinality :db.cardinality/many}
                     {:db/ident       :produto/visualizacoes
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/noHistory   true}

                     {:db/ident       :venda/id
                      :db/valueType   :db.type/uuid
                      :db/cardinality :db.cardinality/one
                      :db/unique      :db.unique/identity}
                     {:db/ident       :venda/produto
                      :db/valueType   :db.type/ref
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :venda/quantidade
                      :db/valueType   :db.type/long
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :venda/status
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one}

                     {:db/ident       :categoria/nome
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :categoria/id
                      :db/valueType   :db.type/uuid
                      :db/cardinality :db.cardinality/one
                      :db/unique      :db.unique/identity}

                     {:db/ident      :tx-data/ip
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one}

                     {:db/ident       :variacao/nome
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one}
                     {:db/ident       :variacao/id
                      :db/valueType   :db.type/uuid
                      :db/cardinality :db.cardinality/one
                      :db/unique      :db.unique/identity}
                     {:db/ident       :variacao/preco
                      :db/valueType   :db.type/bigdec
                      :db/cardinality :db.cardinality/one}])

(defn uuid []
  (UUID/randomUUID))

(defn novo-produto
  ([nome slug preco estoque]
   (novo-produto (uuid) nome slug preco estoque))
  ([uuid nome slug preco estoque]
   {:produto/id      uuid
    :produto/nome    nome
    :produto/slug    slug
    :produto/preco   preco
    :produto/estoque estoque
    :produto/digital false}))
