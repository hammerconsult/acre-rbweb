package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteLevantamentoBemImovel;
import br.com.webpublico.entidadesauxiliares.FiltroLevantamentoColetaDados;
import br.com.webpublico.entidadesauxiliares.LevantamentoColetaDadosItemVo;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoLevantamentoImovelColetaDados;
import br.com.webpublico.enums.TipoColetaDados;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LevantamentoColetaDadosFacade;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-lev-imovel-coleta-dados", pattern = "/levantamento-imovel-coleta-dados/novo/", viewId = "/faces/administrativo/patrimonio/lev-bem-imovel-coleta-dados/edita.xhtml"),
    @URLMapping(id = "editar-lev-imovel-coleta-dados", pattern = "/levantamento-imovel-coleta-dados/editar/#{levantamentoColetaDadosControlador.id}/", viewId = "/faces/administrativo/patrimonio/lev-bem-imovel-coleta-dados/edita.xhtml"),
    @URLMapping(id = "listar-lev-imovel-coleta-dados", pattern = "/levantamento-imovel-coleta-dados/listar/", viewId = "/faces/administrativo/patrimonio/lev-bem-imovel-coleta-dados/lista.xhtml"),
    @URLMapping(id = "ver-lev-imovel-coleta-dados", pattern = "/levantamento-imovel-coleta-dados/ver/#{levantamentoColetaDadosControlador.id}/", viewId = "/faces/administrativo/patrimonio/lev-bem-imovel-coleta-dados/visualizar.xhtml")
})
public class LevantamentoColetaDadosControlador extends PrettyControlador<LevantamentoColetaDados> implements Serializable, CRUD {

    @EJB
    private LevantamentoColetaDadosFacade facade;
    private Future<AssistenteLevantamentoBemImovel> future;
    private AssistenteLevantamentoBemImovel assistente;
    private List<LevantamentoColetaDadosItemVo> levantamentos;
    private FiltroLevantamentoColetaDados filtroColetaDados;

