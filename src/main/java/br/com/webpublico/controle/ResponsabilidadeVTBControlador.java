/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ResponsabilidadeVTBFacade;
import br.com.webpublico.util.*;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-responsabilidade-vtb", pattern = "/responsabilidade-vtb/novo/", viewId = "/faces/financeiro/responsabilidade-vtb/edita.xhtml"),
    @URLMapping(id = "edita-responsabilidade-vtb", pattern = "/responsabilidade-vtb/editar/#{responsabilidadeVTBControlador.id}/", viewId = "/faces/financeiro/responsabilidade-vtb/edita.xhtml"),
    @URLMapping(id = "ver-responsabilidade-vtb", pattern = "/responsabilidade-vtb/ver/#{responsabilidadeVTBControlador.id}/", viewId = "/faces/financeiro/responsabilidade-vtb/visualizar.xhtml"),
    @URLMapping(id = "listar-responsabilidade-vtb", pattern = "/responsabilidade-vtb/listar/", viewId = "/faces/financeiro/responsabilidade-vtb/lista.xhtml")
})
public class ResponsabilidadeVTBControlador extends PrettyControlador<ResponsabilidadeVTB> implements Serializable, CRUD {

    @EJB
    private ResponsabilidadeVTBFacade responsabilidadeVTBFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterEventoContabil;

    //Importação
    private List<ResponsabilidadeVTB> objetos;

