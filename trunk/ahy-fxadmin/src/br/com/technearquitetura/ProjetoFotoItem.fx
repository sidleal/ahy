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
package br.com.technearquitetura;

import javafx.scene.Group;
import javafx.scene.CustomNode;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import br.com.manish.ahy.client.SessionInfo;
import javafx.scene.image.Image;
import java.util.Map;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class ProjetoFotoItem extends CustomNode {
    public var height: Number = 400;
    public var width: Number = 550;

    public var posX: Number = 50;
    public var posY: Number = 100;
    public var projetoFoto: Imagem;

    public var onClick: function(:Imagem);
    public var selected: Boolean;

    var opacidade: Number = 0.5;

    var imgMap: Map = SessionInfo.getInstance().getImageCache();

    var mainGroup: Group = Group {
        content: []
    }

    override function create() : Node {

        var url: String = projetoFoto.getCaminhoArquivo();
        if (url.startsWith("http")) {
            var urlToken: String[] = url.split("\\.");
            var extension: String = urlToken[urlToken.size()-1];
            url = "{url.substring(0, url.length() - extension.length())}thumbnail.{extension}";
        }

        var image: Image;
        if (imgMap.containsKey(url)) {
            image = imgMap.get(url) as Image;
        } else {
            image = Image {
              url: url;
              backgroundLoading: true;
            }
            imgMap.put(url, image);
        }

        var rect: Rectangle = Rectangle {
            width: bind width
            height: bind height
            layoutX: bind posX
            layoutY: bind posY
            fill: Color.TRANSPARENT
            stroke: Color.ORANGE
            strokeWidth: 2
            visible: bind selected
        }

        var imageView: ImageView = ImageView {
          image: image
          fitWidth: bind width
          fitHeight: bind height
          layoutX: bind posX
          layoutY: bind posY
          cursor: Cursor.HAND
          opacity: bind opacidade
          blocksMouse: true
          onMouseEntered: function (me: MouseEvent) {
              this.toFront();
              cresce.playFromStart();
          }
          onMouseExited: function (me: MouseEvent) {
              diminui.playFromStart();
          }
          onMousePressed: function (me: MouseEvent) {
              onClick(projetoFoto);
          }

        }
        insert imageView into mainGroup.content;
        insert rect into mainGroup.content;
        mainGroup
    }

    var cresce = Timeline {
        keyFrames : [
            KeyFrame {
                time : 0.1s
                values : [
                    opacidade => 1 tween Interpolator.EASEOUT
                ]
            }
        ]
    }
    var diminui = Timeline {
        keyFrames : [
            KeyFrame {
                time : 0.3s
                values : [
                    opacidade => 0.5 tween Interpolator.EASEIN
                ]
            }
        ]
    }

}
