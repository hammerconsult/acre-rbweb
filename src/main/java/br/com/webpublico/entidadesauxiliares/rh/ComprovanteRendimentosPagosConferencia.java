package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by zaca on 23/02/17.
 */
public class ComprovanteRendimentosPagosConferencia implements Serializable {
    private String fontePagadora;
    private String fonteCpf;
    private String beneficiarioCpf;
    private String beneficiarioNome;
    private Integer anoExercicio;
    private Integer anoCalendario;
    private String responsavelInformacao;
    private String brasao;
    private VinculoFP instituidor;
    private String naturezaRendimento;
    private BigDecimal totalTributaveis;
    private BigDecimal totalIsentos;
    private List<EventoFP> eventosFps;
    private List<ComprovanteRendimentosTributaveis> rendimentosTributaveis;
    private List<ComprovanteRendimentosIsentos> rendimentosIsentos;

    public ComprovanteRendimentosPagosConferencia(String fontePagadora, String fonteCpf, String beneficiarioCpf, String beneficiarioNome, Integer anoExercicio, Integer anoCalendario, String responsavelInformacao, String brasao, VinculoFP instituidor, String naturezaRendimento, BigDecimal totalTributaveis, BigDecimal totalIsentos, List<EventoFP> eventosFps, List<ComprovanteRendimentosTributaveis> rendimentosTributaveis, List<ComprovanteRendimentosIsentos> rendimentosIsentos) {
        this.fontePagadora = fontePagadora;
        this.fonteCpf = fonteCpf;
        this.beneficiarioCpf = beneficiarioCpf;
        this.beneficiarioNome = beneficiarioNome;
        this.anoExercicio = anoExercicio;
        this.anoCalendario = anoCalendario;
        this.responsavelInformacao = responsavelInformacao;
        this.brasao = brasao;
        this.instituidor = instituidor;
        this.naturezaRendimento = naturezaRendimento;
        this.totalTributaveis = totalTributaveis;
        this.totalIsentos = totalIsentos;
        this.eventosFps = eventosFps;
        this.rendimentosTributaveis = rendimentosTributaveis;
        this.rendimentosIsentos = rendimentosIsentos;
    }

    public ComprovanteRendimentosPagosConferencia() {
        this.eventosFps = Lists.newArrayList();
    }

    public String getFontePagadora() {
        return fontePagadora;
    }

    public void setFontePagadora(String fontePagadora) {
        this.fontePagadora = fontePagadora;
    }

    public String getFonteCpf() {
        return fonteCpf;
    }

    public void setFonteCpf(String fonteCpf) {
        this.fonteCpf = fonteCpf;
    }

    public String getBeneficiarioCpf() {
        return beneficiarioCpf;
    }

    public void setBeneficiarioCpf(String beneficiarioCpf) {
        this.beneficiarioCpf = beneficiarioCpf;
    }

    public String getBeneficiarioNome() {
        return beneficiarioNome;
    }

    public void setBeneficiarioNome(String beneficiarioNome) {
        this.beneficiarioNome = beneficiarioNome;
    }

    public Integer getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Integer anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public Integer getAnoCalendario() {
        return anoCalendario;
    }

    public void setAnoCalendario(Integer anoCalendario) {
        this.anoCalendario = anoCalendario;
    }

    public String getResponsavelInformacao() {
        return responsavelInformacao;
    }

    public void setResponsavelInformacao(String responsavelInformacao) {
        this.responsavelInformacao = responsavelInformacao;
    }

    public List<ComprovanteRendimentosTributaveis> getRendimentosTributaveis() {
        return rendimentosTributaveis;
    }

    public void setRendimentosTributaveis(List<ComprovanteRendimentosTributaveis> rendimentosTributaveis) {
        this.rendimentosTributaveis = rendimentosTributaveis;
    }

    public List<ComprovanteRendimentosIsentos> getRendimentosIsentos() {
        return rendimentosIsentos;
    }

    public void setRendimentosIsentos(List<ComprovanteRendimentosIsentos> rendimentosIsentos) {
        this.rendimentosIsentos = rendimentosIsentos;
    }

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    public VinculoFP getInstituidor() {
        return instituidor;
    }

    public void setInstituidor(VinculoFP instituidor) {
        this.instituidor = instituidor;
    }

    public BigDecimal getTotalTributaveis() {
        return totalTributaveis;
    }

    public String getNaturezaRendimento() {
        return naturezaRendimento;
    }

    public void setNaturezaRendimento(String naturezaRendimento) {
        this.naturezaRendimento = naturezaRendimento;
    }

    public BigDecimal getTotalIsentos() {
        return totalIsentos;
    }

    public void setTotalIsentos(BigDecimal totalIsentos) {
        this.totalIsentos = totalIsentos;
    }

    public void setTotalTributaveis(BigDecimal totalTributaveis) {
        this.totalTributaveis = totalTributaveis;
    }

    public List<EventoFP> getEventosFps() {
        return eventosFps;
    }

    public void setEventosFps(List<EventoFP> eventosFps) {
        this.eventosFps = eventosFps;
    }

    public void adicionarEvento(EventoFP evento) {
        if (this.getEventosFps() == null) {
            this.eventosFps = Lists.newArrayList();
        }

        if (!this.getEventosFps().contains(evento)) {
            this.getEventosFps().add(evento);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComprovanteRendimentosPagosConferencia that = (ComprovanteRendimentosPagosConferencia) o;

        if (fontePagadora != null ? !fontePagadora.equals(that.fontePagadora) : that.fontePagadora != null)
            return false;
        if (fonteCpf != null ? !fonteCpf.equals(that.fonteCpf) : that.fonteCpf != null) return false;
        if (beneficiarioCpf != null ? !beneficiarioCpf.equals(that.beneficiarioCpf) : that.beneficiarioCpf != null)
            return false;
        if (beneficiarioNome != null ? !beneficiarioNome.equals(that.beneficiarioNome) : that.beneficiarioNome != null)
            return false;
        if (anoExercicio != null ? !anoExercicio.equals(that.anoExercicio) : that.anoExercicio != null) return false;
        if (anoCalendario != null ? !anoCalendario.equals(that.anoCalendario) : that.anoCalendario != null)
            return false;
        if (responsavelInformacao != null ? !responsavelInformacao.equals(that.responsavelInformacao) : that.responsavelInformacao != null)
            return false;
        if (rendimentosTributaveis != null ? !rendimentosTributaveis.equals(that.rendimentosTributaveis) : that.rendimentosTributaveis != null)
            return false;
        return rendimentosIsentos != null ? rendimentosIsentos.equals(that.rendimentosIsentos) : that.rendimentosIsentos == null;

    }

    @Override
    public int hashCode() {
        int result = fontePagadora != null ? fontePagadora.hashCode() : 0;
        result = 31 * result + (fonteCpf != null ? fonteCpf.hashCode() : 0);
        result = 31 * result + (beneficiarioCpf != null ? beneficiarioCpf.hashCode() : 0);
        result = 31 * result + (beneficiarioNome != null ? beneficiarioNome.hashCode() : 0);
        result = 31 * result + (anoExercicio != null ? anoExercicio.hashCode() : 0);
        result = 31 * result + (anoCalendario != null ? anoCalendario.hashCode() : 0);
        result = 31 * result + (responsavelInformacao != null ? responsavelInformacao.hashCode() : 0);
        result = 31 * result + (rendimentosTributaveis != null ? rendimentosTributaveis.hashCode() : 0);
        result = 31 * result + (rendimentosIsentos != null ? rendimentosIsentos.hashCode() : 0);
        return result;
    }
}
