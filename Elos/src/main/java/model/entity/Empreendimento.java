package model.entity;

import java.util.ArrayList;
import java.util.List;

public class Empreendimento {
	private int id;
	private String nome;
	private String email;
	private String senha;
	private String login;
	private String tipo;
	private String numeroTelefone;
	private String cidade;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
	private double saldo;
	
    private int cursoGeralId;
    private String anoSemestre;
    private List<Aluno> alunos = new ArrayList<>();
	
	public Empreendimento() {
		super();
	}

	public Empreendimento(int id, String nome, String email, String senha, String login, String tipo,
            String numeroTelefone, String cidade, String createdAt, String updatedAt, String deletedAt) {
        super();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.login = login;
        this.tipo = tipo;
        this.numeroTelefone = numeroTelefone;
        this.cidade = cidade;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNumeroTelefone() {
		return numeroTelefone;
	}

	public void setNumeroTelefone(String numeroTelefone) {
		this.numeroTelefone = numeroTelefone;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
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
	
    public double getSaldo() {
		return saldo;
	}

	public boolean isAluno() {
        return "ALUNO".equalsIgnoreCase(this.tipo);
    }
    
    public int getCursoGeralId() {
        return cursoGeralId;
    }

    public void setCursoGeralId(int cursoGeralId) {
        this.cursoGeralId = cursoGeralId;
    }

    public String getAnoSemestre() {
        return anoSemestre;
    }

    public void setAnoSemestre(String anoSemestre) {
        this.anoSemestre = anoSemestre;
    }
    
    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

	public void setSaldo(double double1) {
		// TODO Auto-generated method stub
		
	}
}