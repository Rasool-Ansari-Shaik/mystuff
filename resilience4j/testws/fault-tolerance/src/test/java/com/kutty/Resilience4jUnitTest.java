package com.kutty;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

public class Resilience4jUnitTest {
	
	interface RemoteService {

        int process(int i);
    }

    private RemoteService service;

    @Before
    public void setUp() {
        service = mock(RemoteService.class);
    }
    
    @Test
    public void whenCircuitBreakerIsUsed_thenItWorksAsExpected() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                                                          // Percentage of failures to start short-circuit
                                                          .failureRateThreshold(20)
                                                          // Min number of call attempts
                                                          .ringBufferSizeInClosedState(5)
                                                          .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("my");
        Function<Integer, Integer> decorated = CircuitBreaker.decorateFunction(circuitBreaker, service::process);

        when(service.process(anyInt())).thenThrow(new RuntimeException());

        for (int i = 0; i < 10; i++) {
            try {
                decorated.apply(i);
            } catch (Exception ignore) {
            }
        }

        verify(service, times(6)).process(any(Integer.class));
    }


}
