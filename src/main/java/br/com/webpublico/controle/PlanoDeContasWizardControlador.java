package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PlanoDeContasFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: Romanini Date: 03/07/13 Time: 14:50 To
 * change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "planoDeContasWizardControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/novo/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/edita.xhtml"),
    @URLMapping(id = "editar-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/editar/#{planoDeContasWizardControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/edita.xhtml"),
    @URLMapping(id = "ver-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/ver/#{planoDeContasWizardControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/visualizar.xhtml"),
    @URLMapping(id = "listar-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/listar/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/lista.xhtml"),
    @URLMapping(id = "contas-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/contas/#{planoDeContasWizardControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/contas.xhtml"),
    @URLMapping(id = "nova-conta-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/contas/#{planoDeContasWizardControlador.id}/nova-conta/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/conta.xhtml"),
    @URLMapping(id = "nova-conta-filha-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/contas/#{planoDeContasWizardControlador.id}/nova-conta-filha/#{planoDeContasWizardControlador.idConta}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/conta.xhtml"),
    @URLMapping(id = "conta-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/contas/#{planoDeContasWizardControlador.id}/conta/#{planoDeContasWizardControlador.idConta}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/conta.xhtml"),
    @URLMapping(id = "ver-conta-plano-de-contas-wizard", pattern = "/plano-de-contas/passo-a-passo/contas/#{planoDeContasWizardControlador.id}/ver-conta/#{planoDeContasWizardControlador.idConta}/", viewId = "/faces/financeiro/planodecontas/planodecontaswizard/conta-visualizar.xhtml")
})
public class PlanoDeContasWizardControlador extends PrettyControlador<PlanoDeContas> implements Serializable, CRUD {

    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    private ConverterGenerico converterTipoConta;
    private ConverterAutoComplete converterFonteDeRecursos;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterExercicio;
    private ConverterGenerico converterCelulasDeInformacao;
    private String exercicio;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Long idConta;
    private Conta conta;
    //Variaveis para Conta
    private int nivelMascara;
    private int quantidadeDeDigitos;
    private String codigoAntes;
    private String codigo;
    private String codigoDepois;
    //Variaveis para Conta Equivalente
    private List<ContaEquivalente> contaEquivalentes;
    private ContaEquivalente contaEquivalente;
    private PlanoDeContas planoDeContas;
    private ConverterGenerico converterPlanos;
    private ConverterAutoComplete converterContaEquivalente;
    private Exercicio exercicioCopiaPlano;
    //Variaveis para relacionamentos da Conta
    private TreeNode itemArvore;

