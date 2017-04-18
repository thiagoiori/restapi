package com.uab.dedam.API.models;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Grupo {
	
	public Grupo(){
		
	}
	
	public Grupo(Integer id,
			String name,
			String self){
		this.setId(id);
		this.setName(name);
		this.setSelf(self);
	}

	private Integer id;
	@JsonIgnore
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	private String self;
	public String getSelf() {
		return "/rest/grupo/" + this.id;
	}
	public void setSelf(String self) {
		this.self = self;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
