package model.entity;

public class Compra {
	private int id;
	private String dataCompra;
	private double valorTotal;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Compra() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Compra(int id, String dataCompra, double valorTotal, int empreendimentoId, String createdAt,
			String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.dataCompra = dataCompra;
		this.valorTotal = valorTotal;
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

	public String getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(String dataCompra) {
		this.dataCompra = dataCompra;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
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
