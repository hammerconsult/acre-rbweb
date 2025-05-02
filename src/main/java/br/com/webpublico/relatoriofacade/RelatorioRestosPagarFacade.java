package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioRestosAPagarItem;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 15/09/2014.
 */
@Stateless
public class RelatorioRestosPagarFacade extends RelatorioContabilSuperFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hoFacade;

    public List<RelatorioRestosAPagarItem> recuperarRelatorioRestosDespesa(Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" with item (conta, descricao, id_conta, superior, naoProcessadosPagos, canceladoNaoProcessados, processadosPagos, canceladoProcessados, inscritosProcessados, inscritosNaoProcessados) as (  ")
            .append(" select conta, ")
            .append("        descricao, ")
            .append("        id_conta, ")
            .append("        superior, ")
            .append("        coalesce(sum(naoProcessadosPagos), 0) as naoProcessadosPagos, ")
            .append("        coalesce(sum(canceladoNaoProcessados), 0) as canceladoNaoProcessados, ")
            .append("        coalesce(sum(processadosPagos), 0) as processadosPagos, ")
            .append("        coalesce(sum(canceladoProcessados), 0) as canceladoProcessados, ")
            .append("        coalesce(sum(inscritosProcessados), 0) as inscritosProcessados, ")
            .append("        coalesce(sum(inscritosProcessados), 0) as inscritosProcessados, ")
            .append("        coalesce(sum(inscritosNaoProcessados), 0) as inscritosNaoProcessados ")
            .append("        from (  ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        sum(coalesce(p.valor,0)) as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("        from pagamento p ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id  ")
            .append(" inner join empenho e on l.empenho_id = e.id  ")
            .append(" inner join despesaorc desp on e.despesaorc_id = desp.id  ")
            .append(" inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id ")
            .append(" inner join conta c on prov.contadedespesa_id = c.id  ")
            .append(" inner join exercicio ex on e.exercicio_id = ex.id  ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" where cast(p.datapagamento as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and cast(p.datapagamento as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(p.datapagamento as Date)) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(" and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id")
            .append(" union all ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao,  ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        sum(coalesce(es.valor,0)) * -1 as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, 0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" where cast(es.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and cast(es.dataestorno as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(es.dataestorno as Date)) ")
            .append(" and es.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS'")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(" and p.status <> 'ABERTO' ")
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" union all")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        sum(coalesce(p.valor,0)) as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from pagamento p ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id  ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" where cast(p.datapagamento as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and cast(p.datapagamento as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(p.datapagamento as Date)) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS'")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(" and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id")
            .append(" union all ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        sum(coalesce(es.valor,0)) * -1 as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" where cast(es.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and cast(es.dataestorno as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(es.dataestorno as Date))  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(" and p.status <> 'ABERTO' ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id ")
            .append(" union all ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, ")
            .append("        coalesce(sum(es.valor),0) as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados,  ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" where cast(es.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and cast(es.dataestorno as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(es.dataestorno as Date)) ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id ")
            .append(" union all  ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        coalesce(sum(es.valor),0) as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")

            .append(" inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append(" inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append(" inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append(" inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append(" inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append(" where cast(es.dataestorno as Date) between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and cast(es.dataestorno as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(es.dataestorno as Date)) ")
            .append(" and es.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" and not substr(c.codigo, 5,2) = '91' ")
            .append(" and ex.id = :exercicio ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" group by c.codigo, c.descricao, c.id, c.superior_id ) ")
            .append(" group by conta, descricao, id_conta, superior ")

                //TODO NaoProcessados
            .append(" union all  ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        0 as inscritosProcessados, ")
            .append("        coalesce(sum(e.valor),0) as inscritosNaoProcessados ")
            .append("   from empenho e on  ")
            .append("  inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append("  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append("  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append("  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")

            .append("  inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append("  inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append("  inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append("  inner join programappa prog on prog.id = a.programa_id ")
            .append("  inner join funcao f on f.id = a.funcao_id ")
            .append("  inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append("  inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append("  inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append("  inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append("  inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append("  where cast(e.dataempenho as Date) = to_date('01/01/'||ex.ano, 'dd/MM/yyyy') ")
            .append("    and cast(e.dataempenho as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(e.dataempenho as Date)) ")
            .append("    and e.categoriaorcamentaria = 'RESTO' ")
            .append("    and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append("    and not substr(c.codigo, 5,2) = '91' ")
            .append("    and ex.id = :exercicio ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append("  group by c.codigo, c.descricao, c.id, c.superior_id ) ")
            .append("  group by conta, descricao, id_conta, superior ")

                //TODO Processados
            .append(" union all  ")
            .append(" select c.codigo as conta, ")
            .append("        c.descricao as descricao, ")
            .append("        c.id as id_conta, ")
            .append("        c.superior_id as superior, ")
            .append("        0 as processadosPagos, ")
            .append("        0 as naoProcessadosPagos, ")
            .append("        0 as canceladoProcessados, ")
            .append("        0 as canceladoNaoProcessados, ")
            .append("        coalesce(sum(e.valor),0) as inscritosProcessados, ")
            .append("        0 as inscritosNaoProcessados ")
            .append("   from empenho e on  ")
            .append("  inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ")
            .append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "")
            .append("  INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append("  INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append("  INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append("  INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")

            .append("  inner join subacaoppa sub on sub.id = prov.subacaoppa_id ")
            .append("  inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append("  inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append("  inner join programappa prog on prog.id = a.programa_id ")
            .append("  inner join funcao f on f.id = a.funcao_id ")
            .append("  inner join subfuncao sf on sf.id = a.subfuncao_id ")
            .append("  inner join fontedespesaorc fontedesp on e.fontedespesaorc_id = fontedesp.id ")
            .append("  inner join provisaoppafonte ppf on fontedesp.provisaoppafonte_id = ppf.id ")
            .append("  inner join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id ")
            .append("  inner join fontederecursos fonte on fonte.id = cd.fontederecursos_id ")

            .append("  where cast(e.dataempenho as Date) = to_date('01/01/'||ex.ano, 'dd/MM/yyyy') ")
            .append("    and cast(e.dataempenho as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(e.dataempenho as Date)) ")
            .append("    and e.categoriaorcamentaria = 'RESTO' ")
            .append("    and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append("    and not substr(c.codigo, 5,2) = '91' ")
            .append("    and ex.id = :exercicio ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append("  group by c.codigo, c.descricao, c.id, c.superior_id ) ")
            .append("  group by conta, descricao, id_conta, superior ")


            .append(" union all ")
            .append(" select sup.codigo as conta, sup.descricao as descricao, sup.id as id_conta, sup.superior_id as superior, it.naoProcessadosPagos, it.canceladoNaoProcessados, it.processadosPagos, it.canceladoProcessados, it.inscritosProcessados, it.inscritosNaoProcessados ")
            .append(" from conta sup ")
            .append(" inner join item it on sup.id = it.superior ")
            .append(" ) select conta, ")
            .append("          trim(descricao) as descricao, ")
            .append("          nivelestrutura(conta, '.') as nivel, ")
            .append("          coalesce(sum(naoProcessadosPagos), 0) as naoProcessadosPagos, ")
            .append("          coalesce(sum(canceladoNaoProcessados), 0) as canceladoNaoProcessados, ")
            .append("          coalesce(sum(processadosPagos), 0) as processadosPagos, ")
            .append("          coalesce(sum(canceladoProcessados), 0) as canceladoProcessados, ")
            .append("          coalesce(sum(inscritosProcessados), 0) as inscritosProcessados, ")
            .append("          coalesce(sum(inscritosNaoProcessados), 0) as inscritosNaoProcessados ")
            .append("     from item it ")
            .append(" group by conta, descricao ")
            .append(" order by conta ");
        Query q = em.createNativeQuery(sql.toString());
        adicionaParametros(parametros, q);
        List<RelatorioRestosAPagarItem> retorno = new ArrayList<>();
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioRestosAPagarItem item = new RelatorioRestosAPagarItem();
                item.setCodigo((String) obj[0]);
                item.setDescricao((String) obj[1]);
                item.setNivel(((BigDecimal) obj[2]).intValue());
                item.setRestosNaoProcessadosPagos((BigDecimal) obj[3]);
                item.setRestosNaoProcessadosCancelados((BigDecimal) obj[4]);
                item.setRestosProcessadosPagos((BigDecimal) obj[5]);
                item.setRestosProcessadosCancelados((BigDecimal) obj[6]);
                item.setInscritosProcessados((BigDecimal) obj[7]);
                item.setInscritosNaoProcessados((BigDecimal) obj[8]);
//                item.set
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<RelatorioRestosAPagarItem> recuperaRelatorioRestosExcetoIntraFuncao(Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        List<RelatorioRestosAPagarItem> lista = relatorioFuncaoExcetoIntraSql(pesquisouUg, parametros);
        return lista;
    }

    private List<RelatorioRestosAPagarItem> relatorioFuncaoExcetoIntraSql(Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select codigo, descricao, id_funcao, coalesce(sum(naoProcessadosPagos), 0) as naoProcessadosPagos, coalesce(sum(canceladoNaoProcessados), 0) as canceladoNaoProcessados, coalesce(sum(processadosPagos), 0) as processadosPagos,  coalesce(sum(canceladoProcessados), 0) as canceladoProcessados from (  ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, sum(coalesce(p.valor,0)) as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p  ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id  ")
            .append(" inner join empenho e on l.empenho_id = e.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO  and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, sum(coalesce(es.valor,0)) * -1 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno) ")
            .append(" and es.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS'")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, sum(coalesce(p.valor,0)) as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' and p.status <> 'ABERTO'")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, sum(coalesce(es.valor,0)) * -1 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO'  ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, 0 as naoProcessadosPagos, coalesce(sum(es.valor),0) as canceladoProcessados, 0 as canceladoNaoProcessados  from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND  NOT SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all  ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, coalesce(sum(es.valor),0) as canceladoNaoProcessados from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND  NOT SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" group by f.codigo, f.descricao, f.id) ")
            .append(" group by CODIGO, descricao, id_funcao ")
            .append(" order by CODIGO ");
        Query q = em.createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RelatorioRestosAPagarItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioRestosAPagarItem item = new RelatorioRestosAPagarItem();
                item.setCodigo((String) obj[0]);
                item.setDescricao((String) obj[1]);
                item.setNivel(1);
                item.setRestosNaoProcessadosPagos((BigDecimal) obj[3]);
                item.setRestosNaoProcessadosCancelados((BigDecimal) obj[4]);
                item.setRestosProcessadosPagos((BigDecimal) obj[5]);
                item.setRestosProcessadosCancelados((BigDecimal) obj[6]);
                retorno.add(item);
                retorno.addAll(relatorioSubFuncaoExcetoIntraSql(pesquisouUg, parametros, ((BigDecimal) obj[2]).longValue()));
            }
            return retorno;
        }
    }

    private List<RelatorioRestosAPagarItem> relatorioSubFuncaoExcetoIntraSql(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select codigo, descricao, coalesce(sum(naoProcessadosPagos), 0) as naoProcessadosPagos, coalesce(sum(canceladoNaoProcessados), 0) as canceladoNaoProcessados, coalesce(sum(processadosPagos), 0) as processadosPagos,  coalesce(sum(canceladoProcessados), 0) as canceladoProcessados from (  ")
            .append(" select f.codigo||'.'||sf.codigo as codigo, sf.descricao as descricao,  sum(coalesce(p.valor,0)) as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p  ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id  ")
            .append(" inner join empenho e on l.empenho_id = e.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" AND f.id  = :FUNCAO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, sf.codigo, sf.descricao ")
            .append(" union all ")
            .append(" select f.codigo ||'.'|| sf.codigo as codigo, sf.descricao as descricao, sum(coalesce(es.valor,0)) * -1 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno) ")
            .append(" and es.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS'")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO ")
            .append(" AND f.id  = :FUNCAO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, sf.codigo, sf.descricao ")
            .append(" union all")
            .append(" select f.codigo ||'.'|| sf.codigo as codigo, sf.descricao as descricao, 0 as processadosPagos, sum(coalesce(p.valor,0)) as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS'")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO ")
            .append(" AND f.id  = :FUNCAO  and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, sf.codigo, sf.descricao ")
            .append(" union all ")
            .append(" select f.codigo ||'.'|| sf.codigo as codigo, sf.descricao as descricao, 0 as processadosPagos, sum(coalesce(es.valor,0)) * -1 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND  NOT  SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO'  ")
            .append(" AND f.id  = :FUNCAO ")
            .append(" group by f.codigo, sf.codigo, sf.descricao ")
            .append(" union all ")
            .append(" select f.codigo ||'.'|| sf.codigo as codigo, sf.descricao as descricao, 0 as processadosPagos, 0 as naoProcessadosPagos, coalesce(sum(es.valor),0) as canceladoProcessados, 0 as canceladoNaoProcessados  from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND  NOT SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" AND f.id  = :FUNCAO ")
            .append(" group by f.codigo, sf.codigo, sf.descricao ")
            .append(" union all  ")
            .append(" select f.codigo ||'.'|| sf.codigo as codigo, sf.descricao as descricao, 0 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, coalesce(sum(es.valor),0) as canceladoNaoProcessados from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND  NOT SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" AND f.id  = :FUNCAO ")
            .append(" group by f.codigo, sf.codigo, sf.descricao) ")
            .append(" group by CODIGO, descricao ")
            .append(" order by CODIGO ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("FUNCAO", id);
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RelatorioRestosAPagarItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioRestosAPagarItem item = new RelatorioRestosAPagarItem();
                item.setCodigo((String) obj[0]);
                item.setDescricao((String) obj[1]);
                item.setNivel(2);
                item.setRestosNaoProcessadosPagos((BigDecimal) obj[2]);
                item.setRestosNaoProcessadosCancelados((BigDecimal) obj[3]);
                item.setRestosProcessadosPagos((BigDecimal) obj[4]);
                item.setRestosProcessadosCancelados((BigDecimal) obj[5]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public List<RelatorioRestosAPagarItem> recuperaRelatorioRestosIntraFuncao(Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        List<RelatorioRestosAPagarItem> lista = relatorioFuncaoIntraSql(pesquisouUg, parametros);
        return lista;
    }

    private List<RelatorioRestosAPagarItem> relatorioFuncaoIntraSql(Boolean pesquisouUg, List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select coalesce(sum(naoProcessadosPagos), 0) as naoProcessadosPagos, coalesce(sum(canceladoNaoProcessados), 0) as canceladoNaoProcessados, coalesce(sum(processadosPagos), 0) as processadosPagos,  coalesce(sum(canceladoProcessados), 0) as canceladoProcessados from (  ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, sum(coalesce(p.valor,0)) as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p  ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id  ")
            .append(" inner join empenho e on l.empenho_id = e.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, sum(coalesce(es.valor,0)) * -1 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno) ")
            .append(" and es.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS'")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO  and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, sum(coalesce(p.valor,0)) as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamento p ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id ")
            .append(" inner join vwhierarquiaorcamentaria vw on p.unidadeorganizacional_id = vw.subordinada_id  ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where p.datapagamento between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(" and p.datapagamento between vw.iniciovigencia and coalesce(vw.fimvigencia, p.datapagamento) ")
            .append(" and p.categoriaorcamentaria = 'RESTO' ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS'")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91' ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO' ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, sum(coalesce(es.valor,0)) * -1 as naoProcessadosPagos, 0 as canceladoProcessados, 0 as canceladoNaoProcessados from pagamentoestorno es ")
            .append(" inner join pagamento p on es.pagamento_id= p.id ")
            .append(" inner join liquidacao l on p.liquidacao_id = l.id ")
            .append(" inner join empenho e on l.empenho_id = e.id ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on es.unidadeorganizacional_id = vw.subordinada_id   ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" where es.dataestorno between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND  to_date(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id  =  :EXERCICIO and p.status <> 'ABERTO'  ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, 0 as naoProcessadosPagos, coalesce(sum(es.valor),0) as canceladoProcessados, 0 as canceladoNaoProcessados  from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'PROCESSADOS' ")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" group by f.codigo, f.descricao, f.id ")
            .append(" union all  ")
            .append(" select f.codigo as codigo, f.descricao as descricao, f.id as id_funcao, 0 as processadosPagos, 0 as naoProcessadosPagos, 0 as canceladoProcessados, coalesce(sum(es.valor),0) as canceladoNaoProcessados from empenhoestorno es  ")
            .append(" inner join empenho e on es.empenho_id = e.id  ")
            .append(" inner join vwhierarquiaorcamentaria vw on e.unidadeorganizacional_id = vw.subordinada_id ");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 1) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " +
            " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :EXERCICIO " : "");
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == 2) {
                sql.append(" and " + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        sql.append(" INNER JOIN exercicio ex ON e.exercicio_id = ex.id  ")
            .append(" INNER JOIN DESPESAORC desp ON e.despesaorc_id = desp.ID  ")
            .append(" INNER JOIN provisaoppadespesa prov ON desp.provisaoppadespesa_id = prov.ID  ")
            .append(" INNER JOIN CONTA c ON prov.contadedespesa_id = c.ID  ")
            .append(" INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacaoppa_id ")
            .append(" INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id ")
            .append(" INNER JOIN FUNCAO F ON F.ID = A.funcao_id ")
            .append(" INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id ")
            .append(" where es.DATAESTORNO between to_date(:DATAINICIAL, 'dd/MM/yyyy') AND to_date(:DATAFINAL, 'dd/MM/yyyy')   ")
            .append(" and es.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, es.dataestorno)  ")
            .append(" and es.categoriaorcamentaria = 'RESTO'  ")
            .append(" and e.tiporestosprocessados = 'NAO_PROCESSADOS' ")
            .append(" AND SUBSTR(c.codigo, 5,2) = '91'  ")
            .append(" AND ex.id = :EXERCICIO ")
            .append(" group by f.codigo, f.descricao, f.id) ");
        Query q = em.createNativeQuery(sql.toString());
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<RelatorioRestosAPagarItem> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioRestosAPagarItem item = new RelatorioRestosAPagarItem();
                item.setRestosNaoProcessadosPagos((BigDecimal) obj[0]);
                item.setRestosNaoProcessadosCancelados((BigDecimal) obj[1]);
                item.setRestosProcessadosPagos((BigDecimal) obj[2]);
                item.setRestosProcessadosCancelados((BigDecimal) obj[3]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public HierarquiaOrganizacionalFacade getHoFacade() {
        return hoFacade;
    }
}
