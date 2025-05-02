/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemParametroTipoDividaDiv;
import br.com.webpublico.entidades.ParametroTipoDividaDiversa;
import br.com.webpublico.entidades.TipoDividaDiversa;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroTipoDividaDiversaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author claudio
 */
@ManagedBean(name = "parametroTipoDividaDiversaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroTipoDividasDiversas", pattern = "/parametro-de-tipo-de-dividas-diversas/novo/",
        viewId = "/faces/tributario/taxasdividasdiversas/parametrotipodividasdiversas/edita.xhtml"),
    @URLMapping(id = "editarParametroTipoDividasDiversas", pattern = "/parametro-de-tipo-de-dividas-diversas/editar/#{parametroTipoDividaDiversaControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/parametrotipodividasdiversas/edita.xhtml"),
    @URLMapping(id = "listarParametroTipoDividasDiversas", pattern = "/parametro-de-tipo-de-dividas-diversas/listar/",
        viewId = "/faces/tributario/taxasdividasdiversas/parametrotipodividasdiversas/lista.xhtml"),
    @URLMapping(id = "verParametroTipoDividasDiversas", pattern = "/parametro-de-tipo-de-dividas-diversas/ver/#{parametroTipoDividaDiversaControlador.id}/",
        viewId = "/faces/tributario/taxasdividasdiversas/parametrotipodividasdiversas/visualizar.xhtml")
})
public class ParametroTipoDividaDiversaControlador extends PrettyControlador<ParametroTipoDividaDiversa> implements Serializable, CRUD {

    @EJB
    private ParametroTipoDividaDiversaFacade parametroTipoDividaDiversaFacade;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete converterTipoDividaDiversa;
    private ItemParametroTipoDividaDiv itemParametroTipoDividaDiv;
    private final Integer QUANTIDADE_MINIMA_PARCELA = 1;

    public ItemParametroTipoDividaDiv getItemParametroTipoDividaDiv() {
        return itemParametroTipoDividaDiv;
    }

    public void setItemParametroTipoDividaDiv(ItemParametroTipoDividaDiv itemParametroTipoDividaDiv) {
        this.itemParametroTipoDividaDiv = itemParametroTipoDividaDiv;
    }

    public void setConverterTipoDividaDiversa(ConverterAutoComplete converterTipoDividaDiversa) {
        this.converterTipoDividaDiversa = converterTipoDividaDiversa;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-de-tipo-de-dividas-diversas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroTipoDividaDiversaFacade;
    }

    //---------------------------- Novo -----------------------------------------------
    @URLAction(mappingId = "novoParametroTipoDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        itemParametroTipoDividaDiv = new ItemParametroTipoDividaDiv();
    }

