package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.CancelamentoIsencaoCadastroImobiliario;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.CancelamentoIsencaoCadastroImobiliarioFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cancelamento-isencao", pattern = "/cancelamento-de-isencao-de-iptu/novo/", viewId = "/faces/tributario/iptu/isencaoimpostos/cancelamento/edita.xhtml"),
    @URLMapping(id = "editar-cancelamento-isencao", pattern = "/cancelamento-de-isencao-de-iptu/editar/#{cancelamentoIsencaoCadastroImobiliarioControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/cancelamento/edita.xhtml"),
    @URLMapping(id = "listar-cancelamento-isencao", pattern = "/cancelamento-de-isencao-de-iptu/listar/", viewId = "/faces/tributario/iptu/isencaoimpostos/cancelamento/lista.xhtml"),
    @URLMapping(id = "ver-cancelamento-isencao", pattern = "/cancelamento-de-isencao-de-iptu/ver/#{cancelamentoIsencaoCadastroImobiliarioControlador.id}/", viewId = "/faces/tributario/iptu/isencaoimpostos/cancelamento/visualizar.xhtml")
})
public class CancelamentoIsencaoCadastroImobiliarioControlador extends PrettyControlador<CancelamentoIsencaoCadastroImobiliario> implements Serializable, CRUD {

    @EJB
    private CancelamentoIsencaoCadastroImobiliarioFacade cancelamentoIsencaoCadastroImobiliarioFacade;
    private FiltroCancelamentoIsencao filtroCancelamentoIsencao;
    private List<IsencaoCadastroImobiliario> isencoes;
    private int qtdeRegistros;
    private int maxRegistros;
    private int pagina;
    private Future<AssistenteBarraProgresso> future;
    private AssistenteBarraProgresso assistente;

    public CancelamentoIsencaoCadastroImobiliarioControlador() {
        super(CancelamentoIsencaoCadastroImobiliario.class);
    }

    public FiltroCancelamentoIsencao getFiltroCancelamentoIsencao() {
        return filtroCancelamentoIsencao;
    }

    public void setFiltroCancelamentoIsencao(FiltroCancelamentoIsencao filtroCancelamentoIsencao) {
        this.filtroCancelamentoIsencao = filtroCancelamentoIsencao;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public Future<AssistenteBarraProgresso> getFuture() {
        return future;
    }

    public void setFuture(Future<AssistenteBarraProgresso> future) {
        this.future = future;
    }

    public List<IsencaoCadastroImobiliario> getIsencoes() {
        return isencoes;
    }

    public void setIsencoes(List<IsencaoCadastroImobiliario> isencoes) {
        this.isencoes = isencoes;
    }

    public int getQtdeRegistros() {
        return qtdeRegistros;
    }

    public void setQtdeRegistros(int qtdeRegistros) {
        this.qtdeRegistros = qtdeRegistros;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cancelamento-de-isencao-de-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return cancelamentoIsencaoCadastroImobiliarioFacade;
    }

    @Override
    @URLAction(mappingId = "novo-cancelamento-isencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        filtroCancelamentoIsencao = new FiltroCancelamentoIsencao();
        qtdeRegistros = 0;
        maxRegistros = 10;
        pagina = 1;
    }

    @Override
    @URLAction(mappingId = "editar-cancelamento-isencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        filtroCancelamentoIsencao = new FiltroCancelamentoIsencao();
    }

    @Override
    @URLAction(mappingId = "ver-cancelamento-isencao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        filtroCancelamentoIsencao = new FiltroCancelamentoIsencao();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public void iniciarIsencao(IsencaoCadastroImobiliario isencao) {
        selecionado.setDataOperacao(cancelamentoIsencaoCadastroImobiliarioFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(cancelamentoIsencaoCadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setIsencao(isencao);
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        filtroCancelamentoIsencao.setCadastroImobiliario((CadastroImobiliario) obj);
    }

    private void buscarIsencoes() {
        isencoes = cancelamentoIsencaoCadastroImobiliarioFacade.buscarIsencoesPorFiltro(filtroCancelamentoIsencao, maxRegistros, pagina);
        if (isencoes == null || isencoes.isEmpty()) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), "O nenhuma isenção encontrada para ser cancelada.");
        }
    }

    public void pesquisarIsencoes() {
        qtdeRegistros = 0;
        maxRegistros = 10;
        pagina = 1;
        qtdeRegistros = cancelamentoIsencaoCadastroImobiliarioFacade.contarIsencoesPorFiltro(filtroCancelamentoIsencao);
        buscarIsencoes();
    }

    public void navegarProximo() {
        if (pagina <= totalPaginas()) {
            pagina += 1;
            buscarIsencoes();
        }
    }

    public void navegarAnterior() {
        if (pagina >= 1) {
            pagina -= 1;
            buscarIsencoes();
        }
    }

    public int totalPaginas() {
        return (qtdeRegistros / maxRegistros) + 1;
    }

    public void cancelarTodos() {
        assistente = new AssistenteBarraProgresso();
        assistente.setExecutando(true);
        assistente.setTotal(qtdeRegistros);
        assistente.setDescricaoProcesso("Cancelamento de Isenções do Cadastro Imobiliário");
        assistente.setUsuarioSistema(cancelamentoIsencaoCadastroImobiliarioFacade.getSistemaFacade().getUsuarioCorrente());

        future = cancelamentoIsencaoCadastroImobiliarioFacade.cancelarTodasIsencoes(assistente, filtroCancelamentoIsencao, selecionado.getMotivo(),
            maxRegistros, totalPaginas());
    }

    public void posCancelamento() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("terminaCancelamento();");
        }
    }

    public class FiltroCancelamentoIsencao {

        private CadastroImobiliario cadastroImobiliario;
        private String numeroProtocolo;
        private Long numeroProcesso;
        private Exercicio exercicioProcesso;

        public CadastroImobiliario getCadastroImobiliario() {
            return cadastroImobiliario;
        }

        public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
            this.cadastroImobiliario = cadastroImobiliario;
        }

        public String getNumeroProtocolo() {
            return numeroProtocolo;
        }

        public void setNumeroProtocolo(String numeroProtocolo) {
            this.numeroProtocolo = numeroProtocolo;
        }

        public Long getNumeroProcesso() {
            return numeroProcesso;
        }

        public void setNumeroProcesso(Long numeroProcesso) {
            this.numeroProcesso = numeroProcesso;
        }

        public Exercicio getExercicioProcesso() {
            return exercicioProcesso;
        }

        public void setExercicioProcesso(Exercicio exercicioProcesso) {
            this.exercicioProcesso = exercicioProcesso;
        }
    }
}
