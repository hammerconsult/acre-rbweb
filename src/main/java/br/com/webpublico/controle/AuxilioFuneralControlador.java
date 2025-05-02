package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AuxilioFuneralFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
import java.util.List;

/**
 * Criado por Mateus
 * Data: 11/05/2017.
 */
@ManagedBean(name = "auxilioFuneralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAuxiliofuneral", pattern = "/auxilio-funeral/novo/", viewId = "/faces/funeral/auxiliofuneral/edita.xhtml"),
    @URLMapping(id = "editarAuxiliofuneral", pattern = "/auxilio-funeral/editar/#{auxilioFuneralControlador.id}/", viewId = "/faces/funeral/auxiliofuneral/edita.xhtml"),
    @URLMapping(id = "listarAuxiliofuneral", pattern = "/auxilio-funeral/listar/", viewId = "/faces/funeral/auxiliofuneral/lista.xhtml"),
    @URLMapping(id = "verAuxiliofuneral", pattern = "/auxilio-funeral/ver/#{auxilioFuneralControlador.id}/", viewId = "/faces/funeral/auxiliofuneral/visualizar.xhtml")
})
public class AuxilioFuneralControlador extends PrettyControlador<AuxilioFuneral> implements Serializable, CRUD {

    @EJB
    private AuxilioFuneralFacade auxilioFuneralFacade;
    private ComposicaoFamiliar composicaoFamiliar;

    public AuxilioFuneralControlador() {
        super(AuxilioFuneral.class);
    }

