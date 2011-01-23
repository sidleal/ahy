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

package br.com.manish.ahy.fxadmin;

import br.com.manish.ahy.client.SessionInfo;
import br.com.manish.ahy.client.WSUtil;
import java.util.HashMap;
import java.util.Map;

public class LoginModel {

    public Map<String, String> authenticate(String login, String passwd) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("login", login);
            parameters.put("password", passwd);

            ret = WSUtil.callMapWS("authenticator", "authenticate", parameters);

            SessionInfo.getInstance().setSessionId(ret.get("sessionid"));

        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

}
