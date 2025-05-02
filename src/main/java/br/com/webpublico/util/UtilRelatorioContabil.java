package br.com.webpublico.util;

import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;

import javax.persistence.Query;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 29/03/2017.
 */
public class UtilRelatorioContabil {

    public static String concatenarParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        if (parametros != null) {
            for (ParametrosRelatorios parametrosRelatorios : parametros) {
                if (parametrosRelatorios.getLocal().equals(local) && parametrosRelatorios.getNomeAtributo() != null) {
                    sql.append(clausula).append(parametrosRelatorios.getNomeAtributo()).append(" ").append(parametrosRelatorios.getOperacao().getDescricao()).append(" ").append(parametrosRelatorios.getCampo1());
                    sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
                }
            }
        }
        return sql.toString();
    }

    public static void adicionarParametros(List<ParametrosRelatorios> parametros, Query q) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            atribuirParametros(q, parametrosRelatorios);
        }
    }

    private static void atribuirParametros(Query q, ParametrosRelatorios parametrosRelatorios) {
        if (parametrosRelatorios.getValor1() != null) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
        }
        if (parametrosRelatorios.getCampo2() != null) {
            q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
        }
    }

    public static void adicionarParametrosRetirandoLocais(List<ParametrosRelatorios> parametros, Query q, Integer... locais) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (!hasLocal(parametrosRelatorios, locais)) {
                atribuirParametros(q, parametrosRelatorios);
            }
        }
    }

    public static void adicionarParametrosComparandoLocais(List<ParametrosRelatorios> parametros, Query q, Integer... locais) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (hasLocal(parametrosRelatorios, locais)) {
                atribuirParametros(q, parametrosRelatorios);
            }
        }
    }

    private static boolean hasLocal(ParametrosRelatorios parametrosRelatorios, Integer[] locais) {
        for (Integer local : locais) {
            if (local.intValue() == parametrosRelatorios.getLocal())
                return true;
        }
        return false;
    }

    public static boolean isApresentacaoUnidadeGestora(ApresentacaoRelatorio apresentacaoRelatorio) {
        return ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacaoRelatorio);
    }

    public static boolean isApresentacaoUnidade(ApresentacaoRelatorio apresentacaoRelatorio) {
        return ApresentacaoRelatorio.UNIDADE.equals(apresentacaoRelatorio);
    }

    public static boolean isApresentacaoOrgao(ApresentacaoRelatorio apresentacaoRelatorio) {
        return ApresentacaoRelatorio.ORGAO.equals(apresentacaoRelatorio);
    }

    public static void adicionarItemDemonstrativoFiltrosCampoACampo(RelatorioDTO dto, ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        dto.adicionarParametro("itemFiltrosRelatorioTemId", itemDemonstrativoFiltros.getRelatorio().getId() != null);
        dto.adicionarParametro("itemFiltrosRelatorioId", itemDemonstrativoFiltros.getRelatorio().getId());
        dto.adicionarParametro("itemFiltrosRelatorioUsaConta", itemDemonstrativoFiltros.getRelatorio().getUsaConta());
        dto.adicionarParametro("itemFiltrosRelatorioUsaPrograma", itemDemonstrativoFiltros.getRelatorio().getUsaPrograma());
        dto.adicionarParametro("itemFiltrosRelatorioUsaAcao", itemDemonstrativoFiltros.getRelatorio().getUsaAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubAcao", itemDemonstrativoFiltros.getRelatorio().getUsaSubAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaSubFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaUnidadeOrg", itemDemonstrativoFiltros.getRelatorio().getUsaUnidadeOrganizacional());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFonteRecurso", itemDemonstrativoFiltros.getRelatorio().getUsaFonteRecurso());
        dto.adicionarParametro("itemFiltrosRelatorioUsaTipoDespesa", itemDemonstrativoFiltros.getRelatorio().getUsaTipoDespesa());
        dto.adicionarParametro("itemFiltrosRelatorioUsaContaFinanceira", itemDemonstrativoFiltros.getRelatorio().getUsaContaFinanceira());
        dto.adicionarParametro("itemFiltrosDataFinal", itemDemonstrativoFiltros.getDataFinal());
        dto.adicionarParametro("itemFiltrosDataInicial", itemDemonstrativoFiltros.getDataInicial());
        dto.adicionarParametro("itemFiltrosDataReferencia", itemDemonstrativoFiltros.getDataReferencia());
        dto.adicionarParametro("itemFiltrosIdsUnidades", itemDemonstrativoFiltros.getIdsUnidades());
        dto.adicionarParametro("itemFiltrosTemApresentacao", itemDemonstrativoFiltros.getApresentacaoRelatorio() != null);
        if (itemDemonstrativoFiltros.getApresentacaoRelatorio() != null) {
            dto.adicionarParametro("itemFiltrosApresentacaoRelatorioDTO", itemDemonstrativoFiltros.getApresentacaoRelatorio().getToDto());
        }
        dto.adicionarParametro("itemFiltrosTemExercicio", itemDemonstrativoFiltros.getExercicio() != null);
        if (itemDemonstrativoFiltros.getExercicio() != null) {
            dto.adicionarParametro("itemFiltrosExercicioId", itemDemonstrativoFiltros.getExercicio().getId());
            dto.adicionarParametro("itemFiltrosExercicioAno", itemDemonstrativoFiltros.getExercicio().getAno());
        }
        dto.adicionarParametro("itemFiltrosPesquisouUg", itemDemonstrativoFiltros.getUnidadeGestora() != null);
        if (itemDemonstrativoFiltros.getUnidadeGestora() != null) {
            dto.adicionarParametro("itemFiltrosUnidadeGestoraId", itemDemonstrativoFiltros.getUnidadeGestora().getId());
        }
        dto.adicionarParametro("itemFiltrosTemParametrosRelatorio", (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()));
        if (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()) {
            dto.adicionarParametro("itemFiltrosParametrosRelatorios", ParametrosRelatorios.parametrosToDto(itemDemonstrativoFiltros.getParametros()));
        }
    }
}
