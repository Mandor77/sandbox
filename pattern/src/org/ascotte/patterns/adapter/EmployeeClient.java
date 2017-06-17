package org.ascotte.patterns.adapter;

import java.util.ArrayList;
import java.util.List;

public class EmployeeClient {

	public List<Employee> getEmployeeList() {
		
		List<Employee> employees = new ArrayList<>();
		
		Employee employeeFromDB = new EmployeeDB("1", "Adrien", "Scotte", "adrien.scotte@null.com");
		employees.add(employeeFromDB);
		
		EmployeeLDAP employeeFromLDAP = new EmployeeLDAP("2", "Bob", "Dylan", "bob.dylan@null.com");
		employees.add(new EmployeeAdapterFromLDAP(employeeFromLDAP));
		
		return employees;
	}
}
