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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSFUtil {
	private static final Log log = LogFactory.getLog(JSFUtil.class);

	public static final String SESSION_INFO = "br.com.manish.ahy.sessionInfo";

	public static void setSessionAttribute(String attribute, Object value) {
		log.debug("Setting session attribute: " + attribute + " - " + value);
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext()
				.getSession(true);
		session.setAttribute(attribute, value);
	}

	public static Object getSessionAttribute(String attribute) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext()
				.getSession(true);
		return session.getAttribute(attribute);
	}

	public static SessionInfo getSessionInfo() {
		SessionInfo ret = (SessionInfo) getSessionAttribute(SESSION_INFO);
		if (ret == null) {
			ret = new SessionInfo();
			setSessionAttribute(SESSION_INFO, ret);
		}
		return ret;
	}

	public static String formatMessage(String msg, Object... items) {
		String ret = "";
		
		//TODO: internationalization here.
		ret = msg;
		
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				ret = msg.replaceAll("\\{"+ i + "\\}", items[i].toString());
			}
		}
		return ret;
	}
}
