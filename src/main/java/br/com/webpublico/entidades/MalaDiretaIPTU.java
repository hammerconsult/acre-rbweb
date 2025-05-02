package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.tributario.TipoEnderecoExportacaoIPTU;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 07/06/2016.
 */
@Entity
@Audited
@Etiqueta("Mala Direta de IPTU")
public class MalaDiretaIPTU extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Exercício")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Inscrição Inicial")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String inscricaoInicial;
    @Etiqueta("Inscrição Final")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String inscricaoFinal;
    @Positivo
    @Etiqueta("Valor Inicial")
    private BigDecimal valorInicial;
    @Positivo
    @Etiqueta("Valor Final")
    private BigDecimal valorFinal;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Imóvel")
    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;
    @Etiqueta("Tipo de Cobrança")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoCobrancaTributario tipoCobranca;
    @Etiqueta("Tipo de Endereço")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoEnderecoExportacaoIPTU tipoEndereco;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Bairro")
    @ManyToOne
    private Bairro bairro;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Contribuinte")
    @ManyToOne
    private Pessoa pessoa;
    @OneToMany(mappedBy = "malaDiretaIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CadastroMalaDiretaIPTU> cadastroMalaDiretaIPTU;
    @Etiqueta("Texto da Mala Direta")
    private String texto;

    public MalaDiretaIPTU() {
        valorInicial = BigDecimal.ZERO;
        valorFinal = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public TipoCobrancaTributario getTipoCobranca() {
        return tipoCobranca;
    }

    public void setTipoCobranca(TipoCobrancaTributario tipoCobranca) {
        this.tipoCobranca = tipoCobranca;
    }

    public List<CadastroMalaDiretaIPTU> getCadastroMalaDiretaIPTU() {
        return cadastroMalaDiretaIPTU;
    }

    public void setCadastroMalaDiretaIPTU(List<CadastroMalaDiretaIPTU> cadastroMalaDiretaIPTU) {
        this.cadastroMalaDiretaIPTU = cadastroMalaDiretaIPTU;
    }

    public BigDecimal getValorInicial() {
        return valorInicial != null ? valorInicial : BigDecimal.ZERO;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal != null ? valorFinal : BigDecimal.ZERO;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TipoEnderecoExportacaoIPTU getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEnderecoExportacaoIPTU tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    @Override
    public String toString() {
        return exercicio.getAno() + " (" + inscricaoInicial + " - " + inscricaoFinal + ")";
    }
}
