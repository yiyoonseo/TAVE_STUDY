package com.example.dustbank.repository;

import com.example.dustbank.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByParent_ParentIdOrderByChildIdAsc(String parentId);
}
