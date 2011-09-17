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
package br.com.manish.ahy.kernel.addon;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ejb.Stateless;

import br.com.manish.ahy.kernel.BaseEJB;
import br.com.manish.ahy.kernel.content.Content;
import br.com.manish.ahy.kernel.util.TextUtil;

@Stateless
public class AddonManagerEJB extends BaseEJB implements AddonManagerEJBLocal {

    private List<BaseAddonEJBLocal> addonList = new ArrayList<BaseAddonEJBLocal>();
    
    @Override
    public List<BaseAddonEJBLocal> getAddonList() {
        if (addonList.size() < 1) {
            try {
                URL url = this.getClass().getResource("");
                String deployPath = url.getPath();
                deployPath = deployPath.substring(0, deployPath.indexOf("ahy.ear") + 7);
        
                File root = new File(deployPath);
    
                if (root.isDirectory()) {
        
                    List<File> listFiles = Arrays.asList(root.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String name) {
                            return name.startsWith("ahy-") && name.endsWith(".jar") && name.indexOf("kernel") <= 0;
                        }
                    }));
                    
                    for (File f : listFiles) {
                        addonList.add(lookupAddon(f.getName()));
                    }
                    
                } else {
                    
                    JarFile jarFile = new JarFile(root);
    
                    Enumeration<JarEntry> resources = jarFile.entries();
                    while (resources.hasMoreElements()) {
                        JarEntry je = resources.nextElement();
                        if (je.getName().matches("ahy-.*\\.jar") && je.getName().indexOf("kernel") <= 0) {
                            addonList.add(lookupAddon(je.getName()));
                        }
                    }
                    
                }
                
            }catch (Exception e) {
                fireOopsException(e, "Error when discoverying addons.");
            }
        }
        
        return addonList;
    }

    private BaseAddonEJBLocal lookupAddon(String fileName) {
        String ejbName = fileName.substring(4, fileName.length() - 4);
        ejbName = "ahy/" + TextUtil.capFirstLetter(ejbName) + "EJB/local";
        BaseAddonEJBLocal ejbLocal = (BaseAddonEJBLocal) getCtx().lookup(ejbName);
        return ejbLocal;
    }
    
    @Override
    public String afterHtmlParser(String html, Content filter) {
        List<BaseAddonEJBLocal> addonList = getAddonList();
        for (BaseAddonEJBLocal addon: addonList) {
            html = addon.afterHtmlParser(html, filter);
        }
        return html;
    }

    @Override
    public BaseAddonEJBLocal getAddon(String module) {
        BaseAddonEJBLocal ret = null;
        List<BaseAddonEJBLocal> addonList = getAddonList();
        for (BaseAddonEJBLocal addon: addonList) {
            if (addon.getVersion().getModule().equals(module)) {
                ret = addon;
                break;
            }
        }
        return ret;
    }
    
    
}
