package br.com.technearquitetura;

import br.com.manish.ahy.client.WSUtil;
import br.com.manish.ahy.fxadmin.FileUtil;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjetoCadastroModel {

    public Map<String, String> gravar(Map<String, String> parameters) {
        Map<String, String> ret = new HashMap<String, String>();

        try {

            if (parameters.get("id") == null) {
                parameters.put("id", "null");
            }

            ret = WSUtil.callMapWS("techne", "saveProject", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }


    public Map<String, String> listarProjetos() {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            ret = WSUtil.callMapWS("techne", "getProjectList", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> listarImagens(Long id) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", String.valueOf(id));

            ret = WSUtil.callMapWS("techne", "getImageList", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }


    public Map<String, String> obterProjeto(Long id) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", String.valueOf(id));
            ret = WSUtil.callMapWS("techne", "getProject", parameters);
        } catch(Exception e) {
            ret.put("error", e.getMessage());
        }
        return ret;
    }

    public Map<String, String> gravarImagens(String projetoID, List<Imagem> imageList) {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            Map<String, String> parameters = new HashMap<String, String>();

            for (Imagem foto: imageList) {
                Map<String, String> imgRet;

                String path = foto.getCaminhoArquivo();
                String fileName = "";
                System.out.println("file " + path);
                if (path.startsWith("file:")) {
                    File f = new File(new URL(path).getPath());

                    Integer packSize = 4064;
                    Long fileSize = f.length();
                    Integer fileIndex = 0;

                    fileName = f.getName();
                    System.out.println("file " + fileName);

                    while (fileIndex < fileSize) {
                        byte[] buffer = FileUtil.readFileAsBytes(f.getPath(), fileIndex, packSize);
                        parameters.put("name", fileName);
                        parameters.put("buffer", FileUtil.getHex(buffer));
                        imgRet = WSUtil.callMapWS("techne", "uploadImage", parameters);

                        fileIndex += packSize;
                        System.out.println("fileIndex" + fileIndex);

                    }

                }

                parameters = new HashMap<String, String>();
                parameters.put("name", fileName);
                parameters.put("id", projetoID);
                if (foto.getId() != null) {
                    parameters.put("resourceId", String.valueOf(foto.getId()));
                }
                parameters.put("resourceLabel", foto.getLegenda());

                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    parameters.put("resourceType", "image/jpeg");

                } else if (fileName.endsWith(".png")) {
                    parameters.put("resourceType", "image/png");
                }

                imgRet = WSUtil.callMapWS("techne", "saveImage", parameters);

                System.out.println("foi.");

            }

        } catch(Exception e) {
            e.printStackTrace();
            ret.put("error", e.getMessage());
        }
        return ret;
    }

}
