package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.RegistroSolicitacaoMaterialExternoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/08/14
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-registro-de-preco-externo", pattern = "/adesao-a-ata-de-registro-de-preco-externo/novo/", viewId = "/faces/administrativo/licitacao/registrosolicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "editar-registro-de-preco-externo", pattern = "/adesao-a-ata-de-registro-de-preco-externo/editar/#{registroSolicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/registrosolicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "ver-registro-de-preco-externo", pattern = "/adesao-a-ata-de-registro-de-preco-externo/ver/#{registroSolicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/registrosolicitacaomaterialexterno/visualizar.xhtml"),
    @URLMapping(id = "listar-registro-de-preco-externo", pattern = "/adesao-a-ata-de-registro-de-preco-externo/listar/", viewId = "/faces/administrativo/licitacao/registrosolicitacaomaterialexterno/lista.xhtml")
})
public class RegistroSolicitacaoMaterialExternoControlador extends PrettyControlador<RegistroSolicitacaoMaterialExterno> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(RegistroSolicitacaoMaterialExternoControlador.class);

    @EJB
    private RegistroSolicitacaoMaterialExternoFacade facade;
    private RegistroSolicitacaoMaterialExternoPublicacao registroPublicacaoSelecionada;
    private RegistroSolicitacaoMaterialExternoFornecedor registroFornecedorSelecionado;
    private ItemSolicitacaoExterno itemSolicitacaoExterno;
    private String modeloProduto;
    private String descricaoProduto;
    private boolean isRepresentanteAderentePessoaFisica = false;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public RegistroSolicitacaoMaterialExternoControlador() {
        super(RegistroSolicitacaoMaterialExterno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/adesao-a-ata-de-registro-de-preco-externo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-registro-de-preco-externo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializaDados();
    }

    @Override
    @URLAction(mappingId = "ver-registro-de-preco-externo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        for (RegistroSolicitacaoMaterialExternoFornecedor forn : selecionado.getFornecedores()) {
            Collections.sort(forn.getItens());
            for (RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno item : forn.itens) {
                atribuirGrupoContaDespesa(item.getItemSolicitacaoExterno());
            }
        }
        atribuirHierarquia();
    }

    @Override
    @URLAction(mappingId = "editar-registro-de-preco-externo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarListasSolicitacaoMaterialExterno();
        carregarItensDeCadaFornecedor();
        atribuirHierarquia();
        setRepresentanteAderentePessoaFisica(facade.getConfiguracaoLicitacaoFacade().verificarUnidadeTercerializadaRh(selecionado.getUnidadeOrganizacional()));
    }

    @Override
    public void salvar() {
        try {
           validarRepresentanteLegal();
           if (podeSalvar()) {
               super.salvar();
           }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void recuperaObjetos() {
        if (Operacoes.EDITAR.equals(operacao)) {
            carregarListasSolicitacaoMaterialExterno();
            carregarItensDeCadaFornecedor();
        }
    }

    private boolean podeSalvar() {
        if (!validarConfirmacao(selecionado)) {
            return false;
        }
        if (!temPublicacaoAdicionada()) {
            return false;
        }
        if (!temTodasPublicacoesObrigatoriasAdicionadas()) {
            return false;
        }
        if (!temFornecedorAdicionado()) {
            return false;
        }
        return true;
    }

    public void validarRepresentanteLegal() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContratoFP() == null && selecionado.getRepresentanteAderentePF() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Representante Legal Aderente deve ser informado.");
        }
        ve.lancarException();
    }

    private void carregarItensDeCadaFornecedor() {
        if (selecionado.registroTemFornecedorAdicionado()) {
            for (RegistroSolicitacaoMaterialExternoFornecedor fornecedor : selecionado.getFornecedores()) {

                if (fornecedorTemItemAdicionado(fornecedor)) {
                    for (RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno itemFornecedor : fornecedor.getItens()) {

                        if (solicitacaoSelecionadaTemItemAdicionado()) {
                            for (ItemSolicitacaoExterno itemSolicitacao : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
                                if (itemFornecedor.getItemSolicitacaoExterno().equals(itemSolicitacao)) {
                                    itemSolicitacao.setSelecionado(Boolean.TRUE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void atribuirGrupoContaDespesa(ItemSolicitacaoExterno item) {
        if (item.getObjetoCompra() != null) {
            item.getObjetoCompra().setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }


    private boolean solicitacaoSelecionadaTemItemAdicionado() {
        if (selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao() != null && !selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean temFornecedorAdicionado() {
        if (selecionado.getFornecedores() == null || selecionado.getFornecedores().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar pelo menos um fornecedor.");
            return false;
        }
        return true;
    }

    private boolean temTodasPublicacoesObrigatoriasAdicionadas() {
        for (TipoPublicacao tipoPublicacao : recuperaTiposDePublicacaoObrigatorio()) {
            if (!recuperaTiposDePublicacaoAdicionado().contains(tipoPublicacao)) {
                FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar uma publicação do tipo: " + tipoPublicacao);
                return false;
            }
        }
        return true;
    }

    private List<TipoPublicacao> recuperaTiposDePublicacaoObrigatorio() {
        List<TipoPublicacao> tiposDePublicacaoObrigatorios = new ArrayList<>();

        tiposDePublicacaoObrigatorios.add(TipoPublicacao.EDITAL);
        tiposDePublicacaoObrigatorios.add(TipoPublicacao.HOMOLOGACAO);
        tiposDePublicacaoObrigatorios.add(TipoPublicacao.ATA_REGISTRO_PRECO);

        return tiposDePublicacaoObrigatorios;
    }

    private List<TipoPublicacao> recuperaTiposDePublicacaoAdicionado() {
        List<TipoPublicacao> tiposDePublicacaoAdicionados = new ArrayList<>();

        for (RegistroSolicitacaoMaterialExternoPublicacao publicacao : selecionado.getPublicacoes()) {
            tiposDePublicacaoAdicionados.add(publicacao.getTipoPublicacao());
        }
        return tiposDePublicacaoAdicionados;
    }

    private boolean temPublicacaoAdicionada() {
        if (selecionado.getPublicacoes() == null || selecionado.getPublicacoes().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar pelo menos uma publicação.");
            return false;
        }
        return true;
    }

    public RegistroSolicitacaoMaterialExternoPublicacao getRegistroPublicacaoSelecionada() {
        return registroPublicacaoSelecionada;
    }

    public void setRegistroPublicacaoSelecionada(RegistroSolicitacaoMaterialExternoPublicacao registroPublicacaoSelecionada) {
        this.registroPublicacaoSelecionada = registroPublicacaoSelecionada;
    }

    public RegistroSolicitacaoMaterialExternoFornecedor getRegistroFornecedorSelecionado() {
        return registroFornecedorSelecionado;
    }

    public void setRegistroFornecedorSelecionado(RegistroSolicitacaoMaterialExternoFornecedor registroFornecedorSelecionado) {
        this.registroFornecedorSelecionado = registroFornecedorSelecionado;
    }

    private void inicializaDados() {
        selecionado.setDataRegistroCarona(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicioRegistro(facade.getSistemaFacade().getExercicioCorrente());
    }

    public void carregarDadosSolicitacaoSelecionada() {
        if (validarSolicitacaoJaUsada(selecionado.getSolicitacaoMaterialExterno())) {
            carregarListasSolicitacaoMaterialExterno();
            selecionado.setUnidadeOrganizacional(selecionado.getSolicitacaoMaterialExterno().getUnidadeOrganizacional());
            selecionado.setPessoaJuridica(selecionado.getSolicitacaoMaterialExterno().getPessoaJuridica());
            selecionado.setNumeroAtaCarona(selecionado.getSolicitacaoMaterialExterno().getNumeroAtaExterna());
            selecionado.setObjeto(selecionado.getSolicitacaoMaterialExterno().getObjeto());
            selecionado.setJustificativa(selecionado.getSolicitacaoMaterialExterno().getJustificativa());
            atribuirHierarquia();

            if (selecionado.getUnidadeOrganizacional() != null) {
                setRepresentanteAderentePessoaFisica(facade.getConfiguracaoLicitacaoFacade().verificarUnidadeTercerializadaRh(selecionado.getUnidadeOrganizacional()));
            }
        } else {
            FacesUtil.addError("Não é possível continuar!", "Já existe uma adesão cadastrada para a solicitação: " + selecionado.getSolicitacaoMaterialExterno());
            selecionado.setSolicitacaoMaterialExterno(null);
        }
    }

    private boolean validarSolicitacaoJaUsada(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        return facade.recuperarRegistroSolicitacaoMaterialExternoPorSolicitacao(solicitacaoMaterialExterno).isEmpty();
    }

    private void carregarListasSolicitacaoMaterialExterno() {
        carregarListasSolicitacaoMaterialExternoSelecionada();
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrganizacionais(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalAdministrativa(parte.trim());
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return facade.getContratoFPFacade().recuperaContrato(parte.trim());
    }

    public List<PessoaJuridica> completarPessoasJuridicas(String parte) {
        List<PessoaJuridica> retorno = Lists.newArrayList();
        for (Pessoa pessoa : facade.getPessoaFacade().listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO)) {
            retorno.add((PessoaJuridica) pessoa);
        }
        return retorno;
    }

    public List<PessoaFisica> completarPessoasFisicas(String parte) {
        List<PessoaFisica> retorno = Lists.newArrayList();
        for (Pessoa pessoa : facade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO)) {
            retorno.add((PessoaFisica) pessoa);
        }
        return retorno;
    }

    public List<VeiculoDePublicacao> completaVeiculoDePublicacao(String parte) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Pessoa> completaPessoaFornecedor(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeLicitacao.getModalidadesRegistroPrecoExterno());
    }

    public List<SelectItem> getTipoAvaliacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao : TipoAvaliacaoLicitacao.values()) {
            retorno.add(new SelectItem(tipoAvaliacaoLicitacao, tipoAvaliacaoLicitacao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoApuracoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoApuracaoLicitacao tipoApuracaoLicitacao : TipoApuracaoLicitacao.values()) {
            retorno.add(new SelectItem(tipoApuracaoLicitacao, tipoApuracaoLicitacao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoPublicacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoPublicacao tipoPublicacao : TipoPublicacao.values()) {
            retorno.add(new SelectItem(tipoPublicacao, tipoPublicacao.getDescricao()));
        }
        return retorno;
    }

    public String getDescricaoTipoModalidade() {
        String descricao = "";
        if (selecionado.getModalidadeCarona() != null) {
            switch (selecionado.getModalidadeCarona()) {
                case PREGAO:
                    descricao = " Tipo de Pregão";
                    break;
                case CONCORRENCIA:
                    descricao = " Tipo de Concorrência";
                    break;
                case RDC:
                    descricao = " Tipo de RDC";
                    break;
                default:
                    return descricao;
            }
        }
        return descricao;
    }

    public List<SelectItem> getTiposModalidade() {
        return Util.getListSelectItem(TipoNaturezaDoProcedimentoLicitacao.getTiposNaturezaProcedimentoRegistroPrecoExterno(selecionado.getModalidadeCarona()));
    }

    public boolean isVisualizar() {
        if (Operacoes.VER.equals(operacao)) {
            return true;
        }
        return false;
    }

    public void novaPublicacaoRegistro() {
        this.registroPublicacaoSelecionada = new RegistroSolicitacaoMaterialExternoPublicacao();
        this.registroPublicacaoSelecionada.setRegistroSolMatExterno(selecionado);
        this.registroPublicacaoSelecionada.setDataPublicacao(facade.getSistemaFacade().getDataOperacao());
    }

    public void confirmarPublicacaoRegistro() {
        if (!validarConfirmacao(this.registroPublicacaoSelecionada)) {
            return;
        }
        if (podeConfirmarPublicacao()) {
            selecionado.setPublicacoes(Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), registroPublicacaoSelecionada));
            cancelarPublicacaoRegistro();
        }
    }

    private boolean podeConfirmarPublicacao() {
        if (mesmaDataDaPublicacao() && mesmoTipoDaPublicacao()) {
            FacesUtil.addOperacaoNaoPermitida("Já existe uma publicação com a mesma data e tipo da publicação informado.");
            return false;
        }
        return true;
    }

    private boolean mesmoTipoDaPublicacao() {
        for (TipoPublicacao tipoPublicacao : recuperaTiposDePublicacaoAdicionado()) {
            if (tipoPublicacao.equals(registroPublicacaoSelecionada.getTipoPublicacao())) {
                return true;
            }
        }
        return false;
    }

    private boolean mesmaDataDaPublicacao() {
        if (selecionado.getPublicacoes() != null && !selecionado.getPublicacoes().isEmpty()) {
            for (RegistroSolicitacaoMaterialExternoPublicacao registroPublicacao : selecionado.getPublicacoes()) {
                if (registroPublicacao.getDataPublicacao().equals(registroPublicacaoSelecionada.getDataPublicacao())
                    && !selecionado.getPublicacoes().contains(registroPublicacaoSelecionada)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelarPublicacaoRegistro() {
        registroPublicacaoSelecionada = null;
    }

    public void selecionarPublicacaoRegistro(RegistroSolicitacaoMaterialExternoPublicacao registroPublicacao) {
        this.registroPublicacaoSelecionada = registroPublicacao;
    }

    public void removerPublicacaoRegistro(RegistroSolicitacaoMaterialExternoPublicacao registroPublicacao) {
        selecionado.getPublicacoes().remove(registroPublicacao);
    }

    private boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    public void novoFornecedorRegistro() {
        this.registroFornecedorSelecionado = new RegistroSolicitacaoMaterialExternoFornecedor();
        this.registroFornecedorSelecionado.setRegistroSolMatExterno(selecionado);
    }

    public void confirmarFornecedorRegistro() {
        if (!validarConfirmacao(this.registroFornecedorSelecionado)) {
            return;
        }
        if (podeConfirmarFornecedor()) {
            selecionado.setFornecedores(Util.adicionarObjetoEmLista(selecionado.getFornecedores(), registroFornecedorSelecionado));
            cancelarFornecedorRegistro();
        }

    }

    private boolean podeConfirmarFornecedor() {
        if (selecionado.getFornecedores() != null && !selecionado.getFornecedores().isEmpty()) {
            for (RegistroSolicitacaoMaterialExternoFornecedor fornecedor : selecionado.getFornecedores()) {
                if (fornecedor.getPessoa().getNome().equals(registroFornecedorSelecionado.getPessoa().getNome())
                    && !selecionado.getFornecedores().contains(registroFornecedorSelecionado)) {
                    FacesUtil.addOperacaoNaoPermitida("Este fornecedor já foi adicionado.");
                    return false;
                }
            }
        }
        return true;
    }

    public void cancelarFornecedorRegistro() {
        this.registroFornecedorSelecionado = null;
    }

    public void selecionarFornecedorRegistro(RegistroSolicitacaoMaterialExternoFornecedor fornecedor) {
        this.registroFornecedorSelecionado = fornecedor;
    }

    public void removerFornecedorRegistro(RegistroSolicitacaoMaterialExternoFornecedor fornecedor) {
        if (fornecedorTemItemAdicionado(fornecedor)) {
            for (ItemSolicitacaoExterno itemSolicitacao : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
                for (RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno itemFornecedor : fornecedor.getItens()) {
                    if (itemSolicitacao.equals(itemFornecedor.getItemSolicitacaoExterno())) {
                        itemSolicitacao.setSelecionado(Boolean.FALSE);
                    }
                }
            }
        }
        selecionado.getFornecedores().remove(fornecedor);
    }

    public void selecionarFornecedorRegistroParaVincularOsItens(RegistroSolicitacaoMaterialExternoFornecedor fornecedor) {
        if (selecionado.getSolicitacaoMaterialExterno() != null) {
            carregarListasSolicitacaoMaterialExternoSelecionada();
            selecionarFornecedorRegistro(fornecedor);
            RequestContext.getCurrentInstance().update("form-itens-do-fornecedor");
            FacesUtil.executaJavaScript("dialogItensDoFornecedor.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Para vincular os itens a este fornecedor é obrigatório informar primeiro a solicitação de registro de preço.");
        }
    }

    private void carregarListasSolicitacaoMaterialExternoSelecionada() {
        selecionado.setSolicitacaoMaterialExterno(facade.getSolicitacaoMaterialExternoFacade().recuperar(selecionado.getSolicitacaoMaterialExterno().getId()));
        for (ItemSolicitacaoExterno item : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
            atribuirGrupoContaDespesa(item);
        }
    }

    public boolean todosItensMarcados() {
        try {
            for (ItemSolicitacaoExterno item : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
                if (item.getSelecionado() == null || !item.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodosItens() {
        for (ItemSolicitacaoExterno item : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
            marcarItemSolicitacaoFornecedor(item);
        }
    }

    public void marcarItemSolicitacaoFornecedor(ItemSolicitacaoExterno item) {
        item.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarTodosItens() {
        for (ItemSolicitacaoExterno item : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
            desmarcarItemSolicitacaoFornecedor(item);
        }
    }

    public void desmarcarItemSolicitacaoFornecedor(ItemSolicitacaoExterno item) {
        item.setSelecionado(Boolean.FALSE);
    }

    public boolean itemJaVinculado(ItemSolicitacaoExterno item) {
        if (selecionado.registroTemFornecedorAdicionado()) {
            for (RegistroSolicitacaoMaterialExternoFornecedor fornecedor : selecionado.getFornecedores()) {
                if (fornecedorTemItemAdicionado(fornecedor)) {
                    for (RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno itemSolicitacaoExterno : fornecedor.getItens()) {
                        if (itemSolicitacaoExterno.getItemSolicitacaoExterno().equals(item) && !fornecedor.equals(registroFornecedorSelecionado)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean fornecedorTemItemAdicionado(RegistroSolicitacaoMaterialExternoFornecedor fornecedor) {
        return fornecedor.getItens() != null && !fornecedor.getItens().isEmpty();
    }

    public boolean validaItensDoFornecedor() {
        for (ItemSolicitacaoExterno itemSolicitacao : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
            if (itemSolicitacao.getSelecionado() != null && itemSolicitacao.getSelecionado()
                && (itemSolicitacao.getDescricaoProduto() == null || itemSolicitacao.getDescricaoProduto().trim().isEmpty())) {
                FacesUtil.addCampoObrigatorio("A descrição do produto " + itemSolicitacao.getDescricao() + " é obrigatória!");
                return false;
            }
        }
        return true;
    }

    public void confirmarItensDoFornecedor() {
        if (!validaItensDoFornecedor()) {
            return;
        }
        limpaListaDeItemDoFornecedor();
        for (ItemSolicitacaoExterno itemSolicitacao : selecionado.getSolicitacaoMaterialExterno().getItensDaSolicitacao()) {
            if (podeAdicionarItemAoFornecedor(itemSolicitacao)) {
                RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno itemFornecedor = new RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno();
                itemFornecedor.setRegSolMatExtFornecedor(registroFornecedorSelecionado);
                itemFornecedor.setItemSolicitacaoExterno(itemSolicitacao);
                registroFornecedorSelecionado.setItens(Util.adicionarObjetoEmLista(registroFornecedorSelecionado.getItens(), itemFornecedor));
            }
        }
        RequestContext.getCurrentInstance().update("form-itens-do-fornecedor");
        FacesUtil.executaJavaScript("dialogItensDoFornecedor.hide()");
        cancelarFornecedorRegistro();
    }

    private boolean podeAdicionarItemAoFornecedor(ItemSolicitacaoExterno itemSolicitacao) {
        return itemSolicitacao.getSelecionado() != null && itemSolicitacao.getSelecionado() && !itemJaVinculado(itemSolicitacao);
    }

    private void limpaListaDeItemDoFornecedor() {
        if (registroFornecedorSelecionado.getItens() != null && !registroFornecedorSelecionado.getItens().isEmpty()) {
            registroFornecedorSelecionado.getItens().clear();
        }
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return itemSolicitacaoExterno;
    }

    public void setItemSolicitacaoExterno(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
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

    public void carregaInformacoesProduto(ItemSolicitacaoExterno item, String nomeComponente) {
        itemSolicitacaoExterno = item;
        modeloProduto = itemSolicitacaoExterno.getModelo();
        descricaoProduto = itemSolicitacaoExterno.getDescricaoProduto();
        FacesUtil.atualizarComponente(nomeComponente);
    }

    public void confirmaInformacoesProduto() {
        if (descricaoProduto == null || descricaoProduto.trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("A descrição do produto é obrigatória!");
            return;
        }
        itemSolicitacaoExterno.setModelo(modeloProduto);
        itemSolicitacaoExterno.setDescricaoProduto(descricaoProduto);
        FacesUtil.executaJavaScript("dlgInfoProduto.hide()");
    }


    private void atribuirHierarquia() {
        selecionado.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(selecionado.getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA, facade.getSistemaFacade().getDataOperacao()));
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.ADESAO_EXTERNA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    @Override
    public void validarExclusaoEntidade() {
        super.validarExclusaoEntidade();
        ValidacaoException ve = new ValidacaoException();
        if (isRegistroContratado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "O registro não pode ser excluido porque possui contrato.");
        }
        ve.lancarException();
    }

    public boolean isRegistroContratado() {
        try {
            return facade.verificarSeExisteContrato(selecionado);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRepresentanteAderentePessoaFisica() {
        return isRepresentanteAderentePessoaFisica;
    }

    public void setRepresentanteAderentePessoaFisica(boolean representanteAderentePessoaFisica) {
        isRepresentanteAderentePessoaFisica = representanteAderentePessoaFisica;
    }
}
