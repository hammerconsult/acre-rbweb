/*
 * Codigo gerado automaticamente em Tue Dec 20 17:59:34 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LancamentoReceitaOrcFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "lancamentoReceitaOrcControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-receita-realizada", pattern = "/receita-realizada/novo/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorc/edita.xhtml"),
    @URLMapping(id = "editar-receita-realizada", pattern = "/receita-realizada/editar/#{lancamentoReceitaOrcControlador.id}/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorc/edita.xhtml"),
    @URLMapping(id = "ver-receita-realizada", pattern = "/receita-realizada/ver/#{lancamentoReceitaOrcControlador.id}/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorc/visualizar.xhtml"),
    @URLMapping(id = "listar-receita-realizada", pattern = "/receita-realizada/listar/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorc/lista.xhtml")
})
public class LancamentoReceitaOrcControlador extends PrettyControlador<LancamentoReceitaOrc> implements Serializable, CRUD, IConciliar {

    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterReceitaLOA;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterContaReceita;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterContaBancaria;
    private List<FonteDeRecursos> fontesDaReceitaLoa;
    private ReceitaLOA rl;
    private ReceitaLOAFonte receitaLOAFonte;
    private ContaReceita contaReceita;
    private MoneyConverter moneyConverter;
    private SubConta subConta;
    private List<ContaDeDestinacao> contasDeDestinacao;
    private ClasseCredor classeCredor;
    private TiposCredito tiposCredito;
    private Date dataInicial;
    private Date dataFinal;
    private List<LancamentoReceitaOrc> lancamentoIntegracao;
    private ContaBancariaEntidade contaBancariaEntidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean mostraBotaoQueNaoDeveSeMostrado;
    private Long conjuntoFontes;
    private List<LancamentoReceitaOrc> bloqueadasPorFase;
    private Date novaDataContabilizacao;


    public LancamentoReceitaOrcControlador() {
        super(LancamentoReceitaOrc.class);
        dataInicial = new Date();
    }

    public void setaClasseCredor(SelectEvent evt) {
        if (classeCredor != null && classeCredor.getId() != null) {
            ((LancamentoReceitaOrc) selecionado).setClasseCredor(this.classeCredor);
        }
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, lancamentoReceitaOrcFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(Conta.class, lancamentoReceitaOrcFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, lancamentoReceitaOrcFacade.getBancariaEntidadeFacade());
        }
        return converterContaBancaria;
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, lancamentoReceitaOrcFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public Date getNovaDataContabilizacao() {
        return novaDataContabilizacao;
    }

    public void setNovaDataContabilizacao(Date novaDataContabilizacao) {
        this.novaDataContabilizacao = novaDataContabilizacao;
    }

    public List<ContaBancariaEntidade> completaContaBancaria(String parte) {
        return lancamentoReceitaOrcFacade.getBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente(), null, null, null);
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        try {
            List<SubConta> subContas = new ArrayList<>();
            if (contaBancariaEntidade != null) {
                subContas = lancamentoReceitaOrcFacade.getSubContaFacade().listaPorContaBancariaUnidadeFonteRecurso(parte.trim(), contaBancariaEntidade, selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente(), idFontesAsString());
            } else {
                subContas = lancamentoReceitaOrcFacade.getSubContaFacade().listaPorContaBancariaUnidadeFonteRecurso(parte.trim(), null, selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente(), idFontesAsString());
                if (subContas.isEmpty()) {
                    FacesUtil.addAtencao("Conta Financeira não encontrada para as fontes de recursos da receita loa.");
                }
            }
            return subContas;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar a conta financeira " + ex.getMessage());
        }
    }

    private String idFontesAsString() {
        StringBuilder stb = new StringBuilder();
        String listaIdsFonte = "";
        if (this.fontesDaReceitaLoa.size() > 0) {
            for (FonteDeRecursos lista : fontesDaReceitaLoa) {
                listaIdsFonte += lista.getId() + ",";
            }
            stb.append(" AND ").append(" SCF.FONTEDERECURSOS_ID IN (").append(listaIdsFonte.substring(0, listaIdsFonte.length() - 1)).append(") ");
        }
        return stb.toString();
    }

    private List<ContaDeDestinacao> getContasDeDestinacaoDaSubConta() {
        if (selecionado.getSubConta() != null) {
            return lancamentoReceitaOrcFacade.buscarContasDeDestinacao(selecionado);
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return lancamentoReceitaOrcFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA");
    }

    public List<Conta> completaContaReceita(String parte) {
        return lancamentoReceitaOrcFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void removeLancReceitasFontes(LancReceitaFonte lanc) {
        ((LancamentoReceitaOrc) selecionado).getLancReceitaFonte().remove(lanc);
        selecionado.setValor(selecionado.getValor().subtract(lanc.getValor()));
    }

    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public void recalcular() {
        LancamentoReceitaOrc lancamento = ((LancamentoReceitaOrc) selecionado);
        for (LancReceitaFonte lancReceita : lancamento.getLancReceitaFonte()) {
            if (lancReceita.getReceitaLoaFonte().getPercentual().equals(BigDecimal.ZERO)) {
                FacesUtil.addAtencao(" O Percentual previsto no orçamento para a fonte " + lancReceita.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos() + " está igual a 0 (ZERO).");
            }
            lancReceita.setValor((lancamento.getValor().multiply(lancReceita.getReceitaLoaFonte().getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
        }
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void atualizaTabela(LancReceitaFonte lanc) {
        RequestContext context = RequestContext.getCurrentInstance();
        lancamentoReceitaOrcFacade.somaLancamentos((LancamentoReceitaOrc) selecionado);
        lancamentoReceitaOrcFacade.calculaDiferenca((LancamentoReceitaOrc) selecionado);
        context.update("Formulario:" + ((LancamentoReceitaOrc) selecionado).getLancReceitaFonte().indexOf(lanc) + ":tabelaLancReceitaFontes");
    }

    public BigDecimal somaLancamentos() {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        dif = lancamentoReceitaOrcFacade.somaLancamentos(((LancamentoReceitaOrc) selecionado));
        return dif;
    }

    public BigDecimal calculaDiferenca() {
        BigDecimal dif = BigDecimal.ZERO;
        if (!selecionado.getLancReceitaFonte().isEmpty()) {
            dif = lancamentoReceitaOrcFacade.calculaDiferenca((LancamentoReceitaOrc) selecionado);
        }
        return dif;
    }

    public void geraLancamentos() {
        try {
            lancamentoReceitaOrcFacade.geraLancamentos((LancamentoReceitaOrc) selecionado, conjuntoFontes);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
        }
    }

    public LancamentoReceitaOrcFacade getFacade() {
        return lancamentoReceitaOrcFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoReceitaOrcFacade;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return lancamentoReceitaOrcFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), ((LancamentoReceitaOrc) selecionado).getPessoa());
    }

    public List<CodigoCO> completarCodigosCOs(String parte) {
        if (selecionado.getLancReceitaFonte() != null && !selecionado.getLancReceitaFonte().isEmpty()) {
            return lancamentoReceitaOrcFacade.getCodigoCOFacade().buscarCodigosCOsPorFonteDeRecursosFiltrandoPorCodigoEDescricao(parte, selecionado.getLancReceitaFonte().get(0).getReceitaLoaFonte().getFonteRecurso());
        }
        return lancamentoReceitaOrcFacade.getCodigoCOFacade().buscarCodigosCOsPorFonteDeRecursosFiltrandoPorCodigoEDescricao(parte, selecionado.getFonteDeRecursos());
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, lancamentoReceitaOrcFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public void setaPessoa(SelectEvent evt) {
        LancamentoReceitaOrc cr = ((LancamentoReceitaOrc) selecionado);
        Pessoa p = (Pessoa) evt.getObject();
        cr.setPessoa(p);
        cr.setClasseCredor(null);
    }

    public Boolean getMostraBotaoQueNaoDeveSeMostrado() {
        return mostraBotaoQueNaoDeveSeMostrado != null ? mostraBotaoQueNaoDeveSeMostrado : false;
    }

    public void setMostraBotaoQueNaoDeveSeMostrado(Boolean mostraBotaoQueNaoDeveSeMostrado) {
        this.mostraBotaoQueNaoDeveSeMostrado = mostraBotaoQueNaoDeveSeMostrado;
    }

    @URLAction(mappingId = "ver-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "novo-receita-realizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        LancamentoReceitaOrc lanc = (LancamentoReceitaOrc) selecionado;
        subConta = null;
        dataFinal = null;
        tiposCredito = null;
        classeCredor = new ClasseCredor();
        contaReceita = new ContaReceita();
        receitaLOAFonte = new ReceitaLOAFonte();
        receitaLOAFonte.setItem(1l);
        conjuntoFontes = 1l;
        fontesDaReceitaLoa = new ArrayList<>();
        contasDeDestinacao = Lists.newArrayList();
        selecionado.setReceitaDeIntegracao(Boolean.FALSE);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        lanc.setDataLancamento(sistemaControlador.getDataOperacao());
        lanc.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        lanc.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        lanc.setExercicio(sistemaControlador.getExercicioCorrente());
        if (lancamentoReceitaOrcFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), lancamentoReceitaOrcFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void definirEventoReceitaRealizada() {
        try {
            if (selecionado.getOperacaoReceitaRealizada() != null
                && selecionado.getReceitaLOA() != null) {
                lancamentoReceitaOrcFacade.definirEventoReceitaRealizada(selecionado);
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public Boolean desabilitarCampoValor() {
        if (!selecionado.getLancReceitaFonte().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void salvar() {
        try {
            validarLancamentoReceita(selecionado);
            if (operacao.equals(Operacoes.NOVO)) {
                lancamentoReceitaOrcFacade.salvarNovaReceita(selecionado, conjuntoFontes);
            } else {
                lancamentoReceitaOrcFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("A receita nº " + selecionado + " foi salva com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            lancamentoReceitaOrcFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<SelectItem> buscarContasDeDestinacao() {
        if (selecionado.getDividaPublica() != null) {
            return Util.getListSelectItem(lancamentoReceitaOrcFacade.getDividaPublicaFacade().buscarContasDeDestinacaoPorDividaPublica(selecionado.getDividaPublica(), TipoValidacao.MOVIMENTO_DIVIDA_PUBLICA, sistemaControlador.getExercicioCorrente(), null));
        }
        return Lists.newArrayList();
    }

    public void atualizarFonteDeRecursos() {
        if (selecionado.getContaDeDestinacao() != null) {
            selecionado.setFonteDeRecursos(selecionado.getContaDeDestinacao().getFonteDeRecursos());
        }
    }

    public Boolean validaCampos(LancamentoReceitaOrc selecionado) {
        if (selecionado.getLancReceitaFonte().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Não foi feito lançamentos de receitas fontes para essa receita realizada.");
            return false;
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Valor deve ser maior que zero(0).");
            return false;
        }
        if (selecionado.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataLancamento()))) {
                FacesUtil.addOperacaoNaoPermitida("A Data de Conciliação deve ser igual ou superior a Data de Lançamento da Receita.");
                return false;
            }
        }
        if (!selecionado.getReceitaDeIntegracao()
            && selecionado.getDataReferencia() != null) {
            if (selecionado.getDataReferencia().compareTo(selecionado.getDataLancamento()) > 0) {
                FacesUtil.addOperacaoNaoPermitida("A Data de Referência deve ser menor ou igual a Data de Lançamento.");
                return false;
            }
        }
        if (selecionado.getEventoContabil() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Evento contábil não encontrado para esta receita realizada.");
            return false;
        }
        return true;
    }

    public void recuperarEditarVer() {
        operacao = Operacoes.EDITAR;
        selecionado = lancamentoReceitaOrcFacade.recuperar(selecionado.getId());
        subConta = selecionado.getSubConta();
        receitaLOAFonte = new ReceitaLOAFonte();
        setarContaBancaria();

        if (selecionado.getReceitaLOA() != null) {
            tiposCredito = ((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita()).getTiposCredito();
        } else {
            tiposCredito = null;
        }
        classeCredor = selecionado.getClasseCredor();
    }

    public List<Pessoa> completaPessoa(String parte) {
        return lancamentoReceitaOrcFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, lancamentoReceitaOrcFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<SelectItem> getListaOperacoesReceitaRealizada() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoReceita op : OperacaoReceita.values()) {
            toReturn.add(new SelectItem(op, op.getDescricao()));
        }
        return toReturn;
    }

    public void armazenarConjuntoFonte() {
        if (selecionado.getReceitaLOA() != null) {
            if (receitaLOAFonte.getItem() != null) {
                conjuntoFontes = receitaLOAFonte.getItem();
                fontesDaReceitaLoa = lancamentoReceitaOrcFacade.recuperarFontesDaReceitaLoa(selecionado, conjuntoFontes);
                subConta = null;
                contaBancariaEntidade = null;
                selecionado.getLancReceitaFonte().clear();
            } else {
                throw new ExcecaoNegocioGenerica("O campo Conjunto de Fonte deve ser informado.");
            }
        }
    }

    public List<SelectItem> getConjuntoDeFontes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        if (selecionado.getReceitaLOA() != null) {
            for (BigDecimal item : lancamentoReceitaOrcFacade.getReceitaLOAFacade().recuperarConjuntoDeFontePorReceitaLoa(selecionado.getReceitaLOA(), sistemaControlador.getExercicioCorrente())) {
                toReturn.add(new SelectItem(item.longValue(), item.toString()));
            }
        }
        return toReturn;
    }

    public List getFonteRecursoPorConjuntoFontes() {
        try {
            if (selecionado.getReceitaLOA() != null) {
                return lancamentoReceitaOrcFacade.getReceitaLOAFacade().recuperarFonteDeRecursoPorConjuntoDeFonte(selecionado.getReceitaLOA(), receitaLOAFonte);
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao recuperar Fonte de Recurso " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void setaReceitaLOA(SelectEvent event) {
        ReceitaLOA receita = (ReceitaLOA) event.getObject();
        selecionado.setLancReceitaFonte(Lists.newArrayList());
        if (receita != null) {
            subConta = null;
            ((LancamentoReceitaOrc) selecionado).setReceitaLOA(lancamentoReceitaOrcFacade.getReceitaLOAFacade().recuperar(receita.getId()));
            tiposCredito = ((ContaReceita) receita.getContaDeReceita()).getTiposCredito();
        }

        if (selecionado.getReceitaLOA().getConjuntos().size() == 1) {
            receitaLOAFonte.setItem(1l);
            selecionado.getReceitaLOA().getConjuntos().get(0);
            conjuntoFontes = selecionado.getReceitaLOA().getConjuntos().get(0);
            fontesDaReceitaLoa = lancamentoReceitaOrcFacade.recuperarFontesDaReceitaLoa(selecionado, conjuntoFontes);
            FacesUtil.executaJavaScript("painelInformacoes.show()");
        } else {
            selecionado.getReceitaLOA().getConjuntos();
            FacesUtil.executaJavaScript("dialogConjFontes.show()");
        }
        definirEventoReceitaRealizada();
    }

    public void recuperarReceitaLOA() {
        ReceitaLOA receitaLOA = lancamentoReceitaOrcFacade.getReceitaLOAFacade().recuperar(selecionado.getReceitaLOA().getId());
        selecionado.setReceitaLOA(receitaLOA);
        selecionado.getReceitaLOA().getConjuntos().get(0);
    }

    public Boolean mostraPainelConvenio() {
        if (((LancamentoReceitaOrc) selecionado).getSubConta() != null && ((LancamentoReceitaOrc) selecionado).getSubConta().getTipoRecursoSubConta() != null) {
            if (((LancamentoReceitaOrc) selecionado).getSubConta().getTipoRecursoSubConta().equals(TipoRecursoSubConta.CONVENIO_CONGENERE)
                && ((LancamentoReceitaOrc) selecionado).getConvenioReceita() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean mostrarPainelDivida() {
        return selecionado.getSubConta() != null
            && TipoRecursoSubConta.OPERACAO_CREDITO.equals(selecionado.getSubConta().getTipoRecursoSubConta())
            && selecionado.getDividaPublica() != null;
    }

    private void atribuirConvenioEDivida() {
        try {
            if (selecionado.getSubConta() != null) {
                if (TipoRecursoSubConta.CONVENIO_CONGENERE.equals(subConta.getTipoRecursoSubConta())) {
                    ConvenioReceita convenioReceita = lancamentoReceitaOrcFacade.getConvenioReceitaFacade().recuperaConvenioPorSubConta(subConta);
                    selecionado.setConvenioReceita(convenioReceita);
                } else if (TipoRecursoSubConta.OPERACAO_CREDITO.equals(subConta.getTipoRecursoSubConta())) {
                    selecionado.setDividaPublica(lancamentoReceitaOrcFacade.getDividaPublicaFacade().buscarDividaPublicaPorSubContaAndUnidade(subConta, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente()));
                    selecionado.setFonteDeRecursos(null);
                } else {
                    selecionado.setConvenioReceita(null);
                    selecionado.setDividaPublica(null);
                    selecionado.setFonteDeRecursos(null);
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addAtencao(ex.getMessage());
        }
    }

    public BigDecimal recuperarSaldoDividaPublicaLongoPrazo() {
        return recuperarSaldoDividaPublica(Intervalo.LONGO_PRAZO);
    }

    public BigDecimal recuperarSaldoDividaPublicaCurtoPrazo() {
        return recuperarSaldoDividaPublica(Intervalo.CURTO_PRAZO);
    }

    public BigDecimal recuperarSaldoTotalDividaPublica() {
        return recuperarSaldoDividaPublicaCurtoPrazo().add(recuperarSaldoDividaPublicaLongoPrazo());
    }

    private BigDecimal recuperarSaldoDividaPublica(Intervalo intervalo) {
        BigDecimal saldo = BigDecimal.ZERO;
        try {
            if (selecionado.getDividaPublica() != null
                && selecionado.getUnidadeOrganizacional() != null
                && selecionado.getFonteDeRecursos() != null) {
                SaldoDividaPublica saldoDivida = lancamentoReceitaOrcFacade.getSaldoDividaPublicaFacade().recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(sistemaControlador.getDataOperacao(),
                    selecionado.getUnidadeOrganizacional(),
                    selecionado.getDividaPublica(),
                    intervalo,
                    selecionado.getContaDeDestinacao());
                if (saldoDivida != null && saldoDivida.getId() != null) {
                    saldo = saldoDivida.getSaldoReal();
                }
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return saldo;
    }

    public ConverterAutoComplete getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, lancamentoReceitaOrcFacade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    public List<ReceitaLOA> completarReceitaLOA(String parte) {
        List<ReceitaLOA> lista = new ArrayList<>();
        try {
            validarOperacaoReceita();
            lista = lancamentoReceitaOrcFacade.getReceitaLOAFacade().buscarContaReceitaPorUnidadeExercicioAndOperacaoReceita(
                parte.trim(),
                sistemaControlador.getExercicioCorrente(),
                sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(),
                selecionado.getOperacaoReceitaRealizada());
            if (lista.isEmpty()) {
                FacesUtil.addAtencao("Conta de receita não encontrada para a operação: " + selecionado.getOperacaoReceitaRealizada().getDescricao());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return lista;
    }

    private void validarOperacaoReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperacaoReceitaRealizada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado para filtrar a conta de receita.");
        }
        ve.lancarException();
    }

    public void definirEventoAndFiltroContaReceita() {
        limparCampoContaReceita();
        definirEventoReceitaRealizada();
    }

    private void limparCampoContaReceita() {
        selecionado.setReceitaLOA(null);
    }

    public ReceitaLOA getRl() {
        return rl;
    }

    public void setRl(ReceitaLOA rl) {
        this.rl = rl;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public String caminhoVisualizar() {
        return "edita";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-realizada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public Boolean mostrarBotaoAlterarConjuntoFonte() {
        if (selecionado.getReceitaLOA() != null) {
            ReceitaLOA receitaLOA = lancamentoReceitaOrcFacade.getReceitaLOAFacade().recuperar(selecionado.getReceitaLOA().getId());
            if (receitaLOA.getReceitaLoaFontes().size() > 1) {
                return true;
            }
        }
        return false;
    }

    private void atribuirSubContaAndDivida() {
        selecionado.setSubConta(lancamentoReceitaOrcFacade.getSubContaFacade().recuperar(subConta.getId()));
        atribuirConvenioEDivida();
    }

    public void setarPessoa(ActionEvent evento) {
        selecionado.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
    }

    public List<LancamentoReceitaOrc> getLancamentoIntegracao() {
        return lancamentoIntegracao;
    }

    public void setLancamentoIntegracao(List<LancamentoReceitaOrc> lancamentoIntegracao) {
        this.lancamentoIntegracao = lancamentoIntegracao;
    }

    public void importarReceitasTributarias() {
        try {
            List<LancamentoReceitaOrc> lancamentoIntegracaoDeuCerto = new ArrayList<>();
            validarImportarReceita();
            if (validarReceitasBloqueadasAoSalvar()) {
                for (LancamentoReceitaOrc l : lancamentoIntegracao) {
                    try {
                        selecionado = l;
                        definirEventoReceitaRealizada();
                        subConta = l.getSubConta();
                        l.setReceitaDeIntegracao(Boolean.TRUE);

                        lancamentoReceitaOrcFacade.gerarLancamentoIntegracao(l);

                        validarLancamentoReceita(l);

                        lancamentoReceitaOrcFacade.salvarNovaReceita(selecionado, null);
                        lancamentoIntegracaoDeuCerto.add(l);

                    } catch (ValidacaoException ve) {
                        FacesUtil.printAllFacesMessages(ve.getMensagens());
                    } catch (ExcecaoNegocioGenerica e) {
                        lancamentoReceitaOrcFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(e);
                    } catch (Exception ex) {
                        FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
                    }
                }
                finalizarIntegracaoReceita(lancamentoIntegracaoDeuCerto);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void finalizarIntegracaoReceita(List<LancamentoReceitaOrc> lancamentoIntegracaoDeuCerto) {
        if (lancamentoIntegracaoDeuCerto.size() > 0) {
            FacesUtil.addOperacaoRealizada(" Foram importadas " + lancamentoIntegracaoDeuCerto.size() + " receitas de arrecadações tributárias.");
            if (lancamentoIntegracaoDeuCerto.containsAll(lancamentoIntegracao)) {
                redireciona();
            } else {
                buscarReceitaTributarias();
                FacesUtil.atualizarComponente("Formulario");
            }
        }
    }

    private void validarLancamentoReceita(LancamentoReceitaOrc receita) {
        ValidacaoException ve = new ValidacaoException();
        receita.realizarValidacoes();
        if (receita.getLancReceitaFonte().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi feito lançamentos de receitas fontes para essa receita realizada.");
        }
        if (receita.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        if (receita.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(receita.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(receita.getDataLancamento()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Conciliação deve ser igual ou superior a Data de Lançamento da Receita.");
            }
        }
        if (receita.getEventoContabil() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Evento contábil não encontrado para esta receita realizada.");
        }
        if (receita.getDividaPublica() != null && receita.getFonteDeRecursos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recurso deve ser informado.");
        }
        if (receita.getSubConta() != null && receita.getSubConta().getObrigarCodigoCO()) {
            for (LancReceitaFonte lrf : receita.getLancReceitaFonte()) {
                if (lrf.getCodigoCO() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Código CO deve ser informado para a Fonte <b>" + lrf.getReceitaLoaFonte().getFonteRecurso() + "</b>.");
                }
            }
        }
        if (isOperacaoNovo() && receita.getDataConciliacao() != null && DataUtil.getAno(receita.getDataConciliacao()).compareTo(getSistemaControlador().getExercicioCorrente().getAno()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O ano da <b>Data de Conciliação (" + DataUtil.getAno(receita.getDataConciliacao()) + ")</b> deve ser igual ao <b>Exercício Logado ("
                + getSistemaControlador().getExercicioCorrente().getAno() + ")</b>.");
        }
        ve.lancarException();
    }

    private void validarImportarReceita() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoIntegracao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado arrecadações tributárias para importação.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void buscarReceitaTributarias() {
        lancamentoIntegracao = Lists.newArrayList();
        try {
            IntegracaoTributarioContabilFiltros filtros = new IntegracaoTributarioContabilFiltros(selecionado.getUnidadeOrganizacional(), dataInicial, dataFinal, contaReceita, null);

            if (validaFiltrosIntegrecao()) {

                lancamentoIntegracao = lancamentoReceitaOrcFacade.getIntegracaoTributarioContabilFacade().getLancamentoReceitaOrcContabilizar(filtros);

                if (!lancamentoIntegracao.isEmpty()) {
                    ConfiguracaoContabil configuracaoContabil = lancamentoReceitaOrcFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente();

                    Preconditions.checkNotNull(configuracaoContabil.getClasseTribContReceitaRea(), " A Classe Credor configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");
                    bloqueadasPorFase = Lists.newArrayList();
                    for (LancamentoReceitaOrc l : lancamentoIntegracao) {
                        if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml", l.getDataLancamento(), selecionado.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                            bloqueadasPorFase.add(l);
                        } else {
                            if (validarLancamentoIntegracao(configuracaoContabil, l)) continue;
                        }
                    }
                    if (!bloqueadasPorFase.isEmpty()) {
                        FacesUtil.atualizarComponente("formMudaData");
                        FacesUtil.executaJavaScript("mudaData.show()");
                    }
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado arrecadações tributárias para importação. ");
                }
            }
        } catch (Exception ex) {
            lancamentoIntegracao = new ArrayList<>();
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public Boolean marcarReceitaBloqueada(LancamentoReceitaOrc l) {
        if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml", l.getDataLancamento(), l.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
            return true;
        }
        return false;
    }

    public Boolean validarReceitasBloqueadasAoSalvar() {
        for (LancamentoReceitaOrc l : lancamentoIntegracao) {
            if (lancamentoReceitaOrcFacade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml", l.getDataLancamento(), l.getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente())) {
                FacesUtil.executaJavaScript("mudaData.show()");
                return false;
            }
        }
        return true;
    }


    private boolean validarLancamentoIntegracao(ConfiguracaoContabil configuracaoContabil, LancamentoReceitaOrc l) {
        try {
            l.setPessoa(lancamentoReceitaOrcFacade.getPessoaFacade().recuperaPessoaDaEntidade(l.getUnidadeOrganizacional(), l.getDataLancamento()));
            l.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());
            Preconditions.checkNotNull(l.getSubConta().getId(), " A Conta Financeira não foi preenchida.");
            Preconditions.checkNotNull(l.getPessoa().getId(), " A Pessoa configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");
            lancamentoReceitaOrcFacade.getConfigReceitaRealizadaFacade().buscarEventoPorContaReceita(l.getReceitaLOA().getContaDeReceita(), TipoLancamento.NORMAL, l.getDataLancamento(), l.getOperacaoReceitaRealizada());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean validaFiltrosIntegrecao() {
        Boolean retorno = true;
        if (selecionado.getUnidadeOrganizacional() == null || selecionado.getUnidadeOrganizacional().getId() == null
            || dataInicial == null
            || dataFinal == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Os campos obrigatórios devem ser informados para filtrar as arrecadações tributárias.");
            retorno = false;
        }
        if (dataFinal.before(dataInicial)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A data final deve ser maior que a data inicial.");
            retorno = false;
        }
        return retorno;
    }

    public void limparCampos() {
        hierarquiaOrganizacional = null;
        contaReceita = null;
        dataInicial = sistemaControlador.getDataOperacao();
        Calendar c = Calendar.getInstance();
        c.setTime(dataInicial);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
        dataInicial = c.getTime();
        dataFinal = sistemaControlador.getDataOperacao();
    }

    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (lancamentoIntegracao != null) {
            for (LancamentoReceitaOrc lancamentoReceitaOrc : lancamentoIntegracao) {
                if (lancamentoReceitaOrc != null) {
                    valor = valor.add(lancamentoReceitaOrc.getValor());
                }
            }
        }
        return valor;
    }

    public Boolean validarNovaDataContabilizacao() {
        if (novaDataContabilizacao == null) {
            FacesUtil.addCampoObrigatorio("O campo Data deve ser informado.");
            return false;
        }
        return true;
    }

    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null
            && selecionado.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        if (selecionado.getId() != null
            && selecionado.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void baixar() {
        try {
            validarLancamentoReceita(selecionado);
            validarDataConciliacao();
            lancamentoReceitaOrcFacade.baixar(selecionado);
            FacesUtil.addOperacaoRealizada(" A Receita Realizada N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataLancamento()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            validarLancamentoReceita(selecionado);
            lancamentoReceitaOrcFacade.estornarBaixa(selecionado);
            FacesUtil.addOperacaoRealizada(" A Receita Realizada N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public ReceitaLOAFonte getReceitaLOAFonte() {
        return receitaLOAFonte;
    }

    public void setReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        this.receitaLOAFonte = receitaLOAFonte;
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/receita-realizada/listar/");
    }

    public void mostrarBotaoQueNaoDeveSeMostrado() {
        mostraBotaoQueNaoDeveSeMostrado = !getMostraBotaoQueNaoDeveSeMostrado();
    }

    public Long getConjuntoFontes() {
        return conjuntoFontes;
    }

    public void setConjuntoFontes(Long conjuntoFontes) {
        this.conjuntoFontes = conjuntoFontes;
    }

    public void atribuirContaBancariaEContaFinanceira() {
        setarContaBancaria();
        atribuirSubContaAndDivida();
        contasDeDestinacao = getContasDeDestinacaoDaSubConta();

    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = subConta.getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<LancamentoReceitaOrc> getBloqueadasPorFase() {
        return bloqueadasPorFase;
    }

    public void setBloqueadasPorFase(List<LancamentoReceitaOrc> bloqueadasPorFase) {
        this.bloqueadasPorFase = bloqueadasPorFase;
    }

    public List<ContaDeDestinacao> getContasDeDestinacao() {
        return contasDeDestinacao;
    }

    public void setContasDeDestinacao(List<ContaDeDestinacao> contasDeDestinacao) {
        this.contasDeDestinacao = contasDeDestinacao;
    }
}

