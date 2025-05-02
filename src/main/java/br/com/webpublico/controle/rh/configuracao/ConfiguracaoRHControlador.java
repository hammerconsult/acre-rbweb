package br.com.webpublico.controle.rh.configuracao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.*;
import br.com.webpublico.entidades.rh.dirf.DirfUsuario;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 29/09/2015  11:50.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-configuracao-rh", pattern = "/configuracao/rh/novo/", viewId = "/faces/rh/configuracao/geral/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-rh", pattern = "/configuracao/rh/editar/#{configuracaoRHControlador.id}/", viewId = "/faces/rh/configuracao/geral/edita.xhtml"),
    @URLMapping(id = "ver-configuracao-rh", pattern = "/configuracao/rh/ver/#{configuracaoRHControlador.id}/", viewId = "/faces/rh/configuracao/geral/visualizar.xhtml"),
    @URLMapping(id = "listar-configuracao-rh", pattern = "/configuracao/rh/listar/", viewId = "/faces/rh/configuracao/geral/lista.xhtml")
})
public class ConfiguracaoRHControlador extends PrettyControlador<ConfiguracaoRH> implements Serializable, CRUD, ValidadorEntidade {
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private ConfiguracaoFaltasInjustificadas configuracaoFaltasInjustificadasSelecionada;
    private HoraExtraUnidade horaExtraUnidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private DirfCodigo dirfCodigo;
    private ConfiguracaoRelatorioRHBaseFP configuracaoRelatorioRHBaseFP;
    private ConverterAutoComplete converterBaseFP;
    private ConfiguracaoWebServiceRH configuracaoWebServiceRH;
    private ConfiguracaoRHClasseCredor configuracaoRHClasseCredor;
    private DirfUsuario dirfUsuario;


    public ConfiguracaoRHControlador() {
        super(ConfiguracaoRH.class);
    }

    public HoraExtraUnidade getHoraExtraUnidade() {
        return horaExtraUnidade;
    }

