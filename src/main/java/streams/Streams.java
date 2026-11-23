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

        Map<String, List<String>> result = getTopCertificationsByDept(employees, 80000.0);
        result.forEach((dept, certs) -> System.out.println(dept + " -> " + certs));

    }

    public static Map<String, List<String>> getTopCertificationsByDept(
            List<Employee> employees, double minSalary) {
        Map<String, List<String>> grouped = new HashMap<>();


        if(employees == null || employees.isEmpty()){
            return Map.of();
        }
/*
        employees.stream()
                .filter(e -> e.getSalary() >= minSalary)
                .filter(e -> e.getCertifications() != null && !e.getCertifications().isEmpty() )
                .forEach(e -> {
                    if(grouped.containsKey(e.getDepartment())){
                        grouped.get(e.getDepartment()).addAll(e.getCertifications());
                    } else {
                        grouped.put(e.getDepartment(), new HashSet<>(e.getCertifications()));
                    }
                });*/

        grouped = employees.stream()
                // Descarta objetos Employee nulos para evitar NullPointerException
                .filter(Objects::nonNull)
                // Conserva solo empleados cuyo salario sea mayor o igual al umbral mínimo
                .filter(e -> e.getSalary() >= minSalary)
                // Verifica que la lista de certificaciones no sea nula antes de convertirla en stream
                .filter(e -> e.getCertifications() != null)
                // Aplana la lista de certificaciones de cada empleado en un stream de pares (departamento, certificación)
                .flatMap(e -> e.getCertifications().stream()
                        // Ignora certificaciones nulas dentro de la lista del empleado
                        .filter(Objects::nonNull)
                        // Empareja el departamento del empleado (clave) con cada certificación (valor)
                        .map(cert -> Map.entry(e.getDepartment(), cert)))
                // Agrupa los pares en un Map<String, List<String>> usando el departamento como clave Engineering -> [AWS, Java, AWS]
                .collect(Collectors.groupingBy(
        /*          Capa 1: Collectors.groupingBy(Map.Entry::getKey, ...)
                    Es el colector principal (el contenedor externo). Funciona igual que un GROUP BY en SQL:
                    Primer argumento (Map.Entry::getKey): Es la función clasificadora. Toma cada pareja y pregunta: "¿Cuál es tu clave?". Como la clave es el departamento ("Engineering", "Sales"), crea cubetas (buckets) en el mapa para cada departamento distinto.
                    Segundo argumento (el downstream collector): En lugar de guardar la lista cruda de Map.Entry en cada cubeta (que es lo que haría groupingBy si no le pasas un segundo argumento), le dice a Java: "No guardes los pares completos; pásalos a este sub-colector para que los procese antes de guardarlos en el mapa".*/
                        // Clasificador: usa la clave de la entrada (departamento) como clave del mapa
                        Map.Entry::getKey,
                        // Paso downstream 1: extrae únicamente el valor de la entrada (la certificación)
                        Collectors.mapping(
                        /*Capa 2: Collectors.mapping(Map.Entry::getValue, ...)
                        ¿Qué hace? Actúa como un .map() interno dentro de cada grupo. Recibe el par ("Engineering", "AWS") y extrae únicamente el valor ("AWS"), descartando la clave del departamento porque ya sabemos en qué cubeta estamos.
                        ¿Por qué es vital? Porque el siguiente paso necesita recibir objetos String sueltos (certificaciones), no objetos Map.Entry.
                        Segundo argumento: Envía esos String extraídos al siguiente colector en la cadena.*/
                                Map.Entry::getValue,
                                // Paso downstream 2: acumula en un TreeSet ordenado de Z a A
                                // (elimina duplicados y ordena automáticamente), luego convierte a ArrayList
                                Collectors.collectingAndThen(
                                        /*Capa 3: Collectors.collectingAndThen(colector, funcionFinalizadora)
                                        Este colector ejecuta una operación de dos tiempos sobre los String de un mismo departamento:
                                        Primera parte (Collectors.toCollection(...)): Recolecta todos los String en una estructura específica.
                                        Segunda parte (ArrayList::new): Una vez que terminó de meter todos los elementos del departamento en esa estructura, aplica una transformación final antes de cerrar el grupo.*/
                                        Collectors.toCollection(() -> new TreeSet<String>(Comparator.reverseOrder())),
                                        ArrayList::new
                                        /*Capa 4: Collectors.toCollection(() -> new TreeSet<String>(Comparator.reverseOrder()))
                                        Aquí ocurre la magia de deduplicación y ordenamiento automático:
                                        TreeSet<String>: Al ser un Set, ignora automáticamente los duplicados (si "AWS" entra dos veces para "Engineering", solo se guarda una). Además, al ser un Tree, mantiene los elementos ordenados en todo momento mientras se insertan.
                                        Comparator.reverseOrder(): Le indica al TreeSet que, en lugar de usar el orden alfabético natural (A -> Z), use el orden inverso (Z -> A).
                                        El remate con ArrayList::new (de la Capa 3): Como la firma de tu método exige devolver Map<String, List<String>> (una List, no un Set), collectingAndThen toma ese TreeSet ya limpio y ordenado y lo envuelve en un new ArrayList<>(treeSet), conservando exactamente el orden Z -> A.*/
                                )
                        )
                ));

        /*

Resumen visual de una cubeta ("Engineering")
Entran pares: ("Engineering", "AWS"), ("Engineering", "Java"), ("Engineering", "AWS")
groupingBy los agrupa bajo la llave "Engineering".
mapping extrae solo valores: "AWS", "Java", "AWS"
TreeSet(reverseOrder) limpia y ordena: ["Java", "AWS"] (tipo TreeSet)
collectingAndThen(ArrayList::new) convierte el tipo: ["Java", "AWS"] (tipo ArrayList, listo para el mapa final).
        * */


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
