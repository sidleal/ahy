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

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.kernel.BaseEJBLocal;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.web.util.JSFUtil;
import br.com.manish.ahy.web.util.SessionInfo;

public abstract class BaseJSF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(this.getClass());

    protected <T extends BaseEJBLocal> T getEJB(Class<? extends BaseEJBLocal> localInterface) {
        T ret = EJBFactory.getInstance().getEJB(localInterface);
        return ret;
    }

    protected SessionInfo getSessionInfo() {
        return JSFUtil.getSessionInfo();
    }

    public Log getLog() {
        return log;
    }

    protected String formatMsg(String msg, Object... items) {
        return JSFUtil.formatMessage(msg, items);
    }

    protected String formatMsg(String msg) {
        return formatMsg(msg, (Object[]) null);
    }

    protected void logException(Exception e) {
        getLog().error(e);
        e.printStackTrace();
    }

    protected void treat(Exception e) {

        OopsException oe = null;
        if (e instanceof OopsException) {
            oe = (OopsException) e;
        } else if (e.getCause() instanceof OopsException) {
            oe = (OopsException) e.getCause();
        }

        String msg = "";
        if (oe != null) {
            msg = oe.getMessage();
            msg = formatMsg(msg, oe.getAdditionalInformation());
            log.warn("OopsException: " + oe.getMessage() + " - " + msg);
        } else {
            Throwable tmp = e;
            msg += tmp.getMessage() + "\n";
            while (tmp.getCause() != null) {
                msg += tmp.getMessage() + "\n";
                tmp = tmp.getCause();
            }
            logException(e);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));

    }

}
