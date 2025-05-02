/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ItemParametroDescontoRendaPatrimonial;
import br.com.webpublico.entidades.ParametroDescontoRendaPatrimonial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroDescontoRendaPatrimonialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;

@ManagedBean(name = "parametroDescontoRendaPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoDescontoRendasPatrimoniais", pattern = "/parametro-de-desconto-de-rendas-patrimoniais/novo/", viewId = "/faces/tributario/rendaspatrimoniais/parametrodescontorendapratrimonial/edita.xhtml"),
        @URLMapping(id = "editarDescontoRendasPatrimoniais", pattern = "/parametro-de-desconto-de-rendas-patrimoniais/editar/#{parametroDescontoRendaPatrimonialControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/parametrodescontorendapratrimonial/edita.xhtml"),
        @URLMapping(id = "listarDescontoRendasPatrimoniais", pattern = "/parametro-de-desconto-de-rendas-patrimoniais/listar/", viewId = "/faces/tributario/rendaspatrimoniais/parametrodescontorendapratrimonial/lista.xhtml"),
        @URLMapping(id = "verDescontoRendasPatrimoniais", pattern = "/parametro-de-desconto-de-rendas-patrimoniais/ver/#{parametroDescontoRendaPatrimonialControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/parametrodescontorendapratrimonial/visualizar.xhtml")
})
public class ParametroDescontoRendaPatrimonialControlador extends PrettyControlador<ParametroDescontoRendaPatrimonial> implements Serializable, CRUD {

    @EJB
    private ParametroDescontoRendaPatrimonialFacade parametroDescontoRendaPatrimonialFacade;
    private ItemParametroDescontoRendaPatrimonial itemParametroDescontoRendaPatrimonial;

    public ParametroDescontoRendaPatrimonialControlador() {
        super(ParametroDescontoRendaPatrimonial.class);
    }

    public ItemParametroDescontoRendaPatrimonial getItemParametroDescontoRendaPatrimonial() {
        return itemParametroDescontoRendaPatrimonial;
    }

    public void setItemParametroDescontoRendaPatrimonial(ItemParametroDescontoRendaPatrimonial itemParametroDescontoRendaPatrimonial) {
        this.itemParametroDescontoRendaPatrimonial = itemParametroDescontoRendaPatrimonial;
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroDescontoRendaPatrimonialFacade;
    }

    @URLAction(mappingId = "editarDescontoRendasPatrimoniais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        itemParametroDescontoRendaPatrimonial = new ItemParametroDescontoRendaPatrimonial();
        itemParametroDescontoRendaPatrimonial.setSequencia(parametroDescontoRendaPatrimonialFacade.sugereSequenciaItemParametroDescontoRendaPatrimonial(selecionado));
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-de-desconto-de-rendas-patrimoniais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoDescontoRendasPatrimoniais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(parametroDescontoRendaPatrimonialFacade.sugereCodigoParametroDescontoRendaPatrimonial());
        itemParametroDescontoRendaPatrimonial = new ItemParametroDescontoRendaPatrimonial();
        itemParametroDescontoRendaPatrimonial.setSequencia(parametroDescontoRendaPatrimonialFacade.sugereSequenciaItemParametroDescontoRendaPatrimonial(selecionado));
    }

