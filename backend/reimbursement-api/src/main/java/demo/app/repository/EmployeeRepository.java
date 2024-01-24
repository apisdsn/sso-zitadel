package demo.app.repository;

import demo.app.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    boolean existsByClientId(String clientId);

    Optional<Employee> findByClientId(String clientId);
}
