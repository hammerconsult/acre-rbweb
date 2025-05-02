/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LoteProcessamento;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.OpcoesFiltroRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.LoteProcessamentoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioRemuneracaServidores", pattern = "/relatorio/remuneracao-servidores/novo/", viewId = "/faces/rh/relatorios/relatorioremuneracaoservidores.xhtml")
})
public class RelatorioRemuneracaoServidoresControlador extends AbstractReport implements Serializable {

    private Integer mes;
    private Integer ano;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
    private String matriculaInicial;
    private String matriculaFinal;
    private String pisPasepInicial;
    private String pisPasepFinal;
    private List<HierarquiaOrganizacional> grupoHierarquia;
    private HierarquiaOrganizacional[] hoSelecionadas;
    private LoteProcessamento loteProcessamento;
    private List<VinculoFP> vinculos;
    private OpcoesFiltroRelatorio opcoesFiltroRelatorio;

    public RelatorioRemuneracaoServidoresControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            String filtros = gerarSql();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getDataVigencia()));
            dto.adicionarParametro("MES", (mes - 1));
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("GRUPO", getMontaGrupoHO());
            dto.adicionarParametro("LOTES", montaIdsContratos());
            dto.adicionarParametro("opcoesFiltroRelatorio", opcoesFiltroRelatorio);
            dto.setNomeRelatorio("RELATORIO-REMUNERACAO-DOS-SERVIDORES-"+opcoesFiltroRelatorio.name());
            dto.setApi("rh/remuneracao-servidores/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private Date getDataVigencia() {
        if (mes != null && ano != null) {
            return DataUtil.criarDataComMesEAno(mes, ano).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    private String getMontaGrupoHO() {
        String retorno = " AND ( ";
        for (HierarquiaOrganizacional ho : hoSelecionadas) {
            retorno += " ho.codigo like ";
            retorno += "'%" + ho.getCodigoSemZerosFinais() + "%'";
            retorno += " or ";
        }
        retorno = retorno.substring(0, retorno.length() - 3);

        retorno += ") ";
        return retorno;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (opcoesFiltroRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo estado do servidor deve ser informado.");
        }
        if (hoSelecionadas.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o(s) órgão(s) para gerar o relatório.");
        }
        ve.lancarException();
        if (ano == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Ano não pode ser igual a 0.");
        }
        if (mes < 1 || mes > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (this.matriculaInicial != null && this.matriculaFinal != null) {
            if (this.matriculaFinal.compareTo(this.getMatriculaInicial()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Matrícula Inicial não pode ser maior que a Matrícula Final.");
            }
        }
        if (this.pisPasepInicial != null && this.pisPasepFinal != null) {
            if (this.pisPasepFinal.compareTo(this.getPisPasepInicial()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Pis/Pasep Inicial não pode ser maior que o Pis/Pasep Final.");
            }
        }
        ve.lancarException();
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        String juncao = " AND ";
        if (this.matriculaInicial != null && !this.matriculaInicial.equals("") && this.matriculaFinal != null && !this.matriculaFinal.equals("")) {
            stb.append(juncao).append(" MATRICULA.MATRICULA BETWEEN ").append(this.matriculaInicial).append(juncao).append(this.matriculaFinal);
        }

        if (this.pisPasepInicial != null && !this.getPisPasepInicial().equals("") && this.pisPasepFinal != null && !this.pisPasepFinal.equals("")) {
            stb.append(juncao).append(" ct.pispasep between ").append(this.pisPasepInicial).append(juncao).append(this.pisPasepFinal);
        }
        return stb.toString();
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(String matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public String getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(String matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }

    public String getPisPasepInicial() {
        return pisPasepInicial;
    }

    public void setPisPasepInicial(String pisPasepInicial) {
        this.pisPasepInicial = pisPasepInicial;
    }

    public String getPisPasepFinal() {
        return pisPasepFinal;
    }

    public void setPisPasepFinal(String pisPasepFinal) {
        this.pisPasepFinal = pisPasepFinal;
    }

    public List<HierarquiaOrganizacional> getGrupoHierarquia() {
        return grupoHierarquia;
    }

    public void setGrupoHierarquia(List<HierarquiaOrganizacional> grupoHierarquia) {
        this.grupoHierarquia = grupoHierarquia;
    }

    public HierarquiaOrganizacional[] getHOSelecionadas() {
        return hoSelecionadas;
    }

    public void setHOSelecionadas(HierarquiaOrganizacional[] HOSelecionadas) {
        this.hoSelecionadas = HOSelecionadas;
    }

    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public List<VinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<VinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    public OpcoesFiltroRelatorio getOpcoesFiltroRelatorio() {
        return opcoesFiltroRelatorio;
    }

    public void setOpcoesFiltroRelatorio(OpcoesFiltroRelatorio opcoesFiltroRelatorio) {
        this.opcoesFiltroRelatorio = opcoesFiltroRelatorio;
    }

    @URLAction(mappingId = "gerarRelatorioRemuneracaServidores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        recuperarHOs();
        this.mes = null;
        this.ano = null;
        this.matriculaInicial = null;
        this.matriculaFinal = null;
        this.pisPasepInicial = null;
        this.pisPasepFinal = null;
        this.opcoesFiltroRelatorio = null;
        this.hoSelecionadas = null;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getLotesProcessamentos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        if (mes != null && mes > 0) {
            for (LoteProcessamento lote : loteProcessamentoFacade.filtrandoByMesAno(mes, ano)) {
                retorno.add(new SelectItem(lote, lote.getDescricao()));
            }
        }
        return retorno;
    }

    public List<SelectItem> getOpcoesSituacoesRelatorio() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (OpcoesFiltroRelatorio o : OpcoesFiltroRelatorio.values()) {
            lista.add(new SelectItem(o, o.getDescricao()));
        }
        return lista;
    }

    private String montaIdsContratos() {
        String retorno = "";
        if (loteProcessamento != null) {
            vinculos = new ArrayList<>();
            vinculos = loteProcessamentoFacade.findVinculosByLote(loteProcessamento);
            if (!vinculos.isEmpty()) {
                String ids = " AND cont.id in ( ";
                for (VinculoFP vinculo : vinculos) {
                    ids += vinculo.getId() + ",";
                }
                retorno = ids.substring(0, ids.length() - 1) + ")";
                ;
            }
        }

        return retorno;
    }

    public void recuperarHOs() {
        grupoHierarquia = hierarquiaOrganizacionalFacade.listaTodasPorNivel("", "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia());
    }
}
