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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordBox;
import javafx.scene.control.TextBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Cursor;
import javafx.scene.layout.LayoutInfo;
import java.util.Map;
import br.com.manish.ahy.client.SessionInfo;
import java.lang.System;

public class Login extends CustomNode {
    var screenHeight: Number = 700;
    var screenWidth: Number = 1000;
    var rootFolder: String = "{__DIR__}".substring(0, "{__DIR__}".length() - "/br.com.manish.ahy.fxadmin/".length());

    var posX: Number = screenWidth/2 - 150/2;
    var posY: Number = 100;

    var _this = this;

    var title: Text = Text {
        content: "Ahy CMS Administrator - Login"
        layoutX: bind screenWidth - title.layoutBounds.width;
        layoutY: 25
        styleClass: "text-admin-title"
    }

    var alertText: String = "";
    var alert: Text = Text {
        content: bind alertText;
        layoutX: bind screenWidth/2 - alert.layoutBounds.width/2;
        layoutY: bind posY + 100
        styleClass: "text-alert"
    }

    var txtUser:TextBox = TextBox {
        promptText: "E-Mail";
        layoutInfo: LayoutInfo{width: 150, height: 20}
        translateX: bind posX
        translateY: bind posY
    }

    var txtPassword:PasswordBox = PasswordBox {
        promptText: "Senha";
        layoutInfo: LayoutInfo{width: 150, height: 20}
        translateX: bind posX
        translateY: bind posY + 30
        onKeyPressed: function( ke: KeyEvent ):Void {
            if (ke.code == KeyCode.VK_ENTER){
                if (validate()) {
                    authenticate();
                }
            }
        }        
    }

    var cmdOK : Button = Button {
        text: "Entrar"
        translateX: bind posX
        translateY: bind posY + 60
        layoutInfo: LayoutInfo{width: 150, height: 20}
        cursor: Cursor.HAND
        action: function () {
            if (validate()) {
                authenticate();
            }
        }
    }

    function validate(): Boolean {
        var ret: Boolean = true;
        alertText = "";

        if (txtUser.text.trim().equals("")) {
            alertText += "User E-Mail required.";
            ret = false;
        }

        if (txtPassword.text.trim().equals("")) {
            alertText += "Password required.";
            ret = false;
        }

        return ret;
    }


    function authenticate() {
        //SessionInfo.getInstance().setDomain("www.technearquitetura.com.br:8080"); //TODO: get the domain from somewhere
        SessionInfo.getInstance().setDomain("techne.ahycms.org:4100"); //TODO: get the domain from somewhere

        var loginModel: LoginModel = new LoginModel();
        var userMap: Map = loginModel.authenticate(txtUser.text, txtPassword.text);

        var userName: String = userMap.get("name") as String;

        if (userName != null) {
            SessionInfo.getInstance().setUserLogin(txtUser.text);
            SessionInfo.getInstance().setUserName(userName);
            var template: Template = Template{};
            insert template into scene.content;
            delete _this from scene.content;
        } else {
            var error: String = userMap.get("error") as String;
            alertText = error;
        }

    }

    var mainGroup: Group = Group {
        content: [title, alert]
    }

    var lineHeader: Line = Line {
        startX: 0, startY: 80
        endX: screenWidth, endY: 80
        strokeWidth: 3
        stroke: Color.BLACK
    };

    override function create() : Node {
        insert txtUser into mainGroup.content;
        insert txtPassword into mainGroup.content;
        insert cmdOK into mainGroup.content;
        insert lineHeader into mainGroup.content;

        mainGroup
    }
}
