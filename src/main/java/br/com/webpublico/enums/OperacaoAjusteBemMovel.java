package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.OperacaoAjusteBemMovelDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mga on 06/03/2018.
 */
public enum OperacaoAjusteBemMovel {
    AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO("Ajuste de Bens Móveis - Original - Aumentativo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO),
    AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO("Ajuste de Bens Móveis - Original - Diminutivo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO),
    AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste de Bens Móveis - Original - Aumentativo - Empresa Pública", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA),
    AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA("Ajuste de Bens Móveis - Original - Diminutivo - Empresa Pública", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA),

    AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO("Ajuste de Bens Móveis - Depreciação - Aumentativo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO),
    AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO("Ajuste de Bens Móveis - Depreciação - Diminutivo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO),

    AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO("Ajuste de Bens Móveis - Amortização  - Aumentativo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO),
    AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO("Ajuste de Bens Móveis - Amortização  - Diminutivo", OperacaoAjusteBemMovelDTO.AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO);
    private String descricao;
    private OperacaoAjusteBemMovelDTO toDto;

    OperacaoAjusteBemMovel(String descricao, OperacaoAjusteBemMovelDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public OperacaoAjusteBemMovelDTO getToDto() {
        return toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesPorTipoAjuste(TipoAjusteBemMovel tipoAjusteBemMovel) {
        switch (tipoAjusteBemMovel) {
            case ORIGINAL:
                return retornaOperacoesOriginal();
            case DEPRECIACAO:
                return retornaOperacoesDepreciacao();
            case AMORTIZACAO:
                return retornaOperacoesAmortizacao();
            default:
                return Lists.newArrayList();
        }
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesOriginal() {
        List<OperacaoAjusteBemMovel> list = Lists.newArrayList();
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO);
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO);
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA);
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA);
        return list;
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesDepreciacao() {
        List<OperacaoAjusteBemMovel> list = Lists.newArrayList();
        list.add(AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO);
        list.add(AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO);
        return list;
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesAmortizacao() {
        List<OperacaoAjusteBemMovel> list = Lists.newArrayList();
        list.add(AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO);
        list.add(AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO);
        return list;
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesAumentativo() {
        List<OperacaoAjusteBemMovel> list = Lists.newArrayList();
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO);
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA);
        list.add(AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO);
        list.add(AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO);
        return list;
    }

    public static List<OperacaoAjusteBemMovel> retornaOperacoesDiminutivo() {
        List<OperacaoAjusteBemMovel> list = Lists.newArrayList();
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO);
        list.add(AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA);
        list.add(AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO);
        list.add(AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO);
        return list;
    }

    public boolean isAumentativoOriginal() {
        return AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO.equals(this)
            || AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA.equals(this);
    }

    public boolean isDiminutivoOriginal() {
        return AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO.equals(this)
            || AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA.equals(this);
    }

    public boolean isAumentativoDepreciacao() {
        return AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO.equals(this);
    }

    public boolean isDiminutivoDepreciacao() {
        return AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO.equals(this);
    }

    public boolean isAumentativoAmortizacao() {
        return AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO.equals(this);
    }

    public boolean isDiminutivoAmortizacao() {
        return AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO.equals(this);
    }
}
