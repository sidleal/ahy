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

package br.com.manish.ahy.kernel.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.kernel.exception.OopsException;

public final class DAOUtil {
    private static Log log = LogFactory.getLog(DAOUtil.class);

    private Connection con = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private Integer pstmtCount = 1;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
    
    public DAOUtil(DataSource ds) {
        try {
            log.debug("Connecting to database.");
            con = ds.getConnection();
            stmt = con.createStatement();
            log.debug("Connection suceeded.");
        } catch (Exception e) {
        	throw new OopsException(e, "Problem when acessing database.");
        }
    }

    public void releaseConnection() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
            log.debug("Connection closed.");
        } catch (Exception rse) {
            log.error(rse);
        }
    }

    public List<Map<String, Object>> executeSQLQuery(String sql) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        try {
            log.info("executeSQLQuery: " + sql);

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
        }

        return ret;
    }

    public String executeSQLQuerySingleResult(String sql) {
        String ret = null;
        try {
            log.info("executeSQLQuerySingleResult: " + sql);

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                ret = rs.getString(1);
            }

        } catch (Exception e) {
        	throw new OopsException(e, "Problem when executing sql command.");
        }

        return ret;
    }

    public void executeSQLCommand(String sql) {

        try {
            log.info("executeSQLCommand: " + sql);

            stmt.execute(sql);

        } catch (Exception e) {
        	throw new OopsException(e, "Problem when executing sql command.");
        }
    }

    public void prepareStatement(String sql) {
        try {
        	log.info("preparedStatement: " + sql);
            pstmt = con.prepareStatement(sql);
            pstmtCount = 1;
        } catch (SQLException e) {
        	throw new OopsException(e, "Problem when preparing sql command.");
        }
    }

    public void addPreparedValue(String value, String type) {
        try {
            if (type.equals("java.lang.Boolean")) {
                pstmt.setBoolean(pstmtCount, Boolean.valueOf(value));
            } else if (type.equals("java.lang.Long")) {
                pstmt.setLong(pstmtCount, Long.valueOf(value));
            } else if (type.equals("java.lang.Integer")) {
                pstmt.setLong(pstmtCount, Integer.valueOf(value));
            } else if (type.equals("java.math.BigDecimal")) {
                pstmt.setBigDecimal(pstmtCount, new BigDecimal(value));
            } else if (type.equals("java.util.Date")) {
                pstmt.setDate(pstmtCount, new java.sql.Date(DATE_FORMAT.parse(value).getTime()));
            } else if (type.equals("java.sql.Blob")) {
                pstmt.setBytes(pstmtCount, FileUtil.readResourceAsBytes(value));
            } else {
                pstmt.setString(pstmtCount, value);                
            }
            pstmtCount++;
        } catch (Exception e) {
        	throw new OopsException(e, "Problem when setting item value on prepared statement.");
        }
    }

    public void executePreparedStatement() {
        try {
            pstmt.executeUpdate();
        } catch (SQLException e) {
        	throw new OopsException(e, "Problem when executing sql command.");
    	}
    }
    
}
