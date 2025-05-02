package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConvenioListaDebitos;
import br.com.webpublico.entidades.ExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.AssistenteExportacaoDebitosIPTU;
import br.com.webpublico.enums.SituacaoExportacaoDebitosIPTU;
import br.com.webpublico.enums.TipoArquivoExportacaoDebitosIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExportacaoDebitosIPTUFacade;
import br.com.webpublico.negocios.tributario.services.ServiceExportacaoDebitosIPTU;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author octavio
 */
@ManagedBean(name = "exportacaoDebitosIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExportarDebitosIPTU", pattern = "/tributario/exportacao-debitos-iptu/novo/", viewId = "/faces/tributario/iptu/exportacaodebitos/edita.xhtml"),
    @URLMapping(id = "verExportarDebitosIPTU", pattern = "/tributario/exportacao-debitos-iptu/ver/#{exportacaoDebitosIPTUControlador.id}/", viewId = "/faces/tributario/iptu/exportacaodebitos/visualizar.xhtml"),
    @URLMapping(id = "listaExportarDebitosIPTU", pattern = "/tributario/exportacao-debitos-iptu/listar/", viewId = "/faces/tributario/iptu/exportacaodebitos/lista.xhtml")
})
public class ExportacaoDebitosIPTUControlador extends PrettyControlador<ExportacaoDebitosIPTU> implements Serializable, CRUD {

    @EJB
    private ExportacaoDebitosIPTUFacade exportacaoDebitosIPTUFacade;

    private boolean canBaixarArquivo;
    private boolean isTarefaAgendada;

    private AssistenteExportacaoDebitosIPTU assistente;
    private Future<AssistenteExportacaoDebitosIPTU> futureExportacao;

    private final ServiceExportacaoDebitosIPTU serviceExportacao;

    public ExportacaoDebitosIPTUControlador() {
        super(ExportacaoDebitosIPTU.class);
        this.serviceExportacao = Util.recuperarSpringBean(ServiceExportacaoDebitosIPTU.class);
        this.canBaixarArquivo = false;
    }

    @URLAction(mappingId = "novoExportarDebitosIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        adicionarInformacoesNovo();
    }

    @URLAction(mappingId = "verExportarDebitosIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        adicionarInformacoesVer();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade<ExportacaoDebitosIPTU> getFacede() {
        return exportacaoDebitosIPTUFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/exportacao-debitos-iptu/";
    }

    public boolean isCanBaixarArquivo() {
        return canBaixarArquivo;
    }

    public void setCanBaixarArquivo(boolean canBaixarArquivo) {
        this.canBaixarArquivo = canBaixarArquivo;
    }

    public AssistenteExportacaoDebitosIPTU getAssistente() {
        if (serviceExportacao.containsExportacao(selecionado.getId())) {
            return serviceExportacao.getExportacao(selecionado.getId());
        }
        return assistente;
    }

    public void setAssistente(AssistenteExportacaoDebitosIPTU assistente) {
        this.assistente = assistente;
    }

    @Override
    public void salvar() {
        try {
            validarExportacao();
            selecionado = exportacaoDebitosIPTUFacade.salvarRetornando(selecionado);
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar exportação. Detalhes: " + e.getMessage());
        }
    }

