(ns ecommerce.avancandoComModelo.aula6  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.produto.db.datomic :as produto]
            [ecommerce.categoria.db.datomic :as categoria]))

(def conn (db/abre-conexao!))

(def jogos (categoria.model/nova-categoria "jogos"))
(def eletronicos (categoria.model/nova-categoria "eletronicos"))
(categoria/insert-categoria! conn [jogos eletronicos])

(def camera (model/novo-produto "Camera" "/camera" 2500.10M))
(def celular (model/novo-produto "Celular" "/celular" 34.9M))
(def calculadora {:produto/nome "Calculadora"})
(def notebook (model/novo-produto "Notebook" "/notebook" 5648.90M))
(produto/insert-produto! conn [camera celular calculadora notebook] "127.0.0.1")

(produto/atribui-categoria! conn [camera celular notebook] eletronicos)

;insert with nested map
(produto/insert-produto! conn [{:produto/id        (model/uuid)
                                :produto/nome      "Camiseta"
                                :produto/slug      "/camiseta"
                                :produto/preco     30M
                                :produto/categoria {:categoria/nome "roupas"
                                                    :categoria/id   (categoria.model/uuid)}}] "200.123.0.1")

;insert with lookup ref by uuid
(produto/insert-produto! conn [{:produto/id        (model/uuid)
                                :produto/nome      "Xbox One"
                                :produto/slug      "/xbox-one"
                                :produto/preco     30M
                                :produto/categoria [:categoria/id (:categoria/id jogos)]}])

(produto/find-product-and-category-names (d/db conn))
(produto/produto-mais-caro (d/db conn))
(produto/produto-mais-barato (d/db conn))

(produto/todos-produtos-por-ip (d/db conn) "200.123.0.1")
(produto/todos-produtos-por-ip (d/db conn) "127.0.0.1")

;(db/apaga-banco!)
