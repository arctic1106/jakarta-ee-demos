package org.acme.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/demo")
public class DemoServ extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String name = req.getParameter("name");
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");
        var rd = getServletContext();
        pw.println("Welcome PERRO " + name + " " + rd.getServerInfo());
    }
}