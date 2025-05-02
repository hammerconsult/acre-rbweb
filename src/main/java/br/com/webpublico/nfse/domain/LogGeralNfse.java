package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Etiqueta("Log Nfs-e")
public class LogGeralNfse extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String conteudo;
    @Tabelavel
    @Etiqueta("Conteúdo")
    private String usuario;
    @Tabelavel
    @Etiqueta("Local")
    private String local;
    @Tabelavel
    @Etiqueta("IP")
    private String ip;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data")
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    private Long idRelacionamento;
    @Transient
    @Tabelavel
    @Etiqueta("Nota Fiscal")
    private NotaFiscal notaFiscal;

    public LogGeralNfse() {
    }

    public LogGeralNfse(String usuario, String local, String ip, Date dataRegistro, NotaFiscal nota) {
        this.usuario = usuario;
        this.local = local;
        this.ip = ip;
        this.dataRegistro = dataRegistro;
        this.notaFiscal = nota;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Long getIdRelacionamento() {
        return idRelacionamento;
    }

    public void setIdRelacionamento(Long idRelacionamento) {
        this.idRelacionamento = idRelacionamento;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public enum Tipo {
        IMPRESSAO_NFSE("Impressão de Nfse");

        private String descricao;

        Tipo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
