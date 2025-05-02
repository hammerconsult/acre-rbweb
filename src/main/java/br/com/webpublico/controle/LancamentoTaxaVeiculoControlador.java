package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ItemLancamentoTaxaVeiculo;
import br.com.webpublico.entidades.LancamentoTaxaVeiculo;
import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LancamentoTaxaVeiculoFacade;
import br.com.webpublico.negocios.TaxaVeiculoFacade;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "lancamentoTaxaVeiculoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lancamentoTaxaVeiculoNovo", pattern = "/lancamento-taxa-veiculo/novo/", viewId = "/faces/administrativo/frota/lancamentotaxaveiculo/edita.xhtml"),
    @URLMapping(id = "lancamentoTaxaVeiculoListar", pattern = "/lancamento-taxa-veiculo/listar/", viewId = "/faces/administrativo/frota/lancamentotaxaveiculo/lista.xhtml"),
    @URLMapping(id = "lancamentoTaxaVeiculoEditar", pattern = "/lancamento-taxa-veiculo/editar/#{lancamentoTaxaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/lancamentotaxaveiculo/edita.xhtml"),
    @URLMapping(id = "lancamentoTaxaVeiculoVer", pattern = "/lancamento-taxa-veiculo/ver/#{lancamentoTaxaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/lancamentotaxaveiculo/visualizar.xhtml"),
})
public class LancamentoTaxaVeiculoControlador extends PrettyControlador<LancamentoTaxaVeiculo> implements Serializable, CRUD {

    @EJB
    private LancamentoTaxaVeiculoFacade lancamentoTaxaVeiculoFacade;
    private ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculo;
    private Boolean taxasObrigatorias;
    private ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculoAlteracao;
    @EJB
    private TaxaVeiculoFacade taxaVeiculoFacade;

    public LancamentoTaxaVeiculoControlador() {
        super(LancamentoTaxaVeiculo.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-taxa-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoTaxaVeiculoFacade;
    }

    @URLAction(mappingId = "lancamentoTaxaVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "lancamentoTaxaVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "lancamentoTaxaVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosDaTela();
    }

    public void novoVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/frota/veiculo/novo/", selecionado);
    }

