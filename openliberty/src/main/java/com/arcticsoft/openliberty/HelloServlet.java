package com.arcticsoft.openliberty;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/servlet")
public class HelloServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		pw.println("HOLA DESDE MY SERVLET! <br/>");
		pw.println("<button onclick=\"window.location.href='" + req.getContextPath() + "/index.html';\">Inicio</button>");
		pw.close();
	}
}
