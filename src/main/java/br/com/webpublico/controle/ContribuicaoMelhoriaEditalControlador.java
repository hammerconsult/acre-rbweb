package br.com.webpublico.controle;


import br.com.webpublico.entidades.ContribuicaoMelhoriaEdital;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContribuicaoMelhoriaEditalFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by William on 28/06/2016.
 */


@ManagedBean(name = "contribuicaoMelhoriaEditalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-contribuicao-edital", pattern = "/contribuicao-melhoria-edital/novo/", viewId = "/faces/tributario/contribuicaomelhoria/edital/edita.xhtml"),
    @URLMapping(id = "edita-contribuicao-edital", pattern = "/contribuicao-melhoria-edital/editar/#{contribuicaoMelhoriaEditalControlador.id}/", viewId = "/faces/tributario/contribuicaomelhoria/edital/edita.xhtml"),
    @URLMapping(id = "ver-contribuicao-edital", pattern = "/contribuicao-melhoria-edital/ver/#{contribuicaoMelhoriaEditalControlador.id}/", viewId = "/faces/tributario/contribuicaomelhoria/edital/visualizar.xhtml"),
    @URLMapping(id = "listar-contribuicao-edital", pattern = "/contribuicao-melhoria-edital/listar/", viewId = "/faces/tributario/contribuicaomelhoria/edital/lista.xhtml")
})
public class ContribuicaoMelhoriaEditalControlador extends PrettyControlador<ContribuicaoMelhoriaEdital> implements CRUD {

    @EJB
    private ContribuicaoMelhoriaEditalFacade contribuicaoMelhoriaEditalFacade;

    public ContribuicaoMelhoriaEditalControlador() {
        super(ContribuicaoMelhoriaEdital.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return contribuicaoMelhoriaEditalFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/contribuicao-melhoria-edital/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "edita-contribuicao-edital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "novo-contribuicao-edital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-contribuicao-edital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getDataEdital().compareTo(selecionado.getDataPublicacao()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("A Data do Edital deve ser menor ou igual a Data da Publicação.");
            return false;
        }
        return true;
    }

    public List<ContribuicaoMelhoriaEdital> completarEdital(String parte) {
        return contribuicaoMelhoriaEditalFacade.listaFiltrando(parte.trim(), "descricao");
    }
}
