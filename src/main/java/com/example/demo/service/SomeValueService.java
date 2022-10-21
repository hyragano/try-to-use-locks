package com.example.demo.service;

import com.example.demo.model.SomeValue;
import com.example.demo.model.Status;
import com.example.demo.repository.SomeValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SomeValueService {

    private final SomeValueRepository someValueRepository;
    private final PlatformTransactionManager transactionManager;

    private final EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void init() {
        someValueRepository.saveAll(List.of(
                SomeValue.builder()
                        .name("some name")
                        .status(Status.PENDING)
                        .build(),
                SomeValue.builder()
                        .name("hertam")
                        .status(Status.PENDING)
                        .build(),
                SomeValue.builder()
                        .name("some name")
                        .status(Status.PENDING)
                        .build(),
                SomeValue.builder()
                        .name("some name")
                        .status(Status.PENDING)
                        .build(),
                SomeValue.builder()
                        .name("some name")
                        .status(Status.PENDING)
                        .build(),
                SomeValue.builder()
                        .name("some name")
                        .status(Status.PENDING)
                        .build()
        ));
    }


    @EventListener
    public void onEvent(ContextRefreshedEvent event) {
        var transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var transactionTemplate = new TransactionTemplate(transactionManager, transactionDefinition);
        while (someValueRepository.existsByStatus(Status.PENDING)) {
            transactionTemplate.executeWithoutResult(status -> {
                someValueRepository.findAllForProcessingEnties(PageRequest.of(0, 4))
                        .forEach(someValue -> {
                            try {
                                transactionTemplate.executeWithoutResult(status1 -> {
                                    someValue.setStatus(Status.PROCESSED);
                                    if (someValue.getName().equals("hertam")) {
                                        throw new RuntimeException("hertam");
                                    }
                                });
                            } catch (Throwable e) {
                                log.info("shit");
                            }
                        });
            });
        }

    }
}
