/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DividaPublicaFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Romanini
 */
public class DividaPublicaSuperControlador extends PrettyControlador<DividaPublica> implements Serializable, CRUD {

    @EJB
    protected DividaPublicaFacade dividaPublicaFacade;
    protected ConverterAutoComplete converterUnidadeOrganizacional;
    protected ConverterAutoComplete converterHierarquiaOrganizacional;
    protected ConverterAutoComplete converterSubConta;
    protected ConverterAutoComplete converterAtoLegal;
    protected ConverterAutoComplete converterPessoa;
    protected ConverterAutoComplete converterOcorrencia;
    protected ConverterAutoComplete converterTribunal;
    protected ConverterAutoComplete converterCategoria;
    protected ConverterAutoComplete converterUnidade;
    protected ConverterAutoComplete converterProcurador;
    protected ConverterAutoComplete converterClasseCredor;
    protected ConverterAutoComplete converterClasseCredorJuros;
    protected ConverterAutoComplete converterClasseCredorOutros;
    protected ConverterAutoComplete converterPeriodicidade;
    protected ConverterAutoComplete converterIndicadorEconomico;
    protected ConverterAutoComplete converterValorIndicadorEconomico;
    protected ConverterAutoComplete converterContaReceita;
    protected ConverterAutoComplete converterExercicio;
    protected ConverterAutoComplete converterContaFinanceira;
    protected MoneyConverter moneyConverter;
    protected TramiteDividaPublica tramite;
    protected PessoaDividaPublica beneficiario;
    protected Boolean existeBeneficiarios;
    protected OcorrenciaDividaPublica odp;
    protected BigDecimal taxaJuros;
    protected Integer carencia;
    protected ParcelaDividaPublica parcelaDividaPublica;
    protected ParcelaDividaPublica parcelaDividaPublicaAux;
    protected BigDecimal valorAnterior;
    protected UnidadeOrganizacional unidadeOrganizacional;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    protected SistemaControlador sistemaControlador;
    protected SubContaDividaPublica subContaDividaPublica;
    protected UnidadeDividaPublica unidadeDividaPublica;
    protected AtoLegal atoLegal;
    protected ContaReceita contaReceita;
    protected Boolean gerouMovimento;
    protected Boolean obrigaInformarAtoLegal;
    protected IndicadorEconomico indicadorEconomico;
    protected DividaPublicaValorIndicadorEconomico dividaPublicaValorIndicadorEconomico;
    protected ContaBancariaEntidade contaBancariaEntidade;
    protected LiberacaoRecurso liberacaoRecurso;
    protected Arquivo arquivo;
    protected boolean validaTotal;
    protected List<HierarquiaOrganizacional> listaUnidades;

    public DividaPublicaSuperControlador() {
        super(DividaPublica.class);
    }

    public boolean isValidaTotal() {
        return validaTotal;
    }

    public void setValidaTotal(boolean validaTotal) {
        this.validaTotal = validaTotal;
    }

