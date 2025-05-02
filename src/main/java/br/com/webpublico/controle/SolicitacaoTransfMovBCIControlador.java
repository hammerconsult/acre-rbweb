package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOSimulacaoTransferenciaMovimentacoesBCI;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoTransfMovBCIFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "solicitacaoTransferenciaMovBCINovo", pattern = "/solicitacao-transferencia-mov-bci/novo/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/solicitacaoTransferencia/edita.xhtml"),
    @URLMapping(id = "solicitacaoTransferenciaMovBCIEditar", pattern = "/solicitacao-transferencia-mov-bci/editar/#{solicitacaoTransfMovBCIControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/solicitacaoTransferencia/edita.xhtml"),
    @URLMapping(id = "solicitacaoTransferenciaMovBCIVer", pattern = "/solicitacao-transferencia-mov-bci/ver/#{solicitacaoTransfMovBCIControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/solicitacaoTransferencia/visualizar.xhtml"),
    @URLMapping(id = "solicitacaoTransferenciaMovBCIListar", pattern = "/solicitacao-transferencia-mov-bci/listar/", viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/solicitacaoTransferencia/lista.xhtml")
})
public class SolicitacaoTransfMovBCIControlador extends PrettyControlador<SolicitacaoTransfMovBCI> implements CRUD {

    @EJB
    private SolicitacaoTransfMovBCIFacade solicitacaoTransfMovBCIFacade;
    private String proprietarioBciOrigem;
    private String proprietarioBciDestino;
    private ParecerTransferenciaMovBCI parecer;
    private AbstractReport abstractReport;

    private List<Situacao> situacoesSelecionadasBciOrigem;
    private List<Situacao> situacoesDisponiveisBciOrigem;
    private List<Situacao> situacoesSelecionadasBciDestino;
    private List<Situacao> situacoesDisponiveisBciDestino;
    private List<ArquivoTransferenciaMovBCI> arquivosRemovidos;
    private List<ArquivoTransferenciaMovBCI> arquivos;

    private ArquivoTransferenciaMovBCI arquivo;
    private Integer index;

