/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BensImoveis;
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
    @URLMapping(id = "relatorio-bens-imoveis", pattern = "/relatorio/bens-imoveis/", viewId = "/faces/financeiro/relatorio/relatoriobensimoveis.xhtml")
})
public class RelatorioBensImoveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoBemFacade grupoBemFacade;
    private TipoOperacaoBensImoveis tipoOperacaoBensImoveis;
    private GrupoBem grupoPatrimonial;
    private TipoGrupo tipoGrupo;
    private TipoLancamento tipoLancamento;

    @URLAction(mappingId = "relatorio-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.dataInicial = getSistemaControlador().getDataOperacao();
        this.dataFinal = getSistemaControlador().getDataOperacao();
        this.exercicio = getSistemaControlador().getExercicioCorrente();
        this.numero = null;
        this.grupoPatrimonial = null;
        this.tipoGrupo = null;
        this.tipoOperacaoBensImoveis = null;
        this.listaUnidades = new ArrayList<>();
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-BENS-IMOVEIS";
    }

    public void gerarRelatorioBensImoveis() {
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
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/bens-imoveis/");
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
        if (tipoOperacaoBensImoveis != null) {
            listaParametros.add(new ParametrosRelatorios(" bens.tipooperacaobemestoque ", ":operacao", null, OperacaoRelatorio.IGUAL, tipoOperacaoBensImoveis.name(), null, 1, false));
            filtros += " Operação: " + tipoOperacaoBensImoveis.getDescricao() + " -";
        } else {
            listaParametros.add(new ParametrosRelatorios(" bens.tipooperacaobemestoque ", ":operacaoRetirada", null, OperacaoRelatorio.NOT_IN, excecoesDeItensListadosSql(), null, 0, false));
        }
        if (grupoPatrimonial != null) {
            listaParametros.add(new ParametrosRelatorios(" grupo.id ", ":grupoBem", null, OperacaoRelatorio.IGUAL, grupoPatrimonial, null, 1, false));
            filtros += " Grupo Patrimonial: " + grupoPatrimonial.getCodigo() + " -";
        }
        atualizaFiltrosGerais();
        return listaParametros;
    }

    private List<String> excecoesDeItensListadosSql() {
        List<String> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA.name());
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA.name());
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA.name());
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA.name());
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA.name());
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA.name());
        return lista;
    }

    private List<TipoOperacaoBensImoveis> excecoesDeItensListados() {
        List<TipoOperacaoBensImoveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getTiposOperacaoBensImoveis() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todas"));
        for (TipoOperacaoBensImoveis tipo : TipoOperacaoBensImoveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getTiposGrupo() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todos"));
        for (TipoGrupo tipo : TipoGrupo.values()) {
            if (tipo.getClasseDeUtilizacao().equals(BensImoveis.class)) {
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

    public List<GrupoBem> completaGrupoPatrimonial(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, TipoBem.IMOVEIS);
    }

    public TipoOperacaoBensImoveis getTipoOperacaoBensImoveis() {
        return tipoOperacaoBensImoveis;
    }

    public void setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis tipoOperacaoBensImoveis) {
        this.tipoOperacaoBensImoveis = tipoOperacaoBensImoveis;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
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
