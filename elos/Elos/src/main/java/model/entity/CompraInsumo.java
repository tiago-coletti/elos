package model.entity;

public class CompraInsumo {
	private int id;
	private double precoUnitario;
	private double quantidadeComprada;
	private double quantidadeRestante;
	private int compraId;
	private int insumoId;
	
	public CompraInsumo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CompraInsumo(int id, double precoUnitario, double quantidadeComprada, double quantidadeRestante, int compraId, int insumoId) {
		super();
		this.id = id;
		this.precoUnitario = precoUnitario;
		this.quantidadeComprada = quantidadeComprada;
		this.quantidadeRestante = quantidadeRestante;
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
	public double getQuantidadeComprada() {
		return quantidadeComprada;
	}
	public void setQuantidadeComprada(double quantidadeComprada) {
		this.quantidadeComprada = quantidadeComprada;
	}
	public double getQuantidadeRestante() {
		return quantidadeRestante;
	}
	public void setQuantidadeRestante(double quantidadeRestante) {
		this.quantidadeRestante = quantidadeRestante;
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
