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
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.kernel.exception.OopsException;

public class BaseEJB implements BaseEJBLocal {
    private Log log = LogFactory.getLog(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Resource
    private SessionContext ctx;
    
    @Resource(mappedName = "java:ahy-cms-ds")
    private DataSource ds;

    
    
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
    
}
