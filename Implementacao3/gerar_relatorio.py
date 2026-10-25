from pathlib import Path
import csv
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4

base = Path(__file__).resolve().parent
with (base / 'resultados.csv').open(encoding='utf-8') as arquivo:
    rows = list(csv.DictReader(arquivo))
styles = getSampleStyleSheet()
styles.add(ParagraphStyle('TituloTrabalho', fontName='Times-Bold', fontSize=15,
                          leading=19, alignment=TA_CENTER, spaceAfter=16))
styles.add(ParagraphStyle('TextoTrabalho', fontName='Times-Roman', fontSize=11,
                          leading=13, alignment=TA_JUSTIFY, firstLineIndent=22,
                          spaceAfter=6))
styles.add(ParagraphStyle('SecaoTrabalho', fontName='Times-Bold', fontSize=12,
                          leading=15, spaceBefore=8, spaceAfter=5, keepWithNext=True))
styles.add(ParagraphStyle('LegendaTrabalho', fontName='Times-Roman', fontSize=10,
                          leading=12, spaceBefore=5, spaceAfter=6, keepWithNext=True))
styles.add(ParagraphStyle('NotaTrabalho', fontName='Times-Roman', fontSize=9,
                          leading=12, spaceAfter=6))
story = []
def p(text, style='TextoTrabalho'):
    story.append(Paragraph(text, styles[style]))

