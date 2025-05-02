package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemMapaComparativoTecnicaPreco;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.MapaComparativoTecnicaPreco;
import br.com.webpublico.entidades.PropostaFornecedor;
import br.com.webpublico.enums.SituacaoItemCertame;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/09/14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class MapaComparativoTecnicaPrecoFacade extends AbstractFacade<MapaComparativoTecnicaPreco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private PropostaTecnicaFacade propostaTecnicaFacade;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private LoteProcessoDeCompraFacade loteProcessoDeCompraFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;
    @EJB
    private StatusLicitacaoFacade statusLicitacaoFacade;

    public MapaComparativoTecnicaPrecoFacade() {
        super(MapaComparativoTecnicaPreco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public PropostaTecnicaFacade getPropostaTecnicaFacade() {
        return propostaTecnicaFacade;
    }

    public ItemPropostaFornecedorFacade getItemPropostaFornecedorFacade() {
        return itemPropostaFornecedorFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public LoteProcessoDeCompraFacade getLoteProcessoDeCompraFacade() {
        return loteProcessoDeCompraFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public StatusFornecedorLicitacaoFacade getStatusFornecedorLicitacaoFacade() {
        return statusFornecedorLicitacaoFacade;
    }

    public StatusLicitacaoFacade getStatusLicitacaoFacade() {
        return statusLicitacaoFacade;
    }

    @Override
    public MapaComparativoTecnicaPreco recuperar(Object id) {
        MapaComparativoTecnicaPreco mctp = super.recuperar(id);

        mctp.getItens().size();

        if (!mctp.getItens().isEmpty()) {
            for (ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco : mctp.getItens()) {
                carregarListasItemProcessoItemProposta(itemMapaComparativoTecnicaPreco);
                carregarListasLoteProcessoLoteProposta(itemMapaComparativoTecnicaPreco);
            }

        }
        Collections.sort(mctp.getItens());
        return mctp;
    }

    @Override
    public void salvarNovo(MapaComparativoTecnicaPreco entity) {
        atualizarItensOrLotesPropostaFornecedor(entity);
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(MapaComparativoTecnicaPreco entity) {
        atualizarItensOrLotesPropostaFornecedor(entity);
        super.salvar(entity);
    }

    public MapaComparativoTecnicaPreco salvarAlternativo(MapaComparativoTecnicaPreco entity) {
        atualizarItensOrLotesPropostaFornecedor(entity);
        entity = em.merge(entity);
        return entity;
    }

    private void atualizarItensOrLotesPropostaFornecedor(MapaComparativoTecnicaPreco entity) {
        for (ItemMapaComparativoTecnicaPreco itemMapa : entity.getItens()) {
            if (entity.getLicitacao().isTipoApuracaoPrecoItem()) {
                itemMapa.getItemMapaComparativoTecnicaPrecoItemProcesso().setItemPropostaVencedor(em.merge(itemMapa.getItemPropostaFornecedor()));
            }
            if (entity.getLicitacao().isTipoApuracaoPrecoLote()) {
                itemMapa.getItemMapaComparativoTecnicaPrecoLoteProcesso().setLotePropostaVencedor(em.merge(itemMapa.getLotePropostaFornecedor()));
            }
        }
    }

    public void salvar(MapaComparativoTecnicaPreco entity, List<PropostaFornecedor> propostas) {
        super.salvar(entity);

        if (propostas != null && !propostas.isEmpty()) {
            for (PropostaFornecedor proposta : propostas) {
                proposta = em.merge(proposta);
            }
        }
    }

    @Override
    public void remover(MapaComparativoTecnicaPreco entity) {
        List<PropostaFornecedor> propostas = entity.getPropostas();
        for (PropostaFornecedor proposta : propostas) {
            proposta = propostaFornecedorFacade.recuperar(proposta.getId());
            proposta.anularNotas();
            em.merge(proposta);
        }
        super.remover(entity);
    }

    private void carregarListasLoteProcessoLoteProposta(ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco) {
        if (itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoLoteProcesso() != null) {
            itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLoteProcessoDeCompra().getItensProcessoDeCompra().size();
            itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLotePropostaVencedor().getItens().size();
        }
    }

    private void carregarListasItemProcessoItemProposta(ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco) {
        if (itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoItemProcesso() != null) {
            itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemProcessoDeCompra().getItensPropostaFornecedor().size();
//            itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemPropostaVencedor().getItensContrato().size();
        }
    }

    public boolean licitacaoTemVinculoEmUmMapaComparativoTecnicaPreco(Licitacao licitacao) {
        String sql = " SELECT * FROM MAPACOMTECPREC WHERE LICITACAO_ID = :lic";

        Query q = em.createNativeQuery(sql, MapaComparativoTecnicaPreco.class);
        q.setParameter("lic", licitacao.getId());

        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public boolean temMapaComparativo(Licitacao licitacao) {
        String sql = " select proposta.id as proposta from propostatecnica proposta " +
            " inner join licitacao lic on lic.id = proposta.licitacao_id " +
            " inner join mapacomtecprec mapa on mapa.licitacao_id = lic.id " +
            " where lic.id = :licitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacao", licitacao.getId());
        return !q.getResultList().isEmpty();
    }

    public boolean isVerificarMapaComparativoPossuiItemClassificadoOrVencedor(Licitacao licitacao) {
        String sql = " select lic.id, item.situacaoitem " +
            " from licitacao lic " +
            " join mapacomtecprec mapa on lic.id    = mapa.licitacao_id " +
            " join itemmapacomtecpre item on item.mapacomtecnicapreco_id = mapa.id " +
            " where lic.id = :licitacao " +
            " and (item.situacaoitem = :classificado or item.situacaoitem = :vencedor) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacao", licitacao.getId());
        q.setParameter("classificado", SituacaoItemCertame.CLASSIFICADO.name());
        q.setParameter("vencedor", SituacaoItemCertame.VENCEDOR.name());
        return !q.getResultList().isEmpty();
    }
}
