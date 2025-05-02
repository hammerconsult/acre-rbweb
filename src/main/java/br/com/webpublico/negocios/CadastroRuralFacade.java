/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroRural;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PropriedadeRural;
import br.com.webpublico.entidades.TipoAreaRural;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class CadastroRuralFacade extends AbstractFacade<CadastroRural> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private AtributoFacade atributoFacade;

    public CadastroRuralFacade() {
        super(CadastroRural.class);
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    @Override
    public CadastroRural recuperar(Object id) {
        CadastroRural ir = em.find(CadastroRural.class, id);

        ir.getPropriedade().size();

        //ci.getHistoricos().size();

        return ir;
    }

    public CadastroRural recuperarSimples(Object id) {
        return em.find(CadastroRural.class, id);
    }

    public void salvar(CadastroRural entity) {
        em.merge(entity);
    }

    public List<CadastroRural> lista() {
        return em.createQuery("from CadastroRural").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Pessoa> listaTodasPessoas(String parte) {
        //System.out.println("LISTA TODAS AS PESSOAS");
        Query q;
        ArrayList<Pessoa> retorno = new ArrayList<Pessoa>();
        q = em.createQuery("from PessoaFisica obj where lower(obj.nome) like :filtro or lower(obj.cpf) like :filtro");
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());
        q = em.createQuery("from PessoaJuridica obj where lower(obj.razaoSocial) like :filtro or lower(obj.cnpj) like :filtro or lower(obj.nomeFantasia) like :filtro");
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());
        return retorno;
    }

    public List<TipoAreaRural> listaTipoAreaRural() {
        String hql = "from TipoAreaRural order by descricao";
        return em.createQuery(hql).getResultList();
    }

    public Long ultimoCodigoMaisUm() {
        String sql = "SELECT max(codigo) FROM CadastroRural";
        Query q = em.createNativeQuery(sql);
        return q.getResultList().get(0) != null ? (((BigDecimal) q.getResultList().get(0)).longValue() + 1) : 1;
    }

    public Long ultimoCodigo() {
        String sql = "SELECT max(codigo) FROM CadastroRural";
        Query q = em.createNativeQuery(sql);
        return q.getResultList().get(0) != null ? (((BigDecimal) q.getResultList().get(0)).longValue()) : 0;
    }

    public List<PropriedadeRural> recuperarPropriedadesVigentes(CadastroRural cadastroRural) {
        String hql = "select pr from PropriedadeRural pr where pr.imovel = :cr and (pr.finalVigencia is null or pr.finalVigencia > :dataAtual)";
        Query q = em.createQuery(hql);
        q.setParameter("cr", cadastroRural);
        q.setParameter("dataAtual", new Date());
        return q.getResultList();
    }

    public List<PropriedadeRural> recuperarPropriedadesNaoVigentes(CadastroRural cadastroRural) {
        String hql = "select pr from PropriedadeRural pr where pr.imovel = :cr and pr.finalVigencia is not null";
        Query q = em.createQuery(hql);
        q.setParameter("cr", cadastroRural);
        return q.getResultList();
    }

    public List<PropriedadeRural> recuperarPropriedadesVigentes(Pessoa pessoa) {
        String hql = "select pr from PropriedadeRural pr where pr.pessoa = :pessoa and (pr.finalVigencia is null or pr.finalVigencia < :dataAtual)";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("dataAtual", new Date());
        return q.getResultList();
    }

    public PropriedadeRural recuperarPropriedadePrincipal(CadastroRural cadastroRural) {
        PropriedadeRural principal = null;

        for (PropriedadeRural propriedadeRural : recuperarPropriedadesVigentes(cadastroRural)) {
            if (principal == null) {
                principal = propriedadeRural;
                continue;
            }

            if (propriedadeRural.getProporcao() > principal.getProporcao()) {
                principal = propriedadeRural;
            }

            if ((propriedadeRural.getId() > principal.getId()) && (propriedadeRural.getProporcao() == principal.getProporcao())) {
                principal = propriedadeRural;
            }
        }

        return principal;
    }

    public List<CadastroRural> listaFiltrandoPorCodigo(String s) {
        String hql = "from CadastroRural obj where ";
        hql += "to_char(obj.codigo) like :filtro ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CadastroRural> completaCodigoNomeLocalizacaoIncraNomeCpfCnpjProprietario(String parte) {
        String hql = "  select distinct cadastro " +
                "         from CadastroRural cadastro"
                + " inner join cadastro.propriedade prop"
                + " inner join prop.pessoa pessoa"
                + "      where lower(cadastro.nomePropriedade) like :parte"
                + "         or lower(cadastro.localizacaoLote) like :parte"
                + "         or lower(cadastro.numeroIncra) like :parte"
                + "         or cadastro.codigo like :parte"
                + "         or pessoa.id in (select pesFisica.id " +
                "                              from PessoaFisica pesFisica " +
                "                             where (lower(pesFisica.nome) like :parte " +
                "                               or pesFisica.cpf like :parte) " +
                "                               and pessoa.id = pesFisica.id)"
                + "         or pessoa.id in (select pesJuridica.id " +
                "                              from PessoaJuridica pesJuridica " +
                "                             where (lower(pesJuridica.razaoSocial) like :parte " +
                "                                       or  lower(pesJuridica.nomeFantasia) like :parte " +
                "                                       or pesJuridica.cnpj like :parte) " +
                "                               and pessoa.id = pesJuridica.id)";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    public List<CadastroRural> completaCodigoNomeLocalizacaoIncra(String parte) {
        String hql = "select distinct cadastro"
                   + " from CadastroRural cadastro"
                   + "  where lower(cadastro.nomePropriedade) like :parte"
                   + "     or lower(cadastro.localizacaoLote) like :parte"
                   + "     or lower(cadastro.numeroIncra) like :parte"
                   + "     or cadastro.codigo like :parte";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setMaxResults(50);
        return consulta.getResultList();
    }

    public List<CadastroRural> filtroCadastroRural(String nomePropriedade, String localizacaoLote, TipoAreaRural tipoAreaRural, String numeroIncra, Long codigo, String proprietario) {
        String juncao = " where ";
        String hql = "select distinct cad from CadastroRural cad "
                + " inner join cad.propriedade propriedade"
                + " inner join propriedade.pessoa pessoa ";

        if (nomePropriedade != null && !nomePropriedade.isEmpty()) {
            hql += juncao+" lower(cad.nomePropriedade) like :nomePropriedade";
            juncao = " and ";
        }
        if (localizacaoLote != null && !localizacaoLote.isEmpty()) {
            hql += juncao+" lower(cad.localizacaoLote) like :localizacaoLote";
            juncao = " and ";
        }
        if (numeroIncra != null && !numeroIncra.isEmpty()) {
            hql += juncao+" lower(cad.numeroIncra) like :numeroIncra";
            juncao = " and ";
        }
        if (proprietario != null && !proprietario.isEmpty()) {
            hql += juncao+" (pessoa.id in (select pf.id from PessoaFisica pf where lower(pf.nome) like :pessoa) "
                    + " or "
                    + "  (pessoa.id in (select pj.id from PessoaJuridica pj where lower(pj.nomeFantasia) like :pessoa)))";
            juncao = " and ";
        }
        if (codigo != null) {
            hql += juncao+" cad.codigo = :codigo";
            juncao = " and ";
        }
        if (tipoAreaRural != null) {
            hql += juncao+" cad.tipoAreaRural = :tipo";
            juncao = " and ";
        }
        Query consulta = em.createQuery(hql);
        consulta.setMaxResults(100);

        if (nomePropriedade != null && !nomePropriedade.isEmpty()) {
            consulta.setParameter("nomePropriedade", "%" + nomePropriedade.toLowerCase() + "%");
        }
        if (localizacaoLote != null && !localizacaoLote.isEmpty()) {
            consulta.setParameter("localizacaoLote", "%" + localizacaoLote.toLowerCase() + "%");
        }
        if (numeroIncra != null && !numeroIncra.isEmpty()) {
            consulta.setParameter("numeroIncra", "%" + numeroIncra.toLowerCase() + "%");
        }
        if (codigo != null) {
            consulta.setParameter("codigo", codigo);
        }
        if (tipoAreaRural != null) {
            consulta.setParameter("tipo", tipoAreaRural);
        }
        if (proprietario != null && !proprietario.isEmpty()) {
            consulta.setParameter("pessoa", "%" + proprietario + "%");
        }

        return consulta.getResultList();
    }

    public CadastroRural cadastroRuralPorCodigo(String codigo) {
        String hql = "from CadastroRural cr where cr.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        List<CadastroRural> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return (CadastroRural) lista.get(0);
        }
        return null;
    }

    public CadastroRural recuperarRural(Object id) {
        CadastroRural rural = super.recuperar(id);
        rural.getPropriedade().size();
        return rural;
    }

    public String recuperaEnderecoCadastro(Long id) {
        String sql = "SELECT CR.NOMEPROPRIEDADE, " +
                "                CR.LOCALIZACAOLOTE " +
                "        FROM CADASTRORURAL CR " +
                "        WHERE CR.ID = :id";
        Query q = em.createNativeQuery(sql).setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            Object[] ob = (Object[]) q.getResultList().get(0);
            CadastroRural.EnderecoCadastroStrings end = new CadastroRural.EnderecoCadastroStrings();
            end.setNomePropriedade((String) ob[0]);
            end.setLocalizacao((String) ob[1]);
            return end.getEnderecoCompleto();
        }
        return null;
    }

    public List<CadastroRural> buscarCadastroRuralPorPessoaAndNumeroIncra(String numeroIncra, Pessoa pessoa) {
        String hql = "select cr from CadastroRural cr " +
            " join cr.propriedade p " +
            " where p.pessoa = :pessoa " +
            "  and cr.numeroIncra like :numeroIncra " +
            "  and (p.inicioVigencia <= sysdate) " +
            "  and (p.finalVigencia is null or p.finalVigencia >= sysdate) ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("numeroIncra", "%" + numeroIncra.trim() + "%");
        return q.getResultList();
    }

    public Boolean buscarSituacaoCadastroRural(Long idCadastro) {
        String sql = "select coalesce(cr.ativo,1) from CadastroRural cr where cr.id = :idCadastro";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? ((BigDecimal)resultList.get(0)).compareTo(BigDecimal.ZERO) != 0 : false;
    }
}
