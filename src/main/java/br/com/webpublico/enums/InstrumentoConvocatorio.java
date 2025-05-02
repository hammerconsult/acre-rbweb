package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum InstrumentoConvocatorio implements EnumComDescricao {
    EDITAL(1, "Edital"),
    AVISO_CONTRATACAO_DIRETA(2, "Aviso de Contratação Direta"),
    ATO_AUTORIZA_CONTRATACAO_DIRETA(3, "Ato que Autoriza a Contratação Direta");

    private Integer codigo;
    private String descricao;

    private InstrumentoConvocatorio(Integer codigo, String descricao) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public static InstrumentoConvocatorio getTipoInstrumentoSolicitacaoCompra(ModalidadeLicitacao modalidade, ModoDisputa modoDisputa) {
        switch (modalidade) {
            case DISPENSA_LICITACAO:
                if (modoDisputa.isDispensaComDisputa()) {
                    return InstrumentoConvocatorio.AVISO_CONTRATACAO_DIRETA;
                }
                return InstrumentoConvocatorio.ATO_AUTORIZA_CONTRATACAO_DIRETA;
            case INEXIGIBILIDADE:
                return InstrumentoConvocatorio.ATO_AUTORIZA_CONTRATACAO_DIRETA;
            default:
                return InstrumentoConvocatorio.EDITAL;
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
