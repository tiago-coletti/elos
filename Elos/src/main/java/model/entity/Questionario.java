package model.entity;

import java.util.List;

public class Questionario {
	private long id;
	private String nome;
	private String descricao;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	private List<Pergunta> perguntas;
	
	public Questionario() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Questionario(long id, String nome, String descricao, String createdAt, String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(String deletedAt) {
		this.deletedAt = deletedAt;
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}
}