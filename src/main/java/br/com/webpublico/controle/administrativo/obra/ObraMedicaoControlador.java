package br.com.webpublico.controle.administrativo.obra;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoFuncionalidadeParaAnexo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.obra.ObraMedicaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaca on 28/04/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-obra-medicao", pattern = "/obra/medicao/novo/", viewId = "/faces/administrativo/obras/medicaoobra/editar.xhtml"),
    @URLMapping(id = "editar-obra-medicao", pattern = "/obra/medicao/editar/#{obraMedicaoControlador.id}/", viewId = "/faces/administrativo/obras/medicaoobra/editar.xhtml"),
    @URLMapping(id = "ver-obra-medicao", pattern = "/obra/medicao/ver/#{obraMedicaoControlador.id}/", viewId = "/faces/administrativo/obras/medicaoobra/visualizar.xhtml"),
    @URLMapping(id = "listar-obra-medicao", pattern = "/obra/medicao/listar/", viewId = "/faces/administrativo/obras/medicaoobra/listar.xhtml")
})
public class ObraMedicaoControlador extends PrettyControlador<ObraMedicao> implements CRUD {

    @EJB
    private ObraMedicaoFacade obraMedicaoFacade;
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    private ObraMedicaoExecucaoContrato execucaoMedicao;
    private ConverterAutoComplete converterExecucaoMedicao;

