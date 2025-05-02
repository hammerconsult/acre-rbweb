/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.LayoutArquivoBordero;
import br.com.webpublico.enums.TipoFaturaArquivoRemessa;
import br.com.webpublico.enums.TipoPagamentoArquivoRemessa;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

/**
 *
 * @author wiplash
 */
@Entity
@Cacheable
@Etiqueta("Layout Arquivo de Remessa")
@Audited
public class LayoutArquivoRemessa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Nome Layout")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String nomeLayout;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'HEADER'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> headerArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TIPO_1'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> registroUmArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TIPO_2'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> registroDoisArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TIPO_3'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> registroTresArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TIPO_4'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> registroQuatroArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TIPO_5'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> registroCincoArquivoRemessas;
    @OneToMany(mappedBy = "layoutArquivoRemessa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRegistroArquivoRemessa = 'TRAILER'")
    @OrderBy(clause = "posicaoInicial")
    private List<RegistroArquivoRemessa> trailerArquivoRemessas;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Layout do Arquivo")
    @Tabelavel
    private LayoutArquivoBordero layoutArquivoBordero;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Fatura")
    private TipoFaturaArquivoRemessa tipoFaturaArquivoRemessa;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Pagamento")
    private TipoPagamentoArquivoRemessa tipoPagamentoArquivoRemessa;
    @Etiqueta("Número")
    private BigInteger numero;
    @Transient
    private Long criadoEm;

    public LayoutArquivoRemessa() {
        criadoEm = System.nanoTime();
        headerArquivoRemessas = new ArrayList<>();
        registroUmArquivoRemessas = new ArrayList<>();
        registroDoisArquivoRemessas = new ArrayList<>();
        registroTresArquivoRemessas = new ArrayList<>();
        registroQuatroArquivoRemessas = new ArrayList<>();
        registroCincoArquivoRemessas = new ArrayList<>();
        trailerArquivoRemessas = new ArrayList<>();
        numero = new BigInteger("0");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLayout() {
        return nomeLayout;
    }

    public void setNomeLayout(String nomeLayout) {
        this.nomeLayout = nomeLayout;
    }

    public List<RegistroArquivoRemessa> getHeaderArquivoRemessas() {
        return headerArquivoRemessas;
    }

    public void setHeaderArquivoRemessas(List<RegistroArquivoRemessa> headerArquivoRemessas) {
        this.headerArquivoRemessas = headerArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getRegistroUmArquivoRemessas() {
        return registroUmArquivoRemessas;
    }

    public void setRegistroUmArquivoRemessas(List<RegistroArquivoRemessa> registroUmArquivoRemessas) {
        this.registroUmArquivoRemessas = registroUmArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getRegistroDoisArquivoRemessas() {
        return registroDoisArquivoRemessas;
    }

    public void setRegistroDoisArquivoRemessas(List<RegistroArquivoRemessa> registroDoisArquivoRemessas) {
        this.registroDoisArquivoRemessas = registroDoisArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getRegistroTresArquivoRemessas() {
        return registroTresArquivoRemessas;
    }

    public void setRegistroTresArquivoRemessas(List<RegistroArquivoRemessa> registroTresArquivoRemessas) {
        this.registroTresArquivoRemessas = registroTresArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getRegistroQuatroArquivoRemessas() {
        return registroQuatroArquivoRemessas;
    }

    public void setRegistroQuatroArquivoRemessas(List<RegistroArquivoRemessa> registroQuatroArquivoRemessas) {
        this.registroQuatroArquivoRemessas = registroQuatroArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getRegistroCincoArquivoRemessas() {
        return registroCincoArquivoRemessas;
    }

    public void setRegistroCincoArquivoRemessas(List<RegistroArquivoRemessa> registroCincoArquivoRemessas) {
        this.registroCincoArquivoRemessas = registroCincoArquivoRemessas;
    }

    public List<RegistroArquivoRemessa> getTrailerArquivoRemessas() {
        return trailerArquivoRemessas;
    }

    public void setTrailerArquivoRemessas(List<RegistroArquivoRemessa> trailerArquivoRemessas) {
        this.trailerArquivoRemessas = trailerArquivoRemessas;
    }

    public LayoutArquivoBordero getLayoutArquivoBordero() {
        return layoutArquivoBordero;
    }

    public void setLayoutArquivoBordero(LayoutArquivoBordero layoutArquivoBordero) {
        this.layoutArquivoBordero = layoutArquivoBordero;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigInteger getNumero() {
        return numero;
    }

    public void setNumero(BigInteger numero) {
        this.numero = numero;
    }

    public TipoFaturaArquivoRemessa getTipoFaturaArquivoRemessa() {
        return tipoFaturaArquivoRemessa;
    }

    public void setTipoFaturaArquivoRemessa(TipoFaturaArquivoRemessa tipoFaturaArquivoRemessa) {
        this.tipoFaturaArquivoRemessa = tipoFaturaArquivoRemessa;
    }

    public TipoPagamentoArquivoRemessa getTipoPagamentoArquivoRemessa() {
        return tipoPagamentoArquivoRemessa;
    }

    public void setTipoPagamentoArquivoRemessa(TipoPagamentoArquivoRemessa tipoPagamentoArquivoRemessa) {
        this.tipoPagamentoArquivoRemessa = tipoPagamentoArquivoRemessa;
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
