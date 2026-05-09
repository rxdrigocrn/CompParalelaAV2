package trabav2comp; // Mantendo o seu pacote

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SortingAnalysis {

    private static final int[] THREAD_CONFIGS = {1, 2, 5, 10, 15, 20}; 
    private static final int[] ARRAY_SIZES = {10000, 50000, 100000};
    private static final int SAMPLES = 5;

    public static void main(String[] args) throws IOException {
        FileWriter csvWriter = new FileWriter("resultados_performance.csv");
        csvWriter.append("Algoritmo,Tamanho,Threads,Amostra,TempoMS\n");

        System.out.println("Iniciando Benchmarks... Isso pode demorar alguns minutos.");

        for (int size : ARRAY_SIZES) {
            for (int threads : THREAD_CONFIGS) {
                for (int s = 1; s <= SAMPLES; s++) {
                    int[] dataOriginal = generateRandomArray(size);
                    
                    runTest("MergeSort", dataOriginal, threads, size, s, csvWriter);
                    runTest("QuickSort", dataOriginal, threads, size, s, csvWriter);
                    runTest("CountingSort", dataOriginal, threads, size, s, csvWriter);
                    
                    // Bubble Sort é muito lento (O(n^2)), rodamos apenas para arrays menores
                    if (size <= 50000) {
                        runTest("BubbleSort", dataOriginal, threads, size, s, csvWriter);
                    }
                }
            }
        }

        csvWriter.close();
        System.out.println("Fim dos testes! Arquivo 'resultados_performance.csv' gravado com sucesso.");
    }

    private static void runTest(String name, int[] original, int threads, int size, int sample, FileWriter writer) throws IOException {
        int[] data = Arrays.copyOf(original, original.length);
        ForkJoinPool pool = new ForkJoinPool(threads);
        
        long start = System.currentTimeMillis();
        
        if (name.equals("MergeSort")) {
            pool.invoke(new ParallelMergeSort(data, 0, data.length - 1));
        } else if (name.equals("QuickSort")) {
            pool.invoke(new ParallelQuickSort(data, 0, data.length - 1));
        } else if (name.equals("BubbleSort")) {
            oddEvenSort(data, threads); // Odd-Even é a versão paralelizável do Bubble Sort
        } else if (name.equals("CountingSort")) {
            countingSort(data); 
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        // Salvando no CSV e forçando a gravação (flush) a cada linha
        writer.append(String.format("%s,%d,%d,%d,%d\n", name, size, threads, sample, duration));
        writer.flush(); 
        pool.shutdown();
        
        System.out.println(name + " | Tam: " + size + " | Threads: " + threads + " | Amostra: " + sample + " | Tempo: " + duration + "ms");
    }

    private static int[] generateRandomArray(int size) {
        Random r = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = r.nextInt(size * 10);
        return arr;
    }

    // ==========================================
    // IMPLEMENTAÇÕES COMPLETAS DOS ALGORITMOS
    // ==========================================

    static class ParallelMergeSort extends RecursiveAction {
        int[] arr; int left, right;
        ParallelMergeSort(int[] arr, int l, int r) { this.arr = arr; this.left = l; this.right = r; }
        
        @Override protected void compute() {
            if (left < right) {
                if (right - left < 1000) { 
                    Arrays.sort(arr, left, right + 1); 
                    return; 
                }
                int m = (left + right) / 2;
                invokeAll(new ParallelMergeSort(arr, left, m), new ParallelMergeSort(arr, m + 1, right));
                merge(arr, left, m, right);
            }
        }
        
        void merge(int[] arr, int l, int m, int r) {
            int n1 = m - l + 1; int n2 = r - m;
            int[] L = new int[n1]; int[] R = new int[n2];
            for (int i = 0; i < n1; ++i) L[i] = arr[l + i];
            for (int j = 0; j < n2; ++j) R[j] = arr[m + 1 + j];
            int i = 0, j = 0, k = l;
            while (i < n1 && j < n2) {
                if (L[i] <= R[j]) { arr[k] = L[i]; i++; } 
                else { arr[k] = R[j]; j++; }
                k++;
            }
            while (i < n1) { arr[k] = L[i]; i++; k++; }
            while (j < n2) { arr[k] = R[j]; j++; k++; }
        }
    }

    static class ParallelQuickSort extends RecursiveAction {
        int[] arr; int low, high;
        ParallelQuickSort(int[] arr, int low, int high) { this.arr = arr; this.low = low; this.high = high; }
        
        @Override protected void compute() {
            if (low < high) {
                int pi = partition(arr, low, high);
                invokeAll(new ParallelQuickSort(arr, low, pi - 1), new ParallelQuickSort(arr, pi + 1, high));
            }
        }
        
        int partition(int[] arr, int low, int high) {
            int pivot = arr[high];
            int i = (low - 1);
            for (int j = low; j < high; j++) {
                if (arr[j] < pivot) {
                    i++;
                    int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                }
            }
            int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
            return i + 1;
        }
    }

    static void oddEvenSort(int[] a, int threads) {
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i = 1; i <= a.length - 2; i = i + 2) { 
                if (a[i] > a[i + 1]) { int temp = a[i]; a[i] = a[i + 1]; a[i + 1] = temp; sorted = false; } 
            }
            for (int i = 0; i <= a.length - 2; i = i + 2) { 
                if (a[i] > a[i + 1]) { int temp = a[i]; a[i] = a[i + 1]; a[i + 1] = temp; sorted = false; } 
            }
        }
    }

    static void countingSort(int[] a) {
        if (a.length == 0) return;
        int max = a[0];
        for (int i = 1; i < a.length; i++) { if (a[i] > max) max = a[i]; }
        int[] count = new int[max + 1];
        for (int i = 0; i < a.length; i++) { count[a[i]]++; }
        int index = 0;
        for (int i = 0; i <= max; i++) {
            while (count[i] > 0) { a[index++] = i; count[i]--; }
        }
    }
}