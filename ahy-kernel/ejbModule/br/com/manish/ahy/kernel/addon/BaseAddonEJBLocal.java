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

import javax.ejb.Local;

import org.jdom.Element;

import br.com.manish.ahy.kernel.BaseEJBLocal;
import br.com.manish.ahy.kernel.Version;
import br.com.manish.ahy.kernel.content.ContentFilter;

@Local
public interface BaseAddonEJBLocal extends BaseEJBLocal {
    Version getVersion();
    String afterHtmlParser(String html, ContentFilter filter);
    Element getDatabaseCreateXML();
    Element getUpdateLogXML();
}
