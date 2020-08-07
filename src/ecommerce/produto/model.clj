(ns ecommerce.produto.model)

(def produto-schema [{:db/ident         :produto/nome
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "O nome do produto"}
                     {:db/ident     :produto/slug
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc       "atalho de acesso"}
                     {:db/ident     :produto/preco
                      :db/valueType :db.type/bigdec
                      :db/cardinality :db.cardinality/one
                      :db/doc       "Valor unitário do produto"}])

(defn novo-produto
  [nome slug preco]
  {:produto/nome  nome
   :produto/slug  slug
   :produto/preco preco})
