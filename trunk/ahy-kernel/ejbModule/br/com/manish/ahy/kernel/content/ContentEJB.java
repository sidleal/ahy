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
package br.com.manish.ahy.kernel.content;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.manish.ahy.kernel.BaseEJB;
import br.com.manish.ahy.kernel.UpdateManagerEJB;

@Stateless
public class ContentEJB extends BaseEJB implements ContentEJBLocal {

    @Override
    public ContentResource getResource(ContentResource filter) {
        ContentResource ret = new ContentResource();

        try {
            getLog().debug(
                    "getImage: Content [" + filter.getContent().getShortcut() + "], Resource: [" + filter.getShortcut()
                            + "]");

            String sql = "select cr from ContentResource cr inner join cr.content c";
            sql += " where cr.shortcut = '" + filter.getShortcut() + "' ";
            sql += " and c.shortcut = '" + filter.getContent().getShortcut() + "'";

            Query query = getEm().createQuery(sql);

            ret = (ContentResource) query.getSingleResult();

        } catch (NoResultException nre) {
            getLog().warn("Image not found: [" + filter.getContent().getShortcut() + "/" + filter.getShortcut() + "]");
            ret = null; // TODO: return a default image.

        } catch (Exception e) {
            throw fireOopsException(e, "Error when retrieving image.");
        }

        return ret;
    }

    @Override
    public Content getContent(Content filter) {
        Content ret = new Content();

        try {
            getLog().debug("getContent: [" + filter.getShortcut() + "]");

            String sql = "select c from Content c";
            sql += " where c.shortcut = '" + filter.getShortcut() + "'";

            Query query = getEm().createQuery(sql);

            ret = (Content) query.getSingleResult();

        } catch (NoResultException nre) {
            getLog().warn("Content not found: [" + filter.getShortcut() + "]");
            ret = null; // TODO: return a default error page.

        } catch (Exception e) {
            throw fireOopsException(e, "Error when retrieving content.");
        }

        return ret;
    }

    @Override
    public String getParsedContent(Content filter) {
        String ret = "";
        Content content = getContent(filter);
        
        if (content != null) {
            ret = content.getText();

            ret = parseResourcesPath(ret, content.getShortcut());
            
            if (!content.getShortcut().startsWith("themes")) {//TODO: config theme.. this is just a test, have to create a class to parse.
                filter.setShortcut("themes/default");
                Content theme = getContent(filter);
                String themeRet = theme.getText();

                themeRet = parseResourcesPath(themeRet, theme.getShortcut());

                themeRet = themeRet.replaceAll("#\\{content\\}", ret);
                
                ret = themeRet;
            }

            ret = ret.replaceAll("#\\{version\\}", UpdateManagerEJB.VERSION + "." + UpdateManagerEJB.REVISION);
            ret = ret.replaceAll("#\\{windowTitle\\}", content.getTitle());
            
            
        }
        
        return ret;
    }

    private String parseResourcesPath(String text, String shortcut) {
        String ret = text;

        int index = ret.indexOf("#{res:");
        while (index > 0) {
            String res = ret.substring(index);
            res = res.substring(6, res.indexOf("}"));
            String regex = "#\\{res:" + res + "\\}";
            res = shortcut + "/" + res;
            ret = ret.replaceAll(regex, "#{contextPath}/" + res);
            index = ret.indexOf("#{res:", index);
        }
        return ret;
    }

}
