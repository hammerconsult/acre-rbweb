package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.*;
import br.com.webpublico.dte.enums.SituacaoLoteNotificacaoDte;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.apache.velocity.VelocityContext;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class LoteNotificacaoDteFacade extends AbstractFacade<LoteNotificacaoDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoContribuinteDteFacade grupoContribuinteDteFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private NotificacaoContribuinteDteFacade notificacaoContribuinteDteFacade;


    public LoteNotificacaoDteFacade() {
        super(LoteNotificacaoDte.class);
    }

    @Override
    public LoteNotificacaoDte recuperar(Object id) {
        LoteNotificacaoDte lote = super.recuperar(id);
        Hibernate.initialize(lote.getDocumentos());
        Hibernate.initialize(lote.getGrupos());
        Hibernate.initialize(lote.getCadastros());
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
            for (ArquivoComposicao arquivoComposicao : lote.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return lote;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future enviarNotificacoes(LoteNotificacaoDte lote, AssistenteBarraProgresso assistenteBarraProgresso) {
        lote = recuperar(lote.getId());
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Buscando contribuintes a serem notificados");
        List<CadastroEconomico> cadastroEconomicos = buscarContribuintesParaNotificar(lote);
        assistenteBarraProgresso.setDescricaoProcesso("Enviando notificações para os contribuintes");
        assistenteBarraProgresso.setTotal(cadastroEconomicos.size());
        for (CadastroEconomico cadastroEconomico : cadastroEconomicos) {
            notificarContribuinte(lote, cadastroEconomico);
            assistenteBarraProgresso.conta();
        }
        lote.setSituacao(SituacaoLoteNotificacaoDte.FINALIZADO);
        salvar(lote);
        return new AsyncResult(null);
    }

    public void notificarContribuinte(LoteNotificacaoDte lote, CadastroEconomico cadastroEconomico) {
        NotificacaoContribuinteDte notificacaoContribuinteDte = new NotificacaoContribuinteDte();
        notificacaoContribuinteDte.setRegistradaEm(new Date());
        notificacaoContribuinteDte.setCadastroEconomico(cadastroEconomico);
        notificacaoContribuinteDte.setTitulo(lote.getTitulo());
        notificacaoContribuinteDte.setCienciaAutomaticaEm(DataUtil.adicionaDias(new Date(), lote.getCienciaAutomaticaEm()));
        try {
            notificacaoContribuinteDte.setConteudo(notificacaoContribuinteDteFacade.trocarTagsConteudo(lote.getConteudo(), cadastroEconomico));
        } catch (Exception e) {
            notificacaoContribuinteDte.setConteudo(lote.getConteudo());
        }
        notificacaoContribuinteDte.setDocumentos(new ArrayList());
        for (LoteNotificacaoDocDte documento : lote.getDocumentos()) {
            NotificacaoContribuinteDocDte notificacaoContribuinteDocDte = new NotificacaoContribuinteDocDte();
            notificacaoContribuinteDocDte.setNotificacaoContribuinte(notificacaoContribuinteDte);
            notificacaoContribuinteDocDte.setModeloDocumento(documento.getModeloDocumento());
            notificacaoContribuinteDocDte.setConteudo(documento.getConteudo());
            notificacaoContribuinteDte.getDocumentos().add(notificacaoContribuinteDocDte);
        }
        notificacaoContribuinteDte.setDetentorArquivoComposicao(lote.getDetentorArquivoComposicao());
        notificacaoContribuinteDte = notificacaoContribuinteDteFacade.salvarRetornando(notificacaoContribuinteDte);
        notificacaoContribuinteDteFacade.enviarEmailsNotificacao(notificacaoContribuinteDte);
    }

    private void addTag(VelocityContext contexto, String chave, Object valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        } else {
            contexto.put(chave, "");
        }
    }

    private List<CadastroEconomico> buscarContribuintesParaNotificar(LoteNotificacaoDte lote) {
        HashSet<CadastroEconomico> contribuintes = new HashSet<>();
        if (lote.getGrupos() != null && !lote.getGrupos().isEmpty()) {
            for (LoteNotificacaoGrupoDte grupo : lote.getGrupos()) {
                for (GrupoContribuinteCmcDte grupoContribuinte : grupo.getGrupo().getCadastros()) {
                    contribuintes.add(grupoContribuinte.getCadastroEconomico());
                }
            }
        }
        if (lote.getCadastros() != null && !lote.getCadastros().isEmpty()) {
            for (LoteNotificacaoCmcDte loteNotificacaoCmcDte : lote.getCadastros()) {
                contribuintes.add(loteNotificacaoCmcDte.getCadastroEconomico());
            }
        }
        return Lists.newArrayList(contribuintes);
    }
}
