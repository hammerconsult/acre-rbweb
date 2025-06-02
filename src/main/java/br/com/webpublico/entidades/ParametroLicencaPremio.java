package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta("Parametrização da Licença Prêmio")
public class ParametroLicencaPremio implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigoLicenca;
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade de Licença Prêmios que pode Acumular")
    @Pesquisavel
    private Integer quantidadeAcumulo;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Dias de Gozo da Licença Prêmio")
    @Pesquisavel
    private Integer diasGozo;
    @Tabelavel
    @Etiqueta("Licença Prêmio é Remunerada?")
    @Pesquisavel
    private Boolean remunerada;
    @ManyToOne
    @Tabelavel
    @Etiqueta("EventoFP")
    @Pesquisavel
    private EventoFP eventoFP;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Número de Parcelamento da Licença Prêmio")
    private Integer parcela;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta("Quantidade Mínima de Dias por Parcela")
    private Integer quantidadeMinimaDiasPorParcela;
    @Etiqueta("Dias de Faltas Injustificadas")
    private Integer diasFaltaInjustificada;
    @Etiqueta("Dias de Licença Prêmio Retardados por Faltas Injustificadas")
    private Integer diasRetardo;
    @Etiqueta("Dias de Afastamento por Interesse Particular")
    private Integer diasAfastInterParticular;
    @Etiqueta("Dias de Licença Prêmio Retardados por Afastamento de Interesse Particular")
    private Integer diasRetardoAfas;
    @Etiqueta("Dias de Licença por Motivo de Doença")
    private Integer diasLicencaMotivoDoenca;
    @Etiqueta("Dias de Licença Prêmio Retardados por Licença por Motivo de Doença")
    private Integer diasRetardoLic;
    @Etiqueta("Dias de Suspensão Disciplinar")
    private Integer diasSuspensaoDisciplinar;
    @Etiqueta("Dias de Licença Prêmio Retardados por Suspensão Disciplinar")
    private Integer diasRetardoSusp;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Dias para Licença Prêmio")
    private Integer periodoParaLicenca;
    @ManyToOne
    private ReferenciaFP referenciaFP;
    private Boolean adiarConcecaoUmMesPorFalta;
    @Etiqueta("Quantidade de Dias para Perda do Período Aquisitivo")
    private Integer quantidadeDiasPerdaPeriodo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Integer getCodigoLicenca() {
        return codigoLicenca;
    }

    public void setCodigoLicenca(Integer codigoLicenca) {
        this.codigoLicenca = codigoLicenca;
    }

    public Integer getQuantidadeAcumulo() {
        return quantidadeAcumulo;
    }

    public void setQuantidadeAcumulo(Integer quantidadeAcumulo) {
        this.quantidadeAcumulo = quantidadeAcumulo;
    }

    public Integer getDiasGozo() {
        return diasGozo;
    }

    public void setDiasGozo(Integer diasGozo) {
        this.diasGozo = diasGozo;
    }

    public Boolean getRemunerada() {
        return remunerada;
    }

    public void setRemunerada(Boolean remunerada) {
        this.remunerada = remunerada;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Integer getParcela() {
        return parcela;
    }

    public void setParcela(Integer parcela) {
        this.parcela = parcela;
    }

    public Integer getQuantidadeMinimaDiasPorParcela() {
        return quantidadeMinimaDiasPorParcela;
    }

    public void setQuantidadeMinimaDiasPorParcela(Integer quantidadeMinimaDiasPorParcela) {
        this.quantidadeMinimaDiasPorParcela = quantidadeMinimaDiasPorParcela;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public Integer getDiasFaltaInjustificada() {
        return diasFaltaInjustificada;
    }

    public void setDiasFaltaInjustificada(Integer diasFaltaInjustificada) {
        this.diasFaltaInjustificada = diasFaltaInjustificada;
    }

    public Integer getDiasAfastInterParticular() {
        return diasAfastInterParticular;
    }

    public void setDiasAfastInterParticular(Integer diasAfastInterParticular) {
        this.diasAfastInterParticular = diasAfastInterParticular;
    }

    public Integer getDiasLicencaMotivoDoenca() {
        return diasLicencaMotivoDoenca;
    }

    public void setDiasLicencaMotivoDoenca(Integer diasLicencaMotivoDoenca) {
        this.diasLicencaMotivoDoenca = diasLicencaMotivoDoenca;
    }

    public Integer getDiasSuspensaoDisciplinar() {
        return diasSuspensaoDisciplinar;
    }

    public void setDiasSuspensaoDisciplinar(Integer diasSuspensaoDisciplinar) {
        this.diasSuspensaoDisciplinar = diasSuspensaoDisciplinar;
    }

    public Integer getDiasRetardo() {
        return diasRetardo;
    }

    public void setDiasRetardo(Integer diasRetardo) {
        this.diasRetardo = diasRetardo;
    }

    public Integer getDiasRetardoAfas() {
        return diasRetardoAfas;
    }

    public void setDiasRetardoAfas(Integer diasRetardoAfas) {
        this.diasRetardoAfas = diasRetardoAfas;
    }

    public Integer getDiasRetardoLic() {
        return diasRetardoLic;
    }

    public void setDiasRetardoLic(Integer diasRetardoLic) {
        this.diasRetardoLic = diasRetardoLic;
    }

    public Integer getDiasRetardoSusp() {
        return diasRetardoSusp;
    }

    public void setDiasRetardoSusp(Integer diasRetardoSusp) {
        this.diasRetardoSusp = diasRetardoSusp;
    }

    public Integer getPeriodoParaLicenca() {
        return periodoParaLicenca;
    }

    public void setPeriodoParaLicenca(Integer periodoParaLicenca) {
        this.periodoParaLicenca = periodoParaLicenca;
    }

    public Boolean getAdiarConcecaoUmMesPorFalta() {
        return adiarConcecaoUmMesPorFalta == null ? Boolean.FALSE : adiarConcecaoUmMesPorFalta;
    }

    public void setAdiarConcecaoUmMesPorFalta(Boolean adiarConcecaoUmMesPorFalta) {
        this.adiarConcecaoUmMesPorFalta = adiarConcecaoUmMesPorFalta;
    }

    public Integer getQuantidadeDiasPerdaPeriodo() {
        return quantidadeDiasPerdaPeriodo;
    }

    public void setQuantidadeDiasPerdaPeriodo(Integer quantidadeDiasPerdaPeriodo) {
        this.quantidadeDiasPerdaPeriodo = quantidadeDiasPerdaPeriodo;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        String toString = "";

        toString += "Código: " + this.getCodigoLicenca().toString() + " - Início: " + DataUtil.getDataFormatada(this.getInicioVigencia());
        if (this.getFinalVigencia() != null) {
            toString += " - Fim: " + DataUtil.getDataFormatada(this.getFinalVigencia());
        }
        if (this.getParcela() != null) {
            toString += " Parcelas: " + this.getParcela().toString();
        }

        return toString;
    }
}
