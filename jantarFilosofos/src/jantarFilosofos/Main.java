package jantarFilosofos;

public class Main {
    static final int NUM_FILOSOFOS = 5;
    static final int DURACAO_MS = 10_000;
    static final int INTERVALO_STATUS_MS = 2_000;

    public static void main(String[] args) throws InterruptedException {

        Garfo[] garfos = new Garfo[NUM_FILOSOFOS];
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new Garfo();
        }


        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];
        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Garfo esquerda = garfos[i];
            Garfo direita = garfos[(i + 1) % NUM_FILOSOFOS];
            filosofos[i] = new Filosofo(i, esquerda, direita);
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
        for (int i = 0; i < filosofos.length; i++) {
            int refeicoes = filosofos[i].refeicoes.get();
            int pensamentos = filosofos[i].pensamentos.get();
            totalRefeicoes += refeicoes;
            System.out.printf("Filosofo %d -> refeicoes: %d | pensamentos: %d%n",
                    i, refeicoes, pensamentos);
        }
        System.out.println("Total de refeicoes: " + totalRefeicoes + "\n");
    }
}