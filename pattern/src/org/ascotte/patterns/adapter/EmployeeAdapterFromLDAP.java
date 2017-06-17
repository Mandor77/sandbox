package org.ascotte.patterns.adapter;

public class EmployeeAdapterFromLDAP implements Employee {

	private EmployeeLDAP instance;
	
	public EmployeeAdapterFromLDAP(EmployeeLDAP instance) {
		this.instance = instance;
	}
	
	@Override
	public String getId() {
		return instance.getCn();
	}

	@Override
	public String getFirstName() {
		return instance.getGivenName();
	}

	@Override
	public String getLastName() {
		return instance.getSurname();
	}

	@Override
	public String getEmail() {
		return instance.getMail();
	}

}
