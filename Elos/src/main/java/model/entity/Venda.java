package model.entity;

public class Venda {
	private int id;
	private double valorTotal;
	private String dataVenda;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Venda() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Venda(int id, double valorTotal, String dataVenda, int empreendimentoId, String createdAt,
			String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.valorTotal = valorTotal;
		this.dataVenda = dataVenda;
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
