package br.com.webpublico.controle.contabil.reinf;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.reinf.NaturezaRendimentoReinf;
import br.com.webpublico.enums.contabil.reinf.TipoGrupoNaturezaRendimentoReinf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.reinf.NaturezaRendimentoReinfFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoNaturezaRendimentoReinf", pattern = "/natureza-rendimento-reinf/novo/", viewId = "/faces/financeiro/reinf/natureza-rendimento-reinf/edita.xhtml"),
    @URLMapping(id = "editarNaturezaRendimentoReinf", pattern = "/natureza-rendimento-reinf/editar/#{naturezaRendimentoReinfControlador.id}/", viewId = "/faces/financeiro/reinf/natureza-rendimento-reinf/edita.xhtml"),
    @URLMapping(id = "verNaturezaRendimentoReinf", pattern = "/natureza-rendimento-reinf/ver/#{naturezaRendimentoReinfControlador.id}/", viewId = "/faces/financeiro/reinf/natureza-rendimento-reinf/visualizar.xhtml"),
    @URLMapping(id = "listarNaturezaRendimentoReinf", pattern = "/natureza-rendimento-reinf/listar/", viewId = "/faces/financeiro/reinf/natureza-rendimento-reinf/lista.xhtml")
})
public class NaturezaRendimentoReinfControlador extends PrettyControlador<NaturezaRendimentoReinf> implements Serializable, CRUD {
    @EJB
    private NaturezaRendimentoReinfFacade facade;
    private Boolean alterarVigenciaPorGrupo;
    private Date fimVigencia;

    public NaturezaRendimentoReinfControlador() {
        super(NaturezaRendimentoReinf.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/natureza-rendimento-reinf/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoNaturezaRendimentoReinf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "editarNaturezaRendimentoReinf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        alterarVigenciaPorGrupo = Boolean.FALSE;
        fimVigencia = selecionado.getFimVigencia();
    }

    @URLAction(mappingId = "verNaturezaRendimentoReinf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getConta() != null) {
            NaturezaRendimentoReinf natureza = facade.buscarNaturezaPorConta(selecionado.getConta());
            if (natureza != null && (selecionado.getId() == null || selecionado.getId().compareTo(natureza.getId()) != 0)) {
                FacesUtil.addOperacaoNaoPermitida("Natureza de Rendimento <b>(" + natureza.toString() + ")</b> já esta com a conta <b>(" + selecionado.getConta() + ")</b> informada como padrão.");
                return false;
            }
        }
        return true;
    }

    public List<SelectItem> getGrupos() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoGrupoNaturezaRendimentoReinf tipo : TipoGrupoNaturezaRendimentoReinf.values()) {
            String descricao = tipo.getDescricao();
            if (tipo.getDescricao().length() > 88) {
                descricao = descricao.substring(0, 88) + "...";
            }
            retorno.add(new SelectItem(tipo, descricao));
        }
        return retorno;
    }

    public List<Conta> completarContasDesdobradas(String parte) {
        return facade.buscarContasDesdobradas(parte);
    }

    public void atualizarFimVigenciaDialog() {
        fimVigencia = facade.getSistemaFacade().getDataOperacao();
    }

    public void encerrarVigencia() {
        try {
            if (alterarVigenciaPorGrupo) {
                validarGrupo();
                facade.atualizarFimVigenciaPorGrupo(fimVigencia, selecionado);
            }
            selecionado.setFimVigencia(fimVigencia);
            facade.salvar(selecionado);
            FacesUtil.executaJavaScript("dialogVigencias.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarGrupo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoGrupo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grupo deve ser informado.");
        }
        ve.lancarException();
    }

    public Boolean getAlterarVigenciaPorGrupo() {
        return alterarVigenciaPorGrupo;
    }

    public void setAlterarVigenciaPorGrupo(Boolean alterarVigenciaPorGrupo) {
        this.alterarVigenciaPorGrupo = alterarVigenciaPorGrupo;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }
}
