/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Parâmetros de Fiscalização")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "PARAMFISCALIZACAORBTRANS")
public class ParametrosFiscalizacaoRBTrans extends ParametrosTransitoRBTrans {

    //ATRIBUTOS DA PRIMEIRA ABA
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Notificação de Autuação")
    private TipoDoctoOficial notificacaoDeAutuacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Ciência da Decisão do Recurso ao Infrator")
    @JoinColumn(name = "CIENCIADECISAORECUINFRATOR_ID")
    private TipoDoctoOficial cienciaDaDecisaoDoRecursoAoInfrator;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Ciência da Decisão do Recurso ao Infrator")
    @JoinColumn(name = "CIENCIADECISAORECJARI_ID")
    private TipoDoctoOficial cienciaDecisaoRecursoJARI;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Notificação de Penalidades")
    private TipoDoctoOficial notificacaoDePenalidades;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Solicitação de Cancelamento da Multa")
    private TipoDoctoOficial cancelamentoMulta;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Solicitação de Cancelamento da Multa")
    private TipoDoctoOficial processoCassacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Notificação de Preclusão do Processo")
    @JoinColumn(name = "NOTIFICAPRECLUSAOPROCESSO_ID")
    private TipoDoctoOficial notificacaoDePreclusaoDoProcesso;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Notificação de Restituição de Pagamento")
    private TipoDoctoOficial notificacaoRestituicao;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação de Arquivamento do Processo")
    private TipoDoctoOficial solicitacaoArquivamento;
    //ATRIBUTOS DA SEGUNDA ABA
    @Obrigatorio
    @Etiqueta("Análise da Autuação")
    private Integer analiseDaAutuacao;
    @Obrigatorio
    @Etiqueta("Primeiro Recurso da Autuação")
    private Integer primeiroRecursoDaAutuacao;
    @Obrigatorio
    @Etiqueta("Prazo da Prefeitura de Resposta ao Recurso")
    @Column(name = "PRAZOPREFEITURARESPOSTARECURSO")
    private Integer prazoDaPrefeituraDeRespostaAoRecurso;
    @Obrigatorio
    @Etiqueta("Vencimento do DAM")
    private Integer vencimentoDoDAM;
    @Obrigatorio
    @Etiqueta("Prazo da Prefeitura para Entrega das Notificações")
    @Column(name = "PRZPREFEITURAENTRNOTIFICACOES")
    private Integer prazoPrefeituraEntregaNotificacoes;
    @Obrigatorio
    @Etiqueta("Prazo para Recurso – JARI")
    private Integer prazoParaRecursoJARI;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Débitos Referentes a fiscalização")
    private Divida divida;

    public ParametrosFiscalizacaoRBTrans() {
        setTipo("FISCALIZACAO");
    }

    public TipoDoctoOficial getCienciaDecisaoRecursoJARI() {
        return cienciaDecisaoRecursoJARI;
    }

    public void setCienciaDecisaoRecursoJARI(TipoDoctoOficial cienciaDecisaoRecursoJARI) {
        this.cienciaDecisaoRecursoJARI = cienciaDecisaoRecursoJARI;
    }

    public TipoDoctoOficial getNotificacaoRestituicao() {
        return notificacaoRestituicao;
    }

    public void setNotificacaoRestituicao(TipoDoctoOficial notificacaoRestituicao) {
        this.notificacaoRestituicao = notificacaoRestituicao;
    }

    public TipoDoctoOficial getCancelamentoMulta() {
        return cancelamentoMulta;
    }

    public void setCancelamentoMulta(TipoDoctoOficial cancelamentoMulta) {
        this.cancelamentoMulta = cancelamentoMulta;
    }

    public TipoDoctoOficial getProcessoCassacao() {
        return processoCassacao;
    }

    public void setProcessoCassacao(TipoDoctoOficial processoCassacao) {
        this.processoCassacao = processoCassacao;
    }

    public TipoDoctoOficial getNotificacaoDeAutuacao() {
        return notificacaoDeAutuacao;
    }

