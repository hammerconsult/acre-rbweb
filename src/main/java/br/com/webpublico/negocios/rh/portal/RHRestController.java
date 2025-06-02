package br.com.webpublico.negocios.rh.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.avaliacao.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.ponto.RHSolicitacaoFaltas;
import br.com.webpublico.entidades.rh.ponto.SolicitacaoFaltas;
import br.com.webpublico.entidades.rh.portal.ProgramacaoFeriasPortal;
import br.com.webpublico.entidades.rh.portal.RHProgramacaoFeriasPortal;
import br.com.webpublico.entidades.rh.portal.RHSolicitacaoAfastamentoPortal;
import br.com.webpublico.entidades.rh.portal.SolicitacaoAfastamentoPortal;
import br.com.webpublico.entidadesauxiliares.rh.portal.*;
import br.com.webpublico.exception.UsuarioWebProblemasCadastroException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.pessoa.dto.CidadeDTO;
import br.com.webpublico.pessoa.dto.PessoaFisicaDTO;
import br.com.webpublico.rh.AvaliacaoRHDTO;
import br.com.webpublico.rh.NivelRespostaDTO;
import br.com.webpublico.rh.QuestaoAlternativaDTO;
import br.com.webpublico.rh.QuestaoAvaliacaoDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.GeradorChaveAutenticacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.*;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 01/11/2016  10:50.
 */
@Controller
@RequestMapping("/rh")
public class RHRestController extends SuperPortalRHRestController {

    private static final Logger logger = LoggerFactory.getLogger(RHRestController.class);


    private static final String BUSCAR_MATRICULA_PORTAL = "Buscar Matrícula para Contracheque";
    private static final String ALTERAR_DADOS_PESSOAIS = "Alterar Dados Pessoais";
    private static final String CONSULTA_HOLERITE = "Consulta de Ficha Financeira Por Ano";
    private static final String CONSULTA_PERIODO_AQUISITIVO = "Consulta de Período Aquisitivo";
    private static final String CONSULTA_PERIODO_AQUISITIVO_SEM_CONCESSAO = "Consulta de Periodo Aquisitivo Sem Concessão";
    private static final String MONTAR_FITLRO_CONTRACHEQUE = "Buscar e Montar Filtro de Matricula - Contracheque";
    private static final String SALVAR_PROGRAMACAO_FERIAS_PORTAL = "Salvar Programação de Férias";
    private static final String SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL = "Salvar Programação de Férias";
    private static final String IMPRESSAO_HOLERITE = "Impressão de Holerite";
    private static final String IMPRESSAO_CONTRACHEQUE = "Impressão de Contracheque";
    private static final String COMPROVANTE_RENDIMENTOS = "Impressão do Comprovante de Rendimentos";


