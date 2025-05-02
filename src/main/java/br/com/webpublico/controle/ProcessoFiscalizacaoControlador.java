/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean(name = "processoFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoFiscalizacao", pattern = "/processo-de-fiscalizacao/novo/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/edita.xhtml"),
    @URLMapping(id = "editarProcessoFiscalizacao", pattern = "/processo-de-fiscalizacao/editar/#{processoFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/edita.xhtml"),
    @URLMapping(id = "encerraProcessoFiscalizacao", pattern = "/processo-de-fiscalizacao/encerramento/#{processoFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/visualizar.xhtml"),
    @URLMapping(id = "listarProcessoFiscalizacao", pattern = "/processo-de-fiscalizacao/listar/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/lista.xhtml"),
    @URLMapping(id = "listarProcessoFiscalizacaoCancelamento", pattern = "/processo-de-fiscalizacao/listar-cancelamento/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/listaCancelamento.xhtml"),
    @URLMapping(id = "verProcessoFiscalizacao", pattern = "/processo-de-fiscalizacao/ver/#{processoFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/visualizar.xhtml"),
    @URLMapping(id = "verProcessoFiscalizacaCancelamento", pattern = "/processo-de-fiscalizacao/ver-cancelamento/#{processoFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/processo/visualizarCancelamento.xhtml")
})
public class ProcessoFiscalizacaoControlador extends PrettyControlador<ProcessoFiscalizacao> implements Serializable, CRUD {

    private RevisaoAuditoria ultimaRevisaoLocalOcorrencia;

    @EJB
    private ProcessoFiscalizacaoFacade processoFiscalizacaoFacade;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterAutoComplete converterInfracaoFiscalizacao;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterUsuarioSistema;
    private ConverterAutoComplete converterSecretariaFiscalizacao;
    private ConverterAutoComplete converterTipoDoctoOficial;
    private ConverterAutoComplete converterQuadra;
    private ConverterAutoComplete converterDenuncia;
    private ConverterAutoComplete converterLote;
    private ConverterAutoComplete converterEndereco;
    private SituacaoProcessoFiscal situacaoProcessoFiscal;
    private Integer tabIndex;
    private TipoDoctoOficial tipoDoctoOficial;
    private TipoDoctoOficial tipoDoctoOficialAutoInfracao;
    private TipoDoctoOficial tipoDoctoOficialParecer;
    private Integer quantidadeInfracao;
    private Integer quantidadeInfracaoTermo;
    private RecursoFiscalizacao recursoFiscalizacao;
    private Lote lotePesquisa;
    private Quadra quadraPesquisa;
    private Bairro bairroPesquisa;
    private Endereco enderecoPesquisa;
    private List<CadastroImobiliario> listaDeImobilariosDaPesquisa;
    private UnidadeOrganizacional unidade;
    private Pessoa pessoaFiltroCadastroEconomico;
    private List<CadastroEconomico> listaDeEconomicoDaPesquisa;
    private List<ProcessoFiscalizacao> listaReincidencias;
    private Boolean existeReincidencia;
    private TipoDoctoOficial tipoDoctoOficialTermoGeral;
    private TermoGeralFiscalizacao termoGeralFiscalizacao;
    private SituacaoProcessoFiscal ultimaSituacao;
    private List<EnderecoCorreio> enderecos;
    private List<Telefone> telefones;
    private List<SociedadeCadastroEconomico> socios;
    private List<Propriedade> propriedadesBCI;
    private List<PropriedadeRural> propriedadesBCR;
    private Lote lote;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;
    @EJB
    private EncerramentoProcessoFiscalizacaoFacade encerramentoAutoInfracaoFiscalizacaoFacade;
    private boolean encerramento;
    private ItemProcessoFiscalizacao novoItemProcessoFiscalizacao;
    @EJB
    private TarefaAuditorFiscalFacade tarefaAuditorFiscalFacade;
    private ConverterAutoComplete converterTarefa;
    @EJB
    private InfracaoFiscalizacaoSecretariaFacade infracaoFiscalizacaoSecretariaFacade;
    private ConverterAutoComplete converterInfracao;
    @EJB
    private PenalidadeFiscalizacaoSecretariaFacade penalidadeFiscalizacaoSecretariaFacade;
    private ConverterAutoComplete converterPenalidade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private MoedaFacade moedaFacade;
    private RecursoFiscalizacao recursoFiscalizacaoEmissaoParecer;
    @EJB
    private DAMFacade damFacade;
    private CancelamentoProcessoFiscal cancelamentoProcessoFiscal;

    public ProcessoFiscalizacaoControlador() {
        super(ProcessoFiscalizacaoControlador.class);
        metadata = new EntidadeMetaData(ProcessoFiscalizacao.class);
        inicializarNovoItemProcessoFiscalizacao();
    }

