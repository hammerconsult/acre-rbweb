package br.com.webpublico.entidades.rh.esocial;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import com.beust.jcommander.internal.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class LogErroEnvioEvento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoArquivoESocial tipoArquivoESocial;

    private Long identificador;

    private String classe;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLog;

    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "logErroEnvioEvento")
    private List<DetalheLogErroEnvioEvento> itemDetalheLog;


    public LogErroEnvioEvento() {
        itemDetalheLog = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoArquivoESocial getTipoArquivoESocial() {
        return tipoArquivoESocial;
    }

    public void setTipoArquivoESocial(TipoArquivoESocial tipoArquivoESocial) {
        this.tipoArquivoESocial = tipoArquivoESocial;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }


    public List<DetalheLogErroEnvioEvento> getItemDetalheLog() {
        return itemDetalheLog;
    }

    public void setItemDetalheLog(List<DetalheLogErroEnvioEvento> itemDetalheLog) {
        this.itemDetalheLog = itemDetalheLog;
    }

    public Date getDataLog() {
        return dataLog;
    }

    public void setDataLog(Date dataLog) {
        this.dataLog = dataLog;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }
}
