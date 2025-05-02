package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 25/09/14
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "efetivacaoEstornoTransferenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEfetivacaoEstornoTransferenciaMovel", pattern = "/efetivacao-estorno-transferencia-movel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoEstornoTransferenciaMovel", pattern = "/efetivacao-estorno-transferencia-movel/editar/#{efetivacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoEstornoTransferenciaMovel", pattern = "/efetivacao-estorno-transferencia-movel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/movel/lista.xhtml"),
    @URLMapping(id = "verEfeticacaoEstornoTransferenciaMovel", pattern = "/efetivacao-estorno-transferencia-movel/ver/#{efetivacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/movel/visualizar.xhtml"),

    @URLMapping(id = "novoEfetivacaoEstornoTransferenciaImovel", pattern = "/efetivacao-estorno-transferencia-imovel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoEstornoTransferenciaImovel", pattern = "/efetivacao-estorno-transferencia-imovel/editar/#{efetivacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoEstornoTransferenciaImovel", pattern = "/efetivacao-estorno-transferencia-imovel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/imovel/lista.xhtml"),
    @URLMapping(id = "verEfeticacaoEstornoTransferenciaImovel", pattern = "/efetivacao-estorno-transferencia-imovel/ver/#{efetivacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoestornotransferencia/imovel/visualizar.xhtml")
})

public class EfetivacaoEstornoTransferenciaControlador extends PrettyControlador<EfetivacaoEstornoTransferencia> implements Serializable, CRUD {

    @EJB
    private SolicitacaoEstornoTransferenciaFacade solicitacaoEstornoFacade;
    @EJB
    private EfetivacaoEstornoTransferenciaFacade efetivacaoEstornoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    private List<ItemEstornoTransferencia> listaItensSolicitacaoEstorno;
    private Boolean solicitacaoAceita;
    private TipoBem tipoBem;

    public EfetivacaoEstornoTransferenciaControlador() {
        super(EfetivacaoEstornoTransferencia.class);
        listaItensSolicitacaoEstorno = Lists.newArrayList();
    }

    public ConfigMovimentacaoBem getConfigMovimentacaoBem() {
        return configMovimentacaoBem;
    }

    public void setConfigMovimentacaoBem(ConfigMovimentacaoBem configMovimentacaoBem) {
        this.configMovimentacaoBem = configMovimentacaoBem;
    }

    @Override
    public AbstractFacade getFacede() {
        return efetivacaoEstornoFacade;
    }

    @URLAction(mappingId = "novoEfetivacaoEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEfetivacaoEstornoMovel() {
        this.tipoBem = TipoBem.MOVEIS;
        novo();
    }

