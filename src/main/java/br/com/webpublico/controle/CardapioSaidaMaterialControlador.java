package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cardapio;
import br.com.webpublico.entidades.CardapioSaidaMaterial;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CardapioSaidaMaterialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cardapio-saida", pattern = "/cardapio-saida-material/novo/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-saida-material/edita.xhtml"),
    @URLMapping(id = "editar-cardapio-saida", pattern = "/cardapio-saida-material/editar/#{cardapioSaidaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-saida-material/edita.xhtml"),
    @URLMapping(id = "listar-cardapio-saida", pattern = "/cardapio-saida-material/listar/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-saida-material/lista.xhtml"),
    @URLMapping(id = "ver-cardapio-saida", pattern = "/cardapio-saida-material/ver/#{cardapioSaidaMaterialControlador.id}/", viewId = "/faces/administrativo/materiais/alimentacao-escolar/cardapio-saida-material/visualizar.xhtml"),
})
public class CardapioSaidaMaterialControlador extends PrettyControlador<CardapioSaidaMaterial> implements Serializable, CRUD {

    @EJB
    private CardapioSaidaMaterialFacade facade;
    private Date dataInicial;
    private Date dataFinal;
    private String historico;
    private List<CardapioSaidaMaterialVO> locaisEstoques;

    public CardapioSaidaMaterialControlador() {
        super(CardapioSaidaMaterial.class);
    }

    @Override
    @URLAction(mappingId = "novo-cardapio-saida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-cardapio-saida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-cardapio-saida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cardapio-saida-material/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado = facade.salvarSaidaMaterial(selecionado, locaisEstoques);
            FacesUtil.addOperacaoRealizada("Saída Gerada com Sucesso.");
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void buscarMateriais() {
        try {
            Util.validarCampos(selecionado);
            locaisEstoques = facade.buscarLocaisEstoque(getCondicaoSql(), selecionado.getCardapio());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (Strings.isNullOrEmpty(historico)){
            ve.adicionarMensagemDeCampoObrigatorio("O campo histórico da saída deve ser informado.");
        }

        if (Util.isListNullOrEmpty(locaisEstoques)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, é necessário ter locais de estoque com materiais.");
        }
        ve.lancarException();
        boolean temQuantidade = false;
        for (CardapioSaidaMaterialVO le : locaisEstoques) {
            if (le.getItens().stream().anyMatch(CardapioSaidaMaterialItemVO::hasQuantidadeSaida)) {
                temQuantidade = true;
                break;
            }
        }
        if (!temQuantidade) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, é necessário ter locais de estoque com quantidade.");
        }
        ve.lancarException();
    }

    public void validarQuantidadeEstoque(CardapioSaidaMaterialItemVO item) {
        if (item.hasQuantidadeSaida() && item.getQuantidadeSaida().compareTo(item.getQuantidadeEstoque()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("A quantidade informada excede a quantidade disponível no estoque.");
            item.setQuantidadeSaida(BigDecimal.ZERO);
        }
    }

    public void limparDadosCardapio() {
        selecionado.setCardapio(null);
    }

    public void listenerCadarpio() {
        if (selecionado.getCardapio() != null) {
            setDataInicial(selecionado.getCardapio().getDataInicial());
            setDataFinal(selecionado.getCardapio().getDataFinal());
        }
    }

    public String getCondicaoSql() {
        String condicao = " and card.id = " + selecionado.getCardapio().getId();
        if (dataInicial != null) {
            condicao += " and trunc(card.datainicial) >= to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy')";
        }
        if (dataFinal != null) {
            condicao += " and trunc(card.datafinal) <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')";
        }
        return condicao;
    }

    public List<Cardapio> completarCardapio(String parte) {
        return facade.getCardapioFacade().buscarCardapio(parte.trim());
    }


    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<CardapioSaidaMaterialVO> getLocaisEstoques() {
        return locaisEstoques;
    }

    public void setLocaisEstoques(List<CardapioSaidaMaterialVO> locaisEstoques) {
        this.locaisEstoques = locaisEstoques;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
