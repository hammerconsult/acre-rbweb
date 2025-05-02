package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDAM;
import br.com.webpublico.entidadesauxiliares.RegistroDAF607;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoDePagamentoBaixa;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.handlers.RestTemplateResponseErrorHandler;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Stateless
@EJB(name = "DAMFacade", beanInterface = DAMFacade.class)
public class DAMFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(DAMFacade.class);
    protected final BigDecimal CEM = new BigDecimal("100");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @Resource
    private SessionContext sessionContext;
    private JdbcDamDAO damDAO;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        damDAO = (JdbcDamDAO) ap.getBean("damDAO");
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public DAM recuperar(Object id) {
        DAM dam = em.find(DAM.class, id);
        Hibernate.initialize(dam.getItens());
        return dam;
    }

    public DAM recuperarDamComCalculos(Long id) {
        DAM dam = em.find(DAM.class, id);
        Hibernate.initialize(dam.getItens());
        return dam;
    }

    public void geraDAM(Calculo calculo) {
        List<ValorDivida> valoresDivida = recuperaValorDividaDoCalculo(calculo);
        for (ValorDivida valorDivida : valoresDivida) {
            try {
                geraDAM(valorDivida);
            } catch (Exception e) {
                logger.error("Erro ao gerar dam pelo valordivida", e);
            }
        }
    }

    public List<ValorDivida> recuperaValorDividaDoCalculo(Calculo calculo) {
        return em.createQuery("from ValorDivida  vd where vd.calculo = :calculo")
            .setParameter("calculo", calculo)
            .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ValorDivida buscarUltimoValorDividaDoCalculo(Long idCalculo) {
        Query q = em.createNativeQuery("select vd.* from ValorDivida vd where vd.calculo_id = :idCalculo order by vd.id desc", ValorDivida.class)
            .setParameter("idCalculo", idCalculo);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            ValorDivida valorDivida = (ValorDivida) resultList.get(0);
            Hibernate.initialize(valorDivida.getParcelaValorDividas());
            Hibernate.initialize(valorDivida.getItemValorDividas());
            return valorDivida;
        }
        return null;
    }

    public List<DAM> geraDAM(ValorDivida vd) throws Exception {
        return geraDAM(vd, false);
    }

    public List<DAM> geraDAM(ValorDivida vd, boolean rodarEmNovaTransacao) throws Exception {
        return geraDAM(vd, sistemaFacade.getExercicioCorrente(), sistemaFacade.getUsuarioCorrente(), rodarEmNovaTransacao);
    }

    public List<DAM> geraDAM(ValorDivida vd, Exercicio exercicio, UsuarioSistema usuario) throws Exception {
        return geraDAM(vd, exercicio, usuario, false);
    }

    public List<DAM> geraDAM(ValorDivida vd, Exercicio exercicio, UsuarioSistema usuario, boolean rodarEmNovaTransacao) throws Exception {
        List<DAM> dams = Lists.newArrayList();
        for (ParcelaValorDivida parcela : vd.getParcelaValorDividas()) {
            dams.add(gerarDAM(parcela, parcela.getVencimento(), exercicio, rodarEmNovaTransacao, usuario));
        }
        return dams;
    }

    public List<DAM> geraDAMs(List<ParcelaValorDivida> parcelas) throws Exception {
        return geraDAMs(parcelas, sistemaFacade.getExercicioCorrente(), sistemaFacade.getUsuarioCorrente());
    }

    public List<DAM> geraDAMs(List<ParcelaValorDivida> parcelas, Exercicio exercicio, UsuarioSistema usuarioSistema) throws Exception {
        List<DAM> retorno = new ArrayList<>();
        for (ParcelaValorDivida parcelaValorDivida : parcelas) {
            Date vencimento = gerarDataDeVencimentoDoDAM(parcelaValorDivida.getVencimento());
            retorno.add(geraDAM(parcelaValorDivida, vencimento, exercicio, usuarioSistema));
        }

        return retorno;
    }

    private Date gerarDataDeVencimentoDoDAM(Date vencimento) {
        if (vencimento.before(new Date())) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
            vencimento = DataUtil.ultimoDiaUtil(c, feriadoFacade).getTime();
        }
        return vencimento;
    }

    public DAM gerarDAM(ParcelaValorDivida parcela, Date vencimento, boolean rodarEmNovaTransacao) throws Exception {
        return gerarDAM(parcela, vencimento, sistemaFacade.getExercicioCorrente(), rodarEmNovaTransacao, sistemaFacade.getUsuarioCorrente());
    }

    public DAM geraDAM(ParcelaValorDivida parcela, Date vencimento) throws Exception {
        return geraDAM(parcela, vencimento, sistemaFacade.getExercicioCorrente(), sistemaFacade.getUsuarioCorrente());
    }

    public DAM geraDAM(ParcelaValorDivida parcela) throws Exception {
        return geraDAM(parcela, DataUtil.ultimoDiaUtil(DataUtil.ultimoDiaMes(sistemaFacade.getDataOperacao()), feriadoFacade).getTime());
    }

    public DAM geraDAM(ParcelaValorDivida parcela, Date vencimento, Exercicio exercicio, UsuarioSistema usuario) throws Exception {
        return gerarDAM(parcela, vencimento, exercicio, false, usuario);
    }

    /*
     * sessionContext.getBusinessObject(DAMFacade.class) é utilizado
     * para a anotação @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) funcionar, rodando o método em uma
     * nova transação e comitando ao finalizar
     */
    public DAM gerarDAM(ParcelaValorDivida parcela, Date vencimento, Exercicio exercicio, boolean rodarEmNovaTransacao, UsuarioSistema usuario) throws Exception {
        if (rodarEmNovaTransacao) {
            return sessionContext.getBusinessObject(DAMFacade.class).geraDAM(parcela, vencimento, exercicio, null, usuario);
        } else {
            return geraDAM(parcela, vencimento, exercicio, null, usuario);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DAM geraDAM(ParcelaValorDivida parcela, Date vencimento, Exercicio exercicio, Long numerosDAM, UsuarioSistema usuario) throws Exception {
        List<ResultadoParcela> resultados = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getId())
            .executaConsulta().getResultados();
        if (resultados.isEmpty()) {
            throw new SituacaoDAMException("Não possuem débitos em aberto para a emissão do DAM");
        }
        return gerarDAMUnicoViaApi(usuario, resultados.get(0));
    }

    public void salvarHistoricoImpressao(DAM dam, UsuarioSistema usuario, HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
        HistoricoImpressaoDAM impressao = new HistoricoImpressaoDAM(dam, usuario, new Date(), tipoImpressao);
        em.merge(impressao);
    }

    public DAM geraDAM(List<ParcelaValorDivida> parcelas, Date vencimento) {
        return geraDAM(parcelas, vencimento, sistemaFacade.getExercicioCorrente(), sistemaFacade.getUsuarioCorrente());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DAM geraDAM(List<ParcelaValorDivida> parcelas, Date vencimento, Exercicio exercicio, UsuarioSistema usuario) {
        List<Long> idsParcelas = Lists.newArrayList();
        for (ParcelaValorDivida parcela : parcelas) {
            idsParcelas.add(parcela.getId());
        }
        List<ResultadoParcela> resultadosParcela = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelas)
            .executaConsulta().getResultados();
        if (resultadosParcela.size() > 1) {
            return gerarDAMCompostoViaApi(usuario, resultadosParcela, vencimento);
        } else {
            return gerarDAMUnicoViaApi(usuario, resultadosParcela.get(0));
        }
    }

    public List<DAM> buscarDamPorCadastroPessoaNumero(Cadastro cadastro, Pessoa pessoa, String numeroDam) {
        StringBuilder hql = new StringBuilder("select distinct dam from DAM dam ")
            .append(" join dam.itens item ");
        if (cadastro != null || pessoa != null) {
            hql.append(" join item.parcela.valorDivida.calculo calculo ")
                .append(" join calculo.pessoas pessoa");
        }
        hql.append(" where dam.situacao <> 'PAGO' ");
        if (cadastro != null) {
            hql.append(" and ").append(" calculo.cadastro = :cadastro ");
        }
        if (pessoa != null) {
            hql.append(" and ").append(" pessoa.pessoa = :pessoa ");
        }
        if (numeroDam != null && !numeroDam.isEmpty()) {
            hql.append(" and ").append(" dam.numeroDAM = :numero ");
        }
        hql.append(" order by dam.id desc");
        Query q = em.createQuery(hql.toString());
        if (cadastro != null) {
            q.setParameter("cadastro", cadastro);
        }
        if (pessoa != null) {
            q.setParameter("pessoa", pessoa);
        }
        if (numeroDam != null && !numeroDam.isEmpty()) {
            q.setParameter("numero", numeroDam.trim());
        }
        List<DAM> dams = q.getResultList();
        return dams;
    }

    public List<ItemDAM> itensDoDam(DAM dam) {
        if (dam != null) {
            return em.createQuery("select item from ItemDAM item where item.DAM = :dam").setParameter("dam", dam).getResultList();
        } else {
            return Lists.newArrayList();
        }
    }

    public void cancelarDamsDaParcela(ParcelaValorDivida parcela, UsuarioSistema usuario) {
        cancelarDamsDaParcela(parcela, usuario, DAM.Tipo.values());
    }

    public void cancelarDamsDaParcela(ParcelaValorDivida parcela, UsuarioSistema usuario, DAM.Tipo... tipos) {
        cancelarDamsDaParcela(parcela.getId(), usuario, tipos);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cancelarDamsDaParcela(Long idParcela, UsuarioSistema usuario, DAM.Tipo... tipos) {
        String sql = "select dam.* from DAM dam " +
            " inner join ItemDam item on item.dam_id = dam.id " +
            " where item.parcela_id = :idParcela " +
            " and dam.situacao = :aberto " +
            " and dam.tipo in (:tipos)";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("aberto", DAM.Situacao.ABERTO.name());
        List<String> strTipos = Lists.newArrayList();
        for (DAM.Tipo tipo : tipos) {
            strTipos.add(tipo.name());
        }
        q.setParameter("tipos", strTipos);
        List<DAM> dams = q.getResultList();
        for (DAM dam : dams) {
            if (!DAM.Tipo.SUBVENCAO.equals(dam.getTipo())) {
                damDAO.atualizar(dam.getId(), DAM.Situacao.CANCELADO, usuario);
            }
        }
    }

    public List<DAM> listaDamsDoValorDivida(ValorDivida valorDivida) {
        String sql = "SELECT dam.* FROM DAM dam " +
            "INNER JOIN itemDam itemDam ON itemDam.dam_id = dam.id " +
            "INNER JOIN parcelaValorDivida pvd ON pvd.id = itemdam.parcela_id " +
            "WHERE pvd.valordivida_id = :idValorDivida " +
            "  and dam.situacao = :situacaoDam " +
            "  and dam.tipo = :tipoDam ";
        Query query = em.createNativeQuery(sql, DAM.class);
        query.setParameter("idValorDivida", valorDivida.getId());
        query.setParameter("situacaoDam", DAM.Situacao.ABERTO.name());
        query.setParameter("tipoDam", DAM.Tipo.UNICO.name());
        List<DAM> dams = query.getResultList();
        for (DAM dam : dams) {
            dam.getItens().size();
        }
        return dams;
    }

    public DAM recuperaDAMpeloCalculo(Calculo calculo) {
        List<DAM> dams = recuperaDAMsPeloCalculo(calculo);
        if (!dams.isEmpty()) {
            return dams.get(0);
        }
        return null;
    }

    public List<DAM> recuperaDAMsPeloCalculo(Calculo calculo, DAM.Situacao situacao) {
        StringBuilder hql = new StringBuilder("select distinct dam from DAM dam ")
            .append(" join dam.itens item ")
            .append(" join item.parcela.valorDivida.calculo calculo")
            .append(" where calculo = :calculo ")
            .append("  and dam.tipo = :tipoDAM ");
        if (situacao != null) {
            hql.append(" and dam.situacao = :situacao");
        }
        hql.append(" order by dam.vencimento desc");
        Query q = em.createQuery(hql.toString(), DAM.class);
        q.setParameter("calculo", calculo);
        q.setParameter("tipoDAM", DAM.Tipo.UNICO);
        if (situacao != null) {
            q.setParameter("situacao", situacao);
        }
        List<DAM> dams = q.getResultList();
        for (DAM dam : dams) {
            dam.getItens().size();
        }
        return dams;
    }

    public List<DAM> recuperaDAMsPeloCalculo(Calculo calculo) {
        return recuperaDAMsPeloCalculo(calculo, DAM.Situacao.ABERTO);
    }

    public DAM recuperaDAMPeloIdParcela(Long idParcela) {
        StringBuilder hql = new StringBuilder("select distinct dam from DAM dam ")
            .append(" join dam.itens item ")
            .append(" where item.parcela.id = :idParcela")
            .append(" and trunc(dam.vencimento) >= trunc(:dataAtual)")
            .append(" and dam.situacao = :situacao and dam.tipo = :tipo");
        Query q = em.createQuery(hql.toString(), DAM.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacao", DAM.Situacao.ABERTO);
        q.setParameter("tipo", DAM.Tipo.UNICO);
        q.setParameter("dataAtual", new Date());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DAM dam = (DAM) resultList.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public DAM buscarDamPagoDaParcela(Long IdParcela) {
        StringBuilder sql = new StringBuilder("select * from DAM ")
            .append(" where id = (select max(dam.id) from dam inner join itemdam item on item.dam_id = dam.id ")
            .append(" and item.parcela_id = :idParcela and dam.situacao = :situacaoDam)");
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", IdParcela);
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DAM dam = (DAM) resultList.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public DAM buscarPrimeiroDamParcela(Long IdParcela) {
        StringBuilder sql = new StringBuilder("select * from DAM ")
            .append(" where id = (select min(dam.id) from dam inner join itemdam item on item.dam_id = dam.id ")
            .append(" and item.parcela_id = :idParcela)");
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", IdParcela);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DAM dam = (DAM) resultList.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public String getNumeroParcela(ParcelaValorDivida pvd) {
        String sql = "SELECT " +
            " pacote_consulta_de_debitos.getnumeroparcela(" +
            "  :idValorDivida," +
            "  :idOpPagamento," +
            "  :promocional," +
            "  :sequencia) " +
            " FROM dual ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idValorDivida", pvd.getValorDivida().getId());
        q.setParameter("idOpPagamento", pvd.getOpcaoPagamento().getId());
        q.setParameter("promocional", pvd.getOpcaoPagamento().getPromocional() ? 1 : 0);
        q.setParameter("sequencia", pvd.getSequenciaParcela());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }

        return "";
    }

    public DAM recuperaUltimoDamParcela(Long IdParcela) {
        String sql = "SELECT d.*  \n" +
            "   FROM dam d\n" +
            "  INNER JOIN itemdam idam ON idam.dam_id = d.id\n" +
            "  LEFT JOIN itemlotebaixa ilb ON ilb.dam_id = d.id\n" +
            "WHERE idam.parcela_id = :idParcela\n" +
            "ORDER BY ilb.datapagamento, d.id";
        Query q = em.createNativeQuery(sql.toString(), DAM.class);
        q.setParameter("idParcela", IdParcela);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DAM dam = (DAM) resultList.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public List<String> recuperaNumeroDamParcela(Long IdParcela) {
        String sql = " select d.numerodam, " +
            " lb.codigoLote, " +
            " pix.valorpago, " +
            " lb.situacaolotebaixa, " +
            " lb.TIPODEPAGAMENTOBAIXA, " +
            " (select alb.TIPOARQUIVOBANCARIOTRIBUTARIO " +
            "        from lotebaixadoarquivo lba " +
            "        inner join arquivolotebaixa alb on lba.ARQUIVOLOTEBAIXA_ID = alb.ID " +
            "        where lba.LOTEBAIXA_ID = lb.ID fetch first 1 rows only)," +
            " pib.id " +
            "   from dam d " +
            "   inner join itemdam idam on idam.dam_id = d.id " +
            "   left join itemlotebaixa ilb on ilb.dam_id = d.id " +
            "   left join lotebaixa lb on lb.id = ilb.lotebaixa_id " +
            "   left join pix on d.PIX_ID = pix.ID " +
            "   left join pagamentointernetbanking pib on pib.id = (select max(pib.id) " +
            "                                                          from pagamentointernetbanking pib" +
            "                                                       where pib.dam_id = d.id) " +
            " where idam.parcela_id = :idparcela " +
            "   and d.situacao in (:situacoes_dam) " +
            "   and (ilb.id is null or lb.situacaolotebaixa not in (:situacoes_lote_baixa)) " +
            " order by ilb.datapagamento desc, d.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idparcela", IdParcela);
        q.setParameter("situacoes_dam", Lists.newArrayList(DAM.Situacao.ABERTO.name(), DAM.Situacao.PAGO.name()));
        q.setParameter("situacoes_lote_baixa", Lists.newArrayList(SituacaoLoteBaixa.ESTORNADO));
        List<Object[]> resultList = q.getResultList();
        List<String> retorno = new ArrayList<>();
        if (!resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                String info = (String) obj[0];
                if (obj[1] != null && (obj[3].equals(SituacaoLoteBaixa.BAIXADO.name()) || obj[3].equals(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()))) {
                    info += " (Lote: " + obj[1];
                    if (obj[4] != null) info += " (" + TipoDePagamentoBaixa.valueOf(obj[4].toString()).getDescricao();
                    if (obj[5] != null) info += " - " + obj[5];
                    info += "))";
                } else if (obj[2] != null && ((BigDecimal) obj[2]).compareTo(new BigDecimal("0")) > 0) {
                    info = info + " (Pago por PIX)";
                } else if (obj[6] != null) {
                    info = info + " (Pago por Internet Banking)";
                }
                retorno.add(info);
            }
        }
        return retorno;
    }

    public Date recuperaDataPagamentoDAM(DAM dam) {
        try {
            String hql = "select max(lb.dataPagamento) from LoteBaixa lb " +
                " inner join lb.itemLoteBaixa ilb " +
                " inner join ilb.itemParcelas ilbp " +
                " inner join ilbp.itemDam id " +
                " inner join id.DAM dam" +
                " where dam = :dam and lb.situacaoLoteBaixa in (:situacoes)";
            Query q = em.createQuery(hql);
            q.setParameter("dam", dam);
            q.setParameter("situacoes", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO, SituacaoLoteBaixa.BAIXADO_INCONSITENTE));

            Date retorno = (Date) q.getSingleResult();
            if (retorno == null) {
                List<ParcelaValorDivida> listaParcelasDoDam = Lists.newArrayList();
                for (ItemDAM item : dam.getItens()) {
                    listaParcelasDoDam.add(item.getParcela());
                }

                hql = "select max(processo.dataPagamento) from ItemProcessoDebito item " +
                    " join item.processoDebito processo " +
                    " where processo.tipo = :tipoProcesso " +
                    " and processo.situacao = :situacaoProcesso " +
                    " and item.parcela in (:parcelas) ";
                q = em.createQuery(hql);
                q.setParameter("tipoProcesso", TipoProcessoDebito.BAIXA);
                q.setParameter("situacaoProcesso", SituacaoProcessoDebito.FINALIZADO);
                q.setParameter("parcelas", listaParcelasDoDam);
                retorno = (Date) q.getSingleResult();

                if (retorno == null) {
                    hql = "select max(avulso.dataPagamento) from PagamentoAvulso avulso " +
                        " where coalesce(avulso.ativo,1) = 1 " +
                        " and avulso.parcelaValorDivida in (:parcelas) ";
                    q = em.createQuery(hql);
                    q.setParameter("parcelas", listaParcelasDoDam);
                    retorno = (Date) q.getSingleResult();
                }
            }
            return retorno;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<HistoricoImpressaoDAM> listaHistoricoImpressaoDam(DAM dam) {
        if (dam.getId() != null) {
            String sql = "select h.* from HistoricoImpressaoDAM h " +
                " where h.dam_id = :idDam ";
            if (DAM.Tipo.COMPOSTO.equals(dam.getTipo())) {
                sql += " and h.parcela_id = :idPrimeiraParcela ";
            }
            sql += " order by h.dataOperacao desc";
            Query q = em.createNativeQuery(sql, HistoricoImpressaoDAM.class);
            q.setParameter("idDam", dam.getId());
            if (DAM.Tipo.COMPOSTO.equals(dam.getTipo())) {
                q.setParameter("idPrimeiraParcela", dam.getItens().get(0).getParcela().getId());
            }
            return q.getResultList();
        }
        return null;
    }

    public List<HistoricoSituacaoDAM> buscarHistoricoDeSituacoesDam(Long idDam) {
        String sql = " select hist.* from historicosituacaodam hist " +
            " where hist.dam_id = :idDam " +
            " order by hist.datasituacao ";

        Query q = em.createNativeQuery(sql, HistoricoSituacaoDAM.class);
        q.setParameter("idDam", idDam);

        List<HistoricoSituacaoDAM> historicos = q.getResultList();
        return historicos != null ? historicos : Lists.<HistoricoSituacaoDAM>newArrayList();
    }

    public DAM recuperaDAM(Long numero, Exercicio exercicio) {
        Query query = em.createQuery("select dam from DAM dam where dam.numero = :numero and dam.exercicio = :exercicio order by dam.sequencia desc");
        query.setParameter("numero", numero);
        query.setParameter("exercicio", exercicio);
        if (!query.getResultList().isEmpty()) {
            DAM dam = (DAM) query.getResultList().get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public DAM verificarSeExisteDamVencidoAndRetornarDamDaParcela(ParcelaValorDivida parcela, Date dataReferencia) {
        StringBuilder hql = new StringBuilder();
        hql.append("select dam from DAM dam join dam.itens item")
            .append(" where item.parcela = :parcela")
            .append(" and dam.tipo in :tipos ")
            .append(" and dam.situacao = :situacaoDAM")
            .append(" and to_date(:dataReferencia,'dd/MM/yyyy') <= to_date(item.parcela.vencimento,'dd/MM/yyyy') ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("parcela", parcela);
        q.setParameter("tipos", Arrays.asList(DAM.Tipo.values()));
        q.setParameter("situacaoDAM", DAM.Situacao.ABERTO);
        q.setParameter("dataReferencia", dataReferencia);
        List<DAM> dams = q.getResultList();
        if (!dams.isEmpty()) {
            DAM dam = dams.get(0);
            Hibernate.initialize(dam.getItens());
            return dam;
        }
        return null;
    }

    public DAM criarDAMParaRegistroDAF607(UsuarioSistema usuarioSistema,
                                          ParcelaValorDivida parcelaValorDivida) {
        List<ResultadoParcela> resultadosParcela = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcelaValorDivida.getId())
            .executaConsulta().getResultados();
        return gerarDAMUnicoViaApi(usuarioSistema, resultadosParcela.get(0), Boolean.FALSE);
    }

    public List<DAM> buscarDamsAgrupadosDaParcela(Long idParcela) {
        String sql = "select dam.* from ItemDam idam " +
            " inner join Dam dam on dam.id = idam.dam_id " +
            " where idam.parcela_id = :idParcela" +
            "  and dam.tipo = :tipoDam " +
            "  and dam.situacao = :situacao";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("tipoDam", DAM.Tipo.COMPOSTO.name());
        q.setParameter("situacao", DAM.Situacao.ABERTO.name());
        List<DAM> dams = q.getResultList();
        for (DAM dam : dams) {
            Hibernate.initialize(dam.getItens());
        }
        return dams;
    }

    public DAM buscarOuGerarDam(ResultadoParcela parcela) {
        return gerarDAMUnicoViaApi(sistemaFacade.getUsuarioCorrente(), parcela);
    }

    public List<DAM> gerarDAMPortalWeb(List<ResultadoParcela> parcelas) {
        List<DAM> dams = Lists.newArrayList();
        try {
            for (ResultadoParcela resultado : parcelas) {
                if (!resultado.getBloqueiaImpressao()) {
                    dams.add(buscarOuGerarDam(resultado));
                }
            }
            return dams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] gerarImpressaoDAMNfse(List<DAM> dams) {
        return gerarImpressaoDAM(dams, HistoricoImpressaoDAM.TipoImpressao.NFSE);
    }

    private byte[] gerarImpressaoDAM(List<DAM> dams, HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        return imprimeDAM.gerarByteImpressaoDamUnicoViaApi(dams, tipoImpressao);
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> resultadoParcelas, Date vencimentoDam, UsuarioSistema usuarioSistema) {
        return gerarDamAgrupado(resultadoParcelas, vencimentoDam, null, usuarioSistema);
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> resultadoParcelas, Date vencimentoDam, Exercicio exercicio, UsuarioSistema usuarioSistema) {
        return gerarDAMCompostoViaApi(usuarioSistema, resultadoParcelas, vencimentoDam);
    }

    public boolean verificarParcelasDoDam(DAM dam, List<ResultadoParcela> resultadoParcelas) {
        for (ItemDAM itemDAM : dam.getItens()) {
            boolean temParcela = false;
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if (resultadoParcela.getIdParcela().equals(itemDAM.getParcela().getId())) {
                    temParcela = true;
                    break;
                }
            }
            if (!temParcela) {
                return false;
            }
        }
        return !dam.getItens().isEmpty();
    }

    public DAM buscarDAMPorNumeroDAM(String numeroDam) {
        String sql = " select dam.* from dam " +
            " where dam.numerodam = :numeroDam ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("numeroDam", numeroDam);

        List<DAM> dans = q.getResultList();
        return (dans != null && !dans.isEmpty()) ? dans.get(0) : null;
    }

    public Date ajustarDataUtil(Date data) {
        return DataUtil.ajustarDataUtil(data, feriadoFacade);
    }

    public ConfiguracaoWebService getConfiguracaoWsDAM() {
        ConfiguracaoWebService configWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.DAM);
        if (configWs == null) {
            throw new ValidacaoException("Nenhuma configuração de webservice para geração do DAM foi encontrada.");
        }
        return configWs;
    }

    public DAM gerarDAMUnicoViaApi(UsuarioSistema usuarioSistema,
                                   ResultadoParcela resultadoParcela) {
        return gerarDAMUnicoViaApi(usuarioSistema, resultadoParcela, Boolean.TRUE);
    }

    private DAM gerarDAMUnicoViaApi(UsuarioSistema usuarioSistema,
                                   ResultadoParcela resultadoParcela,
                                   Boolean validarParcelaEmAberto) {
        try {
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdParcela(resultadoParcela.getIdParcela());
            assistenteDAM.setUsuario(usuarioSistema != null ? usuarioSistema.getLogin() : null);
            assistenteDAM.setValidarParcelaEmAberto(validarParcelaEmAberto);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configuracaoWsDAM = getConfiguracaoWsDAM();
            ResponseEntity<Long> response = restTemplate
                .exchange(configuracaoWsDAM.getUrl() + "/gerar-dam-unico", HttpMethod.POST, httpEntity, Long.class);
            return recuperar(response.getBody());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public DAM gerarDAMCompostoViaApi(UsuarioSistema usuarioSistema,
                                      List<ResultadoParcela> resultadoParcelas,
                                      Date vencimentoDam) {
        try {
            List<Long> idsParcela = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                idsParcela.add(resultadoParcela.getIdParcela());
            }
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setUsuario(usuarioSistema != null ? usuarioSistema.getLogin() : null);
            assistenteDAM.setIdsParcela(idsParcela);
            assistenteDAM.setVencimentoDAM(vencimentoDam);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configuracaoWsDAM = getConfiguracaoWsDAM();
            ResponseEntity<Long> response = restTemplate.exchange(configuracaoWsDAM.getUrl() + "/gerar-dam-composto", HttpMethod.POST,
                httpEntity, Long.class);
            return recuperar(response.getBody());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public DAM gerarDAMUnicoSubvencaoViaApi(SubvencaoParcela subvencaoParcela) {
        try {
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdSubvencaoParcela(subvencaoParcela.getId());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configuracaoWsDAM = getConfiguracaoWsDAM();
            ResponseEntity<Long> response = restTemplate.exchange(configuracaoWsDAM.getUrl() + "/gerar-dam-unico-subvencao", HttpMethod.POST,
                httpEntity, Long.class);
            return recuperar(response.getBody());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public DAM gerarDAMCompostoSubvencaoViaApi(List<SubvencaoParcela> subvencaoParcelas) {
        try {
            List<Long> idsSubvencaoParcela = Lists.newArrayList();
            for (SubvencaoParcela subvencaoParcela : subvencaoParcelas) {
                idsSubvencaoParcela.add(subvencaoParcela.getId());
            }
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdsSubvencaoParcela(idsSubvencaoParcela);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configuracaoWsDAM = getConfiguracaoWsDAM();
            ResponseEntity<Long> response = restTemplate.exchange(configuracaoWsDAM.getUrl() + "/gerar-dam-composto-subvencao", HttpMethod.POST,
                httpEntity, Long.class);
            return recuperar(response.getBody());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public DAM gerarDAMUnicoInconsistenciaViaApi(ValorDivida valorDivida, UsuarioSistema usuarioSistema) {
        try {
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdValorDivida(valorDivida.getId());
            assistenteDAM.setUsuario(usuarioSistema != null ? usuarioSistema.getLogin() : null);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configuracaoWsDAM = getConfiguracaoWsDAM();
            ResponseEntity<Long> response = restTemplate.exchange(configuracaoWsDAM.getUrl() + "/gerar-dam-unico-inconsistencia", HttpMethod.POST,
                httpEntity, Long.class);
            return recuperar(response.getBody());
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public DAM recuperarDAM(Long idParcela) {
        List<DAM> dams = em.createQuery("select dam " +
            "                  from ItemDAM " +
            "               item join item.DAM dam " +
            "               where item.parcela.id = :id ")
            .setParameter("id", idParcela)
            .getResultList();
        return !dams.isEmpty() ? dams.get(0) : null;
    }
}
