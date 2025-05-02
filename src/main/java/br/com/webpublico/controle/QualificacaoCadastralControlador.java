package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.QualificacaoCadastral;
import br.com.webpublico.entidades.rh.esocial.QualificacaoCadastralPessoa;
import br.com.webpublico.entidades.rh.esocial.RetornoQualificacaoCadastral;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioQualificacaoCadastral;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.esocial.SituacaoQualificacaoCadastral;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.QualificacaoCadastralFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by mateus on 17/07/17.
 */

@ManagedBean(name = "qualificacaoCadastralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoQualificacaoCadastral", pattern = "/rh/e-social/qualificacao-cadastral/novo/", viewId = "/faces/rh/esocial/qualificacao-cadastral/edita.xhtml"),
    @URLMapping(id = "editarQualificacaoCadastral", pattern = "/rh/e-social/qualificacao-cadastral/editar/#{qualificacaoCadastralControlador.id}/", viewId = "/faces/rh/esocial/qualificacao-cadastral/edita.xhtml"),
    @URLMapping(id = "listarQualificacaoCadastral", pattern = "/rh/e-social/qualificacao-cadastral/listar/", viewId = "/faces/rh/esocial/qualificacao-cadastral/lista.xhtml"),
    @URLMapping(id = "verQualificacaoCadastral", pattern = "/rh/e-social/qualificacao-cadastral/ver/#{qualificacaoCadastralControlador.id}/", viewId = "/faces/rh/esocial/qualificacao-cadastral/visualizar.xhtml")
})
public class QualificacaoCadastralControlador extends PrettyControlador<QualificacaoCadastral> implements Serializable, CRUD {

    @EJB
    private QualificacaoCadastralFacade qualificacaoCadastralFacade;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<QualificacaoCadastral> futureQualificacao;
    private List<QualificacaoCadastralPessoa> pessoas;
    private QualificacaoCadastralPessoa qualificacaoCadastralPessoa;
    private boolean futureConcluida;
    private SituacaoQualificacaoCadastral processadoRejeitado;
    private ConverterAutoComplete converterHierarquiaOrganizacional;

    public QualificacaoCadastralControlador() {
        super(QualificacaoCadastral.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return qualificacaoCadastralFacade;
    }

    @Override
    @URLAction(mappingId = "novoQualificacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataEnvio(new Date());
        Entidade entidade = qualificacaoCadastralFacade.getEntidadeFacade().buscarEntidadePorUnidade(qualificacaoCadastralFacade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente(), qualificacaoCadastralFacade.getSistemaFacade().getDataOperacao());
        PessoaFisica pessoaUsuarioLogado = qualificacaoCadastralFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica();
        selecionado.setCnpjMunicipio(entidade.getPessoaJuridica().getCnpj());
        selecionado.setCpfTransmissor(pessoaUsuarioLogado.getCpf());
        inicializarAssistenteDetentor();
        pessoas = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "editarQualificacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        inicializarAssistenteDetentor();
        atribuirPessoas();
    }

    private void atribuirPessoas() {
        pessoas = selecionado.getPessoas();
    }

    public List<SelectItem> getProcessadosRejeitados() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (SituacaoQualificacaoCadastral pr : SituacaoQualificacaoCadastral.values()) {
            toReturn.add(new SelectItem(pr, pr.getDescricao()));
        }
        return toReturn;
    }

    public SituacaoQualificacaoCadastral getProcessadoRejeitado() {
        return processadoRejeitado;
    }

    public void setProcessadoRejeitado(SituacaoQualificacaoCadastral processadoRejeitado) {
        this.processadoRejeitado = processadoRejeitado;
    }

    @Override
    @URLAction(mappingId = "verQualificacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        inicializarAssistenteDetentor();
        atribuirPessoas();
    }

    public void terminarProcesso() {
        FacesUtil.executaJavaScript("dlgAcompanhamento.hide()");
        FacesUtil.addOperacaoRealizada("Operação realizada com sucesso!");
        selecionado = qualificacaoCadastralFacade.recuperar(selecionado.getId());
        String url = this.getCaminhoPadrao() + "editar/" + selecionado.getId();
        FacesUtil.redirecionamentoInterno(url);
    }

