/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova_importacao-material", pattern = "/importacao-arquivo-material/", viewId = "/faces/administrativo/materiais/importacao-arquivos/edita.xhtml"),
    @URLMapping(id = "nova_importacao-material-levantamento", pattern = "/importacao-arquivo-levantamento-estoque-material/", viewId = "/faces/administrativo/materiais/importacao-arquivos/importacao-levantamento-estoque.xhtml")
})
public class ImportacaoArquivosControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associacaoMaterial;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LevantamentoEstoqueFacade levantamentoEstoqueFacade;

    private List<Material> materiais;
    private List<String> errosLevantamento;
    private HashMap<Material, List<String>> mapErros;
    private HierarquiaOrganizacional administrativa;
    private HierarquiaOrganizacional orcamentaria;
    private LocalEstoque localEstoque;
    private LevantamentoEstoque levantamento;
    private UploadedFile fileMaterial;
    private UploadedFile fileLevantamento;


    public List<Material> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<Material> materiais) {
        this.materiais = materiais;
    }

    public HashMap<Material, List<String>> getMapErros() {
        return mapErros;
    }

    public void setMapErros(HashMap<Material, List<String>> mapErros) {
        this.mapErros = mapErros;
    }

    public List<String> getErrosLevantamento() {
        return errosLevantamento;
    }

    public void setErrosLevantamento(List<String> errosLevantamento) {
        this.errosLevantamento = errosLevantamento;
    }

    public HierarquiaOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(HierarquiaOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    public HierarquiaOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(HierarquiaOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public LevantamentoEstoque getLevantamento() {
        return levantamento;
    }

    public void setLevantamento(LevantamentoEstoque levantamento) {
        this.levantamento = levantamento;
    }

    public List<String> recuperarErros(Material material) {
        try {
            return getMapErros().get(material);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            materiais = Lists.newArrayList();
            fileMaterial = event.getFile();
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(fileMaterial.getInputstream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            int rowsCount = sheet.getLastRowNum();
            for (int linha = 1; linha <= rowsCount; linha++) {

                Row row = sheet.getRow(linha);
                if (row != null) {
                    int colCounts = row.getLastCellNum();
                    Cell cellLinha = row.getCell(1);
                    if (cellLinha == null || cellLinha.getCellType() == Cell.CELL_TYPE_BLANK) {
                        continue;
                    }
                    Material material = new Material();
                    material.setStatusMaterial(StatusMaterial.AGUARDANDO);
                    material.setDataRegistro(sistemaFacade.getDataOperacao());

                    for (int coluna = 0; coluna < colCounts; coluna++) {
                        Cell cell = row.getCell(coluna);
                        String valorCell = ExcelUtil.getValorCell(cell);
                        if (!Strings.isNullOrEmpty(valorCell)) {
                            if (coluna == 0) {
                                long valorInt = (long) Double.parseDouble(valorCell);
                                material.setCodigo(valorInt);
                            }
                            if (coluna == 1) {
                                String codigo = getValorCell(cell);
                                material.setObjetoCompra(objetoCompraFacade.recuperarPorCodigoOuDescricao(codigo));
                            }
                            if (coluna == 2) {
                                material.setDescricao(valorCell);
                            }
                            if (coluna == 3) {
                                buscarUnidadeMedida(material, valorCell);
                            }
                        }
                    }
                    materiais.add(material);
                }
            }
            preecherMap();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao importar arquivo. " + e.getMessage());
        }
    }

    private String getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        }
        return "";
    }

    public void preecherMap() {
        mapErros = new HashMap<>();
        for (Material material : materiais) {
            try {
                buscarAssociacao(material);
                Util.validarCampos(material);
            } catch (ValidacaoException e) {
                List<String> erros = new ArrayList<>();
                for (FacesMessage facesMessage : e.getAllMensagens()) {
                    erros.add(facesMessage.getDetail());
                }
                mapErros.put(material, erros);
            } catch (ExcecaoNegocioGenerica ex) {
                if (mapErros.get(material) != null) {
                    mapErros.get(material).add(ex.getMessage());
                } else {
                    List<String> erros = new ArrayList<>();
                    erros.add(ex.getMessage());
                    mapErros.put(material, erros);
                }
            }
        }
    }

    public void buscarAssociacao(Material material) throws ExcecaoNegocioGenerica {
        if (material.getObjetoCompra() != null) {
            material.setGrupo(associacaoMaterial.buscarAssociacaoPorGrupoObjetoCompraImportacao(material.getObjetoCompra().getGrupoObjetoCompra()).getGrupoMaterial());
        }
    }

    public void buscarUnidadeMedida(Material material, String valorCell) throws ExcecaoNegocioGenerica {
        try {
            material.setUnidadeMedida(unidadeMedidaFacade.buscarUnidadeMedidaPorDescricao(valorCell));
        } catch (Exception ex) {
        }
    }

    private HierarquiaOrganizacional buscarOrcamentaria(String descricao) {
        HierarquiaOrganizacional orc = null;
        try {
            orc = hierarquiaOrganizacionalFacade.listaFiltrandoPorOrgaoAndTipoOrcamentaria(descricao, sistemaFacade.getDataOperacao()).get(0);
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade Orçamentária para a " + descricao + ".");
        }
        return orc;
    }

    private HierarquiaOrganizacional buscarAdministrativa(String descricao) {
        HierarquiaOrganizacional orc = null;
        try {
            orc = hierarquiaOrganizacionalFacade.listaFiltrandoPorOrgaoAndTipoAdministrativa(descricao).get(0);
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade Orçamentária para a " + descricao + ".");
        }
        return orc;
    }

    private Material buscarMaterial(String codigo) {
        Material mat = null;
        try {
            mat = materialFacade.buscaMaterialPorCodigo(Long.valueOf(codigo.trim()));
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o material para o codigo " + codigo + ".");
        }
        return mat;
    }

    public void salvar() {
        try {
            validarSalvarMaterial();
            for (Material materiais : this.materiais) {
                materialFacade.salvarNovo(materiais);
            }
            FacesUtil.redirecionamentoInterno("/material/listar/");
            FacesUtil.addOperacaoRealizada("Registro(s) salvo(s) com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSalvarMaterial() {

        ValidacaoException ve = new ValidacaoException();
        if (fileMaterial == null || materiais.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um arquivo para realizar a importação");
        }
        ve.lancarException();

        if (mapErros != null && !mapErros.isEmpty()) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Inconsistências Encontradas! Para continuar verifique suas informações na planilha.");
        }

        for (Material material : materiais) {
            if (mapErros.get(material) != null && !mapErros.get(material).isEmpty()) {
                String msgs = "";
                for (String msg : mapErros.get(material)) {
                    msgs += msg.replace("[", "").replace("]", "") + ", ";
                }
                msgs = msgs.substring(0, msgs.length() - 2);
                ve.adicionarMensagemDeOperacaoNaoPermitida("O material " + material.getCodigo() + " possui campos nulos: " + msgs);
            }
        }
        ve.lancarException();
    }

    public void salvarLevantamento() {
        if (fileLevantamento == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe um arquivo para realizar a importação");
        } else {
            levantamentoEstoqueFacade.salvar(levantamento);
            FacesUtil.redirecionamentoInterno("/levantamento-estoque/listar/");
            FacesUtil.addOperacaoRealizada("Registro(s) salvo(s) com sucesso!");
        }
    }

    public String getStyleRow(Material material) {
        try {
            return !recuperarErros(material).isEmpty() ? "color: red" : "";
        } catch (Exception ex) {
            return "";
        }
    }

    public void handleFilesUploadsLevantamento(FileUploadEvent event) {
        try {
            validarCamposObrigatorios();
            errosLevantamento = new ArrayList<>();
            fileLevantamento = event.getFile();
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(fileLevantamento.getInputstream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(2);
            int rowsCount = sheet.getLastRowNum();
            levantamento = new LevantamentoEstoque();
            levantamento.setCodigo(singletonGeradorCodigo.getProximoCodigo(LevantamentoEstoque.class, "codigo"));
            levantamento.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            levantamento.setDataLevantamento(sistemaFacade.getDataOperacao());
            levantamento.setObservacoes("Levantamento realizado através de importação de planilha " + event.getFile().getFileName());
            levantamento.setHierarquiaOrcamentaria(orcamentaria);
            levantamento.setHierarquiaAdministrativa(administrativa);
            levantamento.setUnidadeOrcamentaria(orcamentaria.getSubordinada());
            levantamento.setUnidadeAdministrativa(administrativa.getSubordinada());
            levantamento.setLocalEstoque(localEstoque);
            try {
                Util.validarCampos(levantamento);
                for (int i = 1; i <= rowsCount; i++) {
                    Row row = sheet.getRow(i);
                    ItemLevantamentoEstoque item = new ItemLevantamentoEstoque();
                    item.setLevantamentoEstoque(levantamento);
                    String toString = "";
                    if (row != null) {
                        int colCounts = row.getLastCellNum();
                        for (int x = 0; x < colCounts; x++) {
                            Cell cell = row.getCell(x);
                            String valorCell = ExcelUtil.getValorCell(cell);

                            if (x == 4) {
                                toString += " Código " + valorCell;
                                item.setMaterial(buscarMaterial(valorCell));
                            }
                            if (x == 5) {
                                toString += " Quantidade " + valorCell;
                                item.setQuantidade(new BigDecimal(valorCell.trim()));
                            }
                            if (x == 6) {
                                toString += " Valor " + valorCell;
                                item.setValorUnitario(new BigDecimal(valorCell.trim()));
                            }
                        }
                    }
                    try {
                        Util.validarCampos(item);
                        levantamento.getItensLevantamentoEstoque().add(item);
                    } catch (ValidacaoException ve) {
                        adicionaErro(ve, toString);
                    }
                }
            } catch (ValidacaoException ex) {
                adicionaErro(ex, levantamento.toString());
            } catch (Exception e) {
                errosLevantamento.add(e.getMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao importar arquivo. " + e.getMessage());
        }
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (administrativa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa é obrigatório!");
        }
        if (orcamentaria == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária é obrigatório!");
        }
        if (localEstoque == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Local Estoque é obrigatório!");
        }
        ve.lancarException();
    }

    public void adicionaErro(ValidacaoException ex, String toString) {
        List<String> erros = new ArrayList<>();
        for (FacesMessage facesMessage : ex.getAllMensagens()) {
            erros.add(facesMessage.getDetail() + " para o " + toString);
        }
        errosLevantamento.addAll(erros);
    }

    public List<LocalEstoque> buscarLocalEstoque(String filtro) {
        if (administrativa != null) {
            return localEstoqueFacade.completarLocalEstoqueAbertos(filtro, administrativa.getSubordinada());
        }
        return localEstoqueFacade.completarLocalEstoqueAbertos(filtro);
    }
}
