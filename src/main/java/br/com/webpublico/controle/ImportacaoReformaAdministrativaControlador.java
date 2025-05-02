package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ImportacaoReformaAdministrativaFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importar-reforma-administrativa", pattern = "/importacao/reforma-administrativa", viewId = "/faces/financeiro/importacao-reforma-administrativa.xhtml")
})
public class ImportacaoReformaAdministrativaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoReformaAdministrativaControlador.class);
    @EJB
    private ImportacaoReformaAdministrativaFacade facade;
    private Workbook workbook;
    private Date finalVigencia;
    private Date inicioVigencia;
    private HashMap<String, HierarquiaOrganizacional> mapHierarquias;

    @URLAction(mappingId = "importar-reforma-administrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        workbook = null;
        finalVigencia = new Date();
        inicioVigencia = new Date();
    }

    public void importar(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            workbook = WorkbookFactory.create(file.getInputstream());
            Sheet abaHierarquia = workbook.getSheetAt(0);
            List<NovaHierarquia> hierarquias = Lists.newArrayList();
            mapHierarquias = Maps.newHashMap();
            for (Integer numeroLinha = 2; numeroLinha <= abaHierarquia.getPhysicalNumberOfRows(); numeroLinha++) {
                List<HierarquiaOrganizacional> orcamentarias = Lists.newArrayList();
                if (abaHierarquia.getRow(numeroLinha - 1).getCell(2) != null) {
                    String[] hierarquiasOrcamentarias;
                    try {
                        hierarquiasOrcamentarias = abaHierarquia.getRow(numeroLinha - 1).getCell(2).getStringCellValue().split(",");
                    } catch (IllegalStateException ise) {
                        abaHierarquia.getRow(numeroLinha - 1).getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                        hierarquiasOrcamentarias = ("0" + String.valueOf(abaHierarquia.getRow(numeroLinha - 1).getCell(2))).split(",");
                    }
                    for (String orcamentaria : hierarquiasOrcamentarias) {
                        orcamentarias.add(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(orcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), facade.getSistemaFacade().getDataOperacao()).get(0));
                    }
                }
                if (abaHierarquia.getRow(numeroLinha - 1).getCell(0) != null) {

                    HierarquiaOrganizacional antiga = facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(abaHierarquia.getRow(numeroLinha - 1).getCell(0).getStringCellValue(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, inicioVigencia);
                    hierarquias.add(new NovaHierarquia(antiga, orcamentarias, criarNovaHierarquia(antiga, abaHierarquia.getRow(numeroLinha - 1).getCell(4).getStringCellValue(), abaHierarquia.getRow(numeroLinha - 1).getCell(5).getStringCellValue())));

                }
                if (abaHierarquia.getRow(numeroLinha - 1).getCell(7) != null && abaHierarquia.getRow(numeroLinha - 1).getCell(8) != null) {
                    hierarquias.add(new NovaHierarquia(null, orcamentarias,
                        criarNovaHierarquia(null, abaHierarquia.getRow(numeroLinha - 1).getCell(7).getStringCellValue(), abaHierarquia.getRow(numeroLinha - 1).getCell(8).getStringCellValue())));
                }
            }
            finalizarVigencias(hierarquias);
            facade.processarHierarquias(hierarquias, mapHierarquias);
            workbook.close();
            FacesUtil.addOperacaoRealizada("As hierarquias foram atualizadas com sucesso!");
        } catch (Exception ex) {
            logger.error("Erro ao importar: " + ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao atualizar as hierarquias: " + ex.getMessage());
        }
    }

    private void finalizarVigencias(List<NovaHierarquia> hierarquias) {
        for (NovaHierarquia hierarquia : hierarquias) {
            if (hierarquia.getAntigaHierarquia() != null) {
                hierarquia.getAntigaHierarquia().setFimVigencia(finalVigencia);
            }
        }
    }


    private HierarquiaOrganizacional criarNovaHierarquia(HierarquiaOrganizacional hierarquiaAntiga, String codigo, String descricao) {
        HierarquiaOrganizacional hierarquiaOrganizacional = new HierarquiaOrganizacional();
        hierarquiaOrganizacional.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        hierarquiaOrganizacional.setCodigo(codigo);
        hierarquiaOrganizacional.setInicioVigencia(inicioVigencia);
        hierarquiaOrganizacional.setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        hierarquiaOrganizacional.setSuperior(buscarSuperiorNoMap(hierarquiaOrganizacional).getSubordinada());
        hierarquiaOrganizacional.setIndiceDoNo(buscarSuperiorNoMap(hierarquiaOrganizacional).getIndiceDoNo() + 1);
        hierarquiaOrganizacional.setNivelNaEntidade(hierarquiaOrganizacional.getNivelPorCodigo());
        hierarquiaOrganizacional.setCodigoNo(getStringNo(hierarquiaOrganizacional, hierarquiaOrganizacional.getIndiceDoNo() - 1));
        if (hierarquiaAntiga != null) {
            hierarquiaOrganizacional.setSubordinada(hierarquiaAntiga.getSubordinada());
        } else {
            hierarquiaOrganizacional.setSubordinada(criarNovaUnidade(descricao));
        }
        hierarquiaOrganizacional.setDescricao(descricao);
        mapHierarquias.put(hierarquiaOrganizacional.getCodigoSemZerosFinais().substring(0, hierarquiaOrganizacional.getCodigoSemZerosFinais().length() - 1), hierarquiaOrganizacional);
        return hierarquiaOrganizacional;
    }

    private String getStringNo(HierarquiaOrganizacional hierarquiaOrganizacional, Integer nivel) {
        String[] split = hierarquiaOrganizacional.getCodigo().split("\\.");
        return split[nivel];
    }

    private HierarquiaOrganizacional buscarSuperiorNoMap(HierarquiaOrganizacional subordinada) {
        String codigo = facade.buscarCodigoSemZerosFinais(subordinada);
        if (subordinada.getCodigo().endsWith("00")) {
            codigo = codigo.substring(0, codigo.lastIndexOf("."));
        }
        if (mapHierarquias.get(codigo) != null) {
            return mapHierarquias.get(codigo);
        }
        mapHierarquias.put(codigo, facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(codigo, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), facade.getSistemaFacade().getDataOperacao()).get(0));
        return mapHierarquias.get(codigo);
    }

    private UnidadeOrganizacional criarNovaUnidade(String descricao) {
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setDescricao(descricao);
        return unidadeOrganizacional;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public class NovaHierarquia {
        private HierarquiaOrganizacional antigaHierarquia;
        private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;
        private HierarquiaOrganizacional novaHierarquia;

        public NovaHierarquia(HierarquiaOrganizacional antigaHierarquia, List<HierarquiaOrganizacional> hierarquiasOrcamentarias, HierarquiaOrganizacional novaHierarquia) {
            this.antigaHierarquia = antigaHierarquia;
            this.hierarquiasOrcamentarias = hierarquiasOrcamentarias;
            this.novaHierarquia = novaHierarquia;
        }

        public HierarquiaOrganizacional getAntigaHierarquia() {
            return antigaHierarquia;
        }

        public void setAntigaHierarquia(HierarquiaOrganizacional antigaHierarquia) {
            this.antigaHierarquia = antigaHierarquia;
        }

        public List<HierarquiaOrganizacional> getHierarquiasOrcamentarias() {
            return hierarquiasOrcamentarias;
        }

        public void setHierarquiasOrcamentarias(List<HierarquiaOrganizacional> hierarquiasOrcamentarias) {
            this.hierarquiasOrcamentarias = hierarquiasOrcamentarias;
        }

        public HierarquiaOrganizacional getNovaHierarquia() {
            return novaHierarquia;
        }

        public void setNovaHierarquia(HierarquiaOrganizacional novaHierarquia) {
            this.novaHierarquia = novaHierarquia;
        }
    }
}