    public PlanoDeContasWizardControlador() {
        super(PlanoDeContas.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return planoDeContasFacade;
    }

    @URLAction(mappingId = "novo-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        exercicio = String.valueOf(sistemaControlador.getExercicioCorrenteAsInteger());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    @URLAction(mappingId = "ver-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarContas();
    }

    private void recuperarContas() {
        selecionado = planoDeContasFacade.recuperarContas(super.getId());
    }

    @URLAction(mappingId = "editar-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        iniciarExercicio();
    }

    private void iniciarExercicio() {
        if (selecionado.getExercicio() != null) {
            exercicio = String.valueOf(selecionado.getExercicio().getAno());
        } else {
            exercicio = "";
        }
    }

    @URLAction(mappingId = "nova-conta-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConta() {
        recuperarPlanoDeContas();
        instanciaContaMascara();
        calcularQuantidadeDeDigitos();
        recuperaCodigoAntesDepoisDaConta();
        definirTipoContaDespesaNaoAplicavel();
    }

    public SelectItem[] tiposCATEGORIA() {
        SelectItem[] opcoes = new SelectItem[3];
        opcoes[0] = new SelectItem("", "TODAS");
        opcoes[1] = new SelectItem(CategoriaConta.ANALITICA, CategoriaConta.ANALITICA.getDescricao());
        opcoes[2] = new SelectItem(CategoriaConta.SINTETICA, CategoriaConta.SINTETICA.getDescricao());
        return opcoes;
    }

    public SelectItem[] tiposSimNao() {
        SelectItem[] opcoes = new SelectItem[3];
        opcoes[0] = new SelectItem("", "TODAS");
        opcoes[1] = new SelectItem("true", "SIM");
        opcoes[2] = new SelectItem("false", "NÃO");
        return opcoes;
    }

    public SelectItem[] tiposContaReceitasView() {
        SelectItem[] opcoes = new SelectItem[TiposCredito.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TiposCredito tipo : TiposCredito.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposNaturezaSaldoContaEquivalenteView() {
        SelectItem[] opcoes = new SelectItem[NaturezaSaldoContaEquivalente.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (NaturezaSaldoContaEquivalente tipo : NaturezaSaldoContaEquivalente.values()) {
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
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposContaExtraView() {
        SelectItem[] opcoes = new SelectItem[TipoContaExtraorcamentaria.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoContaExtraorcamentaria tipo : TipoContaExtraorcamentaria.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
            i++;
        }
        return opcoes;
    }

    public SelectItem[] tiposContaDestinacaoView() {
        SelectItem[] opcoes = new SelectItem[TipoDestinacaoRecurso.values().length + 1];
        opcoes[0] = new SelectItem("", "TODAS");
        int i = 1;
        for (TipoDestinacaoRecurso tipo : TipoDestinacaoRecurso.values()) {
            opcoes[i] = new SelectItem(tipo, tipo.toString());
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

    public void salvarContaEquivalente() {
        planoDeContasFacade.getContaFacade().salvar(conta);
        selecionado = planoDeContasFacade.recuperar(((PlanoDeContas) selecionado).getId());
        FacesUtil.addOperacaoRealizada("Conta Equivalente salva com sucesso.");
    }

    public List<Exercicio> recuperaExerciciosSemPlano(String parte) {
        return planoDeContasFacade.getExercicioFacade().listaExerciciosAtualFuturosFiltrando(parte.trim());
    }

    public void duplicaPlanoDeContaParaExercicio() {
        try {
            if (exercicioCopiaPlano == null) {
                FacesUtil.addCampoObrigatorio("O campo exercício deve ser informado.");
            } else {
                recuperarContas();
                planoDeContasFacade.salvarPlanoDuplicado(selecionado, exercicioCopiaPlano);
                RequestContext.getCurrentInstance().execute("dialogDuplicar.hide()");
                FacesUtil.addOperacaoRealizada("Plano de Contas de " + selecionado.getTipoConta().getClasseDaConta() + " foi duplicado com sucesso para o Exercício de <b> " + exercicioCopiaPlano.getAno() + "</b>.");
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getLocalizedMessage().toString());
        }
    }

    public void abrirDialogDuplicar() {
        RequestContext.getCurrentInstance().execute("dialogDuplicar.show()");
    }

    public void cancelaDuplicacao() {
        RequestContext.getCurrentInstance().execute("dialogDuplicar.hide()");
    }

    public void instanciarContaEquivalente() {
        contaEquivalente = new ContaEquivalente();
        recuperarPlanoDeContasAnterior();
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, planoDeContasFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Conta> completaContaEquivalente(String parte) {
        try {
            return planoDeContasFacade.getContaFacade().listaPorPlanoDeContas(planoDeContas, parte);
        } catch (Exception ex) {
            return new ArrayList<Conta>();
        }
    }

    public boolean validaContaEquivalente() {
        if (planoDeContas == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Plano de Contas deve ser informado.");
            return false;
        }
        if (contaEquivalente.getContaOrigem() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Conta Origem deve ser informado.");
            return false;
        }
        if (contaEquivalente.getDataRegistro() == null) {
            return false;
        }

        return true;
    }

    public boolean validaContaEquivalenteRepetida(ContaEquivalente contaEquivalente) {
        for (ContaEquivalente contaDaVez : conta.getContasEquivalentes()) {
            if (contaDaVez.getContaOrigem().equals(contaEquivalente.getContaOrigem()) && !contaDaVez.equals(contaEquivalente)) {
                FacesUtil.addOperacaoNaoPermitida("A Conta Equivalente: " + contaEquivalente.getContaOrigem() + " já foi adicionada.");
                return false;
            }
        }
        return true;
    }

    public void novaContaEquivalente() {
        if (validaContaEquivalente()) {
            contaEquivalente.setContaDestino(conta);
            if (conta.getContasEquivalentes() == null) {
                conta.setContasEquivalentes(new ArrayList<ContaEquivalente>());
            }
            if (validaContaEquivalenteRepetida(contaEquivalente)) {
                conta.setContasEquivalentes(Util.adicionarObjetoEmLista(conta.getContasEquivalentes(), contaEquivalente));
                contaEquivalente = new ContaEquivalente();
            }
        }
    }

    public void alterarContaEquivalentes(ContaEquivalente c) {
        contaEquivalente = (ContaEquivalente) Util.clonarObjeto(c);
        planoDeContas = contaEquivalente.getContaOrigem().getPlanoDeContas();
    }

    public void removeContaEquivalentes(ContaEquivalente c) {
        conta.getContasEquivalentes().remove(c);
    }

    private void recuperarPlanoDeContas() {
        PlanoDeContas recuperado = (PlanoDeContas) Web.pegaDaSessao(PlanoDeContas.class);
        if (recuperado != null) {
            selecionado = recuperado;
        } else {
            selecionado = (PlanoDeContas) getFacede().recuperar(super.getId());
        }
    }

    @URLAction(mappingId = "nova-conta-filha-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaContaFilha() {
        recuperarPlanoDeContas();
        Conta superior = planoDeContasFacade.getContaFacade().recuperar(idConta);
        inicializaConta();
        conta.setSuperior(superior);
        conta.setCodigo(recuperaStringDoNivelDaConta(superior.getNivel() + 1, superior));
        calcularQuantidadeDeDigitos();
        recuperaCodigoAntesDepoisDaConta();
        if (isTipoContaDestinacao()) {
            ContaDeDestinacao contaDestinacaoSuperior = ((ContaDeDestinacao) superior);
            if (contaDestinacaoSuperior.getFonteDeRecursos() != null) {
                ((ContaDeDestinacao) conta).setFonteDeRecursos(contaDestinacaoSuperior.getFonteDeRecursos());
            }
        }
        recuperarNivelDaMascaraTipoConta();
        definirTipoContaDespesaNaoAplicavel();
    }

    @URLAction(mappingId = "contas-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void contasPlanoDeContas() {
        recuperarPlanoDeContas();
    }

    @URLAction(mappingId = "conta-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarConta() {
        recuperarPlanoDeContas();

        conta = planoDeContasFacade.getContaFacade().recuperar(idConta);
        if (selecionado == null) {
            selecionado = conta.getPlanoDeContas();
        }
        recuperarNivelDaMascaraTipoConta();
        recuperaCodigoAntesDepoisDaConta();
        calcularQuantidadeDeDigitos();
        codigo = recuperaStringDoNivelDaConta(conta.getNivel(), conta);
    }

    @URLAction(mappingId = "ver-conta-plano-de-contas-wizard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verConta() {
        recuperarPlanoDeContas();

        conta = planoDeContasFacade.getContaFacade().recuperar(idConta);
        if (selecionado == null) {
            selecionado = conta.getPlanoDeContas();
        }

        recuperaCodigoAntesDepoisDaConta();
        calcularQuantidadeDeDigitos();
        if (canRemoverContaSelecionada() && canInformarCodigoContaDeDestinacao()) {
            this.codigo = recuperaStringDoNivelDaConta(conta.getNivel(), conta);
        }
    }

    public void redirecionarContas() {
        if (Util.validaCampos(selecionado)) {
            if (selecionado.getId() != null) {
                FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
            } else {
                Web.poeNaSessao(this.selecionado);
                FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/0/");
            }
        }
    }

    public void redirecionarNovaConta() {
        if (Util.validaCampos(selecionado)) {
            if (selecionado.getId() != null) {
                FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/nova-conta/");
            } else {
                Web.poeNaSessao(this.selecionado);
                FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/0/nova-conta/");
            }
        }
    }

    public String redirecionarConta(Object o) {

        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/conta/" + Persistencia.getId(o) + "/";
    }

    public String redirecionarVerConta(Object o) {

        if (selecionado.getId() != null) {
            return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/ver-conta/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return FacesUtil.getRequestContextPath() + this.getCaminhoPadrao() + "contas/0/ver-conta/" + Persistencia.getId(o) + "/";
    }

    public String redirecionarNovaContaFilha(Object o) {

        if (selecionado.getId() != null) {
            return this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/nova-conta-filha/" + Persistencia.getId(o) + "/";
        }
        Web.poeNaSessao(this.selecionado);
        return this.getCaminhoPadrao() + "contas/0/nova-conta-filha/" + Persistencia.getId(o) + "/";
    }

    public void redicionarInicio() {
        if (selecionado.getId() == null) {
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "novo/");
        } else {
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
        }
    }

    public void voltarRedirecionarContas() {
        if (selecionado.getId() == null) {
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "novo/");
        } else {
            FacesUtil.redirecionamentoInterno(this.getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/plano-de-contas/passo-a-passo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipoConta() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoConta tipoConta : planoDeContasFacade.getTipoContaFacade().buscarPorExercicio(selecionado.getExercicio())) {
            retorno.add(new SelectItem(tipoConta, tipoConta.getDescricao()));
        }
        return retorno;
    }

    public ConverterGenerico getConverterTipoConta() {
        if (converterTipoConta == null) {
            converterTipoConta = new ConverterGenerico(TipoConta.class, planoDeContasFacade.getTipoContaFacade());
        }
        return converterTipoConta;
    }

    public ConverterGenerico getConverterPlanos() {
        if (converterPlanos == null) {
            converterPlanos = new ConverterGenerico(PlanoDeContas.class, planoDeContasFacade);
        }
        return converterPlanos;
    }

    public ConverterGenerico getConverterCelulasDeInformacao() {
        if (converterCelulasDeInformacao == null) {
            converterCelulasDeInformacao = new ConverterGenerico(CelulaDeInformacao.class, planoDeContasFacade.getCelulaDeInformacaoFacade());
        }
        return converterCelulasDeInformacao;
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

    public ConverterAutoComplete getConverterContaEquivalente() {
        if (converterContaEquivalente == null) {
            converterContaEquivalente = new ConverterAutoComplete(Conta.class, planoDeContasFacade.getContaFacade());
        }
        return converterContaEquivalente;
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String parte) {
        return planoDeContasFacade.getFonteDeRecursosFacade().buscarFontesPorExercicioAndCodigoIniciandoEmAndDescricao(conta.getSuperior().getCodigo(), parte.trim(), selecionado.getExercicio());
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

    public List<Conta> completarContasContabeisAnaliticas(String parte) {
        List<Conta> retorno = Lists.newArrayList();
        retorno.addAll(planoDeContasFacade.getContaFacade().buscarContasContabeis(parte.trim(), sistemaControlador.getExercicioCorrente(), null));
        return retorno;
    }

    public List<SelectItem> getPlanos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (selecionado.getTipoConta() != null) {
            for (PlanoDeContas object : planoDeContasFacade.recuperarPlanoPorTipoContaEExercicio(selecionado.getTipoConta(), selecionado.getExercicio())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    private void recuperarPlanoDeContasAnterior() {
        if (selecionado.getTipoConta() != null) {
            Exercicio exercicioAnterior = planoDeContasFacade.getExercicioFacade().recuperarExercicioPeloAno(selecionado.getExercicio().getAno() - 1);
            planoDeContas = planoDeContasFacade.recuperarPlanoPorTipoContaEExercicio(selecionado.getTipoConta(), exercicioAnterior).get(0);
        }
    }

    public List<SelectItem> getTipoDestinaoRecursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Selecione"));
        for (TipoDestinacaoRecurso object : TipoDestinacaoRecurso.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDaContaDeDespesa() {
        return Util.getListSelectItem(TipoContaDespesa.values());
    }

    public List<SelectItem> getTiposCreditos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        List<TiposCredito> values = Arrays.asList(TiposCredito.values());
        Collections.sort(values, new Comparator<TiposCredito>() {
            @Override
            public int compare(TiposCredito o1, TiposCredito o2) {
                return o1.getCodigo().compareTo(o2.getCodigo());
            }
        });
        for (TiposCredito object : values) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposContaExtra() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoContaExtraorcamentaria object : TipoContaExtraorcamentaria.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCelulasDeInformacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CelulaDeInformacao object : planoDeContasFacade.getCelulaDeInformacaoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaSaldo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaSaldo object : NaturezaSaldo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaSaldoContaEquivalente() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (NaturezaSaldoContaEquivalente object : NaturezaSaldoContaEquivalente.values()) {
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

    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }
        Integer ano = null;
        try {
            ano = Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            FacesUtil.addOperacaoNaoPermitida("Informe somente números.");
        }
        if (ano != null) {
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
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }

    public void setPlanoDeContasFacade(PlanoDeContasFacade planoDeContasFacade) {
        this.planoDeContasFacade = planoDeContasFacade;
    }

    public void setConverterConta(ConverterAutoComplete converterConta) {
        this.converterConta = converterConta;
    }

    public void setConverterCelulasDeInformacao(ConverterGenerico converterCelulasDeInformacao) {
        this.converterCelulasDeInformacao = converterCelulasDeInformacao;
    }

    public List<ContaEquivalente> getContaEquivalentes() {
        return contaEquivalentes;
    }

    public void setContaEquivalentes(List<ContaEquivalente> contaEquivalentes) {
        this.contaEquivalentes = contaEquivalentes;
    }

    public ContaEquivalente getContaEquivalente() {
        return contaEquivalente;
    }

    public void setContaEquivalente(ContaEquivalente contaEquivalente) {
        this.contaEquivalente = contaEquivalente;
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public void setConverterPlanos(ConverterGenerico converterPlanos) {
        this.converterPlanos = converterPlanos;
    }

    public void setConverterContaEquivalente(ConverterAutoComplete converterContaEquivalente) {
        this.converterContaEquivalente = converterContaEquivalente;
    }

    public void setConverterTipoConta(ConverterGenerico converterTipoConta) {
        this.converterTipoConta = converterTipoConta;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public int getNivelMascara() {
        return nivelMascara;
    }

    public void setNivelMascara(int nivelMascara) {
        this.nivelMascara = nivelMascara;
    }

    public int getQuantidadeDeDigitos() {
        return quantidadeDeDigitos;
    }

    public void setQuantidadeDeDigitos(int quantidadeDeDigitos) {
        this.quantidadeDeDigitos = quantidadeDeDigitos;
    }

    public String getCodigoAntes() {
        return codigoAntes;
    }

    public void setCodigoAntes(String codigoAntes) {
        this.codigoAntes = codigoAntes;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoDepois() {
        return codigoDepois;
    }

    public void setCodigoDepois(String codigoDepois) {
        this.codigoDepois = codigoDepois;
    }
//    Metodos Utilizados pela VIEW

    public void selecionarConta(ActionEvent actionEvent) {
        Conta c = (Conta) actionEvent.getComponent().getAttributes().get("objeto");
    }

    public boolean isTipoContaContabil() {
        return ClasseDaConta.CONTABIL.equals(getClasseContaSelecionado());
    }

    public boolean isTipoContaDestinacao() {
        return ClasseDaConta.DESTINACAO.equals(getClasseContaSelecionado());
    }

    public boolean isTipoContaDespesa() {
        return ClasseDaConta.DESPESA.equals(getClasseContaSelecionado());
    }

    public boolean isTipoContaReceita() {
        return ClasseDaConta.RECEITA.equals(getClasseContaSelecionado());
    }

    public boolean isTipoContaAuxiliar() {
        return ClasseDaConta.AUXILIAR.equals(getClasseContaSelecionado());
    }

    public boolean isTipoContaExtraorcamentaria() {
        return ClasseDaConta.EXTRAORCAMENTARIA.equals(getClasseContaSelecionado());
    }

    public ClasseDaConta getClasseContaSelecionado() {
        if (selecionado.getTipoConta() != null) {
            return selecionado.getTipoConta().getClasseDaConta();
        }
        return null;
    }

    public void instanciaContaMascara() {
        inicializaConta();
        recuperaCodigoAntesDepoisDaConta();
    }

    private void inicializaConta() {
        if (isTipoContaContabil()) {
            conta = new ContaContabil();
        } else if (isTipoContaDespesa()) {
            conta = new ContaDespesa();
        } else if (isTipoContaDestinacao()) {
            conta = new ContaDeDestinacao();
        } else if (isTipoContaReceita()) {
            conta = new ContaReceita();
            ((ContaReceita) conta).setTiposCredito(TiposCredito.NAO_APLICAVEL);
        } else if (isTipoContaExtraorcamentaria()) {
            conta = new ContaExtraorcamentaria();
        } else {
            conta = new ContaAuxiliar();
        }
        conta.setAtiva(Boolean.TRUE);
        conta.setPlanoDeContas(selecionado);
        conta.setDataRegistro(sistemaControlador.getDataOperacao());
        conta.setContasEquivalentes(new ArrayList<ContaEquivalente>());
        conta.setExercicio(selecionado.getExercicio());
    }

    private void calcularQuantidadeDeDigitos() {
        int retorno = 0;
        if (!isTipoContaDestinacao()) {
            if (conta.getId() != null) {
                retorno = recuperaStringDoNivelDaConta(conta.getNivel(), conta).length();
            } else {
                if (conta.getSuperior() != null) {
                    retorno = recuperaStringDoNivelDaConta(conta.getSuperior().getNivel() + 1, conta.getSuperior()).length();
                }
            }
            if (retorno == 0) {
                String mascara = selecionado.getTipoConta().getMascara();
                if (!mascara.contains(".")) {
                    retorno = mascara.length();
                } else {
                    int nivelDaMascara = 0;
                    for (int i = 0; i < mascara.length(); i++) {
                        if (".".equals(mascara.substring(i, i + 1))) {
                            nivelDaMascara++;
                        }
                        if (!isTipoContaDestinacao() && nivelDaMascara == 1) {
                            break;
                        }
                    }
                    retorno = nivelDaMascara;
                }
            }
        } else {
            String[] mascara = selecionado.getTipoConta().getMascara().split("\\.");
            if (conta.getSuperior() != null) {
                String[] codigoSuperior = conta.getSuperior().getCodigo().split("\\.");
                retorno = mascara[codigoSuperior.length].length();
            } else {
                retorno = mascara[0].length();
            }
        }
        quantidadeDeDigitos = retorno;
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

    public void recuperaCodigoAntesDepoisDaConta() {
        codigoAntes = "";
        codigoDepois = "";
        codigo = "";
        if (!isTipoContaExtraorcamentaria()) {
            int nivelDoCodigoDaConta = 0;
            String codigo = "";
            if (conta.getId() != null) {
                codigo = conta.getCodigo();
                nivelDoCodigoDaConta = conta.getNivel();
            } else {
                if (conta.getSuperior() != null) {
                    codigo = conta.getSuperior().getCodigo();
                    nivelDoCodigoDaConta = conta.getSuperior().getNivel() + 1;
                }
            }

            if (Strings.isNullOrEmpty(codigo)) {
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
        if (isTipoContaDestinacao()) {
            if (!Strings.isNullOrEmpty(codigoAntes) && !codigoAntes.endsWith(".")) {
                codigoAntes += ".";
            }
            codigoDepois = "";
        }
    }

    public Boolean canRemoverContaSelecionada() {
        return conta != null && podeRemoverConta(conta);
    }

    public boolean canInformarCodigoContaDeDestinacao() {
        return !(isTipoContaDestinacao() && hasSuperior());
    }

    private Boolean podeRemoverConta(Conta conta) {
        return planoDeContasFacade.verificaContasFilhas(conta);
    }

    public boolean hasSuperior() {
        return conta != null && conta.getSuperior() != null;
    }

    public boolean hasFonteInformadaNoSuperior() {
        return isTipoContaDestinacao() &&
            conta.getSuperior() != null &&
            ((ContaDeDestinacao) conta.getSuperior()).getFonteDeRecursos() != null;
    }

    public String getNomeDaClasse() {
        if (isTipoContaContabil()) {
            return ContaContabil.class.getSimpleName();
        } else if (isTipoContaDespesa()) {
            return ContaDespesa.class.getSimpleName();
        } else if (isTipoContaDestinacao()) {
            return ContaDeDestinacao.class.getSimpleName();
        } else if (isTipoContaReceita()) {
            return ContaReceita.class.getSimpleName();
        } else if (isTipoContaExtraorcamentaria()) {
            return ContaExtraorcamentaria.class.getSimpleName();
        } else {
            return ContaAuxiliar.class.getSimpleName();
        }
    }

    public String getNomeDaTela() {
        String planoDeContas = "Plano de Contas ";
        if (selecionado.getId() != null) {
            planoDeContas += "(" + selecionado.toString() + ")";
        }
        if (isTipoContaContabil()) {
            return " Contas Contábeis " + " -  " + planoDeContas;
        } else if (isTipoContaDespesa()) {
            return " Contas de Despesas" + " -  " + planoDeContas;
        } else if (isTipoContaDestinacao()) {
            return " Contas de Destinação de Recurso" + " -  " + planoDeContas;
        } else if (isTipoContaReceita()) {
            return " Contas de Receitas" + " -  " + planoDeContas;
        } else if (isTipoContaExtraorcamentaria()) {
            return " Contas de Extraorçamentárias" + " -  " + planoDeContas;
        } else {
            return " Contas de Extra Auxiliares" + " -  " + planoDeContas;
        }
    }

    public String getNomeTelaConta() {
        if (isTipoContaContabil()) {
            return " Conta Contábil ";
        } else if (isTipoContaDespesa()) {
            return " Conta de Despesa";
        } else if (isTipoContaDestinacao()) {
            return " Conta de Destinação de Recurso";
        } else if (isTipoContaReceita()) {
            return " Conta de Receita";
        } else if (isTipoContaExtraorcamentaria()) {
            return " Conta Extraorçamentária";
        } else {
            return " Conta Extra Auxiliar";
        }
    }

    public void adicionarConta() {
        validarCodigoDescricaoConta();
        String codigoAntigo = tratamentosDoCodigo();
        setaCategoriaConta();
        validaCategoriaConta();
        validacoesContaContabil();
        validarContaDespesa();

        if (podeAdicionarConta(conta)) {
            selecionado.setContas(new ArrayList<Conta>());
            selecionado.setContas(Util.adicionarObjetoEmLista(selecionado.getContas(), conta));
            if (isTipoContaDespesa()) {
                alteraTipoContaDespesaFilhas(conta);
            } else if (isTipoContaDestinacao()) {
                ContaDeDestinacao contaDestinacao = (ContaDeDestinacao) conta;
                if (contaDestinacao.getFonteDeRecursos() == null && conta.getSuperior() != null) {
                    throw new ExcecaoNegocioGenerica("O campo Fonte de Recurso deve ser informado");
                }
            }
        } else {
            conta.setCodigo(codigoAntigo);
            throw new ExcecaoNegocioGenerica("Já foi adicionado uma conta com o código informado. ");
        }
    }

    private ContaDespesa getAsContaDespesa(Conta c) {
        return (ContaDespesa) c;
    }

    private boolean podeAdicionarConta(Conta conta) {
//        for (Conta conta1 : selecionado.getContas()) {
//            if (!conta.equals(conta1)) {
//                if ((conta1.getCodigo().equals(conta.getCodigo())) && !(conta1.getDescricao().toLowerCase().equals(conta.getDescricao().toLowerCase()))) {
//                    return false;
//                }
//            }
//        }
//        return true;
        return planoDeContasFacade.verificaCodigoContaPlanoDeContas(conta);
    }

    private void alteraTipoContaDespesaFilhas(Conta superior) {
        if (getAsContaDespesa(superior).getTipoContaDespesa() == null) {
            return;
        }
        List<Conta> contas = planoDeContasFacade.getContaFacade().recuperaContasDespesaFilhas(superior);
        String codigo = superior.getCodigoSemZerosAoFinal();
        for (Conta conta1 : contas) {
            if (conta1.getCodigo().startsWith(codigo) && !conta1.equals(superior)) {
                if (getAsContaDespesa(conta1).getTipoContaDespesa() == null) {
                    getAsContaDespesa(conta1).setTipoContaDespesa(getAsContaDespesa(superior).getTipoContaDespesa());
                    planoDeContasFacade.getContaFacade().salvar(conta1);
                }
            }
        }
    }

    private String retiraPontoSeForUltimoNivelDaMascara(String codigo) {
        if (codigo.endsWith(".")) {
            return codigo.substring(0, codigo.length() - 1);
        }
        return codigo;
    }

    private String tratamentosDoCodigo() {
        String codigoAntigo = null;
        if (canRemoverContaSelecionada()) {
            String codigo = codigoAntes + this.codigo + "." + codigoDepois;
            codigo = retiraPontoSeForUltimoNivelDaMascara(codigo);
            codigoAntigo = conta.getCodigo();
            conta.setCodigo(codigo);
        } else {
            codigoAntigo = conta.getCodigo();
        }
        return codigoAntigo;
    }

    private void validacoesContaContabil() {
        if (isTipoContaContabil()) {
            ContaContabil c = (ContaContabil) conta;
            if (c.getNaturezaInformacao() != null) {
                if (c.getNaturezaInformacao().equals(NaturezaInformacao.PATRIMONIAL)) {
                    if (!(c.getSubSistema().equals(SubSistema.FINANCEIRO) || c.getSubSistema().equals(SubSistema.PERMANENTE))) {
                        throw new ExcecaoNegocioGenerica("Quando informado a Natureza da Informação como <b>" + c.getNaturezaInformacao().getDescricao() + "</b>, só é permitido o SubSistema do tipo <b>" + SubSistema.FINANCEIRO.getDescricao() + "</b> ou <b>" + SubSistema.PERMANENTE.getDescricao() + "</b>.");
                    }
                }
                if (c.getNaturezaInformacao().equals(NaturezaInformacao.ORCAMENTARIO) || c.getNaturezaInformacao().equals(NaturezaInformacao.CONTROLE)) {
                    if (!(c.getSubSistema().equals(SubSistema.ORCAMENTARIO) || c.getSubSistema().equals(SubSistema.COMPENSADO))) {
                        throw new ExcecaoNegocioGenerica("Quando informado a Natureza da Informação como <b>" + c.getNaturezaInformacao().getDescricao() + "</b>, só é permitido o Subsistema do tipo <b>" + SubSistema.ORCAMENTARIO.getDescricao() + "</b> ou <b>" + SubSistema.COMPENSADO.getDescricao() + "</b>.");
                    }
                }
            }
        }

    }

    public boolean isContaDespesaMenorIgualQuatro() {
        return isTipoContaDespesa() && nivelMascara <= 4;
    }

    private void definirTipoContaDespesaNaoAplicavel() {
        if (isContaDespesaMenorIgualQuatro()) {
            ContaDespesa cd = (ContaDespesa) conta;
            cd.setTipoContaDespesa(TipoContaDespesa.NAO_APLICAVEL);
        }
    }

    private void validarContaDespesa() {
        ValidacaoException ve = new ValidacaoException();
        if (isTipoContaDespesa() && ((ContaDespesa) conta).getTipoContaDespesa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Tipo de Conta de Despesa");
        }
        ve.lancarException();
    }

    public void validaCategoriaConta() {
        if (isTipoContaContabil()) {
            ContaContabil cc = (ContaContabil) conta;
            if (cc.getNaturezaSaldo() == null) {
                throw new ExcecaoNegocioGenerica("O campo Natureza do Saldo deve ser informado.");
            }
            if (cc.getNaturezaConta() == null) {
                throw new ExcecaoNegocioGenerica("O campo Natureza da Conta deve ser informado.");
            } else {
                if (cc.getCategoria().equals(CategoriaConta.ANALITICA)) {
                    if (cc.getNaturezaInformacao() == null) {
                        throw new ExcecaoNegocioGenerica("Quando a Categoria da Conta for ANÁLITICA o campo Natureza da Informação deve ser informado.");
                    } else {
                        if (cc.getSubSistema() == null) {
                            throw new ExcecaoNegocioGenerica("Quando a Categoria da Conta for ANÁLITICA o campo Natureza do SubSistema deve ser informado.");
                        }
                    }
                }
            }
        }
    }

    public boolean isTipoContaContabilAnalitica() {
        try {
            if (isTipoContaContabil() && conta.getCategoria().equals(CategoriaConta.ANALITICA)) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void setaCategoriaConta() {
        if (conta.getPermitirDesdobramento()) {
            conta.setCategoria(CategoriaConta.SINTETICA);
        } else {
            conta.setCategoria(CategoriaConta.ANALITICA);
        }
    }

    public boolean isTipoCategoriaPorDesdobramento() {
        try {
            if (conta.getPermitirDesdobramento()) {
                conta.setCategoria(CategoriaConta.SINTETICA);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean mostraContaCorrespondente() {
        if (conta instanceof ContaReceita) {
            if (((ContaReceita) conta).getTiposCredito() != null) {
                if (((ContaReceita) conta).getTiposCredito().name().startsWith("CREDITO")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean disabilitaBotaoAddConta(Conta c) {
        return !c.getPermitirDesdobramento() || (!isTipoContaDestinacao() && ehUltimoNivel(c, c.getNivel()));
    }

    private boolean ehUltimoNivel(Conta c, int nivelConta) {
        int nivelMascara = 1;
        if (c == null) {
            return false;
        }
        for (int i = 0; i < c.getCodigo().length(); i++) {
            String letra = c.getCodigo().substring(i, i + 1);
            if (letra.equals(".")) {
                nivelMascara++;
            }
        }
        if (nivelConta == nivelMascara) {
            return true;
        }
        return false;
    }

    public String getVerificaMascaraContaReceita() {
        ConfiguracaoPlanejamentoPublico cpp = planoDeContasFacade.getConfiguracaoPlanejamentoPublicoFacade().retornaUltima();
        if (cpp == null || cpp.getMascaraCodigoReceitaLOA() == null) {
            return "99999999";
        }
        return cpp.getMascaraCodigoReceitaLOA().replaceAll("#", "9");
    }

    public String getMascaraTipoContaExtra() {
        return selecionado.getTipoConta().getMascara();
    }

    public void acrescentaCodigoDescricaoFonte() {
        String codigo = "";
        ContaDeDestinacao contaDestinacao = (ContaDeDestinacao) conta;
        if (hasSuperior()) {
            codigo += (contaDestinacao.getFonteDeRecursos().getCodigo().length() == 3 ? contaDestinacao.getFonteDeRecursos().getCodigo().substring(1, 3) : contaDestinacao.getFonteDeRecursos().getCodigo());
            conta.setDescricao(contaDestinacao.getFonteDeRecursos().getDescricao());
        }

        codigo = "." + codigo;
        conta.setCodigo(conta.getSuperior().getCodigo());
        conta.setCodigo(conta.getCodigo() + codigo);

    }

    public Boolean desabilitarFonteDeRecurso() {
        return hasFonteInformadaNoSuperior();
    }

    public void removeConta(Conta conta) {
        try {
            if (podeRemoverConta(conta)) {
                Conta superior = conta.getSuperior();
                planoDeContasFacade.getContaFacade().removeConta(conta);
                if (superior != null) {
                    List<Conta> recuperaContasFilhas = planoDeContasFacade.getContaFacade().recuperaContasFilhas(superior);
                    if (recuperaContasFilhas.isEmpty()) {
                        superior.setCategoria(CategoriaConta.ANALITICA);
                        superior.setPermitirDesdobramento(Boolean.FALSE);
                        planoDeContasFacade.getContaFacade().salvar(superior);
                    }
                }
                FacesUtil.addOperacaoRealizada("A conta " + conta + " foi excluida com sucesso.");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "contas/" + selecionado.getId() + "/");
            } else {
                FacesUtil.addOperacaoNaoPermitida("A conta " + conta + " possui filho(s) associado(s). Por isso não pode ser excluida.");
            }
        } catch (Exception ex) {
            try {
                validarForeinKeysComRegistro(conta);
                descobrirETratarException(ex);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    private void validarForeinKeysComRegistro(Conta conta) {
        ValidacaoException ve = new ValidacaoException();
        List<String> tabelas = planoDeContasFacade.getContaFacade().buscarCasosDeUsoComRegistroVinculado(conta);
        if (tabelas != null && !tabelas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O registro possui vínculo(s) com a(s) seguinte(s) funcionalidade(s): <br/><br/>"
                + StringUtils.join(tabelas, "<br/>"));
        }
        ve.lancarException();
    }

    private void validarCodigoDescricaoConta() {
        ValidacaoException ve = new ValidacaoException();
        if (isTipoContaExtraorcamentaria()) {
            codigo = conta.getCodigo();
        }
        if (Strings.isNullOrEmpty(codigo.trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código deve ser informado.");
        } else {
            if (!isTipoContaExtraorcamentaria() && codigo.length() != quantidadeDeDigitos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe " + quantidadeDeDigitos + " dígitos para adicionar a conta, obedecendo a máscara.");
            }
        }
        if (isTipoContaDestinacao()) {
            if (conta.getSuperior() == null && !"1".equals(codigo) && !"2".equals(codigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código deve ser '1' ou '2' no 1º nível da conta de destinação de recurso.");
            }
        }
        if (Strings.isNullOrEmpty(conta.getDescricao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (selecionado.getId() == null) {
                    selecionado.setDataRegistro(sistemaControlador.getDataOperacao());
                    planoDeContasFacade.salvarNovo(selecionado);
                } else {
                    planoDeContasFacade.salvar(selecionado);
                }

                if (isSessao()) {
                    return;
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao salvar. Detalhes do erro: " + e.getMessage());
            }

        }
    }

    public void salvarConta() {
        try {
            adicionarConta();
            conta.setExercicio(selecionado.getExercicio());
            selecionado = planoDeContasFacade.meuSalvar(conta);
            FacesUtil.addOperacaoRealizada(" A Conta " + conta + " foi salva com sucesso.");
            redirecionarContas();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public Exercicio getExercicioCopiaPlano() {
        return exercicioCopiaPlano;
    }

    public void setExercicioCopiaPlano(Exercicio exercicioCopiaPlano) {
        this.exercicioCopiaPlano = exercicioCopiaPlano;
    }

    public TreeNode getItemArvore() {
        return itemArvore;
    }

    public void setItemArvore(TreeNode itemArvore) {
        this.itemArvore = itemArvore;
    }

    public void setarConta(Conta conta) {
        this.conta = conta;
        itemArvore = new DefaultTreeNode();


        TreeNode inicio = new DefaultTreeNode(this.conta, itemArvore);

        if (isTipoContaContabil()) {
            montarArvoreEventoClpLCP(inicio);
            montarArvoreOCCContaContabil(inicio);
        } else if (isTipoContaDespesa()) {
            montarArvoreOCCConta(inicio);
        } else if (isTipoContaDestinacao()) {
            montarArvoreOCCConta(inicio);
        } else if (isTipoContaReceita()) {
            montarArvoreOCCConta(inicio);
        } else if (isTipoContaExtraorcamentaria()) {
            montarArvoreOCCConta(inicio);
        }

    }

    private void montarArvoreOCCConta(TreeNode inicio) {
        List<OCCConta> origemContaContabeis = planoDeContasFacade.getOrigemContaContabilOccConta(this.conta);
        TreeNode origem = new DefaultTreeNode(origemContaContabeis.isEmpty() ? "Nenhuma Origem Contábil encontrada!" : "Origens Contábeis", inicio);
        for (OCCConta origemContaContabil : origemContaContabeis) {
            TreeNode occ = new DefaultTreeNode(Persistencia.getNomeDaClasse(origemContaContabil.getClass()) + ":" + origemContaContabil.toString(), origem);
            for (Field field : Persistencia.getAtributos(origemContaContabil.getClass())) {
                try {
                    Object o = field.get(origemContaContabil);
                    if (o.equals(this.conta)) {
                        TreeNode contaT = new DefaultTreeNode(" <b> Atributo: " + Persistencia.getNomeDoCampo(field) + " </b>", occ);
                    }
                } catch (Exception e) {
                }
            }
        }

    }

    private void montarArvoreOCCContaContabil(TreeNode inicio) {
        List<OrigemContaContabil> origemContaContabeis = planoDeContasFacade.getOrigemContaContabil(this.conta);
        TreeNode origem = new DefaultTreeNode(origemContaContabeis.isEmpty() ? "Nenhuma Origem Contábil encontrada!" : "Origens Contábeis", inicio);
        for (OrigemContaContabil origemContaContabil : origemContaContabeis) {
            TreeNode occ = new DefaultTreeNode(Persistencia.getNomeDaClasse(origemContaContabil.getClass()) + ":" + origemContaContabil, origem);
            for (Field field : Persistencia.getAtributos(origemContaContabil.getClass())) {
                try {
                    Object o = field.get(origemContaContabil);
                    if (o.equals(this.conta)) {
                        TreeNode contaT = new DefaultTreeNode(" <b> Atributo: " + Persistencia.getNomeDoCampo(field) + " </b>", occ);
                    }
                } catch (Exception e) {
                }
            }
        }

    }

    private void montarArvoreEventoClpLCP(TreeNode inicio) {
        List<EventoContabil> eventosContabil = planoDeContasFacade.getEventosContabil(this.conta);
        TreeNode eventos = new DefaultTreeNode(eventosContabil.isEmpty() ? "Nenhum Evento Contábil encontrado!" : "Eventos Contábeis", inicio);
        for (EventoContabil eventoContabil : eventosContabil) {
            TreeNode evento = new DefaultTreeNode("Evento Contábil: " + eventoContabil, eventos);

            for (ItemEventoCLP itemEventoCLP : eventoContabil.getItemEventoCLPs()) {
                TreeNode clp = new DefaultTreeNode("CLP: " + itemEventoCLP.getClp(), evento);
                for (LCP lcp : itemEventoCLP.getClp().getLcps()) {
                    TreeNode lcpT = new DefaultTreeNode("LCP: " + lcp, clp);
                    for (Field field : Persistencia.getAtributos(lcp.getClass())) {
                        try {
                            Object o = field.get(lcp);
                            if (o.equals(this.conta)) {
                                TreeNode contaT = new DefaultTreeNode(" <b> Atributo: " + Persistencia.getNomeDoCampo(field) + " </b>", lcpT);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public void recuperarNivelDaMascaraTipoConta() {
        if (!ClasseDaConta.EXTRAORCAMENTARIA.equals(selecionado.getTipoConta().getClasseDaConta())) {
            if (conta.getSuperior() != null) {
                nivelMascara = conta.getSuperior().getNivel() + 1;
            } else {
                nivelMascara = 1;
            }

        }
    }

    public void aumentarNivelMascara() {
        if (!ehUltimoNivel(conta.getSuperior(), nivelMascara)) {
            nivelMascara++;
            quantidadeDeDigitos = recuperaStringDoNivelDaConta(nivelMascara, conta.getSuperior()).length();

            atribuirCodigoAntesDepois();
        }
    }

    public void diminuirNivelMascara() {
        if (nivelMascara > conta.getSuperior().getNivel() + 1) {
            nivelMascara--;
            quantidadeDeDigitos = recuperaStringDoNivelDaConta(nivelMascara, conta.getSuperior()).length();
            atribuirCodigoAntesDepois();
        }
    }

    private void atribuirCodigoAntesDepois() {
        if (!isTipoContaExtraorcamentaria()) {
            codigoAntes = "";
            codigoDepois = "";
            codigo = "";

            int nivel = 1;

            for (int i = 0; i < conta.getSuperior().getCodigo().length(); i++) {
                String letra = conta.getSuperior().getCodigo().substring(i, i + 1);

                if (nivel < (nivelMascara)) {
                    codigoAntes += letra;
                }

                if (nivel > (nivelMascara)) {
                    codigoDepois += letra;
                }
                if (letra.equals(".")) {
                    nivel++;
                }
            }
        }
    }
}
