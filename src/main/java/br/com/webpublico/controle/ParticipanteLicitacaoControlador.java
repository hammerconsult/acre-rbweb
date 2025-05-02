/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.ParticipanteLicitacaoFacade;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.hibernate.LazyInitializationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@ManagedBean(name = "participanteLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-participante-licitacao", pattern = "/licitacao/participantes/novo/", viewId = "/faces/administrativo/licitacao/participantesdelicitacao/edita.xhtml"),
    @URLMapping(id = "ver-participante-licitacao", pattern = "/licitacao/participantes/ver/#{participanteLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/participantesdelicitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-participante-licitacao", pattern = "/licitacao/participantes/listar/", viewId = "/faces/administrativo/licitacao/participantesdelicitacao/lista.xhtml")
})
public class ParticipanteLicitacaoControlador extends PrettyControlador<LicitacaoFornecedor> implements Serializable, CRUD {

    public static final String TRIBUTARIO_CONFIGURACOES_PESSOA = "/tributario/configuracoes/pessoa/";
    @EJB
    private ParticipanteLicitacaoFacade participanteLicitacaoFacade;
    private Licitacao licitacaoSelecionada;
    private LicitacaoFornecedor licitacaoFornecedorSelecionado;
    private DoctoHabilitacao documentoHabilitacaoVisualizacaoFornecedor;
    private LicitacaoDoctoFornecedor licitacaoDoctoFornecedorSelecionado;
    private List<LicitacaoFornecedor> listaDeLicitacaoFornecedor;
    private StatusLicitacao statusLicitacao;
    private TipoControlador tipoControlador;
    private TipoEmpresa tipoEmpresaSelecionada;
    private Boolean documentoVencidoHabilitarComRessalva;
    private Boolean abilitarBotaoParticipante;
    private Boolean novoFornecedor;
    private RetiradaEdital[] retiradaEditals;
    private RepresentanteLicitacao representanteLicitacao;

    public ParticipanteLicitacaoControlador() {
        super(LicitacaoFornecedor.class);
    }

    public List<LicitacaoFornecedor> getListaDeLicitacaoFornecedor() {
        return listaDeLicitacaoFornecedor;
    }

    public void setListaDeLicitacaoFornecedor(List<LicitacaoFornecedor> listaDeLicitacaoFornecedor) {
        this.listaDeLicitacaoFornecedor = listaDeLicitacaoFornecedor;
    }

    public LicitacaoDoctoFornecedor getLicitacaoDoctoFornecedorSelecionado() {
        return licitacaoDoctoFornecedorSelecionado;
    }

    public void setLicitacaoDoctoFornecedorSelecionado(LicitacaoDoctoFornecedor licitacaoDoctoFornecedorSelecionado) {
        this.licitacaoDoctoFornecedorSelecionado = licitacaoDoctoFornecedorSelecionado;
    }

    public DoctoHabilitacao getDocumentoHabilitacaoVisualizacaoFornecedor() {
        return documentoHabilitacaoVisualizacaoFornecedor;
    }

    public void setDocumentoHabilitacaoVisualizacaoFornecedor(DoctoHabilitacao documentoHabilitacaoVisualizacaoFornecedor) {
        this.documentoHabilitacaoVisualizacaoFornecedor = documentoHabilitacaoVisualizacaoFornecedor;
    }

    public LicitacaoFornecedor getLicitacaoFornecedorSelecionado() {
        return licitacaoFornecedorSelecionado;
    }

    public void setLicitacaoFornecedorSelecionado(LicitacaoFornecedor licitacaoFornecedorSelecionado) {
        this.licitacaoFornecedorSelecionado = licitacaoFornecedorSelecionado;
    }

    public TipoEmpresa getTipoEmpresaSelecionada() {
        return tipoEmpresaSelecionada;
    }

    public void setTipoEmpresaSelecionada(TipoEmpresa tipoEmpresaSelecionada) {
        this.tipoEmpresaSelecionada = tipoEmpresaSelecionada;
    }

    public Boolean getDocumentoVencidoHabilitarComRessalva() {
        return documentoVencidoHabilitarComRessalva;
    }

    public void setDocumentoVencidoHabilitarComRessalva(Boolean documentoVencidoHabilitarComRessalva) {
        this.documentoVencidoHabilitarComRessalva = documentoVencidoHabilitarComRessalva;
    }

    public RetiradaEdital[] getRetiradaEditals() {
        return retiradaEditals;
    }

    public void setRetiradaEditals(RetiradaEdital[] retiradaEditals) {
        this.retiradaEditals = retiradaEditals;
    }

