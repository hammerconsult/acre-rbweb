/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class CadastroFacade extends AbstractFacade<Cadastro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroFacade() {
        super(Cadastro.class);
    }

    public Cadastro recuperar(Long id) {
        return em.find(Cadastro.class, id);
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        String hql = "select distinct ci from CadastroImobiliario ci left join ci.propriedade p where ci.inscricaoCadastral like :parte or (p.pessoa in "
            + " (select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte)) or (p"
            + ".pessoa in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like "
            + ":parte))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

//    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
//        String hql = "select bce from CadastroEconomico bce "
//                + "left join bce.sociedadeCadastroEconomico sce where"
//                + " bce.inscricaoCadastral like :parte  "
//                + " or bce.pessoa in (select pf from PessoaFisica pf where lower(pf.nome) like :parte or "
//                + "pf.cpf like :parte)"
//                + " or bce.pessoa in (select pj from PessoaJuridica pj where "
//                + "lower(pj.razaoSocial) like :parte or pj.cnpj like :parte)"
//                + "or "
//                + "(sce.socio in (select pf from PessoaFisica pf where lower(pf.nome) like :parte or "
//                + "pf.cpf like :parte)) or (sce.socio in (select pj from PessoaJuridica pj where "
//                + "lower(pj.razaoSocial) like :parte or pj.cnpj like :parte))";
//        Query q = em.createQuery(hql);
//        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
//        return q.getResultList();
//    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        String hql = "select ce from CadastroEconomico ce where"
            + " ce.inscricaoCadastral like :parte  ";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ContratoRendasPatrimoniais> completaContratoRendasPatrimoniais(String parte) {
        String hql = "select distinct crp from ContratoRendasPatrimoniais crp where crp.numeroContrato like :parte "
            + " or crp.locatario in "
            + " (select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte)"
            + " or crp.locatario in "
            + " ( select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like :parte)";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        String hql = "select distinct cr from CadastroRural cr where lower(cr.nomePropriedade) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<MigracaoHistorico> listaHistoricoMigracaoPorCadastro(Cadastro cadastro) {
        return em.createQuery("from MigracaoHistorico m where m.cadastro = :cadastro").setParameter("cadastro", cadastro).getResultList();
    }

    private String montaDescricaoCadastroEconomico(CadastroEconomico ce) {
        String descricao = "";
        descricao += ce.getInscricaoCadastral() + " ";
        Query q = em.createQuery("from Pessoa p where p = :pessoa");
        q.setParameter("pessoa", ce.getPessoa());
        if (q.getSingleResult() != null) {
            Pessoa p = (Pessoa) q.getSingleResult();
            descricao += p.getNomeCpfCnpj();
        }
        return descricao;
    }

    private String montaDescricaoCadastroMobiliario(CadastroImobiliario ci) {
        String descricao = "";
        descricao += ci.getInscricaoCadastral() + " ";
        Query q = em.createQuery(" select pes from Propriedade prop "
            + " join prop.pessoa pes "
            + " where prop.imovel =:ci"
            + " order by prop.proporcao desc");
        q.setParameter("ci", ci);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            Pessoa p = (Pessoa) q.getResultList().get(0);
            descricao += p.getNomeCpfCnpj();
        }
        return descricao;
    }

    private String montaDescricaoCadastroRural(CadastroRural cr) {
        String descricao = cr.getCodigo() + " - " + cr.getNomePropriedade();
        Query q = em.createQuery(" select p from PropriedadeRural pr "
            + " join pr.pessoa p "
            + " where pr.imovel =:cr"
            + " order by pr.proporcao desc");
        q.setParameter("cr", cr);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            Pessoa p = (Pessoa) q.getResultList().get(0);
            descricao += " - " + p.getNomeCpfCnpj();
        }
        return descricao;
    }

    public String montaDescricaoCadastro(TipoCadastroTributario tct, Cadastro cadastro) {
        String retorno = "";
        if (tct.equals(TipoCadastroTributario.ECONOMICO)) {
            retorno = montaDescricaoCadastroEconomico((CadastroEconomico) cadastro);
        } else if (tct.equals(TipoCadastroTributario.IMOBILIARIO)) {
            retorno = montaDescricaoCadastroMobiliario((CadastroImobiliario) cadastro);
        } else if (tct.equals(TipoCadastroTributario.RURAL)) {
            retorno = montaDescricaoCadastroRural((CadastroRural) cadastro);
        }
        return retorno;
    }

    public List<BigDecimal> buscarIdsCadastrosAtivosDaPessoa(Pessoa pessoa, Boolean consultarQuadroSocietarioCmc) {
        return buscarIdsCadastrosAtivosDaPessoa(pessoa.getId(), consultarQuadroSocietarioCmc);
    }

    public List<BigDecimal> buscarIdsCadastrosAtivosDaPessoa(Long idPessoa, Boolean consultarQuadroSocietarioCmc) {
        return buscarIdsCadastrosDaPessoa(idPessoa, consultarQuadroSocietarioCmc, true);
    }

    public List<BigDecimal> buscarIdsCadastrosDaPessoa(Long idPessoa, Boolean consultarQuadroSocietarioCmc, boolean ativo) {
        List<BigDecimal> ids = Lists.newArrayList();
        String sql = "select distinct ci.id from cadastroimobiliario ci " +
            " inner join propriedade prop on prop.imovel_id = ci.id " +
            " left join compromissario comp on comp.cadastroImobiliario_id = ci.id " +
            " where ((prop.pessoa_id = :pessoa and trunc(prop.inicioVigencia) <= :hoje and (prop.finalVigencia is null or trunc(prop.finalVigencia) > :hoje))" +
            "  or (comp.compromissario_id = :pessoa and trunc(comp.inicioVigencia) <= :hoje and (comp.fimVigencia is null or trunc(comp.fimVigencia) > :hoje))) ";
        if (ativo) {
            sql += " and ci.ativo = :ativo ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("pessoa", idPessoa);
        q.setParameter("hoje", DataUtil.dataSemHorario(new Date()));
        if (ativo) {
            q.setParameter("ativo", true);
        }
        ids.addAll(q.getResultList());

        sql = "select ce.id from CadastroEconomico ce " +
            "inner join CE_SITUACAOCADASTRAL ceSit on ceSit.cadastroEconomico_id = ce.id " +
            "inner join SituacaoCadastroEconomico sit on sit.id = ceSit.situacaoCadastroEconomico_id " +
            "where ce.pessoa_id = :pessoa " +
            "  and sit.dataRegistro = (select max(sit2.dataRegistro) from SituacaoCadastroEconomico sit2 " +
            "                           inner join CE_SITUACAOCADASTRAL ceSit2 on ceSit2.situacaoCadastroEconomico_id = sit2.ID " +
            "                           where ceSit2.cadastroEconomico_id = ce.id) ";
        if (ativo) {
            sql += " and sit.SITUACAOCADASTRAL in (:situacao)";
        }
        q = em.createNativeQuery(sql);
        q.setParameter("pessoa", idPessoa);
        if (ativo) {
            q.setParameter("situacao", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        }
        ids.addAll(q.getResultList());

        if (consultarQuadroSocietarioCmc != null && consultarQuadroSocietarioCmc) {
            q = em.createNativeQuery("select ce.id from cadastroeconomico ce " +
                    " inner join SociedadeCadastroEconomico soc on soc.cadastroeconomico_id = ce.id" +
                    " where soc.socio_id = :pessoa " +
                    "  and current_date BETWEEN coalesce(soc.dataInicio, current_date) and coalesce(soc.dataFim, current_date)")
                .setParameter("pessoa", idPessoa);
            ids.addAll(q.getResultList());
        }

        sql = "select distinct cr.id from CadastroRural cr " +
            " inner join PropriedadeRural prop on prop.imovel_id = cr.id " +
            " where (prop.pessoa_id = :pessoa and trunc(prop.inicioVigencia) <= :hoje and (prop.finalVigencia is null or trunc(prop.finalVigencia) > :hoje)) ";
        if (ativo) {
            sql += " and cr.ativo = :ativo ";
        }
        q = em.createNativeQuery(sql);
        q.setParameter("pessoa", idPessoa);
        q.setParameter("hoje", DataUtil.dataSemHorario(new Date()));
        if (ativo) {
            q.setParameter("ativo", true);
        }
        ids.addAll(q.getResultList());
        return ids;
    }
}
