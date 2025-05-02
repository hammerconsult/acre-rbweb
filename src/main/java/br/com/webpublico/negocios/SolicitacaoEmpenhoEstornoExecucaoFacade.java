package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SolicitacaoEmpenhoEstornoExecucao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SolicitacaoEmpenhoEstornoExecucaoFacade extends AbstractFacade<SolicitacaoEmpenhoEstornoExecucao> {

    @EJB
    private SistemaFacade sistemaFacade;

    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;

    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;

    @EJB
    private EmpenhoFacade empenhoFacade;

    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;

    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SolicitacaoEmpenhoEstornoExecucaoFacade() {
        super(SolicitacaoEmpenhoEstornoExecucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(SolicitacaoEmpenhoEstornoExecucao entity) {
        if (entity.getNumero() == null) {
           entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoEmpenhoEstornoExecucao.class, "numero"));
        }
        super.salvarNovo(entity);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }


    public UsuarioSistemaFacade getUsuarioSistemaFacade() { return usuarioSistemaFacade; }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public SolicitacaoEmpenhoEstornoFacade getSolicitacaoEmpenhoEstornoFacade() {
        return solicitacaoEmpenhoEstornoFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }
}
