package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoCabecalho;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoCabecalhoFacade;
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
 * Created by mga on 24/07/2017.
 */
@ManagedBean(name = "configuracaoCabecalhoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-config-cabecalho", pattern = "/configuracao-cabecalho/novo/", viewId = "/faces/comum/configuracao-cabecalho/edita.xhtml"),
    @URLMapping(id = "editar-config-cabecalho", pattern = "/configuracao-cabecalho/editar/#{configuracaoCabecalhoControlador.id}/", viewId = "/faces/comum/configuracao-cabecalho/edita.xhtml"),
    @URLMapping(id = "listar-config-cabecalho", pattern = "/configuracao-cabecalho/listar/", viewId = "/faces/comum/configuracao-cabecalho/lista.xhtml"),
    @URLMapping(id = "ver-config-cabecalho", pattern = "/configuracao-cabecalho/ver/#{configuracaoCabecalhoControlador.id}/", viewId = "/faces/comum/configuracao-cabecalho/visualizar.xhtml")
})
public class ConfiguracaoCabecalhoControlador extends PrettyControlador<ConfiguracaoCabecalho> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoCabecalhoFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ConfiguracaoCabecalhoControlador() {
        super(ConfiguracaoCabecalho.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-cabecalho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "nova-config-cabecalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-config-cabecalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-config-cabecalho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        hierarquiaOrganizacional = facade.getHierarquiaUnidade(selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    public void salvar() {
        try {
            definirUnidade();
            selecionado.realizarValidacoes();
            validarRegrasEspecificas();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade organizacional deve ser informado.");
        }
        if (facade.verificarSeExisteConfiguracaoIgual(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração para unidade organizacional: " + hierarquiaOrganizacional + ".");
        }
        if (selecionado.getPrincipal() && facade.hasPrincipal(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração como Principal.");
        }
        ve.lancarException();
    }

    private void definirUnidade() {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
