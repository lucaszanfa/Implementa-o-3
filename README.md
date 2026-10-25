# Busca em profundidade em grafo direcionado — Java

O programa recebe o nome do arquivo e um vertice, lista todas as arestas de
arvore da floresta DFS e classifica todas as arestas que saem do vertice escolhido.

## Compilar e executar

    javac -encoding UTF-8 --release 8 Main.java
    java Main

Tambem e possivel informar os dados como argumentos:

    java Main graph-test-100-1.txt 1
    java Main graph-test-50000-1.txt 1

O arquivo deve conter `n m`, seguidos de `m` pares `origem destino`.
Os vertices devem estar entre 1 e n. Caminhos com espacos devem ser colocados
entre aspas na linha de comando (sem aspas no modo interativo).

## Ordem e classificacao

A ordem lexicografica considera os rotulos numericos: 1, 2, ..., n.
A busca comeca em 1, visita os vizinhos em ordem crescente e, ao terminar uma
componente, continua no menor vertice ainda nao visitado. O vertice informado
seleciona a classificacao exibida; ele nao altera a raiz da busca.

- **Arvore:** descobre um vertice ainda nao visitado.
- **Retorno:** aponta para um vertice ativo na pilha (ancestral ou o proprio vertice).
- **Avanco:** aponta para um descendente ja concluido, sem ser aresta de arvore.
- **Cruzamento:** aponta para um vertice concluido que nao e descendente.

As arestas de arvore sao impressas na ordem de descoberta. A classificacao do
vertice escolhido e impressa por destino crescente. Lacos sao de retorno.
Arestas paralelas sao tratadas individualmente: so a ocorrencia que descobre
um vertice e de arvore; uma repeticao posterior pode ser de avanco.

## Implementacao

A lista de adjacencia compacta (CSR) usa memoria O(n + m). O arquivo e lido
duas vezes. As listas sao ordenadas antes da busca, com custo
O(soma de d(v) log d(v)). A DFS custa O(n + m) e usa uma pilha explicita,
preservando o comportamento recursivo sem risco de estouro da pilha de chamadas
em caminhos longos. Apenas as classificacoes do vertice solicitado sao guardadas.
