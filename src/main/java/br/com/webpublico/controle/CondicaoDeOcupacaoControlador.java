package br.com.webpublico.controle;

import br.com.webpublico.entidades.CondicaoDeOcupacao;
import br.com.webpublico.entidades.ParametroPatrimonio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CondicaoDeOcupacaoFacade;
import br.com.webpublico.negocios.LoteMaterialFacade;
import br.com.webpublico.util.EntidadeMetaData;
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
 * User: MGA
 * Date: 02/09/14
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "condicaoDeOcupacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaCondicaoDeOcupacao", pattern = "/condicao-de-ocupacao/novo/", viewId = "/faces/administrativo/patrimonio/condicaodeocupacao/edita.xhtml"),
        @URLMapping(id = "editarCondicaoDeOcupacao", pattern = "/condicao-de-ocupacao/editar/#{condicaoDeOcupacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/condicaodeocupacao/edita.xhtml"),
        @URLMapping(id = "listarCondicaoDeOcupacao", pattern = "/condicao-de-ocupacao/listar/", viewId = "/faces/administrativo/patrimonio/condicaodeocupacao/lista.xhtml"),
        @URLMapping(id = "verCondicaoDeOcupacao", pattern = "/condicao-de-ocupacao/ver/#{condicaoDeOcupacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/condicaodeocupacao/visualizar.xhtml")
})
public class CondicaoDeOcupacaoControlador extends PrettyControlador<CondicaoDeOcupacao> implements Serializable, CRUD {

    @EJB
    private CondicaoDeOcupacaoFacade condicaoDeOcupacaoFacade;

    public CondicaoDeOcupacaoControlador() {
        metadata = new EntidadeMetaData(CondicaoDeOcupacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return condicaoDeOcupacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/condicao-de-ocupacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaCondicaoDeOcupacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarCondicaoDeOcupacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCondicaoDeOcupacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            super.salvar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public List<CondicaoDeOcupacao> listaFiltrando(String parte) {
        return condicaoDeOcupacaoFacade.listaFiltrando(parte.trim(), "descricao");
    }
}
