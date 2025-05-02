package br.com.webpublico.negocios.contabil.execucao;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidades.VeiculoDePublicacao;
import br.com.webpublico.entidades.contabil.SancaoAdministrativa;
import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SancaoAdministrativaFacade extends AbstractFacade<SancaoAdministrativa> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;

    public SancaoAdministrativaFacade() {
        super(SancaoAdministrativa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Pessoa> buscarPessoasAtivas(String parte) {
        return pessoaFacade.listaTodasPessoasAtivas(parte.trim());
    }

    public List<UnidadeGestora> buscarUnidadesGestoras(String parte) {
        return unidadeGestoraFacade.listaFiltrandoPorExercicio(sistemaFacade.getExercicioCorrente(), parte);
    }

    public List<Contrato> buscarContratos(String parte) {
        return contratoFacade.buscarContratos(parte);
    }

    public List<VeiculoDePublicacao> buscarVeiculosDePublicacao(String parte) {
        return veiculoDePublicacaoFacade.buscarVeiculosPorNome(parte);
    }
}
