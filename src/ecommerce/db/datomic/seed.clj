(ns ecommerce.db.datomic.seed
  (:require [ecommerce.produto.db.produto :as produto]
            [ecommerce.produto.model :as produto.model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.categoria.db.categoria :as categoria]))

;categorias
(def jogos (categoria.model/nova-categoria "jogos"))
(def eletronicos (categoria.model/nova-categoria "eletronicos"))

;produtos
(def camera (produto.model/novo-produto "Camera" "/camera" 2500.10M 5))
(def celular (produto.model/novo-produto "Celular" "/celular" 34.9M 56))
(def notebook (produto.model/novo-produto "Notebook" "/notebook" 5648.90M 0))
(def cod (assoc (produto.model/novo-produto "C.O.D." "/jogo-cod" 46M 0) :produto/digital true))

(defn insert-seeds!
  [conn]
  (categoria/insert! conn [jogos eletronicos])
  (produto/upsert! conn [camera celular notebook cod] "127.0.0.1")
  (produto/atribui-categoria! conn [camera celular notebook cod] eletronicos))