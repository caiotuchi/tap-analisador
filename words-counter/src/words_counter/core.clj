(ns words-counter.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [cheshire.core :as json]))

(defn- remover-caracteres-especiais [texto]
  (str/replace texto #"[,. \/-<>0-9()\[\]♪!?:]|\n|\r|--" " "))

(defn separar-por-espacos [texto]
  (str/split texto #" "))

(defn remover-vazios [palavras]
  (filter #(not (str/blank? %)) palavras))

(defn transformar-para-minusculas [palavras]
  (map #(str/lower-case %) palavras))

(defn- filtro-palavras [texto]
  (-> texto
      (remover-caracteres-especiais)
      (separar-por-espacos)
      (remover-vazios)
      (transformar-para-minusculas))
  )


(defn- contagem-palavras [sequencia]
  (->> sequencia
       (frequencies)
       (sort-by val >)) 
  )

(defn criar-diretorio [caminho]
  (.mkdir (java.io.File. (str caminho)))
  )

(defn escrever-arquivo [caminho texto]
  (spit caminho texto))

(defn processa-arquivo [arquivo pasta-resultados]
  (let [texto (slurp arquivo)
        palavras (filtro-palavras texto)
        contagem (contagem-palavras palavras)
        nome-arquivo-entrada (.getName arquivo)
        nome-arquivo-saida (str/replace nome-arquivo-entrada #"\.srt$" ".json")
        caminho-arquivo (str pasta-resultados "/" nome-arquivo-saida)]
    ;; Verificar e criar o diretório de resultados
    (criar-diretorio pasta-resultados)

    ;; Escrever o arquivo
    (escrever-arquivo caminho-arquivo
                      (json/generate-string {palavras contagem}))))








(defn processa-diretorio [diretorio pasta-resultados]
  (let [diretorio-java (io/file diretorio)]
    (doseq [arquivo (.listFiles diretorio-java)]
      (when (.endsWith (str arquivo) ".srt") 
        (processa-arquivo arquivo pasta-resultados)))
    (println "Processo finalizado")
    (System/exit 0)))








(defn main []
  (println "Insira o diretório:")
  (let [diretorio (read-line)]
    (if (.isDirectory (io/file diretorio))
      (do
        (println "Diretório encontrado. Processando...")
        (processa-diretorio (io/file diretorio) (str diretorio "/resultados"))) ; Corrigindo esta linha
      (do
        (println "O diretório não foi encontrado. Insira um válido.")
        (main)))))






(main)
