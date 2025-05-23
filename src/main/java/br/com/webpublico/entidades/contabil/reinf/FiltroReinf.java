package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.reinf.eventos.domain.EventoR2098;
import br.com.webpublico.reinf.eventos.domain.EventoR2099;
import br.com.webpublico.reinf.eventos.domain.EventoR4099;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class FiltroReinf {

    private UsuarioSistema usuarioSistema;
    private Entidade empregador;
    private Boolean apenasNaoEnviados;
    private Boolean exclusaoR1000;
    private Date dataInicial;
    private Date dataFinal;

    private Mes mes;

    private Exercicio exercicio;
    private SituacaoESocial situacao;

    private Boolean utilizarVersao2_1;

    private List<HierarquiaOrganizacional> hierarquias;
    private List<ConfiguracaoEmpregadorESocial> itemEmpregadorR1000;
    private List<ProcessoAdministrativoJudicial> itemEmpregadorR1070;
    private List<RegistroEventoRetencaoReinf> itemEmpregadorR2010;
    private List<RegistroEventoRetencaoReinf> itemEmpregadorR2020;
    private List<RegistroEventoRetencaoReinf> itemEmpregadorR4020;
    private EventoR2098 eventoR2098;
    private EventoR2099 eventoR2099;
    private EventoR4099 eventoR4099;
    private RegistroEventoExclusaoReinf eventoR9000;
    private Integer codigoFechamentoReabertura;

    public FiltroReinf() {
        itemEmpregadorR1000 = Lists.newArrayList();
        itemEmpregadorR1070 = Lists.newArrayList();
        itemEmpregadorR2010 = Lists.newArrayList();
        itemEmpregadorR2020 = Lists.newArrayList();
        itemEmpregadorR4020 = Lists.newArrayList();
        codigoFechamentoReabertura = 0;
    }

    public Entidade getEmpregador() {
        return empregador;
    }

    public void setEmpregador(Entidade empregador) {
        this.empregador = empregador;
    }

    public Boolean getApenasNaoEnviados() {
        return apenasNaoEnviados;
    }

    public void setApenasNaoEnviados(Boolean apenasNaoEnviados) {
        this.apenasNaoEnviados = apenasNaoEnviados;
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

    public List<ConfiguracaoEmpregadorESocial> getItemEmpregadorR1000() {
        return itemEmpregadorR1000;
    }

    public void setItemEmpregadorR1000(List<ConfiguracaoEmpregadorESocial> itemEmpregadorR1000) {
        this.itemEmpregadorR1000 = itemEmpregadorR1000;
    }

    public List<ProcessoAdministrativoJudicial> getItemEmpregadorR1070() {
        return itemEmpregadorR1070;
    }

    public void setItemEmpregadorR1070(List<ProcessoAdministrativoJudicial> itemEmpregadorR1070) {
        this.itemEmpregadorR1070 = itemEmpregadorR1070;
    }

    public SituacaoESocial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoESocial situacao) {
        this.situacao = situacao;
    }

    public List<RegistroEventoRetencaoReinf> getItemEmpregadorR2010() {
        return itemEmpregadorR2010;
    }

    public void setItemEmpregadorR2010(List<RegistroEventoRetencaoReinf> itemEmpregadorR2010) {
        this.itemEmpregadorR2010 = itemEmpregadorR2010;
    }

    public List<RegistroEventoRetencaoReinf> getItemEmpregadorR2020() {
        return itemEmpregadorR2020;
    }

    public void setItemEmpregadorR2020(List<RegistroEventoRetencaoReinf> itemEmpregadorR2020) {
        this.itemEmpregadorR2020 = itemEmpregadorR2020;
    }

    public List<RegistroEventoRetencaoReinf> getItemEmpregadorR4020() {
        return itemEmpregadorR4020;
    }

    public void setItemEmpregadorR4020(List<RegistroEventoRetencaoReinf> itemEmpregadorR4020) {
        this.itemEmpregadorR4020 = itemEmpregadorR4020;
    }

    public Date getPrimeiroDiaDoMesAno() {
        return DataUtil.getPrimeiroDiaMes(exercicio.getAno(), mes.getNumeroMesIniciandoEmZero());
    }

    public Date getUltimoDiaDoMesAno() {
        return DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), exercicio.getAno()).toDate();
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public EventoR2098 getEventoR2098() {
        return eventoR2098;
    }

    public void setEventoR2098(EventoR2098 eventoR2098) {
        this.eventoR2098 = eventoR2098;
    }

    public EventoR2099 getEventoR2099() {
        return eventoR2099;
    }

    public void setEventoR2099(EventoR2099 eventoR2099) {
        this.eventoR2099 = eventoR2099;
    }

    public EventoR4099 getEventoR4099() {
        return eventoR4099;
    }

    public void setEventoR4099(EventoR4099 eventoR4099) {
        this.eventoR4099 = eventoR4099;
    }

    public RegistroEventoExclusaoReinf getEventoR9000() {
        return eventoR9000;
    }

    public void setEventoR9000(RegistroEventoExclusaoReinf eventoR9000) {
        this.eventoR9000 = eventoR9000;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public Integer getCodigoFechamentoReabertura() {
        return codigoFechamentoReabertura;
    }

    public void setCodigoFechamentoReabertura(Integer codigoFechamentoReabertura) {
        this.codigoFechamentoReabertura = codigoFechamentoReabertura;
    }

    public Boolean getUtilizarVersao2_1() {
        if (utilizarVersao2_1 == null) {
            utilizarVersao2_1 = Boolean.TRUE;
        }
        return utilizarVersao2_1;
    }

    public void setUtilizarVersao2_1(Boolean utilizarVersao2_1) {
        this.utilizarVersao2_1 = utilizarVersao2_1;
    }

    public Boolean getExclusaoR1000() {
        if (exclusaoR1000 == null) {
            exclusaoR1000 = false;
        }
        return exclusaoR1000;
    }

    public void setExclusaoR1000(Boolean exclusaoR1000) {
        this.exclusaoR1000 = exclusaoR1000;
    }

    public Integer getQuantidadeR2010() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR2010) {
            if (registroEventoRetencaoReinf.getMarcarPraEnviar()) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeJaEnviadasR2010() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR2010) {
            if (registroEventoRetencaoReinf.getId() != null) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeR2020() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR2020) {
            if (registroEventoRetencaoReinf.getMarcarPraEnviar()) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeJaEnviadasR2020() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR2020) {
            if (registroEventoRetencaoReinf.getId() != null) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeR4020() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR4020) {
            if (registroEventoRetencaoReinf.getMarcarPraEnviar()) {
                retorno++;
            }
        }
        return retorno;
    }

    public Integer getQuantidadeJaEnviadasR4020() {
        Integer retorno = 0;
        for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : itemEmpregadorR4020) {
            if (registroEventoRetencaoReinf.getId() != null) {
                retorno++;
            }
        }
        return retorno;
    }
}
