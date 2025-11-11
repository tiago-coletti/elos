package model.entity;

public class Resposta {
	private long id;
	private int empreendimentoId;
	private long perguntaId;
	private String textoResposta;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Resposta() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Resposta(long id, int empreendimentoId, long perguntaId, String textoResposta, String createdAt,
			String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.empreendimentoId = empreendimentoId;
		this.perguntaId = perguntaId;
		this.textoResposta = textoResposta;
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

	public int getEmpreendimentoId() {
		return empreendimentoId;
	}

	public void setEmpreendimentoId(int empreendimentoId) {
		this.empreendimentoId = empreendimentoId;
	}

	public long getPerguntaId() {
		return perguntaId;
	}

	public void setPerguntaId(long perguntaId) {
		this.perguntaId = perguntaId;
	}

	public String getTextoResposta() {
		return textoResposta;
	}

	public void setTextoResposta(String textoResposta) {
		this.textoResposta = textoResposta;
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
}