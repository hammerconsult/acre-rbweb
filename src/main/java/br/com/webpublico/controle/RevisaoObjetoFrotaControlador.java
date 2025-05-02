/*
 * Codigo gerado automaticamente em Thu Nov 03 15:57:46 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RevisaoObjetoFrotaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "revisaoVeiculoNovo", pattern = "/frota/revisao-veiculo/novo/", viewId = "/faces/administrativo/frota/revisao/edita.xhtml"),
    @URLMapping(id = "revisaoVeiculoListar", pattern = "/frota/revisao-veiculo/listar/", viewId = "/faces/administrativo/frota/revisao/lista.xhtml"),
    @URLMapping(id = "revisaoVeiculoEditar", pattern = "/frota/revisao-veiculo/editar/#{revisaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/revisao/edita.xhtml"),
    @URLMapping(id = "revisaoVeiculoVer", pattern = "/frota/revisao-veiculo/ver/#{revisaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/revisao/visualizar.xhtml"),
})
public class RevisaoObjetoFrotaControlador extends PrettyControlador<RevisaoObjetoFrota> implements Serializable, CRUD {

    @EJB
    private RevisaoObjetoFrotaFacade facade;

    public RevisaoObjetoFrotaControlador() {
        super(RevisaoObjetoFrota.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/revisao-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "revisaoVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setKmProgramada(0);
        selecionado.setKmRealizada(0);
    }

    @URLAction(mappingId = "revisaoVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "revisaoVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void novoVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/frota/veiculo/novo/", selecionado);
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatorios();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCamposObrigatorios() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoObjetoFrota().isVeiculo()) {
            if (selecionado.getKmRealizada() == null || selecionado.getKmRealizada() <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Km Realizada deve ser informado e deve ser maior que zero(0).");
            }
            if (selecionado.getKmProgramada() != null && selecionado.getKmProgramada() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Km Programada deve ser maior que zero(0).");
            }
            if (selecionado.getProgramaRevisaoVeiculo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Programa de Revisão deve ser informado.");
            }
        }
        if (selecionado.getRealizadoEm().compareTo(selecionado.getObjetoFrota().getDataAquisicao()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Realização da Revisão não pode ser inferior a Data de Aquisição do Veículo ou Equipamento/Máquina.");
        }
        ve.lancarException();
    }

    public List<ManutencaoObjetoFrota> completaManutencaoParaRevisao(String parte) {
        return facade.getManutencaoObjetoFrotaFacade().listarManutencao(selecionado.getObjetoFrota(), parte.trim());
    }

    public List<ProgramaRevisaoVeiculo> completarProgramaRevisaoVeiculo(String parte) {
        try {
            if (selecionado.getObjetoFrota() != null && selecionado.getTipoObjetoFrota().isVeiculo()) {
                List<ProgramaRevisaoVeiculo> programas = facade.getProgramaRevisaoVeiculoFacade().buscarProgramaRevisaoNaoRealizadosPorVeiculo(selecionado.getObjetoFrota().getVeiculo(), parte.trim());
                validarProgramaRevisaoVeiculo(programas, selecionado.getObjetoFrota().getVeiculo());
                return programas;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
        return new ArrayList<>();
    }

    public List<ProgramaRevisaoEquipamento> completarProgramaRevisaoEquipamento(String parte) {
        try {
            if (selecionado.getObjetoFrota() != null && selecionado.getTipoObjetoFrota().isEquipamento()) {
                List<ProgramaRevisaoEquipamento> programas = facade.getProgramaRevisaoEquipamentoFacade().buscarProgramaRevisaoNaoRealizadosPorEquipamento(selecionado.getObjetoFrota().getEquipamento(), parte.trim());
                validarProgramaRevisaoEquipamento(programas, selecionado.getObjetoFrota().getEquipamento());
                return programas;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
        return new ArrayList<>();
    }

    private void validarProgramaRevisaoVeiculo(List<ProgramaRevisaoVeiculo> programas, Veiculo veiculo) {
        ValidacaoException ve = new ValidacaoException();
        if (programas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum programa de revisão encontrado para o veículo: " + veiculo + ".");
        }
        ve.lancarException();
    }

    private void validarProgramaRevisaoEquipamento(List<ProgramaRevisaoEquipamento> programas, Equipamento equipamento) {
        ValidacaoException ve = new ValidacaoException();
        if (programas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum programa de revisão encontrado para o equipamento: " + equipamento + ".");
        }
        ve.lancarException();
    }

    public boolean renderizarProgramaRevisaoVeiculo() {
        return selecionado.getTipoObjetoFrota() != null && selecionado.getTipoObjetoFrota().isVeiculo();
    }

    public boolean renderizarProgramaRevisaoEquipamento() {
        return selecionado.getTipoObjetoFrota() != null && selecionado.getTipoObjetoFrota().isEquipamento();
    }

    public void processaMudancaTipoObjetoFrota() {
        selecionado.setObjetoFrota(null);
        selecionado.setKmProgramada(null);
        selecionado.setKmRealizada(null);
        selecionado.setManutencaoObjetoFrota(null);
    }

    public void novaManutencao() {
        Web.navegacao(getCaminhoOrigem(), "/frota/manutencao/novo/", selecionado);
    }

    public List<Contrato> completarContratoUnidadeLogada(String filtro) {
        return facade.getContratoFacade().listaFiltrandoContrato(filtro, Util.getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
    }
}
