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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.manish.ahy.kernel.addon.AddonManagerEJBLocal;
import br.com.manish.ahy.kernel.addon.BaseAddonEJBLocal;
import br.com.manish.ahy.kernel.ddl.Parser;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.JPAUtil;

@Stateless
public class UpdateManagerEJB extends BaseEJB implements UpdateManagerEJBLocal {
    public static final Integer VERSION = 1;
    public static final Integer REVISION = 32;
    private static final String KERNEL = "kernel";

    private Parser parser = JPAUtil.getParser();

    @EJB
    private AddonManagerEJBLocal addonManagerEJB;
    
    @Override
    public String getVersion() {
        verifyDatabase(new Version(KERNEL, VERSION, REVISION, ""));
        
        for (BaseAddonEJBLocal addon : addonManagerEJB.getAddonList()) {
            verifyDatabase(addon.getVersion());
        }
        
        return VERSION + "." + REVISION;
    }

    @Override
    public void verifyDatabase(Version version) {
        try {
                        
            getLog().info("Verifying Database version. Module: " + version.getModule() + "(" + version.getMajor() + "." + version.getRevision() + ")");

            boolean tableExists = parser.verifyTableExistence(getDs(), "Version");

            if (!tableExists) { // new
                getLog().info("New installation detected, will create all tables.");
                create(version);

            } else {

                List<Version> versionList = getDatabaseVersionList(version.getModule());

                if (versionList.size() <= 0) {
                    getLog().info("New addon found or probably something goes wrong on first installation, trying to create tables.");
                    create(version);

                } else {
                    Version lastVersion = getDatabaseLastVersion(version.getModule());

                    if (lastVersion.getMajor() < version.getMajor()) {
                        getLog().info("Great! You have a new major version! Database outdated, applying updates.");
                        update(version);
                    } else {
                        if (lastVersion.getRevision() < version.getRevision()) {
                            getLog().info("Database outdated, applying updates.");
                            update(version);
                        } else if (lastVersion.getRevision() > version.getRevision()) {
                            throw new OopsException(
                                    "Hummm.. database version is newer than program version, something is wrong.");
                        } else {
                            getLog().info("Database updated, same version as program. Good Job!");
                        }
                    }
                }

            }

        } catch (Exception e) {
            throw fireOopsException(e, "Error on checking/updating database version. This is not good.");
        }
    }

    private List<Version> getDatabaseVersionList(String module) {
        Query query = getEm().createQuery("from Version where module = '" + module + "' order by major, revision desc");
        List<Version> list = query.getResultList();
        return list;
    }

    private Version getDatabaseLastVersion(String module) {
        Version ret = null;
        List<Version> list = getDatabaseVersionList(module);
        if (list.size() > 0) {
            ret = list.get(0);
        }
        return ret;
    }

    private void create(Version version) {
        try {
            getLog().info("Creating database - Begin.");

            SAXBuilder sb = new SAXBuilder();

            Element root;
            if (version.getModule().equals(KERNEL)) {
                URL url = UpdateManagerEJB.class.getResource("/resources/ddl/database-create.xml");
                Document doc = sb.build(url);
                root = (Element) doc.getRootElement();
            } else {
                root = addonManagerEJB.getAddon(version.getModule()).getDatabaseCreateXML();
            }
            parser.createTables(getDs(), root);

            version.setNote("First installation.");
            getEm().persist(version);

            getLog().info("Creating database - End.");

        } catch (Exception e) {
            throw fireOopsException(e, "Very bad: a error when creating database tables, sorry.");
        }

    }

    private void update(Version version) {
        try {

            getLog().info("Updating database - Begin.");

            Element root;
            if (version.getModule().equals(KERNEL)) {
                SAXBuilder sb = new SAXBuilder();
                URL url = UpdateManagerEJB.class.getResource("/resources/ddl/update-log.xml");
                Document doc = sb.build(url);
                root = doc.getRootElement();
            } else {
                root = addonManagerEJB.getAddon(version.getModule()).getUpdateLogXML();
            }

            Integer baseRevision = Integer.valueOf(root.getAttributeValue("baseRevision"));
            Version dataBaseVersion = getDatabaseLastVersion(version.getModule());

            if (dataBaseVersion.getRevision() < baseRevision) {
                throw new OopsException(
                        "Man, this database is so old. This version needs at least revision: {0}, but database revision is: {1}",
                        baseRevision, dataBaseVersion.getRevision());
            }

            for (Element elUpdate : (List<Element>) root.getChildren()) {
                Integer updRevision = Integer.valueOf(elUpdate.getAttributeValue("revision"));

                if (updRevision > version.getRevision()) {
                    break;
                }
                if (updRevision <= dataBaseVersion.getRevision()) {
                    continue;
                }

                getLog().info("Updating revision: " + updRevision);

                for (Element elTable : (List<Element>) elUpdate.getChildren()) {
                    updateTable(elTable);

                }
                
                version.setNote("Updated with honor!");
                getEm().persist(version);

            }
            getLog().info("Updating database - End.");

        } catch (Exception e) {
            throw fireOopsException(e, "Sorry to say that, but an error occurred when updating database.");
        }

    }

    private List<String> updateTable(Element elTable) {
        List<String> retList = new ArrayList<String>();

        String tableName = elTable.getAttributeValue("name");
        String operationType = elTable.getName();

        getLog().info("Table: " + tableName + " Operation: " + operationType);

        if (operationType.equals("create-table")) {
            parser.createTable(getDs(), elTable);

        } else if (operationType.equals("drop-table")) {
            parser.dropTable(getDs(), elTable);

        } else if (operationType.equals("alter-table")) {

            for (Element elUnit : (List<Element>) elTable.getChildren()) {

                String unitType = elUnit.getName();

                if (unitType.equals("drop-col")) {
                    parser.dropColumn(getDs(), elUnit, tableName);

                } else if (unitType.equals("create-col")) {
                    parser.addColumn(getDs(), elUnit, tableName);

                } else if (unitType.equals("alter-col")) {
                    Element elFrom = elUnit.getChild("from");
                    Element elTo = elUnit.getChild("to");
                    parser.alterColumn(getDs(), elFrom, elTo, tableName);

                } else if (unitType.equals("insert")) {
                    parser.insertRow(getDs(), elUnit, tableName);

                } else if (unitType.equals("delete")) {
                    Element elFilter = elUnit.getChild("filter");
                    parser.deleteRow(getDs(), elFilter, tableName);

                } else if (unitType.equals("update")) {
                    Element elFilter = elUnit.getChild("filter");
                    Element elDump = elUnit.getChild("to");
                    parser.updateRow(getDs(), elFilter, elDump, tableName);
                }
            }

        }

        return retList;
    }

}
