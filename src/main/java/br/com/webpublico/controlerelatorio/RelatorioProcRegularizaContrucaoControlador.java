package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ProcRegularizaConstrucaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioProcessoRegularizaConstrucao", pattern = "/relatorio/processo-regularizacao-contrucao/", viewId = "/faces/tributario/processoregularizaconstrucao/relatorios/relatorio.xhtml")
})
public class RelatorioProcRegularizaContrucaoControlador {

    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    private FiltroRelatorio filtroRelatorio;

    public FiltroRelatorio getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorio filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    @URLAction(mappingId = "novoRelatorioProcessoRegularizaConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtroRelatorio = new FiltroRelatorio();
    }


    public void gerarRelatorio(String tipoRelatorio) {
        try {
            filtroRelatorio.validarRegras();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setNomeRelatorio("Relatório de processos de regularização de construção");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de processos de regularização de construção");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
            dto.adicionarParametro("WHERE", filtroRelatorio.montarWhere());
            dto.adicionarParametro("USUARIO", procRegularizaConstrucaoFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
            dto.setApi("tributario/processo-regularizacao-construcao/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<SelectItem> getSituacoesProcesso() {
        return Util.getListSelectItem(ProcRegularizaConstrucao.Situacao.values());
    }

    public List<SelectItem> getSituacoesAlvara() {
        return Util.getListSelectItem(AlvaraConstrucao.Situacao.values());
    }

    public List<SelectItem> getSituacoesHabitese() {
        return Util.getListSelectItem(Habitese.Situacao.values());
    }

    public class FiltroRelatorio {
        private Long codigo;
        private Integer exercicioInicial;
        private Integer exercicioFinal;
        private String numeroProtocolo;
        private String anoProtocolo;
        private Date dataInicial;
        private Date dataFinal;
        private UsuarioSistema usuario;
        private ProcRegularizaConstrucao.Situacao situacaoProc;
        private String inscricaoInicial;
        private String inscricaoFinal;
        private Bairro bairro;
        private Logradouro logradouro;
        private ResponsavelServico responsavelServico;
        private String matriculaInss;
        private Long codigoAlvara;
        private Integer exercicioAlvara;
        private Date dataExpAlvaraInicial;
        private Date dataExpAlvaraFinal;
        private Date dataVencCartazAlvaraInicial;
        private Date dataVencCartazAlvaraFinal;
        private Date dataVencDebitoAlvaraInicial;
        private Date dataVencDebitoAlvaraFinal;
        private AlvaraConstrucao.Situacao situacaoAlvara;
        private Long codigoHabitese;
        private Integer exercicioHabitese;
        private Date dataExpHabiteseInicial;
        private Date dataExpHabiteseFinal;
        private Date dataVencISSHabiteseInicial;
        private Date dataVencISSHabiteseFinal;
        private Habitese.Situacao situacaoHabitese;

        public Long getCodigo() {
            return codigo;
        }

        public void setCodigo(Long codigo) {
            this.codigo = codigo;
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

        public String getNumeroProtocolo() {
            return numeroProtocolo;
        }

        public void setNumeroProtocolo(String numeroProtocolo) {
            this.numeroProtocolo = numeroProtocolo;
        }

        public String getAnoProtocolo() {
            return anoProtocolo;
        }

        public void setAnoProtocolo(String anoProtocolo) {
            this.anoProtocolo = anoProtocolo;
        }

        public Date getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(Date dataInicial) {
            this.dataInicial = dataInicial;
        }

        public Date getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(Date dataFinal) {
            this.dataFinal = dataFinal;
        }

        public UsuarioSistema getUsuario() {
            return usuario;
        }

        public void setUsuario(UsuarioSistema usuario) {
            this.usuario = usuario;
        }

        public ProcRegularizaConstrucao.Situacao getSituacaoProc() {
            return situacaoProc;
        }

        public void setSituacaoProc(ProcRegularizaConstrucao.Situacao situacaoProc) {
            this.situacaoProc = situacaoProc;
        }

        public String getInscricaoInicial() {
            return inscricaoInicial;
        }

        public void setInscricaoInicial(String inscricaoInicial) {
            this.inscricaoInicial = inscricaoInicial;
        }

        public String getInscricaoFinal() {
            return inscricaoFinal;
        }

        public void setInscricaoFinal(String inscricaoFinal) {
            this.inscricaoFinal = inscricaoFinal;
        }

        public Bairro getBairro() {
            return bairro;
        }

        public void setBairro(Bairro bairro) {
            this.bairro = bairro;
        }

        public Logradouro getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(Logradouro logradouro) {
            this.logradouro = logradouro;
        }

        public ResponsavelServico getResponsavelServico() {
            return responsavelServico;
        }

        public void setResponsavelServico(ResponsavelServico responsavelServico) {
            this.responsavelServico = responsavelServico;
        }

        public String getMatriculaInss() {
            return matriculaInss;
        }

        public void setMatriculaInss(String matriculaInss) {
            this.matriculaInss = matriculaInss;
        }

        public Long getCodigoAlvara() {
            return codigoAlvara;
        }

        public void setCodigoAlvara(Long codigoAlvara) {
            this.codigoAlvara = codigoAlvara;
        }

        public Integer getExercicioAlvara() {
            return exercicioAlvara;
        }

        public void setExercicioAlvara(Integer exercicioAlvara) {
            this.exercicioAlvara = exercicioAlvara;
        }

        public Date getDataExpAlvaraInicial() {
            return dataExpAlvaraInicial;
        }

        public void setDataExpAlvaraInicial(Date dataExpAlvaraInicial) {
            this.dataExpAlvaraInicial = dataExpAlvaraInicial;
        }

        public Date getDataExpAlvaraFinal() {
            return dataExpAlvaraFinal;
        }

        public void setDataExpAlvaraFinal(Date dataExpAlvaraFinal) {
            this.dataExpAlvaraFinal = dataExpAlvaraFinal;
        }

        public Date getDataVencCartazAlvaraInicial() {
            return dataVencCartazAlvaraInicial;
        }

        public void setDataVencCartazAlvaraInicial(Date dataVencCartazAlvaraInicial) {
            this.dataVencCartazAlvaraInicial = dataVencCartazAlvaraInicial;
        }

        public Date getDataVencCartazAlvaraFinal() {
            return dataVencCartazAlvaraFinal;
        }

        public void setDataVencCartazAlvaraFinal(Date dataVencCartazAlvaraFinal) {
            this.dataVencCartazAlvaraFinal = dataVencCartazAlvaraFinal;
        }

        public Date getDataVencDebitoAlvaraInicial() {
            return dataVencDebitoAlvaraInicial;
        }

        public void setDataVencDebitoAlvaraInicial(Date dataVencDebitoAlvaraInicial) {
            this.dataVencDebitoAlvaraInicial = dataVencDebitoAlvaraInicial;
        }

        public Date getDataVencDebitoAlvaraFinal() {
            return dataVencDebitoAlvaraFinal;
        }

        public void setDataVencDebitoAlvaraFinal(Date dataVencDebitoAlvaraFinal) {
            this.dataVencDebitoAlvaraFinal = dataVencDebitoAlvaraFinal;
        }

        public AlvaraConstrucao.Situacao getSituacaoAlvara() {
            return situacaoAlvara;
        }

        public void setSituacaoAlvara(AlvaraConstrucao.Situacao situacaoAlvara) {
            this.situacaoAlvara = situacaoAlvara;
        }

        public Long getCodigoHabitese() {
            return codigoHabitese;
        }

        public void setCodigoHabitese(Long codigoHabitese) {
            this.codigoHabitese = codigoHabitese;
        }

        public Integer getExercicioHabitese() {
            return exercicioHabitese;
        }

        public void setExercicioHabitese(Integer exercicioHabitese) {
            this.exercicioHabitese = exercicioHabitese;
        }

        public Date getDataExpHabiteseInicial() {
            return dataExpHabiteseInicial;
        }

        public void setDataExpHabiteseInicial(Date dataExpHabiteseInicial) {
            this.dataExpHabiteseInicial = dataExpHabiteseInicial;
        }

        public Date getDataExpHabiteseFinal() {
            return dataExpHabiteseFinal;
        }

        public void setDataExpHabiteseFinal(Date dataExpHabiteseFinal) {
            this.dataExpHabiteseFinal = dataExpHabiteseFinal;
        }

        public Date getDataVencISSHabiteseInicial() {
            return dataVencISSHabiteseInicial;
        }

        public void setDataVencISSHabiteseInicial(Date dataVencISSHabiteseInicial) {
            this.dataVencISSHabiteseInicial = dataVencISSHabiteseInicial;
        }

        public Date getDataVencISSHabiteseFinal() {
            return dataVencISSHabiteseFinal;
        }

        public void setDataVencISSHabiteseFinal(Date dataVencISSHabiteseFinal) {
            this.dataVencISSHabiteseFinal = dataVencISSHabiteseFinal;
        }

        public Habitese.Situacao getSituacaoHabitese() {
            return situacaoHabitese;
        }

        public void setSituacaoHabitese(Habitese.Situacao situacaoHabitese) {
            this.situacaoHabitese = situacaoHabitese;
        }

        private String whereOuAnd(StringBuilder condicao) {
            String juncao = " AND ";
            String where = " WHERE ";
            if (condicao.toString().isEmpty()
                || !condicao.toString().toLowerCase().contains(where.toLowerCase().trim())) {
                return where;
            } else {
                return juncao;
            }
        }

        private String montarWhere() {
            StringBuilder retorno = new StringBuilder();
            if (codigo != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.CODIGO = ").append(codigo);
            }
            if (exercicioInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" EXPROC.ANO BETWEEN ").append(exercicioInicial).append(" AND ").append(exercicioFinal);
            }
            if (!Strings.isNullOrEmpty(numeroProtocolo)) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.NUMEROPROTOCOLO = ").append(numeroProtocolo);
            }
            if (!Strings.isNullOrEmpty(anoProtocolo)) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ANOPROTOCOLO = ").append(anoProtocolo);
            }
            if (dataInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" TRUNC(PROC.DATACRIACAO) BETWEEN TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataInicial))
                    .append("', 'dd/MM/yyyy')) AND TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataFinal)).append("', 'dd/MM/yyyy'))");
            }
            if (usuario != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" US.ID = ").append(usuario.getId());
            }
            if (situacaoProc != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.SITUACAO = '").append(situacaoProc.name()).append("'");
            }
            if (!Strings.isNullOrEmpty(inscricaoInicial)) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" CI.INSCRICAOCADASTRAL BETWEEN ").append("'").append(inscricaoInicial.trim())
                    .append("' AND '").append(inscricaoFinal.trim()).append("'");
            }
            if (bairro != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" LB.BAIRRO_ID = ").append(bairro.getId());
            }
            if (logradouro != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" LB.LOGRADOURO_ID = ").append(logradouro.getId());
            }
            if (responsavelServico != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" RESP.ID = ").append(responsavelServico.getId());
            }
            if (!Strings.isNullOrEmpty(matriculaInss)) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.MATRICULAINSS = '").append(matriculaInss.trim()).append("'");
            }
            if (codigoAlvara != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.CODIGO = ")
                    .append(codigoAlvara).append(") ");
            }
            if (this.exercicioAlvara != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC INNER JOIN EXERCICIO EXALVARA ON AC.EXERCICIO_ID = EXALVARA.ID WHERE EXALVARA.ANO = ")
                    .append(exercicioAlvara).append(") ");
            }
            if (dataExpAlvaraInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE TRUNC(AC.DATAEXPEDICAO) ")
                    .append(" BETWEEN TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataExpAlvaraInicial)).append("', 'dd/MM/yyyy')) AND TRUNC(TO_DATE('")
                    .append(DataUtil.getDataFormatada(dataExpAlvaraFinal)).append("', 'dd/MM/yyyy'))) ");
            }
            if (dataVencCartazAlvaraInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE TRUNC(AC.DATAVENCIMENTOCARTAZ) ")
                    .append(" BETWEEN TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataVencCartazAlvaraInicial)).append("', 'dd/MM/yyyy')) AND TRUNC(TO_DATE('")
                    .append(DataUtil.getDataFormatada(dataVencCartazAlvaraFinal)).append("', 'dd/MM/YYYY'))) ");
            }
            if (situacaoAlvara != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.SITUACAO = '")
                    .append(situacaoAlvara.name()).append("') ");
            }
            if (codigoHabitese != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.ID IN (SELECT H.ALVARACONSTRUCAO_ID FROM HABITESE H WHERE H.CODIGO = ")
                    .append(codigoHabitese).append(")) ");
            }
            if (dataExpHabiteseInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.ID IN (SELECT H.ALVARACONSTRUCAO_ID FROM HABITESE H ")
                    .append(" WHERE TRUNC(H.DATAEXPEDICAOTERMO) BETWEEN TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataExpHabiteseInicial))
                    .append("', 'dd/MM/yyyy')) AND TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataExpHabiteseFinal)).append("', 'dd/MM/yyyy')))) ");
            }
            if (dataVencISSHabiteseInicial != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.ID IN (SELECT H.ALVARACONSTRUCAO_ID FROM HABITESE H ")
                    .append(" WHERE TRUNC(H.DATAVENCIMENTOISS) BETWEEN TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataVencISSHabiteseInicial))
                    .append("', 'dd/MM/yyyy')) AND TRUNC(TO_DATE('").append(DataUtil.getDataFormatada(dataVencISSHabiteseFinal)).append("', 'dd/MM/yyyy')))) ");
            }
            if (situacaoHabitese != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC WHERE AC.ID IN (SELECT H.ALVARACONSTRUCAO_ID FROM HABITESE H WHERE H.SITUACAO = '")
                    .append(situacaoHabitese.name()).append("')) ");
            }
            if (this.exercicioHabitese != null) {
                retorno.append(whereOuAnd(retorno));
                retorno.append(" PROC.ID IN (SELECT AC.PROCREGULARIZACONSTRUCAO_ID FROM ALVARACONSTRUCAO AC " +
                        "WHERE AC.ID IN (SELECT H.ALVARACONSTRUCAO_ID FROM HABITESE H INNER JOIN EXERCICIO EXH ON H.EXERCICIO_ID = EXH.ID WHERE EXH.ANO = ")
                    .append(this.exercicioHabitese).append(")) ");
            }
            return retorno.toString();
        }

        private void validarRegras() {
            ValidacaoException ve = new ValidacaoException();
            if ((dataInicial != null && dataFinal == null) || (dataFinal != null && dataInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data Inicial e Final");
            }
            if ((exercicioInicial != null && exercicioFinal == null) || (exercicioFinal != null && exercicioInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar o Exercício Inicial e Final");
            }
            if ((!Strings.isNullOrEmpty(inscricaoInicial) && Strings.isNullOrEmpty(inscricaoFinal)) || (!Strings.isNullOrEmpty(inscricaoFinal) && Strings.isNullOrEmpty(inscricaoInicial))) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Inscrição Inicial e Final");
            }
            if ((!Strings.isNullOrEmpty(numeroProtocolo) && Strings.isNullOrEmpty(anoProtocolo)) || (!Strings.isNullOrEmpty(anoProtocolo) && Strings.isNullOrEmpty(numeroProtocolo))) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar o Número e Ano do Protocolo");
            }
            if ((dataExpAlvaraInicial != null && dataExpAlvaraFinal == null) || (dataExpAlvaraFinal != null && dataExpAlvaraInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data de Expedição do Alvará Inicial e Final");
            }
            if ((dataVencCartazAlvaraInicial != null && dataVencCartazAlvaraFinal == null) || (dataVencCartazAlvaraFinal != null && dataVencCartazAlvaraInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data de Vencimento do Cartaz do Alvará Inicial e Final");
            }
            if ((dataVencDebitoAlvaraInicial != null && dataVencDebitoAlvaraFinal == null) || (dataVencDebitoAlvaraFinal != null && dataVencDebitoAlvaraInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data de Vencimento do Débito do Alvará Inicial e Final");
            }
            if ((dataExpHabiteseInicial != null && dataExpHabiteseFinal == null) || (dataExpHabiteseFinal != null && dataExpHabiteseInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data de Expedição do Habite-se Inicial e Final");
            }
            if ((dataVencISSHabiteseInicial != null && dataVencISSHabiteseFinal == null) || (dataVencISSHabiteseFinal != null && dataVencISSHabiteseInicial == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Data de Vencimento do IIS do Habite-se Inicial e Final");
            }
            ve.lancarException();
        }
    }
}