    @URLAction(mappingId = "novoEfetivacaoEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEfetivacaoEstornoImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        novo();
    }

    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setCodigo(null);
            selecionado.setDataEfetivacao(sistemaFacade.getDataOperacao());
            selecionado.setEfetivador(sistemaFacade.getUsuarioCorrente());
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void recuperarAndValidarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), sistemaFacade.getDataOperacao(), operacao);
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = efetivacaoEstornoFacade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_TRANSFERENCIA_BEM_ESTORNO);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    @URLAction(mappingId = "verEfeticacaoEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoEstornoMovel() {
        this.tipoBem = TipoBem.MOVEIS;
        ver();
    }

    @URLAction(mappingId = "verEfeticacaoEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoEstornoImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        ver();
    }

    @Override
    public void ver() {
        super.ver();
        solicitacaoAceita = selecionado.getSolicitacaoEstorno().getSituacao().equals(SituacaoEventoBem.FINALIZADO);
    }

    @URLAction(mappingId = "editarEfetivacaoEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoEstornoMovel() {
        this.tipoBem = TipoBem.MOVEIS;
        editar();
    }

    @URLAction(mappingId = "editarEfetivacaoEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoEstornoImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        editar();
    }

    @Override
    public void editar() {
        super.editar();
        recarregarItensSolicitacao();
    }

    @Override
    public String getCaminhoPadrao() {
        switch (this.tipoBem) {
            case MOVEIS:
                return "/efetivacao-estorno-transferencia-movel/";
            case IMOVEIS:
                return "/efetivacao-estorno-transferencia-imovel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void recarregarItensSolicitacao() {
        try {
            recuperarAndValidarRegrasConfiguracaoMovimentacaoBem();
            if (selecionado.getSolicitacaoEstorno() != null) {
                List<ItemEstornoTransferencia> itensRecuperados = solicitacaoEstornoFacade.recuperarItensSolicitacaoEstorno(selecionado.getSolicitacaoEstorno());
                if (itensRecuperados != null && !itensRecuperados.isEmpty()) {
                    validarMovimentoComDataRetroativa(itensRecuperados);
                    listaItensSolicitacaoEstorno = itensRecuperados;
                }
            }
        } catch (ValidacaoException ve) {
            selecionado.setSolicitacaoEstorno(null);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            selecionado.setSolicitacaoEstorno(null);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarMovimentoComDataRetroativa(List<ItemEstornoTransferencia> itensRecupedos) {
        ValidacaoException ve = new ValidacaoException();
        if (configMovimentacaoBem.getValidarMovimentoRetroativo()) {
            for (ItemEstornoTransferencia item : itensRecupedos) {
                String mensagem = efetivacaoEstornoFacade.getConfigMovimentacaoBemFacade().validarMovimentoComDataRetroativaBem(item.getBem().getId(), configMovimentacaoBem.getOperacaoMovimentacaoBem().getDescricao(), sistemaFacade.getDataOperacao());
                if (!mensagem.isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarEfetivacao();
            recuperarAndValidarRegrasConfiguracaoMovimentacaoBem();
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(EfetivacaoEstornoTransferencia.class, "codigo"));
            }
            selecionado = efetivacaoEstornoFacade.estornar(selecionado, solicitacaoAceita);
            FacesUtil.addOperacaoRealizada("Estorno realizado com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarEfetivacao() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoAceita == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A situação do estorno deve ser informada.");
        } else if (!solicitacaoAceita && (selecionado.getSolicitacaoEstorno().getMotivoRecusa() == null
            || selecionado.getSolicitacaoEstorno().getMotivoRecusa().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O motivo da recusa deve ser informado.");
        }
        if (listaItensSolicitacaoEstorno.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum item para efetivação do estorno.");
        }
        if (DataUtil.dataSemHorario(selecionado.getDataEfetivacao()).before(DataUtil.dataSemHorario(selecionado.getSolicitacaoEstorno().getDataDeCriacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de efetivação de estorno de transferência deve ser posterior ou igual a data da solicitação estorno de transferência n° " + selecionado.getSolicitacaoEstorno().getCodigo() + ".");
        }
        ve.lancarException();
    }

    public List<ItemEstornoTransferencia> getListaItensSolicitacaoEstorno() {
        return listaItensSolicitacaoEstorno;
    }

    public BigDecimal valorTotalDosBens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!listaItensSolicitacaoEstorno.isEmpty()) {
            for (ItemEstornoTransferencia item : listaItensSolicitacaoEstorno) {
                valorTotal = valorTotal.add(item.getEstadoResultante().getValorOriginal());
            }
        }
        return valorTotal;
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public Boolean getSolicitacaoAceita() {
        return solicitacaoAceita;
    }

    public void setSolicitacaoAceita(Boolean solicitacaoAceita) {
        this.solicitacaoAceita = solicitacaoAceita;
    }

    public Boolean getRenderizarMotivodaRecusa() {
        if (solicitacaoAceita != null && !solicitacaoAceita && selecionado.getSolicitacaoEstorno() != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<SolicitacaoEstornoTransferencia> completaSolicitacaoEstornoSemEfetivacao(String filtro) {
        return solicitacaoEstornoFacade.completaSolicitacaoEstornoSemEfetivacao(filtro, this.sistemaFacade, this.tipoBem);
    }


    public void gerarTermoEfetivacaoSolicitacaoEstorno() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "TermoDeEstornoDeTrasferenciaInterna.jasper";
            HashMap parameters = Maps.newHashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(efetivacaoEstornoFacade.buscarRegistrosTermoEfetivacaoSolicitacaoEstorno(selecionado));
            parameters.put("MODULO", "Patrimônio");
            parameters.put("ENTIDADE", getEntidade());
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("DATA_EFETIVACAO", DataUtil.getDataFormatada(selecionado.getDataEfetivacao()));
            parameters.put("ENTIDADE", getEntidade());
            parameters.put("NOMERELATORIO", "TERMO DE ESTORNO DA TRANSFERÊNCIA DE BENS " + selecionado.getSolicitacaoEstorno().getLoteEfetivacaoTransferencia().getTipoBem().getDescricao().toUpperCase());
            parameters.put("BRASAO", abstractReport.getCaminhoImagem());
            if (sistemaFacade.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
            }
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio {}", ex);
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório ");
        }
    }

    public String getEntidade() {
        try {
            ItemEstornoTransferencia itemEstorno = listaItensSolicitacaoEstorno.get(0);
            UnidadeOrganizacional unidadeDestino = itemEstorno.getEfetivacaoTransferencia().getTransferenciaBem().getLoteTransferenciaBem().getUnidadeDestino();
            return efetivacaoEstornoFacade.getSolicitacaoEstornoTransferenciaFacade().getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(unidadeDestino).getNome().toUpperCase();
        } catch (NullPointerException npe) {
            return "";
        }
    }
}
