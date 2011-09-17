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

public final class HtmlUtil {

    private static final String[][] CHARS = { { "á", "&aacute;" }, { "Á", "&Aacute;" }, { "é", "&eacute;" },
            { "É", "&Eacute;" }, { "í", "&iacute;" }, { "Í", "&Iacute;" }, { "ó", "&oacute;" }, { "Ó", "&Oacute;" },
            { "ú", "&uacute;" }, { "Ú", "&Uacute;" }, { "â", "&acirc;" }, { "Â", "&Acirc;" }, { "ê", "&ecirc;" },
            { "Ê", "&Ecirc;" }, { "ô", "&ocirc;" }, { "Ô", "&Ocirc;" }, { "ç", "&ccedil;" }, { "Ç", "&Ccedil;" },
            { "ã", "&atilde;" }, { "Ã", "&Atilde;" }, { "à", "&agrave;" }, { "À", "&Agrave;" }, { "õ", "&otilde;" },
            { "Õ", "&Otilde;" }, { "è", "&egrave;" }, { "È", "&Egrave;" },
            { "ª", "&ordf;" }, { "²", "&sup2;" }, { "¿", "&iquest;" }, { "³", "&sup3;" }, { "º", "&ordm;" },
            { "¹", "&sup1;" }, { "½", "&frac12;" }, { "©", "&copy;" }, { "®", "&reg;" } };

    private static final String[][] EXTENDED_CHARS = { { "&", "&amp;" }, { "<", "&lt;" }, { ">", "&gt;" },
    	{ "\n", "<br/>" }, { "\t", "&nbsp;&nbsp;&nbsp;&nbsp;" }, { "\"", "&quot;" } };

    private HtmlUtil() {
        super();
    }

    public static String replaceAllSpecialChars(String text) {

        for (String[] item : EXTENDED_CHARS) {
            text = text.replaceAll(item[0], item[1]);
        }

        text = replaceAccentuationChars(text);

        return text;
    }

    public static String replaceAccentuationChars(String text) {

        for (String[] item : CHARS) {
            text = text.replaceAll(item[0], item[1]);
        }

        return text;
    }

    public static String replaceHtmlChars(String text) {

        for (String[] item : CHARS) {
            text = text.replaceAll(item[1], item[0]);
        }

        return text;
    }

    public static String replaceSpecialCharsForLink(String text) {

        String[][] chars = { { "&", "e" }, { " ", "_" }, { "\\?", "" } };

        for (String[] item : chars) {
            text = text.replaceAll(item[0], item[1]);
            text = TextUtil.removeAccentuation(text);
            text = text.toLowerCase();
        }

        return text;
    }

}