package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoExtensaoArquivo implements EnumComDescricao {

    PDF("PDF", "application/pdf", "pdf"),
    XLS("XLS", "application/xls", "xls");

    private String descricao;
    private String tipoConteudo;
    private String extensao;

    TipoExtensaoArquivo(String descricao, String tipoConteudo, String extensao) {
        this.descricao = descricao;
        this.tipoConteudo = tipoConteudo;
        this.extensao = extensao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public String getExtensao() {
        return extensao;
    }
}
