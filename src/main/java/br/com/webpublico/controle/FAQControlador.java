package br.com.webpublico.controle;

import br.com.webpublico.entidades.FAQ;
import br.com.webpublico.enums.ModuloFAQ;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FAQFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 12/07/14
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "faqControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novofaq", pattern = "/faq/novo/", viewId = "/faces/admin/faq/edita.xhtml"),
        @URLMapping(id = "editarfaq", pattern = "/faq/editar/#{faqControlador.id}/", viewId = "/faces/admin/faq/edita.xhtml"),
        @URLMapping(id = "verfaq", pattern = "/faq/ver/#{faqControlador.id}/", viewId = "/faces/admin/faq/visualizar.xhtml"),
        @URLMapping(id = "listarfaq", pattern = "/faq/listar/", viewId = "/faces/admin/faq/lista.xhtml")
})
public class FAQControlador extends PrettyControlador<FAQ> implements Serializable, CRUD {

    @EJB
    private FAQFacade faqFacade;
    //Variaveis utilizados no corpo.xhtml
    private ModuloFAQ moduloFAQ;
    private String filtro;
    private List<FAQ> faqs;

    public FAQControlador() {
        super(FAQ.class);
        filtro = "";
    }

    @Override
    public AbstractFacade getFacede() {
        return faqFacade;
    }

    @URLAction(mappingId = "novofaq", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verfaq", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarfaq", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/faq/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getModulos() {
        return Util.getListSelectItem(getListModulos());
    }

    public List<ModuloFAQ> getListModulos() {
        return Arrays.asList(ModuloFAQ.values());
    }

    //Métodos utilizados no corpo.xhtml
    public ModuloFAQ getModuloFAQ() {
        return moduloFAQ;
    }

    public void setModuloFAQ(ModuloFAQ moduloFAQ) {
        this.moduloFAQ = moduloFAQ;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<FAQ> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<FAQ> faqs) {
        this.faqs = faqs;
    }

    public void atribuirModulo(ModuloFAQ moduloFAQ) {
        this.moduloFAQ = moduloFAQ;
        faqs = null;
        filtro = "";
    }

    public List<FAQ> getFAQsPorModulo() {
        if (moduloFAQ == null) {
            return new ArrayList<FAQ>();
        }
//        if (faqs == null) {
        faqs = faqFacade.getFAQsPorModulo(moduloFAQ, filtro);
//        }
        List<FAQ> retorno = new ArrayList<FAQ>();
        for (FAQ faq : faqs) {
            if (faq.getAssunto().toLowerCase().contains(filtro.toLowerCase())
                    || faq.getPergunta().toLowerCase().contains(filtro.toLowerCase())
                    || faq.getResposta().toLowerCase().contains(filtro.toLowerCase())) {
                retorno.add(faq);
            }
        }
        if (filtro.trim().isEmpty()) {
            return retorno;
        }
        for (FAQ faq : retorno) {
            faq.setAssunto(faq.getAssunto().toLowerCase().replace(filtro.toLowerCase(), "<b><font style='background-color:silver'>" + filtro + "</font></b>"));
            faq.setResposta(faq.getResposta().toLowerCase().replace(filtro.toLowerCase(), "<b><font style='background-color:silver'>" + filtro + "</font></b>"));
            faq.setPergunta(faq.getPergunta().toLowerCase().replace(filtro.toLowerCase(), "<b><font style='background-color:silver'>" + filtro + "</font></b>"));
        }

        return retorno;
    }
}
