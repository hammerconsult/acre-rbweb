/*
 * Codigo gerado automaticamente em Thu Apr 05 08:46:51 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Stateless
public class ClasseCredorFacade extends AbstractFacade<ClasseCredor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClasseCredorFacade() {
        super(ClasseCredor.class);
    }

    public List<ClasseCredor> listaClassePorPessoa(Pessoa p) {
        String sql = "SELECT distinct CC.* "
            + " FROM CLASSECREDORPESSOA CCP"
            + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID"
            + " LEFT JOIN PESSOAFISICA PF ON CCP.PESSOA_ID = PF.ID"
            + " LEFT JOIN PESSOAJURIDICA PJ ON CCP.PESSOA_ID = PJ.ID"
            + " WHERE PF.ID = :pessoa AND CC.ATIVOINATIVO = 'ATIVO'";
        Query q = em.createNativeQuery(sql, ClasseCredor.class);
        q.setParameter("pessoa", p.getId());
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ClasseCredor>();
        } else {
            return lista;
        }
    }

    public List<ClasseCredor> buscarClassesPorPessoa(String parte, Pessoa p) {
        try {
            String sql = "SELECT distinct CC.* "
                + " FROM CLASSECREDORPESSOA CCP"
                + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID"
                + " LEFT JOIN PESSOAFISICA PF ON CCP.PESSOA_ID = PF.ID"
                + " LEFT JOIN PESSOAJURIDICA PJ ON CCP.PESSOA_ID = PJ.ID"
                + " WHERE (PF.ID  = :pessoa OR PJ.ID = :pessoa) AND CC.ATIVOINATIVO = 'ATIVO' "
                + " AND (CC.CODIGO LIKE :parte OR LOWER(CC.DESCRICAO) LIKE :parte) "
                + " AND to_date(:dataOperacao,'dd/mm/yyyy') between ccp.dataInicioVigencia and coalesce(ccp.dataFimVigencia, to_date(:dataOperacao,'dd/mm/yyyy')) "
                + " ORDER BY to_number(CC.CODIGO) ASC";
            Query q = em.createNativeQuery(sql, ClasseCredor.class);
            q.setParameter("pessoa", p.getId());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
            List lista = q.getResultList();
            if (lista.isEmpty()) {
                return new ArrayList<ClasseCredor>();
            } else {
                return lista;
            }
        } catch (Exception e) {
            return new ArrayList<ClasseCredor>();
        }
    }

    public OperacaoClasseCredor recuperaOperacaoAndVigenciaClasseCredor(Pessoa pessoa, ClasseCredor classeCredor, Date data) {
        String hql = " SELECT C.operacaoClasseCredor FROM ClasseCredorPessoa C "
            + " WHERE c.classeCredor = :classe "
            + " AND c.pessoa = :pessoa "
            + " AND (to_date(:data,'dd/mm/yyyy') between c.dataInicioVigencia and coalesce(c.dataFimVigencia, to_date(:data,'dd/mm/yyyy')))";
        Query consulta = em.createQuery(hql, OperacaoClasseCredor.class);
        consulta.setParameter("classe", classeCredor);
        consulta.setParameter("pessoa", pessoa);
        consulta.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        consulta.setMaxResults(1);
        if (consulta.getResultList().isEmpty()) {
            return null;
        }
        return (OperacaoClasseCredor) consulta.getSingleResult();
    }

    public List<ClasseCredor> listaFiltrandoDescricao(String descricao) {
        String sql = "select c.* from ClasseCredor c "
            + " where lower( c.descricao) like :descricao "
            + " or c.codigo like :codigo "
            + " AND c.ATIVOINATIVO = 'ATIVO' " +
            " order by cast(c.codigo as number)";
        Query consulta = em.createNativeQuery(sql, ClasseCredor.class);
        consulta.setParameter("descricao", "%" + descricao.toLowerCase().trim() + "%");
        consulta.setParameter("codigo", descricao.trim() + "%");
        List lista = consulta.getResultList();
        if (!lista.isEmpty()) {
            return consulta.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<ClasseCredor> listaFiltrandoPorPessoaPorTipoClasse(String parte, Pessoa p, TipoClasseCredor tipoClasseCredor) {
        String sql = "SELECT distinct CC.* "
            + " FROM CLASSECREDORPESSOA CCP"
            + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID"
            + " LEFT JOIN PESSOAJURIDICA PJ ON CCP.PESSOA_ID = PJ.ID"
            + " LEFT JOIN PESSOAFISICA PF ON CCP.PESSOA_ID = PF.ID"
            + " where CC.ATIVOINATIVO = :ativo "
            + (p != null ? " and ccp.pessoa_id = :pessoa " : "")
            + " AND (CC.DESCRICAO LIKE :parte OR CC.CODIGO LIKE :parte) "
            + " AND CC.TIPOCLASSECREDOR = :tipo "
            + " order by cast(cc.codigo as number) ";
        Query q = em.createNativeQuery(sql, ClasseCredor.class);
        if (p != null) {
            q.setParameter("pessoa", p.getId());
        }
        q.setParameter("ativo", SituacaoCadastralContabil.ATIVO.name());
        q.setParameter("tipo", tipoClasseCredor.name());
        q.setParameter("parte", "%" + parte.trim() + "%");
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return lista;
        }
    }


    public boolean validarDescricaoRepetida(ClasseCredor classeCredor) {
        String sql = "select * from ClasseCredor c "
            + " where lower( c.descricao) = :desc AND c.ATIVOINATIVO = 'ATIVO'  ";
        if (classeCredor.getId() != null) {
            sql += " and c.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql, ClasseCredor.class);
        consulta.setParameter("desc", classeCredor.getDescricao().toLowerCase());
        if (classeCredor.getId() != null) {
            sql += " and c.id = :id";
            consulta.setParameter("id", classeCredor.getId());
        }
        List lista = consulta.getResultList();
        if (lista.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean validarCodigoRepetido(ClasseCredor classeCredor) {
        String sql = "select * from ClasseCredor c "
            + " where lower( c.codigo) = :desc AND c.ATIVOINATIVO = 'ATIVO' ";
        if (classeCredor.getId() != null) {
            sql += " and c.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql, ClasseCredor.class);
        consulta.setParameter("desc", classeCredor.getCodigo());
        if (classeCredor.getId() != null) {
            sql += " and c.id = :id";
            consulta.setParameter("id", classeCredor.getId());
        }
        List lista = consulta.getResultList();
        if (lista.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<Pessoa> listaTodasPessoasPorClasseCredor(String filtro, ClasseCredor classeCredor) {
        Query q;
        HashSet<Pessoa> retorno = new HashSet<Pessoa>();
        q = getEntityManager().createQuery("select obj from PessoaFisica obj " +
            "inner join obj.classeCredorPessoas classe " +
            "where classe.classeCredor.id = :classe " +
            "and lower(obj.nome) like :filtro " +
            " or replace(replace(obj.cpf,'.',''),'-','') like :filtro", PessoaFisica.class);
        q.setParameter("classe", classeCredor.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("select  obj  from PessoaJuridica obj " +
            "inner join obj.classeCredorPessoas classe " +
            "where classe.classeCredor.id = :classe " +
            "and lower(obj.razaoSocial) like :filtro " +
            "or replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :filtro " +
            "or lower(obj.nomeFantasia) like :filtro", PessoaJuridica.class);
        q.setParameter("classe", classeCredor.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        return new ArrayList<>(retorno);
    }

    public List<ClasseCredor> buscarClassesPorPessoa(Pessoa p, Date data) {
        String sql = "SELECT distinct CC.* "
            + " FROM CLASSECREDORPESSOA CCP"
            + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID"
            + " INNER JOIN PESSOA P ON CCP.PESSOA_ID = P.ID"
            + " WHERE P.ID = :pessoa "
            + " AND CC.ATIVOINATIVO = :situacao" +
            "  and to_date(:dataVigencia, 'dd/MM/yyyy') between CCP.datainiciovigencia and coalesce (ccp.datafimvigencia, to_date(:dataVigencia, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, ClasseCredor.class);
        q.setParameter("pessoa", p.getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(data));
        q.setParameter("situacao", SituacaoCadastralContabil.ATIVO.name());
        List retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return retorno;
        }
    }
}

