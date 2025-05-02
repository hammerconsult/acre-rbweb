package br.com.webpublico.enums.administrativo.frotas;

public enum SituacaoSolicitacaoObjetoFrota {
    AGUARDANDO_RESERVA("Aguardando Reserva"),
    APROVADO("Aprovada"),
    RESERVADO("Reservado"),
    CANCELADA("Cancelada"),
    REJEITADO("Rejeitada");

    private String descricao;

    SituacaoSolicitacaoObjetoFrota(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAprovada() {
        return APROVADO.equals(this);
    }

    public boolean isAguardandoReserva() {
        return SituacaoSolicitacaoObjetoFrota.AGUARDANDO_RESERVA.equals(this);
    }

    public boolean isCancelada() {
        return SituacaoSolicitacaoObjetoFrota.CANCELADA.equals(this);
    }

    public boolean isRejeitada() {
        return SituacaoSolicitacaoObjetoFrota.REJEITADO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
