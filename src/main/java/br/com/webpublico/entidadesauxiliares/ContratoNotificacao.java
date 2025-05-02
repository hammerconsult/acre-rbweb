package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeOrganizacional;

public class ContratoNotificacao {

    private Long idContrato;
    private String msgNotificacao;
    private UnidadeOrganizacional unidadeAdminisrativa;

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getMsgNotificacao() {
        return msgNotificacao;
    }

    public void setMsgNotificacao(String msgNotificacao) {
        this.msgNotificacao = msgNotificacao;
    }

    public UnidadeOrganizacional getUnidadeAdminisrativa() {
        return unidadeAdminisrativa;
    }

    public void setUnidadeAdminisrativa(UnidadeOrganizacional unidadeAdminisrativa) {
        this.unidadeAdminisrativa = unidadeAdminisrativa;
    }
}
