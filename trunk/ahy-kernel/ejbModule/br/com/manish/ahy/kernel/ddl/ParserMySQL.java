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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.DAOUtil;
import br.com.manish.ahy.kernel.util.TextUtil;

public class ParserMySQL implements Parser {
    private static Log log = LogFactory.getLog(ParserMySQL.class);

    private static Map<String, String> dataTypes = new HashMap<String, String>();

    static {
        dataTypes.put("java.lang.String", "VARCHAR");
        dataTypes.put("java.lang.Integer", "INT");
        dataTypes.put("java.lang.Long", "BIGINT");
        dataTypes.put("java.util.Date", "DATETIME");
        dataTypes.put("java.sql.Blob", "MEDIUMBLOB");
        dataTypes.put("java.math.BigDecimal", "DECIMAL");
        dataTypes.put("java.lang.String-Text", "TEXT");
        dataTypes.put("java.lang.Boolean", "BOOLEAN");
    }

    @Override
    public Boolean verifyTableExistence(DataSource ds, String table) {
        String sql = "SHOW TABLES LIKE '" + table + "'";
        DAOUtil dao = new DAOUtil(ds);
        String result = dao.executeSQLQuerySingleResult(sql);
        dao.releaseConnection();
        return result != null;
    }

    @Override
    public void createTables(DataSource ds, Element el) {

        log.debug("createTables");

        for (Element table : (List<Element>) el.getChildren()) {
            createTable(ds, table);
        }
    }

    @Override
    public void createTable(DataSource ds, Element table) {

        DAOUtil dao = new DAOUtil(ds);

        if (!verifyTableExistence(ds, table.getAttributeValue("name"))) {

            Map<String, String> typesMap = new HashMap<String, String>();
            String sql = "";
            sql += "CREATE TABLE " + table.getAttributeValue("name") + " (";
            for (Element col : (List<Element>) table.getChildren()) {
                if (col.getName().equals("col")) {
                    sql += createColumn(col, table.getAttributeValue("name"));
                    typesMap.put(col.getAttributeValue("name"), col.getAttributeValue("type"));
                }
            }
            sql = sql.substring(0, sql.length() - 2);
            sql += ") ENGINE = InnoDB;";

            dao.executeSQLCommand(sql);

            for (Element insert : (List<Element>) table.getChildren()) {
                if (insert.getName().equals("insert")) {
                    for (Element col : (List<Element>) insert.getChildren()) {
                        if (typesMap.get(col.getAttributeValue("key")) == null) {
                            throw new OopsException(
                                    "It seems that you have a spelling error on xml, can't find column [ "
                                            + col.getAttributeValue("key") + " ] in ["
                                            + table.getAttributeValue("name") + "]");
                        }
                        col.setAttribute("type", typesMap.get(col.getAttributeValue("key")));
                    }
                    insertRow(ds, insert, table.getAttributeValue("name"));
                }
            }
        }
        dao.releaseConnection();
    }

    @Override
    public void insertRow(DataSource ds, Element el, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "INSERT INTO " + table + " (";
        String values = "";

        for (Element col : (List<Element>) el.getChildren()) {
            sql += col.getAttributeValue("key") + ", ";
            values += "?, ";
        }

        sql = sql.substring(0, sql.length() - 2);
        values = values.substring(0, values.length() - 2);

        sql += ") VALUES (" + values + ");";

        dao.prepareStatement(sql);
        
        for (Element col : (List<Element>) el.getChildren()) {
            String type = col.getAttributeValue("type");
            String value = getColValue(col);
            dao.addPreparedValue(value, type);
        }
        
        dao.executePreparedStatement();

        dao.releaseConnection();
    }

    @Override
    public void deleteRow(DataSource ds, Element elFilter, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "DELETE FROM " + table;
        sql += " WHERE ";
        for (Element col : (List<Element>) elFilter.getChildren()) {
            sql += col.getAttributeValue("key") + " = ? AND ";
        }
        sql = sql.substring(0, sql.length() - 5);
        sql += ";";

        dao.prepareStatement(sql);

        for (Element col : (List<Element>) elFilter.getChildren()) {
            String type = col.getAttributeValue("type");
            String value = getColValue(col);
            dao.addPreparedValue(value, type);
        }
        
        dao.releaseConnection();
    }

