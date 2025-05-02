package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOOrdemGeralMonitoramento;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoMonitoramentoFiscal;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author octavio
 */
@Stateless
public class OrdemGeralMonitoramentoFacade extends AbstractFacade<OrdemGeralMonitoramento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade;
    @EJB
    private MonitoramentoFiscalFacade monitoramentoFiscalFacade;

    public OrdemGeralMonitoramentoFacade() {
        super(OrdemGeralMonitoramento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntityManager getEm() {
        return em;
    }

    @Override
    public OrdemGeralMonitoramento recuperar(Object id) {
        OrdemGeralMonitoramento ordemGeralMonitoramento = em.find(OrdemGeralMonitoramento.class, id);
        Hibernate.initialize(ordemGeralMonitoramento.getMonitoramentosFiscais());
        if (ordemGeralMonitoramento.getMonitoramentosFiscais() != null) {
            for (MonitoramentoFiscal monitoramentosFiscais : ordemGeralMonitoramento.getMonitoramentosFiscais()) {
                Hibernate.initialize(monitoramentosFiscais.getFiscaisMonitoramento());
            }
        }
        if (ordemGeralMonitoramento.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(ordemGeralMonitoramento.getDetentorArquivoComposicao().getArquivosComposicao());

        }
        if (ordemGeralMonitoramento.getMonitoramentosFiscais() != null) {
            for (MonitoramentoFiscal monitoramentos : ordemGeralMonitoramento.getMonitoramentosFiscais()) {
                Hibernate.initialize(monitoramentos.getLevantamentosUFM());
                Hibernate.initialize(monitoramentos.getHistoricoSituacoesMonitoramentoFiscal());
            }
        }
        return ordemGeralMonitoramento;
    }

    public OrdemGeralMonitoramento salvarRetornando(OrdemGeralMonitoramento entity) {
        return em.merge(entity);
    }

    public OrdemGeralMonitoramento salva(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        return em.merge(ordemGeralMonitoramento);
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public void setUsuarioSistemaFacade(UsuarioSistemaFacade usuarioSistemaFacade) {
        this.usuarioSistemaFacade = usuarioSistemaFacade;
    }

    public ServicoFacade getServicoFacade() {
        return servicoFacade;
    }

    public void setServicoFacade(ServicoFacade servicoFacade) {
        this.servicoFacade = servicoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public MonitoramentoFiscalFacade getMonitoramentoFiscalFacade() {
        return monitoramentoFiscalFacade;
    }

    public void setMonitoramentoFiscalFacade(MonitoramentoFiscalFacade monitoramentoFiscalFacade) {
        this.monitoramentoFiscalFacade = monitoramentoFiscalFacade;
    }

    public List<CadastroEconomico> recuperarPorFiltroCmc(List<Servico> servicos,
                                                         ClassificacaoAtividade classificacaoAtividade,
                                                         String bairro, Date dataInicial,
                                                         Date dataFinal,
                                                         BigDecimal valorInicial,
                                                         BigDecimal valorFinal,
                                                         SituacaoCadastralCadastroEconomico situacao,
                                                         String endereco) {

        String hql = "select ce from CadastroEconomico ce "
            + " join ce.pessoa p "
            + " join p.enderecoscorreio endereco "
            + " join fetch ce.situacaoCadastroEconomico situacao"
            + " left join ce.economicoCNAE ecnae "
            + " left join ce.servico  servico "
            + " left join ce.situacaoCadastroEconomico situacoesCMC"
            + " left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null "
            + " where situacoesCMC =  (select max(situacao.id) from CadastroEconomico cad "
            + " inner join cad.situacaoCadastroEconomico situacao where cad = ce "
            + " and situacoesCMC.situacaoCadastral in (:situacoes))";

        if (servicos != null && !servicos.isEmpty()) {
            hql += " and servico in (:filtroServicos)";
        }
        if (classificacaoAtividade != null) {
            hql += " and ce.classificacaoAtividade = :filtroAtividade ";
        }
        if (endereco != null) {
            hql += " and lower(endereco.logradouro) like :filtroLogradouro ";
        }
        if (bairro != null) {
            hql += " and lower(endereco.bairro) like :filtroBairro ";
        }
        if (situacao != null) {
            hql += " and situacao.dataRegistro = (select max(situacao2.dataRegistro) from CadastroEconomico ce2 "
                + " join ce2.situacaoCadastroEconomico situacao2 where ce2 = ce) and situacao.situacaoCadastral = :situacao  ";
        }
        if (valorInicial != null && valorFinal != null && dataInicial != null || dataFinal != null) {
            hql += " and (select sum(iss.baseCalculo) from CalculoISS iss where iss.cadastroEconomico = ce and iss in (select vd.calculo from ValorDivida vd where vd.calculo.cadastro = ce and vd.emissao >= :filtroDataInicial and vd.emissao <= :filtroDataFinal)) between :filtroValorInicial and :filtroValorFinal ";
        }
        if (endereco != null && !endereco.trim().isEmpty()) {
            hql += " and lower(endereco.logradouro) like  :filtroLogradouro ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR, SituacaoCadastralCadastroEconomico.SUSPENSA));

        if (servicos != null && !servicos.isEmpty()) {
            q.setParameter("filtroServicos", servicos);
        }
        if (classificacaoAtividade != null) {
            q.setParameter("filtroAtividade", classificacaoAtividade);
        }
        if (situacao != null) {
            q.setParameter("situacao", situacao);
        }
        if (bairro != null) {
            q.setParameter("filtroBairro", "%" + bairro.trim().toLowerCase() + "%");
        }
        if (endereco != null && !endereco.trim().isEmpty()) {
            q.setParameter("filtroLogradouro", "%" + endereco.trim().toLowerCase() + "%");
        }
        if (valorInicial != null && valorFinal != null && dataInicial != null || dataFinal != null) {
            q.setParameter("filtroDataInicial", dataInicial);
            q.setParameter("filtroDataFinal", dataFinal);
            q.setParameter("filtroValorInicial", valorInicial);
            q.setParameter("filtroValorFinal", valorFinal);
        }

        return new ArrayList<>(new HashSet<>((ArrayList<CadastroEconomico>) q.getResultList()));
    }

    public CadastroEconomico recuperaPessoaCadastroEconomicoMF(MonitoramentoFiscal monitoramentoFiscal) {
        String hql = "select cmc from MonitoramentoFiscal mf "
            + "join mf.cadastroEconomico cmc "
            + "where mf = :monitoramento";
        Query q = em.createQuery(hql);
        q.setParameter("monitoramento", monitoramentoFiscal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (CadastroEconomico) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<FiscalMonitoramento> fiscaisDoMonitoramento(MonitoramentoFiscal monitoramento) {
        return em.createQuery("select fm from FiscalMonitoramento fm " +
            "where fm.monitoramentoFiscal = :monitoramento").setParameter("monitoramento", monitoramento).getResultList();
    }

    public List<MonitoramentoFiscal> monitoramentosDaOrdem(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        return em.createQuery("select mf from MonitoramentoFiscal mf " +
            "where mf.ordemGeralMonitoramento = :ordemGeralMonitoramento").setParameter("ordemGeralMonitoramento", ordemGeralMonitoramento).getResultList();
    }

    public Boolean isCadastroEconomicoNaMesmaData(MonitoramentoFiscal monitoramento, Date dataInicio, Date dataFim) {
        String sql = "select * from MONITORAMENTOFISCAL " +
            "where CADASTROECONOMICO_ID = :ce " +
            "and (( to_date( :dataInicial, 'dd/MM/yyyy') between DATAINICIALMONITORAMENTO and DATAFINALMONITORAMENTO) " +
            "or ( to_date( :dataFinal, 'dd/MM/yyyy') between DATAINICIALMONITORAMENTO and DATAFINALMONITORAMENTO)) " +
            "and SITUACAOMF <> :situacaoMonitoramento";
        if (monitoramento.getId() != null) {
            sql += " and monitoramentoFiscal.id <> :id";
        }
        Query q = em.createNativeQuery(sql, MonitoramentoFiscal.class);
        q.setParameter("ce", monitoramento.getCadastroEconomico());
        q.setParameter("situacaoMonitoramento", SituacaoMonitoramentoFiscal.CANCELADO.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicio));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFim));
        if (monitoramento.getId() != null) {
            q.setParameter("id", monitoramento.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public void criarNotificacaoUsuarioLogado(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        String msg = "Nova ordem geral de monitoramento criada com prazo da data programada maior que o prazo do parâmetro de monitoramento fiscal!";
        gerarNotificacao(ordemGeralMonitoramento, msg, ordemGeralMonitoramento.getUsuarioLogado());
        ParametroMonitoramentoFiscal parametroMonitoramentoFiscal = parametroMonitoramentoFiscalFacade.retornarParametroExercicioCorrente();
        if (parametroMonitoramentoFiscal != null && parametroMonitoramentoFiscal.getDiretorDepartamento() != null) {
            UsuarioSistema usuarioSistema = usuarioSistemaFacade.usuarioSistemaDaPessoa(parametroMonitoramentoFiscal.getDiretorDepartamento());
            if (usuarioSistema != null) {
                gerarNotificacao(ordemGeralMonitoramento, msg, usuarioSistema);
            }
        }
    }

    private void gerarNotificacao(OrdemGeralMonitoramento ordemGeralMonitoramento, String msg, UsuarioSistema usuarioSistema) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_PRAZO_MONITORAMENTO_FISCAL_MAIOR.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_PRAZO_MONITORAMENTO_FISCAL_MAIOR);
        notificacao.setUsuarioSistema(usuarioSistema);
        notificacao.setLink("/fiscalizacao/ordem-geral-monitoramento/ver/" + ordemGeralMonitoramento.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<VOOrdemGeralMonitoramento> buscarDadosRelatorio(CriteriaQuery criteriaComOsFiltros) {
        List<OrdemGeralMonitoramento> ordens = em.createQuery(criteriaComOsFiltros).getResultList();
        List<VOOrdemGeralMonitoramento> ordensVO = Lists.newArrayList();
        for (OrdemGeralMonitoramento empresaIrregular : ordens) {
            ordensVO.add(new VOOrdemGeralMonitoramento(empresaIrregular));
        }
        return ordensVO;
    }
}
