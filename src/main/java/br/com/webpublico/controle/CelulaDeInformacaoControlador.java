/*
 * Codigo gerado automaticamente em Fri Aug 26 14:08:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CampoCelulaDeInformacao;
import br.com.webpublico.entidades.CelulaDeInformacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CelulaDeInformacaoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean(name = "celulaDeInformacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaCelula", pattern = "/celula-de-informacao/novo/",
                viewId = "/faces/tributario/cadastromunicipal/celuladeinformacao/edita.xhtml"),

        @URLMapping(id = "editarCelula", pattern = "/celula-de-informacao/editar/#{celulaDeInformacaoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/celuladeinformacao/edita.xhtml"),

        @URLMapping(id = "listarCelula", pattern = "/celula-de-informacao/listar/",
                viewId = "/faces/tributario/cadastromunicipal/celuladeinformacao/lista.xhtml"),

        @URLMapping(id = "verCelula", pattern = "/celula-de-informacao/ver/#{celulaDeInformacaoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/celuladeinformacao/visualizar.xhtml")})
public class CelulaDeInformacaoControlador extends PrettyControlador<CelulaDeInformacao> implements Serializable, CRUD {

    @EJB
    private CelulaDeInformacaoFacade celulaDeInformacaoFacade;
    private CampoCelulaDeInformacao campoCelulaDeInformacaoSelecionado;

    public CelulaDeInformacaoControlador() {
        super(CelulaDeInformacao.class);
        metadata = new EntidadeMetaData(CelulaDeInformacao.class);
    }

    public CampoCelulaDeInformacao getCampoCelulaDeInformacaoSelecionado() {
        return campoCelulaDeInformacaoSelecionado;
    }

    public void setCampoCelulaDeInformacaoSelecionado(CampoCelulaDeInformacao campoCelulaDeInformacaoSelecionado) {
        this.campoCelulaDeInformacaoSelecionado = campoCelulaDeInformacaoSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return celulaDeInformacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/celula-de-informacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaCelula", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        campoCelulaDeInformacaoSelecionado = new CampoCelulaDeInformacao();
    }

    @URLAction(mappingId = "editarCelula", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        campoCelulaDeInformacaoSelecionado = new CampoCelulaDeInformacao();
    }

    @URLAction(mappingId = "verCelula", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void adicionarCamposCelula() {
        campoCelulaDeInformacaoSelecionado.setDataRegistro(new Date());
        campoCelulaDeInformacaoSelecionado.setCelulaDeInformacao(selecionado);
        selecionado.getCamposCelulaDeInformacao().add(campoCelulaDeInformacaoSelecionado);
        campoCelulaDeInformacaoSelecionado = new CampoCelulaDeInformacao();
    }

    public void removerCampo(CampoCelulaDeInformacao campo) {
        selecionado.getCamposCelulaDeInformacao().remove(campo);
    }
}
