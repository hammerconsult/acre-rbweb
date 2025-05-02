package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarResquest;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.ResultadoValidacao;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoDebitoFacade extends AbstractFacade<ProcessoDebito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    private JdbcDamDAO jdbcDamDAO;

    public ProcessoDebitoFacade() {
        super(ProcessoDebito.class);
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
        jdbcDamDAO = (JdbcDamDAO) ap.getBean("damDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    @Override
    public void salvar(ProcessoDebito entity) throws RuntimeException {
        throw new RuntimeException("Método inválido: Utilize o método salva(ProcessoDebito)");
    }

    @Override
    public void salvarNovo(ProcessoDebito entity) throws RuntimeException {
        throw new RuntimeException("Método inválido: Utilize o método salvaNovo(ProcessoDebito)");
    }

    public void deletarItens(List<ItemProcessoDebito> itens) {
        for (ItemProcessoDebito item : itens) {
            if (item.getId() != null) {
                em.remove(em.find(ItemProcessoDebito.class, item.getId()));
            }
        }
    }

    public void salvarItens(List<ItemProcessoDebito> itens) {
        for (ItemProcessoDebito item : itens) {
            em.merge(item);
        }
        }
    public ProcessoDebito salvarProcesso(ProcessoDebito processo) {
        return em.merge(processo);
    }

    public ProcessoDebito salvaRetornando(ProcessoDebito entity) {
        return em.merge(entity);
    }


    public boolean codigoEmUso(ProcessoDebito processo) {
        if (processo == null || processo.getCodigo() == null) {
            return false;
        }
        String hql = "from ProcessoDebito pd where pd.codigo = :codigo and pd.exercicio = :exercicio and pd.tipo = '" + processo.getTipo() + "'";
        if (processo.getId() != null) {
            hql += " and pd != :processoDebito";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", processo.getCodigo());
        q.setParameter("exercicio", processo.getExercicio());
        if (processo.getId() != null) {
            q.setParameter("processoDebito", processo);
        }
        return !q.getResultList().isEmpty();
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ResultadoValidacao encerrarProcesso(ProcessoDebito processo, UsuarioSistema usuario) {
        ResultadoValidacao retorno = validarEncerramento(processo);
        if (retorno.isValidado()) {
//            processo.setSituacao(SituacaoProcessoDebito.FINALIZADO);
            int inicial = 0;
            List<ItemProcessoDebito> itens = buscarItemProcesso(inicial, 100, processo, Lists.<ItemProcessoDebito>newArrayList());
            while (!itens.isEmpty()) {
                for (ItemProcessoDebito item : itens) {
                    executarAntesDeProcessar(processo, item.getParcela(), usuario);
                    processarSituacaoDeAcordoComProcesso(processo, item.getParcela());
                }
                inicial += 100;
                itens = buscarItemProcesso(inicial, 100, processo, Lists.<ItemProcessoDebito>newArrayList());
            }
            retorno.limpaMensagens();

            em.createNativeQuery("update ProcessoDebito set situacao = '" + SituacaoProcessoDebito.FINALIZADO.name() + "' where id = " + processo.getId()).executeUpdate();
//            em.merge(processo);
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public void executarDependenciasDoProcesso(ProcessoDebito processo) {
        int inicial = 0;
        List<ItemProcessoDebito> itens = buscarItemProcesso(inicial, 100, processo, Lists.<ItemProcessoDebito>newArrayList());
        while (!itens.isEmpty()) {
            for (ItemProcessoDebito item : itens) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, item.getParcela().getId());
                executarDepoisDeProcessar(processo, parcela);
            }
            inicial += 100;
            itens = buscarItemProcesso(inicial, 100, processo, Lists.<ItemProcessoDebito>newArrayList());
        }
    }

    private void executarAntesDeProcessar(ProcessoDebito processo, ParcelaValorDivida parcela, UsuarioSistema usuario) {
        if (TipoProcessoDebito.BAIXA.equals(processo.getTipo())) {
            DAM damBaixa = consultaDebitoFacade.getDamFacade().verificarSeExisteDamVencidoAndRetornarDamDaParcela(parcela, processo.getDataPagamento());
            if (damBaixa == null) {
                try {
                    damBaixa = consultaDebitoFacade.getDamFacade().geraDAM(parcela, processo.getDataPagamento());
                } catch (Exception e) {
                    logger.error("Erro ao gerar o dam no processo de débito: {}", e);
                }
            }
            if (!DAM.Situacao.ABERTO.equals(damBaixa.getSituacao())) {
                jdbcDamDAO.atualizar(damBaixa.getId(), DAM.Situacao.ABERTO, usuario);
            }
        }
    }

    private void executarDepoisDeProcessar(ProcessoDebito processo, ParcelaValorDivida parcela) {
        realizarOperacoesPorTipoDoCalculo(processo, parcela.getValorDivida().getCalculo());
    }

    private void realizarOperacoesPorTipoDoCalculo(ProcessoDebito processo, Calculo calculo) {
        Calculo.TipoCalculo tipo = calculo.getTipoCalculo();
        switch (tipo) {
            case RB_TRANS:
                calculo = initializeAndUnproxy(calculo);
                if (((CalculoRBTrans) calculo).getTipoCalculoRBTRans().equals(TipoCalculoRBTRans.RENOVACAO_PERMISSAO)) {
                    PermissaoTransporte permissao = permissaoTransporteFacade.recuperaPermissaoPorCMC((CadastroEconomico) calculo.getCadastro());
                    if (permissao != null) {
                        permissao.setHabilitarVerificacaoDocumentos(true);
                        permissao.setDocumentosApresentados(false);
                        em.merge(permissao);
                    }
                    break;
                }
            case INSCRICAO_DA:
                calculo = initializeAndUnproxy(calculo);
                certidaoDividaAtivaFacade.alterarSituacaoDaCdaPorTipoProcessoDebito(processo, calculo);
                break;
        }
    }

    public void processarSituacaoDeAcordoComProcesso(ProcessoDebito processo, ParcelaValorDivida parcela) {
        baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(parcela, processo, SituacaoParcela.BAIXADO_OUTRA_OPCAO, SituacaoParcela.EM_ABERTO);
        salvarSituacaoParcela(parcela, processo.getTipo().getSituacaoParcela());
    }

    private void baixarOutraOpcaoParcelasDaCotaUnicaOrCotaUnidaDasParcelas(ParcelaValorDivida parcela,
                                                                           ProcessoDebito processo,
                                                                           SituacaoParcela novaSituacaoParcela,
                                                                           SituacaoParcela situacoesParaPesquisa) {
        List<ParcelaValorDivida> parcelasOutraOpcao = consultaDebitoFacade.buscarParcelasDaCotaUnicaOrACotaUnicaDasParcelas(parcela, situacoesParaPesquisa);
        if (!parcelasOutraOpcao.isEmpty()) {
            for (ParcelaValorDivida parcelaDaOutraOpcao : parcelasOutraOpcao) {
                if (!hasParcelaNoProcesso(processo, parcelaDaOutraOpcao)) {
                    em.persist(new SituacaoParcelaValorDivida(novaSituacaoParcela, parcelaDaOutraOpcao, parcela.getSituacaoAtual().getSaldo()));
                }
            }
        }
    }

    private boolean hasParcelaNoProcesso(ProcessoDebito processo, ParcelaValorDivida parcela) {
        List resultList = em.createNativeQuery("select i.id from itemprocessodebito i where " +
                " i.processodebito_id = :idProcesso and i.parcela_id = :idParcela")
            .setParameter("idParcela", parcela.getId())
            .setParameter("idProcesso", processo.getId())
            .getResultList();
        return !resultList.isEmpty();
    }

    public void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        salvarSituacaoParcela(parcela, situacao, false);
    }

    public void salvarSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao, boolean recuperar) {
        if (recuperar) {
            parcela = em.find(ParcelaValorDivida.class, parcela.getId());
        }
        SituacaoParcelaValorDivida toAdd = new SituacaoParcelaValorDivida();
        toAdd.setGeraReferencia(false);
        toAdd.setReferencia(parcela.getUltimaSituacao().getReferencia());
        toAdd.setSaldo(parcela.getUltimaSituacao().getSaldo());
        toAdd.setDataLancamento(new Date());
        toAdd.setParcela(parcela);
        toAdd.setSituacaoParcela(situacao);
        em.persist(toAdd);
    }

    private ResultadoValidacao validarEncerramento(ProcessoDebito processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (processo.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser finalizados.");
        }
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ResultadoValidacao> estornar(AssistenteBarraProgresso assistenteBarraProgresso,
                                               ProcessoDebito processo,
                                               SituacaoProcessoDebito situacao) {
        assistenteBarraProgresso.setDescricaoProcesso("Validando estorno de processo de débito...");
        assistenteBarraProgresso.setTotal(0);
        ResultadoValidacao retorno = validarEstorno(processo);
        if (retorno.isValidado()) {
            assistenteBarraProgresso.setDescricaoProcesso("Estornando itens do processo de " + processo.getTipo().getDescricao());
            assistenteBarraProgresso.setTotal(contarItens(processo, new ArrayList()));
            int inicial = 0;
            List<ItemProcessoDebito> itens = buscarItemProcesso(inicial, 500, processo, Lists.<ItemProcessoDebito>newArrayList());
            while (!itens.isEmpty()) {
                for (ItemProcessoDebito item : itens) {
                    estornarItemProcesso(assistenteBarraProgresso, processo, item);
                }
                inicial += 500;
                itens = buscarItemProcesso(inicial, 500, processo, Lists.<ItemProcessoDebito>newArrayList());
            }
            assistenteBarraProgresso.setDescricaoProcesso("Atualizando situação do processo de " + processo.getTipo().getDescricao());
            assistenteBarraProgresso.setTotal(0);
            processo.setSituacao(situacao);
            em.merge(processo);
            retorno.limpaMensagens();
            retorno.addInfo(null, "Processo de Débitos estornado com sucesso!", "");
        }
        return new AsyncResult<>(retorno);
    }

    private void estornarItemProcesso(AssistenteBarraProgresso assistenteBarraProgresso,
                                      ProcessoDebito processo, ItemProcessoDebito item) {
        if (!isPago(item.getResultadoParcela()) &&
            item.getResultadoParcela().getSituacaoEnumValue().name().equals(processo.getTipo().getSituacaoParcela().name())) {
            if (!parcelaValorDividaDAO.hasOutraOpcaoPagamento(item.getParcela())) {
                parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                    item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                    SituacaoParcela.EM_ABERTO);
            } else {
                if (parcelaValorDividaDAO.hasOutraOpcaoPagamentoPago(item.getParcela())) {
                    parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                        item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                        SituacaoParcela.BAIXADO_OUTRA_OPCAO);
                } else {
                    parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(item.getParcela().getId(),
                        item.getParcela().getSituacaoAtual().getReferencia(), item.getParcela().getSituacaoAtual().getSaldo(),
                        SituacaoParcela.EM_ABERTO);
                    parcelaValorDividaDAO.abrirOutraOpcaoPagamentoBaixado(item.getParcela());
                }
            }
        }
        assistenteBarraProgresso.conta();
    }

    public boolean isPago(ResultadoParcela resultadoParcela) {
        return SituacaoParcela.PAGO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_PARCELAMENTO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_REFIS.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_SUBVENCAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.BAIXADO_OUTRA_OPCAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.COMPENSACAO.equals(resultadoParcela.getSituacaoEnumValue()) ||
            SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.equals(resultadoParcela.getSituacaoEnumValue());
    }

    private ResultadoValidacao validarEstorno(ProcessoDebito processo) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (!SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
            retorno.addErro(null, "Não foi possível continuar!", "O processo selecionado encontra-se " + processo.getSituacao().getDescricao() + ". Somente processos FINALIZADOS podem ser estornados.");
        }
        return retorno;
    }

    public ResultadoValidacao adicionarItem(ProcessoDebito processo, ParcelaValorDivida parcela) {
        ResultadoValidacao retorno = validarItem(parcela);
        if (retorno.isValidado()) {
            ItemProcessoDebito item = new ItemProcessoDebito();
            item.setParcela(parcela);
            item.setProcessoDebito(processo);
            item.setSituacaoAnterior(parcela.getUltimaSituacao().getSituacaoParcela());
            item.setSituacaoProxima(processo.getTipo().getSituacaoParcela());
            if (processo.getItens() == null) {
                processo.setItens(new ArrayList<ItemProcessoDebito>());
            }
            processo.getItens().add(item);
        }
        return retorno;
    }

    private ResultadoValidacao validarItem(ParcelaValorDivida parcela) {
        ResultadoValidacao retorno = new ResultadoValidacao();
        if (parcela == null || parcela.getId() == null) {
            retorno.addErro(null, "Não foi possível continuar!", "Parcela não pode ser nula.");
        }
        return retorno;
    }

    @Override
    public List<ProcessoDebito> lista() {
        return em.createQuery("from ProcessoDebito p order by p.codigo").getResultList();
    }

    @Override
    public ProcessoDebito recuperar(Object id) {
        ProcessoDebito p = em.find(ProcessoDebito.class, id);
        if(p.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(p.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return p;
    }

    public ProcessoDebito recuperarItensDoProcesso(Long idProcesso) {
        ProcessoDebito processoDebito = em.find(ProcessoDebito.class, idProcesso);
        Hibernate.initialize(processoDebito.getItens());
        return processoDebito;
    }

    public Long recuperarProximoCodigoPorExercicioTipo(Exercicio exercicio, TipoProcessoDebito tipoProcessoDebito) {
        String sql = " select max(coalesce(obj.codigo,0)) from ProcessoDebito obj "
            + " where obj.exercicio = :exercicio"
            + " and obj.tipo = '" + tipoProcessoDebito.name() + "'";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    public ResultadoValidacao gerarEstornoDeLancamentoOutorga(List<ResultadoParcela> parcelas, UsuarioSistema usuario) {
        ProcessoDebito processoDebito = new ProcessoDebito();
        processoDebito.setUsuarioIncluiu(sistemaFacade.getUsuarioCorrente());
        processoDebito.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
        processoDebito.setLancamento(SistemaFacade.getDataCorrente());
        processoDebito.setExercicio(sistemaFacade.getExercicioCorrente());
        processoDebito.setTipo(TipoProcessoDebito.CANCELAMENTO);
        processoDebito.setCodigo(recuperarProximoCodigoPorExercicioTipo(processoDebito.getExercicio(), processoDebito.getTipo()));

        ResultadoValidacao rv = new ResultadoValidacao();
        List<ParcelaValorDivida> listaParcela = consultaDebitoFacade.buscarParcelaValorDividaDoResultadoParcela(parcelas);
        Iterator<ParcelaValorDivida> iterator = listaParcela.iterator();
        if (iterator.hasNext()) {
            rv.getMensagens().addAll(adicionarItem(iterator.next(), processoDebito, processoDebito.getItens(), listaParcela).getMensagens());
        }

        processoDebito.setMotivo("Estorno de Lançamentou de Outorga.");
        salvaRetornando(processoDebito);
        encerrarProcesso(processoDebito, usuario);
        return rv;
    }


    public ResultadoValidacao adicionarItem(ParcelaValorDivida parcela, ProcessoDebito processoDebito, List<ItemProcessoDebito> itens, List<ParcelaValorDivida> listaParcela) {
        ResultadoValidacao rv = new ResultadoValidacao();

        if (validarItemProcesso(parcela, processoDebito)) {
            try {
                ItemProcessoDebito itemProcessoDebito = new ItemProcessoDebito();
                itemProcessoDebito.setProcessoDebito(processoDebito);
                itemProcessoDebito.setParcela(parcela);
                itemProcessoDebito.setSituacaoProxima(processoDebito.getSituacaoParcela());
                itemProcessoDebito.setSituacaoAnterior(consultaDebitoFacade.getUltimaSituacao(parcela).getSituacaoParcela());
                itemProcessoDebito.setSituacaoProxima(processoDebito.getTipo().getSituacaoParcela());
                itens.add(itemProcessoDebito);
                listaParcela.remove(parcela);

                rv.addInfo(null, "Sucesso!", "Parcela " + parcela.getSequenciaParcela() + " adicionada ao processo.");
            } catch (Exception e) {
                logger.error(e.getMessage());
                rv.addErro(null, "Não foi possível continuar!", "Ocorreu um erro ao adicionar a parcela: " + e.getMessage());
            }
        } else {
            rv.addErro(null, "Não foi possível continuar!", "A parcela selecionada já foi adicionada ao processo!");
        }

        return rv;
    }

    public void adicionarItem(ResultadoParcela resultadoParcelas, ProcessoDebito processoDebito, List<ItemProcessoDebito> itens, List<ResultadoParcela> resultadoConsulta) {
        ValidacaoException ve = new ValidacaoException();
        if (permitirAdicionarItem(itens, resultadoParcelas)) {
            ItemProcessoDebito itemProcessoDebito = new ItemProcessoDebito();
            itemProcessoDebito.setProcessoDebito(processoDebito);
            itemProcessoDebito.setReferencia(resultadoParcelas.getReferencia());
            itemProcessoDebito.setParcela(recuperarParcelaValorDivida(resultadoParcelas.getIdParcela()));
            itemProcessoDebito.setSituacaoProxima(processoDebito.getSituacaoParcela());
            itemProcessoDebito.setSituacaoAnterior(SituacaoParcela.fromDto(resultadoParcelas.getSituacaoEnumValue()));
            itemProcessoDebito.setSituacaoProxima(processoDebito.getTipo().getSituacaoParcela());
            itemProcessoDebito.setResultadoParcela(resultadoParcelas);
            itens.add(itemProcessoDebito);
            resultadoConsulta.remove(resultadoParcelas);
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A parcela selecionada: " + resultadoParcelas.getReferencia() + " - " +
                resultadoParcelas.getDivida() + " - " + resultadoParcelas.getParcela() + " já foi adicionada ao processo!");
            ve.lancarException();
        }
    }

    private boolean permitirAdicionarItem(List<ItemProcessoDebito> itens, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ItemProcessoDebito item : itens) {
            if (item.getParcela().getId().equals(resultadoParcelas.getIdParcela())) {
                valida = false;
                break;
            }
        }
        return valida;
    }

    private ParcelaValorDivida recuperarParcelaValorDivida(Long id) {
        Query q = em.createQuery(" select pvd from ParcelaValorDivida pvd where id = :id");
        q.setParameter("id", id);
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

    public String buscarNumeroDam(ParcelaValorDivida pvd) {
        StringBuilder sql = new StringBuilder(" select dam.numerodam from itemdam item");
        sql.append(" inner join dam on item.dam_id = dam.id");
        sql.append("  inner join parcelavalordivida pvd on item.parcela_id = pvd.id");
        sql.append("  where pvd.id = :parcela and dam.situacao = 'ABERTO'");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("parcela", pvd.getId());
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        }
        return null;
    }


    public boolean validarItemProcesso(ParcelaValorDivida parcela, ProcessoDebito processoDebito) {
        boolean retorno = true;
        for (ItemProcessoDebito item : processoDebito.getItens()) {
            if (item.getParcela().equals(parcela)) {
                retorno = false;
                break;
            }
        }
        return retorno;
    }

    public List<String> recuperarCadastrosDoProcesso(ProcessoDebito processo) {
        List<String> toReturn = Lists.newArrayList();

        StringBuilder sb = new StringBuilder();
        sb.append(" select cad from ProcessoDebito obj ");
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.cadastro cad ");
        sb.append(" where obj.id = :idProcesso");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Cadastro> cadastros = new ArrayList(new HashSet(q.getResultList()));
        if (cadastros != null) {
            for (Cadastro cadastro : cadastros) {
                toReturn.add(cadastro.getNumeroCadastro());
            }
        }

        sb = new StringBuilder();
        sb.append(" select pes.pessoa from ProcessoDebito obj ");
        sb.append(" inner join obj.itens i ");
        sb.append(" inner join i.parcela p ");
        sb.append(" inner join p.valorDivida vd ");
        sb.append(" inner join vd.calculo c ");
        sb.append(" inner join c.pessoas pes ");
        sb.append(" where obj.id = :idProcesso and c.cadastro is null ");

        q = em.createQuery(sb.toString());
        q.setParameter("idProcesso", processo.getId());
        List<Pessoa> pessoas = new ArrayList(new HashSet(q.getResultList()));
        if (pessoas != null) {
            for (Pessoa pessoa : pessoas) {
                toReturn.add(pessoa.getCpf_Cnpj());
            }
        }
        return toReturn;
    }

    public ProcessoDebito buscarUltimoProcessoDebitoPorParcelValorDivida(Long pvd) {
        String sql = "select p.*" +
            "           from itemprocessodebito item " +
            "          inner join processodebito p on p.id = item.processodebito_id" +
            "         where item.parcela_id = :pvd" +
            "           and p.situacao = :finalizado " +
            "           order by p.id desc";
        Query q = em.createNativeQuery(sql, ProcessoDebito.class);
        try {
            q.setMaxResults(1);
            q.setParameter("pvd", pvd);
            q.setParameter("finalizado", SituacaoProcessoDebito.FINALIZADO.name());
            return (ProcessoDebito) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public ProcessoDebito buscarProcessoPorCadastro(Long idCadastro){
        String sql = " select proc.* " +
            " from processodebito proc" +
            "  inner join itemprocessodebito item on proc.id = item.processodebito_id " +
            "  inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            "  inner join valordivida vld on pvd.valordivida_id = vld.id " +
            "  inner join calculo calc on vld.calculo_id = calc.id " +
            "  inner join cadastro cad on calc.cadastro_id = cad.id " +
            "   where cad.id = :idCadastro " +
            "    and proc.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ProcessoDebito.class);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("situacao", SituacaoProcessoDebito.EM_ABERTO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ProcessoDebito) resultList.get(0);
        }
        return null;
    }


    public int contarItens(ProcessoDebito entity, List<ItemProcessoDebito> itensNotIn) {
        if (entity.getId() == null) {
            return 0;
        }
        Query q = getQuery("select count(i.id) ", entity, itensNotIn);
        return ((Number) q.getSingleResult()).intValue();
    }

    private Query getQuery(String select, ProcessoDebito entity, List<ItemProcessoDebito> itensNotIn) {
        List<Long> ids = Lists.newArrayList();
        for (ItemProcessoDebito itemProcessoDebito : itensNotIn) {
            ids.add(itemProcessoDebito.getId());
        }
        String sql = select + " from ItemProcessoDebito i where i.processoDebito = :processoDebito ";
        if (!itensNotIn.isEmpty()) {
            sql += " and i.id not in (:itens)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("processoDebito", entity);
        if (!itensNotIn.isEmpty()) {
            q.setParameter("itens", ids);
        }
        return q;
    }

    public List<ItemProcessoDebito> buscarItemProcesso(int primeiroRegistro, int qtdeRegistro,
                                                       ProcessoDebito entity, List<ItemProcessoDebito> itensNotIn) {
        if (entity.getId() == null) {
            return Lists.newArrayList();
        }
        Query q = getQuery("select i ", entity, itensNotIn);
        q.setFirstResult(primeiroRegistro);
        q.setMaxResults(qtdeRegistro);
        List<ItemProcessoDebito> list = q.getResultList();
        for (ItemProcessoDebito itemProcessoDebito : list) {
            if (itemProcessoDebito.getResultadoParcela() == null) {
                ConsultaParcela consulta = new ConsultaParcela();
                List<ResultadoParcela> resultados = consulta
                    .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, itemProcessoDebito.getParcela().getId())
                    .executaConsulta()
                    .getResultados();
                itemProcessoDebito.setResultadoParcela(resultados.get(0));
            }
        }
        return list;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void lancarProcessoDebito(Exercicio exercicio,
                                     UsuarioSistema usuarioSistema,
                                     TipoProcessoDebito tipoProcessoDebito,
                                     String protocolo, AtoLegal atoLegal, String motivo,
                                     TipoCadastroTributario tipoCadastro,
                                     Cadastro cadastro, Divida divida) {
        List<ResultadoParcela> debitos = buscarDebitosParaProcessoDebito(exercicio, cadastro, divida);
        if (debitos != null && !debitos.isEmpty()) {
            criarProcessoDebitoFinalizado(exercicio, usuarioSistema, tipoProcessoDebito, protocolo, atoLegal, motivo,
                tipoCadastro, cadastro, debitos);
        }
    }

    private void criarProcessoDebitoFinalizado(Exercicio exercicio, UsuarioSistema usuarioSistema,
                                               TipoProcessoDebito tipoProcessoDebito, String protocolo, AtoLegal atoLegal,
                                               String motivo, TipoCadastroTributario tipoCadastro, Cadastro cadastro,
                                               List<ResultadoParcela> debitos) {
        ProcessoDebito processoDebito = new ProcessoDebito();
        processoDebito.setTipo(tipoProcessoDebito);
        processoDebito.setExercicio(exercicio);
        processoDebito.setCodigo(recuperarProximoCodigoPorExercicioTipo(processoDebito.getExercicio(),
            processoDebito.getTipo()));
        processoDebito.setLancamento(new Date());
        processoDebito.setNumeroProtocolo(protocolo);
        processoDebito.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        processoDebito.setUsuarioIncluiu(usuarioSistema);
        processoDebito.setAtoLegal(atoLegal);
        processoDebito.setMotivo(motivo);
        processoDebito.setTipoCadastro(tipoCadastro);
        processoDebito.setCadastro(cadastro);
        processoDebito.setItens(Lists.newArrayList());
        processoDebito = em.merge(processoDebito);
        for (ResultadoParcela resultado : debitos) {
            ItemProcessoDebito itemProcessoDebito = criarItemProcessoDebitoFinalizado(processoDebito, resultado);
            em.merge(itemProcessoDebito);
            salvarSituacaoParcela(itemProcessoDebito.getParcela(), itemProcessoDebito.getSituacaoProxima());
        }
    }

    private SituacaoParcela getSituacaoParcelaPorTipoProcesso(TipoProcessoDebito tipo) {
        switch (tipo) {
            case ISENCAO:
                return SituacaoParcela.ISENTO;
            default:
                throw new ValidacaoException("Situação da Parcela não mapeada para o Tipo de Processo de Débito.");
        }
    }

    private ItemProcessoDebito criarItemProcessoDebitoFinalizado(ProcessoDebito processoDebito, ResultadoParcela resultado) {
        ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, resultado.getIdParcela());
        SituacaoParcela situacaoParcela = getSituacaoParcelaPorTipoProcesso(processoDebito.getTipo());
        ItemProcessoDebito itemProcessoDebito = new ItemProcessoDebito();
        itemProcessoDebito.setProcessoDebito(processoDebito);
        itemProcessoDebito.setParcela(parcelaValorDivida);
        itemProcessoDebito.setSituacaoAnterior(SituacaoParcela.EM_ABERTO);
        itemProcessoDebito.setSituacaoProxima(situacaoParcela);
        return itemProcessoDebito;
    }


    private List<ResultadoParcela> buscarDebitosParaProcessoDebito(Exercicio exercicio, Cadastro cadastro, Divida divida) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.EXERCICIO_ANO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CADASTRO_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.DIVIDA_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, divida.getId());
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.PARCELA_SITUACAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO.name());
        return consultaParcela.executaConsulta().getResultados();
    }
}
