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
import br.com.webpublico.enums.*;
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
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean(name = "relatorioSolicitacaoCotaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-solicitacao-saldo-cota-financeira", pattern = "/relatorio/relatorio-solicitacao-saldo-cota-financeira/", viewId = "/faces/financeiro/relatorio/relatoriosolicsaldocotafinanceira.xhtml")
})
public class RelatorioSolicitacaoCotaFinanceiraControlador extends RelatorioContabilSuperControlador implements Serializable {

    private String numeroInicial;
    private String numeroFinal;
    private StatusSolicitacaoCotaFinanceira status;
    private ResultanteIndependente resultanteIndependente;
    private TipoAdministracao tipoAdministracao;
    private TipoEntidade natureza;

    @URLAction(mappingId = "relatorio-solicitacao-saldo-cota-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        numeroInicial = null;
        numeroFinal = null;
        resultanteIndependente = null;
        status = null;
        natureza = null;
        tipoAdministracao = null;
    }

    public List<SelectItem> getResultantesIndependentes() {
        return Util.getListSelectItem(ResultanteIndependente.values());
    }

    public List<SelectItem> getTiposDeAdministracao() {
        return Util.getListSelectItem(TipoAdministracao.values());
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItem(TipoEntidade.values());
    }

    public List<SelectItem> getListaStatus() {
        return Util.getListSelectItem(StatusSolicitacaoCotaFinanceira.values());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        if (contaFinanceira != null) {
            return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceira(parte, contaFinanceira);
        } else {
            return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, buscarExercicioPelaDataFinal());
        }
    }

    public List<SubConta> completarContasFinanceiras(String parte) {
        return relatorioContabilSuperFacade.getSubContaFacade().listaPorExercicio(parte, buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, buscarExercicioPelaDataFinal());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("apresentacaoRelatorio", apresentacao.getToDto());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/solicitacao-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(A.DTSOLICITACAO) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOS_ID ", ":numero", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getDescricao() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" a.contaDeDestinacao_ID ", ":contaDest", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" a.CONTAFINANCEIRA_ID ", ":contaFinanceira", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " - " + contaFinanceira.getDescricao() + " -";
        }
        if (status != null) {
            parametros.add(new ParametrosRelatorios(" a.STATUS ", ":status", null, OperacaoRelatorio.IGUAL, status.name(), null, 1, false));
            filtros += " Status: " + status.getDescricao() + " -";
        }
        if (resultanteIndependente != null) {
            parametros.add(new ParametrosRelatorios(" a.RESULTANTEINDEPENDENTE ", ":resultanteIndependente", null, OperacaoRelatorio.IGUAL, resultanteIndependente.name(), null, 1, false));
            filtros += " Resultante/Independente: " + resultanteIndependente.getDescricao() + " -";
        }
        if (!Strings.isNullOrEmpty(numeroInicial) && !Strings.isNullOrEmpty(numeroFinal)) {
            parametros.add(new ParametrosRelatorios(" a.NUMERO ", ":numeroInicial", ":numeroFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtros += " Número: " + numeroInicial + " a " + numeroFinal + " -";
        }
        if (tipoAdministracao != null) {
            parametros.add(new ParametrosRelatorios(" vw.TIPOADMINISTRACAO ", ":tipoAdministracao", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false));
            filtros += " Tipo: " + tipoAdministracao.getDescricao() + " -";
        }
        if (natureza != null) {
            parametros.add(new ParametrosRelatorios(" vw.TIPOUNIDADE ", ":natureza", null, OperacaoRelatorio.IGUAL, natureza.name(), null, 1, false));
            filtros += " Natureza: " + natureza.getTipo() + " -";
        }
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.ESFERADOPODER ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
            filtros += " Poder: " + esferaDoPoder.getDescricao() + " -";
        }
        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataInicial, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-SOLICITACAO-FINANCEIRA";
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    public StatusSolicitacaoCotaFinanceira getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoCotaFinanceira status) {
        this.status = status;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public TipoEntidade getNatureza() {
        return natureza;
    }

    public void setNatureza(TipoEntidade natureza) {
        this.natureza = natureza;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }
}