    @Override
    public void updateRow(DataSource ds, Element elFilter, Element elDump, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "UPDATE " + table + " SET ";
        for (Element col : (List<Element>) elDump.getChildren()) {
            sql += col.getAttributeValue("key") + " = ?, ";
        }
        sql = sql.substring(0, sql.length() - 2);

        if (elFilter.getChildren().size() > 0) {
            sql += " WHERE ";

            for (Element col : (List<Element>) elFilter.getChildren()) {
                sql += col.getAttributeValue("key") + " = ? AND ";
            }
            sql = sql.substring(0, sql.length() - 5);
        }
        sql += ";";

        dao.prepareStatement(sql);
        
        for (Element col : (List<Element>) elDump.getChildren()) {
            String type = col.getAttributeValue("type");
            String value = getColValue(col);
            dao.addPreparedValue(value, type);
        }   
        
        for (Element col : (List<Element>) elFilter.getChildren()) {
            String type = col.getAttributeValue("type");
            String value = getColValue(col);
            dao.addPreparedValue(value, type);
        }        
        
        dao.executePreparedStatement();

        dao.releaseConnection();
    }

    private String getColValue(Element col) {
        String value = col.getAttributeValue("value");
        if (value == null) {
            value = col.getText();
        }
        return value;
    }

    private String createColumn(Element el, String table) {

        String sql = "";
        sql += el.getAttributeValue("name") + " ";
        sql += dataTypes.get(el.getAttributeValue("type"));
        if (el.getAttributeValue("maxLength") != null && !el.getAttributeValue("maxLength").equals("")) {
            sql += "(" + el.getAttributeValue("maxLength") + ")";
        }
        if (Boolean.valueOf(el.getAttributeValue("required"))) {
            sql += " NOT NULL";
        }
        if (Boolean.valueOf(el.getAttributeValue("generated"))) {
            sql += " AUTO_INCREMENT";
        }
        if (el.getAttributeValue("default") != null && !el.getAttributeValue("default").equals("")) {
            sql += " DEFAULT " + formatDefaultValue(el);
        }
        sql += ", ";
        if (Boolean.valueOf(el.getAttributeValue("primaryKey"))) {
            sql += "PRIMARY KEY pk_" + TextUtil.tinyFirstLetter(table) + " (" + el.getAttributeValue("name") + "), ";
        }
        String fk = el.getAttributeValue("foreignKey");
        if (fk != null && !fk.equals("")) {
            String[] tokenFK = fk.split("\\.");
            if (tokenFK.length != 2) {
                throw new OopsException("Wrong foreign key instruction, need [Table.field] : [" + fk + "]");
            }
            String nomeFK = "fk_" + TextUtil.tinyFirstLetter(table) + "_" + el.getAttributeValue("name");
            sql += "FOREIGN KEY " + nomeFK + " (" + el.getAttributeValue("name") + ") REFERENCES " + tokenFK[0] + "("
                    + tokenFK[1] + "), ";
        }
        if (Boolean.valueOf(el.getAttributeValue("unique"))) {
            sql += "UNIQUE uq_" + TextUtil.tinyFirstLetter(table) + el.getAttributeValue("name") + " ("
                    + el.getAttributeValue("name") + "), ";
        }

        return sql;

    }

    private String formatDefaultValue(Element el) {
        String ret = "";
        String type = el.getAttributeValue("type");
        String value = el.getAttributeValue("default");
        
        if (type.equals("java.lang.String")) {
            ret = "'" + value + "'";
            
        } else if (type.equals("java.math.BigDecimal")) {
            ret = value.replaceAll(",", ".");
            
        } else if (type.equals("java.util.Date")) {
            // SELECT CONVERT_TZ('2004-01-01 12:00:00','+00:00','+10:00');
            ret = "STR_TO_DATE('" + value.substring(0, 19) + "', '%Y-%m-%d %H:%i:%s')";

        } else {
            ret = value;
        }
        return ret;
    }

    @Override
    public void dropTable(DataSource ds, Element el) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "DROP TABLE ";
        sql += el.getAttributeValue("name");
        sql += ";";

        dao.executeSQLCommand(sql);

        dao.releaseConnection();

    }

    @Override
    public void dropColumn(DataSource ds, Element el, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "ALTER TABLE ";
        sql += table;
        sql += " DROP COLUMN ";
        sql += el.getAttributeValue("name") + ";";

        dao.executeSQLCommand(sql);

        dao.releaseConnection();
    }

    @Override
    public void addColumn(DataSource ds, Element el, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";
        sql += "ALTER TABLE ";
        sql += table;
        sql += " ADD COLUMN ";
        sql += createColumn(el, table);

        sql = sql.substring(0, sql.length() - 2) + ";";

        dao.executeSQLCommand(sql);

        dao.releaseConnection();
    }

    @Override
    public void alterColumn(DataSource ds, Element elFrom, Element elTo, String table) {

        DAOUtil dao = new DAOUtil(ds);

        String sql = "";

        sql += "ALTER TABLE ";
        sql += table;
        sql += " CHANGE COLUMN ";
        sql += elFrom.getAttributeValue("name") + " ";
        sql += createColumn(elTo, table);

        sql = sql.substring(0, sql.length() - 2) + ";";

        dao.executeSQLCommand(sql);

        dao.releaseConnection();
    }

}
