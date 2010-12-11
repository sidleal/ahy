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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.manish.ahy.kernel.UpdateManagerEJBLocal;
import br.com.manish.ahy.kernel.content.Content;
import br.com.manish.ahy.kernel.content.ContentEJBLocal;
import br.com.manish.ahy.web.util.SessionInfo;
import br.com.manish.ahy.web.util.WebUtil;

public class ContentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        verifyVersionAndDatabase(req);
        
        String path = req.getServletPath();
        path = path.substring(1, path.length() - 4);

        Content filter = new Content();
        filter.setShortcut(path);
        
        ContentEJBLocal ejb = EJBFactory.getInstance().getEJB(ContentEJBLocal.class);

        String ret = ejb.getParsedContent(filter);
        
        ret = ret.replaceAll("#\\{contextPath\\}", req.getContextPath());

        resp.setHeader("Content-disposition", "inline; filename=ahy.html");
        resp.setContentType("text/html");

        resp.getOutputStream().write(ret.getBytes());

    }

    private void verifyVersionAndDatabase(HttpServletRequest req) {
        SessionInfo sessInfo = WebUtil.getSessionInfo(req);
        if (sessInfo.getVersion() == null || sessInfo.getVersion().equals("")) {
            UpdateManagerEJBLocal ejb = EJBFactory.getInstance().getEJB(UpdateManagerEJBLocal.class);
            String version = ejb.getVersion();
            sessInfo.setVersion(version);
        }
    }

}
