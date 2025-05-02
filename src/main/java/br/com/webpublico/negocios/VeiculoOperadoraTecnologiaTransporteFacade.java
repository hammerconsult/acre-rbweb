package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoRenovacaoOTT;
import br.com.webpublico.enums.SituacaoVeiculoOTT;
import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.ott.RenovacaoVeiculoOttDTO;
import br.com.webpublico.ott.VeiculoOttDTO;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class VeiculoOperadoraTecnologiaTransporteFacade extends AbstractFacade<VeiculoOperadoraTecnologiaTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;
    @EJB
    private VistoriaVeiculoOttFacade vistoriaVeiculoOttFacade;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;
    @EJB
    private CondutorOperadoraTecnologiaTransporteFacade condutorOperadoraTecnologiaTransporteFacade;

    public VeiculoOperadoraTecnologiaTransporteFacade() {
        super(VeiculoOperadoraTecnologiaTransporte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public VeiculoOperadoraTecnologiaTransporte recuperar(Object id) {
        VeiculoOperadoraTecnologiaTransporte veiculo = em.find(VeiculoOperadoraTecnologiaTransporte.class, id);
        Hibernate.initialize(veiculo.getVeiculoOperadoraDetentorArquivos());
        Hibernate.initialize(veiculo.getHistoricoSituacoesVeiculo());
        Hibernate.initialize(veiculo.getRenovacaoVeiculoOTTS());
        for (RenovacaoVeiculoOTT renovacaoVeiculoOTT : veiculo.getRenovacaoVeiculoOTTS()) {
            if (renovacaoVeiculoOTT != null) {
                Hibernate.initialize(renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos());
            }
        }
        return veiculo;
    }

    public CondutorOperadoraTecnologiaTransporteFacade getCondutorOperadoraTecnologiaTransporteFacade() {
        return condutorOperadoraTecnologiaTransporteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametrosTransitoTransporteFacade getParametrosTransitoTransporteFacade() {
        return parametrosTransitoTransporteFacade;
    }

    public VistoriaVeiculoOttFacade getVistoriaVeiculoOttFacade() {
        return vistoriaVeiculoOttFacade;
    }

    public OperadoraTecnologiaTransporteFacade getOperadoraTecnologiaTransporteFacade() {
        return operadoraTecnologiaTransporteFacade;
    }

    public RenovacaoVeiculoOTT recuperarRenovacao(Object id) {
        RenovacaoVeiculoOTT renovacao = em.find(RenovacaoVeiculoOTT.class, id);
        Hibernate.initialize(renovacao.getVeiculoRenovacaoDetentorArquivos());
        return renovacao;
    }

    public void aprovarParaVistoriaVeiculoOTT(VeiculoOperadoraTecnologiaTransporte selecionado) {
        selecionado.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.AGUARDANDO_VISTORIA);
        criarHistoricoSituacoesVeiculo(selecionado, sistemaFacade.getUsuarioCorrente());
        enviarEmailAguardandoVistoria(selecionado);
        em.merge(selecionado);
    }

    private void enviarEmailAguardandoVistoria(VeiculoOperadoraTecnologiaTransporte veiculo) {
        String titulo = "Veiculo " + veiculo.getModeloVeiculo() + " (" + veiculo.getPlacaVeiculo().toUpperCase() + ")" + " aguardando vistoria.";
        String conteudo = "Solicita-se que o responsável pelo veículo " + veiculo.getModeloVeiculo() + ", placa " + veiculo.getPlacaVeiculo().toUpperCase() + " compareça à RBTRANS, " +
            "situada na Via Verde, 4330, Rio Branco - AC, CEP 69912-293, para a realização dos procedimentos de vistoria.";
        String emailOtt = veiculo.getOperadoraTransporte().getEmailResponsavel();
        if (emailOtt == null || emailOtt.isEmpty()) {
            logger.info("Não foi encontrado o email do responsável pela OTT");
            return;
        }
        EmailService.getInstance().enviarEmail(emailOtt, titulo, conteudo);
    }

    public void aprovarVeiculoOTT(VeiculoOperadoraTecnologiaTransporte selecionado) {
        gerarCertificadoInscricaoVeiculo(selecionado);
        selecionado.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.APROVADO);
        criarHistoricoSituacoesVeiculo(selecionado, sistemaFacade.getUsuarioCorrente());
        em.merge(selecionado);
    }

    public void criarNotificacaoAguardandoAprovacaoVeiculo(VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte) {
        String msg = "Novo cadastro de veículo  de Operadora de Tecnologia de Transporte - OTT, aguardando aprovação.";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVO_VEICULO_OPERADORA_TRANSPORTE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_AGUARDANDO_APROVACAO_NOVO_VEICULO_OPERADORA_TRANSPORTE);
        notificacao.setLink("/veiculo-operadora-tecnologia-transporte/ver/" + veiculoOperadoraTecnologiaTransporte.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public VeiculoOperadoraTecnologiaTransporte buscarVeiculoPorId(Long id) {
        VeiculoOperadoraTecnologiaTransporte condutor = em.find(VeiculoOperadoraTecnologiaTransporte.class, id);
        if (condutor != null) {
            Hibernate.initialize(condutor.getVeiculoOperadoraDetentorArquivos());
            Hibernate.initialize(condutor.getRenovacaoVeiculoOTTS());
        }
        String sql = " select veiculo.* from VEICULOOTTRANSPORTE veiculo " +
            " where veiculo.id = :idVeiculo ";
        Query q = em.createNativeQuery(sql, VeiculoOperadoraTecnologiaTransporte.class);
        q.setParameter("idVeiculo", id);
        if (!q.getResultList().isEmpty()) {
            return (VeiculoOperadoraTecnologiaTransporte) q.getResultList().get(0);
        }
        return null;
    }

    @Override
    public void salvarNovo(VeiculoOperadoraTecnologiaTransporte entity) {
        entity = em.merge(entity);
        criarNotificacaoAguardandoAprovacaoVeiculo(entity);
    }

    public boolean verificarPlacaVeiculoPorOperadora(VeiculoOperadoraTecnologiaTransporte veiculo) {
        String sql = "select O.* from VEICULOOTTRANSPORTE V " +
            "inner join OPERADORATRANSPORTE O on V.OPERADORATRANSPORTE_ID = O.ID " +
            "where upper(V.PLACAVEICULO) = :placaveiculo " +
            "and O.id = :idOperadora";
        if (veiculo.getId() != null) {
            sql += " and V.ID <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("placaveiculo", veiculo.getPlacaVeiculo().trim().toUpperCase());
        q.setParameter("idOperadora", veiculo.getOperadoraTransporte().getId());
        if (veiculo.getId() != null) {
            q.setParameter("id", veiculo.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<VeiculoOperadoraTecnologiaTransporte> listarVeiculosPorOtt(String parte, OperadoraTecnologiaTransporte operadoraTecnologiaTransporte) {
        String sql = "select v.* from VEICULOOTTRANSPORTE v " +
            " inner join marca mr on mr.id = v.marca_id " +
            "  left join cor cor on cor.id = v.cor_id " +
            " where v.situacaoVeiculoOTT IN (:situacoesVeiculoOTT) " +
            "   and v.OPERADORATRANSPORTE_ID = :idOperadora " +
            "   and (lower(v.placaVeiculo) like :parte " +
            "    or lower(v.modeloVeiculo) like :parte " +
            "    or lower(cor.descricao) like :parte " +
            "    or lower(mr.descricao) like :parte) ";
        Query q = em.createNativeQuery(sql, VeiculoOperadoraTecnologiaTransporte.class);
        q.setParameter("situacoesVeiculoOTT", Lists.newArrayList(SituacaoVeiculoOTT.APROVADO.name(), SituacaoVeiculoOTT.AGUARDANDO_VISTORIA.name()));
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idOperadora", operadoraTecnologiaTransporte.getId());
        return q.getResultList();
    }

    public List<VeiculoOperadoraTecnologiaTransporte> buscarVeiculosOttPortal(String cnpjOperadora, Integer inicio, Integer max) {
        Query q = em.createQuery("select v from VeiculoOperadoraTecnologiaTransporte v " +
                " join v.operadoraTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') " +
                " order by v.placaVeiculo")
            .setParameter("cnpj", cnpjOperadora);
        if (inicio != null && max != null) {
            q.setFirstResult(inicio);
            q.setMaxResults(max);
        }
        return q.getResultList();
    }

    public Integer contarVeiculosOttPortal(String cnpjOperadora) {
        Query q = em.createQuery("select count(distinct v.id) from VeiculoOperadoraTecnologiaTransporte v " +
                " join v.operadoraTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') ")
            .setParameter("cnpj", cnpjOperadora);
        return ((Long) q.getSingleResult()).intValue();
    }

    public VeiculoOttDTO buscarVeiculoOttDTOPorCNPJPlaca(String cnpj, String placa) {
        VeiculoOperadoraTecnologiaTransporte veiculo = buscarVeiculoOttPorCNPJPlaca(cnpj, placa);
        if (veiculo != null) {
            return veiculo.toDTO();
        }
        return null;
    }

    public VeiculoOperadoraTecnologiaTransporte buscarVeiculoOttPorCNPJPlaca(String cnpj, String placa) {
        Query q = em.createQuery("select v from VeiculoOperadoraTecnologiaTransporte v " +
                " join v.operadoraTransporte ott " +
                " where replace(replace(replace(ott.cnpj,'.',''),'-',''),'/','') = replace(replace(replace(:cnpj,'.',''),'-',''),'/','') " +
                "   and replace(upper(v.placaVeiculo),'-','') = replace(:placa,'-','') ")
            .setParameter("cnpj", cnpj)
            .setParameter("placa", placa.toUpperCase().trim());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (VeiculoOperadoraTecnologiaTransporte) q.getResultList().get(0);
        }
        return null;
    }

    public String enviarEmailIndeferimentoVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculo) {
        String corpoEmail = montarEmailIndeferimentoVeiculoOtt(veiculo);
        enviarEmail(veiculo.getOperadoraTransporte().getEmailResponsavel(), corpoEmail, "OTT - " + veiculo.getOperadoraTransporte() +
            " - Indeferimento de cadastro do veículo");
        String email = "Foi enviado um e-mail com o motivo do indeferimento para " + veiculo.getOperadoraTransporte().getEmailResponsavel();

        return email;
    }

    private String montarEmailIndeferimentoVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculo) {
        StringBuilder motivoIndeferimento = new StringBuilder();
        motivoIndeferimento.append("Prezado, Informamos que ao analisar a documentação apresentada, ").append("<br/>")
            .append("Constatamos irregularidades com relação as seguintes situações:").append("<br/>")
            .append("Motivo do indeferimento: ").append(veiculo.getMotivoIndeferimento()).append("<br/>")
            .append("Responsável pelo Indeferimento: ").append(veiculo.getUsuarioIndeferimento()).append("<br/>")
            .append("Data do indeferimento: ").append(DataUtil.getDataFormatada(veiculo.getDataIndeferimento(), "dd/MM/yyyy")).append("<br/><br/>")
            .append("OTT: ").append(veiculo.getOperadoraTransporte()).append("<br/>")
            .append("Veiculo: ").append(veiculo.toString());

        return motivoIndeferimento.toString();
    }

    public String enviarEmailAprovacaoVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculo){
        String corpoEmail = montarEmailAprovacaoVeiculoOtt(veiculo);
        enviarEmail(veiculo.getOperadoraTransporte().getEmailResponsavel(), corpoEmail, "OTT - " + veiculo.getOperadoraTransporte() +
            " - Aprovação de cadastro do veículo");
        String email = "Foi enviado um e-mail informando a aprovação do veículo para " + veiculo.getOperadoraTransporte().getEmailResponsavel();

        return email;
    }

    private String montarEmailAprovacaoVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculo) {
        StringBuilder motivoIndeferimento = new StringBuilder();
        motivoIndeferimento.append("Prezado, Informamos que o cadastro do veículo: ").append("<br/>")
            .append(veiculo.toString()).append("<br/>")
            .append("Foi aprovado com sucesso!");

        return motivoIndeferimento.toString();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarEmail(String email, String corpoEmail, String assunto) {
        EmailService.getInstance().enviarEmail(email, assunto, corpoEmail);
    }

    public boolean recuperarVistoriaVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo){
        StringBuilder sql = new StringBuilder();
        sql.append("select VEIC.* from VEICULOOTTRANSPORTE VEIC ")
            .append("inner join VISTORIAVEICULOOTT VIST ")
            .append("on VEIC.ID = VIST.VEICULOOTTRANSPORTE_ID ")
            .append("where VIST.VENCIMENTO > to_date(:vencimento,'dd/MM/yyyy') ")
            .append("and VEIC.ID = :idVeiculo");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("vencimento", DataUtil.getDataFormatada(new Date()));
        q.setParameter("idVeiculo", veiculo.getId());

        return !q.getResultList().isEmpty();
    }

    public void criarHistoricoSituacoesVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo, UsuarioSistema usuario) {
        HistoricoSituacaoVeiculoOTT historicoSituacaoVeiculoOTT = new HistoricoSituacaoVeiculoOTT(veiculo, usuario);
        veiculo.getHistoricoSituacoesVeiculo().add(historicoSituacaoVeiculoOTT);
    }

    public List<HistoricoSituacaoVeiculoOTT> recuperarHistoricoCondutor(Long id) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select HISTORICO.* from HISTORICOSITVEICULOOTT HISTORICO ")
            .append(" inner join VEICULOOTTRANSPORTE VEICULO on VEICULO.ID = HISTORICO.VEICULOOTT_ID ")
            .append(" where VEICULO.ID = :idVeiculo ");

        Query q = em.createNativeQuery(sql.toString(), HistoricoSituacaoVeiculoOTT.class);
        q.setParameter("idVeiculo", id);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public String buscarSituacaoVeiuculoPorOTT(String cnpj, String placaVeiculo) {
        String sql = " select veiculo.SITUACAOVEICULOOTT from OPERADORATRANSPORTE ott " +
            " inner join VEICULOOTTRANSPORTE veiculo on ott.ID = veiculo.OPERADORATRANSPORTE_ID " +
            " where (replace(replace(replace(ott.CNPJ, '.', ''), '-', ''), '/', '')) = :cnpjOTT " +
            " and veiculo.PLACAVEICULO = :placaVeiculo ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("cnpjOTT", StringUtil.retornaApenasNumeros(cnpj));
        q.setParameter("placaVeiculo", StringUtil.removeCaracteresEspeciais(placaVeiculo.trim()));
        try {
            return (String) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CondutorOperadoraTecnologiaTransporte> buscarCondutoresPorVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo) {
        String sql = " select cotransp.id " +
            " from condutorveiculo co " +
            " inner join condutoroperatransporte cotransp on cotransp.id = co.condutoroperatransporte_id " +
            " inner join VEICULOOTTRANSPORTE vt on co.VEICULOOTTRANSPORTE_ID = vt.ID " +
            " where vt.ID = :idVeiculo and cotransp.operadoratectransporte_id = vt.operadoratransporte_id ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idVeiculo", veiculo.getId());
        List<BigDecimal> result = q.getResultList();
        List<CondutorOperadoraTecnologiaTransporte> retorno = Lists.newArrayList();

        for (BigDecimal idCondutor : result) {
            retorno.add(condutorOperadoraTecnologiaTransporteFacade.recuperar(idCondutor.longValue()));
        }
        return retorno;
    }

    public void aprovarRenovacaoVeiculoOTT(RenovacaoVeiculoOTT renovacaoVeiculoOTT) {
        gerarCertificadoRenovacaoVeiculo(renovacaoVeiculoOTT);
        renovacaoVeiculoOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.APROVADO);
        em.merge(renovacaoVeiculoOTT);
    }

    public void rejeitarRenovacaoVeiculoOTT(RenovacaoVeiculoOTT renovacaoVeiculoOTT,
                                            List<VeiculoRenovacaoDetentorArquivo> documentosRejeitados) {
        renovacaoVeiculoOTT.setSituacaoRenovacao(SituacaoRenovacaoOTT.AGUARDANDO_DOCUMENTACAO);
        em.merge(renovacaoVeiculoOTT);
        enviarEmailDocumentacaoRejeitada(renovacaoVeiculoOTT, documentosRejeitados);
    }

    private void enviarEmailDocumentacaoRejeitada(RenovacaoVeiculoOTT renovacao,
                                                  List<VeiculoRenovacaoDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Renovação de Veículo de Operadora de Tecnologia de Transporte";
        String conteudo = "";
        for (VeiculoRenovacaoDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo = conteudo + (documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento") + ": ";
            conteudo = conteudo + documentoRejeitado.getObservacao();
            conteudo = conteudo + "\n";
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmailRenovacao(documentosRejeitados);

        EmailService.getInstance().enviarEmail(renovacao.getVeiculoOtt().getOperadoraTransporte().getEmailResponsavel(),
            titulo, conteudo, anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmailRenovacao(List<VeiculoRenovacaoDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (VeiculoRenovacaoDetentorArquivo documento : documentos) {
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

    public List<RenovacaoVeiculoOttDTO> buscarRenovacoesPorVeiculoOtt(Long id) {

        String hql = " select renovacao" +
            " from RenovacaoVeiculoOTT renovacao " +
            " where renovacao.veiculoOtt.id = :idVeiculo";
        Query q = em.createQuery(hql);
        q.setParameter("idVeiculo", id);
        List<RenovacaoVeiculoOTT> renovacao = q.getResultList();
        List<RenovacaoVeiculoOttDTO> retorno = Lists.newArrayList();
        for (RenovacaoVeiculoOTT arquivo : renovacao) {
            retorno.add(arquivo.toDTO());
        }
        return retorno;
    }

    private Date calcularDataVencimentoCertificado(VeiculoOperadoraTecnologiaTransporte veiculo) {
        Integer ultimoDigito = new Integer(veiculo.getPlacaVeiculo().substring(veiculo.getPlacaVeiculo().trim().length() - 1));
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        DigitoVencimento digitoVencimento = parametrosOtt.getVencimentos()
            .stream().filter(venc -> venc.getDigito().equals(ultimoDigito)
                && DigitoVencimento.TipoDigitoVencimento.CERTIFICADO_VEICULO_OTT.equals(venc.getTipoDigitoVencimento()))
            .findFirst()
            .orElse(null);
        if (digitoVencimento != null) {
            Date vencimento = DateUtils.getData(digitoVencimento.getDia(), digitoVencimento.getMes(), DateUtils.getAno(new Date()));
            if (vencimento.before(new Date())) {
                vencimento = DataUtil.acrescentarUmAno(vencimento);
            }
            return vencimento;
        }
        return null;
    }

    public void carregarDocumentoOTT(CertificadoRenovacaoOTT certificado) {
        try {
            if (certificado.getId() == null) return;
            ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
            certificado.setDocumentoOficial(documentoOficialFacade.gerarDocumentoOTT(certificado, parametrosOtt.getCertificadoRenovacao(), certificado.getDocumentoOficial(), sistemaFacade.getUsuarioCorrente()));
        } catch (Exception e) {
            certificado.setDocumentoOficial(null);
        }
    }

    public void gerarCertificadoInscricaoVeiculo(VeiculoOperadoraTecnologiaTransporte veiculo) {
        if (veiculo.getCertificadoInscricao() != null) return;
        if (SituacaoVeiculoOTT.APROVADO.equals(veiculo.getSituacaoVeiculoOTT())) return;
        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.condutorVinculadoAoVeiculo(veiculo);
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt != null) {
            Date vencimento = calcularDataVencimentoCertificado(veiculo);
            CertificadoRenovacaoOTT certificado = new CertificadoRenovacaoOTT();
            certificado.setVencimento(vencimento);
            certificado.setVeiculoOtt(veiculo);
            certificado.setDataEmissao(new Date());
            veiculo.setCertificadoInscricao(certificado);
            if (condutor != null) {
                //carregarDocumentoOTT(certificado);
                certificado.setCondutorOtt(condutor);
            }
            em.merge(veiculo);
        }
    }

    public void gerarCertificadoRenovacaoVeiculo(RenovacaoVeiculoOTT renovacao) {
        if (renovacao.getCertificadoRenovacaoOTT() != null) return;
        CondutorOperadoraTecnologiaTransporte condutor = condutorOperadoraTecnologiaTransporteFacade.condutorVinculadoAoVeiculo(renovacao.getVeiculoOtt());
        if (condutor == null) {
            FacesUtil.addFatal("Não é possível gerar o certificado!", "O veiculo não está vinculado à nenhum condutor.");
            return;
        }
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt != null && parametrosOtt.getCertificadoAutorizacao() != null) {
            Date vencimento = calcularDataVencimentoCertificado(renovacao.getVeiculoOtt());
            CertificadoRenovacaoOTT certificado = new CertificadoRenovacaoOTT();
            certificado.setVeiculoOtt(renovacao.getVeiculoOtt());
            certificado.setCondutorOtt(condutor);
            certificado.setVencimento(vencimento);
            certificado.setDataEmissao(new Date());
            carregarDocumentoOTT(certificado);
            renovacao.setCertificadoRenovacaoOTT(certificado);
        }
    }

    public CertificadoRenovacaoOTT buscarCertificadoMaisRecenteVeiculo(Long idVeiculo) {
        List<CertificadoRenovacaoOTT> certificados = Lists.newArrayList();
        VeiculoOperadoraTecnologiaTransporte veiculo = recuperar(idVeiculo);
        if (veiculo.getCertificadoInscricao() != null) certificados.add(veiculo.getCertificadoInscricao());
        for (RenovacaoVeiculoOTT renovacao : veiculo.getRenovacaoVeiculoOTTS()) {
            if (renovacao.getCertificadoRenovacaoOTT() != null)
                certificados.add(renovacao.getCertificadoRenovacaoOTT());
        }
        if (certificados.isEmpty()) return null;
        certificados.sort((o1, o2) -> o2.getDataEmissao().compareTo(o1.getDataEmissao()));
        return certificados.get(0);
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public RenovacaoVeiculoOTT buscarRenovacaoOttPorVeiculoSituacao(Long idVeiculo, String situacaoRenovacao) {

        String sql = "select renovacao.* " +
            " from RenovacaoVeiculoOTT renovacao " +
            " inner join veiculoottransporte veiculo on veiculo.id = renovacao.veiculoOtt_id " +
            "  where veiculo.id = :idVeiculo " +
            "    and renovacao.situacaoRenovacao = :situacaoRenovacao ";
        Query q = em.createNativeQuery(sql, RenovacaoVeiculoOTT.class);
        q.setParameter("situacaoRenovacao", situacaoRenovacao);
        q.setParameter("idVeiculo", idVeiculo);
        if (!q.getResultList().isEmpty()) {
            return (RenovacaoVeiculoOTT) q.getResultList().get(0);
        }
        return null;
    }

    public List<VeiculoOttDTO> buscarVeiculosPorPlacaModelo(String filtro, Long idOperadora) {

        String hql = " select veiculo" +
            " from VeiculoOperadoraTecnologiaTransporte veiculo " +
            " where veiculo.operadoraTransporte.id = :idOperadora " +
            " and (lower(veiculo.placaVeiculo)  like :placa " +
            "   or lower(veiculo.modeloVeiculo) like :modelo)" +
            " order by veiculo.placaVeiculo ";
        Query q = em.createQuery(hql);
        q.setParameter("placa", filtro.trim().toLowerCase() + "%");
        q.setParameter("modelo", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("idOperadora", idOperadora);
        List<VeiculoOperadoraTecnologiaTransporte> veiculos = q.getResultList();
        List<VeiculoOttDTO> retorno = Lists.newArrayList();
        for (VeiculoOperadoraTecnologiaTransporte veic : veiculos) {
            retorno.add(veic.toDTO());
        }
        return retorno;
    }

    public void atribuirDocumentosVeiculo(VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte) {
        ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
        if (parametrosOtt == null) return;
        List<DocumentoVeiculoOtt> documentosObrigatorios = parametrosOtt.getDocumentosVeiculo()
            .stream().filter(doc -> doc.getAtivo() && !doc.getAlugado()).collect(Collectors.toList());
        for (DocumentoVeiculoOtt documentoVeiculoOtt : documentosObrigatorios) {
            addDocumentoVeiculoOtt(veiculoOperadoraTecnologiaTransporte, documentoVeiculoOtt);
        }
        if (veiculoOperadoraTecnologiaTransporte.getAlugado()) {
            List<DocumentoVeiculoOtt> documentosObrigatoriosAlugado = parametrosOtt.getDocumentosVeiculo()
                .stream().filter(doc -> doc.getAtivo() && doc.getAlugado()).collect(Collectors.toList());
            for (DocumentoVeiculoOtt documentoVeiculoOtt : documentosObrigatoriosAlugado) {
                addDocumentoVeiculoOtt(veiculoOperadoraTecnologiaTransporte, documentoVeiculoOtt);
            }
        } else {
            veiculoOperadoraTecnologiaTransporte
                .getVeiculoOperadoraDetentorArquivos().removeIf(VeiculoOperadoraDetentorArquivo::getAlugado);
        }
    }

    private void addDocumentoVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte, DocumentoVeiculoOtt documentoVeiculoOtt) {
        if (veiculoOperadoraTecnologiaTransporte.getVeiculoOperadoraDetentorArquivos().stream()
            .noneMatch(veicDoc -> veicDoc.getDescricaoDocumento().equals(documentoVeiculoOtt.getDescricao()))) {
            VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo = new VeiculoOperadoraDetentorArquivo();
            veiculoOperadoraDetentorArquivo.setVeiculoOtt(veiculoOperadoraTecnologiaTransporte);
            veiculoOperadoraDetentorArquivo.popularDadosDocumento(documentoVeiculoOtt);
            veiculoOperadoraTecnologiaTransporte.getVeiculoOperadoraDetentorArquivos().add(veiculoOperadoraDetentorArquivo);
        }
    }

    public void confirmarAvaliacaoDocumentos(VeiculoOperadoraTecnologiaTransporte veiculo,
                                             List<VeiculoOperadoraDetentorArquivo> documentosAvaliacao) {
        for (VeiculoOperadoraDetentorArquivo documentoAvaliacao : documentosAvaliacao) {
            veiculo.getVeiculoOperadoraDetentorArquivos().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<VeiculoOperadoraDetentorArquivo> documentosRejeitados = documentosAvaliacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarVeiculoOTT(veiculo, documentosRejeitados);
        } else {
            aprovarVeiculoOTT(veiculo);
        }
    }

    private void rejeitarVeiculoOTT(VeiculoOperadoraTecnologiaTransporte veiculo,
                                    List<VeiculoOperadoraDetentorArquivo> documentosRejeitados) {
        veiculo.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.AGUARDANDO_DOCUMENTACAO);
        enviarEmailDocumentacaoRejeitada(veiculo, documentosRejeitados);
        em.merge(veiculo);
    }

    private void enviarEmailDocumentacaoRejeitada(VeiculoOperadoraTecnologiaTransporte veiculo,
                                                  List<VeiculoOperadoraDetentorArquivo> documentosRejeitados) {
        String titulo = "Rejeição de Documentação de Veículo de Operadora de Tecnologia de Transporte";
        StringBuilder conteudo = new StringBuilder();
        for (VeiculoOperadoraDetentorArquivo documentoRejeitado : documentosRejeitados) {
            conteudo.append(documentoRejeitado.getDescricaoDocumento() != null ?
                documentoRejeitado.getDescricaoDocumento() : "Outro Documento").append(": ");
            conteudo.append(documentoRejeitado.getObservacao());
            conteudo.append("\n");
        }

        List<AnexoEmailDTO> anexos = criarAnexosEmail(documentosRejeitados);

        EmailService.getInstance().enviarEmail(veiculo.getOperadoraTransporte().getEmailResponsavel(),
            titulo, conteudo.toString(), anexos);
    }

    private List<AnexoEmailDTO> criarAnexosEmail(List<VeiculoOperadoraDetentorArquivo> documentos) {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        for (VeiculoOperadoraDetentorArquivo documento : documentos) {
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

    public RenovacaoVeiculoOTT salvarRenovacaoVeiculo(RenovacaoVeiculoOTT renovacao) {
        return em.merge(renovacao);
    }

    public void confirmarAvaliacaoDocumentosRenovacao(RenovacaoVeiculoOTT renovacaoVeiculoOTT,
                                                      List<VeiculoRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao) {
        for (VeiculoRenovacaoDetentorArquivo documentoAvaliacao : documentosAvaliacaoRenovacao) {
            renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos().stream()
                .filter(docArq -> docArq.getId().equals(documentoAvaliacao.getId()))
                .findFirst()
                .ifPresent(docArq -> {
                    docArq.setStatus(documentoAvaliacao.getStatus());
                    docArq.setObservacao(documentoAvaliacao.getObservacao());
                });
        }
        List<VeiculoRenovacaoDetentorArquivo> documentosRejeitados = documentosAvaliacaoRenovacao.stream()
            .filter(docAval -> docAval.getStatus().equals(StatusDocumentoOtt.REJEITADO))
            .collect(Collectors.toList());
        if (!documentosRejeitados.isEmpty()) {
            rejeitarRenovacaoVeiculoOTT(renovacaoVeiculoOTT, documentosRejeitados);
        } else {
            aprovarRenovacaoVeiculoOTT(renovacaoVeiculoOTT);
        }
    }
}
