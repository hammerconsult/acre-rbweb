/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.ConsultaDebitoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarQueue;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarResquest;
import br.com.webpublico.negocios.tributario.consultaparcela.CalculadorAcrescimos;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.services.ServiceDAM;
import br.com.webpublico.negocios.tributario.singletons.SingletonConsultaDebitos;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.relatoriofacade.RelatorioDamFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.dtos.DamDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoCalculoDTO;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

@Stateless
public class ConsultaDebitoFacade extends AbstractFacade<ValorDivida> {

    public static final DecimalFormat formatterReais = new DecimalFormat("###,###,##0.00");
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private Logger log = LoggerFactory.getLogger(ConsultaDebitoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private CalculoFacade calculoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private PagamentoJudicialFacade pagamentoJudicialFacade;
    @EJB
    private ProcessoIsencaoIPTUFacade processoIsencaoIPTUFacade;
    @EJB
    private SingletonConsultaDebitos singletonConsultaDebitos;
    @EJB
    private ProcessoDebitoFacade processoDebitoFacade;
    @EJB
    private CompensacaoFacade compensacaoFacade;
    @EJB
    private RestituicaoFacade restituicaoFacade;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    @EJB
    private LancamentoMultaAcessoriaFacade lancamentoMultaAcessoriaFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private SubvencaoProcessoFacade subvencaoProcessoFacade;
    @EJB
    private LancamentoDescontoFacade lancamentoDescontoFacade;
    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private BloqueioJudicialFacade bloqueioJudicialFacade;
    private ServiceDAM serviceDAM;
    @EJB
    private ProcessoDeProtestoFacade processoDeProtestoFacade;
    @EJB
    private PixFacade pixFacade;
    @EJB
    private DepoisDePagarQueue depoisDePagarQueue;

    public ConsultaDebitoFacade() {
        super(ValorDivida.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroFacade getCadastroFacade() {
        return cadastroFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

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

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ParametroParcelamentoFacade getParametroParcelamentoFacade() {
        return parametroParcelamentoFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public CalculoFacade getCalculoFacade() {
        return calculoFacade;
    }

    public ProcessoParcelamentoFacade getProcessoParcelamentoFacade() {
        return processoParcelamentoFacade;
    }

    public PagamentoJudicialFacade getPagamentoJudicialFacade() {
        return pagamentoJudicialFacade;
    }

    public List<ValorDividaRetornoBD> listaPorFiltro(Pessoa filtroContribuinte, String filtroInscricao, String filtroLote, String vencidos) {
        StringBuilder sql = new StringBuilder("select vd.id, vd.emissao, vd.valor, vd.divida_id, vd.entidade_id, ").append(" vd.exercicio_id, vd.calculo_id, vd.dataquitacao, ci.codigo, lote.codigolote ").append(" from calculoiptu iptu inner join valordivida vd on vd.calculo_id = iptu.id ").append(" inner join cadastroimobiliario ci on ci.id = iptu.cadastroimobiliario_id ").append(" inner join lote lote on lote.id = ci.lote_id ").append(" inner join propriedade prop on prop.imovel_id = ci.id ").append(" inner join pessoa p on p.id = prop.pessoa_id ").append(" left join pessoafisica pf on pf.id = p.id ").append(" left join pessoajuridica pj on pj.id = p.id ").append(" inner join parcelavalordivida parcela on parcela.valordivida_id = vd.id ").append(" where 1 = 1 ");

        // Adicionar sql de filtros
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            sql.append(" and p.id = :pessoa ");
        }
        if ("vencidos".equals(vencidos.toLowerCase())) {
            sql.append(" and parcela.vencimento <= :dataAtual");
        }
        if ("vincendos".equals(vencidos.toLowerCase())) {
            sql.append(" and parcela.vencimento >= :dataAtual");
        }
        if (filtroInscricao != null && filtroInscricao.length() > 0) {
            sql.append(" and ci.inscricaoCadastral like :filtroInscricao");
        }
        if (filtroLote != null && filtroLote.length() > 0) {
            sql.append(" and lote.codigoLote like :filtroLote");
        }

        // Query
        Query q = em.createNativeQuery(sql.toString());
        // Colocar parametros
        if (filtroInscricao != null && filtroInscricao.length() > 0) {
            q.setParameter("filtroInscricao", "%" + filtroInscricao.trim() + "%");
        }
        if (filtroLote != null && filtroLote.length() > 0) {
            q.setParameter("filtroLote", "%" + filtroLote.trim() + "%");
        }
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            q.setParameter("pessoa", filtroContribuinte.getId());
        }
        if (!"todos".equals(vencidos.toLowerCase())) {
            q.setParameter("dataAtual", new Date());
        }
        // ------------------------------------------
        List<Object[]> listaArrayObjetos = q.getResultList();
        Object[] ob;
        List<ValorDividaRetornoBD> listaEntidadeNaoMapeada = new ArrayList<>();
        for (int i = 0; i < listaArrayObjetos.size(); i++) {
            ob = listaArrayObjetos.get(i);
            ValorDividaRetornoBD ra = new ValorDividaRetornoBD((BigDecimal) ob[0], (Date) ob[1], (BigDecimal) ob[2], (BigDecimal) ob[3], (BigDecimal) ob[4], (BigDecimal) ob[5], (BigDecimal) ob[6], (Date) ob[7], (String) ob[8], (String) ob[9]);
            listaEntidadeNaoMapeada.add(ra);
        }
        return listaEntidadeNaoMapeada;
    }

    @Override
    public ValorDivida recuperar(Object id) {
        ValorDivida vd = em.find(ValorDivida.class, id);
        vd.getParcelaValorDividas().size();
        vd.getItemValorDividas().size();
        return vd;
    }

    public ParcelaValorDivida recuperaParcela(Object id) {
        ParcelaValorDivida p = em.find(ParcelaValorDivida.class, id);
        p.getItensParcelaValorDivida().size();
        return p;
    }

    public List<Propriedade> recuperaPorCadastroImobiliario(CadastroImobiliario ci) {
        String hql = "select p from Propriedade p where p.imovel = :ci";
        Query q = em.createQuery(hql);
        q.setParameter("ci", ci);
        return q.getResultList();
    }

    public List<ParcelaValorDivida> listaParcelaDebito(Pessoa filtroContribuinte, String filtroInscricao, String filtroLote, Date inicioVencimento, Date finalVencimento, int filtroTipoLote, String vencidos) throws UFMException {
        String juncao = " where ";
        StringBuilder hql = new StringBuilder("select distinct pvd.* ").append("from ParcelaValorDivida pvd ").append("inner join ValorDivida vd on vd.id = pvd.valordivida_id ").append("inner join CalculoIPTU calculo on calculo.id = vd.calculo_id ").append("inner join CadastroImobiliario ci on ci.id = calculo.cadastroimobiliario_id ").append("inner join Propriedade prop on prop.imovel_id = ci.id ").append("inner join Lote lote on lote.id = ci.lote_id ");
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            hql.append(juncao).append(" prop.pessoa_id = :pessoa");
            juncao = " and ";
        }

        if (filtroInscricao != null && !"".equals(filtroInscricao.trim())) {
            hql.append(juncao).append("ci.inscricaoCadastral like :codigo");
            juncao = " and ";
        }
        if (filtroTipoLote != 0) {

            if (filtroTipoLote == 2) {
                hql.append(juncao).append("lote.codigoLote like :codigoLote");
                juncao = " and ";
            } else if (filtroTipoLote == 1) {
                hql.append(juncao).append("lote.descricaoLoteamento like :descricaoLoteamento");
                juncao = " and ";
            } else {
                hql.append(juncao).append("lote.descricaoLoteamento like :descricaoLoteamento or lote.codigoLote like :codigoLote");
                juncao = " and ";
            }
        }
        if (inicioVencimento != null) {
            hql.append(juncao).append("pvd.vencimento >= :inicioVencimento");
            juncao = " and ";
        }
        if (finalVencimento != null) {
            hql.append(juncao).append("pvd.vencimento <= :finalVencimento");
            juncao = " and ";
        }
        if (vencidos.equals("vencidos")) {
            hql.append(juncao).append("pvd.vencimento < sysdate");
        } else if (vencidos.equals("vincendos")) {
            hql.append(juncao).append("pvd.vencimento >= sysdate");
        }
        hql.append(" order by pvd.opcaopagamento_id, pvd.vencimento");

        Query q = em.createNativeQuery(hql.toString(), ParcelaValorDivida.class);
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            q.setParameter("pessoa", filtroContribuinte.getId());
        }
        if (filtroInscricao != null && !"".equals(filtroInscricao.trim())) {
            q.setParameter("codigo", "%" + filtroInscricao.trim() + "%");
        }

        if (filtroLote != null && !"".equals(filtroLote.trim()) && (filtroTipoLote == 2 || filtroTipoLote == 3)) {
            q.setParameter("codigoLote", "%" + filtroLote.trim() + "%");
        }
        if (filtroLote != null && !"".equals(filtroLote.trim()) && (filtroTipoLote == 1 || filtroTipoLote == 3)) {
            q.setParameter("descricaoLoteamento", "%" + filtroLote.trim() + "%");
        }

        if (inicioVencimento != null) {
            q.setParameter("inicioVencimento", inicioVencimento);
        }
        if (finalVencimento != null) {
            q.setParameter("finalVencimento", finalVencimento);
        }

        List<ParcelaValorDivida> parcelas = q.getResultList();
        Date dataReferencia = new Date();
        for (ParcelaValorDivida parcela : parcelas) {
            parcela = CalculadorAcrescimos.calculaAcrescimo(parcela, dataReferencia, getValorAtualizadoParcela(parcela), dividaFacade.configuracaoVigente(parcela.getValorDivida().getDivida()));
        }

        return parcelas;
    }

    public List<ParcelaValorDivida> listaParcelaDebitoPorFiltro(SituacaoParcela situacao, String filtroCodigoBarras, String filtroContribuinte, String filtroInscricao, String filtroNumeroDAM) {

        StringBuilder hql = new StringBuilder("select distinct pv from ParcelaValorDivida pv join pv.situacoes situacao where 1=1 ");

        if (filtroCodigoBarras != null && !filtroCodigoBarras.trim().isEmpty()) {
            hql.append(" and (pv.codigoBarras like :filtroCodigoBarras ");
            hql.append(" or (substr(pv.codigoBarras, 0, 11) || ").append("substr(pv.codigoBarras, 15, 11) || ").append("substr(pv.codigoBarras, 29, 11) || ").append("substr(pv.codigoBarras, 43, 11)) ").append("like :filtroCodigoBarras) ");
        }

        hql.append(" and (situacao.situacaoParcela = ").append("'EM_ABERTO' and situacao.id = ").append("(select max(s.id) from SituacaoParcelaValorDivida ").append("s where s.parcela = pv))");


        if (filtroContribuinte != null && !filtroContribuinte.trim().isEmpty()) {
            hql.append(" and ").append(" pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c left join c.cadastroImobiliario.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where lower(p.nome) like :filtroContribuinte)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where lower(p.razaoSocial) like :filtroContribuinte))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c left join c.cadastroRural.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where lower(p.nome) like :filtroContribuinte)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where lower(p.razaoSocial) like :filtroContribuinte)) )").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoISS c where c.cadastroEconomico.pessoa in (select p from PessoaFisica p where lower(p.nome) like :filtroContribuinte) ").append(" or (c.cadastroEconomico.pessoa in (select p from PessoaJuridica p where lower(p.razaoSocial) like :filtroContribuinte))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoRendas c where c.contrato.locatario in (select p from PessoaFisica p where lower(p.nome) like :filtroContribuinte) ").append(" or (c.contrato.locatario in (select p from PessoaJuridica p where lower(p.razaoSocial) like :filtroContribuinte))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoIPTU c left join c.cadastroImobiliario.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where lower(p.nome) like :filtroContribuinte)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where lower(p.razaoSocial) like :filtroContribuinte))) ");
        }
        if (filtroInscricao != null && !filtroInscricao.trim().isEmpty()) {
            hql.append(" and ").append(" pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c " + "where c.cadastroImobiliario.inscricaoCadastral like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c where c.cadastroRural.codigo like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoISS c where c.cadastroEconomico.inscricaoCadastral like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoRendas c where c.contrato.numeroContrato like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoAlvaraFuncionamento c where c.cadastroEconomico.inscricaoCadastral like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoAlvaraLocalizacao c where c.cadastroEconomico.inscricaoCadastral like :filtroInscricao) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoIPTU c where c.cadastroImobiliario.inscricaoCadastral like :filtroInscricao) ");
        }
        if (filtroNumeroDAM != null && !filtroNumeroDAM.trim().isEmpty()) {
            hql.append(" and ").append(" pv.numeroDAM like :filtroNumeroDAM ");
        }
        Query q = em.createQuery(hql.toString(), ParcelaValorDivida.class);
        if (filtroCodigoBarras != null && !filtroCodigoBarras.trim().isEmpty()) {
            q.setParameter("filtroCodigoBarras", "%" + filtroCodigoBarras.toLowerCase() + "%");
        }
        if (filtroContribuinte != null && !filtroContribuinte.trim().isEmpty()) {
            q.setParameter("filtroContribuinte", "%" + filtroContribuinte.toLowerCase() + "%");
        }
        if (filtroInscricao != null && !filtroInscricao.trim().isEmpty()) {
            q.setParameter("filtroInscricao", "%" + filtroInscricao.toLowerCase() + "%");
        }
        if (filtroNumeroDAM != null && !filtroNumeroDAM.trim().isEmpty()) {
            q.setParameter("filtroNumeroDAM", "%" + filtroNumeroDAM.toLowerCase() + "%");
        }

        return q.getResultList();
    }

    public List<DataTableConsultaDebito> listaParcelaDebitoPorFiltroETipoDeCadastro(Pessoa filtroContribuinte, Cadastro cadastroInicial, Cadastro cadastroFinal, Exercicio exercicioInicial, Exercicio exercicioFinal, Date vencimentoInicial, Date vencimentoFinal, TipoCadastroTributario tipoCadastroTributario, Boolean incluiSocios, SituacaoParcela[] situacoes) {
        StringBuilder hql = new StringBuilder("select distinct ").append(" new br.com.webpublico.entidadesauxiliares.DataTableConsultaDebito(").append(" pv,").append(" cadastro,").append(" vd.divida,").append(" vd.exercicio,").append(" case when pv.opcaoPagamento.promocional = 1 then (pv.sequenciaParcela || '/0') else (pv.sequenciaParcela||'/' ||to_char( (select count(parcela.id) from ParcelaValorDivida parcela where parcela.valorDivida = pv.valorDivida and parcela.opcaoPagamento = pv.opcaoPagamento)  )) end,").append(" cal.subDivida,").append(" coalesce(").append(" (select processo.numero || '/' || processo.exercicio.ano ").append(" from ProcessoParcelamento processo ").append(" join processo.itensProcessoParcelamento item ").append(" where item.parcelaValorDivida = pv) ").append(" , (select processo.numero || '/' || processo.exercicio.ano ").append(" from ProcessoParcelamento processo where processo = cal)), ").append(" pv.vencimento, ").append(" situacao, ").append(" pv.valor/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(pv.dataRegistro,'MM')) and m.ano = vd.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1), ").append(" case when situacao.situacaoParcela != 'PAGO' then ").append(" ((situacao.saldo/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(pv.dataRegistro,'MM')) and m.ano = vd.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1)) ").append(" * coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(current_date,'MM')) and m.ano = TO_NUMBER(TO_CHAR(current_date,'yyyy')) and m.indiceEconomico.descricao = 'UFM'),1)) ").append(" else ( ").append(" (select sum(itemLoteParcela.itemLoteBaixa.valorPago) from ItemLoteBaixaParcela itemLoteParcela ").append("  where itemLoteParcela.itemDam.parcela = pv) - 0) ").append(" end, ").append(" (select c.acrescimo from DividaAcrescimo c where c.divida = vd.divida and c.inicioVigencia <= CURRENT_DATE and (c.finalVigencia is null or c.finalVigencia >= CURRENT_DATE)), ").append(" (select coalesce(sum((desconto.itemParcelaValorDivida.valor * (desconto.porcentagemDesconto/100))),0) from DescontoItemParcela desconto where desconto.itemParcelaValorDivida.parcelaValorDivida = pv), ").append(" (select coalesce(sum(item.valor),0) from ItemParcelaValorDivida item where item.parcelaValorDivida = pv and item.itemValorDivida.tributo.tipoTributo = 'IMPOSTO'), ").append(" (select coalesce(sum(item.valor),0) from ItemParcelaValorDivida item where item.parcelaValorDivida = pv and item.itemValorDivida.tributo.tipoTributo <> 'IMPOSTO')) ").append(" from ParcelaValorDivida pv ").append(" join pv.situacoes situacao ").append(" join pv.valorDivida vd ").append(" join vd.calculo cal ").append(" left join cal.cadastro cadastro ").append(" left join cal.pessoas pessoa ").append(" where situacao.id = ").append(" (select max(s.id) from SituacaoParcelaValorDivida s ").append(" where s.parcela = pv) ").append(" and pv not in (select pv from ParcelaValorDivida pv where pv.opcaoPagamento.promocional is true and  pv.vencimento < current_date)");

        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            hql.append(" and pessoa.pessoa = :filtroContribuinte");
            if (incluiSocios) {
                hql.append(" or vd.calculo in ").append(" (select c from CalculoISS c join c.cadastroEconomico.sociedadeCadastroEconomico sociedade where sociedade.socio in (select p from PessoaFisica p where p = :filtroContribuinte) ").append(" or (sociedade.socio in (select p from PessoaJuridica p where p = :filtroContribuinte)))");
            }
        }
        if (vencimentoInicial != null) {
            hql.append(" and  pv.vencimento >= :vencimentoInicial");
        }
        if (vencimentoFinal != null) {
            hql.append(" and pv.vencimento <= :vencimentoFinal");
        }

        if (exercicioInicial != null && exercicioInicial.getId() != null) {
            hql.append(" and  vd.exercicio.ano >= :filtroExercicioInicio");
        }
        if (exercicioFinal != null && exercicioFinal.getId() != null) {
            hql.append(" and  vd.exercicio.ano <= :filtroExercicioFinal");
        }
        //FILTRO PARA CADASTRO INICIAL
        if (cadastroInicial != null && cadastroInicial.getId() != null) {
            hql.append(" and ( cadastro in ");
            if (cadastroInicial instanceof CadastroImobiliario) {
                hql.append(" (select c from CadastroImobiliario c where c.inscricaoCadastral >= :filtroInscricaoInicial) ");
            } else if (cadastroInicial instanceof CadastroRural) {
                hql.append(" (select c from CadastroRural c where to_char(c.codigo) >= :filtroInscricaoInicial) ");
            } else if (cadastroInicial instanceof CadastroEconomico) {
                hql.append(" (select c from CadastroEconomico c where c.inscricaoCadastral >= :filtroInscricaoInicial) ");
            }
            hql.append(")");
        }
        //FILTRO PARA CADASTRO FINAL
        if (cadastroFinal != null && cadastroFinal.getId() != null) {
            hql.append(" and ( cadastro in ");
            if (cadastroFinal instanceof CadastroImobiliario) {
                hql.append(" (select c from CadastroImobiliario c where c.inscricaoCadastral <= :filtroInscricaoFinal) ");
            } else if (cadastroFinal instanceof CadastroRural) {
                hql.append(" (select c from CadastroRural c where to_char(c.codigo) <= :filtroInscricaoFinal) ");
            } else if (cadastroFinal instanceof CadastroEconomico) {
                hql.append(" (select c from CadastroEconomico c where c.inscricaoCadastral <= :filtroInscricaoFinal) ");
            }
            hql.append(")");
        }


        if (situacoes != null && situacoes.length > 0) {
            hql.append(" and situacao.situacaoParcela in (").append(montaStringSituacao(situacoes)).append(") ");
        } else {
            hql.append(" and situacao.situacaoParcela = 'EM_ABERTO'");
        }

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(100);
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            q.setParameter("filtroContribuinte", filtroContribuinte);
        }
        if (cadastroInicial != null && cadastroInicial.getId() != null) {
            if (cadastroInicial instanceof CadastroImobiliario) {
                q.setParameter("filtroInscricaoInicial", ((CadastroImobiliario) cadastroInicial).getInscricaoCadastral().toLowerCase());
            } else if (cadastroInicial instanceof CadastroRural) {
                q.setParameter("filtroInscricaoInicial", ((CadastroRural) cadastroInicial).getCodigo().toString());
            } else if (cadastroInicial instanceof CadastroEconomico) {
                q.setParameter("filtroInscricaoInicial", ((CadastroEconomico) cadastroInicial).getInscricaoCadastral().toLowerCase());
            }
        }
        if (cadastroFinal != null && cadastroFinal.getId() != null) {
            if (cadastroFinal instanceof CadastroImobiliario) {
                q.setParameter("filtroInscricaoFinal", ((CadastroImobiliario) cadastroFinal).getInscricaoCadastral().toLowerCase());
            } else if (cadastroFinal instanceof CadastroRural) {
                q.setParameter("filtroInscricaoFinal", ((CadastroRural) cadastroFinal).getCodigo().toString());
            } else if (cadastroFinal instanceof CadastroEconomico) {
                q.setParameter("filtroInscricaoFinal", ((CadastroEconomico) cadastroFinal).getInscricaoCadastral().toLowerCase());
            }
        }

        if (vencimentoInicial != null) {
            q.setParameter("vencimentoInicial", vencimentoInicial);
        }
        if (vencimentoFinal != null) {
            q.setParameter("vencimentoFinal", vencimentoFinal);
        }

        if (exercicioInicial != null && exercicioInicial.getId() != null) {
            q.setParameter("filtroExercicioInicio", exercicioInicial.getAno());
        }
        if (exercicioFinal != null && exercicioFinal.getId() != null) {
            q.setParameter("filtroExercicioFinal", exercicioFinal.getAno());
        }
        return q.getResultList();
    }

    public List<ParcelaValorDivida> listaParcelaDebitoPorFiltroETipoDeCadastroECodigoDeBarrasENumeroDAM(Pessoa filtroContribuinte, Cadastro cadastro, Boolean incluiSocios, SituacaoParcela situacao, String filtroCodigoBarras, String filtroNumeroDAM, Divida filtroDivida, Exercicio filtroExercicioInicio, Exercicio filtroExercicioFinal, boolean dividaDoExercicio, boolean dividaAtiva, boolean dividaAtivaAjuizada) {

        StringBuilder hql = new StringBuilder("select distinct ").append(" new ParcelaValorDivida(pv, ").append(" ((situacao.saldo/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(pv.dataRegistro,'MM')) and m.ano = pv.valorDivida.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1)) ").append(" * coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(current_date,'MM')) and m.ano = TO_NUMBER(TO_CHAR(current_date,'yyyy')) and m.indiceEconomico.descricao = 'UFM'),1))) ").append("  from DAM dam ").append(" join dam.itens itemDam ").append(" join itemDam.parcela pv ").append(" join pv.situacoes situacao ").append(" left join pv.valorDivida.calculo.pessoas pessoa").append(" where situacao.id = ").append(" (select max(s.id) from SituacaoParcelaValorDivida s ").append(" where s.parcela = pv) and situacao.situacaoParcela = :situacao").append(" ");
        if (filtroCodigoBarras != null && !filtroCodigoBarras.trim().isEmpty()) {
            hql.append(" and (pv.codigoBarras like :filtroCodigoBarras ");
            hql.append(" or (substr(pv.codigoBarras, 0, 11) || ").append("substr(pv.codigoBarras, 15, 11) || ").append("substr(pv.codigoBarras, 29, 11) || ").append("substr(pv.codigoBarras, 43, 11)) ").append("like :filtroCodigoBarras) ");
        }

        //Filtros para tipos de dívidas
        //Do Exercicio
        if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAjuizada) {
            hql.append(" and ").append(" ((coalesce(pv.dividaAtivaAjuizada,0) = 0) and (coalesce (pv.dividaAtiva,0) = 0))");
        }
        //Exercicio e divida ativa
        if (dividaDoExercicio && dividaAtiva && !dividaAtivaAjuizada) {
            hql.append(" and ").append(" ((coalesce(pv.dividaAtivaAjuizada,0) = 0) or pv.dividaAtiva = 1)");
        }
        //Divida ativa
        if (dividaAtiva && !dividaAtivaAjuizada && !dividaDoExercicio) {
            hql.append(" and ").append(" pv.dividaAtiva = 1 ");
        }
        //Divida ativa e ajuizada
        if (dividaAtiva && dividaAtivaAjuizada && !dividaDoExercicio) {
            hql.append(" and ").append("( pv.dividaAtivaAjuizada = 1 or pv.dividaAtiva = 1 )");
        }
        //divida ativa Ajuizada e do exercicio
        if (dividaAtivaAjuizada && dividaDoExercicio && !dividaAtiva) {
            hql.append(" and ").append(" ((coalesce(pv.dividaAtiva,0) = 0) or pv.dividaAtivaAjuizada = 1)");
        }
        //Divida ativa Ajuizada
        if (dividaAtivaAjuizada && !dividaDoExercicio && !dividaAtiva) {
            hql.append(" and ").append(" pv.dividaAtivaAjuizada = 1 ");
        }
        //Final filtros do tipo de dívida

        if (filtroNumeroDAM != null && !filtroNumeroDAM.trim().isEmpty()) {
            hql.append(" and ").append(" dam.numeroDAM like :filtroNumeroDAM ");
        }
        if (filtroDivida != null && !filtroDivida.getDescricao().trim().isEmpty()) {
            hql.append(" and ").append(" pv.valorDivida.divida like :filtroDivida ");
        }
        if (filtroExercicioInicio != null && filtroExercicioInicio.getId() != null) {
            hql.append(" and ").append(" pv.valorDivida.exercicio.ano >= :filtroExercicioInicio");
        }
        if (filtroExercicioFinal != null && filtroExercicioFinal.getId() != null) {
            hql.append(" and ").append(" pv.valorDivida.exercicio.ano <= :filtroExercicioFinal");
        }

        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            hql.append(" and pessoa.pessoa = :filtroContribuinte");
            if (incluiSocios) {
                hql.append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoISS c join c.cadastroEconomico.sociedadeCadastroEconomico sociedade ").append(" where sociedade.socio in ").append("(select p from PessoaFisica p where p = :filtroContribuinte) ").append(" or (sociedade.socio in ").append("(select p from PessoaJuridica p where p = ").append(":filtroContribuinte)))");
            }
        }

        if (cadastro != null && cadastro.getId() != null) {
            hql.append(" and ").append(" pv.valorDivida.calculo.cadastro = :filtroInscricao");
        }

        Query q = em.createQuery(hql.toString());
        q.setParameter("situacao", situacao);
        if (filtroContribuinte != null && filtroContribuinte.getId() != null) {
            q.setParameter("filtroContribuinte", filtroContribuinte);
        }
        if (cadastro != null && cadastro.getId() != null) {

            q.setParameter("filtroInscricao", cadastro);

        }
        if (filtroCodigoBarras != null && !filtroCodigoBarras.trim().isEmpty()) {
            q.setParameter("filtroCodigoBarras", filtroCodigoBarras);
        }
        if (filtroNumeroDAM != null && !filtroNumeroDAM.trim().isEmpty()) {
            q.setParameter("filtroNumeroDAM", "%" + filtroNumeroDAM + "%");
        }
        if (filtroDivida != null && !filtroDivida.getDescricao().trim().isEmpty()) {
            q.setParameter("filtroDivida", filtroDivida);
        }
        if (filtroExercicioInicio != null && filtroExercicioInicio.getId() != null) {
            q.setParameter("filtroExercicioInicio", filtroExercicioInicio.getAno());
        }
        if (filtroExercicioFinal != null && filtroExercicioFinal.getId() != null) {
            q.setParameter("filtroExercicioFinal", filtroExercicioFinal.getAno());
        }
        return (List<ParcelaValorDivida>) q.getResultList();
    }


    public InscricaoDividaAtiva geraInscricaoDividaAtiva(List<ParcelaValorDivida> lista, InscricaoDividaAtiva selecionado) {
        return vinculaDependenciasInscricaoDividaAtiva(lista, selecionado);
    }

    private InscricaoDividaAtiva vinculaDependenciasInscricaoDividaAtiva(List<ParcelaValorDivida> parcelas, InscricaoDividaAtiva selecionado) {
        List<ValorDivida> vd = new ArrayList<>();

        for (ParcelaValorDivida parcela : parcelas) {
            if (!vd.contains(parcela.getValorDivida())) {
                vd.add(parcela.getValorDivida());
            }
        }

        for (ValorDivida valorDivida : vd) {
            ItemInscricaoDividaAtiva item = new ItemInscricaoDividaAtiva();
            item.setItensInscricaoDividaParcelas(new ArrayList<InscricaoDividaParcela>());
            item.setInscricaoDividaAtiva(selecionado);
            item.setDivida(valorDivida.getDivida());
            if (!valorDivida.getCalculo().getPessoas().isEmpty()) {
                item.setPessoa(valorDivida.getCalculo().getPessoas().get(0).getPessoa());
            }
            item.setCadastro(valorDivida.getCalculo().getCadastro());
            for (ParcelaValorDivida parc : parcelas) {
                if (parc.getValorDivida().equals(valorDivida)) {
                    InscricaoDividaParcela inscriacaoParcela = new InscricaoDividaParcela();
                    inscriacaoParcela.setParcelaValorDivida(parc);
                    inscriacaoParcela.setItemInscricaoDividaAtiva(item);
                    item.getItensInscricaoDividaParcelas().add(inscriacaoParcela);
                }
            }
            if (item.getItensInscricaoDividaParcelas().size() > 0) {
                selecionado.getItensInscricaoDividaAtivas().add(item);
            }
        }
        return selecionado;
    }

    public List<ParcelaValorDivida> listaParcelaDebitoPorCadastro(SituacaoParcela situacao, Cadastro cadastro) {
        StringBuilder hql = new StringBuilder("select pv from ParcelaValorDivida pv join pv.situacoes situacao where 1=1 ");
        if (situacao != null) {
            hql.append(" and (situacao.situacaoParcela = ").append(":situacaoParcela and situacao.id = ").append("(select max(s.id) from SituacaoParcelaValorDivida ").append("s where s.parcela = pv))");
        }
        if (cadastro != null && cadastro.getId() != null) {
            hql.append(" and ").append(" pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c where c.cadastroImobiliario.id = :cadastro) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c where c.cadastroRural.id = :cadastro) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoISS c where c.cadastroEconomico.id = :cadastro) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoRendas c where c.cadastro.id = :cadastro) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoIPTU c where c.cadastroImobiliario.id = :cadastro) ");
        }
        Query q = em.createQuery(hql.toString(), ParcelaValorDivida.class);
        if (cadastro != null && cadastro.getId() != null) {
            q.setParameter("cadastro", cadastro.getId());
        }
        if (situacao != null) {
            q.setParameter("situacaoParcela", situacao);
        }
        return q.getResultList();
    }

    public List<ParcelaValorDivida> listaParcelaDebitoPorPessoa(SituacaoParcela situacao, Pessoa pessoa) {
        StringBuilder hql = new StringBuilder("select pv from ParcelaValorDivida pv join pv.situacoes situacao where 1=1 ");
        if (situacao != null) {
            hql.append(" and (situacao.situacaoParcela = ").append(":situacaoParcela and situacao.id = ").append("(select max(s.id) from SituacaoParcelaValorDivida ").append("s where s.parcela = pv))");
        }
        if (pessoa != null && pessoa.getId() != null) {
            hql.append(" and ").append(" pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c left join c.cadastroImobiliario.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where p.id = :pessoa)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where p.id = :pessoa))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoITBI c left join c.cadastroRural.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where p.id = :pessoa)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where p.id = :pessoa)) )").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoISS c where c.cadastroEconomico.pessoa in (select p from PessoaFisica p where p.id = :pessoa) ").append(" or (c.cadastroEconomico.pessoa in (select p from PessoaJuridica p where p.id = :pessoa))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoRendas c where c.contrato.locatario in (select p from PessoaFisica p where p.id = :pessoa) ").append(" or (c.contrato.locatario in (select p from PessoaJuridica p where p.id = :pessoa))) ").append(" or pv.valorDivida.calculo in ").append(" (select c from CalculoIPTU c left join c.cadastroImobiliario.propriedade propriedade ").append(" where (propriedade.pessoa in (select p from PessoaFisica p where p.id = :pessoa)) ").append(" or (propriedade.pessoa in (select p from PessoaJuridica p where p.id = :pessoa))) ");
        }
        Query q = em.createQuery(hql.toString(), ParcelaValorDivida.class);
        if (pessoa != null && pessoa.getId() != null) {
            q.setParameter("pessoa", pessoa.getId());
        }
        if (situacao != null) {
            q.setParameter("situacaoParcela", situacao);
        }
        return q.getResultList();
    }

    public List<DataTableConsultaDebito> listaParcelaCalculandoDebitoPorTipoDeCadastro(Pessoa filtroContribuinte, Cadastro cadastroInicial, Cadastro cadastroFinal, Exercicio exercicioInicial, Exercicio exercicioFinal, Date vencimentoInicial, Date vencimentoFinal, TipoCadastroTributario tipoCadastroTributario, Boolean incluiSocios, SituacaoParcela[] situacoes) throws UFMException {
        List<DataTableConsultaDebito> parcelas = listaParcelaDebitoPorFiltroETipoDeCadastro(filtroContribuinte, cadastroInicial, cadastroFinal, exercicioInicial, exercicioFinal, vencimentoInicial, vencimentoFinal, tipoCadastroTributario, incluiSocios, situacoes);
        return parcelas;
    }

    public SituacaoParcelaValorDivida getUltimaSituacao(ParcelaValorDivida parcela) {
        StringBuilder sql = new StringBuilder("Select s.* from SituacaoParcelaValorDivida s inner join ParcelaValorDivida pvd on pvd.situacaoAtual_id = s.id where pvd.id = :parcela");
        Query q = em.createNativeQuery(sql.toString(), SituacaoParcelaValorDivida.class).setParameter("parcela", parcela.getId()).setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SituacaoParcelaValorDivida) resultList.get(0);
        } else {
            return null;
        }
    }

    public SituacaoParcelaValorDivida getUltimaSituacaoEmAberto(Long idParcela) {
        StringBuilder sql = new StringBuilder("select * from SituacaoParcelaValorDivida where parcela_id = :idParcela and situacaoParcela = :aberta order by dataLancamento desc");
        Query q = em.createNativeQuery(sql.toString(), SituacaoParcelaValorDivida.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("aberta", SituacaoParcela.EM_ABERTO.name());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SituacaoParcelaValorDivida) resultList.get(0);
        } else {
            return null;
        }
    }

    public BigDecimal getValorAtualizadoParcela(ParcelaValorDivida parcela) {
        return getValorAtualizadoParcela(parcela, new Date());
    }

    @Deprecated
    public BigDecimal getValorAtualizadoParcela(ParcelaValorDivida parcela, Date data) {
        StringBuilder hql = new StringBuilder("Select ").append("((s.saldo/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(s.parcela.dataRegistro,'MM')) and m.ano = s.parcela.valorDivida.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1)) ").append(" * coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(:data,'MM')) and m.ano = TO_NUMBER(TO_CHAR(:data,'yyyy')) and m.indiceEconomico.descricao = 'UFM'),1))").append(" from SituacaoParcelaValorDivida  s where s.parcela = :parcela ").append(" and s.id = (Select max(s.id) from SituacaoParcelaValorDivida s where s.parcela = :parcela)");

        Query q = em.createQuery(hql.toString()).setParameter("parcela", parcela).setParameter("data", data).setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public List<ValorDivida> buscaDividasCarne(Divida divida, Cadastro cadastro, Exercicio exercicio) {
        if ((divida == null || divida.getId() == null) || (cadastro == null || cadastro.getId() == null) || (exercicio == null || exercicio.getId() == null)) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT DISTINCT valordivida.* FROM valordivida ").append(" INNER JOIN calculo ON calculo.id = valordivida.calculo_id ").append(" INNER JOIN cadastro ON cadastro.id = calculo.cadastro_id ").append(" INNER JOIN parcelavalordivida parcela ON parcela.valordivida_id = valordivida.id ").append(" INNER JOIN situacaoparcelavalordivida situacao ON situacao.id = parcela.situacaoatual_id ").append(" WHERE valordivida.divida_id = :divida ").append(" AND valordivida.exercicio_id = :exercicio ").append(" AND cadastro.id = :cadastro ").append(" AND situacao.situacaoparcela = 'EM_ABERTO'").append(" AND parcela.vencimento >= sysdate");
        Query q = em.createNativeQuery(sql.toString(), ValorDivida.class);
        q.setParameter("divida", divida.getId());
        q.setParameter("cadastro", cadastro.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<ValorDivida> buscaDividasCarneAno(Divida divida, Cadastro cadastro, Exercicio exercicio) {
        if ((divida == null || divida.getId() == null) || (cadastro == null || cadastro.getId() == null) || (exercicio == null || exercicio.getId() == null)) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT DISTINCT valordivida.* FROM valordivida ").append(" INNER JOIN calculo ON calculo.id = valordivida.calculo_id ").append(" INNER JOIN cadastro ON cadastro.id = calculo.cadastro_id ").append(" INNER JOIN parcelavalordivida parcela ON parcela.valordivida_id = valordivida.id ").append(" INNER JOIN situacaoparcelavalordivida situacao ON situacao.id = parcela.situacaoatual_id ").append(" WHERE valordivida.divida_id = :divida ").append(" AND valordivida.exercicio_id = :exercicio ").append(" AND cadastro.id = :cadastro ").append(" AND situacao.situacaoparcela = 'EM_ABERTO' ").append(" AND parcela.vencimento >= sysdate AND ").append(" to_char(parcela.vencimento, 'yyyy') =:ano ");
        Query q = em.createNativeQuery(sql.toString(), ValorDivida.class);
        q.setParameter("divida", divida.getId());
        q.setParameter("cadastro", cadastro.getId());
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("ano", exercicio.getAno());
        return q.getResultList();
    }

    public String montaMensagemCarne(ValorDivida valorDivida) {
        String mensagem = "";
        String damsImprimir = "";
        String damsNaoImprimir = "";
        Date dataAtual = new Date();
        valorDivida = em.find(ValorDivida.class, valorDivida.getId());
        for (ParcelaValorDivida parcela : valorDivida.getParcelaValorDividas()) {
            if (getUltimaSituacao(parcela).getSituacaoParcela().equals(SituacaoParcela.EM_ABERTO)) {
                if (Util.getDataHoraMinutoSegundoZerado(parcela.getVencimento()).compareTo(Util.getDataHoraMinutoSegundoZerado(dataAtual)) >= 0) {
                    damsImprimir += parcela.getSequenciaParcela();
                }
            } else {
                damsNaoImprimir += parcela.getSequenciaParcela();
            }
        }
        if (!damsNaoImprimir.isEmpty()) {
            mensagem = "A Dívida selecionada possui as parcelas que não podem ser impressas: " + damsNaoImprimir;
        }
        mensagem += "Serão impressas as seguintes parcelas: " + damsImprimir;
        return mensagem;
    }

    public String montaMensagemCarne(ValorDivida valorDivida, Integer ano) {
        String mensagem = "";
        String damsImprimir = "";
        String damsNaoImprimir = "";
        Date dataAtual = new Date();
        valorDivida = em.find(ValorDivida.class, valorDivida.getId());
        for (ParcelaValorDivida parcela : valorDivida.getParcelaValorDividas()) {
            Calendar data = Calendar.getInstance();
            data.setTime(parcela.getVencimento());
            if (getUltimaSituacao(parcela).getSituacaoParcela().equals(SituacaoParcela.EM_ABERTO) && data.get(Calendar.YEAR) == ano) {
                if (Util.getDataHoraMinutoSegundoZerado(parcela.getVencimento()).compareTo(Util.getDataHoraMinutoSegundoZerado(dataAtual)) >= 0) {
                    damsImprimir += parcela.getSequenciaParcela();
                }
            } else {
                damsNaoImprimir += parcela.getSequenciaParcela();
            }
        }
        if (!damsNaoImprimir.isEmpty()) {
            mensagem = "A Dívida selecionada possui as parcelas que não podem ser impressas: " + damsNaoImprimir;
        }
        mensagem += "Serão impressas as seguintes parcelas: " + damsImprimir;
        return mensagem;
    }

    public void geraCarne(ValorDivida valorDividaSelecionado, Exercicio exercicio) throws JRException, IOException {
        valorDividaSelecionado = em.find(ValorDivida.class, valorDividaSelecionado.getId());
        Query q = em.createQuery("select vd.calculo from ValorDivida vd where vd = :vd").setParameter("vd", valorDividaSelecionado);
        Calculo calculo = null;
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            calculo = (Calculo) resultList.get(0);
        }
        String endereco = " ";
        String cpf = " ";
        String nome = " ";
        if (!calculo.getPessoas().isEmpty()) {
            Pessoa pessoa = calculo.getPessoas().get(0).getPessoa();
            endereco = montaEndereco(enderecoFacade.retornaPrimeiroEnderecoCorreioValido(pessoa));
            cpf = pessoa.getCpf_Cnpj();
            nome = pessoa.getNome();
        }
        new EmiteCarneGenerico().emiteCarne(valorDividaSelecionado.getId(), "2º Via", endereco, cpf, nome, calculo.getCadastro().getNumeroCadastro(), exercicio.getAno().toString(), getServiceDAM().isAmbienteHomologacao());
    }

    /*
     * Permite passar uma clausula where adicional
     */
    public void geraCarne(ValorDivida valorDividaSelecionado, Exercicio exercicio, String whereAdicional) throws JRException, IOException {

        valorDividaSelecionado = em.find(ValorDivida.class, valorDividaSelecionado.getId());

        Query q = em.createQuery("select vd.calculo from ValorDivida vd where vd = :vd").setParameter("vd", valorDividaSelecionado);

        Calculo calculo = null;
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            calculo = (Calculo) resultList.get(0);
        }

        String endereco = " ";
        String cpf = " ";
        String nome = " ";
        if (!calculo.getPessoas().isEmpty()) {
            Pessoa pessoa = calculo.getPessoas().get(0).getPessoa();
            endereco = montaEndereco(enderecoFacade.retornaPrimeiroEnderecoCorreioValido(pessoa));
            cpf = pessoa.getCpf_Cnpj();
            nome = pessoa.getNome();
        }

        new EmiteCarneGenerico().emiteCarne(valorDividaSelecionado.getId(), "2º Via", endereco, cpf, nome, calculo.getCadastro().getNumeroCadastro(), exercicio.getAno().toString(), whereAdicional, getServiceDAM().isAmbienteHomologacao());
    }

    public ServiceDAM getServiceDAM() {
        if (serviceDAM == null) {
            serviceDAM = (ServiceDAM) Util.getSpringBeanPeloNome("serviceDAM");
        }
        return serviceDAM;
    }

    public String montaEndereco(EnderecoCorreio end) {
        String retorno = "";
        if (end != null) {
            retorno += " " + end.getLogradouro() != null ? end.getLogradouro() + " , " : "";
            retorno += end.getNumero() != null ? end.getNumero() + ", " : "";
            retorno += end.getBairro() != null ? end.getBairro() + ", " : "";
            retorno += end.getCep() != null ? end.getCep() + ", " : "";
            retorno += end.getLocalidade() != null ? end.getLocalidade() + " - " : "";
            retorno += end.getUf() != null ? end.getUf() : "";

        }
        return retorno;
    }

    public String buscarNumeroTipoCadastroPorParcela(ParcelaValorDivida parcelaValorDivida) {
        StringBuilder sql = new StringBuilder(" SELECT CASE ")
            .append(" WHEN BCI.INSCRICAOCADASTRAL IS NOT NULL")
            .append(" THEN bci.inscricaocadastral ||  '(Cadastro Imobiliário) '")
            .append(" WHEN CMC.INSCRICAOCADASTRAL IS NOT NULL ")
            .append(" THEN cmc.inscricaocadastral  || '(Cadastro Econômico)' ")
            .append(" WHEN TO_CHAR(BCR.CODIGO) IS NOT NULL ")
            .append(" THEN BCR.CODIGO || '(Cadastro Rural) '")
            .append(" WHEN PJ.ID IS NOT NULL ")
            .append(" THEN PJ.CNPJ || ' - ' || PJ.RAZAOSOCIAL || '(Contribuinte Geral)' ")
            .append("ELSE PF.CPF || ' - ' || PF.NOME  || '(Contribuinte Geral)'")
            .append(" END")
            .append(" from ")
            .append(" parcelavalordivida parcela ")
            .append(" inner join valordivida vd on vd.id = parcela.valordivida_id ")
            .append(" inner join calculo on vd.calculo_id = calculo.id ")
            .append(" left join cadastroimobiliario bci on bci.id = calculo.cadastro_id ")
            .append(" left join cadastroeconomico cmc on cmc.id = calculo.cadastro_id ")
            .append(" left join cadastrorural bcr on bcr.id = calculo.cadastro_id ")
            .append(" left join calculopessoa calcpessoa on calcpessoa.calculo_id = calculo.id")
            .append(" left join pessoa pessoa on  calcpessoa.pessoa_id = pessoa.id")
            .append(" left join pessoafisica pf on pf.id = pessoa.id")
            .append(" left join pessoajuridica pj on pj.id = pessoa.id")
            .append(" where parcela.id = :idParcela");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idParcela", parcelaValorDivida.getId());
        q.setMaxResults(1);
        return (String) q.getResultList().get(0);

    }

    public String recuperaNumeroCadastroPorParcela(ParcelaValorDivida parcelaValorDivida) {
        StringBuilder sql = new StringBuilder("SELECT (coalesce(bci.inscricaocadastral, cmc.inscricaocadastral, to_char(bcr.codigo), '-')) ").append(" FROM ").append(" parcelavalordivida parcela ").append(" INNER JOIN valordivida vd ON vd.id = parcela.valordivida_id ").append(" INNER JOIN calculo ON vd.calculo_id = calculo.id ").append(" LEFT JOIN cadastroimobiliario bci ON bci.id = calculo.cadastro_id ").append(" LEFT JOIN cadastroeconomico cmc ON cmc.id = calculo.cadastro_id ").append(" LEFT JOIN cadastrorural bcr ON bcr.id = calculo.cadastro_id ").append(" WHERE parcela.id = :idParcela");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idParcela", parcelaValorDivida.getId());
        q.setMaxResults(1);
        return (String) q.getResultList().get(0);

    }


    public String recuperaNumerocadastroPorValorDivida(ValorDivida valorDivida) {
        StringBuilder sql = new StringBuilder("SELECT coalesce(bci.inscricaocadastral, cmc.inscricaocadastral, to_char(bcr.codigo), '-') AS inscricao FROM ").append(" valordivida vd ").append(" INNER JOIN calculo ON vd.calculo_id = calculo.id ").append(" LEFT JOIN cadastroimobiliario bci ON bci.id = calculo.cadastro_id ").append(" LEFT JOIN cadastroeconomico cmc ON cmc.id = calculo.cadastro_id ").append(" LEFT JOIN cadastrorural bcr ON bcr.id = calculo.cadastro_id ").append(" WHERE vd.id = :idValorDivida");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idValorDivida", valorDivida.getId());
        q.setMaxResults(1);
        return (String) q.getResultList().get(0);

    }


    private String montaStringSituacao(SituacaoParcela[] situacoes) {
        String retorno = "";
        for (SituacaoParcela situacaoParcela : situacoes) {
            retorno += "'" + situacaoParcela.name() + "',";
        }
        return retorno.substring(0, retorno.length() - 1);
    }

    public List<ParamParcelamento> completaParamParcelamentoParaAsDividasDasParcelasSelecionadas(String parte, List<Divida> dividas) {
        StringBuilder hql = new StringBuilder("select param from ParamParcelamento param").append(" inner join param.dividas dividaParcelamento").append(" inner join dividaParcelamento.divida divida").append(" where divida in (:dividas) and (lower(param.descricao) like :descricao or to_char(param.codigo) = :codigo)");
        Query consulta = em.createQuery(hql.toString());
        consulta.setParameter("descricao", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("codigo", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("dividas", dividas);
        return consulta.getResultList();
    }


    public ValorDivida recuperaValorDividaPorCalculo(Calculo calculo) {
        Query q = em.createQuery("from ValorDivida where calculo = :calculo");
        q.setParameter("calculo", calculo);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ValorDivida) resultList.get(0);
        }
        return null;
    }

    public List<ParcelaValorDivida> recuperarParcelasPelaIdentificacaoDaGuia(Integer identificacao) {
        String sql = "select pvd.* " + "       from parcelavalordivida pvd " + " inner join valordivida vd on vd.id = pvd.valordivida_id" + " inner join calculonfse nfse on nfse.id = vd.calculo_id " + " inner join calculo on calculo.id = nfse.id" + " where nfse.identificacaodaguia = :identificacao and calculo.cadastro_id is not null";

        return em.createNativeQuery(sql, ParcelaValorDivida.class).setParameter("identificacao", identificacao).getResultList();
    }

    public List<SituacaoParcelaValorDivida> getSituacoesDaParcela(ParcelaValorDivida pvd) {
        return em.createQuery("from SituacaoParcelaValorDivida where parcela = :pvd").setParameter("pvd", pvd).getResultList();
    }

    public ByteArrayOutputStream gerarImpressaoDAMPortal(List<DAM> dams) throws IOException {
        return gerarImpressaoDAM(dams, HistoricoImpressaoDAM.TipoImpressao.PORTAL, sistemaFacade.getUsuarioCorrente(), true);
    }

    public ByteArrayOutputStream gerarImpressaoDAM(List<DAM> dams, UsuarioSistema usuario, boolean gerarHistorico) throws IOException {
        return gerarImpressaoDAM(dams, HistoricoImpressaoDAM.TipoImpressao.SISTEMA, usuario, gerarHistorico);
    }

    public ByteArrayOutputStream gerarImpressaoDAMAgrupado(DAM dam, HistoricoImpressaoDAM.TipoImpressao origemImpressao, List<ResultadoParcela> parcelas, UsuarioSistema usuario, boolean gerarHistorico) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new ImprimeDAM().gerarBytesImpressaoDamCompostoViaApi(dam));
        return outputStream;
    }


    public ByteArrayOutputStream gerarImpressaoDAM(List<DAM> dams, HistoricoImpressaoDAM.TipoImpressao origemImpressao, UsuarioSistema usuario, boolean gerarHistorico) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new ImprimeDAM().gerarByteImpressaoDamUnicoViaApi(dams));
        return outputStream;
    }

    public List<ResultadoParcela> definirSeParcelasDevemSerSomadasNoTotalizadorDoDemonstrativo(List<ResultadoParcela> parcelas) {
        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();
        for (ResultadoParcela resultado : parcelas) {
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }

        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (resultadoParcela.getCotaUnica() && !resultadoParcela.getVencido() && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name()) || resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.PAGO.name()))) {
                    parcelasDaDivida.clear();
                    parcelasDaDivida.add(resultadoParcela);
                    break;
                } else {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }

        for (ResultadoParcela resultado : parcelas) {
            for (ResultadoParcela resultadoSoma : parcelasSoma) {
                if (resultado.getIdParcela().equals(resultadoSoma.getIdParcela())) {
                    if (resultado.getSituacaoEnumValue().isPago()) {
                        resultado.setSomaNoDemonstrativo(true);
                    } else {
                        boolean soma = false;
                        if ((!resultado.getVencido() && resultado.getCotaUnica())) {
                            soma = true;
                        }
                        if (!resultado.getCotaUnica()) {
                            soma = true;
                        }
                        if ((resultado.getCotaUnica() && (resultado.getSituacaoEnumValue().isInscritoDa() || resultado.getSituacaoEnumValue().isCancelado()))) {
                            soma = true;
                        }
                        if (soma) {
                            resultado.setSomaNoDemonstrativo(true);
                        }
                    }
                    break;
                }
            }
        }

        return parcelas;
    }

    public List<TotalSituacao> calculaTotalPorSituacao(List<ResultadoParcela> resultadoParcelas) {
        List<TotalSituacao> totalPorSituacao = Lists.newArrayList();
        BigDecimal totalEmAbertoAvencer = BigDecimal.ZERO;
        BigDecimal totalEmAbertoVencido = BigDecimal.ZERO;

        for (ResultadoParcela rp : resultadoParcelas) {
            if (rp.getSomaNoDemonstrativo()) {
                if (!rp.getVencido()) {
                    totalEmAbertoAvencer = totalEmAbertoAvencer.add(rp.getValorTotal());
                } else {
                    totalEmAbertoVencido = totalEmAbertoVencido.add(rp.getValorTotal());
                }
            }
        }
        TotalSituacao total;
        if (totalEmAbertoAvencer.compareTo(BigDecimal.ZERO) != 0) {
            total = new TotalSituacao();
            total.setSituacao("À vencer");
            total.setValor(totalEmAbertoAvencer);
            totalPorSituacao.add(total);
        }
        if (totalEmAbertoVencido.compareTo(BigDecimal.ZERO) != 0) {
            total = new TotalSituacao();
            total.setSituacao("Vencido");
            total.setValor(totalEmAbertoVencido);
            totalPorSituacao.add(total);
        }

        return totalPorSituacao;
    }


    public BigDecimal valorPagoPoCalculo(Calculo calculo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        if (parcelas != null && parcelas.size() > 0) {
            BigDecimal valorPago = BigDecimal.ZERO;
            for (ResultadoParcela resultadoParcela : parcelas) {
                valorPago = valorPago.add(resultadoParcela.getValorPago());
            }
            return valorPago;
        }

        return BigDecimal.ZERO;
    }

    public void ajustaValorDividaAtiva(Cadastro cadastro) {

        List<ItemInscricaoDividaAtiva> itens = em.createQuery("select item from ItemInscricaoDividaAtiva item where item.cadastro = :cadastro " + "and item.inscricaoDividaAtiva.exercicio.ano >= 2003").setParameter("cadastro", cadastro).getResultList();

        for (ItemInscricaoDividaAtiva item : itens) {
            BigDecimal valorOriginal = BigDecimal.ZERO;
            for (InscricaoDividaParcela inscricaoDividaParcela : item.getItensInscricaoDividaParcelas()) {
                ParcelaValorDivida pvd = inscricaoDividaParcela.getParcelaValorDivida();
                valorOriginal = valorOriginal.add(pvd.getValor());
            }

            ValorDivida vd = (ValorDivida) em.createQuery(" select vd from ValorDivida vd where vd.calculo = :calculo").setParameter("calculo", item).getResultList().get(0);
            for (ParcelaValorDivida pvd : vd.getParcelaValorDividas()) {
                BigDecimal imposto = BigDecimal.ZERO;
                BigDecimal taxa = BigDecimal.ZERO;

                for (ItemParcelaValorDivida ipvd : pvd.getItensParcelaValorDivida()) {
                    if (ipvd.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO)) {
                        imposto = imposto.add(ipvd.getValor());
                    }
                    if (ipvd.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                        taxa = taxa.add(ipvd.getValor());
                    }
                }

                List<ItemProcessoParcelamento> itensParcelamento = em.createQuery("select i from ItemProcessoParcelamento i where i.parcelaValorDivida = :pvd").setParameter("pvd", pvd).getResultList();
                for (ItemProcessoParcelamento itemPP : itensParcelamento) {
                    BigDecimal correcao = (itemPP.getImposto().add(itemPP.getTaxa())).subtract(imposto.add(taxa));
                    itemPP.setImposto(imposto);
                    itemPP.setTaxa(taxa);
                    itemPP.setCorrecao(correcao);
                    em.merge(itemPP);
                }
            }

        }

        throw new ExcecaoNegocioGenerica("teste");
    }


    private BigDecimal porcentagemDoValorTotal(BigDecimal aplicar, BigDecimal total) {
        return (aplicar.multiply(CEM).divide(total, 6, RoundingMode.HALF_EVEN)).divide(CEM, 6, RoundingMode.HALF_EVEN);
    }


    public List<ParcelaValorDivida> recuperarParcelasDoISS(Long id) {
        String sql = "select pvd.* " + "       from parcelavalordivida pvd" + " inner join valordivida vd on vd.id = pvd.valordivida_id" + " inner join calculoiss cn on cn.id = vd.calculo_id" + "      where cn.id = :id";

        return em.createNativeQuery(sql, ParcelaValorDivida.class).setParameter("id", id).getResultList();
    }

    public List<CalculoPessoa> recuperarCalculoPessoaSemCadastro(Pessoa pessoa) {
        String sql = " select cp.* " + "       from calculoPessoa cp " + "      inner join calculo on calculo.id = cp.calculo_id " + "where cp.pessoa_id =:idPessoa and calculo.cadastro_id is null ";
        Query query = em.createNativeQuery(sql, CalculoPessoa.class);
        query.setParameter("idPessoa", pessoa.getId());
        return query.getResultList();
    }

    public List<CalculoPessoa> recuperarCalculoPessoa(Pessoa pessoa) {
        String sql = " select cp.* " +
            "       from calculoPessoa cp " +
            "      inner join calculo on calculo.id = cp.calculo_id " +
            "where cp.pessoa_id =:idPessoa ";
        Query query = em.createNativeQuery(sql, CalculoPessoa.class);
        query.setParameter("idPessoa", pessoa.getId());
        return query.getResultList();
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<CalculoPessoa> recuperarCalculoPessoa(Cadastro cadastro, Pessoa pessoa) {
        String sql = " select cp.* " +
            "       from calculoPessoa cp " +
            "      inner join calculo on calculo.id = cp.calculo_id " +
            "where calculo.cadastro_id =:idCadastro and cp.pessoa_id =:idPessoa ";
        Query query = em.createNativeQuery(sql, CalculoPessoa.class);
        query.setParameter("idCadastro", cadastro.getId());
        query.setParameter("idPessoa", pessoa.getId());
        return query.getResultList();
    }

    public Cadastro recuperaCadastro(Long idCadastro) {
        return em.find(Cadastro.class, idCadastro);
    }

    public Pessoa recuperaPessoa(Long idCadastro) {
        return em.find(Pessoa.class, idCadastro);
    }

    public List<BigDecimal> recuperaIdsDividaExercicioPorParcelamento(Divida divida) {
        String sql = "select distinct coalesce(dv.id,ppd.divida_id) as divida " + "from paramparcelamento pp " + "inner join ParamParcelamentoDivida ppd on ppd.paramparcelamento_id = pp.id " + "left join divida dv on dv.divida_id = ppd.divida_id " + "where pp.dividaParcelamento_id = :idDivida" + "  and coalesce(dv.id,ppd.divida_id) <> :idDivida";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDivida", divida.getId());
        return q.getResultList();
    }

    public List<BigDecimal> recuperaIdsDividaExercicioPorDividaAtiva(Divida divida) {
        String sql = "select distinct id from divida where divida_id = :idDivida";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDivida", divida.getId());
        return q.getResultList();
    }

    public List<BigDecimal> recuperaIdsDividaAtivaPorDividaExercicio(Divida divida) {
        String sql = "select distinct divida_id from divida where id = :idDivida";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDivida", divida.getId());
        return q.getResultList();
    }

    public Future<List<ResultadoParcela>> executaConsulta(final ConsultaParcela consultaParcela,
                                                          final ConsultaDebitoControlador.ContadorConsulta contadorConsulta) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<ResultadoParcela>> submit = executorService.submit(new Callable<List<ResultadoParcela>>() {
            @Override
            public List<ResultadoParcela> call() throws Exception {
                consultaParcela.executaConsulta();
                contadorConsulta.contar();
                return consultaParcela.getResultados();
            }
        });
        executorService.shutdown();
        return submit;
    }

    public List<ObjetoCampoValor> carregarListaObjetoCampoValorParcelaSelecionada(ResultadoParcela rp) {
        List<ObjetoCampoValor> lista = new ArrayList<>();
        carregarCamposPadroes(lista, rp);
        switch (rp.getTipoCalculoEnumValue()) {
            case INSCRICAO_DA:
                carregarCamposDividaAtiva(lista, rp, rp.getIdParcela());
                break;
            case PARCELAMENTO:
                carregarCamposDividaAtivaParcelada(lista, rp.getIdParcela());
                break;
            case IPTU:
                carregarCamposDoProcessoCalculoIPTU(lista, rp);
                break;
            case MULTA_ACESSORIA:
                carregarCamposDaMultaAcessoria(lista, rp);
                break;
            case CANCELAMENTO_PARCELAMENTO:
                carregarCamposCancelamentoParcelamento(lista, rp, rp.getIdParcela());
                break;
            case CONTA_CORRENTE:
                carregarCamposContaCorrenteTributaria(lista, rp);
                break;
            case BLOQUEIO_JUDICIAL:
                carregarCamposParcelaResidualBloqueioJudicial(lista, rp);
                break;
        }
        carregarCamposDoProcessoDebitos(lista, rp);
        carregarCamposDoProcessoCompensacao(lista, rp);
        carregarCamposDoProcessoRestituicao(lista, rp);
        carregarCamposDoProcessoCreditoContaCorrente(lista, rp);
        carregarCamposDoProcessoDeDesconto(lista, rp);
        carregarCamposPagoPorParcelamento(lista, rp);
        carregarCamposSubvencao(lista, rp);
        carregarCamposAlvara(lista, rp);
        carregarCamposParcelaAguardandoPagtoBloqueioJudicial(lista, rp);
        carregarCamposProcessoDeProtesto(lista, rp);
        carregarCampoObservacao(lista, rp);
        return lista;
    }

    private void carregarCampoObservacao(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            String observacao = rp.getObservacaoCalculo();
            if (Calculo.TipoCalculo.ISS.equals(rp.getTipoCalculoEnumValue()) && Strings.isNullOrEmpty(observacao)) {
                observacao = notaFiscalFacade.getObservacaoDamNotaFiscal(rp.getIdCalculo());
            }
            lista.add(new ObjetoCampoValor("Observação", observacao));
        } catch (Exception e) {
            log.error("Erro ao carregar campo Observação", e);
        }
    }

    private void carregarCamposDoProcessoDebitos(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            ProcessoDebito processoDebito = processoDebitoFacade.buscarUltimoProcessoDebitoPorParcelValorDivida(rp.getIdParcela());
            if (processoDebito != null) {
                lista.add(new ObjetoCampoValor("Processo de Débito", processoDebito.getCodigo().toString() + "/" + processoDebito.getExercicio().toString() + " (" + processoDebito.getTipo().getDescricao() + ")"));
                lista.add(new ObjetoCampoValor("Protocolo do Processo de Débito", processoDebito.getNumeroProtocolo()));
            }
        } catch (Exception ex) {
            logger.error("Errro carregarCamposDoProcessoDebitos:", ex);
        }
    }

    private void carregarCamposDoProcessoCompensacao(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            if (SituacaoParcela.COMPENSACAO.equals(rp.getSituacaoEnumValue())) {
                Compensacao compensacao = compensacaoFacade.buscarUltimoProcessoCompensacaoPorParcelaValorDivida(rp.getIdParcela());
                if (compensacao != null) {
                    lista.add(new ObjetoCampoValor("Processo de Compensação", compensacao.getCodigo().toString() + "/" + compensacao.getExercicio().toString()));
                    lista.add(new ObjetoCampoValor("Protocolo do Processo de Compensação", compensacao.getNumeroProtocolo()));
                }
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposDoProcessoCompensacao:", ex);
        }
    }

    private void carregarCamposDoProcessoRestituicao(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            if (SituacaoParcela.RESTITUICAO.equals(rp.getSituacaoEnumValue())) {
                ItemRestituicao itemRestituicao = restituicaoFacade.buscarUltimoProcessoRestituicaoPorParcelaValorDivida(rp.getIdParcela());
                if (itemRestituicao != null) {
                    lista.add(new ObjetoCampoValor("Processo de Restituição", itemRestituicao.getRestituicao().getCodigo().toString() + "/" + itemRestituicao.getRestituicao().getExercicio().toString()));
                    lista.add(new ObjetoCampoValor("Protocolo do Processo de Restituição", itemRestituicao.getRestituicao().getNumProtocolo()));
                    lista.add(new ObjetoCampoValor("Valor Restituído", Util.formataValor(itemRestituicao.getDiferencaAtualizada())));
                }
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposDoProcessoRestituicao:", ex);
        }
    }

    private void carregarCamposDoProcessoCreditoContaCorrente(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            if (rp.isPago()) {
                CreditoContaCorrente creditoContaCorrente = processoCreditoContaCorrenteFacade.buscarUltimoProcessoPorParcelValorDivida(rp.getIdParcela());
                if (creditoContaCorrente != null) {
                    lista.add(new ObjetoCampoValor("Processo de Crédito em Conta Corrente", creditoContaCorrente.getCodigo().toString() + "/" + creditoContaCorrente.getExercicio().toString()));
                    lista.add(new ObjetoCampoValor("Protocolo do Processo de Crédito em Conta Corrente", creditoContaCorrente.getNumeroProtocolo()));
                }
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposDoProcessoCreditoContaCorrente:", ex);
        }
    }

    private void carregarCamposDoProcessoDeDesconto(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        if (rp.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            try {
                LancamentoDesconto lancamentoDesconto = lancamentoDescontoFacade.buscarUltimoProcessoDescontoPorParcelaValorDivida(rp.getIdParcela());
                if (lancamentoDesconto != null) {
                    lista.add(new ObjetoCampoValor("Processo de Desconto", lancamentoDesconto.getCodigo().toString() + "/" + lancamentoDesconto.getExercicio().toString()));
                    lista.add(new ObjetoCampoValor("Protocolo do Processo de Desconto", lancamentoDesconto.getNumeroProtocolo()));
                }
            } catch (Exception ex) {
                logger.error("Erro carregarCamposDoProcessoCreditoContaCorrente:", ex);
            }
        }
    }

    private void carregarCamposPagoPorParcelamento(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        if (SituacaoParcela.PAGO_PARCELAMENTO.equals(rp.getSituacaoEnumValue())) {
            CancelamentoParcelamento cancelamento = cancelamentoParcelamentoFacade.buscarCancelamentoDaParcelaPaga(rp.getIdParcela());
            if (cancelamento != null) {
                lista.add(new ObjetoCampoValor("Processo de Cancelamento", (cancelamento.getCodigo() != null ? cancelamento.getCodigo() + "/" + cancelamento.getExercicio().getAno() : "-")));
                lista.add(new ObjetoCampoValor("Protocolo de Cancelamento", (cancelamento.getNumeroProtocolo() != null ? cancelamento.getNumeroProtocolo() + "/" + cancelamento.getAnoProtocolo() : "-")));
            }
        }
    }

    private void carregarCamposSubvencao(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        if (SituacaoParcela.PAGO_SUBVENCAO.equals(rp.getSituacaoEnumValue()) || SituacaoParcela.AGUARDANDO_PAGAMENTO_SUBVENCAO.equals(rp.getSituacaoEnumValue()) || Calculo.TipoCalculo.PAGAMENTO_SUBVENCAO.equals(rp.getTipoCalculoEnumValue())) {
            List<SubvencaoProcesso> subvencoes = subvencaoProcessoFacade.buscarSubvencoesDaParcela(rp.getIdParcela());
            Long idEmpresa = null;
            if (subvencoes != null && subvencoes.isEmpty()) {
                Object[] obj = subvencaoProcessoFacade.buscarSuvencaoEfetivadaParcelaResidual(rp.getIdParcela());
                if (obj != null) {
                    SubvencaoProcesso subvencaoProcesso = em.find(SubvencaoProcesso.class, ((BigDecimal) obj[0]).longValue());
                    idEmpresa = ((BigDecimal) obj[1]).longValue();
                    subvencoes.add(subvencaoProcesso);
                }
            }
            if (subvencoes != null) {
                for (SubvencaoProcesso subvencaoProcesso : subvencoes) {
                    lista.add(new ObjetoCampoValor("Processo de Subvenção", subvencaoProcesso.getNumeroDoProcesso().toString()));
                    lista.add(new ObjetoCampoValor("Mês da Subvenção", subvencaoProcesso.getMes().getDescricao()));
                    lista.add(new ObjetoCampoValor("Ano da Subvenção", subvencaoProcesso.getExercicio().getAno().toString()));
                    lista.add(new ObjetoCampoValor("Tipo de Passageiro da Subvenção", subvencaoProcesso.getTipoPassageiro().getDescricao()));
                    lista.add(new ObjetoCampoValor("Data do Processo de Subvenção", DataUtil.getDataFormatada(subvencaoProcesso.getDataLancamento())));
                    if (idEmpresa != null) {
                        for (SubvencaoEmpresas subvencaoEmpresa : subvencaoProcesso.getSubvencaoEmpresas()) {
                            if (subvencaoEmpresa.getId().equals(idEmpresa)) {
                                lista.add(new ObjetoCampoValor("Valor Subvencionado", subvencaoEmpresa.getValorSubvencionado() != null ? Util.reaisToString(subvencaoEmpresa.getValorSubvencionado()) : ""));
                                break;
                            }
                        }
                    }

                }
            }
        }
    }

    private void carregarCamposDoProcessoCalculoIPTU(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            ProcessoCalculo processoCalculo = processoCalculoIPTUFacade.buscarProcessoCalculoIptuPorIdCalculo(rp.getIdCalculo());
            if (processoCalculo != null && processoCalculo.getNumeroProtocolo() != null) {
                lista.add(new ObjetoCampoValor("Protocolo", processoCalculo.getNumeroProtocolo() + (processoCalculo.getAnoProtocolo() != null ? "/" + processoCalculo.getAnoProtocolo() : "")));
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposDoProcessoCalculoIPTU: ", ex);
        }
    }

    private void carregarCamposContaCorrenteTributaria(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            ContaCorrenteTributaria contaCorrenteTributaria = contaCorrenteTributariaFacade.buscarContaCorrentePorCalculo(rp.getIdCalculo());
            if (contaCorrenteTributaria != null) {
                lista.add(new ObjetoCampoValor("Conta Corrente Tributária", contaCorrenteTributaria.getCodigo().toString()));
                lista.add(new ObjetoCampoValor("CPF/CNPJ da Conta Corrente Tributária", contaCorrenteTributaria.getPessoa().getCpf_Cnpj()));
            }
            Compensacao compensacao = contaCorrenteTributariaFacade.buscarCompensacaoPorCalculo(rp.getIdCalculo());
            if (compensacao != null) {
                lista.add(new ObjetoCampoValor("Processo de Compensação", compensacao.getCodigo().toString() + "/" + compensacao.getExercicio().toString()));
                lista.add(new ObjetoCampoValor("Motivo do Processo de Compensação", compensacao.getMotivo()));
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposContaCorrenteTributaria: ", ex);
        }
    }

    private void carregarCamposDaMultaAcessoria(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        try {
            List<ItemLancamentoMultaAcessoria> itens = lancamentoMultaAcessoriaFacade.buscarItensMultaAcessorioPorIdCalculo(rp.getIdCalculo());
            if (itens != null) {
                for (ItemLancamentoMultaAcessoria item : itens) {
                    lista.add(new ObjetoCampoValor("Multa", item.getMultaFiscalizacao().getDescricao()));
                    lista.add(new ObjetoCampoValor("Observação da Multa", item.getObservacao()));
                }
            }
        } catch (Exception ex) {
            logger.error("Erro carregarCamposDaMultaAcessoria: ", ex);
        }
    }

    private void carregarCamposPadroes(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        lista.add(new ObjetoCampoValor("Identificação", rp.getIdParcela().toString() + " " + rp.getSituacaoDescricaoEnum()));
        lista.add(new ObjetoCampoValor(rp.getTipoCadastroTributarioEnumValue().getDescricaoLonga(), rp.getCadastro() + " - " + (rp.getIdPessoa() != null ? pessoaFacade.buscarNomePessoa(rp.getIdPessoa()) : "")));
        lista.add(new ObjetoCampoValor("Referência", rp.getReferencia()));
        lista.add(new ObjetoCampoValor("Exercício", rp.getExercicio().toString()));
        lista.add(new ObjetoCampoValor("Parcela", rp.getParcela() + " - " + rp.getSiglaTipoDeDebito() + " " + rp.getSd().toString()));
        lista.add(new ObjetoCampoValor("Tipo do Cálculo", rp.getTipoCalculoEnumValue().getDescricao()));
        lista.add(new ObjetoCampoValor("Data de Lançamento", DataUtil.getDataFormatada(rp.getEmissao(), "dd/MM/yyyy")));
        lista.add(new ObjetoCampoValor("Data de Vencimento", DataUtil.getDataFormatada(rp.getVencimento(), "dd/MM/yyyy")));
        lista.add(new ObjetoCampoValor("Data de Prescrição", DataUtil.getDataFormatada(rp.getPrescricao(), "dd/MM/yyyy")));
        lista.add(new ObjetoCampoValor("Valor Total", Util.reaisToString(rp.getValorTotal())));
        lista.add(new ObjetoCampoValor("DAM", damFacade.recuperaNumeroDamParcela(rp.getIdParcela())));
        IsencaoCadastroImobiliario isencao = processoIsencaoIPTUFacade.buscarIsencaoPorIdCalculo(rp.getIdCalculo());
        if (isencao != null) {
            lista.add(new ObjetoCampoValor("Processo de Isenção", isencao.getProcessoIsencaoIPTU().getNumeroProcessoComExercicio()));
            lista.add(new ObjetoCampoValor("Categoria da Isenção", isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getDescricao()));
            lista.add(new ObjetoCampoValor("Tipo da Isenção", isencao.getTipoLancamentoIsencao().getDescricao()));
        }
    }

    private void carregarCamposDividaAtiva(List<ObjetoCampoValor> lista, ResultadoParcela rp, Long idParcela) {
        List<CertidaoDividaAtiva> cdas = certidaoDividaAtivaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
        if (!cdas.isEmpty()) {
            for (CertidaoDividaAtiva cda : cdas) {
                lista.add(new ObjetoCampoValor("Certidão Dívida Ativa", cda.getNumero().toString() + "/" + cda.getExercicio().getAno().toString()));
                ProcessoJudicial ajuizamento = certidaoDividaAtivaFacade.processoAjuizamentoDaCda(cda);
                if (ajuizamento != null) {
                    lista.add(new ObjetoCampoValor("Processo", ajuizamento.getNumeroProcessoForum()));
                }
            }
        } else {
            lista.add(new ObjetoCampoValor("Certidão Dívida Ativa", "Certidão não encontrada."));
        }
        carregarCamposParcelamento(lista, rp);
    }

    private void carregarCamposParcelamento(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        List<ProcessoParcelamento> parcelamentos = processoParcelamentoFacade.buscarParcelamentosDaParcela(rp.getIdParcela());
        int qtdeParcelamentos = 0;
        boolean temRefis = false;
        for (ProcessoParcelamento parcelamento : parcelamentos) {
            qtdeParcelamentos++;
            if (parcelamento.getValorTotalDesconto().compareTo(BigDecimal.ZERO) > 0) {
                temRefis = true;
            }
        }
        lista.add(new ObjetoCampoValor("Quantidade de Parcelamentos", qtdeParcelamentos > 0 ? qtdeParcelamentos + "" : "-"));
        lista.add(new ObjetoCampoValor("Refis", temRefis ? "Sim" : "Não"));
    }

    private void carregarCamposCancelamentoParcelamento(List<ObjetoCampoValor> lista, ResultadoParcela rp, Long idResultadoParcela) {
        Long idParcela = buscarIdParcelaOriginalDaParcelaDoCancelamento(idResultadoParcela);

        if (idParcela != null) {
            List<CertidaoDividaAtiva> cdas = certidaoDividaAtivaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
            if (!cdas.isEmpty()) {
                for (CertidaoDividaAtiva cda : cdas) {
                    lista.add(new ObjetoCampoValor("Certidão Dívida Ativa", cda.getNumero().toString() + "/" + cda.getExercicio().getAno().toString()));
                    ProcessoJudicial ajuizamento = certidaoDividaAtivaFacade.processoAjuizamentoDaCda(cda);
                    if (ajuizamento != null) {
                        lista.add(new ObjetoCampoValor("Processo", ajuizamento.getNumeroProcessoForum()));
                    }
                }
            }
            carregarCamposParcelamentoCancelado(lista, rp, idParcela);
        }
    }

    public Long buscarIdParcelaOriginalDaParcelaDoCancelamento(Long idBusca) {
        Long idParcela = cancelamentoParcelamentoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(idBusca);
        ResultadoParcela resultadoParcela = montarResultadoParcelaIdAndTipoCalculo(idParcela);

        Long idParcelaOriginal = null;

        if (resultadoParcela != null) {
            if (Calculo.TipoCalculo.CANCELAMENTO_PARCELAMENTO.name().equals(resultadoParcela.getTipoCalculo())) {
                idParcelaOriginal = buscarIdParcelaOriginalDaParcelaDoCancelamento(resultadoParcela.getIdParcela());
            } else {
                idParcelaOriginal = resultadoParcela.getIdParcela();
            }
        }
        return idParcelaOriginal;
    }

    public ResultadoParcela montarResultadoParcelaIdAndTipoCalculo(Long idParcela) {
        if (idParcela != null) {
            String sql = " select pvd.id, calc.tipocalculo from calculo calc " + " inner join valordivida vd on calc.id = vd.calculo_id " + " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " + " where pvd.id = :idParcela ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcela", idParcela);

            List<Object[]> resultados = q.getResultList();

            if (resultados != null && !resultados.isEmpty()) {
                ResultadoParcela resultadoParcela = new ResultadoParcela();
                Object[] object = resultados.get(0);

                resultadoParcela.setIdParcela(((BigDecimal) object[0]).longValue());
                resultadoParcela.setTipoCalculo((String) object[1]);

                return resultadoParcela;
            }
        }
        return null;
    }

    private void carregarCamposParcelamentoCancelado(List<ObjetoCampoValor> lista, ResultadoParcela rp, Long idParcelaOriginal) {
        List<ProcessoParcelamento> parcelamentos = processoParcelamentoFacade.buscarParcelamentosDaParcela(rp.getIdParcela());
        parcelamentos.addAll(processoParcelamentoFacade.buscarParcelamentosDaParcela(idParcelaOriginal));
        int qtdeParcelamentos = 0;
        boolean temRefis = false;
        for (ProcessoParcelamento parcelamento : parcelamentos) {
            qtdeParcelamentos++;
            if (parcelamento.getValorTotalDesconto().compareTo(BigDecimal.ZERO) > 0) {
                temRefis = true;
            }
        }
        lista.add(new ObjetoCampoValor("Quantidade de Parcelamentos", qtdeParcelamentos > 0 ? qtdeParcelamentos + "" : "-"));
        lista.add(new ObjetoCampoValor("Refis", temRefis ? "Sim" : "Não"));
    }

    private void carregarCamposDividaAtivaParcelada(List<ObjetoCampoValor> lista, Long idParcela) {
        VOProcessoParcelamento pp = processoParcelamentoFacade.recuperarProcessoParcelamento(idParcela);
        if (pp != null) {
            List<CertidaoDividaAtiva> cdas = certidaoDividaAtivaFacade.certidoesDoParcelamento(pp.getId(), false);
            List<CertidaoDividaAtiva> cdasInseridas = new ArrayList<>();
            if (!cdas.isEmpty()) {
                for (CertidaoDividaAtiva cda : cdas) {
                    if (!cdasInseridas.contains(cda)) {
                        lista.add(new ObjetoCampoValor("Certidão Dívida Ativa", cda.getNumero().toString() + "/" + cda.getExercicio().getAno().toString()));
                        ProcessoJudicial ajuizamento = certidaoDividaAtivaFacade.processoAjuizamentoDaCda(cda);
                        if (ajuizamento != null) {
                            lista.add(new ObjetoCampoValor("Processo", ajuizamento.getNumeroProcessoForum()));
                        }
                        cdasInseridas.add(cda);
                    }
                }
            }
        }
    }

    private void carregarCamposAlvara(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        if (rp.getTipoCalculoEnumValue().equals(TipoCalculoDTO.ALVARA)) {
            ProcessoCalculoAlvara processoCalculoAlvara = calculoAlvaraFacade.buscarProcessoCalculoPeloIdParcela(rp.getIdParcela());
            if (processoCalculoAlvara != null) {
                lista.add(new ObjetoCampoValor("Calculo de Alvará", processoCalculoAlvara.getCodigo() + "/" + processoCalculoAlvara.
                    getExercicio().getAno()));
            }
        }
    }

    private void carregarCamposParcelaAguardandoPagtoBloqueioJudicial(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        if (rp.isParcelaBloqueioJudicial()) {
            adicionarInformacoesBloqueioJudicialDeDebitos(lista, bloqueioJudicialFacade.buscarProcessoBloqueioJudicialPeloIdParcelaOriginal(rp.getIdParcela()));
        }
    }

    private void carregarCamposParcelaResidualBloqueioJudicial(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        adicionarInformacoesBloqueioJudicialDeDebitos(lista, bloqueioJudicialFacade.buscarProcessoBloqueioJudicialPeloIdParcela(rp.getIdParcela()));

        ResultadoParcela resultadoParcela = bloqueioJudicialFacade.buscarCalculoParcelaOriginalInfoConsultaDeDebitos(rp.getIdCalculo());
        if (resultadoParcela != null) {
            if (Calculo.TipoCalculo.INSCRICAO_DA.name().equals(resultadoParcela.getTipoCalculo())) {
                carregarCamposDividaAtiva(lista, rp, resultadoParcela.getIdParcela());
            } else if (Calculo.TipoCalculo.PARCELAMENTO.name().equals(resultadoParcela.getTipoCalculo())) {
                carregarCamposDividaAtivaParcelada(lista, resultadoParcela.getIdParcela());
            } else if (Calculo.TipoCalculo.CANCELAMENTO_PARCELAMENTO.name().equals(resultadoParcela.getTipoCalculo())) {
                carregarCamposCancelamentoParcelamento(lista, rp, resultadoParcela.getIdParcela());
            }
        }
    }

    private void adicionarInformacoesBloqueioJudicialDeDebitos(List<ObjetoCampoValor> lista, BloqueioJudicial bloqueioJudicial) {
        if (bloqueioJudicial != null) {
            lista.add(new ObjetoCampoValor("Processo de Bloqueio Judicial de Débitos", bloqueioJudicial.getCodigo() + "/" + bloqueioJudicial.getExercicio().getAno()));
            lista.add(new ObjetoCampoValor("Motivo", bloqueioJudicial.getMotivo()));
        }
    }

    private void carregarCamposProcessoDeProtesto(List<ObjetoCampoValor> lista, ResultadoParcela rp) {
        ProcessoDeProtesto processoDeProtesto = processoDeProtestoFacade.buscarProcessoAtivoDaParcela(rp.getIdParcela());
        if (processoDeProtesto != null) {
            lista.add(new ObjetoCampoValor("Processo de Protesto", processoDeProtesto.getCodigo().toString() + "/" + processoDeProtesto.getExercicio().getAno().toString()));
        }
    }

    public ProcessoDeProtesto buscarProcessoAtivoDaParcela(Long idParcela) {
        return processoDeProtestoFacade.buscarProcessoAtivoDaParcela(idParcela);
    }

    public Long getQuantidadeParcelasValorDivida(ValorDivida valorDivida, OpcaoPagamento opcaoPagamento) {
        String sql = "SELECT coalesce(P.QUANTIDADEPARCELA, COUNT(p.id)) " + "         FROM ParcelaValorDivida p " + "        INNER JOIN valordivida v ON v.id = p.valordivida_id " + "        WHERE v.id = :idValorDivida " + "          AND P.OPCAOPAGAMENTO_ID = :idOpcaoPagamento " + "     GROUP BY P.QUANTIDADEPARCELA";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idValorDivida", valorDivida.getId());
        q.setParameter("idOpcaoPagamento", opcaoPagamento.getId());
        return ((BigDecimal) q.getSingleResult()).longValue();

    }

    public List<CalculoPessoa> buscarCalculoPessoaPorPessoaAndCadastros(Pessoa pessoa, String idsCadastros) {
        String sql = " select cp.* " +
            "    from calculoPessoa cp " +
            "   inner join calculo on calculo.id = cp.calculo_id " +
            " where cp.pessoa_id =:idPessoa ";
        if (idsCadastros != null && !idsCadastros.isEmpty()) {
            sql += " and (calculo.cadastro_id is null or calculo.cadastro_id in (" + idsCadastros + ")) ";
        }
        Query query = em.createNativeQuery(sql, CalculoPessoa.class);
        query.setParameter("idPessoa", pessoa.getId());
        return query.getResultList();
    }

    public String buscarEstadoDaParcela(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return "DÍVIDA ATIVA";
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return "DÍVIDA ATIVA AJUIZADA";
        }
        return "DO EXERCÍCIO";
    }

    public void alterarVencimentoParcelaValorDivida(ParcelaValorDivida parcelaValorDivida, Date vencimento) {
        alterarVencimentoDescontoItemParcela(parcelaValorDivida, vencimento);
        parcelaValorDivida.setVencimento(vencimento);
        em.merge(parcelaValorDivida);
    }

    public List<ParcelaValorDivida> recuperarParcelasDoCalculo(Calculo calculo) {
        return recuperarParcelasDoCalculo(calculo.getId());
    }

    public List<ParcelaValorDivida> recuperarParcelasDoCalculo(Long idCalculo) {
        String sql = " select pvd.* from parcelavalordivida pvd" + "  inner join valordivida vd on vd.id = pvd.valordivida_id " + " where vd.calculo_id = :id_calculo ";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("id_calculo", idCalculo);
        return q.getResultList();
    }

    public List<ParcelaValorDivida> carregarItensDasParcelas(List<ParcelaValorDivida> parcelas) {
        for (ParcelaValorDivida pvd : parcelas) {
            pvd.getItensParcelaValorDivida().size();
        }
        return parcelas;
    }

    public List<ParcelaValorDivida> carregarSituacoesDasParcelas(List<ParcelaValorDivida> parcelas) {
        for (ParcelaValorDivida pvd : parcelas) {
            pvd.getSituacoes().size();
        }
        return parcelas;
    }

    public BigDecimal buscarValorDaParcelaPorTipoDeTributo(ParcelaValorDivida parcelaValorDivida, Tributo.TipoTributo tipoTributo) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
            if (tipoTributo.equals(itemParcelaValorDivida.getItemValorDivida().getTributo().getTipoTributo())) {
                toReturn = toReturn.add(itemParcelaValorDivida.getValor());
            }
        }

        return toReturn;
    }

    public BigDecimal buscarValorDoDebitoPorTipoDeTributo(Calculo calculo, Tributo.TipoTributo tipoTributo) {
        String sql = "select COALESCE(sum(ivd.valor),0) from calculo c " + "inner join valordivida vd on vd.calculo_id = c.id " + "inner join itemvalordivida ivd on ivd.valordivida_id = vd.id " + "inner join tributo tr on tr.id = ivd.tributo_id " + "where c.id = :idCalculo " + "  and ivd.isento = :isento " + "  and tr.tipotributo = :tipoTributo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", calculo.getId());
        q.setParameter("isento", 0);
        q.setParameter("tipoTributo", tipoTributo.name());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarValorDeDescontoDaParcela(ParcelaValorDivida parcelaValorDivida) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
            for (DescontoItemParcela desconto : itemParcelaValorDivida.getDescontos()) {
                toReturn = toReturn.add(desconto.getDesconto());
            }
        }

        return toReturn;
    }

    public ParcelaValorDivida buscarParcelaDoValorResidualDaParcela(ParcelaValorDivida parcelaValorDivida) {
        String sql = " select pvd.* from cancelamentoparcelamento cp " + " inner join valordivida vd on vd.calculo_id = cp.id " + " inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " + " where cp.parcelavalordivida_id =:id_parcela ";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("id_parcela", parcelaValorDivida.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ParcelaValorDivida) resultList.get(0);
        }
        return null;
    }

    private void alterarVencimentoDescontoItemParcela(ParcelaValorDivida parcelaValorDivida, Date vencimento) {
        List<DescontoItemParcela> descontos = buscarDescontoItemParcelaVigentes(parcelaValorDivida);
        if (descontos != null) {
            for (DescontoItemParcela desconto : descontos) {
                desconto.setFim(vencimento);
                em.merge(desconto);
            }


        }
    }

    private List<DescontoItemParcela> buscarDescontoItemParcelaVigentes(ParcelaValorDivida parcelaValorDivida) {
        String sql = " select desconto.* from itemparcelavalordivida item " + " inner join descontoitemparcela desconto on desconto.ITEMPARCELAVALORDIVIDA_ID = item.id " + " where item.PARCELAVALORDIVIDA_ID = :parcela " + " and coalesce(desconto.fim, :vencimento) = :vencimento ";
        Query q = em.createNativeQuery(sql, DescontoItemParcela.class);
        q.setParameter("parcela", parcelaValorDivida.getId());
        q.setParameter("vencimento", parcelaValorDivida.getVencimento());
        return q.getResultList();
    }


    //    @Asynchronous
