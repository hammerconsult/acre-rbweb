/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.PropostaConcessaoDiariaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-diarias", pattern = "/relatorio/diarias/", viewId = "/faces/financeiro/relatorio/relatoriodiarias.xhtml")
})
@ManagedBean
public class RelatorioDiariasControlador extends RelatorioContabilSuperControlador {

    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private PropostaConcessaoDiaria propostaConcessaoDiaria;
    private String filtro;
    private List<HierarquiaOrganizacional> listaDeHierarquias;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;
    private Exercicio exercicioRecuperado;

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public void recuperarExercicioDataFinal(){
        exercicioRecuperado = buscarExercicioPelaDataFinal();
    }

    public RelatorioDiariasControlador() {
    }

    public List<HierarquiaOrganizacional> getListaDeHierarquias() {
        return listaDeHierarquias;
    }

    public void setListaDeHierarquias(List<HierarquiaOrganizacional> listaDeHierarquias) {
        this.listaDeHierarquias = listaDeHierarquias;
    }

    @URLAction(mappingId = "relatorio-diarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.dataInicial = getSistemaFacade().getDataOperacao();
        this.dataFinal = getSistemaFacade().getDataOperacao();
        this.propostaConcessaoDiaria = null;
        this.filtro = "";
        this.listaDeHierarquias = new ArrayList<>();
        this.exercicioRecuperado = getSistemaFacade().getExercicioCorrente();
    }

    public List<PropostaConcessaoDiaria> completaPropostaConcessaoDiaria(String parte) {
        return propostaConcessaoDiariaFacade.listaPropostaConcessaoDiariaPorPessoaOuCodigo(exercicioRecuperado, parte);
    }

    public void gerarRelatorioDiarias() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("EXERCICIO_ID", exercicioRecuperado.getId());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/diarias/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" PROP.tipoProposta ", ":tipoProposta", null, OperacaoRelatorio.DIFERENTE, TipoProposta.SUPRIMENTO_FUNDO.name(), null, 1, false));
        parametros.add(new ParametrosRelatorios(" trunc(coalesce(dc.DATADIARIA, PROP.DATALANCAMENTO)) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        parametros.add(new ParametrosRelatorios(" DESP.EXERCICIO_ID", ":exercicio", null, OperacaoRelatorio.IGUAL, exercicioRecuperado.getId(), null, 1, false));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (this.propostaConcessaoDiaria != null) {
            parametros.add(new ParametrosRelatorios(" PROP.ID ", ":propId", null, OperacaoRelatorio.IGUAL, propostaConcessaoDiaria.getId(), null, 1, false));
            filtro += " Diária: " + this.propostaConcessaoDiaria.getCodigo() + " - " + this.propostaConcessaoDiaria.getPessoaFisica().getNome() + " -";
        }

        if (listaDeHierarquias.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaDeHierarquias) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), exercicioRecuperado, getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> listaIdsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            parametros.add(new ParametrosRelatorios(" ug.exercicio_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, exercicioRecuperado.getId(), null, 1, false));
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" PES.ID ", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtro += " Pessoa: " + pessoa.getCpf_Cnpj() + " - " + pessoa.getNome() + " -";
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-DIARIAS";
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public List<Pessoa> completarPessoasFisicas(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaPessoasFisicas(parte, SituacaoCadastralPessoa.ATIVO);
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Exercicio getExercicioRecuperado() {
        return exercicioRecuperado;
    }

    public void setExercicioRecuperado(Exercicio exercicioRecuperado) {
        this.exercicioRecuperado = exercicioRecuperado;
    }
}
