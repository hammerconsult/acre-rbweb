/*
 * Codigo gerado automaticamente em Thu Aug 04 09:41:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;

@Service
public class VinculoFPLeitorFacade implements Serializable {

    @PersistenceContext
    protected transient EntityManager em;


    // ***************************************************************************
    // RECUPERAR INFORMAÇÕES DE UM DETERMINADO
    // ***************************************************************************
    public Object recuperarVinculo(String matricula, String numero, String classe) {
        String complemento = classe != null && classe.equals("VinculoFP")  ? "" : " where classe = '"+classe+"' ";

        Query q = em.createNativeQuery("select id, matricula, numero, nome, classe from(" +
                " select v.id        as id,                                  " +
                "        m.matricula as matricula,                           " +
                "        v.numero    as numero,                              " +
                "        p.nome      as nome,                                " +
                "        case when v_apo.id is not null then 'Aposentadoria' " +
                "             when v_bfp.id is not null then 'BeneficioFP'   " +
                "             when v_cfp.id is not null then 'ContratoFP'    " +
                "             when v_est.id is not null then 'Estagiario'    " +
                "             when v_pen.id is not null then 'Pensionista'   " +
                "             when v_ben.id is not null then 'Beneficiario'  " +
                "        end         as classe                               " +
                " from vinculofp v                                           " +
                " inner join matriculafp m       on m.id = v.matriculafp_id  " +
                " inner join pessoafisica p      on p.id = m.pessoa_id       " +
                " left  join aposentadoria v_apo on v.id = v_apo.id          " +
                " left  join beneficiofp v_bfp   on v.id = v_bfp.id          " +
                " left  join contratofp v_cfp    on v.id = v_cfp.id          " +
                " left  join estagiario v_est    on v.id = v_est.id          " +
                " left  join pensionista v_pen   on v.id = v_pen.id          " +
                " left  join beneficiario v_ben  on v.id = v_ben.id          " +
                " where m.matricula = :mat                                   " +
                " and   v.numero    = :numero                                " +
                " order by v.iniciovigencia desc)                            " +
                complemento);
        q.setParameter("mat", matricula);
        q.setParameter("numero", numero);
        q.setMaxResults(1);

        try {
            return q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // ***************************************************************************
    // VALIDAR A VIGÊNCIA DE UM VINCULO
    // ***************************************************************************
    public boolean isVinculoFPVigenteEmDataOperacao(Long idVinculo, Date dataOperacao) {
        String sql = "select v.id from VinculoFP v" +
                "      where :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia, :dataOperacao)" +
                "        and v.id = :id";
        Query q = em.createQuery(sql);
        q.setMaxResults(1);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("id", idVinculo);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    // ***************************************************************************
    // VERIFICAR SE O USUÁRIO POSSUI ACESSO A TODOS OS VINCULOS
    // ***************************************************************************
    public boolean isUsuarioComAcessoATodosOsVinculosFP(String login) {
        String sql = "select id from usuariosistema where login = :login and acessotodosvinculosrh = :acesso";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        q.setParameter("login", login);
        q.setParameter("acesso", true);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Object[] getInformacoesHierarquiaOrganizacional(Long idUnidadeOrganizacional, Date dataOperacao) {
        String sql = "select ho.id, ho.codigo, ho.nivelnaentidade from hierarquiaorganizacional ho where ho.subordinada_id = :uo_id" +
                "        and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia,:dataOperacao)" +
                "        and ho.tipoHierarquiaOrganizacional = :tipoHierarquia";
        Query q = em.createNativeQuery(sql);
        q.setParameter("uo_id", idUnidadeOrganizacional);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(1);

        try {
            return (Object[]) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    // ***************************************************************************
    // VALIDAR ACESSO DE UM USUÁRIO A UM VINCULO
    // ***************************************************************************
    public boolean usuarioPossuiAcessoAoVinculo(String login, Long idVinculo, Long idUnidadeOrganizacional, Date dataOperacao) {
        if (isUsuarioComAcessoATodosOsVinculosFP(login)) {
            return true;
        } else {
            Object[] ho = getInformacoesHierarquiaOrganizacional(idUnidadeOrganizacional, dataOperacao);
            if (ho == null) {
                return false;
            }

            if (Integer.parseInt("" + ho[2]) == 2) {
                return usuarioPossuiAcessoAoVinculoPorOrgao(idVinculo, ho, dataOperacao);
            } else {
                return usuarioPossuiAcessoAoVinculoPorUnidade(idVinculo, idUnidadeOrganizacional, dataOperacao);
            }
        }
    }

    private boolean usuarioPossuiAcessoAoVinculoPorOrgao(Long idVinculo, Object[] ho, Date dataOperacao) {
        if (Integer.parseInt("" + ho[2]) != 2) {
            return false;
        }

        String sql = "select v.id from vinculofp v " +
                " inner join lotacaofuncional lot on lot.vinculofp_id = v.id " +
                " inner join hierarquiaorganizacional ho on ho.subordinada_id = lot.unidadeorganizacional_id" +
                "      where substr(ho.codigo,1,6) = :codigoOrgao" +
                "        and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia,:dataOperacao)" +
                "        and :dataOperacao between lot.inicioVigencia and coalesce(lot.finalVigencia,:dataOperacao)" +
                "        and :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia, :dataOperacao)" +
                "        and ho.tipoHierarquiaOrganizacional = :tipoHierarquia" +
                "        and v.id = :vinculo order by v.inicioVigencia desc";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("codigoOrgao", ((String) ho[1]).substring(0, 6));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("vinculo", idVinculo);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    private boolean usuarioPossuiAcessoAoVinculoPorUnidade(Long idVinculo, Long idUnidadeOrganizacional, Date dataOperacao) {
        String sql = " select v.id from vinculofp v" +
                " inner join lotacaofuncional lot on lot.vinculofp_id = v.id " +
                "      where lot.unidadeorganizacional_id = :idUnidade" +
                "        and :dataOperacao between lot.inicioVigencia and coalesce(lot.finalVigencia,:dataOperacao) " +
                "        and :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia,:dataOperacao) " +
                "        and v.id = :idVinculo order by v.inicioVigencia desc";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);

        q.setParameter("idVinculo", idVinculo);
        q.setParameter("idUnidade", idUnidadeOrganizacional);
        q.setParameter("dataOperacao", dataOperacao);

        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }
}
