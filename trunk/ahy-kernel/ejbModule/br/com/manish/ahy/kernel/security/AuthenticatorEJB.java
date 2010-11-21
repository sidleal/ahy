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

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.manish.ahy.kernel.BaseEJB;
import br.com.manish.ahy.kernel.exception.OopsException;
import br.com.manish.ahy.kernel.util.HashUtil;

@Stateless
public class AuthenticatorEJB extends BaseEJB implements AuthenticatorEJBLocal {

    public User authenticate(User filter) {
        User ret = null;

        try {

            getLog().debug("User: [" + filter.getEmail() + "]");

            Query query = getEm().createQuery("from User where email = '" + filter.getEmail() + "'");
            User user = (User) query.getSingleResult();
            
            String hashTeste = HashUtil.getHash(filter.getEmail() + filter.getPassword());

            if (hashTeste.equals(user.getPassword())) {
                ret = user;
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

}
