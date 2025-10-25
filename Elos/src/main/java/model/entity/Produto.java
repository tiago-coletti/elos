package model.entity;

import java.util.List;
import java.util.Objects;

public class Produto {
	private int id;
	private String nome;
	private double precoVenda;
	private int empreendimentoId;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	
	private List<ProdutoInsumo> insumos;
    private List<ProdutoMaoObra> maosObra;
	
	public Produto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Produto(int id, String nome, double precoVenda, int empreendimentoId, String createdAt, String updatedAt,
			String deletedAt) {
		super();
		this.id = id;
		this.nome = nome;
		this.precoVenda = precoVenda;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getPrecoVenda() {
		return precoVenda;
	}

	public void setPrecoVenda(double precoVenda) {
		this.precoVenda = precoVenda;
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

	public List<ProdutoInsumo> getInsumos() {
		return insumos;
	}

	public void setInsumos(List<ProdutoInsumo> insumos) {
		this.insumos = insumos;
	}

	public List<ProdutoMaoObra> getMaosObra() {
		return maosObra;
	}

	public void setMaosObra(List<ProdutoMaoObra> maosObra) {
		this.maosObra = maosObra;
	}

	// ========== MÉTODOS ADICIONADOS PARA CORREÇÃO ==========

	/**
	 * Calcula o custo total somando os custos de todos os insumos da lista.
	 * @return A soma dos custos dos insumos.
	 */
	public double getCustoTotalInsumos() {
		if (insumos == null) {
			return 0.0;
		}
		return insumos.stream()
				.filter(Objects::nonNull)
				.mapToDouble(ProdutoInsumo::getCustoTotal)
				.sum();
	}

	/**
	 * Calcula o custo total somando os custos de todas as etapas de mão de obra.
	 * @return A soma dos custos da mão de obra.
	 */
	public double getCustoTotalMaoObra() {
		if (maosObra == null) {
			return 0.0;
		}
		return maosObra.stream()
				.filter(Objects::nonNull)
				.mapToDouble(ProdutoMaoObra::getCustoTotalEtapa)
				.sum();
	}
}