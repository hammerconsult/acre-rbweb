package br.com.webpublico.controle.contabil.reinf;

/**
 * Created by Romanini on 07/07/2022.
 */

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LiquidacaoDoctoFiscal;
import br.com.webpublico.entidades.ModeloDoctoOficial;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.entidades.contabil.reinf.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.reinf.ReinfFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "consultaOcorrenciasReinfControlador")
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "painel-reinf", pattern = "/contabil/reinf/painel/", viewId = "/faces/financeiro/reinf/painel/visualizar.xhtml"),})
public class ConsultaOcorrenciasReinfControlador {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaOcorrenciasReinfControlador.class);

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReinfFacade reinfFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;


    private String xml;

    private List<OcorrenciaESocialDTO> ocorrencias;

    private List<EventoReinfDTO> eventos;

    private Integer activeIndex;

    private List<TipoArquivoReinf> tipoArquivoReinf;

    private LazyDataModel<EventoReinfDTO> eventosTabela;

    Boolean mostrarSomenteNovosEventos;
    private List<EventoReinfDTO> eventosCompetencia;

    private FiltroReinf selecionado;

    private AssistenteSincronizacaoReinf assistente;

    private Future<AssistenteSincronizacaoReinf> future;

    private RegistroEventoRetencaoReinf registroEventoRetencaoReinf;

    @URLAction(mappingId = "painel-reinf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPainel() {
        inicializarAtributos();
    }


    private void inicializarAtributos() {
        selecionado = new FiltroReinf();
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setApenasNaoEnviados(Boolean.TRUE);
        selecionado.setMes(Mes.getMesToInt(DataUtil.getMes(sistemaFacade.getDataOperacao())));
        selecionado.setDataInicial(DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno()));
        selecionado.setDataFinal(DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno()));

        assistente = new AssistenteSincronizacaoReinf();
        assistente.setSelecionado(selecionado);
        tipoArquivoReinf = Lists.newArrayList();
        Collections.addAll(tipoArquivoReinf, TipoArquivoReinf.values());
        eventosTabela = new LazyEventosDataModel(new ArrayList<EventoReinfDTO>());
        mostrarSomenteNovosEventos = Boolean.TRUE;
    }

    public List<SelectItem> getEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial hierarquia : configuracaoEmpregadorESocialFacade.lista()) {
            toReturn.add(new SelectItem(hierarquia.getEntidade(), hierarquia.getEntidade() + " "));
        }
        return toReturn;
    }

    public void buscarUnidadesEmpregador() {
        if (selecionado.getEmpregador() != null) {
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());

            if (config != null) {
                config = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
                selecionado.setHierarquias(Lists.<HierarquiaOrganizacional>newArrayList());
                for (ItemConfiguracaoEmpregadorESocial itemConfiguracaoEmpregadorESocial : config.getItemConfiguracaoEmpregadorESocial()) {
                    HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(itemConfiguracaoEmpregadorESocial.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                    selecionado.getHierarquias().add(hierarquia);
                }
            }

        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoESocial value : SituacaoESocial.values()) {
            toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }

    private void validarEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmpregador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o empregador para envio do evento.");
        }
        ve.lancarException();
    }

    private void validarEnvioEvento(List lista) {
        ValidacaoException ve = new ValidacaoException();
        if (lista.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi encontrado Evento para envio do efd-reinf.");
        }
        ve.lancarException();
    }

    private void validarBuscaEmpregador() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmpregador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o empregador para consulta de evento.");
        }
        ve.lancarException();
    }

    public void buscarR1000() {
        if (selecionado.getEmpregador() != null) {
            selecionado.getItemEmpregadorR1000().clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                ConfiguracaoEmpregadorESocial co = configuracaoEmpregadorESocialFacade.recuperar(config.getId());
                if (co.getResponsavelReinf() == null) {
                    FacesUtil.addAtencao("Entidade não possui o responsável cadastrado. ");
                }
                if (co.getDataInicioReinf() == null) {
                    FacesUtil.addAtencao("Entidade não possui data de início da obrigatoriedade do envio. ");
                }
                selecionado.getItemEmpregadorR1000().add(co);
            }
        }
    }

    public void buscarR1070() {
        if (selecionado.getEmpregador() != null) {
            selecionado.getItemEmpregadorR1070().clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                selecionado.setItemEmpregadorR1070(configuracaoEmpregadorESocialFacade.buscarR1070(config, selecionado));
            }
        }
    }

    public void buscarR2010() {
        try {
            validarEmpregador();
            validarMesExercicio();
            selecionado.getItemEmpregadorR2010().clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                selecionado.setItemEmpregadorR2010(configuracaoEmpregadorESocialFacade.buscarR2010(config, selecionado));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R2010", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR2010", e);
            logger.error("Erro no método buscarR2010. Habilite o debug para visualizar o erro.");
        }
    }

    public void buscarR2020() {
        try {
            validarEmpregador();
            validarMesExercicio();
            selecionado.getItemEmpregadorR2020().clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                selecionado.setItemEmpregadorR2020(configuracaoEmpregadorESocialFacade.buscarR2020(config, selecionado));
            }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R2020", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR2020", e);
            logger.error("Erro no método buscarR2020. Habilite o debug para visualizar o erro.");
        }
    }

    public void buscarR2098() {
        try {
            validarEmpregador();
            validarMesExercicio();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            assistente.setConfiguracaoEmpregadorESocial(config);
            assistente.setTipoArquivo(TipoArquivoReinf.R2098);
            if (config != null) {
                selecionado.setEventoR2098(configuracaoEmpregadorESocialFacade.buscarR2098(assistente, config, selecionado));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R2098", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR2098", e);
            logger.error("Erro no método buscarR2098. Habilite o debug para visualizar o erro.");
        }

    }

    public void buscarR2099() {
        try {
            validarEmpregador();
            validarMesExercicio();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            assistente.setConfiguracaoEmpregadorESocial(config);
            assistente.setTipoArquivo(TipoArquivoReinf.R2099);
            if (config != null) {
                selecionado.setEventoR2099(configuracaoEmpregadorESocialFacade.buscarR2099(assistente, config, selecionado));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R2099", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR2099", e);
            logger.error("Erro no método buscarR2099. Habilite o debug para visualizar o erro.");
        }
    }

    public void buscarR4020() {
        try {
            validarEmpregador();
            validarMesExercicio();
            selecionado.getItemEmpregadorR4020().clear();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                selecionado.setItemEmpregadorR4020(configuracaoEmpregadorESocialFacade.buscarR4020(config, selecionado));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscad o evento R4020", "Detalhe do erro: " + e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void buscarR4099() {
        try {
            validarEmpregador();
            validarMesExercicio();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            assistente.setConfiguracaoEmpregadorESocial(config);
            assistente.setTipoArquivo(TipoArquivoReinf.R4099);
            if (config != null) {
                selecionado.setEventoR4099(configuracaoEmpregadorESocialFacade.buscarR4099(config, selecionado));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R4099", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR4099", e);
            logger.error("Erro no método buscarR4099. Habilite o debug para visualizar o erro.");
        }
    }

    public List<SelectItem> getTiposDeArquivoReinf() {
        if(assistente.getSelecionado().getUtilizarVersao2_1()){
            List<TipoArquivoReinf> tiposVersao2_1 = Lists.newArrayList();
            tiposVersao2_1.add(TipoArquivoReinf.R2010V2);
            return Util.getListSelectItem(tiposVersao2_1);
        }
        return Util.getListSelectItem(TipoArquivoReinf.getTiposVersao1_5(), false);
    }

    public void buscarR9000() {
        try {
            validarEmpregador();
            validarMesExercicio();
            ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
            if (config != null) {
                RegistroEventoExclusaoReinf registro = new RegistroEventoExclusaoReinf();
                registro.setDataRegistro(new Date());
                registro.setUsuarioSistema(selecionado.getUsuarioSistema());
                registro.setExercicio(selecionado.getExercicio());
                registro.setMes(selecionado.getMes());
                selecionado.setEventoR9000(registro);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar buscar o evento R9000", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método buscarR9000", e);
            logger.error("Erro no método buscarR9000. Habilite o debug para visualizar o erro.");
        }
    }


    private void validarMesExercicio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Campo Mẽs é obrigatório.");
        }
        if (selecionado.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Campo Exercício é obrigatório.");
        }
        ve.lancarException();
    }

    public void enviarR1000() {
        try {
            validarEmpregador();
            validarEnvioEvento(selecionado.getItemEmpregadorR1000());
            iniciarSincronizacao(TipoArquivoReinf.R1000);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R1000", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR1000", e);
            logger.error("Erro no método enviarR1000. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR1070() {
        try {
            validarEmpregador();
            validarEnvioEvento(selecionado.getItemEmpregadorR1070());
            iniciarSincronizacao(TipoArquivoReinf.R1070);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R1070", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR1070", e);
            logger.error("Erro no método enviarR1070. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR2010() {
        try {
            validarEmpregador();
            validarEnvioEvento(selecionado.getItemEmpregadorR2010());
            iniciarSincronizacao(TipoArquivoReinf.R2010);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R2010", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR2010", e);
            logger.error("Erro no método enviarR2010. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR2020() {
        try {
            validarEmpregador();
            validarEnvioEvento(selecionado.getItemEmpregadorR2020());
            iniciarSincronizacao(TipoArquivoReinf.R2020);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R2020", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR2020", e);
            logger.error("Erro no método enviarR2020. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR2098() {
        try {
            validarEmpregador();
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getEventoR2098() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento foi encontrado.");
            }
            ve.lancarException();
            iniciarSincronizacao(TipoArquivoReinf.R2098);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o eventoR2098", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR2098", e);
            logger.error("Erro no método enviarR2098. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR2099() {
        try {
            validarEmpregador();
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getEventoR2099() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento foi encontrado.");
            }
            ve.lancarException();
            iniciarSincronizacao(TipoArquivoReinf.R2099);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R2099", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR2099", e);
            logger.error("Erro no método enviarR2099. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR4099() {
        try {
            validarEmpregador();
            ValidacaoException ve = new ValidacaoException();
            if (selecionado.getEventoR4099() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento foi encontrado.");
            }
            ve.lancarException();
            iniciarSincronizacao(TipoArquivoReinf.R4099);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R4099", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR4099", e);
            logger.error("Erro no método enviarR4099. Habilite o debug para visualizar o erro.");
        }
    }

    public void enviarR4020() {
        try {
            validarEmpregador();
            validarEnvioEvento(selecionado.getItemEmpregadorR4020());
            iniciarSincronizacao(TipoArquivoReinf.R4020);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R100", "Detalhe do erro: " + e.getMessage());
            logger.error(e.getMessage());
        }
    }

    public void enviarR9000() {
        try {
            validarEmpregador();
            validarEventoR9000();
            iniciarSincronizacao(TipoArquivoReinf.R9000);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar enviar o evento R9000", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método enviarR9000", e);
            logger.error("Erro no método enviarR9000. Habilite o debug para visualizar o erro.");
        }
    }

    private void validarEventoR9000() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEventoR9000() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento foi encontrado.");
        } else {
            if (selecionado.getEventoR9000().getExercicio() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Campo Mẽs é obrigatório.");
            }
            if (selecionado.getEventoR9000().getMes() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Campo Exercício é obrigatório.");
            }
            if (selecionado.getEventoR9000().getTipoArquivo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Campo Tipo de arquivo é obrigatório.");
            }
            if (selecionado.getEventoR9000().getNumeroRecibo() == null || StringUtils.isEmpty(selecionado.getEventoR9000().getNumeroRecibo())) {
                ve.adicionarMensagemDeCampoObrigatorio("Campo número do recibo é obrigatório.");
            }
        }
        ve.lancarException();
    }

    private void iniciarSincronizacao(TipoArquivoReinf tipoArquivoReinf) {
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(selecionado.getEmpregador());
        assistente.setConfiguracaoEmpregadorESocial(config);
        assistente.setTipoArquivo(tipoArquivoReinf);
        future = configuracaoEmpregadorESocialFacade.iniciarSincronizacao(assistente);
        FacesUtil.executaJavaScript("iniciarImportacao()");
    }

    public void finalizarImportacao() {
        if (future != null && future.isDone()) {
            try {
                if (assistente.getBarraProgressoItens() != null && !assistente.getBarraProgressoItens().getCalculando() && !Strings.isNullOrEmpty(assistente.getMensagemErro())) {
                    FacesUtil.addOperacaoNaoRealizada(assistente.getMensagemErro());
                } else {
                    FacesUtil.addOperacaoRealizada("Processo finalizado com sucesso.");
                }
                FacesUtil.executaJavaScript("terminarImportacao()");
            } catch (Exception e) {
                FacesUtil.executaJavaScript("terminarImportacao()");
                logger.debug("Erro no método finalizarImportacao", e);
                logger.error("Erro no método finalizarImportacao. Habilite o debug para visualizar o erro.");
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }

        }
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            assistente.getBarraProgressoItens().finaliza();
        }
    }

    public void adicionarRetificacao() {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (registroEventoRetencaoReinf == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Nenhum evento foi selecionado.");
            } else {
                if (Strings.isNullOrEmpty(registroEventoRetencaoReinf.getNumeroReciboOrigem())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O Número do recibo é obrigatório.");
                }
            }
            ve.lancarException();
            RegistroEventoRetencaoReinf novo = (RegistroEventoRetencaoReinf) BeanUtils.cloneBean(registroEventoRetencaoReinf);
            novo.setRetificacao(Boolean.TRUE);
            novo.setId(null);
            novo.setMensagem("");
            novo.setReceitas(Lists.<ReceitaExtraReinf>newArrayList());
            novo.setNotas(Lists.<NotaReinf>newArrayList());
            novo.setPagamentos(Lists.<PagamentoReinf>newArrayList());
            for (ReceitaExtraReinf receita : registroEventoRetencaoReinf.getReceitas()) {
                ReceitaExtraReinf novoObj = (ReceitaExtraReinf) BeanUtils.cloneBean(receita);
                novoObj.setId(null);
                novoObj.setRegistro(novo);
                novo.getReceitas().add(novoObj);
            }
            for (NotaReinf receita : registroEventoRetencaoReinf.getNotas()) {
                NotaReinf novoObj = (NotaReinf) BeanUtils.cloneBean(receita);
                novoObj.setId(null);
                novoObj.setRegistro(novo);
                novo.getNotas().add(novoObj);
            }
            for (PagamentoReinf receita : registroEventoRetencaoReinf.getPagamentos()) {
                PagamentoReinf novoObj = (PagamentoReinf) BeanUtils.cloneBean(receita);
                novoObj.setId(null);
                novoObj.setRegistro(novo);
                novo.getPagamentos().add(novoObj);
            }
            if (TipoArquivoReinf.R2010.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
                selecionado.getItemEmpregadorR2010().remove(registroEventoRetencaoReinf);
                selecionado.getItemEmpregadorR2010().add(novo);
            }
            if (TipoArquivoReinf.R2020.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
                selecionado.getItemEmpregadorR2020().remove(registroEventoRetencaoReinf);
                selecionado.getItemEmpregadorR2020().add(novo);
            }
            if (TipoArquivoReinf.R4020.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
                selecionado.getItemEmpregadorR4020().remove(registroEventoRetencaoReinf);
                selecionado.getItemEmpregadorR4020().add(novo);
            }
            registroEventoRetencaoReinf = null;
            FacesUtil.executaJavaScript("dialogVisualizarR2010.hide()");
            FacesUtil.executaJavaScript("dialogVisualizarR24020.hide()");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao criar retificação: ", e.getMessage());
        }

    }

    public void removerRegistro() {
        try {
            reinfFacade.removerRegistroEvento(registroEventoRetencaoReinf);

            if (TipoArquivoReinf.R2010.equals(registroEventoRetencaoReinf.getTipoArquivo()) || TipoArquivoReinf.R2010V2.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
                buscarR2010();
                selecionado.getItemEmpregadorR2010().forEach(item -> {
                    if (item.getPessoa().equals(registroEventoRetencaoReinf.getPessoa()) &&
                        item.getValorTotalBruto().compareTo(registroEventoRetencaoReinf.getValorTotalBruto()) == 0) {
                        registroEventoRetencaoReinf = item;
                    }
                });
            }

            if (TipoArquivoReinf.R4020.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
                buscarR4020();
                selecionado.getItemEmpregadorR4020().forEach(item -> {
                    if (item.getPessoa().equals(registroEventoRetencaoReinf.getPessoa()) &&
                        item.getValorTotalBruto().compareTo(registroEventoRetencaoReinf.getValorTotalBruto()) == 0) {
                        registroEventoRetencaoReinf = item;
                    }
                });
            }

            FacesUtil.addOperacaoRealizada("Registro removido com sucesso.");
        } catch (Exception e) {
            FacesUtil.addMessageError("Erro ao tentar remover o evento", "Detalhe do erro: " + e.getMessage());
            logger.debug("Erro no método removerRegistro", e);
            logger.error("Erro no método removerRegistro. Habilite o debug para visualizar o erro.");
        }
    }

    public void removerNota(NotaReinf obj) {
        List<ReceitaExtraReinf> receitasParaRemover = Lists.newArrayList();

        for (ReceitaExtraReinf receita : registroEventoRetencaoReinf.getReceitas()) {
            if (receita.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getId().equals(obj.getNota().getLiquidacao().getId())) {
                receitasParaRemover.add(receita);
            }
        }
        for (ReceitaExtraReinf receitaExtraReinf : receitasParaRemover) {
            registroEventoRetencaoReinf.getReceitas().remove(receitaExtraReinf);
        }

        registroEventoRetencaoReinf.getNotas().remove(obj);
        atualizarCampos();
    }

    public void removerReceita(ReceitaExtraReinf obj) {
        registroEventoRetencaoReinf.getReceitas().remove(obj);
        atualizarCampos();
    }

    private void atualizarCampos() {
        registroEventoRetencaoReinf.calcularValores();
        registroEventoRetencaoReinf.setMensagem("");
        if (TipoArquivoReinf.R2010.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:tabEventosEnvio:panelR2010");
            FacesUtil.atualizarComponente("formDialogR2010");
        }
        if (TipoArquivoReinf.R2010.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:tabEventosEnvio:panelR2020");
            FacesUtil.atualizarComponente("formDialogR2010");
        }
        if (TipoArquivoReinf.R4020.equals(registroEventoRetencaoReinf.getTipoArquivo())) {
            FacesUtil.atualizarComponente("Formulario:tabViewEnvioEventos:tabEventosEnvio:panelR4020");
            FacesUtil.atualizarComponente("formDialogR4020");
        }
    }

    public void removerPagamento(PagamentoReinf obj) {
        registroEventoRetencaoReinf.getPagamentos().remove(obj);
        atualizarCampos();
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    private void inicializarFiltros() {
    }

    public void alterarTabs() {
        inicializarFiltros();
        if (selecionado.getEmpregador() != null && selecionado.getEmpregador().getId() != null) {
        } else {
            FacesUtil.addOperacaoNaoRealizada("Selecione o empregador para consuttar o arquivo do E-social.");
        }
    }

    public void buscarEventos() {
        try {
            validarBuscaEmpregador();
            eventos = configuracaoEmpregadorESocialFacade.getEventosReinfEmpregadorAndTipoArquivo(selecionado.getEmpregador(), TipoArquivoReinf.R1000, null, 1, 1);
            if (eventos == null) {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado evento para o empregador.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
        }
    }

    public void buscarEventosPorPeriodo() {
        try {
            validarBuscaEmpregador();
            validarMesExercicio();
            List<EventoReinfDTO> eventos = configuracaoEmpregadorESocialFacade.getEventosPorEmpregadorAndPeriodo(selecionado.getEmpregador(), selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno());
            if (eventos == null) {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado evento para o empregador.");
            }
            Collections.sort(eventos, new Comparator<EventoReinfDTO>() {
                @Override
                public int compare(EventoReinfDTO o1, EventoReinfDTO o2) {
                    return o1.getDataOperacao().compareTo(o2.getDataOperacao());
                }
            });
            eventosCompetencia = Lists.newArrayList();
            if (getMostrarSomenteNovosEventos()) {
                for (EventoReinfDTO evento : eventos) {
                    Boolean achou = Boolean.FALSE;
                    for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : selecionado.getItemEmpregadorR2010()) {
                        if (registroEventoRetencaoReinf.getEventos() == null) {
                            registroEventoRetencaoReinf.setEventos(Lists.<EventoRegistroReinfRetencao>newArrayList());
                        }

                        for (EventoRegistroReinfRetencao registroEventoRetencaoReinfEvento : registroEventoRetencaoReinf.getEventos()) {
                            if (evento.getIdXMLEvento().equals(registroEventoRetencaoReinfEvento.getEvento().getIdXmlEvento())) {
                                achou = Boolean.TRUE;
                            }
                        }
                    }
                    if (!achou) {
                        eventosCompetencia.add(evento);
                    }
                }
            } else {
                eventosCompetencia = eventos;
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
        }
    }

    public void vincularEventos() {
        try {
            for (EventoReinfDTO evento : eventosCompetencia) {
                String xml = evento.getXML();
                String tag = "cnpjPrestador";
                String tagPeriodoApuracao = "perApur";

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xml)));
                document.getDocumentElement().normalize();
                NodeList nodeList = document.getElementsByTagName(tag);


                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node item = nodeList.item(i);
                    if (item != null) {

                        NodeList nodeData = document.getElementsByTagName(tagPeriodoApuracao);
                        Node itemData = nodeData.item(i);
                        String data = itemData.getFirstChild().getNodeValue();

                        String ano = data.split("-")[0];
                        String mes = data.split("-")[1];

                        String cnpj = item.getFirstChild().getNodeValue();

                        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : selecionado.getItemEmpregadorR2010()) {
                            if (registroEventoRetencaoReinf.getEventos() == null) {
                                registroEventoRetencaoReinf.setEventos(Lists.<EventoRegistroReinfRetencao>newArrayList());
                            }

                            String cnpjProcurado = StringUtil.removeCaracteresEspeciaisSemEspaco(registroEventoRetencaoReinf.getPessoa().getCpf_Cnpj());
                            if (cnpj.equals(cnpjProcurado) &&
                                registroEventoRetencaoReinf.getMes().getNumeroMes().equals(Integer.valueOf(mes))
                                && registroEventoRetencaoReinf.getExercicio().getAno().equals(Integer.valueOf(ano))) {


                                Boolean achou = Boolean.FALSE;
                                for (EventoRegistroReinfRetencao registroEventoRetencaoReinfEvento : registroEventoRetencaoReinf.getEventos()) {
                                    if (evento.getIdXMLEvento().equals(registroEventoRetencaoReinfEvento.getEvento().getIdXmlEvento())) {
                                        achou = Boolean.TRUE;
                                    }
                                }
                                if (!achou) {
                                    RegistroReinf registroReinf = configuracaoEmpregadorESocialFacade.criarEventoReinf(evento, registroEventoRetencaoReinf.getEmpregador());

                                    registroEventoRetencaoReinf.getEventos().add(new EventoRegistroReinfRetencao(registroEventoRetencaoReinf, registroReinf));

                                    reinfFacade.salvarRegistroEvento(registroEventoRetencaoReinf);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
        }
    }

    public void removerEventosComErro() {
        try {
            Integer eventosRemovidos = 0;
            for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : selecionado.getItemEmpregadorR2010()) {
                if (registroEventoRetencaoReinf.getEventos() == null) {
                    registroEventoRetencaoReinf.setEventos(Lists.<EventoRegistroReinfRetencao>newArrayList());
                }

                if (registroEventoRetencaoReinf.getId() != null) {
                    if (!registroEventoRetencaoReinf.getEventos().isEmpty()) {

                        EventoRegistroReinfRetencao eventoRegistro = registroEventoRetencaoReinf.getEventos().get(registroEventoRetencaoReinf.getEventos().size() - 1);
                        if (SituacaoESocial.PROCESSADO_COM_ERRO.equals(eventoRegistro.getEvento().getSituacao())) {
                            eventosRemovidos++;
                            reinfFacade.removerRegistroEvento(registroEventoRetencaoReinf);
                        }
                    }
                }
            }

            if (eventosRemovidos != 0) {
                FacesUtil.addInfo("Informação", eventosRemovidos + " empresas foram removidas e pode ser enviada novamente.");
            }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
        }
    }

    public List<EventoReinfDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoReinfDTO> eventos) {
        this.eventos = eventos;
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<TipoArquivoReinf> getTipoArquivoReinf() {
        return tipoArquivoReinf;
    }

    public void setTipoArquivoReinf(List<TipoArquivoReinf> tipoArquivoReinf) {
        this.tipoArquivoReinf = tipoArquivoReinf;
    }

    public LazyDataModel<EventoReinfDTO> getEventosTabela() {
        return eventosTabela;
    }

    public void setEventosTabela(LazyDataModel<EventoReinfDTO> eventosTabela) {
        this.eventosTabela = eventosTabela;
    }

    public FiltroReinf getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(FiltroReinf selecionado) {
        this.selecionado = selecionado;
    }

    public AssistenteSincronizacaoReinf getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteSincronizacaoReinf assistente) {
        this.assistente = assistente;
    }

    public RegistroEventoRetencaoReinf getRegistroEventoRetencaoReinf() {
        return registroEventoRetencaoReinf;
    }

    public void setRegistroEventoRetencaoReinf(RegistroEventoRetencaoReinf registroEventoRetencaoReinf) {
        this.registroEventoRetencaoReinf = registroEventoRetencaoReinf;
    }

    public List<EventoReinfDTO> getEventosCompetencia() {
        return eventosCompetencia;
    }

    public void setEventosCompetencia(List<EventoReinfDTO> eventosCompetencia) {
        this.eventosCompetencia = eventosCompetencia;
    }

    public Boolean getMostrarSomenteNovosEventos() {
        if (mostrarSomenteNovosEventos == null) {
            mostrarSomenteNovosEventos = Boolean.FALSE;
        }
        return mostrarSomenteNovosEventos;
    }

    public void setMostrarSomenteNovosEventos(Boolean mostrarSomenteNovosEventos) {
        this.mostrarSomenteNovosEventos = mostrarSomenteNovosEventos;
    }

    public class LazyEventosDataModel extends LazyDataModel<EventoReinfDTO> {

        private List<EventoReinfDTO> datasource;

        public LazyEventosDataModel(List<EventoReinfDTO> datasource) {
            this.datasource = datasource;
        }

        @Override
        public EventoReinfDTO getRowData(String rowKey) {
            for (EventoReinfDTO evento : datasource) {
                if (evento.toString().equals(rowKey)) {
                    return evento;
                }
            }
            return null;
        }

        @Override
        public Object getRowKey(EventoReinfDTO evento) {
            return evento;
        }

        @Override
        public List<EventoReinfDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            try {
                List<EventoReinfDTO> data = new ArrayList<>();

                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("Formulario:eventos");

                Integer dataSize = 0;
                if (dataTable == null) {
                    dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("Formulario:tabViewEnvioEventos:eventos");
                }

                if (selecionado.getSituacao() != null) {
                    data = configuracaoEmpregadorESocialFacade.getEventosReinfEmpregadorAndTipoArquivo(selecionado.getEmpregador(), TipoArquivoReinf.values()[activeIndex], selecionado.getSituacao(), dataTable.getPage(), pageSize);
                    dataSize = configuracaoEmpregadorESocialFacade.getQuantidadeEventosReinfPorEmpregadorAndTipoArquivo(selecionado.getEmpregador(), TipoArquivoReinf.values()[activeIndex], selecionado.getSituacao());
                } else {
                    data = configuracaoEmpregadorESocialFacade.getEventosReinfEmpregadorAndTipoArquivo(selecionado.getEmpregador(), TipoArquivoReinf.values()[activeIndex], null, dataTable.getPage(), pageSize);
                    dataSize = configuracaoEmpregadorESocialFacade.getQuantidadeEventosReinfPorEmpregadorAndTipoArquivo(selecionado.getEmpregador(), TipoArquivoReinf.values()[activeIndex], null);
                }


                this.setRowCount(dataSize);
                return data;
            } catch (Exception e) {
                FacesUtil.addMessageError("Evento não localizado!", "Não foi possível localizar os eventos para o empregador;");

            }
            return null;
        }
    }

    public void gerarRecibo(EventoReinfDTO evento) {
        try {
            List<TipoDoctoOficial> tipoDoctoOficials = tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.RECIBO_REINF, "");
            if (tipoDoctoOficials == null || tipoDoctoOficials.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar a configuração de documento oficial para o(a) " + ModuloTipoDoctoOficial.RECIBO_REINF.getDescricao() + ".");
            }
            TipoDoctoOficial tipoDoctoOficial = tipoDoctoOficials.get(0);
            ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipoDoctoOficial);
            imprimirDocumentoOficial(evento, mod);
        } catch (Exception ex) {
            logger.debug("Erro no método gerarRecibo", ex);
            logger.error("Erro no método gerarRecibo. Habilite o debug para visualizar o erro.");
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void imprimirDocumentoOficial(EventoReinfDTO evento, ModeloDoctoOficial mod) {
        String conteudoModelo = mod.getConteudo();
        UUID uuid = UUID.randomUUID();
        VelocityContext context = new VelocityContext();
        documentoOficialFacade.tagsReciboReinf(context, evento);
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";
        conteudoModelo = conteudoModelo.replace(string1, string2);

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(uuid.toString(), conteudoModelo);
        repo.setEncoding("UTF-8");
        Template template = ve.getTemplate(uuid.toString(), "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        documentoOficialFacade.emiteDocumentoOficial(documentoOficialFacade.montarHtml(Lists.newArrayList(writer.toString())));
    }

    public void atualizarValoresEMenssagemAoAlterarValorRetidoNota() {
        registroEventoRetencaoReinf.setValorTotalRetencao(BigDecimal.ZERO);
        for (NotaReinf nota : registroEventoRetencaoReinf.getNotas()) {
            registroEventoRetencaoReinf.setValorTotalRetencao(registroEventoRetencaoReinf.getValorTotalRetencao().add(nota.getValorRetido()));
        }
        registroEventoRetencaoReinf.setValorTotalLiquido(registroEventoRetencaoReinf.getValorTotalBruto().subtract(registroEventoRetencaoReinf.getValorTotalRetencao()));

        boolean valido = true;
        for (NotaReinf notaReinf : registroEventoRetencaoReinf.getNotas()) {
            if (!notaReinf.isValido()) {
                valido = false;
                break;
            }
            LiquidacaoDoctoFiscal nota = notaReinf.getNota();
            if (nota.getTipoServicoReinf() == null) {
                valido = false;
                break;
            }
            if (com.itextpdf.styledxmlparser.jsoup.helper.StringUtil.isBlank(nota.getDoctoFiscalLiquidacao().getSerie())) {
                valido = false;
                break;
            }
            if (com.itextpdf.styledxmlparser.jsoup.helper.StringUtil.isBlank(nota.getDoctoFiscalLiquidacao().getNumero())) {
                valido = false;
                break;
            }
            Date dataDocto = nota.getDoctoFiscalLiquidacao().getDataDocto();
            Integer mes = DataUtil.getMes(dataDocto);
            Integer ano = DataUtil.getAno(dataDocto);

            if (!selecionado.getMes().getNumeroMes().equals(mes)) {
                valido = false;
                break;
            }
            if (!selecionado.getExercicio().getAno().equals(ano)) {
                valido = false;
                break;
            }
        }

        if (valido) {
            registroEventoRetencaoReinf.setMensagem("");
        } else {
            registroEventoRetencaoReinf.setMensagem(registroEventoRetencaoReinf.getMensagem() + "<br> </br> ");
        }
    }

    public List<SelectItem> getMes() {
        return Util.getListSelectItem(Mes.values(), false);
    }

}

