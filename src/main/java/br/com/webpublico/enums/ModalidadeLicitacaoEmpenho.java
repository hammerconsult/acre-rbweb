/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.contabil.ModalidadeLicitacaoEmpenhoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum ModalidadeLicitacaoEmpenho {

    CONVITE("Convite", ModalidadeLicitacaoEmpenhoDTO.CONVITE),
    TOMADA_PRECO("Tomada de Preço", ModalidadeLicitacaoEmpenhoDTO.TOMADA_PRECO),
    CONCORRENCIA("Concorrência", ModalidadeLicitacaoEmpenhoDTO.CONCORRENCIA),
    CONCURSO("Concurso", ModalidadeLicitacaoEmpenhoDTO.CONCURSO),
    LEILAO("Leilão", ModalidadeLicitacaoEmpenhoDTO.LEILAO),
    PREGAO("Pregão eletrônico", ModalidadeLicitacaoEmpenhoDTO.PREGAO),
    PREGAO_PRESENCIAL("Pregão presencial", ModalidadeLicitacaoEmpenhoDTO.PREGAO_PRESENCIAL),
    DISPENSA_LICITACAO("Dispensa por valor", ModalidadeLicitacaoEmpenhoDTO.DISPENSA_LICITACAO),
    DISPENSA_EXCETO_VALOR("Dispensa exceto valor", ModalidadeLicitacaoEmpenhoDTO.DISPENSA_EXCETO_VALOR),
    INEXIGIBILIDADE("Inexigibilidade", ModalidadeLicitacaoEmpenhoDTO.INEXIGIBILIDADE),
    RDC("Regime Diferenciado de Contratações – RDC", ModalidadeLicitacaoEmpenhoDTO.RDC),
    ADESAO("Adesão à registro de preço", ModalidadeLicitacaoEmpenhoDTO.ADESAO),
    SEM_LICITACAO("Sem Licitação", ModalidadeLicitacaoEmpenhoDTO.SEM_LICITACAO);
    private String descricao;
    private ModalidadeLicitacaoEmpenhoDTO toDto;

    ModalidadeLicitacaoEmpenho(String descricao, ModalidadeLicitacaoEmpenhoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModalidadeLicitacaoEmpenhoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static ModalidadeLicitacaoEmpenho getModalidadeLicitacaoEmpenhoPelaModalidadeLicitacao(ModalidadeLicitacao modalidade,TipoNaturezaDoProcedimentoLicitacao natureza) {
        switch (modalidade) {
            case CONCORRENCIA:
                return ModalidadeLicitacaoEmpenho.CONCORRENCIA;
            case CONCURSO:
                return ModalidadeLicitacaoEmpenho.CONCURSO;
            case CONVITE:
                return ModalidadeLicitacaoEmpenho.CONVITE;
            case TOMADA_PRECO:
                return ModalidadeLicitacaoEmpenho.TOMADA_PRECO;
            case DISPENSA_LICITACAO:
                return ModalidadeLicitacaoEmpenho.DISPENSA_LICITACAO;
            case RDC:
                return ModalidadeLicitacaoEmpenho.RDC;
            case INEXIGIBILIDADE:
                return ModalidadeLicitacaoEmpenho.INEXIGIBILIDADE;
            case PREGAO: {
                if (natureza != null) {
                    switch (natureza) {
                        case PRESENCIAL:
                        case PRESENCIAL_COM_REGISTRO_DE_PRECO:
                            return ModalidadeLicitacaoEmpenho.PREGAO_PRESENCIAL;
                        case ELETRONICO:
                        case ELETRONICO_COM_REGISTRO_DE_PRECO:
                            return ModalidadeLicitacaoEmpenho.PREGAO;
                    }
                }
                return ModalidadeLicitacaoEmpenho.PREGAO_PRESENCIAL;
            }
            default:
                return ModalidadeLicitacaoEmpenho.SEM_LICITACAO;
        }
    }

    public static List<ModalidadeLicitacaoEmpenho> getModalidadesParaProcessoDeLicitacao() {
        List<ModalidadeLicitacaoEmpenho> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacaoEmpenho.CONVITE);
        modalidades.add(ModalidadeLicitacaoEmpenho.TOMADA_PRECO);
        modalidades.add(ModalidadeLicitacaoEmpenho.CONCORRENCIA);
        modalidades.add(ModalidadeLicitacaoEmpenho.CONCURSO);
        modalidades.add(ModalidadeLicitacaoEmpenho.PREGAO);
        modalidades.add(ModalidadeLicitacaoEmpenho.RDC);
        return modalidades;
    }

    public static List<ModalidadeLicitacaoEmpenho> getModalidadesParaProcessoDeDispensa() {
        List<ModalidadeLicitacaoEmpenho> modalidades = new ArrayList<>();
        modalidades.add(ModalidadeLicitacaoEmpenho.DISPENSA_LICITACAO);
        modalidades.add(ModalidadeLicitacaoEmpenho.INEXIGIBILIDADE);
        return modalidades;
    }
}
