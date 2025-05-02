package br.com.webpublico.controle.rh.parametro.cargocomissaofuncaogratificada;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFG;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFGFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 27/03/2019.
 */
@ManagedBean(name = "parametroControleCargoFGControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-parametro-controle-cargo-fg", pattern = "/parametro-controle-cargo-funcao-gratificada/novo/", viewId = "/faces/rh/administracaodepagamento/parametro-controle-cargo-fg/edita.xhtml"),
    @URLMapping(id = "ver-parametro-controle-cargo-fg", pattern = "/parametro-controle-cargo-funcao-gratificada/ver/#{parametroControleCargoFGControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametro-controle-cargo-fg/visualizar.xhtml"),
    @URLMapping(id = "listar-parametro-controle-cargo-fg", pattern = "/parametro-controle-cargo-funcao-gratificada/listar/", viewId = "/faces/rh/administracaodepagamento/parametro-controle-cargo-fg/lista.xhtml"),
    @URLMapping(id = "editar-parametro-controle-cargo-fg", pattern = "/parametro-controle-cargo-funcao-gratificada/editar/#{parametroControleCargoFGControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametro-controle-cargo-fg/edita.xhtml")
})
public class ParametroControleCargoFGControlador extends PrettyControlador<ParametroControleCargoFG> implements CRUD, Serializable {

    @EJB
    private ParametroControleCargoFGFacade parametroControleCargoFGFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    public ParametroControleCargoFGControlador() {
        super(ParametroControleCargoFG.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroControleCargoFGFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-controle-cargo-funcao-gratificada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-parametro-controle-cargo-fg", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-parametro-controle-cargo-fg", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-parametro-controle-cargo-fg", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<Entidade> completarEntidade(String parte) {
        return entidadeFacade.listaEntidades(parte.trim());
    }

    private void validarQuantidadeMaximaOuValorMaximo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getQuantidadeFuncaoGratificada() != null && selecionado.getQuantidadeFuncaoGratificada() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade Servidores Função Gratificada deve ser maior que zero");
        }
        if (selecionado.getQuantidadeCargoComissao() != null && selecionado.getQuantidadeCargoComissao() < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade Servidores Cargo Comissão deve ser maior que zero");
        }
        if (selecionado.getValorCargoComissao() != null && selecionado.getValorCargoComissao().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor Cargo em Comissão deve ser maior que zero");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarQuantidadeMaximaOuValorMaximo();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
