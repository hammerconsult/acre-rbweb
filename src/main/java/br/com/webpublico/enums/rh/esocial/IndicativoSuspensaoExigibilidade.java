package br.com.webpublico.enums.rh.esocial;

import com.google.common.collect.Lists;
import org.xhtmlrenderer.util.Util;

import java.util.List;

/**
 * Created by mateus on 18/06/18.
 */
public enum IndicativoSuspensaoExigibilidade {
    LIMINAR_EM_MANDADO_DE_SEGURANCA("01", "Liminar em Mandado de Segurança"),
    DEPOSITO_JUDICIAL_DO_MONTANTE_INTEGRAL("02", "Depósito Judicial do Montante Integral"),
    DEPOSITO_ADMINISTRATIVO_DO_MONTANTE_INTEGRAL("03", "Depósito Administrativo do Montante Integral"),
    ANTECIPACAO_DE_TUTELA("04", "Antecipação de Tutela"),
    LIMINAR_EM_MEDIDA_CAUTELAR("05", "Liminar em Medida Cautelar"),
    SENTENCA_EM_MANDADO_DE_SEGURANCA_FAVORAVEL_AO_CONTRIBUINTE("08", "Sentença em Mandado de Segurança Favorável ao Contribuinte"),
    SENTENCA_EM_ACAO_ORDINARIA_FAVORAVEL_CONTRIBUINTE_CONFIRMADA_PELO_TRF("09", "Sentença em Ação Ordinária Favorável ao Contribuinte e Confirmada pelo TRF"),
    ACORDAO_DO_TRF_FAVORAVEL_AO_CONTRIBUINTE("10", "Acórdão do TRF Favorável ao Contribuinte"),
    ACORDAO_DO_STJ_EM_RECURSO_ESPECIAL_FAVORAVEL_AO_CONTRIBUINTE("11", "Acórdão do STJ em Recurso Especial Favorável ao Contribuinte"),
    ACORDAO_DO_STF_EM_RECURSO_EXTRAORDINARIO_FAVORAVEL_AO_CONTRIBUINTE("12", "Acórdão do STF em Recurso Extraordinário Favorável ao Contribuinte"),
    SENTENCA_1_INSTANCIA_NAO_TRANSITADA_EM_JULGADO_COM_EFEITO_SUSPENSIVO("13", "Sentença 1ª instância não transitada em julgado com efeito suspensivo"),
    CONTESTACAO_ADMINISTRATIVA_FAP("14", "Contestação Administrativa FAP"),
    DECISAO_DEFINITIVA_A_FAVOR_DO_CONTRIBUINTE("90", "Decisão Definitiva a favor do contribuinte"),
    SEM_SUSPENSAO_DA_EXIGIBILIDADE("92", "Sem suspensão da exigibilidade");

    private String codigo;
    private String descricao;

    IndicativoSuspensaoExigibilidade(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public static List<IndicativoSuspensaoExigibilidade> buscarIndicativosPeloTipoProcesso(TipoProcessoAdministrativoJudicial tipoProcesso) {
        if (tipoProcesso == null) {
            return Lists.newArrayList();
        }
        List<IndicativoSuspensaoExigibilidade> retorno = Lists.newArrayList();
        switch (tipoProcesso) {
            case ADMINISTRATIVO:
            case PROCESSO_FAP:
                retorno.add(DEPOSITO_ADMINISTRATIVO_DO_MONTANTE_INTEGRAL);
                retorno.add(CONTESTACAO_ADMINISTRATIVA_FAP);
                retorno.add(DECISAO_DEFINITIVA_A_FAVOR_DO_CONTRIBUINTE);
                retorno.add(SEM_SUSPENSAO_DA_EXIGIBILIDADE);
                break;
            case JUDICIAL:
                retorno.add(LIMINAR_EM_MANDADO_DE_SEGURANCA);
                retorno.add(DEPOSITO_JUDICIAL_DO_MONTANTE_INTEGRAL);
                retorno.add(ANTECIPACAO_DE_TUTELA);
                retorno.add(LIMINAR_EM_MEDIDA_CAUTELAR);
                retorno.add(SENTENCA_EM_MANDADO_DE_SEGURANCA_FAVORAVEL_AO_CONTRIBUINTE);
                retorno.add(SENTENCA_EM_ACAO_ORDINARIA_FAVORAVEL_CONTRIBUINTE_CONFIRMADA_PELO_TRF);
                retorno.add(ACORDAO_DO_TRF_FAVORAVEL_AO_CONTRIBUINTE);
                retorno.add(ACORDAO_DO_STJ_EM_RECURSO_ESPECIAL_FAVORAVEL_AO_CONTRIBUINTE);
                retorno.add(ACORDAO_DO_STF_EM_RECURSO_EXTRAORDINARIO_FAVORAVEL_AO_CONTRIBUINTE);
                retorno.add(SENTENCA_1_INSTANCIA_NAO_TRANSITADA_EM_JULGADO_COM_EFEITO_SUSPENSIVO);
                retorno.add(DECISAO_DEFINITIVA_A_FAVOR_DO_CONTRIBUINTE);
                retorno.add(SEM_SUSPENSAO_DA_EXIGIBILIDADE);
                break;
            default:
                retorno.addAll(Lists.<IndicativoSuspensaoExigibilidade>newArrayList(values()));
        }
        return retorno;
    }
}
