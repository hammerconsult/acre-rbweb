package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by carlos on 28/07/15.
 */
@Stateless
public class LeiAcessoInformacaoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LeiAcessoInformacaoFacade() {
    }

    public List<LeiAcessoInformacao> recuperarInformacoes(Integer mes, Integer ano) {
        String sql = "select " +
                " null as id," +
                " folha.mes, " +
                " folha.ano, " +
                " folha.tipofolhadepagamento, " +
                " mat.id as matricula," +
                " uo.id as unidades, " +
                " c.id as contrato, " +
                " pf.id as pessoa," +
                " cargo.id as cargo," +
                " null as lotacao, " +
                "   (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id " +
                "        inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id " +
                "        where ficha.vinculofp_id = v.id and folha.ano = :ano and folha.mes = :mes and item.tipoeventofp = 'VANTAGEM') as valorBruto, " +
                " " +
                "   (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id " +
                "        inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id " +
                "        where ficha.vinculofp_id = v.id and folha.ano = :ano and folha.mes = :mes and item.tipoeventofp = 'DESCONTO') as valorDesconto, " +
                " " +
                " " +
                "   ((select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id " +
                "        inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id " +
                "        where ficha.vinculofp_id = v.id and folha.ano = :ano and folha.mes = :mes and item.tipoeventofp = 'VANTAGEM') " +
                "        - " +
                "   (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id " +
                "        inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id " +
                "        where ficha.vinculofp_id = v.id and folha.ano = :ano and folha.mes = :mes and item.tipoeventofp = 'DESCONTO') " +
                "   )as valorLiquido " +
                " " +
                " " +
                " from vinculofp v inner join matriculafp mat on mat.id = v.matriculafp_id " +
                " inner join contratofp c on c.id = v.id " +
                " inner join pessoafisica pf on pf.id = mat.pessoa_id " +
                " inner join cargo cargo on cargo.id = c.cargo_id " +
                " inner join FichaFinanceiraFP ficha on ficha.vinculofp_id = v.id " +
                " inner join folhadepagamento folha on folha.id  = ficha.folhadepagamento_id " +
                " inner join vwrhcontrato vwrh on vwrh.ID_CONTRATOFP = c.id " +
                " inner join unidadeorganizacional uo on vwrh.ID_UNIDADE_LOTADA = uo.id " +
                " where folha.mes = :mes " +
                " and folha.ano = :ano " +
                " and vwrh.FINALOTACAO IS NULL ";
//        if (unidadeOrganizacional != null) {
//            sql += " and uo.id = :unidadeOrganizacional ";
//        }
        sql += " order by pf.nome ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
//        if (unidadeOrganizacional != null) {
//            q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
//        }

        List resultList = q.getResultList();

        List<LeiAcessoInformacao> lista = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object o : resultList) {
                Object[] objeto = (Object[]) o;
                LeiAcessoInformacao leiAcessoInformacao = new LeiAcessoInformacao();
                leiAcessoInformacao.setMes(Mes.getMesToInt(Integer.parseInt(((BigDecimal) objeto[1]).toString())));
                leiAcessoInformacao.setAno(Integer.parseInt(((BigDecimal) objeto[2]).toString()));
                leiAcessoInformacao.setTipoFolha(objeto[3].toString());
                leiAcessoInformacao.setMatriculaFP((MatriculaFP) getValor(objeto, 4, MatriculaFP.class));
                leiAcessoInformacao.setUnidadeOrganizacional((UnidadeOrganizacional) getValor(objeto, 5, UnidadeOrganizacional.class));
                leiAcessoInformacao.setContratoFP((ContratoFP) getValor(objeto, 6, ContratoFP.class));
                leiAcessoInformacao.setPessoaFisica((PessoaFisica) getValor(objeto, 7, PessoaFisica.class));
                leiAcessoInformacao.setCargo((Cargo) getValor(objeto, 8, Cargo.class));
                leiAcessoInformacao.setSalarioBruto((BigDecimal) objeto[10]);
                leiAcessoInformacao.setDescontoSalario((BigDecimal) objeto[11]);
                leiAcessoInformacao.setSalarioLiquido((BigDecimal) objeto[12]);


                lista.add(leiAcessoInformacao);
            }
        }
        return lista;
    }

    public void removerLAI(Integer mes, Integer ano) {
        String sql = " delete from leiacessoinformacao" +
                " where mes = :mes " +
                " and ano = :ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", Mes.getMesToInt(mes).name());
        q.setParameter("ano", ano);
        q.executeUpdate();
    }

    public boolean verificaStatus(Integer mes, Integer ano) {
        String sql = " select c.statuscompetencia from competenciafp c " +
                " inner join exercicio e on e.id = c.exercicio_id" +
                " where c.statuscompetencia = 'EFETIVADA' " +
                " and c.mes = :mes " +
                " and e.ano = :ano ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return !q.getResultList().isEmpty();
    }

    private Object getValor(Object[] objeto, Integer posicao, Class classe) {
        return em.find(classe, ((BigDecimal) objeto[posicao]).longValue());
    }

    public void salvar(LeiAcessoInformacao leiAcessoInformacao) {
        if (leiAcessoInformacao.getId() == null) {
            em.persist(leiAcessoInformacao);
        } else {
            em.merge(leiAcessoInformacao);
        }
    }
}
