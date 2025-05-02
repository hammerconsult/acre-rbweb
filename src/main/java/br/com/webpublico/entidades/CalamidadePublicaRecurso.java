package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAtoLegalUnidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Calamidade Pública/Recurso")
public class CalamidadePublicaRecurso extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalamidadePublica calamidadePublica;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Unidade")
    @Obrigatorio
    private TipoAtoLegalUnidade tipo;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Unidade Externa")
    private UnidadeExterna unidadeExterna;
    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    @Obrigatorio
    private FonteDeRecursos fonteDeRecursos;
    @Etiqueta("Valor (R$)")
    @Obrigatorio
    @Monetario
    private BigDecimal valor;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    private Date dataRecebimento;
    private String banco;
    private String agencia;
    private String contaCorrente;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Transferência")
    @Obrigatorio
    private Date dataTransferencia;
    @Etiqueta("Valor (R$)")
    @Monetario
    private BigDecimal valorDisponibilizado;
    private BigDecimal valorBloqueado;
    private String tipoConta;


    public CalamidadePublicaRecurso() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalamidadePublica getCalamidadePublica() {
        return calamidadePublica;
    }

    public void setCalamidadePublica(CalamidadePublica calamidadePublica) {
        this.calamidadePublica = calamidadePublica;
    }

    public TipoAtoLegalUnidade getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtoLegalUnidade tipo) {
        this.tipo = tipo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public Boolean isInterna(){
        return TipoAtoLegalUnidade.INTERNA.equals(this.getTipo());
    }

    public String getUnidadeAsString(){
        if(isInterna()){
            return unidadeOrganizacional.getDescricao();
        }
        return unidadeExterna.getPessoaJuridica().getRazaoSocial();
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public BigDecimal getValorDisponibilizado() {
        return valorDisponibilizado;
    }

    public void setValorDisponibilizado(BigDecimal valorDisponibilizado) {
        this.valorDisponibilizado = valorDisponibilizado;
    }

    public BigDecimal getValorBloqueado() {
        return valorBloqueado;
    }

    public void setValorBloqueado(BigDecimal valorBloqueado) {
        this.valorBloqueado = valorBloqueado;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }
}
