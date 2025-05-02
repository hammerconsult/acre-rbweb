/*
 * Codigo gerado automaticamente em Wed Jan 04 10:38:40 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.TomadorDeServico;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaJuridicaFacade;
import br.com.webpublico.negocios.TomadorDeServicoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "tomadorDeServicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTomadorDeServico", pattern = "/rh/tomador-de-servico/novo/", viewId = "/faces/rh/administracaodepagamento/tomadordeservico/edita.xhtml"),
    @URLMapping(id = "listaTomadorDeServico", pattern = "/rh/tomador-de-servico/listar/", viewId = "/faces/rh/administracaodepagamento/tomadordeservico/lista.xhtml"),
    @URLMapping(id = "verTomadorDeServico", pattern = "/rh/tomador-de-servico/ver/#{tomadorDeServicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tomadordeservico/visualizar.xhtml"),
    @URLMapping(id = "editarTomadorDeServico", pattern = "/rh/tomador-de-servico/editar/#{tomadorDeServicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tomadordeservico/edita.xhtml"),
})
public class TomadorDeServicoControlador extends PrettyControlador<TomadorDeServico> implements Serializable, CRUD {

    @EJB
    private TomadorDeServicoFacade tomadorDeServicoFacade;
    private ConverterAutoComplete converterTomador;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;

    public Converter getConverterTomador() {
        if (converterTomador == null) {
            converterTomador = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterTomador;
    }

    public List<PessoaJuridica> completaTomador(String parte) {
        return pessoaJuridicaFacade.listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public TomadorDeServicoControlador() {
        super(TomadorDeServico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tomadorDeServicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tomador-de-servico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tomadorDeServicoFacade.verificaTomador(selecionado)) {
            return true;
        } else {
            FacesUtil.addError("Esta pessoa já esta cadastrada como Tomador de Serviço!", "");
            return false;
        }
    }

    @URLAction(mappingId = "novoTomadorDeServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTomadorDeServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTomadorDeServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }


}
