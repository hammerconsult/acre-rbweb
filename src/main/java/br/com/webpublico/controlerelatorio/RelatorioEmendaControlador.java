package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EmendaItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioEmendaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Edi on 10/11/2015.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-emenda-publicacao", pattern = "/relatorio-emenda/publicacao/", viewId = "/faces/financeiro/relatorio/relatorio-emenda-publicacao.xhtml"),
    @URLMapping(id = "novo-relatorio-emenda-vereador", pattern = "/relatorio-emenda/vereador/", viewId = "/faces/financeiro/relatorio/relatorio-emenda-vereador.xhtml"),
    @URLMapping(id = "novo-relatorio-emenda-beneficiario", pattern = "/relatorio-emenda/beneficiario/", viewId = "/faces/financeiro/relatorio/relatorio-emenda-beneficiario.xhtml")
})
@ManagedBean(name = "relatorioEmendaControlador")
public class RelatorioEmendaControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioEmendaFacade relatorioEmendaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Date dataInicial;
    private Date dataFinal;
    private String filtros;
    private Vereador vereador;
    private Emenda emenda;
    private BeneficiarioEmenda beneficiarioEmenda;
    private UnidadeGestora unidadeGestora;
    private List<HierarquiaOrganizacional> listaUnidades;
    private ConverterAutoComplete converterVereador;
    private ConverterAutoComplete converterBeneficiario;
    private ConverterAutoComplete converterEmenda;

    public RelatorioEmendaControlador() {
    }

    @URLAction(mappingId = "novo-relatorio-emenda-publicacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioPublicacao() {
        parametrosIniciais();
    }

    @URLAction(mappingId = "novo-relatorio-emenda-vereador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioVereador() {
        parametrosIniciais();
    }

    @URLAction(mappingId = "novo-relatorio-emenda-beneficiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioBeneficiario() {
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        unidadeGestora = null;
        vereador = null;
        emenda = null;
        beneficiarioEmenda = null;
        filtros = "";
        dataInicial = sistemaControlador.getDataOperacao();
        dataFinal = sistemaControlador.getDataOperacao();
        listaUnidades = new ArrayList<>();
    }

    public void gerarRelatorioPublicacao() {
        try {
            if (validaDatas()) {
                HashMap parameters = new HashMap();
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSql());
                String nomeArquivo = "RelatorioEmendaPublicacao.jasper";
                parameters.put("FILTROS", filtros);
                parameters.put("SUBREPORT_DIR", getCaminho());
                parameters.put("IMAGEM", getCaminhoImagem() + "Brasao_Camara_Rio_Branco.jpg");
                parameters.put("CAMARA", "Câmara Municipal de Rio Branco - Acre");
                parameters.put("MODULO", "Planejamento Público");
                getUsuarioRelatorio(parameters);
                gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorioPublicacao(), nomeArquivo, parameters, ds);
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorioVereador() {
        try {
            if (validaDatas()) {
                HashMap parameters = new HashMap();
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSql());
                String nomeArquivo = "RelatorioEmendaVereador.jasper";
                parameters.put("FILTROS", filtros);
                parameters.put("SUBREPORT_DIR", getCaminho());
                parameters.put("IMAGEM", getCaminhoImagem() + "Brasao_Camara_Rio_Branco.jpg");
                parameters.put("CAMARA", "Câmara Municipal de Rio Branco - Acre");
                parameters.put("MODULO", "Planejamento Público");
                getUsuarioRelatorio(parameters);
                gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorioVereador(), nomeArquivo, parameters, ds);
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorioBeneficiario() {
        try {
            if (validaDatas()) {
                HashMap parameters = new HashMap();
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(gerarSqlRelatorioBeneficiario());
                String nomeArquivo = "RelatorioEmendaBeneficiario.jasper";
                parameters.put("FILTROS", filtros);
                parameters.put("SUBREPORT_DIR", getCaminho());
                parameters.put("IMAGEM", getCaminhoImagem() + "Brasao_Camara_Rio_Branco.jpg");
                parameters.put("CAMARA", "Câmara Municipal de Rio Branco - Acre");
                parameters.put("MODULO", "Planejamento Público");
                getUsuarioRelatorio(parameters);
                gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorioBeneficiario(), nomeArquivo, parameters, ds);
            }
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void getUsuarioRelatorio(HashMap parameters) {
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
    }

    public String getNomeRelatorioPublicacao() {
        return "RELATORIO-EMENDA-PUBLICACAO";
    }

    public String getNomeRelatorioVereador() {
        return "RELATORIO-EMENDA-VEREADOR";
    }

    public String getNomeRelatorioBeneficiario() {
        return "RELATORIO-EMENDA-BENEFICIARIO";
    }

    public List<Vereador> completarVereador(String parte) {
        try {
            return relatorioEmendaFacade.getVereadorFacade().listaVereadorPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<Emenda> completarEmenda(String parte) {
        try {
            return relatorioEmendaFacade.getEmendaFacade().buscarEmenda(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<BeneficiarioEmenda> completarBeneficiario(String parte) {
        try {
            return relatorioEmendaFacade.getBeneficiarioEmendaFacade().buscarBeneficiarioEmenda(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private Boolean validaDatas() {
        if (this.dataInicial == null || this.dataFinal == null) {
            FacesUtil.addOperacaoNaoPermitida("Favor informar um intervalo de datas.");
            return false;
        }
        if (this.dataInicial.after(this.dataFinal)) {
            FacesUtil.addOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
            return false;
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            FacesUtil.addOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
            return false;
        }
        return true;
    }

    public String getExercicioDaLoa() {
        String exercicio = "";
        LOA loa = getLoaPorExecicio();
        if (loa != null) {
            exercicio = loa.getLdo().getExercicio().toString();
        }
        return exercicio;
    }

    public LOA getLoaPorExecicio() {
        LOA loa = relatorioEmendaFacade.getLoaFacade().listaUltimaLoaPorExercicio(getSistemaControlador().getExercicioCorrente());
        if (loa != null) {
            return loa;
        }
        return null;
    }

    private List<EmendaItem> gerarSql() {
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        montarFiltroData(listaParametros);
        montarFiltroUnidade(listaParametros);
        montarFiltrosGeral(listaParametros);
        filtros = filtros.substring(0, filtros.length() - 1);
        return relatorioEmendaFacade.montarConsultaSql(listaParametros);
    }

    private List<EmendaItem> gerarSqlRelatorioBeneficiario() {
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        montarFiltroData(listaParametros);
        montarFiltroUnidade(listaParametros);
        montarFiltrosGeral(listaParametros);
        filtros = filtros.substring(0, filtros.length() - 1);
        return relatorioEmendaFacade.montarConsultaSqlBeneficiario(listaParametros);
    }

    private void montarFiltrosGeral(List<ParametrosRelatorios> listaParametros) {
        if (this.vereador != null) {
            listaParametros.add(new ParametrosRelatorios(" v.id ", ":idVereador", null, OperacaoRelatorio.IGUAL, vereador.getId(), null, 2, false));
            filtros += " Vereador:  " + vereador.getPessoa().getNome() + " -";
        }
        if (this.emenda != null) {
            listaParametros.add(new ParametrosRelatorios(" e.id ", ":idEmenda", null, OperacaoRelatorio.IGUAL, emenda.getId(), null, 2, false));
        }
        if (this.beneficiarioEmenda != null) {
            listaParametros.add(new ParametrosRelatorios(" bf.id ", ":idBeneficiario", null, OperacaoRelatorio.IGUAL, beneficiarioEmenda.getId(), null, 2, false));
            filtros += "Beneficiário: " + beneficiarioEmenda.getPessoa().getNome();
        }
    }

    private void montarFiltroData(List<ParametrosRelatorios> listaParametros) {
        listaParametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtros = "Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
    }

    private void montarFiltroUnidade(List<ParametrosRelatorios> listaParametros) {
        if (listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<Long>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = relatorioEmendaFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }
    }

    public ConverterAutoComplete getConverterVereador() {
        if (converterVereador == null) {
            converterVereador = new ConverterAutoComplete(Vereador.class, relatorioEmendaFacade.getVereadorFacade());
        }
        return converterVereador;
    }

    public ConverterAutoComplete getConverterEmenda() {
        if (converterEmenda == null) {
            converterEmenda = new ConverterAutoComplete(Emenda.class, relatorioEmendaFacade.getEmendaFacade());
        }
        return converterEmenda;
    }

    public ConverterAutoComplete getConverterBeneficiario() {
        if (converterBeneficiario == null) {
            converterBeneficiario = new ConverterAutoComplete(BeneficiarioEmenda.class, relatorioEmendaFacade.getBeneficiarioEmendaFacade());
        }
        return converterBeneficiario;
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public BeneficiarioEmenda getBeneficiarioEmenda() {
        return beneficiarioEmenda;
    }

    public void setBeneficiarioEmenda(BeneficiarioEmenda beneficiarioEmenda) {
        this.beneficiarioEmenda = beneficiarioEmenda;
    }
}
