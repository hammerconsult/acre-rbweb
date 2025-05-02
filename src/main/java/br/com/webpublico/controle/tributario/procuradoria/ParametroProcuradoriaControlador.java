package br.com.webpublico.controle.tributario.procuradoria;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.procuradoria.DocumentoProcuradoria;
import br.com.webpublico.entidades.tributario.procuradoria.LinksUteis;
import br.com.webpublico.entidades.tributario.procuradoria.ParametroProcuradoria;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.procuradoria.ProcuradoriaParametroFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "parametro-procuradoria-novo", pattern = "/parametro-procuradoria/novo/", viewId = "/faces/tributario/dividaativa/parametroprocuradoria/edita.xhtml"),
        @URLMapping(id = "parametro-procuradoria-edita", pattern = "/parametro-procuradoria/editar/#{parametroProcuradoriaControlador.id}/", viewId = "/faces/tributario/dividaativa/parametroprocuradoria/edita.xhtml"),
        @URLMapping(id = "parametro-procuradoria-ver", pattern = "/parametro-procuradoria/ver/#{parametroProcuradoriaControlador.id}/", viewId = "/faces/tributario/dividaativa/parametroprocuradoria/visualizar.xhtml"),
        @URLMapping(id = "parametro-procuradoria-listar", pattern = "/parametro-procuradoria/listar/", viewId = "/faces/tributario/dividaativa/parametroprocuradoria/lista.xhtml")
})
public class ParametroProcuradoriaControlador extends PrettyControlador<ParametroProcuradoria> implements Serializable, CRUD {

    @EJB
    private ProcuradoriaParametroFacade procuradoriaParametroFacade;
    private DocumentoProcuradoria documentoProcuradoria;
    private LinksUteis linksUteis;

    public ParametroProcuradoriaControlador() {
        super(ParametroProcuradoria.class);
        documentoProcuradoria = new DocumentoProcuradoria();
        linksUteis = new LinksUteis();
    }

    public DocumentoProcuradoria getDocumentoProcuradoria() {
        return documentoProcuradoria;
    }

    public void setDocumentoProcuradoria(DocumentoProcuradoria documentoProcuradoria) {
        this.documentoProcuradoria = documentoProcuradoria;
    }

    public LinksUteis getLinksUteis() {
        return linksUteis;
    }

    public void setLinksUteis(LinksUteis linksUteis) {
        this.linksUteis = linksUteis;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-procuradoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return procuradoriaParametroFacade;
    }

    @Override
    @URLAction(mappingId = "parametro-procuradoria-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "parametro-procuradoria-edita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();

    }

    @Override
    @URLAction(mappingId = "parametro-procuradoria-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public void adicionaDocumento() {
        if (podeAdicionarDocumento()) {
            documentoProcuradoria.setParametroProcuradoria(selecionado);
            selecionado.getDocumentosNecessarios().add(documentoProcuradoria);
            documentoProcuradoria = new DocumentoProcuradoria();
        }
    }

    public boolean podeAdicionarDocumento() {
        boolean retorno = true;
        if (documentoProcuradoria.getDescricao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo descrição é obrigatório");
            retorno = false;
        }
        return retorno;
    }

    public void adicionarLink() {
        if (podeAdicionarLink()) {
            linksUteis.setParametroProcuradoria(selecionado);
            selecionado.getLinks().add(linksUteis);
            linksUteis = new LinksUteis();
        }
    }

    public boolean podeAdicionarLink() {
        boolean retorno = true;
        if (linksUteis.getDescricao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo descrição é obrigatório");
            retorno = false;
        }
        if (linksUteis.getLink().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo link é obrigatório");
            retorno = false;
        }
        return retorno;
    }

    public void removerDocumento(DocumentoProcuradoria doc) {
        selecionado.getDocumentosNecessarios().remove(doc);
    }

    public void editarDocumento(DocumentoProcuradoria doc) {
        documentoProcuradoria = doc;
        selecionado.getDocumentosNecessarios().remove(doc);
    }


    public void removerLink(LinksUteis links) {
        selecionado.getLinks().remove(links);
    }

    public void editarLink(LinksUteis links) {
        linksUteis = links;
        selecionado.getLinks().remove(links);
    }

    @Override
    public void salvar() {
        if (validaVigencia()) {
            super.salvar();
        }
    }

    public boolean validaVigencia() {
        if (selecionado.getDataInicial() == null || selecionado.getDataFinal() == null) {
            FacesUtil.addOperacaoNaoPermitida("A Data Inicial e a Data Final são obrigatórios");
            return false;
        }

        if (selecionado.getDataInicial() != null || selecionado.getDataFinal() != null) {
            if (DataUtil.verificaDataFinalMaiorQueDataInicial(selecionado.getDataInicial(), selecionado.getDataFinal())) {
                FacesUtil.addOperacaoNaoPermitida("A Data Inicial não pode ser maior que a Data Final");
                return false;
            }
        }

        if (selecionado.getDataFinal() != null && selecionado.getDataInicial() != null) {
            List<ParametroProcuradoria> lista = procuradoriaParametroFacade.recuperarTodosParametros(selecionado);
            if (lista != null) {
                for (ParametroProcuradoria p : lista) {
                    if (((!selecionado.getDataInicial().before(p.getDataInicial()) || !selecionado.getDataFinal().before(p.getDataInicial()))) &&
                            (!selecionado.getDataInicial().after(p.getDataFinal()))) {
                        FacesUtil.addOperacaoNaoPermitida("Já existe um parâmetro cadastro nesse intervalo de vigência");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
