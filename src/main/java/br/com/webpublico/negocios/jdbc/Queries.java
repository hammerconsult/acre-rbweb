package br.com.webpublico.negocios.jdbc;

/**
 * Created by venom on 23/02/15.
 */
public abstract class Queries {

    public static String insertParametroEvento() {
        String sql = "insert into parametroevento(id, dataevento, complementohistorico, classeorigem " +
                ", idorigem, complementoid, tipobalancete, eventocontabil_id, unidadeorganizacional_id) " +
                "values (?,?,?,?,?,?,?,?,?)";
        return sql;
    }

    public static String insertItemParametroEvento() {
        String sql = "insert into itemparametroevento(id, valor, tagvalor, operacaoclassecredor" +
                ", parametroevento_id) values (?,?,?,?,?)";
        return sql;
    }

    public static String selectSaldoContaContabil() {
        String sql = "select " +
                "  s.id as id " +
                "  , s.datasaldo as datasaldo " +
                "  , s.totalcredito as totalcredito " +
                "  , s.totaldebito as totaldebito " +
                "from saldocontacontabil s " +
                "where s.contacontabil_id = ? " +
                "and s.unidadeorganizacional_id = ? " +
                "and s.tipobalancete = ? " +
                "order by s.datasaldo";
        return sql;
    }

    public static String insertSaldoContaContabil() {
        String sql = "insert into saldocontacontabil(id, datasaldo, totalcredito, totaldebito " +
                ", tipobalancete, contacontabil_id, unidadeorganizacional_id) values (?,?,?,?,?,?,?)";
        return sql;
    }

    public static String updateSaldoContaContabil() {
        String sql = "update saldocontacontabil s set s.totalcredito = ?, s.totaldebito = ? where id = ?";
        return sql;
    }

