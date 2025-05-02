/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FaixaAlvara;
import br.com.webpublico.entidades.ItemFaixaAlvara;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.FaixaAlvaraFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.ConverterExercicio;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz
 */
@ManagedBean(name = "faixaAlvaraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaFaixaAlvara", pattern = "/faixa-alvara/novo/", viewId = "/faces/tributario/alvara/faixaalvara/edita.xhtml"),
        @URLMapping(id = "editarFaixaAlvara", pattern = "/faixa-alvara/editar/#{faixaAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/faixaalvara/edita.xhtml"),
        @URLMapping(id = "duplicarFaixaAlvara", pattern = "/faixa-alvara/duplicar/#{faixaAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/faixaalvara/edita.xhtml"),
        @URLMapping(id = "listarFaixaAlvara", pattern = "/faixa-alvara/listar/", viewId = "/faces/tributario/alvara/faixaalvara/lista.xhtml"),
        @URLMapping(id = "verFaixaAlvara", pattern = "/faixa-alvara/ver/#{faixaAlvaraControlador.id}/", viewId = "/faces/tributario/alvara/faixaalvara/visualizar.xhtml")
})
public class FaixaAlvaraControlador extends PrettyControlador<FaixaAlvara> implements Serializable, CRUD {

    private ItemFaixaAlvara itemFaixaAlvara;
    @EJB
    private FaixaAlvaraFacade faixaAlvaraFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterExercicio converterExercicio;
    private boolean disableGrauDeRisco = Boolean.TRUE;

    public FaixaAlvaraControlador() {
        super(FaixaAlvara.class);
        this.itemFaixaAlvara = new ItemFaixaAlvara();
        itemFaixaAlvara.setGrauDeRisco(GrauDeRiscoDTO.BAIXO_RISCO_A);
    }

    @Override
    public AbstractFacade getFacede() {
        return faixaAlvaraFacade;
    }

    public ItemFaixaAlvara getItemFaixaAlvara() {
        return itemFaixaAlvara;
    }

    public void setItemFaixaAlvara(ItemFaixaAlvara itemFaixaAlvara) {
        this.itemFaixaAlvara = itemFaixaAlvara;
    }

    public boolean isDisableGrauDeRisco() {
        return disableGrauDeRisco;
    }

