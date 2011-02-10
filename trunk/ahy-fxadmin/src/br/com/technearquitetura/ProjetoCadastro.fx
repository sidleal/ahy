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
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TextBox;
import javafx.scene.Cursor;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import java.util.HashMap;
import java.util.Map;
import br.com.manish.ahy.client.KeyValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;
import java.lang.System;
import br.com.manish.ahy.client.SessionInfo;
import br.com.manish.ahy.fxadmin.AsyncTask;

public class ProjetoCadastro extends CustomNode {
    var height: Number = 600;
    var width: Number = 900;

    var posX: Number = 50;
    var posY: Number = 100;
    var model: ProjetoCadastroModel = new ProjetoCadastroModel();

    var title: Text = Text {
        content: bind "Cadastro de projetos"
        layoutX: bind posX
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
        layoutInfo: LayoutInfo{width: 350, height: 90}
        vertical: true
        items: []
        onMouseReleased: function (me: MouseEvent) {
            clean();
            if ((cmbAcao.selectedItem as Projeto).getId() != null) {
                cmbAcao.cursor = Cursor.WAIT;
                //AsyncTask {
                //   run: function() {
                       carregarProjeto((cmbAcao.selectedItem as Projeto).getId());
                 //  }
                  // onDone: function() {
                       cmbAcao.cursor = Cursor.DEFAULT;
                  // }
                //}.start();
            }
        }
    }
    
    var txtNome:TextBox = TextBox {
        promptText: "Nome do Projeto";
        layoutInfo: LayoutInfo{width: 350, height: 20}
        translateX: bind posX
        translateY: bind posY + 140
    }

    var txtDescricao:TextBox = TextBox {
        promptText: "Descrição do Projeto.";
        layoutInfo: LayoutInfo{width: 350, height: 100}
        translateX: bind posX
        translateY: bind posY + 170
        multiline: true
    }

    var txtFicha:TextBox = TextBox {
        promptText: "Ficha Técnica";
        layoutInfo: LayoutInfo{width: 350, height: 60}
        translateX: bind posX
        translateY: bind posY + 280
        multiline: true
    }

    var cmbSituacao: ChoiceBox = ChoiceBox {
        translateX: bind posX
        translateY: bind posY + 350
        items: []
    }


    var cmbTipo: ChoiceBox = ChoiceBox {
        translateX: bind posX
        translateY: bind posY + 380
        items: []
    }

    var lineDiv: Line = Line {
        startX: bind posX, startY: bind posY + 410
        endX: bind posX + 350, endY: bind posY + 410
        strokeWidth: 1
        stroke: Color.GRAY
    };

    var lineFooter: Line = Line {
        startX: bind posX, startY: bind posY + 470
        endX: bind posX + 350, endY: bind posY + 470
        strokeWidth: 1
        stroke: Color.GRAY
    };

    var cmdGravar : Button = Button {
        text: "Gravar"
        translateX: bind posX + 100
        translateY: bind posY + 430
        layoutInfo: LayoutInfo{width: 150, height: 20}
        cursor: Cursor.HAND
        action: function () {
            if (validate()) {
                submit();
            }
        }
    }

    var fotoCad: ProjetoFotoCadastro = ProjetoFotoCadastro {
        posX: bind posX + 400
        posY: bind posY + 40
    }


    function validate(): Boolean {
        var ret: Boolean = true;
        alertText = "";

        if (txtNome.text.trim().equals("")) {
            alertText += "Informe nome do projeto.";
            ret = false;
        }

        if (txtDescricao.text.trim().equals("")) {
            alertText += "Informe uma descrição.";
            ret = false;
        }

        //if (txtFicha.text.trim().equals("")) {
        //    alertText += "Informe a ficha técnica.";
        //    ret = false;
        //}

        if ((cmbSituacao.selectedItem as KeyValue).toString().equals("")) {
            alertText += "Informe a situação.";
            ret = false;
        }

        if ((cmbTipo.selectedItem as KeyValue).toString().equals("")) {
            alertText += "Informe o tipo.";
            ret = false;
        }

        return ret;
    }

