package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteParecerIsencaoIPTU;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ParecerProcIsencaoIPTUFacade extends AbstractFacade<ParecerProcIsencaoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoIsencaoIPTUFacade solicitacaoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;

    public ParecerProcIsencaoIPTUFacade() {
        super(ParecerProcIsencaoIPTU.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParecerProcIsencaoIPTU recuperar(Object id) {
        ParecerProcIsencaoIPTU parecer = em.find(ParecerProcIsencaoIPTU.class, id);
        if (parecer.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(parecer.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return parecer;
    }

    public Date recuperarDataLogada() {
        return sistemaFacade.getDataOperacao();
    }

    public UsuarioSistema recuperarUsuarioLogado() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public List<ProcessoIsencaoIPTU> buscarSolicitacoesIsencao(String parte) {
        String sql = " select proc.* from processoisencaoiptu proc " +
            " inner join exercicio ex on proc.exercicioprocesso_id = ex.id " +
            " where (proc.numero like :parte or ex.ano like :parte) " +
            " and proc.situacao = :situacaoProcesso " +
            " and exists(select id from isencaocadastroimobiliario isencao where isencao.processoisencaoiptu_id = proc.id) ";

        Query q = em.createNativeQuery(sql, ProcessoIsencaoIPTU.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("situacaoProcesso", ProcessoIsencaoIPTU.Situacao.EFETIVADO.name());

        List<ProcessoIsencaoIPTU> processos = q.getResultList();
        return processos != null ? processos : Lists.<ProcessoIsencaoIPTU>newArrayList();
    }

    public ParecerProcIsencaoIPTU buscarParecerDaSolicitacao(Long idSolicitacao) {
        String sql = " select parecer.* from parecerprocisencaoiptu parecer " +
            " where parecer.solicitacaoisencao_id = :idSolicitacao ";

        Query q = em.createNativeQuery(sql, ParecerProcIsencaoIPTU.class);
        q.setParameter("idSolicitacao", idSolicitacao);

        List<ParecerProcIsencaoIPTU> pareceres = q.getResultList();
        return (pareceres != null && !pareceres.isEmpty()) ? pareceres.get(0) : null;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteParecerIsencaoIPTU> deferirOrIndeferirIsencao(AssistenteParecerIsencaoIPTU assistente) {
        try {
            assistente.setBarraProgresso(new BarraProgressoItens());
            assistente.getBarraProgresso().inicializa();
            assistente.getBarraProgresso().setProcessados(0);
            assistente.getBarraProgresso().setMensagens(assistente.getMensagem());

            ParecerProcIsencaoIPTU parecer = assistente.getParecerIsencaoIPTU();
            ProcessoIsencaoIPTU processo = solicitacaoFacade.recuperarMaisSimples(parecer.getSolicitacaoIsencao());
            assistente.getBarraProgresso().setTotal(processo.getIsencoes().size());

            for (IsencaoCadastroImobiliario isencao : processo.getIsencoes()) {
                isencao.setSituacao(assistente.getSituacaoIsencao());
                assistente.getBarraProgresso().setProcessados(assistente.getBarraProgresso().getProcessados() + 1);
            }
            processo.setSituacao(assistente.getSituacaoSolicitacao());
            assistente.getBarraProgresso().setMensagens("Finalizando...");
            assistente.getBarraProgresso().finaliza();

            parecer.setSolicitacaoIsencao(processo);
            assistente.setParecerIsencaoIPTU(parecer);
        } catch (Exception ex) {
            logger.error("Erro ao deferir ou indeferir Isencao: {}", ex);
        }

        return new AsyncResult<>(assistente);
    }

    public String montarWhere(ProcessoIsencaoIPTU processo, boolean enquadra) {
        return solicitacaoFacade.montarWhere(processo.getCategoriaIsencaoIPTU(), processo.getInscricaoInicial(), processo.getInscricaoFinal(), enquadra);
    }

    public Long buscarIdParecerPeloIdIsencao(Long idIsencao) {
        String sql = " select parecer.id from parecerprocisencaoiptu parecer " +
            " inner join processoisencaoiptu proc on parecer.solicitacaoisencao_id = proc.id " +
            " inner join isencaocadastroimobiliario isencao on proc.id = isencao.processoisencaoiptu_id " +
            " where isencao.id = :idIsencao";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idIsencao", idIsencao);

        List<BigDecimal> quantidade = q.getResultList();
        return (quantidade != null && !quantidade.isEmpty()) ? quantidade.get(0).longValue() : null;
    }

    public void notificarSolicitacoesDeIsencaoEmAberto() {
        Long quantidade = buscarQuantidadeDeSolicitacoesIsencaoIPTUEmAberto();
        if (quantidade > 0L) {
            String msg = quantidade.equals(1L) ? ("Existe " + Util.formataNumeroInteiro(quantidade) + " Solicitação") : ("Existem " + Util.formataNumeroInteiro(quantidade) + " Solicitações");
            for (UsuarioSistema usuarioSistema : buscarUsuariosNotificacao()) {
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao(msg + " de Isenção de IPTU aguardando parecer.");
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo("Notificação de Solicitação de Isenção de IPTU.");
                notificacao.setTipoNotificacao(TipoNotificacao.AVISO_NOTIFICACAO_SOLICITACOES_ISENCAO_IPTU);
                notificacao.setLink("/solicitacao-processo-de-isencao-de-iptu/listar/");
                notificacao.setUsuarioSistema(usuarioSistema);
                NotificacaoService.getService().notificar(notificacao);
            }
        }
    }

    public List<UsuarioSistema> buscarUsuariosNotificacao() {
        String sql = " select us.* from usuariosistema us " +
            " inner join vigenciatribusuario vigencia on us.id = vigencia.usuariosistema_id " +
            " inner join tipousuariotribusuario tipo on vigencia.id = tipo.vigenciatribusuario_id " +
            " where to_date(:dataAtual, 'dd/MM/yyyy') between trunc(vigencia.vigenciainicial) " +
            " and coalesce(to_date(:dataAtual, 'dd/MM/yyyy'), trunc(vigencia.vigenciafinal)) " +
            " and tipo.tipousuariotributario in :tipos ";

        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));
        q.setParameter("tipos", TipoUsuarioTributario.buscarTipoUsuarioIsencaoIPTU());

        List<UsuarioSistema> usuarios = q.getResultList();
        return usuarios != null ? usuarios : Lists.<UsuarioSistema>newArrayList();
    }

    public Long buscarQuantidadeDeSolicitacoesIsencaoIPTUEmAberto() {
        String sql = " select count(*) from isencaocadastroimobiliario isencao " +
            " where isencao.situacao = :situacao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", IsencaoCadastroImobiliario.Situacao.EM_ABERTO.name());

        List<BigDecimal> quantidade = q.getResultList();
        return (quantidade != null && !quantidade.isEmpty()) ? quantidade.get(0).longValue() : 0L;
    }

    public Boolean isRecalculado(Long idParecer) {
        String sql = " select calc.* from calculoiptu calc " +
            " inner join isencaocadastroimobiliario isencao on calc.isencaocadastroimobiliario_id = isencao.id" +
            " inner join processoisencaoiptu processo on isencao.processoisencaoiptu_id = processo.id " +
            " inner join parecerprocisencaoiptu parecer on processo.id = parecer.solicitacaoisencao_id " +
            " where parecer.id = :idParecer ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParecer", idParecer);
        return !q.getResultList().isEmpty();
    }

    public IsencaoCadastroImobiliario recuperarIsencao(Long solicitacao) {
        String sql = " select isencao.* from isencaocadastroimobiliario isencao " +
            " inner join processoisencaoiptu proc on isencao.processoisencaoiptu_id = proc.id " +
            " where proc.id = :idProcesso ";

        Query q = em.createNativeQuery(sql, IsencaoCadastroImobiliario.class);
        q.setParameter("idProcesso", solicitacao);

        List<IsencaoCadastroImobiliario> isencoes = q.getResultList();
        return (isencoes != null && !isencoes.isEmpty()) ? isencoes.get(0) : null;
    }

    public ProcessoIsencaoIPTU recupararProcessoIsencaoPeloIdIsencao(Long idIsencao) {
        String sql = " select * from processoisencaoiptu proc " +
            " inner join isencaocadastroimobiliario isencao on proc.id = isencao.processoisencaoiptu_id " +
            " where isencao.id = :idIsencao ";

        Query q = em.createNativeQuery(sql, ProcessoIsencaoIPTU.class);
        q.setParameter("idIsencao", idIsencao);

        List<ProcessoIsencaoIPTU> processos = q.getResultList();
        return (processos != null && !processos.isEmpty()) ? processos.get(0) : null;
    }

    public DocumentoOficial gerarDocumentoIsencaoIPTU(IsencaoCadastroImobiliario isencaoCadastroImobiliario, DocumentoOficial documento, Cadastro cadastro, TipoDoctoOficial tipo) throws UFMException, AtributosNulosException {
        return documentoOficialFacade.geraDocumentoIsencaoIPTU(isencaoCadastroImobiliario, documento, cadastro, tipo, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getIp());
    }

    public void imprimirDoctoOficial(IsencaoCadastroImobiliario isencao) throws UFMException, AtributosNulosException {
        DocumentoOficial docto = gerarDocumentoIsencaoIPTU(isencao, isencao.getDocumentoOficial(), isencao.getCadastroImobiliario(), isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getTipoDoctoOficial());
        isencao.setDocumentoOficial(docto);
        em.merge(isencao);
        em.merge(docto);
        documentoOficialFacade.emiteDocumentoOficial(docto);
    }
}
