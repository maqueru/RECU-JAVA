package model;

import java.io.Serializable;
import java.util.List;

public class Campeon implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String nombre;
	private String raza;
	private Integer vida;
	private Integer energia;
	private List<Habilidad> habilidades;
	private List<Objeto> objetos ;

	public Campeon() {
		this.id = 0;
		this.nombre = "";
		this.raza = "";
		this.vida=0;
		this.energia=0;
	}
	public Campeon(int id, String nombre, String raza, Integer vida, Integer energia, List<Habilidad> habilidades, List<Objeto> objetos) {
		this.id = id;
		this.nombre = nombre;
		this.raza = raza;
		this.vida=vida;
		this.energia=energia;
		/*this.habilidades=habilidades;
		this.objetos=objetos;*/
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
	
	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public Integer getVida() {
		return vida;
	}

	public void setVida(Integer vida) {
		this.vida = vida;
	}
	public Integer getEnergia() {
		return energia;
	}
	public void setEnergia(Integer energia) {
		this.energia = energia;
	}
	public List<Habilidad> getHabilidades() {
		return habilidades;
	}
	public void setHabilidades(List<Habilidad> habilidades) {
		this.habilidades = habilidades;
	}
	public List<Objeto> getObjetos() {
		return objetos;
	}
	public void setObjetos(List<Objeto> objetos) {
		this.objetos = objetos;
	}
	public void imprimir(){
		System.out.println("Id: " + id);
		System.out.println("Name: " + nombre);
		System.out.println("Vida: " + vida);
		System.out.println("Raza: " + raza);
		System.out.println("Energia: " + energia);
		System.out.println("Habilidades: " + habilidades);
		System.out.println("Objetos: " + objetos);
	}

}
