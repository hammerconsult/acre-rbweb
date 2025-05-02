package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidadesauxiliares.bi.ExportacaoArquivoBI;
import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.contabil.ExportacaoArquivoBIFacade;
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
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Renato Romanini
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-exportacao-bi-contabil", pattern = "/exportacao-bi/contabil/", viewId = "/faces/bi-exportacao/contabil.xhtml"),
    @URLMapping(id = "novo-exportacao-bi-tributario", pattern = "/exportacao-bi/tributario/", viewId = "/faces/bi-exportacao/tributario.xhtml")
})
public class ExportacaoArquivoBIControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExportacaoArquivoBIControlador.class);
    @EJB
    private ExportacaoArquivoBIFacade facade;
    private ExportacaoArquivoBI selecionado;
    private Future<List<ExportacaoArquivoBI>> future;
    private ModuloSistema moduloSistema;
    private TipoExportacaoArquivoBI[] tiposSelecionados;

    public void novo() {
        selecionado = new ExportacaoArquivoBI();
        SistemaControlador sistemaControlador = Util.getSistemaControlador();
        selecionado.setExercicioInicial(sistemaControlador.getExercicioCorrente());
        selecionado.setExercicioFinal(sistemaControlador.getExercicioCorrente());
        selecionado.setDataOperacao(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "novo-exportacao-bi-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoContabil() {
        novo();
        moduloSistema = ModuloSistema.CONTABIL;
    }

    @URLAction(mappingId = "novo-exportacao-bi-tributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoTributario() {
        novo();
        moduloSistema = ModuloSistema.TRIBUTARIO;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (TipoExportacaoArquivoBI tipoExportacaoArquivoBI : TipoExportacaoArquivoBI.values()) {
            if (moduloSistema.equals(tipoExportacaoArquivoBI.getModuloSistema())) {
                retorno.add(new SelectItem(tipoExportacaoArquivoBI, tipoExportacaoArquivoBI.getDescricao()));
            }
        }
        return retorno;
    }

    public void exportarArquivo() {
        try {
            adicionarTiposAoSelecionado();
            validarCampos();
            abrirDialogProgressBar();
            executarPoll();
            future = facade.exportarArquivo(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void adicionarTiposAoSelecionado() {
        selecionado.setTiposSelecionados(Lists.<TipoExportacaoArquivoBI>newArrayList());
        for (TipoExportacaoArquivoBI tipoSelecionado : getTiposSelecionados()) {
            selecionado.getTiposSelecionados().add(tipoSelecionado);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicioInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo exercício inicial é obrigatório.");
        }
        if (selecionado.getExercicioFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo exercício final é obrigatório.");
        }
        if (selecionado.getExercicioInicial() != null && selecionado.getExercicioFinal() != null &&
            selecionado.getExercicioFinal().getAno().compareTo(selecionado.getExercicioInicial().getAno()) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial deve ser menor ou igual ao Exercício Final.");
        }
        if (selecionado.getTiposSelecionados().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O tipo de arquivo a ser gerado é obrigatório.");
        }
        ve.lancarException();
    }

    public void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgressBar.show()");
    }

    public void executarPoll() {
        FacesUtil.executaJavaScript("poll.start()");
    }


    public void finalizarBarraProgressao() {
        if (!selecionado.getBarraProgressoItens().getCalculando()) {
            try {
                boolean deuErro = false;
                for (ExportacaoArquivoBI exportacaoArquivoBI : future.get()) {
                    if (exportacaoArquivoBI.getDeuErro()) {
                        FacesUtil.addError("Erro na geração do arquivo: " + exportacaoArquivoBI.getTipo().getDescricao(), exportacaoArquivoBI.getBarraProgressoItens().getMensagens());
                        deuErro = true;
                    }
                }
                if (!deuErro) {
                    facade.criarArquivoZipado(future.get(), selecionado, moduloSistema);
                    FacesUtil.addInfo("Arquivo gerado com sucesso.", "Utilize o botão de download para baixar e visualizar o arquivo gerado.");
                }
            } catch (Exception e) {
                FacesUtil.addError("Erro ao gerar o Arquivo.", e.getMessage());
            }
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            selecionado.getBarraProgressoItens().finaliza();
        }
    }

    public List<TipoExportacaoArquivoBI> getTiposTributarios() {
        return TipoExportacaoArquivoBI.getTiposArquivosBiPorModulo(ModuloSistema.TRIBUTARIO);
    }

    public List<TipoExportacaoArquivoBI> getTiposContabeis() {
        return TipoExportacaoArquivoBI.getTiposArquivosBiPorModulo(ModuloSistema.CONTABIL);
    }

    public boolean isTodosTributariosMarcados() {
        return getTiposSelecionados() != null && getTiposSelecionados().length == getTiposTributarios().size();
    }

    public boolean isTodosContabeisMarcados() {
        return getTiposSelecionados() != null && getTiposSelecionados().length == getTiposContabeis().size();
    }

    public void marcarTodosTiposTributarios() {
        tiposSelecionados = new TipoExportacaoArquivoBI[getTiposTributarios().size()];
        int posicao = 0;
        for (TipoExportacaoArquivoBI tipo : getTiposTributarios()) {
            tiposSelecionados[posicao] = tipo;
            posicao++;
        }
    }

    public void marcarTodosTiposContabeis() {
        tiposSelecionados = new TipoExportacaoArquivoBI[getTiposContabeis().size()];
        int posicao = 0;
        for (TipoExportacaoArquivoBI tipo : getTiposContabeis()) {
            tiposSelecionados[posicao] = tipo;
            posicao++;
        }
    }

    public void desmarcarTodosTipos() {
        tiposSelecionados = new TipoExportacaoArquivoBI[0];
    }

    public ExportacaoArquivoBI getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ExportacaoArquivoBI selecionado) {
        this.selecionado = selecionado;
    }

    public TipoExportacaoArquivoBI[] getTiposSelecionados() {
        return tiposSelecionados;
    }

    public void setTiposSelecionados(TipoExportacaoArquivoBI[] tiposSelecionados) {
        this.tiposSelecionados = tiposSelecionados;
    }
}
