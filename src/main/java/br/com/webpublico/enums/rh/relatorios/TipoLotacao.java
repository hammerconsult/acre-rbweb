package br.com.webpublico.enums.rh.relatorios;

/**
 * Created by Edi on 22/03/2016.
 */
public enum TipoLotacao {

    RECURSO_FP("Recurso FP"),
    GRUPO_RECURSO_FP("Grupo de Recurso FP"),
    LOTACAO_FUNCIONAL("Lotação Funcional");
    private String descricao;

    TipoLotacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
