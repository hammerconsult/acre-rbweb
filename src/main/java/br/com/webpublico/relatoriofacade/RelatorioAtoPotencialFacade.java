package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioAtoPotencial;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.TipoAtoPotencial;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
import br.com.webpublico.util.UtilRelatorioContabil;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class RelatorioAtoPotencialFacade extends RelatorioContabilSuperFacade {

    public List<RelatorioAtoPotencial> buscarDados(List<ParametrosRelatorios> parametros, Boolean pesquisouUg, ApresentacaoRelatorio apresentacaoRelatorio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ap.data, ")
            .append("       ap.numero, ")
            .append("       ap.TIPOLANCAMENTO, ")
            .append("       ap.TIPOATOPOTENCIAL, ")
            .append("       ap.TIPOOPERACAOATOPOTENCIAL as operacao, ")
            .append("       formatacpfcnpj(coalesce(pf.cpf, pj.cnpj )) || ' - ' || coalesce(pf.nome, pj.razaosocial) as pessoa, ")
            .append("       case when classe.CODIGO is not null then classe.CODIGO || ' - ' || classe.DESCRICAO  else '' end as classe, ")
            .append("       case when ap.NUMEROREFERENCIA is not null and ap.EXERCICIOREFERENCIA_ID is not null ")
            .append("          then ap.numeroreferencia || '/' || exRef.ano else ap.numeroReferencia ")
            .append("       end as referencia, ")
            .append("       ap.historico, ")
            .append("       ap.VALOR, ")
            .append("       ev.codigo || ' - '  || ev.descricao as evento, ")
            .append("       vw.codigo || ' - ' || vw.descricao as unidade, ")
            .append("       vworg.codigo || ' - ' || vworg.descricao as orgao, ")
            .append(apresentacaoRelatorio.isApresentacaoUnidadeGestora() ? " ug.codigo || ' - ' || ug.descricao as unidade_gestora " : " '' as unidade_gestora ")
            .append("  from atopotencial ap ")
            .append("  left join CONVENIODESPESA cd on ap.CONVENIODESPESA_ID = cd.id ")
            .append("  left join entidadebeneficiaria eb on cd.ENTIDADEBENEFICIARIA_ID = eb.id ")
            .append("  left join CONVENIORECEITA cr on ap.CONVENIORECEITA_ID = cr.id ")
            .append("  left join contrato cont on ap.CONTRATO_ID = cont.id ")
            .append("  left join pessoa p on coalesce(eb.pessoaJuridica_ID, coalesce(cr.PESSOA_ID, cont.CONTRATADO_ID)) = p.id ")
            .append("  left join pessoafisica pf on p.id = pf.id ")
            .append("  left join pessoajuridica pj on p.id = pj.id ")
            .append("  left join CLASSECREDOR classe on classe.id = coalesce(cd.CLASSECREDOR_ID,  cr.CLASSECREDOR_ID) ")
            .append("  left join exercicio exRef on ap.EXERCICIOREFERENCIA_ID = exRef.id ")
            .append(" inner join eventocontabil ev on ap.EVENTOCONTABIL_ID = ev.id ")
            .append(buscarJoinVwUnidade("ap.UNIDADEORGANIZACIONAL_ID"))
            .append(buscarJoinVwOrgao())
            .append(buscarJoinUnidadeGestora(apresentacaoRelatorio.name(), pesquisouUg))
            .append(" where trunc(ap.data) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(getAndVigenciaVwUnidade("to_date(:DATAFINAL, 'dd/mm/yyyy')"))
            .append(getAndVigenciaVwOrgao("to_date(:DATAFINAL, 'dd/mm/yyyy')"))
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(" ORDER BY ")
            .append(apresentacaoRelatorio.isApresentacaoUnidadeGestora() ? " ug.codigo, " : "")
            .append(" vw.codigo, ap.NUMERO ");
        Query q = getEm().createNativeQuery(sql.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<Object[]> resultado = q.getResultList();
        List<RelatorioAtoPotencial> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioAtoPotencial relatorioAtoPotencial = new RelatorioAtoPotencial();
                relatorioAtoPotencial.setData((Date) obj[0]);
                relatorioAtoPotencial.setNumero((String) obj[1]);
                relatorioAtoPotencial.setTipoLancamento(TipoLancamento.valueOf((String) obj[2]).getDescricao());
                relatorioAtoPotencial.setTipoAtoPotencial(TipoAtoPotencial.valueOf((String) obj[3]).getDescricao());
                relatorioAtoPotencial.setOperacao(TipoOperacaoAtoPotencial.valueOf((String) obj[4]).getDescricao());
                relatorioAtoPotencial.setPessoa((String) obj[5]);
                relatorioAtoPotencial.setClasse((String) obj[6]);
                relatorioAtoPotencial.setNumeroDataReferencia((String) obj[7]);
                relatorioAtoPotencial.setHistorico((String) obj[8]);
                relatorioAtoPotencial.setValor((BigDecimal) obj[9]);
                relatorioAtoPotencial.setEventoContabil((String) obj[10]);
                relatorioAtoPotencial.setUnidade((String) obj[11]);
                relatorioAtoPotencial.setOrgao((String) obj[12]);
                relatorioAtoPotencial.setUnidadeGestora((String) obj[13]);
                retorno.add(relatorioAtoPotencial);
            }
        }
        return retorno;
    }
}
