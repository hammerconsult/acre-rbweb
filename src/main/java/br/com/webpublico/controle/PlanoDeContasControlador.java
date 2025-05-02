/*
 * Codigo gerado automaticamente em Tue Apr 19 13:57:30 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PlanoDeContasFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.util.ComponentUtils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ManagedBean(name = "planoDeContasControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "ver-plano-de-contas", pattern = "/plano-de-contas/ver/#{planoDeContasControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontas/visualizar.xhtml"),
        @URLMapping(id = "listar-plano-de-contas", pattern = "/plano-de-contas/listar/", viewId = "/faces/financeiro/planodecontas/planodecontas/lista.xhtml")
})
public class PlanoDeContasControlador extends PrettyControlador<PlanoDeContas> implements Serializable, CRUD {

    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    private ConverterGenerico converterTipoConta;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterConta;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private int tabAtual;
    private Conta conta;
    private ConverterGenerico converterCelulasDeInformacao;
    private String stringSetaFoco;
    private int nivelMascara;
    private int quantidadeDeDigitos;
    private String codigoAntes;
    private String codigo;
    private String codigoDepois;
    private String exercicio;
    //VARIAVEIS PARA DIALOG DE CONTA EQUIVALENTE
    private List<ContaEquivalente> contaEquivalentes;
    private ContaEquivalente contaEquivalente;
    private PlanoDeContas planoDeContas;
    private ConverterGenerico converterPlanos;
    private ConverterAutoComplete converterContaEquivalente;
    //VARIAVEIS UTILIZADO COMO FILTRO DO CODIGO DA CONTA
    private String codigoInicial;
    private String codigoFinal;

    public PlanoDeContasControlador() {
        super(PlanoDeContas.class);
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    @Override
    public AbstractFacade getFacede() {
        return planoDeContasFacade;
    }



    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }



    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }




    public Converter getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(FonteDeRecursos.class, planoDeContasFacade.getFonteDeRecursosFacade());
        }
        return converterFonteDeRecursos;
    }


    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, planoDeContasFacade.getContaFacade());
        }
        return converterConta;
    }



    public List<FonteDeRecursos> completaFonteDeRecursos(String parte) {
        return planoDeContasFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaExtraorcamentaria(String parte) {
        return planoDeContasFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaReceitaDivida(String parte) {
        return planoDeContasFacade.getContaFacade().listaFiltrandoReceitaDivida(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<Conta> completaContaContabil(String parte) {
        return planoDeContasFacade.getContaFacade().listaContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getTipoConta() != null) {
            for (PlanoDeContas object : planoDeContasFacade.recuperarPlanoPorTipoContaEExercicio(selecionado.getTipoConta(), selecionado.getExercicio())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTipoConta() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoConta tipoConta : planoDeContasFacade.getTipoContaFacade().lista()) {
            retorno.add(new SelectItem(tipoConta, tipoConta.getDescricao()));
        }
        return retorno;
    }





    public List<SelectItem> getNaturezaSaldo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaSaldo object : NaturezaSaldo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }



    public List<SelectItem> getNaturezaConta() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaConta object : NaturezaConta.values()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaInformacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaInformacao object : NaturezaInformacao.values()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSubSistema() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SubSistema object : SubSistema.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }



    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }


    public boolean isTipoContaContabil() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.CONTABIL)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaDestinacao() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.DESTINACAO)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaDespesa() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.DESPESA)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaReceita() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.RECEITA)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaAuxiliar() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.AUXILIAR)) {
            return true;
        }
        return false;
    }

    public boolean isTipoContaEXTRAORCAMENTARIA() {
        if (getClasseContaSelecionado().equals(ClasseDaConta.EXTRAORCAMENTARIA)) {
            return true;
        }
        return false;
    }

    public ClasseDaConta getClasseContaSelecionado() {
        if (selecionado != null) {
            if (selecionado.getTipoConta() != null) {
                return selecionado.getTipoConta().getClasseDaConta();
            }
        }
        return null;
    }

    public void redirecionarContas() {
        Web.poeNaSessao(this.selecionado);
        FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/");
    }


    public String redirecionarConta(Object o) {
        //System.out.println("Selecionado..: " + this.selecionado);
        Web.poeNaSessao(this.selecionado);
        //System.out.println("Id da Conta..: " + Persistencia.getId(o));
        return "/conta/" + Persistencia.getId(o) + "/";
    }



    public void instanciaContaMascara() {
        inicializaConta();
        recuperaCodigoAntesDepoisDaConta();
    }

    private void inicializaConta() {
        stringSetaFoco = "";
        if (isTipoContaContabil()) {
            tabAtual = 0;
            conta = new ContaContabil();
            stringSetaFoco = "codigoContaContabil";
        } else if (isTipoContaDespesa()) {
            tabAtual = 1;
            conta = new ContaDespesa();
            stringSetaFoco = "codigoContaDespesesa";
        } else if (isTipoContaDestinacao()) {
            tabAtual = 2;
            conta = new ContaDeDestinacao();
            stringSetaFoco = "codigoContaDestinacao";
        } else if (isTipoContaReceita()) {
            tabAtual = 3;
            conta = new ContaReceita();
            stringSetaFoco = "codigoContaReceita";
            ((ContaReceita) conta).setTiposCredito(TiposCredito.NAO_APLICAVEL);
        } else if (isTipoContaEXTRAORCAMENTARIA()) {
            tabAtual = 4;
            stringSetaFoco = "codigoContaExtra";
            conta = new ContaExtraorcamentaria();
        } else {
            conta = new ContaAuxiliar();
        }
        conta.setAtiva(Boolean.TRUE);
        conta.setPlanoDeContas(selecionado);
        conta.setDataRegistro(SistemaFacade.getDataCorrente());
        conta.setContasEquivalentes(new ArrayList<ContaEquivalente>());
        conta.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    public void adicionarConta() {
        boolean deuCerto = true;

        deuCerto = validaCodigoDescricaoPreenchido(deuCerto);
        if (!deuCerto) {
            return;
        }

        String codigoAntigo = tratamentosDoCodigo();

        setaCategoriaConta();

        if (!validacoesContaContabil()) {
            return;
        }

        if (podeAdicionarConta(conta)) {
            selecionado.setContas(Util.adicionarObjetoEmLista(selecionado.getContas(), conta));
            if (isTipoContaDespesa()) {
                alteraTipoContaDespesaFilhas(conta);
            }
            inicializaConta();
        } else {
            conta.setCodigo(codigoAntigo);
        }

        atualizaTabela();
        recuperaCodigoAntesDepoisDaConta();
        calculaQuantidadeDeDigitos();
    }

    public void recuperaCodigoAntesDepoisDaConta() {
        codigoAntes = "";
        codigoDepois = "";
        codigo = "";
        if (!isTipoContaEXTRAORCAMENTARIA()) {
            int nivelDoCodigoDaConta = 0;
            String codigo = null;
            if (conta.getSuperior() == null) {
                codigo = conta.getCodigo();
                nivelDoCodigoDaConta = conta.getNivel();
            } else {
                codigo = conta.getSuperior().getCodigo();
                nivelDoCodigoDaConta = conta.getSuperior().getNivel() + 1;
            }

            if (codigo.trim().isEmpty() || codigo == null) {
                String mascara = selecionado.getTipoConta().getMascara();
                int nivelDaMascara = 1;
                for (int i = 0; i < mascara.length(); i++) {

                    if (nivelDaMascara > 1) {
                        codigoDepois += mascara.substring(i, i + 1).replace("9", "0");
                    }
                    if (mascara.substring(i, i + 1).equals(".")) {
                        nivelDaMascara++;
                    }
                }
            } else {
                int nivel = 1;

                for (int i = 0; i < codigo.length(); i++) {
                    String letra = codigo.substring(i, i + 1);

                    if (nivel < (nivelDoCodigoDaConta)) {
                        codigoAntes += letra;
                    }

                    if (nivel > (nivelDoCodigoDaConta)) {
                        codigoDepois += letra;
                    }
                    if (letra.equals(".")) {
                        nivel++;
                    }
                }
            }
        }
    }

    private boolean podeAdicionarConta(Conta conta) {
        for (Conta conta1 : selecionado.getContas()) {
            if (!conta.equals(conta1)) {
                if ((conta1.getCodigo().equals(conta.getCodigo())) && !(conta1.getDescricao().toLowerCase().equals(conta.getDescricao().toLowerCase()))) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar Conta!", "Já foi adicionado uma conta com o código informado. Código.: " + conta.getCodigo()));
                    return false;
                }
            }
        }
        return true;
    }



    public void calculaQuantidadeDeDigitos() {
        int retorno = 0;
        if (this.conta.getSuperior() == null) {
            retorno = recuperaStringDoNivelDaConta(conta.getNivel(), conta).length();
        } else {
            retorno = recuperaStringDoNivelDaConta(conta.getSuperior().getNivel() + 1, conta.getSuperior()).length();
        }
        if (retorno == 0) {
            String mascara = selecionado.getTipoConta().getMascara();
            if (!mascara.contains(".")) {
                retorno = mascara.length();
            } else {
                int nivelDaMascara = 0;
                String codigo = "";
                for (int i = 0; i < mascara.length(); i++) {
                    if (mascara.substring(i, i + 1).equals(".")) {
                        nivelDaMascara++;
                    } else {
                        codigo += mascara.substring(i, i + 1);
                    }
                    if (nivelDaMascara == 1) {
                        break;
                    }
                }
                retorno = nivelDaMascara;
            }
        }
        quantidadeDeDigitos = retorno;
    }



    public Boolean podeRemoverContaSelecionada() {
        if (conta != null) {
            return podeRemoverConta(conta);
        }
        return false;
    }

    private Boolean podeRemoverConta(Conta conta) {
        for (Iterator it = selecionado.getContas().iterator(); it.hasNext(); ) {
            Conta c = (Conta) it.next();
            if (c.getSuperior() != null) {
                if (c.getSuperior().equals(conta)) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    public void recuperaNivelDaMascaraTipoConta() {
        String mascara = selecionado.getTipoConta().getMascara();
        int nivelDaMascara = 0;
        for (int i = 0; i < mascara.length(); i++) {
            if (mascara.substring(i, i + 1).equals(".")) {
                nivelDaMascara++;
            }
        }
        this.nivelMascara = nivelDaMascara + 1;
    }





    private static String recuperaStringDoNivelDaConta(int nivel, Conta conta) {
        return recuperaStringDoNivel(nivel, conta.getCodigo());
    }

    private static String recuperaStringDoNivel(int nivel, String codigo) {

        int nivelDoCodigo = 0;
        String retorno = "";
        for (int i = 0; i < codigo.length(); i++) {
            if (codigo.substring(i, i + 1).equals(".")) {
                nivelDoCodigo++;
            } else {
                if ((nivelDoCodigo + 1) == nivel) {
                    retorno += codigo.substring(i, i + 1);
                }
            }
            if (nivelDoCodigo == nivel) {
                break;
            }
        }
        return retorno;
    }

    private void atualizaTabela() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        DataTable table = (DataTable) ComponentUtils.findComponent(facesContext.getViewRoot(), "tabela");
        table.resetValue();
    }

    public SelectItem[] tiposCATEGORIA() {
        SelectItem[] opcoes = new SelectItem[3];
        opcoes[0] = new SelectItem("", "TODAS");
        opcoes[1] = new SelectItem(CategoriaConta.ANALITICA, CategoriaConta.ANALITICA.getDescricao());
        opcoes[2] = new SelectItem(CategoriaConta.SINTETICA, CategoriaConta.SINTETICA.getDescricao());
        return opcoes;
    }

    public SelectItem[] tiposContaReceitasView() {
        SelectItem[] opcoes = new SelectItem[TiposCredito.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TiposCredito tipo : TiposCredito.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }



    public SelectItem[] tiposContaDespesaView() {
        SelectItem[] opcoes = new SelectItem[TipoContaDespesa.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoContaDespesa tipo : TipoContaDespesa.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposContaExtraView() {
        SelectItem[] opcoes = new SelectItem[TipoContaExtraorcamentaria.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoContaExtraorcamentaria tipo : TipoContaExtraorcamentaria.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposContaDestinacaoView() {
        SelectItem[] opcoes = new SelectItem[TipoDestinacaoRecurso.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoDestinacaoRecurso tipo : TipoDestinacaoRecurso.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposNaturezaSaldoView() {
        SelectItem[] opcoes = new SelectItem[NaturezaSaldo.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (NaturezaSaldo tipo : NaturezaSaldo.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposNaturezaContaView() {
        SelectItem[] opcoes = new SelectItem[NaturezaConta.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (NaturezaConta tipo : NaturezaConta.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposNaturezaInformacaoView() {
        SelectItem[] opcoes = new SelectItem[NaturezaInformacao.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (NaturezaInformacao tipo : NaturezaInformacao.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposSubSistemaView() {
        SelectItem[] opcoes = new SelectItem[SubSistema.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (SubSistema tipo : SubSistema.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.getDescricao());
            i++;
        }
        return opcoes;
    }

    private void setaFoco() {
        RequestContext.getCurrentInstance().execute("setaFoco('Formulario:tabView:" + stringSetaFoco + "');");
    }




    private boolean validaCodigoDescricaoPreenchido(boolean deuCerto) {
        if (podeRemoverContaSelecionada() && podeInformarCodigoContaDeDestinacao()) {
            if (isTipoContaEXTRAORCAMENTARIA()) {
                this.codigo = this.conta.getCodigo();
            }
            if (this.codigo.isEmpty() || this.codigo == null) {
                deuCerto = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar Conta!", "O campo código é obrigatório."));
            } else {
                if (!isTipoContaEXTRAORCAMENTARIA()) {
                    if (this.codigo.length() != quantidadeDeDigitos) {
                        deuCerto = false;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar Conta!", "Informe " + quantidadeDeDigitos + " digitos para adicionar a conta, obedecendo a máscara."));
                    }
                }
            }
        }
        if (conta.getDescricao().trim() == null || conta.getDescricao().trim().isEmpty()) {
            deuCerto = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar adicionar Conta!", "O campo descrição é obrigatório."));
        }
        return deuCerto;
    }

    private void alteraTipoContaDespesaFilhas(Conta superior) {
        if (getAsContaDespesa(superior).getTipoContaDespesa() == null) {
            return;
        }

        String codigo = superior.getCodigoSemZerosAoFinal();
        for (Conta conta1 : selecionado.getContas()) {
            if (conta1.getCodigo().startsWith(codigo) && !conta1.equals(superior)) {
                if (getAsContaDespesa(conta1).getTipoContaDespesa() == null) {
                    getAsContaDespesa(conta1).setTipoContaDespesa(getAsContaDespesa(superior).getTipoContaDespesa());
                }
            }
        }
    }

    private ContaDespesa getAsContaDespesa(Conta c) {
        return (ContaDespesa) c;
    }

    private String retiraPontoSeForUltimoNivelDaMascara(String codigo) {
        if (codigo.endsWith(".")) {
            return codigo.substring(0, codigo.length() - 1);
        }
        return codigo;
    }

    public boolean mostraFonteRecurso() {
        try {
            if (conta.getSuperior() != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean mostraDetalhamentoFonteRecurso() {
        try {
            if (isTipoContaDestinacao() && conta.getSuperior() != null) {
                ContaDeDestinacao contaDestinacao = (ContaDeDestinacao) conta.getSuperior();
                if (contaDestinacao.getFonteDeRecursos().getId() != null) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean podeInformarCodigoContaDeDestinacao() {
        try {
            if (isTipoContaDestinacao()) {
                if (mostraDetalhamentoFonteRecurso() || mostraFonteRecurso()) {
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }


    public boolean renderizaTabView() {
        if (selecionado.getTipoConta() != null) {
            if (conta instanceof ContaAuxiliar) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }
        Integer ano = Integer.parseInt(value.toString());
        FacesMessage message = new FacesMessage();
        List<Exercicio> exer = planoDeContasFacade.getExercicioFacade().lista();
        Exercicio ex = null;
        for (Exercicio e : exer) {
            if (e.getAno() != null) {
                if (ano.compareTo(e.getAno()) == 0) {
                    ex = e;
                    selecionado.setExercicio(ex);
                }
            }
        }
        if (ex == null) {
            selecionado.setExercicio(null);
            message.setDetail("Exercício Inválido!");
            message.setSummary("Exercício Inválido!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public List<Conta> getContas() {
        List<Conta> retorno = new ArrayList<Conta>();
        retorno.addAll(selecionado.getContas());
        if (!codigoInicial.trim().isEmpty() || !codigoFinal.trim().isEmpty()) {
            retorno.clear();
            Long numeroCodigoInicial = retornaNumeroCodigo(retiraZerosAEsquerda(codigoInicial));
            Long numeroCodigoFinal = retornaNumeroCodigo(retiraZerosAEsquerda(codigoFinal));
            int nivelDoCodigo = retornaNivelCodigoInicialFinal(retiraZerosAEsquerda(codigoInicial), retiraZerosAEsquerda(codigoFinal));

            for (Conta contaDaVez : selecionado.getContas()) {
                if (contaDaVez.getNivel() == nivelDoCodigo) {
                    Long numeroDaContaDaVez = retornaNumeroCodigo(contaDaVez.getCodigoSemZerosAoFinal());
                    if (numeroDaContaDaVez <= numeroCodigoFinal && numeroCodigoInicial == 0l) {
                        retorno = Util.adicionarObjetoEmLista(retorno, contaDaVez);
                    } else if (numeroDaContaDaVez >= numeroCodigoInicial && numeroCodigoFinal == 0l) {
                        retorno = Util.adicionarObjetoEmLista(retorno, contaDaVez);
                    } else if (numeroDaContaDaVez >= numeroCodigoInicial && numeroDaContaDaVez <= numeroCodigoFinal) {
                        retorno = Util.adicionarObjetoEmLista(retorno, contaDaVez);
                    }
                }
            }
        }
        return retorno;
    }

    private String retiraZerosAEsquerda(String codigo) {
        String toReturn = "";
        toReturn = codigo;
        int zeroAPartirDe = toReturn.length();
        for (int i = toReturn.length() - 1; i >= 0; i--) {
            if (toReturn.substring(i, i + 1).equals(".")) {
                zeroAPartirDe = i;
            } else if (!toReturn.substring(i, i + 1).equalsIgnoreCase("0")) {
                return toReturn.substring(0, zeroAPartirDe);
            }
        }
        return toReturn;
    }

    private int calculaNIvel(String codigo) {
        String[] partes = codigo.split("\\.");
        int cont = 0;
        int nivelTemp = 0;
        for (int x = 0; x < partes.length; x++) {
            if (partes[x].replace("0", "").length() > 0) {
                nivelTemp = x;
                cont++;
            }
        }
        if ((nivelTemp + 1) > cont) {
            return nivelTemp + 1;
        } else {
            return cont;
        }
    }

    public int retornaNivelCodigoInicialFinal(String codigoInicial, String codigoFinal) {
        try {
            int nivelCodigoInicial = calculaNIvel(codigoInicial);
            int nivelCodigoFinal = calculaNIvel(codigoFinal);
            if (nivelCodigoFinal < nivelCodigoInicial) {
                return nivelCodigoInicial;
            }
            return nivelCodigoFinal;
        } catch (Exception e) {
            return 1;
        }
    }

    public Long retornaNumeroCodigo(String codigo) {
        try {
            if (codigo.endsWith("0")) {
                codigo = codigo.substring(0, codigo.length() - 2);
            }
            return Long.parseLong(codigo.replace(".", ""));
        } catch (Exception e) {
            return 0l;
        }
    }

    public void copiaCodigoInicialProFinal() {
        codigoFinal = codigoInicial;
    }

    private String tratamentosDoCodigo() {
        String codigoAntigo = null;
        if (podeRemoverContaSelecionada()) {
            String codigo = codigoAntes + this.codigo + "." + codigoDepois;
            if (isTipoContaDestinacao()) {
                if (!conta.getCodigo().isEmpty()) {
                    codigo = conta.getCodigo();
                }
            }
            codigo = retiraPontoSeForUltimoNivelDaMascara(codigo);
            codigoAntigo = conta.getCodigo();
            conta.setCodigo(codigo);
        } else {
            codigoAntigo = conta.getCodigo();
        }
        return codigoAntigo;
    }

    private boolean validacoesContaContabil() {
        if (isTipoContaContabil()) {
            ContaContabil c = (ContaContabil) conta;
            if (c.getNaturezaInformacao() == null) {
                return true;
            }
            if (c.getSubSistema() == null) {
                return true;
            }
            if (c.getNaturezaInformacao().equals(NaturezaInformacao.PATRIMONIAL)) {
                if (!(c.getSubSistema().equals(SubSistema.FINANCEIRO) || c.getSubSistema().equals(SubSistema.PERMANENTE))) {
                    FacesUtil.addError("Não foi possível adicionar a Conta : " + this.conta + ".", "Quando informado a Natureza da Informação com PATRIMONIAL, o Sub-Sistema só é permitido com " + SubSistema.FINANCEIRO.getDescricao() + " ou " + SubSistema.PERMANENTE.getDescricao() + ".");
                    return false;
                }
            }
            if (c.getNaturezaInformacao().equals(NaturezaInformacao.ORCAMENTARIO) || c.getNaturezaInformacao().equals(NaturezaInformacao.CONTROLE)) {
                if (!(c.getSubSistema().equals(SubSistema.ORCAMENTARIO) || c.getSubSistema().equals(SubSistema.COMPENSADO))) {
                    FacesUtil.addError("Não foi possível adicionar a Conta : " + this.conta + ".", "Quando informado a Natureza da Informação com " + c.getNaturezaInformacao() + ", o Sub-Sistema só é permitido com " + SubSistema.ORCAMENTARIO.getDescricao() + " ou " + SubSistema.COMPENSADO.getDescricao() + ".");
                    return false;
                }
            }
        }
        return true;
    }

    private void setaCategoriaConta() {


        if (conta.getPermitirDesdobramento()) {
            conta.setCategoria(CategoriaConta.SINTETICA);
        } else {
            conta.setCategoria(CategoriaConta.ANALITICA);
        }
    }


    @URLAction(mappingId = "ver-plano-de-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/plano-de-contas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recuperarEditarVer() {
        selecionado = planoDeContasFacade.recuperarContas(super.getId());
        instanciaContaMascara();
        recuperaNivelDaMascaraTipoConta();
        if (selecionado.getExercicio() != null) {
            exercicio = String.valueOf(selecionado.getExercicio().getAno());
        } else {
            exercicio = "";
        }
        codigoInicial = "";
        codigoFinal = "";
    }
}
