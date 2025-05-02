/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteContratoCeasa;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.entidadesauxiliares.ParcelaContratoCEASA;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoCEASAFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "contratoCEASAControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoContratoCeasa", pattern = "/contrato-de-ceasa/novo/", viewId = "/faces/tributario/ceasa/contrato/edita.xhtml"),
    @URLMapping(id = "editarContratoCeasa", pattern = "/contrato-de-ceasa/editar/#{contratoCEASAControlador.id}/", viewId = "/faces/tributario/ceasa/contrato/edita.xhtml"),
    @URLMapping(id = "listarContratoCeasa", pattern = "/contrato-de-ceasa/listar/", viewId = "/faces/tributario/ceasa/contrato/lista.xhtml"),
    @URLMapping(id = "verContratoCeasa", pattern = "/contrato-de-ceasa/ver/#{contratoCEASAControlador.id}/", viewId = "/faces/tributario/ceasa/contrato/visualizar.xhtml"),
    @URLMapping(id = "renovarContratoCeasa", pattern = "/contrato-de-ceasa/renovacao/", viewId = "/faces/tributario/ceasa/contrato/renovacao.xhtml")
})
public class ContratoCEASAControlador extends PrettyControlador<ContratoCEASA> implements Serializable, CRUD {

    @EJB
    private ContratoCEASAFacade contratoCEASAFacade;
    private ParametroRendas parametroRendas;
    private CadastroEconomico locatario;
    private PontoComercial pontoComercial;
    private ConverterAutoComplete converterLocalizacao;
    private ConverterAutoComplete converterPonto;
    private ConverterAutoComplete converterPessoa;
    private ConverterGenerico converterParametro;
    private boolean efetivouContrato;
    private boolean gerouDam;
    private Localizacao localizacao;
    private ConverterGenerico converterAtividadePonto;
    private ValorDivida valorDividaContrato;
    private ConverterAutoComplete converterLotacaoVistoriadora;
    private PontoComercialContratoCEASA pontoComercialContratoCEASA;
    private ContratoCEASA contratoRenovado;
    private ConfiguracaoTributario configuracaoTributario;
    private List<DAM> listaDAMs;
    private List<ResultadoParcela> parcelas;
    private List<ResultadoParcela> parcelasLicitacao;
    private BigDecimal valorTotal;
    private ContratoCEASA contratoAlterado;
    private Boolean locatarioJaPossuiContrato = false;
    private boolean mostraDemonstrativoDeParcelas = false;
    private List<ContratoCEASA> contratosRenovacao;
    private List<ParcelaContratoCEASA> parcelasAlteracaoContrato;
    private BigDecimal valorDoCalculoDoContrato;
    private BigDecimal valorDoCalculoDoRateio;
    private Future<AssistenteContratoCeasa> future;
    private AssistenteContratoCeasa assistente;

    public ContratoCEASAControlador() {
        super(ContratoCEASA.class);
    }

    private void calculaValorTotal() {
        CalculoCEASA calculoCEASA = contratoCEASAFacade.recuperaCalculoCeasaPorContratoCeasa(selecionado);
        if (calculoCEASA != null) {
            valorDoCalculoDoContrato = contratoCEASAFacade.getConsultaDebitoFacade().buscarValorDoDebitoPorTipoDeTributo(calculoCEASA, Tributo.TipoTributo.IMPOSTO);
            valorDoCalculoDoRateio = contratoCEASAFacade.getConsultaDebitoFacade().buscarValorDoDebitoPorTipoDeTributo(calculoCEASA, Tributo.TipoTributo.TAXA);

            CalculoLicitacaoCEASA calculoLicitacao = contratoCEASAFacade.recuperaCalculoLicitacaoCeasaPorContratoCeasa(selecionado);
            valorTotal = calculoCEASA.getValorEfetivo();
            recuperaParcelasDoCeasa(calculoCEASA);
            if (calculoLicitacao != null) {
                recuperaParcelasDaLicitacao(calculoLicitacao);
            } else {
                parcelasLicitacao = Lists.newArrayList();
            }
        } else {
            if (parcelas == null) {
                parcelas = new ArrayList<>();
            }
            valorDoCalculoDoContrato = BigDecimal.ZERO;
            valorDoCalculoDoRateio = BigDecimal.ZERO;
        }
    }


    public void mostraDemonstrativoFalse() {
        mostraDemonstrativoDeParcelas = false;
    }

    public boolean isMostraDemonstrativoDeParcelas() {
        return mostraDemonstrativoDeParcelas;
    }

    public void setMostraDemonstrativoDeParcelas(boolean mostraDemonstrativoDeParcelas) {
        this.mostraDemonstrativoDeParcelas = mostraDemonstrativoDeParcelas;
    }

    public List<ContratoCEASA> getContratosRenovacao() {
        return contratosRenovacao;
    }

    public void setContratosRenovacao(List<ContratoCEASA> contratosRenovacao) {
        this.contratosRenovacao = contratosRenovacao;
    }