    public void setDisableGrauDeRisco(boolean disableGrauDeRisco) {
        this.disableGrauDeRisco = disableGrauDeRisco;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getListaExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Exercicio ex : exercicioFacade.listaExerciciosAtualFuturos()) {
            toReturn.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaClassificacaoAtividade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ClassificacaoAtividade object : ClassificacaoAtividade.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaGrauDeRisco() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (GrauDeRiscoDTO object : GrauDeRiscoDTO.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao().toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoAlvara() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAlvara object : TipoAlvara.values()) {
            if(!TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposLocalizacao() {
        return Util.getListSelectItem(TipoLocalizacao.values(), false);
    }

    public List<SelectItem> getTiposMateriaPrima() {
        return Util.getListSelectItem(TipoMateriaPrima.values(), false);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/faixa-alvara/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
    @URLAction(mappingId = "novaFaixaAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.itemFaixaAlvara = new ItemFaixaAlvara();
        itemFaixaAlvara.setGrauDeRisco(GrauDeRiscoDTO.BAIXO_RISCO_A);
    }

    @Override
    public void salvar() {
        if (validaFaixa()) {
           super.salvar();
        }
    }

    @Override
    @URLAction(mappingId = "verFaixaAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarFaixaAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    private boolean validaFaixa() {
        boolean retorno = Boolean.TRUE;
        if (selecionado.getExercicioVigencia() == null) {
            FacesUtil.addError("Impossível continuar", "Selecione o Exercício de Vigência");
            retorno = Boolean.FALSE;
        }
        if (selecionado.getClassificacaoAtividade() == null) {
            FacesUtil.addError("Impossível continuar", "Selecione a Classificação de Atividade");
            retorno = Boolean.FALSE;
        }
        if (selecionado.itemFaixaAlvaras.isEmpty()) {
            FacesUtil.addError("Impossível continuar", "Adicione pelo menos um Item");
            retorno = Boolean.FALSE;
        }
        return retorno;
    }

    @URLAction(mappingId = "duplicarFaixaAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        FaixaAlvara clone = new FaixaAlvara();
        clone.setClassificacaoAtividade(selecionado.getClassificacaoAtividade());
        clone.setExercicioVigencia(null);
        for (ItemFaixaAlvara item : selecionado.getItemFaixaAlvaras()) {
            ItemFaixaAlvara novoItem = new ItemFaixaAlvara();
            novoItem.setFaixaAlvara(clone);
            novoItem.setTipoAlvara(item.getTipoAlvara());
            novoItem.setAreaMetroInicial(item.getAreaMetroInicial());
            novoItem.setAreaMetroFinal(item.getAreaMetroFinal());
            novoItem.setValorTaxaUFMAno(item.getValorTaxaUFMAno());
            novoItem.setValorTaxaUFMMes(item.getValorTaxaUFMMes());
            novoItem.setGrauDeRisco(item.getGrauDeRisco());
            clone.getItemFaixaAlvaras().add(novoItem);
        }
        this.selecionado = null;
        this.selecionado = clone;
    }

    public void addItem() {
        if (validaItem()) {
            if (validaArea()) {
                itemFaixaAlvara.setFaixaAlvara(selecionado);
                selecionado.getItemFaixaAlvaras().add(itemFaixaAlvara);
                itemFaixaAlvara = new ItemFaixaAlvara();
                itemFaixaAlvara.setGrauDeRisco(GrauDeRiscoDTO.BAIXO_RISCO_A);
                disableGrauDeRisco = Boolean.TRUE;
            }
        }
    }

    private boolean validaArea() {
        boolean retorno = Boolean.TRUE;

        for (ItemFaixaAlvara it : selecionado.itemFaixaAlvaras) {
            if (((itemFaixaAlvara.getAreaMetroInicial().compareTo(it.getAreaMetroInicial()) >= 0 && itemFaixaAlvara.getAreaMetroInicial().compareTo(it.getAreaMetroFinal()) <= 0)
                    || (itemFaixaAlvara.getAreaMetroFinal().compareTo(it.getAreaMetroInicial()) >= 0 && itemFaixaAlvara.getAreaMetroFinal().compareTo(it.getAreaMetroFinal()) <= 0)
                    || (itemFaixaAlvara.getAreaMetroInicial().compareTo(it.getAreaMetroInicial()) <= 0 && itemFaixaAlvara.getAreaMetroFinal().compareTo(it.getAreaMetroFinal()) >= 0))
                    && (itemFaixaAlvara.getGrauDeRisco() == it.getGrauDeRisco() && itemFaixaAlvara.getTipoAlvara() == it.getTipoAlvara())) {
                FacesUtil.addError("Impossível continuar", "O intervalo de Áreas informado já contém em outro intervalo");
                retorno = Boolean.FALSE;
            }
        }

        return retorno;
    }

    private boolean validaItem() {
        boolean retorno = Boolean.TRUE;
        if (itemFaixaAlvara.getTipoAlvara() == null) {
            FacesUtil.addError("Campo Obrigatório","Informe o Tipo de Alvará");
            retorno = Boolean.FALSE;
        }
        if (itemFaixaAlvara.getAreaMetroInicial() == null) {
            FacesUtil.addError("Campo Obrigatório", "Informe a Área Inicial");
            retorno = Boolean.FALSE;
        } else if (itemFaixaAlvara.getAreaMetroInicial().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError("Atenção!", "Digite a Área Inicial Maior ou Igual a Zero");
            retorno = Boolean.FALSE;
        }
        if (itemFaixaAlvara.getAreaMetroFinal() == null) {
            FacesUtil.addError("Campo Obrigatório", "Informe a Área Final");
            retorno = Boolean.FALSE;
        } else if (itemFaixaAlvara.getAreaMetroFinal().compareTo(BigDecimal.ZERO) < 1) {
            FacesUtil.addError("Atenção!", "Informe a Área Final Maior que Zero");
            retorno = Boolean.FALSE;
        }
        if (itemFaixaAlvara.getAreaMetroInicial() != null && itemFaixaAlvara.getAreaMetroFinal() != null && itemFaixaAlvara.getAreaMetroInicial().compareTo(itemFaixaAlvara.getAreaMetroFinal()) > 0) {
            FacesUtil.addError("Atenção!", "Informe a Área Final Maior que a Área Inicial");
            retorno = Boolean.FALSE;
        }
        if (itemFaixaAlvara.getValorTaxaUFMAno() == null) {
            FacesUtil.addError("Campo Obrigatório", "Informe o Valor da Taxa em UFM");
            retorno = Boolean.FALSE;
        } else if (itemFaixaAlvara.getValorTaxaUFMAno().compareTo(BigDecimal.ZERO) < 1) {
            FacesUtil.addError("Atenção", "Informe o Valor da Taxa em UFM Maior que Zero");
            retorno = Boolean.FALSE;
        }
        if (itemFaixaAlvara.getGrauDeRisco() == null) {
            FacesUtil.addError("Impossível continuar", "Selecione o Grau de Risco");
            retorno = Boolean.FALSE;
        }
        return retorno;
    }

    public void removeItem(ItemFaixaAlvara item) {
        selecionado.getItemFaixaAlvaras().remove(item);
    }

    public void atualizaGrauDeRisco() {
        if (itemFaixaAlvara.getTipoAlvara().equals(TipoAlvara.SANITARIO) || itemFaixaAlvara.getTipoAlvara().equals(TipoAlvara.AMBIENTAL)) {
            disableGrauDeRisco = Boolean.FALSE;
        } else {
            disableGrauDeRisco = Boolean.TRUE;
        }
    }
}
