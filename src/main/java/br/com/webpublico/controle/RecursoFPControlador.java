/*
 * Codigo gerado automaticamente em Wed Jun 29 13:52:58 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.enums.rh.integracao.TipoContribuicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "recursoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRecursoFP", pattern = "/recursofp/novo/", viewId = "/faces/rh/administracaodepagamento/recursofp/edita.xhtml"),
    @URLMapping(id = "editarRecursoFP", pattern = "/recursofp/editar/#{recursoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recursofp/edita.xhtml"),
    @URLMapping(id = "listarRecursoFP", pattern = "/recursofp/listar/", viewId = "/faces/rh/administracaodepagamento/recursofp/lista.xhtml"),
    @URLMapping(id = "verRecursoFP", pattern = "/recursofp/ver/#{recursoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recursofp/visualizar.xhtml")
})
public class RecursoFPControlador extends PrettyControlador<RecursoFP> implements Serializable, CRUD {

    @EJB
    private RecursoFPFacade recursoFPFacade;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private ConverterAutoComplete converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;

    private FonteDespesaORC fonteDespesaORC;
    //    private EventoFPRecurso eventoFPRecurso;
//    private FonteRecurso fonteRecurso;
    private Tipo tipo;
    private EventoFP eventoFP;
    private OperacaoFormula operacaoFormula;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    private FontesRecursoFP fontesRecursoFP;
    private FontesRecursoFP fontesRecursoFPSelecionado;
    @EJB
    private EmpenhoFacade empenhoFacade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private FonteEventoFP fonteEventoFP;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaExtraorcamentaria;
    private ConverterAutoComplete converterSubConta;
    private ConverterGenerico converterClasseCredor;
    private ConverterAutoComplete converterConta;
    private List<ClasseCredor> listaClasse;
    private FonteEventoFP fonteEventoFPSelecionado;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;



    private boolean novoEditarFonte;
    private boolean fonteConsignacao = true;
    private boolean fonteEmpenho = true;
    private boolean bloquearBotao;

    private int index;

    private boolean empenho;
    private boolean retencao;
    private boolean realizavel;
    private boolean variacao;
    private ContaDeDestinacao contaDeDestinacao;


    @EJB
    private ContaFacade contaFacade;
    private ConverterAutoComplete converterContaDespesaDesdobrada;
    private SubConta contaFinanceira;
    private ConverterAutoComplete converterRecursoFP;

    public RecursoFPControlador() {
        super(RecursoFP.class);
    }

    public RecursoFPFacade getFacade() {
        return recursoFPFacade;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    @Override
    public AbstractFacade getFacede() {
        return recursoFPFacade;
    }

    public void removerFonte(FontesRecursoFP fonte) {
        selecionado.getFontesRecursoFPs().remove(fonte);
    }


    //    public List<SelectItem> getTipoEmpenhoIntegracao() {
//        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
//        for (TipoEmpenhoIntegracao object : TipoEmpenhoIntegracao.values()) {
//            toReturn.add(new SelectItem(object, object.getDescricao()));
//        }
//        return toReturn;
//    }
    //    public void adicionaEvento(FonteDespesaORC fonte) {
//        tipo = Tipo.FONTE;
//        fonteDespesaORC = fonte;
//        eventoFPRecurso.setFonteDespesaORC(fonte);
//    }
    public void adicionaEventoFonte(FontesRecursoFP fonteRecurso) {
        tipo = Tipo.EVENTO;
        fontesRecursoFP = fonteRecurso;
        fonteEventoFP = new FonteEventoFP();
    }

    public void removeFonte(FontesRecursoFP fontesRecursoFP) {
        ((RecursoFP) selecionado).getFontesRecursoFPs().remove(fontesRecursoFP);
        fonteEventoFP = new FonteEventoFP();
        fontesRecursoFP = new FontesRecursoFP();
        fontesRecursoFP.setFonteDespesaORC(null);
        contaFinanceira = new SubConta();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/recursofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoRecursoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        RecursoFP rfp = ((RecursoFP) selecionado);
        fontesRecursoFP = new FontesRecursoFP();
        fonteEventoFP = new FonteEventoFP();
        tipo = Tipo.PRINCIPAL;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaSTR("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        listaClasse = new ArrayList<ClasseCredor>();
        contaFinanceira = new SubConta();
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @URLAction(mappingId = "editarRecursoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        fontesRecursoFP = new FontesRecursoFP();
        fonteEventoFP = new FonteEventoFP();
        tipo = Tipo.PRINCIPAL;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaSTR("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaORCSelecionada(selecionado.getDespesaORC());
    }


    @URLAction(mappingId = "verRecursoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    public void retornaLista() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        String pathReal = servletContext.getContextPath();
        FacesContext.getCurrentInstance().getExternalContext().redirect(pathReal + "/faces/rh/administracaodepagamento/recursofp/lista.xhtml");
    }


    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() != null) {
            if (selecionado.getFinalVigencia() != null) {
                if (selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O inicio da vigência deve ser menor que o final da vigência.");
                }
            }
        }
        if (Operacoes.EDITAR.equals(operacao) && selecionado.getFinalVigencia() != null) {
            if (recursoFPFacade.recursoFPVinculadoVinculoVigente(selecionado, selecionado.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existem vínculos/servidores ativos com registro do Recurso FP vigente em seus cadastros.");
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            if (componenteTreeDespesaORC.getDespesaORCSelecionada() != null && componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
                selecionado.setDespesaORC(componenteTreeDespesaORC.getDespesaORCSelecionada());
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar recursoFP: {} ", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    //    public EventoFPRecurso getEventoFPRecurso() {
//        return eventoFPRecurso;
//    }
//
//    public void setEventoFPRecurso(EventoFPRecurso eventoFPRecurso) {
//        this.eventoFPRecurso = eventoFPRecurso;
//    }
    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    //    public FonteRecurso getFonteRecurso() {
//        return fonteRecurso;
//    }
//
//    public void setFonteRecurso(FonteRecurso fonteRecurso) {
//        this.fonteRecurso = fonteRecurso;
//    }
    public void addFonteRecursoFP() {
        if (validaCamposFontes()) {
            RecursoFP recursoFP = (RecursoFP) selecionado;
            fontesRecursoFP.setRecursoFP(recursoFP);

          //  fontesRecursoFP.setContaFinanceira(contaFinanceira);

            recursoFP.getFontesRecursoFPs().add(fontesRecursoFP);
            fontesRecursoFP = new FontesRecursoFP();
            fontesRecursoFP.setFonteDespesaORC(null);
            contaFinanceira = new SubConta();
        }
    }

    public boolean validaCamposFontes() {
        boolean valida = true;

        if (fontesRecursoFP == null || fontesRecursoFP.getFonteDespesaORC() == null || fontesRecursoFP.getFonteDespesaORC().getId() == null) {
            FacesUtil.addWarn("Atenção!", "Informe a Fonte de Recurso.");
            valida = false;
        }

        if (contaFinanceira == null || contaFinanceira.getId() == null) {
            FacesUtil.addWarn("Atenção!", "Informe a Conta Financeira.");
            valida = false;
        }

        return valida;
    }

    public void excluirEvt(FontesRecursoFP fonte, FonteEventoFP event) {
        fonte.getFonteEventoFPs().remove(event);
    }

    public void excluirFonte(FontesRecursoFP fonte) {
        selecionado.getFontesRecursoFPs().remove(fonte);
    }

    public List<FonteEventoFP> fontesEventosFPs(FontesRecursoFP fontes) {
        return fontes.getFonteEventoFPs();
    }

    public boolean validaEventoFP() {
        boolean valida = true;
        for (FonteEventoFP f : fontesRecursoFP.getFonteEventoFPs()) {
            if (f.getEventoFP().equals(fonteEventoFP.getEventoFP())) {
                valida = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não é permitido inserir mais de um item com o mesmo EventoFP"));
            }
        }
        return valida;
    }

    public void addFonteEventoFonte() {
        if (fontesRecursoFP != null) {
            if (fonteEventoFP != null && fonteEventoFP.getCredor() != null && fonteEventoFP.getClasseCredor() != null && fonteEventoFP.getEventoFP() != null) {
                if (validaEventoFP()) {
                    fonteEventoFP.setFontesRecursoFP(fontesRecursoFP);
                    fontesRecursoFP.getFonteEventoFPs().add(fonteEventoFP);
                    fonteEventoFP = new FonteEventoFP();
                }
            } else {
                FacesUtil.addMessageWarn("Atenção", "Existe campos sem preenchimento.");
            }
        } else {
            FacesUtil.addMessageWarn("Atenção", "Problema grave.");
        }
    }

    public void salvaItem() {
        tipo = Tipo.PRINCIPAL;
        fonteEventoFP = new FonteEventoFP();
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return pessoaJuridicaFacade.listaFiltrandoPorPerfil(PerfilEnum.PERFIL_CREDOR, parte.trim());
    }

    public Converter getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterPessoaJuridica;
    }

    public FontesRecursoFP getFontesRecursoFP() {
        return fontesRecursoFP;
    }

    public void setFontesRecursoFP(FontesRecursoFP fontesRecursoFP) {
        this.fontesRecursoFP = fontesRecursoFP;
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        if (fonteEventoFPSelecionado.getDespesaORC() != null) {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim(), fonteEventoFPSelecionado.getDespesaORC());
        } else {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim());
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, empenhoFacade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<Conta> completaContaExtraorcamentaria(String parte) {
        return contaFacade.listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterContaExtraorcamentaria() {
        if (converterContaExtraorcamentaria == null) {
            converterContaExtraorcamentaria = new ConverterAutoComplete(Conta.class, pagamentoFacade);
        }
        return converterContaExtraorcamentaria;
    }

    public void validaCategoriaContaExtra(FacesContext context, UIComponent component, Object value) {
        FacesMessage msg = new FacesMessage();
        Conta cc = (Conta) value;
        cc = pagamentoFacade.getContaFacade().recuperar(cc.getId());
        if (cc.getCategoria().equals(CategoriaConta.SINTETICA)) {
            msg.setDetail("Conta Sintética! Não poderá ser utilizada!");
            msg.setSummary("Conta Sintética! Não poderá ser utilizada!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public FonteEventoFP getFonteEventoFP() {
        return fonteEventoFP;
    }

    public void setFonteEventoFP(FonteEventoFP fonteEventoFP) {
        this.fonteEventoFP = fonteEventoFP;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void editar(FonteEventoFP fonteE) {
        fonteEventoFP = fonteE;
        tipo = Tipo.EDITAR;
        listaClasse = empenhoFacade.getClasseCredorFacade().listaClassePorPessoa(fonteEventoFP.getCredor());
    }

    public void salvarEditar(FonteEventoFP fonte) {
        tipo = Tipo.PRINCIPAL;
    }

    public void cancelarAcao() {
        tipo = Tipo.PRINCIPAL;
    }

    public List<SubConta> completaSubContas(String parte) {
        if (componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
            return pagamentoFacade.getSubContaFacade().listaPorContaEUnidadeOrganizacional(parte.trim(), componenteTreeDespesaORC.getUnidade().getSubordinada());
        } else {
            return new ArrayList<SubConta>();
        }
    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, pagamentoFacade);
        }
        return converterSubConta;
    }

    public enum Tipo {

        PRINCIPAL,
        EDITAR,
        EVENTO;
    }

    public ConverterGenerico getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterGenerico(ClasseCredor.class, empenhoFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public List<SelectItem> getListaClasseCredor() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));
        if (listaClasse != null && !listaClasse.isEmpty()) {
            for (ClasseCredor cc : listaClasse) {
                list.add(new SelectItem(cc));
            }
        }
        return list;
    }

    public void setPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
        listaClasse = empenhoFacade.getClasseCredorFacade().listaClassePorPessoa(p);
    }

    public List<Conta> completaContaDespesaDesdobrada(String parte) {
        if (fonteEventoFPSelecionado.getDespesaORC() != null) {
            return contaFacade.listaContasFilhasDespesaORC(parte, fonteEventoFPSelecionado.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), fonteEventoFPSelecionado.getDespesaORC().getExercicio());
        } else {
            return new ArrayList<Conta>();
        }
    }

    public ConverterAutoComplete getConverterContaDespesaDesdobrada() {
        if (converterContaDespesaDesdobrada == null) {
            converterContaDespesaDesdobrada = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterContaDespesaDesdobrada;
    }

    public UnidadeOrganizacional getUnidadeContaFinanceira() {
        if (componenteTreeDespesaORC != null) {
            if (componenteTreeDespesaORC.getDespesaORCSelecionada() != null) {
                if (componenteTreeDespesaORC.getDespesaORCSelecionada().getProvisaoPPADespesa() != null) {
                    return componenteTreeDespesaORC.getDespesaORCSelecionada().getProvisaoPPADespesa().getUnidadeOrganizacional();

                }
            }
        }
        return null;
    }

    public ContaDeDestinacao getContaDeDestinacaoContaFinanceira() {
        if (getFontesRecursoFP().getFonteDespesaORC() == null) {
            return null;
        }
        return (ContaDeDestinacao) getFontesRecursoFP().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
    }

    public List<RecursoFP> completaRecursoFP(String parte) {
        return recursoFPFacade.listaRecursosFP(parte, UtilRH.getDataOperacao());
    }

    public ConverterAutoComplete getConverterRecursoFP() {
        if (converterRecursoFP == null) {
            converterRecursoFP = new ConverterAutoComplete(RecursoFP.class, recursoFPFacade);
        }
        return converterRecursoFP;
    }

    public void confirmarFonte() {
        bloquearBotao = false;
        try {
            validarCamposFontes();
            Util.adicionarObjetoEmLista(selecionado.getFontesRecursoFPs(), fontesRecursoFPSelecionado);
            fontesRecursoFPSelecionado = null;
            novoEditarFonte = false;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        }

    }

    public void validarCamposFontes(){

        ValidacaoException onpe = new ValidacaoException();
        if (fontesRecursoFPSelecionado.getInicioVigencia() == null) {
            onpe.adicionarMensagemDeOperacaoNaoPermitida("Informe o início de vigência!");
        }
        if (selecionado.getFontesRecursoFPs() != null) {
            if (fontesRecursoFPSelecionado.getFinalVigencia() != null && fontesRecursoFPSelecionado.getInicioVigencia().after(fontesRecursoFPSelecionado.getFinalVigencia())) {
                onpe.adicionarMensagemDeOperacaoNaoPermitida("O campo Início de Vigência deve ser anterior ao Final de Vigência!");
            }
            for (FontesRecursoFP fonte : selecionado.getFontesRecursoFPs()) {
                if (!fonte.equals(fontesRecursoFPSelecionado) && ((fonte.getFinalVigencia() != null && fontesRecursoFPSelecionado.getFinalVigencia() == null) &&
                    (fontesRecursoFPSelecionado.getInicioVigencia().before(fonte.getFinalVigencia()) &&
                        fontesRecursoFPSelecionado.getInicioVigencia().before(fonte.getInicioVigencia())) ||
                    (fonte.getFinalVigencia() == null && fontesRecursoFPSelecionado.getFinalVigencia() != null) &&
                        (fontesRecursoFPSelecionado.getFinalVigencia().after(fonte.getInicioVigencia()) &&
                            fontesRecursoFPSelecionado.getFinalVigencia().after(fonte.getInicioVigencia())) ||
                    (fonte.getFinalVigencia() == null && fontesRecursoFPSelecionado.getFinalVigencia() == null)
                )) {
                    onpe.adicionarMensagemDeOperacaoNaoPermitida("Já possui Fonte vigênte!");
                }
            }
        }
        onpe.lancarException();
    }

    public void confirmarCloneFonte() {
        try {
            validarCamposFontes();
            Util.adicionarObjetoEmLista(selecionado.getFontesRecursoFPs(), fontesRecursoFPSelecionado);
            fontesRecursoFPSelecionado = null;

        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        }

    }

    public void novaFonte() {
        bloquearBotao = true;
        novoEditarFonte = true;
        fontesRecursoFPSelecionado = new FontesRecursoFP();
        fontesRecursoFPSelecionado.setRecursoFP(selecionado);
    }

    public void cancelarFonte() {
        if (fontesRecursoFPSelecionado.isOperacaoEditar()) {
            confirmarFonte();
        }
        bloquearBotao = false;
        fontesRecursoFPSelecionado = null;
        novoEditarFonte = false;
    }

    public void selecionarFonteParaClonar(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = recursoFPFacade.clonarFonteRecursoFP(fonte, sistemaControlador.getDataOperacao());
        FacesUtil.executaJavaScript("dialogclonefonte.show()");
    }

    public void selecionarFonteParaEdicao(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = (FontesRecursoFP) Util.clonarObjeto(fonte);
        novoEditarFonte = true;
        fontesRecursoFPSelecionado.setOperacao(Operacoes.EDITAR);
        bloquearBotao = true;
    }

    public void selecionarFonteParaVisualizar(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = fonte;
    }


    public void validarEventoFP(){
        ValidacaoException coe = new ValidacaoException();
        if (fonteEventoFPSelecionado.getTipoContabilizacao() == null) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("Selecione o tipo de Contabilização.");
        }
        if (fonteEventoFPSelecionado.getDespesaORC() == null && (empenho || variacao || (!realizavel && !retencao))) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Código reduzido deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getFonteDespesaORC() == null && (empenho || variacao || realizavel && !retencao)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Fonte de Recurso deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getDesdobramento() == null && (empenho || variacao && !retencao && !realizavel)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Fonte de Recurso deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getSubConta() == null && (empenho && !variacao && !realizavel && !retencao)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Conta Financeira/Sub Conta deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getEventoFP() == null && ((empenho || retencao || realizavel) && !variacao)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Evento FP deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getFornecedor() == null && (empenho && !retencao && !variacao && !realizavel)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Pessoa/Fornecedor deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getCredor() == null && (retencao || realizavel && !variacao && !empenho)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Credor deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getTipoContribuicao() == null && (empenho && !retencao && !variacao && !realizavel)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Constribuição deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getContaExtraorcamentaria() == null && (retencao || realizavel && !variacao && !empenho)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Conta Extra Orçamentária deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getOperacaoFormula() == null && (retencao || realizavel || variacao || empenho)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Operação deve ser informado.");
        }
        if (fonteEventoFPSelecionado.getEventoFP() != null && !fontesRecursoFPSelecionado.getFonteEventoFPs().isEmpty()) {
            for (FonteEventoFP f : fontesRecursoFPSelecionado.getFonteEventoFPs()) {
                if (f.getEventoFP() != null && !f.equals(fonteEventoFPSelecionado) && f.getEventoFP().equals(fonteEventoFPSelecionado.getEventoFP())) {
                    coe.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido inserir mais de um item com o mesmo evento!");
                }
            }
        }
        coe.lancarException();
    }

    public boolean isBloquearBotao() {
        return bloquearBotao;
    }

    public boolean isEmpenho() {
        return empenho;
    }

    public boolean isRetencao() {
        return retencao;
    }

    public boolean isRealizavel() {
        return realizavel;
    }

    public boolean isVariacao() {
        return variacao;
    }

    public boolean isNovoEditarFonte() {
        return novoEditarFonte;
    }

    public void setNovoEditarFonte(boolean novoEditarFonte) {
        this.novoEditarFonte = novoEditarFonte;
    }

    public void novoEvento(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = fonte;
        fonteEventoFPSelecionado = new FonteEventoFP();
        fonteEventoFPSelecionado.setFontesRecursoFP(fontesRecursoFPSelecionado);
        bloquearBotao = true;
    }

    public void ocultarCampos() {
        if (fonteEventoFPSelecionado == null || fonteEventoFPSelecionado.getTipoContabilizacao() == null) {
            empenho = false;
            retencao = false;
            realizavel = false;
            variacao = false;
        } else {
            switch (fonteEventoFPSelecionado.getTipoContabilizacao()) {
                case EMPENHO:
                    empenho = true;
                    retencao = false;
                    realizavel = false;
                    variacao = false;
                    break;
                case RETENCAO_EXTRAORCAMENTARIA_CONSIGNACOES:
                    empenho = false;
                    retencao = true;
                    realizavel = false;
                    variacao = false;
                    break;
                default:
                    empenho = true;
                    retencao = true;
                    realizavel = true;
                    variacao = true;
            }
        }
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
    }

    public void preencherFonteRecursoPelaConta() {
        if (fonteEventoFPSelecionado != null && contaDeDestinacao != null) {
        }
    }

    public void confirmarEvento() {
        try {
            validarEventoFP();
            Util.adicionarObjetoEmLista(fontesRecursoFPSelecionado.getFonteEventoFPs(), fonteEventoFPSelecionado);
            fonteEventoFPSelecionado = null;
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        }
        bloquearBotao = false;
    }

    public void cancelarEvento() {
        if (fonteEventoFPSelecionado.isOperacaoEditar()) {
            confirmarEvento();
        }
        fonteEventoFPSelecionado = null;
        bloquearBotao = false;
    }

    public void selecionarEvento(FonteEventoFP evento) {
        fonteEventoFPSelecionado = (FonteEventoFP) Util.clonarObjeto(evento);
        ocultarCampos();
        fonteEventoFPSelecionado.setOperacao(Operacoes.EDITAR);
        bloquearBotao = true;
    }

    public Boolean desabilitaEditarEventos() {
        return fonteEventoFPSelecionado != null && Operacoes.EDITAR.equals(fonteEventoFPSelecionado.getOperacao());
    }

    public void removerEvento(FonteEventoFP evento) {
        fontesRecursoFPSelecionado.getFonteEventoFPs().remove(evento);
    }

    public List<SelectItem> getTiposContabilizacao() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));

        for (TipoContabilizacao tipo : TipoContabilizacao.values()) {
            list.add(new SelectItem(tipo, tipo.getCodigo() + " - " + tipo.getDescricao()));
        }
        return list;
    }

    public Boolean getVerificaEdicao() {
        return !isOperacaoNovo() && !isOperacaoEditar();
    }

    public void preencherFonteRecurso() {
        if (fonteEventoFPSelecionado != null && fonteEventoFPSelecionado.getFonteDespesaORC() != null) {
            fonteEventoFPSelecionado.setFonteDespesaORC(fonteEventoFPSelecionado.getFonteDespesaORC());
        }
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public List<SelectItem> getTiposContribuicoes() {
        return Util.getListSelectItem(TipoContribuicao.values(), false);
    }



    public FontesRecursoFP getFontesRecursoFPSelecionado() {
        return fontesRecursoFPSelecionado;
    }

    public void setFontesRecursoFPSelecionado(FontesRecursoFP fontesRecursoFPSelecionado) {
        this.fontesRecursoFPSelecionado = fontesRecursoFPSelecionado;
    }

    public FonteEventoFP getFonteEventoFPSelecionado() {
        return fonteEventoFPSelecionado;
    }

    public void setFonteEventoFPSelecionado(FonteEventoFP fonteEventoFPSelecionado) {
        this.fonteEventoFPSelecionado = fonteEventoFPSelecionado;
    }
}
