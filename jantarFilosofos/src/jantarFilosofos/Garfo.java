package jantarFilosofos;

public class Garfo {
    private boolean emUso = false;

    // tenta pegar o garfo esperando no maximo timeoutMs; false se estourar o tempo
    public synchronized boolean tentaPegar(long timeoutMs) {
        long limite = System.currentTimeMillis() + timeoutMs;
        try {
            while (emUso) {
                long restante = limite - System.currentTimeMillis();
                if (restante <= 0) {
                    return false;
                }
                wait(restante);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserva o sinal de interrupcao
            return false;
        }
        emUso = true;
        return true;
    }

    public synchronized void soltar() {
        emUso = false;
        notifyAll();
    }
}
