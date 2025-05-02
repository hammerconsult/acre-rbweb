/*
 * Codigo gerado automaticamente em Thu May 05 14:21:20 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.MoneyConverter;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class ProvisaoPPADespesaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    private ProvisaoPPADespesa provisaoPPADespesaSelecionado;
    private ProvisaoPPA provisaoPPASelecionado;
    private List<ProvisaoPPADespesa> listaProvisaoDespesa;
    private List<ProvisaoPPADespesa> listaExcluidos;
    private List<ProvisaoPPAFonte> listaNovos;
    private List<ProvisaoPPAFonte> listaFontesExcluidas;
    @EJB
    private ContaFacade contaDeDespesaFacade;
    private ConverterAutoComplete converterContaDeDespesa;
    private BigDecimal soma;
    private List<ProvisaoPPA> provisaoPPAs;
    private String mensagem;
    private BigDecimal falta;
    private MoneyConverter moneyConverter;
    private Long codigoSoma;
    private ConverterAutoComplete converterProvisaoPPADespesa, converterFonteDeRecursos;
    private ProvisaoPPAFonte provisaoPPAFonte;
    private ProvisaoPPADespesa provAux;
    @EJB
    private PPAFacade ppaFacade;
    private ConverterGenerico converterPPA;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private LDOFacade lDOFacade;
    private List<ProvisaoPPADespesa> listaProvDespLoa;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Integer intervalo;
    private LDO ldo;
    private ConverterAutoComplete converterUnidade;
    private List<ProvisaoPPA> provisoesPPA;
    private HierarquiaOrganizacional hoSelecionada;

    @Override
    public void novo() {
        codigoSoma = 0l;
        listaProvDespLoa = new ArrayList<ProvisaoPPADespesa>();
        provisaoPPAFonte = new ProvisaoPPAFonte();
        provAux = new ProvisaoPPADespesa();
        provAux.setProvisaoPPAFontes(new ArrayList<ProvisaoPPAFonte>());
        listaNovos = new ArrayList<ProvisaoPPAFonte>();
        hoSelecionada = new HierarquiaOrganizacional();
        provisaoPPASelecionado = new ProvisaoPPA();
    }

    public ProvisaoPPADespesaControlador() {
        metadata = new EntidadeMetaData(ProvisaoPPADespesa.class);

    }

    public String vaiProEdita() {
        return "edita.xhtml";
    }

    public ProvisaoPPADespesaFacade getFacade() {
        return provisaoPPADespesaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return provisaoPPADespesaFacade;
    }

    public ProvisaoPPAFonte getProvisaoPPAFonte() {
        return provisaoPPAFonte;
    }

    public void setProvisaoPPAFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        this.provisaoPPAFonte = provisaoPPAFonte;
    }

    //    public List<SelectItem> getContaDeDespesas() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
//        if (provisaoPPASelecionado != null) {
//            for (Conta object : contaDeDespesaFacade.listaContaDespesa((provisaoPPASelecionado.getExercicio()))) {
//                toReturn.add(new SelectItem(object, object.getDescricao()));
//            }
//        }
//        return toReturn;
//    }
    public List<SelectItem> getListaUnidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (provisaoPPASelecionado != null) {
            for (UnidadeOrganizacional uni : provisaoPPADespesaFacade.getUnidadeOrganizacionalFacade().listaUnidadesUsuarioCorrenteNivel3(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao())) {
                toReturn.add(new SelectItem(uni, uni.toString()));
            }
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterUnidade() {
        if (converterUnidade == null) {
            converterUnidade = new ConverterAutoComplete(UnidadeOrganizacional.class, provisaoPPADespesaFacade.getUnidadeOrganizacionalFacade());
        }
        return converterUnidade;
    }

    public List<Conta> completaContas(String parte) {
        try {
            List<Conta> contas = contaDeDespesaFacade.listaFiltrandoDespesaCriteria(parte.trim(), provisaoPPASelecionado.getExercicio());
            if (contas.isEmpty()) {
                return new ArrayList<Conta>();
            } else {
                return contas;
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " "));
            return new ArrayList<Conta>();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), " "));
            return new ArrayList<Conta>();
        }
    }

    public Converter getConverterContaDeDespesa() {
        if (converterContaDeDespesa == null) {
            converterContaDeDespesa = new ConverterAutoComplete(Conta.class, contaDeDespesaFacade);
        }
        return converterContaDeDespesa;
    }

    public List<SelectItem> getTipoDespesaOrc() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        if (getVerificaProvisaoDeLoaEfetivada()) {
        //System.out.println("entro no if");
        for (TipoDespesaORC object : TipoDespesaORC.values()) {
            if (object == TipoDespesaORC.ORCAMENTARIA) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
//        }
        return toReturn;
    }

    public List<SelectItem> getListaEsferaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaOrcamentaria eo : EsferaOrcamentaria.values()) {
            if (eo != EsferaOrcamentaria.ORCAMENTOGERAL) {
                toReturn.add(new SelectItem(eo, eo.getDescricao()));
            }
        }
        return toReturn;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setConverterContaDeDespesa(ConverterAutoComplete converterContaDeDespesa) {
        this.converterContaDeDespesa = converterContaDeDespesa;
    }

    public List<ProvisaoPPADespesa> getListaProvisaoDespesa() {
        return listaProvisaoDespesa;
    }

    public List<ProvisaoPPADespesa> getListaExcluidos() {
        return listaExcluidos;
    }

    public void setListaExcluidos(List<ProvisaoPPADespesa> listaExcluidos) {
        this.listaExcluidos = listaExcluidos;
    }

    public void setListaProvisaoDespesa(List<ProvisaoPPADespesa> listaProvisaoDespesa) {
        this.listaProvisaoDespesa = listaProvisaoDespesa;
    }

    public void recuperaLista() {
        provisoesPPA = getProvisaoPPAs();
    }

    public List<ProvisaoPPA> getProvisaoPPAs() {
        ldo = lDOFacade.listaVigenteNoExercicio(sistemaControlador.getExercicioCorrente());
        if (ldo != null) {
            List<ProvisaoPPA> listona = new ArrayList<ProvisaoPPA>();
            for (ProvisaoPPA pp : provisaoPPADespesaFacade.listaPorLdo(ldo)) {
                pp = provisaoPPADespesaFacade.getProvisaoPPAFacade().recuperar(pp.getId());
                listona.add(pp);
            }
            return listona;
        } else {
            return new ArrayList<ProvisaoPPA>();
        }
    }

    public void setProvisaoPPAs(List<ProvisaoPPA> provisaoPPAs) {
        this.provisaoPPAs = provisaoPPAs;
    }

    public ProvisaoPPA getProvisaoPPASelecionado() {
        return provisaoPPASelecionado;
    }

    public void setProvisaoPPASelecionado(ProvisaoPPA provisaoPPASelecionado) {
        this.provisaoPPASelecionado = provisaoPPASelecionado;
    }

    public ProvisaoPPADespesa getProvisaoPPADespesaSelecionado() {
        return provisaoPPADespesaSelecionado;
    }

    public void setProvisaoPPADespesaSelecionado(ProvisaoPPADespesa provisaoPPADespesaSelecionado) {
        this.provisaoPPADespesaSelecionado = provisaoPPADespesaSelecionado;
    }

    public BigDecimal getFalta() {
        falta = provisaoPPASelecionado.getMetaFinanceira().subtract(getSomaDespesa());
        return falta;
    }

    public void setFalta(BigDecimal falta) {
        this.falta = falta;
    }

    public BigDecimal getSoma() {
        return soma;
    }

    public void salvarDespesa() {
        boolean teste = true;
        if (!provisaoPPADespesaFacade.validaCodigoReduzido(sistemaControlador.getExercicioCorrente(), provisaoPPADespesaSelecionado.getCodigo()).isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O código ja existe para esse exercício", ""));
            teste = false;
        }
        if (provisaoPPADespesaSelecionado.getContaDeDespesa() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O Campo Conta de Depesa é obrigatório", ""));
            teste = false;
        }
        if (provisaoPPADespesaSelecionado.getUnidadeOrganizacional() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O Campo Unidade organizacional é obrigatório", ""));
            teste = false;
        }

        for (ProvisaoPPADespesa p : getListaProvisaoDespesa()) {
            provisaoPPADespesaSelecionado.setDataRegistro(new Date());
            if (provisaoPPADespesaSelecionado.getContaDeDespesa() != null) {
                if (provisaoPPADespesaSelecionado.getContaDeDespesa().equalsID(p.getContaDeDespesa())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "A Conta de Despesa ja foi Lançada!"));
                    teste = false;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "Selecione uma Conta"));
                teste = false;
            }
        }
        if (teste) {
            soma = soma.add(provisaoPPADespesaSelecionado.getValor());
            if (soma.compareTo(provisaoPPASelecionado.getMetaFinanceira()) <= 0) {
                if (!getVerificaProvisaoDeLoaEfetivada()) {
                    provisaoPPADespesaSelecionado.setTipoDespesaORC(TipoDespesaORC.ORCAMENTARIA);
                } else {
                    listaProvDespLoa.add(provisaoPPADespesaSelecionado);
                }

                listaProvisaoDespesa.add(provisaoPPADespesaSelecionado);
                provisaoPPADespesaSelecionado = new ProvisaoPPADespesa();
                provisaoPPADespesaSelecionado.setValor(new BigDecimal(BigInteger.ZERO));
                for (ProvisaoPPADespesa p : listaProvisaoDespesa) {
                    long l = Long.parseLong(p.getCodigo());
                    if (l > codigoSoma) {
                        codigoSoma = l;
                    }
                }
                provisaoPPADespesaSelecionado.setCodigo(String.valueOf(codigoSoma + 1));
                //ARRUMAR PLANEJAMENTO - TODO
//                provisaoPPADespesaSelecionado.setProvisaoPPA(provisaoPPASelecionado);
            } else if (soma.compareTo(provisaoPPASelecionado.getMetaFinanceira()) == 1) {
                soma = soma.subtract(provisaoPPADespesaSelecionado.getValor());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "Os valores de despesas são maiores que a provisão!"));
            }

        }
    }

    public BigDecimal recuperaValorHierarquia(UnidadeOrganizacional uni) {
        BigDecimal valor = BigDecimal.ZERO;
        HierarquiaOrganizacional hie = new HierarquiaOrganizacional();
        UnidadeOrganizacional unii = new UnidadeOrganizacional();
        hie.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ORCAMENTARIA);
//        hie = provisaoPPADespesaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(unii, hie, sistemaControlador.getExercicioCorrente());
        unii = hie.getSubordinada();
        if (hie != null) {
            for (PrevisaoHOContaDestinacao prev : unii.getPrevisaoHOContaDestinacao()) {
                valor = prev.getValor();
            }
//            valor = hie.getValorEstimado();
        }
        return valor;
    }

    public void removerProvisao(ActionEvent evento) {
        ProvisaoPPADespesa provisao = (ProvisaoPPADespesa) evento.getComponent().getAttributes().get("removeProvisoes");
        soma = soma.subtract(provisao.getValor());
        listaExcluidos.add(provisao);
        listaProvisaoDespesa.remove(provisao);
    }

    public void removerProvisaoFonte(ActionEvent evento) {
        ProvisaoPPAFonte provisao = (ProvisaoPPAFonte) evento.getComponent().getAttributes().get("provFonte");
        provAux.getProvisaoPPAFontes().remove(provisao);
        provAux.setValor(getSomaTotal());
        int i = listaProvisaoDespesa.indexOf(provAux);
        listaProvisaoDespesa.set(i, provAux);
        listaFontesExcluidas.add(provisao);
    }

    @Override
    public String salvar() {
        try {
            //(ProvisaoPPA) selecionado
//        if (provisaoPPASelecionado.getSomenteLeitura() == true) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Esta Provisão PPA Despesa não pode sofrer modificações!"));
//            return "edita";
//        } else {
            for (ProvisaoPPADespesa pro : listaExcluidos) {
                provisaoPPADespesaFacade.remover(pro);
            }
            listaProvisaoDespesa.removeAll(listaProvDespLoa);
            for (ProvisaoPPADespesa prod : listaProvisaoDespesa) {
                if (prod.getValor() != null) {
                    selecionado = prod;
                    provisaoPPADespesaFacade.salvarRetornando(prod);
                }
            }
            List<ProvisaoPPADespesa> l = new ArrayList<ProvisaoPPADespesa>();
            for (ProvisaoPPADespesa prod : listaProvDespLoa) {
                if (prod.getValor() != null) {
                    selecionado = prod;
                    l.add(provisaoPPADespesaFacade.salvarRetornando(prod));
                }
            }

            LOA loa = provisaoPPAFacade.getLoaFacade().listaUltimaLoaPorExercicio(sistemaControlador.getExercicioCorrente());

            if (!provisaoPPAFacade.validaEfetivacaoLoa(sistemaControlador.getExercicioCorrente()).isEmpty()) {
                loaFacade.efetuaDespesa(loa, l, OperacaoORC.DOTACAO, null);
            }
            return "lista.xhtml";
        } catch (Exception e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new ExcecaoNegocioGenerica("Erro de restrição ao salvar", e.getCause());
            } else {
                throw new ExcecaoNegocioGenerica("Erro ao salvar", e.getCause());
            }
        }
    }
//    }

    @Override
    public String caminho() {
        return "lista.xhtml";
    }

    public void limpaCamposProvisaoPPAFonte() {
        provisaoPPAFonte.setDestinacaoDeRecursos(null);
        provisaoPPAFonte.setEsferaOrcamentaria(null);
    }

    public void setaProvisaoFonte(ActionEvent e) {
        ProvisaoPPADespesa p = (ProvisaoPPADespesa) e.getComponent().getAttributes().get("objeto");
        if (p.getId() != null) {
            provAux = provisaoPPADespesaFacade.recuperar(p);
        } else {
            p.setProvisaoPPAFontes(new ArrayList<ProvisaoPPAFonte>());
            provAux = p;
        }
        provAux.getProvisaoPPAFontes().addAll(retornaNovos(provAux));
        provAux.getProvisaoPPAFontes().removeAll(retornaExcluidos(provAux));
        provisaoPPAFonte.setProvisaoPPADespesa(provAux);
        if (!provisaoPPAFacade.validaEfetivacaoLoa(sistemaControlador.getExercicioCorrente()).isEmpty()) {
            provisaoPPAFonte.setValor(new BigDecimal(BigInteger.ZERO));
        }
        limpaCamposProvisaoPPAFonte();
    }

    public Boolean validaFonteRecurso() {
        Boolean controle = Boolean.TRUE;
        //Valioda a se a fonte ja foi lançada.
        for (ProvisaoPPAFonte pf : provAux.getProvisaoPPAFontes()) {
            if (provisaoPPAFonte.getDestinacaoDeRecursos().equals(pf.getDestinacaoDeRecursos())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "Esta Fonte de Recursos já foi Lançada!"));
                controle = Boolean.FALSE;
            }
        }
        //Valida valores da Unidade por fonte.

        List<PrevisaoHOContaDestinacao> listaPrevisao = provisaoPPADespesaFacade.getHierarquiaOrganizacionalFacade().listaPrevisoesPorUnidade(provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional(), sistemaControlador.getExercicioCorrente());
        if (!listaPrevisao.isEmpty()) {
            if (!provisaoPPADespesaFacade.getHierarquiaOrganizacionalFacade().validaSeAFonteExiste(provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional(), provisaoPPAFonte.getDestinacaoDeRecursos(), sistemaControlador.getExercicioCorrente())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "Não existe lançamento para a Fonte de Recurso " + provisaoPPAFonte.getDestinacaoDeRecursos()
                        + " na unidade " + provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional()
                        + " para esse exercício"));
                controle = Boolean.FALSE;
            } else {
                //ARRUMAR PLANEJAMENTO - TODO
//                Exercicio ex = provisaoPPAFonte.getProvisaoPPADespesa().getProvisaoPPA().getExercicio();
                Exercicio ex = null;
                ContaDeDestinacao conta = (ContaDeDestinacao) provisaoPPAFonte.getDestinacaoDeRecursos();
                UnidadeOrganizacional uni = provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional();


                BigDecimal valorPrevisao = BigDecimal.ZERO;
                BigDecimal valorFonte = BigDecimal.ZERO;
                valorPrevisao = provisaoPPADespesaFacade.retornaValorDasPrevisoesPorContaUnidadeExerc(ex, conta, uni);
                valorFonte = provisaoPPADespesaFacade.retornaValorFontesPorExercicioUnidadeConta(ex, conta, uni);
                BigDecimal somaValorOjetos = BigDecimal.ZERO;
                for (ProvisaoPPAFonte pp : provAux.getProvisaoPPAFontes()) {
                    if (pp.getId() == null && pp.getDestinacaoDeRecursos().equals(conta)) {
                        somaValorOjetos = somaValorOjetos.add(pp.getValor());
                    }
                }
                valorFonte = valorFonte.add(somaValorOjetos).add(provisaoPPAFonte.getValor());
                if (valorFonte.compareTo(valorPrevisao) > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "O Valor não pode ultrapassar o estipulado para essa fonte no cadastro da unidade"));
                    controle = Boolean.FALSE;
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, "Não foi encontrada nenhuma Fonte de Recurso lançada para a unidade " + provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional() + " para esse exercício"));
            controle = Boolean.FALSE;
        }
        return controle;
    }

    public void addProvisaoFonte() {
        if (provisaoPPAFonte.getDestinacaoDeRecursos() != null) {
//            BigDecimal valorDespesa = getValorPorUnidade(provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional());
//            BigDecimal restante = valorHie.subtract(valorDespesa.subtract(provisaoPPAFonte.getValor()));
            if (validaFonteRecurso()) {
                provAux.getProvisaoPPAFontes().add(provisaoPPAFonte);
                provAux.setValor(getSomaTotal());
                int i = listaProvisaoDespesa.indexOf(provAux);
                listaProvisaoDespesa.set(i, provAux);
                listaNovos.add(provisaoPPAFonte);
                provisaoPPAFonte = new ProvisaoPPAFonte();
                provisaoPPAFonte.setProvisaoPPADespesa(provAux);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O Campo Fonte de Recurso é obrigatório", ""));
        }
    }

    public BigDecimal getSomaTotal() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ProvisaoPPAFonte pro : provAux.getProvisaoPPAFontes()) {
            total = total.add(pro.getValor());
        }
        return total;
    }

    public BigDecimal getValorPorFonte() {
        BigDecimal valorFonte = BigDecimal.ZERO;
        for (ProvisaoPPAFonte ppaF : getListaProvisoesFonte()) {
            valorFonte = ppaF.getValor();
        }
        return valorFonte;
    }

    public BigDecimal getValorPorUnidade(UnidadeOrganizacional uni) {
        BigDecimal valor = provisaoPPADespesaFacade.retornaValorDespesas(sistemaControlador.getExercicioCorrente(), provisaoPPASelecionado, uni);
        BigDecimal totalMemo = BigDecimal.ZERO;
        for (ProvisaoPPADespesa pro : listaProvisaoDespesa) {
            if (pro.getUnidadeOrganizacional() == uni) {
                totalMemo = totalMemo.add(pro.getValor());
            }
        }
        valor = valor.add(totalMemo.add(provisaoPPAFonte.getValor()));
        return valor;
    }

    public BigDecimal getSomaDespesa() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ProvisaoPPADespesa pro : listaProvisaoDespesa) {
            total = total.add(pro.getValor());
        }
        return total;
    }

    public List<ProvisaoPPAFonte> retornaNovos(ProvisaoPPADespesa p) {
        List<ProvisaoPPAFonte> novos = new ArrayList<ProvisaoPPAFonte>();
        for (ProvisaoPPAFonte pro : listaNovos) {
            if (pro.getProvisaoPPADespesa().equals(p)) {
                novos.add(pro);
            }
        }
        return novos;
    }

    public List<ProvisaoPPAFonte> retornaExcluidos(ProvisaoPPADespesa p) {
        List<ProvisaoPPAFonte> excluidos = new ArrayList<ProvisaoPPAFonte>();
        for (ProvisaoPPAFonte pro : listaFontesExcluidas) {
            if (pro.getProvisaoPPADespesa().equals(p)) {
                excluidos.add(pro);
            }
        }
        return excluidos;
    }

    public Converter getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(Conta.class, contaDeDespesaFacade);
        }
        return converterFonteDeRecursos;
    }

    public List<Conta> completaFonteDeRecursos(String parte) {
        List<Conta> retorno = new ArrayList<Conta>();
        //ARRUMAR PLANEJAMENTO - TODO
        List<Conta> listaConta = contaDeDespesaFacade.listaFiltrandoDestinacaoRecursos(parte.trim(), provAux.getSubAcaoPPA().getExercicio());
        for (Conta co : listaConta) {
            retorno.add(co);
        }
        return retorno;
    }

    public List<ProvisaoPPAFonte> getListaProvisoesFonte() {
        return provAux.getProvisaoPPAFontes();

    }

    public Converter getConverterProvisaoPPADespesa() {
        if (converterProvisaoPPADespesa == null) {
            converterProvisaoPPADespesa = new ConverterAutoComplete(ProvisaoPPADespesa.class, provisaoPPADespesaFacade);
        }
        return converterProvisaoPPADespesa;
    }

    public List<ProvisaoPPADespesa> completaProvisaoDespesa(String parte) {
        return provisaoPPADespesaFacade.listaFiltrando(parte.trim(), "codigo");
    }

    //    public Boolean verificaSomenteLeitura() {
//        if (provisaoPPASelecionado.getSomenteLeitura()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
    @Override
    public void selecionar(ActionEvent evento) {
        listaNovos = new ArrayList<ProvisaoPPAFonte>();
        listaFontesExcluidas = new ArrayList<ProvisaoPPAFonte>();
        provisaoPPAFonte = new ProvisaoPPAFonte();
        provAux = new ProvisaoPPADespesa();
        provAux.setProvisaoPPAFontes(new ArrayList<ProvisaoPPAFonte>());
        codigoSoma = 0l;
        provisaoPPASelecionado = (ProvisaoPPA) evento.getComponent().getAttributes().get("objeto");
        listaProvDespLoa = new ArrayList<ProvisaoPPADespesa>();
        provisaoPPADespesaSelecionado = new ProvisaoPPADespesa();
        provisaoPPADespesaSelecionado.setValor(new BigDecimal(BigInteger.ZERO));
        //ARRUMAR PLANEJAMENTO - TODO
//        String cd = provisaoPPADespesaFacade.getCodigo(provisaoPPASelecionado.getExercicio(), provisaoPPASelecionado);
//        provisaoPPADespesaSelecionado.setCodigo(cd);
        //ARRUMAR PLANEJAMENTO - TODO
//        provisaoPPADespesaSelecionado.setProvisaoPPA(provisaoPPASelecionado);
        if (getVerificaProvisaoDeLoaEfetivada()) {
            provisaoPPADespesaSelecionado.setTipoDespesaORC(TipoDespesaORC.ORCAMENTARIA);
        }
        //ARRUMAR PLANEJAMENTO - TODO
//        listaProvisaoDespesa = provisaoPPADespesaFacade.listaProvisaoDispesaPPA(provisaoPPASelecionado);
        listaExcluidos = new ArrayList<ProvisaoPPADespesa>();
        soma = new BigDecimal("0.0");
        if (listaProvisaoDespesa != null) {
            for (ProvisaoPPADespesa pp : listaProvisaoDespesa) {
                soma = soma.add(pp.getValor());
            }
        }

    }

    public void calcularPorIntervalo() {
        try {
            provisaoPPADespesaFacade.recalculaCodigoReduzido(ldo, sistemaControlador.getExercicioCorrente(), intervalo);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Recálculo feito com sucesso!", "Recálculo feito com sucesso!"));
        } catch (ExcecaoNegocioGenerica ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), " "));
        }
    }

    public Boolean getVerificaProvisaoDeLoaEfetivada() {
        if (!provisaoPPAFacade.validaEfetivacaoLoa(sistemaControlador.getExercicioCorrente()).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public List<ProvisaoPPA> getProvisoesPPA() {
        return provisoesPPA;
    }

    public void setProvisoesPPA(List<ProvisaoPPA> provisoesPPA) {
        this.provisoesPPA = provisoesPPA;
    }
}
