package br.com.webpublico.controle;

import br.com.webpublico.entidades.ArquivoTransferenciaMovBCI;
import br.com.webpublico.entidades.ParecerTransferenciaMovBCI;
import br.com.webpublico.entidades.SolicitacaoTransfMovBCI;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoSolicitacaoTransferenciaMovBCI;
import br.com.webpublico.enums.TipoArquivoTransferenciaMovBCI;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParecerTransferenciaMovBCIFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "parecerTransferenciaMovBCINovo", pattern = "/parecer-transferencia-mov-bci/novo/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/parecerTransferencia/edita.xhtml"),
    @URLMapping(id = "parecerTransferenciaMovBCIEditar", pattern = "/parecer-transferencia-mov-bci/editar/#{parecerTransferenciaMovBCIControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/parecerTransferencia/edita.xhtml"),
    @URLMapping(id = "parecerTransferenciaMovBCIVer", pattern = "/parecer-transferencia-mov-bci/ver/#{parecerTransferenciaMovBCIControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/parecerTransferencia/visualizar.xhtml"),
    @URLMapping(id = "parecerTransferenciaMovBCIListar", pattern = "/parecer-transferencia-mov-bci/listar/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/parecerTransferencia/lista.xhtml")
})
public class ParecerTransferenciaMovBCIControlador extends PrettyControlador<ParecerTransferenciaMovBCI> implements CRUD {

    @EJB
    private ParecerTransferenciaMovBCIFacade parecerFacade;

    private String proprietarioBciOrigem;
    private String proprietarioBciDestino;

    private List<ArquivoTransferenciaMovBCI> arquivosRemovidos;
    private List<ArquivoTransferenciaMovBCI> arquivos;

    private ArquivoTransferenciaMovBCI arquivo;
    private Integer index;

