package com.example.workflow;

import java.io.Serializable;

public class Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	
	public Entity(Long id) {
		super();
		this.id = id;
	}

	public Entity(Long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public Entity setName(String name) {
		this.name = name;
		return this;
		
	}

	public String getDescription() {
		return description;
	}

	public Entity setDescription(String description) {
		this.description = description;
		return this;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	
}
