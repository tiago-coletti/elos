package model.entity;

public class CompraInsumo {
	private int id;
	private double precoUnitario;
	private double quantidade;
	private int compraId;
	private int insumoId;
	
	public CompraInsumo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CompraInsumo(int id, double precoUnitario, double quantidade, int compraId, int insumoId) {
		super();
		this.id = id;
		this.precoUnitario = precoUnitario;
		this.quantidade = quantidade;
		this.compraId = compraId;
		this.insumoId = insumoId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getPrecoUnitario() {
		return precoUnitario;
	}
	public void setPrecoUnitario(double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}
	public double getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}
	public int getCompraId() {
		return compraId;
	}
	public void setCompraId(int compraId) {
		this.compraId = compraId;
	}
	public int getInsumoId() {
		return insumoId;
	}
	public void setInsumoId(int insumoId) {
		this.insumoId = insumoId;
	}
	
}
