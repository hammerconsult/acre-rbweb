package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoArquivo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 11/12/13
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Arquivo de retorno do holerite BB")
@Audited
@Entity
public class RetornoHoleriteBB implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String conteudoArquivo;
    @Temporal(TemporalType.DATE)
    private Date dataImportacao;
    @Enumerated(EnumType.STRING)
    private SituacaoArquivo situacaoArquivo;

    public RetornoHoleriteBB() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudoArquivo() {
        return conteudoArquivo;
    }

    public void setConteudoArquivo(String conteudoArquivo) {
        this.conteudoArquivo = conteudoArquivo;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public SituacaoArquivo getSituacaoArquivo() {
        return situacaoArquivo;
    }

    public void setSituacaoArquivo(SituacaoArquivo situacaoArquivo) {
        this.situacaoArquivo = situacaoArquivo;
    }
}
