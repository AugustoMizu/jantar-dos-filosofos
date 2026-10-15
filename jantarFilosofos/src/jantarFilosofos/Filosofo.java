package jantarFilosofos;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import enums.Parametros;

public class Filosofo extends Thread {
	private final int id; // id vai ser a posição no array
	private final Garfo garfoEsquerda;
	private final Garfo garfoDireita;

	final int TEMPO_MAXIMO = Parametros.TEMPO_MAXIMO.getValor(); // usado para definir intervalos de ms
	final int INANICAO = Parametros.INANICAO.getValor(); // tentativas de comer antes de escrever um aviso
	private Random random = new Random();

	// contadores para exibir no main
	public AtomicInteger refeicoes = new AtomicInteger(0);
	public AtomicInteger pensamentos = new AtomicInteger(0);

	public Filosofo(int id, Garfo garfoEsquerda, Garfo garfoDireita) {
		this.id = id;
		this.garfoEsquerda = garfoEsquerda;
		this.garfoDireita = garfoDireita;

	}

	// tenta pegar o garfo esquerdo passando um tempo aleatorio de espera
	private boolean pegaGarfoEsquerda() {
		return this.garfoEsquerda.tentaPegar(tempo());
	}

	// tenta pegar o garfo direito passando um tempo aleatorio de espera
	private boolean pegaGarfoDireita() {
		return this.garfoDireita.tentaPegar(tempo());
	}

	private void soltaGarfoEsquerda() {
		this.garfoEsquerda.soltar();
	}

	private void soltaGarfoDireita() {
		this.garfoDireita.soltar();
	}

	private void soltaGarfos() {
		soltaGarfoEsquerda();
		soltaGarfoDireita();
	}

	// gera um int aleatorio entre zero e TEMPO_MAXIMO
	private int tempo() {
		return random.nextInt(TEMPO_MAXIMO) + 1;
	}

	private void pensar() throws InterruptedException {
		pensamentos.incrementAndGet();
		Thread.sleep(tempo());
	}

	private void comer() throws InterruptedException {
		refeicoes.incrementAndGet();
		Thread.sleep(tempo());
	}

	private boolean pegaGarfos() throws InterruptedException {

		boolean garfoEsquerda, garfoDireita, comecaPelaEquerda = random.nextBoolean();

		if (comecaPelaEquerda) {

			garfoEsquerda = pegaGarfoEsquerda();

			if (garfoEsquerda) {
				garfoDireita = pegaGarfoDireita();

				if (garfoDireita) {
					return true;
				} else {
					soltaGarfoEsquerda();
				}
			}
			return false;
		} else { // começa pela direita

			garfoDireita = pegaGarfoDireita();

			if (garfoDireita) {
				garfoEsquerda = pegaGarfoEsquerda();

				if (garfoEsquerda) {
					return true;
				} else {
					soltaGarfoDireita();
				}
			}
			return false;
		}
	}

	@Override
	public void run() {
		int tentativasFalhas = 0;

		try {
			while (!Thread.currentThread().isInterrupted()) {
				System.out.println("Filosofo " + id + "esta PENSANDO.");
				pensar();

				if (pegaGarfos()) {
					tentativasFalhas = 0; // reseta as tentativas apos pegarf os garfos
					System.out.println("Filosofo " + id + "esta COMENDO.");
					comer();					
					soltaGarfos();
				} else {
					tentativasFalhas++;
					if (tentativasFalhas >= INANICAO) {
						System.out.println("Filosofo " + id + " esta morrendo de INANICAO!");
						tentativasFalhas = 0; // resetas as tentativas
					}
				}
			}
		} catch (InterruptedException e) {
			// thread interrompida pelo main
			System.out.println("Filosofo " + id + " finalizou a execucao.");
		}
	}

}
