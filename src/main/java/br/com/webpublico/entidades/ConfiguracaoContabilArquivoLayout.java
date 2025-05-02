package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoConvenioArquivoMensal;
import br.com.webpublico.enums.TipoExtensaoArquivo;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "CONFIGCARQUIVOLAYOUT")
public class ConfiguracaoContabilArquivoLayout extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private ConfiguracaoContabil configuracaoContabil;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoConvenioArquivoMensal tipo;
    @Obrigatorio
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    private Integer numeroAba;
    private Integer numeroLinhaInicial;
    private Boolean ajustarTamanhoColuna;

    public ConfiguracaoContabilArquivoLayout() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public TipoConvenioArquivoMensal getTipo() {
        return tipo;
    }

    public void setTipo(TipoConvenioArquivoMensal tipo) {
        this.tipo = tipo;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getNumeroAba() {
        return numeroAba;
    }

    public void setNumeroAba(Integer numeroAba) {
        this.numeroAba = numeroAba;
    }

    public Integer getNumeroLinhaInicial() {
        return numeroLinhaInicial;
    }

    public void setNumeroLinhaInicial(Integer numeroLinhaInicial) {
        this.numeroLinhaInicial = numeroLinhaInicial;
    }

    public Boolean getAjustarTamanhoColuna() {
        return ajustarTamanhoColuna == null ? Boolean.FALSE : ajustarTamanhoColuna;
    }

    public void setAjustarTamanhoColuna(Boolean ajustarTamanhoColuna) {
        this.ajustarTamanhoColuna = ajustarTamanhoColuna;
    }

    public boolean isArquivoXlsOrXlsx() {
        return arquivo != null && arquivo.getNome() != null && arquivo.getNome().toLowerCase().contains(ExcelUtil.XLS_EXTENCAO);
    }
}
