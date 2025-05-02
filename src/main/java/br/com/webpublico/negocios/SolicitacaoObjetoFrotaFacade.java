/*
 * Codigo gerado automaticamente em Fri Nov 25 20:23:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.SolicitacaoObjetoFrota;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SolicitacaoObjetoFrotaFacade extends AbstractFacade<SolicitacaoObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoObjetoFrotaFacade() {
        super(SolicitacaoObjetoFrota.class);
    }

    public List<SolicitacaoObjetoFrota> buscarSolicitacoesPorPessoaSemReserva(String parte, UnidadeOrganizacional unidadeAdm) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select s.* from solicitacaoobjetofrota s ")
            .append("  left join pessoafisica pf on s.pessoafisica_id = pf.id ")
            .append(" where not exists (select 1 from reservaobjetofrota reserva where reserva.solicitacaoobjetofrota_id = s.id)")
            .append("   and (s.id like :filtro ")
            .append("       or lower(pf.nome) like :filtro or pf.cpf like :filtro) ")
            .append("   and s.unidadeorganizacional_id = :idUnidade ");
        Query q = em.createNativeQuery(sql.toString(), SolicitacaoObjetoFrota.class);
        q.setParameter("idUnidade", unidadeAdm.getId());
        q.setParameter("filtro", "%" + parte.trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    @Override
    public SolicitacaoObjetoFrota recuperar(Object id) {
        SolicitacaoObjetoFrota solicitacaoObjetoFrota = super.recuperar(id);
        solicitacaoObjetoFrota.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
            solicitacaoObjetoFrota.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));

        solicitacaoObjetoFrota.setHierarquiaSolicitante(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(),
            solicitacaoObjetoFrota.getUnidadeSolicitante(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        return solicitacaoObjetoFrota;
    }

    @Override
    public void salvarNovo(SolicitacaoObjetoFrota entity) {
        entity = em.merge(entity);
        criarNotificarSolicitacaoReserva(entity);
    }

    private void criarNotificarSolicitacaoReserva(SolicitacaoObjetoFrota entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Existe uma nova solicitação de reserva cadastrada no sistema. Solicitante: " + entity.getPessoaFisica().getNome() + ".");
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_NOVA_SOLICITACAO_RESERVA.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_NOVA_SOLICITACAO_RESERVA);
        notificacao.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        notificacao.setLink("/frota/solicitacao/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
