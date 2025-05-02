package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgendamentoViagemSaud;
import br.com.webpublico.entidades.ControleViagemSaud;
import br.com.webpublico.entidades.MotoristaSaud;
import br.com.webpublico.entidades.VeiculoSaud;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ControleViagemSaudFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarControleViagemSaud",
        pattern = "/tributario/saud/controle-viagem-saud/listar/",
        viewId = "/faces/tributario/saud/controleviagemsaud/lista.xhtml"),
    @URLMapping(id = "novoControleViagemSaud",
        pattern = "/tributario/saud/controle-viagem-saud/novo/",
        viewId = "/faces/tributario/saud/controleviagemsaud/edita.xhtml"),
    @URLMapping(id = "editarControleViagemSaud",
        pattern = "/tributario/saud/controle-viagem-saud/editar/#{controleViagemSaudControlador.id}/",
        viewId = "/faces/tributario/saud/controleviagemsaud/edita.xhtml"),
    @URLMapping(id = "verControleViagemSaud",
        pattern = "/tributario/saud/controle-viagem-saud/ver/#{controleViagemSaudControlador.id}/",
        viewId = "/faces/tributario/saud/controleviagemsaud/visualizar.xhtml")
})
public class ControleViagemSaudControlador extends PrettyControlador<ControleViagemSaud> implements Serializable, CRUD {

    @EJB
    private ControleViagemSaudFacade facade;
    private List<VeiculoSaud> veiculosAtivosMotorista;

    public ControleViagemSaudControlador() {
        super(ControleViagemSaud.class);
    }

    @Override
    public ControleViagemSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/controle-viagem-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoControleViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarControleViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscarVeiculosAtivosDoMotorista();
    }

    @URLAction(mappingId = "verControleViagemSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        buscarVeiculosAtivosDoMotorista();
    }

    public List<VeiculoSaud> getVeiculosAtivosMotorista() {
        if (veiculosAtivosMotorista == null) {
            veiculosAtivosMotorista = Lists.newArrayList();
        }
        return veiculosAtivosMotorista;
    }

    public void setVeiculosAtivosMotorista(List<VeiculoSaud> veiculosAtivosMotorista) {
        this.veiculosAtivosMotorista = veiculosAtivosMotorista;
    }

    public List<AgendamentoViagemSaud> completarAgendamentos(String parte) {
        return facade.getAgendamentoViagemSaudFacade().buscarFiltrando(parte);
    }

    public List<MotoristaSaud> completarMotoristasSAUD(String parte) {
        return facade.getMotoristaSaudFacade().buscarFiltrando(parte);
    }

    public void buscarVeiculosAtivosDoMotorista() {
        if (selecionado.getMotoristaSaud() != null) {
            getVeiculosAtivosMotorista().clear();
            getVeiculosAtivosMotorista().addAll(facade.getVeiculoSaudFacade().buscarVeiculosDoMotorista(selecionado.getMotoristaSaud().getId(), true));
        }
    }

    public String formatarHoraMinuto(Integer hh, Integer mm) {
        return DataUtil.formatarHoraMinuto(hh, mm);
    }

    public void verificarVeiculosMotorista() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getMotoristaSaud() != null) {
                buscarVeiculosAtivosDoMotorista();
                List<VeiculoSaud> todosVeiculos = facade.getVeiculoSaudFacade().buscarVeiculosDoMotorista(selecionado.getMotoristaSaud().getId(), false);
                if (todosVeiculos.isEmpty()) {
                    ve.adicionarMensagemWarn("Atenção", "O motorista selecionado não possui veiculos cadastrados.");
                } else if (getVeiculosAtivosMotorista().isEmpty()) {
                    ve.adicionarMensagemWarn("Atenção", "O motorista selecionado não possui veiculos ativos, Selecione um motorista que possua veiculo ativo.");
                }
                ve.lancarException();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
            selecionado.setMotoristaSaud(null);
        }
    }

    @Override
    public void salvar() {
        if (selecionado.getCodigo() == null) {
            selecionado.setCodigo(facade.proximoCodigoAgendamento());
        }
        super.salvar();
    }
}
