# UNIVERSIDADE DE FORTALEZA
## CENTRO DE CIÊNCIAS TECNOLÓGICAS
### CURSO: CIÊNCIA DA COMPUTAÇÃO

<br>

# ANÁLISE DE DESEMPENHO DE ALGORITMOS DE ORDENAÇÃO EM AMBIENTES CONCORRENTES E PARALELOS: UM ESTUDO COMPARATIVO EM JAVA

<br>

**Autor 1:** Rodrigo Cirino Cavalcante Lima 
**Autor 2:** Erfon Spanos

**Palavras-chave:** Algoritmos. Ordenação. Concorrência. Java. Desempenho.

---

## Resumo
Este trabalho propõe uma análise detalhada do desempenho de diferentes algoritmos de ordenação em ambientes seriais e paralelos, utilizando a linguagem de programação Java. A busca por eficiência computacional é essencial e, neste estudo, foram abordados quatro algoritmos: Bubble Sort, Quick Sort, Merge Sort e Counting Sort. As implementações abrangeram versões sequenciais e paralelizadas, utilizando o framework de controle de concorrência `ForkJoinPool`. Foram realizadas análises comparativas variando o tamanho dos conjuntos de dados e o número de threads (1 a 20). Os resultados demonstram o impacto do overhead de gerenciamento de processos e ilustram a Lei de Amdahl na prática, evidenciando que algoritmos com melhor complexidade teórica se beneficiam mais do paralelismo, enquanto algoritmos menos eficientes sofrem degradação de performance com o excesso de concorrência.

## Introdução
A eficiência computacional na organização de dados é um problema fundamental na Ciência da Computação. O advento das arquiteturas multicore popularizou a programação concorrente e paralela como meio para acelerar o processamento. Este trabalho tem como objetivo investigar o comportamento de algoritmos clássicos (Merge Sort, Quick Sort, Bubble Sort e Counting Sort) quando submetidos a ambientes de execução paralela em comparação com a execução serial. A abordagem adotada envolve a implementação destes métodos em Java, empregando o framework `ForkJoinPool` para aplicar a estratégia de "dividir para conquistar" de forma otimizada. Isso permite a distribuição inteligente de subtarefas de ordenação entre múltiplos núcleos de processamento, minimizando gargalos sistêmicos.

## Metodologia
O desenvolvimento consistiu na implementação de um "framework de teste" estruturado para executar análises quantitativas. Foi realizada a análise estatística dos resultados obtidos para identificar padrões de desempenho e comparar os algoritmos sob diferentes condições. Para garantir a validade estatística dos dados, os testes foram configurados para variar o tamanho do conjunto de entrada (arrays de 10.000, 50.000 e 100.000 números inteiros aleatórios) e o número de threads alocadas no pool de processamento (1 para execução serial; 2, 5, 10, 15 e 20 para execução paralela). Cada configuração foi executada em 5 amostras distintas, extraindo-se a média aritmética dos tempos de execução (em milissegundos). Os tempos foram registrados em arquivos CSV e processados por uma interface em **Java Swing** desenvolvida especificamente para a visualização gráfica dos resultados.

## Resultados e Discussão
As análises dos resultados dos testes revelaram padrões de escalabilidade distintos conforme a complexidade assintótica de cada método.

### Gráfico de Desempenho (Interface Java Swing)
*(Insira aqui a imagem do seu gráfico Swing. No GitHub, você pode arrastar a imagem para dentro da caixa de edição para gerar o link dela)*

Conforme demonstrado nos testes, os algoritmos O(n log n), como Merge Sort e Quick Sort, apresentaram tempos extremamente baixos para o array de 10.000 elementos, exibindo uma curva de aceleração (*speedup*) consistente até a marca de 10 threads. A partir deste ponto, o tempo de gerenciamento das subtarefas superou o ganho de processamento, mantendo o tempo estável ou com leves subidas.

O **Bubble Sort** (O(n²)) demonstrou claramente a limitação do paralelismo em algoritmos de alta complexidade: embora o tempo tenha caído de ~88ms (1 thread) para ~62ms (5 threads), o uso de 20 threads elevou o tempo para mais de 83ms. Isso ocorre devido ao custo de sincronização das threads exceder o benefício da execução paralela para esse volume de dados. O **Counting Sort** manteve-se como o mais eficiente em todos os cenários devido à sua natureza de tempo linear.

## Conclusão
Conclui-se que a adoção do processamento paralelo em Java fornece vantagens significativas, porém não ilimitadas. A otimização através da escolha de um algoritmo assintoticamente superior (como o Merge Sort ou Counting Sort em detrimento do Bubble Sort) mostrou-se o fator mais determinante para a velocidade. Adicionalmente, ficou comprovado que adicionar mais threads em volumes de dados relativamente pequenos não garante linearidade de ganho, reforçando os princípios da **Lei de Amdahl**. O estudo atingiu os resultados esperados, entregando artefatos visuais que comprovam a teoria da computação paralela.

## Referências
* CORMEN, T. H. et al. **Algoritmos: teoria e prática**. 3. ed. Rio de Janeiro: Elsevier, 2012.
* GOETZ, B. **Java Concurrency in Practice**. 1. ed. Boston: Addison-Wesley Professional, 2006.
* ORACLE. **Class ForkJoinPool**. Java Platform, Standard Edition 8 API Specification, 2023.

---

### Anexos - Códigos das Implementações
Os códigos-fonte estão organizados no repositório da seguinte forma:
* `SortingAnalysis.java`: Lógica principal, benchmarks e geração do CSV.
* `GraficoSwing.java`: Interface gráfica para plotagem dos resultados.
* `resultados_performance.csv`: Dados brutos gerados pelas execuções.
