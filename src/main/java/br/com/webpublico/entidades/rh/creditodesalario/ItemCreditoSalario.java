package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta(value = "Item Crédito de Salário")
public class ItemCreditoSalario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CreditoSalario creditoSalario;
    @ManyToOne
    private GrupoRecursoFP grupoRecursoFP;
    private Integer sequencial;
    private BigDecimal valorLiquido;

    private String identificador;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao arquivo;

    @NotAudited
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemCreditoSalario", orphanRemoval = true)
    private List<ItemArquivoCreditoSalario> arquivos;


    @OneToMany(mappedBy = "itemCreditoSalario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VinculoFPCreditoSalario> itensVinculoCreditoSalario;
    private String logDetalhado;
    private String erros;


    public ItemCreditoSalario() {
        arquivos = Lists.newLinkedList();
        itensVinculoCreditoSalario = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditoSalario getCreditoSalario() {
        return creditoSalario;
    }

    public void setCreditoSalario(CreditoSalario creditoSalario) {
        this.creditoSalario = creditoSalario;
    }

    public GrupoRecursoFP getGrupoRecursoFP() {
        return grupoRecursoFP;
    }

    public void setGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        this.grupoRecursoFP = grupoRecursoFP;
    }

    public List<ItemArquivoCreditoSalario> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ItemArquivoCreditoSalario> arquivos) {
        this.arquivos = arquivos;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public List<VinculoFPCreditoSalario> getItensVinculoCreditoSalario() {
        return itensVinculoCreditoSalario;
    }

    public void setItensVinculoCreditoSalario(List<VinculoFPCreditoSalario> itensVinculoCreditoSalario) {
        this.itensVinculoCreditoSalario = itensVinculoCreditoSalario;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public DetentorArquivoComposicao getArquivo() {
        return arquivo;
    }

    public void setArquivo(DetentorArquivoComposicao arquivo) {
        this.arquivo = arquivo;
    }

    public String getLogDetalhado() {
        return logDetalhado;
    }

    public void setLogDetalhado(String logDetalhado) {
        this.logDetalhado = logDetalhado;
    }

    public String getErros() {
        return erros;
    }

    public void setErros(String erros) {
        this.erros = erros;
    }
}
