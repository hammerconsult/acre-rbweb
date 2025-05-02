package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CabecalhoRelatorioFacade;
import br.com.webpublico.negocios.FornecedorFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 26/06/14
 * Time: 09:07
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "fornecedorControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-fornecedor", pattern = "/fornecedor/novo/", viewId = "/faces/administrativo/licitacao/fornecedor/edita.xhtml"),
        @URLMapping(id = "editar-fornecedor", pattern = "/fornecedor/editar/#{fornecedorControlador.id}/", viewId = "/faces/administrativo/licitacao/fornecedor/edita.xhtml"),
        @URLMapping(id = "ver-fornecedor", pattern = "/fornecedor/ver/#{fornecedorControlador.id}/", viewId = "/faces/administrativo/licitacao/fornecedor/visualizar.xhtml"),
        @URLMapping(id = "listar-fornecedor", pattern = "/fornecedor/listar/", viewId = "/faces/administrativo/licitacao/fornecedor/lista.xhtml")
})
public class FornecedorControlador extends PrettyControlador<Fornecedor> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(FornecedorControlador.class);

    private ConverterAutoComplete converterPessoaFornecedor;
    private PercentualConverter percentualConverter;
    private DocumentoFornecedor documentoFornecedorSelecionado;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private Integer indiceAba;
    private FornecedorCertidao fornecedorCertidao;


    @EJB
    private FornecedorFacade fornecedorFacade;

    public FornecedorControlador() {
        super(Fornecedor.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fornecedor/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return fornecedorFacade;
    }

    public FornecedorFacade getFornecedorFacade() {
        return fornecedorFacade;
    }

    public DocumentoFornecedor getDocumentoFornecedorSelecionado() {
        return documentoFornecedorSelecionado;
    }

    public void setDocumentoFornecedorSelecionado(DocumentoFornecedor documentoFornecedorSelecionado) {
        this.documentoFornecedorSelecionado = documentoFornecedorSelecionado;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    @URLAction(mappingId = "novo-fornecedor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();

        if (isSessao()) {
            Web.pegaDaSessao(PessoaFisica.class);
            Web.pegaDaSessao(PessoaJuridica.class);
        }

        indiceAba = 0;
        Integer indice = (Integer) Web.pegaDaSessao(Integer.class);
        if (indice != null) {
            indiceAba = indice;
        }
    }

    private void inicializarAtributos() {
        setaDataOperacaoNoAlteradoEm();
        carregarListas();
        inicializaAssistenteDetentorArquivo();
    }

    private void setaCodigoVerificacao() {
        if (selecionado.getCodigoVerificacao() == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selecionado.getAlteradoEm());

            selecionado.setCodigoVerificacao(fornecedorFacade.getDocumentoOficialFacade().criptografa("1W8T jU3aR", selecionado.getPessoa().getCpf_Cnpj(), colocarZeroEsquerda(cal.get(Calendar.DAY_OF_MONTH)) + colocarZeroEsquerda(cal.get(Calendar.MONTH)) + "/" + colocarZeroEsquerda(cal.get(Calendar.HOUR)) + colocarZeroEsquerda(cal.get(Calendar.MINUTE))));
        }
    }

    private String colocarZeroEsquerda(int numero) {
        if (numero < 10) {
            return "0" + numero;
        } else {
            return "" + numero;
        }
    }

    private void inicializaAssistenteDetentorArquivo() {
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, fornecedorFacade.getSistemaFacade().getDataOperacao());
    }

    @Override
    public void salvar() {
        if (validarConfirmacao(selecionado) && validarRegraDeNegocio()) {
            setaCodigoVerificacao();
            try {
                fornecedorFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redirecionarAoSalvar();
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao salvar.");
            }
        }
    }

    @Override
    public void excluir() {
        super.excluir();
    }


    public void redirecionarAoSalvar() {
        FacesUtil.redirecionamentoInterno("/fornecedor/ver/" + selecionado.getId() + "/");
    }

    public void redirecionarAoCancelar() {
        FacesUtil.redirecionamentoInterno("/fornecedor/listar/");
    }

    public void redirecionarAoVisualizar() {
        FacesUtil.redirecionamentoInterno("/fornecedor/editar/" + selecionado.getId() + "/");
    }

    public void setaDataOperacaoNoAlteradoEm() {
        if (Operacoes.NOVO.equals(operacao) || Operacoes.EDITAR.equals(operacao)) {
            selecionado.setAlteradoEm(new Date());
        }
    }

    private boolean validarRegraDeNegocio() {
        if (selecionado.getDocumentosFornecedor() == null || selecionado.getDocumentosFornecedor().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Nenhum documento foi adicionado!");
            return false;
        }
        if (selecionado.getPessoa() instanceof PessoaJuridica && getCnaes().size() < 1) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Nenhum ramo de atividade foi adicionado!");
            return false;
        }
        return true;
    }


    @URLAction(mappingId = "editar-fornecedor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarAtributos();
    }

    @URLAction(mappingId = "ver-fornecedor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        fornecedorCertidao = new FornecedorCertidao(selecionado);
    }

    public List<Pessoa> completaPessoaFornecedor(String parte) {
        return fornecedorFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<CNAE> completaRamoDeAtividade(String parte) {
        return fornecedorFacade.getCnaeFacade().listaFiltrando(parte.trim(), "codigoCnae", "descricaoDetalhada");
    }


    public ConverterAutoComplete getConverterPessoaFornecedor() {
        if (converterPessoaFornecedor == null) {
            converterPessoaFornecedor = new ConverterAutoComplete(Pessoa.class, fornecedorFacade.getPessoaFacade());
        }
        return converterPessoaFornecedor;
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public void carregarListas() {
        if (selecionado.getPessoa() != null) {
            selecionado.setPessoa(fornecedorFacade.getPessoaFacade().recuperar(selecionado.getPessoa().getId()));
        }
    }

    public List<PessoaCNAE> getCnaes() {
        if (selecionado.getPessoa() != null && selecionado.getPessoa() instanceof PessoaJuridica) {
            return fornecedorFacade.getPessoaJuridicaFacade().recuperar(selecionado.getPessoa().getId()).getPessoaCNAE();
        }
        return Lists.newArrayList();
    }

    public List<SociedadePessoaJuridica> getSociedadePessoaJuridica() {
        if (selecionado.getPessoa() != null && selecionado.getPessoa() instanceof PessoaJuridica) {
            return fornecedorFacade.getPessoaJuridicaFacade().recuperar(selecionado.getPessoa().getId()).getSociedadePessoaJuridica();
        }
        return Lists.newArrayList();
    }

    public BigDecimal getTotalSociedadePessoaJuridica() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getPessoa() != null && selecionado.getPessoa() instanceof PessoaJuridica) {
            PessoaJuridica pessoaJuridica = fornecedorFacade.getPessoaJuridicaFacade().recuperar(selecionado.getPessoa().getId());
            for (SociedadePessoaJuridica socio : pessoaJuridica.getSociedadePessoaJuridica()) {
                total = total.add(BigDecimal.valueOf(socio.getProporcao()));
            }
        }
        return total;
    }

    public boolean isVisualizar() {
        try {
            return Operacoes.VER.equals(operacao);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public void novoDocumento() {
        this.documentoFornecedorSelecionado = new DocumentoFornecedor();
        this.documentoFornecedorSelecionado.setFornecedor(selecionado);

    }

    public void confirmarDocumento() {
        if (validaCamposDoDocumento()) {
            selecionado.setDocumentosFornecedor(Util.adicionarObjetoEmLista(selecionado.getDocumentosFornecedor(), this.documentoFornecedorSelecionado));
            cancelarDocumento();
        }
    }

    private boolean validaCamposDoDocumento() {
        if (documentoSelecionadoRequerCampoNumero() && documentoFornecedorSelecionado.getNumero() == null) {
            FacesUtil.addCampoObrigatorio("O campo número é obrigatório.");
            return false;
        }
        if (documentoSelecionadoRequerCampoDataDeEmissao() && documentoFornecedorSelecionado.getDataDeEmissao() == null) {
            FacesUtil.addCampoObrigatorio("O campo data de emissão é obrigatório.");
            return false;
        }
        if (documentoSelecionadoRequerCampoDataDeValidade() && documentoFornecedorSelecionado.getDataDeValidade() == null) {
            FacesUtil.addCampoObrigatorio("O campo data de validade é obrigatório.");
            return false;
        }
        return true;
    }

    public void cancelarDocumento() {
        this.documentoFornecedorSelecionado = null;
    }

    public void selecionarDocumento(DocumentoFornecedor documento) {
        this.documentoFornecedorSelecionado = documento;
    }

    public void removerDocumento(DocumentoFornecedor documento) {
        selecionado.getDocumentosFornecedor().remove(documento);
    }

    public boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }

    private Integer getProximoNumero(List<? extends Object> lista) {
        Integer proxNumero = 1;
        if (selecionado.getDocumentosFornecedor() != null && !selecionado.getDocumentosFornecedor().isEmpty()) {
            for (DocumentoFornecedor documento : selecionado.getDocumentosFornecedor()) {
                if (documento.getNumero() != null) {
                    proxNumero++;
                }
            }
        }
        return proxNumero;
    }

    public boolean pessoaFornecedorIsNull() {
        if(selecionado != null && selecionado.getPessoa() != null){
            return false;
        }
        return true;
    }

    public Date getDataDeVencimento() {
        Date dataVencimento = new Date();
        if (selecionado.getAlteradoEm() != null) {
            dataVencimento = selecionado.getAlteradoEm();
            dataVencimento.setYear(dataVencimento.getYear() + 1);
        }
        return dataVencimento;
    }

    public void validaDadosFornecedor() {
        carregarListas();
        if (fornecedorSemEndereco() || fornecedorSemTelefone()) {
            FacesUtil.executaJavaScript("dialogEditaFornecedor.show();");
        }
    }

    public void redirecionarEditarPessoaFornecedor() {
        if (selecionado.getPessoa() != null) {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                Web.limpaNavegacao();
                Web.navegacao(getCaminhoOrigem(), "/tributario/configuracoes/pessoa/editarpessoafisica/" + selecionado.getPessoa().getId() + "/", selecionado, indiceAba);
                return;
            } else {
                Web.limpaNavegacao();
                Web.navegacao(getCaminhoOrigem(), "/tributario/configuracoes/pessoa/editarpessoajuridica/" + selecionado.getPessoa().getId() + "/", selecionado, indiceAba);
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Informe um fornecedor primeiro para depois adicionar um sócio.");
        }
    }

    private boolean fornecedorSemTelefone() {
        carregarListas();
        if (selecionado.getPessoa() != null && selecionado.getPessoa().getTelefones() == null || selecionado.getPessoa().getTelefones().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean fornecedorSemEndereco() {
        if (selecionado.getPessoa() != null && selecionado.getPessoa().getEnderecos() == null || selecionado.getPessoa().getEnderecos().isEmpty()) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getDocumentoHabilitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (DoctoHabilitacao object : fornecedorFacade.getDoctoHabilitacaoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricaoReduzida()));
        }
        return toReturn;
    }


    public StreamedContent recuperarArquivoParaDownload(ArquivoComposicao arq) {
        if (arq.getArquivo().getId() != null) {
            arq.setArquivo(fornecedorFacade.getArquivoFacade().recuperaDependencias(arq.getArquivo().getId()));
        }
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arq.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arq.getArquivo().getMimeType(), arq.getArquivo().getNome());
        return s;
    }

    public void habilitaCamposDocumentoSelecionado() {
        documentoSelecionadoRequerCampoNumero();
        documentoSelecionadoRequerCampoDataDeEmissao();
        documentoSelecionadoRequerCampoDataDeValidade();
    }

    public boolean documentoSelecionadoRequerCampoDataDeValidade() {
        if (documentoFornecedorSelecionado.getDocumentoHabilitacao() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerValidade() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerValidade()) {
            return true;
        }
        return false;
    }

    public boolean documentoSelecionadoRequerCampoDataDeEmissao() {
        if (documentoFornecedorSelecionado.getDocumentoHabilitacao() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerEmissao() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerEmissao()) {
            return true;
        }
        return false;
    }

    public boolean documentoSelecionadoRequerCampoNumero() {
        if (documentoFornecedorSelecionado.getDocumentoHabilitacao() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerNumero() != null
                && documentoFornecedorSelecionado.getDocumentoHabilitacao().getRequerNumero()) {

            documentoFornecedorSelecionado.setNumero(getProximoNumero(selecionado.getDocumentosFornecedor()));
            return true;
        }
        documentoFornecedorSelecionado.setNumero(null);
        return false;
    }

    public List<SelectItem> getExercicio() {
        List<SelectItem> ano = new ArrayList<>();
        ano.add(new SelectItem(null, ""));
        for (Exercicio exercicio : fornecedorFacade.getExercicioFacade().getExerciciosAtualPassados()) {
            ano.add(new SelectItem(exercicio, exercicio.toString()));
        }
        return ano;
    }

    public boolean isGerarCertificado() {
        boolean valida = true;
        for (DocumentoFornecedor documentoFornecedor : selecionado.getDocumentosFornecedor()) {
            if (documentoFornecedor.getDataDeValidade() != null &&
                    (Util.getDataHoraMinutoSegundoZerado(UtilRH.getDataOperacao()).compareTo(Util.getDataHoraMinutoSegundoZerado(documentoFornecedor.getDataDeValidade())) > 0)) {
                valida = false;
            }
        }
        return valida;
    }

    public FornecedorCertidao getFornecedorCertidao() {
        return fornecedorCertidao;
    }

    public class FornecedorCertidao extends AbstractReport{

        private Fornecedor fornecedor;

        public FornecedorCertidao(Fornecedor fornecedor) {
            this.fornecedor = fornecedor;
        }

        public void geraCertidao() throws JRException, IOException {
            fornecedor = carregarListas(fornecedor);
            if (podeGerarCertidao(fornecedor)) {
                String nomeArquivo = "RelatorioFornecedorCertidao.jasper";

                HashMap parameters = new HashMap();
                parameters.put("IMAGEM", getCaminhoImagem());
                parameters.put("MUNICIPIO", "Prefeitura Municípal de Rio Branco - AC");
                parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
                parameters.put("NOMERELATORIO", "CERTIFICADO DE REGISTRO CADASTRAL - CRC");
                parameters.put("MODULO", "Administrativo");
                parameters.put("USUARIO", fornecedorFacade.getSistemaFacade().getUsuarioCorrente().getNome());
                parameters.put("SQL", fornecedor.getId());
                parameters.put("TELEFONE", fornecedor.getPessoa().getTelefoneValido().toString());
                parameters.put("DATAVALIDADE", retornaDataDeVencimento(fornecedor));
                parameters.put("SUB", getCaminho());

                gerarRelatorio(nomeArquivo, parameters);
            }
        }

        private Fornecedor carregarListas(Fornecedor f) {
            if (f.getId() != null) {
                f = carregarListasFornecedor(f);
            }
            if (f.getPessoa() != null) {
                f.setPessoa(fornecedorFacade.getPessoaFacade().recuperar(f.getPessoa().getId()));
            }
            return f;
        }

        private Fornecedor carregarListasFornecedor(Fornecedor f) {
            return fornecedorFacade.recuperar(f.getId());
        }

        private boolean podeGerarCertidao(Fornecedor fornecedor) {
            if (!fornecedorInformado(fornecedor)) {
                return false;
            }
            if (!validaDadosFornecedorSelecionado(fornecedor)) {
                return false;
            }
            return true;
        }

        private boolean validaDadosFornecedorSelecionado(Fornecedor fornecedor) {
            if (fornecedor.getPessoa() != null && fornecedor.getPessoa().getTelefoneValido() == null) {
                FacesUtil.addOperacaoNaoPermitida("É necessário atualizar o telefone do fornecedor informado antes de gerar a certidão.");
                return false;
            }
            if (fornecedor.getPessoa() != null && fornecedor.getPessoa().getEnderecoPrincipal() == null) {
                FacesUtil.addOperacaoNaoPermitida("É necessário atualizar o endereço do fornecedor informado antes de gerar a certidão.");
                return false;
            }
            return true;
        }

        private boolean fornecedorInformado(Fornecedor f) {
            if (f == null || f.getPessoa() == null) {
                FacesUtil.addOperacaoNaoPermitida("É necessário informar o fornecedor antes de gerar a certidão.");
                return false;
            }
            return true;
        }


        public String retornaDataDeVencimento(Fornecedor f) {
            Date dataVencimento = new Date();
            if (f.getAlteradoEm() != null) {
                dataVencimento = f.getAlteradoEm();
                dataVencimento.setYear(dataVencimento.getYear() + 1);
            }
            return DataUtil.getDataFormatada(dataVencimento);
        }
    }
}
