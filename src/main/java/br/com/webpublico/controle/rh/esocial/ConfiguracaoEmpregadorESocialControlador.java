package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.rh.esocial.CodigoAliquotaFPAEsocial;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.IndicativoContribuicao;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.negocios.rh.esocial.ProcessoAdministrativoJudicialFacade;
import br.com.webpublico.util.*;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.web.client.ResourceAccessException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by William on 05/06/2018.
 */
@ManagedBean(name = "configuracaoEmpregadorESocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-empregador", pattern = "/rh/e-social/configuracao-empregador/novo/", viewId = "/faces/rh/esocial/configuracao-empregador/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-empregador", pattern = "/rh/e-social/configuracao-empregador/editar/#{configuracaoEmpregadorESocialControlador.id}/", viewId = "/faces/rh/esocial/configuracao-empregador/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-empregador", pattern = "/rh/e-social/configuracao-empregador/listar/", viewId = "/faces/rh/esocial/configuracao-empregador/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-empregador", pattern = "/rh/e-social/configuracao-empregador/ver/#{configuracaoEmpregadorESocialControlador.id}/", viewId = "/faces/rh/esocial/configuracao-empregador/visualizar.xhtml"),
    @URLMapping(id = "ver-eventos-empregador", pattern = "/rh/e-social/eventos/ver/", viewId = "/faces/rh/esocial/eventos/visualizar.xhtml"),
    @URLMapping(id = "ver-eventos-empregador-id", pattern = "/rh/e-social/eventos/ver/#{configuracaoEmpregadorESocialControlador.id}/", viewId = "/faces/rh/esocial/eventos/visualizar.xhtml")
})
public class ConfiguracaoEmpregadorESocialControlador extends PrettyControlador<ConfiguracaoEmpregadorESocial> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private ProcessoAdministrativoJudicialFacade processoAdministrativoJudicialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais = Lists.newArrayList();
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataInicioParametroFiltro;
    private Date dataFimParametroFiltro;
    private ItemConfiguracaoEmpregadorESocial itemConfiguracaoEmpregadorESocial;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private List<EventoESocialDTO> eventos;
    private List<OcorrenciaESocialDTO> ocorrencias;
    private String xml;
    private String descricaoResposta;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;

    private IndicativoContribuicao indicativoContribuicao;
    private Map<ItemConfiguracaoEmpregadorESocial, HierarquiaOrganizacional> mapCacheHierarquia;


    public ConfiguracaoEmpregadorESocialControlador() {
        super(ConfiguracaoEmpregadorESocial.class);
        mapCacheHierarquia = Maps.newHashMap();
    }


    public Date getDataInicioParametroFiltro() {
        return dataInicioParametroFiltro;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void setDataInicioParametroFiltro(Date dataInicioParametroFiltro) {
        this.dataInicioParametroFiltro = dataInicioParametroFiltro;
    }

    public Date getDataFimParametroFiltro() {
        return dataFimParametroFiltro;
    }

    public void setDataFimParametroFiltro(Date dataFimParametroFiltro) {
        this.dataFimParametroFiltro = dataFimParametroFiltro;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public ItemConfiguracaoEmpregadorESocial getItemConfiguracaoEmpregadorESocial() {
        return itemConfiguracaoEmpregadorESocial;
    }

    public void setItemConfiguracaoEmpregadorESocial(ItemConfiguracaoEmpregadorESocial itemConfiguracaoEmpregadorESocial) {
        this.itemConfiguracaoEmpregadorESocial = itemConfiguracaoEmpregadorESocial;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoEmpregadorESocialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/configuracao-empregador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<EventoESocialDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoESocialDTO> eventos) {
        this.eventos = eventos;
    }

    @URLAction(mappingId = "nova-configuracao-empregador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
        selecionado.setSenhaCertificado(null);
    }

    @URLAction(mappingId = "editar-configuracao-empregador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributos();
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, configuracaoEmpregadorESocialFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    private void inicializarAtributos() {
        carregarHierarquiasOrganizacionais();
        indicativoContribuicao = new IndicativoContribuicao();
        dataInicioParametroFiltro = DataUtil.getPrimeiroDiaAno(configuracaoEmpregadorESocialFacade.getSistemaFacade().getExercicioCorrente().getAno());
        dataFimParametroFiltro = DataUtil.getUltimoDiaAno(configuracaoEmpregadorESocialFacade.getSistemaFacade().getExercicioCorrente().getAno());
        eventos = Lists.newLinkedList();

    }

    @URLAction(mappingId = "ver-configuracao-empregador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    @URLAction(mappingId = "ver-eventos-empregador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEventos() {

        eventos = Lists.newLinkedList();
    }

    @URLAction(mappingId = "ver-eventos-empregador-id", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEventosAtalho() {
        eventos = Lists.newLinkedList();
        selecionado = configuracaoEmpregadorESocialFacade.recuperar(getId());
    }


    public List<Entidade> completarEntidade(String parte) {
        List<Entidade> entidades = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : buscarHierarquiasVigentesNoPeriodoNoNivel()) {
            entidades.add(entidadeFacade.completarEntidadePelaUnidadeOrganizacional(parte, ho.getSubordinada(), sistemaFacade.getDataOperacao()));
        }
        return entidades;

    }

    public List<SelectItem> getTiposAmbienteESocial() {
        return Util.getListSelectItem(TipoAmbienteESocial.values(), false);
    }

    public List<SelectItem> getTiposLotacaoTributaria() {
        return Util.getListSelectItem(TipoLotacaoTributariaESocial.values(), false);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getDescricaoResposta() {
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public void carregarHierarquiasOrganizacionais() {
        hierarquiasOrganizacionais.clear();
        hierarquiasOrganizacionais = buscarHierarquiasVigentesNoPeriodoNoNivel();
    }

    private List<HierarquiaOrganizacional> buscarHierarquiasVigentesNoPeriodoNoNivel() {
        return configuracaoEmpregadorESocialFacade.getHierarquiaOrganizacionalFacade().
            buscarHierarquiaUsuarioPorTipo(sistemaFacade.getUsuarioCorrente(), dataFimParametroFiltro, TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return configuracaoEmpregadorESocialFacade.getHierarquiaOrganizacionalFacade().
            buscarHierarquiasFiltrandoVigentesPorPeriodoAndTipo(parte.trim(), dataInicioParametroFiltro, dataFimParametroFiltro, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (validaRegrasParaSalvar()) {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao salvar Empregador ESocial: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao salvar Empregador ESocial: ", e.getMessage());
            logger.error("Erro ao salvar Configuração: ", e);
        }


    }

    public IndicativoContribuicao getIndicativoContribuicao() {
        return indicativoContribuicao;
    }

    public void setIndicativoContribuicao(IndicativoContribuicao indicativoContribuicao) {
        this.indicativoContribuicao = indicativoContribuicao;
    }

    @Override
    protected boolean validaRegrasParaSalvar() {
        return super.validaRegrasParaSalvar();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Entidade.");
            ve.lancarException();
        }
        if (configuracaoEmpregadorESocialFacade.hasConfiguracaoParaEntidade(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Entidade " + selecionado.getEntidade() + " já possui configuração.");
        }
        if (selecionado.getInicioVigencia() != null && selecionado.getFinalVigencia() != null) {
            if (DataUtil.verificaDataFinalMaiorQueDataInicial(selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O final de vigência deve ser maior que o início de vigência.");
            }
        }
        if (selecionado.getMenorAprendizEducativo()) {
            if (selecionado.getEntidadeEducativa() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A <b>Entidade Educativa ou de prática desportiva </b> deve ser informado, pois possui menor aprendiz por entidade educativa.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getHierarquias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional hierarquia : hierarquiasOrganizacionais) {
            toReturn.add(new SelectItem(hierarquia, hierarquia.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial hierarquia : configuracaoEmpregadorESocialFacade.lista()) {
            toReturn.add(new SelectItem(hierarquia, hierarquia.getEntidade() + " "));
        }
        return toReturn;
    }


    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoPessoaJuridicaESocial poder : SituacaoPessoaJuridicaESocial.values()) {
            toReturn.add(new SelectItem(poder, poder.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposPontoEletronico() {
        return Util.getListSelectItem(TipoPontoEletronicoESocial.values(), false);
    }

    public List<SelectItem> getProcessosJudiciais() {
        return Util.getListSelectItem(processoAdministrativoJudicialFacade.lista(), false);
    }

    public List<SelectItem> getIndicativosMenorAprendiz() {
        return Util.getListSelectItem(IndicativoMenorAprendizESocial.values(), false);
    }

    public List<SelectItem> getTiposConstratacaoDeficiencia() {
        return Util.getListSelectItem(TipoContratacaoDeficienciaESocial.values(), false);
    }

    public List<SelectItem> getPoderesSubTeto() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaDoPoder poder : EsferaDoPoder.values()) {
            toReturn.add(new SelectItem(poder, poder.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getClassificacoesTributariasEsocial() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ClassificacaoTributariaESocial classTrib : ClassificacaoTributariaESocial.values()) {
            toReturn.add(new SelectItem(classTrib, classTrib.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOpcoesPontoEletronico() {
        return Util.getListSelectItem(OptouPontoEletronico.values(), false);
    }

    public void novoOrgaoConfiguracao() {
        itemConfiguracaoEmpregadorESocial = new ItemConfiguracaoEmpregadorESocial();
        carregarHierarquiasOrganizacionais();
    }

    public void adicionarOrgao() {
        try {
            validarOrgao();
            itemConfiguracaoEmpregadorESocial.setConfigEmpregadorEsocial(selecionado);
            itemConfiguracaoEmpregadorESocial.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            selecionado.getItemConfiguracaoEmpregadorESocial().add(itemConfiguracaoEmpregadorESocial);
            setarNullItemConfiguracaoESocial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerCargo(ItemConfiguracaoEmpregadorESocial item) {
        selecionado.getItemConfiguracaoEmpregadorESocial().remove(item);
    }

    private void validarOrgao() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Órgão.");
        } else {
            if (selecionado.getItemConfiguracaoEmpregadorESocial() != null && !selecionado.getItemConfiguracaoEmpregadorESocial().isEmpty()) {
                for (ItemConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial : selecionado.getItemConfiguracaoEmpregadorESocial()) {
                    if (configuracaoEmpregadorESocial.getUnidadeOrganizacional().equals(hierarquiaOrganizacional.getSubordinada())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Órgão " + hierarquiaOrganizacional + " já foi adicionado.");
                    }
                }
            }
            List<ConfiguracaoEmpregadorESocial> configuracaoes = configuracaoEmpregadorESocialFacade.buscarConfiguracoesVigentesPeloCodigoDaHierarquiaDoItem(hierarquiaOrganizacional.getCodigo(), sistemaFacade.getDataOperacao());
            for (ConfiguracaoEmpregadorESocial conf : configuracaoes) {
                if (selecionado.getId() == null || !selecionado.getId().equals(conf.getId())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Órgão " + hierarquiaOrganizacional + " já está vinculado a configuração do empregador " + conf + ".");
                }
            }
        }

        ve.lancarException();
    }

    public void setarNullItemConfiguracaoESocial() {
        itemConfiguracaoEmpregadorESocial = null;
    }

    public void novaSenha() {
        selecionado.setSenhaCertificado(null);
    }


    public void enviarS1000() {
        try {
            configuracaoEmpregadorESocialFacade.enviarS1000(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-1000: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-1000: ", e.getMessage());
            logger.error("Erro ao salvar Configuração: ", e);
        }
    }

    public void enviarS1010() {
        try {
            configuracaoEmpregadorESocialFacade.enviarS1010(selecionado, null);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-1010: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-1010: ", e.getMessage());
            logger.error("Erro ao salvar Configuração: ", e);
        }
    }

    public void enviarS1020() {
        try {
            configuracaoEmpregadorESocialFacade.enviarS1020(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-1020: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-1020: ", e.getMessage());
            logger.error("Erro ao salvar Configuração: ", e);
        }
    }

    public void enviarS1005() {
        try {
            configuracaoEmpregadorESocialFacade.enviarS1005(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-1005: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-1005: ", e.getMessage());
            logger.error("Erro ao salvar Configuração: ", e);
        }

    }


    public void enviarCargaInicialS1070() {
        try {
            configuracaoEmpregadorESocialFacade.enviarCargaInicialS1070(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-1070: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-1070: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS1070: ", e);
        }
    }

    public void enviarCargaInicialS2200() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            future = configuracaoEmpregadorESocialFacade.enviarCargaInicialS2200(selecionado, assistenteBarraProgresso, sistemaFacade.getDataOperacao());
            FacesUtil.executaJavaScript("acompanhaEnvioEvento()");
            FacesUtil.executaJavaScript("aguarde.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            logger.error("Erro ao validar: ");
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2200: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2200: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS2200: ", e);
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void verificarSeTerminou() {
        if (future != null && future.isDone()) {
            if (assistenteBarraProgresso.getMensagensValidacaoFacesUtil() != null && !assistenteBarraProgresso.getMensagensValidacaoFacesUtil().isEmpty()) {
                for (FacesMessage facesMessage : assistenteBarraProgresso.getMensagensValidacaoFacesUtil()) {
                    FacesUtil.addOperacaoNaoPermitida(facesMessage.getDetail());
                }
            } else {
                FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            }
            future = null;
            FacesUtil.executaJavaScript("termina()");
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }


    public void enviarCargaInicialS2205() {
        try {
            configuracaoEmpregadorESocialFacade.enviarCargaInicialS2205(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2205: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2205: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS1030: ", e);
        }
    }

    public void enviarCargaInicialS2206() {
        try {
            configuracaoEmpregadorESocialFacade.enviarCargaInicialS2206(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2206: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2206: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS1030: ", e);
        }
    }

    public void enviarCargaInicialS2300() {
        try {
            configuracaoEmpregadorESocialFacade.enviarCargaInicialS2300(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2300: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2300: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS2298: ", e);
        }
    }


    public void enviarCargaInicialS2405() {
        try {
            configuracaoEmpregadorESocialFacade.enviarCargaInicialS2405(selecionado);
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao enviar S-2405: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao enviar S-2405: ", e.getMessage());
            logger.error("Erro ao enviarCargaInicialS2405: ", e);
        }
    }

    public void buscarEventos() {
        try {
            if (selecionado != null) {
                eventos = configuracaoEmpregadorESocialFacade.getEventosEmpregador(selecionado.getEntidade());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ResourceAccessException rax) {
            FacesUtil.addError("Erro ao buscar eventos: ", rax.getMessage());
            logger.error("Erro ao salvar Configuração: ", rax);
        } catch (Exception e) {
            FacesUtil.addError("Erro ao buscar eventos: ", e.toString());
            logger.error("Erro ao buscar eventos: ", e);
        }

    }


    public void verEventoEmpregador() {
        FacesUtil.redirecionamentoInterno("/rh/e-social/eventos/ver/" + selecionado.getId());
    }

    public List<SelectItem> getCodigoAliquota() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (CodigoAliquotaFPAEsocial codigoAliquota : configuracaoEmpregadorESocialFacade.getCodigoAliquotaFPAEsocialFacade().buscarCodigoAliquotaFPAEsocial()) {
            toReturn.add(new SelectItem(codigoAliquota, codigoAliquota.getCodigo() + " - " + codigoAliquota.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getMes() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getIndicativoSubstituicao() {
        return Util.getListSelectItem(IndicativoSubstituicao.values(), false);
    }

    public void limparMes() {
        indicativoContribuicao.setMes(null);
    }


    public void removerIndicativoContribuicao(IndicativoContribuicao indicativoContribuicao) {
        selecionado.getItemIndicativoContribuicao().remove(indicativoContribuicao);
    }

    public void adicionarIndicativoContribuicao() {
        try {
            validarIndicativoContribuicao();
            indicativoContribuicao.setEmpregador(selecionado);
            selecionado.getItemIndicativoContribuicao().add(indicativoContribuicao);
            indicativoContribuicao = new IndicativoContribuicao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarIndicativoContribuicao() {
        ValidacaoException ve = new ValidacaoException();
        if (indicativoContribuicao.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano.");
        }
        if (indicativoContribuicao.getMes() == null && !indicativoContribuicao.getDecimoTerceiro()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (indicativoContribuicao.getIndicativoSubstituicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Indicativo de substituição da contribuição.");
        }
        if (indicativoContribuicao.getPercentualContribuicao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Percentual de Contribuição.");
        }
        if (indicativoContribuicao.getFatorMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Fator Mês.");
        }
        if (indicativoContribuicao.getContribuicaoSocial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Contribuição Social.");
        }
        if (indicativoContribuicao.getExercicio() != null) {
            for (IndicativoContribuicao contribuicao : selecionado.getItemIndicativoContribuicao()) {
                if (indicativoContribuicao.getMes() != null) {
                    if (indicativoContribuicao.getExercicio().equals(contribuicao.getExercicio()) && indicativoContribuicao.getMes().equals(contribuicao.getMes())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe configuração para esse Mês/Ano");
                        break;
                    }
                } else {
                    if (indicativoContribuicao.getExercicio().equals(contribuicao.getExercicio()) && indicativoContribuicao.getDecimoTerceiro() == contribuicao.getDecimoTerceiro()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe configuração para esse Ano com a opção de Décimo Terceiro.");
                        break;
                    }
                }

            }
        }
        ve.lancarException();
    }

    public HierarquiaOrganizacional hierarquiaDoItemConfiguracao(ItemConfiguracaoEmpregadorESocial item) {
        if (mapCacheHierarquia.containsKey(item)) {
            return mapCacheHierarquia.get(item);
        }
        HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(item.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
        mapCacheHierarquia.put(item, hierarquia);
        return hierarquia;
    }
}
