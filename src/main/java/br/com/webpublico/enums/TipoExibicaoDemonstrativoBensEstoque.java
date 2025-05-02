package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoExibicaoDemonstrativoBensEstoqueDTO;

public enum TipoExibicaoDemonstrativoBensEstoque implements EnumComDescricao {
    TIPO_GRUPO("Tipo de Estoque > Grupo de Material", TipoExibicaoDemonstrativoBensEstoqueDTO.TIPO_GRUPO),
    TIPO_GRUPO_OPERACAO("Tipo de Estoque > Grupo de Material > Operação", TipoExibicaoDemonstrativoBensEstoqueDTO.TIPO_GRUPO_OPERACAO),
    GRUPO_OPERACAO("Grupo de Material > Operação", TipoExibicaoDemonstrativoBensEstoqueDTO.GRUPO_OPERACAO),
    OPERACAO("Operação", TipoExibicaoDemonstrativoBensEstoqueDTO.OPERACAO);
    private String descricao;
    private TipoExibicaoDemonstrativoBensEstoqueDTO toDto;

    TipoExibicaoDemonstrativoBensEstoque(String descricao, TipoExibicaoDemonstrativoBensEstoqueDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoExibicaoDemonstrativoBensEstoqueDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString(){
        return descricao;
    }

    public boolean isExibirTipo() {
        return TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO.equals(this)
            || TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO_OPERACAO.equals(this);
    }

    public boolean isExibirGrupo() {
        return TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO.equals(this)
            || TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO_OPERACAO.equals(this)
            || TipoExibicaoDemonstrativoBensEstoque.GRUPO_OPERACAO.equals(this);
    }

    public boolean isExibirOperacao() {
        return TipoExibicaoDemonstrativoBensEstoque.TIPO_GRUPO_OPERACAO.equals(this)
            || TipoExibicaoDemonstrativoBensEstoque.GRUPO_OPERACAO.equals(this)
            || TipoExibicaoDemonstrativoBensEstoque.OPERACAO.equals(this);
    }
}
