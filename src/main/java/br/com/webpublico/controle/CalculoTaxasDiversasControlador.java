package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalculoTaxasDiversasFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "calculoTaxasDiversasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTaxasDividasDiversas", pattern = "/lancamento-de-taxas-diversas/novo/",
        viewId = "/faces/tributario/taxasdividasdiversas/lancamentotaxasdiversas/edita.xhtml"),
    @URLMapping(id = "editarTaxasDividasDiversas", pattern = "/lancamento-de-taxas-diversas/editar/#{calculoTaxasDiversasControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/lancamentotaxasdiversas/edita.xhtml"),
    @URLMapping(id = "listarTaxasDividasDiversas", pattern = "/lancamento-de-taxas-diversas/listar/",
        viewId = "/faces/tributario/taxasdividasdiversas/lancamentotaxasdiversas/lista.xhtml"),
    @URLMapping(id = "verTaxasDividasDiversas", pattern = "/lancamento-de-taxas-diversas/ver/#{calculoTaxasDiversasControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/lancamentotaxasdiversas/visualizar.xhtml")
})
public class CalculoTaxasDiversasControlador extends PrettyControlador<CalculoTaxasDiversas> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CalculoTaxasDiversasControlador.class);

    @EJB
    private CalculoTaxasDiversasFacade calculoTaxasDiversasFacade;
    private ItemCalculoTaxasDiversas item;
    private List<EnderecoCorreio> enderecos;
    private ProcessoCalcTaxasDiversas processo;
    private Cadastro informacoesCadastro;
    private List<ResultadoParcela> resultadoConsulta;
    private ConverterGenerico converterTributo;
    private List<TributoTaxaDividasDiversas> tributos;

    public CalculoTaxasDiversasControlador() {
        super(CalculoTaxasDiversas.class);
        resultadoConsulta = new ArrayList<>();
    }

    public ConverterGenerico getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterGenerico(TributoTaxaDividasDiversas.class, calculoTaxasDiversasFacade.getTributoTaxasDividasDiversasFacade());
        }
        return converterTributo;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Cadastro getInformacoesCadastro() {
        return selecionado.getCadastro();
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecos = enderecos;
    }

    public List<SelectItem> getTiposCadastrosTributarioseEditar() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tpcadtrib : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tpcadtrib, tpcadtrib.getDescricao()));
        }
        return lista;
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public ComponentePesquisaGenerico getComponentePesquisaCMC() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public void selecionarObjetoPesquisaGenericoCMC(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setCadastro((Cadastro) obj);
//        this.setaCadastro((Cadastro) obj);
    }

    public List<SelectItem> completaTributosReceitas() {
        List<SelectItem> lista = Lists.newArrayList();
        if (tributos == null) {
            tributos = calculoTaxasDiversasFacade.getTributoTaxasDividasDiversasFacade().listaOrdenada();
        }
        lista.add(new SelectItem(null, ""));
        for (TributoTaxaDividasDiversas trib : tributos) {
            if (Tributo.TipoTributo.TAXA.equals(trib.getTributo().getTipoTributo())) {
                lista.add(new SelectItem(trib, trib.getTributo().getDescricao()));
            }
        }
        return lista;
    }

    public ItemCalculoTaxasDiversas getItem() {
        return item;
    }

    public void setItem(ItemCalculoTaxasDiversas item) {
        this.item = item;
    }

    public boolean habilitaBotaoSalvar() {
        return SituacaoCalculo.ABERTO.equals(selecionado.getSituacao());
    }

    public boolean habilitaBotaoImprimeDAM() {
        if (selecionado != null) {
            return SituacaoCalculo.EMITIDO.equals(selecionado.getSituacao()) && Util.getDataHoraMinutoSegundoZerado(selecionado.getVencimento()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) >= 0;
        }
        return false;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-de-taxas-diversas/";
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoTaxasDiversasFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verTaxasDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaParcelas();
    }

    @URLAction(mappingId = "novoTaxasDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        processo = new ProcessoCalcTaxasDiversas();
        selecionado.setDataHoraLancamento(getSistemaControlador().getDataOperacao());
        selecionado.setDataCalculo(selecionado.getDataHoraLancamento());
        selecionado.setSituacao(SituacaoCalculo.ABERTO);
        item = new ItemCalculoTaxasDiversas();
        enderecos = new ArrayList<EnderecoCorreio>();
        selecionado.setVencimento(calculoTaxasDiversasFacade.calcularVencimento(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selecionado.getDataHoraLancamento());
        selecionado.setExercicio(calculoTaxasDiversasFacade.getExercicioFacade().getExercicioPorAno(calendar.get(Calendar.YEAR)));
        selecionado.setNumero(null);
    }

    @URLAction(mappingId = "editarTaxasDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        processo = calculoTaxasDiversasFacade.recuperaProcesso(selecionado.getProcessoCalcTaxasDiversas());
        item = new ItemCalculoTaxasDiversas();
        if (selecionado.getContribuinte() != null) {
            enderecos = calculoTaxasDiversasFacade.getEnderecoFacade().retornaDoisPrincipais(selecionado.getContribuinte());
        }
    }

    public void setaPessoasCalculo() {
        List<Pessoa> listaPessoa = new ArrayList<>();
        List<CalculoPessoa> listaCalculoPessoa = new ArrayList<>();

        if (selecionado.getCadastro() != null) {
            listaPessoa = calculoTaxasDiversasFacade.getPessoaFacade().recuperaPessoasDoCadastro(selecionado.getCadastro());
        } else {
            listaPessoa.add(selecionado.getContribuinte());
        }
        for (Pessoa p : listaPessoa) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(selecionado);
            calculoPessoa.setPessoa(p);
            listaCalculoPessoa.add(calculoPessoa);
        }
        if (selecionado.getPessoas() != null) {
            selecionado.getPessoas().clear();
        }
        selecionado.setPessoas(listaCalculoPessoa);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado.setValorEfetivo(somaTotalUnitarioReal());
            selecionado.setValorReal(selecionado.getValorEfetivo());
            selecionado.setSimulacao(Boolean.FALSE);
            selecionado.setUsuarioLancamento(calculoTaxasDiversasFacade.getSistemaFacade().getUsuarioCorrente());
            setaPessoasCalculo();
            processo = calculoTaxasDiversasFacade.processarCalculoAndGerarDebito(selecionado);
            FacesUtil.addInfo("Salvo com sucesso!", "Número do processo: " + selecionado.getNumeroFormatado());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + processo.getCalculos().get(0).getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            selecionado.setSituacao(SituacaoCalculo.ABERTO);
            FacesUtil.addFatal("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastroTributario() != null) {
            if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())
                || TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())
                || TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
                if (selecionado.getCadastro() == null || selecionado.getCadastro().getId() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro " +
                        selecionado.getTipoCadastroTributario().getDescricao());
                }
            }
            if (selecionado.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
                if (selecionado.getContribuinte() == null || selecionado.getContribuinte().getId() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe o Contribuinte.");
                }
            }
        } else {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Cadastro.");
        }
        if (selecionado.getVencimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de vencimento.");
        } else {
            if (selecionado.getVencimento().before(new Date())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento deve ser maior que a data atual.");
            }
        }
        if (selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um tributo.");
        }
        ve.lancarException();
    }

    public void addItem() {
        if (validaCamposTributoTaxaDividasDiversas()) {
            adicionaValorDoItem();
            selecionado.getItens().add(item);
            item = new ItemCalculoTaxasDiversas();
        }
    }

    private void adicionaValorDoItem() {
        item.setCalculoTaxasDiversas(selecionado);
        try {
            item.setValorUFM(calculoTaxasDiversasFacade.getMoedaFacade().converterToUFMParaExercicio(item.getValorReal(), selecionado.getExercicio()));
        } catch (UFMException ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removeItem(ItemCalculoTaxasDiversas itemCalculo) {
        if (itemCalculo != null) {
            selecionado.getItens().remove(itemCalculo);
        }
    }

    public BigDecimal totalUnitarioReal(ItemCalculoTaxasDiversas item) {
        BigDecimal quantidade = new BigDecimal(item.getQuantidadeTributoTaxas());
        BigDecimal valor = item.getValorReal();
        BigDecimal total = valor.multiply(quantidade);
        return total.setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal somaTotalUnitarioReal() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ItemCalculoTaxasDiversas itemRec : selecionado.getItens()) {
            BigDecimal quantidade = new BigDecimal(itemRec.getQuantidadeTributoTaxas());
            BigDecimal valor = itemRec.getValorReal();
            BigDecimal total = quantidade.multiply(valor);
            retorno = retorno.add(total);
        }
        return retorno.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void imprimeDam() throws JRException, IOException {
        CalculoTaxasDiversas calculo = processo.getCalculos().get(0);
        ValorDivida valorDivida = calculoTaxasDiversasFacade.retornaValorDividaDoCalculo(processo);
        try {
            DAM dam = calculoTaxasDiversasFacade.getDAMFacade().recuperaDAMpeloCalculo(calculo);
            if (dam == null || dam.getVencimento().before(getSistemaControlador().getDataOperacao())) {
                Date vencimento = ((CalculoTaxasDiversas) calculo).getVencimento();
                dam = calculoTaxasDiversasFacade.getDAMFacade().geraDAM(valorDivida.getParcelaValorDividas().get(0), vencimento);
            }
            new ImprimeDAM().imprimirDamUnicoViaApi(dam);
        } catch (Exception ex) {
            FacesUtil.addError("Ocorreu um erro ao gerar o DAM!", ex.getMessage());
            logger.error("Erro: ", ex);
        }
    }


    private boolean validaCamposTributoTaxaDividasDiversas() {
        boolean retorno = true;
        if (item.getTributoTaxaDividasDiversas() == null || item.getTributoTaxaDividasDiversas().getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o tributo referente a esta Taxa Diversa.");
            retorno = false;
        } else if (itemJaAdicionado()) {
            FacesUtil.addError("Não foi possível continuar!", "Tributo já adicionado na lista.");
            retorno = false;
        }
        if (item.getValorReal() == null || item.getValorReal().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "Informe um valor em R$ válido.");
            retorno = false;
        }
        if (item.getQuantidadeTributoTaxas() == null || item.getQuantidadeTributoTaxas() <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "Informe uma quantidade válida.");
            retorno = false;
        }
        return retorno;
    }

    private boolean itemJaAdicionado() {
        for (ItemCalculoTaxasDiversas itemNaLista : selecionado.getItens()) {
            if (item.getTributoTaxaDividasDiversas().equals(itemNaLista.getTributoTaxaDividasDiversas())) {
                return true;
            }
        }
        return false;
    }

    public String montaDescricaoCadastro(CalculoTaxasDiversas calculoTaxasDiversas) {
        String retorno = "";
        if (calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)
            || calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)
            || calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            retorno = calculoTaxasDiversasFacade.getCadastroFacade().montaDescricaoCadastro(calculoTaxasDiversas.getTipoCadastroTributario(), calculoTaxasDiversas.getCadastro());
        } else if (calculoTaxasDiversas.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            retorno = calculoTaxasDiversas.getContribuinte().getNomeCpfCnpj();
        }
        return retorno;
    }

    public void setaCadastro(Cadastro cadastro) {
        informacoesCadastro = cadastro;
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        if (selecionado.getCadastro() != null) {
            return calculoTaxasDiversasFacade.getPessoaFacade().recuperaPessoasDoCadastro(selecionado.getCadastro());
        } else {
            return Lists.newArrayList();
        }
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (informacoesCadastro instanceof CadastroEconomico) {
            if (informacoesCadastro != null) {
                return calculoTaxasDiversasFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) informacoesCadastro);
            }
        }
        return new SituacaoCadastroEconomico();
    }

    public Testada getTestadaPrincipal() {
        if (informacoesCadastro instanceof CadastroImobiliario) {
            if (informacoesCadastro != null) {
                return calculoTaxasDiversasFacade.getLoteFacade().recuperarTestadaPrincipal(((CadastroImobiliario) informacoesCadastro).getLote());
            }
        }
        return new Testada();
    }

    public void limparCadastro() {
        selecionado.setCadastro(null);
        selecionado.setContribuinte(null);
        enderecos = null;
    }

    public void listenerPesquisaCadastroImobiliario() {
        atualizaComponentes();
    }

    private void atualizaComponentes() {
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.atualizarComponente("formCadastros");
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof CadastroEconomico) {
            if (!((CadastroEconomico) obj).getSituacaoAtual().getSituacaoCadastral().equals(SituacaoCadastralCadastroEconomico.INATIVO)) {
                selecionado.setCadastro((CadastroEconomico) obj);
            } else {
                FacesUtil.addOperacaoNaoPermitida("O Cadastro selecionado encontra-se Inativo");
            }
        }
        if (obj instanceof CadastroImobiliario) {
            if (((CadastroImobiliario) obj).getAtivo().equals(true)) {
                selecionado.setCadastro((CadastroImobiliario) obj);
            } else {
                FacesUtil.addOperacaoNaoPermitida("O Cadastro selecionado encontra-se Inativo");
            }
        }
        if (obj instanceof CadastroRural) {
            selecionado.setCadastro((CadastroRural) obj);
        }
        if (obj instanceof Pessoa) {
            selecionado.setContribuinte((Pessoa) obj);
        }
        if (obj instanceof TributoTaxaDividasDiversas) {
            item.setTributoTaxaDividasDiversas((TributoTaxaDividasDiversas) obj);
        }
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (selecionado.getTipoCadastroTributario() != null) {
            switch (selecionado.getTipoCadastroTributario()) {
                case IMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case ECONOMICO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case PESSOA:
                    PessoaPesquisaGenerico componente = (PessoaPesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
                    componente.setSomenteAtivas(true);
                    return componente;
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public void recuperaParcelas() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getId());
        resultadoConsulta = consultaParcela.executaConsulta().getResultados();
    }

    public DAM recuperaDAM(Long parcela) {
        List<DAM> retorno = calculoTaxasDiversasFacade.recuperaDAM(parcela);
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    public void atualizaValor() throws UFMException {
        if (item != null) {
            if (item.getTributoTaxaDividasDiversas() != null) {
                item.setValorUFM(item.getTributoTaxaDividasDiversas().getValor());
                item.setValorReal(calculoTaxasDiversasFacade.getMoedaFacade().converterToReal(item.getValorUFM()));
            }
        }
    }

    public String getNomeClasse() {
        if (selecionado.getTipoCadastroTributario() != null) {
            switch (selecionado.getTipoCadastroTributario()) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();

    }


    public class PesquisaTaxas {
        private Date dataLancamento;
        private Date dataVencimento;
        private String cadastro;
        private TipoCadastroTributario tipoCadastroTributario;
        private SituacaoCalculo situacaoCalculo;
        private Integer exercicio;
        private Long numero;

        public PesquisaTaxas() {
            dataLancamento = null;
            dataVencimento = null;
            exercicio = 0;
            numero = null;
            situacaoCalculo = null;
            tipoCadastroTributario = null;
            cadastro = null;
        }

        public Integer getExercicio() {
            return exercicio;
        }

        public void setExercicio(Integer exercicio) {
            this.exercicio = exercicio;
        }

        public Long getNumero() {
            return numero;
        }

        public void setNumero(Long numero) {
            this.numero = numero;
        }

        public Date getDataLancamento() {
            return dataLancamento;
        }

        public void setDataLancamento(Date dataLancamento) {
            this.dataLancamento = dataLancamento;
        }

        public Date getDataVencimento() {
            return dataVencimento;
        }

        public void setDataVencimento(Date dataVencimento) {
            this.dataVencimento = dataVencimento;
        }

        public String getCadastro() {
            return cadastro;
        }

        public void setCadastro(String cadastro) {
            this.cadastro = cadastro;
        }

        public TipoCadastroTributario getTipoCadastroTributario() {
            return tipoCadastroTributario;
        }

        public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
            this.tipoCadastroTributario = tipoCadastroTributario;
        }

        public SituacaoCalculo getSituacaoCalculo() {
            return situacaoCalculo;
        }

        public void setSituacaoCalculo(SituacaoCalculo situacaoCalculo) {
            this.situacaoCalculo = situacaoCalculo;
        }

        public void limpaCampos() {
            dataLancamento = null;
            dataVencimento = null;
            exercicio = 0;
            numero = null;
            situacaoCalculo = null;
            tipoCadastroTributario = null;
            cadastro = null;
        }
    }

}
