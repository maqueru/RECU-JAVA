package model;

import java.io.Serializable;

public class Habilidad implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	private Double coste;
	private String descripcion;

	public Habilidad() {
		this.id = 0;
		this.nombre = "";
		this.coste = 0.0;
		this.descripcion="";
	}
	
	public Habilidad(int id, String nombre, Double coste, String descripcion) {
		this.id = id;
		this.nombre = nombre;
		this.coste = coste;
		this.descripcion= descripcion;
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
	
	public void setCoste(Double coste) {
		this.coste = coste;
	}
	public Double getCost() {
		return coste;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	
	public void imprimir(){
		System.out.println("Id: " + id);
		System.out.println("nombre: " + nombre);
		System.out.println("coste: " + coste);
		System.out.println("descripcion: " + descripcion);
	}
}
