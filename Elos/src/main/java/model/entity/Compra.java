package model.entity;

public class Compra {
	private int id;
	private String dataCompra;
	private double valorTotal;
	private int empreendimentoId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Compra() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Compra(int id, String dataCompra, double valorTotal, int empreendimentoId, String created_at,
			String updated_at, String deleted_at) {
		super();
		this.id = id;
		this.dataCompra = dataCompra;
		this.valorTotal = valorTotal;
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
