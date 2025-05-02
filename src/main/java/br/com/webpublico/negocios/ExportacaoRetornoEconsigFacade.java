package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 13/01/14
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ExportacaoRetornoEconsigFacade extends AbstractFacade<ExportarArquivoRetorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNew;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    //    private Integer mes;
//    private Integer ano;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private MiddleRHFacade middleRHFacade;
    @EJB
    private MotivoRejeicaoFacade motivoRejeicaoFacade;

    public ExportacaoRetornoEconsigFacade() {
        super(ExportarArquivoRetorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public void gerarArquivo(ExportarArquivoRetorno selecionado, List<LancamentoFP> lista) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, String> motivosRejeicao = new LinkedHashMap<>();
        carregarMotivosRejeicao(motivosRejeicao);

        for (LancamentoFP lancamento : lista) {
            if (selecionado.isCancelar()) {
                return;
            }
            selecionado.somaContador();
            sb.append(gerarArquivoRetorno(lancamento, selecionado, motivosRejeicao));
        }
        selecionado.setLiberado(true);
        selecionado.setConteudo(sb.toString());
        middleRHFacade.salvarNovoTransactional(selecionado);

    }

    private void carregarMotivosRejeicao(Map<Integer, String> motivosRejeicao) {
        for (MotivoRejeicao motivoRejeicao : motivoRejeicaoFacade.lista()) {
            motivosRejeicao.put(motivoRejeicao.getCodigo(), motivoRejeicao.getDescricao());
        }

    }

    private boolean lancamentoRejeitado(LancamentoFP lancamento) {
        DateTime dataInicial = new DateTime(lancamento.getMesAnoInicial());
        EventoTotalItemFicha eventoTotalItemFicha = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraByEvento(lancamento.getVinculoFP(), dataInicial.getMonthOfYear(), dataInicial.getYear(), lancamento.getEventoFP().getCodigo());
        if (eventoTotalItemFicha == null) {
            return true;
        }
        if (eventoTotalItemFicha.getTotal() != null && eventoTotalItemFicha.getTotal().compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }
        return true;
    }


    public void substituiCaractere(StringBuilder s) {
        s.toString().replaceAll("[ãâàáä]", "a").replaceAll("[êèéë]", "e").replaceAll("[îìíï]", "i").replaceAll("[õôòóö]", "o").replaceAll("[ûúùü]", "u").replaceAll("[ÃÂÀÁÄ]", "A").replaceAll("[ÊÈÉË]", "E").replaceAll("[ÎÌÍÏ]", "I").replaceAll("[ÕÔÒÓÖ]", "O").replaceAll("[ÛÙÚÜ]", "U").replace('ç', 'c').replace('Ç', 'C').replace('ñ', 'n').replace('Ñ', 'N').replaceAll("!", "").replaceAll("\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*", " ").replaceAll("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]", " ").replaceAll("[\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]", " ");
    }

    public String gerarArquivoRetorno(LancamentoFP l, ExportarArquivoRetorno selecionado, Map<Integer, String> motivosRejeicao) {
        StringBuilder linhaRetorno = new StringBuilder();
        try {
            getLinhaArquivoVinculos(linhaRetorno, l, l.getVinculoFP(), selecionado, motivosRejeicao);
        } catch (ValidacaoException val) {
            logger.error("Erro durante a geração", val);
            linhaRetorno = new StringBuilder();
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception e) {
            logger.error("Erro durante a geração", e);
        }
        substituiCaractere(linhaRetorno);

        return linhaRetorno.toString();
    }

    private void getLinhaArquivoVinculos(StringBuilder linhaRetorno, LancamentoFP l, VinculoFP contrato, ExportarArquivoRetorno selecionado, Map<Integer, String> motivosRejeicao) {
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(l.getEventoFP().getCodigo(), 4, "0"));
        linhaRetorno.append(" ");
        if (contrato.getMatriculaFP().getPessoa() != null) {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(contrato.getMatriculaFP().getPessoa().getNome(), 40, " "));
        } else {
            linhaRetorno.append(StringUtils.rightPad(" ", 40, " "));
        }
        linhaRetorno.append(" ");
        //sub folha
        String codigo = "00";
        RecursoDoVinculoFP recursoDoVinculoFP = recursoDoVinculoFPFacade.recuperarRecursosDoVinculoByVinculoUltimoVigente(contrato);
        if (recursoDoVinculoFP != null) {
            String codigoRecurso = recursoDoVinculoFP.getRecursoFP().getCodigo();
//        LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigentePorContratoFPMes(contrato, Util.criaDataPrimeiroDiaMesJoda(selecionado.getMes(), selecionado.getAno()).toDate());
//        //lotação
//        if (lotacao != null) {
//            HierarquiaOrganizacional ho = null;
//            ho = hierarquiaOrganizacionalMap.get(lotacao.getUnidadeOrganizacional().getId());
//            if (ho == null)
//                ho = hierarquiaOrganizacionalFacadeNew.recuperaHierarquiaOrganizacionalPelaUnidade(lotacao.getUnidadeOrganizacional().getId());
//            if (ho != null) {
            String codigoGrande = codigoRecurso.substring(0, codigoRecurso.length() - 2);
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(codigoGrande.replaceAll("\\.", "") + " ", 12, "0"));
//            } else {
//                linhaRetorno.append(StringUtils.leftPad("0", 12));
//            }
//        } else {
//            linhaRetorno.append(StringUtils.leftPad("0", 12));
//        }


            linhaRetorno.append(StringUtils.leftPad(" ", 2));

            codigo = recursoDoVinculoFP.getRecursoFP().getCodigo();
            codigo = codigo.substring(codigo.length() - 2, codigo.length());
        }
        linhaRetorno.append(StringUtils.leftPad(codigo, 2, "0"));
        //fim sub folha
        linhaRetorno.append(StringUtils.leftPad(" ", 4));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getMatriculaFP().getMatricula(), 7, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtils.leftPad(contrato.getNumero(), 2, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        if (contrato instanceof ContratoFP) {
            ContratoFP contratoFP = (ContratoFP) contrato;
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(contratoFP.getCargo().getDescricao(), 35, " "));
        } else if (contrato instanceof Pensionista) {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita("PENSIONISTA", 35, " "));
        } else if (contrato instanceof Aposentadoria) {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita("APOSENTADO(A)", 35, " "));
        } else {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(" ", 35, " "));
        }
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        if (contrato.getInicioVigencia() == null) {
            throw new ValidacaoException("Contrato está sem início de vigência " + contrato);
        }
        linhaRetorno.append(StringUtil.cortaOuCompletaDireita(Util.dateToString(contrato.getInicioVigencia()), 10, " "));
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(getValorFichaFinanceira(contrato, l.getEventoFP().getCodigo(), selecionado.getMes(), selecionado.getAno()).replaceAll("\\.", ""), 11, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        String cpf = "0";
        if (contrato.getMatriculaFP().getPessoa() != null && contrato.getMatriculaFP().getPessoa().getCpf() != null) {
            cpf = contrato.getMatriculaFP().getPessoa().getCpf();
        }
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(cpf.replaceAll("\\.", "").replaceAll("-", ""), 11, "0"));

        if (l.getMotivoRejeicao() != null || lancamentoRejeitado(l)) {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("R");
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(getMotivoRejeicao(l, motivosRejeicao), 255, " "));
        } else {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("P");
            linhaRetorno.append(StringUtils.rightPad(" ", 258));
        }
        linhaRetorno.append("\r\n");
    }

    private String getMotivoRejeicao(LancamentoFP l, Map<Integer, String> motivosRejeicao) {
        if (l.getMotivoRejeicao() != null) {
            return l.getMotivoRejeicao().getDescricao();
        }
        return motivosRejeicao.get(11);
    }

    public String getValorFichaFinanceira(VinculoFP vinculoFP, String codigoEvento, Integer mes, Integer ano) {
        EventoTotalItemFicha totalItemFicha = fichaFinanceiraFPFacade.totalEventosItemFichaFinanceiraByEvento(vinculoFP, mes, ano, codigoEvento);
        if (totalItemFicha != null) {
            BigDecimal valor = totalItemFicha.getTotal().setScale(2);

            return valor.toString().replaceAll("\\.", "").replaceAll(",", "");
        }
        return BigDecimal.ZERO + "";
    }

    @Deprecated
    private void getLinhaArquivoAposentado(StringBuilder linhaRetorno, LancamentoFP l, Aposentadoria ap, ExportarArquivoRetorno selecinado) {
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(l.getEventoFP().getCodigo(), 4, "0"));
        linhaRetorno.append(" ");
        if (l.getVinculoFP().getMatriculaFP().getPessoa() != null) {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(ap.getMatriculaFP().getPessoa().getNome(), 40, " "));
        } else {
            linhaRetorno.append(StringUtils.rightPad(" ", 40, " "));
        }
        linhaRetorno.append(" ");

        LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(ap.getContratoFP(), lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao());
        //lotação
