/*
 * Codigo gerado automaticamente em Thu Dec 22 14:32:20 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.exception.BloqueioMovimentoContabilException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReceitaORCEstornoFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "receitaORCEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-receita-realizada-estorno", pattern = "/receita-realizada-estorno/novo/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorcestorno/edita.xhtml"),
    @URLMapping(id = "editar-receita-realizada-estorno", pattern = "/receita-realizada-estorno/editar/#{receitaORCEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorcestorno/edita.xhtml"),
    @URLMapping(id = "ver-receita-realizada-estorno", pattern = "/receita-realizada-estorno/ver/#{receitaORCEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorcestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-receita-realizada-estorno", pattern = "/receita-realizada-estorno/listar/", viewId = "/faces/financeiro/orcamentario/lancamentoreceitaorcestorno/lista.xhtml")
})
public class ReceitaORCEstornoControlador extends PrettyControlador<ReceitaORCEstorno> implements Serializable, CRUD {


    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private TiposCredito tiposCredito;
    private Long conjuntoFontes;
    private List<FonteDeRecursos> fontesDaReceitaLoa;
    private ReceitaLOA rl;
    private ReceitaLOAFonte receitaLOAFonte;
    private List<FonteDeRecursos> fonteDeRecursosSubConta;

    //Atributos Integração com o Tributário
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataInicial;
    private Date dataFinal;
    private List<ReceitaORCEstorno> lancamentoIntegracao;
    private ContaReceita contaReceita;
    private ContaBancariaEntidade contaBancariaEntidade;
    private Boolean mostraBotaoQueNaoDeveSerMostrado;
    private List<ReceitaORCEstorno> bloqueadasPorFase;
    private Date novaDataContabilizacao;

    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterContaReceita;
    private ConverterAutoComplete converterLancamentoReceitaOrc;
    private ConverterAutoComplete converterContaBancaria;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterReceitaLOA;
    private ConverterAutoComplete converterPessoa;
    private MoneyConverter moneyConverter;


    public ReceitaORCEstornoControlador() {
        super(ReceitaORCEstorno.class);
    }


    @Override
    public AbstractFacade getFacede() {
        return receitaORCEstornoFacade;
    }

    @URLAction(mappingId = "novo-receita-realizada-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        receitaLOAFonte = new ReceitaLOAFonte();
        selecionado.setIntegracao(Boolean.FALSE);
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacionalOrc(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setDataEstorno(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        fonteDeRecursosSubConta = new ArrayList<>();


        if (receitaORCEstornoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", receitaORCEstornoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-receita-realizada-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receita-realizada-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    private void recuperarEditarVer() {
        selecionado = receitaORCEstornoFacade.recuperar(selecionado.getId());
        selecionado.setIntegracao(Boolean.FALSE);
        recuperaContaBancaria();
    }

    @Override
    public void salvar() {
        try {
            validarEstornoReceita();
            if (Operacoes.NOVO.equals(operacao)) {
                receitaORCEstornoFacade.salvarNovoEstorno(selecionado, conjuntoFontes);
            } else {
                receitaORCEstornoFacade.salvarEdicao(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (BloqueioMovimentoContabilException bmce) {
            FacesUtil.addOperacaoNaoPermitida(bmce.getMessage());
        } catch (ExcecaoNegocioGenerica ex) {
            receitaORCEstornoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarEstornoReceita() {
        ValidacaoException ve = new ValidacaoException();

        selecionado.realizarValidacoes();

        if (Operacoes.NOVO.equals(operacao)) {
            if (selecionado.getLancamentoReceitaOrc() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Receita Realizada deve ser informado.");
            }
            if (selecionado.getReceitaORCFonteEstorno().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Para salvar, é necessário efetuar o cálculo para as receitas fontes clicando no botão 'Calcular Laçamentos'.");
            }
            if (selecionado.getLancamentoReceitaOrc() != null) {
                if (selecionado.getLancamentoReceitaOrc().getSaldo().compareTo(selecionado.getValor()) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de " + "<b>" + Util.formataValor(selecionado.getValor()) + "</b>" + " é maior que o saldo de " + "<b>" + Util.formataValor(selecionado.getLancamentoReceitaOrc().getSaldo()) + "</b>" + " disponível na Receita Realizada.");
                }
                if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).before(DataUtil.dataSemHorario(selecionado.getLancamentoReceitaOrc().getDataLancamento()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do estorno deve ser maior ou igual a data da receita realizada. Data do lançamento: <b>" + DataUtil.getDataFormatada(selecionado.getLancamentoReceitaOrc().getDataLancamento()) + "</b>.");
                }

                receitaORCEstornoFacade.getHierarquiaOrganizacionalFacade()
                    .validarUnidadesIguais(selecionado.getLancamentoReceitaOrc().getUnidadeOrganizacional(),
                        selecionado.getUnidadeOrganizacionalOrc(),
                        ve,
                        "A Unidade Organizacional do estorno de receita realizada deve ser a mesma da receita realizada.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void setaPessoa(SelectEvent evt) {
        ReceitaORCEstorno rec = ((ReceitaORCEstorno) selecionado);
        Pessoa p = (Pessoa) evt.getObject();
        rec.setPessoa(p);
        rec.setClasseCredor(null);
    }

    public void setarReceitaLOA(SelectEvent event) {
        ReceitaLOA receita = (ReceitaLOA) event.getObject();
        if (receita != null) {
            selecionado.setContaFinanceira(null);
            ((ReceitaORCEstorno) selecionado).setReceitaLOA(receitaORCEstornoFacade.getReceitaLOAFacade().recuperar(receita.getId()));
            tiposCredito = ((ContaReceita) receita.getContaDeReceita()).getTiposCredito();
        }
        if (selecionado.getReceitaLOA().getConjuntos().size() == 1) {
            receitaLOAFonte.setItem(1l);
            selecionado.getReceitaLOA().getConjuntos().get(0);
            conjuntoFontes = selecionado.getReceitaLOA().getConjuntos().get(0);
            fontesDaReceitaLoa = receitaORCEstornoFacade.recuperarFontesDaReceitaLoa(selecionado, conjuntoFontes);
            FacesUtil.executaJavaScript("painelInformacoes.show()");
        } else {
            selecionado.getReceitaLOA().getConjuntos();
            FacesUtil.executaJavaScript("dialogConjFontes.show()");
        }
        recuperarEventoContabil();
    }

    public void armazenarConjuntoFonte() {
        if (selecionado.getReceitaLOA() != null) {
            if (receitaLOAFonte.getItem() != null) {
                conjuntoFontes = receitaLOAFonte.getItem();
                fontesDaReceitaLoa = receitaORCEstornoFacade.recuperarFontesDaReceitaLoa(selecionado, conjuntoFontes);
                selecionado.setContaFinanceira(null);
                contaBancariaEntidade = null;
                selecionado.getReceitaORCFonteEstorno().clear();
            } else {
                throw new ExcecaoNegocioGenerica("O campo Conjunto de Fonte deve ser informado.");
            }
        }
    }

    public List<SelectItem> getConjuntoDeFontes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        if (selecionado.getReceitaLOA() != null) {
            for (BigDecimal item : receitaORCEstornoFacade.getReceitaLOAFacade().recuperarConjuntoDeFontePorReceitaLoa(selecionado.getReceitaLOA(), sistemaControlador.getExercicioCorrente())) {
                toReturn.add(new SelectItem(item.longValue(), item.toString()));
            }
        }
        return toReturn;
    }

    public void recuperarReceitaLOA() {
        ReceitaLOA receitaLOA = receitaORCEstornoFacade.getReceitaLOAFacade().recuperar(selecionado.getReceitaLOA().getId());
        selecionado.setReceitaLOA(receitaLOA);
        selecionado.getReceitaLOA().getConjuntos().get(0);
    }

    public void recuperarContaFinanceira() {
        SubConta contaFinanceira = receitaORCEstornoFacade.getSubContaFacade().recuperar(selecionado.getContaFinanceira().getId());
        selecionado.setContaFinanceira(contaFinanceira);
    }

    public List getFonteRecursoPorConjuntoFontes() {
        try {
            if (selecionado.getReceitaLOA() != null) {
                return receitaORCEstornoFacade.getReceitaLOAFacade().recuperarFonteDeRecursoPorConjuntoDeFonte(selecionado.getReceitaLOA(), receitaLOAFonte);
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao recuperar Fonte de Recurso " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<ContaBancariaEntidade> completaContaBancaria(String parte) {
        return receitaORCEstornoFacade.getContaBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), selecionado.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente(), null, null, null);
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return receitaORCEstornoFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), ((ReceitaORCEstorno) selecionado).getPessoa());
    }


    public void setaContaBancariaEContaFianceira() {
        setarContaBancaria();
        fonteDeRecursosSubConta = getFontesRecursoSubConta();
    }

    public List<FonteDeRecursos> getFontesRecursoSubConta() {
        if (selecionado.getContaFinanceira() != null) {
            return receitaORCEstornoFacade.recuperarFontesDaSubConta(selecionado);
        }
        return new ArrayList<>();
    }

    public void setarContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getContaFinanceira().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, receitaORCEstornoFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterLancamentoReceitaOrc() {
        if (converterLancamentoReceitaOrc == null) {
            converterLancamentoReceitaOrc = new ConverterAutoComplete(LancamentoReceitaOrc.class, receitaORCEstornoFacade.getLancamentoReceitaOrcFacade());
        }
        return converterLancamentoReceitaOrc;
    }

    public List<LancamentoReceitaOrc> completaLancamento(String parte) {
        return receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().completaLancamentoReceitaORC(parte, sistemaControlador.getExercicioCorrente(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public BigDecimal getSaldoLancamento() {
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        ReceitaORCEstorno ee = ((ReceitaORCEstorno) selecionado);

        if (ee.getLancamentoReceitaOrc() != null) {

            saldo = ee.getLancamentoReceitaOrc().getSaldo();

        }
        return saldo;
    }

    public Boolean mostrarBotaoAlterarConjuntoFonte() {
        if (selecionado.getReceitaLOA() != null) {
            ReceitaLOA receitaLOA = receitaORCEstornoFacade.getReceitaLOAFacade().recuperar(selecionado.getReceitaLOA().getId());
            if (receitaLOA.getReceitaLoaFontes().size() > 1) {
                return true;
            }
        }
        return false;
    }

    public Boolean getVerificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public String actionSelecionar() {
        return "edita";
    }

    public void selecionarReceitaRealizada(ActionEvent evento) {
        ((ReceitaORCEstorno) selecionado).setLancamentoReceitaOrc((LancamentoReceitaOrc) evento.getComponent().getAttributes().get("objeto"));
        LancamentoReceitaOrc lanc = selecionado.getLancamentoReceitaOrc();
        definirAtributosReceita(lanc);
    }

    public void setaLancamento(SelectEvent evt) {
        LancamentoReceitaOrc lanc = (LancamentoReceitaOrc) evt.getObject();
        definirAtributosReceita(lanc);
    }

    private void definirAtributosReceita(LancamentoReceitaOrc lanc) {
        selecionado.setLancamentoReceitaOrc(lanc);
        selecionado.setReceitaLOA(lanc.getReceitaLOA());
        selecionado.setValor(lanc.getSaldo());
        selecionado.setContaFinanceira(selecionado.getLancamentoReceitaOrc().getSubConta());
        selecionado.setPessoa(lanc.getPessoa());
        selecionado.setClasseCredor(lanc.getClasseCredor());
        selecionado.setOperacaoReceitaRealizada(lanc.getOperacaoReceitaRealizada());
        selecionado.setDataReferencia(lanc.getDataReferencia());
        selecionado.setComplementoHistorico(lanc.getComplemento());
        selecionado.getReceitaORCFonteEstorno().clear();
        recuperarConjuntoFonteDaReceitaRealizada(lanc);
        getSaldoLancamento();
        recuperarEventoContabil();
        recuperaContaBancaria();
    }

    private void recuperarConjuntoFonteDaReceitaRealizada(LancamentoReceitaOrc lanc) {
        if (lanc != null) {
            lanc = receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().recuperar(selecionado.getLancamentoReceitaOrc().getId());
            for (Long conj : lanc.getConjuntos()) {
                conjuntoFontes = conj;
            }
        }
    }

    public void recuperarEventoContabil() {
        try {
            if (selecionado.getReceitaLOA() != null && selecionado.getOperacaoReceitaRealizada() != null) {
                ConfigReceitaRealizada configuracao = receitaORCEstornoFacade.getConfigReceitaRealizadaFacade().buscarEventoPorContaReceita(
                    selecionado.getReceitaLOA().getContaDeReceita(),
                    TipoLancamento.ESTORNO,
                    selecionado.getDataEstorno(),
                    selecionado.getOperacaoReceitaRealizada());
                Preconditions.checkArgument(configuracao != null, " Não foi encontrada configuração de evento para os parametros selecionados em data e receita.");
                selecionado.setEventoContabil(configuracao.getEventoContabil());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            selecionado.setEventoContabil(null);
        }
    }

    public void remoteCommandEvento() {
        ((ReceitaORCEstorno) selecionado).setEventoContabil(null);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-realizada-estorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recalcular() {
        ReceitaORCEstorno receita = ((ReceitaORCEstorno) selecionado);
        for (ReceitaORCFonteEstorno receitaEstorno : receita.getReceitaORCFonteEstorno()) {
            if (receitaEstorno.getReceitaLoaFonte().getPercentual().equals(BigDecimal.ZERO)) {
                FacesUtil.addInfo("", "O Percentual previsto no orçamento para a fonte " + receitaEstorno.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos() + " está igual a 0 (ZERO).");
            }
            receitaEstorno.setValor((receita.getValor().multiply(receitaEstorno.getReceitaLoaFonte().getPercentual())).divide(new BigDecimal(100)));
        }
    }

    public BigDecimal somaRecEstornos() {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        dif = receitaORCEstornoFacade.somaReceitaEstornos(((ReceitaORCEstorno) selecionado));
        return dif;
    }

    public BigDecimal somaLancamentos() {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        dif = receitaORCEstornoFacade.somaLancamentos(((ReceitaORCEstorno) selecionado));
        return dif;
    }

    public void removeLancReceitasFontes(ActionEvent e) {
        ReceitaORCFonteEstorno rfe = (ReceitaORCFonteEstorno) e.getComponent().getAttributes().get("recEstornos");
        ((ReceitaORCEstorno) selecionado).getReceitaORCFonteEstorno().remove(rfe);
        selecionado.setValor(BigDecimal.ZERO);
    }

    public BigDecimal calculaDiferenca() {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        if (!selecionado.getReceitaORCFonteEstorno().isEmpty()) {
            dif = receitaORCEstornoFacade.calculaDiferenca((ReceitaORCEstorno) selecionado);
        }
        return dif;
    }

    public void gerarLancamentoParaFonte() {
        try {
            receitaORCEstornoFacade.geraLancamentos((ReceitaORCEstorno) selecionado, conjuntoFontes);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());
        }
    }

    public ConverterAutoComplete getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, receitaORCEstornoFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, receitaORCEstornoFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancaria;
    }


    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public ConverterAutoComplete getConverterReceitaLOA() {
        if (converterReceitaLOA == null) {
            converterReceitaLOA = new ConverterAutoComplete(ReceitaLOA.class, receitaORCEstornoFacade.getReceitaLOAFacade());
        }
        return converterReceitaLOA;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, receitaORCEstornoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(Conta.class, receitaORCEstornoFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return receitaORCEstornoFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA");
    }

    public List<HierarquiaOrganizacional> completaUnidade(String parte) {
        try {
            return receitaORCEstornoFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", getSistemaControlador().getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacionalOrc(hierarquiaOrganizacional.getSubordinada());
            HierarquiaOrganizacional ho = receitaORCEstornoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaAdministrativaDaOrcamentaria(selecionado.getUnidadeOrganizacionalOrc(), selecionado.getDataEstorno());
            selecionado.setUnidadeOrganizacionalAdm(ho.getSubordinada());
        }
    }

    public void adicionarUnidadeDaHierarquiaSelecionadaIntegracao() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacionalOrc(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public List<Conta> completaContaReceita(String parte) {
        return receitaORCEstornoFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        try {
            List<SubConta> subContas = new ArrayList<>();

            if (contaBancariaEntidade != null) {
                subContas = receitaORCEstornoFacade.getSubContaFacade().listaPorContaBancariaUnidadeFonteRecurso(parte.trim(), contaBancariaEntidade, selecionado.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente(), idFontesAsString());
            } else {
                subContas = receitaORCEstornoFacade.getSubContaFacade().listaPorContaBancariaUnidadeFonteRecurso(parte.trim(), null, selecionado.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente(), idFontesAsString());
                if (subContas.isEmpty()) {
                    FacesUtil.addAtencao("Conta Financeira não encontrada para as fontes de recursos da receita loa.");
                }
            }
            return subContas;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar a conta financeira " + ex.getMessage());
        }
    }

    public String idFontesAsString() {
        StringBuilder stb = new StringBuilder();
        String listaIdsFonte = "";
        if (fontesDaReceitaLoa.size() > 0) {
            for (FonteDeRecursos lista : fontesDaReceitaLoa) {
                listaIdsFonte += lista.getId() + ",";
            }
            stb.append(" AND ").append(" SCF.FONTEDERECURSOS_ID IN (").append(listaIdsFonte.substring(0, listaIdsFonte.length() - 1)).append(") ");
        }
        return stb.toString();
    }

    public List<ReceitaLOA> completarReceitaLOA(String parte) {
        return receitaORCEstornoFacade.getReceitaLOAFacade().buscarContaReceitaPorUnidadeExercicioAndOperacaoReceita(
            parte.trim(),
            selecionado.getExercicio(),
            selecionado.getUnidadeOrganizacionalOrc(),
            selecionado.getOperacaoReceitaRealizada());
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

    public List<ReceitaORCEstorno> getLancamentoIntegracao() {
        return lancamentoIntegracao;
    }

    public void setLancamentoIntegracao(List<ReceitaORCEstorno> lancamentoIntegracao) {
        this.lancamentoIntegracao = lancamentoIntegracao;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public void importarReceitasTributarias() {
        try {
            List<ReceitaORCEstorno> lancamentoIntegracaoDeuCerto = new ArrayList<>();
            validarImportarReceita();
            if (validarReceitasBloqueadasAoSalvar()) {
                for (ReceitaORCEstorno l : lancamentoIntegracao) {
                    try {
                        selecionado = l;
                        recuperarEventoContabil();
                        l.setIntegracao(Boolean.TRUE);
                        receitaORCEstornoFacade.gerarLancamentoIntegracao(selecionado);

                        l.realizarValidacoes();
                        receitaORCEstornoFacade.salvarNovoEstorno(selecionado, null);
                        lancamentoIntegracaoDeuCerto.add(l);

                    } catch (ValidacaoException ve) {
                        FacesUtil.printAllFacesMessages(ve.getMensagens());
                    } catch (ExcecaoNegocioGenerica e) {
                        receitaORCEstornoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(e);
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

    private void finalizarIntegracaoReceita(List<ReceitaORCEstorno> lancamentoIntegracaoDeuCerto) {
        if (lancamentoIntegracaoDeuCerto.size() > 0) {
            FacesUtil.addOperacaoRealizada(" Foram importadas " + lancamentoIntegracaoDeuCerto.size() + " estornos de receitas de arrecadações tributárias.");
            if (lancamentoIntegracaoDeuCerto.containsAll(lancamentoIntegracao)) {
                redireciona();
            } else {
                buscarReceitaTributarias();
                FacesUtil.atualizarComponente("Formulario");
            }
        }
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
        try {
            IntegracaoTributarioContabilFiltros filtros = new IntegracaoTributarioContabilFiltros(selecionado.getUnidadeOrganizacionalOrc(), dataInicial, dataFinal, contaReceita, null);

            if (validaFiltrosIntegrecao()) {
                lancamentoIntegracao = receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getIntegracaoTributarioContabilFacade().getLancamentoReceitaOrcEstornoContabilizar(filtros);

                if (!lancamentoIntegracao.isEmpty()) {
                    ConfiguracaoContabil configuracaoContabil = receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getConfiguracaoContabilFacade().configuracaoContabilVigente();

                    Preconditions.checkNotNull(configuracaoContabil.getClasseTribContReceitaRea(), " A Classe Credor configurada para Integração Contábil/Tributário para o Estorno de Receita Realizada não foi informada.");

                    bloqueadasPorFase = Lists.newArrayList();
                    for (ReceitaORCEstorno le : lancamentoIntegracao) {
                        if (receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml", le.getDataEstorno(), selecionado.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente())) {
                            bloqueadasPorFase.add(le);
                        } else {
                            if (validarIntegracaoReceitaOrcEstorno(configuracaoContabil, le)) continue;
                        }
                    }
                    if (!bloqueadasPorFase.isEmpty()) {
                        FacesUtil.atualizarComponente("formMudaData");
                        FacesUtil.executaJavaScript("mudaData.show()");
                    }
                } else {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), " Não foi encontrado estornos de arrecadações tributárias para importação. ");
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public Boolean marcarReceitaBloqueada(ReceitaORCEstorno est) {
        if (receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml", est.getDataEstorno(), est.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente())) {
            return true;
        }
        return false;
    }

    public Boolean validarReceitasBloqueadasAoSalvar() {
        for (ReceitaORCEstorno est : lancamentoIntegracao) {
            if (receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml", est.getDataEstorno(), est.getUnidadeOrganizacionalOrc(), sistemaControlador.getExercicioCorrente())) {
                FacesUtil.executaJavaScript("mudaData.show()");
                return false;
            }
        }
        return true;
    }


    private boolean validarIntegracaoReceitaOrcEstorno(ConfiguracaoContabil configuracaoContabil, ReceitaORCEstorno le) {
        try {
            le.setPessoa(receitaORCEstornoFacade.getLancamentoReceitaOrcFacade().getPessoaFacade().recuperaPessoaDaEntidade(le.getUnidadeOrganizacionalOrc(), le.getDataEstorno()));
            le.setClasseCredor(configuracaoContabil.getClasseTribContReceitaRea());

            receitaORCEstornoFacade.getConfigReceitaRealizadaFacade().buscarEventoPorContaReceita(
                le.getReceitaLOA().getContaDeReceita(),
                TipoLancamento.ESTORNO,
                le.getDataEstorno(),
                le.getOperacaoReceitaRealizada());

        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean validarNovaDataContabilizacao() {
        if (novaDataContabilizacao == null) {
            FacesUtil.addCampoObrigatorio("O campo Data deve ser informado.");
            return false;
        }
        return true;
    }

    private boolean validaFiltrosIntegrecao() {
        Boolean retorno = true;
        if (selecionado.getUnidadeOrganizacionalOrc() == null || selecionado.getUnidadeOrganizacionalOrc().getId() == null
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

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public void recuperaContaBancaria() {
        try {
            contaBancariaEntidade = selecionado.getLancamentoReceitaOrc().getSubConta().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public Boolean desabilitarCampoValor() {
        if (!selecionado.getReceitaORCFonteEstorno().isEmpty()) {
            return true;
        }
        return false;
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }


    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (lancamentoIntegracao != null) {
            for (ReceitaORCEstorno receitaORCEstorno : lancamentoIntegracao) {
                if (receitaORCEstorno != null) {
                    valor = valor.add(receitaORCEstorno.getValor());
                }
            }
        }
        return valor;
    }

    public Boolean getMostraBotaoQueNaoDeveSerMostrado() {
        return mostraBotaoQueNaoDeveSerMostrado != null ? mostraBotaoQueNaoDeveSerMostrado : false;
    }

    public void mostrarBotaoQueNaoDeveSeMostrado() {
        mostraBotaoQueNaoDeveSerMostrado = !getMostraBotaoQueNaoDeveSerMostrado();
    }

    public List<ReceitaORCEstorno> getBloqueadasPorFase() {
        return bloqueadasPorFase;
    }

    public void setBloqueadasPorFase(List<ReceitaORCEstorno> bloqueadasPorFase) {
        this.bloqueadasPorFase = bloqueadasPorFase;
    }

    public Date getNovaDataContabilizacao() {
        return novaDataContabilizacao;
    }

    public void setNovaDataContabilizacao(Date novaDataContabilizacao) {
        this.novaDataContabilizacao = novaDataContabilizacao;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public Long getConjuntoFontes() {
        return conjuntoFontes;
    }

    public void setConjuntoFontes(Long conjuntoFontes) {
        this.conjuntoFontes = conjuntoFontes;
    }

    public List<FonteDeRecursos> getFonteDeRecursosSubConta() {
        return fonteDeRecursosSubConta;
    }

    public void setFonteDeRecursosSubConta(List<FonteDeRecursos> fonteDeRecursosSubConta) {
        this.fonteDeRecursosSubConta = fonteDeRecursosSubConta;
    }

    public ReceitaLOA getRl() {
        return rl;
    }

    public void setRl(ReceitaLOA rl) {
        this.rl = rl;
    }

    public List<FonteDeRecursos> getFontesDaReceitaLoa() {
        return fontesDaReceitaLoa;
    }

    public void setFontesDaReceitaLoa(List<FonteDeRecursos> fontesDaReceitaLoa) {
        this.fontesDaReceitaLoa = fontesDaReceitaLoa;
    }

    public ReceitaLOAFonte getReceitaLOAFonte() {
        return receitaLOAFonte;
    }

    public void setReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        this.receitaLOAFonte = receitaLOAFonte;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
