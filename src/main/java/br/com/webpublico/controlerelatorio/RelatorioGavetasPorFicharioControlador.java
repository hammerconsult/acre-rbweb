package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Fichario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FicharioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 19/05/14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-gavetas-por-fichario", pattern = "/relatorio/gavetas-por-fichario/", viewId = "/faces/rh/relatorios/relatoriogavetasporfichario.xhtml")
})
public class RelatorioGavetasPorFicharioControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FicharioFacade ficharioFacade;
    private Converter converterFichario;
    private Fichario fichario;
    private OpcoesFiltroRelatorio opcoesFiltroRelatorio;
    private String filtro;

    public RelatorioGavetasPorFicharioControlador() {
    }

    public void geraRelatorio() {
        try {
            validarFiltros();
            gerarFiltro();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("rh/gavetas-por-fichario/");
            dto.setNomeRelatorio("gavetas-por-fichario");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
            dto.adicionarParametro("FICHARIO", fichario.getId());
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO GAVETAS POR FICHÁRIO");
            dto.adicionarParametro("OPCOESFILTRORELATORIO", opcoesFiltroRelatorio.name());
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void gerarFiltro() {
        filtro = "";
        if (opcoesFiltroRelatorio.equals(OpcoesFiltroRelatorio.POSICOES_OCUPADAS)) {
            filtro += "POSIÇÕES OCUPADAS DAS GAVETAS";
        }
        if (opcoesFiltroRelatorio.equals(OpcoesFiltroRelatorio.POSICOES_VAZIAS)) {
            filtro += "POSIÇÕES VAZIAS DAS GAVETAS";
        }
        if (opcoesFiltroRelatorio.equals(OpcoesFiltroRelatorio.TODOS)) {
            filtro += "TODAS AS POSIÇÕES DAS GAVETAS";
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (fichario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o fichário");
        }
        if (opcoesFiltroRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o campo posição");
        }
        ve.lancarException();
    }

    public List<SelectItem> opcoesDeSituacao() {
        List<SelectItem> lista = new ArrayList<>();
        for (OpcoesFiltroRelatorio o : OpcoesFiltroRelatorio.values()) {
            lista.add(new SelectItem(o, o.getDescricao()));
        }
        return lista;
    }

    @URLAction(mappingId = "relatorio-gavetas-por-fichario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.fichario = null;
        opcoesFiltroRelatorio = OpcoesFiltroRelatorio.TODOS;
    }


    public Converter getConverterFichario() {
        if (converterFichario == null) {
            converterFichario = new ConverterAutoComplete(Fichario.class, ficharioFacade);
        }
        return converterFichario;
    }

    public List<Fichario> completaFichario(String parte) {
        return ficharioFacade.listaFiltrandoCodigoDescricao(parte.trim());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Fichario getFichario() {
        return fichario;
    }

    public void setFichario(Fichario fichario) {
        this.fichario = fichario;
    }

    public OpcoesFiltroRelatorio getOpcoesFiltroRelatorio() {
        return opcoesFiltroRelatorio;
    }

    public void setOpcoesFiltroRelatorio(OpcoesFiltroRelatorio opcoesFiltroRelatorio) {
        this.opcoesFiltroRelatorio = opcoesFiltroRelatorio;
    }


    public enum OpcoesFiltroRelatorio {
        POSICOES_OCUPADAS("Posições Ocupadas"),
        POSICOES_VAZIAS("Posições Vazias"),
        TODOS("Todas as Posições");

        private String descricao;

        private OpcoesFiltroRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

}
