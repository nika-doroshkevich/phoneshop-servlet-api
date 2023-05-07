package com.es.phoneshop.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ExceptionHandler extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (exception instanceof NoSuchElementException) {
            var errorMessage = exception.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/WEB-INF/pages/errors/errorNoSuchElement.jsp").forward(request, response);
        } else if (statusCode == 500) {
            request.getRequestDispatcher("/WEB-INF/pages/errors/error-500.jsp").forward(request, response);
        }
    }
}
