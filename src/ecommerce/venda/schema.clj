(ns ecommerce.venda.schema
  (:require [schema.core :as s]
            [ecommerce.produto.schema :as produto.schema]))

(s/def Venda
  {:venda/id                          s/Uuid
   (s/optional-key :venda/produto)    produto.schema/Produto
   (s/optional-key :venda/quantidade) s/Int})