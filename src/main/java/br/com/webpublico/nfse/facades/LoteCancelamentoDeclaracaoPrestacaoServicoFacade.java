package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.nfse.domain.CancelamentoDeclaracaoPrestacaoServico;
import br.com.webpublico.nfse.domain.DeclaracaoPrestacaoServico;
import br.com.webpublico.nfse.domain.LoteCancelamentoDeclaracaoPrestacaoServico;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.enums.TipoCancelamento;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class LoteCancelamentoDeclaracaoPrestacaoServicoFacade extends AbstractFacade<LoteCancelamentoDeclaracaoPrestacaoServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CancelamentoNfseFacade cancelamentoNfseFacade;

    public LoteCancelamentoDeclaracaoPrestacaoServicoFacade() {
        super(LoteCancelamentoDeclaracaoPrestacaoServico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public NotaFiscalSearchDTO buscarNotaFiscalOrServicoDeclarado(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento tipoDocumento,
                                                                  CadastroEconomico cadastroEconomico,
                                                                  Long numero) {
        return notaFiscalFacade.buscarNotaFiscalOrServicoDeclarado(tipoDocumento,
            cadastroEconomico,
            numero);
    }

    public byte[] gerarImpressaoNotaFiscalSistemaNfse(Long idNotaFiscal) {
        return notaFiscalFacade.gerarImpressaoNotaFiscalSistemaNfse(idNotaFiscal);
    }

    private CancelamentoDeclaracaoPrestacaoServico criarCancelamentoDeclaracaoPrestacaoServico(LoteCancelamentoDeclaracaoPrestacaoServico lote,
                                                             NotaFiscalSearchDTO notaFiscalSearch) {
        DeclaracaoPrestacaoServico declaracaoPrestacaoServico = em.find(DeclaracaoPrestacaoServico.class,
            notaFiscalSearch.getIdDeclaracaoPrestacaoServico());
        CancelamentoDeclaracaoPrestacaoServico cancelamento = new CancelamentoDeclaracaoPrestacaoServico();
        cancelamento.setDeclaracao(declaracaoPrestacaoServico);
        cancelamento.setLote(lote);
        cancelamento.setDataCancelamento(lote.getDataCancelamento());
        cancelamento.setTipoCancelamento(TipoCancelamento.MANUAL);
        cancelamento.setMotivoCancelamento(lote.getMotivoCancelamento());
        cancelamento.setFiscalResposavel(lote.getFiscalResponsavel());
        cancelamento.setTipoDocumento(lote.getTipoDocumento());
        cancelamento.setObservacoesCancelamento(lote.getObservacoesCancelamento());
        cancelamento.setSituacaoTomador(CancelamentoDeclaracaoPrestacaoServico.Situacao.DEFERIDO);
        cancelamento.setDataDeferimentoTomador(lote.getDataCancelamento());
        return cancelamento;
    }

    public List<NotaFiscalSearchDTO> buscarNotasFiscaisLote(LoteCancelamentoDeclaracaoPrestacaoServico lote) {
        StringBuilder sql = new StringBuilder();
        sql.append(NotaFiscalFacade.getSelectNotaFiscalSearch()).append(NotaFiscalFacade.getFromNotaFiscalSearch());
        sql.append(" inner join cancdeclaprestacaoservico canc on canc.declaracao_id = dec.id ");
        sql.append(" where canc.lote_id = :id_lote ");
        sql.append(" order by coalesce(nf.prestador_id, sd.cadastroeconomico_id), coalesce(nf.numero, sd.numero) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("id_lote", lote.getId());
        return NotaFiscalSearchDTO.toListNotaFiscalSearchDTO((List<Object[]>) q.getResultList());
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<LoteCancelamentoDeclaracaoPrestacaoServico> efetivar(AssistenteBarraProgresso assistente, LoteCancelamentoDeclaracaoPrestacaoServico lote) {
        assistente.setDescricaoProcesso("Cancelando notas fiscais. Por favor aguarde!");
        List<NotaFiscalSearchDTO> notasFiscais = lote.getNotasFiscais();
        lote = em.merge(lote);
        assistente.setTotal(notasFiscais.size());
        for (NotaFiscalSearchDTO notaFiscal : notasFiscais) {
            CancelamentoDeclaracaoPrestacaoServico cancelamento = criarCancelamentoDeclaracaoPrestacaoServico(lote, notaFiscal);
            cancelamento = cancelamentoNfseFacade.salvarRetornando(cancelamento);
            cancelamentoNfseFacade.deferir(cancelamento, lote.getDataCancelamento(), lote.getFiscalResponsavel());
            assistente.conta();
        }
        lote.setNotasFiscais(notasFiscais);
        return new AsyncResult(lote);
    }
}
