/*
 * Codigo gerado automaticamente em Thu Jul 12 14:10:35 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.DividaPassiva;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DividaPassivaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDividaPassiva", pattern = "/divida-passiva/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividapassiva/edita.xhtml"),
    @URLMapping(id = "listarDividaPassiva", pattern = "/divida-passiva/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividapassiva/lista.xhtml"),
    @URLMapping(id = "editarDividaPassiva", pattern = "/divida-passiva/editar/#{dividaPassivaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividapassiva/edita.xhtml"),
    @URLMapping(id = "verDividaPassiva", pattern = "/divida-passiva/ver/#{dividaPassivaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/dividapassiva/visualizar.xhtml"),
})
public class DividaPassivaControlador extends PrettyControlador<DividaPassiva> implements Serializable, CRUD {

    @EJB
    private DividaPassivaFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public DividaPassivaControlador() {
        super(DividaPassiva.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-passiva/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoDividaPassiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        hierarquiaOrganizacional = null;
    }

    @URLAction(mappingId = "verDividaPassiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarHierarquia();
    }

    @URLAction(mappingId = "editarDividaPassiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarHierarquia();
    }

    public void recuperarHierarquia() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(facade.getSistemaFacade().getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarValor();
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
            if (Operacoes.NOVO.equals(operacao)) {
                selecionado.setNumero(getMaiorNumeroMaisUm().toString());
                getFacede().salvarNovo(selecionado);
            } else {
                getFacede().salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarValor() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorLancamento().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero.");
        }
        ve.lancarException();
    }

    public Integer getMaiorNumeroMaisUm() {
        return facade.retornaUltimoNumeroLancamentoManual().add(BigDecimal.ONE).intValue();
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return facade.getEventoContabilFacade().buscarEventosContabeisPorCodigoOrDescricao(parte.trim());
    }

    public List<Contrato> completarContratos(String parte) {
        return facade.getContratoFacade().buscarContratos(parte.trim());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
