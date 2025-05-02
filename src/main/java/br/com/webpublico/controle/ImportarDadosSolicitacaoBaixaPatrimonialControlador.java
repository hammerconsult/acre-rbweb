package br.com.webpublico.controle;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.SolicitacaoBaixaPatrimonial;
import br.com.webpublico.entidadesauxiliares.ImportacaoDadosSolicitacaoBaixa;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ImportacaoDadosSolicitacaoBaixaPatrimonialFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importar-dados-solicitacao-baixa", pattern = "/importar-dados-solicitacao-baixa-patrimonial/novo/",
        viewId = "/faces/administrativo/patrimonio/solicitacaoBaixa/movel/importar-dados.xhtml"),
})
public class ImportarDadosSolicitacaoBaixaPatrimonialControlador implements Serializable {


    @EJB
    private ImportacaoDadosSolicitacaoBaixaPatrimonialFacade facade;
    private AssistenteMovimentacaoBens assistenteBarraProgresso;
    private List<Bem> bens;
    private SolicitacaoBaixaPatrimonial solicitacaoBaixaPatrimonial;
    private Future<ImportacaoDadosSolicitacaoBaixa> futureProcesso;
    private Future<SolicitacaoBaixaPatrimonial> futureSalvar;
    private List<String> inconsistencias;
    private UploadedFile file;
    private ImportacaoDadosSolicitacaoBaixa importacaoDadosSolicitacaoBaixa;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    @URLAction(mappingId = "importar-dados-solicitacao-baixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }

    public void salvar() {
        try {
            validarRegrasAoSalvar();
            iniciarAssistenteBarraProgresso();
            futureSalvar = facade.salvarImportacaoDadosSolicitacao(solicitacaoBaixaPatrimonial, bens, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("iniciarSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void processarArquivo() {
        try {
            iniciarAssistenteBarraProgresso();
            importacaoDadosSolicitacaoBaixa = new ImportacaoDadosSolicitacaoBaixa();
            futureProcesso = facade.processarArquivo(file, assistenteBarraProgresso, importacaoDadosSolicitacaoBaixa);
            FacesUtil.executaJavaScript("iniciarProcessarArquivo()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                SolicitacaoBaixaPatrimonial selecionado = futureSalvar.get();
                if (selecionado != null) {
                    FacesUtil.redirecionamentoInterno("/solicitacao-baixa-bem-movel/ver/" + selecionado.getId());
                    futureSalvar = null;
                    FacesUtil.atualizarComponente("finalizarProcesso()");
                    FacesUtil.addOperacaoRealizada("Solicitação de baixa salva com sucesso.");
                }
            } catch (InterruptedException | ExecutionException e) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível processar o arquivo.");
            }
        }
    }


    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(getDataOperacao(), OperacaoMovimentacaoBem.SOLICITACAO_BAIXA_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(getDataOperacao(), getDataOperacao(), assistenteBarraProgresso.getOperacao());
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }


    public void consultarFutureProcessarArquivo() {
        if (futureProcesso != null && futureProcesso.isDone()) {
            try {
                adicionarInconsistencias();
                bens = Lists.newArrayList();
                bens.addAll(futureProcesso.get().getBens());
                solicitacaoBaixaPatrimonial = futureProcesso.get().getSolicitacaoBaixaPatrimonial();
                futureProcesso = null;
                FacesUtil.executaJavaScript("finalizarProcesso()");
                FacesUtil.atualizarComponente("Formulario:panelDados");
                FacesUtil.atualizarComponente("Formulario:grigGeral");
            } catch (Exception ex) {
                this.solicitacaoBaixaPatrimonial = null;
                this.bens = Lists.newArrayList();
                futureProcesso = null;
                FacesUtil.executaJavaScript("finalizarProcesso()");
                FacesUtil.addOperacaoNaoRealizada("Não foi possível processar o arquivo." + ex.getMessage());
            }
        }
    }

    private void adicionarInconsistencias() {
        if (assistenteBarraProgresso.getMensagens() != null && !assistenteBarraProgresso.getMensagens().isEmpty()) {
            inconsistencias = Lists.newArrayList();
            for (String msg : assistenteBarraProgresso.getMensagens()) {
                FacesUtil.addOperacaoNaoPermitida(msg);
                this.solicitacaoBaixaPatrimonial = null;
            }
            inconsistencias.addAll(assistenteBarraProgresso.getMensagens());
            FacesUtil.addAtencao("Consulte a aba inconsitências para ver detalhes do erro.");
        }
    }

    private void validarRegrasAoSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (file == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, selecione um arquivo com os dados da solicitação de baixa.");
        }
        ve.lancarException();
        if (solicitacaoBaixaPatrimonial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, clique no botão 'Processar Arquivo' para carregar os dados da solicitação de baixa.");
        }
        ve.lancarException();
        solicitacaoBaixaPatrimonial.realizarValidacoes();
        if (solicitacaoBaixaPatrimonial.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        if (bens == null || bens.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A lista de bens não pode ser vazia.");
        }
        validarRegrasConfiguracaoMovimentacaoBem();
        ve.lancarException();
    }

    private void iniciarAssistenteBarraProgresso() {
        assistenteBarraProgresso = new AssistenteMovimentacaoBens(getDataOperacao(), Operacoes.NOVO);
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setSelecionado(solicitacaoBaixaPatrimonial);
        assistenteBarraProgresso.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void handleFilesUploads(FileUploadEvent event) {
        file = event.getFile();
    }

    public BigDecimal getValorTotalOriginalBens() {
        BigDecimal total = BigDecimal.ZERO;
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                if (bem != null && bem.getValorOriginal() != null) {
                    total = total.add(bem.getValorOriginal());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteBens() {
        BigDecimal total = BigDecimal.ZERO;
        if (bens != null && !bens.isEmpty()) {
            for (Bem bem : bens) {
                if (bem != null && bem.getValorAjuste() != null) {
                    total = total.add(bem.getValorDosAjustes());
                }
            }
        }
        return total;
    }

    public List<Bem> getBens() {
        return bens;
    }

    public void setBens(List<Bem> bens) {
        this.bens = bens;
    }

    public SolicitacaoBaixaPatrimonial getSolicitacaoBaixaPatrimonial() {
        return solicitacaoBaixaPatrimonial;
    }

    public void setSolicitacaoBaixaPatrimonial(SolicitacaoBaixaPatrimonial solicitacaoBaixaPatrimonial) {
        this.solicitacaoBaixaPatrimonial = solicitacaoBaixaPatrimonial;
    }

    public AssistenteMovimentacaoBens getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<String> getInconsistencias() {
        return inconsistencias;
    }

    public void setInconsistencias(List<String> inconsistencias) {
        this.inconsistencias = inconsistencias;
    }
}
