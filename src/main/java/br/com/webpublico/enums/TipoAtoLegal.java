/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.comum.TipoAtoLegalDTO;

import java.util.Arrays;

/**
 * @author andre
 */
public enum TipoAtoLegal implements EnumComDescricao {

    DECRETO("Decreto", TipoAtoLegalDTO.DECRETO),
    PORTARIA("Portaria", TipoAtoLegalDTO.PORTARIA),
    RESOLUCAO("Resolução", TipoAtoLegalDTO.RESOLUCAO),
    CIRCULAR("Circular", TipoAtoLegalDTO.CIRCULAR),
    DESPACHO("Despacho", TipoAtoLegalDTO.DESPACHO),
    EMENDA("Emenda", TipoAtoLegalDTO.EMENDA),
    PROJETO_DE_LEI("Projeto de Lei", TipoAtoLegalDTO.PROJETO_DE_LEI),
    OFICIO("Ofício", TipoAtoLegalDTO.OFICIO),
    PARECER("Parecer", TipoAtoLegalDTO.PARECER),
    PROCESSO("Processo", TipoAtoLegalDTO.PROCESSO),
    ATESTADO("Atestado", TipoAtoLegalDTO.ATESTADO),
    MEMORANDO("Memorando", TipoAtoLegalDTO.MEMORANDO),
    MEDIDA_PROVISORIA("Medidas Provisórias", TipoAtoLegalDTO.MEDIDA_PROVISORIA),
    LEI_COMPLEMENTAR("Lei Complementar", TipoAtoLegalDTO.LEI_COMPLEMENTAR),
    LEI_ORDINARIA("Lei ordinária", TipoAtoLegalDTO.LEI_ORDINARIA),
    LEI_ORGANICA("Lei Orgânica", TipoAtoLegalDTO.LEI_ORGANICA),
    LEI_MUNICIPAL("Lei Municipal", TipoAtoLegalDTO.LEI_MUNICIPAL),
    REGIMENTO_INTERNO("Regimento Interno", TipoAtoLegalDTO.REGIMENTO_INTERNO),
    TERMO_POSSE("Termo de Posse", TipoAtoLegalDTO.TERMO_POSSE),
    ORIENTACAO_TECNICA("Orientação técnica", TipoAtoLegalDTO.ORIENTACAO_TECNICA),
    RECOMENDACAO("Recomendação", TipoAtoLegalDTO.RECOMENDACAO),
    INSTRUCAO_NORMATIVA("Instrução Normativa", TipoAtoLegalDTO.INSTRUCAO_NORMATIVA),
    RELATORIO("Relatório", TipoAtoLegalDTO.RELATORIO),
    AUTOGRAFO("Autógrafo", TipoAtoLegalDTO.AUTOGRAFO),
    LEGISLATIVO("Legislativo", TipoAtoLegalDTO.LEGISLATIVO),
    CONTROLE_EXTERNO("Controle Externo", TipoAtoLegalDTO.CONTROLE_EXTERNO),
    CODIGO_MUNICIPAL("Código Municipal", TipoAtoLegalDTO.CODIGO_MUNICIPAL),
    CODIGO_OBRA_EDIFICACAO("Código de Obras e Edificações", TipoAtoLegalDTO.CODIGO_OBRA_EDIFICACAO),
    CODIGO_POSTURAS("Código de Posturas", TipoAtoLegalDTO.CODIGO_POSTURAS),
    CODIGO_SANITARIO("Código Sanitário", TipoAtoLegalDTO.CODIGO_SANITARIO),
    CODIGO_TRIBUTARIO("Código Tributário", TipoAtoLegalDTO.CODIGO_TRIBUTARIO);

    private String descricao;
    private TipoAtoLegalDTO toDto;

    TipoAtoLegal(String descricao, TipoAtoLegalDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoAtoLegalDTO getToDto() {
        return toDto;
    }

    public boolean isLeisOuDecreto() {
        return TipoAtoLegal.DECRETO.equals(this)
            || TipoAtoLegal.LEI_ORDINARIA.equals(this)
            || TipoAtoLegal.LEI_COMPLEMENTAR.equals(this)
            || TipoAtoLegal.LEI_ORGANICA.equals(this)
            || TipoAtoLegal.PROJETO_DE_LEI.equals(this);
    }

    public static TipoAtoLegal getTipoAtoLegalPorDescricao(String descricao) {
        return Arrays.stream(TipoAtoLegal.values()).filter(tipo -> tipo.getDescricao().equals(descricao)).findFirst().orElse(null);
    }
}
