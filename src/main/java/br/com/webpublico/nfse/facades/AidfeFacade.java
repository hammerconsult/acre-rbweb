package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.nfse.domain.Aidfe;
import br.com.webpublico.nfse.domain.SituacaoAidfe;
import br.com.webpublico.nfse.domain.dtos.AidfeNfseDTO;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.seguranca.NotificacaoService;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class AidfeFacade extends AbstractFacade<Aidfe> {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoService;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AidfeFacade() {
        super(Aidfe.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public Page<Aidfe> recuperarAidfePorEmpresaESituacao(Pageable pageable, Long empresaId, String situacao) {
        String hql = "from Aidfe a";

        if (!situacao.equals("TODAS")) {
            hql += " where a.cadastro.id =:empresaId and a.situacao ='" + situacao + "'";
        } else {
            hql += " where a.empresa.id =:empresaId";
        }
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = em.createQuery(hql, Aidfe.class);
        q.setParameter("empresaId", empresaId);
        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        return new PageImpl<Aidfe>(q.getResultList(), pageable, resultCount);
    }

    public Page<AidfeNfseDTO> buscarAidfePorEmpresa(Pageable pageable, Long empresaId, String filtro) {
        String select = "select a.id, a.solicitadaEm, a.analisadaEm, a.quantidadeSolicitada, a.quantidadeDeferida, a.situacao, a.numero ";
        String count = "select count(a.id) ";
        String from = "  from Aidfe a" +
            " where a.cadastro_id = :empresaId " +
            " and (lower(a.situacao) like :filtro " +
            " OR to_char(a.numero) like :filtro) ";
        from += PesquisaGenericaNfseUtil.montarOrdem(pageable);
        Query q = em.createNativeQuery(select + from);
        Query qCount = em.createNativeQuery(count + from);
        q.setParameter("empresaId", empresaId);
        qCount.setParameter("empresaId", empresaId);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        qCount.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        int resultCount = ((Number) qCount.getSingleResult()).intValue();
        List<AidfeNfseDTO> dtos = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            AidfeNfseDTO dto = new AidfeNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setSolicitadoEm((Date) obj[1]);
            dto.setDeferidoEm((Date) obj[2]);
            dto.setQuantidade(((Number) obj[3]).intValue());
            dto.setQuantidadeDeferida(((Number) obj[4]).intValue());
            dto.setSituacaoAIDFE(SituacaoAidfe.valueOf((String) obj[5]).toSituacaoDTO());
            dto.setNumero(((Number) obj[6]).intValue());
            dtos.add(dto);
        }
        return new PageImpl<>(dtos, pageable, resultCount);
    }

    public Page<Aidfe> recuperarAidfePorSituacao(Pageable pageable, String situacao) {
        return recuperarAidfePorSituacao(pageable, situacao);
    }

    public void createAndSave(AidfeNfseDTO dto) {
        CadastroEconomico empresa = cadastroEconomicoService.recuperar(dto.getPrestadorServicos().getId());
        long numero = contarAidfesDaEMpresa(dto);
        Aidfe aidfe = new Aidfe();
        aidfe.setNumeroInicial(dto.getNumeroInicial());
        aidfe.setNumeroFinal(dto.getNumeroFinal());
        aidfe.setObservacaoSolicitacao(dto.getObservacao());
        aidfe.setNumero(numero);
        aidfe.setQuantidadeSolicitada(dto.getQuantidade());
        aidfe.setCadastro(empresa);
        aidfe.setSolicitadaEm(new Date());
        aidfe.setSituacao(SituacaoAidfe.AGUARDANDO);
        aidfe.setUserEmpresa(recuperarUsuarioPorLogin(dto.getUsuarioEmpresa().getLogin()));
        salvar(aidfe);

        NotificacaoService.getService().notificar(
            new Notificacao("Crição de AIDF-e realizada", "Acesse o link para realizar a análise",
                "/nfse/ver/" + aidfe.getId() + "/", Notificacao.Gravidade.ERRO, TipoNotificacao.COMUNICACAO_NFS_ELETRONICA));
    }


    public void atualizar(AidfeNfseDTO dto) {
        if (dto.getId() == null) {
            createAndSave(dto);
        } else {
            CadastroEconomico empresa = cadastroEconomicoService.recuperar(dto.getPrestadorServicos().getId());
            Aidfe aidfe = recuperar(dto.getId());
            aidfe.setNumeroInicial(dto.getNumeroInicial());
            aidfe.setNumeroFinal(dto.getNumeroFinal());
            aidfe.setObservacaoSolicitacao(dto.getObservacao());
            aidfe.setNumero(contarAidfesDaEMpresa(dto));
            aidfe.setQuantidadeSolicitada(dto.getQuantidade());
            aidfe.setCadastro(empresa);
            aidfe.setSolicitadaEm(new Date());
            aidfe.setSituacao(SituacaoAidfe.AGUARDANDO);
            aidfe.setUserEmpresa(recuperarUsuarioPorLogin(dto.getUsuarioEmpresa().getLogin()));
            salvar(aidfe);
        }
    }

    public long contarAidfesDaEMpresa(AidfeNfseDTO dto) {
        Query q = em.createQuery("from Aidfe a where a.cadastro.id =:empresaId");
        q.setParameter("empresaId", dto.getPrestadorServicos().getId());
        return q.getResultList().size() + 1L;
    }


    public boolean hasSolicitacaoAidfeAguardando(Long empresaId) {
        Query q = em.createQuery("from Aidfe a where a.cadastro.id =:empresaId and a.situacao ='AGUARDANDO'");
        q.setParameter("empresaId", empresaId);
        try {
            return q.getResultList().size() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    private UsuarioWeb recuperarUsuarioPorLogin(String login) {
        Query q = em.createQuery("from NfseUser where login = :login");
        q.setParameter("login", login);

        try {
            return (UsuarioWeb) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Long obterProximoNumeroIncial(Long prestadorId) {
        String hql = "select max(a.numeroFinal) from Aidfe a where a.cadastro.id =:prestadorId and a.situacao in ('DEFERIDA','DEFERIDA_PARCIALMENTE')";
        Query q = em.createQuery(hql, Long.class);
        q.setParameter("prestadorId", prestadorId);
        Long numero = (Long) q.getSingleResult();

        if (numero == null) {
            numero = 0L;
        }
        return numero + 1L;
    }

    public Aidfe removerAidfe(Long id) {
        return removerAidfe(id);
    }

    public List<Aidfe> deferirIndeferir(List<Aidfe> selecionadas, SituacaoAidfe situacao) {
        UsuarioWeb user = recuperarUsuarioPorLogin(selecionadas.get(0).getUserEmpresa().getLogin());
        for (Aidfe aidfe : selecionadas) {
//            aidfe.setUserPrefeitura(user);
            aidfe.setSituacao(situacao);
            aidfe.setAnalisadaEm(new Date());
            salvar(aidfe);
        }
        return selecionadas;
    }

    public Aidfe deferirParcialmente(Aidfe aidfe) {
//        aidfe.setUserPrefeitura(recuperarUsuarioPorLogin(aidfe.getUserEmpresa().getLogin()));
        aidfe.setAnalisadaEm(new Date());
        aidfe.setSituacao(SituacaoAidfe.DEFERIDA_PARCIALMENTE);
        salvar(aidfe);
        return aidfe;
    }

    public Aidfe recuperar(Long id, Long prestadorId) {
        Aidfe recuperar = recuperar(id);
        if (recuperar.getCadastro().getId().equals(prestadorId)) {
            return recuperar;
        }
        return null;
    }

    public Aidfe buscarAidfeUltimoNumero(Long prestadorId) {
        Query q = em.createQuery("from Aidfe a where a.cadastro.id =:empresaId and a.situacao in (:situacoes) order by a.analisadaEm desc");
        q.setParameter("empresaId", prestadorId);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoAidfe.DEFERIDA, SituacaoAidfe.DEFERIDA_PARCIALMENTE));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (Aidfe) q.getResultList().get(0);
        }
        return null;
    }

}
