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

package br.com.manish.ahy.kernel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.manish.ahy.kernel.ddl.Parser;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.DAOUtil;
import br.com.manish.ahy.kernel.util.JPAUtil;

@Stateless
public class UpdateManagerEJB extends BaseEJB implements UpdateManagerEJBLocal {
	public static final String VERSION_ALIAS = "Inception";
	public static final Integer REVISION = 24;

	private Parser parser = JPAUtil.getParser();

	@Override
	public String getVersion() {
		verifyDatabase();
		return VERSION_ALIAS + "." + REVISION;
	}

	private void verifyDatabase() {
		try {

			getLog().info("Verifying Database version.");

			boolean tableExists = parser.verifyTableExistence(getDs(),
					"Version");

			if (!tableExists) { // new
				getLog().info(
						"New installation detected, will create all tables.");
				create();

			} else {

				List<Version> versionList = getDatabaseVersionList();

				if (versionList.size() <= 0) {
					getLog().info(
							"Probably something goes wrong on first installation, trying to recreate all tables.");
					create();

				} else {
					Version lastVersion = getDatabaseLastVersion();
					if (lastVersion.getRevision() < REVISION) {
						getLog().info("Database outdated, applying updates.");
						update();
					} else if (lastVersion.getRevision() > REVISION) {
						throw new OopsException(
								"Hummm.. database version is newer than program version, something is wrong.");
					} else {
						getLog().info(
								"Database updated, same version as program. Good Job!");
					}

				}

			}

		} catch (Exception e) {
			throw fireOopsException(e,
					"Error on checking/updating database version. This is not good.");
		}
	}

	private List<Version> getDatabaseVersionList() {
		Query query = getEm()
				.createQuery("from Version order by revision desc");
		List<Version> list = query.getResultList();
		return list;
	}

	private Version getDatabaseLastVersion() {
		Version ret = null;
		List<Version> list = getDatabaseVersionList();
		if (list.size() > 0) {
			ret = list.get(0);
		}
		return ret;
	}

	private void create() {
		try {
			getLog().info("Creating database - Begin.");

			SAXBuilder sb = new SAXBuilder();

			URL url = UpdateManagerEJB.class
					.getResource("/resources/ddl/database-create.xml");
			Document doc = sb.build(url);

			Element root = (Element) doc.getRootElement();

			List<String> createSQLList = parser.createTable(getDs(), root);

			for (String command : createSQLList) {
				DAOUtil.executeSQLCommand(getDs(), command);
			}

			getEm().persist(new Version(REVISION, "First database creation"));

			getLog().info("Creating database - End.");

		} catch (Exception e) {
			throw fireOopsException(e,
					"Very bad: a error when creating database tables, sorry.");
		}

	}

	private void update() {
		try {

			getLog().info("Updating database - Begin.");

			SAXBuilder sb = new SAXBuilder();
			URL url = UpdateManagerEJB.class
					.getResource("/resources/ddl/update-log.xml");
			Document doc = sb.build(url);

			Integer baseRevision = Integer.valueOf(doc.getRootElement()
					.getAttributeValue("baseRevision"));
			Version dataBaseVersion = getDatabaseLastVersion();

			if (dataBaseVersion.getRevision() < baseRevision) {
				throw new OopsException(
						"Man, this database is so old. This version needs at least revision: {0}, but database revision is: {1}",
						baseRevision, dataBaseVersion.getRevision());
			}

			for (Element elUpdate : (List<Element>) doc.getRootElement()
					.getChildren()) {
				Integer updRevision = Integer.valueOf(elUpdate
						.getAttributeValue("revision"));

				if (updRevision > REVISION) {
					break;
				}
				if (updRevision <= dataBaseVersion.getRevision()) {
					continue;
				}

				getLog().info("Updating revisio: " + updRevision);

				List<String> updateSQLList = new ArrayList<String>();

				for (Element elTable : (List<Element>) elUpdate.getChildren()) {
					updateSQLList.addAll(updateTable(elTable));

				}

				for (String command : updateSQLList) {
					DAOUtil.executeSQLCommand(getDs(), command);
				}

				getEm().persist(new Version(REVISION, "Updated with honor!"));

			}
			getLog().info("Updating database - End.");

		} catch (Exception e) {
			throw fireOopsException(e,
					"Sorry to say that, but a error ocurred when updating database.");
		}

	}

	private List<String> updateTable(Element elTable) {
		List<String> retList = new ArrayList<String>();

		String tableName = elTable.getAttributeValue("name");
		String operationType = elTable.getName();

		getLog().debug("Table: " + tableName + " Operation: " + operationType);

		if (operationType.equals("create-table")) {
			retList.addAll(parser.createTable(getDs(), elTable));

		} else if (operationType.equals("drop-table")) {
			retList.add(parser.dropTable(elTable));

		} else if (operationType.equals("alter-table")) {

			for (Element elUnit : (List<Element>) elTable.getChildren()) {

				String unitType = elUnit.getName();

				if (unitType.equals("drop-col")) {
					retList.add(parser.dropColumn(elUnit, tableName));

				} else if (unitType.equals("create-col")) {
					retList.add(parser.addColumn(elUnit, tableName));

				} else if (unitType.equals("alter-col")) {
					Element elFrom = elUnit.getChild("from");
					Element elTo = elUnit.getChild("to");
					retList.add(parser.alterColumn(elFrom, elTo, tableName));

				} else if (unitType.equals("insert")) {
					retList.add(parser.createInsert(elUnit, tableName));

				} else if (unitType.equals("delete")) {
					Element elFilter = elUnit.getChild("filter");
					retList.add(parser.createDelete(elFilter, tableName));

				} else if (unitType.equals("update")) {
					Element elFilter = elUnit.getChild("filter");
					Element elDump = elUnit.getChild("to");
					retList.add(parser
							.createUpdate(elFilter, elDump, tableName));
				}
			}

		}

		return retList;
	}

}