    @URLAction(mappingId = "verDescontoRendasPatrimoniais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (selecionado.getId() == null) {
            selecionado.setCodigo(parametroDescontoRendaPatrimonialFacade.sugereCodigoParametroDescontoRendaPatrimonial());
        }
        super.salvar();
    }

    public void adicionaItem() {
        ParametroDescontoRendaPatrimonial p = getSelecionado();
        itemParametroDescontoRendaPatrimonial.setParametroDescontoRendaPatrimonial(p);
        if (validaItem(itemParametroDescontoRendaPatrimonial)) {
            p.getItensParametroDescontoRendaPatrimonial().add(itemParametroDescontoRendaPatrimonial);
            itemParametroDescontoRendaPatrimonial = new ItemParametroDescontoRendaPatrimonial();
            itemParametroDescontoRendaPatrimonial.setSequencia(parametroDescontoRendaPatrimonialFacade.sugereSequenciaItemParametroDescontoRendaPatrimonial(p));
        }
    }

    public void removeItem(ActionEvent e) {
        ItemParametroDescontoRendaPatrimonial item = (ItemParametroDescontoRendaPatrimonial) e.getComponent().getAttributes().get("objeto");
        ParametroDescontoRendaPatrimonial p = getSelecionado();
        item.setParametroDescontoRendaPatrimonial(null);
        p.getItensParametroDescontoRendaPatrimonial().remove(item);
        itemParametroDescontoRendaPatrimonial.setSequencia(parametroDescontoRendaPatrimonialFacade.sugereSequenciaItemParametroDescontoRendaPatrimonial(p));
    }

    private boolean validaItem(ItemParametroDescontoRendaPatrimonial item) {
        boolean valida = true;
        if (item.getValorInicial() == null) {
            FacesUtil.addError("Atenção!", "Informe o valor inicial.");
            valida = false;
        }
        if (item.getValorFinal() == null) {
            FacesUtil.addError("Atenção!", "Informe o valor final.");
            valida = false;
        }
        if ((item.getValorInicial() != null) && (item.getValorFinal() != null)) {
            if (item.getValorInicial().compareTo(item.getValorFinal()) == 1) {
                FacesUtil.addError("Atenção!", "O valor final deve ser maior que o valor inicial.");
                valida = false;
            } else {
                for (ItemParametroDescontoRendaPatrimonial i : item.getParametroDescontoRendaPatrimonial().getItensParametroDescontoRendaPatrimonial()) {
                    if ((item.getValorInicial().compareTo(i.getValorInicial()) >= 0) && (item.getValorInicial().compareTo(i.getValorFinal()) <= 0)) {
                        FacesUtil.addError("Atenção!", "O valor inicial deve estar fora de uma faixa já cadastrada.");
                        valida = false;
                        break;
                    }
                    if ((item.getValorFinal().compareTo(i.getValorInicial()) >= 0) && (item.getValorFinal().compareTo(i.getValorFinal()) <= 0)) {
                        FacesUtil.addError("Atenção!", "O valor final deve estar fora de uma faixa já cadastrada.");
                        valida = false;
                        break;
                    }
                    if ((item.getValorInicial().compareTo(i.getValorInicial()) >= 0) && (item.getValorFinal().compareTo(i.getValorFinal()) <= 0)) {
                        FacesUtil.addError("Atenção!", "A faixa de valor deve estar fora de uma faixa já cadastrada.");
                        valida = false;
                        break;
                    }
                    if ((i.getValorInicial().compareTo(item.getValorInicial()) >= 0) && (i.getValorFinal().compareTo(item.getValorFinal()) <= 0)) {
                        FacesUtil.addError("Atenção!", "A faixa de valor deve estar fora de uma faixa já cadastrada.");
                        valida = false;
                        break;
                    }
                }
            }
        }

        if (item.getQuantidadeParcelas() == null) {
            FacesUtil.addError("Atenção!", "Informe a quantidade de parcelas.");
            valida = false;
        } else if (item.getQuantidadeParcelas().intValue() <= 0) {
            FacesUtil.addError("Atenção!", "A quantidade de parcelas deve ser maior que zero.");
            valida = false;
        }
        if (item.getDescontoNaDivida() == null) {
            FacesUtil.addError("Atenção!", "Informe o percentual de desconto na dívida.");
            valida = false;
        } else if (item.getDescontoNaDivida().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError("Atenção!", "O percentual de desconto na dívida deve ser igual ou maior que zero.");
            valida = false;
        }
        if (item.getDescontoNaMulta() == null) {
            FacesUtil.addError("Atenção!", "Informe o percentual de desconto na multa.");
            valida = false;
        } else if (item.getDescontoNaMulta().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError("Atenção!", "O percentual de Desconto na multa deve ser igual ou maior que zero.");
            valida = false;
        }
        if (item.getDescontoNosJuros() == null) {
            FacesUtil.addError("Atenção!", "Informe o  percentual de desconto nos juros.");
            valida = false;
        } else if (item.getDescontoNosJuros().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addError("Atenção!", "O percentual de desconto nos juros deve ser igual ou maior que zero.");
            valida = false;
        }
        return valida;
    }
}
