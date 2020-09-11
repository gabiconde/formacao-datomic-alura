(ns ecommerce.bindingdTransactionFilters.aula05
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.produto.db.datomic :as produto.datomic]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)
(def primeiro-produto (last (produto.datomic/find-all (d/db conn))))
(pprint primeiro-produto)

(def ola
  (d/function '{:lang :clojure
                :params [nome]
                :code (str "Olá " nome)}))

(pprint (ola "gabi"))

(def ola
  (d/function '{:lang :clojure
                :params [nome]
                :code (str "Olá " nome)}))

(def incrementa-visualizacao
  #db/fn {:lang   :clojure
          :params [db produto-id]
          :code   (let [visualizacoes (d/q '[:find ?visualizacoes .
                                             :in $ ?id
                                             :where [?produto :produto/id ?id]
                                               [?produto :produto/visualizacoes ?visualizacoes]]
                                           db produto-id)
                        novo-valor (-> visualizacoes (or 0) inc)]
                    [{:produto/id            produto-id
                      :produto/visualizacoes novo-valor}])})
;instala a fn
; o codigo roda no transactor, deixando a alteracao atomica
; lock a fila em quanto processa
(pprint @(d/transact conn [{:db/doc "incrementa as visualizacoes no produto"
                            :db/ident :incrementa-visu
                            :db/fn incrementa-visualizacao}]))

(dotimes [n 10]
  (pprint @(produto.datomic/visualizacao! conn (:produto/id primeiro-produto))))

(pprint (produto.datomic/one-produto-by-id (d/db conn) (:produto/id primeiro-produto)))