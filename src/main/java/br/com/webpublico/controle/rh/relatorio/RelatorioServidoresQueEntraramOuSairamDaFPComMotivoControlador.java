package br.com.webpublico.controle.rh.relatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.rh.relatorio.RelatorioServidoresQueEntraramOuSairamDaFPComMotivoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by William on 01/04/2019.
 */

@ManagedBean(name = "relatorioServidoresQueEntraramOuSairamDaFPComMotivo")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioServidoresQueEntraramOuSairamDaFPComMotivo", pattern = "/relatorio/servidores-entraram-sairam-folha-com-motivo/", viewId = "/faces/rh/relatorios/relatorioservidoresqueentraramousairamdafpcommotivo.xhtml")
})
public class RelatorioServidoresQueEntraramOuSairamDaFPComMotivoControlador extends AbstractReport implements Serializable {

    private Mes mesBase;
    private Integer anoBase;
    private Mes mesFinal;
    private Integer anoFinal;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private EntradaSaida entradaSaida;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String filtro;
    @EJB
    private RelatorioServidoresQueEntraramOuSairamDaFPComMotivoFacade facade;


    public RelatorioServidoresQueEntraramOuSairamDaFPComMotivoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Integer getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(Integer anoFinal) {
        this.anoFinal = anoFinal;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String arquivoJasper = "RelatorioServidoresQueEntraramOuSairamDaFPComMotivo.jasper";
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String imagem = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/img");


            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUÁRIO", usuarioLogado().getNome());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            parameters.put("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            parameters.put("TIPORELATORIO", entradaSaida.getDescricao());
            parameters.put("VERSAO", versao);
            parameters.put("FILTROS", filtro);
            parameters.put("MESANO", mesFinal + "/" + anoFinal);
            parameters.put("TIPOFOLHA", " FOLHA " + tipoFolhaDePagamento.getDescricao());

            Date dataRelatorio = DataUtil.montaData(1, mesFinal.getNumeroMesIniciandoEmZero(), anoFinal).getTime();

            Integer anoAnterior;
            Integer ano;
            Mes mes;
            Mes mesAnterior;
            Boolean entraramNaFolha;
            if (entradaSaida.equals(EntradaSaida.SAIDA)) {
                entraramNaFolha = false;
                anoAnterior = anoFinal;
                mesAnterior = mesFinal;
                mes = mesBase;
                ano = anoBase;
            } else {
                entraramNaFolha = true;
                mes = mesFinal;
                ano = anoFinal;
                mesAnterior = mesBase;
                anoAnterior = anoBase;
            }
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(facade.buscarDadosRelatorio(dataRelatorio, ano, mes, tipoFolhaDePagamento, versao,
                anoAnterior, mesAnterior, entraramNaFolha, buscarOrgao()));

            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo("Servidores Que Entraram Saíram Folha Com Motivo", arquivoJasper, parameters, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório "));
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mesBase == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Base é obrigatório");
        }

        if (anoBase == null || (anoBase == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano Base é obrigatório");
        }

        if (mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês comparação é obrigatório");
        }

        if (anoFinal == null || (anoFinal == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano comparação é obrigatório");
        }

        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório");
        }

        if (versao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Versão é obrigatório");
        }

        ve.lancarException();
    }

    public String buscarOrgao() {
        filtro = "";
        if (hierarquiaOrganizacional != null) {
            filtro += hierarquiaOrganizacional.getSubordinada().getDescricao().toUpperCase();
            return " AND ho.codigo like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%'";
        }
        return null;
    }

    public Mes getMesBase() {
        return mesBase;
    }

    public void setMesBase(Mes mesBase) {
        this.mesBase = mesBase;
    }

    public Integer getAnoBase() {
        return anoBase;
    }

    public void setAnoBase(Integer anoBase) {
        this.anoBase = anoBase;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @URLAction(mappingId = "novoRelatorioServidoresQueEntraramOuSairamDaFPComMotivo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mesBase = Mes.JANEIRO;
        anoBase = sistemaControlador.getExercicioCorrenteAsInteger();
        mesFinal = Mes.JANEIRO;
        anoFinal = sistemaControlador.getExercicioCorrenteAsInteger();
        tipoFolhaDePagamento = null;
        versao = null;
        filtro = null;
        entradaSaida = EntradaSaida.ENTRADA;
        hierarquiaOrganizacional = null;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> retorno = new ArrayList<>();
        for (EntradaSaida entradaSaida : EntradaSaida.values()) {
            retorno.add(new SelectItem(entradaSaida, entradaSaida.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mesBase != null && anoBase != null && tipoFolhaDePagamento != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(mesBase, anoBase, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public EntradaSaida getEntradaSaida() {
        return entradaSaida;
    }

    public void setEntradaSaida(EntradaSaida entradaSaida) {
        this.entradaSaida = entradaSaida;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public enum EntradaSaida {

        ENTRADA("Servidores que entraram na folha"),
        SAIDA("Servidores que saíram da folha");

        private String descricao;

        private EntradaSaida(String descricao) {
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

