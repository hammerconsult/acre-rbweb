package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ConsultaEntidadeController;
import br.com.webpublico.consultaentidade.FieldConsultaEntidade;
import br.com.webpublico.consultaentidade.FiltroConsultaEntidade;
import br.com.webpublico.consultaentidade.TipoCampo;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.jdbc.AuditoriaJDBC;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.url.QueryString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Enumerated;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public abstract class PrettyControlador<T> implements Serializable, CleannerViewScoped {

    protected static final int REGISTROS_POR_PAGINA = 4;
    //protected static final Logger logger = Logger.getLogger("br.com.webpublico.controle.UsuarioSistemaControlador");
    protected static final Logger logger = LoggerFactory.getLogger(PrettyControlador.class);
    protected final BigDecimal CEM = new BigDecimal("100");
    public T selecionado;
    protected EntidadeMetaData metadata;
    protected Operacoes operacao;
    private Long id;
    private String sessao;
    private ConverterAutoComplete converterGenerico;
    private MoneyConverter superMoneyConverter;
    private PercentualConverter superPercentualConverter;
    private List<FiltroConsultaEntidade> filtrosPesquisa;
    private List<FieldConsultaEntidade> fieldsPesquisa;
    private ConsultaEntidadeController.ConverterFieldConsultaEntidade converterFieldConsulta;
    private String mensagemAtualizacaoCadastralPF;

    protected RevisaoAuditoria ultimaRevisao;

    private AuditoriaJDBC auditoriaJDBC;

    public PrettyControlador() {
    }

    @PostConstruct
    public void postConstruct() {
        auditoriaJDBC = (AuditoriaJDBC) Util.getSpringBeanPeloNome("auditoriaJDBC");
    }


    public PrettyControlador(Class classe) {
        updateMetaData(classe);
    }

    public abstract AbstractFacade getFacede();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagemAtualizacaoCadastralPF() {
        return mensagemAtualizacaoCadastralPF;
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
            selecionado = (T) getFacede().recuperar(id);
        }
    }

    protected boolean validaRegrasParaSalvar() {
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
        salvar(Redirecionar.LISTAR);
    }

    public void depoisDeSalvar() {

    }

    public void salvar(Redirecionar redirecionar) {
        try {
            if (validaRegrasParaSalvar()) {
                salvarPorOperacao();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                String caminhoPadrao = ((CRUD) this).getCaminhoPadrao();
                Object key = ((CRUD) this).getUrlKeyValue();
                depoisDeSalvar();
                switch (redirecionar) {
                    case VER:
                        redireciona(caminhoPadrao + "ver/" + key);
                        break;
                    case EDITAR:
                        redireciona(caminhoPadrao + "editar/" + key);
                        break;
                    case LISTAR:
                        redireciona();
                        break;
                }
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void salvarPorOperacao() {
        if (Operacoes.NOVO.equals(operacao)) {
            getFacede().salvarNovo(selecionado);
        } else {
            getFacede().salvar(selecionado);
        }
    }

    public void validarForeinKeysComRegistro(T selecionado) {
        ValidacaoException ve = new ValidacaoException();
        List<String> tabelas = getFacede().buscarCasosDeUsoComRegistroVinculado(selecionado);
        if (tabelas != null && !tabelas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O registro possui vínculo(s) com a(s) seguinte(s) funcionalidade(s): <br/><br/>"
                + StringUtils.join(tabelas, "<br/>"));
        }
        ve.lancarException();
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
                getFacede().remover(selecionado);
                redireciona();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                try {
                    validarForeinKeysComRegistro(selecionado);
                    descobrirETratarException(e);
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                }
            }
        }
    }

    protected void descobrirETratarException(Exception e) {
        Throwable th = BuscaCausa.desenrolarException(e);
        if (th.getClass().equals(SQLIntegrityConstraintViolationException.class) ||
            th.getClass().equals(StaleStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada(getMensagemMotivoNaoExclusao(th.getMessage()));
        } else if (th.getClass().equals(OptimisticLockException.class) ||
            th.getClass().equals(StaleObjectStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
        } else {

            e.printStackTrace();
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public String getMensagemMotivoNaoExclusao(String msg) {
        try {
            String dependencia = getFacede().getNomeDependencia(msg);
            SuperEntidade se = ((SuperEntidade) selecionado);
            return StringUtil.primeiroCaracterMaiusculo(se.getPromonomeDemonstrativoDaEntidade()) + " " + se.getNomeDaEntidade() + " possui dependência(s) com " + dependencia + ".";
        } catch (Exception e) {
            logger.debug("Não foi possivel identificar o motivo da não exclusão {}", e);
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
        FacesUtil.navegaEmbora(selecionado, ((CRUD) this).getCaminhoPadrao());
        cleanScoped();
    }

    public void redireciona(String caminho) {
        FacesUtil.redirecionamentoInterno(caminho);
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

    public String getCaminhoAtual() {
        return PrettyContext.getCurrentInstance().getCurrentViewId().replace("/faces", "");
    }

    public void carregarDaSessao() {
        Object obj = Web.pegaDaSessao(metadata.getEntidade());
        if (!isSessao() || obj == null) {
            return;
        }
        selecionado = (T) obj;
    }

    public ConverterAutoComplete getConverterGenerico() {
        if (converterGenerico == null) {
            converterGenerico = new ConverterAutoComplete(metadata.getEntidade(), getFacede());
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

        return getFacede().listaFiltrando(parte.trim(), atr);
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

    public PercentualConverter getSuperPercentualConverter() {
        if (superPercentualConverter == null) {
            superPercentualConverter = new PercentualConverter();
        }
        return superPercentualConverter;
    }

    public enum Redirecionar {
        VER,
        EDITAR,
        LISTAR
    }

    public List<FiltroConsultaEntidade> getFiltrosPesquisa() {
        return filtrosPesquisa;
    }

    public void setFiltrosPesquisa(List<FiltroConsultaEntidade> filtrosPesquisa) {
        this.filtrosPesquisa = filtrosPesquisa;
    }

    public List<FieldConsultaEntidade> getFieldsPesquisa() {
        return fieldsPesquisa;
    }

    public void setFieldsPesquisa(List<FieldConsultaEntidade> fieldsPesquisa) {
        this.fieldsPesquisa = fieldsPesquisa;
    }

    public ConsultaEntidadeController.ConverterFieldConsultaEntidade getConverterFieldConsulta() {
        return converterFieldConsulta;
    }

    public void setConverterFieldConsulta(ConsultaEntidadeController.ConverterFieldConsultaEntidade converterFieldConsulta) {
        this.converterFieldConsulta = converterFieldConsulta;
    }

    public void adicionarFiltro() {
        filtrosPesquisa.add(new FiltroConsultaEntidade(new FieldConsultaEntidade()));
    }

    public void removerFiltro(FiltroConsultaEntidade filtro) {
        filtrosPesquisa.remove(filtro);
        if (filtrosPesquisa.isEmpty()) {
            adicionarFiltro();
        }
    }

    public void limparFiltros() {
        filtrosPesquisa.clear();
        adicionarFiltro();
    }

    public List<SelectItem> getPesquisaveis() {
        return Util.getListSelectItem(fieldsPesquisa, false);
    }

    public void redirecionarParaNovo() {
        FacesUtil.redirecionamentoInterno(((CRUD) this).getCaminhoPadrao() + "novo/");
    }

    public void redirecionarParaVerOrEditar(Long idSelecionado, String verOrEditar) {
        Web.setCaminhoOrigem(((CRUD) this).getCaminhoPadrao() + "listar/");
        FacesUtil.redirecionamentoInterno(((CRUD) this).getCaminhoPadrao() + verOrEditar + "/" + idSelecionado + "/");
    }

    protected void atualizarConverterFieldConsulta() {
        converterFieldConsulta = new ConsultaEntidadeController.ConverterFieldConsultaEntidade(fieldsPesquisa);
    }

    protected String getCondicaoPesquisa() {
        String retorno = "";
        for (FiltroConsultaEntidade filtro : filtrosPesquisa) {
            if (filtro.getField() != null && filtro.getField().getValor() != null && filtro.getField().getTipo() != null && filtro.getOperacao() != null) {
                if (TipoCampo.ENUM.equals(filtro.getField().getTipo()) || TipoCampo.STRING.equals(filtro.getField().getTipo())) {
                    if (Operador.LIKE.equals(filtro.getOperacao()) || Operador.NOT_LIKE.equals(filtro.getOperacao())) {
                        retorno += " and ( " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " '%" + filtro.getValor() + "%' ";
                        retorno += " or replace(" + filtro.getField().getValor() + ", '.', '') " + filtro.getOperacao().getOperador() + " '%" + filtro.getValor() + "%' )";
                    } else {
                        retorno += " and (" + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " '" + filtro.getValor() + "' ";
                        retorno += " or replace(" + filtro.getField().getValor() + ", '.', '')" + " " + filtro.getOperacao().getOperador() + " '" + filtro.getValor() + "' )";
                    }
                } else if (TipoCampo.DATE.equals(filtro.getField().getTipo())) {
                    retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " to_date('" + DataUtil.getDataFormatada((Date) filtro.getValor()) + "', 'dd/MM/yyyy') ";
                } else {
                    retorno += " and " + filtro.getField().getValor() + " " + filtro.getOperacao().getOperador() + " " + filtro.getValor();
                }
            }
        }
        return retorno;
    }

    //Sobrescrever no controlador que for utilizar
    public void pesquisarEntidade() {
    }

    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
        }
        return ultimaRevisao;
    }

    protected RevisaoAuditoria buscarUltimaAuditoria() {
        if (metadata == null || id == null) {
            return null;
        }
        try {
            Class entidade = metadata.getEntidade();
            return buscarUltimaAuditoria(entidade, id);
        } catch (Exception ex) {
            logger.error("Houve um erro inesperado!", "Não foi possível recuperar a ultima auditoria da " + metadata.getNomeEntidade() + " e id  " + id);
            logger.error("{}", ex);
        }
        return null;
    }

    protected RevisaoAuditoria buscarUltimaAuditoria(Class entidade, Long id) {
        AuditoriaJDBC.FiltroClasseAuditada filtroClasseAuditada =
            new AuditoriaJDBC.FiltroClasseAuditada();
        filtroClasseAuditada.setClasse(entidade);
        LinkedList<AuditoriaJDBC.ClasseAuditada> auditorias = auditoriaJDBC.listarAuditoria(filtroClasseAuditada, id, filtroClasseAuditada.getNivel(), null);
        if (auditorias != null) {
            if (!auditorias.isEmpty()) {
                AuditoriaJDBC.ClasseAuditada classe = auditorias.get(0);
                Object revisao = classe.getAtributos().get("Revisão");
                RevisaoAuditoria revisaoAuditoria = auditoriaJDBC.buscarRevisaoAuditoria(Long.valueOf(revisao.toString()));
                revisaoAuditoria.setClasseEntidade(entidade);
                revisaoAuditoria.setIdEntidade(id);
                revisaoAuditoria.setNomeUsuario(auditoriaJDBC.buscarNomeUsuario(revisaoAuditoria.getUsuario()));
                return revisaoAuditoria;
            }
        }
        return null;
    }

    public void verRevisao() {
        Web.getSessionMap().put("pagina-anterior-auditoria-listar", PrettyContext.getCurrentInstance().getRequestURL().toString());
        FacesUtil.redirecionamentoInterno("/auditoria/detalhar/" + ultimaRevisao.getIdEntidade() + "/" + ultimaRevisao.getClasseEntidade().getSimpleName() + "/");
    }

    public void cleanScoped() {
        selecionado = null;
        FacesContext.getCurrentInstance().getViewRoot()
            .getViewMap().clear();
    }

    public Object clone() {
        return selecionado;
    }

    public void duplicar() {
        try {
            Object clone = ((SuperEntidade) selecionado).clone();
            Web.navegacao(getUrlAtual(), ((CRUD) this).getCaminhoPadrao() + "novo/", clone);
        } catch (Exception e) {
            logger.error("Erro ao duplicar o objeto da classe {}. {}", selecionado.getClass(), e.getMessage());
            logger.debug("Detalhes do erro ao duplicar o objeto da classe {}.", selecionado.getClass(), e);
        }
    }

    public void fecharDialogAtualizacaoCadastralPF() {
        FacesUtil.executaJavaScript("dialogoAtualizacaoPF.hide();");
        limparCampoPessoaFisica();
        atualizarFormulario();
    }

    public void limparCampoPessoaFisica() {
    }

    public void abrirDialogAtualizacaoCadastralPF() {
        FacesUtil.atualizarComponente(idDialogAtualizacaoCadastralPF()+":formDialogoAtualizacaoPF");
        FacesUtil.executaJavaScript("dialogoAtualizacaoPF.show();");
    }

    public String idDialogAtualizacaoCadastralPF() {
        return "dialogAtualizacaoCadastralPF";
    }

    public Long idPfParaAtualizacaoCadastral() {
        return null;
    }

    private void atualizarFormulario() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public void navegarAteAtualizacaoCadastralPF() {
        FacesUtil.executaJavaScript("dialogoAtualizacaoPF.hide();");
        if (idPfParaAtualizacaoCadastral() != null) {
            FacesUtil.redirecionamentoExterno(FacesUtil.getRequestContextPath() + "/tributario/configuracoes/pessoa/editarpessoafisica/" + idPfParaAtualizacaoCadastral(), true);
        }
        limparCampoPessoaFisica();
        atualizarFormulario();
    }

    public void montarMensagemDialogAtualizacaoCadastroPF(PessoaFisica pf) {
        mensagemAtualizacaoCadastralPF = "O contribuinte " +
            pf.getNome() +
            " não atualizou seus dados à mais de 6 meses, para proseguir será necessário fazer a atualização cadastral.";
    }
}
