package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zaca on 24/02/17.
 */
public class ComprovanteRendimentosCedula implements Serializable {
    private String fontePagadora;
    private String fonteCpf;
    private String beneficiarioCpf;
    private String beneficiarioNome;
    private BigDecimal totalRendimentos;
    private BigDecimal decimoTerceiro;
    private Integer anoExercicio;
    private Integer anoCalendario;
    private String responsavelInformacao;
    private String brasao;
    private String naturezaRendimento;
    private VinculoFP instituidor;
    private List<EventoFP> eventosFps;


    public ComprovanteRendimentosCedula(String fontePagadora, String fonteCpf, String beneficiarioCpf, String beneficiarioNome, BigDecimal totalRendimentos, BigDecimal decimoTerceiro, Integer anoExercicio, Integer anoCalendario, String responsavelInformacao, String brasao, String naturezaRendimento) {
        this.fontePagadora = fontePagadora;
        this.fonteCpf = fonteCpf;
        this.beneficiarioCpf = beneficiarioCpf;
        this.beneficiarioNome = beneficiarioNome;
        this.totalRendimentos = totalRendimentos;
        this.decimoTerceiro = decimoTerceiro;
        this.anoExercicio = anoExercicio;
        this.anoCalendario = anoCalendario;
        this.responsavelInformacao = responsavelInformacao;
        this.brasao = brasao;
        this.naturezaRendimento = naturezaRendimento;
    }

    public ComprovanteRendimentosCedula() {
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

    public BigDecimal getTotalRendimentos() {
        return totalRendimentos;
    }

    public void setTotalRendimentos(BigDecimal totalRendimentos) {
        this.totalRendimentos = totalRendimentos;
    }

    public BigDecimal getDecimoTerceiro() {
        return decimoTerceiro;
    }

    public void setDecimoTerceiro(BigDecimal decimoTerceiro) {
        this.decimoTerceiro = decimoTerceiro;
    }

    public String getResponsavelInformacao() {
        return responsavelInformacao;
    }

    public void setResponsavelInformacao(String responsavelInformacao) {
        this.responsavelInformacao = responsavelInformacao;
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

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    public String getNaturezaRendimento() {
        return naturezaRendimento;
    }

    public void setNaturezaRendimento(String naturezaRendimento) {
        this.naturezaRendimento = naturezaRendimento;
    }

    public VinculoFP getInstituidor() {
        return instituidor;
    }

    public void setInstituidor(VinculoFP instituidor) {
        this.instituidor = instituidor;
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

        ComprovanteRendimentosCedula that = (ComprovanteRendimentosCedula) o;

        if (fontePagadora != null ? !fontePagadora.equals(that.fontePagadora) : that.fontePagadora != null)
            return false;
        if (fonteCpf != null ? !fonteCpf.equals(that.fonteCpf) : that.fonteCpf != null) return false;
        if (beneficiarioCpf != null ? !beneficiarioCpf.equals(that.beneficiarioCpf) : that.beneficiarioCpf != null)
            return false;
        if (beneficiarioNome != null ? !beneficiarioNome.equals(that.beneficiarioNome) : that.beneficiarioNome != null)
            return false;
        if (totalRendimentos != null ? !totalRendimentos.equals(that.totalRendimentos) : that.totalRendimentos != null)
            return false;
        return decimoTerceiro != null ? decimoTerceiro.equals(that.decimoTerceiro) : that.decimoTerceiro == null;

    }

    @Override
    public int hashCode() {
        int result = fontePagadora != null ? fontePagadora.hashCode() : 0;
        result = 31 * result + (fonteCpf != null ? fonteCpf.hashCode() : 0);
        result = 31 * result + (beneficiarioCpf != null ? beneficiarioCpf.hashCode() : 0);
        result = 31 * result + (beneficiarioNome != null ? beneficiarioNome.hashCode() : 0);
        result = 31 * result + (totalRendimentos != null ? totalRendimentos.hashCode() : 0);
        result = 31 * result + (decimoTerceiro != null ? decimoTerceiro.hashCode() : 0);
        return result;
    }
}
