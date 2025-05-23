/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoNaturezaDoProcedimentoLicitacaoDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author lucas
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoNaturezaDoProcedimentoLicitacao implements EnumComDescricao {
    PRESENCIAL("Presencial", TipoNaturezaDoProcedimentoLicitacaoDTO.PRESENCIAL),
    ELETRONICO("Eletrônico", TipoNaturezaDoProcedimentoLicitacaoDTO.ELETRONICO),
    REGISTRO_DE_PRECOS("Registro de Preços", TipoNaturezaDoProcedimentoLicitacaoDTO.REGISTRO_DE_PRECOS),
    NORMAL("Normal", TipoNaturezaDoProcedimentoLicitacaoDTO.NORMAL),
    CREDENCIAMENTO("Credenciamento", TipoNaturezaDoProcedimentoLicitacaoDTO.CREDENCIAMENTO),
    ABERTO("Aberto", TipoNaturezaDoProcedimentoLicitacaoDTO.ABERTO),
    FECHADO("Fechado", TipoNaturezaDoProcedimentoLicitacaoDTO.FECHADO),
    COMBINADO("Combinado", TipoNaturezaDoProcedimentoLicitacaoDTO.COMBINADO),
    PRESENCIAL_COM_REGISTRO_DE_PRECO("Presencial com Registro de Preço", TipoNaturezaDoProcedimentoLicitacaoDTO.PRESENCIAL_COM_REGISTRO_DE_PRECO),
    ELETRONICO_COM_REGISTRO_DE_PRECO("Eletrônico com Registro de Preço", TipoNaturezaDoProcedimentoLicitacaoDTO.ELETRONICO_COM_REGISTRO_DE_PRECO),
    ABERTA_COM_REGISTRO_DE_PRECO("Aberta com Registro de Preço", TipoNaturezaDoProcedimentoLicitacaoDTO.ABERTA_COM_REGISTRO_DE_PRECO),
    FECHADA_COM_REGISTRO_DE_PRECO("Fechada com Registro de Preço", TipoNaturezaDoProcedimentoLicitacaoDTO.FECHADA_COM_REGISTRO_DE_PRECO),
    COMBINADO_COM_REGISTRO_DE_PRECO("Combinado com Registro de Preço", TipoNaturezaDoProcedimentoLicitacaoDTO.COMBINADO_COM_REGISTRO_DE_PRECO),
    NAO_APLICAVEL("Não Aplicável", TipoNaturezaDoProcedimentoLicitacaoDTO.NAO_APLICAVEL);
    private String descricao;
    private TipoNaturezaDoProcedimentoLicitacaoDTO dto;

    TipoNaturezaDoProcedimentoLicitacao(String descricao, TipoNaturezaDoProcedimentoLicitacaoDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoNaturezaDoProcedimentoLicitacaoDTO getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<String> getTiposNaturezaAsString(List<TipoNaturezaDoProcedimentoLicitacao> tiposNatureza) {
        List<String> toReturn = Lists.newArrayList();
        for (TipoNaturezaDoProcedimentoLicitacao tipo : tiposNatureza) {
            toReturn.add(tipo.name());
        }
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getTiposNaturezaProcedimento(ModalidadeLicitacao modalidade) {
        List<TipoNaturezaDoProcedimentoLicitacao> toReturn = Lists.newArrayList();
        if (modalidade != null) {
            switch (modalidade) {
                case PREGAO:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO);
                    break;
                case RDC:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO);
                    break;
                case CONCORRENCIA:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.NORMAL);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.CREDENCIAMENTO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.REGISTRO_DE_PRECOS);
                    break;
                case INEXIGIBILIDADE:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.NORMAL);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.CREDENCIAMENTO);
                    break;
                case CREDENCIAMENTO:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.CREDENCIAMENTO);
                    break;
                case LEILAO:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO);
                    break;
                case DISPENSA_LICITACAO:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL);
                    break;
                default:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL);
            }
        }
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getTiposNaturezaProcedimentoRegistroPrecoExterno(ModalidadeLicitacao modalidade) {
        List<TipoNaturezaDoProcedimentoLicitacao> toReturn = Lists.newArrayList();
        if (modalidade != null) {
            switch (modalidade) {
                case PREGAO:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO);
                    break;
                case RDC:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO);
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO);
                    break;
                default:
                    toReturn.add(TipoNaturezaDoProcedimentoLicitacao.REGISTRO_DE_PRECOS);
            }
        }
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getTiposDeNaturezaDeRegistroDePreco() {
        List toReturn = Lists.newArrayList();
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.REGISTRO_DE_PRECOS);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO);
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getNaturezaTipoRDCPregao() {
        List toReturn = Lists.newArrayList();
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO);
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getNaturezaTipoRDCCertame() {
        List toReturn = Lists.newArrayList();
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO);
        return toReturn;
    }

    public static List<TipoNaturezaDoProcedimentoLicitacao> getNaturezaRDCAndPregao() {
        List toReturn = Lists.newArrayList();
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO);
        toReturn.add(TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO);
        return toReturn;
    }

    public boolean isEletronico() {
        return ELETRONICO.equals(this) || ELETRONICO_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNormal() {
        return TipoNaturezaDoProcedimentoLicitacao.NORMAL.equals(this);
    }

    public boolean isNaoAplicavel() {
        return TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL.equals(this);
    }

    public boolean isCredenciamento() {
        return TipoNaturezaDoProcedimentoLicitacao.CREDENCIAMENTO.equals(this);
    }

    public boolean isRegistroPreco() {
        return TipoNaturezaDoProcedimentoLicitacao.REGISTRO_DE_PRECOS.equals(this);
    }

    public boolean isPresencial() {
        return TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL.equals(this);
    }

    public boolean isNaturezaProcedimentoPresencialComRegistroPreco() {
        return TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNaturezaProcedimentoEletronicoComRegistroPreco() {
        return TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNaturezaProcedimentoRDCComRegistroPreco() {
        return TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO.equals(this)
            || TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO.equals(this)
            || TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNaturezaProcedimentoRDCFechado() {
        return TipoNaturezaDoProcedimentoLicitacao.FECHADO.equals(this)
            || TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNaturezaProcedimentoRDCAberto() {
        return TipoNaturezaDoProcedimentoLicitacao.ABERTO.equals(this)
            || TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO.equals(this);
    }

    public boolean isNaturezaProcedimentoRDCCombinado() {
        return TipoNaturezaDoProcedimentoLicitacao.COMBINADO.equals(this)
            || TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO.equals(this);
    }

}
