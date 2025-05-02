package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ArquivoRaisException;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Claudio
 */
@Stateless
public class ArquivoRAISFacade extends AbstractFacade<ArquivoRAIS> {

    private static final Logger logger = LoggerFactory.getLogger(ArquivoRAISFacade.class);
    private static final String DESCRICAO_MODULO_EXPORTACAO = "RAIS";
    private final SimpleDateFormat sdfDiaMesAno = new SimpleDateFormat("ddMMyyyy");
    private final SimpleDateFormat sdfDiaMes = new SimpleDateFormat("ddMM");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ArquivoRAIS selecionado;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private PessoaJuridica pessoaJuridica;
    private RegistroRAISTipo0 registroRAISTipo0;
    private RegistroRAISTipo1 registroRAISTipo1;
    private RegistroRAISTipo2 registroRAISTipo2;
    private RegistroRAISTipo9 registroRAISTipo9;
    private CarteiraTrabalho carteiraTrabalho;
    private Telefone telefone;
    private List<ContratoFP> contratosVigente;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ItemEntidadeDPContasFacade itemEntidadeDPContasFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private EnderecoCorreio enderecoCorreio;
    @EJB
    private CidadeFacade cidadeFacade;
    @Resource
    private SessionContext sctx;
    @EJB
    private ConselhoClasseOrdemFacade conselhoClasseOrdemFacade;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private ArquivoRaisAcompanhamentoFacade arquivoRaisAcompanhamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;

    public ArquivoRAISFacade() {
        super(ArquivoRAIS.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public SessionContext getSctx() {
        return sctx;
    }

    public void setSctx(SessionContext sctx) {
        this.sctx = sctx;
    }

    private BigDecimal getSomaDosEventosMensal(ContratoFP cfp, Mes mes, Integer ano, String nomeReduzido, List<TipoFolhaDePagamento> tiposFolha) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN eventoFP evento ON evento.id = iff.eventoFP_id                                       "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND fp.mes = :mes                                                                        "
            + "       and iff.tipoEventoFP = evento.tipoEventoFP                                             "
            + "       AND ff.vinculofp_id = :cfp_id                                                            "
            + folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  AND ge.nomereduzido = :nomeReduzido))                         "
            + " -                                                                                              "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
            + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
            + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
            + "      WHERE fp.ano = :ano                                                                       "
            + "        AND fp.mes = :mes                                                                       "
            + "        AND ff.vinculofp_id = :cfp_id                                                           "
            + folhas
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
            + "                                 WHERE me.descricao = :descricaoModulo                          "
            + "                                   AND ige.operacaoformula = :subtracao                         "
            + "                                   AND ge.nomereduzido = :nomeReduzido)) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("cfp_id", cfp.getId());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido);
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

//    public BigDecimal getSomaDosEventosMes(ContratoFP c, ArquivoRAIS selecionado, Mes mes, String nomeReduzido, List<TipoFolhaDePagamento> tipos) {
//        StringBuilder sql = new StringBuilder();
//        sql.append(" select (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                       ");
//        sql.append("inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                             ");
//        sql.append("inner join folhadepagamento  fp on fp.id = ff.folhadepagamento_id                               ");
//        sql.append("     where fp.ano = :ano                                                                        ");
//        sql.append("       and fp.mes = :mes                                                                        ");
//        sql.append("       and fp.tipoFolhaDePagamento in :tipos");
//        sql.append("       and iff.tipoEventoFP = 'VANTAGEM'                                                        ");
//        sql.append("       and ff.vinculofp_id = :vinculo_id                                                        ");
//        sql.append("       and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige              ");
//        sql.append("                           inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id      ");
//        sql.append("                           inner join moduloexportacao me on ge.moduloexportacao_id = me.id     ");
//        sql.append("                                where me.descricao = :descricaoModulo                           ");
//        sql.append("                                  and ige.operacaoformula = :adicao                             ");
//        sql.append("                                  and ge.nomereduzido = :nomeReduzido) ");
//        sql.append(" AND ff.vinculofp_id IN (SELECT vinc.ID FROM LotacaoFuncional lot            ");
//        sql.append("            INNER JOIN vinculofp vinc ON lot.vinculofp_id = vinc.ID       ");
//        sql.append("            INNER JOIN HierarquiaOrganizacional ho ON ho.subordinada_id = lot.unidadeorganizacional_id    ");
//        sql.append("            where ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'                    ");
//        String juncao = " and ( ";
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            sql.append(juncao + "   ho.codigo like :ho" + ho.hashCode());
//            juncao = " or ";
//        }
//        sql.append(" ) ) ) ");
//        sql.append(" -                                                                                              ");
//        sql.append("    (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                           ");
//        sql.append(" inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                            ");
//        sql.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id                               ");
//        sql.append("      where fp.ano = :ano                                                                       ");
//        sql.append("        and fp.mes = :mes                                                                       ");
//        sql.append("       and fp.tipoFolhaDePagamento in :tipos");
//        sql.append("       and iff.tipoEventoFP = 'DESCONTO'                                                        ");
//        sql.append("        and ff.vinculofp_id = :vinculo_id                                                       ");
//        sql.append("        and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige             ");
//        sql.append("                            inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id     ");
//        sql.append("                            inner join moduloexportacao me on ge.moduloexportacao_id = me.id    ");
//        sql.append("                                 where me.descricao = :descricaoModulo                          ");
//        sql.append("                                   and ige.operacaoformula = :subtracao                         ");
//        sql.append("                                   and ge.nomereduzido = :nomeReduzido) ");
//        sql.append(" AND ff.vinculofp_id IN (SELECT vinc.ID FROM LotacaoFuncional lot            ");
//        sql.append("            INNER JOIN vinculofp vinc ON lot.vinculofp_id = vinc.ID       ");
//        sql.append("            INNER JOIN HierarquiaOrganizacional ho ON ho.subordinada_id = lot.unidadeorganizacional_id    ");
//        sql.append("            where ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'                    ");
//        juncao = " and ( ";
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            sql.append(juncao + "   ho.codigo like :ho" + ho.hashCode());
//            juncao = " or ";
//        }
//        sql.append(" )  ");
//        sql.append(" ) ) as resultado from dual ");
//
//        Query q = em.createNativeQuery(sql.toString());
//        q.setParameter("ano", selecionado.getExercicio().getAno());
//        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
//        q.setParameter("tipos", tipos);
//
//        q.setParameter("vinculo_id", c.getId());
//        q.setParameter("descricaoModulo", "RAIS");
//        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
//        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
//        q.setParameter("nomeReduzido", nomeReduzido);
//
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            q.setParameter("ho" + ho.hashCode(), ho.getCodigoSemZerosFinais() + "%");
//        }
//
//        try {
//            return (BigDecimal) q.getSingleResult();
//        } catch (NoResultException nre) {
//            return BigDecimal.ZERO;
//        }
//    }

    private BigDecimal getSomaDosEventosAnual(ContratoFP cfp, Integer ano, String nomeReduzido, List<TipoFolhaDePagamento> tiposFolha) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN eventoFP evento ON evento.id = iff.eventoFP_id                                       "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       and iff.tipoEventoFP = evento.tipoEventoFP                                             "
            + "       AND ff.vinculofp_id = :cfp_id                                                            "
            + folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  AND ge.nomereduzido = :nomeReduzido))                         "
            + " -                                                                                              "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
            + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
            + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
            + "      WHERE fp.ano = :ano                                                                       "
            + "        AND ff.vinculofp_id = :cfp_id                                                           "
            + folhas
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
            + "                                 WHERE me.descricao = :descricaoModulo                          "
            + "                                   AND ige.operacaoformula = :subtracao                         "
            + "                                   AND ge.nomereduzido = :nomeReduzido)) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("cfp_id", cfp.getId());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido);
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