    public StreamedContent recuperarArquivoParaDownload(Arquivo arquivo) {
        StreamedContent s = null;
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

    public boolean isFutureConcluida() {
        return futureQualificacao != null && futureQualificacao.isDone();
    }

    public void setFutureConcluida(boolean futureConcluida) {
        this.futureConcluida = futureConcluida;
    }

    public Future<QualificacaoCadastral> getFutureQualificacao() {
        return futureQualificacao;
    }

    public void setFutureQualificacao(Future<QualificacaoCadastral> futureQualificacao) {
        this.futureQualificacao = futureQualificacao;
    }

    private void inicializarAssistenteDetentor() {
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, qualificacaoCadastralFacade.getSistemaFacade().getDataOperacao());
    }

    public void filtrar() {
        List<QualificacaoCadastralPessoa> retorno = Lists.newArrayList();
        if (processadoRejeitado != null) {
            for (QualificacaoCadastralPessoa qualificacaoCadastralPessoa : selecionado.getPessoas()) {
                if (processadoRejeitado.equals(qualificacaoCadastralPessoa.getProcessadoRejeitado())) {
                    retorno.add(qualificacaoCadastralPessoa);
                }
            }
        } else {
            retorno.addAll(selecionado.getPessoas());
        }
        pessoas = retorno;
    }

    public void atribuirQualificacaoCadastralPessoa(QualificacaoCadastralPessoa qualificacaoCadastralPessoa) {
        this.qualificacaoCadastralPessoa = qualificacaoCadastralPessoa;
    }

    public void cancelarQualificacaoCadastralPessoa() {
        qualificacaoCadastralPessoa = null;
    }

