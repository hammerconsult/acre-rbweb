package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoProprietario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author daniel
 */

@ManagedBean(name = "transferenciaProprietarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTransferenciaProprietario", pattern = "/transferencia-de-proprietarios/novo/",
        viewId = "/faces/tributario/cadastromunicipal/transferenciaproprietario/edita.xhtml"),
    @URLMapping(id = "listarTransferenciaProprietario", pattern = "/transferencia-de-proprietarios/listar/",
        viewId = "/faces/tributario/cadastromunicipal/transferenciaproprietario/lista.xhtml"),
    @URLMapping(id = "verTransferenciaProprietario", pattern = "/transferencia-de-proprietarios/ver/#{transferenciaProprietarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/transferenciaproprietario/visualizar.xhtml")
})
public class TransferenciaProprietarioControlador extends PrettyControlador<TransferenciaProprietario> implements Serializable, CRUD {

    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private TransferenciaProprietarioFacade transferenciaProprietarioFacade;
    private ConverterAutoComplete converterPessoa;
    private Historico historico;
    private PessoaTransferenciaProprietario propriedade;
    @EJB
    private CalculaITBIFacade calculaITBIFacade;
    private Future<?> future;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    public TransferenciaProprietarioControlador() {
        super(TransferenciaProprietario.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-de-proprietarios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }


    public PessoaTransferenciaProprietario getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(PessoaTransferenciaProprietario propriedade) {
        this.propriedade = propriedade;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return this.cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public List<Pessoa> completaPessoa(String parte) {
        return this.pessoaFacade.listaTodasPessoas(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaProprietarioFacade;
    }

    public void salvarTransferencia() {
        try {
            validarPenhora(selecionado.getCadastroImobiliario());
            validarEfetivacaoTransferencia();
            transferenciaProprietarioFacade
                .getBloqueioTransferenciaProprietarioFacade()
                .validarBloqueioTransferenciaProprietario(selecionado.getCadastroImobiliario());
            FacesUtil.executaJavaScript("dlgHistoricoTransferencia.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEfetivacaoTransferencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProprietarios() != null && selecionado.getProprietarios().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um proprietário.");
        } else {
            BigDecimal diferenca = getSomaProporcoes().setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(100));
            if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os Proprietários informados possuem juntos mais que 100% da Propriedade");
            } else if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os Proprietários informados possuem juntos menos de 100% da Propriedade");
            }
        }
        ve.lancarException();
    }

