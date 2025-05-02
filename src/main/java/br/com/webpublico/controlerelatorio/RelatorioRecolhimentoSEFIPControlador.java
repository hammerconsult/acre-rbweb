package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Sefip;
import br.com.webpublico.entidadesauxiliares.DetalhesRelatorioRecolhimentoSefip;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioRecolhimentoSefipFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by Desenvolvimento on 06/09/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioRecolhimentoSefip", pattern = "/relatorio/recolhimento/sefip/", viewId = "/faces/rh/relatorios/relatorio-recolhimento-sefip/relatorio-de-recolhimento-sefip.xhtml"),
    @URLMapping(id = "acompanhamentoSefip", pattern = "/relatorio/recolhimento/sefip/acompanhamento/", viewId = "/faces/rh/relatorios/relatorio-recolhimento-sefip/acompanhamento.xhtml")
})
public class RelatorioRecolhimentoSEFIPControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioRecolhimentoSefipFacade relatorioRecolhimentoSefipFacade;

    private Filtros filtros;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<Future<AssistenteBarraProgresso>> futuresAssistente;
    private List<Future<Boolean>> futuresContratos;
    private List<DetalhesRelatorioRecolhimentoSefip> detalhesRelatorio;
    private List<ContratoFP> contratosGeral;
    private Boolean relatorioConcluido;
    private Boolean renderRemoteCommand;
    private Boolean gerouDetalhes;

    public RelatorioRecolhimentoSEFIPControlador() {
        filtros = new Filtros();
        detalhesRelatorio = new ArrayList<>();
        relatorioConcluido = Boolean.FALSE;
        renderRemoteCommand = Boolean.TRUE;
        gerouDetalhes = Boolean.FALSE;
    }

    @URLAction(mappingId = "novoRelatorioRecolhimentoSefip", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = new Filtros();
    }

    @URLAction(mappingId = "acompanhamentoSefip", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void acompanhar() {
        this.filtros = (Filtros) Web.pegaDaSessao(Filtros.class);
        assistenteBarraProgresso = new AssistenteBarraProgresso("Relatório Recolhimento Sefip", 0);
    }

    public void navegarParaAcompanhamento() {
        try {
            validarRelatorio();
            Web.navegacao("/relatorio/recolhimento/sefip/", "/relatorio/recolhimento/sefip/acompanhamento/", filtros);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void navegarParaRelatorioSefip() {
        Web.limpaNavegacao();
        FacesUtil.redirecionamentoInterno("/relatorio/recolhimento/sefip/");
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (filtros.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Mês.");
        }
        if (filtros.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício.");
        }
        if (filtros.getSefip() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Sefip.");
        }
        if (filtros.getSefip() != null) {
            if (!filtros.sefip.getEntidade().isTemAliquotaRAT()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe a Alíquota RAT da entidade <b>" + filtros.getSefip().getEntidade()
                    + "</b>. <b><a href='" + FacesUtil.getRequestContextPath() + "/entidade/editar/" + filtros.getSefip().getEntidade().getId() + "' target='_blank'>Clique aqui para editar</a></b>");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public void buscarContratos() {
        renderRemoteCommand = Boolean.FALSE;
        contratosGeral = Lists.newArrayList();
        futuresContratos = Lists.newArrayList();
        List<HierarquiaOrganizacional> hierarquias = relatorioRecolhimentoSefipFacade.buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(filtros);
        if (!hierarquias.isEmpty()) {
            int partes = hierarquias.size() > 5 ? ((hierarquias.size() / 5) + 1) : hierarquias.size();
            List<List<HierarquiaOrganizacional>> partesContratos = Lists.partition(hierarquias, partes);

            for (List<HierarquiaOrganizacional> parteHierarquias : partesContratos) {
                futuresContratos.add(relatorioRecolhimentoSefipFacade.buscarContratos(contratosGeral, parteHierarquias, filtros, assistenteBarraProgresso));
            }
        } else {
            navegarParaRelatorioSefip();
        }
    }

    public void gerarDetalhesDoRelatorio() {
        if (gerouDetalhes) {
            return;
        }
        futuresAssistente = new ArrayList<>();
        int partes = contratosGeral.size() > 100 ? ((contratosGeral.size() / 5) + 1) : contratosGeral.size();
        List<List<ContratoFP>> partesContratos = Lists.partition(contratosGeral, partes);
        detalhesRelatorio = new ArrayList<>();
        for (List<ContratoFP> parte : partesContratos) {
            futuresAssistente.add(relatorioRecolhimentoSefipFacade.processarContratos(filtros, detalhesRelatorio, assistenteBarraProgresso, parte));
        }
        gerouDetalhes = Boolean.TRUE;
    }

    public Boolean getAcompanharBuscarContratos() {
        boolean terminou = false;
        if (futuresContratos != null && !futuresContratos.isEmpty()) {
            terminou = true;
            for (Future<Boolean> future : futuresContratos) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            gerarDetalhesDoRelatorio();
        }
        return terminou;
    }

    public void setAcompanharBuscarContratos(Boolean acompanharContrato) {
        //para utilizar o inputhidden
    }

    public Boolean getAcompanharProcesso() {
        boolean terminou = false;
        if (futuresAssistente != null && !futuresAssistente.isEmpty()) {
            terminou = true;
            for (Future<AssistenteBarraProgresso> future : futuresAssistente) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            relatorioConcluido = Boolean.TRUE;
        }
        return terminou;
    }

    public void setAcompanharProcesso(Boolean acompanharProcesso) {
        //para utilizar o inputhidden
    }

    public void definirTipoDeRelatorio(Boolean agrupado) {
        for (DetalhesRelatorioRecolhimentoSefip detalhes : detalhesRelatorio) {
            detalhes.setAgrupado(agrupado);
        }
    }

    public void gerarRelatorioAgrupado() throws IOException, JRException {
        definirTipoDeRelatorio(Boolean.TRUE);
        gerarRelatorio("RelatorioRecolhimentoSefipAgrupado.jasper");
    }

    public void gerarRelatorioListagem() throws IOException, JRException {
        definirTipoDeRelatorio(Boolean.FALSE);
        gerarRelatorio("RelatorioRecolhimentoSefip.jasper");
    }

    private void gerarRelatorio(String arquivoJasper) throws IOException, JRException {
        Collections.sort(detalhesRelatorio);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(detalhesRelatorio);
        HashMap parameters = new HashMap();

        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("NOMERELATORIO", "Relatório de Recolhimento SEFIP");
        parameters.put("SECRETARIA", "RECURSOS HUMANOS");
        parameters.put("MODULO", "RH");
        parameters.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
        parameters.put("FILTROS", filtros.getDescricaoFiltros());
        parameters.put("ALIQUOTARAT", filtros.getSefip().getEntidade().getAliquotaRAT().divide(BigDecimal.valueOf(100)).setScale(2));

        if (getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", getSistemaFacade().getUsuarioCorrente().getUsername());
        }
        gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
    }

    public List<SelectItem> recuperarSefip() {
        if (filtros.isExercicioAndMesPreenchidos()) {
            List<Sefip> lista = relatorioRecolhimentoSefipFacade.getSefipFacade().buscarSefipPorMesAndAno(filtros.getMes(), filtros.getExercicio());
            return Util.getListSelectItem(lista);
        }
        return new ArrayList<>();
    }

    public Filtros getFiltros() {
        return filtros;
    }

    public void setFiltros(Filtros filtros) {
        this.filtros = filtros;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<Future<AssistenteBarraProgresso>> getFuturesAssistente() {
        return futuresAssistente;
    }

    public void setFuturesAssistente(List<Future<AssistenteBarraProgresso>> futuresAssistente) {
        this.futuresAssistente = futuresAssistente;
    }

    public List<DetalhesRelatorioRecolhimentoSefip> getDetalhesRelatorio() {
        return detalhesRelatorio;
    }

    public void setDetalhesRelatorio(List<DetalhesRelatorioRecolhimentoSefip> detalhesRelatorio) {
        this.detalhesRelatorio = detalhesRelatorio;
    }

    public Boolean getRelatorioConcluido() {
        return relatorioConcluido;
    }

    public void setRelatorioConcluido(Boolean relatorioConcluido) {
        this.relatorioConcluido = relatorioConcluido;
    }

    public Boolean getRenderRemoteCommand() {
        return renderRemoteCommand;
    }

    public void setRenderRemoteCommand(Boolean renderRemoteCommand) {
        this.renderRemoteCommand = renderRemoteCommand;
    }

    public class Filtros {

        private Mes mes;
        private Exercicio exercicio;
        private Sefip sefip;
        private Boolean salarioMaternidade;
        private Boolean salarioFamilia;

        public Filtros() {
            salarioFamilia = Boolean.TRUE;
            salarioMaternidade = Boolean.TRUE;
        }

        public Mes getMes() {
            return mes;
        }

        public void setMes(Mes mes) {
            this.mes = mes;
        }

        public Exercicio getExercicio() {
            return exercicio;
        }

        public void setExercicio(Exercicio exercicio) {
            this.exercicio = exercicio;
        }

        public Sefip getSefip() {
            return sefip;
        }

        public void setSefip(Sefip sefip) {
            this.sefip = sefip;
        }

        public Boolean getSalarioMaternidade() {
            return salarioMaternidade;
        }

        public void setSalarioMaternidade(Boolean salarioMaternidade) {
            this.salarioMaternidade = salarioMaternidade;
        }

        public Boolean getSalarioFamilia() {
            return salarioFamilia;
        }

        public void setSalarioFamilia(Boolean salarioFamilia) {
            this.salarioFamilia = salarioFamilia;
        }

        public Date getPrimeiroDiaDoMes() {
            Calendar pd = Calendar.getInstance();
            try {
                pd.set(Calendar.DAY_OF_MONTH, 1);
                pd.set(Calendar.MONTH, this.getMes().getNumeroMes() - 1);
                pd.set(Calendar.YEAR, this.getExercicio().getAno());
                pd.setTime(Util.getDataHoraMinutoSegundoZerado(pd.getTime()));
            } catch (NullPointerException nu) {
            }
            return pd.getTime();
        }

        public Date getUltimoDiaDoMes() {
            Calendar ud = Calendar.getInstance();
            try {
                ud.set(Calendar.MONTH, this.getMes().getNumeroMes() - 1);
                ud.set(Calendar.DAY_OF_MONTH, ud.getActualMaximum(Calendar.DAY_OF_MONTH));
                ud.set(Calendar.YEAR, this.getExercicio().getAno());
                ud.setTime(Util.getDataHoraMinutoSegundoZerado(ud.getTime()));
            } catch (NullPointerException nu) {
            }
            return ud.getTime();
        }

        public Boolean isExercicioAndMesPreenchidos() {
            return mes != null && exercicio != null;
        }

        public String getDescricaoFiltros() {
            String descricao = "";
            if (this.mes != null) {
                descricao += " -  Mês: " + this.mes;
            }
            if (this.exercicio != null) {
                descricao += " -  Ano: " + this.exercicio.getAno();
            }
            if (this.sefip != null) {
                descricao += " -  Sefip: " + this.sefip;
            }
            descricao += this.salarioMaternidade ? " -  Calcular salário maternidade" : " -  Não calcular salário maternidade";
            descricao += this.salarioFamilia ? " -  Calcular salário família" : " -  Não calcular salário família";
            return descricao;
        }
    }
}
