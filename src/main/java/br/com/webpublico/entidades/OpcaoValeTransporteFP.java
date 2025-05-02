/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Vale Transporte")
public class OpcaoValeTransporteFP extends SuperEntidade implements Serializable, ValidadorEntidade, ValidadorVigencia, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Contrato da Folha de Pagamento")
    @ManyToOne
    @Obrigatorio
    private ContratoFP contratoFP;
    @Tabelavel
    @Etiqueta("Inicio da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    @Pesquisavel
    private Date finalVigencia;
    private Boolean optante;
    @Tabelavel
    @Etiqueta("Vales Por Dia")
    @Obrigatorio
    @Pesquisavel
    @Positivo(permiteZero = false)
    private Integer valesPorDia;
    @Etiqueta("Observação")
    @Length(maximo = 255)
    private String observacao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Quantidade")
    @Positivo(permiteZero = false)
    private Integer quantidade;
    @Tabelavel
    @Etiqueta("Quantidade Complementar")
    private Integer complementoQuantidade;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private OpcaoValeTransporteFPHist opcaoValeTransporteFPHist;

    public OpcaoValeTransporteFP() {
        quantidade = 44;
        complementoQuantidade = 0;
    }

    public Integer getComplementoQuantidade() {
        return complementoQuantidade;
    }

    public void setComplementoQuantidade(Integer complementoQuantidade) {
        this.complementoQuantidade = complementoQuantidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        OpcaoValeTransporteFPHist historico = temHistorico() ? this.opcaoValeTransporteFPHist : new OpcaoValeTransporteFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.opcaoValeTransporteFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(opcaoValeTransporteFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, OpcaoValeTransporteFPHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public Boolean getOptante() {
        return optante;
    }

    public void setOptante(Boolean optante) {
        this.optante = optante;
    }

    public Integer getValesPorDia() {
        return valesPorDia;
    }

    public void setValesPorDia(Integer valesPorDia) {
        this.valesPorDia = valesPorDia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public OpcaoValeTransporteFPHist getOpcaoValeTransporteFPHist() {
        return opcaoValeTransporteFPHist;
    }

    public void setOpcaoValeTransporteFPHist(OpcaoValeTransporteFPHist opcaoValeTransporteFPHist) {
        this.opcaoValeTransporteFPHist = opcaoValeTransporteFPHist;
    }

    @Override
    public String toString() {
        return "Opção do Vale Transporte de " + contratoFP.getMatriculaFP().getPessoa().getNome();
    }

    @Override
    public int compareTo(Object o) {
        return this.getInicioVigencia().compareTo(((OpcaoValeTransporteFP) o).getInicioVigencia());
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (getOptante() && getValesPorDia() == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Ao selecionar a opção 'Optante' é obrigatório informar o número de vales por dia.");
        }

        if (getFinalVigencia() != null && getFinalVigencia().compareTo(getInicioVigencia()) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de final de vigência deve ser posterior a data de início de vigência.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean temHistorico() {
        return this.opcaoValeTransporteFPHist != null;
    }
}
