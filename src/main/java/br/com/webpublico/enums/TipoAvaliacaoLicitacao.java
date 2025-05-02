/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoAvaliacaoLicitacaoDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoAvaliacaoLicitacao implements EnumComDescricao {

    MENOR_PRECO("Menor Preço", 1, TipoAvaliacaoLicitacaoDTO.MENOR_PRECO),
    MAIOR_DESCONTO("Maior Desconto", 2, TipoAvaliacaoLicitacaoDTO.MAIOR_DESCONTO),
    TECNICA_PRECO("Técnica e Preço", 4, TipoAvaliacaoLicitacaoDTO.TECNICA_PRECO),
    MAIOR_LANCE("Maior Lance", 5, TipoAvaliacaoLicitacaoDTO.MAIOR_LANCE),
    MAIOR_RETORNO_ECONOMICO("Maior Retorno Econômico", 6, TipoAvaliacaoLicitacaoDTO.MAIOR_RETORNO_ECONOMICO),
    NAO_SE_APLICA("Não se Aplica", 7, TipoAvaliacaoLicitacaoDTO.NAO_SE_APLICA),
    MELHOR_TECNICA("Melhor Técnica", 8, TipoAvaliacaoLicitacaoDTO.MELHOR_TECNICA),
    CONTEUDO_ARTISTICO("Conteúdo Artístico", 9, TipoAvaliacaoLicitacaoDTO.CONTEUDO_ARTISTICO);
    private String descricao;
    private Integer codigo;

    private TipoAvaliacaoLicitacaoDTO toDto;

    TipoAvaliacaoLicitacao(String descricao, Integer codigo, TipoAvaliacaoLicitacaoDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public TipoAvaliacaoLicitacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isMaiorDesconto() {
        return MAIOR_DESCONTO.equals(this);
    }

    public static List<TipoAvaliacaoLicitacao> getAvaliacoesLei8666(ModalidadeLicitacao modalidade, TipoNaturezaDoProcedimentoLicitacao natureza) {
        List<TipoAvaliacaoLicitacao> toReturn = Lists.newArrayList();
        if (modalidade != null) {
            switch (modalidade) {
                case PREGAO:
                    toReturn.add(MENOR_PRECO);
                    toReturn.add(MAIOR_DESCONTO);
                    break;
                case CONVITE:
                case DISPENSA_LICITACAO:
                case INEXIGIBILIDADE:
                    toReturn.add(MENOR_PRECO);
                    break;
                case TOMADA_PRECO:
                case CONCURSO:
                    toReturn.add(MENOR_PRECO);
                    toReturn.add(MELHOR_TECNICA);
                    toReturn.add(TECNICA_PRECO);
                    break;
                case CONCORRENCIA:
                    toReturn.add(MENOR_PRECO);
                    if (natureza != null && (natureza.isNormal() || natureza.isRegistroPreco())) {
                        toReturn.add(MELHOR_TECNICA);
                        toReturn.add(TECNICA_PRECO);
                        toReturn.add(MAIOR_LANCE);
                    }
                    break;
                case RDC:
                    toReturn.add(MENOR_PRECO);
                    if (natureza != null && natureza.isNaturezaProcedimentoRDCFechado()) {
                        toReturn.add(MELHOR_TECNICA);
                        toReturn.add(TECNICA_PRECO);
                    }
                    break;
                default:
                    toReturn.add(NAO_SE_APLICA);
            }
        }
        return toReturn;
    }

    public static List<TipoAvaliacaoLicitacao> getAvaliacoes(ModalidadeLicitacao modalidade) {
        List<TipoAvaliacaoLicitacao> toReturn = Lists.newArrayList();
        if (modalidade != null) {
            switch (modalidade) {
                case LEILAO:
                    toReturn.add(MAIOR_LANCE);
                    break;
                case DIALOGO_COMPETITIVO:
                case CONCORRENCIA:
                    toReturn.add(MENOR_PRECO);
                    toReturn.add(MAIOR_DESCONTO);
                    toReturn.add(TECNICA_PRECO);
                    toReturn.add(MAIOR_RETORNO_ECONOMICO);
                    toReturn.add(MELHOR_TECNICA);
                    toReturn.add(CONTEUDO_ARTISTICO);
                    break;
                case CONCURSO:
                    toReturn.add(MELHOR_TECNICA);
                    toReturn.add(CONTEUDO_ARTISTICO);
                    break;
                case PREGAO:
                    toReturn.add(MENOR_PRECO);
                    toReturn.add(MAIOR_DESCONTO);
                    break;
                case DISPENSA_LICITACAO:
                    toReturn.add(MENOR_PRECO);
                    toReturn.add(MAIOR_DESCONTO);
                    toReturn.add(NAO_SE_APLICA);
                    break;
                default:
                    toReturn.add(NAO_SE_APLICA);
            }
        }
        return toReturn;
    }

    public static List<TipoAvaliacaoLicitacao> getAvaliacoesLei14133ModalidadeDispensa(ModoDisputa modoDisputa) {
        List<TipoAvaliacaoLicitacao> toReturn = Lists.newArrayList();
        switch (modoDisputa) {
            case NAO_APLICAVEL:
                toReturn.add(NAO_SE_APLICA);
                break;
            case DISPENSA_COM_DISPUTA:
                toReturn.add(MENOR_PRECO);
                toReturn.add(MAIOR_DESCONTO);
                toReturn.add(MAIOR_LANCE);
                break;
        }
        return toReturn;
    }
}
