package com.zlin.clouddriver.webdav;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebDavDefaultServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String method = req.getMethod();
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
//            try {
//
//            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
