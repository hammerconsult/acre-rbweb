package br.com.webpublico.negocios.rh.relatorio;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class RelatorioServidoresEfetivosSemContriPrevFacade extends RelatorioContabilSuperFacade {

    @TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
    public List buscarDados() {
        String sql = "select distinct m.MATRICULA        as matricula, " +
            "                v.NUMERO                    as contrato, " +
            "                pf.NOME                     as nome, " +
            "                cg.DESCRICAO                as cargo, " +
            "                uo.DESCRICAO                as lotacao_funcional, " +
            "                af.OBSERVACAO               as descricao_afastamento " +
            "from vinculofp v " +
            "         inner join MATRICULAFP m on v.MATRICULAFP_ID = m.ID " +
            "         inner join UNIDADEORGANIZACIONAL uo on v.UNIDADEORGANIZACIONAL_ID = uo.ID " +
            "         inner join PESSOAFISICA pf on m.PESSOA_ID = pf.ID " +
            "         inner join fichafinanceirafp ficha on v.ID = ficha.VINCULOFP_ID " +
            "         inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "         inner join itemfichafinanceirafp item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +
            "         inner join contratofp c on v.ID = c.ID " +
            "         inner join CONTRATOFPCARGO cfp on c.ID = cfp.CONTRATOFP_ID " +
            "         inner join CARGO cg on c.CARGO_ID = cg.ID " +
            "         inner join AFASTAMENTO af on c.ID = af.CONTRATOFP_ID " +
            "         inner join MODALIDADECONTRATOFP moda on c.MODALIDADECONTRATOFP_ID = moda.ID " +
            "where folha.mes = case " +
            "                      when extract(month from folha.CALCULADAEM) = :mes and folha.TIPOFOLHADEPAGAMENTO=:tipoFolha and folha.EFETIVADAEM is null " +
            "                          then 6 " +
            "                      else 5 end " +
            "  and folha.ano = :ano " +
            "  and moda.codigo = :modalidade " +
            "  and v.id not in (select ff.VINCULOFP_ID " +
            "                   from fichafinanceirafp ff " +
            "                            inner join folhadepagamento f on ff.FOLHADEPAGAMENTO_ID = f.ID " +
            "                            inner join itemfichafinanceirafp it on ff.ID = it.FICHAFINANCEIRAFP_ID " +
            "                            inner join eventofp e on e.id = it.eventofp_id " +
            "                   where e.codigo = :evento " +
            "                     and f.id = folha.id)";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("mes", 7);
        q.setParameter("ano", 2020);
        q.setParameter("modalidade", 1);
        q.setParameter("evento", "898");
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        return q.getResultList();
    }

}
