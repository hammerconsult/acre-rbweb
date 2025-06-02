/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoMaterialFacade;
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
    @URLMapping(id = "relatorio-bens-estoque", pattern = "/relatorio/bens-estoque/", viewId = "/faces/financeiro/relatorio/relatoriobensestoque.xhtml")
})
public class RelatorioBensEstoqueControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    private TipoEstoque tipoEstoque;
    private TipoOperacaoBensEstoque tipoOperacaoBemEstoque;
    private GrupoMaterial grupoMaterial;
    private TipoLancamento tipoLancamento;

    @URLAction(mappingId = "relatorio-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        this.numero = null;
        this.tipoEstoque = null;
        this.tipoOperacaoBemEstoque = null;
        this.listaUnidades = new ArrayList<>();
    }

    public void gerarRelatorioBensEstoque() {
        try {
            validarDatasSemVerificarExercicioLogado();
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
            dto.setApi("contabil/bens-estoque/");
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

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    private void adicionarExercicioParaApresentacao(List<ParametrosRelatorios> listaParametros) {
        if (apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
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
        if (tipoEstoque != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipoEstoque ", ":tipoEstoque", null, OperacaoRelatorio.IGUAL, tipoEstoque.name(), null, 1, false));
            filtros += " Tipo de Estoque: " + tipoEstoque.getDescricao() + " -";
        }
        if (tipoLancamento != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipolancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (tipoOperacaoBemEstoque != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.operacoesBensEstoque ", ":operacao", null, OperacaoRelatorio.IGUAL, tipoOperacaoBemEstoque.name(), null, 1, false));
            filtros += " Operação: " + tipoOperacaoBemEstoque.getDescricao() + " -";
        }
        if (grupoMaterial != null) {
            listaParametros.add(new ParametrosRelatorios(" grupo.id ", ":grupoMaterial", null, OperacaoRelatorio.IGUAL, grupoMaterial.getId(), null, 1, false));
            filtros += " Grupo Material: " + grupoMaterial.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return listaParametros;
    }

    public List<SelectItem> getTiposEstoque() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoEstoque tipo : TipoEstoque.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTiposOperacaoBensEstoque() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensEstoque tipo : TipoOperacaoBensEstoque.values()) {
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

    public List<GrupoMaterial> compeltaGrupoMaterial(String parte) {
        return grupoMaterialFacade.listaFiltrandoGrupoDeMaterial(parte.trim());
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-BENS-ESTOQUE";
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBemEstoque() {
        return tipoOperacaoBemEstoque;
    }

    public void setTipoOperacaoBemEstoque(TipoOperacaoBensEstoque tipoOperacaoBemEstoque) {
        this.tipoOperacaoBemEstoque = tipoOperacaoBemEstoque;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}
