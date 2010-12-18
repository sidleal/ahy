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
package br.com.manish.ahy.kernel.addon;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.manish.ahy.kernel.BaseEJB;
import br.com.manish.ahy.kernel.UpdateManagerEJB;

public abstract class BaseAddonEJB extends BaseEJB implements BaseAddonEJBLocal {

    protected Element readXML(String path) {
        Element root = null;
        try {
            SAXBuilder sb = new SAXBuilder();
            URL url = UpdateManagerEJB.class.getResource(path);
            Document doc = sb.build(url);
            root = (Element) doc.getRootElement();
        } catch (Exception e) {
            throw fireOopsException(e, "Error when opening ddl xml's.");
        }
        return root;
    }
    
}
