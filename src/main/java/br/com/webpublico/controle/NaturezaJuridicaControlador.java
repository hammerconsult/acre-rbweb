package br.com.webpublico.controle;

import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.entidades.NaturezaJuridicaIsencao;
import br.com.webpublico.enums.SituacaoNaturezaJuridica;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NaturezaJuridicaFacade;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wellington
 */
@ManagedBean(name = "naturezaJuridicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaNaturezaJuridica", pattern = "/naturezajuridica/novo/", viewId = "/faces/tributario/cadastromunicipal/naturezajuridica/edita.xhtml"),
    @URLMapping(id = "editarNaturezaJuridica", pattern = "/naturezajuridica/editar/#{naturezaJuridicaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/naturezajuridica/edita.xhtml"),
    @URLMapping(id = "listarNaturezaJuridica", pattern = "/naturezajuridica/listar/", viewId = "/faces/tributario/cadastromunicipal/naturezajuridica/lista.xhtml"),
    @URLMapping(id = "verNaturezaJuridica", pattern = "/naturezajuridica/ver/#{naturezaJuridicaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/naturezajuridica/visualizar.xhtml")
})
public class NaturezaJuridicaControlador extends PrettyControlador<NaturezaJuridica> implements Serializable, CRUD {

    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    private TipoMovimentoMensal tipoIsencao;

    public NaturezaJuridicaControlador() {
        super(NaturezaJuridica.class);
    }

    @URLAction(mappingId = "novaNaturezaJuridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(naturezaJuridicaFacade.buscarProximoCodigo());
        selecionado.setSituacao(SituacaoNaturezaJuridica.ATIVO);
    }

    @URLAction(mappingId = "verNaturezaJuridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarNaturezaJuridica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return naturezaJuridicaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/naturezajuridica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoNaturezaJuridica object : TipoNaturezaJuridica.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (SituacaoNaturezaJuridica object : SituacaoNaturezaJuridica.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public boolean podeAlterarCodigo() {
        return this.operacao == null || !this.operacao.equals(Operacoes.EDITAR);
    }

    public boolean validaRegrasNegocio() {
        boolean retorno = true;
        if (this.operacao.equals(Operacoes.NOVO)) {
            NaturezaJuridica naturezaJuridica = naturezaJuridicaFacade.buscarNaturezaPorCodigo(selecionado.getCodigo());
            if (naturezaJuridica != null && naturezaJuridica.getId() != null) {
                retorno = false;
                FacesUtil.addError("O código " + selecionado.getCodigo() + " já está registrado para a Natureza Jurídica '" + naturezaJuridica.getDescricao() + "'! ", "");
            }
        }
        return retorno;
    }

    public boolean getHabilitaAutonomo() {
        return this.selecionado != null && this.selecionado.getTipoNaturezaJuridica() != null &&
            this.selecionado.getTipoNaturezaJuridica().equals(TipoNaturezaJuridica.FISICA);
    }

    public void limparAutonomo() {
        this.selecionado.setAutonomo(Boolean.FALSE);
    }

    @Override
    public void salvar() {
        if (!validaRegrasNegocio()) {
            return;
        }
        super.salvar();
    }


    public boolean podeExcluir() {
        boolean retorno = true;
        if (naturezaJuridicaFacade.existeVinculoComCMC(this.selecionado)) {
            retorno = false;
            FacesUtil.addError("Não foi possível realizar a exclusão! Este registro possui relacionamentos em outros cadastros.", "");
        }
        return retorno;
    }

    @Override
    public void excluir() {
        if (!podeExcluir()) {
            return;
        }
        super.excluir();
    }

    public TipoMovimentoMensal getTipoIsencao() {
        return tipoIsencao;
    }

    public void setTipoIsencao(TipoMovimentoMensal tipoIsencao) {
        this.tipoIsencao = tipoIsencao;
    }
}