    @URLAction(mappingId = "novoAuxiliofuneral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarComposicaoFamiliar();
        selecionado.setDataDoAtendimento(auxilioFuneralFacade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "verAuxiliofuneral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAuxiliofuneral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarComposicaoFamiliar();
    }

    public List<Procedencia> completarProcedencias(String filtro) {
        return auxilioFuneralFacade.getProcedenciaFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    public List<Funeraria> completarFunerarias(String filtro) {
        return auxilioFuneralFacade.getFunerariaFacade().listaFiltrando(filtro.trim(), "descricao");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/auxilio-funeral/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return auxilioFuneralFacade;
    }

    public void adicionarComposicaoFamiliar() {
        try {
            composicaoFamiliar.setAuxilioFuneral(selecionado);
            validarCamposComposicaoFamiliar();
            Util.adicionarObjetoEmLista(selecionado.getFamiliares(), composicaoFamiliar);
            cancelarComposicaoFamiliar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarCamposComposicaoFamiliar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(composicaoFamiliar, ve);
        ve.lancarException();
    }

    public void instanciarComposicaoFamiliar() {
        composicaoFamiliar = new ComposicaoFamiliar();
    }

    public void cancelarComposicaoFamiliar() {
        composicaoFamiliar = null;
    }

    public void editarComposicaoFamiliar(ComposicaoFamiliar composicaoFamiliar) {
        this.composicaoFamiliar = (ComposicaoFamiliar) Util.clonarObjeto(composicaoFamiliar);
    }

    public void removerComposicaoFamiliar(ComposicaoFamiliar composicaoFamiliar) {
        selecionado.getFamiliares().remove(composicaoFamiliar);
    }

    public void limparBeneficio() {
        selecionado.setBeneficio("");
    }

    public List<SelectItem> getTiposDeAsfalto() {
        return Util.getListSelectItem(TipoAsfalto.values());
    }

    public List<SelectItem> getTiposDeTerreno() {
        return Util.getListSelectItem(TipoTerreno.values());
    }

    public List<SelectItem> getCondicoesDeOcupacao() {
        return Util.getListSelectItem(CondicaoOcupacao.values());
    }

    public List<SelectItem> getTiposDeCasa() {
        return Util.getListSelectItem(TipoCasa.values());
    }

    public List<SelectItem> getTiposDeEsgoto() {
        return Util.getListSelectItem(TipoEsgoto.values());
    }

    public List<SelectItem> getTiposDeDistribuicaoDeAgua() {
        return Util.getListSelectItem(TipoDistribuicaoAgua.values());
    }

    public List<SelectItem> getTiposDeEnergiaEletrica() {
        return Util.getListSelectItem(TipoEnergiaEletrica.values());
    }

    public List<SelectItem> getTiposDeBeneficioCedido() {
        return Util.getListSelectItem(TipoBeneficioCedido.values());
    }

    public List<SelectItem> getTiposDeMotivoRequisicaoUmBeneficio() {
        return Util.getListSelectItem(MotivoRequisicaoUmBeneficio.values());
    }

    public List<SelectItem> getEstadosCivis() {
        return Util.getListSelectItem(EstadoCivil.values());
    }

    public List<SelectItem> getRenunciasBeneficios() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoBeneficioCedido tipoBeneficioCedido : TipoBeneficioCedido.values()) {
            if (!TipoBeneficioCedido.JAZIGO_E_URNA.equals(tipoBeneficioCedido)) {
                toReturn.add(new SelectItem(tipoBeneficioCedido, tipoBeneficioCedido.toString()));
            }
        }
        return toReturn;
    }

    public void gerarSolicitacaoSepultamento() {
        emitirDocumentoOficial(ModuloTipoDoctoOficial.SOLICITACAO_SEPULTAMENTO);
    }

    public void gerarDeclaracaoBeneficiosEventuais() {
        emitirDocumentoOficial(ModuloTipoDoctoOficial.DECLARACAO_BENEFICIOS_EVENTUAIS);
    }

    public void gerarRequisicao() {
        emitirDocumentoOficial(ModuloTipoDoctoOficial.REQUISICAO_FUNERAL);
    }

    public void gerarDeclaracaoSolicitanteBeneficiario() {
        emitirDocumentoOficial(ModuloTipoDoctoOficial.DECLARACAO_SOLICITANTE_BENEFICIARIO);
    }

    private ModeloDoctoOficial buscarModeloDoctoOficial(ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        TipoDoctoOficial tipo = auxilioFuneralFacade.getTipoDoctoOficialFacade().buscarTipoDoctoPorModulo(moduloTipoDoctoOficial);
        if (tipo != null) {
            return auxilioFuneralFacade.getTipoDoctoOficialFacade().recuperaModeloVigente(tipo);
        }
        return null;
    }

    private void emitirDocumentoOficial(ModuloTipoDoctoOficial tipo) {
        try {
            ModeloDoctoOficial modelo = buscarModeloDoctoOficial(tipo);
            if (modelo != null) {
                DocumentoOficial docto = auxilioFuneralFacade.getDocumentoOficialFacade()
                    .gerarNovoDocumentoSolicitacaoSepultamento(selecionado, modelo);
                auxilioFuneralFacade.getDocumentoOficialFacade().emiteDocumentoOficial(docto);
            } else {
                FacesUtil.addAtencao("Antes de gerar esse documento configure o modelo de Documento Oficial para o tipo '" + tipo.getDescricao() + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    @Override
    public void salvar() {
        try {
            validarCamposSalvar();
            selecionado = auxilioFuneralFacade.salvarRetornando(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            if(selecionado.getId() != null){
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            }else{
                redireciona();
            }

        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }


    }

    private void validarCamposSalvar() {
        ValidacaoException ve = new ValidacaoException();
        validarCpf(selecionado.getCpfResponsavel(), "Responsável", ve);
        validarCpf(selecionado.getCpfFalecido(), "Falecido", ve);
        validarCpf(selecionado.getCpfResponsavelBeneficio(), "responsável pela solicitação do Auxílio", ve);
        if (selecionado.getDataFalecimento().before(selecionado.getDataNascimentoFalecido())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de nascimento deve ser anterior ou igual a data de falecimento!");
        }
        ve.lancarException();

    }

    public void validarCPFResponsavel(String cpfResponsavel) {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarCpf(cpfResponsavel, "Responsável", ve);
            ve.lancarException();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarCPFFalecido(String cpfResponsavel) {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarCpf(cpfResponsavel, "Falecido", ve);
            ve.lancarException();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarCPFBeneficio(String cpfResponsavel) {
        try {
            ValidacaoException ve = new ValidacaoException();
            validarCpf(cpfResponsavel, "responsável pela solicitação do Auxílio", ve);
            ve.lancarException();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarCpf(String campoCpf, String nomeDoCampo, ValidacaoException ve) {
        if (!campoCpf.isEmpty() && !Util.valida_CpfCnpj(campoCpf)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo CPF do " + nomeDoCampo + " é inválido!");
        }
    }

    public ComposicaoFamiliar getComposicaoFamiliar() {
        return composicaoFamiliar;
    }

    public void setComposicaoFamiliar(ComposicaoFamiliar composicaoFamiliar) {
        this.composicaoFamiliar = composicaoFamiliar;
    }
}
