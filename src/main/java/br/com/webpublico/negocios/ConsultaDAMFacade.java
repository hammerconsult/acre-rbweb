/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaDAM;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoProcessoDebito;

import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Renato
 */
@Stateless
public class ConsultaDAMFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public LoteBaixaFacade getLoteBaixaFacade() {
        return loteBaixaFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<DAM> listaDamPorFiltroETipoDeCadastroNumeroDAM(FiltroConsultaDAM filtroConsultaDAM) {

        String juncao = " where ";
        String sql = "select distinct dam.* from DAM dam " +
                "                 inner join Exercicio ex on dam.exercicio_id = ex.id " +
                "                 inner join ItemDAM item on item.dam_id = dam.id " +
                "                 inner join ParcelaValorDivida pv on item.parcela_id = pv.id " +
                "                 inner join valorDivida vd on vd.id = pv.valordivida_id " +
                "                 left join calculo calc on vd.calculo_id = calc.id " +
                "                 inner join divida d on vd.divida_id = d.id " +
                "                 left join CalculoPessoa cp on cp.calculo_id = calc.id " +
                "                 left join Pessoa pessoa on cp.pessoa_id = pessoa.id " +
                "                 left join Cadastro cad on calc.cadastro_id = cad.id ";

        if (filtroConsultaDAM.getSituacaoDAM() != null) {
            sql += juncao + " dam.situacao = :situacao";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getNumeroDAM() != null) {
            sql += juncao + " dam.numero = :filtroNumeroDAM ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getAnoDAM() != null && filtroConsultaDAM.getAnoDAM().getId() != null) {
            sql += juncao + " ex.ano = :filtroAnoDAM";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getPessoa() != null && filtroConsultaDAM.getPessoa().getId() != null) {
            sql += juncao + " pessoa.id = :filtroContribuinte";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getCadastro() != null && filtroConsultaDAM.getCadastro().getId() != null) {
            sql += juncao + " calc.cadastro_id = :cadastro";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getDividasSeleciondas() != null && filtroConsultaDAM.getDividasSeleciondas().size() > 0) {
            sql += juncao + " d.id in (:idsDivida) ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getCodigoLote() != null && !filtroConsultaDAM.getCodigoLote().trim().isEmpty()) {
            sql += juncao + " exists(select 1 \n" +
                    "              from lotebaixa lb\n" +
                    "             inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id\n" +
                    "           where ilb.dam_id = dam.id and lb.codigoLote = :codigoLote) ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getCodigoBarraCompleto() != null && !filtroConsultaDAM.getCodigoBarraCompleto().trim().isEmpty()) {
            sql += juncao + " replace(replace(replace(dam.codigoBarras, '.', ''), '-', ''), ' ', '') = :codigoBarras ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getDataMovimento() != null) {
            sql += juncao + " exists(select 1 \n" +
                    "              from lotebaixa lb\n" +
                    "             inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id\n" +
                    "           where ilb.dam_id = dam.id and lb.datafinanciamento = :dataMovimento) ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getDataPagamento() != null) {
            sql += juncao + " exists(select 1 \n" +
                    "              from lotebaixa lb\n" +
                    "             inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id\n" +
                    "           where ilb.dam_id = dam.id and lb.datapagamento = :dataPagamento) ";
            juncao = " and ";
        }

        sql += " order by dam.exercicio_id asc, dam.numero asc ";

        Query q = em.createNativeQuery(sql, DAM.class);

        if (filtroConsultaDAM.getSituacaoDAM() != null) {
            q.setParameter("situacao", filtroConsultaDAM.getSituacaoDAM().name());
        }
        if (filtroConsultaDAM.getPessoa() != null && filtroConsultaDAM.getPessoa().getId() != null) {
            q.setParameter("filtroContribuinte", filtroConsultaDAM.getPessoa().getId());
        }
        if (filtroConsultaDAM.getCadastro() != null && filtroConsultaDAM.getCadastro().getId() != null) {
            q.setParameter("cadastro", filtroConsultaDAM.getCadastro().getId());
        }
        if (filtroConsultaDAM.getNumeroDAM() != null) {
            q.setParameter("filtroNumeroDAM", filtroConsultaDAM.getNumeroDAM());
        }
        if (filtroConsultaDAM.getAnoDAM() != null && filtroConsultaDAM.getAnoDAM().getId() != null) {
            q.setParameter("filtroAnoDAM", filtroConsultaDAM.getAnoDAM().getAno());
        }
        if (filtroConsultaDAM.getDividasSeleciondas() != null && filtroConsultaDAM.getDividasSeleciondas().size() > 0) {
            q.setParameter("idsDivida", filtroConsultaDAM.getIdsDividasSelecionadas());
        }
        if (filtroConsultaDAM.getCodigoLote() != null && !filtroConsultaDAM.getCodigoLote().trim().isEmpty()) {
            q.setParameter("codigoLote", filtroConsultaDAM.getCodigoLote());
        }
        if (filtroConsultaDAM.getCodigoBarraCompleto() != null && !filtroConsultaDAM.getCodigoBarraCompleto().trim().isEmpty()) {
            q.setParameter("codigoBarras", filtroConsultaDAM.getCodigoBarraCompleto().replace(".", "").replace("-", "").replace(" ", ""));
        }
        if (filtroConsultaDAM.getDataMovimento() != null) {
            q.setParameter("dataMovimento", filtroConsultaDAM.getDataMovimento());
        }
        if (filtroConsultaDAM.getDataPagamento() != null) {
            q.setParameter("dataPagamento", filtroConsultaDAM.getDataPagamento());
        }
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<DAM> listaParcelaDebitoConsultandoDAM(FiltroConsultaDAM filtroConsultaDAM) {

        String juncao = " where ";
        String hql = "select distinct dam from DAM dam "
                + " inner join dam.itens itemDam "
                + " inner join itemDam.parcela pv "
                + " inner join pv.valorDivida.calculo.pessoas pessoa ";

        if (filtroConsultaDAM.getSituacaoDAM() != null) {
            hql += juncao + " dam.situacao = :situacao";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getNumeroDAM() != null) {
            hql += juncao + " dam.numero = :filtroNumeroDAM ";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getAnoDAM() != null && filtroConsultaDAM.getAnoDAM().getId() != null) {
            hql += juncao + " dam.exercicio = :filtroAnoDAM";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getPessoa() != null && filtroConsultaDAM.getPessoa().getId() != null) {
            hql += juncao + " pessoa.pessoa = :filtroContribuinte";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getCadastro() != null && filtroConsultaDAM.getCadastro().getId() != null) {
            hql += juncao + " pv.valorDivida.calculo.cadastro = :cadastro";
            juncao = " and ";
        }
        if (filtroConsultaDAM.getTipoCadastroTributario() != null) {
            hql += juncao + " pv.valorDivida.divida.tipoCadastro = :tipoCadastro";
            juncao = " and ";
        }

        Query q = em.createQuery(hql.toString(), DAM.class);

        if (filtroConsultaDAM.getSituacaoDAM() != null) {
            q.setParameter("situacao", filtroConsultaDAM.getSituacaoDAM());
        }
        if (filtroConsultaDAM.getPessoa() != null && filtroConsultaDAM.getPessoa().getId() != null) {
            q.setParameter("filtroContribuinte", filtroConsultaDAM.getPessoa());
        }
        if (filtroConsultaDAM.getCadastro() != null && filtroConsultaDAM.getCadastro().getId() != null) {
            q.setParameter("cadastro", filtroConsultaDAM.getCadastro());
        }
        if (filtroConsultaDAM.getNumeroDAM() != null) {
            q.setParameter("filtroNumeroDAM", filtroConsultaDAM.getNumeroDAM());
        }
        if (filtroConsultaDAM.getAnoDAM() != null && filtroConsultaDAM.getAnoDAM().getId() != null) {
            q.setParameter("filtroAnoDAM", filtroConsultaDAM.getAnoDAM().getAno());
        }
        if (filtroConsultaDAM.getTipoCadastroTributario() != null) {
            q.setParameter("tipoCadastro", filtroConsultaDAM.getTipoCadastroTributario());
        }
        return q.getResultList();
    }

    public String nomeDoCadastro(ParcelaValorDivida parcela) {
        String sql = "SELECT DISTINCT "
                + " coalesce(pesFisCadE.nome,"
                + "          pesFisCadR.nome,"
                + "          pesFisCadI.nome,"
                + "          pesFisContrato.nome"
                + "          pesJurCadI.nomefantasia,"
                + "          pesJurCadR.nomefantasia,"
                + "          pesJurCadE.nomefantasia,"
                + "          pesJurContrato.nomefantasia) "
                + " FROM parcelavalordivida pv"
                + " INNER JOIN valordivida valordivida ON pv.valordivida_id = valordivida.id"
                + " INNER JOIN calculo calc ON valordivida.calculo_id = calc.id"
                + " INNER JOIN cadastro cadastro ON calc.cadastro_id = cadastro.id"
                + " "
                + " LEFT JOIN cadastroeconomico cadE ON cadastro.id = cadE.id"
                + " LEFT JOIN pessoa pesCadE ON cadE.pessoa_id = pesCadE.id"
                + " LEFT JOIN pessoafisica pesFisCadE ON pesCadE.id = pesFisCadE.id"
                + " LEFT JOIN pessoajuridica pesJurCadE ON pesCadE.id = pesJurCadE.id"
                + " "
                + " LEFT JOIN cadastroimobiliario cadI ON cadastro.id = cadI.id"
                + " LEFT JOIN propriedade propI ON cadI.id = propI.imovel_id]"
                + " LEFT JOIN pessoa pesCadI ON propI.pessoa_id = pesCadI.id"
                + " LEFT JOIN pessoafisica pesFisCadI ON pesCadI.id = pesFisCadI.id"
                + " LEFT JOIN pessoajuridica pesJurCadI ON pesCadI.id = pesJurCadI.id"
                + " "
                + " LEFT JOIN cadastrorural cadR ON cadastro.id = cadR.id"
                + " LEFT JOIN propriedade propR ON cadR.id = propR.imovel_id "
                + " LEFT JOIN pessoa pesCadR ON propR.pessoa_id = pesCadR.id"
                + " LEFT JOIN pessoafisica pesFisCadR ON pesCadR.id = pesFisCadR.id"
                + " LEFT JOIN pessoajuridica pesJurCadR ON pesCadR.id = pesJurCadR.id"
                + " "
                + " LEFT JOIN contratorendaspatrimoniais contrato ON cadastro.id = contrato.id"
                + " LEFT JOIN pessoa pesContrato ON contrato.locatario_id = pesContrato.id"
                + " LEFT JOIN pessoafisica pesFisContrato ON pesFisContrato.id = pesContrato.id"
                + " LEFT JOIN pessoajuridica pesJurContrato ON pesJurContrato.id = pesContrato.id"
                + " WHERE pv.id = :parcela";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("parcela", parcela.getId());
        try {
            return (String) consulta.getResultList().get(0);
        } catch (Exception e) {
            return " - " + e.getMessage();
        }
    }

    public List<PagamentoAvulso> pagamentosAvulsosPorDam(DAM dam) {
        String sql = "select pagto.* from pagamentoavulso pagto " +
                "inner join itemdam on itemdam.parcela_id = pagto.parcelavalordivida_id " +
                "where itemdam.dam_id = :idDam " +
                " and coalesce(pagto.ativo,1) = 1";
        Query q = em.createNativeQuery(sql, PagamentoAvulso.class);
        q.setParameter("idDam", dam.getId());
        return q.getResultList();
    }

    public List<ItemProcessoDebito> itensProcessoDebitoPorDam(DAM dam) {
        String sql = "select item.* from itemprocessodebito item " +
                "inner join processodebito pro on pro.id = item.processoDebito_id " +
                "inner join itemdam on itemdam.parcela_id = item.parcela_id " +
                "where itemdam.dam_id = :idDam " +
                " and pro.tipo = :tipoProcesso " +
                " and pro.situacao = :situacao";
        Query q = em.createNativeQuery(sql, ItemProcessoDebito.class);
        q.setParameter("idDam", dam.getId());
        q.setParameter("tipoProcesso", TipoProcessoDebito.BAIXA.name());
        q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
        return q.getResultList();
    }

    public List<OcorrenciaPix> buscarOcorrenciasPix(Long idDam) {
        String sql = " select op.* from ocorrenciapix op " +
            " where op.dam_id = :idDam " +
            " order by op.dataocorrencia desc ";

        Query q = em.createNativeQuery(sql, OcorrenciaPix.class);
        q.setParameter("idDam", idDam);

        List<OcorrenciaPix> ocorrencias = q.getResultList();
        return ocorrencias != null ? ocorrencias : Lists.<OcorrenciaPix>newArrayList();
    }
}