    private void adicionarInformacoesNovo() {
        selecionado.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.ABERTO);
        selecionado.setExercicio(exportacaoDebitosIPTUFacade.recuperarExercicioCorrente());
        selecionado.setInscricaoInicial("100000000000000");
        selecionado.setInscricaoFinal("999999999999999");
        selecionado.setVencimentoIncial(new Date());
        selecionado.setVencimentoFinal(new Date());
        selecionado.setDataGeracao(new Date());
        selecionado.setResponsavel(exportacaoDebitosIPTUFacade.recuperarUsuarioCorrente());
        selecionado.setTipoArqExportacaoDebitosIPTU(TipoArquivoExportacaoDebitosIPTU.RBC800);
    }

    private void adicionarInformacoesVer() {
        AssistenteExportacaoDebitosIPTU assistente = getAssistente();
        if (assistente != null) {
            selecionado.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.GERANDO);
            selecionado = exportacaoDebitosIPTUFacade.salvarRetornando(selecionado);
            this.isTarefaAgendada = true;
        } else {
            this.canBaixarArquivo = false;
            selecionado = exportacaoDebitosIPTUFacade.alterarSituacao(selecionado);
        }
    }

    public List<ConvenioListaDebitos> completarConvenioListaDebitos(String parte) {
        return exportacaoDebitosIPTUFacade.buscarConvenios(parte.trim());
    }

    public void exportarDebitosIPTU() {
        try {
            if (!serviceExportacao.containsExportacao(selecionado.getId()) && serviceExportacao.isEmptyExportacoes()) {
                this.isTarefaAgendada = false;
                FacesUtil.atualizarComponente("Formulario");
                assistente = criarAssistente();
                futureExportacao = serviceExportacao.gerarArquivoDeExportacao(assistente);
                iniciarPool();
            } else {
                FacesUtil.addOperacaoNaoPermitida("Já existe uma Exportação de Débitos de IPTU em andamento.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void executarPosExportacaoDebitosIPTU() {
        if (futureExportacao != null && futureExportacao.isDone()) {
            try {
                assistente = futureExportacao.get();
                if (assistente != null && assistente.isEncontrouParcelas()) {
                    selecionado = assistente.getSelecionado();

                    selecionado.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.CONCLUIDO);
                    selecionado.setNumeroRemessa(exportacaoDebitosIPTUFacade.recuperarNumeroRemessa());
                    selecionado = exportacaoDebitosIPTUFacade.salvarRetornando(selecionado);

                    serviceExportacao.removerExportacao(selecionado.getId());

                    assistente.setExecutando(false);
                    assistente = null;
                    futureExportacao = null;

                    this.canBaixarArquivo = true;

                    FacesUtil.atualizarComponente("formExportacao");
                    FacesUtil.atualizarComponente("Formulario");

                    FacesUtil.executaJavaScript("pollProgresso.stop()");
                } else if (assistente != null && !assistente.isEncontrouParcelas()) {
                    futureExportacao = null;
                    FacesUtil.executaJavaScript("wvExportacao.hide();");
                    FacesUtil.addOperacaoNaoRealizada("Não foi encontrado nenhum débito");
                }
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao terminar exportação de débitos. Detalhes: " + e.getMessage());
                logger.error(e.getMessage());
            }
        }
    }

    private AssistenteExportacaoDebitosIPTU criarAssistente() {
        AssistenteExportacaoDebitosIPTU assistente = new AssistenteExportacaoDebitosIPTU();

        assistente.setSelecionado(selecionado);
        assistente.setExercicio(exportacaoDebitosIPTUFacade.recuperarExercicioCorrente());
        assistente.setUsuarioSistema(exportacaoDebitosIPTUFacade.recuperarUsuarioCorrente());
        assistente.setPerfilDev(exportacaoDebitosIPTUFacade.isPerfilDev());
        assistente.setExecutando(true);

        return assistente;
    }

    public StreamedContent baixarArquivo() {
        try {
            if (selecionado.getArquivo() != null) {
                return exportacaoDebitosIPTUFacade.montarArquivoParaDownloadPorArquivo(selecionado.getArquivo());
            } else {
                return exportacaoDebitosIPTUFacade.montarArquivoParaDownload(selecionado);
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar arquivo ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o arquio. Detalhes: " + e.getMessage());
        }
        return null;
    }

    private void validarExportacao() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getConvenioListaDebitos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo convênio de lista de débitos é obrigatório.");
        }
        if (selecionado.getTipoArqExportacaoDebitosIPTU() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de arquivo é obrigatório.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício é obrigatório.");
        }
        if (selecionado.getInscricaoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo inscrição inicial é obrigatório.");
        }
        if (selecionado.getVencimentoIncial() != null && selecionado.getVencimentoFinal() != null &&
            selecionado.getVencimentoIncial().after(selecionado.getVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial não pode ser maior que a data final.");
        }
        if (selecionado.getConvenioListaDebitos() != null && selecionado.getConvenioListaDebitos().getDataInicialDispDebitos() != null &&
            selecionado.getConvenioListaDebitos().getDataInicialDispDebitos().before(new Date())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O convênio selecionado está vencido.");
        }
        if (selecionado.getExercicio() != null && !selecionado.getExercicio().equals(exportacaoDebitosIPTUFacade.recuperarExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício deve ser o exercicio atual.");
        }
        ve.lancarException();
    }

    public void redirecionarParaLista() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public void excluir() {
        exportacaoDebitosIPTUFacade.excluirLinhas(selecionado);
        super.excluir();
    }

    public void verificarCadastros() {
        int erros = exportacaoDebitosIPTUFacade.verificarCadastros(selecionado);
        if (erros > 0) {
            FacesUtil.addAtencao("Existem " + erros + " cadastros com problema!");
        }
    }

    public void iniciarPool() {
        if (serviceExportacao.containsExportacao(selecionado.getId())) {
            FacesUtil.executaJavaScript("wvExportacao.show()");
            FacesUtil.executaJavaScript("pollProgresso.start()");
        }
    }

    public void acompanharGeracaoArquivo() {
        if (isTarefaAgendada) {
            if (!serviceExportacao.containsExportacao(selecionado.getId())) {
                this.canBaixarArquivo = true;
                FacesUtil.executaJavaScript("pollProgresso.stop()");
                FacesUtil.atualizarComponente("formExportacao");
            }
        } else {
            executarPosExportacaoDebitosIPTU();
        }
    }
}
