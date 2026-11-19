/*

getTopCertificationsByDept(List<Employee> employees, double minSalary) returning a Map<String, List<String>> where:

- Each key is a department name.
- Each value is a list of unique certifications (no duplicates) from employees in that department earning at least minSalary, sorted in reverse alphabetical order (Z -> A).
- Rule: If a department has no valid certifications under these conditions, omit it from the resulting map (do not include empty lists).

*/
package streams;
import java.util.*;
import java.util.stream.Collectors;


public class Streams {
    public static void main(String[] args){

        List<Employee> employees = List.of(
                new Employee("Ana", "Engineering", 85000.0, List.of("AWS", "Java", "AWS")),
                new Employee("Luis", "Engineering", 95000.0, List.of("Kubernetes", "Java", "Docker")),
                new Employee("Sofia", "Engineering", 60000.0, List.of("Scrum", "AWS")),
                new Employee("Carlos", "Sales", 72000.0, List.of("Salesforce", "HubSpot")),
                new Employee("Elena", "Sales", 90000.0, List.of("HubSpot", "Negotiation")),
                new Employee("Mateo", "HR", 55000.0, List.of("SHRM")),
                new Employee("Valeria", "Finance", 88000.0, List.of("CPA", "CFA", "Excel")),
                new Employee("Diego", "Finance", 65000.0, List.of("Excel", "CFA")),
                new Employee("Lucia", "Support", 90000.0, List.of()),
                new Employee("Javier", "Engineering", 110000.0, List.of("AWS", "Terraform"))
        );

        Map<String, Set<String>> result = getTopCertificationsByDept(employees, 80000.0);
        result.forEach((dept, certs) -> System.out.println(dept + " -> " + certs));

    }

    public static Map<String, Set<String>> getTopCertificationsByDept(
            List<Employee> employees, double minSalary) {
        Map<String, Set<String>> grouped = new HashMap<>();


        if(employees == null || employees.isEmpty()){
            return Map.of();
        }

        employees.stream()
                .filter(e -> e.getSalary() >= minSalary)
                .filter(e -> e.getCertifications() != null && !e.getCertifications().isEmpty() )
                .forEach(e -> {
                    if(grouped.containsKey(e.getDepartment())){
                        grouped.get(e.getDepartment()).addAll(e.getCertifications());
                    } else {
                        grouped.put(e.getDepartment(), new HashSet<>(e.getCertifications()));
                    }
                });


        return grouped;
    }

}



class Employee {
    private String name;
    private String department;
    private double salary;
    private List<String> certifications;

    public Employee(String name, String department, double salary, List<String> certifications) {
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.certifications = certifications;
    }

    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public List<String> getCertifications() { return certifications; }
}
