package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AuditoriaCadastro;
import br.com.webpublico.entidadesauxiliares.AuditoriaCadastroAssistente;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.relatoriofacade.RelatorioAuditoriaCadastroFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by HardRock on 11/04/2017.
 */
@ManagedBean(name = "relatorioAuditoriaCadastroControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-auditoria-cadastro", pattern = "/relatorio-auditoria-cadastro/",
        viewId = "/faces/tributario/relatorio/auditoriacadastro/auditoria-cadastro.xhtml"),
    @URLMapping(id = "relatorio-auditoria-cadastro-dialog", pattern = "/relatorio-auditoria-cadastro-dialog/",
        viewId = "/faces/tributario/relatorio/auditoriacadastro/auditoria-cadastro-dialog.xhtml")
})
public class RelatorioAuditoriaCadastroControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioAuditoriaCadastroControlador.class);
    @EJB
    private RelatorioAuditoriaCadastroFacade facade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    private AuditoriaCadastroAssistente assistente;
    private ConverterAutoComplete converterProprietario;
    private ConverterAutoComplete converterCompromissario;
    private Future<AuditoriaCadastroAssistente> futureQueryRelatorio;
    private Future<Integer> futureTotalRegistros;
    private Future<ByteArrayOutputStream> futureBytesRelatorio;
    private AuditoriaCadastro auditoriaSelecionada;
    private String htmlHistorico;
    private Long idRevisao;
    private BigDecimal CEM = new BigDecimal("100");

    public RelatorioAuditoriaCadastroControlador() {

    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public String getHtmlHistorico() {
        return htmlHistorico;
    }

    public void setHtmlHistorico(String htmlHistorico) {
        this.htmlHistorico = htmlHistorico;
    }

    @URLAction(mappingId = "relatorio-auditoria-cadastro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        assistente = new AuditoriaCadastroAssistente();
        assistente.novo();
        iniciarFutures();
    }

    @URLAction(mappingId = "relatorio-auditoria-cadastro-dialog", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerarDialogParaIframe() {
        assistente = new AuditoriaCadastroAssistente();
        assistente.novo();
        auditoriaSelecionada = (AuditoriaCadastro) Web.pegaDaSessao("auditoriaSelecionada");
        Web.poeNaSessao("auditoriaSelecionada", auditoriaSelecionada);
        if (auditoriaSelecionada.isCadastroImobiliario()) {
            executarDeParaCadastroImobiliario(auditoriaSelecionada);
        }
        if (auditoriaSelecionada.isCadastroEconomico()) {
            executarDeParaCadastroEconomico(auditoriaSelecionada);
        }
        if (auditoriaSelecionada.isCadastroPessoaFisica()) {
            executarDeParaCadastroPessoaFisica(auditoriaSelecionada);
        }
        if (auditoriaSelecionada.isCadastroPessoaJuridica()) {
            executarDeParaCadastroPessoaJuridica(auditoriaSelecionada);
        }
    }

    public void iniciarConsultaRelatorioSintetico() {
        try {
            validarCampos();
            assistente.setMensagem("Executando consulta sintética");
            futureQueryRelatorio = null;
            assistente.setTipoRelatorio(AuditoriaCadastroAssistente.TipoRelatorio.SINTETICO);
            FacesUtil.executaJavaScript("myAguarde.show()");
            if (assistente.isCadastroContribuinte()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroContribuinteSintetico(montarCondicaoContribuinte().toString());
            }
            if (assistente.isCadastroEconomico()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroEconomicoSintetico(montarCondicaoEconomico().toString());
            }
            if (assistente.isCadastroImobiliario()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroImobiliarioSintetico(montarCondicaoImobiliario().toString());
            }
            FacesUtil.executaJavaScript("acompanhaConsultaRelatorio()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        }
    }

    public void iniciarConsultaRelatorioAnalitico() {
        try {
            validarCampos();
            futureQueryRelatorio = null;
            assistente.setMensagem("Executando consulta analítica");
            assistente.setTipoRelatorio(AuditoriaCadastroAssistente.TipoRelatorio.ANALITICO);
            FacesUtil.executaJavaScript("myAguarde.show()");
            if (assistente.isCadastroContribuinte()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroContribuinteAnalitico(montarCondicaoContribuinte().toString());
            }
            if (assistente.isCadastroEconomico()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroEconomicoAnalitico(montarCondicaoEconomico().toString());
            }
            if (assistente.isCadastroImobiliario()) {
                futureQueryRelatorio = facade.buscarAuditoriaCadastroImobiliarioAnalitico(montarCondicaoImobiliario().toString());
            }
            FacesUtil.executaJavaScript("acompanhaConsultaRelatorio()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        }
    }

    public void executarConsulta() {
        try {
            List<AuditoriaCadastro> resultados = executarConsultaPaginada(assistente.getTotalRegistroPorPagina().intValue(), assistente.getIndexPagina());
            validarPesquisa(resultados);
            assistente.setAuditoriaCadastros(resultados);
            assistente.setTamanhoListaPorPagina(assistente.getAuditoriaCadastros().size());
            FacesUtil.atualizarComponente("Formulario:tabelaMovimentos");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private List<AuditoriaCadastro> executarConsultaPaginada(Integer maxResult, Integer firtResult) {
        List<AuditoriaCadastro> resultados = Lists.newArrayList();
        if (assistente.isCadastroContribuinte()) {
            resultados = facade.buscarAuditoriaCadastroContribuinteAnaliticoPaginado(montarCondicaoContribuinte().toString(), maxResult, firtResult);
        }
        if (assistente.isCadastroImobiliario()) {
            resultados = facade.buscarAuditoriaCadastroImobiliarioAnaliticoPaginado(montarCondicaoImobiliario().toString(), maxResult, firtResult);
        }
        if (assistente.isCadastroEconomico()) {
            resultados = facade.buscarAuditoriaCadastroEconomicoAnaliticoPaginado(montarCondicaoEconomico().toString(), maxResult, firtResult);
        }
        return resultados;
    }

    public void pesquisar() {
        try {
            validarCampos();
            assistente.setMensagem("Buscando registro de auditoria");
            iniciarFutures();
            assistente.iniciarAtributosPaginacao();
            FacesUtil.executaJavaScript("reiniciarPaginacao()");
            FacesUtil.executaJavaScript("myAguarde.show()");
            FacesUtil.atualizarComponente("formAguarde");

            if (assistente.isCadastroImobiliario()) {
                futureTotalRegistros = facade.getTotalRegistroAuditoriaCadastroImobiliarioAnalitico(montarCondicaoImobiliario().toString());

            } else if (assistente.isCadastroEconomico()) {
                futureTotalRegistros = facade.getTotalRegistroAuditoriaCadastroEconomicoAnalitico(montarCondicaoEconomico().toString());

            } else if (assistente.isCadastroContribuinte()) {
                futureTotalRegistros = facade.getTotalRegistroAuditoriaCadastroContribuinteAnalitico(montarCondicaoContribuinte().toString());
            }
            FacesUtil.executaJavaScript("acompanhaConsultaPaginada()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            FacesUtil.executaJavaScript("myAguarde.hide()");
        }
    }

    private void iniciarFutures() {
        futureTotalRegistros = null;
        futureQueryRelatorio = null;
        futureBytesRelatorio = null;
    }

    public void finalizarConsultaRelatorio() throws ExecutionException, InterruptedException {
        assistente.setAuditoriaCadastros(futureQueryRelatorio.get().getAuditoriaCadastros());
        iniciarGeracaoRelatorio();
    }

    public void finalizarGeracaoRelatorio() throws ExecutionException, InterruptedException {
        try {
            futureQueryRelatorio = null;
            futureBytesRelatorio.get().close();
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }

    public void finalizarConsultaPaginacao() throws ExecutionException, InterruptedException {
        assistente.setTotalRegistro(futureTotalRegistros.get());
        executarConsulta();
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void consultarAndamentoQueryRelatorio() {
        if (futureQueryRelatorio != null && futureQueryRelatorio.isDone()) {
            FacesUtil.executaJavaScript("terminaConsultaRelatorio()");
        }
    }

    public void consultarAndamentoQueryPaginacao() {
        if (futureTotalRegistros != null && futureTotalRegistros.isDone()) {
            FacesUtil.executaJavaScript("terminaConsultaPaginacao()");
        }
    }

    public void consultarGeracaoRelatorio() {
        if (futureBytesRelatorio != null && futureBytesRelatorio.isDone()) {
            FacesUtil.executaJavaScript("terminaGeracaoRelatorio()");
        }
    }

    public void iniciarGeracaoRelatorio() {
        try {
            futureBytesRelatorio = null;
            assistente.setMensagem("Gerando relatório em PDF");
            String caminhoReport = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/") + "/";
            String caminhoImagem = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img/") + "/";
            String usuario = getNomeUsuarioLogado();
            String tipoRelatorio = assistente.getTipoRelatorio() != null ? assistente.getTipoRelatorio().getDescricao().toUpperCase() : "";
            String arquivoJasper = getArquivoJasper();
            futureBytesRelatorio = facade.gerarRelatorio(usuario, caminhoReport, caminhoImagem, arquivoJasper, tipoRelatorio, assistente.getAuditoriaCadastros());
            FacesUtil.executaJavaScript("acompanhaGeracaoRelatorio()");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("myAguarde.hide()");
        }
    }

    private String getArquivoJasper() {
        if (assistente.isRelatorioSintetico()) {
            return "RelatorioAuditoriaCadastroSintetico.jasper";
        }
        return "RelatorioAuditoriaCadastroAnalitico.jasper";
    }

    public void baixarPDF() {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            response.addHeader("Content-Disposition:", "attachment; filename=AuditoriaCadastro.pdf");
            response.setContentType("application/pdf");
            byte[] bytes = futureBytesRelatorio.get().toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            faces.responseComplete();
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void limparCampos() {
        assistente.novo();
        FacesUtil.executaJavaScript("reiniciarPaginacao()");
    }


    private void validarPesquisa(List<AuditoriaCadastro> list) {
        ValidacaoException ve = new ValidacaoException();
        if (list.isEmpty()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Não foram encontrados registros para os filtros informados.");
            if (assistente.getAuditoriaCadastros() != null) {
                assistente.getAuditoriaCadastros().clear();
            }
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de cadastro deve ser informado.");
        }
        if (assistente.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de revisão inicial deve ser informado.");
        }
        if (assistente.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de revisão final deve ser informado.");
        }
        ve.lancarException();
        if (assistente.getDataFinal().before(assistente.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de revisão final deve ser superior ou igual a data de revisão inicial.");
        }
        ve.lancarException();
    }

    public String getNomeUsuarioLogado() {
        if (facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            return facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return facade.getSistemaFacade().getUsuarioCorrente().getUsername();
        }
    }

    public StringBuilder montarCondicaoContribuinte() {
        StringBuilder filtro = new StringBuilder();
        filtro.append(montarCondicaoAuditoria());
        if (assistente.getPessoa() != null) {
            filtro.append(" and p.id = ").append(assistente.getPessoa().getId());
        }
        return filtro;
    }

    public StringBuilder montarCondicaoImobiliario() {
        StringBuilder filtro = new StringBuilder();
        filtro.append(montarCondicaoAuditoria());

        if (assistente.getSetorInicial() != null && !assistente.getSetorInicial().isEmpty()) {
            filtro.append(" and setor.codigo >= ").append("'" + assistente.getSetorInicial().trim() + "'");
        }
        if (assistente.getSetorFinal() != null && !assistente.getSetorFinal().isEmpty()) {
            filtro.append(" and setor.codigo <= ").append("'" + assistente.getSetorFinal().trim() + "'");
        }
        if (assistente.getQuadraInicial() != null && !assistente.getQuadraInicial().isEmpty()) {
            filtro.append(" and quadra.codigo >= ").append("'" + assistente.getQuadraInicial().trim() + "'");
        }
        if (assistente.getQuadraFinal() != null && !assistente.getQuadraFinal().isEmpty()) {
            filtro.append(" and quadra.codigo <= ").append("'" + assistente.getQuadraFinal().trim() + "'");
        }
        if (assistente.getLoteInicial() != null && !assistente.getLoteInicial().isEmpty()) {
            filtro.append(" and lote.codigoLote >= ").append("'" + assistente.getLoteInicial().trim() + "'");
        }
        if (assistente.getLoteFinal() != null && !assistente.getLoteFinal().isEmpty()) {
            filtro.append(" and lote.codigoLote <= ").append("'" + assistente.getLoteFinal().trim() + "'");
        }
        if (assistente.getInscricaoBciInicial() != null && !assistente.getInscricaoBciInicial().isEmpty()) {
            filtro.append(" and bci.inscricaocadastral >= ").append("'" + assistente.getInscricaoBciInicial().trim() + "'");
        }
        if (assistente.getInscricaoBciFinal() != null && !assistente.getInscricaoBciFinal().isEmpty()) {
            filtro.append(" and bci.inscricaocadastral <= ").append("'" + assistente.getInscricaoBciFinal().trim() + "'");
        }
        if (assistente.getLogradouro() != null) {
            filtro.append(" and log.id = ").append(assistente.getLogradouro().getId());
        }
        if (assistente.getBairro() != null) {
            filtro.append(" and bairro.id = ").append(assistente.getBairro().getId());
        }
        if (assistente.getProprietario() != null) {
            filtro.append(" and prop.id = ").append(assistente.getProprietario().getId());
        }
        if (assistente.getCompromissario() != null) {
            filtro.append(" and cp.id = ").append(assistente.getCompromissario().getId());
        }
        return filtro;
    }

    public StringBuilder montarCondicaoEconomico() {
        StringBuilder filtro = new StringBuilder();
        filtro.append(montarCondicaoAuditoria());

        if (assistente.getInscricaoBceInicial() != null && !assistente.getInscricaoBceInicial().isEmpty()) {
            filtro.append(" and bce.inscricaocadastral >= ").append("'" + assistente.getInscricaoBceInicial().trim() + "'");
        }
        if (assistente.getInscricaoBceInicial() != null && !assistente.getInscricaoBceInicial().isEmpty()) {
            filtro.append(" and bce.inscricaocadastral <= ").append("'" + assistente.getInscricaoBceInicial().trim() + "'");
        }
        if (assistente.getClassificacaoAtividade() != null) {
            filtro.append(" and bce.classificacaoAtividade = ").append("'" + assistente.getClassificacaoAtividade().name() + "'");
        }
        if (assistente.getGrauDeRisco() != null) {
            filtro.append(" and cnae.grauDeRisco = ").append("'" + assistente.getGrauDeRisco().name() + "'");
        }
        if (assistente.getNaturezaJuridica() != null) {
            filtro.append(" and bce.naturezajuridica_id = ").append(assistente.getNaturezaJuridica().getId());
        }
        if (assistente.getTipoAutonomo() != null) {
            filtro.append(" and bce.tipoAutonomo_id = ").append(assistente.getTipoAutonomo().getId());
        }
        if (assistente.getMei()) {
            filtro.append(" and enq.tipoIssqn = '").append(TipoIssqn.MEI.name()).append("'");
        }
        return filtro;
    }

    public StringBuilder montarCondicaoAuditoria() {
        StringBuilder filtro = new StringBuilder();

        filtro.append(" where to_date(to_char(rev.DATAHORA,'dd/MM/yyyy'),'dd/MM/yyyy') between ")
            .append(" to_date('" + DataUtil.getDataFormatada(assistente.getDataInicial()) + "', 'dd/MM/yyyy') and ")
            .append(" to_date('" + DataUtil.getDataFormatada(assistente.getDataFinal()) + "', 'dd/MM/yyyy')");

        if (assistente.getUsuarioSistema() != null) {
            filtro.append(" and rev.usuario = ").append("'" + assistente.getUsuarioSistema().getLogin() + "'");
        }
        if (assistente.getTipoMovimento() != null) {
            filtro.append(" and aud.REVTYPE = ").append(assistente.getTipoMovimento().getValue());
        }
        return filtro;
    }

    public void redirecionarParaCadastro(AuditoriaCadastro aud) {
        Long idMovimento;
        Long idRevisao;
        if (aud != null) {
            idMovimento = aud.getIdMovimento();
            idRevisao = aud.getIdRevisao();
            if (assistente.isCadastroImobiliario()) {
                FacesUtil.redirecionamentoInterno("/cadastro-imobiliario/ver-retroativo/" + idMovimento + "/revisao/" + idRevisao + "/");
            }
            if (assistente.isCadastroEconomico()) {
                FacesUtil.redirecionamentoInterno("/tributario/cadastroeconomico/ver-retroativo/" + idMovimento + "/revisao/" + idRevisao + "/");
            }
            if (assistente.isCadastroContribuinte()) {
                if (aud.isCadastroPessoaFisica()) {
                    FacesUtil.redirecionamentoInterno("/tributario/configuracoes/pessoa/ver-pessoa-fisica-retroativo/" + idMovimento + "/revisao/" + idRevisao + "/");
                } else {
                    FacesUtil.redirecionamentoInterno("/tributario/configuracoes/pessoa/ver-pessoa-juridica-retroativo/" + idMovimento + "/revisao/" + idRevisao + "/");
                }
            }
        }
    }

    public void limparCamposPorTipoCadastro() {
        if (assistente.isCadastroImobiliario()) {
            assistente.iniciarVariaveisCadastroContribuinte();
            assistente.iniciarVariaveisCadastroEconomico();
        }
        if (assistente.isCadastroEconomico()) {
            assistente.iniciarVariaveisCadastroContribuinte();
            assistente.iniciarVariaveisCadastroImobiliario();
        } else {
            assistente.iniciarVariaveisCadastroImobiliario();
            assistente.iniciarVariaveisCadastroEconomico();
        }
    }

    public ConverterAutoComplete getConverterProprietario() {
        if (converterProprietario == null) {
            converterProprietario = new ConverterAutoComplete(Propriedade.class, facade.getPropriedadeFacade());
        }
        return converterProprietario;
    }

    public ConverterAutoComplete getConverterCompromissario() {
        if (converterCompromissario == null) {
            converterCompromissario = new ConverterAutoComplete(Compromissario.class, facade.getCompromissarioFacade());
        }
        return converterCompromissario;
    }

    public List<Logradouro> completarLogradouro(String parte) {
        if (assistente.getBairro() != null) {
            return facade.getLogradouroFacade().completaLogradourosPorBairro(parte.trim(), assistente.getBairro());
        }
        return facade.getLogradouroFacade().listaFiltrando(parte.trim(), "nome", "nomeUsual", "nomeAntigo");
    }

    public List<Bairro> completarBairro(String parte) {
        return facade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<Propriedade> completarProprietario(String parte) {
        return facade.getPropriedadeFacade().buscarFiltrandoPropriedade(parte.trim());
    }

    public List<Compromissario> completarCompromissario(String parte) {
        return facade.getCompromissarioFacade().buscarFiltrandoCompromissarioPorNome(parte.trim());
    }

    public List<SelectItem> listarTiposAutonomos() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, "Todos..."));
        for (TipoAutonomo tipo : facade.getTipoAutonomoFacade().lista()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<UsuarioSistema> completarUsuarioSistema(String parte) {
        return facade.getUsuarioSistemaFacade().buscarTodosUsuariosPorLoginOuNomeOuCpf(parte.trim());
    }

    public List<SelectItem> getClassificacoesAtividades() {
        return Util.getListSelectItem(ClassificacaoAtividade.values());
    }

    public List<SelectItem> getGrausDeRisco() {
        return Util.getListSelectItem(GrauDeRiscoDTO.values());
    }

    public List<SelectItem> getNaturezasJuridica() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, "Todas..."));
        for (NaturezaJuridica natureza : facade.getNaturezaJuridicaFacade().buscarNaturezasJuridicasAtivas()) {
            toReturn.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toRetun = new ArrayList<>();
        toRetun.add(new SelectItem(null, " "));
        toRetun.add(new SelectItem(TipoCadastroTributario.IMOBILIARIO, TipoCadastroTributario.IMOBILIARIO.getDescricao()));
        toRetun.add(new SelectItem(TipoCadastroTributario.ECONOMICO, TipoCadastroTributario.ECONOMICO.getDescricao()));
        toRetun.add(new SelectItem(TipoCadastroTributario.PESSOA, TipoCadastroTributario.PESSOA.getDescricao()));
        return toRetun;
    }

    public List<SelectItem> getTiposMovimentos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos..."));
        for (AuditoriaCadastroAssistente.TipoMovimento obj : AuditoriaCadastroAssistente.TipoMovimento.values()) {
            toReturn.add(new SelectItem(obj, obj.getValue() + " - " + obj.getDescricao()));
        }
        return toReturn;
    }

    public void selecionarRevisao(AuditoriaCadastro aud) {
        if (aud != null) {
            auditoriaSelecionada = aud;
            Web.poeNaSessao("auditoriaSelecionada", auditoriaSelecionada);
            FacesUtil.executaJavaScript(" $(\"#dadosDialog\").load(\"/relatorio-auditoria-cadastro-dialog/\", ()=>{aguarde.hide();dlgHistorico.show();});");
        }
    }

    private void executarDeParaCadastroEconomico(AuditoriaCadastro audAtual) {
        try {
            CadastroEconomico bceAtual = facade.getCadastroEconomicoFacade().recuperarPorRevisao(audAtual.getIdMovimento(), audAtual.getIdRevisao());
            if (bceAtual != null) {
                assistente.setBceRevisaoAtual(bceAtual);
                assistente.getBceRevisaoAtual().setEconomicoCNAE(assistente.getBceRevisaoAtual().getEconomicoCNAEVigentesNaData(audAtual.getDataRevisao()));

                RevisaoAuditoria revAnterior = facade.getAuditoriaFacade().buscarRevisaoAuditoriaAnterior(
                    audAtual.getDataRevisao(),
                    audAtual.getIdMovimento(),
                    CadastroEconomico.class.getSimpleName());

                if (revAnterior != null) {
                    CadastroEconomico bceAnterior = facade.getCadastroEconomicoFacade().recuperarPorRevisao(audAtual.getIdMovimento(), revAnterior.getId());
                    if (bceAnterior != null) {
                        assistente.setBceRevisaoAnterior(bceAnterior);
                        assistente.getBceRevisaoAnterior().setEconomicoCNAE(assistente.getBceRevisaoAnterior().getEconomicoCNAEVigentesNaData(audAtual.getDataRevisao()));
                    }
                }
            }
        } catch (EJBException ej) {
            if (ej.getCausedByException() instanceof EntityNotFoundException) {
                logger.error("Erro EJBException ao inicializar lista hibernate na revisão " + audAtual.getIdRevisao() + ": {}", ej);
                facade.getAuditoriaFacade().criarAuditoriaFromEntityNotFound(ej.getCausedByException().getMessage());
                executarDeParaCadastroEconomico(audAtual);
            }
        } catch (EntityNotFoundException e) {
            logger.error("Erro EntityNotFoundException ao inicializar lista hibernate na revisão " + audAtual.getIdRevisao() + ": {}", e);
            facade.getAuditoriaFacade().criarAuditoriaFromEntityNotFound(e.getMessage());
            executarDeParaCadastroEconomico(audAtual);
        }
    }

    private void executarDeParaCadastroImobiliario(AuditoriaCadastro audAtual) {
        CadastroImobiliario bciAtual = facade.getCadastroImobiliarioFacade().recuperarPorRevisao(audAtual.getIdMovimento(), audAtual.getIdRevisao());
        if (bciAtual != null) {
            assistente.setBciRevisaoAtual(bciAtual);
            assistente.getBciRevisaoAtual().setPropriedades(assistente.getBciRevisaoAtual().getPropriedadeVigente(audAtual.getDataRevisao()));
            assistente.setPropriedadesHistoricoAtual(assistente.getBciRevisaoAtual().getPropriedadeHistorico(audAtual.getDataRevisao()));
            assistente.getBciRevisaoAtual().setListaCompromissarios(assistente.getBciRevisaoAtual().getCompromissarioVigente(audAtual.getDataRevisao()));

            RevisaoAuditoria revAnterior = facade.getAuditoriaFacade().buscarRevisaoAuditoriaAnterior(audAtual.getDataRevisao(), audAtual.getIdMovimento(), CadastroImobiliario.class.getSimpleName());
            if (revAnterior != null) {
                CadastroImobiliario bciAnterior = facade.getCadastroImobiliarioFacade().recuperarPorRevisao(audAtual.getIdMovimento(), revAnterior.getId());
                if (bciAnterior != null) {
                    assistente.setBciRevisaoAnterior(bciAnterior);
                    assistente.getBciRevisaoAnterior().setPropriedades(assistente.getBciRevisaoAnterior().getPropriedadeVigente(revAnterior.getDataHora()));
                    assistente.setPropriedadesHistoricoAnterior(assistente.getBciRevisaoAnterior().getPropriedadeHistorico(revAnterior.getDataHora()));
                    assistente.getBciRevisaoAnterior().setListaCompromissarios(assistente.getBciRevisaoAnterior().getCompromissarioVigente(revAnterior.getDataHora()));
                }
            }
        }
    }

    private void executarDeParaCadastroPessoaFisica(AuditoriaCadastro audAtual) {
        PessoaFisica pfAtual = facade.getPessoaFacade().recuperarPessoaFisicaPorRevisao(audAtual.getIdMovimento(), audAtual.getIdRevisao());
        if (pfAtual != null) {
            assistente.setPessoaFisicaAtual(pfAtual);
            assistente.setRgAtual(assistente.getPessoaFisicaAtual().getRg());

            RevisaoAuditoria revAnterior = facade.getAuditoriaFacade().buscarRevisaoAuditoriaAnterior(
                audAtual.getDataRevisao(),
                audAtual.getIdMovimento(),
                PessoaFisica.class.getSimpleName());
            if (revAnterior != null) {
                PessoaFisica pfAnterior = facade.getPessoaFacade().recuperarPessoaFisicaPorRevisao(audAtual.getIdMovimento(), revAnterior.getId());
                if (pfAnterior != null) {
                    assistente.setPessoaFisicaAnterior(pfAnterior);
                    assistente.setRgAnterior(assistente.getPessoaFisicaAnterior().getRg());
                }
            }
        }
    }

    private void executarDeParaCadastroPessoaJuridica(AuditoriaCadastro revAtual) {
        PessoaJuridica pjAtual = facade.getPessoaFacade().recuperarPessoaJuridicaPorRevisao(revAtual.getIdMovimento(), revAtual.getIdRevisao());
        if (pjAtual != null) {
            assistente.setPessoaJuridicaAtual(pjAtual);

            RevisaoAuditoria revAnterior = facade.getAuditoriaFacade().buscarRevisaoAuditoriaAnterior(
                revAtual.getDataRevisao(),
                revAtual.getIdMovimento(),
                PessoaJuridica.class.getSimpleName());
            if (revAnterior != null) {
                PessoaJuridica pjAnterior = facade.getPessoaFacade().recuperarPessoaJuridicaPorRevisao(revAtual.getIdMovimento(), revAnterior.getId());
                if (pjAnterior != null) {
                    assistente.setPessoaJuridicaAnterior(pjAnterior);
                }
            }
        }
    }

    public boolean renderizarPanelCadastroImobiliario() {
        return assistente != null && assistente.isCadastroImobiliario();
    }

    public boolean renderizarPanelCadastroEconomico() {
        return assistente != null && assistente.isCadastroEconomico();
    }

    public boolean renderizarPanelCadastroContribuinte() {
        return assistente != null && assistente.isCadastroContribuinte();
    }

    public boolean renderizarCadastroImobiliario() {
        return auditoriaSelecionada != null && auditoriaSelecionada.isCadastroImobiliario();
    }

    public boolean renderizarCadastroEconomico() {
        return auditoriaSelecionada != null && auditoriaSelecionada.isCadastroEconomico();
    }

    public boolean renderizarCadastroPessoaFisica() {
        return auditoriaSelecionada != null && auditoriaSelecionada.isCadastroPessoaFisica();
    }

    public boolean renderizarCadastroPessoaJuridica() {
        return auditoriaSelecionada != null && auditoriaSelecionada.isCadastroPessoaJuridica();
    }

    public AuditoriaCadastroAssistente getAssistente() {
        return assistente;
    }

    public void setAssistente(AuditoriaCadastroAssistente assistente) {
        this.assistente = assistente;
    }

    public AuditoriaCadastro getAuditoriaSelecionada() {
        return auditoriaSelecionada;
    }

    public void setAuditoriaSelecionada(AuditoriaCadastro auditoriaSelecionada) {
        this.auditoriaSelecionada = auditoriaSelecionada;
    }

    public String getStyleCampoTipoCadastro() {
        if (assistente.isCadastroEconomico()) {
            return "margin-right: 30px";
        }
        return "margin-right: 10px";
    }

    public String getStyleCampoDataRevisao() {
        if (assistente.isCadastroEconomico()) {
            return "color: grey; margin-left: 20px";
        }
        return "color: grey";
    }

    public String getStyleCampoUsuario() {
        if (assistente.isCadastroEconomico()) {
            return "margin-right: 70px";
        }
        return " ";
    }

    public String getStyleCampoTipoMovimento() {
        if (assistente.isCadastroEconomico()) {
            return "margin-right: 20px";
        }
        return " ";
    }

    private String gerarHtmlCabecalho() {
        String html = "<table style=\"width: 100%;\">\n" +
            "<tbody>\n" +
            "<tr>\n" +
            "<td style=\"text-align: left;\" align=\"right\"><span style=\"font-family: arial, helvetica, sans-serif;\"><img src=\"../../../img/Brasao_de_Rio_Branco.gif\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" width=\"73\" height=\"90\" /></span></td>\n" +
            "<td><span style=\"font-family: arial, helvetica, sans-serif;\"><strong><span style=\"font-size: large;\">PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO</span></strong></span><br /><span style=\"font-family: arial, helvetica, sans-serif;\"><strong><span style=\"font-size: medium;\">Secretaria da Fazenda<br/></span><span style=\"font-size: medium;\">Histórico e Auditoria dos Cadastros</span></strong></span></td>\n" +
            "</tr>\n" +
            "</tbody>\n" +
            "</table>";
        return html;
    }

    public void gerarPdfDoHistorico() {
        documentoOficialFacade.emiteDocumentoOficial(gerarHtmlCabecalho() + htmlHistorico);
    }

    public BigDecimal getCEM() {
        return CEM;
    }
}
