package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.SituacaoTransfMovimentoPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TransferenciaMovPessoaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/02/15
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-transferencia-mov-pessoa", pattern = "/transferencia-mov-pessoa/novo/", viewId = "/faces/tributario/cadastromunicipal/pessoatransferencia/edita.xhtml"),
    @URLMapping(id = "editar-transferencia-mov-pessoa", pattern = "/transferencia-mov-pessoa/editar/#{transferenciaMovPessoaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoatransferencia/edita.xhtml"),
    @URLMapping(id = "ver-transferencia-mov-pessoa", pattern = "/transferencia-mov-pessoa/ver/#{transferenciaMovPessoaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/pessoatransferencia/visualizar.xhtml"),
    @URLMapping(id = "listar-transferencia-mov-pessoa", pattern = "/transferencia-mov-pessoa/listar/", viewId = "/faces/tributario/cadastromunicipal/pessoatransferencia/lista.xhtml")
})
public class TransferenciaMovPessoaControlador extends PrettyControlador<TransferenciaMovPessoa> implements Serializable, CRUD {

    @EJB
    private TransferenciaMovPessoaFacade transferenciaMovPessoaFacade;
    private CadastroImobiliario cadastroImobiliario;
    private CadastroEconomico cadastroEconomico;
    private CadastroRural cadastroRural;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private boolean habilitaTransferenciaContabil;
    private boolean habilitaTransferenciaTributario;

