package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ExecucaoOperacaoCreditoVo;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ConsultaExecucaoOperacaoCreditoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;

    public List<ExecucaoOperacaoCreditoVo> buscarOperacoes(Date dataInicial, Date dataFinal, String filtros) {
        String sql = " select numero || ' - ' || descricaodivida as divida, " +
            "      codigo || ' - ' || denominacao as programa, " +
            "      codigoUnidade || ' - ' || descricaoUnidade as unidade," +
            "      sum(coalesce(valor, 0)) as normais, " +
            "      sum(coalesce(valorresto, 0)) as restos, " +
            "      sum(coalesce(valor, 0)) + sum(coalesce(valorresto, 0)) as Total " +
            " from (  " +
            " select dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo as codigoUnidade, vw.descricao as descricaoUnidade,  sum(p.valor) as valor, 0 as valorresto " +
            " from pagamento p  " +
            " inner join liquidacao l on p.liquidacao_id = l.id  " +
            " inner join empenho e on l.empenho_id = e.id   " +
            " inner join despesaorc do on do.id = e.despesaorc_id  " +
            " inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id  " +
            " inner join subacaoppa sub on sub.id = ppd.subacaoppa_id  " +
            " inner join acaoppa ac on ac.id = sub.acaoppa_id  " +
            " inner join programappa pro on pro.id = ac.programa_id  " +
            " inner join dividapublica dv on dv.id = e.operacaodecredito_id  " +
            " inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = p.UNIDADEORGANIZACIONAL_ID  " +
            " inner join contadedestinacao cd on cd.id = e.contaDeDestinacao_id " +
            " inner join fontederecursos fonte on fonte.id = e.fonteDeRecursos_id " +
            " where trunc(p.datapagamento) BETWEEN to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy')  " +
            " and to_date(:dataFinal,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal,'dd/MM/yyyy'))  " +
            " and p.categoriaorcamentaria = :normal  " +
            filtros +
            " group by dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo, vw.descricao  " +
            "   " +
            " union all  " +
            "   " +
            " select dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo as codigoUnidade, vw.descricao as descricaoUnidade, sum(pe.valor) * -1 as valor, 0 as valorresto  " +
            " from pagamentoestorno pe  " +
            " inner join pagamento p on p.id = pe.pagamento_id  " +
            " inner join liquidacao l on p.liquidacao_id = l.id  " +
            " inner join empenho e on l.empenho_id = e.id   " +
            " inner join despesaorc do on do.id = e.despesaorc_id  " +
            " inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id  " +
            " inner join subacaoppa sub on sub.id = ppd.subacaoppa_id  " +
            " inner join acaoppa ac on ac.id = sub.acaoppa_id  " +
            " inner join programappa pro on pro.id = ac.programa_id  " +
            " inner join dividapublica dv on dv.id = e.operacaodecredito_id  " +
            " inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = p.UNIDADEORGANIZACIONAL_ID  " +
            " inner join contadedestinacao cd on cd.id = e.contaDeDestinacao_id " +
            " inner join fontederecursos fonte on fonte.id = e.fonteDeRecursos_id " +
            " where trunc(pe.dataestorno) BETWEEN to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy')  " +
            " and to_date(:dataFinal,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal,'dd/MM/yyyy'))  " +
            " and pe.categoriaorcamentaria = :normal  " +
            filtros +
            " GROUP by dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo, vw.descricao  " +
            "   " +
            " union all  " +
            "   " +
            " select dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo as codigoUnidade, vw.descricao as descricaoUnidade, 0 as valor, sum(p.valor) as valorresto   " +
            " from pagamento p  " +
            " inner join liquidacao l on p.liquidacao_id = l.id  " +
            " inner join empenho e on l.empenho_id = e.id   " +
            " inner join despesaorc do on do.id = e.despesaorc_id  " +
            " inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id  " +
            " inner join subacaoppa sub on sub.id = ppd.subacaoppa_id  " +
            " inner join acaoppa ac on ac.id = sub.acaoppa_id  " +
            " inner join programappa pro on pro.id = ac.programa_id  " +
            " inner join dividapublica dv on dv.id = e.operacaodecredito_id  " +
            " inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = p.UNIDADEORGANIZACIONAL_ID  " +
            " inner join contadedestinacao cd on cd.id = e.contaDeDestinacao_id " +
            " inner join fontederecursos fonte on fonte.id = e.fonteDeRecursos_id " +
            " where trunc(p.datapagamento) BETWEEN to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy')  " +
            " and to_date(:dataFinal,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal,'dd/MM/yyyy'))  " +
            " and p.categoriaorcamentaria = :resto " +
            filtros +
            " group by dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo, vw.descricao  " +
            "   " +
            " union all  " +
            "   " +
            " select dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo as codigoUnidade, vw.descricao as descricaoUnidade, 0 as valor,  sum(pe.valor) * -1 as valorresto   " +
            " from pagamentoestorno pe  " +
            " inner join pagamento p on p.id = pe.pagamento_id  " +
            " inner join liquidacao l on p.liquidacao_id = l.id  " +
            " inner join empenho e on l.empenho_id = e.id   " +
            " inner join despesaorc do on do.id = e.despesaorc_id  " +
            " inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id  " +
            " inner join subacaoppa sub on sub.id = ppd.subacaoppa_id  " +
            " inner join acaoppa ac on ac.id = sub.acaoppa_id  " +
            " inner join programappa pro on pro.id = ac.programa_id  " +
            " inner join dividapublica dv on dv.id = e.operacaodecredito_id  " +
            " inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = p.UNIDADEORGANIZACIONAL_ID  " +
            " inner join contadedestinacao cd on cd.id = e.contaDeDestinacao_id " +
            " inner join fontederecursos fonte on fonte.id = e.fonteDeRecursos_id " +
            " where trunc(pe.dataestorno) BETWEEN to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy')  " +
            " and to_date(:dataFinal,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal,'dd/MM/yyyy'))  " +
            " and pe.categoriaorcamentaria = :resto  " +
            filtros +
            " GROUP by dv.numero, dv.descricaodivida, pro.codigo, pro.denominacao, vw.codigo, vw.descricao  " +
            "  " +
            " ) GROUP by numero, descricaodivida, codigo, denominacao, codigoUnidade, descricaoUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("normal", CategoriaOrcamentaria.NORMAL.name());
        q.setParameter("resto", CategoriaOrcamentaria.RESTO.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        List<ExecucaoOperacaoCreditoVo> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ExecucaoOperacaoCreditoVo vo = new ExecucaoOperacaoCreditoVo();
                vo.setDivida((String) obj[0]);
                vo.setPrograma((String) obj[1]);
                vo.setUnidade((String) obj[2]);
                vo.setNormais((BigDecimal) obj[3]);
                vo.setRestos((BigDecimal) obj[4]);
                vo.setTotal((BigDecimal) obj[5]);
                retorno.add(vo);
            }
        }
        return retorno;
    }

    public List<Conta> buscarContasDeDestinacao(String filtro) {
        return contaFacade.listaFiltrandoDestinacaoDeRecursos(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<FonteDeRecursos> buscarFontesDeRecursos(String filtro) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<DividaPublica> buscarDividasPublicas(String filtro) {
        return dividaPublicaFacade.listaFiltrandoDividaPublica(filtro);
    }
}
