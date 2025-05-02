package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Fornecedor do Contrato")
public class FornecedorContrato extends SuperEntidade implements Comparable<FornecedorContrato> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Etiqueta("Fornecedor")
    private Pessoa fornecedor;

    @ManyToOne
    @Etiqueta("Responsável Fornecedor")
    private PessoaFisica responsavelFornecedor;

    private Integer ordem;

    public FornecedorContrato() {
        super();
        ordem = 0;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public PessoaFisica getResponsavelFornecedor() {
        return responsavelFornecedor;
    }

    public void setResponsavelFornecedor(PessoaFisica responsavelFornecedor) {
        this.responsavelFornecedor = responsavelFornecedor;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    @Override
    public int compareTo(FornecedorContrato o) {
        return ComparisonChain.start().compare(ordem, o.getOrdem()).result();
    }
}
