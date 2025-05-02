/*
 * Codigo gerado automaticamente em Thu Aug 04 09:41:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class VinculosFPPermitidosPorUsuarioFacade extends AbstractFacade<VinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VinculosFPPermitidosPorUsuarioFacade() {
        super(VinculoFP.class);
    }

    public VinculoFP recuperarVinculo(String matricula, String numero) {
        Query q = em.createQuery("select v " +
            "  from VinculoFP v" +
            " where v.matriculaFP.matricula = :mat and v.numero = :numero order by v.inicioVigencia desc");
        q.setParameter("mat", matricula);
        q.setParameter("numero", numero);
        q.setMaxResults(1);

        try {
            return (VinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public VinculoFP recuperarVinculo(VinculoFP v) {
        Query q = em.createQuery("select v" +
            "  from VinculoFP v" +
            " where v = :vinculo order by v.inicioVigencia desc");
        q.setParameter("vinculo", v);
        q.setMaxResults(1);
        try {
            return (VinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean isVinculoPermitidoParaUsuarioLogado(VinculoFP v) {
        return recuperarContratoPermitidoParaUsuarioLogado(v) != null;
    }

    public boolean isVinculoFPVigenteEmDataOperacao(VinculoFP v) {
        String hql = "select v.id from VinculoFP v" +
            "     where :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia, :dataOperacao)" +
            " and v = :vinculo";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("vinculo", v);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Método utilizado para COMPLETAR os vínculos que o usuário logado possui acesso
     * Caso o usuário tenha em seu cadastro a opção 'Todos Vinculos' marcada, o sistema irá recuperar todos os vinculos vigentes na data operação.
     * Caso o usuário NÃO tenha em seu cadastro a opção 'Todos Vinculos' seguem as regras abaixo:
     * -> Caso a unidade adminitrativa logada seja um órgão, o sistema irá recuperar todos os vinculos pertencentes diretamente àquele órgão ou que estejam em suas unidades subordinadas.
     * -> Caso a unidade adminitrativa logada NÃO seja um orgão, o sistema irá recuperar todos os vinculos pertencentes somente àquela unidade
     *
     * @param s parte do nome/matrícula/número do contrato do servidor pesquisado
     * @param c classe na qual o desenvolvedor deseja que seja o resultado (ContratoFP, Aposentadoria, Beneficiário etc)
     *          USE 'null' PARA retornar VinculoFP
     */
    public <T> List<T> buscarVinculosPermitidosParaUsuarioLogado(String s, Class c) {
        return buscarVinculosDoOrgaoLogadoNaDataOperacao(s, c);
    }

    private <T> List<T> listaContratosVigentesEm(String s, Date dataOperacao, Class c) {
        String complemento = c != null ? " and obj.class = " + c.getSimpleName() : "";
        String hql = "select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome|| ' (' || obj.matriculaFP.pessoa.nomeTratamento || ') ' || ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id))  from VinculoFP obj " +
            " where :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao) " +
            complemento +
            "   and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "    or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "    or (lower(obj.numero) like :filtro) " +
            "    or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "    order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }


    private <T> List<T> buscarVinculosDoOrgaoLogadoNaDataOperacao(String s, Class c) {
        String complemento = c != null ? " and obj.class = " + c.getSimpleName() : "";

        String hql = "select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome|| ' (' || obj.matriculaFP.pessoa.nomeTratamento || ') ' || ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id))  from VinculoFP obj"+
            " inner join obj.unidadeOrganizacional uo" +
            " inner join uo.hierarquiasOrganizacionais ho" +
            "      where " + getComplementoQueryOrgao("ho.codigo", sistemaFacade.getSistemaService().getAdministrativasOrgao(), false) +
            complemento +
            "        and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia,:dataOperacao)" +
            "        and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataOperacao)" +
            "        and ho.tipoHierarquiaOrganizacional = :tipoHierarquia" +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "        order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        return q.getResultList();
    }


    public String getComplementoQueryOrgao(String nomeCampo, List<HierarquiaOrganizacionalDTO> orgaos, boolean incluiAndComecoQuery) {
        if (orgaos == null || orgaos.isEmpty()) {
            return "";
        }
        StringBuilder complementoQuery = new StringBuilder();
        if (incluiAndComecoQuery) {
            complementoQuery.append(" and ");
        }
        complementoQuery.append(" (");
        for (HierarquiaOrganizacionalDTO orgao : orgaos) {
            complementoQuery.append(nomeCampo + " like '" + orgao.getCodigoSemZerosFinais() + "%' or ");
        }
        complementoQuery = new StringBuilder(complementoQuery.substring(0, complementoQuery.length() - 3));
        complementoQuery.append(") ");

        return complementoQuery.toString();
    }


    private <T> List<T> listaContratosComLotacaoFuncionalNaUnidade(String s, UnidadeOrganizacional uo, Class c) {
        String complemento = c != null ? " and obj.class = " + c.getSimpleName() : "";
        String hql = " select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome|| ' (' || obj.matriculaFP.pessoa.nomeTratamento || ') ' || ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id))  from VinculoFP obj " +
            " inner join obj.lotacaoFuncionals lotacao" +
            "      where lotacao.unidadeOrganizacional = :unidade" +
            complemento +
            "        and :dataOperacao between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:dataOperacao) " +
            "        and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao) " +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "         order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("unidade", uo);
        return q.getResultList();
    }

    /**
     * =======================================================================================================
     * =======================================================================================================
     */

    /**
     * Método utilizado para RECUPERAR o vinculo que o usuário logado possui acesso
     * Caso o usuário tenha em seu cadastro a opção 'Todos Vinculos' marcada, o sistema irá tentar recuperar o vinculo com base na data operação.
     * Caso o usuário NÃO tenha em seu cadastro a opção 'Todos Vinculos' seguem as regras abaixo:
     * -> Caso a unidade adminitrativa logada seja um órgão, o sistema irá recuperar o vinculo desde que ele esteja lotado(lotação funcional) nas unidades subordinadas ao orgão logado.
     * -> Caso a unidade adminitrativa logada NÃO seja um orgão, o sistema irá recuperar o vinculo desde que ele esteja lotado(lotação funcional) na unidade logada
     *
     * @param v vinculoFp a ser pesquisado/validado
     */
    public VinculoFP recuperarContratoPermitidoParaUsuarioLogado(VinculoFP v) {
        if (sistemaFacade.getUsuarioCorrente().getAcessoTodosVinculosRH()) {
            return recuperarVinculo(v);
        } else {
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);

            if (ho.getNivelNaEntidade() == 2) {
                return getVinculoDoOrgaoLogadoNaDataOperacao(v, ho);
            } else {
                return getContratosComLotacaoFuncionalNaUnidade(v, ho.getSubordinada());
            }
        }
    }

    private VinculoFP getVinculoDoOrgaoLogadoNaDataOperacao(VinculoFP v, HierarquiaOrganizacional ho) {
        if (ho.getNivelNaEntidade() != 2) {
            return null;
        }

        String hql = "select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from VinculoFP obj" +
            " inner join obj.lotacaoFuncionals lotacao " +
            " inner join lotacao.unidadeOrganizacional uo" +
            " inner join uo.hierarquiasOrganizacionais ho" +
            "      where substr(ho.codigo,1,6) = :codigoOrgao" +
            "        and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia,:dataOperacao)" +
            "        and :dataOperacao between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:dataOperacao)" +
            "        and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataOperacao)" +
            "        and ho.tipoHierarquiaOrganizacional = :tipoHierarquia" +
            "        and obj = :vinculo order by obj.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("codigoOrgao", ho.getCodigo().substring(0, 6));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        q.setParameter("vinculo", v);
        q.setMaxResults(1);
        try {
            return (VinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    private VinculoFP getContratosComLotacaoFuncionalNaUnidade(VinculoFP v, UnidadeOrganizacional uo) {
        String hql = " select new VinculoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from VinculoFP obj" +
            " inner join obj.lotacaoFuncionals lotacao" +
            "      where lotacao.unidadeOrganizacional = :unidade" +
            "        and :dataOperacao between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:dataOperacao) " +
            "        and obj = :vinculo order by obj.inicioVigencia desc";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);

        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("unidade", uo);
        q.setParameter("vinculo", v);

        try {
            return (VinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
