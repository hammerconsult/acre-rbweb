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
import com.google.common.collect.Lists;
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
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
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

    private ConverterAutoComplete converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;

    private FonteDespesaORC fonteDespesaORC;
    private EventoFP eventoFP;
    private OperacaoFormula operacaoFormula;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    private FontesRecursoFP fontesRecursoFPSelecionado;
    private FontesRecursoFP fontesRecursoFP;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterConta;
    private ConverterAutoComplete converterDetalhamento;
    private FonteEventoFP fonteEventoFPSelecionado;
    @EJB
    private ContaFacade contaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaExtraorcamentaria;
    private ConverterAutoComplete converterSubConta;
    private ConverterGenerico converterClasseCredor;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    private ConverterAutoComplete converterContaDespesaDesdobrada;
    private ConverterAutoComplete converterRecursoFP;
    private ConverterAutoComplete converterVinculoFP;

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
    private GrupoRecursoFP grupoRecursoFP;
    private AgrupamentoRecursoFP agrupamentoRecursoFP;
    private ConverterAutoComplete converterGrupoRecursoFP;

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

    public boolean isMostrarTabelaDeEventos() {
        return selecionado.getFontesRecursoFPs() == null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isNovoEditarFonte() {
        return novoEditarFonte;
    }

    public void setNovoEditarFonte(boolean novoEditarFonte) {
        this.novoEditarFonte = novoEditarFonte;
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoFPFacade;
    }

    public void removerFonte(FontesRecursoFP fonte) {
        selecionado.getFontesRecursoFPs().remove(fonte);
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
        novoEditarFonte = false;
        ocultarCampos();
    }

    @URLAction(mappingId = "editarRecursoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        ocultarCampos();
    }

    @URLAction(mappingId = "verRecursoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void validarInicioVigencia(){
        ValidacaoException onpe = new ValidacaoException();
        if (selecionado.getInicioVigencia() != null) {
            if (selecionado.getFinalVigencia() != null) {
                if (selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                    onpe.adicionarMensagemDeOperacaoNaoPermitida("O inicio da vigência deve ser anterior ao final da vigência!");
                }
            }
        }
        onpe.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            validarInicioVigencia();
            super.salvar();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar recursoFP: {} ", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Operacoes.EDITAR.equals(operacao) && selecionado.getFinalVigencia() != null) {
            if (recursoFPFacade.recursoFPVinculadoVinculoVigente(selecionado, selecionado.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existem vínculos/servidores ativos com registro do Recurso FP vigente em seus cadastros.");
            }
        }
        ve.lancarException();
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public ConverterAutoComplete getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
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

    public void confirmarCloneFonte() {
        try {
            validarCamposFontes();
            Util.adicionarObjetoEmLista(selecionado.getFontesRecursoFPs(), fontesRecursoFPSelecionado);
            fontesRecursoFPSelecionado = null;

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

    public void preencherFonteRecurso() {
        if (fonteEventoFPSelecionado != null && fonteEventoFPSelecionado.getFonteDespesaORC() != null) {
            fonteEventoFPSelecionado.setFonteDespesaORC(fonteEventoFPSelecionado.getFonteDespesaORC());
        }
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

    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposContribuicoes() {
        return Util.getListSelectItem(TipoContribuicao.values(), false);
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

    public FontesRecursoFP getFontesRecursoFPSelecionado() {
        return fontesRecursoFPSelecionado;
    }

    public void setFontesRecursoFPSelecionado(FontesRecursoFP fontesRecursoFPSelecionado) {
        this.fontesRecursoFPSelecionado = fontesRecursoFPSelecionado;
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        if (fonteEventoFPSelecionado.getDespesaORC() != null) {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim(), fonteEventoFPSelecionado.getDespesaORC());
        } else {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim());
        }
    }

    public List<FonteDespesaORC> completaFonteDespesaORCSelecionado(String parte) {
        if (selecionado.getDespesaORC() != null && selecionado.getDespesaORC().getId() != null) {
            return fonteDespesaORCFacade.completaFonteDespesaORC(parte.trim(), selecionado.getDespesaORC());
        } else {
            return Lists.newArrayList();
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, fonteDespesaORCFacade);
        }
        return converterFonteDespesaORC;
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
    }

    public void atribuitFonteDespesa(SelectEvent evt) {
        FonteDespesaORC fdo = (FonteDespesaORC) evt.getObject();
        buscaContaPorFonteDespesaORC(fdo);
    }

    public void buscaContaPorFonteDespesaORC(FonteDespesaORC evt) {
        Conta c = evt.getProvisaoPPAFonte().getDestinacaoDeRecursos();
    }

    public List<SelectItem> getTiposContabilizacao() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));

        for (TipoContabilizacao tipo : TipoContabilizacao.values()) {
            list.add(new SelectItem(tipo, tipo.getCodigo() + " - " + tipo.getDescricao()));
        }
        return list;
    }


    public List<Conta> completaContaExtraorcamentaria(String parte) {
        return contaFacade.listaFiltrandoExtraorcamentario(parte.trim(), sistemaControlador.getExercicioCorrente());
    }


    public ConverterAutoComplete getConverterContaExtraorcamentaria() {
        if (converterContaExtraorcamentaria == null) {
            converterContaExtraorcamentaria = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterContaExtraorcamentaria;
    }

    public void validaCategoriaContaExtra(FacesContext context, UIComponent component, Object value) {
        FacesMessage msg = new FacesMessage();
        Conta cc = (Conta) value;
        cc = contaFacade.recuperar(cc.getId());
        if (cc.getCategoria().equals(CategoriaConta.SINTETICA)) {
            msg.setDetail("Conta Sintética! Não poderá ser utilizada!");
            msg.setSummary("Conta Sintética! Não poderá ser utilizada!");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
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

    public FonteEventoFP getFonteEventoFPSelecionado() {
        return fonteEventoFPSelecionado;
    }

    public void setFonteEventoFPSelecionado(FonteEventoFP fonteEventoFPSelecionado) {
        this.fonteEventoFPSelecionado = fonteEventoFPSelecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<SubConta> completaSubContas(String parte) {
        if (fonteEventoFPSelecionado.getDespesaORC() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo de Código Reduzido é obrigatório");
            return new ArrayList<SubConta>();
        }
        return new ArrayList<SubConta>();

    }

    public ConverterAutoComplete getConverterSubConta() {
        if (converterSubConta == null) {
            converterSubConta = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterSubConta;
    }

    public ConverterGenerico getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterGenerico(ClasseCredor.class, classeCredorFacade);
        }
        return converterClasseCredor;
    }

    public List<SelectItem> getListaClasseCredor() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));
        if (fonteEventoFPSelecionado.getCredor() != null) {
            List<ClasseCredor> classes = classeCredorFacade.listaClassePorPessoa(fonteEventoFPSelecionado.getCredor());
            for (ClasseCredor classe : classes) {
                list.add(new SelectItem(classe));
            }
        }
        return list;
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

    public FonteDeRecursos getFonteRecursoContaFinanceira() {
        if (fonteEventoFPSelecionado != null && fonteEventoFPSelecionado.getFonteDespesaORC() != null) {
            FonteDeRecursos fonteDeRecursos = ((ContaDeDestinacao) fonteEventoFPSelecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos();
            return fonteDeRecursos;
        }
        return null;
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

    public void cancelarFonte() {
        if (fontesRecursoFPSelecionado.isOperacaoEditar()) {
            confirmarFonte();
        }
        bloquearBotao = false;
        fontesRecursoFPSelecionado = null;
        novoEditarFonte = false;
    }


    public void selecionarFonteParaEventos(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = fonte;
        index = 2;
    }

    private void removerFonteRecursoFP(FontesRecursoFP fonte) {
        if (selecionado.getFontesRecursoFPs().contains(fonte)) {
            selecionado.getFontesRecursoFPs().remove(fonte);
        }
    }

    public void fecharDialogEvento() {
        if (fonteEventoFPSelecionado != null) {
            FacesUtil.addAtencao("Há evento sendo editado ou adicionado. Confirme ou cancele a operação!");
        } else {
            FacesUtil.executaJavaScript("dialogEvento.hide()");
            fontesRecursoFPSelecionado = null;
        }
    }

    public void selecionarEvento(FonteEventoFP evento) {
        fonteEventoFPSelecionado = (FonteEventoFP) Util.clonarObjeto(evento);
        ocultarCampos();
        fonteEventoFPSelecionado.setOperacao(Operacoes.EDITAR);
        bloquearBotao = true;
    }

    public void novoAgrupamento() {
        agrupamentoRecursoFP = new AgrupamentoRecursoFP();
    }

    public void cancelarAgrupamento() {
        grupoRecursoFP = null;
        agrupamentoRecursoFP = null;
    }

    private void validarGrupoRecursoFP() {
        ValidacaoException coe = new ValidacaoException();
        if (grupoRecursoFP == null) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo grupo de Recurso fp deve ser informado.");
        }
        coe.lancarException();
    }

    public boolean isFonteConsignacao() {
        return fonteConsignacao;
    }

    public boolean isFonteEmpenho() {
        return fonteEmpenho;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public GrupoRecursoFP getGrupoRecursoFP() {
        return grupoRecursoFP;
    }

    public void setGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        this.grupoRecursoFP = grupoRecursoFP;
    }

    public AgrupamentoRecursoFP getAgrupamentoRecursoFP() {
        return agrupamentoRecursoFP;
    }

    public void setAgrupamentoRecursoFP(AgrupamentoRecursoFP agrupamentoRecursoFP) {
        this.agrupamentoRecursoFP = agrupamentoRecursoFP;
    }

    public void novaFonte() {
        bloquearBotao = true;
        novoEditarFonte = true;
        fontesRecursoFPSelecionado = new FontesRecursoFP();
        fontesRecursoFPSelecionado.setRecursoFP(selecionado);
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


    public void novoEvento(FontesRecursoFP fonte) {
        fontesRecursoFPSelecionado = fonte;
        fonteEventoFPSelecionado = new FonteEventoFP();
        fonteEventoFPSelecionado.setFontesRecursoFP(fontesRecursoFPSelecionado);
        bloquearBotao = true;
    }

    public Boolean desabilitaEditarEventos() {
        return fonteEventoFPSelecionado != null && Operacoes.EDITAR.equals(fonteEventoFPSelecionado.getOperacao());
    }

    public void removerEvento(FonteEventoFP evento) {
        fontesRecursoFPSelecionado.getFonteEventoFPs().remove(evento);
    }

    public Boolean getVerificaEdicao() {
        return !isOperacaoNovo() && !isOperacaoEditar();
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

}
