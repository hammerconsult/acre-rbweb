/*
 * Codigo gerado automaticamente em Thu Dec 29 13:36:59 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoHabilitacao;
import br.com.webpublico.entidades.TipoDoctoHabilitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DoctoHabilitacaoFacade;
import br.com.webpublico.negocios.TipoDoctoHabilitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "doctoHabilitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-documento-habilitacao", pattern = "/documento-habilitacao/novo/", viewId = "/faces/administrativo/licitacao/doctohabilitacao/edita.xhtml"),
    @URLMapping(id = "editar-documento-habilitacao", pattern = "/documento-habilitacao/editar/#{doctoHabilitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/doctohabilitacao/edita.xhtml"),
    @URLMapping(id = "ver-documento-habilitacao", pattern = "/documento-habilitacao/ver/#{doctoHabilitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/doctohabilitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-documento-habilitacao", pattern = "/documento-habilitacao/listar/", viewId = "/faces/administrativo/licitacao/doctohabilitacao/lista.xhtml")
})
public class DoctoHabilitacaoControlador extends PrettyControlador<DoctoHabilitacao> implements Serializable, CRUD {

    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private TipoDoctoHabilitacaoFacade tipoDoctoHabilitacaoFacade;
    private String validadeAux;
    private String prazoAux;

    public DoctoHabilitacaoControlador() {
        super(DoctoHabilitacao.class);
    }

    public String getPrazoAux() {
        return prazoAux;
    }

    public void setPrazoAux(String prazoAux) {
        this.prazoAux = prazoAux;
    }

    public String getValidadeAux() {
        return validadeAux;
    }

    public void setValidadeAux(String validadeAux) {
        this.validadeAux = validadeAux;
    }

    public DoctoHabilitacaoFacade getFacade() {
        return doctoHabilitacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return doctoHabilitacaoFacade;
    }

    public List<TipoDoctoHabilitacao> completarTiposDoctosHabilitacao(String parte) {
        return tipoDoctoHabilitacaoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void excluir() {
        try {
            validarExclusao();
            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoDoctoHabilitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo do Documento deve ser informado.");
        }
        if (!doctoHabilitacaoFacade.validarDescricaoUnica(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(selecionado.getDescricao() + " Já existe um documento com este nome!");
        }
        if (selecionado.getInicioVigencia() != null && selecionado.getFimVigencia() != null && selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Fim de Vigência deve ser superior ao Inicío de Vigência.");
        }
        ve.lancarException();
    }

    private void validarExclusao() {
        ValidacaoException ve = new ValidacaoException();
        if (!doctoHabilitacaoFacade.validarExclusao(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(selecionado.getDescricao() + " está associado com alguma(s) Licitação(ões) !");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novo-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        validadeAux = new String();
        prazoAux = new String();
    }

    @URLAction(mappingId = "ver-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento-habilitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
