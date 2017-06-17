package org.ascotte.patterns.adapter;

import java.util.List;

public class AdapterDemo {

	public static void main(String[] args) {

		EmployeeClient client = new EmployeeClient();

		List<Employee> employees = client.getEmployeeList();

		for (Employee employee : employees) {
			System.out.println(employee.getId() + " " + employee.getFirstName() + " " + employee.getLastName() + " "
					+ employee.getEmail());
		}
	}
}
