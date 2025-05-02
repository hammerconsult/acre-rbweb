package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaProcessoBloqueio", pattern = "/tributario/rbtrans/processo-bloqueio-outorga/listar/", viewId = "/faces/tributario/rbtrans/processobloqueiooutorga/lista.xhtml"),
    @URLMapping(id = "novoBloqueioOutorga", pattern = "/tributario/rbtrans/processo-bloqueio-outorga/novo/", viewId = "/faces/tributario/rbtrans/processobloqueiooutorga/edita.xhtml"),
    @URLMapping(id = "verBloqueioOutorga", pattern = "/tributario/rbtrans/processo-bloqueio-outorga/ver/#{bloqueioOutorgaControlador.id}/", viewId = "/faces/tributario/rbtrans/processobloqueiooutorga/visualizar.xhtml"),
    @URLMapping(id = "editarBloqueioOutorga", pattern = "/tributario/rbtrans/processo-bloqueio-outorga/editar/#{bloqueioOutorgaControlador.id}/", viewId = "/faces/tributario/rbtrans/processobloqueiooutorga/edita.xhtml")})
public class BloqueioOutorgaControlador extends PrettyControlador<BloqueioOutorga> implements CRUD, Serializable {

    @EJB
    private BloqueioOutorgaFacade bloqueioOutorgaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private SubvencaoProcessoFacade subvencaoProcessoFacade;
    @EJB
    private ContribuinteDebitoOutorgaFacade contribuinteDebitoOutorgaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterAtoLegal;
    private ParametroBloqueioOutorga parametroAtual;
    private DadoBloqueioOutorga dadoAtual;
    private boolean todosConfirmados;

    public BloqueioOutorgaControlador() {
        super(BloqueioOutorga.class);
    }

    public void imprimirCertidao() {
        ParametrosOutorgaRBTrans parametrosOutorgaRBTrans = parametroOutorgaFacade.getParametroOutorgaRBTransVigente();
        try {
            documentoOficialFacade.gerarDocumentoBloqueioOutorga(selecionado, parametrosOutorgaRBTrans.getTipoDoctoOficial());
        } catch (UFMException e) {
            logger.error("Erro ao gerar a certidão de bloqueio", e);
        } catch (AtributosNulosException e) {
            logger.error("Erro ao gerar a certidão de bloqueio", e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getParametros().size() == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um parâmetro de referência");
        } else if (selecionado.getDadosBloqueioOutorgasTemporaria().size() == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar os dados do bloqueio");
        } else if (selecionado.getDadosBloqueioOutorgas().size() == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor confirmar a operação de bloqueio");
        }
        ve.lancarException();
    }

    private void validarConfirmarDados() {
        ValidacaoException ve = new ValidacaoException();
        BigDecimal total = totalOutorgaParametros();
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total de outorga deve ser maior que R$ 0,00 para confirmar a operação de bloqueio");
        } else {
            for (DadoBloqueioOutorga dadoBloqueioOutorga : selecionado.getDadosBloqueioOutorgasTemporaria()) {
                dadoBloqueioOutorga.setMontanteOriginal(total);
                if (TipoCalculoRBTRans.TipoValor.VALOR.equals(dadoBloqueioOutorga.getTipoValor())) {
                    dadoBloqueioOutorga.setMontanteBloqueado(dadoBloqueioOutorga.getValor());
                } else {
                    dadoBloqueioOutorga.setMontanteBloqueado(total.multiply(dadoBloqueioOutorga.getValor()).divide(new BigDecimal("100")));
                }
            }
            BigDecimal totalDados = BigDecimal.ZERO;
            for (DadoBloqueioOutorga dadoBloqueioOutorga : selecionado.getDadosBloqueioOutorgasTemporaria()) {
                totalDados = totalDados.add(dadoBloqueioOutorga.getMontanteBloqueado());
            }
            if (total.compareTo(totalDados) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor dos dados de bloqueio resultou em um total maior que o total de outorga dos parâmetros");
                anularDados();
            }
        }
        ve.lancarException();
    }

    public void confirmarDados() {
        try {
            validarConfirmarDados();
            selecionado.getDadosBloqueioOutorgas().clear();
            todosConfirmados = true;
            selecionado.getDadosBloqueioOutorgas().addAll(selecionado.getDadosBloqueioOutorgasTemporaria());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void anularDados() {
        selecionado.getDadosBloqueioOutorgas().clear();
        for (DadoBloqueioOutorga dadoBloqueioOutorga : selecionado.getDadosBloqueioOutorgasTemporaria()) {
            dadoBloqueioOutorga.setMontanteOriginal(BigDecimal.ZERO);
            dadoBloqueioOutorga.setMontanteBloqueado(BigDecimal.ZERO);
        }
        todosConfirmados = false;
    }

    private void validarDado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getParametros().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor inserir um parâmetro antes de inserir um dado de bloqueio");
        } else if (dadoAtual.getDataBloqueio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a data do bloqueio");
        } else if (dadoAtual.getFavorecido() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o favorecido");
        } else if (dadoAtual.getValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o valor do bloqueio");
        } else if (dadoAtual.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do bloqueio deve ser maior que R$ 0,00");
        }
        ve.lancarException();
    }

