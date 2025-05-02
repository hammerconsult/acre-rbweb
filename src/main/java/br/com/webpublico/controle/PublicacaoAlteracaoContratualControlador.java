package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlteracaoContratual;
import br.com.webpublico.entidades.PublicacaoAlteracaoContratual;
import br.com.webpublico.entidades.VeiculoDePublicacao;
import br.com.webpublico.enums.SituacaoContrato;
import br.com.webpublico.enums.TipoAlteracaoContratual;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PublicacaoAlteracaoContratualFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-publicacao-aditivo", pattern = "/publicacao-aditivo/novo/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "ver-publicacao-aditivo", pattern = "/publicacao-aditivo/ver/#{publicacaoAlteracaoContratualControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/visualizar.xhtml"),
    @URLMapping(id = "editar-publicacao-aditivo", pattern = "/publicacao-aditivo/editar/#{publicacaoAlteracaoContratualControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "listar-publicacao-aditivo", pattern = "/publicacao-aditivo/listar/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/lista-aditivo.xhtml"),

    @URLMapping(id = "novo-publicacao-apostilamento", pattern = "/publicacao-apostilamento/novo/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "ver-publicacao-apostilamento", pattern = "/publicacao-apostilamento/ver/#{publicacaoAlteracaoContratualControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/visualizar.xhtml"),
    @URLMapping(id = "editar-publicacao-apostilamento", pattern = "/publicacao-apostilamento/editar/#{publicacaoAlteracaoContratualControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "listar-publicacao-apostilamento", pattern = "/publicacao-apostilamento/listar/", viewId = "/faces/administrativo/contrato/publicacao-alteracao-contratual/lista-apostilamento.xhtml")
})
public class PublicacaoAlteracaoContratualControlador extends PrettyControlador<PublicacaoAlteracaoContratual> implements Serializable, CRUD {

    @EJB
    private PublicacaoAlteracaoContratualFacade facade;
    private TipoAlteracaoContratual tipoAlteracao;

    public PublicacaoAlteracaoContratualControlador() {
        super(PublicacaoAlteracaoContratual.class);
    }

    @URLAction(mappingId = "novo-publicacao-aditivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPublicacaoAditivo() {
        super.novo();
        setTipoAlteracao(TipoAlteracaoContratual.ADITIVO);
    }

    @URLAction(mappingId = "ver-publicacao-aditivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPublicacaoAditivo() {
        super.ver();
        tipoAlteracao = selecionado.getAlteracaoContratual().getTipoAlteracaoContratual();
    }

    @URLAction(mappingId = "editar-publicacao-aditivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPublicacaoAditivo() {
        super.editar();
        tipoAlteracao = selecionado.getAlteracaoContratual().getTipoAlteracaoContratual();
    }

    @URLAction(mappingId = "novo-publicacao-apostilamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaPublicacaoApostilamento() {
        super.novo();
        setTipoAlteracao(TipoAlteracaoContratual.APOSTILAMENTO);
    }

    @URLAction(mappingId = "ver-publicacao-apostilamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPublicacaoApostilamento() {
        super.ver();
        tipoAlteracao = selecionado.getAlteracaoContratual().getTipoAlteracaoContratual();
    }

    @URLAction(mappingId = "editar-publicacao-apostilamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarPublicacaoApostilamento() {
        super.editar();
        tipoAlteracao = selecionado.getAlteracaoContratual().getTipoAlteracaoContratual();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (tipoAlteracao.isAditivo()) {
            return "/publicacao-aditivo/";
        }
        return "/publicacao-apostilamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<VeiculoDePublicacao> completarVeiculosDePublicacao(String filtro) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(filtro.trim(), "nome");
    }

    public List<AlteracaoContratual> completarAditivos(String filtro) {
        if (tipoAlteracao.isAditivo()) {
            return facade.getAlteracaoContratualFacade().buscarAlteracaoContratualContratoPorSituacoes(filtro.trim(), TipoAlteracaoContratual.ADITIVO, SituacaoContrato.values());
        }
        return facade.getAlteracaoContratualFacade().buscarAlteracaoContratualContratoPorSituacoes(filtro.trim(), TipoAlteracaoContratual.APOSTILAMENTO, SituacaoContrato.values());
    }

    public TipoAlteracaoContratual getTipoAlteracao() {
        return tipoAlteracao;
    }

    public void setTipoAlteracao(TipoAlteracaoContratual tipoAlteracao) {
        this.tipoAlteracao = tipoAlteracao;
    }
}
