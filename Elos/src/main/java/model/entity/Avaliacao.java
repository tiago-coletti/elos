package model.entity;

public class Avaliacao {
	private int id;
	private String dataAvaliacao;
	private double notaTotal;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	public Avaliacao() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Avaliacao(int id, String dataAvaliacao, double notaTotal, int empreendimentoId, String createdAt,
			String updatedAt, String deletedAt) {
		super();
		this.id = id;
		this.dataAvaliacao = dataAvaliacao;
		this.notaTotal = notaTotal;
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

	public String getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(String dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public double getNotaTotal() {
		return notaTotal;
	}

	public void setNotaTotal(double notaTotal) {
		this.notaTotal = notaTotal;
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
