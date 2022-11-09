package com.fullstackmaina.streamService.model;

import lombok.Data;


@Data
public class Diabete {
	private String preg;
	private String plas;
	private String pres;
	private String skin;
	private String insu;
	private String mass;
	private String pedi;
	private String age;
	private String classe;

	public String[] line(){
		String[] line = {preg,plas,pres,skin,insu,mass,pedi,age,classe};
		return line;
	}
}

