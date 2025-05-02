/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.enums.TipoPublicacaoLicitacao;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.pncp.enums.OperacaoPncp;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.pncp.service.PncpService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author renato
 */
@Stateless
public class StatusLicitacaoFacade extends AbstractFacade<StatusLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private MapaComparativoTecnicaPrecoFacade mapaComparativoTecnicaPrecoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public StatusLicitacaoFacade() {
        super(StatusLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    @Override
    public void salvarNovo(StatusLicitacao entity) {
        revogarDotacao(entity);
        reservarDotacaoStatusDesertaParaAndamento(entity);
        registrarPublicacaoParaLicitacaoHomologada(entity);
        salvarLicitacaoPortal(entity.getLicitacao());
        em.persist(entity);
    }

    public void enviarEventoPncp(StatusLicitacao entity) {
        if (TipoSituacaoLicitacao.getSituacoesAtualizacaoPncp().contains(entity.getTipoSituacaoLicitacao())) {
            PncpService.getService().enviar(TipoEventoPncp.ITEM_LICITACAO, OperacaoPncp.ALTERAR, entity.getLicitacao().getId());
        }
    }

    private void salvarLicitacaoPortal(Licitacao licitacao) {
        licitacaoFacade.salvarPortal(licitacao);
    }

    @Override
    public void salvar(StatusLicitacao entity) {
        try {
            revogarDotacao(entity);
            registrarPublicacaoParaLicitacaoHomologada(entity);
            salvarLicitacaoPortal(entity.getLicitacao());
            super.salvar(entity);
        } catch (Exception e) {
            throw e;
        }
    }

    private void reservarDotacaoStatusDesertaParaAndamento(StatusLicitacao entity) {
        Licitacao licitacao = licitacaoFacade.recuperarSomenteStatus(entity.getLicitacao().getId());
        if (licitacao != null) {
            if ((entity.isTipoAndamento()
                && TipoSituacaoLicitacao.DESERTA.equals(licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao())
                && !entity.getLicitacao().isRegistroDePrecos())) {

                SolicitacaoMaterial solicitacaoMaterial = licitacaoFacade.recuperarSolicitacaoProcessoDeCompra(licitacao.getProcessoDeCompra());
                validarSolicitacaoMaterial(solicitacaoMaterial);

                DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial = solicitacaoMaterialFacade.buscarDotacaoSolicitacaoMaterial(solicitacaoMaterial);
                validarDotacaoSolicitacaoMaterial(dotacaoSolicitacaoMaterial);
                dotacaoSolicitacaoMaterialFacade.reservarDotacaoSolicitacaoMaterial(dotacaoSolicitacaoMaterial, TipoOperacaoORC.NORMAL);
            }
        }
    }

    private void revogarDotacao(StatusLicitacao entity) {
        if ((entity.isTipoDeserta()
            || entity.isTipoFracassada()
            || entity.isTipoAnulada()
            || entity.isTipoRevogada())
            && !entity.getLicitacao().isRegistroDePrecos()) {

            SolicitacaoMaterial solicitacaoMaterial = licitacaoFacade.recuperarSolicitacaoProcessoDeCompra(entity.getLicitacao().getProcessoDeCompra());
            validarSolicitacaoMaterial(solicitacaoMaterial);

            DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial = solicitacaoMaterialFacade.buscarDotacaoSolicitacaoMaterial(solicitacaoMaterial);
            validarDotacaoSolicitacaoMaterial(dotacaoSolicitacaoMaterial);
            dotacaoSolicitacaoMaterialFacade.reservarDotacaoSolicitacaoMaterial(dotacaoSolicitacaoMaterial, TipoOperacaoORC.ESTORNO);
        }
    }

    private void registrarPublicacaoParaLicitacaoHomologada(StatusLicitacao entity) {
        if (entity.isTipoHomologada()) {
            PublicacaoLicitacao publicacaoLicitacao = new PublicacaoLicitacao();
            publicacaoLicitacao.setLicitacao(entity.getLicitacao());
            publicacaoLicitacao.setDataPublicacao(entity.getDataPublicacao());
            publicacaoLicitacao.setTipoPublicacaoLicitacao(entity.isTipoAdjudicada() ? TipoPublicacaoLicitacao.ADJUDICACAO : TipoPublicacaoLicitacao.HOMOLOGACAO);
            publicacaoLicitacao.setVeiculoDePublicacao(entity.getVeiculoDePublicacao());
            publicacaoLicitacao.setNumeroEdicaoPublicacao(entity.getNumeroEdicao());
            publicacaoLicitacao.setNumeroPagina(entity.getPaginaPublicacao());
            publicacaoLicitacao.setObservacoes(entity.getMotivoStatusLicitacao());
            em.persist(publicacaoLicitacao);
        }
    }

    private void validarDotacaoSolicitacaoMaterial(DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial) {
        if (dotacaoSolicitacaoMaterial == null) {
            throw new ExcecaoNegocioGenerica("Dotação solicitação material não encontrada para gerar saldo orçamentário.");
        }
    }

    private void validarSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        if (solicitacaoMaterial == null) {
            throw new ExcecaoNegocioGenerica("Solicitação material não encontrada para recuperar a reserva de dotação.");
        }
    }

    public StatusLicitacao getNovoStatus(TipoSituacaoLicitacao tipo, Licitacao licitacao, String motivo) {
        StatusLicitacao status = new StatusLicitacao();
        status.setLicitacao(licitacao);
        status.setDataStatus(new Date());
        status.setNumero(licitacao.getStatusAtualLicitacao().getNumero() + 1);
        status.setResponsavel(licitacaoFacade.getSistemaFacade().getUsuarioCorrente());
        status.setTipoSituacaoLicitacao(tipo);
        status.setMotivoStatusLicitacao(motivo);
        return status;
    }

    public StatusLicitacao recuperarUltimoStatusLicitacao(Licitacao licitacao) {
        String sql = "select status.* " +
            "      from licitacao lic " +
            "      inner join statuslicitacao status on status.licitacao_id = lic.id " +
            "      where lic.id = :licitacao_id " +
            "       and status.datastatus = (select max(st.datastatus) from statuslicitacao st where st.licitacao_id = lic.id) ";
        Query q = em.createNativeQuery(sql, StatusLicitacao.class);
        q.setParameter("licitacao_id", licitacao.getId());
        if (!q.getResultList().isEmpty()) {
            return (StatusLicitacao) q.getResultList().get(0);
        }
        return null;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public MapaComparativoTecnicaPrecoFacade getMapaComparativoTecnicaPrecoFacade() {
        return mapaComparativoTecnicaPrecoFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public StatusFornecedorLicitacaoFacade getStatusFornecedorLicitacaoFacade() {
        return statusFornecedorLicitacaoFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
