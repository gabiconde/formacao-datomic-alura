(ns ecommerce.categoria.db.datomic
  (:require [datomic.api :as d]))

(defn todas-as-categorias
  [db]
  (d/q '[:find (pull ?categoria [*])
         :where [?categoria :categoria/nome]]
       db))

(defn insert-categoria!
  [conn categoria]
  @(d/transact conn categoria))

(defn find-produto-by-categoria
  [db nome-categoria]
  (d/q '[:find (pull ?produto [:produto/nome :produto/slug :produto/preco {:produto/categoria [:categoria/nome]}])
         :in $ ?nome
         :where [?categoria :categoria/nome ?nome]
                [?produto :produto/categoria ?categoria]]
       db nome-categoria))
