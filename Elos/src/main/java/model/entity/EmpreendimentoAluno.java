package model.entity;

public class EmpreendimentoAluno {
	private int empreendimentoId;
	private int alunoId;
	private String anoSemestre;
	
	public EmpreendimentoAluno(int empreendimentoId, int alunoId, String anoSemestre) {
		super();
		this.empreendimentoId = empreendimentoId;
		this.alunoId = alunoId;
		this.anoSemestre = anoSemestre;
	}
	
	public EmpreendimentoAluno() {
		super();
	}
	
	public int getEmpreendimentoId() {
		return empreendimentoId;
	}
	
	public void setEmpreendimentoId(int empreendimentoId) {
		this.empreendimentoId = empreendimentoId;
	}
	
	public int getAlunoId() {
		return alunoId;
	}
	
	public void setAlunoId(int alunoId) {
		this.alunoId = alunoId;
	}
	
	public String getAnoSemestre() {
		return anoSemestre;
	}
	
	public void setAnoSemestre(String anoSemestre) {
		this.anoSemestre = anoSemestre;
	}
}