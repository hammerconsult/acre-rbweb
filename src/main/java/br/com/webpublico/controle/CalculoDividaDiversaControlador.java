package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SimulacaoParcelamento;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoValorTributoTaxaDividasDiversas;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CalculoDividaDiversaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "calculoDividaDiversaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCalculoDividaDiversa", pattern = "/divida-diversa/novo/", viewId = "/faces/tributario/taxasdividasdiversas/lancamentodividasdiversas/edita.xhtml"),
    @URLMapping(id = "editarCalculoDividaDiversa", pattern = "/divida-diversa/editar/#{calculoDividaDiversaControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/lancamentodividasdiversas/edita.xhtml"),
    @URLMapping(id = "listarCalculoDividaDiversa", pattern = "/divida-diversa/listar/", viewId = "/faces/tributario/taxasdividasdiversas/lancamentodividasdiversas/lista.xhtml"),
    @URLMapping(id = "verCalculoDividaDiversa", pattern = "/divida-diversa/ver/#{calculoDividaDiversaControlador.id}/", viewId = "/faces/tributario/taxasdividasdiversas/lancamentodividasdiversas/visualizar.xhtml")
})
public class CalculoDividaDiversaControlador extends PrettyControlador<CalculoDividaDiversa> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CalculoDividaDiversa.class);

    @EJB
    private CalculoDividaDiversaFacade calculoDividaDiversaFacade;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterTipoDividaDiversa;
    private ConverterGenerico converterTipoDivida;
    private ConverterAutoComplete converterCadastro;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterTributo;
    private ItemCalculoDivDiversa itemCalculoDivDiversa;
    private List<SimulacaoParcelamento> listaParcelas;
    private Cadastro informacoesCadastro;
    private PesquisaDividas pesquisaDividas;
    private List<CalculoDividaDiversa> listaCalculos;
    private DAM[] damsSelecionados;
    private List<DAM> damsDisponiveis;
    private UploadedFile file;
    private Arquivo arquivoSelecionado;
    private LogradouroBairro logradouroBairroCadastroImobiliario;
    private Pessoa pessoa;

    public CalculoDividaDiversaControlador() {
        super(CalculoDividaDiversa.class);
        pesquisaDividas = new PesquisaDividas();
        metadata = new EntidadeMetaData(CalculoDividaDiversa.class);
        itemCalculoDivDiversa = new ItemCalculoDivDiversa();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<DAM> getDamsDisponiveis() {
        return damsDisponiveis;
    }

    public void setDamsDisponiveis(List<DAM> damsDisponiveis) {
        this.damsDisponiveis = damsDisponiveis;
    }

    public DAM[] getDamsSelecionados() {
        return damsSelecionados;
    }

    public void setDamsSelecionados(DAM[] damsSelecionados) {
        this.damsSelecionados = damsSelecionados;
    }

    public ItemCalculoDivDiversa getItemCalculoDivDiversa() {
        return itemCalculoDivDiversa;
    }

    public void setItemCalculoDivDiversa(ItemCalculoDivDiversa itemCalculoDivDiversa) {
        this.itemCalculoDivDiversa = itemCalculoDivDiversa;
    }

    public PesquisaDividas getPesquisaDividas() {
        return pesquisaDividas;
    }

    public void setPesquisaDividas(PesquisaDividas pesquisaDividas) {
        this.pesquisaDividas = pesquisaDividas;
    }

    public ConverterAutoComplete getConverterTipoDividaDiversa() {
        if (converterTipoDividaDiversa == null) {
            converterTipoDividaDiversa = new ConverterAutoComplete(TipoDividaDiversa.class, calculoDividaDiversaFacade.getTipoDividaDiversaFacade());
        }
        return converterTipoDividaDiversa;
    }

    public Converter getConverterTipoDivida() {
        if (converterTipoDivida == null) {
            converterTipoDivida = new ConverterGenerico(TipoDividaDiversa.class, calculoDividaDiversaFacade.getTipoDividaDiversaFacade());
        }
        return converterTipoDivida;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, calculoDividaDiversaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Exercicio> completarExerciciosDoDebito(String filtro) {
        return calculoDividaDiversaFacade.listarFiltrandoExerciciosAtualPassados(filtro.trim());
    }

    public List<TipoDividaDiversa> completaTipoDividaDiversa(String filtro) {
        return calculoDividaDiversaFacade.getTipoDividaDiversaFacade().buscarTipoDividaDiversaAtivo(filtro.trim());
    }

    public List<CalculoDividaDiversa> getListaCalculos() {
        if (listaCalculos != null) {
            Collections.sort(listaCalculos);
        }
        return listaCalculos;
    }

    public void setListaCalculos(List<CalculoDividaDiversa> listaCalculos) {
        this.listaCalculos = listaCalculos;
    }

    public void novaPesquisa() {
        pesquisaDividas = new PesquisaDividas();
    }

    public void limpaConsulta() {
        novaPesquisa();
        listaCalculos = new ArrayList<>();
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
    }

    public String descricaoCadastro(CalculoDividaDiversa calculoDividaDiversa) {
        String retorno = "";
        if (TipoCadastroTributario.ECONOMICO.equals(calculoDividaDiversa.getTipoCadastroTributario())
            || TipoCadastroTributario.IMOBILIARIO.equals(calculoDividaDiversa.getTipoCadastroTributario())
            || TipoCadastroTributario.RURAL.equals(calculoDividaDiversa.getTipoCadastroTributario())) {
            retorno = calculoDividaDiversaFacade.getCadastroFacade().montaDescricaoCadastro(calculoDividaDiversa.getTipoCadastroTributario(), calculoDividaDiversa.getCadastro());
        } else if (TipoCadastroTributario.PESSOA.equals(calculoDividaDiversa.getTipoCadastroTributario())) {
            retorno = calculoDividaDiversa.getPessoa().getNomeCpfCnpj();
        }
        return retorno;
    }

    public void filtrar() {
        listaCalculos = calculoDividaDiversaFacade.buscarListaDeCalculoDividaDiversaParaCancelamentoPorPesquisaDivida(pesquisaDividas);

    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "TODAS"));
        for (SituacaoCalculoDividaDiversa situacao : SituacaoCalculoDividaDiversa.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposDivida() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "TODAS"));
        for (TipoDividaDiversa tipo : calculoDividaDiversaFacade.getTipoDividaDiversaFacade().lista()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposCadastroTributario() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, " "));
        for (TipoCadastroTributario tipoCadTrib : TipoCadastroTributario.values()) {
            if (isTipoCadastroPermitido(tipoCadTrib)) {
                retorno.add(new SelectItem(tipoCadTrib, tipoCadTrib.getDescricao()));
            }
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterCadastro() {
        if (converterCadastro == null) {
            converterCadastro = new ConverterAutoComplete(Cadastro.class, calculoDividaDiversaFacade.getCadastroFacade());
        }
        return converterCadastro;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, calculoDividaDiversaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(TributoTaxaDividasDiversas.class, calculoDividaDiversaFacade.getTributoTaxasDividasDiversasFacade());
        }
        return converterTributo;
    }

    public List<SelectItem> getTributos() {
        List<SelectItem> lista = Lists.newArrayList();
        lista.add(new SelectItem(null, ""));
        if (selecionado.getTipoDividaDiversa() != null) {
            for (TributoTaxaDividasDiversas tributo : calculoDividaDiversaFacade.getTipoDividaDiversaFacade().listaTributosDoTipoDividaDiversa(selecionado.getTipoDividaDiversa())) {
                lista.add(new SelectItem(tributo, tributo.getTributo().getDescricao()));
            }
        }
        return lista;
    }


    public BigDecimal recuperaValorDoTributo() {
        if (TipoValorTributoTaxaDividasDiversas.VARIAVEL.equals(itemCalculoDivDiversa.getTributoTaxaDividasDiversas().getTipoValorTributo())) {
            return new BigDecimal(BigInteger.ZERO);
        } else {
            try {
                itemCalculoDivDiversa.setValorReal(calculoDividaDiversaFacade.getMoedaFacade().converterToReal(itemCalculoDivDiversa.getTributoTaxaDividasDiversas().getValor()));
            } catch (UFMException ex) {
                logger.error("Erro: ", ex);
            }
            return itemCalculoDivDiversa.getValorReal();
        }
    }

    public void adicionaValorUFM() {
        try {
            itemCalculoDivDiversa.setValorUFM(calculoDividaDiversaFacade.getMoedaFacade().converterToUFMVigente(itemCalculoDivDiversa.getValorReal()));
        } catch (UFMException exception) {
            logger.error("Erro: ", exception);
        }
    }

    public void addItem() {
        if (validaCamposTributoTaxaDividasDiversas()) {
            itemCalculoDivDiversa.setCalculoDividaDiversa(selecionado);
            adicionaValorUFM();
            if (selecionado.getItens() == null) {
                selecionado.setItens(new ArrayList<ItemCalculoDivDiversa>());
            }
            selecionado.getItens().add(itemCalculoDivDiversa);
            itemCalculoDivDiversa = new ItemCalculoDivDiversa();
        }
    }

    public void removeItem(ActionEvent e) {
        ItemCalculoDivDiversa item = (ItemCalculoDivDiversa) e.getComponent().getAttributes().get("objeto");
        selecionado.getItens().remove(item);
    }

    public BigDecimal getTotalUFM() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoDivDiversa item : selecionado.getItens()) {
            total = total.add(item.getValorUFMTotal());
        }
        return total;
    }

    public BigDecimal getTotalReais() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoDivDiversa item : selecionado.getItens()) {
            total = total.add(item.getValorRealTotal());
        }
        return total;
    }

    public boolean itemJaAdicionado() {
        for (ItemCalculoDivDiversa itemNaLista : selecionado.getItens()) {
            if (itemCalculoDivDiversa.getTributoTaxaDividasDiversas().equals(itemNaLista.getTributoTaxaDividasDiversas())) {
                return true;
            }
        }
        return false;

    }

    private boolean validaCamposTributoTaxaDividasDiversas() {
        boolean retorno = true;
        if (itemCalculoDivDiversa.getTributoTaxaDividasDiversas() == null || itemCalculoDivDiversa.getTributoTaxaDividasDiversas().getId() == null) {
            FacesUtil.addError("Atenção!", "Informe o Tributo referente a esta Dívida Diversa.");
            retorno = false;
        } else if (itemJaAdicionado()) {
            FacesUtil.addError("Atenção!", "Tributo já adicionado na lista.");
            retorno = false;
        }
        if (itemCalculoDivDiversa.getQuantidade() == null || itemCalculoDivDiversa.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError("Atenção!", "Informe uma Quantidade válida.");
            retorno = false;
        }
        if (itemCalculoDivDiversa.getValorReal() == null || itemCalculoDivDiversa.getValorReal().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError("Atenção!", "Informe um valor em R$ válido.");
            retorno = false;
        }
        return retorno;
    }

    public boolean isTipoCadastroPermitido(TipoCadastroTributario tipoCadastroTributario) {
        return true;
    }

    public boolean desabilitaTipoDividaDiversa() {
        boolean retorno = false;
        if (selecionado.getItens() != null) {
            if (selecionado.getItens().size() > 0) {
                retorno = true;
            }
        }
        return retorno;
    }

    public void limparItemCalculo() {
        itemCalculoDivDiversa = new ItemCalculoDivDiversa();
    }

    public String montaDescricaoCadastro(CalculoDividaDiversa calculoDividaDiversa) {
        return calculoDividaDiversaFacade.montaDescricaoCadastro(calculoDividaDiversa);
    }

    public List<SimulacaoParcelamento> getListaParcelas() {
        return listaParcelas;
    }

    public void setListaParcelas(List<SimulacaoParcelamento> listaParcelas) {
        this.listaParcelas = listaParcelas;
    }

    public void efetivar() {
        selecionado.setNumeroParcelas(quantidadeMaximaParcelas());
        simularParcelamentoDividaAtiva();
    }

    public void simularParcelamentoDividaAtiva() {
        listaParcelas = new ArrayList<>();
        Date vencimento = (Date) selecionado.getDataVencimento().clone();
        if (selecionado != null) {
            if (selecionado.getNumeroParcelas() != null && selecionado.getNumeroParcelas() > 0 && selecionado.getNumeroParcelas() <= quantidadeMaximaParcelas()) {
                int quantidadeParcelas = selecionado.getNumeroParcelas();
                for (int i = 1; i <= quantidadeParcelas; i++) {
                    SimulacaoParcelamento simulacao = new SimulacaoParcelamento();
                    simulacao.setParcela("Parcela " + i);
                    simulacao.setValor(getTotalReais().divide(new BigDecimal(selecionado.getNumeroParcelas()), 2, RoundingMode.HALF_UP));
                    simulacao.setVencimento(vencimento);
                    vencimento = DataUtil.ajustarData(selecionado.getDataVencimento(), Calendar.MONTH, i, calculoDividaDiversaFacade.getFeriadoFacade(), -1);
                    listaParcelas.add(simulacao);
                }
                BigDecimal diferenca = BigDecimal.ZERO;
                if (getTotalReais().compareTo(getValorTotalSimulacao()) != 0) {
                    diferenca = getTotalReais().subtract(getValorTotalSimulacao());
                }
                listaParcelas.get(0).setValor(listaParcelas.get(0).getValor().add(diferenca));
            }
        }
    }

    private BigDecimal getValorTotalSimulacao() {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (SimulacaoParcelamento simulacao : listaParcelas) {
            toReturn = toReturn.add(simulacao.getValor());
        }
        return toReturn;
    }

    public boolean validaEfetivacao() {
        boolean retorno = true;
        if (selecionado.getNumeroParcelas() == null || selecionado.getNumeroParcelas().compareTo(0) <= 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível realizar a efetivação!", "Número de parcelas não informado ou menor que zero.");
        }
        ItemParametroTipoDividaDiv itemParametroTipoDividaDiv = calculoDividaDiversaFacade.getParametroTipoDividaDiversaFacade().buscaItemParametroTipoDividaDiversa(selecionado.getExercicio(),
            selecionado.getTipoDividaDiversa(),
            getTotalReais());
        if (itemParametroTipoDividaDiv == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível realizar a efetivação!", "Nenhuma parametrização para o Tipo de Dívida Diversa foi informada. Por favor, verifique.");
        } else if (itemParametroTipoDividaDiv.getQuantidadeMaximaParcela().compareTo(selecionado.getNumeroParcelas()) < 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível realizar a efetivação!", "A quantidade de parcelas informada "
                + selecionado.getNumeroParcelas() + " é superior ao máximo permitido "
                + itemParametroTipoDividaDiv.getQuantidadeMaximaParcela() + ".");
        }
        return retorno;
    }

    public void setaPessoasCalculo() {
        if (TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastroTributario())) {
            return;
        }
        List<Pessoa> listaPessoa = new ArrayList<>();
        List<CalculoPessoa> listaCalculoPessoa = new ArrayList<>();
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        if (selecionado.getCadastro() != null) {
            listaPessoa = calculoDividaDiversaFacade.getPessoaFacade().recuperaPessoasDoCadastro(selecionado.getCadastro());
        }
        for (Pessoa p : listaPessoa) {
            calculoPessoa.setCalculo(selecionado);
            calculoPessoa.setPessoa(p);
            listaCalculoPessoa.add(calculoPessoa);
        }
        if (selecionado.getPessoas() != null) {
            selecionado.getPessoas().clear();
        }
        selecionado.setPessoas(listaCalculoPessoa);
    }

    public void efetivarCalculo() {
        if (SituacaoCalculoDividaDiversa.EM_ABERTO.equals(selecionado.getSituacao())) {
            if (validaCampos() && validaEfetivacao()) {
                if (selecionado.getProcessoCalcDivDiversas() != null || selecionado.getProcessoCalcDivDiversas().getId() != null) {
                    try {
                        setaPessoasCalculo();
                        selecionado = calculoDividaDiversaFacade.efetiva(selecionado);
                        selecionado = calculoDividaDiversaFacade.recuperar(selecionado.getId());
                        selecionado.setProcessoCalcDivDiversas(calculoDividaDiversaFacade.recuperaProcesso(selecionado.getProcessoCalcDivDiversas()));
                        gerarDebitosEDAM(selecionado);
                        FacesUtil.addInfo("Cálculo efetivado com sucesso!", "");
                    } catch (Exception e) {
                        FacesUtil.addError("Ocorreu um erro", e.getMessage());
                    }
                } else {
                    FacesUtil.addError("Não foi possível continuar!", "Não foi encontrado nenhum processo para este cálculo. Por favor, tente efetivá-lo novamente.");
                }
            }
        }
    }

    public void gerarDebitosEDAM(CalculoDividaDiversa calculo) throws Exception {
        calculoDividaDiversaFacade.getGeraDebito().geraDebito(calculo.getProcessoCalculo());
        calculoDividaDiversaFacade.getGeraDebito().getDamFacade().geraDAM(calculo);
    }

    public void selecionaDams() {
        try {
            calculoDividaDiversaFacade.getGeraDebito().getDamFacade().geraDAM(selecionado);
            ValorDivida valorDivida = calculoDividaDiversaFacade.retornaValorDividaDoCalculo(selecionado);
            damsDisponiveis = calculoDividaDiversaFacade.getdAMFacade().listaDamsDoValorDivida(valorDivida);
            pessoa = selecionado.getPessoas().get(0).getPessoa();
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível imprimir o DAM!", ex.getMessage());
            logger.error(ex.getMessage());

        }
    }

    public void imprimeDemonstrativo() {
        // imprimir demonstrativo de parcelas
        try {
            ImprimeRelatorio relatorio = new ImprimeRelatorio("DemonstrativoLancDividasDiversas.jasper", new JRBeanCollectionDataSource(listaParcelas));
            relatorio.emite();
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível imprimir o Demonstrativo!", ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    public void imprimeDam() {
        try {
            if (damsSelecionados != null && damsSelecionados.length > 0) {
                new ImprimeDAM().imprimirDamUnicoViaApi(Arrays.asList(damsSelecionados), pessoa.getId());
            }
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível imprimir o DAM!", ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    public boolean podeEditarValorTributo() {
        boolean retorno = false;
        if (itemCalculoDivDiversa.getTributoTaxaDividasDiversas() != null) {
            if (TipoValorTributoTaxaDividasDiversas.VARIAVEL.equals(itemCalculoDivDiversa.getTributoTaxaDividasDiversas().getTipoValorTributo())) {
                retorno = true;
            }
        }
        return retorno;
    }

    public boolean podeEditar() {
        boolean retorno = false;
        if (SituacaoCalculoDividaDiversa.NOVO.equals(selecionado.getSituacao())
            || SituacaoCalculoDividaDiversa.EM_ABERTO.equals(selecionado.getSituacao())) {
            retorno = true;
        }
        return retorno;
    }

    public boolean podeEfetivar() {
        boolean retorno = false;
        if (SituacaoCalculoDividaDiversa.EM_ABERTO.equals(selecionado.getSituacao())) {
            retorno = true;
        }
        return retorno;
    }

    public boolean podeImprimirDAM() {
        boolean retorno = false;
        if (SituacaoCalculoDividaDiversa.EFETIVADO.equals(selecionado.getSituacao())) {
            retorno = true;
        }
        return retorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return this.calculoDividaDiversaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-diversa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoCalculoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarAtributosDoSelecionado();
        arquivoSelecionado = new Arquivo();
        itemCalculoDivDiversa = new ItemCalculoDivDiversa();
        selecionado.setSituacao(SituacaoCalculoDividaDiversa.NOVO);
    }

    @Override
    @URLAction(mappingId = "editarCalculoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        arquivoSelecionado = new Arquivo();
    }

    @Override
    @URLAction(mappingId = "verCalculoDividaDiversa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())) {
            logradouroBairroCadastroImobiliario = calculoDividaDiversaFacade.getCadastroImobiliarioFacade().recuperaLogradouroBairroCadastro((CadastroImobiliario) selecionado.getCadastro());
        }
    }

    public Boolean validaCampos() {
        Boolean retorno = true;
        if (selecionado.getTipoDividaDiversa() == null || selecionado.getTipoDividaDiversa().getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o Tipo de Dívidas Diversas referente ao lançamento.");
            retorno = false;
        }
        if (selecionado.getTipoCadastroTributario() != null) {
            if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())
                || TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())
                || TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
                if (selecionado.getCadastro() == null || selecionado.getCadastro().getId() == null) {
                    FacesUtil.addError("Não foi possível continuar!", "Informe o cadastro " + selecionado.getTipoCadastroTributario().getDescricao() + " referente ao lançamento.");
                    retorno = false;
                }
            }
            if (TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastroTributario())) {
                if (selecionado.getPessoa() == null || selecionado.getPessoa().getId() == null) {
                    FacesUtil.addError("Não foi possível continuar!", "Informe o Contribuinte.");
                    retorno = false;
                }
            }
        } else {
            FacesUtil.addError("Não foi possível continuar!", "Informe o Tipo de Cadastro referente ao lançamento.");
            retorno = false;
        }

        if (selecionado.getDataLancamento() == null) {
            FacesUtil.addError("Não foi possível continuar!", "A Data de Lançamento deve ser informada.");
            retorno = false;
        } else if (selecionado.getDataVencimento() == null || Util.getDataHoraMinutoSegundoZerado(selecionado.getDataVencimento()).compareTo(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataLancamento())) < 0) {
            FacesUtil.addError("Não foi possível continuar!", "A Data de Vencimento deve ser informada e não pode ser inferior a data de lançamento.");
            retorno = false;
        }
        if (selecionado.getNumeroProcessoProtocolo() != null && selecionado.getNumeroProcessoProtocolo().compareTo(0) < 0) {
            FacesUtil.addError("Não foi possível continuar!", "O Número do Processo de Protocolo não pode ser menor que zero.");
            retorno = false;
        }
        if (selecionado.getAnoProcessoProtocolo() != null && selecionado.getAnoProcessoProtocolo().compareTo(0) < 0) {
            FacesUtil.addError("Não foi possível continuar!", "O ano do processo de protocolo não pode ser menor que zero.");
            retorno = false;
        }
        if (selecionado.getItens() == null || selecionado.getItens().size() <= 0) {
            FacesUtil.addError("Não foi possível continuar!", "Nenhum Tributo foi informado para o lançamento.");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public void salvar() {
        definirPrimeiroVencimento();
        adicionarPessoa(Boolean.FALSE);
        if (validaCampos() == true) {
            try {
                selecionado.setDataCalculo(calculoDividaDiversaFacade.getSistemaFacade().getDataOperacao());
                selecionado.setSimulacao(Boolean.FALSE);
                selecionado.setValorEfetivo(getTotalReais());
                selecionado.setValorReal(selecionado.getValorEfetivo());
                setaPessoasCalculo();
                ProcessoCalcDivDiversas processo = calculoDividaDiversaFacade.processaCalculo(selecionado);
                selecionado = processo.getCalculos().get(0);
                FacesUtil.addInfo("Salvo com sucesso!", "");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addError("Atenção!", ex.getMessage());
            } catch (Exception e) {
                FacesUtil.addFatal("Exceção do sistema! ", e.getMessage());
                logger.error(e.getMessage());
            }
        }
    }

    public void definirPrimeiroVencimento() {
        if (selecionado.getDataLancamento() != null) {
            selecionado.setDataVencimento(DataUtil.ultimoDiaMes(selecionado.getDataLancamento()).getTime());
        }
    }

    public void setaCadastro(Cadastro cadastro) {
        informacoesCadastro = cadastro;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (informacoesCadastro instanceof CadastroEconomico) {
            if (informacoesCadastro != null) {
                return calculoDividaDiversaFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) informacoesCadastro);
            }
        }
        return new SituacaoCadastroEconomico();
    }

    public Testada getTestadaPrincipal() {
        if (informacoesCadastro instanceof CadastroImobiliario) {
            if (informacoesCadastro != null) {
                return calculoDividaDiversaFacade.getLoteFacade().recuperarTestadaPrincipal(((CadastroImobiliario) informacoesCadastro).getLote());
            }
        }
        return new Testada();
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        return calculoDividaDiversaFacade.getPessoaFacade().recuperaPessoasDoCadastro(informacoesCadastro);
    }

    public List<Pessoa> recuperaPessoasDoCadastro() {
        if (selecionado.getCadastro() != null) {
            return calculoDividaDiversaFacade.getPessoaFacade().recuperaPessoasDoCadastro(selecionado.getCadastro());
        } else {
            return Lists.newArrayList();
        }
    }

    public Cadastro getInformacoesCadastro() {
        return informacoesCadastro;
    }

    public void setInformacoesCadastro(Cadastro informacoesCadastro) {
        this.informacoesCadastro = informacoesCadastro;
    }

    public void zeraCadastro() {
        selecionado.setCadastro(null);
        selecionado.setPessoa(null);
    }

    public int quantidadeMaximaParcelas() {
        ItemParametroTipoDividaDiv itemParametroTipoDividaDiv = calculoDividaDiversaFacade.getParametroTipoDividaDiversaFacade().buscaItemParametroTipoDividaDiversa(selecionado.getExercicio(), selecionado.getTipoDividaDiversa(), getTotalReais());
        if (itemParametroTipoDividaDiv != null) {
            return itemParametroTipoDividaDiv.getQuantidadeMaximaParcela();
        }
        return 0;
    }

    public String descricaoQuantidadeMaximaParcelas() {
        int qtdeMaxParc = quantidadeMaximaParcelas();
        if (qtdeMaxParc > 0) {
            return qtdeMaxParc + "";
        }
        return "Parâmetro não encontrado.";
    }

    private void inicializarAtributosDoSelecionado() {
        selecionado.setTipoCalculo(Calculo.TipoCalculo.DIVIDA_DIVERSA);
        selecionado.setDataLancamento(calculoDividaDiversaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setDataVencimento(DataUtil.ultimoDiaMes(calculoDividaDiversaFacade.getSistemaFacade().getDataOperacao()).getTime());
        selecionado.setUsuarioLancamento(calculoDividaDiversaFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setItens(new ArrayList<ItemCalculoDivDiversa>());
        selecionado.setSituacao(SituacaoCalculoDividaDiversa.NOVO);
        selecionado.setExercicio(calculoDividaDiversaFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setNumeroParcelas(1);
    }

    public List<SelectItem> tiposCadastrosTributarios() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tpcadtrib : TipoCadastroTributario.values()) {
            if (isTipoCadastroPermitido(tpcadtrib)) {
                lista.add(new SelectItem(tpcadtrib, tpcadtrib.getDescricao()));
            }
        }
        return lista;
    }

    public List<SelectItem> tiposSituacaoCalculo() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, "TODAS"));
        for (SituacaoCalculoDividaDiversa situacao : SituacaoCalculoDividaDiversa.values()) {
            lista.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return lista;
    }

    public void listenerAtualiza() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public List<ArquivoCalcDividaDiversa> getArquivos() {
        List<ArquivoCalcDividaDiversa> lista = new ArrayList<>();

        for (ArquivoCalcDividaDiversa a : selecionado.getArquivoCalcDividaDiversas()) {
            if (!a.getExcluido()) {
                lista.add(a);
            }
        }
        return lista;
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        arquivoSelecionado.setDescricao(file.getFileName());
        adicionarArquivo();
    }

    public void adicionarArquivo() {
        ArquivoCalcDividaDiversa arquivo = new ArquivoCalcDividaDiversa();
        arquivo.setArquivo(arquivoSelecionado);
        arquivo.setCalculoDividaDiversa(selecionado);
        arquivo.setFile((Object) file);
        arquivoSelecionado.setMimeType(file.getContentType());
        arquivoSelecionado.setNome(file.getFileName());
        selecionado.getArquivoCalcDividaDiversas().add(arquivo);
        arquivoSelecionado = new Arquivo();
    }

    public void removeArquivo(ArquivoCalcDividaDiversa arquivo) {
        if (arquivo.getId() != null) {
            arquivo.setExcluido(true);
        } else {
            selecionado.getArquivoCalcDividaDiversas().remove(arquivo);
        }
    }

    public Arquivo getArquivoSelecionado() {
        return arquivoSelecionado;
    }

    public void setArquivoSelecionado(Arquivo arquivoSelecionado) {
        this.arquivoSelecionado = arquivoSelecionado;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getArquivoStream(ArquivoCalcDividaDiversa arquivo) throws IOException {
        UploadedFile download = (UploadedFile) arquivo.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public boolean desabilitaImpressaoDAM() {
        return !SituacaoCalculoDividaDiversa.EFETIVADO.equals(selecionado.getSituacao());
    }

    public boolean desabilitaImpressaoDemonstrativo() {
        if (listaParcelas != null) {
            return listaParcelas.size() <= 0;
        }
        return true;
    }

    public List<ResultadoParcela> getRecuperaParcelas() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getId());
        return consultaParcela.executaConsulta().getResultados();
    }

    public DAM recuperaDAM(Long parcela) {
        List<DAM> retorno = calculoDividaDiversaFacade.recuperaDAM(parcela);
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    public String getTipoLogradouroCadastroImobiliario() {
        if (logradouroBairroCadastroImobiliario != null) {
            return logradouroBairroCadastroImobiliario.getLogradouro().getTipoLogradouro().getDescricao();
        }
        return "";
    }

    public String getLogradouroCadastroImobiliario() {
        if (logradouroBairroCadastroImobiliario != null) {
            return logradouroBairroCadastroImobiliario.getLogradouro().getNome();
        }
        return "";
    }

    public String getBairroCadastroImobiliario() {
        if (logradouroBairroCadastroImobiliario != null) {
            return logradouroBairroCadastroImobiliario.getBairro().getDescricao();
        }
        return "";
    }

    public String getCepCadastroImobiliario() {
        if (logradouroBairroCadastroImobiliario != null) {
            return logradouroBairroCadastroImobiliario.getCep();
        }
        return "";
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
            selecionado.setPessoa((Pessoa) obj);
        }
        if (obj instanceof TributoTaxaDividasDiversas) {
            itemCalculoDivDiversa.setTributoTaxaDividasDiversas((TributoTaxaDividasDiversas) obj);
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

    public List<CalculoDividaDiversa> buscarCalculoDividaDiversaPorNumero(String numero) {
        return calculoDividaDiversaFacade.buscarCalculoDividaDiversaPorNumero(numero);
    }

    public class ImprimeRelatorio extends AbstractReport {
        private String arquivoJasper;
        private JRBeanCollectionDataSource ds;

        public ImprimeRelatorio(String arquivoJasper, JRBeanCollectionDataSource ds) {
            this.arquivoJasper = arquivoJasper;
            this.ds = ds;
        }

        public void emite() throws JRException, IOException {
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", calculoDividaDiversaFacade.getSistemaFacade().getLogin());
            parameters.put("NUMERO", selecionado.getNumeroFormatado() + " / " + selecionado.getExercicio().getAno());
            parameters.put("DATALANCAMENTO", new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getDataLancamento()).toString());
            parameters.put("TIPODIVIDA", selecionado.getTipoDividaDiversa().getDescricao());
            parameters.put("TIPOCADASTRO", selecionado.getTipoCadastroTributario().getDescricaoLonga());
            String descricaoCadastro = "";
            if (TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())) {
                CadastroEconomico ce = ((CadastroEconomico) selecionado.getCadastro());
                descricaoCadastro = ce.getNumeroCadastro() + " " + ce.getPessoa().getNome() + " " + ce.getPessoa().getCpf_Cnpj();
            } else if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())) {
                CadastroImobiliario ci = ((CadastroImobiliario) selecionado.getCadastro());
                descricaoCadastro = ci.getInscricaoCadastral();
            } else if (TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
                CadastroRural cr = ((CadastroRural) selecionado.getCadastro());
                descricaoCadastro = cr.getNumeroCadastro() + " " + cr.getNomePropriedade() + " " + cr.getNumeroIncra();
            } else if (TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastroTributario())) {
                descricaoCadastro = selecionado.getPessoa().getNomeCpfCnpj();
            }
            parameters.put("DESCRICAOCADASTRO", descricaoCadastro);
            gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        }
    }

    public boolean hasPermissaoParaLancarDividaDiversaExercicioAnterior() {
        return calculoDividaDiversaFacade.hasPermissaoParaLancarDividaDiversaExercicioAnterior();
    }

    public void validarPessoa() {
        if (pessoa == null) {
            throw new ValidacaoException("O campo Cadastro deve ser informado.");
        }
    }

    public void adicionarPessoa() {
        adicionarPessoa(Boolean.TRUE);
    }

    public void adicionarPessoa(Boolean emiteMensagem) {
        try {
            if (!TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastroTributario())) {
                return;
            }
            validarPessoa();
            if (selecionado.getPessoas().stream().anyMatch(cp -> cp.getPessoa().equals(pessoa))) {
                pessoa = null;
                return;
            }
            if (selecionado.getPessoas() == null) {
                selecionado.setPessoas(Lists.newArrayList());
            }
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(selecionado);
            calculoPessoa.setPessoa(pessoa);
            selecionado.getPessoas().add(calculoPessoa);
            if (selecionado.getPessoa() == null) {
                selecionado.setPessoa(pessoa);
            }
            pessoa = null;
        } catch (ValidacaoException ve) {
            pessoa = null;
            if (emiteMensagem) {
                FacesUtil.printAllFacesMessages(ve);
            }
        } catch (Exception e) {
            pessoa = null;
            if (emiteMensagem) {
                FacesUtil.addErrorPadrao(e);
            }
        }
    }

    public void removerPessoa(CalculoPessoa calculoPessoa) {
        selecionado.getPessoas().remove(calculoPessoa);
    }
}
