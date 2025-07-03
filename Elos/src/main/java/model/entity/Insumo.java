package model.entity;

public class Insumo {
	private int id;
	private String nome;
	private String unidadeMedida;
	private int empreendimentoId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Insumo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Insumo(int id, String nome, String unidadeMedida, int empreendimentoId, String created_at, String updated_at,
			String deleted_at) {
		super();
		this.id = id;
		this.nome = nome;
		this.unidadeMedida = unidadeMedida;
		this.empreendimentoId = empreendimentoId;
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
