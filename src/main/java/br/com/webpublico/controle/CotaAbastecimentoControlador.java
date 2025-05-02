package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.frotas.TipoCotaAbastecimento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/09/14
 * Time: 08:45
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "cotaAbastecimentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "cotaAbastecimentoNovo", pattern = "/cota-abastecimento/novo/",
        viewId = "/faces/administrativo/frota/cotaabastecimento/edita.xhtml"),
    @URLMapping(id = "cotaAbastecimentoListar", pattern = "/cota-abastecimento/listar/",
        viewId = "/faces/administrativo/frota/cotaabastecimento/lista.xhtml"),
    @URLMapping(id = "cotaAbastecimentoEditar", pattern = "/cota-abastecimento/editar/#{cotaAbastecimentoControlador.id}/",
        viewId = "/faces/administrativo/frota/cotaabastecimento/edita.xhtml"),
    @URLMapping(id = "cotaAbastecimentoVer", pattern = "/cota-abastecimento/ver/#{cotaAbastecimentoControlador.id}/",
        viewId = "/faces/administrativo/frota/cotaabastecimento/visualizar.xhtml"),
})
public class CotaAbastecimentoControlador extends PrettyControlador<CotaAbastecimento> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CotaAbastecimentoControlador.class);

    @EJB
    private CotaAbastecimentoFacade cotaAbastecimentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ItemCotaAbastecimento itemCotaAbastecimento;
    private ConverterAutoComplete converterItemCotaAbastecimento;
    @EJB
    private ItemCotaAbastecimentoFacade itemCotaAbastecimentoFacade;
    @EJB
    private ItemContratoFacade itemContratoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    private boolean isRegistroEditavel;

    public CotaAbastecimentoControlador() {
        super(CotaAbastecimento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cota-abastecimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return cotaAbastecimentoFacade;
    }

    @URLAction(mappingId = "cotaAbastecimentoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "cotaAbastecimentoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "cotaAbastecimentoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosDaTela();
        validarUnidadeGestoraParaEdicao();
    }

    private void validarUnidadeGestoraParaEdicao() {
        if (!isRegistroEditavel()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoNaoPermitida("Apenas usuários da unidade gestora: " + selecionado.getHierarquiaOrganizacionalGestora() + ", podem editar esta Cota para Abastecimento.");
        }
    }

    private void verificarIsRegistroEditavel() {
        isRegistroEditavel = false;
        for (UsuarioUnidadeOrganizacional usuarioUnidadeOrganizacional : sistemaFacade.getUsuarioCorrente().getUsuarioUnidadeOrganizacional()) {
            if (usuarioUnidadeOrganizacional.getUnidadeOrganizacional().equals(selecionado.getUnidadeGestora())) {
                isRegistroEditavel = true;
                break;
            }
        }
    }

    private void inicializarAtributosDaTela() {
        carregarUnidadeOrganizacional();
        verificarIsRegistroEditavel();
    }

    private void carregarUnidadeOrganizacional() {
        if (selecionado != null && selecionado.getUnidadeCotista() != null) {
            selecionado.setHierarquiaOrganizacionalCotista(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
                selecionado.getUnidadeCotista(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
        if (selecionado != null && selecionado.getUnidadeGestora() != null) {
            selecionado.setHierarquiaOrganizacionalGestora(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
                selecionado.getUnidadeGestora(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
        if (selecionado != null && selecionado.getUnidadeFilha() != null) {
            selecionado.setHierarquiaOrganizacionalFilha(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
                selecionado.getUnidadeFilha(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        }
    }

    public void novoItemCotaAbastecimento() {
        try {
            validarNovoItemCotaAbastecimento();
            itemCotaAbastecimento = new ItemCotaAbastecimento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovoItemCotaAbastecimento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + (TipoCotaAbastecimento.DISTRIBUICAO.equals(selecionado.getTipoCotaAbastecimento()) ? "Cota de Distribuição" : "Contrato") + " deve ser informado.");
        }
        ve.lancarException();
    }

    public ItemCotaAbastecimento getItemCotaAbastecimento() {
        return itemCotaAbastecimento;
    }

    public void setItemCotaAbastecimento(ItemCotaAbastecimento itemCotaAbastecimento) {
        this.itemCotaAbastecimento = itemCotaAbastecimento;
    }

    public void validarCotaAbastecimento() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        ve.lancarException();
        if (selecionado.isNormal() &&
            (selecionado.getHierarquiaOrganizacionalGestora() == null ||
                selecionado.getHierarquiaOrganizacionalGestora().getId() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("A Unidade Gestora deve ser informada.");
        }
        if (selecionado.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Contrato deve ser informado.");
        }
        if (selecionado.getHierarquiaOrganizacionalCotista() == null ||
            selecionado.getHierarquiaOrganizacionalCotista().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Unidade Cotista deve ser informada.");
        }
        if (selecionado.isDistribuicao() &&
            (selecionado.getHierarquiaOrganizacionalFilha() == null ||
                selecionado.getHierarquiaOrganizacionalFilha().getId() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("A Unidade Filha deve ser informada.");
        }
        if (selecionado.getItensCotaAbastecimento() == null || selecionado.getItensCotaAbastecimento().size() == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item para a cota foi registrado.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCotaAbastecimento();
            if (isOperacaoNovo()) {
                cotaAbastecimentoFacade.salvarNovo(selecionado);
            } else {
                cotaAbastecimentoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void processaSelecaoContrato() {
        cancelarItemCotaAbastecimento();
        selecionado.setItensCotaAbastecimento(new ArrayList());
    }

    public List<ItemContrato> itensContrato() {
        List<ItemContrato> itens = new ArrayList();
        if (selecionado.isNormal() && selecionado.getContrato() != null) {
            selecionado.setContrato(contratoFacade.recuperarContratoComDependenciasItens(selecionado.getContrato().getId()));
            for (ItemContrato itemContrato : selecionado.getContrato().getItens()) {
                itens.add(itemContratoFacade.recuperar(itemContrato.getId()));
            }
        } else if (selecionado.isDistribuicao() && selecionado.getCotaAbastecimento() != null) {
            for (ItemCotaAbastecimento itemCotaAbastecimento : selecionado.getCotaAbastecimento().getItensCotaAbastecimento()) {
                itens.add(itemContratoFacade.recuperar(itemCotaAbastecimento.getItemContrato().getId()));
            }
        }
        return itens;
    }

    public void processaSelecaoItemContrato() {
        itemCotaAbastecimento.setQuantidadeCota(BigDecimal.ZERO);
        if (itemCotaAbastecimento != null && itemCotaAbastecimento.getItemContrato() != null) {
            itemCotaAbastecimento.setQuantidadeTotal(cotaAbastecimentoFacade.getQuantidadeItemContratoCotaAbastecimento(selecionado, itemCotaAbastecimento));
            itemCotaAbastecimento.setQuantidadeEmOutrasCotas(cotaAbastecimentoFacade.getQuantidadeItemContratoJaUtilizada(selecionado, itemCotaAbastecimento.getItemContrato()));
        }
    }

    private boolean existeItemContratoRegistrado(ItemContrato itemContrato) {
        if (itemContrato != null && itemContrato.getId() != null) {
            if (selecionado.getItensCotaAbastecimento() != null) {
                for (ItemCotaAbastecimento item : selecionado.getItensCotaAbastecimento()) {
                    if (item.getItemContrato().getId().equals(itemContrato.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void validarItemCotaAbastecimento() {
        ValidacaoException ve = new ValidacaoException();
        if (itemCotaAbastecimento.getItemContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Item Contrato deve ser informado.");
        } else if (itemCotaAbastecimento.getCotaAbastecimento() == null && existeItemContratoRegistrado(itemCotaAbastecimento.getItemContrato())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Item Contrato selecionado já foi registrado.");
        }
        if (itemCotaAbastecimento.getQuantidadeCota() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Quantidade deve ser informada.");
        }
        if (itemCotaAbastecimento.getQuantidadeCota() != null) {
            if (itemCotaAbastecimento.getQuantidadeCota().compareTo(BigDecimal.ZERO) <= 0 ||
                itemCotaAbastecimento.getQuantidadeCota().compareTo(itemCotaAbastecimento.getQuantidadeDisponivel()) > 0) {
                ve.adicionarMensagemDeCampoObrigatorio("A Quantidade deve ser um valor maior que zero(0) e menor ou igual a Quantidade Disponível (" + itemCotaAbastecimento.getQuantidadeDisponivel() + ").");
            }
        }
        if (itemCotaAbastecimento.getItemContrato().getControleValor()) {
            if (itemCotaAbastecimento.getValorUnitario() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Item do Contrato possui Tipo de Controle por Valor. Por favor, informe o Valor Unitário.");
            }
        }
        ve.lancarException();
    }

    public void confirmarItemCotaAbastecimento(ItemCotaAbastecimento itemCotaAbastecimento) {
        try {
            validarItemCotaAbastecimento();
            if (selecionado.getItensCotaAbastecimento() == null) {
                selecionado.setItensCotaAbastecimento(new ArrayList());
            }
            itemCotaAbastecimento.setCotaAbastecimento(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensCotaAbastecimento(), itemCotaAbastecimento);
            cancelarItemCotaAbastecimento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarItemCotaAbastecimento() {
        itemCotaAbastecimento = null;
    }

    public void excluirItemCotaAbastecimento(ItemCotaAbastecimento itemCotaAbastecimento) {
        selecionado.getItensCotaAbastecimento().remove(itemCotaAbastecimento);
    }

    public void alterarItemCotaAbastecimento(ItemCotaAbastecimento itemCotaAbastecimento) {
        this.itemCotaAbastecimento = (ItemCotaAbastecimento) Util.clonarObjeto(itemCotaAbastecimento);
    }

    public List<CotaAbastecimento> completaCotasAbastecimento(String parte) {
        return cotaAbastecimentoFacade.buscarCotasParaAbastecimento(parte.trim());
    }

    public List<CotaAbastecimento> completarCotaAbastecimentoNormalPelaUnidadeCotista(String parte) {
        if (selecionado.getUnidadeCotista() == null) {
            return Lists.newArrayList();
        }
        return cotaAbastecimentoFacade.buscarCotaAbastecimentoPorUnidadeCotista(TipoCotaAbastecimento.NORMAL,
            selecionado.getUnidadeCotista(), parte);
    }

    public ConverterAutoComplete getConverterItemCotaAbastecimento() {
        if (converterItemCotaAbastecimento == null) {
            converterItemCotaAbastecimento = new ConverterAutoComplete(ItemCotaAbastecimento.class, itemCotaAbastecimentoFacade);
        }
        return converterItemCotaAbastecimento;
    }

    public List<SelectItem> getTiposCotaAbastecimento() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoCotaAbastecimento.NORMAL, TipoCotaAbastecimento.NORMAL.getDescricao()));
        toReturn.add(new SelectItem(TipoCotaAbastecimento.DISTRIBUICAO, TipoCotaAbastecimento.DISTRIBUICAO.getDescricao()));
        return toReturn;
    }

    public void processarSelecaoTipoCotaAbastecimento() {
        selecionado.setHierarquiaOrganizacionalGestora(null);
        selecionado.setUnidadeGestora(null);
        selecionado.setHierarquiaOrganizacionalCotista(null);
        selecionado.setUnidadeCotista(null);
        selecionado.setContrato(null);
        selecionado.setHierarquiaOrganizacionalFilha(null);
        selecionado.setUnidadeFilha(null);
        selecionado.setItensCotaAbastecimento(new ArrayList());
    }

    public void processarSelecaoCota() {
        if (selecionado.getCotaAbastecimento() == null) {
            selecionado.setContrato(null);
            selecionado.setHierarquiaOrganizacionalGestora(null);
            selecionado.setUnidadeGestora(null);
        }
        selecionado.setContrato(selecionado.getCotaAbastecimento().getContrato());
        selecionado.setHierarquiaOrganizacionalGestora(contratoFacade.buscarHierarquiaVigenteContrato(selecionado.getContrato(), sistemaFacade.getDataOperacao()));
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalFilha(String filtro) {
        if (selecionado.getUnidadeCotista() == null) {
            return Lists.newArrayList();
        }
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalFilhasDe(sistemaFacade.getDataOperacao(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA, selecionado.getUnidadeCotista(), filtro);
    }

    public void processarSelecaoUnidadeCotista() {
        selecionado.setCotaAbastecimento(null);
        selecionado.setHierarquiaOrganizacionalFilha(null);
        selecionado.setUnidadeFilha(null);
    }

    public List<Contrato> completarContrato(String filtro) {
        if (selecionado.getUnidadeGestora() == null) {
            return Lists.newArrayList();
        }
        return contratoFacade.listaFiltrandoContrato(filtro, selecionado.getUnidadeGestora());
    }

    public void processaSelecaoUnidadeGestora() {
        selecionado.setContrato(null);
    }

    public boolean isRegistroEditavel() {
        return isRegistroEditavel;
    }
}
