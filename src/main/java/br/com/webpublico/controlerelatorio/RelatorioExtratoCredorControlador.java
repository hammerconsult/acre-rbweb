package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 11/01/14
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-extrato-credor", pattern = "/relatorio/extrato-credor/", viewId = "/faces/financeiro/relatorio/relatorioextratocredor.xhtml")
})
@ManagedBean
public class RelatorioExtratoCredorControlador extends RelatorioContabilSuperControlador implements Serializable {

    private ContaBancariaEntidade contaBancariaEntidade;
    private FonteDeRecursos fonteDeRecursos;
    private SubConta subConta;
    private DividaPublica dividaPublica;
    private Pessoa pessoa;
    private ContaDespesa contaDespesa;
    private Boolean quebraPorEmpenho;
    private Empenho empenho;

    public RelatorioExtratoCredorControlador() {
    }

    public List<Empenho> buscarEmpenho(String parte) {
        Exercicio ex = getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataInicial));
        if (pessoa != null) {
            if (!listaUnidades.isEmpty()) {
                return relatorioContabilSuperFacade.getEmpenhoFacade().buscarEmpenhoPorUnidadesAndPessoaAndDataInicialEFinal(parte, listaUnidades, pessoa, dataInicial, dataFinal);
            } else if (unidadeGestora != null) {
                return relatorioContabilSuperFacade.getEmpenhoFacade().buscarEmpenhoPorUnidadeGestoraEPessoaEExercicioEDataInicialEFinal(parte, unidadeGestora, ex, pessoa, dataInicial, dataFinal);
            }
        }
        return new ArrayList<>();
    }

    @URLAction(mappingId = "relatorio-extrato-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        limparCamposGeral();
        pessoa = null;
        quebraPorEmpenho = false;
        contaBancariaEntidade = null;
        subConta = null;
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Referência deve ser informado.");
        }
        if (pessoa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final");
        }
        if (dataFinal.after(dataReferencia)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Final não pode ser maior que a Data de Referência");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            atualizarExercicio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("QUEBRA_EMPENHO", quebraPorEmpenho);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/extrato-credor/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            filtros = "";
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void atualizarExercicio() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        exercicio = getExercicioFacade().listaFiltrandoEspecial(formato.format(dataInicial)).get(0);
    }

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
        return filtros;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        filtros = " Credor: " + pessoa.getCpf_Cnpj() + " - " + pessoa.getNome() + " -";
        parametros.add(new ParametrosRelatorios(" p.id ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, this.pessoa.getId(), null, 1, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, formato.format(dataInicial), formato.format(dataFinal), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAREFERENCIA", OperacaoRelatorio.BETWEEN, formato.format(dataInicial), formato.format(dataReferencia), 3, true));

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        parametros = filtrosParametrosUnidade(parametros);
        if (unidadeGestora != null || getApresentacao().equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }

        if (empenho != null) {
            parametros.add(new ParametrosRelatorios(" emp.id ", ":empId", null, OperacaoRelatorio.IGUAL, this.empenho.getId(), null, 1, false));
            filtros += " Empenho: " + empenho.getNumero() + " -";
        }

        if (this.contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" CBE.ID ", ":cbeId", null, OperacaoRelatorio.IGUAL, this.contaBancariaEntidade.getId(), null, 4, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";
        }

        if (this.subConta != null) {
            parametros.add(new ParametrosRelatorios(" SUB.id ", ":subId", null, OperacaoRelatorio.IGUAL, this.subConta.getId(), null, 4, false));
            filtros += " Conta Financeira: " + subConta.getCodigo() + " -";
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigo", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigoSemZerosAoFinal() + " -";
        }
        if (dividaPublica != null) {
            parametros.add(new ParametrosRelatorios(" emp.dividapublica_id ", ":dpId", null, OperacaoRelatorio.IGUAL, dividaPublica.getId(), null, 4, false));
            filtros += " Dívida Pública: " + dividaPublica.getNomeDivida() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "EXTRATO-CREDOR";
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public Boolean getQuebraPorEmpenho() {
        return quebraPorEmpenho;
    }

    public void setQuebraPorEmpenho(Boolean quebraPorEmpenho) {
        this.quebraPorEmpenho = quebraPorEmpenho;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        fonteDeRecursos = null;
        contaBancariaEntidade = retornoContaBancaria();
    }

    public ContaBancariaEntidade retornoContaBancaria() {
        try {
            return subConta.getContaBancariaEntidade();
        } catch (Exception e) {
            return new ContaBancariaEntidade();
        }
    }

    public List<Conta> completarContaDespesa(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaControlador().getExercicioCorrente());
    }
}