    public void setNotificacaoDeAutuacao(TipoDoctoOficial notificacaoDeAutuacao) {
        this.notificacaoDeAutuacao = notificacaoDeAutuacao;
    }

    public TipoDoctoOficial getCienciaDaDecisaoDoRecursoAoInfrator() {
        return cienciaDaDecisaoDoRecursoAoInfrator;
    }

    public void setCienciaDaDecisaoDoRecursoAoInfrator(TipoDoctoOficial cienciaDaDecisaoDoRecursoAoInfrator) {
        this.cienciaDaDecisaoDoRecursoAoInfrator = cienciaDaDecisaoDoRecursoAoInfrator;
    }

    public TipoDoctoOficial getNotificacaoDePenalidades() {
        return notificacaoDePenalidades;
    }

    public void setNotificacaoDePenalidades(TipoDoctoOficial notificacaoDePenalidades) {
        this.notificacaoDePenalidades = notificacaoDePenalidades;
    }

    public TipoDoctoOficial getNotificacaoDePreclusaoDoProcesso() {
        return notificacaoDePreclusaoDoProcesso;
    }

    public void setNotificacaoDePreclusaoDoProcesso(TipoDoctoOficial notificacaoDePreclusaoDoProcesso) {
        this.notificacaoDePreclusaoDoProcesso = notificacaoDePreclusaoDoProcesso;
    }

    public Integer getAnaliseDaAutuacao() {
        return analiseDaAutuacao;
    }

    public void setAnaliseDaAutuacao(Integer analiseDaAutuacao) {
        this.analiseDaAutuacao = analiseDaAutuacao;
    }

    public Integer getPrimeiroRecursoDaAutuacao() {
        return primeiroRecursoDaAutuacao;
    }

    public void setPrimeiroRecursoDaAutuacao(Integer primeiroRecursoDaAutuacao) {
        this.primeiroRecursoDaAutuacao = primeiroRecursoDaAutuacao;
    }

    public Integer getPrazoDaPrefeituraDeRespostaAoRecurso() {
        return prazoDaPrefeituraDeRespostaAoRecurso;
    }

    public void setPrazoDaPrefeituraDeRespostaAoRecurso(Integer prazoDaPrefeituraDeRespostaAoRecurso) {
        this.prazoDaPrefeituraDeRespostaAoRecurso = prazoDaPrefeituraDeRespostaAoRecurso;
    }

    public Integer getVencimentoDoDAM() {
        return vencimentoDoDAM;
    }

    public void setVencimentoDoDAM(Integer vencimentoDoDAM) {
        this.vencimentoDoDAM = vencimentoDoDAM;
    }

    public Integer getPrazoPrefeituraEntregaNotificacoes() {
        return prazoPrefeituraEntregaNotificacoes;
    }

    public void setPrazoPrefeituraEntregaNotificacoes(Integer prazoPrefeituraEntregaNotificacoes) {
        this.prazoPrefeituraEntregaNotificacoes = prazoPrefeituraEntregaNotificacoes;
    }

    public Integer getPrazoParaRecursoJARI() {
        return prazoParaRecursoJARI;
    }

    public void setPrazoParaRecursoJARI(Integer prazoParaRecursoJARI) {
        this.prazoParaRecursoJARI = prazoParaRecursoJARI;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public TipoDoctoOficial getSolicitacaoArquivamento() {
        return solicitacaoArquivamento;
    }

    public void setSolicitacaoArquivamento(TipoDoctoOficial solicitacaoArquivamento) {
        this.solicitacaoArquivamento = solicitacaoArquivamento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (super.getId() != null ? super.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParametrosFiscalizacaoRBTrans)) {
            return false;
        }
        ParametrosFiscalizacaoRBTrans other = (ParametrosFiscalizacaoRBTrans) object;
        if ((super.getId() == null && other.getId() != null) || (super.getId() != null && !super.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParamFiscalizacaoRBTrans[ id=" + super.getId() + " ]";
    }
}
