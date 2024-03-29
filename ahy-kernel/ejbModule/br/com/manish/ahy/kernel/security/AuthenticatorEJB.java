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

package br.com.manish.ahy.kernel.security;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.manish.ahy.kernel.BaseEJB;
import br.com.manish.ahy.kernel.Site;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.HashUtil;

@Stateless
public class AuthenticatorEJB extends BaseEJB implements AuthenticatorEJBLocal {

    public User authenticate(User filter) {
        User ret = null;

        try {

            getLog().debug("User: [" + filter.getEmail() + "]");

            String sql = "from User u";
            sql += " where u.site.domain = '" + filter.getSite().getDomain() + "'";
            sql += " and email = '" + filter.getEmail() + "'";
            
            Query query = getEm().createQuery(sql);
            User user = (User) query.getSingleResult();
            
            String hashTeste = HashUtil.getHash(filter.getEmail() + filter.getPassword());

            if (hashTeste.equals(user.getPassword())) {
                ret = user;
                startSession(user);
            } else {
                throw new OopsException("Invalid password");
            }
        } catch (OopsException oe) {
            throw oe;
        } catch (NoResultException nre) {
            throw new OopsException("User not found.");
        } catch (Exception e) {
            fireOopsException(e, "Error when authenticating the user.");
        }

        return ret;
    }

    @Override
    public Map<String, String> authenticate(Map<String, String> parameters) {
        User filter = new User();

        filter.setEmail(parameters.get("login"));
        filter.setPassword(parameters.get("password"));
        
        filter.setSite(new Site());
        filter.getSite().setDomain(parameters.get("domain"));
        
        User authUser = authenticate(filter);
        
        Map<String, String> ret = new HashMap<String, String>();
        
        ret.put("sessionid", getCurrentUserSessionID());

        ret.put("login", authUser.getEmail());
        ret.put("name", authUser.getName());
        
        return ret;
    }

}
