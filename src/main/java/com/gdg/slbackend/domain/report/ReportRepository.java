package com.gdg.slbackend.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    long countByReportedUserId(Long reportedUserId);
}
