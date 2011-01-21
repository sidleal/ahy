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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("nome", "João da Silva");

        Map<String, String> ret = WSUtil.callMapWS("ahycms.local:8080", "menu/pesquisar", parameters);

        System.out.println(ret.get("nome"));

        //HTTPContent ret = HTTPUtil.getHTTPContent("http://www.manish.com.br", urlParameters);
        //HTTPContent ret = HTTPUtil.getHTTPContent("http://ahycms.local:8080/ws/agoravai", urlParameters);
        //HTTPContent ret = HTTPUtil.getHTTPContent("http://ahycms.local:8080/ws/agoravai");



    }

}
