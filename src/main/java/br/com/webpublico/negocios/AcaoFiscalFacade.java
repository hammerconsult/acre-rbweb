/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorJuros;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorMulta;
import br.com.webpublico.tributario.consultadebitos.dtos.ConfiguracaoAcrescimosDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.OrdenaSituacaoAcaoFiscalPorData;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Claudio
 */
@Stateless
public class AcaoFiscalFacade extends AbstractFacade<AcaoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ProgramacaoFiscalFacade programacaoFiscalFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private NumeroSerieFacade numeroSerieFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private MultaFiscalizacaoFacade multaFiscalizacaoFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private CadastroAidfFacade aifFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;

    public AcaoFiscalFacade() {
        super(AcaoFiscal.class);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public MultaFiscalizacaoFacade getMultaFiscalizacaoFacade() {
        return multaFiscalizacaoFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public CadastroAidfFacade getAifFacade() {
        return aifFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AcaoFiscal recuperar(Object id) {
        AcaoFiscal af = em.find(AcaoFiscal.class, id);
        Hibernate.initialize(af.getSituacoesAcaoFiscal());
        OrdenaSituacaoAcaoFiscalPorData ordenaSituacaoAcaoFiscalPorData = new OrdenaSituacaoAcaoFiscalPorData();
        Collections.sort(af.getSituacoesAcaoFiscal(), ordenaSituacaoAcaoFiscalPorData);
        Hibernate.initialize(af.getFiscalDesignados());
        Hibernate.initialize(af.getDoctosAcaoFiscal());
        Hibernate.initialize(af.getLancamentoDoctoFiscal());
        Collections.sort(af.getLancamentoDoctoFiscal());
        Hibernate.initialize(af.getLevantamentosContabeis());
        Hibernate.initialize(af.getCadastroEconomico());
        Hibernate.initialize(af.getLancamentosContabeis());
        Hibernate.initialize(af.getArquivos());
        Hibernate.initialize(af.getAlteracoesDataArbitramento());
        if (af.getProgramacaoFiscal().getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(af.getProgramacaoFiscal().getDetentorArquivoComposicao().getArquivosComposicao());
        }
        for (RegistroLancamentoContabil reg : af.getLancamentosContabeis()) {
            reg = em.find(RegistroLancamentoContabil.class, reg.getId());
            Hibernate.initialize(reg.getLancamentosContabeis());
            Hibernate.initialize(reg.getMultas());
            Hibernate.initialize(reg.getAutosInfracao());
            Collections.sort(reg.getMultas());
            Collections.sort(reg.getAutosInfracao());
        }
        return af;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ServicoFacade getServicoFacade() {
        return servicoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public ProgramacaoFiscalFacade getProgramacaoFiscalFacade() {
        return programacaoFiscalFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public NumeroSerieFacade getNumeroSerieFacade() {
        return numeroSerieFacade;
    }

    public ParametroFiscalizacaoFacade getParametroFiscalizacaoFacade() {
        return parametroFiscalizacaoFacade;
    }

    @Override
    public List<AcaoFiscal> lista() {
        return em.createQuery("select a from AcaoFiscal a").getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AcaoFiscal> salvarAssincrono(AssistenteBarraProgresso assistente) {
        assistente.setTotal(2);
        assistente.conta();
        assistente.setSelecionado(em.merge(assistente.getSelecionado()));
        assistente.conta();
        assistente.setExecutando(false);
        return new AsyncResult<>((AcaoFiscal) assistente.getSelecionado());
    }

    public void salvarLancamentoContabil(LancamentoContabilFiscal lancamento) {
        em.merge(lancamento);
    }

    public CadastroEconomico buscarCadastroEconomicoDaAcaoFiscal(AcaoFiscal acaoFiscal) {
        String hql = "select cmc from AcaoFiscal af "
            + "join af.cadastroEconomico cmc "
            + "where af = :acao";
        Query q = em.createQuery(hql);
        q.setParameter("acao", acaoFiscal);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (CadastroEconomico) resultList.get(0);
        } else {
            return null;
        }
    }

    public List<ItemCalculoIss> buscarLancamentoDeISSParaAcaoFiscal(Mes mes, Integer ano, CadastroEconomico cadastroEconomico) {
        String sql = "SELECT distinct item.* FROM itemcalculoiss item "
            + " inner join calculoiss cal on cal.id = item.calculo_id "
            + " inner join valordivida vd on vd.calculo_id = cal.id "
            + " inner join processocalculo pro on pro.id = cal.processocalculoiss_id "
            + " inner join processocalculoiss proiss on proiss.id = cal.processocalculoiss_id "
            + " inner join exercicio ex on ex.id = pro.exercicio_id "
            + " where cal.tipocalculoiss = :tipocalculoiss "
            + " and cal.tipoSituacaoCalculoISS = :tiposituacaocalculoiss "
            + " and cal.cadastroeconomico_id = :cadastroEconomico "
            + " and proiss.mesreferencia = :mes "
            + " and ex.ano = :ano "
            + " and vd.divida_id in (:dividas) "
            + " and not exists (select pvd.id from parcelavalordivida pvd "
            + "                 inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id "
            + "                 where spvd.situacaoparcela in (:situacoesCancelamento) "
            + "                 and pvd.valordivida_id = vd.id) ";

        Query q = em.createNativeQuery(sql, ItemCalculoIss.class);
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("ano", ano);
        q.setParameter("cadastroEconomico", cadastroEconomico.getId());
        q.setParameter("tipocalculoiss", TipoCalculoISS.MENSAL.name());
        q.setParameter("tiposituacaocalculoiss", TipoSituacaoCalculoISS.LANCADO.name());
        q.setParameter("dividas", dividaFacade.buscarIdsDividasComParametroFiscalizacao(sistemaFacade.getExercicioCorrente()));
        q.setParameter("situacoesCancelamento", montarParametroInSituacoesParcela());
        return q.getResultList();
    }

    public Integer buscarNumeroAutoInfracaoMaisUm(Integer ano) {
        Query q = em.createNativeQuery("select coalesce(max(numero), 0) + 1 as numero from AutoInfracaoFiscal where ano = :ano");
        q.setParameter("ano", ano);

        return Integer.parseInt(q.getSingleResult().toString());
    }

    public boolean hasUsuarioDesignado(AcaoFiscal af, UsuarioSistema usuarioCorrente) {
        String sql = "select f.id from FiscalDesignado f "
            + "where f.acaoFiscal_id = :acao "
            + "and f.usuarioSistema_id = :usuario "
            + "and u.situacaofiscal = 'ATIVO'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("acao", af.getId());
        q.setParameter("usuario", usuarioCorrente.getId());
        return !q.getResultList().isEmpty();
    }

    public Long getNumeroHomologacaoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(numeroHomologacao), 0) + 1 as numerohomologacao from AcaoFiscal where ano = :ano");
        q.setParameter("ano", sistemaFacade.getExercicioCorrente().getAno());

        return Long.valueOf(q.getSingleResult().toString());
    }

    public AcaoFiscal recuperarPorAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal) {
        String hql = "select auto.registro.acaoFiscal from AutoInfracaoFiscal auto where auto = :autoInfracao";
        Query q = em.createQuery(hql);
        q.setParameter("autoInfracao", autoInfracaoFiscal);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            AcaoFiscal acao = (AcaoFiscal) resultList.get(0);
            Hibernate.initialize(acao.getSituacoesAcaoFiscal());
            return acao;
        }
    }

    public BigDecimal buscaValorPagoISS(LancamentoContabilFiscal lancamentoContabilFiscal, CadastroEconomico cadastroEconomico) {
        List<ItemCalculoIss> itens = buscarLancamentoDeISSParaAcaoFiscal(lancamentoContabilFiscal.getMes(), lancamentoContabilFiscal.getAno(), cadastroEconomico);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : itens) {
            if (item.getAliquota().compareTo(lancamentoContabilFiscal.getAliquotaISS()) == 0) {
                total = total.add(item.getValorCalculado());
            }
        }
        return total;
    }

    public boolean verificarFiscalSupervisor(UsuarioSistema usuarioSistema) {
        TipoUsuarioTribUsuario tipoUsuario = usuarioSistemaFacade.listaTipoUsuarioVigenteDoUsuarioPorTipo(usuarioSistema, TipoUsuarioTributario.FISCAL_TRIBUTARIO);
        if (tipoUsuario != null) {
            return tipoUsuario.getSupervisor();
        }
        return false;
    }

    public AcaoFiscal mergeAcaoFiscal(AcaoFiscal selecionado) {
        for (RegistroLancamentoContabil r : selecionado.getLancamentosContabeis()) {
            for (LancamentoMultaFiscal l : r.getMultas()) {
                if (l.getLancamentoContabilFiscal() != null) {
                    l.setLancamentoContabilFiscal(em.merge(l.getLancamentoContabilFiscal()));
                }
            }
        }
        return em.merge(selecionado);
    }


    public boolean verificaFiscalizacaoParaMesmoCadastroEconomico(AcaoFiscal acaoFiscal) {
        String sql = "select * from AcaoFiscal "
            + "where cadastroEconomico_id = :ce "
            + "and programacaoFiscal_id = :programacao "
            + "and (dataLevantamentoInicial between :dataInicial and :dataFinal "
            + "or dataLevantamentoFinal between :dataInicial and :dataFinal "
            + "or (:dataInicial <= dataLevantamentoInicial "
            + "and :dataFinal >= dataLevantamentoFinal) "
            + "or (:dataInicial >= dataLevantamentoInicial "
            + "and :dataFinal <= dataLevantamentoFinal)) "
            + "and situacaoAcaoFiscal <> '" + SituacaoAcaoFiscal.CANCELADO.name() + "' ";
        Query q = em.createNativeQuery(sql, AcaoFiscal.class);
        q.setParameter("ce", acaoFiscal.getCadastroEconomico().getId());
        q.setParameter("dataInicial", acaoFiscal.getDataLevantamentoInicial());
        q.setParameter("dataFinal", acaoFiscal.getDataLevantamentoFinal());
        q.setParameter("programacao", acaoFiscal.getProgramacaoFiscal().getId());
        return (!q.getResultList().isEmpty());
    }

    public boolean verificaRefiscalizacao(AcaoFiscal acaoFiscal) {
        String sql = "select * from AcaoFiscal "
            + "where cadastroEconomico_id = :ce "
            + "and programacaoFiscal_id <> :programacao "
            + "and (dataLevantamentoInicial between :dataInicial and :dataFinal "
            + "or dataLevantamentoFinal between :dataInicial and :dataFinal "
            + "or (:dataInicial <= dataLevantamentoInicial "
            + "and :dataFinal >= dataLevantamentoFinal) "
            + "or (:dataInicial >= dataLevantamentoInicial "
            + "and :dataFinal <= dataLevantamentoFinal)) "
            + "and situacaoAcaoFiscal <> '" + SituacaoAcaoFiscal.CANCELADO.name() + "' ";
        Query q = em.createNativeQuery(sql, AcaoFiscal.class);
        q.setParameter("ce", acaoFiscal.getCadastroEconomico().getId());
        q.setParameter("dataInicial", acaoFiscal.getDataLevantamentoInicial());
        q.setParameter("dataFinal", acaoFiscal.getDataLevantamentoFinal());
        q.setParameter("programacao", acaoFiscal.getProgramacaoFiscal().getId());
        return (!q.getResultList().isEmpty());
    }

    public boolean validaUsuarioSupervisor(String loginSupervisor, String senhaSupervisor) {
        UsuarioSistema usuarioSistema = sistemaFacade.recuperaUsuarioPorLogin(loginSupervisor);
        if (usuarioSistema == null) {
            return false;
        } else {
            boolean usuarioValido = usuarioSistema.isAutenticacaoCorreta(senhaSupervisor);
            if (usuarioValido) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<CadastroAidf> listaCadastroAidfPorCadastroEconomico(AcaoFiscal acaoFiscal) {
        String hql = "from CadastroAidf ca where ca.cadastroEconomico = :cadastro";
        Query q = this.em.createQuery(hql);
        q.setParameter("cadastro", acaoFiscal.getCadastroEconomico());
        return q.getResultList();
    }

    public Long retornaUltimoNumeroLancamentoFiscal() {
        Long num;
        String sql = " select max(coalesce(obj.numeroLevantamento,0)) from AcaoFiscal obj ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            BigDecimal b = (BigDecimal) resultList.get(0);
            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public List<BigDecimal> buscarAliquotasServicos(CadastroEconomico cadastroEconomico) {
        String sql = "select distinct s.aliquotaisshomologado from servico s join cadastroeconomico_servico sce on s.id = sce.servico_id where  sce.cadastroeconomico_id = :bce order by s.aliquotaisshomologado";
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("bce", cadastroEconomico.getId());
        return q.getResultList();
    }

    public Integer getNumeroTermo() {
        Query q = em.createNativeQuery("select ano || max(substr(numerotermofiscalizacao,5)) + 1 as numerotermofiscalizacao " +
            "from AcaoFiscal " +
            "where ano = :ano " +
            "and NUMEROTERMOFISCALIZACAO is not null " +
            "group by ano ");
        q.setParameter("ano", sistemaFacade.getExercicioCorrente().getAno());
        List resultList = q.getResultList();
        if(!resultList.isEmpty()){
            return Integer.valueOf(resultList.get(0).toString());
        }
        return Integer.valueOf(sistemaFacade.getExercicioCorrente().getAno() + "0001");
    }

    public AcaoFiscal concluiAcao(AcaoFiscal acao) {
        if (naoTemRegistroAberto(acao)) {
            acao.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CONCLUIDO);
            SituacoesAcaoFiscal s = new SituacoesAcaoFiscal();
            s.setAcaoFiscal(acao);
            s.setData(new Date());
            s.setSituacaoAcaoFiscal(SituacaoAcaoFiscal.CONCLUIDO);
            acao = em.merge(acao);
        }
        programacaoFiscalFacade.concluiProgramacao(acao.getProgramacaoFiscal());
        return acao;
    }

    private boolean naoTemRegistroAberto(AcaoFiscal acao) {
        return em.createQuery("select r from RegistroLancamentoContabil r where r.situacao = 'AGUARDANDO' and r.acaoFiscal = :acaoFiscal").setParameter("acaoFiscal", acao).getResultList().isEmpty();
    }

    public boolean usuarioPodeReabrir(UsuarioSistema usuarioCorrente) {
        UsuarioSistema u = em.find(UsuarioSistema.class, usuarioCorrente.getId());
        for (VigenciaTribUsuario vigencia : u.getVigenciaTribUsuarios()) {
            if (vigencia.getVigenciaInicial().before(new Date())
                && (vigencia.getVigenciaFinal() == null || vigencia.getVigenciaFinal().after(new Date()))) {
                for (TipoUsuarioTribUsuario tipo : vigencia.getTipoUsuarioTribUsuarios()) {
                    if (tipo.getSupervisor() && tipo.getTipoUsuarioTributario().equals(TipoUsuarioTributario.FISCAL_TRIBUTARIO)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<FiscalDesignado> fiscaisDaAcao(AcaoFiscal acao) {
        return em.createQuery("select f from FiscalDesignado f where f.acaoFiscal = :acao").setParameter("acao", acao).getResultList();
    }

    public List<AcaoFiscal> acoesDaProgramacao(ProgramacaoFiscal programacao) {
        return em.createQuery("select a from AcaoFiscal a where a.programacaoFiscal = :programacao").setParameter("programacao", programacao).getResultList();
    }


    public ValorDivida retornaValorDividaDoCalculo(Calculo calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo = :calculo");
        q.setParameter("calculo", calculo);
        List<ValorDivida> resultado = q.getResultList();
        for (ValorDivida vd : resultado) {
            ConsultaParcela cp = new ConsultaParcela();
            cp.addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.IGUAL, vd.getId());
            cp.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            cp.executaConsulta();
            if (!cp.getResultados().isEmpty()) {
                vd.getParcelaValorDividas().size();
                return vd;
            }
        }
        return null;
    }

    public boolean temDebitosDiferenteDeAbertoOuCancelado(AcaoFiscal acaoFiscal) {
        List<CalculoFiscalizacao> fiscalizacoes = recuperaCalculoFiscalizacao(acaoFiscal);
        List<CalculoMultaFiscalizacao> multas = recuperaCalculoMultaFiscalizacao(acaoFiscal);
        for (CalculoFiscalizacao fiscalizacao : fiscalizacoes) {
            if (temDebitosDiferenteDeAbertoOuCancelado(fiscalizacao)) {
                return true;
            }
        }
        for (CalculoMultaFiscalizacao multa : multas) {
            if (temDebitosDiferenteDeAbertoOuCancelado(multa)) {
                return true;
            }
        }
        return false;


    }

    public boolean temDebitosDiferenteDeAbertoOuCancelado(Calculo calculo) {
        if (calculo != null) {
            List<ResultadoParcela> consultaFiscalizacao = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.DIFERENTE, SituacaoParcela.EM_ABERTO)
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.DIFERENTE, SituacaoParcela.CANCELAMENTO)
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.DIFERENTE, SituacaoParcela.ESTORNADO)
                .executaConsulta().getResultados();
            return !consultaFiscalizacao.isEmpty();
        }
        return false;
    }

    public boolean temDebitosPagos(AcaoFiscal acaoFiscal) {
        List<CalculoFiscalizacao> fiscalizacoes = recuperaCalculoFiscalizacao(acaoFiscal);
        List<CalculoMultaFiscalizacao> multas = recuperaCalculoMultaFiscalizacao(acaoFiscal);
        for (CalculoFiscalizacao fiscalizacao : fiscalizacoes) {
            if (temDebitosPagos(fiscalizacao)) {
                return true;
            }
        }
        for (CalculoMultaFiscalizacao multa : multas) {
            if (temDebitosPagos(multa)) {
                return true;
            }
        }
        return false;
    }

    public boolean temDebitosPagos(Calculo calculo) {
        if (calculo != null) {
            List<ResultadoParcela> consultaFiscalizacao = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, SituacaoParcela.getSituacoesPagas())
                .executaConsulta().getResultados();
            return !consultaFiscalizacao.isEmpty();
        }
        return false;
    }


    public List<CalculoFiscalizacao> recuperaCalculoFiscalizacao(AcaoFiscal acao) {
        Query q = em.createQuery("select calc from CalculoFiscalizacao  calc where calc.autoInfracaoFiscal.registro.acaoFiscal = :acao");
        q.setParameter("acao", acao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<CalculoMultaFiscalizacao> recuperaCalculoMultaFiscalizacao(AcaoFiscal acao) {
        Query q = em.createQuery("select calc from CalculoMultaFiscalizacao  calc where calc.autoInfracaoFiscal.registro.acaoFiscal = :acao");
        q.setParameter("acao", acao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public void estornarDebitosFiscalizacao(AcaoFiscal acaoFiscal) {
        List<CalculoFiscalizacao> fiscalizacoes = recuperaCalculoFiscalizacao(acaoFiscal);
        List<CalculoMultaFiscalizacao> multas = recuperaCalculoMultaFiscalizacao(acaoFiscal);

        for (CalculoFiscalizacao calculoFiscalizacao : fiscalizacoes) {
            List<ResultadoParcela> consulta = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoFiscalizacao.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                .executaConsulta().getResultados();
            estornaDebitosFiscalizacao(consulta);
        }

        for (CalculoMultaFiscalizacao calculoMultaFiscalizacao : multas) {
            List<ResultadoParcela> consulta = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoMultaFiscalizacao.getId())
                .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
                .executaConsulta().getResultados();
            estornaDebitosFiscalizacao(consulta);
        }
    }

    private void estornaDebitosFiscalizacao(List<ResultadoParcela> parcelas) {
        for (ResultadoParcela parcela : parcelas) {
            ParcelaValorDivida pvd = consultaDebitoFacade.recuperaParcela(parcela.getIdParcela());
            salvaSituacaoParcela(pvd, SituacaoParcela.ESTORNADO);
        }
    }

    private void salvaSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        SituacaoParcelaValorDivida toAdd = new SituacaoParcelaValorDivida();
        toAdd.setSaldo(parcela.getUltimaSituacao().getSaldo());
        toAdd.setDataLancamento(new Date());
        toAdd.setParcela(parcela);
        toAdd.setSituacaoParcela(situacao);
        em.persist(toAdd);
    }

    public void imprimeListagemNostasFiscais(AcaoFiscal acaoFiscal) throws JRException, IOException {
        AbstractReport report = AbstractReport.getAbstractReport();
        report.setGeraNoDialog(true);

        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";

        String arquivoJasper = "ListagemNotasFiscaisAcaoFiscal.jasper";

        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", report.getCaminhoImagem());
        parameters.put("ACAOFISCAL_ID", acaoFiscal.getId());
        parameters.put("USUARIO", sistemaFacade.getLogin());
        report.gerarRelatorio(arquivoJasper, parameters);
    }

    public AcaoFiscal salvaRetornando(AcaoFiscal acaoFiscal) {
        return em.merge(acaoFiscal);
    }

    public List<LancamentoDoctoFiscal> buscarNotasFiscaisDoLocamento(LancamentoContabilFiscal lancamentoContabil) {
        String sql = "select nf.* from lancamentodoctofiscal nf " +
            "where nf.acaofiscal_id = :idAcaoFiscal " +
            "  and extract(month from nf.dataemissao) = :mes " +
            "  and extract(year from nf.dataemissao) = :ano " +
            "  and coalesce(nf.naoTributada,0) = :naoTributada " +
            " order by nf.dataemissao";
        Query q = em.createNativeQuery(sql, LancamentoDoctoFiscal.class);
        q.setParameter("idAcaoFiscal", lancamentoContabil.getRegistroLancamentoContabil().getAcaoFiscal().getId());
        q.setParameter("mes", lancamentoContabil.getMes().getNumeroMes());
        q.setParameter("ano", lancamentoContabil.getAno());
        q.setParameter("naoTributada", !lancamentoContabil.getTributado());

        return q.getResultList();
    }

    public BigDecimal retornaValorTotalDeclaradoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty()
            || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getValorDeclarado());
                    }
                } else {
                    soma = soma.add(lcf.getValorDeclarado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalApuradoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getValorApurado());
                    }
                } else {
                    soma = soma.add(lcf.getValorApurado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalBaseCalculoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getBaseCalculo());
                    }
                } else {
                    soma = soma.add(lcf.getBaseCalculo());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssPagoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getIssPago());
                    }
                } else {
                    soma = soma.add(lcf.getIssPago());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssApuradoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getIssApurado());
                    }
                } else {
                    soma = soma.add(lcf.getIssApurado());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornaValorTotalIssDevidoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getIssDevido());
                    }
                } else {
                    soma = soma.add(lcf.getIssDevido());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean podeCalcularAcrescimoLancamento(LancamentoContabilFiscal lcf) {
        return lcf.getMes() != null && lcf.getAno() != null &&
            (!lcf.getRegistroLancamentoContabil().isCancelado() && !lcf.getRegistroLancamentoContabil().isEstornado());
    }

    public BigDecimal calcularCorrecaoPorArbitramento(int ano, int mes, BigDecimal valor, AcaoFiscal acaoFiscal) {
        BigDecimal indice = recuperaIndiceLevantamentoFiscal(ano, mes, acaoFiscal);
        if (indice.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal divisao = (valor.divide(indice, 6, RoundingMode.HALF_UP));
            BigDecimal multiplica = BigDecimal.ZERO;
            if (acaoFiscal.getUfmArbitramento() != null) {
                multiplica = divisao.multiply(acaoFiscal.getUfmArbitramento());
            }
            BigDecimal correcao = multiplica.subtract(valor);
            if (correcao.compareTo(BigDecimal.ZERO) < 0) {
                correcao = BigDecimal.ZERO;
            }
            return correcao;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaIndiceLevantamentoFiscal(int ano, int mes, AcaoFiscal acaoFiscal) {
        for (LevantamentoContabil levantamentoContabil : acaoFiscal.getLevantamentosContabeis()) {
            if (levantamentoContabil.getMes().getNumeroMes() == mes && levantamentoContabil.getAno() == ano) {
                return levantamentoContabil.getValorIndice();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularCorrecaoLancamento(LancamentoContabilFiscal lcf, AcaoFiscal acaoFiscal) {
        if (podeCalcularAcrescimoLancamento(lcf)) {
            try {
                lcf.setCorrecao(calcularCorrecaoPorArbitramento(lcf.getAno(), lcf.getMes().getNumeroMes(), lcf.getIssDevido(), acaoFiscal));
            } catch (Exception e) {
                lcf.setCorrecao(BigDecimal.ZERO);
                logger.debug("Erro calculando Correção do Lançamento", e);
            }
        }
        return lcf.getCorrecao();
    }

    public BigDecimal retornarValorTotalCorrecaoMonetariaLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
            if (ano != null) {
                if (ano.equals(lcf.getAno())) {
                    if (lcf.getCorrecao().compareTo(BigDecimal.ZERO) <= 0) {
                        lcf.setCorrecao(calcularCorrecaoLancamento(lcf, acaoFiscal));
                    }
                    soma = soma.add(lcf.getCorrecao());
                }
            } else {
                soma = soma.add(lcf.getCorrecao());
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorCorrigido(LancamentoContabilFiscal lcf) {
        return lcf.getCorrecao().add(lcf.getIssDevido());
    }

    public BigDecimal retornarValorTotalCorrigidoLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;

        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getValorCorrigido());
                    }
                } else {
                    soma = soma.add(lcf.getValorCorrigido());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularJurosLancamento(LancamentoContabilFiscal lcf, AcaoFiscal acaoFiscal, Map<Integer, Integer> diaVencimentoPorAno) {
        if (podeCalcularAcrescimoLancamento(lcf)) {
            Divida divida = dividaFacade.recuperar(getConfiguracaoTributarioFacade().retornaUltimo().getDividaISSHomologado().getId());
            ConfiguracaoAcrescimos acrescimo = dividaFacade.configuracaoVigente(divida);
            Calendar c = getDataVencimentoAcrescimo(lcf, diaVencimentoPorAno);
            ConfiguracaoAcrescimosDTO dto = acrescimo.toDto();
            dto.setJurosFracionado(true);
            lcf.setJuros(CalculadorJuros.calculaJuros(dto, c.getTime(), acaoFiscal.getDataArbitramento(), lcf.getIssDevido().add(lcf.getCorrecao()), false));
        }
        if (lcf.getIssDevido().compareTo(BigDecimal.ZERO) <= 0 || lcf.getJuros().compareTo(BigDecimal.ZERO) < 0) {
            lcf.setJuros(BigDecimal.ZERO);
        }
        return lcf.getJuros();
    }

    public Calendar getDataVencimentoAcrescimo(LancamentoContabilFiscal lcf, Map<Integer, Integer> diaVencimentoPorAno) {
        if (!diaVencimentoPorAno.containsKey(lcf.getAno())) {
            carregaDiasPorAno(lcf, diaVencimentoPorAno);
        }
        Calendar c = Calendar.getInstance();
        int ano = lcf.getAno();
        int mes = lcf.getMes().getNumeroMes();
        if (lcf.getMes().equals(Mes.DEZEMBRO)) {
            mes = 0;
            ano++;
        }
        c.set(Calendar.DAY_OF_MONTH, diaVencimentoPorAno.get(lcf.getAno()));
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.YEAR, ano);
        return c;
    }

    public void carregaDiasPorAno(LancamentoContabilFiscal lcf, Map<Integer, Integer> diaVencimentoPorAno) {
        ParametroFiscalizacao param = getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(lcf.getAno());
        int dia = 1;
        if (param != null && param.getDiaVencimentoISS() != null) {
            dia = param.getDiaVencimentoISS();
        }
        diaVencimentoPorAno.put(lcf.getAno(), dia);
    }

    public BigDecimal retornarValorTotalJurosMoraLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getJuros());
                    }
                } else {
                    soma = soma.add(lcf.getJuros());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularMultaLancamento(LancamentoContabilFiscal lcf, AcaoFiscal acaoFiscal, Map<Integer, Integer> diaVencimentoPorAno) {
        if (podeCalcularAcrescimoLancamento(lcf)) {
            Divida divida = dividaFacade.recuperar(getConfiguracaoTributarioFacade().retornaUltimo().getDividaISSHomologado().getId());
            ConfiguracaoAcrescimos acrescimo = dividaFacade.configuracaoVigente(divida);
            Calendar c = getDataVencimentoAcrescimo(lcf, diaVencimentoPorAno);
            lcf.setMulta(CalculadorMulta.calculaMulta(acrescimo.toDto(), c.getTime(), acaoFiscal.getDataArbitramento(), lcf.getIssDevido().add(lcf.getCorrecao()), true));
        }
        if (lcf.getIssDevido().compareTo(BigDecimal.ZERO) <= 0 || lcf.getMulta().compareTo(BigDecimal.ZERO) < 0) {
            lcf.setMulta(BigDecimal.ZERO);
        }
        return lcf.getMulta();
    }

    public BigDecimal retornarValorTotalMultaMoraLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getMulta());
                    }
                } else {
                    soma = soma.add(lcf.getMulta());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalLancamento(LancamentoContabilFiscal lcf) {
        return lcf.getCorrecao().add(lcf.getIssDevido()).add(lcf.getJuros()).add(lcf.getMulta());
    }

    public BigDecimal retornarValorTotalTotalLancamento(Integer ano, RegistroLancamentoContabil reg, AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!acaoFiscal.getLancamentosContabeis().isEmpty() || acaoFiscal.getLancamentosContabeis() != null) {
            for (LancamentoContabilFiscal lcf : reg.getLancamentosContabeis()) {
                if (ano != null) {
                    if (ano.equals(lcf.getAno())) {
                        soma = soma.add(lcf.getCorrecao()).add(lcf.getIssDevido()).add(lcf.getJuros()).add(lcf.getMulta());
                    }
                } else {
                    soma = soma.add(lcf.getCorrecao()).add(lcf.getIssDevido()).add(lcf.getJuros()).add(lcf.getMulta());
                }
            }
        }
        return soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal retornarValorTotalMulta(AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        for (RegistroLancamentoContabil reg : acaoFiscal.getLancamentosContabeisAtivos()) {
            for (LancamentoMultaFiscal multa : reg.getMultas()) {
                soma = soma.add(multa.getValorMulta());
            }
        }
        return soma;
    }

    public BigDecimal retornarValorTotalMulta(RegistroLancamentoContabil registro) {
        BigDecimal soma = BigDecimal.ZERO;
        for (LancamentoMultaFiscal multa : registro.getMultas()) {
            soma = soma.add(multa.getValorMulta());
        }
        return soma;
    }

    public BigDecimal retornarValorTotalMultaIndice(AcaoFiscal acaoFiscal) {
        BigDecimal soma = BigDecimal.ZERO;
        for (RegistroLancamentoContabil reg : acaoFiscal.getLancamentosContabeisAtivos()) {
            for (LancamentoMultaFiscal multa : reg.getMultas()) {
                soma = soma.add(multa.getValorMultaIndice());
            }
        }
        return soma;
    }

    public BigDecimal retornarValorTotalMultaIndice(RegistroLancamentoContabil registro) {
        BigDecimal soma = BigDecimal.ZERO;
        for (LancamentoMultaFiscal multa : registro.getMultas()) {
            soma = soma.add(multa.getValorMultaIndice());
        }
        return soma;
    }

    public String montarReferenciaMulta(LancamentoMultaFiscal lancamentoMultaFiscal) {
        if (lancamentoMultaFiscal.getLancamentoDoctoFiscal() != null) {
            return "NF " + lancamentoMultaFiscal.getLancamentoDoctoFiscal().getNumeroNotaFiscal() +
                (lancamentoMultaFiscal.getLancamentoDoctoFiscal().getAidf() != null ? " AIDF " + lancamentoMultaFiscal.getLancamentoDoctoFiscal().getAidf().getNumeroAidf() : "") +
                (lancamentoMultaFiscal.getLancamentoDoctoFiscal().getSerie() != null ? " Série " + lancamentoMultaFiscal.getLancamentoDoctoFiscal().getSerie().getSerie() : "");
        } else if (lancamentoMultaFiscal.getMes() != null) {
            return lancamentoMultaFiscal.getMes().getDescricao() + "/" + lancamentoMultaFiscal.getAno();
        } else {
            return "" + lancamentoMultaFiscal.getAno();
        }
    }

    public List<TotalizadorLancamentoContabil> buscarLancamentosNfse(AcaoFiscal acaoFiscal) {
        String sql = " select nfse.anoDeReferencia, nfse.mesDeReferencia, sum(nfse.valorTotalDoDebito) " +
            " from calculonfse nfse " +
            " inner join calculo cal on cal.id = nfse.id " +
            " inner join valordivida vd on vd.calculo_id = cal.id " +
            " where cal.cadastro_id = :cadastro_id " +
            " and to_date('15/'||nfse.mesdereferencia||'/'||nfse.anodereferencia,'dd/MM/yyyy') " +
            "             between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
            " and vd.divida_id in :dividas " +
            " and not exists (select pvd.id from parcelavalordivida pvd " +
            "               inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "               where spvd.situacaoparcela in (:situacoesCancelamento) " +
            "               and pvd.valordivida_id = vd.id) " +
            " group by nfse.anoDeReferencia, nfse.mesDeReferencia ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cadastro_id", acaoFiscal.getCadastroEconomico().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(acaoFiscal.getDataLevantamentoInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(acaoFiscal.getDataLevantamentoFinal()));
        q.setParameter("dividas", dividaFacade.buscarIdsDividasComParametroFiscalizacao(sistemaFacade.getExercicioCorrente()));
        q.setParameter("situacoesCancelamento", montarParametroInSituacoesParcela());

        List<Object[]> lista = q.getResultList();
        List<TotalizadorLancamentoContabil> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            TotalizadorLancamentoContabil total = new TotalizadorLancamentoContabil();
            total.setAno(((BigDecimal) obj[0]).intValue());
            total.setMes(Mes.getMesToInt(((BigDecimal) obj[1]).intValue()));
            total.setValor((BigDecimal) obj[2]);
            total.setAliquota(BigDecimal.ZERO);
            total.setTributado(true);
            retorno.add(total);
        }
        return retorno;
    }

    public List<String> montarParametroInSituacoesParcela() {
        List<String> situacoes = Lists.newArrayList();
        for (SituacaoParcela situacaoParcela : SituacaoParcela.getSituacaoCancelamento()) {
            situacoes.add(situacaoParcela.name());
        }
        return situacoes;
    }

    public void salvarComunicaoAcaoFiscal(ComunicacaoAcaoFiscal comunicacao) {
        em.persist(comunicacao);
    }

    public List<ComunicacaoAcaoFiscal> buscarComunicacaoAcaoFiscalPorAcaoFiscal(AcaoFiscal acaoFiscal) {
        String sql = "select com.* from COMUNICACAOACAOFISCAL com where com.acaofiscal_id = :parametroAcao order by com.id desc";
        Query q = em.createNativeQuery(sql, ComunicacaoAcaoFiscal.class);
        q.setParameter("parametroAcao", acaoFiscal.getId());
        return q.getResultList();
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public AutoInfracaoFiscal buscarUltimoAutoInfracao(RegistroLancamentoContabil registro) {
        String sql = "select * from AutoInfracaoFiscal where registro_id = :idRegistro order by ano desc, numero desc, id desc";
        Query q = em.createNativeQuery(sql, AutoInfracaoFiscal.class);
        q.setParameter("idRegistro", registro.getId());
        List<AutoInfracaoFiscal> autos = q.getResultList();
        if (!autos.isEmpty()) {
            return autos.get(0);
        }
        return null;
    }

}


