package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 22/01/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-conta-financeira", pattern = "/relatorio/conta-financeira", viewId = "/faces/financeiro/relatorio/relatoriocontafinanceira.xhtml")
})
public class RelatorioContaFinanceiraControlador extends AbstractReport implements Serializable {
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<HierarquiaOrganizacional> listaUnidades;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterBanco;
    private ConverterAutoComplete converterAgencia;
    private ConverterAutoComplete converterContaBancariaEntidade;
    private Banco banco;
    private Agencia agencia;
    private ContaBancariaEntidade contaBancariaEntidade;
    private SubConta contaFinanceira;
    private SituacaoConta situacaoConta;
    private String filtro;
    private List<FonteDeRecursos> fontes;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RelatorioContaFinanceiraControlador() {
    }

    public void setaContaBancariaEntidade(SelectEvent evento) {
        contaFinanceira = null;
        contaBancariaEntidade = (ContaBancariaEntidade) evento.getObject();
    }

    public void setaAgencia(SelectEvent evento) {
        agencia = (Agencia) evento.getObject();
    }

    @URLAction(mappingId = "relatorio-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.banco = null;
        this.agencia = null;
        this.contaBancariaEntidade = null;
        this.contaFinanceira = null;
        this.filtro = "";
        this.listaUnidades = Lists.newArrayList();
        this.fontes = Lists.newArrayList();
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("Relação de Conta Financeira");
            dto.setApi("contabil/relacao-conta-financeira/");
            dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco - AC");
            dto.adicionarParametro("WHERE", montarWhere());
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("exercicio", sistemaControlador.getExercicioCorrente().getId());
            dto.adicionarParametro("dataReferencia", sistemaControlador.getDataOperacao());
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private String montarWhere() {
        StringBuilder sql = new StringBuilder();
        filtro = "";
        if (this.banco != null) {
            sql.append(" and ba.id = ").append(banco.getId());
            filtro += " Banco: " + this.banco.getNumeroBanco() + " - " + this.banco.getDescricao() + " -";
        }
        if (this.agencia != null) {
            sql.append(" and ag.id = ").append(agencia.getId());
            filtro += " Agência: " + this.agencia.getNomeAgencia() + " -";
        }
        if (this.contaBancariaEntidade != null) {
            sql.append(" and cb.id = ").append(contaBancariaEntidade.getId());
            filtro += " Conta Bancária: " + this.contaBancariaEntidade.getNumeroConta() + "-" + contaBancariaEntidade.getDigitoVerificador() + " - " + this.contaBancariaEntidade.getNomeConta() + " -";
        }
        if (this.contaFinanceira != null) {
            sql.append(" and s.id = ").append(contaFinanceira.getId());
            filtro += " Conta Financeira: " + this.contaFinanceira.getDescricao() + " -";
        }
        if (this.situacaoConta != null) {
            sql.append(" and s.situacao = '").append(situacaoConta.name()).append("'");
        }
        if (fontes != null && !fontes.isEmpty()) {
            String juncao = "";
            sql.append(" and fonte.id in (");
            filtro += fontes.size() > 1 ? " Fontes de Recursos: " : " Fonte de Recursos: ";
            for (FonteDeRecursos fonte : fontes) {
                sql.append(juncao).append(fonte.getId());
                filtro += juncao + fonte.getCodigo();
                juncao = ", ";
            }
            sql.append(")");
            filtro += " -";
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            StringBuilder unds = new StringBuilder();
            String concatUnd = " ";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatUnd).append(lista.getId());
                unds.append(concatUnd).append(lista.getCodigo());
                concatUnd = ", ";
            }
            sql.append(" and VWORC.ID IN (").append(idUnidades.toString()).append(")");
            filtro += " Unidade(s): " + unds.toString() + " -";
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            StringBuilder unds = new StringBuilder();
            String concatUnd = " ";
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                unds.append(concatUnd).append(lista.getId());
                concatUnd = ", ";
            }
            if (unds.length() != 0) {
                sql.append(" and VWORC.ID IN (").append(unds.toString()).append(")");
            }
        }
        filtro = filtro.length() > 0 ? filtro.substring(0, filtro.length() - 1) : "";
        return sql.toString();
    }

    public List<SelectItem> getSituacaoContaFinanceira() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (SituacaoConta s : SituacaoConta.values()) {
            toReturn.add(new SelectItem(s, s.getDescricao()));
        }
        return toReturn;
    }


    public void recuperaContaBancariaApartirDaContaFinanceira() {
        contaBancariaEntidade = retornoContaBancaria(contaBancariaEntidade);
    }

    public ContaBancariaEntidade retornoContaBancaria(ContaBancariaEntidade contaBancariaEntidade) {
        try {
            return contaFinanceira.getContaBancariaEntidade();
        } catch (Exception e) {
            return new ContaBancariaEntidade();
        }
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte, "descricao", "numeroBanco");
    }

    public List<Agencia> completaAgencia(String parte) {
        if (this.banco != null) {
            return agenciaFacade.listaFiltrandoPorBanco(parte.trim(), this.banco);
        } else {
            return agenciaFacade.listaFiltrandoAtributosAgenciaBanco(parte.trim());
        }
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        if (this.agencia != null) {
            return contaBancariaEntidadeFacade.getContabancariaPorAgencia(parte, this.agencia);
        } else {
            return contaBancariaEntidadeFacade.listaPorCodigo(parte);
        }
    }

    public List<SubConta> completaSubConta(String parte) {
        if (this.contaBancariaEntidade != null) {
            return subContaFacade.listaPorContaBancariaEntidade(parte, this.contaBancariaEntidade);
        } else {
            return subContaFacade.listaPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
        }
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, subContaFacade);
        }
        return converterContaFinanceira;
    }

    public ConverterAutoComplete getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public ConverterAutoComplete getConverterAgencia() {
        if (converterAgencia == null) {
            converterAgencia = new ConverterAutoComplete(Agencia.class, agenciaFacade);
        }
        return converterAgencia;
    }

    public ConverterAutoComplete getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterAutoComplete(ContaBancariaEntidade.class, contaBancariaEntidadeFacade);
        }
        return converterContaBancariaEntidade;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public SituacaoConta getSituacaoConta() {
        return situacaoConta;
    }

    public void setSituacaoConta(SituacaoConta situacaoConta) {
        this.situacaoConta = situacaoConta;
    }

    public List<FonteDeRecursos> getFontes() {
        return fontes;
    }

    public void setFontes(List<FonteDeRecursos> fontes) {
        this.fontes = fontes;
    }
}
