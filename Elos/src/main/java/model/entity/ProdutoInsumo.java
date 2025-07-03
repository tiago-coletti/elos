package model.entity;

public class ProdutoInsumo {
	private int id;
	private double quantidadeUtilizada;
	private int insumoId;
	private int produtoId;
	
	public ProdutoInsumo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProdutoInsumo(int id, double quantidadeUtilizada, int insumoId, int produtoId) {
		super();
		this.id = id;
		this.quantidadeUtilizada = quantidadeUtilizada;
		this.insumoId = insumoId;
		this.produtoId = produtoId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getQuantidadeUtilizada() {
		return quantidadeUtilizada;
	}

	public void setQuantidadeUtilizada(double quantidadeUtilizada) {
		this.quantidadeUtilizada = quantidadeUtilizada;
	}

	public int getInsumoId() {
		return insumoId;
	}

	public void setInsumoId(int insumoId) {
		this.insumoId = insumoId;
	}

	public int getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(int produtoId) {
		this.produtoId = produtoId;
	}
	
}
