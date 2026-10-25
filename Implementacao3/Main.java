import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/** Caminho minimo: primeiro peso total, depois quantidade de arestas. */
public class Main {
    static class Aresta {
        final int destino;
        final BigDecimal peso;
        Aresta(int destino, BigDecimal peso) { this.destino = destino; this.peso = peso; }
    }
    static class Grafo {
        final List<Aresta>[] adj;
        final int n, m;
        @SuppressWarnings("unchecked")
        Grafo(int n, int m) {
            this.n = n; this.m = m;
            adj = (List<Aresta>[]) new List<?>[n + 1];
            for (int i = 1; i <= n; i++) adj[i] = new ArrayList<>();
        }
    }
    static class Leitor implements Closeable {
        final InputStream in;
        Leitor(String arquivo) throws IOException { in = new BufferedInputStream(new FileInputStream(arquivo)); }
        String token() throws IOException {
            int c;
            do { c = in.read(); } while (c != -1 && c <= 32);
            if (c == -1) return null;
            StringBuilder s = new StringBuilder();
            do { s.append((char)c); c = in.read(); } while (c != -1 && c > 32);
            return s.toString();
        }
        String exigir() throws IOException {
            String s = token();
            if (s == null) throw new IOException("Arquivo incompleto.");
            return s;
        }
        public void close() throws IOException { in.close(); }
    }
    static Grafo carregar(String arquivo) throws IOException {
        try (Leitor l = new Leitor(arquivo)) {
            int n = Integer.parseInt(l.exigir()), m = Integer.parseInt(l.exigir());
            if (n < 1 || n == Integer.MAX_VALUE || m < 0)
                throw new IllegalArgumentException("Quantidade de vertices ou arestas invalida.");
            Grafo g = new Grafo(n, m);
            for (int i = 0; i < m; i++) {
                int u = Integer.parseInt(l.exigir()), v = Integer.parseInt(l.exigir());
                validar(g, u); validar(g, v);
                BigDecimal peso = new BigDecimal(l.exigir());
                if (peso.signum() <= 0) throw new IllegalArgumentException("Os pesos devem ser positivos.");
                g.adj[u].add(new Aresta(v, peso));
            }
            if (l.token() != null) throw new IOException("Dados extras depois das arestas.");
            return g;
        }
    }
    static void validar(Grafo g, int v) {
        if (v < 1 || v > g.n) throw new IllegalArgumentException("Vertice fora de 1.." + g.n + ".");
    }
    static class Estado implements Comparable<Estado> {
        final int v, arestas;
        final BigDecimal distancia;
        Estado(int v, BigDecimal distancia, int arestas) {
            this.v = v; this.distancia = distancia; this.arestas = arestas;
        }
        public int compareTo(Estado e) {
            int c = distancia.compareTo(e.distancia);
            if (c == 0) c = Integer.compare(arestas, e.arestas);
            return c == 0 ? Integer.compare(v, e.v) : c;
        }
    }
    static class Resultado {
        final BigDecimal distancia; // null indica destino inalcancavel
        final List<Integer> caminho;
        Resultado(BigDecimal distancia, List<Integer> caminho) {
            this.distancia = distancia; this.caminho = caminho;
        }
        int arestas() { return caminho.size() - 1; }
    }
    static Resultado buscar(Grafo g, int origem, int destino) {
        validar(g, origem); validar(g, destino);
        BigDecimal[] dist = new BigDecimal[g.n + 1];
        int[] qtd = new int[g.n + 1], anterior = new int[g.n + 1];
        Arrays.fill(qtd, Integer.MAX_VALUE);
        dist[origem] = BigDecimal.ZERO; qtd[origem] = 0;
        PriorityQueue<Estado> fila = new PriorityQueue<>();
        fila.add(new Estado(origem, BigDecimal.ZERO, 0));
        while (!fila.isEmpty()) {
            Estado atual = fila.poll();
            int u = atual.v;
            // Entradas antigas permanecem na fila, mas nao sao processadas.
            if (atual.distancia.compareTo(dist[u]) != 0 || atual.arestas != qtd[u]) continue;
            if (u == destino) break;
            for (Aresta a : g.adj[u]) {
                BigDecimal nova = dist[u].add(a.peso);
                int passos = qtd[u] + 1;
                int c = dist[a.destino] == null ? -1 : nova.compareTo(dist[a.destino]);
                if (c < 0 || (c == 0 && passos < qtd[a.destino])) {
                    dist[a.destino] = nova; qtd[a.destino] = passos; anterior[a.destino] = u;
                    fila.add(new Estado(a.destino, nova, passos));
                }
            }
        }
        List<Integer> caminho = new ArrayList<>();
        if (dist[destino] != null) {
            for (int v = destino; v != 0; v = anterior[v]) caminho.add(v);
            Collections.reverse(caminho);
        }
        return new Resultado(dist[destino], caminho);
    }
    public static void main(String[] args) {
        try {
            String arquivo; int origem, destino;
            if (args.length == 3) {
                arquivo = args[0]; origem = Integer.parseInt(args[1]); destino = Integer.parseInt(args[2]);
            } else if (args.length == 0) {
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Arquivo do grafo: "); arquivo = teclado.readLine();
                System.out.print("Origem: "); origem = Integer.parseInt(teclado.readLine());
                System.out.print("Destino: "); destino = Integer.parseInt(teclado.readLine());
            } else throw new IllegalArgumentException("Uso: java Main [arquivo origem destino]");
            Grafo g = carregar(arquivo);
            long inicio = System.nanoTime();
            Resultado r = buscar(g, origem, destino);
            long tempo = System.nanoTime() - inicio;
            if (r.distancia == null) System.out.println("Nao existe caminho de " + origem + " para " + destino + ".");
            else {
                System.out.println("Comprimento minimo: " + r.distancia.stripTrailingZeros().toPlainString());
                System.out.println("Numero de arestas: " + r.arestas());
                StringJoiner caminho = new StringJoiner(" -> ");
                for (int v : r.caminho) caminho.add(Integer.toString(v));
                System.out.println("Caminho: " + caminho);
            }
            System.out.printf(Locale.ROOT, "Tempo da busca: %.6f ms%n", tempo / 1e6);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }
}
