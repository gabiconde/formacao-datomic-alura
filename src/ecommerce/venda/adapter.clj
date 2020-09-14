(ns ecommerce.venda.adapter)

(defn datomic->venda
  [entidades]
  (if (map? entidades)
    (map #(dissoc % :db/id) entidades)
    entidades))