    public TransferenciaMovPessoaControlador() {
        super(TransferenciaMovPessoa.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaMovPessoaFacade;
    }

    @URLAction(mappingId = "nova-transferencia-mov-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarTransferencia();
        inicializarArquivo();
    }

    @URLAction(mappingId = "ver-transferencia-mov-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarArquivo();
    }

    @URLAction(mappingId = "editar-transferencia-mov-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarArquivo();
        habilitarTranferencias();

    }

    @Override
    public void salvar() {
        if (podeTransferir()) {
            try {
                if (isOperacaoNovo()) {
                    transferenciaMovPessoaFacade.salvarNovo(selecionado);
                } else {
                    transferenciaMovPessoaFacade.salvarAndReabrir(selecionado);
                }
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    public boolean isPossivelEmitirSimulacao() {
        return selecionado.getPessoaOrigem() != null && !SituacaoTransfMovimentoPessoa.DEFERIDA.equals(selecionado.getSituacao());
    }

    public void processarSelecaoPessoaOrigem() {
        try {
            pessoaVigenteVinculoFP();
            selecionado.setBcis(Lists.<TransferenciaMovPessoaBci>newArrayList());
            selecionado.setBces(Lists.<TransferenciaMovPessoaBce>newArrayList());
            selecionado.setBcrs(Lists.<TransferenciaMovPessoaBcr>newArrayList());
            habilitarTranferencias();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void processarSelecaoPessoaDestino() {
        try {
            habilitarTranferencias();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void pessoaVigenteVinculoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPessoaOrigem() != null && transferenciaMovPessoaFacade.hasPessoaComVinculoFP(selecionado.getPessoaOrigem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa " + selecionado.getPessoaOrigem() + " possui vínculo com a Folha de Pagamento, portanto não é possível realizar a transferência.");
        }
        if (selecionado.getPessoaOrigem() != null && transferenciaMovPessoaFacade.hasPessoaBeneficiariaPensaoAlimenticia(selecionado.getPessoaOrigem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa " + selecionado.getPessoaOrigem() + " é Beneficiária de Pensão Alimentícia, portanto não é possível realizar a transferência.");
        }
        if (ve.temMensagens()) {
            selecionado.setPessoaOrigem(null);
            FacesUtil.atualizarComponente("Formulario");
        }
        ve.lancarException();
    }


    public void habilitarTranferencias() {
        if (selecionado.getPessoaDestino() == null) {
            habilitaTransferenciaContabil = false;
            habilitaTransferenciaTributario = false;
            anularTodasMarcacoes();
            return;
        } else if ("".equals(selecionado.getPessoaDestino().getCpf_Cnpj().trim())) {
            habilitaTransferenciaContabil = false;
            habilitaTransferenciaTributario = false;
            anularTodasMarcacoes();
            return;
        }
        if (selecionado.getPessoaOrigem() == null) {
            habilitaTransferenciaContabil = true;
            habilitaTransferenciaTributario = true;
            anularTodasMarcacoes();
            return;
        } else if ("".equals(selecionado.getPessoaOrigem().getCpf_Cnpj().trim())) {
            habilitaTransferenciaContabil = true;
            habilitaTransferenciaTributario = true;
            return;
        } else if (!selecionado.getPessoaOrigem().getCpf_Cnpj().equals(selecionado.getPessoaDestino().getCpf_Cnpj())) {
            habilitaTransferenciaContabil = false;
            habilitaTransferenciaTributario = true;
            selecionado.setTransfereMovContabeis(false);
            return;
        } else if (selecionado.getPessoaOrigem().getCpf_Cnpj().equals(selecionado.getPessoaDestino().getCpf_Cnpj())) {
            habilitaTransferenciaContabil = true;
            habilitaTransferenciaTributario = true;
            return;
        }
    }

    private void anularTodasMarcacoes() {
        selecionado.setTransfereBcis(false);
        selecionado.setTransfereBces(false);
        selecionado.setTransfereBcrs(false);
        selecionado.setTransfereMovContabeis(false);
        selecionado.setTransfereMovimentosTributario(false);
        selecionado.setInativaPessoaTransferida(false);
    }

    public void emitirSimulacaoTransferenciaMovimento(TransferenciaMovPessoa transferenciaMovPessoa) {
        try {
            AbstractReport ar = AbstractReport.getAbstractReport();
            ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa parametro = new ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa(ar.getCaminho(), ar.getCaminhoImagem(), transferenciaMovPessoa.getUsuarioSistema(), ar.getCaminho());

            ByteArrayOutputStream bytesDoRelatorio = transferenciaMovPessoaFacade.criarSimulacaoTransferenciaMovimento(transferenciaMovPessoa, transferenciaMovPessoaFacade.getSistemaFacade().getUsuarioCorrente(), transferenciaMovPessoaFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(), parametro);
            AbstractReport report = AbstractReport.getAbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("SimulacaoTransferenciaMovimentos", bytesDoRelatorio.toByteArray());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + e.getMessage());
        }
    }

    public StreamedContent montarArquivoParaDownload(ArquivoComposicao arquivo) {
        StreamedContent s = null;
        s = transferenciaMovPessoaFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivoComposicao(arquivo);
        return s;
    }


    private void inicializarTransferencia() {
        selecionado.setDataTransferencia(transferenciaMovPessoaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(transferenciaMovPessoaFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoTransfMovimentoPessoa.ABERTA);
    }

    private void inicializarArquivo() {
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, transferenciaMovPessoaFacade.getSistemaFacade().getDataOperacao());
    }

    private boolean podeTransferir() {
        boolean retorno = Util.validaCampos(selecionado);

        if (retorno) {

        }
        return retorno;
    }

    public boolean desabilitarBotaoEditar(TransferenciaMovPessoa tranf) {
        try {
            if (tranf.getSituacao().equals(SituacaoTransfMovimentoPessoa.ABERTA)
                || tranf.getSituacao().equals(SituacaoTransfMovimentoPessoa.INDEFERIDA)) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    public void selecionarPessoaOrigemPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setPessoaOrigem((Pessoa) obj);
    }

    public void selecionarPessoaDestinoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setPessoaDestino((Pessoa) obj);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-mov-pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }

    public void addCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        if (getCadastroImobiliario() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Cadastro Imobiliário!");
            return;
        }
        for (TransferenciaMovPessoaBci transBci : selecionado.getBcis()) {
            if (transBci.getCadastroImobiliario().equals(cadastroImobiliario)) {
                FacesUtil.addCampoObrigatorio("Cadastro Imobiliário já adicionado!");
                return;
            }
        }
        TransferenciaMovPessoaBci transferenciaMovPessoaBci = new TransferenciaMovPessoaBci();
        transferenciaMovPessoaBci.setTransferenciaMovPessoa(selecionado);
        transferenciaMovPessoaBci.setCadastroImobiliario(cadastroImobiliario);
        selecionado.getBcis().add(transferenciaMovPessoaBci);
        setCadastroImobiliario(null);
    }

    public void addCadastroEconomico(CadastroEconomico cadastroEconomico) {
        if (getCadastroEconomico() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Cadastro Econômico!");
            return;
        }
        for (TransferenciaMovPessoaBce transBce : selecionado.getBces()) {
            if (transBce.getCadastroEconomico().equals(cadastroEconomico)) {
                FacesUtil.addCampoObrigatorio("Cadastro Econômico já adicionado!");
                return;
            }
        }
        TransferenciaMovPessoaBce transferenciaMovPessoaBce = new TransferenciaMovPessoaBce();
        transferenciaMovPessoaBce.setTransferenciaMovPessoa(selecionado);
        transferenciaMovPessoaBce.setCadastroEconomico(cadastroEconomico);
        selecionado.getBces().add(transferenciaMovPessoaBce);
        setCadastroEconomico(null);
    }

    public void delCadastroEconomico(TransferenciaMovPessoaBce transferenciaMovPessoaBce) {
        selecionado.getBces().remove(transferenciaMovPessoaBce);
    }

    public void addCadastroRural(CadastroRural cadastroRural) {
        if (getCadastroRural() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Cadastro Rural!");
            return;
        }
        for (TransferenciaMovPessoaBcr transBcr : selecionado.getBcrs()) {
            if (transBcr.getCadastroRural().equals(cadastroRural)) {
                FacesUtil.addCampoObrigatorio("Cadastro Rural já adicionado!");
                return;
            }
        }
        TransferenciaMovPessoaBcr transferenciaMovPessoaBcr = new TransferenciaMovPessoaBcr();
        transferenciaMovPessoaBcr.setTransferenciaMovPessoa(selecionado);
        transferenciaMovPessoaBcr.setCadastroRural(cadastroRural);
        selecionado.getBcrs().add(transferenciaMovPessoaBcr);
        setCadastroRural(null);
    }

    public void delCadastroRural(TransferenciaMovPessoaBcr transferenciaMovPessoaBcr) {
        selecionado.getBcrs().remove(transferenciaMovPessoaBcr);
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public void delCadastroImobiliario(TransferenciaMovPessoaBci transferenciaMovPessoaBci) {
        selecionado.getBcis().remove(transferenciaMovPessoaBci);
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioPessoaOrigem(String parte) {
        if (selecionado.getPessoaOrigem() == null) {
            Lists.newArrayList();
        }
        return transferenciaMovPessoaFacade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioAtivoPorPessoaAndInscricaoCadastral(parte, selecionado.getPessoaOrigem());
    }

    public List<CadastroEconomico> buscarCadastroEconomicoPessoaOrigem(String parte) {
        if (selecionado.getPessoaOrigem() == null) {
            Lists.newArrayList();
        }
        return transferenciaMovPessoaFacade.getCadastroEconomicoFacade().buscarCadastroEconomicoAtivoPorPessoaAndInscricaoCadastral(parte, selecionado.getPessoaOrigem());
    }

    public List<CadastroRural> buscarCadastroRuralPessoaOrigem(String parte) {
        if (selecionado.getPessoaOrigem() == null) {
            Lists.newArrayList();
        }
        return transferenciaMovPessoaFacade.getCadastroRuralFacade().buscarCadastroRuralPorPessoaAndNumeroIncra(parte, selecionado.getPessoaOrigem());
    }

    public boolean habilitarTransferenciaContabil() {
        return habilitaTransferenciaContabil;
    }

    public boolean habilitarTransferenciaTributario() {
        return habilitaTransferenciaTributario;
    }
}
