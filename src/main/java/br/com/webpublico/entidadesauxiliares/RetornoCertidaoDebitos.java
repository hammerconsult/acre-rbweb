package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.SolicitacaoDocumentoOficialDTO;

public class RetornoCertidaoDebitos {
    private String mensagem;
    private SolicitacaoDocumentoOficialDTO solicitacaoDocumentoOficial;

    public RetornoCertidaoDebitos() {
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public SolicitacaoDocumentoOficialDTO getSolicitacaoDocumentoOficial() {
        return solicitacaoDocumentoOficial;
    }

    public void setSolicitacaoDocumentoOficial(SolicitacaoDocumentoOficialDTO solicitacaoDocumentoOficial) {
        this.solicitacaoDocumentoOficial = solicitacaoDocumentoOficial;
    }
}