//        if (lotacao != null) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacadeNew.recuperaHierarquiaOrganizacionalPelaUnidade(lotacao.getUnidadeOrganizacional().getId());
        if (ho != null) {
            String codigo = ho.getCodigo().substring(2, ho.getCodigo().length());
            linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(codigo.replaceAll("\\.", "") + " ", 12, "0"));
        } else {
            linhaRetorno.append(StringUtils.leftPad("0", 12));
        }
//        } else {
//            linhaRetorno.append(StringUtils.leftPad("0", 12));
//        }


        linhaRetorno.append(StringUtils.leftPad(" ", 2));
        //sub folha
        String codigo = "00";
        RecursoDoVinculoFP recursoDoVinculoFP = recursoDoVinculoFPFacade.recuperarRecursoDoVinculo(ap);
        if (recursoDoVinculoFP != null) {
            codigo = recursoDoVinculoFP.getRecursoFP().getCodigo();
            codigo = codigo.substring(codigo.length() - 2, codigo.length());
        }
        linhaRetorno.append(StringUtils.leftPad(codigo, 2, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 4));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(ap.getMatriculaFP().getMatricula(), 7, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(ap.getNumero(), 2, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtil.cortaOuCompletaDireita(ap.getContratoFP().getCargo().getDescricao(), 35, " "));
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        linhaRetorno.append(StringUtil.cortaOuCompletaDireita(Util.dateToString(ap.getContratoFP().getDataAdmissao()), 10, " "));
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(getValorFichaFinanceira(ap, l.getEventoFP().getCodigo(), selecinado.getMes(), selecinado.getAno()).replaceAll("\\.", ""), 11, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(ap.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""), 11, "0"));

        if (l.getMotivoRejeicao() != null) {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("R");
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(l.getMotivoRejeicao().getDescricao(), 255, " "));
        } else {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("P");
        }
        linhaRetorno.append(System.getProperty("line.separator"));
    }

    @Deprecated
    private void getLinhaArquivoPesnionista(StringBuilder linhaRetorno, LancamentoFP l, Pensionista p, ExportarArquivoRetorno selecinado) {
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(l.getEventoFP().getCodigo(), 4, "0"));
        linhaRetorno.append(" ");
        if (p.getMatriculaFP().getPessoa() != null) {
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(p.getMatriculaFP().getPessoa().getNome(), 40, " "));
        } else {
            linhaRetorno.append(StringUtils.rightPad(" ", 40, " "));
        }
        linhaRetorno.append(" ");

