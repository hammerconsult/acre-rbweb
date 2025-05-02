/*
 * Codigo gerado automaticamente em Mon Nov 14 00:20:54 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbastecimentoObjetoFrotaFacade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ManutencaoObjetoFrotaFacade;
import br.com.webpublico.negocios.ViagemFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "viagemVeiculoNovo", pattern = "/frota/viagem-veiculo/novo/", viewId = "/faces/administrativo/frota/viagens/edita.xhtml"),
    @URLMapping(id = "viagemVeiculoListar", pattern = "/frota/viagem-veiculo/listar/", viewId = "/faces/administrativo/frota/viagens/lista.xhtml"),
    @URLMapping(id = "viagemVeiculoEditar", pattern = "/frota/viagem-veiculo/editar/#{viagemControlador.id}/", viewId = "/faces/administrativo/frota/viagens/edita.xhtml"),
    @URLMapping(id = "viagemVeiculoVer", pattern = "/frota/viagem-veiculo/ver/#{viagemControlador.id}/", viewId = "/faces/administrativo/frota/viagens/visualizar.xhtml"),
})
public class ViagemControlador extends PrettyControlador<Viagem> implements Serializable, CRUD {

    @EJB
    private ViagemFacade viagemFacade;
    @EJB
    private ManutencaoObjetoFrotaFacade manutencaoObjetoFrotaFacade;
    private ItinerarioViagem itinerarioViagem;
    private ViagemManutencaoVeiculo viagemManutencaoVeiculo;
    private ViagemAbastecimento viagemAbastecimento;
    @EJB
    private AbastecimentoObjetoFrotaFacade abastecimentoObjetoFrotaFacade;

    public ViagemControlador() {
        super(Viagem.class);
    }


    private void inicializarAtributosDaTela() {
        inicializarItinerario();
        inicializarViagemManutencaoVeiculo();
        inicializarViagemAbastecimento();
    }

    private void inicializarItinerario() {
        itinerarioViagem = new ItinerarioViagem();
    }

    private void inicializarViagemManutencaoVeiculo() {
        viagemManutencaoVeiculo = new ViagemManutencaoVeiculo();
        viagemManutencaoVeiculo.setViagem(selecionado);
    }

    public void inicializarViagemAbastecimento() {
        viagemAbastecimento = new ViagemAbastecimento();
    }

    @Override
    public AbstractFacade getFacede() {
        return viagemFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/viagem-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "viagemVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "viagemVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarAtributosDaTela();
    }

    @URLAction(mappingId = "viagemVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributosDaTela();
    }

    public void novoVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/frota/veiculo/novo/", selecionado);
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validaRegrasEspecificas();
            selecionado = viagemFacade.salvarViagem(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }


    public boolean desabilitaSelecaoDoVeiculo() {
        return (selecionado.getManutencoesVeiculo() != null &&
            selecionado.getManutencoesVeiculo().size() > 0) ||
            (selecionado.getAbastecimentos() != null &&
                selecionado.getAbastecimentos().size() > 0);
    }

    //----METODOS DO ITINERARIO DA VIAGEM
    public ItinerarioViagem getItinerarioViagem() {
        return itinerarioViagem;
    }

    public void setItinerarioViagem(ItinerarioViagem itinerarioViagem) {
        this.itinerarioViagem = itinerarioViagem;
    }

    private boolean existeCidadeNoItinerario(ItinerarioViagem itinerarioViagem) {
        if (selecionado.getItinerarioViagem() != null && selecionado.getItinerarioViagem().size() > 0 &&
            itinerarioViagem.getCidade() != null) {
            for (ItinerarioViagem item : selecionado.getItinerarioViagem()) {
                if (!item.equals(itinerarioViagem) && item.getCidade().getId().equals(itinerarioViagem.getCidade().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int buscarNovaOrdemItinerario() {
        if (selecionado != null && selecionado.getItinerarioViagem() != null && selecionado.getItinerarioViagem().size() > 0) {
            return selecionado.getItinerarioViagem().get(selecionado.getItinerarioViagem().size() - 1).getOrdem() + 1;
        }
        return 1;
    }

    private boolean validaInformacoesItinerario() {
        boolean valida = true;
        if (itinerarioViagem.getCidade() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A cidade deve ser informada.'");
        }
        if (existeCidadeNoItinerario(itinerarioViagem)) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A cidade selecionada já encontra-se no itinerário.");
        }
        return valida;
    }

    public void confirmarManutencaoItinerario() {
        if (validaInformacoesItinerario()) {
            if (selecionado.getItinerarioViagem() == null) {
                selecionado.setItinerarioViagem(new ArrayList());
            }
            itinerarioViagem.setViagem(selecionado);
            itinerarioViagem.setOrdem(buscarNovaOrdemItinerario());
            selecionado.getItinerarioViagem().add(itinerarioViagem);
            inicializarItinerario();
        }

    }

    public void excluirItinerario(ItinerarioViagem itinerarioViagem) {
        Integer ordemRemovida = itinerarioViagem.getOrdem();
        selecionado.getItinerarioViagem().remove(itinerarioViagem);
        for (ItinerarioViagem i : selecionado.getItinerarioViagem()) {
            if (i.getOrdem().compareTo(ordemRemovida) > 0) {
                i.setOrdem(i.getOrdem() - 1);
            }
        }
    }

    public void subirOrdemItinerario(ItinerarioViagem itinerarioViagem) {
        int indexObjSelecionado = selecionado.getItinerarioViagem().indexOf(itinerarioViagem);
        if (indexObjSelecionado > 0) {
            Integer ordemSuperior = selecionado.getItinerarioViagem().get(indexObjSelecionado - 1).getOrdem();
            selecionado.getItinerarioViagem().get(indexObjSelecionado - 1).setOrdem(itinerarioViagem.getOrdem());
            itinerarioViagem.setOrdem(ordemSuperior);
        }
    }

    public void descerOrdemItinerario(ItinerarioViagem itinerarioViagem) {
        int indexObjSelecionado = selecionado.getItinerarioViagem().indexOf(itinerarioViagem);
        if (indexObjSelecionado < selecionado.getItinerarioViagem().size()) {
            Integer ordemInferior = selecionado.getItinerarioViagem().get(indexObjSelecionado + 1).getOrdem();
            selecionado.getItinerarioViagem().get(indexObjSelecionado + 1).setOrdem(itinerarioViagem.getOrdem());
            itinerarioViagem.setOrdem(ordemInferior);
        }
    }

    //--MÉTODOS DAS MANUTENÇÕES DA VIAGEM
    public ViagemManutencaoVeiculo getViagemManutencaoVeiculo() {
        return viagemManutencaoVeiculo;
    }

    public void setViagemManutencaoVeiculo(ViagemManutencaoVeiculo viagemManutencaoVeiculo) {
        this.viagemManutencaoVeiculo = viagemManutencaoVeiculo;
    }

    public List<ManutencaoObjetoFrota> completaManutencao(String parte) {
        return manutencaoObjetoFrotaFacade.listarManutencao(selecionado.getVeiculo(), parte.trim());
    }

    public void novaManutencao() {
        Web.navegacao(getCaminhoOrigem(), "/frota/manutencao/novo/", selecionado);
    }

    private boolean existeManutencaoAdicionada(ViagemManutencaoVeiculo viagemManutencaoVeiculo) {
        if (selecionado.getManutencoesVeiculo() != null) {
            for (ViagemManutencaoVeiculo item : selecionado.getManutencoesVeiculo()) {
                if (!item.equals(viagemManutencaoVeiculo) && item.getManutencaoObjetoFrota().equals(viagemManutencaoVeiculo.getManutencaoObjetoFrota())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validaInformacoesManutencaoVeiculo() {
        boolean retorno = true;
        if (viagemManutencaoVeiculo.getManutencaoObjetoFrota() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("A manutenção deve ser informada.");
        } else if (existeManutencaoAdicionada(viagemManutencaoVeiculo)) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A manutenção seleciona já foi adicionada.");
        }
        return retorno;
    }

    public void adicionarViagemManutencaoVeiculo() {
        if (validaInformacoesManutencaoVeiculo()) {
            if (selecionado.getManutencoesVeiculo() == null) {
                selecionado.setManutencoesVeiculo(new ArrayList());
            }
            selecionado.getManutencoesVeiculo().add(viagemManutencaoVeiculo);
            inicializarViagemManutencaoVeiculo();
        }

    }

    public void removerManutencao(ViagemManutencaoVeiculo viagemManutencaoVeiculo) {
        selecionado.getManutencoesVeiculo().remove(viagemManutencaoVeiculo);
    }

    //--INICIO DOS MÉTODOS DE ABASTECIMENTO

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (selecionado.getDataSaida().compareTo(selecionado.getVeiculo().getDataAquisicao()) < 0) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("A Data de Saída não pode ser inferior a Data de Aquisição do Veículo.");
        }
        return validou;
    }

    public ViagemAbastecimento getViagemAbastecimento() {
        return viagemAbastecimento;
    }

    public void setViagemAbastecimento(ViagemAbastecimento viagemAbastecimento) {
        this.viagemAbastecimento = viagemAbastecimento;
    }

    public void novoAbastecimento() {
        Web.navegacao(getCaminhoOrigem(), "/abastecimento-veiculo/novo/", selecionado);
    }

    public List<AbastecimentoObjetoFrota> completaAbastecimento(String parte) {
        return abastecimentoObjetoFrotaFacade.listarAbastecimento(selecionado.getVeiculo(), parte.trim());
    }

    private boolean validaInformacoesAbastecimentoVeiculo() {
        boolean retorno = true;
        retorno = Util.validaCampos(viagemAbastecimento);
        if (retorno) {
            if (viagemAbastecimento.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("A Quantidade não pode ser menor ou igual a 0(zero).");
            }
            if (viagemAbastecimento.getQuilometragemAtual().compareTo(BigDecimal.ZERO) <= 0) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("A Quilometragem Atual não pode ser menor ou igual a 0(zero).");
            }
        }
        return retorno;
    }

    public void adicionarViagemAbastecimento() {
        if (validaInformacoesAbastecimentoVeiculo()) {
            if (selecionado.getAbastecimentos() == null) {
                selecionado.setAbastecimentos(new ArrayList());
            }
            viagemAbastecimento.setViagem(selecionado);
            selecionado.getAbastecimentos().add(viagemAbastecimento);
            inicializarViagemAbastecimento();
            FacesUtil.atualizarComponente("Formulario:tab-view-geral:tabela-abastecimento");
            FacesUtil.executaJavaScript("dlgAbastecimento.hide()");
        }
    }

    public void removerAbastecimento(ViagemAbastecimento viagemAbastecimento) {
        selecionado.getAbastecimentos().remove(viagemAbastecimento);
    }
}
