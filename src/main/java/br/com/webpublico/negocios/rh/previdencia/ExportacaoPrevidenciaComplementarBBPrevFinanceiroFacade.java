package br.com.webpublico.negocios.rh.previdencia;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.previdencia.ExportacaoPrevidenciaComplementarBBPrevFinanceiro;
import br.com.webpublico.entidadesauxiliares.rh.previdencia.ValorContribuicaoPrevidenciaComplementarVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
public class ExportacaoPrevidenciaComplementarBBPrevFinanceiroFacade extends AbstractFacade<ExportacaoPrevidenciaComplementarBBPrevFinanceiro> {
    private static final String CODIGO_DO_PLANO = "0141";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;


    public ExportacaoPrevidenciaComplementarBBPrevFinanceiroFacade() {
        super(ExportacaoPrevidenciaComplementarBBPrevFinanceiro.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ExportacaoPrevidenciaComplementarBBPrevFinanceiro gerarArquivo(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao, List<ContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso) throws IOException {
        ByteArrayOutputStream arquivoRetorno = new ByteArrayOutputStream();
        Arquivo arquivo = new Arquivo();
        arquivo.montarImputStream();
        InputStreamReader streamReader = new InputStreamReader(arquivo.getInputStream());
        arquivoRetorno.write(montarArquivo(exportacao, contratos, assistenteBarraProgresso).getBytes());
        exportacao = salvarArquivoGerado(exportacao, arquivoRetorno);
        arquivoRetorno.close();
        streamReader.close();
        arquivo.getInputStream().close();
        return exportacao;
    }

    private ExportacaoPrevidenciaComplementarBBPrevFinanceiro salvarArquivoGerado(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao, ByteArrayOutputStream outputStream) {
        try {
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

            Arquivo arquivo = new Arquivo();
            String nomeArquivo = montarNomeArquivo(exportacao);
            arquivo.setDescricao(nomeArquivo + ".dat");
            arquivo.setMimeType("text/plain");
            arquivo.setNome(nomeArquivo + ".dat");

            exportacao.setArquivo(arquivoFacade.retornaArquivoSalvo(arquivo, is));
            is.close();
            exportacao.setDataGeracao(new Date());
            return salvarRetornando(exportacao);
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo da previdência complementar BBPrev financeiro {}", e);
        }
        return null;
    }

    private String montarNomeArquivo(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao) {
        StringBuilder sb = new StringBuilder();
        //PRÉ-FIXO
        sb.append("R");
        //CÓDIGO DA PATROCINADORA
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCodigoPatrocinadora(), 5, "0"));
        //CNPB
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCnpb(), 10, "0"));
        //DATA DE REFERÊNCIA
        sb.append(exportacao.getAno()).append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
        return sb.toString();
    }

    private String montarArquivo(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao, List<ContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso) {
        Integer totalRegistroContribuicoes = 0;
        BigDecimal totalValorContribuicoesPatrocinadora = BigDecimal.ZERO;
        BigDecimal totalValorContribuicoesParticipante = BigDecimal.ZERO;
        StringBuilder sb = new StringBuilder();
        sb.append(montarHeader(exportacao));

        StringBuilder registroContribuicoes = new StringBuilder();
        for (ContratoFP contrato : contratos) {
            List<ValorContribuicaoPrevidenciaComplementarVO> valores = buscarValoresContribuicaoParaServidor(contrato, exportacao.getMes(), exportacao.getAno());
            totalRegistroContribuicoes += valores.size();
            registroContribuicoes.append(montarContribuicoesMensais(exportacao, contrato, valores));
            for (ValorContribuicaoPrevidenciaComplementarVO valor : valores) {
                totalValorContribuicoesPatrocinadora = totalValorContribuicoesPatrocinadora.add(valor.getValorPatrocinador());
                totalValorContribuicoesParticipante = totalValorContribuicoesParticipante.add(valor.getValorServidor());
            }

            assistenteBarraProgresso.conta();
        }
        sb.append(registroContribuicoes);
        sb.append(montarTotalizadores(totalRegistroContribuicoes, totalValorContribuicoesPatrocinadora, totalValorContribuicoesParticipante));
        return sb.toString();
    }

    private String montarHeader(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao) {
        StringBuilder sb = new StringBuilder();
        //TIPO DE REGISTRO
        sb.append("1");
        //CÓDIGO DA PATROCINADORA
        sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getPatrocinador().getEntidade().getCodigoPatrocinadora(), 5, "0"));
        //ANO DE REFERÊNCIA e MÊS DE REFERÊNCIA
        sb.append(exportacao.getAno()).append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
        //RESERVADO
        sb.append(StringUtil.cortarOuCompletarEsquerda("0", 88, "0")).append("\n");
        return sb.toString();
    }

    private String montarContribuicoesMensais(ExportacaoPrevidenciaComplementarBBPrevFinanceiro exportacao, ContratoFP contratoFP, List<ValorContribuicaoPrevidenciaComplementarVO> valores) {
        StringBuilder sb = new StringBuilder();
        for (ValorContribuicaoPrevidenciaComplementarVO valorContribuicao : valores) {
            //TIPO DE REGISTRO
            sb.append("2");
            //MATRÍCULA DO PARTICIPANTE
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula(), 7, "0"));
            sb.append(StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0"));
            //ANO DE COMPETÊNCIA
            sb.append(exportacao.getAno());
            //MÊS DE COMPETÊNCIA
            sb.append(StringUtil.cortarOuCompletarEsquerda(exportacao.getMes() + "", 2, "0"));
            //CÓDIGO DO PLANO
            sb.append(CODIGO_DO_PLANO);
            //CÓDIGO DA CONTRIBUIÇÃO
            sb.append(StringUtil.cortarOuCompletarEsquerda(valorContribuicao.getCodigo() != null ? valorContribuicao.getCodigo() : "00", 2, "0"));
            //SALÁRIO
            Date dataOperacao = DataUtil.criarDataUltimoDiaMes(exportacao.getMes(), exportacao.getAno()).toDate();
            ExoneracaoRescisao exoneracao = exoneracaoRescisaoFacade.recuperaContratoExonerado(contratoFP, Mes.getMesToInt(exportacao.getMes()), exportacao.getAno());
            if (exoneracao != null) {
                dataOperacao = exoneracao.getDataRescisao();
            }
            BigDecimal valorSalario = buscarValorEnquadramentoFuncional(contratoFP, dataOperacao);
            if (valorSalario != null && valorSalario.compareTo(BigDecimal.ZERO) > 0) {
                String[] valor = valorSalario.toString().split("\\.");
                sb.append(StringUtil.cortarOuCompletarEsquerda(valor.length >= 1 ? valor[0] : valorSalario.toString(), 9, "0"));
                sb.append(StringUtil.cortaOuCompletaDireita(valor.length > 1 ? valor[1] : "0", 2, "0"));
            } else {
                sb.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));
            }
            //MARGEM CONSIGNÁVEL
            sb.append("00000000000");

            //VALOR DA CONTRIBUIÇÃO DO PARTICIPANTE
            BigDecimal valorContribuicaoParticipante = valorContribuicao.getValorServidor();
            if (valorContribuicaoParticipante != null && valorContribuicaoParticipante.compareTo(BigDecimal.ZERO) > 0) {
                String[] valor = valorContribuicaoParticipante.toString().split("\\.");
                sb.append(StringUtil.cortarOuCompletarEsquerda(valor.length >= 1 ? valor[0] : valorContribuicaoParticipante.toString(), 9, "0"));
                sb.append(StringUtil.cortaOuCompletaDireita(valor.length > 1 ? valor[1] : "0", 2, "0"));
            } else {
                sb.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));
            }

            //VALOR DA CONTRIBUIÇÃO DA PATROCINADORA
            BigDecimal valorContribuicaoPatrocinadora = valorContribuicao.getValorPatrocinador();
            if (valorContribuicaoPatrocinadora != null && valorContribuicaoPatrocinadora.compareTo(BigDecimal.ZERO) > 0) {
                String[] valor = valorContribuicaoPatrocinadora.toString().split("\\.");
                sb.append(StringUtil.cortarOuCompletarEsquerda(valor.length >= 1 ? valor[0] : valorContribuicaoPatrocinadora.toString(), 9, "0"));
                sb.append(StringUtil.cortaOuCompletaDireita(valor.length > 1 ? valor[1] : "0", 2, "0"));
            } else {
                sb.append(StringUtil.cortarOuCompletarEsquerda("", 11, "0"));
            }
            //RESERVADO
            sb.append(StringUtil.cortarOuCompletarEsquerda("0", 34, "0"));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String montarTotalizadores(Integer totalRegistroContribuicoes, BigDecimal totalValorContribuicoesPatrocinadora, BigDecimal totalValorContribuicoesParticipante) {
        StringBuilder sb = new StringBuilder();
        //TIPO DE REGISTRO
        sb.append("3");
        //TOTAL DE REGISTROS
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroContribuicoes + 1 + "", 6, "0"));
        //totalRegistroContribuicoes
        sb.append(StringUtil.cortarOuCompletarEsquerda(totalRegistroContribuicoes + "", 6, "0"));

        //VALOR TOTAL DAS CONTRIBUIÇÕES DA PATROCINADORA
        String[] valorPatricinadora = totalValorContribuicoesPatrocinadora.toString().split("\\.");
        sb.append(StringUtil.cortarOuCompletarEsquerda(valorPatricinadora.length >= 1 ? valorPatricinadora[0] : totalValorContribuicoesPatrocinadora.toString(), 9, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(valorPatricinadora.length > 1 ? valorPatricinadora[1] : "0", 2, "0"));

        //VALOR TOTAL DAS CONTRIBUIÇÕES DO PARTICIPANTE
        String[] valorParticipante = totalValorContribuicoesParticipante.toString().split("\\.");
        sb.append(StringUtil.cortarOuCompletarEsquerda(valorParticipante.length >= 1 ? valorParticipante[0] : totalValorContribuicoesPatrocinadora.toString(), 9, "0"));
        sb.append(StringUtil.cortaOuCompletaDireita(valorParticipante.length > 1 ? valorParticipante[1] : "0", 2, "0"));
        //RESERVADO
        sb.append(StringUtil.cortarOuCompletarEsquerda("", 65, "0")).append("\n");
        return sb.toString();
    }

    public List<ContratoFP> buscarContratosComPrevidenciaComplementarVigenteFiltrandoMatriculaOrNomeOrCPF(String s, Integer mes, Integer ano, List<Long> idsUnidades) {
        String hql = " select distinct new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) " +
            " from ItemPrevidenciaComplementar itemPrev " +
            " inner join itemPrev.previdenciaComplementar prev " +
            " inner join prev.contratoFP contrato " +
            " inner join contrato.matriculaFP matricula " +
            " inner join matricula.pessoa pf " +
            " inner join contrato.unidadeOrganizacional un " +
            " where ((lower(pf.nome) like :filtro) or" +
            "           (lower(pf.cpf) like :filtro) or" +
            "           (lower(matricula.matricula) like :filtro)) " +
            " and un.id in (:unidades) " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(contrato.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                                                     and to_date(to_char(coalesce(contrato.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itemPrev.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                                                     and to_date(to_char(coalesce(itemPrev.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy') ";

        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("unidades", idsUnidades);
        return q.getResultList();
    }

    public List<ContratoFP> buscarContratosParaExportacao(Integer mes, Integer ano, List<Long> idsUnidades) {
        String hql = " select distinct contrato " +
            " from ItemPrevidenciaComplementar itemPrev " +
            " inner join itemPrev.previdenciaComplementar prev " +
            " inner join prev.contratoFP contrato " +
            " inner join contrato.unidadeOrganizacional un " +
            " where to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(itemPrev.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                               and to_date(to_char(coalesce(itemPrev.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy')" +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(contrato.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "                                               and to_date(to_char(coalesce(contrato.finalVigencia, :dataVigencia),'mm/yyyy'),'mm/yyyy')" +
            "   and un.id in (:unidades) ";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));
        q.setParameter("unidades", idsUnidades);
        List<ContratoFP> contratosFPs = Lists.newArrayList();
        List<ContratoFP> contratos = q.getResultList();
        for (ContratoFP c : contratos) {
            contratosFPs.add(contratoFPFacade.recuperarContratoComLotacaoFuncional(c.getId()));
        }
        return contratosFPs;
    }

    public BigDecimal buscarValorEnquadramentoFuncional(ContratoFP contratoFP, Date dataOperacao) {
        String sql = "select enqpcs.vencimentobase " +
            " from enquadramentofuncional eq " +
            "         inner join enquadramentopcs enqpcs " +
            "                    on enqpcs.categoriapcs_id = eq.categoriapcs_id and enqpcs.progressaopcs_id = eq.progressaopcs_id " +
            "  and :dataReferencia between eq.iniciovigencia and coalesce(eq.finalvigencia, :dataReferencia) " +
            "  and :dataReferencia between enqpcs.iniciovigencia and coalesce(enqpcs.finalvigencia, :dataReferencia) " +
            "  and eq.contratoservidor_id = :idContrato " +
            " order by eq.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contratoFP.getId());
        q.setParameter("dataReferencia", dataOperacao);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) resultList.get(0);
    }

    public List<ValorContribuicaoPrevidenciaComplementarVO> buscarValoresContribuicaoParaServidor(ContratoFP contratoFP, Integer mes, Integer ano) {
        String sql = "select case" +
            "           when ev.tipocontribuicaobbprev = 'SERVIDOR' " +
            "               then sum(coalesce(itemficha.valor, 0)) end valorservidor, " +
            "       case" +
            "           when ev.tipocontribuicaobbprev = 'PATROCINADOR' " +
            "               then sum(coalesce(itemficha.valor, 0)) end valorpatrocinador, " +
            "       ev.codigocontribuicaobbprev                        codigo " +
            " from itemfichafinanceirafp itemficha " +
            "         inner join fichafinanceirafp ficha " +
            "                    on itemficha.fichafinanceirafp_id = ficha.id " +
            "         inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
            "         inner join eventofp ev on itemficha.eventofp_id = ev.id " +
            " where folha.ano = :ano " +
            "  and folha.mes = :mes " +
            "  and ficha.vinculofp_id = :contrato " +
            "  and ev.tipocontribuicaobbprev is not null " +
            "  and folha.efetivadaem is not null " +
            " group by ev.codigocontribuicaobbprev, ev.tipocontribuicaobbprev";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", ano);
        q.setParameter("contrato", contratoFP.getId());
        List<Object[]> resultado = q.getResultList();

        if (resultado != null && !resultado.isEmpty()) {
            List<ValorContribuicaoPrevidenciaComplementarVO> valores = Lists.newArrayList();
            for (Object[] obj : resultado) {
                ValorContribuicaoPrevidenciaComplementarVO valor = new ValorContribuicaoPrevidenciaComplementarVO();
                valor.setValorServidor(obj[0] != null ? (BigDecimal) obj[0] : BigDecimal.ZERO);
                valor.setValorPatrocinador(obj[1] != null ? (BigDecimal) obj[1] : BigDecimal.ZERO);
                valor.setCodigo(obj[2] != null ? (String) obj[2] : "");
                valores.add(valor);
            }
            Map<String, ValorContribuicaoPrevidenciaComplementarVO> mapPercentuais = new HashMap<>();
            for (ValorContribuicaoPrevidenciaComplementarVO v : valores) {
                ValorContribuicaoPrevidenciaComplementarVO valor = mapPercentuais.get(v.getCodigo());
                if (valor != null) {
                    valor.setValorServidor(valor.getValorServidor().add(v.getValorServidor()));
                    valor.setValorPatrocinador(valor.getValorPatrocinador().add(v.getValorPatrocinador()));
                    mapPercentuais.put(valor.getCodigo(), valor);
                } else {
                    mapPercentuais.put(v.getCodigo(), v);
                }
            }
            return Lists.newArrayList(mapPercentuais.values());
        }
        return Lists.newArrayList();
    }
}
