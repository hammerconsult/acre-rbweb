package br.com.webpublico.controle;

import br.com.webpublico.entidades.AssociacaoGrupoObjetoCompraGrupoMaterial;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AssociacaoGrupoObjetoCompraGrupoMaterialFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created by Buzatto on 06/12/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-associacao-grupo-objeto-compra-grupo-material", pattern = "/associacao-grupo-objeto-compra-grupo-material/novo/", viewId = "/faces/administrativo/materiais/associacaogrupoobjetocompragrupomaterial/edita.xhtml"),
    @URLMapping(id = "editar-associacao-grupo-objeto-compra-grupo-material", pattern = "/associacao-grupo-objeto-compra-grupo-material/editar/#{associacaoGrupoObjetoCompraGrupoControlador.id}/", viewId = "/faces/administrativo/materiais/associacaogrupoobjetocompragrupomaterial/edita.xhtml"),
    @URLMapping(id = "ver-associacao-grupo-objeto-compra-grupo-material", pattern = "/associacao-grupo-objeto-compra-grupo-material/ver/#{associacaoGrupoObjetoCompraGrupoControlador.id}/", viewId = "/faces/administrativo/materiais/associacaogrupoobjetocompragrupomaterial/visualizar.xhtml"),
    @URLMapping(id = "listar-associacao-grupo-objeto-compra-grupo-material", pattern = "/associacao-grupo-objeto-compra-grupo-material/listar/", viewId = "/faces/administrativo/materiais/associacaogrupoobjetocompragrupomaterial/lista.xhtml")
})
public class AssociacaoGrupoObjetoCompraGrupoControlador extends PrettyControlador<AssociacaoGrupoObjetoCompraGrupoMaterial> implements CRUD, Serializable {

    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associacaoGrupoObjetoCompraGrupoMaterialFacade;

    private List<AssociacaoGrupoObjetoCompraGrupoMaterial> associacoes = Lists.newArrayList();

    private List<Conta> contasVinculadasGrupoMaterial;

