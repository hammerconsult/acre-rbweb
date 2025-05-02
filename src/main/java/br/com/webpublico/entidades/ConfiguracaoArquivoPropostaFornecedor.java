package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 11/03/15
 * Time: 18:01
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Proposta do Fornecedor")
@Table(name = "CONFIGARQUIVOPROPOSTAFORN")
public class ConfiguracaoArquivoPropostaFornecedor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Sistema")
    @Pesquisavel
    private Arquivo arquivo;
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Manual")
    @Pesquisavel
    private Arquivo manual;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Versão")
    @Pesquisavel
    private Integer versao;

    public ConfiguracaoArquivoPropostaFornecedor() {
        versao = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Arquivo getManual() {
        return manual;
    }

    public void setManual(Arquivo manual) {
        this.manual = manual;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}
