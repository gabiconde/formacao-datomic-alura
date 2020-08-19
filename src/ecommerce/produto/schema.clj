(ns ecommerce.produto.schema
  (:require [schema.core :as s]
            [ecommerce.categoria.schema :refer [Categoria]]))

(s/def Produto
  {:produto/id                             s/Uuid
   :produto/nome                           s/Str
   :produto/slug                           s/Str
   :produto/preco                          BigDecimal
   (s/optional-key :produto/palavra-chave) [s/Str]
   (s/optional-key :produto/categoria)     Categoria})
