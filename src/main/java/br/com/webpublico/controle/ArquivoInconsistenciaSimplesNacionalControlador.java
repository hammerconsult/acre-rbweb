package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.entidadesauxiliares.ItemArquivoSimplesNacionalProcessado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoInconsistenciaSimplesNacionalFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by Desenvolvimento on 19/07/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSimplesNacional", pattern = "/simples-nacional/novo/", viewId = "/faces/tributario/issqn/simples-nacional/edita.xhtml"),
    @URLMapping(id = "editarSimplesNacional", pattern = "/simples-nacional/editar/#{arquivoInconsistenciaSimplesNacionalControlador.id}/", viewId = "/faces/tributario/issqn/simples-nacional/edita.xhtml"),
    @URLMapping(id = "listarSimplesNacional", pattern = "/simples-nacional/listar/", viewId = "/faces/tributario/issqn/simples-nacional/lista.xhtml"),
    @URLMapping(id = "verSimplesNacional", pattern = "/simples-nacional/ver/#{arquivoInconsistenciaSimplesNacionalControlador.id}/", viewId = "/faces/tributario/issqn/simples-nacional/visualizar.xhtml")
})
public class ArquivoInconsistenciaSimplesNacionalControlador extends PrettyControlador<ArquivoInconsistenciaSimplesNacional> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(ArquivoInconsistenciaSimplesNacionalControlador.class);
    @EJB
    private ArquivoInconsistenciaSimplesNacionalFacade simplesNacionalFacade;

    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    private AssistenteBarraProgresso assistenteDoProcessoArquivo;
    private Future<Void> futureVoid;
    private List<Future<List<ItemArquivoSimplesNacionalProcessado>>> futureDados;
    private List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado;
    private ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao;

    public ArquivoInconsistenciaSimplesNacionalControlador() {
        super(ArquivoInconsistenciaSimplesNacional.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return simplesNacionalFacade;
    }

    public List<ItemArquivoSimplesNacionalProcessado> getItensArquivoProcessado() {
        return itensArquivoProcessado;
    }

    public void setItensArquivoProcessado(List<ItemArquivoSimplesNacionalProcessado> itensArquivoProcessado) {
        this.itensArquivoProcessado = itensArquivoProcessado;
    }

    public ArquivoExportadoSimplesNacional.TipoExportacao getTipoExportacao() {
        return tipoExportacao;
    }

    public void setTipoExportacao(ArquivoExportadoSimplesNacional.TipoExportacao tipoExportacao) {
        this.tipoExportacao = tipoExportacao;
    }

    @URLAction(mappingId = "novoSimplesNacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(simplesNacionalFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(simplesNacionalFacade.getSistemaFacade().getUsuarioCorrente());
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, selecionado.getDataCadastro());
    }

    @URLAction(mappingId = "verSimplesNacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void buscarInformacoes() {
        itensArquivoProcessado = null;
        assistenteDoProcessoArquivo = new AssistenteBarraProgresso();
        assistenteDoProcessoArquivo.setDataAtual(new Date());
        assistenteDoProcessoArquivo.setUsuarioSistema(simplesNacionalFacade.getSistemaFacade().getUsuarioCorrente());
        assistenteDoProcessoArquivo.setDescricaoProcesso("Processando dados do arquivo importado...");
        ParametroSimplesNacional parametroSimplesNacional = recuperarParametroSimplesNacionalVigente();
        selecionado = simplesNacionalFacade.recuperarArquivoComItens(selecionado);
        List<List<ItemArquivoSimplesNacional>> partition = Lists.partition(selecionado.getListaItensArquivoSimplesNacional(),
            selecionado.getListaItensArquivoSimplesNacional().size() > 5 ?  (selecionado.getListaItensArquivoSimplesNacional().size() / 5) : 1);
        assistenteDoProcessoArquivo.setTotal(selecionado.getListaItensArquivoSimplesNacional().size());
        futureDados = Lists.newArrayList();
        for (List<ItemArquivoSimplesNacional> parteItens : partition) {
            futureDados.add(simplesNacionalFacade.buscarItensArquivoProcessados(assistenteDoProcessoArquivo,
                parametroSimplesNacional, parteItens));
        }
        FacesUtil.executaJavaScript("acompanharBuscaInformacoes()");
    }

    public void acompanharBuscaInformacoes() {
        if (futureDados != null && futureDados.stream().allMatch(Future::isDone)) {
            FacesUtil.executaJavaScript("finalizarBuscaInformacoes()");
        }
    }

    public void finalizarBuscaInformacoes() throws ExecutionException, InterruptedException {
        itensArquivoProcessado = Lists.newArrayList();
        for (Future<List<ItemArquivoSimplesNacionalProcessado>> future : futureDados) {
            itensArquivoProcessado.addAll(future.get());
        }
    }

    @URLAction(mappingId = "editarSimplesNacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/simples-nacional/";
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

    public AssistenteBarraProgresso getAssistenteDoProcessoArquivo() {
        return assistenteDoProcessoArquivo;
    }

    public void setAssistenteDoProcessoArquivo(AssistenteBarraProgresso assistenteDoProcessoArquivo) {
        this.assistenteDoProcessoArquivo = assistenteDoProcessoArquivo;
    }

    public void processarArquivoImportacao() throws IOException {
        if (selecionado.isArquivoImportacaoSelecionado()) {
            ParametroSimplesNacional parametroSimplesNacional = recuperarParametroSimplesNacionalVigente();

            if (parametroSimplesNacional != null) {
                FacesUtil.executaJavaScript("iniciarArquivo()");
                FacesUtil.executaJavaScript("dialogProcessando.show()");
                selecionado = simplesNacionalFacade.salvarArquivo(selecionado);
                List<String> cnpjs = simplesNacionalFacade.lerArquivoImportado(selecionado);
                assistenteDoProcessoArquivo = new AssistenteBarraProgresso();
                assistenteDoProcessoArquivo.setDescricaoProcesso("Registrando arquivo do Simples Nacional");
                assistenteDoProcessoArquivo.setUsuarioSistema(simplesNacionalFacade.getSistemaFacade().getUsuarioCorrente());
                assistenteDoProcessoArquivo.setTotal(cnpjs.size());
                futureVoid = simplesNacionalFacade.registrarCNPJsArquivoSimplesNacional(assistenteDoProcessoArquivo, selecionado, cnpjs);
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não existe Parâmetro de Simples Nacional cadastrado para o exercício de " +
                    simplesNacionalFacade.getSistemaFacade().getExercicioCorrente().getAno());
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Selecione um arquivo do tipo txt para processar.");
        }
    }

    private ParametroSimplesNacional recuperarParametroSimplesNacionalVigente() {
        Exercicio exercicioCorrente = simplesNacionalFacade.getSistemaFacade().getExercicioCorrente();
        return simplesNacionalFacade.getParametroSimplesNacionalFacade().buscarParametroSimplesNacionalPorExercicio(exercicioCorrente);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getDetentorArquivoImportacao().getArquivosComposicao().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Selecione um arquivo de importação para realizar o processo.");
            return false;
        }

        if (assistenteDoProcessoArquivo == null) {
            FacesUtil.addOperacaoNaoPermitida("Processe o arquivo de importação para salvar a operação.");
            return false;
        }

        if (!selecionado.isArquivoDoProcessoOriginal()) {
            FacesUtil.addOperacaoNaoPermitida("O arquivo de importação foi alterado e deve ser processado novamente.");
            return false;
        }

        return true;
    }

    public void acompanharProcesso() {
        if (futureVoid != null && futureVoid.isDone()) {
            FacesUtil.executaJavaScript("terminarArquivo()");
        }
    }

    public void finalizarProcesso() {
        assistenteDoProcessoArquivo.setDescricaoProcesso("Finalizando processo...");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        FacesUtil.addOperacaoRealizada("Arquivo processado com sucesso!");
    }

    public void exportarArquivo() {
        try {
            ParametroSimplesNacional parametroSimplesNacional = recuperarParametroSimplesNacionalVigente();
            List<ItemArquivoSimplesNacional> itensArquivo = simplesNacionalFacade.buscarItensArquivoExportacaoArquivo(selecionado, tipoExportacao);
            validarExportacaoArquivo(parametroSimplesNacional, itensArquivo);
            assistenteDoProcessoArquivo = new AssistenteBarraProgresso();
            assistenteDoProcessoArquivo.setDataAtual(new Date());
            assistenteDoProcessoArquivo.setUsuarioSistema(simplesNacionalFacade.getSistemaFacade().getUsuarioCorrente());
            assistenteDoProcessoArquivo.setDescricaoProcesso("Processando dados para exportação do arquivo...");
            List<List<ItemArquivoSimplesNacional>> partition = Lists.partition(itensArquivo,
                itensArquivo.size() > 5 ?  (itensArquivo.size() / 5) : 1);
            assistenteDoProcessoArquivo.setTotal(itensArquivo.size());
            futureDados = Lists.newArrayList();
            for (List<ItemArquivoSimplesNacional> parteItens : partition) {
                futureDados.add(simplesNacionalFacade.buscarItensArquivoProcessadosExportacaoArquivo(assistenteDoProcessoArquivo,
                    parametroSimplesNacional, parteItens, tipoExportacao));
            }
            FacesUtil.executaJavaScript("acompanharDadosExportacaoArquivo()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharDadosExportacaoArquivo() {
        if (futureDados != null && futureDados.stream().allMatch(Future::isDone)) {
            FacesUtil.executaJavaScript("finalizarDadosExportacaoArquivo()");
        }
    }

    public void finalizarDadosExportacaoArquivo() throws Exception {
        List<ItemArquivoSimplesNacionalProcessado> itens = Lists.newArrayList();
        for (Future<List<ItemArquivoSimplesNacionalProcessado>> future : futureDados) {
            List<ItemArquivoSimplesNacionalProcessado> parteItens = future.get();
            if (parteItens != null && !parteItens.isEmpty()) {
                itens.addAll(parteItens);
            }
        }
        if (itens.isEmpty()) {
            FacesUtil.executaJavaScript("dialogProcessando.hide()");
            FacesUtil.addAtencao("Nenhum registro encontrado para exportação do arquivo <strong>" +
                tipoExportacao.getDescricao() + "</strong>.");
        } else {
            futureVoid = simplesNacionalFacade.gerarArquivoExportacao(assistenteDoProcessoArquivo,
                selecionado, itens, tipoExportacao);
            FacesUtil.executaJavaScript("acompanharExportacaoArquivo()");
        }
    }

    public void acompanharExportacaoArquivo() {
        if (futureVoid != null && futureVoid.isDone()) {
            FacesUtil.executaJavaScript("finalizarExportacaoArquivo()");
        }
    }

    public void finalizarExportacaoArquivo() {
        selecionado = simplesNacionalFacade.recuperar(selecionado.getId());
        FacesUtil.addOperacaoRealizada("Exportação do arquivo finalizada com sucesso. Efetue o download na lista de arquivos exportados.");
    }

    private void validarExportacaoArquivo(ParametroSimplesNacional parametroSimplesNacional,
                                          List<ItemArquivoSimplesNacional> itensArquivo) {
        if (tipoExportacao == null) {
            throw new ValidacaoException("O campo Tipo de Exportação deve ser informado.");
        }
        if (ArquivoExportadoSimplesNacional.TipoExportacao.INI.equals(tipoExportacao)) {
            if (selecionado.hasArquivoExportado(tipoExportacao)) {
                throw new ValidacaoException("O arquivo do tipo de exportação <strong>" + tipoExportacao.getDescricao() +
                    "</strong> já foi exportado.");
            }
        } else {
            if (!selecionado.hasArquivoExportado(ArquivoExportadoSimplesNacional.TipoExportacao.INI)) {
                throw new ValidacaoException("O arquivo do tipo de exportação <strong>" +
                    ArquivoExportadoSimplesNacional.TipoExportacao.INI.getDescricao() +
                    "</strong> deve ser gerado primeiro para poder gerar o arquivo do tipo de exportação <strong>"
                    + tipoExportacao.getDescricao() + "</strong>.");
            }
        }
        if (parametroSimplesNacional == null) {
            throw new ValidacaoException("Parâmetro do Simples Nacional não encontrado.");
        }
        if (itensArquivo == null || itensArquivo.isEmpty()) {
            throw new ValidacaoException("Nenhum registro encontrado para exportação do arquivo <strong>" +
                tipoExportacao.getDescricao() + "</strong>.");
        }
    }

    public enum TipoDownloadArquivo {
        CMC_COM_DEBITO,
        PESSOA_SEM_CMC,
        CNPJ_SEM_CADASTRO,
        TODOS;

        public boolean isCmcComDebito() {
            return this.equals(CMC_COM_DEBITO);
        }

        public boolean isPessoaSemCMC() {
            return this.equals(PESSOA_SEM_CMC);
        }

        public boolean isCNPJSemCadastro() {
            return this.equals(CNPJ_SEM_CADASTRO);
        }

        public boolean isTodos() {
            return this.equals(TODOS);
        }
    }

    public List<SelectItem> selectItemsTiposExportacao() {
        return Util.getListSelectItem(ArquivoExportadoSimplesNacional.TipoExportacao.values(), false);
    }

    public List<ItemArquivoSimplesNacionalProcessado> getCNPJSComDebitosVencidos() {
        return itensArquivoProcessado.stream()
            .filter(i -> i.getTemPessoa() && i.getTemDebitosVencidos()).collect(Collectors.toList());
    }

    public List<ItemArquivoSimplesNacionalProcessado> getCNPJSSemDebitosVencidos() {
        return itensArquivoProcessado.stream()
            .filter(i -> i.getTemPessoa() && !i.getTemDebitosVencidos()).collect(Collectors.toList());
    }

    public List<ItemArquivoSimplesNacionalProcessado> getPessoasSemCMC() {
        return itensArquivoProcessado.stream()
            .filter(i -> i.getTemPessoa() && !i.getTemCmc()).collect(Collectors.toList());
    }

    public List<ItemArquivoSimplesNacionalProcessado> getCNPJSSemCadastro() {
        return itensArquivoProcessado.stream()
            .filter(i -> !i.getTemPessoa()).collect(Collectors.toList());
    }
}
