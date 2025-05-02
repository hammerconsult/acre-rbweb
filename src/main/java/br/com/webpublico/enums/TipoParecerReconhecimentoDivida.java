package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoParecerReconhecimentoDivida implements EnumComDescricao {

    COMUNICADO_INSTAURACAO_PAD_CGM("Comunicado de Instauração de PAD a CGM"),
    PARECER_DO_CONTROLE_INTERNO("Parecer do Controle Interno"),
    PARECER_DA_PGM("Parecer da PGM"),
    PUBLICACAO("Publicação"),
    TERMO_DE_RECONHECIMENTO_DE_DIVIDA("Termo de Reconhecimento de Dívida"),
    OUTROS("Outros");

    private String descricao;

    TipoParecerReconhecimentoDivida(String descricao) {
        this.descricao = descricao;
    }

    public boolean isParecerDoControleInterno() {
        return PARECER_DO_CONTROLE_INTERNO.equals(this);
    }

    public boolean isParecerDaPGM() {
        return PARECER_DA_PGM.equals(this);
    }

    public boolean isComunicadoInstauracaoPADCGM() {
        return COMUNICADO_INSTAURACAO_PAD_CGM.equals(this);
    }

    public boolean isTermoDeReconhecimento() {
        return TERMO_DE_RECONHECIMENTO_DE_DIVIDA.equals(this);
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
