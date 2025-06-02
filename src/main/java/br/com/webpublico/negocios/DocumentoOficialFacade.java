package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.ControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ParametroLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.TipoNumeroDoctoOficial;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.ProcessoLicenciamentoAmbientalFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.CalculadorAcrescimos;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonNumeroDoctoOficial;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.HtmlPdfDTO;
import br.com.webpublico.ws.model.WSSolicitacaoDocumentoOficial;
import br.com.webpublico.ws.model.WsAutenticaCertidao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.hibernate.Hibernate;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class DocumentoOficialFacade extends AbstractFacade<DocumentoOficial> {

    private static final Logger logger = Logger.getLogger(DocumentoOficialFacade.class);
    private final SimpleDateFormat formatterData = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat dfUFM = new DecimalFormat("###,###,##0.0000");
    private final DecimalFormat dfReais = new DecimalFormat("###,###,##0.00");
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private final String urlApiQRCode = "http://api.qrserver.com/v1/create-qr-code/?data=";
    private final String complementoURL = "autenticidade-de-documentos/";
    private final String sizeQRCode = "75x75";
    private final String caminhoOtt = "ott";
    private final String caminhoCondutorOtt = "condutor-ott";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private CalculoDoctoOficialFacade calculoDoctoOficialFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroCobrancaAdministrativaFacade parametroCobrancaAdministrativaFacade;
    private List<PropriedadeRural> propriedadesRurais;
    private Boolean documentoValido = true;
    private SituacaoCadastroEconomico situacaoCadastroEconomico;
    private List<Propriedade> propriedades;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PeticaoFacade peticaoFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private TextoFixoFacade textoFixoFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private AutoInfracaoFiscalFacade autoInfracaoFiscalFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ParametroRendasPatrimoniaisFacade parametroRendasPatrimoniaisFacade;
    @EJB
    private FiscalizacaoRBTransFacade fiscalizacaoRBTransFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private SubvencaoParametroFacade subvencaoParametroFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private CalculoTaxasDiversasFacade calculoTaxasDiversasFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoAssinaturaFacade configuracaoAssinaturaFacade;
    @EJB
    private AvaliacaoSolicitacaoDeCompraFacade avaliacaoSolicitacaoDeCompraFacade;
    @EJB
    private ParametroInformacaoRBTransFacade parametroInformacaoRBTransFacade;
    @EJB
    private ProcessoLicenciamentoAmbientalFacade processoLicenciamentoAmbientalFacade;
    @EJB
    private VeiculoOperadoraTecnologiaTransporteFacade veiculoOperadoraTecnologiaTransporteFacade;

    private SingletonNumeroDoctoOficial singletonNumeroDoctoOficial;

    public DocumentoOficialFacade() {
        super(DocumentoOficial.class);
    }

    @PostConstruct
    public void init() {
        singletonNumeroDoctoOficial = Util.recuperarSpringBean(SingletonNumeroDoctoOficial.class);
    }

    public static void atualizaComponentesDoDialog() {
        FacesUtil.atualizarComponente("FormularioDocumentoOficial");
        FacesUtil.executaJavaScript("mostraDocumentoOficial()");
        FacesUtil.executaJavaScript("ajustaImpressaoDocumentoOficial()");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public CalculoDoctoOficialFacade getCalculoDoctoOficialFacade() {
        return calculoDoctoOficialFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public FiscalizacaoRBTransFacade getFiscalizacaoRBTransFacade() {
        return fiscalizacaoRBTransFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    @Override
    public DocumentoOficial recuperar(Object id) {
        DocumentoOficial documentoOficial = em.find(DocumentoOficial.class, id);
        return documentoOficial;
    }

    public DocumentoOficial salvarRetornando(DocumentoOficial documentoOficial) {
        return em.merge(documentoOficial);
    }

    public void removerExcluindoExecucaoOrc(DocumentoOficial entity) {
        List<ExecucaoOrcDocumentoOficial> execucoes = buscarExecucoesOrcsDoDocumentoOficial(entity);
        if (execucoes != null && !execucoes.isEmpty()) {
            for (ExecucaoOrcDocumentoOficial execucao : execucoes) {
                em.remove(execucao);
            }
        }
        super.remover(entity);
    }

    public void removerExcluindoSolicitacaoMaterialDocumentoOficial(SolicitacaoMaterialDocumentoOficial smdo) {
        em.remove(recuperar(SolicitacaoMaterialDocumentoOficial.class, smdo.getId()));
        super.remover(smdo.getDocumentoOficial());
    }

    public List<ExecucaoOrcDocumentoOficial> buscarExecucoesOrcsDoDocumentoOficial(DocumentoOficial documentoOficial) {
        String sql = "select * from execorcdocumentooficial where documentooficial_id = :idDocOficial ";
        Query q = em.createNativeQuery(sql, ExecucaoOrcDocumentoOficial.class);
        q.setParameter("idDocOficial", documentoOficial.getId());
        return q.getResultList();
    }

    public String criptografa(String pUsuario, String pSenha, String rChavePublica) {
        try {
            String sEncrypted = "";
            Integer contador;
            String chave = "";
            String usuario;
            String senha;
            String chavePublica;

            if ("".equals(rChavePublica)) {
                int dia = Calendar.DAY_OF_MONTH + 1;
                int hora = Calendar.HOUR_OF_DAY + 1;
                int minuto = Calendar.MINUTE + 1;
                int segundo = Calendar.SECOND + 1;
                chavePublica = String.valueOf(dia * hora * minuto * segundo).trim();
            } else {
                chavePublica = rChavePublica;
            }

            usuario = (pUsuario + space(10)).substring(0, 10);
            senha = (pSenha + space(10)).substring(0, 10);

            String chaveInterna = "ABACO";

            for (contador = usuario.length() - 1; contador >= 0; contador--) {
                chave = chave + usuario.charAt(contador) + (int) usuario.charAt(contador);

                if (contador < chavePublica.length()) {
                    chave = chave + chavePublica.charAt(contador);
                }

                if (contador < chaveInterna.length()) {
                    chave = chave + chaveInterna.charAt(contador);
                }
            }
            Endecrypt crypt = new Endecrypt();
            sEncrypted = crypt.get3DESEncrypt(senha, chave);
            return sEncrypted;
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        }
        return "";
    }

//    public boolean autenticaCertidao(){
//
//    }

    public String space(int valor) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public void descriptografa(String pUsuario, String pSenhaCripto, String pChavePublica, String rSenha, String chaveInterna) {
        String sEncrypted = null;
        Integer contador;
        String chave = null;
        String usuario;
        //String senha;
        //String senhaCripto;
        String chavePublica;
        usuario = (pUsuario + space(10)).substring(1, 10);
        //senhaCripto = pSenhaCripto;
        chavePublica = pChavePublica;
        if (chaveInterna.isEmpty()) {
            chaveInterna = "ABACO";
        }
        if (chaveInterna.equals("OCABA")) {
            chaveInterna = "";
        }
        for (contador = usuario.length(); contador > 1; contador--) {
            chave = chave + usuario.substring(contador, contador + 1) + Integer.parseInt(usuario.substring(contador, contador + 1));

            if (contador <= chavePublica.length()) {
                chave = chave + chavePublica.substring(contador, contador + 1);
            }

            if (contador <= chaveInterna.length()) {
                chave = chave + chaveInterna.substring(contador, contador + 1);
            }
        }
        rSenha = sEncrypted;
    }

    public void tagsDocumento(VelocityContext context, DocumentoOficial documentoOficial) {
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.NUMERO_DOCUMENTO.name(), documentoOficial.getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.ANO_DOCUMENTO.name(), documentoOficial.getExercicio());
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.DATA_EMISSAO.name(), nomeDoCampo(documentoOficial, "dataEmissao", TipoFormato.DATA_HORA_SEGUNDOS));
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.DATA_VALIDADE.name(), nomeDoCampo(documentoOficial, "validade", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.DIAS_VALIDADE.name(), documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getValidadeDocto());


        String codigo = GeradorChaveAutenticacao.formataChaveDeAutenticacao(documentoOficial.getCodigoVerificacao());
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.CODIGO_VERIFICACAO.name(), codigo);
    }

    public TipoCadastroTributario recuperaTipoCadastroTributario(TipoCadastroDoctoOficial tipo) {
        if (tipo == null) {
            return null;
        }
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTROECONOMICO)) {
            return TipoCadastroTributario.ECONOMICO;
        }
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO)) {
            return TipoCadastroTributario.IMOBILIARIO;
        }
        if (tipo.equals(TipoCadastroDoctoOficial.CADASTRORURAL)) {
            return TipoCadastroTributario.RURAL;
        }
        if (tipo.equals(TipoCadastroDoctoOficial.PESSOAFISICA)) {
            return TipoCadastroTributario.PESSOA;
        }
        if (tipo.equals(TipoCadastroDoctoOficial.PESSOAJURIDICA)) {
            return TipoCadastroTributario.PESSOA;
        }
        return null;
    }

    public void tagsTextoFixo(VelocityContext context, DocumentoOficial documentoOficial, String conteudo) {
        TipoCadastroTributario tipoCadastroDoctoOficial = recuperaTipoCadastroTributario(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        List<TextoFixo> textoFixos = textoFixoFacade.buscarTextoFixoDoTipoDeCadastro(tipoCadastroDoctoOficial);
        for (TextoFixo textoFixo : textoFixos) {
            if (conteudo.contains(textoFixo.getTag())) {
                context.put(textoFixo.getTag().substring(1, textoFixo.getTag().length()), textoFixo.getTextoFixo());
            }
        }
    }

    public void tagsConfiguracaoDocumento(VelocityContext context) {
        String quebra = "<p style=\"page-break-before: always\"><br/></p>";
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.QUEBRA_PAGINA.name(), quebra);
    }

    public void tagsIP(VelocityContext context, DocumentoOficial documentoOficial) {
        addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.IP.name(), documentoOficial.getIpMaquina());
    }

    public void tagsUsuario(VelocityContext context, DocumentoOficial documentoOficial) {
        if (documentoOficial.getUsuarioSistema() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.USUARIO.name(), documentoOficial.getUsuarioSistema().getLogin());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsConfiguracaoDocumento.USUARIO.name(), SistemaFacade.obtemLogin());
        }
    }

    public void tagsDataHoje(VelocityContext context) {
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO.name(), DataUtil.getAno(new Date()));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES.name(), StringUtil.preencheString(DataUtil.getMes(new Date()) + "", 2, '0'));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA.name(), StringUtil.preencheString(DataUtil.getDia(new Date()) + "", 2, '0'));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA_EXTENSO.name(), DataUtil.diaExtenso(new SimpleDateFormat("dd").format(new Date())));
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES_EXTENSO.name(), Mes.getMesToInt(DataUtil.getMes(new Date())).getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO_EXTENSO.name(), DataUtil.diaExtenso(new SimpleDateFormat("yyyy").format(new Date())));
    }

    public void tagsAtributos(VelocityContext context, SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        for (ValorAtributoSolicitacao atributo : solicitacaoDoctoOficial.getValoresAtributosSolicitacao()) {
            addTag(context, atributo.getAtributoDoctoOficial().getTag(), atributo.getValor());
        }
    }

    public void tagsGeraisSolicitacao(VelocityContext context, SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        if (solicitacaoDoctoOficial.getFinalidade() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.FINALIDADE.name(), solicitacaoDoctoOficial.getFinalidade().getDescricao());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.FINALIDADE.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCIDO.name(), nomeDoCampo(solicitacaoDoctoOficial, "valorNormalVencido", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCER.name(), nomeDoCampo(solicitacaoDoctoOficial, "valorNormalVencer", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCIDO.name(), nomeDoCampo(solicitacaoDoctoOficial, "valorParcelamentoVencido", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCER.name(), nomeDoCampo(solicitacaoDoctoOficial, "valorParcelamentoVencer", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCIDOACRESCIMOS.name(), nomeDoCampo(solicitacaoDoctoOficial, "getValorNormalVencidoAcres", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCIDOACRESCIMOS.name(), nomeDoCampo(solicitacaoDoctoOficial, "valorParcelamentoVencidoAcres", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCIDOPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorNormalVencido() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorNormalVencido()));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCERPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorNormalVencer() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorNormalVencer()));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCIDOPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorParcelamentoVencido() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorParcelamentoVencido()));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCERPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorParcelamentoVencer() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorParcelamentoVencer()));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORNORMALVENCIDOACRESCIMOSPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorNormalVencidoAcres() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorNormalVencidoAcres()));
        addTag(context, TipoModeloDoctoOficial.TagsGerais.VALORPARCELAMENTOVENCIDOACRESCIMOSPOREXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoDoctoOficial.getValorParcelamentoVencidoAcres() == null ? BigDecimal.ZERO : solicitacaoDoctoOficial.getValorParcelamentoVencidoAcres()));

        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(),
                !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                    gerarQRCode("", solicitacaoDoctoOficial.getId(), configuracaoTributario) : "");
        }
        if (FacesContext.getCurrentInstance() != null && solicitacaoDoctoOficial.getUsuarioSistema() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.ASSINATURA_RESPONSAVEL.name(), "<p style=\"text-align: center;\"><span style=\"font-family: arial, helvetica, sans-serif;\">_________________________________________________</span><br /><strong><em><span style=\"font-family: arial, helvetica, sans-serif;\">Servidor Respons&aacute;vel<br /></span></em></strong><span style=\"font-family: arial, helvetica, sans-serif; font-size: small;\">SECRETARIA MUNICIPAL DE FINAN&Ccedil;AS</span></p>");
            addTag(context, TipoModeloDoctoOficial.TagsGerais.NOME_RESPONSAVEL.name(), solicitacaoDoctoOficial.getUsuarioSistema().getNome());
            MatriculaFP matriculaFP = matriculaFPFacade.matriculaFPVigentePorPessoa(solicitacaoDoctoOficial.getUsuarioSistema().getPessoaFisica());
            addTag(context, TipoModeloDoctoOficial.TagsGerais.MATRICULA_RESPONSAVEL.name(), matriculaFP != null ? matriculaFP.getMatricula() : "");
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.ASSINATURA_RESPONSAVEL.name(), " Documento emitido pelo Portal do Contribuinte ");
            addTag(context, TipoModeloDoctoOficial.TagsGerais.MATRICULA_RESPONSAVEL.name(), "");
        }

        if (solicitacaoDoctoOficial.getPessoa() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCertidaoBensImoveis.LISTA_IMOVEIS.name(), montarTabelaImoveisDaPessoa(solicitacaoDoctoOficial.getPessoa()));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsCertidaoBensImoveis.LISTA_IMOVEIS.name(), "");
        }

        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.OBSERVACAO.name(), solicitacaoDoctoOficial.getObservacao());
        tagsAtributos(context, solicitacaoDoctoOficial);
    }

    public void tagsGeraisFiscalizacao(VelocityContext context, AcaoFiscal acaoFiscal,
                                       ParametroFiscalizacao parametroFiscalizacao,
                                       AutoInfracaoFiscal autoInfracaoFiscal) throws UFMException {
        Calendar cal = Calendar.getInstance();
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA.name(), Util.formatterDiaMesAno.format(cal.getTime()));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DIA_ATUAL.name(), cal.get(Calendar.DAY_OF_MONTH));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.MES_ATUAL.name(), Mes.getMesToInt(cal.get(Calendar.MONTH) + 1).getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ANO_ATUAL.name(), cal.get(Calendar.YEAR));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NOME_DIRETOR_DEPTO.name(), nomeDoCampo(parametroFiscalizacao, "diretor.nome", null));
        ParametroFiscalizacao param = parametroFiscalizacaoFacade.recuperarParametroFiscalizacao(sistemaFacade.getExercicioCorrente());
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_DIAS_PROGRAMACAO.name(), nomeDoCampo(param, "prazoFiscalizacao", null));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NOME_SECRETARIO.name(), nomeDoCampo(parametroFiscalizacao, "nomeSecretario", null));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_DIAS_APRESENTACAO_DOCTO.name(), nomeDoCampo(param, "prazoApresentacaoDocto", null));

        if (acaoFiscal != null) {
            acaoFiscal = acaoFiscalFacade.recuperar(acaoFiscal.getId());

            if (acaoFiscal.getOrdemServico() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ORDEM_SERVICO.name(), StringUtil.preencheString(acaoFiscal.getOrdemServico().toString(), 5, '0') + "/" + acaoFiscal.getAno());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ORDEM_SERVICO.name(), "");
            }

            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_INICIAL_FISCAL.name(), nomeDoCampo(acaoFiscal, "dataInicial", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_FINAL_FISCAL.name(), nomeDoCampo(acaoFiscal, "dataFinal", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_INICIAL_FISCALIZACAO.name(), nomeDoCampo(acaoFiscal, "dataLevantamentoInicial", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_FINAL_FISCALIZACAO.name(), nomeDoCampo(acaoFiscal, "dataLevantamentoFinal", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.PROGRAMACAO_FISCAL.name(), nomeDoCampo(acaoFiscal, "programacaoFiscal.numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.TEXTO_CONCLUSAO.name(), nomeDoCampo(acaoFiscal, "conclusao", null));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_PROGRAMACAO.name(), nomeDoCampo(acaoFiscal, "programacaoFiscal.dataCriacao", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_CNAE.name(), montaListaCnae(acaoFiscal));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_TERMO_FISCALIZACAO.name(), nomeDoCampo(acaoFiscal, "dataTermoFiscalizacao", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_TERMO_FISCALIZACAO.name(), nomeDoCampo(acaoFiscal, "numeroTermoFiscalizacao", null));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.AGENTES_FISCAIS.name(), montarTabelaAgentesFiscaisPorAcaoFiscalAndMatricula(acaoFiscal, false));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.AGENTES_FISCAIS_MATRICULA.name(), montarTabelaAgentesFiscaisPorAcaoFiscalAndMatricula(acaoFiscal, true));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_LEVANTAMENTO.name(), formataNumeroLevantamento(acaoFiscal));
            if (!acaoFiscal.getFiscalDesignados().isEmpty()) {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS.name(), montaAssinaturaFiscais(acaoFiscal, false));
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS_MATRICULA.name(), montaAssinaturaFiscais(acaoFiscal, true));
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS_MATRICULA.name(), "");
            }

            BigDecimal totalIssqCorrigido = BigDecimal.ZERO;
            BigDecimal totalPenalidades = BigDecimal.ZERO;
            BigDecimal totalJuros = BigDecimal.ZERO;
            BigDecimal totalMulta = BigDecimal.ZERO;

            for (RegistroLancamentoContabil registro : acaoFiscal.getLancamentosContabeis()) {
                if (!registro.getSituacao().equals(RegistroLancamentoContabil.Situacao.ESTORNADO)
                    && !registro.getSituacao().equals(RegistroLancamentoContabil.Situacao.CANCELADO)) {
                    for (LancamentoContabilFiscal lancamento : registro.getLancamentosContabeis()) {
                        totalIssqCorrigido = totalIssqCorrigido.add(lancamento.getValorCorrigido());
                        totalMulta = totalMulta.add(lancamento.getMulta());
                        totalJuros = totalJuros.add(lancamento.getJuros());
                    }
                    for (LancamentoMultaFiscal lancamentoMultaFiscal : registro.getMultas()) {
                        totalPenalidades = totalPenalidades.add(lancamentoMultaFiscal.getValorMulta());
                    }
                }
            }
            BigDecimal totalGeral = totalIssqCorrigido.add(totalMulta.add(totalJuros.add(totalPenalidades)));

            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_GERAL.name(), decimalFormat.format(totalGeral));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_ISSQN_CORRIGIDO.name(), decimalFormat.format(totalIssqCorrigido));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_JUROS_MORA.name(), decimalFormat.format(totalJuros));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_MULTA_MORA.name(), decimalFormat.format(totalMulta));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_PENALIDADES.name(), decimalFormat.format(totalPenalidades));

        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ORDEM_SERVICO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_INICIAL_FISCAL.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_FINAL_FISCAL.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_INICIAL_FISCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_FINAL_FISCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.PROGRAMACAO_FISCAL.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.TEXTO_CONCLUSAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_PROGRAMACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_CNAE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.DATA_TERMO_FISCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_TERMO_FISCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.AGENTES_FISCAIS.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.AGENTES_FISCAIS_MATRICULA.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_LEVANTAMENTO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.ASSINATURAS_FISCAIS_MATRICULA.name(), "");

            BigDecimal totalPenalidades = BigDecimal.ZERO;

            for (LancamentoMultaFiscal lancamentoMultaFiscal : autoInfracaoFiscal.getRegistro().getMultas()) {
                totalPenalidades = totalPenalidades.add(lancamentoMultaFiscal.getValorMulta());
            }

            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_GERAL.name(), decimalFormat.format(totalPenalidades));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_ISSQN_CORRIGIDO.name(), BigDecimal.ZERO);
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_JUROS_MORA.name(), BigDecimal.ZERO);
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_MULTA_MORA.name(), BigDecimal.ZERO);

            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_TOTAL_PENALIDADES.name(), decimalFormat.format(totalPenalidades));
        }
    }

    public void tagsAutoInfracao(VelocityContext context, AutoInfracaoFiscal auto, ParametroFiscalizacao parametroFiscalizacao) throws UFMException {
        if (auto.getId() != null) {
            auto = autoInfracaoFiscalFacade.recuperar(auto.getId());
        }

        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.FUNDAMENTACAO.name(), nomeDoCampo(auto, "fundamentacao", null));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.HISTORICOFISCAL.name(), nomeDoCampo(auto, "historicoFiscal", null));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.NUMERO_AUTOINFRACAO.name(), StringUtil.preencheString(auto.getNumero().toString(), 5, '0') + "/" + auto.getAno());
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_AUTO_UFM.name(), nomeDoCampo(auto, "valorAutoInfracao", TipoFormato.VALOR_UFM));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.VALOR_AUTO_REAIS.name(), nomeDoCampo(auto, "valorAutoInfracao", TipoFormato.VALOR_UFM));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_LANCAMENTOS.name(), montaTabelaLancamentosContabeisFiscal(auto.getRegistro()));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_LEVANTAMENTO_CREDITO_TRIBUTARIO.name(), montaTabelaLevantamentoCreditoTributario(auto.getRegistro()));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_MULTAS.name(), montaTabelaLancamentosMultasFiscal(auto.getRegistro()));
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacao.LISTA_EMBASAMENTO_MULTAS.name(), montaTabelaEmbasamentosMultasFiscal(auto.getRegistro()));
    }

    public void tagsCertidaoDividaAtiva(VelocityContext context, CertidaoDividaAtiva certidaoDividaAtiva) throws UFMException {
        ValoresAtualizadosCDA valoresAtualizadosCDA = certidaoDividaAtivaFacade.valorAtualizadoDaCertidao(certidaoDividaAtiva);
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        BigDecimal valorTotalCertidao = valoresAtualizadosCDA.getValorTotal();
        try {
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.ASSINATURA.name(), nomeDoCampo(certidaoDividaAtiva, "funcionarioAssinante.nome", null));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.DATA_HOJE.name(), new SimpleDateFormat("EEEE, d' de 'MMMM' de 'yyyy", new Locale("pt", "BR")).format(new Date()));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.EXERCICIO.name(), nomeDoCampo(certidaoDividaAtiva, "exercicio.ano", null));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.DATA_CDA.name(), nomeDoCampo(certidaoDividaAtiva, "dataCertidao", TipoFormato.DATA));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.NUMERO.name(), nomeDoCampo(certidaoDividaAtiva, "numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.TABELA_DE_PARCELA.name(), montarTabelaDividaAtiva(valoresAtualizadosCDA.getComposicao()));
            Divida dividaCertidao = null;
            if (!valoresAtualizadosCDA.getComposicao().isEmpty()) {
                dividaCertidao = dividaFacade.recuperar(valoresAtualizadosCDA.getComposicao().get(0).getResultadoParcela().getIdDivida());
            }

            List<ResultadoParcela> parcelasCertidao = certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(certidaoDividaAtiva);
            Divida dividaAutoInfracaoIss = configuracaoTributario.getDividaAutoInfracaoISS();
            Divida dividaMultaFiscalizacao = configuracaoTributario.getDividaMultaFiscalizacao();
            List<AutoInfracaoFiscal> autosDasParcelas = Lists.newArrayList();
            List<ResultadoParcela> parcelasFiltradasPorDivida = parcelasCertidao.stream().filter(
                rp -> {
                    boolean dividaAtivaAutoInfracaoIgualDividaParcela = dividaAutoInfracaoIss != null && dividaAutoInfracaoIss.getDivida() != null && rp.getIdDivida().equals(dividaAutoInfracaoIss.getDivida().getId());
                    boolean dividaAtivaMultaFiscalizacaoIgualDividaParcela = dividaMultaFiscalizacao != null && dividaMultaFiscalizacao.getDivida() != null && rp.getIdDivida().equals(dividaMultaFiscalizacao.getDivida().getId());
                    return dividaAtivaAutoInfracaoIgualDividaParcela || dividaAtivaMultaFiscalizacaoIgualDividaParcela;
                }).collect(Collectors.toList());

            for (ResultadoParcela rp : parcelasFiltradasPorDivida) {
                AutoInfracaoFiscal auto = autoInfracaoFiscalFacade.recuperarAutoInfracaoDaParcelaInscritaEmDA(rp.getIdParcela());
                if (auto != null) autosDasParcelas.add(auto);
            }

            if (!autosDasParcelas.isEmpty()) {
                StringBuilder numerosAutosConcatenados = new StringBuilder();
                StringBuilder numeroProcessoAdmConcatenados = new StringBuilder();
                for (AutoInfracaoFiscal auto : autosDasParcelas) {
                    String numeroAno = StringUtil.preencheString(auto.getNumero().toString(), 5, '0') + "/" + auto.getAno();
                    if (!numerosAutosConcatenados.toString().contains(numeroAno)) {
                        numerosAutosConcatenados.append(numeroAno);
                        numerosAutosConcatenados.append(", ");
                    }

                    if (auto.getRegistro() != null && auto.getRegistro().getNumeroProcessoAdministrativo() != null) {
                        String numeroProcessoAdm = auto.getRegistro().getNumeroProcessoAdministrativo();
                        if (!numeroProcessoAdmConcatenados.toString().contains(numeroProcessoAdm)) {
                            numeroProcessoAdmConcatenados.append(numeroProcessoAdm);
                            numeroProcessoAdmConcatenados.append(", ");
                        }
                    }
                }
                if(!numerosAutosConcatenados.toString().isEmpty()) {
                    numerosAutosConcatenados.deleteCharAt(numerosAutosConcatenados.length() - 2);
                }
                if(!numeroProcessoAdmConcatenados.toString().isEmpty()) {
                    numeroProcessoAdmConcatenados.deleteCharAt(numeroProcessoAdmConcatenados.length() - 2);
                }
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.NUMERO_AUTO_INFRACAO.name(), numerosAutosConcatenados.toString());
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.PROTOCOLO_ACAO_FISCAL.name(), numeroProcessoAdmConcatenados.toString());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.NUMERO_AUTO_INFRACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.PROTOCOLO_ACAO_FISCAL.name(), "");
            }

            if (dividaCertidao != null) {
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.NUMERO_LIVRO.name(), dividaCertidao.getNrLivroDividaAtiva());
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.ORIGEM_DIVIDA.name(), dividaCertidao.getDescricao());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.NUMERO_LIVRO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.ORIGEM_DIVIDA.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.VALOR_TOTAL.name(), Util.formataValor(valorTotalCertidao));
            addTag(context, TipoModeloDoctoOficial.TagsDividaAtiva.VALOR_EXTENSO.name(), ValorPorExtenso.valorPorExtenso(valorTotalCertidao));
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro ao trocar tags da certidão de divida ativa", e);
        }
        Cadastro cadastro = certidaoDividaAtiva.getCadastro();
        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                try {
                    Query q = em.createQuery(" from CadastroImobiliario c where c = :chave").setParameter("chave", cadastro);
                    CadastroImobiliario imobiliario = (CadastroImobiliario) q.getSingleResult();
                    Lote lote = imobiliario.getLote();

                    List logradouro = loteFacade.recuperaAtributoPrincipaldoLote("logradouro", lote);
                    if (logradouro != null && !logradouro.isEmpty()) {
                        Logradouro logradouroPrincipal = (Logradouro) logradouro.get(0);
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO_PRINCIPAL.name(), nomeDoCampo(logradouroPrincipal, "nome", null));
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO.name(), logradouroPrincipal.getNome());
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO_PRINCIPAL.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO.name(), "");
                    }
                    List bairro = loteFacade.recuperaAtributoPrincipaldoLote("bairro", lote);
                    if (bairro != null && !bairro.isEmpty()) {
                        Bairro bairroPrincial = (Bairro) bairro.get(0);
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.BAIRRO.name(), bairroPrincial.getDescricao());
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.BAIRRO.name(), "");
                    }
                    List testada = loteFacade.recuperaAtributoPrincipaldoLote("testada", lote);
                    if (testada != null && !testada.isEmpty()) {
                        Testada testadaPrincipal = (Testada) testada.get(0);
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.TESTADA_PRINCIPAL.name(), nomeDoCampo(testadaPrincipal, "tamanho", null));
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.TESTADA_PRINCIPAL.name(), "");
                    }
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.AREA_CONSTRUIDA.name(), imobiliario.getAreaTotalConstruida());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.AREA_DO_TERRENO.name(), imobiliario.getLote().getAreaLote());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CADASTRO_IMOBILIARIO.name(), imobiliario.getInscricaoCadastral());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.NUMERO_LOGRADOURO.name(), imobiliario.getNumero());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CIDADE.name(), configuracaoTributario.getCidade().getNome());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.UF.name(), configuracaoTributario.getCidade().getUf());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOTE.name(), imobiliario.getLote().getCodigoLote());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.VALOR_VENAL.name(), Util.formataValor(imobiliario.getValorVenal()));
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.QUADRA.name(), imobiliario.getLote().getQuadra().getDescricao());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.PROPRIETARIO.name(), imobiliario.getPropriedadeVigente().get(0).getPessoa().getNome());
                } catch (ExcecaoNegocioGenerica e) {
                    e.printStackTrace();
                }
            } else if (cadastro instanceof CadastroEconomico) {
                try {
                    Query q = em.createQuery(" from CadastroEconomico c where c = :chave").setParameter("chave", cadastro);
                    CadastroEconomico economico = (CadastroEconomico) q.getSingleResult();
                    if (economico != null) {
                        Hibernate.initialize(economico.getEconomicoCNAE());
                        if (economico.getCadastroImobiliario() != null) {
                            Lote lote = economico.getCadastroImobiliario().getLote();
                            Logradouro logradouro = (Logradouro) loteFacade.recuperaAtributoPorLote("logradouro", lote);
                            Bairro bairro = (Bairro) loteFacade.recuperaAtributoPorLote("bairro", lote);
                            Quadra quadra = (Quadra) loteFacade.recuperaAtributoPorLote("quadra", lote);

                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.BAIRRO_BCI.name(), nomeDoCampo(bairro, "descricao", null));
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NUMERO_BCI.name(), nomeDoCampo(economico.getCadastroImobiliario(), "numero", null));
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOTE_BCI.name(), nomeDoCampo(lote, "codigoLote", null));
                            if (economico.getClassificacaoAtividade() != null) {
                                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), economico.getClassificacaoAtividade().getDescricao());
                            } else {
                                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), "");
                            }
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.QUADRA_BCI.name(), nomeDoCampo(quadra, "descricao", null));
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CMC.name(), economico.getInscricaoCadastral());
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.COMPLEMENTO_BCI.name(), nomeDoCampo(lote, "complemento", null));
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.INSCRICAO_CADASTRAL_BCI.name(), economico.getCadastroImobiliario().getInscricaoCadastral());
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOGRADOURO_BCI.name(), nomeDoCampo(logradouro, "nome", null));
                            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.SITUACAO_CADASTRAL.name(), economico.getSituacaoAtual().getSituacaoCadastral().name());
                        }
                        String atividadeEconomica = "";
                        if (economico.getEconomicoCNAE() != null && !economico.getEconomicoCNAE().isEmpty()) {
                            if (economico.getEconomicoCNAEPrincipal() != null && economico.getEconomicoCNAEPrincipal().getCnae() != null) {
                                atividadeEconomica = economico.getEconomicoCNAEPrincipal().getCnae().getDescricaoDetalhada();
                            } else if (economico.getEconomicoCNAE().get(0) != null && economico.getEconomicoCNAE().get(0).getCnae() != null) {
                                atividadeEconomica = economico.getEconomicoCNAE().get(0).getCnae().getDescricaoDetalhada();
                            }
                        }
                        atividadeEconomica = (atividadeEconomica == null ? "" : atividadeEconomica);
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), atividadeEconomica);

                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                        addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.AREA_UTILIZACAO.name(), numberFormat.format(economico.getAreaUtilizacao()));
                    }
                } catch (ExcecaoNegocioGenerica e) {
                    e.printStackTrace();
                }
            } else if (cadastro instanceof CadastroRural) {
                try {
                    Query q = em.createQuery(" from CadastroRural c where c = :chave").setParameter("chave", cadastro);
                    CadastroRural rural = (CadastroRural) q.getSingleResult();
                    rural.getPropriedade().size();
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.AREA.name(), rural.getAreaLote());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.CADASTRO_RURAL.name(), rural.getNumeroCadastro());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.INCRA.name(), rural.getNumeroIncra());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.TIPO_AREA.name(), rural.getTipoAreaRural().getDescricao());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.VALOR_VENAL.name(), Util.formataValor(rural.getValorVenalLote()));
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.LOCALIZACAO.name(), rural.getLocalizacaoLote());
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.PROPRIETARIO.name(), rural.getPropriedadesVigenteNaData(sistemaFacade.getDataOperacao()).get(0).getPessoa().getNome());
                } catch (ExcecaoNegocioGenerica e) {
                    e.printStackTrace();
                }
            }
        } else {
            Pessoa pessoa = certidaoDividaAtiva.getPessoa();
            if (pessoa instanceof PessoaFisica) {
                try {
                    PessoaFisica pessoaFisica = pessoaFisicaFacade.recuperar(pessoa.getId());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.RG_INSC_ESTADUAL.name(), pessoaFisica.getRg());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CONTRIBUINTE.name(), pessoaFisica.getNome());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CPF.name(), pessoaFisica.getCpf_Cnpj());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.EMAIL.name(), pessoaFisica.getEmail());
                    List<EnderecoCorreio> retornaEnderecoCorreioDaPessoa = enderecoFacade.retornaEnderecoCorreioDaPessoa(pessoaFisica);
                    if (retornaEnderecoCorreioDaPessoa == null || retornaEnderecoCorreioDaPessoa.isEmpty()) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.BAIRRO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CIDADE.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.UF.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.LOGRADOURO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.COMPLEMENTO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), "");
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.BAIRRO.name(), retornaEnderecoCorreioDaPessoa.get(0).getBairro());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CIDADE.name(), retornaEnderecoCorreioDaPessoa.get(0).getLocalidade());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.UF.name(), retornaEnderecoCorreioDaPessoa.get(0).getUf());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.LOGRADOURO.name(), retornaEnderecoCorreioDaPessoa.get(0).getLogradouro());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.COMPLEMENTO.name(), retornaEnderecoCorreioDaPessoa.get(0).getComplemento());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), retornaEnderecoCorreioDaPessoa.get(0).getCep());
                    }
                    Query q = em.createQuery("select t from Telefone t where t.pessoa = :p");
                    q.setParameter("p", pessoa);
                    List<Telefone> resultList = q.getResultList();
                    if (resultList == null || resultList.isEmpty()) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.TELEFONE.name(), "");
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.TELEFONE.name(), resultList.get(0).getTelefone());
                    }
                } catch (ExcecaoNegocioGenerica e) {
                    e.printStackTrace();
                }
            }
            if (pessoa instanceof PessoaJuridica) {
                try {
                    PessoaJuridica pessoaJuridica = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(pessoa.getCpf_Cnpj());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CNPJ.name(), pessoaJuridica.getCnpj());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.NOMEFANTASIA.name(), pessoaJuridica.getNomeFantasia());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.RAZAO_SOCIAL.name(), pessoaJuridica.getRazaoSocial());
                    List<EnderecoCorreio> retornaEnderecoCorreioDaPessoa = enderecoFacade.retornaEnderecoCorreioDaPessoa(pessoaJuridica);
                    if (retornaEnderecoCorreioDaPessoa == null || retornaEnderecoCorreioDaPessoa.isEmpty()) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.BAIRRO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CIDADE.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.UF.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.LOGRADOURO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.COMPLEMENTO.name(), "");
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), "");
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.BAIRRO.name(), retornaEnderecoCorreioDaPessoa.get(0).getBairro());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CIDADE.name(), retornaEnderecoCorreioDaPessoa.get(0).getLocalidade());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.UF.name(), retornaEnderecoCorreioDaPessoa.get(0).getUf());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.LOGRADOURO.name(), retornaEnderecoCorreioDaPessoa.get(0).getLogradouro());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.COMPLEMENTO.name(), retornaEnderecoCorreioDaPessoa.get(0).getComplemento());
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), retornaEnderecoCorreioDaPessoa.get(0).getCep());
                    }
                    Query q = em.createQuery("select t from Telefone t where t.pessoa = :p");
                    q.setParameter("p", pessoaJuridica);
                    List<Telefone> resultList = q.getResultList();
                    if (resultList == null || resultList.isEmpty()) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.TELEFONE.name(), "");
                    } else {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.TELEFONE.name(), resultList.get(0).getTelefone());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void tagsCertidaoPeticaoDividaAtiva(VelocityContext context, Peticao peticao) throws UFMException {
        ParametrosPeticao param = peticaoFacade.recuperaUltimoParametro();
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.ASSINATURA.name(), nomeDoCampo(param, "funcionarioResponsavel.nome", null));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.DATA_HOJE.name(), new SimpleDateFormat("EEEE, d' de 'MMMM' de 'yyyy", new Locale("pt", "BR")).format(new Date()));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.EXERCICIO.name(), peticao.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.NUMERO.name(), nomeDoCampo(peticao, "codigo", null));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.TABELA_DE_CADASTRO.name(), montaTabelaDeCadastroDaPeticao(peticao));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.TABELA_DE_CDA.name(), montaTabelaDeCDA(peticao));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.TABELA_DE_COMPROMISSARIO.name(), montaTabelaDeCompromissario(peticao));
//        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.PROCURADOR_NOME.name(), recuperaNomeProcurador());
//        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.PROCURADOR_OAB.name(), recuperaOABProcurador());
        BigDecimal valorTotalPeticao = recuperaValorTotalPeticaoDividaAtiva(peticao);
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.VALOR_TOTAL_PETICAO.name(), Util.formataUFM(valorTotalPeticao));
        addTag(context, TipoModeloDoctoOficial.TagsPeticaoDividaAtiva.VALOR_TOTAL_PETICAO_EX.name(), ValorPorExtenso.valorPorExtenso(valorTotalPeticao));
    }

    public String recuperarEnderecoDeCorrespondencia(Pessoa pessoa) {
        if (pessoa == null) {
            return "";
        }
        String sql = "select ec.* " +
            "        from pessoa_enderecocorreio pe " +
            "  inner join pessoa p on p.id = pe.pessoa_id " +
            "  inner join enderecocorreio ec on ec.id = pe.enderecoscorreio_id " +
            "       where p.id = :pessoa_id " +
            "         and ec.tipoendereco = 'CORRESPONDENCIA' " +
            "         and ec.principal = 1 " +
            "       union " +
            "      select ec.* " +
            "        from pessoa_enderecocorreio pe " +
            "  inner join pessoa p on p.id = pe.pessoa_id " +
            "  inner join enderecocorreio ec on ec.id = pe.enderecoscorreio_id " +
            "       where p.id = :pessoa_id " +
            "         and ec.tipoendereco = 'CORRESPONDENCIA' " +
            "         and rownum < 2 " +
            "       union " +
            "      select ec.* " +
            "        from pessoa_enderecocorreio pe " +
            "  inner join pessoa p on p.id = pe.pessoa_id " +
            "  inner join enderecocorreio ec on ec.id = pe.enderecoscorreio_id " +
            "       where p.id = :pessoa_id " +
            "         and ec.principal = 1 " +
            "         and rownum < 2 " +
            "      union " +
            "      select ec.* " +
            "        from pessoa_enderecocorreio pe " +
            "  inner join pessoa p on p.id = pe.pessoa_id " +
            "  inner join enderecocorreio ec on ec.id = pe.enderecoscorreio_id " +
            "       where p.id = :pessoa_id " +
            "         and rownum < 2";

        List retorno = em.createNativeQuery(sql, EnderecoCorreio.class).setParameter("pessoa_id", pessoa.getId()).getResultList();
        EnderecoCorreio ec = null;

        if (!retorno.isEmpty()) {
            ec = (EnderecoCorreio) retorno.get(0);
        }

        if (ec != null) {
            return ec.toString();
        }

        return "Não existe endereço de correspondência para este contribuinte.";
    }

    public void tagsRBTrans(VelocityContext context, TermoRBTrans termo) {
        Calendar c = Calendar.getInstance();
        c.setTime(termo.getDataTermo());
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
        ParametrosInformacoesRBTrans parametroInformacoes = parametroInformacaoRBTransFacade.buscarParametroPeloExercicio(exercicio);
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(termo.getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getId());
        if (cadastroEconomico != null) {
            Pessoa permissionario = pessoaFacade.recuperar(cadastroEconomico.getPessoa().getId());

            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CNPJ_MUNICIPIO.name(), nomeDoCampo(parametroInformacoes, "cnpj", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.EMAIL.name(), nomeDoCampo(parametroInformacoes, "email", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.ENDERECO.name(), nomeDoCampo(parametroInformacoes, "endereco", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.TELEFONEFAX.name(), nomeDoCampo(parametroInformacoes, "telFax", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CEP.name(), nomeDoCampo(parametroInformacoes, "cep", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_SECRETARIA.name(), nomeDoCampo(parametroInformacoes, "secretaria", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_SECRETARIO.name(), nomeDoCampo(parametroInformacoes, "secretario", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DECRETO_NOMEACAO_SECRETARIO.name(), nomeDoCampo(parametroInformacoes, "decretoNomeacao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CARGO_SECRETARIO.name(), nomeDoCampo(parametroInformacoes, "cargoSecretario", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_RESPONSAVEL.name(), nomeDoCampo(parametroInformacoes, "responsavel", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CARGO_RESPONSAVEL.name(), nomeDoCampo(parametroInformacoes, "cargoResponsavel", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NUMERO_PERMISSAO.name(), nomeDoCampo(termo, "permissaoTransporte.numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_PERMISSIONARIO.name(), nomeDoCampo(termo.getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getPessoa(), "nome", null));
            if (!permissionario.getEnderecos().isEmpty()) {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_PERMISSIONARIO.name(), permissionario.getEnderecos().get(0).getLogradouro()
                    + ", " + permissionario.getEnderecos().get(0).getNumero());
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_PERMISSIONARIO.name(), permissionario.getEnderecos().get(0).getBairro());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_PERMISSIONARIO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_PERMISSIONARIO.name(), "");
            }

            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_POR_EXTENSO.name(), DataUtil.recuperarDataEscritaPorExtenso(sistemaFacade.getDataOperacao()));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_EMISSAO_TERMO.name(), DataUtil.getDataFormatada(termo.getDataTermo()));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_TERMO_ANO.name(), DataUtil.getAno(termo.getDataTermo()));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_TERMO_DIA_EXTENSO.name(), DataUtil.diaExtenso(new SimpleDateFormat("dd").format(termo.getDataTermo())));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_TERMO_MES_EXTENSO.name(), Mes.getMesToInt(DataUtil.getMes(termo.getDataTermo())).getDescricao());
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_TERMO_DIA.name(), new SimpleDateFormat("dd").format(termo.getDataTermo()));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CPF_PERMISSIONARIO.name(), termo.getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getPessoa().getCpf_Cnpj());
            if (permissionario instanceof PessoaFisica) {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.RG_PERMISSIONARIO.name(), nomeDoCampo(permissaoTransporteFacade.recuperarRg((PessoaFisica) termo.getPermissaoTransporte().getPermissionarioVigente().getCadastroEconomico().getPessoa()), "rg", null));
            }
            addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.OBSERVACAO.name(), permissionario.getObservacao());

            MotoristaAuxiliar motorista = permissaoTransporteFacade.recuperaMotorista(termo);
            try {

                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_MOTORISTA.name(), motorista.getCadastroEconomico().getPessoa().getNome());
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CPF_MOTORISTA.name(), motorista.getCadastroEconomico().getPessoa().getCpf_Cnpj());
            } catch (Exception ex) {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOME_MOTORISTA.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CPF_MOTORISTA.name(), "");
            }

            try {
                if (motorista.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
                    RG rg = permissaoTransporteFacade.recuperarRg((PessoaFisica) motorista.getCadastroEconomico().getPessoa());
                    addTag(context, TipoModeloDoctoOficial.TagsRBTrans.RG_MOTORISTA.name(), nomeDoCampo(rg, "numero", null));
                }
            } catch (Exception ex) {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.RG_MOTORISTA.name(), "");
            }

            try {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_MOTORISTA.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_MOTORISTA.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_MOTORISTA.name(), motorista.getCadastroEconomico().getPessoa().getEnderecos().get(0).getLogradouro()
                    + ", " + permissionario.getEnderecos().get(0).getNumero());
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_MOTORISTA.name(), motorista.getCadastroEconomico().getPessoa().getEnderecos().get(0).getBairro());
            } catch (Exception ex) {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_MOTORISTA.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_MOTORISTA.name(), "");
            }

            VeiculoPermissionario vp = permissaoTransporteFacade.retornaUltimoVeiculo(!TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_VEICULO.equals(termo.getTipoTermoRBTrans()), termo.getPermissaoTransporte());
            VeiculoTransporte vt = vp != null ? vp.getVeiculoTransporte() : null;
            if (vt == null) {
                VeiculoPermissionario veiculoPermissionario = permissaoTransporteFacade.retornaUltimoVeiculo(false, termo.getPermissaoTransporte());
                vt = veiculoPermissionario == null ? null : veiculoPermissionario.getVeiculoTransporte();
            }
            adicionaTagsVeiculoTransporteRBTrans(context, vt, false);

            if (termo.getTipoTermoRBTrans().equals(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_INSERCAO_VEICULO) ||
                termo.getTipoTermoRBTrans().equals(TipoTermoRBTrans.TERMO_AUTORIZA_BAIXA_VEICULO)) {
                VeiculoPermissionario veiculoPermissionario = permissaoTransporteFacade.retornaUltimoVeiculoBaixado(termo.getPermissaoTransporte());
                adicionaTagsVeiculoTransporteRBTrans(context,
                    veiculoPermissionario != null ? veiculoPermissionario.getVeiculoTransporte() : null, true);
            }

            if (parametroInformacoes != null && parametroInformacoes.getDetentorArquivoComposicao() != null && parametroInformacoes.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo() != null) {
                addTagAssinatura(context, TipoModeloDoctoOficial.TagsRBTrans.ASSINATURA_DO_SUPERINTENDENTE.name(), parametroInformacoes.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsRBTrans.ASSINATURA_DO_SUPERINTENDENTE.name(), "");
            }
        }
    }

    private void adicionaTagsVeiculoTransporteRBTrans(VelocityContext context, VeiculoTransporte veiculo, boolean baixado) {
        if (baixado) {
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_PLACA.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "placa", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_CHASSI.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "chassi", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_ANO_FABRICACAO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "anoFabricacao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_ANO_MODELO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "anoModelo", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_MARCA.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "modelo.marca", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_MODELO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "modelo", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_COR.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "cor.descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_ESPECIE.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "especie", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_TIPO_VEICULO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "tipoVeiculo.descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.VEICULO_BAIXADO_NOTA_FISCAL.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "notaFiscal", null));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.PLACA.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "placa", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CHASSI.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "chassi", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.ANO_FABRICACAO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "anoFabricacao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.ANO_MODELO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "anoModelo", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MARCA.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "modelo.marca", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MODELO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "modelo", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.COR.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "cor.descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.ESPECIE.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "especie", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.TIPO_VEICULO.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "tipoVeiculo.descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.NOTA_FISCAL.name(), veiculo == null ? "" : nomeDoCampo(veiculo, "notaFiscal", null));
        }
    }

    public void tagsTermoAdvertencia(VelocityContext context, ProcessoFiscalizacao processoFiscalizacao) {
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.DATA_CIENCIA.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "dataCiencia", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.DATA_LAVRATURA.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "dataLavratura", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.DESCRICAO_INFRACAO.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "descricaoDaInfracao", null));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.ENCERRAMENTO_TERMO.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "enceramentoTermoAdvertencia.descricao", null));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.NOME_PESSOA_CIENCIA.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "nomePessoaCiencia", null));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.NUMERO.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "numero", null));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.USUARIO.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "usuarioSistema.pessoaFisica.nome", null));
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.OBSERVACAO.name(), nomeDoCampo(processoFiscalizacao.getTermoAdvertencia(), "descricaoDaInfracao", null));
        if (processoFiscalizacao.getItensProcessoFiscalizacao() != null && !processoFiscalizacao.getItensProcessoFiscalizacao().isEmpty()) {
            addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.LISTA_INFRACAO.name(), montaListaInfracao(processoFiscalizacao));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsTermoDeAdvertencia.LISTA_INFRACAO.name(), " ");
        }
    }

    public void tagsAutoInfracaoFiscalizacao(VelocityContext context, ProcessoFiscalizacao processoFiscalizacao) {
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.BAIRRO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.ENDERECO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.NUMERO_ENDERECO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.PONTO_REFERENCIA.name(), "");

        if (processoFiscalizacao.getLocalOcorrencia() != null && processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro() != null) {
            if (processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getBairro() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.BAIRRO.name(), processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getBairro().getDescricao());
            }
            if (processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro() != null &&
                processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro().getTipoLogradouro() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.ENDERECO.name(), processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro().getTipoLogradouro().getDescricao() + " " +
                    processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro().getNome());
            } else if (processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.ENDERECO.name(), processoFiscalizacao.getLocalOcorrencia().getLogradouroBairro().getLogradouro().getNome());
            }
            addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.NUMERO_ENDERECO.name(), processoFiscalizacao.getLocalOcorrencia().getNumero());
            addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.PONTO_REFERENCIA.name(), processoFiscalizacao.getLocalOcorrencia().getPontoReferencia());
        }
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.DATA_CIENCIA.name(), formataData(processoFiscalizacao.getAutoInfracaoFiscalizacao().getDataCiencia()));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.DATA_INFRACAO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "dataInfracao", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.DATA_LAVRATURA.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "dataLavratura", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.DESCRICAO_INFRACAO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "descricaoInfracao", null));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.ENCERRAMENTO_AUTO.name(), "ENCERRAMENTO DO AUTO");
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.LISTA_INFRACAO.name(), montaListaInfracao(processoFiscalizacao));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.NOME_PESSOA_CIENCIA.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "pessoaCiencia", null));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.NUMERO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "numero", null));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.PRAZO_RECURSO.name(), formataData(processoFiscalizacao.getAutoInfracaoFiscalizacao().getPrazoRecurso()));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.USUARIO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "usuarioSistema.pessoaFisica.nome", null));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.NUMERO_FORMULARIO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "numeroFormulario", null));
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.OBSERVACAO.name(), nomeDoCampo(processoFiscalizacao.getAutoInfracaoFiscalizacao(), "descricaoInfracao", null));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.VALOR_TOTAL_PENALIDADES_UFM.name(), dfUFM.format(processoFiscalizacao.somaValorTotalUFMItemProcesso()));
        addTag(context, TipoModeloDoctoOficial.TagsAutoInfracaoFiscalizacao.VALOR_TOTAL_PENALIDADES_RS.name(), dfReais.format(processoFiscalizacao.somaValorTotalReaisItemProcesso(moedaFacade.recuperaValorUFMParaData(processoFiscalizacao.getDataCadastro()))));
    }

    public String formataData(Date data) {
        return data != null ? formatterData.format(data) : "";
    }

    public String montaListaInfracao(ProcessoFiscalizacao processoFiscalizacao) {
        BigDecimal totalValorUFM = BigDecimal.ZERO;
        BigDecimal totalValorReais = BigDecimal.ZERO;
        BigDecimal valorUFMConvertido = moedaFacade.recuperaValorUFMParaData(processoFiscalizacao.getDataCadastro());
        String lancamentosHtml = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        lancamentosHtml += "<tr><td align=\"left\" width=\"30%\"><b>Infração</b></td>";
        lancamentosHtml += "<td align=\"left\" width=\"40%\"><b>Penalidade</b></td>";
        lancamentosHtml += "<td align=\"right\" width=\"15%\"><b>Valor (UFM)</b></td>";
        lancamentosHtml += "<td align=\"right\" width=\"15%\"><b>Valor (R$)</b></td>";

        for (ItemProcessoFiscalizacao item : processoFiscalizacao.getItensProcessoFiscalizacao()) {
            lancamentosHtml += "<tr><td align=\"left\">" + item.getInfracaoFiscalizacaoSecretaria().getDescricaoReduzida() + "</td>";
            lancamentosHtml += "<td align=\"left\">" + item.getPenalidadeFiscalizacaoSecretaria().getDescricaoReduzida() + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfUFM.format(item.valorTotalUFM()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(item.valorTotalUFMConvertido(valorUFMConvertido)) + "</td>";

            totalValorUFM = totalValorUFM.add(item.valorTotalUFM());
            totalValorReais = totalValorReais.add(item.valorTotalUFMConvertido(valorUFMConvertido));
        }

        lancamentosHtml += "<tr><td colspan=\"14\"><hr><td></tr>";
        lancamentosHtml += "<tr><td align=\"left\" colspan=\"2\"><b>TOTAL</b></td>";
        lancamentosHtml += "<td align=\"right\">" + dfUFM.format(totalValorUFM) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalValorReais) + "</td>";
        lancamentosHtml += "</table>";

        return lancamentosHtml;
    }


    public void tagsIsencaoIPTU(VelocityContext context, IsencaoCadastroImobiliario isencaoCadastroImobiliario) throws UFMException {
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.DESCRICAO_CATEGORIA_ISENCAO.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "categoriaIsencaoIPTU.descricao", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.DESCRICAO_LEI.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "categoriaIsencaoIPTU.descricaoLei", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.NUMERO_LEI.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "categoriaIsencaoIPTU.numeroLei", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.NUMERO_PROCESSO.name(), StringUtil.preencheString(isencaoCadastroImobiliario.getProcessoIsencaoIPTU().getNumero() + "", 5, '0'));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.EXERCICIO_PROCESSO.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "exercicioProcesso.ano", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.NUMERO_PROTOCOLO.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "numeroProtocolo", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.ANO_PROTOCOLO.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "anoProtocolo", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.EXERCICIO_REFERENCIA.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "exercicioReferencia.ano", null));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.DATA_LANCAMENTO.name(), nomeDoCampo(isencaoCadastroImobiliario.getProcessoIsencaoIPTU(), "dataLancamento", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsIsencaoIPTU.DATA_VALIDADE_ISENCAO.name(), nomeDoCampo(isencaoCadastroImobiliario, "finalVigencia", TipoFormato.DATA));
    }

    public void tagsCadastroEconomico(VelocityContext context, DocumentoOficial documentoOficial) throws AtributosNulosException {
        try {
            Pessoa p = null;
            CadastroEconomico ce = null;
            PessoaJuridica pj = new PessoaJuridica();

            if (documentoOficial.getCadastro() != null && documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial().equals(TipoCadastroDoctoOficial.CADASTROECONOMICO)) {
                ce = this.cadastroEconomicoFacade.recuperar(documentoOficial.getCadastro().getId());
            }
            if (ce != null && ce.getPessoa() != null) {
                p = this.pessoaFacade.recuperar(ce.getPessoa().getId());
                if (p instanceof PessoaJuridica) {
                    pj = (PessoaJuridica) p;
                }
            } else {
                return;
            }
            Lote lote = new Lote();
            if (ce.getCadastroImobiliario() != null && ce.getCadastroImobiliario().getLote() != null) {
                lote = this.loteFacade.recuperar(ce.getCadastroImobiliario().getLote().getId());
            }

            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CMC.name(), ce.getInscricaoCadastral());
            if (!ce.getSituacaoCadastroEconomico().isEmpty()) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.SITUACAO_CADASTRAL.name(), ce.getSituacaoCadastroEconomico().get(0).getSituacaoCadastral().getDescricao());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.SITUACAO_CADASTRAL.name(), SituacaoCadastralCadastroEconomico.NULA.name());
            }
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.DATA_ABERTURA.name(), nomeDoCampo(ce, "abertura", TipoFormato.DATA));

            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.RAZAO_SOCIAL.name(), ce.getPessoa().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NOME_FANTASIA.name(), nomeDoCampo(pj, "nomeFantasia", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CPF_CNPJ.name(), p.getCpf_Cnpj());
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.INSCRICAO_ESTADUAL.name(), ce.getPessoa().getRg_InscricaoEstadual());

            EnderecoCorreio enderecoCorreio = recuperaEnderecoPessoa(p);
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.TIPO_LOGRADOURO_PESSOA.name(), nomeDoCampo(enderecoCorreio, "tipoEndereco", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOGRADOURO_PESSOA.name(), nomeDoCampo(enderecoCorreio, "logradouro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NUMERO_PESSOA.name(), nomeDoCampo(enderecoCorreio, "numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.BAIRRO_PESSOA.name(), nomeDoCampo(enderecoCorreio, "bairro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.COMPLEMENTO_PESSOA.name(), nomeDoCampo(enderecoCorreio, "complemento", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CIDADE_PESSOA.name(), nomeDoCampo(enderecoCorreio, "localidade", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.UF_PESSOA.name(), nomeDoCampo(enderecoCorreio, "uf", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CEP_PESSOA.name(), nomeDoCampo(enderecoCorreio, "cep", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.TELEFONES_PESSOA.name(), montaListaTelefones(p));

            EnderecoCadastroEconomico enderecoLocalizacao = ce.getEnderecoPrimario();
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.TIPO_LOGRADOURO_BCE_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOGRADOURO_BCE_LOCALIZACAO.name(), nomeDoCampo(enderecoLocalizacao, "logradouro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.BAIRRO_BCE_LOCALIZACAO.name(), nomeDoCampo(enderecoLocalizacao, "bairro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NUMERO_BCE_LOCALIZACAO.name(), nomeDoCampo(enderecoLocalizacao, "numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.COMPLEMENTO_BCE_LOCALIZACAO.name(), nomeDoCampo(enderecoLocalizacao, "complemento", null));

            EnderecoCadastroEconomico enderecoCorrespondencia = ce.getEnderecoSecundario();
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.TIPO_LOGRADOURO_BCE_CORRESPONDENCIA.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOGRADOURO_BCE_CORRESPONDENCIA.name(), nomeDoCampo(enderecoCorrespondencia, "logradouro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.BAIRRO_BCE_CORRESPONDENCIA.name(), nomeDoCampo(enderecoCorrespondencia, "bairro", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NUMERO_BCE_CORRESPONDENCIA.name(), nomeDoCampo(enderecoCorrespondencia, "numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.COMPLEMENTO_BCE_CORRESPONDENCIA.name(), nomeDoCampo(enderecoCorrespondencia, "complemento", null));

            Logradouro logradouroBCIdoBCE = (Logradouro) loteFacade.recuperaAtributoPorLote("logradouro", lote);
            Bairro bairroBCIdoBCE = (Bairro) loteFacade.recuperaAtributoPorLote("bairro", lote);
            Quadra quadraBCIdoBCE = (Quadra) loteFacade.recuperaAtributoPorLote("quadra", lote);

            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOGRADOURO_BCI.name(), nomeDoCampo(logradouroBCIdoBCE, "nome", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.BAIRRO_BCI.name(), nomeDoCampo(bairroBCIdoBCE, "descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NUMERO_BCI.name(), nomeDoCampo(ce.getCadastroImobiliario(), "numero", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.COMPLEMENTO_BCI.name(), nomeDoCampo(lote, "complemento", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.INSCRICAO_CADASTRAL_BCI.name(), nomeDoCampo(ce, "cadastroImobiliario.inscricaoCadastral", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.QUADRA_BCI.name(), nomeDoCampo(quadraBCIdoBCE, "descricao", null));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.LOTE_BCI.name(), nomeDoCampo(lote, "codigoLote", null));

            EnquadramentoFiscal enquadramentoFiscal = cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(ce);
            if (enquadramentoFiscal != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.PORTE.name(), enquadramentoFiscal.getPorte() != null ? enquadramentoFiscal.getPorte().getDescricao() : "");
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.PORTE.name(), "");
            }
            if (ce.getClassificacaoAtividade() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), ce.getClassificacaoAtividade().getDescricao());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CLASSIFICACAO.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.AREA_UTILIZACAO.name(), nomeDoCampo(ce, "areaUtilizacao", TipoFormato.DECIMAL));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(ce.getPessoa()));

            EconomicoCNAE primario = ce.getEconomicoCNAEPrincipal();
            if (primario != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CNAE_PRIMARIO.name(), primario.getCnae().getCodigoCnae() + " - " + primario.getCnae().getDescricaoDetalhada() + (primario.getCnae().getNivelComplexibilidade() != null ? (" - " + primario.getCnae().getNivelComplexibilidade()) : ""));
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CNAE_PRIMARIO.name(), "");
            }

            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.CNAES_SEGUNDARIOS.name(), montaTabelaCnaes(ce.getEconomicoCNAESecundariosVigentes()));
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.NATUREZA_JURIDICA.name(), ce.getNaturezaJuridica() != null ? ce.getNaturezaJuridica().getDescricao() : "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.HORARIOS_FUNCIONAMENTO.name(), montaTabelaHorariosFuncionamento(pj.getHorariosFuncionamentoAtivos()));

            addTagAtividadeEconomica(context, documentoOficial);
        } catch (NullPointerException e) {
            throw new AtributosNulosException("Existem atributos não preenchidos para o cadastro econômico em utilização.");
        }
    }

    public EnderecoCorreio recuperaEnderecoPessoa(Pessoa pessoa) {
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            if (pessoa.getEnderecos().size() > 1) {
                for (EnderecoCorreio correio : pessoa.getEnderecos()) {
                    if (correio.getPrincipal().equals(Boolean.TRUE)) {
                        return correio;
                    }
                }
                return pessoa.getEnderecos().get(0);
            } else {
                return pessoa.getEnderecos().get(0);
            }
        }
        return new EnderecoCorreio();
    }

    public void tagsCadastroImobiliario(VelocityContext context, DocumentoOficial documentoOficial) {
        CadastroImobiliario cadastroImobiliario = recuperaCadastroImobiliario(documentoOficial);
        Lote lote = new Lote();
        if (cadastroImobiliario.getLote() != null) {
            lote = this.loteFacade.recuperar(cadastroImobiliario.getLote().getId());
        }

        Logradouro logradouro = (Logradouro) loteFacade.recuperaAtributoPorLote("logradouro", lote);
        List logradouros = loteFacade.recuperaAtributoPrincipaldoLote("logradouro", lote);
        Logradouro logradouroPrincipal = null;
        if (logradouros != null && !logradouros.isEmpty()) {
            logradouroPrincipal = (Logradouro) logradouros.get(0);
        }
        List testadas = loteFacade.recuperaAtributoPrincipaldoLote("testada", lote);
        Testada testadaPrincipal = null;
        if (logradouros != null && !logradouros.isEmpty()) {
            testadaPrincipal = (Testada) testadas.get(0);
        }
        Bairro bairro = (Bairro) loteFacade.recuperaAtributoPorLote("bairro", lote);
        Quadra quadra = (Quadra) loteFacade.recuperaAtributoPorLote("quadra", lote);
        String cep = (String) loteFacade.recuperaAtributoPorLote("logradouroBairro.cep", lote);

        String nomeLoteamento = "";
        String quadraLoteamento = "";
        String loteLoteamento = "";

        String descricaoLote = lote.getDescricaoLoteamento();
        if (!Strings.isNullOrEmpty(descricaoLote)) {
            nomeLoteamento = descricaoLote.substring(5, descricaoLote.indexOf("Q:"));
            quadraLoteamento = descricaoLote.substring(descricaoLote.indexOf("Q:") + 2, descricaoLote.indexOf("L:"));
            loteLoteamento = descricaoLote.substring(descricaoLote.indexOf("L:") + 2);
        }

        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CADASTRO_IMOBILIARIO.name(), nomeDoCampo(cadastroImobiliario, "codigo", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.INSCRICAO_CADASTRAL.name(), nomeDoCampo(cadastroImobiliario, "inscricaoCadastral", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.NUMERO_LOGRADOURO.name(), nomeDoCampo(cadastroImobiliario, "numero", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.COMPLEMENTO.name(), nomeDoCampo(cadastroImobiliario, "complementoEndereco", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO.name(), nomeDoCampo(logradouro, "tipoLogradouro.descricao", null) + " " + nomeDoCampo(logradouro, "nome", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.BAIRRO.name(), nomeDoCampo(bairro, "descricao", null));

        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CEP.name(), nomeDoCampo(cep, "logradouroBairro.cep", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.QUADRA.name(), nomeDoCampo(quadra, "descricao", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOTE.name(), nomeDoCampo(lote, "codigoLote", null));

        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.NOME_DO_LOTEAMENTO.name(), nomeLoteamento.trim());
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.QUADRA_DO_LOTEAMENTO.name(), quadraLoteamento.trim());
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOTE_DO_LOTEAMENTO.name(), loteLoteamento.trim());

        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOTE.name(), nomeDoCampo(lote, "codigoLote", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.AREA_DO_TERRENO.name(), nomeDoCampo(lote, "areaLote", TipoFormato.DECIMAL));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOTEAMENTO.name(), nomeDoCampo(lote, "descricaoLoteamento", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.TIPO_IMOVEL.name(), cadastroImobiliario.getConstrucoesAtivas().isEmpty() ? TipoImovel.TERRITORIAL.name() : TipoImovel.PREDIAL.name());
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CARACTERISTICA_CONSTRUCAO.name(), montarCaracteristicasContrucao(cadastroImobiliario));

        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CIDADE.name(), nomeDoCampo(configuracaoTributario.getCidade().getNome(), "cidade", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.UF.name(), nomeDoCampo(configuracaoTributario.getCidade().getUf().getNome(), "uf", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.TESTADA_PRINCIPAL.name(), nomeDoCampo(testadaPrincipal, "face.codigoFace", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.TESTADA_EM_METROS.name(), testadaPrincipal != null && testadaPrincipal.getTamanho() != null
            ? testadaPrincipal.getTamanho() : BigDecimal.ZERO);
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LADO_FACE.name(), nomeDoCampo(testadaPrincipal, "face.lado", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LARGURA_FACE.name(), nomeDoCampo(testadaPrincipal, "face.largura", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.LOGRADOURO_PRINCIPAL.name(), nomeDoCampo(logradouroPrincipal, "nome", null));
        Propriedade propriedade = cadastroImobiliarioFacade.recuperarProprietarioPrincipal(cadastroImobiliario);
        if (propriedade != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(propriedade.getPessoa()));
        }

        tagsDocumento(context, documentoOficial);
        String codigo = GeradorChaveAutenticacao.formataChaveDeAutenticacao(documentoOficial.getCodigoVerificacao());
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.CODIGO_VERIFICACAO.name(), codigo);

        addTagProprietario(cadastroImobiliario, context);
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.PROPRIETARIOS.name(), addTagsProprietarios(cadastroImobiliario));

        if (!cadastroImobiliario.getCompromissarioVigente().isEmpty()) {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CPF_CNPJ_COMPROMISSARIO.name(), cadastroImobiliario.getCompromissarioVigente().get(0).getCompromissario().getCpf_Cnpj());
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.COMPROMISSARIO.name(), cadastroImobiliario.getCompromissarioVigente().get(0).getCompromissario().getNome());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CPF_CNPJ_COMPROMISSARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.COMPROMISSARIO.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.COMPROMISSARIOS.name(), addTagsCompromissarios(cadastroImobiliario));

        addTagTotalAreaConstruido(cadastroImobiliario, context);
        addTagValorVenal(cadastroImobiliario, context);
    }

    private String montarCaracteristicasContrucao(CadastroImobiliario cadastroImobiliario) {
        String sql = "select ci.inscricaocadastral, carneiptu.fatorpeso, \n" +
            "   max((select vp.valor from construcao_valoratributo civa\n" +
            "         inner join valoratributo va on va.id = civa.atributos_id\n" +
            "         inner join valorpossivel vp on vp.id = va.valordiscreto_id\n" +
            "         inner join atributo on atributo.id = civa.atributos_key\n" +
            "         inner join construcao constru on constru.id = civa.construcao_id\n" +
            "         where constru.id = carneiptu.construcao_id and coalesce(constru.cancelada,0) = 0\n" +
            "           and atributo.identificacao = 'tipo_construcao')) as tipo_construcao\n" +
            "from calculoiptu iptu\n" +
            "inner join calculo calc on calc.id = iptu.id\n" +
            "inner join cadastroimobiliario ci on ci.id = calc.cadastro_id\n" +
            "inner join processocalculo pro on pro.id = calc.processocalculo_id\n" +
            "inner join carneiptu on carneiptu.calculo_id = calc.id and (carneiptu.construcao_id = (select max(construcao_id) from carneiptu where carneiptu.calculo_id = calc.id) or carneiptu.construcao_id is null)\n" +
            "where pro.exercicio_id = :idExercicio\n" +
            "and ci.id = :idCadastro\n" +
            "group by ci.inscricaocadastral, carneiptu.fatorpeso";
        Query q = em.createNativeQuery(sql);
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(new Date()));
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idCadastro", cadastroImobiliario.getId());
        List<Object[]> lista = q.getResultList();
        if (!lista.isEmpty()) {

            String tipo = lista.get(0)[2] != null ? (String) lista.get(0)[2] : "Sem Informação";
            BigDecimal fator = lista.get(0)[1] != null ? (BigDecimal) lista.get(0)[1] : BigDecimal.ZERO;
            return "Tipo: " + tipo + " Fator/Peso: " + decimalFormat.format(fator);
        }
        return "Sem Informação";
    }

    private String addTagsProprietarios(CadastroImobiliario ci) {
        StringBuilder proprietario = new StringBuilder();

        List<Propriedade> propriedades = ci.getPropriedadeVigente();
        if (propriedades != null && !propriedades.isEmpty()) {
            for (Propriedade propriedade : propriedades) {
                proprietario.append("<br />" + propriedade.getPessoa().getNome() + " (CPF/CNPJ: " + propriedade.getPessoa().getCpf_Cnpj() + ")");
            }
            return proprietario.toString();
        }
        return proprietario.append("Sem informações.").toString();
    }

    private String addTagsCompromissarios(CadastroImobiliario ci) {
        StringBuilder compromissario = new StringBuilder();

        List<Compromissario> compromissarios = ci.getCompromissarioVigente();
        if (compromissarios != null && !compromissarios.isEmpty()) {
            for (Compromissario comp : compromissarios) {
                compromissario.append("<br />" + comp.getCompromissario().getNome() + " (CPF/CNPJ: " + comp.getCompromissario().getCpf_Cnpj() + ")");
            }
            return compromissario.toString();
        }
        return compromissario.append("Sem informações.").toString();
    }

    public void tagsCadastroRural(VelocityContext context, DocumentoOficial documentoOficial) {
        CadastroRural cr = null;
        if (documentoOficial.getCadastro() != null) {
            cr = this.cadastroRuralFacade.recuperar(documentoOficial.getCadastro().getId());
        }
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.CADASTRO_RURAL.name(), nomeDoCampo(cr, "codigo", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.INCRA.name(), nomeDoCampo(cr, "numeroIncra", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.LOCALIZACAO.name(), nomeDoCampo(cr, "localizacaoLote", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.AREA.name(), nomeDoCampo(cr, "areaLote", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.TIPO_AREA.name(), nomeDoCampo(cr, "tipoAreaRural", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.VALOR_VENAL.name(), nomeDoCampo(cr, "valorVenalLote,", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.NOME_PROPRIEDADE.name(), nomeDoCampo(cr, "nomePropriedade", null));
        addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(cadastroRuralFacade.recuperarPropriedadePrincipal(cr).getPessoa()));
        addTagProprietarioRural(context, cr);
    }

    public void tagsPessoaFisica(VelocityContext context, DocumentoOficial documentoOficial) {
        if (documentoOficial.getPessoa() != null) {
            if (documentoOficial.getPessoa() instanceof PessoaFisica) {
                PessoaFisica pes = this.pessoaFisicaFacade.recuperar(documentoOficial.getPessoa().getId());

                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CONTRIBUINTE.name(), pes.getNome());
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.RG_INSC_ESTADUAL.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CPF.name(), pes.getCpf());
                if (!pes.getTelefones().isEmpty()) {
                    boolean temPrincipal = false;
                    for (Telefone tel : pes.getTelefones()) {
                        if (tel.getPrincipal()) {
                            addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.TELEFONE.name(), tel.getTelefone());
                            temPrincipal = true;
                        }
                    }
                    if (!temPrincipal) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.TELEFONE.name(), pes.getTelefones().get(0).getTelefone());
                    }
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.TELEFONE.name(), "");
                }
                if (!pes.getEnderecos().isEmpty()) {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.LOGRADOURO.name(), pes.getEnderecos().get(0).getLogradouro());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.NUMERO.name(), pes.getEnderecos().get(0).getNumero());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.COMPLEMENTO.name(), pes.getEnderecos().get(0).getComplemento());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), pes.getEnderecos().get(0).getCep());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.BAIRRO.name(), pes.getEnderecos().get(0).getBairro());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CIDADE.name(), pes.getEnderecos().get(0).getLocalidade());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.UF.name(), pes.getEnderecos().get(0).getUf());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), pes.getEnderecos().get(0).getCep());
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.LOGRADOURO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.NUMERO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.COMPLEMENTO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.BAIRRO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CIDADE.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.UF.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.CEP.name(), "");
                }
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.EMAIL.name(), pes.getEmail());
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(pes));
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.ENDERECO_DOMICILIOFISCAL.name(), pes.getEnderecoDomicilioFiscal() != null ? pes.getEnderecoDomicilioFiscal().toString() : "");
            }
        }
    }

    public void tagsPessoaJuridica(VelocityContext context, DocumentoOficial documentoOficial) {
        if (documentoOficial.getPessoa() != null) {
            if (documentoOficial.getPessoa() instanceof PessoaJuridica) {
                PessoaJuridica pes = this.pessoaJuridicaFacade.recuperar(documentoOficial.getPessoa().getId());

                addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.NOMEFANTASIA.name(), pes.getNomeFantasia());
                addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.RAZAO_SOCIAL.name(), pes.getRazaoSocial());
                addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CNPJ.name(), pes.getCnpj());
                if (!pes.getEnderecos().isEmpty()) {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), pes.getEnderecos().get(0).getCep());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.LOGRADOURO.name(), pes.getEnderecos().get(0).getLogradouro());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.NUMERO.name(), pes.getEnderecos().get(0).getNumero());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.BAIRRO.name(), pes.getEnderecos().get(0).getBairro());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CIDADE.name(), pes.getEnderecos().get(0).getLocalidade());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.UF.name(), pes.getEnderecos().get(0).getUf());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), pes.getEnderecos().get(0).getCep());
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.COMPLEMENTO.name(), pes.getEnderecos().get(0).getComplemento());
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.LOGRADOURO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.NUMERO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.BAIRRO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CIDADE.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.UF.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.CEP.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.COMPLEMENTO.name(), "");
                }
                addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(pes));
                addTag(context, TipoModeloDoctoOficial.TagsPessoaFisica.ENDERECO_DOMICILIOFISCAL.name(), pes.getEnderecoDomicilioFiscal() != null ? pes.getEnderecoDomicilioFiscal().toString() : "");
                if (!pes.getTelefones().isEmpty()) {
                    boolean temPrincipal = false;
                    for (Telefone tel : pes.getTelefones()) {
                        if (tel.getPrincipal().equals(Boolean.TRUE)) {
                            addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.TELEFONE.name(), tel.getTelefone());
                            temPrincipal = true;
                        }
                    }
                    if (!temPrincipal) {
                        addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.TELEFONE.name(), pes.getTelefones().get(0).getTelefone());
                    }
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsPessoaJuridica.TELEFONE.name(), "");
                }
            }
        }
    }

    private void addTag(VelocityContext contexto, String chave, Object valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        } else {
            contexto.put(chave, "");
        }
    }

    private ParcelaValorDivida calculaAcrescimoParcela(ParcelaValorDivida parcela) {
        parcela = em.find(ParcelaValorDivida.class, parcela.getId());
        parcela.getItensParcelaValorDivida().size();
        parcela.getSituacoes().size();
        Date data = new Date();
        CalculadorAcrescimos.calculaAcrescimo(parcela, data, consultaDebitoFacade.getValorAtualizadoParcela(parcela), dividaFacade.configuracaoVigente(parcela.getValorDivida().getDivida()));
        return parcela;
    }

    private CadastroEconomico recuperarCadastroEconomico(DocumentoOficial documentoOficial) {
        if (documentoOficial.getCadastro() != null) {
            return this.cadastroEconomicoFacade.recuperar(documentoOficial.getCadastro().getId());
        }
        return null;
    }

    private void addTagAtividadeEconomica(VelocityContext context, DocumentoOficial documentoOficial) {
        CadastroEconomico cadastroEconomico = recuperarCadastroEconomico(documentoOficial);
        if (!cadastroEconomico.getEconomicoCNAE().isEmpty()) {
            StringBuilder atividade = new StringBuilder();
            for (EconomicoCNAE ecnae : cadastroEconomico.getEconomicoCNAE()) {
                atividade.append(ecnae.getCnae().getDescricaoDetalhada()).append(", ");
                if (ecnae.getCnae().getDescricaoDetalhada() != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), atividade.substring(0, atividade.length() - 2));
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), "");
                }
            }
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroEconomico.ATIVIDADE_ECONOMICA.name(), "");
        }
    }

    private Propriedade buscaPropropriedadeVigenteBci(List<Propriedade> propriedades) {
        if (!propriedades.isEmpty()) {
            for (Propriedade propriedade : propriedades) {
                if (propriedade.getFinalVigencia() == null || propriedade.getFinalVigencia().compareTo(new Date()) < 0) {
                    return propriedade;
                }
            }
        }
        return null;
    }

    private void addTagProprietario(CadastroImobiliario ci, VelocityContext context) {
        Propriedade p = ci != null && ci.getPropriedadeVigente() != null &&
            !ci.getPropriedadeVigente().isEmpty() ? ci.getPropriedadeVigente().get(0) : null;
        if (p != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.PROPRIETARIO.name(), p.getPessoa().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CPF_CNPJ_PROPRIETARIO.name(), p.getPessoa().getCpf_Cnpj());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.PROPRIETARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.CPF_CNPJ_PROPRIETARIO.name(), "");
        }
    }

    private PropriedadeRural buscaPropropriedadeRuralVigenteBcr(List<PropriedadeRural> propriedadesRurais) {
        if (!propriedadesRurais.isEmpty()) {
            for (PropriedadeRural propriedadeRural : propriedadesRurais) {
                if (propriedadeRural.getFinalVigencia() == null || propriedadeRural.getFinalVigencia().compareTo(new Date()) < 0) {
                    return propriedadeRural;
                }
            }
        }
        return null;
    }

    private void addTagProprietarioRural(VelocityContext context, CadastroRural cadastroRural) {
        PropriedadeRural propriedadeRural = buscaPropropriedadeRuralVigenteBcr(cadastroRural.getPropriedade());
        if (propriedadeRural != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.PROPRIETARIO.name(), propriedadeRural.getPessoa().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.CPF_CNPJ.name(), propriedadeRural.getPessoa().getCpf_Cnpj());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.PROPRIETARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsCadastroRural.CPF_CNPJ.name(), "");
        }
    }

    private void addTagValorVenal(CadastroImobiliario cadastroImobiliario, VelocityContext context) {
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.VALOR_VENAL.name(), Util.formataValor(cadastroImobiliario.getValorVenal()));
    }

    private void addTagTotalAreaConstruido(CadastroImobiliario ci, VelocityContext context) {
        double totalConstruido = 0;
        if (!ci.getConstrucoesAtivas().isEmpty()) {
            for (Construcao construcao : ci.getConstrucoesAtivas()) {
                totalConstruido += (construcao.getAreaConstruida());
            }
        }
        addTag(context, TipoModeloDoctoOficial.TagsCadastroImobiliario.AREA_CONSTRUIDA.name(), Util.formataNumero(totalConstruido));
    }

    public void adicionaTagsNoContexto(VelocityContext context, DocumentoOficial documentoOficial) throws AtributosNulosException {
        if (TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
            tagsCadastroEconomico(context, documentoOficial);
        }
        if (TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
            tagsCadastroImobiliario(context, documentoOficial);
        }
        if (TipoCadastroDoctoOficial.CADASTRORURAL.equals(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
            tagsCadastroRural(context, documentoOficial);
        }
        if (TipoCadastroDoctoOficial.PESSOAFISICA.equals(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
            tagsPessoaFisica(context, documentoOficial);
        }
        if (TipoCadastroDoctoOficial.PESSOAJURIDICA.equals(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
            tagsPessoaJuridica(context, documentoOficial);
        }
        tagsDocumento(context, documentoOficial);
    }

    public String mesclaTagsDocto(String conteudo, DocumentoOficial documentoOficial, ModuloTipoDoctoOficial modulo, Object entidade, Object entidadeAux) throws UFMException, AtributosNulosException {
        UUID uuid = UUID.randomUUID();

        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";

        conteudo = conteudo.replace(string1, string2);

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        VelocityContext context = new VelocityContext();
        adicionaTagsNoContexto(context, documentoOficial);
        tagsDataHoje(context);
        tagsTextoFixo(context, documentoOficial, conteudo);
        tagsConfiguracaoDocumento(context);
        tagsIP(context, documentoOficial);
        tagsUsuario(context, documentoOficial);
        switch (modulo) {
            case SOLICITACAO:
                tagsGeraisSolicitacao(context, (SolicitacaoDoctoOficial) entidade);
                break;
            case FISCALIZACAO:
                if (entidade instanceof AcaoFiscal) {
                    tagsGeraisFiscalizacao(context, (AcaoFiscal) entidade, (ParametroFiscalizacao) entidadeAux, null);
                } else if (entidade instanceof AutoInfracaoFiscal) {
                    tagsGeraisFiscalizacao(context, ((AutoInfracaoFiscal) entidade).getRegistro().getAcaoFiscal(), (ParametroFiscalizacao) entidadeAux, (AutoInfracaoFiscal) entidade);
                    tagsAutoInfracao(context, ((AutoInfracaoFiscal) entidade), (ParametroFiscalizacao) entidadeAux);
                }
                break;
            case CERTIDADIVIDAATIVA:
                tagsCertidaoDividaAtiva(context, (CertidaoDividaAtiva) entidade);
                break;
            case PETICAODIVIDAATIVA:
                tagsCertidaoPeticaoDividaAtiva(context, (Peticao) entidade);
                break;
            case RBTRANS:
                tagsRBTrans(context, (TermoRBTrans) entidade);
                break;
            case TERMOADVERTENCIA:
                tagsTermoAdvertencia(context, (ProcessoFiscalizacao) entidade);
                break;
            case AUTOINFRACAO:
                tagsAutoInfracaoFiscalizacao(context, (ProcessoFiscalizacao) entidade);
                break;
            case PARECER_FISCALIZACAO:
                tagsParecerRecursoFiscalizacao(context, (RecursoFiscalizacao) entidade);
                break;
            case TERMO_GERAIS:
                tagsTermoGerais(context, (TermoGeralFiscalizacao) entidade);
                break;
            case TERMODIVIDAATIVA:
                tagsTermoDividaAtiva(context, (LinhaDoLivroDividaAtiva) entidade);
                break;
            case FISCALIZACAORBTRANS:
                tagsFiscalizacaoRBTrans(context, (FiscalizacaoRBTrans) entidade, documentoOficial);
                break;
            case PROTOCOLO:
                tagsProtocolo(context, (Processo) entidade, (Tramite) entidadeAux);
                break;
            case CONTRATO_RENDAS_PATRIMONIAIS:
                tagsContratoRendasPatrimoniais(context, (ContratoRendasPatrimoniais) entidade);
                break;
            case CONTRATO_CEASA:
                tagsContratoCEASA(context, (ContratoCEASA) entidade);
                break;
            case ISENCAO_IPTU:
                tagsIsencaoIPTU(context, (IsencaoCadastroImobiliario) entidade);
                break;
            case COBRANCAADMINISTRATIVA:
                tagsCobrancaAdministrativa(context, (ItemNotificacao) entidade);
                break;
            case TERMO_PARCELAMENTO:
                conteudo = tagsProcessoParcelamento(context, (List<ProcessoParcelamento>) entidade, conteudo);
                break;
            case SOLICITACAO_SEPULTAMENTO:
            case DECLARACAO_BENEFICIOS_EVENTUAIS:
            case DECLARACAO_SOLICITANTE_BENEFICIARIO:
            case REQUISICAO_FUNERAL:
                tagsAuxilioFuneral(context, (AuxilioFuneral) entidade);
                break;
            case CERTIDAO_BAIXA_ATIVIDADE:
                tagsCertidaoBaixaAtividade(context, (CertidaoAtividadeBCE) entidade);
                break;
            case SUBVENCAO:
                tagsSubvencao(context, (SubvencaoEmpresas) entidade);
                break;
            case RBTRANS_CERTIFICADO_OTT:
                tagsRBTransCertificadoOTT(context, (CertificadoAnualOTT) entidade);
                break;
            case RBTRANS_CERTIFICADO_CONDUTOR_OTT:
                tagsRBTransCertificadoOTT(context, (CertificadoCondutorOTT) entidade);
                break;
            case RBTRANS_CERTIFICADO_RENOVACAO_OTT:
                tagsRBTransCertificadoOTT(context, (CertificadoRenovacaoOTT) entidade);
                break;
            case MONITORAMENTO_FISCAL:
                tagsOrdemGeralMonitoramento(context, (OrdemGeralMonitoramento) entidade);
                break;
            case BLOQUEIO_OUTORGA:
                tagsProcessoBloqueioOutorga(context, (BloqueioOutorga) entidade);
                break;
            case ALVARA_CONSTRUCAO:
            case ALVARA_IMEDIATO:
                tagsProcessoRegularizacaoConstrucao(context, ((AlvaraConstrucao) entidade).getProcRegularizaConstrucao());
                tagsAlvaraConstrucao(context, (AlvaraConstrucao) entidade);
                break;
            case HABITESE_CONSTRUCAO:
                tagsProcessoRegularizacaoConstrucao(context, ((Habitese) entidade).getAlvaraConstrucao().getProcRegularizaConstrucao());
                tagsHabiteseConstrucao(context, (Habitese) entidade);
                break;
            case PROCESSO_PROTESTO:
                tagsProcessoDeProtesto(context, (ProcessoDeProtesto) entidade);
                break;
            case LICENCA_ETR:
                tagsLicencaETR(context, (RequerimentoLicenciamentoETR) entidade);
                break;
            case NOTA_EMPENHO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RESTO_EMPENHO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_ESTORNO_EMPENHO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RESTO_ESTORNO_EMPENHO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_LIQUIDACAO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);

            case NOTA_RESTO_LIQUIDACAO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_ESTORNO_LIQUIDACAO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RESTO_ESTORNO_LIQUIDACAO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_PAGAMENTO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RESTO_PAGAMENTO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_ESTORNO_PAGAMENTO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RESTO_ESTORNO_PAGAMENTO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;
            case NOTA_RECEITA_EXTRA:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case NOTA_RECEITA_EXTRA_PAGAMENTO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case NOTA_PAGAMENTO_EXTRA:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case NOTA_RECEITA_EXTRA_ESTORNO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case NOTA_PAGAMENTO_EXTRA_ESTORNO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case RECIBO_REINF:
                tagsReciboReinf(context, (EventoReinfDTO) entidade);
                break;

            case NOTA_OBRIGACAO_A_PAGAR_ESTORNO:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case SOLICITACAO_MATERIAL:
                tagsSolicitacaoMaterial(context, (SolicitacaoMaterial) entidade);
                break;

            case NOTA_OBRIGACAO_A_PAGAR:
                tagsNotaOrcamentaria(context, (NotaExecucaoOrcamentaria) entidade);
                break;

            case TR:
                tagsSolicitacaoMaterial(context, (SolicitacaoMaterial) entidade);
                break;

            case DFD:
                tagsSolicitacaoMaterial(context, (SolicitacaoMaterial) entidade);
                break;

            case CERTIDAO_MARCA_FOGO:
                tagsMarcaFogo(context, (MarcaFogo) entidade);
                break;

            case DOCUMENTO_LICENCIAMENTO_AMBIENTAL:
                tagsLicenciamentoAmbiental(context, (ProcessoLicenciamentoAmbiental) entidade);
                break;
        }
        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource(uuid.toString(), conteudo);
        repo.setEncoding("UTF-8");
        tagQrCode(context, documentoOficial);
        tagAssinatura(context, documentoOficial);
        Template template = ve.getTemplate(uuid.toString(), "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void tagsMarcaFogo(VelocityContext context, MarcaFogo marcaFogo) {
        Pessoa pessoa = pessoaFacade.recuperar(marcaFogo.getPessoa().getId());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_SIGLA.name(), marcaFogo.getSigla());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_NUMERO.name(), marcaFogo.getCodigo());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_EXERCICIO.name(), marcaFogo.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_NOME.name(), pessoa.getNome());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_CPFCNPJ.name(), pessoa.getCpf_Cnpj());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_NUMERO_PROTOCOLO.name(), marcaFogo.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_ANO_PROTOCOLO.name(), marcaFogo.getAnoProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_DESCRICAO.name(), marcaFogo.getDescricaoMarcaFogo());
        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_RG.name(), pessoa.getRg_InscricaoEstadual() + (StringUtils.isBlank(pessoa.getOrgaoExpedidor()) ? "" : " - " + pessoa.getOrgaoExpedidor()));
        if (pessoa.getEnderecoPrincipal() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_ENDERECO.name(), pessoa.getEnderecoPrincipal().toString());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_ENDERECO.name(), "");
        }
        if (marcaFogo.getUsuarioSistema() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_NOME_USUARIO.name(), marcaFogo.getUsuarioSistema().getNome());
            MatriculaFP matriculaFP = matriculaFPFacade.matriculaFPVigentePorPessoa(marcaFogo.getUsuarioSistema().getPessoaFisica());
            if (matriculaFP != null) {
                addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_MAT_USUARIO.name(), matriculaFP.getMatricula());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_MAT_USUARIO.name(), "");
            }
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_NOME_USUARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_MAT_USUARIO.name(), "");
        }

        String cadastrosRurais = marcaFogo.getCadastrosRurais().stream()
            .map(cadastro -> cadastro.getCadastroRural().toString())
            .collect(Collectors.joining(", "));

        addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_CADASTROS_RURAIS.name(), cadastrosRurais);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            arquivoFacade.recupera(marcaFogo.getLogo().getId(), outputStream);
            String base64 = Util.getBase64Encode(outputStream.toByteArray());
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_LOGO.name(),
                "<img src=\"" + "data:image/png;base64," + base64 + "\" width=\"100%\" height=\"500\"/>");
        } catch (Exception e) {
            addTag(context, TipoModeloDoctoOficial.TagsMarcaFogo.MF_LOGO.name(), "");
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(),
            !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                gerarQRCode("certidao-marca-fogo", marcaFogo.getId(), configuracaoTributario) : "");
    }

    public void tagsLicenciamentoAmbiental(VelocityContext context, ProcessoLicenciamentoAmbiental processo) {
        processo = processoLicenciamentoAmbientalFacade.recuperar(processo.getId());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.CODIGO.name(), processo.getSequencial());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.EXERCICIO.name(), processo.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.PROTOCOLO.name(), processo.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.ANO.name(), processo.getAnoProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.RAZAO_SOCIAL.name(),
            processo.getRequerente().getPessoa().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.LOCALIZACAO.name(),
            processo.getRequerente().getEnderecoCorreio().toString());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.CPF_CNPJ.name(),
            processo.getRequerente().getPessoa().getCpf_Cnpj());
        ControleLicenciamentoAmbiental controle = processoLicenciamentoAmbientalFacade
            .buscarUltimoControlePorStatus(processo, StatusLicenciamentoAmbiental.CONDICIONANTES);
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.CONDICIONANTES.name(),
            controle != null ? controle.getParecer() : "");
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.DESCRICAO.name(),
            processo.getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.OBSERVACAO.name(), processo.getObservacao());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.CNAES.name(),
            montarTabelaCNAEsLicenciamentoAmbiental(processo.getCnaes()));
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.SECRETARIO.name(),
            processo.getAssinaturaSecretario() != null ? processo.getAssinaturaSecretario().getSecretario() : "");
        addTagAssinatura(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.ASSINATURA_SECRETARIO.name(),
            processo.getAssinaturaSecretario() != null ? processo.getAssinaturaSecretario().getSecretario().getArquivo() : null);
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.DECRETO_NOMEACAO_SECRETARIO.name(),
            processo.getAssinaturaSecretario() != null ? processo.getAssinaturaSecretario().getSecretario().getDecretoNomeacao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.DIRETOR.name(),
            processo.getAssinaturaDiretor() != null ? processo.getAssinaturaDiretor().getSecretario() : "");
        addTagAssinatura(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.ASSINATURA_DIRETOR.name(),
            processo.getAssinaturaDiretor() != null ? processo.getAssinaturaDiretor().getSecretario().getArquivo() : null);
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.DECRETO_NOMEACAO_DIRETOR.name(),
            processo.getAssinaturaDiretor() != null ? processo.getAssinaturaDiretor().getSecretario().getDecretoNomeacao() : "");

        tagsParametroLicenciamentoAmbiental(context, processo);
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(),
            !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                gerarQRCode("licenciamento-ambiental", processo.getId(), configuracaoTributario) : "");
    }

    private void tagsParametroLicenciamentoAmbiental(VelocityContext context, ProcessoLicenciamentoAmbiental processo) {
        ParametroLicenciamentoAmbiental parametro = processoLicenciamentoAmbientalFacade
            .getParametroLicenciamentoAmbientalFacade().buscarParametroPeloExercicio(processo.getExercicio());
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.SECRETARIA.name(),
            parametro != null ? parametro.getSecretaria() : "");
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.SECRETARIO_ADJUNTO.name(),
            parametro != null && parametro.getSecretarioAdjunto() != null ?
                parametro.getSecretarioAdjunto() : "");
        addTagAssinatura(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.ASSINATURA_SECRETARIO_ADJUNTO.name(),
            parametro != null && parametro.getSecretarioAdjunto() != null ?
                parametro.getSecretarioAdjunto().getArquivo() : null);
        addTag(context, TipoModeloDoctoOficial.TagsLicenciamentoAmbiental.DECRETO_NOMEACAO_SECRETARIO_ADJUNTO.name(),
            parametro != null && parametro.getSecretarioAdjunto() != null ?
                parametro.getSecretarioAdjunto().getDecretoNomeacao() : "");
    }

    private String montarTabelaCNAEsLicenciamentoAmbiental(List<ProcessoLicenciamentoAmbientalCNAE> cnaesProcesso) {
        String html = "<table \" width=\"100%;\" style=\"border-collapse: collapse; font-size: 12pt; " +
            "font-family: arial, helvetica, sans-serif;\" border=\"1;\">"
            + "<tr>"
            + "<th align=\"center\" style=\"width=\"25%;\">Código CNAE</th>"
            + "<th align=\"center\">Descrição</th>"
            + "</tr>";
        for (ProcessoLicenciamentoAmbientalCNAE cnaeProcesso : cnaesProcesso) {
            html += "<tr>"
                + "<th align=\"center\">" + cnaeProcesso.getCnae().getCodigoCnae() + "</th>"
                + "<th align=\"center\">" + cnaeProcesso.getCnae().getDescricaoReduzida() + "</th>"
                + "</tr>";
        }
        html += "</table>";
        return html;
    }

    public void tagsNotaOrcamentaria(VelocityContext context, NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NOME_DA_NOTA.name(), notaExecucaoOrcamentaria.getNomeDaNota());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_DOCUMENTO.name(), notaExecucaoOrcamentaria.getNumeroDocumento());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DATA_DOCUMENTO.name(), notaExecucaoOrcamentaria.getData());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_ORGAO.name(), notaExecucaoOrcamentaria.getCodigoOrgao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_UNIDADE.name(), notaExecucaoOrcamentaria.getCodigoUnidade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_ORGAO.name(), notaExecucaoOrcamentaria.getDescricaoOrgao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_UNIDADE.name(), notaExecucaoOrcamentaria.getDescricaoUnidade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.BAIRRO.name(), notaExecucaoOrcamentaria.getBairro());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CEP.name(), notaExecucaoOrcamentaria.getCep());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CIDADE.name(), notaExecucaoOrcamentaria.getCidade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CPF_CNPJ.name(), notaExecucaoOrcamentaria.getCpfCnpj());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DATA_EMPENHO.name(), notaExecucaoOrcamentaria.getData());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DATA_ESTORNO.name(), notaExecucaoOrcamentaria.getDataEstorno());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_FUNCAO.name(), notaExecucaoOrcamentaria.getCodigoFuncao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_PROGRAMA_TRABALHO.name(), notaExecucaoOrcamentaria.getCodigoProgramaTrabalho());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_SUBFUNCAO.name(), notaExecucaoOrcamentaria.getCodigoSubFuncao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_PROGRAMA.name(), notaExecucaoOrcamentaria.getCodigoPrograma());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CODIGO_ACAO.name(), notaExecucaoOrcamentaria.getCodigoProjetoAtividade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_ACAO.name(), notaExecucaoOrcamentaria.getDescricaoProjetoAtividade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.PROGRAMA_DE_TRABALHO.name(), notaExecucaoOrcamentaria.getCodigoProgramaTrabalho());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_CLASSE_PESSOA.name(), notaExecucaoOrcamentaria.getClassePessoa());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESTINACAO_RECURSO.name(), notaExecucaoOrcamentaria.getDescricaoDestinacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.ESPECIFICACAO_DESPESA.name(), notaExecucaoOrcamentaria.getEspecificacaoDespesa());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.FUNCIONAL_PROGRAMATICA.name(), notaExecucaoOrcamentaria.getFuncionalProgramatica());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_FUNCAO.name(), notaExecucaoOrcamentaria.getDescricaoFuncao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_SUBFUNCAO.name(), notaExecucaoOrcamentaria.getDescricaoSubFuncao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.HISTORICO.name(), notaExecucaoOrcamentaria.getHistorico());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_LICITACAO.name(), notaExecucaoOrcamentaria.getNumeroLicitacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.MODALIDADE_LICITACAO.name(), notaExecucaoOrcamentaria.getModalidadeLicitacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.LICITACAO.name(), notaExecucaoOrcamentaria.getLicitacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.LOGRADOURO.name(), notaExecucaoOrcamentaria.getLogradouro());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NATUREZA_DESPESA.name(), notaExecucaoOrcamentaria.getNaturezaDespesa());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NOME_PESSOA.name(), notaExecucaoOrcamentaria.getNomePessoa());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.TIPO_EMPENHO.name(), notaExecucaoOrcamentaria.getTipoEmpenho());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_EMPENHO.name(), notaExecucaoOrcamentaria.getNumeroEmpenho());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.SALDO_ANTERIOR.name(), nomeDoCampo(notaExecucaoOrcamentaria, "saldoAnterior", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.SALDO_ATUAL.name(), nomeDoCampo(notaExecucaoOrcamentaria, "saldoAtual", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.VALOR.name(), nomeDoCampo(notaExecucaoOrcamentaria, "valor", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.VALOR_ESTORNO.name(), nomeDoCampo(notaExecucaoOrcamentaria, "valorEstorno", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.UF.name(), notaExecucaoOrcamentaria.getUf());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.VALOR_EXTENSO.name(), notaExecucaoOrcamentaria.getValorPorExtenso());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_CONTRATO.name(), notaExecucaoOrcamentaria.getNumeroContrato());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.INICIO_CONTRATO.name(), notaExecucaoOrcamentaria.getInicioContrato());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.FIM_CONTRATO.name(), notaExecucaoOrcamentaria.getFimContrato());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.FIM_CONTRATO_ATUAL.name(), notaExecucaoOrcamentaria.getFimContratoAtual());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.EXECUCAO_CONTRATO.name(), notaExecucaoOrcamentaria.getExecucaoContrato());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.LOCALIDADE.name(), notaExecucaoOrcamentaria.getLocalidade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.EXERCICIO_ORIGINAL_EMPENHO.name(), notaExecucaoOrcamentaria.getExercicioOriginalEmpenho());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.VALOR_EMPENHADO.name(), nomeDoCampo(notaExecucaoOrcamentaria, "valorEmpenhado", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_LIQUIDACAO.name(), notaExecucaoOrcamentaria.getNumeroLiquidacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DATA_LIQUIDACAO.name(), notaExecucaoOrcamentaria.getDataLiquidacao());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_PAGAMENTO.name(), notaExecucaoOrcamentaria.getNumeroPagamento());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DATA_PAGAMENTO.name(), notaExecucaoOrcamentaria.getDataPagamento());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.VALOR_LIQUIDADO.name(), nomeDoCampo(notaExecucaoOrcamentaria, "valorLiquidado", TipoFormato.VALOR_REAIS));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_AGENCIA.name(), notaExecucaoOrcamentaria.getDescricaoAgencia());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DESCRICAO_BANCO.name(), notaExecucaoOrcamentaria.getDescricaoBanco());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_AGENCIA.name(), notaExecucaoOrcamentaria.getNumeroAgencia());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_BANCO.name(), notaExecucaoOrcamentaria.getNumeroBanco());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DIGITO_VERIFICADOR.name(), notaExecucaoOrcamentaria.getDigitoVerificador());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.NUMERO_CONTA_CORRENTE.name(), notaExecucaoOrcamentaria.getNumeroContaCorrente());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DIGITO_VERIFICADOR_CONTA_CORRENTE.name(), notaExecucaoOrcamentaria.getDigitoVerificadorContaCorrente());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.TIPO_CONTA_BANCARIA.name(), notaExecucaoOrcamentaria.getTipoContaBancaria());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.SITUACAO_CONTA_BANCARIA.name(), notaExecucaoOrcamentaria.getSituacaoContaBancaria());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.BANCO_AGENCIA_CONTA.name(), notaExecucaoOrcamentaria.getBancoAgenciaConta());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CONTA_FINANCEIRA.name(), notaExecucaoOrcamentaria.getContaFinanceira());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CONTA_EXTRAORCAMENTARIA.name(), notaExecucaoOrcamentaria.getContaExtraorcamentaria());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.BANCO_CONTA_BANCARIA.name(), notaExecucaoOrcamentaria.getBancoContaBancariaEntidade());
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DOCUMENTOS_COMPROBATORIOS.name(), montarDocumentosComprobatorios(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DETALHAMENTOS.name(), montarDetalhamentos(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.RETENCOES.name(), montarRetencoes(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.PAGAMENTOS.name(), montarPagamentos(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.FATURAS.name(), montarFaturas(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.CONVENIOS.name(), montarConvenios(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.GPS.name(), montarGps(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DARFS.name(), montarDarfs(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.DARFS_SIMPLES.name(), montarDarfsSimples(notaExecucaoOrcamentaria));
        addTag(context, TipoModeloDoctoOficial.TagsNotaOrcamentaria.RECEITAS_EXTRAS.name(), montarReceitasExtras(notaExecucaoOrcamentaria));
    }

    private String montarPagamentos(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getPagamentos() != null && !notaExecucaoOrcamentaria.getPagamentos().isEmpty()) {
            retorno = " <table \"width=\"100%\" border=\"0\" >";
            for (NotaExecucaoOrcamentariaPagamento pagamento : notaExecucaoOrcamentaria.getPagamentos()) {
                retorno += " <tr>";
                retorno += " <td align=\"left\"> Número: " + pagamento.getNumero() + " </td>";
                retorno += " <td align=\"left\"> Data: " + DataUtil.getDataFormatada(pagamento.getDataPag()) + " </td>";
                retorno += " <td align=\"left\"> Histórico: " + pagamento.getHistorico() + " </td>";
                retorno += " <td align=\"right\"> Valor: " + nomeDoCampo(pagamento, "valor", TipoFormato.VALOR_REAIS) + " </td>";
                retorno += " </tr>";
            }
            retorno += "</table>";
        }
        return retorno;
    }

    private String montarRetencoes(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getRetencoes() != null && !notaExecucaoOrcamentaria.getRetencoes().isEmpty()) {
            int contador = 1;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (NotaExecucaoOrcamentariaRetencao retencao : notaExecucaoOrcamentaria.getRetencoes()) {
                retorno += retencao.getNumero() +
                    "        Data: " + DataUtil.getDataFormatada(retencao.getDataRet()) +
                    "        " + retencao.getContaExtra() +
                    "        Valor: " + nomeDoCampo(retencao, "valor", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getRetencoes().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
                valorTotal = valorTotal.add(retencao.getValor());
            }
            retorno += "</br>Total: " + Util.formataValor(valorTotal) + "</br>";
        }
        return retorno;
    }

    private String montarFaturas(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getFaturas() != null && !notaExecucaoOrcamentaria.getFaturas().isEmpty()) {
            int contador = 1;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (NotaExecucaoOrcamentariaFatura fatura : notaExecucaoOrcamentaria.getFaturas()) {
                retorno += " Tipo: " + fatura.getTipoGuia() +
                    "        Venc: " + DataUtil.getDataFormatada(fatura.getDataVencimento()) +
                    "        Cód. Barras:" + fatura.getCodigoBarra() +
                    "        Valor: " + nomeDoCampo(fatura, "valor", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getFaturas().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
                valorTotal = valorTotal.add(fatura.getValor());
            }
            retorno += "</br>Total: " + Util.formataValor(valorTotal) + "</br>";
        }
        return retorno;
    }

    private String montarConvenios(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getConvenios() != null && !notaExecucaoOrcamentaria.getConvenios().isEmpty()) {
            int contador = 1;
            for (NotaExecucaoOrcamentariaConvenio convenio : notaExecucaoOrcamentaria.getConvenios()) {
                retorno += " Tipo: " + convenio.getTipoGuia() +
                    "        Cód. Barras: " + convenio.getCodigoBarra();
                if (notaExecucaoOrcamentaria.getConvenios().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
            }
        }
        return retorno;
    }

    private String montarGps(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getGps() != null && !notaExecucaoOrcamentaria.getGps().isEmpty()) {
            int contador = 1;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (NotaExecucaoOrcamentariaGps gps : notaExecucaoOrcamentaria.getGps()) {
                retorno += " Tipo: " + gps.getTipoGuia() +
                    "        Compensação: " + DataUtil.getDataFormatada(gps.getPeriodoComp()) +
                    "        Código da Receita: " + gps.getCodigoTributo() + " - " + gps.getDigitoTributo() +
                    "        Valor: " + nomeDoCampo(gps, "valor", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getGps().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
                valorTotal = valorTotal.add(gps.getValor());
            }
            retorno += "</br>Total: " + Util.formataValor(valorTotal) + "</br>";
        }
        return retorno;
    }

    private String montarDarfs(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getDarfs() != null && !notaExecucaoOrcamentaria.getDarfs().isEmpty()) {
            int contador = 1;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (NotaExecucaoOrcamentariaDarf darf : notaExecucaoOrcamentaria.getDarfs()) {
                retorno += " Tipo: " + darf.getTipoGuia() +
                    "        Vencimento: " + DataUtil.getDataFormatada(darf.getDataVencimento()) +
                    "        Código da Receita: " + darf.getCodigoTributo() + " - " + darf.getCodigoIdentificador() +
                    "        Valor Principal: " + nomeDoCampo(darf, "valorPrincipal", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getDarfs().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
                valorTotal = valorTotal.add(darf.getValorPrincipal());
            }
            retorno += "</br>Total: " + Util.formataValor(valorTotal) + "</br>";
        }
        return retorno;
    }

    private String montarDarfsSimples(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getDarfsSimples() != null && !notaExecucaoOrcamentaria.getDarfsSimples().isEmpty()) {
            int contador = 1;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (NotaExecucaoOrcamentariaDarf darf : notaExecucaoOrcamentaria.getDarfsSimples()) {
                retorno += " Tipo: " + darf.getTipoGuia() +
                    "        Apuração: " + DataUtil.getDataFormatada(darf.getPeriodoApuracao()) +
                    "        Código da Receita: " + darf.getCodigoTributo() + " - " + darf.getCodigoIdentificador() +
                    "        Valor Principal: " + nomeDoCampo(darf, "valorPrincipal", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getDarfsSimples().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
                valorTotal = valorTotal.add(darf.getValorPrincipal());
            }
            retorno += "</br>Total: " + Util.formataValor(valorTotal) + "</br>";
        }
        return retorno;
    }

    private String montarReceitasExtras(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getReceitasExtras() != null && !notaExecucaoOrcamentaria.getReceitasExtras().isEmpty()) {
            BigDecimal valorTotal = BigDecimal.ZERO;
            retorno = " <table \"width=\"100%\" border=\"0\" >";
            for (NotaExecucaoOrcamentariaReceitaExtra receitaExtra : notaExecucaoOrcamentaria.getReceitasExtras()) {
                retorno += " <tr>";
                retorno += " <td align=\"left\"> Número: " + receitaExtra.getNumero() + " </td>";
                retorno += " <td align=\"left\"> Data: " + DataUtil.getDataFormatada(receitaExtra.getDataReceita()) + " </td>";
                retorno += " <td align=\"left\"> Histórico: " + receitaExtra.getHistorico() + " </td>";
                retorno += " <td align=\"left\"> Evento: " + receitaExtra.getEvento() + " </td>";
                retorno += " <td align=\"right\"> Valor: " + nomeDoCampo(receitaExtra, "valor", TipoFormato.VALOR_REAIS) + " </td>";
                retorno += " </tr>";
                valorTotal = valorTotal.add(receitaExtra.getValor());
            }
            retorno += "<tr><td colspan=\"5\" align=\"right\"> Total: " + Util.formataValor(valorTotal) + " </td></tr>";
            retorno += "</table>";
        }
        return retorno;
    }

    private String montarDocumentosComprobatorios(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getDocumentosComprobatorios() != null && !notaExecucaoOrcamentaria.getDocumentosComprobatorios().isEmpty()) {
            int contador = 1;
            for (NotaExecucaoOrcamentariaDocumentoComprobatorio documentoComprobatorio : notaExecucaoOrcamentaria.getDocumentosComprobatorios()) {
                retorno += " Número: " + documentoComprobatorio.getNumero() +
                    "   Data: " + DataUtil.getDataFormatada(documentoComprobatorio.getData()) +
                    "   Tipo: " + documentoComprobatorio.getTipo() +
                    (documentoComprobatorio.getUf() != null ? "   UF: " + documentoComprobatorio.getUf() : "") +
                    "   Valor: " + nomeDoCampo(documentoComprobatorio, "valor", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getDocumentosComprobatorios().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
            }
        }
        return retorno;
    }

    private String montarDetalhamentos(NotaExecucaoOrcamentaria notaExecucaoOrcamentaria) {
        String retorno = "";
        if (notaExecucaoOrcamentaria.getDetalhamentos() != null && !notaExecucaoOrcamentaria.getDetalhamentos().isEmpty()) {
            int contador = 1;
            for (NotaExecucaoOrcamentariaDetalhamento detalhamento : notaExecucaoOrcamentaria.getDetalhamentos()) {
                retorno += " Conta: " + detalhamento.getConta() + " - " + detalhamento.getDescricao() +
                    "   Evento: " + detalhamento.getEvento() +
                    "   Valor: " + nomeDoCampo(detalhamento, "valor", TipoFormato.VALOR_REAIS);
                if (notaExecucaoOrcamentaria.getDocumentosComprobatorios().size() > contador) {
                    retorno += "</br>";
                    contador++;
                }
            }
        }
        return retorno;
    }

    private void tagAssinatura(VelocityContext context, DocumentoOficial doc) {
        if (doc.getModeloDoctoOficial() != null && doc.getModeloDoctoOficial().getTipoDoctoOficial() != null) {
            doc.getModeloDoctoOficial().setTipoDoctoOficial(tipoDoctoOficialFacade.recuperar(doc.getModeloDoctoOficial().getTipoDoctoOficial().getId()));
            List<AssinaturaTipoDoctoOficial> assinaturas = doc.getModeloDoctoOficial().getTipoDoctoOficial().getAssinaturas();

            if (assinaturas != null && !assinaturas.isEmpty()) {
                for (AssinaturaTipoDoctoOficial assinaturaDocto : assinaturas) {
                    if (assinaturaDocto.getUsuarioSistema() != null && assinaturaDocto.isVigente()) {
                        AssinaturaUsuarioSistema assinatura = usuarioSistemaFacade.buscarAssinaturaUsuarioSistema(assinaturaDocto.getUsuarioSistema().getId());
                        String tag = StringUtil.removeCaracteresEspeciaisSemEspaco(assinaturaDocto.getUsuarioSistema().getLogin());
                        addTag(context, tag, montarStringAssinaturaUsuario(assinatura));
                    }
                }
            }
        }
    }

    private String montarStringAssinaturaUsuario(AssinaturaUsuarioSistema aus) {
        String assinatura = null;
        if (aus != null && aus.getDetentorArquivoComposicao() != null && aus.getDetentorArquivoComposicao().getArquivoComposicao() != null && aus.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getByteArrayDosDados() != null) {
            byte[] bytes = aus.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getByteArrayDosDados();
            assinatura = "<img src='" + "data:image/png;base64," + (new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8)) + "' width='100' height='100' style='width: 100px !important; height: 100px !important;'/>";
        }
        return assinatura;
    }

    private void tagsLicencaETR(VelocityContext context, RequerimentoLicenciamentoETR requerimento) {
        addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.PROTOCOLO.name(), requerimento.getNumeroAnoProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.REQUERENTE.name(), requerimento.getCadastroEconomico().getPessoa().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.CNPJ_REQUERENTE.name(), requerimento.getCadastroEconomico().getPessoa().getCpf_Cnpj());

        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(requerimento.getCadastroEconomico().getId());
        if (!cadastroEconomico.getSociedadeCadastroEconomico().isEmpty()) {
            addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.REQUERENTE_TECNICO.name(), cadastroEconomico.getSociedadeCadastroEconomico().get(0).getSocio().getNome());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.REQUERENTE_TECNICO.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.REQUERENTE_TECNICO_EXECUCAO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsLicencaETR.ENDERECO_REQUERIMENTO.name(), requerimento.getEnderecoInstalacao().getEnderecoCompleto());
    }

    private void tagsHabiteseConstrucao(VelocityContext context, Habitese habitese) {
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.CODIGO.name(), habitese.getCodigo());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.EXERCICIO.name(), habitese.getExercicio());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.USUARIO.name(), habitese.getUsuarioSistema().getLogin());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.SITUACAO.name(), habitese.getSituacao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.DATA_EXPEDICAO.name(), DataUtil.getDataFormatada(habitese.getDataExpedicaoTermo()));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.DATA_VENCIMENTO_ISS.name(), DataUtil.getDataFormatada(habitese.getDataVencimentoISS()));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.DATA_VISTORIA.name(), DataUtil.getDataFormatada(habitese.getDataVistoria()));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.NUMERO_PROTOCOLO.name(), habitese.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.ANO_PROTOCOLO.name(), habitese.getAnoProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.CARACTERISTICAS_CONSTRUCAO.name(), montarTabelaCaracteristicasConstrucaoHabitese(habitese));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.SERVICOS.name(), montarTabelaServicosHabitese(habitese));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.DEDUCOES.name(), montarTabelaDeducoesHabitese(habitese));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.ISSQN.name(), montarTabelaISSQNHabitese(habitese));
        addTag(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.EXERCICIO_PROCESSO.name(), habitese.getExercicio().getAno());
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(),
                !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                    gerarQRCode("habitese-construcao", habitese.getId(), configuracaoTributario) : "");
        }
        ParametroRegularizacao parametroRegularizacao = alvaraConstrucaoFacade.getParametroRegularizacaoFacade()
            .buscarParametroRegularizacaoPorExercicio(exercicioFacade.getExercicioCorrente());
        if (parametroRegularizacao != null) {
            addTagAssinatura(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.ASSINATURA_SECRETARIO.name(),
                parametroRegularizacao.getAssinaturaSecretario());
            addTagAssinatura(context, TipoModeloDoctoOficial.TagsHabiteseConstrucao.ASSINATURA_DIRETOR.name(),
                parametroRegularizacao.getAssinaturaDiretor());
        }
    }

    private void tagsProcessoDeProtesto(VelocityContext context, ProcessoDeProtesto processoDeProtesto) {
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.EXERCICIO.name(), processoDeProtesto.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.NUMERO.name(), processoDeProtesto.getCodigo());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.PROTOCOLO.name(), processoDeProtesto.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.DATA.name(), DataUtil.getDataFormatada(processoDeProtesto.getLancamento()));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.USUARIO.name(), processoDeProtesto.getUsuarioIncluiu());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.SITUACAO.name(), processoDeProtesto.getSituacao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.ATO_LEGAL.name(), processoDeProtesto.getAtoLegal());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.MOTIVO.name(), processoDeProtesto.getMotivo());
        addTag(context, TipoModeloDoctoOficial.TagsProcessoDeProtesto.DEBITOS.name(), montarListaParcelasProcessoDeProtesto(processoDeProtesto));
    }

    private String montarListaParcelasProcessoDeProtesto(ProcessoDeProtesto processoDeProtesto) {
        List<Long> idsParcelas = Lists.newArrayList();

        for (ItemProcessoDeProtesto item : processoDeProtesto.getItens()) {
            idsParcelas.add(item.getParcela().getId());
        }

        if (!idsParcelas.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelas);
            consultaParcela.executaConsulta();
            return montarTabelaProcessoDeProtesto(consultaParcela.getResultados());
        } else {
            return "";
        }
    }

    private void tagsProcessoRegularizacaoConstrucao(VelocityContext context, ProcRegularizaConstrucao procRegularizaConstrucao) {
        procRegularizaConstrucao = em.find(ProcRegularizaConstrucao.class, procRegularizaConstrucao.getId());
        if (procRegularizaConstrucao.getResponsavelProjeto() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.CAU_CREA_AUTOR_PROJETO.name(),
                procRegularizaConstrucao.getResponsavelProjeto().getCauCrea());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.CAU_CREA_AUTOR_PROJETO.name(),
                "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.ART_RRT_PROJETO.name(),
            procRegularizaConstrucao.getArtRrtAutorProjeto());
        if (procRegularizaConstrucao.getResponsavelExecucao() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.CAU_CREA_RESP_EXECUCAO.name(),
                procRegularizaConstrucao.getResponsavelExecucao().getCauCrea());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.CAU_CREA_RESP_EXECUCAO.name(),
                "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsProcessoRegularizacaoConstrucao.ART_RRT_EXECUCAO.name(),
            procRegularizaConstrucao.getArtRrtResponsavelExecucao());
    }

    private void tagsAlvaraConstrucao(VelocityContext context, AlvaraConstrucao alvaraConstrucao) {
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CODIGO.name(), alvaraConstrucao.getCodigo());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.EXERCICIO.name(), alvaraConstrucao.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.USUARIO.name(), alvaraConstrucao.getUsuarioIncluiu() != null ? alvaraConstrucao.getUsuarioIncluiu().getLogin() : "");
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.SITUACAO.name(), alvaraConstrucao.getSituacao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.DATA_EXPEDICAO.name(), DataUtil.getDataFormatada(alvaraConstrucao.getDataExpedicao()));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.DATA_VENCIMENTO_CARTAZ.name(), DataUtil.getDataFormatada(alvaraConstrucao.getDataVencimentoCartaz()));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.DATA_VENCIMENTO_DEBITO.name(), DataUtil.getDataFormatada(alvaraConstrucao.getDataVencimentoDebito()));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.NUMERO_PROTOCOLO.name(), alvaraConstrucao.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ANO_PROTOCOLO.name(), alvaraConstrucao.getAnoProtocolo());

        SolicitacaoAlvaraImediato solicitacaoAlvaraImediato = alvaraConstrucaoFacade.buscarSolicitacaoImediato(alvaraConstrucao.getProcRegularizaConstrucao());
        if (solicitacaoAlvaraImediato != null) {
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.RESPONSAVEL_OBRA.name(), solicitacaoAlvaraImediato.getAutorProjeto() != null ? solicitacaoAlvaraImediato.getAutorProjeto().toString() : "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.RESPONSAVEL_EXECUCAO.name(), solicitacaoAlvaraImediato.getResponsavelExecucao() != null ? solicitacaoAlvaraImediato.getResponsavelExecucao().toString() : "");

            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_A_CONSTRUIR.name(), Util.formataValorSemCifrao(solicitacaoAlvaraImediato.getAreaConstruir()));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_A_DEMOLIR.name(), Util.formataValorSemCifrao(solicitacaoAlvaraImediato.getAreaDemolir()));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_TOTAL.name(), Util.formataValorSemCifrao(solicitacaoAlvaraImediato.getAreaTotal()));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.NUMERO_PAVIMENTOS.name(), solicitacaoAlvaraImediato.getNumeroPavimentos());
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.MATRICULA_OBRA_INSS.name(), solicitacaoAlvaraImediato.getCei());
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CLASSIFICACAO_USO.name(), solicitacaoAlvaraImediato.getClassificacaoUsoItem().toString());
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CATEGORIA_USO.name(), solicitacaoAlvaraImediato.getClassificacaoUso().toString());
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ZONA.name(), solicitacaoAlvaraImediato.getZona().toString());
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.OBSERVACAO.name(), solicitacaoAlvaraImediato.getObservacao());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.RESPONSAVEL_OBRA.name(), alvaraConstrucao.getResponsavelServico() != null ? alvaraConstrucao.getResponsavelServico().toString() : "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.RESPONSAVEL_EXECUCAO.name(), alvaraConstrucao.getResponsavelServico() != null ? alvaraConstrucao.getResponsavelServico().toString() : "");

            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_A_CONSTRUIR.name(), Util.formataValorSemCifrao(BigDecimal.ZERO));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_A_DEMOLIR.name(), Util.formataValorSemCifrao(BigDecimal.ZERO));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.AREA_TOTAL.name(), Util.formataValorSemCifrao(BigDecimal.ZERO));
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.NUMERO_PAVIMENTOS.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.MATRICULA_OBRA_INSS.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CLASSIFICACAO_USO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CATEGORIA_USO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ZONA.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.OBSERVACAO.name(), "");
        }

        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.SERVICO.name(), !alvaraConstrucao.getServicos().isEmpty() ? alvaraConstrucao.getServicos().get(0).getServicoConstrucao().toString() : "");
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ITEM_SERVICO.name(), !alvaraConstrucao.getServicos().isEmpty() ? alvaraConstrucao.getServicos().get(0).getItemServicoConstrucao().toString() : "");

        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.PROCESSO_REGULARIZACAO.name(), alvaraConstrucao.getProcRegularizaConstrucao().toString());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.PROCESSO_REGULARIZACAO_ANO_PROTOCOLO.name(), alvaraConstrucao.getProcRegularizaConstrucao().getAnoProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.PROCESSO_REGULARIZACAO_NUMERO_PROTOCOLO.name(), alvaraConstrucao.getProcRegularizaConstrucao().getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.SERVICOS.name(), montarTabelaServicosAlvara(alvaraConstrucao));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.CALCULO.name(), montarTabelaCalculoAlvara(alvaraConstrucao));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.DETALHES_CALCULO.name(), montarTabelaDetalhesCalculoAlvara(alvaraConstrucao));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.DAM.name(), montarTabelaDamGenerica(alvaraConstrucaoFacade.buscarParcelas(alvaraConstrucao)));
        addTag(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.EXERCICIO_PROCESSO.name(), alvaraConstrucao.getExercicio().getAno());
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(),
                !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                    gerarQRCode("alvara-construcao", alvaraConstrucao.getId(), configuracaoTributario) : "");
        }
        ParametroRegularizacao parametroRegularizacao = alvaraConstrucaoFacade.getParametroRegularizacaoFacade()
            .buscarParametroRegularizacaoPorExercicio(exercicioFacade.getExercicioCorrente());
        if (parametroRegularizacao != null) {
            addTagAssinatura(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ASSINATURA_SECRETARIO.name(),
                parametroRegularizacao.getAssinaturaSecretario());
            addTagAssinatura(context, TipoModeloDoctoOficial.TagsAlvaraConstrucao.ASSINATURA_DIRETOR.name(),
                parametroRegularizacao.getAssinaturaDiretor());
        }
    }

    private void addTagAssinatura(VelocityContext context, String tag, Arquivo arquivo) {
        String imagemAssinatura = "";
        if (arquivo != null) {
            arquivo = arquivoFacade.recupera(arquivo.getId());
            imagemAssinatura = "<img src=\"data:image/png;base64," + Util.getBase64Encode(arquivo.getByteArrayDosDados()) + "\" alt=\"Assinatura\" style=\"width: 150px !important; height: 150px !important;\" />";
        }
        addTag(context, tag, imagemAssinatura);
    }

    private void tagQrCode(VelocityContext context, DocumentoOficial doc) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (!context.containsKey(TipoModeloDoctoOficial.TagsGerais.QRCODE.name())) {
            addTag(context, TipoModeloDoctoOficial.TagsGerais.QRCODE.name(), gerarQRCode("validar-documento-oficial", doc.getCodigoVerificacao(), configuracaoTributario));
        }
    }

    public void tagsAuxilioFuneral(VelocityContext context, AuxilioFuneral auxilioFuneral) throws UFMException {
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.NOME_RESPONSAVEL.name(), nomeDoCampo(auxilioFuneral.getNomeResponsavelSolicitacaoSep(), "nomeResponsavelSolicitacaoSep", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.ENDERECO_RESPONSAVEL.name(), nomeDoCampo(auxilioFuneral.getEnderecoResponsavelSolicSep(), "enderecoResponsavelSolicSep", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.RG_RESPONSAVEL.name(), nomeDoCampo(auxilioFuneral.getRgResponsavelSolicitacaoSep(), "rgResponsavelSolicitacaoSep", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.ESTADO_CIVIL_RESPONSAVEL.name(), nomeDoCampo(auxilioFuneral.getEstadoCivilResponsavelSolicSep(), "estadoCivilResponsavelSolicSep", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.CPF_RESPONSAVEL.name(), nomeDoCampo(auxilioFuneral.getRgResponsavelBeneficio(), "rgResponsavelBeneficio", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.BENEFICIO_RENUNCIADO_DECLARACAO_BENEFICIO.name(), nomeDoCampo(auxilioFuneral.getTipoBeneficioRenunciado(), "tipoBeneficioRenunciado", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.NOME_FALECIDO.name(), nomeDoCampo(auxilioFuneral.getNomeFalecido(), "nomeFalecido", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.HORA_REQUISICAO.name(), nomeDoCampo(auxilioFuneral.getDataHoraRequisicao(), "dataHoraRequisicao", TipoFormato.HORA_MINUTO));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.DATA_REQUISICAO.name(), nomeDoCampo(auxilioFuneral.getDataHoraRequisicao(), "dataHoraRequisicao", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.CEMITERIO.name(), nomeDoCampo(auxilioFuneral.getCemiterio(), "cemiterio", null));
        addTag(context, TipoModeloDoctoOficial.TagsAuxilioFuneral.FUNERARIA.name(), nomeDoCampo(auxilioFuneral.getFuneraria(), "funeraria.descricao", null));
    }

    private void tagsCertidaoBaixaAtividade(VelocityContext context, CertidaoAtividadeBCE certidaoAtividadeBCE) {
        addTag(context, TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.SITUACAO_BAIXA_BCE.name(), certidaoAtividadeBCE.getSituacao().getDescricaoCertidao());
        addTag(context, TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.DATA_BAIXA_BCE.name(), nomeDoCampo(certidaoAtividadeBCE.getDataCadastro(), "dataCadastro", TipoFormato.DATA));
        addTag(context, TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.DATA_BAIXA_BCE_EXTENSO.name(), new SimpleDateFormat("EEEE, d' de 'MMMM' de 'yyyy").format(certidaoAtividadeBCE.getDataCadastro()));
        addTag(context, TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.PROTOCOLO_BAIXA_BCE.name(), nomeDoCampo(certidaoAtividadeBCE.getNumeroAnoProtocolo(), "numeroAnoProtocolo", null));
        addTag(context, TipoModeloDoctoOficial.TagsCertidaoBaixaAtividade.MOTIVO_BAIXA_BCE.name(), nomeDoCampo(certidaoAtividadeBCE.getMotivo(), "motivo", null));
    }

    private void tagsRBTransCertificadoOTT(VelocityContext context, CertificadoAnualOTT certificado) {
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.EXERCICIO.name(), certificado.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.RAZAO_SOCIAL_OTT.name(), certificado.getOperadoraTecTransporte().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.VENCIMENTO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getVencimento()));
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.CNPJ_OTT.name(), certificado.getOperadoraTecTransporte().getCnpj());
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.DATA_EMISSAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getDataEmissao()));
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.HORA_EMISSAO.name(), new SimpleDateFormat("HH:mm").format(certificado.getDataEmissao()));
        addTag(context, TipoModeloDoctoOficial.TagsCertificadoOTTRBTrans.NOME_REPRESENTANTE.name(), certificado.getOperadoraTecTransporte().getNomeResponsavel());

        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.QR_CODE.name(),
                !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                    gerarQRCode(caminhoOtt, certificado.getOperadoraTecTransporte().getId(), configuracaoTributario) : "");
        }
    }

    private void tagsRBTransCertificadoOTT(VelocityContext context, CertificadoCondutorOTT certificado) {
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.NOME_CONDUTOR.name(), certificado.getCondutorOperaTransporte().getNomeCondutor());
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.CPF_CONDUTOR.name(), certificado.getCondutorOperaTransporte().getCpf());
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_CONDUTOR.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getVencimento()));
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.DATA_EMISSAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getDataEmissao()));
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.HORA_EMISSAO.name(), new SimpleDateFormat("HH:mm").format(certificado.getDataEmissao()));
        if (certificado.getVeiculoOtt() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.MARCA_VEICULO.name(), certificado.getVeiculoOtt().getMarca().getDescricao());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.MODELO_VEICULO.name(), certificado.getVeiculoOtt().getModeloVeiculo());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.PLACA_VEICULO.name(), certificado.getVeiculoOtt().getPlacaVeiculo());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.PLACA_VEICULO.name(), certificado.getVeiculoOtt().getPlacaVeiculo());

            CertificadoRenovacaoOTT certificadorenovacao = veiculoOperadoraTecnologiaTransporteFacade.buscarCertificadoMaisRecenteVeiculo(certificado.getVeiculoOtt().getId());
            if (certificadorenovacao != null && certificadorenovacao.getVencimento() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_VEICULO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificadorenovacao.getVencimento()));
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_VEICULO.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VEICULO_ALUGADO.name(), certificado.getVeiculoOtt().getAlugado() ? "Sim" : "Não");
        }
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.RAZAO_SOCIAL_OTT.name(), certificado.getCondutorOperaTransporte().getOperadoraTecTransporte().getNome());

        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.QR_CODE.name(),
                !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) ?
                    gerarQRCode(caminhoCondutorOtt, certificado.getCondutorOperaTransporte().getId(), configuracaoTributario) : "");
        }
    }

    private void tagsRBTransCertificadoOTT(VelocityContext context, CertificadoRenovacaoOTT certificado) {
        if (certificado.getCondutorOtt() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.NOME_CONDUTOR.name(), certificado.getCondutorOtt().getNomeCondutor());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.CPF_CONDUTOR.name(), certificado.getCondutorOtt().getCpf());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.RAZAO_SOCIAL_OTT.name(), certificado.getCondutorOtt().getOperadoraTecTransporte().getNome());
        }
        if (certificado.getVeiculoOtt() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.MARCA_VEICULO.name(), certificado.getVeiculoOtt().getMarca().getDescricao());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.MODELO_VEICULO.name(), certificado.getVeiculoOtt().getModeloVeiculo());
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.PLACA_VEICULO.name(), certificado.getVeiculoOtt().getPlacaVeiculo());

            CertificadoRenovacaoOTT certificadorenovacao = veiculoOperadoraTecnologiaTransporteFacade.buscarCertificadoMaisRecenteVeiculo(certificado.getVeiculoOtt().getId());
            if (certificadorenovacao != null && certificadorenovacao.getVencimento() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_VEICULO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificadorenovacao.getVencimento()));
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_VEICULO.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VEICULO_ALUGADO.name(), certificado.getVeiculoOtt().getAlugado() ? "Sim" : "Não");
        }
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.VENCIMENTO_CERTIFICADO_CONDUTOR.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getVencimento()));
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.DATA_EMISSAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(certificado.getDataEmissao()));
        addTag(context, TipoModeloDoctoOficial.TagsCondutorOTTRBTrans.HORA_EMISSAO.name(), new SimpleDateFormat("HH:mm").format(certificado.getDataEmissao()));
    }

    private String gerarQRCode(String caminho, Long id, ConfiguracaoTributario config) {
        String concatenacaminho = Strings.isNullOrEmpty(caminho) ? "" : caminho + "/";
        String conteudo = config.getUrlPortalContribuinte() + complementoURL + concatenacaminho + id;
        try {
            return "<img src='" + "data:image/png;base64," + BarCode.generateBase64QRCodePng(conteudo, 100, 100) + "' alt='QR CODE' width='100' height='100' style='width: 100px !important; height: 100px !important;'/>";
        } catch (IOException e) {
            logger.error("Erro ao gerar qrcode {}", e);
        }
        return "";
    }

    private String gerarQRCode(String caminho, String valor, ConfiguracaoTributario config) {
        String concatenacaminho = Strings.isNullOrEmpty(caminho) ? "" : caminho + "/";
        String conteudo = config.getUrlPortalContribuinte() + complementoURL + concatenacaminho + valor;
        try {
            return "<img src='" + "data:image/png;base64," + BarCode.generateBase64QRCodePng(conteudo, 100, 100) + "' alt='QR CODE' width='100' height='100' style='width: 100px !important; height: 100px !important;'/>";
        } catch (IOException e) {
            logger.error("Erro ao gerar qrcode {}", e);
        }
        return "";
    }

    private void tagsSubvencao(VelocityContext context, SubvencaoEmpresas subvencaoEmpresa) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_NUMERO.name(), subvencaoEmpresa.getSubvencaoProcesso().getNumeroDoProcesso());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_EXERCICIO.name(), subvencaoEmpresa.getSubvencaoProcesso().getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_REFERENCIA.name(), subvencaoEmpresa.getSubvencaoProcesso().getMes().getNumeroMes());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_DATA_LANCAMENTO.name(), DataUtil.getDataFormatada(subvencaoEmpresa.getSubvencaoProcesso().getDataLancamento(), "dd/MM/yyyy"));
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_TIPO.name(), subvencaoEmpresa.getSubvencaoProcesso().getTipoPassageiro().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PASSAGEIROS_TRANSPORTADOS.name(), subvencaoEmpresa.getSubvencaoProcesso().getQtdeAlunosTransportados());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_PASSAGEM_POR_PASSAGEIRO.name(), dfReais.format(subvencaoEmpresa.getSubvencaoProcesso().getValorPassagem()));
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PERCENTUAL_SUBVENCAO.name(), dfReais.format(subvencaoEmpresa.getSubvencaoProcesso().getPercentualSubvencao()));


        CadastroEconomico devedor = cadastroEconomicoFacade.recuperar(subvencaoEmpresa.getEmpresaDevedora().getId());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_RAZAO_SOCIAL.name(), devedor.getPessoa().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_CNPJ.name(), devedor.getPessoa().getCpf_Cnpj());

        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_LOGRADOURO_ENDERECO.name(), devedor.getLocalizacao().getLogradouro());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_NUMERO_ENDERECO.name(), devedor.getLocalizacao().getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_COMPLEMENTO_ENDERECO.name(), devedor.getLocalizacao().getComplemento());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_BAIRRO_ENDERECO.name(), devedor.getLocalizacao().getBairro());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_CEP.name(), devedor.getLocalizacao().getCep());
        if (configuracaoTributario != null && configuracaoTributario.getCidade() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_MUNICIPIO.name(), configuracaoTributario.getCidade().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_UF.name(), configuracaoTributario.getCidade().getUf().getNome());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_MUNICIPIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_UF.name(), "");
        }
        if (devedor.getPessoa().getTelefonePrincipal() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_TELEFONE.name(), devedor.getPessoa().getTelefonePrincipal().getTelefone());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DEVEDOR_TELEFONE.name(), "");
        }

        CadastroEconomico credor = cadastroEconomicoFacade.recuperar(subvencaoEmpresa.getCadastroEconomico().getId());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_RAZAO_SOCIAL.name(), credor.getPessoa().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_CNPJ.name(), credor.getPessoa().getCpf_Cnpj());

        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_LOGRADOURO_ENDERECO.name(), credor.getLocalizacao().getLogradouro());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_NUMERO_ENDERECO.name(), credor.getLocalizacao().getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_COMPLEMENTO_ENDERECO.name(), credor.getLocalizacao().getComplemento());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_BAIRRO_ENDERECO.name(), credor.getLocalizacao().getBairro());
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_CEP.name(), credor.getLocalizacao().getCep());
        if (configuracaoTributario != null && configuracaoTributario.getCidade() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_MUNICIPIO.name(), configuracaoTributario.getCidade().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_UF.name(), configuracaoTributario.getCidade().getUf().getNome());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_MUNICIPIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_UF.name(), "");
        }
        if (credor.getPessoa().getTelefonePrincipal() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_TELEFONE.name(), credor.getPessoa().getTelefonePrincipal().getTelefone());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CREDOR_TELEFONE.name(), "");
        }

        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_TOTAL_CREDITO_COMPENSAR.name(), dfReais.format(subvencaoEmpresa.getValorSubvencao()));
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_CREDITO_UTILIZADO_SEM_HONORARIOS.name(), dfReais.format(subvencaoEmpresa.getValorSubvencionado().subtract(subvencaoEmpresa.totalHonorarios())));
        addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_TOTAL_HONORARIOS.name(), dfReais.format(subvencaoEmpresa.totalHonorarios()));
        if (subvencaoEmpresa.getBloqueioOutorga() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_DE_BLOQUEIO.name(), subvencaoEmpresa.getBloqueioOutorga().getCodigo());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_BLOQUEADO_POR_PROCESSO.name(), dfReais.format(subvencaoEmpresa.getBloqueioOutorga().totalBloqueado()));
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.EXERCICIO_DO_PROCESSO_DE_BLOQUEIO.name(), subvencaoEmpresa.getBloqueioOutorga().getExercicio());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.PROCESSO_DE_BLOQUEIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.VALOR_BLOQUEADO_POR_PROCESSO.name(), dfReais.format(0));
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.EXERCICIO_DO_PROCESSO_DE_BLOQUEIO.name(), "");
        }
        List<Long> idsParcelas = Lists.newArrayList();
        for (SubvencaoParcela parcela : subvencaoEmpresa.getSubvencaoParcela()) {
            idsParcelas.add(parcela.getParcelaValorDivida().getId());
        }
        if (!idsParcelas.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelas);
            consultaParcela.executaConsulta();
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.LISTA_DEBITOS_SUBVENCIONADOS.name(), montarTabelaDebitosSubvencionados(consultaParcela.getResultados()));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.LISTA_DEBITOS_SUBVENCIONADOS.name(), "");
        }

        SubvencaoParametro subvencaoParametro = subvencaoParametroFacade.retornarParametro();
        if (subvencaoParametro != null) {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.NOME_PRIMEIRO_ASSINANTE.name(), subvencaoParametro.getPrimeiroAssinante());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CARGO_PRIMEIRO_ASSINANTE.name(), subvencaoParametro.getPrimeiroCargo());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DECRETO_PRIMEIRO_ASSINANTE.name(), subvencaoParametro.getPrimeiroDecreto());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.NOME_SEGUNDO_ASSINANTE.name(), subvencaoParametro.getSegundoAssinante());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CARGO_SEGUNDO_ASSINANTE.name(), subvencaoParametro.getSegundoCargo());
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DECRETO_SEGUNDO_ASSINANTE.name(), subvencaoParametro.getSegundoDecreto());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.NOME_PRIMEIRO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CARGO_PRIMEIRO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DECRETO_PRIMEIRO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.NOME_SEGUNDO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.CARGO_SEGUNDO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DECRETO_SEGUNDO_ASSINANTE.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsSubvencao.DECRETO_SEGUNDO_ASSINANTE.name(), "");
        }
    }

    private void tagsOrdemGeralMonitoramento(VelocityContext context, OrdemGeralMonitoramento ordemGeralMonitoramento) {
        if (ordemGeralMonitoramento != null) {
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.SITUACAO_DA_ORDEM.name(), ordemGeralMonitoramento.getSituacaoOGM().getDescricao());
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.DATA_PROGRAMADA_DE.name(), DataUtil.getDataFormatada(ordemGeralMonitoramento.getDataInicial(), "dd/MM/yyyy"));
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.DATA_PROGRAMADA_ATE.name(), DataUtil.getDataFormatada(ordemGeralMonitoramento.getDataFinal(), "dd/MM/yyyy"));
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.USUARIO_LOGADO.name(), ordemGeralMonitoramento.getUsuarioLogado());
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.DATA_CRIACAO.name(), DataUtil.getDataFormatada(ordemGeralMonitoramento.getDataCriacao(), "dd/MM/yyyy"));
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.AUDITOR_FISCAL_DA_ORDEM.name(), ordemGeralMonitoramento.getAuditorFiscal());
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.DESCRICAO.name(), ordemGeralMonitoramento.getDescricao());
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.NUMERO_PROTOCOLO.name(), ordemGeralMonitoramento.getNumeroProtocolo());
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.ANO_PROTOCOLO.name(), ordemGeralMonitoramento.getAnoProtocolo());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.NUMERO_PROTOCOLO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsOrdemGeralMonitoramento.ANO_PROTOCOLO.name(), "");
        }
    }

    private String tagsProcessoParcelamento(VelocityContext context, List<ProcessoParcelamento> parcelamentos, String conteudo) {
        BigDecimal parcelarOriginal = BigDecimal.ZERO;
        BigDecimal parcelarJuros = BigDecimal.ZERO;
        BigDecimal parcelarMulta = BigDecimal.ZERO;
        BigDecimal parcelarCorrecao = BigDecimal.ZERO;
        BigDecimal parcelarHonorarios = BigDecimal.ZERO;

        BigDecimal descontoOriginal = BigDecimal.ZERO;
        BigDecimal descontoMulta = BigDecimal.ZERO;
        BigDecimal descontoJuros = BigDecimal.ZERO;
        BigDecimal descontoCorrecao = BigDecimal.ZERO;
        BigDecimal descontoHonorarios = BigDecimal.ZERO;

        StringBuilder numeroParcelamento = new StringBuilder();
        StringBuilder anoParcelamento = new StringBuilder();
        StringBuilder numeroAnoParcelamento = new StringBuilder();
        StringBuilder dataParcelamento = new StringBuilder();

        BigDecimal valorParceladoOriginal = BigDecimal.ZERO;
        BigDecimal valorParceladoJuros = BigDecimal.ZERO;
        BigDecimal valorParceladoMulta = BigDecimal.ZERO;
        BigDecimal valorParceladoCorrecao = BigDecimal.ZERO;
        BigDecimal valorParceladoHonorarios = BigDecimal.ZERO;
        BigDecimal valorTotalParcelado = BigDecimal.ZERO;

        StringBuilder percentualEntrada = new StringBuilder();
        StringBuilder percentualEntradaExtenso = new StringBuilder();
        StringBuilder quantidadeParcelas = new StringBuilder();
        StringBuilder vencimentoPrimeiraParcela = new StringBuilder();

        BigDecimal valorParcelas = BigDecimal.ZERO;
        BigDecimal valorEntrada = BigDecimal.ZERO;
        BigDecimal valorTotalAParcelar = BigDecimal.ZERO;
        BigDecimal totalGeralAParcelar = BigDecimal.ZERO;

        for (int i = 0; i < parcelamentos.size(); i++) {

            ProcessoParcelamento parcelamentoDaVez = parcelamentos.get(i);

            for (ItemProcessoParcelamento item : parcelamentoDaVez.getItensProcessoParcelamento()) {
                parcelarOriginal = parcelarOriginal.add(item.getImposto().add(item.getTaxa()));
                parcelarJuros = parcelarJuros.add(item.getJuros());
                parcelarMulta = parcelarMulta.add(item.getMulta());
                parcelarCorrecao = parcelarCorrecao.add(item.getCorrecao());
                parcelarHonorarios = parcelarHonorarios.add(item.getHonorarios());
            }

            descontoOriginal = descontoOriginal.add(parcelarOriginal.subtract(parcelamentoDaVez.getValorTotalImposto().add(parcelamentoDaVez.getValorTotalTaxa())));
            descontoMulta = descontoMulta.add(parcelarMulta.subtract(parcelamentoDaVez.getValorTotalMulta()));
            descontoJuros = descontoJuros.add(parcelarJuros.subtract(parcelamentoDaVez.getValorTotalJuros()));
            descontoCorrecao = descontoCorrecao.add(parcelarCorrecao.subtract(parcelamentoDaVez.getValorTotalCorrecao()));
            descontoHonorarios = descontoHonorarios.add(parcelarHonorarios.subtract(parcelamentoDaVez.getValorTotalHonorarios()));

            parcelamentoDaVez.definirNumeroProcesso();

            numeroAnoParcelamento.append(nomeDoCampo(parcelamentoDaVez, "numeroComposto", null)).append("/").append(nomeDoCampo(parcelamentoDaVez, "exercicio.ano", null));
            if ((i + 1) != parcelamentos.size()) numeroAnoParcelamento.append(", ");

            numeroParcelamento.append(nomeDoCampo(parcelamentoDaVez, "numeroComposto", null));
            if ((i + 1) != parcelamentos.size()) numeroParcelamento.append(", ");

            anoParcelamento.append(nomeDoCampo(parcelamentoDaVez, "exercicio.ano", null));
            if ((i + 1) != parcelamentos.size()) anoParcelamento.append(", ");

            dataParcelamento.append(nomeDoCampo(parcelamentoDaVez, "dataProcessoParcelamento", TipoFormato.DATA));
            if ((i + 1) != parcelamentos.size()) dataParcelamento.append(", ");

            valorParceladoOriginal = valorParceladoOriginal.add(parcelamentoDaVez.getTotalOriginal());
            valorParceladoJuros = valorParceladoJuros.add(parcelamentoDaVez.getValorTotalJuros());
            valorParceladoMulta = valorParceladoMulta.add(parcelamentoDaVez.getValorTotalMulta());
            valorParceladoCorrecao = valorParceladoCorrecao.add(parcelamentoDaVez.getValorTotalCorrecao());
            valorParceladoHonorarios = valorParceladoHonorarios.add(parcelamentoDaVez.getValorTotalHonorariosAtualizado());
            valorTotalParcelado = valorTotalParcelado.add(parcelamentoDaVez.getTotalGeral());

            percentualEntrada.append(decimalFormat.format(parcelamentoDaVez.getPercentualEntrada())).append("%");
            if ((i + 1) != parcelamentos.size()) percentualEntrada.append(", ");
            percentualEntradaExtenso.append(ValorPorExtenso.valorPorExtenso(parcelamentoDaVez.getPercentualEntrada(), false)).append(" por cento");
            if ((i + 1) != parcelamentos.size()) percentualEntradaExtenso.append(", ");
            quantidadeParcelas.append(parcelamentoDaVez.getNumeroParcelas());
            if ((i + 1) != parcelamentos.size()) quantidadeParcelas.append(", ");
            vencimentoPrimeiraParcela.append(nomeDoCampo(parcelamentoDaVez, "vencimentoPrimeiraParcela", TipoFormato.DATA));
            if ((i + 1) != parcelamentos.size()) vencimentoPrimeiraParcela.append(", ");

            valorEntrada = valorEntrada.add(parcelamentoDaVez.getValorEntrada());
            valorParcelas = valorParcelas.add(parcelamentoDaVez.getValorParcela());
            valorTotalAParcelar = valorTotalAParcelar.add(parcelamentoDaVez.getTotalGeral());
            totalGeralAParcelar = parcelarOriginal.add(parcelarJuros).add(parcelarMulta.add(parcelarCorrecao.add(parcelarHonorarios)));
        }

        if (parcelamentos.size() > 1) {
            String tagNumeroAno = "$" + TipoModeloDoctoOficial.TagsProcessoParcelamento.NUMERO_PARCELAMENTO.name() + "/$" + TipoModeloDoctoOficial.TagsProcessoParcelamento.ANO_PARCELAMENTO.name();
            conteudo = conteudo.replace(tagNumeroAno, numeroAnoParcelamento.toString());
        }

        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.NUMERO_PARCELAMENTO.name(), numeroParcelamento);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.ANO_PARCELAMENTO.name(), anoParcelamento);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.DATA_PARCELAMENTO.name(), dataParcelamento);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_A_PARCELAR_ORIGINAL.name(), decimalFormat.format(parcelarOriginal));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_A_PARCELAR_JUROS.name(), decimalFormat.format(parcelarJuros));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_A_PARCELAR_MULTA.name(), decimalFormat.format(parcelarMulta));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_A_PARCELAR_CORRECAO_MONETARIA.name(), decimalFormat.format(parcelarCorrecao));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_A_PARCELAR_HONORARIOS.name(), decimalFormat.format(parcelarHonorarios));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_DESCONTO_ORIGINAL.name(), decimalFormat.format(descontoOriginal));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_DESCONTO_JUROS.name(), decimalFormat.format(descontoJuros));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_DESCONTO_MULTA.name(), decimalFormat.format(descontoMulta));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_DESCONTO_CORRECAO_MONETARIA.name(), decimalFormat.format(descontoCorrecao));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_DESCONTO_HONORARIOS.name(), decimalFormat.format(descontoHonorarios));

        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELADO_ORIGINAL.name(), decimalFormat.format(valorParceladoOriginal));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELADO_JUROS.name(), decimalFormat.format(valorParceladoJuros));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELADO_MULTA.name(), decimalFormat.format(valorParceladoMulta));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELADO_CORRECAO_MONETARIA.name(), decimalFormat.format(valorParceladoCorrecao));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELADO_HONORARIOS.name(), valorParceladoHonorarios);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_TOTAL_PARCELADO.name(), decimalFormat.format(valorTotalParcelado));

        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PERCENTUAL_ENTRADA.name(), percentualEntrada);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PERCENTUAL_ENTRADA_EXTENSO.name(), percentualEntradaExtenso);

        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.QUANTIDADE_PARCELAS.name(), quantidadeParcelas);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VENCIMENTO_PRIMEIRA_PARCELA.name(), vencimentoPrimeiraParcela);
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_ENTRADA.name(), decimalFormat.format(valorEntrada));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_PARCELAS.name(), decimalFormat.format(valorParcelas));

        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_TOTAL_A_PARCELAR.name(), decimalFormat.format(valorTotalAParcelar));
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.VALOR_TOTAL_DESCONTO.name(), decimalFormat.format(totalGeralAParcelar.subtract(valorTotalAParcelar)));

        adicionaFiadorDoParcelamento(context, parcelamentos);
        adicionaProcuradorDoParcelamento(context, parcelamentos);

        return conteudo;
    }

    private void adicionaFiadorDoParcelamento(VelocityContext context, List<ProcessoParcelamento> parcelamentos) {
        Pessoa pes = null;
        if (parcelamentos.size() > 1) {
            adicionarTagsVaziasAoFiador(context);
            return;
        }
        ProcessoParcelamento parcelamento = parcelamentos.get(0);
        if (parcelamento.getFiador() != null) {
            if (parcelamento.getFiador() instanceof PessoaFisica) {
                pes = this.pessoaFisicaFacade.recuperar(parcelamento.getFiador().getId());

                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NOME.name(), nomeDoCampo(pes, "nome", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_RG_INSC_ESTADUAL.name(), getRg((PessoaFisica) pes) != null ? getRg((PessoaFisica) pes) : " ");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CPF.name(), nomeDoCampo(pes, "cpf", null));
            } else if (parcelamento.getFiador() instanceof PessoaJuridica) {
                pes = this.pessoaJuridicaFacade.recuperar(parcelamento.getFiador().getId());

                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NOME.name(), nomeDoCampo(pes, "razaoSocial", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_RG_INSC_ESTADUAL.name(), nomeDoCampo(pes, "inscricaoEstadual", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CPF.name(), nomeDoCampo(pes, "cnpj", null));
            }
            if (pes != null && !pes.getTelefones().isEmpty()) {
                boolean temPrincipal = false;
                for (Telefone tel : pes.getTelefones()) {
                    if (tel.getPrincipal().equals(Boolean.TRUE)) {
                        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_TELEFONE.name(), tel.getTelefone());
                        temPrincipal = true;
                    }
                }
                if (!temPrincipal) {
                    addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_TELEFONE.name(), pes.getTelefones().get(0).getTelefone());
                }
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_TELEFONE.name(), "");
            }
            if (pes != null && !pes.getEnderecos().isEmpty()) {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_LOGRADOURO.name(), pes.getEnderecos().get(0).getLogradouro());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NUMERO.name(), pes.getEnderecos().get(0).getNumero());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_COMPLEMENTO.name(), pes.getEnderecos().get(0).getComplemento());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CEP.name(), pes.getEnderecos().get(0).getCep());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_BAIRRO.name(), pes.getEnderecos().get(0).getBairro());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CIDADE.name(), pes.getEnderecos().get(0).getLocalidade());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_UF.name(), pes.getEnderecos().get(0).getUf());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CEP.name(), pes.getEnderecos().get(0).getCep());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_LOGRADOURO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NUMERO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_COMPLEMENTO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CEP.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_BAIRRO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CIDADE.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_UF.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CEP.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_EMAIL.name(), (pes != null ? pes.getEmail() : ""));
            addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(pes));
        } else {
            adicionarTagsVaziasAoFiador(context);
        }
    }

    private void adicionarTagsVaziasAoFiador(VelocityContext context) {
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_BAIRRO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CEP.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CIDADE.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_COMPLEMENTO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_CPF.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_EMAIL.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_ENDERECO_CORRESPONDENCIA.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_LOGRADOURO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NOME.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_NUMERO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_RG_INSC_ESTADUAL.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_TELEFONE.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.FIADOR_UF.name(), "");
    }

    private void adicionaProcuradorDoParcelamento(VelocityContext context, List<ProcessoParcelamento> parcelamentos) {
        Pessoa pes = null;
        if (parcelamentos.size() > 1) {
            adicionarTagsVaziasAoProcurador(context);
            return;
        }
        ProcessoParcelamento parcelamento = parcelamentos.get(0);
        if (parcelamento.getProcurador() != null) {
            if (parcelamento.getProcurador() instanceof PessoaFisica) {
                pes = this.pessoaFisicaFacade.recuperar(parcelamento.getProcurador().getId());

                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NOME.name(), nomeDoCampo((PessoaFisica) pes, "nome", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_RG_INSC_ESTADUAL.name(), getRg((PessoaFisica) pes) != null ? getRg((PessoaFisica) pes) : " ");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CPF.name(), nomeDoCampo((PessoaFisica) pes, "cpf", null));
            } else if (parcelamento.getProcurador() instanceof PessoaJuridica) {
                pes = this.pessoaJuridicaFacade.recuperar(parcelamento.getProcurador().getId());

                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NOME.name(), nomeDoCampo((PessoaJuridica) pes, "razaoSocial", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_RG_INSC_ESTADUAL.name(), nomeDoCampo((PessoaJuridica) pes, "inscricaoEstadual", null));
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CPF.name(), nomeDoCampo((PessoaJuridica) pes, "cnpj", null));
            }
            if (pes != null && !pes.getTelefones().isEmpty()) {
                boolean temPrincipal = false;
                for (Telefone tel : pes.getTelefones()) {
                    if (tel.getPrincipal().equals(Boolean.TRUE)) {
                        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_TELEFONE.name(), tel.getTelefone());
                        temPrincipal = true;
                    }
                }
                if (!temPrincipal) {
                    addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_TELEFONE.name(), pes.getTelefones().get(0).getTelefone());
                }
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_TELEFONE.name(), "");
            }
            if (pes != null && !pes.getEnderecos().isEmpty()) {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_LOGRADOURO.name(), pes.getEnderecos().get(0).getLogradouro());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NUMERO.name(), pes.getEnderecos().get(0).getNumero());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_COMPLEMENTO.name(), pes.getEnderecos().get(0).getComplemento());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CEP.name(), pes.getEnderecos().get(0).getCep());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_BAIRRO.name(), pes.getEnderecos().get(0).getBairro());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CIDADE.name(), pes.getEnderecos().get(0).getLocalidade());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_UF.name(), pes.getEnderecos().get(0).getUf());
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CEP.name(), pes.getEnderecos().get(0).getCep());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_LOGRADOURO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NUMERO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_COMPLEMENTO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CEP.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_BAIRRO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CIDADE.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_UF.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CEP.name(), "");
            }
            addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_EMAIL.name(), (pes != null ? pes.getEmail() : ""));
            addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_ENDERECO_CORRESPONDENCIA.name(), recuperarEnderecoDeCorrespondencia(pes));
        } else {
            adicionarTagsVaziasAoProcurador(context);
        }
    }

    private void adicionarTagsVaziasAoProcurador(VelocityContext context) {
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_BAIRRO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CEP.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CIDADE.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_COMPLEMENTO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_CPF.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_EMAIL.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_ENDERECO_CORRESPONDENCIA.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_LOGRADOURO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NOME.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_NUMERO.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_RG_INSC_ESTADUAL.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_TELEFONE.name(), "");
        addTag(context, TipoModeloDoctoOficial.TagsProcessoParcelamento.PROCURADOR_UF.name(), "");
    }

    public void emiteDocumentoOficial(DocumentoOficial... documentoOficial) {
        String conteudo = geraConteudoDocumento(documentoOficial);
        try {
            emiteDocumentoOficial(conteudo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadDocumentoOficialPdf(DocumentoOficial documentoOficial, String nomeArquivo, boolean hasImagem, String styleTabela, String stylePagina) {
        try {
            String conteudo = geraConteudoDocumento(styleTabela, stylePagina, documentoOficial);
            byte[] bytes;

            if (hasImagem) {
                HtmlPdfDTO htmlPdfDTO = new HtmlPdfDTO();
                htmlPdfDTO.setConteudo(conteudo);
                htmlPdfDTO.setNomeArquivo(nomeArquivo);
                bytes = ReportService.getInstance().converterHtmlParaPdf(htmlPdfDTO);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Html2Pdf.convert(conteudo, baos);
                bytes = baos.toByteArray();
            }

            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "attachment;filename= " + (nomeArquivo != null ? nomeArquivo : "DOCUMENTO-OFICIAL-" + documentoOficial.getNumero()) + ".pdf");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar ao fazer download documento oficial em pdf: ", e);
        }
    }

    public void emiteDocumentoOficial(String conteudo) {
        Web.getSessionMap().put("documentoOficial", conteudo);
        atualizaComponentesDoDialog();
    }

    public String geraConteudoDocumento(DocumentoOficial... documentos) {
        return geraConteudoDocumento(null, null, documentos);
    }

    public String geraConteudoDocumento(String styleTabela, String stylePagina, DocumentoOficial... documentos) {
        String inicio = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <title>WebPúblico</title>"
            + " <style type=\"text/css\">"
            + " /*webreportcss*/"
            + " @media print {\n"
            + " html, body {\n"
            + "     height: 99%;"
            + "  }\n"
            + "    *{-webkit-print-color-adjust:exact}\n"
            + "    .naoImprime {\n"
            + "        display:none;\n"
            + "    }\n"
            + "}"
            + (styleTabela != null ? styleTabela : "")
            + " @page{ \n"
            + (stylePagina != null ? stylePagina : "")
            + " }\n"
            + " .watermark {\n"
            + "    -webkit-transform: rotate(-45deg);\n"
            + "    -moz-transform: rotate(-45deg);\n"
            + "    -ms-transform: rotate(-45deg);\n"
            + "    -o-transform: rotate(-90deg);\n"
            + "    font-size: 92pt;\n"
            + "    font-weight: bold;\n"
            + "    position: absolute;\n"
            + "    opacity: 0.25;\n"
            + "    width: 100%;\n"
            + "    text-align: center;\n"
            + "    z-index: 1000;\n"
            + "    top: 40%;\n"
            + "}\n"
            + "#footer { \n"
            + "     display: block;"
            + "     position: running(footer); \n"
            + "} "
            + ".rodape { \n"
            + "     display: block;"
            + "     position: running(footer); \n"
            + "} "
            + " </style>"
            + " <style type=\"text/css\">"
            + " .break { page-break-before: always; }"
            + " </style>"
            + " </head>"
            + " <body>";
        String conteudo = "";
        int i = 1;
        boolean hasNovoConteudo = false;
        for (DocumentoOficial doc : documentos) {
            if (doc != null) {
                if (!doc.getConteudo().contains("<html>")) {
                    hasNovoConteudo = true;
                    conteudo += inicio + "<div id=\"documentoOficial" + doc.getId() + "\">";
                    if (DocumentoOficial.SituacaoDocumentoOficial.CANCELADO.equals(doc.getSituacaoDocumento())) {
                        conteudo += " <div class=\"watermark\">cancelado</div>";
                    }
                    conteudo += FacesUtil.alteraURLImagens(doc.getConteudo()) + "</div>";
                    inicio = "";
                } else {
                    conteudo += doc.getConteudo();
                }
            }
            if (i != documentos.length) {
                conteudo += "<div class=\"centralizado naoImprime\" style=\"color: gray\">" +
                    "------------------Quebra de Página------------------</div>";
                conteudo += "<p style=\"page-break-before: always; left:0; right:0;\"><br/></p>";
            }
            i++;
        }
        if (hasNovoConteudo) {
            conteudo += " </body>"
                + " </html>";
        }
        return conteudo;
    }

    public void gerarRelatorioDasNotasOrcamentarias(List<NotaExecucaoOrcamentaria> notas, ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        List<String> conteudos = montarConteudoDasNotas(notas, moduloTipoDoctoOficial);
        documentoOficialFacade.emiteDocumentoOficial(montarHtml(conteudos));
    }

    private List<String> montarConteudoDasNotas(List<NotaExecucaoOrcamentaria> notas, ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        List<String> conteudos = Lists.newArrayList();
        if (notas != null && !notas.isEmpty()) {
            List<TipoDoctoOficial> tipoDoctoOficials = tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(moduloTipoDoctoOficial, "");
            if (tipoDoctoOficials == null || tipoDoctoOficials.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar a configuração de documento oficial para o(a) " + moduloTipoDoctoOficial.getDescricao() + ".");
            }
            TipoDoctoOficial tipoDoctoOficial = tipoDoctoOficials.get(0);
            ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipoDoctoOficial);

            for (NotaExecucaoOrcamentaria nota : notas) {
                String conteudoModelo = mod.getConteudo();
                UUID uuid = UUID.randomUUID();
                VelocityContext context = new VelocityContext();
                documentoOficialFacade.tagsNotaOrcamentaria(context, nota);
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
                conteudos.add(writer.toString());
            }
        }
        return conteudos;
    }

    public String montarHtml(List<String> conteudos) {
        String retorno = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <title>WebPúblico</title>"
            + " <style type=\"text/css\">"
            + " /*webreportcss*/"
            + " @media print {\n"
            + " html, body {\n"
            + "     height: 99%;"
            + "  }\n"
            + "    *{-webkit-print-color-adjust:exact}\n"
            + "    .naoImprime {\n"
            + "        display:none;\n"
            + "    }\n"
            + "}"
            + " @page{ \n"
            + "    \n"
            + "}\n"
            + " .watermark {\n"
            + "    -webkit-transform: rotate(-45deg);\n"
            + "    -moz-transform: rotate(-45deg);\n"
            + "    -ms-transform: rotate(-45deg);\n"
            + "    -o-transform: rotate(-90deg);\n"
            + "    font-size: 92pt;\n"
            + "    font-weight: bold;\n"
            + "    position: absolute;\n"
            + "    opacity: 0.25;\n"
            + "    width: 100%;\n"
            + "    text-align: center;\n"
            + "    z-index: 1000;\n"
            + "    top: 40%;\n"
            + "}\n"
            + "#footer { \n"
            + "     display: block;"
            + "     position: running(footer); \n"
            + "} "
            + " </style>"
            + " <style type=\"text/css\">"
            + " .break { page-break-before: always; }"
            + " </style>"
            + " </head>"
            + " <body>";
        int paginaAtual = 1;
        for (String conteudo : conteudos) {
            retorno += conteudo;
            if (paginaAtual != conteudos.size()) {
                retorno += "<div class=\"centralizado naoImprime\" style=\"color: gray\">" +
                    "------------------Quebra de Página------------------</div>";
                retorno += "<p style=\"page-break-before: always; left:0; right:0;\"><br/></p>";
            }
            paginaAtual++;
        }
        retorno += " </body> </html>";
        return retorno;
    }

    public DocumentoOficial geraNovoDocumentoFiscalizacao(AcaoFiscal acaoFiscal, ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa, SistemaControlador sistemaControlador, ParametroFiscalizacao parametroFiscalizacao) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, cadastro, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), acaoFiscal, parametroFiscalizacao));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoAutoInfracao(AutoInfracaoFiscal auto, ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa, SistemaControlador sistemaControlador, ParametroFiscalizacao parametroFiscalizacao) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, cadastro, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), auto, parametroFiscalizacao));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoIsencaoIPTU(IsencaoCadastroImobiliario isencaoCadastroImobiliario, ModeloDoctoOficial modelo, Cadastro cadastro, UsuarioSistema usu, Exercicio exercicio, String ip) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, cadastro, null, usu, exercicio, ip);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), isencaoCadastroImobiliario, null));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial gerarNovoDocumentoSolicitacaoSepultamento(AuxilioFuneral auxilioFuneral, ModeloDoctoOficial modelo) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, null, null, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getIp());
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), auxilioFuneral, null));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumento(Object obj, ModeloDoctoOficial mod, Cadastro cadastro, Pessoa pessoa) throws UFMException, AtributosNulosException {
        return geraNovoDocumento(obj, mod, cadastro, pessoa, null, null);
    }

    public DocumentoOficial geraNovoDocumento(Object obj, ModeloDoctoOficial mod, Cadastro cadastro, Pessoa pessoa, Long idUndiadeOrganizacional, String conteudo) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(mod, cadastro, pessoa);
        documentoOficial.setConteudo(conteudo != null ? conteudo : mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), obj, null));
        documentoOficial = this.documentoOficialFacade.salvarRetornando(documentoOficial);
        if (idUndiadeOrganizacional != null) {
            gerarNotificacaoAssinatura(documentoOficial, idUndiadeOrganizacional);
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarNovoDocumento(Object obj, TipoDoctoOficial tipo, Cadastro cadastro, Pessoa pessoa, UsuarioSistema usuario, Exercicio exercico, String ip) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            documentoOficial = novoDocumento(mod, cadastro, pessoa, usuario, exercico, ip);
            documentoOficial.setConteudo(mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), obj, null));
            return documentoOficialFacade.salvarRetornando(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    private DocumentoOficial geraNovoDocumentoSolicitacao(SolicitacaoDoctoOficial sol, ModeloDoctoOficial mod, Cadastro cadastro, Pessoa pessoa) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(mod, cadastro, pessoa);
        documentoOficial.setDataSolicitacao(sol.getDataSolicitacao());
        documentoOficial.setDataEmissao(sol.getDataSolicitacao());
        documentoOficial.setValidade(validadeDocumento(sol.getDataSolicitacao(), mod.getTipoDoctoOficial()));
        documentoOficial.setConteudo(mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), sol, null));
        return this.documentoOficialFacade.salvarRetornando(documentoOficial);
    }

    private DocumentoOficial geraNovoDocumentoEmBranco(ModeloDoctoOficial mod, Cadastro cadastro, Pessoa pessoa) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(mod, cadastro, pessoa);
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoProtocolo(Processo processo, Tramite tramite, ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, cadastro, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), processo, tramite));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    private DocumentoOficial novoDocumento(ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa, UsuarioSistema usuarioSistema, Exercicio exercicio, String ip) {
        Integer numeroDocumento = geraNumeroDocumentoOficial(modelo.getTipoDoctoOficial(), modelo.getTipoDoctoOficial(), exercicio);
        Date validade = validadeDocumento(modelo.getTipoDoctoOficial());

        DocumentoOficial documentoOficial = new DocumentoOficial();
        documentoOficial.setModeloDoctoOficial(modelo);
        documentoOficial.setDataEmissao(new Date());
        documentoOficial.setDataSolicitacao(documentoOficial.getDataSolicitacao());
        documentoOficial.setNumero(numeroDocumento);
        documentoOficial.setExercicio(exercicio);
        documentoOficial.setValidade(validade);
        documentoOficial.setUsuarioSistema(usuarioSistema);
        documentoOficial.setIpMaquina(ip);
        documentoOficial.setCadastro(cadastro);
        documentoOficial.setPessoa(pessoa);

        Calendar cal = Calendar.getInstance();
        cal.setTime(documentoOficial.getDataEmissao());

        documentoOficial.setCodigoVerificacao(geraCodigoVerificacaoDoctoOficial(documentoOficial));
        return documentoOficial;
    }

    private String geraCodigoVerificacaoDoctoOficial(DocumentoOficial documentoOficial) {
        String chave = documentoOficial.getNumero().toString() +
            documentoOficial.getExercicio().getAno() +
            documentoOficial.getModeloDoctoOficial().getId();
        return GeradorChaveAutenticacao.geraChaveDeAutenticacao(chave, GeradorChaveAutenticacao.TipoAutenticacao.DOCUMENTO_OFICIAL);
    }

    private String criptografiaDocumento(String numeroContratoComExercicio, Date dataEmissao, String chaveInterna, Date validade) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(geraCodigo(numeroContratoComExercicio, dataEmissao, chaveInterna, validade).getBytes());
            return converteBytesParaString(md.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String geraCodigo(String numeroContratoComExercicio, Date dataEmissao, String chaveInterna, Date validade) {
        return dataEmissao.toString() + numeroContratoComExercicio + chaveInterna + validade.toString();
    }

    public String converteBytesParaString(byte[] value) throws UnsupportedEncodingException {
        byte[] base64 = Base64.encodeBase64(value);
        String retorno = new String(base64, "UTF-8");
        return retorno;
    }

    public DocumentoOficial novoDocumento(ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa) {
        return novoDocumento(modelo, cadastro, pessoa, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getIp());
    }

    private DocumentoOficial geraNovoDocumentoAutoInfracao(ProcessoFiscalizacao processoFiscalizacao, ModeloDoctoOficial mod, Cadastro cadastro, Pessoa pessoa) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(mod, cadastro, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), processoFiscalizacao, null));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumento(Object obj, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        if (documento != null && documento.isValido()) {
            return recuperaDocumento(documento);
        } else {
            ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
            if (mod == null || Strings.isNullOrEmpty(mod.getConteudo())) {
                throw new ValidacaoException("O Tipo de Documento " + tipo.getDescricao() + "não possui o layout de documento cadastrado.");
            }
            return geraNovoDocumento(obj, mod, cadastro, pessoa);
        }
    }

    public DocumentoOficial gerarDocumentoSolicitacao(SolicitacaoDoctoOficial solicitacao, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = gerarDocumentoSolicitacaoSemEmitir(solicitacao, documento, cadastro, pessoa, tipo, sistemaControlador);
        if (documentoOficial.getConteudo() != null) {
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoHabiteseConstrucao(Habitese habitese, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = null;
        if (mod != null) {
            documentoOficial = geraNovoDocumento(habitese, mod, habitese.getAlvaraConstrucao().getProcRegularizaConstrucao().getCadastroImobiliario(), null);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoAlvaraConstrucao(AlvaraConstrucao alvara, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = null;
        if (mod != null) {
            documentoOficial = geraNovoDocumento(alvara, mod, alvara.getProcRegularizaConstrucao().getCadastroImobiliario(), null);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoSubvencao(SubvencaoEmpresas subvencaoEmpresa, DocumentoOficial documento, Cadastro cadastro, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(subvencaoEmpresa, mod, cadastro, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoBloqueioOutorga(BloqueioOutorga bloqueioOutorga, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = null;
        if (mod != null) {
            documentoOficial = geraNovoDocumento(bloqueioOutorga, mod, null, null);
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoProcessoProtesto(ProcessoDeProtesto processoDeProtesto, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = null;
        if (mod != null) {
            documentoOficial = geraNovoDocumento(processoDeProtesto, mod, processoDeProtesto.getCadastroProtesto(), processoDeProtesto.getPessoaProtesto());
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoOrdemGeraldeMonitoramento(OrdemGeralMonitoramento ordemGeralMonitoramento, DocumentoOficial documento, Cadastro cadastro, TipoDoctoOficial tipo, UsuarioSistema usuarioSistema) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(ordemGeralMonitoramento, mod, cadastro, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(usuarioSistema);
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoOrdemGeralRelatorioFinal(OrdemGeralMonitoramento ordemGeralMonitoramento, DocumentoOficial documento, TipoDoctoOficial tipo, UsuarioSistema usuarioSistema) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(ordemGeralMonitoramento, mod, null, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(usuarioSistema);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoSolicitacaoSemEmitir(SolicitacaoDoctoOficial solicitacao, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
                if (documentoOficial.getConteudo() == null) {
                    if (documentoOficial.getValidade() == null) {
                        documentoOficial.setValidade(validadeDocumento(solicitacao.getDataSolicitacao(), tipo));
                    }
                    if (documentoOficial.getUsuarioSistema() == null) {
                        documentoOficial.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                    }
                    documentoOficial.setCodigoVerificacao(geraCodigoVerificacaoDoctoOficial(documentoOficial));
                    documentoOficial.setConteudo(mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), solicitacao, null));
                }
            } else {
                documentoOficial = geraNovoDocumentoSolicitacao(solicitacao, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoEmBranco(Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            documentoOficial = geraNovoDocumentoEmBranco(mod, cadastro, pessoa);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public String geraDocumentoSolicitacaoPortalWeb(SolicitacaoDoctoOficial solicitacao) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(solicitacao.getTipoDoctoOficial());
        DocumentoOficial documentoOficial;
        if (mod != null) {
            if (solicitacao.getDocumentoOficial() != null) {
                documentoOficial = recuperaDocumento(solicitacao.getDocumentoOficial());
                if (documentoOficial.getConteudo() == null) {
                    documentoOficial.setConteudo(mesclaTagsDocto(mod.getConteudo(), documentoOficial, mod.getTipoDoctoOficial().getModuloTipoDoctoOficial(), solicitacao, null));
                }
            } else {
                documentoOficial = geraNovoDocumento(solicitacao, mod, solicitacao.getCadastro(), solicitacao.getPessoa());
            }
            return geraConteudoDocumento(documentoOficial);
        } else {
            throw new ExcecaoNegocioGenerica("O Tipo de Documento não possui o layout de documento cadastrado.");
        }
    }

    public DocumentoOficial geraDocumentoFiscalizacao(AcaoFiscal acaoFiscal, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador, ParametroFiscalizacao parametroFiscalizacao) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoFiscalizacao(acaoFiscal, mod, cadastro, pessoa, sistemaControlador, parametroFiscalizacao);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoAutoInfracao(AutoInfracaoFiscal auto, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador, ParametroFiscalizacao parametroFiscalizacao) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoAutoInfracao(auto, mod, cadastro, pessoa, sistemaControlador, parametroFiscalizacao);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoIsencaoIPTU(IsencaoCadastroImobiliario isencaoCadastroImobiliario, DocumentoOficial documento, Cadastro cadastro, TipoDoctoOficial tipo, UsuarioSistema usu, Exercicio exercicio, String ip) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoIsencaoIPTU(isencaoCadastroImobiliario, mod, cadastro, usu, exercicio, ip);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certidaoDividaAtiva, mod, cadastro, pessoa);
            }
            alteraSituacaoDocumentoOficial(documentoOficial, (certidaoDividaAtiva.getSituacaoCertidaoDA().equals(SituacaoCertidaoDA.CANCELADA) ? DocumentoOficial.SituacaoDocumentoOficial.CANCELADO : DocumentoOficial.SituacaoDocumentoOficial.ATIVO));
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoBaixaAtividade(CertidaoAtividadeBCE certidaoAtividadeBCE, DocumentoOficial documento, Cadastro cadastro, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certidaoAtividadeBCE, mod, cadastro, null);
            }
            alteraSituacaoDocumentoOficial(documentoOficial, DocumentoOficial.SituacaoDocumentoOficial.ATIVO);
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoDividaAtivaSemImprimir(CertidaoDividaAtiva certidaoDividaAtiva, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certidaoDividaAtiva, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoPeticaoDividaAtiva(Peticao peticao, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(peticao, mod, cadastro, pessoa);
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoFiscalizacaoRBTrans(FiscalizacaoRBTrans fisc, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(fisc, mod, cadastro, pessoa);
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoProtocoloWeb(Processo processo, Tramite tramite, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoProtocolo(processo, tramite, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoProtocolo(Processo processo, Tramite tramite, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            documentoOficial = geraNovoDocumentoProtocolo(processo, tramite, mod, cadastro, pessoa);
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoRBTrans(TermoRBTrans termo,
                                                 DocumentoOficial documento,
                                                 Cadastro cadastro,
                                                 Pessoa pessoa,
                                                 TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(termo, mod, cadastro, pessoa);
            }

        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public void emitirDocumentoRBTrans(DocumentoOficial documentoOficial, TermoRBTrans termoRBTrans) {
        if (documentoOficial.getConteudo() == null) {
            try {
                documentoOficial.setConteudo(mesclaTagsDocto(documentoOficial.getModeloDoctoOficial().getConteudo(), documentoOficial, documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getModuloTipoDoctoOficial(), termoRBTrans, null));
                documentoOficial = documentoOficialFacade.salvarRetornando(documentoOficial);
            } catch (UFMException | AtributosNulosException e) {
                logger.error("Erro ao emitir documento do RBTrans: {}", e);
            }
        }
        emiteDocumentoOficial(documentoOficial);
    }

    public DocumentoOficial geraDocumentoTermoAdvertencia(ProcessoFiscalizacao processo, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(processo, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoTermoParcelamento(ProcessoParcelamento parcelamento, ParcelamentoJudicial parcelamentoJudicial, DocumentoOficial documento) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = null;
        if (parcelamento == null && parcelamentoJudicial == null) return null;

        ProcessoParcelamento primeiroProcesso = parcelamento == null ? parcelamentoJudicial.getItens().get(0).getProcessoParcelamento() : parcelamento;
        Pessoa pessoaProcesso = parcelamento == null ? parcelamentoJudicial.getPessoa() : parcelamento.getPessoa();
        if (parcelamentoJudicial == null && primeiroProcesso.getCadastro() != null) {
            mod = tipoDoctoOficialFacade.recuperaModeloVigente(primeiroProcesso.getParamParcelamento().getTermoCadastro());
        } else if (pessoaProcesso instanceof PessoaFisica) {
            mod = tipoDoctoOficialFacade.recuperaModeloVigente(primeiroProcesso.getParamParcelamento().getTermoPessoaFisica());
        } else if (pessoaProcesso instanceof PessoaJuridica) {
            mod = tipoDoctoOficialFacade.recuperaModeloVigente(primeiroProcesso.getParamParcelamento().getTermoPessoaJuridica());
        }

        DocumentoOficial documentoOficial = null;
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                if (parcelamento != null) {
                    documentoOficial = geraNovoDocumento(Lists.newArrayList(parcelamento), mod, parcelamento.getCadastro(), parcelamento.getPessoa());
                } else {
                    documentoOficial = geraNovoDocumento(parcelamentoJudicial.getItens().stream().map(ItemParcelamentoJudicial::getProcessoParcelamento).collect(Collectors.toList()), mod, primeiroProcesso.getCadastro(), parcelamentoJudicial.getPessoa());
                }
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoAutoInfracaoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoAutoInfracao(processoFiscalizacao, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoParecerRecursoFiscalizacao(RecursoFiscalizacao recurso, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoParecerRecursoFiscalizacao(recurso, mod, cadastro, pessoa, sistemaControlador);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoParecerRecursoFiscalizacao(RecursoFiscalizacao recurso, ModeloDoctoOficial modelo, Cadastro cadastro, Pessoa pessoa, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, cadastro, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), recurso, null));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoTermoGeral(TermoGeralFiscalizacao termo, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(termo, mod, cadastro, pessoa);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    private Date validadeDocumento(TipoDoctoOficial tipoDoctoOficial) {
        return validadeDocumento(new Date(), tipoDoctoOficial);
    }

    private Date validadeDocumento(Date data, TipoDoctoOficial tipoDoctoOficial) {
        Calendar validade = Calendar.getInstance();
        validade.setTime(data);
        validade.add(Calendar.DAY_OF_MONTH, tipoDoctoOficial.getValidadeDocto());
        return validade.getTime();
    }

    public Integer geraNumeroDocumentoOficial(TipoDoctoOficial tipo, TipoDoctoOficial tipoDoctoOficial, Exercicio exercicio) {
        if (tipo.getTipoSequencia() == null && TipoSequenciaDoctoOficial.EXERCICIO.equals(tipo.getGrupoDoctoOficial().getTipoSequencia())) {
            return singletonNumeroDoctoOficial.recuperarProximoNumero(tipoDoctoOficial.getGrupoDoctoOficial().getId(), exercicio.getId(),
                tipoDoctoOficial, TipoNumeroDoctoOficial.EXERCICIO_GRUPO);
        } else if (tipo.getTipoSequencia() == null && TipoSequenciaDoctoOficial.SEQUENCIA.equals(tipo.getGrupoDoctoOficial().getTipoSequencia())) {
            return singletonNumeroDoctoOficial.recuperarProximoNumero(tipoDoctoOficial.getGrupoDoctoOficial().getId(), tipoDoctoOficial,
                TipoNumeroDoctoOficial.GRUPO);
        } else if (TipoSequenciaDoctoOficial.EXERCICIO.equals(tipo.getTipoSequencia())) {
            return singletonNumeroDoctoOficial.recuperarProximoNumero(tipoDoctoOficial.getId(), exercicio.getId(), tipoDoctoOficial,
                TipoNumeroDoctoOficial.EXERCICIO_TIPO);
        } else if (TipoSequenciaDoctoOficial.SEQUENCIA.equals(tipo.getTipoSequencia())) {
            return singletonNumeroDoctoOficial.recuperarProximoNumero(tipoDoctoOficial.getId(), tipoDoctoOficial,
                TipoNumeroDoctoOficial.TIPO);
        } else {
            return 0;
        }
    }

    private String colocarZeroEsquerda(int numero) {
        if (numero < 10) {
            return "0" + numero;
        } else {
            return "" + numero;
        }
    }

    public CadastroImobiliario recuperaCadastroImobiliario(DocumentoOficial documentoOficial) {
        CadastroImobiliario cadastroImobiliario = new CadastroImobiliario();
        if (documentoOficial.getCadastro() != null) {
            cadastroImobiliario = em.find(CadastroImobiliario.class, documentoOficial.getCadastro().getId());
        }
        return cadastroImobiliario;
    }

    private Pessoa recuperaPessoa(CadastroEconomico ce) {
        if (!ce.getPessoa().getEnderecos().isEmpty()) {
            return this.pessoaFacade.recuperar(ce.getPessoa().getId());
        }
        return null;
    }

    private DocumentoPessoal getRg(PessoaFisica pes) {
        if (pes != null) {
            for (DocumentoPessoal documentoPessoal : pes.getDocumentosPessoais()) {
                if (documentoPessoal instanceof RG) {
                    return documentoPessoal;
                }
            }
        }
        return null;
    }

    public DocumentoOficial recuperaDocumento(DocumentoOficial documentoOficial) {
        List resultList = em.createQuery("select doc from DocumentoOficial doc where doc = :doc")
            .setParameter("doc", documentoOficial)
            .getResultList();
        return !resultList.isEmpty() ? (DocumentoOficial) resultList.get(0) : null;
    }

    private String montarTabelaISSQNHabitese(Habitese habitese) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";

        tabela += "<tr>";
        tabela += "<td align=\"left\">" + "Base de Cálculo de ISSQN" + "</td>";
        if (habitese.getBaseCalculoISSQN() != null)
            tabela += "<td align=\"left\">" + decimalFormat.format(habitese.getBaseCalculoISSQN()) + "</td>";
        tabela += "</tr>";

        tabela += "<tr>";
        tabela += "<td align=\"left\">" + "Alíquota" + "</td>";
        if (habitese.getValorAliquota() != null)
            tabela += "<td align=\"left\">" + decimalFormat.format(habitese.getValorAliquota()) + "%</td>";
        tabela += "</tr>";

        tabela += "<tr>";
        tabela += "<td align=\"left\">" + "Valor Alíquota ISSQN" + "</td>";
        if (habitese.getValorAliquotaCalculado() != null)
            tabela += "<td align=\"left\">" + decimalFormat.format(habitese.getValorAliquotaCalculado()) + "</td>";
        tabela += "</tr>";

        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaServicosHabitese(Habitese habitese) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>Serviço</b></th>";
        tabela += "<th align=\"left\"><b>Classe</b></th>";
        tabela += "<th align=\"left\"><b>Tipo</b></th>";
        tabela += "<th align=\"left\"><b>Área (m²)</b></th>";
        tabela += "</tr>";

        if (habitese.getCaracteristica() != null) {
            for (ServicosAlvaraConstrucao servico : habitese.getCaracteristica().getServicos()) {
                tabela += "<tr>";
                tabela += "<td align=\"left\">" + (servico.getServicoConstrucao() != null ? servico.getServicoConstrucao().getDescricao() : "") + "</td>";
                tabela += "<td align=\"left\">" + (servico.getHabiteseClassesConstrucao() != null ? servico.getHabiteseClassesConstrucao().getDescricao() : "") + "</td>";
                tabela += "<td align=\"left\">" + (servico.getServicoConstrucao() != null && servico.getServicoConstrucao().getTipoConstrucao() != null ? servico.getServicoConstrucao().getTipoConstrucao().getDescricao() : "") + "</td>";
                tabela += "<td align=\"left\">" + decimalFormat.format(servico.getArea() != null ? servico.getArea() : BigDecimal.ZERO) + "</td>";
                tabela += "</tr>";
            }
        }
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaDeducoesHabitese(Habitese habitese) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>Discriminação</b></th>";
        tabela += "<th align=\"left\"><b>Base de Cálculo</b></th>";
        tabela += "</tr>";

        for (DeducaoHabitese deducao : habitese.getDeducoes()) {
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + deducao.getDescricao() + "</td>";
            tabela += "<td align=\"left\">" + decimalFormat.format(deducao.getBaseDeCalculo() != null ? deducao.getBaseDeCalculo() : BigDecimal.ZERO) + "</td>";
            tabela += "</tr>";
        }
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaCaracteristicasConstrucaoHabitese(Habitese habitese) {
        Map<String, String> mapCaracteristicas = Maps.newHashMap();
        mapCaracteristicas.put("Habite-se", habitese.getCaracteristica().getTipoHabitese() != null ?
            habitese.getCaracteristica().getTipoHabitese().getDescricao() : "");
        mapCaracteristicas.put("Pavimentos", habitese.getCaracteristica().getQuantidadeDePavimentos() != null ?
            habitese.getCaracteristica().getQuantidadeDePavimentos().toString() : "");
        mapCaracteristicas.put("Classe", habitese.getCaracteristica().getClasse() != null ?
            habitese.getCaracteristica().getClasse().getDescricao() : "");
        mapCaracteristicas.put("Tipo", habitese.getCaracteristica().getTipoConstrucao() != null ?
            habitese.getCaracteristica().getTipoConstrucao().getDescricao() : "");
        mapCaracteristicas.put("Padrão", habitese.getCaracteristica().getPadrao() != null ?
            habitese.getCaracteristica().getPadrao().getDescricao() : "");
        mapCaracteristicas.put("Tempo de Construção", habitese.getCaracteristica().getTempoConstrucao() != null ?
            habitese.getCaracteristica().getTempoConstrucao().toString() : "");
        mapCaracteristicas.put("Valor da Mão de Obra", habitese.getCaracteristica().getValorMaoDeObra() != null ?
            decimalFormat.format(habitese.getCaracteristica().getValorMaoDeObra()) : "");
        mapCaracteristicas.put("Área Construida", habitese.getCaracteristica().getAreaConstruida() != null ?
            decimalFormat.format(habitese.getCaracteristica().getAreaConstruida()) : "");
        mapCaracteristicas.put("Base de Cálculo", habitese.getCaracteristica().getBaseDeCalculo() != null ?
            decimalFormat.format(habitese.getCaracteristica().getBaseDeCalculo()) : "");

        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr>";
        for (String key : mapCaracteristicas.keySet()) {
            tabela += "<th align=\"left\"><b>" + key + "</b></th>";
        }
        tabela += "</tr>";
        tabela += "<tr>";
        for (String key : mapCaracteristicas.keySet()) {
            tabela += "<td align=\"left\">" + mapCaracteristicas.get(key) + "</td>";
        }
        tabela += "</tr>";
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaServicosAlvara(AlvaraConstrucao alvaraConstrucao) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>Serviço</b></th>";
        tabela += "<th align=\"left\"><b>Classe</b></th>";
        tabela += "<th align=\"left\"><b>Tipo</b></th>";
        tabela += "<th align=\"left\"><b>Área (m²)</b></th>";
        tabela += "</tr>";

        for (ServicosAlvaraConstrucao servico : alvaraConstrucao.getServicos()) {
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + (servico.getServicoConstrucao() != null ? servico.getServicoConstrucao().getDescricao() : "") + "</td>";
            tabela += "<td align=\"left\">" + (servico.getItemServicoConstrucao() != null ? servico.getItemServicoConstrucao().getDescricao() : "") + "</td>";
            tabela += "<td align=\"left\">" + (servico.getServicoConstrucao() != null && servico.getServicoConstrucao().getTipoConstrucao() != null ? servico.getServicoConstrucao().getTipoConstrucao().getDescricao() : "") + "</td>";
            tabela += "<td align=\"left\">" + decimalFormat.format(servico.getArea() != null ? servico.getArea() : BigDecimal.ZERO) + "</td>";
            tabela += "</tr>";
        }
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaCalculoAlvara(AlvaraConstrucao alvaraConstrucao) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>Área de Construção</b></th>";
        tabela += "<th align=\"left\"><b>Vencimento</b></th>";
        tabela += "<th align=\"left\"><b>Valor Calculado (UFM)</b></th>";
        tabela += "<th align=\"left\"><b>Valor Calculado (R$)</b></th>";
        tabela += "</tr>";

        if (alvaraConstrucao.getProcessoCalcAlvaConstHabi() != null) {
            for (CalculoAlvaraConstrucaoHabitese calculo : alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese()) {
                tabela += "<tr>";
                tabela += "<td align=\"left\">" + decimalFormat.format(calculo.getAreaTotal()) + "</td>";
                tabela += "<td align=\"left\">" + formataData(calculo.getVencimento()) + "</td>";
                tabela += "<td align=\"left\">" + dfUFM.format(calculo.getValorUFM()) + "</td>";
                tabela += "<td align=\"left\">" + dfReais.format(calculo.getValorEfetivo()) + "</td>";
                tabela += "</tr>";
            }
        }
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaDetalhesCalculoAlvara(AlvaraConstrucao alvaraConstrucao) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>Tributo</b></th>";
        tabela += "<th align=\"left\"><b>Valor (UFM)</b></th>";
        tabela += "<th align=\"left\"><b>Valor (R$)</b></th>";
        tabela += "</tr>";

        if (alvaraConstrucao.getProcessoCalcAlvaConstHabi() != null) {
            if (!alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().isEmpty()) {
                CalculoAlvaraConstrucaoHabitese calculo = alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0);
                for (ItemCalculoAlvaraConstrucaoHabitese item : calculo.getItensCalculo()) {
                    tabela += "<tr>";
                    tabela += "<td align=\"left\">" + item.getTributo().toString() + "</td>";
                    tabela += "<td align=\"left\">" + dfUFM.format(item.getValorUFM()) + "</td>";
                    tabela += "<td align=\"left\">" + dfReais.format(item.getValorReal()) + "</td>";
                    tabela += "</tr>";
                }
            }
        }

        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaDamGenerica(List<ResultadoParcela> resultadoParcelas) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><th align=\"left\"><b>DAM</b></th>";
        tabela += "<th align=\"left\"><b>Dívida</b></th>";
        tabela += "<th align=\"left\"><b>Exercício</b></th>";
        tabela += "<th align=\"left\"><b>Vencimento</b></th>";
        tabela += "<th align=\"left\"><b>Imposto</b></th>";
        tabela += "<th align=\"left\"><b>Taxa</b></th>";
        tabela += "<th align=\"left\"><b>Desconto</b></th>";
        tabela += "<th align=\"left\"><b>Juros</b></th>";
        tabela += "<th align=\"left\"><b>Multa</b></th>";
        tabela += "<th align=\"left\"><b>Correção</b></th>";
        tabela += "<th align=\"left\"><b>Honorários</b></th>";
        tabela += "<th align=\"left\"><b>Total</b></th>";
        tabela += "<th align=\"left\"><b>Situação da Parcela</b></th>";
        tabela += "</tr>";

        for (ResultadoParcela parcela : resultadoParcelas) {
            List<DAM> dams = calculoTaxasDiversasFacade.recuperaDAM(parcela.getIdParcela());
            String numeroDam = "";
            if (!dams.isEmpty()) {
                numeroDam = dams.get(0).getNumeroDAM();
            }
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + numeroDam + "</td>";
            tabela += "<td align=\"left\">" + parcela.getDivida() + "</td>";
            tabela += "<td align=\"left\">" + parcela.getExercicio() + "</td>";
            tabela += "<td align=\"left\">" + parcela.getVencimentoToString() + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorImposto()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorTaxa()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorDesconto()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorJuros()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorMulta()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorCorrecao()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorHonorarios()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parcela.getValorTotal()) + "</td>";
            tabela += "<td align=\"left\">" + parcela.getSituacaoDescricaoEnum() + "</td>";
            tabela += "</tr>";
        }
        tabela += "</table>";
        return tabela;
    }

    private String montarTabelaDividaAtiva(List<ComposicaoCDA> composicaoCDA) {

        String itens = "<table \" width=\"100%\" style=\"font-size:x-small\">"
            + "<tr>"
            + "<th align=\"center\">Ano</th>"
            + "<th align=\"center\">Dívida</th>"
            + "<th align=\"center\">Parcela</th>"
            + "<th align=\"center\">Vencimento</th>"
            + "<th align=\"center\">Data Inscr.</th>"
            + "<th align=\"center\">Ano/Livro</th>"
            + "<th align=\"center\">Seq.</th>"
            + "<th align=\"center\">Pág.</th>"
            + "<th align=\"center\">Tipo</th>"
            + "<th align=\"right\">Valor</th>"
            + "<th align=\"right\">Multa</th>"
            + "<th align=\"right\">Juros</th>"
            + "<th align=\"right\">Correção</th>"
            + "<th align=\"right\">Honorários</th>"
            + "<th align=\"right\">Total</th>"
            + "</tr>";
        BigDecimal totalLancado = BigDecimal.ZERO;
        BigDecimal totalMulta = BigDecimal.ZERO;
        BigDecimal totalJuros = BigDecimal.ZERO;
        BigDecimal totalCorrecao = BigDecimal.ZERO;
        BigDecimal totalHonorarios = BigDecimal.ZERO;
        BigDecimal totalTotal = BigDecimal.ZERO;

        for (ComposicaoCDA composicao : composicaoCDA) {
            totalLancado = totalLancado.add(composicao.getValorTributo());
            totalMulta = totalMulta.add(composicao.getValorMulta());
            totalJuros = totalJuros.add(composicao.getValorJuros());
            totalCorrecao = totalCorrecao.add(composicao.getValorCorrecao());
            totalHonorarios = totalHonorarios.add(composicao.getValorHonorarios());
            totalTotal = totalTotal.add(composicao.getValorTotal());
            InscricaoDividaAtiva inscricao = certidaoDividaAtivaFacade.recuperarInscricaoDividaAtiva(composicao.getIdItemInscricaoDividaAtiva());
            LivroDividaAtiva livroDividaAtiva = certidaoDividaAtivaFacade.recuperarLivroDivida(inscricao);
            LinhaDoLivroDividaAtiva linhaDoLivroDividaAtiva = certidaoDividaAtivaFacade.recuperarLinhaDoLivroDividaAtiva(composicao.getIdItemInscricaoDividaAtiva());
            Date dataInscricao = new Date();
            if (inscricao != null) {
                dataInscricao = inscricao.getDataInscricao();
            }
            itens += "<tr>"
                + "<th align=\"center\">" + composicao.getResultadoParcela().getExercicio() + "</th>"
                + "<th align=\"center\">" + composicao.getResultadoParcela().getDivida() + "</th>"
                + "<th align=\"center\">" + composicao.getResultadoParcela().getParcela() + "</th>"
                + "<th align=\"center\">" + formatterData.format(composicao.getResultadoParcela().getVencimento()) + "</th>"
                + "<th align=\"center\">" + formatterData.format(dataInscricao) + "</th>"
                + "<th align=\"center\">" + nomeDoCampo(livroDividaAtiva, "exercicio.ano", null) + "/" + nomeDoCampo(livroDividaAtiva, "numero", null) + "</th>"
                + "<th align=\"center\">" + nomeDoCampo(livroDividaAtiva, "sequencia", null) + "</th>"
                + "<th align=\"center\">" + nomeDoCampo(linhaDoLivroDividaAtiva, "pagina", null) + "</th>"
                + "<th align=\"center\">" + composicao.getTipoTributo().getDescricao() + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorTributo()) + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorMulta()) + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorJuros()) + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorCorrecao()) + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorHonorarios()) + "</th>"
                + "<th align=\"right\"> " + dfReais.format(composicao.getValorTotal()) + "</th>"
                + "</tr>";
        }

        itens += " <tr/><tr/><tr/><tr/>";
        itens += " <tr><th colspan=\"15\" align=\"right\"> Total Valor Lançado: R$ " + dfReais.format(totalLancado) + " </th></tr>"
            + "<tr><th colspan=\"15\" align=\"right\"> Total Multa: R$ " + dfReais.format(totalMulta) + " </th></tr>"
            + "<tr><th colspan=\"15\" align=\"right\"> Total Juros: R$ " + dfReais.format(totalJuros) + " </th></tr>"
            + "<tr><th colspan=\"15\" align=\"right\"> Total Correção: R$ " + dfReais.format(totalCorrecao) + " </th></tr>"
            + "<tr><th colspan=\"15\" align=\"right\"> Total Honorários: R$ " + dfReais.format(totalHonorarios) + " </th></tr>"
            + "<tr><th colspan=\"15\" align=\"right\"> Total: R$ " + dfReais.format(totalTotal) + " </th></tr>"
            + "</table>";
        return itens;
    }

    private String montarTabelaDebitosSubvencionados(List<ResultadoParcela> parcelas) {
        String itens = "<table width=\"100%\" style=\"font-size:x-small; border: 1px solid; border-collapse: collapse\" border=\"1\">"
            + "<tr>"
            + "<th align=\"center\"> CADASTRO </th>"
            + "<th align=\"center\"> REFERÊNCIA </th>"
            + "<th align=\"center\"> DÍVIDA </th>"
            + "<th align=\"center\"> EXERCÍCIO </th>"
            + "<th align=\"center\"> PARC. </th>"
            + "<th align=\"center\"> VENCIMENTO </th>"
            + "<th align=\"center\"> VALOR </th>"
            + "<th align=\"center\"> PROCESSO JUDICIAL </th>"
            + "</tr>";
        BigDecimal total = BigDecimal.ZERO;

        for (ResultadoParcela resultadoParcela : parcelas) {
            total = total.add(resultadoParcela.getValorTotal());
            String processoJudicial = inscricaoDividaAtivaFacade.numeroProcessoJudicialCda(resultadoParcela.getIdParcela());
            itens += "<tr>"
                + "<th align=\"center\">" + resultadoParcela.getCadastro() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getReferencia() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getDivida() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getExercicio() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getParcela() + "</th>"
                + "<th align=\"center\">" + formatterData.format(resultadoParcela.getVencimento()) + "</th>"
                + "<th align=\"center\"> " + dfReais.format(resultadoParcela.getValorTotal()) + "</th>"
                + "<th align=\"center\"> " + processoJudicial + "</th>"
                + "</tr>";
        }
        itens += " <tr><th colspan=\"5\">&nbsp;</th>"
            + " <th align=\"center\">TOTAL:</th>"
            + " <th align=\"center\">" + dfReais.format(total) + "</th>"
            + " <th>&nbsp;</th>"
            + " </tr>"
            + "</table>";
        return itens;
    }

    private String montarTabelaProcessoDeProtesto(List<ResultadoParcela> parcelas) {
        String itens = "<table width=\"100%\" style=\"font-size:x-small; border: 1px solid; border-collapse: collapse\" border=\"1\">"
            + "<tr>"
            + "<th align=\"center\"> CADASTRO </th>"
            + "<th align=\"center\"> REFERÊNCIA </th>"
            + "<th align=\"center\"> DÍVIDA </th>"
            + "<th align=\"center\"> EXERCÍCIO </th>"
            + "<th align=\"center\"> PARC. </th>"
            + "<th align=\"center\"> VENCIMENTO </th>"
            + "<th align=\"center\"> VALOR </th>"
            + "</tr>";

        for (ResultadoParcela resultadoParcela : parcelas) {
            itens += "<tr>"
                + "<th align=\"center\">" + resultadoParcela.getCadastro() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getReferencia() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getDivida() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getExercicio() + "</th>"
                + "<th align=\"center\">" + resultadoParcela.getParcela() + "</th>"
                + "<th align=\"center\">" + formatterData.format(resultadoParcela.getVencimento()) + "</th>"
                + "<th align=\"center\"> " + dfReais.format(resultadoParcela.getValorTotal()) + "</th>"
                + "</tr>";
        }
        itens += " <tr><th colspan=\"5\">&nbsp;</th>"
            + "</table>";
        return itens;
    }


    private String montaTabelaLancamentosContabeisFiscal(RegistroLancamentoContabil registro) {
        BigDecimal totalValorDeclarado = BigDecimal.ZERO;
        BigDecimal totalValorApurado = BigDecimal.ZERO;
        BigDecimal totalBaseCalculo = BigDecimal.ZERO;
        BigDecimal totalIssDevido = BigDecimal.ZERO;
        BigDecimal totalCorrecao = BigDecimal.ZERO;
        BigDecimal totalCorrigido = BigDecimal.ZERO;
        BigDecimal totalJuros = BigDecimal.ZERO;
        BigDecimal totalMulta = BigDecimal.ZERO;
        BigDecimal totalTotal = BigDecimal.ZERO;

        String lancamentosHtml = "<table border=\"0\" width=\"100%\" style=\"font-size:smaller\">";
        lancamentosHtml += "<tr><td align=\"right\"><b>Mês</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Ano</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Valor Declarado</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Valor Apurado</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Base Cálculo</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>% ISS</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Saldo do ISS</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Correção</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Valor Corrigido</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Juros</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Multa</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Total</b></td></tr>";

        for (LancamentoContabilFiscal lcf : registro.getLancamentosContabeis()) {
            lancamentosHtml += "<tr><td align=\"left\">" + lcf.getMes() + "</td>";
            lancamentosHtml += "<td align=\"right\">" + lcf.getAno() + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getValorDeclarado()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getValorApurado()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getBaseCalculo()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getAliquotaISS()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getIssDevido()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getCorrecao()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getValorCorrigido()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getJuros()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getMulta()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getIssDevido().add(lcf.getCorrecao()).add(lcf.getJuros()).add(lcf.getMulta())) + "</td></tr>";

            totalValorDeclarado = totalValorDeclarado.add(lcf.getValorDeclarado());
            totalValorApurado = totalValorApurado.add(lcf.getValorApurado());
            totalBaseCalculo = totalBaseCalculo.add(lcf.getBaseCalculo());
            totalIssDevido = totalIssDevido.add(lcf.getIssDevido());
            totalCorrecao = totalCorrecao.add(lcf.getCorrecao());
            totalCorrigido = totalCorrigido.add(lcf.getValorCorrigido());
            totalJuros = totalJuros.add(lcf.getJuros());
            totalMulta = totalMulta.add(lcf.getMulta());
            totalTotal = totalTotal.add(lcf.getIssDevido().add(lcf.getCorrecao()).add(lcf.getJuros()).add(lcf.getMulta()));
        }

        lancamentosHtml += "<tr><td colspan=\"14\"><hr><td></tr>";
        lancamentosHtml += "<tr><td align=\"left\" colspan=\"2\"><b>TOTAIS</b></td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalValorDeclarado) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalValorApurado) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalBaseCalculo) + "</td>";
        lancamentosHtml += "<td align=\"right\">&nbsp;</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalIssDevido) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalCorrecao) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalCorrigido) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalJuros) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalMulta) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalTotal) + "</td></tr>";
        lancamentosHtml += "</table>";

        return lancamentosHtml;
    }

    private String montaTabelaLevantamentoCreditoTributario(RegistroLancamentoContabil reg) {
        BigDecimal totalValorDeclarado = BigDecimal.ZERO;
        BigDecimal totalValorApurado = BigDecimal.ZERO;
        BigDecimal totalBaseCalculo = BigDecimal.ZERO;
        BigDecimal totalIssDevido = BigDecimal.ZERO;
        BigDecimal totalIssApurado = BigDecimal.ZERO;
        BigDecimal totalIssPago = BigDecimal.ZERO;

        String lancamentosHtml = "<table border=\"1\" width=\"100%\" style=\"font-size:x-small\">";
        lancamentosHtml += "<tr><td align=\"left\"><b>Mês</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Ano</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Valor Declarado</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Valor Apurado</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>Base Cálculo</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>% ISS</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>ISS Devido</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>ISS Apurado</b></td>";
        lancamentosHtml += "<td align=\"right\"><b>ISS Lançado</b></td></tr>";
        for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
            lancamentosHtml += "<tr><td align=\"left\">" + lcf.getMes() + "</td>";
            lancamentosHtml += "<td align=\"right\">" + lcf.getAno() + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getValorDeclarado()) + "</td>";
            totalValorDeclarado = totalValorDeclarado.add(lcf.getValorDeclarado());
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getValorApurado()) + "</td>";
            totalValorApurado = totalValorApurado.add(lcf.getValorApurado());
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getBaseCalculo()) + "</td>";
            totalBaseCalculo = totalBaseCalculo.add(lcf.getBaseCalculo());
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getAliquotaISS()) + "</td>";
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getIssDevido()) + "</td>";
            totalIssDevido = totalIssDevido.add(lcf.getIssDevido());
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getIssApurado()) + "</td>";
            totalIssApurado = totalIssApurado.add(lcf.getIssApurado());
            lancamentosHtml += "<td align=\"right\">" + dfReais.format(lcf.getIssPago()) + "</td></tr>";
            totalIssPago = totalIssPago.add(lcf.getIssPago());
        }
        lancamentosHtml += "<tr><td align=\"left\" colspan=\"2\"><b>TOTAIS</b></td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalValorDeclarado) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalValorApurado) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalBaseCalculo) + "</td>";
        lancamentosHtml += "<td align=\"right\">&nbsp;</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalIssDevido) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalIssApurado) + "</td>";
        lancamentosHtml += "<td align=\"right\">" + dfReais.format(totalIssPago) + "</td></tr>";
        lancamentosHtml += "</table>";

        return lancamentosHtml;
    }

    // Ex: http://localhost:8080/webpublico/
    public String geraUrlImagemDir() {
        try {
            return FacesUtil.geraUrlImagemDir();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String montarTabelaImoveisDaPessoa(Pessoa pessoa) {
        String html = "<table border=\"0\" width=\"100%\">";
        html += "<tr>";
        html += "<td align=\"center\"><b>Inscrição Imobiliária</b></td>";
        html += "<td align=\"left\"><b>Setor</b></td>";
        html += "<td align=\"left\"><b>Quadra</b></td>";
        html += "<td align=\"left\"><b>Lote</b></td>";
        html += "<td align=\"left\"><b>Bairro</b></td>";
        html += "<td align=\"left\"><b>Logradouro</b></td>";
        html += "<td align=\"left\"><b>Número</b></td>";
        html += "<td align=\"left\"><b>Complemento</b></td>";
        html += "</tr>";

        List<CadastroImobiliario> cadastroImobiliarios = cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoa(pessoa);
        for (CadastroImobiliario ci : cadastroImobiliarios) {
            html += "<tr>";
            html += "<td align=\"center\">" + ci.getInscricaoCadastral() + "</td>";
            html += "<td align=\"left\">" + ci.getLote().getSetor().getCodigo() + "</td>";
            html += "<td align=\"left\">" + ci.getLote().getQuadra().getCodigo() + "</td>";
            html += "<td align=\"left\">" + ci.getLote().getCodigoLote() + "</td>";
            Testada testadaPrincipal = ci.getLote().getTestadaPrincipal();
            if (testadaPrincipal != null && testadaPrincipal.getFace() != null && testadaPrincipal.getFace().getLogradouroBairro() != null) {
                html += "<td align=\"left\">" + testadaPrincipal.getFace().getLogradouroBairro().getBairro() + "</td>";
                html += "<td align=\"left\">" + testadaPrincipal.getFace().getLogradouroBairro().getLogradouro() + "</td>";
            } else {
                html += "<td align=\"left\"></td>";
                html += "<td align=\"left\"></td>";
            }
            html += "<td align=\"left\">" + (ci.getNumero() != null ? ci.getNumero() : "") + "</td>";
            html += "<td align=\"left\">" + (ci.getComplementoEndereco() != null ? ci.getComplementoEndereco() : "") + "</td>";
            html += "</tr>";
        }

        html += "</table>";

        return html;
    }

    private String montarTabelaAgentesFiscaisPorAcaoFiscalAndMatricula(AcaoFiscal acaoFiscal, boolean matricula) {
        StringBuilder fiscaisHtml = new StringBuilder("");
        fiscaisHtml.append("<div style=\"display : table\">");
        fiscaisHtml.append("  <div style=\"display: table-row; height: 5px\"></div>");

        for (FiscalDesignado fiscal : acaoFiscal.getFiscalDesignados()) {
            MatriculaFP matriculaFP = null;
            if (matricula) {
                matriculaFP = matriculaFPFacade.matriculaFPVigentePorPessoa(fiscal.getUsuarioSistema().getPessoaFisica());
            }

            String nome = "";
            if (fiscal.getUsuarioSistema().getPessoaFisica() != null) {
                nome = fiscal.getUsuarioSistema().getPessoaFisica().getNome();
            }
            fiscaisHtml.append("<div style=\"display : table-row\">");
            fiscaisHtml.append("    <div style=\"width:50%;display : table-cell; vertical-align : middle!important\"> ");
            fiscaisHtml.append(nome).append(matriculaFP != null ? " - Matrícula Nº " + matriculaFP.getMatricula() : "");
            fiscaisHtml.append("    </div>");
            fiscaisHtml.append("    <div style=\"width:30%; display : table-cell; vertical-align : middle!important\">");
            fiscaisHtml.append("        <hr style=\"width:90%; border-style: inset; display:block\" align=\"center\"/>");
            fiscaisHtml.append("    </div>");
            fiscaisHtml.append("</div>");
            fiscaisHtml.append("   <div style=\"display: table-row; height: 5px\"></div>");
        }
        fiscaisHtml.append("</div>");
        return fiscaisHtml.toString();
    }

    private String montaTabelaLancamentosMultasFiscal(RegistroLancamentoContabil reg) {
        BigDecimal totalPenalidades = BigDecimal.ZERO;
        BigDecimal totalUFM = BigDecimal.ZERO;

        String multasHtml = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        multasHtml += "<tr>";
        multasHtml += "<td align=\"left\"><b>Mês</b></td>";
        multasHtml += "<td align=\"center\"><b>Ano</b></td>";
        multasHtml += "<td align=\"left\"><b>Multa</b></td>";
        multasHtml += "<td align=\"right\"><b>Aliquota(%)</b></td>";
        multasHtml += "<td align=\"right\"><b>Valor(R$)</b></td>";
        multasHtml += "<td align=\"right\"><b>Valor(UFM)</b></td>";

        multasHtml += "</tr>";
        multasHtml += "<tr><td colspan=\"6\"><hr></td></tr>";
        Collections.sort(reg.getMultas());
        for (LancamentoMultaFiscal lmf : reg.getMultas()) {
            multasHtml += "<tr>";
            multasHtml += "<td align=\"left\">" + (lmf.getMes() != null ? lmf.getMes().getDescricao() : "-") + "</td>";
            multasHtml += "<td align=\"center\">" + lmf.getAno() + "</td>";
            multasHtml += "<td align=\"left\">" + lmf.getMultaFiscalizacao().getArtigo() + "</td>";
            multasHtml += "<td align=\"right\">" + dfReais.format(lmf.getAliquota() != null ? lmf.getAliquota() : 0) + "</td>";
            multasHtml += "<td align=\"right\">" + dfReais.format(lmf.getValorMulta() != null ? lmf.getValorMulta() : 0) + "</td>";
            multasHtml += "<td align=\"right\">" + dfUFM.format(lmf.getValorMultaIndice() != null ? lmf.getValorMultaIndice() : 0) + "</td>";
            multasHtml += "</tr>";
            totalPenalidades = totalPenalidades.add(lmf.getValorMulta());
            totalUFM = totalUFM.add(lmf.getValorMultaIndice());
        }
        multasHtml += "<tr><td colspan=\"6\"><hr></td></tr>";

        multasHtml += "<tr><td colspan=\"3\">&nbsp;</td>";
        multasHtml += "<td align=\"left\"><b>TOTAL PENALIDADES:</b></td>";
        multasHtml += "<td align=\"right\">" + dfReais.format(totalPenalidades) + "</td>";
        multasHtml += "<td align=\"right\">" + dfUFM.format(totalUFM) + "</td></tr>";


        multasHtml += "</table>";
        return multasHtml;
    }

//    private String montaTabelaEmbasamentosMultasFiscal(RegistroLancamentoContabil reg) {
//        Set<MultaFiscalizacao> multas = Sets.newHashSet();
//        for (LancamentoMultaFiscal lmf : reg.getMultas()) {
//            multas.add(lmf.getMultaFiscalizacao());
//        }
//        String embasamentosHtml = "<table border=\"0\" width=\"100%\">";
//        for (MultaFiscalizacao multa : multas) {
//            embasamentosHtml += "<tr><td align=\"left\" style=\"font-size:x-small\"><b>" + multa.getArtigo() + "</b></td></tr>";
//            embasamentosHtml += "<tr><td><hr></td></tr>";
//            embasamentosHtml += "<tr><td align=\"left\">" + multa.getEmbasamento() + "</td></tr>";
//        }
//        embasamentosHtml += "</table>";
//        System.out.println("embasamentosHtml >> " + embasamentosHtml);
//        System.out.println("embasamentosHtml fim ---------- " );
//        return embasamentosHtml;
//    }

    private String montaTabelaEmbasamentosMultasFiscal(RegistroLancamentoContabil reg) {
        Set<MultaFiscalizacao> multas = Sets.newHashSet();
        for (LancamentoMultaFiscal lmf : reg.getMultas()) {
            multas.add(lmf.getMultaFiscalizacao());
        }
        String embasamentosHtml = "";
        for (MultaFiscalizacao multa : multas) {
            embasamentosHtml += "<br />" + multa.getArtigo() + "<br />";
            embasamentosHtml += multa.getEmbasamento() + "<br />";
        }
        return embasamentosHtml;
    }

    private String montaTabelaDeCadastroDaPeticao(Peticao peticao) throws UFMException {
        String div = "";
        peticao = peticaoFacade.recuperar(peticao.getId());
        for (ItemPeticao itemPeticao : peticao.getItensPeticao()) {
            if (itemPeticao.getCertidaoDividaAtiva().getCadastro() instanceof CadastroImobiliario) {
                div = montaDivParaCadastroImobiliario(div, itemPeticao, peticao);
            }
            if (itemPeticao.getCertidaoDividaAtiva().getCadastro() instanceof CadastroEconomico) {
                div = montaDivParaCadastroEconomico(div, itemPeticao, peticao);
            }
            if (itemPeticao.getCertidaoDividaAtiva().getCadastro() instanceof CadastroRural) {
                div = montaDivParaCadastroRural(div, itemPeticao, peticao);
            }
        }
        return div;
    }

    private String montaDivParaCadastroImobiliario(String div, ItemPeticao itemPeticao, Peticao peticao) throws UFMException {
        if (itemPeticao.getCertidaoDividaAtiva().getCadastro() != null) {
            CadastroImobiliario cadastroImobiliario = this.cadastroImobiliarioFacade.recuperar(itemPeticao.getCertidaoDividaAtiva().getCadastro().getId());
            Lote lote = new Lote();
            if (cadastroImobiliario.getLote() != null) {
                lote = this.loteFacade.recuperar(cadastroImobiliario.getLote().getId());
            }

            //DIV PARA CADASTRO IMOBILIARIO
            div += "<div style=\"border: 1px solid black\">";
            div += "<table style=\"width: 100%; font-size: 10px\">";
            div += "<tr>";
            div += " <td> Código: " + cadastroImobiliario.getCodigo() + " </td>";
            div += " <td> INSC. CAD.: " + cadastroImobiliario.getInscricaoCadastral() + " </td>";
            div += " <td> LOT: " + lote.getCodigoLote() + " </td>";
            div += " <td> QD: " + descricaoQuadra(lote) + " </td>";
            div += " <td> LT: " + descricaoLote(lote) + "</td>";
            div += "</tr><tr>";
            div += " <td colspan=\"2\"> ENDERECO: " + descricaoEndereco(lote) + " </td>";
            div += " <td colspan=\"3\"> BAIRRO: " + descricaoBairro(lote) + " </td>";
            div += "</tr><tr>";
            div += " <td> CERTIDAO: " + itemPeticao.getCertidaoDividaAtiva().getNumero() + "/" + itemPeticao.getCertidaoDividaAtiva().getExercicio().getAno() + " </td>";
            if (itemPeticao.getCertidaoDividaAtiva().getDataCertidao() != null) {
                div += " <td> DATA: " + formatterData.format(itemPeticao.getCertidaoDividaAtiva().getDataCertidao()) + "</td>";
            } else {
                div += " <td> DATA: </td>";
            }
            div += " <td> Vlr Total Original: " + dfReais.format(itemPeticao.getValorOriginal()) + " </td>";
            div += " <td> Vlr Total Atualizado: " + dfReais.format(itemPeticao.getAtualizado()) + " </td>";
            div += "</tr>";
            div += "</table>";
            div += "</div>";
        }
        return div;
    }

    private String descricaoQuadra(Lote lote) {
        try {
            return lote.getQuadra().getDescricao();
        } catch (NullPointerException e) {
            return "-";
        }
    }

    private String descricaoLote(Lote lote) {
        try {
            return lote.getDescricaoLoteamento();
        } catch (NullPointerException e) {
            return "-";
        }
    }

    private String descricaoEndereco(Lote lote) {
        try {
            return lote.getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getNome();
        } catch (NullPointerException e) {
            return "-";
        }
    }

    private String descricaoBairro(Lote lote) {
        try {
            return "removido";
        } catch (NullPointerException e) {
            return "-";
        }
    }

    private String montaDivParaCadastroEconomico(String div, ItemPeticao itemPeticao, Peticao peticao) throws UFMException {
        if (itemPeticao.getCertidaoDividaAtiva().getCadastro() != null) {
            CadastroEconomico ce = this.cadastroEconomicoFacade.recuperar(itemPeticao.getCertidaoDividaAtiva().getCadastro().getId());
            Pessoa p = this.pessoaFacade.recuperar(ce.getPessoa().getId());
            //DIV PARA CADASTRO ECONOMICO

            div += "<div style=\"border: 1px solid black\">"
                + "<table style=\"width: 100%; font-size: 10px\">"
                + "<tr>"
                + " <td colspan=\"3\"> CADASTRO ECONOMICO : " + ce.getInscricaoCadastral() + "</td>"
                + "</tr>"
                + "<tr>"
                + " <td colspan=\"2\"> NOME/RAZAO SOCIAL:" + p.getNome() + " </td>"
                + " <td> CPF/CNPJ:" + p.getCpf_Cnpj() + " </td>"
                + "</tr>"
                + "<tr> "
                + " <td colspan=\"2\"> ENDERECO:" + p.getEnderecos().get(0).getLogradouro() + " </td>"
                + " <td> BAIRRO: " + p.getEnderecos().get(0).getBairro() + " </td>"
                + "</tr> "
                + "<tr>"
                + " <td> CERTIDAO: " + itemPeticao.getCertidaoDividaAtiva().getNumero() + "/" + itemPeticao.getCertidaoDividaAtiva().getExercicio().getAno() + " </td>"
                + " <td> DATA: " + formatterData.format(itemPeticao.getCertidaoDividaAtiva().getDocumentoOficial().getDataEmissao()) + "</td>"
                + " <td> Vlr Total Original: " + dfReais.format(itemPeticao.getValorOriginal()) + " </td>"
                + " <td> Vlr Total Atualizado: " + dfReais.format(itemPeticao.getAtualizado()) + " </td>"
                + "</tr>"
                + "</table>"
                + "</div>";
        }
        return div;
    }

    private String montaDivParaCadastroRural(String div, ItemPeticao itemPeticao, Peticao peticao) throws UFMException {
        if (itemPeticao.getCertidaoDividaAtiva().getCadastro() != null) {
            CadastroRural cr = this.cadastroRuralFacade.recuperar(itemPeticao.getCertidaoDividaAtiva().getCadastro().getId());
            //DIV PARA CADASTRO RURAL
            div += " <div style=\"border: 1px solid black\">"
                + "<table style=\"width: 100%; font-size: 10px\">"
                + "<tr>"
                + " <td colspan=\"3\"> CODIGO CADASTRO RURAL: " + cr.getCodigo() + " </td>"
                + "</tr>"
                + "<tr>"
                + " <td colspan=\"2\">NOME DA PROPRIEDADE: " + cr.getNomePropriedade() + " </td>"
                + " <td> LOCALIZACAO: " + cr.getLocalizacaoLote() + " </td>"
                + "</tr>"
                + "<tr>"
                + " <td> AREA: " + dfReais.format(cr.getAreaLote()) + " </td>"
                + " <td> TIPO DE AREA: " + cr.getTipoAreaRural() + "</td>"
                + " <td> INCRA:	" + cr.getNumeroIncra() + " </td>"
                + "</tr>"
                + "<tr>"
                + " <td> CERTIDAO: " + itemPeticao.getCertidaoDividaAtiva().getNumero() + "/" + itemPeticao.getCertidaoDividaAtiva().getExercicio().getAno() + " </td>"
                + " <td> DATA: " + formatterData.format(itemPeticao.getCertidaoDividaAtiva().getDocumentoOficial().getDataEmissao()) + "</td>"
                + " <td> Vlr Total Original: " + dfReais.format(itemPeticao.getValorOriginal()) + " </td>"
                + " <td> Vlr Total Atualizado: " + dfReais.format(itemPeticao.getAtualizado()) + " </td>"
                + "</tr>"
                + "</table>"
                + "</div>";
        }
        return div;
    }

    private void tagsParecerRecursoFiscalizacao(VelocityContext context, RecursoFiscalizacao recurso) {
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.DATA_ENTRADA.name(), formatterData.format(recurso.getDataEntrada()));
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.DATA_NOVO_PRAZO.name(), formatterData.format(recurso.getDataNovoPrazo()));
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.DATA_PARECER.name(), formatterData.format(recurso.getDataParecer()));
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.PARECER.name(), recurso.getParecerRecurso().toString());
        if (recurso.getResultadoParecerFiscalizacao() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.RESULTADO_PARECER.name(), recurso.getResultadoParecerFiscalizacao().getDescricao());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.RESULTADO_PARECER.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.TEOR.name(), recurso.getTeorRecurso());
        addTag(context, TipoModeloDoctoOficial.TagsParecerRecursoFiscalizacao.TIPO_RECURSO.name(), recurso.getTipoRecursoFiscalizacao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsGeraisSemValidacao.OBSERVACAO.name(), recurso.getParecerRecurso().toString());
    }

    private String montaListaCnae(AcaoFiscal acaoFiscal) {
        String retorno = "";
        List<EconomicoCNAE> cnaes = this.cadastroEconomicoFacade.getEconomicoCNAEVigentes(acaoFiscal.getCadastroEconomico());
        if (!cnaes.isEmpty()) {
            for (EconomicoCNAE economicoCNAE : cnaes) {
                retorno += " " + economicoCNAE.getCnae().getCodigoCnae() + " - " + economicoCNAE.getCnae().getDescricaoDetalhada() + ",";
            }
            return (retorno.substring(0, retorno.length() - 3)) + ".";
        } else {
            return "";
        }
    }

    public String montaListaTelefones(Pessoa pessoa) {
        StringBuilder telefones = new StringBuilder();
        List<Telefone> lista = pessoaFacade.telefonePorPessoa(pessoa);
        for (Telefone tel : lista) {
            telefones.append(tel.getTelefone());
            telefones.append("\n ");
        }
        return telefones.toString();
    }

    private String montaAssinaturaFiscais(AcaoFiscal acaoFiscal, boolean matricula) {
        String retorno = "<table style='width:100%'><tr>";
        int contador = 0;
        for (FiscalDesignado fiscalDesignado : acaoFiscal.getFiscalDesignados()) {
            MatriculaFP matriculaFP = null;
            if (matricula) {
                matriculaFP = matriculaFPFacade.matriculaFPVigentePorPessoa(fiscalDesignado.getUsuarioSistema().getPessoaFisica());
            }

            String nomeUsuarioSistema = fiscalDesignado.getUsuarioSistema().getPessoaFisica() == null ? "" : fiscalDesignado.getUsuarioSistema().getPessoaFisica().getNome();
            retorno += "<td align='left'> <h4>" + nomeUsuarioSistema + (matriculaFP != null ? " - Matrícula Nº " + matriculaFP.getMatricula() : "") + "<br/></h4></td>";
            if (contador == 1) {
                retorno += "</tr><tr>";
                contador = 0;
            }
            contador++;
        }
        retorno += "</tr></table>";
        return retorno.replace("null", "");
    }

    private Object formataNumeroLevantamento(AcaoFiscal acaoFiscal) {
        String retorno = "";
        if (acaoFiscal.getNumeroLevantamento() != null) {
            retorno = StringUtil.preencheString(acaoFiscal.getNumeroLevantamento().toString(), 5, '0');
            retorno = retorno + "/" + acaoFiscal.getAno();
        }
        return retorno;
    }

    private void tagsTermoGerais(VelocityContext context, TermoGeralFiscalizacao termoGeralFiscalizacao) {
        for (ValorAtributoFiscalizacao atributo : termoGeralFiscalizacao.getValoresAtributosFiscalizacao()) {
            addTag(context, atributo.getAtributoDoctoOficial().getTag(), atributo.getValor());
        }
    }

    public DocumentoOficial geraDocumentoTermoInscricaoDividaAtiva(LinhaDoLivroDividaAtiva linhaDoLivroDividaAtiva, DocumentoOficial documento, Cadastro cadastro, Pessoa pessoa, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(linhaDoLivroDividaAtiva, mod, cadastro, pessoa);
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    private void tagsFiscalizacaoRBTrans(VelocityContext context, FiscalizacaoRBTrans fisc, DocumentoOficial documentoOficial) throws UFMException, AtributosNulosException {
        fisc = fiscalizacaoRBTransFacade.recuperar(fisc);
        CadastroEconomico permissionario = fisc.getAutuacaoFiscalizacao().getCadastroEconomico();
        Pessoa pessoa = permissionario != null ? permissionario.getPessoa() : fisc.getAutuacaoFiscalizacao().getPessoaClandestina() != null ? fisc.getAutuacaoFiscalizacao().getPessoaClandestina() : null;
        addTag(context, TipoModeloDoctoOficial.TagsRBTrans.CPF_PERMISSIONARIO.name(), pessoa != null ? pessoa.getCpf_Cnpj() : "");
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.CPF_CNPJ_PERMISSIONARIO.name(), pessoa != null ? pessoa.getCpf_Cnpj() : "");
        if (permissionario != null && pessoa != null && pessoa instanceof PessoaFisica) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_PERMISSIONARIO.name(), nomeDoCampo(pessoa, "dataNascimento", TipoFormato.DATA));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_PERMISSIONARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.LOGRADOURO_PERMISSIONARIO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.BAIRRO_PERMISSIONARIO.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.TIPO_TRANSPORTE.name(), fisc.getAutuacaoFiscalizacao().getOcorrenciasAutuacao().get(0).getOcorrenciaFiscalizacao().getEspecieTransporte().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_SEQUENCIAL_DOCUMENTO.name(), nomeDoCampo(documentoOficial, "numero", null));
        PermissaoTransporte permissaoTransporte = permissaoTransporteFacade.recuperaPermissoesMotoristaAuxiliar(fisc.getAutuacaoFiscalizacao().getCadastroEconomico());
        if (permissaoTransporte != null && !permissaoTransporte.getMotoristasAuxiliares().isEmpty()) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_PERMISSAO.name(), permissaoTransporte.getNumero());
            if (permissaoTransporte.getMotoristasAuxiliares().size() == 1) {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_MOTORISTAAUX.name(), permissaoTransporte.getMotoristasAuxiliares().get(0).getPessoaFisica().getDataNascimento() != null ? formatterData.format(permissaoTransporte.getMotoristasAuxiliares().get(0).getPessoaFisica().getDataNascimento()) : "");
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_MOTORISTAAUX.name(), permissaoTransporte.getMotoristasAuxiliares().get(0).getPessoaFisica().getDataNascimento() != null ? formatterData.format(permissaoTransporte.getMotoristasAuxiliares().get(0).getPessoaFisica().getDataNascimento()) : "");
                addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_MOTORISTAAUX1.name(), permissaoTransporte.getMotoristasAuxiliares().get(1).getPessoaFisica().getDataNascimento() != null ? formatterData.format(permissaoTransporte.getMotoristasAuxiliares().get(1).getPessoaFisica().getDataNascimento()) : "");
            }
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_PERMISSAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_MOTORISTAAUX.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_NASCIMENTO_MOTORISTAAUX1.name(), "");
        }
        if (fisc.getAutuacaoFiscalizacao().getMotoristaInfrator() != null) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.CPF_MOTORISTA_INFRATOR.name(), fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getCpf());
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NOME_MOTORISTA_INFRATOR.name(), fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getNome());

        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.CPF_MOTORISTA_INFRATOR.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NOME_MOTORISTA_INFRATOR.name(), "");
        }
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_AUTUACAO.name(), formatterData.format(fisc.getAutuacaoFiscalizacao().getDataAutuacao()));
        addTag(context, TipoModeloDoctoOficial.TagsRBTrans.DATA_EMISSAO_TERMO.name(), formatterData.format(new Date()));
        NotificacaoRBTrans notificacaoAutuacao = null;
        for (NotificacaoRBTrans obj : fisc.getAutuacaoFiscalizacao().getNotificacoes()) {
            if (obj.getTipoProcessoRBTrans().equals(TipoProcessoRBTrans.AUTUACAO)) {
                notificacaoAutuacao = obj;
            }
        }
        if (notificacaoAutuacao != null) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_EMISSAO_NOTIFICACAO_AUTUACAO.name(), formatterData.format(notificacaoAutuacao.getDataNitificacao()));
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DESCRICAO_DETALHADA.name(), fisc.getAutuacaoFiscalizacao().getDescricaoDetalhada());
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_NOTIFICACAO.name(), notificacaoAutuacao.getNumero() + "/" + notificacaoAutuacao.getAno());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DATA_EMISSAO_NOTIFICACAO_AUTUACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.DESCRICAO_DETALHADA.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_NOTIFICACAO.name(), "");
        }
        EnderecoCorreio enderecoInfrator = null;
        if (fisc.getAutuacaoFiscalizacao().getMotoristaInfrator() != null) {
            if (!fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getEnderecoscorreio().isEmpty()) {
                for (EnderecoCorreio obj : fisc.getAutuacaoFiscalizacao().getMotoristaInfrator().getEnderecoscorreio()) {
                    if (obj.getPrincipal()) {
                        enderecoInfrator = obj;
                    }
                }
            }
        }
        VeiculoPermissionario veiculoPermissionario = null;
        if (permissaoTransporte != null) {
            veiculoPermissionario = permissaoTransporteFacade.retornaUltimoVeiculo(false, permissaoTransporte);
        }
        VeiculoTransporte veiculoTransporte = null;
        if (veiculoPermissionario != null) {
            veiculoTransporte = veiculoPermissionario.getVeiculoTransporte();
        }
        if (veiculoTransporte != null) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.PLACA_VEICULO.name(), veiculoTransporte.getPlaca());
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.VEICULO_MARCA_MODELO.name(), veiculoTransporte.getModelo() + "/" + veiculoTransporte.getModelo().getMarca());
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.PLACA.name(), veiculoTransporte.getPlaca());
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MODELO.name(), veiculoTransporte.getModelo());
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MARCA.name(), veiculoTransporte.getModelo().getMarca());
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.PLACA_VEICULO.name(), fisc.getAutuacaoFiscalizacao().getPlacaVeiculo() != null ? fisc.getAutuacaoFiscalizacao().getPlacaVeiculo() : "");
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.VEICULO_MARCA_MODELO.name(), fisc.getAutuacaoFiscalizacao().getDescricaoVeiculo() != null ? fisc.getAutuacaoFiscalizacao().getDescricaoVeiculo() : "");
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.PLACA.name(), fisc.getAutuacaoFiscalizacao().getPlacaVeiculo() != null ? fisc.getAutuacaoFiscalizacao().getPlacaVeiculo() : "");
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MODELO.name(), fisc.getAutuacaoFiscalizacao().getDescricaoVeiculo() != null ? fisc.getAutuacaoFiscalizacao().getDescricaoVeiculo() : "");
            addTag(context, TipoModeloDoctoOficial.TagsRBTrans.MARCA.name(), "");
        }

        if (enderecoInfrator != null) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.ENDERECO_MOTORISTA_INFRATOR.name(), enderecoInfrator);
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.ENDERECO_MOTORISTA_INFRATOR.name(), "");
        }
        EnderecoCorreio enderecoPermissionario = null;
        if (permissionario != null) {
            for (EnderecoCorreio obj : permissionario.getPessoa().getEnderecoscorreio()) {
                if (obj.getPrincipal()) {
                    enderecoPermissionario = obj;
                }
            }
        }
        if (enderecoPermissionario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.ENDERECO_PERMISSIONARIO.name(), enderecoPermissionario);
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.ENDERECO_PERMISSIONARIO.name(), "");
        }

        if (fisc.getAutuacaoFiscalizacao().getAgenteAutuador() != null) {
            String agente_autuador = fisc.getAutuacaoFiscalizacao().getAgenteAutuador().toString();
            if (fisc.getAutuacaoFiscalizacao().getAgenteAutuador().getPessoaFisica().getRg() != null) {
                agente_autuador += " RG Nº " + fisc.getAutuacaoFiscalizacao().getAgenteAutuador().getPessoaFisica().getRg();
            }
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.FISCAL_AUTUADOR.name(), agente_autuador);
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.FISCAL_AUTUADOR.name(), "");
        }

        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.HORA_AUTUACAO.name(), new SimpleDateFormat("HH:mm").format(fisc.getAutuacaoFiscalizacao().getDataAutuacao()));
        String infracoes = "<ul>";
        BigDecimal valorTotalMulta = BigDecimal.ZERO;
        for (OcorrenciaAutuacaoRBTrans obj : fisc.getAutuacaoFiscalizacao().getOcorrenciasAutuacao()) {
            valorTotalMulta = valorTotalMulta.add(obj.getOcorrenciaFiscalizacao().getValor());
            infracoes += "<li>" + obj.getOcorrenciaFiscalizacao() + "<li/>";
        }
        infracoes += "<ul/>";

        BigDecimal valorTotalMultaReal = valorTotalMulta.multiply(moedaFacade.recuperaValorVigenteUFM());
        valorTotalMultaReal = valorTotalMultaReal.setScale(2, RoundingMode.HALF_EVEN);

        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.INFRACOES.name(), infracoes);
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.VALOR_MULTA.name(), valorTotalMultaReal);
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.LOCAL_AUTUACAO.name(), fisc.getAutuacaoFiscalizacao().getLocalAutuacao());
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NOME_PERMISSIONARIO.name(), pessoa != null ? pessoa.getNome() : "");
        addTag(context, TipoModeloDoctoOficial.TagsFiscalizacaoRBTrans.NUMERO_AUTUACAO.name(), fisc.getAutuacaoFiscalizacao().getCodigo());
    }

    private void tagsTermoDividaAtiva(VelocityContext context, LinhaDoLivroDividaAtiva linhaDoLivroDividaAtiva) throws UFMException {

        LivroDividaAtiva livroDividaAtiva = linhaDoLivroDividaAtiva.getItemLivroDividaAtiva().getLivroDividaAtiva();
        InscricaoDividaAtiva inscricao = linhaDoLivroDividaAtiva.getItemInscricaoDividaAtiva().getInscricaoDividaAtiva();

        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.NUMERO_TERMO.name(), linhaDoLivroDividaAtiva.getTermoInscricaoDividaAtiva().getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.DATA_INSCRICAO.name(), formatterData.format(inscricao.getDataInscricao()));
        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.NUMERO_LIVRO.name(), livroDividaAtiva.getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.PAGINA_LIVRO.name(), linhaDoLivroDividaAtiva.getPagina());
        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.ORIGEM_DIVIDA.name(), linhaDoLivroDividaAtiva.getItemInscricaoDividaAtiva().getDivida().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsTermoDividaAtiva.DISCRIMINACAO_VALORES.name(), montaTabelaDeValoresDoTermoInscricao(linhaDoLivroDividaAtiva));
    }

    private void tagsProtocolo(VelocityContext context, Processo processo, Tramite tramite) throws UFMException {
        if (processo.getSubAssunto() == null) {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.ASSUNTO.name(), processo.getAssunto());
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.SUB_ASSUNTO.name(), "");
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.ASSUNTO.name(), processo.getSubAssunto().getAssunto().getNome());
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.SUB_ASSUNTO.name(), processo.getSubAssunto().getDescricao());
        }
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.DATA_PROCESSO.name(), "Rio Branco, " + new SimpleDateFormat("d' de 'MMMM' ('EEEE') de 'yyyy", new Locale("pt", "BR")).format(processo.getDataRegistro()));
        HierarquiaOrganizacional hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(processo.getUoCadastro(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA, processo.getDataRegistro());
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.UNIDADE_ORGANIZACIONAL_RESP.name(), hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : processo.getUoCadastro().getDescricao());
        if (processo.getSenha() == null) {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.SENHA_CONSULTA.name(), "");
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.SENHA_CONSULTA.name(), processo.getSenha());
        }
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.NUMERO_PROCESSO.name(), processo.getNumero() + "/" + processo.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.NOME_REQUERENTE.name(), processo.getPessoa().getNome());
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.CPF_CNPJ_REQUERENTE.name(), processo.getPessoa().getCpf_Cnpj());
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.SITUACAO.name(), processo.getSituacao().getDescricao());
        if (!processo.getPessoa().getEnderecos().isEmpty()) {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.ENDERECO_REQUERENTE.name(), processo.getPessoa().getEnderecos().get(0));
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.ENDERECO_REQUERENTE.name(), "");
        }

        String tramites = "";
        if (!processo.getTramites().isEmpty()) {
            tramites = " <table bordercolor=\"#000000\" border=\"1\" style=\"width:100%; border-collapse: collapse\" cellpadding=\"2\">";
            for (Tramite tra : processo.getTramites()) {
                String tipoPrazo = "horas";
                if (tra.getTipoPrazo() != null) {
                    tipoPrazo = tra.getTipoPrazo().getDescricao();
                }
                if (processo.getProtocolo()) {
                    if (tra.getUnidadeOrganizacional() != null) {
                        hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(tra.getUnidadeOrganizacional(),
                            TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                        tramites += "<tr><td style=\"width:95%\">" + (hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : tra.getUnidadeOrganizacional()) + "</td><td style=\"width:5%\">&nbsp;</td></tr>";
                    } else {
                        tramites += "<tr><td style=\"width:95%\">Externo: " + tra.getDestinoExterno() + "</td><td style=\"width:5%\">&nbsp;</td></tr>";
                    }
                } else {
                    hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(tra.getUnidadeOrganizacional(),
                        TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                    tramites += "<tr><td style=\"width:95%\">" + (hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : tra.getUnidadeOrganizacional()) + ", Prazo estimado " + tra.getPrazo() + " " + tipoPrazo + "." + "</td><td style=\"width:5%\">&nbsp;</td></tr>";
                }
            }
            tramites += "</table><br/> ";
        }
        addTag(context, TipoModeloDoctoOficial.TagsProtocolo.TRAMITES_PROCESSO.name(), tramites);

        if (tramite != null) {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.NUMERO_PARECER.name(), StringUtil.cortaOuCompletaEsquerda((tramite.getIndice() + 1) + "", 2, "0") + "/" + processo.getExercicio().getAno());
            if (tramite.getDataConclusao() != null) {
                addTag(context, TipoModeloDoctoOficial.TagsProtocolo.DATA_PARECER.name(), "Rio Branco, " + new SimpleDateFormat("d' de 'MMMM' ('EEEE') de 'yyyy", new Locale("pt", "BR")).format(tramite.getDataConclusao()));
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProtocolo.DATA_PARECER.name(), "Rio Branco, " + new SimpleDateFormat("d' de 'MMMM' ('EEEE') de 'yyyy", new Locale("pt", "BR")).format(tramite.getDataRegistro()));
            }
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.RESPONSAVEL_PARECER.name(), tramite.getResponsavelParecer());
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.TEXTO_PARECER.name(), tramite.getParecer());
            if (tramite.getUnidadeOrganizacional() != null) {
                hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(tramite.getUnidadeOrganizacional(),
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                addTag(context, TipoModeloDoctoOficial.TagsProtocolo.UNIDADE_DESTINO.name(), hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : tramite.getDestinoTramite());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsProtocolo.UNIDADE_DESTINO.name(), tramite.getDestinoTramite());
            }

        } else {
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.DATA_PARECER.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.NUMERO_PARECER.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.RESPONSAVEL_PARECER.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.TEXTO_PARECER.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsProtocolo.UNIDADE_DESTINO.name(), "");
        }
    }

    private void tagsContratoRendasPatrimoniais(VelocityContext context, ContratoRendasPatrimoniais contrato) throws UFMException {
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NUMERO_CONTRATO.name(), contrato.getNumeroContrato());
        String pontosResumidos = "";
        String pontosDetalhados = "";
        String pontosValores = "";
        Localizacao localizacao = null;

        for (PontoComercialContratoRendasPatrimoniais p : contrato.getPontoComercialContratoRendasPatrimoniais()) {
            PontoComercial ponto = p.getPontoComercial();

            if (localizacao == null) {
                localizacao = ponto.getLocalizacao();

            }
            pontosResumidos += "BOX Nº " + ponto.getNumeroBox();
            if (ponto.getLocalizacao() != null) {
                pontosResumidos += " DO " + ponto.getLocalizacao().getDescricao().toUpperCase();
            }
            pontosResumidos += "<br/>";

            if (ponto.getLocalizacao() != null && ponto.getTipoPontoComercial() != null &&
                ponto.getLocalizacao().getBairro() != null && ponto.getLocalizacao().getLogradouro() != null) {
                pontosDetalhados += "O Box Nº " + ponto.getNumeroBox() + " medindo " + ponto.getArea() + " m² de área, situado no(a) "
                    + ponto.getLocalizacao().getDescricao() + " (" + ponto.getTipoPontoComercial().getDescricao() + "), localizado no bairro "
                    + ponto.getLocalizacao().getBairro().getDescricao() + ", " + ponto.getLocalizacao().getLogradouro().getNome()
                    + " " + ponto.getLocalizacao().getNumero() + "<br/>";
            }

            DecimalFormat area = new DecimalFormat("#,##0.00");
            if (p.getValorCalculadoMes() == null) {
                p.setValorCalculadoMes(BigDecimal.ZERO);
            }
            if (ponto.getArea() == null) {
                ponto.setArea(BigDecimal.ZERO);
            }
            pontosValores += dfUFM.format(p.getValorCalculadoMes()) + " UFM, pelo uso de " + area.format(ponto.getArea()) + " metros quadrados do imóvel, objeto do presente Termo. <br/>";
        }

        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PONTOS_COMERCIAIS_RESUMO.name(), pontosResumidos);
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PONTOS_COMERCIAIS_DETALHADA.name(), pontosDetalhados);
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PONTOS_COMERCIAIS_VALORES.name(), pontosValores);
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.VIGENCIA_CONTRATO.name(), contrato.getPeriodoVigencia());
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DIA_DATA_CONTRATO.name(), new SimpleDateFormat("dd").format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.MES_DATA_CONTRATO.name(), Mes.getMesToInt(Integer.valueOf(new SimpleDateFormat("MM").format(contrato.getDataInicio()))));
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.ANO_DATA_CONTRATO.name(), new SimpleDateFormat("yyyy").format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DATA_CONTRATO.name(), formatterData.format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DATA_CONTRATO_EXTENSO.name(), "Rio Branco - AC, " + new SimpleDateFormat("EEEE, d' de 'MMMM' de 'yyyy", new Locale("pt", "BR")).format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.ATIVIDADE_CONTRATO.name(), contrato.getAtividadePonto() != null ? contrato.getAtividadePonto().getDescricao() : "");
        if (localizacao != null) {
            UnidadeOrganizacional uo = localizacao.getSecretaria();
            if (uo != null) {
                HierarquiaOrganizacional hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(uo,
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA, sistemaFacade.getDataOperacao());
                if (hierarquiaOrganizacional != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DESCRICAO_UNIDADE_ORGANIZACIONAL.name(),
                        hierarquiaOrganizacional.getDescricao());
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DESCRICAO_UNIDADE_ORGANIZACIONAL.name(), "");
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_SECRETARIA.name(), hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : uo.getDescricao());

                if (uo.getEntidade() != null && uo.getEntidade().getPessoaJuridica() != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CNPJ_LOCALIZACAO.name(), uo.getEntidade().getPessoaJuridica().getCnpj());
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CNPJ_LOCALIZACAO.name(), "");
                }
                if (uo.getEndereco() != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.LOGRADOURO_LOCALIZACAO.name(), uo.getEndereco().getLogradouro().getNome());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NUMERO_LOCALIZACAO.name(), uo.getEndereco().getNumero());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.BAIRRO_LOCALIZACAO.name(), "removido");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CEP_LOCALIZACAO.name(), "removido");
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.LOGRADOURO_LOCALIZACAO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NUMERO_LOCALIZACAO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.BAIRRO_LOCALIZACAO.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CEP_LOCALIZACAO.name(), "");
                }
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_SECRETARIA.name(), "");
            }
            if (localizacao.getRepresentanteSecretaria() != null) {
                PessoaFisica representante = (PessoaFisica) pessoaFacade.recuperarPF(localizacao.getRepresentanteSecretaria().getId());
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_REPRESENTANTE_LOCALIZACAO.name(), representante.getNome());
                RG rg = representante.getRg();
                if (rg != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.RG_REPRESENTANTE_LOCALIZACAO.name(), rg.getNumero() + ", " + rg.getOrgaoEmissao() + " " + rg.getUf());
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CPF_REPRESENTANTE_LOCALIZACAO.name(), representante.getCpf());
                String cargoRepresentante = localizacao.getCargoRepresentante();
                if (cargoRepresentante != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_REPRESENTANTE_LOCALIZACAO.name(), cargoRepresentante);
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_REPRESENTANTE_LOCALIZACAO.name(), "");

                }
            }
            if (localizacao.getProcurador() != null) {
                PessoaFisica procurador = (PessoaFisica) pessoaFacade.recuperarPF(localizacao.getProcurador().getId());
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_PROCURADOR_LOCALIZACAO.name(), procurador.getNome());
                RG rg = procurador.getRg();
                if (rg != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.RG_PROCURADOR_LOCALIZACAO.name(), rg.getNumero() + ", " + rg.getOrgaoEmissao() + " " + rg.getUf());
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CPF_PROCURADOR_LOCALIZACAO.name(), procurador.getCpf());
                String cargoProcurador = localizacao.getCargoProcurador();
                if (cargoProcurador != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_PROCURADOR_LOCALIZACAO.name(), cargoProcurador);
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_PROCURADOR_LOCALIZACAO.name(), "");
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DECRETO_LOCALIZACAO.name(), localizacao.getDecreto());
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PORTARIA_LOCALIZACAO.name(), localizacao.getPortaria());
            } else {
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_PROCURADOR_LOCALIZACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_PROCURADOR_LOCALIZACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CPF_PROCURADOR_LOCALIZACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.RG_PROCURADOR_LOCALIZACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DECRETO_LOCALIZACAO.name(), "");
                addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PORTARIA_LOCALIZACAO.name(), "");
            }
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DESCRICAO_UNIDADE_ORGANIZACIONAL.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_SECRETARIA.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.NOME_PROCURADOR_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CARGO_PROCURADOR_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.CPF_PROCURADOR_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.RG_PROCURADOR_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.DECRETO_LOCALIZACAO.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoRendasPatrimoniais.PORTARIA_LOCALIZACAO.name(), "");
        }

    }

    private void tagsContratoCEASA(VelocityContext context, ContratoCEASA contrato) throws UFMException {
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_CONTRATO.name(), contrato.getNumeroContrato());
        BigDecimal areaTotalBoxes = BigDecimal.ZERO;
        BigDecimal valorContratoMetroQuadrado = BigDecimal.ZERO;
        StringBuilder numerosBoxes = new StringBuilder("");
        if (!contrato.getPontoComercialContratoCEASAs().isEmpty()) {
            for (PontoComercialContratoCEASA ponto : contrato.getPontoComercialContratoCEASAs()) {
                areaTotalBoxes = areaTotalBoxes.add(ponto.getPontoComercial().getArea());
                valorContratoMetroQuadrado = valorContratoMetroQuadrado.add(ponto.getValorMetroQuadrado());
                numerosBoxes.append(ponto.getPontoComercial().getNumeroBox() + ", ");
            }
            numerosBoxes.substring(0, numerosBoxes.length() - 3);
        }

        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.AREA_BOXES_CONTRATO.name(), nomeDoCampo(contrato, "areaTotalBoxes", null) + "m²");
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_BOXES_CONTRATO.name(), numerosBoxes);
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_CONTRATO_METRO_QUADRADO.name(), "R$ " + Util.formataNumero((((contrato.getSomaDoValorTotalContrato().divide(areaTotalBoxes, 10, RoundingMode.HALF_EVEN)).divide(new BigDecimal(contrato.getPeriodoVigencia()), 10, RoundingMode.HALF_EVEN)).multiply(moedaFacade.recuperaValorVigenteUFM()))));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_CONTRATO_MES.name(), "R$ " + Util.formataNumero(contrato.getSomaDoValorCalculadoMes().multiply(moedaFacade.recuperaValorVigenteUFM())));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_TOTAL_CONTRATO.name(), "R$ " + Util.formataNumero(contrato.getValorTotalContrato()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_LICITACAO_CONTRATO.name(), contrato.getValorLicitacaoFormatado());
        ParametroRendas parametro = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), contrato.getLotacaoVistoriadora());

        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VIGENCIA_CONTRATO.name(), parametro.getQuantidadeMesesVigencia());
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.DIA_DATA_CONTRATO.name(), new SimpleDateFormat("dd").format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.MES_DATA_CONTRATO.name(), new SimpleDateFormat("EEEE").format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.ANO_DATA_CONTRATO.name(), new SimpleDateFormat("yyyy").format(contrato.getDataInicio()));

        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_TOTAL_RATEIO.name(), "R$ " + Util.formataNumero(contrato.getValorTotalRateio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_RATEIO_MES.name(), "R$ " + Util.formataNumero(contrato.getValorMensalRateio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.DATA_CONTRATO.name(), formatterData.format(contrato.getDataInicio()));
        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.DATA_CONTRATO_EXTENSO.name(), "Rio Branco - AC, " + new SimpleDateFormat("EEEE, d' de 'MMMM' de 'yyyy", new Locale("pt", "BR")).format(contrato.getDataInicio()));

        addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.ATIVIDADE_CONTRATO.name(), contrato.getAtividadePonto().getDescricao());

        BigDecimal valorRateioMetroQuadrado;
        BigDecimal valorAreaMetroQuadrado = BigDecimal.ZERO;
        for (PontoComercialContratoCEASA pc : contrato.getPontoComercialContratoCEASAs()) {
            valorAreaMetroQuadrado = valorAreaMetroQuadrado.add(pc.getArea());
        }
        if (contrato.getValorMensalRateio() != null) {
            valorRateioMetroQuadrado = contrato.getValorMensalRateio().divide(valorAreaMetroQuadrado, BigDecimal.ROUND_HALF_EVEN);
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.VALOR_RATEIO_METRO_QUADRADO.name(), "R$ " + Util.formataNumero(valorRateioMetroQuadrado));
        }
        CadastroEconomico locatario = cadastroEconomicoFacade.recuperar(contrato.getLocatario().getId());

        if (locatario != null) {
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_CMC.name(), locatario.getNumeroCadastro());
            if (contrato.getLocatario().getPessoa() != null) {
                Pessoa pessoaLocatario = pessoaFacade.recuperar(locatario.getPessoa().getId());
                addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.RAZAO_CMC.name(), pessoaLocatario.getNome());
                addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CNPJ_CMC.name(), pessoaLocatario.getCpf_Cnpj());

                EnderecoCorreio endereco = null;
                for (EnderecoCorreio e : pessoaLocatario.getEnderecoscorreio()) {
                    if (e.getPrincipal()) {
                        endereco = e;
                        break;
                    } else if (endereco == null) {
                        endereco = e;
                    }
                }

                if (endereco != null) {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.LOGRADOURO_CMC.name(), endereco.getLogradouro());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_ENDERECO_CMC.name(), endereco.getNumero());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.COMPLEMENTO_CMC.name(), endereco.getComplemento());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.BAIRRO_CMC.name(), endereco.getBairro());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CEP_CMC.name(), endereco.getCep());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CIDADE_CMC.name(), endereco.getLocalidade());
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.UF_CMC.name(), endereco.getUf());
                } else {
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.LOGRADOURO_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_ENDERECO_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.COMPLEMENTO_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.BAIRRO_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CEP_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CIDADE_CMC.name(), "");
                    addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.UF_CMC.name(), "");
                }

                Telefone telefone = null;
                for (Telefone t : pessoaLocatario.getTelefones()) {
                    if (t.getPrincipal()) {
                        telefone = t;
                        break;
                    } else if (telefone == null) {
                        telefone = t;
                    }
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.TELEFONE_CMC.name(), nomeDoCampo(telefone, "telefone", null));

                String socios = "";
                for (SociedadeCadastroEconomico s : locatario.getSociedadeCadastroEconomico()) {
                    if (s.getSocio() instanceof PessoaFisica) {
                        PessoaFisica pessoaSocio = (PessoaFisica) pessoaFacade.recuperarPF(s.getSocio().getId());
                        EnderecoCorreio enderecoSocio = pessoaSocio.getEnderecoPrincipal();

                        socios += pessoaSocio.getNome() + ", CPF " + pessoaSocio.getCpf();

                        if (pessoaSocio.getRg() != null) {
                            socios += ", cédula de identidade " + pessoaSocio.getRg().getNumero()
                                + ", expedida pela " + pessoaSocio.getRg().getOrgaoEmissao();
                            if (pessoaSocio.getRg().getUf() != null) {
                                socios += "/" + pessoaSocio.getRg().getUf().getUf();
                            }
                        }

                        if (enderecoSocio != null) {
                            socios += ", residente à " + enderecoSocio.getLogradouro()
                                + ", nº " + enderecoSocio.getNumero() + ", Bairro " + enderecoSocio.getBairro()
                                + ", CEP " + enderecoSocio.getCep() + ", na cidade de " + enderecoSocio.getLocalidade()
                                + ", Estado do(e) " + enderecoSocio.getUf();
                        }


                        socios += ";";
                    }
                }
                if (!socios.equals("")) {
                    socios = socios.substring(0, socios.length() - 1);
                }
                addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.SOCIOS.name(), socios);

            }
        } else {
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.RAZAO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CNPJ_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.LOGRADOURO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.NUMERO_ENDERECO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.COMPLEMENTO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.BAIRRO_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CEP_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.CIDADE_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.UF_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.TELEFONE_CMC.name(), "");
            addTag(context, TipoModeloDoctoOficial.TagsContratoCEASA.SOCIOS.name(), "");
        }
    }

    private void tagsCobrancaAdministrativa(VelocityContext context, ItemNotificacao item) throws UFMException {
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(new Date()));
        ParametrosCobrancaAdministrativa parametro = parametroCobrancaAdministrativaFacade.parametrosCobrancaAdministrativaPorExercicio(exercicio);
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.NUMERO_ITEM_COBRANCA.name(), item.getNumero());
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.VENCIMENTO.name(), nomeDoCampo(item.getNotificacaoADM(), "vencimentoDam", TipoFormato.DATA));

        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.DIRETOR_DEPARTAMENTO.name(), parametro != null && parametro.getDiretorDepartamento() != null ? parametro.getDiretorDepartamento().getNome() : "");
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.CHEFE_DIVISAO.name(), parametro != null && parametro.getChefeDaDivisao() != null ? parametro.getChefeDaDivisao().getNome() : "");
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.CARGO.name(), parametro != null ? parametro.getCargo() : "");
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.PORTARIA.name(), parametro != null ? parametro.getPortaria() : "");
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.EXERCICIO.name(), parametro != null && parametro.getExercicio() != null ? parametro.getExercicio().getAno() : "");
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.DATA_ATUAL.name(), DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy"));
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.DATA_VENCIMENTO_NOTIFICACAO.name(), nomeDoCampo(item.getNotificacaoADM().getVencimentoNotificacao(), "vencimentoNotificacao", TipoFormato.DATA));

        String dividas = recuperarDescricaoDivida(item.getItemProgramacaoCobrancaLista());
        if (!dividas.isEmpty()) {
            dividas = dividas.substring(0, dividas.length() - 2);
        }
        addTag(context, TipoModeloDoctoOficial.TagsCobrancaAdministrativa.DIVIDAS.name(), !dividas.isEmpty() ? dividas : "");
    }

    private void tagsProcessoBloqueioOutorga(VelocityContext context, BloqueioOutorga bloqueioOutorga) throws UFMException {
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.ATO_LEGAL.name(), bloqueioOutorga.getAtoLegal());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.EMPRESA.name(), bloqueioOutorga.getCadastroEconomico().getInscricaoCadastral() + " - " + bloqueioOutorga.getCadastroEconomico().getPessoa().getNomeCpfCnpj());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.MOTIVO_OU_FUNDAMENTACAO.name(), bloqueioOutorga.getMotivo());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.PROCESSO_DATA_LANCAMENTO.name(), DataUtil.getDataFormatada(bloqueioOutorga.getLancamento()));
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.PROCESSO_EXERCICIO.name(), bloqueioOutorga.getExercicio());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.PROCESSO_NUMERO.name(), bloqueioOutorga.getCodigo());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.PROCESSO_PROTOCOLO.name(), bloqueioOutorga.getNumeroProtocolo());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.PROCESSO_SITUACAO.name(), bloqueioOutorga.getSituacao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.USUARIO_LOGADO.name(), bloqueioOutorga.getUsuarioIncluiu());
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.LISTA_PARAMETROS.name(), montaTabelaParametrosBloqueioOutorga(bloqueioOutorga));
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.LISTA_DADOS_BLOQUEIO.name(), montaTabelaDadosBloqueioOutorga(bloqueioOutorga));
        addTag(context, TipoModeloDoctoOficial.TagsBloqueioOutorga.LISTA_VALORES_BLOQUEADOS.name(), montaTabelaValoresBloqueioOutorga(bloqueioOutorga));
    }

    private String montaTabelaValoresBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><td align=\"left\"><b>Data</b></th>";
        tabela += "<th align=\"left\"><b>Favorecido</b></th>";
        tabela += "<th align=\"left\"><b>Montante Original</b></th>";
        tabela += "<th align=\"left\"><b>Tipo</b></th>";
        tabela += "<th align=\"left\"><b>Valor Bloqueado</b></th>";
        tabela += "<th align=\"left\"><b>Montante Bloqueado</b></th>";
        tabela += "</tr>";

        for (DadoBloqueioOutorga dado : bloqueioOutorga.getDadosBloqueioOutorgas()) {
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + DataUtil.getDataFormatada(dado.getDataBloqueio()) + "</td>";
            tabela += "<td align=\"left\">" + dado.getFavorecido().getNomeCpfCnpj() + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(dado.getMontanteOriginal()) + "</td>";
            tabela += "<td align=\"left\">" + dado.getTipoValor().getDescricao() + "</td>";
            if (dado.getTipoValor().equals(TipoCalculoRBTRans.TipoValor.PERCENTUAL)) {
                tabela += "<td align=\"left\">" + dado.getValor() + "%</td>";
            } else {
                tabela += "<td align=\"left\">" + dfReais.format(dado.getValor()) + "</td>";
            }
            tabela += "<td align=\"left\">" + dfReais.format(dado.getMontanteBloqueado()) + "</td>";
            tabela += "</tr>";
        }
        tabela += "</table>";

        return tabela;
    }

    public String montaTabelaCnaes(List<EconomicoCNAE> cnaes) {
        if (cnaes != null && !cnaes.isEmpty()) {
            String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
            tabela += "<tr><td align=\"left\"><b>CNAE</b></th>";
            tabela += "<th align=\"left\"><b>Descrição</b></th>";
            tabela += "<th align=\"left\"><b>Grau de Risco</b></th>";
            tabela += "</tr>";

            for (EconomicoCNAE cnae : cnaes) {
                tabela += "<tr>";
                tabela += "<td align=\"left\">" + cnae.getCnae().getCodigoCnae() + "</td>";
                tabela += "<td align=\"left\">" + cnae.getCnae().getDescricaoDetalhada() + "</td>";
                tabela += "<td align=\"left\">" + (cnae.getCnae().getNivelComplexibilidade() != null ? cnae.getCnae().getNivelComplexibilidade() : "") + "</td>";
                tabela += "</tr>";
            }
            tabela += "</table>";

            return tabela;
        }
        return "";
    }

    public String montaTabelaHorariosFuncionamento(List<PessoaHorarioFuncionamento> horarios) {
        if (horarios != null && !horarios.isEmpty()) {
            String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
            tabela += "<tr><td align=\"left\"><b>Código</b></th>";
            tabela += "<th align=\"left\"><b>Descrição</b></th>";
            tabela += "</tr>";

            for (PessoaHorarioFuncionamento horario : horarios) {
                tabela += "<tr>";
                tabela += "<td align=\"left\">" + horario.getHorarioFuncionamento().getCodigo() + "</td>";
                tabela += "<td align=\"left\">" + horario.getHorarioFuncionamento().getDescricao() + "</td>";
                tabela += "</tr>";
            }
            tabela += "</table>";

            return tabela;
        }
        return "";
    }

    private String montaTabelaDadosBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><td align=\"left\"><b>Data</b></th>";
        tabela += "<th align=\"left\"><b>Favorecido</b></th>";
        tabela += "<th align=\"left\"><b>Tipo</b></th>";
        tabela += "<th align=\"left\"><b>Valor</b></th>";
        tabela += "</tr>";


        for (DadoBloqueioOutorga dado : bloqueioOutorga.getDadosBloqueioOutorgas()) {
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + DataUtil.getDataFormatada(dado.getDataBloqueio()) + "</td>";
            tabela += "<td align=\"left\">" + dado.getFavorecido().getNomeCpfCnpj() + "</td>";
            tabela += "<td align=\"left\">" + dado.getTipoValor().getDescricao() + "</td>";
            if (dado.getTipoValor().equals(TipoCalculoRBTRans.TipoValor.PERCENTUAL)) {
                tabela += "<td align=\"left\">" + dado.getValor() + "%</td>";
            } else {
                tabela += "<td align=\"left\">" + dfReais.format(dado.getValor()) + "</td>";
            }
            tabela += "</tr>";
        }
        tabela += "</table>";

        return tabela;
    }

    private String montaTabelaParametrosBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        String tabela = "<table border=\"0\" width=\"100%\" style=\"font-size:x-small\">";
        tabela += "<tr><td align=\"left\"><b>Exercício</b></th>";
        tabela += "<th align=\"left\"><b>Mês</b></th>";
        tabela += "<th align=\"left\"><b>Tipo</b></th>";
        tabela += "<th align=\"left\"><b>Nº de Passageiros</b></th>";
        tabela += "<th align=\"left\"><b>Valor da Passagem</b></th>";
        tabela += "<th align=\"left\"><b>Total Outorga</b></th>";
        tabela += "</tr>";

        BigDecimal total = BigDecimal.ZERO;
        for (ParametroBloqueioOutorga parametro : bloqueioOutorga.getParametros()) {
            tabela += "<tr>";
            tabela += "<td align=\"left\">" + parametro.getExercicio() + "</td>";
            tabela += "<td align=\"left\">" + parametro.getMes() + "</td>";
            tabela += "<td align=\"left\">" + parametro.getTipoPassageiro().getDescricao() + "</td>";
            tabela += "<td align=\"left\">" + parametro.getQtdPassageiros() + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parametro.getValorPassagem()) + "</td>";
            tabela += "<td align=\"left\">" + dfReais.format(parametro.getValorPassagem().multiply(parametro.getQtdPassageiros())) + "</td>";
            tabela += "</tr>";
            total = total.add(parametro.getValorPassagem().multiply(parametro.getQtdPassageiros()));
        }
        tabela += "<tr>";
        tabela += "<td align=\"left\"></td>";
        tabela += "<td align=\"left\"></td>";
        tabela += "<td align=\"left\"></td>";
        tabela += "<td align=\"left\"></td>";
        tabela += "<td align=\"left\">Total:</td>";
        tabela += "<td align=\"left\">" + dfReais.format(total) + "</td>";
        tabela += "</tr>";
        tabela += "</table>";

        return tabela;
    }

    private String recuperarDescricaoDivida(List<ItemProgramacaoCobranca> itens) {
        List<Long> idsParcelas = Lists.newArrayList();

        for (ItemProgramacaoCobranca iten : itens) {
            idsParcelas.add(iten.getParcelaValorDivida().getId());
        }

        if (idsParcelas.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct concat(D.DESCRICAO, ', ') from ITEMPROGRAMACAOCOBRANCA IPC ")
            .append(" inner join PARCELAVALORDIVIDA PVD ON IPC.PARCELAVALORDIVIDA_ID = PVD.ID ")
            .append(" inner join VALORDIVIDA V on PVD.VALORDIVIDA_ID = V.ID ")
            .append(" inner join DIVIDA D on V.DIVIDA_ID = D.ID ")
            .append(" where pvd.ID in (:ids) ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ids", idsParcelas);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            StringBuilder divida = new StringBuilder();
            for (Object o : q.getResultList()) {
                divida.append(o);
            }
            return divida.toString();
        }
        return "";
    }

    private String montaTabelaDeCDA(Peticao peticao) throws UFMException {
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
        String conteudo =
            " <table style=\"width: 100%; font-size: 10px\">"
                + "<th> Número </th>"
                + "<th> Vlr Original </th>"
                + "<th> Vlr Juros </th>"
                + "<th> Vlr Multa </th>"
                + "<th> Vlr Atualizado </th>"
                + "<th> Vlr Total </th>"
                + "</tr>";
        peticao = peticaoFacade.recuperar(peticao.getId());
        for (ItemPeticao item : peticao.getItensPeticao()) {
            CertidaoDividaAtiva certidao = item.getCertidaoDividaAtiva();
            certidao = em.find(CertidaoDividaAtiva.class, certidao.getId());
            List<ParcelaValorDivida> parcelasDoItem = certidaoDividaAtivaFacade.recuperaParcelasDaCertidao(certidao);
            BigDecimal valorOriginal = item.getValorOriginal();
            BigDecimal valorJuros = calculaValorJurosDasParcelas(parcelasDoItem, valorUFMVigente);
            BigDecimal valorMulta = calculaValorMultaDasParcelas(parcelasDoItem, valorUFMVigente);
            BigDecimal valorAtualizado = item.getAtualizado();
            BigDecimal valorTotal = valorJuros.add(valorMulta).add(valorAtualizado);
            conteudo += "<tr>"
                + "<td> " + certidao.getNumero() + "</td>"
                + "<td> " + dfReais.format(valorOriginal) + " </td>"
                + "<td> " + dfReais.format(valorJuros) + "</td>"
                + "<td> " + dfReais.format(valorMulta) + "</td>"
                + "<td> " + dfReais.format(valorAtualizado) + " </td>"
                + "<td> " + dfReais.format(valorTotal) + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td colspan=\"7\"> Valor Total : " + ValorPorExtenso.valorPorExtenso(valorTotal) + " </td>"
                + "</tr>";
        }
        return conteudo += "</table>";
    }

    private BigDecimal recuperaValorTotalPeticaoDividaAtiva(Peticao peticao) {
        BigDecimal valor = BigDecimal.ZERO;
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
        peticao = peticaoFacade.recuperar(peticao.getId());
        for (ItemPeticao item : peticao.getItensPeticao()) {
            CertidaoDividaAtiva certidao = item.getCertidaoDividaAtiva();
            certidao = em.find(CertidaoDividaAtiva.class, certidao.getId());
            List<ParcelaValorDivida> parcelasDoItem = certidaoDividaAtivaFacade.recuperaParcelasDaCertidao(certidao);
            BigDecimal valorJuros = calculaValorJurosDasParcelas(parcelasDoItem, valorUFMVigente);
            BigDecimal valorMulta = calculaValorMultaDasParcelas(parcelasDoItem, valorUFMVigente);
            BigDecimal valorAtualizado = item.getAtualizado().multiply(valorUFMVigente);
            BigDecimal valorTotal = valorJuros.add(valorMulta).add(valorAtualizado);
            valor = valor.add(valorTotal);
        }
        return valor;
    }

    public boolean verificaSeExisteCadastroImobiliario(Peticao peticao) {
        peticao = peticaoFacade.recuperar(peticao.getId());
        for (ItemPeticao item : peticao.getItensPeticao()) {
            CertidaoDividaAtiva certidao = item.getCertidaoDividaAtiva();
            if (certidao.getCadastro() instanceof CadastroImobiliario) {
                return true;
            }
        }
        return false;
    }

    private String montaTabelaDeCompromissario(Peticao peticao) {
        int quantidadeDeCompromissario = 0;
        if (verificaSeExisteCadastroImobiliario(peticao)) {
            peticao = peticaoFacade.recuperar(peticao.getId());
            String conteudo =
                "<table style=\"width: 100%; font-size: 10px\">"
                    + "<th> Nome </th>"
                    + "<th> CPF/CNPJ </th>"
                    + "<th> Endereço Proprietário </th>"
                    + "<th> Endereço Compromissário </th>"
                    + "<th> Número correspondência </th>"
                    + "<th> Bairro  </th>"
                    + "<th> Cidade </th>"
                    + "<th> Estado </th>"
                    + "<th> CEP </th>"
                    + "<th> Complemento </th>"
                    + "</tr>";
            for (ItemPeticao item : peticao.getItensPeticao()) {
                CertidaoDividaAtiva certidao = item.getCertidaoDividaAtiva();
                if (certidao.getCadastro() instanceof CadastroImobiliario) {
                    CadastroImobiliario cadastro = cadastroImobiliarioFacade.recuperar(certidao.getCadastro().getId());
                    for (Compromissario compromissarioBCI : cadastro.getCompromissarioVigente()) {
                        Pessoa compromissario = compromissarioBCI.getCompromissario();
                        if (compromissario != null) {
                            compromissario = pessoaFacade.recuperar(compromissario.getId());
                            EnderecoCorreio enderecoCompromissario = compromissario.getEnderecos().get(0);
                            List<Propriedade> proprietarios = cadastroImobiliarioFacade.recuperarProprietariosVigentes(cadastro);
                            Pessoa proprietario = null;
                            if (!proprietarios.isEmpty()) {
                                proprietario = proprietarios.get(0).getPessoa();
                                proprietario = pessoaFacade.recuperar(proprietario.getId());
                                EnderecoCorreio endereoProprietario = proprietario.getEnderecos().get(0);
                                conteudo += "<tr>"
                                    + "	<td align=\"center\">" + retornaValor(compromissario.getNome()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(compromissario.getCpf_Cnpj()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(endereoProprietario.getLogradouro()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getLogradouro()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getNumero()) + "</td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getBairro()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getLocalidade()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getUf()) + " </td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getCep()) + "</td>"
                                    + "	<td align=\"center\">" + retornaValor(enderecoCompromissario.getComplemento()) + " </td>"
                                    + "	</tr>";
                            }
                            quantidadeDeCompromissario++;
                        } else {
                            conteudo += "<tr>"
                                + "	<td colspan=\"10\" align=\"center\"> Não existe compromissário para o cadastro imobiliário </td>"
                                + "	</tr>";
                        }
                    }
                }
            }
            conteudo = conteudo + "</table>";
            if (quantidadeDeCompromissario == 0) {
                return "";
            }
            return conteudo;
        }
        return "";
    }

    public String retornaValor(String valor) {
        if (valor == null) {
            return " - ";
        }
        return valor;
    }


    private BigDecimal calculaValorJurosDasParcelas(List<ParcelaValorDivida> parcelasDoItem, BigDecimal ufmVigente) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ParcelaValorDivida parcela : parcelasDoItem) {
            CalculadorAcrescimos.calculaAcrescimo(parcela, new Date(), consultaDebitoFacade.getValorAtualizadoParcela(parcela), dividaFacade.configuracaoVigente(parcela.getValorDivida().getDivida()));
            valor = valor.add(parcela.getValorJuros());
        }
        return valor;
    }

    private BigDecimal calculaValorMultaDasParcelas(List<ParcelaValorDivida> parcelasDoItem, BigDecimal ufmVigente) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ParcelaValorDivida parcela : parcelasDoItem) {
            CalculadorAcrescimos.calculaAcrescimo(parcela, new Date(), consultaDebitoFacade.getValorAtualizadoParcela(parcela), dividaFacade.configuracaoVigente(parcela.getValorDivida().getDivida()));
            valor = valor.add(parcela.getValorMulta());
        }
        return valor;
    }

    public BigDecimal recuperaValorTotalDaCertidao(List<ParcelaValorDivida> listaParcelasDoItem) throws UFMException {
        BigDecimal valor = calculaValorTotalDaCertidao(listaParcelasDoItem);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return valor;
    }

    private BigDecimal calculaValorTotalDaCertidao(List<ParcelaValorDivida> listaParcelasDoItem) throws UFMException {
        BigDecimal totalTotal = BigDecimal.ZERO;

        for (ParcelaValorDivida parcela : listaParcelasDoItem) {
            parcela = calculaAcrescimoParcela(parcela);
            BigDecimal saldo = parcela.getValorSaldo();
            BigDecimal multa = parcela.getValorMulta();
            BigDecimal juros = parcela.getValorJuros();
            BigDecimal total = saldo.add(multa).add(juros);

            totalTotal = totalTotal.add(total);
        }
        return totalTotal;
    }

    private BigDecimal calculaValorTotalDaCertidaoPelaView(List<ResultadoParcela> listaParcelasDoItem) {
        BigDecimal totalTotal = BigDecimal.ZERO;

        for (ResultadoParcela parcela : listaParcelasDoItem) {
            totalTotal = totalTotal.add(parcela.getValorTotal());
        }

        return totalTotal;
    }

    private String montaTabelaDeValoresDoTermoInscricao(LinhaDoLivroDividaAtiva linhaDoLivroDividaAtiva) throws UFMException {
        BigDecimal valorSaldo = BigDecimal.ZERO;
        BigDecimal valorJuros = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        BigDecimal valorTotal = BigDecimal.ZERO;

        List<ParcelaValorDivida> parcelasDoItem = inscricaoDividaAtivaFacade.recuperarParcelasItemInscricaoDividaAtiva(linhaDoLivroDividaAtiva.getItemInscricaoDividaAtiva());
        for (ParcelaValorDivida parcela : parcelasDoItem) {
            parcela = calculaAcrescimoParcela(parcela);

            valorSaldo = valorSaldo.add(parcela.getValorSaldo());
            valorJuros = valorJuros.add(parcela.getValorJuros());
            valorMulta = valorMulta.add(parcela.getValorMulta());
            valorTotal = valorTotal.add(parcela.getValorTotal());
        }

        String html = "<table border=\"1\" width=\"100%\" cellspacing=\"0\" style=\"font-size:x-small\">"
            + "			<tr>"
            + "				<td> &nbsp; DISCRIMINAÇÃO DE VALORES INSCRITOS </td>"
            + "				<td align=\"center\"> EM UFM </td>"
            + "				<td align=\"center\"> EM REAIS (R$) </td>"
            + "			</tr>"
            + "			<tr>"
            + "				<td> &nbsp; &bull; VALOR PARCELA </td>"
            + "				<td align=\"center\"> " + dfUFM.format(moedaFacade.converterToUFMVigente(valorSaldo)) + "</td>"
            + "				<td align=\"center\"> " + dfReais.format(valorSaldo) + "</td>"
            + "			</tr>"
            + "			<tr>"
            + "				<td> &nbsp; &bull; JUROS </td>"
            + "				<td align=\"center\"> " + dfUFM.format(moedaFacade.converterToUFMVigente(valorJuros)) + " </td>"
            + "				<td align=\"center\"> " + dfReais.format(valorJuros) + "</td>"
            + "			</tr>"
            + "			<tr>"
            + "				<td> &nbsp; &bull; MULTA </td>"
            + "				<td align=\"center\"> " + dfUFM.format(moedaFacade.converterToUFMVigente(valorMulta)) + "</td>"
            + "				<td align=\"center\"> " + dfReais.format(valorMulta) + " </td>"
            + "			</tr>"
            + "			<tr>"
            + "				<td>  &nbsp; &bull;TOTAL </td>"
            + "				<td align=\"center\"> " + dfUFM.format(moedaFacade.converterToUFMVigente(valorTotal)) + "</td>"
            + "				<td align=\"center\"> " + dfReais.format(valorTotal) + "</td>"
            + "			</tr>"
            + "			<tr>"
            + "				<td colspan=\"3\"> &nbsp; Valor total por extenso: " + ValorPorExtenso.valorPorExtenso(valorTotal) + "</td>"
            + "			</tr>"
            + "		</table>";
        return html;
    }

    public DocumentoOficial geraDocumentoContratoRendasPatrimoniais(ContratoRendasPatrimoniais contrato, DocumentoOficial documento, TipoDoctoOficial tipo, Pessoa pessoa, SistemaControlador sistemaControlador) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoContratoRendasPatrimoniais(contrato, mod, pessoa, sistemaControlador);
            }
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoContratoRendasPatrimoniais(ContratoRendasPatrimoniais contrato, ModeloDoctoOficial modelo, Pessoa pessoa, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, null, pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), contrato, null));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraDocumentoContratoCEASA(ContratoCEASA contrato, DocumentoOficial documento, TipoDoctoOficial tipo, Pessoa pessoa, SistemaControlador sistemaControlador) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumentoContratoCEASA(contrato, mod, pessoa, sistemaControlador);
            }
            emiteDocumentoOficial(documentoOficial);
            contrato.setDocumentoOficial(documentoOficial);
            salvaDocumentoOficinalNoContrato(contrato);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public void salvaDocumentoOficinalNoContrato(ContratoCEASA contratoCEASA) {
        em.merge(contratoCEASA);
    }

    public DocumentoOficial geraDocumentoCobrancaAdministrativa(AssistenteNotificacaoCobranca assistente, ItemNotificacao item, DocumentoOficial documento, TipoDoctoOficial tipo) throws Exception {
        ModeloDoctoOficial modeloDocumento = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (modeloDocumento != null) {
            if (documento != null && documento.getId() != null) {
                documentoOficial = recuperaDocumento(documento);
                if (documentoOficial.getConteudo() == null) {
                    if (documentoOficial.getValidade() == null) {
                        documentoOficial.setValidade(validadeDocumento(item.getNotificacaoADM().getProgramacaoCobranca().getDataProgramacaoCobranca(), tipo));
                    }
                    if (documentoOficial.getUsuarioSistema() == null) {
                        documentoOficial.setUsuarioSistema(item.getNotificacaoADM().getProgramacaoCobranca().getUsuarioSistema());
                    }
                    documentoOficial.setCodigoVerificacao(geraCodigoVerificacaoDoctoOficial(documentoOficial));
                    documentoOficial.setConteudo(mesclaTagsDocto(modeloDocumento.getConteudo(), documentoOficial, modeloDocumento.getTipoDoctoOficial().getModuloTipoDoctoOficial(), item, item));
                }
            } else {
                documentoOficial = geraNovoDocumentoCobrancaAdministrativa(item, modeloDocumento, assistente);
            }
            if (assistente.getEmitirNotificacao()) {
                emiteDocumentoOficial(documentoOficial);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoContratoCEASA(ContratoCEASA contratoCEASA, ModeloDoctoOficial modelo, Pessoa pessoa, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, contratoCEASA.getLocatario(), pessoa);
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), contratoCEASA, contratoCEASA));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public DocumentoOficial geraNovoDocumentoCobrancaAdministrativa(ItemNotificacao item, ModeloDoctoOficial modelo, AssistenteNotificacaoCobranca assistente) throws UFMException, AtributosNulosException {
        DocumentoOficial documentoOficial = novoDocumento(modelo, item.getCadastro(), item.getContribuinte(), assistente.getUsuarioSistema(), assistente.getExercicio(), assistente.getIp());
        documentoOficial.setConteudo(mesclaTagsDocto(modelo.getConteudo(), documentoOficial, modelo.getTipoDoctoOficial().getModuloTipoDoctoOficial(), item, item));
        this.documentoOficialFacade.salvarNovo(documentoOficial);
        return documentoOficial;
    }

    public Object recuperaValorCampo(Object obj, String atributo) throws IllegalAccessException, NoSuchFieldException {
        String[] composto;
        if (atributo.contains(".")) {
            composto = atributo.split("\\.");
        } else {
            composto = new String[]{atributo};
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals(composto[0])) {
                obj = field.get(obj) == null ? "" : field.get(obj);
                if (composto.length > 1) {
                    obj = recuperaValorCampo(obj, pegaNovoArray(composto));
                }
            }
        }
        return obj;
    }

    private String pegaNovoArray(String[] array) {
        String primeiro = array[0];
        String retorno = "";
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != primeiro) {
                retorno += array[i];
                ++j;
            }
        }
        return retorno;
    }

    public String nomeDoCampo(Object obj, String atributo, TipoFormato formato) {
        String retorno = "";
        if (obj == null) return retorno;
        try {
            obj = initializeAndUnproxy(obj);
            if (formato != null) {
                Object obj2 = recuperaValorCampo(obj, atributo);
                if (obj2 != null) {
                    if (TipoFormato.DATA.equals(formato)) {
                        retorno = Util.formatterDiaMesAno.format(recuperaValorCampo(obj, atributo));
                    }
                    if (TipoFormato.DATA_HORA_SEGUNDOS.equals(formato)) {
                        retorno = Util.formatterDataHora.format(recuperaValorCampo(obj, atributo));
                    }
                    if (TipoFormato.VALOR_REAIS.equals(formato)) {
                        retorno = Util.formataValor((BigDecimal) recuperaValorCampo(obj, atributo));
                    }
                    if (TipoFormato.VALOR_UFM.equals(formato)) {
                        retorno = Util.formataValor((BigDecimal) recuperaValorCampo(obj, atributo));
                    }
                    if (TipoFormato.DECIMAL.equals(formato)) {
                        retorno = Util.formataNumero(recuperaValorCampo(obj, atributo));
                    }
                    if (TipoFormato.HORA_MINUTO.equals(formato)) {
                        retorno = Util.formatterHoraMinuto.format(recuperaValorCampo(obj, atributo));
                    }
                }
                return retorno;
            } else {
                return recuperaValorCampo(obj, atributo).toString();
            }
        } catch (Exception e) {
            return "";
        }
    }

    public void alteraSituacaoDocumentoOficial(DocumentoOficial documentoOficial, DocumentoOficial.SituacaoDocumentoOficial situacaoDocumento) {
        if (!situacaoDocumento.equals(documentoOficial.getSituacaoDocumento())) {
            documentoOficial.setSituacaoDocumento(situacaoDocumento);
            em.merge(documentoOficial);
        }
    }

    public boolean validouAutenticacao(AutenticaCertidao autenticaCertidao) {
        return buscarSocilicitacaoComAutenticidade(autenticaCertidao) != null;
    }

    public WSSolicitacaoDocumentoOficial buscarSocilicitacaoComAutenticidade(AutenticaCertidao autenticaCertidao) {
        StringBuilder sql = new StringBuilder();

        if (TipoCertidao.BAIXA_ATIVIDADE.equals(autenticaCertidao.getTipoCertidao())) {
            sql.append("select doc.NUMERO, trunc(cer.DATACADASTRO) as dataemissao, ")
                .append("      'CERTIDÃO DE BAIXA DE ATIVIDADE' as tipoDocumento, 'Cadastro Econômico' as tipoCadastro, ")
                .append("      ce.INSCRICAOCADASTRAL ")
                .append(" from DOCUMENTOOFICIAL doc ")
                .append("inner join CadastroEconomico ce on ce.id = doc.CADASTRO_ID ")
                .append("inner join CERTIDAOATIVIDADEBCE cer on cer.DOCUMENTOOFICIAL_ID = doc.id ")
                .append("where doc.CODIGOVERIFICACAO like :codigo ")
                .append("  and trunc(cer.DATACADASTRO) = :emissao ");
            Query q = em.createNativeQuery(sql.toString());
            q.setParameter("codigo", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaCertidao.getCodigoVerificacao()).trim());
            q.setParameter("emissao", DataUtil.getDataFormatada(autenticaCertidao.getDataEmissao()));
            if (!q.getResultList().isEmpty()) {
                return new WSSolicitacaoDocumentoOficial((Object[]) q.getResultList().get(0));
            }
        } else {
            sql.append(" select SOL.* from SOLICITACAODOCTOOFICIAL SOL ")
                .append(" inner join DOCUMENTOOFICIAL DOC on SOL.DOCUMENTOOFICIAL_ID = DOC.ID  ")
                .append(" inner join MODELODOCTOOFICIAL MODELO on DOC.MODELODOCTOOFICIAL_ID = MODELO.ID ")
                .append(" inner join TIPODOCTOOFICIAL TIPO on MODELO.TIPODOCTOOFICIAL_ID = TIPO.ID ");

            sql.append(criarComplementoJoin(autenticaCertidao));

            sql.append(" where trunc(DOC.DATAEMISSAO) = :emissao ");
            if (autenticaCertidao.getDataVencimento() != null) {
                sql.append(" and trunc(DOC.VALIDADE) = :validadeDoc ");
            }
            if (autenticaCertidao.getNumeroDocumento() != null) {
                sql.append(" and DOC.CODIGOVERIFICACAO = :codigo and DOC.NUMERO = :numero ");
            }
            sql.append(" and TIPO.TIPOVALIDACAODOCTOOFICIAL = :tipoValidacao ")
                .append(" and TIPO.TIPOCADASTRODOCTOOFICIAL = :tipoCadastro ")
                .append(" and SOL.SITUACAOSOLICITACAO <> :situacaoSolicitacao ");

            if (TipoCertidao.CADASTRO_IMOBILIARIO.equals(autenticaCertidao.getTipoCertidao())) {
                sql.append(" and trunc(PROP.FINALVIGENCIA) is null ")
                    .append(" and coalesce (replace(replace(PF.CPF,'.',''),'-',''), replace(replace(replace(PJ.CNPJ,'.',''),'-',''),'/','')) = :cpfCnpj ");
            }

            Query q = em.createNativeQuery(sql.toString(), SolicitacaoDoctoOficial.class);
            q.setParameter("emissao", DataUtil.getDataFormatada(autenticaCertidao.getDataEmissao()));
            if (autenticaCertidao.getDataVencimento() != null) {
                q.setParameter("validadeDoc", DataUtil.getDataFormatada(autenticaCertidao.getDataVencimento()));
            }
            q.setParameter("codigo", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaCertidao.getCodigoVerificacao()).trim());
            if (autenticaCertidao.getNumeroDocumento() != null) {
                q.setParameter("numero", autenticaCertidao.getNumeroDocumento());
            }
            q.setParameter("tipoValidacao", autenticaCertidao.getTipoCertidao().getTipoValidacao().name().trim());
            q.setParameter("tipoCadastro", autenticaCertidao.getTipoCadastro().name().trim());
            q.setParameter("situacaoSolicitacao", SituacaoSolicitacao.CANCELADO);
            if (TipoCertidao.CADASTRO_IMOBILIARIO.equals(autenticaCertidao.getTipoCertidao())) {
                q.setParameter("cpfCnpj", StringUtil.retornaApenasNumeros(autenticaCertidao.getCpfCnpj()));
            }
            if (!q.getResultList().isEmpty()) {
                return new WSSolicitacaoDocumentoOficial((SolicitacaoDoctoOficial) q.getResultList().get(0));
            }
        }
        return null;
    }

    private StringBuilder criarComplementoJoin(AutenticaCertidao autenticaCertidao) {
        StringBuilder sql = new StringBuilder();

        if (TipoCertidao.CADASTRO_IMOBILIARIO.equals(autenticaCertidao.getTipoCertidao())) {
            sql.append(" inner join CADASTROIMOBILIARIO CAD on SOL.CADASTROIMOBILIARIO_ID = CAD.ID ")
                .append(" inner join PROPRIEDADE PROP on CAD.ID = PROP.IMOVEL_ID ")
                .append(" inner join PESSOA PES on PROP.PESSOA_ID = PES.ID ")
                .append(" left join PESSOAFISICA PF on PF.ID = PES.ID ")
                .append(" left join PESSOAJURIDICA PJ on PJ.ID = PES.ID ");

            return sql;
        }
        return sql;
    }

    public void enviarEmailDocumentoOficial(String[] emailsSeparados, DocumentoOficial documentoOficial, String assunto, String mensagemEmail) throws ValidacaoException, MessagingException {
        if (documentoOficial != null) {
            String conteudoCompleto = geraConteudoDocumento(documentoOficial);
            HtmlPdfDTO htmlPdfDTO = new HtmlPdfDTO();
            htmlPdfDTO.setConteudo(conteudoCompleto);
            htmlPdfDTO.setNomeArquivo(documentoOficial.getCodigoVerificacao());
            byte[] pdf = ReportService.getInstance().converterHtmlParaPdf(htmlPdfDTO);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(pdf.length);
            baos.write(pdf, 0, pdf.length);
            ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
            AnexoEmailDTO anexoDto = new AnexoEmailDTO(baos, "anexo", "application/pdf", ".pdf");
            EmailService.getInstance().enviarEmail(emailsSeparados, assunto, mensagemEmail, anexoDto);
        }
    }

    public DocumentoOficial gerarDocumentoOTT(CertificadoAnualOTT certificado, TipoDoctoOficial tipo, DocumentoOficial documento, UsuarioSistema usuarioSistema) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certificado, mod, null, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(usuarioSistema);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoOTT(CertificadoRenovacaoOTT certificado, TipoDoctoOficial tipo, DocumentoOficial documento, UsuarioSistema usuarioSistema) throws Exception {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certificado, mod, null, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(usuarioSistema);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
            throw new Exception("O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public DocumentoOficial gerarDocumentoOTT(CertificadoCondutorOTT certificado, TipoDoctoOficial tipo, DocumentoOficial documento, UsuarioSistema usuarioSistema) throws UFMException, AtributosNulosException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(certificado, mod, null, null);
            }
            if (documentoOficial.getUsuarioSistema() == null) {
                documentoOficial.setUsuarioSistema(usuarioSistema);
            }
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public WSSolicitacaoDocumentoOficial buscarDocumentoPorId(Long id) {
        String sql = " select doc.* from solicitacaodoctooficial doc " +
            " where doc.id = :id ";
        Query q = em.createNativeQuery(sql, SolicitacaoDoctoOficial.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            SolicitacaoDoctoOficial sol = (SolicitacaoDoctoOficial) q.getResultList().get(0);
            return new WSSolicitacaoDocumentoOficial(sol);
        }
        return null;
    }

    public WsAutenticaCertidao buscarCertidaoPorIdDaSolicitacao(Long id) {
        String sql = " select sol.* from solicitacaodoctooficial sol " +
            " inner join documentooficial doc on doc.id = sol.documentooficial_id " +
            " where sol.id = :id ";
        Query q = em.createNativeQuery(sql, SolicitacaoDoctoOficial.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            SolicitacaoDoctoOficial sol = (SolicitacaoDoctoOficial) q.getResultList().get(0);
            WsAutenticaCertidao certidao = new WsAutenticaCertidao();
            certidao.setCodigoVerificacao(sol.getDocumentoOficial().getCodigoVerificacao());
            certidao.setDataEmissao(sol.getDocumentoOficial().getDataEmissao());
            certidao.setDataVencimento(sol.getDocumentoOficial().getValidade());
            certidao.setNumeroDocumento(sol.getDocumentoOficial().getNumero());
            if (sol.getTipoCadastroDoctoOficialEnumValue() != null) {
                certidao.setTipoCadastro(sol.getTipoCadastroDoctoOficialEnumValue());
            }
            return certidao;
        }
        return null;
    }

    public DocumentoOficial buscarDocumentoPorChave(String chave) {
        Query q = em.createNativeQuery("select * from documentooficial where codigoverificacao = :chave", DocumentoOficial.class);
        q.setParameter("chave", chave);
        List<DocumentoOficial> docs = q.getResultList();
        if (!docs.isEmpty()) {
            return docs.get(0);
        } else {
            return null;
        }
    }

    public DocumentoOficial buscarDocumentoOficialExecucaoOrcamentariaPorModulo(Long id, ModuloTipoDoctoOficial modulo) {
        if (id == null || modulo == null) {
            return null;
        }
        String sql = "select doc.* from execorcdocumentooficial exdoc " +
            " inner join documentooficial doc on exdoc.documentooficial_id = doc.id " +
            ExecucaoOrcDocumentoOficial.getSql(modulo) +
            " order by doc.id desc ";
        Query q = em.createNativeQuery(sql, DocumentoOficial.class);
        q.setMaxResults(1);
        q.setParameter("obj", id);
        List<DocumentoOficial> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public List<ExecucaoOrcDocumentoOficial> buscarExecucoesOrcsDocumentosOficiais(Long id, ModuloTipoDoctoOficial modulo) {
        if (id == null || modulo == null) {
            return Lists.newArrayList();
        }
        String sql = "select exdoc.* from execorcdocumentooficial exdoc " + ExecucaoOrcDocumentoOficial.getSql(modulo);
        Query q = em.createNativeQuery(sql, ExecucaoOrcDocumentoOficial.class);
        q.setParameter("obj", id);
        return q.getResultList();
    }

    public boolean isPermitirImpressao(DocumentoOficial documentoOficial) {
        if (documentoOficial.getId() == null) {
            return true;
        }
        TipoDoctoOficial tipoDoctoOficial = documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial();
        if (tipoDoctoOficial.getExigirAssinatura()) {
            if (tipoDoctoOficial.getPermitirImpressaoSemAssinatura()) {
                return true;
            } else {
                List<AssinaturaDocumentoOficial> assinaturas = configuracaoAssinaturaFacade.buscarAssinaturaPorDoctoOficial(documentoOficial);
                if (assinaturas != null) {
                    boolean todasAssinadas = true;
                    for (AssinaturaDocumentoOficial assinatura : assinaturas) {
                        if (!assinatura.isAssinado()) {
                            todasAssinadas = false;
                        }
                    }
                    if (!todasAssinadas) {
                        FacesUtil.addWarn("Atenção!", "Documento não permitido ser impresso até que possua todas as assinaturas.");
                    }
                    return todasAssinadas;
                }
            }
        }
        return true;
    }

    private void gerarNotificacaoAssinatura(DocumentoOficial documentoOficial, Long idUndiadeOrganizacional) {
        List<ConfiguracaoAssinatura> configuracaoAssinaturas = configuracaoAssinaturaFacade.buscarConfiguracaoVigentePorModuloEUnidade(documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getModuloTipoDoctoOficial(), idUndiadeOrganizacional, documentoOficial.getDataEmissao());

        if (documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getExigirAssinatura()
            && !documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getPermitirImpressaoSemAssinatura() &&
            (configuracaoAssinaturas == null || configuracaoAssinaturas.isEmpty())) {
            throw new ValidacaoException("Não foi encontrado Configuração de Assinatura vigente para nenhum usuário," +
                " é necessario ter ao menos uma configuração vigente para a " + documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getModuloTipoDoctoOficial().getDescricao() +
                " e unidade orçamentária " + configuracaoAssinaturaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(documentoOficial.getDataEmissao(), idUndiadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA).toString());
        }

        List<Notificacao> notificacoes = Lists.newArrayList();
        for (ConfiguracaoAssinatura configuracaoAssinatura : configuracaoAssinaturas) {
            UsuarioSistema usuarioSistema = usuarioSistemaFacade.usuarioSistemaDaPessoa(configuracaoAssinatura.getPessoa());
            if (usuarioSistema != null) {
                AssinaturaDocumentoOficial assinatura = configuracaoAssinaturaFacade.buscarAssinaturaPorDoctoOficialUsuario(documentoOficial, usuarioSistema);
                if (assinatura == null) {
                    assinatura = new AssinaturaDocumentoOficial();
                    assinatura.setDocumentoOficial(documentoOficial);
                    assinatura.setSituacao(AssinaturaDocumentoOficial.SituacaoAssinatura.PENDENTE);
                    assinatura.setUsuarioSistema(usuarioSistema);
                    assinatura.setDataLimite(configuracaoAssinatura.getFimVigencia());
                    assinatura.setConfiguracaoAssinatura(configuracaoAssinatura);
                    assinatura = configuracaoAssinaturaFacade.salvarNotificacaoAssinatura(assinatura);
                }
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao(assinatura.getConfiguracaoAssinatura().getModulo().getDescricao() + " aguardando ser assinada.");
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo("Documento oficial para assinar.");
                notificacao.setTipoNotificacao(TipoNotificacao.DOCUMENTO_OFICIAL);
                notificacao.setUsuarioSistema(usuarioSistema);
                notificacao.setLink("/documento-oficial/assinar/" + assinatura.getDocumentoOficial().getId() + "/");
                notificacoes.add(notificacao);
            }
        }
        NotificacaoService.getService().notificar(notificacoes);
    }

    public void assinarDocumento(AssinaturaDocumentoOficial assinaturaDocumentoOficial) {
        assinaturaDocumentoOficial.setDataAssinatura(new Date());
        DocumentoOficial documentoOficial = assinaturaDocumentoOficial.getDocumentoOficial();
        String hash = documentoOficial.getId() + ""
            + assinaturaDocumentoOficial.getDataAssinatura() + ""
            + assinaturaDocumentoOficial.getUsuarioSistema();
        assinaturaDocumentoOficial.setHashAssinatura(Util.stringHexa(Util.gerarHash(hash, "MD5")));
        assinaturaDocumentoOficial.setSituacao(AssinaturaDocumentoOficial.SituacaoAssinatura.ASSINADO);
        configuracaoAssinaturaFacade.salvarNotificacaoAssinatura(assinaturaDocumentoOficial);
        atualizarConteudoAssinatura(documentoOficial, assinaturaDocumentoOficial);
    }

    private void atualizarConteudoAssinatura(DocumentoOficial documentoOficial, AssinaturaDocumentoOficial assinaturaDocumentoOficial) {
        String conteudo = documentoOficial.getConteudo();
        if (!Strings.isNullOrEmpty(documentoOficial.getConteudoAssinatura())) {
            conteudo = conteudo.replace(documentoOficial.getConteudoAssinatura(), "");
        }

        String imagemAssinatura = "";
        Arquivo arquivo = null;
        if (assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas() != null && !assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().isEmpty()) {
            if (assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao() != null &&
                !assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
                arquivo = assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivosComposicao().get(0).getArquivo();
            } else if (assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivoComposicao() != null) {
                arquivo = assinaturaDocumentoOficial.getUsuarioSistema().getAssinaturas().get(0).getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
            }

        }
        if (arquivo != null) {
            arquivo = arquivoFacade.recupera(arquivo.getId());
            imagemAssinatura = "<img src=\"data:image/png;base64," + Util.getBase64Encode(arquivo.getByteArrayDosDados()) + "\" alt=\"ASSINATURA_DIGITAL\" style=\"width: 80px !important; height: 80px !important;\" />";
        } else {
            String caminhoDaImagem = FacesUtil.geraUrlImagemDir().replace("faces/", "") + "img/assinado_digitalmente.png";
            imagemAssinatura = "<img src=\"" + caminhoDaImagem + "\" alt=\"ASSINATURA_DIGITAL\" style=\"width: 80px !important; height: 80px !important;\" />";
        }

        String nome = assinaturaDocumentoOficial.getUsuarioSistema().getPessoaFisica().getNome();
        nome = " <div> " + imagemAssinatura + "  <br/> " + nome + " </div>";

        conteudo = conteudo.replace(assinaturaDocumentoOficial.getUsuarioSistema().getPessoaFisica().getNome(), nome);

        if (documentoOficial.getId() != null && documentoOficial.getModeloDoctoOficial().getTipoDoctoOficial().getExigirAssinatura()) {
            List<AssinaturaDocumentoOficial> assinaturas = configuracaoAssinaturaFacade.buscarAssinaturaPorDoctoOficial(documentoOficial);
            String assinatura = "<br></br>" +
                " <div style=\"text-align: center!important;\"> Assinaturas </div> " +
                " <table \" width=\"100%\" style=\"font-size:12px\">"
                + "<tr>"
                + "<th align=\"center\"> Servidor </th>"
                + "<th align=\"center\"> Código de verificação </th>"
                + "<th align=\"center\"> Data de assinatura </th>"
                + "</tr>";

            for (AssinaturaDocumentoOficial ass : assinaturas) {
                if (ass.isAssinado()) {
                    assinatura += " <tr>";
                    assinatura += " <td align=\"center\">  " + ass.getUsuarioSistema().getPessoaFisica().getNome() + " </td>";
                    assinatura += " <td align=\"center\">  " + ass.getHashAssinatura() + " </td>";
                    assinatura += " <td align=\"center\">  " + Util.formatterDataHora.format(ass.getDataAssinatura()) + " </td>";
                    assinatura += "</tr>";
                }
            }
            assinatura += "</table>";
            conteudo = conteudo + assinatura;
            documentoOficial.setConteudo(conteudo);
            documentoOficial.setConteudoAssinatura(assinatura);
            em.merge(documentoOficial);
        }
    }

    public void tagsReciboReinf(VelocityContext context, EventoReinfDTO reciboReinfVO) {
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.TIPO.name(), reciboReinfVO.getTipoArquivo() != null ? reciboReinfVO.getTipoArquivo().getCodigo() : "");
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.EMPRESA.name(), reciboReinfVO.getCnpjEmpresa());
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.SITUACAO.name(), reciboReinfVO.getSituacao() != null ? reciboReinfVO.getSituacao().getDescricao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.CODIGO_RESPOSTA.name(), reciboReinfVO.getCodigoResposta());
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.DESCRICAO_RESPOSTA.name(), reciboReinfVO.getDescricaoResposta());
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.OPERACAO.name(), reciboReinfVO.getOperacao() != null ? reciboReinfVO.getOperacao().name() : "");
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.RECIBO.name(), reciboReinfVO.getReciboEntrega());
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.MES_ANO.name(), reciboReinfVO.getMesApur() != null ? reciboReinfVO.getMesApur() + "/" + reciboReinfVO.getAnoApur() : "");
        addTag(context, TipoModeloDoctoOficial.TagsReciboReinf.DATA_REGISTRO.name(), DataUtil.getDataFormatadaDiaHora(reciboReinfVO.getDataOperacao()));
    }

    public DocumentoOficial geraDocumentoLicencaETR(RequerimentoLicenciamentoETR requerimentoLicenciamentoETR, DocumentoOficial documento, CadastroEconomico cadastroEconomico, TipoDoctoOficial tipo, SistemaControlador sistemaControlador) throws AtributosNulosException, UFMException {
        ModeloDoctoOficial mod = tipoDoctoOficialFacade.recuperaModeloVigente(tipo);
        DocumentoOficial documentoOficial = new DocumentoOficial();
        if (mod != null) {
            if (documento != null) {
                documentoOficial = recuperaDocumento(documento);
            } else {
                documentoOficial = geraNovoDocumento(requerimentoLicenciamentoETR, mod, cadastroEconomico, null);
            }
            alteraSituacaoDocumentoOficial(documentoOficial, DocumentoOficial.SituacaoDocumentoOficial.ATIVO);
            emiteDocumentoOficial(documentoOficial);
        } else {
            FacesUtil.addFatal("Não é possível imprimir esse documento!", "O Tipo de Documento não possui o layout de documento cadastrado.");
        }
        return documentoOficial;
    }

    public void tagsSolicitacaoMaterial(VelocityContext context, SolicitacaoMaterial solicitacaoMaterial) {
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.OBJETO.name(), solicitacaoMaterial.getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.VALOR_SERVICO.name(), Util.formataValor(solicitacaoMaterial.getValor()));
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.VALOR_SERVICO_POR_EXTENSO.name(), ValorPorExtenso.valorPorExtenso(solicitacaoMaterial.getValor()));
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.MODALIDADE_LICITACAO.name(), solicitacaoMaterial.getModalidadeLicitacao() != null ? solicitacaoMaterial.getModalidadeLicitacao().getDescricao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.TIPO.name(), solicitacaoMaterial.getTipoAvaliacao() != null ? solicitacaoMaterial.getTipoAvaliacao().getDescricao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.PROGRAMA_DE_TRABALHO.name(), montarProgramaDeTrabalho(solicitacaoMaterial));
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.JUSTIFICATIVA.name(), solicitacaoMaterial.getJustificativa());
        addTagsItensEPrevisaoPCASolicitacaoMaterial(context, solicitacaoMaterial);
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.FORMA_PAGAMENTO.name(), solicitacaoMaterial.getFormaDePagamento());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.PRAZO_ENTREGA.name(), solicitacaoMaterial.getPrazoDeEntrega() + " " + solicitacaoMaterial.getTipoPrazoEntrega().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.PRAZO_EXECUCAO.name(), solicitacaoMaterial.getPrazoExecucao() + " " + solicitacaoMaterial.getTipoPrazoDeExecucao().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.PRAZO_VIGENCIA.name(), solicitacaoMaterial.getVigencia() + " " + solicitacaoMaterial.getTipoPrazoDeVigencia().getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.NUMERO_PROCESSO.name(), solicitacaoMaterial.getNumero() + "/" + solicitacaoMaterial.getExercicio().getAno());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.UNIDADE_ADMINISTRATIVA.name(), getHierarquiaDaUnidade(solicitacaoMaterial.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, solicitacaoMaterial.getDataSolicitacao()));
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.SOLICITANTE.name(), solicitacaoMaterial.getSolicitante().toString());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.LEI_LICITACAO.name(), (solicitacaoMaterial.getAmparoLegal() != null && solicitacaoMaterial.getAmparoLegal().getLeiLicitacao() != null) ? solicitacaoMaterial.getAmparoLegal().getLeiLicitacao().getDescricao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.DESCRICAO_NECESSIDADE.name(), solicitacaoMaterial.getDescricaoNecessidade());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.SOLUCAO_SUGERIDA.name(), solicitacaoMaterial.getDescricao());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.DESCRICAO_COTACAO.name(), solicitacaoMaterial.getCotacao() != null ? solicitacaoMaterial.getCotacao().getDescricao() : "");
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.INICIO_EXECUCAO.name(), DataUtil.getDataFormatada(solicitacaoMaterial.getInicioExecucao()));
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.DESCRICAO_COMPLEMENTAR.name(), solicitacaoMaterial.getDescricaoComplementarPrazo());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.CONTRATACOES_CORRELATAS.name(), solicitacaoMaterial.getContratacoesCorrelatas());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.CONTRATACOES_INTERDEPENDENTES.name(), solicitacaoMaterial.getContratacoesIndependentes());
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.GRAU_PRIORIDADE.name(), solicitacaoMaterial.getTipoGrauPrioridade() != null ? solicitacaoMaterial.getTipoGrauPrioridade().getDescricao() : "");
        UsuarioSistema aprovador = avaliacaoSolicitacaoDeCompraFacade.buscarUsuarioAprovouPorSolicitacaoMaterial(solicitacaoMaterial);
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.APROVADOR_SOLICITACAO.name(), aprovador != null ? aprovador.toString() : "");
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.LEI_14133_OU_LEGISLACAO_ANTERIOR.name(),
            (solicitacaoMaterial.getAmparoLegal() != null && solicitacaoMaterial.getAmparoLegal().getLeiLicitacao() != null) ?
                (solicitacaoMaterial.getAmparoLegal().getLeiLicitacao().isLei14133()
                    ? solicitacaoMaterial.getAmparoLegal().getLeiLicitacao().getDescricao()
                    : "legislação anterior(" + LeiLicitacao.LEI_8666.getDescricao() + ")")
                : "");
    }

    private String getHierarquiaDaUnidade(UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo, Date dataReferencia) {
        HierarquiaOrganizacional hierarquiaOrganizacional = pessoaFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalVigentePorUnidade(unidade, tipo, dataReferencia);
        return hierarquiaOrganizacional != null ? hierarquiaOrganizacional.toString() : "";
    }

    private void addTagsItensEPrevisaoPCASolicitacaoMaterial(VelocityContext context, SolicitacaoMaterial solicitacaoMaterial) {
        String retornoItens = "";
        String retornoPrevisaoPCA = "";
        HashSet<String> itensPca = new HashSet<>();
        if (solicitacaoMaterial.getLoteSolicitacaoMaterials() != null && !solicitacaoMaterial.getLoteSolicitacaoMaterials().isEmpty()) {
            retornoItens = "<table width=\"100%\" style=\"font-size:x-small; border: 1px solid; border-collapse: collapse\" border=\"1\">";
            for (LoteSolicitacaoMaterial lote : solicitacaoMaterial.getLoteSolicitacaoMaterials()) {
                retornoItens += "<tr>"
                    + "<th colspan=\"6\"  align=\"left\"> "
                    + lote.toString() + ", Valor: " + Util.formataValor(lote.getValor())
                    + (solicitacaoMaterial.getTipoAvaliacao().isMaiorDesconto() && solicitacaoMaterial.getCotacao().getFormularioCotacao().isTipoApuracaoPorLote() ? ", Desconto Mínimo: " + Util.formataPercentual(lote.getPercentualDescontoMinimo()) : "")
                    + " </th>"
                    + "</tr>"
                    + "<tr>"
                    + "<th align=\"center\"> NÚMERO </th>"
                    + "<th align=\"left\"> DESCRIÇÃO </th>"
                    + "<th align=\"center\"> UM </th>"
                    + "<th align=\"center\"> QTDE </th>"
                    + "<th align=\"center\"> PREÇO UNITÁRIO (R$) </th>"
                    + "<th align=\"center\"> PREÇO TOTAL (R$) </th>"
                    + (solicitacaoMaterial.getTipoAvaliacao().isMaiorDesconto() && solicitacaoMaterial.getCotacao().getFormularioCotacao().isTipoApuracaoPorItem() ? " <th align=\"center\"> DESCONTO MÍNIMO (%) </th> " : "")
                    + "</tr>";
                if (lote.getItensSolicitacao() != null && !lote.getItensSolicitacao().isEmpty()) {
                    for (ItemSolicitacao item : lote.getItensSolicitacao()) {
                        String mascaraQuantidade = item.getUnidadeMedida() != null && item.getUnidadeMedida().getMascaraQuantidade() != null ? item.getUnidadeMedida().getMascaraQuantidade().getMascara() : null;
                        String mascaraValorUnitario = item.getUnidadeMedida() != null && item.getUnidadeMedida().getMascaraValorUnitario() != null ? item.getUnidadeMedida().getMascaraValorUnitario().getMascara() : null;
                        retornoItens += "<tr>"
                            + "<td align=\"center\">" + item.getNumero() + "</td>"
                            + "<td align=\"left\">" + item.getDescricao() + (item.getDescricaoComplementar() != null ? ", " + item.getDescricaoComplementar() : "") + "</dh>"
                            + "<td align=\"center\">" + item.getUnidadeMedida().getSigla() + "</td>"
                            + "<td align=\"center\">" + Util.formatarValor(item.getQuantidade(), mascaraQuantidade, false) + "</td>"
                            + "<td align=\"center\">" + Util.formatarValor(item.getPreco(), mascaraValorUnitario, false) + "</td>"
                            + "<td align=\"center\">" + Util.formatarValor(item.getPrecoTotal(), null, false) + "</td>"
                            + (solicitacaoMaterial.getTipoAvaliacao().isMaiorDesconto() && solicitacaoMaterial.getCotacao().getFormularioCotacao().isTipoApuracaoPorItem() ? "<td align=\"center\">" + Util.formataPercentual(item.getPercentualDescontoMinimo()) + "</td>" : "")
                            + "</tr>";

                        itensPca.add(item.getPcaObjetoCompra() == null
                            ? "NÃO SE APLICA"
                            : "Item " + item.getPcaObjetoCompra().getNumero() + " do PCA " + item.getPcaObjetoCompra().getPlanoContratacaoAnualGrupo().getPlanoContratacaoAnual().getExercicio().getAno());
                    }
                }
            }
            retornoItens += "</table>";

            for (String item : itensPca) {
                retornoPrevisaoPCA += "<p>" + item + "</p>";
            }
        }
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.ITENS.name(), retornoItens);
        addTag(context, TipoModeloDoctoOficial.TagsSolicitacaoMaterial.PREVISAO_PCA.name(), retornoPrevisaoPCA);
    }

    private String montarProgramaDeTrabalho(SolicitacaoMaterial solicitacaoMaterial) {
        List<Object[]> programasDetrabalho = buscarProgramaDeTrabalho(solicitacaoMaterial);
        if (programasDetrabalho != null && !programasDetrabalho.isEmpty()) {
            String retorno = "<table width=\"100%\" style=\"font-size:x-small; border: 1px solid; border-collapse: collapse\" border=\"1\">"
                + "<tr>"
                + "<th align=\"center\"> PROGRAMA DE TRABALHO </th>"
                + "<th align=\"center\"> NATUREZA DA DESPESA </th>"
                + "<th align=\"center\"> FONTE </th>"
                + "<th align=\"center\"> VALOR DA DESPESA </th>"
                + "</tr>";
            BigDecimal total = BigDecimal.ZERO;

            for (Object[] obj : programasDetrabalho) {
                total = total.add((BigDecimal) obj[3]);
                retorno += "<tr>"
                    + "<td align=\"center\">" + obj[0].toString() + "</td>"
                    + "<td align=\"center\">" + obj[1].toString() + "</dh>"
                    + "<td align=\"center\">" + obj[2].toString() + "</td>"
                    + "<td align=\"center\">" + Util.formataValor((BigDecimal) obj[3]) + "</td>"
                    + "</tr>";
            }
            retorno += " <tr>"
                + " <th colspan=\"3\" align=\"right\">TOTAL</th>"
                + " <th align=\"center\">" + Util.formataValor(total) + "</th>"
                + " </tr>"
                + "</table>";
            return retorno;
        }
        return "";
    }

    public List<Object[]> buscarProgramaDeTrabalho(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = " select distinct " +
            "        f.codigo || '.' || sf.codigo || '.' || prog.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as programadetrabalho, " +
            "        cdesp.codigo as naturezadadespesa, " +
            "        fr.codigo || '(' || fr.descricao || ')' as fontederecurso, " +
            "        sum(dir.valor) as valor " +
            " from dotsolmat d " +
            "         inner join dotacaosolmatitem di on di.dotacaosolicitacaomaterial_id = d.id " +
            "         inner join dotacaosolmatitemfonte dir on dir.dotacaosolmatitem_id = di.id " +
            "         inner join fontedespesaorc fontdesp on fontdesp.id = dir.fontedespesaorc_id " +
            "         inner join provisaoppafonte ppf on fontdesp.provisaoppafonte_id = ppf.id " +
            "         inner join despesaorc desp on fontdesp.despesaorc_id = desp.id " +
            "         inner join provisaoppadespesa provdesp on ppf.provisaoppadespesa_id = provdesp.id " +
            "         inner join conta cdesp on provdesp.contadedespesa_id = cdesp.id " +
            "         inner join contadedestinacao cd on ppf.destinacaoderecursos_id = cd.id " +
            "         inner join fontederecursos fr on cd.fontederecursos_id = fr.id " +
            "         inner join subacaoppa sub on sub.id = provdesp.subacaoppa_id " +
            "         inner join acaoppa a on a.id = sub.acaoppa_id " +
            "         inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id " +
            "         inner join programappa prog on prog.id = a.programa_id " +
            "         inner join funcao f on f.id = a.funcao_id " +
            "         inner join subfuncao sf on sf.id = a.subfuncao_id " +
            " where d.solicitacaomaterial_id = :idSolicitacaoMaterial " +
            " group by f.codigo, sf.codigo, prog.codigo, tpa.codigo, a.codigo, sub.codigo, cdesp.codigo, fr.codigo, fr.descricao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacaoMaterial", solicitacaoMaterial.getId());
        return q.getResultList();
    }

    public byte[] converterDocumentoOficialParaPDF(DocumentoOficial documentoOficial, String nomeArquivo) {
        HtmlPdfDTO htmlPdfDTO = new HtmlPdfDTO();
        htmlPdfDTO.setConteudo(documentoOficial.getConteudo());
        htmlPdfDTO.setNomeArquivo(nomeArquivo);
        return ReportService.getInstance().converterHtmlParaPdf(htmlPdfDTO);
    }
}
