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
import javafx.scene.shape.Rectangle;
import javafx.scene.CustomNode;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class MenuItem extends CustomNode {
    public var posX: Number;
    public var posY: Number;
    public var label: String;

    public var onClick: function();

    var status: String = "normal";

    public function unselect() {
        status = "normal";
    }
    public function select() {
        status = "selected";
    }

    var animY: Number = 0;
    var animH: Number = 20;

    function animate() {
        Timeline {
            keyFrames : [
                KeyFrame {
                    time : 0.1s
                    values : [
                        animY => 10 tween Interpolator.EASEBOTH,
                        animH => 0 tween Interpolator.EASEBOTH
                    ]
                },
                KeyFrame {
                    time : 0.3s
                    values : [
                        animY => 0 tween Interpolator.EASEBOTH,
                        animH => 20 tween Interpolator.EASEBOTH
                    ]
                }
            ]
        }.playFromStart();

    }


    override function create() : Node {
        var itemRect: Rectangle = Rectangle {
            width: 20, height: bind animH
            styleClass: bind "menu-rect-{status}"
            layoutX: 0, layoutY: bind animY
        }
        var itemLabel: Text = Text {
            content: bind label
            layoutX: 23
            layoutY: 3
            styleClass: bind "menu-label-{status}"
        }
        var itemBg: Rectangle = Rectangle {
            width: 110, height: 20
            fill: Color.WHITE
            layoutX: 0, layoutY: 0
        }
        var item: Group = Group {
            content: [itemBg, itemRect, itemLabel]
            cursor: Cursor.HAND
            translateX: bind posX
            translateY: bind posY
            onMouseEntered: function(e: MouseEvent) {
               if (not status.equals("selected")) {
                   animate();
                   status = "hover";
               }
            }
            onMousePressed: function (e: MouseEvent) {
                onClick();
            }
            onMouseExited: function (e: MouseEvent) {
                if (not status.equals("selected")) {
                    animate();
                    status = "normal";
                }
            }
        }
        item
    }
}
