package br.com.webpublico.controle;

import br.com.webpublico.entidades.BandeiraCartao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BandeiraCartaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 15/03/2017.
 */
@ManagedBean(name = "bandeiraCartaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-bandeira", pattern = "/bandeira-cartao/novo/", viewId = "/faces/tributario/arrecadacao/bandeiracartao/edita.xhtml"),
    @URLMapping(id = "editar-bandeira", pattern = "/bandeira-cartao/editar/#{bandeiraCartaoControlador.id}/", viewId = "/faces/tributario/arrecadacao/bandeiracartao/edita.xhtml"),
    @URLMapping(id = "ver-bandeira", pattern = "/bandeira-cartao/ver/#{bandeiraCartaoControlador.id}/", viewId = "/faces/tributario/arrecadacao/bandeiracartao/visualizar.xhtml"),
    @URLMapping(id = "listar-bandeira", pattern = "/bandeira-cartao/listar/", viewId = "/faces/tributario/arrecadacao/bandeiracartao/lista.xhtml")
})
public class BandeiraCartaoControlador extends PrettyControlador<BandeiraCartao> implements CRUD {

    @EJB
    private BandeiraCartaoFacade bandeiraCartaoFacade;

    public BandeiraCartaoControlador() {
        super(BandeiraCartao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bandeira-cartao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return bandeiraCartaoFacade;
    }

    @URLAction(mappingId = "nova-bandeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }


    @URLAction(mappingId = "editar-bandeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    @URLAction(mappingId = "ver-bandeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    @Override
    public boolean validaRegrasEspecificas() {
        if (bandeiraCartaoFacade.hasBandeiraCadastrada(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe uma bandeira de cartão com a descrição <b>" + selecionado.getDescricao() + "</b>");
            return false;
        }
        if (selecionado.getQuantidadeParcelas() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a quantidade de parcelas.");
            return false;
        } else {
            if(selecionado.getQuantidadeParcelas() <= 0) {
                FacesUtil.addOperacaoNaoPermitida("A quantidade de parcelas deve ser maior que zero.");
                return false;
            }
        }
        return true;
    }
}
