package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorItemAdesaoAta;
import br.com.webpublico.entidadesauxiliares.ValidacaoObjetoCompraEspecificacao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoMaterialExternoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 30/07/14
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-sol-interna", pattern = "/solicitacao-adesao-ata-registro-preco-interna/novo/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "editar-sol-interna", pattern = "/solicitacao-adesao-ata-registro-preco-interna/editar/#{solicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "ver-sol-interna", pattern = "/solicitacao-adesao-ata-registro-preco-interna/ver/#{solicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/visualizar.xhtml"),
    @URLMapping(id = "listar-sol-interna", pattern = "/solicitacao-adesao-ata-registro-preco-interna/listar/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/lista-interna.xhtml"),

    @URLMapping(id = "novo-sol-externa", pattern = "/solicitacao-adesao-ata-registro-preco-externa/novo/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "editar-sol-externa", pattern = "/solicitacao-adesao-ata-registro-preco-externa/editar/#{solicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/edita.xhtml"),
    @URLMapping(id = "ver-sol-externa", pattern = "/solicitacao-adesao-ata-registro-preco-externa/ver/#{solicitacaoMaterialExternoControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/visualizar.xhtml"),
    @URLMapping(id = "listar-sol-externa", pattern = "/solicitacao-adesao-ata-registro-preco-externa/listar/", viewId = "/faces/administrativo/licitacao/solicitacaomaterialexterno/lista-externa.xhtml"),
})
public class SolicitacaoMaterialExternoControlador extends PrettyControlador<SolicitacaoMaterialExterno> implements Serializable, CRUD {

    @EJB
    private SolicitacaoMaterialExternoFacade facade;
    private ItemSolicitacaoExterno itemSolicitacao;
    private ItemSolicitacaoExternoDotacao itemSolicitacaoDotacao;
    private Map<AgrupadorItemAdesaoAta, BigDecimal> mapSaldoUnidadeGestora;
    private UnidadeGestora unidadeGestora;
    private List<ItemSolicitacaoExterno> itensControleValor;
    private List<ItemSolicitacaoExterno> itensControleQuantidade;
    private Boolean solicitacaoContratada;

    public SolicitacaoMaterialExternoControlador() {
        super(SolicitacaoMaterialExterno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.isSolicitacaoInterna()) {
            return "/solicitacao-adesao-ata-registro-preco-interna/";
        }
        return "/solicitacao-adesao-ata-registro-preco-externa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-sol-interna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaInterna() {
        novo();
        selecionado.setTipoSolicitacaoRegistroPreco(TipoSolicitacaoRegistroPreco.INTERNA);
    }

    @URLAction(mappingId = "ver-sol-interna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verInterna() {
        ver();
    }

    @URLAction(mappingId = "editar-sol-interna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarInterna() {
        editar();
    }

    @URLAction(mappingId = "novo-sol-externa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaExterna() {
        novo();
        selecionado.setTipoSolicitacaoRegistroPreco(TipoSolicitacaoRegistroPreco.EXTERNA);
    }

    @URLAction(mappingId = "ver-sol-externa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verExterna() {
        ver();
    }

    @URLAction(mappingId = "editar-sol-externa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarExterna() {
        editar();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        setSolicitacaoContratada(false);
        inicializarAtributosDaTela();
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarAtributosDaTela();
        recuperarItensSelecionados();
        atribuirHierarquia();
        separarItensValorQuantidade(selecionado.getItensDaSolicitacao());
    }

    @Override
    public void editar() {
        try {
            super.editar();
            atribuirHierarquia();
            recuperarItensSelecionados();
            inicializarAtributosDaTela();
            validarStatusLicitacao();
            setSolicitacaoContratada(facade.verificarSolicitacaoContratada(selecionado));
            if (selecionado.isSolicitacaoInterna()) {
                limparListaItens();
                criarItens();
            }
            atribuirSubTipoObjetoCompraAoItem(selecionado.getItensDaSolicitacao());
            if (selecionado.isSolicitacaoExterna()) {
                separarItensValorQuantidade(selecionado.getItensDaSolicitacao());
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    private void atribuirSubTipoObjetoCompraAoItem(List<ItemSolicitacaoExterno> itens) {
        if (selecionado.isSolicitacaoExterna()) {
            for (ItemSolicitacaoExterno item : itens) {
                buscarSubtiposObjetoCompraDaConfiguracaoTipoObjetoCompra(item);
            }
        }
    }

    public void atribuirGrupoContaDespesa(ItemSolicitacaoExterno item) {
        if (item.getObjetoCompra() != null) {
            item.getObjetoCompra().setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    public void validarItemSolicitacaoExternoEmManutencao() {
        ValidacaoException ve = new ValidacaoException();
        if (this.itemSolicitacao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe um registro na aba 'Itens' sendo inserido ou editado, por favor termine a operação antes de salvar o cadastro.");
        }
        ve.lancarException();
    }

    private void validarSaldoDisponivelSolicitacaoInterna(ItemSolicitacaoExterno item, ValidacaoException ve) {
        if (selecionado.isSolicitacaoInterna()) {
            if (item != null && item.getSelecionado() != null && item.getSelecionado()) {
                BigDecimal saldo = getSaldoTotalDisponivelItem(item);
                if (item.isControleValor()) {
                    if (item.getValorTotal().compareTo(saldo) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado para o item " + item.getObjetoCompra() +
                            " é superior ao valor máximo permitido. Valor máximo permitido: " + saldo + ".");
                    }
                    if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + item.getObjetoCompra() + " não possui valor disponível para adesão.");
                    }
                } else {
                    if (item.getQuantidade().compareTo(saldo) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada para o item " + item.getObjetoCompra() +
                            " é superior a quantidade máxima permitida. Quantidade máxima permitida: " + saldo + ".");
                    }
                    if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O item " + item.getObjetoCompra() + " não possui quantidade disponível para adesão.");
                    }
                }
            }
        }
    }

