package model.entity;

public class Aluno {
	private int id;
	private String nome;
	private String matricula;
	private String curso;
	
	public Aluno(int id, String nome, String matricula, String curso) {
		super();
		this.id = id;
		this.nome = nome;
		this.matricula = matricula;
		this.curso = curso;
	}

	public Aluno() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}
}