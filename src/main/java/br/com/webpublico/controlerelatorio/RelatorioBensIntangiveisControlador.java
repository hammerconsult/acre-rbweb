/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BensIntangiveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
 * @author Claudio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-bens-intangiveis", pattern = "/relatorio/bens-intangiveis/", viewId = "/faces/financeiro/relatorio/relatoriobensintangiveis.xhtml")
})
public class RelatorioBensIntangiveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoBemFacade grupoBemFacade;
    private TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis;
    private TipoGrupo tipoGrupo;
    private TipoLancamento tipoLancamento;
    private GrupoBem grupoPatrimonial;

    @URLAction(mappingId = "relatorio-bens-intangiveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.exercicio = getSistemaControlador().getExercicioCorrente();
        this.numero = null;
        this.tipoGrupo = null;
        this.grupoPatrimonial = null;
        this.tipoOperacaoBensIntangiveis = null;
        this.listaUnidades = new ArrayList<>();
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-BENS-INTANGIVEIS";
    }

    public void gerarRelatorioBensIntangiveis() {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/bens-intangiveis/");
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
        filtros = "";
        filtrosParametroDatas(parametros);
        adicionarExercicioParaApresentacao(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    private void adicionarExercicioParaApresentacao(List<ParametrosRelatorios> listaParametros) {
        if (apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
        }
    }

    private void filtrosParametroDatas(List<ParametrosRelatorios> listaParametros) {
        listaParametros.add(new ParametrosRelatorios(" trunc(bens.databem) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 2, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {
        if (numero != null && !numero.isEmpty()) {
            listaParametros.add(new ParametrosRelatorios(" bens.numero ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (tipoGrupo != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipogrupo ", ":tipoGrupo", null, OperacaoRelatorio.IGUAL, tipoGrupo.name(), null, 1, false));
            filtros += " Tipo de Grupo: " + tipoGrupo.getDescricao() + " -";
        }
        if (tipoLancamento != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipolancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (tipoOperacaoBensIntangiveis != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipooperacaobemestoque ", ":operacao", null, OperacaoRelatorio.IGUAL, tipoOperacaoBensIntangiveis.name(), null, 1, false));
            filtros += " Operação: " + tipoOperacaoBensIntangiveis.getDescricao() + " -";
        }
        if (grupoPatrimonial != null) {
            listaParametros.add(new ParametrosRelatorios(" grupo.id ", ":grupoBem", null, OperacaoRelatorio.IGUAL, grupoPatrimonial, null, 1, false));
            filtros += " Grupo Patrimonial: " + grupoPatrimonial.getCodigo() + " -";
        }
        atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<SelectItem> getTiposGrupos() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todos"));
        for (TipoGrupo tipo : TipoGrupo.values()) {
            if (tipo.getClasseDeUtilizacao().equals(BensIntangiveis.class)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getTiposOperacaoBensEstoque() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todas"));
        for (TipoOperacaoBensIntangiveis tipo : TipoOperacaoBensIntangiveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getTiposLancamento() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todos"));
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<GrupoBem> completarGruposPatrimoniais(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, TipoBem.INTANGIVEIS);
    }

    public TipoOperacaoBensIntangiveis getTipoOperacaoBensIntangiveis() {
        return tipoOperacaoBensIntangiveis;
    }

    public void setTipoOperacaoBensIntangiveis(TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis) {
        this.tipoOperacaoBensIntangiveis = tipoOperacaoBensIntangiveis;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    private List<TipoOperacaoBensIntangiveis> excecoesDeItensListados() {
        List<TipoOperacaoBensIntangiveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensIntangiveis.AQUISICAO_BENS_INTANGIVEIS);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensIntangiveis.TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA);
        return lista;
    }

}
