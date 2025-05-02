/*
 * Codigo gerado automaticamente em Wed Aug 03 16:16:03 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.entidades.GrauParentTipoDepend;
import br.com.webpublico.entidades.TipoDependente;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.enums.rh.previdencia.GrauParentescoBBPrev;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrauDeParentescoFacade;
import br.com.webpublico.negocios.TipoDependenteFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Leonardo
 */
@ManagedBean(name = "grauDeParentescoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGrauDeParentesco", pattern = "/graudeparentesco/novo/", viewId = "/faces/rh/administracaodepagamento/graudeparentesco/edita.xhtml"),
    @URLMapping(id = "editarGrauDeParentesco", pattern = "/graudeparentesco/editar/#{grauDeParentescoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/graudeparentesco/edita.xhtml"),
    @URLMapping(id = "listarGrauDeParentesco", pattern = "/graudeparentesco/listar/", viewId = "/faces/rh/administracaodepagamento/graudeparentesco/lista.xhtml"),
    @URLMapping(id = "verGrauDeParentesco", pattern = "/graudeparentesco/ver/#{grauDeParentescoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/graudeparentesco/visualizar.xhtml")
})
public class GrauDeParentescoControlador extends PrettyControlador<GrauDeParentesco> implements Serializable, CRUD {

    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    private ConverterGenerico converterTipoDependente;
    private GrauParentTipoDepend grauParentTipoDepend;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    private List<GrauParentTipoDepend> listaExcluidos;
    private TipoDependente tipoDependente;

    public GrauDeParentescoControlador() {
        super(GrauDeParentesco.class);
    }

    public GrauDeParentescoFacade getFacade() {
        return grauDeParentescoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return grauDeParentescoFacade;
    }

    @URLAction(mappingId = "novoGrauDeParentesco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        //System.out.println("sessao -> " + getSessao());
        selecionado.setGrauParentTipoDepends(new ArrayList<GrauParentTipoDepend>());
        grauParentTipoDepend = new GrauParentTipoDepend();
        listaExcluidos = Lists.newArrayList();
    }

    @URLAction(mappingId = "verGrauDeParentesco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarGrauDeParentesco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        grauParentTipoDepend = new GrauParentTipoDepend();
        listaExcluidos = new ArrayList<>();
    }

    public void adicionaGrauParentTipoDepend() {
        if (tipoDependente != null) {
            grauParentTipoDepend.setTipoDependente(tipoDependente);
            grauParentTipoDepend.setGrauDeParentesco(((GrauDeParentesco) selecionado));
            grauParentTipoDepend.setDataRegistro(new Date());
            ((GrauDeParentesco) selecionado).getGrauParentTipoDepends().add(grauParentTipoDepend);
            grauParentTipoDepend = new GrauParentTipoDepend();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ops", "Selecione um Tipo de Dependente"));
        }

    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            grauDeParentescoFacade.salvar(selecionado, listaExcluidos);
            listaExcluidos.clear();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo descrição deve ser informado");
        }
        if (Strings.isNullOrEmpty(selecionado.getCodigoEsocial())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código do E-social deve ser informado");
        }
        if (Strings.isNullOrEmpty(selecionado.getCodigoSig())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código do Sig deve ser informado");
        }
        if (selecionado.getGrauParentescoBBPrev() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Grau de Parentesco BBPrev deve ser informado");
        }
        if (selecionado.getTipoDependEstudoAtuarial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Dependente Estudo Atuarial deve ser informado.");
        }
        ve.lancarException();
    }

    public void removeTipoDependete(GrauParentTipoDepend grauParentesco) {
        selecionado.getGrauParentTipoDepends().remove(grauParentesco);
        listaExcluidos.add(grauParentesco);
    }

    public List<SelectItem> getTiposDependente() {
        return Util.getListSelectItem(tipoDependenteFacade.lista());
    }

    public List<SelectItem> getGrauParentescoBBPrev() {
        return Util.getListSelectItem(GrauParentescoBBPrev.values());
    }

    public ConverterGenerico getConverterTipoDependente() {
        if (converterTipoDependente == null) {
            converterTipoDependente = new ConverterGenerico(TipoDependente.class, tipoDependenteFacade);
        }
        return converterTipoDependente;
    }

    public TipoDependente getTipoDependente() {
        return tipoDependente;
    }

    public void setTipoDependente(TipoDependente tipoDependente) {
        this.tipoDependente = tipoDependente;
    }

    public GrauParentTipoDepend getGrauParentTipoDepend() {
        return grauParentTipoDepend;
    }

    public void setGrauParentTipoDepend(GrauParentTipoDepend grauParentTipoDepend) {
        this.grauParentTipoDepend = grauParentTipoDepend;
    }

    public List<SelectItem> getTiposDependenciasEstudoAtuarial() {
        return Util.getListSelectItem(TipoDependenciaEstudoAtuarial.values());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/graudeparentesco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
