package com.arcticsoft;

import jakarta.servlet.*;
import java.io.IOException;

public class ServletFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        String nombre = request.getParameter("nombre");
        if ((nombre == null) || nombre.equals("filtrame"))
            request.setAttribute("texto", ", esta petición ha sido filtrada y/o los parámetros se han ignorado :)<br>");
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }
}