(ns ecommerce.db.datomic.produto
  (:require [datomic.api :as d]))

(defn find-all [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/nome]] db))

(defn find-by-slug
  [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug-buscado
         :where [?entidade :produto/slug ?slug-buscado]]
       db slug))

(defn update-produto
  [conn id-entity attr value]
  @(d/transact conn [[:db/add id-entity attr value]]))

(defn remove-produto
  [conn id-entity attr value]
  @(d/transact conn [[:db/retract id-entity attr value]]))