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

package br.com.manish.ahy.kernel.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.manish.ahy.kernel.Site;
import br.com.manish.ahy.kernel.util.HashUtil;

@Singleton
public class SessionManagerEJB implements SessionManagerEJBLocal {
    private Map<String, SessionInfo> sessionMap = new HashMap<String, SessionInfo>();

    private Map<String, Object> sessionBufferMap = new HashMap<String, Object>();

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public String newSession(User user) {
        SessionInfo info = new SessionInfo();
        info.setUser(user);
        info.setStart(new Date());
        info.setLastAccess(info.getStart());

        String id = HashUtil.getHash(info.getUser().getEmail() + info.getStart());
        info.setId(id);
        sessionMap.put(id, info);

        return id;
    }

    @Override
    public void endSession(String id) {
        SessionInfo info = sessionMap.get(id);
        if (info != null) {
            sessionMap.remove(id);
        }
    }

    @Override
    public Boolean validateSession(String id) {
        Boolean ret = false;
        SessionInfo info = sessionMap.get(id);
        if (info != null) {
            info.setLastAccess(new Date());
            ret = true;
        }
        return ret;
    }

    @Override
    public SessionInfo getInfo(String id) {
        return sessionMap.get(id);
    }

    @Override
    public Site getSite(String domain) {
        Site ret = null;
        
        String sql = "from Site where domain = '" + domain + "'";
        ret = (Site) em.createQuery(sql).getSingleResult();
        
        return ret;
    }
    
    @Override
    public void setSessionAttribute(String name, Object value) {
        sessionBufferMap.put(name, value);
    }
    
    @Override
    public Object getSessionAttribute(String name) {
        return sessionBufferMap.get(name);
    }
}