    public ObraMedicaoControlador() {
        super(ObraMedicao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/obra/medicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @URLAction(mappingId = "novo-obra-medicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        setTipoDocumentoAnexo(null);


    }

    @URLAction(mappingId = "editar-obra-medicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setTipoDocumentoAnexo(null);
    }

    @URLAction(mappingId = "ver-obra-medicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
        setTipoDocumentoAnexo(null);
    }

    @Override
    public AbstractFacade getFacede() {
        return obraMedicaoFacade;
    }


    @Override
    public void salvar() {
        try {
            validarSelecionado();
            if (isOperacaoNovo()) {
                selecionado.setNumero(obraMedicaoFacade.getProximoNumero(selecionado.getObra()));
                obraMedicaoFacade.salvarNovo(selecionado);
            } else {
                obraMedicaoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
            logger.error("erro.:", ex);
        }
    }

    private void validarSelecionado() {
        selecionado.realizarValidacoes();
        validarObrigatoriedadeAnexo();
        validarRegrasEspecificas();
    }

    @Override
    public void excluir() {
        if (!selecionado.equals(obraMedicaoFacade.buscarUltimaObraMedicaoPorMaiorNumeroDaObra(selecionado.getObra()))) {
            FacesUtil.addOperacaoNaoPermitida("A Medição " + selecionado + " não é a última medição realizada, portanto, não poderá ser excluída!");
            return;
        }
        super.excluir();
    }

    private void validarRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if ((selecionado.getValorTotal() != null) && selecionado.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que 0.");
        }
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
            if (selecionado.getDataInicial().after(selecionado.getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data Inicial não pode ser maior que a data final.");
            }
        }
        ObraMedicao medicaoVigente = obraMedicaoFacade.buscarUltimaMedicaoPorObra(selecionado);
        if (medicaoVigente != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma medição vigente iniciando nestas datas para essa obra " + selecionado.getObra() + ".");
        }
        if (obraMedicaoFacade.buscarMedicaoPorNumero(selecionado) != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma medição com o número informado.");
        }
        BigDecimal totalMedicoes = BigDecimal.ZERO;
        selecionado.setObra(obraMedicaoFacade.getObraFacade().recuperar(selecionado.getObra().getId()));
        for (ObraMedicao obraMedicao : selecionado.getObra().getMedicoes()) {
            if (!obraMedicao.equals(selecionado)) {
                totalMedicoes = totalMedicoes.add(obraMedicao.getValorTotal());
            }
        }
        if (totalMedicoes.add(selecionado.getValorTotal()).compareTo(selecionado.getObra().getContrato().getValorTotal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total das medições (" + Util.formataValor(totalMedicoes.add(selecionado.getValorTotal())) + ") excedeu o total contratado na obra <b> " + Util.formataValor(selecionado.getObra().getContrato().getValorTotal()) + "</b>");
        }
        if (selecionado.getExecucoesMedicao() == null || selecionado.getExecucoesMedicao().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É Obrigatório adicionar ao menos uma Execução da Medição.");
        } else {
            if (selecionado.getValorTotal().compareTo(selecionado.getValorTotalExecucoes()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da medição " + Util.formataValor(selecionado.getValorTotal())
                    + " ultrapassa o valor total das execuções para esta medição " + Util.formataValor(selecionado.getValorTotalExecucoes()) + ".");
            }
        }

        ve.lancarException();
    }

    private void validarObrigatoriedadeAnexo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAnexos() == null || selecionado.getAnexos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório anexar um documento na medição da obra.");
        } else {
            obraMedicaoFacade.getDocumentosNecessariosAnexoFacade().validarSeTodosOsTipoDeDocumentoForamAnexados(TipoFuncionalidadeParaAnexo.OBRA, selecionado.getAnexos());
        }
        ve.lancarException();
    }

    public void carregarArquivo(FileUploadEvent event) {
        try {
            Arquivo arquivo = definirArquivo(event);
            ObraMedicaoAnexo anexo = new ObraMedicaoAnexo();
            anexo.setArquivo(arquivo);
            anexo.setObraMedicao(selecionado);
            anexo.setTipoDocumentoAnexo(getTipoDocumentoAnexo());
            selecionado.setAnexos(Util.adicionarObjetoEmLista(selecionado.getAnexos(), anexo));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private Arquivo definirArquivo(FileUploadEvent event) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType(event.getFile().getContentType());
        arquivo.setNome(event.getFile().getFileName());
        arquivo.setDescricao(event.getFile().getFileName());
        arquivo.setTamanho(event.getFile().getSize());
        arquivo.setInputStream(event.getFile().getInputstream());
        arquivo = obraMedicaoFacade.getArquivoFacade().novoArquivoMemoria(arquivo);
        return arquivo;
    }

    public StreamedContent recuperarArquivo(Arquivo arquivo) {
        return obraMedicaoFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arquivo);
    }

    public List<Obra> buscarTodasObrasPorNome(String filter) {
        return obraMedicaoFacade.getObraFacade().buscarTodasObrasPorNome(filter);
    }

    public List<SelectItem> getTiposDocumentos() {
        List<TipoDocumentoAnexo> anexos = obraMedicaoFacade.getTipoDocumentoAnexoFacade().buscarTodosTipoDocumentosAnexo();
        return Util.getListSelectItem(anexos, true);
    }

    public void removerAnexoMedicao(ObraMedicaoAnexo anexo) {
        this.selecionado.getAnexos().remove(anexo);
    }

    public void novaExecucaoMedicao() {
        try {
            selecionado.realizarValidacoes();
            execucaoMedicao = new ObraMedicaoExecucaoContrato();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void cancelarExecucaoMedicao() {
        execucaoMedicao = null;
    }

    public void removerExecucaoContrato(ObraMedicaoExecucaoContrato execucaoContrato) {
        this.execucaoMedicao = (ObraMedicaoExecucaoContrato) Util.clonarObjeto(execucaoContrato);
        selecionado.getExecucoesMedicao().remove(execucaoContrato);
    }

    public void adicionarExecucaoMedicao() {
        try {
            validarExecucaoMedicao();
            execucaoMedicao.setObraMedicao(selecionado);
            selecionado.setExecucoesMedicao(Util.adicionarObjetoEmLista(selecionado.getExecucoesMedicao(), execucaoMedicao));
            cancelarExecucaoMedicao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarExecucaoMedicao() {
        Util.validarCampos(execucaoMedicao);
        selecionado.validarMesmaExecucaoEmLista(execucaoMedicao);
        validarMesmaExecucaoContratoParaMedicoesDiferentes();
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public ObraMedicaoExecucaoContrato getExecucaoMedicao() {
        return execucaoMedicao;
    }

    public void setExecucaoMedicao(ObraMedicaoExecucaoContrato execucaoMedicao) {
        this.execucaoMedicao = execucaoMedicao;
    }

    public ConverterAutoComplete getConverterExecucaoMedicao() {
        if (converterExecucaoMedicao == null) {
            converterExecucaoMedicao = new ConverterAutoComplete(ExecucaoContrato.class, obraMedicaoFacade.getExecucaoContratoFacade());
        }
        return converterExecucaoMedicao;
    }

    public List<SelectItem> getExecucoesContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        try {
            if (selecionado.getObra() != null && selecionado.getObra().getContrato() != null) {
                Contrato contrato = obraMedicaoFacade.getObraFacade().getContratoFacade().recuperar(selecionado.getObra().getContrato().getId());
                validarBuscaExecucaoContrato(contrato);
                for (ExecucaoContrato execucaoContrato : contrato.getExecucoes()) {
                    if (execucaoContrato != null) {
                        toReturn.add(new SelectItem(execucaoContrato, execucaoContrato.toString()));
                    }
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
        return toReturn;
    }

    private void validarMesmaExecucaoContratoParaMedicoesDiferentes() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorTotal().compareTo(execucaoMedicao.getExecucaoContrato().getValor()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("o valor da medição " + Util.formataValor(selecionado.getValorTotal())
                + " ultrapassa o valor da execução do contrato de " + Util.formataValor(execucaoMedicao.getExecucaoContrato().getValor()) + ".");
        }
        ve.lancarException();
        BigDecimal totalMedicoesExecutadas = obraMedicaoFacade.getValorTotalMedicoesPorExecucao(execucaoMedicao.getExecucaoContrato());
        if (totalMedicoesExecutadas.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalMedicao = totalMedicoesExecutadas.add(selecionado.getValorTotal());
            if (totalMedicao.compareTo(execucaoMedicao.getExecucaoContrato().getValor()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da medição ultrapassa o valor já executado para a execução do contrato. "
                    + Util.formataValor(execucaoMedicao.getExecucaoContrato().getValor()) + "<br/><br/>"
                    + " Valor da medição: <b>" + Util.formataValor(selecionado.getValorTotal()) + "</b>"
                    + " Total das medições já executadas: <b>" + Util.formataValor(totalMedicoesExecutadas) + "</b>"
                    + " Total das medições já executadas + medição atual: <b>" + Util.formataValor(totalMedicao) + "</b>");
            }
        }
        ve.lancarException();
    }

    private void validarBuscaExecucaoContrato(Contrato contrato) {
        ValidacaoException ve = new ValidacaoException();
        if (contrato == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A obra selecionado não possui contratgo");
        }
        ve.lancarException();
        if (contrato.getExecucoes() == null || contrato.getExecucoes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato " + selecionado.getObra().getContrato() + " não possui execuções.");
        }
        ve.lancarException();
    }

    public boolean hasExecucaoAdicionada() {
        return selecionado.getExecucoesMedicao() != null && !selecionado.getExecucoesMedicao().isEmpty();
    }

    public void obraSelecionada(){
        if(selecionado.getObra()!=null) {
            selecionado.setNumero(obraMedicaoFacade.getProximoNumero(selecionado.getObra()));
        }else{
            selecionado.setNumero(0L);
        }
    }
}
