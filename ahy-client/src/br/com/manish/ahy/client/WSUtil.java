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

import br.com.manish.ahy.client.exception.OopsException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WSUtil {

    public static String callStringWS(String wsName, String action, Map<String, String> parameters) {
        String ret = "";
        try {
            String post = "";
            post += "sessionid=" + SessionInfo.getInstance().getSessionId() + "&";
            for (String key: parameters.keySet()) {
                post += key + "=" + URLEncoder.encode(parameters.get(key),"UTF-8") + "&";
            }

            if (post.length() > 1) {
                post = post.substring(0, post.length()-1);
            }

            HTTPContent content = HTTPUtil.getHTTPContent("http://" 
                    + SessionInfo.getInstance().getDomain() + "/ws/" + wsName + "/" + action, post);
            if (content.getSucess()) {
                ret = content.getContentAsString();
            }
            
        } catch (Exception e) {
            throw new OopsException(e, "Error when calling String WS: {0}", wsName);
        }

        return ret;
    }

    public static Map<String, String> callMapWS(String wsName, String action, Map<String, String> parameters) {
        Map<String, String> ret = new HashMap<String, String>();
        try {
            String content = callStringWS(wsName, action, parameters);

            String[] retTokens = content.split("&");
            for (String item: retTokens) {
                String[] itemToken = item.split("=");
                if (item.indexOf("=") > 0) {
                    ret.put(itemToken[0], URLDecoder.decode(itemToken[1].trim(), "UTF-8"));
                } else {
                    ret.put("error", "Blank response from httpcontent.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new OopsException(e, "Error when calling Map WS: {0}", wsName);
        }
        
        return ret;
    }

}
