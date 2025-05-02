/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.DividaPublica;
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
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@ManagedBean(name = "relatorioMovimentacaoDividaPublicaEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-estorno-mov-div-pub", pattern = "/relatorio/estorno-movimento-divida-publica/", viewId = "/faces/financeiro/relatorio/relatoriomovimentodividapublicaestorno.xhtml")
})
public class RelatorioMovimentacaoDividaPublicaEstornoControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioMovimentacaoDividaPublicaEstornoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroInicial;
    private String numeroFinal;
    private DividaPublica dividaPublica;
    private String filtro;
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    private List<HierarquiaOrganizacional> listaDeHierarquias;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public RelatorioMovimentacaoDividaPublicaEstornoControlador() {
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
        if (this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial deve ser menor que a Data Final.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("FILTRO", filtro);
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.setNomeRelatorio("Relatório-de-Estorno-da-Movimentação-de-Dívida-Pública");
            dto.setApi("contabil/estorno-movimento-divida-publica/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(est.DATA) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (listaDeHierarquias.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaDeHierarquias) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }
        if (this.numeroInicial != null && !"".equals(this.numeroInicial)
            && this.numeroFinal != null && !"".equals(this.numeroFinal)) {
            parametros.add(new ParametrosRelatorios("  cast(est.numero as INTEGER) ", ":numeroInicial", ":numeroFinl", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtro += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }
        if (this.dividaPublica != null) {
            parametros.add(new ParametrosRelatorios(" est.DIVIDAPUBLICA_ID ", ":duvidaPublica", null, OperacaoRelatorio.IGUAL, dividaPublica.getId(), false, 1, false));
            filtro += " Dívida Pública: " + dividaPublica.getDescricaoDivida() + " -";
        }
        if (this.operacaoMovimentoDividaPublica != null) {
            parametros.add(new ParametrosRelatorios(" est.OPERACAOMOVIMENTODIVIDAPUBLICA ", ":operacao", null, OperacaoRelatorio.IGUAL, operacaoMovimentoDividaPublica.name(), false, 1, false));
            filtro += " Operação: " + operacaoMovimentoDividaPublica.getDescricao() + " -";
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ug", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), false, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, sistemaFacade.getExercicioCorrente().getId(), null, 0, false));
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    @URLAction(mappingId = "relatorio-estorno-mov-div-pub", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataFinal = new Date();
        this.dataInicial = new Date();
        this.numeroInicial = "";
        this.numeroFinal = "";
        this.dividaPublica = null;
        this.filtro = "";
        this.operacaoMovimentoDividaPublica = null;
        this.listaDeHierarquias = new ArrayList<>();
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

    public List<HierarquiaOrganizacional> getListaDeHierarquias() {
        return listaDeHierarquias;
    }

    public void setListaDeHierarquias(List<HierarquiaOrganizacional> listaDeHierarquias) {
        this.listaDeHierarquias = listaDeHierarquias;
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