    public Licitacao getLicitacaoSelecionada() {
        try {
            if (licitacaoSelecionada != null) {
                licitacaoSelecionada.getListaDeDoctoHabilitacao();
            }
        } catch (LazyInitializationException lazy) {
            licitacaoSelecionada.setDocumentosProcesso(participanteLicitacaoFacade.getLicitacaoFacade().recuperaDoctosHabLicitacao(licitacaoSelecionada));
        }

        return licitacaoSelecionada;
    }

    public void setLicitacaoSelecionada(Licitacao licitacaoSelecionada) {
        this.licitacaoSelecionada = licitacaoSelecionada;
    }

    @URLAction(mappingId = "novo-participante-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        Licitacao licitacao = (Licitacao) Web.pegaDaSessao(Licitacao.class);
        PessoaFisica pf = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        PessoaJuridica pj = (PessoaJuridica) Web.pegaDaSessao(PessoaJuridica.class);
        LicitacaoFornecedor licitacaoFornecedor = (LicitacaoFornecedor) Web.pegaDaSessao(LicitacaoFornecedor.class);

        super.novo();
        tipoControlador = TipoControlador.ADICIONAR_FORNECEDOR;
        parametrosIniciais();
        novoFornecedor = (Boolean) Web.pegaDaSessao(Boolean.class);
        colocarNaSessao(licitacao, pf, pj, licitacaoFornecedor);
    }

    @URLAction(mappingId = "ver-participante-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        licitacaoSelecionada = selecionado.getLicitacao();
    }

    private void colocarNaSessao(Licitacao licitacao, PessoaFisica pf, PessoaJuridica pj, LicitacaoFornecedor licitacaoFornecedor) {
        if (licitacao != null) {
            setLicitacaoSelecionada(licitacao);
            carregarLicitacaoSelecionada();
        }
        if (licitacaoFornecedor != null) {
            licitacaoFornecedorSelecionado = licitacaoFornecedor;
            if (pf != null) {
                getLicitacaoFornecedorSelecionado().setEmpresa(pf);
                novoFornecedor = Boolean.TRUE;
            }
            if (pj != null) {
                getLicitacaoFornecedorSelecionado().setEmpresa(pj);
                novoFornecedor = Boolean.TRUE;
            }
        }
        listaDeLicitacaoFornecedor = (List<LicitacaoFornecedor>) Web.pegaDaSessao(listaDeLicitacaoFornecedor.getClass());
    }

    public void habilitarPartipante() {
        super.novo();
        tipoControlador = TipoControlador.HABILITAR_FORNECEDOR;
        parametrosIniciais();
    }

    private boolean controladorTipoAdicionarFornecedor() {
        return tipoControlador.equals(TipoControlador.ADICIONAR_FORNECEDOR);
    }

    private void parametrosIniciais() {
        licitacaoDoctoFornecedorSelecionado = null;
        listaDeLicitacaoFornecedor = Lists.newArrayList();
        documentoHabilitacaoVisualizacaoFornecedor = null;
        tipoEmpresaSelecionada = TipoEmpresa.MICRO;
        documentoVencidoHabilitarComRessalva = Boolean.FALSE;
        representanteLicitacao = new RepresentanteLicitacao();
        novoFornecedor = Boolean.FALSE;
    }

    public void navegarParaPessoaFisica() {
        Web.navegacao(getUrlAtual(),
            TRIBUTARIO_CONFIGURACOES_PESSOA + "novapessoafisica/",
            selecionado,
            licitacaoSelecionada,
            licitacaoFornecedorSelecionado,
            listaDeLicitacaoFornecedor,
            novoFornecedor);
    }

    public void navegarParaPessoaJuridica() {
        Web.navegacao(getUrlAtual(),
            TRIBUTARIO_CONFIGURACOES_PESSOA + "novapessoajuridica/",
            selecionado,
            licitacaoSelecionada,
            licitacaoFornecedorSelecionado,
            listaDeLicitacaoFornecedor,
            novoFornecedor);
    }

