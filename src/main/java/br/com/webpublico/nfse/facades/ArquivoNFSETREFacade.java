package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.ArquivoNFSETRE;
import br.com.webpublico.nfse.domain.ArquivoNFSETREDetalhe;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@Stateless
public class ArquivoNFSETREFacade extends AbstractFacade<ArquivoNFSETRE> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ArquivoNFSETREFacade arquivoNFSETREFacade;

    public ArquivoNFSETREFacade() {
        super(ArquivoNFSETRE.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public ArquivoNFSETRE recuperar(Object id) {
        ArquivoNFSETRE arquivoNFSETRE = super.recuperar(id);
        Hibernate.initialize(arquivoNFSETRE.getDetalhes());
        if (arquivoNFSETRE.getDetentorArquivoComposicao() != null)
            Hibernate.initialize(arquivoNFSETRE.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes());
        if (arquivoNFSETRE.getArquivoGerado() != null)
            Hibernate.initialize(arquivoNFSETRE.getArquivoGerado().getPartes());
        return arquivoNFSETRE;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future gerarArquivoNfseTre(ArquivoNFSETRE arquivoNFSETRE,
                                      AssistenteBarraProgresso assistente) throws IOException {
        ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();

        ByteArrayOutputStream arquivoRetorno = new ByteArrayOutputStream();
        arquivoRetorno.write(montarHeaderRetorno(arquivoNFSETRE).getBytes());

        Arquivo arquivo = arquivoNFSETRE.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
        arquivo = arquivoFacade.montarImputStream(arquivo);

        InputStreamReader streamReader = new InputStreamReader(arquivo.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        String linha;
        //desconsidera o header
        reader.readLine();
        List<String> cpfCnpjConsulta = Lists.newArrayList();
        List<String> linhasArquivo = Lists.newArrayList();
        while ((linha = reader.readLine()) != null) {
            String cnpjDestinatario = StringUtil.retornaApenasNumeros(linha.substring(3, 18));
            cpfCnpjConsulta.add(cnpjDestinatario);
            if (cpfCnpjConsulta.size() % 1000 == 0) {
                linhasArquivo.addAll(arquivoNFSETREFacade.processarLoteCpfCnpj(arquivoNFSETRE, configuracaoNfse,
                    arquivoRetorno, cpfCnpjConsulta));
                assistente.contar(cpfCnpjConsulta.size());
                cpfCnpjConsulta = Lists.newArrayList();
            }
        }
        if (!cpfCnpjConsulta.isEmpty()) {
            linhasArquivo.addAll(arquivoNFSETREFacade.processarLoteCpfCnpj(arquivoNFSETRE, configuracaoNfse,
                arquivoRetorno, cpfCnpjConsulta));
            assistente.contar(cpfCnpjConsulta.size());
        }

        arquivoRetorno.write(montarTrailerRetorno(linhasArquivo).getBytes());

        arquivoNFSETREFacade.salvarArquivoGerado(arquivoNFSETRE, arquivoRetorno);

        arquivoRetorno.close();
        reader.close();
        streamReader.close();
        arquivo.getInputStream().close();

        return new AsyncResult(null);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<String> processarLoteCpfCnpj(ArquivoNFSETRE arquivoNFSETRE,
                                              ConfiguracaoNfse configuracaoNfse,
                                              ByteArrayOutputStream arquivoRetorno,
                                              List<String> cpfCnpjConsulta) throws IOException {
        List<String> linhasRetorno = Lists.newArrayList();
        List<Object[]> notasFiscais = buscarNotasFiscaisDestinatario(arquivoNFSETRE, cpfCnpjConsulta);
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (Object[] notaFiscal : notasFiscais) {
                ArquivoNFSETREDetalhe detalhe = new ArquivoNFSETREDetalhe();
                detalhe.setArquivoNFSETRE(arquivoNFSETRE);
                detalhe.setUf(notaFiscal[0] != null ? (String) notaFiscal[0] : "");
                detalhe.setCnpjDestinatario(notaFiscal[1] != null ? (String) notaFiscal[1] : "");
                detalhe.setTipoPessoaEmitente(notaFiscal[2] != null ? ((Character) notaFiscal[2]).toString() : "");
                detalhe.setCpfCnpjEmitente(notaFiscal[3] != null ? (String) notaFiscal[3] : "");
                detalhe.setNaturezaOperacao(notaFiscal[4] != null ? (String) notaFiscal[4] : "");
                detalhe.setModeloNotaFiscal(notaFiscal[5] != null ? (String) notaFiscal[5] : "");
                detalhe.setEmissaoNotaFiscal((Date) notaFiscal[6]);
                detalhe.setSerieNotaFiscal(notaFiscal[7] != null ? (String) notaFiscal[7] : "");
                detalhe.setNumeroNotaFiscal(notaFiscal[8] != null ? ((Number) notaFiscal[8]).longValue() : 0);
                detalhe.setSituacaoNotaFiscal(notaFiscal[9] != null ? ((Character) notaFiscal[9]).toString() : "");
                detalhe.setNumeroNotaFiscalSubs(notaFiscal[10] != null ? ((Number) notaFiscal[10]).longValue() : 0L);
                detalhe.setValorTotalNotaFiscal(notaFiscal[11] != null ? (BigDecimal) notaFiscal[11] : BigDecimal.ZERO);
                detalhe.setChaveAcesso(notaFiscal[12] != null ? ((String) notaFiscal[12]).replaceAll("-", "").replaceAll(" ", "") : "");
                detalhe.setUrlAcessoNfse(configuracaoNfse.getUrlAutenticacaoNfse() + (notaFiscal[13] != null ? ((Number) notaFiscal[13]).longValue() : 0L));
                em.persist(detalhe);
                linhasRetorno.add(montarDetalheRetorno(configuracaoNfse, detalhe));
            }
        }
        for (String linhaRetorno : linhasRetorno) {
            arquivoRetorno.write(linhaRetorno.getBytes());
            arquivoNFSETRE.setTotalLinhas(arquivoNFSETRE.getTotalLinhas() + 1);
        }
        return linhasRetorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void salvarArquivoGerado(ArquivoNFSETRE arquivoNFSETRE, ByteArrayOutputStream outputStream) {
        try {
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

            Arquivo arquivo = new Arquivo();
            arquivo.setDescricao("arquivo" + arquivoNFSETRE.getId() + ".txt");
            arquivo.setMimeType("text/plain");
            arquivo.setNome("arquivo" + arquivoNFSETRE.getId() + ".txt");

            arquivoNFSETRE.setArquivoGerado(arquivoFacade.retornaArquivoSalvo(arquivo, is));

            is.close();

            salvar(arquivoNFSETRE);
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo nfse tre {}", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private String montarTrailerRetorno(List<String> linhasArquivo) {

        String footer = "9";
        int size = 0;
        StringBuilder detail = new StringBuilder();
        for (String item : linhasArquivo) {
            size++;
            detail.append(item);
        }
        Checksum checksum = new CRC32();
        checksum.update(detail.toString().getBytes(), 0, detail.toString().getBytes().length);

        footer += StringUtils.leftPad(size + "", 9, "0");
        footer += StringUtils.leftPad(checksum.getValue()+"", 32, " ");
        footer += StringUtils.leftPad(" ", 355, " ");

        return footer;
    }

    public String montarHeaderRetorno(ArquivoNFSETRE arquivoNFSETRE) {
        ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
        Cidade cidade = configuracaoNfse.getCidade();

        String header = "1";
        header += StringUtils.leftPad(cidade.getUf().getUf(), 2, "0");
        header += StringUtils.leftPad("04034583000122", 14, "0");
        header += DataUtil.getDataFormatada(arquivoNFSETRE.getDataGeracao(), "ddMMyyyy");
        header += StringUtils.leftPad(arquivoNFSETRE.getNumeroNotificacaoTre() != null ?
            arquivoNFSETRE.getNumeroNotificacaoTre().toString() : "0", 10, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getAnoRemessa().toString(), 4, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getMesRemessa().toString(), 2, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getNumeroLoteRemessa() != null ?
            arquivoNFSETRE.getNumeroLoteRemessa().toString() : "0", 4, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getAnoRemessaCorrecao() != null ?
            arquivoNFSETRE.getAnoRemessaCorrecao().toString() : "0", 4, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getMesRemessaCorrecao() != null ?
            arquivoNFSETRE.getMesRemessaCorrecao().toString() : "0", 2, "0");
        header += StringUtils.leftPad(arquivoNFSETRE.getNumeroLoteRemessaCorrecao() != null ?
            arquivoNFSETRE.getNumeroLoteRemessaCorrecao().toString() : "0", 4, "0");
        header += "120";
        header += "ATSENFE";
        header += StringUtils.leftPad(" ", 332, " ");
        header += Util.quebraLinha();
        return header;
    }

    private String montarDetalheRetorno(ConfiguracaoNfse configuracaoNfse, ArquivoNFSETREDetalhe arquivoNFSETREDetalhe) {
        String detalhe = "2";
        if (arquivoNFSETREDetalhe.getUf() != null) {
            detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getUf(), 2, "0");
        } else {
            detalhe += StringUtils.leftPad(configuracaoNfse.getCidade().getUf().getUf(), 2, "0");
        }
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getCnpjDestinatario(), 14, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getTipoPessoaEmitente(), 1, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getCpfCnpjEmitente(), 14, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getNaturezaOperacao(), 4, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getModeloNotaFiscal(), 2, " ");
        detalhe += DataUtil.getDataFormatada(arquivoNFSETREDetalhe.getEmissaoNotaFiscal(), "ddMMyyyy");
        if (arquivoNFSETREDetalhe.getSerieNotaFiscal() != null) {
            detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getSerieNotaFiscal(), 3, " ");
        } else {
            detalhe += StringUtils.leftPad("", 3, " ");
        }
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getNumeroNotaFiscal().toString(), 15, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getSituacaoNotaFiscal(), 1, " ");
        if (arquivoNFSETREDetalhe.getNumeroNotaFiscalSubs() != null) {
            detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getNumeroNotaFiscalSubs().toString(), 15, "0");
        } else {
            detalhe += StringUtils.leftPad("", 15, "0");
        }
        detalhe += StringUtils.leftPad(StringUtil.retornaApenasNumeros(Util.formataValorSemCifrao(arquivoNFSETREDetalhe.getValorTotalNotaFiscal())), 17, "0");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getChaveAcesso() != null ? arquivoNFSETREDetalhe.getChaveAcesso() : "", 50, " ");
        detalhe += StringUtils.leftPad(arquivoNFSETREDetalhe.getUrlAcessoNfse() != null ? arquivoNFSETREDetalhe.getUrlAcessoNfse() : "", 250, " ");
        detalhe += Util.quebraLinha();
        return detalhe;
    }

    public List<Object[]> buscarNotasFiscaisDestinatario(ArquivoNFSETRE arquivoNFSETRE,
                                                         List<String> cpfCnpjConsulta) {
        String sql = " select ufp.uf as uf, " +
            "       dpt.cpfcnpj as cnpjDestinatario, " +
            "       case " +
            "          when length(dpp.cpfcnpj) = 11 then 'F' " +
            "          when length(dpp.cpfcnpj) = 14 then 'J' " +
            "       end as tipoPessoaEmitente, " +
            "       dpp.cpfcnpj as cpfCnpjEmitente, " +
            "       'SERV' as naturezaOperacao, " +
            "       'NF' as modeloNotaFiscal, " +
            "       nf.emissao as emissaoNotaFiscal, " +
            "       '' as serieNotaFiscal, " +
            "       nf.numero as numeroNotaFiscal, " +
            "       case " +
            "           when dps.situacao = 'CANCELADA' then 'C' " +
            "           else 'A' " +
            "       end as situacaoNotaFiscal, " +
            "       0 as numeroNotaFiscalSub, " +
            "       dps.totalservicos as valorTotalNotaFiscal, " +
            "       nf.codigoVerificacao as chaveAcesso, " +
            "       nf.id as id " +
            "   from notafiscal nf " +
            "  inner join declaracaoprestacaoservico dps on dps.id = nf.declaracaoprestacaoservico_id " +
            "  inner join dadospessoaisnfse dpp on dpp.id = dps.dadospessoaisprestador_id " +
            "  inner join dadospessoaisnfse dpt on dpt.id = dps.dadospessoaistomador_id " +
            "  inner join itemdeclaracaoservico ids on ids.declaracaoprestacaoservico_id = dps.id " +
            "  left join cidade cp on cp.id = ids.municipio_id " +
            "  left join uf ufp on ufp.id = cp.uf_id " +
            "where dpt.cpfcnpj in (:cnpjDestinatario) " +
            "  and trunc(nf.emissao) between :inicial and :final " +
            "order by dpt.cpfcnpj, nf.id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cnpjDestinatario", cpfCnpjConsulta);
        q.setParameter("inicial", Util.getDataHoraMinutoSegundoZerado(arquivoNFSETRE.getAbrangenciaInicial()));
        q.setParameter("final", Util.getDataHoraMinutoSegundoZerado(arquivoNFSETRE.getAbrangenciaFinal()));
        return q.getResultList();
    }

    public Integer contarLinhasArquivoNfseTre(ArquivoNFSETRE arquivoNFSETRE) {
        try {
            Arquivo arquivo = arquivoNFSETRE.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
            arquivo. montarImputStream();
            InputStreamReader streamReader = new InputStreamReader(arquivo.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count += 1;
            }
            reader.close();
            streamReader.close();
            arquivo.getInputStream().close();
            return count - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ArquivoNFSETRE> salvarArquivo(ArquivoNFSETRE selecionado) {

        selecionado.setTotalLinhas(contarLinhasArquivoNfseTre(selecionado));
        return new AsyncResult<>(salvarRetornando(selecionado));
    }
}
