package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoAssinatura;
import br.com.webpublico.entidades.ConfiguracaoAssinaturaUnidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoAssinaturaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "configuracaoAssinaturaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-assinatura", pattern = "/configuracao-assinatura-digital/novo/", viewId = "/faces/financeiro/configuracao-assinatura/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-assinatura", pattern = "/configuracao-assinatura-digital/editar/#{configuracaoAssinaturaControlador.id}/", viewId = "/faces/financeiro/configuracao-assinatura/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-assinatura", pattern = "/configuracao-assinatura-digital/listar/", viewId = "/faces/financeiro/configuracao-assinatura/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-assinatura", pattern = "/configuracao-assinatura-digital/ver/#{configuracaoAssinaturaControlador.id}/", viewId = "/faces/financeiro/configuracao-assinatura/visualizar.xhtml")
})
public class ConfiguracaoAssinaturaControlador extends ConfigEventoSuperControlador<ConfiguracaoAssinatura> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoAssinaturaFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ConfiguracaoAssinaturaControlador() {
        super(ConfiguracaoAssinatura.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @URLAction(mappingId = "novo-configuracao-assinatura", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-configuracao-assinatura", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarHierarquia();
    }

    @URLAction(mappingId = "edita-configuracao-assinatura", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarHierarquia();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-assinatura-digital/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
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

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        if (selecionado.getModulo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Módulo deve ser informado.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado.");
        }
        if (selecionado.getUnidades() == null || selecionado.getUnidades().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessario informar ao menos uma unidade orçamentária.");
        }
        ve.lancarException();
        if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Fim de Vigência deve ser superior ao campo Início de Vigência");
        }
        ve.lancarException();
    }

    public void adicionarHierarquia() {
        try {
            validarAdicionarHierarquia();
            ConfiguracaoAssinaturaUnidade cau = new ConfiguracaoAssinaturaUnidade();
            cau.setConfiguracaoAssinatura(selecionado);
            cau.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            cau.setHierarquiaOrganizacional(hierarquiaOrganizacional);
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), cau);
            hierarquiaOrganizacional = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarHierarquia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        if (selecionado.getModulo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Módulo deve ser informado.");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado.");
        }
        ve.lancarException();
        if (facade.hasConfiguracaoVigenteMesmoModuloEUnidade(selecionado, hierarquiaOrganizacional.getSubordinada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma pessoa vigente para o módulo e unidade orçamentária selecionadas.");
        }
        for (ConfiguracaoAssinaturaUnidade cau : selecionado.getUnidades()) {
            if (cau.getUnidadeOrganizacional().equals(hierarquiaOrganizacional.getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Unidade Orçamentária já adicionada.");
            }
        }
        ve.lancarException();
    }

    public void removerHierarquia(ConfiguracaoAssinaturaUnidade cau) {
        selecionado.getUnidades().remove(cau);
    }

    private void recuperarHierarquia() {
        for (ConfiguracaoAssinaturaUnidade cau : selecionado.getUnidades()) {
            if (cau.getHierarquiaOrganizacional() == null) {
                cau.setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaOrganizacionalPorUnidadeId(cau.getUnidadeOrganizacional().getId(), TipoHierarquiaOrganizacional.ORCAMENTARIA, (selecionado.getFimVigencia() != null ? selecionado.getFimVigencia() : selecionado.getInicioVigencia())));
            }
        }
    }

    public List<Pessoa> completarPessoasFisicasAtivas(String parte) {
        return facade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), facade.getSistemaFacade().getDataOperacao());
    }

    public List<SelectItem> getModulos() {
        return Util.getListSelectItem(ModuloTipoDoctoOficial.getNotas());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