    public void setHoraExtraUnidade(HoraExtraUnidade horaExtraUnidade) {
        this.horaExtraUnidade = horaExtraUnidade;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoRHFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao/rh/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConfiguracaoWebServiceRH getConfiguracaoWebServiceRH() {
        return configuracaoWebServiceRH;
    }

    public void setConfiguracaoWebServiceRH(ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        this.configuracaoWebServiceRH = configuracaoWebServiceRH;
    }

    public DirfCodigo getDirfCodigo() {
        return dirfCodigo;
    }

    public void setDirfCodigo(DirfCodigo dirfCodigo) {
        this.dirfCodigo = dirfCodigo;
    }

    public DirfUsuario getDirfUsuario() {
        return dirfUsuario;
    }

    public void setDirfUsuario(DirfUsuario dirfUsuario) {
        this.dirfUsuario = dirfUsuario;
    }

    @Override
    public void salvar() {
        try {
            validarConfirmacao();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Campo Início de Vigência obrigatório.");
        }
        if (selecionado.getInicioVigencia() != null) {
            if (!DataUtil.isVigenciaValida(selecionado, configuracaoRHFacade.buscarConfiguracoesRH(selecionado))) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Erro na validação da Vigência.");
            }
        }
        if (selecionado.getConfiguracaoPrevidencia().getInicioRegistroIndividualizado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Início de Registro Individualizado de Contribuição' na aba 'Previdência' deve ser informado.");
        }
        if (selecionado.getHoraExtraMaximaPadrao() == null || selecionado.getHoraExtraMaximaPadrao().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Hora Extra máxima padrão na aba 'Total de Hora Extra' deve ser informado e deve ser maior que zero.");
        }
        if (StringUtils.isNotEmpty(selecionado.getVencimentoBasePortal()) && !baseFPFacade.existeCodigo(selecionado.getVencimentoBasePortal())) {
            ve.adicionarMensagemDeCampoObrigatorio("O código da BaseFP utilizado não existe, favor informar um código válido.");
        }
        if (selecionado.getDataObrigatoriedadeApuracaoIR() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de obrigatoriedade de Indicação de Apuração de IR (S1200) na aba E-social deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "nova-configuracao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        setConfiguracaoRelatorioRHBaseFP(new ConfiguracaoRelatorioRHBaseFP());
        configuracaoWebServiceRH = new ConfiguracaoWebServiceRH();
        dirfCodigo = new DirfCodigo();
        dirfUsuario = new DirfUsuario();
        cancelarConfiguracaoRHClasseCredor();
    }

    @URLAction(mappingId = "ver-configuracao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-configuracao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarConfiguracoes();
        setConfiguracaoRelatorioRHBaseFP(new ConfiguracaoRelatorioRHBaseFP());
        configuracaoWebServiceRH = new ConfiguracaoWebServiceRH();
        dirfCodigo = new DirfCodigo();
        dirfUsuario = new DirfUsuario();
        cancelarConfiguracaoRHClasseCredor();
    }


    private void carregarConfiguracoes() {
        if (selecionado.getConfiguracaoFP() == null) {
            selecionado.setConfiguracaoFP(new ConfiguracaoFP());
        }
        if (selecionado.getConfiguracaoRescisao() == null) {
            selecionado.setConfiguracaoRescisao(new ConfiguracaoRescisao());
        }
        if (selecionado.getConfiguracaoCreditoSalario() == null) {
            selecionado.setConfiguracaoCreditoSalario(new ConfiguracaoCreditoSalario());
        }
        if (selecionado.getConfiguracaoRelatorio() == null) {
            selecionado.setConfiguracaoRelatorio(new ConfiguracaoRelatorio());
        }
        carregarConfiguracaoPrevidencia();
    }

    private void carregarConfiguracaoPrevidencia() {
        if (selecionado.getConfiguracaoPrevidencia() == null) {
            selecionado.setConfiguracaoPrevidencia(new ConfiguracaoPrevidencia());
        }
    }

    public List<BaseFP> completarBaseFP(String filtro) {
        return getBaseFPFacade().buscarBasePFsPorCodigoOrDescricao(filtro);
    }

    public List<ConfiguracaoFaltasInjustificadas> recuperarConfiguracoesFaltasInjustificadas() {
        return configuracaoFaltasInjustificadasFacade.buscarConfiguracoesFaltasInjustificadasOrdenadasDecrescentementePorInicioVigencia();
    }

    public ConfiguracaoFaltasInjustificadas getConfiguracaoFaltasInjustificadasSelecionada() {
        return configuracaoFaltasInjustificadasSelecionada;
    }

    public void setConfiguracaoFaltasInjustificadasSelecionada(ConfiguracaoFaltasInjustificadas configuracaoFaltasInjustificadasSelecionada) {
        this.configuracaoFaltasInjustificadasSelecionada = configuracaoFaltasInjustificadasSelecionada;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void selecionarConfiguracaoFaltasInjustificadasAndRecuperarHierarquia(ConfiguracaoFaltasInjustificadas configuracaoFaltasInjustificadasSelecionada) {
        this.configuracaoFaltasInjustificadasSelecionada = configuracaoFaltasInjustificadasSelecionada;
        this.configuracaoFaltasInjustificadasSelecionada.setOperacao(Operacoes.EDITAR);
        recuperarHierarquia();
        removerConfiguracaoFaltasInjustificadas(configuracaoFaltasInjustificadasSelecionada);
    }


    public void recuperarHierarquia() {
        this.hierarquiaOrganizacional = configuracaoRHFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(this.configuracaoFaltasInjustificadasSelecionada.getUnidadeOrganizacional().getId());
    }

    public void removerConfiguracaoFaltasInjustificadas(ConfiguracaoFaltasInjustificadas configuracaoFaltasInjustificadasSelecionada) {
        selecionado.getConfiguracoesFaltasInjustificadas().remove(configuracaoFaltasInjustificadasSelecionada);
    }


    public void removerConfiguracaoRelatorioIncidenciaBases(ConfiguracaoRelatorioRHBaseFP configBase) {
        getSelecionado().getConfiguracaoRelatorio().getConfiguracaoRelatorioRHBaseFPs().remove(configBase);
    }

    public void novaConfiguracaoDeFaltas() {
        this.configuracaoFaltasInjustificadasSelecionada = new ConfiguracaoFaltasInjustificadas();
        this.configuracaoFaltasInjustificadasSelecionada.setConfiguracaoRH(selecionado);
    }

    public void novaConfiguracaoHoraExtraUnidade() {
        this.horaExtraUnidade = new HoraExtraUnidade();
    }

    public void adicionarBaseFP() {
        try {
            validarBaseFP();
            getConfiguracaoRelatorioRHBaseFP().setConfiguracaoRelatorio(getSelecionado().getConfiguracaoRelatorio());
            getSelecionado().getConfiguracaoRelatorio().getConfiguracaoRelatorioRHBaseFPs().add(getConfiguracaoRelatorioRHBaseFP());
            setConfiguracaoRelatorioRHBaseFP(new ConfiguracaoRelatorioRHBaseFP());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (getConfiguracaoRelatorioRHBaseFP().getBaseFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor, informe a base para adicionar");
        }
        validarAdicionarBaseFP(ve);
        ve.lancarException();
    }

    private void validarAdicionarBaseFP(ValidacaoException ve) {
        if (getConfiguracaoRelatorioRHBaseFP().getBaseFP() != null) {
            if (!CollectionUtils.isEmpty(getSelecionado().getConfiguracaoRelatorio().getConfiguracaoRelatorioRHBaseFPs())) {
                for (ConfiguracaoRelatorioRHBaseFP relatorioRHBaseFP : getSelecionado().getConfiguracaoRelatorio().getConfiguracaoRelatorioRHBaseFPs()) {
                    if (relatorioRHBaseFP.getBaseFP().equals(getConfiguracaoRelatorioRHBaseFP().getBaseFP())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Base " + getConfiguracaoRelatorioRHBaseFP().getBaseFP() + " já foi adicionada.");
                    }
                }
            }
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquias(String parte) {
        return configuracaoRHFacade.getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        return new ConverterAutoComplete(HierarquiaOrganizacional.class, configuracaoRHFacade.getHierarquiaOrganizacionalFacade());
    }

    public void atribuirOrgao(SelectEvent event) {
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) event.getObject();
        this.configuracaoFaltasInjustificadasSelecionada.setUnidadeOrganizacional(ho.getSubordinada());
    }

    public ConverterAutoComplete getConverterReferenciaFP() {
        return new ConverterAutoComplete(ReferenciaFP.class, referenciaFPFacade);
    }

    public List<SelectItem> getReferenciasFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ReferenciaFP object : referenciaFPFacade.buscarReferenciasOrdenadasPorCodigo()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposArquivo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoArquivoESocial object : TipoArquivoESocial.values()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public void confirmarConfiguracaoDeFaltaInjustificada() {
        if (podeConfirmarConfiguracaoDeFaltasInjustificada()) {
            adicionarConfiguracaoFaltasInjustificadasSelecionada();
            cancelarConfiguracaoDeFaltaInjustificada();
        }
    }

    public void cancelarConfiguracaoMaximaHoraExtraOrgao() {
        horaExtraUnidade = null;
    }

    public void removerConfiguracaoMaximaHoraExtraOrgao(HoraExtraUnidade horaExtra) {
        selecionado.getHoraExtraUnidade().remove(horaExtra);
    }

    public void adicionarConfiguracaoMaximaHoraExtraOrgao() {
        try {
            validarConfiguracaoMaximaHoraExtraOrgao();
            horaExtraUnidade.setConfiguracaoRH(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getHoraExtraUnidade(), horaExtraUnidade);
            horaExtraUnidade = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void encerrarVigencia(HoraExtraUnidade horaExtra) {
        horaExtra.setFimVigencia(new Date());
    }


    private void validarConfiguracaoMaximaHoraExtraOrgao() {
        ValidacaoException ve = new ValidacaoException();
        if (horaExtraUnidade.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Vigência Inicial.");
        }
        if ((horaExtraUnidade.getInicioVigencia() != null && horaExtraUnidade.getFimVigencia() != null) && horaExtraUnidade.getInicioVigencia().after(horaExtraUnidade.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final não pode ser maior que a Data Final");
        }
        if (horaExtraUnidade.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Órgão.");
        }
        if (horaExtraUnidade.getHoraExtra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade de Hora Extra.");
        } else if (horaExtraUnidade.getHoraExtra().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de Hora Extra deve ser maior que Zero.");
        }

        if (!selecionado.getHoraExtraUnidade().isEmpty() && ve.getMensagens().isEmpty()) {
            for (HoraExtraUnidade horaAdicionada : selecionado.getHoraExtraUnidade()) {
                if (horaAdicionada.getHierarquiaOrganizacional().equals(horaExtraUnidade.getHierarquiaOrganizacional())) {
                    if (horaAdicionada.getFimVigencia() == null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(horaExtraUnidade.getHierarquiaOrganizacional() + " já adicionada vigente, encerre a vigência para continuar.");
                        break;
                    }
                    if (horaAdicionada.getInicioVigencia().compareTo(horaExtraUnidade.getInicioVigencia()) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse Órgão já possui registro com Início de Vigência para o dia " + DataUtil.getDataFormatada(horaAdicionada.getInicioVigencia()) + ".");
                        break;
                    }
                    if (horaAdicionada.getFimVigencia().after(horaExtraUnidade.getInicioVigencia()) || horaAdicionada.getFimVigencia().compareTo(horaExtraUnidade.getInicioVigencia()) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Esse Órgão já possui registro vigênte para o dia " + DataUtil.getDataFormatada(horaAdicionada.getFimVigencia()) + ".");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarConfiguracaoFaltasInjustificadasSelecionada() {
        selecionado.setConfiguracoesFaltasInjustificadas(Util.adicionarObjetoEmLista(selecionado.getConfiguracoesFaltasInjustificadas(), this.configuracaoFaltasInjustificadasSelecionada));
    }

    private boolean podeConfirmarConfiguracaoDeFaltasInjustificada() {
        if (!Util.validaCampos(this.configuracaoFaltasInjustificadasSelecionada)) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(this.configuracaoFaltasInjustificadasSelecionada, getConfiguracoesFaltasInjustificadasPorCodigo2NivelDaUnidadeOrganizacional(this.configuracaoFaltasInjustificadasSelecionada.getUnidadeOrganizacional()))) {
            return false;
        }
        return true;
    }

    public void adicionarConfiguracaoRHClasseCredor() {
        try {
            validarClasseCredor();
            configuracaoRHClasseCredor.setConfiguracaoRH(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getConfiguracoesClassesCredor(), configuracaoRHClasseCredor);
            cancelarConfiguracaoRHClasseCredor();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void instanciarConfiguracaoRHClasseCredor() {
        configuracaoRHClasseCredor = new ConfiguracaoRHClasseCredor();
    }

    public void cancelarConfiguracaoRHClasseCredor() {
        configuracaoRHClasseCredor = null;
    }

    private void validarClasseCredor() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoRHClasseCredor.getClasseCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Classe de Pessoa deve ser informado.");
        }
        ve.lancarException();
        for (ConfiguracaoRHClasseCredor rhClasseCredor : selecionado.getConfiguracoesClassesCredor()) {
            if (configuracaoRHClasseCredor.getClasseCredor().equals(rhClasseCredor.getClasseCredor()) && !configuracaoRHClasseCredor.equals(rhClasseCredor)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Classe de Pessoa selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public void editarConfiguracaoRHClasseCredor(ConfiguracaoRHClasseCredor configuracaoRHClasseCredor) {
        this.configuracaoRHClasseCredor = (ConfiguracaoRHClasseCredor) Util.clonarObjeto(configuracaoRHClasseCredor);
    }

    public void removerConfiguracaoRHClasseCredor(ConfiguracaoRHClasseCredor configuracaoRHClasseCredor) {
        selecionado.getConfiguracoesClassesCredor().remove(configuracaoRHClasseCredor);
    }

    public List<ClasseCredor> buscarClassesCredor(String filtro) {
        return configuracaoRHFacade.buscarClassesCredor(filtro);
    }

    private List<ConfiguracaoFaltasInjustificadas> getConfiguracoesFaltasInjustificadasPorCodigo2NivelDaUnidadeOrganizacional(UnidadeOrganizacional uoQueSeraAdicionada) {
        List<ConfiguracaoFaltasInjustificadas> configuracoes = new ArrayList<>();
        for (ConfiguracaoFaltasInjustificadas cfi : selecionado.getConfiguracoesFaltasInjustificadas()) {
            if (getCodigo2NivelPorUnidadeOrganizacional(cfi.getUnidadeOrganizacional()).equals(getCodigo2NivelPorUnidadeOrganizacional(uoQueSeraAdicionada))) {
                configuracoes.add(cfi);
            }
        }
        return configuracoes;
    }

    public String getCodigo2NivelPorUnidadeOrganizacional(UnidadeOrganizacional uo) {
        return configuracaoRHFacade.getHierarquiaOrganizacionalFacade().recuperaHierarquiaOrganizacionalPelaUnidade(uo.getId()).getCodigoDo2NivelDeHierarquia();
    }

    public void cancelarConfiguracaoDeFaltaInjustificada() {
        if (this.configuracaoFaltasInjustificadasSelecionada.isEditando()) {
            adicionarConfiguracaoFaltasInjustificadasSelecionada();
        }
        this.configuracaoFaltasInjustificadasSelecionada = null;
        this.hierarquiaOrganizacional = null;
    }

    public List<SelectItem> getEventos() {
        return Util.getListSelectItem(eventoFPFacade.listaEventosAtivosFolha());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeConta.values(), false);
    }

    public ConfiguracaoRelatorioRHBaseFP getConfiguracaoRelatorioRHBaseFP() {
        return configuracaoRelatorioRHBaseFP;
    }

    public void setConfiguracaoRelatorioRHBaseFP(ConfiguracaoRelatorioRHBaseFP configuracaoRelatorioRHBaseFP) {
        this.configuracaoRelatorioRHBaseFP = configuracaoRelatorioRHBaseFP;
    }

    public ConverterAutoComplete getConverterBaseFP() {
        return new ConverterAutoComplete(BaseFP.class, getBaseFPFacade());
    }

    public void setConverterBaseFP(ConverterAutoComplete converterBaseFP) {
        this.converterBaseFP = converterBaseFP;
    }

    public BaseFPFacade getBaseFPFacade() {
        return baseFPFacade;
    }

    public Date getMinimaDataParaInicioVigencia() {
        return sistemaFacade.getDataOperacao();
    }

    public List<SelectItem> getTipoWebService() {
        return Util.getListSelectItem(TipoWebService.values());
    }

    public void alterarConfiguracaoWebService(ConfiguracaoWebServiceRH config) {
        configuracaoWebServiceRH = (ConfiguracaoWebServiceRH) Util.clonarEmNiveis(config, 1);

    }

    public void removerConfiguracaoWebService(ConfiguracaoWebServiceRH config) {
        selecionado.getConfiguracaoWebServiceRH().remove(config);
    }

    public void adicionarConfiguracaoWebService() {
        try {
            validarConfiguracaoWebService(configuracaoWebServiceRH);
            configuracaoWebServiceRH.setConfiguracaoRH(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getConfiguracaoWebServiceRH(), configuracaoWebServiceRH);
            configuracaoWebServiceRH = new ConfiguracaoWebServiceRH();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarConfiguracaoWebService(ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoWebServiceRH.getTipoWebService() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo.");
        }
        if (Strings.isNullOrEmpty(configuracaoWebServiceRH.getChave())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Chave.");
        }
        if (Strings.isNullOrEmpty(configuracaoWebServiceRH.getUrl())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a URL.");
        }
        ve.lancarException();
    }

    public ConfiguracaoRHClasseCredor getConfiguracaoRHClasseCredor() {
        return configuracaoRHClasseCredor;
    }

    public void setConfiguracaoRHClasseCredor(ConfiguracaoRHClasseCredor configuracaoRHClasseCredor) {
        this.configuracaoRHClasseCredor = configuracaoRHClasseCredor;
    }

    public void adicionarConfiguracaoDIRF() {
        try {
            validarConfiguracaoDIRF();
            dirfCodigo.setConfiguracaoRH(selecionado);
            selecionado.getItemDirfCodigo().add(dirfCodigo);
            dirfCodigo = new DirfCodigo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerConfiguracaoDIRF(DirfCodigo dirf) {
        selecionado.getItemDirfCodigo().remove(dirf);
    }

    private void validarConfiguracaoDIRF() {
        ValidacaoException ve = new ValidacaoException();
        if (dirfCodigo.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o ano da Configuração da Dirf.");
        }
        if (Strings.isNullOrEmpty(dirfCodigo.getCodigo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o código da Configuração da Dirf.");
        }
        ve.lancarException();
        for (DirfCodigo itemDirfCodigo : selecionado.getItemDirfCodigo()) {
            if (itemDirfCodigo.getExercicio().getAno().equals(dirfCodigo.getExercicio().getAno())) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("Exercício já cadastrado");
                break;
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDePlataformas() {
        return Util.getListSelectItem(TipoDePlataforma.values());
    }

    public List<SelectItem> getTiposTercoFeriasAutomatico() {
        return Util.getListSelectItem(TipoTercoFeriasAutomatico.values());
    }

    public void adicionarUsuarioDirf() {
        dirfUsuario.setConfiguracaoRH(selecionado);
        selecionado.getItemDirfUsuario().add(dirfUsuario);
        dirfUsuario = new DirfUsuario();
    }

    public void removerUsuarioDirf(DirfUsuario dirfUsuario) {
        selecionado.getItemDirfUsuario().remove(dirfUsuario);
    }

}
