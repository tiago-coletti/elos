package model.entity;

public class Avaliacao {
	private int id;
	private String dataAvaliacao;
	private double notaTotal;
	private int empreendimentoId;
	String created_at;
	String updated_at;
	String deleted_at;
	
	public Avaliacao() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Avaliacao(int id, String dataAvaliacao, double notaTotal, int empreendimentoId, String created_at,
			String updated_at, String deleted_at) {
		super();
		this.id = id;
		this.dataAvaliacao = dataAvaliacao;
		this.notaTotal = notaTotal;
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
