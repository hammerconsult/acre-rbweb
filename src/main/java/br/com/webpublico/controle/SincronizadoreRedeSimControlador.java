/*
 * Codigo gerado automaticamente em Mon Feb 28 16:58:30 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.services.IntegracaoRedeSimService;
import br.com.webpublico.tributario.dto.EnderecoCorreioDTO;
import br.com.webpublico.tributario.dto.PessoaCnaeDTO;
import br.com.webpublico.tributario.dto.PessoaJuridicaDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "sincronizadoreRedeSimControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "eventosRedeSim",
        pattern = "/tributario/cadastroeconomico/eventos-rede-sim/listar/",
        viewId = "/faces/tributario/cadastromunicipal/eventoredesim/lista.xhtml"),
    @URLMapping(id = "verEventosRedeSim",
        pattern = "/tributario/cadastroeconomico/eventos-rede-sim/ver/#{sincronizadoreRedeSimControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/eventoredesim/visualizar.xhtml")

})
public class SincronizadoreRedeSimControlador extends PrettyControlador<EventoRedeSim> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SincronizadoreRedeSimControlador.class);
    @EJB
    private SincronizadorRedeSimFacade sincronizadorRedeSimFacade;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private LazyDataModel<EventoRedeSim> model;
    private PessoaJuridicaDTO pessoaRedeSim;
    private EventoRedeSim evento;
    private PessoaJuridica pessoaJuridica;
    private CadastroEconomico cadastroEconomico;
    private ObjectMapper mapper = new ObjectMapper();
    private Date filtroDataInicial;
    private Date filtroDataFinal;
    private EventoRedeSim.TipoEvento filtroTipoEvento;
    private ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoTarefaAgendada = ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada.PROCESSAR_EVENTO_REDESIM;
    private IntegracaoRedeSimService integracaoRedeSimService;

    public SincronizadoreRedeSimControlador() {
        super(EventoRedeSim.class);
        integracaoRedeSimService = (IntegracaoRedeSimService) Util.getSpringBeanPeloNome("integracaoRedeSimService");
    }

    public ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada getTipoTarefaAgendada() {
        return tipoTarefaAgendada;
    }

    public LazyDataModel<EventoRedeSim> getModel() {
        if (model == null) {
            buscarEventosNaBase();
        }
        return model;
    }

    public PessoaJuridicaDTO getPessoaRedeSim() {
        return pessoaRedeSim;
    }

    public void setPessoaRedeSim(PessoaJuridicaDTO pessoaRedeSim) {
        this.pessoaRedeSim = pessoaRedeSim;
    }

    public void setModel(LazyDataModel<EventoRedeSim> model) {
        this.model = model;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public Date getFiltroDataInicial() {
        return filtroDataInicial;
    }

    public void setFiltroDataInicial(Date filtroDataInicial) {
        this.filtroDataInicial = filtroDataInicial;
    }

    public Date getFiltroDataFinal() {
        return filtroDataFinal;
    }

    public void setFiltroDataFinal(Date filtroDataFinal) {
        this.filtroDataFinal = filtroDataFinal;
    }

    public EventoRedeSim.TipoEvento getFiltroTipoEvento() {
        return filtroTipoEvento;
    }

    public void setFiltroTipoEvento(EventoRedeSim.TipoEvento filtroTipoEvento) {
        this.filtroTipoEvento = filtroTipoEvento;
    }

    @Override
    public AbstractFacade getFacede() {
        return sincronizadorRedeSimFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastroeconomico/eventos-rede-sim/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verEventosRedeSim", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        try {
            selecionarPessoaRedeSim(selecionado);
        } catch (Exception e) {
            pessoaRedeSim = null;
            logger.error("Não foi possível ler o conteúdo desse evento: {}", e);
        }
    }

    @URLAction(mappingId = "eventosRedeSim", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listarEventosNaBase() {
        filtroDataInicial = null;
        filtroDataFinal = null;
        filtroTipoEvento = null;
        buscarEventosNaBase();
    }

    public void buscarEventosNaBase() {
        model = new LazyDataModel<EventoRedeSim>() {
            @Override
            public List<EventoRedeSim> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(integracaoRedeSimFacade.contarEventosNaoProcessados(filtroDataInicial, filtroDataFinal, filtroTipoEvento));
                return integracaoRedeSimFacade.buscarEventosNaoProcessados(first, pageSize, filtroDataInicial, filtroDataFinal, filtroTipoEvento);
            }
        };
    }

    public void buscarEmpresasRedeSim() {
        try {
            validarAgendamento();
            integracaoRedeSimFacade.buscarEventosRedeSim();
            buscarEventosNaBase();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao comunicar com a RedeSim: " + ex.getMessage());
        }
    }

    private void validarAgendamento() {
        if (integracaoRedeSimFacade.getConfiguracaoAgendamentoTarefaFacade().hasAgendamento(tipoTarefaAgendada)) {
            throw new ValidacaoException("Existe Agendamento configurado para Integração da RedeSim. " +
                "Por esse motivo as opções de integração manual estão desabilitadas.");
        }
    }

    /*
    Se alterar esse método, verificar se é necessário alterar os métodos no IntegracaoRedeSimService (consultarAndProcessarIntegracaoRedeSIM())
  */
    public void selecionarPessoaRedeSim(EventoRedeSim dto) throws IOException {
        this.evento = dto;
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            pessoaRedeSim = mapper.readValue(evento.getConteudo(), PessoaJuridicaDTO.class);
            if (pessoaRedeSim.getCnaes() != null) {
                for (PessoaCnaeDTO pessoaRedeSimCnae : pessoaRedeSim.getCnaes()) {
                    CNAE cnae = cadastroEconomicoFacade.getCnaeFacade().buscarCnaePorCodigo(pessoaRedeSimCnae.getCnae().getCodigo());
                    pessoaRedeSimCnae.getCnae().setNome(cnae.getDescricaoDetalhada());
                }
            }
            if (EventoRedeSim.TipoEvento.ATUALIZACAO.equals(evento.getTipoEvento())) {
                cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(pessoaRedeSim.getCnpj());
                if (cadastroEconomico != null) {
                    cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
                }
                pessoaJuridica = cadastroEconomicoFacade.getPessoaFacade().buscarPessoaJuridicaPorCNPJ(pessoaRedeSim.getCnpj(), true);
            }
            FacesUtil.atualizarComponente("FormularioInsersaoRedeSim");
            FacesUtil.executaJavaScript("mostrarDialogRedeSim();");
        } catch (Exception e) {
            logger.error("Erro ao converter o json: ", e);
            FacesUtil.addOperacaoNaoPermitida("O conteúdo está numa versão diferente da atual!");
        }
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void verificarSePessoaJaTemCadastro() {
        cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(pessoaRedeSim.getCnpj());
        confirmarInsersaoPessoa(pessoaComEnderecoDeRioBranco() && cadastroEconomico == null);
    }

    private boolean pessoaComEnderecoDeRioBranco() {
        EnderecoCorreioDTO enderecoCorreio = pessoaRedeSim.getEnderecoCorreio();
        return enderecoCorreio != null && enderecoCorreio.getLocalidade().equalsIgnoreCase("RIO BRANCO") && enderecoCorreio.getUf().equalsIgnoreCase("AC");
    }

    public void confirmarInsersaoPessoa(boolean criaNovoCadastro) {
        try {
            validarAgendamento();
            integracaoRedeSimService.integrarEventoRedeSim(evento, sistemaFacade.getUsuarioCorrente(),
                sistemaFacade.getUsuarioBancoDeDados(), criaNovoCadastro, false);
            buscarEventosNaBase();
            FacesUtil.addOperacaoRealizada("Operação realizada com sucesso.");
            recarregarPagina();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void navegarParaCadastro() {
        if (cadastroEconomico.getId() != null) {
            FacesUtil.redirecionamentoInterno("/tributario/cadastroeconomico/ver/" + cadastroEconomico.getId() + "/");
        } else {
            recarregarPagina();
        }
    }

    public void recarregarPagina() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public void cancelar() {
        recarregarPagina();
    }

    public List<SelectItem> tiposEventos() {
        return Util.getListSelectItem(EventoRedeSim.TipoEvento.values());
    }
}
