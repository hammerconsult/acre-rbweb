/*
 * Codigo gerado automaticamente em Thu Jan 12 10:56:38 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoServicoCompravel;
import br.com.webpublico.entidades.ServicoCompravel;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoServicoCompravelFacade;
import br.com.webpublico.negocios.ServicoCompravelFacade;
import br.com.webpublico.negocios.UnidadeMedidaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "servicoCompravelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-servico-compravel", pattern = "/servico-compravel/novo/", viewId = "/faces/administrativo/licitacao/servicocompravel/edita.xhtml"),
    @URLMapping(id = "editar-servico-compravel", pattern = "/servico-compravel/editar/#{servicoCompravelControlador.id}/", viewId = "/faces/administrativo/licitacao/servicocompravel/edita.xhtml"),
    @URLMapping(id = "ver-servico-compravel", pattern = "/servico-compravel/ver/#{servicoCompravelControlador.id}/", viewId = "/faces/administrativo/licitacao/servicocompravel/visualizar.xhtml"),
    @URLMapping(id = "listar-servico-compravel", pattern = "/servico-compravel/listar/", viewId = "/faces/administrativo/licitacao/servicocompravel/lista.xhtml")
})
public class ServicoCompravelControlador extends PrettyControlador<ServicoCompravel> implements Serializable, CRUD {

    @EJB
    private ServicoCompravelFacade servicoCompravelFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private GrupoServicoCompravelFacade grupoServicoCompravelFacade;
    private ConverterAutoComplete converterGrupoServicoCompravel;
    private ConverterAutoComplete converterUnidadeMedida;
    private GrupoServicoCompravel grupoServicoCompravelSelecionado;
    private UnidadeMedida unidadeMedida;
    private ServicoCompravel servicoCompravel;

    public ServicoCompravelControlador() {
        super(ServicoCompravel.class);
    }

    public ServicoCompravel getServicoCompravel() {
        return servicoCompravel;
    }

    public void setServicoCompravel(ServicoCompravel servicoCompravel) {
        this.servicoCompravel = servicoCompravel;
    }

    public GrupoServicoCompravel getGrupoServicoCompravelSelecionado() {
        return grupoServicoCompravelSelecionado;
    }

    public void setGrupoServicoCompravelSelecionado(GrupoServicoCompravel grupoServicoCompravelSelecionado) {
        this.grupoServicoCompravelSelecionado = grupoServicoCompravelSelecionado;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public GrupoServicoCompravelFacade getGrupoServicoCompravelFacade() {
        return grupoServicoCompravelFacade;
    }

    public void setGrupoServicoCompravelFacade(GrupoServicoCompravelFacade grupoServicoCompravelFacade) {
        this.grupoServicoCompravelFacade = grupoServicoCompravelFacade;
    }

    public ServicoCompravelFacade getServicoCompravelFacade() {
        return servicoCompravelFacade;
    }

    public void setServicoCompravelFacade(ServicoCompravelFacade servicoCompravelFacade) {
        this.servicoCompravelFacade = servicoCompravelFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }

    public void setUnidadeMedidaFacade(UnidadeMedidaFacade unidadeMedidaFacade) {
        this.unidadeMedidaFacade = unidadeMedidaFacade;
    }

    public List<UnidadeMedida> completaUnidadeMedida(String parte) {
        return unidadeMedidaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<GrupoServicoCompravel> completaGrupoServicoCompravel(String parte) {
        return grupoServicoCompravelFacade.listaDescricaoPeloId(parte.trim());
    }

    public ConverterAutoComplete converterUnidMedida() {
        if (this.converterUnidadeMedida == null) {
            this.converterUnidadeMedida = new ConverterAutoComplete(UnidadeMedida.class, unidadeMedidaFacade);
        }
        return converterUnidadeMedida;
    }

    public ConverterAutoComplete converterGrupoServComp() {
        if (this.converterGrupoServicoCompravel == null) {
            this.converterGrupoServicoCompravel = new ConverterAutoComplete(GrupoServicoCompravel.class, grupoServicoCompravelFacade);
        }
        return this.converterGrupoServicoCompravel;
    }

    @Override
    public void salvar() {
        if (((ServicoCompravel) selecionado).getCodigo() < 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código inválido!", "Não pode Ser Repetido ou menor que 1!"));
            return;
        }

        if (servicoCompravelFacade.validaCodigoEditar((ServicoCompravel) selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código inválido!", "Não pode Ser Repetido ou menor que 1!"));
            return;
        }
        super.salvar();
    }

    @Override
    public AbstractFacade getFacede() {
        return servicoCompravelFacade;
    }

    @Override
    public void excluir() {
        if (servicoCompravelFacade.validaAlteracaoEExclusao((ServicoCompravel) selecionado)) {
            super.excluir();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível remover!", "Este Serviço já encontra-se vinculado!"));
        }
    }

    @URLAction(mappingId = "novo-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        unidadeMedida = new UnidadeMedida();
        grupoServicoCompravelSelecionado = new GrupoServicoCompravel();
        ((ServicoCompravel) selecionado).setCodigo(servicoCompravelFacade.recuperaUltimoCodigo());
    }

    @URLAction(mappingId = "ver-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-servico-compravel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/servico-compravel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
