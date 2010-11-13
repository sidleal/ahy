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
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import br.com.manish.ahy.ddl.Parser;
import br.com.manish.ahy.ddl.ParserMySQL;
import br.com.manish.ahy.exception.OopsException;

public final class JPAUtil {
	private static Log log = LogFactory.getLog(JPAUtil.class);

	public static final String BLOB_BATCH_PREFIX = "#{blob}:";

	private JPAUtil() {
		super();
	}

	private static Map<String, Class<? extends Parser>> parserMap = new HashMap<String, Class<? extends Parser>>();

	static {
		parserMap.put("org.hibernate.dialect.MySQL5Dialect", ParserMySQL.class);
	}

	public static Blob streamToBlob(InputStream stream) throws IOException {
		return Hibernate.createBlob(stream);
	}

	public static Blob bytesToBlob(byte[] stream) {
		return Hibernate.createBlob(stream);
	}

	public static byte[] blobToBytes(Blob blob) {
		byte[] anexoArquivo = null;
		try {
			long tamanhoOriginal = blob.length();
			if (tamanhoOriginal > Integer.MAX_VALUE) {
				throw new OopsException("File too big.");
			}
			int tamanho = Long.valueOf(tamanhoOriginal).intValue();

			anexoArquivo = new byte[tamanho];
			blob.getBinaryStream().read(anexoArquivo);
		} catch (Exception e) {
			throw new OopsException(e, "Error reading file data.");
		}
		return anexoArquivo;
	}

	public static String getDatabaseTypeConfig() {

		String ret = null;

		SAXBuilder sb = new SAXBuilder();
		Document doc;

		try {

			URL url = JPAUtil.class.getResource("/META-INF/persistence.xml");
			doc = sb.build(url);

			String dialect = "";
			Element prop = doc.getRootElement().getChild("persistence-unit")
					.getChild("properties");
			for (Element el : (List<Element>) prop.getChildren()) {
				if (el.getAttributeValue("name").equals("hibernate.dialect")) {
					dialect = el.getAttributeValue("value");
					break;
				}
			}
	
			if (parserMap.get(dialect) != null) {
				ret = dialect;
			} else {
	
				String acceptedValues = "";
				for (String key : parserMap.keySet()) {
					acceptedValues += key + ",";
				}
				acceptedValues = acceptedValues.substring(0,
						acceptedValues.length() - 1);
	
				throw new OopsException("DataBase: [" + dialect
						+ "] not supported yet, allowed: " + acceptedValues);
	
			}
	
			log.info("Database type: [" + ret + "]");

		} catch (Exception e) {
			throw new OopsException(e, "Error when discovering database config.");
		}

		return ret;

	}

	public static Parser getParser() {

		String baseConfig = getDatabaseTypeConfig();

		Parser ret = null;

		try {
			ret = (Parser) parserMap.get(baseConfig).newInstance();
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			throw new OopsException(e, "Error on getting parser: ["
					+ baseConfig + "]");
		}

		return ret;

	}

}
