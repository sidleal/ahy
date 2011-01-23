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
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.manish.ahy.kernel.BaseEJBLocal;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.TextUtil;

public class WSServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String ret = "";
        
        String domain = req.getServerName();
        String path = req.getPathInfo();
        
        Map <String, String> parameters = new HashMap<String, String>();
        
        parameters.put("domain", domain);
        
        for (String par : req.getParameterMap().keySet()) {
            parameters.put(par, req.getParameter(par));
        }
        
        String[] ejbTokens = path.split("/");
        String ejbName = TextUtil.capFirstLetter(ejbTokens[1]) + "EJB";
        String action = ejbTokens[2];
        
        BaseEJBLocal ejb = EJBFactory.getInstance().getEJB(ejbName);
        
        ejb.setCurrentUserSessionID(parameters.get("sessionid"));
        
        Map<String, String> retMap = new HashMap<String, String>();
        try {
            Method m = ejb.getClass().getMethod(action, Map.class);
            retMap = (Map<String, String>) m.invoke(ejb, parameters);
            
        } catch (Exception e) {
            if (e.getCause().getCause() instanceof OopsException) { //TODO: refactoring
                ret = e.getCause().getCause().getMessage();
            } else {
                ret = e.getMessage();
            }
            ret = "error=" + "Error: " + ret + "&";
            e.printStackTrace();
        }

        for (String key: retMap.keySet()) {
            ret += key + "=" + URLEncoder.encode(retMap.get(key), "UTF-8") + "&";
        }
        
        if (ret.length() > 1) {
            ret = ret.substring(0, ret.length()-1);
        }
        
        resp.setHeader("Content-disposition", "inline; filename=ahy.html");
        resp.setContentType("text/xml;charset=UTF-8");

        resp.getOutputStream().write(ret.getBytes());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String domain = req.getServerName();
        String path = req.getServletPath();

        resp.setHeader("Content-disposition", "inline; filename=ahy.html");
        resp.setContentType("text/html");

        resp.getOutputStream().write(("Not implemented yet. [" + domain + " - " + path + "]").getBytes());

    }

}
