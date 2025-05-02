package br.com.webpublico.controle.contabil;


import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.entidades.TipoConta;
import br.com.webpublico.entidadesauxiliares.AssistenteImportacaoPlanoDeContas;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.ContaVO;
import br.com.webpublico.entidadesauxiliares.DeparaContaVO;
import br.com.webpublico.enums.SituacaoImportarPlanoDeContas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ImportarPlanoDeContasFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.itextpdf.io.IOException;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.swing.text.MaskFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-importar-plano-contas", pattern = "/importar-plano-contas/", viewId = "/faces/financeiro/importar-plano-contas/edita.xhtml")
})
public class ImportarPlanoDeContasControlador {

    private static final Logger logger = LoggerFactory.getLogger(ImportarPlanoDeContasControlador.class);
    @EJB
    private ImportarPlanoDeContasFacade facade;
    private AssistenteImportacaoPlanoDeContas selecionado;
    private CompletableFuture<AssistenteImportacaoPlanoDeContas> futureImportacaoPlanoContas;


    @URLAction(mappingId = "novo-importar-plano-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new AssistenteImportacaoPlanoDeContas();
        selecionado.setDataOperacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setExercicioAnterior(facade.getExercicioFacade().getExercicioPorAno(selecionado.getExercicio().getAno() - 1));
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<SelectItem> getTiposConta() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (selecionado != null) {
            for (TipoConta tipoConta : facade.getTipoContaFacade().buscarPorExercicio(selecionado.getExercicio())) {
                retorno.add(new SelectItem(tipoConta, tipoConta.toString()));
            }
        }
        return retorno;
    }

    public void importar(FileUploadEvent event) {
        try {
            if (selecionado != null) {
                validarCampos();
                selecionado.setTodasContas(Lists.<ContaVO>newArrayList());
                selecionado.setContasImportadas(Lists.<ContaVO>newArrayList());
                selecionado.setContasAlteradas(Lists.<ContaVO>newArrayList());
                selecionado.setContasExcluidas(Lists.<ContaVO>newArrayList());
                selecionado.setDeparaContas(Lists.<DeparaContaVO>newArrayList());
                selecionado.setMensagens(Lists.<String>newArrayList());
                UploadedFile file = event.getFile();
                Workbook workbook = WorkbookFactory.create(file.getInputstream());
                importarPlanilhaExcelTCE(workbook);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo da Conta deve ser informado.");
        }
        if (selecionado.getLinhaPrimeiroRegistro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número da linha do primeiro registro deve ser informado.");
        }
        if (selecionado.getPosicaoColunaCodigoConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna código da conta deve ser informado.");
        }
        if (selecionado.getPosicaoColunaDescricaoConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna descrição da conta deve ser informado.");
        }
        if (selecionado.getPosicaoColunaOperacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Operação deve ser informado.");
        }
        if (selecionado.getTipoConta() != null && selecionado.getTipoConta().getClasseDaConta().isClasseContabil()) {
            if (selecionado.getPosicaoColunaSubsistema() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Subsistema deve ser informado.");
            }
            if (selecionado.getPosicaoColunaNaturezaSaldo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Natureza do Saldo deve ser informado.");
            }
            if (selecionado.getPosicaoColunaNaturezaDaConta() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Natureza da Conta deve ser informado.");
            }
        }
        if (selecionado.getPosicaoColunaCodigoContaDepara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna código da conta (De/Para) origem deve ser informado.");
        }
        if (selecionado.getPosicaoColunaDescricaoContaDepara() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna descrição da conta (De/Para) origem deve ser informado.");
        }
        if (selecionado.getPosicaoColunaCodigoSiconfi() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Código Siconfi da conta deve ser informado.");
        }
        if (selecionado.getPosicaoColunaCodigoTCE() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Posição da coluna Código TCE da conta deve ser informado.");
        }
        ve.lancarException();
    }

    public void finalizarImportacao() {
        if (futureImportacaoPlanoContas != null && futureImportacaoPlanoContas.isDone()) {
            try {
                if (selecionado.isSituacao(SituacaoImportarPlanoDeContas.EXCLUIDO)) {
                    iniciarBarraProgresso();
                    selecionado.setDescricaoProcesso("Processando inclusão/alteração");
                    futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                        try{
                            return facade.processarIncluirAlterarContas(selecionado);
                        } catch (IOException ex){
                            throw new RuntimeException(ex);
                        }
                    });
                }

                if (selecionado.isSituacao(SituacaoImportarPlanoDeContas.NOVAS_CONTAS)) {
                    iniciarBarraProgresso();
                    selecionado.setDescricaoProcesso("Iniciando importação");
                    futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                        try{
                            return facade.iniciarImportarDepara(selecionado);
                        } catch (IOException ex){
                            throw new RuntimeException(ex);
                        }
                    });
                }

