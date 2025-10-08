package model.entity;

public class Resposta {
	private int id;
	private int resposta;
	private int avaliacaoId;
	private int perguntaId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Resposta() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Resposta(int id, int resposta, int avaliacaoId, int perguntaId, String created_at, String updated_at,
			String deleted_at) {
		super();
		this.id = id;
		this.resposta = resposta;
		this.avaliacaoId = avaliacaoId;
		this.perguntaId = perguntaId;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.deleted_at = deleted_at;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getResposta() {
		return resposta;
	}
	
	public void setResposta(int resposta) {
		this.resposta = resposta;
	}
	
	public int getAvaliacaoId() {
		return avaliacaoId;
	}
	
	public void setAvaliacaoId(int avaliacaoId) {
		this.avaliacaoId = avaliacaoId;
	}
	
	public int getPerguntaId() {
		return perguntaId;
	}
	
	public void setPerguntaId(int perguntaId) {
		this.perguntaId = perguntaId;
	}
	
	public String getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public String getUpdated_at() {
		return updated_at;
	}
	
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	public String getDeleted_at() {
		return deleted_at;
	}
	
	public void setDeleted_at(String deleted_at) {
		this.deleted_at = deleted_at;
	}
	
}
