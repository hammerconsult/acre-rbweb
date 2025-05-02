/*
 * Codigo gerado automaticamente em Mon Sep 26 15:49:23 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.PercentualConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editar-receita-loa", pattern = "/receita-loa/editar/#{receitaLOAControlador.id}/", viewId = "/faces/financeiro/ppa/receitaloa/edita.xhtml"),
    @URLMapping(id = "ver-receita-loa", pattern = "/receita-loa/ver/#{receitaLOAControlador.id}/", viewId = "/faces/financeiro/ppa/receitaloa/visualizar.xhtml"),
    @URLMapping(id = "lista-receita-loa", pattern = "/receita-loa/listar/", viewId = "/faces/financeiro/ppa/receitaloa/lista.xhtml")
})
public class ReceitaLOAControlador extends PrettyControlador<LOA> implements Serializable, CRUD {

    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    private ConverterGenerico converterLoa;
    @EJB
    private ContaFacade contaDeReceitaFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterContaDeReceita;
    private ConverterAutoComplete converterContaDeReceitaFonte;
    private LOA loa;
    private ReceitaLOAFonte receitaLOAFonte;
    private ReceitaLOA receitaLOA;
    private MoneyConverter moneyConverter;
    private PercentualConverter percentualConverter;
    private Conta contaReceita;
    private HierarquiaOrganizacional hierarquiaOrgResponsavel;
    private UIComponent componente;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private BigDecimal valorReceitaLOA;
    private String codigoReceita = "";
    private String fraseErro = "";
    private String filtroReceita;

    public ReceitaLOAControlador() {
        super(LOA.class);
    }

    public LOAFacade getFacade() {
        return loaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return loaFacade;
    }

    public ReceitaLOAFonte getReceitaLOAFonte() {
        return receitaLOAFonte;
    }

    public void setReceitaLOAFonte(ReceitaLOAFonte receitaLOAFonte) {
        this.receitaLOAFonte = receitaLOAFonte;
    }

    public LOA getLoa() {
        return loa;
    }

    public void setLoa(LOA loa) {
        this.loa = loa;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public List<SelectItem> getContaDeReceita() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Conta object : contaDeReceitaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            if (!eo.name().equals("ORCAMENTOGERAL")) {
                toReturn.add(new SelectItem(eo, eo.getDescricao()));
            }
        }
        return toReturn;
    }

    public Boolean mostraArredondamento() {
        Boolean controle = true;
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getRounding()) {
                controle = false;
            }
        }
        return controle;
    }

    public Converter getConverterContaDeReceita() {
        if (converterContaDeReceita == null) {
            converterContaDeReceita = new ConverterAutoComplete(Conta.class, contaDeReceitaFacade);
        }
        return converterContaDeReceita;
    }

    public Converter getConverterContaDeReceitaFonte() {
        if (converterContaDeReceitaFonte == null) {
            converterContaDeReceitaFonte = new ConverterAutoComplete(Conta.class, contaDeReceitaFacade);
        }
        return converterContaDeReceitaFonte;
    }

    public List<LOA> listaLoas() {
        return receitaLOAFacade.listaLoas();
    }

    public void instancia() {
//        receitaLOA = new ReceitaLOA();
    }

    public Boolean validaValorFracionado() {
        BigDecimal valorReceitaLOAArredondado = valorReceitaLOA;
        valorReceitaLOAArredondado = valorReceitaLOAArredondado.setScale(0, BigDecimal.ROUND_UP);
        if (valorReceitaLOA.subtract(valorReceitaLOAArredondado).compareTo(BigDecimal.ZERO) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "Foi adicionado um valor fracionado"));
        }
        return true;
    }

    public void validaValorFracionadoTabela() {
        LOA l = ((LOA) selecionado);
        BigDecimal totalValoresTabela = BigDecimal.ZERO;
        BigDecimal totalValoresTabelaArredondado = BigDecimal.ZERO;
        for (ReceitaLOA rl : l.getReceitaLOAs()) {
            totalValoresTabela = totalValoresTabela.add(rl.getValor());
        }
        totalValoresTabelaArredondado = totalValoresTabela;
        totalValoresTabelaArredondado = totalValoresTabelaArredondado.setScale(0, BigDecimal.ROUND_UP);
        if (totalValoresTabela.subtract(totalValoresTabelaArredondado).compareTo(BigDecimal.ZERO) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "Existe valores fracionados na tabela"));
        }
    }

    public Boolean validaCodigoReceita(String codigo) {
        if (!codigo.equals("")) {
            if (receitaLOAFacade.verificaCodigoReceita(receitaLOA)) {
                return false;
            }
            for (ReceitaLOA rl : ((LOA) selecionado).getReceitaLOAs()) {
                if (rl.getCodigoReduzido().equals(codigo)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getVerificaMascaraReceitaLOA() {
        ConfiguracaoPlanejamentoPublico cpp = receitaLOAFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp.getMascaraCodigoReceitaLOA() == null) {
            return null;
        }
        return cpp.getMascaraCodigoReceitaLOA().replaceAll("#", "9");
    }

    public void calculaPorPercentual(PrevisaoReceitaOrc prev) {
        PrevisaoReceitaOrc p = prev;
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        valor = (p.getReceitaLOA().getValor().multiply(prev.getPercentual())).divide(new BigDecimal(100));
        p.setValorProgramado(valor);
        RequestContext context = RequestContext.getCurrentInstance();
    }

    public void calculaPorProgramado(PrevisaoReceitaOrc prev) {
        PrevisaoReceitaOrc p = prev;
        BigDecimal valor = new BigDecimal(BigInteger.ZERO);
        BigDecimal prog = p.getValorProgramado();
        BigDecimal saldo = p.getReceitaLOA().getValor();
        valor = (prog.multiply(new BigDecimal(100))).divide(saldo, 4, RoundingMode.HALF_UP);
        p.setPercentual(valor);
    }

    public BigDecimal somaTotaisPercentuais(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            soma = soma.add(p.getPercentual());
        }
        return soma;
    }

    public BigDecimal somaTotaisProgramados(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            soma = soma.add(p.getValorProgramado());
        }
        return soma;
    }

    public BigDecimal somaTotaisUtilizados(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            soma = soma.add(p.getValorUtilizado());
        }
        return soma;
    }

    public BigDecimal somaSaldosTotais(ReceitaLOA rl) {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            soma = soma.add(p.getValorProgramado().subtract(p.getValorUtilizado()));
        }
        return soma;
    }

    public void calculaSaldoCumulativo(ReceitaLOA rl) {
        BigDecimal saldoAnteriorCota = new BigDecimal(BigInteger.ZERO);
        for (PrevisaoReceitaOrc p : rl.getPrevisaoReceitaOrc()) {
            p.setSaldo((p.getValorProgramado().subtract(p.getValorUtilizado())).add(saldoAnteriorCota));
            saldoAnteriorCota = p.getSaldo();
        }
    }

    public BigDecimal calculaDiferenca(ReceitaLOA rl) {
        BigDecimal dif = new BigDecimal(BigInteger.ZERO);
        dif = rl.getValor().subtract(somaTotaisProgramados(rl));
        return dif;
    }

//    @Override
//    public List getLista() {
//        lista = receitaLOAFacade.listaPorExercicio(sistemaControlador.getExercicioCorrente());
//        return lista;
//    }
    public void atualizaTabela(ReceitaLOA receita) {
        RequestContext context = RequestContext.getCurrentInstance();
        calculaSaldoCumulativo(receita);
        context.update("Formulario:tabelaReceitas:" + ((LOA) selecionado).getReceitaLOAs().indexOf(receita) + ":tabelaPrevisoes");
    }

    public void instanciaParametros() {
        valorReceitaLOA = new BigDecimal(BigInteger.ZERO);
        contaReceita = new Conta();
        receitaLOA = new ReceitaLOA();
        codigoReceita = "";
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        fraseErro = "";
    }

    public void addicionaReceitas() {
        if (hierarquiaOrgResponsavel != null && contaReceita != null) {
            if (hierarquiaOrgResponsavel.getId() != null) {
                if (validaCodigoReceita(codigoReceita)) {
                    receitaLOA.setEntidade(hierarquiaOrgResponsavel.getSubordinada());
                    receitaLOA.setLoa(loa);
                    receitaLOA.setContaDeReceita(contaReceita);
                    receitaLOA.setValor(valorReceitaLOA);
                    receitaLOA.setSaldo(valorReceitaLOA);
                    try {
                        Date data = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + sistemaControlador.getExercicioCorrenteAsInteger());
                        receitaLOA.setDataRegistro(data);
                    } catch (Exception e) {
                    }
                    ((LOA) selecionado).getReceitaLOAs().add(receitaLOA);
                    validaValorFracionado();
                    instanciaParametros();
                } else {
                    FacesContext.getCurrentInstance().addMessage("Formulario:botao", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Código Reduzido da Receita LOA já cadastrado!"));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage("Formulario:botao", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Preencha todos os campos antes de adicionar na lista!"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:botao", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Preencha todos os campos antes de adicionar na lista!"));
        }
    }

    public void addicionaReceitaFonteAssocia(ActionEvent e) {
        receitaLOA = (ReceitaLOA) e.getComponent().getAttributes().get("objetos");
        receitaLOAFonte.setReceitaLOA(receitaLOA);
        if (((LOA) selecionado).getEfetivada()) {
            receitaLOAFonte.setValor(new BigDecimal(BigInteger.ZERO));
        }
        receitaLOAFacade.geraPrevisoesReceitaOrc(receitaLOA);
    }

    public boolean validaLoaEfetivada(ReceitaLOA r) {
        if (loa.getEfetivada() && r.getValor().compareTo(BigDecimal.ZERO) != 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setaFonteDespesa(SelectEvent evt) {
        ContaDeDestinacao cd = (ContaDeDestinacao) evt.getObject();
        receitaLOAFonte.setDestinacaoDeRecursos(cd);
    }

    public void addicionaReceitaFonte() {
        if (receitaLOAFonte.getDestinacaoDeRecursos() == null) {
            FacesContext.getCurrentInstance().addMessage("formDialogo:fonteDeRecursos", new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Informe uma Fonte de Recurso"));
        }
        else if (receitaLOAFonte.getPercentual().compareTo(BigDecimal.ZERO) > 0) {
            if ((verificaPorcentagensFontes().add(receitaLOAFonte.getPercentual())).compareTo(new BigDecimal(100)) <= 0) {
                receitaLOAFonte.setDateRegistro(new Date());
                receitaLOAFonte.setValor((receitaLOAFonte.getPercentual().multiply(receitaLOA.getValor()).divide(new BigDecimal(100))));
                ((LOA) selecionado).getReceitaLOAs().get(((LOA) selecionado).getReceitaLOAs().indexOf(receitaLOA)).getReceitaLoaFontes().add(receitaLOAFonte);
                receitaLOAFonte = new ReceitaLOAFonte();
                receitaLOAFonte.setReceitaLOA(receitaLOA);
                receitaLOAFonte.setDestinacaoDeRecursos(null);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, " ", "Porcentagem informada ultrapassa o limite de 100%"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("formDialogo:percentual", new FacesMessage(FacesMessage.SEVERITY_WARN, " ", "O campo percentual deve ser maior que zero!"));
        }
    }

    public BigDecimal verificaPorcentagensFontes() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            soma = soma.add(rlf.getPercentual());
        }
        return soma;
    }

    public void recalculaValoresReceita() {
        //System.out.println("entrou no recalcula");
        if (validaAlteraValorReceitaLOA()) {
            //System.out.println("passou recalcula");
            calculaPrevisoes();
            calculaFontes();
            receitaLOA.setSaldo(receitaLOA.getValor());
            RequestContext.getCurrentInstance().execute("valorReceita.hide()");
        }
    }

    public void calculaPrevisoes() {
        for (PrevisaoReceitaOrc pro : receitaLOA.getPrevisaoReceitaOrc()) {
            pro.setValorProgramado((receitaLOA.getValor().multiply(pro.getPercentual()).divide(new BigDecimal(100))));
        }
    }

    public void calculaFontes() {
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            rlf.setValor((receitaLOA.getValor().multiply(rlf.getPercentual()).divide(new BigDecimal(100))));
        }
    }

    public void alteraValorReceita(ActionEvent ae) {
        receitaLOA = (ReceitaLOA) ae.getComponent().getAttributes().get("alteraValor");
    }

    public Boolean validaAlteraValorReceitaLOA() {
        //System.out.println("entra no valida");
        boolean retorno = true;
        if (receitaLOA.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            //System.out.println("entra no if");
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar.", "O valor não deve ser negativo."));
            receitaLOA.setValor(BigDecimal.ZERO);
        }
        return retorno;
    }

    public void liberaReceitaLOA() {
        BigDecimal soma = verificaPorcentagensFontes();
        if (!receitaLOA.getReceitaLoaFontes().isEmpty()) {
            if (soma.compareTo(new BigDecimal(100)) != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Porcentagem informada não é 100%"));
            } else {
                Boolean b = true;
                for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
                    if (rlf.getRounding()) {
                        b = false;
                    }
                }
                if (b) {
                    receitaLOA.getReceitaLoaFontes().get(0).setRounding(true);
                }
                instanciaParametros();
                RequestContext.getCurrentInstance().execute("dialogo.hide()");
            }
        } else {
            instanciaParametros();
            RequestContext.getCurrentInstance().execute("dialogo.hide()");
        }
    }

    public void selecionaRouding(ReceitaLOAFonte rlf) {
        rlf.setRounding(Boolean.TRUE);
    }

    public BigDecimal somaTotaisPercentuais() {
        BigDecimal soma = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            soma = soma.add(rlf.getPercentual());
        }
        return soma;
    }

    public BigDecimal getTotalReceitas() {
        BigDecimal somaTotal = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOA r : ((LOA) selecionado).getReceitaLOAs()) {
//            if (!r.getContaDeReceita().getCodigo().startsWith("9") || r.getTipoPrevisaoReceitaLoa().equals(TipoPrevisaoReceitaLoa.DEDUCAO)) {
//                somaTotal = somaTotal.add(r.getValor());
//            } else {
//                somaTotal = somaTotal.subtract(r.getValor());
//            }
        }
        return somaTotal;
    }

    public BigDecimal getRestoReceitas() {
        BigDecimal restaTotal = BigDecimal.ZERO;
        if (getTotalReceitas() != null && loa.getValorDaReceita() != null) {
            restaTotal = getTotalReceitas();
            restaTotal = restaTotal.subtract(loa.getValorDaReceita());
        }
        return restaTotal;
    }

    public void removerReceitasLoa(ActionEvent evento) {
        ReceitaLOA r = (ReceitaLOA) evento.getComponent().getAttributes().get("objeto");
        ((LOA) selecionado).getReceitaLOAs().remove(r);
        receitaLOA.getReceitaLoaFontes().clear();
    }

    public void removerReceitasLoaFonte(ActionEvent evento) {
        ReceitaLOAFonte r = (ReceitaLOAFonte) evento.getComponent().getAttributes().get("objetos");
        ((LOA) selecionado).getReceitaLOAs().get(((LOA) selecionado).getReceitaLOAs().indexOf(receitaLOA)).getReceitaLoaFontes().remove(r);
    }

    public List<Conta> completaFonteParaReceitas(String parte) {
        return contaDeReceitaFacade.listaFiltrandoDestinacaoDeRecursos(parte.trim(), loa.getLdo().getExercicio());
    }

    public List<Conta> completaContaParaReceitas(String parte) {
        return contaDeReceitaFacade.listaFiltrandoReceita(parte.trim(), loa.getLdo().getExercicio());
    }

    public void validaCategoriaConta(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Conta c = (Conta) value;
        c = contaDeReceitaFacade.recuperar(c);
        if (c.getCategoria() != null) {
            if (c.getCategoria().equals(CategoriaConta.SINTETICA)) {
                message.setDetail("Conta Sintética. Não pode ser utilizada!");
                message.setSummary("Conta Sintética. Não pode ser utilizada!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    @URLAction(mappingId = "ver-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        operacao = Operacoes.EDITAR;
        filtroReceita = null;
        receitaLOA = new ReceitaLOA();
        receitaLOA.setPrevisaoReceitaOrc(new ArrayList<PrevisaoReceitaOrc>());
        contaReceita = new Conta();
        receitaLOAFonte = new ReceitaLOAFonte();
        LOA lo = loaFacade.recuperar(selecionado.getId());
        selecionado = lo;
        loa = lo;
        hierarquiaOrgResponsavel = new HierarquiaOrganizacional();
        codigoReceita = "";
        valorReceitaLOA = BigDecimal.ZERO;
        validaValorFracionadoTabela();
    }

    public HierarquiaOrganizacional getHierarquiaOrgResponsavel() {
        return hierarquiaOrgResponsavel;
    }

    public void setHierarquiaOrgResponsavel(HierarquiaOrganizacional hierarquiaOrgResponsavel) {
        this.hierarquiaOrgResponsavel = hierarquiaOrgResponsavel;
    }

    public void salvar() {
        LOA loa = ((LOA) selecionado);
        try {
            //if (verificaLancamentosPrevisoesReceitaOrc(loa.getReceitaLOAs())) {
            if (operacao == Operacoes.NOVO) {
                this.getFacade().salvarNovo(loa);
            } else {
                this.getFacade().salvar(loa);
            }
            redireciona();
//            lista = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
//            return "lista.xhtml";
//            } else {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", fraseErro));
////                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Verifique os lançamentos de Previsões de Receita Orçamentária"));
//                return "edita.xhtml";
//            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
//            return "edita.xhtml";
        }
    }

    public Boolean verificaLancamentosPrevisoesReceitaOrc(List<ReceitaLOA> receitas) {
        Boolean b = false;
        fraseErro = "";
        if (!receitas.isEmpty()) {
            for (ReceitaLOA rl : receitas) {
                if (rl.getReceitaLoaFontes().isEmpty()) {
                    fraseErro = "Receita LOA " + rl.getCodigoReduzido() + " não tem fonte vinculada!";
                    break;
                } else {
                    Boolean z = (calculaDiferenca(rl).compareTo(BigDecimal.ZERO) == 0);
                    if (calculaDiferenca(rl).compareTo(BigDecimal.ZERO) == 0) {
                        b = true;
                    }
//                    } else {
//                        fraseErro = "Verifique os lançamentos da Receita LOA " + rl.getCodigoReduzido();
//                        b = false;
//                        break;
//                    }
                }
            }
        } else {
            fraseErro = "Nenhuma previsão encontrada!";
        }
        return b;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void rouding(ActionEvent ae) {
        ReceitaLOAFonte receita = (ReceitaLOAFonte) ae.getComponent().getAttributes().get("objeto");
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.equals(receita)) {
                rlf.setRounding(Boolean.TRUE);
            } else {
                rlf.setRounding(Boolean.FALSE);
            }
        }
    }

    public BigDecimal getValorReceitaLOA() {
        return valorReceitaLOA;
    }

    public void setValorReceitaLOA(BigDecimal valorReceitaLOA) {
        this.valorReceitaLOA = valorReceitaLOA;
    }

    public String getCodigoReceita() {
        return codigoReceita;
    }

    public void setCodigoReceita(String codigoReceita) {
        this.codigoReceita = codigoReceita;
    }

    public String getFiltroReceita() {
        return filtroReceita;
    }

    public void setFiltroReceita(String filtroReceita) {
        this.filtroReceita = filtroReceita;
    }

    public List<ReceitaLOA> getListaDaTabela() {
        List<ReceitaLOA> retorno = new ArrayList<>();
        for (ReceitaLOA daVez : ((LOA) selecionado).getReceitaLOAs()) {
            if (filtroReceita == null
                    || filtroReceita.trim().isEmpty()
                    || daVez.getContaDeReceita().getCodigo().contains(filtroReceita.trim())
                    || daVez.getContaDeReceita().getDescricao().contains(filtroReceita.trim())
                    || daVez.getEntidade().getDescricao().contains(filtroReceita.trim())) {
                retorno.add(daVez);
            }
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-loa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
