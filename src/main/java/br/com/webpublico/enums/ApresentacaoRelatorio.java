package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.ApresentacaoRelatorioDTO;

/**
 * Created by Mateus on 01/04/2015.
 */
public enum ApresentacaoRelatorio {

    CONSOLIDADO("Consolidado", "CSL", ApresentacaoRelatorioDTO.CONSOLIDADO),
    ORGAO("Orgão", "ORG", ApresentacaoRelatorioDTO.ORGAO),
    UNIDADE("Unidade", "UND", ApresentacaoRelatorioDTO.UNIDADE),
    UNIDADE_GESTORA("Unidade Gestora", "UNG", ApresentacaoRelatorioDTO.UNIDADE_GESTORA);
    private String descricao;
    private String sigla;
    private ApresentacaoRelatorioDTO toDto;

    ApresentacaoRelatorio(String descricao, String sigla, ApresentacaoRelatorioDTO toDto) {
        this.sigla = sigla;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public ApresentacaoRelatorioDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isApresentacaoUnidadeGestora() {
        return UNIDADE_GESTORA.equals(this);
    }

    public boolean isApresentacaoUnidade() {
        return UNIDADE.equals(this);
    }

    public boolean isApresentacaoOrgao() {
        return ORGAO.equals(this);
    }

    public boolean isApresentacaoConsolidado() {
        return CONSOLIDADO.equals(this);
    }
}
