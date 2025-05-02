package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTributoBI;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.tributario.TipoTributoBI;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTributoBIFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-config-tributo-bi", pattern = "/config-tributo-bi/configuracao-tributario/novo/", viewId = "/faces/bi-exportacao/configuracao-tributario/edita.xhtml"),
    @URLMapping(id = "edita-config-tributo-bi", pattern = "/config-tributo-bi/configuracao-tributario/editar/#{configuracaoTributoBIControlador.id}/", viewId = "/faces/bi-exportacao/configuracao-tributario/edita.xhtml"),
    @URLMapping(id = "listar-config-tributo-bi", pattern = "/config-tributo-bi/configuracao-tributario/listar/", viewId = "/faces/bi-exportacao/configuracao-tributario/lista.xhtml"),
    @URLMapping(id = "ver-config-tributo-bi", pattern = "/config-tributo-bi/configuracao-tributario/ver/#{configuracaoTributoBIControlador.id}/", viewId = "/faces/bi-exportacao/configuracao-tributario/visualizar.xhtml")
})
public class ConfiguracaoTributoBIControlador extends PrettyControlador<ConfiguracaoTributoBI> implements CRUD {

    @EJB
    private ConfiguracaoTributoBIFacade configuracaoTributoBIFacade;

    public ConfiguracaoTributoBIControlador() {
        super(ConfiguracaoTributoBI.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/config-tributo-bi/configuracao-tributario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoTributoBIFacade;
    }

    @Override
    public void salvar() {
        try {
            validarConfiguracao();
            selecionado = configuracaoTributoBIFacade.salvarConfiguracao(selecionado);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Configuração de Tributos do BI. Detalhes: " + e.getMessage());
            logger.error("Erro ao salvar configuração de tributos bi. ", e);
        }
    }

    private void validarConfiguracao() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getTipoTributoBI() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Tributo BI deve ser informado.");
        }
        if (selecionado.getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tributo deve ser informado.");
        }
        if (selecionado.getTipoTributoBI() != null && selecionado.getTributo() != null) {
            ConfiguracaoTributoBI configuracao = configuracaoTributoBIFacade.buscarConfiguracaoPorTipoAndTributo(selecionado);
            if ((selecionado.getId() != null && configuracao != null && !selecionado.getId().equals(configuracao.getId())) ||
                (selecionado.getId() == null && configuracao != null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Já existe uma configuração salva com o Tipo de Tributo: " +
                    selecionado.getTipoTributoBI().getDescricao() + " e Tributo: " + selecionado.getTributo());
            }
        }
        ve.lancarException();
    }

    @Override
    @URLAction(mappingId = "novo-config-tributo-bi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "edita-config-tributo-bi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-config-tributo-bi", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> buscarTiposTributoBI() {
        return Util.getListSelectItem(TipoTributoBI.values(), false);
    }

    public List<Tributo> buscarTributos(String parte) {
        return configuracaoTributoBIFacade.buscarTributosPelaDescricao(parte);
    }
}
