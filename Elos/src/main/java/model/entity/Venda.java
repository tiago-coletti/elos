package model.entity;

public class Venda {
	private int id;
	private double valorTotal;
	private String dataVenda;
	private int empreendimentoId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Venda() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Venda(int id, double valorTotal, String dataVenda, int empreendimentoId, String created_at,
			String updated_at, String deleted_at) {
		super();
		this.id = id;
		this.valorTotal = valorTotal;
		this.dataVenda = dataVenda;
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

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(String dataVenda) {
		this.dataVenda = dataVenda;
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
