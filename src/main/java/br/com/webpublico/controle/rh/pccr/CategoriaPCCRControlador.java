package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.rh.pccr.*;
import br.com.webpublico.entidades.rh.pccr.CargoCategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.pccr.CategoriaPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.PlanoCargosSalariosPCCRFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCategoriaPCCR", pattern = "/categoria-pccr-novo/novo/", viewId = "/faces/rh/pccr/categoriapccr/edita.xhtml"),
        @URLMapping(id = "editarCategoriaPCCR", pattern = "/categoria-pccr-novo/editar/#{categoriaPCCRControlador.id}/", viewId = "/faces/rh/pccr/categoriapccr/edita.xhtml"),
        @URLMapping(id = "listarCategoriaPCCR", pattern = "/categoria-pccr-novo/listar/", viewId = "/faces/rh/pccr/categoriapccr/lista.xhtml"),
        @URLMapping(id = "verCategoriaPCCR", pattern = "/categoria-pccr-novo/ver/#{categoriaPCCRControlador.id}/", viewId = "/faces/rh/pccr/categoriapccr/visualizar.xhtml")
})
public class CategoriaPCCRControlador extends PrettyControlador<CategoriaPCCR> implements Serializable, CRUD {
    @EJB
    private CategoriaPCCRFacade categoriaPCCRFacade;
    private CategoriaPCCR filho;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    private ConverterAutoComplete converterPlanoCargosPCCR;
    private CargoCategoriaPCCR cargoCategoriaPCCR;
    @EJB
    private CargoFacade cargoFacade;

    public CategoriaPCCRControlador() {
        super(CategoriaPCCR.class);
    }

    public CargoCategoriaPCCR getCargoCategoriaPCCR() {
        return cargoCategoriaPCCR;
    }

    public void setCargoCategoriaPCCR(CargoCategoriaPCCR cargoCategoriaPCCR) {
        this.cargoCategoriaPCCR = cargoCategoriaPCCR;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/categoria-pccr-novo/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CategoriaPCCR getFilho() {
        return filho;
    }

    public void setFilho(CategoriaPCCR filho) {
        this.filho = filho;
    }

    public List<SelectItem> getPlanosCargosSalariosPCCRs() {
        return Util.getListSelectItem(planoCargosSalariosPCCRFacade.findPlanosCargosPCCRVigentes(SistemaFacade.getDataCorrente()));
    }


    public Converter getConverterPlanoPCCR() {
        if (converterPlanoCargosPCCR == null) {
            converterPlanoCargosPCCR = new ConverterAutoComplete(PlanoCargosSalariosPCCR.class, planoCargosSalariosPCCRFacade);
        }
        return converterPlanoCargosPCCR;
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaPCCRFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "novoCategoriaPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
        filho = new CategoriaPCCR();
        cargoCategoriaPCCR = new CargoCategoriaPCCR();
    }

    @URLAction(mappingId = "verCategoriaPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarCategoriaPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void salvar() {
        if (podeSalvar())
            super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private boolean podeSalvar() {
        if (selecionado.getPlanoCargosSalariosPCCR() == null) {
            FacesUtil.addWarn("Atenção ", "Selecione um plano de cargos e salários.");
            return false;
        }
        if (selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addWarn("Atenção ", "Preencha a descrição corretamente.");
            return false;
        }
        if (selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addWarn("Atenção ", "Preencha a descrição corretamente.");
            return false;
        }
        if (selecionado.getFilhos().isEmpty()) {
            FacesUtil.addWarn("Atenção ", "Por favor, adicione filhos e categoria.");
            return false;
        } else {
            for (CategoriaPCCR c : selecionado.getFilhos()) {
                if (c.getCargosCategoriaPCCR().isEmpty()) {
                    FacesUtil.addWarn("Atenção ", "Por favor, adicione cargo(s) à categoria filha.");
                    return false;
                }
            }
        }

        return true;
    }

    public List<Cargo> completaCargosPorTipoPCCR(String parte) {
        if (selecionado == null || selecionado.getPlanoCargosSalariosPCCR() == null) {
            return null;
        }
        return cargoFacade.buscarCargosPorTipoPCSAndVigenteAndAtivoAndCodigoOrDescricao(parte.trim(), selecionado.getPlanoCargosSalariosPCCR().getTipoPCS());
    }

    public void associaFilhoAoCargo(CategoriaPCCR cat) {
        if (selecionado.getPlanoCargosSalariosPCCR() == null) {
            FacesUtil.addWarn("Atenção ", "Selecione um plano de cargos e salários.");
        }
        cargoCategoriaPCCR.setCategoriaPCCR(cat);
    }

    public void addCargoCategoria() {
        if (podeAdicionarCargoCategoria()) {
            CategoriaPCCR fi = cargoCategoriaPCCR.getCategoriaPCCR();
            fi.getCargosCategoriaPCCR().add(cargoCategoriaPCCR);
            cargoCategoriaPCCR = new CargoCategoriaPCCR();
        }
    }

    private boolean podeAdicionarCargoCategoria() {
        if (cargoCategoriaPCCR != null) {
            if (cargoCategoriaPCCR.getCargo() == null) {
                FacesUtil.addWarn("Atenção ", "Selecione um cargo para a categoria.");
                return false;
            }
            if (cargoCategoriaPCCR.getProgressaoPCCR() == null) {
                FacesUtil.addWarn("Atenção ", "Selecione uma progressão para a categoria");
                return false;
            }
            if (cargoCategoriaPCCR.getInicioVigencia() == null) {
                FacesUtil.addWarn("Atenção ", "Preencha o inicio de vigência.");
                return false;
            }
        } else {
            logger.debug("cargo categoria null");
            return false;
        }

        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public void removeFilho(CategoriaPCCR fi) {
        selecionado.getFilhos().remove(fi);
    }

    public void removeCargo(CategoriaPCCR filho, CargoCategoriaPCCR cargo) {
        filho.getCargosCategoriaPCCR().remove(cargo);
    }

    public void addFilho() {
        if (podeAdicionarFilho()) {
            filho.setSuperior(selecionado);
            filho.setPlanoCargosSalariosPCCR(selecionado.getPlanoCargosSalariosPCCR());
            selecionado.getFilhos().add(filho);
            filho = new CategoriaPCCR();
        }
    }

    private boolean podeAdicionarFilho() {
        if (filho != null) {
            if (filho.getDescricao() != null && filho.getDescricao().equals("")) {
                FacesUtil.addWarn("Atenção ", "Preencha a descrição da Categoria filha.");
                return false;
            }
        }
        return true;

    }
}
