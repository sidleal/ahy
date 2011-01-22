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
package br.com.manish.ahy.fxadmin;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.scene.paint.Color;

/**
 * @author sidleal@gmail.com
 */

var screenHeight: Number = Screen.primary.visualBounds.height;
var screenWidth: Number = Screen.primary.visualBounds.width;
var rootFolder: String = "{__DIR__}".substring(0, "{__DIR__}".length() - "/br.com.manish.ahy.fxadmin/".length());

var scene : Scene =  Scene {
    stylesheets: "{rootFolder}/resources/template.css";
    content: [
        Login{}
    ]
    fill: Color.WHITE
}

Stage {
    title: "AhyCMS - Administrator"
    scene: scene
    width: screenWidth
    height: screenHeight;

}