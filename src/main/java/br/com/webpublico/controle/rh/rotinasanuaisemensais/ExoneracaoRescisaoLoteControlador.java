package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.exoneracao.ExoneracaoRescisaoContrato;
import br.com.webpublico.entidades.rh.exoneracao.ExoneracaoRescisaoLote;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.ExoneracaoRescisaoLoteFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ManagedBean(name = "exoneracaoRescisaoLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-exoneracao-rescisao", pattern = "/exoneracao-rescisao-lote/novo/", viewId = "/faces/rh/rotinasanuaisemensais/exoneracaorescisaolote/edita.xhtml"),
    @URLMapping(id = "ver-exoneracao-rescisao", pattern = "/exoneracao-rescisao-lote/ver/#{exoneracaoRescisaoLoteControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/exoneracaorescisaolote/visualizar.xhtml"),
    @URLMapping(id = "lista-exoneracao-rescisao", pattern = "/exoneracao-rescisao-lote/listar/", viewId = "/faces/rh/rotinasanuaisemensais/exoneracaorescisaolote/lista.xhtml")
})
public class ExoneracaoRescisaoLoteControlador extends PrettyControlador<ExoneracaoRescisaoLote> implements CRUD {
    private static final Logger logger = LoggerFactory.getLogger(ExoneracaoRescisaoLoteControlador.class);

    @EJB
    private ExoneracaoRescisaoLoteFacade exoneracaoRescisaoLoteFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ModalidadeContratoFP modalidade;
    private List<ModalidadeContratoFP> modalidades;
    private CompletableFuture<ExoneracaoRescisaoLote> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;


    public ExoneracaoRescisaoLoteControlador() {
        super(ExoneracaoRescisaoLote.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exoneracao-rescisao-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    public ModalidadeContratoFP getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeContratoFP modalidade) {
        this.modalidade = modalidade;
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    @Override
    public AbstractFacade getFacede() {
        return exoneracaoRescisaoLoteFacade;
    }

    @URLAction(mappingId = "novo-exoneracao-rescisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataOperacao(new Date());
        modalidades = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-exoneracao-rescisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getItemExoneracaoContrato());
    }

    public String mensagemFichaFinanceiraAberta() {
        return (!selecionado.getItemExoneracaoContrato().isEmpty() && selecionado.getDataExoneracao() != null && !exoneracaoRescisaoLoteFacade.buscarFichasFinanceirasParaExclusao(selecionado).isEmpty()) ? "if (!confirm('ATENÇÃO! Foi encontrada ficha financeira em aberto processada em Folha Normal na competência da exoneração. Essa ficha financeira será excluída.')) return false;" : "aguarde.show()";
    }

    @Override
    public void salvar() {
        try {
            validarRealizarExoneracao();
            for (ExoneracaoRescisaoContrato itemExoneracaoContrato : selecionado.getItemExoneracaoContrato()) {
                validarAfastamentoAndConcessaoFerias(itemExoneracaoContrato.getVinculoFP());
            }
            selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            selecionado.setDataOperacao(new Date());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Exoneração/Rescisão por Lote");
            assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            assistenteBarraProgresso.setTotal((selecionado.getItemExoneracaoContrato().size() + 1));

            future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
                () -> {
                    try {
                        return exoneracaoRescisaoLoteFacade.salvar(selecionado, assistenteBarraProgresso);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            FacesUtil.executaJavaScript("gerarExoneracaoRescisaoLote()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
            logger.error("Erro ao salvar a exoneração rescisão por lote {}", e);
        }
    }

    public void verificarTermino() {
        if (future != null && future.isDone()) {
            Boolean podeRedirecionar = Boolean.TRUE;
            try {
                ExoneracaoRescisaoLote exoneracaoRescisaoLote = future.get();
                if (exoneracaoRescisaoLote != null) {
                    selecionado = exoneracaoRescisaoLote;
                }
            } catch (Exception e) {
                logger.error("erro", e);
                FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao salvar a exoneração rescisão por lote.");
                podeRedirecionar = Boolean.FALSE;
            }
            FacesUtil.executaJavaScript("termina()");
            future = null;
            FacesUtil.executaJavaScript("dialogo.hide()");
            if (podeRedirecionar) {
                redireciona();
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            }
        }
    }

    private void validarAfastamentoAndConcessaoFerias(VinculoFP vinculoFP) {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = exoneracaoRescisaoFacade.hasAfastamentoDataExoneracao(vinculoFP, selecionado.getDataExoneracao());
        List<ConcessaoFeriasLicenca> concessao = exoneracaoRescisaoFacade.hasConcessaoDataExoneracao(vinculoFP, selecionado.getDataExoneracao());
        if (afastamentos != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + vinculoFP + " tem afastamento à vencer na data <b>" + DataUtil.getDataFormatada(afastamentos.get(0).getTermino()) + "</b>" + " não podendo ser exonerado.");
        }
        if (concessao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + vinculoFP + "  tem concessão de férias à vencer na data <b>" + DataUtil.getDataFormatada(concessao.get(0).getDataFinal()) + "</b>" + " não podendo ser exonerado.");
        }
        ve.lancarException();
    }

    private void validarRealizarExoneracao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItemExoneracaoContrato().isEmpty() || selecionado.getItemExoneracaoContrato() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum servidor adicionado para realizar a exoneração.");
        }
        if (selecionado.getDataExoneracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Exoneração");
        }
        if (selecionado.getMotivoExoneracaoRescisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo da Exoneração/Rescisão deve ser informado!");
            ve.lancarException();
        }
        if (selecionado.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Motivo da Exoneração/Rescisão selecionado não possui um 'Motivo Exoneração/Rescisão E-Social' cadastrado!<br>" +
                "  Vá em /motivo-exoneracao-rescisao/listar/ , ache o Motivo da Exoneração/Rescisão que esta tentando usar e cadastre um 'Motivo Exoneração/Rescisão E-Social' para ele.");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Ato Legal deve ser informado!");
        }
    }

    public List<SelectItem> getModalidadeContrato() {
        return Util.getListSelectItem(modalidadeContratoFPFacade.lista());
    }

    public void buscarContratoExoneracao() {
        try {
            validarBuscarContratos();
            selecionado.getItemExoneracaoContrato().clear();
            List<VinculoFP> vinculos = exoneracaoRescisaoLoteFacade.buscarVinculoFPPorModalidadeContrato(modalidades);
            if (vinculos != null) {
                for (VinculoFP vinculo : vinculos) {
                    ExoneracaoRescisaoContrato exoneracao = new ExoneracaoRescisaoContrato();
                    exoneracao.setExoneracaoRescisaoLote(selecionado);
                    exoneracao.setVinculoFP(vinculo);
                    selecionado.getItemExoneracaoContrato().add(exoneracao);
                }
                Collections.sort(selecionado.getItemExoneracaoContrato());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void validarBuscarContratos() {
        ValidacaoException ve = new ValidacaoException();
        if (modalidades == null || modalidades.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi adicionado nenhuma Modalidade de Contrato para pesquisa.");
        }
        ve.lancarException();
    }

    private void validarModalidadeContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (modalidades.contains(modalidade)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Modalidade de contrato já adicionada!");
        }
        ve.lancarException();
    }

    public void adicionarModalidadeContratoFP() {
        try {
            validarModalidadeContratoFP();
            modalidades.add(modalidade);
            modalidade = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerModalidadeContrato(ModalidadeContratoFP modalidade) {
        modalidades.remove(modalidade);
    }

    public void removerServidorListaExonerados(ExoneracaoRescisaoContrato item) {
        selecionado.getItemExoneracaoContrato().remove(item);
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
