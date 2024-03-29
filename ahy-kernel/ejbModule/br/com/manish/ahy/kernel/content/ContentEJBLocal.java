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

import java.util.List;

import javax.ejb.Local;

import br.com.manish.ahy.kernel.BaseEJBLocal;

@Local
public interface ContentEJBLocal extends BaseEJBLocal {
    ContentResource getResource(ContentResource filter);
    Content getContent(Content filter);
    Content getContentById(Long id);
    String getParsedContent(ContentFilter filter);    
    Content save(Content content);
    void remove(Content content);
    ContentResource getResourceById(Long id);
    ContentResource saveResource(ContentResource contentRes);
    List<ContentResource> getResourcesList(ContentResource filter);
    ContentResource getFirstResource(Content filter);
    void removeContentResource(Long id);
    List<ContentResource> getResourcesListByLabel(ContentResource filter);
}
