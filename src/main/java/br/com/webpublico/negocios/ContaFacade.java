/*
 * Codigo gerado automaticamente em Tue Apr 19 16:06:05 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;
import org.springframework.dao.DataIntegrityViolationException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ContaFacade extends AbstractFacade<Conta> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade planejamentoPublicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ContaFacade() {
        super(Conta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private String montaPaternMascaraDespesa(Exercicio e) {
        ConfiguracaoPlanejamentoPublico cpp = planejamentoPublicoFacade.retornaUltima();

        String sql = " SELECT c.*, cd.* FROM conta c ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id=ple.planodedespesas_id AND ple.exercicio_id=:exerc ";
        sql += " INNER JOIN contadespesa cd ON cd.id = C.id ";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", e.getId());

        String conf = cpp.getNivelContaDespesaPPA();
        List lista = q.getResultList();
        String cd1 = ((Conta) lista.get(0)).getCodigo();

        String partesConf[] = conf.split("\\.");
        String partesCod[] = cd1.split("\\.");
        int tamanhoConf = partesConf.length;

        StringBuilder regex = new StringBuilder();
        int cont = 0;
        for (String p : partesCod) {
            int tamanhoParte = p.length();
            if (cont < tamanhoConf) {
                if (tamanhoParte == 1) {
                    regex.append("[1-9]");
                } else if ((tamanhoParte > 1)) {
                    regex.append("(");
                    for (int x = 0; x < tamanhoParte; x++) {
                        if (x == 0) {
                            regex.append("[0-9]");
                        } else if (x == 1) {
                            regex.append("[1-9]");
                        } else {
                            regex.append("[0-9]");
                        }
                    }
                    for (int x = 0; x < tamanhoParte; x++) {
                        if (x == 0) {
                            regex.append("|[1-9]");
                        } else {
                            regex.append("[0-9]");
                        }
                    }
                    regex.append(")");
                }
            } else {
                regex.append(p.replaceAll("[1-9]", "0"));
            }
            cont++;
            regex.append(".");
        }
        String toReturn = regex.substring(0, regex.toString().length() - 1);
        return toReturn;

    }

    public List<Conta> listaFiltrandoInicioCodigo(String codigo, PlanoDeContas pla) {
        codigo = codigo.trim() + "%";
        String hql = "select c from Conta c join c.planoDeContas pl where pl.id = :plano " +
            " and (c.codigo like :codigo or lower(c.descricao) like :parteDesc)";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setParameter("parteDesc", codigo.toLowerCase() + "%");
        q.setParameter("plano", pla.getId());

        return q.getResultList();
    }

    public List<Conta> listaFiltrandoDespesaCriteria(String s, Exercicio e) {

        List<Conta> contasSelecionadas = new ArrayList<Conta>();
        String regex = montaPaternMascaraDespesa(e);
        if (!regex.isEmpty()) {
            String sql = " SELECT c.*, cd.* FROM conta c ";
            sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
            sql += " INNER JOIN planodecontasexercicio ple ON pl.id=ple.planodedespesas_id AND ple.exercicio_id=:exerc ";
            sql += " INNER JOIN contadespesa cd ON cd.id = C.id ";
            sql += " WHERE REGEXP_LIKE (C.codigo, :regex)";
            sql += " AND ((replace(C.codigo,'.','') LIKE :parteCod) OR (lower(C.descricao) LIKE :parteDesc))";
            sql += " ORDER BY c.codigo ";
            Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
            q.setParameter("regex", regex);
            q.setParameter("exerc", e.getId());
            q.setParameter("parteCod", "%" + s.toLowerCase().replace(".", "") + "%");
            q.setParameter("parteDesc", "%" + s.toLowerCase() + "%");
            contasSelecionadas = q.getResultList();
        }
        return contasSelecionadas;
    }

    public List<Conta> listaFiltrandoContaDespesa(String s, Exercicio e) {
        String sql = " SELECT c.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido FROM contadespesa cd ";
        sql += " INNER JOIN conta C ON C.id = cd.id ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodedespesas_id AND ple.exercicio_id = :exerc ";
        sql += " WHERE ((replace(C.codigo, '.', '') LIKE :parteCod) OR (lower(C.descricao) LIKE :parteDesc))";
        sql += " order by c.codigo";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", e.getId());
        q.setParameter("parteCod", s.trim().toLowerCase().replace(".", "") + "%");
        q.setParameter("parteDesc", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public ContaDespesa recuperarContaDespesaPorCodigoExercicio(String codigo, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido FROM contadespesa cd ";
        sql += " INNER JOIN conta C ON C.id = cd.id ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodedespesas_id AND ple.exercicio_id = :exerc ";
        sql += " WHERE C.codigo = :codigo";
        sql += " order by c.codigo";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaDespesa) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar uma conta de despesa para o codigo " + codigo + " no exercício de " + exercicio.getAno() + ".");
        }

    }

    public ContaContabil buscarContaContabilPorCodigoAndExercicio(String codigo, Exercicio exercicio) {
        String sql = "  ";
        sql += " SELECT c.*, ";
        sql += "        cc.migracao, ";
        sql += "        cc.naturezaconta, ";
        sql += "        cc.naturezainformacao, ";
        sql += "        cc.naturezasaldo, ";
        sql += "        cc.subsistema ";
        sql += "    FROM contacontabil cc ";
        sql += "    INNER JOIN conta C ON C.id = cc.id ";
        sql += "    INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += "    INNER JOIN planodecontasexercicio ple ON pl.id = ple.planocontabil_id AND ple.exercicio_id = :exerc ";
        sql += "    WHERE C.codigo = :codigo ";
        sql += "    order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaContabil.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaContabil) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public List<ContaContabil> buscarContasContabeisPorCodigoOrDescricaoAndExercicio(String filtro, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT c.*, ")
            .append("        cc.migracao, ")
            .append("        cc.naturezaconta, ")
            .append("        cc.naturezainformacao, ")
            .append("        cc.naturezasaldo, ")
            .append("        cc.subsistema ")
            .append("    FROM contacontabil cc ")
            .append("    INNER JOIN conta C ON C.id = cc.id ")
            .append("    INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ")
            .append("    INNER JOIN planodecontasexercicio ple ON pl.id = ple.planocontabil_id AND ple.exercicio_id = :exerc ")
            .append("    WHERE replace(C.codigo, '.', '') like :filtro or lower(c.descricao) like :filtro ")
            .append("    order by c.codigo ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ContaContabil.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().replace(".", "") + "%");
        try {
            return (List<ContaContabil>) q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public List<ContaContabil> buscarContasContabeis(String parte, Exercicio exercicio, Conta contaPai) {
        String sql = " select c.*, " +
            "   cc.migracao, " +
            "   cc.naturezaconta, " +
            "   cc.naturezainformacao, " +
            "   cc.naturezasaldo, " +
            "   cc.subsistema " +
            " from contacontabil cc " +
            "    inner join conta c on c.id = cc.id " +
            "    inner join planodecontas pl on c.planodecontas_id = pl.id " +
            "    inner join planodecontasexercicio ple on pl.id = ple.planocontabil_id " +
            " where ple.exercicio_id = :exerc " +
            "   and (replace(c.codigo, '.', '') like :parte or lower(c.descricao) like :parte) " +
            "   and c.categoria <> :sintetica " +
            (contaPai != null ? " and replace(c.codigo, '.', '') like :codigoContaPaiSemZeros " : "") +
            " order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), ContaContabil.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("sintetica", CategoriaConta.SINTETICA.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase().replace(".", "") + "%");
        if (contaPai != null) {
            q.setParameter("codigoContaPaiSemZeros", contaPai.getCodigoSemZerosAoFinal().trim().toLowerCase().replace(".", "") + "%");
        }
        return q.getResultList();
    }

    public ContaDespesa buscarContaDespesaPorCodigoExercicio(String codigo, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido FROM contadespesa cd ";
        sql += " INNER JOIN conta C ON C.id = cd.id ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodedespesas_id AND ple.exercicio_id = :exerc ";
        sql += " WHERE C.codigo = :codigo";
        sql += " order by c.codigo";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaDespesa) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ContaExtraorcamentaria buscarContaExtraOrcamentariaPorCodigoExercicio(String codigo, Exercicio exercicio) {
        String sql = " select c.*,  " +
            "       ce.consignacao, " +
            "       ce.contacontabil_id, " +
            "       ce.contaextraorcamentaria_id, " +
            "       ce.tipocontaextra_id, " +
            "       ce.tipocontaextraorcamentaria, " +
            "       ce.tiporetencao_id " +
            "    from contaextraorcamentaria ce  " +
            "    inner join conta c on c.id = ce.id  " +
            "    inner join planodecontas pl on c.planodecontas_id = pl.id  " +
            "    inner join planodecontasexercicio ple on pl.id = ple.planoextraorcamentario_id  " +
            "        and ple.exercicio_id = :idExercicio " +
            "    WHERE C.codigo = :codigo " +
            "    order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaExtraorcamentaria) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public ContaDeDestinacao buscarContaDestinacaoRecursoPorCodigoExercicio(String codigo, Exercicio exercicio) {
        String sql = " select c.*, " +
            "       cd.datacriacao, " +
            "       cd.fontederecursos_id, " +
            "       cd.codigoCO " +
            "    from contadedestinacao cd  " +
            "    inner join conta c on c.id = cd.id  " +
            "    inner join planodecontas pl on c.planodecontas_id = pl.id  " +
            "    inner join planodecontasexercicio ple on pl.id = ple.planodedestinacaoderecursos_id  " +
            "        and ple.exercicio_id = :idExercicio " +
            "    WHERE C.codigo = :codigo  " +
            "    order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaDeDestinacao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ContaReceita buscarContaReceitaPorCodigoAndExercicio(String codigo, Exercicio exercicio) {
        String sql = "  ";
        sql += " SELECT c.*, ";
        sql += "        cr.codigoreduzido, ";
        sql += "        cr.correspondente_id, ";
        sql += "        cr.descricaoreduzida, ";
        sql += "        cr.geracreditoarrecadacao, ";
        sql += "        cr.migracao, ";
        sql += "        cr.tiposcredito ";
        sql += "    FROM contareceita cr ";
        sql += "    INNER JOIN conta C ON C.id = cr.id ";
        sql += "    INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += "    INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodereceitas_id AND ple.exercicio_id = :exerc ";
        sql += "    WHERE C.codigo = :codigo ";
        sql += "    order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaReceita.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaReceita) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ContaReceita buscarContaAuxiliarPorCodigoAndExercicio(String codigo, Exercicio exercicio) {
        String sql = "  ";
        sql += " SELECT c.*, ";
        sql += "        ca.temp, ";
        sql += "        ca.contacontabil_id, ";
        sql += "    FROM contaauxiliar ca ";
        sql += "    INNER JOIN conta c ON c.id = ca.id ";
        sql += "    INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += "    INNER JOIN planodecontasexercicio ple ON pl.id = ple.planoauxiliar_id AND ple.exercicio_id = :exerc ";
        sql += "    WHERE c.codigo = :codigo ";
        sql += "    order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaReceita.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (ContaReceita) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Conta> listaFiltrandoContaDespesaNivelDesdobramento(String parte, Exercicio exercicio) {
        String sql = " SELECT C.*, CD.TIPOCONTADESPESA, CD.CODIGOREDUZIDO, CD.MIGRACAO "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " WHERE SUBSTR(C.CODIGO, 11, 2) <> '00' "
            + " AND C.DTYPE = 'ContaDespesa' "
            + " AND ((replace(C.codigo, '.', '') LIKE :parteCod) OR (lower(C.descricao) LIKE :parteDesc)) ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("parteCod", "%" + parte.toLowerCase().replace(".", "") + "%");
        q.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaDespesaNivelDesdobramentoEhTipoDespesa(String parte, Exercicio exercicio, TipoContaDespesa tipoContaDespesa) {
        String sql = " SELECT C.*, CD.TIPOCONTADESPESA, CD.CODIGOREDUZIDO, CD.MIGRACAO "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " WHERE SUBSTR(C.CODIGO, 11, 2) <> '00' "
            + " AND C.DTYPE = 'ContaDespesa' "
            + " AND CD.TIPOCONTADESPESA = :tipoConta"
            + " AND ((replace(C.codigo, '.', '') LIKE :parteCod) OR (lower(C.descricao) LIKE :parteDesc)) ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("parteCod", "%" + parte.toLowerCase().replace(".", "") + "%");
        q.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaReceitaPorExercicio(String parte, Exercicio exercicio) {
        String sql = "SELECT C.*, " +
            "            cr.codigoreduzido, cr.descricaoreduzida, cr.migracao, cr.correspondente_id, cr.tiposcredito, cr.geracreditoarrecadacao " +
            " FROM CONTA C " +
            " INNER JOIN contareceita cr on cr.id = c.id" +
            " INNER JOIN PLANODECONTAS PC ON PC.ID = C.PLANODECONTAS_ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = PC.ID " +
            " WHERE PCE.EXERCICIO_ID = :exercicio " +
            " AND C.DTYPE = 'ContaReceita'" +
            " AND (LOWER(C.DESCRICAO) LIKE :parteDescricao OR (REPLACE(C.CODIGO, '.', '') LIKE :parteCodigo)) " +
            " ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaReceita.class);
        q.setParameter("parteCodigo", parte.toLowerCase().replace(".", "") + "%");
        q.setParameter("parteDescricao", "%" + parte.toLowerCase() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> recuperarContaReceitaEquivalentePorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, " +
            "                cr.codigoreduzido, cr.descricaoreduzida, cr.migracao, cr.correspondente_id, cr.tiposcredito, cr.geracreditoarrecadacao " +
            "  FROM CONTA contaDestino " +
            " INNER JOIN contareceita cr on cr.id = contaDestino.id" +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.PLANODERECEITAS_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id = :exercicio";
        Query consulta = em.createNativeQuery(sql, ContaReceita.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        return consulta.getResultList();
    }

    public List<Conta> listaFiltrandoReceita(String s, Exercicio e) {
        String hql = " select z from Conta z, PlanoDeContasExercicio y "
            + " where z.planoDeContas = y.planoDeReceitas"
            + " and y.exercicio =:paramExerc "
            + " and "
            + " (lower(z.descricao) like :filtro or"
            + " (replace(z.codigo, '.', '') like :filtro2))"
            + " order by z.codigo";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramExerc", e);
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "") + "%");
        q.setParameter("filtro2", s.toLowerCase().replace(".", "") + "%");
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoReceitaAnalitica(String s, Exercicio e) {
        String hql = " select z from Conta z, PlanoDeContasExercicio y "
            + " where z.planoDeContas = y.planoDeReceitas" +
            "   and z.categoria = :categoria"
            + " and y.exercicio =:paramExerc "
            + " and "
            + " (lower(z.descricao) like :filtro or"
            + " (replace(z.codigo, '.', '') like :filtro))"
            + " order by z.codigo";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramExerc", e);
        q.setParameter("categoria", CategoriaConta.ANALITICA);
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "") + "%");
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaDeReceita(String s) {
        String hql = " select c from Conta c, PlanoDeContasExercicio p "
            + " where c.planoDeContas = p.planoDeReceitas "
            + " and "
            + " (lower(c.descricao) like :filtro "
            + " or "
            + " (replace(c.codigo, '.', '') like :filtro)) "
            + " order by c.codigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoReceitaDivida(String s, Exercicio e) {
        String hql = " select c from Conta c, PlanoDeContasExercicio p, ContaReceita cr "
            + " where c.planoDeContas = p.planoDeReceitas "
            + " and cr.id = c.id "
            + " and p.exercicio = :paramExerc "
            + " and (lower(c.descricao) like :filtro "
            + "       or replace(c.codigo, '.', '') like :filtro"
            + "       or c.codigo like :filtro) "
            + " and cr.tiposCredito in ('DIVIDA_ATIVA_TRIBUTARIA','DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES','DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS') "
            + " order by c.codigo";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramExerc", e);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoReceitaOperacaoCredito(String s, Exercicio e) {
        String hql = " select c from Conta c, PlanoDeContasExercicio p, ContaReceita cr "
            + " where c.planoDeContas = p.planoDeReceitas "
            + " and cr.id = c.id "
            + " and p.exercicio = :paramExerc "
            + " and ((lower(c.descricao) like :filtro or lower(c.codigo) like :filtro)) "
            + " and cr.tiposCredito in ('OPERACAO_DE_CREDITO') "
            + " order by c.codigo";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramExerc", e);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoReceitaPorTipoCredito(String s, TiposCredito tiposCredito, Exercicio e) {
        String sql;
        sql = " SELECT C.*, CR.CODIGOREDUZIDO, CR.DESCRICAOREDUZIDA, CR.MIGRACAO, CR.CORRESPONDENTE_ID, CR.TIPOSCREDITO, CR.geraCreditoArrecadacao "
            + " FROM CONTA C "
            + " INNER JOIN CONTARECEITA CR ON C.ID = CR.ID "
            + " INNER JOIN PLANODECONTAS PC ON C.PLANODECONTAS_ID = PC.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PCE ON PC.ID = PCE.PLANODERECEITAS_ID AND PCE.EXERCICIO_ID = :ex "
            + " WHERE (TRIM (REPLACE (C.CODIGO, '.', '')) LIKE :parteCod "
            + " OR LOWER(C.DESCRICAO) LIKE LOWER( :parteDesc)) ";
        if (tiposCredito != null) {
            sql += "AND CR.TIPOSCREDITO = :tc";
        }
        sql += " ORDER BY C.CODIGO";
        Query q = em.createNativeQuery(sql, ContaReceita.class);
        q.setParameter("ex", e.getId());
        q.setParameter("parteCod", "%" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("parteDesc", "%" + s.toLowerCase().trim() + "%");
        if (tiposCredito != null) {
            q.setParameter("tc", tiposCredito.name());
        }
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaExtraPorTipoDeContaExtra(String parte, Exercicio e, TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        String sql;
        sql = " SELECT C.*, CE.TIPORETENCAO_ID, CE.CONSIGNACAO, CE.TIPOCONTAEXTRAORCAMENTARIA, CE.CONTACONTABIL_ID, CE.CONTAEXTRAORCAMENTARIA_ID "
            + " FROM CONTA C "
            + " INNER JOIN CONTAEXTRAORCAMENTARIA CE ON C.ID = CE.ID "
            + " INNER JOIN PLANODECONTAS PC ON C.PLANODECONTAS_ID = PC.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PCE ON PC.ID = PCE.PLANOEXTRAORCAMENTARIO_ID AND PCE.EXERCICIO_ID = :exercicio "
            + " WHERE (TRIM (REPLACE (C.CODIGO, '.', '')) LIKE :parteCod "
            + " OR LOWER(C.DESCRICAO) LIKE LOWER (:parteDesc)) ";
        if (tipoContaExtraorcamentaria != null) {
            sql += " AND CE.TIPOCONTAEXTRAORCAMENTARIA = :tipo ";
        }
        sql += " ORDER BY C.CODIGO ";
        Query consulta = em.createNativeQuery(sql, ContaExtraorcamentaria.class);
        consulta.setParameter("exercicio", e.getId());
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        if (tipoContaExtraorcamentaria != null) {
            consulta.setParameter("tipo", tipoContaExtraorcamentaria.name());
        }
        return consulta.getResultList();
    }

    public List<Conta> listaFiltrandoContaDestinacaoPorTpoDestinacao(String parte, Exercicio e) {
        String sql = " SELECT C.*, CD.FONTEDERECURSOS_ID, CD.DATACRIACAO, cd.codigoCO "
            + " FROM CONTA C "
            + " INNER JOIN CONTADEDESTINACAO CD ON C.ID = CD.ID "
            + " INNER JOIN PLANODECONTAS PC ON C.PLANODECONTAS_ID = PC.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PLE ON PC.ID = PLE.PLANODEDESTINACAODERECURSOS_ID AND PLE.EXERCICIO_ID = :exercicio "
            + " WHERE (TRIM (REPLACE (C.CODIGO, '.', '')) LIKE :parteCod "
            + " OR LOWER(C.DESCRICAO) LIKE LOWER (:parteDesc)) "
            + "   AND c.permitirDesdobramento = 0 "
            + "   AND CD.FONTEDERECURSOS_ID IS NOT NULL "
            + " ORDER BY C.CODIGO ";
        Query consulta = em.createNativeQuery(sql, ContaDeDestinacao.class);
        consulta.setParameter("exercicio", e.getId());
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    public List<Conta> listaFiltrandoDestinacaoDeRecursos(String s, Exercicio e) {
        String sql = "SELECT con.*, cdd.datacriacao, cdd.fontederecursos_id, cdd.codigoCO "
            + "  FROM conta con "
            + "INNER JOIN contadedestinacao cdd ON cdd.id = con.id "
            + "INNER JOIN fontederecursos fonte on cdd.fontederecursos_id = fonte.id "
            + "INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + "INNER JOIN planodecontasexercicio pdce ON pdce.planodedestinacaoderecursos_id = pdc.id "
            + "WHERE pdce.exercicio_id = :exer "
            + "AND (fonte.codigo LIKE :codigo OR lower(con.descricao) LIKE :filtro) "
            + "AND cdd.fontederecursos_id IS NOT null "
            + "AND con.permitirDesdobramento = 0 " +
            "  ORDER BY CON.CODIGO";
        Query q = getEntityManager().createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("exer", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigo", s.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Conta> recuperarContaDeDestinacaoEquivalentePorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, " +
            "                 cdd.datacriacao, cdd.fontederecursos_id, cdd.codigoCO " +
            "  FROM CONTA contaDestino " +
            " INNER JOIN contadedestinacao cdd ON cdd.id = contaDestino.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.planodedestinacaoderecursos_id " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id = :exercicio" +
            " AND cdd.fontederecursos_id IS NOT null " +
            " AND contaDestino.permitirDesdobramento = 0 " +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaDeDestinacao.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return consulta.getResultList();
    }

    public List<Conta> buscarContasDeDestinacaoDeRecursos(String s, Exercicio e) {
        String sql = "SELECT con.*, cdd.datacriacao, cdd.fontederecursos_id, cdd.codigoCO " +
            "  FROM conta con "
            + "INNER JOIN contadedestinacao cdd ON cdd.id = con.id "
            + "INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + "INNER JOIN planodecontasexercicio pdce ON pdce.planodedestinacaoderecursos_id = pdc.id "
            + "WHERE pdce.exercicio_id = :exer "
            + "AND (lower(replace(con.codigo, '.', '')) LIKE :filtro OR lower(con.descricao) LIKE :filtro) "
            + "AND cdd.fontederecursos_id IS NOT null "
            + "AND con.permitirDesdobramento = 0  " +
            "  ORDER BY CON.CODIGO";
        Query q = getEntityManager().createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("exer", e.getId());
        q.setParameter("filtro", "%" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List listaFiltrandoExtraorcamentario(String s, Exercicio e) {
        String sql = "SELECT con.*, ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria FROM conta con "
            + " INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pdce ON pdc.id = pdce.planoextraorcamentario_id AND pdce.exercicio_id = :ex "
            + " INNER JOIN contaextraorcamentaria ce ON ce.id = con.id "
            + " WHERE (lower(con.descricao) LIKE :filtro OR lower(replace(con.codigo, '.', '')) LIKE :filtro) "
            + " AND CON.ATIVA = 1"
            + " ORDER BY con.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "").trim() + "%");
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ContaExtraorcamentaria>();
        } else {
            return lista;
        }
    }

    public List<Conta> recuperarContaExtraorcamentariaEquivalentePorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, " +
            "                ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria " +
            "  FROM CONTA contaDestino " +
            " INNER JOIN contaextraorcamentaria ce ON ce.id = contaDestino.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.PLANOEXTRAORCAMENTARIO_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id = :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaExtraorcamentaria.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return consulta.getResultList();
    }

    public List listaFiltrandoTodasContaExtraorcamentaria(String s, Exercicio e) {
        String sql = "SELECT con.*, ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria FROM conta con "
            + " INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pdce ON pdc.id = pdce.planoextraorcamentario_id AND pdce.exercicio_id = :ex "
            + " INNER JOIN contaextraorcamentaria ce ON ce.id = con.id "
            + " WHERE (lower(con.descricao) LIKE :filtro OR lower(replace(con.codigo, '.', '')) LIKE :filtro) "
            + " ORDER BY con.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "").trim() + "%");
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ContaExtraorcamentaria>();
        } else {
            return lista;
        }
    }

    public List<ContaExtraorcamentaria> listaFiltrandoTodasContaExtraPorExercicio(String s, Exercicio e) {
        String sql = "SELECT con.*, ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria "
            + "  FROM conta con "
            + " INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pdce ON pdc.id = pdce.planoextraorcamentario_id AND pdce.exercicio_id = :ex "
            + " INNER JOIN contaextraorcamentaria ce ON ce.id = con.id "
            + " WHERE (lower(con.descricao) LIKE :filtro OR lower(replace(con.codigo, '.', '')) LIKE :filtro) "
            + " ORDER BY con.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "").trim() + "%");
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ContaExtraorcamentaria>();
        } else {
            return lista;
        }
    }

    public List<Conta> listaFiltrandoExtraorcamentarioSemConsignacao(String s, Exercicio e) {
        String sql = " SELECT con.* , ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria FROM conta con "
            + " INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pdce ON pdc.id = pdce.planoextraorcamentario_id AND pdce.exercicio_id = :ex "
            + " INNER JOIN contaextraorcamentaria ce ON ce.id = con.id "
            + " WHERE (lower(con.descricao) LIKE :filtro OR lower(replace(con.codigo, '.', '')) LIKE :filtro) "
            + " AND (CE.TIPOCONTAEXTRAORCAMENTARIA <> 'DEPOSITOS_CONSIGNACOES' OR CE.TIPOCONTAEXTRAORCAMENTARIA IS NULL) "
            + " AND CON.ATIVA = 1"
            + " ORDER BY con.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
//        q.setParameter("filtro2", s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<TipoContaDespesa> buscarTiposContasDespesaNosFilhosDaConta(ContaDespesa conta) {
        String sql = " SELECT DISTINCT to_char(CONTA.TIPOCONTADESPESA) AS TIPOCONTADESPESA "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CONTA ON C.ID = CONTA.ID "
            + " WHERE C.CODIGO LIKE :CONTA "
            + " AND C.EXERCICIO_ID = :EXERCICIO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("CONTA", conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("EXERCICIO", sistemaFacade.getExercicioCorrente().getId());
        List<String> resultado = q.getResultList();
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (TipoContaDespesa tipoContaDespesa : TipoContaDespesa.values()) {
                for (String lista : resultado) {
                    if (tipoContaDespesa.name().equals(lista)) {
                        if (!toReturn.contains(tipoContaDespesa)) {
                            toReturn.add(tipoContaDespesa);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public List<Conta> listaFiltrandoDestinacaoRecursos(String parte, Exercicio e) {
        String sql = "SELECT co.*, cd.* FROM contadedestinacao cd "
            + "INNER JOIN conta co ON co.id = cd.id "
            + "INNER JOIN planodecontas pdc ON co.planodecontas_id = pdc.id "
            + "INNER JOIN planodecontasexercicio pe ON pe.planodedestinacaoderecursos_id = pdc.id "
            + "WHERE pe.exercicio_id = :ex "
            + "AND co.permitirDesdobramento = 0 "
            + "AND cd.fonteDeRecursos_id is not null "
            + "AND (lower(replace(co.codigo, '.', '')) LIKE :filtro OR lower(co.descricao) LIKE :filtro) "
            + "AND rownum <= 10 " +
            " order by co.codigo";

        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + parte.toLowerCase().replace(".", "").trim() + "%");
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoDestinacaoRecursosSubConta(String parte, Exercicio e, List<Long> subContas) {
        String sql = " SELECT co.*, cd.* FROM contadedestinacao cd "
            + " INNER JOIN conta co ON co.id = cd.id "
            + " INNER JOIN planodecontas pdc ON co.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pe ON pe.planodedestinacaoderecursos_id = pdc.id "
            + " INNER JOIN FONTEDERECURSOS FR ON CD.FONTEDERECURSOS_ID = FR.ID "
            + " INNER JOIN SUBCONTAFONTEREC SCFR ON SCFR.FONTEDERECURSOS_ID = FR.ID "
            + " INNER JOIN SUBCONTA SC ON SCFR.SUBCONTA_ID = SC.ID "
            + " WHERE pe.exercicio_id = :ex "
            + " AND (lower(co.codigo) LIKE :filtro OR "
            + " lower(co.descricao) LIKE :filtro ) "
            + " AND SC.ID IN (:contas) ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("contas", subContas);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaDespesa(String s, TipoContaDespesa tipoContaDespesa, Exercicio e) {
        String sql = " SELECT c.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido";
        sql += " FROM CONTA C ";
        sql += " INNER JOIN contadespesa cd on c.id = cd.id ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodedespesas_id AND ple.exercicio_id = :exerc ";
        sql += " WHERE (TRIM (REPLACE (C.codigo, '.','')) LIKE :parteCod ";
        sql += " OR LOWER(C.descricao) LIKE LOWER (:parteDesc)) ";

        if (tipoContaDespesa != null) {
            sql += " and cd.tipoContaDespesa = :tipo ";
        }
        sql += " order by c.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", e.getId());
        q.setParameter("parteCod", "%" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("parteDesc", "%" + s.toLowerCase().trim() + "%");
        if (tipoContaDespesa != null) {
            q.setParameter("tipo", tipoContaDespesa.name());
        }
        return q.getResultList();
    }

    public List<Conta> listaContasFilhasDespesaORC(String s, Conta c, Exercicio e) {
        String codigo = c.getCodigo().substring(0, 9) + '%';
        String sql = "SELECT "
            + " C.*"
            + " ,CD.*"
            + " FROM CONTA C"
            + " INNER JOIN CONTADESPESA CD ON C.ID = CD.ID"
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID"
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio"
            + " WHERE C.DTYPE = 'ContaDespesa' AND C.CODIGO LIKE :cod"
            + " AND (LOWER(C.DESCRICAO) LIKE :filtro OR C.CODIGO LIKE :filtro) AND ROWNUM <= 10";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("cod", codigo);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("exercicio", e.getId());
        List<Conta> listac = q.getResultList();
        listac.remove(c);
        return listac;
    }

    public List<Conta> buscarContasFilhasDespesaORCPorTipo(String s, Conta c, Exercicio e, TipoContaDespesa tipoContaDespesa, Boolean trazerTodasContas) {
        String sql = " SELECT "
            + " C.* "
            + " ,CD.migracao, cd.tipoContaDespesa, CD.codigoReduzido "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON C.ID = CD.ID "
            + " INNER JOIN CONTA SUP ON sup.ID = C.SUPERIOR_ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " WHERE C.DTYPE = 'ContaDespesa' "
            + (tipoContaDespesa != null ? " AND cd.TIPOCONTADESPESA = :tipo " : "")
            + (c != null ? "   and sup.codigo like :codigoSuperior " : "")
            + "   AND (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','') LIKE :codigoSemPonto or C.CODIGO LIKE :codigoNormal)) "
            + "   and C.ativa = 1 "
            + " ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("codigoNormal", "%" + s.toLowerCase().trim() + "%");
        if (c != null) {
            q.setParameter("codigoSuperior", "%" + c.getCodigoSemZerosAoFinal() + "%");
        }
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("exercicio", e.getId());
        if (tipoContaDespesa != null) {
            q.setParameter("tipo", tipoContaDespesa.name());
        }
        if (!trazerTodasContas) {
            q.setMaxResults(10);
        }
        List<Conta> listac = q.getResultList();
        if (c != null) {
            listac.remove(c);
        }
        return listac;
    }

    public List<Conta> buscarContasDesdobradasNoEmpenho(String s, Conta c, Exercicio e, TipoContaDespesa tipoContaDespesa, Boolean trazerTodasContas, Empenho empenho) {
        String sql = " SELECT "
            + " C.* "
            + " ,CD.migracao, cd.tipoContaDespesa, CD.codigoReduzido "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON C.ID = CD.ID "
            + " INNER JOIN CONTA SUP ON sup.ID = C.SUPERIOR_ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " inner join DesdobramentoEmpenho de on cd.id = de.conta_id "
            + " WHERE C.DTYPE = 'ContaDespesa' "
            + "   AND cd.TIPOCONTADESPESA = :tipo "
            + "   AND de.empenho_id = :empenho "
            + "   and sup.codigo like :codigoSuperior"
            + " AND (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','') LIKE :codigoSemPonto or C.CODIGO LIKE :codigoNormal))"
            + "   ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("codigoNormal", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigoSuperior", "%" + c.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("exercicio", e.getId());
        q.setParameter("tipo", tipoContaDespesa.name());
        q.setParameter("empenho", empenho.getId());
        if (!trazerTodasContas) {
            q.setMaxResults(10);
        }
        List<Conta> retorno = q.getResultList();
        retorno.remove(c);
        return retorno;
    }

    public List<TipoContaDespesa> listaDeTipoDasContasFilhasDespesaORCPorConta(Conta c, Exercicio e) {
        String sql = " SELECT DISTINCT CD.TIPOCONTADESPESA " +
            "        FROM CONTA C " +
            "  INNER JOIN CONTADESPESA CD ON C.ID = CD.ID " +
            "  INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio " +
            "       WHERE C.DTYPE = 'ContaDespesa' " +
            "         AND C.SUPERIOR_ID = :idSuperior ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idSuperior", c.getId());
        q.setParameter("exercicio", e.getId());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List<String> lista = q.getResultList();
            List<TipoContaDespesa> retorno = new ArrayList<TipoContaDespesa>();
            for (String s : lista) {
                retorno.add(TipoContaDespesa.valueOf(s));
            }

            return retorno;
        }
        return new ArrayList<>();
    }

    public ContaDeDestinacao recuperarContaDestinacaoPorFonte(FonteDeRecursos fonte) {
        String sql = "SELECT c.*, cd.fontederecursos_id, cd.datacriacao, cd.codigoCO " +
            "      FROM CONTA C " +
            "INNER JOIN CONTADEDESTINACAO CD ON C.ID = CD.ID " +
            "INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID " +
            "INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = pe.planodedestinacaoderecursos_id AND PE.EXERCICIO_ID = :exercicio " +
            "     WHERE C.DTYPE = 'ContaDeDestinacao' " +
            "       AND c.permitirDesdobramento = 0 " +
            "       AND CD.fontederecursos_id = :fonte";

        try {
            Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
            q.setParameter("fonte", fonte.getId());
            q.setParameter("exercicio", fonte.getExercicio().getId());
            q.setMaxResults(1);
            return (ContaDeDestinacao) q.getSingleResult();
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possivel encontrar nenhuma Conta no Plano de Contas de Destinação para a fonte de recurso " + fonte.toString() + ".");
        }
    }

    public List<Conta> listaContasPorLiquidacao(String s, Liquidacao l, Exercicio e) {
        String sql = " SELECT DISTINCT C.*, CD.* "
            + " FROM LIQUIDACAO LIQ "
            + " INNER JOIN DESDOBRAMENTO DESD ON LIQ.ID = DESD.LIQUIDACAO_ID "
            + " INNER JOIN CONTA C ON C.ID = DESD.CONTA_ID "
            + " INNER JOIN CONTADESPESA CD ON C.ID = CD.ID "
            + " WHERE C.DTYPE = 'ContaDespesa' AND LIQ.ID = :liq AND LIQ.EXERCICIO_ID = :exercicio "
            + " AND (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','')) LIKE :filtro) AND ROWNUM <= 10";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("liq", l.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("exercicio", e.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<Conta>();
        } else {
            return (ArrayList<Conta>) q.getResultList();
        }
    }

    public List<Conta> listaContasContabeis(String s, Exercicio e) {
        String sql = "SELECT co.*, "
            + " cc.migracao, "
            + " cc.naturezaSaldo,"
            + " cc.naturezaConta,"
            + " cc.naturezaInformacao,"
            + " cc.subSistema  FROM conta co "
            + " INNER JOIN contacontabil cc ON cc.id = co.id "
            + " INNER JOIN planodecontas pc ON pc.id = co.planodecontas_id "
            + " INNER JOIN planodecontasexercicio pce ON pce.planocontabil_id = pc.id "
            + " WHERE pce.exercicio_id = :ex "
            + " AND co.dtype=\'ContaContabil\' "
            + " and ((replace(co.codigo,'.','') like replace(:filtro,'.','')||'%') or (lower(co.descricao) like '%'||lower(:filtro)||'%'))" +
            " order by co.codigo asc";

        Query q = getEntityManager().createNativeQuery(sql, ContaContabil.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", s.toLowerCase());
        q.setMaxResults(10);
        return q.getResultList();
    }

    @Override
    public Conta recuperar(Object id) {
        long codigo;
        if (id instanceof Conta) {
            codigo = ((Conta) id).getId();
        } else {
            codigo = (Long) id;
        }
        Conta c = getEntityManager().find(Conta.class, codigo);
//        c.getFilhos().size();
        c.getContasEquivalentes().size();
        return c;
    }

    public Conta recuperarReference(Long id) {
        Conta c = em.getReference(Conta.class, id);
//        c.getFilhos().size();
        c.getContasEquivalentes().size();
        return c;
    }

    public BigDecimal saldoContaExtraorcamentaria(Conta conta) {
        BigDecimal receita = new BigDecimal(BigInteger.ZERO);
        BigDecimal pagamento = new BigDecimal(BigInteger.ZERO);
        String sql = "SELECT sum(valor) FROM receitaextra WHERE contaextraorcamentaria_id =:param ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("param", conta.getId());
        if (q.getSingleResult() != null) {
            receita = (BigDecimal) q.getSingleResult();
        }

        String sql2 = "SELECT sum(valor) FROM pagamentoextra WHERE contaextraorcamentaria_id =:param";
        Query q2 = em.createNativeQuery(sql2);
        q2.setParameter("param", conta.getId());
        if (q2.getSingleResult() != null) {
            pagamento = (BigDecimal) q2.getSingleResult();
        }

        return receita.subtract(pagamento);
    }

    public List<Conta> listaPorPlanoDeContas(PlanoDeContas pdc, String parte) throws Exception {
        try {
            String sqlContas = "select cc from Conta cc "
                + "where "
                + "cc.planoDeContas = :pdc "
                + "and "
                + "(replace(cc.codigo,'.','') LIKE :codigo or lower(cc.descricao) like :parte)";
            Query q = em.createQuery(sqlContas);
            q.setParameter("pdc", pdc);
            q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
            q.setParameter("codigo", "" + parte.trim().replace(".", "") + "%");
            q.setMaxResults(10);
            List resultList = q.getResultList();
            return resultList;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public CategoriaConta recuperaCategoriaDaConta(Conta c) {
        if (c != null) {
            for (Conta conta1 : recuperaContasFilhas(c)) {
                if (conta1.getSuperior() != null) {
                    if (conta1.getSuperior().equals(c)) {
                        return CategoriaConta.SINTETICA;
                    }
                }
            }
        }
        return CategoriaConta.ANALITICA;
    }

    public List<Conta> recuperaContasFilhas(Conta c) {
        try {
            Query consulta = em.createQuery("SELECT c FROM Conta c where c.superior.id = :superior");
            consulta.setParameter("superior", c.getId());
            List resultList = consulta.getResultList();
            if (!resultList.isEmpty()) {
                return resultList;
            }
            return new ArrayList<Conta>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Conta>();
    }

    public List<Conta> recuperaContasDespesaFilhas(Conta c) {
        try {
            if (c.getId() == null) {
                return new ArrayList<Conta>();
            }
            Query consulta = em.createQuery("SELECT c FROM ContaDespesa c WHERE c.superior = :superior");
            consulta.setParameter("superior", c);
            List resultList = consulta.getResultList();
            if (!resultList.isEmpty()) {
                return resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Conta>();
    }

    public List<Conta> buscarContasDespesaFilhas(List<Long> idContas) {
        try {
            Query consulta = em.createQuery("SELECT c FROM ContaDespesa c WHERE c.superior.id in :superior");
            consulta.setParameter("superior", idContas);
            List resultList = consulta.getResultList();
            if (!resultList.isEmpty()) {
                return resultList;
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public Boolean existeEssaContaNaReceitaLoa(ContaReceita conta) {
        Query q = em.createQuery("select r from ReceitaLOA r where r.contaDeReceita = :conta");
        q.setParameter("conta", conta);
        return !q.getResultList().isEmpty();
    }

    public void removeConta(Conta entity) {
        try {
            Object chave = Persistencia.getId(entity);
            Object obj = recuperar(chave);
            if (obj != null) {
                getEntityManager().remove(obj);
            }
        } catch (DataIntegrityViolationException d) {
            throw new ExcecaoNegocioGenerica(d.getMessage() + " Existem relacionamentos");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<Conta> listaFiltrandoDestinacaoRecursosPorProvisaoHOConta(String parte, Exercicio e, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT co.*, cd.* FROM contadedestinacao cd" +
            " INNER JOIN conta co ON co.id = cd.id" +
            " inner join PrevisaoHOContaDestinacao prov on co.id = prov.conta_id" +
            " INNER JOIN planodecontas pdc ON co.planodecontas_id = pdc.id" +
            " INNER JOIN planodecontasexercicio pe ON pe.planodedestinacaoderecursos_id = pdc.id" +
            " WHERE pe.exercicio_id = :idExercicio" +
            "        AND prov.exercicio_id = :idExercicio" +
            "        AND prov.unidadeorganizacional_id = :idUnidade" +
            "        AND (replace(co.codigo,'.','') LIKE :filtroCodigo OR" +
            "               lower(co.descricao) LIKE :filtroDesc )";

        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("idExercicio", e.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("filtroCodigo", "" + parte.trim().replace(".", "") + "%");
        q.setParameter("filtroDesc", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<String> getContasPai(Conta conta) {
        try {
            Query consulta = em.createNativeQuery(" WITH CONTAS (ID,ATIVA,DATAREGISTRO, FUNCAO, PERMITIRDESDOBRAMENTO,RUBRICA,TIPOCONTACONTABIL,PLANODECONTAS_ID,DTYPE,EXERCICIO_ID,CATEGORIA, SUPERIOR_ID, CODIGO, DESCRICAO) AS ( " +
                " SELECT c.ID, c.ATIVA, c.DATAREGISTRO,  c.FUNCAO,  c.PERMITIRDESDOBRAMENTO, c.RUBRICA, c.TIPOCONTACONTABIL, c.PLANODECONTAS_ID, c.DTYPE, c.EXERCICIO_ID, c.CATEGORIA,  c.SUPERIOR_ID,  c.CODIGO,  c.DESCRICAO  " +
                "  from conta c " +
                " where c.id = :idConta " +
                " UNION ALL  " +
                " SELECT sup.ID, sup.ATIVA, sup.DATAREGISTRO,  sup.FUNCAO,  sup.PERMITIRDESDOBRAMENTO, sup.RUBRICA, sup.TIPOCONTACONTABIL, sup.PLANODECONTAS_ID, sup.DTYPE, sup.EXERCICIO_ID, sup.CATEGORIA,  sup.SUPERIOR_ID,  sup.CODIGO,  sup.DESCRICAO " +
                "  from conta sup  " +
                " inner join contas contas on contas.superior_id = sup.id " +
                " ) SELECT CONTA.CODIGO  ||' - '||CONTA.DESCRICAO FROM CONTAS CONTA" +
                " order by conta.codigo");
            consulta.setParameter("idConta", conta.getId());
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean hasFilhos(Conta conta) {
        String sql = "select 1 " +
            "           from conta c " +
            "          where c.superior_id = :idConta ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idConta", conta.getId());
        return !q.getResultList().isEmpty();
    }

    public List<BigDecimal> buscarIdsDeContasComFilhos(Conta conta) {
        String sql = "WITH todasAsContas (codigo, superior, id) AS ( " +
            "       SELECT c.codigo, c.superior_id AS superior, c.id AS id " +
            "         FROM conta c " +
            "        WHERE c.id = :idConta " +
            "    UNION ALL " +
            "       SELECT filho.codigo, filho.superior_id AS superior, filho.id " +
            "         FROM conta filho " +
            "         JOIN todasAsContas sup ON sup.id = filho.superior_id) " +
            "SELECT con.id FROM conta con " +
            "        WHERE con.id in (SELECT todasAsContas.id from todasAsContas) " +
            "        and con.id <> :idConta " +
            "     order by con.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idConta", conta.getId());
        return q.getResultList();
    }

    public List<Conta> buscaCorrespondentesFilhas(Conta conta) {
        Query q = em.createQuery(" select origem.* from Conta origem " +
            " inner join contaequivalente ce on origem.id = ce.contaorigem_id " +
            " inner join conta destino on ce.contadestino_id = destino.id " +
            " where destino.id = :conta ");
        q.setParameter("conta", conta);
        return q.getResultList();
    }

    public List<ContaContabil> recuperarContaContabilEquivalentePorCodigo(String codigo, Exercicio exercicio) {
        String sql = "SELECT C2015.*, cc.migracao,cc.naturezaSaldo, cc.naturezaConta, cc.naturezaInformacao, cc.subSistema" +
            "  FROM CONTA C2015 " +
            " inner join CONTACONTABIL cc on c2015.id = cc.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON C2015.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA C2014 ON EQ.CONTAORIGEM_ID = C2014.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON C2015.PLANODECONTAS_ID = PDE.PLANOCONTABIL_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE C2014.CODIGO like :codigo" +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaContabil.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("codigo", "%" + codigo + "%");
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<ContaContabil>) consulta.getResultList();
    }

    public List<ContaContabil> recuperarContaContabilEquivalentePorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, cc.migracao,cc.naturezaSaldo, cc.naturezaConta, cc.naturezaInformacao, cc.subSistema" +
            "  FROM CONTA contaDestino " +
            " inner join CONTACONTABIL cc on contaDestino.id = cc.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.PLANOCONTABIL_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaContabil.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<ContaContabil>) consulta.getResultList();
    }

    public List<ContaExtraorcamentaria> recuperarContaExtraEquivalentePorCodigo(String codigo, Exercicio exercicio) {
        String sql = " SELECT C2015.*, CE.CONSIGNACAO, CE.CONTACONTABIL_ID, CE.CONTAEXTRAORCAMENTARIA_ID, CE.TIPOCONTAEXTRAORCAMENTARIA, CE.TIPORETENCAO_ID " +
            " FROM CONTA C2015 " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CE ON C2015.ID = CE.ID " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON C2015.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA C2014 ON EQ.CONTAORIGEM_ID = C2014.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON C2015.PLANODECONTAS_ID = PDE.PLANOEXTRAORCAMENTARIO_ID " +
            " INNER JOIN EXERCICIO E ON PDE.EXERCICIO_ID = E.ID " +
            " WHERE C2014.CODIGO LIKE :codigo" +
            " AND E.ID = :exercicio " +
            " AND TO_CHAR(EQ.VIGENCIA,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaExtraorcamentaria.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("codigo", "%" + codigo + "%");
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<ContaExtraorcamentaria>) consulta.getResultList();
    }

    public List<ContaExtraorcamentaria> recuperarContaExtraEquivalentePorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = " SELECT contaDestino.*, CE.CONSIGNACAO, CE.CONTACONTABIL_ID, CE.CONTAEXTRAORCAMENTARIA_ID, CE.TIPOCONTAEXTRAORCAMENTARIA, CE.TIPORETENCAO_ID  " +
            "  FROM CONTA contaDestino " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CE ON contaDestino.ID = CE.ID " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.planoExtraorcamentario_id " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaExtraorcamentaria.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<ContaExtraorcamentaria>) consulta.getResultList();
    }

    public ContaExtraorcamentaria recuperarContaExtraPorExercicio(String codigo, Exercicio exercicio) {
        String sql = " select c.*,  CE.CONSIGNACAO, CE.CONTACONTABIL_ID, CE.CONTAEXTRAORCAMENTARIA_ID, CE.TIPOCONTAEXTRAORCAMENTARIA, CE.TIPORETENCAO_ID " +
            " from conta c " +
            " inner join contaextraorcamentaria ce on c.id = ce.id " +
            " inner join planodecontasexercicio pde on c.planodecontas_id = pde.planoextraorcamentario_id " +
            " inner join exercicio e on pde.exercicio_id = e.id " +
            " where c.codigo like :codigo" +
            " and e.id = :idExercicio " +
            " and c.ativa = 1 ";
        Query consulta = em.createNativeQuery(sql, ContaExtraorcamentaria.class);
        consulta.setParameter("idExercicio", exercicio.getId());
        consulta.setParameter("codigo", "%" + codigo + "%");
        if (!consulta.getResultList().isEmpty()) {
            return (ContaExtraorcamentaria) consulta.getResultList().get(0);
        }
        return null;
    }

    public Conta contaPorCodigoExercicio(String codigo, Exercicio exercicio) {
        String hql = "select c from ContaReceita c " +
            " join c.planoDeContas pc " +
            " where replace(c.codigo,'.','') like :codigo " +
            " and pc.exercicio = :exercicio";

        Query q = em.createQuery(hql);
        q.setParameter("codigo", "%" + codigo.trim().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio);

        if (!q.getResultList().isEmpty()) {
            return (Conta) q.getResultList().get(0);
        }
        return null;
    }

    public Conta buscarContaPorCodigoEExercicio(String codigo, Exercicio exercicio) {
        String sql = "select c from Conta c " +
            " inner join c.exercicio ex " +
            " where replace(c.codigo,'.','') like :codigo " +
            " and ex.ano = :exercicio ";

        Query q = em.createQuery(sql, Conta.class);
        q.setParameter("codigo", "%" + codigo.trim().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getAno());
        if (!q.getResultList().isEmpty()) {
            return (Conta) q.getResultList().get(0);
        }
        return null;
    }

    public Conta buscarContaPorCodigoEExercicioEDtype(String codigo, Exercicio exercicio, String dtype) {
        String sql = "select c from Conta c " +
            " inner join c.exercicio ex " +
            " where replace(c.codigo,'.','') like :codigo " +
            " and ex.ano = :exercicio " +
            " and c.dType = :dtype ";

        Query q = em.createQuery(sql, Conta.class);
        q.setParameter("codigo", codigo.trim().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("dtype", dtype);
        if (!q.getResultList().isEmpty()) {
            return (Conta) q.getResultList().get(0);
        }
        return null;
    }

    public List<Conta> buscarContasEquivalentesPorIdOrigemAndExercicioAndDType(Conta contaOrigem, Exercicio exercicio) {
        if (contaOrigem instanceof ContaContabil) {
            return recuperarContasContabeisEquivalentesPorId(contaOrigem, exercicio);
        } else if (contaOrigem instanceof ContaReceita) {
            return recuperarContaReceitaEquivalentePorId(contaOrigem, exercicio);
        } else if (contaOrigem instanceof ContaDespesa) {
            return recuperarContasDespesaEquivalentesPorId(contaOrigem, exercicio);
        } else if (contaOrigem instanceof ContaDeDestinacao) {
            return recuperarContasDestinacaoEquivalentesPorId(contaOrigem, exercicio);
        }
        return null;
    }

    public List<Conta> recuperarContasContabeisEquivalentesPorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, cc.migracao,cc.naturezaSaldo, cc.naturezaConta, cc.naturezaInformacao, cc.subSistema" +
            "  FROM CONTA contaDestino " +
            " inner join CONTACONTABIL cc on contaDestino.id = cc.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.PLANOCONTABIL_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaContabil.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<Conta>) consulta.getResultList();
    }

    public List<Conta> recuperarContasDespesaEquivalentesPorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, CD.migracao, cd.tipoContaDespesa, CD.codigoReduzido " +
            "  FROM CONTA contaDestino " +
            " inner join CONTADESPESA cd on contaDestino.id = cd.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.planoDeDespesas_ID " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaDespesa.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<Conta>) consulta.getResultList();
    }

    public List<Conta> recuperarContasDestinacaoEquivalentesPorId(Conta contaOrigem, Exercicio exercicio) {
        String sql = "SELECT contaDestino.*, cd.fontederecursos_id, cd.datacriacao, cd.codigoCO " +
            "  FROM CONTA contaDestino " +
            " inner join ContaDeDestinacao cd on contaDestino.id = cd.id " +
            " INNER JOIN CONTAEQUIVALENTE EQ ON contaDestino.ID = EQ.CONTADESTINO_ID " +
            " INNER JOIN CONTA contaOrigem ON EQ.CONTAORIGEM_ID = contaOrigem.ID " +
            " INNER JOIN PLANODECONTASEXERCICIO PDE ON contaDestino.PLANODECONTAS_ID = PDE.planodedestinacaoderecursos_id " +
            " inner join exercicio e on PDE.EXERCICIO_ID = e.id " +
            " WHERE contaOrigem.id = :contaOrigem" +
            " AND cd.fontederecursos_id IS NOT null " +
            " AND E.id= :exercicio" +
            " and to_char(eq.vigencia,'yyyy') = :vigencia";
        Query consulta = em.createNativeQuery(sql, ContaDeDestinacao.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("contaOrigem", contaOrigem.getId());
        consulta.setParameter("vigencia", exercicio.getAno());
        return (List<Conta>) consulta.getResultList();
    }

    public List<ContaExtraorcamentaria> listaFiltrandoContaExtraAtivasPorExercicio(String s, Exercicio e) {
        String sql = "SELECT con.*, ce.contacontabil_id, ce.contaextraorcamentaria_id, ce.tiporetencao_id, ce.consignacao, ce.tipoContaExtraorcamentaria "
            + "  FROM conta con "
            + " INNER JOIN planodecontas pdc ON con.planodecontas_id = pdc.id "
            + " INNER JOIN planodecontasexercicio pdce ON pdc.id = pdce.planoextraorcamentario_id AND pdce.exercicio_id = :ex "
            + " INNER JOIN contaextraorcamentaria ce ON ce.id = con.id "
            + " WHERE (lower(con.descricao) LIKE :filtro "
            + "          OR lower(con.codigo) LIKE :filtro"
            + "          OR lower(replace(con.codigo, '.', '')) LIKE :filtro) "
            + " ORDER BY con.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, ContaExtraorcamentaria.class);
        q.setParameter("ex", e.getId());
        q.setParameter("filtro", "%" + s.toLowerCase().replace(".", "").trim() + "%");
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ContaExtraorcamentaria>();
        } else {
            return lista;
        }
    }


    public ContaDespesa buscarContaDesdobramento99(String codigo, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido FROM contadespesa cd ";
        sql += " INNER JOIN conta C ON C.id = cd.id ";
        sql += " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id ";
        sql += " INNER JOIN planodecontasexercicio ple ON pl.id = ple.planodedespesas_id AND ple.exercicio_id = :exerc ";
        sql += " WHERE C.codigo like :codigo";
        sql += " order by c.codigo";
        Query q = getEntityManager().createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("codigo", codigo + ".99%");
        q.setMaxResults(1);
        try {
            return (ContaDespesa) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar uma conta de despesa para o codigo " + codigo + ".99 no exercício de " + exercicio.getAno() + ".");
        }
    }

    public List<Conta> buscarContasDespesaAteNivelPorExercicio(String filtro, Exercicio exercicio, Integer nivel) {
        String sql = " SELECT C.*, CD.TIPOCONTADESPESA, CD.CODIGOREDUZIDO, CD.MIGRACAO "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " WHERE nivelestrutura(C.CODIGO, '.') <= :nivel "
            + " AND C.DTYPE = 'ContaDespesa' "
            + " AND ((replace(C.codigo, '.', '') LIKE :filtro) OR (lower(C.descricao) LIKE :filtro)) "
            + " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("nivel", nivel);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorCodigoOrDescricao(String filtro, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.fonteDeRecursos_id,cd.dataCriacao, cd.codigoCO  "
            + "  FROM  contaDeDestinacao cd "
            + " INNER JOIN conta c ON c.id = cd.id "
            + " INNER JOIN planodecontas pc on pc.id = c.planodecontas_id "
            + " INNER JOIN planodecontasexercicio pce on pc.id = pce.planodedestinacaoderecursos_id "
            + " WHERE pce.exercicio_id = :exercicio "
            + "   AND c.permitirDesdobramento = 0 "
            + "   AND cd.FONTEDERECURSOS_ID IS NOT NULL "
            + "   AND ((replace(C.codigo, '.', '') LIKE :filtro) OR (lower(C.descricao) LIKE :filtro)) "
            + " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public List<Conta> buscarContasDespesaAteNivel(List<Long> idContas, Integer nivel) {
        try {
            String sql = " SELECT C.*, CD.TIPOCONTADESPESA, CD.CODIGOREDUZIDO, CD.MIGRACAO "
                + " FROM CONTA C " +
                "   inner join conta superior on c.superior_id = superior.id"
                + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
                + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
                + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID "
                + " WHERE nivelestrutura(C.CODIGO, '.') = :nivel "
                + " AND C.DTYPE = 'ContaDespesa' "
                + " AND superior.SUPERIOR_ID in :superior "
                + " order by c.codigo ";
            Query consulta = em.createNativeQuery(sql, ContaDespesa.class);
            consulta.setParameter("superior", idContas);
            consulta.setParameter("nivel", nivel);
            List resultList = consulta.getResultList();
            if (!resultList.isEmpty()) {
                return resultList;
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(String filtro, Exercicio exercicio, Long idSubConta) {
        String sql = " SELECT c.*, cd.fonteDeRecursos_id,cd.dataCriacao, cd.codigoCO "
            + "  FROM  contaDeDestinacao cd "
            + " INNER JOIN conta c ON c.id = cd.id "
            + " INNER JOIN planodecontas pc on pc.id = c.planodecontas_id "
            + " INNER JOIN planodecontasexercicio pce on pc.id = pce.planodedestinacaoderecursos_id "
            + " inner join SUBCONTAFONTEREC subContaFont on subContaFont.CONTADEDESTINACAO_ID = cd.id "
            + " WHERE pce.exercicio_id = :exercicio "
            + "   AND subContaFont.SUBCONTA_ID = :idSubConta "
            + "   AND c.permitirDesdobramento = 0 "
            + "   AND cd.FONTEDERECURSOS_ID IS NOT NULL "
            + "   AND ((replace(C.codigo, '.', '') LIKE :filtro) OR (lower(C.descricao) LIKE :filtro)) "
            + " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("idSubConta", idSubConta);
        try {
            return q.getResultList();
        } catch (NoResultException nr) {
            return Lists.newArrayList();
        }
    }

    public ContaDeDestinacao recuperarContaDeDestinacaoPorCodigoEExercicio(String codigo, Long idExercicio) {
        String sql = " SELECT c.*, cd.fonteDeRecursos_id,cd.dataCriacao, cd.codigoCO " +
            "  FROM  contaDeDestinacao cd " +
            " INNER JOIN conta c ON c.id = cd.id " +
            " INNER JOIN planodecontas pc on pc.id = c.planodecontas_id " +
            " INNER JOIN planodecontasexercicio pce on pc.id = pce.planodedestinacaoderecursos_id " +
            " WHERE pce.EXERCICIO_ID = :idExercicio " +
            " AND c.codigo like :codigo " +
            " AND c.permitirDesdobramento = 0 " +
            " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("codigo", "%" + codigo + "%");
        q.setParameter("idExercicio", idExercicio);
        try {
            return (ContaDeDestinacao) q.getResultList().get(0);
        } catch (NoResultException nr) {
            return null;
        }
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorContaFinanceirAndExercicio(String parte, SubConta contaFinanceira, Exercicio ex) {
        String sql = "SELECT c.*, cd.fonteDeRecursos_id, cd.dataCriacao, cd.codigoCO FROM subconta subconta "
            + " INNER JOIN subcontafonterec subcontafonte ON subconta.id = subcontafonte.subconta_id "
            + " INNER JOIN contaDeDestinacao cd ON cd.id = subcontafonte.contaDeDestinacao_id  "
            + " INNER JOIN conta c ON cd.id = c.id  AND c.exercicio_id = :ex "
            + " WHERE subconta.id = :contaFinanceira_id "
            + " AND ((UPPER(c.CODIGO) LIKE :param) OR (c.DESCRICAO LIKE :param)) ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("contaFinanceira_id", contaFinanceira.getId());
        q.setParameter("ex", ex.getId());
        q.setParameter("param", "%" + parte.toUpperCase().trim() + "%");
        List<ContaDeDestinacao> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public Conta buscarContaPorCodigoSiconfi(String codigoSiconfi, Exercicio exercicio) {
        String sql = " select c from Conta c " +
            "where c.codigoSICONFI like :codigoSiconfi " +
            "and c.exercicio = :exercicioId " +
            "order by c.codigo desc ";
        Query q = em.createQuery(sql, Conta.class);
        q.setParameter("codigoSiconfi", codigoSiconfi);
        q.setParameter("exercicioId", exercicio);
        q.setMaxResults(1);
        try {
            return (Conta) q.getSingleResult();
        } catch (NoResultException nre) {
            sql = " select c from Conta c " +
                "where replace(c.codigo, '.', '') like :codigoSiconfi " +
                "and c.exercicio = :exercicioId " +
                "order by c.codigo desc ";
            q = em.createQuery(sql, Conta.class);
            q.setParameter("codigoSiconfi", codigoSiconfi + "%");
            q.setParameter("exercicioId", exercicio);
            q.setMaxResults(1);
            return (Conta) q.getSingleResult();
        }
    }

    public List<ContaAuxiliarDetalhada> buscarContasAuxiliaresDetalhadasPorCodigoOrDescricao(String filtro, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional, ContaContabil contaContabil) {
        String sql = " select c.*, " +
            "                 ca.contaDeDestinacao_id," +
            "                 ca.conta_id," +
            "                 ca.exercicioAtual_id,  " +
            "                 ca.exercicioOrigem_id,  " +
            "                 ca.unidadeOrganizacional_id,  " +
            "                 ca.subSistema,  " +
            "                 ca.dividaConsolidada,  " +
            "                 ca.es,  " +
            "                 ca.classificacaoFuncional,  " +
            "                 ca.hashContaAuxiliarDetalhada,  " +
            "                 ca.contaContabil_id,  " +
            "                 ca.tipoContaAuxiliar_id,  " +
            "                 ca.codigoco  " +
            "            from conta c " +
            "           inner join contaauxiliardetalhada ca on ca.id = c.id " +
            "           where c.exercicio_id = :exercicio " +
            "             and ca.unidadeOrganizacional_id = :unidade " +
            "             and ca.contacontabil_id = :contaContabil " +
            "             and (replace(c.codigo, '.') like :filtro or lower(c.descricao) like :filtro) " +
            "             and exists (select 1 from saldocontacontabil where contacontabil_id = c.id) " +
            "           order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaAuxiliarDetalhada.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().replace(".", "") + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("contaContabil", contaContabil.getId());
        return q.getResultList();
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorReceitaLOA(ReceitaLOA receitaLOA) {
        String sql = "SELECT c.*, cd.fonteDeRecursos_id, cd.dataCriacao, cd.codigoCO FROM receitaloa re "
            + " INNER JOIN receitaloafonte fonte ON re.id = fonte.receitaloa_id "
            + " INNER JOIN contadedestinacao cd ON fonte.destinacaoderecursos_id = cd.id "
            + " INNER JOIN conta c on cd.id = c.id  "
            + " WHERE re.id = :receita_id";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("receita_id", receitaLOA.getId());

        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public List<Conta> buscarContasDeDespesaPorAcaoSubacaoAndUnidade(String parte, SubAcaoPPA subAcaoPPA, AcaoPPA acaoPPA, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        String sql = " select c.*, cd.* " +
            "  from conta c " +
            " inner join contadespesa cd on c.id = cd.id " +
            " inner join provisaoppadespesa pd on c.id = pd.contadedespesa_id " +
            " inner join subacaoppa sb on pd.subacaoppa_id = sb.id " +
            " inner join acaoppa ac on ac.id = sb.acaoppa_id  " +
            " where (replace(C.codigo,'.','') LIKE :filtro OR lower(C.descricao) LIKE :filtro)" +
            " and sb.id = :subacao and ac.id = :acaoId " +
            " and pd.unidadeOrganizacional_id = :unidade " +
            " and c.exercicio_id = :exercicio " +
            " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("subacao", subAcaoPPA.getId());
        q.setParameter("acaoId", acaoPPA.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<Conta> buscarContaDespesaFilhasDespesaOrcPorTipoAndExercicio(String parte, Conta conta, Exercicio exercicio, TipoContaDespesa tipoContaDespesa, Integer maxResult) {
        String codigo = conta.getCodigo().substring(0, 9) + "." + parte + '%';
        String sql = " SELECT "
            + " C.* "
            + " ,CD.migracao, cd.tipoContaDespesa, CD.codigoReduzido "
            + " FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON C.ID = CD.ID "
            + " INNER JOIN CONTA SUP ON sup.ID = C.SUPERIOR_ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID AND PE.EXERCICIO_ID = :exercicio "
            + " WHERE C.DTYPE = 'ContaDespesa' " +
            (tipoContaDespesa == null ? "" : "   AND cd.TIPOCONTADESPESA = :tipo ") +
            "   and sup.codigo like :codigoSuperior"
            + " AND (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','') LIKE :codigoSemPonto or C.CODIGO LIKE :codigoNormal))" +
            " and c.categoria = :categoria" +
            "   ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("codigoNormal", codigo);
        q.setParameter("codigoSuperior", "%" + conta.getCodigoSemZerosAoFinal() + "%");
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + parte.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("categoria", CategoriaConta.ANALITICA.name());
        if (tipoContaDespesa != null)
            q.setParameter("tipo", tipoContaDespesa.name());
        if (maxResult != null) {
            q.setMaxResults(maxResult);
        }
        try {
            List<Conta> resultList = q.getResultList();
            if (CategoriaConta.ANALITICA.equals(conta.getCategoria())) {
                resultList.add(conta);
            }
            Collections.sort(resultList);
            return resultList;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<Conta> buscarContasExtraorcamentario(String filtro, Exercicio exercicio, CategoriaConta categoriaConta) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ")
            .append("  con.*, ")
            .append("  ce.contacontabil_id, ")
            .append("  ce.contaextraorcamentaria_id, ")
            .append("  ce.tiporetencao_id, ")
            .append("  ce.consignacao, ")
            .append("  ce.tipocontaextraorcamentaria ")
            .append("from conta con ")
            .append("  inner join planodecontas pdc on con.planodecontas_id = pdc.id ")
            .append("  inner join planodecontasexercicio pdce on pdc.id = pdce.planoextraorcamentario_id and pdce.exercicio_id = :ex ")
            .append("  inner join contaextraorcamentaria ce on ce.id = con.id ")
            .append("where (lower(con.descricao) like :filtro or lower(replace(con.codigo, '.', '')) like :filtro) ")
            .append("      and con.ativa = :bollean ").append((categoriaConta == null ? "" : " and con.categoria = :categoria "))
            .append("order by con.codigo ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), ContaExtraorcamentaria.class);
        q.setParameter("ex", exercicio.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase().replace(".", "").trim() + "%");
        q.setParameter("bollean", Boolean.TRUE);
        if (categoriaConta != null)
            q.setParameter("categoria", categoriaConta.name());
        q.setMaxResults(10);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return lista;
        }
    }

    public List<ContaDeDestinacao> buscarContaDaProvissaoPPAFonte(Exercicio exercicio, String parte) {
        String sql = "  select distinct co.*, cd.* " +
            " from CONTA co " +
            " INNER JOIN contadedestinacao cd on cd.id = co.id " +
            " INNER join PROVISAOPPAFONTE ppf on co.ID = ppf.DESTINACAODERECURSOS_ID " +
            " where co.exercicio_id = :exercicio " +
            " AND ((upper(co.CODIGO) LIKE :parte) or (upper(co.DESCRICAO) LIKE :parte)) ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("exercicio", exercicio.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        }
        return resultList;
    }

    public List<Conta> buscarPorPlanoDeContas(PlanoDeContas pdc, String parte) {
        String sql = "select cc " +
            " from Conta cc " +
            " where cc.planoDeContas = :pdc " +
            " and (replace(cc.codigo,'.','') LIKE :codigo " +
            " or lower(cc.descricao) like :parte)";
        Query q = em.createQuery(sql);
        q.setParameter("pdc", pdc);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("codigo", "" + parte.trim().replace(".", "") + "%");
        q.setMaxResults(10);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<Conta> buscarContasDeDespesaPorAcaoSubacaoAndUnidade(String parte, SubAcaoPPA subAcaoPPA, AcaoPPA acaoPPA, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio, String filtroContasPai) {
        String sql = " select c.*, cd.* " +
            "  from conta c " +
            " inner join contadespesa cd on c.id = cd.id " +
            " inner join provisaoppadespesa pd on c.id = pd.contadedespesa_id " +
            " inner join subacaoppa sb on pd.subacaoppa_id = sb.id " +
            " inner join acaoppa ac on ac.id = sb.acaoppa_id  " +
            " where (replace(C.codigo,'.','') LIKE :filtro OR lower(C.descricao) LIKE :filtro)" +
            " and sb.id = :subacao and ac.id = :acaoId " +
            " and pd.unidadeOrganizacional_id = :unidade " +
            " and c.exercicio_id = :exercicio " +
            filtroContasPai +
            " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("subacao", subAcaoPPA.getId());
        q.setParameter("acaoId", acaoPPA.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<Conta> buscarContasDeDespesaFilhasDe(String filtro, String filtroContasPai, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.* FROM conta c " +
            " INNER JOIN planodecontas pl ON C.planodecontas_id=pl.id " +
            " INNER JOIN planodecontasexercicio ple ON pl.id=ple.planodedespesas_id  " +
            " INNER JOIN contadespesa cd ON cd.id = C.id " +
            " WHERE ((replace(C.codigo,'.','') LIKE :parteCod) OR (lower(C.descricao) LIKE :parteDesc))" +
            " and nivelestrutura(C.CODIGO, '.') = :nivel " +
            filtroContasPai +
            " AND ple.exercicio_id = :exerc " +
            " ORDER BY c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("exerc", exercicio.getId());
        q.setParameter("nivel", 4);
        q.setParameter("parteCod", "%" + filtro.toLowerCase().replace(".", "") + "%");
        q.setParameter("parteDesc", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Conta> buscarContasDeDestinacaoPorContaAcaoSubacaoAndUnidade(String parte, Conta contaDespesa, SubAcaoPPA subAcaoPPA, AcaoPPA acaoPPA, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        String sql = " select c.*, cd.datacriacao, cd.fontederecursos_id, cd.codigoCO " +
            "  from conta c " +
            " inner join contadedestinacao cd on c.id = cd.id " +
            " inner join provisaoppafonte pf on c.id = pf.destinacaoDeRecursos_id  " +
            " inner join provisaoppadespesa pd on pd.id = pf.PROVISAOPPADESPESA_ID " +
            " inner join subacaoppa sb on pd.subacaoppa_id = sb.id " +
            " inner join acaoppa ac on ac.id = sb.acaoppa_id  " +
            " where (replace(C.codigo,'.','') LIKE :filtro OR lower(C.descricao) LIKE :filtro)" +
            " and sb.id = :subacao and ac.id = :acaoId " +
            " and pd.contadedespesa_id = :contaDespesa " +
            " and pd.unidadeOrganizacional_id = :unidade " +
            " and c.exercicio_id = :exercicio " +
            " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("filtro", "%" + parte.replace(".", "").trim().toLowerCase() + "%");
        q.setParameter("contaDespesa", contaDespesa.getId());
        q.setParameter("subacao", subAcaoPPA.getId());
        q.setParameter("acaoId", acaoPPA.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }


    public List<Conta> buscarContasDespesaPorNivelNivel(String filtro, Integer nivel, Exercicio exercicio) {
        String sql = " SELECT C.*, CD.TIPOCONTADESPESA, CD.CODIGOREDUZIDO, CD.MIGRACAO "
            + "  FROM CONTA C "
            + " INNER JOIN CONTADESPESA CD ON CD.ID = C.ID "
            + " INNER JOIN PLANODECONTAS PLANO ON C.PLANODECONTAS_ID = PLANO.ID "
            + " INNER JOIN PLANODECONTASEXERCICIO PE ON PLANO.ID = PE.PLANODEDESPESAS_ID "
            + " WHERE nivelestrutura(C.CODIGO, '.') = :nivel "
            + "   AND PE.exercicio_id = :exercicio "
            + "   AND C.DTYPE = 'ContaDespesa' "
            + "   AND ((replace(C.codigo, '.', '') LIKE :filtro) OR (lower(C.descricao) LIKE :filtro)) "
            + " order by c.codigo ";
        Query consulta = em.createNativeQuery(sql, ContaDespesa.class);
        consulta.setParameter("exercicio", exercicio.getId());
        consulta.setParameter("filtro", "%" + filtro.toLowerCase().replace(".", "") + "%");
        consulta.setParameter("nivel", nivel);
        List resultList = consulta.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