                if (selecionado.isSituacao(SituacaoImportarPlanoDeContas.VALIDADO) && selecionado.getMostrarContasNaoEncontrada()) {
                    FacesUtil.executaJavaScript("terminarImportacao()");
                    FacesUtil.atualizarComponente("Formulario");
                    FacesUtil.addOperacaoRealizada("Validação realizada com sucesso");
                }
                if (selecionado.isSituacao(SituacaoImportarPlanoDeContas.FINALIZADO)
                    || selecionado.isSituacao(SituacaoImportarPlanoDeContas.IMPORTADO)) {
                    FacesUtil.executaJavaScript("terminarImportacao()");
                    FacesUtil.atualizarComponente("Formulario");

                    if (selecionado.getBarraProgressoItens() != null
                        && !selecionado.getBarraProgressoItens().getCalculando()
                        && !Strings.isNullOrEmpty(selecionado.getMensagemErro())) {
                        FacesUtil.addOperacaoNaoRealizada(selecionado.getMensagemErro());
                    } else {
                        FacesUtil.addOperacaoRealizada("Processo finalizado com sucesso.");
                    }
                }
            } catch (Exception e) {
                logger.error("Error no acompanhamento da importação de plano de contas ", e);
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }

        }
    }

    public void abortar() {
        if (futureImportacaoPlanoContas != null) {
            futureImportacaoPlanoContas.cancel(true);
            selecionado.getBarraProgressoItens().finaliza();
        }
    }

    public List<SelectItem> getPlanosExercicioAnterior() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getTipoConta() != null) {
            try {
                for (PlanoDeContas planoDeContas : facade.getPlanoDeContasFacade().buscarPlanosPorClasseDaContaEExercicio(selecionado.getTipoConta().getClasseDaConta(), selecionado.getExercicioAnterior())) {
                    toReturn.add(new SelectItem(planoDeContas, planoDeContas.toString()));
                }
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            }
        }
        return toReturn;
    }

    public void definirPosicoes() {
        selecionado.definirPosicaoExcelTCE();
    }

    private void importarPlanilhaExcelTCE(Workbook workbook) {
        importarContasDeparas(workbook);
    }

    public void validarContas() {
        try {
            iniciarBarraProgresso();
            selecionado.setDescricaoProcesso("Validando contas");
            futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                try{
                    return facade.validarContas(selecionado);
                } catch (IOException ex){
                    throw new RuntimeException(ex);
                }
            });
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void aplicarAtributosConta() {
        try {
            iniciarBarraProgresso();
            selecionado.setDescricaoProcesso("Aplicando atributos");
            futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                try{
                    return facade.aplicarAtributosConta(selecionado);
                } catch (IOException ex){
                    throw new RuntimeException(ex);
                }
            });
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void iniciarBarraProgresso() {
        selecionado.setBarraProgressoItens(new BarraProgressoItens());
    }

    private void importarContasDeparas(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        int rowsCount = sheet.getLastRowNum();
        for (int i = selecionado.getLinhaPrimeiroRegistro(); i <= rowsCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            ContaVO contaVO = retornarConta(row);
            if (contaVO != null) {
                String operacao = getValorCelula(row, selecionado.getPosicaoColunaOperacao());
                ContaVO contaDestino;
                if (Strings.isNullOrEmpty(operacao) || "ORIGINAL".equalsIgnoreCase(operacao)) {
                    DeparaContaVO depara = new DeparaContaVO();
                    depara.setContaOrigem(contaVO);
                    contaDestino = new ContaVO();

                    contaDestino.setCodigo(getValorCelula(row, selecionado.getPosicaoColunaCodigoContaDepara()));
                    contaDestino.setDescricao(getValorCelula(row, selecionado.getPosicaoColunaDescricaoContaDepara()));
                    contaDestino.setSubSistema(getValorCelula(row, selecionado.getPosicaoColunaSubsistema()));
                    contaDestino.setNaturezaConta(getValorCelula(row, selecionado.getPosicaoColunaNaturezaDaConta()));
                    contaDestino.setNaturezaSaldo(getValorCelula(row, selecionado.getPosicaoColunaNaturezaSaldo()));
                    contaDestino.setNaturezaInformacao(getValorCelula(row, selecionado.getPosicaoColunaNaturezaInformacao()));
                    contaDestino.setContaContabil(selecionado.getTipoConta().getClasseDaConta().isClasseContabil());
                    String codigoTCE = getValorCelula(row, selecionado.getPosicaoColunaCodigoTCE());
                    contaDestino.setCodigoTCE(codigoTCE == null ? contaVO.getCodigoTCE() : codigoTCE);
                    String codigoSiconfi = getValorCelula(row, selecionado.getPosicaoColunaCodigoSiconfi());
                    contaDestino.setCodigoSiconfi(codigoSiconfi == null ? contaVO.getCodigoSiconfi() : codigoSiconfi);

                    if (!Strings.isNullOrEmpty(contaDestino.getCodigo()) && !Strings.isNullOrEmpty(contaDestino.getDescricao())) {
                        depara.setContaDestino(contaDestino);
                        selecionado.getDeparaContas().add(depara);
                    } else {
                        selecionado.getContasImportadas().add(contaVO);
                    }
                } else {
                    switch (operacao.toUpperCase()) {
                        case "EXCLUÍDA":
                            selecionado.getContasExcluidas().add(contaVO);
                            break;
                        case "ALTERADA":
                            facade.recuperarConta(selecionado, selecionado.getExercicioAnterior(), contaVO);
                            selecionado.getContasAlteradas().add(contaVO);
                            break;
                        case "INCLUÍDA":
                            selecionado.getContasImportadas().add(contaVO);
                            break;
                    }

                }
            }
        }
    }

    private ContaVO retornarConta(Row row) {
        ContaVO conta = new ContaVO();
        try {
            String mascara = selecionado.getTipoConta().getMascara().replace("9", "#");
            String codigoSoNumero = getValorCelula(row, selecionado.getPosicaoColunaCodigoConta()).replaceAll("[^0-9]", "");
            MaskFormatter formatter = new MaskFormatter(mascara);
            formatter.setPlaceholderCharacter('_');
            formatter.setValueContainsLiteralCharacters(false);
            conta.setCodigo(formatter.valueToString(codigoSoNumero));
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Ocorreu um erro ao formatar o código com a máscara " + selecionado.getTipoConta().getMascara() + ". Detalhe do erro: " + ex.getMessage());
        }
        conta.setDescricao(getValorCelula(row, selecionado.getPosicaoColunaDescricaoConta()));

        if (selecionado.getTipoConta().getClasseDaConta().isClasseContabil()) {
            conta.setContaContabil(Boolean.TRUE);
            conta.setSubSistema(getValorCelula(row, selecionado.getPosicaoColunaSubsistema()));
            conta.setNaturezaConta(getValorCelula(row, selecionado.getPosicaoColunaNaturezaDaConta()));
            conta.setNaturezaSaldo(getValorCelula(row, selecionado.getPosicaoColunaNaturezaSaldo()));
            conta.setNaturezaInformacao(getValorCelula(row, selecionado.getPosicaoColunaNaturezaInformacao()));
        } else {
            conta.setContaContabil(Boolean.FALSE);
        }
        if (getValorCelula(row, selecionado.getPosicaoColunaCodigoContaDepara()) != null) {
            ContaVO contaVO = new ContaVO();
            contaVO.setCodigo(getValorCelula(row, selecionado.getPosicaoColunaCodigoContaDepara()));
            if (!Strings.isNullOrEmpty(contaVO.getCodigo())) {
                facade.recuperarConta(selecionado, selecionado.getExercicioAnterior(), contaVO);
                conta.setContaEquivalente(contaVO.getContaRecuperada());
            }
        }
        String codigoTCE = getValorCelula(row, selecionado.getPosicaoColunaCodigoTCE());
        conta.setCodigoTCE(codigoTCE == null ? conta.getContaRecuperada() != null ? conta.getContaRecuperada().getCodigoTCE() : "" : codigoTCE);
        String codigoSiconfi = getValorCelula(row, selecionado.getPosicaoColunaCodigoSiconfi());
        conta.setCodigoSiconfi(codigoSiconfi == null ? conta.getContaRecuperada() != null ? conta.getContaRecuperada().getCodigoSICONFI() : "" : codigoSiconfi);
        if (!Strings.isNullOrEmpty(conta.getCodigo())
            && !Strings.isNullOrEmpty(conta.getDescricao())) {
            return conta;
        }
        return null;
    }

    public void inicializarImportacaoDoPlanoDeContas() {
        try {
            validarImportacao();
            iniciarBarraProgresso();
            selecionado.setDescricaoProcesso("Inicializando importação");
            futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                try{
                    return facade.inicializarImportacaoDoPlanoDeContas(selecionado);
                } catch (IOException ex){
                    throw new RuntimeException(ex);
                }
            });
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarImportacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContasExcluidas().isEmpty() && selecionado.getContasImportadas().isEmpty() && selecionado.getContasAlteradas().isEmpty() && selecionado.getDeparaContas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há contas para processar.");
        }
        ve.lancarException();
    }

    public void salvarContas() {
        try {
            iniciarBarraProgresso();
            selecionado.setDescricaoProcesso("Processando exclusão");
            futureImportacaoPlanoContas = AsyncExecutor.getInstance().execute(selecionado, ()-> {
                try{
                    return facade.processarExcluirContas(selecionado);
                } catch (IOException ex){
                    throw new RuntimeException(ex);
                }
            });
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<Conta> completarContasEquivalentes(String filtros) {
        return facade.getPlanoDeContasFacade().getContaFacade().buscarPorPlanoDeContas(selecionado.getPlanoDeContasExercicioAnterior(), filtros);
    }

    public void removerConta(ContaVO conta) {
        selecionado.getTodasContas().remove(conta);
        selecionado.getContasImportadas().remove(conta);
        selecionado.getContasAlteradas().remove(conta);
        selecionado.getContasExcluidas().remove(conta);
    }

    public void removerContaDepara(DeparaContaVO conta) {
        selecionado.getDeparaContas().remove(conta);
    }

    private String getValorCelula(Row row, Integer posicao) {
        if (row == null || posicao == null) {
            return "";
        }
        Cell cell = row.getCell(posicao);
        if (cell == null) {
            return "";
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return ExcelUtil.getValorCell(cell);
    }

    public void gerarPDFLog() {
        String secretaria = facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente().getDescricao();
        String conteudo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<center>"
            + "<table>"
            + "<tr>"
            + "<td> <h2> " + secretaria + " </h2> </td>"
            + "</tr>"
            + "<tr>"
            + "<td colspan=\"2\"> <center> LOG DE ERROS DE IMPORTAÇÃO DE PLANO DE CONTAS </center> </td>"
            + "</tr>"
            + "</table>"
            + "</center>"
            + "<div style=\"border : solid #92B8D3 1px; \"";
        for (String mensagen : selecionado.getMensagens()) {
            conteudo += "<p>";
            conteudo += mensagen;
            conteudo += "</p>";
        }
        conteudo += "</div>"
            + "USUÁRIO RESPONSÁVEL:" + facade.getSistemaFacade().getUsuarioCorrente().toString() + "<br/>"
            + "               DATA:" + DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()) + "<br/>"
            + " </body>"
            + " </html>";

        Util.downloadPDF("Log de erros de importação de plano de contas", conteudo, FacesContext.getCurrentInstance());
    }

    public AssistenteImportacaoPlanoDeContas getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(AssistenteImportacaoPlanoDeContas selecionado) {
        this.selecionado = selecionado;
    }

}
