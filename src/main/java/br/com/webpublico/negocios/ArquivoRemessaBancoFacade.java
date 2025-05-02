/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ValidadorValoresOBN600;
import br.com.webpublico.enums.SituacaoBordero;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.ArquivoOBN600;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Stateless
public class ArquivoRemessaBancoFacade extends AbstractFacade<ArquivoRemessaBanco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private BorderoFacade borderoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private LayoutArquivoRemessaFacade layoutArquivoRemessaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoOBN600 arquivoOBN600;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ConfiguracaoArquivoObnFacade configuracaoArquivoObnFacade;

    public ArquivoRemessaBancoFacade() {
        super(ArquivoRemessaBanco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM ARQUIVOREMESSABANCO CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public void salvar(ArquivoRemessaBanco entity, List<Bordero> borderosExclusao) {
        validarBorderoEmOutroArquivoRemessaBanco(entity);
        for (Bordero bordero : borderosExclusao) {
            bordero.setSituacao(SituacaoBordero.DEFERIDO);
            bordero.setDataGeracaoArquivo(null);
            alterarSituacaoItemBordero(bordero, SituacaoItemBordero.DEFERIDO);
            em.merge(bordero);
        }
        BigDecimal valor = BigDecimal.ZERO;
        Long quant = 0l;
        for (ArquivoRemBancoBordero arquivoRemBancoBordero : entity.getArquivoRemBancoBorderos()) {
            valor = valor.add(arquivoRemBancoBordero.getBordero().getValor());
            Bordero bordero = getBorderoFacade().recuperar(arquivoRemBancoBordero.getBordero().getId());
            alterarSituacaoItemBordero(bordero, SituacaoItemBordero.EFETUADO);
            quant++;
        }
        entity.setValorTotalBor(valor);
        entity.setValorTotalDoc(valor);
        entity.setQntdDocumento(quant);
        em.merge(entity);
    }

    @Override
    public void salvarNovo(ArquivoRemessaBanco entity) {
        validarBorderoEmOutroArquivoRemessaBanco(entity);
        entity.setNumero(getUltimoNumero());
        entity.setDataGeracao(sistemaFacade.getDataOperacao());
        for (ArquivoRemBancoBordero arbb : entity.getArquivoRemBancoBorderos()) {
            Bordero bordero = getBorderoFacade().recuperar(arbb.getBordero().getId());
            bordero.setSituacao(SituacaoBordero.AGUARDANDO_ENVIO);
            bordero.setDataGeracaoArquivo(sistemaFacade.getDataOperacao());
            alterarSituacaoItemBordero(bordero, SituacaoItemBordero.EFETUADO);
        }
        em.persist(entity);
    }

//    /*
//    Método para ajustar a geração do arquivo com ordens bancária anterior a data(19/02/2016). Será removido posteriormente.
//    */
//
//    private void gerarNumeroREProvisorio(Bordero bordero) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        Date dataLimite = null;
//        try {
//            dataLimite = sdf.parse("19/02/2016");
//            if (Util.getDataHoraMinutoSegundoZerado(bordero.getDataGeracao()).compareTo(Util.getDataHoraMinutoSegundoZerado(dataLimite)) <= 0) {
//                borderoFacade.definirNumeroOrdemBancariaNoMovimento(bordero);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    private void alterarSituacaoItemBordero(Bordero bordero, SituacaoItemBordero situacaoItemBordero) {
        for (BorderoPagamento pagamento : bordero.getListaPagamentos()) {
            if (!pagamento.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                pagamento.setSituacaoItemBordero(situacaoItemBordero);
            }
        }

        for (BorderoPagamentoExtra pagamentoExtra : bordero.getListaPagamentosExtra()) {
            if (!pagamentoExtra.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                pagamentoExtra.setSituacaoItemBordero(situacaoItemBordero);
            }
        }

        for (BorderoTransferenciaFinanceira transferenciaContaFinanceira : bordero.getListaTransferenciaFinanceira()) {
            if (!transferenciaContaFinanceira.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                transferenciaContaFinanceira.setSituacaoItemBordero(situacaoItemBordero);
            }
        }

        for (BorderoTransferenciaMesmaUnidade transferenciaMesmaUnidade : bordero.getListaTransferenciaMesmaUnidade()) {
            if (!transferenciaMesmaUnidade.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                transferenciaMesmaUnidade.setSituacaoItemBordero(situacaoItemBordero);
            }
        }

        for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : bordero.getListaLiberacaoCotaFinanceira()) {
            if (!borderoLiberacaoFinanceira.getSituacaoItemBordero().equals(SituacaoItemBordero.INDEFIRIDO)) {
                borderoLiberacaoFinanceira.setSituacaoItemBordero(situacaoItemBordero);
            }
        }
        em.merge(bordero);
    }


    public List<ArquivoRemBancoBordero> recuperaArquivo(ArquivoRemessaBanco arb) {
        String sql = "SELECT ARRB.* "
            + " FROM ARQUIVOREMESSABANCO ARB"
            + " INNER JOIN ARQUIVOREMBANCOBORDERO ARRB ON ARRB.ARQUIVOREMESSABANCO_ID = ARB.ID"
            + " INNER JOIN BORDERO B ON ARRB.BORDERO_ID = B.ID"
            + " LEFT JOIN BORDEROPAGAMENTO BP ON BP.BORDERO_ID = B.ID"
            + " LEFT JOIN PAGAMENTO P ON BP.PAGAMENTO_ID = P.ID"
            + " LEFT JOIN BORDEROPAGAMENTOEXTRA BPE ON BPE.BORDERO_ID = B.ID"
            + " LEFT JOIN PAGAMENTOEXTRA PE ON BPE.PAGAMENTOEXTRA_ID = PE.ID"
            + " WHERE ARB.ID = :param";
        Query q = em.createNativeQuery(sql, ArquivoRemBancoBordero.class);
        q.setParameter("param", arb.getId());
        return q.getResultList();
    }

    @Override
    public ArquivoRemessaBanco recuperar(Object id) {
        ArquivoRemessaBanco cd = em.find(ArquivoRemessaBanco.class, id);
        cd.getArquivoRemBancoBorderos().size();
        for (ArquivoRemBancoBordero arquivoBordero : cd.getArquivoRemBancoBorderos()) {
            Bordero b = em.getReference(Bordero.class, arquivoBordero.getBordero().getId());
            b.getListaPagamentos().size();
            b.getListaPagamentosExtra().size();
            b.getListaTransferenciaFinanceira().size();
            b.getListaTransferenciaMesmaUnidade().size();
            b.getListaLiberacaoCotaFinanceira().size();
            arquivoBordero.setBordero(b);
        }
        return cd;
    }

    public List<ValidadorValoresOBN600> recuperarValidadorValores(ArquivoRemessaBanco arquivoRemessaBanco) {

        List<ValidadorValoresOBN600> retorno = recuperarValidadorValoresPegandoDoMovimento(arquivoRemessaBanco);
        List<ValidadorValoresOBN600> itensBordero = recuperarValidadorValoresPegandoDoItemBordero(arquivoRemessaBanco);
        List<ValidadorValoresOBN600> borderos = recuperarValidadorValoresPegandoBordero(arquivoRemessaBanco);

        for (ValidadorValoresOBN600 validador : retorno) {
            for (ValidadorValoresOBN600 item : itensBordero) {
                if (validador.getIdBordero().equals(item.getIdBordero())) {
                    validador.setValorItemBordero(item.getValorItemBordero());
                }
            }

            for (ValidadorValoresOBN600 bordero : borderos) {
                if (validador.getIdBordero().equals(bordero.getIdBordero())) {
                    validador.setTotalBordero(bordero.getTotalBordero());
                }
            }
        }

        for (ValidadorValoresOBN600 validador : retorno) {
            if (validador.getTotalMovimentos().compareTo(validador.getValorItemBordero()) != 0
                || validador.getTotalMovimentos().compareTo(validador.getTotalBordero()) != 0
                || validador.getValorItemBordero().compareTo(validador.getTotalBordero()) != 0) {
                validador.setPossuiDiferenca(true);
            } else {
                validador.setPossuiDiferenca(false);
            }
        }

        Collections.sort(retorno, new Comparator<ValidadorValoresOBN600>() {
            @Override
            public int compare(ValidadorValoresOBN600 o1, ValidadorValoresOBN600 o2) {
                return o2.getBordero().compareTo(o1.getBordero());
            }
        });
        return retorno;
    }

    public List<ValidadorValoresOBN600> recuperarValidadorValoresPegandoBordero(ArquivoRemessaBanco
                                                                                    arquivoRemessaBanco) {
        String sql = " select b.id, b.sequenciaarquivo, b.valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "where arr.id = :id " +
            "order by b.sequenciaarquivo";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("id", arquivoRemessaBanco.getId());
        List<ValidadorValoresOBN600> retorno = new ArrayList<ValidadorValoresOBN600>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            ValidadorValoresOBN600 validador = new ValidadorValoresOBN600();
            validador.setIdBordero(String.valueOf((BigDecimal) objeto[0]));
            validador.setBordero((String) objeto[1]);
            validador.setTotalBordero((BigDecimal) objeto[2]);
            retorno.add(validador);
        }
        return retorno;
    }

    public List<ValidadorValoresOBN600> recuperarValidadorValoresPegandoDoItemBordero(ArquivoRemessaBanco
                                                                                          arquivoRemessaBanco) {
        String sql = "select id, bordero, sum(valor) from ( " +
            "select b.id, b.sequenciaarquivo as bordero, bp.valor as valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join borderopagamento bp on b.id = bp.bordero_id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, bpx.valor as valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join borderopagamentoextra bpx on b.id = bpx.bordero_id " +
            "where arr.id = :id " +
            "and bpx.situacaoitembordero <> 'INDEFIRIDO' " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, bpx.valor as valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROTRANSFFINANCEIRA bpx on b.id = bpx.bordero_id " +
            "where arr.id = :id " +
            "and bpx.situacaoitembordero <> 'INDEFIRIDO' " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, bpx.valor as valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROLIBCOTAFINANCEIRA bpx on b.id = bpx.bordero_id " +
            "where arr.id = :id " +
            "and bpx.situacaoitembordero <> 'INDEFIRIDO' " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, bpx.valor as valor " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROTRANSFMESMAUNIDADE bpx on b.id = bpx.bordero_id " +
            "where arr.id = :id " +
            "and bpx.situacaoitembordero <> 'INDEFIRIDO' " +
            ") " +
            "group by id, bordero " +
            "order by id, bordero";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("id", arquivoRemessaBanco.getId());
        List<ValidadorValoresOBN600> retorno = new ArrayList<ValidadorValoresOBN600>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            ValidadorValoresOBN600 validador = new ValidadorValoresOBN600();
            validador.setIdBordero(String.valueOf((BigDecimal) objeto[0]));
            validador.setBordero((String) objeto[1]);
            validador.setValorItemBordero((BigDecimal) objeto[2]);
            retorno.add(validador);
        }
        return retorno;
    }

    public List<ValidadorValoresOBN600> recuperarValidadorValoresPegandoDoMovimento(ArquivoRemessaBanco
                                                                                        arquivoRemessaBanco) {
        String sql = "select id, arquivo, sum(pagamento), sum(despesa), sum(total) from ( " +
            "select b.id, b.sequenciaarquivo as arquivo,  sum(p.valorfinal) as pagamento,0 as despesa,  (0 + sum(coalesce(p.valorfinal,0))) as total " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join borderopagamento bp on b.id = bp.bordero_id " +
            "inner join pagamento p on bp.pagamento_id= p.id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "group by b.id, b.sequenciaarquivo " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, 0 as pagamento, sum(p.valor) as despesa,  (0 + sum(coalesce(p.valor,0))) as total " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join borderopagamentoextra bp on b.id = bp.bordero_id " +
            "inner join pagamentoextra p on bp.pagamentoextra_id= p.id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "group by b.id, b.sequenciaarquivo " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, 0 as pagamento, sum(p.valor) as despesa,  (0 + sum(coalesce(p.valor,0))) as total " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROTRANSFFINANCEIRA bp on b.id = bp.bordero_id " +
            "inner join TRANSFERENCIACONTAFINANC p on bp.TRANSFFINANCEIRA_ID = p.id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "group by b.id, b.sequenciaarquivo " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, 0 as pagamento, sum(p.valor) as despesa,  (0 + sum(coalesce(p.valor,0))) as total " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROLIBCOTAFINANCEIRA bp on b.id = bp.bordero_id " +
            "inner join LiberacaoCotaFinanceira p on bp.LIBCOTAFINANCEIRA_ID= p.id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "group by b.id, b.sequenciaarquivo " +
            " " +
            "union all " +
            " " +
            "select b.id, b.sequenciaarquivo, 0 as pagamento, sum(p.valor) as despesa,  (0 + sum(coalesce(p.valor,0))) as total " +
            "from arquivoremessabanco arr " +
            "inner join arquivorembancobordero arb on arr.id = arb.arquivoremessabanco_id " +
            "inner join bordero b on arb.bordero_id = b.id " +
            "inner join BORDEROTRANSFMESMAUNIDADE bp on b.id = bp.bordero_id " +
            "inner join TransferenciaMesmaUnidade p on bp.TRANSFMESMAUNIDADE_ID= p.id " +
            "where arr.id = :id " +
            "and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "group by b.id, b.sequenciaarquivo " +
            ") " +
            "group by id, arquivo " +
            "order by id, arquivo";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("id", arquivoRemessaBanco.getId());
        List<ValidadorValoresOBN600> retorno = new ArrayList<ValidadorValoresOBN600>();
        List resultList = consulta.getResultList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;

            ValidadorValoresOBN600 validador = new ValidadorValoresOBN600();
            validador.setIdBordero(String.valueOf((BigDecimal) objeto[0]));
            validador.setBordero((String) objeto[1]);
            validador.setPagamento((BigDecimal) objeto[2]);
            validador.setDespesaExtra((BigDecimal) objeto[3]);
            validador.setTotalMovimentos((BigDecimal) objeto[4]);
            retorno.add(validador);
        }
        return retorno;
    }

    public List<GuiaPagamento> buscarGuiasPagamentoPorOrdemBancaria(String condicao) {
        String sql = " select guiapag.* " +
            "           from guiapagamento guiapag " +
            "           inner join pagamento pag on pag.id = guiapag.pagamento_id " +
            "           inner join borderopagamento bp on bp.pagamento_id = pag.id " +
            "           inner join bordero ob on ob.id = bp.bordero_id " +
            "           where ob.id in (" + condicao + ")";
        Query q = em.createNativeQuery(sql, GuiaPagamento.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public List<GuiaPagamentoExtra> buscarGuiasDespesaExtraPorOrdemBancaria(String condicao) {
        String sql = " select guiaDesp.* " +
            "           from guiapagamentoextra guiaDesp " +
            "           inner join pagamentoextra desp on desp.id = guiaDesp.pagamentoextra_id " +
            "           inner join borderopagamentoextra bp on bp.pagamentoextra_id = desp.id " +
            "           inner join bordero ob on ob.id = bp.bordero_id " +
            "           where ob.id in (" + condicao + ")";
        Query q = em.createNativeQuery(sql, GuiaPagamentoExtra.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private void validarBorderoEmOutroArquivoRemessaBanco(ArquivoRemessaBanco selecionado) {
        ValidacaoException ve = new ValidacaoException();
        for (ArquivoRemBancoBordero arbb : selecionado.getArquivoRemBancoBorderos()) {
            ArquivoRemessaBanco arquivoComMesmoBordeiro = buscarArquivoPorBorderoEmOutroArquivo(arbb);
            if (arquivoComMesmoBordeiro != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Ordem Bancária <b>" + arbb.getBordero().toString() +
                    "</b> já foi adicionado em outra OBN 600 <b>" + arquivoComMesmoBordeiro.getNumero() + " - " + DataUtil.getDataFormatada(arquivoComMesmoBordeiro.getDataGeracao()) + "</b>"
                );
            }
        }
        ve.lancarException();
    }

    private ArquivoRemessaBanco buscarArquivoPorBorderoEmOutroArquivo(ArquivoRemBancoBordero arbb) {
        if (arbb == null || arbb.getBordero() == null || arbb.getArquivoRemessaBanco() == null) return null;
        String sql = "select arb.* " +
            " from arquivoremessabanco arb " +
            "    inner join arquivorembancobordero arbb on arbb.arquivoremessabanco_id = arb.id " +
            "    inner join bordero b on b.id = arbb.bordero_id " +
            " where b.id = :idBordero ";
        if (arbb.getArquivoRemessaBanco().getId() != null) {
            sql += " and arb.id <> :idArquivo ";
        }
        Query q = em.createNativeQuery(sql, ArquivoRemessaBanco.class);
        q.setParameter("idBordero", arbb.getBordero().getId());
        if (arbb.getArquivoRemessaBanco().getId() != null) {
            q.setParameter("idArquivo", arbb.getArquivoRemessaBanco().getId());
        }
        List<ArquivoRemessaBanco> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    public void salvaAssociativa(ArquivoRemBancoBordero arbb) {
        em.merge(arbb);
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public BorderoFacade getBorderoFacade() {
        return borderoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public LayoutArquivoRemessaFacade getLayoutArquivoRemessaFacade() {
        return layoutArquivoRemessaFacade;
    }

    public ArquivoOBN600 getArquivoOBN600() {
        return arquivoOBN600;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public ConfiguracaoArquivoObnFacade getConfiguracaoArquivoObnFacade() {
        return configuracaoArquivoObnFacade;
    }
}
