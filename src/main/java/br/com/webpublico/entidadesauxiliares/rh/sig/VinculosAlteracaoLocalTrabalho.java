package br.com.webpublico.entidadesauxiliares.rh.sig;

import br.com.webpublico.entidades.AlteracaoVinculoLotacao;

public class VinculosAlteracaoLocalTrabalho {
    private String motivoRejeicao;
    private String descricaoVinculo;
    private AlteracaoVinculoLotacao alteracaoVinculoLotacao;

    public VinculosAlteracaoLocalTrabalho(AlteracaoVinculoLotacao alteracaoVinculoLotacao, String motivoRejeicao, String descricaoVinculo) {
        this.alteracaoVinculoLotacao = alteracaoVinculoLotacao;
        this.motivoRejeicao = motivoRejeicao;
        this.descricaoVinculo = descricaoVinculo;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getDescricaoVinculo() {
        return descricaoVinculo;
    }

    public void setDescricaoVinculo(String descricaoVinculo) {
        this.descricaoVinculo = descricaoVinculo;
    }

    public AlteracaoVinculoLotacao getAlteracaoVinculoLotacao() {
        return alteracaoVinculoLotacao;
    }

    public void setAlteracaoVinculoLotacao(AlteracaoVinculoLotacao alteracaoVinculoLotacao) {
        this.alteracaoVinculoLotacao = alteracaoVinculoLotacao;
    }
}
