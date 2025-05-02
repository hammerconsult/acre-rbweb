/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaPublicaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-divida-publica", pattern = "/relatorio/demonstrativo-divida-publica", viewId = "/faces/financeiro/relatorio/relatoriomovimentodividapublica.xhtml")
})
@ManagedBean
public class RelatorioMovimentacaoDividaPublicaControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private List<HierarquiaOrganizacional> listaUnidades;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroInicial;
    private String numeroFinal;
    private DividaPublica dividaPublica;
    private String filtro;
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;

    public RelatorioMovimentacaoDividaPublicaControlador() {
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("Movimento de Dívida Pública");
            dto.setApi("contabil/movimentacao-divida-publica/");
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

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(MOV.DATA) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }

        if (this.numeroInicial != null && !this.numeroInicial.equals("")
            && this.numeroFinal != null && !this.numeroFinal.equals("")) {
            parametros.add(new ParametrosRelatorios("  cast(mov.numero as INTEGER) ", ":numeroInicial", ":numeroFinl", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtro += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }
        if (this.dividaPublica != null) {
            parametros.add(new ParametrosRelatorios(" MOV.DIVIDAPUBLICA_ID ", ":duvidaPublica", null, OperacaoRelatorio.IGUAL, dividaPublica.getId(), false, 1, false));
            filtro += " Dívida Pública: " + dividaPublica.getDescricaoDivida() + " -";
        }
        if (this.operacaoMovimentoDividaPublica != null) {
            parametros.add(new ParametrosRelatorios(" MOV.OPERACAOMOVIMENTODIVIDAPUBLICA ", ":operacao", null, OperacaoRelatorio.IGUAL, operacaoMovimentoDividaPublica.name(), false, 1, false));
            filtro += " Operação: " + operacaoMovimentoDividaPublica.getDescricao() + " -";
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ug", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), false, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 1, false));
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    @URLAction(mappingId = "relatorio-demonstrativo-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.dataFinal = getSistemaFacade().getDataOperacao();
        this.dataInicial = getSistemaFacade().getDataOperacao();
        this.numeroInicial = "";
        this.numeroFinal = "";
        this.dividaPublica = null;
        this.filtro = "";
        this.operacaoMovimentoDividaPublica = null;
        this.listaUnidades = Lists.newArrayList();
    }

    public List<DividaPublica> completarDividasPublicas(String parte) {
        return dividaPublicaFacade.listaFiltrandoDividaPublica(parte);
    }

    public List<SelectItem> getOperacoesMovimentosDividasPublicas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoMovimentoDividaPublica omdp : OperacaoMovimentoDividaPublica.values()) {
            toReturn.add(new SelectItem(omdp, omdp.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public Exercicio getExercicio() {
        return dataFinal != null ? exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal)) : getSistemaFacade().getExercicioCorrente();
    }

    public Date getDataFiltro() {
        return dataFinal != null ? dataFinal : getSistemaFacade().getDataOperacao();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
