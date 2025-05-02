package br.com.webpublico.enums.rh;

/**
 * @Author peixe on 27/10/2016  16:33.
 */
public enum TipoFuncionalidadeRH {

    LICENCA_PREMIO("Licença Prêmio");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    TipoFuncionalidadeRH(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
