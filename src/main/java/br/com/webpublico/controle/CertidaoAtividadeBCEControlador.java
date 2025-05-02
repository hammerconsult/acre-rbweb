package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoCertidaoAtividadeBCE;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "certidaoAtividadeBCEControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBaixaAtividade", pattern = "/baixa-atividade/novo/", viewId = "/faces/tributario/cadastromunicipal/certidaoatividadebce/edita.xhtml"),
    @URLMapping(id = "editarBaixaAtividade", pattern = "/baixa-atividade/editar/#{certidaoAtividadeBCEControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/certidaoatividadebce/edita.xhtml"),
    @URLMapping(id = "listarBaixaAtividade", pattern = "/baixa-atividade/listar/", viewId = "/faces/tributario/cadastromunicipal/certidaoatividadebce/lista.xhtml"),
    @URLMapping(id = "verBaixaAtividade", pattern = "/baixa-atividade/ver/#{certidaoAtividadeBCEControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/certidaoatividadebce/visualizar.xhtml")
})
public class CertidaoAtividadeBCEControlador extends PrettyControlador<CertidaoAtividadeBCE> implements Serializable, CRUD {

    @EJB
    private CertidaoAtividadeBCEFacade certidaoAtividadeBCEFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    private List<ResultadoParcela> parcelasValorDivida;
    private UploadedFile file;
    private Arquivo arquivo;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public AbstractFacade getFacede() {
        return certidaoAtividadeBCEFacade;
    }

    public CertidaoAtividadeBCEControlador() {
        super(CertidaoAtividadeBCE.class);
    }

    @URLAction(mappingId = "novoBaixaAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        arquivo = new Arquivo();
        selecionado.setDataCadastro(getSistemaControlador().getDataOperacao());
        selecionado.setOperador(getSistemaControlador().getUsuarioCorrente());
        parcelasValorDivida = Lists.newArrayList();
    }

