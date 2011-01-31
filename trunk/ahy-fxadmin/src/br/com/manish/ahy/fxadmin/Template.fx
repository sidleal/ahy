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
import br.com.technearquitetura.ProjetoCadastro;
import br.com.technearquitetura.ConteudoCadastro;
import javafx.stage.Screen;

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

    var screenInfo: Text = Text {
        content: "{Screen.primary.visualBounds.width} x {Screen.primary.visualBounds.height}";
        layoutX: bind screenWidth - loggedUser.layoutBounds.width;
        layoutY: 100
        styleClass: "text-logged-user"
    }

    var mainGroup: Group = Group {
        content: [title, loggedUser, screenInfo]
    }

    var menuGroup: Group = Group {}

    var proj: ProjetoCadastro = ProjetoCadastro{};
    var content: ConteudoCadastro = ConteudoCadastro{};

    var projeto: MenuItem = MenuItem {
        label: "Projetos"
        posX: 5, posY: 50
        onClick: function() {
            unselectMenu();
            projeto.select();
            insert proj into mainGroup.content;
        }
    }

    var editarPagina: MenuItem = MenuItem {
        label: "Páginas"
        posX: 125, posY: 50
        onClick: function() {
            unselectMenu();
            editarPagina.select();
            insert content into mainGroup.content;
        }
    }

    var sair: MenuItem = MenuItem {
        label: "Sair"
        posX: 245, posY: 50
        onClick: function() {
            unselectMenu();
            var login: Login = Login{};
            insert login into scene.content;
            SessionInfo.getInstance().setUserLogin(null);
            SessionInfo.getInstance().setUserName(null);
            delete this from scene.content;
        }
    }

    function unselectMenu(): Void {
        projeto.unselect();
        editarPagina.unselect();
        sair.unselect();

        delete proj from mainGroup.content;
        delete content from mainGroup.content;
    }

    function buildMenu() {
        insert projeto into menuGroup.content;
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
