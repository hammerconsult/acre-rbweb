package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoIRP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ParticipanteIRPFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParticipanteIRP", pattern = "/participante-intencao-registro-de-preco/novo/", viewId = "/faces/administrativo/licitacao/intencao-registro-preco-participante/edita.xhtml"),
    @URLMapping(id = "editarParticipanteIRP", pattern = "/participante-intencao-registro-de-preco/editar/#{participanteIRPControlador.id}/", viewId = "/faces/administrativo/licitacao/intencao-registro-preco-participante/edita.xhtml"),
    @URLMapping(id = "listarParticipanteIRP", pattern = "/participante-intencao-registro-de-preco/listar/", viewId = "/faces/administrativo/licitacao/intencao-registro-preco-participante/lista.xhtml"),
    @URLMapping(id = "verParticipanteIRP", pattern = "/participante-intencao-registro-de-preco/ver/#{participanteIRPControlador.id}/", viewId = "/faces/administrativo/licitacao/intencao-registro-preco-participante/visualizar.xhtml")
})
public class ParticipanteIRPControlador extends PrettyControlador<ParticipanteIRP> implements Serializable, CRUD {

    @EJB
    private ParticipanteIRPFacade facade;
    private UnidadeParticipanteIRP unidadeParticipante;
    private List<ItemParticipanteIRP> itensIRPDisponieis;
    private boolean todosItensSelecionados;

    public ParticipanteIRPControlador() {
        super(ParticipanteIRP.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/participante-intencao-registro-de-preco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoParticipanteIRP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setParticipante(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataInteresse(getDataOperacao());
        selecionado.setSituacao(SituacaoIRP.EM_ELABORACAO);
        itensIRPDisponieis = Lists.newArrayList();
        novaUnidade();
        recuperarIrp();
    }

    @URLAction(mappingId = "editarParticipanteIRP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (SituacaoIRP.CONCLUIDA.equals(selecionado.getIntencaoRegistroDePreco().getSituacaoIRP())) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
            FacesUtil.addOperacaoNaoPermitida("O participante não pederá sofrer alterações, pois a intenção de registro de preço foi concluída.");
        }
        ordenarItensIRPPorLoteAndItem(selecionado.getItens());
        inicializarAtributos();
    }

    @URLAction(mappingId = "verParticipanteIRP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        for (ItemParticipanteIRP item : selecionado.getItens()) {
            atribuirGrupoContaDespesa(item.getItemIntencaoRegistroPreco());
        }
        ordenarItensIRPPorLoteAndItem(selecionado.getItens());
    }

    private void inicializarAtributos() {
        recupearItensIRP();
    }

