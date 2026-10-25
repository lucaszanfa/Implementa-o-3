import java.io.*;
import java.math.BigDecimal;
import java.util.*;
public class Testes {
 static void check(boolean b) { if(!b) throw new AssertionError(); }
 static void add(Main.Grafo g,int u,int v,int w) {g.adj[u].add(new Main.Aresta(v,BigDecimal.valueOf(w)));}
 static void random() {
  Random rnd=new Random(42);
  for(int t=0;t<200;t++) {
   int n=2+rnd.nextInt(10); Main.Grafo g=new Main.Grafo(n,0);
   long[][] d=new long[n+1][n+1];int[][] h=new int[n+1][n+1];
   for(int u=1;u<=n;u++) {
    Arrays.fill(d[u],1000000000);d[u][u]=0;
    for(int v=1;v<=n;v++) if(rnd.nextInt(4)==0) {
     int w=1+rnd.nextInt(10);add(g,u,v,w);
     if(w<d[u][v]) {d[u][v]=w;h[u][v]=1;}
    }
   }
   for(int k=1;k<=n;k++) for(int u=1;u<=n;u++) for(int v=1;v<=n;v++) {
    long nd=d[u][k]+d[k][v];int nh=h[u][k]+h[k][v];
    if(nd<d[u][v] || (nd==d[u][v] && nh<h[u][v])) {d[u][v]=nd;h[u][v]=nh;}
   }
   for(int u=1;u<=n;u++) for(int v=1;v<=n;v++) {
    Main.Resultado r=Main.buscar(g,u,v);
    if(d[u][v]>=1000000000) check(r.distancia==null);
    else {
     check(r.distancia.compareTo(BigDecimal.valueOf(d[u][v]))==0 && r.arestas()==h[u][v]);
     check(r.caminho.get(0)==u && r.caminho.get(r.caminho.size()-1)==v);
     BigDecimal soma=BigDecimal.ZERO;
     for(int i=1;i<r.caminho.size();i++) {
      BigDecimal w=null;
      for(Main.Aresta a:g.adj[r.caminho.get(i-1)]) if(a.destino==r.caminho.get(i))
       if(w==null || a.peso.compareTo(w)<0) w=a.peso;
      check(w!=null);soma=soma.add(w);
     }
     check(soma.compareTo(r.distancia)==0);
    }
   }
  }
 }
 public static void main(String[] args) throws Exception {
  random();
  Main.Grafo g=new Main.Grafo(6,0);
  add(g,1,2,1);add(g,2,3,1);add(g,3,6,2);add(g,1,4,3);add(g,4,6,1);
  check(Main.buscar(g,1,6).arestas()==2);
  Main.Grafo dec=new Main.Grafo(3,0);
  dec.adj[1].add(new Main.Aresta(2,new BigDecimal("0.1")));
  dec.adj[2].add(new Main.Aresta(3,new BigDecimal("0.2")));
  dec.adj[1].add(new Main.Aresta(3,new BigDecimal("0.3")));
  add(dec,1,3,5);add(dec,1,1,1);check(Main.buscar(dec,1,3).arestas()==1);
  new File("instancias").mkdirs();
  try(PrintWriter csv=new PrintWriter("resultados.csv","UTF-8")) {
   csv.println("tipo,vertices,arestas,comprimento,arestas_caminho,correto,mediana_ms,min_ms,max_ms");
   for(String tipo:new String[]{"Esparso","Denso"}) for(int n:new int[]{100,200,400,800}) {
    int m=tipo.equals("Esparso")?2*n-3:n*(n-1);
    String arquivo="instancias/"+tipo.toLowerCase(Locale.ROOT)+"-"+n+".txt";
    try(PrintWriter out=new PrintWriter(arquivo,"UTF-8")) {
     out.println(n+" "+m);
     for(int u=1;u<=n;u++) for(int v=1;v<=n;v++)
      if(tipo.equals("Esparso")?(v==u+1 || v==u+2):u!=v)
       out.println(u+" "+v+" "+Math.abs(v-u));
    }
    g=Main.carregar(arquivo);
    for(int i=0;i<10;i++) Main.buscar(g,1,n);
    long[] tempos=new long[21];Main.Resultado r=null;
    for(int i=0;i<21;i++) {
     long inicio=System.nanoTime();r=Main.buscar(g,1,n);tempos[i]=System.nanoTime()-inicio;
     check(r.distancia.compareTo(BigDecimal.valueOf(n-1))==0);
     check(r.arestas()==(tipo.equals("Esparso")?n/2:1));
    }
    Arrays.sort(tempos);
    csv.printf(Locale.ROOT,"%s,%d,%d,%s,%d,true,%.6f,%.6f,%.6f%n",tipo,n,m,r.distancia,r.arestas(),tempos[10]/1e6,tempos[0]/1e6,tempos[20]/1e6);
   }
  }
  System.out.println("OK: 200 grafos aleatorios, todos os pares, casos especiais e 8 instancias de desempenho.");
 }
}
