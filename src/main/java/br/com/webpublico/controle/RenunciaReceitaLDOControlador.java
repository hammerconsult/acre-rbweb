/*
 * Codigo gerado automaticamente em Thu Jun 09 10:17:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeRenunciaLDO;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.negocios.RenunciaReceitaLDOFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
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
import java.util.List;

@ManagedBean(name = "renunciaReceitaLDOControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-renuncia-receita-ldo", pattern = "/renuncia-receita-ldo/novo/", viewId = "/faces/financeiro/ppa/renunciareceitaldo/edita.xhtml"),
        @URLMapping(id = "editar-renuncia-receita-ldo", pattern = "/renuncia-receita-ldo/editar/#{renunciaReceitaLDOControlador.id}/", viewId = "/faces/financeiro/ppa/renunciareceitaldo/edita.xhtml"),
        @URLMapping(id = "ver-renuncia-receita-ldo", pattern = "/renuncia-receita-ldo/ver/#{renunciaReceitaLDOControlador.id}/", viewId = "/faces/financeiro/ppa/renunciareceitaldo/visualizar.xhtml"),
        @URLMapping(id = "listar-renuncia-receita-ldo", pattern = "/renuncia-receita-ldo/listar/", viewId = "/faces/financeiro/ppa/renunciareceitaldo/lista.xhtml")
})
public class RenunciaReceitaLDOControlador extends PrettyControlador<RenunciaReceitaLDO> implements Serializable, CRUD {

    @EJB
    private RenunciaReceitaLDOFacade renunciaReceitaLDOFacade;
    @EJB
    private LDOFacade ldoFacade;
    private ConverterGenerico converterLdo;
    private List<RenunciaReceitaExercicioLDO> listarenunciaReceitaExercicio;
    private MoneyConverter moneyConverter;

    public RenunciaReceitaLDOControlador() {
        super(RenunciaReceitaLDO.class);
    }

    public RenunciaReceitaLDOFacade getFacade() {
        return renunciaReceitaLDOFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return renunciaReceitaLDOFacade;
    }

    public List<SelectItem> getListaLDO() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        for (LDO ldo : ldoFacade.listLDOExercicio(sistemaControlador.getExercicioCorrente())) {
            toReturn.add(new SelectItem(ldo, ldo.toString()));
        }
        return toReturn;
    }

    public List<LDO> getLdosPorExercicio(Exercicio ex) {
        return ldoFacade.listaLDOPorExercicio(ex);
    }


    public Boolean validaCampos() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novo-renuncia-receita-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        listarenunciaReceitaExercicio = new ArrayList<RenunciaReceitaExercicioLDO>();
        super.novo();
    }

    @URLAction(mappingId = "ver-renuncia-receita-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editar-renuncia-receita-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/renuncia-receita-ldo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaExerciciosLDO() && validaCampos()) {
            selecionado.setRenunciaReceitaExercicioLDOs(listarenunciaReceitaExercicio);
            super.salvar();
        }

    }

    public void recuperarEditaVer() {
        selecionado = renunciaReceitaLDOFacade.recuperar(super.getId());
        listarenunciaReceitaExercicio = selecionado.getRenunciaReceitaExercicioLDOs();
    }

    /**
     * @return the converterLdo
     */
    public ConverterGenerico getConverterLdo() {
        if (converterLdo == null) {
            converterLdo = new ConverterGenerico(LDO.class, ldoFacade);
        }
        return converterLdo;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<SelectItem> getModalidadesRenuncias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ModalidadeRenunciaLDO object : ModalidadeRenunciaLDO.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    /**
     * @return the listarenunciaReceitaExercicio
     */
    public List<RenunciaReceitaExercicioLDO> getListarenunciaReceitaExercicio() {
        return listarenunciaReceitaExercicio;
    }

    /**
     * @param listarenunciaReceitaExercicio the listarenunciaReceitaExercicio to
     *                                      set
     */
    public void setListarenunciaReceitaExercicio(List<RenunciaReceitaExercicioLDO> listarenunciaReceitaExercicio) {
        this.listarenunciaReceitaExercicio = listarenunciaReceitaExercicio;
    }

    public void atualizaLista() {
        RenunciaReceitaLDO r = ((RenunciaReceitaLDO) selecionado);
        try {
            listarenunciaReceitaExercicio = renunciaReceitaLDOFacade.listaRenunciaReceitaLDO(r);
            validaExerciciosLDO();
        } catch (ExcecaoNegocioGenerica ex) {
            this.listarenunciaReceitaExercicio = new ArrayList<RenunciaReceitaExercicioLDO>();
        }
    }

    private boolean validaExerciciosLDO() {
        if (selecionado.getLdo() != null) {
            RenunciaReceitaLDO r = ((RenunciaReceitaLDO) selecionado);
            if (listarenunciaReceitaExercicio.size() < 3) {
                 FacesUtil.addOperacaoNaoPermitida("É necessário que os três próximos exercícios estejam cadastrados.");
                return false;
            }
        }
        return true;
    }
}