def tabela(tipo, numero):
    p(f'Tabela {numero} – Resultados para o grafo {tipo.lower()}.', 'LegendaTrabalho')
    data = [['Vértices', 'Arestas', 'Peso do\ncaminho', 'Arestas do\ncaminho', 'Tempo mediano\n(ms)']]
    for r in rows:
        if r['tipo'] == tipo:
            data.append([r['vertices'], r['arestas'], r['comprimento'],
                         r['arestas_caminho'], f"{float(r['mediana_ms']):.4f}".replace('.', ',')])
    table = Table(data, colWidths=[64, 77, 90, 93, 121], repeatRows=1, hAlign='CENTER')
    table.setStyle(TableStyle([
        ('FONTNAME', (0,0), (-1,-1), 'Times-Roman'),
        ('FONTNAME', (0,0), (-1,0), 'Times-Bold'),
        ('FONTSIZE', (0,0), (-1,-1), 10),
        ('LEADING', (0,0), (-1,-1), 12),
        ('ALIGN', (0,0), (-1,-1), 'CENTER'),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('LINEABOVE', (0,0), (-1,0), 0.7, colors.black),
        ('LINEBELOW', (0,0), (-1,0), 0.4, colors.black),
        ('LINEBELOW', (0,-1), (-1,-1), 0.7, colors.black),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(table)
    story.append(Spacer(1, 8))

p('Implementação 3<br/>Caminho mínimo com o menor número de arestas', 'TituloTrabalho')
p('1. Objetivo', 'SecaoTrabalho')
p('O objetivo deste trabalho foi implementar uma busca de caminho mínimo em grafos direcionados e ponderados, considerando apenas pesos positivos. Além de encontrar o menor peso total entre a origem e o destino, o programa deve escolher o caminho com menos arestas quando houver empate no peso.')
p('2. Implementação', 'SecaoTrabalho')
p('A solução foi desenvolvida em Java, usando listas de adjacência para armazenar o grafo e o algoritmo de Dijkstra para realizar a busca. Para cada vértice, são guardados a menor distância encontrada, o número de arestas desse percurso e o vértice anterior, que permite reconstruir o caminho ao final.')
p('A principal adaptação está no critério de comparação. Um novo caminho substitui o atual quando tem peso menor ou quando tem o mesmo peso e usa menos arestas. A fila de prioridade segue essa mesma ordem: primeiro a distância e depois a quantidade de arestas. Entradas da fila que já foram substituídas por resultados melhores são ignoradas.')
p('Como os pesos são positivos, a distância aumenta a cada aresta percorrida. Isso permite manter a propriedade de Dijkstra: ao retirar da fila um vértice com seu resultado atualizado, esse resultado já é definitivo. O desempate pela quantidade de arestas também é respeitado pela ordem da fila.')
p('Os pesos são representados por BigDecimal para evitar erros de arredondamento nas comparações. Por exemplo, os valores 0.1 + 0.2 e 0.3 precisam ser considerados iguais para que o desempate funcione corretamente. Se a origem for igual ao destino, o resultado é um caminho de peso zero e sem arestas. Quando o destino não é alcançável, o programa informa que não existe caminho.')
p('A implementação ocupa O(V + E) de memória. Como a fila pode manter entradas antigas, o custo de tempo é O(V + E log(E + 1)), desconsiderando o custo variável das operações com BigDecimal. V representa o número de vértices e E, o número de arestas.')
p('3. Entrada e exemplo de execução', 'SecaoTrabalho')
p('O arquivo de entrada começa com o número de vértices e de arestas. Depois, cada linha descreve uma aresta no formato origem, destino e peso. Os vértices são numerados de 1 a n. O nome do arquivo e os vértices da consulta podem ser informados pelo terminal, como em <font name="Courier" size="9">java Main exemplo.txt 1 6</font>.')
p('No arquivo de exemplo, existem dois caminhos de peso 4: 1 → 2 → 3 → 6 e 1 → 4 → 6. O programa retorna o segundo, pois ele tem duas arestas, enquanto o primeiro tem três. A saída mostra o peso total, a quantidade de arestas e a sequência de vértices.')

story.append(PageBreak())
p('4. Testes e resultados', 'SecaoTrabalho')
p('Foram testados todos os pares de 200 grafos aleatórios, com 2 a 11 vértices e pesos de 1 a 10. Todos os resultados coincidiram com Floyd-Warshall em peso e número de arestas. Também foram conferidos os caminhos e os casos especiais: decimais, laços, arestas paralelas e destinos inacessíveis.')
p('Para avaliar o tempo de execução, foram utilizados dois tipos de grafos, com 100, 200, 400 e 800 vértices. Em todos eles, a consulta foi do vértice 1 ao vértice n. Antes das medições, foram feitas 10 buscas de aquecimento; depois, 21 buscas medidas com System.nanoTime. As tabelas apresentam a mediana dessas medições. O tempo inclui a busca e a reconstrução do caminho, sem leitura do arquivo ou impressão. Os testes foram executados no Windows, com Java 8 (1.8.0_481).')
p('4.1. Grafos esparsos', 'SecaoTrabalho')
p('Neste tipo, cada vértice i se liga a i + 1, com peso 1, e a i + 2, com peso 2, quando esses destinos existem. Assim, o grafo tem 2n − 3 arestas. O peso esperado é n − 1, e o menor número de arestas é o arredondamento para cima de (n − 1)/2. Os resultados da Tabela 1 coincidiram com esses valores em todos os tamanhos.')
tabela('Esparso', 1)
p('4.2. Grafos densos', 'SecaoTrabalho')
p('Foram usados grafos direcionados completos, sem laços, com peso |v − u| na aresta de u para v. Nesse caso, há n(n − 1) arestas. O caminho direto de 1 até n tem peso n − 1 e uma aresta. Outros percursos crescentes empatam no peso, mas usam mais arestas. Como mostra a Tabela 2, o programa escolheu a ligação direta nas quatro instâncias.')
tabela('Denso', 2)
p('5. Análise dos resultados', 'SecaoTrabalho')
p('As oito instâncias retornaram o peso e o número de arestas esperados. O maior tempo mediano foi de 5,63 ms, no grafo denso de 800 vértices. Nos esparsos, os tempos não cresceram regularmente; por serem buscas curtas, a JVM e a atividade do sistema influenciam bastante. As medições não demonstram, sozinhas, a complexidade do algoritmo.')
p('Essas famílias permitem conferir a resposta e o desempate, mas não representam todos os grafos. Os testes aleatórios complementam a avaliação. As entradas estão em instancias/ e as medições completas em resultados.csv. Para repetir, execute java Testes.')

def rodape(canvas, doc):
    canvas.saveState()
    canvas.setFont('Times-Roman', 9)
    canvas.drawRightString(A4[0] - 58, 30, str(doc.page))
    canvas.restoreState()

SimpleDocTemplate(str(base / 'relatorio.pdf'), pagesize=A4,
                  rightMargin=58, leftMargin=58, topMargin=40, bottomMargin=40,
                  title='Implementação 3 - Caminho mínimo com o menor número de arestas',
                  author='').build(story, onFirstPage=rodape, onLaterPages=rodape)
print('relatorio.pdf gerado')
