package br.com.webpublico.negocios;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCondutorOTT;
import br.com.webpublico.enums.SituacaoRenovacaoOTT;
import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.ott.CondutorOttDTO;
import br.com.webpublico.ott.RenovacaoCondutorOttDTO;
import br.com.webpublico.ott.VeiculoOttDTO;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CondutorOperadoraTecnologiaTransporteFacade extends AbstractFacade<CondutorOperadoraTecnologiaTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private VeiculoOperadoraTecnologiaTransporteFacade veiculoOperadoraTecnologiaTransporteFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;

    public CondutorOperadoraTecnologiaTransporteFacade() {
        super(CondutorOperadoraTecnologiaTransporte.class);
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

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public OperadoraTecnologiaTransporteFacade getOperadoraTecnologiaTransporteFacade() {
        return operadoraTecnologiaTransporteFacade;
    }

    public VeiculoOperadoraTecnologiaTransporteFacade getVeiculoOperadoraTecnologiaTransporteFacade() {
        return veiculoOperadoraTecnologiaTransporteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void aprovarCondutorOTT(CondutorOperadoraTecnologiaTransporte selecionado, VeiculoOperadoraTecnologiaTransporte veiculo) {
        gerarNovoCertificado(selecionado, veiculo, selecionado.getDataCadastro());
        selecionado.setSituacaoCondutorOTT(SituacaoCondutorOTT.APROVADO);
        criarHistoricoSituacoesCondutor(selecionado);
        em.merge(selecionado);
    }

    public void criarNotificacaoAguardandoAprovacao(CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte) {
        String msg = "Novo cadastro de Condutor de Operadora de Tecnologia de Transporte - OTT, aguardando aprovação. ";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVO_CONDUTOR_OPERADORA_TRANSPORTE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVO_CONDUTOR_OPERADORA_TRANSPORTE);
        notificacao.setLink("/condutor-operadora-tecnologia-transporte/ver/" + condutorOperadoraTecnologiaTransporte.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    @Override
    public void salvarNovo(CondutorOperadoraTecnologiaTransporte entity) {
        entity = em.merge(entity);
        criarNotificacaoAguardandoAprovacao(entity);
    }

    public void gerarCertificadoVeiculo(CondutorOperadoraTecnologiaTransporte condutor) {
        for (CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculoOperadora : condutor.getCondutorOperadoraVeiculoOperadoras()) {
            veiculoOperadoraTecnologiaTransporteFacade.gerarCertificadoInscricaoVeiculo(condutorOperadoraVeiculoOperadora.getVeiculoOTTransporte());
        }
    }

    @Override
    public CondutorOperadoraTecnologiaTransporte recuperar(Object id) {
        CondutorOperadoraTecnologiaTransporte condutor = em.find(CondutorOperadoraTecnologiaTransporte.class, id);
        Hibernate.initialize(condutor.getCondutorOperadoraVeiculoOperadoras());
        Hibernate.initialize(condutor.getCertificados());
        Hibernate.initialize(condutor.getCondutorOperadoraDetentorArquivos());
        Hibernate.initialize(condutor.getHistoricoSituacoesCondutor());
        Hibernate.initialize(condutor.getRenovacaoCondutorOTTS());
        for (RenovacaoCondutorOTT renovacaoCondutorOTT : condutor.getRenovacaoCondutorOTTS()) {
            if (renovacaoCondutorOTT != null) {
                Hibernate.initialize(renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos());
            }
        }
        for (CondutorOperadoraVeiculoOperadora condutorVeiculo : condutor.getCondutorOperadoraVeiculoOperadoras()) {
            if (condutorVeiculo != null) {
                Hibernate.initialize(condutorVeiculo.getVeiculoOTTransporte().getRenovacaoVeiculoOTTS());
            }
        }
        if (condutor.getFoto() != null) {
            Hibernate.initialize(condutor.getFoto().getPartes());
        }
        return condutor;
    }

    public RenovacaoCondutorOTT recuperarRenovacao(Object id) {
        RenovacaoCondutorOTT renovacao = em.find(RenovacaoCondutorOTT.class, id);
        Hibernate.initialize(renovacao.getCondutorRenovacaoDetentorArquivos());
        return renovacao;
    }

    public Boolean verificarCpfJaExiste(CondutorOperadoraTecnologiaTransporte condutor) {
        String sql = "select C.* from CONDUTOROPERATRANSPORTE C " +
            "inner join OPERADORATRANSPORTE O on C.OPERADORATECTRANSPORTE_ID = O.ID " +
            "where C.CPF = :cpf " +
            "and o.id = :idOperadora";
        if (condutor.getId() != null) {
            sql += " and C.ID <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpf", condutor.getCpf().trim());
        q.setParameter("idOperadora", condutor.getOperadoraTecTransporte().getId());
        if (condutor.getId() != null) {
            q.setParameter("id", condutor.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public CondutorOperadoraTecnologiaTransporte gerarNovoCertificado(CondutorOperadoraTecnologiaTransporte condutor, VeiculoOperadoraTecnologiaTransporte veiculo, Date dataEmissaoCertificado) {
        if (veiculo.getId() == null) return null;
        Date vencimento = DataUtil.acrescentarUmAno(dataEmissaoCertificado);
        if (vencimento.before(DataUtil.dataSemHorario(new Date()))) {
            vencimento = DataUtil.acrescentarUmAno(new Date());
        }
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt != null && parametrosOtt.getCertificadoAutorizacao() != null) {
            CertificadoCondutorOTT certificado = new CertificadoCondutorOTT();
            certificado.setCondutorOperaTransporte(condutor);
            certificado.setVeiculoOtt(veiculo);
            certificado.setVencimento(vencimento);
            certificado.setDataEmissao(new Date());
            try {
                certificado.setDocumentoOficial(documentoOficialFacade.gerarDocumentoOTT(certificado, parametrosOtt.getCertificadoAutorizacao(), certificado.getDocumentoOficial(), sistemaFacade.getUsuarioCorrente()));
            } catch (Exception e) {
                certificado.setDocumentoOficial(null);
                logger.error("Erro ao gerar documento de certificado: " + e);
            }

            if (veiculo.getCertificadoInscricao() != null) {
                if (veiculo.getCertificadoInscricao().getCondutorOtt() == null) {
                    veiculo.getCertificadoInscricao().setCondutorOtt(condutor);
                }
                if (veiculo.getCertificadoInscricao().getDocumentoOficial() == null) {
                    veiculo.getCertificadoInscricao().setDocumentoOficial(certificado.getDocumentoOficial());
                }
                em.merge(veiculo);
            }

            condutor.getCertificados().add(certificado);
            FacesUtil.addOperacaoRealizada("Certificado com vencimento em " + new SimpleDateFormat("dd/MM/yyyy").format(vencimento) + " gerado com sucesso!");
            return em.merge(condutor);
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi encontrado parâmetro configurado para certificado de autorização de renovação!");
        }
        return null;
    }

    public List<CondutorOperadoraTecnologiaTransporte> buscarCondutoresOttPortal(String cnpjOperadora, Integer inicio, Integer max) {
        Query q = em.createQuery("select c from CondutorOperadoraTecnologiaTransporte c " +
                " join c.operadoraTecTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') " +
                " order by c.nomeCondutor")
            .setParameter("cnpj", cnpjOperadora);
        if (inicio != null && max != null) {
            q.setFirstResult(inicio);
            q.setMaxResults(max);
        }
        List<CondutorOperadoraTecnologiaTransporte> retorno = q.getResultList();
        for (CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte : retorno) {
            Hibernate.initialize(condutorOperadoraTecnologiaTransporte.getCondutorOperadoraVeiculoOperadoras());
        }
        return retorno;
    }

    public Integer contarCondutoresOttPortal(String cnpjOperadora) {
        Query q = em.createQuery("select count(distinct c.id) from CondutorOperadoraTecnologiaTransporte c " +
                " join c.operadoraTecTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') " +
                " order by c.nomeCondutor")
            .setParameter("cnpj", cnpjOperadora);
        return ((Long) q.getSingleResult()).intValue();
    }

    public CondutorOttDTO buscarCondutorOttDTOPorCPF(String cnpj, String cpf) {
        CondutorOperadoraTecnologiaTransporte condutor = buscarCondutorOttPorCPF(cnpj, cpf);
        if (condutor != null) {
            return condutor.toDTO();
        }
        return null;
    }

    public CondutorOperadoraTecnologiaTransporte buscarCondutorOttPorCPF(String cnpj, String cpf) {
        Query q = em.createQuery("select c from CondutorOperadoraTecnologiaTransporte c " +
                " join c.operadoraTecTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') " +
                "   and replace(replace(replace(c.cpf,'.',''),'-',''),'/','') = replace(replace(replace(:cpf,'.',''),'-',''),'/','') ")
            .setParameter("cnpj", cnpj)
            .setParameter("cpf", cpf);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (CondutorOperadoraTecnologiaTransporte) q.getResultList().get(0);
        }
        return null;
    }

    public CondutorOttDTO adicionarVeiculoCondutor(String cnpj, String cpf, String placa) {
        VeiculoOperadoraTecnologiaTransporte veiculo = veiculoOperadoraTecnologiaTransporteFacade.buscarVeiculoOttPorCNPJPlaca(cnpj, placa);
        if (veiculo != null) {
            CondutorOperadoraTecnologiaTransporte condutor = buscarCondutorOttPorCPF(cnpj, cpf);
            if (condutor != null) {
                CondutorOperadoraVeiculoOperadora veiculoCondutor = new CondutorOperadoraVeiculoOperadora();
                veiculoCondutor.setCondutorOperaTransporte(condutor);
                veiculoCondutor.setVeiculoOTTransporte(veiculo);
                condutor.getCondutorOperadoraVeiculoOperadoras().add(veiculoCondutor);
                return condutor.toDTO();
            }
        }
        return null;
    }

    public String enviarEmailIndeferimentoCondurtorOtt(AssistenteBarraProgresso assistente) {
        CondutorOperadoraTecnologiaTransporte condutor = (CondutorOperadoraTecnologiaTransporte) assistente.getSelecionado();
        assistente.setTotal(1);
        String corpoEmail = montarEmailIndeferimentoCondutorOtt(condutor);
        enviarEmail(condutor.getOperadoraTecTransporte().getEmailResponsavel(), corpoEmail, "OTT - " + condutor.getOperadoraTecTransporte() +
            " - Indeferimento de cadastro do Condutor");
        String email = "Foi enviado um e-mail com o motivo do indeferimento para " + condutor.getOperadoraTecTransporte().getEmailResponsavel();
        assistente.conta();
        assistente.setExecutando(false);
        return email;
    }

    private String montarEmailIndeferimentoCondutorOtt(CondutorOperadoraTecnologiaTransporte condutor) {
        StringBuilder motivoIndeferimento = new StringBuilder();
        motivoIndeferimento.append("Prezado, Informamos que ao analisar a documentação apresentada, ")
            .append("constatamos irregularidades com relação as seguintes situações:").append("<br/>")
            .append("Motivo do indeferimento: ").append(condutor.getMotivoIndeferimento()).append("<br/>")
            .append("Responsável pelo Indeferimento: ").append(condutor.getUsuarioIndeferimento()).append("<br/>")
            .append("Data do indeferimento: ").append(DataUtil.getDataFormatada(condutor.getDataIndeferimento(), "dd/MM/yyyy")).append("<br/><br/>")
            .append("OTT: ").append(condutor.getOperadoraTecTransporte()).append("<br/>")
            .append("Condutor: ").append(condutor.getNomeCondutor()).append("<br/>")
            .append("CPF: ").append(condutor.getCpf()).append("<br/>");

        return motivoIndeferimento.toString();
    }

    public String enviarEmailAprovacaoCondutorOtt(AssistenteBarraProgresso assistente) {
        CondutorOperadoraTecnologiaTransporte condutor = (CondutorOperadoraTecnologiaTransporte) assistente.getSelecionado();
        assistente.setTotal(1);
        String corpoEmail = montarEmailAprovacaoCondutorOtt(condutor);
        enviarEmail(condutor.getOperadoraTecTransporte().getEmailResponsavel(), corpoEmail, "OTT - " + condutor.getOperadoraTecTransporte() +
            " - Aprovação de cadastro do condutor");
        String email = "Foi enviado um e-mail informando a aprovação do condutor para " + condutor.getOperadoraTecTransporte().getEmailResponsavel();
        assistente.conta();
        assistente.setExecutando(false);
        return email;
    }

    private String montarEmailAprovacaoCondutorOtt(CondutorOperadoraTecnologiaTransporte condutor) {
        StringBuilder motivoIndeferimento = new StringBuilder();
        motivoIndeferimento.append("À OTT: " + condutor.getOperadoraTecTransporte().getNome()).append("<br/>")
            .append("Prezado, Informamos que o cadastro do condutor: ").append("<br/>")
            .append(condutor.getNomeCondutor() + " com CPF: ").append("<br/>")
            .append(condutor.getCpf() + " foi aprovado com sucesso!");

        return motivoIndeferimento.toString();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    public void criarHistoricoSituacoesCondutor(CondutorOperadoraTecnologiaTransporte condutor) {
        HistoricoSituacaoCondutorOTT historicoSituacaoCondutorOTT = new HistoricoSituacaoCondutorOTT(condutor, sistemaFacade.getUsuarioCorrente());
        condutor.getHistoricoSituacoesCondutor().add(historicoSituacaoCondutorOTT);
    }

    public List<HistoricoSituacaoCondutorOTT> recuperarHistoricoCondutor(Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select HISTORICO.* from HISTORICOSITCONDUTOROTT HISTORICO ")
            .append(" inner join CONDUTOROPERATRANSPORTE CONDUTOR on CONDUTOR.ID = HISTORICO.CONDUTOROTT_ID ")
            .append(" where CONDUTOR.ID = :idCondutor ");

        Query q = em.createNativeQuery(sql.toString(), HistoricoSituacaoCondutorOTT.class);

        q.setParameter("idCondutor", id);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public String buscarSituacaoCondutorPorOTT(String cnpj, String cpfCondutor) {
        String sql = " select condutor.SITUACAOCONDUTOROTT from OPERADORATRANSPORTE ott " +
            " inner join CONDUTOROPERATRANSPORTE condutor on ott.ID = condutor.OPERADORATECTRANSPORTE_ID " +
            " where (replace(replace(replace(ott.CNPJ), '.', ''), '-', ''), '/', '')) = :cnpjOTT " +
            " and (replace(replace(condutor.CPF), '.', ''), '-', '') = :cpfCondutor ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("cnpjOTT", StringUtil.retornaApenasNumeros(cnpj));
        q.setParameter("cpfCondutor", StringUtil.retornaApenasNumeros(cpfCondutor));

        if (q.getSingleResult() != null) {
            return (String) q.getSingleResult();
        }
        return null;
    }

    public RenovacaoCondutorOTT aprovarRenovacaoCondutorOTT(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        renovacaoCondutorOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.APROVADO);
        return em.merge(renovacaoCondutorOTT);
    }

    public void rejeitarRenovacaoCondutorOTT(RenovacaoCondutorOTT renovacaoCondutorOTT,
                                             List<CondutorRenovacaoDetentorArquivo> documentosRejeitados) {
        renovacaoCondutorOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.AGUARDANDO_DOCUMENTACAO);
        em.merge(renovacaoCondutorOTT);
        enviarEmailDocumentacaoRejeitada(renovacaoCondutorOTT, documentosRejeitados);
    }

    private void enviarEmailDocumentacaoRejeitada(RenovacaoCondutorOTT renovacao,
                                                  List<CondutorRenovacaoDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Renovação do Condutor de Operadora de Tecnologia de Transporte";
        String conteudo = "";
        for (CondutorRenovacaoDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo = conteudo + (documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento") + ": ";
            conteudo = conteudo + documentoRejeitado.getObservacao();
            conteudo = conteudo + "\n";
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmailRenovacao(documentosRejeitados);

        EmailService.getInstance().enviarEmail(renovacao.getCondutorOtt().getOperadoraTecTransporte().getEmailResponsavel(),
            titulo, conteudo, anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmailRenovacao(List<CondutorRenovacaoDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (CondutorRenovacaoDetentorArquivo documento : documentos) {
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

    public CondutorOperadoraTecnologiaTransporte condutorVinculadoAoVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo) {
        String sql = "select condutor.id " +
            " from condutorveiculo condutorveiculo " +
            "         inner join condutoroperatransporte condutor on condutorveiculo.condutoroperatransporte_id = condutor.id " +
            "         inner join veiculoottransporte veiculo on condutorveiculo.veiculoottransporte_id = veiculo.id " +
            " where veiculo.id = :idVeiculo" +
            "  and veiculo.operadoratransporte_id = :idOtt " +
            "  and condutor.operadoratectransporte_id = :idOtt";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idVeiculo", veiculo.getId());
        query.setParameter("idOtt", veiculo.getOperadoraTransporte().getId());
        List<BigDecimal> resultQuery = query.getResultList();
        if (!resultQuery.isEmpty()) {
            return recuperar(((BigDecimal) resultQuery.get(0)).longValue());
        }
        return null;
    }

    public List<ArquivoDTO> recuperarArquivosVeiculo(VeiculoOttDTO veiculo) {
        String hql = " select veic from VeiculoOperadoraDetentorArquivo veic where veic.veiculoOtt.id = :idVeiculo";
        Query q = em.createQuery(hql);
        q.setParameter("idVeiculo", veiculo.getId());

        List<VeiculoOperadoraDetentorArquivo> arquivos = q.getResultList();
        List<ArquivoDTO> retorno = Lists.newArrayList();

        for (VeiculoOperadoraDetentorArquivo arquivo : arquivos) {
            retorno.add(arquivo.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().toArquivoDTO());
        }
        return retorno;
    }

    public List<RenovacaoCondutorOttDTO> buscarRenovacoesPorCondutorOtt(Long id) {

        String hql = " select renovacao" +
            " from RenovacaoCondutorOTT renovacao " +
            " where renovacao.condutorOtt.id = :idCondutor";
        Query q = em.createQuery(hql);
        q.setParameter("idCondutor", id);
        List<RenovacaoCondutorOTT> renovacao = q.getResultList();
        List<RenovacaoCondutorOttDTO> retorno = Lists.newArrayList();
        for (RenovacaoCondutorOTT arquivo : renovacao) {
            retorno.add(arquivo.toDTO());
        }
        return retorno;
    }

    public RenovacaoCondutorOTT gerarNovoCertificadoRenovacaoCondutor(RenovacaoCondutorOTT renovacao, Date vencimento, UsuarioSistema usuarioSistema, ParametrosOtt parametrosOtt) {
        if (parametrosOtt != null && parametrosOtt.getCertificadoRenovacao() != null) {
            CertificadoRenovacaoOTT certificado = new CertificadoRenovacaoOTT();
            certificado.setCondutorOtt(renovacao.getCondutorOtt());
            certificado.setVencimento(vencimento);
            certificado.setDataEmissao(new Date());
            try {
                certificado.setDocumentoOficial(documentoOficialFacade.gerarDocumentoOTT(certificado, parametrosOtt.getCertificadoRenovacao(), certificado.getDocumentoOficial(), usuarioSistema));
            } catch (Exception e) {
                certificado.setDocumentoOficial(null);
            }
            renovacao.setCertificadoRenovacaoOTT(certificado);
            return em.merge(renovacao);
        }
        return null;
    }

    public RenovacaoCondutorOTT buscarRenovacaoOttPorCondutorSituacao(Long idCondutor, String situacaoRenovacao) {

        String sql = "select renovacao.* " +
            " from RenovacaoCondutorOTT renovacao " +
            " inner join CondutorOperaTransporte condutor on condutor.id = renovacao.condutorOtt_id " +
            "  where condutor.id = :idCondutor " +
            "    and renovacao.situacaoRenovacao = :situacaoRenovacao ";
        Query q = em.createNativeQuery(sql, RenovacaoCondutorOTT.class);
        q.setParameter("situacaoRenovacao", situacaoRenovacao);
        q.setParameter("idCondutor", idCondutor);
        if (!q.getResultList().isEmpty()) {
            return (RenovacaoCondutorOTT) q.getResultList().get(0);
        }
        return null;
    }

    public List<CondutorOttDTO> buscarCondutoresPorCpfNome(String filtro, Long idOperadora) {

        String hql = " select condutor" +
            " from CondutorOperadoraTecnologiaTransporte condutor " +
            " where condutor.operadoraTecTransporte.id = :idOperadora " +
            " and (lower(condutor.nomeCondutor) like :filtro " +
            " or (REGEXP_REPLACE(condutor.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]'))) " +
            " order by condutor.nomeCondutor";
        Query q = em.createQuery(hql);
        q.setParameter("cpf", filtro.trim());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("idOperadora", idOperadora);
        List<CondutorOperadoraTecnologiaTransporte> condutores = q.getResultList();
        List<CondutorOttDTO> retorno = Lists.newArrayList();
        for (CondutorOperadoraTecnologiaTransporte cond : condutores) {
            retorno.add(cond.toDTO());
        }
        return retorno;
    }

    public void atribuirDocumentosCondutor(CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte) {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return;
        List<DocumentoCondutorOtt> documentosObrigatorios = parametrosOtt.getDocumentosCondutor()
            .stream().filter(doc -> doc.getAtivo() && !doc.getServidorPublico()).collect(Collectors.toList());
        for (DocumentoCondutorOtt documentoCondutorOtt : documentosObrigatorios) {
            addDocumentoCondutorOtt(condutorOperadoraTecnologiaTransporte, documentoCondutorOtt);
        }
        if (condutorOperadoraTecnologiaTransporte.getServidorPublico()) {
            List<DocumentoCondutorOtt> documentosObrigatoriosServidor = parametrosOtt.getDocumentosCondutor()
                .stream().filter(doc -> doc.getAtivo() && doc.getServidorPublico()).collect(Collectors.toList());
            for (DocumentoCondutorOtt documentoCondutorOtt : documentosObrigatoriosServidor) {
                addDocumentoCondutorOtt(condutorOperadoraTecnologiaTransporte, documentoCondutorOtt);
            }
        } else {
            condutorOperadoraTecnologiaTransporte
                .getCondutorOperadoraDetentorArquivos().removeIf(CondutorOperadoraDetentorArquivo::getServidorPublico);
        }
    }

    private void addDocumentoCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOperadoraTecnologiaTransporte,
                                         DocumentoCondutorOtt documentoCondutorOtt) {
        if (condutorOperadoraTecnologiaTransporte.getCondutorOperadoraDetentorArquivos().stream()
            .noneMatch(condDoc -> condDoc.getDescricaoDocumento().equals(documentoCondutorOtt.getDescricao()))) {
            CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo = new CondutorOperadoraDetentorArquivo();
            condutorOperadoraDetentorArquivo.setCondutorOtt(condutorOperadoraTecnologiaTransporte);
            condutorOperadoraDetentorArquivo.popularDadosDocumento(documentoCondutorOtt);
            condutorOperadoraTecnologiaTransporte.getCondutorOperadoraDetentorArquivos().add(condutorOperadoraDetentorArquivo);
        }
    }

    public void confirmarAvaliacaoDocumentos(CondutorOperadoraTecnologiaTransporte condutor,
                                             List<CondutorOperadoraDetentorArquivo> documentosAvaliacao, VeiculoOperadoraTecnologiaTransporte veiculo) {
        for (CondutorOperadoraDetentorArquivo documentoAvaliacao : documentosAvaliacao) {
            condutor.getCondutorOperadoraDetentorArquivos().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<CondutorOperadoraDetentorArquivo> documentosRejeitados = documentosAvaliacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarCondutorOTT(condutor, documentosRejeitados);
        } else {
            aprovarCondutorOTT(condutor, veiculo);
        }
    }

    private void rejeitarCondutorOTT(CondutorOperadoraTecnologiaTransporte condutor,
                                     List<CondutorOperadoraDetentorArquivo> documentosRejeitados) {
        condutor.setSituacaoCondutorOTT(SituacaoCondutorOTT.AGUARDANDO_DOCUMENTACAO);
        enviarEmailDocumentacaoRejeitada(condutor, documentosRejeitados);
        em.merge(condutor);
    }

    private void enviarEmailDocumentacaoRejeitada(CondutorOperadoraTecnologiaTransporte condutor,
                                                  List<CondutorOperadoraDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Condutor de Operadora de Tecnologia de Transporte";
        StringBuilder conteudo = new StringBuilder();
        for (CondutorOperadoraDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo.append(documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento").append(": ");
            conteudo.append(documentoRejeitado.getObservacao());
            conteudo.append("\n");
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmail(documentosRejeitados);

        EmailService.getInstance().enviarEmail(condutor.getOperadoraTecTransporte().getEmailResponsavel(),
            titulo, conteudo.toString(), anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmail(List<CondutorOperadoraDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (CondutorOperadoraDetentorArquivo documento : documentos) {
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

    public void confirmarAvaliacaoDocumentosRenovacao(RenovacaoCondutorOTT renovacaoCondutorOTT,
                                                      List<CondutorRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao) {
        for (CondutorRenovacaoDetentorArquivo documentoAvaliacao : documentosAvaliacaoRenovacao) {
            renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<CondutorRenovacaoDetentorArquivo> documentosRejeitados = documentosAvaliacaoRenovacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarRenovacaoCondutorOTT(renovacaoCondutorOTT, documentosRejeitados);
        } else {
            aprovarRenovacaoCondutorOTT(renovacaoCondutorOTT);
        }
    }

    public RenovacaoCondutorOTT salvarRenovacao(RenovacaoCondutorOTT renovacao) {
        return em.merge(renovacao);
    }
}
