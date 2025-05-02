package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabio
 */

@GrupoDiagrama(nome = "Tributário")
@Entity

@Audited
@Etiqueta(value = "Tipo de Denúncia")
public class TipoDenuncia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta(value = "Sequência")
    @Tabelavel
    private Long codigo;
    @Pesquisavel
    @Etiqueta(value = "Secretaria")
    @Tabelavel
    @ManyToOne
    private SecretariaFiscalizacao secretariaFiscalizacao;
    @Etiqueta(value = "Descrição")
    @Tabelavel
    private String descricao;
    @Etiqueta(value = "Descrição Detalhada")
    private String descricaoDetalhada;

    public TipoDenuncia() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

}