    public void adicionarDado() {
        try {
            validarDado();
            selecionado.getDadosBloqueioOutorgasTemporaria().add(dadoAtual);
            todosConfirmados = false;
            dadoAtual.setBloqueioOutorga(selecionado);
            limparDado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void limparDado() {
        dadoAtual = new DadoBloqueioOutorga();
        dadoAtual.setTipoValor(TipoCalculoRBTRans.TipoValor.VALOR);
    }

    private void validarPametro() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o C.M.C da Empresa");
        } else if (selecionado.getParametros().size() != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Apenas um parâmetro pode ser inserido por processo de bloqueio");
        } else if (parametroAtual.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o exercício de refêrencia");
        } else if (!temParametrosOutorgaExercicio(parametroAtual.getExercicio())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe uma outorga para o exercício de referência escolhido");
        } else if (getParametroSubVencaoParaParametroBloqueio(parametroAtual) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foram encontrados parâmetros de outorga para o mês e tipo de passageiro escolhidos");
        } else if (!temIPO(parametroAtual.getExercicio(), parametroAtual.getMes(), parametroAtual.getTipoPassageiro())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado um cadastro de outorga para o mês, tipo de passageiro e empresa escolhidos");
        }
        ve.lancarException();
    }

    public void adicionarParametro() {
        try {
            validarPametro();
            selecionado.getParametros().add(parametroAtual);
            parametroAtual.setBloqueioOutorga(selecionado);
            ParametroOutorgaSubvencao parametroOutorgaSubvencao = getParametroSubVencaoParaParametroBloqueio(parametroAtual);
            parametroAtual.setValorPassagem(parametroOutorgaSubvencao.getValorPassagem());
            parametroAtual.setQtdPassageiros(getIPO(parametroAtual.getExercicio(), parametroAtual.getMes(), parametroOutorgaSubvencao.getTipoPassageiro()).getAlunosTransportados());
            parametroAtual.setParametroOutorgaSubvencao(parametroOutorgaSubvencao);
            limparParametro();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerParametro(ParametroBloqueioOutorga parametroBloqueioOutorga) {
        selecionado.getParametros().remove(parametroBloqueioOutorga);
        anularDados();
    }

    private boolean possuiOutroBloqueioJaFinalizadoParaOsParametros() {
        for (ParametroBloqueioOutorga parametroBloqueioOutorga : selecionado.getParametros()) {
            if (bloqueioOutorgaFacade.buscarBloqueioFinalizadoParaParametroSubVencao(selecionado.getCadastroEconomico(), parametroBloqueioOutorga.getParametroOutorgaSubvencao()) != null) {
                return true;
            }
        }
        return false;
    }

    private void validarFinalizarBloqueio() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getSituacao().equals(selecionado.getSituacao().EM_ABERTO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo não esta em aberto e não pode ser finalizado.");
        } else if (possuiOutroBloqueioJaFinalizadoParaOsParametros()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível finalizar este processo pois já existe outro processo finalizado para os parâmetros informados.");
        }
        ve.lancarException();
    }

    public void finalizarBloqueio() {
        try {
            validarFinalizarBloqueio();
            selecionado.setSituacao(BloqueioOutorga.Situacao.FINALIZADO);
            getFacede().salvar(selecionado);
            FacesUtil.addInfo("Processo finalizado com sucesso!", "");
            navegaParaVisualizar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void excluir() {
        if (selecionado.getSituacao().equals(selecionado.getSituacao().ESTORNADO) || (selecionado.getSituacao().equals(selecionado.getSituacao().FINALIZADO))) {
            FacesUtil.addError("O processo está finalizado e não pode ser excluido!", "");
        } else {
            try {
                getFacede().remover(selecionado);
                FacesUtil.addInfo("Processo excluído com sucesso!", "");
                cancelar();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível realizar a exclusão!", "");
            }
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getSituacao().equals(selecionado.getSituacao().FINALIZADO)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo não esta finalizado e não pode ser estornado.");
        } else {
            List<SubvencaoProcesso> subvencaoProcessos = subvencaoProcessoFacade.buscarProcessosParaBloqueio(selecionado);
            for (SubvencaoProcesso subvencaoProcesso : subvencaoProcessos) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Esse processo de bloqueio não pode ser estornado pois está vínculado ao processo de subvenção <b>" + subvencaoProcesso.getNumeroDoProcesso() + "</b>");
            }
        }
        ve.lancarException();
    }

    public void estornarProcesso() {
        try {
            validarEstorno();
            selecionado.setSituacao(BloqueioOutorga.Situacao.ESTORNADO);
            selecionado.setDataEstorno(new Date());
            getFacede().salvar(selecionado);
            FacesUtil.addInfo("Processo estornado com sucesso!", "");
            navegaParaVisualizar();
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide();");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limpaEstorno() {
        selecionado.setMotivoEstorno("");
        selecionado.setDataEstorno(new Date());
    }

    public void limparParametro() {
        parametroAtual = new ParametroBloqueioOutorga();
    }

    private boolean temParametrosOutorgaExercicio(Exercicio exercicio) {
        return parametroOutorgaFacade.hasParametroOutorgaNoExercicio(exercicio);
    }

    private OutorgaIPO getIPO(Exercicio exercicio, Mes mes, TipoPassageiro tipoPassageiro) {
        List<OutorgaIPO> ipos = contribuinteDebitoOutorgaFacade.buscarLancamentoOutorgaIPO(mes, exercicio, selecionado.getCadastroEconomico());
        for (OutorgaIPO outorgaIPO : ipos) {
            if (outorgaIPO.getTipoPassageiro().equals(tipoPassageiro)) {
                return outorgaIPO;
            }
        }
        return null;
    }

    private boolean temIPO(Exercicio exercicio, Mes mes, TipoPassageiro tipoPassageiro) {
        return getIPO(exercicio, mes, tipoPassageiro) != null;
    }

    private ParametrosOutorgaRBTrans getParametrosOutorgaPorExercicio(Exercicio exercicio) {
        return parametroOutorgaFacade.recuperarParametroSubvencao(exercicio);
    }

    public ParametroOutorgaSubvencao getParametroSubVencaoParaParametroBloqueio(ParametroBloqueioOutorga parametroBloqueioOutorga) {
        ParametrosOutorgaRBTrans parametrosOutorgaRBTrans = getParametrosOutorgaPorExercicio(parametroBloqueioOutorga.getExercicio());
        for (ParametroOutorgaSubvencao parametroOutorgaSubvencao : parametrosOutorgaRBTrans.getParametroOutorgaSubvencao()) {
            if (parametroOutorgaSubvencao.getMes().equals(parametroBloqueioOutorga.getMes()) &&
                parametroOutorgaSubvencao.getTipoPassageiro().equals(parametroBloqueioOutorga.getTipoPassageiro())) {
                return parametroOutorgaSubvencao;
            }
        }
        return null;
    }

    public BigDecimal totalOutorgaParametro(ParametroBloqueioOutorga parametroBloqueioOutorga) {
        return parametroBloqueioOutorga.getValorPassagem().multiply(parametroBloqueioOutorga.getQtdPassageiros());
    }

    public BigDecimal totalOutorgaParametros() {
        BigDecimal total = new BigDecimal(0L);
        for (ParametroBloqueioOutorga parametroBloqueioOutorga : selecionado.getParametros()) {
            total = total.add(totalOutorgaParametro(parametroBloqueioOutorga));
        }
        return total;
    }

    private void navegaParaVisualizar() {
        Web.navegacao(getUrlAtual(), new StringBuilder(getCaminhoPadrao()).append("ver/").append(selecionado.getId()).append("/").toString());
    }

    @URLAction(mappingId = "novoBloqueioOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioIncluiu(sistemaControlador.getUsuarioCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setLancamento(sistemaControlador.getDataOperacao());
        selecionado.setSituacao(BloqueioOutorga.Situacao.EM_ABERTO);
        limparDado();
        limparParametro();
    }

    @URLAction(mappingId = "verBloqueioOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.setDataEstorno(new Date());
    }

    @URLAction(mappingId = "editarBloqueioOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.getDadosBloqueioOutorgasTemporaria().addAll(selecionado.getDadosBloqueioOutorgas());
        limparParametro();
        limparDado();
        todosConfirmados = true;
    }

    @Override
    public void salvar() {
        if (validaRegrasParaSalvar()) {
            try {
                validarSalvar();
                if (selecionado.getCodigo() == null || selecionado.getCodigo() == 0) {
                    selecionado.setCodigo(bloqueioOutorgaFacade.ultimoCodigoMaisUm(selecionado.getExercicio()));
                }
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                if (selecionado.getId() != null) {
                    navegaParaVisualizar();
                } else {
                    redireciona();
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return bloqueioOutorgaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/rbtrans/processo-bloqueio-outorga/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrando(parte.trim(), "nome");
    }

    public ParametroBloqueioOutorga getParametroAtual() {
        return parametroAtual;
    }

    public DadoBloqueioOutorga getDadoAtual() {
        return dadoAtual;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> selectItems = Lists.newArrayList();
        for (Mes mes : Mes.values()) {
            selectItems.add(new SelectItem(mes, mes.getDescricao()));
        }
        return selectItems;
    }

    public List<SelectItem> getTiposPassageiros() {
        List<SelectItem> selectItems = Lists.newArrayList();
        for (TipoPassageiro tipoPassageiro : TipoPassageiro.values()) {
            selectItems.add(new SelectItem(tipoPassageiro, tipoPassageiro.getDescricao()));
        }
        return selectItems;
    }

    public List<SelectItem> getTiposBloqueio() {
        List<SelectItem> selectItems = Lists.newArrayList();
        for (TipoCalculoRBTRans.TipoValor valor : TipoCalculoRBTRans.TipoValor.values()) {
            if (TipoCalculoRBTRans.TipoValor.NENHUM.equals(valor)) {
                continue;
            }
            selectItems.add(new SelectItem(valor, valor.getDescricao()));
        }
        return selectItems;
    }

    public boolean isTodosConfirmados() {
        return todosConfirmados;
    }
}
