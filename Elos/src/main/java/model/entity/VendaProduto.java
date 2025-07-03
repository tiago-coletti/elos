package model.entity;

public class VendaProduto {
	private int id;
	private double quantidade;
	private double precoUnitario;
	private int produtoId;
	private int vendaId;
	
	public VendaProduto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public VendaProduto(int id, double quantidade, double precoUnitario, int produtoId, int vendaId) {
		super();
		this.id = id;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
		this.produtoId = produtoId;
		this.vendaId = vendaId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}
	
	public double getPrecoUnitario() {
		return precoUnitario;
	}
	
	public void setPrecoUnitario(double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}
	
	public int getProdutoId() {
		return produtoId;
	}
	
	public void setProdutoId(int produtoId) {
		this.produtoId = produtoId;
	}
	
	public int getVendaId() {
		return vendaId;
	}
	
	public void setVendaId(int vendaId) {
		this.vendaId = vendaId;
	}
	
}
