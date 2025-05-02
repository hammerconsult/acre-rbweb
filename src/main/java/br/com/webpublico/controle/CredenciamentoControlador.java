package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-credenciamento", pattern = "/credenciamento/novo/", viewId = "/faces/administrativo/licitacao/credenciamento/edita.xhtml"),
    @URLMapping(id = "editar-credenciamento", pattern = "/credenciamento/editar/#{credenciamentoControlador.id}/", viewId = "/faces/administrativo/licitacao/credenciamento/edita.xhtml"),
    @URLMapping(id = "ver-credenciamento", pattern = "/credenciamento/ver/#{credenciamentoControlador.id}/", viewId = "/faces/administrativo/licitacao/credenciamento/visualizar.xhtml"),
    @URLMapping(id = "listar-credenciamento", pattern = "/credenciamento/listar/", viewId = "/faces/administrativo/licitacao/credenciamento/lista.xhtml")
})
public class CredenciamentoControlador extends PrettyControlador<Licitacao> implements Serializable, CRUD {

    @EJB
    protected LicitacaoFacade facade;
    private DoctoHabLicitacao documentoProcesso;
    private ParecerLicitacao parecer;
    private LicitacaoFornecedor licitacaoFornecedor;
    private LicitacaoDoctoFornecedor documentoFornecedor;
    private PropostaFornecedor propostaFornecedor;
    private List<LoteProcessoDeCompra> lotesProcessoCompra;
    private PublicacaoLicitacao publicacaoProcesso;
    private HierarquiaOrganizacional unidadeAdministrativa;
    private String modeloProduto;
    private String descricaoProduto;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso;
    private ItemPropostaFornecedor itemProposta;
    private StatusLicitacao statusAtual;
    private List<ItemPropostaFornecedor> itensProposta;

    private Boolean bloquearEdicao;

    public CredenciamentoControlador() {
        super(Licitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-credenciamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setEmitidaEm(getDataOperacao());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica());
        StatusLicitacao status = facade.getStatusLicitacaoFacade().getNovoStatus(TipoSituacaoLicitacao.ANDAMENTO, selecionado, "Status criado automaticamente pelo sistema para um novo credenciamento");
        selecionado.getListaDeStatusLicitacao().add(status);
        statusAtual = status;
        recuperarProcessoCompra();
    }

    @URLAction(mappingId = "ver-credenciamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarCredenciamento(getId());
        setUnidadeAdministrativaProcessoCompra();
        buscarLotesAndItensProcessoCompra();
        statusAtual = facade.getStatusLicitacaoFacade().recuperarUltimoStatusLicitacao(selecionado);
        Collections.sort(selecionado.getFornecedores());
    }

    @URLAction(mappingId = "editar-credenciamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarCredenciamento(getId());
        statusAtual = facade.getStatusLicitacaoFacade().recuperarUltimoStatusLicitacao(selecionado);
        bloquearEdicao = facade.getStatusLicitacaoFacade().recuperarUltimoStatusLicitacao(selecionado).isTipoHomologada();

        setUnidadeAdministrativaProcessoCompra();
        buscarLotesAndItensProcessoCompra();
        Collections.sort(selecionado.getFornecedores());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/credenciamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarCamposSelecionado();
            facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            selecionado = facade.salvarCredenciamento(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar credenciamento", e);
            logger.debug("Problema ao salvar credenciamento: {}", e);
            descobrirETratarException(e);
        }
    }

    public void concluir() {
        try {
            validarRegrasSelecionado();
            facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            removerLoteAndItemNaoSelecionadoProposta();
            selecionado = facade.concluirCredenciamento(selecionado);
            FacesUtil.addOperacaoRealizada("Registro concluído com sucesso!");
            redirecionarParVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao concluir credenciamento", e);
            logger.debug("Problema ao concluir credenciamento: {}", e);
            descobrirETratarException(e);
        }
    }

    private void recuperarProcessoCompra() {
        ProcessoDeCompra pdc = (ProcessoDeCompra) Web.pegaDaSessao("PROCESSO_DE_COMPRA");
        if (pdc != null) {
            selecionado.setProcessoDeCompra(pdc);
            listenerProcessoCompra();
        }
    }

