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
package br.com.manish.ahy.kernel.exception;

public class OopsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Object[] additionalInformation;

    public OopsException(String message) {
        super(message);
    }

    public OopsException(Throwable cause, String message) {
        super(message, cause);
    }

    public OopsException(String message, Object... additionalInfo) {
        super(message);
        this.additionalInformation = additionalInfo;
    }

    public OopsException(Throwable cause, String message, Object... additionalInfo) {
        super(message, cause);
        this.additionalInformation = additionalInfo;
    }

    public String getFormattedStackTrace() {
        String retorno = "";
        Throwable causa = this.getCause();

        if (causa != null) {
            StackTraceElement[] stack = causa.getStackTrace();
            retorno = causa.toString();

            for (int i = 0; i < stack.length; i++) {
                retorno += stack[i] + "\n";
            }
        }

        return retorno;
    }

    public Object[] getAdditionalInformation() {
        return additionalInformation;
    }

}
