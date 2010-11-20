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

package br.com.manish.ahy.web.util;

import java.io.Serializable;

import br.com.manish.ahy.kernel.security.User;

public class SessionInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String version;
	private User user;
	private String forwardAfterLogin;

	public String getUserEmail() {
	    String ret = null;
	    if (getUser() != null) {
	        ret = getUser().getEmail();
	    }
		return ret;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getForwardAfterLogin() {
        return forwardAfterLogin;
    }

    public void setForwardAfterLogin(String forwardAfterLogin) {
        this.forwardAfterLogin = forwardAfterLogin;
    }

}