    private void recuperarIrp() {
        IntencaoRegistroPreco irp = (IntencaoRegistroPreco) Web.pegaDaSessao("IRP");
        if (irp != null) {
            selecionado.setIntencaoRegistroDePreco(irp);
            recupearItensIRP();
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            adicionarItensSelecionadosAoParticipantes();
            validarParticipanteSemItens();
            selecionado = facade.salvarParticipante(selecionado);
            FacesUtil.addOperacaoRealizada("Intenção salva com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.debug("Salvar participante intenção de registro de preço {}", ex);
            descobrirETratarException(ex);
        }
    }

    private void validarParticipanteSemItens() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItens().isEmpty() && selecionado.isParticipanteAprovado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item adicionado para a unidade participante.");
        }
        ve.lancarException();
    }

    private void adicionarItensSelecionadosAoParticipantes() {
        selecionado.setItens(Lists.<ItemParticipanteIRP>newArrayList());
        for (ItemParticipanteIRP item : itensIRPDisponieis) {
            if (item.getSelecionado()) {
                selecionado.getItens().add(item);
                if (item.getItemIntencaoRegistroPreco().isTipoControlePorValor()) {
                    item.setQuantidade(BigDecimal.ONE);
                }
            }
        }
    }

    private void validarSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.isParticipanteAprovado()) {
            if (Util.isListNullOrEmpty(itensIRPDisponieis)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item encontado para a intenção de registro de preço selecionada.");
            } else if (itensIRPDisponieis.stream().noneMatch(ItemParticipanteIRP::getSelecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item selecionado para o particante irp.");
            }
            ve.lancarException();

            for (ItemParticipanteIRP item : itensIRPDisponieis) {
                if (item.getSelecionado()) {
                    if (item.isTipoControlePorQuantidade()) {
                        if (item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser informado com valor maior que zero(0) para item: " + item.getItemIntencaoRegistroPreco().getObjetoCompra() + ".");
                        }
                    } else {
                        if (item.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser informado com valor maior que zero(0) para item: " + item.getItemIntencaoRegistroPreco().getObjetoCompra() + ".");
                        }
                        if (item.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Quando o controle for por valor a quantidade o item deve ser igual um(1).");
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void listenerUnidadeParticipante() {
        try {
            validarUnidadeParticipanteJaCadastrada();
        } catch (ValidacaoException ve) {
            selecionado.setHierarquiaOrganizacional(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarUnidadeParticipanteJaCadastrada() {
        ValidacaoException ve = new ValidacaoException();
        List<UnidadeOrganizacional> unidades = facade.getIntencaoRegistroPrecoFacade().buscarUnidadesParticipantesIRP(selecionado.getIntencaoRegistroDePreco());
        unidades.stream()
            .filter(unid -> unid.equals(selecionado.getHierarquiaOrganizacional().getSubordinada()))
            .map(unid -> "A unidade <b>" + selecionado.getHierarquiaOrganizacional() + "</b>, já foi cadastrada como participante no irp.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    public boolean isTodosItensSelecionados() {
        return todosItensSelecionados;
    }

    public void setItemSelecionado(ItemParticipanteIRP item, boolean selecionado) {
        item.setSelecionado(selecionado);
        item.setQuantidade((item.getSelecionado() && item.getItemIntencaoRegistroPreco().isTipoControlePorValor()) ? BigDecimal.ONE : BigDecimal.ZERO);
        item.setValor(BigDecimal.ZERO);

    }

    public void setTodosItensSelecionados(boolean todosSelecionados) {
        this.todosItensSelecionados = todosSelecionados;
        if (itensIRPDisponieis != null && !itensIRPDisponieis.isEmpty()) {
            for (ItemParticipanteIRP item : itensIRPDisponieis) {
                setItemSelecionado(item, todosSelecionados);
            }
        }
    }

    private void validarVigenciaUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeParticipante.getFimVigencia() != null && unidadeParticipante.getFimVigencia().before(unidadeParticipante.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser posterior ao início de vigência.");
        }
        ve.lancarException();
        for (UnidadeParticipanteIRP unidadeAdicionada : selecionado.getUnidades()) {
            if (!unidadeAdicionada.equals(unidadeParticipante)) {
                unidadeAdicionada.validarVigencia(unidadeParticipante);
            }
        }
    }

    public void adicionarUnidade() {
        try {
            Util.validarCampos(unidadeParticipante);
            validarVigenciaUnidade();
            selecionado.setHierarquiaOrganizacional(unidadeParticipante.getHierarquiaParticipante());
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidadeParticipante);
            cancelarUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void novaUnidade() {
        unidadeParticipante = new UnidadeParticipanteIRP();
        unidadeParticipante.setParticipanteIRP(selecionado);
        unidadeParticipante.setInicioVigencia(getDataOperacao());
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void cancelarUnidade() {
        unidadeParticipante = null;
    }

    public void editarUnidade(UnidadeParticipanteIRP unidade) {
        unidadeParticipante = (UnidadeParticipanteIRP) Util.clonarObjeto(unidade);
    }

    public void removerUnidade(UnidadeParticipanteIRP unidade) {
        selecionado.getUnidades().remove(unidade);
        selecionado.setHierarquiaOrganizacional(null);
    }

    public List<IntencaoRegistroPreco> completarIRP(String filtro) {
        List<IntencaoRegistroPreco> list = facade.getIntencaoRegistroPrecoFacade().buscarIRPVigentePorSituacao(selecionado.getDataInteresse(), filtro.trim(), SituacaoIRP.EM_ELABORACAO);
        if (list.isEmpty()) {
            FacesUtil.addAtencao("Não foi encontrado intenção de registro de preço vigente em elaboração.");
            return list;
        }
        return list;
    }

    public void recupearItensIRP() {
        IntencaoRegistroPreco intencao = facade.getIntencaoRegistroPrecoFacade().recuperarComDependenciasItens(selecionado.getIntencaoRegistroDePreco().getId());
        itensIRPDisponieis = Lists.newArrayList();
        List<ItemIntencaoRegistroPreco> itensIRPSalvos = getItensIRPDosItensParticipantesSalvos();

        for (LoteIntencaoRegistroPreco loteIRP : intencao.getLotes()) {
            for (ItemIntencaoRegistroPreco itemIRP : loteIRP.getItens()) {
                ItemIntencaoRegistroPreco itemSalvo = getItemIRPDoItemParticipante(itensIRPSalvos, itemIRP);
                ItemParticipanteIRP novoItem = criarOrAtualizarItemParticipante(itemIRP, itemSalvo);
                novoItem.setSelecionado(true);
                itensIRPDisponieis.add(novoItem);
            }
        }
        ordenarItensIRPPorLoteAndItem(itensIRPDisponieis);
    }

    public void ordenarItensIRPPorLoteAndItem(List<ItemParticipanteIRP> itens) {
        Collections.sort(itens);
    }

    private ItemParticipanteIRP criarOrAtualizarItemParticipante(ItemIntencaoRegistroPreco itemIRP, ItemIntencaoRegistroPreco itemIrpSalvo) {
        ItemParticipanteIRP novoItem = new ItemParticipanteIRP();
        atribuirGrupoContaDespesa(itemIRP);
        novoItem.setParticipanteIRP(selecionado);
        novoItem.setItemIntencaoRegistroPreco(itemIRP);
        novoItem.setSelecionado(true);

        if (itemIrpSalvo == null) {
            novoItem.setQuantidade(itemIRP.isTipoControlePorValor() ? BigDecimal.ONE : BigDecimal.ZERO);
            novoItem.setValor(BigDecimal.ZERO);
        } else {
            for (ItemParticipanteIRP itemDoSelecionado : selecionado.getItens()) {
                if (itemDoSelecionado.getItemIntencaoRegistroPreco().getId().equals(itemIrpSalvo.getId())) {
                    novoItem.setQuantidade(itemDoSelecionado.getQuantidade());
                    novoItem.setValor(itemDoSelecionado.getValor());

                }
            }
        }
        return novoItem;
    }

    private ItemIntencaoRegistroPreco getItemIRPDoItemParticipante(List<ItemIntencaoRegistroPreco> itensIRPSalvos, ItemIntencaoRegistroPreco itemIRP) {
        ItemIntencaoRegistroPreco itemSalvo;
        try {
            itemSalvo = itensIRPSalvos.get(itensIRPSalvos.indexOf(itemIRP));
        } catch (ArrayIndexOutOfBoundsException a) {
            itemSalvo = null;
        }
        return itemSalvo;
    }

    private List<ItemIntencaoRegistroPreco> getItensIRPDosItensParticipantesSalvos() {
        List<ItemIntencaoRegistroPreco> retorno = Lists.newArrayList();
        for (ItemParticipanteIRP item : selecionado.getItens()) {
            if (item.getItemIntencaoRegistroPreco() != null) {
                retorno.add(item.getItemIntencaoRegistroPreco());
            }
        }
        return retorno;
    }

    private void atribuirGrupoContaDespesa(ItemIntencaoRegistroPreco item) {
        if (item != null && item.getObjetoCompra() != null) {
            try {
                item.getObjetoCompra().setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra()));
            } catch (ExcecaoNegocioGenerica e) {
                logger.debug(e.getMessage());
            }
        }
    }

    public UnidadeParticipanteIRP getUnidadeParticipante() {
        return unidadeParticipante;
    }

    public void setUnidadeParticipante(UnidadeParticipanteIRP unidadeParticipante) {
        this.unidadeParticipante = unidadeParticipante;
    }

    public List<ItemParticipanteIRP> getItensIRPDisponieis() {
        return itensIRPDisponieis;
    }

    public void setItensIRPDisponieis(List<ItemParticipanteIRP> itensIRPDisponieis) {
        this.itensIRPDisponieis = itensIRPDisponieis;
    }
}
