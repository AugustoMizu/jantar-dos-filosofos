package enums;

public enum Parametros {
	/**usado em {@link jantarFilosofos/Filosofo}*/
	TEMPO_MAXIMO(100), // maior intervalo (ms) usado em pensar e comer
	TIMEOUT_GARFO(500), // tempo maximo (ms) de espera por um garfo antes de desistir
	INANICAO(5); // tentativas seguidas sem comer antes de avisar

	private final int valor;

	Parametros(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}