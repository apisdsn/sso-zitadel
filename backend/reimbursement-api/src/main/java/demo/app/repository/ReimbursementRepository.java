package demo.app.repository;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {
    Optional<Reimbursement> findFirstByEmployeeAndReimbursementId(Employee employee, Long id);

    List<Reimbursement> findByStatusFalse();
}
