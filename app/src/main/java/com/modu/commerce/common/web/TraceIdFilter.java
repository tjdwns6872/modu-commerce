package com.modu.commerce.common.web;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String TRACE_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
        String traceId = Optional.ofNullable(request.getHeader(TRACE_HEADER))
                                 .filter(s -> !s.isBlank())
                                 .orElse(UUID.randomUUID().toString());
        MDC.put(TRACE_ID, traceId);
            try {
              response.setHeader(TRACE_HEADER, traceId);
              chain.doFilter(request, response);
            } finally {
              MDC.remove(TRACE_ID);   // ★ 반드시 finally
            }
    }
    
}
