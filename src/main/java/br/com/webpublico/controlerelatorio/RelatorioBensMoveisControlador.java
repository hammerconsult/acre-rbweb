/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
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
    @URLMapping(id = "relatorio-bens-moveis", pattern = "/relatorio/bens-moveis/", viewId = "/faces/financeiro/relatorio/relatoriobensmoveis.xhtml")
})
public class RelatorioBensMoveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoBemFacade grupoBemFacade;
    private String numero;
    private GrupoBem grupoBem;
    private TipoGrupo tipoGrupo;
    private TipoLancamento tipoLancamento;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;


    @URLAction(mappingId = "relatorio-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.exercicio = getSistemaControlador().getExercicioCorrente();
        this.numero = null;
        this.apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        this.tipoLancamento = null;
        this.tipoOperacaoBensMoveis = null;
        this.tipoGrupo = null;
        this.grupoBem = null;
        this.listaUnidades = new ArrayList<>();
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-BENS-MOVEIS";
    }

    public void gerarRelatorioBensMoveis() {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/bens-moveis/");
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
        listaParametros.add(new ParametrosRelatorios(" trunc(bens.dataBensMoveis) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> listaParametros) {
        if (numero != null && !numero.isEmpty()) {
            listaParametros.add(new ParametrosRelatorios(" bens.numero ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (tipoGrupo != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipogrupo ", ":tipoGrupo", null, OperacaoRelatorio.IGUAL, tipoGrupo.name(), null, 1, false));
            filtros += " Tipo Grupo: " + tipoGrupo.getDescricao() + " -";
        }
        if (tipoLancamento != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipolancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (tipoOperacaoBensMoveis != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipoOperacaoBemEstoque ", ":tipoOperacao", null, OperacaoRelatorio.IGUAL, tipoOperacaoBensMoveis.name(), null, 1, false));
            filtros += " Operação: " + tipoOperacaoBensMoveis.getDescricao() + " -";
        }
        if (grupoBem != null) {
            listaParametros.add(new ParametrosRelatorios(" grupo.id  ", ":grupoBem", null, OperacaoRelatorio.IGUAL, grupoBem.getId(), null, 1, false));
            filtros += " Grupo Patrimonial: " + grupoBem.getCodigo() + " - " + grupoBem.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<SelectItem> getTiposOperacaoBensMoveis() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensMoveis tipo : TipoOperacaoBensMoveis.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTiposGrupo() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoGrupo tipo : TipoGrupo.values()) {
            if (tipo.getClasseDeUtilizacao().equals(BensMoveis.class))
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
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

    public List<GrupoBem> completarGrupoPatrimonial(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}
