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

;foward navigation
(defn find-produto-by-categoria-foward
  [db nome-categoria]
  (d/q '[:find (pull ?produto [:produto/nome :produto/slug :produto/preco {:produto/categoria [:categoria/nome]}])
         :in $ ?nome
         :where [?categoria :categoria/nome ?nome]
                [?produto :produto/categoria ?categoria]]
       db nome-categoria))

;backward navigation
(defn find-produto-by-categoria-backward
  [db nome-categoria]
  (d/q '[:find (pull ?categoria [:categoria/nome {:produto/_categoria [:produto/nome :produto/slug :produto/preco]}])
         :in $ ?nome
         :where [?categoria :categoria/nome ?nome]
         [?produto :produto/categoria ?categoria]]
       db nome-categoria))
