/*
 * Codigo gerado automaticamente em Wed May 18 14:27:33 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.Perecibilidade;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoMarca;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "materialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMaterial", pattern = "/material/novo/", viewId = "/faces/administrativo/materiais/materiais/edita.xhtml"),
    @URLMapping(id = "editarMaterial", pattern = "/material/editar/#{materialControlador.id}/", viewId = "/faces/administrativo/materiais/materiais/edita.xhtml"),
    @URLMapping(id = "listarMaterial", pattern = "/material/listar/", viewId = "/faces/administrativo/materiais/materiais/lista.xhtml"),
    @URLMapping(id = "verMaterial", pattern = "/material/ver/#{materialControlador.id}/", viewId = "/faces/administrativo/materiais/materiais/visualizar.xhtml")
})
public class MaterialControlador extends PrettyControlador<Material> implements Serializable, CRUD {

    @EJB
    private MaterialFacade facade;
    private Boolean materialVinculadoAoEstoque;
    private ConverterAutoComplete converterDerivacaoComponente;
    private List<LocalEstoqueMaterial> locaisEstoque;
    private List<DerivacaoObjetoCompraComponente> derivacoesComponente;

    public MaterialControlador() {
        metadata = new EntidadeMetaData(Material.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataRegistro(new Date());
        selecionado.setStatusMaterial(StatusMaterial.AGUARDANDO);
        Marca marca = (Marca) Web.pegaDaSessao(Marca.class);
        if (marca != null) {
            selecionado.setMarca(marca);
        }
        materialVinculadoAoEstoque = false;
    }

    @Override
    @URLAction(mappingId = "editarMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        setSessao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao"));
        if (isSessao()) {
            selecionado = (Material) Web.pegaDaSessao(metadata.getEntidade());
        } else {
            super.editar();
        }

        Marca marca = (Marca) Web.pegaDaSessao(Marca.class);
        if (marca != null) {
            selecionado.setMarca(marca);
        }
        verificarSeMaterialPodeSerEditado();
        Collections.sort(selecionado.getEfetivacoes());
        buscarLocaisEstoqueMaterial();
        if (selecionado.getObjetoCompra().isObjetoDerivacao()) {
            derivacoesComponente = facade.getDerivacaoObjetoCompraComponenteFacade().buscarComponentesPorDerivacao(selecionado.getObjetoCompra().getDerivacaoObjetoCompra());
        }
    }

    @Override
    @URLAction(mappingId = "verMaterial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getEfetivacoes());
        buscarLocaisEstoqueMaterial();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public List<Modelo> completarModelo(String parte) {
        if (selecionado.getMarca() != null) {
            return facade.getModeloFacade().listaPorMarca(parte.trim(), selecionado.getMarca());
        } else {
            FacesUtil.addCampoObrigatorio("O campo Marca deve ser informado.");
            return new ArrayList<>();
        }
    }

    public void setaMarca(SelectEvent evt) {
        selecionado.setMarca(((Marca) evt.getObject()));
    }

    @Override
    public void excluir() {
        try {
            facade.remover(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public ConverterAutoComplete getConverterDerivacaoComponente() {
        if (converterDerivacaoComponente == null) {
            converterDerivacaoComponente = new ConverterAutoComplete(DerivacaoObjetoCompraComponente.class, facade.getDerivacaoObjetoCompraComponenteFacade());
        }
        return converterDerivacaoComponente;
    }

    public List<Material> completarMaterialDeferidoPorDescricaoECodigo(String parte) {
        return facade.buscarMateriaisPorCodigoOuDescricao(parte.trim(), Lists.newArrayList(StatusMaterial.DEFERIDO));
    }

    public List<Material> completarMaterialDeferidoInativoPorDescricaoECodigo(String parte) {
        return facade.buscarMateriaisPorCodigoOuDescricao(parte.trim(), StatusMaterial.getSituacoesDeferidoInativo());
    }

    public void buscarLocaisEstoqueMaterial() {
        locaisEstoque = facade.getLocalEstoqueFacade().buscarLocaisEstoqueMaterial(selecionado);
    }

    public void navegarParaModelo() {
        selecionado.setModelo(null);
        if (selecionado.getMarca() != null) {
            Web.navegacao(getUrlAtual(), "/modelo/novo/", selecionado, selecionado.getMarca());
        } else {
            FacesUtil.addOperacaoNaoPermitida("O campo Marca deve ser informado.");
        }
    }

    public void navegarParaMarca() {
        selecionado.setMarca(null);
        selecionado.setModelo(null);
        Web.navegacao(getUrlAtual(), "/marca/novo/", selecionado, TipoMarca.MARCA_MATERIAL_CONSUMO);
    }

    public List<SelectItem> getPerecibilidades() {
        return Util.getListSelectItemSemCampoVazio(Perecibilidade.values());
    }

    public List<SelectItem> getDerivacoesComponente() {
        if (!Util.isListNullOrEmpty(derivacoesComponente)) {
            return Util.getListSelectItem(derivacoesComponente);
        }
        return Lists.newArrayList();
    }

    public void verificarSeMaterialPodeSerEditado() {
        materialVinculadoAoEstoque = selecionado.getId() != null && facade.verificarSeMaterialTemEntradaNoEstoque(selecionado);
    }

    public void atribuirGrupoMaterial() {
        if (selecionado.getObjetoCompra() != null) {
            try {
                AssociacaoGrupoObjetoCompraGrupoMaterial associacao = facade.getAssociacaoFacade().buscarAssociacaoPorGrupoObjetoCompraVigente(
                    selecionado.getObjetoCompra().getGrupoObjetoCompra(),
                    facade.getSistemaFacade().getDataOperacao());
                selecionado.setGrupo(associacao.getGrupoMaterial());

                if (selecionado.getObjetoCompra().isObjetoDerivacao()) {
                    derivacoesComponente = facade.getDerivacaoObjetoCompraComponenteFacade().buscarComponentesPorDerivacao(selecionado.getObjetoCompra().getDerivacaoObjetoCompra());
                }
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
            }
        }
    }

    public Boolean getMaterialVinculadoAoEstoque() {
        return materialVinculadoAoEstoque;
    }

    public void setMaterialVinculadoAoEstoque(Boolean materialVinculadoAoEstoque) {
        this.materialVinculadoAoEstoque = materialVinculadoAoEstoque;
    }

    public List<LocalEstoqueMaterial> getLocaisEstoque() {
        return locaisEstoque;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    public void inativar() {
        this.selecionado.setStatusMaterial(StatusMaterial.INATIVO);
        salvar();
    }

    public void reativar() {
        this.selecionado.setStatusMaterial(StatusMaterial.DEFERIDO);
        salvar();
    }
}
