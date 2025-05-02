/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-servidores-pasep", pattern = "/relatorio/servidores-pasep/", viewId = "/faces/rh/relatorios/relatorioservidorespasep.xhtml")
})
public class RelatorioServidoresPASEPControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private OpcoesFiltroRelatorioServidoresPASEP opcoesFiltroRelatorio;
    private Boolean semPispasep;
    private String filtros;

    public RelatorioServidoresPASEPControlador() {
        this.semPispasep = Boolean.TRUE;
        this.geraNoDialog = Boolean.TRUE;
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        String sql = gerarSql();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE SERVIDORES PASEP");
        dto.adicionarParametro("SQL", sql);
        dto.adicionarParametro("FILTROS", filtros);
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("SUBREPORT_DIR", getCaminho());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("TIPORELATORIO", getOpcoesFiltroRelatorio().getToDto());
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE SERVIDORES PASEP");
        dto.setApi("rh/relatorio-de-servidores-pasep/");
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        return dto;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de Servidores PASEP: {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioServidoresPASEP) {
        try {
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioServidoresPASEP));
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório de Servidores PASEP: {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String gerarSql() {
        filtros = "";
        StringBuilder stb = new StringBuilder(" ");
        String juncao = " AND ";

        if (!this.semPispasep) {
            stb.append(juncao).append(" ct.pispasep is null ");
        } else {
            stb.append(juncao + " ct.pispasep is not null ");
        }
        filtros += "Servidores: " + (semPispasep ? " COM PISPASEP ": "SEM PISPASEP ");
        return stb.toString();
    }

    @URLAction(mappingId = "relatorio-servidores-pasep", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        semPispasep = Boolean.TRUE;
        opcoesFiltroRelatorio = OpcoesFiltroRelatorioServidoresPASEP.TODOS;
        filtros = "";
    }

    public List<SelectItem> opcoesDeSituacao() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (OpcoesFiltroRelatorioServidoresPASEP o : OpcoesFiltroRelatorioServidoresPASEP.values()) {
            lista.add(new SelectItem(o, o.getDescricao()));
        }
        return lista;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public OpcoesFiltroRelatorioServidoresPASEP getOpcoesFiltroRelatorio() {
        return opcoesFiltroRelatorio;
    }

    public void setOpcoesFiltroRelatorio(OpcoesFiltroRelatorioServidoresPASEP opcoesFiltroRelatorio) {
        this.opcoesFiltroRelatorio = opcoesFiltroRelatorio;
    }

    public Boolean getSemPispasep() {
        return semPispasep;
    }

    public void setSemPispasep(Boolean semPispasep) {
        this.semPispasep = semPispasep;
    }
}