    public ParecerTransferenciaMovBCIControlador() {
        super(ParecerTransferenciaMovBCI.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parecerFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parecer-transferencia-mov-bci/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getProprietarioBciOrigem() {
        return proprietarioBciOrigem;
    }

    public void setProprietarioBciOrigem(String proprietarioBciOrigem) {
        this.proprietarioBciOrigem = proprietarioBciOrigem;
    }

    public String getProprietarioBciDestino() {
        return proprietarioBciDestino;
    }

    public void setProprietarioBciDestino(String proprietarioBciDestino) {
        this.proprietarioBciDestino = proprietarioBciDestino;
    }

    public ArquivoTransferenciaMovBCI getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoTransferenciaMovBCI arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    @URLAction(mappingId = "parecerTransferenciaMovBCINovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataParecer(new Date());
        selecionado.setUsarioParecer(parecerFacade.buscarUsuarioLogado());
        arquivo = new ArquivoTransferenciaMovBCI();
        arquivosRemovidos = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "parecerTransferenciaMovBCIEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = parecerFacade.recuperar(getId());
        arquivo = new ArquivoTransferenciaMovBCI();
        arquivosRemovidos = Lists.newArrayList();
        ordenarArquivos();
        if(!isSolicitacaoEmAberto()) {
            FacesUtil.addOperacaoNaoRealizada("O Parecer não pode ser editado pois a solicitação referente a ele foi " + selecionado.getSolicitacao().getSituacao().getDescricao() + ".");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    @Override
    @URLAction(mappingId = "parecerTransferenciaMovBCIVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = parecerFacade.recuperar(getId());
        buscarProprietario();
        ordenarArquivos();
    }

    @Override
    public void salvar() {
        try {
            validarParecer();
            criarNovosArquivos();
            selecionado = parecerFacade.salvarRetornando(selecionado);
            selecionado = parecerFacade.salvaParecer(selecionado, arquivos, arquivosRemovidos);
            lancarMensagem(getMensagemSucessoAoSalvar(), true);
            redirecionarParaVisualiza();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar Parecer. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Parecer. Detalhes: " + e.getMessage());
        }
    }

    private void criarNovosArquivos() {
        arquivos = Lists.newArrayList();
        for (ArquivoTransferenciaMovBCI arquivo : selecionado.getArquivos()) {
            if(arquivo.getId() == null) {
                ArquivoTransferenciaMovBCI novoArquivo = new ArquivoTransferenciaMovBCI();
                novoArquivo.setTipoArquivo(TipoArquivoTransferenciaMovBCI.OUTROS);
                novoArquivo.setDetentorArquivoComposicao(arquivo.getDetentorArquivoComposicao());

                arquivos.add(novoArquivo);
            }
        }
    }

    private void redirecionarParaVisualiza() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void lancarMensagem(String msg, boolean operacaoRealizada) {
        if(operacaoRealizada) {
            FacesUtil.addOperacaoRealizada(msg);
        } else {
            FacesUtil.addOperacaoNaoRealizada(msg);
        }
    }

    private void validarParecer() {
        ValidacaoException ve = new ValidacaoException();
        if(selecionado.getSolicitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Solicitação de Transferência deve ser informado.");
        }
        if(Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SolicitacaoTransfMovBCI> buscarSolicitacoes(String parte) {
        return parecerFacade.buscarSolicitacoes(parte);
    }

    public void buscarProprietario() {
        if (selecionado.getSolicitacao() != null) {
            if (selecionado.getSolicitacao().getCadastroOrigem() != null) {
                proprietarioBciOrigem = parecerFacade.buscarProprietarioBCI(selecionado.getSolicitacao().getCadastroOrigem().getId());
            }
            if (selecionado.getSolicitacao().getCadastroDestino() != null) {
                proprietarioBciDestino = parecerFacade.buscarProprietarioBCI(selecionado.getSolicitacao().getCadastroDestino().getId());
            }
        }
    }

    public boolean canRenderizarEnderecoBci(boolean isBciOrigem) {
        if (selecionado != null && selecionado.getSolicitacao() != null) {
            if (isBciOrigem)
                return selecionado.getSolicitacao().getCadastroOrigem() != null && selecionado.getSolicitacao().getEnderecoCadastroOrigem() != null;
            return selecionado.getSolicitacao().getCadastroDestino() != null && selecionado.getSolicitacao().getEnderecoCadastroDestino() != null;
        }
        return false;
    }

    public boolean canRenderizarProprietarioBci(boolean isBciOrigem) {
        if (selecionado != null && selecionado.getSolicitacao() != null) {
            if (isBciOrigem)
                return selecionado.getSolicitacao().getCadastroOrigem() != null && proprietarioBciOrigem != null;
            return selecionado.getSolicitacao().getCadastroDestino() != null && proprietarioBciDestino != null;
        }
        return false;
    }

    public void deferirTransferencia() {
        try {
            selecionado = parecerFacade.deferirTransferencia(selecionado);
            lancarMensagem("Solicitação de Transferência Deferida com sucesso.", true);
            redirecionarParaVisualiza();
        } catch (Exception e) {
            logger.error("Erro ao Deferir Solicitação de Transferência dos Movimentos da Cadastro Imobiliário: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao Deferir Solicitação de Transferência dos Movimentos da Cadastro Imobiliário. Detalhes : " +
                e.getMessage());
        }
    }

    public void indeferirTransferencia() {
        try {
            selecionado = parecerFacade.indeferirTransferencia(selecionado);
            lancarMensagem("Solicitação de Transferência Indeferida com sucesso.", true);
            redirecionarParaVisualiza();
        } catch (Exception e) {
            logger.error("Erro ao Deferir Solicitação de Transferência dos Movimentos da Cadastro Imobiliário: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao Deferir Solicitação de Transferência dos Movimentos da Cadastro Imobiliário. Detalhes : " +
                e.getMessage());
        }
    }

    public boolean renderizarBotaoEditar(ParecerTransferenciaMovBCI obj) {
        selecionado = obj;
        return isSolicitacaoEmAberto();
    }

    public boolean isSolicitacaoEmAberto() {
        return SituacaoSolicitacaoTransferenciaMovBCI.ABERTA.equals(selecionado.getSolicitacao().getSituacao());
    }

    public void adicionarArquivo() {
        try {
            validarArquivo();
            Util.adicionarObjetoEmLista(selecionado.getArquivos(), arquivo);
            arquivo = new ArquivoTransferenciaMovBCI();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if(arquivo.getDetentorArquivoComposicao().getArquivoComposicao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um arquivo para adicionar.");
        }
        ve.lancarException();
    }

    public void excluirArquivo(ArquivoTransferenciaMovBCI arquivoTransferencia) {
        selecionado.getArquivos().remove(arquivoTransferencia);
        if(arquivoTransferencia.getId() != null) {
            arquivosRemovidos.add(arquivoTransferencia);
        }
    }

    public void alterarTab(TabChangeEvent evt) {
        TabView tabView = (TabView) evt.getComponent();
        index = tabView.getChildren().indexOf(evt.getTab());
    }

    public void ordenarArquivos() {
        Collections.sort(selecionado.getArquivos(), new Comparator<ArquivoTransferenciaMovBCI>() {
            @Override
            public int compare(ArquivoTransferenciaMovBCI o1, ArquivoTransferenciaMovBCI o2) {
                return ComparisonChain.start().compare(o1.getTipoArquivo().getOrdenacao(), o2.getTipoArquivo().getOrdenacao())
                    .compare(o1.getDetentorArquivoComposicao().getArquivoComposicao().getDataUpload(),
                        o2.getDetentorArquivoComposicao().getArquivoComposicao().getDataUpload()).result();
            }
        });
    }
}
