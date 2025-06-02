package br.com.webpublico.nfse.controladores;

import br.com.webpublico.DateUtils;
import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.ModalidadeEmissao;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by William on 01/02/2018.
 */


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nfseListar", pattern = "/nfse/nfse/listar/", viewId = "/faces/tributario/nfse/nfse/lista.xhtml"),
    @URLMapping(id = "nfseListarLogImpressao", pattern = "/nfse/nfse/log-impressao/", viewId = "/faces/tributario/nfse/nfse/listaLogImpressao.xhtml"),
    @URLMapping(id = "nfseVer", pattern = "/nfse/nfse/ver/#{notaFiscalControlador.id}/", viewId = "/faces/tributario/nfse/nfse/visualizar.xhtml"),
    @URLMapping(id = "nfseEditar", pattern = "/nfse/nfse/editar/#{notaFiscalControlador.id}/", viewId = "/faces/tributario/nfse/nfse/edita.xhtml"),
    @URLMapping(id = "nfseVerPorRps", pattern = "/nfse/nfse/ver-rps/#{notaFiscalControlador.id}/", viewId = "/faces/tributario/nfse/nfse/visualizar.xhtml"),
})
public class NotaFiscalControlador extends PrettyControlador<NotaFiscal> implements CRUD {

    @EJB
    private NotaFiscalFacade facade;
    private List<ResultadoParcela> parcelasDaNota;
    private List<CartaCorrecaoNfse> cartasCorrecao;
    private Map<ResultadoParcela, String> mapaNumeroDam;
    private ItemDeclaracaoServico itemDeclaracaoServico;
    private DetalheItemDeclaracaoServico detalheItemDeclaracaoServico;
    private List<LogGeralNfse> logsImpressao;
    private List<DeclaracaoMensalServico> declaracaoMensalServicos;
    private Boolean possuiConstrucaoCivil;
    private Boolean possuiIntermediario;

