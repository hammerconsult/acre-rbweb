package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoCadastralPessoa;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoAlteracaoCadastralPessoa;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.StringUtil;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AlteracaoCadastralPessoaFacade extends AbstractFacade<AlteracaoCadastralPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    public AlteracaoCadastralPessoaFacade() {
        super(AlteracaoCadastralPessoa.class);
    }

    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AlteracaoCadastralPessoa recuperar(Object id) {
        AlteracaoCadastralPessoa alteracaoCadastralPessoa = em.find(AlteracaoCadastralPessoa.class, id);
        if (alteracaoCadastralPessoa.getPessoa() != null) {
            if (alteracaoCadastralPessoa.getPessoa().getTelefones() != null && !alteracaoCadastralPessoa.getPessoa().getTelefones().isEmpty()) {
                Hibernate.initialize(alteracaoCadastralPessoa.getPessoa().getTelefones());
            }
        }
        if (alteracaoCadastralPessoa.getPessoa() != null) {
            if (alteracaoCadastralPessoa.getPessoa().getEnderecos() != null && !alteracaoCadastralPessoa.getPessoa().getEnderecos().isEmpty()) {
                Hibernate.initialize(alteracaoCadastralPessoa.getPessoa().getEnderecos());
            }
        }
        if (alteracaoCadastralPessoa.getDetentorArquivoComposicao() != null) {
            if (alteracaoCadastralPessoa.getDetentorArquivoComposicao().getArquivosComposicao() != null
                && !alteracaoCadastralPessoa.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
                Hibernate.initialize(alteracaoCadastralPessoa.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        if (alteracaoCadastralPessoa.getPessoa().getDetentorArquivoComposicao() != null) {
            if (alteracaoCadastralPessoa.getPessoa().getDetentorArquivoComposicao().getArquivosComposicao() != null
                && !alteracaoCadastralPessoa.getPessoa().getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
                Hibernate.initialize(alteracaoCadastralPessoa.getPessoa().getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }

        return alteracaoCadastralPessoa;
    }

    public void salvarPessoa(Pessoa pessoa) {
        em.merge(pessoa);
    }

    public void salvarSelecionado(AlteracaoCadastralPessoa selecionado) {
        em.merge(selecionado);
    }

    public String enviarEmailDeferimentoAlteracaoCadastral(AssistenteBarraProgresso assistente) {
        AlteracaoCadastralPessoa selecionado = (AlteracaoCadastralPessoa) assistente.getSelecionado();
        assistente.setTotal(1);
        String corpoEmail = montarEmailDeferimentoAlteracaoCadastral(selecionado);
        enviarEmail(selecionado.getPessoa().getEmail(), corpoEmail, "Deferimento de alteração cadastral");
        assistente.conta();
        assistente.setExecutando(false);
        return "Foi enviado um e-mail informando o deferimento de alteração cadastral para " + selecionado.getEmail();
    }

    private String montarEmailDeferimentoAlteracaoCadastral(AlteracaoCadastralPessoa selecionado) {
        return "Prezado " + selecionado.getNomeRazaoSocial() + ", " +
            "as alterações cadastrais com os dados informados foram realizadas com sucesso. " +
            "<br/><br/>" + montarCorpoEmail();
    }

    public String enviarEmailIndeferimentoAlteracaoCadastral(AssistenteBarraProgresso assistente) {
        AlteracaoCadastralPessoa selecionado = (AlteracaoCadastralPessoa) assistente.getSelecionado();
        assistente.setTotal(1);
        String corpoEmail = montarEmailIndeferimentoAlteracaoCadastral(selecionado);
        enviarEmail(selecionado.getPessoa().getEmail(), corpoEmail, "Indeferimento de alteração cadastral");
        assistente.conta();
        assistente.setExecutando(false);
        return "Foi enviado um e-mail informando o indeferimento de alteração cadastral para " + selecionado.getEmail();
    }

    private String montarEmailIndeferimentoAlteracaoCadastral(AlteracaoCadastralPessoa selecionado) {
        return "Prezado " + selecionado.getNomeRazaoSocial() + ", " +
            "não foi possível realizar as alterações cadastrais solicitadas com sucesso. " +
            "Motivo: " + selecionado.getMotivoConclusao() +
            "<br/><br/>" + montarCorpoEmail();
    }

    private String montarCorpoEmail() {
        ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();
        return "Em caso de qualquer dúvida, entrar em contato com a Secretaria Municipal de Finanças através das " +
            "Centrais de Atendimento ao Cidadão, cujas informações de contato e endereço estão disponíveis no " +
            "Portal do Cidadão no link " + config.getUrlPortalContribuinte() + ", " +
            "ou presencialmente no endereço " + config.getEnderecoEmail() + " - " + config.getBairroEmail() + " - " +
            config.getCidade().getNome() + ".<br/><br/>" +
            "Atenciosamente,<br/><br/>" +
            config.getSecretariaEmail() + "<br/>" +
            config.getEnderecoEmail() + "<br/>" +
            config.getBairroEmail() + " - " + config.getCidade().getNome() + "<br/>" +
            "CEP " + config.getCepEmail() + "<br/>" +
            "Tel: " + config.getTelefoneEmail() + "<br/>";
    }

    public void criarNotificacaoAlteracaoCadastralPessoaAguardandoAprovacao(AlteracaoCadastralPessoa selecionado) {
        String msg = "Alteração cadastral de Pessoa aguardando aprovação.";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_ALTERACAO_CADASTRAL_PESSOA_AGUARDANDO_APROVACAO.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_ALTERACAO_CADASTRAL_PESSOA_AGUARDANDO_APROVACAO);
        notificacao.setLink("/alterar-cadastro-pessoa/ver/" + selecionado.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    public boolean verificarAlteracaoEmAbertoParaPessoa(String cpfCnpj) {

        String sql = " select alteracao.* from alteracaocadastralpessoa alteracao " +
            " inner join pessoa p on alteracao.pessoa_id = p.id " +
            " left join pessoafisica pf on p.id = pf.id " +
            " left join pessoajuridica pj on p.id = pj.id " +
            " where coalesce (replace(replace(pf.cpf,'.',''),'-',''), replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')) = :cpfCnpj " +
            " and alteracao.situacao = :situacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpfCnpj", StringUtil.retornaApenasNumeros(cpfCnpj));
        q.setParameter("situacao", SituacaoAlteracaoCadastralPessoa.EM_ABERTO.name());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public AlteracaoCadastralPessoa recuperarAlteracaoPeloIdPessoa(Long id) {
        String sql = " select alteracao.* from alteracaocadastralpessoa alteracao " +
            " where alteracao.pessoa_id = :idPessoa " +
            " and alteracao.situacao = :situacao";
        Query q = em.createNativeQuery(sql, AlteracaoCadastralPessoa.class);
        q.setParameter("idPessoa", id);
        q.setParameter("situacao", SituacaoAlteracaoCadastralPessoa.EM_ABERTO.name());
        if (!q.getResultList().isEmpty()) {

            return (AlteracaoCadastralPessoa) q.getResultList().get(0);
        }
        return null;
    }
}
