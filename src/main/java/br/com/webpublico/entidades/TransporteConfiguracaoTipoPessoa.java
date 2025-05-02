package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.enums.TipoPessoaPermitido;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import javax.persistence.*;

@GrupoDiagrama(nome = "Contabil")
@Entity
@Table(name = "TRANSPORTECONFIGTIPOPESSOA")
@Etiqueta("Transporte Configuração Tipo Viagem")
public class TransporteConfiguracaoTipoPessoa extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Configuração")
    private TipoConfiguracaoContabil tipoConfiguracaoContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transporte Configuração Contábil")
    private TransporteConfiguracaoContabil transporteConfigContabil;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Pessoa")
    @Obrigatorio
    private TipoPessoaPermitido tipoPessoa;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoTipoPessoa() {
        super();
    }

    public TransporteConfiguracaoTipoPessoa(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, TipoPessoaPermitido tipoPessoa, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.tipoPessoa = tipoPessoa;
        this.log = log;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoConfiguracaoContabil getTipoConfiguracaoContabil() {
        return tipoConfiguracaoContabil;
    }

    public void setTipoConfiguracaoContabil(TipoConfiguracaoContabil tipoConfiguracaoContabil) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
    }

    public TransporteConfiguracaoContabil getTransporteConfigContabil() {
        return transporteConfigContabil;
    }

    public void setTransporteConfigContabil(TransporteConfiguracaoContabil transporteConfigContabil) {
        this.transporteConfigContabil = transporteConfigContabil;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public TipoPessoaPermitido getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaPermitido tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    @Override
    public String toString() {
        try {
            return tipoPessoa.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }
}
