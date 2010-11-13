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

package br.com.manish.ahy.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.exception.OopsException;

public final class DAOUtil {
    private static Log log = LogFactory.getLog(DAOUtil.class);

    private DAOUtil() {
    }

    public static List<Map<String, Object>> executeSQLQuery(DataSource ds, String sql) {

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            try {
                log.debug("Connecting with database.");
                con = ds.getConnection();
                stmt = con.createStatement();
                log.debug("Connection ok.");
            } catch (Exception e) {
                throw new OopsException(e, "Problem when acessing database.");
            }

            log.info("executing: " + sql);

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> item = new HashMap<String, Object>();
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    item.put(rsmd.getColumnLabel(i), rs.getObject(i));
                }
                ret.add(item);
            }

        } catch (Exception e) {
            throw new OopsException(e, "Problem when executing sql command.");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception rse) {
                log.error(rse);
            }
            log.debug("Connection closed.");
        }

        return ret;
    }

    public static void executeSQLCommand(DataSource ds, String sql) {
        Connection con = null;
        Statement stmt = null;

        try {

            try {
                log.debug("Connecting with database.");
                con = ds.getConnection();
                stmt = con.createStatement();
                log.debug("Connection ok.");
            } catch (Exception e) {
                throw new OopsException(e, "Problem when acessing database.");
            }

            log.info("executing: " + sql);

            if (checkForBlob(sql, con)) {
                log.debug("Blob field, using prepared statement.");
            } else {
                stmt.execute(sql);
            }

        } catch (Exception e) {
        	throw new OopsException(e, "Problem when executing sql command.");

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception rse) {
                log.error(rse);
            }
            log.debug("Connection closed.");
        }
    }

    private static Boolean checkForBlob(String sql, Connection con) throws SQLException, IOException {

        int blob = sql.indexOf(JPAUtil.BLOB_BATCH_PREFIX);

        Boolean blobPresent = blob > 0;

        if (sql.trim().startsWith("INSERT") && blobPresent) {
            String sqlA = sql.substring(0, sql.indexOf("VALUES (") + 8);
            String sqlB = sql.substring(sql.indexOf("VALUES (") + 8, sql.length());
            sqlB = sqlB.replaceAll(" ", "");
            sqlB = sqlB.substring(0, sqlB.indexOf(")"));
            String[] tokens = sqlB.split(",");

            for (int i = 0; i < tokens.length; i++) {
                sqlA += "?,";
            }
            sqlA = sqlA.substring(0, sqlA.length() - 1) + ")";

            log.info("Prepared: " + sqlA);

            PreparedStatement pstmt = con.prepareStatement(sqlA);

            for (int i = 0; i < tokens.length; i++) {
                String str = tokens[i];
                if (str.startsWith(JPAUtil.BLOB_BATCH_PREFIX)) {
                    String path = "";
                    path = str.substring(JPAUtil.BLOB_BATCH_PREFIX.length(), str.length());
                    byte[] fileArray = FileUtil.readResourceAsBytes(path);
                    pstmt.setBytes(i + 1, fileArray);

                } else {
                    pstmt.setString(i + 1, str.replaceAll("'", ""));
                }
            }

            pstmt.executeUpdate();
        }

        return blobPresent;
    }

    public static String executeSingleResultSQLQuery(DataSource ds, String sql) {

        String ret = null;

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            try {
                log.debug("Connecting with database.");
                con = ds.getConnection();
                stmt = con.createStatement();
                log.debug("Connection ok.");
            } catch (Exception e) {
                throw new OopsException(e, "Problem when acessing database.");
            }

            log.info("executing: " + sql);

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                ret = rs.getString(1);
            }

        } catch (Exception e) {
        	throw new OopsException(e, "Problem when executing sql query.");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception rse) {
                log.error(rse);
            }
            log.debug("Connection closed.");
        }
        return ret;
    }

}
