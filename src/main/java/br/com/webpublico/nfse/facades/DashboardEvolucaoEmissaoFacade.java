package br.com.webpublico.nfse.facades;


import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.FiltroEvolucaoEmissaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.RowEvolucaoEmissaoNfseDTO;
import com.beust.jcommander.internal.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class DashboardEvolucaoEmissaoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Future<List<RowEvolucaoEmissaoNfseDTO>> buscarInformacoes(FiltroEvolucaoEmissaoNfseDTO filtro) {
        List<RowEvolucaoEmissaoNfseDTO> retorno = Lists.newArrayList();
        String sql = "with competencia (data) as ( " +
            "    select to_date(to_char(:data_inicial, 'MM/yyyy'), 'MM/yyyy') " +
            "    from dual " +
            "    union all " +
            "    select add_months(competencia.data, 1) " +
            "    from competencia " +
            "    where add_months(competencia.data, 1) <= to_date(to_char(:data_final, 'MM/yyyy'), 'MM/yyyy')) " +
            "select extract(year from competencia.data) as ano, " +
            "       extract(month from competencia.data) as mes, " +
            "       (select count(1) " +
            "           from usuarioweb uw " +
            "        where uw.ACTIVATED = 1 " +
            "          and to_date(to_char(uw.created_date, 'MM/yyyy'), 'MM/yyyy') " +
            "            <= to_date(to_char(competencia.data, 'MM/yyyy'), 'MM/yyyy') " +
            "          and exists(select 1 " +
            "                     from USERNFSECADASTROECONOMICO uw_ce " +
            "                     where uw_ce.USUARIONFSE_ID = uw.id " +
            "                       and uw_ce.SITUACAO = :aprovado)) as quantidade_usuarios, " +
            "       (select count(1) " +
            "           from notafiscal nf inner join declaracaoprestacaoservico dec on nf.DECLARACAOPRESTACAOSERVICO_ID = dec.ID " +
            "        where dec.situacao != 'CANCELADA' and trunc(to_date(to_char(nf.emissao, 'MM/yyyy'), 'MM/yyyy')) " +
            "                  <= trunc(to_date(to_char(competencia.data, 'MM/yyyy'), 'MM/yyyy'))) as quantidade_notas " +
            "from competencia " +
            "order by 1, 2";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data_inicial", filtro.getDataInicial());
        q.setParameter("data_final", filtro.getDataFinal());
        q.setParameter("aprovado", UserNfseCadastroEconomico.Situacao.APROVADO.name());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                RowEvolucaoEmissaoNfseDTO row = new RowEvolucaoEmissaoNfseDTO();
                row.setAno(((Number) obj[0]).intValue());
                row.setMes(((Number) obj[1]).intValue());
                row.setQuantidadeUsuarios(((Number) obj[2]).longValue());
                row.setQuantidadeNotas(((Number) obj[3]).longValue());

                retorno.add(row);
            }
        }
        return new AsyncResult<>(retorno);
    }
}
