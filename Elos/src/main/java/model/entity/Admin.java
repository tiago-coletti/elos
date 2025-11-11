package model.entity;

public class Admin {
	private int id;
	private String nome;
	private String login;
	private String email;
	private String senha;
	
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Admin(int id, String nome, String login, String email, String senha) {
		super();
		this.id = id;
		this.nome = nome;
		this.login = login;
		this.email = email;
		this.senha = senha;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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
}