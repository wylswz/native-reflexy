package com.xmbsmdsj.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReflectionConfigEntry {
	@JsonProperty
	String name;
	@JsonProperty
	Boolean allDeclaredConstructors;
	@JsonProperty
	Boolean allPublicConstructors;
	@JsonProperty
	Boolean allDeclaredMethods;
	@JsonProperty
	Boolean allPublicMethods;
	@JsonProperty
	Boolean allDeclaredClasses;
	@JsonProperty
	Boolean allPublicClasses;

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
