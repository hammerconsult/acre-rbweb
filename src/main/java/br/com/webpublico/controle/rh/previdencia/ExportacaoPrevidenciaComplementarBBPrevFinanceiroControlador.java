package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ItemEntidadeDPContas;
import br.com.webpublico.entidades.ItemEntidadeDPContasUnidadeOrganizacional;
import br.com.webpublico.entidades.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrevFinanceiro;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrevFinanceiroFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "exportacaoPrevidenciaComplementarBBPrevFinanceiroControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExportacaoPrevidenciaComplementarBBPrevFinanceiro", pattern = "/exportacao-previdencia-complementar-bbprev-financeiro/novo/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprevfinanceiro/edita.xhtml"),
    @URLMapping(id = "listarExportacaoPrevidenciaComplementarBBPrevFinanceiro", pattern = "/exportacao-previdencia-complementar-bbprev-financeiro/listar/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprevfinanceiro/lista.xhtml"),
    @URLMapping(id = "verExportacaoPrevidenciaComplementarBBPrevFinanceiro", pattern = "/exportacao-previdencia-complementar-bbprev-financeiro/ver/#{exportacaoPrevidenciaComplementarBBPrevFinanceiroControlador.id}/", viewId = "/faces/rh/previdencia/exportacaoprevidenciacomplementarbbprevfinanceiro/visualizar.xhtml")
})
public class ExportacaoPrevidenciaComplementarBBPrevFinanceiroControlador extends PrettyControlador<ExportacaoPrevidenciaComplementarBBPrevFinanceiro> implements Serializable, CRUD {

    @EJB
    private ExportacaoPrevidenciaComplementarBBPrevFinanceiroFacade facade;
    @EJB
    private EntidadeDPContasFacade entidadeDPContasFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ContratoFP contratoFPSelecionado;
    private List<ContratoFP> contratos;
    private List<ContratoFP> contratosParaGeracao;
    private ConverterAutoComplete converterItemEntidadeDPContas;
    private CompletableFuture<ExportacaoPrevidenciaComplementarBBPrevFinanceiro> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ExportacaoPrevidenciaComplementarBBPrevFinanceiroControlador() {
        super(ExportacaoPrevidenciaComplementarBBPrevFinanceiro.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/exportacao-previdencia-complementar-bbprev-financeiro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoExportacaoPrevidenciaComplementarBBPrevFinanceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataGeracao(new Date());
        contratos = Lists.newArrayList();
        contratosParaGeracao = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "verExportacaoPrevidenciaComplementarBBPrevFinanceiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        recuperarArquivo();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (contratos != null && !contratos.isEmpty()) {
                contratosParaGeracao = contratos;
            } else {
                contratosParaGeracao = facade.buscarContratosParaExportacao(selecionado.getMes(), selecionado.getAno(), montarIdOrgaoItemEntidadeDPContas(selecionado.getPatrocinador()));
            }
            validarContratos();
            validarContribuicoes();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Geração de arquivo da Previdência Complementar BBPrev Financeiro");
            assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            assistenteBarraProgresso.setTotal(contratosParaGeracao.size());

            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> {
                    try {
                        return facade.gerarArquivo(selecionado, contratosParaGeracao, assistenteBarraProgresso);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            FacesUtil.executaJavaScript("gerarArquivoPrevidenciaComplementarFinanceiro()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao exportar arquivo da previdência complementar BBPrev financeiro {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public List<Long> montarIdOrgaoItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        List<Long> ids = Lists.newArrayList();
        itemEntidadeDPContas = itemEntidadeDPContasFacade.recuperar(itemEntidadeDPContas.getId());
        for (ItemEntidadeDPContasUnidadeOrganizacional item : itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional()) {
            ids.add(item.getHierarquiaOrganizacional().getSubordinada().getId());
        }
        return ids;
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            try {
                selecionado = future.get();
            } catch (Exception e) {
                logger.error("erro", e);
                FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao gerar arquivo da Previdência Complementar BBPrev Financeiro.");
            }
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.executaJavaScript("dialogo.hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPatrocinador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Patrocinador deve ser informado.");
        }
        if (selecionado.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        } else if (selecionado.getMes() < 1 || selecionado.getMes() > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        ve.lancarException();
    }

    public void validarContratos() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (contratosParaGeracao == null || contratosParaGeracao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foram encontrados servidores com os filtros selecionados.");
        }
        ve.lancarException();
    }

    public void validarContribuicoes() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        boolean existeContribuicoes = false;
        for (ContratoFP contrato : contratosParaGeracao) {
            if (!facade.buscarValoresContribuicaoParaServidor(contrato, selecionado.getMes(), selecionado.getAno()).isEmpty()){
                existeContribuicoes = true;
                break;
            }
        }
        if (!existeContribuicoes) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível apurar valores de Contribuição do Participante ou Contribuição da Patrocinadora na geração. Por favor, confira se existem folhas de pagamento efetivadas na competência selecionada e que contenham verbas configuradas quanto à Previdência Complementar.");
        }
        ve.lancarException();
    }

    private void recuperarArquivo() {
        selecionado.setArquivo(arquivoFacade.recuperaDependencias(selecionado.getArquivo().getId()));
    }

    public DefaultStreamedContent getFileDownload() {
        selecionado.getArquivo().montarImputStream();
        return new DefaultStreamedContent(selecionado.getArquivo().getInputStream(), "text/plain", selecionado.getArquivo().getNome());
    }

    public void adicionarContrato() {
        try {
            validarContratosAdicionados();
            contratos.add(contratoFPSelecionado);
            contratoFPSelecionado = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarContratosAdicionados() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        for (ContratoFP contrato : contratos) {
            if (contratoFPSelecionado != null && contrato.getId().equals(contratoFPSelecionado.getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato selecionado já se encontra na lista de exportação.");
            }
        }
        ve.lancarException();
    }

    public void removerContrato(ContratoFP contrato) {
        contratos.remove(contrato);
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        if (selecionado.getMes() != null && selecionado.getAno() != null && selecionado.getPatrocinador() != null) {
            return facade.buscarContratosComPrevidenciaComplementarVigenteFiltrandoMatriculaOrNomeOrCPF(parte.trim(), selecionado.getMes(), selecionado.getAno(), montarIdOrgaoItemEntidadeDPContas(selecionado.getPatrocinador()));
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getEntidades() {
        return Util.getListSelectItem(entidadeDPContasFacade.recuperarItensEntidadeDPContasVigenteParaCategoria(CategoriaDeclaracaoPrestacaoContas.BBPREV, UtilRH.getDataOperacao()));
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public List<ContratoFP> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoFP> contratos) {
        this.contratos = contratos;
    }

    public ConverterAutoComplete getConverterItemEntidadeDPContas() {
        if (converterItemEntidadeDPContas == null) {
            converterItemEntidadeDPContas = new ConverterAutoComplete(ItemEntidadeDPContas.class, entidadeDPContasFacade);
        }
        return converterItemEntidadeDPContas;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
