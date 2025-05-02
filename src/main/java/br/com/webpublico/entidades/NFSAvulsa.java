package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemNotaAvulsa;
import br.com.webpublico.enums.TipoTomadorPrestadorNF;
import br.com.webpublico.nfse.domain.dtos.NFSAvulsaItemNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NFSAvulsaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Numerico;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Gustavo
 */
@Entity
@Audited
@Etiqueta("Nota Fiscal Avulsa")
public class NFSAvulsa implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Exercício")
    @ManyToOne
    @Tabelavel(ordemApresentacao = 1)
    private Exercicio exercicio;
    @Etiqueta("Número")
    @Tabelavel(ordemApresentacao = 2)
    private Long numero;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data de Emissão")
    @Tabelavel(ordemApresentacao = 3)
    private Date emissao;
    @ManyToOne
    private Pessoa prestador;
    @Etiqueta("Prestador")
    @Tabelavel(ordemApresentacao = 5)
    @Pesquisavel
    @Transient
    private String prestadorParaLista;
    @ManyToOne
    private Pessoa tomador;
    @Pesquisavel
    @Etiqueta("Tomador")
    @Tabelavel(ordemApresentacao = 6)
    @Transient
    private String tomadorParaLista;
    @Tabelavel(ordemApresentacao = 8)
    @Numerico
    @Etiqueta("Total do ISS (R$)")
    private BigDecimal valorTotalIss;
    @Tabelavel(ordemApresentacao = 7)
    @Numerico
    @Etiqueta("Total da Nota (R$)")
    private BigDecimal valorServicos;
    @Tabelavel(ordemApresentacao = 9)
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação da Nota Fiscal")
    private Situacao situacao;
    @ManyToOne
    private CadastroEconomico cmcPrestador;
    @ManyToOne
    private CadastroEconomico cmcTomador;
    @Etiqueta("Percentual de ISS (%)")
    private BigDecimal valorIss;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 4)
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date dataNota;
    @Transient
    private Long criadoEm;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "NFSAvulsa")
    private List<NFSAvulsaItem> itens;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @OneToOne(cascade = CascadeType.ALL)
    private NFSAvulsa nFSAvulsa;
    @Transient
    private String autenticidade;
    @Enumerated(EnumType.STRING)
    private TipoTomadorPrestadorNF tipoPrestadorNF;
    @Enumerated(EnumType.STRING)
    private TipoTomadorPrestadorNF tipoTomadorNF;
    private String motivo;
    @ManyToOne
    @Tabelavel(ordemApresentacao = 10)
    @Etiqueta("Usuário da Emissão")
    private UsuarioSistema usuario;
    @Enumerated(EnumType.STRING)
    private OrigemNotaAvulsa origemNotaAvulsa;

    public NFSAvulsa() {
        dataNota = new Date();
        valorIss = BigDecimal.ZERO;
        valorServicos = BigDecimal.ZERO;
        valorTotalIss = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
        itens = new ArrayList<>();
        situacao = Situacao.ABERTA;
        origemNotaAvulsa = OrigemNotaAvulsa.WEBPUBLICO;
    }

    public NFSAvulsa(Long id, Pessoa prestador, Pessoa tomador, NFSAvulsa nFSAvulsa) {
        this.setId(id);
        this.prestador = prestador;
        this.tomador = tomador;
        this.exercicio = nFSAvulsa.getExercicio();
        this.numero = nFSAvulsa.getNumero();
        this.emissao = nFSAvulsa.getEmissao();
        this.situacao = nFSAvulsa.getSituacao();
        this.valorServicos = nFSAvulsa.getValorServicos();
        this.valorTotalIss = nFSAvulsa.getValorTotalIss();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTomadorPrestadorNF getTipoPrestadorNF() {
        return tipoPrestadorNF;
    }

    public void setTipoPrestadorNF(TipoTomadorPrestadorNF tipoPrestadorNF) {
        this.tipoPrestadorNF = tipoPrestadorNF;
    }

    public TipoTomadorPrestadorNF getTipoTomadorNF() {
        return tipoTomadorNF;
    }

    public void setTipoTomadorNF(TipoTomadorPrestadorNF tipoTomadorNF) {
        this.tipoTomadorNF = tipoTomadorNF;
    }


    public String getTomadorParaLista() {
        return tomadorParaLista;
    }

    public void setTomadorParaLista(String tomadorParaLista) {
        this.tomadorParaLista = tomadorParaLista;
    }

    public String getPrestadorParaLista() {
        return prestadorParaLista;
    }

    public void setPrestadorParaLista(String prestadorParaLista) {
        this.prestadorParaLista = prestadorParaLista;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public CadastroEconomico getCmcPrestador() {
        return cmcPrestador;
    }

    public void setCmcPrestador(CadastroEconomico cmcPrestador) {
        this.cmcPrestador = cmcPrestador;
    }

    public CadastroEconomico getCmcTomador() {
        return cmcTomador;
    }

    public void setCmcTomador(CadastroEconomico cmcTomador) {
        this.cmcTomador = cmcTomador;
    }

    public List<NFSAvulsaItem> getItens() {
        return itens;
    }

    public void setItens(List<NFSAvulsaItem> itens) {
        this.itens = itens;
    }

    public void addItem(NFSAvulsaItem item) {
        this.itens.add(item);
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Pessoa getPrestador() {
        return prestador;
    }

    public void setPrestador(Pessoa prestador) {
        this.prestador = prestador;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Pessoa getTomador() {
        return tomador;
    }

    public void setTomador(Pessoa tomador) {
        this.tomador = tomador;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public BigDecimal getValorServicos() {
        return valorServicos;
    }

    public void setValorServicos(BigDecimal valorServicos) {
        this.valorServicos = valorServicos;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorTotalIss() {
        return valorTotalIss;
    }

    public void setValorTotalIss(BigDecimal valorTotalIss) {
        this.valorTotalIss = valorTotalIss;
    }

    public Date getDataNota() {
        return dataNota;
    }

    public void setDataNota(Date dataNota) {
        this.dataNota = dataNota;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public NFSAvulsa getNFSAvulsa() {
        return nFSAvulsa;
    }

    public void setNFSAvulsa(NFSAvulsa nFSAvulsa) {
        this.nFSAvulsa = nFSAvulsa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroCompleto() {
        String completo = numero.toString();
        while (completo.length() < 8) {
            completo = "0" + completo;
        }
        return completo;
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
        return numero + "";
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public OrigemNotaAvulsa getOrigemNotaAvulsa() {
        return origemNotaAvulsa;
    }

    public void setOrigemNotaAvulsa(OrigemNotaAvulsa origemNotaAvulsa) {
        this.origemNotaAvulsa = origemNotaAvulsa;
    }

    public enum Situacao {

        ABERTA("Aberta"),
        EMITIDA("Emitida"),
        CONCLUIDA("Concluída"),
        CANCELADA("Cancelada"),
        ALTERADA("Alterada");
        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @Override
    public NfseDTO toNfseDto() {
        NFSAvulsaNfseDTO nfsAvulsaNfseDTO = new NFSAvulsaNfseDTO();
        nfsAvulsaNfseDTO.setId(id);
        nfsAvulsaNfseDTO.setNumero(numero);
        nfsAvulsaNfseDTO.setDataNota(dataNota);
        nfsAvulsaNfseDTO.setPrestador(prestador.toNfseDto());
        nfsAvulsaNfseDTO.setTomador(tomador.toNfseDto());
        nfsAvulsaNfseDTO.setItens(new ArrayList());
        if (itens != null) {
            for (NFSAvulsaItem item : itens) {
                nfsAvulsaNfseDTO.getItens().add((NFSAvulsaItemNfseDTO) item.toNfseDto());
            }
        }
        return nfsAvulsaNfseDTO;
    }
}
