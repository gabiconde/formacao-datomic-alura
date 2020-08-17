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