//    public BigDecimal getSomaDosEventosVerbaFixa(ContratoFP c, ArquivoRAIS selecionado, String nomeReduzido, TipoFolhaDePagamento... tiposFolhaDePagamento) {
//        String tiposFolha = "";
//        for(TipoFolhaDePagamento t : tiposFolhaDePagamento) {
//            tiposFolha += "'"+t.name()+"',";
//        }
//        StringBuilder sql = new StringBuilder();
//        sql.append(" select (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                    ");
//        sql.append("inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                             ");
//        sql.append("inner join folhadepagamento  fp on fp.id = ff.folhadepagamento_id                               ");
//        sql.append("     where fp.ano = :ano                                                                        ");
//        sql.append("       and fp.tipoFolhaDePagamento in ( "+tiposFolha.substring(0, tiposFolha.length()-1) +")");
//        sql.append("       and ff.vinculofp_id = :vinculo_id                                                        ");
//        sql.append("       and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige              ");
//        sql.append("                           inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id      ");
//        sql.append("                           inner join moduloexportacao me on ge.moduloexportacao_id = me.id     ");
//        sql.append("                                where me.descricao = :descricaoModulo                           ");
//        sql.append("                                  and ige.operacaoformula = :adicao                             ");
//        sql.append("                                  and ge.nomereduzido = :nomeReduzido) ");
//        sql.append("AND ff.vinculofp_id IN (SELECT vinc.ID FROM LotacaoFuncional lot            ");
//        sql.append("            INNER JOIN vinculofp vinc ON lot.vinculofp_id = vinc.ID       ");
//        sql.append("            INNER JOIN HierarquiaOrganizacional ho ON ho.subordinada_id = lot.unidadeorganizacional_id    ");
//        sql.append("            where ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'                    ");
//        String juncao = " and ( ";
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            sql.append(juncao + "   ho.codigo like :ho" + ho.hashCode());
//            juncao = " or ";
//        }
//        sql.append(" ) ) ) ");
//        sql.append(" -                                                                                              ");
//        sql.append("    (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                           ");
//        sql.append(" inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                            ");
//        sql.append(" inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id                               ");
//        sql.append("      where fp.ano = :ano                                                                       ");
//        sql.append("       and fp.tipoFolhaDePagamento in ( "+tiposFolha.substring(0, tiposFolha.length()-1) +")");
//        sql.append("        and ff.vinculofp_id = :vinculo_id                                                       ");
//        sql.append("        and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige             ");
//        sql.append("                            inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id     ");
//        sql.append("                            inner join moduloexportacao me on ge.moduloexportacao_id = me.id    ");
//        sql.append("                                 where me.descricao = :descricaoModulo                          ");
//        sql.append("                                   and ige.operacaoformula = :subtracao                         ");
//        sql.append("                                   and ge.nomereduzido = :nomeReduzido) ");
//        sql.append("AND ff.vinculofp_id IN (SELECT vinc.ID FROM LotacaoFuncional lot            ");
//        sql.append("            INNER JOIN vinculofp vinc ON lot.vinculofp_id = vinc.ID       ");
//        sql.append("            INNER JOIN HierarquiaOrganizacional ho ON ho.subordinada_id = lot.unidadeorganizacional_id    ");
//        sql.append("            where ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'                    ");
//        juncao = " and ( ";
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            sql.append(juncao + "   ho.codigo like :ho" + ho.hashCode());
//            juncao = " or ";
//        }
//        sql.append(" )  ");
//        sql.append(" ) ) as resultado from dual ");
//
//        Query q = em.createNativeQuery(sql.toString());
//        q.setParameter("ano", selecionado.getExercicio().getAno());
//        q.setParameter("vinculo_id", c.getId());
////        String tiposFolha = "";
////        for(TipoFolhaDePagamento t : tiposFolhaDePagamento) {
////            tiposFolha += t.name()+",";
////        }
////        q.setParameter("tiposFolha", tiposFolha.substring(0, tiposFolha.length()-1));
//        q.setParameter("descricaoModulo", "RAIS");
//        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
//        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
//        q.setParameter("nomeReduzido", nomeReduzido);
//        for (HierarquiaOrganizacional ho : selecionado.getHos()) {
//            q.setParameter("ho" + ho.hashCode(), ho.getCodigoSemZerosFinais() + "%");
//        }
//
//        try {
//            return (BigDecimal) q.getSingleResult();
//        } catch (NoResultException nre) {
//            return BigDecimal.ZERO;
//        }
//    }

    public FolhaDePagamento getFolhaDePagamento(ContratoFP c, ArquivoRAIS selecionado, String nomeReduzido, List<TipoFolhaDePagamento> tipoFolhaDePagamento) {
        StringBuilder sql = new StringBuilder();
        sql.append("      select fp.* from itemfichafinanceirafp iff                                                ");
        sql.append("  inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                           ");
        sql.append("  inner join folhadepagamento  fp on fp.id = ff.folhadepagamento_id                             ");
        sql.append("       where fp.ano = :ano                                                                      ");
        sql.append("         and fp.tipoFolhaDePagamento in (:tipoFolha)                                            ");
        sql.append("         and ff.vinculofp_id = :vinculo_id                                                      ");
        sql.append("         and iff.eventofp_id in (select ige.eventofp_id from itemgrupoexportacao ige            ");
        sql.append("                           inner join grupoexportacao ge on ige.grupoexportacao_id = ge.id      ");
        sql.append("                           inner join moduloexportacao me on ge.moduloexportacao_id = me.id     ");
        sql.append("                                where me.descricao = :descricaoModulo                           ");
        sql.append("                                  and ge.nomereduzido = :nomeReduzido)                          ");
        sql.append("       order by fp.calculadaEm desc                                                             ");

        Query q = em.createNativeQuery(sql.toString(), FolhaDePagamento.class);
        q.setParameter("ano", selecionado.getExercicio().getAno());
        q.setParameter("vinculo_id", c.getId());
        q.setParameter("tipoFolha", folhasPagamento(tipoFolhaDePagamento));
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido);
        q.setMaxResults(1);
        try {
            return (FolhaDePagamento) q.getSingleResult();
        } catch (NoResultException nre) {
            return new FolhaDePagamento();
        }
    }

    private List<String> folhasPagamento(List<TipoFolhaDePagamento> tipoFolhaDePagamento) {
        List<String> folha = Lists.newArrayList();
        for (TipoFolhaDePagamento folhaDePagamento : tipoFolhaDePagamento) {
            folha.add(folhaDePagamento.name());
        }
        return folha;
    }

    @Override
    public ArquivoRAIS salvarRetornando(ArquivoRAIS arquivoRAIS) {
        try {
            return getEntityManager().merge(arquivoRAIS);
        } catch (Exception ex) {
            logger.error("Problema ao Salvar o Arquivo RAIS", ex);
        }
        return null;
    }

    public void jaExisteArquivoGeradoParaExercicio(ArquivoRAIS ar) throws ExcecaoNegocioGenerica {
        String hql = "select a from ArquivoRAIS a where a.exercicio = :exercicio and a.entidade = :entidade";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", ar.getExercicio());
        q.setParameter("entidade", ar.getEntidade());
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Já existe um arquivo gerado para '" + ar.getEntidade() + "' no exercício de " + ar.getExercicio());
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void gerarArquivoRais(ArquivoRAIS arquivoRAIS, AuxiliarAndamentoRais aar) {
        String textoDoArquivo = "";
//        try {
//            textoDoArquivo = getConteudoArquivo();
//        } catch (ArquivoRaisException e) {
//            addLog("&nbsp;&bull; Ocorreu um erro ao gerar o arquivo. " + e.getMessage(), "<font style='color : red;'>", "</font>");
//            this.auxiliarAndamentoRais.pararProcessamento();
//            textoDoArquivo = "";
//        }

        selecionado.setGeradoEm(new Date());
        selecionado.setConteudo(textoDoArquivo);

        try {
//            addLog("SALVANDO ARQUIVO GERADO...");
            this.selecionado = (ArquivoRAIS) arquivoRaisAcompanhamentoFacade.salvar(this.selecionado);

        } catch (Exception e) {
//            this.auxiliarAndamentoRais.pararProcessamento();
            e.printStackTrace();
//            addLog("&nbsp;&bull; NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e, "<font style='color : darkred;'><i>", "</i></font>");
        }

//        addLog("&nbsp;&bull; ARQUIVO GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
//        this.auxiliarAndamentoRais.setTotal(Integer.parseInt("" + this.auxiliarAndamentoRais.getTotal()));
//        this.auxiliarAndamentoRais.pararProcessamento();
    }

    public List<HierarquiaOrganizacional> recuperarHierarquiasDaEntidade(Entidade e, CategoriaDeclaracaoPrestacaoContas cat, Date inicio, Date fim) {
        String hql = " select distinct ho from DeclaracaoPrestacaoContas dpc" +
            " inner join dpc.entidadesDPContas ents" +
            " inner join ents.itensEntidaDPContas iecs" +
            " inner join iecs.itemEntidadeUnidadeOrganizacional ieuo" +
            " inner join iecs.entidade e" +
            " inner join ieuo.hierarquiaOrganizacional ho" +
            " where ((ents.inicioVigencia  >= :inicio and ents.inicioVigencia <= :fim) " +
            "     or (ents.finalVigencia   >= :inicio and ents.finalVigencia  <= :fim) " +
            "     or (ents.inicioVigencia  <= :inicio and ents.finalVigencia  >= :fim))" +
            "   and dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "   and e = :entidade";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("entidade", e);
        q.setParameter("inicio", inicio, TemporalType.DATE);
        q.setParameter("fim", fim, TemporalType.DATE);
        return q.getResultList();
    }

    public List<Long> getContratosParaGeracaoRais(ArquivoRAIS ar) {
        String hql = "  select c.id from ContratoFP c, DeclaracaoPrestacaoContas dpc"
            + " inner join c.recursosDoVinculoFP rv"
            + " inner join c.fichasFinanceiraFP ff"
            + " inner join rv.recursoFP rfp"
            + " inner join ff.folhaDePagamento fp"
            + " inner join c.modalidadeContratoFP modalidade"
            + " inner join c.vinculoEmpregaticio ve"
            + " inner join dpc.entidadesDPContas ents"
            + " inner join ents.itensEntidaDPContas iecs"
            + " inner join iecs.itemEntidadeUnidadeOrganizacional ieuo"
            + " inner join iecs.entidade e"
            + " inner join ieuo.hierarquiaOrganizacional ho"
            + " where (:inicio between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + "        or :fim between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + "        or (rv.inicioVigencia between :inicio and :fim and rv.finalVigencia between :inicio and :fim))"
            + "     and ((ents.inicioVigencia  >= :inicio and ents.inicioVigencia <= :fim) "
            + "        or (ents.finalVigencia   >= :inicio and ents.finalVigencia  <= :fim) "
            + "        or (ents.inicioVigencia  <= :inicio and ents.finalVigencia  >= :fim))"
            + "     and dpc.categoriaDeclaracaoPrestacaoContas = :cat"
            + "     and e = :entidade"
            + "     and rfp.codigoOrgao = split(ho.codigo,2,'.')"
            + "     and modalidade.codigo not in :modalidadesNaoPermitidas"
            + "     and ve.codigo != :codigoNaoRais  " +
            "       and fp.ano = :ano order by c.dataAdmissao";

//                + "        and fp.tipoFolhaDePagamento in :tiposFolha";

        Query q = em.createQuery(hql);
        q.setParameter("ano", DataUtil.getAno(ar.getPrimeiroDia()));
        q.setParameter("modalidadesNaoPermitidas", Arrays.asList(Long.parseLong("6"), Long.parseLong("7"), Long.parseLong("8")));
        q.setParameter("codigoNaoRais", Long.parseLong("0"));
        q.setParameter("cat", CategoriaDeclaracaoPrestacaoContas.RAIS);
        q.setParameter("entidade", ar.getEntidade());
        q.setParameter("inicio", ar.getPrimeiroDia(), TemporalType.DATE);
        q.setParameter("fim", ar.getUltimoDia(), TemporalType.DATE);
//        q.setParameter("tiposFolha", TipoFolhaDePagamento.getFolhasParaArquivoRAIS());
        return q.getResultList();
    }

    public List<ContratoFP> getContratosParaGeracaoRais(ArquivoRAIS ar, HierarquiaOrganizacional hierarquia, String filtro) {
        String hql = "  select distinct c from ContratoFP c"
            + " inner join c.recursosDoVinculoFP rv"
            + " inner join c.fichasFinanceiraFP ff"
            + " inner join rv.recursoFP rfp"
            + " inner join ff.folhaDePagamento fp"
            + " inner join c.modalidadeContratoFP modalidade"
            + " inner join c.vinculoEmpregaticio ve"
            + " inner join c.matriculaFP matricula"
            + " inner join matricula.pessoa pf"
            + " where (:inicio between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + " or :fim between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + " or (rv.inicioVigencia between :inicio and :fim and rv.finalVigencia between :inicio and :fim))"
            + "        and rfp.codigoOrgao = :unidade"
            + "        and modalidade.codigo not in :modalidadesNaoPermitidas"
            + "        and ve.codigo != :codigoNaoRais"
            + "        and fp.ano = :ano"
            + "        and ((lower(pf.nome) like :filtro)"
            + "        or (lower(matricula.matricula) like :filtro))";

        Query q = em.createQuery(hql);
        q.setParameter("inicio", ar.getPrimeiroDia());
        q.setParameter("fim", ar.getUltimoDia());
        q.setParameter("ano", DataUtil.getAno(ar.getPrimeiroDia()));
        q.setParameter("unidade", Integer.parseInt(hierarquia.getCodigoDo2NivelDeHierarquia()));
        q.setParameter("modalidadesNaoPermitidas", Arrays.asList(Long.parseLong("6"), Long.parseLong("7"), Long.parseLong("8")));
        q.setParameter("codigoNaoRais", Long.parseLong("0"));
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    private List<HierarquiaOrganizacional> buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado() {
        return entidadeFacade.buscarHierarquiasDaEntidade(selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.RAIS, selecionado.getPrimeiroDia(), selecionado.getUltimoDia());
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future getCabecalhoArquivoRais(AuxiliarAndamentoRais aar) throws ArquivoRaisException {
        AsyncResult<AuxiliarAndamentoRais> result = new AsyncResult<>(aar);
        try {
            jaExisteArquivoGeradoParaExercicio(aar.getArquivoRAIS());
        } catch (ExcecaoNegocioGenerica re) {
            aar.getLog().add(Util.dateHourToString(new Date()) + " - REMOVENDO ARQUIVO ANTERIOR...<br/>");
            arquivoRaisAcompanhamentoFacade.deleteRais(aar.getArquivoRAIS());
        }

        aar.getLog().add(Util.dateHourToString(new Date()) + " - PREPARANDO ARQUIVO PARA GERAÇÃO DOS DADOS...<br/>");
        selecionado = aar.getArquivoRAIS();
        pessoaJuridica = pessoaJuridicaFacade.recuperar(selecionado.getEntidade().getPessoaJuridica().getId());

        aar.getLog().add(Util.dateHourToString(new Date()) + " - GERANDO REGISTRO TIPO 0 DO ARQUIVO...<br/>");
        aar.setRegistroRAISTipo0(montaTipo0());
        aar.getRegistroRAISTipo0().validarLayout(aar);
        aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : green;'><i> - &nbsp;&bull; GERADO COM SUCESSO...</i></font><br/>");
        aar.getLog().add(Util.dateHourToString(new Date()) + " - GERANDO REGISTRO TIPO 1 DO ARQUIVO...<br/>");
        aar.setRegistroRAISTipo1(montaTipo1());
        aar.getRegistroRAISTipo1().validarLayout(aar);

        aar.getLog().add(Util.dateHourToString(new Date()) + "<font style='color : green;'><i> - &nbsp;&bull; GERADO COM SUCESSO...</i></font><br/>");
        return new AsyncResult<>(result);
    }


    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AuxiliarAndamentoRais> getConteudoArquivoRais(AuxiliarAndamentoRais aar, List<Long> idsContratos) throws ArquivoRaisException {
        AsyncResult<AuxiliarAndamentoRais> result = new AsyncResult<>(aar);
        selecionado = aar.getArquivoRAIS();
        pessoaJuridica = pessoaJuridicaFacade.recuperar(selecionado.getEntidade().getPessoaJuridica().getId());

        for (Long id : idsContratos) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            ContratoFP contrato = contratoFPFacade.recuperar(id);
            aar.adicionar(montaTipo2(contrato));
            aar = getRodapeArquivoRais(aar);
            addLog(aar, " - <font style='color : green;'><i>GERADO COM SUCESSO</i></font> &nbsp;&bull; REGISTRO DE : " + contrato.toString().toUpperCase(), "<b>", "</b>");
            registroRAISTipo2.validarLayout(aar);
        }
        return result;
    }

    public AuxiliarAndamentoRais getRodapeArquivoRais(AuxiliarAndamentoRais aar) throws ArquivoRaisException {
        addLog(aar, "GERANDO REGISTRO TIPO 9 DO ARQUIVO...");
        aar.setRegistroRAISTipo9(montaTipo9(aar));
        aar.getRegistroRAISTipo9().validarLayout(aar);
        addLog(aar, "&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
        return aar;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> definirConteudoArquivo(AssistenteBarraProgresso assistenteBarraProgresso, ArquivoRAIS arquivoRAIS, AuxiliarAndamentoRais auxiliarAndamentoRais) {
        String conteudo = auxiliarAndamentoRais.getRegistroRAISTipo0().toString();
        conteudo += System.getProperty("line.separator");

        conteudo += auxiliarAndamentoRais.getRegistroRAISTipo1().toString();
        conteudo += System.getProperty("line.separator");
        assistenteBarraProgresso.setDescricaoProcesso("Gerando conteúdo do arquivo....");
        assistenteBarraProgresso.setTotal(auxiliarAndamentoRais.getRegistrosRAISTipo2().size());
        StringBuilder conteudoBuilder = new StringBuilder(conteudo);
        for (RegistroRAISTipo2 registroRAISTipo2 : auxiliarAndamentoRais.getRegistrosRAISTipo2()) {
            conteudoBuilder.append(registroRAISTipo2.toString());
            conteudoBuilder.append(System.getProperty("line.separator"));
            assistenteBarraProgresso.conta();
        }
        conteudo = conteudoBuilder.toString();

        auxiliarAndamentoRais.getRegistroRAISTipo9().setTotalRegistrosTipo2(auxiliarAndamentoRais.getRegistrosRAISTipo2().size() + "");
        conteudo += auxiliarAndamentoRais.getRegistroRAISTipo9().toString();
        arquivoRAIS.setConteudo(conteudo);
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public void criaTipo0() {
        registroRAISTipo0 = new RegistroRAISTipo0();
        registroRAISTipo0.setPrefixo("00");
        registroRAISTipo0.setTipoRegistro("0");
        registroRAISTipo0.setConstante("1");

        String tipoInscricao = "";
        tipoInscricao = pessoaJuridica.getTipoInscricao() != null ? pessoaJuridica.getTipoInscricao().getDescricao() : "";
        switch (tipoInscricao) {
            case "CNPJ":
                registroRAISTipo0.setTipoInscricaoResponsavel("1");
                registroRAISTipo0.setCnpjCEIResponsavel(pessoaJuridica.getCnpj() != null ? pessoaJuridica.getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", "") : "");
                registroRAISTipo0.setInscricaoCNPJCEI(pessoaJuridica.getCnpj() != null ? pessoaJuridica.getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", "") : "");
                break;
            case "CEI":
                registroRAISTipo0.setTipoInscricaoResponsavel("3");
                registroRAISTipo0.setCnpjCEIResponsavel(pessoaJuridica.getCei() != null ? StringUtil.cortarOuCompletarEsquerda(pessoaJuridica.getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0") : "");
                registroRAISTipo0.setInscricaoCNPJCEI(pessoaJuridica.getCei() != null ? StringUtil.cortarOuCompletarEsquerda(pessoaJuridica.getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0") : "");
                break;
        }

        registroRAISTipo0.setRazaoSocialNomeResponsavel(StringUtil.cortaOuCompletaDireita(pessoaJuridica.getNomeReduzido(), 40, " "));

        enderecoCorreio = pessoaJuridica.getEnderecoPrincipal(); //getEnderecoCorreio(pessoaJuridica);
        if (enderecoCorreio != null) {
            if (enderecoCorreio.getLocalidade() != null && enderecoCorreio.getUf() != null) {
                Cidade cidade = cidadeFacade.recuperaCidadePorNomeEEstado(enderecoCorreio.getLocalidade(), enderecoCorreio.getUf());
                if (cidade != null) {
                    registroRAISTipo0.setCodigoMunicipio(StringUtil.cortarOuCompletarEsquerda(cidade.getCodigo() != null ? "" + cidade.getCodigo() : "", 7, "0"));
                    registroRAISTipo0.setNomeMunicipio(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getLocalidade()) : " ", 30, " "));
                    registroRAISTipo0.setUf(enderecoCorreio.getUf() != null ? enderecoCorreio.getUf() : "");
                }
            }

            registroRAISTipo0.setLogradouroResponsavel(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getLogradouro()) : " ", 40, " "));
            int numeroLogradouro = 0;
            if (enderecoCorreio.getNumero() != null) {
                try {
                    numeroLogradouro = Integer.parseInt(enderecoCorreio.getNumero());
                } catch (Exception e) {
                    numeroLogradouro = 0;
                }
            }
            registroRAISTipo0.setNumeroLogradouro(StringUtil.cortarOuCompletarEsquerda(String.valueOf(numeroLogradouro), 6, "0"));
            registroRAISTipo0.setComplemento(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getComplemento()) : " ", 21, " "));
            registroRAISTipo0.setBairro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getBairro() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getBairro()) : " ", 19, " "));
            registroRAISTipo0.setCep(enderecoCorreio.getCep() != null ? enderecoCorreio.getCep().replaceAll("-", "") : "");
        }

        telefone = getTelefone(pessoaJuridica);
        if (telefone != null) {
            registroRAISTipo0.setDdd(telefone.getDDD());
            registroRAISTipo0.setTelefone(StringUtil.cortarOuCompletarEsquerda(telefone.getSomenteTelefone(), 9, "0"));
        }

        registroRAISTipo0.setIndicadorRetificacaoDeclaracao(selecionado.getIndicadorRetificacao().getCodigo());
        if (selecionado.getIndicadorRetificacao().getCodigo().equals("1")) {
            // codigo 1 = retifica os estabelecimentos entregues anteriormente
            registroRAISTipo0.setDataRetificacao(sdfDiaMesAno.format(new Date()));
        } else {
            // codigo 2 = a declaração não é retificação (é primeira entrega)
            registroRAISTipo0.setDataRetificacao(StringUtil.cortaOuCompletaDireita("0", 8, "0"));
        }
        registroRAISTipo0.setDataGeracaoArquivo(sdfDiaMesAno.format(new Date()));
        registroRAISTipo0.setEmailResponsavel(StringUtil.cortaOuCompletaDireita(selecionado.getPessoaFisica().getEmail() != null ? selecionado.getPessoaFisica().getEmail() : " ", 45, " "));
        registroRAISTipo0.setNomeResponsavel(StringUtil.cortaOuCompletaDireita(selecionado.getPessoaFisica().getNome() != null ? StringUtil.removeCaracteresEspeciais(selecionado.getPessoaFisica().getNome()) : " ", 52, ""));
        registroRAISTipo0.setEspacosBranco24(StringUtil.cortaOuCompletaDireita(" ", 24, " "));
        registroRAISTipo0.setCpfResponsavel(selecionado.getPessoaFisica().getCpf().replaceAll("\\.", "").replaceAll("-", ""));
        registroRAISTipo0.setCreaResponsavel(StringUtil.cortarOuCompletarEsquerda(getCreaPessoa(), 12, "0"));
        registroRAISTipo0.setDataNascimentoResponsavel(selecionado.getPessoaFisica().getDataNascimento() != null ? sdfDiaMesAno.format(selecionado.getPessoaFisica().getDataNascimento()) : StringUtil.cortaOuCompletaDireita(" ", 8, " "));
        registroRAISTipo0.setEspacosBranco192(StringUtil.cortaOuCompletaDireita(" ", 192, " "));
    }

    public RegistroRAISTipo0 montaTipo0() throws ArquivoRaisException {
        criaTipo0();
        return registroRAISTipo0;
    }

    public void criaTipo1() {
        registroRAISTipo1 = new RegistroRAISTipo1();
        registroRAISTipo1.setPrefixo("00");
        registroRAISTipo1.setTipoRegistro("1");
        registroRAISTipo1.setRazaoSocial(StringUtil.cortaOuCompletaDireita(selecionado.getEntidade().getNome(), 52, " "));


        enderecoCorreio = pessoaJuridica.getEnderecoPrincipal(); //getEnderecoCorreio(pessoaJuridica);
        if (enderecoCorreio != null) {

            if (enderecoCorreio.getLocalidade() != null && enderecoCorreio.getUf() != null) {
                Cidade cidade = cidadeFacade.recuperaCidadePorNomeEEstado(enderecoCorreio.getLocalidade(), enderecoCorreio.getUf());
                if (cidade != null) {
                    registroRAISTipo1.setCodigoMunicipio(StringUtil.cortarOuCompletarEsquerda(cidade.getCodigo() != null ? "" + cidade.getCodigo() : "", 7, "0"));
                    registroRAISTipo1.setNomeMunicipio(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLocalidade() != null ? enderecoCorreio.getLocalidade() : " ", 30, " "));
                    registroRAISTipo1.setUf(StringUtil.cortarOuCompletarEsquerda(enderecoCorreio.getUf() != null ? enderecoCorreio.getUf() : " ", 2, " "));
                }
            }
            registroRAISTipo1.setLogradouro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getLogradouro() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getLogradouro()) : " ", 40, " "));
            int numeroLogradouro = 0;
            if (enderecoCorreio.getNumero() != null) {
                try {
                    numeroLogradouro = Integer.parseInt(enderecoCorreio.getNumero());
                } catch (Exception e) {
                    numeroLogradouro = 0;
                }
            }
            registroRAISTipo1.setNumeroLogradouro(StringUtil.cortarOuCompletarEsquerda(String.valueOf(numeroLogradouro), 6, "0"));
            registroRAISTipo1.setComplemento(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getComplemento() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getComplemento()) : " ", 21, " "));
            registroRAISTipo1.setBairro(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getBairro() != null ? StringUtil.removeCaracteresEspeciais(enderecoCorreio.getBairro()) : " ", 19, " "));
            registroRAISTipo1.setCep(StringUtil.cortaOuCompletaDireita(enderecoCorreio.getCep() != null ? enderecoCorreio.getCep().replaceAll("-", "") : " ", 8, " "));
        }

        telefone = getTelefone(pessoaJuridica);
        if (telefone != null) {
            registroRAISTipo1.setDdd(StringUtil.cortarOuCompletarEsquerda(telefone.getDDD(), 2, " "));
            registroRAISTipo1.setTelefone(StringUtil.cortarOuCompletarEsquerda(telefone.getSomenteTelefone(), 9, "0"));
        }

        registroRAISTipo1.setEmail(StringUtil.cortaOuCompletaDireita(pessoaJuridica.getEmail() != null ? pessoaJuridica.getEmail() : " ", 45, " "));

        registroRAISTipo1.setCnae(selecionado.getEntidade().getCnae() != null && selecionado.getEntidade().getCnae().getCodigoCnae() != null ? StringUtil.cortarOuCompletarEsquerda(selecionado.getEntidade().getCnae().getCodigoCnae(), 7, "0") : "");
        registroRAISTipo1.setNaturezaJuridica(selecionado.getEntidade().getNaturezaJuridicaEntidade() != null ? StringUtil.cortarOuCompletarEsquerda(String.valueOf(selecionado.getEntidade().getNaturezaJuridicaEntidade().getCodigo()), 4, "0") : "");
        registroRAISTipo1.setNumeroProprietarios("0001");
        registroRAISTipo1.setDataBase("12"); //????


        String tipoInscricao = pessoaJuridica.getTipoInscricao() != null ? pessoaJuridica.getTipoInscricao().getDescricao() : " ";
        switch (tipoInscricao) {
            case "CNPJ":
                registroRAISTipo1.setTipoInscricaoEstabelecimento("1");
                registroRAISTipo1.setInscricaoCNPJCEI(pessoaJuridica.getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""));
                registroRAISTipo1.setMatriculaCEI(StringUtil.cortaOuCompletaDireita("0", 12, "0"));
                break;
            case "CEI":
                registroRAISTipo1.setTipoInscricaoEstabelecimento("3");
                registroRAISTipo1.setInscricaoCNPJCEI(StringUtil.cortarOuCompletarEsquerda(pessoaJuridica.getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0"));
                registroRAISTipo1.setMatriculaCEI(StringUtil.cortarOuCompletarEsquerda(pessoaJuridica.getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 12, "0"));
                break;
            default:
                registroRAISTipo1.setMatriculaCEI(StringUtil.cortaOuCompletaDireita("0", 12, "0"));
                break;
        }

        registroRAISTipo1.setTipoRAIS("0");
        registroRAISTipo1.setZeros("00");

        registroRAISTipo1.setAno(String.valueOf(selecionado.getExercicio().getAno()));

        TipoEmpresa tipoEmpresa = pessoaJuridica.getTipoEmpresa();
        if (tipoEmpresa != null) {
            switch (tipoEmpresa) {
                case MICRO:
                    registroRAISTipo1.setPorteEmpresa("1");
                    break;
                case PEQUENA:
                    registroRAISTipo1.setPorteEmpresa("2");
                    break;
                default:
                    registroRAISTipo1.setPorteEmpresa("3");
                    break;
            }
        } else {
            registroRAISTipo1.setPorteEmpresa("3");
        }

        Simples simples = selecionado.getEntidade().getSimples();

        if (simples == null || simples == Simples.NAO_OPTANTE
            || simples == Simples.NAO_OPTANTE_EMPRESA_COM_LIMINAR
            || simples == Simples.NAO_OPTANTE_PRODUTOR_RURAL) {
            registroRAISTipo1.setIndicadorOptantesSimples("2");
        } else {
            registroRAISTipo1.setIndicadorOptantesSimples("1");
        }

        registroRAISTipo1.setIndicadorParticipacaoPAT("2");
        registroRAISTipo1.setTrabalhadoresRecebemAte5Salarios(StringUtil.cortarOuCompletarEsquerda("0", 6, "0"));
        registroRAISTipo1.setTrabalhadoresRecebemAcima5Salarios(StringUtil.cortarOuCompletarEsquerda("0", 6, "0"));
        registroRAISTipo1.setPercentualServicoProprio(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setPercentualAdministracaoCozinhas(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setPercentualRefeicaoConvenio(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setPercentualRefeicaoTransportada(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setPercentualCestaAlimento(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setPercentualAlimentacaoConvenio(StringUtil.cortarOuCompletarEsquerda("0", 3, "0"));
        registroRAISTipo1.setIndicadorEncerramentoAtividades("2");
        registroRAISTipo1.setDataEncerramentoAtividades(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
        registroRAISTipo1.setContribuicaoAssociativaCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo1.setContribuicaoAssociativaValor(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
        registroRAISTipo1.setContribuicaoSindicalCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo1.setContribuicaoSindicalValor(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
        registroRAISTipo1.setContribuicaoAssistencialCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo1.setContribuicaoAssistencialValor(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
        registroRAISTipo1.setContribuicaoConfederativaCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo1.setContribuicaoConfederativaValor(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
        registroRAISTipo1.setAtividadeAnoBase("1");
        registroRAISTipo1.setIndicadorPagamentoContribuicaoSindical("2");
        registroRAISTipo1.setCnpjEstabelecimentoContribuicaoSindical(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo1.setIndicadorEmpresaFiliadaSindicato("2");
        registroRAISTipo1.setTipoSistemaControlePonto("02"); //????
        registroRAISTipo1.setEspacosBranco85(StringUtil.cortaOuCompletaDireita(" ", 85, " "));
        registroRAISTipo1.setInformacaoUsoExclusivoEmpresa(StringUtil.cortaOuCompletaDireita(" ", 45, " "));
    }

    public RegistroRAISTipo1 montaTipo1() throws ArquivoRaisException {
        criaTipo1();
        return registroRAISTipo1;

    }

    public void criarTipo2(ContratoFP contratoFP) {
        registroRAISTipo2 = new RegistroRAISTipo2(contratoFP.getId());
        String tipoInscricao = pessoaJuridica.getTipoInscricao() != null ? pessoaJuridica.getTipoInscricao().getDescricao() : " ";
        switch (tipoInscricao) {
            case "CNPJ":
                registroRAISTipo2.setInscricaoCNPJCEI(pessoaJuridica.getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""));
                break;
            case "CEI":
                registroRAISTipo2.setInscricaoCNPJCEI(StringUtil.cortarOuCompletarEsquerda(pessoaJuridica.getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0"));
                break;
            default:
                registroRAISTipo2.setInscricaoCNPJCEI("");
                break;
        }

        registroRAISTipo2.setPrefixo("00");
        registroRAISTipo2.setTipoRegistro("2");

        carteiraTrabalho = documentoPessoalFacade.recuperaCarteiraTrabalho(contratoFP.getMatriculaFP().getPessoa());
        if (carteiraTrabalho != null && carteiraTrabalho.getId() != null) {
            registroRAISTipo2.setCodigoPisPasep(StringUtil.cortarOuCompletarEsquerda(carteiraTrabalho.getPisPasep() != null ? carteiraTrabalho.getPisPasep().trim() : "0", 11, "0"));
            registroRAISTipo2.setCtpsNumero(StringUtil.cortarOuCompletarEsquerda(carteiraTrabalho.getNumero() != null ? carteiraTrabalho.getNumero().trim() : "0", 8, "0"));
            registroRAISTipo2.setCtpsSerie(StringUtil.cortarOuCompletarEsquerda(carteiraTrabalho.getSerie() == null ? "0" : carteiraTrabalho.getSerie(), 5, "0"));
        } else {
            registroRAISTipo2.setCodigoPisPasep(StringUtil.cortarOuCompletarEsquerda("0", 11, "0"));
            registroRAISTipo2.setCtpsNumero(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
            registroRAISTipo2.setCtpsSerie(StringUtil.cortarOuCompletarEsquerda("0", 5, "0"));
        }
        registroRAISTipo2.setNomeEmpregado(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciais(contratoFP.getMatriculaFP().getPessoa().getNome()), 52, " "));
        registroRAISTipo2.setDataNascimento(contratoFP.getMatriculaFP().getPessoa().getDataNascimento() != null ? sdfDiaMesAno.format(contratoFP.getMatriculaFP().getPessoa().getDataNascimento()) : "");

        String nacionalidade = "0";
        if (contratoFP.getMatriculaFP().getPessoa().getNacionalidade() != null && contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getCodigoRaiz() != null) {
            nacionalidade = contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getCodigoRaiz().toString();
        }
        registroRAISTipo2.setNacionalidade(StringUtil.cortarOuCompletarEsquerda(nacionalidade, 2, "0"));

        String anoDeChegada = "0";
        if (contratoFP.getMatriculaFP().getPessoa().getAnoChegada() != null && !nacionalidade.equals("10")) {
            anoDeChegada = String.valueOf(contratoFP.getMatriculaFP().getPessoa().getAnoChegada());
        }
        registroRAISTipo2.setAnoChegadaAoPais(StringUtil.cortarOuCompletarEsquerda(anoDeChegada, 4, "0"));

        String grauInstrucao = "0";
        if (contratoFP.getMatriculaFP().getPessoa().getNivelEscolaridade() != null && contratoFP.getMatriculaFP().getPessoa().getNivelEscolaridade().getGrauInstrucaoCAGED() != null) {
            grauInstrucao = contratoFP.getMatriculaFP().getPessoa().getNivelEscolaridade().getGrauInstrucaoCAGED().getCodigo();
        }

        registroRAISTipo2.setGrauInstrucao(StringUtil.cortarOuCompletarEsquerda(grauInstrucao, 2, "0"));
        registroRAISTipo2.setCpf(contratoFP.getMatriculaFP().getPessoa().getCpf().replaceAll("\\.", "").replaceAll("-", ""));

        registroRAISTipo2.setDataAdmissaoTransferencia(contratoFP.getDataAdmissao() != null ? sdfDiaMesAno.format(contratoFP.getDataAdmissao()) : "");
        registroRAISTipo2.setTipoAdmissao(contratoFP.getTipoAdmissaoRAIS() != null ? StringUtil.cortarOuCompletarEsquerda(String.valueOf(contratoFP.getTipoAdmissaoRAIS().getCodigo()), 2, "0") : "");

        String salario = "0";
        BigDecimal salarioContratual = enquadramentoPCSFacade.salarioBaseContratoServidorAno(contratoFP, selecionado.getUltimoDia());
        salario = getValorFormatadoParaArquivo(salarioContratual);

        registroRAISTipo2.setSalarioContratual(StringUtil.cortarOuCompletarEsquerda(salario.replaceAll("\\.", "").replaceAll(",", ""), 9, "0"));
        registroRAISTipo2.setTipoSalarioContratual("1"); //fixo 1 - mensal
        registroRAISTipo2.setHorasSemanais(StringUtil.cortarOuCompletarEsquerda(String.valueOf(contratoFP.getJornadaDeTrabalho() != null ? contratoFP.getJornadaDeTrabalho().getHorasSemanal() : "0"), 2, "0"));
        CBO cbo = buscarCboCargoContratoFP(contratoFP);

        registroRAISTipo2.setCbo(cbo != null ? StringUtil.cortarOuCompletarEsquerda(String.valueOf(cbo.getCodigo()), 6, "0") : "000000");
        registroRAISTipo2.setVinculoEmpregaticio(contratoFP.getVinculoEmpregaticio() != null ? StringUtil.cortarOuCompletarEsquerda(String.valueOf(contratoFP.getVinculoEmpregaticio().getCodigo()), 2, "0") : "0");

        ExoneracaoRescisao exoneracaoRescisao = exoneracaoRescisaoFacade.recuperaExoneracaoRecisaoPorAno(contratoFP, selecionado.getExercicio().getAno());
        registroRAISTipo2.setCodigoDesligamento(StringUtil.cortarOuCompletarEsquerda(String.valueOf(exoneracaoRescisao.getId() == null ? "0" : exoneracaoRescisao.getMotivoExoneracaoRescisao().getMotivoDesligamentoRAIS().getCodigo()), 2, "0"));
        registroRAISTipo2.setDataDesligamento(exoneracaoRescisao.getId() == null ? "0000" : sdfDiaMes.format(exoneracaoRescisao.getDataRescisao())); //ddMM
        BigDecimal valorRemuneracao;
        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.JANEIRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoJaneiro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.FEVEREIRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoFevereiro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.MARCO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoMarco(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.ABRIL, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoAbril(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.MAIO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoMaio(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.JUNHO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoJunho(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.JULHO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoJulho(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.AGOSTO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoAgosto(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.SETEMBRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoSetembro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.OUTUBRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoOutubro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.NOVEMBRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoNovembro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        valorRemuneracao = getValorRemuneracaoMes(contratoFP, Mes.DEZEMBRO, GrupoExportacaoRAIS.REMUNERACAO_MENSAL.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setRemuneracaoDezembro(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        FolhaDePagamento fp = getFolhaDePagamento(contratoFP, selecionado, GrupoExportacaoRAIS.SALARIO_13_PARCELA_1.getNomeReduzido(), Lists.newArrayList(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO, TipoFolhaDePagamento.NORMAL));
        if (fp != null && fp.getId() != null) {
            valorRemuneracao = getValorRemuneracaoMes(contratoFP, fp.getMes(), GrupoExportacaoRAIS.SALARIO_13_PARCELA_1.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO, TipoFolhaDePagamento.NORMAL));
            registroRAISTipo2.setRemuneracao13SalarioAdiantamento(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));
            registroRAISTipo2.setMesPagamento13SalarioAdiantamento(StringUtil.cortarOuCompletarEsquerda(fp.getMes().getNumeroMesString(), 2, "0"));
        } else {
            registroRAISTipo2.setRemuneracao13SalarioAdiantamento(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
            registroRAISTipo2.setMesPagamento13SalarioAdiantamento("00");
        }

        FolhaDePagamento fp2 = getFolhaDePagamento(contratoFP, selecionado, GrupoExportacaoRAIS.SALARIO_13_PARCELA_2.getNomeReduzido(), Lists.newArrayList(TipoFolhaDePagamento.SALARIO_13, TipoFolhaDePagamento.RESCISAO));
        if (fp2 != null && fp2.getId() != null) {
            valorRemuneracao = getValorRemuneracaoMes(contratoFP, fp2.getMes(), GrupoExportacaoRAIS.SALARIO_13_PARCELA_2.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.SALARIO_13, TipoFolhaDePagamento.RESCISAO));
            registroRAISTipo2.setRemuneracao13SalarioFinal(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));
            registroRAISTipo2.setMesPagamento13SalarioFinal(StringUtil.cortarOuCompletarEsquerda(fp2.getMes().getNumeroMesString(), 2, "0"));
        } else {
            registroRAISTipo2.setRemuneracao13SalarioFinal(StringUtil.cortarOuCompletarEsquerda("0", 9, "0"));
            registroRAISTipo2.setMesPagamento13SalarioFinal("00");
        }

        registroRAISTipo2.setRacaCor(contratoFP.getMatriculaFP().getPessoa().getRacaCor() != null ? contratoFP.getMatriculaFP().getPessoa().getRacaCor().getCodigo() : "9");
        registroRAISTipo2.setIndicadorDeficiencia(contratoFP.getMatriculaFP().getPessoa().getDeficienteFisico() != null && contratoFP.getMatriculaFP().getPessoa().getDeficienteFisico() ? "1" : "2");
        registroRAISTipo2.setTipoDeficiencia(contratoFP.getMatriculaFP().getPessoa().getTipoDeficiencia() != null ? buscarCodigoTipoDeficiencia(contratoFP.getMatriculaFP().getPessoa().getTipoDeficiencia()) : "0");
        registroRAISTipo2.setIndicadorAlvara("2");

        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.AVISO_PREVIO.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.NORMAL));
        registroRAISTipo2.setAvisoPrevioIndenizado(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 9, "0"));

        registroRAISTipo2.setSexo(contratoFP.getMatriculaFP().getPessoa().getSexo() != null ? contratoFP.getMatriculaFP().getPessoa().getSexo().getCodigo() : "");

        List<Afastamento> afastamentos = afastamentoFacade.recuperaAfastamentos(contratoFP, selecionado.getExercicio().getAno());

        registroRAISTipo2.setMotivoPrimeiroAfastamento(afastamentos.isEmpty() || afastamentos.get(0) == null ? "00" : StringUtil.cortarOuCompletarEsquerda(String.valueOf(afastamentos.get(0).getTipoAfastamento().getMotivoAfastamentoRais().getCodigo()), 2, "0"));
        registroRAISTipo2.setDataInicioPrimeiroAfastamento(afastamentos.isEmpty() || afastamentos.get(0) == null ? "0000" : sdfDiaMes.format(definirDataInicioAfastamento(afastamentos.get(0).getInicio())));
        registroRAISTipo2.setDataFinalPrimeiroAfastamento(afastamentos.isEmpty() || afastamentos.get(0) == null ? "0000" : sdfDiaMes.format(definirDataFinalAfastamento(afastamentos.get(0).getTermino())));

        registroRAISTipo2.setMotivoSegundoAfastamento(afastamentos.isEmpty() || afastamentos.size() == 1 || afastamentos.get(1) == null ? "00" : StringUtil.cortarOuCompletarEsquerda(String.valueOf(afastamentos.get(1).getTipoAfastamento().getMotivoAfastamentoRais().getCodigo()), 2, "0"));
        registroRAISTipo2.setDataInicioSegundoAfastamento(afastamentos.isEmpty() || afastamentos.size() == 1 || afastamentos.get(1) == null ? "0000" : sdfDiaMes.format(definirDataInicioAfastamento(afastamentos.get(1).getInicio())));
        registroRAISTipo2.setDataFinalSegundoAfastamento(afastamentos.isEmpty() || afastamentos.size() == 1 || afastamentos.get(1) == null ? "0000" : sdfDiaMes.format(definirDataFinalAfastamento(afastamentos.get(1).getTermino())));
        registroRAISTipo2.setMotivoTerceiroAfastamento(afastamentos.isEmpty() || afastamentos.size() <= 2 || afastamentos.get(2) == null ? "00" : StringUtil.cortarOuCompletarEsquerda(String.valueOf(afastamentos.get(2).getTipoAfastamento().getMotivoAfastamentoRais().getCodigo()), 2, "0"));
        registroRAISTipo2.setDataInicioTerceiroAfastamento(afastamentos.isEmpty() || afastamentos.size() <= 2 || afastamentos.get(2) == null ? "0000" : sdfDiaMes.format(definirDataInicioAfastamento(afastamentos.get(2).getInicio())));
        registroRAISTipo2.setDataFinalTerceiroAfastamento(afastamentos.isEmpty() || afastamentos.size() <= 2 || afastamentos.get(2) == null ? "0000" : sdfDiaMes.format(definirDataFinalAfastamento(afastamentos.get(2).getTermino())));
        Integer diasAfastados = 0;
        for (int i = 0; i < afastamentos.size(); i++) {
            Calendar inicio = Calendar.getInstance();
            inicio.setTime(afastamentos.get(i).getInicio());
            Calendar fim = Calendar.getInstance();
            fim.setTime(afastamentos.get(i).getTermino());
            if (inicio.get(Calendar.YEAR) < selecionado.getExercicio().getAno()) {
                inicio.setTime(DataUtil.getPrimeiroDiaAno(selecionado.getExercicio().getAno()));
            }
            if (fim.get(Calendar.YEAR) > selecionado.getExercicio().getAno()) {
                fim.setTime(DataUtil.getUltimoDiaAno(selecionado.getExercicio().getAno()));
            }
            diasAfastados += DataUtil.getDias(inicio.getTime(), fim.getTime());
        }

        registroRAISTipo2.setQuantidadeDiasAfastamento(StringUtil.cortarOuCompletarEsquerda(String.valueOf(diasAfastados), 3, "0"));

        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.FERIAS_INDENIZADAS.getNomeReduzido(), TipoFolhaDePagamento.getTiposFolhaDePagamentoDeRescisao());
        registroRAISTipo2.setValorFeriasIndenizadas(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));
        registroRAISTipo2.setValorBancoDeHoras(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
        registroRAISTipo2.setQuantidadeMesesBancoDeHoras("00");

        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.DISSIDIO_COLETIVO.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.NORMAL));
        registroRAISTipo2.setValorDissidioColetivo(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));
        registroRAISTipo2.setQuantidadeMesesDissidioColetivo("00");

        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.GRATIFICACOES.getNomeReduzido(), TipoFolhaDePagamento.getTiposFolhaDePagamentoDeRescisao());
        registroRAISTipo2.setValorGratificacoes(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));
        registroRAISTipo2.setQuantidadeMesesGratificacoes("00");

        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.MULTA_RESCISORIA.getNomeReduzido(), TipoFolhaDePagamento.getTiposFolhaDePagamentoDeRescisao());
        registroRAISTipo2.setValorMultaRescisaoSemJustaCausa(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));

        registroRAISTipo2.setContribuicaoAssociativa1OcorrenciaCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo2.setContribuicaoAssociativa1OcorrenciaValor(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));
        registroRAISTipo2.setContribuicaoAssociativa2OcorrenciaCNPJ(StringUtil.cortarOuCompletarEsquerda("0", 14, "0"));
        registroRAISTipo2.setContribuicaoAssociativa2OcorrenciaValor(StringUtil.cortarOuCompletarEsquerda("0", 8, "0"));

        registroRAISTipo2.setContribuicaoSindicalCNPJ(StringUtil.cortarOuCompletarEsquerda(getCNPJPorTipoSindicato(
            contratoFP, TipoItemSindicato.CONTRIBUICAO_SINDICAL).replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0"));
        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.CONTRIBUICAO_SINDICAL.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.NORMAL));
        registroRAISTipo2.setContribuicaoSindicalValor(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));

        registroRAISTipo2.setContribuicaoAssistencialCNPJ(StringUtil.cortarOuCompletarEsquerda(getCNPJPorTipoSindicato(
            contratoFP, TipoItemSindicato.CONTRIBUICAO_ASSISTENCIAL).replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0"));
        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.CONTRUBUICAO_ASSISTENCIAL.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.NORMAL));
        registroRAISTipo2.setContribuicaoAssistencialValor(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));

        registroRAISTipo2.setContribuicaoConfederativaCNPJ(StringUtil.cortarOuCompletarEsquerda(getCNPJPorTipoSindicato(
            contratoFP, TipoItemSindicato.CONTRIBUICAO_CONFEDERATIVA).replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0"));
        valorRemuneracao = getValorRemuneracao(contratoFP, GrupoExportacaoRAIS.CONTRIBUICAO_CONFEDERATIVA.getNomeReduzido(), Arrays.asList(TipoFolhaDePagamento.NORMAL));
        registroRAISTipo2.setContribuicaoConfederativaValor(StringUtil.cortarOuCompletarEsquerda(getValorFormatadoParaArquivo(valorRemuneracao), 8, "0"));

        LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigenteNoAno(contratoFP, selecionado.getExercicio().getAno());
        Cidade cidade = new Cidade();
        if (lotacaoFuncional != null && lotacaoFuncional.getId() != null) {
            cidade = cidadeFacade.recuperaCidadeDaLotacao(lotacaoFuncional);
        }
        registroRAISTipo2.setMunicipioTrabalho(StringUtil.cortarOuCompletarEsquerda(String.valueOf(cidade != null && cidade.getCodigo() != null ? cidade.getCodigo() : "0"), 7, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.JANEIRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasJaneiro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.FEVEREIRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasFevereiro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.MARCO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasMarco(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.ABRIL, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasAbril(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.MAIO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasMaio(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.JUNHO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasJunho(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.JULHO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasJulho(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.AGOSTO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasAgosto(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.SETEMBRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasSetembro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));
        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.OUTUBRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasOutubro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.NOVEMBRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasNovembro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        valorRemuneracao = getSomaHoraExtraMes(contratoFP, Mes.DEZEMBRO, selecionado.getExercicio().getAno(), GrupoExportacaoRAIS.HORAS_EXTRAS.getNomeReduzido(), TipoFolhaDePagamento.getFolhasParaArquivoRAISRemuneracaoMes());
        registroRAISTipo2.setHorasExtrasDezembro(StringUtil.cortarOuCompletarEsquerda(valorRemuneracao.toString(), 3, "0"));

        registroRAISTipo2.setIndicadorEmpregadoFiliadoSindicato("2");
        registroRAISTipo2.setIdentificadorAprendizGravida("2");
        registroRAISTipo2.setIdenficadorTrabalhoParcial("2");
        registroRAISTipo2.setIdentificadorTeleTrabalho("2");
        registroRAISTipo2.setIdentificadorTrabalhoIntermitente("2");
        registroRAISTipo2.setMatriculaContrato(StringUtil.cortarOuCompletarEsquerda(contratoFP.getMatriculaFP().getMatricula() + "/" + contratoFP.getNumero(), 30, " "));
        registroRAISTipo2.setCategoriaTrabalhador(contratoFP.getCategoriaTrabalhador() == null ? "000" : StringUtil.cortarOuCompletarEsquerda(contratoFP.getCategoriaTrabalhador().getCodigo().toString(), 3, "0"));
        registroRAISTipo2.setInformacaoUsoExclusivoEmpresa(StringUtil.cortarOuCompletarEsquerda(" ", 8, " "));
    }

    private Date definirDataInicioAfastamento(Date inicio) {
        Calendar c = Calendar.getInstance();
        c.setTime(inicio);
        if (c.get(Calendar.YEAR) < selecionado.getExercicio().getAno()) {
            return DataUtil.getPrimeiroDiaAno(c.get(Calendar.YEAR));
        }
        return inicio;
    }

    private Date definirDataFinalAfastamento(Date inicio) {
        Calendar c = Calendar.getInstance();
        c.setTime(inicio);
        if (c.get(Calendar.YEAR) > selecionado.getExercicio().getAno()) {
            return DataUtil.getUltimoDiaAno(c.get(Calendar.YEAR));
        }
        return inicio;
    }

    private CBO buscarCboCargoContratoFP(ContratoFP contrato) {
        String sql = "select cbo.* from ContratoFPCargo cargo " +
            " inner join cbo on cargo.CBO_ID = cbo.id " +
            " where cargo.FIMVIGENCIA is null" +
            " and cargo.CONTRATOFP_ID = :contrato";
        Query q = em.createNativeQuery(sql, CBO.class);
        q.setParameter("contrato", contrato.getId());
        if (!q.getResultList().isEmpty()) {
            return (CBO) q.getResultList().get(0);
        }
        return null;
    }

    public RegistroRAISTipo2 montaTipo2(ContratoFP contratoFP) {
        criarTipo2(contratoFP);
        return registroRAISTipo2;
    }

    public RegistroRAISTipo9 montaTipo9(AuxiliarAndamentoRais aar) {
        criaTipo9(aar);
        return registroRAISTipo9;
    }

    public void criaTipo9(AuxiliarAndamentoRais aar) {
        registroRAISTipo9 = new RegistroRAISTipo9();
        String cnpjCei = "";
        String tipoInscricao = aar.getArquivoRAIS().getEntidade().getPessoaJuridica().getTipoInscricao() != null ? aar.getArquivoRAIS().getEntidade().getPessoaJuridica().getTipoInscricao().getDescricao() : " ";
        switch (tipoInscricao) {
            case "CNPJ":
                cnpjCei = aar.getArquivoRAIS().getEntidade().getPessoaJuridica().getCnpj().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", "");
                break;
            case "CEI":
                cnpjCei = StringUtil.cortarOuCompletarEsquerda(aar.getArquivoRAIS().getEntidade().getPessoaJuridica().getCei().replaceAll("\\.", "").replaceAll("/", "").replaceAll("-", ""), 14, "0");
                break;
            default:
                cnpjCei = "";
                break;
        }

        registroRAISTipo9.setInscricaoCNPJCEI(cnpjCei);
        registroRAISTipo9.setPrefixo("00");
        registroRAISTipo9.setTipoRegistro("9");
        registroRAISTipo9.setTotalRegistrosTipo1(StringUtil.cortarOuCompletarEsquerda("1", 6, "0"));
        registroRAISTipo9.setEspacosBranco549(StringUtil.cortarOuCompletarEsquerda(" ", 549, " "));
    }


    public EnderecoCorreio getEnderecoCorreio(Pessoa pessoa) {
        enderecoCorreio = null;
        if (!pessoa.getEnderecos().isEmpty()) {
            for (EnderecoCorreio e : pessoa.getEnderecos()) {
                if (e.getPrincipal()) {
                    enderecoCorreio = e;
                    break;
                }
            }
            //se não possuir nenhum endereço marcado como principal pega o primeiro endereco;
            if (enderecoCorreio == null) {
                for (EnderecoCorreio e : pessoa.getEnderecos()) {
                    enderecoCorreio = e;
                    break;
                }
            }
        }
        return enderecoCorreio;
//        List<EnderecoCorreio> enderecos = enderecoFacade.retornaEnderecoCorreioDaPessoa(pessoa);
//        if (enderecos != null && !enderecos.isEmpty()) {
//            return enderecos.get(0);
//        }
//        return null;
    }

    public Telefone getTelefone(Pessoa pessoa) {

        telefone = null;
        if (!pessoa.getTelefones().isEmpty()) {
            for (Telefone t : pessoa.getTelefones()) {
                if (t.getPrincipal()) {
                    telefone = t;
                    break;
                }
            }
            //se não possuir nenhum telefone marcado como principal pega o primeiro telefone;
            if (telefone == null) {
                for (Telefone t : pessoa.getTelefones()) {
                    telefone = t;
                    break;
                }
            }
        }
        return telefone;
//        List<Telefone> telefones = pessoaFacade.telefonePorPessoa(selecionado.getEntidade().getPessoaJuridica());
//        if (!telefones.isEmpty()) {
//            return telefones.get(0);
//        }
//        return new Telefone();
    }

    public String getCreaPessoa() {
        ConselhoClasseContratoFP cccfp = conselhoClasseOrdemFacade.recuperaCreaPessoa(selecionado.getPessoaFisica());
        if (cccfp.getId() == null) {
            return "";
        }
        return cccfp.getNumeroDocumento();
    }

    private BigDecimal getSomaHoraExtraMes(ContratoFP cfp, Mes mes, Integer ano, String nomeReduzido, List<TipoFolhaDePagamento> tiposFolha) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT coalesce(sum(iff.valorreferencia),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND fp.mes = :mes                                                                        "
            + "       AND ff.vinculofp_id = :cfp_id                                                            "
            + folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  AND ge.nomereduzido = :nomeReduzido)                         ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("cfp_id", cfp.getId());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("nomeReduzido", nomeReduzido);
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getValorRemuneracaoMes(ContratoFP contratoFP, Mes mes, String nomeReduzido, List<TipoFolhaDePagamento> tipos) {
        BigDecimal valorEncontrado = getSomaDosEventosMensal(contratoFP, mes, selecionado.getExercicio().getAno(), nomeReduzido, tipos);
        return valorEncontrado;
    }

    private BigDecimal getValorRemuneracao(ContratoFP contratoFP, String nomeReduzido, List<TipoFolhaDePagamento> tipos) {
        BigDecimal valorEncontrado = getSomaDosEventosAnual(contratoFP, selecionado.getExercicio().getAno(), nomeReduzido, tipos);
        return valorEncontrado;
    }

    private String getValorFormatadoParaArquivo(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        String r = nf.format(valor);
        r = StringUtil.retornaApenasNumeros(r);
        return r;
    }

    private String getCNPJPorTipoSindicato(ContratoFP contratoFP, TipoItemSindicato item) {
        PessoaJuridica pj = pessoaJuridicaFacade.recuperaPessoaSindicatoAno(contratoFP, item, selecionado.getExercicio().getAno());
        if (pj == null) {
            return "";
        }

        return pj.getCnpj() != null ? pj.getCnpj() : "";
    }


    public void addLog(AuxiliarAndamentoRais aar, String valor) {
        try {
            String agora = Util.dateHourToString(new Date());
            aar.getLog().add(agora + " - " + aar.getCalculados() + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (aar == null) {
                aar = new AuxiliarAndamentoRais();
            }
            aar.setLog(new ArrayList<String>());
            aar.getLog().add(valor);
        }
    }

    private void addLog(AuxiliarAndamentoRais aar, String valor, String abre, String fecha) {
        addLog(aar, abre + valor + fecha);
    }

    public List<List> particionarEmDez(List objetos) {
        List<List> retorno = new ArrayList<>();
        int parte = objetos.size() / 10;
        retorno.add(objetos.subList(0, parte));
        retorno.add(objetos.subList(parte, parte * 2));
        retorno.add(objetos.subList(parte * 2, parte * 3));
        retorno.add(objetos.subList(parte * 3, parte * 4));
        retorno.add(objetos.subList(parte * 4, parte * 5));
        retorno.add(objetos.subList(parte * 5, parte * 6));
        retorno.add(objetos.subList(parte * 6, parte * 7));
        retorno.add(objetos.subList(parte * 7, parte * 8));
        retorno.add(objetos.subList(parte * 8, parte * 9));
        retorno.add(objetos.subList(parte * 9, objetos.size()));
        return retorno;
    }

    private String buscarCodigoTipoDeficiencia(TipoDeficiencia tipo){
        return TipoDeficiencia.OUTROS.equals(tipo) || TipoDeficiencia.PSICOSSOCIAL.equals(tipo) ? "5" : tipo.getCodigo();
    }
}
