package com.es.phoneshop.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    private ExceptionHandler handler = new ExceptionHandler();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGetNoSuchElementException() throws ServletException, IOException {
        Exception exception = new NoSuchElementException();
        when(request.getAttribute(eq("jakarta.servlet.error.exception"))).thenReturn(exception);
        Integer statusCode = 500;
        when(request.getAttribute(eq("jakarta.servlet.error.status_code"))).thenReturn(statusCode);
        handler.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("errorMessage"), any());
    }

    @Test
    public void testDoGetError500() throws ServletException, IOException {
        Exception exception = new Exception();
        when(request.getAttribute(eq("jakarta.servlet.error.exception"))).thenReturn(exception);
        Integer statusCode = 500;
        when(request.getAttribute(eq("jakarta.servlet.error.status_code"))).thenReturn(statusCode);
        handler.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request, times(0)).setAttribute(eq("errorMessage"), any());
    }
}
