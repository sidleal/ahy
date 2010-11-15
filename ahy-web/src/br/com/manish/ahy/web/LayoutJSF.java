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

package br.com.manish.ahy.web;

import br.com.manish.ahy.kernel.UpdateManagerEJBLocal;
import br.com.manish.ahy.kernel.exception.OopsException;

public class LayoutJSF extends BaseJSF {

	
    public String getVersion() {
        String version = getSessionInfo().getVersion();
        try {
            if (version == null) {
                UpdateManagerEJBLocal ejb = getEJB(UpdateManagerEJBLocal.class);
                
                getLog().info("Verifying kernel/database version.");
                version = ejb.getVersion();
                getSessionInfo().setVersion(version);
                
            }
        } catch (OopsException oe) {
    		logException(oe);
            version = formatMsg("ERROR: {0} - Check log files.", formatMsg(oe.getMessage(), oe.getAdditionalInformation()));
 
        } catch (Exception e) {
        	logException(e);
            version = formatMsg("ERROR: {0} - Check log files.", e.getMessage());
        }
        return version;
    }

	
}
