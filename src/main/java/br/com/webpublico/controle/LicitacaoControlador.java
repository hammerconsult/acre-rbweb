/*
 * Codigo gerado automaticamente em Thu Dec 01 17:13:45 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.FieldConsultaEntidade;
import br.com.webpublico.consultaentidade.FiltroConsultaEntidade;
import br.com.webpublico.consultaentidade.TipoAlinhamento;
import br.com.webpublico.consultaentidade.TipoCampo;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.administrativo.LicitacaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "licitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-licitacao", pattern = "/licitacao/novo/", viewId = "/faces/administrativo/licitacao/licitacao/edita.xhtml"),
    @URLMapping(id = "editar-licitacao", pattern = "/licitacao/editar/#{licitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/licitacao/edita.xhtml"),
    @URLMapping(id = "ver-licitacao", pattern = "/licitacao/ver/#{licitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/licitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-licitacao", pattern = "/licitacao/listar/", viewId = "/faces/administrativo/licitacao/licitacao/lista.xhtml")
})
public class LicitacaoControlador extends PrettyControlador<Licitacao> implements Serializable, CRUD {

    public static final int DOIS = 2;
    public static final int TRES = 3;
    private static final Logger logger = LoggerFactory.getLogger(LicitacaoControlador.class);
    @EJB
    private LicitacaoFacade licitacaoFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterLicitacao;
    private DoctoLicitacao doctoLicitacaoSelecionado;
    private DoctoHabLicitacao doctoHabLicitacaoSelecionado;
    private List<MembroComissao> membrosNaoExonerados;
    private List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir;
    private MoneyConverter moneyConverter;
    private PublicacaoLicitacao publicacaoLicitacaoSelecionada;
    private StatusLicitacao statusLicitacaoSelecionado;
    private SolicitacaoMaterial solicitacao;
    private DoctoHabilitacao[] doctoHabilitacaos;
    private ObjetoCompra objetoCompraSelecionado;
    private ItemSolicitacao itemSolicitacaoEdicao;
    private List<LicitacaoVO> licitacoesEmAndamento;
    private List<LicitacaoVO> licitacoesAdjudicadas;
    private List<LicitacaoVO> licitacoesHomologadas;
    private List<LicitacaoVO> licitacoesAnuladas;
    private List<LicitacaoVO> licitacoesCanceladas;
    private List<LicitacaoVO> licitacoesDesertas;
    private List<LicitacaoVO> licitacoesEmRecurso;
    private List<LicitacaoVO> licitacoesProrrogadas;
    private List<LicitacaoVO> licitacoesFracassadas;
    private List<LicitacaoVO> licitacoesRevogadas;
    private List<LicitacaoVO> licitacoesSuspensas;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;
    private FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso;

    public LicitacaoControlador() {
        super(Licitacao.class);
    }


    public void parametrosIniciaisAoSelecionar() {
        recuperarItensELotesProcessoDeCompra();
        membrosNaoExonerados = null;
    }

    private void parametrosSolicitacao() {
        solicitacao = licitacaoFacade.recuperarSolicitacaoProcessoDeCompra(selecionado.getProcessoDeCompra());
        selecionado.setModalidadeLicitacao(solicitacao.getModalidadeLicitacao());

        if (solicitacao.getTipoNaturezaDoProcedimento() != null) {
            selecionado.setNaturezaDoProcedimento(solicitacao.getTipoNaturezaDoProcedimento());
        }
        selecionado.setNaturezaDoProcedimento(solicitacao.getTipoNaturezaDoProcedimento());
        selecionado.setTipoApuracao(solicitacao.getTipoApuracao());
        selecionado.setTipoAvaliacao(solicitacao.getTipoAvaliacao());
        selecionado.setPeriodoDeExecucao(solicitacao.getPrazoExecucao());
        selecionado.setLocalDeEntrega(solicitacao.getLocalDeEntrega());
        selecionado.setPrazoDeVigencia(solicitacao.getVigencia());
        selecionado.setFormaDePagamento(solicitacao.getFormaDePagamento());
        if (Operacoes.NOVO.equals(getOperacao())) {
            selecionado.setTipoPrazoExecucao(solicitacao.getTipoPrazoDeExecucao());
            selecionado.setTipoPrazoVigencia(solicitacao.getTipoPrazoDeVigencia());
        }
    }

    private void setarProcessoDeCompraAndValidar() {
        recuperarItensELotesProcessoDeCompra();
        selecionado.setValorMaximo(recuperarValorTotalPDC());
        verificaLicitacaoExclusivaMicroPequenaEmpresa();
        parametrosSolicitacao();
        setUnidadeAdministrativaProcessoCompra();
    }

    public void setUnidadeAdministrativaProcessoCompra() {
        selecionado.setUnidadeAdministrativa(licitacaoFacade.getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getProcessoDeCompra().getUnidadeOrganizacional(),
            licitacaoFacade.getSistemaFacade().getDataOperacao()
        ));
    }

    @Override
    public void salvar() {
        try {
            if (!Util.isStringNulaOuVazia(selecionado.getLinkSistemaOrigem())) {
                validarLinkSistemaOrigem();
            }
            licitacaoFacade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            selecionado = licitacaoFacade.salvarLicitacao(selecionado);
            FacesUtil.addOperacaoRealizada("A licitação " + selecionado + " foi salvo com sucesso.");
            FacesUtil.redirecionamentoInterno("/licitacao/ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void excluir() {
        if (licitacaoFacade.validaExclusao(selecionado)) {
            super.excluir();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível excluir.", "Esta licitação encontra-se vinculada a uma ou mais propostas de fornecedor."));
        }
    }

    public void validarLinkSistemaOrigem() {
        ValidacaoException ve = new ValidacaoException();
        String exceptionMsg = "Link do Sistema de Origem é inválido.";
        String exemplo = "https://www.exemplo.com";
        try {
            URI uri = new URI(selecionado.getLinkSistemaOrigem());
            URL url = uri.toURL();
            if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Protocolo inválido.");
            }
        } catch (MalformedURLException e) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(exceptionMsg + " São suportados apenas protocolos HTTP ou HTTPS. Exemplo: " + exemplo);
        } catch (URISyntaxException e) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(exceptionMsg + " Verifique a presença de caracteres não permitidos em URLs. A URL deve seguir o padrão " + exemplo);
        } catch (Exception e) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(exceptionMsg + " Informe uma URL no padrão " + exemplo);
        }
        ve.lancarException();
    }

    public void redirecionarParaLicitacao(Licitacao licitacao) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + licitacao.getId() + "/");
    }

    public List<ItemProcessoDeCompra> getListaDeItemProcessoDeCompraParaExibir() {
        return listaDeItemProcessoDeCompraParaExibir;
    }

    public void setListaDeItemProcessoDeCompraParaExibir(List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        this.listaDeItemProcessoDeCompraParaExibir = listaDeItemProcessoDeCompraParaExibir;
    }

    public PublicacaoLicitacao getPublicacaoLicitacaoSelecionada() {
        return publicacaoLicitacaoSelecionada;
    }

    public void setPublicacaoLicitacaoSelecionada(PublicacaoLicitacao publicacaoLicitacaoSelecionada) {
        this.publicacaoLicitacaoSelecionada = publicacaoLicitacaoSelecionada;
    }

    public StatusLicitacao getStatusLicitacaoSelecionado() {
        return statusLicitacaoSelecionado;
    }

    public void setStatusLicitacaoSelecionado(StatusLicitacao statusLicitacaoSelecionado) {
        this.statusLicitacaoSelecionado = statusLicitacaoSelecionado;
    }

    public LicitacaoFacade getFacade() {
        return licitacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return licitacaoFacade;
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeLicitacao.getModalidadesLicitacao());
    }

    public List<SelectItem> getTiposDeAvaliacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao : TipoAvaliacaoLicitacao.values()) {
            retorno.add(new SelectItem(tipoAvaliacaoLicitacao, tipoAvaliacaoLicitacao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getRegimeDeExecucao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (RegimeDeExecucao object : RegimeDeExecucao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<Comissao> completaComissao(String parte) {
        return licitacaoFacade.getComissaoFacade().buscarComissaoVigente(parte.trim());
    }

    public List<VeiculoDePublicacao> completaVeiculoPublicacao(String parte) {
        return licitacaoFacade.getVeiculoDePublicacaoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public DoctoHabLicitacao getDoctoHabLicitacaoSelecionado() {
        return doctoHabLicitacaoSelecionado;
    }

    public void setDoctoHabLicitacaoSelecionado(DoctoHabLicitacao doctoHabLicitacaoSelecionado) {
        this.doctoHabLicitacaoSelecionado = doctoHabLicitacaoSelecionado;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setMoneyConverter(MoneyConverter moneyConverter) {
        this.moneyConverter = moneyConverter;
    }

    public void alterarPublicacao(PublicacaoLicitacao publicacao) {
        this.publicacaoLicitacaoSelecionada = (PublicacaoLicitacao) Util.clonarObjeto(publicacao);
    }

    public void removerPublicacao(PublicacaoLicitacao publicacao) {
        selecionado.getPublicacoes().remove(publicacao);
    }

    public boolean desabilitarBotaoExcluirPublicacao(PublicacaoLicitacao pl) {
        if (selecionado.isHomologada()) {
            if (!pl.isAdjudicacao() && !pl.isHomolocacao()) {
                return true;
            }
        }
        return false;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, licitacaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return licitacaoFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<Pessoa> completaPessoaJuridica(String parte) {
        return licitacaoFacade.getPessoaFacade().listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return licitacaoFacade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    private void recuperarItensELotesProcessoDeCompra() {
        if (selecionado.getProcessoDeCompra() != null) {
            selecionado.getProcessoDeCompra().setLotesProcessoDeCompra(licitacaoFacade.getProcessoDeCompraFacade().recuperarLotesProcesso(selecionado.getProcessoDeCompra()));
            ordenaLotesEItensDoProcessoEPreencheLista(selecionado.getProcessoDeCompra());
            atribuirGrupoContaDespesa();
        }
    }

    private void atribuirGrupoContaDespesa() {
        for (ItemProcessoDeCompra item : listaDeItemProcessoDeCompraParaExibir) {
            item.getObjetoCompra().setGrupoContaDespesa(licitacaoFacade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    public boolean temParecerDoTipoJuridicoEdital() {
        return licitacaoFacade.getParecerLicitacaoFacade().recuperarParecerDoTipoJuridicoEdital(selecionado);
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return licitacaoFacade.buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(parte.trim());
    }

    public List<Licitacao> buscarLicitacoesPorDescricaoOrNumeroOrExercicio(String parte) {
        return licitacaoFacade.buscarLicitacoes(parte.trim());
    }

    public DoctoLicitacao getDoctoLicitacao() {
        return doctoLicitacaoSelecionado;
    }

    public void setDoctoLicitacao(DoctoLicitacao doctoLicitacaoSelecionado) {
        this.doctoLicitacaoSelecionado = doctoLicitacaoSelecionado;
    }

    public void geraPDF(ModeloDocto modelo) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline;filename=" + modelo.getNome() + ".pdf");
            response.setCharacterEncoding("UTF-8");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
                + " <html>"
                + " <head>"
                + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
                + " <title>"
                + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">"
                + modelo.getNome()
                + " </title>"
                + " </head>"
                + " <body>"
                + modelo.getModelo()
                + " </body>"
                + " </html>", baos);
            byte[] bytes = baos.toByteArray();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            facesContext.responseComplete();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


    public void confirmarDocumentoHab() {
        if (validaDocumentoHabilitacao()) {
            if (selecionado.getDocumentosProcesso().isEmpty()) {
                for (DoctoHabilitacao doctoHabilitacao : doctoHabilitacaos) {
                    doctoHabLicitacaoSelecionado = new DoctoHabLicitacao();
                    selecionado.getListaDeDoctoHabilitacao().add(doctoHabilitacao);
                    doctoHabLicitacaoSelecionado.setDoctoHabilitacao(doctoHabilitacao);
                    doctoHabLicitacaoSelecionado.setLicitacao(selecionado);
                    selecionado.getDocumentosProcesso().add(doctoHabLicitacaoSelecionado);
                }
            } else {
                selecionado.getDocumentosProcesso().clear();
                for (DoctoHabilitacao doctoHabilitacao : doctoHabilitacaos) {
                    doctoHabLicitacaoSelecionado = new DoctoHabLicitacao();
                    selecionado.getListaDeDoctoHabilitacao().add(doctoHabilitacao);
                    doctoHabLicitacaoSelecionado.setDoctoHabilitacao(doctoHabilitacao);
                    doctoHabLicitacaoSelecionado.setLicitacao(selecionado);
                    selecionado.getDocumentosProcesso().add(doctoHabLicitacaoSelecionado);
                }
            }
            cancelarDoctoHabLicitacaoSelecionado();
        }
    }

    private boolean validaDocumentoHabilitacao() {
        if (documentoHabilitacaoIsNull()) {
            return false;
        }

//        if (documentoHabilitacaoJaFoiAdicionado()) {
//            return false;
//        }

        return true;
    }

    private boolean documentoHabilitacaoIsNull() {
        if (doctoHabilitacaos == null) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório selecionar um documento.");
            return true;
        }
        return false;
    }

    public void removerDocumentoHab(DoctoHabLicitacao doc) {
        selecionado.getDocumentosProcesso().remove(doc);
    }

    public void validarDataDePublicacao() {
        ValidacaoException ve = new ValidacaoException();
        Boolean validou = Boolean.TRUE;
        if (publicacaoLicitacaoSelecionada.getDataPublicacao().compareTo(selecionado.getEmitidaEm()) < 0) {
            publicacaoLicitacaoSelecionada.setDataPublicacao(null);
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data da publicação deve ser igual ou posterior a data de emissão do edital.");
            validou = Boolean.FALSE;
        }

        if (validou) {
            for (ParecerLicitacao parecerLicitacao : selecionado.getPareceres()) {
                if (TipoParecerLicitacao.JURIDICO_EDITAL.equals(parecerLicitacao.getTipoParecerLicitacao())) {
                    if (publicacaoLicitacaoSelecionada.getDataPublicacao().compareTo(parecerLicitacao.getDataParecer()) < 0) {
                        publicacaoLicitacaoSelecionada.setDataPublicacao(null);
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A data de publicação deve ser igual ou posterior a data do parecer jurídico - edital.");
                    }
                }
            }
        }

        ve.lancarException();
    }

    public void selecionaDataEmitidaEm(DateSelectEvent d) {
        selecionado.setEmitidaEm(d.getDate());
    }

    public void validacoesDoProcessoDeCompraSelecionado() {
        Licitacao lic = licitacaoFacade.recuperarLicitacaoDoProcessoCompra(selecionado.getProcessoDeCompra());
        if (lic != null) {
            lic.setListaDeStatusLicitacao(licitacaoFacade.recuperarListaDeStatus(lic));
            StatusLicitacao ultimoStatus = recuperarUltimoStatusDaLicitacao(lic);

            if (selecionado.isStatusLicitacaoAndamento(ultimoStatus) || selecionado.isStatusLicitacaoHomologada(ultimoStatus) ||
                selecionado.isStatusLicitacaoAdjucada(ultimoStatus) || selecionado.isStatusLicitacaoEmRecurso(ultimoStatus)) {
                FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Processo de compra inválido.", "Este processo de compra possui vínculo com outra licitação."));
                selecionado.setProcessoDeCompra(null);
            } else {
                if (selecionado.isStatusLicitacaoFracassada(ultimoStatus)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_INFO, "Processo de compra reaproveitado.", "Este processo de compra já passou por uma licitação fracassada."));
                }

                if (selecionado.isStatusLicitacaoRevogada(ultimoStatus)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_INFO, "Processo de compra reaproveitado.", "Este processo de compra já passou por uma licitação revogada."));
                }

                if (selecionado.isStatusLicitacaoCancelada(ultimoStatus)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_INFO, "Processo de compra reaproveitado.", "Este processo de compra já passou por uma licitação cancelada."));
                }

                if (selecionado.isStatusLicitacaoAnulada(ultimoStatus)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_INFO, "Processo de compra reaproveitado.", "Este processo de compra já passou por uma licitação anulada."));
                }

                if (selecionado.isStatusLicitacaoDeserta(ultimoStatus)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra", new FacesMessage(FacesMessage.SEVERITY_INFO, "Processo de compra reaproveitado.", "Este processo de compra já passou por uma licitação deserta."));
                }
                setarProcessoDeCompraAndValidar();
            }
        } else {
            setarProcessoDeCompraAndValidar();
        }
    }

    public void verificaLicitacaoExclusivaMicroPequenaEmpresa() {
        List<LoteProcessoDeCompra> lotes = licitacaoFacade.buscarLotesExclusivoMicroPequenaEmpresa(selecionado.getProcessoDeCompra().getId());
        if (!lotes.isEmpty()) {
            BigDecimal valorTotalLote = BigDecimal.ZERO;
            for (LoteProcessoDeCompra lote : lotes) {
                valorTotalLote = valorTotalLote.add(lote.getValor());
            }

            BigDecimal limiteValor = new BigDecimal(80000);
            if (valorTotalLote.compareTo(limiteValor) > 0) {
                FacesContext.getCurrentInstance().addMessage("Formulario:processoCompra",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Processo de compra inválido.",
                        "Este processo de compra possui valor acima de R$ " + limiteValor + "."));

                listaDeItemProcessoDeCompraParaExibir.clear();
            }
        }
    }

    public BigDecimal recuperarValorTotalPDC() {
        BigDecimal acumulado = BigDecimal.ZERO;
        if (selecionado.getProcessoDeCompra() != null) {
            for (LoteProcessoDeCompra loteProcessoDeCompra : selecionado.getProcessoDeCompra().getLotesProcessoDeCompra()) {
                acumulado = acumulado.add(loteProcessoDeCompra.getValor());
            }
        }
        return acumulado;
    }

    public void ordenaLotesEItensDoProcessoEPreencheLista(ProcessoDeCompra pdc) {
        listaDeItemProcessoDeCompraParaExibir = licitacaoFacade.ordenaLotesEItensDoProcessoEPreencheLista(pdc);
    }

    private StatusLicitacao recuperarUltimoStatusDaLicitacao(Licitacao lic) {
        return lic.recuperarUltimoStatusDaLicitacao();
    }

    public void cancelarPublicacaoSelecionada() {
        this.publicacaoLicitacaoSelecionada = null;
    }

    public void novaPublicacao() {
        this.publicacaoLicitacaoSelecionada = new PublicacaoLicitacao();
        this.publicacaoLicitacaoSelecionada.setLicitacao(selecionado);
    }

    public void confirmarPublicacao() {
        try {
            validarAdicaoPublicacao();
            adicionarSobrescreverPublicacao();
            cancelarPublicacaoSelecionada();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicaoPublicacao() {
        ValidacaoException ve = new ValidacaoException();

        validarDataDePublicacao();

        validarTipoDaPublicacaoParaLicitacaoHomologada();

        ve.lancarException();
    }

    private void validarTipoDaPublicacaoParaLicitacaoHomologada() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isHomologada()) {
            if (!selecionado.temEstaPublicacaoAdicionada(publicacaoLicitacaoSelecionada)) {
                if (!publicacaoLicitacaoSelecionada.isAdjudicacao() && !publicacaoLicitacaoSelecionada.isHomolocacao()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Para licitação " +
                        selecionado.getStatusAtual().getTipoSituacaoLicitacao().getDescricao()
                        + " somente é permitido adicionar nova publicação do tipo "
                        + TipoPublicacaoLicitacao.ADJUDICACAO.getDescricao()
                        + " ou " + TipoPublicacaoLicitacao.HOMOLOGACAO.getDescricao());
                }
            }
        }
    }

    private void adicionarSobrescreverPublicacao() {
        selecionado.setPublicacoes(Util.adicionarObjetoEmLista(selecionado.getPublicacoes(), publicacaoLicitacaoSelecionada));
    }

    public boolean fornecedorPessoaJuridica(LicitacaoFornecedor licForn) {
        return licitacaoFacade.getDocumentoLicitacaoFacade().fornecedorPessoaJuridica(licForn);
    }

    public void novoDoctoHabLicitacaoSelecionado() {
        if (podeAdicionarDocumentosObrigatorios()) {
            this.doctoHabLicitacaoSelecionado = new DoctoHabLicitacao();
            this.doctoHabLicitacaoSelecionado.setLicitacao(selecionado);
        }
    }

    public void novoDoctoLicitacaoSelecionado() {
        this.doctoLicitacaoSelecionado = new DoctoLicitacao();
        this.doctoLicitacaoSelecionado.setLicitacao(selecionado);
    }

    public void cancelarDoctoHabLicitacaoSelecionado() {
        this.doctoHabLicitacaoSelecionado = null;
    }

    public void cancelarDoctoLicitacaoSelecionado() {
        this.doctoLicitacaoSelecionado = null;
    }

    private Boolean podeAdicionarDocumentosObrigatorios() {
        try {
            if (selecionado.getFornecedores().size() > 0) {
                FacesUtil.addOperacaoNaoPermitida("Já existem fornecedores cadastrados nesta licitação.");
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.TRUE;
        }
    }

    public boolean licitacaoHomologada() {
        return selecionado.isStatusLicitacaoHomologada(selecionado.getStatusAtualLicitacao());
    }


    private Boolean validaSePodeGerarEdital() {
        return Util.validaCampos(selecionado);
    }


    public void removerMembrosExonerados(SelectEvent event) {
        membrosNaoExonerados = null;
    }

    public List<MembroComissao> getMembrosNaoExonerados() {
        if (membrosNaoExonerados == null) {
            if (selecionado.getComissao() == null || selecionado.getComissao().getMembroComissao() == null) {
                return membrosNaoExonerados;
            }

            membrosNaoExonerados = new ArrayList<>();
            for (MembroComissao membroComissao : selecionado.getComissao().getMembroComissao()) {
                if (membroComissao.getExoneradoEm() == null) {
                    membrosNaoExonerados.add(membroComissao);
                }
            }
        }
        return membrosNaoExonerados;
    }

    public void setMembrosNaoExonerados(List<MembroComissao> membrosNaoExonerados) {
        this.membrosNaoExonerados = membrosNaoExonerados;
    }

    public void adicionarMembroNestaLicitacao(MembroComissao membroComissao) {
        LicitacaoMembroComissao membroToAdd = new LicitacaoMembroComissao(selecionado, membroComissao);
        selecionado.setListaDeLicitacaoMembroComissao(Util.adicionarObjetoEmLista(selecionado.getListaDeLicitacaoMembroComissao(), membroToAdd));
    }

    public void removerMembroDestaLicitacao(MembroComissao membroComissao) {
        LicitacaoMembroComissao membroToRemove = null;
        for (LicitacaoMembroComissao licitacaoMembroComissao : selecionado.getListaDeLicitacaoMembroComissao()) {
            if (licitacaoMembroComissao.getMembroComissao().equals(membroComissao)) {
                membroToRemove = licitacaoMembroComissao;
            }
        }
        if (membroToRemove != null) {
            selecionado.getListaDeLicitacaoMembroComissao().remove(membroToRemove);
        }
    }

    public ConverterAutoComplete getConverterLicitacao() {
        if (converterLicitacao == null) {
            converterLicitacao = new ConverterAutoComplete(Licitacao.class, licitacaoFacade);
        }
        return converterLicitacao;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    @URLAction(mappingId = "novo-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciaisDoStatusLicitacaoSelecionado();
        listaDeItemProcessoDeCompraParaExibir = new ArrayList<>();
        selecionado.setEmitidaEm(licitacaoFacade.getSistemaFacade().getDataOperacao());
        membrosNaoExonerados = null;
        recuperarProcessoDeCompra();
    }

    private void recuperarProcessoDeCompra() {
        ProcessoDeCompra pdc = (ProcessoDeCompra) Web.pegaDaSessao("PROCESSO_DE_COMPRA");
        if (pdc != null) {
            selecionado.setProcessoDeCompra(pdc);
            validacoesDoProcessoDeCompraSelecionado();
        }
    }

    private void parametrosIniciaisDoStatusLicitacaoSelecionado() {
        novoStatusLicitacao();
    }

    private void cancelarStatusLicitacaoSelecionado() {
        statusLicitacaoSelecionado = null;
    }

    private void novoStatusLicitacao() {
        statusLicitacaoSelecionado = licitacaoFacade.getStatusLicitacaoFacade().getNovoStatus(TipoSituacaoLicitacao.ANDAMENTO, selecionado, "Status criado automaticamente pelo sistema para nova licitação");
        selecionado.setListaDeStatusLicitacao(Util.adicionarObjetoEmLista(selecionado.getListaDeStatusLicitacao(), statusLicitacaoSelecionado));
        cancelarStatusLicitacaoSelecionado();
    }

    @URLAction(mappingId = "ver-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        parametrosIniciaisAoSelecionar();
        listaDeItemProcessoDeCompraParaExibir = licitacaoFacade.recuperaItensProcessoDeCompra(selecionado);
        atribuirGrupoContaDespesa();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void geraDocumento(VersaoDoctoLicitacao versao) {
        VersaoDoctoLicitacao novaVersao = new VersaoDoctoLicitacao();
        novaVersao.setModeloDocto(new ModeloDocto());


        novaVersao.getModeloDocto().setModelo(licitacaoFacade.getDocumentoLicitacaoFacade().mesclaTagsModelo(versao.getModeloDocto(), selecionado, listaDeItemProcessoDeCompraParaExibir));
        novaVersao.getModeloDocto().setNome(versao.getModeloDocto().getNome());

        geraPDF(novaVersao.getModeloDocto());

    }

    public EventoPncpVO getEventoPncpVO() {
        EventoPncpVO eventoPncpVO = new EventoPncpVO();
        eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.LICITACAO);
        eventoPncpVO.setIdOrigem(selecionado.getId());
        eventoPncpVO.setIdPncp(selecionado.getIdPncp());
        eventoPncpVO.setSequencialIdPncp(selecionado.getSequencialIdPncp());
        eventoPncpVO.setSequencialIdPncpLicitacao(selecionado.getSequencialIdPncp());
        eventoPncpVO.setAnoPncp(selecionado.getExercicio().getAno());
        eventoPncpVO.setIntegradoPNCP(selecionado.getIdPncp() != null && selecionado.getSequencialIdPncp() != null);
        return eventoPncpVO;
    }

    public void confirmarIdPncp(ActionEvent evento) {
        EventoPncpVO eventoPncpVO = (EventoPncpVO) evento.getComponent().getAttributes().get("objeto");
        selecionado.setIdPncp(eventoPncpVO.getIdPncp());
        selecionado.setSequencialIdPncp(eventoPncpVO.getSequencialIdPncp());
        PncpService.getService().criarEventoAtualizacaoIdSequencialPncp(selecionado.getId(), selecionado.getNumeroAnoLicitacao());
        selecionado = licitacaoFacade.salvarRetornando(selecionado);
        redirecionarParaLicitacao(selecionado);
    }

    public void geraDocumentoPadrao(DoctoLicitacao docto) {
        if (validaSePodeGerarEdital()) {
            if (docto.getModeloDocto() != null) {
                ModeloDocto novoModelo = new ModeloDocto();

                novoModelo.setModelo(licitacaoFacade.getDocumentoLicitacaoFacade().mesclaTagsModelo(docto.getModeloDocto(), selecionado, listaDeItemProcessoDeCompraParaExibir));
                novoModelo.setNome(docto.getModeloDocto().getNome());
                novoModelo.setModelo(FacesUtil.alteraURLImagens(novoModelo.getModelo()));

                geraPDF(novoModelo);
            } else {
                FacesUtil.addError("Não é posssível emitir!", "O documento selecionado não modelo vinculado!");
            }
        }
    }

    public List<ModeloDocto> completaModeloDoctoLicitacao(String parte) {
        return licitacaoFacade.getModeloDoctoFacade().listaFiltrando(parte, "nome");
    }

    public Boolean getVerificaEdicao() {
        if (selecionado.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void confirmarModeloDoctoLicitacao() {
        if (validaDocumentoLicitacao()) {
            selecionado.setListaDeDocumentos(Util.adicionarObjetoEmLista(selecionado.getListaDeDocumentos(), doctoLicitacaoSelecionado));
            cancelarDoctoLicitacaoSelecionado();
        }
    }

    private Boolean validaDocumentoLicitacao() {
        if (modeloDocumentoIsNull()) {
            return Boolean.FALSE;
        }

        if (modeloDocumentoJaFoiAdicionado()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private boolean modeloDocumentoJaFoiAdicionado() {
        if (selecionado.getListaDeDocumentos() != null && !selecionado.getListaDeDocumentos().isEmpty()) {
            for (DoctoLicitacao doc : selecionado.getListaDeDocumentos()) {
                if (doctoLicitacaoSelecionado.getModeloDocto().equals(doc.getModeloDocto())) {
                    FacesUtil.addOperacaoNaoPermitida("O modelo de documento selecionado já foi adicionado.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean modeloDocumentoIsNull() {
        if (this.doctoLicitacaoSelecionado.getModeloDocto() == null) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório selecionar um modelo de documento.");
            return true;
        }
        return false;
    }

    public void removeModeloDoctoLicitacao(DoctoLicitacao docLicitacao) {
        selecionado.getListaDeDocumentos().remove(docLicitacao);
    }

    public DoctoLicitacao getDoctoLicitacaoSelecionado() {
        return doctoLicitacaoSelecionado;
    }

    public void setDoctoLicitacaoSelecionado(DoctoLicitacao doctoLicitacaoSelecionado) {
        this.doctoLicitacaoSelecionado = doctoLicitacaoSelecionado;
    }

    public void openDialog() {
        FacesUtil.executaJavaScript("dialogModeloDocumento.show()");
    }

    public List<SelectItem> getExercicios() {
        return licitacaoFacade.getExercicioFacade().getExerciciosComprasLicitacao();
    }

    public Boolean desabilitaCampoDataEmissaoDoEdital() {
        return selecionado.getPublicacoes().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
    }


    public List<DoctoHabilitacao> getDocumentoObrigatorio() {
        return licitacaoFacade.getDoctoHabilitacaoFacade().buscarDocumentosVigentes("", licitacaoFacade.getSistemaFacade().getDataOperacao());
    }

    public void setaPregoeiroResponsavel(MembroComissao membroComissao) {
        selecionado.setPregoeiroResponsavel(membroComissao);
    }

    public Boolean pregoeiro(MembroComissao membroComissao) {
        if (membroComissao.equals(selecionado.getPregoeiroResponsavel())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public SolicitacaoMaterial getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoMaterial solicitacao) {
        this.solicitacao = solicitacao;
    }

    public List<SelectItem> getTipoPrazo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrazo tipoPrazo : TipoPrazo.values()) {
            toReturn.add(new SelectItem(tipoPrazo, tipoPrazo.getDescricao()));
        }
        return toReturn;
    }

    public boolean isVisualizar() {
        if (Operacoes.VER.equals(operacao)) {
            return true;
        }
        return false;
    }

    public boolean bloquearEdicao() {
        boolean retorno;
        retorno = isEditando();
        if (selecionado.getId() != null) {
            retorno = licitacaoFacade.licitacaoTemPropostaFornecedor(selecionado);
        }
        return retorno;
    }

    private boolean isEditando() {
        if (Operacoes.EDITAR.equals(operacao)) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getTiposDePublicacaoLicitacao() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoPublicacaoLicitacao tipoPublicacaoLicitacao : TipoPublicacaoLicitacao.values()) {
            if (!TipoPublicacaoLicitacao.EDITAL.equals(tipoPublicacaoLicitacao) && !TipoPublicacaoLicitacao.TERMO_RECONHECIMENTO_DIVIDA.equals(tipoPublicacaoLicitacao)) {
                retorno.add(new SelectItem(tipoPublicacaoLicitacao, tipoPublicacaoLicitacao.getDescricao()));
            }
        }
        return retorno;
    }

    public DoctoHabilitacao[] getDoctoHabilitacaos() {
        return doctoHabilitacaos;
    }

    public void setDoctoHabilitacaos(DoctoHabilitacao[] doctoHabilitacaos) {
        this.doctoHabilitacaos = doctoHabilitacaos;
    }

    public List<Licitacao> buscarLicitacaoPorAnoOrNumeroOrDescricaoDoPCSemAtaDeRegPrecoAndUsuarioGestorLicitacao(String parte) {
        return licitacaoFacade.buscarLicitacaoPorAnoOrNumeroOrDescricaoDoPCSemAtaDeRegPrecoAndUsuarioGestorLicitacao(parte.trim(), licitacaoFacade.getSistemaFacade().getUsuarioCorrente());
    }

    public ItemSolicitacao getItemSolicitacaoEdicao() {
        return itemSolicitacaoEdicao;
    }

    public void setItemSolicitacaoEdicao(ItemSolicitacao itemSolicitacaoEdicao) {
        this.itemSolicitacaoEdicao = itemSolicitacaoEdicao;
    }

    public void processarEdicaoItemSolicitacao() {
        if (this.itemSolicitacaoEdicao != null) {
            itemSolicitacaoEdicao = licitacaoFacade.mergerItemSolicitacao(this.itemSolicitacaoEdicao);
        }
    }

    public ObjetoCompra getObjetoCompraSelecionado() {
        return objetoCompraSelecionado;
    }

    public void setObjetoCompraSelecionado(ObjetoCompra objetoCompraSelecionado) {
        this.objetoCompraSelecionado = objetoCompraSelecionado;
    }

    public void recuperarObjetoDeCompra() {
        if (this.itemSolicitacaoEdicao != null) {
            objetoCompraSelecionado = licitacaoFacade.getObjetoCompraFacade().recuperar(itemSolicitacaoEdicao.getObjetoCompra().getId());
        }
    }

    public void atribuirTextoAoComplementoDoItem(ObjetoDeCompraEspecificacao especificacao) {
        itemSolicitacaoEdicao.setDescricaoComplementar(especificacao.getTexto());
    }

    public List<LicitacaoVO> getLicitacoesEmAndamento() {
        return licitacoesEmAndamento;
    }

    public void setLicitacoesEmAndamento(List<LicitacaoVO> licitacoesEmAndamento) {
        this.licitacoesEmAndamento = licitacoesEmAndamento;
    }

    public List<LicitacaoVO> getLicitacoesAdjudicadas() {
        return licitacoesAdjudicadas;
    }

    public void setLicitacoesAdjudicadas(List<LicitacaoVO> licitacoesAdjudicadas) {
        this.licitacoesAdjudicadas = licitacoesAdjudicadas;
    }

    public List<LicitacaoVO> getLicitacoesHomologadas() {
        return licitacoesHomologadas;
    }

    public void setLicitacoesHomologadas(List<LicitacaoVO> licitacoesHomologadas) {
        this.licitacoesHomologadas = licitacoesHomologadas;
    }

    public List<LicitacaoVO> getLicitacoesAnuladas() {
        return licitacoesAnuladas;
    }

    public void setLicitacoesAnuladas(List<LicitacaoVO> licitacoesAnuladas) {
        this.licitacoesAnuladas = licitacoesAnuladas;
    }

    public List<LicitacaoVO> getLicitacoesCanceladas() {
        return licitacoesCanceladas;
    }

    public void setLicitacoesCanceladas(List<LicitacaoVO> licitacoesCanceladas) {
        this.licitacoesCanceladas = licitacoesCanceladas;
    }

    public List<LicitacaoVO> getLicitacoesDesertas() {
        return licitacoesDesertas;
    }

    public void setLicitacoesDesertas(List<LicitacaoVO> licitacoesDesertas) {
        this.licitacoesDesertas = licitacoesDesertas;
    }

    public List<LicitacaoVO> getLicitacoesEmRecurso() {
        return licitacoesEmRecurso;
    }

    public void setLicitacoesEmRecurso(List<LicitacaoVO> licitacoesEmRecurso) {
        this.licitacoesEmRecurso = licitacoesEmRecurso;
    }

    public List<LicitacaoVO> getLicitacoesProrrogadas() {
        return licitacoesProrrogadas;
    }

    public void setLicitacoesProrrogadas(List<LicitacaoVO> licitacoesProrrogadas) {
        this.licitacoesProrrogadas = licitacoesProrrogadas;
    }

    public List<LicitacaoVO> getLicitacoesFracassadas() {
        return licitacoesFracassadas;
    }

    public void setLicitacoesFracassadas(List<LicitacaoVO> licitacoesFracassadas) {
        this.licitacoesFracassadas = licitacoesFracassadas;
    }

    public List<LicitacaoVO> getLicitacoesRevogadas() {
        return licitacoesRevogadas;
    }

    public void setLicitacoesRevogadas(List<LicitacaoVO> licitacoesRevogadas) {
        this.licitacoesRevogadas = licitacoesRevogadas;
    }

    public List<LicitacaoVO> getLicitacoesSuspensas() {
        return licitacoesSuspensas;
    }

    public void setLicitacoesSuspensas(List<LicitacaoVO> licitacoesSuspensas) {
        this.licitacoesSuspensas = licitacoesSuspensas;
    }

    @URLAction(mappingId = "listar-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        setFiltrosPesquisa(Lists.<FiltroConsultaEntidade>newArrayList());
        limparFiltros();
        montarFieldsPesquisa();
        pesquisarEntidade();
    }

    private void montarFieldsPesquisa() {
        setFieldsPesquisa(Lists.<FieldConsultaEntidade>newArrayList());
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Resumo do Objeto", "obj.resumoDoObjeto", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Número da Modalidade", "obj.numero", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Número do Processo Licitatório", "obj.numeroLicitacao", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Tipo de Avaliação", "obj.tipoAvaliacao", "br.com.webpublico.enums.TipoAvaliacaoLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Tipo de Apuração", "obj.tipoApuracao", "br.com.webpublico.enums.TipoApuracaoLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Modalidade da Licitação", "obj.modalidadeLicitacao", "br.com.webpublico.enums.ModalidadeLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Exercício da Modalidade", "exModalidade.ano", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Natureza do Procedimento", "obj.naturezaDoProcedimento", "br.com.webpublico.enums.TipoNaturezaDoProcedimentoLicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Exercício", "ex.ano", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Data de Emissão", "obj.emitidaEm", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Data de Abertura", "obj.abertaEm", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Data de Homologação", "obj.homologadaEm", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Local de Entrega", "obj.localDeEntrega", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Regime de Execução", "obj.regimeDeExecucao", "br.com.webpublico.enums.RegimeDeExecucao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Forma de Pagamento", "obj.formaDePagamento", "br.com.webpublico.enums.RegimeDeExecucao", TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Período de Execução", "obj.periodoDeExecucao", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Tipo Prazo de Execução", "obj.tipoPrazoExecucao", "br.com.webpublico.enums.TipoPrazo", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Prazo de Vigência", "obj.prazoDeVigencia", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Tipo Prazo de Vigência", "obj.tipoPrazoVigencia", "br.com.webpublico.enums.TipoPrazo", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Código da Unidade", "vwadm.codigo", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Licitação.Descrição da Unidade", "vwadm.descricao", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Data do Processo", "pc.dataProcesso", null, TipoCampo.DATE, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Número", "pc.numero", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Exercício", "exPc.ano", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Tipo do Processo de Compra", "pc.tipoProcessoDeCompra", "br.com.webpublico.enums.TipoProcessoDeCompra", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Solicitação de Compra", "solMat.numero", null, TipoCampo.INTEGER, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Descrição", "pc.descricao", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Tipo da Solicitação", "pc.tipoSolicitacao", "br.com.webpublico.enums.TipoSolicitacao", TipoCampo.ENUM, TipoAlinhamento.ESQUERDA));
        getFieldsPesquisa().add(new FieldConsultaEntidade("Processo de Compra.Responsável", "coalesce(pfUsu.nome, usu.login)", null, TipoCampo.STRING, TipoAlinhamento.ESQUERDA));
        atualizarConverterFieldConsulta();
    }

    @Override
    public void pesquisarEntidade() {
        String condicao = getCondicaoPesquisa();
        licitacoesEmAndamento = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.ANDAMENTO);
        licitacoesAdjudicadas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.ADJUDICADA);
        licitacoesHomologadas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.HOMOLOGADA);
        licitacoesAnuladas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.ANULADA);
        licitacoesCanceladas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.CANCELADA);
        licitacoesDesertas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.DESERTA);
        licitacoesEmRecurso = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.EM_RECURSO);
        licitacoesFracassadas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.FRACASSADA);
        licitacoesProrrogadas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.PRORROGADA);
        licitacoesRevogadas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.REVOGADA);
        licitacoesSuspensas = licitacaoFacade.buscarLicitacoesVO(condicao, TipoSituacaoLicitacao.SUSPENSA);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.LICITACAO);
        }
        if ("tab-ajuste".equals(tab)) {
            filtroAjusteProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.LICITACAO);
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroAjusteProcesso() {
        return filtroAjusteProcesso;
    }

    public void setFiltroAjusteProcesso(FiltroHistoricoProcessoLicitatorio filtroAjusteProcesso) {
        this.filtroAjusteProcesso = filtroAjusteProcesso;
    }

    public void atualizarDataEncerramento() {
        if (selecionado.getAbertaEm() != null) {
            selecionado.setEncerradaEm(DataUtil.adicionarMinutos(selecionado.getAbertaEm(), 30));
        }
    }
}
