(ns ecommerce.categoria.schema
  (:require [schema.core :as s]))

(s/def Categoria
  {:categoria/id   s/Uuid
   :categoria/nome s/Str})