    public LevantamentoColetaDadosControlador() {
        super(LevantamentoColetaDados.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/levantamento-imovel-coleta-dados/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-lev-imovel-coleta-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataColeta(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        novoAssistente();
        novoFiltro();
    }

    @Override
    @URLAction(mappingId = "editar-lev-imovel-coleta-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        operacao = Operacoes.EDITAR;
        recuperarObjeto();
    }

    @URLAction(mappingId = "ver-lev-imovel-coleta-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        selecionado.setItens(facade.buscarItens(selecionado));
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            future = facade.salvarColetaDados(assistente, levantamentos);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void confirmarColeta() {
        try {
            novoAssistente();
            future = facade.confirmarColetaDados(assistente);
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void aprovarTodasColeta() {
        selecionado.getItens().forEach(item -> item.setSituacao(SituacaoLevantamentoImovelColetaDados.APROVADO));
    }

    public void aprovarColeta(LevantamentoColetaDadosItem item) {
        item.setSituacao(SituacaoLevantamentoImovelColetaDados.APROVADO);
    }

    public void importarPlanilha(FileUploadEvent event) {
        levantamentos = Lists.newArrayList();
        try {
            UploadedFile file = event.getFile();
            Workbook workbook = WorkbookFactory.create(file.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);

            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                Iterator<Cell> cell = row.cellIterator();
                if (row.getRowNum() > 1) {
                    LevantamentoColetaDadosItemVo levVo = new LevantamentoColetaDadosItemVo();
                    LevantamentoBemImovel levImovel = new LevantamentoBemImovel();
                    while (cell.hasNext()) {
                        HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(cell.next().getStringCellValue(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, facade.getSistemaFacade().getDataOperacao());
                        if (hoAdm != null) {
                            levImovel.setUnidadeAdministrativa(hoAdm.getSubordinada());
                        } else {
                            levImovel.setUnidadeAdministrativa(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
                        }
                        break;
                    }
                    levVo.setLevantamentoBemImovel(levImovel);
                    levantamentos.add(levVo);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }

    private void percorrerCelulas(Row row) {
        LevantamentoColetaDadosItemVo levVo = new LevantamentoColetaDadosItemVo();
        LevantamentoBemImovel levImovel = new LevantamentoBemImovel();

        for (LevantamentoColetaDadosItemVo.CampoImportacao coluna : LevantamentoColetaDadosItemVo.CampoImportacao.values()) {
            Cell cell = row.getCell(coluna.ordinal());

            String valorCelula = getValorCell(cell);
            if (!Strings.isNullOrEmpty(valorCelula)) {
                switch (coluna) {
                    case UNIDADE_ADM:
                        HierarquiaOrganizacional hoAdm = facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(valorCelula.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, facade.getSistemaFacade().getDataOperacao());
                        if (hoAdm != null) {
                            levImovel.setUnidadeAdministrativa(hoAdm.getSubordinada());
                        } else {
                            levImovel.setUnidadeAdministrativa(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
                        }
                        break;
                    case UNIDADE_ORC:
                        HierarquiaOrganizacional hoOrc = facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(valorCelula.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA, facade.getSistemaFacade().getDataOperacao());
                        if (hoOrc != null) {
                            levImovel.setUnidadeOrcamentaria(hoOrc.getSubordinada());
                        } else {
                            levImovel.setUnidadeOrcamentaria(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
                        }
                        break;
                    case GRUPO_PATRIMONIAL:
                        break;
                    case REGISTRO_PATRIMONIAL:
                        break;
                    case LOCALIZACAO:
                        break;
                    case DESCRICAO_IMOVEL:
                        break;
                    case CONDICAO_OCUPACAO:
                        break;
                    case ESTADO_CONSERVACAO:
                        break;
                    case SITUACAO_CONSERVACAO:
                        break;
                    case DATA_AQUISICAO:
                        break;
                    case VALOR_ATUAL_BEM:
                        break;
                    case TIPO_AQUISICAO:
                        break;

                    default:
                        break;
                }
            }
        }
        levVo.setLevantamentoBemImovel(levImovel);
        levantamentos.add(levVo);
    }

    private String getValorCell(Cell cell) {
        if (cell == null || cell.getStringCellValue().equals(getNomePlanilhaExterna())) {
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

    public StreamedContent exportarPlanilha() {
        try {
            ExcelUtil excel = new ExcelUtil();
            String nomePlanilha = getNomePlanilhaExterna() + "_temp";
            File file = File.createTempFile(nomePlanilha, "xls");
            int linhaInicial = 0;

            FileOutputStream fout = new FileOutputStream(file);
            XSSFWorkbook pastaDeTrabalho = new XSSFWorkbook();
            XSSFSheet sheet = excel.criarSheet(pastaDeTrabalho, nomePlanilha);

            linhaInicial = adicionarTituloColunas(excel, linhaInicial, sheet);
            linhaInicial++;

            XSSFRow titulo = excel.criaRow(sheet, linhaInicial);
            excel.criaCell(titulo, linhaInicial).setCellValue("Todos os campos são obrigatórios e não devem conter espaços.");

            pastaDeTrabalho.write(fout);


            InputStream stream = Files.newInputStream(file.toPath());
            return new DefaultStreamedContent(stream, "application/xls", getNomePlanilhaExterna() + ".xls");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a exportação da planilha: " + ex.getMessage());
        }
        return null;
    }

    private String getNomePlanilhaExterna() {
        return "Levantamento de Bem Imóvel";
    }


    private int adicionarTituloColunas(ExcelUtil excel, int linhaInicial, XSSFSheet sheet) {
        XSSFRow titulo = excel.criaRow(sheet, linhaInicial);
        excel.criaCell(titulo, linhaInicial).setCellValue(getNomePlanilhaExterna());
        linhaInicial++;

        List<String> titulosColuna = Lists.newArrayList();
        for (LevantamentoColetaDadosItemVo.CampoImportacao campo : LevantamentoColetaDadosItemVo.CampoImportacao.values()) {
            titulosColuna.add(campo.getDescricao());
        }
        XSSFRow cabecalho = excel.criaRow(sheet, linhaInicial);
        for (String atributo : titulosColuna) {
            excel.criaCell(cabecalho, titulosColuna.indexOf(atributo)).setCellValue(atributo);
        }
        return linhaInicial;
    }

    private void novoAssistente() {
        assistente = new AssistenteLevantamentoBemImovel();
        assistente.setLevantameno(selecionado);
        assistente.setUnidadeAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        assistente.setUnidadeOrc(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public void acompanharFuture() {
        if (future.isDone() || future.isCancelled()) {
            FacesUtil.executaJavaScript("finalizarFuture()");
            redireciona();
        }
    }

    public void pesquisarBci() {
        if (assistente != null) {
            levantamentos = facade.buscarCadastroImobiliarioLevantamentoImovel(filtroColetaDados.getCondicaoPesquisaBci());
        }
    }

    public void novoFiltro() {
        filtroColetaDados = new FiltroLevantamentoColetaDados();
    }

    public List<Logradouro> completarLogradouro(String parte) {
        if (filtroColetaDados.getBairro() != null) {
            return facade.getLogradouroFacade().completaLogradourosPorBairro(parte.trim(), filtroColetaDados.getBairro());
        }
        return facade.getLogradouroFacade().listaFiltrando(parte.trim(), "nome", "nomeUsual", "nomeAntigo");
    }

    public List<Bairro> completarBairro(String parte) {
        return facade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<Lote> completarLote(String parte) {
        return facade.getLoteFacade().listaFiltrando(parte.trim(), "codigoLote");
    }

    public List<Propriedade> completarProprietario(String parte) {
        return facade.getPropriedadeFacade().buscarFiltrandoPropriedade(parte.trim());
    }

    public List<SelectItem> getTiposColetas() {
        return Util.getListSelectItemSemCampoVazio(TipoColetaDados.values(), false);
    }

    public AssistenteLevantamentoBemImovel getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteLevantamentoBemImovel assistente) {
        this.assistente = assistente;
    }

    public List<LevantamentoColetaDadosItemVo> getLevantamentos() {
        return levantamentos;
    }

    public void setLevantamentos(List<LevantamentoColetaDadosItemVo> levantamentos) {
        this.levantamentos = levantamentos;
    }

    public FiltroLevantamentoColetaDados getFiltroColetaDados() {
        return filtroColetaDados;
    }

    public void setFiltroColetaDados(FiltroLevantamentoColetaDados filtroColetaDados) {
        this.filtroColetaDados = filtroColetaDados;
    }
}
