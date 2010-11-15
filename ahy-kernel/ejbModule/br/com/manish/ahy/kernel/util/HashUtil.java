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

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.manish.ahy.kernel.exception.OopsException;

public final class HashUtil {
    private static Log log = LogFactory.getLog(HashUtil.class);

    private HashUtil() {
        super();
    }

    static final String HEXES =  "0123456789ABCDEF";
    static final String HEXES2 = "01A3456789ABDDEF0123456789AACDEF";

    public static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static byte[] getBytes(String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), HEXES.length());
        }
        return bts;
    }

    public static String getHash(String text) {
        String ret = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            ret = getHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
            throw new OopsException(e, "Hash Error.");
        }
        return ret;
    }

    public static String enc(String text) {
        String ret = null;
        try {
            Cipher cipher = getCipher(true);
            byte[] encr = cipher.doFinal(text.getBytes());
            ret = getHex(encr);
        } catch (Exception e) {
            log.error(e);
            throw new OopsException(e, "Enc Error.");
        }

        return ret;

    }

    public static String dec(String text) {
        String ret = null;
        try {
            Cipher cipher = getCipher(false);
            byte[] orig = cipher.doFinal(getBytes(text));
            ret = new String(orig);
        } catch (Exception e) {
            log.error(e);
            throw new OopsException(e, "Dec Error.");
        }

        return ret;
    }

    private static Cipher getCipher(boolean enc) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKeySpec skeySpec = new SecretKeySpec(getBytes(HEXES2), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        if (enc) {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        }
        return cipher;
    }

}