    public DividaPublicaFacade getFacade() {
        return dividaPublicaFacade;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Boolean getGerouMovimento() {
        return gerouMovimento;
    }

    public void setGerouMovimento(Boolean gerouMovimento) {
        this.gerouMovimento = gerouMovimento;
    }

    public void setaValorDivida(AjaxBehaviorEvent event) {
        BigDecimal valorDivida = (BigDecimal) ((UIOutput) event.getSource()).getValue();
        selecionado.setValorNominal(valorDivida);
    }

    public void setaTaxaJuros(AjaxBehaviorEvent event) {
        if (selecionado.getTaxaJuros() != null) {
            taxaJuros = selecionado.getTaxaJuros();
        }
    }

    public List<UnidadeOrganizacional> completaUnidadeOrganizacional(String parte) {
        return dividaPublicaFacade.getUnidadeOrganizacionalFacade().listaUnidadesUsuarioCorrenteNivel3(sistemaControlador.getUsuarioCorrente(), parte.trim(), sistemaControlador.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return dividaPublicaFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA", 3);
    }


    public List<SelectItem> getTiposDeValidacao() {
        return Util.getListSelectItemSemCampoVazio(TipoValidacao.values());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        if (subContaDividaPublica.getSubConta() != null && subContaDividaPublica.getExercicio() != null) {
            return dividaPublicaFacade.getContaFacade().buscarContasDeDestinacaoPorContaFinanceirAndExercicio(parte, subContaDividaPublica.getSubConta(), subContaDividaPublica.getExercicio());
        }
        return Lists.newArrayList();
    }

    public List<SubConta> completarContasFinanceiras(String parte) {
        List<Long> unidades = Lists.newArrayList();
        for (UnidadeDividaPublica udp : selecionado.getUnidades()) {
            unidades.add(udp.getUnidadeOrganizacional().getId());
        }
        if (!unidades.isEmpty() && subContaDividaPublica.getExercicio() != null) {
            return dividaPublicaFacade.getSubContaFacade().listaContaFinanceiraDividaPublica(parte.trim(), subContaDividaPublica.getExercicio(), unidades);
        } else {
            FacesUtil.addAtencao("A Conta Financeira é filtrada por Unidade Organizacional, Tipo de Conta Financeira(Movimento e Pagamento) e Exercício.");
            return Lists.newArrayList();
        }
    }

    private HierarquiaOrganizacional recuperarHierarquiaDaUnidade(UnidadeDividaPublica unidadeDividaPublica) {
        return dividaPublicaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(unidadeDividaPublica.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, dividaPublicaFacade.getSubContaFacade());
        }
        return converterSubConta;
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(UnidadeOrganizacional.class, dividaPublicaFacade.getUnidadeOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterIndicadorEconomico() {
        if (converterIndicadorEconomico == null) {
            converterIndicadorEconomico = new ConverterAutoComplete(IndicadorEconomico.class, dividaPublicaFacade.getIndicadorEconomicoFacade());
        }
        return converterIndicadorEconomico;
    }

    public ConverterAutoComplete getConverterValorIndicadorEconomico() {
        if (converterValorIndicadorEconomico == null) {
            converterValorIndicadorEconomico = new ConverterAutoComplete(ValorIndicadorEconomico.class, dividaPublicaFacade.getValorIndicadorEconomicoFacade());
        }
        return converterValorIndicadorEconomico;
    }

    @Override
    public void novo() {
        super.novo();
        existeBeneficiarios = false;
        odp = new OcorrenciaDividaPublica();
        tramite = new TramiteDividaPublica();
        dividaPublicaValorIndicadorEconomico = new DividaPublicaValorIndicadorEconomico();
        tramite.setDataTramite(sistemaControlador.getDataOperacao());
        beneficiario = new PessoaDividaPublica();
        liberacaoRecurso = new LiberacaoRecurso();
        taxaJuros = BigDecimal.ZERO;
        liberacaoRecurso = new LiberacaoRecurso();
        liberacaoRecurso.setDataLiberacao(sistemaControlador.getDataOperacao());
        this.selecionado.setTaxaJuros(BigDecimal.ZERO);
        valorAnterior = BigDecimal.ZERO;
        parcelaDividaPublica = new ParcelaDividaPublica();
        parcelaDividaPublicaAux = new ParcelaDividaPublica();
        unidadeOrganizacional = new UnidadeOrganizacional();
        instanciarSubContaDividaPublica();
        instanciarUnidadeDividaPublica();
        recuperaAtoLegalDaSessao();
        gerouMovimento = false;
        obrigaInformarAtoLegal = false;
        carencia = new Integer(0);
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
        recuperaAtoLegalDaSessao();
    }

    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
        recuperaAtoLegalDaSessao();
    }

    public void recuperarEditarVer() {
        selecionado = dividaPublicaFacade.recuperar(selecionado.getId());
        if (selecionado.getBeneficiarios() != null) {
            if (!selecionado.getBeneficiarios().isEmpty()) {
                existeBeneficiarios = true;
            } else {
                existeBeneficiarios = false;
            }
        } else {
            existeBeneficiarios = false;
        }
        gerouMovimento = dividaPublicaFacade.verificaGerouMovimentacao(selecionado);
        obrigaInformarAtoLegal = gerouMovimento;
        liberacaoRecurso = new LiberacaoRecurso();
        odp = new OcorrenciaDividaPublica();
        tramite = new TramiteDividaPublica();
        beneficiario = new PessoaDividaPublica();
        dividaPublicaValorIndicadorEconomico = new DividaPublicaValorIndicadorEconomico();
        taxaJuros = new BigDecimal(BigInteger.ZERO);
        if (this.selecionado.getValorIndicadoresEconomicos() != null) {
            for (DividaPublicaValorIndicadorEconomico indic : this.selecionado.getValorIndicadoresEconomicos()) {
                taxaJuros = taxaJuros.add(indic.getValorIndexador());
            }
        }
        for (UnidadeDividaPublica unidadeDividaPublica : selecionado.getUnidades()) {
            unidadeDividaPublica.setHierarquiaOrganizacional(recuperarHierarquiaDaUnidade(unidadeDividaPublica));
        }
        carencia = new Integer(0);
        parcelaDividaPublica = new ParcelaDividaPublica();
        parcelaDividaPublicaAux = new ParcelaDividaPublica();
        unidadeOrganizacional = null;
        instanciarSubContaDividaPublica();
        instanciarUnidadeDividaPublica();
        valorAnterior = BigDecimal.ZERO;
    }

    public List<CategoriaDividaPublica> completaCategoria(String parte) {
        return dividaPublicaFacade.listaPrecatorios(parte.trim());
    }

    public List<Conta> completaContaReceita(String parte) {
        return dividaPublicaFacade.getContaFacade().listaFiltrandoReceitaDivida(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Exercicio> completaExercicio(String parte) {
        return dividaPublicaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<CategoriaDividaPublica> completaCategoriaNaoPrecatorios(String parte) {
        return dividaPublicaFacade.listaNaoPrecatorios(parte.trim());
    }

    public List<DividaPublica> getListaPrecatorios() {
        return dividaPublicaFacade.listaPre();
    }

    public List<DividaPublica> getListaOutros() {
        return dividaPublicaFacade.listaOutros();
    }

    public List<CategoriaDividaPublica> completaOperCredito(String parte) {
        return dividaPublicaFacade.listaOperacaoCredito(parte.trim());
    }

    public List<DividaPublica> getListaOpeCredito() {
        return dividaPublicaFacade.listaOpeCred();
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        CategoriaDividaPublica c = (CategoriaDividaPublica) value;
        CategoriaConta categoria = dividaPublicaFacade.getCategoriaDividaPublicaFacade().retornaTipoCategoria(c);
        if (!categoria.equals(CategoriaConta.ANALITICA)) {
            message.setSummary("Operação não Permitida! ");
            message.setDetail("Conta Sintética, não pode ser utilizada. ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

    }

    public ConverterAutoComplete getConverterTribunal() {
        if (converterTribunal == null) {
            converterTribunal = new ConverterAutoComplete(Tribunal.class, dividaPublicaFacade.getTribunalFacade());
        }
        return converterTribunal;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(ContaReceita.class, dividaPublicaFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, dividaPublicaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterPeriodicidade() {
        if (converterPeriodicidade == null) {
            converterPeriodicidade = new ConverterAutoComplete(Periodicidade.class, dividaPublicaFacade.getPeriodicidadeFacade());
        }
        return converterPeriodicidade;
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, dividaPublicaFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    @Override
    public void salvar() {
        CategoriaDividaPublica categoria = ((DividaPublica) selecionado).getCategoriaDividaPublica();
        DividaPublica dp = ((DividaPublica) selecionado);
        if (validaCamposDivida(categoria, dp)) return;
        if (validaDataDivida()) return;

        if (operacao.equals(Operacoes.NOVO)) {
            if (validaNumeroProcesso(dp)) return;
            dp.setNumero(dividaPublicaFacade.retornaUltimoIdentificador().toString());
            dp.setSaldo(dp.getValorNominal());
            dividaPublicaFacade.salvarNovo(dp);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Operação Realizada ", " Registro salvo com sucesso! "));
            redireciona();
        } else {
            dividaPublicaFacade.salvar(dp);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Operação Realizada ", " Registro alterado com sucesso! "));
            redireciona();
        }
    }

    private boolean validaNumeroProcesso(DividaPublica dp) {
        if (dividaPublicaFacade.listaNumeroDocContProc(dp)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Número do Processo já existe. Informe outro número.");
            return true;
        }
        return false;
    }

    private boolean validaCamposDivida(CategoriaDividaPublica categoria, DividaPublica dp) {
        if (categoria != null) {
            if (validaCampos(categoria, dp)) return true;
            if (validaInformarAtoLegal()) return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório!  ", " O campo Natureza da Dívida Pública deve ser informado."));
            return true;
        }
        return false;
    }

    private boolean validaInformarAtoLegal() {
        if (!selecionado.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
            if (obrigaInformarAtoLegal) {
                if (selecionado.getAtosLegais().isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Ato Legal deve ser informado."));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validaDataDivida() {
        if (selecionado.getDataInicio() != null) {
            if (selecionado.getDataFim() != null) {
                if (selecionado.getDataInicio().after(selecionado.getDataFim())) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A data de Fim (" + DataUtil.getDataFormatada(selecionado.getDataFim()) + ") não pode ser menor que a Data de Inicio (" + DataUtil.getDataFormatada(selecionado.getDataInicio()) + ").");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validaCampos(CategoriaDividaPublica categoria, DividaPublica dp) {
        if (!validaSeForPrecatorio(categoria, dp)) {
            return true;
        }
        if (!validaSeForOperacaoCredito(categoria, dp)) {
            return true;
        }
        if (!validaSeForDividaPublica(categoria, dp)) {
            return true;
        }
        return false;
    }

    public void removeParcelas(ParcelaDividaPublica p) {
        ((DividaPublica) selecionado).getParcelas().remove(p);
    }

    public void adicionaParcela() {
        if (parcelaDividaPublicaAux.getDataParcela() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Data da Parcela deve ser informado."));
        } else if (parcelaDividaPublicaAux.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor de Saldo Devedor não pode ser igual a zero.");
        } else if (parcelaDividaPublicaAux.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor de Saldo Devedor não pode ser menor que zero.");
        } else if (parcelaDividaPublicaAux.getValorAmortizado().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor Amortizado não pode ser igual a zero.");
        } else if (parcelaDividaPublicaAux.getValorAmortizado().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor Amortizado não pode ser menor que zero.");
        } else if (parcelaDividaPublicaAux.getValorJuros().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Valor de Juros não pode ser menor que zero.");
        } else {
            parcelaDividaPublicaAux.setValorPrestacao(parcelaDividaPublicaAux.getValorAmortizado().add(parcelaDividaPublicaAux.getValorJuros()));
            selecionado.getParcelas().add(new ParcelaDividaPublica(parcelaDividaPublicaAux.getDataParcela(), parcelaDividaPublicaAux.getValorAmortizado(), parcelaDividaPublicaAux.getValorJuros(), parcelaDividaPublicaAux.getSaldo(), selecionado, parcelaDividaPublicaAux.getValorPrestacao()));
            parcelaDividaPublicaAux = new ParcelaDividaPublica();
        }
    }

    public void alteraParcelasTabela(ParcelaDividaPublica parcelaDividaPublica) {
        this.parcelaDividaPublica = parcelaDividaPublica;
        valorAnterior = parcelaDividaPublica.getValorAmortizado();
    }

    public void setaValorAmortizado(AjaxBehaviorEvent event) {
        BigDecimal valor = (BigDecimal) ((UIOutput) event.getSource()).getValue();
        parcelaDividaPublica.setValorAmortizado(valor);
    }

    public void setaValorJuros(AjaxBehaviorEvent event) {
        BigDecimal valor = (BigDecimal) ((UIOutput) event.getSource()).getValue();
        parcelaDividaPublica.setValorJuros(valor);
    }

    public void setaSaldoDialog() {
        if (parcelaDividaPublica.getDataParcela() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Favor Preencher a Data da Parcela!", "Favor Preencher a Data da Parcela"));
        } else if (parcelaDividaPublica.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "O Valor de Saldo Devedor não pode ser igual a zero!", "O Valor de Saldo Devedor não pode ser igual a zero!"));
        } else if (parcelaDividaPublica.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "O Valor de Saldo Devedor não pode ser menor que zero!", "O Valor de Saldo Devedor não pode ser menor que zero!"));
        } else if (parcelaDividaPublica.getValorAmortizado().compareTo(BigDecimal.ZERO) == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "O Valor Amortizado não pode ser igual a zero!", "O Valor Amortizado não pode ser igual a zero!"));
        } else if (parcelaDividaPublica.getValorAmortizado().compareTo(BigDecimal.ZERO) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "O Valor Amortizado não pode ser menor que zero!", "O Valor Amortizado não pode ser menor que zero!"));
        } else if (parcelaDividaPublica.getValorJuros().compareTo(BigDecimal.ZERO) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "O Valor de Juros não pode ser menor que zero!", "O Valor de Juros não pode ser menor que zero!"));
        } else {
            parcelaDividaPublica.setValorPrestacao(parcelaDividaPublica.getValorAmortizado().add(parcelaDividaPublica.getValorJuros()));
            selecionado.setParcelas(Util.adicionarObjetoEmLista(selecionado.getParcelas(), parcelaDividaPublica));
            parcelaDividaPublica = null;
            RequestContext.getCurrentInstance().execute("dialogAltParcela.hide()");
        }
    }

    public void alteraBeneficiariosTabela(ActionEvent evento) {
        beneficiario = (PessoaDividaPublica) evento.getComponent().getAttributes().get("ben");
    }

    public List<PessoaDividaPublica> getBeneficiariosAtivos() {
        List<PessoaDividaPublica> listaB = new ArrayList<PessoaDividaPublica>();
        if (selecionado != null) {
            for (PessoaDividaPublica pdp : ((DividaPublica) selecionado).getBeneficiarios()) {
                if (pdp.getCancelado() == Boolean.FALSE) {
                    listaB.add(pdp);
                }
            }
        }
        return listaB;
    }

    public List<PessoaDividaPublica> getBeneficiariosCancelados() {
        List<PessoaDividaPublica> listaB = new ArrayList<PessoaDividaPublica>();
        if (selecionado != null) {
            for (PessoaDividaPublica pdp : ((DividaPublica) selecionado).getBeneficiarios()) {
                if (pdp.getCancelado() == Boolean.TRUE) {
                    listaB.add(pdp);
                }
            }
        }
        return listaB;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        DividaPublica ad = ((DividaPublica) selecionado);
        return dividaPublicaFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, ad.getPessoa());
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, dividaPublicaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterClasseCredorJuros() {
        if (converterClasseCredorJuros == null) {
            converterClasseCredorJuros = new ConverterAutoComplete(ClasseCredor.class, dividaPublicaFacade.getClasseCredorFacade());
        }
        return converterClasseCredorJuros;
    }

    public ConverterAutoComplete getConverterClasseCredorOutros() {
        if (converterClasseCredorOutros == null) {
            converterClasseCredorOutros = new ConverterAutoComplete(ClasseCredor.class, dividaPublicaFacade.getClasseCredorFacade());
        }
        return converterClasseCredorOutros;
    }

    public void cancelaBeneficiariosTabela(ActionEvent evento) {
        beneficiario = (PessoaDividaPublica) evento.getComponent().getAttributes().get("ben");
    }

    public Boolean validaCancelamentoBeneficiario() {
        Boolean valida = Boolean.TRUE;
        if (beneficiario.getDataCancelamento() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Data do Cancelamento deve ser informado."));
            valida = Boolean.FALSE;
        }
        if (beneficiario.getMotivoCancelamento().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Motivo do Cancelamento deve ser informado."));
            valida = Boolean.FALSE;
        }
        return valida;
    }

    public void cancelaBeneficiario() {
        if (validaCancelamentoBeneficiario()) {
            beneficiario.setCancelado(Boolean.TRUE);
            RequestContext.getCurrentInstance().execute("dialogCancelamentoBen.hide()");
            beneficiario = new PessoaDividaPublica();
        }
    }

    public void calculaSaldoDialog() {
        if (parcelaDividaPublica.getDataParcela() == null) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " Favor preencher a data da parcela.");
        } else if (parcelaDividaPublica.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O Valor de Saldo Devedor não pode ser igual a zero.");
        } else if (parcelaDividaPublica.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O Valor de Saldo Devedor não pode ser menor que zero.");
        } else if (parcelaDividaPublica.getValorAmortizado().compareTo(BigDecimal.ZERO) == 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O Valor Amortizado não pode ser igual a zero.");
        } else if (parcelaDividaPublica.getValorAmortizado().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O Valor Amortizado não pode ser menor que zero.");
        } else if (parcelaDividaPublica.getValorJuros().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O Valor de Juros não pode ser menor que zero.");
        } else {
            parcelaDividaPublica.setValorPrestacao(parcelaDividaPublica.getValorAmortizado().add(parcelaDividaPublica.getValorJuros()));
            if (parcelaDividaPublica.getSaldo().compareTo(parcelaDividaPublica.getValorPrestacao()) > 0 || parcelaDividaPublica.getSaldo().compareTo(parcelaDividaPublica.getValorPrestacao()) < 0) {
                parcelaDividaPublica.setSaldo(parcelaDividaPublica.getSaldo().add(parcelaDividaPublica.getValorAmortizado().subtract(valorAnterior)));
            }
            selecionado.setParcelas(Util.adicionarObjetoEmLista(selecionado.getParcelas(), parcelaDividaPublica));
            RequestContext.getCurrentInstance().execute("dialogAltParcela.hide()");
        }
    }

    public BigDecimal getTotalMulta() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (ParcelaDividaPublica pdp : dp.getParcelas()) {
            if (pdp.getMulta() != null) {
                total = total.add(pdp.getMulta());
            }
        }
        return total;
    }

    public BigDecimal getTotalOutrosEncargos() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (ParcelaDividaPublica pdp : dp.getParcelas()) {
            if (pdp.getOutrosEncargos() != null) {
                total = total.add(pdp.getOutrosEncargos());
            }
        }
        return total;
    }

    public BigDecimal getTotalAmortizacao() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (ParcelaDividaPublica pdp : dp.getParcelas()) {
            total = total.add(pdp.getValorAmortizado());
        }
        return total;
    }