    public void efetivarTransferencia() {
        try {
            validarHistorico();
            assistenteBarraProgresso = new AssistenteBarraProgresso(getSistemaControlador().getUsuarioCorrente(),
                "Efetivando a Transferência de Proprietários ...", 1);
            future = transferenciaProprietarioFacade.efetivarTransferencia(assistenteBarraProgresso, selecionado, historico);
            FacesUtil.executaJavaScript("dlgHistoricoTransferencia.hide()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.executaJavaScript("openDialog(dlgAcompanhamento)");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void acompanharEfetivacao() {
        if (future.isDone() || future.isCancelled()) {
            try {
                FacesUtil.executaJavaScript("poll.stop()");
                FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
                Object object = future.get();
                if (object instanceof TransferenciaProprietario) {
                    FacesUtil.addOperacaoRealizada("Transferência realizada com sucesso.");
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + ((TransferenciaProprietario) object).getId());
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado ao efetivar a transferência de proprietário. Erro: " + object);
                }
            } catch (Exception e) {
                logger.error("Erro ao finalizar efetivação de transferência de proprietário. {}", e.getMessage());
                logger.debug("Detalhe do erro ao finalizar efetivação de transferência de proprietário.", e);
                FacesUtil.addOperacaoRealizada("Erro inesperado ao efetivar a transferência de proprietário.");
            }
        }
    }


    private void validarHistorico() {
        ValidacaoException ve = new ValidacaoException();
        if (historico.getDataRegistro() != null && historico.getDataSolicitacao() != null && (historico.getDataSolicitacao().compareTo(historico.getDataRegistro()) == 1)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Impossível continuar - A data de solicitação é maior que a data de registro.");
        }
        if (historico.getSolicitante() == null || historico.getSolicitante().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Solicitante!");
        }
        if (historico.getMotivo() == null || historico.getMotivo().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Motivo!");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTipoProprietarios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProprietario object : TipoProprietario.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private void validarNovoProprietario() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroImobiliario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um cadastro imobiliário antes de continuar");
        }
        if (propriedade.getPessoa() == null || propriedade.getPessoa().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um proprietário antes de continuar");
        } else {
            if (!selecionado.getProprietarios().isEmpty()) {
                for (PessoaTransferenciaProprietario p : selecionado.getProprietarios()) {
                    if (!p.equals(propriedade) && p.getPessoa().equals(propriedade.getPessoa())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Pessoa selecionada já informada.");
                        break;
                    }
                }
            }
        }
        if (propriedade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma proporção válida.");
        } else {
            if (propriedade.getProporcao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe uma proporção válida.");
            } else {
                BigDecimal soma = proporcaoProprietario();
                if (soma.compareTo(new BigDecimal(100)) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A proporção informada não deve ultrapassar 100%.");
                }
                if (propriedade.getProporcao() <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O percentual informado deve ser maior que zero.");
                }
            }
        }
        if (propriedade.getTipoProprietario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um tipo de proprietário.");
        }
        ve.lancarException();
    }

    private BigDecimal proporcaoProprietario() {
        BigDecimal prop = getSomaProporcoes().setScale(2, RoundingMode.HALF_UP);
        return prop.add(new BigDecimal(propriedade.getProporcao()).setScale(2, RoundingMode.HALF_UP));
    }

    public void removeProprietario(PessoaTransferenciaProprietario proprietario) {
        selecionado.getProprietarios().remove(proprietario);
    }

    public void editarProprietatio(PessoaTransferenciaProprietario proprietario) {
        propriedade = (PessoaTransferenciaProprietario) Util.clonarObjeto(proprietario);
    }