    //---------------------------- Editar ---------------------------------------------
    @URLAction(mappingId = "editarParametroTipoDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    //---------------------------- ver -----------------------------------------------
    @URLAction(mappingId = "verParametroTipoDividasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            if (validaCampos()) {
                parametroTipoDividaDiversaFacade.salvar(selecionado);
                selecionado = new ParametroTipoDividaDiversa();
                itemParametroTipoDividaDiv = new ItemParametroTipoDividaDiv();
                FacesUtil.addInfo("Salvo com Sucesso!", "");
                redireciona();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getExercicio() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Exercício.");
        }
        if (selecionado.getTipoDividaDiversa() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Tipo de Dívidas Diversas.");
        }
        if (selecionado.getItensParametro() == null || selecionado.getItensParametro().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Adicione ao menos uma faixa de valor na parametrização.");
        }
        if (parametroTipoDividaDiversaFacade.existeCombinacaoIgualDoParametro(selecionado)) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Já existe um registro com o Exercício e Tipo de Dívidas Diversas cadastrado.");
        }
        return retorno;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return parametroTipoDividaDiversaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(parametroTipoDividaDiversaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<TipoDividaDiversa> completaTipoDividaDiversa(String parte) {
        return parametroTipoDividaDiversaFacade.getTipoDividaDiversaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterTipoDividaDiversa() {
        if (converterTipoDividaDiversa == null) {
            converterTipoDividaDiversa = new ConverterAutoComplete(TipoDividaDiversa.class, parametroTipoDividaDiversaFacade.getTipoDividaDiversaFacade());
        }
        return converterTipoDividaDiversa;
    }

    public void adicionaItemParametro() {
        if (validaCamposItemParametro()) {
            itemParametroTipoDividaDiv.setParametroTipoDividaDiversa(selecionado);
            selecionado.getItensParametro().add(itemParametroTipoDividaDiv);
            itemParametroTipoDividaDiv = new ItemParametroTipoDividaDiv();
        }
    }

    public void removeItemParametro(ItemParametroTipoDividaDiv item) {
        selecionado.getItensParametro().remove(item);
    }

    private boolean validaCamposItemParametro() {
        boolean retorno = true;

        retorno = isValorInicialValido(retorno);
        retorno = isValorFinalValido(retorno);
        retorno = isFaixaValoresParametroValido(retorno);
        retorno = isQuantidadeParcelaValida(retorno);
        retorno = isDescontoMultaValido(retorno);
        retorno = isDescontoJurosValido(retorno);
        retorno = isDescontoCorrecaoValido(retorno);

        return retorno;
    }

    private boolean isFaixaValoresParametroValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getValorInicial() != null && itemParametroTipoDividaDiv.getValorFinal() != null
            && itemParametroTipoDividaDiv.getValorInicial().compareTo(itemParametroTipoDividaDiv.getValorFinal()) > 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "O Valor Final deve ser maior que o Valor Inicial.");
        } else if (valoresJaParametrizados()) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Os valores informados já estão contidos em outra faixa de parametrização.");
        }
        return retorno;
    }

    private boolean isValorInicialValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getValorInicial() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Valor Inicial.");
        } else if (itemParametroTipoDividaDiv.getValorInicial().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "O Valor Inicial não pode ser igual ou inferior a zero.");
        }
        return retorno;
    }

    private boolean isValorFinalValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getValorFinal() == null) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o Valor Final.");
        } else if (itemParametroTipoDividaDiv.getValorFinal().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "O Valor Final não pode ser igual ou inferior a zero.");
        }
        return retorno;
    }

    private boolean isQuantidadeParcelaValida(boolean retorno) {
        if (itemParametroTipoDividaDiv.getQuantidadeMaximaParcela() == null
            || itemParametroTipoDividaDiv.getQuantidadeMaximaParcela().compareTo(QUANTIDADE_MINIMA_PARCELA) < 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe a quantidade de parcelas. Mínimo 1 (uma).");
        }
        return retorno;
    }

    private boolean isDescontoMultaValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getPercentualDescontoMulta() == null) {
            itemParametroTipoDividaDiv.setPercentualDescontoMulta(BigDecimal.ZERO);
        } else if (itemParametroTipoDividaDiv.getPercentualDescontoMulta().compareTo(BigDecimal.ZERO) < 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o percentual de desconto sobre a multa, não devendo ser igual ou inferior a zero.");
        }
        return retorno;
    }

    private boolean isDescontoJurosValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getPercentualDescontoJuros() == null) {
            itemParametroTipoDividaDiv.setPercentualDescontoJuros(BigDecimal.ZERO);
        } else if (itemParametroTipoDividaDiv.getPercentualDescontoJuros().compareTo(BigDecimal.ZERO) < 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o percentual de desconto sobre os juros, não devendo ser igual ou inferior zero.");
        }
        return retorno;
    }

    private boolean isDescontoCorrecaoValido(boolean retorno) {
        if (itemParametroTipoDividaDiv.getPercentualDescontoCorrecao() == null) {
            itemParametroTipoDividaDiv.setPercentualDescontoCorrecao(BigDecimal.ZERO);
        } else if (itemParametroTipoDividaDiv.getPercentualDescontoCorrecao().compareTo(BigDecimal.ZERO) < 0) {
            retorno = false;
            FacesUtil.addError("Não foi possível continuar!", "Informe o percentual de desconto sobre a correção, não devendo ser igual ou inferior a zero.");
        }
        return retorno;
    }

    private boolean valoresJaParametrizados() {
        boolean retorno = false;
        for (ItemParametroTipoDividaDiv item : selecionado.getItensParametro()) {
            retorno = valorInicialEntreFaixaDeValores(item, retorno);
            retorno = valorFinalEntreFaixaDeValores(item, retorno);
            retorno = valorInicialEFinalEntreFaixaDeValores(item, retorno);
        }
        return retorno;
    }

    private boolean valorInicialEntreFaixaDeValores(ItemParametroTipoDividaDiv item, boolean retorno) {
        if (item.getValorInicial().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorInicial().setScale(2, RoundingMode.HALF_EVEN)) >= 0
            && item.getValorInicial().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorFinal().setScale(2, RoundingMode.HALF_EVEN)) <= 0) {
            retorno = true;
        }
        return retorno;
    }

    private boolean valorFinalEntreFaixaDeValores(ItemParametroTipoDividaDiv item, boolean retorno) {
        if (item.getValorFinal().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorInicial().setScale(2, RoundingMode.HALF_EVEN)) >= 0
            && item.getValorFinal().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorFinal().setScale(2, RoundingMode.HALF_EVEN)) <= 0) {
            retorno = true;
        }
        return retorno;
    }

    private boolean valorInicialEFinalEntreFaixaDeValores(ItemParametroTipoDividaDiv item, boolean retorno) {
        if (item.getValorInicial().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorInicial().setScale(2, RoundingMode.HALF_EVEN)) <= 0
            && item.getValorFinal().setScale(2, RoundingMode.HALF_EVEN).compareTo(itemParametroTipoDividaDiv.getValorFinal().setScale(2, RoundingMode.HALF_EVEN)) >= 0) {
            retorno = true;
        }
        return retorno;
    }

    public ParametroTipoDividaDiversaControlador() {
        super(ParametroTipoDividaDiversa.class);
        itemParametroTipoDividaDiv = new ItemParametroTipoDividaDiv();
    }
}
