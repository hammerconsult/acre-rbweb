package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.nfse.enums.TipoParametroNfse;
import com.google.common.base.Strings;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class CancelamentoNfseFacade extends AbstractFacade<CancelamentoDeclaracaoPrestacaoServico> {


    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private ServicoDeclaradoFacade servicoDeclaradoFacade;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private MensagemContribuinteFacade mensagemContribuinteFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CancelamentoNfseFacade() {
        super(CancelamentoDeclaracaoPrestacaoServico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotaFiscalFacade getNotaFiscalFacade() {
        return notaFiscalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<CancelamentoDeclaracaoPrestacaoServico> buscarCancelamentosDaDeclaracao(DeclaracaoPrestacaoServico dps, CancelamentoDeclaracaoPrestacaoServico.Situacao situacao) {
        Query q = em.createQuery("select c from CancelamentoDeclaracaoPrestacaoServico c " +
            " where c.declaracao = :declaracao and c.situacaoFiscal = :situacao");
        q.setParameter("declaracao", dps);
        q.setParameter("situacao", situacao);
        return q.getResultList();
    }

    public void indeferir(CancelamentoDeclaracaoPrestacaoServico cancelamento) throws ValidacaoException {
        cancelamento.setDataDeferimentoFiscal(sistemaFacade.getDataOperacao());
        cancelamento.setFiscalResposavel(sistemaFacade.getUsuarioCorrente());
        cancelamento.setSituacaoFiscal(CancelamentoDeclaracaoPrestacaoServico.Situacao.NAO_DEFERIDO);
        cancelamento.setVisualizadoPeloContribuinte(false);
        salvar(cancelamento);
        DeclaracaoPrestacaoServico declaracaoPrestacaoServico = cancelamento.getDeclaracao();
    }

    public void deferir(CancelamentoDeclaracaoPrestacaoServico cancelamento,
                        Date dataDeferimento, UsuarioSistema usuarioDeferimento) throws ValidacaoException {
        if (!cancelamento.getSituacaoTomador().equals(CancelamentoDeclaracaoPrestacaoServico.Situacao.DEFERIDO)) {
            throw new ValidacaoException("Cancelamento não deferido pelo tomador.");
        }
        cancelamento.setDataDeferimentoFiscal(dataDeferimento);
        cancelamento.setVisualizadoPeloContribuinte(false);
        DeclaracaoPrestacaoServico declaracaoPrestacaoServico = cancelamento.getDeclaracao();
        cancelamento.setSituacaoFiscal(CancelamentoDeclaracaoPrestacaoServico.Situacao.DEFERIDO);
        cancelamento.setFiscalResposavel(usuarioDeferimento);
        salvar(cancelamento);
        declaracaoPrestacaoServico.setSituacao(SituacaoNota.CANCELADA);
        em.merge(declaracaoPrestacaoServico);
        if (declaracaoPrestacaoServico.getTipoDocumento().equals(TipoDocumentoNfse.NFSE)) {
            notaFiscalFacade.removerNaNota(notaFiscalFacade.buscarNotaPorDeclaracaoPrestacaoServico(declaracaoPrestacaoServico));
        }
    }


    public ConfiguracaoNfse getConfiguracaoNfse() {
        return configuracaoNfseFacade.recuperarConfiguracao();
    }


    public CancelamentoDeclaracaoPrestacaoServico salvarCancelamentoManual(CancelamentoDeclaracaoPrestacaoServico selecionado, NotaFiscal notaFiscal) {
        notaFiscal.getDeclaracaoPrestacaoServico().setSituacao(SituacaoNota.CANCELADA);
        notaFiscalFacade.salvar(notaFiscal);
        return em.merge(selecionado);
    }

    public CancelamentoDeclaracaoPrestacaoServico salvarCancelamentoManual(CancelamentoDeclaracaoPrestacaoServico selecionado, ServicoDeclarado servicoDeclarado) {
        servicoDeclarado.getDeclaracaoPrestacaoServico().setSituacao(SituacaoNota.CANCELADA);
        servicoDeclaradoFacade.salvarRetornando(servicoDeclarado);
        return em.merge(selecionado);
    }
}