    public AssistenteContratoCeasa getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteContratoCeasa assistente) {
        this.assistente = assistente;
    }

    public void recuperaParcelasDoCeasa(CalculoCEASA calc) {
        parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calc.getId()).executaConsulta().getResultados();
        if (parcelas != null) {
            Collections.sort(parcelas, new OrdenaResultadoParcelaPorVencimento());

        }
    }

    public void recuperaParcelasDaLicitacao(CalculoLicitacaoCEASA calc) {
        parcelasLicitacao = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calc.getId()).executaConsulta().getResultados();
    }

    public ContratoCEASA getContratoAlterado() {
        return contratoAlterado;
    }

    public void setContratoAlterado(ContratoCEASA contratoAlterado) {
        this.contratoAlterado = contratoAlterado;
    }

    public Boolean getLocatarioJaPossuiContrato() {
        return locatarioJaPossuiContrato;
    }

    public void setLocatarioJaPossuiContrato(Boolean locatarioJaPossuiContrato) {
        this.locatarioJaPossuiContrato = locatarioJaPossuiContrato;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public List<ResultadoParcela> getParcelasLicitacao() {
        return parcelasLicitacao;
    }

    public void setParcelasLicitacao(List<ResultadoParcela> parcelasLicitacao) {
        this.parcelasLicitacao = parcelasLicitacao;
    }

    public BigDecimal getValorDoCalculoDoContrato() {
        return valorDoCalculoDoContrato != null ? valorDoCalculoDoContrato : BigDecimal.ZERO;
    }

    public void setValorDoCalculoDoContrato(BigDecimal valorDoCalculoDoContrato) {
        this.valorDoCalculoDoContrato = valorDoCalculoDoContrato;
    }

    public BigDecimal getValorDoCalculoDoRateio() {
        return valorDoCalculoDoRateio != null ? valorDoCalculoDoRateio : BigDecimal.ZERO;
    }

    public void setValorDoCalculoDoRateio(BigDecimal valorDoCalculoDoRateio) {
        this.valorDoCalculoDoRateio = valorDoCalculoDoRateio;
    }

    @Override
    public AbstractFacade getFacede() {
        return contratoCEASAFacade;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public PontoComercial getPontoComercial() {
        return pontoComercial;
    }

    public void setPontoComercial(PontoComercial pontoComercial) {
        this.pontoComercial = pontoComercial;
    }

    public boolean isEfetivouContrato() {
        return efetivouContrato;
    }

    public void setEfetivouContrato(boolean efetivouContrato) {
        this.efetivouContrato = efetivouContrato;
    }

    public boolean isGerouDam() {
        return gerouDam;
    }

    public void setGerouDam(boolean gerouDam) {
        this.gerouDam = gerouDam;
    }

    public CadastroEconomico getLocatario() {
        return locatario;
    }

    public void setLocatario(CadastroEconomico locatario) {
        this.locatario = locatario;
    }

    public ParametroRendas getParametroRendas() {
        return parametroRendas;
    }

    public void setParametroRendas(ParametroRendas parametroRendas) {
        this.parametroRendas = parametroRendas;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contrato-de-ceasa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novoContratoCeasa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            validarPermissaoUsuario();
            definirSessao();
            if (selecionado == null) {
                super.novo();
                configuracaoTributario = contratoCEASAFacade.getConfiguracaoTributarioFacade().retornaUltimo();
                selecionado.setLotacaoVistoriadora(configuracaoTributario.getCeasaLotacaoVistoriadora());
                this.parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
                validarParametroCeasa();
                sugereParametroRendas();
                locatario = null;
                efetivouContrato = false;
                gerouDam = false;
                recuperarParametrosContrato();
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
            redireciona();
        }
    }

    private void validarPermissaoUsuario() {
        if (!usuarioTemPermissao()) {
            throw new ValidacaoException("Não foi possível continuar! Você não tem autorização para cadastrar novos contratos.");
        }
    }

    private void validarParametroCeasa() {
        if (parametroRendas == null) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("Parâmetro de CEASA não encontrado para o exercício corrente.");
        }
        if (parametroRendas.getDataInicioContrato() == null || parametroRendas.getDataFimContrato() == null) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("Não foram definidas as datas de início e término do contrato no Parâmetro de CEASA!");
        } else if (parametroRendas.getDataInicioContrato().after(contratoCEASAFacade.getSistemaFacade().getDataOperacao())) {
            throw new ValidacaoException().adicionarMensagemDeOperacaoNaoPermitida("A data corrente é menor que a data de parâmetro de início de contrato!");
        }
    }

    private void recuperarParametrosContrato() {
        localizacao = new Localizacao();
        selecionado.setLotacaoVistoriadora(configuracaoTributario.getCeasaLotacaoVistoriadora());
        selecionado.setUsuarioInclusao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
        pontoComercialContratoCEASA = new PontoComercialContratoCEASA();
        this.parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
        selecionado.setValorUFMAtual(contratoCEASAFacade.getMoedaFacade().buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
        selecionado.setDataInicio(contratoCEASAFacade.getSistemaFacade().getDataOperacao());
        selecionado.setDataFim(parametroRendas.getDataFimContrato());
        selecionado.setQuantidadeParcelas(contratoCEASAFacade.mesVigente(selecionado.getDataInicio(), parametroRendas));
        selecionado.setPeriodoVigencia(contratoCEASAFacade.mesVigente(selecionado.getDataInicio(), parametroRendas));
    }

    @URLAction(mappingId = "renovarContratoCeasa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void renovacaoAutomaticaDeContrato() {
        selecionado = new ContratoCEASA();
        configuracaoTributario = contratoCEASAFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        contratosRenovacao = new ArrayList<>();
        selecionado.setUsuarioOperacao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
        recuperarParametrosContrato();
        this.parametroRendas = recuperaParametroRendas(getSistemaControlador().getDataOperacao());
        selecionado.setDataOperacao(getSistemaControlador().getDataOperacao());
    }

    private boolean usuarioTemPermissao() {
        return contratoCEASAFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.PERMITIR_CADASTRAR_NOVOS_CONTRATOS_DE_CEASA);
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public void verificarSePodeCriarContratoNovoParaCMC() {
        List<ContratoCEASA> contratoRecuperado = contratoCEASAFacade.recuperarContratoCeasa(locatario, selecionado.getId());
        if (contratoRecuperado == null) {
            locatarioJaPossuiContrato = false;
            FacesUtil.atualizarComponente("Formulario");
        } else {
            locatarioJaPossuiContrato = true;
            FacesUtil.addOperacaoNaoPermitida("O Cadastro Econômico selecionado possui um Contrato de Ceasa vigente");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        this.setLocatario((CadastroEconomico) obj);
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }


    public String formataUfm() {
        DecimalFormat dcf = new DecimalFormat("#,##0.00");
        return dcf.format(contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM());
    }

    @URLAction(mappingId = "editarContratoCeasa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        pontoComercialContratoCEASA = new PontoComercialContratoCEASA();
        configuracaoTributario = contratoCEASAFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        selecionado.setLotacaoVistoriadora(configuracaoTributario.getCeasaLotacaoVistoriadora());
        if (!selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            localizacao = selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao();
        }
        this.parametroRendas = recuperaParametroRendas(selecionado.getDataInicio());
        atribuiValoresObjetos();
    }

    @URLAction(mappingId = "verContratoCeasa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        calculaValorTotal();
    }

    public void atribuiValoresObjetos() {
        atribuiValoresObjetos(selecionado);
    }

    public void atribuiValoresObjetosContratoAlterado() {
        atribuiValoresObjetos(contratoAlterado);
    }

    private void atribuiValoresObjetos(ContratoCEASA contrato) {
        this.valorDividaContrato = contratoCEASAFacade.retornaValorDividaDoContrato(selecionado);
        locatario = selecionado.getLocatario();
        efetivouContrato = isContratoEfetivado();
        gerouDam = false;
        for (PontoComercialContratoCEASA ponto : contrato.getPontoComercialContratoCEASAs()) {
            BigDecimal valorEmReais = ponto.getValorTotalContrato().multiply(contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM());//VALOR CALCULADO POR MES
            BigDecimal valorPorMes = ponto.getValorCalculadoMes().multiply(contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM());//VALOR CALCULADO POR MES
            ponto.setValorReal(valorEmReais.setScale(2, RoundingMode.HALF_EVEN));
            ponto.setValorTotalMes(valorPorMes.setScale(2, RoundingMode.HALF_EVEN));
        }
    }


    @Override
    public void salvar() {
        if (validaCamposControlador()) {
            selecionado.setPeriodoVigencia(selecionado.getQuantidadeParcelas());
            selecionado.setLocatario(locatario);
            selecionado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
            if (!selecionado.getPontoComercialContratoCEASAs().isEmpty() &&
                selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao() != null) {
                selecionado.setTipoUtilizacao(selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getTipoOcupacaoLocalizacao());
            }
            if (validaCampos()) {
                try {
                    if (selecionado.getId() == null) {
                        contratoCEASAFacade.salvarNovo(selecionado, getSistemaControlador().getExercicioCorrente());
                        ValorDivida valorDivida = contratoCEASAFacade.retornaValorDividaDoContrato(selecionado);
                        contratoCEASAFacade.getdAMFacade().geraDAM(valorDivida);
                    } else {
                        contratoCEASAFacade.salvar(selecionado, getSistemaControlador().getExercicioCorrente());
                    }
                    efetivouContrato = SituacaoContratoCEASA.ATIVO.equals(selecionado.getSituacaoContrato());
                    FacesUtil.addInfo("Salvo com sucesso!", "");
                } catch (Exception e) {
                    FacesUtil.addError("Houve uma exceção do sistema: ", e.getMessage().toString());
                }
            }
        }
    }

    public void salvarRenovacao() {
        contratoCEASAFacade.salvar(selecionado, getSistemaControlador().getExercicioCorrente());
    }

    private boolean validaCampos() {
        boolean resultado = true;

        if (selecionado.getDataInicio() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe a Data de Início do Contrato."));
        }

        if (selecionado.getLocatario() == null && selecionado.getLocatario().getId() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe o Locatário."));
        }

        if (this.parametroRendas == null && this.parametroRendas.getId() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe o Parâmetro para a Lotação informada."));
        }

        if (selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe o(s) Ponto(s) Comercial(ais) objetos do contrato."));
        }

        if (selecionado.getTipoUtilizacao() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe o Tipo de Ponto Comercial."));
        }
        if (selecionado.getUsuarioInclusao() == null && selecionado.getUsuarioInclusao().getId() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe o usuário."));
        }

        if (selecionado.getValorLicitacao() != null && selecionado.getValorLicitacao().doubleValue() < new Double("0")) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "O Campo Valor da Licitação deve ser maior ou igual a zero."));
        }

        return resultado;
    }

    public ValorDivida getValorDividaContrato() {
        return valorDividaContrato;
    }

    public void setValorDividaContrato(ValorDivida valorDividaContrato) {
        this.valorDividaContrato = valorDividaContrato;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoUtilizacaoRendasPatrimoniais t : TipoUtilizacaoRendasPatrimoniais.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterParametro() {
        if (converterParametro == null) {
            converterParametro = new ConverterGenerico(ParametroRendas.class, contratoCEASAFacade.getParametroFacade());
        }
        return converterParametro;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, contratoCEASAFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public Converter getConverterLocalizacao() {
        if (converterLocalizacao == null) {
            converterLocalizacao = new ConverterAutoComplete(Localizacao.class, contratoCEASAFacade.getLocalizacaoFacade());
        }
        return converterLocalizacao;
    }

    public Converter getConverterPonto() {
        if (converterPonto == null) {
            converterPonto = new ConverterAutoComplete(PontoComercial.class, contratoCEASAFacade.getPontoComercialFacade());
        }
        return converterPonto;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return contratoCEASAFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<Localizacao> completaLocalizacao(String parte) {
        UsuarioSistema usuarioCorrente = contratoCEASAFacade.getUsuarioSistemaFacade().recuperar(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente().getId());
        if (usuarioCorrente.temEstaLotacao(selecionado.getLotacaoVistoriadora(), SistemaFacade.getDataCorrente())) {
            return contratoCEASAFacade.getLocalizacaoFacade().listaFiltrandoPorLotacao(selecionado.getLotacaoVistoriadora(),
                parte.trim(), "descricao");
        } else {
            return new ArrayList<>();
        }

    }

    public List<PontoComercial> completaPonto(String parte) {
        List<PontoComercial> lista = new ArrayList<PontoComercial>();
        if (localizacao == null || localizacao.getId() == null) {
            PontoComercial erro = new PontoComercial();
            erro.setNumeroBox("Selecione a Localização desejada.");
            lista.add(erro);
        } else {
            lista = contratoCEASAFacade.getPontoComercialFacade().buscarPontosQueNaoEstaoEmContratoCeasa(localizacao, parte.trim());
            if (lista.isEmpty()) {
                PontoComercial erro = new PontoComercial();
                erro.setNumeroBox("Não há pontos Comerciais cadastrados para a Localização.");
                lista.add(erro);
            }
        }
        return lista;
    }

    public List<PontoComercial> completaPontosQueNaoEstaoEmContrato(String parte) {
        List<PontoComercial> lista = contratoCEASAFacade.getPontoComercialFacade().buscarPontosQueNaoEstaoEmContratoCeasa(localizacao, parte.trim());
        if (contratoAlterado != null) {
            for (PontoComercialContratoCEASA pontoComercial : selecionado.getPontoComercialContratoCEASAs()) {
                boolean temPontoComercial = false;
                for (PontoComercialContratoCEASA pontoComercialNovo : contratoAlterado.getPontoComercialContratoCEASAs()) {
                    if (pontoComercial.getPontoComercial().equals(pontoComercialNovo.getPontoComercial())) {
                        temPontoComercial = true;
                    }
                }
                if (!temPontoComercial) {
                    lista.add(pontoComercial.getPontoComercial());
                }
            }
        }
        if (lista.isEmpty()) {
            PontoComercial erro = new PontoComercial();
            erro.setNumeroBox("Ponto(s) Comercial(is) não encontrado(s) ou ocupado(s).");
            lista.add(erro);
        }
        return lista;
    }

    public List<SelectItem> getParametros() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        if (parametroRendas == null || parametroRendas.getId() == null) {
            toReturn.add(new SelectItem(null, " "));
        } else {
            toReturn.add(new SelectItem(parametroRendas, parametroRendas.toString()));
        }
        return toReturn;
    }


    public String getTipoPessoa() {
        String tipo = "";
        if (locatario != null && locatario.getId() != null) {
            if (locatario.getPessoa() instanceof PessoaFisica) {
                tipo = "Fisica";
            } else if (locatario.getPessoa() instanceof PessoaJuridica) {
                tipo = "Juridica";
            }
        }
        return tipo;
    }

    public List<EnderecoCorreio> getEnderecos() {
        if (locatario == null || locatario.getId() == null) {
            return new ArrayList<EnderecoCorreio>();
        } else {
            return contratoCEASAFacade.getEnderecoFacade().enderecoPorPessoa(locatario.getPessoa());
        }
    }


    public List<Telefone> getTelefones() {
        if (locatario == null || locatario.getId() == null) {
            return new ArrayList<Telefone>();
        } else {
            return contratoCEASAFacade.getPessoaFacade().telefonePorPessoa(locatario.getPessoa());
        }
    }

    public boolean isPodeImprimirContrato() {
        boolean imprime = true;

// Comentado a pedido do Pessoal do CEASA. O Contrato poderá sim ser impresso sem o pagamento do DAM da Licitação
//
//        if (isImprimeDAMLicitacaoHabilitado()) {
//            ValorDivida valorDivida = facade.retornaValorDividaLicitacaoDoContrato(selecionado);
//            valorDivida = facade.recuperaValordivida(valorDivida);
//
//            SituacaoParcelaValorDivida situacaoParcelaValorDivida = null;
//            for (ParcelaValorDivida p : valorDivida.getParcelaValorDividas()) {
//                situacaoParcelaValorDivida = consultaDebitoFacade.getUltimaSituacao(p);
//                if ((situacaoParcelaValorDivida.getSituacaoParcela() != SituacaoParcela.PAGO)
//                        && (situacaoParcelaValorDivida.getSituacaoParcela() != SituacaoParcela.PAGO_COTA_UNICA)
//                        && (situacaoParcelaValorDivida.getSituacaoParcela() != SituacaoParcela.PAGO_REFIS)) {
//                    imprime = false;
//                    break;
//                }
//            }
//        }
        return imprime;
    }

    public void verificaSeTemDocumentoOficialGerado() {
        if (selecionado.getDocumentoOficial() != null) {
            FacesUtil.executaJavaScript("dialogDocumentoOficial.show()");
        } else {
            imprimeNovoContrato();
        }
    }

    public void imprimeNovoContrato() {
        try {
            contratoCEASAFacade.geraNovoDocumento(selecionado, getSistemaControlador());
            FacesUtil.executaJavaScript("dialogDocumentoOficial.hide()");
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void imprimeContratoExistente() {
        try {
            contratoCEASAFacade.geraDocumentoExistente(selecionado, getSistemaControlador());
            FacesUtil.executaJavaScript("dialogDocumentoOficial.hide()");
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void gerarDebitoQuandoNaoGerado() {
        if (!contratoCEASAFacade.existemParcelasEmAberto(selecionado)) {
            salvarRenovacao();
        }
    }

    public void imprimeDAM() {
        if (contratoCEASAFacade.existemParcelasEmAberto(selecionado)) {
            String[] contrato;
            if (selecionado.getNumeroContrato().contains("-")) {
                contrato = selecionado.getNumeroContrato().split("-");
            } else {
                contrato = new String[]{"0"};
            }
            CalculoCEASA calculoCEASA = contratoCEASAFacade.recuperaCalculoCeasaPorContratoCeasa(selecionado);
            List<DAM> damsRecuperado = contratoCEASAFacade.getdAMFacade().recuperaDAMsPeloCalculo(calculoCEASA, DAM.Situacao.ABERTO);
            List<DAM> damParaGerar = new ArrayList<>();
            ValorDivida valorDivida = contratoCEASAFacade.retornaValorDividaDoContrato(selecionado);
            if (!damsRecuperado.isEmpty()) {
                for (DAM dams : damsRecuperado) {
                    damParaGerar.add(dams);
                }

                ImprimeDAM imprimeDAM = parametrosImprimeDam(contrato);
                try {
                    imprimeDAM.imprimirDamUnicoViaApi(damParaGerar);
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
                return;
            }
            ImprimeDAM imprimeDAM = parametrosImprimeDam(contrato);
            List<DAM> dam = null;
            try {
                dam = contratoCEASAFacade.getdAMFacade().geraDAMs(valorDivida.getParcelaValorDividas());
                imprimeDAM.imprimirDamUnicoViaApi(dam);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }
    }

    private ImprimeDAM parametrosImprimeDam(String[] contrato) {
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        imprimeDAM.adicionarParametro("INSCRICAO_CONTRIBUINTE", locatario.getInscricaoCadastral());
        imprimeDAM.adicionarParametro("REFERENCIA", selecionado.getQuantidadeParcelas() + "/" + contrato[contrato.length - 1]);
        imprimeDAM.adicionarParametro("RENOVACAO", contrato[contrato.length - 1]);
        return imprimeDAM;
    }


    public void imprimeDamLicitacao() throws JRException, IOException {
        if (contratoCEASAFacade.existemParcelasEmAbertoLicitacao(selecionado)) {
            HashMap parameters = new HashMap();
            ValorDivida aImprimir = contratoCEASAFacade.retornaValorDividaLicitacaoDoContrato(selecionado);
            Pessoa locatario = selecionado.getLocatario().getPessoa();
            ImprimeDAM dam = new ImprimeDAM();
            dam.adicionarParametro("INSCRICAO_CONTRIBUINTE", selecionado.getLocatario().getInscricaoCadastral());
            try {
                ValorDivida valorDivida = contratoCEASAFacade.retornaValorDividaLicitacaoDoContrato(selecionado);
                List<DAM> dams = contratoCEASAFacade.getdAMFacade().geraDAMs(valorDivida.getParcelaValorDividas());
                dam.imprimirDamUnicoViaApi(dams);
            } catch (Exception ex) {
                FacesUtil.addError("Não foi possível continuar!", ex.getMessage());
            }
        } else {
            FacesUtil.addInfo("Atenção!", "Não existe parcelas em Aberto!");
        }
    }

    private void validarRenovacaoContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPontoComercialContratoCEASAs() == null || selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui ponto comercial adicionado, não é possível continuar com a renovação.");
        }
        if ("".equals(selecionado.getMotivoOperacao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O motivo da operação é obrigatório.");
        }
        if (contratoRenovado.getQuantidadeParcelas() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui parcelas para renovação!");
        }
        ve.lancarException();
    }

    public void renovarContrato() {
        try {
            validarRenovacaoContrato();

            selecionado.setSituacaoContrato(SituacaoContratoCEASA.RENOVADO);
            contratoCEASAFacade.salvar(selecionado);
            contratoRenovado = contratoCEASAFacade.salvarContrato(contratoRenovado);
            try {
                ProcessoCalculoCEASA processoCalculoCEASA = contratoCEASAFacade.gerarProcessoCalculo(contratoRenovado, BigDecimal.ZERO);
                contratoCEASAFacade.gerarDebito(processoCalculoCEASA, contratoRenovado);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
            selecionado = contratoRenovado;
            efetivouContrato = isContratoEfetivado();
            FacesUtil.addInfo("Contrato renovado com sucesso!", "");
            RequestContext.getCurrentInstance().execute("widgetDialogRenovacao.hide()");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public ConverterGenerico getConverterAtividadePonto() {
        if (converterAtividadePonto == null) {
            converterAtividadePonto = new ConverterGenerico(AtividadePonto.class, contratoCEASAFacade.getAtividadeFacade());
        }
        return converterAtividadePonto;
    }

    public List<SelectItem> getAtividadePontos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (AtividadePonto a : contratoCEASAFacade.getAtividadeFacade().listaOrdenando("obj.descricao")) {
            toReturn.add(new SelectItem(a, a.getDescricao()));
        }
        return toReturn;
    }


    public void efetivaContrato() {
        if (validaEfetivacao()) {
            selecionado.setSituacaoContrato(SituacaoContratoCEASA.ATIVO);
            salvar();
            RequestContext.getCurrentInstance().execute("widgetDialogEfetivacao.hide()");
            FacesUtil.redirecionamentoInterno("/contrato-de-ceasa/ver/" + selecionado.getId());
        }
    }

    public void chamaDialogConfirmacao() {
        if (validaCamposControlador()) {
            selecionado.setLocatario(locatario);
            selecionado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
            if (selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao() != null) {
                selecionado.setTipoUtilizacao(selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getTipoOcupacaoLocalizacao());
            }
            if (validaCampos()) {
                if (selecionado.getValorLicitacao() == null || selecionado.getValorLicitacao().compareTo(BigDecimal.ZERO) == 0) {
                    FacesUtil.executaJavaScript("widgetConfirmaSemValorLicitacao.show()");
                } else {
                    FacesUtil.executaJavaScript("widgetConfirmaComValorLicitacao.show()");
                }
            }
        }
    }

    public void chamaDialogEfetivacao() {
        FacesUtil.atualizarComponente("formEfetivacao");
        FacesUtil.executaJavaScript("widgetDialogEfetivacao.show()");
    }

    public boolean validaCamposControlador() {
        boolean resultado = true;
        if (parametroRendas == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Não existe um parâmetro vigente cadastrado.");
        }
        if (locatario == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o locatário.");
        }
        if (selecionado.getDataInicio() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a data de início.");
        }
        if (this.getLocalizacao() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe uma localização.");
        }
        if (selecionado.getAtividadePonto() == null) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a atividade do ponto comercial.");
        }
        if (selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe ao menos um ponto comercial.");
        }
        return resultado;
    }

    public boolean validarQuantidadeParcela(ContratoCEASA contrato) {
        return (contrato != null && contrato.getQuantidadeParcelas() != null && contrato.getQuantidadeParcelas() <= contrato.getPeriodoVigencia() && contrato.getQuantidadeParcelas() > 0);
    }

    public List<ParcelaContratoCEASA> getSimulacaoParcelas() {
        List<ParcelaContratoCEASA> lista = new ArrayList<>();
        if (parametroRendas != null) {
            if (validarQuantidadeParcela(selecionado)) {
                int qtdeParcelas = selecionado.getQuantidadeParcelas();
                if (selecionado.getDiaVencimento() != null && qtdeParcelas != 0) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(selecionado.getDataInicio());
                    atribuirValorParaSimulacao(lista, qtdeParcelas, c, selecionado);
                }
            }
        }
        return lista;
    }

    public List<ParcelaContratoCEASA> getParcelasAlteracaoContrato() {
        return parcelasAlteracaoContrato;
    }

    public void setParcelasAlteracaoContrato(List<ParcelaContratoCEASA> parcelasAlteracaoContrato) {
        this.parcelasAlteracaoContrato = parcelasAlteracaoContrato;
    }

    private int getQuantidadeParcelasAbertas() {
        int qtde = 0;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                qtde++;
            }
        }
        Integer mesVigente = contratoCEASAFacade.mesVigente(new Date(), parametroRendas);
        if (qtde > mesVigente) {
            qtde = mesVigente;
        }
        return qtde;
    }

    public void calcularSimulacaoParcelasAlteracaoContrato() {
        List<ParcelaContratoCEASA> lista = new ArrayList<>();
        if (parametroRendas != null) {
            int qtdeParcelas = 0;
            if (getSaldoTotal().compareTo(BigDecimal.ZERO) > 0) {
                qtdeParcelas = getQuantidadeParcelasAbertas();
            }
            if (qtdeParcelas <= 0 && parametroRendas.getQuantidadeParcelas() != null
                && getSaldoTotal().compareTo(BigDecimal.ZERO) > 0) {
                qtdeParcelas = parametroRendas.getQuantidadeParcelas();
            }
            if (contratoAlterado.getQuantidadeParcelas() == null) {
                contratoAlterado.setQuantidadeParcelas(qtdeParcelas);
            } else if (contratoAlterado.getQuantidadeParcelas() > qtdeParcelas) {
                contratoAlterado.setQuantidadeParcelas(qtdeParcelas);
            }
            if (contratoAlterado.getDiaVencimento() != null && qtdeParcelas > 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(getSistemaControlador().getDataOperacao());
                atribuirValorParaSimulacao(lista, contratoAlterado.getQuantidadeParcelas(), c, contratoAlterado);
            }
        }
        parcelasAlteracaoContrato = lista;
    }

    public List<ParcelaContratoCEASA> getSimulacaoParcelasRenovacao() {
        List<ParcelaContratoCEASA> lista = new ArrayList<>();
        if (contratoRenovado != null && parametroRendas != null) {
            if (validarQuantidadeParcela(contratoRenovado)) {
                if (contratoRenovado.getQuantidadeParcelas() <= contratoRenovado.getPeriodoVigencia()) {
                    int qtdeParcelas = contratoRenovado.getQuantidadeParcelas();
                    if (contratoRenovado.getDiaVencimento() != null && qtdeParcelas != 0) {
                        int dia = contratoRenovado.getDiaVencimento();
                        Calendar c = Calendar.getInstance();
                        c.setTime(contratoRenovado.getDataInicio());
                        c.set(Calendar.DAY_OF_MONTH, dia);
                        atribuirValorParaSimulacao(lista, qtdeParcelas, c, contratoRenovado);
                    }
                }
            }
        }
        return lista;
    }


    private void atribuirValorParaSimulacao(List<ParcelaContratoCEASA> lista, int qtdeParcelas, Calendar c, ContratoCEASA contrato) {
        BigDecimal valorParcela;
        if (contratoAlterado != null) {
            contrato.setValorTotalContrato(getSaldoDoContrato().setScale(2, RoundingMode.HALF_EVEN));
            valorParcela = contrato.getValorTotalContrato().divide(new BigDecimal(contratoAlterado.getQuantidadeParcelas()), 2, RoundingMode.HALF_UP);
        } else if (contratoRenovado != null) {
            contrato.setValorTotalContrato(totalDoContratoRenovado());
            valorParcela = contrato.getValorTotalContrato().divide(new BigDecimal(contratoRenovado.getQuantidadeParcelas()));
        } else {
            valorParcela = totalDoContratoMes();
            contrato.setValorTotalContrato(totalDoContrato());
        }
        BigDecimal valorParcelaRateio = BigDecimal.ZERO;
        if (contratoAlterado != null) {
            if (contratoAlterado.getValorTotalRateio().compareTo(BigDecimal.ZERO) > 0) {
                contrato.setValorTotalRateio(contratoAlterado.getValorTotalRateio().setScale(2, RoundingMode.HALF_EVEN));
                contrato.setValorMensalRateio(contrato.getValorTotalRateio().divide(new BigDecimal(qtdeParcelas), 2, RoundingMode.HALF_UP));
            } else {
                contrato.setValorTotalRateio(BigDecimal.ZERO);
                contrato.setValorMensalRateio(BigDecimal.ZERO);
            }
        } else {
            calculaValorDoRateioTotalEValorPorMes(contrato);
        }

        if (contrato.getValorTotalRateio().compareTo(BigDecimal.ZERO) > 0) {
            valorParcelaRateio = contrato.getValorTotalRateio().divide(new BigDecimal(qtdeParcelas), 2, RoundingMode.HALF_UP);
        }

        int diaVencimento = parametroRendas.getDiaVencimentoParcelas().intValue();
        int diaAtual = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, diaVencimento);

        if (diaAtual > diaVencimento) {
            c.add(Calendar.MONTH, 1);
        }

        Date vencimento = c.getTime();
        for (int i = 1; i <= qtdeParcelas; i++) {
            ParcelaContratoCEASA parcela = new ParcelaContratoCEASA();
            parcela.setParcela(i);
            parcela.setVencimento(vencimento);
            parcela.setValor(valorParcela);
            parcela.setValorRateio(valorParcelaRateio);
            parcela.setValorTotal(valorParcela.add(valorParcelaRateio));
            lista.add(parcela);
            vencimento = adicionarMeses(c.getTime(), i);
        }
    }

    public Date adicionarMeses(Date dataOriginal, int qtdeMes) {
        Calendar c = Calendar.getInstance();

        c.setTime(dataOriginal);
        c.add(Calendar.MONTH, qtdeMes);
        return c.getTime();
    }


    public double roundToDecimals(double d, int c) {
        int temp = (int) ((d * Math.pow(10, c)));
        return (((double) temp) / Math.pow(10, c));
    }

    private boolean validaEfetivacao() {
        boolean resultado = true;
        if (selecionado.getQuantidadeParcelas() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe a quantidade de parcelas.");
            resultado = false;
        } else if (selecionado.getQuantidadeParcelas() <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "Informe a quantidade de parcelas.");
            resultado = false;
        } else if (this.parametroRendas.getQuantidadeParcelas() == null) {
            FacesUtil.addError("Não foi possível continuar!", "O Parâmetro da lotação deve conter a quantidade de parcelas informada.");
            resultado = false;
        } else if (selecionado.getQuantidadeParcelas() > selecionado.getPeriodoVigencia()) {
            FacesUtil.addError("Não foi possível continuar!", "A quantidade de parcelas deve ser menor ou igual ao Período de Vigência do Contrato.");
            resultado = false;
        }
        return resultado;
    }

    public List<LotacaoVistoriadora> getLotacaoVistoriadoras() {
        return contratoCEASAFacade.getLotacaoVistoriadoraFacade().listaPorUsuarioSistema(selecionado.getUsuarioInclusao());
    }

    public List<LotacaoVistoriadora> completaLotacaoVistoriadora(String parte) {
        List<LotacaoVistoriadora> lista = new ArrayList<LotacaoVistoriadora>();
        parte = parte.trim().toLowerCase();

        for (LotacaoVistoriadora l : getLotacaoVistoriadoras()) {
            if ((l.getCodigo().toString().contains(parte)) || (l.getDescricao().toLowerCase().trim().contains(parte))) {
                lista.add(l);
            }
        }
        return lista;
    }

    public Converter getConverterLotacaoVistoriadora() {
        if (converterLotacaoVistoriadora == null) {
            converterLotacaoVistoriadora = new ConverterAutoComplete(LotacaoVistoriadora.class, contratoCEASAFacade.getLotacaoVistoriadoraFacade());
        }
        return converterLotacaoVistoriadora;
    }

    public boolean isListaLotacaoMaiorQueUm() {
        return getLotacaoVistoriadoras().size() > 1;
    }

    public void sugereParametroRendas() {
        BigDecimal totalServico = BigDecimal.ZERO;
        selecionado.setQuantidadeParcelas(getQuantidadeParcelas());
        selecionado.setIndiceEconomico(this.parametroRendas.getIndiceEconomico());
        selecionado.setAreaTotalRateio(parametroRendas.getAreaTotal());
        for (ServicoRateioCeasa servico : parametroRendas.getListaServicoRateioCeasa()) {
            totalServico = totalServico.add(servico.getValor());
        }
        selecionado.setValorServicosRateio(totalServico);
    }


    public PontoComercialContratoCEASA getPontoComercialContratoCEASA() {
        return pontoComercialContratoCEASA;
    }

    public void setPontoComercialContratoCEASA(PontoComercialContratoCEASA pontoComercialContratoCEASA) {
        this.pontoComercialContratoCEASA = pontoComercialContratoCEASA;
    }

    public void adicionaPontoComercialContratoAlterado() {
        adicionaPontoComercial(contratoAlterado);
    }

    public void adicionaPontoComercial() {
        adicionaPontoComercial(selecionado);
    }

    public void adicionaPontoComercialContratoRenovado() {
        adicionaPontoComercial(contratoRenovado);
    }

    private void adicionaPontoComercial(ContratoCEASA contrato) {
        if (validarPontoComercial(contrato)) return;

        BigDecimal qntdeParcela = new BigDecimal(contrato.getQuantidadeParcelas());
        //valor total do contrato (área do box X valor do metro quadrado).
        BigDecimal valor = pontoComercialContratoCEASA.getPontoComercial()
            .getArea().multiply(pontoComercialContratoCEASA.getPontoComercial().getValorMetroQuadrado());
        BigDecimal valorTotalContrato = valor.multiply(qntdeParcela);

        pontoComercialContratoCEASA.setValorTotalContrato(valorTotalContrato);
        pontoComercialContratoCEASA.setArea(pontoComercialContratoCEASA.getPontoComercial().getArea());
        pontoComercialContratoCEASA.setValorMetroQuadrado(pontoComercialContratoCEASA.getPontoComercial().getValorMetroQuadrado());
        pontoComercialContratoCEASA.setValorCalculadoMes(valor);
        pontoComercialContratoCEASA.setContratoCEASA(contrato);

        BigDecimal valorEmReais = pontoComercialContratoCEASA.getValorTotalContrato().multiply(contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM());//VALOR CALCULADO POR MES
        BigDecimal valorPorMes = pontoComercialContratoCEASA.getValorCalculadoMes().multiply(contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM());//VALOR CALCULADO POR MES
        pontoComercialContratoCEASA.setValorReal(valorEmReais.setScale(2, RoundingMode.HALF_EVEN));
        pontoComercialContratoCEASA.setValorTotalMes(valorPorMes.setScale(2, RoundingMode.HALF_EVEN));
        contrato.getPontoComercialContratoCEASAs().add(pontoComercialContratoCEASA);

        if (pontoComercialContratoCEASA.getPontoComercial().getLocalizacao() != null) {
            if (pontoComercialContratoCEASA.getPontoComercial().getLocalizacao().getCalculaRateio() == null || pontoComercialContratoCEASA.getPontoComercial().getLocalizacao().getCalculaRateio()) {
                calculaValorDoRateioTotalEValorPorMes(contrato);
            }
        }
        pontoComercialContratoCEASA = new PontoComercialContratoCEASA();

    }

    private boolean validarPontoComercial(ContratoCEASA contrato) {
        if (localizacao == null) {
            FacesUtil.addError("Não foi possível continuar!", "Selecione a localização.");
            return true;
        }
        if (pontoComercialContratoCEASA.getPontoComercial().getValorMetroQuadrado().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Valor do M² menor ou igual a Zero.");
            return true;
        }
        if (pontoComercialContratoCEASA.getPontoComercial() != null) {
            if (pontoComercialContratoCEASA.getPontoComercial().getArea().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError("Não foi possível continuar!", "O ponto comercial não possui Área (M²) cadastrado.");
                return true;
            }
        } else {
            FacesUtil.addError("Não foi possível continuar!", "Selecione um ponto comercial.");
            return true;
        }
        if (pontoComercialContratoCEASA.getPontoComercial() != null && pontoComercialContratoCEASA.getPontoComercial().getArea().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "O ponto comercial possui Área (M²) menor ou igual a Zero.");
            return true;
        }
        if (parametroRendas == null || parametroRendas.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Para adicionar um Ponto Comercial o Parâmetro deve ser selecionado.");
            return true;
        } else if (pontoComercialContratoCEASA.getPontoComercial() == null || pontoComercialContratoCEASA.getPontoComercial().getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Selecione o Ponto Comercial desejado.");
            return true;
        } else {
            for (PontoComercialContratoCEASA p : contrato.getPontoComercialContratoCEASAs()) {
                if (p.getPontoComercial().equals(pontoComercialContratoCEASA.getPontoComercial())) {
                    FacesUtil.addError("Não foi possível continuar!", "Ponto Comercial já selecionado.");
                    return true;
                }
            }
        }
        return false;
    }

    public BigDecimal totalDoContrato() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            for (PontoComercialContratoCEASA ponto : selecionado.getPontoComercialContratoCEASAs()) {
                valor = valor.add(ponto.getValorReal());
            }
        }
        return valor;
    }

    public BigDecimal totalDoContratoRenovado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (contratoRenovado != null) {
            if (!contratoRenovado.getPontoComercialContratoCEASAs().isEmpty()) {
                for (PontoComercialContratoCEASA ponto : contratoRenovado.getPontoComercialContratoCEASAs()) {
                    valor = valor.add(ponto.getValorTotalMes().multiply(new BigDecimal(contratoRenovado.getPeriodoVigencia())));
                }
            }
        }
        return valor;
    }

    public BigDecimal totalDoContratoAlterado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!contratoAlterado.getPontoComercialContratoCEASAs().isEmpty()) {
            for (PontoComercialContratoCEASA ponto : contratoAlterado.getPontoComercialContratoCEASAs()) {
                valor = valor.add(ponto.getValorReal());
            }
        }
        return valor;
    }

    public BigDecimal totalGeralDoContrato() {
        return totalDoContrato().add(selecionado.getValorTotalRateio());
    }

    public BigDecimal totalGeralDoContratoRenovado() {
        if (contratoRenovado != null) {
            return totalDoContrato().add(contratoRenovado.getValorTotalRateio());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal totalDoContratoMes() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            for (PontoComercialContratoCEASA ponto : selecionado.getPontoComercialContratoCEASAs()) {
                valor = valor.add(ponto.getValorTotalMes());
            }
        }
        return valor;
    }

    public BigDecimal totalDoContratoAlteradoPorMes() {
        BigDecimal valor = BigDecimal.ZERO;
        if (contratoAlterado != null) {
            if (!contratoAlterado.getPontoComercialContratoCEASAs().isEmpty()) {
                for (PontoComercialContratoCEASA ponto : contratoAlterado.getPontoComercialContratoCEASAs()) {
                    valor = valor.add(ponto.getValorTotalMes().multiply(new BigDecimal(contratoAlterado.getQuantidadeParcelas())));
                }
            }
        }
        return valor;
    }

    public BigDecimal totalDoContratoRenovadoPorMes() {
        BigDecimal valor = BigDecimal.ZERO;
        if (contratoRenovado != null) {
            if (!contratoRenovado.getPontoComercialContratoCEASAs().isEmpty()) {
                for (PontoComercialContratoCEASA ponto : contratoRenovado.getPontoComercialContratoCEASAs()) {
                    valor = valor.add(ponto.getValorTotalMes().multiply(new BigDecimal(contratoRenovado.getQuantidadeParcelas())));
                }
            }
        }
        return valor;
    }


    public void poeNaSessao() {
        Web.poeTodosFieldsNaSessao(this);
    }

    private void calculaValorDoRateioTotalEValorPorMes(ContratoCEASA contrato) {
        BigDecimal qtdeParcelas = new BigDecimal(contrato.getQuantidadeParcelas());
        contrato.setValorMensalRateio((contrato.getValorServicosRateio().divide(contrato.getAreaTotalRateio())).multiply(contrato.getSomaTotalArea()));
        contrato.setValorTotalRateio(contrato.getValorMensalRateio().multiply(qtdeParcelas));
    }


    public void removePontoComercial(ActionEvent e) {
        PontoComercialContratoCEASA p = (PontoComercialContratoCEASA) e.getComponent().getAttributes().get("objeto");
        p.setContratoCEASA(null);
        selecionado.getPontoComercialContratoCEASAs().remove(p);
        calculaValorDoRateioTotalEValorPorMes(selecionado);
    }

    public ContratoCEASA getContratoRenovado() {
        return contratoRenovado;
    }

    public void setContratoRenovado(ContratoCEASA contratoCEASA) {
        this.contratoRenovado = contratoCEASA;
    }

    public boolean isContratoEfetivado() {
        return selecionado.getSituacaoContrato() != null &&
            (selecionado.getSituacaoContrato().equals(SituacaoContratoCEASA.ATIVO));
    }

    public BigDecimal getMultiplicaPeloUFM(BigDecimal valor) {
        try {
            BigDecimal ufm = contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM();
            return valor.multiply(ufm.setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDoValorTotalContratoMultiplicado() {
        try {
            return getMultiplicaPeloUFM(selecionado.getSomaDoValorTotalContrato());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Integer getQuantidadeParcelas() {
        try {
            if (selecionado.getQuantidadeParcelas() != null) {
                return selecionado.getQuantidadeParcelas();
            } else {
                return parametroRendas.getQuantidadeParcelas();
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public void atualizaCamposPessoa() {
        FacesUtil.atualizarComponente("Formulario:panelCadastro:panelLocatario:tabelaEnderecos");
        FacesUtil.atualizarComponente("Formulario:panelCadastro:panelLocatario:tabelaTelefones");
    }

    public boolean isImprimeDAMLicitacaoHabilitado() {
        return (isContratoEfetivado() && (this.selecionado.getValorLicitacao() != null && this.selecionado.getValorLicitacao().compareTo(BigDecimal.ZERO) > 0));
    }

    public ParametroRendas recuperaParametroRendas(Date dataOperacao) {
        try {
            LotacaoVistoriadora lotacao = contratoCEASAFacade.getLotacaoVistoriadoraFacade().recuperar(selecionado.getLotacaoVistoriadora().getId());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataOperacao);
            Exercicio exercicio = contratoCEASAFacade.getParametroRendasPatrimoniaisFacade().getExercicioFacade().getExercicioPorAno(cal.get(Calendar.YEAR));
            return contratoCEASAFacade.getParametroRendasPatrimoniaisFacade().recuperaParametroRendasPorExercicioLotacaoVistoriadora(exercicio, lotacao);
        } catch (Exception e) {
            return null;
        }
    }


    public boolean temContratoCadastrado() {
        if (selecionado == null || selecionado.getNumeroContrato() == null) {
            return true;
        }
        return false;
    }

    public List<DAM> getListaDAMs() {
        return listaDAMs;
    }

    public void setListaDAMs(List<DAM> listaDAMs) {
        this.listaDAMs = listaDAMs;
    }

    public void rescindirContrato() {
        if (validarRescisaoDeContrato()) {
            if (temParcelaVencida()) {
                FacesUtil.executaJavaScript("dialogRescindirParcela.show()");
            } else {
                chamaDialogRescisao();
            }
        }
    }

    private boolean validarRescisaoDeContrato() {
        if (selecionado.getSituacaoContrato() != SituacaoContratoCEASA.ATIVO) {
            FacesUtil.addError("Atenção!", "Somente contratos ATIVOS podem ser rescindidos.");
            return false;
        }
        return true;
    }

    public SituacaoContratoCEASA getSituacaoDoContratoOrinario(ContratoCEASA contrato) {
        if (contrato != null) {
            return contrato.getSituacaoContrato();
        }
        return null;
    }


    private boolean temParcelaVencida() {
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getVencimento().before(getSistemaControlador().getDataOperacao()) && !parcela.getSituacao().equals("PAGO")) {
                return true;
            }
        }
        return false;
    }

    public void chamaDialogRescisao() {
        selecionado.setDataOperacao(new Date());
        FacesUtil.atualizarComponente("confirmacaoRescisao");
        FacesUtil.executaJavaScript("dialogRescindirParcela.hide()");
        FacesUtil.executaJavaScript("dialogConfirmacaoRescisao.show()");
    }

    public void confirmarRescisaoDoContrato() {
        if (!selecionado.getMotivoOperacao().trim().equals("")) {
            selecionado.setUsuarioOperacao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacaoContrato(SituacaoContratoCEASA.RESCINDIDO);
            cancelarDamEParcelaNaoVencidas();
            contratoCEASAFacade.salvar(selecionado);
            recuperarParcelas();
            FacesUtil.executaJavaScript("dialogConfirmacaoRescisao.hide()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addInfo("Contrato rescindido com sucesso!", "");
        } else {
            FacesUtil.addError("Atenção!", "O motivo da rescisão do contrato é obrigatório.");
        }
    }

    private void recuperarParcelas() {
        CalculoCEASA calculoCEASA = contratoCEASAFacade.recuperaCalculoCeasaPorContratoCeasa(selecionado);
        parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoCEASA.getId()).executaConsulta().getResultados();
        CalculoLicitacaoCEASA calculoLicitacao = contratoCEASAFacade.recuperaCalculoLicitacaoCeasaPorContratoCeasa(selecionado);
        if (calculoLicitacao != null) {
            parcelasLicitacao = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoLicitacao.getId()).executaConsulta().getResultados();
        } else {
            parcelasLicitacao = Lists.newArrayList();
        }
    }

    public void cancelarDamEParcelaNaoVencidas() {
        for (ResultadoParcela parcela : parcelas) {
            if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) && !parcela.getVencido()) {
                ParcelaValorDivida pvd = contratoCEASAFacade.recuperaParcelaValorDivida(parcela.getIdParcela());
                contratoCEASAFacade.getdAMFacade().cancelarDamsDaParcela(pvd, contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
                contratoCEASAFacade.cancelarParcela(pvd, parcela.getValorTotal());
            }
        }

        for (ResultadoParcela parcelas : parcelasLicitacao) {
            if (SituacaoParcela.EM_ABERTO.equals(parcelas.getSituacaoEnumValue()) && !parcelas.getVencido()) {
                ParcelaValorDivida pvd = contratoCEASAFacade.recuperaParcelaValorDivida(parcelas.getIdParcela());
                contratoCEASAFacade.getdAMFacade().cancelarDamsDaParcela(pvd, contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
                contratoCEASAFacade.cancelarParcela(pvd, parcelas.getValorTotal());
            }
        }
    }


    public void encerrarContrato() {
        if (podeEncerrarContrato()) {
            if (temParcelaVencida()) {
                FacesUtil.executaJavaScript("dialogEncerramentoParcela.show()");
            } else {
                chamaDialogRescisao();
            }
        }
    }

    public void chamaDialogEncerramento() {
        selecionado.setDataOperacao(new Date());
        FacesUtil.atualizarComponente("confirmacaoEncerramento");
        FacesUtil.executaJavaScript("dialogEncerramentoParcela.hide()");
        FacesUtil.executaJavaScript("dialogConfirmacaoEncerramento.show()");
    }

    public boolean podeEncerrarContrato() {
        boolean valida = true;
        if (selecionado.getSituacaoContrato() != SituacaoContratoCEASA.ATIVO) {
            valida = false;
            FacesUtil.addError("Atenção!", " Somente contratos ATIVOS podem ser encerrados.");
        }
        if (getSistemaControlador().getDataOperacao().before(selecionado.getDataFim())) {
            valida = false;
            FacesUtil.addError("Atenção", " A data de término do contrato ainda não ocorreu e não é possível encerrá-lo. Se for o caso, proceda a rescisão do contrato.");

        }
        return valida;
    }

    public void confirmarEncerramentoDoContrato() {
        if (!selecionado.getMotivoOperacao().trim().equals("")) {
            selecionado.setUsuarioOperacao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacaoContrato(SituacaoContratoCEASA.ENCERRADO);
            cancelarDamEParcelaNaoVencidas();
            contratoCEASAFacade.salvar(selecionado);
            recuperarParcelas();
            FacesUtil.executaJavaScript("dialogConfirmacaoEncerramento.hide()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addInfo("Contrato encerrado com sucesso!", "");
        } else {
            FacesUtil.addError("Atenção!", "O motivo do encerramento do contrato é obrigatório.");
        }
    }

    public void chamaDialogAlteracaoContrato() {
        if (podeAlterarContrato()) {
            selecionado.setUsuarioOperacao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setDataOperacao(new Date());
            criarNovoContratoAlterado();
            FacesUtil.atualizarComponente("alteracaoContrato");
            FacesUtil.executaJavaScript("alteracaoContrato.show()");
        }
    }

    private boolean podeAlterarContrato() {
        boolean retorno = true;
        if (!selecionado.getSituacaoContrato().equals(SituacaoContratoCEASA.ATIVO)) {
            FacesUtil.addOperacaoNaoRealizada("Não é possível alterar contrato que não é ATIVO.");
            retorno = false;
        }
        if (localizacao == null) {
            FacesUtil.addOperacaoNaoRealizada("Não é possível alterar contrato que não possui localização.");
            retorno = false;
        }
        return retorno;
    }

    public void criarNovoContratoAlterado() {
        contratoAlterado = new ContratoCEASA();
        verificaSeTemParcelaLicitacaoEmAbertoESetaNoContratoAlterado();
        BigDecimal totalServico = BigDecimal.ZERO;
        contratoAlterado.setNumeroContrato(selecionado.getNumeroContrato());
        contratoAlterado.setDataInicio(selecionado.getDataInicio());
        contratoAlterado.setDataOperacao(getSistemaControlador().getDataOperacao());
        contratoAlterado.setTipoUtilizacao(selecionado.getTipoUtilizacao());
        contratoAlterado.setDiaVencimento(parametroRendas.getDiaVencimentoParcelas().intValue());
        contratoAlterado.setPeriodoVigencia(contratoCEASAFacade.mesVigente(contratoAlterado.getDataInicio(), parametroRendas));
        contratoAlterado.setSituacaoContrato(SituacaoContratoCEASA.ATIVO);
        contratoAlterado.setUsuarioInclusao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
        contratoAlterado.setOriginario(selecionado);
        contratoAlterado.setAtividadePonto(selecionado.getAtividadePonto());
        contratoAlterado.setLotacaoVistoriadora(selecionado.getLotacaoVistoriadora());
        contratoAlterado.setIndiceEconomico(selecionado.getIndiceEconomico());
        contratoAlterado.setValorUFMAtual(contratoCEASAFacade.getMoedaFacade().buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
        contratoAlterado.setDataFim(parametroRendas.getDataFimContrato());
        contratoAlterado.setQuantidadeParcelas(contratoCEASAFacade.mesVigente(contratoAlterado.getDataInicio(), parametroRendas));
        contratoAlterado.setLocatario(selecionado.getLocatario());
        contratoAlterado.setAreaTotalRateio(parametroRendas.getAreaTotal());
        contratoAlterado.setValorTotalRateio(selecionado.getValorTotalRateio());
        if (!selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            localizacao = selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao();
            for (ServicoRateioCeasa servico : parametroRendas.getListaServicoRateioCeasa()) {
                totalServico = totalServico.add(servico.getValor());
            }
        }
        contratoAlterado.setValorServicosRateio(totalServico);
        copiarPontosComerciais(contratoAlterado);
        FacesUtil.atualizarComponente("idAlteracaoContrato");
    }

    private void verificaSeTemParcelaLicitacaoEmAbertoESetaNoContratoAlterado() {
        CalculoLicitacaoCEASA calculoLicitacao = contratoCEASAFacade.recuperaCalculoLicitacaoCeasaPorContratoCeasa(selecionado);
        if (calculoLicitacao != null) {
            parcelasLicitacao = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoLicitacao.getId()).executaConsulta().getResultados();
            for (ResultadoParcela parcela : parcelasLicitacao) {
                if (parcela.getSituacao().equals("EM_ABERTO")) {
                    contratoAlterado.setValorLicitacao(selecionado.getValorLicitacao());
                }
            }
        } else {
            parcelasLicitacao = Lists.newArrayList();
        }
    }

    private void copiarPontosComerciais(ContratoCEASA contrato) {
        List<PontoComercialContratoCEASA> listaDePontosComercialCEASA;
        listaDePontosComercialCEASA = contratoCEASAFacade.recuperarPontoDoContrato(selecionado);
        for (PontoComercialContratoCEASA p : listaDePontosComercialCEASA) {
            PontoComercialContratoCEASA pontoNovo = new PontoComercialContratoCEASA();
            pontoNovo.setPontoComercial(p.getPontoComercial());
            pontoNovo.setValorMetroQuadrado(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setArea(p.getPontoComercial().getArea());
            pontoNovo.setValorTotalContrato(p.getValorTotalContrato());
            pontoNovo.setContratoCEASA(contrato);
            BigDecimal valorCalculadoMes = p.getPontoComercial().getArea().multiply(p.getPontoComercial().getValorMetroQuadrado());
            pontoNovo.setValorCalculadoMes(valorCalculadoMes);

            BigDecimal valor = valorCalculadoMes.multiply(BigDecimal.valueOf(contrato.getPeriodoVigencia()));
            pontoNovo.setValorTotalContrato(valor);

            BigDecimal ufm = contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM();
            BigDecimal valorPorMes = pontoNovo.getValorCalculadoMes().multiply(ufm.setScale(2, RoundingMode.HALF_UP));
            pontoNovo.setValorTotalMes(valorPorMes.setScale(2, RoundingMode.HALF_EVEN));

            BigDecimal valorEmReais = pontoNovo.getValorTotalContrato().multiply(ufm.setScale(2, RoundingMode.HALF_UP));
            pontoNovo.setValorReal(valorEmReais.setScale(2, RoundingMode.HALF_EVEN));

            contrato.getPontoComercialContratoCEASAs().add(pontoNovo);
        }
    }

    public void confirmarAlteracaoDeContrato() {
        try {
            validarConfirmarAlteracaoDeContrato();
            mostraDemonstrativoDeParcelas = true;
            calcularSimulacaoParcelasAlteracaoContrato();
            if (getSaldoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addAtencao("O contrato será gerado sem parcelas pois o saldo do contrato é zero!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarConfirmarAlteracaoDeContrato() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoOperacao() == null || selecionado.getMotivoOperacao().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo da Operação é obrigatório!");
        }
        if (contratoAlterado.getLocatario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Locatário é obrigatório!");
        } else {
            List<ContratoCEASA> contratoRecuperado = contratoCEASAFacade.recuperarContratoCeasa(contratoAlterado.getLocatario(), selecionado.getId());
            if (contratoRecuperado != null && !contratoRecuperado.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico selecionado possui um Contrato de Ceasa vigente!");
            }
        }
        if (contratoAlterado.getPontoComercialContratoCEASAs().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui Ponto Comercial adicionado.");
        }
        ve.lancarException();
    }

    public void removePontoComercialContratoAlterado(ActionEvent e) {
        PontoComercialContratoCEASA p = (PontoComercialContratoCEASA) e.getComponent().getAttributes().get("objeto");
        p.setContratoCEASA(null);
        contratoAlterado.getPontoComercialContratoCEASAs().remove(p);
    }

    public void salvarAlteracaoContrato() {

        cancelarDamEParcelaNaoVencidas();
        selecionado.setSituacaoContrato(SituacaoContratoCEASA.ALTERADO);
        contratoCEASAFacade.salvar(selecionado);
        BigDecimal saldoDoContrato = getSaldoDoContrato().add(contratoAlterado.getValorTotalRateio());
        contratoAlterado = contratoCEASAFacade.salvarContrato(contratoAlterado);
        try {
            ProcessoCalculoCEASA processoCalculoCEASA = contratoCEASAFacade.gerarProcessoCalculo(contratoAlterado, saldoDoContrato);
            if (saldoDoContrato.compareTo(BigDecimal.ZERO) > 0) {
                contratoCEASAFacade.gerarDebito(processoCalculoCEASA, contratoAlterado);
            }
            FacesUtil.executaJavaScript("alteracaoContrato.hide()");
            FacesUtil.addOperacaoRealizada("Contrato Alterado com Sucesso!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível alterar o contrato!");
            logger.debug("Não foi possível alterar o contrato! {}", e);
        }
        redireciona();
    }

    public void chamaDialogRenovacaoContrato() {
        if (contratoCEASAFacade.getMoedaFacade().recuperaValorVigenteUFM().equals(BigDecimal.ZERO)) {
            FacesUtil.addError("ATENÇÃO!", "Não existe UFM cadastrada para o exercício corrente.");
            return;
        }
        if (SituacaoContratoCEASA.ATIVO.equals(selecionado.getSituacaoContrato()) && selecionado.getDataFim().compareTo(getSistemaControlador().getDataOperacao()) >= 1) {
            FacesUtil.addError("ATENÇÃO!", "Não é possível renovar contrato que ainda está vigente.");
            return;
        }
        if (recuperaParametroRendas(getSistemaControlador().getDataOperacao()) == null) {
            FacesUtil.addOperacaoNaoPermitida("Parâmetro de Rendas Patrimoniais não encontrado! Por favor, verifique!");
            return;
        }

        if (selecionado.getSituacaoContrato() != SituacaoContratoCEASA.ATIVO) {
            FacesUtil.addError("ATENÇÃO!", "Não é possível renovar contrato que não é ATIVO.");
        } else {
            criarNovoContratoRenovado();
            int dia = contratoRenovado.getDiaVencimento();
            Calendar c = Calendar.getInstance();
            c.setTime(contratoRenovado.getDataInicio());
            c.set(Calendar.DAY_OF_MONTH, dia);
            List<ParcelaContratoCEASA> lista = new ArrayList<>();

            atribuirValorParaSimulacao(lista, contratoRenovado.getQuantidadeParcelas(), c, contratoRenovado);
            FacesUtil.atualizarComponente("formRenovacao");
            FacesUtil.executaJavaScript("widgetDialogRenovacao.show()");
        }
    }

    public void criarNovoContratoRenovado() {
        ParametroRendas parametroRendasRenovacao = recuperaParametroRendas(getSistemaControlador().getDataOperacao());

        BigDecimal totalServico = BigDecimal.ZERO;
        contratoRenovado = new ContratoCEASA();
        contratoRenovado.setOriginario(selecionado);
        String numeroSequencia = contratoCEASAFacade.getSequenciaRenovacaoContrato(selecionado);
        String[] numeroContrato = selecionado.getNumeroContrato().split("-");
        contratoRenovado.setNumeroContrato(numeroContrato[0] + "-" + (numeroSequencia));
        contratoRenovado.setDataInicio(parametroRendasRenovacao.getDataInicioContrato());
        contratoRenovado.setDiaVencimento(parametroRendasRenovacao.getDiaVencimentoParcelas().intValue());
        contratoRenovado.setLocatario(selecionado.getLocatario());
        contratoRenovado.setPeriodoVigencia(contratoCEASAFacade.mesVigente(contratoRenovado.getDataInicio(), parametroRendasRenovacao));
        contratoRenovado.setQuantidadeParcelas(contratoCEASAFacade.mesVigente(contratoRenovado.getDataInicio(), parametroRendasRenovacao));
        contratoRenovado.setLotacaoVistoriadora(selecionado.getLotacaoVistoriadora());
        contratoRenovado.setAtividadePonto(selecionado.getAtividadePonto());
        contratoRenovado.setIndiceEconomico((selecionado.getIndiceEconomico()));
        contratoRenovado.setDataFim(parametroRendasRenovacao.getDataFimContrato());
        contratoRenovado.setValorUFMAtual(contratoCEASAFacade.getMoedaFacade().buscarValorUFMParaAno(getSistemaControlador().getExercicioCorrente().getAno()));
        selecionado.setUsuarioOperacao(contratoCEASAFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataOperacao(getSistemaControlador().getDataOperacao());
        copiarPontosComerciais(contratoRenovado);
        contratoRenovado.setAreaTotalRateio(parametroRendasRenovacao.getAreaTotal());
        if (!selecionado.getPontoComercialContratoCEASAs().isEmpty()) {
            localizacao = selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao();
            if (localizacao != null && (localizacao.getCalculaRateio() == null || localizacao.getCalculaRateio())) {
                for (ServicoRateioCeasa servico : parametroRendasRenovacao.getListaServicoRateioCeasa()) {
                    totalServico = totalServico.add(servico.getValor());
                }
            }
        }
        BigDecimal qtdeParcelas = new BigDecimal(contratoRenovado.getQuantidadeParcelas());
        contratoRenovado.setValorServicosRateio(totalServico);
        contratoRenovado.setValorMensalRateio((contratoRenovado.getValorServicosRateio().divide(contratoRenovado.getAreaTotalRateio())).multiply(contratoRenovado.getSomaTotalArea()).setScale(2, RoundingMode.UP));
        contratoRenovado.setValorTotalRateio(contratoRenovado.getValorMensalRateio().multiply(qtdeParcelas));
        contratoRenovado.setSituacaoContrato(SituacaoContratoCEASA.ATIVO);
        contratoRenovado.setTipoUtilizacao(selecionado.getTipoUtilizacao());
        contratoRenovado.setUsuarioInclusao(selecionado.getUsuarioInclusao());

        parametroRendas = parametroRendasRenovacao;
        FacesUtil.atualizarComponente("dialogRenovacao");
    }

    public BigDecimal getCalcularUFMDoContratoEmReais(BigDecimal valor) {
        try {
            return valor.multiply(selecionado.getValorUFMAtual());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public void pesquisarContratosASeremRenovados() {
        if (!selecionado.getMotivoOperacao().trim().equals("")) {
            if (temUfmParaAData()) {
                contratosRenovacao = contratoCEASAFacade.buscarContratosQuePodemSerRenovados(getSistemaControlador().getDataOperacao());
                if (contratosRenovacao.size() > 0) {
                    FacesUtil.addOperacaoRealizada(contratosRenovacao.size() + " Contratos estão prontos para serem renovados, pressione o botão 'Renovar' para continuar.");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não existe contratos a serem renovados.");
                }
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("O campo Motivo é obrigatório.");
        }
    }

    public BigDecimal getCalcularValorTotalMensal() {
        try {
            return getCalcularUFMDoContratoEmReais(selecionado.getSomaDoValorCalculadoMes());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCalcularDoValorTotalContratoMultiplicado() {
        try {
            return getCalcularUFMDoContratoEmReais(selecionado.getSomaDoValorTotalContrato());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private boolean temUfmParaAData() {
        BigDecimal valor = contratoCEASAFacade.getMoedaFacade().recuperaValorUFMParaData(getSistemaControlador().getDataOperacao());
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        FacesUtil.addOperacaoNaoRealizada("Não existe valor de UFM para o exercício da operação.");
        return false;
    }

    public void verificarTerminoRenovacao() {
        if (future != null && future.isDone()) {
            assistente.setExecutando(false);
            FacesUtil.executaJavaScript("terminaRendas()");
            try {
                for (ProcessoCalculoCEASA processo : future.get().getProcessosDeCalculo()) {
                    contratoCEASAFacade.gerarDebitoContrato(processo);
                    for (CalculoCEASA calculo : processo.getCalculos()) {
                        contratoCEASAFacade.gerarDebitoLicitacao(calculo.getContrato());
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao gerar os debitos: {}", e);
            } finally {
                future = null;
            }
        }
    }

    public void encerrarFuture() {
        FacesUtil.executaJavaScript("terminaProcesso()");
    }

    public void lerListaDeContratosParaRenovacao() {
        assistente = criarAssistente();
        future = contratoCEASAFacade.lerListaDeContratosParaRenovacao(assistente);
    }

    private AssistenteContratoCeasa criarAssistente() {
        AssistenteContratoCeasa assistente = new AssistenteContratoCeasa();
        assistente.setExecutando(true);
        assistente.setDescricaoProcesso("Renovação de Contratos CEASA");
        assistente.setUsuarioSistema(Util.recuperarUsuarioCorrente());
        assistente.setExercicio(contratoCEASAFacade.getSistemaFacade().getExercicioCorrente());
        assistente.setDataAtual(contratoCEASAFacade.getSistemaFacade().getDataOperacao());
        assistente.setMotivo(selecionado.getMotivoOperacao());
        assistente.setParametroRendas(this.parametroRendas);
        assistente.setContratos(this.contratosRenovacao);
        assistente.setTotal(this.contratosRenovacao.size());

        return assistente;
    }

    public BigDecimal getValorTotalPagoNoContrato() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    valorPago = valorPago.add(parcela.getValorPago());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getValorOriginalVencidoContrato() {
        BigDecimal valorVencido = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isVencidoEEmAberto(new Date())) {
                    valorVencido = valorVencido.add(parcela.getValorOriginal());
                }
            }
        }
        return valorVencido;
    }

    public BigDecimal getValorPagoDoContrato() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    valorPago = valorPago.add(parcela.getValorPago());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getValorVencidoDoContrato() {
        BigDecimal valorVencido = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isVencidoEEmAberto(new Date())) {
                    valorVencido = valorVencido.add(parcela.getValorImposto().add(parcela.getValorTaxa()));
                }
            }
        }
        return valorVencido;
    }

    public BigDecimal getValorPagoDoRateio() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isPago()) {
                    valorPago = valorPago.add(parcela.getValorTaxa());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getValorVencidoDoRateio() {
        BigDecimal valorPago = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (parcela.isVencidoEEmAberto(new Date())) {
                    valorPago = valorPago.add(parcela.getValorTaxa());
                }
            }
        }
        return valorPago;
    }

    public BigDecimal getSaldoDoContrato() {
        if (contratoAlterado != null) {
            BigDecimal saldo = totalDoContratoAlterado().subtract(getValorPagoDoContrato()).subtract(getValorVencidoDoContrato());
            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                return BigDecimal.ZERO;
            }
            return saldo;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoDoRateio() {
        if (contratoAlterado != null) {
            BigDecimal saldo = getValorDoCalculoDoRateio().subtract(getValorPagoDoRateio()).subtract(getValorVencidoDoRateio());
            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                return BigDecimal.ZERO;
            }
            return saldo;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoTotal() {
        return getSaldoDoContrato().add(getSaldoDoRateio());
    }

    public void navegarParaProximoContrato() {
        Web.navegacao("", "/contrato-de-ceasa/ver/" + selecionado.getOriginario().getId() + "/");
    }

}
