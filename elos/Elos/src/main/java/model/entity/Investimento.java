package model.entity;

public class Investimento {
	private int id;
	private String descricao;
	private double valor;
	private String dataInvestimento;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Investimento() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Investimento(int id, String descricao, double valor, String dataInvestimento, int empreendimentoId,
			String createdAt, String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.valor = valor;
		this.dataInvestimento = dataInvestimento;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getDataInvestimento() {
		return dataInvestimento;
	}

	public void setDataInvestimento(String dataInvestimento) {
		this.dataInvestimento = dataInvestimento;
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
