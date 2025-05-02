/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AplicacaoFinanceiraItem;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioAplicacaoFinanceiraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-aplicacao-financeira", pattern = "/relatorio/aplicacao-financeira/", viewId = "/faces/financeiro/relatorio/relatorioaplicacaofinanceira.xhtml")
})
public class RelatorioAplicacaoFinanceiraControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterBanco;
    private ConverterAutoComplete converterAgencia;
    private ConverterAutoComplete converterContaBancariaEntidade;
    @EJB
    private RelatorioAplicacaoFinanceiraFacade relatorioAplicacaoFinanceiraFacade;
    private Banco banco;
    private Agencia agencia;
    private ContaBancariaEntidade contaBancariaEntidade;
    private SubConta contaFinanceira;
    private Date dataInicial;
    private Date dataFinal;
    private String filtro;
    private List<HierarquiaOrganizacional> listaUnidades;
    @Enumerated(EnumType.STRING)
    private Apresentacao apresentacao;
    private UnidadeGestora unidadeGestora;

    private enum Apresentacao {

        CONSOLIDADO("Consolidado"),
        ORGAO("Orgão"),
        UNIDADE("Unidade"),
        UNIDADE_GESTORA("Unidade Gestora");
        private String descricao;

        private Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Apresentacao ap : Apresentacao.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public RelatorioAplicacaoFinanceiraControlador() {
    }



    @URLAction(mappingId = "relatorio-aplicacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.banco = null;
        this.agencia = null;
        this.contaBancariaEntidade = null;
        this.contaFinanceira = null;
        this.dataInicial = new Date();
        this.dataFinal = new Date();
        this.filtro = "";
        this.listaUnidades = new ArrayList<>();
    }


    public void gerarRelatorio(String tipoRelatorio) {
        try {

            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-aplicacao-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }



    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Conferência de Rendimentos Pagos");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Relatório de Acompanhamento de Atualização Cadastral");
        dto.adicionarParametro("PARAMETROSRELATORIO", ParametrosRelatorios.parametrosToDto(buscarParametros()));
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.adicionarParametro("UNIDADEGESTORA", this.unidadeGestora!= null);
        return dto;
    }

    private List<ParametrosRelatorios> buscarParametros() {
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        listaParametros.add(new ParametrosRelatorios(null, ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        filtro = " Período: " + formataData(dataInicial) + " a " + formataData(dataFinal) + " -";

        if (this.banco != null) {
            listaParametros.add(new ParametrosRelatorios(" banco.id ", ":banco", null, OperacaoRelatorio.IGUAL, banco.getId(), null, 1, false));
            filtro += " Banco: " + this.banco.getNumeroBanco() + " - " + this.banco.getDescricao() + " -";
        }
        if (this.agencia != null) {
            listaParametros.add(new ParametrosRelatorios(" agencia.id ", ":agencia", null, OperacaoRelatorio.IGUAL, agencia.getId(), null, 1, false));
            filtro += " Agência: " + this.agencia.getNomeAgencia() + " -";
        }
        if (this.contaBancariaEntidade != null) {
            listaParametros.add(new ParametrosRelatorios(" CONTABANC.id ", ":contaBancaria", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtro += " Conta Bancária: " + this.contaBancariaEntidade.getNumeroConta() + " - " + this.contaBancariaEntidade.getNomeConta() + " -";
        }
        if (this.contaFinanceira != null) {
            listaParametros.add(new ParametrosRelatorios(" SUB.id ", ":contaFinanceira", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtro += " Conta Financeira: " + this.contaFinanceira.getDescricao() + " -";
        }
        if (this.listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            List<Long> listaIdsUnidades = new ArrayList<>();
            listaUndsUsuarios = relatorioAplicacaoFinanceiraFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }

        if (this.unidadeGestora != null) {
            listaParametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (this.unidadeGestora != null || apresentacao.name().equals("UNIDADE_GESTORA")) {
            listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, sistemaControlador.getExercicioCorrente().getId(), null, 0, false));
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return listaParametros;
    }


    private String formataData(Date data) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if (data != null) {
            return format.format(data);
        } else {
            return "";
        }
    }

    private Boolean validaDatas() {
        if (this.dataInicial == null || this.dataFinal == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validação", "Favor informar um intervalo de datas."));
            return false;
        }
        if (this.dataInicial.after(this.dataFinal)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validação", "Data Inicial não pode ser maior que a Data Final."));
            return false;
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Validação ", "As datas estão com exercícios diferentes"));
            return false;
        }
        return true;
    }

    public List<Banco> completaBanco(String parte) {
        return relatorioAplicacaoFinanceiraFacade.getBancoFacade().listaFiltrando(parte, "descricao");
    }

    public List<Agencia> completaAgencia(String parte) {
        return relatorioAplicacaoFinanceiraFacade.getAgenciaFacade().listaFiltrandoPorBanco(parte, this.banco);
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        return relatorioAplicacaoFinanceiraFacade.getContaBancariaEntidadeFacade().getContabancariaPorAgencia(parte, this.agencia);
    }

    public List<SubConta> completaSubConta(String parte) {
        return relatorioAplicacaoFinanceiraFacade.getSubContaFacade().listaPorContaBancariaEntidade(parte, this.contaBancariaEntidade);
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, relatorioAplicacaoFinanceiraFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, relatorioAplicacaoFinanceiraFacade.getBancoFacade());
        }
        return converterBanco;
    }

    public ConverterAutoComplete getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, relatorioAplicacaoFinanceiraFacade.getAgenciaFacade());
        }
        return converterAgencia;
    }

    public ConverterAutoComplete getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, relatorioAplicacaoFinanceiraFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    public void setaAgencia(SelectEvent evento) {
        agencia = (Agencia) evento.getObject();
    }

    public void setaContaBancariaEntidade(SelectEvent evento) {
        contaBancariaEntidade = (ContaBancariaEntidade) evento.getObject();
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
