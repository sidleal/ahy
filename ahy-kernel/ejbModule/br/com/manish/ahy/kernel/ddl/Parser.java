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
    	
package br.com.manish.ahy.kernel.ddl;

import javax.sql.DataSource;

import org.jdom.Element;

public interface Parser {

	Boolean verifyTableExistence(DataSource ds, String table);

	void createTable(DataSource ds, Element el);
	
	void createTables(DataSource ds, Element el);

	void dropTable(DataSource ds, Element el);

	void dropColumn(DataSource ds, Element el, String table);

	void addColumn(DataSource ds, Element el, String table);

	void alterColumn(DataSource ds, Element elFrom, Element elTo, String table);

	void insertRow(DataSource ds, Element el, String table);

	void deleteRow(DataSource ds, Element elFilter, String table);

	void updateRow(DataSource ds, Element elFilter, Element elDump, String table);

}
