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

import javafx.scene.Group;
import javafx.scene.CustomNode;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import br.com.manish.ahy.client.SessionInfo;

public class Template extends CustomNode {
    var screenHeight: Number = 700;
    var screenWidth: Number = 1000;
    var rootFolder: String = "{__DIR__}".substring(0, "{__DIR__}".length() - "/br.com.manish.ahy.fxadmin/".length());

    var title: Text = Text {
        content: "Ahy CMS Administrator"
        layoutX: bind screenWidth - title.layoutBounds.width;
        layoutY: 25
        styleClass: "text-admin-title"
    }

    var loggedUser: Text = Text {
        content: bind SessionInfo.getInstance().getUserName();
        layoutX: bind screenWidth - loggedUser.layoutBounds.width;
        layoutY: 85
        styleClass: "text-logged-user"
    }

    var mainGroup: Group = Group {
        content: [title, loggedUser]
    }

    var menuGroup: Group = Group {}

    var novoProjeto: MenuItem = MenuItem {
        label: "Novo Projeto"
        posX: 5, posY: 50
        onClick: function() {
            unselectMenu();
            novoProjeto.select();
        }
    }

    var editarPagina: MenuItem = MenuItem {
        label: "Editar Páginas"
        posX: 145, posY: 50
        onClick: function() {
            unselectMenu();
            editarPagina.select();
        }
    }

    var sair: MenuItem = MenuItem {
        label: "Sair"
        posX: 285, posY: 50
        onClick: function() {
            unselectMenu();
            sair.select();
        }
    }

    function unselectMenu(): Void {
        novoProjeto.unselect();
        editarPagina.unselect();
        sair.unselect();
    }

    function buildMenu() {
        insert novoProjeto into menuGroup.content;
        insert editarPagina into menuGroup.content;
        insert sair into menuGroup.content;
    }


    var lineHeader: Line = Line {
        startX: 0, startY: 80
        endX: screenWidth, endY: 80
        strokeWidth: 3
        stroke: Color.BLACK
    };

    override function create() : Node {
 
        insert lineHeader into mainGroup.content;
        buildMenu();
        insert menuGroup into mainGroup.content;

        mainGroup
    }
}
