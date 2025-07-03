package model.entity;

public class Investimento {
	private int id;
	private String descricao;
	private double valor;
	private String dataInvestimento;
	private int empreendimentoId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Investimento() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Investimento(int id, String descricao, double valor, String dataInvestimento, int empreendimentoId,
			String created_at, String updated_at, String deleted_at) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.valor = valor;
		this.dataInvestimento = dataInvestimento;
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
