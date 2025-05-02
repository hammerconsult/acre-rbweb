/*
 * Codigo gerado automaticamente em Mon Oct 17 17:36:16 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.JulgamentoMultaVeiculo;
import br.com.webpublico.entidades.MultaVeiculo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MultaVeiculoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "multaVeiculoNovo", pattern = "/frota/multa-veiculo/novo/", viewId = "/faces/administrativo/frota/multaveiculo/edita.xhtml"),
        @URLMapping(id = "multaVeiculoListar", pattern = "/frota/multa-veiculo/listar/", viewId = "/faces/administrativo/frota/multaveiculo/lista.xhtml"),
        @URLMapping(id = "multaVeiculoEditar", pattern = "/frota/multa-veiculo/editar/#{multaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/multaveiculo/edita.xhtml"),
        @URLMapping(id = "multaVeiculoVer", pattern = "/frota/multa-veiculo/ver/#{multaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/multaveiculo/visualizar.xhtml"),
})
public class MultaVeiculoControlador extends PrettyControlador<MultaVeiculo> implements Serializable, CRUD {

    @EJB
    private MultaVeiculoFacade multaVeiculoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private JulgamentoMultaVeiculo julgamentoMultaVeiculo;

    public MultaVeiculoControlador() {
        super(MultaVeiculo.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return multaVeiculoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/multa-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "multaVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarEntidade();
        iniciarAtributos();
    }

    @URLAction(mappingId = "multaVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        iniciarAtributos();
    }

    @URLAction(mappingId = "multaVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        iniciarAtributos();
    }

    public void inicializarEntidade() {
        selecionado.setValor(BigDecimal.ZERO);
    }

    private void iniciarAtributos() {
        julgamentoMultaVeiculo = new JulgamentoMultaVeiculo();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("O valor deve ser maior que 0(zero).");
        }
        if (selecionado.getTeveContestacao() != null && selecionado.getTeveContestacao().equals(Boolean.TRUE) &&
                selecionado.getDataContestacao() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("A Data da Contestação deve ser informada.");
        }
        if(selecionado.getEmitidaEm().compareTo(selecionado.getVeiculo().getDataAquisicao()) < 0){
            retorno = false;
            FacesUtil.addCampoObrigatorio("A Data de Emissão não pode ser inferior a Data de Aquisição do Veículo.");
        }
        return retorno;
    }

    public void novoVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/frota/veiculo/novo/", selecionado);
    }

    public void novoTipoDeMulta() {
        Web.navegacao(getCaminhoOrigem(), "/frota/tipo-de-multa/novo/", selecionado);
    }

    public void processaMudancaContestacao() {
        selecionado.setDataContestacao(null);
    }

    public JulgamentoMultaVeiculo getJulgamentoMultaVeiculo() {
        return julgamentoMultaVeiculo;
    }

    public void setJulgamentoMultaVeiculo(JulgamentoMultaVeiculo julgamentoMultaVeiculo) {
        this.julgamentoMultaVeiculo = julgamentoMultaVeiculo;
    }

    public List<SelectItem> listaResultadoJulgamento() {
        return Util.getListSelectItem(Arrays.asList(JulgamentoMultaVeiculo.ResultadoJulgamento.values()));
    }

    public void selecionouHouveRessarcimento() {
        julgamentoMultaVeiculo.setNumeroDam(null);
        julgamentoMultaVeiculo.setValor(null);
    }

    public boolean validaDadosJulgamento(JulgamentoMultaVeiculo julgamentoMultaVeiculo) {
        boolean retorno = true;
        if (!Util.validaCampos(julgamentoMultaVeiculo)) {
            retorno = false;
        }
        if (julgamentoMultaVeiculo.getHouveRessarcimento() &&
                julgamentoMultaVeiculo.getNumeroDam().trim().isEmpty()) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("O campo Numero do Dam deve ser informado.");
        }
        if (julgamentoMultaVeiculo.getHouveRessarcimento() &&
                (julgamentoMultaVeiculo.getValor() == null ||
                        julgamentoMultaVeiculo.getValor().compareTo(BigDecimal.ZERO) <= 0)) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("O campo Valor deve ser informado com um valor maior que 0(zero).");
        }
        return retorno;
    }

    public void confirmarJulgamento() {
        if (!validaDadosJulgamento(julgamentoMultaVeiculo)) {
            return;
        }
        julgamentoMultaVeiculo.setMultaVeiculo(selecionado);
        if (selecionado.getJulgamentosMultaVeiculo() == null) {
            selecionado.setJulgamentosMultaVeiculo(new ArrayList());
        }
        selecionado.getJulgamentosMultaVeiculo().add(julgamentoMultaVeiculo);
        julgamentoMultaVeiculo = new JulgamentoMultaVeiculo();
    }

    public void removerJulgamento(JulgamentoMultaVeiculo julgamentoMultaVeiculo) {
        selecionado.getJulgamentosMultaVeiculo().remove(julgamentoMultaVeiculo);
    }
}