    public void adicionarProprietario() {
        try {
            validarNovoProprietario();
            propriedade.setTransferenciaProprietario(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getProprietarios(), propriedade);
            novoProprietario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBloqueioTransferenciaProprietario() {
        if (selecionado.getCadastroImobiliario() != null) {
            BloqueioTransferenciaProprietario bloqueioTransferenciaProprietario = transferenciaProprietarioFacade
                .getBloqueioTransferenciaProprietarioFacade().buscarBloqueioPorCadastroDataReferencia(
                    selecionado.getCadastroImobiliario(), transferenciaProprietarioFacade
                        .getBloqueioTransferenciaProprietarioFacade().getSistemaFacade().getDataOperacao());
            if (bloqueioTransferenciaProprietario != null) {
                throw new ValidacaoException("O cadastro imobiliário " + selecionado.getCadastroImobiliario() +
                    " possui um bloqueio de transferência de proprietário. Motivo do bloqueio: " +
                    bloqueioTransferenciaProprietario.getMotivo());
            }
        }
    }

    public void preencheProprietarios() {
        selecionado.setProprietariosAnteriores(Lists.<PessoaTransferenciaProprietarioAnterior>newArrayList());
        selecionaProprietariosVelhos(selecionado.getCadastroImobiliario());
    }

    public void mudouCadastroImobiliario() {
        try {
            transferenciaProprietarioFacade
                .getBloqueioTransferenciaProprietarioFacade()
                .validarBloqueioTransferenciaProprietario(selecionado.getCadastroImobiliario());
            preencheProprietarios();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void selecionaProprietariosVelhos(CadastroImobiliario cadastroImobiliario) {
        ValidacaoException ve = new ValidacaoException();
        cadastroImobiliario = cadastroImobiliarioFacade.recuperar(cadastroImobiliario.getId());
        for (Propriedade p : cadastroImobiliario.getPropriedadeVigente()) {
            PessoaTransferenciaProprietarioAnterior novaPropriedade = new PessoaTransferenciaProprietarioAnterior();
            novaPropriedade.setTransferenciaProprietario(selecionado);
            novaPropriedade.setPessoa(p.getPessoa());
            novaPropriedade.setProporcao(p.getProporcao());
            selecionado.getProprietariosAnteriores().add(novaPropriedade);
        }
        try {
            validarPenhora(cadastroImobiliario);
            ve.lancarException();
        } catch (ValidacaoException v) {
            FacesUtil.printAllFacesMessages(v.getMensagens());
        }
    }

    public BigDecimal getSomaProporcoes() {
        if (selecionado.getProprietarios() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (PessoaTransferenciaProprietario p : selecionado.getProprietarios()) {
            if (!p.equals(propriedade)) {
                total = total.add(new BigDecimal(p.getProporcao()));
            }
        }
        return total;
    }

    public Double getCalcularSomaProporcoes() {
        BigDecimal total = BigDecimal.ZERO;
        for (PessoaTransferenciaProprietario p : selecionado.getProprietarios()) {
            total = total.add(BigDecimal.valueOf(p.getProporcao()).setScale(2, RoundingMode.HALF_UP));
        }
        return total.doubleValue();
    }

    public boolean isImovelComPenhora() {
        if (selecionado != null && selecionado.getCadastroImobiliario() != null) {
            return cadastroImobiliarioFacade.imovelComPenhora(selecionado.getCadastroImobiliario());
        }
        return false;
    }

    private void validarPenhora(CadastroImobiliario originario) {
        ValidacaoException ve = new ValidacaoException();
        if (isImovelComPenhora()) {
            String mensagem = "O imóvel informado encontra-se penhorado.";
            Penhora penhora = cadastroImobiliarioFacade.retornaPenhoraDoImovel(originario);
            if (penhora != null && penhora.getId() != null) {
                mensagem += "Conforme, Processo nº. " + penhora.getNumeroProcesso() + ", em data de " +
                    DataUtil.getDataFormatada(penhora.getDataPenhora()) + ", pelo Autor " + penhora.getAutor().getNome();
            }
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        ve.lancarException();
    }

    public TransferenciaProprietario getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TransferenciaProprietario selecionado) {
        this.selecionado = selecionado;
    }

    @URLAction(mappingId = "verTransferenciaProprietario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoTransferenciaProprietario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        pegarObjetosDaSessao();
        if (selecionado == null) {
            super.novo();
            selecionado.setDataTransferencia(new Date());
            selecionado.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
        }
        inicializarListas();
        Web.limpaNavegacao();
    }

    private void inicializarListas() {
        if (historico == null) {
            historico = new Historico();
            historico.setDigitador(getSistemaControlador().getUsuarioCorrente());
            historico.setDataRegistro(new Date());
        }
        if (propriedade == null) {
            novoProprietario();
        }
    }

    private void novoProprietario() {
        propriedade = new PessoaTransferenciaProprietario();
    }

    public void vaiParaCadastro() {
        Web.navegacao(getUrlAtual(), "/cadastro-imobiliario/ver/" + selecionado.getCadastroImobiliario().getId() + "/");
    }

    public void colocarObjetoNaSessao() {
        Web.poeNaSessao(selecionado);
        Web.poeNaSessao(historico);
        Web.poeNaSessao(propriedade);
    }

    private void pegarObjetosDaSessao() {
        selecionado = (TransferenciaProprietario) Web.pegaDaSessao(TransferenciaProprietario.class);
        historico = (Historico) Web.pegaDaSessao(Historico.class);
        propriedade = (PessoaTransferenciaProprietario) Web.pegaDaSessao(PessoaTransferenciaProprietario.class);
    }
}
