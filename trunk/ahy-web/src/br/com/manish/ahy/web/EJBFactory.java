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

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.manish.ahy.kernel.BaseEJBLocal;
import br.com.manish.ahy.kernel.exception.OopsException;

public class EJBFactory {

    private static EJBFactory me;

    InitialContext ic;

    public static EJBFactory getInstance() {
        if (me == null) {
            me = new EJBFactory();
        }
        return me;
    }

    private EJBFactory() {
        ic = setupContext();
    }

    public <T extends BaseEJBLocal> T getEJB(Class<? extends BaseEJBLocal> localInterface) {
        
        String ejbName = localInterface.getSimpleName();
        ejbName = ejbName.substring(0, ejbName.length() - 5);
            
        return getEJB(ejbName);
    }

    public <T extends BaseEJBLocal> T getEJB(String ejbName) {
        String jndiName = "";

        try {
            jndiName = "ahy/" + ejbName + "/local";
            T instancia = (T) ic.lookup(jndiName);
            return instancia;

        } catch (NamingException e) {
            throw new OopsException(e, "Wrong ejb name: {0}", jndiName);
        }
    }
    
    private InitialContext setupContext() {
    	
        String server = System.getProperty("ahycms.jnpServer");
        if (server == null || server.equals("")) {
            server = "localhost";//default
        }

        String jnpPort = System.getProperty("ahycms.jnpPort");
        if (jnpPort == null || jnpPort.equals("")) {
            jnpPort = "1099";//default
        }
        
        Properties env = new Properties();
        env.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        env.setProperty(InitialContext.PROVIDER_URL, "jnp://" + server + ":" + jnpPort); 
        env.setProperty(InitialContext.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

        InitialContext ic;
        try {
            ic = new InitialContext(env);
        } catch (NamingException e) {
            throw new OopsException(e, "Error when setting up JNDI Context.");
        }
        return ic;
    }
}
