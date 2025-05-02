package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

public class ObjetoContrato {

    private Licitacao licitacao;
    private DispensaDeLicitacao dispensaDeLicitacao;
    private ParticipanteIRP participanteIRP;
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;
    private RegistroSolicitacaoMaterialExterno registroPrecoExterno;

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroPrecoExterno() {
        return registroPrecoExterno;
    }

    public void setRegistroPrecoExterno(RegistroSolicitacaoMaterialExterno registroPrecoExterno) {
        this.registroPrecoExterno = registroPrecoExterno;
    }
}
