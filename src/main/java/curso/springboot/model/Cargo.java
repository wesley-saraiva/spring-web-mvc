package curso.springboot.model;

public enum Cargo {

	JUNIOR("Junior"), PLENO("Pleno"), SENIOR("Senior");

	private String nome;

	private Cargo(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
