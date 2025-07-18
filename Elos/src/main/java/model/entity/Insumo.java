package model.entity;

public class Insumo {
	private int id;
	private String nome;
	private String unidadeMedida;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Insumo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Insumo(int id, String nome, String unidadeMedida, int empreendimentoId, String createdAt, String updatedAt,
			String deletedAt) {
		super();
		this.id = id;
		this.nome = nome;
		this.unidadeMedida = unidadeMedida;
		this.empreendimentoId = empreendimentoId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
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

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public int getEmpreendimentoId() {
		return empreendimentoId;
	}

	public void setEmpreendimentoId(int empreendimentoId) {
		this.empreendimentoId = empreendimentoId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String created_at) {
		this.createdAt = created_at;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updated_at) {
		this.updatedAt = updated_at;
	}

	public String getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(String deleted_at) {
		this.deletedAt = deleted_at;
	}
	
}
