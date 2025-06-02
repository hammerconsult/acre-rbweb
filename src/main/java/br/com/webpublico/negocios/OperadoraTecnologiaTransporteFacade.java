package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.DocumentoCondutorOtt;
import br.com.webpublico.entidades.DocumentoVeiculoOtt;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.comum.TemplateEmailFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.util.RandomUtil;
import br.com.webpublico.ott.RenovacaoOperadoraOttDTO;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class OperadoraTecnologiaTransporteFacade extends AbstractFacade<OperadoraTecnologiaTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private TemplateEmailFacade templateEmailFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;

    public OperadoraTecnologiaTransporteFacade() {
        super(OperadoraTecnologiaTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public void aprovarOTT(OperadoraTecnologiaTransporte operadora) {
        vincularPessoa(operadora);
        criarUsuarioWeb(operadora);
        gerarCertificadoOtt(operadora.getId(), new Date());
        operadora.setSituacao(SituacaoOTT.APROVADO);
        criarHistoricoSituacaoOTT(operadora, sistemaFacade.getUsuarioCorrente());
        em.merge(operadora);
    }

    private void vincularPessoa(OperadoraTecnologiaTransporte operadora) {
        if (operadora.getPessoaJuridica() == null) {
            operadora.setPessoaJuridica(pessoaJuridicaFacade.recuperaPessoaJuridicaPorCNPJ(operadora.getCnpj()));
        }
    }

    private void criarUsuarioWeb(OperadoraTecnologiaTransporte operadora) {
        UsuarioWeb usuarioWeb = null;
        if (operadora.getPessoaJuridica() != null) {
            operadora.setPessoaJuridica(pessoaJuridicaFacade.recuperar(operadora.getPessoaJuridica().getId()));
            usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(operadora.getPessoaJuridica().getId());
        }
        if (usuarioWeb != null) {
            enviarEmailUsuarioWebOTT(usuarioWeb, null);
            return;
        }
        PessoaJuridica pessoa = pessoaJuridicaFacade.recuperaPessoaJuridicaPorCNPJ(operadora.getCnpj());
        if (pessoa == null) {
            pessoa = criarPessoaJuridicaDaOTT(operadora);
            usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());
        }
        if (usuarioWeb != null) {
            enviarEmailUsuarioWebOTT(usuarioWeb, null);
            return;
        }
        String senha = RandomUtil.generatePassword();
        if (pessoa.getUsuariosWeb().isEmpty()) {
            usuarioWeb = new UsuarioWeb();
            usuarioWeb.setLogin(StringUtil.retornaApenasNumeros(pessoa.getCpf_Cnpj()));
            usuarioWeb.setPessoa(pessoa);
            usuarioWeb.setEmail(operadora.getEmailResponsavel());
            pessoa.getUsuariosWeb().add(usuarioWeb);
        } else {
            usuarioWeb = pessoa.getUsuariosWeb().get(0);
        }
        usuarioWeb.setPassword(usuarioWebFacade.encripitografarSenha(senha));
        pessoa = em.merge(pessoa);
        enviarEmailUsuarioWebOTT(usuarioWeb, senha);
        operadora.setPessoaJuridica(pessoa);
        em.merge(operadora);
    }

    private PessoaJuridica criarPessoaJuridicaDaOTT(OperadoraTecnologiaTransporte operadora) {
        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj(operadora.getCnpj());
        pj.setRazaoSocial(operadora.getNome());
        pj.setInscricaoEstadual(operadora.getInscricaoEstadual());
        pj.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);

        EnderecoCorreio endereco = new EnderecoCorreio();
        endereco.setCep(operadora.getCep());
        endereco.setLocalidade(operadora.getCidade());
        endereco.setUf(operadora.getUf());
        endereco.setLogradouro(operadora.getEnderecoComercial());
        endereco.setNumero(operadora.getNumeroEndereco());
        endereco.setBairro(operadora.getBairro());
        endereco.setComplemento(operadora.getComplemento());
        pj.getEnderecos().add(endereco);

        if (operadora.getTelefoneComercial() != null && !operadora.getTelefoneComercial().trim().isEmpty()) {
            Telefone fone = new Telefone();
            fone.setPessoa(pj);
            fone.setTipoFone(TipoTelefone.COMERCIAL);
            fone.setDataRegistro(new Date());
            fone.setPrincipal(true);
            fone.setTelefone(operadora.getTelefoneComercial());
            pj.getTelefones().add(fone);
        }

        if (operadora.getCelular() != null && !operadora.getCelular().trim().isEmpty()) {
            Telefone fone = new Telefone();
            fone.setPessoa(pj);
            fone.setTipoFone(TipoTelefone.CELULAR);
            fone.setDataRegistro(new Date());
            fone.setPrincipal(false);
            fone.setTelefone(operadora.getCelular());
            pj.getTelefones().add(fone);
        }

        return em.merge(pj);
    }

    public void criarNotificacaoAguardandoAprovacao(OperadoraTecnologiaTransporte operadoraTecnologiaTransporte) {
        String msg = "Novo cadastro de Operadora de Tecnologia de Transporte - OTT, aguardando aprovação. ";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVA_OPERADORA_TECNOLOGIA_TRANSPORTE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVA_OPERADORA_TECNOLOGIA_TRANSPORTE);
        notificacao.setLink("/operadora-tecnologia-transporte/ver/" + operadoraTecnologiaTransporte.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    @Override
    public void salvarNovo(OperadoraTecnologiaTransporte entity) {
        entity = em.merge(entity);
        criarNotificacaoAguardandoAprovacao(entity);
    }

    public List<OperadoraTecnologiaTransporte> listarOperadoras(String parte) {
        String sql = "select o.* from OPERADORATRANSPORTE o " +
            "where o.situacao = :situacao " +
            "and lower(o.nome) " +
            "like :parte";
        Query q = em.createNativeQuery(sql, OperadoraTecnologiaTransporte.class);
        q.setParameter("situacao", SituacaoOTT.APROVADO.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public boolean verificarCnpjJaExistente(Long idOperadora, String cnpj) {
        String sql = "select o.id from OperadoraTransporte o " +
            " where replace(replace(replace(o.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') ";
        if (idOperadora != null) {
            sql += " and o.id <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("cnpj", cnpj.trim());
        if (idOperadora != null) {
            q.setParameter("id", idOperadora);
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    public OperadoraTecnologiaTransporte recuperar(Object id) {
        OperadoraTecnologiaTransporte ott = super.recuperar(id);
        Hibernate.initialize(ott.getCertificados());
        Hibernate.initialize(ott.getDetentorArquivoComposicao());
        Hibernate.initialize(ott.getHistoricoSituacoesOtt());
        Hibernate.initialize(ott.getRenovacaoOperadoraOTTS());
        for (RenovacaoOperadoraOTT renovacaoOperadoraOTT : ott.getRenovacaoOperadoraOTTS()) {
            if (renovacaoOperadoraOTT != null) {
                Hibernate.initialize(renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos());
            }
        }
        if (ott.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(ott.getDetentorArquivoComposicao());
        }
        return ott;
    }

    public DocumentoCredenciamentoOtt recuperarDocumentoCredenciamento(Object id) {
        return em.find(DocumentoCredenciamentoOtt.class, id);
    }

    public DocumentoCondutorOtt recuperarDocumentoCondutor(Object id) {
        return em.find(DocumentoCondutorOtt.class, id);
    }

    public DocumentoVeiculoOtt recuperarDocumentoVeiculo(Object id) {
        return em.find(DocumentoVeiculoOtt.class, id);
    }

    public CertificadoAnualOTT gerarNovoCertificado(OperadoraTecnologiaTransporte ott, Exercicio exercicio, Date vencimento, UsuarioSistema usuarioSistema, ParametrosOtt parametrosOtt) {
        if (parametrosOtt != null && parametrosOtt.getCertificadoCredenciamento() != null) {
            CertificadoAnualOTT certificado = new CertificadoAnualOTT();
            certificado.setOperadoraTecTransporte(ott);
            certificado.setExercicio(exercicio);
            certificado.setVencimento(vencimento);
            certificado.setDataEmissao(new Date());
            try {
                certificado.setDocumentoOficial(documentoOficialFacade.gerarDocumentoOTT(certificado, parametrosOtt.getCertificadoCredenciamento(), certificado.getDocumentoOficial(), usuarioSistema));
            } catch (Exception e) {
                certificado.setDocumentoOficial(null);
            }
            return em.merge(certificado);
        }
        return null;
    }

    public OperadoraTecnologiaTransporte buscarOperadoraPorCNPJ(String cnpj) {
        String sql = "select o.* from OperadoraTransporte o " +
            " where replace(replace(replace(o.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') ";
        Query q = em.createNativeQuery(sql, OperadoraTecnologiaTransporte.class);
        q.setParameter("cnpj", cnpj.trim());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (OperadoraTecnologiaTransporte) resultList.get(0);
        }
        return null;
    }

    public OperadoraTecnologiaTransporte buscarOTTPorId(Long id) {
        String sql = " select o.* from OperadoraTransporte o " +
            " where o.id = :idOtt ";
        Query q = em.createNativeQuery(sql, OperadoraTecnologiaTransporte.class);
        q.setParameter("idOtt", id);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (OperadoraTecnologiaTransporte) resultList.get(0);
        }
        return null;
    }

    public String enviarEmailIndeferimentoOtt(OperadoraTecnologiaTransporte operadora) {
        String corpoEmail = montarEmailIndeferimentoOtt(operadora);
        enviarEmail(operadora.getEmailResponsavel(), corpoEmail, "OTT - " + operadora.getNome() +
            " - Indeferimento de cadastro do Condutor");
        String email = "Foi enviado um e-mail com o motivo do indeferimento para " + operadora.getEmailResponsavel();

        return email;
    }

    private String montarEmailIndeferimentoOtt(OperadoraTecnologiaTransporte operadora) {
        StringBuilder motivoIndeferimento = new StringBuilder();
        motivoIndeferimento.append("Prezado, Informamos que ao analisar a documentação apresentada, ").append("<br/>")
            .append("constatamos irregularidades com relação as seguintes situações:").append("<br/>")
            .append("Motivo do indeferimento: ").append(operadora.getMotivoIndeferimento()).append("<br/>")
            .append("Responsável pelo Indeferimento: ").append(operadora.getUsuarioIndeferimento()).append("<br/>")
            .append("Data do indeferimento: ").append(DataUtil.getDataFormatada(operadora.getDataIndeferimento(), "dd/MM/yyyy")).append("<br/><br/>")
            .append("OTT: ").append(operadora.getNome()).append("<br/>")
            .append("CNPJ: ").append(operadora.getCnpj());

        return motivoIndeferimento.toString();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void enviarEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    public void criarHistoricoSituacaoOTT(OperadoraTecnologiaTransporte ott, UsuarioSistema usuario) {
        HistoricoSituacaoOTT historicoSituacaoOTT = new HistoricoSituacaoOTT(ott, usuario);
        ott.getHistoricoSituacoesOtt().add(historicoSituacaoOTT);
    }

    public OperadoraTecnologiaTransporte criarHistoricoSituacaoOTTRenovacao(OperadoraTecnologiaTransporte ott, UsuarioSistema usuario) {
        HistoricoSituacaoOTT historico = new HistoricoSituacaoOTT();
        historico.setOperadoraTecTransporte(ott);
        historico.setSituacaoOTT(SituacaoOTT.APROVADO);
        historico.setDataOperacao(sistemaFacade.getDataOperacao());
        historico.setUsuarioAlteracao(usuario);
        ott.getHistoricoSituacoesOtt().add(historico);
        return em.merge(ott);
    }

    public OperadoraTecnologiaTransporte buscarOperadoraOttPorCNPJ(String cnpj) {
        Query q = em.createQuery("select opera from OperadoraTecnologiaTransporte opera " +
            "     where replace(replace(replace(opera.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') ");
        q.setParameter("cnpj", cnpj);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (OperadoraTecnologiaTransporte) resultList.get(0);
        }
        return null;
    }

    public void criarNotificacaoOperadoraPortal(OperadoraTecnologiaTransporte operadora) {
        String msg = "Alteração Cadastro de Operadora de Tecnologia de Transporte - OTT";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVA_OPERADORA_TECNOLOGIA_TRANSPORTE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVA_OPERADORA_TECNOLOGIA_TRANSPORTE);
        notificacao.setLink("/operadora-tecnologia-transporte/ver/" + operadora.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public OperadoraTecnologiaTransporte buscarOperadoraPorId(Long id) {
        OperadoraTecnologiaTransporte condutor = em.find(OperadoraTecnologiaTransporte.class, id);
        if (condutor != null) {
            Hibernate.initialize(condutor.getDetentorArquivoComposicao());
            Hibernate.initialize(condutor.getRenovacaoOperadoraOTTS());
            Hibernate.initialize(condutor.getCertificados());
        }
        String sql = " select opera.* from OPERADORATRANSPORTE opera " +
            " where opera.id = :idOperadora ";
        Query q = em.createNativeQuery(sql, OperadoraTecnologiaTransporte.class);
        q.setParameter("idOperadora", id);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (OperadoraTecnologiaTransporte) resultList.get(0);
        }
        return null;
    }

    public RenovacaoOperadoraOTT buscarRenovacaoOttPorOperadoraSituacao(Long idOperadora, String situacaoRenovacao) {

        String sql = "select renovacao.* " +
            " from RenovacaoOperadoraOTT renovacao " +
            " inner join operadoratransporte opera on opera.id = renovacao.operadora_id " +
            "  where opera.id = :idOperadora " +
            "    and renovacao.situacaoRenovacao = :situacaoRenovacao ";
        Query q = em.createNativeQuery(sql, RenovacaoOperadoraOTT.class);
        q.setParameter("situacaoRenovacao", situacaoRenovacao);
        q.setParameter("idOperadora", idOperadora);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (RenovacaoOperadoraOTT) resultList.get(0);
        }
        return null;
    }

    public RenovacaoOperadoraOTT recuperarRenovacao(Object id) {
        RenovacaoOperadoraOTT renovacao = em.find(RenovacaoOperadoraOTT.class, id);
        Hibernate.initialize(renovacao.getOperadoraRenovacaoDetentorArquivos());
        return renovacao;
    }

    public List<RenovacaoOperadoraOttDTO> buscarRenovacoesPorOperadoraOtt(Long id) {

        String hql = " select renovacao" +
            " from RenovacaoOperadoraOTT renovacao " +
            " where renovacao.operadora.id = :idOperadora";
        Query q = em.createQuery(hql);
        q.setParameter("idOperadora", id);
        List<RenovacaoOperadoraOTT> renovacao = q.getResultList();
        List<RenovacaoOperadoraOttDTO> retorno = Lists.newArrayList();
        for (RenovacaoOperadoraOTT arquivo : renovacao) {
            retorno.add(arquivo.toDTO());
        }
        return retorno;
    }

    public RenovacaoOperadoraOTT aprovarRenovacaoOperadoraOTT(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        renovacaoOperadoraOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.APROVADO);
        return em.merge(renovacaoOperadoraOTT);
    }

    public void rejeitarRenovacaoOperadoraOTT(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        renovacaoOperadoraOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.REJEITADO);
        em.merge(renovacaoOperadoraOTT);
    }

    public boolean verificarCertificadoValido(Long idOperadora) {
        boolean certificadoValido = false;
        OperadoraTecnologiaTransporte operadora = recuperar(idOperadora);
        for (CertificadoAnualOTT certificado : operadora.getCertificados()) {
            if (certificado.getVencimento() != null && certificado.getVencimento().after(sistemaFacade.getDataOperacao())) {
                certificadoValido = true;
                break;
            }
        }
        return certificadoValido;
    }

    public RenovacaoOperadoraOTT novaRenovacaoOperadora(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        renovacaoOperadoraOTT.setDataRenovacao(sistemaFacade.getDataOperacao());
        renovacaoOperadoraOTT.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        renovacaoOperadoraOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.CADASTRADO);
        criarHistoricoSituacaoOTTRenovacao(renovacaoOperadoraOTT.getOperadora(), sistemaFacade.getUsuarioCorrente());
        return em.merge(renovacaoOperadoraOTT);
    }

    public void atribuirDocumentosObrigatoriosCredenciamento(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return;
        List<DocumentoCredenciamentoOtt> documentosObrigatorios = parametrosOtt.getDocumentosCredenciamento()
            .stream().filter(doc -> doc.getAtivo() && doc.getRenovacao()).collect(Collectors.toList());
        for (DocumentoCredenciamentoOtt documentoCredenciamentoOtt : documentosObrigatorios) {
            OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo = new OperadoraRenovacaoDetentorArquivo();
            operadoraRenovacaoDetentorArquivo.setOperadora(renovacaoOperadoraOTT.getOperadora());
            operadoraRenovacaoDetentorArquivo.setRenovacaoOperadoraOTT(renovacaoOperadoraOTT);
            operadoraRenovacaoDetentorArquivo.popularDadosDocumento(documentoCredenciamentoOtt);
            renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos().add(operadoraRenovacaoDetentorArquivo);
        }
    }

    public void atribuirDocumentosObrigatoriosCredenciamento(OperadoraTecnologiaTransporte operadora) {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return;
        List<DocumentoCredenciamentoOtt> documentosObrigatorios = parametrosOtt.getDocumentosCredenciamento()
            .stream().filter(DocumentoCredenciamentoOtt::getAtivo).collect(Collectors.toList());
        for (DocumentoCredenciamentoOtt documentoCredenciamentoOtt : documentosObrigatorios) {
            OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo = new OperadoraTransporteDetentorArquivo();
            operadoraTransporteDetentorArquivo.setOperadoraTecTransporte(operadora);
            operadoraTransporteDetentorArquivo.popularDadosDocumentoCredenciamento(documentoCredenciamentoOtt);
            operadora.getDetentorArquivoComposicao().add(operadoraTransporteDetentorArquivo);
        }
    }

    public void confirmarAvaliacaoDocumentos(OperadoraTecnologiaTransporte operadora,
                                             List<OperadoraTransporteDetentorArquivo> documentosAvaliacao) {
        for (OperadoraTransporteDetentorArquivo documentoAvaliacao : documentosAvaliacao) {
            operadora.getDetentorArquivoComposicao().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<OperadoraTransporteDetentorArquivo> documentosRejeitados = documentosAvaliacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarOTT(operadora, documentosRejeitados);
        } else {
            aprovarOTT(operadora);
        }
    }

    private void enviarEmailUsuarioWebOTT(UsuarioWeb usuarioWeb, String novaSenha) {
        String corpoEmail = criarEmailUsuarioOTT(usuarioWeb.getPessoa(), novaSenha);
        enviarEmail(usuarioWeb.getEmail(), corpoEmail, "Acesso à Área do Contribuinte");
    }

    public String montarEmailOTT(String nome, String cpfCnpj, String corpo) {
        StringBuilder sb = new StringBuilder();
        String cnpj = Util.formatarCnpj(cpfCnpj);
        if (nome != null) {
            sb.append("Olá ").append("<b>").append(nome).append(" (").append(cnpj).append(")").append("</b>").append("<br/><br/>");
        }
        sb.append(corpo);
        sb.append(getParteFinalDoEmailOTT());
        return sb.toString();
    }

    public String getParteFinalDoEmailOTT() {
        String rodapeRbTrans = configuracaoTributarioFacade.recuperarRodapeEmailPortal(true);
        return "<br/><br/>" + rodapeRbTrans;
    }

    private String criarEmailUsuarioOTT(Pessoa pessoa, String novaSenha) {
        String corpo = "Recebemos sua solicitação para acesso no <b>Portal do Cidadão</b> da Prefeitura Municipal de Rio Branco, no serviço de <b>Cadastro de OTT.</b>" + "<br/>" +
            "Para a sua segurança, <b>Acesse à Área do Contribuinte</b> do sistema no endereço <a href=\"" + configuracaoTributarioFacade.recuperarUrlPortal() + "\">" + configuracaoTributarioFacade.recuperarUrlPortal() + "</a>,";
        if (novaSenha != null && !novaSenha.isEmpty()) {
            corpo +=
                " efetue seu login com os dados informados abaixo." + "<br/>" +
                    "<br/>" +
                    "Login: " + StringUtil.retornaApenasNumeros(pessoa.getCpf_Cnpj()) + "<br/>" +
                    "Senha temporária: " + novaSenha + "<br/>" +
                    "<br/>" +
                    "Altere sua senha de acesso por uma combinação de sua preferência.";
        } else {
            corpo += " efetue seu login com o cpf/cnpj " + StringUtil.retornaApenasNumeros(pessoa.getCpf_Cnpj()) + " e sua senha já cadastrada.";
        }
        corpo += "<br/>" + "<br/>" +
            "Caso você não tenha solicitado estas informações, por favor entre em contato com o suporte." + "<br/>";
        return montarEmailOTT(pessoa.getNome(), pessoa.getCpf_Cnpj(), corpo);
    }

    private void rejeitarOTT(OperadoraTecnologiaTransporte operadora, List<OperadoraTransporteDetentorArquivo> documentosRejeitados) {
        criarUsuarioWeb(operadora);
        operadora.setSituacao(SituacaoOTT.AGUARDANDO_DOCUMENTACAO);
        enviarEmailDocumentacaoRejeitada(operadora, documentosRejeitados);
        em.merge(operadora);
    }

    private void enviarEmailDocumentacaoRejeitada(OperadoraTecnologiaTransporte operadora,
                                                  List<OperadoraTransporteDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Cadastro de Operadora de Tecnologia de Transporte";
        StringBuilder conteudo = new StringBuilder();
        for (OperadoraTransporteDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo.append(documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento").append(": ");
            conteudo.append(documentoRejeitado.getObservacao());
            conteudo.append("\n");
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmail(documentosRejeitados);

        EmailService.getInstance().enviarEmail(operadora.getEmailResponsavel().trim(),
            titulo, conteudo.toString(), anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmail(List<OperadoraTransporteDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (OperadoraTransporteDetentorArquivo documento : documentos) {
            Arquivo arquivo = arquivoFacade.recuperaDependencias(documento.getDetentorArquivoComp().getArquivoComposicao().getArquivo().getId());
            byte[] byteArrayDosDados = arquivo.getByteArrayDosDados();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byteArrayDosDados.length);
            outputStream.write(byteArrayDosDados, 0, byteArrayDosDados.length);
            String mimeType = arquivoFacade.getMimeType(new ByteArrayInputStream(arquivo.getByteArrayDosDados()));
            AnexoEmailDTO anexoDto = new AnexoEmailDTO(outputStream, arquivo.getNome(), mimeType,
                arquivoFacade.getExtension(mimeType));
            anexos.add(anexoDto);
        }
        return anexos;
    }

    public void confirmarAvaliacaoDocumentosRenovacao(RenovacaoOperadoraOTT renovacao,
                                                      List<OperadoraRenovacaoDetentorArquivo> documentosAvaliacao) {
        for (OperadoraRenovacaoDetentorArquivo documentoAvaliacao : documentosAvaliacao) {
            renovacao.getOperadoraRenovacaoDetentorArquivos().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<OperadoraRenovacaoDetentorArquivo> documentosRejeitados = documentosAvaliacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarRenovacaoOTT(renovacao, documentosRejeitados);
        } else {
            aprovarRenovacaoOTT(renovacao);
        }
    }

    private void rejeitarRenovacaoOTT(RenovacaoOperadoraOTT renovacao, List<OperadoraRenovacaoDetentorArquivo> documentosRejeitados) {
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.AGUARDANDO_DOCUMENTACAO);
        enviarEmailDocumentacaoRejeitada(renovacao, documentosRejeitados);
        em.merge(renovacao);
    }

    private void enviarEmailDocumentacaoRejeitada(RenovacaoOperadoraOTT renovacao,
                                                  List<OperadoraRenovacaoDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Cadastro de Operadora de Tecnologia de Transporte";
        String conteudo = "";
        for (OperadoraRenovacaoDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo = conteudo + (documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento") + ": ";
            conteudo = conteudo + documentoRejeitado.getObservacao();
            conteudo = conteudo + "\n";
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmailRenovacao(documentosRejeitados);

        EmailService.getInstance().enviarEmail(renovacao.getOperadora().getEmailResponsavel(),
            titulo, conteudo, anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmailRenovacao(List<OperadoraRenovacaoDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (OperadoraRenovacaoDetentorArquivo documento : documentos) {
            Arquivo arquivo = arquivoFacade.recuperaDependencias(documento.getDetentorArquivoComp().getArquivoComposicao().getArquivo().getId());
            byte[] byteArrayDosDados = arquivo.getByteArrayDosDados();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byteArrayDosDados.length);
            outputStream.write(byteArrayDosDados, 0, byteArrayDosDados.length);
            String mimeType = arquivoFacade.getMimeType(new ByteArrayInputStream(arquivo.getByteArrayDosDados()));
            AnexoEmailDTO anexoDto = new AnexoEmailDTO(outputStream, arquivo.getNome(), mimeType,
                arquivoFacade.getExtension(mimeType));
            anexos.add(anexoDto);
        }
        return anexos;
    }

    public void aprovarRenovacaoOTT(RenovacaoOperadoraOTT renovacao) {
        gerarCertificadoOtt(renovacao.getOperadora().getId(), renovacao.getDataRenovacao());
        renovacao.setSituacaoRenovacao(SituacaoRenovacaoOTT.APROVADO);
        em.merge(renovacao);
    }

    public void gerarCertificadoOtt(long idOtt, Date dataInicioCertificado) {
        Date vencimento = DataUtil.acrescentarUmAno(dataInicioCertificado);
        if (vencimento.before(DataUtil.dataSemHorario(new Date()))) {
            vencimento = DataUtil.acrescentarUmAno(new Date());
        }
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt != null && parametrosOtt.getCertificadoCredenciamento() != null) {
            OperadoraTecnologiaTransporte operadora = recuperar(idOtt);
            CertificadoAnualOTT certificadoAnualOTT = gerarNovoCertificado(operadora, sistemaFacade.getExercicioCorrente(),
                vencimento, sistemaFacade.getUsuarioCorrente(), parametrosOtt);
            if (certificadoAnualOTT == null) {
                throw new ValidacaoException("Não foi encontrado o tipo de documento configurado para a certificado de renovação de autorização");
            }
        } else {
            throw new ValidacaoException("Não foi encontrado parâmetro configurado para certificado de autorização de renovação!");
        }
    }

    public RenovacaoOperadoraOTT recuperarRenovacaoOperadoraOTT(Long id) {
        RenovacaoOperadoraOTT renovacao = em.find(RenovacaoOperadoraOTT.class, id);
        Hibernate.initialize(renovacao.getOperadoraRenovacaoDetentorArquivos());
        return renovacao;
    }

    public void salvarRenovacao(RenovacaoOperadoraOTT renovacao) {
        em.merge(renovacao);
    }

    public Boolean cnpjPermiteCadastroOTT(String cnpj) {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        PessoaJuridica pessoaJuridica = pessoaJuridicaFacade.recuperaPessoaJuridicaPorCNPJ(cnpj);
        if (pessoaJuridica != null) {
            pessoaJuridica = pessoaJuridicaFacade.recuperar(pessoaJuridica.getId());
            return pessoaJuridica.getPessoaCNAE()
                .stream().anyMatch(pessoaCnae -> parametrosOtt.getCnaes()
                    .stream()
                    .anyMatch(paramCnae -> paramCnae.getCnae().getId().equals(pessoaCnae.getCnae().getId())));
        } else {
            EventoRedeSimDTO eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(cnpj,
                sistemaFacade.getUsuarioBancoDeDados(), false);
            if (eventoRedeSimDTO != null) {
                return eventoRedeSimDTO.getPessoaDTO().getCnaes()
                    .stream()
                    .anyMatch(pessoaCnaeDTO -> parametrosOtt.getCnaes()
                        .stream()
                        .anyMatch(paramCnae -> paramCnae.getCnae().getCodigoCnae()
                            .equals(pessoaCnaeDTO.getCnae().getCodigo())));
            }
        }
        return false;
    }
}
