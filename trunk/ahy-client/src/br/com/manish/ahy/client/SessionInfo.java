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

package br.com.manish.ahy.client;

import java.util.HashMap;
import java.util.Map;

public class SessionInfo {

    private static SessionInfo me;

    private SessionInfo() {
    }

    public static SessionInfo getInstance() {
        if (me == null) {
            me = new SessionInfo();
        }
        return me;
    }

    private String domain;
    private String sessionId = "";
    private String userLogin;
    private String userName;

    private Map<String, Object> imageCache = new HashMap<String, Object>();

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getImageCache() {
        return imageCache;
    }

    public void setImageCache(Map<String, Object> imageCache) {
        this.imageCache = imageCache;
    }

}
