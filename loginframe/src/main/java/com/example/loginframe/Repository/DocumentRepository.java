package com.example.loginframe.Repository;

import com.example.loginframe.Entity.Documents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Documents,Long> {
}
