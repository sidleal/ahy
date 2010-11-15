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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class TextUtil {
    private static Log log = LogFactory.getLog(TextUtil.class);

    private TextUtil() {
        super();
    }

    public static String removeAccentuation(String text) {

        String[][] chars = { { "á", "a" }, { "Á", "A" }, { "é", "e" }, { "É", "E" }, { "í", "i" }, { "Í", "I" },
                { "ó", "o" }, { "Ó", "O" }, { "ú", "u" }, { "Ú", "U" }, { "â", "a" }, { "Â", "A" }, { "ê", "e" },
                { "Ê", "E" }, { "ô", "o" }, { "Ô", "O" }, { "ç", "c" }, { "Ç", "C" }, { "ã", "a" }, { "Ã", "A" },
                { "õ", "o" }, { "Õ", "O" }, { "à", "a" }, { "À", "A" } };

        for (String[] item : chars) {
            text = text.replaceAll(item[0], item[1]);
        }

        return text;
    }

    public static String tinyFirstLetter(String str) {
        String firstLetter = str.substring(0, 1);
        String ret = str.substring(1, str.length());
        ret = firstLetter.toLowerCase() + ret;
        return ret;
    }

    public static String capFirstLetter(String str) {
        String firstLetter = str.substring(0, 1);
        String ret = str.substring(1, str.length());
        ret = firstLetter.toUpperCase() + ret;
        return ret;
    }

}
