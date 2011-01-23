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
package br.com.manish.ahy.kernel;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.security.SessionInfo;
import br.com.manish.ahy.kernel.security.SessionManagerEJBLocal;
import br.com.manish.ahy.kernel.security.User;

public class BaseEJB implements BaseEJBLocal {
    private Log log = LogFactory.getLog(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;
    
    @Resource(mappedName = "java:ahy-cms-ds")
    private DataSource ds;

    @EJB
    private SessionManagerEJBLocal sessionInfoEJB;

    private String currentUserSessionID = "";

    @Override
    public void setCurrentUserSessionID(String currentUserSessionID) {
        this.currentUserSessionID = currentUserSessionID;
    }

    protected String getCurrentUserSessionID() {
        return currentUserSessionID;
    }
    
    protected void validateSession() {
        validateSession(currentUserSessionID);
    }
    
    protected void validateSession(String sessionId) {
        if (!sessionInfoEJB.validateSession(sessionId)) {
            throw new OopsException("Session expired.");
        }
    }
    
    protected void startSession(User user) {
        String id = sessionInfoEJB.newSession(user);
        setCurrentUserSessionID(id);
    }
    
    protected User getLoggedUser() {
        if (currentUserSessionID == null || currentUserSessionID.equals("")) {
            throw new IllegalArgumentException("Invalid Session.");
        }
        User ret = null;
        SessionInfo sessionInfo = sessionInfoEJB.getInfo(currentUserSessionID);
        if (sessionInfo != null) {
            ret = sessionInfo.getUser();
        }
        return ret;
    }
    
    protected void logException(Throwable e, String message) {
        e.printStackTrace();
        log.error(message, e);
    }

    protected OopsException fireOopsException(Throwable e, String message) {
        logException(e, message);
        return new OopsException(e, message);
    }
    
    
    
	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public SessionContext getCtx() {
		return ctx;
	}

	public void setCtx(SessionContext ctx) {
		this.ctx = ctx;
	}

	public DataSource getDs() {
		return ds;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}
    
	protected Site getSite(String domain) {
	    return sessionInfoEJB.getSite(domain);
	}
	
}
