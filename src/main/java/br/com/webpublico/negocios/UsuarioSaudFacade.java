package br.com.webpublico.negocios;

import br.com.webpublico.StringUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.TemplateEmail;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidades.comum.trocatag.TrocaTagRejeicaoDocumentacaoUsuarioSaud;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.util.EmailService;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UsuarioSaudFacade extends AbstractFacade<UsuarioSaud> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametroSaudFacade parametroSaudFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;

    public UsuarioSaudFacade() {
        super(UsuarioSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroSaudFacade getParametroSaudFacade() {
        return parametroSaudFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    @Override
    public UsuarioSaud recuperar(Object id) {
        UsuarioSaud usuarioSaud = super.recuperar(id);
        if (usuarioSaud != null) {
            Hibernate.initialize(usuarioSaud.getDocumentos());
            if (usuarioSaud.getFoto() != null) {
                Hibernate.initialize(usuarioSaud.getFoto().getPartes());
            }
            if (usuarioSaud.getEndereco() != null &&
                usuarioSaud.getEndereco().getCidade() != null) {
                usuarioSaud.getEndereco().setUf(usuarioSaud.getEndereco().getCidade().getUf());
            }
        }
        return usuarioSaud;
    }

    public void inicializarDocumentosObrigatorios(UsuarioSaud usuarioSaud) {
        usuarioSaud.setDocumentos(Lists.newArrayList());
        ParametroSaud parametroSaud = parametroSaudFacade.recuperarUltimo();
        if (parametroSaud != null) {
            for (ParametroSaudDocumento parametroSaudDocumento : parametroSaud.getDocumentos()) {
                if (parametroSaudDocumento.getAtivo()) {
                    usuarioSaud.getDocumentos().add(new UsuarioSaudDocumento(usuarioSaud, parametroSaudDocumento));
                }
            }
        }
    }

    public boolean hasUsuarioSaudRegistrado(UsuarioSaud usuarioSaud) {
        String sql = " select u.* from usuariosaud u " +
            " where regexp_replace(u.cpf, '[^0-9]', '') = regexp_replace(:cpf, '[^0-9]', '') ";
        if (usuarioSaud.getId() != null) {
            sql += " and u.id != :id ";
        }
        Query q = em.createNativeQuery(sql, UsuarioSaud.class);
        q.setParameter("cpf", usuarioSaud.getCpf());
        if (usuarioSaud.getId() != null) {
            q.setParameter("id", usuarioSaud.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<UsuarioSaud> buscarFiltrando(String parte) {
        String sql = " select u.* from usuariosaud u " +
            " where (trim(regexp_replace(u.cpf, '[^0-9]', '')) like regexp_replace(:parte, '[^0-9]', '') or trim(lower(u.nome)) like :parte) " +
            " and u.status in :status";
        Query q = em.createNativeQuery(sql, UsuarioSaud.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("status", Lists.newArrayList(UsuarioSaud.Status.ATIVO.name(), UsuarioSaud.Status.CADASTRADO.name()));
        return q.getResultList();
    }

    @Override
    public void preSave(UsuarioSaud usuarioSaud) {
        usuarioSaud.realizarValidacoes();
        if (hasUsuarioSaudRegistrado(usuarioSaud)) {
            throw new ValidacaoException("O CPF informado já está registrado.");
        }
    }

    private void vincularOuCriarUsuarioWeb(UsuarioSaud usuarioSaud) {
        UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebPorLogin(usuarioSaud.getCpf());
        if (usuarioWeb == null) {
            usuarioWeb = criarUsuarioWeb(usuarioSaud);
        }
        usuarioSaud.setUsuarioWeb(usuarioWeb);
    }

    private UsuarioWeb criarUsuarioWeb(UsuarioSaud usuarioSaud) {
        String cpf = StringUtils.removerMascaraCpfCnpj(usuarioSaud.getCpf());
        UsuarioWeb usuarioWeb = new UsuarioWeb();
        usuarioWeb.setLogin(cpf);
        PessoaFisica pessoaFisica = pessoaFisicaFacade.buscarPessoaPeloCPF(cpf);
        if (pessoaFisica == null) {
            pessoaFisica = criarPessoaFisica(usuarioSaud);
        }
        usuarioWeb.setPessoa(pessoaFisica);
        usuarioWeb.setEmail(usuarioSaud.getEmail());
        usuarioWeb.setPassword(usuarioWebFacade.encripitografarSenha(Util.gerarSenhaUsuarioPortalWeb()));
        usuarioWeb.setActivated(Boolean.TRUE);
        return usuarioWebFacade.salvarRetornando(usuarioWeb);
    }

    private PessoaFisica criarPessoaFisica(UsuarioSaud usuarioSaud) {
        PessoaFisica pessoaFisica = new PessoaFisica();
        pessoaFisica.setCpf(StringUtils.removerMascaraCpfCnpj(usuarioSaud.getCpf()));
        pessoaFisica.setNome(usuarioSaud.getNome());
        pessoaFisica.setDataNascimento(usuarioSaud.getDataNascimento());
        pessoaFisica.setEmail(usuarioSaud.getEmail());
        pessoaFisica.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        pessoaFisica.getTelefones().add(new Telefone(pessoaFisica, usuarioSaud.getTelefone(),
            Boolean.TRUE, TipoTelefone.CELULAR));
        pessoaFisica.getEnderecos().add(new EnderecoCorreio(null,
            usuarioSaud.getEndereco().getLogradouro(), null, usuarioSaud.getEndereco().getBairro(),
            usuarioSaud.getEndereco().getCidade().getNome(),
            usuarioSaud.getEndereco().getCidade().getUf().getUf(), usuarioSaud.getEndereco().getNumero(),
            TipoEndereco.RESIDENCIAL, Boolean.TRUE));
        return pessoaFisicaFacade.salvarRetornando(pessoaFisica);
    }

    public void ativarUsuario(UsuarioSaud usuarioSaud) {
        if (usuarioSaud.getUsuarioWeb() == null && !Strings.isNullOrEmpty(usuarioSaud.getEmail())) {
            vincularOuCriarUsuarioWeb(usuarioSaud);
        }
        usuarioSaud.setStatus(UsuarioSaud.Status.ATIVO);
        salvar(usuarioSaud);
    }

    public void inativarUsuario(UsuarioSaud usuarioSaud) {
        usuarioSaud.setStatus(UsuarioSaud.Status.INATIVO);
        salvar(usuarioSaud);
    }

    public void confirmarAvaliacaoDocumentos(UsuarioSaud usuarioSaud) {
        if (usuarioSaud.hasDocumentoRejeitado(usuarioSaud)) {
            usuarioSaud.setStatus(UsuarioSaud.Status.AGUARDANDO_DOCUMENTACAO);
            enviarEmailDocumentacaoRejeitada(usuarioSaud);
        } else {
            usuarioSaud.setStatus(UsuarioSaud.Status.DOCUMENTACAO_APROVADA);
        }
        em.merge(usuarioSaud);
    }

    private void enviarEmailDocumentacaoRejeitada(UsuarioSaud usuarioSaud) {
        TemplateEmail templateEmail = templateEmailFacade.findByTipoTemplateEmail(TipoTemplateEmail.REJEICAO_DOCUMENTACAO_USUARIO_SAUD);
        if (templateEmail == null) {
            throw new ValidacaoException("Template de e-mail não configurado. " + TipoTemplateEmail.REJEICAO_DOCUMENTACAO_USUARIO_SAUD);
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        ConfiguracaoEmail configuracaoEmail = configuracaoEmailFacade.recuperarUtilmo();
        EmailService.getInstance().enviarEmail(usuarioSaud.getEmail(),
            templateEmail.getAssunto(),
            new TrocaTagRejeicaoDocumentacaoUsuarioSaud(configuracaoTributario, usuarioSaud)
                .trocarTags(templateEmail.getConteudo()));
    }

    public UsuarioSaudDocumento recuperarDocumento(Long id) {
        return em.find(UsuarioSaudDocumento.class, id);
    }
}
