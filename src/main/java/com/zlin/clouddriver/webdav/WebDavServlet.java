package com.zlin.clouddriver.webdav;

import org.apache.catalina.Globals;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.servlets.WebdavServlet;
import org.apache.catalina.webresources.DirResourceSet;

import javax.servlet.ServletException;
import java.io.File;

public class WebDavServlet extends WebdavServlet {
    private String baseDav = "/Users/wangzhulin/project/spring/clouddriver-webdav/src/main/resources/";

    @Override
    public void init() throws ServletException {
        WebResourceRoot webResourceRoot = (WebResourceRoot) getServletContext().getAttribute(Globals.RESOURCES_ATTR);
        File webFile = new File(baseDav);
        webResourceRoot.addPreResources(new DirResourceSet(webResourceRoot, "/",
                webFile.getAbsolutePath(), "/"));
        super.init();
    }
}
