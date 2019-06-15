package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = FileSend.class)
public class FileSend {

	private String name;
	private byte[] base;	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getBase() {
		return base;
	}

	public void setBase(byte[] base) {
		this.base = base;
	}

	@Override
	public String toString() {
		return "FileSend [name=" + name + ", base=" + base + "]";
	}

}
