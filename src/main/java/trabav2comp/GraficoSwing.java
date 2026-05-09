package trabav2comp;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.List;

public class GraficoSwing extends JPanel {

    // Estrutura para armazenar as médias: Algoritmo -> (Threads -> Tempo Médio)
    private Map<String, Map<Integer, Double>> dadosMedios = new HashMap<>();
    private final int[] THREADS = {1, 2, 5, 10, 15, 20};
    private double tempoMaximo = 0;

    // Cores para cada algoritmo
    private Map<String, Color> cores = new HashMap<>();

    public GraficoSwing() {
        cores.put("MergeSort", Color.BLUE);
        cores.put("QuickSort", Color.RED);
        cores.put("BubbleSort", new Color(150, 0, 150)); // Roxo
        cores.put("CountingSort", new Color(0, 150, 0)); // Verde

        carregarDados();
    }

    private void carregarDados() {
        Map<String, Map<Integer, List<Integer>>> agrupador = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/trabav2comp/resultados_performance.csv"))) {
            String linha = br.readLine(); // Pula o cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(",");
                String algoritmo = colunas[0];
                int tamanho = Integer.parseInt(colunas[1]);
                int threads = Integer.parseInt(colunas[2]);
                int tempo = Integer.parseInt(colunas[4]);

                // Vamos focar no gráfico do array de 10.000 para ficar visível (BubbleSort distorce arrays maiores)
                if (tamanho == 10000) {
                    agrupador.putIfAbsent(algoritmo, new HashMap<>());
                    agrupador.get(algoritmo).putIfAbsent(threads, new ArrayList<>());
                    agrupador.get(algoritmo).get(threads).add(tempo);
                }
            }

            // Calcular as médias
            for (String algo : agrupador.keySet()) {
                dadosMedios.put(algo, new HashMap<>());
                for (int t : agrupador.get(algo).keySet()) {
                    List<Integer> tempos = agrupador.get(algo).get(t);
                    double media = tempos.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                    dadosMedios.get(algo).put(t, media);

                    if (media > tempoMaximo) {
                        tempoMaximo = media;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao ler o CSV. Certifique-se de que o arquivo 'resultados_performance.csv' existe na mesma pasta.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 60;
        int larguraEixoX = getWidth() - (2 * padding);
        int alturaEixoY = getHeight() - (2 * padding);

        // Fundo branco
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Desenhar eixos X e Y
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, getHeight() - padding, padding, padding); // Eixo Y
        g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // Eixo X

        // Legendas dos eixos
        g2.drawString("Tempo (ms)", padding - 50, padding - 10);
        g2.drawString("Número de Threads", getWidth() / 2, getHeight() - padding + 40);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Análise de Desempenho (Array Tamanho 10.000)", getWidth() / 2 - 150, padding - 30);

        // Marcadores no Eixo X (Threads)
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < THREADS.length; i++) {
            int x = padding + (i * larguraEixoX / (THREADS.length - 1));
            g2.drawLine(x, getHeight() - padding, x, getHeight() - padding + 5);
            g2.drawString(String.valueOf(THREADS[i]), x - 5, getHeight() - padding + 20);
        }

        // Marcadores no Eixo Y (Tempo)
        int numMarcadoresY = 10;
        for (int i = 0; i <= numMarcadoresY; i++) {
            int y = getHeight() - padding - (i * alturaEixoY / numMarcadoresY);
            g2.drawLine(padding - 5, y, padding, y);
            String valorY = String.format("%.1f", (tempoMaximo / numMarcadoresY) * i);
            g2.drawString(valorY, padding - 40, y + 5);
        }

        // Desenhar as linhas dos gráficos
        int legendaY = padding;
        for (String algoritmo : dadosMedios.keySet()) {
            g2.setColor(cores.getOrDefault(algoritmo, Color.BLACK));
            g2.setStroke(new BasicStroke(2));

            // Legenda no canto superior direito
            g2.fillRect(getWidth() - padding - 80, legendaY, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawString(algoritmo, getWidth() - padding - 60, legendaY + 12);
            legendaY += 25;

            // Pontos e Linhas
            g2.setColor(cores.getOrDefault(algoritmo, Color.BLACK));
            Point pontoAnterior = null;

            for (int i = 0; i < THREADS.length; i++) {
                int t = THREADS[i];
                if (dadosMedios.get(algoritmo).containsKey(t)) {
                    double tempoMedia = dadosMedios.get(algoritmo).get(t);
                    
                    int x = padding + (i * larguraEixoX / (THREADS.length - 1));
                    int y = (int) (getHeight() - padding - ((tempoMedia / tempoMaximo) * alturaEixoY));

                    g2.fillOval(x - 4, y - 4, 8, 8); // Bolinha do ponto

                    if (pontoAnterior != null) {
                        g2.drawLine(pontoAnterior.x, pontoAnterior.y, x, y); // Linha ligando
                    }
                    pontoAnterior = new Point(x, y);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ponto Extra - Gráfico de Análise de Algoritmos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Centraliza a janela
            frame.add(new GraficoSwing());
            frame.setVisible(true); // Exibe a interface gráfica
        });
    }
}