    @Override
    public void salvar() {
        try {
            validarAoSalvar();
            validarStatusLicitacao();
            atualizaListaDeItensParaSalvar();
            selecionado = facade.salvarSolicitacao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarAoSalvar() {
        ValidacaoException ve = new ValidacaoException();
        validarCampos(ve);
        validarItemSolicitacaoExternoEmManutencao();
        for (ItemSolicitacaoExterno item : itensControleValor) {
            validarSaldoDisponivelSolicitacaoInterna(item, ve);
        }
        for (ItemSolicitacaoExterno item : itensControleQuantidade) {
            validarSaldoDisponivelSolicitacaoInterna(item, ve);
        }
        ve.lancarException();
    }

    private void validarStatusLicitacao() {
        if (selecionado.isSolicitacaoInterna()) {
            facade.getAtaRegistroPrecoFacade().getLicitacaoFacade().verificarStatusLicitacao(selecionado.getAtaRegistroPreco().getLicitacao());
        }
    }

    public void selecionarEspecificacao(ActionEvent evento) {
        ObjetoDeCompraEspecificacao especificacao = (ObjetoDeCompraEspecificacao) evento.getComponent().getAttributes().get("objeto");
        itemSolicitacao.setDescricaoComplementar(especificacao.getTexto());
    }

    public void limparEspecificacao() {
        itemSolicitacao.setDescricaoComplementar(null);
    }

    private void inicializarAtributosDaTela() {
        itensControleValor = Lists.newArrayList();
        itensControleQuantidade = Lists.newArrayList();
    }

    private void recuperarItensSelecionados() {
        if (selecionado.isSolicitacaoInterna()) {
            for (ItemSolicitacaoExterno item : selecionado.getItensDaSolicitacao()) {
                item.setSelecionado(Boolean.TRUE);
            }
        }
    }

    private void atribuirHierarquia() {
        selecionado.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(selecionado.getUnidadeOrganizacional(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA, facade.getSistemaFacade().getDataOperacao()));
    }

    private void atualizaListaDeItensParaSalvar() {
        List<ItemSolicitacaoExterno> itensQueSeraoSalvos = new ArrayList<>();
        for (ItemSolicitacaoExterno item : itensControleValor) {
            if (item.getSelecionado() != null && item.getSelecionado()) {
                itensQueSeraoSalvos.add(item);
            }
        }
        for (ItemSolicitacaoExterno item : itensControleQuantidade) {
            if (item.getSelecionado() != null && item.getSelecionado()) {
                itensQueSeraoSalvos.add(item);
            }
        }
        selecionado.setItensDaSolicitacao(itensQueSeraoSalvos);
    }

    private void validarCampos(ValidacaoException ve) {
        validarTipoSolicitacaoAndUnidade(ve);
        validarCamposSolicitacaoExterna(ve);
        validarCamposSolicitacaoInterna(ve);
        validarCamposComuns(ve);
    }

    private void validarTipoSolicitacaoAndUnidade(ValidacaoException ve) {
        if (selecionado.getTipoSolicitacaoRegistroPreco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Solicitação deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo órgão/entidade/fundo solicitante deve ser informado.");
        }
    }

    private void validarCamposComuns(ValidacaoException ve) {
        if (selecionado.getDocumentoGerenciador() == null || selecionado.getDocumentoGerenciador().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo documento de autorização do gerenciador deve ser informado.");
        }
        if (selecionado.getDocumentoAutorizacao() == null || selecionado.getDocumentoAutorizacao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo documento de autorização do fornecedor deve ser informado.");
        }
        if (selecionado.getObjeto() == null || selecionado.getObjeto().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo objeto deve ser informado.");
        }
        if (selecionado.getJustificativa() == null || selecionado.getJustificativa().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo justificativa deve ser informado.");
        }
        if (selecionado.getDocumentoAutorizacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo documento de autorização do fornecedor é obrigatório.");
        }
        ve.lancarException();
    }

    private void validarCamposSolicitacaoInterna(ValidacaoException ve) {
        if (selecionado.isSolicitacaoInterna()) {
            if (selecionado.getAtaRegistroPreco() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo ata de registro de preço interna deve ser informado.");
            }
            if (selecionado.getDocumento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo documento da solicitação deve ser informado.");
            }
            if (selecionado.getAtaRegistroPreco() != null) {
                selecionado.setAtaRegistroPreco(facade.getAtaRegistroPrecoFacade().recuperarComDependenciasAdesao(selecionado.getAtaRegistroPreco().getId()));
                if (selecionado.getAtaRegistroPreco().getAdesoes().size() > 5) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A ata de registro de preço informada já tem 5 adesões, sendo o limite máximo permitido.");
                }
            }
            if (isNenhumItemSelecionado()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar pelo menos um item a esta solicitação.");
            }
        }
    }

    private void validarCamposSolicitacaoExterna(ValidacaoException ve) {
        if (selecionado.isSolicitacaoExterna()) {
            if (selecionado.getPessoaJuridica() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo órgão/entidade/fundo gerenciador deve ser informado.");
            }
            if (selecionado.getNumeroAtaExterna() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo número ata de registro de preço externa deve ser informado.");
            }
            if (!hasItensValor() && !hasItensQuantidade()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um item a esta solicitação.");
            }
            for (ItemSolicitacaoExterno item : itensControleQuantidade) {
                validarSubTipoObjetoCompra(item);
            }
            for (ItemSolicitacaoExterno item : itensControleValor) {
                validarSubTipoObjetoCompra(item);
            }
            facade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
        }
    }

    private boolean isNenhumItemSelecionado() {
        if (hasItensValor()) {
            for (ItemSolicitacaoExterno item : itensControleValor) {
                if (item.getSelecionado() != null && item.getSelecionado()) {
                    return false;
                }
            }
        }
        if (hasItensQuantidade()) {
            for (ItemSolicitacaoExterno item : itensControleQuantidade) {
                if (item.getSelecionado() != null && item.getSelecionado()) {
                    return false;
                }
            }
        }
        return true;
    }

    public ItemSolicitacaoExterno getItemSolicitacao() {
        return itemSolicitacao;
    }

    public void setItemSolicitacao(ItemSolicitacaoExterno itemSolicitacao) {
        this.itemSolicitacao = itemSolicitacao;
    }

    public ItemSolicitacaoExternoDotacao getItemSolicitacaoDotacao() {
        return itemSolicitacaoDotacao;
    }

    public void setItemSolicitacaoDotacao(ItemSolicitacaoExternoDotacao itemSolicitacaoDotacao) {
        this.itemSolicitacaoDotacao = itemSolicitacaoDotacao;
    }

    public List<UnidadeOrganizacional> completarUnidadeOrganizacional(String parte) {
        List<UnidadeOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaOrganizacionalAdministrativa(parte.trim())) {
            Util.adicionarObjetoEmLista(retorno, hierarquiaOrganizacional.getSubordinada());
        }
        return retorno;
    }

