package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosConsultaFiscalizacaoRBTrans;
import br.com.webpublico.entidadesauxiliares.VOCalculo;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FiscalizacaoRBTransFacade;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "fiscalizacaoRBTransControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarFiscalizacao",
        pattern = "/fiscalizacao-de-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/fiscalizacao/processoautuacao/lista.xhtml"),
    @URLMapping(id = "criarFiscalizacao",
        pattern = "/fiscalizacao-de-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/fiscalizacao/processoautuacao/edita.xhtml"),
    @URLMapping(id = "editarFiscalizacao",
        pattern = "/fiscalizacao-de-transporte/editar/#{fiscalizacaoRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/fiscalizacao/processoautuacao/edita.xhtml"),
}
)
public class FiscalizacaoRBTransControlador extends PrettyControlador implements Serializable, CRUD {

    @EJB
    private FiscalizacaoRBTransFacade fiscalizacaoRBTransFacade;
    private FormFiscalizacaoRBTrans form;
    public ConverterAutoComplete converterPessoaFisica;
    public ConverterAutoComplete converterOcorrenciaFiscalizacaoRBTrans;
    public ConverterGenerico converterCidade;
    public ConverterAutoComplete converterUsuarioSistema;
    public ConverterAutoComplete converterCadastroEconomico;
    public ConverterAutoComplete converterAgenteAutuador;

    public FormFiscalizacaoRBTrans getForm() {
        return form;
    }

    public void setForm(FormFiscalizacaoRBTrans form) {
        this.form = form;
    }

    @URLAction(mappingId = "criarFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        form = (FormFiscalizacaoRBTrans) Web.pegaDaSessao(FormFiscalizacaoRBTrans.class);
        if (form == null) {
            form = new FormFiscalizacaoRBTrans();
            validaParamentros();
            operacao = Operacoes.NOVO;
            form.setFiscalizacaoRBTrans(new FiscalizacaoRBTrans());
            form.getFiscalizacaoRBTrans().setAutuacaoFiscalizacao(new AutuacaoFiscalizacaoRBTrans());
            form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setVeiculoTransporte(null);
            form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setMotoristaInfrator(null);
            form.setMotorista(null);
            form.setEnderecosMotorista(new ArrayList<EnderecoCorreio>());
            form.setOcorrenciasFiscalizacaoRBTrans(new ArrayList<OcorrenciaFiscalizacaoRBTrans>());
            form.setVeiculoTransporte(null);
            form.getFiscalizacaoRBTrans().setInfratorReincidente(false);
            form.setEnderecosPermissionario(new ArrayList<EnderecoCorreio>());
            getAbaAtiva();
            form.setOcorrenciaSelecionada(null);
            form.getFiscalizacaoRBTrans().setSituacoesFiscalizacao(new ArrayList<SituacaoFiscalizacaoRBTrans>());
            form.setRecursoRBTrans(new RecursoRBTrans());
            form.setRecursoJARI(new RecursoRBTrans());
            form.recursoConselho = new RecursoRBTrans();
            form.setAnaliseAutuacao(new AnaliseAutuacaoRBTrans());
            form.setAnaliseRecurso(new AnaliseRecursoRBTrans());
            form.setAnaliseRecursoJARI(new AnaliseRecursoRBTrans());
            form.setAnaliseRecursoConselho(new AnaliseRecursoRBTrans());
            form.setNotificacaoAutuacaoRBTrans(new NotificacaoRBTrans());
            form.setNotificacaoPenalidade(new NotificacaoRBTrans());
            form.setArquivo(new Arquivo());
            form.setArquivoCancelamento(new Arquivo());
            form.setFiscalizacaoArquivoRBTransRecurso(new ArrayList<FiscalizacaoArquivoRBTrans>());
            form.setFiscalizacaoArquivoRBTransRecursoJARI(new ArrayList<FiscalizacaoArquivoRBTrans>());
            form.setFiscalizacaoArquivoRBTransRecursoCONSELHO(new ArrayList<FiscalizacaoArquivoRBTrans>());
            form.setFiscalizacaoArquivoRBTransCancelamentoMulta(new ArrayList<FiscalizacaoArquivoRBTrans>());
            form.setCamposInconsistentes(new ArrayList<CampoInconsistenciaAutoInfracaoRBTrans>());
            form.setCamposInconsistentes(carregaListaCamposInconsistentes());
        }
    }

