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
package br.com.manish.ahy.menu;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jdom.Element;

import br.com.manish.ahy.kernel.Version;
import br.com.manish.ahy.kernel.addon.BaseAddonEJB;

@Stateless
public class MenuEJB extends BaseAddonEJB implements MenuEJBLocal {
    public static final Integer VERSION = 1;
    public static final Integer REVISION = 30;
    private static final String MENU = "menu";

    
    @Override
    public String afterHtmlParser(String html) {
        
        Menu main = getMainMenu("Menu");
        
        Menu filter = new Menu();
        filter.setSuperMenu(main);
        
        List<Menu> subList = getList(filter);
        
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>" + main.getName() + "</h3>");
        sb.append("<ul>");
        for (Menu item: subList) {
            sb.append("<li><a href='#{contextPath}" + item.getPath() + "'>" + item.getName() + "</a></li>");
        }
        sb.append("</ul>");
        
        return html.replaceAll("#\\{menu\\}", sb.toString());
    }

    
    public List<Menu> getList(Menu filter) {
        List<Menu> ret = new ArrayList<Menu>();

        try {
            getLog().debug("getList: Menu.");

            String sql = "select m from Menu m ";
            if (filter.getSuperMenu() != null) {
                sql += " where m.superMenu.id = " + filter.getSuperMenu().getId();
            }
            sql += " order by m.position";

            Query query = getEm().createQuery(sql);

            ret = (List<Menu>) query.getResultList();

        } catch (Exception e) {
            throw fireOopsException(e, "Error when retrieving the menu list.");
        }

        return ret;
    }

    private Menu getMainMenu(String name) {
        Menu ret = null;
        try {
            String sql = "select m from Menu m where m.superMenu = null and m.name = '" + name + "'";
            Query query = getEm().createQuery(sql);
            ret = (Menu) query.getSingleResult();

        } catch (Exception e) {
            throw fireOopsException(e, "Error when retrieving main menu.");
        }
        return ret;
    }
    
    @Override
    public Element getDatabaseCreateXML() {
        return readXML("/resources/ddl/menu-database-create.xml");
    }

    @Override
    public Element getUpdateLogXML() {
        return readXML("/resources/ddl/menu-update-log.xml");
    }

    @Override
    public Version getVersion() {
        return new Version(MENU, VERSION, REVISION, "");
    }

    
}