    @RequestMapping(value = "/holerites",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSFichaFinanceira>> buscarHoleritesPorVinculo(@RequestBody WSFichaFinanceira ficha) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), CONSULTA_HOLERITE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        ConfiguracaoRH configuracaoRH = getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null) {
            if (!configuracaoRH.getMostrarDadosRHPortal()) {
                singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONSULTA_HOLERITE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
                return new ResponseEntity<List<WSFichaFinanceira>>(new ArrayList<WSFichaFinanceira>(), HttpStatus.OK);
            }
        }
        List<WSFichaFinanceira> fichaFinanceiras = findFichaFinanceiras(ficha.getWsVinculoFP(), ficha.getAno());
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONSULTA_HOLERITE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(fichaFinanceiras, HttpStatus.OK);
    }

    @RequestMapping(value = "/periodo-aquisitivo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSPeriodoAquisitivo>> buscarPeriodosAquisitivos(@RequestBody WSVinculoFP vinculoFP) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), CONSULTA_PERIODO_AQUISITIVO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<WSPeriodoAquisitivo> periodos = findPeriodoAquisitivos(vinculoFP);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONSULTA_PERIODO_AQUISITIVO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(periodos, HttpStatus.OK);
    }

    @RequestMapping(value = "/periodos-aquisitivo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSPeriodoAquisitivo>> buscarPeriodosAquisitivosSemConcessao(@RequestBody WSVinculoFP vinculoFP) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), CONSULTA_PERIODO_AQUISITIVO_SEM_CONCESSAO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<WSPeriodoAquisitivo> periodos = findPeriodoAquisitivosSemConcessao(vinculoFP);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONSULTA_PERIODO_AQUISITIVO_SEM_CONCESSAO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(periodos, HttpStatus.OK);
    }

    @RequestMapping(value = "/programacao-ferias",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RHProgramacaoFeriasPortal> salvarProgramacaoFeriasPortal(@RequestBody RHProgramacaoFeriasPortal programacaoFeriasPortal) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), SALVAR_PROGRAMACAO_FERIAS_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        ProgramacaoFeriasPortal programacaoPortal = transformarProgramacao(programacaoFeriasPortal);
        if (programacaoPortal != null) {
            getProgramacaoFeriasPortalFacade().salvar(programacaoPortal);
        } else {
            logger.debug("Erro ao tentar persistir a programação de férias do portal {} ", programacaoPortal);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_PROGRAMACAO_FERIAS_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            new ResponseEntity<>(programacaoFeriasPortal, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_PROGRAMACAO_FERIAS_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(programacaoFeriasPortal, HttpStatus.OK);
    }


    private ProgramacaoFeriasPortal transformarProgramacao(RHProgramacaoFeriasPortal programacaoPortal) {
        ProgramacaoFeriasPortal rh = new ProgramacaoFeriasPortal();
        if (programacaoPortal.getPeriodoAquisitivoFLId() != null) {
            PeriodoAquisitivoFL periodo = getPeriodoAquisitivoFLFacade().recuperar(programacaoPortal.getPeriodoAquisitivoFLId());
            if (periodo != null) {
                rh.setId(programacaoPortal.getId());
                rh.setDataInicio(programacaoPortal.getDataInicio());
                rh.setDataFim(programacaoPortal.getDataFim());
                rh.setDiasAbono(programacaoPortal.getDiasAbono());
                rh.setAbonoPecunia(programacaoPortal.getAbonoPecunia());
                rh.setPeriodoAquisitivoFL(periodo);
                return rh;
            }
        }
        return null;
    }

    @RequestMapping(value = "/solicitacao-afastamento",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RHSolicitacaoAfastamentoPortal> salvarSolicitacaoAfastamento(@RequestBody RHSolicitacaoAfastamentoPortal rhSolicitacaoAfastamentoPortal) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        SolicitacaoAfastamentoPortal solicitacaoAfastamentoPortal = transformarSolicitacaoAfastamento(rhSolicitacaoAfastamentoPortal);
        if (solicitacaoAfastamentoPortal != null) {
            try {
                validarPeriodoSolicitacaoConcomitante(solicitacaoAfastamentoPortal);
                validarPeriodoAfastamentoConcomitante(solicitacaoAfastamentoPortal);
            } catch (ValidacaoException ve) {
                String mensagem = ve.getMensagens().get(0).getDetail();
                rhSolicitacaoAfastamentoPortal.setMensagemErro(mensagem);
                return new ResponseEntity<>(rhSolicitacaoAfastamentoPortal, HttpStatus.OK);
            }
            getSolicitacaoAfastamentoPortalFacade().salvarECriarNotificacao(solicitacaoAfastamentoPortal);
        } else {
            logger.debug("Erro ao tentar persistir a solicitação de afastamento do portal {} ", solicitacaoAfastamentoPortal);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            new ResponseEntity<>(rhSolicitacaoAfastamentoPortal, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(rhSolicitacaoAfastamentoPortal, HttpStatus.OK);
    }

    public void validarPeriodoSolicitacaoConcomitante(SolicitacaoAfastamentoPortal solicitacaoAfastamentoPortal) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        SolicitacaoAfastamentoPortal solicitacaoConcomitante = getSolicitacaoAfastamentoPortalFacade().buscarSolicitacaoConcomitanteComStatusEmAnaliseOuAprovado(solicitacaoAfastamentoPortal);
        if (solicitacaoConcomitante != null) {
            String mensagem = "Já existe Solicitação de Afastamento cadastrada no periodo "
                + DataUtil.getDataFormatada(solicitacaoConcomitante.getDataInicio())
                + " à "
                + DataUtil.getDataFormatada(solicitacaoConcomitante.getDataFim())
                + " com status: " + solicitacaoConcomitante.getStatus().getDescricao()
                + ". O sistema não permite solicitações em análise ou aprovada com data concomitante.";
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        ve.lancarException();
    }

    public void validarPeriodoAfastamentoConcomitante(SolicitacaoAfastamentoPortal solicitacaoAfastamentoPortal) throws ValidacaoException {
        Afastamento afastamento = new Afastamento();
        afastamento.setContratoFP(solicitacaoAfastamentoPortal.getContratoFP());
        afastamento.setTipoAfastamento(solicitacaoAfastamentoPortal.getTipoAfastamento());
        afastamento.setDataCadastro(sistemaService.getDataOperacao());
        afastamento.setInicio(solicitacaoAfastamentoPortal.getDataInicio());
        afastamento.setTermino(solicitacaoAfastamentoPortal.getDataFim());
        Util.validarCampos(afastamento);
        getAfastamentoFacade().validarCampos(afastamento);
    }

    @RequestMapping(value = "/get-tipos-afastamentos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoAfastamento>> buscarTiposAfastamentos() {
        try {
            List<WSTipoAfastamento> tipos = Lists.newArrayList();
            for (TipoAfastamento tipoAfastamento : getTipoAfastamentoFacade().tiposAfastamentosAtivos()) {
                tipos.add(new WSTipoAfastamento(tipoAfastamento));
            }
            return new ResponseEntity<>(tipos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/get-solicitacoes-afastamentos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RHSolicitacaoAfastamentoPortal>> buscarSolicitacoesAfastamentos(@RequestParam(value = "vinculo-id", required = true) Long vinculoId) {
        try {
            List<RHSolicitacaoAfastamentoPortal> rhSolicitacaoAfastamentoPortals = Lists.<RHSolicitacaoAfastamentoPortal>newArrayList();
            for (SolicitacaoAfastamentoPortal solicitacaoAfastamentoPortal : getSolicitacaoAfastamentoPortalFacade().buscarSolicitacoesAfastamentos(vinculoId)) {
                RHSolicitacaoAfastamentoPortal dto = transformarSolicitacaoAfastamentoDto(solicitacaoAfastamentoPortal);
                rhSolicitacaoAfastamentoPortals.add(dto);
            }
            return new ResponseEntity<>(rhSolicitacaoAfastamentoPortals, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private SolicitacaoAfastamentoPortal transformarSolicitacaoAfastamento(RHSolicitacaoAfastamentoPortal dto) {
        if (dto != null) {
            SolicitacaoAfastamentoPortal solicitacao = new SolicitacaoAfastamentoPortal();
            solicitacao.setId(dto.getId());
            solicitacao.setCriadaEm(sistemaService.getDataOperacao());
            solicitacao.setDataInicio(dto.getDataInicio());
            solicitacao.setDataFim(dto.getDataFim());
            solicitacao.setStatus(dto.getStatus());
            solicitacao.setMotivoRejeicao(dto.getMotivoRejeicao());
            if (dto.getTipoAfastamento() != null && dto.getTipoAfastamento().getId() != null) {
                solicitacao.setTipoAfastamento(getTipoAfastamentoFacade().recuperar(dto.getTipoAfastamento().getId()));
            }
            if (!dto.getAnexos().isEmpty()) {
                DetentorArquivoComposicao detentorArquivoComposicao = criarDetentorArquivoComposicao(dto.getAnexos());
                solicitacao.setDetentorArquivoComposicao(detentorArquivoComposicao);
            }
            solicitacao.setContratoFP((ContratoFP) getVinculoFPFacade().recuperar(dto.getVinculoFP().getId()));
            return solicitacao;
        }
        return null;
    }

    private RHSolicitacaoAfastamentoPortal transformarSolicitacaoAfastamentoDto(SolicitacaoAfastamentoPortal solicitacao) {
        RHSolicitacaoAfastamentoPortal dto = new RHSolicitacaoAfastamentoPortal();
        dto.setId(solicitacao.getId());
        dto.setDataInicio(solicitacao.getDataInicio());
        dto.setDataFim(solicitacao.getDataFim());
        dto.setTipoAfastamento(new WSTipoAfastamento(solicitacao.getTipoAfastamento()));
        dto.setStatus(solicitacao.getStatus());
        dto.setMotivoRejeicao(solicitacao.getMotivoRejeicao());
        dto.setAnexos(Lists.<ArquivoDTO>newArrayList());
        if (solicitacao.getDetentorArquivoComposicao() != null) {
            for (ArquivoComposicao arquivoComposicao : solicitacao.getDetentorArquivoComposicao().getArquivosComposicao()) {
                ArquivoDTO arquivoDTO = arquivoComposicao.getArquivo().toArquivoDTO();
                dto.getAnexos().add(arquivoDTO);
            }
        }
        return dto;
    }

    private DetentorArquivoComposicao criarDetentorArquivoComposicao(List<ArquivoDTO> anexos) {
        DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
        try {
            for (ArquivoDTO arquivoDTO : anexos) {
                Arquivo arquivo = Arquivo.toArquivo(arquivoDTO);
                ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                arquivoComposicao.setDataUpload(new Date());
                arquivoComposicao.setArquivo(arquivo);
                arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                detentorArquivoComposicao.setArquivoComposicao(arquivoComposicao);
                detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return detentorArquivoComposicao;
    }

    @RequestMapping(value = "/comprovante-rendimentos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RelatorioDTO> gerarRelatorioComprovanteRendimento(@RequestParam(value = "id", required = true) Long idVinculo,
                                                                            @RequestParam(value = "ano", required = true) Integer ano) {
        try {
            VinculoFP vinculoFP = getVinculoFPFacade().recuperarSimples(idVinculo);
            List<VinculoFP> vinculoFPS = getVinculoFPFacade().buscarTodosVinculosPorPessoaVigentesNoAno(vinculoFP.getMatriculaFP().getPessoa().getId(), ano);

            RelatorioDTO dto = gerarDTOComprovanteRendimentos(vinculoFPS, ano);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /* chamada do aplicativo do servidor */
    @RequestMapping(value = "/comprovante-rendimentos-base64",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarRelatorioComprovanteRendimentosBase64(@RequestParam(value = "id", required = true) Long idVinculo,
                                                                             @RequestParam(value = "ano", required = true) Integer ano) throws IOException, JRException {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), COMPROVANTE_RENDIMENTOS, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = gerarComprovanteRendimentos(idVinculo, ano);
        Base64 codec = new Base64();
        if (bytes == null) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        String encoded = codec.encodeBase64String(bytes);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), COMPROVANTE_RENDIMENTOS, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(encoded, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-dias-documentacao-portal",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> buscarQuantidadeDiasApresentacaoDocumentacaoPortal() {
        Integer dias = getConfiguracaoRHFacade().buscarQuantidadeDiasDocumentacaoPortal();
        return new ResponseEntity<>(dias, HttpStatus.OK);
    }

    @RequestMapping(value = "/impressao-contracheque",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarContracheque(@RequestBody WSFichaFinanceira ficha) {
        String usuarioAlternativo = ficha.getWsVinculoFP() != null ? ficha.getWsVinculoFP().getMatricula() + "/" + ficha.getWsVinculoFP().getNumero() : PORTAL_WEB;
        definirUsuarioAlternativo(usuarioAlternativo);
        singletonMetricas.iniciarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] relatorio = gerarContacheque(ficha);
        singletonMetricas.finalizarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(relatorio, HttpStatus.OK);
    }

    /* chamada do aplicativo do servidor */
    @RequestMapping(value = "/impressao-contracheque-base64",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarContrachequeBase64(@RequestBody WSFichaFinanceira ficha) {
        String usuarioAlternativo = ficha.getWsVinculoFP() != null ? ficha.getWsVinculoFP().getMatricula() + "/" + ficha.getWsVinculoFP().getNumero() : PORTAL_WEB;
        definirUsuarioAlternativo(usuarioAlternativo);
        singletonMetricas.iniciarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] relatorio = gerarContacheque(ficha);
        Base64 codec = new Base64();
        if (relatorio == null) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        String encoded = codec.encodeBase64String(relatorio);
        singletonMetricas.finalizarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(encoded, HttpStatus.OK);
    }

    @RequestMapping(value = "/impressao-contracheque-novo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RelatorioDTO> gerarContrachequeNovo(@RequestBody WSFichaFinanceira ficha) {
        String usuarioAlternativo = ficha.getWsVinculoFP() != null ? ficha.getWsVinculoFP().getMatricula() + "/" + ficha.getWsVinculoFP().getNumero() : PORTAL_WEB;
        definirUsuarioAlternativo(usuarioAlternativo);
        singletonMetricas.iniciarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        RelatorioDTO relatorio = gerarDtoContrachequeNovo(ficha);
        singletonMetricas.finalizarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(relatorio, HttpStatus.OK);
    }

    /* chamada do aplicativo do servidor */
    @RequestMapping(value = "/impressao-contracheque-novo-base64",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarContrachequeNovoBase64(@RequestBody WSFichaFinanceira ficha) {
        String usuarioAlternativo = ficha.getWsVinculoFP() != null ? ficha.getWsVinculoFP().getMatricula() + "/" + ficha.getWsVinculoFP().getNumero() : PORTAL_WEB;
        definirUsuarioAlternativo(usuarioAlternativo);
        singletonMetricas.iniciarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] relatorio = gerarContachequeNovo(ficha);
        Base64 codec = new Base64();
        if (relatorio == null) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        String encoded = codec.encodeBase64String(relatorio);
        singletonMetricas.finalizarMetrica(usuarioAlternativo, IMPRESSAO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(encoded, HttpStatus.OK);
    }


    @RequestMapping(value = "/consignacao-servidor",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ConsignacaoServidorDTO> buscarConsignacao(@RequestBody WSFichaFinanceira ficha) {
        String usuarioAlternativo = ficha.getWsVinculoFP() != null ? ficha.getWsVinculoFP().getMatricula() + "/" + ficha.getWsVinculoFP().getNumero() : PORTAL_WEB;
        definirUsuarioAlternativo(usuarioAlternativo);
        VinculoFP vinculoFP = new VinculoFP();
        vinculoFP.setId(ficha.getWsVinculoFP().getId());
        ConsignacaoServidorDTO consignacao = getLancamentoFPFacade().buscarConsignacaoPorServidorMesAno(ficha.getMes(), ficha.getAno(), ficha.getTipoFolhaDePagamento(), vinculoFP);
        return new ResponseEntity<>(consignacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/atualizar-foto",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity atualizarFotoServidor(@RequestBody FotoPessoaDTO foto) {
        String usuarioAlternativo = PORTAL_WEB + " Atualização da Foto";
        definirUsuarioAlternativo(usuarioAlternativo);
        getPortalContribunteFacade().atualizarFotoPessoa(foto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/calendario-pagamentos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ItemCalendarioFPDTO>> buscarConsignacao() {
        DateTime dateTime = new DateTime();
        List<ItemCalendarioFPDTO> itemCalendarioFPDTOS = getCalendarioFPFacade().buscarCalendarioDTO(dateTime.getYear());
        return new ResponseEntity<>(itemCalendarioFPDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/calendario-feriados",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<FeriadoDTO>> buscarFeriados(@RequestParam(value = "ano", required = false) Integer ano) {
        List<FeriadoDTO> feriados = getFeriadoFacade().buscarFeriadosPorAnoPortal(ano != null ? ano : new DateTime().getYear());
        return new ResponseEntity<>(feriados, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-chave-autenticacao-portal",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarChaveAutenticacaoPortal(@RequestBody String preChave) {
        String chave = GeradorChaveAutenticacao.geraChaveDeAutenticacao(preChave, GeradorChaveAutenticacao.TipoAutenticacao.DOCUMENTO_OFICIAL);
        chave = GeradorChaveAutenticacao.formataChaveDeAutenticacao(chave);
        return new ResponseEntity<>(chave, HttpStatus.OK);
    }


    @RequestMapping(value = "/alterar-dados-pessoais",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSPessoa> atualizarDadosCadastraisPessoa(@RequestBody WSPessoa pessoa) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), ALTERAR_DADOS_PESSOAIS, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        atualizarDadosPessoais(pessoa);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), ALTERAR_DADOS_PESSOAIS, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(pessoa, HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-matricula",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSFichaFinanceira>> atualizarDadosCadastraisPessoa(@RequestBody FiltroContracheque filtro) {
        definirUsuarioAlternativo();
        singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.CONTRA_CHEQUE, filtro.getCpf());
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_MATRICULA_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        ConfiguracaoRH configuracaoRH = getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente();
        if (configuracaoRH != null) {
            if (!configuracaoRH.getMostrarDadosRHPortal()) {
                singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_MATRICULA_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
                return new ResponseEntity<List<WSFichaFinanceira>>(new ArrayList<WSFichaFinanceira>(), HttpStatus.OK);
            }
        }
        Integer anoAtual = DateTime.now().getYear();
        List<FichaFinanceiraFP> fichaFinanceiraFPS = getFichaFinanceiraFPFacade().buscarFichaPorCpf(filtro.getCpf(), anoAtual);
        if (fichaFinanceiraFPS == null || fichaFinanceiraFPS.isEmpty()) {
            while ((fichaFinanceiraFPS == null || fichaFinanceiraFPS.isEmpty()) && anoAtual > DateTime.now().getYear() - 10) {
                anoAtual = anoAtual - 1;
                fichaFinanceiraFPS = getFichaFinanceiraFPFacade().buscarFichaPorCpf(filtro.getCpf(), anoAtual);
            }
        }
        List<WSFichaFinanceira> wsFichaFinanceiras = WSFichaFinanceira.convertFichaFinanceiraToWSFichaFinanceiraList(fichaFinanceiraFPS);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_MATRICULA_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(wsFichaFinanceiras, HttpStatus.OK);
    }

    @RequestMapping(value = "/montar-filtro",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSPessoa> montarFiltro(@RequestBody FiltroContracheque filtro) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), MONTAR_FITLRO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        PessoaFisica pessoa = getPortalContribunteFacade().buscarQualquerPessoaPorCpfAndMatricula(filtro.getCpf(), filtro.getMatricula());
        if (pessoa != null) {
            WSPessoa wsPessoa = new WSPessoa();
            wsPessoa.setId(pessoa.getId());
            wsPessoa.setNome(pessoa.getNome());
            wsPessoa.setNomeMae(pessoa.getMae());
            wsPessoa.setNascimento(pessoa.getDataNascimento());
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), MONTAR_FITLRO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            return new ResponseEntity(wsPessoa, HttpStatus.OK);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), MONTAR_FITLRO_CONTRACHEQUE, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/buscar-por-cpf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> buscarCpfServidor(@RequestParam(value = "cpf", required = true) String cpf) {
        try {
            PessoaFisicaDTO pessoaFisicaDTO = getPortalContribunteFacade().buscarPessoaFisicaPorCPF(cpf);
            return new ResponseEntity<>(pessoaFisicaDTO, HttpStatus.OK);
        } catch (UsuarioWebProblemasCadastroException e) {
            logger.error("Erro: ", e);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("erro", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/buscar-pessoa-por-cpf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PessoaFisicaDTO> buscarPessoaPorCpf(@RequestParam(value = "cpf", required = true) String cpf) {
        try {
            PessoaFisicaDTO pessoaFisicaDTO = getPortalContribunteFacade().buscarPessoaFisicaPorCpf(cpf);
            return new ResponseEntity<>(pessoaFisicaDTO, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/buscar-configuracao-rh",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ConfiguracaoRHDTO> buscarConfiguracaoRH() {
        try {
            ConfiguracaoRHDTO configuracaoRHDTO = getPortalContribunteFacade().buscarConfiguracaoRHDTO();
            return new ResponseEntity<>(configuracaoRHDTO, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/get-cidades",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CidadeDTO>> buscarCidades() {
        try {
            if(getCacheRH().getCidades() == null){
                List<Cidade> cidades = getCidadeFacade().lista();
                getCacheRH().setCidades(cidades);
            }
            return new ResponseEntity<>(Cidade.toCidadesDTO(getCacheRH().getCidades()), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/get-cidades-por-estado",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CidadeDTO>> buscarCidades(@RequestParam(value = "id_uf", required = true) Long idEstado) {
        try {
            if(!getCacheRH().getCidadesPorEstado().containsKey(idEstado)){
                List<Cidade> cidades = getCidadeFacade().buscarCidadesPorEstado(idEstado);
                getCacheRH().getCidadesPorEstado().put(idEstado, cidades);
            }
            return new ResponseEntity<>(Cidade.toCidadesDTO(getCacheRH().getCidadesPorEstado().get(idEstado)), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/salvar-campos-alterados",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CamposPortal> gravarCamposPortal(@RequestBody CamposPortal campos) {
        getCamposAlteradosPortalFacade().salvarCamposAlterados(campos);
        return new ResponseEntity<>(campos, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-campos-alterados",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CamposPortal> buscarCamposAlterados(@RequestBody CamposPortal campos) {
        try {
            return new ResponseEntity<>(getCamposAlteradosPortalFacade().buscarCamposSalvos(campos.getIdPessoa()), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/get-campos-alterados-dependentes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<CampoModificadoDependente>> buscarCamposAlteradosDependentes(@RequestBody CamposPortal campos) {
        try {
            return new ResponseEntity<>(getCamposAlteradosPortalFacade().buscarCamposSalvosDependentes(campos.getIdPessoa()), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/relatorio-dto-registro-previdenciario-individualizado",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RelatorioDTO> gerarDTORegistroIndividualizado(@RequestBody WSVinculoFP WSVinculoFP) {
        try {
            RelatorioDTO dto = gerarDTORegistroIndividualizadoContribuicao(WSVinculoFP);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/vinculo-fp-por-id",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSVinculoFP> buscarVinculoPorId(@RequestParam(value = "id", required = true) Long idVinculo) {
        try {
            WSVinculoFP wsVinculo = new WSVinculoFP();
            VinculoFP vinculo = getVinculoFPFacade().recuperarSimples(idVinculo);
            if (vinculo != null) {
                wsVinculo.setId(vinculo.getId());
                wsVinculo.setMatricula(vinculo.getMatriculaFP().getMatricula());
                wsVinculo.setNumero(vinculo.getNumero());
                return new ResponseEntity<>(wsVinculo, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return null;
    }

    @RequestMapping(value = "/vinculo-fp-detalhe-por-id",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VinculoFPDTODetalhe> buscarVinculoDetalhePorId(@RequestParam(value = "id", required = true) Long idVinculo) {
        try {
            VinculoFPDTODetalhe vinculo = getVinculoFPFacade().buscarDetalhesServidor(idVinculo, true);
            if (vinculo != null) {
                return new ResponseEntity<>(vinculo, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return null;
    }

    @RequestMapping(value = "/v2/vinculo-fp-detalhe-por-id",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<VinculoFPDTODetalhe> buscarVinculoDetalheV2PorId(@RequestParam(value = "id", required = true) Long idVinculo) {
        try {
            VinculoFPDTODetalhe vinculo = getVinculoFPFacade().buscarDetalhesServidor(idVinculo, false);
            if (vinculo != null) {
                return new ResponseEntity<>(vinculo, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return null;
    }

    @RequestMapping(value = "/vinculofp-historico-funcional",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<VinculoFPDTOHistorico>> buscarVinculoHistoricoPorId(@RequestParam(value = "id", required = false) Long idVinculo,
                                                                                   @RequestParam(value = "matricula", required = false) String matricula,
                                                                                   @RequestParam(value = "cpf", required = false) String cpf) {
        try {
            List<VinculoFPDTOHistorico> vinculoFPDTOHistoricos = getVinculoFPFacade().buscarHistoricoLotacao(idVinculo, matricula, cpf);
            if (vinculoFPDTOHistoricos != null) {
                return new ResponseEntity<>(vinculoFPDTOHistoricos, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/contrato-fp-modalidade-concursado",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> verificarContratoPorModalidade(@RequestParam(value = "id", required = true) Long idPessoa) {
        try {
            boolean modalidade = getPortalContribunteFacade().getContratoFPFacade().isContratoFpPorModalidade(idPessoa, ModalidadeContratoFP.CONCURSADOS);
            return new ResponseEntity<>(modalidade, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/contratos-fp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSVinculoFP>> buscarContratosFP(@RequestBody WSPessoa wsPessoa) {
        List<WSVinculoFP> wsVinculos = Lists.newLinkedList();
        try {
            List<VinculoFP> vinculoFPS = getPortalContribunteFacade().getPessoaFacade().getVinculoFPFacade().listaTodosVinculosPorPessoa(wsPessoa.getId());
            wsVinculos = WSVinculoFP.convertVinculoFPToWSVinculoFPList(vinculoFPS);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(wsVinculos, HttpStatus.OK);
    }


    @RequestMapping(value = "/gravar-resposta",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> gravarResposta(@RequestBody QuestaoAlternativaDTO questaoAlternativa) {
        definirUsuarioAlternativo();
        VinculoFP vinculoFP = new VinculoFP();
        vinculoFP.setId(questaoAlternativa.getIdVinculo());
        AvaliacaoVinculoFP avaliacao = getAvaliacaoRHFacade().recuperarAvaliacaoVinculo(questaoAlternativa.getIdAvaliacaoVinculo());
        RespostaAvaliacao respostaAvaliacao = getRespostaAvaliacao(questaoAlternativa, avaliacao);
        getAvaliacaoRHFacade().gravarResposta(respostaAvaliacao);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private RespostaAvaliacao getRespostaAvaliacao(QuestaoAlternativaDTO questaoAlternativa, AvaliacaoVinculoFP avaliacao) {
        RespostaAvaliacao respostaAvaliacao = new RespostaAvaliacao();
        respostaAvaliacao.setAvaliacaoVinculoFP(avaliacao);

        QuestaoAvaliacao questao = new QuestaoAvaliacao();
        questao.setId(questaoAlternativa.getIdQuestaoAvaliacao());
        respostaAvaliacao.setQuestaoAvaliacao(questao);

        NivelResposta nivelResposta = new NivelResposta();
        nivelResposta.setId(questaoAlternativa.getNivelResposta().getId());
        respostaAvaliacao.setNivelResposta(nivelResposta);

        avaliacao.getRespostas().add(respostaAvaliacao);

        return respostaAvaliacao;
    }

    @RequestMapping(value = "/avaliacao-servidor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AvaliacaoRHDTO> buscarAvaliacaoServidor(@RequestParam(value = "id", required = true) Long idAvaliacao) {
        definirUsuarioAlternativo();
        List<AvaliacaoRHDTO> avaliacaoes = Lists.newArrayList();
        List<AvaliacaoVinculoFP> avaliacaoesVinculos = getAvaliacaoRHFacade().buscarAvaliacaoVinculo(idAvaliacao);
        avaliacaoes = buscarAvaliacoesVinculo(avaliacaoesVinculos);
        if (avaliacaoes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(avaliacaoes.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/avaliacoes-servidor",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AvaliacaoRHDTO>> buscarAvaliacoesServidor(@RequestParam(value = "idPessoa", required = true) Long idPessoa) {
        definirUsuarioAlternativo();
        List<AvaliacaoRHDTO> avaliacaoes = Lists.newArrayList();
        List<AvaliacaoVinculoFP> avaliacaoesVinculos = getAvaliacaoRHFacade().buscarAvaliacaoVinculos(idPessoa);

        avaliacaoes = buscarAvaliacoesVinculo(avaliacaoesVinculos);
        return new ResponseEntity<>(avaliacaoes, HttpStatus.OK);
    }

    private List<AvaliacaoRHDTO> buscarAvaliacoesVinculo(List<AvaliacaoVinculoFP> avaliacaoesVinculos) {
        List<AvaliacaoRHDTO> avaliacaoes = Lists.newArrayList();
        for (AvaliacaoVinculoFP avaliacao : avaliacaoesVinculos) {
            AvaliacaoRHDTO dto = new AvaliacaoRHDTO();
            dto.setId(avaliacao.getAvaliacaoRH().getId());
            dto.setIdVinculo(avaliacao.getVinculoFP().getId());
            dto.setJaRespondida(getAvaliacaoRHFacade().verificarAvaliacaoRespondida(avaliacao));
            dto.setIdAvaliacaoVinculo(avaliacao.getId());
            dto.setNomeMontagemAvaliacao(avaliacao.getAvaliacaoRH().getMontagemAvaliacao().getNome());
            dto.setNome(avaliacao.getAvaliacaoRH().getNome());
            MontagemAvaliacao montagemAvaliacao = getMontagemAvaliacaoFacade().recuperar(avaliacao.getAvaliacaoRH().getMontagemAvaliacao().getId());
            convertToQuestaoDTO(dto, montagemAvaliacao);
            avaliacaoes.add(dto);
        }
        return avaliacaoes;
    }


    private void convertToQuestaoDTO(AvaliacaoRHDTO dto, MontagemAvaliacao montagemAvaliacao) {
        for (QuestaoAvaliacao questao : montagemAvaliacao.getQuestoes()) {
            QuestaoAvaliacaoDTO questaoDTO = new QuestaoAvaliacaoDTO();
            questaoDTO.setId(questao.getId());
            questaoDTO.setQuestao(questao.getQuestao());
            questaoDTO.setOrdem(questao.getOrdem());
            for (QuestaoAlternativa alternativa : questao.getAlternativas()) {
                convertToQuestaoDTO(questaoDTO, alternativa);
            }
            dto.getQuestoes().add(questaoDTO);
        }
    }

    private void convertToQuestaoDTO(QuestaoAvaliacaoDTO questaoDTO, QuestaoAlternativa alternativa) {
        QuestaoAlternativaDTO alternativaDTO = new QuestaoAlternativaDTO();
        alternativaDTO.setId(alternativa.getId());
        alternativaDTO.setIdQuestaoAvaliacao(questaoDTO.getId());
        alternativaDTO.setDescricao(alternativa.getDescricao());
        NivelRespostaDTO nivelDTO = convertToNivelRespostaDTO(alternativa);
        alternativaDTO.setNivelResposta(nivelDTO);
        questaoDTO.getAlternativas().add(alternativaDTO);
    }

    private NivelRespostaDTO convertToNivelRespostaDTO(QuestaoAlternativa alternativa) {
        NivelRespostaDTO nivelDTO = new NivelRespostaDTO();
        nivelDTO.setId(alternativa.getNivelResposta().getId());
        nivelDTO.setNome(alternativa.getNivelResposta().getNome());
        nivelDTO.setPercentualInicio(alternativa.getNivelResposta().getPercentualInicio());
        nivelDTO.setPercentualFim(alternativa.getNivelResposta().getPercentualFim());
        return nivelDTO;
    }

    @RequestMapping(value = "/solicitacao-faltas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RHSolicitacaoFaltas> salvarSolicitacaoFaltas(@RequestBody RHSolicitacaoFaltas rhSolicitacaoFaltas) {
        definirUsuarioAlternativo();
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        SolicitacaoFaltas solicitacaoFaltas = solicitacaoFaltasToFaltas(rhSolicitacaoFaltas);
        if (solicitacaoFaltas != null) {
            solicitacaoFaltas= getSolicitacaoFaltasFacade().salvarECriarNotificacao(solicitacaoFaltas);
            rhSolicitacaoFaltas.setId(solicitacaoFaltas.getId());
        } else {
            logger.debug("Erro ao tentar persistir a solicitação de afastamento do portal {} ", solicitacaoFaltas);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            new ResponseEntity<>(rhSolicitacaoFaltas, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), SALVAR_SOLICITACAO_AFASTAMENTO_PORTAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(rhSolicitacaoFaltas, HttpStatus.OK);
    }

    private SolicitacaoFaltas solicitacaoFaltasToFaltas(RHSolicitacaoFaltas dto) {
        if (dto != null) {
            SolicitacaoFaltas solicitacao = new SolicitacaoFaltas();
            solicitacao.setId(dto.getId());
            solicitacao.setCriadaEm(sistemaService.getDataOperacao());
            solicitacao.setDataInicio(dto.getDataInicio());
            solicitacao.setDataFim(dto.getDataFim());
            solicitacao.setStatus(dto.getStatusSolicitacaoFaltas());
            solicitacao.setMotivoRejeicao(dto.getMotivoRejeicao());
            if (dto.getTipoFalta() != null) {
                solicitacao.setTipoFalta(dto.getTipoFalta());
            }
            solicitacao.setContratoFP((ContratoFP) getVinculoFPFacade().recuperar(dto.getVinculoFP().getId()));
            return solicitacao;
        }
        return null;
    }

}