    public List<PessoaJuridica> completarPessoaJuridica(String parte) {
        List<PessoaJuridica> retorno = new ArrayList<>();
        for (Pessoa pessoa : facade.getPessoaFacade().listaPessoasJuridicas(parte.trim())) {
            retorno.add((PessoaJuridica) pessoa);
        }
        return retorno;
    }

    public List<ObjetoCompra> buscarObjetoCompra(String codigoOrDescricao) {
        return facade.getObjetoCompraFacade().completaObjetoCompra(codigoOrDescricao);
    }

    public void recuperarObjetoDeCompra() {
        itemSolicitacao.setObjetoCompra(facade.getObjetoCompraFacade().recuperar(itemSolicitacao.getObjetoCompra().getId()));
        atribuirGrupoContaDespesa(itemSolicitacao);
    }

    public List<SelectItem> getTiposControle() {
        return Util.getListSelectItem(TipoControleLicitacao.values());
    }

    public List<SelectItem> getSubtiposObjetoCompra(ItemSolicitacaoExterno item) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (item.getSubTiposObjetoCompra() != null && !item.getSubTiposObjetoCompra().isEmpty()) {
            for (SubTipoObjetoCompra object : item.getSubTiposObjetoCompra()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public void novoItem() {
        try {
            validarNovoItem();
            itemSolicitacao = new ItemSolicitacaoExterno();
            itemSolicitacao.setSolicitacaoMaterialExterno(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public Integer getProximoNumeroItem(ItemSolicitacaoExterno itemSolicitacao) {
        if (itemSolicitacao.isControleValor()) {
            return itensControleValor.size() + 1;
        }
        return itensControleQuantidade.size() + 1;
    }

    private void validarNovoItem() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoSolicitacaoRegistroPreco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Solicitação deve ser informado.");
        }
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão/Entidade/Fundo Solicitante deve ser informado.");
        }
        ve.lancarException();
    }

    public void selecionarItem(ItemSolicitacaoExterno item) {
        this.itemSolicitacao = (ItemSolicitacaoExterno) Util.clonarObjeto(item);
        recuperarObjetoDeCompra();
    }

    public void removerItem(ItemSolicitacaoExterno item) {
        if (item.isControleValor()) {
            itensControleValor.remove(item);
        } else {
            itensControleQuantidade.remove(item);
        }
    }

    private void adicionarItemValorQuantidade(ItemSolicitacaoExterno item) {
        if (item.isControleValor()) {
            Util.adicionarObjetoEmLista(itensControleValor, item);
        } else {
            Util.adicionarObjetoEmLista(itensControleQuantidade, item);
        }
    }

    private void separarItensValorQuantidade(List<ItemSolicitacaoExterno> itens) {
        for (ItemSolicitacaoExterno item : itens) {
            atribuirGrupoContaDespesa(item);
            marcarItemSolicitacaoExterno(item);
            if (item.isControleValor()) {
                itensControleValor.add(item);
                continue;
            }
            itensControleQuantidade.add(item);
        }
        Collections.sort(itensControleValor);
        Collections.sort(itensControleQuantidade);
    }

    public void listenerObjetoCompra() {
        if (itemSolicitacao.getObjetoCompra() != null) {
            recuperarObjetoDeCompra();
            itemSolicitacao.setDescricaoProduto(itemSolicitacao.getObjetoCompra().getDescricao());
            TabelaEspecificacaoObjetoCompraControlador controlador = (TabelaEspecificacaoObjetoCompraControlador) Util.getControladorPeloNome("tabelaEspecificacaoObjetoCompraControlador");
            controlador.recuperarObjetoCompra(itemSolicitacao.getObjetoCompra());
        }
    }

    public void confirmarItem() {
        try {
            if (itemSolicitacao.isControleValor()) {
                itemSolicitacao.setQuantidade(BigDecimal.ONE);
                itemSolicitacao.setValorUnitario(itemSolicitacao.getValorTotal());
            }
            validarItem();
            buscarSubtiposObjetoCompraDaConfiguracaoTipoObjetoCompra(itemSolicitacao);
            marcarItemSolicitacaoExterno(itemSolicitacao);
            if (itemSolicitacao.getNumero() == null) {
                itemSolicitacao.setNumero(getProximoNumeroItem(itemSolicitacao));
            }
            atribuirGrupoContaDespesa(itemSolicitacao);
            adicionarItemValorQuantidade(itemSolicitacao);
            cancelarItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarItem() {
        itemSolicitacao = null;
    }

    private void validarItem() {
        ValidacaoException ve = new ValidacaoException();
        if (itemSolicitacao.getTipoControle() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de controle deve ser informado.");
        }
        if (itemSolicitacao.getObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo material deve ser informado.");
        }
        if (itemSolicitacao.getUnidadeMedida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade de medida deve ser informado.");
        }
        if (itemSolicitacao.isControleQuantidade()) {
            if (itemSolicitacao.getQuantidade() == null || itemSolicitacao.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade deve ser informado.");
            }
            if (itemSolicitacao.getValorUnitario() == null || itemSolicitacao.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo valor unitário deve ser informado.");
            }
        }
        if (itemSolicitacao.isControleValor()) {
            if (itemSolicitacao.getValorTotal() == null || itemSolicitacao.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo valor total deve ser informado.");
            }
        }
        if (selecionado.isSolicitacaoExterna()) {
            if (Strings.isNullOrEmpty(itemSolicitacao.getFonteDaPesquisa())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo fonte da pesquisa deve ser informado.");
            }
            if (hasItensValor() || hasItensQuantidade()) {
                List<ItemSolicitacaoExterno> itens = itemSolicitacao.isControleValor() ? itensControleValor : itensControleQuantidade;
                for (ItemSolicitacaoExterno item : itens) {
                    if (!item.equals(itemSolicitacao)) {
                        ValidacaoObjetoCompraEspecificacao validacaoObjetoCompraEspecificacao = new ValidacaoObjetoCompraEspecificacao();
                        validacaoObjetoCompraEspecificacao.setObjetoCompraSelecionado(itemSolicitacao.getObjetoCompra());
                        validacaoObjetoCompraEspecificacao.setObjetoCompraEmLista(item.getObjetoCompra());
                        validacaoObjetoCompraEspecificacao.setEspeficicacaoSelecionada(itemSolicitacao.getDescricaoComplementar());
                        validacaoObjetoCompraEspecificacao.setEspeficicacaoEmLista(item.getDescricaoComplementar());
                        validacaoObjetoCompraEspecificacao.setNumeroItem(item.getNumero());
                        facade.getObjetoCompraFacade().validarObjetoCompraAndEspecificacaoIguais(validacaoObjetoCompraEspecificacao);
                    }
                }
            }
        }
        validarQuantidadeItemSolicitacao();
        ve.lancarException();
    }

    public void setSolicitacaoContratada(Boolean solicitacaoContratada) {
        this.solicitacaoContratada = solicitacaoContratada;
    }

    public boolean getSolicitacaoContratada() {
        return solicitacaoContratada;
    }

    public boolean hasItensQuantidade() {
        return itensControleQuantidade != null && !itensControleQuantidade.isEmpty();
    }

    public boolean hasItensValor() {
        return itensControleValor != null && !itensControleValor.isEmpty();
    }

    public void calcularValorTotal() {
        try {
            validarQuantidadeItemSolicitacao();
            BigDecimal valorTotal = itemSolicitacao.calcularValorTotal();
            itemSolicitacao.setValorTotal(valorTotal);
        } catch (ValidacaoException ve) {
            itemSolicitacao.setQuantidade(itemSolicitacao.getSaldoQuantidadeMaximaAdesao());
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarQuantidadeInferiorAQuantidadeDoPregao() {
        ValidacaoException ve = new ValidacaoException();
        validarSaldoDisponivelSolicitacaoInterna(itemSolicitacao, ve);
        ve.lancarException();

        if (itemSolicitacao.isControleValor()) {
            if (itemSolicitacao.getValorTotal().compareTo(itemSolicitacao.getValorMaximoAdesao()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado é superior ao valor máximo permitido de 50% do valor licitado. " +
                    "Valor máximo permitido: " + Util.formataValor(itemSolicitacao.getValorMaximoAdesao()) + ".");
            } else {
                if (itemSolicitacao.getValorTotal().compareTo(itemSolicitacao.getSaldoValorMaximoAdesao()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor informado é superior ao saldo disponível de " + Util.formataValor(itemSolicitacao.getSaldoValorMaximoAdesao()) + ".");
                    itemSolicitacao.setValorTotal(itemSolicitacao.getSaldoValorMaximoAdesao());
                }
            }
        } else {
            if (itemSolicitacao.getQuantidade().compareTo(itemSolicitacao.getQuantidadeMaximaAdesao()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada é superior a quantidade máxima permitida de 50% da quantidade licitada. " +
                    "Quantidade máxima permitida: " + Util.formataNumero(itemSolicitacao.getQuantidadeMaximaAdesao()) + ".");
            } else {
                if (itemSolicitacao.getQuantidade().compareTo(itemSolicitacao.getQuantidadeMaximaAdesao()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade informada é superior a quantidade disponível de " + Util.formataNumero(itemSolicitacao.getSaldoQuantidadeMaximaAdesao()) + ".");
                    itemSolicitacao.setQuantidade(itemSolicitacao.getSaldoQuantidadeMaximaAdesao());
                }
            }
        }
        ve.lancarException();
    }

    public BigDecimal getSaldoTotalDisponivelItem(ItemSolicitacaoExterno item) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<AgrupadorItemAdesaoAta, BigDecimal> entry : mapSaldoUnidadeGestora.entrySet()) {
            if (entry.getKey().getItemSolicitacaoExterno().isMesmoItemObjetoCompra(item) && entry.getKey().getAdesaoRealizada()) {
                total = total.add(entry.getValue());
            }
        }
        BigDecimal valorLicitado = item.getValorLicitado().multiply(new BigDecimal("2"));
        BigDecimal quantidadeLicitada = item.getQuantidadeLicitada().multiply(new BigDecimal("2"));
        return item.isControleValor() ? valorLicitado.subtract(total) : quantidadeLicitada.subtract(total);
    }

    private void validarQuantidadeInferiorAQuantidadeDoCertame() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemCertame itemCertame : buscarCertame().getListaItemCertame()) {
            if ((itemSolicitacao.getObjetoCompra() != null &&
                itemCertame.getObjetoCompra().equals(itemSolicitacao.getObjetoCompra()))) {
                if (itemSolicitacao.getQuantidade().compareTo(itemCertame.getQuantidadeItem()) > 0) {
                    itemSolicitacao.setQuantidade(BigDecimal.ZERO);
                    ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A quantidade informada é superior a quantidade máxima permitida. Quantidade máxima permitida:" + itemCertame.getQuantidadeItem() + ".");
                }
                break;
            }
        }
        ve.lancarException();
    }

    private void validarQuantidadeItemSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isSolicitacaoInterna()) {
            if (selecionado.getAtaRegistroPreco().getLicitacao().isPregao() || selecionado.getAtaRegistroPreco().getLicitacao().isRDCPregao()) {
                validarQuantidadeInferiorAQuantidadeDoPregao();
            }
            if ((selecionado.getAtaRegistroPreco().getLicitacao().isConcorrencia() || selecionado.getAtaRegistroPreco().getLicitacao().isRDCMapaComparativo())) {
                validarQuantidadeInferiorAQuantidadeDoCertame();
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<SelectItem> getTipoSolicitacaoRegistroPreco() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoSolicitacaoRegistroPreco tipoSolicitacao : TipoSolicitacaoRegistroPreco.values()) {
            retorno.add(new SelectItem(tipoSolicitacao, tipoSolicitacao.getDescricao()));
        }
        return retorno;
    }

    public void carregarItensDaAtaNaSolicitacao() {
        try {
            validarUnidade();
            limparListaItens();
            if (selecionado.getAtaRegistroPreco() != null) {
                selecionado.setAtaRegistroPreco(facade.getAtaRegistroPrecoFacade().recuperarComDependenciasAdesao(selecionado.getAtaRegistroPreco().getId()));
                criarItens();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            selecionado.setAtaRegistroPreco(null);
            FacesUtil.atualizarComponente("Formulario");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para processar os itens da ata é necessário selecionar o Órgão/Entidade/Fundo Solicitante.");
        }
        ve.lancarException();
    }

    public void criarItens() {
        if (selecionado.getAtaRegistroPreco().getLicitacao().isPregao() || selecionado.getAtaRegistroPreco().getLicitacao().isRDCPregao()) {
            criarItensAPartirDosItensDoPregao();
        }
        if (selecionado.getAtaRegistroPreco().getLicitacao().isConcorrencia() || selecionado.getAtaRegistroPreco().getLicitacao().isRDCMapaComparativo()) {
            criarItensAPartirDosItensDoMapaComparativo();
        }
    }

    private void criarItensAPartirDosItensDoMapaComparativo() {
        Map<AgrupadorItemAdesaoAta, BigDecimal> mapaAdesoesRealizadas = criarMapaAdesoesRealizadas();

        for (ItemCertame itemCertame : buscarCertame().getListaItemCertame()) {
            if (SituacaoItemCertame.VENCEDOR.equals(itemCertame.getSituacaoItemCertame())) {
                ItemSolicitacaoExterno novoItem = new ItemSolicitacaoExterno();
                novoItem.setSolicitacaoMaterialExterno(selecionado);
                novoItem.setItemProcessoCompra(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra());
                novoItem.setUnidadeMedida(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida());
                novoItem.definirTipoControle();
                novoItem.setValorUnitario(itemCertame.getPrecoItem());
                novoItem.setQuantidade(itemCertame.getQuantidadeItem());
                novoItem.setNumero(itemCertame.getNumeroItem());
                novoItem.setQuantidadeLicitada(itemCertame.getQuantidadeItem());
                novoItem.setValorLicitado(novoItem.getQuantidadeLicitada().multiply(itemCertame.getPrecoItem()));
                novoItem.setObjetoCompra(itemCertame.getObjetoCompra());
                novoItem.setDescricaoProduto(itemCertame.getDescricaoProdutoItem());
                novoItem.setDescricaoComplementar(itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getDescricaoComplementar());
                atribuirGrupoContaDespesa(novoItem);
                unidadeGestora = recuperarUnidadeGestoraUnidadeCorrespondente();

                criarMapaNovaAdesao(novoItem, mapaAdesoesRealizadas);
                if (novoItem.isControleQuantidade()) {
                    novoItem.setValorTotal(novoItem.calcularValorTotal());
                }
                adicionarItemValorQuantidade(novoItem);
            }
        }
        mapSaldoUnidadeGestora.putAll(mapaAdesoesRealizadas);
    }

    private void criarItensAPartirDosItensDoPregao() {
        Map<AgrupadorItemAdesaoAta, BigDecimal> mapaAdesoesRealizadas = criarMapaAdesoesRealizadas();

        for (ItemPregao itemPregao : buscarPregao().getListaDeItemPregao()) {
            if (itemPregao.getItemPregaoLanceVencedor() != null && itemPregao.getItemPregaoLanceVencedor().getStatus().isVencedor()) {

                if (itemPregao.getPregao().getLicitacao().isTipoApuracaoPrecoItem()) {
                    novoItemSolicitacaoPorItemPregao(mapaAdesoesRealizadas, itemPregao.getObjetoCompra(), itemPregao.getValor(), itemPregao.getItemPregaoItemProcesso().getItemProcessoDeCompra(), itemPregao.getQuantidade());
                } else {
                    if (itemPregao.getPregao().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                        List<ItemProcessoDeCompra> itensProcessoCompra = facade.getProcessoDeCompraFacade().recuperarItensDoLote(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
                        for (ItemProcessoDeCompra itemProcessoCompra : itensProcessoCompra) {
                            novoItemSolicitacaoPorItemPregao(mapaAdesoesRealizadas, itemProcessoCompra.getObjetoCompra(), itemProcessoCompra.getItemSolicitacaoMaterial().getPreco(), itemProcessoCompra, itemProcessoCompra.getItemSolicitacaoMaterial().getQuantidade());
                        }
                    } else {
                        for (ItemPregaoLoteItemProcesso item : itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso()) {
                            novoItemSolicitacaoPorItemPregao(mapaAdesoesRealizadas, item.getItemProcessoDeCompra().getObjetoCompra(), item.getValor(), item.getItemProcessoDeCompra(), item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getQuantidade());
                        }
                    }
                }
            }
        }
        mapSaldoUnidadeGestora.putAll(mapaAdesoesRealizadas);
    }

    private void novoItemSolicitacaoPorItemPregao(Map<AgrupadorItemAdesaoAta, BigDecimal> mapaAdesoesRealizadas, ObjetoCompra objetoCompra, BigDecimal valor, ItemProcessoDeCompra itemProcessoDeCompra, BigDecimal quantidade) {

        ItemSolicitacaoExterno novoItem = new ItemSolicitacaoExterno();
        novoItem.setSolicitacaoMaterialExterno(selecionado);
        novoItem.setObjetoCompra(objetoCompra);
        novoItem.setUnidadeMedida(itemProcessoDeCompra.getItemSolicitacaoMaterial().getUnidadeMedida());
        novoItem.setNumero(itemProcessoDeCompra.getNumero());
        novoItem.setItemProcessoCompra(itemProcessoDeCompra);
        novoItem.definirTipoControle();
        novoItem.setQuantidadeLicitada(quantidade);
        novoItem.setQuantidade(novoItem.isControleValor() ? BigDecimal.ONE : BigDecimal.ZERO);
        novoItem.setValorUnitario(valor);
        novoItem.setValorLicitado(novoItem.isControleValor() ? valor : novoItem.getQuantidadeLicitada().multiply(valor));
        novoItem.setDescricaoProduto(itemProcessoDeCompra.getDescricao());
        novoItem.setDescricaoComplementar(itemProcessoDeCompra.getDescricaoComplementar());
        atribuirGrupoContaDespesa(novoItem);
        unidadeGestora = recuperarUnidadeGestoraUnidadeCorrespondente();

        criarMapaNovaAdesao(novoItem, mapaAdesoesRealizadas);
        if (novoItem.isControleQuantidade()) {
            novoItem.setValorTotal(novoItem.calcularValorTotal());
        }
        adicionarItemValorQuantidade(novoItem);
    }

    private BigDecimal atualizarQuantidadeItemParaProcessoIrp(ObjetoCompra objetoCompra, ItemProcessoDeCompra itemProcessoDeCompra, BigDecimal quantidade) {
        ItemParticipanteIRP itemPartIRP = facade.getAtaRegistroPrecoFacade().getParticipanteIRPFacade().buscarItemPorUnidade(
            selecionado.getAtaRegistroPreco().getLicitacao().getId(),
            selecionado.getAtaRegistroPreco().getUnidadeOrganizacional(),
            objetoCompra,
            itemProcessoDeCompra.getNumero(),
            itemProcessoDeCompra.getLoteProcessoDeCompra().getNumero());
        if (itemPartIRP != null) {
            quantidade = itemPartIRP.getQuantidade();
        }
        return quantidade;
    }

    private void criarMapaNovaAdesao(ItemSolicitacaoExterno novoItem, Map<AgrupadorItemAdesaoAta, BigDecimal> mapAdesoesRealizadas) {
        AgrupadorItemAdesaoAta agrupador = new AgrupadorItemAdesaoAta(unidadeGestora, novoItem);
        if (mapSaldoUnidadeGestora.get(agrupador) == null) {
            mapSaldoUnidadeGestora.put(agrupador, BigDecimal.ZERO);
        }
        BigDecimal qtdeUtilizada = facade.recuperarQuantidadeUtilizadaOutrasSolicitacoes(selecionado.getAtaRegistroPreco(), novoItem.getObjetoCompra(), selecionado.getUnidadeOrganizacional(), novoItem.getItemProcessoCompra());
        BigDecimal saldoQuantidade = novoItem.getQuantidadeMaximaAdesao();
        novoItem.setSaldoQuantidadeMaximaAdesao(saldoQuantidade);
        novoItem.setQuantidade(saldoQuantidade);

        BigDecimal valorUtilizado = facade.recuperarValorUtilizadaOutrasSolicitacoes(selecionado.getAtaRegistroPreco(), novoItem.getObjetoCompra(), selecionado.getUnidadeOrganizacional(), novoItem.getItemProcessoCompra());
        BigDecimal saldoValor = novoItem.getValorMaximoAdesao();
        novoItem.setSaldoValorMaximoAdesao(saldoValor);
        novoItem.setValorTotal(novoItem.isControleValor() ? saldoValor : novoItem.getValorTotal());

        for (Map.Entry<AgrupadorItemAdesaoAta, BigDecimal> entryAderido : mapAdesoesRealizadas.entrySet()) {
            ItemSolicitacaoExterno itemAderido = entryAderido.getKey().getItemSolicitacaoExterno();
            if (unidadeGestora.equals(entryAderido.getKey().getUnidadeGestora())
                && itemAderido.isMesmoItemObjetoCompra(novoItem)) {
                novoItem.setSelecionado(isOperacaoEditar());
                novoItem.setDescricaoComplementar(itemAderido.getDescricaoComplementar());
                if (novoItem.isControleQuantidade()) {
                    saldoQuantidade = isOperacaoNovo() ? novoItem.getQuantidadeMaximaAdesao().subtract(qtdeUtilizada) : qtdeUtilizada;
                    novoItem.setSaldoQuantidadeMaximaAdesao(saldoQuantidade);
                    novoItem.setQuantidade(saldoQuantidade);
                } else {
                    saldoValor = isOperacaoNovo() ? novoItem.getValorMaximoAdesao().subtract(valorUtilizado) : valorUtilizado;
                    novoItem.setSaldoValorMaximoAdesao(saldoValor);
                    novoItem.setValorTotal(saldoValor);
                }
            }
        }
        mapSaldoUnidadeGestora.put(agrupador, mapSaldoUnidadeGestora.get(agrupador).add(novoItem.isControleValor() ? novoItem.getValorTotal() : novoItem.getQuantidade()));
    }

    private Map<AgrupadorItemAdesaoAta, BigDecimal> criarMapaAdesoesRealizadas() {
        List<SolicitacaoMaterialExterno> solicitacoes = facade.buscarSoliciacoesPorAtaResgistroPreco(selecionado.getAtaRegistroPreco());
        Map<AgrupadorItemAdesaoAta, BigDecimal> mapa = new HashMap<>();
        for (SolicitacaoMaterialExterno sol : solicitacoes) {

            Set<UnidadeGestora> unidadesGestoraAderidas = recuperarUndiadeGestoraAdesoesRealizadas(sol);
            for (UnidadeGestora unidadeGestora : unidadesGestoraAderidas) {

                for (ItemSolicitacaoExterno itemAderido : sol.getItensDaSolicitacao()) {
                    AgrupadorItemAdesaoAta agrupador = new AgrupadorItemAdesaoAta(unidadeGestora, itemAderido);
                    agrupador.setAdesaoRealizada(true);
                    if (mapa.get(agrupador) == null) {
                        mapa.put(agrupador, BigDecimal.ZERO);
                    }
                    mapa.put(agrupador, mapa.get(agrupador).add(itemAderido.isControleValor() ? itemAderido.getValorTotal() : itemAderido.getQuantidade()));
                }
            }
        }
        return mapa;
    }

    private UnidadeGestora recuperarUnidadeGestoraUnidadeCorrespondente() {
        List<HierarquiaOrganizacional> hoOrcamentarias = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getHierarquiaOrganizacional().getSubordinada(), selecionado.getDataSolicitacao());
        return facade.getUnidadeGestoraFacade().recuperarPorUnidadeOrcamentariaAndTipo(facade.getSistemaFacade().getExercicioCorrente(), TipoUnidadeGestora.ADMINISTRATIVO, hoOrcamentarias);
    }

    private Set<UnidadeGestora> recuperarUndiadeGestoraAdesoesRealizadas(SolicitacaoMaterialExterno sol) {
        List<HierarquiaOrganizacional> hoOrcamentarias = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(sol.getUnidadeOrganizacional(), sol.getDataSolicitacao());
        Set<UnidadeGestora> unidadesGestoraAderidas = new HashSet<>();
        UnidadeGestora ug = facade.getUnidadeGestoraFacade().recuperarPorUnidadeOrcamentariaAndTipo(facade.getSistemaFacade().getExercicioCorrente(), TipoUnidadeGestora.ADMINISTRATIVO, hoOrcamentarias);
        if (ug != null) {
            unidadesGestoraAderidas.add(ug);
        }
        return unidadesGestoraAderidas;
    }

    private Certame buscarCertame() {
        return facade.getCertameFacade().recuperarCertameDaLicitacao(selecionado.getAtaRegistroPreco().getLicitacao());
    }

    private Pregao buscarPregao() {
        return facade.getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado.getAtaRegistroPreco().getLicitacao());
    }

    private void limparListaItens() {
        if (hasItensQuantidade()) {
            itensControleQuantidade.clear();
        }
        if (hasItensValor()) {
            itensControleValor.clear();
        }
        mapSaldoUnidadeGestora = new HashMap<>();
    }

    public boolean isTodosItensValorMarcados() {
        try {
            for (ItemSolicitacaoExterno ise : itensControleValor) {
                if (ise.getSelecionado() == null || !ise.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodosItensValor() {
        for (ItemSolicitacaoExterno ise : itensControleValor) {
            marcarItemSolicitacaoExterno(ise);
        }
    }

    public void desmarcarTodosItensValor() {
        for (ItemSolicitacaoExterno ise : itensControleValor) {
            desmarcarItemSolicitacaoExterno(ise);
        }
    }

    public boolean isTodosItensQuantidadeMarcados() {
        try {
            for (ItemSolicitacaoExterno ise : itensControleQuantidade) {
                if (ise.getSelecionado() == null || !ise.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodosItensQuantidade() {
        for (ItemSolicitacaoExterno ise : itensControleQuantidade) {
            marcarItemSolicitacaoExterno(ise);
        }
    }

    public void desmarcarTodosItensQuantidade() {
        for (ItemSolicitacaoExterno ise : itensControleQuantidade) {
            desmarcarItemSolicitacaoExterno(ise);
        }
    }

    public void desmarcarItemSolicitacaoExterno(ItemSolicitacaoExterno ise) {
        ise.setSelecionado(Boolean.FALSE);
    }

    public void marcarItemSolicitacaoExterno(ItemSolicitacaoExterno ise) {
        ise.setSelecionado(Boolean.TRUE);
    }

    public void processarMudancaDeTipoDeSolicitacao() {
        if (selecionado.isSolicitacaoExterna()) {
            selecionado.setAtaRegistroPreco(null);
            limparListaItens();
        }
        if (selecionado.isSolicitacaoInterna()) {
            cancelarItemSolicitacaoAndItemReserva();
            selecionado.setPessoaJuridica(null);
            selecionado.setNumeroAtaExterna(null);
        }
    }

    public boolean isSolicitacaoContrada() {
        try {
            return facade.verificarSolicitacaoContratada(selecionado);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isSolicitacaoComAdesaoExterna() {
        try {
            return facade.verificarSolicitacaoComAdesaoExterna(selecionado);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void validarExclusaoEntidade() {
        super.validarExclusaoEntidade();
        ValidacaoException ve = new ValidacaoException();
        if (isSolicitacaoComAdesaoExterna()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "A solicitação está vinculada a um registro de preço externo.");
        }
        if (isSolicitacaoContrada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não Permitida!", "A solicitação está vinculada a um contrato.");
        }
        ve.lancarException();
    }

    public List<FonteDespesaORC> buscarFonteDespesaORCPorCodigoOrDescricao(String codigoOrDescricao) {
        if (this.itemSolicitacaoDotacao.getDespesaORC() != null) {
            return facade.getFonteDespesaORCFacade().completaFonteDespesaORC(codigoOrDescricao.trim(),
                this.itemSolicitacaoDotacao.getDespesaORC());
        }
        return Lists.newArrayList();
    }

    private void buscarSubtiposObjetoCompraDaConfiguracaoTipoObjetoCompra(ItemSolicitacaoExterno item) {
        try {
            Set<SubTipoObjetoCompra> subTipos = new HashSet<>();
            if (item.getObjetoCompra().getTipoObjetoCompra() != null && selecionado.getDataSolicitacao() != null) {
                List<ConfigTipoObjetoCompra> configuracoes = facade.getConfigTipoObjetoCompraFacade().buscarConfiguracoesVigente(
                    selecionado.getDataSolicitacao(),
                    item.getObjetoCompra().getTipoObjetoCompra());
                if (configuracoes != null && !configuracoes.isEmpty()) {
                    for (ConfigTipoObjetoCompra config : configuracoes) {
                        if (config.getSubtipoObjetoCompra() != null) {
                            subTipos.add(config.getSubtipoObjetoCompra());
                        }
                    }
                    item.setSubTiposObjetoCompra(Lists.newArrayList(subTipos));
                    if (item.getSubTiposObjetoCompra().size() == 1 && item.getSubTipoObjetoCompra() == null) {
                        item.setSubTipoObjetoCompra(item.getSubTiposObjetoCompra().get(0));
                    }
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }


    public void definirDependenciasParaCriarItemSolicitacaoExternoDotacao(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        try {
            this.itemSolicitacao = itemSolicitacaoExterno;
            validarSubTipoObjetoCompra(itemSolicitacao);
            inserirItemSolicitacaoExternoDotacao();
            FacesUtil.executaJavaScript("dlgItemSolicitacaoExternoDotacao.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void validarSubTipoObjetoCompra(ItemSolicitacaoExterno item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getSubTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo subtipo de objeto de compra deve ser informado.");
        }
        ve.lancarException();
    }

    public void inserirItemSolicitacaoExternoDotacao() {
        itemSolicitacaoDotacao = new ItemSolicitacaoExternoDotacao();
        itemSolicitacaoDotacao.setItemSolicitacaoExterno(itemSolicitacao);
        itemSolicitacaoDotacao.setValorReservado(itemSolicitacao.getValorParaReservar());
    }

    private void validarItemSolicitacaoExternoDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (itemSolicitacaoDotacao.getDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo elemento de despesa deve ser informado.");
        }
        if (itemSolicitacaoDotacao.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fonte de despesa orçamentária deve ser informado.");
        }
        if (itemSolicitacaoDotacao.getValorReservado() == null ||
            itemSolicitacaoDotacao.getValorReservado().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O valor da reserva deve ser informado, com um valor maior que zero(0).");
        }
        if (itemSolicitacaoDotacao.getValorReservado() != null &&
            itemSolicitacaoDotacao.getValorReservado().compareTo(this.itemSolicitacao.getValorParaReservar()) > 0) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O valor da reserva ultrapassa o valor à ser reservado.");
        }
        ve.lancarException();
    }

    public void confirmarItemSolicitacaoExternoDotacao() {
        try {
            validarItemSolicitacaoExternoDotacao();
            itemSolicitacao.getDotacoes().add(itemSolicitacaoDotacao);
            cancelarItemSolicitacaoAndItemReserva();
            FacesUtil.executaJavaScript("dlgItemSolicitacaoExternoDotacao.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void cancelarItemSolicitacaoAndItemReserva() {
        itemSolicitacao = null;
        itemSolicitacaoDotacao = null;
    }

    public void cancelarItemSolicitacaoExternoDotacao() {
        itemSolicitacao = null;
        this.itemSolicitacaoDotacao = null;
        FacesUtil.executaJavaScript("dlgItemSolicitacaoExternoDotacao.hide()");
    }

    public void removerItemSolicitacaoExternoDotacao(ItemSolicitacaoExternoDotacao itemSolicitacaoExternoDotacao) {
        this.itemSolicitacao.getDotacoes().remove(itemSolicitacaoExternoDotacao);
    }

    public void limparDadosAta() {
        selecionado.setAtaRegistroPreco(null);
        limparListaItens();
    }

    public void atribuirNullParaEspecificao() {
        itemSolicitacao.setDescricaoComplementar(null);
        itemSolicitacao.setObjetoCompra(null);
    }

    public BigDecimal getValorTotalItensPorValor() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemSolicitacaoExterno item : itensControleValor) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalItensPorQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemSolicitacaoExterno item : itensControleQuantidade) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalItens() {
        return getValorTotalItensPorValor().add(getValorTotalItensPorQuantidade());
    }

    public BigDecimal getSaldoOrcamentario() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (itemSolicitacaoDotacao != null
            && itemSolicitacaoDotacao.getDespesaORC() != null
            && itemSolicitacaoDotacao.getFonteDespesaORC() != null) {
            saldo = facade.buscarSaldoFonteDespesaORC(itemSolicitacaoDotacao);
        }
        return saldo;
    }

    public boolean hasDotacaoAdiconadaParaItem(ItemSolicitacaoExterno item) {
        return item != null && item.getDotacoes() != null && !item.getDotacoes().isEmpty();
    }

    public List<SolicitacaoMaterialExterno> buscarSolicitacaoMaterialExternoTipoExternaPorAnoOrNumeroOndeUsuarioGestorLicitacao(String anoOrNumero) {
        return facade.buscarSolicitacaoMaterialExternoPorAnoOrNumeroAndTipoOndeUsuarioGestorLicitacao(anoOrNumero,
            TipoSolicitacaoRegistroPreco.EXTERNA, facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().
            buscarHierarquiaOrganizacionalAdministrativaPorCodigoOrDescricaoAndNivelAndDataAndUsuarioCorrenteAndGestorLicitacao(parte, null,
                facade.getSistemaFacade().getDataOperacao(), facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<ItemSolicitacaoExterno> getItensControleValor() {
        return itensControleValor;
    }

    public void setItensControleValor(List<ItemSolicitacaoExterno> itensControleValor) {
        this.itensControleValor = itensControleValor;
    }

    public List<ItemSolicitacaoExterno> getItensControleQuantidade() {
        return itensControleQuantidade;
    }

    public void setItensControleQuantidade(List<ItemSolicitacaoExterno> itensControleQuantidade) {
        this.itensControleQuantidade = itensControleQuantidade;
    }
}
