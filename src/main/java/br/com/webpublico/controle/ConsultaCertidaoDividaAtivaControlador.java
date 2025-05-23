/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.TotalSituacao;
import br.com.webpublico.entidadesauxiliares.VOConsultaCda;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "consultaCertidaoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReemissaodecda",
        pattern = "/consulta-e-reemissao-de-certidao-de-divida-ativa/",
        viewId = "/faces/tributario/dividaativa/reemissaoconsultacertidao/reemissaoconsultacda.xhtml"),
    @URLMapping(id = "novoEnvioCertidaoDA",
        pattern = "/enviar-certicao-de-divida-ativa/",
        viewId = "/faces/tributario/dividaativa/certidaodividaativa/enviarcda.xhtml")

})
public class ConsultaCertidaoDividaAtivaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaCertidaoDividaAtivaControlador.class);
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    private CertidaoDividaAtiva certidaoDividaAtiva;
    private CertidaoDividaAtiva certidaoDividaAtivaSelecionada;
    private CertidaoDividaAtivaLegada certidaoLegadaSelecionada;
    private CertidaoDividaAtiva cdaDesvinculada;
    private ConverterAutoComplete converterCadastroImobiliario, converterCadastroEconomico,
        converterCadastroRural, converterPessoa;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete dividaConverter;
    private List<Divida> listaDividas;
    private Divida[] dividasSelecionadas;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private List<CertidaoDividaAtiva> certidoesDividaAtiva;
    private CertidaoDividaAtiva[] certidoesSelecionadas;
    private Peticao peticao;
    private CertidaoDividaAtiva.FiltrosPesquisaCertidaoDividaAtiva filtros;
    private int inicio, maxResult;
    @EJB
    private CertidaoDividaAtivaDataModel certidaoDividaAtivaDataModel;
    private Future future;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    private List<ResultadoParcela> parcelasItemInscricaoDividaAtiva;
    private Map<String, BigDecimal> totaisSituacao;
    private ConsultaParcela consultaParcela;
    private LinkedList<ResultadoParcela> parcelasDoParcelamentoOriginado;
    private String sqlCargaSofplanCDA;
    private String sqlCargaSofplanParcelamento;
    private Boolean consideraParcelamento;
    private Boolean exiteCDAComProblemaDeComunicacao;
    private List<CertidaoDividaAtivaLegada> legadasOriginais;
    private List<CertidaoDividaAtivaLegada> legadasTransferir;
    private CertidaoDividaAtivaLegada legadaSelecionada;
    private Integer quantidadeRegistrosCda;
    private Integer quantidadeRegistrosParcelamento;
    private Map<Long, ValoresAtualizadosCDA> mapaIdCertidaoComValoresCDA;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    public String getCaminhoPadrao() {
        return "/consulta-e-reemissao-de-certidao-de-divida-ativa/";
    }

    public CertidaoDividaAtivaDataModel getCertidaoDividaAtivaDataModel() {
        return certidaoDividaAtivaDataModel;
    }

    public void setCertidaoDividaAtivaDataModel(CertidaoDividaAtivaDataModel certidaoDividaAtivaDataModel) {
        this.certidaoDividaAtivaDataModel = certidaoDividaAtivaDataModel;
    }

    public CertidaoDividaAtiva.FiltrosPesquisaCertidaoDividaAtiva getFiltros() {
        return filtros;
    }

    public void setFiltros(CertidaoDividaAtiva.FiltrosPesquisaCertidaoDividaAtiva filtros) {
        this.filtros = filtros;
    }

    public String getSqlCargaSofplanCDA() {
        return sqlCargaSofplanCDA;
    }

    public void setSqlCargaSofplanCDA(String sqlCargaSofplanCDA) {
        this.sqlCargaSofplanCDA = sqlCargaSofplanCDA;
    }

    public String getSqlCargaSofplanParcelamento() {
        return sqlCargaSofplanParcelamento;
    }

    public void setSqlCargaSofplanParcelamento(String sqlCargaSofplanParcelamento) {
        this.sqlCargaSofplanParcelamento = sqlCargaSofplanParcelamento;
    }

    public Peticao getPeticao() {
        return peticao;
    }

    public void setPeticao(Peticao peticao) {
        this.peticao = peticao;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Boolean getConsideraParcelamento() {
        return consideraParcelamento;
    }

    public void setConsideraParcelamento(Boolean consideraParcelamento) {
        this.consideraParcelamento = consideraParcelamento;
    }

    public Divida[] getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(Divida[] dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public List<Divida> getListaDividas() {
        return listaDividas;
    }

    public void setListaDividas(List<Divida> listaDividas) {
        this.listaDividas = listaDividas;
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public List<CertidaoDividaAtiva> getCertidoesDividaAtiva() {
        return certidoesDividaAtiva;
    }

    public void setCertidoesDividaAtiva(List<CertidaoDividaAtiva> listaCertidoes) {
        this.certidoesDividaAtiva = listaCertidoes;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public CertidaoDividaAtiva[] getCertidoesSelecionadas() {
        return certidoesSelecionadas;
    }

    public void setCertidoesSelecionadas(CertidaoDividaAtiva[] certidoesSelecionadas) {
        this.certidoesSelecionadas = certidoesSelecionadas;
    }

    public String numeroDoAutoInfracao(Long idItemInscricaoDividaAtiva) {
        return certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscaAutoInfracao(idItemInscricaoDividaAtiva);
    }

    public Integer getQuantidadeRegistrosParcelamento() {
        return quantidadeRegistrosParcelamento;
    }

    public void setQuantidadeRegistrosParcelamento(Integer quantidadeRegistrosParcelamento) {
        this.quantidadeRegistrosParcelamento = quantidadeRegistrosParcelamento;
    }

    public Integer getQuantidadeRegistrosCda() {
        return quantidadeRegistrosCda;
    }

    public void setQuantidadeRegistrosCda(Integer quantidadeRegistrosCda) {
        this.quantidadeRegistrosCda = quantidadeRegistrosCda;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @URLAction(mappingId = "novoEnvioCertidaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorEnvio() {
        novo();
        sqlCargaSofplanCDA = certidaoDividaAtivaFacade.recuperarSqlCargaSoftPlanCDA();
        sqlCargaSofplanParcelamento = certidaoDividaAtivaFacade.recuperarSqlCargaSoftPlanParcelamento();
        consideraParcelamento = false;
        quantidadeRegistrosCda = 0;
        quantidadeRegistrosParcelamento = 0;
    }

    @URLAction(mappingId = "novoReemissaodecda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtros = new CertidaoDividaAtiva.FiltrosPesquisaCertidaoDividaAtiva();
        certidaoDividaAtivaDataModel.setMapaParametrosExternos(null);

        certidoesSelecionadas = new CertidaoDividaAtiva[0];
        certidaoDividaAtiva = new CertidaoDividaAtiva();
        valorMinimo = null;
        valorMaximo = null;
        certidoesDividaAtiva = new ArrayList<>();
        listaDividas = new ArrayList<>();
        inicio = 0;
        maxResult = 10;
        totaisSituacao = new HashMap<>();
        legadasOriginais = new ArrayList<>();
        legadasTransferir = new ArrayList<>();
    }

    public List<Pessoa> completaPessoa(String parte) {
        return certidaoDividaAtivaFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return certidaoDividaAtivaFacade.completaCadastroImobiliario(parte.trim());
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return certidaoDividaAtivaFacade.completaCadastroEconomico(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return certidaoDividaAtivaFacade.completaCadastroRural(parte.trim());
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return certidaoDividaAtivaFacade.completaTipoDoctoOficial(parte.trim(), recuperaTipoCadastroDoctoOficial(filtros.getTipoCadastroTributario()));
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public CertidaoDividaAtivaFacade getCertidaoDividaAtivaFacade() {
        return certidaoDividaAtivaFacade;
    }

    public void setCertidaoDividaAtivaFacade(CertidaoDividaAtivaFacade certidaoDividaAtivaFacade) {
        this.certidaoDividaAtivaFacade = certidaoDividaAtivaFacade;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, certidaoDividaAtivaFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, certidaoDividaAtivaFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete converterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, certidaoDividaAtivaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, certidaoDividaAtivaFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(certidaoDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public void consultar() {
        if (validaFiltros()) {
            List<Divida> dividas = dividasSelecionadasParaArrayList();
            try {
                certidoesDividaAtiva = certidaoDividaAtivaFacade.consultaCDA(filtros.getTipoCadastroTributario(), filtros.getCadastroInicial(), filtros.getCadastroFinal(), filtros.getNumeroCdaInicial(),
                    filtros.getNumeroCdaFinal(), filtros.getDataInscricaoInicial(), filtros.getDataInscricaoFinal(), valorMinimo, valorMaximo, filtros.getPessoa(), dividas, filtros.getExercicioInicial(), filtros.getExercicioFinal());
            } catch (UFMException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar pesquisar!", ex.getMessage()));
            }
        }
    }

    public void recuperaCertidao(CertidaoDividaAtiva certidao) {
        certidaoDividaAtiva = certidaoDividaAtivaFacade.recuperar(certidao.getId());
    }

    private boolean validaFiltros() {
        boolean retorno = true;
        if (filtros.getTipoCadastroTributario() == null) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Selecione o tipo de cadastro.");
        }
        return retorno;
    }

    private List<Divida> dividasSelecionadasParaArrayList() {
        if (dividasSelecionadas.length > 0) {
            List<Divida> dividas = new ArrayList<>();
            for (Divida div : dividasSelecionadas) {
                dividas.add(div);
            }
            return dividas;
        }
        return new ArrayList<>();
    }

    public String recuperaDividaOrigem(CertidaoDividaAtiva certidao) {
        return certidaoDividaAtivaFacade.recuperaDividaOrigemPorCertidaoDividaAtiva(certidao);
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : certidaoDividaAtivaFacade.getExercicioFacade().lista()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    private void inverterExercicio() {
        if (filtros.getExercicioInicial().getAno().compareTo(filtros.getExercicioFinal().getAno()) > 0) {
            Exercicio aux = filtros.getExercicioInicial();
            filtros.setExercicioInicial(filtros.getExercicioFinal());
            filtros.setExercicioFinal(aux);
        }
    }

    private void inverterDataInscricao() {
        if (filtros.getDataInscricaoInicial().compareTo(filtros.getDataInscricaoFinal()) > 0) {
            Date aux = filtros.getDataInscricaoInicial();
            filtros.setDataInscricaoInicial(filtros.getDataInscricaoFinal());
            filtros.setDataInscricaoFinal(aux);
        }
    }

    private void inverterNumeroCertidao() {
        if (filtros.getNumeroCdaInicial().compareTo(filtros.getNumeroCdaFinal()) > 0) {
            Long aux = filtros.getNumeroCdaInicial();
            filtros.setNumeroCdaInicial(filtros.getNumeroCdaFinal());
            filtros.setNumeroCdaFinal(aux);
        }
    }

    private void inverterValores() {
        if (valorMinimo.compareTo(valorMaximo) > 0) {
            BigDecimal aux = valorMinimo;
            valorMinimo = valorMaximo;
            valorMaximo = aux;
        }
    }

    public void limpaCadastro() {
        buscarDividasPorTipoCadastroTributario();
        filtros.novo(false);
    }

    private TipoCadastroDoctoOficial recuperaTipoCadastroDoctoOficial(TipoCadastroTributario tipoCadastroTributario) {
        if (tipoCadastroTributario == null) {
            return null;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
            return TipoCadastroDoctoOficial.CADASTROECONOMICO;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
            return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) && filtros.getPessoa() instanceof PessoaFisica) {
            return TipoCadastroDoctoOficial.PESSOAFISICA;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) && filtros.getPessoa() instanceof PessoaJuridica) {
            return TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
            return TipoCadastroDoctoOficial.CADASTRORURAL;
        }
        return null;
    }

    public void copiarCadastroInicialParaCadastroFinal() {
        if (filtros.getCadastroInicial() != null) {
            filtros.setCadastroFinal(filtros.getCadastroInicial());
        }
    }

    public List<SelectItem> getTiposDeDividas() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Divida tipo : listaDividas) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getSituacoesDaCertidao() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (SituacaoCertidaoDA situacao : SituacaoCertidaoDA.values()) {
            if (situacao.isVisibled()) {
                lista.add(new SelectItem(situacao, situacao.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> getSituacoesJudiciais() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (SituacaoJudicial situacao : SituacaoJudicial.values()) {
            lista.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return lista;
    }

    public Converter getDividaConverter() {
        if (dividaConverter == null) {
            dividaConverter = new ConverterAutoComplete(Divida.class, certidaoDividaAtivaFacade.getDividaFacade());
        }
        return dividaConverter;
    }

    public List<Divida> buscarDividasPorTipoCadastroTributario() {
        if (filtros.getTipoCadastroTributario() != null) {
            try {
                listaDividas = certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscarDividasPorTipoCadastroTributario(filtros.getTipoCadastroTributario());
                return listaDividas;
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva recuperaLivroDaParcela(ParcelaValorDivida parcelaValorDivida) {
        return certidaoDividaAtivaFacade.recuperaLinhaSequenciaNumeroDoLivro(parcelaValorDivida);
    }

    public String recuperaUltimaSituacaoDaParcela(ParcelaValorDivida parcelaValorDivida) {
        return certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().retornaUltimaSituacaoDaParcela(parcelaValorDivida);
    }

    public String recuperaUltimaReferenciaDaParcela(ParcelaValorDivida parcelaValorDivida) {
        return certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().retornaUltimaReferenciaDaParcela(parcelaValorDivida);
    }

    public void pesquisar() {
        certidoesDividaAtiva = certidaoDividaAtivaFacade.recuperaCDA(filtros, inicio, maxResult, false, false);
        if (certidoesDividaAtiva.isEmpty()) {
            FacesUtil.addWarn("Atenção!", "Nenhuma CDA encontrada com os filtros informados!");
        }
    }

    public void emitirTodas() {
        List<CertidaoDividaAtiva> listaCertidoes = new ArrayList<>();
        if (certidoesSelecionadas.length < 1) {
            FacesUtil.addError("Impossível Continuar!", "Nenhuma Certidão foi Selecionada para emissão!");
        } else {
            for (CertidaoDividaAtiva certidao : certidoesSelecionadas) {
                if (certidao.getDocumentoOficial() == null) {
                    try {
                        certidao = certidaoDividaAtivaFacade.geraDocumentoSemImprimir(certidao, getSistemaControlador());
                    } catch (UFMException e) {
                        logger.error(e.getMessage());
                    } catch (AtributosNulosException e) {
                        logger.error(e.getMessage());
                    } catch (ExcecaoNegocioGenerica e) {
                        FacesUtil.addOperacaoNaoPermitida(e.getMessage());
                    }
                }
                if (certidao != null && certidao.getDocumentoOficial() != null) {
                    listaCertidoes.add(certidao);
                }
            }
            List<DocumentoOficial> documentos = Lists.newArrayList();
            for (CertidaoDividaAtiva cda : listaCertidoes) {
                documentos.add(cda.getDocumentoOficial());
            }
            certidaoDividaAtivaFacade.getDocumentoOficialFacade().emiteDocumentoOficial(documentos.toArray(new DocumentoOficial[documentos.size()]));
        }
    }

    public void atribuiPeticao(CertidaoDividaAtiva certidao) {
        setPeticao(certidaoDividaAtivaFacade.getPeticaoFacade().peticaoDaCertidao(certidao));
    }

    public void proximaPagina() {
        inicio += maxResult;
        pesquisar();
    }

    public void paginaAnterior() {
        inicio -= maxResult;
        pesquisar();
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public boolean desabilitaBotaoProximaPagina() {
        return certidoesDividaAtiva == null || certidoesDividaAtiva.size() < maxResult;
    }

    public void filtrarDepoisDeEnviarCDA() {
        filtrar();
    }

    public void filtrar() {
        if (validaFiltros()) {
            Map<RecuperadorFacade.FiltroCampoOperador, Object> parametros = new HashMap<>();

            parametros.put(new RecuperadorFacade.FiltroCampoOperador("situacaoCertidaoDA", Operador.DIFERENTE), SituacaoCertidaoDA.JUNCAO_CDALEGADA);

            if (filtros.getDivida() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("divida", Operador.IGUAL, certidaoDividaAtivaDataModel.getComplementoDivida(Operador.IGUAL)), filtros.getDivida());
            }
            if (filtros.getSituacaoCertidao() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("situacaoCertidaoDA", Operador.IGUAL), filtros.getSituacaoCertidao());
            }
            if (filtros.getSituacaoJudicial() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("situacaoJudicial", Operador.IGUAL), filtros.getSituacaoJudicial());
            }
            if (filtros.getNumeroCdaInicial() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("numero", Operador.MAIOR_IGUAL), filtros.getNumeroCdaInicial());
            }
            if (filtros.getNumeroCdaFinal() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("numero", Operador.MENOR_IGUAL), filtros.getNumeroCdaFinal());
            }
            if (filtros.getExercicioInicial() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicio.ano", Operador.MAIOR_IGUAL), filtros.getExercicioInicial().getAno());
            }
            if (filtros.getExercicioFinal() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicio.ano", Operador.MENOR_IGUAL), filtros.getExercicioFinal().getAno());
            }
            if (filtros.getExercicioCdaLegadaInicial() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicioCDALegada", Operador.MAIOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoExericioCdaLegada(Operador.MAIOR_IGUAL)), filtros.getExercicioCdaLegadaInicial().getAno());
            }
            if (filtros.getExercicioCdaLegadaFinal() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicioCDALegada", Operador.MENOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoExericioCdaLegada(Operador.MENOR_IGUAL)), filtros.getExercicioCdaLegadaFinal().getAno());
            }
            if (filtros.getNumeroLegadoInicial() != null && !"".equals(filtros.getNumeroLegadoInicial().trim())) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("numeroCDALegada", Operador.MAIOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoNumeroCdaLegada(Operador.MAIOR_IGUAL)), filtros.getNumeroLegadoInicial());
            }
            if (filtros.getNumeroLegadoFinal() != null && !"".equals(filtros.getNumeroLegadoFinal().trim())) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("numeroCDALegada", Operador.MENOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoNumeroCdaLegada(Operador.MENOR_IGUAL)), filtros.getNumeroLegadoInicial());
            }
            if (filtros.getTipoCadastroTributario() != null && !TipoCadastroTributario.PESSOA.equals(filtros.getTipoCadastroTributario())) {
                if (filtros.getCadastroInicial() != null && !"".equals(filtros.getCadastroInicial().trim())) {
                    parametros.put(new RecuperadorFacade.FiltroCampoOperador("cadastroInicial", Operador.MAIOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoCadastroInicial(filtros.getTipoCadastroTributario())), filtros.getCadastroInicial());
                }
                if (filtros.getCadastroFinal() != null && !"".equals(filtros.getCadastroFinal().trim())) {
                    parametros.put(new RecuperadorFacade.FiltroCampoOperador("cadastroFinal", Operador.MENOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoCadastroFinal(filtros.getTipoCadastroTributario())), filtros.getCadastroFinal());
                }
            }

            if (filtros.getTipoCadastroTributario() != null
                && TipoCadastroTributario.PESSOA.equals(filtros.getTipoCadastroTributario())
                && filtros.getPessoa() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("pessoa.id", Operador.IGUAL), filtros.getPessoa().getId());
            }

            if (filtros.getExercicioInicialDivida() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicioInicialDivida", Operador.MAIOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoExercicioDivida(Operador.MAIOR_IGUAL)), filtros.getExercicioInicialDivida().getAno());
            }
            if (filtros.getExercicioFinalDivida() != null) {
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("exercicioFinalDivida", Operador.MENOR_IGUAL, certidaoDividaAtivaDataModel.getComplementoExercicioDivida(Operador.MENOR_IGUAL)), filtros.getExercicioFinalDivida().getAno());
            }
            if (filtros.getProcessoJudicial() != null && !"".equals(filtros.getProcessoJudicial())) {
                String processoJudicial = StringUtil.removeZerosEsquerda(StringUtil.retornaApenasNumeros(filtros.getProcessoJudicial()));
                parametros.put(new RecuperadorFacade.FiltroCampoOperador("processoJudicial", Operador.LIKE, certidaoDividaAtivaDataModel.getComplementoProcessoJudicial()), processoJudicial);
            }
            mapaIdCertidaoComValoresCDA = Maps.newHashMap();
            certidaoDividaAtivaDataModel.setPrimeiraPesquisa(true);
            certidaoDividaAtivaDataModel.setMapaParametrosExternos(parametros);
        }
    }

    public String numeroProcessoAjuizamento(CertidaoDividaAtiva certidao) {
        return certidaoDividaAtivaFacade.numeroProcessoAjuizamentoDaCda(certidao);
    }

    public void validaCertidoesSelecionadas() {
        if (certidoesSelecionadas != null && certidoesSelecionadas.length > 0) {
            RequestContext.getCurrentInstance().execute("dialogEnviar.show()");
        } else {
            FacesUtil.addError("Atenção", "Nenhuma Certidão foi selecionada!");
        }
    }

    public void enviarCertidoes() {
        if (certidoesSelecionadas != null && certidoesSelecionadas.length > 0) {
            future = comunicaSofPlanFacade.enviarCDA(Arrays.asList(certidoesSelecionadas));
        }
    }

    public void consultaEnvio() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("terminaEnvio()");
        }
    }

    public void finalizarEnvio() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            future = null;
        }
    }

    public List<ItensCertidaoDaConsulta> itensCertidaoDaConsulta(CertidaoDividaAtiva certidao) {
        List<ItensCertidaoDaConsulta> itensCertidaoDaConsultas = certidaoDividaAtivaFacade.itensCertidaoDaConsulta(certidao);
        for (ItensCertidaoDaConsulta item : itensCertidaoDaConsultas) {
            if (item.getNumeroLivro() == null || item.getNumeroLivro() <= 0) {
                certidao.setSemLivro(true);
            }
        }
        return itensCertidaoDaConsultas;
    }

    public List<ResultadoParcela> getParcelasItemInscricaoDividaAtiva() {
        return parcelasItemInscricaoDividaAtiva;
    }

    public void setParcelasItemInscricaoDividaAtiva(List<ResultadoParcela> parcelasItemInscricaoDividaAtiva) {
        this.parcelasItemInscricaoDividaAtiva = parcelasItemInscricaoDividaAtiva;
    }

    public String valorDaCertidao(CertidaoDividaAtiva certidao) {
        if (mapaIdCertidaoComValoresCDA == null) {
            mapaIdCertidaoComValoresCDA = Maps.newHashMap();
        }
        if (!mapaIdCertidaoComValoresCDA.containsKey(certidao.getId())) {
            mapaIdCertidaoComValoresCDA.put(certidao.getId(), certidaoDividaAtivaFacade.valorAtualizadoDaCertidao(certidao));
        }
        return Util.formataValor(mapaIdCertidaoComValoresCDA.get(certidao.getId()).getValorTotal());
    }

    public void buscarParcelasItemInscricaoDividaAtiva(Long idCalculo, CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
        parcelasItemInscricaoDividaAtiva = certidaoDividaAtivaFacade.buscarParcelasDoCalculo(idCalculo);
    }

    public String certidoesLegadas(CertidaoDividaAtiva certidaoDividaAtiva) {
        List<CertidaoDividaAtivaLegada> certidaoDividaAtivaLegadas = certidaoDividaAtivaFacade.listaCertidoesLegadas(certidaoDividaAtiva);
        StringBuilder retorno = new StringBuilder();
        String juncao = "";
        for (CertidaoDividaAtivaLegada certidaoDividaAtivaLegada : certidaoDividaAtivaLegadas) {
            retorno.append(juncao).append(certidaoDividaAtivaLegada.getNumeroLegadoFormatado());
            juncao = ", ";
        }
        return retorno.toString();
    }

    public void imprimirConsultaDebito() {
        try {
            validarConsultaDebito();

            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            List<JasperPrint> jasperPrints = Lists.newArrayList();
            totaisSituacao = Maps.newHashMap();

            Map<VOConsultaCda, List<CertidaoDividaAtiva>> mapaCdas = preencherMapaCdas();
            for (Map.Entry<VOConsultaCda, List<CertidaoDividaAtiva>> entry : mapaCdas.entrySet()) {
                Cadastro cadastro = entry.getKey().getCadastro();
                Pessoa pessoa = entry.getKey().getPessoa();
                List<CertidaoDividaAtiva> cdas = entry.getValue();

                parcelasDoParcelamentoOriginado = Lists.newLinkedList();
                consultaParcela = new ConsultaParcela();
                String filtros = adicionarFiltrosCadastro(cadastro);

                if (pessoa != null) {
                    pessoa = certidaoDividaAtivaFacade.getPessoaFacade().recarregar(pessoa);
                    EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();

                    LinkedList<ResultadoParcela> resultados = Lists.newLinkedList();

                    List<Long> idsCalculo = montarIdsCalculoCda(cdas);

                    adicionarParametroConsultaCda(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculo);
                    adicionarParametroConsultaCda(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.INSCRICAO_DA);
                    adicionarParametroConsultaCda(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
                    adicionarParametroConsultaCda(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IN, Lists.newArrayList(0, 1));
                    adicionarParametroConsultaCda(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.NOT_IN, Lists.newArrayList(SituacaoParcela.ESTORNADO, SituacaoParcela.CANCELAMENTO));

                    adicionarOrdemPrioritaria(consultaParcela);

                    resultados.addAll(consultaParcela.executaConsulta().getResultados());
                    pesquisarParcelamentoDividaAtiva(resultados);
                    removerSituacaoNaoSelecionadasVindasDoParcelamento(parcelasDoParcelamentoOriginado);

                    if (!parcelasDoParcelamentoOriginado.isEmpty()) {
                        ordenarParcelasPelaReferencia(parcelasDoParcelamentoOriginado);
                        resultados.addAll(parcelasDoParcelamentoOriginado);
                    }

                    ordenarParcelasPorCadastroOrPessoa(resultados);
                    montarMapa(resultados);

                    List<TotalSituacao> totalPorSituacao = Lists.newArrayList();
                    for (String situacao : totaisSituacao.keySet()) {
                        TotalSituacao total = new TotalSituacao();
                        total.setSituacao(situacao);
                        total.setValor(totaisSituacao.get(situacao));
                        totalPorSituacao.add(total);
                    }

                    HashMap<String, Object> parameters = Maps.newHashMap();
                    String nome = "RelatorioConsultaDebitos.jasper";
                    parameters.put("BRASAO", abstractReport.getCaminhoImagem());
                    parameters.put("USUARIO", SistemaFacade.obtemLogin());
                    parameters.put("PESSOA", pessoa.getNome());
                    parameters.put("CPF_CNPJ", pessoa.getCpf_Cnpj());
                    if (enderecoCorrespondencia != null) {
                        parameters.put("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
                    }
                    parameters.put("FILTROS", filtros);
                    parameters.put("TOTALPORSITUACAO", totalPorSituacao);
                    parameters.put("SUBREPORT_DIR", abstractReport.getCaminho());

                    JasperPrint jasper = abstractReport.gerarBytesDeRelatorioComDadosEmCollectionView(abstractReport.getCaminho(), nome, parameters, new JRBeanCollectionDataSource(resultados));
                    jasperPrints.add(jasper);
                }
            }
            ByteArrayOutputStream dadosByte = abstractReport.exportarJaspersParaPDF(jasperPrints);
            abstractReport.setGeraNoDialog(true);
            abstractReport.escreveNoResponse("RelatorioConsultaDebitos", dadosByte.toByteArray());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar demonstrativo de debitos. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar demonstrativo de débitos. Detalhes: " + e.getMessage());
        }
    }

    private void validarConsultaDebito() {
        ValidacaoException ve = new ValidacaoException();
        if (certidoesSelecionadas.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione uma ou mais Certidões de Dívida Ativa para Imprimir o Demonstrativo de Débitos.");
        }
        ve.lancarException();
    }

    private List<Long> montarIdsCalculoCda(List<CertidaoDividaAtiva> cdas) {
        List<Long> idsCalculo = Lists.newArrayList();
        for (CertidaoDividaAtiva cda : cdas) {
            cda = certidaoDividaAtivaFacade.recuperar(cda.getId());
            for (ItemCertidaoDividaAtiva itemCda : cda.getItensCertidaoDividaAtiva()) {
                idsCalculo.add((itemCda.getItemInscricaoDividaAtiva().getId()));
            }
        }
        return idsCalculo;
    }

    private String adicionarFiltrosCadastro(Cadastro cadastro) {
        String filtros = "";

        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                filtros = getFiltros().getTipoCadastroTributario().getDescricaoLonga() + ": " + ((CadastroImobiliario) cadastro).getInscricaoCadastral();
            } else {
                filtros = getFiltros().getTipoCadastroTributario().getDescricaoLonga() + ": " + ((CadastroEconomico) cadastro).getInscricaoCadastral();
            }
        }
        return filtros;
    }

    private Map<VOConsultaCda, List<CertidaoDividaAtiva>> preencherMapaCdas() {
        Map<VOConsultaCda, List<CertidaoDividaAtiva>> mapaCdas = Maps.newHashMap();
        for (CertidaoDividaAtiva cda : certidoesSelecionadas) {
            Pessoa pessoa = null;
            if (cda.getCadastro() != null) {
                List<Pessoa> pessoas = certidaoDividaAtivaFacade.getPessoaFacade().recuperaPessoasDoCadastro(cda.getCadastro());
                if (!pessoas.isEmpty()) {
                    pessoa = pessoas.get(0);
                }
            } else {
                pessoa = cda.getPessoa();
            }
            VOConsultaCda voCda = new VOConsultaCda(pessoa, cda.getCadastro());
            if (!mapaCdas.containsKey(voCda)) {
                mapaCdas.put(voCda, Lists.newArrayList(cda));
            } else {
                mapaCdas.get(voCda).add(cda);
            }
        }
        return mapaCdas;
    }

    private void adicionarParametroConsultaCda(ConsultaParcela.Campo campo, ConsultaParcela.Operador operador, Object valor) {
        if (consultaParcela == null) {
            consultaParcela = new ConsultaParcela();
        }
        consultaParcela.addParameter(campo, operador, valor);
    }

    private void ordenarParcelasPelaReferencia(List<ResultadoParcela> parcelas) {
        if (parcelas != null && !parcelas.isEmpty()) {
            Collections.sort(parcelas, new Comparator<ResultadoParcela>() {
                @Override
                public int compare(ResultadoParcela p1, ResultadoParcela p2) {
                    return ComparisonChain.start()
                        .compare(p1.getReferencia(), p2.getReferencia())
                        .compare(p1.getVencimento(), p2.getVencimento())
                        .result();
                }
            });
        }
    }

    private void ordenarParcelasPorCadastroOrPessoa(List<ResultadoParcela> parcelas) {
        if (parcelas != null && !parcelas.isEmpty()) {
            Collections.sort(parcelas, new Comparator<ResultadoParcela>() {
                @Override
                public int compare(ResultadoParcela p1, ResultadoParcela p2) {
                    if (!p1.getCadastro().equals("") && !p2.getCadastro().equals("")) {
                        return ComparisonChain.start().compare(p1.getCadastro(), p2.getCadastro()).result();
                    }
                    if (p1.getIdPessoa() != null && p2.getIdPessoa() != null) {
                        return ComparisonChain.start().compare(p1.getIdPessoa(), p2.getIdPessoa()).result();
                    }
                    return 0;
                }
            });
        }
    }

    private void removerSituacaoNaoSelecionadasVindasDoParcelamento(List<ResultadoParcela> parcelas) {
        List<SituacaoParcela> situacoesComparacao = Lists.newArrayList(SituacaoParcela.ESTORNADO, SituacaoParcela.PARCELAMENTO_CANCELADO);
        List<ResultadoParcela> aRemover = Lists.newArrayList();
        for (ResultadoParcela rp : parcelas) {
            if (situacoesComparacao.contains(rp.getSituacaoEnumValue())) {
                aRemover.add(rp);
            }
        }
        for (ResultadoParcela rp : aRemover) {
            parcelas.remove(rp);
        }
    }

    private void montarMapa(List<ResultadoParcela> resultadosParcela) {
        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();
        for (ResultadoParcela resultado : resultadosParcela) {
            resultado.setDebitoProtestado(certidaoDividaAtivaFacade.buscarProcessoAtivoDaParcela(
                resultado.getIdParcela()) == null ? Boolean.FALSE : Boolean.TRUE);
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }

        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (resultadoParcela.getCotaUnica()
                    && !resultadoParcela.getVencido()
                    && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())
                    || resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.PAGO.name()))
                ) {
                    parcelasDaDivida.clear();
                    parcelasDaDivida.add(resultadoParcela);
                    break;
                } else {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }
        for (ResultadoParcela resultado : parcelasSoma) {
            BigDecimal total = BigDecimal.ZERO;
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(getSistemaControlador().getDataOperacao());
            if (!totaisSituacao.containsKey(situacaoParaProcessamento)) {
                totaisSituacao.put(situacaoParaProcessamento, BigDecimal.ZERO);
            }
            if (resultado.isPago()) {
                total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorPago());
            } else {
                boolean soma = false;
                if ((!resultado.getVencido() && resultado.getCotaUnica())) {
                    soma = true;
                }
                if (!resultado.getCotaUnica()) {
                    soma = true;
                }
                if ((resultado.getCotaUnica() && (resultado.isInscrito() || resultado.isCancelado()))) {
                    soma = true;
                }
                if (soma) {
                    total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorTotal());
                    resultado.setSomaNoDemonstrativo(true);
                    totaisSituacao.put(situacaoParaProcessamento, total);
                }
            }
            totaisSituacao.put(situacaoParaProcessamento, total);
        }
    }

    private void pesquisarParcelamentoDividaAtiva(LinkedList<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = recuperarParcelamentosDasParcelasFiltradas(parcelas);
        if (!listaIdsCalculoParcelamento.isEmpty()) {
            adicionarParcelamentos(listaIdsCalculoParcelamento);
        }
    }

    private List<Long> recuperarParcelamentosDasParcelasFiltradas(LinkedList<ResultadoParcela> parcelas) {
        List<Long> listaIdsCalculoParcelamento = Lists.newArrayList();
        for (ResultadoParcela resultado : parcelas) {
            if (SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO.equals(resultado.getSituacaoEnumValue())) {
                List<Long> compensacoes = certidaoDividaAtivaFacade.getPagamentoJudicialFacade().recuperarIDDoPagamentoJudicialDaParcelaOriginal(resultado.getIdParcela());
                for (Long id : compensacoes) {
                    if (!listaIdsCalculoParcelamento.contains(id)) {
                        listaIdsCalculoParcelamento.add(id);
                    }
                }
            } else {
                List<Long> parcelamentos = certidaoDividaAtivaFacade.getProcessoParcelamentoFacade().recuperarIDDoParcelamentoDaParcelaOriginal(resultado.getIdParcela());
                if (!parcelamentos.isEmpty()) {
                    for (Long id : parcelamentos) {
                        if (!listaIdsCalculoParcelamento.contains(id)) {
                            listaIdsCalculoParcelamento.add(id);
                        }
                    }
                }
            }
        }
        return listaIdsCalculoParcelamento;
    }

    private void adicionarParcelamentos(List<Long> listaIdsCalculoParcelamento) {
        ConsultaParcela cp = new ConsultaParcela();
        cp.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, listaIdsCalculoParcelamento);
        adicionarOrdemPrioritaria(cp);
        adicionarOrdemPadrao(cp);
        cp.executaConsulta();
        for (ResultadoParcela rp : cp.getResultados()) {
            if (!consultaParcela.getResultados().contains(rp) && !parcelasDoParcelamentoOriginado.contains(rp)) {
                parcelasDoParcelamentoOriginado.add(rp);
                if (SituacaoParcela.PARCELADO.equals(rp.getSituacaoEnumValue()) || SituacaoParcela.ESTORNADO.equals(rp.getSituacaoEnumValue())) {
                    LinkedList<ResultadoParcela> listaRp = Lists.newLinkedList();
                    listaRp.add(rp);
                    pesquisarParcelamentoDividaAtiva(listaRp);
                } else if (SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO.equals(rp.getSituacaoEnumValue())) {
                    LinkedList<ResultadoParcela> listaRp = Lists.newLinkedList();
                    listaRp.add(rp);
                    pesquisarParcelamentoDividaAtiva(listaRp);
                }
            }
        }
    }

    private void adicionarOrdemPrioritaria(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.CMC_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCR_CODIGO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PF_CPF, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PJ_CNPJ, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    private void adicionarOrdemPadrao(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.SD, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    public String enviarAProcuradoria(CertidaoDividaAtiva cda) {
        return certidaoDividaAtivaFacade.envioAProcuradoria(cda);
    }

    public void enviarCDABySql() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            future = comunicaSofPlanFacade.enviarCDA(assistenteBarraProgresso, sqlCargaSofplanCDA, consideraParcelamento);
        } catch (Exception e) {
            FacesUtil.addAtencao(e.getMessage());
        }
    }

    public void enviarParcelamentoBySql() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            future = comunicaSofPlanFacade.enviarParcelamento(assistenteBarraProgresso, sqlCargaSofplanParcelamento);
        } catch (Exception e) {
            FacesUtil.addAtencao(e.getMessage());
        }
    }

    public void salvarSQL() {
        certidaoDividaAtivaFacade.salvarSqlCargaSoftPlan(sqlCargaSofplanCDA, sqlCargaSofplanParcelamento);
    }

    public void contarCDA() {
        quantidadeRegistrosCda = certidaoDividaAtivaFacade.contarCDABySQL(sqlCargaSofplanCDA);
    }

    public void contarParcelamento() {
        quantidadeRegistrosParcelamento = certidaoDividaAtivaFacade.contarParcelamentoBySQL(sqlCargaSofplanParcelamento);
    }

    public List<ComunicacaoSoftPlan> getComunicacoesCDA() {
        if (certidaoDividaAtiva != null && certidaoDividaAtiva.getId() != null) {
            List<ComunicacaoSoftPlan> comunicacoes = certidaoDividaAtivaFacade.recuperarComunicacoesCDA(certidaoDividaAtiva);
            return comunicacoes;
        }
        return Lists.newArrayList();
    }

    public ValoresAtualizadosCDA getDetalhamentoValoresCda() {
        if (certidaoDividaAtiva != null && certidaoDividaAtiva.getId() != null) {

            if (mapaIdCertidaoComValoresCDA == null) {
                mapaIdCertidaoComValoresCDA = Maps.newHashMap();
            }
            if (!mapaIdCertidaoComValoresCDA.containsKey(certidaoDividaAtiva.getId())) {
                mapaIdCertidaoComValoresCDA.put(certidaoDividaAtiva.getId(), certidaoDividaAtivaFacade.valorAtualizadoDaCertidao(certidaoDividaAtiva));
            }
            return mapaIdCertidaoComValoresCDA.get(certidaoDividaAtiva.getId());
        }
        return new ValoresAtualizadosCDA();
    }

    public void imprimirCDAComFalha() {
        FacesUtil.redirecionamentoInterno("/consulta-de-certidao-de-divida-ativa-nao-comunicadas");
    }

    public Boolean getExiteCDAComProblemaDeComunicacao() {
        if (exiteCDAComProblemaDeComunicacao == null) {
            exiteCDAComProblemaDeComunicacao = certidaoDividaAtivaFacade.exiteCDAComProblemaDeComunicacao();
        }
        return exiteCDAComProblemaDeComunicacao;
    }

    public static class ItensCertidaoDaConsulta {
        private Long numero;
        private Integer ano;
        private ItemInscricaoDividaAtiva.Situacao situacaoItemInscricao;
        private TipoCadastroTributario tipoCadastroTributario;
        private String descricaoDivida;
        private Long numeroLivro;
        private Long anoLivro;
        private Long paginaLivro;
        private Long linhaLivro;
        private Long idItemInscricaoDividaAtiva;
        private Long idCadastro;

        public Long getNumero() {
            return numero;
        }

        public void setNumero(Long numero) {
            this.numero = numero;
        }

        public Long getAnoLivro() {
            return anoLivro;
        }

        public void setAnoLivro(Long anoLivro) {
            this.anoLivro = anoLivro;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public ItemInscricaoDividaAtiva.Situacao getSituacaoItemInscricao() {
            return situacaoItemInscricao;
        }

        public void setSituacaoItemInscricao(ItemInscricaoDividaAtiva.Situacao situacaoItemInscricao) {
            this.situacaoItemInscricao = situacaoItemInscricao;
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
        }

        public String getDescricaoDivida() {
            return descricaoDivida;
        }

        public void setDescricaoDivida(String descricaoDivida) {
            this.descricaoDivida = descricaoDivida;
        }

        public Long getNumeroLivro() {
            return numeroLivro;
        }

        public void setNumeroLivro(Long numeroLivro) {
            this.numeroLivro = numeroLivro;
        }

        public Long getPaginaLivro() {
            return paginaLivro;
        }

        public void setPaginaLivro(Long paginaLivro) {
            this.paginaLivro = paginaLivro;
        }

        public Long getLinhaLivro() {
            return linhaLivro;
        }

        public void setLinhaLivro(Long linhaLivro) {
            this.linhaLivro = linhaLivro;
        }

        public Long getIdItemInscricaoDividaAtiva() {
            return idItemInscricaoDividaAtiva;
        }

        public void setIdItemInscricaoDividaAtiva(Long idItemInscricaoDividaAtiva) {
            this.idItemInscricaoDividaAtiva = idItemInscricaoDividaAtiva;
        }

        public Long getIdCadastro() {
            return idCadastro;
        }

        public void setIdCadastro(Long idCadastro) {
            this.idCadastro = idCadastro;
        }
    }

    public void iniciarProcessoDesvincularCDA(CertidaoDividaAtiva cda, ItensCertidaoDaConsulta itemConsulta) {
        certidaoDividaAtiva = certidaoDividaAtivaFacade.recuperar(cda.getId());
        legadasOriginais = certidaoDividaAtiva.getCertidoesLegadas();
        legadasTransferir.clear();
        certidaoDividaAtivaSelecionada = cda;
        ItemCertidaoDividaAtiva itemCertidao = certidaoDividaAtivaFacade.recuperaItemCertidaoDividaAtiva(itemConsulta.getIdItemInscricaoDividaAtiva());
        criarNovaCDA(cda, itemCertidao);
    }

    public void emitirVariasCertidoes() {
        if (certidoesSelecionadas != null && certidoesSelecionadas.length > 0) {
            DocumentoOficial[] documentosSelecionados = {};
            ordenarListaPorDescricaoDivida();
            for (CertidaoDividaAtiva certidaoSelecionada : certidoesSelecionadas) {
                try {
                    if (certidaoSelecionada.getDocumentoOficial() == null) {
                        CertidaoDividaAtiva cdaComNovoDocumento = certidaoDividaAtivaFacade.geraDocumento(certidaoSelecionada, (SistemaControlador) Util.getControladorPeloNome("sistemaControlador"), true);
                        documentosSelecionados = (DocumentoOficial[]) Util.adicionarObjetoNoArray(documentosSelecionados, cdaComNovoDocumento.getDocumentoOficial());
                    } else {
                        documentosSelecionados = (DocumentoOficial[]) Util.adicionarObjetoNoArray(documentosSelecionados, certidaoSelecionada.getDocumentoOficial());
                    }
                } catch (Exception e) {
                    FacesUtil.addOperacaoNaoRealizada(e.getMessage() + ": " + certidaoSelecionada.getNumero() + "/" + certidaoSelecionada.getExercicio());
                    logger.error(e.getMessage());
                }
            }
            if (documentosSelecionados != null && documentosSelecionados.length > 0) {
                certidaoDividaAtivaFacade.getDocumentoOficialFacade().emiteDocumentoOficial(documentosSelecionados);
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Selecione ao menos uma Certidão de Divida Ativa.");
        }
    }

    public void desvincularCDA() {
        try {
            validarDesvincularCDA();
            FacesUtil.executaJavaScript("dlgDesvincularCda.hide()");
            cdaDesvinculada.setNumero(certidaoDividaAtivaFacade.recuperaProximoNumeroCDA());
            cdaDesvinculada.setCertidoesLegadas(legadasTransferir);
            certidaoDividaAtiva.setCertidoesLegadas(legadasOriginais);
            cdaDesvinculada = certidaoDividaAtivaFacade.salvarRetornando(cdaDesvinculada);
            certidaoDividaAtiva = certidaoDividaAtivaFacade.salvarRetornando(certidaoDividaAtiva);
            certidaoDividaAtivaFacade.adicionarNovaCDANoProcessoJudicial(certidaoDividaAtiva, cdaDesvinculada);

            enviarCertidaoSofPlan(certidaoDividaAtiva);
            enviarCertidaoSofPlan(cdaDesvinculada);

            filtrarDepoisDeEnviarCDA();
            FacesUtil.atualizarComponente("Formulario:tabela");
            FacesUtil.addOperacaoRealizada("Desmembramento da cda:  " + certidaoDividaAtiva.getNumero() + "/" + certidaoDividaAtiva.getExercicio().getAno() + " realizado com sucesso." +
                " Foi criado uma nova cda com o número: <b>" + cdaDesvinculada.getNumero() + "/" + cdaDesvinculada.getExercicio().getAno() + "</b>.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarNovaCDA(CertidaoDividaAtiva cdaOriginal, ItemCertidaoDividaAtiva itemCDA) {

        cdaOriginal = certidaoDividaAtivaFacade.recuperar(cdaOriginal.getId());
        cdaDesvinculada = (CertidaoDividaAtiva) Util.clonarObjeto(cdaOriginal);
        cdaDesvinculada.setId((Long) null);
        cdaDesvinculada.setUsuarioDesvinculacao(getSistemaControlador().getUsuarioCorrente());
        cdaDesvinculada.setDataDesvinculacao(getSistemaControlador().getDataOperacao());
        cdaDesvinculada.setMotivoDesvinculacao("");
        cdaDesvinculada.setItensCertidaoDividaAtiva(new ArrayList<ItemCertidaoDividaAtiva>());
        cdaDesvinculada.setComunicacoes(new ArrayList<ComunicacaoSoftPlan>());
        cdaDesvinculada.setCertidoesLegadas(new ArrayList<CertidaoDividaAtivaLegada>());

        int ano = itemCDA.getItemInscricaoDividaAtiva().getInscricaoDividaAtiva().getExercicio().getAno() + 1;
        cdaDesvinculada.setExercicio(certidaoDividaAtivaFacade.getExercicioFacade().getExercicioPorAno(ano));

        itemCDA.setCertidao(cdaDesvinculada);
        cdaDesvinculada.getItensCertidaoDividaAtiva().add(itemCDA);
        certidaoDividaAtiva.getItensCertidaoDividaAtiva().remove(itemCDA);
    }

    public void cancelarDesvincularCDA() {
        cdaDesvinculada = new CertidaoDividaAtiva();
        FacesUtil.executaJavaScript("dlgDesvincularCda.hide()");
    }

    private void validarDesvincularCDA() {
        ValidacaoException ve = new ValidacaoException();
        if (legadasTransferir == null || legadasTransferir.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma legada à transferir.");
        }
        if (cdaDesvinculada.getDataDesvinculacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data da desvinculação deve ser informada.");
        }
        if (cdaDesvinculada.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício da CDA desvinculada ser informado.");
        }
        if (cdaDesvinculada.getMotivoDesvinculacao() == null || cdaDesvinculada.getMotivoDesvinculacao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo da desvinculação deve ser informado.");
        }
        ve.lancarException();
    }

    private Set<Integer> getExerciciosDebitoCda(CertidaoDividaAtiva cda) {
        List<ItensCertidaoDaConsulta> itensConsulta = certidaoDividaAtivaFacade.itensCertidaoDaConsulta(cda);
        Set<Integer> exercicios = new HashSet<Integer>();
        for (ItensCertidaoDaConsulta item : itensConsulta) {
            exercicios.add(item.getAno());
        }
        return exercicios;
    }

    public void enviarCertidaoSofPlan(CertidaoDividaAtiva cda) {
        try {
            comunicaSofPlanFacade.enviarCDA(Lists.newArrayList(cda));
        } catch (Exception ex) {
            logger.error("Falha ao enviar a CDA: {}", ex);
        }
    }

    public boolean renderizarBtnDesvincularCDA(CertidaoDividaAtiva cda) {
        Set<Integer> exerciciosDebito = getExerciciosDebitoCda(cda);
        return exerciciosDebito.size() > 1;
    }

    public void transferirCDALegada(CertidaoDividaAtivaLegada original, boolean remover) {
        if (legadasTransferir.isEmpty()) {
            legadasTransferir.add(criarCDALegadaWithVinculo(original, true));
        } else {
            CertidaoDividaAtivaLegada cdaLegadaTransferir = null;
            for (CertidaoDividaAtivaLegada transferida : legadasTransferir) {
                if (transferida.getNumeroLegadoFormatado().equals(original.getNumeroLegadoFormatado())) {
                    FacesUtil.addOperacaoNaoPermitida("A CDA Legada: " + original.getNumeroLegadoFormatado() + " já adicionado na lista de CDA(s) Legadas a Transferir.");
                    cdaLegadaTransferir = null;
                    break;
                } else {
                    cdaLegadaTransferir = criarCDALegadaWithVinculo(original, true);
                }
            }
            if (cdaLegadaTransferir != null) {
                legadasTransferir.add(cdaLegadaTransferir);
            }
        }
        if (remover) {
            removerCDALegadaDaListaOriginal(original);
        }
    }

    public void transferirTodasCDAsLegadas() {
        for (CertidaoDividaAtivaLegada original : legadasOriginais) {
            transferirCDALegada(original, false);
        }
        legadasOriginais.clear();
    }

    public void desfazerTransferenciaTodasCDAsLegadas() {
        if (legadasOriginais.isEmpty()) {
            legadasOriginais.addAll(trocarReferencias(legadasTransferir));
        } else {
            Set<CertidaoDividaAtivaLegada> adicionar = Sets.newHashSet();
            Set<CertidaoDividaAtivaLegada> remover = Sets.newHashSet();
            for (CertidaoDividaAtivaLegada transferir : legadasTransferir) {
                for (CertidaoDividaAtivaLegada legada : legadasOriginais) {
                    if (legada.getNumeroLegadoFormatado().equals(transferir.getNumeroLegadoFormatado())) {
                        remover.add(transferir);
                    } else {
                        adicionar.add(transferir);
                    }
                }
            }
            adicionar.removeAll(remover);
            legadasOriginais.addAll(trocarReferencias((new ArrayList<>(adicionar))));
        }
        legadasTransferir.clear();
    }

    public List<CertidaoDividaAtivaLegada> trocarReferencias(List<CertidaoDividaAtivaLegada> listaLegada) {
        for (CertidaoDividaAtivaLegada legada : listaLegada) {
            legada.setCertidaoDividaAtiva(certidaoDividaAtiva);
        }
        return listaLegada;
    }

    public void desfazerTransferenciaCDALegada(CertidaoDividaAtivaLegada transferida) {
        if (legadasOriginais.isEmpty()) {
            transferida.setCertidaoDividaAtiva(certidaoDividaAtiva);
            legadasOriginais.add(transferida);
        } else {
            CertidaoDividaAtivaLegada cdaLegadaVoltarOriginal = null;
            for (CertidaoDividaAtivaLegada original : legadasOriginais) {
                if (original.getNumeroLegadoFormatado().equals(transferida.getNumeroLegadoFormatado())) {
                    FacesUtil.addOperacaoNaoPermitida("A CDA Legada: " + transferida.getNumeroLegadoFormatado() + " já adicionado na lista de CDA(s) Legadas Originais.");
                    cdaLegadaVoltarOriginal = null;
                    legadasTransferir.remove(transferida);
                    break;
                } else {
                    cdaLegadaVoltarOriginal = criarCDALegadaWithVinculo(transferida, false);
                    legadasTransferir.remove(transferida);
                }
            }
            if (cdaLegadaVoltarOriginal != null) {
                cdaLegadaVoltarOriginal.setCertidaoDividaAtiva(certidaoDividaAtiva);
                legadasOriginais.add(cdaLegadaVoltarOriginal);
            }
        }
        removerCDALegadaDaListaTransferencia(transferida);
    }

    public void removerCDALegadaDaListaOriginal(CertidaoDividaAtivaLegada legada) {
        boolean existeNaListaTransferir = false;
        for (CertidaoDividaAtivaLegada transferir : legadasTransferir) {
            if (transferir.getNumeroLegadoFormatado().equals(legada.getNumeroLegadoFormatado())) {
                existeNaListaTransferir = true;
            }
        }
        if (existeNaListaTransferir) {
            legadasOriginais.remove(legada);
        } else {
            FacesUtil.addOperacaoNaoRealizada("A CDA Legada não pode ser excluída, pois, não está na lista de CDA(s) Legadas para a transferência.");
        }
    }

    public void removerCDALegadaDaListaTransferencia(CertidaoDividaAtivaLegada legada) {
        boolean existeNaListaOriginal = false;
        for (CertidaoDividaAtivaLegada original : legadasOriginais) {
            if (original.getNumeroLegadoFormatado().equals(legada.getNumeroLegadoFormatado())) {
                existeNaListaOriginal = true;
            }
        }
        if (existeNaListaOriginal) {
            legadasTransferir.remove(legada);
        } else {
            FacesUtil.addOperacaoNaoRealizada("A CDA Legada não pode ser excluída, pois, não está na lista de CDA(s) Legadas Originais.");
        }
    }

    public CertidaoDividaAtivaLegada criarCDALegadaWithVinculo(CertidaoDividaAtivaLegada cdaLegada, boolean isTransferencia) {
        if (isTransferencia) {
            CertidaoDividaAtivaLegada novaCdaLegada = new CertidaoDividaAtivaLegada();
            novaCdaLegada.setNumeroLegado(cdaLegada.getNumeroLegado());
            novaCdaLegada.setAnoLegado(cdaLegada.getAnoLegado());
            novaCdaLegada.setCertidaoDividaAtiva(cdaDesvinculada);
            return novaCdaLegada;
        } else {
            CertidaoDividaAtivaLegada novaCdaLegada = new CertidaoDividaAtivaLegada();
            novaCdaLegada.setNumeroLegado(cdaLegada.getNumeroLegado());
            novaCdaLegada.setAnoLegado(cdaLegada.getAnoLegado());
            novaCdaLegada.setCertidaoDividaAtiva(certidaoDividaAtiva);
            return novaCdaLegada;
        }
    }

    public boolean desabilitarTransferirTodosCasoListaOriginaisFazia() {
        if (legadasOriginais.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean desabilitarVoltarTodosCasoListaTransferenciaFazia() {
        if (legadasTransferir.isEmpty()) {
            return true;
        }
        return false;
    }

    public CertidaoDividaAtiva getCdaDesvinculada() {
        return cdaDesvinculada;
    }

    public void setCdaDesvinculada(CertidaoDividaAtiva cdaDesvinculada) {
        this.cdaDesvinculada = cdaDesvinculada;
    }

    private boolean verificarCertidaoParaEnvioAProcuradoria(CertidaoDividaAtiva cda) {
        return CertidaoDividaAtiva.RetornoComunicacao.SUCESSO.getDescricao().equals(certidaoDividaAtivaFacade.envioAProcuradoria(cda));
    }

    public List<CertidaoDividaAtivaLegada> getLegadasOriginais() {
        return legadasOriginais;
    }

    public void setLegadasOriginais(List<CertidaoDividaAtivaLegada> legadasOriginais) {
        this.legadasOriginais = legadasOriginais;
    }

    public List<CertidaoDividaAtivaLegada> getLegadasTransferir() {
        return legadasTransferir;
    }

    public void setLegadasTransferir(List<CertidaoDividaAtivaLegada> legadasTransferir) {
        this.legadasTransferir = legadasTransferir;
    }

    public CertidaoDividaAtivaLegada getLegadaSelecionada() {
        return legadaSelecionada;
    }

    public void setLegadaSelecionada(CertidaoDividaAtivaLegada legadaSelecionada) {
        this.legadaSelecionada = legadaSelecionada;
    }

    private void ordenarListaPorDescricaoDivida() {
        List<CertidaoDividaAtiva> certidoesReorganizadas = Lists.newArrayList();
        for (CertidaoDividaAtiva certidoesSelecionada : certidoesSelecionadas) {
            certidoesReorganizadas.add(certidaoDividaAtivaFacade.recuperar(certidoesSelecionada.getId()));
        }
        certidoesSelecionadas = new CertidaoDividaAtiva[certidoesReorganizadas.size()];
        Collections.sort(certidoesReorganizadas, new Comparator<CertidaoDividaAtiva>() {
            @Override
            public int compare(CertidaoDividaAtiva o1, CertidaoDividaAtiva o2) {
                if ((o1.getItensCertidaoDividaAtiva() != null && !o1.getItensCertidaoDividaAtiva().isEmpty())
                    && (o2.getItensCertidaoDividaAtiva() != null && !o2.getItensCertidaoDividaAtiva().isEmpty())) {
                    return o1.getItensCertidaoDividaAtiva().get(0).getItemInscricaoDividaAtiva().getDivida().getDescricao()
                        .compareTo(o2.getItensCertidaoDividaAtiva().get(0).getItemInscricaoDividaAtiva().getDivida().getDescricao());
                }
                return -1;
            }
        });
        certidoesSelecionadas = certidoesReorganizadas.toArray(certidoesSelecionadas);
    }
}
