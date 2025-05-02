package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.SolicitacaoRPS;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoRPSNfseDTO;
import br.com.webpublico.nfse.enums.SituacaoSolicitacaoRPS;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.seguranca.NotificacaoService;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoRPSFacade extends AbstractFacade<SolicitacaoRPS> {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoRPSFacade() {
        super(SolicitacaoRPS.class);
    }

    public Page<SolicitacaoRPSNfseDTO> buscarSolicitacaoRPSPorEmpresa(Pageable pageable, Long empresaId, String filtro) {
        String select = "select sol.id," +
            " sol.solicitadaem, " +
            " sol.analisadaem," +
            " sol.quantidadesolicitada, " +
            " sol.observacaoanalise," +
            " sol.observacaosolicitacao, " +
            " sol.userempresa_id," +
            " sol.prestador_id, " +
            " sol.userprefeitura_id, " +
            " sol.situacao ";
        String count = "select count(sol.id) ";
        String from = "  from SolicitacaoRPS sol" +
            " where sol.prestador_id = :empresaId";
        from += PesquisaGenericaNfseUtil.montarOrdem(pageable);
        Query q = em.createNativeQuery(select + from);
        Query qCount = em.createNativeQuery(count + from);
        q.setParameter("empresaId", empresaId);
        qCount.setParameter("empresaId", empresaId);
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        int resultCount = ((Number) qCount.getSingleResult()).intValue();
        List<SolicitacaoRPSNfseDTO> dtos = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            SolicitacaoRPSNfseDTO dto = new SolicitacaoRPSNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setSolicitadaEm((Date) obj[1]);
            dto.setAnalisadaEm((Date) obj[2]);
            dto.setQuantidadeSolicitada(((Number) obj[3]).intValue());
            dto.setObservacaoAnalise(((String) obj[4]));
            dto.setObservacaoSolicitacao(((String) obj[5]));
            /*dto.setUserEmpresa(((Number) obj[6]).intValue());*/
            /*dto.setPrestador(((Number) obj[7]).intValue());*/
            /*dto.setUserPrefeitura(((Number) obj[8]).intValue());*/
            dto.setSituacao(SituacaoSolicitacaoRPS.valueOf((String) obj[9]).toSituacaoDTO());
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, pageable, resultCount);
    }

    public Integer buscarTotalQuantidadeSolicitadaEmpresa(Long empresaId) {
        try {
            String select = "select sum(sol.quantidadesolicitada) from solicitacaorps sol " +
                " where sol.situacao = :situacao" +
                " and sol.prestador_id = :empresaId";
            Query q = em.createNativeQuery(select);
            q.setParameter("empresaId", empresaId);
            q.setParameter("situacao", SituacaoSolicitacaoRPS.DEFERIDA.name());
            q.setMaxResults(1);
            return ((Number) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public void createAndSave(SolicitacaoRPSNfseDTO dto) {
        CadastroEconomico empresa = cadastroEconomicoFacade.recuperar(dto.getPrestador().getId());
        SolicitacaoRPS solicitacaoRPS = new SolicitacaoRPS();
        solicitacaoRPS.setSituacao(SituacaoSolicitacaoRPS.valueOf(dto.getSituacao().name()));
        solicitacaoRPS.setSolicitadaEm(dto.getSolicitadaEm());
        solicitacaoRPS.setQuantidadeSolicitada(dto.getQuantidadeSolicitada());
        solicitacaoRPS.setObservacaoSolicitacao(dto.getObservacaoSolicitacao());
        solicitacaoRPS.setUserEmpresa(usuarioWebFacade.recuperarUsuarioPorLogin(dto.getUserEmpresa().getLogin()));
        solicitacaoRPS.setPrestador(empresa);
        salvarNovo(solicitacaoRPS);
        NotificacaoService.getService().notificar(
            new Notificacao("Crição de Solicitação de RPS realizada", "Acesse o link para realizar a análise",
                "/nfse/solicitacao-rps/ver/" + solicitacaoRPS.getId() + "/", Notificacao.Gravidade.ERRO, TipoNotificacao.COMUNICACAO_NFS_ELETRONICA));
    }

    public void atualizar(SolicitacaoRPSNfseDTO dto) {
        if (dto.getId() == null) {
            createAndSave(dto);
        } else {

        }
    }

    public SolicitacaoRPS recuperar(Long id, Long prestadorId) {
        SolicitacaoRPS recuperar = recuperar(id);
        if (recuperar.getPrestador().getId().equals(prestadorId)) {
            return recuperar;
        }
        return null;
    }
}
