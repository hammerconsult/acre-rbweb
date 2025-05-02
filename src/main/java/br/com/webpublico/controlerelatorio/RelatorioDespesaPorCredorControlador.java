package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 11/12/2014.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-despesa-credor", pattern = "/relatorio/despesa-credor/", viewId = "/faces/financeiro/relatorio/relatoriodespesaporcredor.xhtml")
})
@ManagedBean
public class RelatorioDespesaPorCredorControlador extends AbstractReport implements Serializable {

    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private Converter converterFornecedor;
    private Pessoa pessoa;
    private Exercicio exercicio;
    private UnidadeGestora unidadeGestora;
    private Mes mes;

    public RelatorioDespesaPorCredorControlador() {
    }

    public List<UnidadeGestora> completarUnidadesGestora(String parte) {
        if (exercicio != null) {
            return unidadeGestoraFacade.listaFiltrandoPorExercicio(exercicio, parte);
        }
        return null;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (pessoa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-despesa-credor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        pessoa = null;
        exercicio = null;
        unidadeGestora = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTRO_RELATORIO", mes.getDescricao().toUpperCase());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("DATAINICIAL", getDataInicial(mes, exercicio));
            dto.adicionarParametro("DATAFINAL", getDataFinal(mes, exercicio));
            dto.adicionarParametro("CREDOR_NOME", pessoa.getNome());
            dto.adicionarParametro("CREDOR", pessoa.getId());
            dto.adicionarParametro("CPF_CNPJ", pessoa.getCpf_Cnpj());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            if (unidadeGestora != null) {
                dto.adicionarParametro("UNIDADEGESTORA", unidadeGestora.getId());
                if (unidadeGestora.getCodigo().equals("001")){
                    dto.adicionarParametro("UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
                }
            }
            dto.setNomeRelatorio("RELATÓRIO-DE-DESPESA-POR-CREDOR");
            dto.setApi("contabil/despesa-credor/");
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

    public Converter getConverterFornecedor() {
        if (converterFornecedor == null) {
            converterFornecedor = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterFornecedor;
    }

    public List<Pessoa> completarFornecedor(String parte) {
        return pessoaFacade.listaTodasPessoasPorId(parte.trim());
    }

    public List<Exercicio> completarExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public String getDataInicial(Mes mes, Exercicio exercicio) {
        return "01/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public String getDataFinal(Mes mes, Exercicio exercicio) {
        return Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