    public BigDecimal getTotalJuros() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (ParcelaDividaPublica pdp : dp.getParcelas()) {
            total = total.add(pdp.getValorJuros());
        }
        return total;
    }

    public BigDecimal getTotalPrestacao() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (ParcelaDividaPublica pdp : dp.getParcelas()) {
            total = total.add(pdp.getValorPrestacao());
        }
        return total;
    }

    public BigDecimal getTotalVlrIndividualizado() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getValor());
            }
        }
        return total;
    }

    public BigDecimal getTotalDebitos() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getDebitoCompensado());
            }
        }
        return total;
    }

    public BigDecimal getTotalContribuicao() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getContribPrev());
            }
        }
        return total;
    }

    public BigDecimal getTotalFgts() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getFgts());
            }
        }
        return total;
    }

    public BigDecimal getTotalIR() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getIr());
            }
        }
        return total;
    }

    public BigDecimal getTotalHonorarios() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getHonorarioAdvocaticio());
            }
        }
        return total;
    }

    public BigDecimal getTotalSaldoBeneficiarios() {
        BigDecimal total = BigDecimal.ZERO;
        DividaPublica dp = ((DividaPublica) selecionado);
        for (PessoaDividaPublica pdp : dp.getBeneficiarios()) {
            if (!pdp.getCancelado()) {
                total = total.add(pdp.getSaldoAPagar());
            }
        }

        return total;
    }

    public void removerTodasParcelas() {
        selecionado.setParcelas(new ArrayList<ParcelaDividaPublica>());
    }

    public void geraParcelas() {
        if (selecionado.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parcelas não Gerada ", " O Valor da Dívida deve ser maior que zero(0)."));
        } else if (selecionado.getQuantidadeMeses() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parcelas não Gerada", " Informe uma prazo para o parcelamento da dívida."));
        } else if (carencia == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parcelas não Gerada ", " A Carência não pode ser vazia."));
        } else if (carencia.compareTo(Integer.valueOf(0)) < 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parcelas não Gerada ", " A Carência não pode ser menor que zero(0)."));
        } else if (carencia.compareTo(selecionado.getQuantidadeMeses()) >= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Parcelas não Gerada ", " A Carência não pode ser maior ou igual ao Prazo."));
        } else if (!selecionado.getParcelas().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Para adicionar outro parcelamento. Remova todas parcelas adicionadas na lista de parcelamentos.");
        } else {
            BigDecimal valorDivida = selecionado.getValorNominal();
            BigDecimal taxaJurosMes = BigDecimal.ZERO;
            if (taxaJuros.compareTo(BigDecimal.ZERO) > 0) {
                taxaJurosMes = taxaJuros.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_EVEN);
            }
            BigDecimal valorAmortizacao = valorDivida.divide(BigDecimal.valueOf(selecionado.getQuantidadeMeses()).subtract(BigDecimal.valueOf(carencia)), 2, RoundingMode.DOWN);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selecionado.getDataInicio());
            if (carencia > 0) {
                selecionado.getParcelas().add(new ParcelaDividaPublica(selecionado.getDataInicio(), BigDecimal.ZERO, BigDecimal.ZERO, valorDivida, selecionado, BigDecimal.ZERO));
                for (int i = 1; i < carencia; i++) {
                    calendar.add(Calendar.MONTH, 1);
                    selecionado.getParcelas().add(new ParcelaDividaPublica(calendar.getTime(), BigDecimal.ZERO, BigDecimal.ZERO, valorDivida, selecionado, BigDecimal.ZERO));
                }
                calendar.add(Calendar.MONTH, 1);
            }
            selecionado.getParcelas().add(new ParcelaDividaPublica(calendar.getTime(), valorAmortizacao, BigDecimal.ZERO, valorDivida, selecionado, valorAmortizacao));
            for (int i = 1; i < (selecionado.getQuantidadeMeses() - carencia); i++) {
                valorDivida = valorDivida.subtract(valorAmortizacao);
                calendar.add(Calendar.MONTH, 1);
                selecionado.getParcelas().add(new ParcelaDividaPublica(calendar.getTime(), valorAmortizacao, BigDecimal.ZERO, valorDivida, selecionado, valorAmortizacao));
            }
            BigDecimal diferenca = selecionado.getValorNominal().subtract(getTotalPrestacao());
            if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
                selecionado.getParcelas().get(selecionado.getParcelas().size() - 1).setValorPrestacao(diferenca.add(selecionado.getParcelas().get(selecionado.getParcelas().size() - 1).getValorPrestacao()));
            }
        }
        carencia = new Integer(0);
    }

    @Override
    public AbstractFacade getFacede() {
        return dividaPublicaFacade;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return dividaPublicaFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<PessoaFisica> completaProcurador(String parte) {
        return dividaPublicaFacade.getPessoaFisicaFacade().listaProcurador(parte.trim());
    }

    public ConverterAutoComplete getConverterProcurador() {
        if (converterProcurador == null) {
            converterProcurador = new ConverterAutoComplete(PessoaFisica.class, dividaPublicaFacade.getPessoaFisicaFacade());
        }
        return converterProcurador;
    }

    public List<Pessoa> completaBeneficiarios(String parte) {
        DividaPublica dp = ((DividaPublica) selecionado);
        List<Pessoa> pessoas = dividaPublicaFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
        if (dp.getPessoa() != null) {
            if (pessoas.contains(dp.getPessoa())) {
                pessoas.remove(dp.getPessoa());
            }
        }
        return pessoas;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, dividaPublicaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<SelectItem> getQualificacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (QualificacaoBeneficiario object : QualificacaoBeneficiario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaDebito() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaDebito object : NaturezaDebito.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<UnidadeJudiciaria> completaUnidade(String parte) {
        return dividaPublicaFacade.getUnidadeJudiciariaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterUnidade() {
        if (converterUnidade == null) {
            converterUnidade = new ConverterAutoComplete(UnidadeJudiciaria.class, dividaPublicaFacade.getUnidadeJudiciariaFacade());
        }
        return converterUnidade;
    }

    public List<Tribunal> completaTribunal(String parte) {
        return dividaPublicaFacade.getTribunalFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Periodicidade> completaPeriodicidade(String parte) {
        return dividaPublicaFacade.getPeriodicidadeFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterCategoria() {
        if (converterCategoria == null) {
            converterCategoria = new ConverterAutoComplete(CategoriaDividaPublica.class, dividaPublicaFacade.getCategoriaDividaPublicaFacade());
        }
        return converterCategoria;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return dividaPublicaFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<IndicadorEconomico> completaIndicadorEconomicoMoeda(String parte) {
        return dividaPublicaFacade.getIndicadorEconomicoFacade().listaPorTipoIndicador(parte.trim(), TipoIndicador.MOEDA);
    }

    public List<IndicadorEconomico> completaIndicadorEconomicoPercentual(String parte) {
        return dividaPublicaFacade.getIndicadorEconomicoFacade().listaPorTipoIndicador(parte.trim(), TipoIndicador.INDICE_PERCENTUAL);
    }

    public List<ValorIndicadorEconomico> completaValorIndicador(String parte) {
        return dividaPublicaFacade.getIndicadorEconomicoFacade().listaValorPorIndicador(indicadorEconomico);
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, dividaPublicaFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<OcorrenciaDividaPublica> completaOcorrenciaDividaPublica(String parte) {
        return dividaPublicaFacade.getOcorrenciaDividaPublicaFacade().listaFiltrando(parte.trim(), "ocorrencia");
    }

    public ConverterAutoComplete getConverterOcorrencia() {
        if (converterOcorrencia == null) {
            converterOcorrencia = new ConverterAutoComplete(OcorrenciaDividaPublica.class, dividaPublicaFacade.getOcorrenciaDividaPublicaFacade());
        }
        return converterOcorrencia;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void validaClasseDapessoaSelecionada() {
        try {
            DividaPublica ad = ((DividaPublica) selecionado);
            ad.setClasseCredor(null);
            if (selecionado.getNaturezaDividaPublica() != null) {
                if (selecionado.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
                    List l = dividaPublicaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(" ", ad.getPessoa(), TipoClasseCredor.PRECATORIOS);
                    Preconditions.checkArgument(!l.isEmpty(), " A pessoa: " + ad.getPessoa() + " não contém uma classe do tipo Precatório em seu cadastro.");
                } else {
                    List l = dividaPublicaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(" ", ad.getPessoa(), TipoClasseCredor.DIVIDA_PUBLICA);
                    Preconditions.checkArgument(!l.isEmpty(), " A pessoa: " + ad.getPessoa() + " não contém uma classe do Tipo Dívida Pública em seu cadastro.");
                }
            }
        } catch (IllegalArgumentException e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }

    }

    public void setarPessoa(ActionEvent evento) {
        selecionado.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
        validaClasseDapessoaSelecionada();
    }

    public void setarBeneficiario(ActionEvent evento) {
        beneficiario.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
    }

    public String caminhoVisualizar() {
        return "edita";
    }

    public void adicionaOcorrencia() {
        DividaPublica dp = ((DividaPublica) selecionado);
        if (odp == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Ocorrência deve ser informado para adicionar."));
        } else if (tramite.getDataTramite() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Data da Ocorrência deve ser informado para adicionar."));
        } else {
            tramite.setDividaPublica(dp);
            tramite.setOcorrenciaDividaPublica(odp);
            dp.getTramites().add(tramite);
            tramite = new TramiteDividaPublica();
            odp = new OcorrenciaDividaPublica();
        }
    }

    public void removeOcorrencia(ActionEvent evt) {
        TramiteDividaPublica tdp = (TramiteDividaPublica) evt.getComponent().getAttributes().get("removeOcorrencia");
        ((DividaPublica) selecionado).getTramites().remove(tdp);
    }

    public Boolean validaAdicionarBeneficiario() {
        Boolean valida = Boolean.TRUE;
        if (beneficiario.getPessoa() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Parte Credora deve ser informado para adicionar."));
            valida = Boolean.FALSE;
        }
        if (beneficiario.getQualificacaoBeneficiario() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Qualificação deve ser informado para adicionar."));
            valida = Boolean.FALSE;
        }
        if (beneficiario.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Individualizado deve ser maior que zero.");
            valida = Boolean.FALSE;
        }

        return valida;
    }

    public void adicionaBeneficiario() {
        DividaPublica dp = ((DividaPublica) selecionado);
        if (validaAdicionarBeneficiario()) {
            beneficiario.setDividaPublica(dp);
            dp.setBeneficiarios(Util.adicionarObjetoEmLista(dp.getBeneficiarios(), beneficiario));
            RequestContext.getCurrentInstance().execute("dialogAltBeneficiario.hide()");
            beneficiario = new PessoaDividaPublica();
        }
        if (dp.getValorNominal().compareTo(BigDecimal.ZERO) >= 0) {
            if (getTotalSaldoBeneficiarios().compareTo(dp.getValorNominal()) >= 0) {
                FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), " O valor total dos Beneficiários de " + "<b>" + Util.formataValor(getTotalSaldoBeneficiarios()) + "</b>" + " é maior que o Valor Total do Precatórios de " + "<b>" + Util.formataValor(selecionado.getValorNominal()) + "</b>");
            }
        }
    }

    public void removeBeneficiario(ActionEvent evt) {
        PessoaDividaPublica pdp = (PessoaDividaPublica) evt.getComponent().getAttributes().get("removeBeneficiario");
        ((DividaPublica) selecionado).getBeneficiarios().remove(pdp);
    }

    public Boolean liberaParcelamento() {
        CategoriaDividaPublica dp = ((DividaPublica) selecionado).getCategoriaDividaPublica();
        if (dp != null) {
            if (dp.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {
                return false;
            }
        }
        return true;
    }

    public Boolean liberaCalculo() {
        CategoriaDividaPublica dp = ((DividaPublica) selecionado).getCategoriaDividaPublica();
        if (dp != null) {
            if (dp.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
                return false;
            }
        }
        return true;
    }

    public Boolean verificaNaturezaDivida() {
        if (selecionado.getCategoriaDividaPublica() == null) {
            return false;
        }
        CategoriaDividaPublica dp = ((DividaPublica) selecionado).getCategoriaDividaPublica();
        if (dp != null) {
            if (dp.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
                return true;
            }
        }
        existeBeneficiarios = false;
        return false;
    }

    public boolean isVerificaNaturezaDParcelamento() {
        DividaPublica dp = ((DividaPublica) selecionado);
        if (dp.getNaturezaDebito() != null && dp != null) {
            if (dp.getNaturezaDebito().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {

                return false;
            }
        }
        return true;
    }

    public boolean isVerificaNaturezaDCalculo() {
        DividaPublica dp = ((DividaPublica) selecionado);
        if (dp.getNaturezaDebito() != null && dp != null) {
            if (dp.getNaturezaDebito().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {
                return true;
            }
        }
        return false;
    }

    public TramiteDividaPublica getTramite() {
        return tramite;
    }

    public void setTramite(TramiteDividaPublica tramite) {
        this.tramite = tramite;
    }

    public Boolean getExisteBeneficiarios() {
        return existeBeneficiarios;
    }

    public void setExisteBeneficiarios(Boolean existeBeneficiarios) {
        this.existeBeneficiarios = existeBeneficiarios;
    }

    public OcorrenciaDividaPublica getOdp() {
        return odp;
    }

    public void setOdp(OcorrenciaDividaPublica odp) {
        this.odp = odp;
    }

    public ParcelaDividaPublica getParcelaDividaPublica() {
        return parcelaDividaPublica;
    }

    public void setParcelaDividaPublica(ParcelaDividaPublica parcelaDividaPublica) {
        this.parcelaDividaPublica = parcelaDividaPublica;
    }

    public BigDecimal getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(BigDecimal valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public PessoaDividaPublica getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(PessoaDividaPublica beneficiario) {
        this.beneficiario = beneficiario;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeDividaPublica.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (unidadeDividaPublica.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
        for (UnidadeDividaPublica udp : selecionado.getUnidades()) {
            if (!udp.equals(unidadeDividaPublica) && udp.getUnidadeOrganizacional().equals(unidadeDividaPublica.getHierarquiaOrganizacional().getSubordinada()) && udp.getExercicio().equals(unidadeDividaPublica.getExercicio())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é possível adicionar Unidades Orçamentárias iguais no mesmo exercício.");
            }
        }
        ve.lancarException();
    }

    public void adicionarUnidadeOrganizacional() {
        try {
            validarUnidade();
            unidadeDividaPublica.setDividaPublica(selecionado);
            unidadeDividaPublica.setUnidadeOrganizacional(unidadeDividaPublica.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidadeDividaPublica);
            instanciarUnidadeDividaPublica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarContaFinanceira() {
        try {
            validarSubContaDividaPublica();
            subContaDividaPublica.setFonteDeRecursos(subContaDividaPublica.getContaDeDestinacao().getFonteDeRecursos());
            subContaDividaPublica.setDividaPublica(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getContasFinanceiras(), subContaDividaPublica);
            instanciarSubContaDividaPublica();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void instanciarSubContaDividaPublica() {
        subContaDividaPublica = new SubContaDividaPublica();
        subContaDividaPublica.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    private void instanciarUnidadeDividaPublica() {
        unidadeDividaPublica = new UnidadeDividaPublica();
        unidadeDividaPublica.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    private void validarSubContaDividaPublica() {
        ValidacaoException ve = new ValidacaoException();
        if (subContaDividaPublica.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (subContaDividaPublica.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        }
        if (subContaDividaPublica.getContaDeDestinacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Destinação de Recurso deve ser informado.");
        }
        if (subContaDividaPublica.getTipoValidacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Validação deve ser informado.");
        }
        ve.lancarException();
        for (SubContaDividaPublica udp : selecionado.getContasFinanceiras()) {
            if (!udp.equals(subContaDividaPublica)
                && udp.getSubConta().equals(subContaDividaPublica.getSubConta())
                && udp.getContaDeDestinacao().equals(subContaDividaPublica.getContaDeDestinacao())
                && udp.getTipoValidacao().equals(subContaDividaPublica.getTipoValidacao())
                && udp.getExercicio().equals(subContaDividaPublica.getExercicio())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um regitro adicionado com a mesma conta financeira, fonte de recurso e tipo de validação!");
                break;
            }
        }
        ve.lancarException();
    }

    public void removeSubConta(SubContaDividaPublica subContaDividaPublica) {
        selecionado.getContasFinanceiras().remove(subContaDividaPublica);
    }

    public void removerUnidade(UnidadeDividaPublica unidade) {
        selecionado.getUnidades().remove(unidade);
    }

    public void editarUnidadeOrganizacional(UnidadeDividaPublica unidadeDividaPublica) {
        this.unidadeDividaPublica = (UnidadeDividaPublica) Util.clonarObjeto(unidadeDividaPublica);
        this.unidadeDividaPublica.setHierarquiaOrganizacional(recuperarHierarquiaDaUnidade(this.unidadeDividaPublica));
    }

    public void editarContaFinanceira(SubContaDividaPublica subContaDivida) {
        subContaDividaPublica = (SubContaDividaPublica) Util.clonarObjeto(subContaDivida);
    }

    public void limparSubContaAndFonte() {
        subContaDividaPublica.setSubConta(null);
        subContaDividaPublica.setFonteDeRecursos(null);
    }

    public SubContaDividaPublica getSubContaDividaPublica() {
        return subContaDividaPublica;
    }

    public void setSubContaDividaPublica(SubContaDividaPublica subContaDividaPublica) {
        this.subContaDividaPublica = subContaDividaPublica;
    }

    private boolean validaSeForDividaPublica(CategoriaDividaPublica categoria, DividaPublica dp) {
        boolean deuCerto = true;
        if (categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.DEMAIS_OBRIGACOES)
            || categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OBRIGACOES_FISCAIS)
            || categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PASSIVO_ATUARIAL)
            || categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.NAO_APLICAVEL)
            || categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OBRIGACOES_FORNECEDORES)
            || categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OBRIGACOES_PESSOAL)) {
            if (dp.getNumeroProtocolo().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número do Contrato deve ser informado.");
                deuCerto = false;
            }
            if (dp.getNumeroDocContProc().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número do Processo deve ser informado.");
                deuCerto = false;
            }
            if (dp.getExercicio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Exercício deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataHomologacao() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Data deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataInicio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Pagamento Inicial deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataFim() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Pagamento Final deve ser informado.");
                deuCerto = false;
            }
            if (dp.getQuantidadeMeses() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Teste.");
                deuCerto = false;
            }
            if (dp.getPessoa() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Pessoa deve ser informado.");
                deuCerto = false;
            }
            if (dp.getClasseCredor() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Classe deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDescricaoDivida().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Descrição deve ser informado.");
                deuCerto = false;
            }
            if (dp.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Original do Contrato dever ser maior que zero(0).");
                deuCerto = false;
            }
            if (dp.getAtosLegais().isEmpty()) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Informe pelo menos um Ato Legal para continuar a operação.");
                deuCerto = false;
            }
        }
        return deuCerto;
    }

    private boolean validaSeForPrecatorio(CategoriaDividaPublica categoria, DividaPublica dp) {
        boolean deuCerto = true;
        if (categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
            if (dp.getNumeroProtocolo().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número do Precatório deve ser informado.");
                deuCerto = false;
            }
            if (dp.getNumeroDocContProc().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número do Processo deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataHomologacao() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data deve ser informado.");
                deuCerto = false;
            }
            if (dp.getExercicio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Exercício deve ser informado.");
                deuCerto = false;
            }
            if (dp.getPessoa() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Pessoa deve ser informado.");
                deuCerto = false;
            }
            if (dp.getClasseCredor() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Classe deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDescricaoDivida().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Descrição deve ser informado.");
                deuCerto = false;
            }
            if (dp.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo valor total dever ser maior que zero (0).");
                deuCerto = false;
            }
        }
        return deuCerto;
    }

    private boolean validaSeForOperacaoCredito(CategoriaDividaPublica categoria, DividaPublica dp) {
        boolean deuCerto = true;
        if (categoria.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {
            if (dp.getNumeroDocContProc().trim().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Número do Contrato deve ser informado.");
                deuCerto = false;
            }
            if (dp.getExercicio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Exercício deve ser informado.");
                deuCerto = false;
            }
            if (dp.getPessoa() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Pessoa deve ser informado.");
                deuCerto = false;
            }
            if (dp.getClasseCredor() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Classe deve ser informado.");
                deuCerto = false;
            }
            if (dp.getIndicadorEconomico() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Moeda deve ser informado.");
                deuCerto = false;
            }

            if (dp.getIndicadorPercentual() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Indexador deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataHomologacao() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data da Contratação deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataInicio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data Inicial deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDataFim() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Data Final deve ser informado.");
                deuCerto = false;
            }

            if (dp.getPeriodicidade() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Periodicidade do Pagamento deve ser informado.");
                deuCerto = false;
            }
            if (dp.getDescricaoDivida().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Finalidade do Crédito deve ser informado.");
                deuCerto = false;
            }
            if (dp.getTaxaJuros() == null || dp.getTaxaJuros().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Taxas de Juros deve ser informado.");
                deuCerto = false;
            }
            if (dp.getValorNominal().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor Original do Contrato deve ser maior que zero(0).");
                deuCerto = false;
            }
            if (dp.getContraPartida().compareTo(BigDecimal.ZERO) <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor da Contrapartida deve ser maior que zero(0).");
                deuCerto = false;
            }
            if (dp.getAtosLegais().isEmpty()) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Informe pelo menos um Ato Legal para continuar a operação.");
                deuCerto = false;
            }
            if (dp.getContasReceitas().isEmpty()) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Informe pelo menos uma Conta de Receita para continuar a operação.");
                deuCerto = false;
            }
        }
        return deuCerto;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-publica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void adicionarAtoLegal() {
        if (validarAtoLegal()) {
            DividaPublicaAtoLegal dividaPublicaAtoLegal = new DividaPublicaAtoLegal();
            dividaPublicaAtoLegal.setAtoLegal(atoLegal);
            dividaPublicaAtoLegal.setDividaPublica(this.selecionado);
            this.selecionado.getAtosLegais().add(dividaPublicaAtoLegal);
            this.atoLegal = null;
            if (obrigaInformarAtoLegal) {
                obrigaInformarAtoLegal = false;
            }
        }
    }

    public void adicionarIndexador() {
        if (dividaPublicaValorIndicadorEconomico.getMoeda() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Moeda deve ser informado para adicionar.");
        }
        if (dividaPublicaValorIndicadorEconomico.getIndexador() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Indexador deve ser informado para adicionar.");
        }
        if (dividaPublicaValorIndicadorEconomico.getValorIndexador() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor de ser maior que zero(0).");
        } else {
            for (DividaPublicaValorIndicadorEconomico valor : this.selecionado.getValorIndicadoresEconomicos()) {
                if (valor.equals(dividaPublicaValorIndicadorEconomico)) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Este indexador já foi adicionado.");
                    dividaPublicaValorIndicadorEconomico = null;
                    return;
                }
            }
            dividaPublicaValorIndicadorEconomico.setDividaPublica(selecionado);
            this.selecionado.getValorIndicadoresEconomicos().add(dividaPublicaValorIndicadorEconomico);
            taxaJuros = taxaJuros.add(dividaPublicaValorIndicadorEconomico.getValorIndexador());
            dividaPublicaValorIndicadorEconomico = new DividaPublicaValorIndicadorEconomico();
        }
    }

    public void removerAtoLegal(DividaPublicaAtoLegal dividaPublicaAtoLegal) {
        this.selecionado.getAtosLegais().remove(dividaPublicaAtoLegal);
    }

    public void removerIndexador(DividaPublicaValorIndicadorEconomico dividaPublicaValorIndicadorEconomico) {
        taxaJuros = taxaJuros.subtract(dividaPublicaValorIndicadorEconomico.getValorIndexador());
        selecionado.getValorIndicadoresEconomicos().remove(dividaPublicaValorIndicadorEconomico);
    }

    private boolean validarAtoLegal() {
        String summary = "Operação não Permitida!  ";
        if (this.atoLegal == null) {
            FacesUtil.addError(summary, " Campo Ato Legal deve ser informado para adicionar.");
            return false;
        }
        for (DividaPublicaAtoLegal dividaPublicaAtoLegal : this.selecionado.getAtosLegais()) {
            if (dividaPublicaAtoLegal.getAtoLegal().equals(this.atoLegal)) {
                FacesUtil.addError(summary, " O Ato Legal selecionado já foi adicionado na lista.");
                return false;
            }
        }
        return true;
    }

    public void adicionarContaReceita() {
        if (validarContaReceita()) {
            DividaPublicaContaReceita dividaPublicaContaReceita = new DividaPublicaContaReceita();
            dividaPublicaContaReceita.setContaReceita(contaReceita);
            dividaPublicaContaReceita.setDividaPublica(this.selecionado);
            dividaPublicaContaReceita.setExercicio(sistemaControlador.getExercicioCorrente());
            this.selecionado.getContasReceitas().add(dividaPublicaContaReceita);
            this.contaReceita = null;
        }
    }

    public void removerContaReceita(DividaPublicaContaReceita dividaPublicaContaReceita) {
        this.selecionado.getContasReceitas().remove(dividaPublicaContaReceita);
    }

    private boolean validarContaReceita() {
        String summary = "Operação não Permitida! ";
        if (this.contaReceita == null) {
            FacesUtil.addError(summary, "O campo Conta de Receita deve ser informado para adicionar.");
            return false;
        }
        for (DividaPublicaContaReceita dividaPublicaContaReceita : this.selecionado.getContasReceitas()) {
            if (dividaPublicaContaReceita.getContaReceita().equals(this.contaReceita)) {
                FacesUtil.addError(summary, "A Conta de Receita Selecionado já foi adicionado na Lista.");
                return false;
            }
        }
        return true;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    private void recuperaAtoLegalDaSessao() {
        Object atoLegalDaSessao = Web.pegaDaSessao(AtoLegal.class);
        if (atoLegalDaSessao
            != null) {
            this.atoLegal = (AtoLegal) atoLegalDaSessao;
        }
    }

    public Boolean getObrigaInformarAtoLegal() {
        return obrigaInformarAtoLegal;
    }

    public void setObrigaInformarAtoLegal(Boolean obrigaInformarAtoLegal) {
        this.obrigaInformarAtoLegal = obrigaInformarAtoLegal;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public void excluir() {
        if (!dividaPublicaFacade.verificaGerouMovimentacao(selecionado)) {
            super.excluir();
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Possui Movimentos associados." + selecionado.getCategoriaDividaPublica().getNaturezaDividaPublica().getDescricao().toUpperCase() + ": " + selecionado.toStringAutoComplete());
        }
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public Integer getCarencia() {
        return carencia;
    }

    public void setCarencia(Integer carencia) {
        this.carencia = carencia;
    }

    public IndicadorEconomico getIndicadorEconomico() {
        return indicadorEconomico;
    }

    public void setIndicadorEconomico(IndicadorEconomico indicadorEconomico) {
        this.indicadorEconomico = indicadorEconomico;
    }

    public ParcelaDividaPublica getParcelaDividaPublicaAux() {
        return parcelaDividaPublicaAux;
    }

    public void setParcelaDividaPublicaAux(ParcelaDividaPublica parcelaDividaPublicaAux) {
        this.parcelaDividaPublicaAux = parcelaDividaPublicaAux;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public void setDividaPublicaFacade(DividaPublicaFacade dividaPublicaFacade) {
        this.dividaPublicaFacade = dividaPublicaFacade;
    }

    public void removerArquivo(ArquivoDividaPublica arquivo) {
        selecionado.getArquvios().remove(arquivo);
    }

    public void uploadArquivos(FileUploadEvent event) throws Exception {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(dividaPublicaFacade.getArquivoFacade().getMimeType(event.getFile().getFileName()));
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setDescricao(event.getFile().getFileName());

            ArquivoDividaPublica arquivoDividaPublica = new ArquivoDividaPublica();
            arquivo = dividaPublicaFacade.getArquivoFacade().novoArquivoMemoria(arquivo, event.getFile().getInputstream());
            arquivoDividaPublica.setArquivo(arquivo);
            arquivoDividaPublica.setDividaPublica(this.selecionado);

            selecionado.getArquvios().add(arquivoDividaPublica);

        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Erro ao anexar arquivo: " + ex.getMessage());
        }
    }

    public void setarArquivo(Arquivo arquivo1) {
        setArquivo(arquivo1);
    }

    public Boolean verificaSelecionado() {
        if (selecionado != null) {
            if (selecionado.getId() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<Empenho> getListaEmpenhos() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarEmpenhosPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> getListaEmpenhoEstorno() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarEmpenhosEstornoPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> getListaLiquidacao() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarLiquidacoesPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> getListaLiquidacaoEstorno() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarLiquidacoesEstornoPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> getListaPagamento() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarPagamentosPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> getListaPagamentoEstorno() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.buscarPagamentosEstornoPorDividaPublica(selecionado);
        } else {
            return new ArrayList<>();
        }
    }

    public List<MovimentoDividaPublica> getListaMovimentoDividaPublica() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.listaMovimentoDividaPublica(selecionado, TipoLancamento.NORMAL);
        } else {
            return new ArrayList<>();
        }
    }

    public List<MovimentoDividaPublica> getListaMovimentoDividaPublicaEstorno() {
        if (verificaSelecionado()) {
            return dividaPublicaFacade.listaMovimentoDividaPublica(selecionado, TipoLancamento.ESTORNO);
        } else {
            return new ArrayList<>();
        }
    }

    public BigDecimal getTotalPagamentosValorPrincipal() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosPorDividaPublica(selecionado, SubTipoDespesa.VALOR_PRINCIPAL));
        }
        return total;
    }

    public BigDecimal getTotalPagamentosValorJuros() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosPorDividaPublica(selecionado, SubTipoDespesa.JUROS));
        }
        return total;
    }

    public BigDecimal getTotalPagamentosOutrosEncargos() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosPorDividaPublica(selecionado, SubTipoDespesa.OUTROS_ENCARGOS));
        }
        return total;
    }

    public BigDecimal getTotalPagamentosEstornoValorPrincipal() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosEstornoPorDividaPublica(selecionado, SubTipoDespesa.VALOR_PRINCIPAL));
        }
        return total;
    }

    public BigDecimal getTotalPagamentosEstornoValorJuros() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosEstornoPorDividaPublica(selecionado, SubTipoDespesa.JUROS));
        }
        return total;
    }

    public BigDecimal getTotalPagamentosEstornoOutrosEncargos() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getId() != null) {
            total = total.add(dividaPublicaFacade.buscarTotalDePagamentosEstornoPorDividaPublica(selecionado, SubTipoDespesa.OUTROS_ENCARGOS));
        }
        return total;
    }

    public BigDecimal getCalculaPagamentosComValorPrincipal() {
        BigDecimal total = BigDecimal.ZERO;
        total = getTotalPagamentosValorPrincipal().subtract(getTotalPagamentosEstornoValorPrincipal());
        return total;
    }

    public BigDecimal getCalculaPagamentosComValorJuros() {
        BigDecimal total = BigDecimal.ZERO;
        total = getTotalPagamentosValorJuros().subtract(getTotalPagamentosEstornoValorJuros());
        return total;
    }

    public BigDecimal getCalculaPagamentosComOutrosEncargos() {
        BigDecimal total = BigDecimal.ZERO;
        total = getTotalPagamentosOutrosEncargos().subtract(getTotalPagamentosEstornoOutrosEncargos());
        return total;
    }

    public BigDecimal getSomaEstornoPagamentos() {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoEstorno pe : getListaPagamentoEstorno()) {
            estornos = estornos.add(pe.getValor());
        }
        return estornos;
    }

    public BigDecimal getSomaPagamentos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Pagamento p : getListaPagamento()) {
            valor = valor.add(p.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (Liquidacao l : getListaLiquidacao()) {
            liq = liq.add(l.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEstornoLiquidacoes() {
        BigDecimal liq = new BigDecimal(BigInteger.ZERO);
        for (LiquidacaoEstorno le : getListaLiquidacaoEstorno()) {
            liq = liq.add(le.getValor());
        }
        return liq;
    }

    public BigDecimal getSomaEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (Empenho e : getListaEmpenhos()) {
            valor = valor.add(e.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaEstornoEmpenhos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (EmpenhoEstorno ee : getListaEmpenhoEstorno()) {
            valor = valor.add(ee.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaTotalPagamentos() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        valor = getSomaPagamentos().subtract(getSomaEstornoPagamentos());
        return valor;
    }

    public BigDecimal getSomaMovimentoDividaPublica() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (MovimentoDividaPublica mov : getListaMovimentoDividaPublica()) {
            valor = valor.add(mov.getValor());
        }
        return valor;
    }

    public BigDecimal getSomaMovimentoDividaPublicaEstorno() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        for (MovimentoDividaPublica mov : getListaMovimentoDividaPublicaEstorno()) {
            valor = valor.add(mov.getValor());
        }
        return valor;
    }

    public BigDecimal getCalculaRestanteAPagar() {
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        valor = selecionado.getValorNominal().subtract(getSomaTotalPagamentos());
        return valor;

    }

    public boolean validaRestanteAPagar() {
        return validaTotal = getCalculaRestanteAPagar().compareTo(BigDecimal.ZERO) == 0;
    }

    public void redirecionarVerArquivo(ArquivoDividaPublica arquivoDividaPublica) {
        Arquivo arquivo = arquivoDividaPublica.getArquivo();
        if (arquivoDividaPublica.getId() != null) {
            arquivo = dividaPublicaFacade.getArquivoFacade().recuperaDependencias(arquivo.getId());
        }
        AbstractReport.poeRelatorioNaSessao(arquivo.getByteArrayDosDados());
    }

    public DividaPublicaValorIndicadorEconomico getDividaPublicaValorIndicadorEconomico() {
        return dividaPublicaValorIndicadorEconomico;
    }

    public void setDividaPublicaValorIndicadorEconomico(DividaPublicaValorIndicadorEconomico dividaPublicaValorIndicadorEconomico) {
        this.dividaPublicaValorIndicadorEconomico = dividaPublicaValorIndicadorEconomico;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public void setaDataInicio(AjaxBehaviorEvent event) {
        Date data = (Date) ((UIOutput) event.getSource()).getValue();
        ((DividaPublica) selecionado).setDataInicio(data);
        setaQuantidadeMeses();
    }

    public void setaDataInicioDateSelect(DateSelectEvent event) {
        ((DividaPublica) selecionado).setDataInicio(event.getDate());
        setaQuantidadeMeses();
    }

    public void setaPessoa(SelectEvent evt) {
        ((DividaPublica) selecionado).setPessoa((Pessoa) evt.getObject());
        ((DividaPublica) selecionado).setClasseCredor(null);
        validaClasseDapessoaSelecionada();
    }

    public void setaIndicador(SelectEvent evt) {
        indicadorEconomico = (IndicadorEconomico) evt.getObject();
        ((DividaPublica) selecionado).setIndicadorEconomico(indicadorEconomico);
    }

    public void setaDataFim(AjaxBehaviorEvent event) {
        Date data = (Date) ((UIOutput) event.getSource()).getValue();
        ((DividaPublica) selecionado).setDataFim(data);
        setaQuantidadeMeses();
    }

    public void setaDataFimDateSelect(DateSelectEvent event) {
        ((DividaPublica) selecionado).setDataFim(event.getDate());
        setaQuantidadeMeses();
    }

    public void setaQuantidadeMeses() {
        if (validaDatas()) {
            ((DividaPublica) selecionado).setQuantidadeMeses(getDiferencaMeses(((DividaPublica) selecionado).getDataInicio(), ((DividaPublica) selecionado).getDataFim()));
        } else {
            selecionado.setQuantidadeMeses(0);
        }
    }

    public void calculaPrazoTotal() {
        if (selecionado.getPrazoCarenciaDivida() != null
            && selecionado.getPrazoAmortizacaoDivida() != null) {
            selecionado.setPrazoTotalDivida(selecionado.getPrazoAmortizacaoDivida().add(selecionado.getPrazoCarenciaDivida()));
        }
    }

    private int getDiferencaMeses(Date dataIni, Date dataFim) {
        int meses = 0;
        boolean dataIniMaior = false;
        GregorianCalendar gcInicial = new GregorianCalendar();
        GregorianCalendar gcFinal = new GregorianCalendar();

        if (dataFim.after(dataIni)) {
            gcInicial.setTime(dataIni);
            gcFinal.setTime(dataFim);
        } else {
            dataIniMaior = true;
            gcInicial.setTime(dataIni);
            gcFinal.setTime(dataFim);
        }

        gcFinal.clear(Calendar.MILLISECOND);
        gcFinal.clear(Calendar.SECOND);
        gcFinal.clear(Calendar.MINUTE);
        gcFinal.clear(Calendar.HOUR_OF_DAY);
        gcFinal.clear(Calendar.DATE);

        gcInicial.clear(Calendar.MILLISECOND);
        gcInicial.clear(Calendar.SECOND);
        gcInicial.clear(Calendar.MINUTE);
        gcInicial.clear(Calendar.HOUR_OF_DAY);
        gcInicial.clear(Calendar.DATE);
        while (gcInicial.before(gcFinal)) {
            gcInicial.add(Calendar.MONTH, 1);
            meses = dataIniMaior ? --meses : ++meses;
        }
        meses++;
        return meses;
    }

    public Boolean validaDatas() {
        if (((DividaPublica) selecionado).getDataInicio() != null
            && ((DividaPublica) selecionado).getDataFim() != null
            && ((DividaPublica) selecionado).getNaturezaDividaPublica() != null) {
            if (!((DividaPublica) selecionado).getDataInicio().after(((DividaPublica) selecionado).getDataFim())) {
                return true;
            } else {
                if (selecionado.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Data Final deve ser maior que Data Inicial.");
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O data do Pagamento Final deve ser maior que a data do Pagamento Inicial.");
                }
                return false;
            }
        }
        return false;
    }

    public LiberacaoRecurso getLiberacaoRecurso() {
        return liberacaoRecurso;
    }

    public void setLiberacaoRecurso(LiberacaoRecurso liberacaoRecurso) {
        this.liberacaoRecurso = liberacaoRecurso;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public UnidadeDividaPublica getUnidadeDividaPublica() {
        return unidadeDividaPublica;
    }

    public void setUnidadeDividaPublica(UnidadeDividaPublica unidadeDividaPublica) {
        this.unidadeDividaPublica = unidadeDividaPublica;
    }
}
