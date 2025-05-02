package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOCdaProtesto;
import br.com.webpublico.entidadesauxiliares.VODamRemessaFile;
import br.com.webpublico.entidadesauxiliares.VOPessoa;
import br.com.webpublico.enums.tributario.*;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.RemessaProtestoFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.dao.JDBCProtestoDAO;
import br.com.webpublico.negocios.tributario.protesto.model.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceRemessaProtesto {
    private static final Long NUMERO_DIARIO_MAX_REMESSAS = 9L; //TODO mudar para config tributario
    private static final String COD_IBGE_MUNIC_RIO_BRANCO = "1200401";
    private static final String COD_REMESSA_ENVIADA = "0000";
    private static final String VERSAO_LAYOUT = "043";
    private static final String PRACA_PROTESTO = "RIO BRANCO";
    private static final Logger logger = LoggerFactory.getLogger(ServiceRemessaProtesto.class);
    private RemessaProtestoFacade remessaFacade;
    private ExercicioFacade exercicioFacade;
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private LeitorWsConfig leitorWsConfig;
    @Audited
    private JDBCProtestoDAO protestoDAO;

    @PostConstruct
    private void init() {
        remessaFacade = (RemessaProtestoFacade) Util.getFacadeViaLookup("java:module/RemessaProtestoFacade");
        configuracaoTributarioFacade = (ConfiguracaoTributarioFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoTributarioFacade");
        exercicioFacade = (ExercicioFacade) Util.getFacadeViaLookup("java:module/ExercicioFacade");
        leitorWsConfig = (LeitorWsConfig) Util.getFacadeViaLookup("java:module/LeitorWsConfig");
    }

    public AssistenteBarraProgresso consultarOuGerarDam(AssistenteBarraProgresso assistente) {
        try {
            assistente.setDescricaoProcesso("Envio de Remessa de Protesto - Consultando/gerando os DAMs das parcela ...");
            RemessaProtesto remessa = (RemessaProtesto) assistente.getSelecionado();
            int i = 1;
            for (CdaRemessaProtesto cda : remessa.getCdas()) {
                assistente.setDescricaoProcesso("Envio de Remessa de Protesto - Consultando/gerando DAM " + i + " de " + remessa.getCdas().size());
                if (cda.getParcelasDaCda().isEmpty()) {
                    cda.getParcelasDaCda().addAll(remessaFacade.buscarParcelasEmAbertoDaCDA(cda.getCda()));
                }
                remessaFacade.gerarDamAgrupadoParaParcelasDaCda(cda.getParcelasDaCda(), assistente);
                i++;
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar remessa. ", e);
            assistente.removerProcessoDoAcompanhamento();
        }
        return assistente;
    }

    public AssistenteBarraProgresso enviarRemessa(AssistenteBarraProgresso assistente) {
        try {
            assistente = enviarRemessaAndAtualizarSituacoes(assistente, true);
            assistente.contar(((RemessaProtesto) assistente.getSelecionado()).getCdas().size());
        } catch (Exception e) {
            logger.error("Erro ao enviar remessa. ", e);
            assistente.removerProcessoDoAcompanhamento();
            assistente.getMensagens().add(e.getMessage());
        }
        return assistente;
    }

    public void enviarRemessaPorAgendamento() {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setExercicio(exercicioFacade.getExercicioPorAno(Calendar.getInstance().get(Calendar.YEAR)));
        assistente.setUsuarioSistema(Util.recuperarUsuarioCorrente());
        assistente.setIp(Util.obterIpUsuario());
        assistente.setDescricaoProcesso("Envio de Remessa de Protesto por Agendamento de Tarefas.");
        try {
            List<CdaRemessaProtesto> cdaRemessaProtestos = remessaFacade.buscarCdasParaEnvioDeRemessa();
            if (cdaRemessaProtestos != null && !cdaRemessaProtestos.isEmpty()) {
                RemessaProtesto remessa = new RemessaProtesto();
                remessa.setEnvioRemessa(new Date());
                remessa.setSequencia(remessaFacade.gerarSequenciaRemessa());
                remessa.setCdas(cdaRemessaProtestos);
                remessa.setOrigemRemessa(OrigemRemessaProtesto.AGENDAMENTO);
                assistente.setSelecionado(remessa);

                enviarRemessaProtesto(assistente, false);
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar remessa por agendamento. ", e);
            assistente.removerProcessoDoAcompanhamento();
        }
    }

    public void atualizarSituacaoDeProtestoDasParcelas() {
        remessaFacade.atualizarSituacaoDeProtestoDasParcelas();
    }

    public AssistenteBarraProgresso enviarRemessaAndAtualizarSituacoes(AssistenteBarraProgresso assistente, boolean exibirValidacao) throws Exception {
        assistente = consultarOuGerarDam(assistente);
        assistente = enviarRemessaProtesto(assistente, exibirValidacao);
        assistente.setDescricaoProcesso("Envio de Remessa de Protesto - Atualizando situações.");
        consultarSituacoes(assistente);
        return assistente;
    }

    public AssistenteBarraProgresso enviarRemessaProtesto(AssistenteBarraProgresso assistente, boolean exibirValidacao) throws Exception {
        assistente.setDescricaoProcesso("Envio de Remessa de Protesto - Montando arquivo de remessa.");

        ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();
        remessaFacade.validarConfiguracaoTributaria(config, true);

        RemessaType remessaType = gerarRemessa(assistente, config, exibirValidacao);
        if (remessaType != null) {
            try {
                ((RemessaProtesto) assistente.getSelecionado()).setXmlEnvio(Util.typeToXML(remessaType));
                RemessaProtesto remessaProtesto = enviarRemessaAndMontarOcorrencias(assistente);
                assistente.setSelecionado(remessaProtesto);
                return assistente;
            } catch (JAXBException e) {
                logger.error("Erro ao fazer parse do xml. ", e);
            }
        }
        return assistente;
    }

    private RemessaType gerarRemessa(AssistenteBarraProgresso assistente, ConfiguracaoTributario config, boolean exibirValidacao) throws Exception {
        RemessaType remessaType = new RemessaType();
        RemessaProtesto remessa = (RemessaProtesto) assistente.getSelecionado();

        Long numeroDeRemessas = remessaFacade.buscarNumeroDeRemessasNaDataAtual();
        if (numeroDeRemessas < NUMERO_DIARIO_MAX_REMESSAS) {
            numeroDeRemessas = numeroDeRemessas + 1L;
            remessaType.setNomeArquivo(montarNomeArquivoRemessa(remessa, config.getCodigoApresentante(), numeroDeRemessas));
            remessa.setNomeArquivo(remessaType.getNomeArquivo());

            BigDecimal valorTotal = BigDecimal.ZERO;
            Map<Long, VODamRemessaFile> mapaDamPorCDA = Maps.newHashMap();
            ComarcaRemessaType comarca = new ComarcaRemessaType();
            comarca.setCodigoMunicipio(COD_IBGE_MUNIC_RIO_BRANCO);
            comarca.setHeader(criarHeaderRemessa(remessa, config, remessa.getCdas().size()));
            Long sequencial = 2L;
            for (CdaRemessaProtesto cda : remessa.getCdas()) {
                if (cda.getParcelasDaCda().isEmpty()) {
                    cda.getParcelasDaCda().addAll(remessaFacade.buscarParcelasEmAbertoDaCDA(cda.getCda()));
                }
                comarca.getTransacoes().add(criarTransacaoRemessa(cda.getCda(), cda.getParcelasDaCda(), sequencial, config, assistente, mapaDamPorCDA));
                valorTotal = valorTotal.add(getValorTotalParcelas(cda.getParcelasDaCda()));
                sequencial++;
                assistente.conta();
            }
            comarca.setTrailler(criarTraillerRemessa(remessa.getEnvioRemessa(), sequencial, valorTotal, config, comarca.getHeader()));
            remessaType.setComarca(comarca);
            assistente.setSelecionado(remessa);
            return remessaType;
        } else {
            if (exibirValidacao) {
                throw new Exception("O número máximo de remessas diárias " + NUMERO_DIARIO_MAX_REMESSAS + " já foi atingido.");
            }
        }
        return null;
    }

    private BigDecimal getValorTotalParcelas(List<ResultadoParcela> parcelas) {
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela != null) {
                total = total.add(parcela.getValorTotal());
            }
        }
        return total;
    }

    private BigDecimal getValorTotalSemDescontoParcelas(List<ResultadoParcela> parcelas) {
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela != null) {
                total = total.add(parcela.getValorTotalSemDesconto());
            }
        }
        return total;
    }

    private TraillerRemessaType criarTraillerRemessa(Date envioRemessa, Long sequencial, BigDecimal valorTotal, ConfiguracaoTributario config, HeaderRemessaType header) {
        TraillerRemessaType trailler = new TraillerRemessaType();

        trailler.setRegistroTrailler("9");
        trailler.setCodigoPortador(config.getCodigoApresentante());
        trailler.setNomePortador(config.getNomeApresentante());
        trailler.setDataMovimento(remessaFacade.formatarData(envioRemessa));
        trailler.setSomatorioSegurancaRegistro(montarSomatorioSegurancaQtdRegistrosHeader(header));
        trailler.setSomatorioSegurancaValores(valorTotal.toString());
        trailler.setComplementoRegistro("");
        trailler.setSequencialDoRegistro(StringUtil.cortarOuCompletarEsquerda(sequencial + "", 4, "0"));

        return trailler;
    }


    private String montarSomatorioSegurancaQtdRegistrosHeader(HeaderRemessaType header) {
        Integer somatorio = converterStringParaInteiro(header.getQuantidadeParcelas()) +
            converterStringParaInteiro(header.getQuantidadeParcelasContribuintePrincipal()) +
            converterStringParaInteiro(header.getQuantidadeTitulosTipoDmiDriCbi()) +
            converterStringParaInteiro(header.getQuantidadeDemaisParcelas());

        return somatorio.toString();
    }

    private Integer converterStringParaInteiro(String descricao) {
        try {
            return Integer.valueOf(descricao);
        } catch (Exception ex) {
            return 0;
        }
    }

    private TransacaoRemessaType criarTransacaoRemessa(CertidaoDividaAtiva cda, List<ResultadoParcela> parcelas, Long sequencial, ConfiguracaoTributario config, AssistenteBarraProgresso assistente, Map<Long, VODamRemessaFile> mapaDamPorCDA) {
        VOPessoa pessoa = remessaFacade.buscarInformacoesContribuinte(cda);
        VOPessoa sacador = remessaFacade.buscarInformacoesSacador();

        TransacaoRemessaType transacao = new TransacaoRemessaType();
        transacao.setRegistroTransacao("1");
        transacao.setCodigoPortador(config.getCodigoApresentante());
        transacao.setCodigoAgenciaCedente("");
        transacao.setCedente(StringUtil.removeAcentuacao(config.getNomeApresentante()));
        transacao.setSacador(StringUtil.cortaDireita(45, sacador.getNomeSemAcento()).toUpperCase());
        transacao.setDocumentoSacador(sacador.getCpfCnpj() != null ? StringUtil.cortarOuCompletarEsquerda(StringUtil.retornaApenasNumeros(sacador.getCpfCnpj()), 14, "0") : "");
        transacao.setEnderecoSacador(StringUtil.cortaDireita(45, remessaFacade.buscarEnderecoPessoa(sacador.getEndereco())));
        transacao.setCepSacador(sacador.getEndereco() != null ? StringUtil.cortaDireita(8, sacador.getEndereco().getCep()) : "");
        transacao.setCidadeSacador(sacador.getEndereco() != null ? StringUtil.cortaDireita(20, sacador.getEndereco().getLocalidade().toUpperCase()) : "");
        transacao.setUfSacador(sacador.getEndereco() != null ? StringUtil.cortaDireita(2, sacador.getEndereco().getUf().toUpperCase()) : "");
        transacao.setEspecieParcela("CDA");
        transacao.setNossoNumero(cda.getNumeroCdaComExercicio());
        transacao.setNumeroDaParcela(transacao.getNossoNumero());
        transacao.setEmissaoParcela(remessaFacade.formatarData(cda.getDataCertidao()));
        transacao.setVencimentoParcela("99999999");
        transacao.setTipoMoeda("001");
        transacao.setValorParcela(getValorTotalSemDescontoParcelas(parcelas).toString());
        transacao.setSaldoParcela(getValorTotalParcelas(parcelas).toString());
        transacao.setPracaDeProtesto(PRACA_PROTESTO);
        transacao.setTipoEndosso("");
        transacao.setInfoAceite("N");
        transacao.setQuantidadeContribuintes("1");
        transacao.setNomeContribuinte(pessoa != null ? StringUtil.cortaDireita(45, pessoa.getNomeSemAcento()).toUpperCase() : "");
        transacao.setTipoDoctoContribuinte(pessoa != null ? (pessoa.isPessoaFisica() ? "002" : (pessoa.isPessoaJuridica() ? "001" : "")) : "");
        transacao.setCpfCnpjContribuinte(pessoa != null ? StringUtil.cortarOuCompletarEsquerda(StringUtil.retornaApenasNumeros(pessoa.getCpfCnpj()), 14, "0") : "");
        transacao.setDocumentoContribuinte("");
        transacao.setEnderecoContribuinte(pessoa != null ? StringUtil.cortaDireita(45, remessaFacade.buscarEnderecoPessoa(pessoa.getEndereco())) : "");
        transacao.setCepContribuinte(pessoa != null && pessoa.getEndereco() != null ? StringUtil.cortaDireita(8, pessoa.getEndereco().getCep()) : "");
        transacao.setCidadeContribuinte(pessoa != null && pessoa.getEndereco() != null ? StringUtil.cortaDireita(20, pessoa.getEndereco().getLocalidade().toUpperCase()) : "");
        transacao.setUfContribuinte(pessoa != null && pessoa.getEndereco() != null ? StringUtil.cortaDireita(2, pessoa.getEndereco().getUf().toUpperCase()) : "");
        transacao.setCodigoCartorio("");
        transacao.setProtocoloCartorio("");
        transacao.setTipoOcorrencia("");
        transacao.setDataProtocolo("");
        transacao.setValorCustasCartorio("");
        transacao.setDeclaracaoDoPortador("D");
        transacao.setDataOcorrencia("");
        transacao.setCodigoIrregularidade("");
        transacao.setBairroContribuinte(pessoa != null && pessoa.getEndereco() != null ? StringUtil.cortaDireita(20, StringUtil.removeAcentuacao(pessoa.getEndereco().getBairro()).toUpperCase()) : "");
        transacao.setValorCustasCartorioDistribuidor("");
        transacao.setRegistroDeDistribuicao("");
        transacao.setValorGravacaoEletronica("");
        transacao.setNumOperacaoBanco("0");
        transacao.setNumContratoBanco("0");
        transacao.setNumParcelaContrato("0");
        transacao.setTipoLetraCambio("0");
        transacao.setComplementoCodigoIrregularidade("");
        transacao.setProtestoMotivoFalencia("");
        transacao.setInstrumentoProtesto("");
        transacao.setValorDemaisDespesas("0");
        transacao.setComplementoRegistro(remessaFacade.gerarBase64AnexosProtesto(cda, parcelas, assistente, mapaDamPorCDA));
        transacao.setSequencialDoRegistro(StringUtil.cortarOuCompletarEsquerda(sequencial + "", 4, "0"));
        return transacao;
    }

    private void consultarSituacoes(AssistenteBarraProgresso assistente) {
        RemessaProtesto remessaProtesto = (RemessaProtesto) assistente.getSelecionado();
        if (remessaProtesto == null ||
            !SituacaoRemessaProtesto.ENVIADO.equals(remessaProtesto.getSituacaoRemessa()) ||
            remessaProtesto.getCdas().isEmpty()) {
            return;
        }
        List<CdaRemessaProtesto> cdasRemessa = ((RemessaProtesto) assistente.getSelecionado()).getCdas();
        List<VOCdaProtesto> vosCdasRemessa = cdasRemessa.stream().map((cdaRemessa) -> new VOCdaProtesto(cdaRemessa.getId(),
            cdaRemessa.getNossoNumero(), cdaRemessa.getCda().getId(),
            cdaRemessa.getCda().getNumeroCdaComExercicio(), cdaRemessa.getSituacaoProtesto())).collect(Collectors.toList());
        String usuario = assistente.getUsuarioSistema() != null ?
            assistente.getUsuarioSistema().getLogin() : "Atualização feita automaticamente";
        try {

            remessaFacade.consultarSituacaoDeProtestoDasParcelas(vosCdasRemessa);
            protestoDAO.atualizarSituacaoProtestoParcelas(vosCdasRemessa, usuario, true);
        } catch (Exception e) {
            protestoDAO.inserirLogsCDAs(vosCdasRemessa, usuario, "Erro: " + e.getMessage());
        }
    }

    public RemessaProtesto enviarRemessaAndMontarOcorrencias(AssistenteBarraProgresso assistente) {
        RemessaProtesto remessa = (RemessaProtesto) assistente.getSelecionado();

        remessa.setXmlResposta(enviarXmlRemessa(remessa, assistente));

        Map<String, String> ocorrenciasRemessa = buscarOcorrenciasRemessa(remessa.getXmlResposta());

        if (ocorrenciasRemessa.get(COD_REMESSA_ENVIADA) != null) {
            remessa.setSituacaoRemessa(SituacaoRemessaProtesto.ENVIADO);
        } else {
            remessa.setSituacaoRemessa(SituacaoRemessaProtesto.RECUSADO);
        }

        for (Map.Entry<String, String> entry : ocorrenciasRemessa.entrySet()) {
            LogRemessaProtesto logRemessa = new LogRemessaProtesto(remessa);

            RespostaLogValidacao respostaLogValidacao = new RespostaLogValidacao();
            respostaLogValidacao.setLog(logRemessa);
            respostaLogValidacao.setCodigoValidacao(entry.getKey());
            respostaLogValidacao.setRespostaValidacao(entry.getValue());

            logRemessa.getValidacoes().add(respostaLogValidacao);
            remessa.getLogs().add(logRemessa);
        }
        return remessaFacade.salvarRetornando(remessa);
    }

    private Map<String, String> buscarOcorrenciasRemessa(String xml) {
        Map<String, String> ocorrencias = Maps.newLinkedHashMap();
        try {
            if (!"".equals(xml)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xml)));

                NodeList nodeCodigo = document.getElementsByTagName("codigo");
                NodeList nodeOcorrencia = document.getElementsByTagName("ocorrencia");

                for (int i = 0; i < nodeCodigo.getLength(); i++) {
                    for (int j = i; j < nodeOcorrencia.getLength(); j++) {
                        String codigo = nodeCodigo.item(i).getFirstChild().getNodeValue();
                        String ocorrencia = nodeOcorrencia.item(j).getFirstChild().getNodeValue();

                        if (codigo != null && ocorrencia != null) {
                            if (!ocorrencias.containsKey(codigo)) {
                                ocorrencias.put(codigo, ocorrencia);
                            }
                            break;
                        }
                    }
                }
            }
            return ocorrencias;
        } catch (Exception e) {
            logger.error("Erro ao montar mapa de ocorrencias. ", e);
            ocorrencias.put("", "O arquivo de resposta está corrompido.");
        }
        return ocorrencias;
    }

    public String enviarXmlRemessa(RemessaProtesto remessa, AssistenteBarraProgresso assistente) {
        try {
            assistente.setDescricaoProcesso("Envio de Remessa de Protesto - Enviando Remessa de Protesto...");

            ConfiguracaoWebService configuracaoWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PROTESTO);
            remessaFacade.validarConfiguracaoWsProtesto(configuracaoWs, true);

            String url = configuracaoWs.getDetalhe() + "/enviar-remessa" +
                "?nomeArquivo=" + remessa.getNomeArquivo() +
                "&usuario=" + configuracaoWs.getUsuario() +
                "&senha=" + configuracaoWs.getSenha() +
                "&urlCra=" + configuracaoWs.getUrl();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, remessa.getXmlEnvio(), String.class);
            assistente.contar(remessa.getCdas().size());

            return response.getBody();
        } catch (RestClientException ce) {
            logger.error("Erro ao conectar com o serviço 'enviar-remessa' de integração de protestos.  ", ce);
            assistente.removerProcessoDoAcompanhamento();
        } catch (Exception e) {
            logger.error("Erro ao recuperar resposta de envio da remessa. ", e);
            assistente.removerProcessoDoAcompanhamento();
        }
        return "";
    }


    private String montarNomeArquivoRemessa(RemessaProtesto remessa, String codigoApresentante, Long sequencialRemessa) {
        return "B" + codigoApresentante + remessaFacade.formatarData(remessa.getEnvioRemessa(), "ddMM.yy") + sequencialRemessa;
    }

    private HeaderRemessaType criarHeaderRemessa(RemessaProtesto remessa, ConfiguracaoTributario config, int quantidadeCda) {
        String quantidadeParcelas = quantidadeCda + "";

        HeaderRemessaType header = new HeaderRemessaType();
        header.setRegistroHeader("0");
        header.setCodigoPortador(config.getCodigoApresentante());
        header.setNomePortador(config.getNomeApresentante());
        header.setDataMovimento(remessaFacade.formatarData(remessa.getEnvioRemessa()));
        header.setSiglaRemetente("BFO");
        header.setSiglaDestinatario("SDT");
        header.setSiglaTransacao("TPR");
        header.setSequencia(remessa.getSequencia().toString());
        header.setQuantidadeParcelas(quantidadeParcelas);
        header.setQuantidadeParcelasContribuintePrincipal(quantidadeParcelas);
        header.setQuantidadeTitulosTipoDmiDriCbi("0");
        header.setQuantidadeDemaisParcelas(quantidadeParcelas);
        header.setAgenciaCentralizadora("");
        header.setVersaoLayout(VERSAO_LAYOUT);
        header.setCodigoMunicipio(COD_IBGE_MUNIC_RIO_BRANCO);
        header.setComplementoRegistro("");
        header.setSequenciaDoRegistro("1");

        return header;
    }

}

