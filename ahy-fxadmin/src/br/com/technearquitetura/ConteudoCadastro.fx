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

import javafx.scene.CustomNode;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TextBox;
import javafx.scene.Cursor;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.ListView;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import br.com.manish.ahy.fxadmin.ImageFileFilter;
import javax.swing.filechooser.FileFilter;
import br.com.manish.ahy.client.SessionInfo;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class ConteudoCadastro extends CustomNode {
    var height: Number = 600;
    var width: Number = 900;
    var rootFolder: String = "{__DIR__}".substring(0, "{__DIR__}".length() - "/br.com.technearquitetura/".length());

    var posX: Number = 50;
    var posY: Number = 100;
    var model: ConteudoCadastroModel = new ConteudoCadastroModel();

    var title: Text = Text {
        content: "Cadastro de Conteúdo"
        layoutX: bind posX;
        layoutY: bind posY + 10
        styleClass: "text-addon-title"
    }

    var alertText: String = "";
    var alert: Text = Text {
        content: bind alertText;
        layoutX: bind posX;
        layoutY: bind posY - 10
        styleClass: "text-alert"
    }

    var cmbAcao: ListView = ListView {
        translateX: bind posX
        translateY: bind posY + 40
        layoutInfo: LayoutInfo{width: 550, height: 90}
        vertical: true
        items: []
        onMouseReleased: function (me: MouseEvent) {
            if ((cmbAcao.selectedItem as Conteudo).getId() != null) {
                //cmbAcao.cursor = Cursor.WAIT;
                //AsyncTask {
                //   run: function() {
                       carregarConteudo((cmbAcao.selectedItem as Conteudo).getId());
                //   }
                //   onDone: function() {
                      // cmbAcao.cursor = Cursor.DEFAULT;
               //    }
              //  }.start();
            }
        }
    }
    
    var txtTexto:TextBox = TextBox {
        promptText: "Texto";
        layoutInfo: LayoutInfo{width: 550, height: 300}
        translateX: bind posX
        translateY: bind posY + 150
        multiline: true
    }

    var fotoAnterior: Imagem = new Imagem();
    var foto: Imagem = new Imagem();
    var contentShortcut: String = "";

    var imageView: ImageView = ImageView {
      image: Image {url: "{rootFolder}/resources/adicionar.jpg"}
      fitWidth: 120
      fitHeight: 100
      layoutX: bind posX + 212
      layoutY: bind posY + 465
      cursor: Cursor.HAND
      blocksMouse: true
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

                foto = new Imagem();
                foto.setId(null);
                foto.setLegenda("");
                foto.setCaminhoArquivo("file:///{selectedFile.getPath()}");
                
                imageView.image = Image {url: foto.getCaminhoArquivo()};

            }

      }
    }

    var lineDiv: Line = Line {
        startX: bind posX, startY: bind posY + 570
        endX: bind posX + 550, endY: bind posY + 570
        strokeWidth: 1
        stroke: Color.GRAY
    };

    var lineFooter: Line = Line {
        startX: bind posX, startY: bind posY + 610
        endX: bind posX + 550, endY: bind posY + 610
        strokeWidth: 1
        stroke: Color.GRAY
    };

    var cmdGravar : Button = Button {
        text: "Gravar"
        translateX: bind posX + 200
        translateY: bind posY + 580
        layoutInfo: LayoutInfo{width: 150, height: 20}
        cursor: Cursor.HAND
        action: function () {
            if (validate()) {
                submit();
            }
        }
    }

    function validate(): Boolean {
        var ret: Boolean = true;
        alertText = "";

        if (txtTexto.text.trim().equals("")) {
            alertText += "Informe o texto.";
            ret = false;
        }

        return ret;
    }

    function submit() {
        var map: Map = new HashMap();
        map.put("id", (cmbAcao.selectedItem as Conteudo).getId());
        txtTexto.commit();
        map.put("text", txtTexto.text);

        var ret: Map = model.gravar(map);

        if (ret.get("error") != null) {
            alertText = ret.get("error") as String;
        } else {

            if (foto.getId() == null) {
                if (fotoAnterior.getId() != null) {
                    model.excluirFoto(String.valueOf(fotoAnterior.getId()));
                }
                model.gravarImagem(contentShortcut, foto);
            }

            alertText = "Conteúdo gravado corretamente."
        }

    }

    function carregarConteudo(id: String) {

        var ret: Map = model.obterConteudo(id);

        if (ret.get("id") != null) {
            contentShortcut = ret.get("shortcut") as String;

            txtTexto.text = ret.get("text") as String;
            var imgRet: Map = model.obterFotoPrincipal(contentShortcut);
            if (imgRet.get("id") != null and not imgRet.get("id").equals("null")) {
                foto = new Imagem();
                foto.setId(Long.valueOf(imgRet.get("id") as String));

                var path: String = "http://{SessionInfo.getInstance().getDomain()}/{imgRet.get("shortcut")}";
                foto.setCaminhoArquivo(path);

                fotoAnterior = foto;
                imageView.image = Image {url: path};
            } else {
                imageView.image = Image {url: "{rootFolder}/resources/adicionar.jpg"}
            }

        } else {
            alertText = ret.get("error") as String;
        }

    }

    var mainGroup: Group = Group {
        content: [title, alert, cmbAcao, txtTexto, imageView, cmdGravar, lineDiv, lineFooter]
    }

    function carregarListaConteudo() {
        var list: Map = model.listarConteudos();

        cmbAcao.items = [];

        for (contentId in list.keySet()) {
            insert new Conteudo(contentId as String, list.get(contentId) as String) into cmbAcao.items;
        }
    }

    override function create() : Node {
        carregarListaConteudo();

        mainGroup
    }
}
