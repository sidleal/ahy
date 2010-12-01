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

import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.manish.ahy.kernel.security.AuthenticatorEJBLocal;
import br.com.manish.ahy.kernel.security.User;

@ManagedBean
@SessionScoped
public class LoginJSF extends BaseJSF {
	private static final long serialVersionUID = 1L;

    private User user = new User();
    
    public String getTitle() {
        return formatMsg("Login");
    }
    public String getEmailLabel() {
        return formatMsg("E-Mail");
    }
    public String getPasswordLabel() {
        return formatMsg("Password");
    }
    public String getSendLabel() {
        return formatMsg("Send");
    }
    
    public String authenticate() {
        try {
            AuthenticatorEJBLocal ejb = getEJB(AuthenticatorEJBLocal.class);
            user = ejb.authenticate(user);
    
            if (user != null) {
                getSessionInfo().setUser(user);
                redirectToAfterLoginPage();
            }
        } catch (Exception e) {
            treat(e);
        }
        
        return "";
    }
    
    private void redirectToAfterLoginPage() {
        ViewHandler vh = FacesContext.getCurrentInstance().getApplication().getViewHandler();
        UIViewRoot newRoot = vh.createView(FacesContext.getCurrentInstance(), "/portal.xhtml");
        if (getSessionInfo().getForwardAfterLogin() != null) {
            newRoot = vh.createView(FacesContext.getCurrentInstance(), getSessionInfo().getForwardAfterLogin());
        }
        FacesContext.getCurrentInstance().setViewRoot(newRoot);
    }
    
    public String logoff() {

        try {
            ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) ectx.getSession(false);
            session.invalidate();

        } catch (Exception e) {
            treat(e);
        }

        return "main";
    }
    
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
	
}
