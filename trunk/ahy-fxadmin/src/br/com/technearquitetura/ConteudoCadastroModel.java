package br.com.technearquitetura;

import br.com.manish.ahy.client.WSUtil;
import br.com.manish.ahy.fxadmin.FileUtil;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ConteudoCadastroModel {

    public Map<String, String> gravar(Map<String, String> parameters) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            ret = WSUtil.callMapWS("techne", "saveContent", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }


    public Map<String, String> listarConteudos() {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            ret = WSUtil.callMapWS("techne", "getContentList", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> obterConteudo(String id) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", id);
            ret = WSUtil.callMapWS("techne", "getContent", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> gravarImagem(String contentShortcut, Imagem foto) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();

            String path = foto.getCaminhoArquivo();
            String fileName = "";

            if (path.startsWith("file:")) {
                File f = new File(new URL(path).getPath());

                Integer packSize = 4064;
                Long fileSize = f.length();
                Integer fileIndex = 0;

                fileName = f.getName();

                while (fileIndex < fileSize) {
                    byte[] buffer = FileUtil.readFileAsBytes(f.getPath(), fileIndex, packSize);
                    parameters.put("name", fileName);
                    parameters.put("buffer", FileUtil.getHex(buffer));
                    ret = WSUtil.callMapWS("techne", "uploadImage", parameters);
                    fileIndex += packSize;
                }

            }

            parameters = new HashMap<String, String>();
            parameters.put("name", fileName);
            parameters.put("shortcut", contentShortcut);
            
            parameters.put("resourceLabel", "interna.");

            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                parameters.put("resourceType", "image/jpeg");

            } else if (fileName.endsWith(".png")) {
                parameters.put("resourceType", "image/png");
            }

            ret = WSUtil.callMapWS("techne", "saveImage", parameters);

        } catch(Exception e) {
            e.printStackTrace();
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> obterFotoPrincipal(String shortcut) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("shortcut", shortcut);
            ret = WSUtil.callMapWS("techne", "getFirstImage", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> excluirFoto(String id) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", id);
            ret = WSUtil.callMapWS("techne", "removeImage", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }
}
