package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroMalaDiretaIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroMalaDiretaIptuFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Pedro on 19/02/2024.
 */
@ManagedBean(name = "parametroMalaDiretaIptuControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "novo-parametro-mala-direta", pattern = "/parametro-mala-direta-iptu/novo/", viewId = "/faces/tributario/iptu/maladireta/parametromaladireta/edita.xhtml"),
    @URLMapping(id = "listar-parametro-mala-direta", pattern = "/parametro-mala-direta-iptu/listar/", viewId = "/faces/tributario/iptu/maladireta/parametromaladireta/lista.xhtml"),
    @URLMapping(id = "editar-parametro-mala-direta", pattern = "/parametro-mala-direta-iptu/editar/#{parametroMalaDiretaIptuControlador.id}/", viewId = "/faces/tributario/iptu/maladireta/parametromaladireta/edita.xhtml"),
    @URLMapping(id = "ver-parametro-mala-direta", pattern = "/parametro-mala-direta-iptu/ver/#{parametroMalaDiretaIptuControlador.id}/", viewId = "/faces/tributario/iptu/maladireta/parametromaladireta/visualizar.xhtml"),
})
public class ParametroMalaDiretaIptuControlador extends PrettyControlador<ParametroMalaDiretaIPTU> implements CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ParametroMalaDiretaIptuControlador.class);
    @EJB
    private ParametroMalaDiretaIptuFacade parametroMalaDiretaIptuFacade;

    public ParametroMalaDiretaIptuControlador() {
        super(ParametroMalaDiretaIPTU.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroMalaDiretaIptuFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-mala-direta-iptu/";
    }

    @URLAction(mappingId = "novo-parametro-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-parametro-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-parametro-mala-direta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroMalaDiretaIptuFacade.jaExisteParamatroComExercicio(selecionado.getId(), selecionado.getExercicio().getAno())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Ja existe um parâmetro cadastrado para o exercício de " + selecionado.getExercicio().getAno());
        }
        ve.lancarException();
        return !ve.temMensagens();
    }
}
