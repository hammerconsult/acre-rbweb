/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-concessoes-comprovacoes-diarias", pattern = "/relatorio/demonstrativo-concessoes-comprovacoes-diarias/", viewId = "/faces/financeiro/relatorio/relatorioconcessoesdiarias.xhtml"),
    @URLMapping(id = "relatorio-demonstrativo-concessoes-comprovacoes-suprimento-fundos", pattern = "/relatorio/demonstrativo-concessoes-comprovacoes-suprimento-fundos/", viewId = "/faces/financeiro/relatorio/relatorioconcessoescomprovacoes.xhtml")
})
@ManagedBean
public class RelatorioDemonstConcessoesControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private TipoContaDespesa tipoContaDespesa;
    private TipoAdministracao tipoAdministracao;
    private SituacaoPropostaConcessaoDiaria situacaoPropostaConcessaoDiaria;
    private TipoRelatorio tipoRelatorio;

    public RelatorioDemonstConcessoesControlador() {
    }

    @URLAction(mappingId = "relatorio-demonstrativo-concessoes-comprovacoes-diarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposDiaria() {
        limparCampos();
        tipoRelatorio = TipoRelatorio.DIARIA;
    }

    @URLAction(mappingId = "relatorio-demonstrativo-concessoes-comprovacoes-suprimento-fundos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposSuprimento() {
        limparCampos();
        tipoRelatorio = TipoRelatorio.SUPRIMENTOFUNDOS;
    }

    private void limparCampos() {
        super.limparCamposGeral();
        this.tipoAdministracao = null;
        this.tipoContaDespesa = null;
    }

    public void limparUnidade() {
        listaUnidades = Lists.newArrayList();
        unidadeGestora = null;
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("NOMERELATORIO", TipoRelatorio.SUPRIMENTOFUNDOS.equals(tipoRelatorio) ? "Demonstrativo das concessões e comprovações - Suprimento de fundos" : "Demonstrativo das concessões e comprovações - Diárias");
            dto.adicionarParametro("MODULO", "Financeiro e Contábil");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-concessoes-diarias/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getTiposDeAdministracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao ta : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoesDePropostaConcessaoDiaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoPropostaConcessaoDiaria spcd : SituacaoPropostaConcessaoDiaria.values()) {
            toReturn.add(new SelectItem(spcd, (spcd.ordinal() + 1) + " - " + spcd.getDescricao()));
        }
        toReturn.add(new SelectItem(null, "3 - Ambos"));
        toReturn.add(new SelectItem(null, "4 - Todos"));
        return toReturn;
    }

    public List<SelectItem> getTiposDeContaDespesa() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoContaDespesa.DIARIA_CIVIL, "Demonstrativo das Concessões e Comprovações" + " - " + TipoContaDespesa.DIARIA_CIVIL.getDescricao()));
        toReturn.add(new SelectItem(TipoContaDespesa.DIARIA_CAMPO, "Demonstrativo das Concessões e Comprovações" + " - " + TipoContaDespesa.DIARIA_CAMPO.getDescricao()));
        toReturn.add(new SelectItem(TipoContaDespesa.COLABORADOR_EVENTUAL, "Demonstrativo das Concessões e Comprovações" + " - " + TipoContaDespesa.COLABORADOR_EVENTUAL.getDescricao()));
        return toReturn;
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" coalesce(trunc(dc.DATADIARIA), trunc(p.dataLancamento)) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        parametros.add(new ParametrosRelatorios(" trunc(PAG.DATAPAGAMENTO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(PAGEST.DATAESTORNO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 5, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 6, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : unidadesDoUsuario) {
                idsUnidades.add(lista.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 6, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 6, false));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (TipoRelatorio.SUPRIMENTOFUNDOS.equals(tipoRelatorio)) {
            parametros.add(new ParametrosRelatorios(" P.TIPOPROPOSTA ", ":tipoProposta", null, OperacaoRelatorio.IGUAL, TipoProposta.SUPRIMENTO_FUNDO.name(), null, 1, false));
        } else {
            parametros.add(new ParametrosRelatorios(" P.TIPOPROPOSTA ", ":tipoProposta", null, OperacaoRelatorio.DIFERENTE, TipoProposta.SUPRIMENTO_FUNDO.name(), null, 1, false));
        }
        if (tipoContaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" emp.TIPOCONTADESPESA ", ":tipoContaDesp", null, OperacaoRelatorio.LIKE, this.tipoContaDespesa.name(), null, 6, false));
            filtros += " Tipo de Conta: " + tipoContaDespesa.getDescricao() + " -";
        }
        if (situacaoPropostaConcessaoDiaria != null) {
            parametros.add(new ParametrosRelatorios(" P.SITUACAODIARIA ", ":situacao", null, OperacaoRelatorio.IGUAL, situacaoPropostaConcessaoDiaria.name(), null, 1, false));
            filtros += " Situação da Diária: " + this.situacaoPropostaConcessaoDiaria.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        if (TipoRelatorio.SUPRIMENTOFUNDOS.equals(tipoRelatorio)) {
            return "DEMONSTRATIVO-CONCESSOES-COMPROVACOES-SUPRIMENTO-DE-FUNDOS";
        } else {
            return "DEMONSTRATIVO-CONCESSOES-COMPROVACOES-DIARIAS";
        }
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public SituacaoPropostaConcessaoDiaria getSituacaoPropostaConcessaoDiaria() {
        return situacaoPropostaConcessaoDiaria;
    }

    public void setSituacaoPropostaConcessaoDiaria(SituacaoPropostaConcessaoDiaria situacaoPropostaConcessaoDiaria) {
        this.situacaoPropostaConcessaoDiaria = situacaoPropostaConcessaoDiaria;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public enum TipoRelatorio {
        DIARIA,
        SUPRIMENTOFUNDOS;

        TipoRelatorio() {
        }
    }
}
