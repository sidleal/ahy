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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.DAOUtil;
import br.com.manish.ahy.kernel.util.JPAUtil;
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
        String result = DAOUtil.executeSingleResultSQLQuery(ds, sql);
        return result != null;
    }

    @Override
    public List<String> createTables(DataSource ds, Element el) {

        List<String> ret = new ArrayList<String>();

        for (Element table : (List<Element>) el.getChildren()) {
            ret.addAll(createTable(ds, table));
        }

        return ret;
    }

    @Override
    public List<String> createTable(DataSource ds, Element table) {

        List<String> ret = new ArrayList<String>();

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
            sql += ");";

            ret.add(sql);

            for (Element insert : (List<Element>) table.getChildren()) {
                if (insert.getName().equals("insert")) {
                    for (Element col : (List<Element>) insert.getChildren()) {
                        if (typesMap.get(col.getAttributeValue("key")) == null) {
                            throw new OopsException("It seems that you have a typo error on xml, can't find column [ "
                                    + col.getAttributeValue("key") + " ] in [" + table.getAttributeValue("name") + "]");
                        }
                        col.setAttribute("type", typesMap.get(col.getAttributeValue("key")));
                    }
                    ret.add(createInsert(insert, table.getAttributeValue("name")));
                }
            }
        }
        return ret;
    }

    @Override
    public String createInsert(Element el, String table) {
        String sql = "";
        sql += "INSERT INTO " + table + " (";
        String values = "";

        for (Element col : (List<Element>) el.getChildren()) {
            sql += col.getAttributeValue("key") + ", ";
            values += formatValueForInsert(col) + ", ";
        }

        sql = sql.substring(0, sql.length() - 2);
        values = values.substring(0, values.length() - 2);

        sql += ") VALUES (" + values + ");";
        return sql;
    }

    @Override
    public String createDelete(Element elFilter, String table) {
        String sql = "";
        sql += "DELETE FROM " + table;
        sql += " WHERE ";
        for (Element col : (List<Element>) elFilter.getChildren()) {
            sql += col.getAttributeValue("key") + " = " + formatValueForInsert(col) + " AND ";
        }
        sql = sql.substring(0, sql.length() - 5);
        sql += ";";
        return sql;
    }

    @Override
    public String createUpdate(Element elFilter, Element elDump, String table) {
        String sql = "";
        sql += "UPDATE " + table + " SET ";
        for (Element col : (List<Element>) elDump.getChildren()) {
            sql += col.getAttributeValue("key") + " = " + formatValueForInsert(col) + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);

        if (elFilter.getChildren().size() > 0) {
            sql += " WHERE ";

            for (Element col : (List<Element>) elFilter.getChildren()) {
                sql += col.getAttributeValue("key") + " = " + formatValueForInsert(col) + " AND ";
            }
            sql = sql.substring(0, sql.length() - 5);
        }
        sql += ";";
        return sql;
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
            sql += " DEFAULT " + el.getAttributeValue("default");
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

    private String formatValueForInsert(Element el) {
        String ret = "";
        String type = el.getAttributeValue("type");
        String value = el.getAttributeValue("value");
        if (type.equals("java.lang.String")) {
            ret = "'" + value + "'";
        } else if (type.equals("java.math.BigDecimal")) {
            ret = value.replaceAll(",", ".");
        } else if (type.equals("java.util.Date")) {
            // SELECT CONVERT_TZ('2004-01-01 12:00:00','+00:00','+10:00');
            ret = "STR_TO_DATE('" + value.substring(0, 19) + "', '%Y-%m-%d %H:%i:%s')";
        } else if (type.equals("java.sql.Blob")) {
            ret = JPAUtil.BLOB_BATCH_PREFIX + value;
        } else {
            ret = value;
        }
        return ret;
    }

    @Override
    public String dropTable(Element el) {
        String sql = "";
        sql += "DROP TABLE ";
        sql += el.getAttributeValue("name");
        sql += ";";

        return sql;
    }

    @Override
    public String dropColumn(Element el, String table) {
        String sql = "";
        sql += "ALTER TABLE ";
        sql += table;
        sql += " DROP COLUMN ";
        sql += el.getAttributeValue("name") + ";";

        return sql;
    }

    @Override
    public String addColumn(Element el, String table) {
        String sql = "";
        sql += "ALTER TABLE ";
        sql += table;
        sql += " ADD COLUMN ";
        sql += createColumn(el, table);

        sql = sql.substring(0, sql.length() - 2) + ";";

        return sql;
    }

    @Override
    public String alterColumn(Element elFrom, Element elTo, String table) {
        String sql = "";

        sql += "ALTER TABLE ";
        sql += table;
        sql += " CHANGE COLUMN ";
        sql += elFrom.getAttributeValue("name") + " ";
        sql += createColumn(elTo, table);

        sql = sql.substring(0, sql.length() - 2) + ";";

        return sql;
    }

}