    public ResponsabilidadeVTBControlador() {
        super(ResponsabilidadeVTB.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return responsabilidadeVTBFacade;
    }

    @URLAction(mappingId = "nova-responsabilidade-vtb", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataRegistro(UtilRH.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        if (responsabilidadeVTBFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", responsabilidadeVTBFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-responsabilidade-vtb", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "edita-responsabilidade-vtb", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/responsabilidade-vtb/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void salvar() {
        try {
            salvarSemRedirecionar();
            lancarMensagemAoSalvar();
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void lancarMensagemAoSalvar() {
        if (isOperacaoNovo()) {
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso!");
        } else {
            FacesUtil.addOperacaoRealizada("Registro alterado com sucesso!");
        }
    }

    private void salvarSemRedirecionar() {
        validarCampos();
        if (isOperacaoNovo()) {
            responsabilidadeVTBFacade.salvarNovo(selecionado);
        } else {
            responsabilidadeVTBFacade.salvar(selecionado);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero(0)!");
        }
        ve.lancarException();
    }

    public void removerEvento() {
        selecionado.setEventoContabil(null);
    }

    public List<TipoLancamento> getTiposDeLancamento() {
        return Arrays.asList(TipoLancamento.values());
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return responsabilidadeVTBFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(parte.trim(), TipoEventoContabil.RESPONSABILIDADE_VTB, selecionado.getTipoLancamento());
    }

    public List<ClasseCredor> completarClassesCredores(String parte) {
        return responsabilidadeVTBFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), selecionado.getPessoa());
    }

    public List<Pessoa> completarPessoas(String parte) {
        return responsabilidadeVTBFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public ConverterAutoComplete getConverterEventoContabil() {
        if (converterEventoContabil == null) {
            converterEventoContabil = new ConverterAutoComplete(EventoContabil.class, responsabilidadeVTBFacade.getEventoContabilFacade());
        }
        return converterEventoContabil;
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, responsabilidadeVTBFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, responsabilidadeVTBFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<ResponsabilidadeVTB> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<ResponsabilidadeVTB> objetos) {
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
                    String unidade = "";
                    String eventoContabil = "";
                    String pessoa = "";
                    String classe = "";
                    String historico = "";
                    BigDecimal valor = BigDecimal.ZERO;

                    for (int j = 1; j < colCounts; j++) {
                        Cell cell = row.getCell(j);
                        String valorCell = ExcelUtil.getValorCell(cell);


                        if (j == 2) {
                            tipoLancamento = valorCell;
                        }
                        if (j == 3) {
                            unidade = valorCell;
                        }
                        if (j == 4) {
                            eventoContabil = valorCell;
                        }
                        if (j == 5) {
                            pessoa = valorCell;
                        }
                        if (j == 6) {
                            classe = valorCell;
                        }
                        if (j == 7) {
                            historico = valorCell;
                        }
                        if (j == 8) {
                            try {
                                valor = new BigDecimal(valorCell);
                            } catch (NumberFormatException nfe) {
                                valor = BigDecimal.ZERO;
                            }
                        }
                    }
                    atribuirObjeto(tipoLancamento, unidade, eventoContabil, pessoa, classe, historico, valor);
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao importar arquivo. " + e.getMessage());
        }
    }

    private void atribuirObjeto(String tipoLancamento, String unidade, String eventoContabil, String pessoa, String classe, String historico, BigDecimal valor) {
        if (!tipoLancamento.isEmpty() && valor.compareTo(BigDecimal.ZERO) > 0) {
            ResponsabilidadeVTB responsabilidadeVTB = new ResponsabilidadeVTB();
            try {
                responsabilidadeVTB.setTipoLancamento(TipoLancamento.valueOf(tipoLancamento.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ExcecaoNegocioGenerica("Não foi possível encontrar o tipo de lançamento " + tipoLancamento.toUpperCase());
            }

            responsabilidadeVTB.setUnidadeOrganizacional(getUnidade(unidade));
            if (!responsabilidadeVTB.getUnidadeOrganizacional().equals(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente())) {
                throw new ExcecaoNegocioGenerica("A unidade " + responsabilidadeVTB.getUnidadeOrganizacional() + " é diferente da unidade logada " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente() + ".");
            }
            responsabilidadeVTB.setEventoContabil(getEventoContabil(eventoContabil, responsabilidadeVTB.getTipoLancamento()));
            responsabilidadeVTB.setPessoa(getPessoa(pessoa));
            responsabilidadeVTB.setClasseCredor(getClasseCredor(classe, responsabilidadeVTB.getPessoa()));
            responsabilidadeVTB.setHistorico(historico);
            responsabilidadeVTB.setValor(valor);
            responsabilidadeVTB.setDataRegistro(sistemaControlador.getDataOperacao());
            responsabilidadeVTB.setExercicio(sistemaControlador.getExercicioCorrente());
            Util.adicionarObjetoEmLista(objetos, responsabilidadeVTB);
        }
    }

    public void salvarImportacao() {
        try {
            for (ResponsabilidadeVTB objeto : objetos) {
                selecionado = objeto;
                salvarSemRedirecionar();
            }
            FacesUtil.addOperacaoRealizada("As " + objetos.size() + " Responsabilidades por Valores, Títulos e Bens foram salvas com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private ClasseCredor getClasseCredor(String classe, Pessoa pessoa) {
        List<ClasseCredor> classes = responsabilidadeVTBFacade.getClasseCredorFacade().buscarClassesPorPessoa(classe.trim(), pessoa);
        if (classes.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar uma Classe com o código " + classe + " para a Pessoa " + pessoa + ".");
        }
        if (classes.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que uma Classe com o código " + classe + " para a Pessoa " + pessoa + ".");
        }
        return classes.get(0);
    }

    private Pessoa getPessoa(String pessoa) {
        List<Pessoa> pessoas = responsabilidadeVTBFacade.getPessoaFacade().listaTodasPessoasAtivas(pessoa);
        if (pessoas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar uma Pessoa com o CPF/CNPJ " + pessoa + ".");
        }
        if (pessoas.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que uma Pessoa com o CPF/CNPJ " + pessoa + ".");
        }
        return pessoas.get(0);
    }

    private EventoContabil getEventoContabil(String eventoContabil, TipoLancamento tipo) {
        eventoContabil = Double.valueOf(eventoContabil).intValue() + "";
        List<EventoContabil> eventoContabils = responsabilidadeVTBFacade.getEventoContabilFacade().listaFiltrandoPorTipoEventoTipoLancamento(eventoContabil.trim(), TipoEventoContabil.RESPONSABILIDADE_VTB, tipo);
        if (eventoContabils.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Evento Contábil com o codigo " + eventoContabil + " e tipo de lançamento " + tipo.getDescricao() + ".");
        }
        if (eventoContabils.size() > 1) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais que evento contábil para o código " + eventoContabil + " e tipo de lançamento " + tipo.getDescricao() + ".");
        }
        return eventoContabils.get(0);
    }

    private UnidadeOrganizacional getUnidade(String codigoUnidadeOrcamentaria) {
        HierarquiaOrganizacional orc = null;
        try {
            orc = responsabilidadeVTBFacade.getHierarquiaOrganizacionalFacade().recuperarHierarquiaPorCodigoTipoData(codigoUnidadeOrcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA, sistemaControlador.getDataOperacao());
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar a Unidade Orçamentária para o código " + codigoUnidadeOrcamentaria + ".");
        }
        return orc.getSubordinada();

    }
}
