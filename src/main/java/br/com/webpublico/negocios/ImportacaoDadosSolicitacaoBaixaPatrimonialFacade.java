package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImportacaoDadosSolicitacaoBaixa;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import com.google.common.base.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ImportacaoDadosSolicitacaoBaixaPatrimonialFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoDadosSolicitacaoBaixaPatrimonialFacade.class);
    @EJB
    private SolicitacaoBaixaPatrimonialFacade solicitacaoBaixaPatrimonialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<SolicitacaoBaixaPatrimonial> salvarImportacaoDadosSolicitacao(SolicitacaoBaixaPatrimonial selecionado, List<Bem> bens,
                                                                                AssistenteMovimentacaoBens assistente) {
        try {
            assistente.setDescricaoProcesso("Salvando Importação de Dados de Solicitação de Baixa Patrimonial");
            selecionado = em.merge(selecionado);
            assistente.setTotal(bens.size());
            solicitacaoBaixaPatrimonialFacade.processarBensEmElaboracao(selecionado, bens, assistente);

            bloquearBens(bens, assistente.getConfigMovimentacaoBem());

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar importação de dados da solicitação de baixa de bens móveis {}", ex);
        }
        return new AsyncResult<>(selecionado);
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<ImportacaoDadosSolicitacaoBaixa> processarArquivo(UploadedFile file, AssistenteMovimentacaoBens assistente, ImportacaoDadosSolicitacaoBaixa importacaoDadosSolicitacaoBaixa) {
        try {
            assistente.setDescricaoProcesso("Importando Dados do Arquivo...");
            HierarquiaOrganizacional hierarquiaAdministrativa = null;
            HierarquiaOrganizacional hierarquiaOrcamentaria = null;
            TipoBaixa tipoBaixa = null;
            String motivoBaixa = null;

            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputstream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            assistente.setTotal(rowsCount);

            for (int linha = 1; linha <= rowsCount; linha++) {
                Row row = sheet.getRow(linha);
                if (row != null) {
                    int colCounts = row.getLastCellNum();
                    String tipoBaixaCell = "";
                    String motivoCell = "";
                    String identificacaoCell = "";
                    String codigoUnidadeAdmCell = "";
                    String codigoUnidadeOrcCell = "";

                    for (int coluna = 0; coluna < colCounts; coluna++) {
                        Cell cell = row.getCell(coluna);
                        if (cell == null) {
                            continue;
                        }
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String valorCell = ExcelUtil.getValorCell(cell);

                        if (coluna == 0) {
                            tipoBaixaCell = valorCell;
                        }
                        if (coluna == 1) {
                            motivoCell = valorCell;
                        }
                        if (coluna == 2) {
                            identificacaoCell = valorCell;
                        }
                        if (coluna == 3) {
                            codigoUnidadeAdmCell = valorCell;
                        }
                        if (coluna == 4) {
                            codigoUnidadeOrcCell = valorCell;
                        }
                    }
                    if (Strings.isNullOrEmpty(tipoBaixaCell)
                        && Strings.isNullOrEmpty(motivoCell)
                        && Strings.isNullOrEmpty(identificacaoCell)
                        && Strings.isNullOrEmpty(codigoUnidadeAdmCell)
                        && Strings.isNullOrEmpty(codigoUnidadeOrcCell)) {
                        break;
                    }
                    validarCelulaVazia(linha, tipoBaixaCell, motivoCell, identificacaoCell, codigoUnidadeAdmCell, codigoUnidadeOrcCell, assistente);
                    hierarquiaAdministrativa = recuperarHierarquiaAdministrativa(assistente, hierarquiaAdministrativa, codigoUnidadeAdmCell);
                    hierarquiaOrcamentaria = recuperarHierarquiaOrcamentaria(assistente, codigoUnidadeOrcCell);
                    tipoBaixa = recuperarTipoBaixa(assistente, tipoBaixa, tipoBaixaCell);
                    motivoBaixa = recuperarMotivoBaixa(motivoBaixa, motivoCell);

                    Bem bem = recuperarBem(identificacaoCell, hierarquiaAdministrativa, hierarquiaOrcamentaria, assistente);
                    validarAndAdicionarBem(assistente, importacaoDadosSolicitacaoBaixa, identificacaoCell, bem);
                }
                assistente.conta();
            }
            SolicitacaoBaixaPatrimonial solicitacao = criarSolicitacaoBaixa(tipoBaixa, motivoBaixa, assistente, hierarquiaAdministrativa, hierarquiaOrcamentaria);
            importacaoDadosSolicitacaoBaixa.setSolicitacaoBaixaPatrimonial(solicitacao);
        } catch (Exception ex) {
            logger.error("Erro ao processar arquivo de dados da solicitação de baixa de bens móveis {}", ex);
        }
        return new AsyncResult<>(importacaoDadosSolicitacaoBaixa);
    }

    public void bloquearBens(List<Bem> bensSelecionados, ConfigMovimentacaoBem configMovimentacaoBem) {
        for (Bem bensSelecionado : bensSelecionados) {
            configMovimentacaoBemFacade.bloquearUnicoBem(configMovimentacaoBem, bensSelecionado.getId());
        }
    }

    private void validarAndAdicionarBem(AssistenteMovimentacaoBens assistente, ImportacaoDadosSolicitacaoBaixa importacaoDadosSolicitacaoBaixa, String identificacaoCell, Bem bem) {
        if (bem != null) {
            EventoBem ultimoEvento = bemFacade.recuperarUltimoEventoBem(bem.getId());
            if (!bemFacade.situacoesParaMovimentacoes().contains(ultimoEvento.getSituacaoEventoBem().name())) {
                assistente.getMensagens().add("O bem " + identificacaoCell + " foi movimentado na(o) " + ultimoEvento.getTipoEventoBem().getDescricao() + ".");

            } else if (!bem.validarDataLancamentoPosteriorAProximoMovimentacao(assistente.getDataLancamento(), ultimoEvento)) {
                assistente.getMensagens().add(" O bem " + identificacaoCell + " possui movimento de " + ultimoEvento.getTipoEventoBem() + " em " + DataUtil.getDataFormatada(ultimoEvento.getDataLancamento())
                    + " que é posterior à data de lançamento da solicitação de baixa patrimonial: " + DataUtil.getDataFormatada(assistente.getDataLancamento()) + ".");

            } else {
                importacaoDadosSolicitacaoBaixa.getBens().add(bem);
            }
        }
    }

    private String recuperarMotivoBaixa(String motivoBaixa, String motivo) {
        if (Strings.isNullOrEmpty(motivoBaixa)) {
            motivoBaixa = motivo;
        }
        return motivoBaixa;
    }

    private TipoBaixa recuperarTipoBaixa(AssistenteMovimentacaoBens assistente, TipoBaixa tipoBaixaObjeto, String tipoBaixa) {
        try {
            if (tipoBaixaObjeto == null) {
                tipoBaixaObjeto = TipoBaixa.valueOf(tipoBaixa);
            }
        } catch (IllegalArgumentException e) {
            assistente.getMensagens().add("Valor invalido para o campo tipo baixa. Valor informado: " + tipoBaixa);
        }
        return tipoBaixaObjeto;
    }

    private HierarquiaOrganizacional recuperarHierarquiaOrcamentaria(AssistenteMovimentacaoBens assistente, String codigoUnidadeOrc) {
        HierarquiaOrganizacional hierarquiaOrcamentaria;
        hierarquiaOrcamentaria = hierarquiaOrganizacionalFacade.recuperarHierarquiaPorCodigoTipoData(codigoUnidadeOrc, TipoHierarquiaOrganizacional.ORCAMENTARIA, assistente.getDataLancamento());
        validarCampoNulo(hierarquiaOrcamentaria, "Hierarquia orçamentária não encontrada para o código " + codigoUnidadeOrc + ".", assistente);
        return hierarquiaOrcamentaria;
    }

    private HierarquiaOrganizacional recuperarHierarquiaAdministrativa(AssistenteMovimentacaoBens assistente, HierarquiaOrganizacional hierarquiaAdministrativa, String codigoUnidadeAdm) {
        if (hierarquiaAdministrativa == null) {
            hierarquiaAdministrativa = hierarquiaOrganizacionalFacade.recuperarHierarquiaPorCodigoTipoData(codigoUnidadeAdm, TipoHierarquiaOrganizacional.ADMINISTRATIVA, assistente.getDataLancamento());
        }
        validarCampoNulo(hierarquiaAdministrativa, "Hierarquia administrativa não encontrada para o código " + codigoUnidadeAdm + ".", assistente);
        return hierarquiaAdministrativa;
    }

    private SolicitacaoBaixaPatrimonial criarSolicitacaoBaixa(TipoBaixa tipoBaixa, String motivo, AssistenteMovimentacaoBens assistente,
                                                              HierarquiaOrganizacional hierarquiaAdm, HierarquiaOrganizacional hierarquiaOrc) {
        SolicitacaoBaixaPatrimonial novaSolicitacao = new SolicitacaoBaixaPatrimonial();
        novaSolicitacao.setTipoBem(TipoBem.MOVEIS);
        novaSolicitacao.setUsuarioSistema(assistente.getUsuarioSistema());
        novaSolicitacao.setDataSolicitacao(assistente.getDataLancamento());
        novaSolicitacao.setMotivo(motivo);
        novaSolicitacao.setTipoBaixa(tipoBaixa);
        novaSolicitacao.setUnidadeOrcamentaria(hierarquiaOrc.getSubordinada());
        novaSolicitacao.setUnidadeAdministrativa(hierarquiaAdm.getSubordinada());
        return novaSolicitacao;
    }

    private Bem recuperarBem(String identificacao, HierarquiaOrganizacional hierarquiaAdministrativa,
                             HierarquiaOrganizacional hierarquiaOrcamentaria, AssistenteMovimentacaoBens assistente) {
        Bem bem;
        bem = bemFacade.recuperarBemPorUnidadeAdministrativa(identificacao, hierarquiaAdministrativa.getSubordinada());
        validarCampoNulo(bem, "Bem não encontrato para o registro patrimonial: " + identificacao + " e unidade: " + hierarquiaAdministrativa + ".", assistente);

        if (bem != null) {
            EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(bem);
            validarCampoNulo(ultimoEstadoBem, "Último estado bem não encontrato para o bem: " + bem + ".", assistente);

            bem.setOrcamentaria(hierarquiaOrcamentaria.getCodigo() + " - " + hierarquiaOrcamentaria.getDescricao());
            if (!ultimoEstadoBem.getDetentoraOrcamentaria().equals(hierarquiaOrcamentaria.getSubordinada())) {
                assistente.getMensagens().add("A unidade importada " + hierarquiaOrcamentaria.getCodigo() + " é diferente da recuperada do bem " + bem.getIdentificacao() + ".");
            }
            Bem.preencherDadosTrasientsDoBem(bem, ultimoEstadoBem);
            return bem;
        }
        return null;
    }

    private void validarCelulaVazia(int i, String tipoBaixa, String motivo, String identificacao,
                                    String hierarquiaOrganizacional, String unidadeOrcamentaria, AssistenteMovimentacaoBens assistente) {
        if (Strings.isNullOrEmpty(tipoBaixa)) {
            assistente.getMensagens().add("O campo Tipo de Baixa está vazio na linha " + i + "!");
        }
        if (Strings.isNullOrEmpty(motivo)) {
            assistente.getMensagens().add("O campo Motivo está vazio na linha " + i + "!");
        }
        if (Strings.isNullOrEmpty(identificacao)) {
            assistente.getMensagens().add("O campo Registro Patrimonial está vazio na linha " + i + "!");
        }
        if (Strings.isNullOrEmpty(hierarquiaOrganizacional)) {
            assistente.getMensagens().add("O campo Unidade Administrativa está vazio na linha " + i + "!");
        }
        if (Strings.isNullOrEmpty(unidadeOrcamentaria)) {
            assistente.getMensagens().add("O campo Unidade Orçamentária está vazio na linha " + i + "!");
        }
    }

    private void validarCampoNulo(Object campo, String mensagem, AssistenteMovimentacaoBens assistente) {
        if (campo == null) {
            assistente.getMensagens().add(mensagem);
        }
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SolicitacaoBaixaPatrimonialFacade getSolicitacaoBaixaPatrimonialFacade() {
        return solicitacaoBaixaPatrimonialFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }
}
