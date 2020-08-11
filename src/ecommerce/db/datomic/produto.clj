(ns ecommerce.db.datomic.produto
  (:require [datomic.api :as d]))

;pull explicit attr by attr
(defn find-all [db]
  (d/q '[:find (pull ?entidade [:produto/nome :produto/preco :produto/slug])
         :where [?entidade :produto/nome]] db))

;pull generic return all including the id
(defn find-all2 [db]
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :produto/nome]] db))

(defn find-by-slug
  [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug-buscado
         :where [?entidade :produto/slug ?slug-buscado]]
       db slug))
;any entity that has the attribute :produto/slug

(defn find-all-slugs
  [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))
;use _ when we don't care about the data

(defn find-price-and-name
  [db]
  (d/q '[:find ?nome ?preco
         ;:keys nome preco ;[{:nome x :preco y}]
         :keys produto/nome produto/preco ;[#:produto{:nome x :preco y}]
         :where [?produto :produto/preco ?preco]
                [?produto :produto/nome ?nome]] db))
;if the ?produto is no explicit the return will be a cartesian product
; of all values it found [nome1, preco1] [nome1, preco2] [nome2, preco1] [nome2, preco2]

(defn find-by-price
  [db valor-minimo]
  (d/q '[:find ?nome ?preco
         :in $ ?valor-minimo
         :where [?produto :produto/preco ?preco]
                [(> ?preco ?valor-minimo)]
                [?produto :produto/nome ?nome]]
       db valor-minimo))

(defn find-by-tag
  [db tag]
  (d/q '[:find (pull ?produto [*])
         :in $ ?tag
         :where [?produto :produto/tags ?tag]]
       db tag))

(defn update-produto
  [conn id-entity attr value]
  @(d/transact conn [[:db/add id-entity attr value]]))

(defn remove-produto
  [conn id-entity attr value]
  @(d/transact conn [[:db/retract id-entity attr value]]))