    public Calendar calculaDataNovoPrazo(Date dataAnterior, Integer numeroDias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataAnterior);
        if (numeroDias != null) {
            calendar.add(Calendar.DAY_OF_MONTH, numeroDias);
        }
        return calendar;
    }

    public Lote getLote() {
        return lote;
    }

    public List<PropriedadeRural> getPropriedadesBCR() {
        return propriedadesBCR;
    }

    public void setPropriedadesBCR(List<PropriedadeRural> propriedadesBCR) {
        this.propriedadesBCR = propriedadesBCR;
    }

    public List<Propriedade> getPropriedadesBCI() {
        return propriedadesBCI;
    }

    public void setPropriedadesBCI(List<Propriedade> propriedadesBCI) {
        this.propriedadesBCI = propriedadesBCI;
    }

    public List<SociedadeCadastroEconomico> getSocios() {
        return socios;
    }

    public void setSocios(List<SociedadeCadastroEconomico> socios) {
        this.socios = socios;
    }

    public Integer getQuantidadeInfracao() {
        return quantidadeInfracao;
    }

    public void setQuantidadeInfracao(Integer quantidadeInfracao) {
        this.quantidadeInfracao = quantidadeInfracao;
    }

    public ProcessoFiscalizacao getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ProcessoFiscalizacao selecionado) {
        this.selecionado = selecionado;
    }

    public SituacaoProcessoFiscal getSituacaoProcessoFiscal() {
        return situacaoProcessoFiscal;
    }

    public void setSituacaoProcessoFiscal(SituacaoProcessoFiscal situacaoProcessoFiscal) {
        this.situacaoProcessoFiscal = situacaoProcessoFiscal;
    }

    public Integer getQuantidadeInfracaoTermo() {
        return quantidadeInfracaoTermo;
    }

    public void setQuantidadeInfracaoTermo(Integer quantidadeInfracaoTermo) {
        this.quantidadeInfracaoTermo = quantidadeInfracaoTermo;
    }

    public Boolean getExisteReincidencia() {
        return existeReincidencia;
    }

    public void setExisteReincidencia(Boolean existeReincidencia) {
        this.existeReincidencia = existeReincidencia;
    }

    public ItemProcessoFiscalizacao getNovoItemProcessoFiscalizacao() {
        return novoItemProcessoFiscalizacao;
    }

    public void setNovoItemProcessoFiscalizacao(ItemProcessoFiscalizacao novoItemProcessoFiscalizacao) {
        this.novoItemProcessoFiscalizacao = novoItemProcessoFiscalizacao;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (selecionado.getId() == null) {
                    selecionado.setCodigo(codigoPorSecretaria());
                }
                selecionado = processoFiscalizacaoFacade.salva(selecionado);
                FacesUtil.addInfo("Operação realizada com sucesso", "O registro foi salvo");
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
            } catch (Exception e) {
                FacesUtil.addError("Erro ao Salvar", e.getMessage());
            }
        }
    }

    public RecursoFiscalizacao retornaUltimoRecurso() {
        RecursoFiscalizacao retorno = new RecursoFiscalizacao();
        if (!selecionado.getRecursoFiscalizacaos().isEmpty()) {
            int ultimoRecurso = selecionado.getRecursoFiscalizacaos().size() - 1;
            retorno = selecionado.getRecursoFiscalizacaos().get(ultimoRecurso);
        }
        return retorno;
    }


    public Boolean validaCampos() {
        boolean retorno = true;
        if (!Util.validaCampos(selecionado)) {
            retorno = false;
        }
        if (selecionado.getSecretariaFiscalizacao() == null || selecionado.getSecretariaFiscalizacao().getId() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Informe a Secretaria.");
        }
        if (selecionado.getTipoLevantamentoFiscalizacao() == null || selecionado.getTipoLevantamentoFiscalizacao().getDescricao().isEmpty()) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Informe o Tipo do Levantamento.");
        } else if (selecionado.getTipoLevantamentoFiscalizacao().equals(TipoLevantamentoFiscalizacao.DENUNCIA)) {
            if (selecionado.getDenuncia() == null) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("Informe o Número da Denúncia.");
            }
        }
        if (selecionado.getAutoInfracaoFiscalizacao().getDataCiencia() != null && Util.getDataHoraMinutoSegundoZerado(selecionado.getAutoInfracaoFiscalizacao().getDataCiencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) > 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A data da ciência do Auto de Infração não pode ser superior a data atual.");
        }
        if (selecionado.getTermoAdvertencia().getDataCiencia() != null && selecionado.getTermoAdvertencia().getDataCiencia().compareTo(new Date()) > 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("A data da ciência do Termo de Advertência não pode ser superior a data atual.");
        }
        if (selecionado.getTipoCadastroFiscalizacao() != null) {
            if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.PESSOA) && selecionado.getPessoa() == null) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("Informe uma pessoa.");
            }
            if (!selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.PESSOA) && selecionado.getCadastro() == null) {
                retorno = false;
                FacesUtil.addCampoObrigatorio("Informe um cadastro.");
            }
        }
        if (selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro() == null ||
            selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro().getId() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Informe o bairro do local da ocorrência.");
        }
        if (selecionado.getLocalOcorrencia().getLogradouroBairro().getLogradouro() == null ||
            selecionado.getLocalOcorrencia().getLogradouroBairro().getLogradouro().getId() == null) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("Informe o logradouro do local da ocorrência.");
        }

        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-de-fiscalizacao/";
    }


    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    @URLAction(mappingId = "novoProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (isSessao()) {
            pegaDaSessao();
        } else {
            inicializaAtributos();
        }
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, processoFiscalizacaoFacade.getSistemaFacade().getDataOperacao());
    }

    private void pegaDaSessao() {
        try {
            Web.pegaTodosFieldsNaSessao(this);
            pegaPessoaDaSessao();
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    private void pegaPessoaDaSessao() {
        Pessoa pessoaDaSessao = (Pessoa) Web.pegaDaSessao(PessoaFisica.class);
        if (pessoaDaSessao == null) {
            pessoaDaSessao = (Pessoa) Web.pegaDaSessao(PessoaJuridica.class);
        }
        if (pessoaDaSessao != null) {
            selecionado.setPessoa(pessoaDaSessao);
            selecionado.setCadastro(null);
        }
    }


    public void poeNaSessao() {
        try {
            Web.poeTodosFieldsNaSessao(this);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void inicializaAtributos() {
        this.situacaoProcessoFiscal = new SituacaoProcessoFiscal();
        selecionado.setSituacoesProcessoFiscalizacao(new ArrayList<SituacaoProcessoFiscal>());
        selecionado.setRecursoFiscalizacaos(new ArrayList<RecursoFiscalizacao>());
        this.tipoDoctoOficial = null;
        this.tipoDoctoOficialAutoInfracao = null;
        this.tipoDoctoOficialParecer = null;
        selecionado.setCodigo("Gerado automaticamente");
        selecionado.setSecretariaFiscalizacao(retornaSecretariaDoUsuarioLogado());
        this.recursoFiscalizacao = retornaUltimoRecurso();
        this.tabIndex = 0;
        quantidadeInfracao = null;
        this.criaSituacaoDigitadoNoProcesso();
        this.existeReincidencia = Boolean.FALSE;
        this.tipoDoctoOficialTermoGeral = null;
        this.termoGeralFiscalizacao = null;
        selecionado.setTermoAdvertencia(new TermoAdvertencia());
        selecionado.setAutoInfracaoFiscalizacao(new AutoInfracaoFiscalizacao());
        selecionado.setTermoGeralFiscalizacao(new ArrayList<TermoGeralFiscalizacao>());
        selecionado.setLocalOcorrencia(new LocalOcorrencia());
    }

    public List<ProcessoFiscalizacao> getLista() {
        recuperaUnidadeDoUsuarioCorrente();
        return this.processoFiscalizacaoFacade.listaProcessos(unidade);
    }

    private void recuperaUnidadeDoUsuarioCorrente() {
        try {
            this.unidade = getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente();
        } catch (Exception e) {
        }
    }

    public boolean getEncerramento() {
        return encerramento;
    }

    @Override
    @URLAction(mappingId = "verProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (selecionado.getLocalOcorrencia() != null) {
                ultimaRevisaoLocalOcorrencia = buscarUltimaAuditoria(LocalOcorrencia.class, selecionado.getLocalOcorrencia().getId());
                if (ultimaRevisaoLocalOcorrencia != null && ultimaRevisao != null &&
                    ultimaRevisaoLocalOcorrencia.getDataHora().after(ultimaRevisao.getDataHora())) {
                    ultimaRevisao = ultimaRevisaoLocalOcorrencia;
                } else {
                    ultimaRevisaoLocalOcorrencia = null;
                }
            }
        }
        return ultimaRevisao;
    }

    public void verRevisao() {
        if (ultimaRevisaoLocalOcorrencia == null) {
            super.verRevisao();
        } else {
            Web.getSessionMap().put("pagina-anterior-auditoria-listar", PrettyContext.getCurrentInstance().getRequestURL().toString());
            FacesUtil.redirecionamentoInterno("/auditoria/ver/" + ultimaRevisaoLocalOcorrencia.getId() + "/" + selecionado.getLocalOcorrencia().getId() + "/" + LocalOcorrencia.class.getSimpleName() + "/");
        }
    }

    @URLAction(mappingId = "verProcessoFiscalizacaCancelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCancelamento() {
        super.ver();
    }

    @URLAction(mappingId = "encerraProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void encerrar() {
        super.editar();
        encerramento = true;
    }

    @URLAction(mappingId = "editarProcessoFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        tabIndex = 0;
        super.editar();
        this.situacaoProcessoFiscal = new SituacaoProcessoFiscal();
        this.recursoFiscalizacao = new RecursoFiscalizacao();
        this.alteraTipoRecursoFiscalizacao();

        if (selecionado.getTermoAdvertencia().getId() != null) {
            if (selecionado.getTermoAdvertencia().getDocumentoOficial() != null) {
                tipoDoctoOficial = selecionado.getTermoAdvertencia().getDocumentoOficial().getModeloDoctoOficial().getTipoDoctoOficial();
            }
        }

        quantidadeInfracao = null;
        this.tipoDoctoOficialTermoGeral = null;
        this.termoGeralFiscalizacao = null;
        this.carregaExisteReincidencia();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, processoFiscalizacaoFacade.getSistemaFacade().getDataOperacao());

    }

    public List<SelectItem> getTiposLevantamentoFiscalizacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoLevantamentoFiscalizacao tlf : TipoLevantamentoFiscalizacao.values()) {
            retorno.add(new SelectItem(tlf, tlf.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getResultadoRecursoFiscalizacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (ResultadoParecerFiscalizacao rpf : ResultadoParecerFiscalizacao.values()) {
            retorno.add(new SelectItem(rpf, rpf.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposEnceramento() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (EnceramentoTermoAdvertencia e : EnceramentoTermoAdvertencia.values()) {
            retorno.add(new SelectItem(e, e.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposCadastroFiscalizacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tlf : TipoCadastroTributario.values()) {
            retorno.add(new SelectItem(tlf, tlf.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoesProcessoFiscalizacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (StatusProcessoFiscalizacao status : StatusProcessoFiscalizacao.values()) {
            retorno.add(new SelectItem(status, status.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposRecursosFiscalizacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoRecursoFiscalizacao tipo : TipoRecursoFiscalizacao.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return this.processoFiscalizacaoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<Denuncia> completaDenuncia(String parte) {
        if (selecionado.getSecretariaFiscalizacao() != null) {
            return this.processoFiscalizacaoFacade.getDenunciaFacade().listaFiltrando(parte.trim(), selecionado.getSecretariaFiscalizacao());
        }
        return null;
    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, this.processoFiscalizacaoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public Converter getConverterDenuncia() {
        if (converterDenuncia == null) {
            converterDenuncia = new ConverterAutoComplete(Denuncia.class, this.processoFiscalizacaoFacade.getDenunciaFacade());
        }
        return converterDenuncia;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return this.processoFiscalizacaoFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, this.processoFiscalizacaoFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return this.processoFiscalizacaoFacade.getCadastroRuralFacade().completaCodigoNomeLocalizacaoIncraNomeCpfCnpjProprietario(parte.trim());
    }

    public Converter getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, this.processoFiscalizacaoFacade.getCadastroRuralFacade());
        }

        return converterCadastroRural;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return this.processoFiscalizacaoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, this.processoFiscalizacaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        return this.processoFiscalizacaoFacade.completaSecretaria(parte.trim());
    }

    public Converter getConverterSecretaria() {
        if (converterSecretariaFiscalizacao == null) {
            converterSecretariaFiscalizacao = new ConverterAutoComplete(SecretariaFiscalizacao.class, this.processoFiscalizacaoFacade.getSecretariaFiscalizacaoFacade());
        }
        return converterSecretariaFiscalizacao;
    }

    public Converter getConverterUsuarioSistema() {
        if (converterUsuarioSistema == null) {
            converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, this.processoFiscalizacaoFacade.getUsuarioSistemaFacade());
        }
        return converterUsuarioSistema;
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return processoFiscalizacaoFacade.getUsuarioSistemaFacade().listaFiltrandoUsuarioSistemaPorTipo(parte.trim(), TipoUsuarioTributario.FISCAL_OBRAS, TipoUsuarioTributario.FISCAL_MEIO_AMBIENTE, TipoUsuarioTributario.FISCAL_SANITARIO);
    }

    public void adicionaSituacao(StatusProcessoFiscalizacao status) {
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setData(new Date());
        spf.setStatusProcessoFiscalizacao(status);
        spf.setProcessoFiscalizacao(selecionado);
        selecionado.getSituacoesProcessoFiscalizacao().add(spf);
    }

    public String ultimaSituacao(ProcessoFiscalizacao processo) {
        ultimaSituacao = processoFiscalizacaoFacade.recuperaUltimaSituacaoDoProcesso(processo);
        try {
            return ultimaSituacao.getStatusProcessoFiscalizacao().getDescricao();
        } catch (Exception e) {
            if (selecionado != null) {
                if (selecionado.getSituacoesProcessoFiscalizacao().isEmpty()) {
                    return " - ";
                } else {
                    return selecionado.getSituacoesProcessoFiscalizacao().get(0).getStatusProcessoFiscalizacao().getDescricao();
                }
            } else {
                return " - ";
            }
        }
    }

    public String dataUltimaSituacao() {
        if (this.ultimaSituacao.getData() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(ultimaSituacao.getData());
        } else {
            return " - ";
        }
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void recuperaCodigo() {
        if (selecionado.getSecretariaFiscalizacao() != null) {
            selecionado.setCodigo(String.valueOf(processoFiscalizacaoFacade.recuperaProximaCodigo(selecionado.getSecretariaFiscalizacao())));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar recupar próximo código do processo.", "Selecione a secretaria para recuperar próximo código."));
        }
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return processoFiscalizacaoFacade.completaTipoDoctoOficial(parte.trim(), recuperaTipoCadastroDoctoOficial());
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialAutoInfracao(String parte) {
        return processoFiscalizacaoFacade.completaTipoDoctoOficialAutoInfracao(parte.trim(), recuperaTipoCadastroDoctoOficial());
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialParecerRecurso(String parte) {
        return processoFiscalizacaoFacade.completaTipoDoctoOficialParecerRecurso(parte.trim(), recuperaTipoCadastroDoctoOficial());
    }

    public ConverterAutoComplete getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, processoFiscalizacaoFacade.getLoteFacade());
        }
        return converterLote;
    }

    public ConverterAutoComplete getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, processoFiscalizacaoFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public ConverterAutoComplete getConverterEndereco() {
        if (converterEndereco == null) {
            converterEndereco = new ConverterAutoComplete(Endereco.class, processoFiscalizacaoFacade.getEnderecoFacade());
        }
        return converterEndereco;
    }

    public List<Endereco> completaEndereco(String parte) {
        return processoFiscalizacaoFacade.completaEndereco(parte.trim());
    }

    public List<Logradouro> completaLogradouros(String parte) {
        if (selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro() == null ||
            selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro().getId() == null) {
            return new ArrayList();
        }
        return logradouroFacade.completaLogradourosPorBairro(parte, selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro());
    }

    public void vinculaLogradouroBairroLocalOcorrencia() {
        if (selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro() != null && selecionado.getLocalOcorrencia().getLogradouroBairro().getLogradouro() != null) {
            selecionado.getLocalOcorrencia().setLogradouroBairro(logradouroFacade.recuperaLogradourBairro(selecionado.getLocalOcorrencia().getLogradouroBairro().getBairro(),
                selecionado.getLocalOcorrencia().getLogradouroBairro().getLogradouro()));
        }
    }

    public List<Lote> completaLote(String parte) {
        if (quadraPesquisa != null) {
            return processoFiscalizacaoFacade.completaLotePorQuadra(parte, quadraPesquisa);
        }
        return processoFiscalizacaoFacade.completaLote(parte);
    }

    public List<Quadra> completaQuadra(String parte) {
        return processoFiscalizacaoFacade.completaQuadra(parte);
    }

    public ConverterAutoComplete converterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, processoFiscalizacaoFacade.getTipoDoctoOficialFacade());
        }
        return converterTipoDoctoOficial;
    }

    public ProcessoFiscalizacaoFacade getProcessoFiscalizacaoFacade() {
        return processoFiscalizacaoFacade;
    }

    public void setProcessoFiscalizacaoFacade(ProcessoFiscalizacaoFacade processoFiscalizacaoFacade) {
        this.processoFiscalizacaoFacade = processoFiscalizacaoFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public boolean verificaAntesGerarTermoAdvertencia() {
        selecionaTipoDocumentoDoTermo();
        return validaCamposPreenchidosDoTermo();
    }

    public boolean termoAdvertenciaNotificacaoFoiGerado() {
        boolean retorno = false;
        if (selecionado.getTermoAdvertencia() != null &&
            selecionado.getTermoAdvertencia().getDocumentoOficial() != null &&
            selecionado.getTermoAdvertencia().getDocumentoOficial().getId() != null) {
            retorno = true;
        }
        return retorno;
    }

    public void solicitaGeracaoTermoDeAdvertencia() {
        if (termoAdvertenciaNotificacaoFoiGerado()) {
            FacesUtil.executaJavaScript("confirmaGeracaoTermo.show()");
        } else if (temInfracao()) {
            gerarTermoDeAdvertencia();
        } else {
            FacesUtil.executaJavaScript("confirmaGeracaoTermoSemInfracao.show()");
        }
    }

    public boolean temInfracao() {
        if (selecionado.getItensProcessoFiscalizacao() == null || selecionado.getItensProcessoFiscalizacao().size() <= 0) {
            return false;
        }
        return true;
    }

    public void gerarTermoDeAdvertencia() {
        if (!verificaAntesGerarTermoAdvertencia()) {
            return;
        }
        if (!termoAdvertenciaNotificacaoFoiGerado()) {
            SituacaoProcessoFiscal situacao = new SituacaoProcessoFiscal();
            situacao.setData(new Date());
            situacao.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.TERMO_ADVERTENCIA);
            situacao.setProcessoFiscalizacao(selecionado);
            if (!existeSituacao(situacao)) {
                selecionado.getSituacoesProcessoFiscalizacao().add(situacao);
            }
            this.alteraTipoRecursoFiscalizacao();
        }
        try {
            selecionado.getTermoAdvertencia().setDocumentoOficial(processoFiscalizacaoFacade.geraDocumentoTermoAdvertencia(tipoDoctoOficial, selecionado, getSistemaControlador()));
            FacesUtil.addInfo("Termo de advertência gerado com sucesso!", "");
        } catch (UFMException ex) {
            FacesUtil.addError("Impossível continuar!", ex.getMessage());
        } catch (AtributosNulosException ex) {
            FacesUtil.addError("Impossível continuar!", ex.getMessage());
        }

    }

    private void selecionaTipoDocumentoDoTermo() {
        switch (selecionado.getTipoCadastroFiscalizacao()) {
            case IMOBILIARIO:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getTermoImobiliario();
                break;
            case ECONOMICO:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getTermoEconomico();
                break;
            case RURAL:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getTermoRural();
                break;
            case PESSOA:
                if (selecionado.getPessoa() instanceof PessoaFisica) {
                    tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getTermoPessoaFisica();
                } else {
                    tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getTermoPessoaFisica();
                }
                break;
        }
    }

    private void selecionaTipoDocumentoDoAuto() {
        switch (selecionado.getTipoCadastroFiscalizacao()) {
            case IMOBILIARIO:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getAutoImobiliario();
                break;
            case ECONOMICO:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getAutoEconomico();
                break;
            case RURAL:
                tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getAutoRural();
                break;
            case PESSOA:
                if (selecionado.getPessoa() instanceof PessoaFisica) {
                    tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getAutoPessoaFisica();
                } else {
                    tipoDoctoOficial = selecionado.getSecretariaFiscalizacao().getAutoPessoaJuridica();
                }
                break;
        }
    }

    public void geraPDFTermoDeAdvertencia() {
        processoFiscalizacaoFacade.geraPDF(selecionado.getTermoAdvertencia().getDocumentoOficial());
    }

    public boolean autoInfracaoFoiGerado() {
        boolean retorno = false;
        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() != null) {
            retorno = true;
        }
        return retorno;
    }

    public void solicitaGeracaoAutoInfracao() {
        if (autoInfracaoFoiGerado()) {
            FacesUtil.executaJavaScript("confirmaGeracaoAuto.show()");
        } else {
            gerarAutoInfracao();
        }
    }

    public void gerarAutoInfracao() {
        selecionaTipoDocumentoDoAuto();
        if (!validaCamposPreenchidosDoAutoInfracao()) {
            return;
        }
        try {
            if (!autoInfracaoFoiGerado()) {
                SituacaoProcessoFiscal situacao = new SituacaoProcessoFiscal();
                situacao.setData(new Date());
                situacao.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.AUTUADO);
                situacao.setProcessoFiscalizacao(selecionado);
                if (!existeSituacao(situacao)) {
                    selecionado.getSituacoesProcessoFiscalizacao().add(situacao);
                }
                this.alteraTipoRecursoFiscalizacao();
                selecionado.getAutoInfracaoFiscalizacao().setNumero(processoFiscalizacaoFacade.recuperaProximaCodigoAutoDeInfracao(selecionado.getSecretariaFiscalizacao()));
            }
            selecionado.getAutoInfracaoFiscalizacao().setDocumentoOficial(processoFiscalizacaoFacade.geraDocumentoAutoInfracao(tipoDoctoOficial, selecionado, getSistemaControlador()));
            FacesUtil.addInfo("Auto de infração gerado com sucesso!", "");
        } catch (UFMException ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
        } catch (AtributosNulosException ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
        }
    }

    public void geraPDFAutoInfracao() {
        processoFiscalizacaoFacade.geraPDF(selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial());
    }

    public boolean validaCamposPreenchidosDoTermo() {
        boolean deuCerto = true;
        if (selecionado.getTermoAdvertencia().getNumero() == null || selecionado.getTermoAdvertencia().getNumero().trim().length() <= 0) {
            deuCerto = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe o número do Termo");
        }
        if (selecionado.getTermoAdvertencia().getDataLavratura() == null) {
            deuCerto = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe a data de Lavratura");
        }
        if (selecionado.getTermoAdvertencia().getUsuarioSistema() == null) {
            deuCerto = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe o Auditor Fiscal");
        }
        if (selecionado.getTermoAdvertencia().getDescricaoDaInfracao() == null) {
            deuCerto = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe a Descrição da Infração/Notificação");
        }
        if (tipoDoctoOficial == null) {
            deuCerto = false;
            tabIndex = 1;
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado nenhum Tipo de Documento Oficial para esse tipo de cadastro na secretaria " + selecionado.getSecretariaFiscalizacao().getUnidadeOrganizacional().getDescricao());
        }
        if (selecionado.getDataCadastro() == null) {
            deuCerto = false;
            tabIndex = 2;
            FacesUtil.addCampoObrigatorio("Data da Fiscalização deve ser informada.");
        }
        if (selecionado.getTermoAdvertencia() != null &&
            selecionado.getTermoAdvertencia().getDataCiencia() != null &&
            selecionado.getTermoAdvertencia().getDataLavratura() != null &&
            (selecionado.getTermoAdvertencia().getDataCiencia().compareTo(selecionado.getTermoAdvertencia().getDataLavratura()) < 0)) {
            deuCerto = false;
            tabIndex = 2;
            FacesUtil.addOperacaoNaoPermitida("Data de Ciência não pode ser menor que a Data de Lavratura.");
        }
        if (selecionado.getTermoAdvertencia() != null &&
            selecionado.getTermoAdvertencia().getDataCiencia() != null &&
            selecionado.getDataCadastro() != null &&
            (selecionado.getTermoAdvertencia().getDataCiencia().compareTo(selecionado.getDataCadastro()) < 0)) {
            deuCerto = false;
            tabIndex = 2;
            FacesUtil.addOperacaoNaoPermitida("Data de Ciência não pode ser menor que a Data da Fiscalização.");
        }
        return deuCerto;
    }

    public void atualizaPrazoRecurso() {
        if (selecionado.getAutoInfracaoFiscalizacao().getDataCiencia() != null) {
            Calendar calendar = calculaDataNovoPrazo(selecionado.getAutoInfracaoFiscalizacao().getDataCiencia(), selecionado.getSecretariaFiscalizacao().getPrazoAutoInfracao());
            selecionado.getAutoInfracaoFiscalizacao().setPrazoRecurso(calendar.getTime());
        }
    }

    public void limpaCadastroPessoa() {
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.SUSPENSA);
        return situacoes;
    }

    public void carregaExisteReincidencia() {
        this.listaReincidencias = this.processoFiscalizacaoFacade.recuperaReincidencias(selecionado);
        this.existeReincidencia = !listaReincidencias.isEmpty();
        if (selecionado.getCadastro() instanceof CadastroImobiliario) {
            CadastroImobiliario ci = processoFiscalizacaoFacade.getCadastroImobiliarioFacade().recuperar(selecionado.getCadastro().getId());
            lote = ci.getLote();
            propriedadesBCI = ci.getPropriedadeVigente();
            selecionado.setCadastro(ci);
        }
        if (selecionado.getCadastro() instanceof CadastroEconomico) {
            CadastroEconomico ce = processoFiscalizacaoFacade.getCadastroEconomicoFacade().recuperar(selecionado.getCadastro().getId());
            socios = ce.getSociedadeCadastroEconomico();
            selecionado.setCadastro(ce);
        }
        if (selecionado.getCadastro() instanceof CadastroRural) {
            CadastroRural cr = processoFiscalizacaoFacade.getCadastroRuralFacade().recuperar(selecionado.getCadastro().getId());
            propriedadesBCR = cr.getPropriedadesAtuais();
            selecionado.setCadastro(cr);
        }
        if (selecionado.getCadastro() == null && selecionado.getPessoa() != null) {
            Pessoa recuperar = processoFiscalizacaoFacade.getPessoaFacade().recuperar(selecionado.getPessoa().getId());
            enderecos = recuperar.getEnderecos();
            telefones = recuperar.getTelefones();
            selecionado.setPessoa(recuperar);
        }

        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.atualizarComponente(":Formulario:tabs:reincidencia");
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    private TipoCadastroDoctoOficial recuperaTipoCadastroDoctoOficial() {
        if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.ECONOMICO)) {
            return TipoCadastroDoctoOficial.CADASTROECONOMICO;
        }
        if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.IMOBILIARIO)) {
            return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
        }
        if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.RURAL)) {
            return TipoCadastroDoctoOficial.CADASTRORURAL;
        }
        if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.PESSOA) && selecionado.getPessoa() instanceof PessoaFisica) {
            return TipoCadastroDoctoOficial.PESSOAFISICA;
        }
        if (selecionado.getTipoCadastroFiscalizacao().equals(TipoCadastroTributario.PESSOA) && selecionado.getPessoa() instanceof PessoaJuridica) {
            return TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }
        return null;
    }

    public TipoDoctoOficial getTipoDoctoOficialAutoInfracao() {
        return tipoDoctoOficialAutoInfracao;
    }

    public void setTipoDoctoOficialAutoInfracao(TipoDoctoOficial tipoDoctoOficialAutoInfracao) {
        this.tipoDoctoOficialAutoInfracao = tipoDoctoOficialAutoInfracao;
    }

    public String codigoPorSecretaria() {
        return this.processoFiscalizacaoFacade.codigoPorSecretaria();
    }

    public RecursoFiscalizacao getRecursoFiscalizacao() {
        return recursoFiscalizacao;
    }

    public void setRecursoFiscalizacao(RecursoFiscalizacao recursoFiscalizacao) {
        this.recursoFiscalizacao = recursoFiscalizacao;
    }

    public void adicionarRecurso() {
        if (!validaCamposRecurso()) {
            return;
        }
        if (!validaCienciaDoAutoDeInfracao()) {
            return;
        }
        recursoFiscalizacao.setProcessoFiscalizacao(selecionado);

        if (selecionado.getSecretariaFiscalizacao() != null) {
            if (selecionado.getSecretariaFiscalizacao().getPrazoAutoInfracao() != null) {
                recursoFiscalizacao.setDataNovoPrazo(calculaDataNovoPrazo(getSistemaControlador().getDataOperacao(), selecionado.getSecretariaFiscalizacao().getPrazoAutoInfracao()).getTime());
            } else {
                recursoFiscalizacao.setDataNovoPrazo(calculaDataNovoPrazo(getSistemaControlador().getDataOperacao(), 0).getTime());
            }
        }

        selecionado.getRecursoFiscalizacaos().add(recursoFiscalizacao);
        if (recursoFiscalizacao.getTipoRecursoFiscalizacao() != null) {
            if (recursoFiscalizacao.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.TERMO_ADVERTENCIA)) {
                adicionaSituacao(StatusProcessoFiscalizacao.RECURSO_TERMO_ADVERTENCIA);
            }

            if (recursoFiscalizacao.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.AUTOINFRACAO_1INSTANCIA)) {
                adicionaSituacao(StatusProcessoFiscalizacao.RECURSO_1INSTANCIA);
            }

            if (recursoFiscalizacao.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.AUTOINFRACAO_2INSTANCIA)) {
                adicionaSituacao(StatusProcessoFiscalizacao.RECURSO_2INSTANCIA);
            }
        }
        recursoFiscalizacao = new RecursoFiscalizacao();
        this.alteraTipoRecursoFiscalizacao();

    }

    public void removerRecurso(RecursoFiscalizacao recursoFiscalizacao) {
        selecionado.getRecursoFiscalizacaos().remove(recursoFiscalizacao);
        selecionado.getSituacoesProcessoFiscalizacao().remove(selecionado.getSituacoesProcessoFiscalizacao().size() - 1);
        this.alteraTipoRecursoFiscalizacao();
    }

    public void removerInfracaoPenalidade(ItemProcessoFiscalizacao itemProcessoFiscalizacao) {
        selecionado.getItensProcessoFiscalizacao().remove(itemProcessoFiscalizacao);
    }

    private boolean validaCamposRecurso() {
        boolean deuCerto = true;
        if (this.recursoFiscalizacao.getDataEntrada() == null) {
            deuCerto = false;
            tabIndex = 3;
            FacesUtil.addError("Erro ao tentar adicionar recurso.", "O campo data de Entrada é obrigatório.");
        } else {
            if (this.recursoFiscalizacao.getDataEntrada().after(new Date())) {
                deuCerto = false;
                tabIndex = 3;
                FacesUtil.addError("Erro ao tentar adicionar recurso.", "A data de entrada não pode ser maior que hoje.");
            }
        }
        if (this.recursoFiscalizacao.getTeorRecurso() == null || this.recursoFiscalizacao.getTeorRecurso().trim().length() <= 0) {
            deuCerto = false;
            tabIndex = 3;
            FacesUtil.addError("Erro ao tentar adicionar recurso.", "O campo teor do recurso é obrigatório.");
        }
        if (this.recursoFiscalizacao.getDataEntrada() != null && selecionado.getAutoInfracaoFiscalizacao().getPrazoRecurso() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(this.recursoFiscalizacao.getDataEntrada()).compareTo(Util.getDataHoraMinutoSegundoZerado(selecionado.getAutoInfracaoFiscalizacao().getPrazoRecurso())) > 0
                && this.recursoFiscalizacao.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.TERMO_ADVERTENCIA)) {
                FacesUtil.addError("Atenção!", "O prazo de recurso já está vencido, não é possível adicionar mais recursos.");
                deuCerto = false;
            }
        }
        if (!selecionado.getRecursoFiscalizacaos().isEmpty()) {
            int indiceDoUltimoCara = selecionado.getRecursoFiscalizacaos().size() - 1;
            if (selecionado.getRecursoFiscalizacaos().get(indiceDoUltimoCara).getDataParecer() == null) {
                FacesUtil.addError("Atenção!", "Não é possível adicionar um novo recurso, caso não houver parecer do recurso anterior.");
                deuCerto = false;
            }
            if (Util.getDataHoraMinutoSegundoZerado(this.recursoFiscalizacao.getDataEntrada()).compareTo(Util.getDataHoraMinutoSegundoZerado(selecionado.getRecursoFiscalizacaos().get(indiceDoUltimoCara).getDataEntrada())) < 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                FacesUtil.addError("Atenção!", "A data de entrada não pode ser menor a data de entrada do ultimo recurso.");
                deuCerto = false;
            }
            if (Util.getDataHoraMinutoSegundoZerado(this.recursoFiscalizacao.getDataEntrada()).compareTo(Util.getDataHoraMinutoSegundoZerado(selecionado.getRecursoFiscalizacaos().get(indiceDoUltimoCara).getDataNovoPrazo())) > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                FacesUtil.addError("Atenção!", "O prazo de recurso já está vencido, não é possível adicionar mais recursos.");
                deuCerto = false;
            }
        }

        return deuCerto;
    }

    public void emitirParecer(RecursoFiscalizacao recurso) {
        this.recursoFiscalizacaoEmissaoParecer = recurso;
        this.recursoFiscalizacao = new RecursoFiscalizacao();
        this.recursoFiscalizacao.setDataParecer(new Date());
        if (this.recursoFiscalizacaoEmissaoParecer.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.TERMO_ADVERTENCIA)) {
            this.recursoFiscalizacao.setDataNovoPrazo(calculaDataNovoPrazo(this.recursoFiscalizacao.getDataParecer(),
                selecionado.getSecretariaFiscalizacao().getPrazoRecurso()).getTime());
        } else if (this.recursoFiscalizacaoEmissaoParecer.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.AUTOINFRACAO_1INSTANCIA)) {
            this.recursoFiscalizacao.setDataNovoPrazo(calculaDataNovoPrazo(this.recursoFiscalizacao.getDataParecer(),
                selecionado.getSecretariaFiscalizacao().getPrazoPrimeiraInstancia()).getTime());
        } else if (this.recursoFiscalizacaoEmissaoParecer.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.AUTOINFRACAO_2INSTANCIA)) {
            this.recursoFiscalizacao.setDataNovoPrazo(calculaDataNovoPrazo(this.recursoFiscalizacao.getDataParecer(),
                selecionado.getSecretariaFiscalizacao().getPrazoSegundaInstancia()).getTime());
        }
        tipoDoctoOficialParecer = null;
    }

    public boolean validaCamposEmissaoParecer() {
        boolean retorno = true;
        if (this.recursoFiscalizacao.getParecerRecurso() == null && this.recursoFiscalizacao.getParecerRecurso().isEmpty()) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Parecer do Recurso deve ser informado.");
        }
        if (this.recursoFiscalizacao.getResultadoParecerFiscalizacao() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Resultado do Recurso deve ser informado.");
        }
        if (tipoDoctoOficial == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Tipo de Documento Oficial deve ser informado.");
        }

        return retorno;
    }

    public void salvarEmissaoParecer() {
        if (!validaCamposEmissaoParecer()) {
            return;
        }
        try {
            this.recursoFiscalizacaoEmissaoParecer.setDataNovoPrazo(this.recursoFiscalizacao.getDataNovoPrazo());
            this.recursoFiscalizacaoEmissaoParecer.setDataParecer(this.recursoFiscalizacao.getDataParecer());
            this.recursoFiscalizacaoEmissaoParecer.setParecerRecurso(this.recursoFiscalizacao.getParecerRecurso());
            this.recursoFiscalizacaoEmissaoParecer.setResultadoParecerFiscalizacao(this.recursoFiscalizacao.getResultadoParecerFiscalizacao());
            DocumentoOficial geraDocumentoParecer = processoFiscalizacaoFacade.geraDocumentoParecer(tipoDoctoOficialParecer,
                this.recursoFiscalizacaoEmissaoParecer, selecionado.getCadastro(), selecionado.getPessoa(), getSistemaControlador());
            this.recursoFiscalizacaoEmissaoParecer.setDocumentoOficial(geraDocumentoParecer);
            FacesUtil.addInfo("Parecer salvo com sucesso", "Para emitir o parecer salve o processo de fiscalização");
            FacesUtil.executaJavaScript("dialogParecerRecurso.hide()");
        } catch (UFMException ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
        } catch (AtributosNulosException ex) {
            FacesUtil.addError("Impossível continuar", ex.getMessage());
        }
        atualizaRecurso();
    }

    private boolean validaCamposPreenchidosDoAutoInfracao() {
        boolean retorno = true;
        if (selecionado.getAutoInfracaoFiscalizacao().getDataLavratura() == null) {
            retorno = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe a data de Lavratura");
        }
        if (selecionado.getAutoInfracaoFiscalizacao().getUsuarioSistema() == null) {
            retorno = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe o auditor fiscal");
        }
        if (selecionado.getAutoInfracaoFiscalizacao().getDescricaoInfracao() == null) {
            retorno = false;
            tabIndex = 1;
            FacesUtil.addCampoObrigatorio("Informe a descrição da infração");
        }
        if (tipoDoctoOficial == null) {
            retorno = false;
            tabIndex = 1;
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado nenhum Tipo de Documento Oficial para esse tipo de cadastro na secretaria " + selecionado.getSecretariaFiscalizacao().getUnidadeOrganizacional().getDescricao());
        }
        if (selecionado.getItensProcessoFiscalizacao() == null || selecionado.getItensProcessoFiscalizacao().size() <= 0) {
            retorno = false;
            tabIndex = 2;
            FacesUtil.addOperacaoNaoPermitida("Nenhuma Infração/Penalidade registrada.");
        }
        if (selecionado.getDataCadastro() == null) {
            retorno = false;
            tabIndex = 2;
            FacesUtil.addCampoObrigatorio("Data da Fiscalização deve ser informada.");
        }

        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDataCiencia() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDataLavratura() != null &&
            (selecionado.getAutoInfracaoFiscalizacao().getDataCiencia().compareTo(selecionado.getAutoInfracaoFiscalizacao().getDataLavratura()) < 0)) {
            retorno = false;
            tabIndex = 3;
            FacesUtil.addOperacaoNaoPermitida("Data de Ciência não pode ser menor que a Data de Lavratura.");
        }
        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDataCiencia() != null &&
            selecionado.getDataCadastro() != null &&
            (selecionado.getAutoInfracaoFiscalizacao().getDataCiencia().compareTo(selecionado.getDataCadastro()) < 0)) {
            retorno = false;
            tabIndex = 3;
            FacesUtil.addOperacaoNaoPermitida("Data de Ciência não pode ser menor que a Data da Fiscalização.");
        }
        return retorno;
    }

    public void mudaTab(int numero) {
        tabIndex = numero;
    }

    private String criarNumeroTermo() {
        return "";
    }

    private void criaSituacaoDigitadoNoProcesso() {
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setData(selecionado.getDataCadastro());
        spf.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.DIGITADO);
        spf.setProcessoFiscalizacao(selecionado);
        selecionado.getSituacoesProcessoFiscalizacao().add(spf);
    }

    private void imprimeDam(ValorDivida valorDivida) throws Exception {
        List<DAM> dams = processoFiscalizacaoFacade.getDamFacade().geraDAMs(valorDivida.getParcelaValorDividas());
        ImprimeDAM imprimeDAM = new ImprimeDAM();
        imprimeDAM.setGeraNoDialog(true);
        imprimeDAM.imprimirDamUnicoViaApi(dams);
    }

    public void geraDam() throws JRException, IOException {
        try {
            ValorDivida valorDivida = processoFiscalizacaoFacade.getValorDivida(selecionado);
            imprimeDam(valorDivida);
        } catch (Exception ex) {
            FacesUtil.addError("Não foi possível imprimir o(s) DAM(s)!", ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    private String montaDescricao() {
        StringBuilder sb = new StringBuilder("");
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        BigDecimal valorTotal = BigDecimal.ZERO;
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        //                123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901
        sb.append("INFRACAO                             TRIBUTO                                        VALOR").append("\n");
        //                Infração para Poluição Sonora        SEMEIA - Infração                    10.00.000,00

        return sb.toString();
    }

    private String incluiTexto(String texto, int tamanho, boolean alinhaEsquerda) {
        String retorno = null;
        if (texto == null || tamanho < 0) {
            return "";
        }
        if (texto.length() >= tamanho) {
            return texto.substring(0, tamanho);
        } else {
            int diferenca = tamanho - texto.length();
            String espacos = "";
            for (int i = 0; i < diferenca; i++) {
                espacos += " ";
            }
            if (alinhaEsquerda) {
                retorno = texto + espacos;
            } else {
                retorno = espacos + texto;
            }
        }
        return retorno;
    }

    public void atualizaRecurso() {
        this.recursoFiscalizacao = new RecursoFiscalizacao();
        this.alteraTipoRecursoFiscalizacao();

    }

    public void geraPDFRecurso(RecursoFiscalizacao recurso) {
        processoFiscalizacaoFacade.geraPDF(recurso.getDocumentoOficial());
    }

    public void limpaFiltroPesquisa() {
        bairroPesquisa = null;
        lotePesquisa = null;
        quadraPesquisa = null;
        enderecoPesquisa = null;
        listaDeImobilariosDaPesquisa = null;
    }

    public void adicionarImobiliario(CadastroImobiliario cadastro) {
        selecionado.setCadastro(cadastro);
    }

    public boolean validaCamposPreenchidosDaPesquisa() {
        boolean deuCerto = true;
        if (bairroPesquisa == null && lotePesquisa == null && quadraPesquisa == null && enderecoPesquisa == null) {
            deuCerto = false;
            FacesUtil.addError("Erro ao tentar pesquisar!", "Informe pelo menos um filtro para efetuar a pesquisa.");
        }
        return deuCerto;

    }

    public String proprietarioDoCadastroImobiliario(CadastroImobiliario cadastro) {
        try {
            return processoFiscalizacaoFacade.nomeProprietario(cadastro);
        } catch (Exception e) {
            return " - ";
        }

    }

    public Endereco getEnderecoPesquisa() {
        return enderecoPesquisa;
    }

    public void setEnderecoPesquisa(Endereco enderecoPesquisa) {
        this.enderecoPesquisa = enderecoPesquisa;
    }

    public Bairro getBairroPesquisa() {
        return bairroPesquisa;
    }

    public void setBairroPesquisa(Bairro bairroPesquisa) {
        this.bairroPesquisa = bairroPesquisa;
    }

    public Lote getLotePesquisa() {
        return lotePesquisa;
    }

    public void setLotePesquisa(Lote lotePesquisa) {
        this.lotePesquisa = lotePesquisa;
    }

    public Quadra getQuadraPesquisa() {
        return quadraPesquisa;
    }

    public void setQuadraPesquisa(Quadra quadraPesquisa) {
        this.quadraPesquisa = quadraPesquisa;
    }

    public List<CadastroImobiliario> getListaDeImobilariosDaPesquisa() {
        return listaDeImobilariosDaPesquisa;
    }

    public void setListaDeImobilariosDaPesquisa(List<CadastroImobiliario> listaDeImobilariosDaPesquisa) {
        this.listaDeImobilariosDaPesquisa = listaDeImobilariosDaPesquisa;
    }

    public Boolean desabilitaGeracaoAutoInfracao() {
        Boolean retorno = Boolean.FALSE;
        if (isProcessoPago()) {
            retorno = Boolean.TRUE;
        }
        return retorno;
    }

    public Boolean desabilitaImpressaoAutoInfracao() {
        Boolean retorno = Boolean.FALSE;
        if (selecionado.getAutoInfracaoFiscalizacao() == null ||
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() == null ||
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() == null) {
            retorno = Boolean.TRUE;
        }
        return retorno;
    }

    public Boolean desabilitaGeracaoTermoInfracaoNotificacao() {
        Boolean retorno = Boolean.FALSE;
        if (isProcessoPago()) {
            retorno = Boolean.TRUE;
        }
        return retorno;
    }


    public Boolean desabilitaImpressaoTermoInfracaoNotificacao() {
        Boolean retorno = Boolean.FALSE;
        if (selecionado.getTermoAdvertencia() == null ||
            selecionado.getTermoAdvertencia().getDocumentoOficial() == null ||
            selecionado.getTermoAdvertencia().getDocumentoOficial().getId() == null) {
            retorno = Boolean.TRUE;
        }
        return retorno;
    }

    public boolean isProcessoPago() {
        if (!selecionado.getSituacoesProcessoFiscalizacao().isEmpty() && selecionado.getId() != null) {
            SituacaoProcessoFiscal ultimaSituacao = null;
            try {
                ultimaSituacao = processoFiscalizacaoFacade.recuperaUltimaSituacaoDoProcesso(selecionado);
                if (ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.PAGO) || ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.ENCERRADO)) {
                    return true;
                }
            } catch (Exception e) {
                ultimaSituacao = selecionado.getSituacoesProcessoFiscalizacao().get(0);
            }
        }
        return false;
    }

    private void alteraTipoRecursoFiscalizacao() {
        SituacaoProcessoFiscal spf = recuperaNaListaUltimaSituacaoDoProcesso();
        if (spf != null) {
            if (spf.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.TERMO_ADVERTENCIA)) {
                this.recursoFiscalizacao.setTipoRecursoFiscalizacao(TipoRecursoFiscalizacao.TERMO_ADVERTENCIA);
            } else if (spf.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.AUTUADO)) {
                this.recursoFiscalizacao.setTipoRecursoFiscalizacao(TipoRecursoFiscalizacao.AUTOINFRACAO_1INSTANCIA);
            } else if (spf.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.RECURSO_1INSTANCIA)) {
                this.recursoFiscalizacao.setTipoRecursoFiscalizacao(TipoRecursoFiscalizacao.AUTOINFRACAO_2INSTANCIA);
            }
        }
    }

    public boolean desabilitaBotaoGerarDam() {
        if (selecionado.getId() == null) {
            return true;
        }
        if (selecionado.getAutoInfracaoFiscalizacao() == null ||
            selecionado.getAutoInfracaoFiscalizacao().getId() == null ||
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() == null ||
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() == null) {
            return true;
        }
        List<ResultadoParcela> parcelas = getRecuperaParcelas();
        if (parcelas != null && parcelas.size() > 0) {
            DAM dam = recuperaDAM(parcelas.get(0).getIdParcela());
            if (dam != null && !dam.getSituacao().equals(DAM.Situacao.ABERTO)) {
                return true;
            }
        }
        return false;
    }

    public TipoDoctoOficial getTipoDoctoOficialParecer() {
        return tipoDoctoOficialParecer;
    }

    public void setTipoDoctoOficialParecer(TipoDoctoOficial tipoDoctoOficialParecer) {
        this.tipoDoctoOficialParecer = tipoDoctoOficialParecer;
    }

    private SituacaoProcessoFiscal recuperaNaListaUltimaSituacaoDoProcesso() {
        int ultimaPosicao = selecionado.getSituacoesProcessoFiscalizacao().size() - 1;
        if (!selecionado.getSituacoesProcessoFiscalizacao().isEmpty()) {
            return selecionado.getSituacoesProcessoFiscalizacao().get(ultimaPosicao);
        }
        return null;
    }

    private boolean existeSituacao(SituacaoProcessoFiscal situacao) {
        for (SituacaoProcessoFiscal situacaoProcessoFiscal1 : selecionado.getSituacoesProcessoFiscalizacao()) {
            if (situacao.equals(situacaoProcessoFiscal1)) {
                return true;
            }
        }
        return false;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (selecionado.getCadastro() != null) {
            return processoFiscalizacaoFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) selecionado.getCadastro());
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public Testada getTestadaPrincipal() {
        if (selecionado.getCadastro() != null) {
            return processoFiscalizacaoFacade.getLoteFacade().recuperarTestadaPrincipal(((CadastroImobiliario) selecionado.getCadastro()).getLote());
        } else {
            return new Testada();
        }
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        List<Pessoa> retorno = new ArrayList<Pessoa>();
        if (selecionado.getCadastro() instanceof CadastroImobiliario) {
            CadastroImobiliario cadastroIm = processoFiscalizacaoFacade.getCadastroImobiliarioFacade().recuperar(selecionado.getCadastro().getId());
            for (Propriedade p : cadastroIm.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (selecionado.getCadastro() instanceof CadastroRural) {
            CadastroRural cadastroRu = processoFiscalizacaoFacade.getCadastroRuralFacade().recuperar(selecionado.getCadastro().getId());
            for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (selecionado.getCadastro() instanceof CadastroEconomico) {
            CadastroEconomico cadastroEco = processoFiscalizacaoFacade.getCadastroEconomicoFacade().recuperar(selecionado.getCadastro().getId());
            for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                retorno.add(sociedadeCadastroEconomico.getSocio());
            }
        }
        return retorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return processoFiscalizacaoFacade;
    }

    public Pessoa getPessoaFiltroCadastroEconomico() {
        return pessoaFiltroCadastroEconomico;
    }

    public void setPessoaFiltroCadastroEconomico(Pessoa pessoaFiltroCadastroEconomico) {
        this.pessoaFiltroCadastroEconomico = pessoaFiltroCadastroEconomico;
    }

    public List<CadastroEconomico> getListaDeEconomicoDaPesquisa() {
        return listaDeEconomicoDaPesquisa;
    }

    public void setListaDeEconomicoDaPesquisa(List<CadastroEconomico> listaDeEconomicoDaPesquisa) {
        this.listaDeEconomicoDaPesquisa = listaDeEconomicoDaPesquisa;
    }

    public List<Pessoa> completaSocio(String parte) {
        return processoFiscalizacaoFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public void pesquisaCadastroEconomicioPorSocio() {
        listaDeEconomicoDaPesquisa = processoFiscalizacaoFacade.pesquisaCadastroDeSocio(pessoaFiltroCadastroEconomico);
    }

    public void adicionarEconomio(CadastroEconomico cadastro) {
        selecionado.setCadastro(cadastro);
    }

    public void limpaFiltroPesquisaCadastroEconomico() {
        pessoaFiltroCadastroEconomico = null;
        listaDeEconomicoDaPesquisa = null;
    }

    private boolean validaCienciaDoAutoDeInfracao() {
        boolean deuCerto = true;
        if (recursoFiscalizacao.getTipoRecursoFiscalizacao() != null && recursoFiscalizacao.getTipoRecursoFiscalizacao().equals(TipoRecursoFiscalizacao.AUTOINFRACAO_1INSTANCIA)) {
            return deuCerto;
        }
        if (selecionado.getAutoInfracaoFiscalizacao().getPessoaCiencia() == null || selecionado.getAutoInfracaoFiscalizacao().getPessoaCiencia().trim().length() <= 0) {
            FacesUtil.addError("Atenção!", "Não é possível adicionar um novo recurso, caso não houver nome da pessoa da ciência no auto de infração.");
            deuCerto = false;
        }
        if (selecionado.getAutoInfracaoFiscalizacao().getDataCiencia() == null) {
            FacesUtil.addError("Atenção!", "Não é possível adicionar um novo recurso, caso não houver data de ciência no auto de infração.");
            deuCerto = false;
        }
        return deuCerto;
    }

    public TipoDoctoOficial getTipoDoctoOficialTermoGeral() {
        return tipoDoctoOficialTermoGeral;
    }

    public void setTipoDoctoOficialTermoGeral(TipoDoctoOficial tipoDoctoOficialTermoGeral) {
        this.tipoDoctoOficialTermoGeral = tipoDoctoOficialTermoGeral;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialTermoGerais(String parte) {
        return processoFiscalizacaoFacade.completaTipoDoctoOficialTermoGerais(parte.trim(), recuperaTipoCadastroDoctoOficial());
    }

    public TermoGeralFiscalizacao getTermoGeralFiscalizacao() {
        return termoGeralFiscalizacao;
    }

    public void setTermoGeralFiscalizacao(TermoGeralFiscalizacao termoGeralFiscalizacao) {
        this.termoGeralFiscalizacao = termoGeralFiscalizacao;
    }

    public void selecionou(SelectEvent e) {
        tipoDoctoOficialTermoGeral = (TipoDoctoOficial) e.getObject();
        recuperaAtributosDoTipo();
    }

    public void recuperaAtributosDoTipo() {
        if (tipoDoctoOficialTermoGeral != null) {
            tipoDoctoOficialTermoGeral.setListaAtributosDoctoOficial(processoFiscalizacaoFacade.getTipoDoctoOficialFacade().recuperaAtributoPorTipoDocto(tipoDoctoOficialTermoGeral));
            this.termoGeralFiscalizacao = new TermoGeralFiscalizacao();
            this.termoGeralFiscalizacao.setProcessoFiscalizacao(selecionado);
            this.termoGeralFiscalizacao.setValoresAtributosFiscalizacao(new ArrayList<ValorAtributoFiscalizacao>());
            for (AtributoDoctoOficial atributoDoctoOficial : tipoDoctoOficialTermoGeral.getListaAtributosDoctoOficial()) {
                ValorAtributoFiscalizacao valorAtributo = new ValorAtributoFiscalizacao();
                valorAtributo.setTermoGeralFiscalizacao(termoGeralFiscalizacao);
                valorAtributo.setAtributoDoctoOficial(atributoDoctoOficial);
                this.termoGeralFiscalizacao.getValoresAtributosFiscalizacao().add(valorAtributo);
            }
        } else {
            FacesUtil.addError("Erro ao recuperar Atributos!", "Selecione um tipo de documento oficial.");
        }
    }

    public boolean desabilitaEditorDocumento() {
        if (tipoDoctoOficialTermoGeral != null) {
            try {
                return tipoDoctoOficialTermoGeral.getImprimirDiretoPDF();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void recuperaDocumentoVigente() {
        if (!validaCamposObrigatorios()) {
            return;
        }
        try {
            termoGeralFiscalizacao.setDocumentoOficial(processoFiscalizacaoFacade.getDocumentoOficialFacade().geraDocumentoTermoGeral(termoGeralFiscalizacao, null, ((ProcessoFiscalizacao) selecionado).getCadastro(), ((ProcessoFiscalizacao) selecionado).getPessoa(), tipoDoctoOficialTermoGeral, getSistemaControlador()));
            selecionado.getTermoGeralFiscalizacao().add(termoGeralFiscalizacao);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
//        RequestContext requeste = RequestContext.getCurrentInstance();
//        requeste.execute("dialogDocumentoGeral.show()");
    }

    public void imprimirDocumento() {
        selecionado.getTermoGeralFiscalizacao().add(termoGeralFiscalizacao);
        processoFiscalizacaoFacade.getDocumentoOficialFacade().emiteDocumentoOficial(termoGeralFiscalizacao.getDocumentoOficial());
        this.termoGeralFiscalizacao = null;
        this.tipoDoctoOficialTermoGeral = null;
    }

    public void geraPDF(DocumentoOficial documento) {
        processoFiscalizacaoFacade.getDocumentoOficialFacade().emiteDocumentoOficial(documento);
    }

    private boolean validaCamposObrigatorios() {
        boolean deuCerto = true;
        for (ValorAtributoFiscalizacao valorAtributoFiscalizacao : termoGeralFiscalizacao.getValoresAtributosFiscalizacao()) {
            if (valorAtributoFiscalizacao.getAtributoDoctoOficial().getObrigatorio() && (valorAtributoFiscalizacao.getValor() == null || valorAtributoFiscalizacao.getValor().trim().length() <= 0)) {
                deuCerto = false;
                FacesUtil.addError("Erro ao gerar Documento!", "O atributo " + valorAtributoFiscalizacao.getAtributoDoctoOficial().getCampo() + " é obrigátorio e não foi informado. ");
            }
        }
        return deuCerto;
    }

    private SecretariaFiscalizacao retornaSecretariaDoUsuarioLogado() {
        for (SecretariaFiscalizacao secretariaFiscalizacao : this.processoFiscalizacaoFacade.getSecretariaFiscalizacaoFacade().getLista()) {
            if (secretariaFiscalizacao.getUnidadeOrganizacional().equals(getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente())) {
                return secretariaFiscalizacao;
            }
        }
        return null;
    }

    public boolean validaDesconto() {
        UsuarioSistema usuario = getSistemaControlador().getUsuarioCorrente();
        TipoUsuarioTribUsuario tipo = processoFiscalizacaoFacade.getUsuarioSistemaFacade().listaTipoUsuarioVigenteDoUsuarioPorTipo(usuario, TipoUsuarioTributario.FISCAL_TRIBUTARIO);
        if (tipo != null) {
            return tipo.getSupervisor();
        }
        return false;
    }

    public boolean getDenuncia() {
        if (selecionado == null || selecionado.getTipoLevantamentoFiscalizacao() == null) {
            return false;
        }
        return selecionado.getTipoLevantamentoFiscalizacao().equals(TipoLevantamentoFiscalizacao.DENUNCIA);
    }

    public List<ProcessoFiscalizacao> recuperaProcessosReincidentes() {
        return listaReincidencias;

    }

    public String getNomeClasse() {
        if (selecionado.getTipoCadastroFiscalizacao() != null) {
            switch (selecionado.getTipoCadastroFiscalizacao()) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {


            selecionado.setCadastro((Cadastro) obj);
        } else if (obj instanceof Pessoa) {
            selecionado.setPessoa((Pessoa) obj);
        }
        carregaExisteReincidencia();
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (selecionado.getTipoCadastroFiscalizacao() != null) {
            switch (selecionado.getTipoCadastroFiscalizacao()) {
                case IMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case ECONOMICO:
                    PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                    List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
                    situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
                    situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
                    situacao.add(SituacaoCadastralCadastroEconomico.SUSPENSA);
                    componente.setSituacao(situacao);
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case PESSOA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public void removerTermoGeral(TermoGeralFiscalizacao termo) {
        selecionado.getTermoGeralFiscalizacao().remove(termo);
    }

    public void encerrarProcesso() {
        SituacaoProcessoFiscal spf = new SituacaoProcessoFiscal();
        spf.setProcessoFiscalizacao(selecionado);
        spf.setData(new Date());
        spf.setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao.ENCERRADO);
        selecionado.getSituacoesProcessoFiscalizacao().add(spf);
        encerramentoAutoInfracaoFiscalizacaoFacade.salvar(selecionado);
        FacesUtil.addOperacaoRealizada("O processo de fiscalização foi encerrado");
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public ConverterAutoComplete getConverterTarefa() {
        if (converterTarefa == null) {
            converterTarefa = new ConverterAutoComplete(TarefaAuditorFiscal.class, tarefaAuditorFiscalFacade);
        }
        return converterTarefa;
    }

    public List<TarefaAuditorFiscal> tarefas() {
        return tarefaAuditorFiscalFacade.listarTarefaOrdenandoPorCodigo(selecionado.getSecretariaFiscalizacao());
    }

    public ConverterAutoComplete getConverterInfracao() {
        if (converterInfracao == null) {
            converterInfracao = new ConverterAutoComplete(InfracaoFiscalizacaoSecretaria.class, infracaoFiscalizacaoSecretariaFacade);
        }
        return converterInfracao;
    }

    public List<InfracaoFiscalizacaoSecretaria> infracoes() {
        return infracaoFiscalizacaoSecretariaFacade.listarInfracaoOrdenandoPorCodigo(selecionado.getSecretariaFiscalizacao());
    }

    public ConverterAutoComplete getConverterPenalidade() {
        if (converterPenalidade == null) {
            converterPenalidade = new ConverterAutoComplete(PenalidadeFiscalizacaoSecretaria.class, penalidadeFiscalizacaoSecretariaFacade);
        }
        return converterPenalidade;
    }

    public List<PenalidadeFiscalizacaoSecretaria> penalidades() {
        return penalidadeFiscalizacaoSecretariaFacade.listarPenalidadesOrdenandoPorCodigo(selecionado.getSecretariaFiscalizacao());
    }

    private Boolean validaInformacoesParaAdicionarNovoItemProcessoFiscalizacao() {
        Boolean retorno = Boolean.TRUE;
        if (novoItemProcessoFiscalizacao.getTarefaAuditorFiscal() == null ||
            novoItemProcessoFiscalizacao.getTarefaAuditorFiscal().getId() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Tarefa do Auditor Fiscal não informada! Por favor informe a tarefa do auditor fiscal.");
        }
        if (novoItemProcessoFiscalizacao.getInfracaoFiscalizacaoSecretaria() == null ||
            novoItemProcessoFiscalizacao.getInfracaoFiscalizacaoSecretaria().getId() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Infração não informada! Por favor informe a infração.");
        }
        if (novoItemProcessoFiscalizacao.getPenalidadeFiscalizacaoSecretaria() == null ||
            novoItemProcessoFiscalizacao.getPenalidadeFiscalizacaoSecretaria().getId() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Penalidade não informada! Por favor informe a penalidade.");
        }
        if (novoItemProcessoFiscalizacao.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("Quantidade não pode ser inferior ou igual a 0(zero)! Por favor informe um valor maior que 0(zero).");
        }
        if (selecionado.getDataCadastro() == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addOperacaoNaoPermitida("A data da fiscalização deve ser informada.");
        }
        return retorno;
    }

    private void inicializarNovoItemProcessoFiscalizacao() {
        novoItemProcessoFiscalizacao = new ItemProcessoFiscalizacao();
        novoItemProcessoFiscalizacao.setQuantidade(new BigDecimal("1"));
    }

    public void adicionarNovoItemProcessoFiscalizacao() {
        if (!validaInformacoesParaAdicionarNovoItemProcessoFiscalizacao()) {
            return;
        }
        novoItemProcessoFiscalizacao.setProcessoFiscalizacao(selecionado);
        novoItemProcessoFiscalizacao.setValor(novoItemProcessoFiscalizacao.getPenalidadeFiscalizacaoSecretaria().getValor());
        if (selecionado.getItensProcessoFiscalizacao() == null) {
            selecionado.setItensProcessoFiscalizacao(new ArrayList());
        }
        selecionado.getItensProcessoFiscalizacao().add(novoItemProcessoFiscalizacao);
        inicializarNovoItemProcessoFiscalizacao();
    }

    public void limparLogradouroLocalOcorrencia() {
        selecionado.getLocalOcorrencia().getLogradouroBairro().setLogradouro(null);
    }

    public BigDecimal valorUFMVigente() {
        if (selecionado.getDataCadastro() != null) {
            return moedaFacade.recuperaValorUFMParaData(selecionado.getDataCadastro());
        }
        return BigDecimal.ZERO;

    }

    public Boolean desabilitaRemocaoInfracaoPenalidade() {
        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean desabilitaInsercaoInfracaoPenalidade() {
        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean desabilitaSelecaoSecretaria() {
        if (selecionado.getId() != null) {
            return Boolean.TRUE;
        }
        if (selecionado.getItensProcessoFiscalizacao() != null && selecionado.getItensProcessoFiscalizacao().size() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean desabilitaSelecaoDataDaFiscalizacao() {
        if (selecionado.getTermoAdvertencia() != null &&
            selecionado.getTermoAdvertencia().getDocumentoOficial() != null &&
            selecionado.getTermoAdvertencia().getDocumentoOficial().getId() != null) {
            return Boolean.TRUE;
        }
        if (selecionado.getAutoInfracaoFiscalizacao() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial() != null &&
            selecionado.getAutoInfracaoFiscalizacao().getDocumentoOficial().getId() != null) {
            return Boolean.TRUE;
        }
        if (selecionado.getItensProcessoFiscalizacao() != null &&
            selecionado.getItensProcessoFiscalizacao().size() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<ResultadoParcela> getRecuperaParcelas() {
        if (selecionado != null && selecionado.getId() != null) {
            CalculoProcFiscalizacao calculo = processoFiscalizacaoFacade.recuperaCalculo(selecionado);
            if (calculo != null) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
                return consultaParcela.executaConsulta().getResultados();
            }
        }
        return new ArrayList();
    }

    public DAM recuperaDAM(Long parcela) {
        if (parcela != null && parcela > 0) {
            return damFacade.recuperaDAMPeloIdParcela(parcela);
        }
        return null;
    }


    public boolean gerouCalculo(ProcessoFiscalizacao processoFiscalizacao) {
        if (processoFiscalizacao != null && processoFiscalizacao.getId() != null) {
            CalculoProcFiscalizacao calculo = processoFiscalizacaoFacade.recuperaCalculo(processoFiscalizacao);
            if (calculo != null && calculo.getId() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean desabilitaBotaoEditar(ProcessoFiscalizacao processoFiscalizacao) {
        ultimaSituacao(processoFiscalizacao);
        if (ultimaSituacao != null && (ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.ENCERRADO) ||
            ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.CANCELADO) ||
            ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.PAGO))) {
            return true;
        }
        return false;
    }

    public CancelamentoProcessoFiscal getCancelamentoProcessoFiscal() {
        return cancelamentoProcessoFiscal;
    }

    public void setCancelamentoProcessoFiscal(CancelamentoProcessoFiscal cancelamentoProcessoFiscal) {
        this.cancelamentoProcessoFiscal = cancelamentoProcessoFiscal;
    }

    public boolean existeParcelasDiferenteDeEmAberto(ProcessoFiscalizacao processoFiscalizacao) {
        boolean existe = false;
        CalculoProcFiscalizacao calculo = processoFiscalizacaoFacade.recuperaCalculo(processoFiscalizacao);
        if (calculo != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId());
            consultaParcela.executaConsulta();
            if (consultaParcela.getResultados() != null && consultaParcela.getResultados().size() > 0) {
                for (ResultadoParcela resultadoParcela : consultaParcela.getResultados()) {
                    if (!resultadoParcela.getSituacaoNameEnum().equals("EM_ABERTO")) {
                        //System.out.println("Existe");
                        existe = true;
                        break;
                    }
                }
            }
        }
        return existe;

    }

    public boolean podeCancelar() {
        boolean pode = true;
        ultimaSituacao(selecionado);
        if (ultimaSituacao.getStatusProcessoFiscalizacao().equals(StatusProcessoFiscalizacao.CANCELADO)) {
            pode = false;
            FacesUtil.addOperacaoNaoPermitida("Não foi possível continuar! O processo já está cancelado.");
        }
        if (existeParcelasDiferenteDeEmAberto(selecionado)) {
            pode = false;
            FacesUtil.addOperacaoNaoPermitida("Não foi possível continuar! O débito deste processo possui parcela(s) com situação diferente de EM ABERTO.");
        }
        return pode;
    }

    public void solicitaCancelarProcessoFiscalizacao() {
        if (podeCancelar()) {
            cancelamentoProcessoFiscal = new CancelamentoProcessoFiscal();
            cancelamentoProcessoFiscal.setDataHora(new Date());
            cancelamentoProcessoFiscal.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            FacesUtil.executaJavaScript("dialogCancelamento.show()");
        }
    }

    public boolean validaInformacoesCancelamento() {
        boolean retorno = true;
        if (cancelamentoProcessoFiscal.getMotivo() == null || cancelamentoProcessoFiscal.getMotivo().trim().isEmpty()) {
            retorno = false;
            FacesUtil.addCampoObrigatorio("O motivo deve ser informado.");
        }
        return retorno;
    }

    public void cancelarProcessoFiscalizacao() {
        try {
            if (validaInformacoesCancelamento()) {
                processoFiscalizacaoFacade.cancelaDebito(selecionado);
                selecionado.setCancelamentoProcessoFiscal(cancelamentoProcessoFiscal);
                adicionaSituacao(StatusProcessoFiscalizacao.CANCELADO);
                processoFiscalizacaoFacade.salva(selecionado);
                FacesUtil.addOperacaoRealizada("Cancelamento realizado.");
                FacesUtil.executaJavaScript("dialogCancelamento.hide()");
                Web.navegacao("", "/processo-de-fiscalizacao/listar-cancelamento/");
            }
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void fecharCancelamento() {
        Web.navegacao("/processo-de-fiscalizacao/ver-cancelamento/", "/processo-de-fiscalizacao/listar-cancelamento/");
    }

    public List<UsuarioSistema> completarFiscalDesignado(String filtro) {
        return processoFiscalizacaoFacade.completarFiscalDesignado(filtro);
    }
}