    @URLAction(mappingId = "verBaixaAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBaixaAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/baixa-atividade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public List<SelectItem> getTiposCertidoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCertidaoAtividadeBCE cer : TipoCertidaoAtividadeBCE.values()) {
            toReturn.add(new SelectItem(cer, cer.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();

            CadastroEconomico cadastroEconomico = criarNovaSituacaoCadastral();
            alterarDadosCmcPorSituacao(cadastroEconomico);
            certidaoAtividadeBCEFacade.montarArquivoParaSalvar(selecionado);
            selecionado = certidaoAtividadeBCEFacade.salvarRetornado(selecionado);
            cadastroEconomicoFacade.salvar(cadastroEconomico);

            FacesUtil.addInfo("Sucesso!", "Processo realizado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addFatal("Erro!", "Não foi possível realizar esta operação");
            logger.error(e.getMessage());
        }
    }

    private void alterarDadosCmcPorSituacao(CadastroEconomico cadastroEconomico) {
        switch (cadastroEconomico.getSituacaoAtual().getSituacaoCadastral()) {
            case BAIXADA: {
                cadastroEconomico.getEnquadramentoVigente().setSubstitutoTributario(Boolean.FALSE);
                break;
            }
        }
    }

    public void limparValorDoCadastroEconomico() {
        selecionado.setCadastroEconomico(null);
    }

    private CadastroEconomico criarNovaSituacaoCadastral() {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(selecionado.getCadastroEconomico().getId());
        SituacaoCadastroEconomico situacao = new SituacaoCadastroEconomico();
        situacao.setSituacaoCadastral(verificarSituacao());
        situacao.setDataAlteracao(selecionado.getDataCadastro());
        situacao.setDataRegistro(new Date());
        cadastroEconomico.getSituacaoCadastroEconomico().add(situacao);
        return cadastroEconomico;
    }

    private List<ResultadoParcela> pesquisarDebitosDoCadastro() {
        ConsultaParcela consulta = new ConsultaParcela();
        adicionarParametro(consulta);
        consulta.executaConsulta();
        return consulta.getResultados();
    }

    private void adicionarParametro(ConsultaParcela consulta) {
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastroEconomico().getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, getSistemaControlador().getDataOperacao());
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data da Baixa!");
        }
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Econômico!");
        }
        if (selecionado.getSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a situação!");
        }
        if (selecionado.getMotivo() == null || "".equals(selecionado.getMotivo().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo!");
        }
        if (selecionado.getCadastroEconomico() != null) {
            SituacaoCadastroEconomico sit = cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico(selecionado.getCadastroEconomico());
            if (selecionado.getSituacao() != null) {
                if (sit != null && SituacaoCadastralCadastroEconomico.INATIVO.equals(sit.getSituacaoCadastral()) && (!TipoCertidaoAtividadeBCE.ATIVO.equals(selecionado.getSituacao()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico encontra-se INATIVO, só pode ser ATIVADO");
                } else if (sit != null && SituacaoCadastralCadastroEconomico.SUSPENSA.equals(sit.getSituacaoCadastral()) && TipoCertidaoAtividadeBCE.SUSPENSO.equals(selecionado.getSituacao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico já encontra-se SUSPENSO e não pode ser SUSPENSO");
                } else if (sit != null && (SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.equals(sit.getSituacaoCadastral()) || SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.equals(sit.getSituacaoCadastral())) && TipoCertidaoAtividadeBCE.ATIVO.equals(selecionado.getSituacao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico já encontra-se ATIVO e não pode ser ATIVADO");
                } else if (sit != null && (sit.getSituacaoCadastral().equals(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR) || sit.getSituacaoCadastral().equals(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR)) && selecionado.getSituacao().equals(TipoCertidaoAtividadeBCE.ATIVO)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico encontra-se ATIVADO");
                }
            }
        }
        ve.lancarException();
    }

    private void validarDebitos() {
        ValidacaoException ve = new ValidacaoException();
        if (!TipoCertidaoAtividadeBCE.ATIVO.equals(selecionado.getSituacao())) {
            parcelasValorDivida = pesquisarDebitosDoCadastro();
            if (!parcelasValorDivida.isEmpty()) {
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Parcela(s) vencida(s) e não paga(s) encontrada(s)");
                FacesUtil.atualizarComponente("Formulario");
            }
        }
        ve.lancarException();
    }

    private SituacaoCadastralCadastroEconomico verificarSituacao() {
        if (selecionado.getSituacao() != null) {
            return selecionado.getSituacao().getSituacaoCadastral();
        }
        return SituacaoCadastralCadastroEconomico.ATIVA_REGULAR;
    }

    public void gerarCertidao() {
        try {
            SituacaoCadastroEconomico situacaoCadastroEconomico = cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico(selecionado.getCadastroEconomico());
            montarCertidao(selecionado.getCadastroEconomico().getPessoa().getNome(),
                selecionado.getCadastroEconomico().getInscricaoCadastral(),
                selecionado.getCadastroEconomico().getPessoa().getCpf_Cnpj(),
                situacaoCadastroEconomico != null ? situacaoCadastroEconomico.getSituacaoCadastral().getDescricao() : "",
                situacaoCadastroEconomico != null ? situacaoCadastroEconomico.getSituacaoCadastral().getDescricaoMovimento() : "");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void montarCertidao(String contribuinte, String cmc, String cpfcnpj, String atividade, String descricaoMovimento) throws IOException, JRException {
        AbstractReport report = AbstractReport.getAbstractReport();
        report.setGeraNoDialog(true);
        String arquivoJasper = "CertificadoBaixaDeAtividade.jasper";
        HashMap parametros = new HashMap();
        parametros.put("CONTRIBUINTE", contribuinte);
        parametros.put("CMC", cmc);
        parametros.put("CPFCNPJ", cpfcnpj);
        parametros.put("ATIVIDADE", atividade);
        parametros.put("TIPO", descricaoMovimento);
        parametros.put("DATACADASTRO", selecionado.getDataCadastro());
        parametros.put("BRASAO_RIO_BRANCO", report.getCaminhoImagem());
        parametros.put("USUARIO", getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getNome());
        parametros.put("ASSINATURA", configuracaoTributarioFacade.retornaUltimo().getAssinaturaCertidaoAtividadeCmc());
        report.gerarRelatorio(arquivoJasper, parametros);
    }

    public List<ResultadoParcela> getParcelasValorDivida() {
        if (parcelasValorDivida == null) {
            parcelasValorDivida = Lists.newArrayList();
        }
        return parcelasValorDivida;
    }

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        adicionarArquivo();
    }

    public StreamedContent getArquivoStream(CertidaoBCEArquivos certidaoBCEArquivos) throws IOException {
        UploadedFile download = (UploadedFile) certidaoBCEArquivos.getFile();
        return new DefaultStreamedContent(download.getInputstream(), download.getContentType(), download.getFileName());
    }

    public void adicionarArquivo() {
        try {
            validarArquivo(arquivo);
            CertidaoBCEArquivos certidaoBCEArquivos = new CertidaoBCEArquivos();
            certidaoBCEArquivos.setCertidaoAtividadeBce(selecionado);
            certidaoBCEArquivos.setArquivo(arquivo);
            certidaoBCEArquivos.setFile((Object) file);
            arquivo.setMimeType(file.getContentType());
            arquivo.setNome(file.getFileName());
            selecionado.getCertidaoArquivos().add(certidaoBCEArquivos);
            arquivo = new Arquivo();
            file = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerArquivo(CertidaoBCEArquivos certidaoBCEArquivos) {
        selecionado.getCertidaoArquivos().remove(certidaoBCEArquivos);
    }

    private void validarArquivo(Arquivo arq) {
        ValidacaoException ve = new ValidacaoException();
        if (arq == null && (arq.getDescricao() == null || "".equals(arq.getDescricao().trim()))) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a descrição do Arquivo!");
        }
        if (file == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um Arquivo!");
        }
        ve.lancarException();
    }

    public void atualizarForm() {
        try {
            validarDebitos();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<CadastroEconomico> completarCadastroEconomico(String parte) {
        if (selecionado.getSituacao() != null) {
            if (TipoCertidaoAtividadeBCE.ATIVO.equals(selecionado.getSituacao())) {
                return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.SUSPENSA,
                    SituacaoCadastralCadastroEconomico.INATIVO,
                    SituacaoCadastralCadastroEconomico.NULA,
                    SituacaoCadastralCadastroEconomico.BAIXADA);
            } else {
                return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
            }
        }
        return cadastroEconomicoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.NULA);
    }

    public void gerarCertidaoDocumentoOficial() {
        try {
            DocumentoOficial documentoOficial = gerarDocumento();
            documentoOficialFacade.emiteDocumentoOficial(documentoOficial);

            selecionado.setDocumentoOficial(documentoOficial);
            selecionado = certidaoAtividadeBCEFacade.salvarRetornado(selecionado);
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    private DocumentoOficial gerarDocumento() throws UFMException, AtributosNulosException {
        if (selecionado.getDocumentoOficial() == null) {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            TipoDoctoOficial tipo = configuracaoTributario.getTipoDoctoOficialBaixaCmc();
            if (tipo != null) {
                DocumentoOficial docto = documentoOficialFacade.geraDocumentoBaixaAtividade(selecionado, selecionado.getDocumentoOficial(), selecionado.getCadastroEconomico(), tipo, getSistemaControlador());
                selecionado.setDocumentoOficial(docto);
                return docto;
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro do tipo de documento para a Certidão de Baixa!");
            }
        }
        return selecionado.getDocumentoOficial();
    }

}
