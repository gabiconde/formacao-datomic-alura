(ns ecommerce.categoria.adapter
  (:require [schema.core :as s]
            [ecommerce.categoria.schema :refer [Categoria]]))

(defn datomic->categoria
  [entidades]
  (map #(dissoc % :db/id) entidades))

