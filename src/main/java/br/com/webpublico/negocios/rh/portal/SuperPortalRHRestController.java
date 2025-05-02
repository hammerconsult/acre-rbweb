package br.com.webpublico.negocios.rh.portal;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.configuracao.DirfCodigo;
import br.com.webpublico.entidadesauxiliares.AssistenteComprovanteRendimentos;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteContraCheque;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistentePrevidenciaContribuicao;
import br.com.webpublico.exception.UsuarioWebNaoExistenteException;
import br.com.webpublico.exception.UsuarioWebProblemasCadastroException;
import br.com.webpublico.exception.UsuarioWebSenhaInvalidaException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.avaliacao.AvaliacaoRHFacade;
import br.com.webpublico.negocios.rh.avaliacao.MontagemAvaliacaoFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.ponto.SolicitacaoFaltasFacade;
import br.com.webpublico.negocios.rh.relatorio.PrevidenciaContribuicaoIndividualizadaFacade;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.singletons.CacheRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.WSFichaFinanceira;
import br.com.webpublico.ws.model.WSPeriodoAquisitivo;
import br.com.webpublico.ws.model.WSPessoa;
import br.com.webpublico.ws.model.WSVinculoFP;
import br.com.webpublico.ws.portalweb.WSPortal;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.InitialContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SuperPortalRHRestController extends WSPortal implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SuperPortalRHRestController.class);
    @Autowired
    protected SistemaService sistemaService;
    @Autowired
    public SingletonMetricas singletonMetricas;
    private VinculoFPFacade vinculoFPFacade;
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private MatriculaFPFacade matriculaFPFacade;
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private ComprovanteRendimentosFacade comprovanteRendimentosFacade;
    private ProgramacaoFeriasPortalFacade programacaoFeriasPortalFacade;
    private PortalContribunteFacade portalContribunteFacade;
    private ConfiguracaoRHFacade configuracaoRHFacade;
    private CidadeFacade cidadeFacade;
    private CamposAlteradosPortalFacade camposAlteradosPortalFacade;
    private LancamentoFPFacade lancamentoFPFacade;
    private PrevidenciaContribuicaoIndividualizadaFacade previdenciaContribuicaoIndividualizadaFacade;
    private CalendarioFPFacade calendarioFPFacade;
    private FeriadoFacade feriadoFacade;
    private SolicitacaoAfastamentoPortalFacade solicitacaoAfastamentoPortalFacade;
    private SolicitacaoFaltasFacade solicitacaoFaltasFacade;
    private ArquivoFacade arquivoFacade;
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private AfastamentoFacade afastamentoFacade;
    private AvaliacaoRHFacade avaliacaoRHFacade;
    private MontagemAvaliacaoFacade montagemAvaliacaoFacade;
    private CacheRH cacheRH;

    public static Logger getLogger() {
        return logger;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        if (vinculoFPFacade == null) {
            try {
                vinculoFPFacade = (VinculoFPFacade) new InitialContext().lookup("java:module/VinculoFPFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return vinculoFPFacade;
    }

    public AvaliacaoRHFacade getAvaliacaoRHFacade() {
        if (avaliacaoRHFacade == null) {
            try {
                avaliacaoRHFacade = (AvaliacaoRHFacade) new InitialContext().lookup("java:module/AvaliacaoRHFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return avaliacaoRHFacade;
    }
    public MontagemAvaliacaoFacade getMontagemAvaliacaoFacade() {
        if (montagemAvaliacaoFacade == null) {
            try {
                montagemAvaliacaoFacade = (MontagemAvaliacaoFacade) new InitialContext().lookup("java:module/MontagemAvaliacaoFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return montagemAvaliacaoFacade;
    }

    public LancamentoFPFacade getLancamentoFPFacade() {
        if (lancamentoFPFacade == null) {
            try {
                lancamentoFPFacade = (LancamentoFPFacade) new InitialContext().lookup("java:module/LancamentoFPFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return lancamentoFPFacade;
    }


    public CamposAlteradosPortalFacade getCamposAlteradosPortalFacade() {
        if (camposAlteradosPortalFacade == null) {
            try {
                camposAlteradosPortalFacade = (CamposAlteradosPortalFacade) new InitialContext().lookup("java:module/CamposAlteradosPortalFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return camposAlteradosPortalFacade;
    }

    public CidadeFacade getCidadeFacade() {
        if (cidadeFacade == null) {
            try {
                cidadeFacade = (CidadeFacade) new InitialContext().lookup("java:module/CidadeFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return cidadeFacade;
    }
    public CacheRH getCacheRH() {
        if (cacheRH == null) {
            try {
                cacheRH = (CacheRH) new InitialContext().lookup("java:module/CacheRH");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return cacheRH;
    }


    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        if (configuracaoRHFacade == null) {
            try {
                configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return configuracaoRHFacade;
    }

    public PortalContribunteFacade getPortalContribunteFacade() {
        if (portalContribunteFacade == null) {
            try {
                portalContribunteFacade = (PortalContribunteFacade) new InitialContext().lookup("java:module/PortalContribunteFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return portalContribunteFacade;
    }

    public ProgramacaoFeriasPortalFacade getProgramacaoFeriasPortalFacade() {
        if (programacaoFeriasPortalFacade == null) {
            try {
                programacaoFeriasPortalFacade = (ProgramacaoFeriasPortalFacade) new InitialContext().lookup("java:module/ProgramacaoFeriasPortalFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return programacaoFeriasPortalFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        if (fichaFinanceiraFPFacade == null) {
            try {
                fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return fichaFinanceiraFPFacade;
    }

    public PrevidenciaContribuicaoIndividualizadaFacade getPrevidenciaContribuicaoIndividualizadaFacade() {
        if (previdenciaContribuicaoIndividualizadaFacade == null) {
            try {
                previdenciaContribuicaoIndividualizadaFacade = (PrevidenciaContribuicaoIndividualizadaFacade) new InitialContext().lookup("java:module/PrevidenciaContribuicaoIndividualizadaFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return previdenciaContribuicaoIndividualizadaFacade;
    }


    public PeriodoAquisitivoFLFacade getPeriodoAquisitivoFLFacade() {
        if (periodoAquisitivoFLFacade == null) {
            try {
                periodoAquisitivoFLFacade = (PeriodoAquisitivoFLFacade) new InitialContext().lookup("java:module/PeriodoAquisitivoFLFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return periodoAquisitivoFLFacade;
    }

    public ComprovanteRendimentosFacade getComprovanteRendimentosFacade() {
        if (comprovanteRendimentosFacade == null) {
            try {
                comprovanteRendimentosFacade = (ComprovanteRendimentosFacade) new InitialContext().lookup("java:module/ComprovanteRendimentosFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return comprovanteRendimentosFacade;
    }

    public MatriculaFPFacade getMatriculaFPFacade() {
        if (matriculaFPFacade == null) {
            try {
                matriculaFPFacade = (MatriculaFPFacade) new InitialContext().lookup("java:module/MatriculaFPFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return matriculaFPFacade;
    }

    public List<WSVinculoFP> findServidoresPorPessoa(WSPessoa pessoa) throws UsuarioWebSenhaInvalidaException, UsuarioWebNaoExistenteException, UsuarioWebProblemasCadastroException {
        definirUsuarioAlternativo();
        List<VinculoFP> vinculoFPs = new ArrayList<>();
        long time;
        try {
            time = System.currentTimeMillis();
            logger.debug("pesquisando vinculos por pessoa " + pessoa.getNome());
            vinculoFPs = getVinculoFPFacade().listaTodosVinculosVigentes(pessoa.getNome());
            logger.debug("vinculos: " + vinculoFPs);
            logger.debug("tempo pra carregar os vinculos no webpublico: " + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WSVinculoFP.convertVinculoFPToWSVinculoFPList(vinculoFPs);
    }

    public List<WSFichaFinanceira> findFichaFinanceiras(WSVinculoFP wsVinculoFP, Integer ano) {
        definirUsuarioAlternativo();
        List<FichaFinanceiraFP> fichaFinanceiraFPs = getFichaFinanceiraFPFacade().recuperaFichaFinanceiraFPPorVinculoAndAno(wsVinculoFP.getId(), ano);
        List<WSFichaFinanceira> wsFichaFinanceiras = WSFichaFinanceira.convertFichaFinanceiraToWSFichaFinanceiraList(fichaFinanceiraFPs);
        ConfiguracaoRH configuracaoRH = getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null) {
            DirfCodigo dirfCodigoPorAno = getConfiguracaoRHFacade().getDirfCodigoPorAno(ano, configuracaoRH);
            if (dirfCodigoPorAno != null) {
                for (WSFichaFinanceira wsFichaFinanceira : wsFichaFinanceiras) {
                    wsFichaFinanceira.setPermiteEmitirInformeRendimentos(dirfCodigoPorAno.getPermiteEmitirInfRendimentos());
                }
            }
        }
        return wsFichaFinanceiras;
    }

    public byte[] gerarContacheque(WSFichaFinanceira wsFichaFinanceira) {
        definirUsuarioAlternativo();
        ByteArrayOutputStream baos = getFichaFinanceiraFPFacade().gerarContracheque(wsFichaFinanceira);
        return baos != null ? baos.toByteArray() : null;
    }

    public byte[] gerarContachequeNovo(WSFichaFinanceira wsFichaFinanceira) {
        definirUsuarioAlternativo();
        AssistenteContraCheque assistente = new AssistenteContraCheque();
        assistente.getWsFichaFinanceira().add(wsFichaFinanceira);
        ByteArrayOutputStream baos = getFichaFinanceiraFPFacade().gerarContrachequeNovo(assistente);
        return baos != null ? baos.toByteArray() : null;
    }

    public RelatorioDTO gerarDtoContrachequeNovo(WSFichaFinanceira wsFichaFinanceira) {
        definirUsuarioAlternativo();
        AssistenteContraCheque assistente = new AssistenteContraCheque();
        assistente.getWsFichaFinanceira().add(wsFichaFinanceira);
        RelatorioDTO dto = getFichaFinanceiraFPFacade().montarRelatorioDTO(assistente, null);
        return dto != null ? dto : null;
    }

    public RelatorioDTO gerarDTOComprovanteRendimentos(List<VinculoFP> vinculos, Integer ano) {
        definirUsuarioAlternativo();
        ConfiguracaoRH configuracaoRH = getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null) {
            DirfCodigo dirfCodigoPorAno = getConfiguracaoRHFacade().getDirfCodigoPorAno(ano, configuracaoRH);
            if (dirfCodigoPorAno != null) {
                if (!dirfCodigoPorAno.getPermiteEmitirInfRendimentos()) {
                    return null;
                }
            }
        }
        AssistenteComprovanteRendimentos assistente = preencherAssistenteComprovante(vinculos, ano);
        RelatorioDTO dto = getComprovanteRendimentosFacade().montarRelatorioDTO(assistente);
        return dto != null ? dto : null;
    }

    private AssistenteComprovanteRendimentos preencherAssistenteComprovante(List<VinculoFP> vinculos, Integer ano) {
        AssistenteComprovanteRendimentos assistente = new AssistenteComprovanteRendimentos();
        Exercicio anoCalendario = getComprovanteRendimentosFacade().getExercicioFacade().recuperarExercicioPeloAno(ano);
        assistente.setAnoCalendario(anoCalendario);
        assistente.getVinculos().addAll(vinculos);
        return assistente;
    }

    public RelatorioDTO gerarDTORegistroIndividualizadoContribuicao(WSVinculoFP WSVinculoFP) {
        definirUsuarioAlternativo();
        AssistentePrevidenciaContribuicao assistente = preencherAssistentePrevidencia(WSVinculoFP);
        RelatorioDTO dto = getPrevidenciaContribuicaoIndividualizadaFacade().montarRelatorioDTO(assistente);
        return dto != null ? dto : null;
    }

    private AssistentePrevidenciaContribuicao preencherAssistentePrevidencia(WSVinculoFP WSVinculoFP) {
        if (WSVinculoFP != null) {
            AssistentePrevidenciaContribuicao assistente = new AssistentePrevidenciaContribuicao();
            VinculoFP vinculo = null;
            if("multiplosVinculos".equals(WSVinculoFP.getNumero())){
                vinculo = getVinculoFPFacade().buscarVinculosParaMatricula(WSVinculoFP.getMatricula()).get(0);
            } else {
                vinculo = getVinculoFPFacade().recuperarVinculoPorMatriculaETipoOrNumero(WSVinculoFP.getMatricula(), WSVinculoFP.getNumero());
            }
            MatriculaFP matriculaFP = getMatriculaFPFacade().buscarMatriculaFPPorMatricula(WSVinculoFP.getMatricula());
            assistente.setMatriculaFP(matriculaFP);
            assistente.setContrato(WSVinculoFP.getNumero());
            assistente.setInicio(vinculo.getContratoFP().getDataAdmissao());
            assistente.setTermino(sistemaService.getDataOperacao());
            return assistente;
        }
        return null;
    }


    public byte[] gerarComprovanteRendimentos(Long idVinculo, Integer ano) throws IOException, JRException {
        definirUsuarioAlternativo();
        ConfiguracaoRH configuracaoRH = getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null) {
            DirfCodigo dirfCodigoPorAno = getConfiguracaoRHFacade().getDirfCodigoPorAno(ano, configuracaoRH);
            if (dirfCodigoPorAno != null) {
                if (!dirfCodigoPorAno.getPermiteEmitirInfRendimentos()) {
                    return null;
                }
            }
        }
        return getComprovanteRendimentosFacade().gerarRelatorioPortal(idVinculo, ano);
    }

    public List<WSPeriodoAquisitivo> findPeriodoAquisitivos(WSVinculoFP wsVinculoFP) {
        definirUsuarioAlternativo();
        return getPeriodoAquisitivoFLFacade().recuperarPeriodoAquisitivoPorVinculoPortal(wsVinculoFP);
    }

    public List<WSPeriodoAquisitivo> findPeriodoAquisitivosSemConcessao(WSVinculoFP wsVinculoFP) {
        definirUsuarioAlternativo();
        return getPeriodoAquisitivoFLFacade().recuperarPeriodoAquisitivoPorVinculoPortalSemConcessao(wsVinculoFP);
    }

    public void atualizarDadosPessoais(WSPessoa wsPessoa) {
        definirUsuarioAlternativo();
        getPortalContribunteFacade().atualizarDadosPessoaisPortal(wsPessoa);
    }

    public SistemaService getSistemaService() {
        return sistemaService;
    }

    public CalendarioFPFacade getCalendarioFPFacade() {
        if (calendarioFPFacade == null) {
            try {
                calendarioFPFacade = (CalendarioFPFacade) new InitialContext().lookup("java:module/CalendarioFPFacade");
            } catch (Exception e) {
                logger.error("erro: ", e);
            }
        }
        return calendarioFPFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        if (feriadoFacade == null) {
            try {
                feriadoFacade = (FeriadoFacade) new InitialContext().lookup("java:module/FeriadoFacade");
            } catch (Exception e) {
                logger.error("erro: ", e);
            }
        }
        return feriadoFacade;
    }

    public SolicitacaoAfastamentoPortalFacade getSolicitacaoAfastamentoPortalFacade() {
        if (solicitacaoAfastamentoPortalFacade == null) {
            try {
                solicitacaoAfastamentoPortalFacade = (SolicitacaoAfastamentoPortalFacade) new InitialContext().lookup("java:module/SolicitacaoAfastamentoPortalFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return solicitacaoAfastamentoPortalFacade;
    }

    public SolicitacaoFaltasFacade getSolicitacaoFaltasFacade() {
        if (solicitacaoFaltasFacade == null) {
            try {
                solicitacaoFaltasFacade = (SolicitacaoFaltasFacade) new InitialContext().lookup("java:module/SolicitacaoFaltasFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return solicitacaoFaltasFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        if (arquivoFacade == null) {
            try {
                arquivoFacade = (ArquivoFacade) new InitialContext().lookup("java:module/ArquivoFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return arquivoFacade;
    }

    public TipoAfastamentoFacade getTipoAfastamentoFacade() {
        if (tipoAfastamentoFacade == null) {
            try {
                tipoAfastamentoFacade = (TipoAfastamentoFacade) new InitialContext().lookup("java:module/TipoAfastamentoFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return tipoAfastamentoFacade;
    }

    public AfastamentoFacade getAfastamentoFacade() {
        if (afastamentoFacade == null) {
            try {
                afastamentoFacade = (AfastamentoFacade) new InitialContext().lookup("java:module/AfastamentoFacade");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return afastamentoFacade;
    }
}
