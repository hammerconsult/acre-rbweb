/*
 * Codigo gerado automaticamente em Tue May 24 13:55:42 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoInventario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.InventarioEstoqueFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "inventarioEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoInventarioEstoque", pattern = "/inventario-estoque/novo/", viewId = "/faces/administrativo/materiais/inventarioestoque/edita.xhtml"),
    @URLMapping(id = "editarInventarioEstoque", pattern = "/inventario-estoque/editar/#{inventarioEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/inventarioestoque/edita.xhtml"),
    @URLMapping(id = "listarInventarioEstoque", pattern = "/inventario-estoque/listar/", viewId = "/faces/administrativo/materiais/inventarioestoque/lista.xhtml"),
    @URLMapping(id = "verInventarioEstoque", pattern = "/inventario-estoque/ver/#{inventarioEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/inventarioestoque/visualizar.xhtml"),
    @URLMapping(id = "inventariar", pattern = "/inventario-estoque/inventariar/#{inventarioEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/inventarioestoque/inventariar.xhtml")
})
public class InventarioEstoqueControlador extends PrettyControlador<InventarioEstoque> implements Serializable, CRUD {

    @EJB
    private InventarioEstoqueFacade facade;
    private Estoque estoque;
    private ItemInventarioEstoque itemInventarioEstoqueSelecionado;
    private Material material;
    private ItemInventarioEstoqueControlador itemInventarioEstoqueControlador;
    private ConsolidadorDeEstoque consolidadorDeEstoque;

    public InventarioEstoqueControlador() {
        metadata = new EntidadeMetaData(InventarioEstoque.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/inventario-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoInventarioEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        criarParametrosIniciaisNovo();
    }

    @Override
    @URLAction(mappingId = "editarInventarioEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarListasDependentes();
        itemInventarioEstoqueSelecionado = null;
    }

    @Override
    @URLAction(mappingId = "verInventarioEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "inventariar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void inventariar() {
        editar();
        itemInventarioEstoqueControlador = (ItemInventarioEstoqueControlador) Util.getControladorPeloNome("itemInventarioEstoqueControlador");
        itemInventarioEstoqueControlador.inventariar(selecionado);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validarRegrasGerais();
            selecionado = facade.salvarInventario(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Inventário salvo com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao tentar salvar o inventário. Por favor, verifique as informações digitadas e tente novamente.");
        }
    }

    public void concluirInventario() {
        try {
            validarRegrasGerais();
            selecionado = facade.concluirInventario(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada("Inventário concluído com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao tentar concluir o inventário. Por favor, verifique as informações digitadas e tente novamente.");
        }
    }

    public void redirecionarParaInventariar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "inventariar/" + selecionado.getId() + "/");
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<Material> completarMaterial(String parte) {
        return facade.getMaterialFacade().completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(
            selecionado.getLocalEstoque(), null, parte, StatusMaterial.getSituacoesDeferidoInativo());
    }

    public List<SelectItem> getTiposInventario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoInventario object : TipoInventario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private void criarParametrosIniciaisNovo() {
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setIniciadoEm(new Date());
        if (selecionado.getTipoInventario() == null) {
            selecionado.setTipoInventario(TipoInventario.GERAL);
        }
        itemInventarioEstoqueSelecionado = null;
    }

    public void novoItemInventarioEstoque() {
        try {
            selecionado.realizarValidacoes();
            this.itemInventarioEstoqueSelecionado = new ItemInventarioEstoque();
            this.itemInventarioEstoqueSelecionado.setInventarioEstoque(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelaritemInventarioEstoque() {
        this.itemInventarioEstoqueSelecionado = null;
    }

    public void alterouTipoInventario(ValueChangeEvent evento) {
        RequestContext.getCurrentInstance().execute("dialogAlteracaoTipoInventario.show()");
    }

    public void adicionarMaterial() {
        try {
            validarAoAdicionarItemInventario();
            vincularItemInventarioEstoqueSelecionadoAoLoteItemInventario(itemInventarioEstoqueSelecionado);
            itemInventarioEstoqueSelecionado.setQuantidadeEsperada(consolidadorDeEstoque.getFisicoConsolidado(itemInventarioEstoqueSelecionado.getMaterial()));
            itemInventarioEstoqueSelecionado.setFinanceiro(consolidadorDeEstoque.getFinanceiro(itemInventarioEstoqueSelecionado.getMaterial()));
            itemInventarioEstoqueSelecionado.setEncontrado(Boolean.FALSE);
            itemInventarioEstoqueSelecionado.setAjustado(Boolean.FALSE);
            itemInventarioEstoqueSelecionado.setDivergente(Boolean.FALSE);
            selecionado.setItensInventarioEstoque(Util.adicionarObjetoEmLista(selecionado.getItensInventarioEstoque(), itemInventarioEstoqueSelecionado));
            cancelaritemInventarioEstoque();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAoAdicionarItemInventario() {
        ValidacaoException ve = new ValidacaoException();
        if (itemInventarioEstoqueSelecionado.getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Material deve ser informado.");
        }
        ve.lancarException();
        selecionado.validarMesmoObjetoEmLista(itemInventarioEstoqueSelecionado);
    }


    private void validarRegrasGerais() {
        selecionado.realizarValidacoes();

        ValidacaoException ve = new ValidacaoException();
        List<InventarioEstoque> inventariosAbertos = facade.recuperarInventariosAbertosParaLocal(selecionado);
        if (inventariosAbertos != null && !inventariosAbertos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um inventário aberto no período informado, não será possível prosseguir com este inventário.");
            InventarioEstoque ie = facade.recuperarInventariosAbertosParaLocal(selecionado).get(0);
            String complemento = ie.getEncerradoEm() != null ? " será encerrado em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ie.getEncerradoEm()) : " ainda não encerrado.";
            ve.adicionarMensagemDeOperacaoNaoPermitida("Inventário Aberto!  Iniciado em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ie.getIniciadoEm()) + " e " + complemento);
            selecionado.setEncerradoEm(null);
        }
        if (selecionado.getEncerradoEm() != null && selecionado.getIniciadoEm() != null && selecionado.getEncerradoEm().before(selecionado.getIniciadoEm())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de encerramento não pode ser anterior a data de iniciação do inventário.");
            selecionado.setEncerradoEm(null);
        }

        if (selecionado.getEncerradoEm() != null && Util.getDataHoraMinutoSegundoZerado(selecionado.getEncerradoEm()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de encerramento deve ser posterior a data de hoje.");
            selecionado.setEncerradoEm(null);
        }
        ve.lancarException();
    }

    public void removerMaterialListaInvetariados(ItemInventarioEstoque itemInventarioEstoque) {
        selecionado.getItensInventarioEstoque().remove(itemInventarioEstoque);
    }

    public boolean isInventarioConcluido(Long idInventarioEstoque) {
        return facade.isInventarioConcluido(idInventarioEstoque);
    }

    public boolean isInventarioAberto(InventarioEstoque inventarioEstoque) {
        return inventarioEstoque.isInventarioAberto();
    }

    public void recuperarMateriaisEmEstoque() {
        try {
            selecionado.realizarValidacoes();
            selecionado.setItensInventarioEstoque(null);
            List<Material> listaDeMateriais = facade.getMaterialFacade().completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(
                selecionado.getLocalEstoque(), null, "", StatusMaterial.getSituacoesDeferidoInativo());

            Set<Material> matSet = new HashSet<>();
            matSet.addAll(listaDeMateriais);

            consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorSemReservaEstoque(selecionado.getLocalEstoque(), matSet, selecionado.getIniciadoEm());

            for (Material materialDaVez : listaDeMateriais) {
                ItemInventarioEstoque item = new ItemInventarioEstoque();
                item.setQuantidadeEsperada(consolidadorDeEstoque.getFisicoConsolidado(materialDaVez));
                item.setFinanceiro(consolidadorDeEstoque.getFinanceiro(materialDaVez));
                item.setMaterial(materialDaVez);
                item.setInventarioEstoque(selecionado);
                item.setEncontrado(Boolean.FALSE);
                item.setAjustado(Boolean.FALSE);
                item.setDivergente(Boolean.FALSE);
                selecionado.setItensInventarioEstoque(Util.adicionarObjetoEmLista(selecionado.getItensInventarioEstoque(), item));
                vincularItemInventarioEstoqueSelecionadoAoLoteItemInventario(item);
            }
            listaDeMateriais.clear();
            matSet.clear();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void confirmarAlteracaoTipoInventario() {
        selecionado.setItensInventarioEstoque(null);
        if (selecionado.isInventarioGeral()) {
            cancelaritemInventarioEstoque();
        }
        if (selecionado.getLocalEstoque() != null) {
            LocalEstoque localEstoque = facade.getLocalEstoqueFacade().recuperar(selecionado.getLocalEstoque().getId());
            List<Material> materials = facade.getMaterialFacade().completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(
                localEstoque, null, "", StatusMaterial.getSituacoesDeferidoInativo());
            Set<Material> setMateriais = new HashSet<>();
            setMateriais.addAll(materials);
            consolidadorDeEstoque = facade.getLocalEstoqueFacade().getNovoConsolidadorSemReservaEstoque(localEstoque, setMateriais, facade.getSistemaFacade().getDataOperacao());
        }
    }

    public void cancelarAlteracaoTipoInventario() {
        if (selecionado.isInventarioGeral()) {
            selecionado.setTipoInventario(TipoInventario.AMOSTRAGEM);
            return;
        }
        if (selecionado.isInventarioAmostragem()) {
            selecionado.setTipoInventario(TipoInventario.GERAL);
        }
    }

    private void validarVinculoLoteMaterialComItemMaterial(ItemInventarioEstoque itemInventarioLocal) {
        ValidacaoException ve = new ValidacaoException();
        if (itemInventarioLocal == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Item Inventário Local deve ser informado.");

        } else if (itemInventarioLocal.getInventarioEstoque().getLocalEstoque() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Local de Estoque deve ser informado.");

        } else if (itemInventarioLocal.getMaterial() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Material deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarEstoqueLoteMaterial(List<EstoqueLoteMaterial> estoquesLote, Material material) {
        ValidacaoException ve = new ValidacaoException();
        if (estoquesLote == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Estoque lote material não encontrado para o material: " + material + ".");
        }
        ve.lancarException();
    }

    private void vincularItemInventarioEstoqueSelecionadoAoLoteItemInventario(ItemInventarioEstoque itemInventarioEstoque) {
        try {
            validarVinculoLoteMaterialComItemMaterial(itemInventarioEstoque);
            List<EstoqueLoteMaterial> estoquesLote = consolidadorDeEstoque.getLotes(itemInventarioEstoque.getMaterial());
            validarEstoqueLoteMaterial(estoquesLote, itemInventarioEstoque.getMaterial());
            for (EstoqueLoteMaterial estoqueLoteMaterial : estoquesLote) {
                LoteItemInventario loteItemParaAdd = new LoteItemInventario();
                loteItemParaAdd.setItemInventarioEstoque(itemInventarioEstoque);
                loteItemParaAdd.setLoteMaterial(estoqueLoteMaterial.getLoteMaterial());
                loteItemParaAdd.setQuantidadeEsperada(estoqueLoteMaterial.getQuantidade());
                loteItemParaAdd.setQuantidadeConstatada(null);
                itemInventarioEstoque.setLotesItemInventario(Util.adicionarObjetoEmLista(itemInventarioEstoque.getLotesItemInventario(), loteItemParaAdd));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void carregarListasDependentes() {
        selecionado.setItensInventarioEstoque(facade.recuperarItensInventario(selecionado));
        for (ItemInventarioEstoque itemInventarioEstoque : selecionado.getItensInventarioEstoque()) {
            itemInventarioEstoque.setLotesItemInventario(facade.recuperarLotesItemInventario(itemInventarioEstoque));
        }
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public ItemInventarioEstoque getItemInventarioEstoqueSelecionado() {
        return itemInventarioEstoqueSelecionado;
    }

    public void setItemInventarioEstoqueSelecionado(ItemInventarioEstoque itemInventarioEstoqueSelecionado) {
        this.itemInventarioEstoqueSelecionado = itemInventarioEstoqueSelecionado;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