    public NotaFiscalControlador() {
        super(NotaFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<ResultadoParcela> getParcelasDaNota() {
        return parcelasDaNota;
    }

    public void setParcelasDaNota(List<ResultadoParcela> parcelasDaNota) {
        this.parcelasDaNota = parcelasDaNota;
    }

    public List<CartaCorrecaoNfse> getCartasCorrecao() {
        return cartasCorrecao;
    }

    public Map<ResultadoParcela, String> getMapaNumeroDam() {
        return mapaNumeroDam;
    }

    public void setMapaNumeroDam(Map<ResultadoParcela, String> mapaNumeroDam) {
        this.mapaNumeroDam = mapaNumeroDam;
    }

    public ItemDeclaracaoServico getItemDeclaracaoServico() {
        return itemDeclaracaoServico;
    }

    public void setItemDeclaracaoServico(ItemDeclaracaoServico itemDeclaracaoServico) {
        this.itemDeclaracaoServico = itemDeclaracaoServico;
    }

    public DetalheItemDeclaracaoServico getDetalheItemDeclaracaoServico() {
        return detalheItemDeclaracaoServico;
    }

    public void setDetalheItemDeclaracaoServico(DetalheItemDeclaracaoServico detalheItemDeclaracaoServico) {
        this.detalheItemDeclaracaoServico = detalheItemDeclaracaoServico;
    }

    public Boolean getPossuiConstrucaoCivil() {
        return possuiConstrucaoCivil;
    }

    public void setPossuiConstrucaoCivil(Boolean possuiConstrucaoCivil) {
        this.possuiConstrucaoCivil = possuiConstrucaoCivil;
    }

    public Boolean getPossuiIntermediario() {
        return possuiIntermediario;
    }

    public void setPossuiIntermediario(Boolean possuiIntermediario) {
        this.possuiIntermediario = possuiIntermediario;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/nfse/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<LogGeralNfse> getLogsImpressao() {
        return logsImpressao;
    }

    @Override
    @URLAction(mappingId = "nfseVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        tratarCamposAuxiliares();
        recuperarDebitos();
        recuperarLogsImpressao();
        recuperarEncerramentoMensal();
    }

    private void recuperarEncerramentoMensal() {
        declaracaoMensalServicos = facade.getDeclaracaoMensalServicoFacade().buscarDMSdaNota(selecionado.getId());
    }

    private void recuperarLogsImpressao() {
        logsImpressao = facade.recuperarLogsImpressao(selecionado.getId());
    }

    @Override
    @URLAction(mappingId = "nfseEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        tratarCamposAuxiliares();
    }

    private void tratarCamposAuxiliares() {
        possuiConstrucaoCivil = selecionado.getDeclaracaoPrestacaoServico().getConstrucaoCivil() != null;
        possuiIntermediario = selecionado.getDeclaracaoPrestacaoServico().getIntermediario() != null;
        itemDeclaracaoServico = selecionado.getDeclaracaoPrestacaoServico().getItens().get(0);
    }

    private void recuperarDebitos() {
        parcelasDaNota = facade.buscarParcelasDaNota(selecionado.getId());
        cartasCorrecao = facade.buscarCartaCorrecaoPorNotaFiscal(selecionado.getId());
        mapaNumeroDam = Maps.newHashMap();
        for (ResultadoParcela resultadoParcela : parcelasDaNota) {
            DAM dam = facade.getDamFacade().recuperaUltimoDamParcela(resultadoParcela.getIdParcela());
            mapaNumeroDam.put(resultadoParcela, dam != null ? dam.getNumeroDAM() : "");
        }
    }

    @URLAction(mappingId = "nfseVerPorRps", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verPorRps() {
        selecionado = facade.buscarNotaPorRPS(getId());
        if (selecionado == null) {
            FacesUtil.addOperacaoNaoRealizada("O RPS não está processado.");
            FacesUtil.redirecionamentoInterno("/nfse/rps/listar/");
            return;
        }
        tratarCamposAuxiliares();
        recuperarDebitos();
        recuperarLogsImpressao();
        recuperarEncerramentoMensal();
    }

    public void imprimir() {
        imprimir(selecionado.getId());
    }

    public void removerNaNota() {
        facade.removerNaNota(selecionado);
        FacesUtil.addOperacaoRealizada("Cache limpo");
    }

    public void imprimir(Long id) {
        try {
            byte[] bytes = facade.gerarImpressaoNotaFiscalSistemaNfse(id);
            AbstractReport report = new AbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("Nota Fiscal Eletrônica", bytes);
            logsImpressao.add(facade.inserirLogImpressao(id));
        } catch (Exception e) {
            logger.error("Erro ao imprimir Nfs-e. Erro: {}", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao imprimir Nfs-e. Erro: " + e.getMessage());
        }
    }

    public void imprimirDAM() {
        try {
            List<ResultadoParcela> parcelas = facade.getDeclaracaoMensalServicoFacade().recuperarDebitosDaDecalaracao(selecionado.getId(), selecionado.getPrestador().getId());
            List<DAM> dams = Lists.newArrayList();
            for (ResultadoParcela parcela : parcelas) {
                ParcelaValorDivida p = new ParcelaValorDivida();
                p.setId(parcela.getIdParcela());
                DAM dam = facade.getDamFacade().recuperaDAMPeloIdParcela(parcela.getIdParcela());
                if (dam == null || dam.getValorTotal().compareTo(parcela.getValorTotal()) != 0) {
                    Date vencimento = parcela.getVencimento() == null || parcela.isVencido(new Date()) ? DataUtil.ultimoDiaMes(new Date()).getTime() : parcela.getVencimento();
                    dam = facade.getDamFacade().geraDAM(p, vencimento);
                }
                dams.add(dam);
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error("Erro ao emitir o DAM da declaraçção: {}", e);
        }
    }

    public List<DeclaracaoMensalServico> getDeclaracaoMensalServicos() {
        return declaracaoMensalServicos;
    }

    public void setDeclaracaoMensalServicos(List<DeclaracaoMensalServico> declaracaoMensalServicos) {
        this.declaracaoMensalServicos = declaracaoMensalServicos;
    }

    public String getEmissaoFormatada() {
        if (DateUtils.hasHourOrMinute(selecionado.getEmissao())) {
            return DateUtils.getDataFormatada(selecionado.getEmissao(), "dd/MM/yyyy HH:mm");
        } else {
            return DateUtils.getDataFormatada(selecionado.getEmissao(), "dd/MM/yyyy");
        }
    }

    @Override
    public void salvar() {
        try {
            ConfiguracaoNfse configuracaoNfse = facade.getConfiguracaoNfseFacade().recuperarConfiguracao();
            selecionado.setInformacoesAdicionais(selecionado.gerarInformacoesAdicionais(configuracaoNfse, facade.buscarVencimentoIss(selecionado)));
            facade.salvar(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeEmissao.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoNota.values());
    }

    public List<SelectItem> getNaturezas() {
        return Util.getListSelectItem(Exigibilidade.values());
    }

    public List<SelectItem> getTiposDocumentoSemDesif() {
        List<SelectItem> tipos = new ArrayList<>();
        tipos.add(new SelectItem(null, ""));
        tipos.add(new SelectItem(TipoDocumentoNfse.NFSE, TipoDocumentoNfse.NFSE.getDescricao()));
        tipos.add(new SelectItem(TipoDocumentoNfse.SERVICO_DECLARADO, TipoDocumentoNfse.SERVICO_DECLARADO.getDescricao()));
        return tipos;
    }

    public void tratarPossuiConstrucaoCivil() {
        if (possuiConstrucaoCivil) {
            selecionado.getDeclaracaoPrestacaoServico().setConstrucaoCivil(new ConstrucaoCivil());
        } else {
            selecionado.getDeclaracaoPrestacaoServico().setConstrucaoCivil(null);
        }
    }

    public void tratarPossuiIntermediario() {
        if (possuiIntermediario) {
            selecionado.getDeclaracaoPrestacaoServico().setIntermediario(new IntermediarioServico());
        } else {
            selecionado.getDeclaracaoPrestacaoServico().setIntermediario(null);
        }
    }

    public void tratarModalidade() {
        if (ModalidadeEmissao.NAO_IDENTIFICADO.equals(selecionado.getDeclaracaoPrestacaoServico().getModalidade())) {
            selecionado.getDeclaracaoPrestacaoServico().setDadosPessoaisTomador(null);
        } else {
            selecionado.getDeclaracaoPrestacaoServico().setDadosPessoaisTomador(new DadosPessoaisNfse());
        }
    }

    public void calcularValores() {
        try {
            facade.calcularValoresNotaFiscal(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao calcular valores da nota fiscal {}", e);
        }
    }

    public void calcularItem() {
        try {
            facade.calcularValoresItemDeclaracaoServico(itemDeclaracaoServico);
            facade.calcularValoresNotaFiscal(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao calcular item da nota fiscal {}", e);
        }
    }

    public void inserirDetalheItemDeclaracao() {
        detalheItemDeclaracaoServico = new DetalheItemDeclaracaoServico();
    }

    public void cancelarDetalheItemDeclaracao() {
        detalheItemDeclaracaoServico = null;
    }

    public void adicionarDetalheItemDeclaracao() {
        try {
            detalheItemDeclaracaoServico.realizarValidacoes();
            detalheItemDeclaracaoServico.setItemDeclaracaoServico(itemDeclaracaoServico);
            if (itemDeclaracaoServico.getDetalhes() == null) {
                itemDeclaracaoServico.setDetalhes(new ArrayList());
            }
            itemDeclaracaoServico.getDetalhes().add(detalheItemDeclaracaoServico);
            cancelarDetalheItemDeclaracao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void removerDetalheItemDeclaracao(DetalheItemDeclaracaoServico detalhe) {
        itemDeclaracaoServico.getDetalhes().remove(detalhe);
    }

    public void alterarDetalheItemDeclaracao(DetalheItemDeclaracaoServico detalhe) {
        detalheItemDeclaracaoServico = (DetalheItemDeclaracaoServico) Util.clonarObjeto(detalhe);
    }
}
