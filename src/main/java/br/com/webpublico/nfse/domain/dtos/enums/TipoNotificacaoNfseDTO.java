package br.com.webpublico.nfse.domain.dtos.enums;

public enum TipoNotificacaoNfseDTO {
    CANCELAMENTO_NFS_ELETRONICA("Cancelamento de Nota Fiscal Eletrônica"),
    RECLAMACAO_NOTA_PREMIADA("Reclamações Portal da Nota Premiada"),
    FALE_CONOSCO_PORTAL_NFSE("Fale Conosco (Nfs-e)");

    private String descricao;

    TipoNotificacaoNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
