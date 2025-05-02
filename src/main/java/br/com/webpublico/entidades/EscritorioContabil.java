/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.EscritorioContabilDTO;
import br.com.webpublico.nfse.domain.dtos.EscritorioContabilNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("Escritório Contábil e/ou Contador")
public class EscritorioContabil implements Serializable, NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    private Long codigo;
    @Pesquisavel
    @ManyToOne
    private PessoaJuridica pessoa;
    @Etiqueta("Nome do Escritório")
    @Tabelavel
    @Transient
    private String nomeEscritorio;
    @Tabelavel
    @Etiqueta("CRC do Escritório Contábil")
    @Pesquisavel
    private String crcEscritorio;
    @ManyToOne
    @Pesquisavel
    private PessoaFisica responsavel;
    @Etiqueta("Contador")
    @Tabelavel
    @Transient
    private String nomeContador;
    @Tabelavel
    @Etiqueta("CRC do Contador")
    @Pesquisavel
    private String crc;
    @Transient
    private Long criadoEm;

    public EscritorioContabil(Long id, Long codigo, String crcEscritorio, String crc, String nomeEscritorio, String nomeContador) {
        this.id = id;
        this.codigo = codigo;
        this.crcEscritorio = crcEscritorio;
        this.crc = crc;
        this.nomeEscritorio = nomeEscritorio;
        this.nomeContador = nomeContador;
    }

    public EscritorioContabil() {
        this.criadoEm = System.nanoTime();
    }

    public EscritorioContabil(EscritorioContabilDTO escritorioContabil) {
        this.id = escritorioContabil.getId();
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica pessoa) {
        this.responsavel = pessoa;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public PessoaJuridica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaJuridica pessoa) {
        this.pessoa = pessoa;
    }

    public String getCrcEscritorio() {
        return crcEscritorio;
    }

    public void setCrcEscritorio(String crcEscritorio) {
        this.crcEscritorio = crcEscritorio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNomeEscritorio() {
        return nomeEscritorio;
    }

    public void setNomeEscritorio(String nomeEscritorio) {
        this.nomeEscritorio = nomeEscritorio;
    }

    public String getNomeContador() {
        return nomeContador;
    }

    public void setNomeContador(String nomeContador) {
        this.nomeContador = nomeContador;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        if (pessoa != null) {
            return pessoa.getNomeCpfCnpj();
        } else if (responsavel != null) {
            return responsavel.getNomeCpfCnpj();
        }
        return "";
    }

    @Override
    public EscritorioContabilNfseDTO toNfseDto() {
        EscritorioContabilNfseDTO dto = new EscritorioContabilNfseDTO();
        dto.setId(this.id);
        dto.setCodigo(this.codigo);
        dto.setCrcEscritorio(this.crcEscritorio);
        dto.setCrcContador(this.crc);
        if (this.pessoa != null)
            dto.setDadosPessoais(this.pessoa.toNfseDto().getDadosPessoais());
        if (this.responsavel != null)
            dto.setResponsavel(this.responsavel.toNfseDto().getDadosPessoais());
        return dto;
    }
}
