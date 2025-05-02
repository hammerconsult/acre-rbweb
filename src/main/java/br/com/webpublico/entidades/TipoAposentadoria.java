/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Tipo de Aposentadoria")
public class TipoAposentadoria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    private String codigoCaixaAtuarial;
    @ManyToOne
    private MotivoExoneracaoRescisao motivoExoneracaoRescisao;
    public static final String TEMPO_DE_CONTRIBUICAO = "1"; // == tempo de serviço!
    public static final String COMPULSORIA = "2";
    public static final String INVALIDEZ = "3";
    public static final String IDADE = "4";
    public static final String POR_MORTE = "5";

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public boolean isTempoContribuicao() {
        return TipoAposentadoria.TEMPO_DE_CONTRIBUICAO.equals(codigo);
    }

    public boolean isCompulsoria() {
        return TipoAposentadoria.COMPULSORIA.equals(codigo);
    }

    public boolean isMorte() {
        return TipoAposentadoria.POR_MORTE.equals(codigo);
    }

    public boolean isInvalidez() {
        return TipoAposentadoria.INVALIDEZ.equals(codigo);
    }

    public boolean isPorIdade() {
        return TipoAposentadoria.IDADE.equals(codigo);
    }

    public String getCodigoCaixaAtuarial() {
        return codigoCaixaAtuarial;
    }

    public void setCodigoCaixaAtuarial(String codigoCaixaAtuarial) {
        this.codigoCaixaAtuarial = codigoCaixaAtuarial;
    }

    public MotivoExoneracaoRescisao getMotivoExoneracaoRescisao() {
        return motivoExoneracaoRescisao;
    }

    public void setMotivoExoneracaoRescisao(MotivoExoneracaoRescisao motivoExoneracaoRescisao) {
        this.motivoExoneracaoRescisao = motivoExoneracaoRescisao;
    }
}
