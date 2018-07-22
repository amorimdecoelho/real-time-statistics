package com.n26.realtimestatistics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController
        extends ResponseEntityExceptionHandler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(value
            = { Throwable.class })
    protected ResponseEntity<Object> handleError(
            RuntimeException ex, WebRequest request) {
        final String msg = "Error processing the request";
        logger.error(msg, ex);
        return handleExceptionInternal(ex, msg,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value
            = { JsonProcessingException.class, HttpMessageConversionException.class })
    protected ResponseEntity<Object> handleJsonParsing(
            RuntimeException ex, WebRequest request) {
        final String msg = "Error parsing the transaction";
        logger.warn(msg, ex);
        return handleExceptionInternal(ex, msg,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
