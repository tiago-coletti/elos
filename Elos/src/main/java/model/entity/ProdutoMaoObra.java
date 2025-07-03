package model.entity;

public class ProdutoMaoObra {
	private int id;
	private double horasUtilizadas;
	private int maoObraId;
	private int produtoId;
	
	public ProdutoMaoObra() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ProdutoMaoObra(int id, double horasUtilizadas, int maoObraId, int produtoId) {
		super();
		this.id = id;
		this.horasUtilizadas = horasUtilizadas;
		this.maoObraId = maoObraId;
		this.produtoId = produtoId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getHorasUtilizadas() {
		return horasUtilizadas;
	}

	public void setHorasUtilizadas(double horasUtilizadas) {
		this.horasUtilizadas = horasUtilizadas;
	}

	public int getMaoObraId() {
		return maoObraId;
	}

	public void setMaoObraId(int maoObraId) {
		this.maoObraId = maoObraId;
	}

	public int getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(int produtoId) {
		this.produtoId = produtoId;
	}
}
