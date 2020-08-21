(ns ecommerce.produto.adapter
  (:require [schema.core :as s]
            [clojure.walk :as walk]
            [ecommerce.produto.schema :as schema]))

(defn remove-nested-db-id
  [entidate]
  (if (map? entidate)
    (dissoc entidate :db/id)
    entidate))

(s/defn datomic->produto :- [schema/Produto]
  [entidades]
  (walk/prewalk remove-nested-db-id entidades))