    public void exportar() {
        try {
            validarExportacaoArquivo();
            List<ClasseCredor> classesConfiguradas = qualificacaoCadastralFacade.buscarClassesCredoresDaConfiguracao();
            List<ClasseCredor> classesCadastradas = qualificacaoCadastralFacade.buscarClassesCredorPrestadoresDeServico();
            if (!classesConfiguradas.isEmpty() && !classesCadastradas.isEmpty() && !classesConfiguradas.containsAll(classesCadastradas)) {
                FacesUtil.atualizarComponente("formClasse");
                FacesUtil.executaJavaScript("dialogClasse.show()");
            } else {
                FacesUtil.atualizarComponente("form-acompanhamento");
                FacesUtil.executaJavaScript("dlgAcompanhamento.show()");
                exportarSemVerificarClasses();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao exportar: " + ex.getMessage());
        }
    }

    public void exportarSemVerificarClasses() {
        try {
            FacesUtil.executaJavaScript("dialogClasse.hide()");
            futureQualificacao = null;
            inicializarAssistenteBarraProgresso("Exportando Qualificações");
            selecionado = qualificacaoCadastralFacade.salvarRetornando(selecionado);
            futureQualificacao = qualificacaoCadastralFacade.buscarInformacoesParaExportar(selecionado, assistenteBarraProgresso, qualificacaoCadastralFacade.getSistemaFacade().getDataOperacao());
            FacesUtil.executaJavaScript("acompanharGeracao()");
            FacesUtil.atualizarComponente("form-acompanhamento");
            FacesUtil.executaJavaScript("dlgAcompanhamento.show()");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao exportar: " + ex.getMessage());
        }
    }

    public String linkConfiguracaoRH() {
        return "Atenção! Existem classes de pessoas cadastradas que não estão na configuração de exportação do E-Social na " + Util.linkBlack("/configuracao/rh/listar/", "Configuração RH") + ". Deseja continuar?";
    }

    private void validarExportacaoArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Entidade para continuar.");
        }
        if (selecionado.getResponsavel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Responsável para continuar.");
        }
        ve.lancarException();
    }

    private void inicializarAssistenteBarraProgresso(String descricaoProcesso) {
        assistenteBarraProgresso = new AssistenteBarraProgresso(descricaoProcesso, 1);
    }

    public void importar(FileUploadEvent event) {
        try {
            inicializarAssistenteBarraProgresso("Processando arquivo importado");
            UploadedFile file = event.getFile();
            SituacaoQualificacaoCadastral processadoRejeitado = file.getFileName().contains("PROCESSADO") ? SituacaoQualificacaoCadastral.QUALIFICADO : SituacaoQualificacaoCadastral.PENDENTE;
            assistenteBarraProgresso.setTotal(buscarQuantidadeDeLinhasNoArquivo(file.getInputstream()));
            qualificacaoCadastralFacade.limparRetornos(selecionado, processadoRejeitado);
            selecionado = qualificacaoCadastralFacade.recuperar(selecionado.getId());
            futureQualificacao = qualificacaoCadastralFacade.importarArquivo(selecionado, assistenteBarraProgresso, file, processadoRejeitado);
            FacesUtil.executaJavaScript("acompanharGeracao()");
        } catch (Exception ex) {
            logger.error("erro: ", ex);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao importar: " + ex.getMessage());
        }

    }

    private int buscarQuantidadeDeLinhasNoArquivo(InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/qualificacao-cadastral/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    public void setAssistenteDetentorArquivoComposicao(AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao) {
        this.assistenteDetentorArquivoComposicao = assistenteDetentorArquivoComposicao;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<QualificacaoCadastralPessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<QualificacaoCadastralPessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public QualificacaoCadastralPessoa getQualificacaoCadastralPessoa() {
        return qualificacaoCadastralPessoa;
    }

    public void setQualificacaoCadastralPessoa(QualificacaoCadastralPessoa qualificacaoCadastralPessoa) {
        this.qualificacaoCadastralPessoa = qualificacaoCadastralPessoa;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, qualificacaoCadastralFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterHierarquiaOrganizacional;
    }

    public void gerarRelatorio() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            abstractReport.setGeraNoDialog(true);
            String nomeArquivo = "RelatorioQualificacaoCadastral.jasper";
            parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
            parameters.put("NOMERELATORIO", "Relatório de Qualificações Cadastrais");
            parameters.put("BRASAO", abstractReport.getCaminhoImagem());
            parameters.put("USUARIO", qualificacaoCadastralFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            parameters.put("MODULO", "Recursos Humanos");
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(popularRelatorio());
            abstractReport.gerarRelatorioComDadosEmCollection(nomeArquivo, parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getLocalizedMessage());
        }
    }

    private List<RelatorioQualificacaoCadastral> popularRelatorio() {
        List<RelatorioQualificacaoCadastral> retorno = Lists.newArrayList();
        for (QualificacaoCadastralPessoa qualificacaoCadastralPessoa : pessoas) {
            RelatorioQualificacaoCadastral relatorioQualificacaoCadastral;
            if (qualificacaoCadastralPessoa.getRetornos().isEmpty()) {
                relatorioQualificacaoCadastral = new RelatorioQualificacaoCadastral();
                atribuirCamposPessoaParaRelatorio(qualificacaoCadastralPessoa, relatorioQualificacaoCadastral);
                retorno.add(relatorioQualificacaoCadastral);
            } else {
                for (RetornoQualificacaoCadastral retornoQualificacaoCadastral : qualificacaoCadastralPessoa.getRetornos()) {
                    relatorioQualificacaoCadastral = new RelatorioQualificacaoCadastral();
                    atribuirCamposPessoaParaRelatorio(qualificacaoCadastralPessoa, relatorioQualificacaoCadastral);
                    relatorioQualificacaoCadastral.setObservacao(retornoQualificacaoCadastral.getDescricaoInconsistencia() +
                        (retornoQualificacaoCadastral.getObservacao() != null && !retornoQualificacaoCadastral.getObservacao().isEmpty() ?
                            " - " + retornoQualificacaoCadastral.getObservacao() : ""));
                    retorno.add(relatorioQualificacaoCadastral);
                }
            }

        }
        return retorno;
    }

    private void atribuirCamposPessoaParaRelatorio(QualificacaoCadastralPessoa qualificacaoCadastralPessoa, RelatorioQualificacaoCadastral relatorioQualificacaoCadastral) {
        relatorioQualificacaoCadastral.setCpf(qualificacaoCadastralPessoa.getCpfComPontuacao());
        relatorioQualificacaoCadastral.setDataNascimento(qualificacaoCadastralPessoa.getDataNascimento());
        relatorioQualificacaoCadastral.setNis(qualificacaoCadastralPessoa.getNis());
        relatorioQualificacaoCadastral.setNomeFuncionario(qualificacaoCadastralPessoa.getNomeFuncionario());
        relatorioQualificacaoCadastral.setProcessadoRejeitado(qualificacaoCadastralPessoa.getProcessadoRejeitado() != null ? qualificacaoCadastralPessoa.getProcessadoRejeitado().getDescricao() : null);
    }

    public List<Entidade> completaEntidades(String parte) {
        return qualificacaoCadastralFacade.getEntidadeFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return qualificacaoCadastralFacade.getHierarquiaOrganizacionalFacade().filtraPorNivelSemView(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }
}
