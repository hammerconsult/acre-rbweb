package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FuncaoParametrosITBI;
import br.com.webpublico.entidades.ParametrosFuncionarios;
import br.com.webpublico.entidades.ParametrosITBI;
import br.com.webpublico.enums.TipoFuncaoParametrosITBI;
import com.beust.jcommander.internal.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FuncaoParametroITBIFacade extends AbstractFacade<FuncaoParametrosITBI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FuncaoParametroITBIFacade() {
        super(FuncaoParametrosITBI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ParametrosFuncionarios> buscarFuncionariosPorFuncao(ParametrosITBI parametro, TipoFuncaoParametrosITBI funcao, String parte) {
        String sql = " select param.* " +
            "   from parametrosfuncionarios param " +
            " inner join funcaoparametrositbi func on param.funcaoparametrositbi_id = func.id " +
            "  left join pessoafisica pf on pf.id = param.pessoa_id " +
            "  left join pessoajuridica pj on pj.id = param.pessoa_id " +
            " where func.funcao = :funcao " +
            "   and param.parametrosbce_id = :param " +
            "   and (coalesce(pf.nome, pj.razaosocial) like :parte or" +
            "        regexp_replace(coalesce(pf.cpf, pj.cnpj), '[^0-9]') like :parte) " +
            "   and (param.vigenciainicial <= sysdate and (param.vigenciafinal is null or param.vigenciafinal >= sysdate)) ";
        Query q = em.createNativeQuery(sql, ParametrosFuncionarios.class);
        q.setParameter("param", parametro.getId());
        q.setParameter("funcao", funcao.name());
        q.setParameter("parte", "%" + parte.replace(".", "").replaceAll("-", "") + "%");
        return q.getResultList();
    }

    public List<FuncaoParametrosITBI> buscarFuncoes(String parte) {
        String sql = " select funcao.* from funcaoparametrositbi funcao " +
            " where funcao.descricao like :parte ";

        Query q = em.createNativeQuery(sql, FuncaoParametrosITBI.class);
        q.setParameter("parte", "%" + parte.trim() + "%");

        List<FuncaoParametrosITBI> funcoes = q.getResultList();

        return funcoes != null ? funcoes : Lists.<FuncaoParametrosITBI>newArrayList();
    }
}
