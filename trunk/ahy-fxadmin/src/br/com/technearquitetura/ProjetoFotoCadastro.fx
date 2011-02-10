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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.io.File;
import javafx.scene.text.Text;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javax.swing.filechooser.FileFilter;
import br.com.manish.ahy.fxadmin.ImageFileFilter;
import javafx.scene.control.TextBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.CheckBox;

public class ProjetoFotoCadastro extends CustomNode {
    var height: Number = 430;
    var width: Number = 550;

    public var posX: Number = 50;
    public var posY: Number = 100;

    var opacidade: Number = 0.5;

    public var list: List = new ArrayList() on replace {
        montaList();
    };

    var borda: Rectangle = Rectangle {
        width: bind width
        height: bind height
        stroke: Color.GRAY
        layoutX: bind posX
        layoutY: bind posY
        fill: Color.WHITE
    }

    var projetoFotoSelected: Imagem;

    var txtLabel:TextBox = TextBox {
        promptText: "Legenda";
        layoutInfo: LayoutInfo{width: 410, height: 20}
        layoutX: bind posX
        layoutY: bind posY - 23
        onKeyPressed: function( ke: KeyEvent ):Void {
            projetoFotoSelected.setLegenda(txtLabel.rawText);
        }
    }

    var chkFrontPage: CheckBox = CheckBox {
	allowTriState: false
	selected: false
        translateX: bind posX + 415
        translateY: bind posY - 21
        onMouseReleased: function( me: MouseEvent ):Void {
            projetoFotoSelected.setShowInFrontPage(chkFrontPage.selected);
        }
    }

    var lblFrontPage: Text = Text {
        content: "Página principal";
        layoutX: bind posX + 430;
        layoutY: bind posY - 21
        styleClass: "text-label"
    }

    var imageGroup: Group = Group {
        content: []
    }

    var mainGroup: Group = Group {
        content: [borda, imageGroup]
    }


    function montaList(): Void {
      imageGroup.content = [];
      
      var countX: Number = 0;
      var countY: Number = 0;
      for (item in list) {
            var cX = countX;
            var cY = countY;
            var fotoItem: ProjetoFotoItem = ProjetoFotoItem {
                width: bind width / 4 - 10
                height: bind height / 4 - 10
                posX: bind posX + 5 + (width/4-10 + 5) * cX
                posY: bind posY + 5 + (height/4-10 + 5) * cY
                projetoFoto: item as Imagem
                onClick: function(projFoto: Imagem) {
                    for (it in imageGroup.content) {
                        if (it instanceof ProjetoFotoItem) {
                            (it as ProjetoFotoItem).selected = false;
                        }
                    }
                    fotoItem.selected = true;
                    projetoFotoSelected = projFoto;
                    txtLabel.commit();
                    txtLabel.text = projFoto.getLegenda();
                    chkFrontPage.selected = projFoto.getShowInFrontPage();
                }

            }
            insert fotoItem into imageGroup.content;
            countX++;
            if (countX > 3) {
              countX = 0;
              countY++;
            }
        }
        var itemNew: Group = Group {
            content: [
                Rectangle {
                    width: bind width / 4 - 10
                    height: bind height / 4 - 10
                    stroke: Color.GRAY
                    layoutX: bind posX + 5 + (width/4-10 + 5) * countX
                    layoutY: bind posY + 5 + (height/4-10 + 5) * countY
                    fill: Color.GRAY
                    cursor: Cursor.HAND
                    onMousePressed: function (me: MouseEvent) {
                        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                        UIManager.put("swing.boldMetal", Boolean.FALSE);
                        var panel: JPanel = new JPanel();
                        var fc: JFileChooser = new JFileChooser();
                        var ff: FileFilter = new ImageFileFilter();
                        fc.setFileFilter(ff);         
                        var returnVal = fc.showOpenDialog(panel);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            var selectedFile: File = fc.getSelectedFile();

                            var foto: Imagem = new Imagem();
                            foto.setId(null);
                            foto.setLegenda("");
                            foto.setCaminhoArquivo("file:///{selectedFile.getPath()}");

                            delete itemNew from imageGroup.content;
                            list.add(foto);
                            montaList();
                        }
                    }
                },
                Text {
                    content: "  Nova\nImagem";
                    layoutX: bind posX + 5 + (width/4-10 + 5) * countX + 25
                    layoutY: bind posY + 5 + (height/4-10 + 5) * countY + 25
                    styleClass: "text-internal-button"
                }
            ]
            opacity: bind opacidade
            onMouseEntered: function (me: MouseEvent) {
                itemNew.toFront();
                cresce.playFromStart();
            }
            onMouseExited: function (me: MouseEvent) {
                diminui.playFromStart();
            }
        }
        insert itemNew into imageGroup.content;

    }


    override function create() : Node {
        montaList();

        insert txtLabel into mainGroup.content;
        insert chkFrontPage into mainGroup.content;
        insert lblFrontPage into mainGroup.content;
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
                time : 0.4s
                values : [
                    opacidade => 0.5 tween Interpolator.EASEIN
                ]
            }
        ]
    }
}