    public List<SelectItem> getTipoEmpresa() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(TipoEmpresa.MICRO));
        retorno.add(new SelectItem(TipoEmpresa.PEQUENA));
        retorno.add(new SelectItem(TipoEmpresa.NORMAL));
        return retorno;
    }

    private void validarCampos() {
        ValidacaoException va = new ValidacaoException();
        if (licitacaoSelecionada == null) {
            va.adicionarMensagemDeCampoObrigatorio("O campo Licitação é obrigatório.");
        }
        if (listaDeLicitacaoFornecedor == null || listaDeLicitacaoFornecedor.isEmpty()) {
            va.adicionarMensagemDeCampoObrigatorio("Nenhum fornecedor foi informado.");
        }
        if (va.temMensagens()) {
            throw va;
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            participanteLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(licitacaoSelecionada);
            for (LicitacaoFornecedor licFornecedor : listaDeLicitacaoFornecedor) {
                if (licFornecedor.getEmpresa() instanceof PessoaJuridica) {
                    participanteLicitacaoFacade.getLicitacaoFacade().salvarPessoaJuridica((PessoaJuridica) licFornecedor.getEmpresa());
                }
                if (licFornecedor.getEmpresa() instanceof PessoaFisica) {
                    participanteLicitacaoFacade.getLicitacaoFacade().salvarPessoaFisica((PessoaFisica) licFornecedor.getEmpresa());
                }
            }

            licitacaoSelecionada = participanteLicitacaoFacade.getLicitacaoFacade().recuperar(licitacaoSelecionada.getId());
            licitacaoSelecionada.setFornecedores(listaDeLicitacaoFornecedor);
            participanteLicitacaoFacade.getLicitacaoFacade().salvarLicitacao(licitacaoSelecionada);
            redireciona();
            FacesUtil.addOperacaoRealizada(" A Licitação " + licitacaoSelecionada.toStringAutoComplete() + " com o(s) " + licitacaoSelecionada.getFornecedores().size() + " Fornecedore(s) foi salva com sucesso!");
        } catch (StatusLicitacaoException se) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void carregarLicitacaoSelecionada() {
        licitacaoSelecionada = participanteLicitacaoFacade.getLicitacaoFacade().recuperar(licitacaoSelecionada.getId());
        statusLicitacao = licitacaoSelecionada.getStatusAtualLicitacao();
        listaDeLicitacaoFornecedor = licitacaoSelecionada.getFornecedores();
        abilitarBotaoParticipante = validaPublicacaoLicitacao();
    }

    private boolean validaPublicacaoLicitacao() {
        boolean result = true;
        List<PublicacaoLicitacao> list = licitacaoSelecionada.getPublicacoes();
        for (PublicacaoLicitacao publicacao : list) {
            if ((publicacao.getNumeroEdicaoPublicacao() == null || publicacao.getNumeroEdicaoPublicacao().isEmpty()) || (publicacao.getNumeroPagina() == null || publicacao.getNumeroPagina().isEmpty())) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A licitação " + licitacaoSelecionada.getNumero() + " possui publicações que não foram preechidos os campos 'N° da Pagina ou N° da edição'");
                result = false;
            }
        }
        return result;
    }

    public void carregarLicitacaoSelecionadaParaHabilitacao() {
        licitacaoSelecionada = participanteLicitacaoFacade.getLicitacaoFacade().recuperar(licitacaoSelecionada.getId());

        if (licitacaoSelecionada.getAbertaEm() != null) {
            if (licitacaoSelecionada.getModalidadeLicitacao().isPregao()) {
                listaDeLicitacaoFornecedor = participanteLicitacaoFacade.getLicitacaoFacade().getFornecedoresVencedoresDoPregao(licitacaoSelecionada);

                if (listaDeLicitacaoFornecedor.isEmpty()) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Esta licitação do tipo pregão não possui vencedores para serem habilitados.");
                }
            } else {
                listaDeLicitacaoFornecedor = licitacaoSelecionada.getFornecedores();
            }
        } else {
            FacesUtil.addError("Atenção", "A Licitação: " + licitacaoSelecionada + ", não possui Data de Abertura. " +
                "Por favor, informar a Data de Abertura antes de Habilitar o(s) Fornecedor(es)!");
            licitacaoSelecionada = null;
        }
    }

    public void criarNovoLicitacaoFornecedorSelecionado() {
        licitacaoFornecedorSelecionado = new LicitacaoFornecedor(licitacaoSelecionada, recuperaMaiorCodigoDaLicitacaoFornecedor());
        representanteLicitacao = new RepresentanteLicitacao();
        novoFornecedor = Boolean.TRUE;
    }

    private Integer recuperaMaiorCodigoDaLicitacaoFornecedor() {
        return participanteLicitacaoFacade.getMaiorCodigoLicitacaoFornecedor(listaDeLicitacaoFornecedor);
    }

    public void situacaoCadastralPessoaEstaValida(SelectEvent evento) {
        if (licitacaoFornecedorSelecionado.getEmpresa() != null && licitacaoFornecedorSelecionado.getEmpresa() instanceof PessoaFisica) {
            licitacaoFornecedorSelecionado.setRepresentante(null);
        }

        Pessoa pessoa = (Pessoa) evento.getObject();
        if (!pessoa.getSituacaoCadastralPessoa().equals(SituacaoCadastralPessoa.ATIVO)) {
            UIComponent campo = evento.getComponent();
            ((UIInput) campo).setValue(null);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Situação Cadastral Inválida!", "A pessoa jurídica selecionada está com situação cadastral '" + pessoa.getSituacaoCadastralPessoa() + "'. Por favor, regularize a situação!"));
        }
    }

    public boolean fornecedorSelecionadoEPessoaJuridica() {
        if (licitacaoFornecedorSelecionado != null) {
            if (licitacaoFornecedorSelecionado.getEmpresa() != null) {
                return licitacaoFornecedorSelecionado.getEmpresa() instanceof PessoaJuridica;
            }
        }

        return false;
    }

    public boolean permiteExibirJustificativaClassificacao() {
        try {
            return licitacaoFornecedorSelecionado.getTipoClassificacaoFornecedor().equals(TipoClassificacaoFornecedor.INABILITADO) ? true : false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean desabilitaBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        try {
            for (LicitacaoDoctoFornecedor licitacaoDoctoFornecedor : licitacaoFornecedorSelecionado.getDocumentosFornecedor()) {
                if (licitacaoDoctoFornecedor.getDoctoHabilitacao().equals(documentoHabilitacao)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public String tituloBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        return desabilitaBotaoAdicionarDocumentoFornecedor(documentoHabilitacao) ? "O documento já foi adicionado a lista de documentos apresentados." : "Clique para vincular este documento.";
    }

    public void visualizarDocumentoHabilitacaoFornecedor(ActionEvent evento) {
        this.documentoHabilitacaoVisualizacaoFornecedor = (DoctoHabilitacao) evento.getComponent().getAttributes().get("itemOrigem");
        this.licitacaoDoctoFornecedorSelecionado = new LicitacaoDoctoFornecedor();
        atualizarComponente("formularioDialogDocumentosFornecedor");
        executarScript("dialogDocumentosFornecedor.show()");
    }

    private void atualizarComponente(String nomeComponente) {
        RequestContext.getCurrentInstance().update(nomeComponente);
    }

    private void executarScript(String script) {
        RequestContext.getCurrentInstance().execute(script);
    }

    public void removerDocumentoDoFornecedor(ActionEvent evento) {
        LicitacaoDoctoFornecedor licitacaoDoctoFornecedor = (LicitacaoDoctoFornecedor) evento.getComponent().getAttributes().get("itemDestino");
        licitacaoFornecedorSelecionado.getDocumentosFornecedor().remove(licitacaoDoctoFornecedor);
        FacesContext.getCurrentInstance().addMessage("Formulario:accordionPanelFornecedor", new FacesMessage(FacesMessage.SEVERITY_WARN, "Documento Removido!", "O documento selecionado foi removido com sucesso."));
        executarScript("dialogDocumentosFornecedor.hide()");
    }

    public void alterarFornecedor() {
        adicionarLicitacaoFornecedor();
        documentoVencidoHabilitarComRessalva = Boolean.FALSE;
    }

    public void adicionarLicitacaoFornecedor() {
        try {
            validarFornecedor();
            adicionarSobrescreverLicitacaoFornecedor();

            if (!controladorTipoAdicionarFornecedor()) {
                habilitarFornecedorSelecionado();
            }

            criarNovoFornecedor();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void criarNovoFornecedor() {
        criarNovoLicitacaoFornecedorSelecionado();
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void validarFornecedor() {
        ValidacaoException va = new ValidacaoException();
        if (licitacaoFornecedorSelecionado.getEmpresa() == null) {
            va.adicionarMensagemDeCampoObrigatorio("Por favor, selecione um fornecedor para adicioná-lo.");
        } else {
            if (!(licitacaoSelecionada.isPregao() && licitacaoSelecionada.getNaturezaDoProcedimento() != null && licitacaoSelecionada.getNaturezaDoProcedimento().isEletronico())) {
                if (licitacaoFornecedorSelecionado.getEmpresa().isPessoaJuridica()
                    && (licitacaoFornecedorSelecionado.getRepresentante() == null
                    || Strings.isNullOrEmpty(licitacaoFornecedorSelecionado.getRepresentante().getCpf())
                    || Strings.isNullOrEmpty(licitacaoFornecedorSelecionado.getRepresentante().getNome()))) {
                    va.adicionarMensagemDeCampoObrigatorio("Por favor, informe o representante do fornecedor informado.");
                }
                if (licitacaoFornecedorSelecionado.getEmpresa().isPessoaJuridica()) {
                    if (licitacaoFornecedorSelecionado.getInstrumentoRepresentacao() == null || licitacaoFornecedorSelecionado.getInstrumentoRepresentacao().isEmpty()) {
                        va.adicionarMensagemDeCampoObrigatorio("Por favor, o campo instrumento de representação deve ser informado.");
                    }
                }
            }
            if (fornecedorJaAdicionado()) {
                va.adicionarMensagemDeOperacaoNaoPermitida("O Fornecedor selecionado já foi adicionado a esta licitação.");
            }
        }
        validarCodigo();

        if (verificarCodigoFornecedorJaAdicionado()) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O Código selecionado já foi adicionado a esta licitação.");
        }

        if (va.temMensagens()) {
            throw va;
        }
    }

    private void validarCodigo() {
        ValidacaoException va = new ValidacaoException();
        if (licitacaoFornecedorSelecionado.getCodigo() == null) {
            va.adicionarMensagemDeCampoObrigatorio("O Código deve ser informado!");
        } else if (licitacaoFornecedorSelecionado.getCodigo() <= 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser maior que 0!");
        }
        if (va.temMensagens()) {
            throw va;
        }
    }

    private boolean podeValidar(AtributoMetadata object) {
        if (!fornecedorPessoaJuridica(licitacaoFornecedorSelecionado)) {
            if (object.getEtiqueta().equalsIgnoreCase("representante")) {
                return false;
            }
        }

        return true;
    }

    private void adicionarSobrescreverLicitacaoFornecedor() {
        carregaDocumentosDeHabilitacaoDaLicitacaoSelecionada();
        licitacaoFornecedorSelecionado.habilitarFornecedor(licitacaoSelecionada.getListaDeDoctoHabilitacao(), documentoVencidoHabilitarComRessalva);
        processaClassificacaoTecnicaFornecedor();
        licitacaoFornecedorSelecionado.setRepresentante(representanteLicitacao);
        setListaDeLicitacaoFornecedor(Util.adicionarObjetoEmLista(listaDeLicitacaoFornecedor, licitacaoFornecedorSelecionado));
        adicionarMensagemNotificacaoExpiracaoTempoEntregaDocumentos();
    }

    private void carregaDocumentosDeHabilitacaoDaLicitacaoSelecionada() {
        try {
            licitacaoSelecionada.getListaDeDoctoHabilitacao();
        } catch (LazyInitializationException lazy) {
            licitacaoSelecionada.setDocumentosProcesso(participanteLicitacaoFacade.getLicitacaoFacade().recuperaDoctosHabLicitacao(licitacaoSelecionada));
        }
    }

    private void processaClassificacaoTecnicaFornecedor() {
        if (licitacaoSelecionada.isTecnicaEPreco()) {
            licitacaoFornecedorSelecionado.setClassificacaoTecnica(TipoClassificacaoFornecedor.INABILITADO);
        }
    }

    private void adicionarMensagemNotificacaoExpiracaoTempoEntregaDocumentos() {
        ConfiguracaoAdministrativa confAdm = participanteLicitacaoFacade.getLicitacaoFacade().getConfiguracaoAdministrativaFacade().recuperaParametro("DIAS_FORNECEDOR_POSSUI_REGULAMENTAR_SITUACAO");

        if (licitacaoSelecionada.getAbertaEm() == null) {
            return;
        }

        Date hoje = new Date();
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(licitacaoSelecionada.getAbertaEm());
        dataLimite.add(Calendar.DATE, Integer.parseInt(confAdm.getValor()));
        if (hoje.after(dataLimite.getTime())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "A data limite para regularização expirou.", "O sistema permitiu que o fornecedor fosse adicionado, porém a data limite para apresentação dos documentos já expirou."));
        }
    }

    private void habilitarFornecedorSelecionado() {
        boolean habilitou = licitacaoFornecedorSelecionado.habilitarFornecedor(recuperarTodosDocumentosDestaLicitacao(), documentoVencidoHabilitarComRessalva);
        if (!habilitou) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fornecedor inabilitado!", "Verifique a validade dos documentos, ou se todos os requeridos foram apresentados."));
        }
    }

    private List<DoctoHabilitacao> recuperarTodosDocumentosDestaLicitacao() {
        ArrayList<DoctoHabilitacao> listaDeRetorno = new ArrayList<DoctoHabilitacao>();
        try {
            for (DoctoHabLicitacao doctoHabLicitacaoDaVez : licitacaoSelecionada.getDocumentosProcesso()) {
                if (doctoHabLicitacaoDaVez.getDoctoHabilitacao() != null) {
                    listaDeRetorno.add(doctoHabLicitacaoDaVez.getDoctoHabilitacao());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return listaDeRetorno;
    }

    private Boolean fornecedorJaAdicionado() {
        for (LicitacaoFornecedor licitacaoFornecedor : listaDeLicitacaoFornecedor) {
            if (licitacaoFornecedor.getEmpresa().equals(licitacaoFornecedorSelecionado.getEmpresa()) && !listaDeLicitacaoFornecedor.contains(licitacaoFornecedorSelecionado)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean verificarCodigoFornecedorJaAdicionado() {
        for (LicitacaoFornecedor licitacaoFornecedor : listaDeLicitacaoFornecedor) {
            if (licitacaoFornecedor.getCodigo().equals(licitacaoFornecedorSelecionado.getCodigo()) && !listaDeLicitacaoFornecedor.contains(licitacaoFornecedorSelecionado)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    public void tornarNullLicitacaoFornecedorSelecionado() {
        this.licitacaoFornecedorSelecionado = null;
    }

    public boolean fornecedorPessoaJuridica(LicitacaoFornecedor licForn) {
        if (licForn.getEmpresa() instanceof PessoaJuridica) {
            return true;
        }

        return false;
    }

    public void selecionarLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        licitacaoFornecedorSelecionado = (LicitacaoFornecedor) Util.clonarObjeto(licitacaoFornecedor);
        if(licitacaoFornecedor.getRepresentante() != null){
            representanteLicitacao = licitacaoFornecedor.getRepresentante();
        }
        novoFornecedor = Boolean.TRUE;
    }

    public void removerLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        licitacaoFornecedorSelecionado = licitacaoFornecedor;
        if (licitacaoFornecedorSelecionado != null) {
            listaDeLicitacaoFornecedor.remove(licitacaoFornecedorSelecionado);
            criarNovoLicitacaoFornecedorSelecionado();
        }
    }

    public String montarStringValidadeDocumentoFornecedor(LicitacaoDoctoFornecedor licitacaoDoctoFornecedor) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").format(licitacaoDoctoFornecedor.getDataDeValidade());
        } catch (Exception e) {
            return "";
        }
    }

    public void adicionarDocumentoAoFornecedor() {
        if (!validarDocumentoHabilitacaoSelecionado()) {
            atualizarComponente("formularioDialogDocumentosFornecedor");
            return;
        }

        if (validarDocumentoVencido()) {
            if (licitacaoFornecedorSelecionado.getEmpresa() instanceof PessoaJuridica) {
                // Documento vencido e empresa do Tipo MICRO ou PEQUENA.
                if (TipoEmpresa.MICRO.equals(((PessoaJuridica) licitacaoFornecedorSelecionado.getEmpresa()).getTipoEmpresa())
                    || TipoEmpresa.PEQUENA.equals(((PessoaJuridica) licitacaoFornecedorSelecionado.getEmpresa()).getTipoEmpresa())) {
                    informaPrazoParaRegularizarDocumentacao(licitacaoFornecedorSelecionado.getEmpresa().getNome());
                    criarVinculosLicitacoesDocumentos();
                    concluirVinculosLicitacoesDocumentos();
                    // Habilitar com ressalva.
                    documentoVencidoHabilitarComRessalva = Boolean.TRUE;
                    return;
                }
                // Documento vencido e empresa do Tipo INDEFINIDO.
                if (TipoEmpresa.INDEFINIDO.equals(((PessoaJuridica) licitacaoFornecedorSelecionado.getEmpresa()).getTipoEmpresa())) {
                    executarScript("dialogTipoEmpresa.show()");
                    executarScript("dialogDocumentosFornecedor.hide()");
                    criarVinculosLicitacoesDocumentos();
                    // Habilitar com ressalva.
                    documentoVencidoHabilitarComRessalva = Boolean.TRUE;
                    return;
                }
                // Documento vencido e empresa do Tipo NORMAL ou GRANDE, ou seja, nenhum dos casos acima. Inabilitar Fornecedor
                licitacaoDoctoFornecedorSelecionado.setDocumentoVencido(Boolean.TRUE);
            }
            if (licitacaoFornecedorSelecionado.getEmpresa() instanceof PessoaFisica) {
                // Documento vencido e Pessoa Física. Inabilitar o fornecedor.
                licitacaoDoctoFornecedorSelecionado.setDocumentoVencido(Boolean.TRUE);
                FacesUtil.addInfo("Validade Expirada.", "Este documento venceu antes da data de abertura da licitação. O documento será aceito, porém o fornecedor será inabilitado.");
            }
        }

        criarVinculosLicitacoesDocumentos();
        concluirVinculosLicitacoesDocumentos();
    }

    private void informaPrazoParaRegularizarDocumentacao(String nomeFornecedor) {
        FacesUtil.addInfo("Atenção", "O fornecedor " + nomeFornecedor + " tem o prazo de 03 (três) dias para regularizar o documento. O fornecedor será habilitado com resalva.");
    }

    private Boolean validarDocumentoVencido() {
        return licitacaoDoctoFornecedorSelecionado.getDataDeValidade().before(licitacaoSelecionada.getAbertaEm());
    }

    public void setarNovoTipoEmpresa() {
        ((PessoaJuridica) licitacaoFornecedorSelecionado.getEmpresa()).setTipoEmpresa(tipoEmpresaSelecionada);
        if (TipoEmpresa.NORMAL.equals(((PessoaJuridica) licitacaoFornecedorSelecionado.getEmpresa()).getTipoEmpresa())) {
            licitacaoDoctoFornecedorSelecionado.setDocumentoVencido(Boolean.TRUE);
            concluirVinculosLicitacoesDocumentos();
        } else {
            informaPrazoParaRegularizarDocumentacao(licitacaoFornecedorSelecionado.getEmpresa().getNome());
            concluirVinculosLicitacoesDocumentos();
        }
    }

    private Boolean validarDocumentoHabilitacaoSelecionado() {
        Boolean validou = Boolean.TRUE;
        if (this.documentoHabilitacaoVisualizacaoFornecedor == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Erro no documento habilitação.", "Não existe documento habilitação selecionado, por favor selecione algum para continuar."));
            return Boolean.FALSE;
        }

        if (this.documentoHabilitacaoVisualizacaoFornecedor.getRequerNumero() && (this.licitacaoDoctoFornecedorSelecionado.getNumeroDaCertidao() == null || this.licitacaoDoctoFornecedorSelecionado.getNumeroDaCertidao().toString().trim().length() <= 0)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Número obrigatório", "Por favor, informe o número."));
            validou = Boolean.FALSE;
        }

        if (this.documentoHabilitacaoVisualizacaoFornecedor.getRequerEmissao() && this.licitacaoDoctoFornecedorSelecionado.getDataDeEmissao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data de emissão obrigatória", "Por favor, informe a data de emissão."));
            validou = Boolean.FALSE;
        }

        /*
         * O campo 'Requer Validade' indica somente que o documento requer a
         * validade onde seu período não precisará ser validado
         */
        if (this.licitacaoDoctoFornecedorSelecionado.getDataDeValidade() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validade obrigatória", "Por favor, informe a validade deste documento."));
            validou = Boolean.FALSE;
        }
//        else if (!validarValidadeDeDocumentoAdicionado()) {
//            /*
//             * Mesmo que algum documento apresentado esteja vencido, ele deve
//             * ser aceito, e o fornecedor será inabilitado.
//             */
//            validou = Boolean.TRUE;
//        }

        return validou;
    }

    private void criarVinculosLicitacoesDocumentos() {
        licitacaoDoctoFornecedorSelecionado.setLicitacaoFornecedor(licitacaoFornecedorSelecionado);
        licitacaoDoctoFornecedorSelecionado.setDoctoHabilitacao(documentoHabilitacaoVisualizacaoFornecedor);
        if (licitacaoFornecedorSelecionado.getDocumentosFornecedor() == null) {
            licitacaoFornecedorSelecionado.setDocumentosFornecedor(new ArrayList<LicitacaoDoctoFornecedor>());
        }
        licitacaoFornecedorSelecionado.getDocumentosFornecedor().add(licitacaoDoctoFornecedorSelecionado);
    }

    public void concluirVinculosLicitacoesDocumentos() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Documento Vinculado!", "O documento selecionado foi vinculado com sucesso."));
        atualizarComponente(":Formulario");
        executarScript("dialogDocumentosFornecedor.hide()");
    }

    public void cancelarFornecedorSelecionado() {
        tornarNullLicitacaoFornecedorSelecionado();
        novoFornecedor = Boolean.FALSE;
    }

    public List<RetiradaEdital> buscarRetiradaEdital() {
        return participanteLicitacaoFacade.getRetiradaEditalFacade().buscarRetiradaEdital(licitacaoSelecionada);
    }

    public void adicionarRetirada() {
        Integer codigo = recuperaMaiorCodigoDaLicitacaoFornecedor();
        LicitacaoFornecedor licForn = new LicitacaoFornecedor();
        for (RetiradaEdital edital : retiradaEditals) {
            licForn.setCodigo(codigo);
            licForn.setLicitacao(edital.getLicitacao());
            licForn.setEmpresa(edital.getFornecedor());
            licForn.setRepresentante(edital.getRepresentante());
            if (isRetiradaParticipanteLicitacao()) {
                listaDeLicitacaoFornecedor.add(licForn);
                codigo = codigo + 1;
            }
        }
    }

    public boolean isRetiradaParticipanteLicitacao() {
        boolean valida = true;
        for (RetiradaEdital edital : retiradaEditals) {
            for (LicitacaoFornecedor fornecedor : listaDeLicitacaoFornecedor) {
                if (fornecedor.getEmpresa().equals(edital.getFornecedor())) {
                    FacesUtil.addOperacaoNaoPermitida("O fornecedor já está participando da licitação!");
                    valida = false;
                }
            }
        }
        return valida;
    }

    @Override
    public AbstractFacade getFacede() {
        return participanteLicitacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/participantes/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean getAbilitarBotaoParticipante() {
        return abilitarBotaoParticipante;
    }

    public void setAbilitarBotaoParticipante(Boolean abilitarBotaoParticipante) {
        this.abilitarBotaoParticipante = abilitarBotaoParticipante;
    }

    public boolean isVisualizar() {
        if (Operacoes.VER.equals(operacao)) {
            return true;
        }
        return false;
    }

    public boolean estaEditandoFornecedor() {
        return listaDeLicitacaoFornecedor.contains(selecionado);
    }

    public boolean possuiItemAdjudicado() {
        if (licitacaoSelecionada == null) {
            return !participanteLicitacaoFacade.getLicitacaoFacade().recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(selecionado.getLicitacao(), SituacaoItemProcessoDeCompra.ADJUDICADO).isEmpty();
        }
        return !participanteLicitacaoFacade.getLicitacaoFacade().recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(licitacaoSelecionada, SituacaoItemProcessoDeCompra.ADJUDICADO).isEmpty();
    }

    public boolean hasItensHomologados() {
        if (licitacaoSelecionada == null) {
            return !participanteLicitacaoFacade.getLicitacaoFacade().recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(selecionado.getLicitacao(), SituacaoItemProcessoDeCompra.HOMOLOGADO).isEmpty();
        }
        return !participanteLicitacaoFacade.getLicitacaoFacade().recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(licitacaoSelecionada, SituacaoItemProcessoDeCompra.HOMOLOGADO).isEmpty();
    }

    public boolean bloquearEdicaoExclusaoDoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        if (hasItensHomologados()) {
            return true;
        }

        if (!possuiItemAdjudicado()) {
            return false;
        }

        if (licitacaoFornecedor.getEmpresa() != null) {
            return participanteLicitacaoFacade.getLicitacaoFacade().fornecedorDaLicitacaoTemProposta(licitacaoSelecionada, licitacaoFornecedor.getEmpresa());
        }
        return true;
    }

    public boolean desabilitaNovoParticipante() {
        if (licitacaoFornecedorSelecionado != null) {
            return true;
        }

        if (licitacaoSelecionada == null) {
            return true;
        }
        if (TipoSituacaoLicitacao.ADJUDICADA.equals(statusLicitacao.getTipoSituacaoLicitacao()) ||
            TipoSituacaoLicitacao.HOMOLOGADA.equals(statusLicitacao.getTipoSituacaoLicitacao())) {
            return true;
        }
        if (possuiItemAdjudicado()) {
            return true;
        }

        if (participanteLicitacaoFacade.getLicitacaoFacade().getCertameFacade().licitacaoTemVinculoEmUmCertame(licitacaoSelecionada)
            || participanteLicitacaoFacade.getLicitacaoFacade().getMapaComparativoTecnicaPrecoFacade().licitacaoTemVinculoEmUmMapaComparativoTecnicaPreco(licitacaoSelecionada)) {
            return true;
        }

        return false;
    }

    public StatusLicitacao getStatusLicitacao() {
        return statusLicitacao;
    }

    public void setStatusLicitacao(StatusLicitacao statusLicitacao) {
        this.statusLicitacao = statusLicitacao;
    }

    public void validarAndVerificarCPF() {
        try {
            validarCpf();
            verificarExistenciaDeRepresentante();
            licitacaoFornecedorSelecionado.setRepresentante(representanteLicitacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCpf() {
        ValidacaoException ve = new ValidacaoException();
        if (representanteLicitacao.getCpf() != null && !Util.validarCpf(representanteLicitacao.getCpf())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido.");
            setRepresentanteLicitacao(new RepresentanteLicitacao());
        }
        if (licitacaoSelecionada.isPregao() && licitacaoSelecionada.getNaturezaDoProcedimento() != null && licitacaoSelecionada.getNaturezaDoProcedimento().isEletronico()) {
            return;
        }
        if (StringUtils.isEmpty(representanteLicitacao.getCpf())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CPF é obrigatório.");
            setRepresentanteLicitacao(new RepresentanteLicitacao());
        }
        ve.lancarException();
    }

    public void verificarExistenciaDeRepresentante() {
        getRepresentanteLicitacao().setNome(null);
        if (StringUtils.isNotEmpty(representanteLicitacao.getCpf())) {
            RepresentanteLicitacao representante = participanteLicitacaoFacade.getLicitacaoFacade().getRepresentanteLicitacaoFacade().buscarRepresentanteLicitacaoPorCpf(representanteLicitacao.getCpf());
            if (representante.getId() != null) {
                setRepresentanteLicitacao(representante);
            }
        }
    }

    public RepresentanteLicitacao getRepresentanteLicitacao() {
        return representanteLicitacao;
    }

    public void setRepresentanteLicitacao(RepresentanteLicitacao representanteLicitacao) {
        this.representanteLicitacao = representanteLicitacao;
    }

    public Boolean getNovoFornecedor() {
        return novoFornecedor;
    }

    public void setNovoFornecedor(Boolean novoFornecedor) {
        this.novoFornecedor = novoFornecedor;
    }
}
