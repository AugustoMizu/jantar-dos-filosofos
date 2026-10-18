package jantarFilosofos;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import enums.Parametros;

public class Filosofo extends Thread {
	private final int id; // id vai ser a posição no array
	private final Garfo garfoEsquerda;
	private final Garfo garfoDireita;
	private final Semaphore garcom; // limita quantos filosofos disputam garfos ao mesmo tempo

	final int TEMPO_MAXIMO = Parametros.TEMPO_MAXIMO.getValor(); // usado para definir intervalos de ms
	final int TIMEOUT_GARFO = Parametros.TIMEOUT_GARFO.getValor(); // tempo maximo de espera por um garfo
	final int INANICAO = Parametros.INANICAO.getValor(); // tentativas de comer antes de escrever um aviso
	private Random random = new Random();

	// contadores para exibir no main
	public AtomicInteger refeicoes = new AtomicInteger(0);
	public AtomicInteger pensamentos = new AtomicInteger(0);

	public Filosofo(int id, Garfo garfoEsquerda, Garfo garfoDireita, Semaphore garcom) {
		this.id = id;
		this.garfoEsquerda = garfoEsquerda;
		this.garfoDireita = garfoDireita;
		this.garcom = garcom;
	}

	// tenta pegar o garfo esquerdo esperando no maximo TIMEOUT_GARFO
	private boolean pegaGarfoEsquerda() {
		return this.garfoEsquerda.tentaPegar(tempo(TIMEOUT_GARFO));
	}

	// tenta pegar o garfo direito esperando no maximo TIMEOUT_GARFO
	private boolean pegaGarfoDireita() {
		return this.garfoDireita.tentaPegar(tempo(TIMEOUT_GARFO));
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

	// gera um int aleatorio entre 1 e o parametro
	private int tempo(int parametro) {
		return random.nextInt(parametro) + 1;
	}

	private void pensar() throws InterruptedException {
		pensamentos.incrementAndGet();
		Thread.sleep(tempo(TEMPO_MAXIMO));
	}

	private void comer() throws InterruptedException {
		refeicoes.incrementAndGet();
		Thread.sleep(tempo(TEMPO_MAXIMO));
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
				System.out.println("Filosofo " + id + " esta PENSANDO.");
				pensar();

				// pede permissao ao garcom (fila FIFO). Fica fora do try para nao
				// liberar uma permissao que nao foi adquirida se for interrompido.
				garcom.acquire();
				try {
					if (pegaGarfos()) {
						tentativasFalhas = 0; // reseta as tentativas apos pegar os garfos
						System.out.println("Filosofo " + id + " esta COMENDO.");
						try {
							comer();
						} finally {
							soltaGarfos(); // sempre devolve os garfos, mesmo se interrompido comendo
						}
					} else {
						tentativasFalhas++;
						if (tentativasFalhas >= INANICAO) {
							System.out.println("Filosofo " + id + " esta morrendo de INANICAO!");
							tentativasFalhas = 0; // reseta as tentativas
						}
					}
				} finally {
					garcom.release(); // so devolve a permissao depois de soltar os garfos
				}
			}
		} catch (InterruptedException e) {
			// thread interrompida pelo main
		}
		System.out.println("Filosofo " + id + " finalizou a execucao.");
	}

}