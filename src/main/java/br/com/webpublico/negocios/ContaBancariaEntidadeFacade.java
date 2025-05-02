/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

/**
 * @author terminal1
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoContaFinanceira;
import br.com.webpublico.enums.TipoRecursoSubConta;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ContaBancariaEntidadeFacade extends AbstractFacade<ContaBancariaEntidade> {

    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ContaBancariaEntidadeFacade() {
        super(ContaBancariaEntidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    @Override
    public ContaBancariaEntidade recuperar(Object id) {
        ContaBancariaEntidade cbe = em.find(ContaBancariaEntidade.class, id);
        cbe.getSubContas().size();
        for (SubConta sc : cbe.getSubContas()) {
            sc.getUnidadesOrganizacionais().size();
            sc.getSubContaFonteRecs().size();
        }
        return cbe;
    }

    @Override
    public List<ContaBancariaEntidade> lista() {
        String sql = "SELECT CBE.* FROM CONTABANCARIAENTIDADE CBE ORDER BY CBE.ID DESC";
        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaOrdenada() {
        String sql = "SELECT CBE.* FROM CONTABANCARIAENTIDADE CBE ORDER BY CBE.NOMECONTA";
        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaContaporReceitaLOAFonte(ReceitaLOAFonte receita) {
        StringBuilder sql = new StringBuilder();
        sql.append(sql);
        sql.append(" SELECT * from CONTABANCARIAENTIDADE con ");
        sql.append(" INNER JOIN FONTEDERECURSOS fonte on fonte.ID = con.FONTEDERECURSOS_ID ");
        sql.append(" INNER JOIN CONTADEDESTINACAO desti on desti.FONTEDERECURSOS_ID = fonte.ID ");
        sql.append(" INNER JOIN RECEITALOAFONTE rec  on rec.DESTINACAODERECURSOS_ID = desti.ID and rec.ID=:param");

        Query q = getEntityManager().createNativeQuery(sql.toString(), ContaBancariaEntidade.class);
        q.setParameter("param", receita.getId());
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaPorFonteDeRecursos(String parte, FonteDeRecursos fr, String... atributos) {
        String hql = "from ContaBancariaEntidade c where c.fonteDeRecursos =:fr "
            + "and ";
        for (String atributo : atributos) {
            hql += "lower(c." + atributo + ") like :parte OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        q.setParameter("fr", fr);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaContasPorEntidade(Entidade entidade) {
        StringBuilder sql = new StringBuilder();
        sql.append(sql);
        sql.append(" from ContaBancariaEntidade c where c.entidade = :entidade and c.codigoDoConvenio is not null");
        Query q = em.createQuery(sql.toString());
        q.setParameter("entidade", entidade);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaContasPorBanco(Banco banco) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from ContaBancariaEntidade c where c.agencia.banco = :banco");
        Query q = em.createQuery(hql.toString());
        q.setParameter("banco", banco);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaContasPorBancos(Banco[] bancos) {
        //System.out.println("Entrou aqui");
        if (bancos == null) {
            return new ArrayList();
        }
        StringBuilder bancosIn = new StringBuilder();
        String juncao = "(";
        for (Banco banco : bancos) {
            bancosIn.append(juncao);
            bancosIn.append(banco.getId());
            juncao = ",";
        }
        if (bancosIn.length() <= 0) {
            return new ArrayList();
        }
        bancosIn.append(")");
        String sql = " select * from contabancariaentidade cbe " +
            " inner join agencia a on cbe.agencia_id = a.id " +
            " inner join banco b on a.banco_id = b.id " +
            " where b.id in " + bancosIn.toString();
        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        return q.getResultList();
    }

    public List<SubConta> listaFiltrandoSubContasPorBanco(Banco banco, String parte) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from SubConta c where c.contaBancariaEntidade.agencia.banco = :banco and c.codigo like :numeroConta");
        Query q = em.createQuery(hql.toString());
        q.setParameter("banco", banco);
        q.setParameter("numeroConta", "%" + banco + "%");
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaPorCodigo(String parte) {
        String sql = "select cbe.* from contabancariaentidade cbe "
            + " inner join agencia a on cbe.agencia_id = a.id"
            + " inner join banco b on a.banco_id = b.id"
            + " where (cbe.numeroconta||cbe.digitoverificador like :param or (lower (cbe.nomeConta) like :param) "
            + " or a.numeroAgencia || a.digitoverificador like :param or (lower(a.nomeAgencia) like :param) "
            + " or lower(b.descricao) like :param or b.numeroBanco like :param)";
        Query q = getEntityManager().createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaFiltrandoPorUnidadeDoUsuario(String parte, UnidadeOrganizacional unidade, Exercicio exercicio, UsuarioSistema usuarioSistema, Date data) {
        String sql = " SELECT DISTINCT CBE.* FROM CONTABANCARIAENTIDADE CBE "
            + " INNER JOIN AGENCIA A ON CBE.AGENCIA_ID = A.ID "
            + " INNER JOIN BANCO B ON A.BANCO_ID = B.ID "
            + " INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID "
            + " INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID "
            + " INNER JOIN HIERARQUIAORGANIZACIONAL HOORC ON UNID.ID = HOORC.SUBORDINADA_ID "
            + " AND HOORC.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' AND TO_DATE(:data, 'dd/MM/yyyy') BETWEEN HOORC.INICIOVIGENCIA AND COALESCE(HOORC.FIMVIGENCIA, TO_DATE(:data, 'dd/MM/yyyy')) "
            + " INNER JOIN HIERARQUIAORGCORRE CORRESP on HOORC.id = corresp.hierarquiaorgorc_id"
            + " INNER JOIN usuariounidadeorganizacorc UUO ON UUO.UNIDADEORGANIZACIONAL_ID = hoorc.SUBORDINADA_ID "
            + " INNER JOIN USUARIOSISTEMA USU ON UUO.USUARIOSISTEMA_ID = USU.ID "
            + " WHERE (CBE.NUMEROCONTA LIKE :param OR (LOWER (CBE.NOMECONTA) LIKE :param) "
            + " OR A.NUMEROAGENCIA LIKE :param OR (LOWER(A.NOMEAGENCIA) LIKE :param) "
            + " OR LOWER(B.DESCRICAO) LIKE :param OR B.NUMEROBANCO LIKE :param) "
            + " AND SC.EXERCICIO_ID = :exercicio "
            + " AND UUO.USUARIOSISTEMA_ID = :usuario "
            + " ORDER BY CBE.NUMEROCONTA ";
        Query q = getEntityManager().createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("usuario", usuarioSistema.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        q.setMaxResults(10);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<ContaBancariaEntidade>();
        }
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> getContabancariaPorAgencia(String parte, Agencia agencia) {
//        //System.out.println("parte..: " + parte);
        //System.out.println("Agencia..: " + agencia);
        String hql = "select conta.* from ContaBancariaEntidade conta "
            + "where conta.nomeConta like :nomeconta "
            + "and conta.agencia_id = :agencia";
        Query q = em.createNativeQuery(hql, ContaBancariaEntidade.class);
        q.setParameter("nomeconta", "%" + parte + "%");
        q.setParameter("agencia", agencia.getId());

        List<ContaBancariaEntidade> contaBancariaEntidade = (List<ContaBancariaEntidade>) q.getResultList();
        return contaBancariaEntidade;
    }

    public ContaBancariaEntidade getContabancariaPorEntidadeAgencia(Entidade entidade, Agencia agencia) {
        String hql = "select conta from ContaBancariaEntidade conta "
            + "where conta.entidade = :entidade "
            + "and conta.agencia = :agencia";

        Query q = em.createQuery(hql);
        q.setParameter("entidade", entidade);
        q.setParameter("agencia", agencia);

        List<ContaBancariaEntidade> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new ContaBancariaEntidade();
        }

        return lista.get(0);
    }

    public boolean validaVigenciaHierarquia(UnidadeOrganizacional unidadeOrganizacional, Date data) {
        String sql = "SELECT * "
            + " FROM VWHIERARQUIAORCAMENTARIA "
            + " WHERE SUBORDINADA_ID = :unidade"
            + " and (to_date(:data, 'dd/MM/yyyy') BETWEEN INICIOVIGENCIA AND COALESCE(FIMVIGENCIA, to_date(:data, 'dd/MM/yyyy')))";
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("data", data, TemporalType.DATE);
        if (q.getResultList() == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validaContaBancMesmoCodigoEAgencia(ContaBancariaEntidade contaBancariaEntidade) {
        String hql = " FROM ContaBancariaEntidade cbe "
            + " WHERE cbe.numeroConta = :numero "
            + " AND cbe.digitoVerificador = :digito"
            + " AND cbe.agencia = :idAgencia ";
        if (contaBancariaEntidade.getId() != null) {
            hql += " AND cbe.id <> :idConta ";
        }
        Query consulta = em.createQuery(hql);
        consulta.setParameter("numero", contaBancariaEntidade.getNumeroConta().trim());
        consulta.setParameter("digito", contaBancariaEntidade.getDigitoVerificador().trim());
        consulta.setParameter("idAgencia", contaBancariaEntidade.getAgencia());
        if (contaBancariaEntidade.getId() != null) {
            consulta.setParameter("idConta", contaBancariaEntidade.getId());
        }
        if (consulta.getResultList().isEmpty()) {
            return true;
        }
        return false;

    }

    public Banco recuperaBancoPorAgencica(Agencia agencia) {
        String sql = "SELECT b.* "
            + " FROM agencia ag "
            + " INNER JOIN banco b ON b.id = ag.banco_id "
            + " WHERE ag.id = :agencia";
        Query q = em.createNativeQuery(sql, Banco.class);
        q.setParameter("agencia", agencia.getId());
        if (!q.getResultList().isEmpty()) {
            return (Banco) q.getSingleResult();
        } else {
            return new Banco();
        }
    }

    public List<ContaBancariaEntidade> listaPorSituacao(String parte, SituacaoConta situacaoConta) {
        String consulta = " from ContaBancariaEntidade cbe "
            + " where (( lower(cbe.numeroConta) like :parte) "
            + " or ( lower(cbe.nomeConta) like :parte)) "
            + " and cbe.situacao = :sc ";

        Query q = em.createQuery(consulta, ContaBancariaEntidade.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("sc", situacaoConta);
        q.setMaxResults(10);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<ContaBancariaEntidade>();
        } else {
            return q.getResultList();
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }


    public List<ContaBancariaEntidade> listaFiltrandoAtivaPorUnidade(String parte, UnidadeOrganizacional unidade, Exercicio exercicio, FonteDeRecursos fonteDeRecursos, List<TipoContaFinanceira> tiposContaFinanceiras, List<TipoRecursoSubConta> tipoRecursos) {
        String sql = "SELECT DISTINCT CBE.* FROM CONTABANCARIAENTIDADE CBE " +
            "                  INNER JOIN AGENCIA A ON CBE.AGENCIA_ID = A.ID " +
            "                  INNER JOIN BANCO B ON A.BANCO_ID = B.ID " +
            "                  INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  LEFT  JOIN SUBCONTAFONTEREC SCF ON SCF.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  WHERE (CBE.NUMEROCONTA LIKE :param OR (LOWER (CBE.NOMECONTA) LIKE :param) " +
            "                  OR A.NUMEROAGENCIA LIKE :param OR (LOWER(A.NOMEAGENCIA) LIKE :param) " +
            "                  OR LOWER(B.DESCRICAO) LIKE :param OR B.NUMEROBANCO LIKE :param) " +
            "                  AND SC.EXERCICIO_ID = :exercicio " +
            "                  AND CBE.SITUACAO = '" + SituacaoConta.ATIVO.name() + "'";
        if (unidade != null) {
            if (unidade.getId() != null) {
                sql += "   AND UNID.ID = :idUnidade";
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                sql += "   AND SCF.FONTEDERECURSOS_ID = :idFonteRecurso";
            }
        }
        if (tiposContaFinanceiras != null) {
            sql += "                  AND SUB.TIPOCONTAFINANCEIRA IN (" + subContaFacade.getTiposContaFinanceiraAsString(tiposContaFinanceiras) + ")";
        }

        if (tipoRecursos != null) {
            sql += "                  AND SUB.TIPORECURSOSUBCONTA IN (" + subContaFacade.getTiposRecursosAsString(tipoRecursos) + ")";
        }
        sql += "           ORDER BY CBE.NUMEROCONTA";
        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", exercicio.getId());
        if (unidade != null) {
            if (unidade.getId() != null) {
                q.setParameter("idUnidade", unidade.getId());
            }
        }
        if (fonteDeRecursos != null) {
            if (fonteDeRecursos.getId() != null) {
                q.setParameter("idFonteRecurso", fonteDeRecursos.getId());
            }
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<ContaBancariaEntidade>();
        }
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> buscarContasBancariasPorUnidade(String parte, UnidadeOrganizacional unidade, Exercicio exercicio) {
        String sql = "SELECT DISTINCT CBE.* FROM CONTABANCARIAENTIDADE CBE " +
            "                  INNER JOIN AGENCIA A ON CBE.AGENCIA_ID = A.ID " +
            "                  INNER JOIN BANCO B ON A.BANCO_ID = B.ID " +
            "                  INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  WHERE (CBE.NUMEROCONTA LIKE :param OR (LOWER (CBE.NOMECONTA) LIKE :param) " +
            "                  OR A.NUMEROAGENCIA LIKE :param OR (LOWER(A.NOMEAGENCIA) LIKE :param) " +
            "                  OR LOWER(B.DESCRICAO) LIKE :param OR B.NUMEROBANCO LIKE :param) " +
            "                  AND SC.EXERCICIO_ID = :exercicio " +
            "                  AND CBE.SITUACAO = :ativo " +
            "                  AND SUB.SITUACAO = :ativo " +
            "                  AND UNID.ID = :idUnidade " +
            "                  ORDER BY CBE.NUMEROCONTA ";

        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        q.setMaxResults(10);
        List<ContaBancariaEntidade> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public List<UnidadeOrganizacional> buscarUnidadesDaContaBancaria(ContaBancariaEntidade contaBancariaEntidade, Exercicio exercicio, Date dataOperacao) {

        String sql = "SELECT DISTINCT UNID.* " +
            "                  FROM CONTABANCARIAENTIDADE CBE " +
            "                  INNER JOIN SUBCONTA SUB ON SUB.CONTABANCARIAENTIDADE_ID = CBE.ID " +
            "                  INNER JOIN SUBCONTAUNIORG SC ON SC.SUBCONTA_ID = SUB.ID " +
            "                  INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = SC.UNIDADEORGANIZACIONAL_ID " +
            "                  INNER JOIN vwhierarquiaorcamentaria vw on unid.id = vw.subordinada_id  " +
            "                  AND SC.EXERCICIO_ID = :exercicio " +
            "                  AND CBE.SITUACAO = :ativo " +
            "                  AND SUB.SITUACAO = :ativo " +
            "                  AND to_date(:dataOperacao, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "                  AND CBE.ID = :idConta ";

        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("idConta", contaBancariaEntidade.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        List<UnidadeOrganizacional> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public List<ContaBancariaEntidade> buscarContasBancariasParaCreditoDeSalario() {
        String hql = " select cbe from ContaBancariaEntidade cbe" +
            " inner join cbe.entidade e " +
            " where cbe.codigoDoConvenio is not null ";

        hql += " order by cbe.contaPrincipalFP desc, e.nome, cbe.agencia.numeroAgencia, cbe.agencia.digitoVerificador, cbe.numeroConta, cbe.digitoVerificador";

        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public ContaBancariaEntidade buscarContaBancariaPrincipalParaCreditoDeSalario() {
        String hql = " select cbe from ContaBancariaEntidade cbe" +
            " inner join cbe.entidade e " +
            " where cbe.codigoDoConvenio is not null " +
            " and cbe.contaPrincipalFP = :hasConta " +
            " order by cbe.id ";
        Query q = em.createQuery(hql);
        q.setParameter("hasConta", Boolean.TRUE);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ContaBancariaEntidade) resultList.get(0);
        }
        return null;
    }

    public Boolean hasContaBancariaPrincipalFP(Long idContaBancariaEntidade) {
        String sql = " select * from CONTABANCARIAENTIDADE where contaPrincipalFP = :hasConta ";
        if (idContaBancariaEntidade != null) {
            sql += " and id <> :idContaBancariaEntidade";
        }

        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("hasConta", Boolean.TRUE);
        if (idContaBancariaEntidade != null) {
            q.setParameter("idContaBancariaEntidade", idContaBancariaEntidade);
        }
        return q.getResultList().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
    }

    public List<Object[]> buscarContasBancariasEntidades(Exercicio exercicio, Date dataOperacao) {
        String sql = " select b.NUMEROBANCO as banco, " +
            "       ag.NUMEROAGENCIA || ag.DIGITOVERIFICADOR as agencia, " +
            " cbe.NUMEROCONTA || cbe.DIGITOVERIFICADOR as numeroConta,  cbe.TIPOCONTABANCARIA, " +
            "       cbe.DATAABERTURA, " +
            "       cbe.DATAENCERRAMENTO, " +
            "       coalesce((SELECT coalesce(ssc.TOTALCREDITO, 0) - coalesce(ssc.TOTALDEBITO, 0) " +
            "        FROM saldosubconta ssc " +
            "        WHERE trunc(ssc.datasaldo) = (select min(trunc(s.datasaldo)) from saldosubconta s " +
            "                                      where trunc(s.datasaldo) <= to_date(:dataOperacao, 'dd/MM/yyyy') " +
            "                                        and s.UNIDADEORGANIZACIONAL_ID = ssc.UNIDADEORGANIZACIONAL_ID " +
            "                                        AND ssc.subconta_id = s.subconta_id " +
            "                                        and ssc.CONTADEDESTINACAO_ID = s.CONTADEDESTINACAO_ID) " +
            "          AND ssc.subconta_id = sub.id " +
            "          AND ssc.unidadeorganizacional_id = subuni.UNIDADEORGANIZACIONAL_ID " +
            "          and ssc.CONTADEDESTINACAO_ID = subfont.CONTADEDESTINACAO_ID), 0) as saldo, " +
            "       vw.codigo, " +
            "       pj.cnpj as cnpjResponsavel, " +
            "       fr.codigo as fontederecursos, " +
            "        (select c.codigo from OCCBANCO occ " +
            "                           inner join origemcontacontabil origem on occ.id = origem.id " +
            "                           inner join conta c on origem.CONTACONTABIL_ID = c.id " +
            "         where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(origem.INICIOVIGENCIA) and coalesce(trunc(origem.FIMVIGENCIA), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "            and occ.SUBCONTA_ID = sub.id) as contaContabil " +
            "from CONTABANCARIAENTIDADE cbe " +
            "inner join agencia ag on cbe.AGENCIA_ID = ag.ID " +
            "inner join banco b on ag.BANCO_ID = b.ID " +
            "inner join subconta sub on cbe.ID = sub.CONTABANCARIAENTIDADE_ID " +
            "inner join SUBCONTAUNIORG subuni on sub.ID = subuni.SUBCONTA_ID and subuni.EXERCICIO_ID = :exercicio " +
            "inner join vwhierarquiaorcamentaria vw on subuni.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            "inner join SUBCONTAFONTEREC subfont on sub.ID = subfont.SUBCONTA_ID " +
            "inner join FONTEDERECURSOS fr on subfont.FONTEDERECURSOS_ID = fr.ID and fr.EXERCICIO_ID = :exercicio " +
            " left join entidade ent on cbe.ENTIDADE_ID = ent.ID " +
            " left join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.id " +
            "where sub.SITUACAO = :ativo " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "order by b.NUMEROBANCO, " +
            "       ag.NUMEROAGENCIA, " +
            "       ag.DIGITOVERIFICADOR, " +
            "       cbe.NUMEROCONTA, " +
            "       cbe.DIGITOVERIFICADOR ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        return q.getResultList();
    }

    public List<Object[]> buscarContasFinanceiras(Exercicio exercicio, Date dataOperacao) {
        String sql = " select b.NUMEROBANCO as banco, " +
            "       ag.NUMEROAGENCIA || ag.DIGITOVERIFICADOR as agencia, " +
            "       sub.codigo as numeroConta, cbe.TIPOCONTABANCARIA, sub.tipoContaFinanceira,  " +
            "       cbe.DATAABERTURA, " +
            "       cbe.DATAENCERRAMENTO, " +
            "       coalesce((SELECT coalesce(ssc.TOTALCREDITO, 0) - coalesce(ssc.TOTALDEBITO, 0) " +
            "        FROM saldosubconta ssc " +
            "        WHERE trunc(ssc.datasaldo) = (select min(trunc(s.datasaldo)) from saldosubconta s " +
            "                                      where trunc(s.datasaldo) <= to_date('01/01/' || :ano, 'dd/MM/yyyy') " +
            "                                        and s.UNIDADEORGANIZACIONAL_ID = ssc.UNIDADEORGANIZACIONAL_ID " +
            "                                        AND ssc.subconta_id = s.subconta_id " +
            "                                        and ssc.CONTADEDESTINACAO_ID = s.CONTADEDESTINACAO_ID) " +
            "          AND ssc.subconta_id = sub.id " +
            "          AND ssc.unidadeorganizacional_id = subuni.UNIDADEORGANIZACIONAL_ID " +
            "          and ssc.CONTADEDESTINACAO_ID = subfont.CONTADEDESTINACAO_ID), 0) as saldo, " +
            "       vw.codigo, " +
            "       pj.cnpj as cnpjResponsavel, " +
            "       fr.codigo as fontederecursos, " +
            "        (select c.codigo from OCCBANCO occ " +
            "                           inner join origemcontacontabil origem on occ.id = origem.id " +
            "                           inner join conta c on origem.CONTACONTABIL_ID = c.id " +
            "         where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(origem.INICIOVIGENCIA) and coalesce(trunc(origem.FIMVIGENCIA), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "            and occ.SUBCONTA_ID = sub.id) as contaContabil " +
            "from CONTABANCARIAENTIDADE cbe " +
            "inner join agencia ag on cbe.AGENCIA_ID = ag.ID " +
            "inner join banco b on ag.BANCO_ID = b.ID " +
            "inner join subconta sub on cbe.ID = sub.CONTABANCARIAENTIDADE_ID " +
            "inner join SUBCONTAUNIORG subuni on sub.ID = subuni.SUBCONTA_ID and subuni.EXERCICIO_ID = :exercicio " +
            "inner join vwhierarquiaorcamentaria vw on subuni.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            "inner join SUBCONTAFONTEREC subfont on sub.ID = subfont.SUBCONTA_ID " +
            "inner join FONTEDERECURSOS fr on subfont.FONTEDERECURSOS_ID = fr.ID and fr.EXERCICIO_ID = :exercicio " +
            " left join entidade ent on cbe.ENTIDADE_ID = ent.ID " +
            " left join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.id " +
            "where sub.SITUACAO = :ativo " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "order by b.NUMEROBANCO, " +
            "       ag.NUMEROAGENCIA, " +
            "       ag.DIGITOVERIFICADOR, " +
            "       cbe.NUMEROCONTA, " +
            "       cbe.DIGITOVERIFICADOR ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        return q.getResultList();
    }

    public List<Object[]> buscarBancos(Exercicio exercicio) {
        String sql = " select distinct b.descricao as nomeBanco, " +
            "     b.NUMEROBANCO as banco " +
            "from CONTABANCARIAENTIDADE cbe " +
            "inner join agencia ag on cbe.AGENCIA_ID = ag.ID " +
            "inner join banco b on ag.BANCO_ID = b.ID " +
            "inner join subconta sub on cbe.ID = sub.CONTABANCARIAENTIDADE_ID " +
            "inner join SUBCONTAUNIORG subuni on sub.ID = subuni.SUBCONTA_ID and subuni.EXERCICIO_ID = :exercicio " +
            "where sub.SITUACAO = :ativo " +
            "order by b.NUMEROBANCO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<Object[]> buscarMovimentoContas(Exercicio exercicio, boolean isContaFinanceira) {
        String sql = " select b.NUMEROBANCO as banco, " +
            "        ag.NUMEROAGENCIA || ag.DIGITOVERIFICADOR as agencia, " +
            (isContaFinanceira ? " sub.codigo as numeroConta, " : " cbe.NUMEROCONTA || cbe.DIGITOVERIFICADOR as numeroConta, ") +
            "        mcf.DATASALDO, " +
            "        case when mcf.TOTALCREDITO > 0 then 1 else 2 end as tipomovimento, " +
            "        case when mcf.TOTALCREDITO > 0 then mcf.TOTALCREDITO else mcf.TOTALDEBITO end as valor " +
            " from MovimentoContaFinanceira mcf " +
            " inner join subconta sub on mcf.SUBCONTA_ID = sub.ID " +
            " inner join CONTABANCARIAENTIDADE cbe on cbe.ID = sub.CONTABANCARIAENTIDADE_ID " +
            " inner join agencia ag on cbe.AGENCIA_ID = ag.ID " +
            " inner join banco b on ag.BANCO_ID = b.ID " +
            " where sub.SITUACAO = :ativo " +
            " and extract(year from mcf.DATASALDO) = :exercicio " +
            " order by mcf.DATASALDO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        return q.getResultList();
    }

    public List<Object[]> buscarSaldosContas(Exercicio exercicio, boolean isContaFinanceira) {
        String sql = " select banco, " +
            "   agencia, " +
            "   numeroConta, " +
            "   dataSaldo," +
            "   sum(valor) as valor, " +
            "   fontederecursos " +
            " from (" +
            " SELECT b.NUMEROBANCO as banco, " +
            "       ag.NUMEROAGENCIA || ag.DIGITOVERIFICADOR as agencia, " +
            (isContaFinanceira ? " sub.codigo as numeroConta, " : " cbe.NUMEROCONTA || cbe.DIGITOVERIFICADOR as numeroConta, ") +
            "       ssc.DATASALDO, " +
            "       coalesce(ssc.TOTALCREDITO, 0) - coalesce(ssc.TOTALDEBITO, 0) as valor, " +
            "       fr.codigo as fontederecursos " +
            " FROM saldosubconta ssc " +
            "inner join subconta sub on ssc.SUBCONTA_ID = sub.ID " +
            "inner join CONTABANCARIAENTIDADE cbe on cbe.ID = sub.CONTABANCARIAENTIDADE_ID " +
            "inner join agencia ag on cbe.AGENCIA_ID = ag.ID " +
            "inner join banco b on ag.BANCO_ID = b.ID " +
            "inner join FONTEDERECURSOS fr on ssc.fonteDeRecursos_id = fr.ID " +
            " where extract(year from ssc.datasaldo) = :exercicio " +
            " and sub.SITUACAO = :ativo " +
            " ) " +
            " group by banco, " +
            "   agencia, " +
            "   numeroConta, " +
            "   dataSaldo, " +
            "   fontederecursos " +
            " order by datasaldo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("ativo", SituacaoConta.ATIVO.name());
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> buscarContaBancaria(String numeroConta, String digitoVerificador) {
        if (numeroConta == null || numeroConta.isEmpty()) return Lists.newArrayList();
        String sql = "select * from contabancariaentidade " +
            " where situacao = :situacao" +
            "   and (lower(numeroConta) LIKE :filtro OR lower(nomeConta) LIKE :filtro) " +
            (digitoVerificador != null ? " and digitoVerificador = :dgtVerificador " : "") +
            " order by id desc";
        Query q = em.createNativeQuery(sql, ContaBancariaEntidade.class);
        q.setParameter("situacao", SituacaoConta.ATIVO.name());
        q.setParameter("filtro", "%" + numeroConta.trim().toLowerCase() + "%");
        if (digitoVerificador != null && !digitoVerificador.isEmpty()) {
            q.setParameter("dgtVerificador", digitoVerificador);
        }
        return q.getResultList();
    }
}
