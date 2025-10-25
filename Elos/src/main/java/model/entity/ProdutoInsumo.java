package model.entity;

public class ProdutoInsumo {
	private int id;
	private double quantidadeUtilizada;
	private int insumoId;
	private int produtoId;
	private String insumoNome;
	private Insumo insumo;
	
	public ProdutoInsumo() {
		super();
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

	public String getInsumoNome() {
		return insumoNome;
	}

	public void setInsumoNome(String insumoNome) {
		this.insumoNome = insumoNome;
	}
	
	public Insumo getInsumo() {
		return insumo;
	}

	public void setInsumo(Insumo insumo) {
		this.insumo = insumo;
	}
	
	public double getCustoTotal() {
		if (insumo != null && insumo.getCustoEstimado() > 0) {
			return quantidadeUtilizada * insumo.getCustoEstimado();
		}
		return 0.0;
	}
}