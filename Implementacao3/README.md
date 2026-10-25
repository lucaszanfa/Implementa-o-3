# Implementação 3 — Caminho mínimo com menos arestas

Solução em Java 8 ou superior. Dijkstra minimiza primeiro o peso total e depois o número de arestas. Pesos positivos inteiros ou decimais são tratados exatamente com BigDecimal.

## Compilar e executar

No terminal, dentro de `Implementacao3`:

```powershell
javac -encoding UTF-8 --release 8 Main.java Testes.java
java Main exemplo.txt 1 6
```

Saída esperada (o tempo varia):

```text
Comprimento minimo: 4
Numero de arestas: 2
Caminho: 1 -> 4 -> 6
```

Também é possível executar `java Main` e responder às perguntas. Use aspas em caminhos de arquivo com espaços na linha de comando.

## Formato do arquivo

```text
n m
origem destino peso
... (exatamente m triplas)
```

Vértices de 1 a n, pesos estritamente positivos, decimais com ponto. Origem e destino da consulta são argumentos do programa. Arquivos sem pesos da pasta superior pertencem à atividade anterior e não servem como entrada desta solução.

Se não existir caminho, o programa informa isso. Origem igual ao destino retorna peso 0, zero arestas e o próprio vértice. Arestas paralelas e laços são aceitos. Entre caminhos com o mesmo peso e o mesmo número de arestas, qualquer um pode ser retornado.

## Testes e relatório

```powershell
java Testes
python gerar_relatorio.py
```

O primeiro comando compara a solução com Floyd-Warshall em 200 grafos aleatórios, para todos os pares; verifica o caminho reconstruído e casos especiais. Também gera oito instâncias: grafos esparsos e densos, cada um com 100, 200, 400 e 800 vértices. Cada instância tem 10 aquecimentos e 21 medições. Os tempos excluem leitura e impressão.

- `instancias/`: arquivos ponderados usados no experimento.
- `resultados.csv`: resultados reais, mediana, mínimo e máximo das medições.
- `relatorio.pdf`: algoritmo, justificativa, metodologia e uma tabela por tipo de grafo.
- `gerar_relatorio.py`: recria o PDF; requer Python com ReportLab (`python -m pip install reportlab`).

A implementação e os testes não dependem de Python. Para executar outra instância:

```powershell
java Main instancias/denso-800.txt 1 800
```

A fila mantém entradas antigas e as descarta ao removê-las. A complexidade é O(V + E log(E + 1)) em tempo e O(V + E) em memória, sem contabilizar o custo variável da aritmética decimal. Consulte o PDF para detalhes e limitações das medições.
