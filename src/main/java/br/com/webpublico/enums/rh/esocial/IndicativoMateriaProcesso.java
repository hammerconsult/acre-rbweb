package br.com.webpublico.enums.rh.esocial;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mateus on 18/06/18.
 */
public enum IndicativoMateriaProcesso {
    TRIBUTARIA(1, "Tributária"),
    AUTORIZACAO_DE_TRABALHO_DE_MENOR(2, "Autorização de trabalho de menor"),
    DISPENSA_CONTRATACAO_PESSOA_COM_DEFICIENCIA(3, "Dispensa, ainda que parcial, de contratação de pessoa com deficiência (PCD)"),
    DISPENSA_CONTRATACAO_DE_APRENDIZ(4, "Dispensa, ainda que parcial, de contratação de aprendiz"),
    SEGURANCA_SAUDE_DO_TRABALHO(5, "Segurança e Saúde do Trabalho"),
    CONVERSAO_LICENCA_SAUDE_EM_ACIDENTE_DE_TRABALHO(6, "Conversão de Licença Saúde em Acidente de Trabalho"),
    FGTS_OU_CONTRIBUICAO_SOCIAL_RESCISORIA(7, "FGTS ou Contribuição Social Rescisória (Lei Complementar 110/2001)"),
    CONTRIBUICAO_SINDICAL(8, "Contribuição sindical"),
    OUTROS_ASSUNTOS(99, "Outros assuntos");

    private Integer codigo;
    private String descricao;

    IndicativoMateriaProcesso(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public static List<IndicativoMateriaProcesso> buscarIndicativosPeloTipoProcesso(TipoProcessoAdministrativoJudicial tipoProcesso) {
        if (tipoProcesso == null) {
            return Lists.newArrayList();
        }
        switch (tipoProcesso) {
            case NUMERO_DE_BENEFICIO:
                return Lists.newArrayList(CONVERSAO_LICENCA_SAUDE_EM_ACIDENTE_DE_TRABALHO);
            default:
                return Lists.newArrayList(values());
        }
    }
}
