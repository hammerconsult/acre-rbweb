package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.EmendaPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.EmpenhoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusEmenda;
import br.com.webpublico.enums.TipoAprovacaoEmendaUsuario;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class EmendaFacade extends AbstractFacade<Emenda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private VereadorFacade vereadorFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private ModalidadeIntervencaoFacade modalidadeIntervencaoFacade;
    @EJB
    private TipoRealizacaoPretendidaFacade tipoRealizacaoPretendidaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SuplementacaoEmendaFacade suplementacaoEmendaFacade;
    @EJB
    private AnulacaoEmendaFacade anulacaoEmendaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConfiguracaoEmendaFacade configuracaoEmendaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    public EmendaFacade() {
        super(Emenda.class);
    }

    @Override
    public Emenda recuperar(Object id) {
        Emenda emenda = em.find(Emenda.class, id);
        Hibernate.initialize(emenda.getAnulacaoEmendas());
        Hibernate.initialize(emenda.getBeneficiarioEmendas());
        Hibernate.initialize(emenda.getSuplementacaoEmendas());
        Hibernate.initialize(emenda.getHistoricos());
        Hibernate.initialize(emenda.getHistoricosAlteracoes());
        for (HistoricoAlteracaoEmenda historicoAlteracao : emenda.getHistoricosAlteracoes()) {
            if (historicoAlteracao.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(historicoAlteracao.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes());
            }
        }
        for (BeneficiarioEmenda beneficiarioEmenda : emenda.getBeneficiarioEmendas()) {
            Hibernate.initialize(beneficiarioEmenda.getResponsavelBeneficiarioEmendas());
        }
        return emenda;
    }

    @Override
    public void salvar(Emenda entity) {
        super.salvar(entity);
        portalTransparenciaNovoFacade.salvarPortal(new EmendaPortal(entity));
    }

    @Override
    public void salvarNovo(Emenda entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroEmenda(entity.getExercicio()));
        }
        super.salvarNovo(entity);
        portalTransparenciaNovoFacade.salvarPortal(new EmendaPortal(entity));
    }

    public void movimentarSuplementacoesAnulacoes(Emenda entity) {
        salvar(entity);
        for (SuplementacaoEmenda suplementacao : entity.getSuplementacaoEmendas()) {
            suplementacaoEmendaFacade.movimentarProvisaoPPASuplementacao(suplementacao);
        }
        for (AnulacaoEmenda anulacao : entity.getAnulacaoEmendas()) {
            anulacaoEmendaFacade.movimentarProvisaoPPAAnulacao(anulacao);
            validarSaldoProvisaoPPAFonte(anulacao, entity);
        }
    }

    private void validarSaldoProvisaoPPAFonte(AnulacaoEmenda anul, Emenda entity) {
        BigDecimal saldo = provisaoPPAFacade.getProvisaoPPAFonteFacade().recuperarSaldoPorFonteUnidadeExercicioContaDespesa(UtilRH.getExercicio(), anul.getUnidadeOrganizacional(), anul.getConta(), anul.getSubAcaoPPA(), entity.getDataEmenda());
        if (anul.getValor().compareTo(saldo) > 0) {
            throw new ExcecaoNegocioGenerica("Saldo de <b> " + Util.formataValor(saldo) + "</b> na fonte orçamentária é insuficiente, para realizar o cancelamento compensatórios no valor de <b>" + Util.formataValor(anul.getValor()) + "</b>.");
        }
    }

    public List<Emenda> buscarEmenda(String parte) {
        String sql = " select e.* from emenda e " +
            "           inner join vereador v on v.id = e.vereador_id " +
            "           inner join pessoa p on p.id = v.pessoa_id " +
            "           left join pessoajuridica pj on pj.id = p.id " +
            "           left join pessoafisica pf on pf.id = p.id " +
            "               where (pj.cnpj like :parte " +
            "                       or lower(pj.razaosocial) like :parte" +
            "                       or pf.cpf like :parte " +
            "                       or lower(pf.nome) like :parte) ";
        Query q = em.createNativeQuery(sql, Emenda.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List<Emenda> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public boolean canUsuarioAprovarOrReprovar(TipoAprovacaoEmendaUsuario tipoAprovacaoEmendaUsuario) {
        String sql = " select 1 from APROVACAOEMENDA ape " +
            "inner join APROVACAOEMENDAUSUARIO apeusu on ape.id = apeusu.aprovacaoemenda_id " +
            "where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ape.iniciovigencia) and coalesce(trunc(ape.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and apeusu.usuarioSistema_id = :usuario " +
            " and apeusu.tipoAprovacaoEmendaUsuario = :tipo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("tipo", tipoAprovacaoEmendaUsuario.name());
        return !q.getResultList().isEmpty();
    }

    public boolean hasLimiteAtingidoParaVereador(Vereador vereador, ConfiguracaoEmenda configuracaoEmenda) {
        String sql = " select count(id) from emenda em " +
            " where em.vereador_id = :vereador " +
            " and em.statusCamara <> :rejeitado " +
            " and em.statusPrefeitura <> :rejeitado " +
            " and em.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("rejeitado", StatusEmenda.REJEITADO.name());
        q.setParameter("vereador", vereador.getId());
        q.setParameter("exercicio", sistemaFacade.getExercicioCorrente().getId());
        q.setMaxResults(1);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return configuracaoEmenda.getQuantidadeMaximaEmenda().compareTo(resultado.get(0).intValue()) == 0;
        }
        return false;
    }

    public List<Emenda> buscarEmendasAprovadasPorExercicioFonteDespesaORCEUnidade(String parte, Exercicio exercicio, FonteDespesaORC fonteDespesaORC, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select e.* " +
            " from emenda e " +
            "    inner join vereador v on v.id = e.vereador_id " +
            "    inner join pessoa p on p.id = v.pessoa_id " +
            "    inner join SuplementacaoEmenda se on e.id = se.EMENDA_ID " +
            "    left join pessoajuridica pj on pj.id = p.id " +
            "    left join pessoafisica pf on pf.id = p.id " +
            " where se.UNIDADEORGANIZACIONAL_ID = :idUnidade " +
            "  and se.CONTA_ID = :idContaDespesa " +
            "  and se.SUBACAOPPA_ID = :idSubProjetoAtividade " +
            "  and se.DESTINACAODERECURSOS_ID = :idContaDestinacaoRecurso " +
            "  and e.EXERCICIO_ID = :idExercicio " +
            "  and e.statusCamara = :aprovado " +
            "  and e.statusPrefeitura = :aprovado " +
            "  and (pj.cnpj like :parte or lower(pj.razaosocial) like :parte or pf.cpf like :parte or lower(pf.nome) like :parte) ";
        Query q = em.createNativeQuery(sql, Emenda.class);
        q.setParameter("idSubProjetoAtividade", fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getId());
        q.setParameter("idContaDestinacaoRecurso", fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().getId());
        q.setParameter("idContaDespesa", fonteDespesaORC.getContaDeDespesa().getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("aprovado", StatusEmenda.APROVADO.name());
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List<Emenda> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public List<Emenda> buscarEmendasAprovadasPorExercicioAndVereador(String filtro) {
        String sql = " select e.* " +
            " from emenda e " +
            "    inner join vereador v on v.id = e.vereador_id " +
            "    inner join pessoa p on p.id = v.pessoa_id " +
            "    left join pessoajuridica pj on pj.id = p.id " +
            "    left join pessoafisica pf on pf.id = p.id " +
            " where e.EXERCICIO_ID = :idExercicio " +
            "  and e.statusCamara = :aprovado " +
            "  and e.statusPrefeitura = :aprovado " +
            "  and (lower(pj.razaosocial) like :parte or lower(pf.nome) like :parte or e.numero like :parte) ";
        Query q = em.createNativeQuery(sql, Emenda.class);
        q.setParameter("idExercicio", sistemaFacade.getExercicioCorrente().getId());
        q.setParameter("parte", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("aprovado", StatusEmenda.APROVADO.name());
        List<Emenda> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public BigDecimal buscarValorTotalEmendaPorVereadorEExercicio(Emenda emenda) {
        String sql =  " select coalesce(sum(se.valor), 0) " +
            " from emenda e " +
            "    inner join SuplementacaoEmenda se on e.id = se.EMENDA_ID " +
            " where e.vereador_id = :idVereador " +
            "   and e.exercicio_id = :idExercicio " +
            (emenda.getId() != null ? " and e.id <> :idEmenda " : "");
        Query q = em.createNativeQuery(sql);
        q.setParameter("idVereador", emenda.getVereador().getId());
        q.setParameter("idExercicio", emenda.getExercicio().getId());
        if (emenda.getId() != null) {
            q.setParameter("idEmenda", emenda.getId());
        }
        return (BigDecimal) q.getSingleResult();
    }

    public VereadorFacade getVereadorFacade() {
        return vereadorFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public ModalidadeIntervencaoFacade getModalidadeIntervencaoFacade() {
        return modalidadeIntervencaoFacade;
    }

    public TipoRealizacaoPretendidaFacade getTipoRealizacaoPretendidaFacade() {
        return tipoRealizacaoPretendidaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ConfiguracaoEmenda getConfiguracaoEmenda() {
        return configuracaoEmendaFacade.buscarConfiguracaoPorExercicio(sistemaFacade.getExercicioCorrente());
    }

    public SuplementacaoEmendaFacade getSuplementacaoEmendaFacade() {
        return suplementacaoEmendaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Emenda> buscarTodasEmendasPorExercicio() {
        String sql = " select e.* " +
            " from emenda e " +
            " where e.EXERCICIO_ID = :idExercicio ";
        Query q = em.createNativeQuery(sql, Emenda.class);
        q.setParameter("idExercicio", sistemaFacade.getExercicioCorrente().getId());
        List<Emenda> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }
}
