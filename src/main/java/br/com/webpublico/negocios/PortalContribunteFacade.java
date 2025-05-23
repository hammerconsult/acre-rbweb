/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.controle.ConsultaDebitoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TermoUso;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.PessoaFisicaPortal;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioBCE;
import br.com.webpublico.entidadesauxiliares.ImpressaoCarneIPTU;
import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.entidadesauxiliares.VOAlvaraItens;
import br.com.webpublico.entidadesauxiliares.comum.UsuarioPortalWebDTO;
import br.com.webpublico.entidadesauxiliares.rh.portal.ConfiguracaoRHDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;
import br.com.webpublico.exception.*;
import br.com.webpublico.negocios.comum.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.documentoportalservidor.DocumentoPortalContribuinteFacade;
import br.com.webpublico.negocios.rh.portal.PessoaFisicaPortalFacade;
import br.com.webpublico.negocios.tributario.auxiliares.FiltroConsultaDebitos;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.nfse.domain.dtos.ImagemUsuarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.nfse.facades.NfseAuthorityFacade;
import br.com.webpublico.ott.*;
import br.com.webpublico.ott.enums.TipoPermissaoRBTransDTO;
import br.com.webpublico.ott.enums.TipoRespostaOttDTO;
import br.com.webpublico.pessoa.dto.*;
import br.com.webpublico.pessoa.enumeration.TipoPessoaPortal;
import br.com.webpublico.singletons.CacheRH;
import br.com.webpublico.tributario.EmissaoNotaAvulsaDTO;
import br.com.webpublico.tributario.GeraAliquotaNotaAvulsaDTO;
import br.com.webpublico.tributario.NovoTomadorDTO;
import br.com.webpublico.tributario.ParcelaPixDTO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.SituacaoParcelaDTO;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.commons.codec.binary.Base64;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class PortalContribunteFacade extends AbstractFacade<Pessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SolicitacaoDoctoOficialFacade solicitacaoDoctoOficialFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private AlvaraFacade alvaraFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CalculoITBIFacade calculoITBIFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroImobiliarioImpressaoHistFacade cadastroImobiliarioImpressaoHistFacade;
    @EJB
    private CadastroEconomicoImpressaoHistFacade cadastroEconomicoImpressaoHistFacade;
    @EJB
    private NFSAvulsaFacade nfsAvulsaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ConsultaAutenticidadeNFSAvulsaFacade consultaAutenticidadeNFSAvulsaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ImpressaoCarneIPTUFacade impressaoCarneIPTUFacade;
    @EJB
    private NivelEscolaridadeFacade nivelEscolaridadeFacade;
    @EJB
    private ProfissaoFacade profissaoFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private ConselhoClasseOrdemFacade conselhoClasseOrdemFacade;
    @EJB
    private FaleConoscoFacade faleConoscoFacade;
    @EJB
    private DocumentoPortalContribuinteFacade documentoPortalContribuinteFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    @EJB
    private FormacaoFacade formacaoFacade;
    @EJB
    private AreaFormacaoFacade areaFormacaoFacade;
    @EJB
    private HabilidadeFacade habilidadeFacade;
    @EJB
    private CIDFacade cidFacade;
    @EJB
    private PessoaFisicaPortalFacade pessoaFisicaPortalFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;
    @EJB
    private VeiculoOperadoraTecnologiaTransporteFacade veiculoOperadoraTecnologiaTransporteFacade;
    @EJB
    private CondutorOperadoraTecnologiaTransporteFacade condutorOperadoraTecnologiaTransporteFacade;
    @EJB
    private MarcaFacade marcaFacade;
    @EJB
    private CorFacade corFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade ottFacade;
    @EJB
    private AlteracaoCadastralPessoaFacade alteracaoCadastralPessoaFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private NfseAuthorityFacade nfseAuthorityFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private ContraChequeFacade contraChequeFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private RelatorioCadastroEconomicoFacade relatorioCadastroEconomicoFacade;
    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    @EJB
    private TermoUsoFacade termoUsoFacade;
    @EJB
    private SolicitacaoCertidaoPortalFacade solicitacaoCertidaoPortalFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private SolicitacaoCadastroPessoaFacade solicitacaoCadastroPessoaFacade;
    @EJB
    private CacheRH cacheRH;
    @EJB
    private ParametroSaudFacade parametroSaudFacade;
    @EJB
    private UsuarioSaudFacade usuarioSaudFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoPortalContribuinteFacade configuracaoPortalContribuinteFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private GeraValorDividaSolicitacaoCadastroCredor geraValorDividaSolicitacaoCadastroCredor;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;

    public PortalContribunteFacade() {
        super(Pessoa.class);
    }

    public UsuarioWebFacade getUsuarioWebFacade() {
        return usuarioWebFacade;
    }

    public ConsultaAutenticidadeNFSAvulsaFacade getConsultaAutenticidadeNFSAvulsaFacade() {
        return consultaAutenticidadeNFSAvulsaFacade;
    }

    public ProcRegularizaConstrucaoFacade getProcRegularizaConstrucaoFacade() {
        return procRegularizaConstrucaoFacade;
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }

    public HabiteseConstrucaoFacade getHabiteseConstrucaoFacade() {
        return habiteseConstrucaoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public CalculoAlvaraFacade getCalculoAlvaraFacade() {
        return calculoAlvaraFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public CalculoITBIFacade getcalculoITBIFacade() {
        return calculoITBIFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public NivelEscolaridadeFacade getNivelEscolaridadeFacade() {
        return nivelEscolaridadeFacade;
    }

    public ProfissaoFacade getProfissaoFacade() {
        return profissaoFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public DocumentoPortalContribuinteFacade getDocumentoPortalContribuinteFacade() {
        return documentoPortalContribuinteFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public NacionalidadeFacade getNacionalidadeFacade() {
        return nacionalidadeFacade;
    }

    public ConselhoClasseOrdemFacade getConselhoClasseOrdemFacade() {
        return conselhoClasseOrdemFacade;
    }

    public FormacaoFacade getFormacaoFacade() {
        return formacaoFacade;
    }

    public AreaFormacaoFacade getAreaFormacaoFacade() {
        return areaFormacaoFacade;
    }

    public HabilidadeFacade getHabilidadeFacade() {
        return habilidadeFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public VeiculoOperadoraTecnologiaTransporteFacade getVeiculoOperadoraTecnologiaTransporteFacade() {
        return veiculoOperadoraTecnologiaTransporteFacade;
    }

    public CondutorOperadoraTecnologiaTransporteFacade getCondutorOperadoraTecnologiaTransporteFacade() {
        return condutorOperadoraTecnologiaTransporteFacade;
    }

    public OperadoraTecnologiaTransporteFacade getOttFacade() {
        return ottFacade;
    }

    public void setOttFacade(OperadoraTecnologiaTransporteFacade ottFacade) {
        this.ottFacade = ottFacade;
    }

    public AlteracaoCadastralPessoaFacade getAlteracaoCadastralPessoaFacade() {
        return alteracaoCadastralPessoaFacade;
    }

    public void setAlteracaoCadastralPessoaFacade(AlteracaoCadastralPessoaFacade alteracaoCadastralPessoaFacade) {
        this.alteracaoCadastralPessoaFacade = alteracaoCadastralPessoaFacade;
    }

    public AlvaraFacade getAlvaraFacade() {
        return alvaraFacade;
    }

    public CalculaISSFacade getCalculaISSFacade() {
        return calculaISSFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public SolicitacaoCertidaoPortalFacade getSolicitacaoCertidaoPortalFacade() {
        return solicitacaoCertidaoPortalFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfiguracaoPortalContribuinteFacade getConfiguracaoPortalContribuinteFacade() {
        return configuracaoPortalContribuinteFacade;
    }

    public SolicitacaoCadastroPessoaFacade getSolicitacaoCadastroPessoaFacade() {
        return solicitacaoCadastroPessoaFacade;
    }

    public OperadoraTecnologiaTransporteFacade getOperadoraTecnologiaTransporteFacade() {
        return operadoraTecnologiaTransporteFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OperadoraTecnologiaTransporteDTO salvarOperadoraTecnologiaTransporte(@RequestBody OperadoraTecnologiaTransporteDTO operadoraDTO) throws Exception {
        OperadoraTecnologiaTransporte operadora = new OperadoraTecnologiaTransporte();
        operadora.setNome(operadoraDTO.getNome().toUpperCase());
        operadora.setCnpj(Util.formatarCnpj(operadoraDTO.getCnpj()));
        operadora.setInscricaoEstadual(operadoraDTO.getInscricaoEstadual());
        operadora.setInscricaoMunicipal(operadoraDTO.getInscricaoMunicipal());
        operadora.setTelefoneComercial(operadoraDTO.getTelefoneComercial());
        operadora.setCelular(operadoraDTO.getCelular());
        operadora.setNomeResponsavel(operadoraDTO.getNomeResponsavel().toUpperCase());
        operadora.setCpfResponsavel(Util.formatarCpf(operadoraDTO.getCpfResponsavel()));
        operadora.setEmailResponsavel(operadoraDTO.getEmailResponsavel().toLowerCase());
        operadora.setCep(operadoraDTO.getCep());
        operadora.setCidade(operadoraDTO.getCidade());
        operadora.setUf(operadoraDTO.getUf());
        operadora.setEnderecoComercial(operadoraDTO.getEnderecoComercial());
        operadora.setNumeroEndereco(operadoraDTO.getNumeroEndereco());
        operadora.setBairro(operadoraDTO.getBairro());
        operadora.setComplemento(operadoraDTO.getComplemento());
        operadora.setSituacao(SituacaoOTT.CADASTRADO);
        operadoraTecnologiaTransporteFacade.criarHistoricoSituacaoOTT(operadora, sistemaFacade.getUsuarioCorrente());
        addAnexosOperadoraTecnologiaTransporte(operadora, operadoraDTO.getAnexos());
        operadora = em.merge(operadora);
        operadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacao(operadora);
        operadoraDTO.setId(operadora.getId());
        return operadoraDTO;
    }

    public OperadoraTecnologiaTransporteDTO salvarOperadoraOttPortal(@RequestBody OperadoraTecnologiaTransporteDTO operadoraDTO) {
        OperadoraTecnologiaTransporte operadora = ottFacade.recuperar(operadoraDTO.getId());
        operadora.setInscricaoEstadual(operadoraDTO.getInscricaoEstadual());
        operadora.setInscricaoMunicipal(operadoraDTO.getInscricaoMunicipal());
        operadora.setTelefoneComercial(operadoraDTO.getTelefoneComercial());
        operadora.setCelular(operadoraDTO.getCelular());
        operadora.setNomeResponsavel(operadoraDTO.getNomeResponsavel().toUpperCase());
        operadora.setCpfResponsavel(Util.formatarCpf(operadoraDTO.getCpfResponsavel()));
        operadora.setEmailResponsavel(operadoraDTO.getEmailResponsavel().toLowerCase());
        operadora.setCep(operadoraDTO.getCep());
        operadora.setCidade(operadoraDTO.getCidade());
        operadora.setUf(operadoraDTO.getUf());
        operadora.setEnderecoComercial(operadoraDTO.getEnderecoComercial());
        operadora.setNumeroEndereco(operadoraDTO.getNumeroEndereco());
        operadora.setBairro(operadoraDTO.getBairro());
        operadora.setComplemento(operadoraDTO.getComplemento());
        operadora.setSituacao(SituacaoOTT.APROVADO);
        operadora = em.merge(operadora);
        OperadoraTecnologiaTransporte operadoraWp = operadoraTecnologiaTransporteFacade.criarHistoricoSituacaoOTTRenovacao(operadora, sistemaFacade.getUsuarioCorrente());
        operadoraTecnologiaTransporteFacade.criarNotificacaoOperadoraPortal(operadoraWp);
        return operadoraDTO;
    }

    public VeiculoOttDTO salvarVeiculoOTT(VeiculoOttDTO veiculoOttDTO) throws Exception {
        if (veiculoOttDTO.getId() != null) {
            VeiculoOperadoraTecnologiaTransporte veiculo = getVeiculoOperadoraTecnologiaTransporte(veiculoOttDTO);
            veiculo.getHistoricoSituacoesVeiculo().addAll(veiculoOperadoraTecnologiaTransporteFacade.recuperarHistoricoCondutor(veiculoOttDTO.getId()));
            veiculo.setId(veiculoOttDTO.getId());
            veiculoOperadoraTecnologiaTransporteFacade.criarHistoricoSituacoesVeiculo(veiculo, sistemaFacade.getUsuarioCorrente());
            veiculo = em.merge(veiculo);
            veiculoOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacaoVeiculo(veiculo);
            veiculoOttDTO.setId(veiculo.getId());
            return veiculoOttDTO;
        }
        VeiculoOperadoraTecnologiaTransporte veiculo = getVeiculoOperadoraTecnologiaTransporte(veiculoOttDTO);
        veiculoOperadoraTecnologiaTransporteFacade.criarHistoricoSituacoesVeiculo(veiculo, sistemaFacade.getUsuarioCorrente());
        veiculo = em.merge(veiculo);
        veiculoOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacaoVeiculo(veiculo);
        veiculoOttDTO.setId(veiculo.getId());
        return veiculoOttDTO;
    }

    public VeiculoRestOttDTO salvarVeiculoRestOttDTO(VeiculoRestOttDTO veiculoRestOttDTO) {

        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(veiculoRestOttDTO.getCnpj());
        VeiculoOperadoraTecnologiaTransporte veiculo = new VeiculoOperadoraTecnologiaTransporte();
        if (operadoraTecnologiaTransporte != null) {
            veiculo.setOperadoraTransporte(operadoraTecnologiaTransporte);
            veiculo.setPlacaVeiculo(veiculoRestOttDTO.getPlacaVeiculo().toUpperCase().trim());
            veiculo.setModeloVeiculo(veiculoRestOttDTO.getModeloVeiculo().toUpperCase());

            veiculo.setMarca(buscarMarca(veiculoRestOttDTO.getMarca().toUpperCase()));
            veiculo.setAnoFabricacaoVeiculo(veiculoRestOttDTO.getAnoFabricacaoVeiculo());
            veiculo.setCor(buscarCor(veiculoRestOttDTO.getCor().toUpperCase()));
            veiculo.setCrlv(veiculoRestOttDTO.getCrlv());
            veiculo.setVeiculoAdaptado(veiculoRestOttDTO.getVeiculoAdaptado());
            veiculo.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.CADASTRADO);
            veiculo.setVeiculoPoluente(VeiculoPoluente.getEnumByDTO(veiculoRestOttDTO.getVeiculoPoluente()));
            if (!veiculoOperadoraTecnologiaTransporteFacade.verificarPlacaVeiculoPorOperadora(veiculo)) {
                veiculo = em.merge(veiculo);
                veiculoOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacaoVeiculo(veiculo);
                veiculoRestOttDTO.setId(veiculo.getId());

                return veiculoRestOttDTO;
            }
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.VEICULO_JA_EXISTENTE);
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.OTT_INEXISTENTE);
    }

    public CondutorRestOttDTO salvarCondutorRestOttDTO(CondutorRestOttDTO condutorRestOttDTO) {
        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(condutorRestOttDTO.getCnpj());
        CondutorOperadoraTecnologiaTransporte condutor = new CondutorOperadoraTecnologiaTransporte();
        if (operadoraTecnologiaTransporte != null) {
            condutor.setOperadoraTecTransporte(operadoraTecnologiaTransporte);
            condutor.setNomeCondutor(condutorRestOttDTO.getNomeCondutor().toUpperCase());
            condutor.setCpf(condutorRestOttDTO.getCpf());
            condutor.setEquipamentoCondutor(EquipamentoCondutor.getEnumByDTO(condutorRestOttDTO.getEquipamentoCondutor()));
            condutor.setGenero(Sexo.getEnumByDTO(condutorRestOttDTO.getGenero()));
            condutor.setRg(condutorRestOttDTO.getRg());
            condutor.setCnh(condutorRestOttDTO.getCnh());
            condutor.setCep(condutorRestOttDTO.getCep());
            condutor.setEnderecoLogradouro(condutorRestOttDTO.getEnderecoLogradouro());
            condutor.setNumeroEndereco(condutorRestOttDTO.getNumeroEndereco());
            condutor.setComplemento(condutorRestOttDTO.getComplemento());
            condutor.setBairro(condutorRestOttDTO.getBairro());
            condutor.setCidade(condutorRestOttDTO.getCidade());
            condutor.setUf(condutorRestOttDTO.getUf().toUpperCase());
            condutor.setTelefoneComercial(condutorRestOttDTO.getTelefoneComercial());
            condutor.setCelular(condutorRestOttDTO.getCelular());
            condutor.setDataCadastro(new Date());
            condutor.setSituacaoCondutorOTT(SituacaoCondutorOTT.CADASTRADO);

            if (!condutorOperadoraTecnologiaTransporteFacade.verificarCpfJaExiste(condutor)) {
                condutor = em.merge(condutor);
                condutorOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacao(condutor);
                condutorRestOttDTO.setId(condutor.getId());

                return condutorRestOttDTO;
            }
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CONDUTOR_JA_EXISTENTE);
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.OTT_INEXISTENTE);
    }

    public void validarCamposVeiculoRestOTT(VeiculoRestOttDTO veiculoRestOttDTO) {
        ValidacaoExceptionOtt vott = new ValidacaoExceptionOtt();
        if (Strings.isNullOrEmpty(veiculoRestOttDTO.getCnpj()) || Strings.isNullOrEmpty(veiculoRestOttDTO.getSenha())
            || Strings.isNullOrEmpty(veiculoRestOttDTO.getModeloVeiculo()) || Strings.isNullOrEmpty(veiculoRestOttDTO.getPlacaVeiculo())
            || Strings.isNullOrEmpty(veiculoRestOttDTO.getMarca())
            || veiculoRestOttDTO.getAnoFabricacaoVeiculo() == null || Strings.isNullOrEmpty(veiculoRestOttDTO.getCor())
            || Strings.isNullOrEmpty(veiculoRestOttDTO.getCrlv())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CAMPO_OBRIGATORIO_VEICULO);
        }
        vott.lancarExceptionOtt();
    }

    public void validarCamposCondutorRestOTT(CondutorRestOttDTO condutorRestOttDTO) {
        ValidacaoExceptionOtt vott = new ValidacaoExceptionOtt();
        if (Strings.isNullOrEmpty(condutorRestOttDTO.getCnpj()) || Strings.isNullOrEmpty(condutorRestOttDTO.getSenha())
            || Strings.isNullOrEmpty(condutorRestOttDTO.getNomeCondutor()) || Strings.isNullOrEmpty(condutorRestOttDTO.getCpf())
            || Strings.isNullOrEmpty(condutorRestOttDTO.getCnh())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CAMPO_OBRIGATORIO_CONDUTOR);
        }
        if (!Util.valida_CpfCnpj(condutorRestOttDTO.getCpf())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CPF_INVALIDO);
        }

        vott.lancarExceptionOtt();
    }

    public void validarCamposSituacaoVeiculoOtt(SituacaoVeiculoRestDTO situacao) {

        ValidacaoExceptionOtt vott = new ValidacaoExceptionOtt();

        if (Strings.isNullOrEmpty(situacao.getCnpj())
            || Strings.isNullOrEmpty(situacao.getSenha())
            || Strings.isNullOrEmpty(situacao.getPlacaVeiculo())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CAMPO_OBRIGATORIO_SITUACAO_VEICULO);
        }

        vott.lancarExceptionOtt();
    }

    public void validarCamposViagemCondutorOtt(ViagemCondutorOttDTO viagem) {

        ValidacaoExceptionOtt vott = new ValidacaoExceptionOtt();

        if (Strings.isNullOrEmpty(viagem.getCnpjOtt())
            || Strings.isNullOrEmpty(viagem.getSenhaUsuario())
            || Strings.isNullOrEmpty(viagem.getCpfCondutor())
            || Strings.isNullOrEmpty(viagem.getPlacaVeiculo())
            || viagem.getDistanciaCorrida() == null
            || viagem.getValorCorrida() == null
            || viagem.getValorDesconto() == null) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CAMPO_OBRIGATORIO_SITUACAO_VEICULO);
        }

        if (!Util.valida_CpfCnpj(viagem.getCnpjOtt())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CPF_INVALIDO);
        }

        vott.lancarExceptionOtt();
    }

    public void validarCamposSituacaoCondutorOtt(SituacaoCondutorRestDTO situacao) {

        ValidacaoExceptionOtt vott = new ValidacaoExceptionOtt();

        if (Strings.isNullOrEmpty(situacao.getCnpj())
            || Strings.isNullOrEmpty(situacao.getSenha())
            || Strings.isNullOrEmpty(situacao.getCpfCondutor())) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CAMPO_OBRIGATORIO_SITUACAO_CONDUTOR);
        }

        vott.lancarExceptionOtt();
    }

    public ViagemCondutorOttDTO salvarViagemCondutor(@RequestBody ViagemCondutorOttDTO viagemCondutorOttDTO) {

        OperadoraTecnologiaTransporte ott = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(StringUtil.retornaApenasNumeros(viagemCondutorOttDTO.getCnpjOtt()));
        if (ott != null) {
            CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.
                buscarCondutorOttPorCPF(viagemCondutorOttDTO.getCnpjOtt(), StringUtil.retornaApenasNumeros(viagemCondutorOttDTO.getCpfCondutor()));
            if (condutor != null) {
                VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.
                    buscarVeiculoOttPorCNPJPlaca(viagemCondutorOttDTO.getCnpjOtt(), StringUtil.removeCaracteresEspeciais(viagemCondutorOttDTO.getPlacaVeiculo()));
                if (veiculo != null) {
                    ViagemCondutorOtt viagemCondutorOtt = new ViagemCondutorOtt();

                    viagemCondutorOtt.setOtt(ott);
                    viagemCondutorOtt.setCondutorOtt(condutor);
                    viagemCondutorOtt.setVeiculoOtt(veiculo);
                    viagemCondutorOtt.setDistanciaCorrida(viagemCondutorOttDTO.getDistanciaCorrida());
                    viagemCondutorOtt.setGeneroPassageiro(Sexo.getEnumByDTO(viagemCondutorOttDTO.getGeneroPassageiro()));
                    viagemCondutorOtt.setValorCorrida(viagemCondutorOttDTO.getValorCorrida());
                    viagemCondutorOtt.setValorDesconto(viagemCondutorOttDTO.getValorDesconto());
                    viagemCondutorOtt.setDataViagem(new Date());

                    viagemCondutorOtt = em.merge(viagemCondutorOtt);
                    viagemCondutorOttDTO.setId(viagemCondutorOtt.getId());

                    return viagemCondutorOttDTO;

                }
                throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.VEICULO_INXISTENTE);
            }
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.CONDUTOR_INXISTENTE);
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.OTT_INEXISTENTE);
    }

    public String recuperarSituacaoVeiculoOTT(SituacaoVeiculoRestDTO situacao) {
        String retornoSituacao = veiculoOperadoraTecnologiaTransporteFacade.buscarSituacaoVeiuculoPorOTT(situacao.getCnpj(), situacao.getPlacaVeiculo());
        if (retornoSituacao != null) {
            return retornoSituacao;
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.SEM_RESULTADOS);
    }

    public String recuperarSituacaoCondutorOTT(SituacaoCondutorRestDTO situacao) {
        String retornoSituacao = condutorOperadoraTecnologiaTransporteFacade.buscarSituacaoCondutorPorOTT(situacao.getCnpj(), situacao.getCpfCondutor());

        if (retornoSituacao != null) {
            return retornoSituacao;
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.SEM_RESULTADOS);

    }


    private Marca buscarMarca(String descricaoMarca) {
        Marca marca = marcaFacade.recuperarMarcaPorDescricao(descricaoMarca);

        if (marca == null) {
            marca = new Marca();
            marca.setDescricao(descricaoMarca.toUpperCase());
            marca.setTipoMarca(TipoMarca.CARRO);

            return marcaFacade.salvarMerge(marca);
        }
        return marca;
    }

    private Cor buscarCor(String descricaoCor) {
        Cor cor = corFacade.findByDescricao(descricaoCor);

        if (cor == null) {
            cor = new Cor();
            cor.setDescricao(descricaoCor.toUpperCase());

            return corFacade.salvarMerge(cor);
        }
        return cor;
    }

    public CondutorOttDTO excluirCondutorOtt(@RequestBody CondutorOttDTO condutorOttDTO) {
        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(condutorOttDTO.getOperadoraTransporte().getCnpj());
        if (operadoraTecnologiaTransporte != null) {
            CondutorOperadoraTecnologiaTransporte condutor = em.find(CondutorOperadoraTecnologiaTransporte.class, condutorOttDTO.getId());
            em.remove(condutor);
            return condutorOttDTO;
        }
        return condutorOttDTO;
    }

    public VeiculoOttDTO excluirVeiculoOtt(@RequestBody VeiculoOttDTO veiculoOttDTO) {
        VeiculoOperadoraTecnologiaTransporte operadoraTecnologiaTransporte = veiculoOperadoraTecnologiaTransporteFacade.buscarVeiculoOttPorCNPJPlaca(veiculoOttDTO.getOperadoraTransporte().getCnpj(), veiculoOttDTO.getPlacaVeiculo());

        if (operadoraTecnologiaTransporte != null) {
            VeiculoOperadoraTecnologiaTransporte veiculo = em.find(VeiculoOperadoraTecnologiaTransporte.class, veiculoOttDTO.getId());
            List<CondutorOperadoraTecnologiaTransporte> condutores = veiculoOperadoraTecnologiaTransporteFacade.buscarCondutoresPorVeiculo(veiculo);
            for (CondutorOperadoraTecnologiaTransporte cd : condutores) {
                List<CondutorOperadoraVeiculoOperadora> aRemover = Lists.newArrayList();
                for (CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculo : cd.getCondutorOperadoraVeiculoOperadoras()) {
                    if (condutorOperadoraVeiculo.getVeiculoOTTransporte().getId().equals(veiculo.getId())) {
                        aRemover.add(condutorOperadoraVeiculo);
                    }
                }
                for (CondutorOperadoraVeiculoOperadora remover : aRemover) {
                    cd.getCondutorOperadoraVeiculoOperadoras().remove(remover);
                }
                em.merge(cd);
            }
            em.remove(veiculo);
            return veiculoOttDTO;
        }
        return veiculoOttDTO;
    }

    private VeiculoOperadoraTecnologiaTransporte getVeiculoOperadoraTecnologiaTransporte(VeiculoOttDTO veiculoOttDTO) throws Exception {
        VeiculoOperadoraTecnologiaTransporte veiculo = new VeiculoOperadoraTecnologiaTransporte();
        veiculo.setPlacaVeiculo(veiculoOttDTO.getPlacaVeiculo().toUpperCase());
        veiculo.setOperadoraTransporte(operadoraTecnologiaTransporteFacade.recuperar(veiculoOttDTO.getOperadoraTransporte().getId()));
        veiculo.setModeloVeiculo(veiculoOttDTO.getModeloVeiculo().toUpperCase());
        veiculo.setMarca(em.find(Marca.class, veiculoOttDTO.getMarca().getId()));
        veiculo.setAnoFabricacaoVeiculo(veiculoOttDTO.getAnoFabricacaoVeiculo());
        veiculo.setCor(em.find(Cor.class, veiculoOttDTO.getCor().getId()));
        veiculo.setCrlv(veiculoOttDTO.getCrlv());
        veiculo.setVeiculoPoluente(VeiculoPoluente.getEnumByDTO(veiculoOttDTO.getVeiculoPoluente()));
        veiculo.setVeiculoAdaptado(veiculoOttDTO.getVeiculoAdaptado());
        veiculo.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.CADASTRADO);
        veiculo.setAlugado(veiculoOttDTO.getAlugado());
        addAnexosVeiculo(veiculo, veiculoOttDTO.getAnexos());
        return veiculo;
    }

    public void addAnexosVeiculo(VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte,
                                 List<AnexoVeiculoOttDTO> anexos) throws Exception {
        for (AnexoVeiculoOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                VeiculoOperadoraDetentorArquivo veicArq = new VeiculoOperadoraDetentorArquivo();
                veicArq.setVeiculoOtt(veiculoOperadoraTecnologiaTransporte);
                veicArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                veicArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                veiculoOperadoraTecnologiaTransporte.getVeiculoOperadoraDetentorArquivos().add(veicArq);
            }
        }
    }

    public CondutorOttDTO salvarCondutorOTT(CondutorOttDTO condutorOttDTO) throws Exception {
        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(condutorOttDTO.getOperadoraTransporte().getCnpj());
        if (operadoraTecnologiaTransporte != null) {
            if (condutorOttDTO.getId() != null) {
                CondutorOperadoraTecnologiaTransporte condutor = getCondutorOperadoraTecnologiaTransporte(condutorOttDTO);
                condutor.getHistoricoSituacoesCondutor().addAll(condutorOperadoraTecnologiaTransporteFacade.recuperarHistoricoCondutor(condutorOttDTO.getId()));
                condutor.setId(condutorOttDTO.getId());
                condutorOperadoraTecnologiaTransporteFacade.criarHistoricoSituacoesCondutor(condutor);
                condutor = em.merge(condutor);
                condutorOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacao(condutor);
                condutorOttDTO.setId(condutor.getId());
                return condutorOttDTO;
            }
            CondutorOperadoraTecnologiaTransporte condutor = getCondutorOperadoraTecnologiaTransporte(condutorOttDTO);
            condutorOperadoraTecnologiaTransporteFacade.criarHistoricoSituacoesCondutor(condutor);
            condutor = em.merge(condutor);

            condutorOperadoraTecnologiaTransporteFacade.criarNotificacaoAguardandoAprovacao(condutor);
            condutorOttDTO.setId(condutor.getId());
            return condutorOttDTO;
        }
        return null;
    }

    public void addAnexosCondutor(CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte,
                                  List<AnexoCondutorOttDTO> anexos) throws Exception {
        for (AnexoCondutorOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                CondutorOperadoraDetentorArquivo condArq = new CondutorOperadoraDetentorArquivo();
                condArq.setCondutorOtt(condutorOperadoraTecnologiaTransporte);
                condArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                condArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                condutorOperadoraTecnologiaTransporte.getCondutorOperadoraDetentorArquivos().add(condArq);
            }
        }
    }

    public CondutorOttDTO desativarCondutorOTT(@RequestBody CondutorOttDTO condutorOttDTO) {
        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte = operadoraTecnologiaTransporteFacade.buscarOperadoraPorCNPJ(condutorOttDTO.getOperadoraTransporte().getCnpj());
        if (operadoraTecnologiaTransporte != null) {
            CondutorOperadoraTecnologiaTransporte condutor = getCondutorOperadoraTecnologiaTransporteFacade().buscarCondutorOttPorCPF(condutorOttDTO.getOperadoraTransporte().getCnpj(), condutorOttDTO.getCpf());
            condutor.setSituacaoCondutorOTT(SituacaoCondutorOTT.DESATIVADO);
            condutor.setMotivoDesativamento(condutorOttDTO.getMotivoDesativamento());
            em.merge(condutor);
            return condutorOttDTO;
        }
        return condutorOttDTO;
    }

    public VeiculoOttDTO desativarVeiculoOTT(@RequestBody VeiculoOttDTO veiculoOttDTO) {
        VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte = veiculoOperadoraTecnologiaTransporteFacade.buscarVeiculoOttPorCNPJPlaca(veiculoOttDTO.getOperadoraTransporte().getCnpj(), veiculoOttDTO.getPlacaVeiculo());
        if (veiculoOperadoraTecnologiaTransporte != null) {
            VeiculoOperadoraTecnologiaTransporte veiculo = getVeiculoOperadoraTecnologiaTransporteFacade().buscarVeiculoOttPorCNPJPlaca(veiculoOttDTO.getOperadoraTransporte().getCnpj(), veiculoOttDTO.getPlacaVeiculo());
            veiculo.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.DESATIVADO);
            veiculo.setMotivoDesativamento(veiculoOttDTO.getMotivoDesativamento());
            em.merge(veiculo);
            return veiculoOttDTO;
        }
        return veiculoOttDTO;
    }

    private CondutorOperadoraTecnologiaTransporte getCondutorOperadoraTecnologiaTransporte(CondutorOttDTO condutorOttDTO) throws Exception {
        CondutorOperadoraTecnologiaTransporte condutor = new CondutorOperadoraTecnologiaTransporte();
        condutor.setOperadoraTecTransporte(operadoraTecnologiaTransporteFacade.recuperar(condutorOttDTO.getOperadoraTransporte().getId()));
        condutor.setCpf(Util.formatarCpf(condutorOttDTO.getCpf()));
        condutor.setNomeCondutor(condutorOttDTO.getNomeCondutor().toUpperCase());
        condutor.setCnh(condutorOttDTO.getCnh());
        condutor.setRg(condutorOttDTO.getRg());
        condutor.setCep(condutorOttDTO.getCep());
        condutor.setEnderecoLogradouro(condutorOttDTO.getEnderecoLogradouro());
        condutor.setNumeroEndereco(condutorOttDTO.getNumeroEndereco());
        condutor.setBairro(condutorOttDTO.getBairro());
        condutor.setCidade(condutorOttDTO.getCidade());
        condutor.setUf(condutorOttDTO.getUf());
        condutor.setComplemento(condutorOttDTO.getComplemento());
        condutor.setDataCadastro(new Date());
        condutor.setCep(condutorOttDTO.getCep());
        condutor.setTelefoneComercial(condutorOttDTO.getTelefoneComercial());
        condutor.setCelular(condutorOttDTO.getCelular());
        condutor.setGenero(Sexo.getEnumByDTO(condutorOttDTO.getGenero()));
        condutor.setEquipamentoCondutor(EquipamentoCondutor.getEnumByDTO(condutorOttDTO.getEquipamentoCondutor()));
        condutor.setSituacaoCondutorOTT(SituacaoCondutorOTT.CADASTRADO);
        condutor.setServidorPublico(condutorOttDTO.getServidorPublico());
        if (condutorOttDTO.getFoto() != null) {
            Arquivo foto = Arquivo.toArquivo(condutorOttDTO.getFoto().getId(), condutorOttDTO.getFoto().getNome(),
                condutorOttDTO.getFoto().getDescricao(), condutorOttDTO.getFoto().getMimeType(),
                condutorOttDTO.getFoto().getTamanho(), condutorOttDTO.getFoto().getConteudo());
            condutor.setFoto(foto);
        }
        addAnexosCondutor(condutor, condutorOttDTO.getAnexos());
        return condutor;
    }

    public void addAnexosOperadoraTecnologiaTransporte(OperadoraTecnologiaTransporte operadoraTecnologiaTransporte,
                                                       List<AnexoCredenciamentoOttDTO> anexos) throws Exception {
        for (AnexoCredenciamentoOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                OperadoraTransporteDetentorArquivo ottArq = new OperadoraTransporteDetentorArquivo();
                ottArq.setOperadoraTecTransporte(operadoraTecnologiaTransporte);
                ottArq.popularDadosDocumentoCredenciamento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                ottArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                operadoraTecnologiaTransporte.getDetentorArquivoComposicao().add(ottArq);
            }
        }
    }

    public void salvarSolicitacaoCadastroViaPortal(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        Pessoa pessoa = null;
        TipoPessoa tipoPessoa = TipoPessoa.FISICA;
        if (solicitacaoCadastro.temCpf()) {
            pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(solicitacaoCadastro.getCpf(), false);
        } else {
            tipoPessoa = TipoPessoa.JURIDICA;
            pessoa = pessoaFacade.buscarPessoaJuridicaPorCNPJ(solicitacaoCadastro.getCnpj(), false);
        }
        if (pessoa == null) {
            criarPessoaComSolicitacaoDeCadastro(solicitacaoCadastro);
        } else {
            atualizarPessoaDaSolicitacaoDeCadastro(solicitacaoCadastro, pessoa, tipoPessoa);
        }
    }

    public boolean verificarPessoaRejeitada(String key) {
        SolicitacaoCadastroPessoa solicitacao = solicitacaoCadastroPessoaFacade.findByKey(key);
        SituacaoCadastralPessoa situacao = getPessoaFacade().buscarSituacaoCadastralPessoaPorCpfCnpj(solicitacao.getCpfCnpj());
        return SituacaoCadastralPessoa.REJEITADO.equals(situacao);
    }

    public CalculoSolicitacaoCadastroCredor gerarDebitoSolicitacaoCadastroCredor(String key) {
        SolicitacaoCadastroPessoa solicitacao = solicitacaoCadastroPessoaFacade.findByKey(key);
        Pessoa pessoa = getPessoaFacade().buscarPessoaPorCpfOrCnpj(solicitacao.getCpfCnpj());
        return gerarDebito(pessoa, solicitacao);
    }

    public CalculoSolicitacaoCadastroCredor gerarDebito(Pessoa pessoa, SolicitacaoCadastroPessoa solicitacao) {
        try {
            ConfiguracaoPortalContribuinte config = configuracaoPortalContribuinteFacade.buscarUtilmo();
            ProcessoCalculoSolicitacaoCadastroCredor processo = new ProcessoCalculoSolicitacaoCadastroCredor();
            processo.setExercicio(sistemaFacade.getExercicioCorrente());
            processo.setDataLancamento(sistemaFacade.getDataOperacao());
            processo.setDivida(config.getDividaCredor());
            processo = em.merge(processo);

            BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
            if (valorUFMVigente.equals(BigDecimal.ZERO)) {
                logger.error("PortalContribuinteFacade, Valor ufm vigente está zerado: " + valorUFMVigente);
            }
            CalculoSolicitacaoCadastroCredor calculo = gerarNovoCalculoSolicitacaoCadastroCredor(processo, pessoa, solicitacao, valorUFMVigente.multiply(config.getPorcentagemUfmCredor()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
            geraValorDividaSolicitacaoCadastroCredor.geraValorDivida(calculo, buscarOpcoesPagamentoDivida(config));
            return calculo;
        } catch (Exception e) {
            logger.error("Problema ao gerar opções de pagamento do débito no cálculo de solicitação de cadastro de credor {}", e);
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private CalculoSolicitacaoCadastroCredor gerarNovoCalculoSolicitacaoCadastroCredor(ProcessoCalculoSolicitacaoCadastroCredor processo, Pessoa pessoa, SolicitacaoCadastroPessoa solicitacao, BigDecimal valor) {
        CalculoSolicitacaoCadastroCredor calculo = new CalculoSolicitacaoCadastroCredor();
        calculo.setSolicitacaoCadastroPessoa(solicitacao);
        calculo.setProcessoCalculoSolicitacao(processo);
        calculo.setProcessoCalculo(processo);
        calculo.setDataCalculo(processo.getDataLancamento());
        calculo.setSubDivida(1L);
        calculo.setReferencia("Solicitação de Cadastro de Credor " + pessoa.getNome());
        calculo.setObservacao("Gerado por solicitação de cadastro de credor pelo portal do contribuinte, " + pessoa.getNome() + " - " + pessoa.getEmail());
        calculo.setValorEfetivo(valor);
        calculo.setValorReal(valor);
        gerarCalculoPessoa(calculo, pessoa);
        calculo = em.merge(calculo);
        return calculo;
    }

    private void gerarCalculoPessoa(CalculoSolicitacaoCadastroCredor calculo, Pessoa pessoa) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        calculoPessoa.setPessoa(pessoa);
        calculo.getPessoas().add(calculoPessoa);
    }

    private List<OpcaoPagamentoDivida> buscarOpcoesPagamentoDivida(ConfiguracaoPortalContribuinte config) {
        if (config.getDividaCredor() == null) {
            throw new ExcecaoNegocioGenerica("Dívida não configurada para a solicitação de cadastro de credor.");
        }
        if (config.getTributoCredor() == null) {
            throw new ExcecaoNegocioGenerica("Tributo não configurado para a solicitação de cadastro de credor.");
        }
        return geraValorDividaSolicitacaoCadastroCredor.validaOpcoesPagamento(config.getDividaCredor(), new Date());
    }

    private void atualizarPessoaDaSolicitacaoDeCadastro(WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa, TipoPessoa tipoPessoa) {
        atualizarDadosPessoa(solicitacaoCadastro, pessoa, tipoPessoa);
        processarTelefones(solicitacaoCadastro, pessoa);
        processarEndereco(solicitacaoCadastro, pessoa);
        popularDocumentosObrigatorios(solicitacaoCadastro, pessoa);
        atualizarContasBancariasDaPessoa(solicitacaoCadastro, pessoa);
        if (SituacaoCadastralPessoa.REJEITADO.equals(pessoa.getSituacaoCadastralPessoa())) {
            pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
            pessoa.adicionarHistoricoSituacaoCadastral();
        }
        em.merge(pessoa);
    }

    private void criarPessoaComSolicitacaoDeCadastro(WsSolicitacaoCadastro solicitacaoCadastro) {
        Pessoa pessoa = getPessoa(solicitacaoCadastro);
        processarTelefones(solicitacaoCadastro, pessoa);
        processarEndereco(solicitacaoCadastro, pessoa);
        em.merge(pessoa);
        solicitacaoCadastroPessoaFacade.concluirSolicitacaoCadastro(solicitacaoCadastro.getKey());
    }

    private Pessoa getPessoa(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        Pessoa pessoa = null;
        if (solicitacaoCadastro.temCpf()) {
            pessoa = getNovaPessoaFisica(solicitacaoCadastro);
            if (pessoaFacade.hasOutraPessoaComMesmoCpf((PessoaFisica) pessoa, false)) {
                throw new ValidacaoException("Já existe uma pessoa cadastrada com esse CPF");
            }
        } else {
            pessoa = getNovaPessoaJuridica(solicitacaoCadastro);
            if (pessoaFacade.hasOutraPessoaComMesmoCnpj((PessoaJuridica) pessoa, false)) {
                throw new ValidacaoException("Já existe uma pessoa cadastrada com esse CNPJ");
            }
            processarCnaes(solicitacaoCadastro, (PessoaJuridica) pessoa);
            processarSocios(solicitacaoCadastro, (PessoaJuridica) pessoa);
        }
        pessoa.setEmail(solicitacaoCadastro.getEmail());
        pessoa.setHomePage(solicitacaoCadastro.getHomePage());
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        pessoa.adicionarHistoricoSituacaoCadastral();
        pessoa.setKey(solicitacaoCadastro.getKey());
        pessoa.setUnidadeOrganizacional(solicitacaoCadastro.getIdUnidadeOrganizacional() != null ? (UnidadeOrganizacional) hierarquiaOrganizacionalFacade.recuperar(UnidadeOrganizacional.class, solicitacaoCadastro.getIdUnidadeOrganizacional()) : null);
        pessoa.setContaCorrenteBancPessoas(Lists.newArrayList());
        popularDocumentosObrigatorios(solicitacaoCadastro, pessoa);
        atualizarContasBancariasDaPessoa(solicitacaoCadastro, pessoa);
        return pessoa;
    }

    private void atualizarContasBancariasDaPessoa(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa) {
        if (solicitacaoCadastro.getContasBancarias() != null && !solicitacaoCadastro.getContasBancarias().isEmpty()) {
            if (pessoa.getId() != null) {
                pessoa.getContaCorrenteBancPessoas().clear();
            }
            solicitacaoCadastro.getContasBancarias().forEach(cb -> {
                ContaCorrenteBancPessoa novaConta = criarContaCorrenteBancPessoa(cb, pessoa);
                if (novaConta.getPrincipal()) {
                    pessoa.setContaCorrentePrincipal(novaConta);
                }
                pessoa.getContaCorrenteBancPessoas().add(novaConta);
            });
        }
    }

    private ContaCorrenteBancPessoa criarContaCorrenteBancPessoa(ContaCorrenteBancariaDTO dto, Pessoa pessoa) {
        if (dto == null) {
            return null;
        }
        ContaCorrenteBancPessoa ccbp = new ContaCorrenteBancPessoa();
        ccbp.setContaCorrenteBancaria(new ContaCorrenteBancaria());
        ccbp.setPessoa(pessoa);
        ccbp.setPrincipal(dto.getPrincipal());
        ccbp.getContaCorrenteBancaria().setAgencia(getAgenciaFacade().recuperar(dto.getAgenciaDTO().getId()));
        ccbp.getContaCorrenteBancaria().setModalidadeConta(ModalidadeConta.valueOf(dto.getNameModalidadeConta()));
        ccbp.getContaCorrenteBancaria().setNumeroConta(dto.getNumeroConta());
        ccbp.getContaCorrenteBancaria().setDigitoVerificador(dto.getDigitoVerificador());
        ccbp.getContaCorrenteBancaria().setSituacao(SituacaoConta.ATIVO);
        return ccbp;
    }

    private void popularDocumentosObrigatorios(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa) {
        try {
            if (solicitacaoCadastro.getArquivosObrigatorios() == null || solicitacaoCadastro.getArquivosObrigatorios().isEmpty()) {
                return;
            }
            DetentorArquivoComposicao detentorArquivoComposicao = pessoa.getDetentorArquivoComposicao() != null ?
                pessoa.getDetentorArquivoComposicao() : new DetentorArquivoComposicao();
            for (ArquivoDTO arquivoDTO : solicitacaoCadastro.getArquivosObrigatorios()) {
                ArquivoComposicao arquivoComposicao = detentorArquivoComposicao.buscarPorNomeArquivo(arquivoDTO.getNome());
                if (arquivoComposicao == null) {
                    arquivoComposicao = new ArquivoComposicao();
                }
                arquivoComposicao.setDataUpload(new Date());
                String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                String data = arquivoDTO.getConteudo().split("base64,")[1];
                String mimeType;
                try {
                    mimeType = dataInfo.split(":")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    mimeType = null;
                }
                Base64 decoder = new Base64();
                byte[] imgBytes = decoder.decode(data);
                arquivoComposicao.setArquivo(arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes)));
                arquivoComposicao.getArquivo().setNome(arquivoDTO.getNome());
                arquivoComposicao.getArquivo().setDescricao(arquivoDTO.getDescricao());
                arquivoComposicao.getArquivo().setMimeType(mimeType);
                if (arquivoComposicao.getDetentorArquivoComposicao() == null) {
                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);
                }
            }
            pessoa.setDetentorArquivoComposicao(detentorArquivoComposicao);
        } catch (Exception e) {
            logger.error("não foi possível popular os arquivos ", e);
        }
    }

    private Processo getProtocolo(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, Pessoa
        pessoa, ConfiguracaoTributario configuracaoTributario) {
        Processo protocolo = new Processo();
        protocolo.setProtocolo(Boolean.TRUE);
        protocolo.setAssunto(configuracaoTributario.getAssunto());
        protocolo.setUoCadastro(configuracaoTributario.getUnidadeOrganizacional());
        protocolo.setResponsavelCadastro(configuracaoTributario.getUsuarioSistema());
        protocolo.setPessoa(pessoa);
        protocolo.setExercicio(exercicioFacade.getExercicioPorAno(LocalDate.now().getYear()));
        protocolo.setAno(LocalDate.now().getYear());
        protocolo.setDataRegistro(LocalDate.now().toDate());
        protocolo.setNumero(processoFacade.gerarCodigo(protocolo));
        protocolo.setSenha(processoFacade.geraSenha());
        protocolo.setSituacao(SituacaoProcesso.GERADO);
        protocolo.setObjetivo(getObjetivoDoProcesso(solicitacaoCadastro, configuracaoTributario));
        criarTramiteParaProtocolo(protocolo);
        return protocolo;
    }

    private void criarTramiteParaProtocolo(Processo protocolo) {
        Tramite tramite = new Tramite();
        tramite.setDataRegistro(new Date());
        tramite.setProcesso(protocolo);
        tramite.setUnidadeOrganizacional(protocolo.getUoCadastro());
        tramite.setOrigem(protocolo.getUoCadastro());
        tramite.setSituacaoTramite(null);
        tramite.setIndice(0);
        tramite.setStatus(true);
        tramite.setAceito(false);
        tramite.setUsuarioTramite(protocolo.getResponsavelCadastro());
        protocolo.getTramites().add(tramite);
    }

    private String getObjetivoDoProcesso(WsSolicitacaoCadastro solicitacaoCadastro, ConfiguracaoTributario
        configuracaoTributario) {
        StringBuilder objetivo = new StringBuilder();
        objetivo.append(configuracaoTributario.getAssunto()).append(". ")
            .append("Dados cadastrais: ");
        if (solicitacaoCadastro.temCpf()) {
            objetivo.append("CPF: ").append(solicitacaoCadastro.getCep())
                .append(", Nome: ").append(solicitacaoCadastro.getNome())
                .append(", Data de Nascimento: ").append(DataUtil.getDataFormatada(solicitacaoCadastro.getDataNascimento()))
                .append(", Sexo: ").append(solicitacaoCadastro.getSexo())
                .append(", Nome da Mãe: ").append(solicitacaoCadastro.getNomeMae())
                .append(", Endereço Residencial: ").append(solicitacaoCadastro.getCep())
                .append(", ").append(solicitacaoCadastro.getLocalidade())
                .append(", ").append(solicitacaoCadastro.getUf())
                .append(", ").append(solicitacaoCadastro.getLogradouro())
                .append(", ").append(solicitacaoCadastro.getNumero());
        } else {
            objetivo.append("CNPJ: ").append(solicitacaoCadastro.getCnpj())
                .append(", Razão Social: ").append(solicitacaoCadastro.getRazaoSocial())
                .append(", Nome Fantasia: ").append(solicitacaoCadastro.getNomeFantasia())
                .append(", Endereço Comercial: ").append(solicitacaoCadastro.getCep())
                .append(", ").append(solicitacaoCadastro.getLocalidade())
                .append(", ").append(solicitacaoCadastro.getUf())
                .append(", ").append(solicitacaoCadastro.getLogradouro())
                .append(", ").append(solicitacaoCadastro.getNumero());
        }
        return objetivo.toString();
    }

    private void processarEndereco(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa) {
        if (pessoa.getId() != null) {
            pessoa.getEnderecos().clear();
        }
        EnderecoCorreio endereco = getNovoEndereco(solicitacaoCadastro);
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.getEnderecos().add(endereco);
    }

    private void processarCnaes(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, PessoaJuridica pessoa) {
        if (solicitacaoCadastro.getCnaes() != null) {
            for (CnaeDTO cnaeDTO : solicitacaoCadastro.getCnaes()) {
                PessoaCNAE pessoaCNAE = getNovoPessoaCnae(cnaeDTO);
                if (pessoaCNAE != null) {
                    pessoaCNAE.setPessoa(pessoa);
                    pessoa.getPessoaCNAE().add(pessoaCNAE);
                }
            }
        }
    }

    private void processarSocios(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, PessoaJuridica pessoa) {
        if (solicitacaoCadastro.getSocios() != null) {
            for (WSPessoa wsPessoa : solicitacaoCadastro.getSocios()) {
                SociedadePessoaJuridica sociedadePessoaJuridica = getNovaSociedadePessoa(wsPessoa);
                if (sociedadePessoaJuridica != null) {
                    sociedadePessoaJuridica.setPessoaJuridica(pessoa);
                    pessoa.getSociedadePessoaJuridica().add(sociedadePessoaJuridica);
                }
            }
        }
    }

    private void atualizarDadosPessoa(WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa, TipoPessoa tipoPessoa) {
        if (TipoPessoa.FISICA.equals(tipoPessoa)) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getRg() == null && solicitacaoCadastro.hasRg()) {
                pf.getDocumentosPessoais().add(new RG(pf, solicitacaoCadastro.getRg(),
                    solicitacaoCadastro.getOrgaoEmissao(), ufFacade.recuperar(solicitacaoCadastro.getUfRG().getId()),
                    solicitacaoCadastro.getEmissaoRG()));
            } else {
                pf.getRg().setNumero(solicitacaoCadastro.getRg());
                pf.getRg().setOrgaoEmissao(solicitacaoCadastro.getOrgaoEmissao());
                if (solicitacaoCadastro.getUfRG() != null) {
                    pf.getRg().setUf(ufFacade.recuperaUfPorUf(solicitacaoCadastro.getUfRG().getUf()));
                }
                pf.getRg().setDataemissao(solicitacaoCadastro.getEmissaoRG());
            }
            pessoa.setUnidadeOrganizacional(solicitacaoCadastro.getIdUnidadeOrganizacional() != null ? (UnidadeOrganizacional) hierarquiaOrganizacionalFacade.recuperar(UnidadeOrganizacional.class, solicitacaoCadastro.getIdUnidadeOrganizacional()) : null);
            pf.setNome(solicitacaoCadastro.getNome());
            pf.setNomeTratamento(solicitacaoCadastro.getNomeTratamento());
            pf.setDataNascimento(solicitacaoCadastro.getDataNascimento());
            pf.setSexo(solicitacaoCadastro.getSexo());
            pf.setMae(solicitacaoCadastro.getNomeMae());
            pf.setPai(solicitacaoCadastro.getNomePai());
            pf.setHomePage(solicitacaoCadastro.getHomePage());
            if (solicitacaoCadastro.getNivelEsolaridade() != null) {
                pf.setNivelEscolaridade(nivelEscolaridadeFacade.recuperar(solicitacaoCadastro.getNivelEsolaridade().getId()));
            }
            if (solicitacaoCadastro.getProfissao() != null) {
                pf.setProfissao(profissaoFacade.recuperar(solicitacaoCadastro.getProfissao().getId()));
            }
        } else {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            pj.setInscricaoEstadual(solicitacaoCadastro.getInscricaoEstadual());
            pj.setNomeFantasia(solicitacaoCadastro.getNomeFantasia());
            pj.setRazaoSocial(solicitacaoCadastro.getRazaoSocial());
            pj.setCei(solicitacaoCadastro.getCei());
            pj.setTipoEmpresa(solicitacaoCadastro.hasTipoEmpresa() ? TipoEmpresa.valueOf(solicitacaoCadastro.getTipoEmpresa().name()) : null);
        }
    }

    private void processarTelefones(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro, Pessoa pessoa) {
        if (pessoa.getId() != null) {
            pessoa.getTelefones().clear();
        }
        if (solicitacaoCadastro.getTelefoneResidencial() != null && !solicitacaoCadastro.getTelefoneResidencial().isEmpty()) {
            Telefone telefone = getNovoTelefone(pessoa, solicitacaoCadastro.getTelefoneResidencial(), TipoTelefone.RESIDENCIAL);
            if (pessoa.getTelefonePrincipal() == null) {
                pessoa.setTelefonePrincipal(telefone);
            }
            pessoa.getTelefones().add(telefone);
        }
        if (solicitacaoCadastro.getTelefoneCelular() != null && !solicitacaoCadastro.getTelefoneCelular().isEmpty()) {
            Telefone telefone = getNovoTelefone(pessoa, solicitacaoCadastro.getTelefoneCelular(), TipoTelefone.CELULAR);
            if (pessoa.getTelefonePrincipal() == null) {
                pessoa.setTelefonePrincipal(telefone);
            }
            pessoa.getTelefones().add(telefone);
        }
        if (solicitacaoCadastro.getTelefoneComercial() != null && !solicitacaoCadastro.getTelefoneComercial().isEmpty()) {
            Telefone telefone = getNovoTelefone(pessoa, solicitacaoCadastro.getTelefoneComercial(), TipoTelefone.COMERCIAL);
            if (pessoa.getTelefonePrincipal() == null) {
                pessoa.setTelefonePrincipal(telefone);
            }
            pessoa.getTelefones().add(telefone);
        }
    }

    private Telefone getNovoTelefone(Pessoa pessoa, String telefoneInformado, TipoTelefone tipoTelefone) {
        Telefone telefone = new Telefone();
        telefone.setPessoa(pessoa);
        telefone.setTelefone(telefoneInformado);
        telefone.setTipoFone(tipoTelefone);
        if (pessoa.getTelefonePrincipal() == null) {
            telefone.setPrincipal(Boolean.TRUE);
        }
        return telefone;
    }

    private PessoaJuridica getNovaPessoaJuridica(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj(Util.formatarCnpj(solicitacaoCadastro.getCnpj()));
        pj.setInscricaoEstadual(solicitacaoCadastro.getInscricaoEstadual());
        pj.setNomeFantasia(solicitacaoCadastro.getNomeFantasia());
        pj.setRazaoSocial(solicitacaoCadastro.getRazaoSocial());
        pj.setCei(solicitacaoCadastro.getCei());
        pj.setTipoEmpresa(solicitacaoCadastro.hasTipoEmpresa() ? TipoEmpresa.valueOf(solicitacaoCadastro.getTipoEmpresa().name()) : null);
        return pj;
    }

    private PessoaFisica getNovaPessoaFisica(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        PessoaFisica pf = new PessoaFisica();
        pf.setCpf(Util.formatarCpf(solicitacaoCadastro.getCpf()));
        pf.setDataNascimento(solicitacaoCadastro.getDataNascimento());
        pf.setMae(solicitacaoCadastro.getNomeMae());
        pf.setNome(solicitacaoCadastro.getNome());
        pf.setNomeTratamento(solicitacaoCadastro.getNomeTratamento());
        pf.setPai(solicitacaoCadastro.getNomePai());
        pf.setSexo(solicitacaoCadastro.getSexo());
        pf.setHomePage(solicitacaoCadastro.getHomePage());
        pf.setRacaCor(RacaCor.NAO_INFORMADA);
        pf.setNivelEscolaridade(solicitacaoCadastro.hasNivelEsolaridade() ? nivelEscolaridadeFacade.recuperar(solicitacaoCadastro.getNivelEsolaridade().getId()) : null);
        pf.setProfissao(solicitacaoCadastro.hasProfissao() ? profissaoFacade.recuperar(solicitacaoCadastro.getProfissao().getId()) : null);
        if (solicitacaoCadastro.hasRg()) {
            pf.getDocumentosPessoais().add(new RG(pf, solicitacaoCadastro.getRg(),
                solicitacaoCadastro.getOrgaoEmissao(), ufFacade.recuperar(solicitacaoCadastro.getUfRG().getId()),
                solicitacaoCadastro.getEmissaoRG()));
        }
        if (solicitacaoCadastro.hasPisPasep()) {
            pf.getDocumentosPessoais().add(new CarteiraTrabalho(pf, solicitacaoCadastro.getPisPasep(), ufFacade.recuperar(solicitacaoCadastro.getUfRG().getId())));
        }
        pf.getDocumentosPessoais().add(new Habilitacao(pf, solicitacaoCadastro.getNumeroCnh(), solicitacaoCadastro.getCategoriaCnh(), solicitacaoCadastro.getValidadeCnh(), new Date()));
        return pf;
    }

    private EnderecoCorreio getNovoEndereco(@RequestBody WsSolicitacaoCadastro solicitacaoCadastro) {
        EnderecoCorreio endereco = new EnderecoCorreio();
        endereco.setCep(solicitacaoCadastro.getCep());
        endereco.setLocalidade(solicitacaoCadastro.getLocalidade());
        endereco.setUf(solicitacaoCadastro.getUf());
        endereco.setLogradouro(solicitacaoCadastro.getLogradouro());
        endereco.setNumero(solicitacaoCadastro.getNumero());
        endereco.setBairro(solicitacaoCadastro.getBairro());
        endereco.setComplemento(solicitacaoCadastro.getComplemento());
        endereco.setTipoEndereco(solicitacaoCadastro.temCpf() ? TipoEndereco.RESIDENCIAL : TipoEndereco.COMERCIAL);
        endereco.setPrincipal(Boolean.TRUE);
        return endereco;
    }

    private PessoaCNAE getNovoPessoaCnae(CnaeDTO cnaeDTO) {
        CNAE cnae = cnaeFacade.recuperar(cnaeDTO.getId());
        if (cnae != null) {
            PessoaCNAE pessoaCNAE = new PessoaCNAE();
            pessoaCNAE.setCnae(cnae);
            pessoaCNAE.setDataregistro(new Date());
            pessoaCNAE.setInicio(new Date());
            return pessoaCNAE;
        }
        return null;
    }

    private SociedadePessoaJuridica getNovaSociedadePessoa(WSPessoa wsPessoa) {
        Pessoa pessoa = pessoaFacade.recuperar(wsPessoa.getId());
        if (pessoa != null) {
            SociedadePessoaJuridica socio = new SociedadePessoaJuridica();
            socio.setSocio(pessoa);
            socio.setDataInicio(new Date());
            socio.setDataRegistro(new Date());
            socio.setProporcao(wsPessoa.getProporcaoSocio().doubleValue());
            return socio;
        }
        return null;
    }

    public void alterarSenhaPortalWeb(String cpf, String novaSenha) throws
        UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException {
        Pessoa pessoa = recuperarWSPessoaPorCPF(cpf);
        if (pessoa != null) {
            UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());
            usuarioWeb.setPassword(usuarioWebFacade.encripitografarSenha(novaSenha));
            usuarioWebFacade.salvar(usuarioWeb);
        }
    }

    private WSPessoa buscarEDefineVinculosFPDaPessoa(WSPessoa wsPessoa) {
        List<WSVinculoFP> vinculos = pessoaFacade.getVinculoFPFacade().buscarVinculosPorPessoalPortal(wsPessoa.getId());
        wsPessoa.setVinculos(vinculos);
        return wsPessoa;
    }

    public WSPessoa fazerLoginPortalWeb(String senha, String cpfCnpj) throws
        UsuarioWebNaoExistenteException, UsuarioWebSenhaInvalidaException, UsuarioWebProblemasCadastroException, UsuarioWebInativoException {
        WSPessoa wsPessoa = new WSPessoa(recuperarWSPessoaPorCPF(cpfCnpj), cpfCnpj, senha);
        if (configuracaoRHFacade.habilitaDadosRHPortal()) {
            buscarEDefineVinculosFPDaPessoa(wsPessoa);
        }
        if (wsPessoa.getUsuario() != null) {
            String senhaCript = wsPessoa.getUsuario().getSenha();
            if (wsPessoa.getUsuario().getAtivo()) {
                if (Seguranca.bCryptPasswordEncoder.matches(senha, senhaCript)) {
                    usuarioWebFacade.atribuirUltimoAcesso(cpfCnpj);
                    wsPessoa.setOtt(verificarOTTDoCnpj(wsPessoa.getCpfCnpj()));
                    return wsPessoa;
                } else {
                    throw new UsuarioWebSenhaInvalidaException("Senha Inválida.");
                }
            } else {
                throw new UsuarioWebInativoException("Usuário Inátivo");
            }
        }
        throw new UsuarioWebNaoExistenteException("Nenhum usuário para essa pessoa.");
    }

    public UsuarioPortalWebDTO fazerLoginPortalWebApp(String senha, String cpfCnpj) throws
        UsuarioWebNaoExistenteException, UsuarioWebSenhaInvalidaException, UsuarioWebProblemasCadastroException {
        UsuarioWeb usuarioWeb = usuarioWebFacade.buscarNfseUserPorLogin(cpfCnpj);
        WSPessoa wsPessoa = null;
        try {
            wsPessoa = new WSPessoa(recuperarWSPessoaPorCPF(cpfCnpj), cpfCnpj, senha);
        } catch (Exception e) {
            logger.debug("Nenhuma pessoa com o CPF " + cpfCnpj, e);
        }
        ConfiguracaoRH configuracaoRH = null;
        try {
            if (usuarioWeb.getPessoa() != null) {
                configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
                if (configuracaoRH != null && configuracaoRH.getMostrarDadosRHPortal()) {
                    wsPessoa = buscarEDefineVinculosFPDaPessoa(new WSPessoa(usuarioWeb.getPessoa()));
                }
            }
        } catch (Exception e) {
            logger.debug("Nenhuma configuração de rh encontrada ", e);
        }
        if (usuarioWeb != null) {
            String senhaCript = usuarioWeb.isPasswordTemporary() ?
                usuarioWebFacade.encripitografarSenha(usuarioWeb.getPassword()) : usuarioWeb.getPassword();
            if (Seguranca.bCryptPasswordEncoder.matches(senha, senhaCript)) {
                usuarioWebFacade.atribuirUltimoAcesso(cpfCnpj);
                if (usuarioWeb.isPasswordTemporary()) {
                    usuarioWebFacade.atribuirSenhaCriotografada(cpfCnpj, senhaCript);
                }
                if (wsPessoa != null) {
                    usuarioWeb.setCreatedBy(wsPessoa.getNome());
                }
                return new UsuarioPortalWebDTO(usuarioWeb, wsPessoa);
            } else {
                throw new UsuarioWebSenhaInvalidaException("Senha Inválida.");
            }
        }
        throw new UsuarioWebNaoExistenteException("Nenhum usuário para essa pessoa.");
    }

    public WSPessoa fazerLoginPortalRestOTT(String senha, String cpfCnpj) throws ValidacaoExceptionOtt {
        WSPessoa wsPessoa = null;
        if (Util.valida_CpfCnpj(cpfCnpj)) {
            wsPessoa = new WSPessoa(recuperarWSPessoaRestOtt(cpfCnpj), cpfCnpj, senha);
            if (wsPessoa.getUsuario() != null) {
                if (wsPessoa.getUsuario().getSenha().equals(senha)) {
                    wsPessoa.setOtt(verificarOTTDoCnpj(wsPessoa.getCpfCnpj()));
                    return wsPessoa;
                } else {
                    throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.SENHA_INCORRETA);
                }
            }
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.USUARIO_INEXISTENTE);
        }
        throw new ValidacaoExceptionOtt((TipoRespostaOttDTO.CNPJ_INVALIDO));
    }

    public Pessoa recuperarWSPessoaPorCPF(String cpf) throws
        UsuarioWebProblemasCadastroException, UsuarioWebNaoExistenteException {
        List<Pessoa> pessoas = pessoaFacade.listaTodasPessoasPorCPFCNPJ(cpf, SituacaoCadastralPessoa.ATIVO);
        if (pessoas == null || pessoas.isEmpty()) {
            throw new UsuarioWebNaoExistenteException("Nenhuma pessoa com esse cpf/cnpj.");
        } else if (pessoas.size() == 1) {
            return pessoas.get(0);
        } else {
            throw new UsuarioWebProblemasCadastroException("Mais de um pessoa com esse cpf/cnpj.");
        }
    }

    public Pessoa recuperarWSPessoaRestOtt(String cpf) throws
        ValidacaoExceptionOtt {
        List<Pessoa> pessoas = pessoaFacade.listaTodasPessoasPorCPFCNPJ(cpf);
        if (pessoas == null || pessoas.isEmpty()) {
            throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.PESSOA_INEXISTENTE);
        } else if (pessoas.size() == 1) {
            Pessoa pessoa = pessoas.get(0);
            informarUltimaDataDeAcessoAoUsuario(cpf, pessoa);
            return pessoa;
        }
        throw new ValidacaoExceptionOtt(TipoRespostaOttDTO.USUARIO_INEXISTENTE);
    }

    private void informarUltimaDataDeAcessoAoUsuario(String cpfEmail, Pessoa pessoa) {
        if (pessoa.getUsuariosWeb() != null && !pessoa.getUsuariosWeb().isEmpty()) {
            boolean achou = false;
            for (UsuarioWeb usuarioWeb : pessoa.getUsuariosWeb()) {
                if (usuarioWeb.getEmail().equals(cpfEmail)) {
                    if (usuarioWeb.isPasswordTemporary()) {
                        usuarioWeb.setPassword(usuarioWebFacade.encripitografarSenha(usuarioWeb.getPassword()));
                    }
                    usuarioWeb.setUltimoAcesso(new Date());
                    achou = true;
                }
            }
            if (!achou) {
                if (pessoa.getUsuariosWeb().get(0).isPasswordTemporary()) {
                    pessoa.getUsuariosWeb().get(0).setPassword(usuarioWebFacade.encripitografarSenha(pessoa.getUsuariosWeb().get(0).getPassword()));
                }
                pessoa.getUsuariosWeb().get(0).setUltimoAcesso(new Date());
            }
        }
    }

    public ConfiguracaoRHDTO buscarConfiguracaoRHDTO() throws ExcecaoNegocioGenerica {
        return configuracaoRHFacade.buscarConfiguracaoRH().toConfiguracaoRHDTO();
    }

    public PessoaFisicaDTO buscarPessoaFisicaPorCPF(String cpf) throws
        UsuarioWebProblemasCadastroException, UsuarioWebNaoExistenteException {
        PessoaFisicaPortal pfPortal = pessoaFisicaPortalFacade.buscarPessoaPortalPorCpf(cpf);
        List<PessoaFisica> pessoas = pessoaFacade.buscarPessoasFisicasPorCpf(cpf);

        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null && !configuracaoRH.getPermitirAcessoPortal()) {
            throw new UsuarioWebProblemasCadastroException("Bloqueio Por Configuração, Sistema Bloqueado Para Atualização Cadastral");
        }
        if (pfPortal != null && SituacaoPessoaPortal.LIBERADO.equals(pfPortal.getStatus())) {
            PessoaFisica pessoa = pfPortal.getPessoaFisica();
            return validarPessoaAtualizacao(pessoa);
        }

        if (pfPortal == null) {
            if (pessoas == null || pessoas.isEmpty()) {
                throw new UsuarioWebProblemasCadastroException("Nenhuma pessoa com esse cpf.");
            } else if (pessoas.size() == 1) {
                PessoaFisica pessoa = pessoas.get(0);
                return validarPessoaAtualizacao(pessoa);
            } else if (pessoas.size() > 1) {
                throw new UsuarioWebProblemasCadastroException("Mais de um pessoa com esse cpf.");
            }
            throw new UsuarioWebNaoExistenteException("Nenhum usuário para essa pessoa.");
        } else {
            if (pessoas.size() > 1) {
                throw new UsuarioWebProblemasCadastroException("Mais de um pessoa com esse cpf.");
            }
            PessoaFisica pessoa = pessoas.get(0);
            if (registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(pessoa)) {
                throw new UsuarioWebProblemasCadastroException("Pessoa com Registro de Óbito.");
            } else if (!pessoaFacade.hasVinculoAtivo(pessoa)) {
                throw new UsuarioWebProblemasCadastroException("Servidor sem contrato ativo.");
            } else {
                PessoaFisicaDTO pessoaFisicaDTO = PessoaFisicaPortal.toPessoaFisicaDTO(pfPortal);
                if (!SituacaoPessoaPortal.AGUARDANDO_LIBERACAO.equals(pfPortal.getStatus())) {
                    pessoaFisicaDTO.setConfirmacaoCadastro(true);
                } else {
                    pessoaFisicaDTO.setConfirmacaoCadastro(false);
                }
                pfPortal.getPessoaFisica().getItemTempoContratoFPPessoa().size();
                if (!pessoas.isEmpty()) {
                    PessoaFisica dependenteAtivo = pessoas.get(0);
                    if (dependenteAtivo.getDependentesAtivos() != null && !dependenteAtivo.getDependentesAtivos().isEmpty()) {
                        pessoaFisicaDTO.setDependentes(Dependente.toDependentesDTO(dependenteAtivo.getDependentesAtivos()));
                    }
                } else {
                    pessoas = Lists.newArrayList(pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false));
                    pessoaFisicaDTO.setDependentes(Dependente.toDependentesDTO(pessoas.get(0).getDependentesAtivos()));
                }
                pessoaFisicaDTO.setNumeroContrato(getNumeroContrato(pfPortal.getPessoaFisica()));
                return pessoaFisicaDTO;
            }
        }
    }

    public PessoaFisicaDTO validarPessoaAtualizacao(PessoaFisica pessoa) throws UsuarioWebProblemasCadastroException {
        if (registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(pessoa)) {
            throw new UsuarioWebProblemasCadastroException("Pessoa com Registro de Óbito.");
        } else if (pessoaFacade.hasVinculoAtivo(pessoa)) {
            PessoaFisicaDTO pessoaFisicaDTO = pessoa.toPessoaFisicaDTO();
            pessoaFisicaDTO.setConfirmacaoCadastro(false);
            return pessoaFisicaDTO;
        } else {
            throw new UsuarioWebProblemasCadastroException("Servidor sem contrato ativo.");
        }
    }

    private String getNumeroContrato(PessoaFisica pessoa) {
        String sql = " select max(vinculo.numero) from vinculofp vinculo " +
            " inner join matriculafp mat on vinculo.matriculafp_id = mat.id " +
            " where mat.PESSOA_ID = :idPessoa ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", pessoa.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }
        return "";
    }

    public String alterarLoginPortalWeb(String cpfCnpj) {
        List<Pessoa> pessoas = pessoaFacade.listaTodasPessoasPorCPFCNPJ(cpfCnpj);
        String msg = "";
        if (pessoas == null || pessoas.isEmpty()) {
            msg = "Não encontramos seu CPF/CNPJ cadastrado em nossa base de dados, por favor, dirija-se a uma central de atendimento para regularizar seu cadastro.";
        } else if (pessoas.size() == 1 && pessoas.get(0).getUsuariosWeb() != null && !pessoas.get(0).getUsuariosWeb().isEmpty()) {
            msg = usuarioWebFacade.criarNovaSenhaeEnviarPorEmail(pessoas.get(0));
        } else if (pessoas.size() == 1 && (pessoas.get(0).getUsuariosWeb() == null || pessoas.get(0).getUsuariosWeb().isEmpty())) {
            msg = "Você ainda não tem o cadastro para a área do contribuinte, volte a página de login e acesse o link 'Primeiro Acesso'.";
        } else if (pessoas.size() > 1) {
            msg = "Encontramos uma irregularidade no cpf informado, por favor, dirija-se a uma central de atendimento para regularizar seu cadastro.";
        }
        return msg;
    }

    public ConsultaCPF verificarDisponibilidadeCPF(String cpf) {
        cpf = StringUtil.retornaApenasNumeros(cpf);
        Pessoa pessoa;
        String tipo;
        if (cpf.length() == 11) {
            tipo = "CPF";
            pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false);
        } else {
            tipo = "CNPJ";
            pessoa = pessoaFacade.buscarPessoaJuridicaPorCNPJ(cpf, false);
        }
        if (pessoa != null) {
            WSPessoa wsPessoa = new WSPessoa(pessoa);
            UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());
            if (usuarioWeb != null) {
                return new ConsultaCPF(false, "Já existe um cadastro com " + tipo + " informado.", wsPessoa);
            }
        }
        if (tipo.equals("CPF")) {
            if (pessoa != null) {
                WSPessoa wsPessoa = new WSPessoa(pessoa);
                return new ConsultaCPF(true, "O seu cadastrado já existe na base de dados da prefeitura, confirme algumas informações para continuar ", wsPessoa);
            }
            return new ConsultaCPF("O seu cadastrado ainda não existe na base de dados da prefeitura, informe alguns dados para continuar", true);
        } else {
            if (pessoa == null) {
                return new ConsultaCPF("O CNPJ: " + cpf + " não está registrado na base de dados. " +
                    "Por favor, faça o cadastro do CNPJ na REDESIM pela junta comercial. ", false);
            }
            WSPessoa wsPessoa = new WSPessoa(pessoa);
            return new ConsultaCPF(false, "O CNPJ: " + cpf + " já está registrado na base de dados. " +
                "Por favor, compareça a um CAC presencialmente, ou acesse o atendimento online. ",
                wsPessoa);
        }
    }

    public ConsultaCPF verificarDisponibilidadeCPFParaPrimeiroAcesso(String cpf) {
        cpf = StringUtil.retornaApenasNumeros(cpf);
        Pessoa pessoa;
        String tipo;
        if (cpf.length() == 11) {
            tipo = "CPF";
            pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false);
        } else {
            tipo = "CNPJ";
            pessoa = pessoaFacade.buscarPessoaJuridicaPorCNPJ(cpf, false);
        }
        if (pessoa != null) {
            WSPessoa wsPessoa = new WSPessoa(pessoa);
            UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());
            if (usuarioWeb != null) {
                return new ConsultaCPF(false, "Já existe um cadastro no portal com " + tipo + " informado.", wsPessoa);
            }
        } else {
            return new ConsultaCPF("O " + (tipo.equals("CPF") ? "CPF: " : "CNPJ: ") + cpf + " não está registrado na base de dados. " +
                "Por favor, compareça a um CAC presencialmente, ou acesse o atendimento online. ", false);
        }
        return new ConsultaCPF(null, true);
    }

    public ConsultaCPF buscarConsultaCPFValidoCredor(String cpf, String email) {
        cpf = StringUtil.retornaApenasNumeros(cpf);
        Pessoa pessoa;
        String tipo;
        if (cpf.length() == 11) {
            tipo = "CPF";
            pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false);
        } else {
            tipo = "CNPJ";
            pessoa = pessoaFacade.buscarPessoaJuridicaPorCNPJ(cpf, false);
        }

        if (solicitacaoCadastroPessoaFacade.hasSolicitacaoNaoConcluidaPorCPFAndEmail(cpf, email)) {
            return new ConsultaCPF(false, "Já existe uma Solicitação/Alteração de cadastro para o " + tipo + " informado, por favor verifique o e-mail <b>" + email + "</b> para mais informações." +
                "</br>Caso ainda tenha dúvidas, compareça a um CAC presencialmente, ou acesse o atendimento online.", null);
        }

        if (pessoa != null) {
            WSPessoa wsPessoa = new WSPessoa(pessoa);
            UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());

            if (usuarioWeb != null) {
                if (usuarioWeb.getEmail() != null && !usuarioWeb.getEmail().trim().isEmpty() && !usuarioWeb.getEmail().trim().equalsIgnoreCase(email.trim())) {
                    return new ConsultaCPF(false, "O email (<b>" + email + "</b>) informado é diferente do email já cadastrado <b>" + usuarioWeb.getEmail() + "</b>) para o " + tipo + " <b>" + pessoa.getCpf_Cnpj() + "</b>." +
                        "</br>Por favor, utilize o e-mail já cadastrado, caso não tenha mais acesso ao e-mail, compareça a um CAC presencialmente, ou acesse o atendimento online.", wsPessoa);
                }
            } else if (pessoa.getEmail() != null && !pessoa.getEmail().trim().isEmpty() && !pessoa.getEmail().trim().equalsIgnoreCase(email.trim())) {
                return new ConsultaCPF(false, "O e-mail informado (<b>" + email + "</b>) é diferente do e-mail já cadastrado (<b>" + pessoa.getEmail() + "</b>) para o " + tipo + " <b>" + pessoa.getCpf_Cnpj() + "</b>." +
                    "</br>Por favor, utilize o e-mail já cadastrado, caso não tenha mais acesso ao e-mail, compareça a um CAC presencialmente, ou acesse o atendimento online.", wsPessoa);
            }

            if (SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(pessoa.getSituacaoCadastralPessoa())) {
                return new ConsultaCPF(false, "Já existe uma Solicitação/Alteração de cadastro de credor em andamento para o " + tipo + " informado, por favor verifique o e-mail <b>" + pessoa.getEmail() + "</b> para mais informações." +
                    "</br>Caso ainda tenha dúvidas, compareça a um CAC presencialmente, ou acesse o atendimento online.", wsPessoa);
            }

            if (SituacaoCadastralPessoa.REJEITADO.equals(pessoa.getSituacaoCadastralPessoa())) {
                return new ConsultaCPF(false, "Solicitação/Alteração de cadastro de credor foi rejeitada, por favor verifique o e-mail <b>" + pessoa.getEmail() + "</b> para mais informações." +
                    "</br>Caso ainda tenha dúvidas, compareça a um CAC presencialmente, ou acesse o atendimento online.", wsPessoa);
            }
            return new ConsultaCPF(true, null, wsPessoa);
        }
        return new ConsultaCPF(null, true);
    }

    private boolean verificarOTTDoCnpj(String cnpj) {
        return operadoraTecnologiaTransporteFacade.verificarCnpjJaExistente(null, cnpj);
    }


    public void desabilitarLoginPortalWeb(String cpf) {
        UsuarioWeb usuarioWeb = usuarioWebFacade.recuperarUsuarioPorLogin(cpf);
        usuarioWeb.setActivated(false);
        usuarioWebFacade.salvar(usuarioWeb);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarEmail(String email, String corpoEmail, String assunto) {
        AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(assunto, 0),
            () -> {
                EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
                return null;
            });
    }

    public void atualizarDadosPessoaisPortal(WSPessoa pessoa) {
        atualizarEnderecoPessoa(pessoa);
        atualizarTelefonePessoa(pessoa);
    }

    private void atualizarTelefonePessoa(WSPessoa pessoa) {
        PessoaFisica pf = (PessoaFisica) recuperar(pessoa.getId());
        List<Telefone> listaPortal = criarListaDoPortal(pf, pessoa.getTelefones());
        if (!listaPortal.isEmpty()) {
            pf.getTelefones().clear();
            pf.getTelefones().addAll(listaPortal);
            em.merge(pf);
        }
    }

    private List<Telefone> criarListaDoPortal(PessoaFisica pf, List<WSTelefoneDaPessoa> telefones) {
        List<Telefone> tels = Lists.newLinkedList();
        for (WSTelefoneDaPessoa telefone : telefones) {
            Telefone tel = new Telefone(telefone.getId(), pf, Util.substituiCaracterEspecial(telefone.getNumero(), ""), telefone.getTipo());
            tels.add(tel);
        }
        return tels;
    }

    private void atualizarEnderecoPessoa(WSPessoa pessoa) {
        PessoaFisica pf = (PessoaFisica) recuperar(pessoa.getId());
        for (EnderecoCorreio enderecoCorreio : pf.getEnderecos()) {
            for (WSEnderecoDaPessoa wsEnderecoDaPessoa : pessoa.getEnderecos()) {
                if (wsEnderecoDaPessoa.getId().equals(enderecoCorreio.getId())) {
                    WSEnderecoDaPessoa.copyEnderecoPortalToEnderecoCorreio(enderecoCorreio, wsEnderecoDaPessoa);
                }
            }
        }
    }

    public List<WSProcesso> buscarProtocolosPorPessoaPortalWeb(String cpf, Integer inicio, Integer max) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return buscarProtocolosPorPessoaPortalWeb(pessoa, inicio, max);
    }

    public List<WSProcesso> buscarProtocolosPorPessoaPortalWeb(Pessoa pessoa, Integer inicio, Integer max) {
        Query q = em.createQuery("select p from Processo p where p.pessoa = :pessoa order by p.id");
        q.setParameter("pessoa", pessoa);
        if (inicio != null && max != null) {
            q.setFirstResult(inicio);
            q.setMaxResults(max);
        }
        List<WSProcesso> processos = Lists.newArrayList();
        for (Processo processo : (List<Processo>) q.getResultList()) {
            processos.add(new WSProcesso(processo));
        }
        return processos;
    }

    public Integer contarProtocolosPorPessoaPortalWeb(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);

        Query q = em.createQuery("select count(p.id) from Processo p where p.pessoa = :pessoa");
        q.setParameter("pessoa", pessoa);
        return ((Long) q.getSingleResult()).intValue();
    }

    private List<SituacaoParcela> buscarSituacoesParaConsultaDebitosPortal() {
        List<SituacaoParcela> situacoes = Lists.newArrayList();
        situacoes.add(SituacaoParcela.EM_ABERTO);
        return situacoes;
    }

    public Integer contarDebitosPortalWeb(FiltroConsultaDebitos filtro) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(filtro.getCpf());
        if (pessoa != null) {
            ConsultaParcela consultaParcela = carregarConsultaParcelaPorFiltro(pessoa, filtro);
            consultaParcela.executaCount();
            return consultaParcela.getTotalRegistros();
        }
        return 0;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ResultadoParcela> buscarDebitosPortalWeb(FiltroConsultaDebitos filtro) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(filtro.getCpf());
        return pessoa == null ? Lists.newArrayList() : buscarDebitosPortalWeb(pessoa, filtro);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    private List<ResultadoParcela> buscarDebitosPortalWeb(Pessoa pessoa, FiltroConsultaDebitos filtro) {
        ConsultaParcela consultaParcela = carregarConsultaParcelaPorFiltro(pessoa, filtro);
        if (filtro.getInicio() != null && filtro.getMax() != null) {
            consultaParcela.setFirtResult(filtro.getInicio());
            consultaParcela.setMaxResult(filtro.getMax());
        }
        definirOrdem(consultaParcela);
        return consultaParcela.executaConsulta().getResultados();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ResultadoParcela> buscarDebitosPortalWebPorCadastro(FiltroConsultaDebitos filtro) {
        ConsultaParcela consultaParcela = carregarConsultaParcelaPorFiltroCadastros(filtro);
        definirOrdem(consultaParcela);
        return consultaParcela.executaConsulta().getResultados();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ResultadoParcela> buscarDebitosPortalWebPorPessoa(FiltroConsultaDebitos filtro) {
        try {
            Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(filtro.getCpf());
            ConsultaParcela consultaParcela = carregarConsultaParcelaPorFiltroPessoa(pessoa.getId(), filtro);
            definirOrdem(consultaParcela);
            return consultaParcela.executaConsulta().getResultados();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Lists.newArrayList();
    }

    private ConsultaParcela carregarConsultaParcelaPorFiltro(Pessoa pessoa, FiltroConsultaDebitos filtro) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, buscarSituacoesParaConsultaDebitosPortal());

        if (TipoCadastroTributario.IMOBILIARIO.name().equals(filtro.getTipoConsulta())) {
            consultaParcela.addParameter(ConsultaParcela.Campo.BCI_ID, ConsultaParcela.Operador.IS_NOT_NULL, null);
            consultaParcela.addComplementoDoWhere(" and vw.cadastro_id in (select prop.imovel_id " +
                " from propriedade prop " +
                " where (prop.finalvigencia is null or trunc (prop.finalvigencia) >= trunc(sysdate)) and prop.pessoa_id = " + pessoa.getId() + ") ");
        }
        if (TipoCadastroTributario.PESSOA.name().equals(filtro.getTipoConsulta())) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, pessoa.getId());
        }
        if (TipoCadastroTributario.ECONOMICO.name().equals(filtro.getTipoConsulta())) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CMC_ID, ConsultaParcela.Operador.IS_NOT_NULL, null);
            consultaParcela.addComplementoDoWhere(" and cmc.id in (select cadEc.ID " +
                " from sociedadecadastroeconomico sce " +
                " inner join cadastroeconomico cadEc on sce.cadastroEconomico_id = cadEc.id " +
                " where (sce.datafim is null or trunc (sce.datafim) >= trunc(sysdate)) and (sce.socio_id = " + pessoa.getId() +
                " or cadEc.PESSOA_ID = " + pessoa.getId() + " )) ");
        }
        if (filtro.getIdCadastro() != null && !TipoCadastroTributario.PESSOA.name().equals(filtro.getTipoConsulta())) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, filtro.getIdCadastro());
        }
        adicionarParametrosPorFiltro(consultaParcela, filtro);
        return consultaParcela;
    }

    private void adicionarParametrosPorFiltro(ConsultaParcela consultaParcela, FiltroConsultaDebitos filtro) {
        if (filtro.getIdDivida() != null && filtro.getIdDivida() > 0) {
            consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtro.getIdDivida());
        }
        if (filtro.getExercicioInicial() != null && filtro.getExercicioInicial() > 0) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtro.getExercicioInicial());
        } else {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR, 2003); // TODO Criar configuração depois de validado
        }
        if (filtro.getExercicioFinal() != null && filtro.getExercicioFinal() > 0) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtro.getExercicioFinal());
        }
        if (filtro.getVencimentoInicial() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, filtro.getVencimentoInicial());
        }
        if (filtro.getVencimentoFinal() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, filtro.getVencimentoFinal());
        }
    }

    private ConsultaParcela carregarConsultaParcelaPorFiltroCadastros(FiltroConsultaDebitos filtro) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, buscarSituacoesParaConsultaDebitosPortal());
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, filtro.getIdsCadastros());
        adicionarParametrosPorFiltro(consultaParcela, filtro);
        return consultaParcela;
    }

    private ConsultaParcela carregarConsultaParcelaPorFiltroPessoa(Long idPessoa, FiltroConsultaDebitos filtro) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, buscarSituacoesParaConsultaDebitosPortal());
        consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, idPessoa);
        adicionarParametrosPorFiltro(consultaParcela, filtro);
        consultaParcela.addComplementoDoWhere(" and vw.cadastro_id is null ");
        return consultaParcela;
    }

    private void definirOrdem(ConsultaParcela consultaParcela) {
        consultaParcela.getOrdenacao().clear();
        adicionarOrdemPrioritaria(consultaParcela);
        adicionarOrdemPadrao(consultaParcela);
    }

    private void adicionarOrdemPrioritaria(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.CMC_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCI_INSCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.BCR_CODIGO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.ORDEM_APRESENTACAO, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    private void adicionarOrdemPadrao(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.SD, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Ordem.TipoOrdem.DESC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.QUANTIDADE_PARCELA_VALOR_DIVIDA, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    public ByteArrayOutputStream gerarEmitirDAMPortalWeb(List<ResultadoParcela> parcelas) {
        List<DAM> dams = Lists.newArrayList();
        try {
            for (ResultadoParcela resultado : parcelas) {
                DAM dam = null;
                Date vencimentoDam = resultado.getVencimento();
                if (!resultado.getVencido()) {
                    dam = consultaDebitoFacade.getDamFacade().buscarPrimeiroDamParcela(resultado.getIdParcela());
                } else {
                    vencimentoDam = DataUtil.ultimoDiaMes(new Date()).getTime();
                }
                if (dam == null || dam.getValorTotal().compareTo(resultado.getValorTotal()) != 0) {
                    ParcelaValorDivida parcela = consultaDebitoFacade.recuperaParcela(resultado.getIdParcela());
                    dam = consultaDebitoFacade.getDamFacade().geraDAM(parcela, vencimentoDam);
                    em.flush();
                }
                dams.add(dam);
            }
            return consultaDebitoFacade.gerarImpressaoDAMPortal(dams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DAM> gerarDAMPortalWeb(List<ResultadoParcela> parcelas) {
        List<DAM> dams = Lists.newArrayList();
        try {
            for (ResultadoParcela parcela : parcelas) {
                DAM dam = recuperarDAMPeloIdParcela(parcela.getIdParcela());
                dam = dam != null ? dam : damFacade.buscarOuGerarDam(parcela);
                dams.add(dam);
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar gerar DAM. " + e);
        }
        return dams;
    }

    public DAM gerarDAMAgrupadoPortalWeb(List<ResultadoParcela> parcelas) {
        LocalDate localDate = LocalDate.now();
        Calendar c = DataUtil.ultimoDiaUtil(DataUtil.ultimoDiaMes(localDate.toDate()), feriadoFacade);
        return damFacade.gerarDamAgrupado(parcelas, c.getTime());
    }

    public List<DAM> gerarDAMPixPortal(List<ParcelaPixDTO> parcelasPix) {
        List<DAM> dans = Lists.newArrayList();
        try {
            for (ParcelaPixDTO parcelaPix : parcelasPix) {
                ResultadoParcela parcela = montarParcela(parcelaPix);
                DAM dam = recuperarDAMPeloIdParcela(parcela.getIdParcela());
                dam = dam != null ? dam : damFacade.buscarOuGerarDam(parcela);
                if (dam != null && dam.getId() != null) {
                    dans.add(dam);
                    parcelaPix.setIdDam(dam.getId());
                    parcelaPix.setVencimento(dam.getVencimento());
                    if (dam.getPix() != null) {
                        parcelaPix.setQrCodePix(dam.getPix().getQrCode());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar gerar DAM. " + e);
        }
        return dans;
    }

    private ResultadoParcela montarParcela(ParcelaPixDTO parcelaPix) {
        ResultadoParcela parcela = new ResultadoParcela();

        parcela.setIdParcela(parcelaPix.getIdParcela());
        parcela.setIdCadastro(parcelaPix.getIdCadastro());
        parcela.setIdPessoa(parcelaPix.getIdPessoa());
        parcela.setIdDivida(parcelaPix.getIdDivida());
        parcela.setOrdemApresentacao(parcelaPix.getOrdemApresentacao());
        parcela.setVencimento(parcelaPix.getVencimento());
        parcela.setTipoCalculo(parcelaPix.getTipoCalculo());

        return parcela;
    }

    public List<DAM> recuperarDamsPeloIdParcela(List<Long> parcelas) {
        List<DAM> dams = Lists.newArrayList();
        for (Long parcela : parcelas) {
            DAM dam = damFacade.recuperaDAMPeloIdParcela(parcela);
            if (dam != null) {
                dams.add(dam);
            }
        }
        return dams;
    }

    public ByteArrayOutputStream gerarCarneIPTUPortalWeb(Integer ano, String inscricaoCadastral) throws IOException {
        List<ImpressaoCarneIPTU> carnes = impressaoCarneIPTUFacade.buscarCarnesIPTU(ano, inscricaoCadastral, inscricaoCadastral);
        carnes = removerParcelasVencidas(carnes);
        if (!carnes.isEmpty()) {
            impressaoCarneIPTUFacade.verificarEGerarDamCarnesIptu(carnes, HistoricoImpressaoDAM.TipoImpressao.PORTAL);
            return impressaoCarneIPTUFacade.gerarImpressaoIPTUPortal(carnes, ano, inscricaoCadastral);
        }
        return new ByteArrayOutputStream(0);
    }

    public List<ImpressaoCarneIPTU> removerParcelasVencidas(List<ImpressaoCarneIPTU> carnes) {
        List<ImpressaoCarneIPTU> carnesNaoVencidos = Lists.newArrayList();
        for (ImpressaoCarneIPTU carne : carnes) {
            if (carne.getVencimento().compareTo(new Date()) >= 0 && Strings.isNullOrEmpty(carne.getNumeroDAM())) {
                carnesNaoVencidos.add(carne);
            }
        }
        carnes.removeAll(carnesNaoVencidos);
        return carnes;
    }

    public ByteArrayOutputStream gerarImpressaoDemonstrativo(List<ResultadoParcela> parcelas) {
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getIdPessoa() != null) {
                Pessoa pessoa = recuperar(parcela.getIdPessoa());
                pessoa.getEnderecos().size();
                return gerarImpressaoDemonstrativo(pessoa, parcelas);
            }
        }
        return null;
    }

    public ByteArrayOutputStream gerarImpressaoDemonstrativo(Pessoa
                                                                 pessoa, List<ResultadoParcela> resultadoParcelas) {
        EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        Map<String, Map<String, Object>> jaspers = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put("BRASAO", img);
        parametros.put("USUARIO", SistemaFacade.obtemLogin());
        parametros.put("PESSOA", pessoa.getNome());
        parametros.put("CPF_CNPJ", pessoa.getCpf_Cnpj());
        if (enderecoCorrespondencia != null) {
            parametros.put("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
        }

        for (ResultadoParcela resultado : resultadoParcelas) {
            resultado.setSomaNoDemonstrativo(true);
        }

        Map<ConsultaDebitoControlador.CadastroConsulta, List<ResultadoParcela>> mapa = montarMapa(resultadoParcelas);
        resultadoParcelas = Lists.newLinkedList();
        for (ConsultaDebitoControlador.CadastroConsulta cadastroConsulta : mapa.keySet()) {
            for (ResultadoParcela resultadoParcela : mapa.get(cadastroConsulta)) {
                resultadoParcela.setDebitoProtestado(consultaDebitoFacade.buscarProcessoAtivoDaParcela(
                    resultadoParcela.getIdParcela()) == null ? Boolean.FALSE : Boolean.TRUE);
                resultadoParcelas.add(resultadoParcela);
            }
        }

        parametros.put("FILTROS", "Pessoa: " + pessoa.getNome() + " - " + "Situação da Parcela: " + SituacaoParcela.EM_ABERTO.getDescricao());
        parametros.put("TOTALPORSITUACAO", consultaDebitoFacade.calculaTotalPorSituacao(resultadoParcelas));
        parametros.put("SUBREPORT_DIR", subReport);
        jaspers.put(subReport + "RelatorioConsultaDebitos.jasper", parametros);
        try {
            List jpList = new ArrayList();
            for (String arquivo : jaspers.keySet()) {
                jpList.add(JasperFillManager.fillReport(arquivo, jaspers.get(arquivo), new JRBeanCollectionDataSource(resultadoParcelas)));
            }
            ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
            exporter.exportReport();
            return dadosByte;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Map<ConsultaDebitoControlador.CadastroConsulta, List<ResultadoParcela>> montarMapa
        (List<ResultadoParcela> parcelas) {
        Map<ConsultaDebitoControlador.CadastroConsulta, List<ResultadoParcela>> mapa = new HashMap<>();
        if (parcelas != null) {
            for (ResultadoParcela resultado : parcelas) {
                ConsultaDebitoControlador.CadastroConsulta cadastro = new ConsultaDebitoControlador.CadastroConsulta(resultado.getIdCadastro(), resultado.getCadastro(), TipoCadastroTributario.valueOf(resultado.getTipoCadastro()), "", null);

                if (!mapa.containsKey(cadastro)) {
                    mapa.put(cadastro, new ArrayList<ResultadoParcela>());
                }
                mapa.get(cadastro).add(resultado);
            }
        }
        return mapa;
    }

    public List<WSSolicitacaoDocumentoOficial> buscarSolicitacoesPortalWeb(String cpfCnpj, int firstResult,
                                                                           int maxResult) {
        List<WSSolicitacaoDocumentoOficial> toReturn = Lists.newArrayList();
        Pessoa pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(cpfCnpj);
        String sql = "select * from (" +
            " select distinct sdo.* " +
            "    from solicitacaodoctooficial sdo " +
            "   left join cadastroeconomico ce on ce.id = sdo.cadastroeconomico_id " +
            "   left join sociedadecadastroeconomico sce on sce.cadastroeconomico_id = ce.id " +
            "   left join cadastroimobiliario ci on ci.id = sdo.cadastroimobiliario_id " +
            "   left join propriedade prop on prop.imovel_id = ci.id " +
            " where sdo.pessoafisica_id =   :idPessoa" +
            "    or sdo.pessoajuridica_id = :idPessoa" +
            "    or ce.pessoa_id =          :idPessoa" +
            "    or sce.socio_id =          :idPessoa" +
            "    or prop.pessoa_id =        :idPessoa)" +
            " order by datasolicitacao desc, coalesce(cadastroimobiliario_id, cadastroeconomico_id, pessoafisica_id, pessoajuridica_id) desc";
        Query q = em.createNativeQuery(sql, SolicitacaoDoctoOficial.class);
        q.setParameter("idPessoa", pessoa.getId());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResult);
        List resultList = q.getResultList();
        for (SolicitacaoDoctoOficial sdo : (List<SolicitacaoDoctoOficial>) resultList) {
            toReturn.add(new WSSolicitacaoDocumentoOficial(sdo));
        }
        return toReturn;
    }

    public Integer buscarQuantidadeSolicitacoesPortalWeb(String cpfCnpj) {
        Pessoa pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(cpfCnpj);
        String sql = "select count(distinct sdo.id)  " +
            "   from solicitacaodoctooficial sdo " +
            "  left join cadastroeconomico ce on ce.id = sdo.cadastroeconomico_id " +
            "  left join sociedadecadastroeconomico sce on sce.cadastroeconomico_id = ce.id " +
            "  left join cadastroimobiliario ci on ci.id = sdo.cadastroimobiliario_id " +
            "  left join propriedade prop on prop.imovel_id = ci.id " +
            "where sdo.pessoafisica_id =   :idPessoa " +
            "   or sdo.pessoajuridica_id = :idPessoa " +
            "   or ce.pessoa_id =          :idPessoa " +
            "   or sce.socio_id =          :idPessoa " +
            "   or prop.pessoa_id =        :idPessoa";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", pessoa.getId());
        return !q.getResultList().isEmpty() ? ((BigDecimal) q.getSingleResult()).intValue() : 0;
    }

    public String gerarConteudoDocumentoOficial(WSSolicitacaoDocumentoOficial wsSolicitacao) throws Exception {
        SolicitacaoDoctoOficial sol = em.find(SolicitacaoDoctoOficial.class, wsSolicitacao.getId());
        return documentoOficialFacade.geraDocumentoSolicitacaoPortalWeb(sol);
    }

    public WsValidacaoSolicitacaoDoctoOficial gerarSolicitacaoParaPessoa(String cpf, Long tipo) throws
        ExcecaoNegocioGenerica {
        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        Pessoa pessoa = null;
        try {
            pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        } catch (Exception e) {
            validacao.setMensagem("Não foi possivel recuperar a pessoa");
        }
        if (pessoa == null) {
            validacao.setMensagem("Não foi possivel recuperar a pessoa");
        }
        TipoDoctoOficial tipoDoctoOficial = solicitacaoDoctoOficialFacade.getTipoDoctoOficialFacade().recuperar(tipo);
        SolicitacaoDoctoOficial solicitacao = new SolicitacaoDoctoOficial();
        if (!solicitacaoDoctoOficialFacade.existeCertidaoVigente(tipo, pessoa instanceof PessoaFisica ? TipoCadastroDoctoOficial.PESSOAFISICA : TipoCadastroDoctoOficial.PESSOAJURIDICA, pessoa.getId())) {
            boolean permiteSolicitarDocumento = false;
            try {
                if (pessoa instanceof PessoaFisica) {
                    solicitacao.setPessoaFisica((PessoaFisica) pessoa);
                } else {
                    solicitacao.setPessoaJuridica((PessoaJuridica) pessoa);
                }
                solicitacao.setDataSolicitacao(new Date());
                solicitacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoDoctoOficial.class, "codigo"));
                solicitacao.setTipoDoctoOficial(tipoDoctoOficial);
                solicitacao.setDocumentoOficial(documentoOficialFacade.geraDocumentoEmBranco(null, pessoa, tipoDoctoOficial));
                permiteSolicitarDocumento = solicitacaoDoctoOficialFacade.permitirSolicitarDocumento(solicitacao).getValido();
                if (permiteSolicitarDocumento) {
                    solicitacao = em.merge(solicitacao);
                    solicitacaoDoctoOficialFacade.calculaValorCertidao(solicitacao);
                    validacao.setSolicitou(true);
                }
            } catch (Exception e) {
                validacao.setMensagem("Erro ao gerar a certidão");
            }
            if (!permiteSolicitarDocumento) {
                validacao.setMensagem("O contribuinte " +
                    (TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "não" : "") +
                    " possui débitos em aberto" +
                    (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "/suspensos" : "") +
                    ". Não é possível fazer a geração da " + tipoDoctoOficial.getDescricao() + "!");
            }
        } else {
            validacao.setMensagem("Já existe uma solicitação de " + tipoDoctoOficial.getDescricao() + " vigente!");
        }
        return validacao;
    }

    public List<VeiculoOttDTO> buscarVeiculosOTT(String cnpj, Integer inicio, Integer max) throws
        UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException, UsuarioWebSenhaInvalidaException {
        List<VeiculoOttDTO> retorno = Lists.newArrayList();
        for (VeiculoOperadoraTecnologiaTransporte veiculo : veiculoOperadoraTecnologiaTransporteFacade.buscarVeiculosOttPortal(cnpj, inicio, max)) {
            retorno.add(veiculo.toDTO());
        }
        return retorno;
    }

    public Integer contarVeiculosOTT(String cnpj) throws UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException, UsuarioWebSenhaInvalidaException {
        return veiculoOperadoraTecnologiaTransporteFacade.contarVeiculosOttPortal(cnpj);
    }

    public List<CondutorOttDTO> buscarCondutoresOTT(String cnpj, Integer inicio, Integer max) throws
        UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException, UsuarioWebSenhaInvalidaException {
        List<CondutorOttDTO> retorno = Lists.newArrayList();
        for (CondutorOperadoraTecnologiaTransporte condutor : condutorOperadoraTecnologiaTransporteFacade.buscarCondutoresOttPortal(cnpj, inicio, max)) {
            retorno.add(condutor.toDTO());
        }
        return retorno;
    }

    public Integer contarCondutoresOTT(String cnpj) throws UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException, UsuarioWebSenhaInvalidaException {
        return condutorOperadoraTecnologiaTransporteFacade.contarCondutoresOttPortal(cnpj);
    }

    public List<WSEmpresa> buscarCadastrosEconomicos(String cpf, Integer indexInicio, Integer maxResult) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return buscarCadastrosEconomicos(pessoa, indexInicio, maxResult);
    }

    public List<WSEmpresa> buscarCadastrosEconomicos(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return buscarCadastrosEconomicos(pessoa);
    }

    public List<WSEmpresa> buscarCadastrosEconomicos(Pessoa pessoa, Integer inicio, Integer max) {
        List<WSEmpresa> retorno = Lists.newArrayList();
        for (CadastroEconomico cadastroEconomico : cadastroEconomicoFacade.recuperarCadastrosDaPessoa(pessoa, inicio, max)) {
            retorno.add(new WSEmpresa(cadastroEconomico));
        }
        return retorno;
    }

    public List<WSEmpresa> buscarCadastrosEconomicos(Pessoa pessoa) {
        List<WSEmpresa> retorno = Lists.newArrayList();
        for (CadastroEconomico cadastroEconomico : cadastroEconomicoFacade.recuperarCadastrosSomenteDaPessoa(pessoa)) {
            retorno.add(new WSEmpresa(cadastroEconomico));
        }
        return retorno;
    }

    public List<WSAlvara> buscarAlvarasPorCmc(String cmc) {
        List<Alvara> alvaras = alvaraFacade.buscarAlvarasPorInscricaoCadastral(cmc, false);
        List<WSAlvara> retorno = Lists.newArrayList();
        if (alvaras != null && !alvaras.isEmpty()) {
            for (Alvara alvara : alvaras) {
                SituacaoParcela situacaoParcelaAlvara = recuperarSituacaoParcelaOriginalAlvara(alvara);
                String descricaoSituacao;
                if (situacaoParcelaAlvara == null) {
                    descricaoSituacao = "Isento";
                } else {
                    descricaoSituacao = situacaoParcelaAlvara.getDescricao();
                }

                VOAlvara voAlvara = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(alvara.getId());
                ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
                calculoAlvaraFacade.definirPermissaoImpressao(voAlvara, configuracaoTributario, null, null);
                retorno.add(new WSAlvara(alvara, descricaoSituacao, voAlvara.getEmitir()));
            }
        }
        return retorno;
    }

    public ByteArrayOutputStream gerarRelatorioAlvaraPortal(String cpf, Long idAlvara) {
        WSAlvara wsAlvara = new WSAlvara(em.find(Alvara.class, idAlvara), "", false);
        WSPessoa wsPessoa = new WSPessoa(pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf));
        return gerarRelatorioAlvaraPortal(wsAlvara, wsPessoa);
    }

    public ByteArrayOutputStream gerarRelatorioAlvaraPortal(WSAlvara wsAlvara, WSPessoa pessoa) {
        try {
            Alvara alvara = em.find(Alvara.class, wsAlvara.getId());
            VOAlvara voAlvara = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(alvara.getId());
            calculoAlvaraFacade.definirPermissaoImpressao(voAlvara, configuracaoTributarioFacade.retornaUltimo(), null, null);

            if (voAlvara.getEmitir()) {
                if (alvara.getEmissao() == null) {
                    alvara.setEmissao(new Date());
                }
                if (alvara.getAssinaturadigital() == null || calculoAlvaraFacade.SEM_ASSINATURA.equals(alvara.getAssinaturadigital())) {
                    alvara.setAssinaturadigital(calculoAlvaraFacade.geraAssinaturaDigital(alvara));
                }
                em.merge(alvara);
                return gerarBytesRelatorioAlvaraPortal(wsAlvara, pessoa);
            } else {
                List<ResultadoParcela> resultados = Lists.newArrayList();
                for (VOAlvaraItens item : voAlvara.getItens()) {
                    ConsultaParcela consultaParcela = new ConsultaParcela();
                    consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, item.getId());
                    consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
                    resultados.addAll(consultaParcela.executaConsulta().getResultados());
                }

                if (!resultados.isEmpty()) {
                    return gerarEmitirDAMPortalWeb(resultados);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar byteArray do alvará pelo portal", e);
        }
        return null;
    }

    private ByteArrayOutputStream gerarBytesRelatorioAlvaraPortal(WSAlvara alvara, WSPessoa pessoa) {
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try {
            adicionarReciboImpressaoAlvaraPortal(alvara, pessoa);
            bytes = alvaraFacade.imprimirAlvaraPortal(alvara.getId(), TipoAlvara.tipoAlvaraPorDescricao(alvara.getTipo()), subReport, img);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return bytes;
    }

    public Alvara adicionarReciboImpressaoAlvaraPortal(WSAlvara alvara, WSPessoa pessoa) {
        Alvara alvaraRecuperado = alvaraFacade.recuperar(alvara.getId());

        ReciboImpressaoAlvara recibo = new ReciboImpressaoAlvara();
        recibo.setNomeResposavel(pessoa.getNome());
        recibo.setCpfResposavel(pessoa.getCpfCnpj());
        recibo.setMotivo("ALVARÁ IMPRESSO ATRAVÉS DO PORTAL DO CONTRIBUINTE");

        recibo.setDataImpressao(new Date());
        recibo.setNomeResposavel(recibo.getNomeResposavel().toUpperCase());
        recibo.setSequencia(singletonGeradorCodigo.getProximoCodigo(ReciboImpressaoAlvara.class, "sequencia"));
        recibo.setAlvara(alvaraRecuperado);
        alvaraRecuperado.getRecibosImpressaoAlvara().add(recibo);
        em.flush();
        return alvaraRecuperado;
    }

    public List<WSItbi> buscarCalculosItbi(String cpf, Integer inicio, Integer max) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return buscarCalculosItbi(pessoa, inicio, max);
    }

    public Integer contarCalculosItbi(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return calculoITBIFacade.contarCalculosComLaudosAssinados(pessoa);
    }

    private List<WSItbi> buscarCalculosItbi(Pessoa pessoa, Integer inicio, Integer max) {
        return calculoITBIFacade.montarWsItbiAssinado(pessoa, inicio, max);
    }

    public List<WSImovel> buscarCadastrosImobiliariosParaPesquisa(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        List<WSImovel> retorno = Lists.newArrayList();
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoaParaPesquisa(pessoa);
        for (CadastroImobiliario cadastroImobiliario : cadastros) {
            retorno.add(new WSImovel(cadastroImobiliario.getId(), cadastroImobiliario.getInscricaoCadastral()));
        }
        return retorno;
    }

    public List<WSImovel> buscarCadastrosImobiliarios(String cpf, Integer indexInicio, Integer maxResult) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return buscarImoveisPorPessoa(pessoa, indexInicio, maxResult);
    }

    public List<WSImovel> buscarCadastroImobiliarioAtivoPorPessoaAndInscricaoCadastral(String cpfCnpj, String inscricaoCadastral) {
        List<WSImovel> retorno = Lists.newArrayList();
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.buscarCadastroImobiliarioAtivoPorInscricaoCadastralAndCpfCnpj(inscricaoCadastral, cpfCnpj);
        for (CadastroImobiliario cadastroImobiliario : cadastros) {
            retorno.add(new WSImovel(cadastroImobiliario));
        }
        return retorno;
    }

    private List<WSImovel> buscarImoveisPorPessoa(Pessoa pessoa, Integer indexInicio, Integer maxResult) {
        List<WSImovel> retorno = Lists.newArrayList();
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoa(pessoa, indexInicio, maxResult);
        cadastroImobiliarioFacade.atualizaValoresDosCadastros(cadastros);
        for (CadastroImobiliario cadastroImobiliario : cadastros) {
            retorno.add(new WSImovel(cadastroImobiliario));
        }
        return retorno;
    }

    public Integer contarImoveisPorPessoa(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return cadastroImobiliarioFacade.contarCadastrosAtivosDaPessoa(pessoa);
    }

    public Integer contarEmpresasPorPessoa(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(cpf);
        return cadastroEconomicoFacade.contarCadastrosDaPessoa(pessoa);
    }


    public ByteArrayOutputStream imprimirLaudoItbi(Long idProcesso) {
        List<Long> damsParcela = Lists.newArrayList();
        ProcessoCalculoITBI processo = calculoITBIFacade.recuperar(idProcesso);

        List<Long> idsCalculo = Lists.newArrayList();
        for (CalculoITBI calculo : processo.getCalculos()) {
            idsCalculo.add(calculo.getId());
        }
        List<ResultadoParcela> parcelasNaoPagas = Lists.newArrayList();

        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculo);

        List<ResultadoParcela> resultados = consultaParcela.executaConsulta().getResultados();
        for (ResultadoParcela parcela : resultados) {
            DAM dam = damFacade.recuperaUltimoDamParcela(parcela.getIdParcela());
            damsParcela.add(dam.getId());
            if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                parcelasNaoPagas.add(parcela);
            }
        }
        if (!parcelasNaoPagas.isEmpty()) {
            return gerarEmitirDAMPortalWeb(parcelasNaoPagas);
        }
        return calculoITBIFacade.imprimirLaudo(processo.getId(), damsParcela);
    }

    public byte[] gerarRelatorioCadastroImobiliario(String inscricao) {
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin(), false);
        dto.setNomeParametroBrasao("BRASAO_RIO_BRANCO");
        dto.adicionarParametro("complementoQuery", " where ci.inscricaocadastral = '" + inscricao + "'");
        dto.adicionarParametro("incluirJoinPessoa", Boolean.FALSE);
        dto.setNomeRelatorio("Relatório de Cadastro Imobiliário");
        dto.setApi("tributario/cadastro-imobiliario/sincrono/");
        String urlWebreport = configuracaoDeRelatorioFacade.getConfiguracaoPorChave().getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<byte[]> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, byte[].class);
        return exchange.getBody();
    }

    public List<WSNFSAvulsa> buscarNotasPortal(String cpf, int inicio, int fim) {
        Pessoa pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false);
        return buscarNotasPortal(inicio, fim, pessoa);
    }

    public GeraAliquotaNotaAvulsaDTO buscarInformacoesAliquota(String cpf) {
        return nfsAvulsaFacade.definirAliquotaISS(cpf);
    }

    private List<WSNFSAvulsa> buscarNotasPortal(int inicio, int fim, Pessoa pessoa) {
        List<WSNFSAvulsa> retorno = Lists.newArrayList();
        List<NFSAvulsa> notas = nfsAvulsaFacade.buscarNotasDaPessoa(pessoa, inicio, fim);
        for (NFSAvulsa nota : notas) {
            retorno.add(new WSNFSAvulsa(nota));
        }
        return retorno;
    }

    public WSNFSAvulsa buscarNotasPortal(Long id) {
        return new WSNFSAvulsa(nfsAvulsaFacade.recuperar(id));
    }

    public Integer contarNotasPortal(String cpf) {
        Pessoa pessoa = pessoaFacade.buscarPessoaFisicaPorCPF(cpf, false);
        return nfsAvulsaFacade.contarNotasDaPessoa(pessoa);

    }

    public ByteArrayOutputStream gerarRelatorioCadastroEconomico(String cmc) throws IOException {
        AssistenteRelatorioBCE assistente = new AssistenteRelatorioBCE();
        assistente.setTipoRelatorio(AssistenteRelatorioBCE.RELATORIO_COMPLETO);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();

        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperaPorInscricao(cmc);

        assistente.setWhere(new StringBuilder());
        if (cadastroEconomico != null) {
            assistente.getWhere().append(" and ce.inscricaocadastral = '").append(cadastroEconomico.getInscricaoCadastral()).append("' ");
            assistente.setOrdemSql(" order by ce.inscricaocadastral ");
        } else {
            relatorioCadastroEconomicoFacade.montarCondicao(assistente);
        }
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeRelatorio("Boletim De Cadastro Mobiliário - BCM");
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", "Emitido Pelo Portal do Contribuinte");
        dto.adicionarParametro("LOGIN", "Emitido Pelo Portal do Contribuinte");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        dto.adicionarParametro("NOMERELATORIO", "BOLETIM DE CADASTRO MOBILIÁRIO - BCM");
        dto.adicionarParametro("URL_PORTAL", relatorioCadastroEconomicoFacade.atribuirUrlPortal());
        dto.adicionarParametro("TIPO_RELATORIO", assistente.getTipoRelatorio().equals(0));
        dto.adicionarParametro("FILTROS", "Inscrição Cadastral : " + cmc);
        dto.adicionarParametro("WHERE", assistente.getWhere());
        dto.adicionarParametro("ORDERBY", assistente.getOrdemSql());
        dto.setApi("tributario/boletim-cadastro-mobiliario/");
        dto.setUrlWebpublico(configuracao.getUrlWebpublico());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<byte[]> responseEntity =
            new RestTemplate().exchange(configuracao.getUrlCompleta(dto.getApi() + "gerar-sincrono"), HttpMethod.POST, request, byte[].class);

        byte[] bytes = responseEntity.getBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.write(bytes);
        return baos;
    }

    public List<WSTipoDocumentoOficial> buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial tipo) {
        List<TipoDoctoOficial> tipos = em.createQuery("select t from TipoDoctoOficial  t where t.tipoCadastroDoctoOficial = :tipo and t.disponivelSolicitacaoWeb = true")
            .setParameter("tipo", tipo).getResultList();
        List<WSTipoDocumentoOficial> retorno = Lists.newArrayList();
        for (TipoDoctoOficial tipoDoctoOficial : tipos) {
            retorno.add(new WSTipoDocumentoOficial(tipoDoctoOficial));
        }
        return retorno;
    }

    public WsValidacaoSolicitacaoDoctoOficial gerarSolicitacaoParaCadastroImobiliario(String inscricao, Long tipo) throws
        ExcecaoNegocioGenerica {
        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        CadastroImobiliario cadastro = cadastroImobiliarioFacade.recuperaPorInscricao(inscricao);
        TipoDoctoOficial tipoDoctoOficial = tipoDoctoOficialFacade.recuperar(tipo);
        if (!solicitacaoDoctoOficialFacade.existeCertidaoVigente(tipo, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO, cadastro.getId())) {
            boolean permiteSolicitarDocumento = false;
            try {
                SolicitacaoDoctoOficial solicitacao = new SolicitacaoDoctoOficial();
                solicitacao.setCadastroImobiliario(cadastro);
                solicitacao.setDataSolicitacao(new Date());
                solicitacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoDoctoOficial.class, "codigo"));
                solicitacao.setTipoDoctoOficial(tipoDoctoOficial);

                solicitacao.setDocumentoOficial(documentoOficialFacade.geraDocumentoEmBranco(cadastro, null, tipoDoctoOficial));
                permiteSolicitarDocumento = solicitacaoDoctoOficialFacade.permitirSolicitarDocumento(solicitacao).getValido();
                if (permiteSolicitarDocumento) {
                    solicitacao = em.merge(solicitacao);
                    solicitacaoDoctoOficialFacade.calculaValorCertidao(solicitacao);
                    validacao.setSolicitou(true);
                }
            } catch (Exception e) {
                validacao.setMensagem("Erro ao gerar a certidão");
            }
            if (!permiteSolicitarDocumento) {
                validacao.setMensagem("O imóvel " +
                    (TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "não" : "") +
                    " possui débitos em aberto" +
                    (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "/suspensos" : "") +
                    ". Não é possível fazer a geração da " + tipoDoctoOficial.getDescricao() + "!");
            }
        } else {
            validacao.setMensagem("Já existe uma solicitação de " + tipoDoctoOficial.getDescricao() + " vigente!");
        }
        return validacao;
    }

    public WsValidacaoSolicitacaoDoctoOficial gerarSolicitacaoParaCadastroEconomico(String cmc, Long tipo) throws
        ExcecaoNegocioGenerica {
        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        CadastroEconomico cadastro = cadastroEconomicoFacade.recuperaPorInscricao(cmc);
        TipoDoctoOficial tipoDoctoOficial = tipoDoctoOficialFacade.recuperar(tipo);
        if (!solicitacaoDoctoOficialFacade.existeCertidaoVigente(tipo, TipoCadastroDoctoOficial.CADASTROECONOMICO, cadastro.getId())) {
            boolean permiteSolicitarDocumento = false;
            try {
                SolicitacaoDoctoOficial solicitacao = new SolicitacaoDoctoOficial();
                solicitacao.setCadastroEconomico(cadastro);
                solicitacao.setDataSolicitacao(new Date());
                solicitacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoDoctoOficial.class, "codigo"));
                solicitacao.setTipoDoctoOficial(tipoDoctoOficial);
                solicitacao.setDocumentoOficial(documentoOficialFacade.geraDocumentoEmBranco(cadastro, null, tipoDoctoOficial));
                permiteSolicitarDocumento = solicitacaoDoctoOficialFacade.permitirSolicitarDocumento(solicitacao).getValido();
                if (permiteSolicitarDocumento) {
                    solicitacao = em.merge(solicitacao);
                    solicitacaoDoctoOficialFacade.calculaValorCertidao(solicitacao);
                    validacao.setSolicitou(true);
                }
            } catch (Exception e) {
                validacao.setMensagem("Erro ao gerar a certidão");
            }
            if (!permiteSolicitarDocumento) {
                validacao.setMensagem("O contribuinte econômico " +
                    (TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "não" : "") +
                    " possui débitos em aberto" +
                    (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(tipoDoctoOficial.getTipoValidacaoDoctoOficial()) ? "/suspensos" : "") +
                    ". Não é possível fazer a geração da " + tipoDoctoOficial.getDescricao() + "!");
            }
        } else {
            validacao.setMensagem("Já existe uma solicitação de " + tipoDoctoOficial.getDescricao() + " vigente!");
        }
        return validacao;
    }

    private SituacaoParcela recuperarSituacaoParcelaOriginalAlvara(Alvara alvara) {
        VOAlvara voAlvara = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(alvara.getId());

        if (voAlvara != null && !voAlvara.getItens().isEmpty()) {
            List<ResultadoParcela> parcelas = Lists.newArrayList();
            for (VOAlvaraItens item : voAlvara.getItens()) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, item.getId());
                parcelas.addAll(consultaParcela.executaConsulta().getResultados());
            }

            boolean pago = true;
            if (!parcelas.isEmpty()) {
                for (ResultadoParcela parcela : parcelas) {
                    if (SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                        pago = false;
                        break;
                    }
                }
            }
            if (!pago) {
                return SituacaoParcela.fromDto(SituacaoParcelaDTO.EM_ABERTO);
            }
            if (!parcelas.isEmpty()) {
                return SituacaoParcela.fromDto(parcelas.get(0).getSituacaoEnumValue());
            }
        }
        return null;
    }

    public List<WSDivida> buscarDividasPortalWeb(String cpf) {
        List<Divida> dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
        List<WSDivida> retorno = Lists.newArrayList();
        for (Divida divida : dividas) {
            retorno.add(new WSDivida(divida));
        }
        return retorno;
    }

    public PessoaFisica buscarQualquerPessoaPorCpfAndMatricula(String cpf, String matricula) {
        return pessoaFacade.buscarPessoaPorCpfAndMatricula(cpf, matricula);
    }

    public void salvarFaleConosco(FaleConosco faleConosco) {
        faleConoscoFacade.salvarFaleConosco(faleConosco);
    }

    public List<WSDocumentoPortalServidor> buscarDocumentosParaPortal() {
        return documentoPortalContribuinteFacade.buscarDocumentosParaOservidorNoPortal();
    }


    public List<DocumentoObrigatorioDTO> buscarDocumentosObrigatorioCadastro(TipoPessoa tipoPessoa, TipoSolicitacaoCadastroPessoa tipoSolicitacaoCadastroPessoa) {
        List<DocumentoObrigatorioPortal> documentoObrigatorios = documentoPortalContribuinteFacade.buscarDocumentosObrigatorioCadastro(tipoPessoa, tipoSolicitacaoCadastroPessoa);
        List<DocumentoObrigatorioDTO> retorno = Lists.newArrayList();
        for (DocumentoObrigatorioPortal doc : documentoObrigatorios) {
            retorno.add(new DocumentoObrigatorioDTO(doc.getId(), doc.getDescricao(), TipoPessoaPortal.valueOf(doc.getTipoPessoa().name())));
        }
        return retorno;
    }

    public CIDFacade getCidFacade() {
        return cidFacade;
    }

    public GrauDeParentescoFacade getGrauDeParentescoFacade() {
        return grauDeParentescoFacade;
    }

    public List<MarcaDTO> buscarMarcasDTOPorTipoMarcaPortalWeb(TipoMarca tipoMarca) {
        String hql = "select m from Marca m where m.tipoMarca = :tipoMarca and m.descricao is not null order by m.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("tipoMarca", tipoMarca);
        List<Marca> marcas = q.getResultList();
        List<MarcaDTO> retorno = Lists.newArrayList();
        for (Marca marca : marcas) {
            retorno.add(marca.toDTO());
        }
        return retorno;
    }

    public List<CorDTO> buscarCoresPortalWeb() {
        String hql = "select c from Cor c where c.descricao is not null order by c.descricao";
        Query q = em.createQuery(hql);
        List<Cor> cores = q.getResultList();
        List<CorDTO> retorno = Lists.newArrayList();
        for (Cor cor : cores) {
            retorno.add(cor.toDTO());
        }
        return retorno;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public List<ArquivoDTO> recuperarArquivosCondutor(CondutorOttDTO condutor) {
        String hql = " select con from CondutorOperadoraDetentorArquivo con where con.condutorOtt.id = :idCondutor";
        Query q = em.createQuery(hql);
        q.setParameter("idCondutor", condutor.getId());

        List<CondutorOperadoraDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (CondutorOperadoraDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public List<ArquivoDTO> recuperarArquivosVeiculo(VeiculoOttDTO veiculo) {
        String hql = " select veic from VeiculoOperadoraDetentorArquivo veic where veic.veiculoOtt.id = :idVeiculo";
        Query q = em.createQuery(hql);
        q.setParameter("idVeiculo", veiculo.getId());

        List<VeiculoOperadoraDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (VeiculoOperadoraDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public PessoaFisicaDTO buscarPessoaFisicaPorCpf(String cpf) {
        PessoaFisica pessoa = pessoaFacade.recuperaPessoaFisicaPorCPF(cpf);
        if (pessoa != null) {
            if (pessoa.getUnidadeOrganizacional() != null) {
                HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), pessoa.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
                return pessoa.toPessoaFisicaDTO(ho);
            }
            return pessoa.toPessoaFisicaDTO();
        }
        return null;
    }

    public PessoaJuridicaDTO buscarPessoaJuridicaPorCnpj(String cnpj) {
        PessoaJuridica pessoa = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(cnpj);
        if (pessoa != null) {
            PessoaJuridicaDTO pessoaJuridicaDTO = pessoa.toPessoaJuridicaDTO();
            pessoaJuridicaDTO.setTelefones(Lists.newArrayList(Telefone.toTelefone(pessoa.getTelefonePrincipal())));
            return pessoa.toPessoaJuridicaDTO();
        }
        return null;
    }

    public void salvarAlteracaoPessoaFisica(@RequestBody PessoaFisicaDTO pessoaFisicaDTO) {
        AlteracaoCadastralPessoa alteracaoCadastralPessoa = new AlteracaoCadastralPessoa();
        alteracaoCadastralPessoa.setNomeRazaoSocial(pessoaFisicaDTO.getNome());
        alteracaoCadastralPessoa.setDataNascimentoAbertura(pessoaFisicaDTO.getDataNascimento());
        if (pessoaFisicaDTO.getEnderecos() != null && !pessoaFisicaDTO.getEnderecos().isEmpty()) {
            alteracaoCadastralPessoa.setCep(pessoaFisicaDTO.getEnderecos().get(0).getCep());
            alteracaoCadastralPessoa.setLogradouro(pessoaFisicaDTO.getEnderecos().get(0).getLogradouro());
            alteracaoCadastralPessoa.setBairro(pessoaFisicaDTO.getEnderecos().get(0).getBairro());
            alteracaoCadastralPessoa.setCidade(pessoaFisicaDTO.getEnderecos().get(0).getLocalidade());
            alteracaoCadastralPessoa.setUf(pessoaFisicaDTO.getEnderecos().get(0).getUf());
            alteracaoCadastralPessoa.setComplemento(pessoaFisicaDTO.getEnderecos().get(0).getComplemento());
            alteracaoCadastralPessoa.setNumero(pessoaFisicaDTO.getEnderecos().get(0).getNumero());
        }
        alteracaoCadastralPessoa.setEmail(pessoaFisicaDTO.getEmail());
        if (pessoaFisicaDTO.getTelefones() != null && !pessoaFisicaDTO.getTelefones().isEmpty()) {
            alteracaoCadastralPessoa.setTelefone(pessoaFisicaDTO.getTelefones().get(0).getTelefone());
        }
        PessoaFisica pf = PessoaFisica.dtoToPessoaFisica(pessoaFisicaDTO);
        alteracaoCadastralPessoa.setPessoa(pf);
        alteracaoCadastralPessoa.setSituacao(SituacaoAlteracaoCadastralPessoa.EM_ABERTO);
        alteracaoCadastralPessoa.setDataAlteracao(new Date());

        alteracaoCadastralPessoa = em.merge(alteracaoCadastralPessoa);
        alteracaoCadastralPessoaFacade.criarNotificacaoAlteracaoCadastralPessoaAguardandoAprovacao(alteracaoCadastralPessoa);
    }

    public void salvarAlteracaoPessoaJuridica(@RequestBody PessoaJuridicaDTO pessoaJuridicaDTO) {
        AlteracaoCadastralPessoa alteracaoCadastralPessoa = new AlteracaoCadastralPessoa();
        alteracaoCadastralPessoa.setNomeRazaoSocial(pessoaJuridicaDTO.getNome());
        alteracaoCadastralPessoa.setDataNascimentoAbertura(pessoaJuridicaDTO.getInicioAtividade());
        alteracaoCadastralPessoa.setCep(pessoaJuridicaDTO.getEnderecoCorreio().getCep());
        alteracaoCadastralPessoa.setLogradouro(pessoaJuridicaDTO.getEnderecoCorreio().getLogradouro());
        alteracaoCadastralPessoa.setBairro(pessoaJuridicaDTO.getEnderecoCorreio().getBairro());
        alteracaoCadastralPessoa.setCidade(pessoaJuridicaDTO.getEnderecoCorreio().getLocalidade());
        alteracaoCadastralPessoa.setUf(pessoaJuridicaDTO.getEnderecoCorreio().getUf());
        alteracaoCadastralPessoa.setComplemento(pessoaJuridicaDTO.getEnderecoCorreio().getComplemento());
        alteracaoCadastralPessoa.setNumero(pessoaJuridicaDTO.getEnderecoCorreio().getNumero());
        alteracaoCadastralPessoa.setEmail(pessoaJuridicaDTO.getEmail());
        alteracaoCadastralPessoa.setTelefone(pessoaJuridicaDTO.getTelefones().get(0).getTelefone());
        PessoaJuridica pj = PessoaJuridica.dtoToPessoaJuridica(pessoaJuridicaDTO);
        alteracaoCadastralPessoa.setPessoa(pj);
        alteracaoCadastralPessoa.setSituacao(SituacaoAlteracaoCadastralPessoa.EM_ABERTO);
        alteracaoCadastralPessoa.setDataAlteracao(new Date());

        alteracaoCadastralPessoa = em.merge(alteracaoCadastralPessoa);
        alteracaoCadastralPessoaFacade.criarNotificacaoAlteracaoCadastralPessoaAguardandoAprovacao(alteracaoCadastralPessoa);
    }

    public void salvarArquivosAlteracaoPessoa(List<ArquivoDTO> arquivos) {
        DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
        for (ArquivoDTO arquivoDTO : arquivos) {
            try {
                AlteracaoCadastralPessoa alteracaoPessoa = alteracaoCadastralPessoaFacade.recuperarAlteracaoPeloIdPessoa(arquivoDTO.getId());
                if (alteracaoPessoa != null) {
                    ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                    arquivoComposicao.setDataUpload(new Date());
                    arquivoComposicao.setArquivo(new Arquivo());
                    String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                    String data = arquivoDTO.getConteudo().split("base64,")[1];
                    String mimeType;
                    try {
                        mimeType = dataInfo.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        mimeType = null;
                    }
                    Base64 decoder = new Base64();
                    byte[] imgBytes = decoder.decode(data);
                    Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
                    arquivo.setNome(arquivoDTO.getNome());
                    arquivo.setMimeType(mimeType);
                    arquivoComposicao.setArquivo(arquivo);

                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);

                }

                if (alteracaoPessoa != null) {
                    alteracaoPessoa.setDetentorArquivoComposicao(detentorArquivoComposicao);
                }
            } catch (Exception e) {
                logger.error("erro ", e);
            }
        }
    }

    public TipoDependenteFacade getTipoDependenteFacade() {
        return tipoDependenteFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ResultadoParcela> buscarDebitosIPTU(WsIPTU wsIPTU, boolean carneIptu) {
        CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperaPorInscricao(wsIPTU.getMatriculaImovel());
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(wsIPTU.getCpfCnpj());
        if (pessoa != null && cadastroImobiliario != null) {
            return buscarParcelasPorCadastroImobiliario(cadastroImobiliario, pessoa, carneIptu);
        }
        return Lists.newArrayList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ResultadoParcela> buscarDebitosISSQNFixo(WsIssqnFixo wsISSQNFixo) {
        Pessoa pessoa = pessoaFacade.buscarPessoaAtivasPorCpfOrCnpj(wsISSQNFixo.getCpfCnpj());
        return buscarParcelasISSQNFixoPorCpfCnpj(wsISSQNFixo.getAno(), pessoa);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    private List<ResultadoParcela> buscarParcelasPorCadastroImobiliario(CadastroImobiliario cadastroImobiliario, Pessoa pessoa, boolean carneIptu) {
        ConsultaParcela consultaParcela = carregarConsultaParcelaBCIPorWs(cadastroImobiliario, pessoa, carneIptu);
        adicionarOrdemPadrao(consultaParcela);
        return consultaParcela.executaConsulta().getResultados();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    private List<ResultadoParcela> buscarParcelasISSQNFixoPorCpfCnpj(Integer ano, Pessoa pessoa) {
        ConsultaParcela consultaParcela = carregarConsultaParcelaCMCPorWs(ano, pessoa);
        adicionarOrdemPadrao(consultaParcela);
        return consultaParcela.executaConsulta().getResultados();
    }

    private ConsultaParcela carregarConsultaParcelaBCIPorWs(CadastroImobiliario cadastroImobiliario, Pessoa pessoa, boolean carneIptu) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, cadastroImobiliario.getId());
        if (carneIptu) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.DIFERENTE, 1);
            consultaParcela.addComplementoDoWhere(" and vw.divida_id = " + atribuirDividaIPTU().getId() + " and vw.exercicio = " +
                sistemaFacade.getExercicioCorrente().getAno());
        } else {
            List<Divida> dividasParcelamento = parametroParcelamentoFacade.buscarDividasParcelamento(atribuirDividaIPTU().getDivida());
            String ids = atribuirDividaIPTU().getDivida().getId() + "";
            for (Divida divida : dividasParcelamento) {
                if (divida.getIsParcelamento()) {
                    ids += ", " + divida.getId();
                }
            }

            consultaParcela.addComplementoDoWhere(
                " and ((vw.divida_id in (" + ids + ") and vw.exercicio >= " + atribuirExercicioEmissaoIPTU().getAno() +
                    ") or (vw.divida_id = " + atribuirDividaIPTU().getId() + " and vw.exercicio = " + sistemaFacade.getExercicioCorrente().getAno() + ")) ");

            consultaParcela.addComplementoDoWhere(" and (" + pessoa.getId() + " in (select prop.pessoa_id " +
                " from propriedade prop " +
                " where (prop.finalvigencia is null or trunc (prop.finalvigencia) >= trunc(sysdate)) and prop.imovel_id = " + cadastroImobiliario.getId() + ") " +
                " or " + pessoa.getId() + " in (select comp.compromissario_id" +
                " from COMPROMISSARIO comp where (comp.fimvigencia is null or trunc(comp.fimvigencia) >= trunc(sysdate)) " +
                " and comp.CADASTROIMOBILIARIO_ID = " + cadastroImobiliario.getId() + "))");
        }
        return consultaParcela;
    }

    private ConsultaParcela carregarConsultaParcelaCMCPorWs(Integer ano, Pessoa pessoa) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, atribuirDividaIssqnFixo().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, pessoa.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, ano);
        return consultaParcela;
    }

    private Divida atribuirDividaIPTU() {
        return configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();
    }

    private Exercicio atribuirExercicioEmissaoIPTU() {
        return getExercicioFacade().getExercicioPorAno(sistemaFacade.getExercicioCorrente().getAno() - configuracaoTributarioFacade.retornaUltimo().getQuantidadeAnosPrescricao());
    }

    public ContraChequeFacade getContraChequeFacade() {
        return contraChequeFacade;
    }

    public ParametroSaudFacade getParametroSaudFacade() {
        return parametroSaudFacade;
    }

    private Divida atribuirDividaIssqnFixo() {
        return configuracaoTributarioFacade.retornaUltimo().getDividaISSFixo();
    }

    public List<ArquivoDTO> recuperarRenovacaoArquivosCondutor(Long idRenovacaoCondutor) {
        String hql = " select con from CondutorRenovacaoDetentorArquivo con where con.renovacaoCondutorOTT.id = :idRenovacaoCondutor";
        Query q = em.createQuery(hql);
        q.setParameter("idRenovacaoCondutor", idRenovacaoCondutor);

        List<CondutorRenovacaoDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (CondutorRenovacaoDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public List<ArquivoDTO> recuperarRenovacaoArquivosVeiculo(Long idRenovacaoVeiculo) {
        String hql = " select veic from VeiculoRenovacaoDetentorArquivo veic where veic.renovacaoVeiculoOTT.id = :idRenovacaoVeiculo";
        Query q = em.createQuery(hql);
        q.setParameter("idRenovacaoVeiculo", idRenovacaoVeiculo);

        List<VeiculoRenovacaoDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (VeiculoRenovacaoDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public RenovacaoCondutorOttDTO salvarRenovacaoCondutorOtt(RenovacaoCondutorOttDTO renovacaoCondutorOttDTO) throws Exception {
        RenovacaoCondutorOTT renovacao = new RenovacaoCondutorOTT();
        renovacao.setCondutorOtt(condutorOperadoraTecnologiaTransporteFacade.recuperar(renovacaoCondutorOttDTO.getCondutorOttDTO().getId()));
        renovacao.setDataRenovacao(sistemaFacade.getDataOperacao());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.CADASTRADO);
        renovacao.setExercicioRenovacao(sistemaFacade.getExercicioCorrente());
        addAnexosRenovacaoCondutor(renovacao, renovacaoCondutorOttDTO.getAnexos());
        renovacao = em.merge(renovacao);
        renovacaoCondutorOttDTO.setId(renovacao.getId());
        return renovacaoCondutorOttDTO;
    }

    public void addAnexosRenovacaoCondutor(RenovacaoCondutorOTT renovacaoCondutorOTT,
                                           List<AnexoCondutorOttDTO> anexos) throws Exception {
        for (AnexoCondutorOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                CondutorRenovacaoDetentorArquivo renovacaoCondArq = new CondutorRenovacaoDetentorArquivo();
                renovacaoCondArq.setRenovacaoCondutorOTT(renovacaoCondutorOTT);
                renovacaoCondArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                renovacaoCondArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos().add(renovacaoCondArq);
            }
        }
    }

    public RenovacaoVeiculoOttDTO salvarRenovacaoVeiculoOtt(RenovacaoVeiculoOttDTO renovacaoVeiculoOttDTO) throws Exception {
        RenovacaoVeiculoOTT renovacao = new RenovacaoVeiculoOTT();
        renovacao.setVeiculoOtt(veiculoOperadoraTecnologiaTransporteFacade.recuperar(renovacaoVeiculoOttDTO.getVeiculoOttDTO().getId()));
        renovacao.setDataRenovacao(sistemaFacade.getDataOperacao());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.CADASTRADO);
        renovacao.setExercicioRenovacao(sistemaFacade.getExercicioCorrente());
        addAnexosRenovacaoVeiculo(renovacao, renovacaoVeiculoOttDTO.getAnexos());
        renovacao = veiculoOperadoraTecnologiaTransporteFacade.salvarRenovacaoVeiculo(renovacao);
        renovacaoVeiculoOttDTO.setId(renovacao.getId());
        return renovacaoVeiculoOttDTO;
    }

    public void addAnexosRenovacaoVeiculo(RenovacaoVeiculoOTT renovacaoVeiculoOTT,
                                          List<AnexoVeiculoOttDTO> anexos) throws Exception {
        for (AnexoVeiculoOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                VeiculoRenovacaoDetentorArquivo renovacaoVeicArq = new VeiculoRenovacaoDetentorArquivo();
                renovacaoVeicArq.setRenovacaoVeiculoOTT(renovacaoVeiculoOTT);
                renovacaoVeicArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                renovacaoVeicArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos().add(renovacaoVeicArq);
            }
        }
    }

    public void salvarArquivosCondutorOttRenovacao(List<ArquivoDTO> arquivos) {

        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.recuperar(arquivos.get(0).getId());

        RenovacaoCondutorOTT renovacaoCondutorOTT = condutorOperadoraTecnologiaTransporteFacade.buscarRenovacaoOttPorCondutorSituacao(
            condutor.getId(), SituacaoRenovacaoOTT.CADASTRADO.name());

        renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos().clear();

        for (ArquivoDTO arquivoDTO : arquivos) {
            try {
                if (condutor != null) {

                    CondutorRenovacaoDetentorArquivo condutorArq = new CondutorRenovacaoDetentorArquivo();
                    condutorArq.setCondutorOtt(condutor);
//                    condutorArq.setDocumentoCondutorOtt(DocumentoCondutorOtt.getEnumByDescricao(arquivoDTO.getDescricao()));
                    condutorArq.setRenovacaoOTT(renovacaoCondutorOTT);

                    ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                    arquivoComposicao.setDataUpload(new Date());
                    arquivoComposicao.setArquivo(new Arquivo());
                    String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                    String data = arquivoDTO.getConteudo().split("base64,")[1];
                    String mimeType;
                    try {
                        mimeType = dataInfo.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        mimeType = null;
                    }
                    Base64 decoder = new Base64();
                    byte[] imgBytes = decoder.decode(data);
                    Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
                    arquivo.setNome(arquivoDTO.getNome());
                    arquivo.setDescricao(arquivoDTO.getDescricao());
                    arquivo.setMimeType(mimeType);
                    arquivoComposicao.setArquivo(arquivo);

                    DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);

                    condutorArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos().add(condutorArq);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void salvarArquivosVeiculoOttRenovacao(List<ArquivoDTO> arquivos) {

        VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.recuperar(arquivos.get(0).getId());

        RenovacaoVeiculoOTT renovacaoVeiculoOTT = veiculoOperadoraTecnologiaTransporteFacade.buscarRenovacaoOttPorVeiculoSituacao(
            veiculo.getId(), SituacaoRenovacaoOTT.CADASTRADO.name());

        renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos().clear();

        for (ArquivoDTO arquivoDTO : arquivos) {
            try {
                if (veiculo != null) {

                    VeiculoRenovacaoDetentorArquivo veiculoArq = new VeiculoRenovacaoDetentorArquivo();
                    veiculoArq.setVeiculoOtt(veiculo);
//                    veiculoArq.setDocumentoVeiculoOtt(DocumentoVeiculoOtt.getEnumByDescricao(arquivoDTO.getDescricao()));
                    veiculoArq.setRenovacaoVeiculoOTT(renovacaoVeiculoOTT);

                    ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                    arquivoComposicao.setDataUpload(new Date());
                    arquivoComposicao.setArquivo(new Arquivo());
                    String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                    String data = arquivoDTO.getConteudo().split("base64,")[1];
                    String mimeType;
                    try {
                        mimeType = dataInfo.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        mimeType = null;
                    }
                    Base64 decoder = new Base64();
                    byte[] imgBytes = decoder.decode(data);
                    Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
                    arquivo.setNome(arquivoDTO.getNome());
                    arquivo.setDescricao(arquivoDTO.getDescricao());
                    arquivo.setMimeType(mimeType);
                    arquivoComposicao.setArquivo(arquivo);

                    DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);

                    veiculoArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos().add(veiculoArq);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public List<ArquivoDTO> recuperarArquivosOperadora(OperadoraTecnologiaTransporteDTO operadora) {
        String hql = " select opera from OperadoraTransporteDetentorArquivo opera where opera.operadoraTecTransporte.id = :idOperadora";
        Query q = em.createQuery(hql);
        q.setParameter("idOperadora", operadora.getId());

        List<OperadoraTransporteDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (OperadoraTransporteDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public void salvarArquivosOperadoraOtt(List<ArquivoDTO> arquivos) {

        OperadoraTecnologiaTransporte operadora = ottFacade.recuperar(arquivos.get(0).getId());
        Exercicio exercicioPorAno = exercicioFacade.getExercicioPorAno(DataUtil.getAno(operadora.getDataCadastro()));

        operadora.getDetentorArquivoComposicao().clear();

        for (ArquivoDTO arquivoDTO : arquivos) {
            try {
                if (operadora != null) {

                    OperadoraTransporteDetentorArquivo operaArq = new OperadoraTransporteDetentorArquivo();
                    operaArq.setOperadoraTecTransporte(operadora);
                    operaArq.setExercicio(exercicioPorAno);
//                    operaArq.setDocumentosOperadoraTransporte(DocumentosOperadoraTrasporte.getEnumByDescricao(arquivoDTO.getDescricao()));

                    ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                    arquivoComposicao.setDataUpload(new Date());
                    arquivoComposicao.setArquivo(new Arquivo());
                    String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                    String data = arquivoDTO.getConteudo().split("base64,")[1];
                    String mimeType;
                    try {
                        mimeType = dataInfo.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        mimeType = null;
                    }
                    Base64 decoder = new Base64();
                    byte[] imgBytes = decoder.decode(data);
                    Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
                    arquivo.setNome(arquivoDTO.getNome());
                    arquivo.setDescricao(arquivoDTO.getDescricao());
                    arquivo.setMimeType(mimeType);
                    arquivoComposicao.setArquivo(arquivo);

                    DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);

                    operaArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    operadora.getDetentorArquivoComposicao().add(operaArq);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public RenovacaoOperadoraOttDTO salvarRenovacaoOperadora(RenovacaoOperadoraOttDTO renovacaoOperadora) throws Exception {
        RenovacaoOperadoraOTT renovacao = new RenovacaoOperadoraOTT();
        renovacao.setOperadora(operadoraTecnologiaTransporteFacade.recuperar(renovacaoOperadora.getOperadoraOttDTO().getId()));
        renovacao.setDataRenovacao(renovacaoOperadora.getDataRenovacao());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.CADASTRADO);
        renovacao.setExercicioRenovacao(exercicioFacade.getExercicioPorAno(renovacaoOperadora.getExercicioRenovacao()));
        addAnexosRenovacaoOperadora(renovacao, renovacaoOperadora.getAnexos());
        renovacao = em.merge(renovacao);
        renovacaoOperadora.setId(renovacao.getId());
        return renovacaoOperadora;
    }

    public void addAnexosRenovacaoOperadora(RenovacaoOperadoraOTT renovacaoOperadoraOTT,
                                            List<AnexoCredenciamentoOttDTO> anexos) throws Exception {
        for (AnexoCredenciamentoOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                OperadoraRenovacaoDetentorArquivo ottArq = new OperadoraRenovacaoDetentorArquivo();
                ottArq.setRenovacaoOperadoraOTT(renovacaoOperadoraOTT);
                ottArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                ottArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos().add(ottArq);
            }
        }
    }

    private DetentorArquivoComposicao criarDetentorArquivoComposicao(String conteudo, String nome, String descricao) throws Exception {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setArquivo(new Arquivo());
        String dataInfo = conteudo.split(";base64,")[0];
        String data = conteudo.split("base64,")[1];
        String mimeType;
        try {
            mimeType = dataInfo.split(":")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mimeType = null;
        }
        Base64 decoder = new Base64();
        byte[] imgBytes = decoder.decode(data);
        Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(),
            new ByteArrayInputStream(imgBytes));
        arquivo.setNome(nome);
        arquivo.setDescricao(descricao);
        arquivo.setMimeType(mimeType);
        arquivoComposicao.setArquivo(arquivo);

        DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
        arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
        detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
        detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);
        return detentorArquivoComposicao;
    }

    public void salvarArquivosOperadoraOttRenovacao(List<ArquivoDTO> arquivos) {

        OperadoraTecnologiaTransporte operadora = ottFacade.recuperar(arquivos.get(0).getId());

        RenovacaoOperadoraOTT renovacaoOperadoraOTT = ottFacade.buscarRenovacaoOttPorOperadoraSituacao(
            operadora.getId(), SituacaoRenovacaoOTT.CADASTRADO.name());

        renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos().clear();

        for (ArquivoDTO arquivoDTO : arquivos) {
            try {
                if (operadora != null) {

                    OperadoraRenovacaoDetentorArquivo condutorArq = new OperadoraRenovacaoDetentorArquivo();
                    condutorArq.setOperadora(operadora);
//                    condutorArq.setDocumentoOperadoraOtt(DocumentosOperadoraTrasporte.getEnumByDescricao(arquivoDTO.getDescricao()));
                    condutorArq.setRenovacaoOperadoraOTT(renovacaoOperadoraOTT);

                    ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                    arquivoComposicao.setDataUpload(new Date());
                    arquivoComposicao.setArquivo(new Arquivo());
                    String dataInfo = arquivoDTO.getConteudo().split(";base64,")[0];
                    String data = arquivoDTO.getConteudo().split("base64,")[1];
                    String mimeType;
                    try {
                        mimeType = dataInfo.split(":")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        mimeType = null;
                    }
                    Base64 decoder = new Base64();
                    byte[] imgBytes = decoder.decode(data);
                    Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), new ByteArrayInputStream(imgBytes));
                    arquivo.setNome(arquivoDTO.getNome());
                    arquivo.setDescricao(arquivoDTO.getDescricao());
                    arquivo.setMimeType(mimeType);
                    arquivoComposicao.setArquivo(arquivo);

                    DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
                    arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
                    detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);

                    condutorArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                    renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos().add(condutorArq);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public List<ArquivoDTO> recuperarRenovacaoArquivosOperadora(Long idRenovacaoOperadora) {
        String hql = " select con from OperadoraRenovacaoDetentorArquivo con where con.renovacaoOperadoraOTT.id = :idRenovacaoOperadora";
        Query q = em.createQuery(hql);
        q.setParameter("idRenovacaoOperadora", idRenovacaoOperadora);

        List<OperadoraRenovacaoDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (OperadoraRenovacaoDetentorArquivo arquivo : arquivos) {
//            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO(arquivo.getDocumentoOperadoraOtt().getDescricao()));
        }
        return retorno;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroImobiliarioImpressaoHistFacade getCadastroImobiliarioImpressaoHistFacade() {
        return cadastroImobiliarioImpressaoHistFacade;
    }

    public CadastroEconomicoImpressaoHistFacade getCadastroEconomicoImpressaoHistFacade() {
        return cadastroEconomicoImpressaoHistFacade;
    }

    public void atualizarFotoPessoa(FotoPessoaDTO foto) {
        ImagemUsuarioNfseDTO dtoFoto = convertToImagemUsuarioNfseDto(foto);
        pessoaFacade.atualizarImagemPessoa(dtoFoto);
    }

    private ImagemUsuarioNfseDTO convertToImagemUsuarioNfseDto(FotoPessoaDTO foto) {
        ImagemUsuarioNfseDTO dtoFoto = new ImagemUsuarioNfseDTO();
        dtoFoto.setConteudo(foto.getFoto());
        dtoFoto.setId(foto.getId());
        PessoaNfseDTO pessoa = new PessoaNfseDTO();
        pessoa.setId(foto.getId());
        dtoFoto.setPessoa(pessoa);
        return dtoFoto;
    }

    public List<WsProcRegularizaConstrucao> buscarProcessosRegularizacaoConstrucao(String cpfCnpj, String anoProtocolo, String numeroProtocolo, String inscricaoCadastral, Integer codigo, Integer ano, Date dataCriacaoProcesso) {
        List<ProcRegularizaConstrucao> processos = procRegularizaConstrucaoFacade.buscarProcessos(procRegularizaConstrucaoFacade.criarCriteriaConsulta(anoProtocolo, numeroProtocolo, inscricaoCadastral, cpfCnpj, codigo, ano, dataCriacaoProcesso));
        List<WsProcRegularizaConstrucao> wsProcessos = Lists.newArrayList();
        for (ProcRegularizaConstrucao proc : processos) {
            wsProcessos.add(new WsProcRegularizaConstrucao(proc));
        }
        return wsProcessos;
    }

    public WsAlvaraConstrucao buscarAlvaraDeConstrucao(Long id) {
        AlvaraConstrucao alvara = alvaraConstrucaoFacade.recuperar(id);
        if (alvara != null) {
            return new WsAlvaraConstrucao(alvara);
        } else {
            return null;
        }
    }

    public WsHabitese buscarHabiteseDeConstrucao(Long id) {
        Habitese habitese = habiteseConstrucaoFacade.recuperar(id);
        if (habitese != null) {
            return new WsHabitese(habitese);
        } else {
            return null;
        }
    }

    public WsTermoUso buscarTermoUsoVigente() {
        TermoUso termoUso = termoUsoFacade.buscarTermoUsoVigente(Sistema.PORTAL_CONTRIBUINTE);
        if (termoUso != null) {
            return new WsTermoUso(termoUso);
        }
        return null;
    }

    public Boolean hasTermoUsoVigente(Long idPessoa) {
        UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(idPessoa);
        return termoUsoFacade.hasTermoUsoPortalParaAceite(usuarioWeb);
    }

    public void aceitarTermoUso(Long idPessoa, Long idTermo) {
        UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(idPessoa);
        TermoUso termoUso = termoUsoFacade.recuperar(idTermo);
        termoUsoFacade.aceitarTermoUsoPortal(usuarioWeb, termoUso);
    }

    public DAM recuperarDAMPeloIdParcela(Long idParcela) {
        return damFacade.recuperaDAMPeloIdParcela(idParcela);
    }

    public boolean permiteEmitirCarneIPTU() {
        return configuracaoTributarioFacade.permiteEmitirCarneIPTUPortal();
    }

    public NovoTomadorDTO buscarInformacoesTomador(String cpfCnpj) {
        String sql = " select coalesce(pf.nome, pj.razaosocial), cmc.inscricaocadastral " +
            " from pessoa p " +
            " left join pessoafisica pf on p.id = pf.id " +
            " left join pessoajuridica pj on p.id = pj.id " +
            " left join cadastroeconomico cmc on p.id = cmc.pessoa_id " +
            " where coalesce(replace(replace(pf.cpf,'.',''),'-',''), " +
            " replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')) = :cpfCnpj ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cpfCnpj", StringUtil.retornaApenasNumeros(cpfCnpj));

        List<Object[]> resultados = q.getResultList();

        if (resultados != null && !resultados.isEmpty()) {
            Object[] obj = resultados.get(0);
            NovoTomadorDTO tomador = new NovoTomadorDTO();
            tomador.setNomeRazaoSocial(obj[0] != null ? (String) obj[0] : null);
            tomador.setCadastroEconomico(obj[1] != null ? (String) obj[1] : null);

            return tomador;
        }
        return null;
    }

    public WSNFSAvulsa gerarNotaFiscaoAvulsaPortal(@RequestBody EmissaoNotaAvulsaDTO dto) throws Exception {
        return nfsAvulsaFacade.gerarNotaAvulsaPortalContribuinte(dto);
    }

    public byte[] gerarImpressaoNotaAvulsa(Long idNota) throws Exception {
        NFSAvulsa nfsAvulsa = nfsAvulsaFacade.recuperar(idNota);
        if (nfsAvulsa != null) {
            return nfsAvulsaFacade.gerarImpressaoNFSAvulsa(nfsAvulsa);
        }
        return null;
    }

    public void registrarSolicitacaoCadastroPessoa(String cpf, String email) {
        solicitacaoCadastroPessoaFacade.registrarSolicitacaoCadastroPessoa(cpf, email);
    }

    public void registrarSolicitacaoCadastroCredor(String cpf, String email) {
        solicitacaoCadastroPessoaFacade.registrarSolicitacaoCadastroCredor(cpf, email);
    }

    public SolicitacaoCadastroPessoa getSolicitacaoCadastroPessoa(String key) {
        return solicitacaoCadastroPessoaFacade.findByKey(key);
    }

    public List<ArquivoDTO> buscarArquivosDaPessoa(Long idPessoa) {
        String hql = " select pes.detentorArquivoComposicao from Pessoa pes where pes.id = :idPessoa";
        Query q = em.createQuery(hql);
        q.setParameter("idPessoa", idPessoa);

        List<DetentorArquivoComposicao> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (DetentorArquivoComposicao arquivo : arquivos) {
            for (ArquivoComposicao arquivoComposicao : arquivo.getArquivosComposicao()) {
                retorno.add(arquivoComposicao.getArquivo().toArquivoDTO(arquivoComposicao.getArquivo().getNome()));
            }
        }
        return retorno;
    }

    public void cadastrarUsuarioSaud(WsUsuarioSaud wsUsuarioSaud) {
        UsuarioSaud usuarioSaud = new UsuarioSaud();
        usuarioSaud.setFoto(ArquivoUtil.converterArquivoDTOToArquivo(wsUsuarioSaud.getFoto(), arquivoFacade));
        usuarioSaud.setCpf(wsUsuarioSaud.getCpf());
        usuarioSaud.setNome(wsUsuarioSaud.getNome());
        usuarioSaud.setDataNascimento(wsUsuarioSaud.getDataNascimento());
        usuarioSaud.setRenda(wsUsuarioSaud.getRenda());
        usuarioSaud.setTelefone(wsUsuarioSaud.getTelefone());
        usuarioSaud.setEmail(wsUsuarioSaud.getEmail());
        usuarioSaud.getEndereco().setCep(wsUsuarioSaud.getCep());
        usuarioSaud.getEndereco().setBairro(wsUsuarioSaud.getBairro());
        usuarioSaud.getEndereco().setLogradouro(wsUsuarioSaud.getLogradouro());
        usuarioSaud.getEndereco().setNumero(wsUsuarioSaud.getNumero());
        usuarioSaud.getEndereco().setCidade(cidadeFacade.recuperaCidadePorNomeEEstado(wsUsuarioSaud.getLocalidade(),
            wsUsuarioSaud.getUf()));
        for (WsUsuarioSaudDocumento wsUsuarioSaudDocumento : wsUsuarioSaud.getDocumentos()) {
            UsuarioSaudDocumento usuarioSaudDocumento = new UsuarioSaudDocumento();
            usuarioSaudDocumento.setUsuarioSaud(usuarioSaud);
            ParametroSaudDocumento parametroSaudDocumento = parametroSaudFacade.recuperarParametroSaudDocumento(
                wsUsuarioSaudDocumento.getParametroSaudDocumento().getId());
            usuarioSaudDocumento.setParametroSaudDocumento(parametroSaudDocumento);
            usuarioSaudDocumento.setDocumento(ArquivoUtil.converterArquivoDTOToArquivo(
                wsUsuarioSaudDocumento.getArquivo(), arquivoFacade));
            usuarioSaud.getDocumentos().add(usuarioSaudDocumento);
        }
        usuarioSaudFacade.salvarNovo(usuarioSaud);
    }


    public List<WsParametroSaudDocumento> getWsParametroSaudDocumentos() {
        List<WsParametroSaudDocumento> documentos = Lists.newArrayList();
        ParametroSaud parametroSaud = parametroSaudFacade.recuperarUltimo();
        if (parametroSaud != null &&
            parametroSaud.getDocumentos() != null) {
            documentos = parametroSaud.getDocumentos().stream().map(WsParametroSaudDocumento::new).collect(Collectors.toList());
        }
        return documentos;
    }

    public WsUsuarioSaud getWsUsuarioSaud(Long id) {
        UsuarioSaud usuarioSaud = usuarioSaudFacade.recuperar(id);
        return new WsUsuarioSaud(usuarioSaud, arquivoFacade);
    }

    public void salvarRetornoDocumentosRejeitados(WsUsuarioSaud wsUsuarioSaud) {
        UsuarioSaud usuarioSaud = usuarioSaudFacade.recuperar(wsUsuarioSaud.getId());
        usuarioSaud.setStatus(UsuarioSaud.Status.RETORNO_DOCUMENTACAO);
        for (UsuarioSaudDocumento usuarioSaudDocumento : usuarioSaud.getDocumentos()) {
            for (WsUsuarioSaudDocumento wsUsuarioSaudDocumento : wsUsuarioSaud.getDocumentos()) {
                if (usuarioSaudDocumento.getId().equals(wsUsuarioSaudDocumento.getId())) {
                    if (usuarioSaudDocumento.getStatus().equals(UsuarioSaudDocumento.Status.REJEITADO)) {
                        usuarioSaudDocumento.setDocumento(ArquivoUtil.converterArquivoDTOToArquivo(wsUsuarioSaudDocumento.getArquivo(),
                            arquivoFacade));
                        usuarioSaudDocumento.setStatus(UsuarioSaudDocumento.Status.AGUARDANDO_AVALIACAO);
                        usuarioSaudDocumento.setObservacao(null);
                        break;
                    }
                }
            }
        }
        usuarioSaudFacade.salvar(usuarioSaud);
    }

    public List<DocumentoCredenciamentoOttDTO> getDocumentosCredenciamentoOtt() {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return Lists.newArrayList();
        return parametrosOtt.getDocumentosCredenciamento()
            .stream().filter(DocumentoCredenciamentoOtt::getAtivo)
            .map(doc -> new DocumentoCredenciamentoOttDTO(doc.getId(),
                doc.getDescricao(), doc.getExtensoesPermitidas(), doc.getRenovacao(), doc.getObrigatorio())).collect(Collectors.toList());
    }

    public List<DocumentoVeiculoOttDTO> getDocumentosVeiculoOtt() {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return Lists.newArrayList();
        return parametrosOtt.getDocumentosVeiculo()
            .stream().filter(DocumentoVeiculoOtt::getAtivo)
            .map(doc -> new DocumentoVeiculoOttDTO(doc.getId(),
                doc.getDescricao(), doc.getExtensoesPermitidas(), doc.getAlugado(),
                doc.getRenovacao(), doc.getObrigatorio())).collect(Collectors.toList());
    }

    public List<DocumentoCondutorOttDTO> getDocumentosCondutorOtt() {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return Lists.newArrayList();
        return parametrosOtt.getDocumentosCondutor()
            .stream().filter(DocumentoCondutorOtt::getAtivo)
            .map(doc -> new DocumentoCondutorOttDTO(doc.getId(),
                doc.getDescricao(), doc.getExtensoesPermitidas(), doc.getServidorPublico(),
                doc.getRenovacao(), doc.getObrigatorio())).collect(Collectors.toList());
    }

    public List<AnexoCredenciamentoOttDTO> getAnexosCredenciamento(Long id) {
        List<AnexoCredenciamentoOttDTO> anexos = Lists.newArrayList();
        OperadoraTecnologiaTransporte operadora = operadoraTecnologiaTransporteFacade.recuperar(id);
        for (OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo : operadora.getDetentorArquivoComposicao()) {
            anexos.add(operadoraTransporteDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosCredenciamento(OperadoraTecnologiaTransporteDTO operadoraDTO) throws Exception {
        OperadoraTecnologiaTransporte operadoraTecnologiaTransporte =
            operadoraTecnologiaTransporteFacade.recuperar(operadoraDTO.getId());
        addAnexosOperadoraTecnologiaTransporte(operadoraTecnologiaTransporte, operadoraDTO.getAnexos());
        operadoraTecnologiaTransporte.setSituacao(SituacaoOTT.RETORNO_DOCUMENTACAO);
        operadoraTecnologiaTransporteFacade.salvar(operadoraTecnologiaTransporte);
    }

    public List<AnexoCredenciamentoOttDTO> getAnexosRenovacao(Long id) {
        List<AnexoCredenciamentoOttDTO> anexos = Lists.newArrayList();
        RenovacaoOperadoraOTT renovacao = operadoraTecnologiaTransporteFacade.recuperarRenovacaoOperadoraOTT(id);
        for (OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo : renovacao.getOperadoraRenovacaoDetentorArquivos()) {
            anexos.add(operadoraRenovacaoDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosRenovacao(RenovacaoOperadoraOttDTO renovacaoDTO) throws Exception {
        RenovacaoOperadoraOTT renovacao = operadoraTecnologiaTransporteFacade.recuperarRenovacao(renovacaoDTO.getId());
        addAnexosRenovacaoOtt(renovacao, renovacaoDTO.getAnexos());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.RETORNO_DOCUMENTACAO);
        operadoraTecnologiaTransporteFacade.salvarRenovacao(renovacao);
    }

    public void addAnexosRenovacaoOtt(RenovacaoOperadoraOTT renovacaoOperadoraOTT,
                                      List<AnexoCredenciamentoOttDTO> anexos) throws Exception {
        for (AnexoCredenciamentoOttDTO anexo : anexos) {
            if (anexo.getId() == null) {
                OperadoraRenovacaoDetentorArquivo renovacaoArq = new OperadoraRenovacaoDetentorArquivo();
                renovacaoArq.setRenovacaoOperadoraOTT(renovacaoOperadoraOTT);
                renovacaoArq.popularDadosDocumento(anexo);
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(anexo.getConteudo(),
                    anexo.getNome(), anexo.getDescricao());
                renovacaoArq.setDetentorArquivoComposicao(detentorArquivoComposicao);
                renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos().add(renovacaoArq);
            }
        }
    }

    public List<AnexoVeiculoOttDTO> getAnexosVeiculo(Long id) {
        List<AnexoVeiculoOttDTO> anexos = Lists.newArrayList();
        VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.recuperar(id);
        for (VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo : veiculo.getVeiculoOperadoraDetentorArquivos()) {
            anexos.add(veiculoOperadoraDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosVeiculo(VeiculoOttDTO veiculoOttDTO) throws Exception {
        VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.recuperar(veiculoOttDTO.getId());
        addAnexosVeiculo(veiculo, veiculoOttDTO.getAnexos());
        veiculo.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.RETORNO_DOCUMENTACAO);
        veiculoOperadoraTecnologiaTransporteFacade.salvar(veiculo);
    }

    public VeiculoOttDTO recuperarVeiculoOtt(Long id) {
        VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.recuperar(id);
        return veiculo.toDTO();
    }

    public RenovacaoVeiculoOttDTO recuperarRenovacaoVeiculoOtt(Long id) {
        RenovacaoVeiculoOTT renovacao = veiculoOperadoraTecnologiaTransporteFacade.recuperarRenovacao(id);
        return renovacao.toDTO();
    }

    public List<AnexoVeiculoOttDTO> getAnexosRenovacaoVeiculo(Long id) {
        List<AnexoVeiculoOttDTO> anexos = Lists.newArrayList();
        RenovacaoVeiculoOTT renovacao = veiculoOperadoraTecnologiaTransporteFacade.recuperarRenovacao(id);
        for (VeiculoRenovacaoDetentorArquivo veiculoRenovacaoDetentorArquivo : renovacao.getVeiculoRenovacaoDetentorArquivos()) {
            anexos.add(veiculoRenovacaoDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosRenovacaoVeiculo(RenovacaoVeiculoOttDTO renovacaoVeiculoOttDTO) throws Exception {
        RenovacaoVeiculoOTT renovacaoVeiculoOTT = veiculoOperadoraTecnologiaTransporteFacade.recuperarRenovacao(renovacaoVeiculoOttDTO.getId());
        addAnexosRenovacaoVeiculo(renovacaoVeiculoOTT, renovacaoVeiculoOttDTO.getAnexos());
        renovacaoVeiculoOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.RETORNO_DOCUMENTACAO);
        veiculoOperadoraTecnologiaTransporteFacade.salvarRenovacaoVeiculo(renovacaoVeiculoOTT);
    }

    public CondutorOttDTO recuperarCondutorOtt(Long id) {
        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.recuperar(id);
        return condutor.toDTO();
    }

    public List<AnexoCondutorOttDTO> getAnexosCondutor(Long id) {
        List<AnexoCondutorOttDTO> anexos = Lists.newArrayList();
        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.recuperar(id);
        for (CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo : condutor.getCondutorOperadoraDetentorArquivos()) {
            anexos.add(condutorOperadoraDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosCondutor(CondutorOttDTO condutorOttDTO) throws Exception {
        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.recuperar(condutorOttDTO.getId());
        addAnexosCondutor(condutor, condutorOttDTO.getAnexos());
        condutor.setSituacaoCondutorOTT(SituacaoCondutorOTT.RETORNO_DOCUMENTACAO);
        condutorOperadoraTecnologiaTransporteFacade.salvar(condutor);
    }

    public RenovacaoCondutorOttDTO recuperarRenovacaoCondutorOtt(Long id) {
        RenovacaoCondutorOTT renovacao = condutorOperadoraTecnologiaTransporteFacade.recuperarRenovacao(id);
        return renovacao.toDTO();
    }

    public List<AnexoCondutorOttDTO> getAnexosRenovacaoCondutor(Long id) {
        List<AnexoCondutorOttDTO> anexos = Lists.newArrayList();
        RenovacaoCondutorOTT renovacao = condutorOperadoraTecnologiaTransporteFacade.recuperarRenovacao(id);
        for (CondutorRenovacaoDetentorArquivo condutorRenovacaoDetentorArquivo : renovacao.getCondutorRenovacaoDetentorArquivos()) {
            anexos.add(condutorRenovacaoDetentorArquivo.toDTO());
        }
        return anexos;
    }

    public void reenviarAnexosRenovacaoCondutor(RenovacaoCondutorOttDTO renovacaoCondutorOttDTO) throws Exception {
        RenovacaoCondutorOTT renovacao = condutorOperadoraTecnologiaTransporteFacade.recuperarRenovacao(renovacaoCondutorOttDTO.getId());
        addAnexosRenovacaoCondutor(renovacao, renovacaoCondutorOttDTO.getAnexos());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.RETORNO_DOCUMENTACAO);
        condutorOperadoraTecnologiaTransporteFacade.salvarRenovacao(renovacao);
    }

    public ParametrosTransitoTransporteDTO recuperarParametroTransitoTransporte(TipoPermissaoRBTransDTO tipo) {
        ParametrosTransitoTransporte parametrosTransitoTransporte = parametrosTransitoTransporteFacade
            .recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.valueOf(tipo.name()));
        ParametrosTransitoTransporteDTO dto = new ParametrosTransitoTransporteDTO();
        dto.setTipoPermissao(tipo);
        dto.setLimiteIdade(parametrosTransitoTransporte.getLimiteIdade());
        return dto;
    }

    public ArquivoDTO getModeloAdesivoOtt() {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt.getDetentorArquivoComposicao() != null
            && parametrosOtt.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
            return parametrosOtt.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO();
        }
        return null;
    }

    public Boolean cnpjPermiteCadastroOTT(String cnpj) {
        return operadoraTecnologiaTransporteFacade.cnpjPermiteCadastroOTT(cnpj);
    }
}
