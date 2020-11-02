package com.phytel.patient.match.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;

@Component
class MyFilter extends GenericFilterBean {

	private static final String X_TRANSACTION_ID = "x-transaction-id";

	public static String correlationId;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		getCorrelation(request, response);
		addCorrelationToResponse(request, response);

		chain.doFilter(request, response);

	}

	public void getCorrelation(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Map<String, String> headers = Collections.list(httpRequest.getHeaderNames()).stream()
				.collect(Collectors.toMap(h -> h, httpRequest::getHeader));

		/* Check if transaction id exists in request header */
		if (headers.containsKey(X_TRANSACTION_ID) && !X_TRANSACTION_ID.isEmpty()) {
			correlationId = headers.get(X_TRANSACTION_ID);
		} else {
			correlationId = generateCorrelationId();
		}
		
		/* Add transaction id to each log */
		MDC.put(X_TRANSACTION_ID, correlationId );

		Tracing.newBuilder()
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, X_TRANSACTION_ID));
		ExtraFieldPropagation.set(X_TRANSACTION_ID, correlationId);
		
	}

	/* Generate correlation value */
	public String generateCorrelationId() {
		return String.format("ATOM~%s", UUID.randomUUID().toString());
		
	}

	/* For tracing return transaction id in response header */
	public void addCorrelationToResponse(ServletRequest request, ServletResponse response) {
		((HttpServletResponse) response).addHeader(X_TRANSACTION_ID, ExtraFieldPropagation.get(X_TRANSACTION_ID));
	}

}