    public AssociacaoGrupoObjetoCompraGrupoControlador() {
        super(AssociacaoGrupoObjetoCompraGrupoMaterial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/associacao-grupo-objeto-compra-grupo-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return associacaoGrupoObjetoCompraGrupoMaterialFacade;
    }

    @Override
    @URLAction(mappingId = "nova-associacao-grupo-objeto-compra-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-associacao-grupo-objeto-compra-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-associacao-grupo-objeto-compra-grupo-material", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        buscarContasDespesaPorGrupoMaterial();
    }

    @Override
    public void salvar() {
        try {
            validarAssociacaoParaSalvar();
            if (isOperacaoNovo()) {
                for (AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoObjetoCompraGrupoMaterial : associacoes) {
                    associacaoGrupoObjetoCompraGrupoMaterialFacade.salvar(associacaoGrupoObjetoCompraGrupoMaterial);
                }
            } else {
                validarAssociacaoJaCadastrada();
                associacaoGrupoObjetoCompraGrupoMaterialFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<GrupoMaterial> completarGrupoMaterialAnalitico(String parte) {
        return associacaoGrupoObjetoCompraGrupoMaterialFacade.getGrupoMaterialFacade().buscarGrupoMaterialNivelAnalitico(parte.trim());
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPermanenteMovelPorCodigoOrDescricao(String codigoOrDescricao) {
        return associacaoGrupoObjetoCompraGrupoMaterialFacade.getObjetoCompraFacade().getGrupoObjetoCompraFacade().buscarGrupoObjetoCompraConsumoPorCodigoOrDescricao(codigoOrDescricao.trim().toLowerCase());
    }

    public void validarAssociacaoParaSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (isOperacaoNovo()) {
            if (associacoes.isEmpty())
                ve.adicionarMensagemDeCampoObrigatorio("É obrigatório adicionar pelo menos 1(uma) associação de grupos!");
        } else {
            if (selecionado.getFimVigencia() != null &&
                selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de Vigência Final deve ser maior ou igual a Vigência Inicial!");
            }
        }
        ve.lancarException();
    }

    public List<AssociacaoGrupoObjetoCompraGrupoMaterial> getAssociacoes() {
        return associacoes;
    }

    public void setAssociacoes(List<AssociacaoGrupoObjetoCompraGrupoMaterial> associacoes) {
        this.associacoes = associacoes;
    }

    public void adicionarAssociacao() {
        try {
            validarAssociacao();
            if (!associacoes.contains(selecionado)) {
                associacoes.add(selecionado);
                selecionado = new AssociacaoGrupoObjetoCompraGrupoMaterial();
                setContasVinculadasGrupoMaterial(null);
                RequestContext.getCurrentInstance().update("Formulario:elementoGrupoObjetoCompra:codigo-descricao-grupo-objeto");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void removerAssociacao(AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoObjetoCompraGrupoMaterial) {
        if (associacoes.contains(associacaoGrupoObjetoCompraGrupoMaterial)) {
            associacoes.remove(associacaoGrupoObjetoCompraGrupoMaterial);
        }
    }

    public void selecionarAssociacao(AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoObjetoCompraGrupoMaterial) {
        selecionado = associacaoGrupoObjetoCompraGrupoMaterial;
        removerAssociacao(associacaoGrupoObjetoCompraGrupoMaterial);
    }

    private void validarAssociacao() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposObrigatorios(ve);
        validarAssociacaoJaAdicionada(ve);
        validarAssociacaoJaCadastrada();
    }

    private void validarAssociacaoJaAdicionada(ValidacaoException ve) {
        for (AssociacaoGrupoObjetoCompraGrupoMaterial associacao : associacoes) {
            if (associacao.getGrupoObjetoCompra().equals(selecionado.getGrupoObjetoCompra()) && associacao.getGrupoMaterial().equals(selecionado.getGrupoMaterial())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(getDetalheMensagemValidacaoAssociacaoJaExistente("adicionada"));
                ve.lancarException();
            }
        }
    }

    private void validarAssociacaoJaCadastrada() {
        ValidacaoException ve = new ValidacaoException();
        AssociacaoGrupoObjetoCompraGrupoMaterial associacao = associacaoGrupoObjetoCompraGrupoMaterialFacade.buscarAssociacaoPorGrupoObjetoCompraVigente(selecionado);
        if (associacao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(getDetalheMensagemValidacaoAssociacaoJaExistente("cadastrada") + " </br><b><a href='" + FacesUtil.getRequestContextPath() + "/associacao-grupo-objeto-compra-grupo-material/ver/" + associacao.getId() + "' target='_blank'>Clique aqui para visualizar</a></b>");
        }
        ve.lancarException();
    }

    private String getDetalheMensagemValidacaoAssociacaoJaExistente(String parte) {
        return "Já existe associação <b>" + parte.toUpperCase() + "</b> com o grupo de objeto de compra: <b>" + selecionado.getGrupoObjetoCompra() + ".</b>";
    }

    private void validarCamposObrigatorios(ValidacaoException ve) {
        Util.validarCamposObrigatorios(selecionado, ve);
        ve.lancarException();
    }

    public void encerrarVigencia() {
        try {
            selecionado.setFinalVigencia(associacaoGrupoObjetoCompraGrupoMaterialFacade.getSistemaFacade().getDataOperacao());
            validarAssociacaoParaSalvar();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public List<Conta> getContasVinculadasGrupoMaterial() {
        return contasVinculadasGrupoMaterial;
    }

    public void setContasVinculadasGrupoMaterial(List<Conta> contasVinculadasGrupoMaterial) {
        this.contasVinculadasGrupoMaterial = contasVinculadasGrupoMaterial;
    }

    public void buscarContasDespesaPorGrupoMaterial() {
        setContasVinculadasGrupoMaterial(associacaoGrupoObjetoCompraGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(selecionado.getGrupoMaterial()));
    }

}
