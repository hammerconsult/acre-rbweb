/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-liberacao-saldo-conta-financeira", pattern = "/relatorio/liberacao-saldo-conta-financeira/", viewId = "/faces/financeiro/relatorio/relatorioliberasaldocotafinanceira.xhtml")
})
public class RelatorioLiberacaoCotaFinanceiraControlador extends RelatorioContabilSuperControlador implements Serializable {

    private TipoLiberacaoFinanceira tipoLiberacaoFinanceira;
    private TipoUnidade tipoUnidade;

    @URLAction(mappingId = "relatorio-liberacao-saldo-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        tipoLiberacaoFinanceira = null;
    }

    public List<SelectItem> getTiposDeUnidade() {
        return Util.getListSelectItemSemCampoVazio(TipoUnidade.values());
    }

    public List<SelectItem> getTiposDeLiberacaoFinanceira() {
        return Util.getListSelectItem(TipoLiberacaoFinanceira.values());
    }

    public List<FonteDeRecursos> buscarFonteRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, getSistemaFacade().getExercicioCorrente());
    }

    public List<SubConta> buscarSubConta(String parte) {
        return relatorioContabilSuperFacade.getSubContaFacade().listaFiltrando(parte, "descricao");
    }

    public List<Conta> buscarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio("Relatório de Liberação de Saldo Conta Financeira");
            dto.setApi("contabil/liberacao-conta-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(A.DATALIBERACAO) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOS_ID ", ":fonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getDescricao() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" a.contaDeDestinacao_ID ", ":cdest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" a.CONTAFINANCEIRA_ID ", ":contaFinanceira", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        if (tipoLiberacaoFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" a.TIPOLIBERACAOFINANCEIRA ", ":status", null, OperacaoRelatorio.IGUAL, tipoLiberacaoFinanceira.name(), null, 1, false));
            filtros += " Tipo da Liberação: " + tipoLiberacaoFinanceira.getDescricao() + " -";
        }

        if (!Strings.isNullOrEmpty(numero)) {
            parametros.add(new ParametrosRelatorios(" a.NUMERO ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }

        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            filtros += " Tipo de Unidade: " + tipoUnidade.getDescricao() + " -";
            if (TipoUnidade.CONCEDIDA.equals(tipoUnidade)) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            } else {
                parametros.add(new ParametrosRelatorios(" vwso.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                filtros += " Tipo de Unidade: " + tipoUnidade.getDescricao() + " -";
                if (TipoUnidade.CONCEDIDA.equals(tipoUnidade)) {
                    parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
                } else {
                    parametros.add(new ParametrosRelatorios(" vwso.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
                }
            }
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaFacade().getExercicioCorrente().getId(), null, 0, false));
        }
        atualizaFiltrosGerais();
        return parametros;
    }


    @Override
    public String getNomeRelatorio() {
        return "";
    }

    public TipoLiberacaoFinanceira getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(TipoLiberacaoFinanceira tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    public TipoUnidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    private enum TipoUnidade {
        CONCEDIDA("Unidade Concedida"),
        CONCEDENTE("Unidade Concedente");

        private String descricao;

        TipoUnidade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
