(ns ecommerce.produto.schema
  (:require [schema.core :as s]
            [ecommerce.categoria.schema :refer [Categoria]]))

(def Variacao
  {:variacao/id   s/Uuid
   :variacao/nome s/Str
   :variacao/preco BigDecimal})

(s/def Produto
  {:produto/id                             s/Uuid
   (s/optional-key :produto/nome)          s/Str
   (s/optional-key :produto/slug)          s/Str
   (s/optional-key :produto/preco)         BigDecimal
   (s/optional-key :produto/palavra-chave) [s/Str]
   (s/optional-key :produto/categoria)     Categoria
   (s/optional-key :produto/estoque)       s/Int
   (s/optional-key :produto/digital)       s/Bool
   (s/optional-key :produto/variacao)      [Variacao]})
