/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Situacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.BancoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.pessoa.dto.BancoDTO;
import br.com.webpublico.repositorios.jdbc.chavenegocio.ChaveNegocioBanco;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "Bancario")
@Entity

@Audited
public class Banco extends SuperEntidadeComChaveNegocio implements NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Column(length = 70)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Column(length = 70)
    @Etiqueta("ISPB")
    private String ispb;
    @Pesquisavel
    @Tabelavel
    @Column(unique = true)
    @Obrigatorio
    @Etiqueta("Número do Banco")
    private String numeroBanco;
    @Pesquisavel
//    @Tabelavel
    @Etiqueta("Dígito do Banco")
    private String digitoVerificador;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Home Page")
    private String homePage;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número do Contrato")
    private String numeroContrato;
    @ManyToOne
    private PessoaJuridica pessoaJuridica;

    //Construtor padrão
    public Banco() {
        super();
        situacao = Situacao.ATIVO;
    }

    public Banco(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Banco(String descricao, String numeroBanco, String digitoVerificador, String homePage) {
        this.descricao = descricao;
        this.numeroBanco = numeroBanco;
        this.digitoVerificador = digitoVerificador;
        this.homePage = homePage;
        situacao = Situacao.ATIVO;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public String getIspb() {
        return ispb;
    }

    public void setIspb(String ispb) {
        this.ispb = ispb;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    //    @Override
//    public String toString() {
//        String toString = "";
//
//        if (numeroBanco != null) {
//            toString += numeroBanco + " - ";
//        }
//        if (descricao != null) {
//            toString += descricao + " - ";
//        }
//        if (!toString.isEmpty()) {
//            return toString.substring(0, toString.length() - 2);
//        }
//        return toString;
//    }

    @Override
    public String toString() {
        if (numeroBanco != null && digitoVerificador != null && descricao != null) {
            return numeroBanco + "-" + digitoVerificador + " - " + descricao;
        } else if (numeroBanco != null && descricao != null) {
            return numeroBanco + " - " + descricao;
        }
        return "";
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public static List<BancoDTO> toBancosDTO(List<Banco> bancos) {
        if (bancos == null) {
            return null;
        }
        List<BancoDTO> dtos = Lists.newLinkedList();
        for (Banco banco : bancos) {
            BancoDTO dto = toBancoDTO(banco);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static BancoDTO toBancoDTO(Banco banco) {
        if (banco == null) {
            return null;
        }
        BancoDTO dto = new BancoDTO(banco.getId(), banco.getDescricao(), banco.getNumeroBanco());
        return dto;

    }

    public static Banco dtoToBanco(BancoDTO bancoPisPasep) {
        Banco b = new Banco();
        b.setId(bancoPisPasep.getId());
        return b;
    }

    @Override
    public BancoNfseDTO toNfseDto() {
        return new BancoNfseDTO(id, descricao, numeroBanco, digitoVerificador, homePage, numeroContrato);
    }

    @Override
    public ChaveNegocio getChaveNegocio() {
        return new ChaveNegocioBanco(numeroBanco);
    }
}