    public void novaTaxaVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/taxa-veiculo/novo/", selecionado);
    }

    private void inicializarAtributosDaTela() {
        inicializarItemLancamentoTaxaVeiculo();
    }

    private void inicializarItemLancamentoTaxaVeiculo() {
        itemLancamentoTaxaVeiculo = new ItemLancamentoTaxaVeiculo();
        taxasObrigatorias = Boolean.TRUE;
    }

    public ItemLancamentoTaxaVeiculo getItemLancamentoTaxaVeiculo() {
        return itemLancamentoTaxaVeiculo;
    }

    public void setItemLancamentoTaxaVeiculo(ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculo) {
        this.itemLancamentoTaxaVeiculo = itemLancamentoTaxaVeiculo;
    }

    public Boolean getTaxasObrigatorias() {
        return taxasObrigatorias;
    }

    public void setTaxasObrigatorias(Boolean taxasObrigatorias) {
        this.taxasObrigatorias = taxasObrigatorias;
    }

    private boolean existeItemLancamentoTaxaVeiculoAdicionado(List<ItemLancamentoTaxaVeiculo> itensLancamentoTaxaVeiculo, ItemLancamentoTaxaVeiculo item) {
        if (itensLancamentoTaxaVeiculo != null) {
            for (ItemLancamentoTaxaVeiculo iltv : itensLancamentoTaxaVeiculo) {
                if (iltv.getTaxaVeiculo().getId().equals(item.getTaxaVeiculo().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validaAdicaoItemLancamentoTaxaVeiculo(ItemLancamentoTaxaVeiculo item) {
        boolean retorno = true;
        retorno = Util.validaCampos(item);
        if (retorno) {
            if (item.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("O valor deve ser maior que 0(zero).");
            }
            if (existeItemLancamentoTaxaVeiculoAdicionado(selecionado.getItensLancamentoTaxaVeiculo(), itemLancamentoTaxaVeiculo)) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("A taxa selecionada já foi adicionada.");
            }
        }
        return retorno;
    }

    public void adicionarItemLancamentoTaxaVeiculo(ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculo) {
        if (!validaAdicaoItemLancamentoTaxaVeiculo(itemLancamentoTaxaVeiculo)) {
            return;
        }
        itemLancamentoTaxaVeiculo.setLancamentoTaxaVeiculo(selecionado);
        if (selecionado.getItensLancamentoTaxaVeiculo() == null) {
            selecionado.setItensLancamentoTaxaVeiculo(new ArrayList());
        }
        selecionado.getItensLancamentoTaxaVeiculo().add(itemLancamentoTaxaVeiculo);
        itemLancamentoTaxaVeiculoAlteracao = null;
        inicializarItemLancamentoTaxaVeiculo();
    }

    public void cancelarAdicaoItemLancamentoVeiculo() {
        if (itemLancamentoTaxaVeiculoAlteracao != null) {
            adicionarItemLancamentoTaxaVeiculo(itemLancamentoTaxaVeiculoAlteracao);
        }
        inicializarItemLancamentoTaxaVeiculo();
    }

    public void excluirItemLancamentoTaxaVeiculo(ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculo) {
        selecionado.getItensLancamentoTaxaVeiculo().remove(itemLancamentoTaxaVeiculo);
    }

    public void alterarItemLancamentoTaxaVeiculo(ItemLancamentoTaxaVeiculo item) {
        itemLancamentoTaxaVeiculo = ItemLancamentoTaxaVeiculo.clonar(item);
        itemLancamentoTaxaVeiculoAlteracao = ItemLancamentoTaxaVeiculo.clonar(item);
        selecionado.getItensLancamentoTaxaVeiculo().remove(item);
    }

    public List<TaxaVeiculo> completaTaxaVeiculo(String parte) {
        return taxaVeiculoFacade.listarTaxaVeiculo(parte, taxasObrigatorias, idTaxasRegistradas());
    }

    public void processarAlteracaoTaxasObrigatorias() {
        if (itemLancamentoTaxaVeiculo != null) {
            itemLancamentoTaxaVeiculo.setTaxaVeiculo(null);
        }
    }

    private List<Long> idTaxasRegistradas() {
        List<Long> ids = Lists.newArrayList();
        if (selecionado.getItensLancamentoTaxaVeiculo() != null) {
            for (ItemLancamentoTaxaVeiculo item : selecionado.getItensLancamentoTaxaVeiculo()) {
                ids.add(item.getTaxaVeiculo().getId());
            }
        }
        if (selecionado.getVeiculo() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = lancamentoTaxaVeiculoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(lancamentoTaxaVeiculoFacade.getSistemaFacade().getDataOperacao(),
                selecionado.getVeiculo().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            List<TaxaVeiculo> taxasConfiguradas = lancamentoTaxaVeiculoFacade.getParametroFrotasFacade().buscarTaxasPorHierarquia(hierarquiaOrganizacional, lancamentoTaxaVeiculoFacade.getSistemaFacade().getDataOperacao());
            for (TaxaVeiculo taxaVeiculo : taxasConfiguradas) {
                ids.add(taxaVeiculo.getId());
            }
        }
        return ids;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (selecionado.getItensLancamentoTaxaVeiculo() == null || selecionado.getItensLancamentoTaxaVeiculo().size() <= 0) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("Nenhuma taxa foi registrada.");
        } else {
            if (taxaVeiculoFacade.hasTaxaObrigatoriaNaoSelecionada(idTaxasRegistradas(), lancamentoTaxaVeiculoFacade.getSistemaFacade().getDataOperacao())) {
                validou = false;
                FacesUtil.addOperacaoNaoPermitida("Existem taxas obrigatórias não registradas.");
            }
        }
        if (selecionado.getAno() != null && selecionado.getVeiculo() != null &&
            lancamentoTaxaVeiculoFacade.existeLancamentoTaxaVeiculo(selecionado)) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("Já existe um lançamento para este veículo e ano.");
        }
        return validou;
    }
}
