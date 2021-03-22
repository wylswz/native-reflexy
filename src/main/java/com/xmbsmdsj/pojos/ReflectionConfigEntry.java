package com.xmbsmdsj.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReflectionConfigEntry {
	@JsonProperty
	public String name;
	@JsonProperty
	public Boolean allDeclaredConstructors;
	@JsonProperty
	public Boolean allPublicConstructors;
	@JsonProperty
	public Boolean allDeclaredMethods;
	@JsonProperty
	public Boolean allPublicMethods;
	@JsonProperty
	public Boolean allDeclaredClasses;
	@JsonProperty
	public Boolean allPublicClasses;

	public ReflectionConfigEntry(String className) {
		this.name = className;
		this.allDeclaredClasses = true;
		this.allDeclaredConstructors = true;
		this.allDeclaredMethods = true;
		this.allPublicClasses = true;
		this.allPublicMethods = true;
		this.allPublicConstructors = true;
	}
}
