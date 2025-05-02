/*
 * Codigo gerado automaticamente em Wed May 18 13:42:33 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.SituacaoFonteRecurso;
import br.com.webpublico.enums.TipoFonteRecurso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class FonteDeRecursosFacade extends AbstractFacade<FonteDeRecursos> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private CodigoCOFacade codigoCOFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FonteDeRecursosFacade() {
        super(FonteDeRecursos.class);
    }

    @Override
    public FonteDeRecursos recuperar(Object id) {
        FonteDeRecursos fonteDeRecursos = em.find(FonteDeRecursos.class, id);
        Hibernate.initialize(fonteDeRecursos.getFontesEquivalentes());
        Hibernate.initialize(fonteDeRecursos.getCodigosCOs());
        return fonteDeRecursos;
    }

    public List<ReceitaLOAFonte> listaFonteDeRecursosPorReceitaLOA(String parte, ReceitaLOA receitaLOA) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select FONTE.* from RECEITALOA rec");
        sql.append(" INNER JOIN  RECEITALOAFONTE FONTE on FONTE.RECEITALOA_ID = rec.ID ");
        sql.append(" INNER JOIN CONTA con on con.ID = rec.CONTADERECEITA_ID ");
        sql.append(" where rec.ID =:recParam ");
        sql.append(" AND (UPPER(con.DESCRICAO) LIKE :param) or (con.CODIGO LIKE :param) ");
        parte = "%" + parte.toLowerCase().trim() + "%";

        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOAFonte.class);
        q.setParameter("param", parte);
        long l = receitaLOA.getId();
        q.setParameter("recParam", l);
        return q.getResultList();
    }

    public List<ReceitaLOAFonte> comboRecursosPorReceitaLOA(ReceitaLOA receitaLOA) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select FONTE.* from RECEITALOA rec");
        sql.append(" INNER JOIN  RECEITALOAFONTE FONTE on FONTE.RECEITALOA_ID = rec.ID ");
        sql.append(" where rec.ID =:recParam ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOAFonte.class);
        long l = receitaLOA.getId();
        q.setParameter("recParam", l);
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaFonteDeRecursosPorReceitaLOA(ReceitaLOA receitaLOA) {
        String sql = "SELECT fr.* FROM receitaloa re "
            + " INNER JOIN receitaloafonte fonte ON re.id = fonte.receitaloa_id "
            + " INNER JOIN contadedestinacao conta ON fonte.destinacaoderecursos_id = conta.id "
            + " INNER JOIN fontederecursos fr ON fr.id = conta.fontederecursos_id AND fr.situacaocadastral = 'ATIVO'"
            + " WHERE re.id = :receita_id";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("receita_id", receitaLOA.getId());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaFonteDeRecursosPorContaFinanceira(SubConta contaFinanceira, Exercicio exercicio) {
        String sql = "SELECT fonte.* FROM subconta subconta "
            + " INNER JOIN subcontafonterec subcontafonte ON subconta.id = subcontafonte.subconta_id "
            + " INNER JOIN fontederecursos fonte ON fonte.id = subcontafonte.fontederecursos_id AND fonte.exercicio_id = :exercicio AND fonte.situacaocadastral = 'ATIVO'"
            + " WHERE subconta.id = :contaFinanceira_id";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("contaFinanceira_id", contaFinanceira.getId());
        q.setParameter("exercicio", exercicio.getId());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacaoPorContaFinanceira(SubConta contaFinanceira, Exercicio exercicio) {
        String sql = " SELECT c.*, cd.fonteDeRecursos_id,cd.dataCriacao, cd.codigoco "
            + "  FROM subconta subconta "
            + " INNER JOIN subcontafonterec subcontafonte ON subconta.id = subcontafonte.subconta_id "
            + " INNER JOIN contaDeDestinacao cd ON cd.fonteDeRecursos_id = subcontafonte.fontederecursos_id "
            + " INNER JOIN conta c ON c.id = cd.id "
            + " INNER JOIN planodecontas pc on pc.id = c.planodecontas_id "
            + " INNER JOIN planodecontasexercicio pce on pc.id = pce.planodedestinacaoderecursos_id and pce.exercicio_id = :exercicio "
            + " WHERE subconta.id = :contaFinanceira_id "
            + "   AND c.permitirDesdobramento = 0 "
            + " order by c.codigo ";
        Query q = em.createNativeQuery(sql, ContaDeDestinacao.class);
        q.setParameter("contaFinanceira_id", contaFinanceira.getId());
        q.setParameter("exercicio", exercicio.getId());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<ContaDeDestinacao>();
        }
        return q.getResultList();
    }

    private Exercicio getExercicioCorrente() {
        SistemaControlador s = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return s.getExercicioCorrente();
    }

    public List<FonteDeRecursos> listaFiltrandoPorContaFinanceira(String parte, SubConta contaFinanceira) {
        String sql = "SELECT fonte.* FROM subconta subconta "
            + " INNER JOIN subcontafonterec subcontafonte ON subconta.id = subcontafonte.subconta_id "
            + " INNER JOIN fontederecursos fonte ON fonte.id = subcontafonte.fontederecursos_id "
            + "                                  AND fonte.situacaocadastral = 'ATIVO'"
            + "                                  AND fonte.exercicio_id = :exerc_id"
            + " WHERE subconta.id = :contaFinanceira_id "
            + " AND ((UPPER(FONTE.CODIGO) LIKE :param) OR (FONTE.DESCRICAO LIKE :param)) ";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("contaFinanceira_id", contaFinanceira.getId());
        q.setParameter("exerc_id", getExercicioCorrente().getId());
        q.setParameter("param", "%" + parte.toUpperCase().trim() + "%");
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaFiltrandoPorContaFinanceiraExercicio(String parte, SubConta contaFinanceira, Exercicio ex) {
        String sql = "SELECT fonte.* FROM subconta subconta "
            + " INNER JOIN subcontafonterec subcontafonte ON subconta.id = subcontafonte.subconta_id "
            + " INNER JOIN fontederecursos fonte ON fonte.id = subcontafonte.fontederecursos_id  "
            + " AND fonte.exercicio_id = :ex "
            + " AND fonte.situacaocadastral = 'ATIVO'"
            + " WHERE subconta.id = :contaFinanceira_id "
            + " AND ((UPPER(FONTE.CODIGO) LIKE :param) OR (FONTE.DESCRICAO LIKE :param)) ";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("contaFinanceira_id", contaFinanceira.getId());
        q.setParameter("ex", ex.getId());
        q.setParameter("param", "%" + parte.toUpperCase().trim() + "%");
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaFiltrandoPorExercicio(String parte, Exercicio exercicio) {
        String sql = "SELECT FR.* "
            + " FROM FONTEDERECURSOS FR"
            + " WHERE ((lower (FR.CODIGO) LIKE :parte) OR (lower (FR.DESCRICAO) LIKE :parte)) "
            + " AND FR.EXERCICIO_ID = :ex"
            + " AND fr.situacaocadastral = 'ATIVO' ORDER BY codigo";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ex", exercicio.getId());
        q.setMaxResults(10);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> buscarFontesPorExercicioAndCodigoIniciandoEmAndDescricao(String inicioCodigo, String descricao, Exercicio exercicio) {
        String sql = "SELECT FR.* "
            + " FROM FONTEDERECURSOS FR "
            + " WHERE lower(FR.CODIGO) LIKE :inicioCodigo and (lower(FR.CODIGO) LIKE :parte OR lower(FR.DESCRICAO) LIKE :parte) "
            + " AND FR.EXERCICIO_ID = :ex "
            + " AND fr.situacaocadastral = 'ATIVO' ORDER BY codigo ";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("parte", "%" + descricao.toLowerCase().trim() + "%");
        q.setParameter("inicioCodigo", inicioCodigo.toLowerCase().trim() + "%");
        q.setParameter("ex", exercicio.getId());
        q.setMaxResults(10);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaFiltrandoPorExercicioETipo(String parte, Exercicio exercicio, TipoFonteRecurso tipoFonteRecurso) {
        String sql = "SELECT FR.* "
            + " FROM FONTEDERECURSOS FR"
            + " WHERE ((lower (FR.CODIGO) like :parte) or (lower (FR.DESCRICAO) like :parte))"
            + " AND FR.EXERCICIO_ID = :ex"
            + " AND FR.SITUACAOCADASTRAL = 'ATIVO' "
            + " AND FR.TIPOFONTERECURSO = :tipo"
            + " ORDER BY CODIGO";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ex", exercicio.getId());
        q.setParameter("tipo", tipoFonteRecurso.name());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<FonteDeRecursos>();
        }
        return q.getResultList();
    }

    public List<FonteDeRecursos> listaPorExercicio(Exercicio ex) {
        String sql = "SELECT * FROM FONTEDERECURSOS WHERE EXERCICIO_ID = :ex ORDER BY CODIGO";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }

    public FonteDeRecursos recuperaPorCodigoExericio(FonteDeRecursos fonteDeRecursos, Exercicio ex) {
        String sql = "SELECT * FROM FONTEDERECURSOS WHERE CODIGO = :cod AND EXERCICIO_ID = :ex";
        if (fonteDeRecursos.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("cod", fonteDeRecursos.getCodigo().trim());
        q.setParameter("ex", ex.getId());
        if (fonteDeRecursos.getId() != null) {
            q.setParameter("id", fonteDeRecursos.getId());
        }
        q.setMaxResults(1);
        try {
            if (q.getSingleResult() != null) {
                return (FonteDeRecursos) q.getSingleResult();
            }
        } catch (NoResultException nre) {

        }
        return null;
    }

    public FonteDeRecursos recuperaPorCodigoExercicio(FonteDeRecursos fonteDeRecursos, Exercicio ex) {
        String sql = "SELECT * FROM FONTEDERECURSOS WHERE CODIGO = :cod AND EXERCICIO_ID = :ex";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("cod", fonteDeRecursos.getCodigo().trim());
        q.setParameter("ex", ex.getId());
        q.setMaxResults(1);
        try {
            if (q.getSingleResult() != null) {
                return (FonteDeRecursos) q.getSingleResult();
            }
        } catch (NoResultException nre) {

        }
        return null;
    }

    public List<FonteDeRecursos> buscarFontesDeRecursoEquivalentesPorId(FonteDeRecursos fonteOrigem, Exercicio exercicio) {
        String sql = "SELECT frDestino.* " +
            "  FROM FonteDeRecursos frOrigem " +
            " INNER JOIN FonteDeRecursosEquivalente EQ ON frOrigem.ID = EQ.fonteDeRecursosOrigem_id " +
            " INNER JOIN FonteDeRecursos frDestino ON EQ.fonteDeRecursosDestino_id = frDestino.ID " +
            " WHERE frOrigem.id = :contaOrigem" +
            " AND frDestino.exercicio_id = :exercicio";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("contaOrigem", fonteOrigem.getId());
        return q.getResultList();
    }

    public FonteDeRecursos buscarFonteDeRecursoPorCodigoSiconfi(String codigoSiconfi, Exercicio exercicio) {
        String sql = " select fr.* from fontederecursos fr  " +
            "where fr.codigosiconfi = :codigoSiconfi " +
            "and fr.exercicio_id = :exercicioId " +
            "order by fr.CODIGO asc ";

        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("codigoSiconfi", codigoSiconfi);
        q.setParameter("exercicioId", exercicio.getId());
        q.setMaxResults(1);
        return (FonteDeRecursos) q.getSingleResult();
    }

    public FonteDeRecursos salvarRetornando(FonteDeRecursos fonteDeRecursos) {
        return em.merge(fonteDeRecursos);
    }

    @Override
    public void salvarNovo(FonteDeRecursos fonteDeRecursos) {
        validarFonteDeRecursos(fonteDeRecursos);
        super.salvarNovo(fonteDeRecursos);
    }

    @Override
    public void salvar(FonteDeRecursos fonteDeRecursos) {
        validarFonteDeRecursos(fonteDeRecursos);
        super.salvar(fonteDeRecursos);
    }

    private void validarFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        fonteDeRecursos.realizarValidacoes();
        validarCodigoRepetidoNoExercicio(fonteDeRecursos);
    }

    private void validarCodigoRepetidoNoExercicio(FonteDeRecursos fonteDeRecursos) {
        ValidacaoException ve = new ValidacaoException();
        if (fonteDeRecursos.getCodigo() != null) {
            FonteDeRecursos f = recuperaPorCodigoExericio(fonteDeRecursos, fonteDeRecursos.getExercicio());
            if (f != null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe uma Fonte de Recurso com o mesmo código para o exercício de " + fonteDeRecursos.getExercicio() + ".");
            }
        }
        ve.lancarException();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public CodigoCOFacade getCodigoCOFacade() {
        return codigoCOFacade;
    }

    public List<FonteDeRecursos> buscarFontesDeRecursoPorContaReceitaAndExercicio(UnidadeOrganizacional unidadeOrganizacional,
                                                                                  Exercicio exercicio,
                                                                                  ContaReceita contaReceita,
                                                                                  OperacaoReceita operacaoReceita) {
        String sql = " select distinct fr.* " +
            "   from receitaloa r " +
            "  inner join loa on loa.id = r.loa_id " +
            "  inner join ldo on ldo.id = loa.ldo_id " +
            "  inner join exercicio e on e.id = ldo.exercicio_id " +
            "  inner join receitaloafonte rlf on rlf.receitaloa_id = r.id " +
            "  inner join contadedestinacao cd on cd.id = rlf.destinacaoderecursos_id " +
            "  inner join fontederecursos fr on fr.id = cd.fontederecursos_id " +
            "where r.entidade_id = :unidadeorganizacional_id " +
            "  and e.id = :exercicio_id " +
            "  and r.contadereceita_id = :contadereceita_id " +
            "  and r.operacaoreceita = :operacaoreceita " +
            "  and fr.situacaocadastral = :ativo ";
        Query q = em.createNativeQuery(sql, FonteDeRecursos.class);
        q.setParameter("unidadeorganizacional_id", unidadeOrganizacional.getId());
        q.setParameter("exercicio_id", exercicio.getId());
        q.setParameter("contadereceita_id", contaReceita.getId());
        q.setParameter("operacaoreceita", operacaoReceita.name());
        q.setParameter("ativo", SituacaoFonteRecurso.ATIVO.name());
        return q.getResultList();
    }

    public FonteDeRecursos buscarPorCodigoExercicio(String codigo, Exercicio ex) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM FONTEDERECURSOS WHERE trim(CODIGO) = :cod AND EXERCICIO_ID = :ex");
            Query q = em.createNativeQuery(sql.toString(), FonteDeRecursos.class);
            q.setParameter("cod", codigo.trim());
            q.setParameter("ex", ex.getId());
            q.setMaxResults(1);
            return (FonteDeRecursos) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