//    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public Future<List<ResultadoParcela>> executarConsultaDeParcelas(final AssistenteBarraProgresso assistente, final ConsultaDebitoControlador consultaDebitoControlador, final ConsultaParcela consultaParcela, final ConsultaDebitoControlador.ContadorConsulta consultaContador) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<ResultadoParcela>> submit = executorService.submit(new Callable<List<ResultadoParcela>>() {
            @Override
            public List<ResultadoParcela> call() throws Exception {
                List<ResultadoParcela> listaRetorno = Lists.newArrayList();
                List<BigDecimal> ids = singletonConsultaDebitos.getProximosCalcular(consultaDebitoControlador.getIdentificadorConsulta());
                while (ids.size() > 0) {
                    ConsultaParcela consultaCadastro = new ConsultaParcela();
                    consultaCadastro.copiarFiltros(consultaParcela).copiarOrdenacoes(consultaParcela);

                    consultaCadastro.removerParametro(ConsultaParcela.Campo.CADASTRO_ID);
                    consultaCadastro.removerParametro(ConsultaParcela.Campo.PESSOA_ID);

                    consultaCadastro.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, ids);
                    consultaCadastro.addComplementoDoWhere(consultaParcela.getComplementoWhere());
                    listaRetorno.addAll(consultaCadastro.executaConsulta().getResultados());
                    consultaContador.contar(ids.size());
                    assistente.contar(ids.size());
                    ids = singletonConsultaDebitos.getProximosCalcular(consultaDebitoControlador.getIdentificadorConsulta());
                }
                return listaRetorno;
            }
        });
        executorService.shutdown();
        return submit;
    }

    public ProcessoDebitoFacade getProcessoDebitoFacade() {
        return processoDebitoFacade;
    }

    public List<ParcelaValorDivida> buscarParcelaValorDividaDoResultadoParcela(List<ResultadoParcela> resultadoParcelas) {
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : resultadoParcelas) {
            parcelas.add(recuperaParcela(resultadoParcela.getIdParcela()));
        }
        return parcelas;
    }

    public Calculo.TipoCalculo buscarTipoCalculoDaParcela(Long idParcela) {
        try {
            String sql = "select calculo.tipoCalculo from ParcelaValorDivida pvd " + "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " + "inner join Calculo on calculo.id = vd.calculo_id " + "where pvd.id = :idParcela";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcela", idParcela);
            return Calculo.TipoCalculo.valueOf((String) q.getSingleResult());
        } catch (Exception ex) {
            logger.error("Erro ao buscar o tipo de calculo da parcela {}: {}", idParcela, ex);
            return null;
        }
    }

    public String montarNumeroParcela(ParcelaValorDivida parcela) {
        String sql = "select PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(PVD.valordivida_ID, OP.ID, OP.PROMOCIONAL, PVD.SEQUENCIAPARCELA) \n" + "from parcelavalordivida pvd \n" + "inner join opcaopagamento op on op.id = pvd.opcaopagamento_id\n" + "where pvd.id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getId());
        return (String) q.getResultList().get(0);
    }

    public Long buscarIdCalculoDaParcela(ParcelaValorDivida parcelaValorDivida) {
        String sql = "select vd.calculo_id from ParcelaValorDivida pvd " + " inner join ValorDivida vd on vd.id = pvd.valorDivida_id " + " where pvd.id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcelaValorDivida.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return ((BigDecimal) resultList.get(0)).longValue();
        }
        return null;
    }

    public void enviarEmailsDAMs(List<DamDTO> dams, String[] emailsSeparados) {
        List<DAM> collect = Lists.newArrayList();
        for (DamDTO dam : dams) {
            collect.add(damFacade.recuperar(dam.getId()));
        }
        enviarEmailsDAMs(collect, emailsSeparados, false, "Enviado pelo sistema de ISS online", Lists.<ResultadoParcela>newArrayList());
    }


    public void enviarEmailsDAMs(List<DAM> dams, String[] emailsSeparados, Boolean agrupado, String mensagemEmail, List<ResultadoParcela> selecionados) {
        try {
            String assunto = "Prefeitura Municipal de " + configuracaoTributarioFacade.retornaUltimo().getCidade().getNome() +
                " / " + configuracaoTributarioFacade.retornaUltimo().getCidade().getUf() + " - Documento de Arrecadação";
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (!agrupado) {
                outputStream.write(imprimeDAM.gerarByteImpressaoDamUnicoViaApi(Lists.newArrayList(dams)));
            } else {
                outputStream.write(imprimeDAM.gerarBytesImpressaoDamCompostoViaApi(dams.get(0)));
            }
            AnexoEmailDTO anexoEmailDTO = new AnexoEmailDTO(outputStream, "dam", "application/pdf", ".pdf");
            EmailService.getInstance().enviarEmail(emailsSeparados, assunto, mensagemEmail, anexoEmailDTO);
        } catch (ExcecaoNegocioGenerica e) {
            log.error("Ocorreu um erro ao enviar o e-mail: {}", e);
            throw e;
        } catch (Exception e) {
            log.error("Ocorreu um erro ao enviar o e-mail: {}", e);
        }
    }


    public List<ResultadoParcela> buscarParcelasCalculo(Calculo calculo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados();
    }

    public List<PixDTO> gerarQrCodePix(List<DAM> dans) {
        return pixFacade.gerarQrCodePIX(dans);
    }

    public List<ResultadoParcela> buscarResultadosParcelaDoDam(DAM dam) {
        List<Long> idsParcela = Lists.newArrayList();
        for (ItemDAM itemDAM : dam.getItens()) {
            idsParcela.add(itemDAM.getParcela().getId());
        }
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_ID, br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IN, idsParcela);
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados();
    }

    public List<ParcelaValorDivida> buscarParcelasPorIdDam(Long idDam) {
        String sql = " select pvd.* " +
            " from parcelavalordivida pvd " +
            " inner join itemdam idam on pvd.id = idam.parcela_id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where idam.dam_id = :idDam ";

        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("idDam", idDam);

        List<ParcelaValorDivida> parcelas = q.getResultList();

        if (parcelas != null) {
            for (ParcelaValorDivida parcela : parcelas) {
                Hibernate.initialize(parcela.getSituacoes());
            }
        }
        return parcelas != null ? parcelas : Lists.newArrayList();
    }

    public List<ParcelaValorDivida> buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(ParcelaValorDivida parcela, SituacaoParcela situacaoParcela) {
        String sql = "select pvd.* from parcelavalordivida pvd "
            + " inner join situacaoparcelavalordivida situacao on situacao.id = pvd.situacaoatual_id "
            + " where pvd.valordivida_id = :valorDivida and pvd.opcaopagamento_id <> :opcaoPagamento "
            + " and situacao.situacaoparcela = :situacao";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("valorDivida", parcela.getValorDivida().getId());
        q.setParameter("opcaoPagamento", parcela.getOpcaoPagamento().getId());
        q.setParameter("situacao", situacaoParcela.name());
        return q.getResultList();
    }


    public void baixarParcelaValorDivida(ParcelaValorDivida parcela) throws Exception {
        SituacaoParcelaValorDivida situacaoAnterior = getUltimaSituacao(parcela);
        SituacaoParcelaValorDivida novaSituacao = new SituacaoParcelaValorDivida(SituacaoParcela.PAGO, parcela, situacaoAnterior.getSaldo());
        novaSituacao.setGeraReferencia(false);
        novaSituacao.setReferencia(situacaoAnterior.getReferencia());
        em.persist(novaSituacao);
        baixarParcelasDaCotaUnicaOrCotaUnicaDaParcela(parcela);
    }

    private void baixarParcelasDaCotaUnicaOrCotaUnicaDaParcela(ParcelaValorDivida parcela) throws Exception {
        List<ParcelaValorDivida> demaisParcelas = buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, SituacaoParcela.EM_ABERTO);
        if (demaisParcelas != null && !demaisParcelas.isEmpty()) {
            damFacade.geraDAMs(demaisParcelas);
            for (ParcelaValorDivida outraParcela : demaisParcelas) {
                SituacaoParcelaValorDivida situacaoAnterior = getUltimaSituacao(outraParcela);
                em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.BAIXADO_OUTRA_OPCAO, outraParcela, situacaoAnterior.getSaldo()));
            }
        }
    }

    public void baixarParcelasDoDam(DAM dam) throws Exception {
        List<ParcelaValorDivida> parcelasDoDam = buscarParcelasPorIdDam(dam.getId());
        for (ParcelaValorDivida parcela : parcelasDoDam) {
            baixarParcelaValorDivida(parcela);
        }
        depoisDePagarQueue.enqueueProcess(DepoisDePagarResquest.build().nome("Pagamento de DAM " + dam.getNumeroDAM()).parcelas(parcelasDoDam));
    }
}
