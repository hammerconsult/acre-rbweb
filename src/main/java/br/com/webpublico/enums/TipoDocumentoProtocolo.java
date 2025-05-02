package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum TipoDocumentoProtocolo {

    CAPA_PROCESSO("Capa do Processo"),
    CAPA_PROTOCOLO("Capa do Protocolo"),
    TRAMITE_PROCESSO("Trâmite do Processo"),
    PROCESSO_WEB("Processo Web"),
    PROTOCOLO_WEB("Protocolo Web");

    private TipoDocumentoProtocolo(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
