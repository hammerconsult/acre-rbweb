package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 01/07/14
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ArquivoComposicao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @ManyToOne
    private UsuarioSistema usuario;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUpload;

    @Transient
    private Object file;


    public ArquivoComposicao() {
        super();
    }

    public static ArquivoComposicao clonar(ArquivoComposicao arquivoComposicao, DetentorArquivoComposicao detentorArquivoComposicao) {
        ArquivoComposicao clone = new ArquivoComposicao();
        clone.setCriadoEm(arquivoComposicao.getCriadoEm());
        clone.setArquivo(Arquivo.clonar(arquivoComposicao.getArquivo()));
        clone.setDataUpload(arquivoComposicao.getDataUpload());
        clone.setDetentorArquivoComposicao(detentorArquivoComposicao);
        clone.setFile(arquivoComposicao.getFile());
        clone.setUsuario(arquivoComposicao.getUsuario());
        return clone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Date getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(Date dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }
}
