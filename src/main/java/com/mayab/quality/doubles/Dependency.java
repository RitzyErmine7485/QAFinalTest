package com.mayab.quality.doubles;

public class Dependency {
	private final SubDependency subDependency;
	
	public Dependency(SubDependency subdependency) {
		super();
		this.subDependency = subdependency;
	}
	
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
	
	public String getSubDependencyClassName() {
		return subDependency.getClassName();
	}
	
	public int addTwo(int i) {
		return i + 2;
	}
	
	public String getClassNameUpperCase() {
		return getClassName().toUpperCase();
	}
}
