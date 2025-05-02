package br.com.webpublico.enums.rh.previdencia;

public enum TipoSituacaoBBPrev {
    LICENCA_SAUDE("02", "Licença Saúde"),
    CONTRATO_SUSPENSO("03", "Contrato Suspenso"),
    PARTICIPANTE_EXTERIOR("04", "Participante no Exterior"),
    LICENCA_SEM_REMUNERACAO("06", "Licença Sem Remuneração"),
    LICENCA_MATERNIDADE("07", "Licença Maternidade"),
    LICENCA_RECLUSAO("08", "Licença Reclusão");

    private String codigo;
    private String descricao;

    TipoSituacaoBBPrev(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
