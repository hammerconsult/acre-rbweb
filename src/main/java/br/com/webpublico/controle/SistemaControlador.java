/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FavoritoDTO;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.entidadesauxiliares.comum.AlterarDataUnidadeLogada;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.report.WebReportDTO;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.VerificadorSessoesAtivasUsuario;
import br.com.webpublico.seguranca.resources.UsuarioSistemaResource;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.singletons.SingletonFavoritos;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpush.push.Push;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ManagerdBean que contém as configurações correntes dos usuário.
 *
 * @author Munif
 */
@ManagedBean
@SessionScoped
public class SistemaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SistemaControlador.class);
    private final String URL_NOTIFICACAO_SERVICE = "URL_NOTIFICACAO_SERVICE";
    private final String DEFAULT_NOTIFICACAO_SERVICE = "https://notificacao.webpublico.dev.br";

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private FavoritoFacade favoritoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private String filtroFavorido;
    private String nomeFavorito;
    private String urlFavorito;
    private String appVersion;
    private SingletonFavoritos singletonFavoritos;
    private AlterarDataUnidadeLogada alterarDataUnidadeLogada;
    private SistemaService sistemaService;
    private WebReportDTO webReportDTO;
    private String liberacaoVersao;
    private String notasVersao;
    private Boolean exibirVersaoDialog;

    /**
     * Creates a new instance of SistemaControlador
     */
    public SistemaControlador() {
    }


    @PostConstruct
    public void init() {
        Properties properties = new Properties();
        try {
            singletonFavoritos = (SingletonFavoritos) Util.getSpringBeanPeloNome("singletonFavoritos");
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            sistemaService = (SistemaService) ap.getBean("sistemaService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        appVersion = properties.getProperty("version");
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public Integer getExercicioCorrenteAsInteger() {
        return Integer.parseInt(sistemaFacade.getExercicioCorrente().toString());
    }

    public Integer getExercicio() {
        return Integer.parseInt(sistemaFacade.getExercicioCorrente().toString());
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        try {
            sistemaFacade.setExercicioCorrente(exercicioCorrente);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrcamentoCorrente() {
        UnidadeOrganizacional orc = sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente();
        if (orc == null) {
            vaiParaErroDeNenhumaUnidadeVigente();
        }
        return orc;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdministrativaCorrente() {
        try {
            return sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente();
        } catch (Exception e) {
            e.printStackTrace();
            vaiParaErroDeNenhumaUnidadeVigente();
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativaCorrente() {
        try {
            return sistemaFacade.getSistemaService().getHierarquiAdministrativaCorrente();
        } catch (Exception e) {
            vaiParaErroDeNenhumaUnidadeVigente();
        }
        return null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentariaCorrente() {
        try {
            return sistemaFacade.getSistemaService().getHierarquiOrcamentariaCorrente();
        } catch (Exception e) {
            vaiParaErroDeNenhumaUnidadeVigente();
        }
        return null;
    }


    private void vaiParaErroDeNenhumaUnidadeVigente() {
        try {
            FacesUtil.redirecionamentoInterno("/acesso-negado/sem-unidade-organizacional/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redirecionarParaHome() {
        try {
            FacesUtil.redirecionamentoInterno("/home");
        } catch (Exception e) {
            logger.error("Não foi possível redirecionar para o HOME", e);
        }
    }


    public String getUsuarioBancoConectado() {
        return sistemaFacade.getUsuarioDB();
    }

    public String getUrlBanco() {
        return sistemaFacade.getURLBanco();
    }

    public void trocarUnidadeAdministrativa() {
        try {
            FacesUtil.executaJavaScript("aguarde.show();");
            if (mostrarDialogEscolherUnidadeOrcamentaria()) {
                FacesUtil.executaJavaScript("hierarquias.show();");
                FacesUtil.executaJavaScript("aguarde.hide();");
            } else {
                notificarTrocaUnidade();
            }
        } catch (ValidacaoException e) {
            FacesUtil.executaJavaScript("aguarde.hide();");
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            vaiParaErroDeNenhumaUnidadeVigente();
        }
    }

    public void trocarUnidadeOrcamentaria() {
        try {
            notificarTrocaUnidade();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível alterar a Unidade Orçamentária");
        }
    }

    public void trocarUnidadeOrcamentariaSemAlterarAdministrativa(UnidadeOrganizacional unidade) {
        try {
            sistemaFacade.getSistemaService().setOrcamentariaCorrente(unidade.getId(), Boolean.FALSE);
            notificarTrocaUnidade();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível alterar a Unidade Orçamentária");
        }
    }

    public Boolean mostrarDialogEscolherUnidadeOrcamentaria() {
        try {
            if (getTodasHierarquiaOrcamentariasDaAdminstrativa().size() > 1) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar as Unidades Orçamentárias da Administrativa logada.");
        }
        return Boolean.FALSE;
    }

    public List<HierarquiaOrganizacional> getTodasHierarquiaOrcamentariasDaAdminstrativa() {
        return sistemaFacade.getTodasHierarquiaOrcamentariasDaAdminstrativa();
    }

    public ClasseDoAtributo getClasseLote() {
        return ClasseDoAtributo.LOTE;
    }

    public ClasseDoAtributo getClasseCadastroImobiliario() {
        return ClasseDoAtributo.CADASTRO_IMOBILIARIO;
    }

    public ClasseDoAtributo getClasseCadastroRural() {
        return ClasseDoAtributo.CADASTRO_RURAL;
    }

    public ClasseDoAtributo getClasseConstrucao() {
        return ClasseDoAtributo.CONSTRUCAO;
    }

    public ClasseDoAtributo getClasseCadastroEconomico() {
        return ClasseDoAtributo.CADASTRO_ECONOMICO;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaService.getUsuarioCorrente();
    }

    public String getIpCorrente() {
        return sistemaFacade.getIp();
    }

    public List<HierarquiaOrganizacionalDTO> getUnidadesOrcamentariasDoUsuario() {
        return sistemaFacade.getUnidadesOrcamentariasDoUsuario();

    }

    public String getMunicipio() {
        return sistemaFacade.getMunicipio();
    }

    public LOA getLOADoExercicio(Exercicio exerc) throws ExcecaoNegocioGenerica {
        return exercicioFacade.getLoaPorExercicio(exerc);
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisGestorProtocolo() {
        return usuarioSistemaFacade.unidadesOrganizacionaisGestorProtocolo(getUsuarioCorrente());
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisGestorMateriais() {
        return usuarioSistemaFacade.unidadesOrganizacionaisGestorMateriais(getUsuarioCorrente());
    }

    public ExceptionLog geraExceptionLog(ExceptionLog log) {
        return sistemaFacade.geraExceptionLog(log);
    }

    public void baixarTxTExceptionLog(ExceptionLog log) {
        String pulaLinha = System.getProperty("line.separator");
        StringBuilder conteudo = new StringBuilder("======= Exception Causada no Sistema do município de " + getMunicipio() + " =======\n\n").append(pulaLinha);
        conteudo.append(pulaLinha).append("Data: ").append(log.getDataRegistro());
        conteudo.append(pulaLinha).append("Local: ").append(log.getPagina());
        conteudo.append(pulaLinha).append("Tipo: ").append(log.getTipoException());
        conteudo.append(pulaLinha).append("Usuário: ").append(log.getUsuarioLogado());
        conteudo.append(pulaLinha).append("IP: ").append(log.getIp());
        conteudo.append(pulaLinha).append("Caused By: ").append(log.getCausedBy()).append(pulaLinha);
        conteudo.append(pulaLinha).append("Stack Trace: ").append(log.getStackTrace());
        byte[] bytes = conteudo.toString().getBytes();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setContentType("application/txt");
        response.setHeader("Content-Disposition", "inline; filename=error.txt");
        response.setContentLength(bytes.length);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public void setDataOperacao(Date dataOperacao) {
        sistemaFacade.setDataOperacao(dataOperacao);
    }

    public String getDataOperacaoFormatada() {
        return Util.formatterDiaMesAno.format(sistemaFacade.getDataOperacao());
    }

    public String getAnoOperacaoFormatada() {
        return Util.formatterAno.format(sistemaFacade.getDataOperacao());
    }

    public String getNomeFavorito() {
        return nomeFavorito;
    }

    public void setNomeFavorito(String nomeFavorito) {
        this.nomeFavorito = nomeFavorito;
    }

    public String getUrlFavorito() {
//        if (urlFavorito == null) {
        urlFavorito = PrettyContext.getCurrentInstance().getRequestURL().toString();
//        }
        return urlFavorito;
    }

    public void setUrlFavorito(String urlFavorito) {
        this.urlFavorito = urlFavorito;
    }

    public List<FavoritoDTO> getFavoritos() {
        List<FavoritoDTO> favoritos = singletonFavoritos.getFavoritos();
        if (filtroFavorido != null) {
            if ("".equals(filtroFavorido.trim())) {
                return favoritos;
            }
            List<FavoritoDTO> retorno = new ArrayList<>();
            for (FavoritoDTO favorito : favoritos) {
                if (favorito.getNome() != null) {
                    if (favorito.getNome().toLowerCase().contains(filtroFavorido.toLowerCase())) {
                        retorno.add(favorito);
                    }
                }
            }
            return retorno;
        }
        return favoritos;
    }

    public String getFiltroFavorido() {
        return filtroFavorido;
    }

    public void setFiltroFavorido(String filtroFavorido) {
        this.filtroFavorido = filtroFavorido;
    }

    public void adicionarFavorito() {
        Favorito favorito = new Favorito(nomeFavorito, urlFavorito, getUsuarioCorrente());
        favoritoFacade.salvar(favorito);
        singletonFavoritos.adicionarFavorito(favorito.toDTO());
        urlFavorito = null;
        nomeFavorito = null;
    }

    public void removerFavorito(FavoritoDTO dto) {
        Long id = dto.getId();
        if (id != null) {
            Favorito favorito = (Favorito) favoritoFacade.recuperar(Favorito.class, id);
            favoritoFacade.remover(favorito);
        }
        singletonFavoritos.remover(dto);
    }

    public void redirecionarFavorito(Favorito favorito) {
        try {
            FacesUtil.redirecionamentoInterno(favorito.getUrl());
        } catch (Exception e) {

        }
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }


    public void notificarTrocaUnidade() {
        Push.sendTo(getUsuarioCorrente(), "trocouUnidade");
    }

    public String getCanal() {
        return "/user-" + getUsuarioCorrente().getId();
    }

    public String recuperarFaseBloqueando() {
        return getSistemaFacade().getSistemaService().recuperarFaseBloqueando();
    }

    public SistemaFacade.PerfilApp getPerfilAplicacao() {
        return sistemaFacade.getPerfilAplicacao();
    }

    public String getMsgConfiguracaoPerfilAplicacao() {
        return sistemaFacade.getConfiguracaoPerfilApp().getMensagemRodape();
    }

    public void imprimirRelatorioDaSessao(Boolean novaAba) {
        try {
            StreamedContent relatorio = (StreamedContent) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("relatorio");
            InputStream is = relatorio.getStream();
            is.reset();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int reads = is.read();
            while (reads != -1) {
                baos.write(reads);
                reads = is.read();
            }
            imprimirRelatorio(baos.toByteArray(), relatorio.getName(), novaAba);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void imprimirRelatorio(byte[] bytes, String nome, Boolean novaAba) {
        if (bytes != null && bytes.length > 0) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.responseComplete();
                if (bytes != null && bytes.length > 0) {
                    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                    response.setContentType(novaAba ? "application/pdf" : "application/force-download");
                    response.setHeader("Content-disposition", "inline; filename=\"" + nome + ".pdf\"");
                    response.setContentLength(bytes.length);
                    ServletOutputStream outputStream = response.getOutputStream();
                    outputStream.write(bytes, 0, bytes.length);
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            }
        }

    }

    public void keepSessionAlive() {
        logger.debug("Keep Alive {}", getUsuarioCorrente());
    }

    public AlterarDataUnidadeLogada getAlterarDataUnidadeLogada() {
        return alterarDataUnidadeLogada;
    }

    public void setAlterarDataUnidadeLogada(AlterarDataUnidadeLogada alterarDataUnidadeLogada) {
        this.alterarDataUnidadeLogada = alterarDataUnidadeLogada;
    }

    public void prepararTrocarUnidade(String tipo) {
        this.alterarDataUnidadeLogada = new AlterarDataUnidadeLogada();
        this.alterarDataUnidadeLogada.setTipo(TipoHierarquiaOrganizacional.valueOf(tipo));
        this.alterarDataUnidadeLogada.setDataOperacao(sistemaFacade.getDataOperacao());
        this.alterarDataUnidadeLogada.setAdministrativa(getHierarquiaOrganizacionalAdministrativaCorrente());
        this.alterarDataUnidadeLogada.setOrcamentaria(getHierarquiaOrganizacionalOrcamentariaCorrente());
    }

    public void atribuirDataHoje() {
        this.alterarDataUnidadeLogada.setDataOperacao(new Date());
        atribuirNulUnidades();
    }

    public void atribuirNulUnidades() {
        try {
            Boolean limpar = verificarVigenciaDasUnidades();
            if (limpar) {
                this.alterarDataUnidadeLogada.setAdministrativa(null);
                this.alterarDataUnidadeLogada.setOrcamentaria(null);
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada("Erro ao verificar vigência das unidades. Erro: " + e.getMessage());
        }

    }

    private Boolean verificarVigenciaDasUnidades() {
        if (this.alterarDataUnidadeLogada.getAdministrativa() == null || this.alterarDataUnidadeLogada.getOrcamentaria() == null || this.alterarDataUnidadeLogada.getDataOperacao() == null) {
            return Boolean.TRUE;
        }
        UnidadeOrganizacional orcamentaria = this.alterarDataUnidadeLogada.getOrcamentaria().getSubordinada();
        UnidadeOrganizacional administrativa = this.alterarDataUnidadeLogada.getAdministrativa().getSubordinada();
        Date dataOperacao = this.alterarDataUnidadeLogada.getDataOperacao();

        HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, orcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, administrativa, TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        if (hoAdm == null || hoOrc == null) {
            return Boolean.TRUE;
        }
        if (hoAdm != null) {
            this.alterarDataUnidadeLogada.setAdministrativa(hoAdm);
        }
        if (hoOrc != null) {
            this.alterarDataUnidadeLogada.setOrcamentaria(hoOrc);
        }
        return Boolean.FALSE;
    }

    public List<SelectItem> getTiposHierarquias() {
        if (this.alterarDataUnidadeLogada != null) {
            List<SelectItem> retorno = Lists.newArrayList();
            retorno.add(new SelectItem(this.alterarDataUnidadeLogada.getTipo(), this.alterarDataUnidadeLogada.getTipo().getDescricao()));
            return retorno;
        }
        return Lists.newArrayList();
    }

    public void confirmarTrocarUnidade() {
        try {
            validarConfirmarTrocaUnidade();
            UnidadeOrganizacional orcamentaria = this.alterarDataUnidadeLogada.getOrcamentaria().getSubordinada();
            UnidadeOrganizacional administrativa = this.alterarDataUnidadeLogada.getAdministrativa().getSubordinada();
            sistemaFacade.getSistemaService().trocarDataSistema(this.alterarDataUnidadeLogada.getDataOperacao());
            sistemaFacade.getSistemaService().atribuirUnidadesDoUsuarioCorrente(administrativa, orcamentaria);
            UsuarioSistemaResource usuarioSistemaResource = (UsuarioSistemaResource) Util.getSpringBeanPeloNome("usuarioSistemaResource");
            usuarioSistemaResource.atribuirNullUsuario();
            notificarTrocaUnidade();
            FacesUtil.executaJavaScript("trocarUnidadeLogada.hide();location.reload();");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada("Erro ao trocar unidade do usuário. Erro: " + e.getMessage());
        }
    }

    private void validarConfirmarTrocaUnidade() {
        ValidacaoException ex = new ValidacaoException();
        if (this.alterarDataUnidadeLogada.getDataOperacao() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Selecione a data logada.");
        }
        if (this.alterarDataUnidadeLogada.getOrcamentaria() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Informe a unidade orçamentária.");
        }
        if (this.alterarDataUnidadeLogada.getAdministrativa() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Informe a unidade administrativa.");
        }
        ex.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte, getUsuarioCorrente(), alterarDataUnidadeLogada.getDataOperacao(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte, getUsuarioCorrente(), alterarDataUnidadeLogada.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<SelectItem> buscarHierarquiasOrcamentariaDaAdministrativa() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (alterarDataUnidadeLogada != null) {
            if (alterarDataUnidadeLogada.getAdministrativa() != null && alterarDataUnidadeLogada.getAdministrativa().getSubordinada() != null) {
                for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(alterarDataUnidadeLogada.getAdministrativa().getSubordinada(), alterarDataUnidadeLogada.getDataOperacao())) {
                    toReturn.add(new SelectItem(obj, obj.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> buscarHierarquiasAdministrativaDaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (alterarDataUnidadeLogada != null) {
            if (alterarDataUnidadeLogada.getOrcamentaria() != null && alterarDataUnidadeLogada.getOrcamentaria().getSubordinada() != null) {
                for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.buscarHierarquiaAdministrativaPorUnidadeOrcamentaria(alterarDataUnidadeLogada.getOrcamentaria().getSubordinada(), alterarDataUnidadeLogada.getDataOperacao())) {
                    toReturn.add(new SelectItem(obj, obj.toString()));
                }
            }
        }
        return toReturn;
    }

    public Map<String, WebReportDTO> getRelatoriosUsuario() {
        return sistemaService.getReportService().getRelatorios(getUsuarioCorrente());
    }

    public boolean isRelatorioConcluido(String relatorio) {
        return getRelatoriosUsuario().get(relatorio) != null && getRelatoriosUsuario().get(relatorio).isConcluido();
    }

    public boolean isRelatorioPDF(String relatorio) {
        return TipoRelatorioDTO.PDF.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioPDFDownload(String relatorio) {
        return TipoRelatorioDTO.PDF_DOWNLOAD.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioXLS(String relatorio) {
        return TipoRelatorioDTO.XLS.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioCsv(String relatorio) {
        return TipoRelatorioDTO.CSV.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioDocx(String relatorio) {
        return TipoRelatorioDTO.DOCX.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioXml(String relatorio) {
        return TipoRelatorioDTO.XML.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioTxt(String relatorio) {
        return TipoRelatorioDTO.TXT.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }

    public boolean isRelatorioHtml(String relatorio) {
        return TipoRelatorioDTO.HTML.equals(getRelatoriosUsuario().get(relatorio).getDto().getTipoRelatorio());
    }


    public List<String> getTodosRelatoriosUsuario() {
        return Lists.newArrayList(sistemaService.getReportService().getRelatorios(getUsuarioCorrente()).keySet());
    }

    public Boolean isRelatorioNaoVisualizado() {
        Map<String, WebReportDTO> relatoriosUsuario = getRelatoriosUsuario();
        for (String s : relatoriosUsuario.keySet()) {
            WebReportDTO webReportDTO = relatoriosUsuario.get(s);
            if (!webReportDTO.getVisualizado() && webReportDTO.isConcluido()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public void imprimirRelatorio(String uuid) throws IOException {
        marcarComoVisualizado(uuid);
        sistemaService.getReportService().imprimirRelatorio(getUsuarioCorrente(), uuid);
    }


    public void cancelarUltimoRelatorioGerandoNovo() {
        try {
            sistemaService.getReportService().removerRelatorio(getUsuarioCorrente(), webReportDTO.getUuid());
            webReportDTO.getDto().setVerificarCache(Boolean.FALSE);
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), webReportDTO.getDto());
            webReportDTO = null;
            FacesUtil.executaJavaScript("visualizarRelatorioOutroUsuario.hide()");
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void removerRelatorio(String uuid) {
        sistemaService.getReportService().removerRelatorio(getUsuarioCorrente(), uuid);
    }

    public void visualizarAcompanhamento(String uuid) {
        Map<String, WebReportDTO> relatorios = sistemaService.getReportService().getRelatorios(getUsuarioCorrente());
        for (String s : relatorios.keySet()) {
            if (s.equals(uuid)) {
                this.webReportDTO = relatorios.get(s);
                if (this.webReportDTO != null) {
                    if (this.webReportDTO.isConcluido()) {
                        this.webReportDTO.setVisualizado(Boolean.TRUE);
                    } else {
                        this.webReportDTO.setFim(System.currentTimeMillis());
                    }
                }
            }
        }
    }

    public void marcarComoVisualizado(String uuid) {
        Map<String, WebReportDTO> relatorios = sistemaService.getReportService().getRelatorios(getUsuarioCorrente());
        for (String s : relatorios.keySet()) {
            if (s.equals(uuid)) {
                if (relatorios.get(s).isConcluido()) {
                    relatorios.get(s).setVisualizado(Boolean.TRUE);
                }
            }
        }
    }

    public WebReportDTO getWebReportDTO() {
        return webReportDTO;
    }

    public void setWebReportDTO(WebReportDTO webReportDTO) {
        this.webReportDTO = webReportDTO;
    }

    public void difinirUltimaVersaoAoUsuario() {
        usuarioSistemaFacade.atualizarVersaoUsuario(getUsuarioCorrente(), getAppVersion());
        getUsuarioCorrente().setUltimaVersao(getAppVersion());
        setExibirVersaoDialog(false);
    }

    public String getNotasVersao() {
        return notasVersao;
    }

    public void setNotasVersao(String notasVersao) {
        this.notasVersao = notasVersao;
    }

    public boolean getExibirVersaoDialog() {
        if (exibirVersaoDialog == null) {
            exibirVersaoDialog = getUsuarioCorrente().getExibirVersao() && !getUsuarioCorrente().getUltimaVersao().equals(appVersion);
        }
        return exibirVersaoDialog;
    }

    public void setExibirVersaoDialog(boolean exibirVersaoDialog) {
        this.exibirVersaoDialog = exibirVersaoDialog;
    }

    public void verVersao() {
        try {
            LinkedHashMap versaoChamados = ((SingletonRecursosSistema) Util.getSpringBeanPeloNome("singletonRecursosSistema"))
                .getVersaoNoChamados(getAppVersion());
            if (versaoChamados != null) {
                Date liberacao =
                    new SimpleDateFormat("yyyy-MM-dd").parse(((String) versaoChamados.get("liberacao")));
                setNotasVersao((String) versaoChamados.get("notas"));
                setLiberacaoVersao(DataUtil.getDataFormatada(liberacao));
                difinirUltimaVersaoAoUsuario();
            }
        } catch (Exception e) {
            logger.error("erro ao procesar", e);
        }
    }

    public String getUrlNotificacaoService() {
        return Strings.isNullOrEmpty(System.getenv(URL_NOTIFICACAO_SERVICE)) ? DEFAULT_NOTIFICACAO_SERVICE : System.getenv(URL_NOTIFICACAO_SERVICE);
    }

    public String getLiberacaoVersao() {
        return liberacaoVersao;
    }

    public void setLiberacaoVersao(String liberacaoVersao) {
        this.liberacaoVersao = liberacaoVersao;
    }

    public int getTempoAntesDoAvisoEncerrarSessaoMinutos() {
        return VerificadorSessoesAtivasUsuario.getTempoAntesDoAvisoEncerrarSessaoMinutos(getUsuarioCorrente());
    }

    public int getTempoAntesDeEncerrarSessaoMinutos() {
        return getUsuarioCorrente().getTempoMaximoInativoMinutos() - getTempoAntesDoAvisoEncerrarSessaoMinutos();
    }

    public int getTempoMaximoInativoPadrao() {
        return UsuarioSistema.TEMPO_MAXIMO_INATIVO_MINUTOS;
    }
}