    @URLAction(mappingId = "editarFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void selecionar() {
        form = (FormFiscalizacaoRBTrans) Web.pegaDaSessao(FormFiscalizacaoRBTrans.class);
        if (form == null) {
            novo();
            operacao = Operacoes.EDITAR;
            form.setFiscalizacaoRBTrans(fiscalizacaoRBTransFacade.recuperar(getId()));
            if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans() != null) {
                form.setAnaliseAutuacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getAnaliseAutuacaoRBTrans());
            }
            for (OcorrenciaAutuacaoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao()) {
                form.getOcorrenciasFiscalizacaoRBTrans().add(obj.getOcorrenciaFiscalizacao());
            }
            form.setOcorrenciaSelecionada(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao().get(0).getOcorrenciaFiscalizacao());
            form.setMotorista(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getMotoristaInfrator());
            recuperaNotificacaoes();
            recuperaRecursos();
            if (form.getMotorista() != null) {
                form.setMotorista(fiscalizacaoRBTransFacade.getPessoaFisicaFacade().recuperar(form.getMotorista().getId()));
                form.setCnhMotorista(form.getMotorista().getCNH() != null ? form.getMotorista().getCNH().toString() : "");
                form.setEnderecosMotorista(fiscalizacaoRBTransFacade.getEnderecoCorreioFacade().recuperaEnderecosPessoa(form.getMotorista()));
            }
            form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setMotoristaInfrator(form.getMotorista());
        }
    }

    @URLAction(mappingId = "listarFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        form.setFiltros(new FiltrosConsultaFiscalizacaoRBTrans());
    }

    public void validaParamentros() {
        if (fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Não existe parâmetro de fiscalização cadastrado"));
            redireciona();
        }
    }


    @Override
    public String getCaminhoPadrao() {
        return "/fiscalizacao-de-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return form.getFiscalizacaoRBTrans().getId();
    }


    public FiscalizacaoRBTransControlador() {
        form = new FormFiscalizacaoRBTrans();
        setListaFiscalizacoes(new ArrayList<FiscalizacaoRBTrans>());
    }

    @Override
    public AbstractFacade getFacede() {
        return fiscalizacaoRBTransFacade;
    }

    public List<CampoInconsistenciaAutoInfracaoRBTrans> carregaListaCamposInconsistentes() {
        List<CampoInconsistenciaAutoInfracaoRBTrans> toReturn = new ArrayList<>();
        for (CamposAutoInfracao obj : CamposAutoInfracao.values()) {
            CampoInconsistenciaAutoInfracaoRBTrans campo = new CampoInconsistenciaAutoInfracaoRBTrans();
            campo.setCamposAutoInfracao(obj);
            toReturn.add(campo);
        }
        return toReturn;

    }

    public boolean isCampoInconsistenciaSelecionado(CampoInconsistenciaAutoInfracaoRBTrans campo) {
        for (CampoInconsistenciaAutoInfracaoRBTrans obj : form.getAnaliseAutuacao().getCamposInconsistentes()) {
            if (campo.getCamposAutoInfracao().equals(obj.getCamposAutoInfracao())) {
                return true;
            }
        }
        return false;
    }

    public List<CampoInconsistenciaAutoInfracaoRBTrans> getCamposInconsistentes() {
        return form.getCamposInconsistentes();
    }

    public void setCamposInconsistentes(List<CampoInconsistenciaAutoInfracaoRBTrans> camposInconsistentes) {
        form.setCamposInconsistentes(camposInconsistentes);
    }

    public OcorrenciaFiscalizacaoRBTrans getOcorrenciaSelecionada() {
        return form.getOcorrenciaSelecionada();
    }

    public void setOcorrenciaSelecionada(OcorrenciaFiscalizacaoRBTrans ocorrenciaSelecionada) {
        form.setOcorrenciaSelecionada(ocorrenciaSelecionada);
    }

    public Arquivo getArquivoCancelamento() {
        return form.getArquivoCancelamento();
    }

    public void setArquivoCancelamento(Arquivo arquivoCancelamento) {
        form.setArquivoCancelamento(arquivoCancelamento);
    }

    public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransCancelamentoMulta() {
        return form.getFiscalizacaoArquivoRBTransCancelamentoMulta();
    }

    public void setFiscalizacaoArquivoRBTransCancelamentoMulta(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransCancelamentoMulta) {
        form.setFiscalizacaoArquivoRBTransCancelamentoMulta(fiscalizacaoArquivoRBTransCancelamentoMulta);
    }

    public AnaliseRecursoRBTrans getAnaliseRecursoJARI() {
        return form.getAnaliseRecursoJARI();
    }

    public void setAnaliseRecursoJARI(AnaliseRecursoRBTrans analiseRecursoJARI) {
        form.setAnaliseRecursoJARI(analiseRecursoJARI);
    }

    public RecursoRBTrans getRecursoConselho() {
        return form.getRecursoConselho();
    }

    public void setRecursoConselho(RecursoRBTrans recursoConselho) {
        form.setRecursoConselho(recursoConselho);
    }

    public AnaliseRecursoRBTrans getAnaliseRecursoConselho() {
        return form.getAnaliseRecursoConselho();
    }

    public void setAnaliseRecursoConselho(AnaliseRecursoRBTrans analiseRecursoConselho) {
        form.setAnaliseRecursoConselho(analiseRecursoConselho);
    }

    public List<FiscalizacaoRBTrans> getListaFiscalizacoes() {
        return form.getListaFiscalizacoes();
    }

    public void setListaFiscalizacoes(List<FiscalizacaoRBTrans> listaFiscalizacoes) {
        form.setListaFiscalizacoes(listaFiscalizacoes);
    }

    public FiltrosConsultaFiscalizacaoRBTrans getFiltros() {
        return form.getFiltros();
    }

    public void setFiltros(FiltrosConsultaFiscalizacaoRBTrans filtros) {
        form.setFiltros(filtros);
    }

    public NotificacaoRBTrans getNotificacaoPenalidade() {
        return form.getNotificacaoPenalidade();
    }

    public void setNotificacaoPenalidade(NotificacaoRBTrans notificacaoPenalidade) {
        form.setNotificacaoPenalidade(notificacaoPenalidade);
    }

    public RecursoRBTrans getRecursoJARI() {
        return form.getRecursoJARI();
    }

    public void setRecursoJARI(RecursoRBTrans recursoJARI) {
        form.setRecursoJARI(recursoJARI);
    }

    public AnaliseRecursoRBTrans getAnaliseRecurso() {
        return form.getAnaliseRecurso();
    }

    public void setAnaliseRecurso(AnaliseRecursoRBTrans analiseRecurso) {
        form.setAnaliseRecurso(analiseRecurso);
    }

    public RecursoRBTrans getRecursoRBTrans() {
        return form.getRecursoRBTrans();
    }

    public void setRecursoRBTrans(RecursoRBTrans recursoRBTrans) {
        form.setRecursoRBTrans(recursoRBTrans);
    }

    public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransRecurso() {
        return form.getFiscalizacaoArquivoRBTransRecurso();
    }

    public void setFiscalizacaoArquivoRBTransRecurso(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecurso) {
        form.setFiscalizacaoArquivoRBTransRecurso(fiscalizacaoArquivoRBTransRecurso);
    }

    public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransRecursoJARI() {
        return form.getFiscalizacaoArquivoRBTransRecursoJARI();
    }

    public void setFiscalizacaoArquivoRBTransRecursoJARI(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecursoJARI) {
        form.setFiscalizacaoArquivoRBTransRecursoJARI(fiscalizacaoArquivoRBTransRecursoJARI);
    }

    public Arquivo getArquivoRecurso() {
        return form.getArquivoRecurso();
    }

    public void setArquivoRecurso(Arquivo arquivoRecurso) {
        form.setArquivoRecurso(arquivoRecurso);
    }

    public Arquivo getArquivo() {
        return form.getArquivo();
    }

    public void setArquivo(Arquivo arquivo) {
        form.setArquivo(arquivo);
    }

    public NotificacaoRBTrans getNotificacaoAutuacaoRBTrans() {
        return form.getNotificacaoAutuacaoRBTrans();
    }

    public void setNotificacaoAutuacaoRBTrans(NotificacaoRBTrans notificacaoAutuacaoRBTrans) {
        form.setNotificacaoAutuacaoRBTrans(notificacaoAutuacaoRBTrans);
    }

    public AnaliseAutuacaoRBTrans getAnaliseAutuacao() {
        return form.getAnaliseAutuacao();
    }

    public void setAnaliseAutuacao(AnaliseAutuacaoRBTrans analiseAutuacao) {
        form.setAnaliseAutuacao(analiseAutuacao);
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, fiscalizacaoRBTransFacade.getPessoaFisicaFacade());
        }
        return converterPessoaFisica;
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, fiscalizacaoRBTransFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public Converter getConverterOcorrenciaFiscalizacaoRBTrans() {
        if (converterOcorrenciaFiscalizacaoRBTrans == null) {
            converterOcorrenciaFiscalizacaoRBTrans = new ConverterAutoComplete(OcorrenciaFiscalizacaoRBTrans.class, fiscalizacaoRBTransFacade.getOcorrenciaFiscalizacaoRBTransFacade());
        }
        return converterOcorrenciaFiscalizacaoRBTrans;
    }

    public Converter getConverterUsuarioSistema() {
        if (converterUsuarioSistema == null) {
            converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, fiscalizacaoRBTransFacade.getUsuarioSistemaFacade());
        }
        return converterUsuarioSistema;
    }

    public ConverterAutoComplete getConverterAgenteAutuador() {
        if (converterAgenteAutuador == null) {
            converterAgenteAutuador = new ConverterAutoComplete(AgenteAutuador.class, fiscalizacaoRBTransFacade.getAgenteAutuadorFacade());
        }
        return converterAgenteAutuador;
    }

    public SituacaoFiscalizacaoRBTrans getSituacaoAtual() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (obj.isVigente()) {
                    return obj;
                }
            }
        }
        return null;
    }


    public String getDescricaoSituacaoAtual() {
        for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
            if (StatusFiscalizacaoRBTrans.ARQUIVADO.equals(obj.getStatusFiscalizacao()) || StatusFiscalizacaoRBTrans.FINALIZADO.equals(obj.getStatusFiscalizacao())) {
                return obj.getStatusFiscalizacao().getDescricao();
            }
            if (obj.isVigente()) {
                return obj.getStatusFiscalizacao().getDescricao() + " até - " + new SimpleDateFormat("dd/MM/yyyy").format(obj.getDataFinal());
            }
        }
        return "Novo Processo de Autuação";
    }

    public Converter getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterGenerico(Cidade.class, fiscalizacaoRBTransFacade.getCidadeFacade());
        }
        return converterCidade;
    }

    public List<OcorrenciaFiscalizacaoRBTrans> getOcorrenciasFiscalizacaoRBTrans() {
        return form.getOcorrenciasFiscalizacaoRBTrans();
    }

    public void setOcorrenciasFiscalizacaoRBTrans(List<OcorrenciaFiscalizacaoRBTrans> ocorrenciasFiscalizacaoRBTrans) {
        form.setOcorrenciasFiscalizacaoRBTrans(ocorrenciasFiscalizacaoRBTrans);
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return fiscalizacaoRBTransFacade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public List<OcorrenciaFiscalizacaoRBTrans> completaOcorrencias(String parte) {
        return fiscalizacaoRBTransFacade.getOcorrenciaFiscalizacaoRBTransFacade().recuperaOcorrenciasPorTipo(parte.trim(), getFiscalizacaoRBTrans().getEspecieTransporte());
    }

    public List<UsuarioSistema> completaAgenteAutuador(String parte) {
        return fiscalizacaoRBTransFacade.getUsuarioSistemaFacade().listaFiltrandoUsuarioSistemaPorTipo2(parte.trim(),
            TipoUsuarioTributario.FISCAL_TRANSITO,
            TipoUsuarioTributario.FISCAL_TRANSPORTE,
            TipoUsuarioTributario.FISCAL_TRANSITO_TRANSPORTE);
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return fiscalizacaoRBTransFacade.listaCadastrosEconomicosPermissionarioEOutorgas(parte.trim().toLowerCase());
    }

    public List<EnderecoCorreio> getEnderecosMotorista() {
        return form.getEnderecosMotorista();
    }

    public void setEnderecosMotorista(List<EnderecoCorreio> enderecosMotorista) {
        form.setEnderecosMotorista(enderecosMotorista);
    }

    public PessoaFisica getMotorista() {
        return form.getMotorista();
    }

    public void setMotorista(PessoaFisica motorista) {
        form.setMotorista(motorista);
    }

    public FiscalizacaoRBTransFacade getFiscalizacaoRBTransFacade() {
        return fiscalizacaoRBTransFacade;
    }

    public void setFiscalizacaoRBTransFacade(FiscalizacaoRBTransFacade fiscalizacaoRBTransFacade) {
        this.fiscalizacaoRBTransFacade = fiscalizacaoRBTransFacade;
    }

    public void limpaMotorista() {
        form.setMotorista(null);
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setMotoristaInfrator(null);
    }

    public String getCorStatus() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            Calendar dataHoje = Calendar.getInstance();
            dataHoje.setTime(new Date());
            Calendar dataFinalSituacao = Calendar.getInstance();
            dataFinalSituacao.setTime(getSituacaoAtual().getDataFinal());
            if (dataFinalSituacao.getTimeInMillis() >= dataHoje.getTimeInMillis()) {
                return "#228B22";
            } else {
                return "#F00";
            }
        } else {
            return "#228B22";
        }
    }

    public Habilitacao getCNH() {
        for (DocumentoPessoal obj : fiscalizacaoRBTransFacade.getPessoaFisicaFacade().recuperarDocumentosPessoais(form.getMotorista())) {
            if (obj instanceof Habilitacao) {
                return (Habilitacao) obj;
            }
        }
        return null;
    }

    public void setVeiculoTransporte(VeiculoTransporte veiculo) {
        form.setVeiculoTransporte(veiculo);
    }

    public List<EnderecoCorreio> getEnderecosPermissionario() {
        return form.getEnderecosPermissionario();
    }

    public void setEnderecosPermissionario(List<EnderecoCorreio> enderecosPermissionario) {
        form.setEnderecosPermissionario(enderecosPermissionario);
    }

    public FiscalizacaoRBTrans getFiscalizacaoRBTrans() {
        return form.getFiscalizacaoRBTrans();
    }

    public String getCnhMotorista() {
        return form.getCnhMotorista();
    }

    public void setCnhMotorista(String cnhMotorista) {
        form.setCnhMotorista(cnhMotorista);
    }

    public void setFiscalizacaoRBTrans(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
        form.setFiscalizacaoRBTrans(fiscalizacaoRBTrans);
    }

    public List<SelectItem> getOrgaosAutuadores() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OrgaoAutuadorRBTrans obj : OrgaoAutuadorRBTrans.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacaoesFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (StatusFiscalizacaoRBTrans obj : StatusFiscalizacaoRBTrans.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getEspeciesTransporte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EspecieTransporte obj : EspecieTransporte.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getPareceresAnaliseAutuacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ParecerAnalise obj : ParecerAnalise.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDeCiencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCiencia obj : TipoCiencia.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        ConfiguracaoTributario configuracaoTributario = fiscalizacaoRBTransFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        toReturn.add(new SelectItem(configuracaoTributario.getCidade(), configuracaoTributario.getCidade().getNome()));
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setCidadeInfracao(configuracaoTributario.getCidade());
        return toReturn;
    }

    public void recuperaNotificacaoes() {
        if (!form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes().isEmpty()) {
            for (NotificacaoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes()) {
                if (TipoProcessoRBTrans.AUTUACAO.equals(obj.getTipoProcessoRBTrans())) {
                    form.setNotificacaoAutuacaoRBTrans(obj);
                }
                if (TipoProcessoRBTrans.PENALIDADE.equals(obj.getTipoProcessoRBTrans())) {
                    form.setNotificacaoPenalidade(obj);
                }
            }
        }
    }

    public void recuperaRecursos() {
        if (!form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getRecursosRBTrans().isEmpty()) {
            for (RecursoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getRecursosRBTrans()) {
                if (TipoProcessoRBTrans.AUTUACAO.equals(obj.getTipoProcessoRBTrans())) {
                    form.setRecursoRBTrans(obj);
                    ;
                }
                if (TipoProcessoRBTrans.PENALIDADE.equals(obj.getTipoProcessoRBTrans())) {
                    form.setRecursoJARI(obj);
                }
                if (TipoProcessoRBTrans.CONSELHO.equals(obj.getTipoProcessoRBTrans())) {
                    form.setRecursoConselho(obj);
                }
            }
        }
    }

    public void atribuiDataAutuacao(DateSelectEvent evt) {
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setDataAutuacao(evt.getDate());
    }

    public void atribuiDataCienciaNotificacao(DateSelectEvent evt) {
        form.getNotificacaoAutuacaoRBTrans().setDataCiencia(evt.getDate());
    }

    public void atribuirVeiculoTransporte(SelectEvent evt) {
        form.setVeiculoTransporte((VeiculoTransporte) evt.getObject());
    }

    public void atribuirCadastroEconomico(SelectEvent evt) {
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setCadastroEconomico((CadastroEconomico) evt.getObject());
    }

    public void atribuiPessoaMotorista(SelectEvent evt) {
        form.setMotorista((PessoaFisica) evt.getObject());
        form.setMotorista(fiscalizacaoRBTransFacade.getPessoaFisicaFacade().recuperar(form.getMotorista().getId()));
        form.setEnderecosMotorista(fiscalizacaoRBTransFacade.getEnderecoCorreioFacade().recuperaEnderecosPessoa(form.getMotorista()));
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setMotoristaInfrator(form.getMotorista());
    }

    public int getAbaAtiva() {
        if (getSituacaoAtual() != null) {
            if (StatusFiscalizacaoRBTrans.ANALISE_AUTUACAO.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 1;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 2;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 3;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 4;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 5;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO_JARI.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 6;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_JARI.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 7;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_CANCELAMENTO_MULTA_JARI.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 8;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_PAGAMENTO_MULTA.equals(getSituacaoAtual().getStatusFiscalizacao())
                || StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO_CONSELHO.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 9;
            }
            if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_CONSELHO.equals(getSituacaoAtual().getStatusFiscalizacao())) {
                return 10;
            }
        }

        return 0;
    }

    public void validarAnaliseAutuacao() {
        ValidacaoException ve = new ValidacaoException();

        if (form.getAnaliseAutuacao().getParecerAnalise() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Parcer da Análise é um campo obrigatório");
        } else {
            if (ParecerAnalise.INDEFERIDO.equals(form.getAnaliseAutuacao().getParecerAnalise())) {
                if (form.getAnaliseAutuacao().getCamposInconsistentes().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("E necessário selecionar os campos que geram inconsistências no auto");
                }

                if ("".equals(form.getAnaliseAutuacao().getDescricao().trim())) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Descrição do Parecer é um campo obrigatório");
                }
            }
        }

        ve.lancarException();
    }

    public void validarCienciaNotificacaoAutuacao() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getNotificacaoAutuacaoRBTrans().getId() != null) {
            if (form.getNotificacaoAutuacaoRBTrans().getTipoCiencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Ciência é um campo obrigatório.");
            } else {
                if (TipoCiencia.CORRESPONDENCIA.equals(form.getNotificacaoAutuacaoRBTrans().getTipoCiencia())
                    || TipoCiencia.OFFICE_BOY.equals(form.getNotificacaoAutuacaoRBTrans().getTipoCiencia())) {
                    if (form.getNotificacaoAutuacaoRBTrans().getNomeResponsavelCiencia() == null ||
                        form.getNotificacaoAutuacaoRBTrans().getNomeResponsavelCiencia().isEmpty()) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Nome do Responsável é um campo obrigatório.");
                    }
                    if (form.getNotificacaoAutuacaoRBTrans().getDataCiencia() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("A Data de Ciência é um campo obrigatório.");
                    }
                }

                if (TipoCiencia.DIARIO_OFICIAL.equals(form.getNotificacaoAutuacaoRBTrans().getTipoCiencia())) {
                    if (form.getNotificacaoAutuacaoRBTrans().getDataPublicacaoDiarioOficial() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("A Data de Publicação é um campo obrigatório.");
                    }

                    if (form.getNotificacaoAutuacaoRBTrans().getNumeroDiarioOficial() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Número do Diário Oficial é um campo obrigatório.");
                    }

                    if (form.getNotificacaoAutuacaoRBTrans().getOrgaoComunicacao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Órgão de Comunicação é um campo obrigatório.");
                    }
                    if (form.getNotificacaoAutuacaoRBTrans().getOrgaoComunicacao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Texto de Publicação é um campo obrigatório.");
                    }
                }
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar a Ciência da Notificação da Infração, deve-se emitir a Notificação.");
        }
    }

    public void validarCienciaNotificacaoPenalidade() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getNotificacaoPenalidade().getId() != null) {
            if (form.getNotificacaoPenalidade().getTipoCiencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Ciencia e um campo obrigatório");
            } else {
                if (TipoCiencia.CORRESPONDENCIA.equals(form.getNotificacaoPenalidade().getTipoCiencia())
                    || TipoCiencia.OFFICE_BOY.equals(form.getNotificacaoPenalidade().getTipoCiencia())) {
                    if (form.getNotificacaoPenalidade().getNomeResponsavelCiencia() == null || form.getNotificacaoPenalidade().getNomeResponsavelCiencia().isEmpty()) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Nome do Responsável é um campo obrigatório.");
                    }

                    if (form.getNotificacaoPenalidade().getDataCiencia() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("A Data de Ciência é um campo obrigatório.");
                    }
                }

                if (TipoCiencia.DIARIO_OFICIAL.equals(form.getNotificacaoPenalidade().getTipoCiencia())) {
                    if (form.getNotificacaoPenalidade().getDataPublicacaoDiarioOficial() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("A Data de Publicação é um campo obrigatório.");
                    }
                    if (form.getNotificacaoPenalidade().getOrgaoComunicacao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Órgão de Comunicação é um campo obrigatório.");
                    }
                    if (form.getNotificacaoPenalidade().getOrgaoComunicacao() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Texto de Publicação é um campo obrigatório.");
                    }
                    if (form.getNotificacaoPenalidade().getNumeroDiarioOficial() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O Número do Diário Oficial é um campo obrigatório.");
                    }
                }
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para salvar a Ciência da Notificação da Penalidade, deve-se emitir a Notificação.");
        }
        ve.lancarException();
    }

    public boolean validaCancelamentoMulta() {
        boolean valida = true;
        if (form.getFiscalizacaoArquivoRBTransCancelamentoMulta().isEmpty()) {
            FacesUtil.addWarn("Atenção!", "É necessário anexar o documento de confirmação do cancelamento.");
            valida = false;
        }
        return valida;
    }

    public boolean validaSalvar() {
        boolean valida = true;
        if (form.getFiscalizacaoRBTrans().getId() == null) {
            FacesUtil.addWarn("Atenção!", "Para salvar, é necessário iniciar o processo de trâmite.");
            valida = false;
        }
        return valida;
    }

    public Date retornaDataPrazo(Date dataInicial, StatusFiscalizacaoRBTrans statusFiscalizacaoRBTrans) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataInicial);
        ParametrosFiscalizacaoRBTrans param = fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente();

        if (StatusFiscalizacaoRBTrans.ANALISE_AUTUACAO.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoPrefeituraEntregaNotificacoes());
        }

        if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrimeiroRecursoDaAutuacao());
        }

        if (StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoDaPrefeituraDeRespostaAoRecurso());
        }

        if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoDaPrefeituraDeRespostaAoRecurso());
        }
        if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoPrefeituraEntregaNotificacoes());
        }

        if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_JARI.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoDaPrefeituraDeRespostaAoRecurso());
        }

        if (StatusFiscalizacaoRBTrans.AGUARDANDO_CANCELAMENTO_MULTA_JARI.equals(statusFiscalizacaoRBTrans)) {
            calendar.add(Calendar.DAY_OF_YEAR, fiscalizacaoRBTransFacade.getParametrosTransitoRBTransFacade().getParametrosFiscalizacaoRBTransVigente().getPrazoDaPrefeituraDeRespostaAoRecurso());
        }

        return calendar.getTime();
    }

    public void voltar() {
        cancelar();
    }

    public boolean getDesabilitaAbaAutuacao() {
        if (form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean getDesabilitaAbaAnaliseAutuacao() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.ANALISE_AUTUACAO.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaNotificacao() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaRecurso() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaAnaliseRecurso() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaNotificacaoPenalidade() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaRecursoJARI() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO_JARI.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean getDesabilitaAbaAnaliseRecursoJARI() {
        if (!form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().isEmpty()) {
            for (SituacaoFiscalizacaoRBTrans obj : form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao()) {
                if (StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_JARI.equals(obj.getStatusFiscalizacao()) && obj.isVigente()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setAbaAtiva(int abaAtiva) {
        form.setAbaAtiva(abaAtiva);
    }

    private void validarRecurso() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getRecursoRBTrans().isApresentado()) {
            if (form.getRecursoRBTrans().getNumeroProtocolo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Número do Protocolo!");
            }
            if (form.getRecursoRBTrans().getAnoProtocolo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Ano do Protocolo!");
            }
            if ("".equals(form.getRecursoRBTrans().getTeorProtocolo().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Teor do Protocolo!");
            }
        }
        ve.lancarException();
    }

    public void validarRecursoJARI() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getRecursoJARI().isApresentado()) {
            if (form.getRecursoJARI().getNumeroProtocolo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Número do Protocolo é um campo obrigatório");
            }
            if (form.getRecursoJARI().getAnoProtocolo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Ano do Protocolo é um campo obrigatório");
            }
            if ("".equals(form.getRecursoJARI().getTeorProtocolo().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O Teor do Protocolo e um campo obrigatório");
            }
        }
        ve.lancarException();
    }

    public void validarAutuacao() {
        ValidacaoException v = new ValidacaoException();
        if ("".equals(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getLocalAutuacao().trim())) {
            v.adicionarMensagemDeCampoObrigatorio("O Local da Autuação é um campo obrigatório");
        }
        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCodigo() == null) {
            v.adicionarMensagemDeCampoObrigatorio("O Número da Autuação é um campo obrigatório");
        } else {
            if (fiscalizacaoRBTransFacade.hasNumeroAutuacao(form.getFiscalizacaoRBTrans().getId(), form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCodigo())) {
                v.adicionarMensagemDeOperacaoNaoPermitida("O número da Autuação já existe");
            }
        }

        if (!isFiscalizacaoClandestina()) {
            if (getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico() == null) {
                v.adicionarMensagemDeCampoObrigatorio("Os Dados do Permissionário são necessários!");
            }
        } else {
            if (getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getPessoaClandestina() == null) {
                v.adicionarMensagemDeCampoObrigatorio("Os Dados da Pessoa Clandestina são necessários!");
            }
            if (getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getPlacaVeiculo() == null ||
                getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getDescricaoVeiculo() == null) {
                v.adicionarMensagemDeCampoObrigatorio("Os Dados do Veículo Clandestino são necessários!");
            }
        }

        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getDataAutuacao() == null) {
            v.adicionarMensagemDeCampoObrigatorio("A Data da Autuação é um campo obrigatório");
        }
        if (form.getFiscalizacaoRBTrans().getOrgaoAutuador() == null) {
            v.adicionarMensagemDeCampoObrigatorio("O Órgão Autuador é um campo obrigatório");
        }

        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCidadeInfracao() == null) {
            v.adicionarMensagemDeCampoObrigatorio("A Cidade da Autuação é um campo obrigatório");
        }

        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao().isEmpty()) {
            v.adicionarMensagemDeOperacaoNaoPermitida("É necessário selecionar ao menos uma Ocorrência");
        }

        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getAgenteAutuador() == null) {
            v.adicionarMensagemDeCampoObrigatorio("O Agente Autuador é um Campo Obrigatório");
        }
        v.lancarException();
    }

    private void validarAnaliseRecurso() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getAnaliseRecurso().getParecerAnalise() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Parecer da Análise do Recurso!");
        }
        if ("".equals(form.getAnaliseRecurso().getDescricao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição da Análise do Recurso!");
        }
        ve.lancarException();
    }

    private void validarAnaliseRecursoJARI() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getAnaliseRecursoJARI().getParecerAnalise() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Parecer da Análise do Recurso!");
        }

        if ("".equals(form.getAnaliseRecursoJARI().getDescricao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição da Análise do Recurso!");
        }
        ve.lancarException();
    }


    public void adicionaCamposInconsistencia(CampoInconsistenciaAutoInfracaoRBTrans campo) {
        campo.setAnaliseAutuacaoRBTrans(form.getAnaliseAutuacao());
        form.getAnaliseAutuacao().getCamposInconsistentes().add(campo);
    }

    public void removeCamposInconsistencia(CampoInconsistenciaAutoInfracaoRBTrans campo) {
        form.getAnaliseAutuacao().getCamposInconsistentes().remove(campo);
    }

    public void adicionaOcorrenciaAutuacao() {
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setOcorrenciasAutuacao(new ArrayList<OcorrenciaAutuacaoRBTrans>());
        OcorrenciaAutuacaoRBTrans ocorrenciaAutuacao = new OcorrenciaAutuacaoRBTrans();
        ocorrenciaAutuacao.setAutuacaoFiscalizacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao());
        ocorrenciaAutuacao.setOcorrenciaFiscalizacao(form.getOcorrenciaSelecionada());
        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao().add(ocorrenciaAutuacao);
    }

    public void desabilitaComponente(String idComponente) {
        FacesUtil.atualizarComponente(idComponente);
    }

    public void salvarFiscalizacao() {
        if (Operacoes.NOVO.equals(operacao)) {
            if (validaSalvar()) {
                try {
                    fiscalizacaoRBTransFacade.salvarNovo(form.getFiscalizacaoRBTrans());
                    FacesUtil.addInfo("Salvo com Sucesso!", "");
                    redireciona();
                } catch (Exception t) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));
                }
            }
        } else {
            try {
                fiscalizacaoRBTransFacade.verificaArquivos(form.getFiscalizacaoRBTrans());
                fiscalizacaoRBTransFacade.salvar(form.getFiscalizacaoRBTrans());
                FacesUtil.addInfo("Salvo com Sucesso!", "");
            } catch (Exception t) {
                logger.error("Erro ao Salvar a Fiscalização: {}", t);
                FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
            }
        }
    }

    public void salvarAutuacao() {
        try {
            validarAutuacao();
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.ANALISE_AUTUACAO));
            situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.ANALISE_AUTUACAO);
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
            try {
                form.setFiscalizacaoRBTrans(fiscalizacaoRBTransFacade.salvarAutuacao(form.getFiscalizacaoRBTrans()));
                FacesUtil.addInfo("Autuação salva com Sucesso!", "");
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            } catch (Exception t) {
                logger.error("Erro ao Salvar a Autuação: {}", t);
                FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvaAnaliseAutuacao() {
        try {
            validarAnaliseAutuacao();
            form.getAnaliseAutuacao().setDataAnalise(new Date());
            getSituacaoAtual().setFinalizadoEM(new Date());
            form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setAnaliseAutuacaoRBTrans(form.getAnaliseAutuacao());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            situacao.setDataInicial(new Date());
            if (ParecerAnalise.DEFERIDO.equals(form.getAnaliseAutuacao().getParecerAnalise())) {
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR);
                situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR));
            } else {
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.ARQUIVADO);
                situacao.setDataFinal(new Date());
            }
            form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
            try {
                fiscalizacaoRBTransFacade.salvaAnaliseAutuacao(form.getFiscalizacaoRBTrans());
                FacesUtil.addInfo("Análise da Autuação salva com Sucesso!", "");
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            } catch (Exception t) {
                logger.error("Erro ao Salvar a Análise da Autuação: {}", t);
                FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvaNotificacaoAutuacao(FiscalizacaoRBTrans fiscalizacao) {
        try {
            fiscalizacaoRBTransFacade.salvaNotificacaoAutuacao(fiscalizacao);
        } catch (Exception t) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));

        }
    }

    public void salvarCienciaNotificacao() {
        try {
            validarCienciaNotificacaoAutuacao();
            getSituacaoAtual().setFinalizadoEM(new Date());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO));
            situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO);
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
            try {
                fiscalizacaoRBTransFacade.salvaCienciaNotificacao(form.getFiscalizacaoRBTrans());
                FacesUtil.addInfo("Ciência da Notificação salva com sucesso!", "");
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            } catch (Exception t) {
                logger.error("Erro ao Salvar a Ciência da Notificação: {}", t);
                FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvarCienciaNotificacaoPenalidade() {
        try {
            validarCienciaNotificacaoPenalidade();
            getSituacaoAtual().setFinalizadoEM(new Date());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO));
            situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO_JARI);
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
            try {
                fiscalizacaoRBTransFacade.salvaCienciaNotificacao(form.getFiscalizacaoRBTrans());
                FacesUtil.addInfo("Ciência da Notificação de Penalidade salva com sucesso!", "");
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            } catch (Exception t) {
                logger.error("Erro ao Salvar a Ciência da Notificação de Penalidade: {}", t);
                FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvarRecurso() {
        try {
            validarRecurso();
            getSituacaoAtual().setFinalizadoEM(new Date());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            if (form.getRecursoRBTrans().isApresentado()) {
                situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO));
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO);
                situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
                form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                form.getRecursoRBTrans().setTipoProcessoRBTrans(TipoProcessoRBTrans.AUTUACAO);
                form.getRecursoRBTrans().setAutuacaoFiscalizacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao());

                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getRecursosRBTrans().add(form.getRecursoRBTrans());
                try {
                    fiscalizacaoRBTransFacade.salvarRecurso(form.getFiscalizacaoRBTrans());
                    FacesUtil.addInfo("Recurso Salvo com sucesso!", "");
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                } catch (Exception t) {
                    logger.error("Erro ao Salvar o Recurso: {}", t);
                    FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
                }
            } else {
                try {
                    fiscalizacaoRBTransFacade.gerarDadosParaPagamento(form.getFiscalizacaoRBTrans());
                    situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE));
                    situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE);
                    situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
                    form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);

                    fiscalizacaoRBTransFacade.salvarRecurso(form.getFiscalizacaoRBTrans());
                    FacesUtil.addInfo("Recurso Salvo com sucesso!", "");

                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                } catch (Exception t) {
                    logger.error("Erro ao Salvar o Recurso: {}", t);
                    FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvarRecursoJARI() {
        try {
            validarRecursoJARI();
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            if (form.getRecursoJARI().isApresentado()) {
                getSituacaoAtual().setFinalizadoEM(new Date());
                situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_JARI));
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_PARECER_RECURSO_JARI);
                form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                form.getRecursoJARI().setTipoProcessoRBTrans(TipoProcessoRBTrans.PENALIDADE);
                form.getRecursoJARI().setAutuacaoFiscalizacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao());
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getRecursosRBTrans().add(form.getRecursoJARI());
                try {
                    fiscalizacaoRBTransFacade.salvarRecurso(form.getFiscalizacaoRBTrans());
                    FacesUtil.addInfo("Recurso JARI Salvo com sucesso!", "");
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                } catch (Exception t) {
                    logger.error("Erro ao Salvar o Recurso JARI: {}", t);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));
                }
            } else {
                if (form.getFiscalizacaoRBTrans().isMultaPaga()) {
                    if (verificaExistenciaInfracaoGrupoE(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao())) {
                        getSituacaoAtual().setFinalizadoEM(new Date());
                        situacao.setDataFinal(new Date());
                        situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.CASSACAO_PERMISSAO);
                        form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                    } else {
                        getSituacaoAtual().setFinalizadoEM(new Date());
                        situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.FINALIZADO);
                        situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR));
                        form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                    }
                    try {
                        fiscalizacaoRBTransFacade.salvarRecurso(form.getFiscalizacaoRBTrans());
                        FacesUtil.addInfo("Recurso JARI Salvo com sucesso!", "");
                        Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                    } catch (Exception t) {
                        logger.error("Erro ao Salvar o Recurso JARI: {}", t);
                        FacesUtil.addFatal("Exceção do sistema!", t.getMessage());
                    }
                } else {
                    salvaBloqueioServico();
                    FacesUtil.addAtencao("Aguardando pagamento da multa!");
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvaBloqueioServico() {
        if (getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico() != null) {
            BloqueioPermissaoFisacalizacao bloqueio = new BloqueioPermissaoFisacalizacao();
            PermissaoTransporte permissao = fiscalizacaoRBTransFacade.getPermissaoTransporteFacade().recuperaPermissoesAtivasDoPermissionario(getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico()).get(0);
            permissao = fiscalizacaoRBTransFacade.getPermissaoTransporteFacade().recuperar(permissao.getId());
            bloqueio.setDataInicial(new Date());
            bloqueio.setFiscalizacaoRBTrans(form.getFiscalizacaoRBTrans());
            bloqueio.setPermissaoTransporte(permissao);
            permissao.getListaBloqueioPermissaoFiscalizacao().add(bloqueio);

            fiscalizacaoRBTransFacade.getPermissaoTransporteFacade().salva(permissao);
        }
    }

    public void salvarAnaliseRecurso() {
        try {
            validarAnaliseRecurso();
            form.getAnaliseRecurso().setData(new Date());
            form.getRecursoRBTrans().setAnaliseRecursoRBTrans(form.getAnaliseRecurso());
            getSituacaoAtual().setFinalizadoEM(new Date());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
            situacao.setDataInicial(new Date());

            if (ParecerAnalise.DEFERIDO.equals(form.getAnaliseRecurso().getParecerAnalise())) {
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.ARQUIVADO);
                situacao.setDataFinal(new Date());
            } else {
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_PENALIDADE);
                situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR));
                fiscalizacaoRBTransFacade.gerarDadosParaPagamento(form.getFiscalizacaoRBTrans());
            }
            form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
            try {
                fiscalizacaoRBTransFacade.salvaAnaliseRecurso(form.getFiscalizacaoRBTrans());
                FacesUtil.addInfo("Análise do Recurso Salva com sucesso!", "");
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            } catch (Exception t) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));
                Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
        }
    }

    public void salvarAnaliseRecursoJari() {
        try {
            validarAnaliseRecursoJARI();
            form.getAnaliseRecursoJARI().setData(new Date());
            form.getRecursoJARI().setAnaliseRecursoRBTrans(form.getAnaliseRecursoJARI());
            SituacaoFiscalizacaoRBTrans situacao = new SituacaoFiscalizacaoRBTrans();
            situacao.setDataInicial(new Date());
            if (ParecerAnalise.DEFERIDO.equals(form.getAnaliseRecursoJARI().getParecerAnalise())) {
                situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.FINALIZADO);
                situacao.setDataFinal(new Date());
                getSituacaoAtual().setFinalizadoEM(new Date());
                situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
                form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                try {
                    fiscalizacaoRBTransFacade.salvaAnaliseRecurso(form.getFiscalizacaoRBTrans());
                    fiscalizacaoRBTransFacade.cancelaDivida(form.getFiscalizacaoRBTrans());
                    FacesUtil.addInfo("Recurso Salvo com sucesso!", "");
                    FacesUtil.addInfo("O débito foi cancelado com sucesso!", "");
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                } catch (Exception t) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                }

            } else {
                if (form.getFiscalizacaoRBTrans().isMultaPaga()) {
                    if (verificaExistenciaInfracaoGrupoE(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getOcorrenciasAutuacao())) {
                        situacao.setDataFinal(new Date());
                        situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.CASSACAO_PERMISSAO);
                        getSituacaoAtual().setFinalizadoEM(new Date());
                        situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
                        form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                    } else {
                        situacao.setStatusFiscalizacao(StatusFiscalizacaoRBTrans.AGUARDANDO_RECURSO_CONSELHO);
                        situacao.setDataFinal(retornaDataPrazo(situacao.getDataInicial(), StatusFiscalizacaoRBTrans.AGUARDANDO_NOTIFICACAO_INFRATOR));
                        getSituacaoAtual().setFinalizadoEM(new Date());
                        situacao.setFiscalizacao(form.getFiscalizacaoRBTrans());
                        form.getFiscalizacaoRBTrans().getSituacoesFiscalizacao().add(situacao);
                    }
                    try {
                        fiscalizacaoRBTransFacade.salvaAnaliseRecurso(form.getFiscalizacaoRBTrans());
                        FacesUtil.addInfo("Recurso Salvo com sucesso!", "");
                        Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                    } catch (Exception t) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", t.getMessage()));
                        Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Atenção", "Aguardando pagamento da multa"));
                    Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
                }
            }
        } catch (ValidacaoException ve){
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            Web.navegacao(new StringBuilder(getCaminhoPadrao()).append("listar/").toString(), new StringBuilder(getCaminhoPadrao()).append("editar/").append(form.getFiscalizacaoRBTrans().getId()).append("/").toString());
        }
    }

    public boolean isTipoDeNotificacaoExistente(TipoProcessoRBTrans tipo) {
        for (NotificacaoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes()) {
            if (tipo.equals(obj.getTipoProcessoRBTrans())) {
                return true;
            }
        }
        return false;
    }

    private void validarImpressaoNotificacao() {
        ValidacaoException ve = new ValidacaoException();
        if (form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para imprimir a notificação, a autuação precisa estar gerada!");
        }
        ve.lancarException();
    }

    public void imprimirDocumentoNotificacao() {
        try {
            validarImpressaoNotificacao();
            if (!isTipoDeNotificacaoExistente(TipoProcessoRBTrans.AUTUACAO)) {
                form.getNotificacaoAutuacaoRBTrans().setAutuacaoFiscalizacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao());
                form.getNotificacaoAutuacaoRBTrans().setDataNitificacao(new Date());
                form.getNotificacaoAutuacaoRBTrans().setTipoProcessoRBTrans(TipoProcessoRBTrans.AUTUACAO);
                form.getNotificacaoAutuacaoRBTrans().setAno(getSistemaControlador().getExercicioCorrente().getAno());
                form.getNotificacaoAutuacaoRBTrans().setNumero(fiscalizacaoRBTransFacade.sugereCodigoNotificacao(getSistemaControlador().getExercicioCorrente().getAno()));
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes().add(form.getNotificacaoAutuacaoRBTrans());
                salvaNotificacaoAutuacao(form.getFiscalizacaoRBTrans());
            }

            if (form.getFiscalizacaoRBTrans().getId() != null) {
                form.setFiscalizacaoRBTrans(fiscalizacaoRBTransFacade.recuperar(form.getFiscalizacaoRBTrans()));
                for (NotificacaoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes()) {
                    if (TipoProcessoRBTrans.AUTUACAO.equals(obj.getTipoProcessoRBTrans())) {
                        form.setNotificacaoAutuacaoRBTrans(obj);
                    }
                }
                try {
                    fiscalizacaoRBTransFacade.geraDocumento(TipoDocumentoFiscalizacaoRBTrans.NOTIFICACAO_INFRATOR,
                        form.getFiscalizacaoRBTrans(),
                        form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico(),
                        retornarPessoaDaFiscalizacao(),
                        getSistemaControlador());
                    form.getFiscalizacaoRBTrans().setDocumentoNotificacao(null);
                } catch (Exception ex) {
                    logger.error("Erro ao imprimir a notificação: {}", ex);
                }
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não é possível emitir a Notificação, siga as etapas da Fiscalização!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void imprimirDocumentoNotificacaoPenalidade() {
        if (!isTipoDeNotificacaoExistente(TipoProcessoRBTrans.PENALIDADE)) {
            form.getNotificacaoPenalidade().setAutuacaoFiscalizacao(form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao());
            form.getNotificacaoPenalidade().setDataNitificacao(new Date());
            form.getNotificacaoPenalidade().setTipoProcessoRBTrans(TipoProcessoRBTrans.PENALIDADE);
            form.getNotificacaoPenalidade().setAno(getSistemaControlador().getExercicioCorrente().getAno());
            form.getNotificacaoPenalidade().setNumero(fiscalizacaoRBTransFacade.sugereCodigoNotificacao(getSistemaControlador().getExercicioCorrente().getAno()));
            form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes().add(form.getNotificacaoPenalidade());

            salvaNotificacaoAutuacao(form.getFiscalizacaoRBTrans());
        }

        form.setFiscalizacaoRBTrans(fiscalizacaoRBTransFacade.recuperar(form.getFiscalizacaoRBTrans()));
        for (NotificacaoRBTrans obj : form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getNotificacoes()) {
            if (TipoProcessoRBTrans.PENALIDADE.equals(obj.getTipoProcessoRBTrans())) {
                form.setNotificacaoPenalidade(obj);
            }
        }
        try {
            fiscalizacaoRBTransFacade.geraDocumento(TipoDocumentoFiscalizacaoRBTrans.NOTIFICACAO_PENALIDADE,
                form.getFiscalizacaoRBTrans(),
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico(),
                retornarPessoaDaFiscalizacao(),
                getSistemaControlador());
            form.getFiscalizacaoRBTrans().setDocumentoNotificacao(null);
        } catch (Exception ex) {
            logger.error("Erro ao imprimir a notificação de penalidade: {}", ex);
        }
    }

    private Pessoa retornarPessoaDaFiscalizacao() {
        return form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico() != null ?
                    form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico().getPessoa() :
                    form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getPessoaClandestina();
    }

    public void imprimirDocumentoParecerRecurso() {
        if (form.getAnaliseRecurso().getId() == null) {
            form.getAnaliseRecurso().setData(new Date());
            form.getRecursoRBTrans().setAnaliseRecursoRBTrans(form.getAnaliseRecurso());
            salvarAnaliseRecurso();
            recuperaRecursos();
        }
        form.setFiscalizacaoRBTrans(fiscalizacaoRBTransFacade.recuperar(form.getFiscalizacaoRBTrans()));
        try {
            fiscalizacaoRBTransFacade.geraDocumento(TipoDocumentoFiscalizacaoRBTrans.PARECER_RECURSO,
                form.getFiscalizacaoRBTrans(),
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico(),
                retornarPessoaDaFiscalizacao(),
                getSistemaControlador());
            form.getFiscalizacaoRBTrans().setDocumentoNotificacao(null);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void imprimirSolicitacaoCancelamentoMulta() {
        try {
            fiscalizacaoRBTransFacade.geraDocumento(TipoDocumentoFiscalizacaoRBTrans.SOLICITACAO_CANCELAMENTO_DIVIDA,
                form.getFiscalizacaoRBTrans(),
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico(),
                retornarPessoaDaFiscalizacao(),
                getSistemaControlador());
            form.getFiscalizacaoRBTrans().setDocumentoNotificacao(null);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void imprimeParecerRecursoJARI() {
        try {
            fiscalizacaoRBTransFacade.geraDocumento(TipoDocumentoFiscalizacaoRBTrans.PARECER_RECURSO_JARI,
                form.getFiscalizacaoRBTrans(),
                form.getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCadastroEconomico(),
                retornarPessoaDaFiscalizacao(),
                getSistemaControlador());
            form.getFiscalizacaoRBTrans().setDocumentoNotificacao(null);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public String retornaPaginaEditar() {
        return "edita";
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(file.getFile().getContentType());
            arq.setDescricao(null);
            arq.setTamanho(file.getFile().getSize());
            arq.setInputStream(file.getFile().getInputstream());

            FiscalizacaoArquivoRBTrans fiscArq = new FiscalizacaoArquivoRBTrans();
            fiscArq.setArquivo(fiscalizacaoRBTransFacade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            fiscArq.setFiscalizacaoRBTrans(form.fiscalizacaoRBTrans);

            form.fiscalizacaoRBTrans.getFiscalizacaoArquivo().add(fiscArq);

        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }


    public void removeArquivo(FiscalizacaoArquivoRBTrans fiscalizacaoArquivoRBTrans) {
        if (fiscalizacaoArquivoRBTrans.getId() != null) {
            fiscalizacaoArquivoRBTrans.setExcluido(true);
        } else {
            form.getFiscalizacaoRBTrans().getFiscalizacaoArquivo().remove(fiscalizacaoArquivoRBTrans);
        }
    }

    public void gerarDAM() throws JRException, IOException, UFMException {
        try {
            VOCalculo voCalculo = fiscalizacaoRBTransFacade.buscarCalculoDaFiscalizacao(form.getFiscalizacaoRBTrans());
            if (voCalculo != null) {
                ValorDivida valorDivida = fiscalizacaoRBTransFacade.getDamFacade().buscarUltimoValorDividaDoCalculo(voCalculo.getIdCalculo());
                Date vencimento = valorDivida.getParcelaValorDividas().get(0).getVencimento();

                DAM dam = fiscalizacaoRBTransFacade.getDamFacade().geraDAM(valorDivida.getParcelaValorDividas().get(0), vencimento);
                ImprimeDAM imprimeDAM = new ImprimeDAM();
                imprimeDAM.setGeraNoDialog(true);
                imprimeDAM.imprimirDamUnicoViaApi(dam);
            } else {
                FacesUtil.addOperacaoNaoPermitida("Não existe débito gerado para a fiscalização!");
            }
        } catch (Exception ex) {
            logger.error("Erro ao imprimir DAM: {}", ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", ex.getMessage()));
        }
    }

    public String escreveNomeValorEPreencheEspacos(String tipoTaxa, String valor, int tamanho) {
        int total = tamanho;
        int nome = tipoTaxa.length();
        int moeda = 4;
        int vl = valor.length();
        int espacos = total - (nome + moeda + vl);

        String retorno = tipoTaxa.toUpperCase();
        for (int i = 0; i < espacos; i++) {
            retorno += " ";
        }

        retorno += " R$ " + valor;
        retorno = retorno.replace(".", ",");
        return retorno;
    }

    public BigDecimal retornarValorTotalMulta(List<OcorrenciaAutuacaoRBTrans> ocorrencias) throws UFMException {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (OcorrenciaAutuacaoRBTrans obj : ocorrencias) {
            if (getFiscalizacaoRBTrans().getInfratorReincidente() != null) {
                valorTotal = valorTotal.add(obj.getOcorrenciaFiscalizacao().getValor().
                    multiply(new BigDecimal(obj.getOcorrenciaFiscalizacao().getFatorMultReincidencia())));
            }
        }
        return fiscalizacaoRBTransFacade.getMoedaFacade().recuperaValorUFMParaData(
            getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getDataAutuacao()).multiply(valorTotal);
    }


    public boolean verificaExistenciaInfracaoGrupoE(List<OcorrenciaAutuacaoRBTrans> ocorrencias) {
        for (OcorrenciaAutuacaoRBTrans obj : ocorrencias) {
            if ("E".equals(obj.getOcorrenciaFiscalizacao().getGrupo())) {
                return true;
            }
        }
        return false;
    }

    public void recuperarMotorista() {
        PessoaFisica motoristaInfrator = getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getMotoristaInfrator();
        if (motoristaInfrator != null) {
            motoristaInfrator = fiscalizacaoRBTransFacade.getPessoaFisicaFacade().recuperar(motoristaInfrator.getId());
            getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().setMotoristaInfrator(motoristaInfrator);
            form.setCnhMotorista(motoristaInfrator.getCNH() != null ? motoristaInfrator.getCNH().toString() : "");
        }
    }

    public void recuperaMotoristaDaSessao() {
        PessoaFisica motoristaAux = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        if (motoristaAux != null) {
            form.setMotorista(motoristaAux);
        }
    }

    public List<AgenteAutuador> completarAgentesAutuadores(String parte) {
        return fiscalizacaoRBTransFacade.getAgenteAutuadorFacade().buscarAgentesAutuadoresAtivos(parte.trim());
    }

    public boolean isFiscalizacaoClandestina() {
        return EspecieTransporte.CLANDESTINO.equals(getFiscalizacaoRBTrans().getEspecieTransporte());
    }

    public class FormFiscalizacaoRBTrans {
        protected Operacoes operacao;
        public FiscalizacaoRBTrans fiscalizacaoRBTrans;
        public AutuacaoFiscalizacaoRBTrans autuacao;
        public AnaliseAutuacaoRBTrans analiseAutuacao;
        public PessoaFisica motorista;
        public List<EnderecoCorreio> enderecosMotorista;
        public OcorrenciaFiscalizacaoRBTrans ocorrenciaSelecionada;
        public List<OcorrenciaFiscalizacaoRBTrans> ocorrenciasFiscalizacaoRBTrans;
        public List<EnderecoCorreio> enderecosPermissionario;
        public int abaAtiva;
        public NotificacaoRBTrans notificacaoAutuacaoRBTrans;
        public NotificacaoRBTrans notificacaoPenalidade;
        public Arquivo arquivo;
        public Arquivo arquivoRecurso;
        public Arquivo arquivoCancelamento;
        public UploadedFile file;
        public VeiculoTransporte veiculoTransporte;
        public List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecurso;
        public List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecursoJARI;
        public List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecursoCONSELHO;
        public List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransCancelamentoMulta;
        public List<FiscalizacaoArquivoRBTrans> arquivos;
        public RecursoRBTrans recursoRBTrans;
        public RecursoRBTrans recursoJARI;
        public RecursoRBTrans recursoConselho;
        public AnaliseRecursoRBTrans analiseRecurso;
        public AnaliseRecursoRBTrans analiseRecursoJARI;
        public AnaliseRecursoRBTrans analiseRecursoConselho;
        public FiltrosConsultaFiscalizacaoRBTrans filtros;
        public List<FiscalizacaoRBTrans> listaFiscalizacoes;
        public List<CampoInconsistenciaAutoInfracaoRBTrans> camposInconsistentes;
        public String cnhMotorista;

        public FormFiscalizacaoRBTrans() {
            fiscalizacaoArquivoRBTransRecurso = new ArrayList<>();
            fiscalizacaoArquivoRBTransRecursoJARI = new ArrayList<>();
            fiscalizacaoArquivoRBTransRecursoCONSELHO = new ArrayList<>();
            fiscalizacaoArquivoRBTransCancelamentoMulta = new ArrayList<>();
            listaFiscalizacoes = new ArrayList<>();
            camposInconsistentes = new ArrayList<>();
            ocorrenciasFiscalizacaoRBTrans = new ArrayList<>();
            enderecosPermissionario = new ArrayList<>();
            arquivos = new ArrayList<>();
            cnhMotorista = "";
        }

        public List<FiscalizacaoArquivoRBTrans> getArquivos() {
            return arquivos;
        }

        public void setArquivos(List<FiscalizacaoArquivoRBTrans> arquivos) {
            this.arquivos = arquivos;
        }

        public Operacoes getOperacao() {
            return operacao;
        }

        public void setOperacao(Operacoes operacao) {
            this.operacao = operacao;
        }

        public FiscalizacaoRBTrans getFiscalizacaoRBTrans() {
            return fiscalizacaoRBTrans;
        }

        public void setFiscalizacaoRBTrans(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
            this.fiscalizacaoRBTrans = fiscalizacaoRBTrans;
        }

        public AutuacaoFiscalizacaoRBTrans getAutuacao() {
            return autuacao;
        }

        public void setAutuacao(AutuacaoFiscalizacaoRBTrans autuacao) {
            this.autuacao = autuacao;
        }

        public AnaliseAutuacaoRBTrans getAnaliseAutuacao() {
            return analiseAutuacao;
        }

        public void setAnaliseAutuacao(AnaliseAutuacaoRBTrans analiseAutuacao) {
            this.analiseAutuacao = analiseAutuacao;
        }

        public VeiculoTransporte getVeiculoTransporte() {
            return veiculoTransporte;
        }

        public void setVeiculoTransporte(VeiculoTransporte veiculoTransporte) {
            this.veiculoTransporte = veiculoTransporte;
        }

        public PessoaFisica getMotorista() {
            return motorista;
        }

        public void setMotorista(PessoaFisica motorista) {
            this.motorista = motorista;
        }

        public List<EnderecoCorreio> getEnderecosMotorista() {
            return enderecosMotorista;
        }

        public void setEnderecosMotorista(List<EnderecoCorreio> enderecosMotorista) {
            this.enderecosMotorista = enderecosMotorista;
        }

        public OcorrenciaFiscalizacaoRBTrans getOcorrenciaSelecionada() {
            return ocorrenciaSelecionada;
        }

        public void setOcorrenciaSelecionada(OcorrenciaFiscalizacaoRBTrans ocorrenciaSelecionada) {
            this.ocorrenciaSelecionada = ocorrenciaSelecionada;
        }

        public List<OcorrenciaFiscalizacaoRBTrans> getOcorrenciasFiscalizacaoRBTrans() {
            return ocorrenciasFiscalizacaoRBTrans;
        }

        public void setOcorrenciasFiscalizacaoRBTrans(List<OcorrenciaFiscalizacaoRBTrans> ocorrenciasFiscalizacaoRBTrans) {
            this.ocorrenciasFiscalizacaoRBTrans = ocorrenciasFiscalizacaoRBTrans;
        }

        public List<EnderecoCorreio> getEnderecosPermissionario() {
            return enderecosPermissionario;
        }

        public void setEnderecosPermissionario(List<EnderecoCorreio> enderecosPermissionario) {
            this.enderecosPermissionario = enderecosPermissionario;
        }

        public int getAbaAtiva() {
            return abaAtiva;
        }

        public void setAbaAtiva(int abaAtiva) {
            this.abaAtiva = abaAtiva;
        }

        public NotificacaoRBTrans getNotificacaoAutuacaoRBTrans() {
            return notificacaoAutuacaoRBTrans;
        }

        public void setNotificacaoAutuacaoRBTrans(NotificacaoRBTrans notificacaoAutuacaoRBTrans) {
            this.notificacaoAutuacaoRBTrans = notificacaoAutuacaoRBTrans;
        }

        public NotificacaoRBTrans getNotificacaoPenalidade() {
            return notificacaoPenalidade;
        }

        public void setNotificacaoPenalidade(NotificacaoRBTrans notificacaoPenalidade) {
            this.notificacaoPenalidade = notificacaoPenalidade;
        }

        public Arquivo getArquivo() {
            return arquivo;
        }

        public void setArquivo(Arquivo arquivo) {
            this.arquivo = arquivo;
        }

        public Arquivo getArquivoRecurso() {
            return arquivoRecurso;
        }

        public void setArquivoRecurso(Arquivo arquivoRecurso) {
            this.arquivoRecurso = arquivoRecurso;
        }

        public Arquivo getArquivoCancelamento() {
            return arquivoCancelamento;
        }

        public void setArquivoCancelamento(Arquivo arquivoCancelamento) {
            this.arquivoCancelamento = arquivoCancelamento;
        }

        public UploadedFile getFile() {
            return file;
        }

        public void setFile(UploadedFile file) {
            this.file = file;
        }

        public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransRecurso() {
            return fiscalizacaoArquivoRBTransRecurso;
        }

        public void setFiscalizacaoArquivoRBTransRecurso(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecurso) {
            this.fiscalizacaoArquivoRBTransRecurso = fiscalizacaoArquivoRBTransRecurso;
        }

        public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransRecursoJARI() {
            return fiscalizacaoArquivoRBTransRecursoJARI;
        }

        public void setFiscalizacaoArquivoRBTransRecursoJARI(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecursoJARI) {
            this.fiscalizacaoArquivoRBTransRecursoJARI = fiscalizacaoArquivoRBTransRecursoJARI;
        }

        public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransRecursoCONSELHO() {
            return fiscalizacaoArquivoRBTransRecursoCONSELHO;
        }

        public void setFiscalizacaoArquivoRBTransRecursoCONSELHO(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransRecursoCONSELHO) {
            this.fiscalizacaoArquivoRBTransRecursoCONSELHO = fiscalizacaoArquivoRBTransRecursoCONSELHO;
        }

        public List<FiscalizacaoArquivoRBTrans> getFiscalizacaoArquivoRBTransCancelamentoMulta() {
            return fiscalizacaoArquivoRBTransCancelamentoMulta;
        }

        public void setFiscalizacaoArquivoRBTransCancelamentoMulta(List<FiscalizacaoArquivoRBTrans> fiscalizacaoArquivoRBTransCancelamentoMulta) {
            this.fiscalizacaoArquivoRBTransCancelamentoMulta = fiscalizacaoArquivoRBTransCancelamentoMulta;
        }

        public RecursoRBTrans getRecursoRBTrans() {
            return recursoRBTrans;
        }

        public void setRecursoRBTrans(RecursoRBTrans recursoRBTrans) {
            this.recursoRBTrans = recursoRBTrans;
        }

        public RecursoRBTrans getRecursoJARI() {
            return recursoJARI;
        }

        public void setRecursoJARI(RecursoRBTrans recursoJARI) {
            this.recursoJARI = recursoJARI;
        }

        public RecursoRBTrans getRecursoConselho() {
            return recursoConselho;
        }

        public void setRecursoConselho(RecursoRBTrans recursoConselho) {
            this.recursoConselho = recursoConselho;
        }

        public AnaliseRecursoRBTrans getAnaliseRecurso() {
            return analiseRecurso;
        }

        public void setAnaliseRecurso(AnaliseRecursoRBTrans analiseRecurso) {
            this.analiseRecurso = analiseRecurso;
        }

        public AnaliseRecursoRBTrans getAnaliseRecursoJARI() {
            return analiseRecursoJARI;
        }

        public void setAnaliseRecursoJARI(AnaliseRecursoRBTrans analiseRecursoJARI) {
            this.analiseRecursoJARI = analiseRecursoJARI;
        }

        public String getCnhMotorista() {
            return cnhMotorista;
        }

        public void setCnhMotorista(String cnhMotorista) {
            this.cnhMotorista = cnhMotorista;
        }

        public AnaliseRecursoRBTrans getAnaliseRecursoConselho() {
            return analiseRecursoConselho;
        }

        public void setAnaliseRecursoConselho(AnaliseRecursoRBTrans analiseRecursoConselho) {
            this.analiseRecursoConselho = analiseRecursoConselho;
        }

        public FiltrosConsultaFiscalizacaoRBTrans getFiltros() {
            return filtros;
        }

        public void setFiltros(FiltrosConsultaFiscalizacaoRBTrans filtros) {
            this.filtros = filtros;
        }

        public List<FiscalizacaoRBTrans> getListaFiscalizacoes() {
            return listaFiscalizacoes;
        }

        public void setListaFiscalizacoes(List<FiscalizacaoRBTrans> listaFiscalizacoes) {
            this.listaFiscalizacoes = listaFiscalizacoes;
        }

        public List<CampoInconsistenciaAutoInfracaoRBTrans> getCamposInconsistentes() {
            return camposInconsistentes;
        }

        public void setCamposInconsistentes(List<CampoInconsistenciaAutoInfracaoRBTrans> camposInconsistentes) {
            this.camposInconsistentes = camposInconsistentes;
        }
    }

}
