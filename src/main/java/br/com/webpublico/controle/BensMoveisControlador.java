/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BensMoveisFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bens-moveis", pattern = "/contabil/bens-moveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/bensmoveis/edita.xhtml"),
    @URLMapping(id = "editar-bens-moveis", pattern = "/contabil/bens-moveis/editar/#{bensMoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensmoveis/edita.xhtml"),
    @URLMapping(id = "ver-bens-moveis", pattern = "/contabil/bens-moveis/ver/#{bensMoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensmoveis/visualizar.xhtml"),
    @URLMapping(id = "listar-bens-moveis", pattern = "/contabil/bens-moveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/bensmoveis/lista.xhtml")
})
public class BensMoveisControlador extends PrettyControlador<BensMoveis> implements Serializable, CRUD {

    @EJB
    private BensMoveisFacade facade;
    private List<BensMoveis> objetos;

    public BensMoveisControlador() {
        super(BensMoveis.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setDataBensMoveis(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    @URLAction(mappingId = "editar-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            salvarSemRedirecionar();
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public Boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    private void salvarSemRedirecionar() {
        Util.validarCampos(selecionado);
        validarRegrasEspecifica();
        if (isOperacaoNovo()) {
            facade.salvarNovo(selecionado);
        } else {
            facade.salvar(selecionado);
        }
    }

    public void definirEventoAndTipo() {
        definirEvento();
        atribuirNullParaTipoBaixaAndTipoIngresso();
    }

    private void atribuirNullParaTipoBaixaAndTipoIngresso() {
        selecionado.setTipoBaixaBens(null);
        selecionado.setTipoIngresso(null);
    }

    public boolean isTipoOperacaoParaBaixa() {
        return TipoOperacaoBensMoveis.DESINCORPORACAO_BENS_MOVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    public boolean isTipoOperacaoParaIngresso() {
        return TipoOperacaoBensMoveis.INCORPORACAO_BENS_MOVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    public boolean renderizarTipoIngresso() {
        return selecionado.getTipoOperacaoBemEstoque() != null
            && isTipoOperacaoParaIngresso();
    }

    public boolean renderizarTipoBaixa() {
        return selecionado.getTipoOperacaoBemEstoque() != null
            && isTipoOperacaoParaBaixa();
    }

    private void validarRegrasEspecifica() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor deve ser maior que zero (0).");
        }
        if (selecionado.getTipoIngresso() == null && isTipoOperacaoParaIngresso()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Ingresso deve ser informado.");
        }
        if (selecionado.getTipoBaixaBens() == null && isTipoOperacaoParaBaixa()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Baixa deve ser informado.");
        }
        ve.lancarException();
    }

    public void definirEvento() {
        try {
            facade.buscarEventoContabil(selecionado);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<TipoIngresso> completarTipoIngresso(String parte) {
        return facade.getTipoIngressoFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<GrupoBem> completarGrupoPatrimonial(String parte) {
        return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
    }

    public List<TipoBaixaBens> completarTipoBaixaBens(String parte) {
        return facade.getTipoBaixaBensFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoLancamento tl : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tl, tl.getDescricao()));
        }
        return toReturn;
    }

    private List<TipoOperacaoBensMoveis> getExcecoesDeItensListadosParaOperacaoBensMoveis() {
        List<TipoOperacaoBensMoveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS);
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaConcedida()) {
            lista.add(tipoOperacaoBensMoveis);
        }
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaRecebida()) {
            lista.add(tipoOperacaoBensMoveis);
        }
        return lista;
    }

    public List<SelectItem> getListaTipoOperacaoBemMoveis() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoBensMoveis tobe : TipoOperacaoBensMoveis.values()) {
            if (!getExcecoesDeItensListadosParaOperacaoBensMoveis().contains(tobe)) {
                toReturn.add(new SelectItem(tobe, tobe.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoGrupo() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo tg : TipoGrupo.values()) {
            if (tg.getClasseDeUtilizacao() == selecionado.getClass()) {
                toReturn.add(new SelectItem(tg, tg.getDescricao()));
            }
        }
        return toReturn;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaAdministrativaDaOrcamentaria(selecionado.getUnidadeOrganizacional(), selecionado.getDataBensMoveis());
            try {
                return hierarquiaOrganizacional;
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Hierarquia Administrativa não encontrada para a unidade: " + selecionado.getUnidadeOrganizacional());
            }
        }
        return null;
    }

    public List<BensMoveis> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<BensMoveis> objetos) {
        this.objetos = objetos;
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {

            objetos = Lists.newArrayList();

            UploadedFile file = event.getFile();
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputstream());
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int colCounts = row.getLastCellNum();
                    String tipoLancamento = "";
                    String unidadeAdministrativa = "";
                    String unidadeOrcamentaria = "";
                    String eventoContabil = "";
                    String operacao = "";
                    String tipoDeGrupo = "";
                    String tipoDeIngresso = "";
                    String tipoDeBaixa = "";
                    String grupoPatrimonio = "";
                    String historico = "";
                    BigDecimal valor = BigDecimal.ZERO;

                    for (int j = 1; j < colCounts; j++) {
                        Cell cell = row.getCell(j);
                        String valorCell = ExcelUtil.getValorCell(cell);

                        switch (j) {
                            case 2:
                                tipoLancamento = valorCell;
                                break;
                            case 3:
                                unidadeAdministrativa = valorCell;
                                break;
                            case 4:
                                unidadeOrcamentaria = valorCell;
                                break;
                            case 5:
                                eventoContabil = valorCell;
                                break;
                            case 6:
                                operacao = valorCell;
                                break;
                            case 7:
                                tipoDeGrupo = valorCell;
                                break;
                            case 8:
                                tipoDeIngresso = valorCell;
                                break;
                            case 9:
                                tipoDeBaixa = valorCell;
                                break;
                            case 10:
                                grupoPatrimonio = valorCell;
                                break;
                            case 11:
                                historico = valorCell;
                                break;
                            case 12:
                                try {
                                    valor = new BigDecimal(valorCell);
                                } catch (NumberFormatException nfe) {
                                    valor = BigDecimal.ZERO;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    atribuirObjeto(tipoLancamento, unidadeAdministrativa, unidadeOrcamentaria, eventoContabil, operacao, tipoDeGrupo, tipoDeIngresso, tipoDeBaixa, grupoPatrimonio, historico, valor);
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao importar arquivo. " + e.getMessage());
        }
    }

    private void atribuirObjeto(String tipoLancamento, String unidadeAdministrativa, String unidadeOrcamentaria, String eventoContabil, String operacao, String tipoDeGrupo, String tipoDeIngresso, String tipoDeBaixa, String grupoPatrimonio, String historico, BigDecimal valor) {
        if (!tipoLancamento.isEmpty() && valor.compareTo(BigDecimal.ZERO) > 0) {

            BensMoveis bensMoveis = new BensMoveis();
            bensMoveis.setDataBensMoveis(facade.getSistemaFacade().getDataOperacao());
            bensMoveis.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            atribuirTipoLancamento(tipoLancamento, bensMoveis);
            atribuirOperacao(operacao, bensMoveis);
            atribuirTipoDeGrupo(tipoDeGrupo, bensMoveis);
            bensMoveis.setTipoBaixaBens(recuperarTipoBaixa(tipoDeBaixa));
            bensMoveis.setTipoIngresso(recuperarTipoIngresso(tipoDeIngresso));
            bensMoveis.setGrupoBem(recuperarGrupoBem(grupoPatrimonio));
            bensMoveis.setUnidadeOrganizacional(recuperarUnidade(unidadeOrcamentaria));
            if (!bensMoveis.getUnidadeOrganizacional().equals(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente())) {
                throw new ExcecaoNegocioGenerica("A unidade " + bensMoveis.getUnidadeOrganizacional() + " é diferente da unidade logada " + facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente() + ".");
            }
            bensMoveis.setEventoContabil(recuperarEventoContabil(eventoContabil, bensMoveis.getTipoLancamento()));
            bensMoveis.setHistorico(historico);
            bensMoveis.setValor(valor);
            objetos.add(bensMoveis);
        }
    }

    private TipoBaixaBens recuperarTipoBaixa(String tipoDeBaixa) {
        if (tipoDeBaixa.trim().isEmpty()) {
            return null;
        }
        try {
            List<TipoBaixaBens> tipos = facade.getTipoBaixaBensFacade().listaFiltrandoPorTipoBem(tipoDeBaixa.trim(), TipoBem.MOVEIS);
            if (tipos.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar um Tipo de Baixa para a descrição " + tipoDeBaixa + ".");
            }
            if (tipos.size() > 1) {
                throw new ExcecaoNegocioGenerica("Foi encontrado mais que um Tipo de Baixa para a descrição " + tipoDeBaixa + ".");
            }
            return tipos.get(0);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o Tipo de Baixa para a descrição " + tipoDeBaixa + ".");
        }
    }

    private TipoIngresso recuperarTipoIngresso(String tipoDeIngresso) {
        if (tipoDeIngresso.trim().isEmpty()) {
            return null;
        }
        try {
            List<TipoIngresso> tipos = facade.getTipoIngressoFacade().listaFiltrandoPorTipoBem(tipoDeIngresso.trim(), TipoBem.MOVEIS);
            if (tipos.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar um Tipo de Ingresso para a descrição " + tipoDeIngresso + ".");
            }
            if (tipos.size() > 1) {
                throw new ExcecaoNegocioGenerica("Foi encontrado mais que um Tipo de Ingresso para a descrição " + tipoDeIngresso + ".");
            }
            return tipos.get(0);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o Tipo de Ingresso para a descrição " + tipoDeIngresso + ".");
        }
    }

    private void atribuirTipoDeGrupo(String tipoDeGrupo, BensMoveis bensMoveis) {
        try {
            bensMoveis.setTipoGrupo(TipoGrupo.valueOf(ExcelUtil.getEnum(tipoDeGrupo, TipoGrupo.values()).name()));
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o Tipo de Grupo " + tipoDeGrupo.toUpperCase());
        }
    }

    private void atribuirOperacao(String operacao, BensMoveis bensMoveis) {
        try {
            bensMoveis.setTipoOperacaoBemEstoque(TipoOperacaoBensMoveis.valueOf(ExcelUtil.getEnum(operacao, TipoOperacaoBensMoveis.values()).name()));
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Operação " + operacao.toUpperCase());
        }
    }

    private void atribuirTipoLancamento(String tipoLancamento, BensMoveis bensMoveis) {
        try {
            bensMoveis.setTipoLancamento(TipoLancamento.valueOf(ExcelUtil.getEnum(tipoLancamento, TipoLancamento.values()).name()));
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar o tipo de lançamento " + tipoLancamento.toUpperCase());
        }
    }

    private EventoContabil recuperarEventoContabil(String eventoContabil, TipoLancamento tipo) {
        eventoContabil = Double.valueOf(eventoContabil).intValue() + "";
        List<EventoContabil> eventoContabils = facade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(eventoContabil.trim(), TipoEventoContabil.BENS_MOVEIS, tipo);
        if (eventoContabils.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Evento Contábil com o codigo " + eventoContabil + " e tipo de lançamento " + tipo.getDescricao() + ".");
        }
        if (eventoContabils.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que evento contábil para o código " + eventoContabil + " e tipo de lançamento " + tipo.getDescricao() + ".");
        }
        return eventoContabils.get(0);
    }

    private UnidadeOrganizacional recuperarUnidade(String codigoUnidadeOrcamentaria) {
        HierarquiaOrganizacional orc = null;
        try {
            orc = facade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(codigoUnidadeOrcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA, facade.getSistemaFacade().getDataOperacao());
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade Orçamentária para o código " + codigoUnidadeOrcamentaria + ".");
        }
        return orc.getSubordinada();

    }

    private GrupoBem recuperarGrupoBem(String codigoGrupoBem) {
        try {
            List<GrupoBem> grupos = facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(codigoGrupoBem, TipoBem.MOVEIS);
            if (grupos.isEmpty()) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar um Grupo para o código " + codigoGrupoBem + ".");
            }
            if (grupos.size() > 1) {
                throw new ExcecaoNegocioGenerica("Foi encontrado mais que um Grupo para o código " + codigoGrupoBem + ".");
            }
            return grupos.get(0);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade Orçamentária para o código " + codigoGrupoBem + ".");
        }
    }

    public void salvarImportacao() {
        try {
            for (BensMoveis objeto : objetos) {
                selecionado = objeto;
                salvarSemRedirecionar();
            }
            FacesUtil.addOperacaoRealizada("Os " + objetos.size() + " registros de Bens Móveis foram salvos com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }
}
