package br.com.webpublico.controle.padrao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.padrao.AbstractService;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.url.QueryString;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Enumerated;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public abstract class AbstractController<T> implements Serializable {

    protected static final int REGISTROS_POR_PAGINA = 4;
    //protected static final Logger logger = Logger.getLogger("br.com.webpublico.controle.UsuarioSistemaControlador");
    protected static final Logger logger = LoggerFactory.getLogger(PrettyControlador.class);
    protected final BigDecimal CEM = new BigDecimal("100");
    public T selecionado;
    protected EntidadeMetaData metadata;
    protected Operacoes operacao;
    private Long id;
    private String sessao;
    private ConverterAutoCompleteSpring converterGenerico;
    private MoneyConverter superMoneyConverter;

    public AbstractController() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public AbstractController(Class classe) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        updateMetaData(classe);
    }

    public abstract AbstractService getService();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected void definirSessao() {
        QueryString qs = PrettyContext.getCurrentInstance().getRequestQueryString();
        sessao = qs.getParameter("sessao");
        if (sessao == null) {
            sessao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao");
        }
    }

    public void novo() {
        try {
            selecionado = (T) metadata.getEntidade().newInstance();
            definirSessao();
            carregarDaSessao();
            operacao = Operacoes.NOVO;
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error(ex.getMessage());
            FacesUtil.addError("Houve um erro inesperado!", "Não foi possível criar um(a) " + metadata.getNomeEntidade());
            logger.error("{}", ex);
        }
    }

    public void ver() {
        editar();
        operacao = Operacoes.VER;
    }

    public void editar() {
        operacao = Operacoes.EDITAR;
        recuperarObjeto();
    }

    public void recuperarObjeto() {
        sessao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao");
        selecionado = (T) Web.pegaDaSessao(metadata.getEntidade());
        if (selecionado == null || !Persistencia.getFieldId(metadata.getEntidade()).equals(id)) {
            selecionado = (T) getService().recuperar(id);
        }
    }

    private boolean validaRegrasParaSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }

        if (!validaRegrasEspecificas()) {
            return false;
        }
        return true;
    }

    public boolean validaRegrasEspecificas() {
        return true;
    }

    public void salvar() {
        try {
            if (!validaRegrasParaSalvar()) {
                return;
            }
            if (isOperacaoNovo()) {
                getService().salvarNovo((SuperEntidade) selecionado);
            }
            if (isOperacaoEditar() || isOperacaoVer()) {
                getService().salvar((SuperEntidade) selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (OptimisticLockException ole) {
            FacesUtil.addOperacaoNaoPermitida("O registro que você está alterando acabou de ser modificado por outro usuário.");
            FacesUtil.addOperacaoNaoPermitida("Verifique as informações e tente novamente.");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void cancelar() {
        Web.getEsperaRetorno();
        redireciona();
    }

    @Deprecated
    public boolean validaRegrasParaExcluir() {
        return true;
    }

    public void validarExclusaoEntidade() {

    }

    public void excluir() {
        if (validaRegrasParaExcluir()) {
            try {
                validarExclusaoEntidade();
                getService().remover((SuperEntidade) selecionado);
                redireciona();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    protected void descobrirETratarException(Exception e) {
        Throwable th = BuscaCausa.desenrolarException(e);
        if (th.getClass().equals(SQLIntegrityConstraintViolationException.class) ||
            th.getClass().equals(StaleStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada(getMensagemMotivoNaoExclusao(e.getMessage()));
        } else if (e.getClass().equals(OptimisticLockException.class) ||
            th.getClass().equals(StaleObjectStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public String getMensagemMotivoNaoExclusao(String msg) {
        try {
            String dependencia = getService().getNomeDependencia(msg);
            SuperEntidade se = ((SuperEntidade) selecionado);
            return StringUtil.primeiroCaracterMaiusculo(se.getPromonomeDemonstrativoDaEntidade()) + " " + se.getNomeDaEntidade() + " possui dependência(s) com " + dependencia + ".";

        } catch (Exception e) {
            return "Este registro possui dependência(s).";
        }
    }

    protected String getMensagemSucessoAoSalvar() {
        return "Registro salvo com sucesso.";
    }

    protected String getMensagemSucessoAoExcluir() {
        return "Registro excluído com sucesso.";
    }

    public String getCaminhoOrigem() {
        if (id == null) {
            return ((CRUD) this).getCaminhoPadrao() + "novo/";
        } else {
            return ((CRUD) this).getCaminhoPadrao() + "editar/" + id + "/";
        }
    }

    public String getCaminhoDestinoNovoCadastro() {
        return ((CRUD) this).getCaminhoPadrao() + "novo/";
    }

    public T getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(T selecionado) {
        this.selecionado = selecionado;
    }

    public void redireciona() {
        CRUD c = (CRUD) this;
        FacesUtil.navegaEmbora(selecionado, c.getCaminhoPadrao());
    }

    public EntidadeMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(EntidadeMetaData metadata) {
        this.metadata = metadata;
    }

    public void updateMetaData(Class classe) {
        metadata = new EntidadeMetaData(classe);
    }

    public boolean isSessao() {
        if (sessao != null && sessao.trim().length() > 0) {
            return true;
        }
        return false;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }

    public void carregarDaSessao() {
        Object obj = Web.pegaDaSessao(this.metadata.getEntidade());
        if (!isSessao() || obj == null) {
            return;
        }
        selecionado = (T) obj;
    }

    public ConverterAutoCompleteSpring getConverterGenerico() {
        if (converterGenerico == null) {
            converterGenerico = new ConverterAutoCompleteSpring(metadata.getEntidade(), getService());
        }

        return converterGenerico;
    }

    public List<T> completarEstaEntidade(String parte) {
        List<Field> atributos = getAtributosPesquisavelAutoCompleteGenerico();
        int quantidadeAtributosPesquisaveis = 0;
        for (Field atributo : atributos) {
            if (atributo.isAnnotationPresent(Pesquisavel.class) && !Persistencia.isAtributoDeEntidade(atributo) && !atributo.isAnnotationPresent(Enumerated.class)) {
                quantidadeAtributosPesquisaveis++;
            }
        }
        String[] atr = new String[quantidadeAtributosPesquisaveis];
        int posicao = 0;
        for (Field atributo : atributos) {
            if (atributo.isAnnotationPresent(Pesquisavel.class) && !Persistencia.isAtributoDeEntidade(atributo) && !atributo.isAnnotationPresent(Enumerated.class)) {
                atr[posicao] = atributo.getName();
                posicao++;
            }
        }

        return getService().buscarFiltrando(parte.trim(), atr);
    }

    public List<Field> getAtributosPesquisavelAutoCompleteGenerico() {
        return Persistencia.getAtributos(metadata.getEntidade());
    }

    public String getNomeEntidade() {
        return metadata.getEntidade().getSimpleName();
    }

    public boolean isOperacaoNovo() {
        return Operacoes.NOVO.equals(getOperacao());
    }

    public boolean isOperacaoEditar() {
        return Operacoes.EDITAR.equals(getOperacao());
    }

    public boolean isOperacaoVer() {
        return Operacoes.VER.equals(getOperacao());
    }

    public MoneyConverter getSuperMoneyConverter() {
        if (superMoneyConverter == null) {
            superMoneyConverter = new MoneyConverter();
        }
        return superMoneyConverter;
    }
}