    public static String selectOccGrupoMaterial() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.grupomaterial_id as grupomaterial " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occgrupomaterial occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccGrupoBem() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.grupobem_id as grupobem " +
                "  , occ.tipogrupo as tipogrupo " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occgrupobem occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccOrigemRecurso() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.origemsuplementacaoorc as origem " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occorigemrecurso occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccTipoPassivoAtuarial() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.tipopassivoatuarial_id as passivo " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occtipopassivoatuarial occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccNaturezaDividaPublica() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.categoriadividapublica_id as categoria " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occnaturezadividapublica occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccClassePessoa() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.classepessoa_id as classepessoa " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occclassepessoa occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy') " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccBanco() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.subconta_id as subconta " +
                "  , ori.tagocc_id as tagocc  " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occbanco occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.subconta_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy')  " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy')   " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectOccConta() {
        String sql = "select " +
                "  occ.id as id " +
                "  , occ.contaorigem_id as contaorigem " +
                "  , ori.tagocc_id as tagocc " +
                "  , ori.contacontabil_id as contacontabil " +
                "  , ori.reprocessar as reprocessar " +
                "  , ori.containtra_id as intra " +
                "  , ori.containteruniao_id as interuniao " +
                "  , ori.containterestado_id as interestado " +
                "  , ori.containtermunicipal_id as intermunicipal " +
                "  , ori.iniciovigencia as iniciovigencia " +
                "  , ori.fimvigencia as fimvigencia " +
                "from occconta occ " +
                "  inner join origemcontacontabil ori " +
                "    on occ.id = ori.id " +
                "where ori.tagocc_id = ? " +
                "and occ.contaorigem_id = ? " +
                "and ori.iniciovigencia >= to_date(?, 'dd/MM/yyyy') " +
                "and (ori.fimvigencia <= to_date(?, 'dd/MM/yyyy')  " +
                "or ori.fimvigencia is null)";
        return sql;
    }

    public static String selectConfigPagamento() {
        String sql = "select " +
                "  cp.id as id " +
                "  , cp.tipocontadespesa as tipocontadespesa " +
                "  , cf.iniciovigencia as iniciovigencia " +
                "  , cf.fimvigencia as fimvigencia " +
                "  , cf.eventocontabil_id as eventocontabil " +
                "from configpagamento cp " +
                "  inner join configuracaoevento cf " +
                "    on cp.id = cf.id " +
                "  inner join configpagamentocontadesp cpcd " +
                "    on cpcd.configpagamento_id = cp.id " +
                "where (to_date(?, 'dd/MM/yyyy') between cf.iniciovigencia " +
                "and coalesce(cf.fimvigencia, sysdate) or cf.fimvigencia is null) " +
                "and cp.tipocontadespesa = ? " +
                "and cf.tipolancamento = ? " +
                "and cpcd.contadespesa_id = ?";
        return sql;
    }

    public static String selectEventoContabil() {
        String sql = "select " +
                "  ev.id as id " +
                "  , ev.codigo as codigo " +
                "  , ev.descricao as descricao " +
                "  , ev.chave as chave " +
                "  , ev.tipobalancete as tipobalancete " +
                "  , ev.eventotce as eventotce " +
                "  , ev.tipoeventocontabil as tipoeventocontabil " +
                "  , ev.iniciovigencia as iniciovigencia " +
                "  , ev.fimvigencia as fimvigencia " +
                "  , ev.tipooperacaoconciliacao as tipooperacaoconciliacao " +
                "from eventocontabil ev " +
                "where ev.id = ?";
        return sql;
    }

    public static String selectCLP() {
        String sql = "select " +
                "  clp.id as id " +
                "  , clp.codigo as codigo " +
                "  , clp.descricao as descricao " +
                "  , clp.nome as nome " +
                "  , clp.iniciovigencia as iniciovigencia " +
                "  , clp.fimvigencia as fimvigencia " +
                "  , clp.exercicio_id as exercicioId " +
                "  , ex.ano as ano " +
                "from itemeventoclp item " +
                "  inner join clp clp " +
                "    on item.clp_id = clp.id " +
                "  inner join exercicio ex " +
                "    on clp.exercicio_id = ex.id " +
                "where item.tagvalor = ? " +
                "and item.eventocontabil_id = ? " +
                "and to_date(?, 'dd/MM/yyyy') " +
                "between clp.iniciovigencia " +
                "and coalesce(clp.fimvigencia, to_date(?, 'dd/MM/yyyy'))";
        return sql;
    }

    public static String selectLCP() {
        String sql = "select " +
                "  lcp.id as id " +
                "  , lcp.codigo as codigo " +
                "  , lcp.obrigatorio as obrigatorio " +
                "  , lcp.variacao as variacao " +
                "  , lcp.variaoperacao as variaoperacao " +
                "  , lcp.item as item " +
                "  , lcp.usointerno as usointerno " +
                "  , lcp.tipomovimentotcecredito as tipomovtcecredito " +
                "  , lcp.tipomovimentotcedebito as tipomovtcedebito " +
                "  , lcp.contacredito_id as contacredito " +
                "  , lcp.contadebito_id as contadebito " +
                "  , lcp.contacreditointra_id as contacreditointra " +
                "  , lcp.contacreditointeruniao_id as contacreditointeruniao " +
                "  , lcp.contacreditointerestado_id as contacreditointerestado " +
                "  , lcp.contacreditointermunicipal_id as contacreditointermunicipal " +
                "  , lcp.contadebitointra_id as contadebitointra " +
                "  , lcp.contadebitointeruniao_id as contadebitointeruniao " +
                "  , lcp.contadebitointerestado_id as contadebitointerestado " +
                "  , lcp.contadebitointermunicipal_id as contadebitointermunicipal " +
                "  , lcp.tagocccredito_id as tagocccredito " +
                "  , lcp.tagoccdebito_id as tagoccdebito " +
                "  , lcp.tipocontaauxiliarcredito_id as tipoauxiliarcredito " +
                "  , lcp.tipocontaauxiliardebito_id as tipoauxiliardebito " +
                "from lcp lcp " +
                "where lcp.clp_id = ?";
        return sql;
    }

    public static String selectTagOcc() {
        String sql = "select " +
                "  tag.id as id " +
                "  , tag.codigo as codigo " +
                "  , trim(tag.descricao) as descricao " +
                "  , tag.entidadeocc as entidadeocc " +
                "from tagocc tag";
        return sql;
    }

    public static String selectContaById() {
        String sql = "select " +
                "  con.id as id " +
                "  , con.codigo as codigo " +
                "  , con.superior_id as superior " +
                "  , pdc.id as planodecontasid " +
                "from conta con " +
                "  inner join planodecontas pdc " +
                "    on con.planodecontas_id = pdc.id " +
                "where con.id = ? ";
        return sql;
    }

    public static String selectContaByListId() {
        String sql = "select " +
                "  con.id as id " +
                "  , con.codigo as codigo " +
                "  , con.superior_id as superior " +
                "  , pdc.id as planodecontasid " +
                "from conta con " +
                "  inner join planodecontas pdc " +
                "    on con.planodecontas_id = pdc.id " +
                "where con.id in (?)";
        return sql;
    }

    public static String selectContaDespesa() {
        String sql = "select " +
                "  cd.id as id " +
                "  , con.codigo as conta " +
                "  , cd.tipocontadespesa as tipocontadespesa " +
                "  , cd.codigoreduzido as codigoreduzido " +
                "  , con.ativa as ativa " +
                "  , con.dataregistro as dataregistro " +
                "  , con.descricao as descricao " +
                "  , con.funcao as funcao " +
                "  , con.permitirdesdobramento as desdobramento " +
                "  , con.dtype as dtype " +
                "  , con.categoria as categoria " +
                "  , con.superior_id as superior " +
                "from contadespesa cd " +
                "  inner join conta con " +
                "    on cd.id = con.id " +
                "where cd.id = ?";
        return sql;
    }

    public static String selectContaAuxiliar() {
        String sql = "select " +
                "  ca.id as id " +
                "  , ca.contacontabil_id as contacontabil " +
                "  , con.codigo as codigo " +
                "  , trim(con.descricao) as descricao " +
                "from contaauxiliar ca " +
                "  inner join conta con " +
                "    on ca.id = con.id " +
                "  inner join exercicio ex " +
                "    on con.exercicio_id = ex.id " +
                "where con.codigo like ? " +
                "and con.planodecontas_id = ? " +
                "and ca.contacontabil_id = ? ";
        return sql;
    }

    public static String selectTipoContaAuxiliar() {
        String sql = "select " +
                "  tca.id as id " +
                "  , tca.codigo as codigo " +
                "  , trim(tca.descricao) as descricao " +
                "  , trim(tca.chave) as chave " +
                "  , trim(tca.mascara) as mascara " +
                "  , trim(tca.itens) as itens " +
                "from tipocontaauxiliar tca";
        return sql;
    }

    public static String selectSubconta() {
        String sql = "select " +
                " s.id as id " +
                " , s.codigo as codigo " +
                " , s.contabancariaentidade_id as contabancaria " +
                "from subconta s " +
                "where s.id = ? ";
        return sql;
    }

    public static String selectContaBancariaEntidade() {
        String sql = " select cbe.id as id " +
                " , cbe.numeroconta as numero " +
                " , cbe.digitoverificador as digito " +
                " , cbe.agencia_id as agencia " +
                "from contabancariaentidade cbe " +
                "where cbe.id = ? ";
        return sql;
    }

    public static String selectAgencia() {
        String sql = " select ag.id as id " +
                " , ag.nomeagencia as nome " +
                " , ag.numeroagencia as numero " +
                " , ag.digitoverificador as digito " +
                " , ag.banco_id as banco " +
                "from agencia ag " +
                "where ag.id = ? ";
        return sql;
    }

    public static String selectBanco() {
        String sql = " select b.id as id " +
                " , b.descricao as descricao " +
                " , b.numerobanco as numero " +
                "from banco b ";
        return sql;
    }

    public static String selectPlanoDeContasAuxiliar() {
        String sql = "select " +
                "  pdce.id as id " +
                "  , pdce.planoauxiliar_id as auxiliar " +
                "  , ex.id as idexercicio " +
                "from planodecontasexercicio pdce " +
                "  inner join exercicio ex " +
                "    on pdce.exercicio_id = ex.id " +
                "where ex.ano = ? ";
        return sql;
    }

    public static String insertPlanoDeContas() {
        String sql = "insert into planodecontas(id, dataregistro, descricao" +
                ", tipoconta_id, exercicio_id) values (?,?,?,?,?)";
        return sql;
    }

    public static String updatePlanoDeContasExercicioSetPlanoAuxiliarId() {
        String sql = " update planodecontasexercicio set planoauxiliar_id = ? where id = ? ";
        return sql;
    }

    public static String insertContaAuxiliar() {
        String sql = "insert into contaauxiliar (id, temp, contacontabil_id) values (?,?,?)";
        return sql;
    }

    public static String insertConta() {
        String sql = "insert into conta (ID, codigo, ativa, descricao, planodecontas_id" +
                ", superior_id, dtype, exercicio_id, dataregistro) values (?,?,?,?,?,?,?,?,?)";
        return sql;
    }

    public static String selectGruposMaterial() {
        String sql = "select " +
                "  gm.id as id " +
                "  , gm.codigo as codigo " +
                "  , trim(gm.descricao) as descricao " +
                "  , gm.superior_id as superior " +
                "  , gm.ativoinativo as ativo " +
                "from grupomaterial gm " +
                "order by gm.codigo";
        return sql;
    }

    public static String selectGruposBem() {
        String sql = "select " +
                "  gb.id as id " +
                "  , gb.codigo as codigo " +
                "  , gb.descricao as descricao " +
                "  , gb.superior_id as superior " +
                "  , gb.ativoinativo as ativo " +
                "  , gb.tipobem as tipobem " +
                "from grupobem gb " +
                "order by gb.codigo";
        return sql;
    }

    public static String selectTiposPassivoAtuarial() {
        String sql = "select " +
                "  tpa.id as id " +
                "  , tpa.codigo as codigo " +
                "  , trim(tpa.descricao) as descricao " +
                "from tipopassivoatuarial tpa";
        return sql;
    }

    public static String selectCategoriasDividaPublica() {
        String sql = "select " +
                "  cdp.id as id " +
                "  , cdp.codigo as codigo " +
                "  , trim(cdp.descricao) as descricao " +
                "  , cdp.superior_id as superior " +
                "  , cdp.naturezadividapublica as natureza " +
                "  , cdp.ativoinativo as ativo " +
                "from categoriadividapublica cdp " +
                "order by cdp.codigo ";
        return sql;
    }

    public static String selectClassesCredor() {
        String sql = "select " +
                "  cc.id as id " +
                "  , trim(cc.descricao) as descricao " +
                "  , cc.codigo as codigo " +
                "  , cc.tipoclassecredor as tipoclasse " +
                "  , cc.ativoinativo as ativo " +
                "from classecredor cc";
        return sql;
    }

    public static String selectExercicios() {
        String sql = "select " +
                "  ex.id as id " +
                "  , ex.ano as ano " +
                "  , ex.dataregistro as dataregistro " +
                "from exercicio ex";
        return sql;
    }

    public static String insertLancamentoContabil() {
        String sql = "insert into lancamentocontabil(id, datalancamento, valor, complementohistorico, numero " +
                ", contacredito_id, contadebito_id, clphistoricocontabil_id, unidadeorganizacional_id " +
                ", lcp_id, itemparametroevento_id, contaauxiliarcredito_id, contaauxiliardebito_id) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return sql;
    }
}
