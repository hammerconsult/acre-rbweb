/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
@ManagedBean(name = "relatorioTransferenciaContaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "transferencia-conta-financeira", pattern = "/relatorio/contabil/transf-conta-financeira", viewId = "/faces/financeiro/relatorio/relatoriotransferenciacontafinanceira.xhtml"),
})
public class RelatorioTransferenciaContaFinanceiraControlador implements Serializable {

    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SubContaFacade contaFinanceiraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private String numeroInicial;
    private String numeroFinal;
    private TipoTransferenciaFinanceira tipo;
    private Date dataInicial;
    private Date dataFinal;
    private FonteDeRecursos fonteDeRecursosRetirada;
    private FonteDeRecursos fonteDeRecursosDeposito;
    private Conta contaDeDestinacaoRetirada;
    private Conta contaDeDestinacaoDeposito;
    private SubConta contaFinanceiraRetirada;
    private SubConta contaFinanceiraDeposito;
    private ResultanteIndependente resultanteIndependente;
    private String filtro;
    private TipoAdministracao tipoAdministracao;
    private TipoEntidade natureza;
    private EsferaDoPoder esferaDoPoder;
    private List<HierarquiaOrganizacional> listaUnidades;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;

    @URLAction(mappingId = "transferencia-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        numeroFinal = "";
        numeroInicial = "";
        dataInicial = new Date();
        dataFinal = new Date();
        fonteDeRecursosDeposito = null;
        fonteDeRecursosRetirada = null;
        contaFinanceiraDeposito = null;
        contaFinanceiraRetirada = null;
        resultanteIndependente = null;
        contaDeDestinacaoRetirada = null;
        contaDeDestinacaoDeposito = null;
        tipo = null;
        filtro = null;
        natureza = null;
        tipoAdministracao = null;
        esferaDoPoder = null;
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        listaUnidades = new ArrayList<>();
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values());
    }

    public List<SelectItem> getTiposAdministracoes() {
        return Util.getListSelectItem(TipoAdministracao.values());
    }

    public List<SelectItem> getEsferasDosPoderes() {
        return Util.getListSelectItem(EsferaDoPoder.values());
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItem(TipoEntidade.values());
    }

    public List<SelectItem> getResultantesIndependentes() {
        return Util.getListSelectItem(ResultanteIndependente.values());
    }

    public List<SelectItem> getTiposTransferenciasFinanceiras() {
        return Util.getListSelectItem(TipoTransferenciaFinanceira.values());
    }

    public List<FonteDeRecursos> completaFonteRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte, getExercicio());
    }

    public List<SubConta> completaSubConta(String parte) {
        return contaFinanceiraFacade.listaPorExercicio(parte, getExercicio());
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(filtro,  getExercicio());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validaDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("TRANSFERÊNCIA DE SALDO NA CONTA FINANCEIRA");
            dto.setApi("contabil/transferencia-conta-financeira/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validaDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial deve ser menor que a Data Final");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(A.DATATRANSFERENCIA) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (this.tipo != null) {
            parametros.add(new ParametrosRelatorios(" a.TIPOTRANSFERENCIAFINANCEIRA ", ":tipoTranferencia", null, OperacaoRelatorio.IGUAL, tipo.name(), null, 1, false));
            filtro += " Tipo Transferência: " + tipo.getDescricao() + " -";
        }

        if (fonteDeRecursosDeposito != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOSDEPOSITO_ID ", ":fonteDeposito", null, OperacaoRelatorio.IGUAL, fonteDeRecursosDeposito.getId(), null, 1, false));
            filtro += " Fonte de Recurso de Depósito: " + fonteDeRecursosDeposito.getDescricao() + " -";
        }

        if (fonteDeRecursosRetirada != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOSRETIRADA_ID ", ":fonteRetirada", null, OperacaoRelatorio.IGUAL, fonteDeRecursosRetirada.getId(), null, 1, false));
            filtro += " Fonte de Recurso de Retirada: " + fonteDeRecursosRetirada.getDescricao() + " -";
        }

        if (contaDeDestinacaoDeposito != null) {
            parametros.add(new ParametrosRelatorios(" a.contaDeDestinacaoDeposito_ID ", ":cDestDeposito", null, OperacaoRelatorio.IGUAL, contaDeDestinacaoDeposito.getId(), null, 1, false));
            filtro += " Conta de Destinação de Recurso de Depósito: " + contaDeDestinacaoDeposito.getCodigo() + " -";
        }

        if (contaDeDestinacaoRetirada != null) {
            parametros.add(new ParametrosRelatorios(" a.contaDeDestinacaoRetirada_ID ", ":cDestRetirada", null, OperacaoRelatorio.IGUAL, contaDeDestinacaoRetirada.getId(), null, 1, false));
            filtro += " Conta de Destinação de Recurso de Retirada: " + contaDeDestinacaoRetirada.getCodigo() + " -";
        }

        if (this.contaFinanceiraDeposito != null) {
            parametros.add(new ParametrosRelatorios(" a.SUBCONTADEPOSITO_ID ", ":subContaDeposito", null, OperacaoRelatorio.IGUAL, contaFinanceiraDeposito.getId(), null, 1, false));
            filtro += " Conta de Depósito: " + contaFinanceiraDeposito.getDescricao() + " -";
        }

        if (this.contaFinanceiraRetirada != null) {
            parametros.add(new ParametrosRelatorios(" a.SUBCONTARETIRADA_ID ", ":subContaRetirada", null, OperacaoRelatorio.IGUAL, contaFinanceiraRetirada.getId(), null, 1, false));
            filtro += " Conta de Retirada: " + contaFinanceiraRetirada.getDescricao() + " -";
        }

        if (this.resultanteIndependente != null) {
            parametros.add(new ParametrosRelatorios(" a.RESULTANTEINDEPENDENTE ", ":resultanteIndependente", null, OperacaoRelatorio.IGUAL, resultanteIndependente.name(), null, 1, false));
            filtro += " Resultante/Independente: " + resultanteIndependente.getDescricao() + " -";
        }

        if (this.numeroInicial != null && !this.numeroInicial.equals("")
            && this.numeroFinal != null && !this.numeroFinal.equals("")) {
            parametros.add(new ParametrosRelatorios(" a.NUMERO ", ":numeroInicial", ":numeroFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtro += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }

        if (this.tipoAdministracao != null) {
            parametros.add(new ParametrosRelatorios(" vw.TIPOADMINISTRACAO ", ":tipoAdministracao", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false));
            filtro += " Tipo: " + tipoAdministracao.getDescricao() + " -";
        }
        if (this.natureza != null) {
            parametros.add(new ParametrosRelatorios(" vw.TIPOUNIDADE ", ":natureza", null, OperacaoRelatorio.IGUAL, natureza.name(), null, 1, false));
            filtro += " Natureza: " + natureza.getTipo() + " -";
        }
        if (this.esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.ESFERADOPODER ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
            filtro += " Poder: " + esferaDoPoder.getDescricao() + " -";
        }

        if (!listaUnidades.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<Long> idsUnidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> unidadesDoUsuario =hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), getExercicio(), getDataReferencia(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 1, false));
        }

        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    public Exercicio getExercicio() {
        return dataFinal != null ? exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal)) : getExercicio();
    }

    public Date getDataReferencia() {
        return dataFinal != null ? dataFinal : sistemaFacade.getDataOperacao();
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

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public SubConta getContaFinanceiraDeposito() {
        return contaFinanceiraDeposito;
    }

    public void setContaFinanceiraDeposito(SubConta contaFinanceiraDeposito) {
        this.contaFinanceiraDeposito = contaFinanceiraDeposito;
    }

    public SubConta getContaFinanceiraRetirada() {
        return contaFinanceiraRetirada;
    }

    public void setContaFinanceiraRetirada(SubConta contaFinanceiraRetirada) {
        this.contaFinanceiraRetirada = contaFinanceiraRetirada;
    }

    public FonteDeRecursos getFonteDeRecursosDeposito() {
        return fonteDeRecursosDeposito;
    }

    public void setFonteDeRecursosDeposito(FonteDeRecursos fonteDeRecursosDeposito) {
        this.fonteDeRecursosDeposito = fonteDeRecursosDeposito;
    }

    public FonteDeRecursos getFonteDeRecursosRetirada() {
        return fonteDeRecursosRetirada;
    }

    public void setFonteDeRecursosRetirada(FonteDeRecursos fonteDeRecursosRetirada) {
        this.fonteDeRecursosRetirada = fonteDeRecursosRetirada;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    public TipoTransferenciaFinanceira getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransferenciaFinanceira tipo) {
        this.tipo = tipo;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
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

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Conta getContaDeDestinacaoRetirada() {
        return contaDeDestinacaoRetirada;
    }

    public void setContaDeDestinacaoRetirada(Conta contaDeDestinacaoRetirada) {
        this.contaDeDestinacaoRetirada = contaDeDestinacaoRetirada;
    }

    public Conta getContaDeDestinacaoDeposito() {
        return contaDeDestinacaoDeposito;
    }

    public void setContaDeDestinacaoDeposito(Conta contaDeDestinacaoDeposito) {
        this.contaDeDestinacaoDeposito = contaDeDestinacaoDeposito;
    }
}
