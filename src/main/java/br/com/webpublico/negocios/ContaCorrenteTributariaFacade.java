package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroResiduoArrecadacao;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.DTO.ValoresPagosParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Fabio on 07/08/2018.
 */
@Stateless
public class ContaCorrenteTributariaFacade extends AbstractFacade<ContaCorrenteTributaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigoTributario singletonGeradorCodigoTributario;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CacheTributario cacheTributario;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private ConsultaDAMFacade consultaDAMFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GeraValorDividaContaCorrente geraValorDividaContaCorrente;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private RestituicaoFacade restituicaoFacade;
    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;

    public ContaCorrenteTributariaFacade() {
        super(ContaCorrenteTributaria.class);
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CacheTributario getCacheTributario() {
        return cacheTributario;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public LoteBaixaFacade getLoteBaixaFacade() {
        return loteBaixaFacade;
    }

    public ConsultaDAMFacade getConsultaDAMFacade() {
        return consultaDAMFacade;
    }

    public RestituicaoFacade getRestituicaoFacade() {
        return restituicaoFacade;
    }

    @Override
    public ContaCorrenteTributaria recuperar(Object id) {
        ContaCorrenteTributaria cc = em.find(ContaCorrenteTributaria.class, id);
        Hibernate.initialize(cc.getParcelas());
        Hibernate.initialize(cc.getPessoa().getEnderecos());
        Hibernate.initialize(cc.getPessoa().getTelefones());
        if (cc.getPessoa() instanceof PessoaFisica) {
            Hibernate.initialize(((PessoaFisica) cc.getPessoa()).getDocumentosPessoais());
        }
        return cc;
    }

    private boolean isParcelaEstaNaContaCorrente(ContaCorrenteTributaria contaCorrente, ParcelaValorDivida parcela) {
        boolean temAParcela = false;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : contaCorrente.getParcelas()) {
            if (parcelaContaCorrenteTributaria.getParcelaValorDivida().getId().equals(parcela.getId())) {
                temAParcela = true;
                break;
            }
        }
        return temAParcela;
    }

    public void adicionarParcelaNaContaCorrente(ContaCorrenteTributaria contaCorrente, ParcelaValorDivida parcela, TipoDiferencaContaCorrente tipoDiferenca, BigDecimal valorDiferenca, ContaCorrenteTributaria.Origem origem, ItemLoteBaixa itemLoteBaixa) {
        if (!isParcelaEstaNaContaCorrente(contaCorrente, parcela) || ContaCorrenteTributaria.Origem.ARRECADACAO.equals(origem)) {
            ParcelaContaCorrenteTributaria parcelaContaCorrente = new ParcelaContaCorrenteTributaria();
            parcelaContaCorrente.setContaCorrente(contaCorrente);
            parcelaContaCorrente.setParcelaValorDivida(parcela);
            parcelaContaCorrente.setTipoDiferenca(tipoDiferenca);
            parcelaContaCorrente.setValorDiferenca(valorDiferenca);
            parcelaContaCorrente.setOrigem(origem);
            parcelaContaCorrente.setItemLoteBaixa(itemLoteBaixa);

            contaCorrente.getParcelas().add(parcelaContaCorrente);
        }
    }

    public void removerParcelaDaContaCorrente(ContaCorrenteTributaria contaCorrente, ParcelaValorDivida parcela, ContaCorrenteTributaria.Origem origem, ItemLoteBaixa itemLoteBaixa) {
        List<ParcelaContaCorrenteTributaria> parcelaParaRemover = Lists.newArrayList();
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : contaCorrente.getParcelas()) {
            if (parcelaContaCorrenteTributaria.getParcelaValorDivida().getId().equals(parcela.getId())) {
                if (origem.equals(ContaCorrenteTributaria.Origem.ARRECADACAO) && itemLoteBaixa.equals(parcelaContaCorrenteTributaria.getItemLoteBaixa())) {
                    if (parcelaContaCorrenteTributaria.getCalculoContaCorrente() == null) {
                        parcelaParaRemover.add(parcelaContaCorrenteTributaria);
                    } else {
                        if (cancelarParcelaGeradaNaContaCorrente(parcelaContaCorrenteTributaria)) {
                            parcelaParaRemover.add(parcelaContaCorrenteTributaria);
                        }
                    }
                }
            }
        }
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelaParaRemover) {
            contaCorrente.getParcelas().remove(parcelaContaCorrenteTributaria);
        }
    }

    private boolean cancelarParcelaGeradaNaContaCorrente(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        boolean cancelou = false;
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, parcelaContaCorrenteTributaria.getCalculoContaCorrente().getId());
        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(parcela.getIdParcela(), parcela.getReferencia(), BigDecimal.ZERO, SituacaoParcela.CANCELAMENTO);
                cancelou = true;
            }
        }
        return cancelou;
    }

    public ContaCorrenteTributaria buscarOuCriarContaCorrentePorPessoa(Pessoa pessoa, ContaCorrenteTributaria.Origem origem) {
        ContaCorrenteTributaria contaCorrente = buscarContaCorrentePorPessoa(pessoa);
        if (contaCorrente == null) {
            contaCorrente = new ContaCorrenteTributaria();
            contaCorrente.setPessoa(pessoa);
            contaCorrente.setOrigem(origem);
            contaCorrente.setDataCadastro(new Date());
            contaCorrente.setCodigo(singletonGeradorCodigoTributario.getProximoCodigo(ContaCorrenteTributaria.class, "codigo"));
        }
        return contaCorrente;
    }

    public ContaCorrenteTributaria buscarContaCorrentePorPessoa(Pessoa pessoa) {
        String sql = "select cct.* from ContaCorrenteTributaria cct where cct.pessoa_id = :idPessoa";
        Query q = em.createNativeQuery(sql, ContaCorrenteTributaria.class);
        q.setParameter("idPessoa", pessoa.getId());
        if (!q.getResultList().isEmpty()) {
            return (ContaCorrenteTributaria) q.getResultList().get(0);
        }
        return null;
    }

    public boolean verificarParcelaNaContaCorrente(ParcelaValorDivida parcela) {
        String sql = "select pcc.id from PARCELACONTACORRENTETRIB pcc " +
            " where pcc.parcelaValorDivida_id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getId());
        return !q.getResultList().isEmpty();
    }

    public List<Pessoa> buscarPessoasComContaCorrente(String filtro) {
        List<Pessoa> retorno = Lists.newArrayList();
        retorno.addAll(buscarPessoasFisicasComContaCorrente(filtro));
        retorno.addAll(buscarPessoasJuridicasComContaCorrente(filtro));
        return retorno;
    }

    public List<PessoaFisica> buscarPessoasFisicasComContaCorrente(String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select p.id, pf.nome, pf.cpf, p.situacaoCadastralPessoa from ContaCorrenteTributaria cct ")
            .append(" inner join Pessoa p on cct.pessoa_id = p.id ")
            .append(" inner join pessoafisica pf on p.id = pf.id ")
            .append(" where lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ")
            .append("  or replace(replace(pf.cpf, '.', ''), '-', '')  like :filtro ")
            .append(" order by pf.nome ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().replace(".", "").replace("/", "").replace("-", "") + "%");
        List<PessoaFisica> pessoas = Lists.newArrayList();
        q.setMaxResults(10);
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            PessoaFisica pf = new PessoaFisica();
            pf.setId(((Number) o[0]).longValue());
            pf.setNome((String) o[1]);
            pf.setCpf((String) o[2]);
            pf.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.valueOf((String) o[3]));
            pessoas.add(pf);
        }
        return pessoas;
    }

    public List<PessoaJuridica> buscarPessoasJuridicasComContaCorrente(String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select p.id, pj.razaoSocial, pj.cnpj, p.situacaoCadastralPessoa from ContaCorrenteTributaria cct ")
            .append(" inner join Pessoa p on cct.pessoa_id = p.id ")
            .append(" inner join pessoajuridica pj on p.id = pj.id ")
            .append(" where lower(translate(pj.razaosocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ")
            .append("  or replace(replace(replace(pj.cnpj, '.', ''), '/', ''), '-', '')  like :filtro ")
            .append(" order by pj.razaosocial ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().replace(".", "").replace("/", "").replace("-", "") + "%");
        List<PessoaJuridica> pessoas = Lists.newArrayList();
        q.setMaxResults(10);
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            PessoaJuridica pj = new PessoaJuridica();
            pj.setId(((Number) o[0]).longValue());
            pj.setRazaoSocial((String) o[1]);
            pj.setCnpj((String) o[2]);
            pj.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.valueOf((String) o[3]));
            pessoas.add(pj);
        }
        return pessoas;
    }

    public Pessoa buscarPessoaDaParcela(ParcelaValorDivida parcela) {
        String sql = "select coalesce(prop.pessoa_id, ce.pessoa_id, pes.id) as idPessoa " +
            "from ParcelaValorDivida pvd " +
            "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            "inner join Calculo cal on cal.id = vd.calculo_id " +
            "left join CadastroImobiliario ci on ci.id = cal.cadastro_id " +
            "left join Propriedade prop on prop.imovel_id = ci.id and current_date between prop.iniciovigencia and coalesce(prop.finalvigencia,current_date) " +
            "left join CadastroEconomico ce on ce.id = cal.cadastro_id " +
            "inner join CalculoPessoa cp on cp.calculo_id = cal.id " +
            "inner join Pessoa pes on pes.id = cp.pessoa_id " +
            "where pvd.id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getId());
        Long idPessoa = ((BigDecimal) q.getResultList().get(0)).longValue();
        return pessoaFacade.recuperarSimples(idPessoa);
    }

    public BigDecimal getValorDiferencaAtualizada(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        Date dataPagamento = parcelaContaCorrenteTributaria.getResultadoParcela().getPagamento();
        if (dataPagamento != null) {
            BigDecimal ufmEpoca = getUfmDaData(dataPagamento);
            BigDecimal ufmAtual = getUfmDaData(new Date());

            BigDecimal valorCorrigido = parcelaContaCorrenteTributaria.getValorDiferenca();
            if ((ufmEpoca != null && ufmEpoca.compareTo(BigDecimal.ZERO) > 0) &&
                (ufmAtual != null && ufmAtual.compareTo(BigDecimal.ZERO) > 0) &&
                ufmAtual.compareTo(ufmEpoca) > 0) {
                valorCorrigido = (valorCorrigido.divide(ufmEpoca, 8, RoundingMode.HALF_UP)).multiply(ufmAtual);
            }
            return valorCorrigido;
        }
        return parcelaContaCorrenteTributaria.getValorDiferenca();
    }

    private BigDecimal getUfmDaData(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        return getCacheTributario().buscarValorUFMParaAno(c.get(Calendar.YEAR));
    }

    public BigDecimal buscarValorCreditoUtilizado(Pessoa pessoa) {
        String sql = "select coalesce(sum(icom.VALORCOMPENSADO),0) from ItemCompensacao icom " +
            " inner join Compensacao com on com.id = icom.COMPENSACAO_ID " +
            " where com.PESSOA_ID = :idPessoa " +
            " and com.SITUACAO = :situacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarValorResiduoUtilizado(ContaCorrenteTributaria contaCorrenteTributaria) {
        String sql = "select coalesce(sum(vd.valor),0) from CalculoContaCorrente ccc " +
            " inner join Calculo calc on ccc.id = calc.id" +
            " inner join ValorDivida vd on vd.calculo_id = calc.id " +
            " where ccc.contaCorrenteTributaria_id = :idContaCorrente";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContaCorrente", contaCorrenteTributaria.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public CalculoContaCorrente criarCalculoContaCorrente(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria, BigDecimal saldoResiduoDisponivel) {
        ProcessoCalculoContaCorrente processo = new ProcessoCalculoContaCorrente();
        processo.setExercicio(parcelaContaCorrenteTributaria.getParcelaValorDivida().getValorDivida().getExercicio());
        processo.setDivida(parcelaContaCorrenteTributaria.getParcelaValorDivida().getValorDivida().getDivida());
        processo.setDataLancamento(new Date());
        processo.setDescricao("Resíduo da Conta Corrente " + parcelaContaCorrenteTributaria.getContaCorrente().getCodigo());

        CalculoContaCorrente calculo = new CalculoContaCorrente();
        calculo.setDataCalculo(processo.getDataLancamento());
        calculo.setReferencia(parcelaContaCorrenteTributaria.getParcelaValorDivida().getSituacaoAtual().getReferencia());
        calculo.setContaCorrenteTributaria(parcelaContaCorrenteTributaria.getContaCorrente());
        calculo.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        calculo.setPessoa(parcelaContaCorrenteTributaria.getContaCorrente().getPessoa());
        calculo.setCadastro(buscarCadastroDaParcela(parcelaContaCorrenteTributaria.getParcelaValorDivida()));
        calculo.setValorEfetivo(saldoResiduoDisponivel);
        calculo.setValorReal(saldoResiduoDisponivel);
        calculo.setVencimento(calcularVencimento());
        calculo.setDividaAtiva(parcelaContaCorrenteTributaria.getParcelaValorDivida().getDividaAtiva());
        calculo.setDividaAtivaAjuizada(parcelaContaCorrenteTributaria.getParcelaValorDivida().getDividaAtivaAjuizada());
        calculo.setSequenciaParcela(parcelaContaCorrenteTributaria.getParcelaValorDivida().getSequenciaParcela());
        calculo.setQuantidadeParcela(consultaDebitoFacade.getQuantidadeParcelasValorDivida(parcelaContaCorrenteTributaria.getParcelaValorDivida().getValorDivida(), parcelaContaCorrenteTributaria.getParcelaValorDivida().getOpcaoPagamento()));

        List<CalculoPessoa> pessoasDaParcela = buscarPessoasDaParcela(parcelaContaCorrenteTributaria.getParcelaValorDivida());
        for (CalculoPessoa cp : pessoasDaParcela) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculo);
            calculoPessoa.setPessoa(cp.getPessoa());
            if (calculo.getPessoas() == null) {
                calculo.setPessoas(new ArrayList<CalculoPessoa>());
            }
            calculo.getPessoas().add(calculoPessoa);
        }

        criarItensCalculoContaCorrente(calculo, parcelaContaCorrenteTributaria.getParcelaValorDivida(), saldoResiduoDisponivel);

        calculo.setProcessoCalculo(processo);
        processo.getCalculoContaCorrente().add(calculo);

        return calculo;
    }

    private void criarItensCalculoContaCorrente(CalculoContaCorrente calculo, ParcelaValorDivida parcelaValorDivida, BigDecimal valorNovaParcela) {
        String sql = "select ivd.tributo_id, ipvd.valor as valorTributo, pvd.valor as valorParcela " +
            " from ItemParcelaValorDivida ipvd " +
            " inner join ItemValorDivida ivd on ivd.id = ipvd.itemValorDivida_id " +
            " inner join ParcelaValorDivida pvd on pvd.id = ipvd.parcelaValorDivida_id " +
            " where pvd.id = :idParcela " +
            " and coalesce(ipvd.valor,0) > 0 " +
            " and ivd.tributo_id is not null";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcelaValorDivida.getId());
        List<Object[]> lista = q.getResultList();
        BigDecimal valorTotalCalculado = BigDecimal.ZERO;
        for (Object[] obj : lista) {
            ItemCalculoContaCorrente item = new ItemCalculoContaCorrente();
            item.setCalculoContaCorrente(calculo);
            item.setTributo(tributoFacade.recuperar(((BigDecimal) obj[0]).longValue()));

            BigDecimal valorTributo = (BigDecimal) obj[1];
            BigDecimal valorParcela = (BigDecimal) obj[2];
            BigDecimal valorProporcional = valorTributo.multiply(valorNovaParcela).divide(valorParcela, 8, RoundingMode.HALF_UP);
            valorTotalCalculado = valorTotalCalculado.add(valorProporcional);
            item.setValor(valorProporcional);

            calculo.getItens().add(item);
        }
        BigDecimal diferenca = valorTotalCalculado.subtract(valorNovaParcela);
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal valorPrimeiroItem = calculo.getItens().get(0).getValor();
            valorPrimeiroItem = valorPrimeiroItem.subtract(diferenca);
            calculo.getItens().get(0).setValor(valorPrimeiroItem);
        }
    }

    public Date calcularVencimento() {
        Calendar ultimoDiaMes = DataUtil.ultimoDiaMes(new Date());
        ultimoDiaMes = DataUtil.ultimoDiaUtil(ultimoDiaMes, feriadoFacade);
        if (ultimoDiaMes.getTime().compareTo(new Date()) < 0) {
            ultimoDiaMes.add(Calendar.MONTH, 1);
            ultimoDiaMes = DataUtil.ultimoDiaUtil(Calendar.getInstance(), feriadoFacade);
        }
        return ultimoDiaMes.getTime();
    }

    public List<CalculoPessoa> buscarPessoasDaParcela(ParcelaValorDivida parcela) {
        String hql = "select pes from ValorDivida vd " +
            " join vd.calculo cal" +
            " join cal.pessoas pes " +
            " where vd.id = :idValorDivida";
        Query q = em.createQuery(hql);
        q.setParameter("idValorDivida", parcela.getValorDivida().getId());
        return q.getResultList();
    }

    public Cadastro buscarCadastroDaParcela(ParcelaValorDivida parcela) {
        String hql = "select cal.cadastro from ValorDivida vd " +
            " join vd.calculo cal" +
            " where vd.id = :idValorDivida";
        Query q = em.createQuery(hql);
        q.setParameter("idValorDivida", parcela.getValorDivida().getId());
        if (!q.getResultList().isEmpty()) {
            return (Cadastro) q.getResultList().get(0);
        }
        return null;
    }

    public CalculoContaCorrente salvarCalculo(CalculoContaCorrente calculoContaCorrente) {
        return em.merge(calculoContaCorrente);
    }

    public ProcessoCalculoContaCorrente salvarProcessoCalculoContaCorrente(ProcessoCalculoContaCorrente processoCalculoContaCorrente) {
        return em.merge(processoCalculoContaCorrente);
    }

    public void gerarDebito(CalculoContaCorrente calculoContaCorrente) throws Exception {
        geraValorDividaContaCorrente.geraDebito(calculoContaCorrente.getProcessoCalculo());
    }

    public void gerarDAM(CalculoContaCorrente calculoContaCorrente) {
        geraValorDividaContaCorrente.getDamFacade().geraDAM(calculoContaCorrente);
    }

    public List<ResultadoParcela> buscarParcelasDoResidual(ContaCorrenteTributaria contaCorrenteTributaria) {
        String sql = "select ccc.id from CalculoContaCorrente ccc " +
            " where ccc.contaCorrenteTributaria_id = :idContaCorrente";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContaCorrente", contaCorrenteTributaria.getId());
        List<BigDecimal> lista = q.getResultList();
        if (!lista.isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, lista);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.DIFERENTE, SituacaoParcela.CANCELAMENTO);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    public ParcelaContaCorrenteTributaria salvarParcela(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        return em.merge(parcelaContaCorrenteTributaria);
    }

    public ContaCorrenteTributaria salvarRetornando(ContaCorrenteTributaria contaCorrenteTributaria) {
        ContaCorrenteTributaria cct = em.merge(contaCorrenteTributaria);
        return cct;
    }

    public BigDecimal somarValorResiduoUtilizado(List<ParcelaContaCorrenteTributaria> parcelasDoResidual) {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : parcelasDoResidual) {
            if (parcelaContaCorrenteTributaria.getCalculoContaCorrente() != null) {
                total = total.add(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
            }
        }
        return total;
    }

    public ContaCorrenteTributaria buscarContaCorrentePorCalculo(Long idCalculo) {
        String sql = "select cct.* from ContaCorrenteTributaria cct " +
            " inner join CalculoContaCorrente ccc on ccc.contacorrentetributaria_id = cct.id " +
            " where ccc.id = :idCalculo";
        Query q = em.createNativeQuery(sql, ContaCorrenteTributaria.class);
        try {
            q.setMaxResults(1);
            q.setParameter("idCalculo", idCalculo);
            return (ContaCorrenteTributaria) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public Compensacao buscarCompensacaoPorCalculo(Long idCalculo) {
        String sql = "select comp.* from Compensacao comp " +
            " inner join CalculoContaCorrente ccc on ccc.compensacao_id = comp.id " +
            " where ccc.id = :idCalculo";
        Query q = em.createNativeQuery(sql, Compensacao.class);
        try {
            q.setMaxResults(1);
            q.setParameter("idCalculo", idCalculo);
            return (Compensacao) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void compensarParcelaCompensada(Pessoa pessoa, ParcelaValorDivida parcela, BigDecimal valorCompensado) {
        ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria = buscarParcelaContaCorrenteTributaria(pessoa, parcela);
        if (parcelaContaCorrenteTributaria != null) {
            parcelaContaCorrenteTributaria.setValorCompesado(valorCompensado);
            parcelaContaCorrenteTributaria.setValorDiferencaAtualizada(valorCompensado);
            parcelaContaCorrenteTributaria.setCompensada(true);
            em.merge(parcelaContaCorrenteTributaria);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void adicionarValorAParcelaCompensada(Pessoa pessoa, ParcelaValorDivida parcela, BigDecimal diferencaAtualizada, BigDecimal valorCompensado) {
        ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria = buscarParcelaContaCorrenteTributaria(pessoa, parcela);
        if (parcelaContaCorrenteTributaria != null) {
            parcelaContaCorrenteTributaria.setValorCompesado(parcelaContaCorrenteTributaria.getValorCompesado().add(valorCompensado));
            parcelaContaCorrenteTributaria.setValorDiferencaAtualizada(diferencaAtualizada);
            em.merge(parcelaContaCorrenteTributaria);
        }
    }

    private ParcelaContaCorrenteTributaria buscarParcelaContaCorrenteTributaria(Pessoa pessoa, ParcelaValorDivida parcela) {
        String sql = "select p.* from PARCELACONTACORRENTETRIB p " +
            " inner join ContaCorrenteTributaria cct on cct.id = p.contaCorrente_id" +
            " where cct.pessoa_id = :idPessoa" +
            "  and p.parcelaValorDivida_id = :idParcela";
        Query q = em.createNativeQuery(sql, ParcelaContaCorrenteTributaria.class);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("idParcela", parcela.getId());
        q.setMaxResults(1);
        ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria = null;
        if (!q.getResultList().isEmpty()) {
            parcelaContaCorrenteTributaria = (ParcelaContaCorrenteTributaria) q.getResultList().get(0);
        }
        return parcelaContaCorrenteTributaria;
    }

    public ValoresPagosParcela buscarValorPagosNaCompensacao(Long idParcela) {
        String sql = "select item.valorCompensado, comp.dataCompensacao from Compensacao comp " +
            "inner join ItemCompensacao item on item.compensacao_id = comp.id " +
            "where item.parcela_id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);

        return preencherValoresPagosParcela(q);
    }

    public ValoresPagosParcela buscarValorePagoNaRestituicao(Long idParcela) {
        String sql = " select item.valorrestituido, rest.datarestituicao from restituicao rest " +
            " inner join itemrestituicao item on rest.id = item.restituicao_id " +
            " where item.parcelavalordivida_id = :idParcela ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);

        return preencherValoresPagosParcela(q);
    }

    private ValoresPagosParcela preencherValoresPagosParcela(Query q) {
        ValoresPagosParcela valor = new ValoresPagosParcela();
        if (!q.getResultList().isEmpty()) {
            Object[] obj = (Object[]) q.getResultList().get(0);
            valor.setTotalPago(obj[0] != null ? (BigDecimal) obj[0] : BigDecimal.ZERO);
            valor.setDataPagamento(obj[1] != null ? (Date) obj[1] : null);
        }
        return valor;
    }

    @Asynchronous
    public Future<List<ParcelaContaCorrenteTributaria>> buscarParcelasResiduoArrecadacao(FiltroResiduoArrecadacao filtro) {
        String sql = " select p.* " +
            "    from parcelacontacorrentetrib p " +
            "   inner join parcelavalordivida pvd on pvd.id = p.parcelavalordivida_id " +
            "   inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "   inner join contacorrentetributaria ct on ct.id = p.contacorrente_id " +
            "   left join pessoafisica pf on pf.id = ct.pessoa_id " +
            "   left join pessoajuridica pj on pj.id = ct.pessoa_id " +
            " where p.tipodiferenca = :tipo_diferenca ";
        if (filtro.getContribuinte() != null) {
            sql += " and ct.pessoa_id = :pessoa_id ";
        }
        if (filtro.getDataPagamentoInicial() != null) {
            sql += " and trunc(pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id, spvd.situacaoparcela)) >= " +
                " trunc(:data_pagamento_inicial) ";
        }
        if (filtro.getDataPagamentoFinal() != null) {
            sql += " and trunc(pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id, spvd.situacaoparcela)) <= " +
                " trunc(:data_pagamento_final) ";
        }
        if (filtro.getSituacao() != null) {
            sql += filtro.getSituacao().equals("1") ? " and p.calculocontacorrente_id is not null " :
                " and p.calculocontacorrente_id is null ";
        }
        sql += " order by coalesce(pf.nome, pj.razaosocial) ";
        Query q = em.createNativeQuery(sql, ParcelaContaCorrenteTributaria.class);
        q.setParameter("tipo_diferenca", TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO.name());
        if (filtro.getContribuinte() != null) {
            q.setParameter("pessoa_id", filtro.getContribuinte().getId());
        }
        if (filtro.getDataPagamentoInicial() != null) {
            q.setParameter("data_pagamento_inicial", filtro.getDataPagamentoInicial());
        }
        if (filtro.getDataPagamentoFinal() != null) {
            q.setParameter("data_pagamento_final", filtro.getDataPagamentoFinal());
        }
        List<ParcelaContaCorrenteTributaria> parcelas = q.getResultList();
        if (parcelas != null) {
            for (ParcelaContaCorrenteTributaria parcela : parcelas) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                List<ResultadoParcela> resultados = consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_ID,
                        br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, parcela.getParcelaValorDivida().getId())
                    .executaConsulta().getResultados();
                parcela.setResultadoParcela(!resultados.isEmpty() ? resultados.get(0) : null);
                processoCreditoContaCorrenteFacade.buscarValorPagoNaArrecadacao(consultaParcela.getResultados(), parcela.getOrigem());
                parcela.setValorDiferencaAtualizada(getValorDiferencaAtualizada(parcela));
            }
        }
        return new AsyncResult<>(parcelas);
    }

    @Asynchronous
    public Future gerarDebitoResidualArrecadacao(AssistenteBarraProgresso assistenteBarraProgresso,
                                                 List<ParcelaContaCorrenteTributaria> parcelas) {
        assistenteBarraProgresso.setDescricaoProcesso("Gerando débitos de resíduos de arrecadação...");
        assistenteBarraProgresso.setTotal(parcelas.size());
        for (ParcelaContaCorrenteTributaria parcela : parcelas) {
            contaCorrenteTributariaFacade.gerarDebitoResidualArrecadacao(parcela);
            assistenteBarraProgresso.conta();
        }
        return new AsyncResult<>(null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarDebitoResidualArrecadacao(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        try {
            BigDecimal valorDoDebito = parcelaContaCorrenteTributaria.getValorDiferencaAtualizada();
            if (valorDoDebito.compareTo(BigDecimal.ZERO) > 0) {
                CalculoContaCorrente calculoContaCorrente = criarCalculoContaCorrente(parcelaContaCorrenteTributaria, valorDoDebito);
                calculoContaCorrente.setProcessoCalculo(salvarProcessoCalculoContaCorrente(calculoContaCorrente.getProcessoCalculo()));
                calculoContaCorrente = calculoContaCorrente.getProcessoCalculo().getCalculoContaCorrente().get(0);
                gerarDebito(calculoContaCorrente);
                if (parcelaContaCorrenteTributaria.getTipoDiferenca().equals(TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO)) {
                    if (parcelaContaCorrenteTributaria.getCalculoContaCorrente() == null) {
                        parcelaContaCorrenteTributaria.setCalculoContaCorrente(calculoContaCorrente);
                        parcelaContaCorrenteTributaria.setValorDiferencaUtilizada(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
                    }
                }
                salvarParcela(parcelaContaCorrenteTributaria);
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar débito de residual da arrecadação. Parcela [{}]. Erro {}",
                parcelaContaCorrenteTributaria.getId(), e.getMessage());
            logger.debug("Detalhes do erro ao gerar débito de residual da arrecadação. Parcela [{}].",
                parcelaContaCorrenteTributaria.getId(), e);
        }
    }
}
