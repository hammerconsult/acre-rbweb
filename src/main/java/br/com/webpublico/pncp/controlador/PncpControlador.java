package br.com.webpublico.pncp.controlador;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.EsferaGovernamental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.pncp.dto.*;
import br.com.webpublico.pncp.entidade.EventoPncp;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.OperacaoPncp;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.enums.TipoProcessoPncp;
import br.com.webpublico.pncp.facade.EventoPncpFacade;
import br.com.webpublico.pncp.service.PncpService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.LogUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-integracao-pncp", pattern = "/integracao-pncp/", viewId = "/faces/administrativo/licitacao/pncp/integracao-pncp.xhtml")
})
public class PncpControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PncpControlador.class);
    public static final String MENSAGEM_INTEGRACAO = "Integração com o PNCP realizada com sucesso. Aguarde alguns minutos e consulte os eventos enviados.";

    @EJB
    private EventoPncpFacade eventoPncpFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private OrgaoEntidadePncpFacade orgaoEntidadePncpFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private PlanoContratacaoAnualFacade planoContratacaoAnualFacade;
    private PncpService pncpService;

    private List<EventoPncp> eventos;
    private List<OrgaoEntidadeConsultaDTO> orgaosEntidades;
    private List<UnidadeConsultaDTO> unidades;
    private List<EnteAutorizadoDTO> entesAutorizados;
    private FiltroConsultaDTO filtroConsulta;
    private List<ContratacaoConsultaDTO> contratacoes;
    private List<AtaRegistroPrecoConsultaDTO> atasRegistroPreco;
    private List<ContratoEmpenhoConsultaDTO> contratosEmpenhos;
    private List<PlanoContratacaoAnualConsultaDTO> planosContratacoesAnuais;
    private ContratacaoConsultaDTO contratacaoConsultaDTO;
    private PlanoContratacaoAnualConsultaDTO planoContratacaoAnual;
    private EventoPncpVO eventoPncpVO;

    @URLAction(mappingId = "nova-integracao-pncp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novoFiltroConsulta();
        contratacoes = Lists.newArrayList();
        atasRegistroPreco = Lists.newArrayList();
    }

    private void novoFiltroConsulta() {
        filtroConsulta = new FiltroConsultaDTO();
        filtroConsulta.setDataInicial(DataUtil.getPrimeiroDiaMes(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear() - 1));
        filtroConsulta.setDataFinal(new Date());
    }

    private PncpService getPncpService() {
        if (pncpService == null) {
            pncpService = (PncpService) Util.getSpringBeanPeloNome("pncpService");
        }
        return pncpService;
    }

    public void validarCamposConsulta() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroConsulta.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial deve ser informada");
        }
        if (filtroConsulta.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Final deve ser informada");
        }

        if (filtroConsulta.getDataInicial() != null && filtroConsulta.getDataFinal() != null && filtroConsulta.getDataInicial().after(filtroConsulta.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser inferior ou igual à Data Final.");
        }
        ve.lancarException();
    }

    public void buscarContratoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            contratosEmpenhos = getPncpService().buscarContratoEmpenho(ve, filtroConsulta);
            ve.lancarException();

            contratosEmpenhos.forEach(contEmp -> {
                EventoPncpVO eventoPncpVO = new EventoPncpVO();
                eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.CONTRATO_EMPENHO);
                eventoPncpVO.setIdOrigem(contEmp.getId());
                eventoPncpVO.setIdPncp(contEmp.getIdPncp());
                eventoPncpVO.setSequencialIdPncp(contEmp.getSequencialIdPncp());
                eventoPncpVO.setSequencialIdPncpLicitacao(contEmp.getSequencialIdPncp());
                eventoPncpVO.setAnoPncp(contEmp.getAnoCompra());
                eventoPncpVO.setIntegradoPNCP(contEmp.getIdPncp() != null && contEmp.getSequencialIdPncp() != null);
                recuperarUltimoEventoPncp(eventoPncpVO);
                contEmp.setEventoPncpVO(eventoPncpVO);
            });
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao buscar contrato/empenho pncp ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }


    public void buscarAtas() {
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            atasRegistroPreco = getPncpService().buscarAtaRegistroPreco(ve, filtroConsulta);
            ve.lancarException();

            atasRegistroPreco.forEach(ata -> {
                EventoPncpVO eventoPncpVO = new EventoPncpVO();
                eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.ATA_REGISTRO_PRECO);
                eventoPncpVO.setIdOrigem(ata.getId());
                eventoPncpVO.setIdPncp(ata.getIdPncp());
                eventoPncpVO.setSequencialIdPncp(ata.getSequencialIdPncp());
                eventoPncpVO.setSequencialIdPncpLicitacao(ata.getSequencialIdPncp());
                eventoPncpVO.setAnoPncp(ata.getAnoCompra());
                eventoPncpVO.setIntegradoPNCP(ata.getIdPncp() != null && ata.getSequencialIdPncp() != null);
                recuperarUltimoEventoPncp(eventoPncpVO);
                ata.setEventoPncpVO(eventoPncpVO);
            });

        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            logger.error("erro ao buscar atas. ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarContratacao() {
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            contratacoes = getPncpService().buscarContratacao(ve, filtroConsulta);
            ve.lancarException();

            contratacoes.forEach(cont -> {
                EventoPncpVO eventoPncpVO = new EventoPncpVO();
                eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.LICITACAO);
                eventoPncpVO.setIdOrigem(cont.getId());
                eventoPncpVO.setIdPncp(cont.getIdPncp());
                eventoPncpVO.setSequencialIdPncp(cont.getSequencialIdPncp());
                eventoPncpVO.setSequencialIdPncpLicitacao(cont.getSequencialIdPncp());
                eventoPncpVO.setAnoPncp(cont.getAnoCompra());
                eventoPncpVO.setIntegradoPNCP(cont.getIdPncp() != null && cont.getSequencialIdPncp() != null);
                recuperarUltimoEventoPncp(eventoPncpVO);
                cont.setEventoPncpVO(eventoPncpVO);
            });

        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            logger.error("erro ao buscar licitações. ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarItensContratacao(ContratacaoConsultaDTO contratacaoDTO) {
        contratacaoConsultaDTO = contratacaoDTO;
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            getPncpService().buscarItensContratacao(ve, contratacaoConsultaDTO);
            ve.lancarException();
            FacesUtil.atualizarComponente("formDlgItemResultado");
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao buscarItensContratacao ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarPlanosContratacoesAnuais() {
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            planosContratacoesAnuais = getPncpService().buscarPlanosContratacoesAnuais(ve, filtroConsulta);
            ve.lancarException();

            planosContratacoesAnuais.forEach(pca -> {
                EventoPncpVO eventoPncpVO = new EventoPncpVO();
                eventoPncpVO.setTipoEventoPncp(TipoEventoPncp.PLANO_CONTRATACAO_ANUAL);
                eventoPncpVO.setIdOrigem(pca.getId());
                eventoPncpVO.setIdPncp(pca.getIdPncp());
                eventoPncpVO.setSequencialIdPncp(pca.getSequencialIdPncp());
                eventoPncpVO.setAnoPncp(pca.getAnoPca());
                eventoPncpVO.setIntegradoPNCP(pca.getIdPncp() != null && pca.getSequencialIdPncp() != null);
                recuperarUltimoEventoPncp(eventoPncpVO);
                pca.setEventoPncpVO(eventoPncpVO);
            });

        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao buscar Planos de Contratações Anuais - PCA. ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarItensPlanoContratacaoAnual(PlanoContratacaoAnualConsultaDTO pca) {
        planoContratacaoAnual = pca;
        ValidacaoException ve = new ValidacaoException();
        try {
            validarCamposConsulta();
            getPncpService().buscarItensPlanoContratacaoAnual(ve, planoContratacaoAnual);
            ve.lancarException();
            FacesUtil.atualizarComponente("formDlgItemPCA");
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        } catch (Exception e) {
            Util.loggingError(this.getClass(), "Erro ao buscar itens do Plano de Contratações Anual - PCA.", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void buscarOrgaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        try {
            orgaosEntidades = getPncpService().buscarOrgaoEntidade(ve);
            verificarOrgaoEntidadeUnidadeCadastradaPncp();

            unidades = Lists.newArrayList();
            orgaosEntidades.forEach(entidadePncpDTO -> unidades.addAll(entidadePncpDTO.getUnidades()));

            entesAutorizados = Lists.newArrayList();
            orgaosEntidades.forEach(entidadePncpDTO -> entesAutorizados.addAll(entidadePncpDTO.getEntesAutorizados()));

            Comparator<OrgaoEntidadeConsultaDTO> comparatorEntidade = Comparator.comparing(OrgaoEntidadeConsultaDTO::getCnpj);
            orgaosEntidades = orgaosEntidades.stream().sorted(comparatorEntidade).collect(Collectors.toList());

            Comparator<UnidadeConsultaDTO> comparatorOrgao = Comparator.comparing(UnidadeConsultaDTO::getCodigoUnidade);
            unidades = unidades.stream().sorted(comparatorOrgao).collect(Collectors.toList());
            popularEventoPncpVOOrgaoEntidadeUnidadeEnteAutorizado();

            ve.lancarException();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            logger.debug("Erro inesperado no buscarEntidades.", ex);
            logger.error("Erro inesperado no buscarEntidades.");
            FacesUtil.addError("Erro ao tentar realizar integração com o Portal Nacional de Contratações Públicas", "");
        }
    }

    private void popularEventoPncpVOOrgaoEntidadeUnidadeEnteAutorizado() {
        orgaosEntidades.forEach(ent -> {
            EventoPncpVO novoEventoVO = new EventoPncpVO();
            novoEventoVO.setTipoEventoPncp(TipoEventoPncp.ORGAO_ENTIDADE);
            novoEventoVO.setIdOrigem(ent.getIdEntidade());
            novoEventoVO.setIntegradoPNCP(ent.getIntegrado());
            recuperarUltimoEventoPncp(novoEventoVO);
            ent.setEventoPncpVO(novoEventoVO);
        });

        unidades.forEach(orgao -> {
            EventoPncpVO novoEventoVO = new EventoPncpVO();
            novoEventoVO.setTipoEventoPncp(TipoEventoPncp.UNIDADE);
            novoEventoVO.setIdOrigem(orgao.getIdUnidade());
            novoEventoVO.setIntegradoPNCP(orgao.getIntegradoPNCP());
            recuperarUltimoEventoPncp(novoEventoVO);
            orgao.setEventoPncpVO(novoEventoVO);
        });

        entesAutorizados.forEach(ente -> {
            EventoPncpVO novoEventoVO = new EventoPncpVO();
            novoEventoVO.setTipoEventoPncp(TipoEventoPncp.USUARIO);
            novoEventoVO.setIdOrigem(Long.valueOf(ente.getIdUsuario()));
            novoEventoVO.setIntegradoPNCP(true);
            recuperarUltimoEventoPncp(novoEventoVO);
            ente.setEventoPncpVO(novoEventoVO);
        });
    }

    public void enviarTodasUnidade() {
        ValidacaoException ve = new ValidacaoException();
        try {
            for (UnidadeConsultaDTO unidade : unidades) {
                if (!unidade.getIntegradoPNCP()) {
                    EventoPncp evento = criarEvento(TipoEventoPncp.UNIDADE, OperacaoPncp.INSERIR, unidade.getIdUnidade());
                    getPncpService().enviar(evento, ve);
                    ve.lancarException();
                }
            }
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoCargaInicial.", ex);
        }
    }

    public List<EventoPncp> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoPncp> eventos) {
        this.eventos = eventos;
    }

    public void buscarTodosEventos() {
        this.eventos = eventoPncpFacade.buscarTodos();
    }

    public void enviarEventoCargaInicialOrgaoEntidade() {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(TipoEventoPncp.CARGA_INICIAL_ORGAO_ENTIDADE, OperacaoPncp.INSERIR, null);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoCargaInicial.", ex);
        }
    }

    public void enviarEventoCargaInicialContratacao() {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(TipoEventoPncp.CARGA_INICIAL_LICITACAO, OperacaoPncp.INSERIR, null);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoCargaInicialLicitacao.", ex);
        }
    }

    public void enviarEventoCargaInicialContratoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(TipoEventoPncp.CARGA_INICIAL_CONTRATO_EMPENHO, OperacaoPncp.INSERIR, null);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoCargaContratoEmpenho.", ex);
        }
    }

    public void enviarEventoContratoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        for (ContratoEmpenhoConsultaDTO dto : contratosEmpenhos) {
            if (dto.getIdPncp() == null) {
                try {
                    EventoPncp evento = criarEvento(TipoEventoPncp.CONTRATO_EMPENHO, OperacaoPncp.INSERIR, dto.getId());
                    getPncpService().enviar(evento, ve);
                    ve.lancarException();
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                } catch (Exception ex) {
                    LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoContratoEmpenho.", ex);
                }
            }
        }
        FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
    }

    public void enviarUsuario() {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(TipoEventoPncp.USUARIO, OperacaoPncp.ALTERAR, null);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarUsuario.", ex);
        }
    }

    private EventoPncp criarEvento(TipoEventoPncp tipoEvento, OperacaoPncp operacao, Long idOrigem) {
        return getPncpService().criarEvento(tipoEvento, operacao, idOrigem);
    }

    public void enviarEventoContratacao() {
        ValidacaoException ve = new ValidacaoException();
        for (ContratacaoConsultaDTO contratacao : contratacoes) {
            if (contratacao.getIdPncp() == null) {
                try {
                    EventoPncp evento = criarEvento(TipoEventoPncp.LICITACAO, OperacaoPncp.INSERIR, contratacao.getId());
                    getPncpService().enviar(evento, ve);
                    ve.lancarException();
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                } catch (Exception ex) {
                    LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoLicitacoes.", ex);
                }
            }
        }
        FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
    }

    public void enviarEventoCargaInicialAtaRegistroPreco() {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(TipoEventoPncp.CARGA_INICIAL_ATA_REGISTRO_PRECO, OperacaoPncp.INSERIR, null);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoCargaInicialLicitacao.", ex);
        }
    }

    public void enviarEventoAtas() {
        ValidacaoException ve = new ValidacaoException();
        for (AtaRegistroPrecoConsultaDTO ataDTO : atasRegistroPreco) {
            if (ataDTO.getIdPncp() == null) {
                try {
                    EventoPncp evento = criarEvento(TipoEventoPncp.ATA_REGISTRO_PRECO, OperacaoPncp.INSERIR, ataDTO.getId());
                    getPncpService().enviar(evento, ve);
                    ve.lancarException();
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                } catch (Exception ex) {
                    LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEventoAtas.", ex);
                }
            }
        }
        FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
    }

    public void enviarEventoPlanoContratacaoAnual() {
        ValidacaoException ve = new ValidacaoException();
        for (PlanoContratacaoAnualConsultaDTO pca : planosContratacoesAnuais) {
            if (pca.getIdPncp() == null) {
                try {
                    EventoPncp evento = criarEvento(TipoEventoPncp.PLANO_CONTRATACAO_ANUAL, OperacaoPncp.INSERIR, pca.getId());
                    getPncpService().enviar(evento, ve);
                    ve.lancarException();
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                } catch (Exception ex) {
                    LogUtil.registrarExcecao(logger, "Erro inesperado ao enviar evento PCA .", ex);
                }
            }
        }
        FacesUtil.addOperacaoRealizada(MENSAGEM_INTEGRACAO);
    }

    public void enviarEvento(TipoEventoPncp tipo, OperacaoPncp operacao, Long idOrigem) {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(tipo, operacao, idOrigem);
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            Thread.sleep(2000);
            FacesUtil.executaJavaScript("finalizarRetornoIntegracao()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEvento.", ex);
        }
    }

    public void enviarEventoPncp(EventoPncpVO eventoPncpVO, OperacaoPncp operacao) {
        ValidacaoException ve = new ValidacaoException();
        try {
            EventoPncp evento = criarEvento(eventoPncpVO.getTipoEventoPncp(), operacao, eventoPncpVO.getIdOrigem());
            getPncpService().enviar(evento, ve);
            ve.lancarException();
            Thread.sleep(2000);
            atribuirSequencialIdPncpNoEventoPncpVO(eventoPncpVO);
            lancarMensagemRetornoAPI(eventoPncpVO);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            LogUtil.registrarExcecao(logger, "Erro inesperado no enviarEvento.", ex);
        }
    }

    private void lancarMensagemRetornoAPI(EventoPncpVO eventoPncpVO) {
        if (eventoPncpVO.getUltimoEventoPncp() != null) {
            FacesUtil.addOperacaoRealizada(eventoPncpVO.getUltimoEventoPncp().getSituacao().getMensagem());
            if (!Strings.isNullOrEmpty(eventoPncpVO.getUltimoEventoPncp().getMensagemDeErro())) {
                FacesUtil.addOperacaoNaoRealizada(eventoPncpVO.getUltimoEventoPncp().getMensagemDeErro());
            }
        }
    }

    public void atualizarIntegracaoRetornoEvento() {
        try {
            atribuirSequencialIdPncpNoEventoPncpVO(eventoPncpVO);
            lancarMensagemRetornoAPI(eventoPncpVO);
            FacesUtil.executaJavaScript("atualizarRetornoIntegracao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void atribuirSequencialIdPncpNoEventoPncpVO(EventoPncpVO eventoPncpVO) {
        recuperarUltimoEventoPncp(eventoPncpVO);
        String idPncp = null;
        String sequencialIdPncp = null;

        if (eventoPncpVO.getIdOrigem() != null) {
            switch (eventoPncpVO.getTipoEventoPncp()) {
                case ORGAO_ENTIDADE:
                case UNIDADE:
                    verificarOrgaoEntidadeUnidadeCadastradaPncp();
                    break;

                case LICITACAO:
                    Licitacao licitacao = licitacaoFacade.recuperarSomenteLicitacao(eventoPncpVO.getIdOrigem());
                    if (licitacao != null) {
                        idPncp = licitacao.getIdPncp();
                        sequencialIdPncp = licitacao.getSequencialIdPncp();
                    } else {
                        DispensaDeLicitacao dispensa = dispensaDeLicitacaoFacade.recuperrarSemDependencias(eventoPncpVO.getIdOrigem());
                        if (dispensa != null) {
                            idPncp = dispensa.getIdPncp();
                            sequencialIdPncp = dispensa.getSequencialIdPncp();
                        }
                    }
                    break;

                case ATA_REGISTRO_PRECO:
                    AtaRegistroPreco ata = ataRegistroPrecoFacade.recuperarSemDependencias(eventoPncpVO.getIdOrigem());
                    if (ata != null) {
                        idPncp = ata.getIdPncp();
                        sequencialIdPncp = ata.getSequencialIdPncp();
                    }
                    break;
                case CONTRATO_EMPENHO:
                    Contrato contrato = contratoFacade.recuperarSemDependencias(eventoPncpVO.getIdOrigem());
                    if (contrato != null) {
                        idPncp = contrato.getIdPncp();
                        sequencialIdPncp = contrato.getSequencialIdPncp();
                    } else {
                        ExecucaoProcessoEmpenho execProc = execucaoProcessoFacade.buscarExecucaoProcessoEmpenhoPorEmpenho(eventoPncpVO.getIdOrigem());
                        if (execProc != null) {
                            idPncp = execProc.getIdPncp();
                            sequencialIdPncp = execProc.getSequencialIdPncp();
                        }
                    }
                    break;
                case PLANO_CONTRATACAO_ANUAL:
                    PlanoContratacaoAnual pca = planoContratacaoAnualFacade.recuperar(eventoPncpVO.getIdOrigem());
                    if (pca != null) {
                        idPncp = pca.getIdPncp();
                        sequencialIdPncp = pca.getSequencialIdPncp();
                    }
                    break;
            }
        }
        eventoPncpVO.setIdPncp(idPncp);
        eventoPncpVO.setSequencialIdPncp(sequencialIdPncp);
        eventoPncpVO.setIntegradoPNCP(idPncp != null && sequencialIdPncp != null);
    }

    public String montarUrlPncp(EventoPncpVO evento) {
        try {
            if (evento.getTipoEventoPncp().isAtaRegistroPreco()) {
                return getPncpService().montarUrlPncp("/app/atas/", evento.getIdPncp(), evento.getAnoPncp(), evento.getSequencialIdPncpLicitacao() + "/" + evento.getSequencialIdPncp());

            } else if (evento.getTipoEventoPncp().equals(TipoEventoPncp.CONTRATO_EMPENHO)) {
                return getPncpService().montarUrlPncp("/app/contratos/", evento.getIdPncp(), evento.getAnoPncp(), evento.getSequencialIdPncp());
            } else if (evento.getTipoEventoPncp().equals(TipoEventoPncp.PLANO_CONTRATACAO_ANUAL)) {
                return getPncpService().montarUrlPncp("/app/pca/", evento.getIdPncp(), evento.getAnoPncp(), evento.getSequencialIdPncp());
            }
            return getPncpService().montarUrlPncp("/app/editais/", evento.getIdPncp(), evento.getAnoPncp(), evento.getSequencialIdPncp());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            return evento.getIdPncp();
        }
        return null;
    }

    public String montarIdContratacaoPncp(EventoPncpVO evento) {
        return getPncpService().montarIdContratacaoPncp(evento.getIdPncp(), evento.getAnoPncp(), evento.getSequencialIdPncp());
    }

    public void preRenderComponenteBotoesPncp(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
        atribuirSequencialIdPncpNoEventoPncpVO(this.eventoPncpVO);
    }

    public void recuperarUltimoEventoPncp(EventoPncpVO eventoPncpVO) {
        if (eventoPncpVO.getIdOrigem() != null) {
            eventoPncpVO.setUltimoEventoPncp(eventoPncpFacade.recuperarUltimoEvento(eventoPncpVO.getIdOrigem()));
        }
    }

    public void buscarTodosEventosPorOrigem(Long idOrigem) {
        if (idOrigem != null) {
            eventos = eventoPncpFacade.buscarPorIdOrigem(idOrigem);
            if (!Util.isListNullOrEmpty(eventos)) {
                eventos.forEach(ev -> Collections.sort(ev.getLogs()));
                eventos.forEach(ev -> Collections.sort(ev.getEnvios()));
            }
        }
    }

    public boolean isStringNula(String campo) {
        return Util.isStringNulaOuVazia(campo);
    }

    public void buscarUltimaSincronizacao(EventoPncpVO eventoPncpVO) {
        try {
            atribuirSequencialIdPncpNoEventoPncpVO(eventoPncpVO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void verificarOrgaoEntidadeUnidadeCadastradaPncp() {
        ValidacaoException ve = new ValidacaoException();
        try {
            getPncpService().getBaseUrl(ve);
            getPncpService().verificarOrgaoEntidadeCadastradaPncp(orgaosEntidades, ve);
            getPncpService().verificarUnidadeCadastradaPncp(orgaosEntidades, ve);
            ve.lancarException();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public String getCaminhoNavegacao(TipoProcessoPncp tipo, Long id) {
        if (id == null || tipo == null) {
            return "";
        }
        String caminho = "";
        switch (tipo) {
            case LICITACAO:
                caminho = "/licitacao/ver/";
                break;
            case CREDENCIAMENTO:
                caminho = "/credenciamento/ver/";
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                caminho = "/dispensa-licitacao/ver/";
                break;
            case ATA_REGISTRO_PRECO:
                caminho = "/ata-registro-preco/ver/";
                break;
            case CONTRATO:
                caminho = "/contrato-adm/ver/";
                break;
            case EMPENHO:
                caminho = "/empenho/ver/";
                break;
            case PLANO_CONTRATACAO_ANUAL:
                caminho = "/plano-contratacao-anual/ver/";
                break;
            default:
                caminho = "/integracao-pncp/";
        }
        return caminho + id + "/";
    }


    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaPorOrgaoAndTipoAdministrativaAndUsuario(parte.trim());
    }

    public List<EnteAutorizadoDTO> getEntesAutorizados() {
        return entesAutorizados;
    }

    public void setEntesAutorizados(List<EnteAutorizadoDTO> entesAutorizados) {
        this.entesAutorizados = entesAutorizados;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }

    public List<OrgaoEntidadeConsultaDTO> getOrgaosEntidades() {
        return orgaosEntidades;
    }

    public void setOrgaosEntidades(List<OrgaoEntidadeConsultaDTO> orgaosEntidades) {
        this.orgaosEntidades = orgaosEntidades;
    }

    public List<UnidadeConsultaDTO> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeConsultaDTO> unidades) {
        this.unidades = unidades;
    }

    public List<SelectItem> getPoder() {
        return Util.getListSelectItem(EsferaDoPoder.values(), false);
    }

    public List<SelectItem> getEsfera() {
        return Util.getListSelectItem(EsferaGovernamental.values(), false);
    }

    public FiltroConsultaDTO getFiltroConsulta() {
        return filtroConsulta;
    }

    public void setFiltroConsulta(FiltroConsultaDTO filtroConsulta) {
        this.filtroConsulta = filtroConsulta;
    }

    public List<ContratacaoConsultaDTO> getContratacoes() {
        return contratacoes;
    }

    public void setContratacoes(List<ContratacaoConsultaDTO> contratacoes) {
        this.contratacoes = contratacoes;
    }

    public List<AtaRegistroPrecoConsultaDTO> getAtasRegistroPreco() {
        return atasRegistroPreco;
    }

    public void setAtasRegistroPreco(List<AtaRegistroPrecoConsultaDTO> ataRegistros) {
        this.atasRegistroPreco = ataRegistros;
    }

    public ContratacaoConsultaDTO getContratacaoConsultaDTO() {
        return contratacaoConsultaDTO;
    }

    public void setContratacaoConsultaDTO(ContratacaoConsultaDTO contratacaoConsultaDTO) {
        this.contratacaoConsultaDTO = contratacaoConsultaDTO;
    }

    public List<ContratoEmpenhoConsultaDTO> getContratosEmpenhos() {
        return contratosEmpenhos;
    }

    public void setContratosEmpenhos(List<ContratoEmpenhoConsultaDTO> contratosEmpenhos) {
        this.contratosEmpenhos = contratosEmpenhos;
    }

    public PlanoContratacaoAnualConsultaDTO getPlanoContratacaoAnual() {
        return planoContratacaoAnual;
    }

    public void setPlanoContratacaoAnual(PlanoContratacaoAnualConsultaDTO planoContratacaoAnual) {
        this.planoContratacaoAnual = planoContratacaoAnual;
    }

    public List<PlanoContratacaoAnualConsultaDTO> getPlanosContratacoesAnuais() {
        return planosContratacoesAnuais;
    }

    public void setPlanosContratacoesAnuais(List<PlanoContratacaoAnualConsultaDTO> planosContratacoesAnuais) {
        this.planosContratacoesAnuais = planosContratacoesAnuais;
    }
}
