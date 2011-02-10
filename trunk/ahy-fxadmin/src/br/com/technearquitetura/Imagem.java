package br.com.technearquitetura;

public class Imagem {

    private Long id;
    private String legenda;
    private String caminhoArquivo;
    private Boolean showInFrontPage = false;

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public Boolean getShowInFrontPage() {
        return showInFrontPage;
    }

    public void setShowInFrontPage(Boolean showInFrontPage) {
        this.showInFrontPage = showInFrontPage;
    }
    
}
