import entities.Address;
import entities.Employee;
import entities.Project;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Engine implements Runnable{
    private final EntityManager entityManager;
    private BufferedReader bufferedReader;


    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Please select exercise number:");

        try {
            int exNum = Integer.parseInt(bufferedReader.readLine());

            switch (exNum){
                case 2: exTwo();
                case 3: exThree();
                case 4: exFour();
                case 5: exFive();
                case 6: exSix();
                case 7: exSeven();
                case 8: exEight();
                case 9: exNine();
                case 10: exTen();
                case 11: exEleven();
                case 12: exTwelve();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            entityManager.close();
        }

    }

    private void exTwelve() {
        entityManager.createQuery
                ("SELECT e FROM Employee e group by e.department.name having MAX(e.salary) between 30000 AND 70000", Employee.class)
                .getResultList()
                .forEach(employee -> {
                    System.out.printf("%s %.2f%n", employee.getDepartment().getName(), employee.getSalary());
                });

    }

    private void exEleven() throws IOException {

        System.out.println("Please insert a letter:");
        String startStr = bufferedReader.readLine();
       entityManager.createQuery("SELECT e FROM Employee e WHERE  e.firstName  LIKE '%'", Employee.class)
                .setParameter(startStr + "%", startStr)
                .getResultList()
                .forEach(employee -> {
                    System.out.printf("%s %s - %s - ($%.2f)", employee.getFirstName(), employee.getLastName(), employee.getJobTitle(), employee.getSalary());
                });
    }

    private void exTen() {
        entityManager.getTransaction().begin();
        entityManager
                .createQuery("UPDATE Employee e SET e.salary = e.salary * 1.12 WHERE e.department.id IN (1,2, 4, 11)")
                .executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.createQuery("SELECT e FROM Employee e WHERE e.department.id IN (1, 2, 4, 11)",Employee.class)
                .getResultList()
                .forEach(employee -> {
                    System.out.printf("%s %s ($%.2f)%n", employee.getFirstName(), employee.getLastName(), employee.getSalary());
                });
    }

    private void exNine() {

        entityManager.createQuery("SELECT p FROM Project p ORDER BY p.name",Project.class)
                .setMaxResults(10)
                .getResultList()
                .forEach(project -> {
                    System.out.printf
                            ("Project name: %s%n Project Description: %s%n" +
                                    " Project Start Date: %s%n Project End Date: %s%n"
                                    ,project.getName(), project.getDescription(), project.getStartDate(), project.getEndDate());
                });
    }

    private void exEight() throws IOException {
        System.out.println("Please enter employee id:");
        int employeeId = Integer.parseInt(bufferedReader.readLine());

        Employee employee = entityManager.find(Employee.class, employeeId);

        System.out.printf("%s %s - %s%n", employee.getFirstName(), employee.getLastName(), employee.getJobTitle());
        employee.getProjects().forEach(project -> System.out.println(project.getName()));
    }

    private void exSeven() {
        entityManager
                .createQuery("SELECT a FROM Address a ORDER BY a.employees.size DESC ", Address.class)
                .setMaxResults(10)
                .getResultList()
                .forEach(address -> {
                    System.out.printf("%s, %s - %d employees%n", address.getText(), address.getTown() == null ? "Unknown" : address.getTown().getName(), address.getEmployees().size());
                });
    }

    private void exSix() throws IOException {
        System.out.println("Please enter employee last name:");

        String lastName = bufferedReader.readLine();

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e WHERE e.lastName = :l_name", Employee.class)
                .setParameter("l_name", lastName)
                .getSingleResult();

        Address address = createAddress("Vitoshka 15");
        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();
    }

    private Address createAddress(String s) {
        Address address =new Address();
        address.setText(s);
        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();
        return address;
    }

    private void exFive() {

       entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.id = 6 ORDER BY e.salary", Employee.class)
                .getResultStream()
               .forEach(employee -> {
                   System.out.printf("%s %s from %s - $%.2f%n",
                           employee.getFirstName(),
                           employee.getLastName(),
                           employee.getDepartment().getName(),
                           employee.getSalary());
               });

    }

    private void exFour() {
         entityManager.createQuery("SELECT e.firstName FROM Employee e WHERE e.salary > 50000")
                 .getResultList()
                 .forEach(System.out::println);
    }

    private void exThree() throws IOException {
        System.out.println("Enter full name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];

        Long e = entityManager.createQuery("SELECT count(e) FROM Employee e WHERE e.firstName = :f_name AND e.lastName = :l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName)
                .getSingleResult();
        if(e == 0){
            System.out.println("No");
        }else {
            System.out.println("Yes");
        }


    }

    private void exTwo() {
        entityManager.getTransaction().begin();
       Query query = entityManager.createQuery("UPDATE Town t SET t.name = upper(t.name) WHERE length(t.name) <= 5 ");
        System.out.println(query.executeUpdate());
        entityManager.getTransaction().commit();
    }
}
