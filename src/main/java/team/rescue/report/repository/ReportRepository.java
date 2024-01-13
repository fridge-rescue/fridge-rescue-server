package team.rescue.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.rescue.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
