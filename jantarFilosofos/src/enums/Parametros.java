package enums;

public enum Parametros {
	/**usado em {@link jantarFilosofos/Filosofo}*/
	TEMPO_MAXIMO(1000),	// define tempo maximo de comer e pensar
	INANICAO(20); // define tentativas maximas antes de declarar inanição
	
	private final int valor;
	
	Parametros(int valor){
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
	
}
