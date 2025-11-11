package model.entity;

import java.util.List;

public class Pergunta {
	private long id;
	private long questionarioId;
	private String textoPergunta;
	private String tipoPergunta;
	private int ordem;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	private List<Resposta> respostas;
	
	public Pergunta() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Pergunta(long id, long questionarioId, String textoPergunta, String tipoPergunta, int ordem,
			String createdAt, String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.questionarioId = questionarioId;
		this.textoPergunta = textoPergunta;
		this.tipoPergunta = tipoPergunta;
		this.ordem = ordem;
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

	public long getQuestionarioId() {
		return questionarioId;
	}

	public void setQuestionarioId(long questionarioId) {
		this.questionarioId = questionarioId;
	}

	public String getTextoPergunta() {
		return textoPergunta;
	}

	public void setTextoPergunta(String textoPergunta) {
		this.textoPergunta = textoPergunta;
	}

	public String getTipoPergunta() {
		return tipoPergunta;
	}

	public void setTipoPergunta(String tipoPergunta) {
		this.tipoPergunta = tipoPergunta;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
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

	public List<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}
}