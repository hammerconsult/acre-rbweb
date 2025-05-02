package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.EnquadramentoPCCR;
import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.entidades.rh.pccr.ProgressaoPCCR;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.pccr.EnquadramentoPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ProgressaoPCCRFacade;
import br.com.webpublico.util.Persistencia;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/06/14
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novoEnquadramentoPCCR", pattern = "/enquadramentopccr/novo/", viewId = "/faces/rh/pccr/enquadramentopccr/edita.xhtml")
//        //@URLMapping(id = "editarEnquadramentoFuncional", pattern = "/enquadramento-funcional/editar/#{enquadramentoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/edita.xhtml"),
//        //@URLMapping(id = "listarEnquadramentoFuncional", pattern = "/enquadramento-funcional/listar/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/lista.xhtml"),
//        ///@URLMapping(id = "verEnquadramentoFuncional", pattern = "/enquadramento-funcional/ver/#{enquadramentoFuncionalControlador.id}/", viewId = "/faces/rh/administracaodepagamento/enquadramentofuncional/visualizar.xhtml")
//})
public class EnquadramentoPCCRControlador extends PrettyControlador<EnquadramentoPCCR> implements Serializable, CRUD {
    @EJB
    private EnquadramentoPCCRFacade enquadramentoPCCRFacade;

    private PlanoCargosSalariosPCCR planoCargosSalariosPCCR;
    private CategoriaPCCR categoriaPCCR;
    private ProgressaoPCCR progressaoPCCR;
    @EJB
    private ProgressaoPCCRFacade progressaoPCCRFacade;

    public EnquadramentoPCCRControlador() {
        super(EnquadramentoPCCR.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/enquadramentopccr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoPCCRFacade;
    }

    public PlanoCargosSalariosPCCR getPlanoCargosSalariosPCCR() {
        return planoCargosSalariosPCCR;
    }

    public void setPlanoCargosSalariosPCCR(PlanoCargosSalariosPCCR planoCargosSalariosPCCR) {
        this.planoCargosSalariosPCCR = planoCargosSalariosPCCR;
    }

    public CategoriaPCCR getCategoriaPCCR() {
        return categoriaPCCR;
    }

    public void setCategoriaPCCR(CategoriaPCCR categoriaPCCR) {
        this.categoriaPCCR = categoriaPCCR;
    }

    public ProgressaoPCCR getProgressaoPCCR() {
        return progressaoPCCR;
    }

    public void setProgressaoPCCR(ProgressaoPCCR progressaoPCCR) {
        this.progressaoPCCR = progressaoPCCR;
    }

    public List<SelectItem> getPlanosCargosSalariosPCCRs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoCargosSalariosPCCR object : enquadramentoPCCRFacade.findPlanosCargosPCCRVigentes(SistemaFacade.getDataCorrente())) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriasPCCRs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosPCCR == null) return toReturn;
        for (CategoriaPCCR object : enquadramentoPCCRFacade.findCategoriasPCCRByPlanoCargos(planoCargosSalariosPCCR)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getProgressoesPCCRs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
        if (planoCargosSalariosPCCR == null) return toReturn;
        for (ProgressaoPCCR object : enquadramentoPCCRFacade.findProgressoesPCCRByPlanoCargos(planoCargosSalariosPCCR)) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

//    @URLAction(mappingId = "novoEnquadramentoPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    @Override
    public void novo() {
        super.novo();
        planoCargosSalariosPCCR = null;
        categoriaPCCR = null;
        progressaoPCCR = null;
    }


    public Converter getConverterPlanoPCCR() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                Object chave = null;
                try {
                    chave = Persistencia.getFieldId(PlanoCargosSalariosPCCR.class).getType().getConstructor(String.class).newInstance(s);
                    PlanoCargosSalariosPCCR plano = (PlanoCargosSalariosPCCR) enquadramentoPCCRFacade.findOnePlanosCargosPCCR((Long) chave);
                    return plano;
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                if (o != null) {
                    return String.valueOf(Persistencia.getId(o));
//                    return ((PlanoCargosSalariosPCCR)o).getDescricao()+"";
                } else {
                    return null;
                }
            }
        };
    }

    public void gerarDados() {
        enquadramentoPCCRFacade.preencheTudoTUUUUDO();
    }
    public void gerarProgessao() {
        progressaoPCCRFacade.preencherProgressoes();
    }

    public Converter getConverterCategoriaPCCR() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                Object chave = null;
                try {
                    chave = Persistencia.getFieldId(PlanoCargosSalariosPCCR.class).getType().getConstructor(String.class).newInstance(s);
                    return enquadramentoPCCRFacade.recuperar(CategoriaPCCR.class, chave);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return null;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                if (o != null) {
                    return String.valueOf(Persistencia.getId(o));
                } else {
                    return null;
                }
            }
        };
    }

    public Converter getConverterProgressaoPCCR() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
                Object chave = null;
                try {
                    chave = Persistencia.getFieldId(PlanoCargosSalariosPCCR.class).getType().getConstructor(String.class).newInstance(s);
                    return enquadramentoPCCRFacade.recuperar(ProgressaoPCCR.class, chave);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return null;
            }

            @Override
            public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
                if (o != null) {
                    return String.valueOf(Persistencia.getId(o));
                } else {
                    return null;
                }
            }
        };
    }

}
