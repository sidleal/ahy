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

import java.util.HashSet;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import br.com.manish.ahy.web.util.JSFUtil;
import br.com.manish.ahy.web.util.SessionInfo;

public class PermissionChecker implements PhaseListener {
    private static final long serialVersionUID = 1L;

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        ExternalContext ext = context.getExternalContext();
        HttpSession session = (HttpSession) ext.getSession(false);
        boolean newSession = (session == null) || (session.isNew());
        boolean postback = !ext.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;
        if (timedout) {
            Application app = context.getApplication();
            ViewHandler viewHandler = app.getViewHandler();
            UIViewRoot view = viewHandler.createView(context, "/login.xhtml");
            context.setViewRoot(view);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Your session has expired, please re-login.", null));
            context.renderResponse();
            try {
                viewHandler.renderView(context, view);
                context.responseComplete();
            } catch (Throwable t) {
                throw new FacesException("Session timed out", t);
            }
        }        
    }

    public void afterPhase(PhaseEvent event) {
        FacesContext fc = event.getFacesContext();

//        if (WebXml.getInstance(fc).getFacesResourceKey((HttpServletRequest) fc.getExternalContext().getRequest()) != null) {
//            return;
//        }

        Set<String> publicPages = new HashSet<String>();
        publicPages.add("login.xhtml");
        publicPages.add("main.xhtml");

        boolean publicPage = false;
        for (String page : publicPages) {
            if (fc.getViewRoot().getViewId().lastIndexOf(page) > -1) {
                publicPage = true;
            }
        }
        
        System.out.println("----" + fc.getViewRoot().getViewId());
        System.out.println(JSFUtil.getSessionInfo());
        System.out.println(JSFUtil.getSessionInfo().getUserEmail());
        
        if (!publicPage) {
            SessionInfo info = JSFUtil.getSessionInfo();

            if (info == null || info.getUserEmail() == null) {
                JSFUtil.getSessionInfo().setForwardAfterLogin(fc.getViewRoot().getViewId());
                NavigationHandler nh = fc.getApplication().getNavigationHandler();
                nh.handleNavigation(fc, null, "login");
            }

        }
    }

}
