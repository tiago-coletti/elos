package model.entity;

public class MaoObra {
	private int id;
	private String nome;
	private double custoHora;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;

	public MaoObra() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MaoObra(int id, String nome, double custoHora, int empreendimentoId) {
		super();
		this.id = id;
		this.nome = nome;
		this.custoHora = custoHora;
		this.empreendimentoId = empreendimentoId;
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

	public double getCustoHora() {
		return custoHora;
	}

	public void setCustoHora(double custoHora) {
		this.custoHora = custoHora;
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
