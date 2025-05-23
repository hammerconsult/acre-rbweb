/*
 * Codigo gerado automaticamente em Mon Feb 28 16:58:30 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.AnexoLei1232006;
import br.com.webpublico.nfse.domain.LivroFiscal;
import br.com.webpublico.nfse.enums.VersaoDesif;
import br.com.webpublico.nfse.facades.AnexoLei1232006Facade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.tributario.dto.PessoaJuridicaDTO;
import br.com.webpublico.tributario.dto.SociedadePessoaJuridicaDTO;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.util.anotacoes.SessaoManual;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@ManagedBean(name = "cadastroEconomicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "cadastrarCadastroEconomicoTributario",
        pattern = "/tributario/cadastroeconomico/novo/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/edita.xhtml"),
    @URLMapping(id = "cadastrarCadastroEconomicoRBTrans",
        pattern = "/tributario/cadastroeconomico/novo/permissionario/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/edita.xhtml"),
    @URLMapping(id = "cadastrarCadastroEconomicoMotoristaRBTrans",
        pattern = "/tributario/cadastroeconomico/novo/motorista/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/edita.xhtml"),
    @URLMapping(id = "alterarCadastroEconomicoTributario",
        pattern = "/tributario/cadastroeconomico/editar/#{cadastroEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/edita.xhtml"),
    @URLMapping(id = "listarCadastroEconomicoTributario",
        pattern = "/tributario/cadastroeconomico/listar/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/lista.xhtml"),
    @URLMapping(id = "visualizarCadastroEconomicoTributario",
        pattern = "/tributario/cadastroeconomico/ver/#{cadastroEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/visualizar.xhtml"),

    @URLMapping(id = "visualizarCadastroEconomicoTributarioRetroativo",
        pattern = "/tributario/cadastroeconomico/ver-retroativo/#{cadastroEconomicoControlador.id}/revisao/#{cadastroEconomicoControlador.idRevisao}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/visualizar.xhtml"),

    @URLMapping(id = "cadastrarCadastroEconomicoAlvara",
        pattern = "/tributario/cadastroeconomico-do-alvara/novo/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/alvara/edita.xhtml"),
    @URLMapping(id = "alterarCadastroEconomicoAlvara",
        pattern = "/tributario/cadastroeconomico-do-alvara/editar/#{cadastroEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/alvara/edita.xhtml"),
    @URLMapping(id = "listarCadastroEconomicoAlvara",
        pattern = "/tributario/cadastroeconomico-do-alvara/listar/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/alvara/lista.xhtml"),
    @URLMapping(id = "visualizarCadastroEconomicoAlvara",
        pattern = "/tributario/cadastroeconomico-do-alvara/ver/#{cadastroEconomicoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroeconomico/alvara/visualizar.xhtml"),
})
public class CadastroEconomicoControlador extends PrettyControlador<CadastroEconomico> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CadastroEconomicoControlador.class);
    private static final String KEY_SESSAO_TIPO_CADASTRO = "TIPOCADASTRO";
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private AlteracaoCmcFacade alteracaoCmcFacade;
    @EJB
    private CertidaoAtividadeBCEFacade certidaoAtividadeBCEFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private AnexoLei1232006Facade anexoLei1232006Facade;
    private ConverterAutoComplete converterEscritorioContabil;
    private ConverterGenerico converterTipoDocumentoFiscal;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterServico;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterCnae;
    private ConverterGenerico converterOficio;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterTributo;
    private ConverterGenerico converterTipoautonomo;
    private ConverterGenerico converterCaracteristica;
    private ConverterAutoComplete converterCEP;
    private ConverterAutoComplete converterLogradouro;
    private ConverterAutoComplete converterBairro;
    private ConverterAutoComplete converterNaturezaJuridica;

    private EconomicoCNAE cnae;
    private Servico servico;
    private EnderecoCadastroEconomico endereco;
    private List<FileUploadEvent> fileUploadEvents;
    private List<Servico> listaServico;
    private List<CadastroEconomico> lista;
    private Lote lote;
    private RegistroJuntas juntas;
    private EnquadramentoFiscal enquadramentoFiscal;
    @SessaoManual
    private SociedadeCadastroEconomico socio;
    private SituacaoCadastroEconomico situacaoCadastroEconomico;
    private TipoProcessoEconomico tipoProcessoEconomico;
    private boolean alterandoCNAE;
    private Historico historico;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String codigoVerificador;
    private PlacaAutomovelCmc placa;
    private PermissaoTransporte permissaoTransporte;
    private StreamedContent imagemFoto;
    private List<RegistroDB> historicoLegado;
    private boolean mostraHistoricoLegado;
    private BCECaracFuncionamento bcecf;
    private Long id;
    private IsencaoCadastroEconomico isencao;
    private Servico servicoFiltro;
    private Boolean cadastroDoAlvara;
    private Integer activeIndex;
    private Boolean editandoEnquadramento;
    private Long idRevisao;
    private Date dataRetroativa;
    private List<Historico> historicos;

    private List<JuntaComercialPessoaJuridica> juntasComercialPessoaJuridica;
    private List<SociedadePessoaJuridica> sociedadesPessoaJuridica;
    private List<PessoaCNAE> cnaesPessoa;
    private List<RepresentanteLegalPessoa> representantesPessoaJuridica;
    private List<FilialPessoaJuridica> filiaisPessoaJuridica;
    private EventoRedeSimDTO eventoRedeSimDTO;
    private List<HistoricoAlteracaoRedeSim> listaHistoricosRedeSimPessoa;

    public Boolean autonomoMotorista;
    private Boolean mei;
    private EnquadramentoAmbiental enquadramentoAmbiental;
    private List<RequerimentoLicenciamentoETR> requerimentoLicenciamentoETRList;

    private List<HistoricoInscricaoCadastral> historicosInscricaoCadastral;
    private List<AnexoLei1232006> anexosLei1232006;

    public CadastroEconomicoControlador() {
        super(CadastroEconomico.class);
    }

    public void changeTab(TabChangeEvent evt) {
        TabView tabView = (TabView) evt.getComponent();
        activeIndex = tabView.getChildren().indexOf(evt.getTab());
    }

    public PessoaJuridicaDTO getPessoaRedeSim() {
        return eventoRedeSimDTO != null ? eventoRedeSimDTO.getPessoaDTO() : null;
    }

    @URLActions(actions = {
        @URLAction(mappingId = "cadastrarCadastroEconomicoRBTrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false),
        @URLAction(mappingId = "cadastrarCadastroEconomicoMotoristaRBTrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    })
    public void novoCadastroEconomicoRBTrans() {
        try {
            Date dataAtual = new Date();
            Web.pegaTodosFieldsNaSessao(this);
            operacao = Operacoes.NOVO;
            if (selecionado == null) {
                permissaoTransporte = (PermissaoTransporte) Web.pegaDaSessao("permissaoTransporte");
                if (permissaoTransporte != null) {
                    selecionado = new CadastroEconomico();
                    inicializaCadastro();
                    enquadramentoFiscal.setCadastroEconomico(selecionado);
                    enquadramentoFiscal.setInicioVigencia(dataAtual);
                    enquadramentoFiscal.setTipoIssqn(TipoIssqn.FIXO);
                    selecionado.getEnquadramentos().add(enquadramentoFiscal);
                    enquadramentoFiscal = new EnquadramentoFiscal();
                    selecionado.setResponsavelPeloCadastro(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
                    ParametrosTransitoTransporte parametro = getFacade().getPermissaoTransporteFacade()
                        .getParametrosTransitoTransporteFacade()
                        .recuperarParametroVigentePeloTipo(getPermissaoTransporte().getTipoPermissaoRBTrans());
                    if (parametro != null) {
                        selecionado.setTipoAutonomo(parametro.getTipoAutonomo());
                        selecionado.setNaturezaJuridica(parametro.getNaturezaJuridica());
                    } else {
                        FacesUtil.addAtencao("Não existe parametro, vigente, de " + permissaoTransporte.getTipoPermissaoRBTrans().getDescricao() + " cadastrado.");
                        redireciona();
                    }
                } else {
                    FacesUtil.addAtencao("Não foi possível selecionar uma permissão");
                    redireciona();
                }
            }
        } catch (IllegalAccessException e) {
            logger.debug(e.getMessage());
        }
    }

    public Boolean getCadastroDoAlvara() {
        return cadastroDoAlvara != null ? cadastroDoAlvara : Boolean.FALSE;
    }

    public Boolean getEditandoEnquadramento() {
        return editandoEnquadramento != null ? editandoEnquadramento : false;
    }

    public Date getDataRetroativa() {
        return dataRetroativa;
    }

    public void setEditandoEnquadramento(Boolean editandoEnquadramento) {
        this.editandoEnquadramento = editandoEnquadramento;
    }

    @Override
    @URLAction(mappingId = "alterarCadastroEconomicoTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        defineSessao();
        cadastroDoAlvara = false;
        operacao = Operacoes.EDITAR;
        situacaoCadastroEconomico = new SituacaoCadastroEconomico();
        selecionar();
        if (isVerificarSituacao()) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    private void ordenarEnquadramentos() {
        Collections.sort(selecionado.getEnquadramentos(), new Comparator<EnquadramentoFiscal>() {
            @Override
            public int compare(EnquadramentoFiscal o1, EnquadramentoFiscal o2) {
                return ComparisonChain.start().compare(o1.getInicioVigencia(), o2.getInicioVigencia(), Ordering.<Date>natural().reverse()).result();
            }
        });
    }

    private void defineSessao() {
        setSessao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao"));
    }

    @URLAction(mappingId = "alterarCadastroEconomicoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAlvara() {
        defineSessao();
        cadastroDoAlvara = true;
        operacao = Operacoes.EDITAR;
        situacaoCadastroEconomico = new SituacaoCadastroEconomico();
        selecionar();
    }

    @URLAction(mappingId = "visualizarCadastroEconomicoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAlvara() {
        cadastroDoAlvara = true;
        operacao = Operacoes.VER;
        selecionar();
    }

    @Override
    @URLAction(mappingId = "visualizarCadastroEconomicoTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        cadastroDoAlvara = false;
        operacao = Operacoes.VER;
        selecionar();
    }

    @URLAction(mappingId = "visualizarCadastroEconomicoTributarioRetroativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRetroativo() {
        RevisaoAuditoria rev = (RevisaoAuditoria) cadastroEconomicoFacade.getAuditoriaFacade().recuperar(RevisaoAuditoria.class, idRevisao);
        dataRetroativa = rev.getDataHora();
        selecionarPorRevisao(getId(), getIdRevisao());
    }

    @URLAction(mappingId = "cadastrarCadastroEconomicoTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void criarCadastroEconomico() {
        cadastroDoAlvara = false;
        operacao = Operacoes.NOVO;
        novoCadastroEconomico();
    }

    @URLAction(mappingId = "cadastrarCadastroEconomicoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void criarCadastroEconomicoAlvara() {
        cadastroDoAlvara = true;
        operacao = Operacoes.NOVO;
        novoCadastroEconomico();
    }

    public void selecionarPorRevisao(Long id, Long idRevisao) {
        try {
            CadastroEconomico bceRecuperado = cadastroEconomicoFacade.recuperarPorRevisao(id, idRevisao);
            if (bceRecuperado != null) {
                selecionado = bceRecuperado;
            }
            selecionar();
        } catch (EJBException ej) {
            if (ej.getCausedByException() instanceof EntityNotFoundException) {
                logger.error("Erro EJBException ao inicializar lista hibernate na revisão " + idRevisao + ": {}", ej);
                cadastroEconomicoFacade.getAuditoriaFacade().criarAuditoriaFromEntityNotFound(ej.getCausedByException().getMessage());
                selecionarPorRevisao(id, idRevisao);
            }
        } catch (EntityNotFoundException e) {
            logger.error("Erro EntityNotFoundException ao inicializar lista hibernate na revisão " + idRevisao + ": {}", e);
            cadastroEconomicoFacade.getAuditoriaFacade().criarAuditoriaFromEntityNotFound(e.getMessage());
            selecionarPorRevisao(id, idRevisao);
        } catch (Exception e) {
            logger.debug("Deu exception e vai tentar criar o registros!");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public PlacaAutomovelCmc getPlaca() {
        return placa;
    }

    public void setPlaca(PlacaAutomovelCmc placa) {
        this.placa = placa;
    }

    public boolean isAlterandoCNAE() {
        return alterandoCNAE;
    }

    public void setAlterandoCNAE(boolean alterandoCNAE) {
        this.alterandoCNAE = alterandoCNAE;
    }

    public ConverterAutoComplete getConverterServico() {
        if (converterServico == null) {
            converterServico = new ConverterAutoComplete(Servico.class, cadastroEconomicoFacade.getServicoFacade());
        }
        return converterServico;
    }

    public Converter getConverterTipoautonomo() {
        if (converterTipoautonomo == null) {
            converterTipoautonomo = new ConverterGenerico(TipoAutonomo.class, cadastroEconomicoFacade.getTipoAutonomoFacade());
        }
        return converterTipoautonomo;
    }

    public Converter getConverterOficio() {
        if (converterOficio == null) {
            converterOficio = new ConverterGenerico(AtoLegal.class, cadastroEconomicoFacade.getAtoLegalFacade());
        }
        return converterOficio;
    }

    public List<CadastroEconomico> getLista() {
        if (lista == null) {
            lista = new ArrayList<>();
        }
        return lista;
    }

    public void setLista(List<CadastroEconomico> lista) {
        this.lista = lista;
    }

    public IsencaoCadastroEconomico getIsencao() {
        return isencao;
    }

    public void setIsencao(IsencaoCadastroEconomico isencao) {
        this.isencao = isencao;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public EconomicoCNAE getCnae() {
        return cnae;
    }

    public void setCnae(EconomicoCNAE cnae) {
        this.cnae = cnae;
    }

    public CadastroEconomicoFacade getFacade() {
        return cadastroEconomicoFacade;
    }

    public SociedadeCadastroEconomico getSocio() {
        return socio;
    }

    public void setSocio(SociedadeCadastroEconomico socio) {
        this.socio = socio;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(SituacaoCadastroEconomico situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public EnderecoCadastroEconomico getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCadastroEconomico endereco) {
        this.endereco = endereco;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public Integer getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(Integer activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<Telefone> getTelefones() {
        List<Telefone> toReturn = new ArrayList<>();
        if (selecionado != null && selecionado.getPessoa() != null) {
            toReturn = cadastroEconomicoFacade.getPessoaFacade().telefonePorPessoa(selecionado.getPessoa());
        }
        return toReturn;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroEconomicoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public RegistroJuntas getJuntas() {
        return juntas;
    }

    public void setJuntas(RegistroJuntas juntas) {
        this.juntas = juntas;
    }

    public EnquadramentoFiscal getEnquadramentoFiscal() {
        return enquadramentoFiscal;
    }

    public void setEnquadramentoFiscal(EnquadramentoFiscal enquadramentoFiscal) {
        this.enquadramentoFiscal = enquadramentoFiscal;
    }

    public TipoProcessoEconomico getTipoProcessoEconomico() {
        return tipoProcessoEconomico;
    }

    public void setTipoProcessoEconomico(TipoProcessoEconomico tipoProcessoEconomico) {
        this.tipoProcessoEconomico = tipoProcessoEconomico;
    }

    public List<FilialPessoaJuridica> getFiliaisPessoaJuridica() {
        return filiaisPessoaJuridica;
    }

    public void setFiliaisPessoaJuridica(List<FilialPessoaJuridica> filiaisPessoaJuridica) {
        this.filiaisPessoaJuridica = filiaisPessoaJuridica;
    }

    public List<RepresentanteLegalPessoa> getRepresentantesPessoaJuridica() {
        return representantesPessoaJuridica;
    }

    public void setRepresentantesPessoaJuridica(List<RepresentanteLegalPessoa> representantesPessoaJuridica) {
        this.representantesPessoaJuridica = representantesPessoaJuridica;
    }

    public List<PessoaCNAE> getCnaesPessoa() {
        return cnaesPessoa;
    }

    public void setCnaesPessoa(List<PessoaCNAE> cnaesPessoa) {
        this.cnaesPessoa = cnaesPessoa;
    }

    public List<SociedadePessoaJuridica> getSociedadesPessoaJuridica() {
        return sociedadesPessoaJuridica;
    }

    public void setSociedadesPessoaJuridica(List<SociedadePessoaJuridica> sociedadesPessoaJuridica) {
        this.sociedadesPessoaJuridica = sociedadesPessoaJuridica;
    }

    public List<JuntaComercialPessoaJuridica> getJuntasComercialPessoaJuridica() {
        return juntasComercialPessoaJuridica;
    }

    public void setJuntasComercialPessoaJuridica(List<JuntaComercialPessoaJuridica> juntasComercialPessoaJuridica) {
        this.juntasComercialPessoaJuridica = juntasComercialPessoaJuridica;
    }

    public EnquadramentoAmbiental getEnquadramentoAmbiental() {
        return enquadramentoAmbiental;
    }

    public void setEnquadramentoAmbiental(EnquadramentoAmbiental enquadramentoAmbiental) {
        this.enquadramentoAmbiental = enquadramentoAmbiental;
    }

    public boolean isPessoaFisica() {
        return selecionado.getPessoa() == null || selecionado.getPessoa() instanceof PessoaFisica;
    }

    public boolean podeEditarOuExcluirEnquadramento() {
        if (isPessoaFisica()) {
            if (selecionado.getNaturezaJuridica() == null) return false;
            return selecionado.getTipoAutonomo() != null && selecionado.getTipoAutonomo().getEditaEnquadramento();
        }
        return true;
    }

    public void atualizarPainelAutonomo() {
        if (enquadramentoFiscal != null) {
            enquadramentoFiscal.setTipoIssqn(TipoIssqn.FIXO);
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public void atualizarDadosPessoa() {
        if (selecionado.getPessoa() != null) {
            if (isOperacaoNovo() && selecionado.getEnquadramentos() != null) {
                selecionado.getEnquadramentos().clear();
            }
            if (selecionado.getPessoa() instanceof PessoaJuridica) {
                PessoaJuridica pessoaJuridica = (PessoaJuridica) cadastroEconomicoFacade.getPessoaFacade().recuperarPJ(selecionado.getPessoa().getId());
                juntasComercialPessoaJuridica = pessoaJuridica.getJuntaComercial();
                sociedadesPessoaJuridica = pessoaJuridica.getSociedadePessoaJuridica();
                representantesPessoaJuridica = pessoaJuridica.getRepresentantesLegal();
                filiaisPessoaJuridica = pessoaJuridica.getFiliais();

                for (JuntaComercialPessoaJuridica juntaComercial : juntasComercialPessoaJuridica) {
                    selecionado.setNaturezaJuridica(juntaComercial.getNaturezaJuridica());
                }
                selecionado.getSociedadeCadastroEconomico().clear();
                for (SociedadePessoaJuridica sociedade : sociedadesPessoaJuridica) {
                    adicionarSociedadeCadastroEconomico(sociedade.getSocio(), sociedade.getDataInicio(), sociedade.getDataFim(), sociedade.getProporcao());
                }
                selecionado.setPessoa(pessoaJuridica);
                cadastroEconomicoFacade.adicionarServicos(selecionado, pessoaJuridica);
            } else if (selecionado.getPessoa() instanceof PessoaFisica) {
                PessoaFisica pessoaFisica = (PessoaFisica) cadastroEconomicoFacade.getPessoaFacade().recuperarPF(selecionado.getPessoa().getId());
                selecionado.getSociedadeCadastroEconomico().clear();
                adicionarSociedadeCadastroEconomico(selecionado.getPessoa(), new Date(), null, 100.0);
                adicionarInformacoesEnquadramentoFiscal();
                selecionado.setPessoa(pessoaFisica);
                selecionado.setClassificacaoAtividade(ClassificacaoAtividade.PROFISSIONAL_AUTONOMO);
                FacesUtil.executaJavaScript("abrirExpandDataTable()");
            }
            cnaesPessoa = selecionado.getPessoa().getPessoaCNAEAtivos();
            selecionado.getEconomicoCNAE().clear();
            for (PessoaCNAE pessoaCNAE : cnaesPessoa) {
                if (pessoaCNAE.getExercidaNoLocal() != null && pessoaCNAE.getExercidaNoLocal()) {
                    EconomicoCNAE cnae = new EconomicoCNAE();
                    cnae.setCadastroEconomico(selecionado);
                    cnae.setCnae(pessoaCNAE.getCnae());
                    cnae.setInicio(pessoaCNAE.getInicio());
                    cnae.setFim(pessoaCNAE.getFim());
                    cnae.setDataregistro(new Date());
                    cnae.setHorarioFuncionamento(pessoaCNAE.getHorarioFuncionamento());
                    cnae.setTipo(pessoaCNAE.getTipo());
                    selecionado.getEconomicoCNAE().add(cnae);
                }
            }
        }
        FacesUtil.atualizarComponente("Formulario:panelDadosPessoa");
    }

    private void adicionarSociedadeCadastroEconomico(Pessoa pessoa, Date dataInicio, Date dataFim, Double proporcao) {
        SociedadeCadastroEconomico novoSocio = new SociedadeCadastroEconomico();
        novoSocio.setSocio(pessoa);
        novoSocio.setDataInicio(dataInicio);
        novoSocio.setDataFim(dataFim);
        novoSocio.setProporcao(proporcao);
        novoSocio.setDataRegistro(new Date());
        novoSocio.setCadastroEconomico(selecionado);
        selecionado.getSociedadeCadastroEconomico().add(novoSocio);
    }

    private void adicionarInformacoesEnquadramentoFiscal() {
        if (enquadramentoFiscal == null) {
            enquadramentoFiscal = new EnquadramentoFiscal();
        }
        enquadramentoFiscal.setPorte(TipoPorte.OUTRO);
        enquadramentoFiscal.setTipoContribuinte(TipoContribuinte.PERMANENTE);
        enquadramentoFiscal.setRegimeTributario(RegimeTributario.LUCRO_PRESUMIDO);
        enquadramentoFiscal.setRegimeEspecialTributacao(RegimeEspecialTributacao.PADRAO);
        enquadramentoFiscal.setTipoIssqn(TipoIssqn.FIXO);
        enquadramentoFiscal.setSubstitutoTributario(false);
        enquadramentoFiscal.setTipoNotaFiscalServico(null);
        adicionarEnquadramento();
    }

    public void selecionar(CadastroEconomico cadastroEconomico) {
        selecionado = cadastroEconomico;
        if (selecionado != null && selecionado.getId() != null) {
            selecionado = cadastroEconomicoFacade.recuperar(selecionado.getId());
            Web.navegacao(getCaminhoPadrao() + "listar/", getCaminhoPadrao() + "editar/" + selecionado.getInscricaoCadastral() + "/", selecionado);
        }
    }

    public boolean isRetroativo() {
        return idRevisao != null;
    }

    public void salvar() {
        FacesUtil.executaJavaScript("aguarde.show()");
        try {
            removerEconomicoCnaeDeCnaeInativo();
            validarImovel();
            validarCampos();
            if (selecionado.getId() == null) {
                cadastroEconomicoFacade.gerarInscricaoCadastral(selecionado);
                validarCodigo();
                selecionado = cadastroEconomicoFacade.salvar(selecionado, fileUploadEvents);
                cadastroEconomicoFacade.criarUsuarioWeb(selecionado);
                if (cadastroDerivadoRBTrans()) {
                    salvarDerivadoRBTrans();
                }
                Web.limpaNavegacao();
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "O Cadastro Econômico foi salvo com sucesso.");
                recarregarPagina();
            } else {
                salvarHistorico();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void removerEconomicoCnaeDeCnaeInativo() {
        List<EconomicoCNAE> aRemover = Lists.newArrayList();
        for (EconomicoCNAE ec : selecionado.getEconomicoCNAE()) {
            if (CNAE.Situacao.INATIVO.equals(ec.getCnae().getSituacao())) {
                aRemover.add(ec);
            }
        }
        for (EconomicoCNAE economicoCNAE : aRemover) {
            selecionado.getEconomicoCNAE().remove(economicoCNAE);
        }
    }

    private void salvarDerivadoRBTrans() {
        try {
            CalculoRBTrans calculoRBTrans = getFacade().getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaCadastroAutonomo(selecionado, permissaoTransporte.getTipoPermissaoRBTrans());
            gerarDebitosEDAM(calculoRBTrans);
            if (calculoRBTrans.getTipoCalculoRBTRans().getGeraIss()) {
                ProcessoCalculoISS processoCalculo = getFacade().getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarIss(selecionado);
                getFacade().getPermissaoTransporteFacade().getCalculoRBTransFacade().getGeraDebitoRbtrans().geraDebito(processoCalculo);
                getFacade()
                    .getPermissaoTransporteFacade()
                    .getCalculoRBTransFacade()
                    .getGeraDebito()
                    .getDamFacade()
                    .geraDAM(processoCalculo.getCalculos().get(0));
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarDebitosEDAM(CalculoRBTrans calculo) {
        getFacade().getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDebito(calculo);
        getFacade().getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDAM(calculo);
    }

    @Override
    public void excluir() {
        try {
            validarExclusaoEntidade();
            getFacede().remover(selecionado);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            try {
                validarForeinKeysComRegistro(selecionado);
                descobrirETratarException(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    public List<EscritorioContabil> completarEscritoriosContabeis(String parte) {
        return cadastroEconomicoFacade.getEscritorioContabilFacade().listaFiltrando(parte.trim(), "responsavel.nome");
    }

    public List<SelectItem> getTipoIss() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (RegimeTributario.SIMPLES_NACIONAL.equals(enquadramentoFiscal.getRegimeTributario())) {
            toReturn.add(new SelectItem(TipoIssqn.ESTIMADO, TipoIssqn.ESTIMADO.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.FIXO, TipoIssqn.FIXO.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.MEI, TipoIssqn.MEI.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.NAO_INCIDENCIA, TipoIssqn.NAO_INCIDENCIA.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.SIMPLES_NACIONAL, TipoIssqn.SIMPLES_NACIONAL.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.SUBLIMITE_ULTRAPASSADO, TipoIssqn.SUBLIMITE_ULTRAPASSADO.getDescricao()));
        } else if (RegimeTributario.LUCRO_PRESUMIDO.equals(enquadramentoFiscal.getRegimeTributario()) || RegimeEspecialTributacao.INSTITUICAO_FINANCEIRA.equals(enquadramentoFiscal.getRegimeEspecialTributacao())) {
            toReturn.add(new SelectItem(TipoIssqn.ESTIMADO, TipoIssqn.ESTIMADO.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.FIXO, TipoIssqn.FIXO.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.IMUNE, TipoIssqn.IMUNE.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.ISENTO, TipoIssqn.ISENTO.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.MENSAL, TipoIssqn.MENSAL.getDescricao()));
            toReturn.add(new SelectItem(TipoIssqn.NAO_INCIDENCIA, TipoIssqn.NAO_INCIDENCIA.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoNotaFiscal() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoNotaFiscalServico object : TipoNotaFiscalServico.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPeriodosIss() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPeriodoValorEstimado object : TipoPeriodoValorEstimado.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoContribuinte() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoContribuinte contribuinte : TipoContribuinte.values()) {
            retorno.add(new SelectItem(contribuinte, contribuinte.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getRegimesTributario() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (RegimeTributario regime : RegimeTributario.values()) {
            retorno.add(new SelectItem(regime, regime.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getRegimesEspeciaisTributacao() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (RegimeEspecialTributacao regime : RegimeEspecialTributacao.values()) {
            retorno.add(new SelectItem(regime, regime.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSequenciaDias() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (int contador = 1; contador <= 31; contador++) {
            retorno.add(new SelectItem(contador));
        }
        return retorno;
    }

    public List<SelectItem> getTipoPorte() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoPorte porte : TipoPorte.getTiposVisiveis()) {
            retorno.add(new SelectItem(porte, porte.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoPagamento() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoPagamento pagamento : TipoPagamento.values()) {
            retorno.add(new SelectItem(pagamento, pagamento.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoFisica() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFisicaTributario object : TipoFisicaTributario.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoJuridica() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoJuridicaTributario object : TipoJuridicaTributario.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getClassificacaoAtividade() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ClassificacaoAtividade object : ClassificacaoAtividade.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDocumentosFiscais() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDocumentoFiscal object : cadastroEconomicoFacade.getTipoDocumentoFiscalFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoCadastral() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoCadastralCadastroEconomico object : SituacaoCadastralCadastroEconomico.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposAutonomos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAutonomo object : cadastroEconomicoFacade.getTipoAutonomoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoCnae() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (EconomicoCNAE.TipoCnae object : EconomicoCNAE.TipoCnae.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterEscritorioContabil() {
        if (converterEscritorioContabil == null) {
            converterEscritorioContabil = new ConverterAutoComplete(EscritorioContabil.class, cadastroEconomicoFacade.getEscritorioContabilFacade());
        }
        return converterEscritorioContabil;
    }

    public Converter getConverterCNAE() {
        if (converterCnae == null) {
            converterCnae = new ConverterAutoComplete(CNAE.class, cadastroEconomicoFacade.getCnaeFacade());
        }
        return converterCnae;
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, cadastroEconomicoFacade.getTributoFacade());
        }
        return converterTributo;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, cadastroEconomicoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return cadastroEconomicoFacade.getPessoaFacade().listaTodasPessoas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<Tributo> completaTributo(String parte) {
        return cadastroEconomicoFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return cadastroEconomicoFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
    }

    public void adicionarCnae() {
        try {
            validarInformacoesDoCnae(cnae);
            cnae.setDataregistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            cnae.setCadastroEconomico(selecionado);
            selecionado.setEconomicoCNAE(Util.adicionarObjetoEmLista(selecionado.getEconomicoCNAE(), cnae));
            cnae = new EconomicoCNAE();
            alterandoCNAE = false;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarSituacaoCadastroEconomico() {
        try {
            validarSituacaoCadastroEconomico(situacaoCadastroEconomico);
            situacaoCadastroEconomico.setDataRegistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            selecionado.getSituacaoCadastroEconomico().add(situacaoCadastroEconomico);
            situacaoCadastroEconomico = new SituacaoCadastroEconomico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerSituacaoCadastroEconomico(SituacaoCadastroEconomico situacao) {
        if (situacao != null) {
            selecionado.getSituacaoCadastroEconomico().remove(situacao);
        }
    }

    public void removerCnae(EconomicoCNAE economicoCNAE) {
        if (economicoCNAE != null) {
            selecionado.getEconomicoCNAE().remove(economicoCNAE);
        }
    }

    public void cancelarCnae() {
        alterandoCNAE = false;
        setCnae(new EconomicoCNAE());
    }

    public void alterarCnae(EconomicoCNAE economicoCNAE) {
        if (economicoCNAE.getHorarioFuncionamento() != null) {
            economicoCNAE.setHorarioFuncionamento(cadastroEconomicoFacade.getHorarioFuncionamentoFacade().recuperar(economicoCNAE.getHorarioFuncionamento().getId()));
        }
        alterandoCNAE = true;
        cnae = (EconomicoCNAE) Util.clonarObjeto(economicoCNAE);
    }

    public List<EconomicoCNAE> getCnaes() {
        return selecionado.getEconomicoCNAE();
    }

    public List<EconomicoCNAE> getCnaesAtivos() {
        return selecionado.getEconomicoCNAEAtivos();
    }

    public List<EconomicoCNAE> getCnaesVigentes() {
        return selecionado.getEconomicoCNAEVigentes();
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, cadastroEconomicoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<CNAE> completaCNAE(String parte) {
        return cadastroEconomicoFacade.getCnaeFacade().listaCnaesAtivos(parte.trim());
    }

    public String getCrc() {
        if (selecionado.getEscritorioContabil() != null) {
            return selecionado.getEscritorioContabil().getCrc();
        } else {
            return "";
        }
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroEconomicoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "codigo");
    }

    public List<CadastroEconomico> completarCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte);
    }

    public List<CadastroEconomico> completarInstituicoesFinanceiras(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(RegimeEspecialTributacao.INSTITUICAO_FINANCEIRA,
            parte);
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public List<HistoricoAlteracaoRedeSim> getListaHistoricosRedeSimPessoa() {
        return listaHistoricosRedeSimPessoa;
    }

    public void setListaHistoricosRedeSimPessoa(List<HistoricoAlteracaoRedeSim> listaHistoricosRedeSimPessoa) {
        this.listaHistoricosRedeSimPessoa = listaHistoricosRedeSimPessoa;
    }

    public void setaLote(SelectEvent e) {
        CadastroImobiliario cad;
        cad = (CadastroImobiliario) e.getObject();
        lote = cad.getLote();
    }

    public boolean isFisica() {
        if (selecionado != null && selecionado.getPessoa() != null) {
            return Util.checkInstaceof(selecionado.getPessoa(), PessoaFisica.class);
        }
        return false;
    }

    public boolean isJuridica() {
        if (selecionado != null && selecionado.getPessoa() != null) {
            return Util.checkInstaceof(selecionado.getPessoa(), PessoaJuridica.class);
        } else {
            return false;
        }
    }

    public List<Servico> completaServico(String parte) {
        return cadastroEconomicoFacade.getServicoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Servico> completaServicoAutoComplete(String parte) {
        return cadastroEconomicoFacade.getServicoFacade().completaServico(parte.trim());
    }

    public void adicionarSocios() {
        try {
            validarSocios(socio);
            socio.setDataRegistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            socio.setCadastroEconomico(selecionado);
            selecionado.getSociedadeCadastroEconomico().add(socio);
            socio = new SociedadeCadastroEconomico();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarSocio(SociedadeCadastroEconomico s) {
        socio = s;
    }

    public void confirmarEdicaoSocio() {
        selecionado.getSociedadeCadastroEconomico().remove(socio);
        selecionado.getSociedadeCadastroEconomico().add(socio);
        socio = new SociedadeCadastroEconomico();
    }

    public void cancelarEdicaoSocio() {
        socio = new SociedadeCadastroEconomico();
    }

    public void adicionarServico(Servico srv) {
        if (srv != null && srv.getId() != null) {
            selecionado.getServico().add(srv);
            listaServico.remove(srv);
        }
    }

    public void removerServico(Servico serv) {
        if (serv != null) {
            selecionado.getServico().remove(serv);
        }
    }

    public void removerSocios(SociedadeCadastroEconomico sociedade) {
        if (sociedade != null) {
            selecionado.getSociedadeCadastroEconomico().remove(sociedade);
        }
    }

    public void adicionarTipoProcesso() {
        if (validaSituacaoProcesso(tipoProcessoEconomico)) {
            tipoProcessoEconomico.setCadastroEconomico(selecionado);
            selecionado.getTipoProcessoEconomico().add(tipoProcessoEconomico);
            tipoProcessoEconomico = new TipoProcessoEconomico();
        }
    }

    public void removerTipoProcesso(TipoProcessoEconomico tipoProcesso) {
        if (tipoProcesso != null) {
            selecionado.getTipoProcessoEconomico().remove(tipoProcesso);
        }
    }

    public void addJuntas() {
        try {
            validarSituacaoJuntaComercial(juntas);
            juntas.setDataRegistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            selecionado.getRegistroJuntas().add(juntas);
            juntas = new RegistroJuntas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerJuntas(RegistroJuntas registroJunta) {
        if (registroJunta != null) {
            selecionado.getRegistroJuntas().remove(registroJunta);
        }
    }


    public void adicionarEnquadramento() {
        try {
            validarEnquadramentoFiscal(enquadramentoFiscal);
            Date dataAtual = new Date();
            if (selecionado.getEnquadramentoVigente() != null) {
                selecionado.getEnquadramentoVigente().setFimVigencia(dataAtual);
            }
            enquadramentoFiscal.setCadastroEconomico(selecionado);
            enquadramentoFiscal.setInicioVigencia(dataAtual);
            selecionado.getEnquadramentos().add(enquadramentoFiscal);
            enquadramentoFiscal = new EnquadramentoFiscal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerEnquadramento(EnquadramentoFiscal enquadramento) {
        if (!podeEditarOuExcluirEnquadramento()) return;
        if (enquadramento != null) {
            selecionado.getEnquadramentos().remove(enquadramento);
        }
    }

    public void editarEnquadramento(EnquadramentoFiscal enquadramento) {
        if (!podeEditarOuExcluirEnquadramento()) return;
        if (enquadramento != null) {
            this.enquadramentoFiscal = new EnquadramentoFiscal();
            this.enquadramentoFiscal.setPorte(enquadramento.getPorte());
            this.enquadramentoFiscal.setTipoContribuinte(enquadramento.getTipoContribuinte());
            this.enquadramentoFiscal.setRegimeTributario(enquadramento.getRegimeTributario());
            this.enquadramentoFiscal.setRegimeEspecialTributacao(enquadramento.getRegimeEspecialTributacao());
            this.enquadramentoFiscal.setBanco(enquadramento.getBanco());
            this.enquadramentoFiscal.setVersaoDesif(enquadramento.getVersaoDesif());
            this.enquadramentoFiscal.setTipoIssqn(enquadramento.getTipoIssqn());
            this.enquadramentoFiscal.setTipoPeriodoValorEstimado(enquadramento.getTipoPeriodoValorEstimado());
            this.enquadramentoFiscal.setIssEstimado(enquadramento.getIssEstimado());
            this.enquadramentoFiscal.setSubstitutoTributario(enquadramento.getSubstitutoTributario());
            this.enquadramentoFiscal.setTipoNotaFiscalServico(enquadramento.getTipoNotaFiscalServico());
            this.enquadramentoFiscal.setEscritorioContabil(enquadramento.getEscritorioContabil());
            this.editandoEnquadramento = true;
        }
    }

    public void salvarEdicaoEnquadramento() {
        adicionarEnquadramento();
    }

    public void cancelarEdicaoEnquadramento() {
        this.enquadramentoFiscal = new EnquadramentoFiscal();
        this.editandoEnquadramento = false;
    }

    public void setaSocio(ActionEvent a) {
        socio = new SociedadeCadastroEconomico();
        socio = (SociedadeCadastroEconomico) a.getComponent().getAttributes().get("objeto");
    }

    public void alteraSocio() {
        selecionado.getSociedadeCadastroEconomico().set(selecionado.getSociedadeCadastroEconomico().indexOf(socio), socio);
        socio = new SociedadeCadastroEconomico();
    }

    public BigDecimal getTotalSocios() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (SociedadeCadastroEconomico soc : selecionado.getSociedadeCadastroEconomico()) {
            if ((soc.getDataFim() == null) || (soc.getDataFim().after(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao()))) {
                if (soc.getProporcao() != null) {
                    total = total.add(BigDecimal.valueOf(soc.getProporcao()));
                }
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void uploadArquivos(FileUploadEvent event) {
        fileUploadEvents.add(event);
    }

    public void uploadArquivosSelecionados(FileUploadEvent event) {
        fileUploadEvents.add(event);
    }

    public void removerArquivo(ActionEvent evento) {
        FileUploadEvent e = (FileUploadEvent) evento.getComponent().getAttributes().get("arquivos");
        fileUploadEvents.remove(e);
    }

    public void removeFileUpload(ActionEvent event) {
        Arquivo arq = (Arquivo) event.getComponent().getAttributes().get("remove");
        selecionado.getArquivos().remove(arq);
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public void setFileUploadEvents(List<FileUploadEvent> fileUploadEvents) {
        this.fileUploadEvents = fileUploadEvents;
    }

    public void validarCodigo() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getInscricaoCadastral() != null && selecionado.getInscricaoCadastralSemDigito().trim().length() < 6) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o código do C.M.C. com 6 (seis) dígitos.");
        }
        if (selecionado.getInscricaoCadastral() != null && validaSeExisteCodigo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe outro Cadastro Econômico com o código informado. Informe outro código para efetuar o cadastro.");
        }
        if (selecionado.getInscricaoCadastral() != null && cadastroEconomicoFacade.jaExisteEsteCMC(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("C.M.C. já está cadastrado!");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma pessoa para o Cadastro Econômico");
        } else {
            List<FacesMessage> mensagensValidacao = validarSePessoaPodePossuirMaisDeUmCMC();
            if (!mensagensValidacao.isEmpty()) {
                ve.getMensagens().addAll(mensagensValidacao);
            }
        }
        if (alterandoCNAE) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Há um CNAE sendo alterado, cancele ou grave para continuar");
        }
        if (selecionado.getEnquadramentoVigente() == null && !cadastroDerivadoRBTrans()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um enquadramento fiscal vigente!");
        }
        if ((selecionado.getAreaUtilizacao() <= 0) && getCadastroDoAlvara() && !cadastroDerivadoRBTrans()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Área de Utilização!");
        }
        if (selecionado.getAbertura() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Abertura!");
        }
        if (selecionado.getSituacaoCadastroEconomico().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma situação cadastral!");
        }
        if (selecionado.getEconomicoCNAE().isEmpty() && !cadastroDerivadoRBTrans() && (selecionado.getNaturezaJuridica() != null && selecionado.getNaturezaJuridica().getNecessitaCNAE())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um CNAE!");
        } else {
            for (EconomicoCNAE ec : selecionado.getEconomicoCNAE()) {
                if (CNAE.Situacao.INATIVO.equals(ec.getCnae().getSituacao())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Não é possível salvar um C.M.C. que possua um CNAE Inativo!");
                }
            }
        }
        if (selecionado.getCadastroImobiliario() != null) {
            if (selecionado.getTipoImovelSituacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Situação do Imóvel");
            }
        }
        if (selecionado.getSociedadeCadastroEconomico().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Sócio do Cadastro Econômico!");
        }
        if (hasNaturezaJuridicaNula()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Natureza Jurídica!");
        }
        if (!hasNaturezaJuridicaNula() && hasTipoNaturezaJuridicaInconsistente()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de pessoa do contribuinte informado é incompatível com a natureza jurídica. Por favor, selecione uma natureza jurídica compatível com o contribuinte!");
        }
        if (!hasNaturezaJuridicaNula() && (selecionado.getNaturezaJuridica().getAutonomo() && (selecionado.getTipoAutonomo() == null || selecionado.getTipoAutonomo().getId() == null))) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Autônomo!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean hasCaracteristicaSemPublicidade() {
        for (BCECaracFuncionamento carac : selecionado.getListaBCECaracFuncionamento()) {
            if (carac.getCaracFuncionamento().getSemPublicidade()) {
                return true;
            }
        }
        return false;
    }

    public List<SituacaoCadastroEconomico> getUltimaSiuacao() {
        List<SituacaoCadastroEconomico> toReturn = new ArrayList<SituacaoCadastroEconomico>();
        if (!selecionado.getSituacaoCadastroEconomico().isEmpty()) {
            Collections.sort(selecionado.getSituacaoCadastroEconomico());
            toReturn.add(selecionado.getSituacaoCadastroEconomico().get(selecionado.getSituacaoCadastroEconomico().size() - 1));
        }
        return toReturn;
    }

    private void validarSituacaoCadastroEconomico(SituacaoCadastroEconomico situacaoCadastroEconomico) {
        ValidacaoException ve = new ValidacaoException();
        if (situacaoCadastroEconomico.getSituacaoCadastral() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Situação cadastral!");
        }
        if (situacaoCadastroEconomico.getDataAlteracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data da Situação cadastral!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarSituacaoJuntaComercial(RegistroJuntas junta) {
        ValidacaoException ve = new ValidacaoException();
        if (junta.getDescricao() == null || junta.getDescricao().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição!");
        }
        if (junta.getNumRegistro() == null || junta.getNumRegistro().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Número do Registro!");
        }
        if (junta.getDataUltima() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarEnquadramentoFiscal(EnquadramentoFiscal enquadramento) {
        ValidacaoException ve = new ValidacaoException();
        if (enquadramento.getTipoContribuinte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Tipo de Contribuinte' deve ser informado!");
        }
        if (enquadramento.getRegimeTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Regime Tributário' deve ser informado!");
        }
        if (enquadramento.getRegimeEspecialTributacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Regime Especial Tributação' deve ser informado!");
        } else {
            if (RegimeEspecialTributacao.INSTITUICAO_FINANCEIRA.equals(enquadramento.getRegimeEspecialTributacao())) {
                if (enquadramento.getBanco() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo 'Banco' deve ser informado!");
                }
                if (enquadramento.getVersaoDesif() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo 'Versão DES-IF' deve ser informado!");
                }
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private boolean validaSituacaoProcesso(TipoProcessoEconomico tipoProcesso) {
        boolean validacao = true;
        if (tipoProcesso.getNumeroProcesso() == null || tipoProcesso.getNumeroProcesso().isEmpty()) {
            validacao = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Número do Processo");
        }
        if (tipoProcesso.getDataProcesso() == null) {
            validacao = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Data do Processo");
        }
        return validacao;
    }

    private void validarInformacoesDoCnae(EconomicoCNAE cnae) {
        ValidacaoException ve = new ValidacaoException();
        if (cnae.getCnae() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'CNAE' deve ser informado");
        }
        if (cnae.getInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data Inicial' deve ser informado");
        }
        if (cnae.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Tipo' deve ser informado");
        } else {
            if (EconomicoCNAE.TipoCnae.Primaria.equals(cnae.getTipo())) {
                for (EconomicoCNAE ec : getCnaesVigentes()) {
                    if (EconomicoCNAE.TipoCnae.Primaria.equals(ec.getTipo()) && !ec.equals(cnae)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um CNAE primário.");
                        break;
                    }
                }
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarSocios(SociedadeCadastroEconomico socio) {
        ValidacaoException ve = new ValidacaoException();
        if (socio.getSocio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um sócio!");
        }
        if (socio.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de início!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<VistoriaFiscalizacao> getVistorias() {
        return cadastroEconomicoFacade.getVistoriaFiscalizacaoFacade().listaPorCadastroEconomico(selecionado);
    }

    public List<CadastroAidf> getAIDFS() {
        return cadastroEconomicoFacade.getCadastroAidfFacade().listaPorCadastroEconomico(selecionado);
    }

    public boolean isVerificarSituacao() {
        SituacaoCadastroEconomico sit = cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico(selecionado);
        SituacaoCadastralCadastroEconomico situacao = null;
        if (sit != null && sit.getSituacaoCadastral() != null) {
            situacao = sit.getSituacaoCadastral();
        }
        return SituacaoCadastralCadastroEconomico.INATIVO.equals(situacao) || SituacaoCadastralCadastroEconomico.BAIXADA.equals(situacao) || SituacaoCadastralCadastroEconomico.SUSPENSA.equals(situacao);
    }

    public boolean getTipoEstimado() {
        return enquadramentoFiscal != null && enquadramentoFiscal.getTipoIssqn() != null && TipoIssqn.ESTIMADO.equals(enquadramentoFiscal.getTipoIssqn());
    }

    public Converter getConverterTipoDocumentoFiscal() {
        if (converterTipoDocumentoFiscal == null) {
            converterTipoDocumentoFiscal = new ConverterGenerico(TipoDocumentoFiscal.class, cadastroEconomicoFacade.getTipoDocumentoFiscalFacade());
        }
        return converterTipoDocumentoFiscal;
    }

    public List<Servico> getListaServicos() {
        return listaServico;
    }

    public List<Historico> getListaHistoricos() {
        if (historicos == null || historicos.isEmpty()) {
            if (dataRetroativa != null) {
                historicos = cadastroEconomicoFacade.getHistoricoFacade().recuperaPorCadastro(selecionado, dataRetroativa);
            } else {
                historicos = cadastroEconomicoFacade.getHistoricoFacade().recuperaPorCadastro(selecionado, new Date());
            }
        }
        return historicos;
    }

    public void salvarHistorico() {
        try {
            preencherHistorico();
            if (isAguardandoValidacao()) {
                SituacaoCadastroEconomico situacao = new SituacaoCadastroEconomico();
                situacao.setDataRegistro(new Date());
                situacao.setDataAlteracao(new Date());
                situacao.setSituacaoCadastral(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
                selecionado.getSituacaoCadastroEconomico().add(situacao);
                NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(selecionado.getId());
            }
            selecionado = cadastroEconomicoFacade.salvar(selecionado, fileUploadEvents);
            if (alteracaoCmcFacade.hasAlteracaoCadastro(selecionado.getId())) {
                cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(selecionado, isMei());
            }
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar o cadastro economico: ", ex);
            descobrirETratarException(ex);
        }
    }

    private void preencherHistorico() {
        historico.setDataRegistro(new Date());
        historico.setDataSolicitacao(historico.getDataRegistro());
        historico.setDigitador(getSistemaControlador().getUsuarioCorrente());
        historico.setSolicitante(getSistemaControlador().getUsuarioCorrente().getNome());
        historico.setMotivo("");
        historico.setCadastro(selecionado);
        selecionado.getHistoricos().add(historico);
    }

    public List<AtoLegal> completaOficios(String parte) {
        return cadastroEconomicoFacade.getAtoLegalFacade().listaOficiosBCENaoUsados(selecionado, parte);
    }

    public boolean isCadastroDeOficio() {
        return selecionado != null && selecionado.getCadastroPorOficio() != null && selecionado.getCadastroPorOficio();
    }

    private boolean hasNaturezaJuridicaNula() {
        boolean toReturn = false;
        if (selecionado.getNaturezaJuridica() == null || selecionado.getNaturezaJuridica().getId() == null) {
            toReturn = true;
        }

        return toReturn;
    }

    private boolean hasTipoNaturezaJuridicaInconsistente() {
        boolean toReturn = false;
        if (hasNaturezaJuridicaNula()) {
            toReturn = true;
        }
        if ((isFisica() && TipoNaturezaJuridica.JURIDICA.equals(selecionado.getNaturezaJuridica().getTipoNaturezaJuridica())) ||
            isJuridica() && TipoNaturezaJuridica.FISICA.equals(selecionado.getNaturezaJuridica().getTipoNaturezaJuridica())) {
            toReturn = true;
        }
        return toReturn;
    }

    private boolean validaSeExisteCodigo() {
        if (selecionado.getInscricaoCadastral().trim().length() == 7) {
            return cadastroEconomicoFacade.validaCodigoExiste(selecionado);
        }
        return false;
    }

    private List<FacesMessage> validarSePessoaPodePossuirMaisDeUmCMC() {
        ValidacaoException ve = new ValidacaoException();
        List<CadastroEconomico> cadastroEconomicosDaPessoa = cadastroEconomicoFacade.recuperaCadastrosAtivosDaPessoa(selecionado.getPessoa());
        if (selecionado.getId() == null) {
            cadastroEconomicosDaPessoa.add(selecionado);
        }
        if (cadastroEconomicosDaPessoa.size() > 1) {
            int quantidade = cadastroEconomicosDaPessoa.size();
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                if (selecionado.getTipoAutonomo() != null && quantidade > selecionado.getTipoAutonomo().getQuantidadeCadastroRBTrans()) {
                    if (cadastroDerivadoRBTrans()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe " + selecionado.getTipoAutonomo().getQuantidadeCadastroRBTrans() + " C.M.C.(s) cadastrados para a pessoa informada.");
                    } else {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um C.M.C. cadastrado para a pessoa informada.");
                    }
                }
            } else {
                boolean permite = false;
                for (EconomicoCNAE economicoCNAEAtivo : selecionado.getEconomicoCNAEAtivos()) {
                    if (EconomicoCNAE.TipoCnae.Primaria.equals(economicoCNAEAtivo.getTipo()) &&
                        economicoCNAEAtivo.getCnae() != null && economicoCNAEAtivo.getCnae().getMultiplosCMC()) {
                        permite = true;
                        break;
                    }
                }
                if (!permite) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um C.M.C. cadastrado para a pessoa informada.");
                }
            }
        }
        return ve.getMensagens();
    }

    public String getCodigoVerificador() {
        return codigoVerificador;
    }

    public void setCodigoVerificador(String codigoVerificador) {
        this.codigoVerificador = codigoVerificador;
    }

    public void adicionarPlaca() {
        try {
            validarPlaca();
            selecionado.addPlaca(placa);
            placa = new PlacaAutomovelCmc();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerPlaca(PlacaAutomovelCmc pl) {
        if (pl != null) {
            selecionado.getPlacas().remove(pl);
        }
    }

    public void validarPlaca() {
        ValidacaoException ve = new ValidacaoException();
        for (PlacaAutomovelCmc pl : selecionado.getPlacas()) {
            if (placa.getPlaca().equals(pl.getPlaca())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Essa placa já foi informada!");
            }
        }
        if (placa.getPlaca().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a placa!");
        }
        if (selecionado.getTipoAutonomo() != null && selecionado.getTipoAutonomo().getNecessitaPlacas() && selecionado.getTipoAutonomo().getQtdPlacas() != null) {
            if (selecionado.getPlacas().size() >= selecionado.getTipoAutonomo().getQtdPlacas()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de placas atingiu o limite!");
            }
        }
        if ((placa != null && placa.getPlaca() != null) && placa.getPlaca().length() < 7) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A placa informada é inválida.");
        }
        ve.lancarException();
    }

    public List<HorarioFuncionamento> getHorarios() {
        return cadastroEconomicoFacade.getHorarioFuncionamentoFacade().listaComItens();
    }

    public List<HorarioFuncionamentoItem> itensHorarios(HorarioFuncionamento horario) {
        return cadastroEconomicoFacade.getHorarioFuncionamentoFacade().recuperar(horario.getId()).getItens();
    }

    public void selecionarHorario(HorarioFuncionamento horario) {
        try {
            validarHorarioDeFuncionamento(horario);
            PessoaHorarioFuncionamento pessoaHorarioFuncionamento = new PessoaHorarioFuncionamento();
            pessoaHorarioFuncionamento.setPessoa(selecionado.getPessoa());
            pessoaHorarioFuncionamento.setHorarioFuncionamento(horario);
            pessoaHorarioFuncionamento.setAtivo(true);
            Util.adicionarObjetoEmLista(selecionado.getPessoa().getHorariosFuncionamento(), pessoaHorarioFuncionamento);
            FacesUtil.executaJavaScript("dialogoHorarioFuncionamento.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarHorarioDeFuncionamento(HorarioFuncionamento horario) {
        ValidacaoException ve = new ValidacaoException();
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : selecionado.getPessoa().getHorariosFuncionamento()) {
            if (pessoaHorarioFuncionamento.getHorarioFuncionamento().equals(horario)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O horário selecionado já está adicionado.");
            }
        }
        ve.lancarException();
    }

    public void removerHorarioFuncionamento(PessoaHorarioFuncionamento pessoaHorarioFuncionamento) {
        selecionado.getPessoa().getHorariosFuncionamento().remove(pessoaHorarioFuncionamento);
    }

    public boolean cadastroDerivadoRBTrans() {
        return this.permissaoTransporte != null;
    }

    public boolean getApresentaDadosParaRBTrans() {
        return cadastroDerivadoRBTrans();
    }

    public void carregarFoto() {
        Arquivo arq = selecionado.getPessoa() != null ? selecionado.getPessoa().getArquivo() : null;
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : cadastroEconomicoFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }
                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType());
            } catch (Exception ex) {
                logger.error("{}", ex);
                imagemFoto = null;
            }
        } else {
            imagemFoto = null;
        }
    }

    public StreamedContent getImagemFoto() {
        carregarFoto();
        return imagemFoto;
    }

    public void setImagemFoto(StreamedContent imagemFoto) {
        this.imagemFoto = imagemFoto;
    }

    public List<RegistroDB> getHistoricoLegado() {
        try {
            if (historicoLegado == null && selecionado != null) {
                historicoLegado = new ArrayList<>();
                historicoLegado = cadastroEconomicoFacade.getHistoricoLegadoFacade().listaHistoricoCMC(selecionado.getInscricaoCadastral());
                if (historicoLegado == null || historicoLegado.isEmpty()) {
                    return Lists.newArrayList();
                }
            }
            return historicoLegado;
        } catch (Exception e) {
            logger.debug("Não foi possível recuperar o histórico legado");
            return Lists.newArrayList();
        }
    }

    public void mostrarHistoricoLegado() {
        mostraHistoricoLegado = true;
    }

    public boolean isMostraHistoricoLegado() {
        return mostraHistoricoLegado;
    }

    public void setMostraHistoricoLegado(boolean mostraHistoricoLegado) {
        this.mostraHistoricoLegado = mostraHistoricoLegado;
    }

    public Testada getTestadaPrincipal() {
        if (selecionado.getCadastroImobiliario() != null) {
            return cadastroEconomicoFacade.getLoteFacade().recuperarTestadaPrincipal(selecionado.getCadastroImobiliario().getLote());
        }
        return new Testada();
    }

    public Converter getConverterCaracteristica() {
        if (converterCaracteristica == null) {
            converterCaracteristica = new ConverterGenerico(CaracFuncionamento.class, cadastroEconomicoFacade.getCaracteristicasFuncionamentoFacede());
        }
        return converterCaracteristica;
    }

    public BCECaracFuncionamento getBcecf() {
        return bcecf;
    }

    public void setBcecf(BCECaracFuncionamento bcecf) {
        this.bcecf = bcecf;
    }

    public List<SelectItem> getCaracteristicas() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (CaracFuncionamento cf : cadastroEconomicoFacade.getCaracteristicasFuncionamentoFacede().listaOrdenadoPorCodigo()) {
            retorno.add(new SelectItem(cf, cf.getCodigo() + " - " + cf.getDescricaoCurta() + " - " + cf.getDescricao()));
        }
        return retorno;
    }

    public void adiconaCarcteristica() {
        if (validaCamposCaracFuncionamento()) {
            if (bcecf.getCaracFuncionamento().getSemPublicidade()) {
                selecionado.setAreaPublicidade(null);
            }
            bcecf.setCadastroEconomico(selecionado);
            selecionado.getListaBCECaracFuncionamento().add(bcecf);
            bcecf = new BCECaracFuncionamento();
            bcecf.setQuantidade(1l);
        }
    }

    public boolean validaCamposCaracFuncionamento() {
        boolean retorno = true;
        if (bcecf.getCaracFuncionamento() == null || bcecf.getCaracFuncionamento().getId() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Característica de Funcionamento");
            retorno = false;
        }
        if (bcecf.getQuantidade() == null && (bcecf.getCaracFuncionamento() != null && !bcecf.getCaracFuncionamento().getSemPublicidade())) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe uma quantidade.");
            retorno = false;
        }
        if (bcecf.getQuantidade() != null && (bcecf.getCaracFuncionamento() != null && !bcecf.getCaracFuncionamento().getSemPublicidade())) {
            if (bcecf.getQuantidade() <= 0) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe uma quantidade válida maior que zero.");
                retorno = false;
            }
        }
        if (validaAdicionarCaracFuncIgual(bcecf)) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Característica de Funcionamento já adicionada.");
            retorno = false;
        }
        return retorno;
    }

    private boolean validaAdicionarCaracFuncIgual(BCECaracFuncionamento bcecf) {
        boolean retorno = false;
        for (BCECaracFuncionamento bCECaracFuncionamento : this.selecionado.getListaBCECaracFuncionamento()) {
            if (bCECaracFuncionamento.getCaracFuncionamento().equals(bcecf.getCaracFuncionamento())) {
                return true;
            }
        }
        return retorno;
    }

    public void removeCaracteristica(BCECaracFuncionamento cf) {
        selecionado.getListaBCECaracFuncionamento().remove(cf);
    }

    public void limparCampos() {
        lista = new ArrayList<>();
        Util.limparCampos(this);
    }

    public void validarImovel() {
        ValidacaoException ve = new ValidacaoException();
        if (getTipoImovelSituacao().isEmpty() || getTipoImovelSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Situação do Imóvel!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<SelectItem> getTipoImovelSituacao() {
        List<SelectItem> listas = new ArrayList<SelectItem>();
        listas.add(new SelectItem(null, " "));
        for (TipoImovelSituacao situacao : TipoImovelSituacao.values()) {
            listas.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return listas;
    }

    @Override
    public AbstractFacade getFacede() {
        return cadastroEconomicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (!getCadastroDoAlvara()) {
            return "/tributario/cadastroeconomico/";
        }
        return "/tributario/cadastroeconomico-do-alvara/";
    }

    public String getRetornaCaminhoPadrao() {
        StringBuilder padrao = new StringBuilder();
        padrao.append(getCaminhoPadrao());
        if (Operacoes.NOVO.equals(operacao)) {
            padrao.append("novo/");
        }
        if (Operacoes.EDITAR.equals(operacao)) {
            padrao.append("editar/").append(getUrlKeyValue()).append("/");
        }
        if (Operacoes.VER.equals(operacao)) {
            padrao.append("ver/").append(getUrlKeyValue()).append("/");
        }
        return padrao.toString();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getInscricaoCadastral();
    }

    public void selecionar() {
        if (!isSessao()) {
            selecionarPadrao();
        } else {
            try {
                Web.pegaTodosFieldsNaSessao(this);
            } catch (IllegalAccessException e) {
                logger.debug("Não foi possível recuperar o CMC {} da sessão {} ", id, e.getMessage());
            }
            if (this.getSelecionado() == null) {
                selecionarPadrao();
            } else {
                definirPessoaCasoExistaNaSessao();
            }
        }
        carregaFoto();
        ordenarEnquadramentos();
        recuperarRequerimentoLicenciamentoETRList();
    }

    public void carregaFoto() {
        Arquivo arq = selecionado.getPessoa().getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : cadastroEconomicoFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream teste = new ByteArrayInputStream(buffer.toByteArray());
                imagemFoto = new DefaultStreamedContent(teste, arq.getMimeType(), arq.getNome());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    private void selecionarPadrao() {
        if (selecionado == null) {
            selecionado = cadastroEconomicoFacade.recuperar(id);
        }
        instanciaTudo();
        Collections.sort(selecionado.getEconomicoCNAE());
        cadastroEconomicoFacade.getAtributoFacade().completaAtributos(selecionado.getAtributos(), ClasseDoAtributo.CADASTRO_ECONOMICO);
        cnaesPessoa = selecionado.getPessoa().getPessoaCNAEAtivos();

        if (selecionado.getPessoa() instanceof PessoaJuridica) {
            PessoaJuridica pessoaJuridica = (PessoaJuridica) cadastroEconomicoFacade.getPessoaFacade().recuperarPJ(selecionado.getPessoa().getId());
            Collections.sort(pessoaJuridica.getPessoaCNAE());

            juntasComercialPessoaJuridica = pessoaJuridica.getJuntaComercial();
            representantesPessoaJuridica = pessoaJuridica.getRepresentantesLegal();
            filiaisPessoaJuridica = pessoaJuridica.getFiliais();
            listaHistoricosRedeSimPessoa = pessoaJuridica.getHistoricosIntegracaoRedeSim();
            Collections.sort(listaHistoricosRedeSimPessoa);
        }
    }

    public void exibirRevisaoAuditoria(HistoricoAlteracaoRedeSim historico) {
        Long idRevtype = cadastroEconomicoFacade.recuperarIdRevisaoCadastroEconomicoHistoricoRedeSim(selecionado, historico);
        if (idRevtype != null) {
            Web.getSessionMap().put("pagina-anterior-auditoria-listar", getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.redirecionamentoInterno("/auditoria/ver/" + idRevtype + "/" + selecionado.getId() + "/" + CadastroEconomico.class.getSimpleName());
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível localizar a informação da revisão desse histórico, tente um outro histórico ou entre em contato com o suporte!");
        }
    }


    public void novoCadastroEconomico() {
        try {
            super.novo();
            socio = new SociedadeCadastroEconomico();
            if (!isSessao()) {
                inicializaCadastro();
            } else {
                Web.pegaTodosFieldsNaSessao(this);
                if (this.getSelecionado() == null) {
                    selecionado = new CadastroEconomico();
                    inicializaCadastro();
                } else {
                    definirPessoaCasoExistaNaSessao();
                }
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void definirPessoaCasoExistaNaSessao() {
        Pessoa daSessao = (Pessoa) Web.pegaDaSessao(PessoaFisica.class);
        String tipoString = (String) Web.pegaDaSessao(KEY_SESSAO_TIPO_CADASTRO);
        TipoPessoaCadastro tipoPessoaCadastro = tipoString != null ? TipoPessoaCadastro.valueOf(tipoString) : TipoPessoaCadastro.PESSOA;

        if (daSessao == null) {
            daSessao = (Pessoa) Web.pegaDaSessao(PessoaJuridica.class);
        }
        if (daSessao != null) {
            if (socio == null) {
                socio = new SociedadeCadastroEconomico();
            }
            if (TipoPessoaCadastro.SOCIO.equals(tipoPessoaCadastro)) {
                socio.setSocio(daSessao);
            } else {
                selecionado.setPessoa(daSessao);
            }
        }
        atualizarDadosPessoa();
    }

    private void inicializaCadastro() {
        selecionado.setPessoa((PessoaFisica) Web.pegaDaSessao(PessoaFisica.class));
        selecionado.setPessoa((PessoaJuridica) Web.pegaDaSessao(PessoaJuridica.class));
        instanciaTudo();
        selecionado.setAtributos(cadastroEconomicoFacade.getAtributoFacade().novoMapaAtributoValorAtributo(ClasseDoAtributo.CADASTRO_ECONOMICO));
        selecionado.setAbertura(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioDoCadastro(getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataCadastro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
        if (selecionado.getSituacaoCadastroEconomico().isEmpty()) {
            situacaoCadastroEconomico.setSituacaoCadastral(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
            situacaoCadastroEconomico.setDataAlteracao(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            situacaoCadastroEconomico.setDataRegistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            selecionado.getSituacaoCadastroEconomico().add(situacaoCadastroEconomico);
        }
    }

    public void poeNaSessao(TipoPessoaCadastro tipoPessoaCadastro) {
        Web.poeNaSessao(KEY_SESSAO_TIPO_CADASTRO, tipoPessoaCadastro.name());
        Web.poeTodosFieldsNaSessao(this);
    }

    public void instanciaTudo() {
        alterandoCNAE = false;
        endereco = new EnderecoCadastroEconomico(TipoEndereco.CORRESPONDENCIA, selecionado);
        lote = new Lote();
        socio = new SociedadeCadastroEconomico();
        juntas = new RegistroJuntas();
        enquadramentoFiscal = new EnquadramentoFiscal();
        fileUploadEvents = new ArrayList<>();
        servico = new Servico();
        cnae = new EconomicoCNAE();
        tipoProcessoEconomico = new TipoProcessoEconomico();
        placa = new PlacaAutomovelCmc();
        this.imagemFoto = null;
        mostraHistoricoLegado = false;
        bcecf = new BCECaracFuncionamento();
        isencao = new IsencaoCadastroEconomico();
        situacaoCadastroEconomico = new SituacaoCadastroEconomico();
        editandoEnquadramento = false;
        enquadramentoAmbiental = new EnquadramentoAmbiental();
        iniciaHistorico();
    }

    private void iniciaHistorico() {
        historico = new Historico();
        historico.setDataRegistro(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
        historico.setDigitador(cadastroEconomicoFacade.getSistemaFacade().getUsuarioCorrente());
        historico.setSequencia(1L);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void addIsencao() {
        if (validaIsencao()) {
            IsencaoCadastroEconomico i = isencaoVigente();
            if (i != null) {
                if (i.getFinalVigencia() == null || i.getFinalVigencia().after(isencao.getInicioVigencia())) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(isencao.getInicioVigencia());
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    i.setFinalVigencia(c.getTime());
                }
            }
            isencao.setSequencia((long) selecionado.getIsencoes().size() + 1);
            isencao.setCadastroEconomico(selecionado);
            isencao.setUsuario(cadastroEconomicoFacade.getSistemaFacade().getUsuarioCorrente());
            isencao.setDataHoraInclusao(cadastroEconomicoFacade.getSistemaFacade().getDataOperacao());
            selecionado.getIsencoes().add(isencao);
            isencao = new IsencaoCadastroEconomico();
        }
    }

    public void removeIsencao(IsencaoCadastroEconomico isencao) {
        if (selecionado.getIsencoes().contains(isencao)) {
            selecionado.getIsencoes().remove(isencao);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "A isenção de sequencia " + isencao.getSequencia() + " deste Cadastro Econômico foi removido com sucesso");
        }
    }

    public IsencaoCadastroEconomico isencaoVigente() {
        for (IsencaoCadastroEconomico i : selecionado.getIsencoes()) {
            if (i.getFinalVigencia() == null) {
                return i;
            }
            long agora = cadastroEconomicoFacade.getSistemaFacade().getDataOperacao().getTime();
            long inicioVigencia = i.getInicioVigencia().getTime();
            long finalVigencia = i.getFinalVigencia().getTime();
            if (inicioVigencia <= agora && finalVigencia >= agora) {
                return i;
            }
        }
        return null;
    }

    public boolean validaIsencao() {
        boolean valida = true;
        if (isencao.getInicioVigencia() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a data de inicio de vigência");
            valida = false;
        }
        if (isencao.getFinalVigencia() != null && isencao.getFinalVigencia().before(isencao.getInicioVigencia())) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A data de final de vigência deve ser maior que a data de inicio de vigência");
            valida = false;
        }
        if (isencao.getTributo() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Tributo.");
            valida = false;
        }
        if (isencao.getPercentual() == null || isencao.getPercentual().compareTo(BigDecimal.ZERO) <= 0 || isencao.getPercentual().compareTo(new BigDecimal("100")) > 0) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O percentual deve ser maior que 0 (zero) e menor ou igual a 100 (cem)");
            valida = false;
        }
        if (isencao.getAtoLegal() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Ato Legal.");
            valida = false;
        }
        return valida;
    }

    public List<SelectItem> getNaturezaJuridica() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        if (selecionado.getPessoa() == null || selecionado.getPessoa().getId() == null) {
            return toReturn;
        }
        List<NaturezaJuridica> listaNaturezaJuridica = new ArrayList<>();
        if (isFisica()) {
            listaNaturezaJuridica = cadastroEconomicoFacade.getNaturezaJuridicaFacade().listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica.FISICA);
        }
        if (isJuridica()) {
            listaNaturezaJuridica = cadastroEconomicoFacade.getNaturezaJuridicaFacade().listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica.JURIDICA);
        }
        for (NaturezaJuridica naturezaJuridica : listaNaturezaJuridica) {
            toReturn.add(new SelectItem(naturezaJuridica, naturezaJuridica.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterNaturezaJuridica() {
        if (converterNaturezaJuridica == null) {
            converterNaturezaJuridica = new ConverterAutoComplete(NaturezaJuridica.class, cadastroEconomicoFacade.getNaturezaJuridicaFacade());
        }
        return converterNaturezaJuridica;
    }

    public boolean getTipoAutonomo() {
        try {
            return selecionado.getNaturezaJuridica() != null && selecionado.getNaturezaJuridica().getAutonomo();
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean getHabilitaPanelTipoAutonomo() {
        return getTipoAutonomo() || cadastroDerivadoRBTrans();
    }

    public void limparDadosAoSelecionarNaturezaJuridica() {
        selecionado.setTipoAutonomo(null);
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, cadastroEconomicoFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Converter getConverterCEP() {
        if (converterCEP == null) {
            converterCEP = new ConverterAutoComplete(CEP.class, cadastroEconomicoFacade.getCepFacade());
        }
        return converterCEP;
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, cadastroEconomicoFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public List<String> completaLogradouro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().listaLogradourosString(l, parte.trim());
    }


    public List<String> completaBairro(String parte) {
        String l = "";
        if (endereco.getLocalidade() != null) {
            l = endereco.getLocalidade();
        }
        return cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().listaBairrosString(l, parte.trim());
    }

    public List<String> completaCEP(String parte) {
        return cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().listaCEPString(parte.trim());
    }

    public void atualizaLogradouro() {
        EnderecoCorreio ec = endereco.converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
        cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().atualizarLogradouros(ec);
        endereco.setCep(ec.getCep());
        endereco.setLogradouro(ec.getLogradouro());
        endereco.setComplemento(ec.getComplemento());
        endereco.setBairro(ec.getBairro());
        endereco.setLocalidade(ec.getLocalidade());
        endereco.setUf(ec.getUf());
        endereco.setNumero(ec.getNumero());
        endereco.setTipoEndereco(ec.getTipoEndereco());
    }

    public List<CEPUF> getListaUF() {
        return cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().listaUf();
    }

    public List<String> completaCidade(String parte) {
        String l = "";
        if (endereco.getUf() != null) {
            l = endereco.getUf();
        }
        return cadastroEconomicoFacade.getPessoaFacade().getConsultaCepFacade().listaLocalidadesString(l, parte.trim());
    }

    private void validarEndereco() {
        ValidacaoException ve = new ValidacaoException();
        if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CEP é obrigatório.");
        }
        if (endereco.getBairro() == null || endereco.getLogradouro().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bairro é obrigatório.");
        }
        if (endereco.getLocalidade() == null || endereco.getLocalidade().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cidade é obrigatório.");
        }
        if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Logradouro é obrigatório.");
        }
        ve.lancarException();
    }

    public void adicionarEndereco() {
        try {
            validarEndereco();
            if (endereco.getSequencial() == null) {
                endereco.definirSequencial();
            }
            Util.adicionarObjetoEmLista(selecionado.getListaEnderecoCadastroEconomico(), endereco);
            limparEndereco();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removeEndereco(ActionEvent evento) {
        EnderecoCadastroEconomico vendereco = (EnderecoCadastroEconomico) evento.getComponent().getAttributes().get("vendereco");
        selecionado.getEnderecosSecundarios()
            .stream()
            .filter(es -> es.getSequencial() > vendereco.getSequencial())
            .forEach(es -> es.setSequencial(es.getSequencial() - 1));
        selecionado.getListaEnderecoCadastroEconomico().remove(vendereco);
    }

    public void alterarEndereco(EnderecoCadastroEconomico endereco) {
        this.endereco = (EnderecoCadastroEconomico) Util.clonarObjeto(endereco);
    }

    public void limparEndereco() {
        endereco = new EnderecoCadastroEconomico(TipoEndereco.CORRESPONDENCIA, selecionado);
    }

    public Face recuperaLoteFacePrincipal(Lote lote) {
        Testada testada = cadastroEconomicoFacade.getLoteFacade().recuperarTestadaPrincipal(lote);
        if (testada != null) {
            return testada.getFace();
        }
        return null;
    }

    public void instanciaFiltroServico() {
        servicoFiltro = new Servico();
        filtrarServicos();
    }

    public void filtrarServicos() {
        String where = "";
        String juncao = " where ";
        StringBuilder idsServicosAdicionados = new StringBuilder();
        for (Servico servicoAdd : selecionado.getServico()) {
            idsServicosAdicionados.append(servicoAdd.getId());
            idsServicosAdicionados.append(", ");
        }
        if (idsServicosAdicionados.length() > 0) {
            idsServicosAdicionados = new StringBuilder(idsServicosAdicionados.substring(0, idsServicosAdicionados.length() - 2));
            where += juncao + " not id in (" + idsServicosAdicionados + ") ";
            juncao = " and ";
        }
        if (servicoFiltro.getCodigo() != null && !servicoFiltro.getCodigo().isEmpty()) {
            where += juncao + " upper(codigo) like '%" + servicoFiltro.getCodigo().toUpperCase() + "%'";
            juncao = " and ";
        }
        if (servicoFiltro.getNome() != null && !servicoFiltro.getNome().isEmpty()) {
            where += juncao + " upper(nome) like '%" + servicoFiltro.getNome().toUpperCase() + "%'";
            juncao = " and ";
        }
        listaServico = cadastroEconomicoFacade.getServicoFacade().listaComWhere(where);
    }

    public Servico getServicoFiltro() {
        return servicoFiltro;
    }

    public void setServicoFiltro(Servico servicoFiltro) {
        this.servicoFiltro = servicoFiltro;
        FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Campo Numerico !");
    }

    public String getUrlEdicaoPessoa() {
        if (selecionado.getPessoa() != null) {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                return "/tributario/configuracoes/pessoa/editarpessoafisica/" + selecionado.getPessoa().getId() + "/";
            } else {
                return "/tributario/configuracoes/pessoa/editarpessoajuridica/" + selecionado.getPessoa().getId() + "/";
            }
        }
        return "";
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void selecaoCaracteristica() {
        if (bcecf.getCaracFuncionamento().getSemPublicidade()) {
            bcecf.setQuantidade(0L);
        } else {
            bcecf.setQuantidade(1L);
        }
    }

    public List<CertidaoAtividadeBCE> getCertidaoAtividadeBCE() {
        if (selecionado.getId() != null) {
            return certidaoAtividadeBCEFacade.recuperarCertidaoAtividadeBCE(selecionado);
        }
        return new ArrayList<>();

    }

    public void editaPessoa(Pessoa pessoa, TipoPessoaCadastro tipoPessoaCadastro) {
        if (pessoa != null && pessoa.getId() != null) {
            poeNaSessao(tipoPessoaCadastro);
            if (pessoa instanceof PessoaJuridica) {
                Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/editarpessoajuridica/" + pessoa.getId() + "/");
            } else {
                Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/editarpessoafisica/" + pessoa.getId() + "/");
            }
        }
    }

    public void verPessoa() {
        if (isJuridica()) {
            Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/verpessoajuridica/" + selecionado.getPessoa().getId() + "/");
        } else {
            Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/verpessoafisica/" + selecionado.getPessoa().getId() + "/");
        }

    }

    public void novoPessoaFisica(TipoPessoaCadastro tipoPessoaCadastro) {
        poeNaSessao(tipoPessoaCadastro);
        Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/novapessoafisica/");
    }

    public void novoPessoaJuridica(TipoPessoaCadastro tipoPessoaCadastro) {
        poeNaSessao(tipoPessoaCadastro);
        Web.navegacao(getUrlAtual(), "/tributario/configuracoes/pessoa/novapessoajuridica/");
    }

    public TipoPessoaCadastro getTipoPessoaCadastro() {
        return TipoPessoaCadastro.PESSOA;
    }

    public TipoPessoaCadastro getTipoSocioCadastro() {
        return TipoPessoaCadastro.SOCIO;
    }

    public TipoCadastroTributario getTipoCadastroContribuinte() {
        return TipoCadastroTributario.PESSOA;
    }

    public Long getIdRevisao() {
        return idRevisao;
    }

    public void setIdRevisao(Long idRevisao) {
        this.idRevisao = idRevisao;
    }

    public boolean hasPermissaoAlterar() {
        return Operacoes.NOVO.equals(operacao) || usuarioSistemaFacade.validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(),
            AutorizacaoTributario.PERMITIR_ALTERAR_ENQUADRAMENTO_FISCAL);
    }

    public boolean isAguardandoValidacao() {
        List<SituacaoCadastroEconomico> ultimaSiuacao = getUltimaSiuacao();
        if (ultimaSiuacao != null || !ultimaSiuacao.isEmpty()) {
            return SituacaoCadastralCadastroEconomico.AGUARDANDO_AVALIACAO.equals(ultimaSiuacao.get(0).getSituacaoCadastral());
        }
        return false;
    }

    public String getNomeBotaoSalvar() {
        if (isAguardandoValidacao()) {
            return "Aprovar";
        }
        return "Salvar";
    }

    public String getNomeTipoPessoa() {
        if (isPessoaFisica()) {
            return "Física";
        }
        return "Jurídica";
    }

    public List<LivroFiscal> getLivrosFiscais() {
        return Lists.newArrayList();
    }

    public boolean contemCnaePessoa(PessoaCNAE pessoaCNAE) {
        boolean contem = false;
        if (getCnaes() != null && pessoaCNAE != null) {
            for (EconomicoCNAE economicoCNAE : getCnaes()) {
                if (economicoCNAE.getCnae() != null && pessoaCNAE.getCnae() != null &&
                    economicoCNAE.getCnae().equals(pessoaCNAE.getCnae())) {
                    contem = true;
                    break;
                }
            }
        }
        return contem;
    }

    public boolean contemTodosCnaePessoa() {
        boolean contem = !getCnaes().isEmpty();
        for (PessoaCNAE pessoaCNAE : cnaesPessoa) {
            boolean contemCnae = false;
            for (EconomicoCNAE economicoCNAE : getCnaes()) {
                if (economicoCNAE.getCnae().equals(pessoaCNAE.getCnae())) {
                    contemCnae = true;
                }
            }
            if (!contemCnae) {
                return false;
            }
        }
        return contem;
    }

    public void removerCnaePessoa(PessoaCNAE pessoaCNAE) {
        EconomicoCNAE cnaeParaRemover = null;
        for (EconomicoCNAE economicoCNAE : getCnaes()) {
            if (pessoaCNAE.getCnae().equals(economicoCNAE.getCnae())) {
                cnaeParaRemover = economicoCNAE;
                break;
            }
        }
        if (cnaeParaRemover != null) {
            selecionado.getEconomicoCNAE().remove(cnaeParaRemover);
        }
    }

    public void removerTodosCnaePessoa() {
        selecionado.getEconomicoCNAE().clear();
    }

    public void adicionarCnaePessoa(PessoaCNAE pessoaCNAE) {
        try {
            EconomicoCNAE cnaeParaAdicionar = new EconomicoCNAE();
            cnaeParaAdicionar.setCadastroEconomico(selecionado);
            cnaeParaAdicionar.setCnae(pessoaCNAE.getCnae());
            cnaeParaAdicionar.setInicio(pessoaCNAE.getInicio());
            cnaeParaAdicionar.setFim(pessoaCNAE.getFim());
            cnaeParaAdicionar.setDataregistro(pessoaCNAE.getDataregistro());
            cnaeParaAdicionar.setHorarioFuncionamento(pessoaCNAE.getHorarioFuncionamento());
            cnaeParaAdicionar.setTipo(pessoaCNAE.getTipo());
            cnaeParaAdicionar.setExercidaNoLocal(pessoaCNAE.getExercidaNoLocal());
            selecionado.getEconomicoCNAE().add(cnaeParaAdicionar);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void adicionarTodosCnaePessoa() {
        for (PessoaCNAE pessoaCNAE : cnaesPessoa) {
            if (!contemCnaePessoa(pessoaCNAE) && pessoaCNAE.getExercidaNoLocal() != null && pessoaCNAE.getExercidaNoLocal()) {
                adicionarCnaePessoa(pessoaCNAE);
            }
        }
    }

    private void validarSincronizacaoComRedeSim(ConfiguracaoWebService configuracaoWs) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoa().isPessoaFisica()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A sincronização deve ser feita exclusívamente à pessoas jurídicas");
        }
        if (configuracaoWs == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A sincronização precisa de uma configuração de conexão, confira nas configurações do tributário, aba WebServices se existe essa configuração");
        } else {
            if (Strings.isNullOrEmpty(configuracaoWs.getUrl())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a URL de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getUsuario())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar o usuário de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getSenha())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a senha de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getDetalhe())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a URL do integrador nas configuração do tributário, aba WebServices, campo detalhes");
            }
        }
        ve.lancarException();
    }

    public void sincronizarComRedeSim() {
        try {
            eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(selecionado.getPessoa().getCpf_Cnpj(), cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados(), true);
            if (eventoRedeSimDTO == null) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possível encontrar as informações do CNPJ: " + selecionado.getPessoa().getCpf_Cnpj() + " na base da REDESIM");
            } else {
                FacesUtil.atualizarComponente("FormularioRedeSim");
                FacesUtil.executaJavaScript("mostrarDialogRedeSim()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao comunicar com a RedeSim: ", ex);
            FacesUtil.addOperacaoNaoRealizada("Erro ao comunicar com a RedeSim: " + ex.getMessage());
        }
    }

    public void confirmarAtualizacaoDadosPessoaRedeSim() {
        integracaoRedeSimFacade.atualizarDadosPessoaJuridica(eventoRedeSimDTO, HistoricoAlteracaoRedeSim.CADASTRO_ECONOMICO,
            getSistemaControlador().getUsuarioCorrente(), cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados());
        integracaoRedeSimFacade.atualizarDadosDoCadastro(eventoRedeSimDTO, selecionado, false,
            getSistemaControlador().getUsuarioCorrente());
        EventoRedeSim evento = new EventoRedeSim();
        evento.setCodigo(selecionado.getInscricaoCadastral() + "-" + juntasComercialPessoaJuridica.size());
        evento.setVersao(eventoRedeSimDTO.getVersao());
        evento.setIdentificador("Sincronização Manual");
        evento.setCnpj(Util.formatarCnpj(eventoRedeSimDTO.getPessoaDTO().getCnpj()));
        evento.setTipoEvento(EventoRedeSim.TipoEvento.ATUALIZACAO);
        evento.setSituacao(EventoRedeSim.Situacao.PROCESSADO);
        evento.setDescricao(selecionado.getDescricao());
        try {
            evento.setSituacaoEmpresa(eventoRedeSimDTO.getPessoaDTO().getDescricaoSituacaoRFB());
            evento.setConteudo(new ObjectMapper().writeValueAsString(eventoRedeSimDTO.getPessoaDTO()));
        } catch (JsonProcessingException e) {
            evento.setConteudo(null);
        }
        integracaoRedeSimFacade.salvarEventoRedeSim(evento);
        if (selecionado.getPessoa().isPessoaJuridica()) {
            integracaoRedeSimFacade.confirmarResposta(eventoRedeSimDTO, (PessoaJuridica) selecionado.getPessoa(), selecionado);
        }

        selecionado = null;
        selecionarPadrao();
        abrirDialogSelecaoCnaes();
    }

    private void abrirDialogSelecaoCnaes() {
        if (isJuridica()) {
            removerCnaesNaoExercidosNoLocal();
            adicionarCnaesParaCMC();
            salvarCadastroEconomicoAlterandoGrauDeRiscoCnae();
        }
    }

    private void removerCnaesNaoExercidosNoLocal() {
        List<EconomicoCNAE> cnaesRemover = Lists.newArrayList();
        for (EconomicoCNAE economicoCNAE : selecionado.getEconomicoCNAE()) {
            boolean temNaPessoa = false;
            for (PessoaCNAE pessoaCNAE : getCnaesPessoa()) {
                if (economicoCNAE.getCnae() != null && pessoaCNAE.getCnae() != null &&
                    pessoaCNAE.getCnae().equals(economicoCNAE.getCnae())) {
                    temNaPessoa = true;
                    break;
                }
            }
            if (!temNaPessoa) {
                cnaesRemover.add(economicoCNAE);
            }
        }
        for (PessoaCNAE pessoaCNAE : getCnaesPessoa()) {
            for (EconomicoCNAE economicoCNAE : getCnaes()) {
                if (economicoCNAE.getCnae() != null && pessoaCNAE.getCnae() != null &&
                    economicoCNAE.getCnae().equals(pessoaCNAE.getCnae())) {
                    cnaesRemover.add(economicoCNAE);
                    break;
                }
            }
        }
        selecionado.getEconomicoCNAE().removeAll(cnaesRemover);
    }

    private void adicionarCnaesParaCMC() {
        for (PessoaCNAE pessoaCNAE : getCnaesPessoa()) {
            boolean jaTem = false;
            for (EconomicoCNAE economicoCNAE : selecionado.getEconomicoCNAEAtivos()) {
                if (pessoaCNAE != null && pessoaCNAE.getCnae() != null && economicoCNAE != null && economicoCNAE.getCnae() != null &&
                    pessoaCNAE.getCnae().getCodigoCnae().equals(economicoCNAE.getCnae().getCodigoCnae()) &&
                    pessoaCNAE.getCnae().getGrauDeRisco().equals(economicoCNAE.getCnae().getGrauDeRisco())) {

                    jaTem = true;
                    break;
                }
            }
            if (!jaTem) {
                adicionarCnaePessoa(pessoaCNAE);
            }
        }
    }

    public void salvarCadastroEconomicoAlterandoGrauDeRiscoCnae() {
        if (selecionado.validarAlteracaoCnaeCadastroEconomico()) {
            selecionado = cadastroEconomicoFacade.salvarRetornado(selecionado);
            if (alteracaoCmcFacade.hasAlteracaoCadastro(selecionado.getId())
                && (SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.equals(selecionado.getSituacaoAtual().getSituacaoCadastral())
                || SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.equals(selecionado.getSituacaoAtual().getSituacaoCadastral()))) {
                cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(selecionado, isMei());
            }
            FacesUtil.addOperacaoRealizada("Sincronização realizada com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } else {
            FacesUtil.addOperacaoNaoPermitida("Deve existir um CNAE Primário selecionado");
        }
    }

    public void recarregarPagina() {
        if (alteracaoCmcFacade.hasAlteracaoCadastro(selecionado.getId())) {
            cadastroEconomicoFacade.criarNotificacaoDeCalculoDeAlvara(selecionado, isMei());
        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public String tratarNomeSocio(SociedadePessoaJuridicaDTO socio) {
        return socio.getPessoa().isPessoaJuridica() ? socio.getPessoa().getPessoaJuridica().getNome()
            : socio.getPessoa().getPessoaFisica().getNome();
    }

    public PessoaJuridica getPessoaJuridica() {
        if (isJuridica()) {
            return Util.recuperarObjetoHibernateProxy(selecionado.getPessoa());
        }
        return null;
    }

    public String tratarCnpj(String cnpj) {
        return Util.formatarCnpj(cnpj);
    }

    public String tratarCpf(String cpf) {
        return Util.formatarCpf(cpf);
    }

    public boolean isPessoaJuridicaBaixada() {
        if (selecionado.getPessoa() != null && selecionado.getPessoa().isPessoaJuridica()) {
            return !SituacaoCadastralPessoa.ATIVO.equals(selecionado.getPessoa().getSituacaoCadastralPessoa());
        }
        return false;
    }

    public enum TipoPessoaCadastro {
        PESSOA,
        SOCIO;
    }

    public List<PessoaHorarioFuncionamento> buscarHorariosDeFuncionamento() {
        return selecionado.getPessoa().getHorariosFuncionamentoAtivos();
    }

    public boolean isAutonomoMotorista() {
        if (selecionado.getId() != null) {
            if (autonomoMotorista == null) {
                autonomoMotorista = cadastroEconomicoFacade.isAutonomoMotorista(selecionado);
            }
            return autonomoMotorista;
        }
        return false;
    }

    public void mudouClassificacaoAtividade() {
        if (selecionado != null && selecionado.getClassificacaoAtividade() != null && selecionado.getClassificacaoAtividade().isPrestacaoServico()) {
            enquadramentoFiscal.setTipoNotaFiscalServico(TipoNotaFiscalServico.ELETRONICA);
        } else {
            enquadramentoFiscal.setTipoNotaFiscalServico(TipoNotaFiscalServico.NAO_EMITE);
        }
    }

    public List<SelectItem> getTiposMateriaPrima() {
        return Util.getListSelectItem(TipoMateriaPrima.values(), false);
    }

    public List<SelectItem> getTiposLocalizacao() {
        return Util.getListSelectItem(TipoLocalizacao.values(), false);
    }

    public List<SelectItem> buscarTiposSimNao() {
        return Util.getListSelectItem(TipoSimNao.values(), false);
    }

    public boolean isAreaTerrenoParticular() {
        return enquadramentoAmbiental != null && TipoSimNao.SIM.equals(enquadramentoAmbiental.getTerrenoParticular());
    }

    public void adicionarEnquadramentoAmbiental() {
        if (!selecionado.getEnquadramentosAmbientais().contains(enquadramentoAmbiental)) {
            for (EnquadramentoAmbiental enquadramento : selecionado.getEnquadramentosAmbientais()) {
                if (enquadramento.isVigente()) {
                    enquadramento.setFinalVigencia(new Date());
                }
            }
        }
        enquadramentoAmbiental.setCadastroEconomico(selecionado);
        enquadramentoAmbiental.setInicioVigencia(new Date());
        Util.adicionarObjetoEmLista(selecionado.getEnquadramentosAmbientais(), enquadramentoAmbiental);

        enquadramentoAmbiental = new EnquadramentoAmbiental();
    }

    public void editarEnquadramentoAmbiental(EnquadramentoAmbiental ambiental) {
        enquadramentoAmbiental = (EnquadramentoAmbiental) Util.clonarObjeto(ambiental);
    }

    public void removerEnquadramentoAmbiental(EnquadramentoAmbiental ambiental) {
        selecionado.getEnquadramentosAmbientais().remove(ambiental);
    }

    public boolean hasSomenteCnaeDispensado() {
        for (EconomicoCNAE cnaeAtivo : selecionado.getEconomicoCNAEAtivos()) {
            Boolean dispensado = cnaeAtivo.getCnae().getDispensado();
            if (dispensado == null || !dispensado) {
                return false;
            }
        }
        return true;
    }

    public void enviarBCMParaRedeSim() {
        try {
            cadastroEconomicoFacade.enviarBcmParaRedeSim(selecionado);
        } catch (Exception e) {
            logger.error("Erro ao enviar BCM para redesim. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o BCM para RedeSim. Detalhes: " + e.getMessage());
        }
        try {
            calculoAlvaraFacade.enviarDispensaLicenciamento(cadastroEconomicoFacade.getSistemaFacade()
                .getExercicioCorrente(), selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao enviar Dispensa de Licenciamento para redesim. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar Dispensa de Licenciamento para redesim. ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar a dispensa de licenciamento do BCM para RedeSim. Detalhes: " + e.getMessage());
        }
    }

    public boolean isMei() {
        if (selecionado != null && selecionado.getId() != null) {
            if (mei == null) {
                EnquadramentoFiscal enquadramentoFiscal = cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(selecionado);
                mei = (enquadramentoFiscal != null && enquadramentoFiscal.isMei());
            }
            return mei;
        }
        return false;
    }

    public List<SelectItem> getVersoesDesif() {
        return Util.getListSelectItem(VersaoDesif.values());
    }

    public List<RequerimentoLicenciamentoETR> getRequerimentoLicenciamentoETRList() {
        return requerimentoLicenciamentoETRList;
    }

    public void setRequerimentoLicenciamentoETRList(List<RequerimentoLicenciamentoETR> requerimentoLicenciamentoETRList) {
        this.requerimentoLicenciamentoETRList = requerimentoLicenciamentoETRList;
    }

    public List<HistoricoInscricaoCadastral> getHistoricosInscricaoCadastral() {
        return historicosInscricaoCadastral;
    }

    public void setHistoricosInscricaoCadastral(List<HistoricoInscricaoCadastral> historicosInscricaoCadastral) {
        this.historicosInscricaoCadastral = historicosInscricaoCadastral;
    }

    public void visualizarRequerimentoLicenciamentoETR(RequerimentoLicenciamentoETR requerimento) {
        FacesUtil.redirecionamentoInterno("/tributario/licenciamento-etr/requerimento/ver/" + requerimento.getId());
    }

    public void recuperarRequerimentoLicenciamentoETRList() {
        EconomicoCNAE economicoCNAEPrincipal = selecionado.getEconomicoCNAEPrincipal();
        ParametroETR parametroETR = cadastroEconomicoFacade.getParametroETRFacade().recuperarParametroETR();
        if (economicoCNAEPrincipal != null &&
            parametroETR != null &&
            economicoCNAEPrincipal.getCnae().equals(parametroETR.getCnae())) {
            requerimentoLicenciamentoETRList = cadastroEconomicoFacade.getRequerimentoLicenciamentoETRFacade()
                .buscarPorCadastroEconomico(selecionado);
        }
    }

    public void mudouAba(TabChangeEvent evt) {
        if (evt == null || evt.getTab() == null) {
            return;
        }
        if (evt.getTab().getId().equals("tabHistoricosInscricaoCadastral")) {
            carregarHistoricosInscricaoCadastral();
        }
    }

    private void carregarHistoricosInscricaoCadastral() {
        historicosInscricaoCadastral = cadastroEconomicoFacade.getHistoricoInscricaoCadastralFacade()
            .buscarHistoricosPorCadastro(selecionado);
        FacesUtil.atualizarComponente("Formulario:tabViewGeral:opCmcsAnteriores");
    }

    public List<SelectItem> getAnexosLei1232006() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (anexosLei1232006 == null) {
            anexosLei1232006 = anexoLei1232006Facade.buscarTodosOrdenandoPorParametro("descricao");
        }
        toReturn.add(new SelectItem(null, ""));
        for (AnexoLei1232006 anexoLei1232006 : anexosLei1232006) {
            toReturn.add(new SelectItem(anexoLei1232006, anexoLei1232006.getDescricao()));
        }
        return toReturn;
    }
}
