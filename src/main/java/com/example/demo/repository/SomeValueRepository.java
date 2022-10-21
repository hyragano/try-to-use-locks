package com.example.demo.repository;

import com.example.demo.model.SomeValue;
import com.example.demo.model.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import javax.persistence.LockModeType;
import java.util.List;

public interface SomeValueRepository extends JpaRepository<SomeValue, Long> {

    @Query(nativeQuery = true, value = "select id from some_value where status = 'PENDING' for update nowait")
    List<Long> findAllForProcessing(Pageable pageable);
    @Query(nativeQuery = true, value = "select * from some_value where status = 'PENDING' for update nowait")
    List<SomeValue> findAllForProcessingEnties(Pageable pageable);

    boolean existsByStatus(Status status);
}
