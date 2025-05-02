/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author wiplash
 */
@Entity
@Cacheable
@Etiqueta("Registro Arquivo de Remessa")
@Audited
public class RegistroArquivoRemessa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer posicaoInicial;
    private Integer tamanho;
    private Integer posicaoFinal;
    private String tipo;
    @ManyToOne
    private LayoutArquivoRemessa layoutArquivoRemessa;
    @Enumerated(EnumType.STRING)
    private TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Header tipoRegistroOBN600Header;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Tipo1 tipoRegistroOBN600Tipo1;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Tipo2 tipoRegistroOBN600Tipo2;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Tipo3 tipoRegistroOBN600Tipo3;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Tipo4 tipoRegistroOBN600Tipo4;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Tipo5 tipoRegistroOBN600Tipo5;
    @Enumerated(EnumType.STRING)
    private TipoRegistroOBN600Trailer tipoRegistroOBN600Trailer;
    @Transient
    private Long criadoEm;

    public RegistroArquivoRemessa() {
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Header tipoRegistroOBN600Header) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Header = tipoRegistroOBN600Header;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Tipo1 tipoRegistroOBN600Tipo1) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Tipo1 = tipoRegistroOBN600Tipo1;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Tipo2 tipoRegistroOBN600Tipo2) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Tipo2 = tipoRegistroOBN600Tipo2;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Tipo3 tipoRegistroOBN600Tipo3) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Tipo3 = tipoRegistroOBN600Tipo3;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Tipo4 tipoRegistroOBN600Tipo4) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Tipo4 = tipoRegistroOBN600Tipo4;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Tipo5 tipoRegistroOBN600Tipo5) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Tipo5 = tipoRegistroOBN600Tipo5;
        criadoEm = System.nanoTime();
    }

    public RegistroArquivoRemessa(Integer posicaoInicial, Integer tamanho, Integer posicaoFinal, String tipo, LayoutArquivoRemessa layoutArquivoRemessa, TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa, TipoRegistroOBN600Trailer tipoRegistroOBN600Trailer) {
        this.posicaoInicial = posicaoInicial;
        this.tamanho = tamanho;
        this.posicaoFinal = posicaoFinal;
        this.tipo = tipo;
        this.layoutArquivoRemessa = layoutArquivoRemessa;
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
        this.tipoRegistroOBN600Trailer = tipoRegistroOBN600Trailer;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosicaoInicial() {
        return posicaoInicial;
    }

    public void setPosicaoInicial(Integer posicaoInicial) {
        this.posicaoInicial = posicaoInicial;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getPosicaoFinal() {
        return posicaoFinal;
    }

    public void setPosicaoFinal(Integer posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LayoutArquivoRemessa getLayoutArquivoRemessa() {
        return layoutArquivoRemessa;
    }

    public void setLayoutArquivoRemessa(LayoutArquivoRemessa layoutArquivoRemessa) {
        this.layoutArquivoRemessa = layoutArquivoRemessa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoRegistroArquivoRemessa getTipoRegistroArquivoRemessa() {
        return tipoRegistroArquivoRemessa;
    }

    public void setTipoRegistroArquivoRemessa(TipoRegistroArquivoRemessa tipoRegistroArquivoRemessa) {
        this.tipoRegistroArquivoRemessa = tipoRegistroArquivoRemessa;
    }

    public TipoRegistroOBN600Header getTipoRegistroOBN600Header() {
        return tipoRegistroOBN600Header;
    }

    public void setTipoRegistroOBN600Header(TipoRegistroOBN600Header tipoRegistroOBN600Header) {
        this.tipoRegistroOBN600Header = tipoRegistroOBN600Header;
    }

    public TipoRegistroOBN600Tipo1 getTipoRegistroOBN600Tipo1() {
        return tipoRegistroOBN600Tipo1;
    }

    public void setTipoRegistroOBN600Tipo1(TipoRegistroOBN600Tipo1 tipoRegistroOBN600Tipo1) {
        this.tipoRegistroOBN600Tipo1 = tipoRegistroOBN600Tipo1;
    }

    public TipoRegistroOBN600Tipo2 getTipoRegistroOBN600Tipo2() {
        return tipoRegistroOBN600Tipo2;
    }

    public void setTipoRegistroOBN600Tipo2(TipoRegistroOBN600Tipo2 tipoRegistroOBN600Tipo2) {
        this.tipoRegistroOBN600Tipo2 = tipoRegistroOBN600Tipo2;
    }

    public TipoRegistroOBN600Tipo3 getTipoRegistroOBN600Tipo3() {
        return tipoRegistroOBN600Tipo3;
    }

    public void setTipoRegistroOBN600Tipo3(TipoRegistroOBN600Tipo3 tipoRegistroOBN600Tipo3) {
        this.tipoRegistroOBN600Tipo3 = tipoRegistroOBN600Tipo3;
    }

    public TipoRegistroOBN600Tipo4 getTipoRegistroOBN600Tipo4() {
        return tipoRegistroOBN600Tipo4;
    }

    public void setTipoRegistroOBN600Tipo4(TipoRegistroOBN600Tipo4 tipoRegistroOBN600Tipo4) {
        this.tipoRegistroOBN600Tipo4 = tipoRegistroOBN600Tipo4;
    }

    public TipoRegistroOBN600Tipo5 getTipoRegistroOBN600Tipo5() {
        return tipoRegistroOBN600Tipo5;
    }

    public void setTipoRegistroOBN600Tipo5(TipoRegistroOBN600Tipo5 tipoRegistroOBN600Tipo5) {
        this.tipoRegistroOBN600Tipo5 = tipoRegistroOBN600Tipo5;
    }

    public TipoRegistroOBN600Trailer getTipoRegistroOBN600Trailer() {
        return tipoRegistroOBN600Trailer;
    }

    public void setTipoRegistroOBN600Trailer(TipoRegistroOBN600Trailer tipoRegistroOBN600Trailer) {
        this.tipoRegistroOBN600Trailer = tipoRegistroOBN600Trailer;
    }

    public TipoRegistroArquivoRemessa getTipoUtilizado() {
        if (this.tipoRegistroOBN600Header != null) {
            return TipoRegistroArquivoRemessa.HEADER;
        } else if (this.tipoRegistroOBN600Tipo1 != null) {
            return TipoRegistroArquivoRemessa.TIPO_1;
        } else if (this.tipoRegistroOBN600Tipo2 != null) {
            return TipoRegistroArquivoRemessa.TIPO_2;
        } else if (this.tipoRegistroOBN600Tipo3 != null) {
            return TipoRegistroArquivoRemessa.TIPO_3;
        } else if (this.tipoRegistroOBN600Tipo4 != null) {
            return TipoRegistroArquivoRemessa.TIPO_4;
        } else if (this.tipoRegistroOBN600Tipo5 != null) {
            return TipoRegistroArquivoRemessa.TIPO_5;
        } else if (this.tipoRegistroOBN600Trailer != null) {
            return TipoRegistroArquivoRemessa.TRAILER;
        }
        return null;
    }

    public String getDescricaoTipoUtilizado() {
        if (this.tipoRegistroOBN600Header != null) {
            return this.tipoRegistroOBN600Header.getDescricao();
        } else if (this.tipoRegistroOBN600Tipo1 != null) {
            return this.tipoRegistroOBN600Tipo1.getDescricao();
        } else if (this.tipoRegistroOBN600Tipo2 != null) {
            return this.tipoRegistroOBN600Tipo2.getDescricao();
        } else if (this.tipoRegistroOBN600Tipo3 != null) {
            return this.tipoRegistroOBN600Tipo3.getDescricao();
        } else if (this.tipoRegistroOBN600Tipo4 != null) {
            return this.tipoRegistroOBN600Tipo4.getDescricao();
        } else if (this.tipoRegistroOBN600Tipo5 != null) {
            return this.tipoRegistroOBN600Tipo5.getDescricao();
        } else if (this.tipoRegistroOBN600Trailer != null) {
            return this.tipoRegistroOBN600Trailer.getDescricao();
        }
        return "";
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