//        LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(p);
        //lotação
//        if (lotacao != null) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacadeNew.recuperaHierarquiaOrganizacionalPelaUnidade(p.getUnidadeOrganizacional().getId());
        if (ho != null) {
            String codigo = ho.getCodigo().substring(2, ho.getCodigo().length());
            linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(codigo.replaceAll("\\.", "") + " ", 12, "0"));
        } else {
            linhaRetorno.append(StringUtils.leftPad("0", 12));
        }
//        } else {
//            linhaRetorno.append(StringUtils.leftPad("0", 12));
//        }

        linhaRetorno.append(StringUtils.leftPad(" ", 2));
        //sub folha
        String codigo = "00";
        RecursoDoVinculoFP recursoDoVinculoFP = recursoDoVinculoFPFacade.recuperarRecursoDoVinculo(p);
        if (recursoDoVinculoFP != null) {
            codigo = recursoDoVinculoFP.getRecursoFP().getCodigo();
            codigo = codigo.substring(codigo.length() - 2, codigo.length());
        }
        linhaRetorno.append(StringUtils.leftPad(codigo, 2, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 4));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(p.getMatriculaFP().getMatricula(), 7, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtils.leftPad(p.getNumero(), 2, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtils.rightPad(" ", 35, " "));
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        linhaRetorno.append(StringUtil.cortaOuCompletaDireita(Util.dateToString(p.getInicioVigencia()), 10, " "));
        linhaRetorno.append(StringUtils.leftPad(" ", 1));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(getValorFichaFinanceira(p, l.getEventoFP().getCodigo(), selecinado.getMes(), selecinado.getAno()).replaceAll("\\.", ""), 11, "0"));
        linhaRetorno.append(StringUtils.leftPad(" ", 3));
        linhaRetorno.append(StringUtil.cortaOuCompletaEsquerda(p.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""), 11, "0"));

        if (l.getMotivoRejeicao() != null) {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("R");
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append(StringUtil.cortaOuCompletaDireita(l.getMotivoRejeicao().getDescricao(), 255, " "));
        } else {
            linhaRetorno.append(StringUtils.leftPad(" ", 3));
            linhaRetorno.append("P");
        }
        linhaRetorno.append(System.getProperty("line.separator"));
    }

    public boolean jaExisteLancamentoParaMesEAnoEFolha(ExportarArquivoRetorno retorno) {
        Query q = em.createQuery("select retorno from ExportarArquivoRetorno  retorno " +
            "     where retorno.mes = :mes " +
            "       and retorno.ano = :ano " +
            "       and retorno.tipoFolhaDePagamento = :tipoFolha" +
            "       and retorno.versao = :versao");
        q.setParameter("mes", retorno.getMes());
        q.setParameter("ano", retorno.getAno());
        q.setParameter("tipoFolha", retorno.getTipoFolhaDePagamento());
        q.setParameter("versao", retorno.getVersao());
        return !q.getResultList().isEmpty();
    }

}