    public SolicitacaoTransfMovBCIControlador() {
        super(SolicitacaoTransfMovBCI.class);
        abstractReport = AbstractReport.getAbstractReport();
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoTransfMovBCIFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-transferencia-mov-bci/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "solicitacaoTransferenciaMovBCINovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarCampos();
        arquivo = new ArquivoTransferenciaMovBCI();
        arquivosRemovidos = Lists.newArrayList();
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

    public List<Situacao> getSituacoesSelecionadasBciOrigem() {
        return situacoesSelecionadasBciOrigem;
    }

    public void setSituacoesSelecionadasBciOrigem(List<Situacao> situacoesSelecionadasBciOrigem) {
        this.situacoesSelecionadasBciOrigem = situacoesSelecionadasBciOrigem;
    }

    public List<Situacao> getSituacoesSelecionadasBciDestino() {
        return situacoesSelecionadasBciDestino;
    }

    public void setSituacoesSelecionadasBciDestino(List<Situacao> situacoesSelecionadasBciDestino) {
        this.situacoesSelecionadasBciDestino = situacoesSelecionadasBciDestino;
    }

    public ParecerTransferenciaMovBCI getParecer() {
        return parecer;
    }

    public void setParecer(ParecerTransferenciaMovBCI parecer) {
        this.parecer = parecer;
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

    public void atribuirSituacoes() {
        atribuirSituacoesDisponiveisBciOrigem();
        atribuirSituacoesDisponiveisBciDestino();
    }

    private void atribuirSituacoesDisponiveisBciOrigem() {
        if (situacoesDisponiveisBciOrigem != null) {
            situacoesSelecionadasBciOrigem = Lists.newArrayList(situacoesDisponiveisBciOrigem);
        } else {
            situacoesSelecionadasBciOrigem = Lists.newArrayList(Situacao.values());
            this.situacoesDisponiveisBciOrigem = Lists.newArrayList(Situacao.values());
        }
    }

    private void atribuirSituacoesDisponiveisBciDestino() {
        if (situacoesDisponiveisBciDestino != null) {
            situacoesSelecionadasBciDestino = Lists.newArrayList(situacoesDisponiveisBciDestino);
        } else {
            situacoesSelecionadasBciDestino = Lists.newArrayList(Situacao.values());
            this.situacoesDisponiveisBciDestino = Lists.newArrayList(Situacao.values());
        }
    }

    private void inicializarCampos() {
        selecionado.setDataSolicitacao(solicitacaoTransfMovBCIFacade.getSistemaFacade().getDataOperacao());
        selecionado.setResponsavel(solicitacaoTransfMovBCIFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoSolicitacaoTransferenciaMovBCI.ABERTA);
        situacoesSelecionadasBciOrigem = Lists.newArrayList();
        situacoesSelecionadasBciDestino = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "solicitacaoTransferenciaMovBCIEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = solicitacaoTransfMovBCIFacade.recuperar(getId());
        arquivo = new ArquivoTransferenciaMovBCI();
        if(!isSolicitacaoTransferenciaAberta()) {
            FacesUtil.addOperacaoNaoRealizada("A Solicitação não pode ser editada pois foi " + selecionado.getSituacao().getDescricao() + ".");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
        buscarEnderecoAndProprietario(true);
        buscarEnderecoAndProprietario(false);
        ordenarArquivos();
        arquivosRemovidos = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "solicitacaoTransferenciaMovBCIVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = solicitacaoTransfMovBCIFacade.recuperar(getId());

        buscarEnderecoAndProprietario(true);
        buscarEnderecoAndProprietario(false);
        if (parecer == null) {
            parecer = solicitacaoTransfMovBCIFacade.buscarParecerTransferencia(selecionado.getId());
        }
        ordenarArquivos();
    }

    @Override
    public void salvar() {
        try {
            validarSolicitacao();
            criarNovosArquivos();
            selecionado = solicitacaoTransfMovBCIFacade.salvarSolicitacao(operacao, selecionado, arquivosRemovidos);
            salvarSimulacaoTransferencia();

            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar simulação: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar simulação. Detalhes: " + e.getMessage());
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

    private void salvarSimulacaoTransferencia() throws Exception {
        VOSimulacaoTransferenciaMovimentacoesBCI simulacao = solicitacaoTransfMovBCIFacade.buscarDados(selecionado);
        salvarSimulacao(montarNomeRelatorio(), montarParametrosRelatorio(), simulacao);
    }

    private void validarSolicitacao() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getCadastroOrigem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Imobiliário de Origem deve ser informado.");
        }
        if (selecionado.getCadastroDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Imobiliário de Destino deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo deve ser informado.");
        }
        ve.lancarException();
    }

    public List<CadastroImobiliario> buscarCadastrosOrigem(String parte) {
        Boolean ativo = null;
        if (situacoesSelecionadasBciOrigem != null && situacoesSelecionadasBciOrigem.size() == 1) {
            for (Situacao situacao : situacoesSelecionadasBciOrigem) {
                ativo = Situacao.ATIVO.equals(situacao);
            }
        }
        return solicitacaoTransfMovBCIFacade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioPorSituacao(parte, ativo, "inscricaoCadastral");
    }

    public List<CadastroImobiliario> buscarCadastrosDestino(String parte) {
        Boolean ativo = null;
        if (situacoesSelecionadasBciDestino != null && situacoesSelecionadasBciDestino.size() == 1) {
            for (Situacao situacao : situacoesSelecionadasBciDestino) {
                ativo = Situacao.ATIVO.equals(situacao);
            }
        }
        return solicitacaoTransfMovBCIFacade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioPorSituacao(parte, ativo, "inscricaoCadastral");
    }

    public void buscarEnderecoAndProprietario(boolean isBciOrigem) {
        if (isBciOrigem) {
            if (selecionado.getCadastroOrigem() != null) {
                selecionado.setEnderecoCadastroOrigem(solicitacaoTransfMovBCIFacade.buscarEnderecoBCI(selecionado.getCadastroOrigem().getId()));
                proprietarioBciOrigem = solicitacaoTransfMovBCIFacade.buscarProprietarioBCI(selecionado.getCadastroOrigem().getId(),
                    solicitacaoTransfMovBCIFacade.getSistemaFacade().getDataOperacao());
            }
        } else {
            if (selecionado.getCadastroDestino() != null) {
                selecionado.setEnderecoCadastroDestino(solicitacaoTransfMovBCIFacade.buscarEnderecoBCI(selecionado.getCadastroDestino().getId()));
                proprietarioBciDestino = solicitacaoTransfMovBCIFacade.buscarProprietarioBCI(selecionado.getCadastroDestino().getId(),
                    solicitacaoTransfMovBCIFacade.getSistemaFacade().getDataOperacao());
            }
        }
    }

    public boolean canRenderizarEnderecoBci(boolean isBciOrigem) {
        if (isBciOrigem)
            return selecionado.getCadastroOrigem() != null && selecionado.getEnderecoCadastroOrigem() != null;
        return selecionado.getCadastroDestino() != null && selecionado.getEnderecoCadastroDestino() != null;
    }

    public boolean canRenderizarProprietarioBci(boolean isBciOrigem) {
        if (isBciOrigem)
            return selecionado.getCadastroOrigem() != null && proprietarioBciOrigem != null;
        return selecionado.getCadastroDestino() != null && proprietarioBciDestino != null;
    }

    public List<SelectItem> situacoesBci() {
        return Util.getListSelectItem(Situacao.values());
    }

    public void marcarTransferenciasRelacionadas() {
        selecionado.setTransferirDividasAtivas(selecionado.getTransferirDebitosDams());
        selecionado.setTransferirCalculosRevisaoIPTU(selecionado.getTransferirDebitosDams());
        selecionado.setTransferirIsencoesIPTU(selecionado.getTransferirDebitosDams());
        selecionado.setTransferirParcelamentos(selecionado.getTransferirDebitosDams());
        selecionado.setTransferirLancamentosITBI(selecionado.getTransferirDebitosDams());
    }

    public boolean canAlterarTransferencias() {
        return selecionado.getTransferirDebitosDams();
    }

    public void gerarSimulacao() {
        try {
            validarSimulacao();
            abstractReport.setGeraNoDialog(true);
            VOSimulacaoTransferenciaMovimentacoesBCI simulacao = solicitacaoTransfMovBCIFacade.buscarDados(selecionado);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(Lists.newArrayList(simulacao));
            abstractReport.gerarRelatorioComDadosEmCollection(montarNomeRelatorio(), montarParametrosRelatorio(), ds);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar simulação. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String montarNomeRelatorio() {
        return TipoArquivoTransferenciaMovBCI.SIMULACAO.getNomeArquivo() + ".jasper";
    }

    private HashMap montarParametrosRelatorio() {
        HashMap parametros = Maps.newHashMap();
        parametros.put("BRASAO", abstractReport.getCaminhoImagem());
        parametros.put("SUBREPORT_DIR", abstractReport.getCaminhoSubReport());
        parametros.put("MODULO", ModuloFAQ.TRIBUTARIO.getDescricao());
        parametros.put("SECRETARIA", "");
        parametros.put("USUARIO", solicitacaoTransfMovBCIFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
        parametros.put("DATAOPERACAO", solicitacaoTransfMovBCIFacade.getSistemaFacade().getDataOperacao());
        parametros.put("NOMERELATORIO", "Simulação de Transferência de Movimentos do Cadastro Imobiliário");
        return parametros;
    }

    private void salvarSimulacao(String arquivo, HashMap parametros, VOSimulacaoTransferenciaMovimentacoesBCI simulacao) throws Exception {
        JRBeanCollectionDataSource ds;
        ds = new JRBeanCollectionDataSource(Lists.newArrayList(simulacao));
        byte[] bytes = abstractReport.gerarBytesRelatorio(arquivo, parametros, ds);
        solicitacaoTransfMovBCIFacade.salvarSimulacao(bytes, selecionado, arquivos);
    }

    private void validarSimulacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroOrigem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Imobiliário de Origem deve ser informado.");
        }
        ve.lancarException();
    }

    public boolean isSolicitacaoTransferenciaAberta() {
        return SituacaoSolicitacaoTransferenciaMovBCI.ABERTA.equals(selecionado.getSituacao());
    }

    public boolean renderizarBotaoEditar(SolicitacaoTransfMovBCI obj) {
        selecionado = obj;
        return isSolicitacaoTransferenciaAberta();
    }

    public List<Situacao> situacoesBciOrigem() {
        List<Situacao> situacoes = Lists.newArrayList();
        if (situacoesDisponiveisBciOrigem != null) {
            for (Situacao situacao : situacoesDisponiveisBciOrigem) {
                if (!situacoesSelecionadasBciOrigem.contains(situacao)) {
                    situacoes.add(situacao);
                }
            }
        }
        return situacoes;
    }

    public List<Situacao> situacoesBciDestino() {
        List<Situacao> situacoes = Lists.newArrayList();
        if (situacoesDisponiveisBciDestino != null) {
            for (Situacao situacao : situacoesDisponiveisBciDestino) {
                if (!situacoesSelecionadasBciDestino.contains(situacao)) {
                    situacoes.add(situacao);
                }
            }
        }
        return situacoes;
    }

    public void removerSituacaoOrigem(Situacao situacao) {
        if (situacoesSelecionadasBciOrigem.size() > 1) {
            situacoesSelecionadasBciOrigem.remove(situacao);
        }
    }

    public void removerSituacaoDestino(Situacao situacao) {
        if (situacoesSelecionadasBciDestino.size() > 1) {
            situacoesSelecionadasBciDestino.remove(situacao);
        }
    }

    public void adicionarTodasSituacoesOrigem() {
        for (Situacao situacao : Lists.newArrayList(Situacao.values())) {
            adicionarSituacaoOrigem(situacao);
        }
    }

    public void adicionarTodasSituacoesDestino() {
        for (Situacao situacao : Lists.newArrayList(Situacao.values())) {
            adicionarSituacaoDestino(situacao);
        }
    }

    public void adicionarSituacaoOrigem(Situacao situacao) {
        if (!situacoesSelecionadasBciOrigem.contains(situacao)) {
            situacoesSelecionadasBciOrigem.add(situacao);
        }
    }

    public void adicionarSituacaoDestino(Situacao situacao) {
        if (!situacoesSelecionadasBciDestino.contains(situacao)) {
            situacoesSelecionadasBciDestino.add(situacao);
        }
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