    function clean(): Void {
        txtNome.text = "";
        txtDescricao.text = "";
        txtFicha.text = "";
        cmbSituacao.clearSelection();
        fotoCad.list = new ArrayList();
        cmbTipo.clearSelection();
    }

    function submit() {
        var map: Map = new HashMap();
        map.put("id", String.valueOf((cmbAcao.selectedItem as Projeto).getId()));
        map.put("title", txtNome.text);
        map.put("teaser", txtFicha.text);
        map.put("text", txtDescricao.text);
        map.put("status", (cmbSituacao.selectedItem as KeyValue).getKey());
        map.put("type", (cmbTipo.selectedItem as KeyValue).getKey());

        var ret: Map = model.gravar(map);

        if (ret.get("id") != null) {

            System.out.println("vai gravar imgs {fotoCad.list.size()}");

            var imgRet: Map = model.gravarImagens(ret.get("id") as String, fotoCad.list);
            if (imgRet.get("error") != null) {
                alertText = "Erro ao gravar as imagens.";
            } else {
                alertText = "Projeto foi gravado corretamente.";
            }
            //var id: Long = Long.valueOf(ret.get("id") as String);
            clean();
            carregarListaProjetos();

        } else {
            alertText = ret.get("error") as String;
        }

    }

    function carregarProjeto(id: Long) {

        var ret: Map = model.obterProjeto(id);

        if (ret.get("id") != null) {

            txtNome.text = ret.get("title") as String;
            txtFicha.text = ret.get("teaser") as String;
            txtDescricao.text = ret.get("text") as String;

            var i = 0;
            for (item in cmbSituacao.items) {
                if ((item as KeyValue).getKey().equals(ret.get("status"))) {
                    cmbSituacao.select(i);
                }
                i++;
            }

            i = 0;
            for (item in cmbTipo.items) {
                if ((item as KeyValue).getKey().equals(ret.get("type"))) {
                    cmbTipo.select(i);
                }
                i++;
            }

           // AsyncTask {
           //    run: function() {

            var imageList: List = new ArrayList();

            var imgRet: Map = model.listarImagens(id);

            for (imgId in imgRet.keySet()) {
                var foto: Imagem = new Imagem();
                foto.setId(Long.valueOf(imgId as String));
                var imgToken: String[] = (imgRet.get(imgId) as String).split("\\|\\|");

                if (imgToken[1].startsWith("!")) {
                    foto.setShowInFrontPage(true);
                    foto.setLegenda(imgToken[1].substring(1, imgToken[1].length()));
                } else {
                    foto.setShowInFrontPage(false);
                    foto.setLegenda(imgToken[1]);
                }
                
                var path: String = "http://{SessionInfo.getInstance().getDomain()}/{imgToken[0]}";
                foto.setCaminhoArquivo(path);
                imageList.add(foto);
            }
            fotoCad.list = imageList;

            //   }
           //    onDone: function() {
           //    }
          // }.start();


        } else {
            alertText = ret.get("error") as String;
        }

    }

    var mainGroup: Group = Group {
        content: [title, alert, cmbAcao, txtNome, txtDescricao, txtFicha, cmbSituacao,
            cmbTipo, cmdGravar, lineDiv, lineFooter, fotoCad]
    }

    function carregarListaProjetos() {
        var list: Map = model.listarProjetos();

        cmbAcao.items = [];

        insert new Projeto(null, " -- Novo Projeto --") into cmbAcao.items;
        for (projId in list.keySet()) {
            insert new Projeto(Long.valueOf(projId as String), list.get(projId) as String) into cmbAcao.items;
        }
    }

    override function create() : Node {
        insert new KeyValue("RAS", "Rascunho") into cmbSituacao.items;
        insert new KeyValue("LNC", "Lançamento") into cmbSituacao.items;
        insert new KeyValue("NRM", "Normal") into cmbSituacao.items;
        insert new KeyValue("EXC", "Excluir") into cmbSituacao.items;

        insert new KeyValue("RES", "Residenciais") into cmbTipo.items;
        insert new KeyValue("COM", "Comerciais") into cmbTipo.items;
        insert new KeyValue("IND", "Industriais") into cmbTipo.items;

        carregarListaProjetos();

        mainGroup
    }
}
