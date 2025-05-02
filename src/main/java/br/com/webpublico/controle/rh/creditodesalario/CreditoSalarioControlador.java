package br.com.webpublico.controle.rh.creditodesalario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.creditodesalario.*;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorioConferenciaCreditoSalario;
import br.com.webpublico.enums.TipoGeracaoCreditoSalario;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean(name = "creditoSalarioControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCreditoSalario", pattern = "/credito-de-salario-old/novo/", viewId = "/faces/rh/administracaodepagamento/creditosalario/edita.xhtml"),
    @URLMapping(id = "verCreditoSalario", pattern = "/credito-de-salario-old/ver/#{creditoSalarioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/creditosalario/visualizar.xhtml"),
    @URLMapping(id = "listaCreditoSalario", pattern = "/credito-de-salario-old/listar/", viewId = "/faces/rh/administracaodepagamento/creditosalario/lista.xhtml"),
    @URLMapping(id = "logCreditoSalario", pattern = "/credito-de-salario-old/acompanhamento/", viewId = "/faces/rh/administracaodepagamento/creditosalario/log.xhtml")
})
public class CreditoSalarioControlador extends PrettyControlador<CreditoSalario> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CreditoSalarioControlador.class);
    @EJB
    private CreditoSalarioFacade creditoSalarioFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private StreamedContent fileDownload;
    private DependenciasDirf dependenciasCreditoSalario;
    private ConverterGenerico converterCompetenciaFP;
    private ConverterGenerico converterFolhaDePagamento;
    private ConverterGenerico converterContaBancariaEntidade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private ConverterAutoComplete converterMatriculaFP;
    private AbstractReport abstractReport;
    private List<GrupoRecursoFP> grupos;
    private List<BeneficioPensaoAlimenticia> beneficiarios;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private List<CompetenciaFP> competencias;
    @EJB
    private SistemaFacade sistemaFacade;

    public CreditoSalarioControlador() {
        super(CreditoSalario.class);
    }

    private static void atualizarComponentesDoDialog() {
        FacesUtil.atualizarComponente("FormularioDocumentoOficial");
        FacesUtil.executaJavaScript("mostraDocumentoOficial()");
        FacesUtil.executaJavaScript("ajustaImpressaoDocumentoOficial()");
    }

    @Override
    public AbstractFacade getFacede() {
        return creditoSalarioFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/credito-de-salario-old/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoCreditoSalario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado = new CreditoSalario();
        selecionado.setDataCredito(new Date());
        selecionado.setTipoLog(DependenciasDirf.TipoLog.TODOS);
        abstractReport = AbstractReport.getAbstractReport();
        dependenciasCreditoSalario = new DependenciasDirf();
        dependenciasCreditoSalario.setParado(Boolean.TRUE);
        beneficiarios = Lists.newLinkedList();
        limparGrupo();
    }

    @Override
    @URLAction(mappingId = "verCreditoSalario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        if (selecionado.getArquivo() != null) {
            selecionado.setArquivo(creditoSalarioFacade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
        }
    }

    @URLAction(mappingId = "logCreditoSalario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            if (dependenciasCreditoSalario.getParado()) {
                dependenciasCreditoSalario.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                dependenciasCreditoSalario.setCaminhoRelatorio(abstractReport.getCaminho());
                dependenciasCreditoSalario.setCaminhoImagem(abstractReport.getCaminhoImagem());
                dependenciasCreditoSalario.iniciarProcesso();
                dependenciasCreditoSalario.setDescricaoProcesso("Geração do Arquivo de Crédito de Salário " + selecionado.getCompetenciaFP().toString());
                creditoSalarioFacade.gerarArquivo(selecionado, dependenciasCreditoSalario);
            }
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void buscarBeneficiarios() {
        beneficiarios = Lists.newLinkedList();
        if (TipoGeracaoCreditoSalario.PENSIONISTAS.equals(selecionado.getTipoGeracaoCreditoSalario()) && selecionado.getMatriculas() != null) {
            for (MatriculaFP matriculaFP : selecionado.getMatriculas()) {
                for (VinculoFP vinculoFP : buscarContratoPorMatricula(matriculaFP)) {
                    verificarAndAdicionarNaLista(pensaoAlimenticiaFacade.buscarBeneficiarioPensaoAlimenticiaVigentePorVinculo(vinculoFP, selecionado.getCompetenciaFP()));
                }
            }
        }
    }

    private void verificarAndAdicionarNaLista(List<BeneficioPensaoAlimenticia> beneficioPensaoAlimenticias) {
        for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : beneficioPensaoAlimenticias) {
            if (!beneficiarios.contains(beneficioPensaoAlimenticia)) {
                beneficiarios.add(beneficioPensaoAlimenticia);
            }
        }
    }

    public boolean isPodeRenderizar() {
        return !beneficiarios.isEmpty();
    }

    private List<VinculoFP> buscarContratoPorMatricula(MatriculaFP matriculaFP) {
        return vinculoFPFacade.buscarVinculosVigentePorMatricula(matriculaFP.getMatricula(), sistemaControlador.getDataOperacao());
    }

    public List<BeneficioPensaoAlimenticia> getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(List<BeneficioPensaoAlimenticia> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public List<GrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    //Elaborar estratégia pra colocar todos os servidores num unico relatório.
    public StreamedContent montarRelatorioConferenciaParaDownload() {
        StreamedContent s = null;
        s = creditoSalarioFacade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(selecionado.getArquivoRelatorio());
        return s;
    }

    public void redirecionarVisualiza() {
        FacesUtil.redirecionamentoInterno("/credito-de-salario-old/ver/" + selecionado.getId());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return creditoSalarioFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<MatriculaFP> completaMatriculaFP(String parte) {
        return creditoSalarioFacade.getMatriculaFPFacade().recuperaMatriculaFiltroPessoa(parte.trim());
    }

    public void limparOrgao() {
        selecionado.setHierarquiaOrganizacional(null);
        carregarListaGruposRecursosFP();
    }

    public void carregarListaGruposRecursosFP() {
        limparGrupo();
        if (selecionado.getFolhaDePagamento() != null) {
            if (selecionado.getHierarquiaOrganizacional() != null) {
                grupos.addAll(grupoRecursoFPFacade.buscarGruposRecursoFPPorFolhaPagamento(selecionado.getFolhaDePagamento().getId(), selecionado.getHierarquiaOrganizacional()));
            } else {
                grupos.addAll(grupoRecursoFPFacade.buscarGruposRecursoFPPorFolhaPagamento(selecionado.getFolhaDePagamento().getId(), null));
            }
        }
    }

    void limparGrupo() {
        grupos = Lists.newLinkedList();
        selecionado.getGrupos().clear();
    }

    public String icone(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String title(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            return "Clique para desmarcar este grupo de recurso.";
        }
        return "Clique para selecionar este grupo de recurso.";
    }

    public String iconeTodos() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            return "ui-icon-check";
        }
        return "ui-icon-none";
    }

    public String titleTodos() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            return "Clique para desmarcar todos os grupos de recurso.";
        }
        return "Clique para selecionar todos os grupos de recurso.";
    }

    public void selecionarGrupoRecursoFP(GrupoRecursoFP grupo) {
        if (selecionado.getGrupos().contains(grupo)) {
            selecionado.getGrupos().remove(grupo);
        } else {
            selecionado.getGrupos().add(grupo);
        }
    }

    public void selecionarTodosGruposRecursoFP() {
        if (selecionado.getGrupos().size() == grupos.size()) {
            selecionado.getGrupos().removeAll(grupos);
        } else {
            selecionado.getGrupos().clear();
            for (GrupoRecursoFP grupo : grupos) {
                selecionarGrupoRecursoFP(grupo);
            }
        }
    }

    public List<SelectItem> getListaTipoGeracaoCreditoSalario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGeracaoCreditoSalario tga : TipoGeracaoCreditoSalario.values()) {
            toReturn.add(new SelectItem(tga, tga.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCompetenciasFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (CompetenciaFP obj : creditoSalarioFacade.getCompetenciaFPFacade().buscarTodasCompetencias()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getFolhasDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        if (selecionado.getCompetenciaFP() == null) {
            return toReturn;
        }

        List<FolhaDePagamento> lista = creditoSalarioFacade.getFolhaPagamentoFacade().recuperaFolhaPorCompetenciaEStatus(selecionado.getCompetenciaFP());
        if (lista.isEmpty()) {
            return toReturn;

        }

        for (FolhaDePagamento obj : lista) {
            if (obj.getCalculadaEm() != null) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }

        return toReturn;
    }

    public List<SelectItem> getContasBancariasDaEntidade() {
        List<SelectItem> toReturn = new ArrayList<>();

        List<ContaBancariaEntidade> contas = creditoSalarioFacade.getContaBancariaEntidadeFacade().buscarContasBancariasParaCreditoDeSalario();
        if (contas.isEmpty()) {
            return toReturn;
        }

        for (ContaBancariaEntidade conta : contas) {
            toReturn.add(new SelectItem(conta, conta.toString()));
        }

        return toReturn;
    }

    public ConverterGenerico getConverterCompetenciaFP() {
        if (converterCompetenciaFP == null) {
            converterCompetenciaFP = new ConverterGenerico(CompetenciaFP.class, creditoSalarioFacade.getCompetenciaFPFacade());
        }
        return converterCompetenciaFP;
    }

    public ConverterGenerico getConverterFolhaDePagamento() {
        if (converterFolhaDePagamento == null) {
            converterFolhaDePagamento = new ConverterGenerico(FolhaDePagamento.class, creditoSalarioFacade.getFolhaPagamentoFacade());
        }
        return converterFolhaDePagamento;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, creditoSalarioFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public Converter getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterAutoComplete(MatriculaFP.class, creditoSalarioFacade.getMatriculaFPFacade());
        }
        return converterMatriculaFP;
    }

    public Converter getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterGenerico(ContaBancariaEntidade.class, creditoSalarioFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    private void validarEndereco(EnderecoCorreio e, HierarquiaOrganizacional ho) {
        ValidacaoException ve = new ValidacaoException();
        if (e == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Endereço cadastrado para a Pessoa Jurídica " + ho.getSubordinada().getEntidade().getPessoaJuridica());
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContaBancariaEntidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Bancária deve ser informado.");
        }

        if (selecionado.getCompetenciaFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência deve ser informado.");
        }

        if (selecionado.getFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Folha de Pagamento deve ser informado.");
        }

        if (selecionado.getDataCredito() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Crédito deve ser informado.");
        }

        if (selecionado.getTipoGeracaoCreditoSalario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Arquivo deve ser informado.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarDadosUsadosDaHierarquiaRoot(HierarquiaOrganizacional ho) {
        ValidacaoException ve = new ValidacaoException();
        if (ho.getSubordinada().getEntidade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe entidade cadastrada para a Unidade Organizacional " + ho.getSubordinada());
        } else {
            if (ho.getSubordinada().getEntidade().getPessoaJuridica() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Pessoa Jurídica cadastrada para a Entidade " + ho.getSubordinada().getEntidade());
            } else if (ho.getSubordinada().getEntidade().getPessoaJuridica().getCnpj() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe CNPJ cadastrado para a Pessoa Jurídica " + ho.getSubordinada().getEntidade().getPessoaJuridica());
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarDadosDoSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContaBancariaEntidade().getAgencia() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Agência cadastrada para a Conta Bancária selecionada.");
        } else if (selecionado.getContaBancariaEntidade().getAgencia().getBanco() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Banco cadastrado para a Agência da Conta Bancária selecionada.");
        } else {
            if (selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco() == null || selecionado.getContaBancariaEntidade().getAgencia().getBanco().getNumeroBanco().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Número do Banco cadastrado para o Banco da Conta Bancária selecionada.");
            }
            if (selecionado.getContaBancariaEntidade().getAgencia().getBanco().getDescricao() == null || selecionado.getContaBancariaEntidade().getAgencia().getBanco().getDescricao().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Nome cadastrado para o Banco da Conta Bancária selecionada.");
            }
            if (selecionado.getContaBancariaEntidade().getAgencia().getNumeroAgencia() == null || selecionado.getContaBancariaEntidade().getAgencia().getNumeroAgencia().trim().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Número do Banco cadastrado para o Banco da Conta Bancária selecionada.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private EnderecoCorreio recuperarEndereco(Pessoa pessoa) {
        return creditoSalarioFacade.getEnderecoFacade().retornaPrimeiroEnderecoCorreioValido(pessoa);
    }

    private List<ParametrosRelatorioConferenciaCreditoSalario> getRelatorio(GrupoRecursoFP grupo) {
        List<ParametrosRelatorioConferenciaCreditoSalario> retorno = new ArrayList<>();
        retorno.add(grupo.getParametrosRelatorioConferenciaCreditoSalario());
        for (ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioConferenciaCreditoSalario : retorno) {
            Collections.sort(parametrosRelatorioConferenciaCreditoSalario.getItens());
        }
        return retorno;
    }

    public String mensagemArquivoExistente() {
        String retorno = "Já existe um arquivo gerado para a conta bancária: <b>" + selecionado.getContaBancariaEntidade() + "</b> e folha: <b>" + selecionado.getFolhaDePagamento() + "</b> para: <b> " + selecionado.getTipoGeracaoCreditoSalario().getDescricao() + "</b>.";
        retorno += "<br/><br/>";
        return retorno;
    }

    public void validarParametrosParaGeracao() {
        try {
            validarCampos();
            validarDadosDoSelecionado();
            HierarquiaOrganizacional ho = creditoSalarioFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getContaBancariaEntidade().getEntidade().getUnidadeOrganizacional(), UtilRH.getDataOperacao());
            validarDadosUsadosDaHierarquiaRoot(ho);
            selecionado.setHierarquiaOrganizacional(ho);
            EnderecoCorreio ec = recuperarEndereco(selecionado.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica());
            validarEndereco(ec, selecionado.getHierarquiaOrganizacional());
            selecionado.getHierarquiaOrganizacional().getSubordinada().getEntidade().getPessoaJuridica().setEnderecoPrincipal(ec);
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        }

        try {
            //creditoSalarioFacade.getCreditoSalarioAcompanhamentoFacade().jaExisteCreditoSalarioGeradoMesmosParametros(selecionado);
            gerarCreditoDeSalario();
        } catch (RuntimeException re) {
            FacesUtil.executaJavaScript("dialogArquivoJaExistente.show()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("form-arquivo-existente");
            return;
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
            return;
        }
    }

    public void gerarCreditoDeSalario() {
        FacesUtil.redirecionamentoInterno("/credito-de-salario-old/acompanhamento/");
    }

    public void redirecionarParaNovo() {
        FacesUtil.redirecionamentoInterno("/credito-de-salario-old/novo/");
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (CreditoSalario) evento.getComponent().getAttributes().get("objetoArquivo");
        try {
            selecionado.setArquivo(creditoSalarioFacade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void selecionarCreditoSalario(ActionEvent evento) {
        selecionado = (CreditoSalario) evento.getComponent().getAttributes().get("objRelatorio");
        try {
            selecionado.setArquivoRelatorio(creditoSalarioFacade.getArquivoFacade().recuperaDependencias(selecionado.getArquivoRelatorio().getId()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public StreamedContent recuperarArquivoParaDownload() {
        List<Arquivo> arquivos = buscarArquivos(selecionado);
        StreamedContent s = createMasterFile(arquivos);
        return s;
    }

    private List<Arquivo> buscarArquivos(CreditoSalario selecionado) {
        selecionado = creditoSalarioFacade.recuperar(selecionado.getId());
        List<Arquivo> arquivos = Lists.newLinkedList();
        for (ItemCreditoSalario itemCreditoSalario : selecionado.getItensCreditoSalario()) {
            for (ItemArquivoCreditoSalario itemArquivoCreditoSalario : itemCreditoSalario.getArquivos()) {
                if (TipoArquivoCreditoSalario.REMESSA.equals(itemArquivoCreditoSalario.getTipoArquivo())) {
                    arquivos.add(itemArquivoCreditoSalario.getArquivo());
                }
            }
        }
        return arquivos;
    }


    public StreamedContent createMasterFile(List<Arquivo> arquivos) {
        try {
            File zip = File.createTempFile("creditoSalario-" + Util.dateToString(new Date()), ".zip");
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(zip);
            ZipOutputStream zout = new ZipOutputStream(fout);

            for (Arquivo m : arquivos) {

                /*arquivo = File.createTempFile("Holerite", "txt");
                fos = new FileOutputStream(arquivo);
                fos.write(selecionado.getConteudoArquivo().toString().getBytes());
                fos.close();
                InputStream stream = new FileInputStream(arquivo);*/

                m = arquivoFacade.recuperaDependencias(m.getId());
                File temp = File.createTempFile(m.getNome(), "REM");
                FileOutputStream fos = new FileOutputStream(temp);
                fos.write(m.getByteArrayDosDados());
                fos.close();
                FileInputStream fin = new FileInputStream(temp);
                zout.putNextEntry(new ZipEntry(m.getNome()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
            FileInputStream fis = new FileInputStream(zip);
            fileDownload = new DefaultStreamedContent(fis, "application/zip", "creditoSalario-" + Util.dateToString(new Date()) + ".zip");
            zout.close();
            return fileDownload;
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo ZIP do Sicap, comunique o administrador. Detalhe: " + ioe.getMessage());
        }
        return null;
    }


    public StreamedContent recuperarArquivoParaDownload(ItemArquivoCreditoSalario item) {
        StreamedContent s = null;
        /*String dataGeracao = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String codigoConvenio = selecionado.getContaBancariaEntidade().getCodigoDoConvenio();
        String numeroRemessa = StringUtil.cortaOuCompletaEsquerda(selecionado.getNumeroRemessa() + "", 6, "0");
        String nomeArquivo = "ACC." + dataGeracao + "." + codigoConvenio + "." + numeroRemessa + ".REM";*/
        //selecionado.getArquivo().setNome(item.getArquivo().getNome());
        Arquivo arquivo = arquivoFacade.recuperaDependencias(item.getArquivo().getId());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arquivo.getMimeType(), arquivo.getNome());
        return s;
    }

    public void gerarLog(String valor) throws FileNotFoundException, IOException {
        DependenciasDirf.TipoLog tipoLog = DependenciasDirf.TipoLog.valueOf(valor);
        String conteudo = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<p style='font-size : 15px;'><b><u>" + selecionado + "<u></b></p>"
            + "<p style='font-size : 15px;'><b><u>USUÁRIO RESPONSÁVEL: " + creditoSalarioFacade.getSistemaFacade().getUsuarioCorrente().getLogin() + "<u></b></p>"
            + "<p style='font-size : 10px; font-family: monospace !important; white-space: pre !important;'>"
            + dependenciasCreditoSalario.recuperarSomenteStringDoLog(tipoLog)
            + "</p>"
            + " </body>"
            + " </html>";

        Web.getSessionMap().put("documentoOficial", conteudo);
        atualizarComponentesDoDialog();
    }

    public void atualizarFinal() {
        if (dependenciasCreditoSalario != null) {
            if (dependenciasCreditoSalario.getParado()) {
                FacesUtil.atualizarComponente("Formulario");
                if (dependenciasCreditoSalario.getLogGeral().get(DependenciasDirf.TipoLog.ERRO) != null && !dependenciasCreditoSalario.getLogGeral().get(DependenciasDirf.TipoLog.ERRO).isEmpty()) {
                    FacesUtil.addError("Atenção", "Houve erros durante a geração do arquivo, vá em 'Exportar Log' para verificar as inconsistências.");
                }
            }
        }
    }

    public Integer recuperarTotalOcorrenciasDoLog(DependenciasDirf.TipoLog tipoLog) {
        List<String> resultados = dependenciasCreditoSalario.getLogGeral().get(tipoLog);
        try {
            return resultados.size();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public DependenciasDirf getDependenciasCreditoSalario() {
        return dependenciasCreditoSalario;
    }

    public void setDependenciasCreditoSalario(DependenciasDirf dependenciasCreditoSalario) {
        this.dependenciasCreditoSalario = dependenciasCreditoSalario;
    }

    @Override
    public CreditoSalario getSelecionado() {
        return selecionado;
    }

    @Override
    public void setSelecionado(CreditoSalario selecionado) {
        this.selecionado = selecionado;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (ItemCreditoSalario itemCreditoSalario : selecionado.getItensCreditoSalario()) {
                total = total.add(itemCreditoSalario.getValorLiquido());
            }

        }
        return total;
    }

    public void validarInconsistencias(CreditoSalario creditoSalario){
        ValidacaoException ve = new ValidacaoException();
        List<LogCreditoSalario> inconsistencias = creditoSalarioFacade.getInconsistencias(selecionado);
        if (inconsistencias == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado inconsistências durante a geração do arquivo.");
        }
        ve.lancarException();
    }

    public void gerarRelatorioInconsistencias() {
        try {
            validarInconsistencias(selecionado);
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/relatorio-inconsistencias-credito-salario/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("Inconsistências do Arquivo Crédito Salário");
        dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "Departamento de Recursos Humano");
        dto.adicionarParametro("NOMERELATORIO", "Inconsistências do Arquivo Crédito Salário");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("FOLHADEPAGAMENTO", selecionado.getFolhaDePagamento().toString());
        dto.adicionarParametro("COMPETENCIA", selecionado.getFolhaDePagamento().getCompetenciaFP().toString());
        dto.adicionarParametro("FILTRO", selecionado.getId());
        return dto;
    }
}
