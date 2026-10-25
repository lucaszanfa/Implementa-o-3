import java.io.*;
import java.util.Arrays;

public class Main {
    // Leitura com buffer para arquivos com milhoes de arestas.
    static class Leitor implements Closeable {
        private final InputStream entrada;
        Leitor(String arquivo) throws IOException {
            entrada = new BufferedInputStream(new FileInputStream(arquivo));
        }
        int proximoInt() throws IOException {
            int c;
            do { c = entrada.read(); } while (c >= 0 && c <= 32);
            if (c < 0) throw new EOFException("Fim inesperado do arquivo.");
            if (c < '0' || c > '9') throw new IOException("Esperado inteiro nao negativo.");
            int valor = 0;
            do {
                valor = Math.addExact(Math.multiplyExact(valor, 10), c - '0');
                c = entrada.read();
            } while (c >= '0' && c <= '9');
            if (c > 32) throw new IOException("Inteiro invalido no arquivo.");
            return valor;
        }
        void verificarFim() throws IOException {
            int c;
            do { c = entrada.read(); } while (c >= 0 && c <= 32);
            if (c >= 0) throw new IOException("Dados extras apos as arestas declaradas.");
        }
        public void close() throws IOException { entrada.close(); }
    }

    static class Grafo {
        final int n;
        final int[] inicio, destinos;
        Grafo(int n, int[] inicio, int[] destinos) {
            this.n = n;
            this.inicio = inicio;
            this.destinos = destinos;
        }
    }

    static Grafo carregar(String arquivo) throws IOException {
        int n, m;
        int[] inicio;
        // Duas passagens evitam manter uma copia temporaria de todas as arestas.
        try (Leitor leitor = new Leitor(arquivo)) {
            n = leitor.proximoInt();
            m = leitor.proximoInt();
            if (n < 1 || n > Integer.MAX_VALUE - 2)
                throw new IOException("Numero de vertices invalido.");
            inicio = new int[n + 2];
            for (int i = 0; i < m; i++) {
                int u = leitor.proximoInt(), v = leitor.proximoInt();
                if (u < 1 || u > n || v < 1 || v > n)
                    throw new IOException("Vertice invalido na aresta " + (i + 1) + ".");
                inicio[u + 1]++;
            }
            leitor.verificarFim();
        }
        for (int v = 1; v <= n; v++) inicio[v + 1] += inicio[v];
        int[] destinos = new int[m];
        int[] proxima = inicio.clone();
        try (Leitor leitor = new Leitor(arquivo)) {
            if (leitor.proximoInt() != n || leitor.proximoInt() != m)
                throw new IOException("Arquivo alterado durante a leitura.");
            for (int i = 0; i < m; i++) {
                int u = leitor.proximoInt(), v = leitor.proximoInt();
                if (u < 1 || u > n || v < 1 || v > n || proxima[u] >= inicio[u + 1])
                    throw new IOException("Arquivo alterado durante a leitura.");
                destinos[proxima[u]++] = v;
            }
            leitor.verificarFim();
        }
        for (int v = 1; v <= n; v++)
            Arrays.sort(destinos, inicio[v], inicio[v + 1]);
        return new Grafo(n, inicio, destinos);
    }

    static void buscar(Grafo g, int escolhido, PrintWriter saida) {
        // 0 = branco (nao descoberto), 1 = cinza (ativo), 2 = preto (concluido).
        byte[] cor = new byte[g.n + 1];
        int[] descoberta = new int[g.n + 1];
        int[] proxima = g.inicio.clone();
        int[] pilha = new int[g.n];
        String[] tipos = new String[g.inicio[escolhido + 1] - g.inicio[escolhido]];
        int tempo = 0;
        saida.println("Arestas de arvore (ordem da DFS):");
        int quantidadeArvore = 0;
        for (int raiz = 1; raiz <= g.n; raiz++) {
            if (cor[raiz] != 0) continue;
            int topo = 0;
            pilha[topo] = raiz;
            cor[raiz] = 1;
            descoberta[raiz] = ++tempo;
            while (topo >= 0) {
                int u = pilha[topo];
                if (proxima[u] == g.inicio[u + 1]) {
                    cor[u] = 2;
                    topo--;
                    continue;
                }
                int aresta = proxima[u]++;
                int v = g.destinos[aresta];
                String tipo;
                if (cor[v] == 0) tipo = "arvore";
                else if (cor[v] == 1) tipo = "retorno";
                // Um destino preto descoberto depois de u e descendente de u.
                else if (descoberta[u] < descoberta[v]) tipo = "avanco";
                else tipo = "cruzamento";
                if (u == escolhido) tipos[aresta - g.inicio[u]] = tipo;
                if (cor[v] == 0) {
                    saida.println(u + " -> " + v);
                    quantidadeArvore++;
                    cor[v] = 1;
                    descoberta[v] = ++tempo;
                    pilha[++topo] = v;
                }
            }
        }
        if (quantidadeArvore == 0) saida.println("(nenhuma)");
        saida.println("Classificacao das arestas que saem do vertice " + escolhido + ":");
        for (int i = 0; i < tipos.length; i++)
            saida.println(escolhido + " -> " + g.destinos[g.inicio[escolhido] + i] + ": " + tipos[i]);
        if (tipos.length == 0) saida.println("(nenhuma)");
        saida.flush();
    }

    public static void main(String[] args) {
        try {
            String arquivo, numero;
            if (args.length == 2) {
                arquivo = args[0];
                numero = args[1];
            } else if (args.length == 0) {
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Nome do arquivo: ");
                arquivo = teclado.readLine();
                System.out.print("Numero do vertice: ");
                numero = teclado.readLine();
            } else throw new IllegalArgumentException("Uso: java Main [arquivo vertice]");
            if (arquivo == null || arquivo.trim().isEmpty() || numero == null)
                throw new IllegalArgumentException("Informe o arquivo e o vertice.");
            int vertice = Integer.parseInt(numero.trim());
            Grafo grafo = carregar(arquivo);
            if (vertice < 1 || vertice > grafo.n)
                throw new IllegalArgumentException("Vertice deve estar entre 1 e " + grafo.n + ".");
            buscar(grafo, vertice, new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out))));
        } catch (NumberFormatException e) {
            System.err.println("Erro: numero de vertice invalido.");
            System.exit(1);
        } catch (ArithmeticException e) {
            System.err.println("Erro: inteiro no arquivo excede o limite suportado.");
            System.exit(1);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}
