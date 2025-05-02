/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.controle.PesquisaCadastroEconomicoControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.ProcessoCalculoISS;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.PessoaJuridicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "relatorioDemonstrativoIssPorCmc")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioLancamentoIssCMC", pattern = "/tributario/relatorio-de-lancamento-de-iss/", viewId = "/faces/tributario/issqn/relatorios/demonstrativoIssPorCmc.xhtml"),
})

public class RelatorioDemonstrativoIssPorCmc extends AbstractReport implements Serializable {

    @EJB
    private CadastroEconomicoFacade inscricaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private CadastroEconomico inscricao;
    private ConverterAutoComplete converterInscricao;
    private Pessoa pessoa;
    private ConverterAutoComplete converterPessoa;
    private PessoaJuridica pessoaJuridica;
    private ConverterAutoComplete converterPessoaJuridica;
    private StringBuilder where;
    private StringBuilder filtros;
    private Mes mesInicial;
    private Mes mesFinal;
    private Integer exercicioInicial;
    private Integer exercicioFinal;
    private Integer relatorioSelecionado;
    private ProcessoCalculoISS processo;
    private String tipoCalculoISS;

    public ProcessoCalculoISS getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoCalculoISS processo) {
        this.processo = processo;
    }

    public Integer getRelatorioSelecionado() {
        return relatorioSelecionado;
    }

    public void setRelatorioSelecionado(Integer relatorioSelecionado) {
        this.relatorioSelecionado = relatorioSelecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public ConverterAutoComplete getConverterInscricao() {
        if (converterInscricao == null) {
            converterInscricao = new ConverterAutoComplete(CadastroEconomico.class, inscricaoFacade);
        }
        return converterInscricao;
    }

    public ConverterAutoComplete getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(Pessoa.class, pessoaJuridicaFacade);
        }
        return converterPessoaJuridica;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CadastroEconomico getInscricao() {
        return inscricao;
    }

    public void setInscricao(CadastroEconomico inscricao) {
        this.inscricao = inscricao;
    }

    public List<CadastroEconomico> completaInscricao(String parte) {
        return inscricaoFacade.listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(parte);
    }

    public List<Pessoa> completaPessoas(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<Pessoa> completaPessoaJuridica(String parte) {
        return pessoaFacade.listaPessoasJuridicas(parte.trim());
    }

    public String getTipoCalculoISS() {
        return tipoCalculoISS;
    }

    public void setTipoCalculoISS(String tipoCalculoISS) {
        this.tipoCalculoISS = tipoCalculoISS;
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = Lists.newArrayList();
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            situacoes.add(sit);
        }
        return situacoes;
    }


    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        inscricao = (CadastroEconomico) obj;
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        for (SituacaoCadastralCadastroEconomico sit : SituacaoCadastralCadastroEconomico.values()) {
            situacao.add(sit);
        }
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioInicial != null && exercicioFinal != null && mesInicial != null && mesFinal != null) {
            if (exercicioFinal < exercicioInicial) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício de Referência Inicial não pode ser posterior ao Exercício de Referência Final.");
            } else {
                if (mesInicial.getNumeroMes() > mesFinal.getNumeroMes()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Mês de Referência Inicial não pode ser posterior ao Mês de Referência Final.");
                }
            }
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtros = new StringBuilder();
            where = new StringBuilder();
            validarCampos();
            montarCondicao();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.adicionarParametro("complementoQuery", where.toString());
            dto.setNomeRelatorio("RELATÓRIO DE LANÇAMENTO DE ISS");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.setApi(relatorioSelecionado == 1 ? "tributario/lancamento-iss/" : "tributario/lancamento-iss/detalhado/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public RelatorioDemonstrativoIssPorCmc() {
    }

    @URLAction(mappingId = "novoRelatorioLancamentoIssCMC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = new StringBuilder();
        mesInicial = Mes.JANEIRO;
        mesFinal = Mes.DEZEMBRO;
        relatorioSelecionado = 1;
        setExercicioInicial(sistemaControlador.getExercicioCorrente().getAno());
        setExercicioFinal(sistemaControlador.getExercicioCorrente().getAno());
        inscricao = null;
        pessoa = null;
        pessoaJuridica = null;
        where = new StringBuilder();
    }

    public void montarCondicao() {
        String juncao = "where ";
        if (inscricao != null && inscricao.getId() != null) {
            this.where.append(juncao).append(" ce.id = ").append(inscricao.getId());
            juncao = " and ";
            filtros.append("Cadastro Econômico = ").append(inscricao.getInscricaoCadastral()).append("; ");
        }
        if (exercicioInicial != null) {
            this.where.append(juncao).append(" exercicio.ano between ").append(exercicioInicial);
            juncao = " and ";
            filtros.append("Exercicio Inicial = ").append(exercicioInicial).append("; ");
        }
        if (exercicioFinal != null) {
            this.where.append(juncao).append(" ").append(exercicioFinal);
            juncao = " and ";
            filtros.append("Exercício Final = ").append(exercicioFinal).append("; ");
        }
        if (mesInicial != null) {
            this.where.append(juncao).append("iss.mesreferencia between -1 ");
            juncao = " and ";
            filtros.append("Mês de Referência Inicial = ").append(mesInicial).append("; ");
        }
        if (mesFinal != null) {
            this.where.append(juncao).append(" ").append(mesFinal.getNumeroMes());
            juncao = " and ";
            filtros.append("Mês de Referência Final = ").append(mesFinal).append("; ");
        }

        if (tipoCalculoISS != null && !tipoCalculoISS.isEmpty()) {
            this.where.append(juncao).append(" calculo.tipocalculoiss = '").append(tipoCalculoISS.trim()).append("'  ");
            juncao = " and ";
            filtros.append("Tipo de Cálculo = ").append(tipoCalculoISS.trim()).append(" ;");
        }
    }
}
