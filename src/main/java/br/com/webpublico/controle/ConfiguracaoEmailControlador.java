package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoEmail;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-email", pattern = "/configuracao-email/novo/", viewId = "/faces/admin/configuracaoemail/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-email", pattern = "/configuracao-email/editar/#{configuracaoEmailControlador.id}/", viewId = "/faces/admin/configuracaoemail/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-email", pattern = "/configuracao-email/listar/", viewId = "/faces/admin/configuracaoemail/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-email", pattern = "/configuracao-email/ver/#{configuracaoEmailControlador.id}/", viewId = "/faces/admin/configuracaoemail/visualizar.xhtml")
})
public class ConfiguracaoEmailControlador extends PrettyControlador<ConfiguracaoEmail> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoEmailFacade facade;

    public ConfiguracaoEmailControlador() {
        super(ConfiguracaoEmail.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-email/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-configuracao-email", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (facade.hasConfiguracaoCadastrada()) {
            selecionado = facade.recuperarUtilmo();
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId());
        }
    }

    @URLAction(mappingId = "ver-configuracao-email", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-email", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }


    @Override
    public void depoisDeSalvar() {
        EmailService.getInstance().atualizarConfiguracao(selecionado);
    }
}
