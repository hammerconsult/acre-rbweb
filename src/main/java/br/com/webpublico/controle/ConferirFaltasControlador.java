/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.RelatorioConferenciaFaltas;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FaltasFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.util.*;

/**
 * @author boy
 */
@ManagedBean(name = "conferirFaltasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultarFaltas", pattern = "/faltas/consultar/", viewId = "/faces/rh/administracaodepagamento/faltas/conferirfaltas.xhtml"),
})
public class ConferirFaltasControlador extends PrettyControlador<Faltas> {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FaltasFacade faltasFacade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<Faltas> listaDeFaltas;
    private Boolean mostraPainelFaltas;

    public ConferirFaltasControlador() {
        super(Faltas.class);
        limpaCampos();
    }

    @Override
    public AbstractFacade getFacede() {
        return faltasFacade;
    }

    @URLAction(mappingId = "consultarFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mostraPainelFaltas = false;
        listaDeFaltas = Lists.newArrayList();
        hierarquiaOrganizacionalSelecionada = new HierarquiaOrganizacional();
        dataFinal = null;
        dataInicial = null;
    }

    public void imprimir() {
        try {
            String arquivoJasper = "RelacaoFaltas.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(montarRelatorio());
            HashMap parameters = new HashMap();
            AbstractReport report = AbstractReport.getAbstractReport();
            parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE RELAÇÃO DE FALTAS");
            parameters.put("IMAGEM", report.getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", report.getCaminho());

            report.setGeraNoDialog(true);
            report.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (Exception ex) {
            logger.error("Erro ao emitir relatório: " + ex.getMessage());
        }
    }

    private List<RelatorioConferenciaFaltas> montarRelatorio() {
        List<RelatorioConferenciaFaltas> faltasRelatorio = Lists.newArrayList();
        for (Faltas listaDeFalta : listaDeFaltas) {
            RelatorioConferenciaFaltas faltaRelatorio = new RelatorioConferenciaFaltas();
            faltaRelatorio.setNome(listaDeFalta.getContratoFP().getMatriculaFP().getPessoa().getNome());
            faltaRelatorio.setMatricula(listaDeFalta.getContratoFP().getMatriculaFP().getMatricula() + "/" + listaDeFalta.getContratoFP().getNumero());
            faltaRelatorio.setTotalFaltas(listaDeFalta.getTotalDias().intValue());
            faltaRelatorio.setOrgao(hierarquiaOrganizacionalSelecionada.toString());
            for (Faltas falta : recuperaListaLancamentosFaltas(listaDeFalta.getContratoFP())) {
                Faltas obj = new Faltas();
                obj.setInicio(falta.getInicio());
                obj.setFim(falta.getFim());
                obj.setTipoDaFalta(falta.getTipoFalta().getDescricao());
                obj.setDias(falta.getDias());
                faltaRelatorio.getFaltas().add(obj);
            }
            faltasRelatorio.add(faltaRelatorio);
        }
        return faltasRelatorio;
    }

    public Boolean getMostraPainelFaltas() {
        return mostraPainelFaltas;
    }

    public void setMostraPainelFaltas(Boolean mostraPainelFaltas) {
        this.mostraPainelFaltas = mostraPainelFaltas;
    }

    public List<Faltas> getListaDeFaltas() {
        return listaDeFaltas;
    }

    public void setListaDeFaltas(List<Faltas> listaDeFaltas) {
        this.listaDeFaltas = listaDeFaltas;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<Faltas> recuperaListaLancamentosFaltas(ContratoFP contrato) {
        return faltasFacade.recuperaFaltasPorContratoPeriodo(contrato, dataInicial, dataFinal);
    }

    public Boolean validaCampos() {
        boolean valida = true;
        if (dataInicial == null) {
            FacesUtil.addWarn("Atenção", "A data inicial deve ser informada");
            valida = false;
        }

        if (dataFinal == null) {
            FacesUtil.addWarn("Atenção", "A data final deve ser informada");
            valida = false;
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            FacesUtil.addWarn("Atenção", "O Local de Trabalho deve ser informado");
            valida = false;
        }

        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal)) {
            FacesUtil.addWarn("Atenção", "A data inicial não pode ser posterior a data final");
            valida = false;
        }
        return valida;
    }

    public void consultar() {
        if (validaCampos()) {
            listaDeFaltas = faltasFacade.recuperaFaltasConferencia(hierarquiaOrganizacionalSelecionada, dataInicial, dataFinal);
            Collections.sort(listaDeFaltas, new Comparator<Faltas>() {
                @Override
                public int compare(Faltas o1, Faltas o2) {
                    return o1.getContratoFP().getMatriculaFP().getPessoa().getNome().compareTo(o2.getContratoFP().getMatriculaFP().getPessoa().getNome());
                }
            });
        }
    }

}