    private void redirecionarParVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void validarRegrasSelecionado() {
        validarCamposSelecionado();
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasPublicacoes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos uma publicação.");
        }
        if (!selecionado.hasFornecedores()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um credenciado.");
        }
        ve.lancarException();
        for (LicitacaoFornecedor fornecedor : selecionado.getFornecedores()) {
            if (fornecedor.isFornecedorInabilitado()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O credenciado " + fornecedor.getEmpresa().getNomeCpfCnpj() + " está inabilitado.");
            }
            if (!fornecedor.hasPropostas()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" O credenciado " + fornecedor.getEmpresa().getNomeCpfCnpj() + " está sem proposta definida.");
            }
        }
        ve.lancarException();
    }

    private void validarCamposSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProcessoDeCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo processo de compra deve ser informado.");
        }
        if (selecionado.getEmitidaEm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data deve ser informado.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício deve ser informado.");
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo responsável deve ser informado.");
        }
        if (selecionado.getPeriodoDeExecucao() == null && selecionado.getTipoPrazoExecucao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo prazo de execução deve ser informado.");
        }
        if (selecionado.getPrazoDeVigencia() == null && selecionado.getTipoPrazoVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo prazo de vigência deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getLocalDeEntrega())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo local de entrega deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getFormaDePagamento())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo forma de pagamento deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getResumoDoObjeto())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo resumo do objeto deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo justificativa deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getFundamentacaoLegal())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fundamentação legal deve ser informado.");
        }
        ve.lancarException();
    }

    public void listenerProcessoCompra() {
        buscarLotesAndItensProcessoCompra();
        popularDadosProcesso();
    }

    private void buscarLotesAndItensProcessoCompra() {
        lotesProcessoCompra = facade.getProcessoDeCompraFacade().buscarLotesProcessoCompra(selecionado.getProcessoDeCompra());
        for (LoteProcessoDeCompra lote : lotesProcessoCompra) {
            List<ItemProcessoDeCompra> itens = facade.getProcessoDeCompraFacade().buscarItensProcessoCompraPorLote(lote);
            for (ItemProcessoDeCompra item : itens) {
                if (item != null && item.getObjetoCompra() != null) {
                    GrupoContaDespesa grupoContaDespesa = facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra());
                    item.getObjetoCompra().setGrupoContaDespesa(grupoContaDespesa);
                }
            }
            lote.setItensProcessoDeCompra(itens);
        }
    }

    private void popularDadosProcesso() {
        try {
            SolicitacaoMaterial solicitacao = selecionado.getProcessoDeCompra().getSolicitacaoMaterial();
            selecionado.setModalidadeLicitacao(solicitacao.getModalidadeLicitacao());
            selecionado.setNaturezaDoProcedimento(solicitacao.getTipoNaturezaDoProcedimento());
            selecionado.setTipoAvaliacao(solicitacao.getTipoAvaliacao());
            selecionado.setTipoApuracao(solicitacao.getTipoApuracao());
            selecionado.setPeriodoDeExecucao(solicitacao.getPrazoExecucao());
            selecionado.setTipoPrazoExecucao(solicitacao.getTipoPrazoDeExecucao());
            selecionado.setPrazoDeVigencia(solicitacao.getVigencia());
            selecionado.setTipoPrazoVigencia(solicitacao.getTipoPrazoDeVigencia());
            selecionado.setLocalDeEntrega(solicitacao.getLocalDeEntrega());
            selecionado.setFormaDePagamento(solicitacao.getFormaDePagamento());
            selecionado.setJustificativa(solicitacao.getJustificativa());
            selecionado.setExercicio(selecionado.getProcessoDeCompra().getExercicio());
            selecionado.setExercicioModalidade(selecionado.getProcessoDeCompra().getExercicio());
            selecionado.setValorMaximo(solicitacao.getValor());
            selecionado.setNumero(selecionado.getProcessoDeCompra().getNumero());
            setUnidadeAdministrativaProcessoCompra();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<ProcessoDeCompra> completarProcessoCompra(String anoNumeroDescricao) {
        return facade.getProcessoDeCompraFacade().buscarProcessoCompraSemProcessoLicitario(anoNumeroDescricao,
            TipoProcessoDeCompra.CREDENCIAMENTO, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<Pessoa> completarPessoasFisicas(String parte) {
        return facade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<VeiculoDePublicacao> completarVeiculoPublicacao(String parte) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }


    public List<DoctoHabilitacao> completarDocumentos(String parte) {
        return facade.getDoctoHabilitacaoFacade().buscarDocumentosVigentes(parte.trim(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<Pessoa> completarCredenciados(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<SelectItem> getTiposPrazo() {
        return Util.getListSelectItem(TipoPrazo.values());
    }

    public List<SelectItem> getTiposParecer() {
        return Util.getListSelectItemSemCampoVazio(TipoParecerLicitacao.getTiposCredenciamento().toArray());
    }

    private void validarProcessoCompraInformado() {
        if (selecionado.getProcessoDeCompra() == null) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada("O campo processo de compra deve ser informado.");
            throw ve;
        }
    }

    public void novoDocumentoProcesso() {
        documentoProcesso = new DoctoHabLicitacao();
        documentoProcesso.setLicitacao(selecionado);
    }

    public void novoParecer() {
        try {
            validarProcessoCompraInformado();
            parecer = new ParecerLicitacao();
            parecer.setLicitacao(selecionado);

            parecer.setDataParecer(facade.getSistemaFacade().getDataOperacao());
            parecer.setNumero(retornaProximoNumero(selecionado.getPareceres()));
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:tipo-parecer')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void novaPublicacao() {
        publicacaoProcesso = new PublicacaoLicitacao();
        publicacaoProcesso.setLicitacao(selecionado);
        publicacaoProcesso.setTipoPublicacaoLicitacao(TipoPublicacaoLicitacao.AVISO);
        publicacaoProcesso.setDataPublicacao(facade.getSistemaFacade().getDataOperacao());
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:veiculo-publicacao_input')");
    }

    public void novoFornecedor() {
        try {
            validarProcessoCompraInformado();
            licitacaoFornecedor = new LicitacaoFornecedor();
            licitacaoFornecedor.setLicitacao(selecionado);
            licitacaoFornecedor.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.INABILITADO);
            licitacaoFornecedor.setCodigo(retornaProximoNumero(selecionado.getFornecedores()));
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:fornecedor_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private Integer retornaProximoNumero(List lista) {
        if (lista != null && !lista.isEmpty()) {
            return lista.size() + 1;
        }
        return 1;
    }

    public void adicionarDocumentoProcesso() {
        try {
            validarDocumentoProcesso();
            Util.adicionarObjetoEmLista(selecionado.getDocumentosProcesso(), documentoProcesso);
            cancelarDocumentoProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarDocumentoProcesso() {
        Util.validarCampos(documentoProcesso);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.hasDocumentosProcesso()) {
            for (DoctoHabLicitacao doc : selecionado.getDocumentosProcesso()) {
                if (doc.getDoctoHabilitacao().equals(documentoProcesso.getDoctoHabilitacao()) && !documentoProcesso.equals(doc)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O documento selecionado já foi adicionado.");
                }
            }
        }
        ve.lancarException();
    }

    public void confirmarParecer() {
        try {
            validarParecer();
            Util.adicionarObjetoEmLista(selecionado.getPareceres(), parecer);
            cancelarParecer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarParecer() {
        Util.validarCampos(parecer);
        ValidacaoException ve = new ValidacaoException();
        if (parecer.getDeferido() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar o status 'Deferido/Indeferido do parecer'.");
        }
        if (selecionado.hasPareceres()) {
            for (ParecerLicitacao parecer : selecionado.getPareceres()) {
                if (parecer.getNumero().equals(this.parecer.getNumero()) && !parecer.getDataParecer().equals(this.parecer.getDataParecer())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um parecer com o número " + this.parecer.getNumero() + ".");
                }
            }
        }
        ve.lancarException();
    }

    public void confirmarPublicacao() {
        try {
            validarPulicacao();
            Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), publicacaoProcesso);
            cancelarPublicacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarPulicacao() {
        Util.validarCampos(publicacaoProcesso);
        if (selecionado.hasPublicacoes()) {
            ValidacaoException ve = new ValidacaoException();
            for (PublicacaoLicitacao publicacao : selecionado.getPublicacoes()) {
                if (publicacao.getDataPublicacao().equals(publicacaoProcesso.getDataPublicacao())
                    && !selecionado.getPublicacoes().contains(publicacaoProcesso)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma publicação com a data informada.");
                }
            }
            ve.lancarException();
        }
    }

    public void adicionarCredenciado() {
        try {
            validarCredenciado();
            Util.adicionarObjetoEmLista(selecionado.getFornecedores(), licitacaoFornecedor);
            cancelarCredenciado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCredenciado() {
        ValidacaoException ve = new ValidacaoException();
        if (licitacaoFornecedor.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código deve ser informado.");
        }
        if (licitacaoFornecedor.getEmpresa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo credenciado deve ser informado.");
        }
        if (selecionado.hasFornecedores())
            for (LicitacaoFornecedor lf : selecionado.getFornecedores()) {
                if (lf.getEmpresa().equals(licitacaoFornecedor.getEmpresa())
                    && !selecionado.getFornecedores().contains(licitacaoFornecedor)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O credenciado selecionado já foi adicionado.");
                }
            }
        ve.lancarException();
    }

    public void cancelarCredenciado() {
        licitacaoFornecedor = null;
    }

    public void cancelarPublicacao() {
        this.publicacaoProcesso = null;
    }

    public void cancelarParecer() {
        parecer = null;
    }

    public void cancelarDocumentoProcesso() {
        documentoProcesso = null;
    }

    public void editarDocumentoProcesso(DoctoHabLicitacao documento) {
        documentoProcesso = documento;
    }

    public void editarParecer(ParecerLicitacao parecer) {
        this.parecer = parecer;
    }

    public void editarPublicacao(PublicacaoLicitacao publicacao) {
        publicacaoProcesso = publicacao;
    }

    public void editarFornecedor(LicitacaoFornecedor fornecedor) {
        this.licitacaoFornecedor = fornecedor;
    }

    public void removerDocumentoProcesso(DoctoHabLicitacao documento) {
        selecionado.getDocumentosProcesso().remove(documento);
    }

    public void removerParecer(ParecerLicitacao parecer) {
        selecionado.getPareceres().remove(parecer);
    }

    public void removerPublicacao(PublicacaoLicitacao publicacao) {
        selecionado.getPublicacoes().remove(publicacao);
    }

    public void removerFornecedor(LicitacaoFornecedor fornecedor) {
        selecionado.getFornecedores().remove(fornecedor);
    }

    public void selecionarCredenciado(LicitacaoFornecedor credenciado) {
        this.licitacaoFornecedor = credenciado;
    }

    public void selecionarProposta(LicitacaoFornecedor credenciado) {
        try {
            validarSelecaoCredenciado();
            selecionarCredenciado(credenciado);
            validarCredenciadoHabilitado();
            selecionarPropostaCredenciado();
            FacesUtil.atualizarComponente("form-proposta");
            FacesUtil.executaJavaScript("dlgProposta.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isProcessoEmAndamento() {
        return statusAtual != null && statusAtual.isTipoAndamento();
    }

    public boolean jaLancouPrecoDePeloMenosUmItem() {
        for (ItemPropostaFornecedor item : itensProposta) {
            if (item.hasDescricaoProduto()) {
                return true;
            }
        }
        return false;
    }

    public void carregarFornecedorSelecionadoParaVisualizarProposta(LicitacaoFornecedor credenciado) {
        selecionarCredenciado(credenciado);
        selecionarPropostaCredenciado();
        if (jaLancouPrecoDePeloMenosUmItem()) {
            RequestContext.getCurrentInstance().update("form-detalhes-proposta");
            FacesUtil.executaJavaScript("dlgDetalhesProposta.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório realizar o lançamento da proposta antes de visualizar.");
            cancelarCredenciado();
        }
    }

    private void validarCredenciadoHabilitado() {
        ValidacaoException ve = new ValidacaoException();
        if (!licitacaoFornecedor.isFornecedorHabilitadoOuRessalva()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para lançar a proposta, o credenciado deve estar habilitado. Situação atual deste credenciado: <b>" + licitacaoFornecedor.getTipoClassificacaoFornecedor().getDescricao() + "</b>.");
        }
        ve.lancarException();
    }

    private void criarLoteAndItensProposta() {
        for (LoteProcessoDeCompra loteProcesso : lotesProcessoCompra) {
            LotePropostaFornecedor novoLoteProposta = criarLoteProposta(loteProcesso);

            for (ItemProcessoDeCompra itemProcesso : loteProcesso.getItensProcessoDeCompra()) {
                ItemPropostaFornecedor novoItemProposta = criarItemProposta(novoLoteProposta, itemProcesso);
                Util.adicionarObjetoEmLista(novoLoteProposta.getItens(), novoItemProposta);
            }
            Util.adicionarObjetoEmLista(propostaFornecedor.getLotes(), novoLoteProposta);
        }
    }

    public ItemPropostaFornecedor criarItemProposta(LotePropostaFornecedor loteProposta, ItemProcessoDeCompra itemProcesso) {
        ItemPropostaFornecedor itemProposta = new ItemPropostaFornecedor();
        itemProposta.setItemProcessoDeCompra(itemProcesso);
        itemProposta.setLotePropostaFornecedor(loteProposta);
        itemProposta.setPreco(itemProcesso.getItemSolicitacaoMaterial().getPreco());
        return itemProposta;
    }

    public LotePropostaFornecedor criarLoteProposta(LoteProcessoDeCompra loteProcesso) {
        LotePropostaFornecedor lote = new LotePropostaFornecedor();
        lote.setPropostaFornecedor(propostaFornecedor);
        lote.setLoteProcessoDeCompra(loteProcesso);
        lote.setValor(loteProcesso.getValor());
        lote.setItens(Lists.newArrayList());
        return lote;
    }

    public void selecionarPropostaCredenciado() {
        if (licitacaoFornecedor.hasPropostas()) {
            propostaFornecedor = licitacaoFornecedor.getPropostaFornecedor();
        } else {
            propostaFornecedor = new PropostaFornecedor();
            propostaFornecedor.setDataProposta(getDataOperacao());
            propostaFornecedor.setLotes(Lists.newArrayList());
            propostaFornecedor.setLicitacao(selecionado);
            propostaFornecedor.setFornecedor(licitacaoFornecedor.getEmpresa());
            propostaFornecedor.setLicitacaoFornecedor(licitacaoFornecedor);
            criarLoteAndItensProposta();
        }
        popularItensProposta();
    }

    private void popularItensProposta() {
        itensProposta = Lists.newArrayList();
        for (LotePropostaFornecedor loteProposta : propostaFornecedor.getLotes()) {
            for (ItemPropostaFornecedor item : loteProposta.getItens()) {
                if (item.hasDescricaoProduto()) {
                    item.setSelecionado(true);
                }
                itensProposta.add(item);
            }
        }
    }

    public void carregarDocumentosNecessarios(LicitacaoFornecedor fornecedor) {
        try {
            validarSelecaoCredenciado();
            selecionarCredenciado(fornecedor);
            FacesUtil.executaJavaScript("dialogHabFornecedor.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSelecaoCredenciado() {
        ValidacaoException ve = new ValidacaoException();
        validarProcessoCompraInformado();
        if (!selecionado.hasDocumentosProcesso()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os documentos devem ser informados antes de habilitar o credenciado.");
        }
        if (documentoProcesso != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Finalize a alteração no documento antes de habilitar o credenciado.");
        }
        ve.lancarException();
    }

    public Boolean desabilitaBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        try {
            for (LicitacaoDoctoFornecedor documentoFornecedor : licitacaoFornecedor.getDocumentosFornecedor()) {
                if (documentoFornecedor.getDoctoHabilitacao().equals(documentoHabilitacao)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public String tituloBotaoAdicionarDocumentoFornecedor(DoctoHabilitacao documentoHabilitacao) {
        return desabilitaBotaoAdicionarDocumentoFornecedor(documentoHabilitacao) ? "O documento já foi adicionado a lista de documentos apresentados." : "Clique para vincular este documento.";
    }

    public void passarDocumentoDoCredenciamentoParaCredenciado(DoctoHabilitacao documento) {
        documentoFornecedor = new LicitacaoDoctoFornecedor();
        documentoFornecedor.setDoctoHabilitacao(documento);
        FacesUtil.atualizarComponente("form-documento-fornecedor");
        FacesUtil.executaJavaScript("dialogDocumentoFornecedor.show()");
    }

    public void removerDocumentoCredenciado(LicitacaoDoctoFornecedor docto) {
        licitacaoFornecedor.getDocumentosFornecedor().remove(docto);
    }

    public void confirmarTodosDocumentosCredenciado() {
        processarClassificacaoCredenciado(getDataOperacao());
        notificarUsuarioSobreStatusFornecedor();
        Util.adicionarObjetoEmLista(selecionado.getFornecedores(), licitacaoFornecedor);
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-fornecedores");
        FacesUtil.executaJavaScript("dialogHabFornecedor.hide()");
    }

    public void processarClassificacaoCredenciado(Date dataOperacao) {
        if (!licitacaoFornecedor.hasDocumentos()) {
            licitacaoFornecedor.setTipoClassificacaoFornecedor(TipoClassificacaoFornecedor.INABILITADO);
            return;
        }
        for (LicitacaoDoctoFornecedor ldf : licitacaoFornecedor.getDocumentosFornecedor()) {
            licitacaoFornecedor.setTipoClassificacaoFornecedor(getSituacaoDeAcordoComEsteDocumento(dataOperacao, ldf));
        }
    }

    public TipoClassificacaoFornecedor getSituacaoDeAcordoComEsteDocumento(Date dataOperacao, LicitacaoDoctoFornecedor documento) {
        LicitacaoFornecedor fornecedor = documento.getLicitacaoFornecedor();
        if (!isDocumentoVencido(dataOperacao, documento)) {
            documento.setDocumentoVencido(Boolean.FALSE);
            fornecedor.setJustificativaClassificacao("Habilitado pois está com documento ok.");
            return TipoClassificacaoFornecedor.HABILITADO;
        }
        Pessoa f = fornecedor.getEmpresa();

        documento.setDocumentoVencido(Boolean.TRUE);
        if (f.isPessoaJuridica() && (f.isMicroEmpresa() || f.isPequenaEmpresa() || f.isTipoEmpresaIndefinido())) {
            fornecedor.setJustificativaClassificacao("Habilitado com ressalva pois é pessoa jurídica, está com o documento '" + documento + "' vencido e o tipo da empresa é " + f.getTipoEmpresa().getDescricao().toUpperCase() + ". O credenciado tem um prazo de 3(três) dias para regularizar o documento.");
            return TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA;
        }

        if (f.isPessoaJuridica()) {
            fornecedor.setJustificativaClassificacao("Inabilitado pois está com documento vencido e não se enquadra para habilitação com ressalva pois o tipo da empresa é " + f.getTipoEmpresa().getDescricao().toUpperCase() + ".");
        } else if (f.isPessoaFisica()) {
            fornecedor.setJustificativaClassificacao("Inabilitado pois está com documento vencido e não se enquadra para habilitação com ressalva pois o credenciado é uma pessoa física.");
        }
        return TipoClassificacaoFornecedor.INABILITADO;
    }

    public boolean isDocumentoVencido(Date dataOperacao, LicitacaoDoctoFornecedor documento) {
        try {
            return documento.getDataDeValidade() != null && documento.getDataDeValidade().compareTo(dataOperacao) < 0;
        } catch (NullPointerException npe) {
            return true;
        }
    }

    private void notificarUsuarioSobreStatusFornecedor() {
        if (licitacaoFornecedor.isFornecedorInabilitado() && licitacaoFornecedor.hasDocumentos()) {
            FacesUtil.addError(licitacaoFornecedor.getTipoClassificacaoFornecedor().getDescricao(), licitacaoFornecedor.getJustificativaClassificacao());
            return;
        }
        if (licitacaoFornecedor.isFornecedorInabilitado() && !licitacaoFornecedor.hasDocumentos()) {
            FacesUtil.addError(licitacaoFornecedor.getTipoClassificacaoFornecedor().getDescricao(), "Não foram apresentados documentos pelo credenciado.");
            return;
        }
        if (licitacaoFornecedor.isFornecedorHabilitadoComRessalva()) {
            FacesUtil.addWarn(licitacaoFornecedor.getTipoClassificacaoFornecedor().getDescricao(), licitacaoFornecedor.getJustificativaClassificacao());
            return;
        }
        if (licitacaoFornecedor.isFornecedorHabilitado()) {
            FacesUtil.addInfo(licitacaoFornecedor.getTipoClassificacaoFornecedor().getDescricao(), "O credenciado foi habilitado com sucesso.");
        }
    }

    private void atualizarItens() {
        if (!licitacaoFornecedor.hasDocumentos()) {
            for (ItemPropostaFornecedor ipf : licitacaoFornecedor.getEmpresa().getItensPropostaFornecedor()) {
                ipf.getItemPregao().setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
            }
        }
    }

    public void confirmarDocumentoSelecionadoDoFornecedor() {
        try {
            documentoFornecedor.validarCamposObrigatoriosDocumentosSelecionados();
            documentoFornecedor.setLicitacaoFornecedor(licitacaoFornecedor);
            Util.adicionarObjetoEmLista(licitacaoFornecedor.getDocumentosFornecedor(), documentoFornecedor);
            FacesUtil.atualizarComponente("formulario-hab-fornecedor");
            FacesUtil.executaJavaScript("dialogDocumentoFornecedor.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public String getCorDaDescricao(String descricao) {
        if ("Habilitado".equalsIgnoreCase(descricao)) {
            return "green";
        }
        if ("Habilitado com Ressalva".equalsIgnoreCase(descricao)) {
            return "goldenrod";
        }
        return "#cd0a0a";
    }

    public BigDecimal getValorTotalProposta() {
        BigDecimal total = BigDecimal.ZERO;
        if (itensProposta != null && !itensProposta.isEmpty()) {
            for (ItemPropostaFornecedor itemProposta : itensProposta) {
                if (itemProposta.getSelecionado()) {
                    total = total.add(itemProposta.getPrecoTotal());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (itensProposta != null && !itensProposta.isEmpty()) {
            for (ItemPropostaFornecedor itemProposta : itensProposta) {
                total = total.add(itemProposta.getPrecoTotal());
            }
        }
        return total;
    }

    public void confirmarProposta() {
        try {
            validarProposta();
            licitacaoFornecedor.setPropostaFornecedor(propostaFornecedor);
            Util.adicionarObjetoEmLista(selecionado.getFornecedores(), licitacaoFornecedor);
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:panel-fornecedor");
            FacesUtil.executaJavaScript("dlgProposta.hide()");
            cancelarCredenciado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void removerLoteAndItemNaoSelecionadoProposta() {
        for (LicitacaoFornecedor forn : selecionado.getFornecedores()) {
            if (forn.hasPropostas()) {
                for (LotePropostaFornecedor lote : forn.getPropostaFornecedor().getLotes()) {
                    Iterator<ItemPropostaFornecedor> itItem = lote.getItens().iterator();
                    while (itItem.hasNext()) {
                        ItemPropostaFornecedor item = itItem.next();
                        if (!item.hasDescricaoProduto()) {
                            itItem.remove();
                        }
                    }
                    lote.setValor(lote.getValorTotalItens());
                }
                Iterator<LotePropostaFornecedor> itLote = forn.getPropostaFornecedor().getLotes().iterator();
                while (itLote.hasNext()) {
                    LotePropostaFornecedor lote = itLote.next();
                    if (lote.getItens().isEmpty()) {
                        itLote.remove();
                    }
                }
            }
        }
    }

    public void cancelarProposta() {
        cancelarCredenciado();
    }

    public void validarProposta() {
        Util.validarCampos(propostaFornecedor);
        ValidacaoException ve = new ValidacaoException();
        for (ItemPropostaFornecedor item : itensProposta) {
            if (item.getSelecionado() && !item.hasDescricaoProduto()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O item Nº " + item.getNumeroItem() + " está sem a descrição do produto.");
            }
        }
        ve.lancarException();
    }

    public ItemPropostaFornecedor getItemProposta() {
        return itemProposta;
    }

    public void setItemProposta(ItemPropostaFornecedor itemProposta) {
        this.itemProposta = itemProposta;
    }

    public String getModeloProduto() {
        return modeloProduto;
    }

    public void setModeloProduto(String modeloProduto) {
        this.modeloProduto = modeloProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void carregaInformacoesProduto(ItemPropostaFornecedor item, String nomeComponente) {
        itemProposta = item;
        modeloProduto = itemProposta.getModelo();
        descricaoProduto = itemProposta.getDescricaoProduto();
        FacesUtil.atualizarComponente(nomeComponente);
    }

    public void confirmaInformacoesProduto() {
        if (descricaoProduto == null || descricaoProduto.trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("A descrição do produto é obrigatória!");
            return;
        }
        itemProposta.setModelo(modeloProduto);
        itemProposta.setDescricaoProduto(descricaoProduto);
        FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }

    public void limparInformacoesDaProposta(ItemPropostaFornecedor item) {
        if (!item.getSelecionado()) {
            item.setMarca(null);
            item.setModelo(null);
            item.setDescricaoProduto(null);
        }
    }

    public void selecionarTodosItensPropostaFornecedor() {
        if (itensProposta != null && !itensProposta.isEmpty()) {
            for (ItemPropostaFornecedor item : itensProposta) {
                item.setSelecionado(true);
            }
        }
    }

    public void desmarcarTodosItensPropostaFornecedor() {
        if (itensProposta != null && !itensProposta.isEmpty()) {
            for (ItemPropostaFornecedor item : itensProposta) {
                item.setSelecionado(false);
            }
        }
    }

    public boolean hasTodosItensPropostaFornecedorSelecionados() {
        if (itensProposta != null && !itensProposta.isEmpty()) {
            int quantidadeSelecionado = 0;
            for (ItemPropostaFornecedor item : itensProposta) {
                quantidadeSelecionado = item.getSelecionado() ? quantidadeSelecionado + 1 : quantidadeSelecionado;
            }
            return quantidadeSelecionado - itensProposta.size() == 0;
        }
        return false;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public void setUnidadeAdministrativaProcessoCompra() {
        setUnidadeAdministrativa(facade.getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getProcessoDeCompra().getUnidadeOrganizacional(),
            getDataOperacao()
        ));
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.CREDENCIAMENTO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
        if ("tab-ajuste".equals(tab)) {
            filtroAjusteProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.CREDENCIAMENTO);
        }
    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.LICITACAO);
        eventoPncpVO.setIdOrigem(selecionado.getId());
        eventoPncpVO.setIdPncp(selecionado.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(selecionado.getSequencialIdPncp());
        eventoPncpVO.setSequencialIdPncpLicitacao(selecionado.getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(selecionado.getExercicio().getAno());
        return eventoPncpVO;
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        selecionado.setIdPncp(eventoPncpVO.getIdPncp());
        selecionado.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(selecionado.getId(), selecionado.getNumeroAnoLicitacao());
        selecionado = facade.salvarRetornando(selecionado);
        redirecionarParVer();
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public List<LoteProcessoDeCompra> getLotesProcessoCompra() {
        return lotesProcessoCompra;
    }

    public void setLotesProcessoCompra(List<LoteProcessoDeCompra> lotesProcessoCompra) {
        this.lotesProcessoCompra = lotesProcessoCompra;
    }

    public DoctoHabLicitacao getDocumentoProcesso() {
        return documentoProcesso;
    }

    public void setDocumentoProcesso(DoctoHabLicitacao documentoProcesso) {
        this.documentoProcesso = documentoProcesso;
    }

    public ParecerLicitacao getParecer() {
        return parecer;
    }

    public void setParecer(ParecerLicitacao parecer) {
        this.parecer = parecer;
    }

    public PublicacaoLicitacao getPublicacaoProcesso() {
        return publicacaoProcesso;
    }

    public void setPublicacaoProcesso(PublicacaoLicitacao publicacaoProcesso) {
        this.publicacaoProcesso = publicacaoProcesso;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public LicitacaoDoctoFornecedor getDocumentoFornecedor() {
        return documentoFornecedor;
    }

    public void setDocumentoFornecedor(LicitacaoDoctoFornecedor documentoFornecedor) {
        this.documentoFornecedor = documentoFornecedor;
    }

    public List<ItemPropostaFornecedor> getItensProposta() {
        return itensProposta;
    }

    public void setItensProposta(List<ItemPropostaFornecedor> itensProposta) {
        this.itensProposta = itensProposta;
    }

    public Boolean getBloquearEdicao() {
        return bloquearEdicao;
    }

    public void setBloquearEdicao(Boolean bloquearEdicao) {
        this.bloquearEdicao = bloquearEdicao;
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroAjusteProcesso() {
        return filtroAjusteProcesso;
    }

    public void setFiltroAjusteProcesso(FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso) {
        this.filtroAjusteProcesso = filtroAjusteProcesso;
    }
}
