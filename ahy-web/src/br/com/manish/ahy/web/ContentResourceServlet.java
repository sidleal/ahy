//  Ahy - A pure java CMS.
//  Copyright (C) 2010 Sidney Leal (manish.com.br)
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package br.com.manish.ahy.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.manish.ahy.kernel.Site;
import br.com.manish.ahy.kernel.content.Content;
import br.com.manish.ahy.kernel.content.ContentEJBLocal;
import br.com.manish.ahy.kernel.content.ContentResource;
import br.com.manish.ahy.kernel.util.JPAUtil;

public class ContentResourceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getServletPath();
        path = path.substring(1, path.length());
        
        String domain = req.getServerName();
        
        if (path.indexOf("/") < 1) {
            throw new ServletException("Wrong resource path. [" + path + "]");
        }

        String contentShortCut = path.substring(0, path.lastIndexOf("/"));
        String resourceShortCut = path.substring(path.lastIndexOf("/")+1, path.length());
        
        ContentResource filter = new ContentResource();
        filter.setContent(new Content());
        
        filter.getContent().setShortcut(contentShortCut);
        filter.getContent().setSite(new Site());
        filter.getContent().getSite().setDomain(domain);
        filter.setShortcut(resourceShortCut);
        
        ContentEJBLocal ejb = EJBFactory.getInstance().getEJB(ContentEJBLocal.class);

        ContentResource ret = ejb.getResource(filter);
        
        if (ret != null) {
            resp.setHeader("Content-disposition", "inline; filename=" + ret.getShortcut());
            long expires = new Date().getTime() + 1000*60*60*6; // six hours
            resp.setDateHeader("Expires", expires);
            resp.setContentType(ret.getType());
    
            byte[] file = JPAUtil.blobToBytes(ret.getData());
            resp.getOutputStream().write(file);
        }

    }

}
