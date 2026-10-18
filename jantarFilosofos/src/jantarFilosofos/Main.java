package jantarFilosofos;

import java.util.concurrent.Semaphore;

public class Main {
    static final int NUM_FILOSOFOS = 5;
    static final int DURACAO_MS = 50_000;
    static final int INTERVALO_STATUS_MS = 2_000;

    public static void main(String[] args) throws InterruptedException {

        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo();
        }

        // garcom: no maximo N-1 filosofos disputam garfos ao mesmo tempo.
        // O "true" torna o semaforo justo (FIFO), entao ninguem espera para sempre.
        Semaphore garcom = new Semaphore(NUM_FILOSOFOS - 1, true);

        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo esquerda = garfos[i];
            Garfo direita = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, esquerda, direita, garcom);
        }

        System.out.println("Iniciando jantar com " + NUM_FILOSOFOS + " filosofos por "
                + DURACAO_MS / 1000 + " segundos...\n");

        for (Filosofo f : filosofos) {
            f.start();
        }

        //loop de acompanhamento: filosofos pensando e comendo
        long fim = System.currentTimeMillis() + DURACAO_MS;
        while (System.currentTimeMillis() < fim) {
            Thread.sleep(INTERVALO_STATUS_MS);
            imprimeStatus(filosofos, "Status parcial");
        }


        for (Filosofo f : filosofos) {
            f.interrupt();
        }
        for (Filosofo f : filosofos) {
            f.join();
        }

        System.out.println();
        imprimeStatus(filosofos, "Resultado final");
    }

    private static void imprimeStatus(Filosofo[] filosofos, String titulo) {
        System.out.println("=== " + titulo + " ===");
        int totalRefeicoes = 0;
        int totalPensamentos = 0;
        for (int i = 0; i < filosofos.length; i++) {
            int refeicoes = filosofos[i].refeicoes.get();
            int pensamentos = filosofos[i].pensamentos.get();
            int falhasComer = filosofos[i].falhasComer.get();
            totalRefeicoes += refeicoes;
            totalPensamentos += pensamentos;
            System.out.printf("Filosofo %d -> refeicoes: %d | pensamentos: %d | falhas em comer: %d%n",
                    i, refeicoes, pensamentos, falhasComer);
        }
        System.out.println("Total de refeicoes: " + totalRefeicoes);
        System.out.println("Total de pensamentos: " + totalPensamentos);
        System.out.printf("Media de refeições: %.2f%n", (double) totalRefeicoes/NUM_FILOSOFOS);
        System.out.printf("Media de pensamentos: %.2f%n%n", (double)totalPensamentos/NUM_FILOSOFOS);
      
    }
}