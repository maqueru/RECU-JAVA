package model;

import java.io.Serializable;

public class Objeto implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	private Integer valor;
	private String efecto;

	public Objeto() {
		this.id = 0;
		this.nombre = "";
		this.valor = 0;
		this.efecto="";
	}
	
	public Objeto(int id, String nombre, Integer valor, String efecto) {
		this.id = id;
		this.nombre = nombre;
		this.valor = valor;
		this.efecto= efecto;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return nombre;
	}

	public void setName(String nombre) {
		this.nombre = nombre;
	}
	
	public void setValor(Integer valor) {
		this.valor = valor;
	}
	public Integer getValor() {
		return valor;
	}

	public String getEfecto() {
		return efecto;
	}

	public void setEfecto(String efecto) {
		this.efecto = efecto;
	}


	
	public void imprimir(){
		System.out.println("Id: " + id);
		System.out.println("nombre: " + nombre);
		System.out.println("valor: " + valor);
		System.out.println("efecto: " + efecto);
	}
}
