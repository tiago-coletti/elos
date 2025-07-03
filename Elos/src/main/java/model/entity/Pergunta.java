package model.entity;

public class Pergunta {
	private int id;
	private String texto;
	
	public Pergunta() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Pergunta(int id, String texto) {
		super();
		this.id = id;
		this.texto = texto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
}
