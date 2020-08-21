(ns ecommerce.db.datomic.seed
  (:require [ecommerce.produto.db.datomic :as produto]
            [ecommerce.produto.model :as produto.model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.categoria.db.datomic :as categoria]))

;categorias
(def jogos (categoria.model/nova-categoria "jogos"))
(def eletronicos (categoria.model/nova-categoria "eletronicos"))

;produtos
(def camera (produto.model/novo-produto "Camera" "/camera" 2500.10M))
(def celular (produto.model/novo-produto "Celular" "/celular" 34.9M))
(def notebook (produto.model/novo-produto "Notebook" "/notebook" 5648.90M))

(defn insert-seeds!
  [conn]
  (categoria/insert-categoria! conn [jogos eletronicos])
  (produto/upsert-produto! conn [camera celular notebook] "127.0.0.1")
  (produto/atribui-categoria! conn [camera celular notebook] eletronicos))