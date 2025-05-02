/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class GeracaoSagresFacade {

    @EJB
    private ContaFacade contaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    // 01
    public List listaRegistrosOrcamento(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select exLdo.ano as anoldo, loa.aprovacao as aprovacaoLoa, ato.numero, ");
        sql.append(" exAto.ano as anoato, ldo.aprovacao as aprovacaoLdo, ldo.versao as numeroLdo ");
        sql.append(" from loa  ");
        sql.append(" left join ldo on ldo.id = loa.ldo_id ");
        sql.append(" left join exercicio exLdo on exLdo.id = ldo.exercicio_id ");
        sql.append(" left join atolegal ato on ato.id = loa.atolegal_id ");
        sql.append(" left join exercicio exAto on exAto.id = ato.exercicio_id ");
        sql.append(" where exLdo.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 02
    public List listaRegistrosUnidadeOrcamentaria(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct uni.codigo as codigo, ent.nome, pf.cpf, ato.tipoatolegal, ent.codigotce ");
        sql.append(" from unidadeorganizacional uni ");
        sql.append(" inner join entidade ent on uni.entidade_id = ent.id ");
        sql.append(" inner join atolegal ato on ent.atolegal_id = ato.id ");
        sql.append(" inner join hierarquiaorganizacional ho on ho.subordinada_id = uni.id ");
        sql.append(" inner join gestorordenadorentidade geord on ent.id = geord.entidade_id");
        sql.append(" inner join contratofp cfp on geord.contrato_id = cfp.id");
        sql.append(" inner join vinculofp vfp on cfp.id = vfp.id");
        sql.append(" inner join matriculafp mfp on vfp.matriculafp_id = mfp.id");
        sql.append(" left join pessoafisica pf on pf.id = mfp.pessoa_id ");
        sql.append(" left join exercicio ex on ex.id = ho.exercicio_id ");
        sql.append(" where ent.ativa = 1 ");
        sql.append(" and ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 03
    public List listaRegistrosProgramas(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pro.codigo, pro.denominacao, pro.objetivo from programappa pro ");
        sql.append(" left join ppa on ppa.id = pro.ppa_id ");
        sql.append(" left join ldo on ldo.ppa_id = ppa.id ");
        sql.append(" left join exercicio ex on ex.id = ldo.exercicio_id ");
        sql.append(" where ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 04
    public List listaRegistrosAcoes(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ac.codigo, ac.descricao, tipo.codigo as codtipo from provisaoppaldo pldo ");
        sql.append(" inner join ldo ldo on ldo.id=pldo.ldo_id ");
        sql.append(" inner join subacaoppa sub on sub.id = pldo.subacaoppa_id ");
        sql.append(" inner join acaoppa ac on ac.id = sub.acaoppa_id ");
        sql.append(" inner join tipoacaoppa tipo on ac.tipoacaoppa_id = tipo.id ");
        sql.append(" inner join exercicio ex on ex.id = ldo.exercicio_id ");
        sql.append(" where ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 05
    public List listaRegistrosDotacao(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ex.ano as anoLoa, uni.codigo as codUni, fu.codigo as codFuncao, ");
        sql.append(" sf.codigo as codSubFuncao, pgp.codigo as codProg, ap.codigo as codAcao, substr(con.codigo, 1,1) as catEco, ");
        sql.append(" substr(con.codigo, 3,1) as natDesp, substr(con.codigo, 5,2) as modDesp, substr(con.codigo, 8,2) as elemDesp, ");
        sql.append(" loa.valordadespesa as valorLoa ");
        sql.append(" from provisaoppadespesa ppd ");
        sql.append(" inner join contadespesa cd on ppd.contadedespesa_id = cd.id ");
        sql.append(" inner join conta con on cd.id = con.id ");
        sql.append(" inner join subacaoppa sap on sap.id = ppd.subacaoppa_id  ");
        sql.append(" inner join acaoppa ap on sap.acaoppa_id = ap.id ");
        sql.append(" inner join unidadeorganizacional uni on ap.responsavel_id = uni.id ");
        sql.append(" inner join funcao fu on ap.funcao_id = fu.id ");
        sql.append(" inner join subfuncao sf on ap.subfuncao_id = sf.id ");
        sql.append(" inner join programappa pgp on ap.programa_id = pgp.id ");
        sql.append(" inner join ppa ppa on pgp.ppa_id = ppa.id ");
        sql.append(" inner join ldo ldo on ldo.ppa_id = ppa.id ");
        sql.append(" inner join exercicio ex on ldo.exercicio_id = ex.id ");
        sql.append(" inner join loa loa on loa.ldo_id = ldo.id ");
        sql.append(" where ppd.tipodespesaorc = 'ORCAMENTARIA' ");
        sql.append(" and ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 06
    public List listaRegistrosElencoContas(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, con.codigo, con.descricao as titulo, 0 as codigoReduzido, con.funcao as funcao, ");
        sql.append(" con.tipocontacontabil as natureza, ' ' as funcionamento, con.permitirdesdobramento as escriturada, ");
        sql.append(" 0 as sistemaContabil, tp.classedaconta as tipoconta, super.codigo as superior, con.id as idConta ");
        sql.append(" from conta con ");
        sql.append(" inner join planodecontas pdc on con.planodecontas_id = pdc.id ");
        sql.append(" inner join planodecontasexercicio pdce on pdc.id = pdce.planocontabil_id ");
        sql.append(" inner join exercicio ex on pdce.exercicio_id = ex.id ");
        sql.append(" inner join tipoconta tp on tp.id = pdc.tipoconta_id ");
        sql.append(" left join conta super on super.id = con.superior_id ");
        sql.append(" where ex.ano = :ano ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 07
    public List listaRegistrosCadastroContas() {
        StringBuilder sql = new StringBuilder();
        sql.append("select cce.numeroconta, ban.numerobanco, ag.numeroagencia, sc.descricao, con.codigo, cce.tipoContaBancaria ");
        sql.append(" from contabancariaentidade cce ");
        sql.append(" inner join subconta sc on sc.contabancariaentidade_id = cce.id ");
        sql.append(" inner join occbanco occb on occb.subconta_id = sc.id ");
        sql.append(" inner join origemcontacontabil occ on occ.id = occb.id ");
        sql.append(" inner join conta con on con.id = occ.contacontabil_id ");
        sql.append(" inner join agencia ag on ag.id = cce.agencia_id ");
        sql.append(" inner join banco ban on ban.id = ag.banco_id ");
        sql.append(" where cce.situacao = 'ATIVO'");
        Query q = em.createNativeQuery(sql.toString());
        return q.getResultList();
    }

    // 08
    public List listaRegistrosRelacionamentoReceitaOrcamentaria(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select rl.codigoreduzido, con.descricao, contacontabil.codigo as stn, con.codigo ");
        sql.append(" from receitaloa rl ");
        sql.append(" inner join conta con on con.id = rl.contadereceita_id ");
        sql.append(" inner join occconta occ on occ.contaorigem_id = con.id ");
        sql.append(" inner join origemcontacontabil org on org.id = occ.id ");
        sql.append(" inner join conta contacontabil on contacontabil.id = org.contacontabil_id ");
        sql.append(" inner join loa loa on loa.id = rl.loa_id ");
        sql.append(" inner join ldo ldo on ldo.id = loa.ldo_id ");
        sql.append(" inner join exercicio ex on ex.id = ldo.exercicio_id ");
        sql.append(" where ex.ano = :ano ");
        sql.append(" and org.tagocc_id = (select tag.id from tagocc tag where tag.descricao = 'Lançamento de receita') ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 09
    public List listaRegistrosRelacionamentoReceitaExtra() {
        StringBuilder sql = new StringBuilder();
        sql.append("select co.codigo as codigo, contacontabil.codigo as contacontabil ");
        sql.append(" from contaextraorcamentaria ex ");
        sql.append(" inner join conta co on co.id = ex.id ");
        sql.append(" inner join occconta occconta on occconta.contaorigem_id = ex.id ");
        sql.append(" inner join origemcontacontabil origem on origem.id = occconta.id ");
        sql.append(" inner join conta contacontabil on contacontabil.id = origem.contacontabil_id ");
        sql.append(" where ex.contaextraorcamentaria_id is null ");
        sql.append(" and origem.tagocc_id = (select tag.id from tagocc tag where tag.descricao = 'Lançamento de receita extra') ");
        sql.append(" and co.codigo like '1%'");
        Query q = em.createNativeQuery(sql.toString());
        return q.getResultList();
    }

    // 10
    public List listaRegistrosRelacionamentoDespesaExtra() {
        StringBuilder sql = new StringBuilder();
        sql.append("select co.codigo as codigo, contacontabil.codigo as contacontabil ");
        sql.append(" from contaextraorcamentaria ex ");
        sql.append(" inner join conta co on co.id = ex.id ");
        sql.append(" inner join occconta occconta on occconta.contaorigem_id = ex.id ");
        sql.append(" inner join origemcontacontabil origem on origem.id = occconta.id ");
        sql.append(" inner join conta contacontabil on contacontabil.id = origem.contacontabil_id ");
        sql.append(" where ex.contaextraorcamentaria_id is null ");
        sql.append(" and origem.tagocc_id = (select tag.id from tagocc tag where tag.descricao = 'Lançamento de despesa extra') ");
        sql.append(" and co.codigo like '2%'");
        Query q = em.createNativeQuery(sql.toString());
        return q.getResultList();
    }

    // 11
    public List listaRegistrosPrevisaoReceita(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, rloa.codigoreduzido, rloa.valor from receitaloa rloa ");
        sql.append(" inner join loa loa on loa.id = rloa.loa_id ");
        sql.append(" inner join ldo ldo on ldo.id = loa.ldo_id ");
        sql.append(" inner join exercicio ex on ex.id = ldo.exercicio_id ");
        sql.append(" where ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 12
    public List listaRegistrosAtualizacaoOrcamentaria(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, fun.codigo as funcao, sfun.codigo as subfuncao, pro.codigo as programa, ");
        sql.append(" appa.codigo as acao, ato.numero as numeroDecreto, exato.ano as anoAto, sorc.origemsuplemtacao as tipoalteracao, ");
        sql.append(" substr(con.codigo, 1,1) as catEco, substr(con.codigo, 3,1) as natDesp, substr(con.codigo, 5,2) as modDesp, ");
        sql.append(" substr(con.codigo, 8,2) as elemDesp, sorc.valor ");
        sql.append(" from alteracaoOrc aorc ");
        sql.append(" inner join SuplementacaoORC sorc on sorc.alteracaoorc_id = aorc.id ");
        sql.append(" inner join FonteDespesaORC fdorc on fdorc.id = sorc.fontedespesaorc_id ");
        sql.append(" inner join DespesaORC dorc on dorc.id = fdorc.despesaorc_id ");
        sql.append(" inner join ProvisaoPPADespesa pppad on pppad.id = dorc.provisaoppadespesa_id ");
        sql.append(" inner join SubAcaoPPA sappa on sappa.id = pppad.subacaoppa_id  ");
        sql.append(" inner join AcaoPPA appa on appa.id = sappa.acaoppa_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = appa.responsavel_id ");
        sql.append(" inner join Funcao fun on fun.id = appa.funcao_id ");
        sql.append(" inner join SubFuncao sfun on sfun.id = appa.subfuncao_id ");
        sql.append(" inner join ProgramaPPA pro on pro.id = appa.programa_id ");
        sql.append(" inner join conta con on con.id = pppad.contadedespesa_id ");
        sql.append(" inner join exercicio ex on ex.id = dorc.exercicio_id ");
        sql.append(" inner join atolegal ato on ato.id = aorc.atolegal_id ");
        sql.append(" inner join exercicio exAto on exAto.id = ato.exercicio_id ");
        sql.append(" where ex.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 13
    public List listaRegistrosDecretos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano as anoDecreto, ato.numero as numeroDecreto, atolei.numero as numeroLei, ");
        sql.append(" exlei.ano as anoLei, ato.datapromulgacao ");
        sql.append(" from atolegal ato ");
        sql.append(" inner join exercicio ex on ex.id = ato.exercicio_id ");
        sql.append(" inner join atolegal atoLei on atoLei.id = ato.superior_id ");
        sql.append(" inner join exercicio exLei on exlei.id = atolei.exercicio_id ");
        sql.append(" where ato.tipoatolegal = 'DECRETO' and ato.propositoatolegal = 'ALTERACAO_ORCAMENTARIA' ");
        sql.append(" and ex.ano = :ano ");
        sql.append(" and extract(MONTH from ato.datapromulgacao) = :mes");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 14
    public List listaRegistrosEmpenhos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select exEmp.ano as anoEmpenho, uni.codigo as codUnidade, fun.codigo as codFuncao, sfun.codigo as codSubFuncao, ");
        sql.append(" pro.codigo as codPrograma, appa.codigo as codAcao, substr(con.codigo, 1,1) as catEco, substr(con.codigo, 3,1) as natDesp, ");
        sql.append(" substr(con.codigo, 5,2) as modDesp, substr(con.codigo, 8,2) as elemDesp, substr(con.codigo, 11,2) as subElemDesp, ");
        sql.append(" lic.ModalidadeLicitacao, lic.numeroLicitacao, emp.numero as numEmpenho, emp.tipoempenho, emp.dataempenho, emp.valor, ");
        sql.append(" emp.complementohistorico, coalesce(pesf.cpf, pesj.cnpj) as cpfcnpjpes, lic.numero as numeroProLic, '' as TipoFonte, '' as cpfOrdenador ");
        sql.append(" from empenho emp ");
        sql.append(" inner join exercicio exEmp on exEmp.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join DespesaORC dorc on dorc.id = emp.despesaorc_id ");
        sql.append(" inner join ProvisaoPPADespesa pppad on pppad.id = dorc.provisaoppadespesa_id ");
        sql.append(" inner join SubAcaoPPA sappa on sappa.id = pppad.subacaoppa_id  ");
        sql.append(" inner join AcaoPPA appa on appa.id = sappa.acaoppa_id ");
        sql.append(" inner join unidadeorganizacional uniAcaoPPA on uni.id = appa.responsavel_id ");
        sql.append(" inner join Funcao fun on fun.id = appa.funcao_id ");
        sql.append(" inner join SubFuncao sfun on sfun.id = appa.subfuncao_id ");
        sql.append(" inner join ProgramaPPA pro on pro.id = appa.programa_id ");
        sql.append(" inner join conta con on con.id = pppad.contadedespesa_id ");
        sql.append(" left join pessoafisica pesf on pesf.id = emp.fornecedor_id ");
        sql.append(" left join pessoajuridica pesj on pesj.id = emp.fornecedor_id ");
        sql.append(" left join licitacao lic on lic.id = emp.licitacao_id ");
        sql.append(" where exEmp.ano = :ano ");
        sql.append(" and extract(MONTH from emp.dataempenho) = :mes");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 15
    public List listaRegistrosEmpenhoEstorno(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as codUnidade, emp.numero as numeroEmpenho, empest.numero as numeroEstorno, empest.dataestorno, ");
        sql.append(" empest.valor, empest.complementohistorico ");
        sql.append(" from empenhoestorno empest ");
        sql.append(" inner join empenho emp on emp.id = empest.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where ex.ano = :ano ");
        sql.append(" and extract(MONTH from empest.dataestorno) = :mes");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 16
    public List listaRegistrosLiquidacao(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as codUnidade, emp.numero as numEmpenho, liq.numero as numLiquidacao, ");
        sql.append(" liq.dataliquidacao, liq.valor, tpdocto.codigo as codTpDocto, docto.numero as numDocto, docto.serie as serieDocto, uf.uf ");
        sql.append(" from liquidacao liq ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = liq.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id ");
        sql.append(" inner join doctofiscalliquidacao docto on docto.id = ldf.doctofiscalliquidacao_id ");
        sql.append(" inner join tipodocumentofiscal tpdocto on tpdocto.id = docto.tipodocumentofiscal_id ");
        sql.append(" left join uf uf on uf.id = docto.uf_id ");
        sql.append(" where ex.ano = :ano ");
        sql.append(" and extract(MONTH from liq.dataliquidacao) = :mes");
        sql.append(" and liq.categoriaOrcamentaria = 'NORMAL'");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 17
    public List listaRegistrosPagamentos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select exEmp.ano as anoPagamento, uni.codigo as codUnidade, emp.numero as numEmpenho, pag.numeropagamento as numPagamento, ");
        sql.append(" pag.datapagamento, pag.valor, conban.numeroconta, pag.numDocumento, pag.tipodocpagto, ban.numerobanco, ");
        sql.append(" agPes.numeroAgencia as numeroAgPes, ccban.numeroConta as numeroContaPes, '1' as origemRecursos, ccban.modalidadeConta ");
        sql.append(" from pagamento pag ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio exEmp on exEmp.id = emp.exercicio_id ");
        sql.append(" inner join exercicio exPag on exPag.id = pag.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join subconta subc on subc.id = pag.subconta_id ");
        sql.append(" inner join contabancariaentidade conban on conban.id = subc.contabancariaentidade_id ");
        sql.append(" inner join agencia ag on ag.id = conban.agencia_id ");
        sql.append(" inner join banco ban on ban.id = ag.banco_id ");
        sql.append(" inner join pessoa pes on pes.id = emp.fornecedor_id ");
        sql.append(" left join contaCorrenteBancPessoa ccbanpes on ccbanpes.pessoa_id = pes.id ");
        sql.append(" left join contacorrentebancaria ccban on ccban.id = ccbanpes.contaCorrenteBancaria_id ");
        sql.append(" left join agencia agPes on agPes.id = ccban.agencia_id ");
        sql.append(" left join banco banPes on banPes.id = agPes.banco_id ");
        sql.append(" where pag.status = 'PAGO' ");
        sql.append(" and expag.ano = :ano ");
        sql.append(" and extract(MONTH from pag.datapagamento) = :mes");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 18
    public List listaRegistrosRetencao(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select exEmp.ano as anoPagamento, uni.codigo as codUnidade, emp.numero as numEmpenho, ");
        sql.append(" pag.numeropagamento as numPagamento, rpag.valor, '1' as tipoRetencao ");
        sql.append(" from retencaopgto rpag ");
        sql.append(" inner join pagamento pag on pag.id = rpag.pagamento_id ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio exPag on exPag.id = pag.exercicio_id ");
        sql.append(" inner join exercicio exEmp on exEmp.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where expag.ano = :ano ");
        sql.append(" and extract(MONTH from rpag.dataretencao) = :mes");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 19
    public List listaRegistrosReceitaOrcamentaria(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select rl.codigoreduzido, lro.tipoOperacao, lro.valor ");
        sql.append(" from lancamentoreceitaorc lro ");
        sql.append(" inner join receitaloa rl on rl.id = lro.receitaloa_id ");
        sql.append(" inner join conta con on con.id = rl.contadereceita_id ");
        sql.append(" where extract(month from lro.datalancamento) = :mes ");
        sql.append(" and extract(year from lro.datalancamento) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 20
    public List listaRegistrosReceitaExtra(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select con.codigo, re.valor ");
        sql.append(" from receitaextra re ");
        sql.append(" inner join conta con on con.id = re.contaextraorcamentaria_id ");
        sql.append(" where extract(month from re.dataReceita) = :mes ");
        sql.append(" and extract(year from re.dataReceita) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 21
    public List listaRegistrosDespesaExtra(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select con.codigo, pe.valor ");
        sql.append(" from pagamentoextra pe ");
        sql.append(" inner join conta con on con.id = pe.contaextraorcamentaria_id ");
        sql.append(" where extract(month from pe.dataPagto) = :mes ");
        sql.append(" and extract(year from pe.dataPagto) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 22
    public List listaRegistrosRestosInscritos(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select exEmp.ano as anoEmpenho, uni.codigo as codUnidade, fun.codigo as codFuncao, sfun.codigo as codSubFuncao, ");
        sql.append(" pro.codigo as codPrograma, appa.codigo as codAcao, substr(con.codigo, 1,1) as catEco, substr(con.codigo, 3,1) as natDesp, ");
        sql.append(" substr(con.codigo, 5,2) as modDesp, substr(con.codigo, 8,2) as elemDesp, substr(con.codigo, 11,2) as subElemDesp, ");
        sql.append(" lic.ModalidadeLicitacao, lic.numeroLicitacao, emp.numero as numEmpenho, emp.tipoempenho, emp.dataempenho, emp.valor, ");
        sql.append(" emp.complementohistorico, coalesce(pesf.cpf, pesj.cnpj) as cpfcnpjpes, '' as TipoFonte, 0 as valorProc, 0 as valorNaoProc, '' as cpfOrdenador ");
        sql.append(" from empenho emp ");
        sql.append(" inner join exercicio exEmp on exEmp.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join DespesaORC dorc on dorc.id = emp.despesaorc_id ");
        sql.append(" inner join ProvisaoPPADespesa pppad on pppad.id = dorc.provisaoppadespesa_id ");
        sql.append(" inner join SubAcaoPPA sappa on sappa.id = pppad.subacaoppa_id ");
        sql.append(" inner join AcaoPPA appa on appa.id = sappa.acaoppa_id ");
        sql.append(" inner join unidadeorganizacional uniAcaoPPa on uni.id = appa.responsavel_id ");
        sql.append(" inner join Funcao fun on fun.id = appa.funcao_id ");
        sql.append(" inner join SubFuncao sfun on sfun.id = appa.subfuncao_id ");
        sql.append(" inner join ProgramaPPA pro on pro.id = appa.programa_id ");
        sql.append(" inner join conta con on con.id = pppad.contadedespesa_id ");
        sql.append(" left join pessoafisica pesf on pesf.id = emp.fornecedor_id ");
        sql.append(" left join pessoajuridica pesj on pesj.id = emp.fornecedor_id ");
        sql.append(" left join licitacao lic on lic.id = emp.licitacao_id ");
        sql.append(" where emp.categoriaOrcamentaria = 'RESTO' ");
        sql.append(" and exEmp.ano = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 23
    public List listaRegistrosEstornoRestos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numEmpenho, est.numero as numEstorno, ");
        sql.append(" est.dataestorno, est.valor, est.complementohistorico ");
        sql.append(" from empenhoestorno est ");
        sql.append(" inner join empenho emp on est.empenho_id = emp.id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where est.categoriaorcamentaria = 'RESTO' ");
        sql.append(" and extract(month from est.dataestorno) = :mes ");
        sql.append(" and extract(year from est.dataestorno) = :ano ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 24
    public List listaRegistrosPagamentosRestos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numEmpenho, pag.numeropagamento, pag.datapagamento, ");
        sql.append(" pag.valor, conb.numeroconta, '' as numCheque, '' as numDoc, ban.numerobanco, ag.numeroagencia, conb.numeroconta as numconta, ");
        sql.append(" fon.codigo as fonte, '' as tipoconta ");
        sql.append(" from pagamento pag ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join subconta scon on scon.id = pag.subconta_id ");
        sql.append(" inner join contabancariaentidade conb on conb.id = scon.contabancariaentidade_id ");
        sql.append(" inner join agencia ag on ag.id = conb.agencia_id ");
        sql.append(" inner join banco ban on ban.id = ag.banco_id ");
        sql.append(" inner join fontederecursos fon on fon.id = conb.fontederecursos_id ");
        sql.append(" where pag.categoriaorcamentaria = 'RESTO' ");
        sql.append(" and extract(month from pag.datapagamento) = :mes ");
        sql.append(" and extract(year from pag.datapagamento) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 25
    public List listaRegistrosRetencaoRestos(Integer ano, Integer mes) {
        // ver o tipo da retenção com o valdir
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as empenho, pag.numeropagamento as pagamento, ");
        sql.append(" rpag.valor, tr.codigo as tiporetencao ");
        sql.append(" from retencaopgto rpag ");
        sql.append(" inner join pagamento pag on pag.id = rpag.pagamento_id ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join contaextraorcamentaria cextra on cextra.id = rpag.contaextraorcamentaria_id ");
        sql.append(" left join tiporetencao tr on tr.id = cextra.tiporetencao_id ");
        sql.append(" where pag.categoriaorcamentaria = 'RESTO' ");
        sql.append(" and extract(month from rpag.dataretencao) = :mes ");
        sql.append(" and extract(year from rpag.dataretencao) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 26
    public List listaRegistrosConciliacaoBancaria(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select con.numeroconta, rownum as sequencia, 1 as formaConciliacao, pag.complementohistorico, ");
        sql.append(" pag.datapagamento, 0 as cheque, 0 as docDebAutomatico, pag.valor, con.tipocontabancaria ");
        sql.append(" from pagamento pag ");
        sql.append(" inner join subconta scon on scon.id = pag.subconta_id ");
        sql.append(" inner join contabancariaentidade con on con.id = scon.contabancariaentidade_id ");
        sql.append(" where pag.status = 'PAGO' ");
        sql.append(" and extract(month from pag.datapagamento) = :mes ");
        sql.append(" and extract(year from pag.datapagamento) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 27
    public List listaRegistrosSaldoInicial(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select con1.numeroconta, saldo1.valor, con1.tipoContaBancaria ");
        sql.append(" from contabancariaentidade con1 ");
        sql.append(" inner join subconta sc1 on sc1.contabancariaentidade_id = con1.id ");
        sql.append(" inner join saldosubconta saldo1 on saldo1.subconta_id = sc1.id ");
        sql.append(" where con1.situacao = 'ATIVO' ");
        sql.append(" and saldo1.datasaldo = (select min(saldo.datasaldo) ");
        sql.append(" from contabancariaentidade con ");
        sql.append(" inner join subconta sc on sc.contabancariaentidade_id = con.id ");
        sql.append(" inner join saldosubconta saldo on saldo.subconta_id = sc.id ");
        sql.append(" where con.situacao = 'ATIVO' ");
        sql.append(" and con.id = con1.id ");
        sql.append(" and extract(year from saldo.datasaldo) = :ano) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 28
    public List listaRegistrosSaldoMensal(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select con1.numeroconta, saldo1.valor, con1.tipoContaBancaria ");
        sql.append(" from contabancariaentidade con1 ");
        sql.append(" inner join subconta sc1 on sc1.contabancariaentidade_id = con1.id ");
        sql.append(" inner join saldosubconta saldo1 on saldo1.subconta_id = sc1.id ");
        sql.append(" where con1.situacao = 'ATIVO' ");
        sql.append(" and saldo1.datasaldo = (select min(saldo.datasaldo) ");
        sql.append(" from contabancariaentidade con ");
        sql.append(" inner join subconta sc on sc.contabancariaentidade_id = con.id ");
        sql.append(" inner join saldosubconta saldo on saldo.subconta_id = sc.id ");
        sql.append(" where con.situacao = 'ATIVO' ");
        sql.append(" and con.id = con1.id ");
        sql.append(" and extract(year from saldo.datasaldo) = :ano ");
        sql.append(" and extract(month from saldo.datasaldo) = :mes) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 29
    public List listaRegistrosLancamentoContabil(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select lanc.numero, lanc.datalancamento, evento.tipoLancamento, evento.tipobalancete, lanc.complementohistorico ");
        sql.append(" from lancamentocontabil lanc ");
        sql.append(" inner join itemparametroevento item on lanc.itemparametroevento_id = item.id ");
        sql.append(" inner join parametroevento par on item.parametroevento_id = par.id ");
        sql.append(" inner join eventocontabil evento on par.eventocontabil_id = evento.id ");
        sql.append(" where extract(month from lanc.datalancamento) = :mes ");
        sql.append(" and extract(year from lanc.datalancamento) = :ano ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 30
    public List listaRegistrosLancamentoContabilItem(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select lanc.numero, contacredito.codigo, '2' as tipo, lanc.valor ");
        sql.append(" from lancamentocontabil lanc ");
        sql.append(" inner join conta contaCredito on lanc.contacredito_id = contacredito.id ");
        sql.append(" where extract(month from lanc.datalancamento) = :mes ");
        sql.append(" and extract(year from lanc.datalancamento) = :ano ");
        sql.append(" UNION ALL ");
        sql.append(" select lanc.numero, contadebito.codigo, '1' as tipo, lanc.valor ");
        sql.append(" from lancamentocontabil lanc ");
        sql.append(" inner join conta contaDebito on lanc.contadebito_id = contadebito.id ");
        sql.append(" where extract(month from lanc.datalancamento) = :mes ");
        sql.append(" and extract(year from lanc.datalancamento) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 31
    public List listaRegistrosFornecedores(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select coalesce(pf.cpf, pj.cnpj) as documento, coalesce(pf.nome, pj.razaosocial) as nome, ");
        sql.append(" case when pf.id <> null then 2 else 1 end as tipo, uf.uf, ende.localidade ");
        sql.append(" from pessoa p ");
        sql.append(" inner join pessoa_perfil pp on pp.id_pessoa = p.id ");
        sql.append(" left join pessoafisica pf on pf.id = p.id ");
        sql.append(" left join pessoajuridica pj on pj.id = p.id ");
        sql.append(" left join pessoa_enderecocorreio pec on pec.pessoa_id = p.id ");
        sql.append(" left join enderecocorreio ende on ende.id = pec.enderecoscorreio_id ");
        sql.append(" left join uf uf on uf.nome = ende.uf ");
        sql.append(" where pp.perfil = 'PERFIL_CREDOR' ");
        Query q = em.createNativeQuery(sql.toString());
        //q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 32
    public List listaRegistrosExcessoArrecadacao(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ao.dataalteracao, rl.codigoreduzido, rao.valor from alteracaoorc ao ");
        sql.append(" inner join receitaalteracaoorc rao on rao.alteracaoorc_id = ao.id ");
        sql.append(" inner join receitaloa rl on rl.id = rao.receitaloa_id ");
        sql.append(" where rao.origemreceitaalteracaoorc = 'EPERACAO_EXCESSO' ");
        sql.append(" and extract(month from ao.dataalteracao) = :mes ");
        sql.append(" and extract(year from ao.dataalteracao) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 33
    public List listaRegistrosLiquidacaoRestosEstorno(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numeroEmpenho, le.numero as numeroEstorno, le.dataestorno, ");
        sql.append(" le.valor, le.complementohistorico ");
        sql.append(" from liquidacaoestorno le ");
        sql.append(" inner join liquidacao liq on liq.id = le.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.liquidacao_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where le.categoriaorcamentaria = 'RESTO' ");
        sql.append(" and extract(month from le.dataestorno) = :mes ");
        sql.append(" and extract(year from le.dataestorno) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 34
    public List listaRegistrosPagamentoEstorno(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numeroEmpenho, pges.numero as numeroEstorno, pges.dataestorno, ");
        sql.append(" pges.valor, pges.complementohistorico ");
        sql.append(" from pagamentoestorno pges ");
        sql.append(" inner join pagamento pag on pag.id = pges.pagamento_id ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where pges.categoriaOrcamentaria = 'NORMAL' ");
        sql.append(" and extract(month from pges.dataestorno) = :mes ");
        sql.append(" and extract(year from pges.dataestorno) = :ano ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 35
    public List listaRegistrosLiquidacaoEstorno(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numeroEmpenho, les.numero as numeroEstorno, les.dataestorno, ");
        sql.append(" les.valor, les.complementohistorico ");
        sql.append(" from liquidacaoestorno les ");
        sql.append(" inner join liquidacao liq on liq.id = les.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where les.categoriaOrcamentaria = 'NORMAL' ");
        sql.append(" and extract(month from les.dataestorno) = :mes ");
        sql.append(" and extract(year from les.dataestorno) = :ano");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 36
    public List listaRegistrosLiquidacaoRestos(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as codUnidade, emp.numero as numEmpenho, liq.numero as numLiquidacao, ");
        sql.append(" liq.dataliquidacao, liq.valor, tpdocto.codigo as codTpDocto, docto.numero as numDocto, docto.serie as serieDocto, uf.uf ");
        sql.append(" from liquidacao liq ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = liq.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id ");
        sql.append(" inner join doctofiscalliquidacao docto on docto.id = ldf.doctofiscalliquidacao_id ");
        sql.append(" inner join tipodocumentofiscal tpdocto on tpdocto.id = docto.tipodocumentofiscal_id ");
        sql.append(" left join uf uf on uf.id = docto.uf_id ");
        sql.append(" where extract(YEAR from liq.dataliquidacao) = :ano ");
        sql.append(" and extract(MONTH from liq.dataliquidacao) = :mes ");
        sql.append(" and liq.categoriaOrcamentaria = 'RESTO'");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    // 37
    public List listaRegistrosPagamentoRestoEstorno(Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ex.ano, uni.codigo as unidade, emp.numero as numeroEmpenho, pges.numero as numeroEstorno, pges.dataestorno, ");
        sql.append(" pges.valor, pges.complementohistorico ");
        sql.append(" from pagamentoestorno pges ");
        sql.append(" inner join pagamento pag on pag.id = pges.pagamento_id ");
        sql.append(" inner join liquidacao liq on liq.id = pag.liquidacao_id ");
        sql.append(" inner join empenho emp on emp.id = liq.empenho_id ");
        sql.append(" inner join exercicio ex on ex.id = emp.exercicio_id ");
        sql.append(" inner join unidadeorganizacional uni on uni.id = emp.unidadeorganizacional_id ");
        sql.append(" where pges.categoriaOrcamentaria = 'RESTO' ");
        sql.append(" and extract(month from pges.dataestorno) = :mes ");
        sql.append(" and extract(year from pges.dataestorno) = :ano ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 38
    public List listaRegistrosAgentePolitico(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pf.cpf, '' as rg, '' as orgaoexpedidor, pf.nome, ende.localidade, uf.uf, pes.email, cargo.descricao ");
        sql.append(" from unidadeorganizacional uni ");
        sql.append(" inner join entidade ent on ent.id = uni.entidade_id ");
        sql.append(" inner join gestorordenadorentidade gent on gent.entidade_id = ent.id");
        sql.append(" inner join CONTRATOFP contrato on contrato.ID = gent.contrato_id");
        sql.append(" inner join VINCULOFP vinculo on contrato.id = vinculo.id");
        sql.append(" inner join MATRICULAFP mat on vinculo.matriculafp_id = mat.id");
        sql.append(" inner join pessoafisica pf on pf.id = mat.pessoa_id ");
        sql.append(" inner join pessoa pes on pes.id = pf.id ");
        sql.append(" left join pessoa_enderecocorreio pec on pec.pessoa_id = pes.id ");
        sql.append(" left join enderecocorreio ende on ende.id = pec.enderecoscorreio_id ");
        sql.append(" left join uf uf on uf.nome = ende.uf ");
        sql.append(" inner join CARGO cargo on cargo.ID = contrato.CARGO_ID ");
        sql.append(" inner join LOTACAOFUNCIONAL lotacao on lotacao.VINCULOFP_ID = contrato.ID ");
        sql.append(" where vinculo.INICIOVIGENCIA <= current_date and coalesce(vinculo.FINALVIGENCIA,current_date) >= current_date ");
        sql.append(" and lotacao.INICIOVIGENCIA <= current_date and coalesce(lotacao.FINALVIGENCIA,current_date) >= current_date ");
        Query q = em.createNativeQuery(sql.toString());
        //q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 39
    public List listaRegistrosOrdenador(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pf.cpf, uni.codigo as unidade, vinculo.INICIOVIGENCIA, 1 as inicio ");
        sql.append(" from unidadeorganizacional uni ");
        sql.append(" inner join entidade ent on ent.id = uni.entidade_id ");
        sql.append(" inner join gestorordenadorentidade gent on gent.entidade_id = ent.id  ");
        sql.append(" inner join CONTRATOFP contrato on contrato.ID = gent.contrato_id  ");
        sql.append(" inner join VINCULOFP vinculo on contrato.id = vinculo.id  ");
        sql.append(" inner join MATRICULAFP mat on vinculo.matriculafp_id = mat.id  ");
        sql.append(" inner join pessoafisica pf on pf.id = mat.pessoa_id  ");
        sql.append(" where vinculo.INICIOVIGENCIA <= current_date and coalesce(vinculo.FINALVIGENCIA,current_date) >= current_date");
        Query q = em.createNativeQuery(sql.toString());
        //q.setParameter("ano", ano);
        return q.getResultList();
    }

    // 40
    public List listaRegistrosGestor(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select pf.cpf, vinculo.INICIOVIGENCIA, 1 as inicio ");
        sql.append(" from unidadeorganizacional uni ");
        sql.append(" inner join entidade ent on ent.id = uni.entidade_id ");
        sql.append(" inner join gestorordenadorentidade gent on gent.entidade_id = ent.id ");
        sql.append(" inner join CONTRATOFP contrato on contrato.ID = gent.contrato_id ");
        sql.append(" inner join VINCULOFP vinculo on contrato.id = vinculo.id ");
        sql.append(" inner join MATRICULAFP mat on vinculo.matriculafp_id = mat.id ");
        sql.append(" inner join pessoafisica pf on pf.id = mat.pessoa_id ");
        sql.append(" where vinculo.INICIOVIGENCIA <= current_date and coalesce(vinculo.FINALVIGENCIA,current_date) >= current_date ");
        Query q = em.createNativeQuery(sql.toString());
        //q.setParameter("ano", ano);
        return q.getResultList();
    }

}
