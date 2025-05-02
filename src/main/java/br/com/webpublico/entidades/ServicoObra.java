package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by venom on 10/09/14.
 */
@Entity
@Audited
@Etiqueta(value = "Serviços de Obras")
public class ServicoObra implements Serializable, Comparable<ServicoObra> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Nome")
    @Obrigatorio
    private String nome;
    @Invisivel
    @Etiqueta(value = "Serviço Superior")
    @ManyToOne
    private ServicoObra superior;
    @Invisivel
    @OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoObra> filhos;
    @Enumerated(EnumType.STRING)
    @Invisivel
    private TipoServicoObra tipoServicoObra;
    @Invisivel
    private Integer ordem;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ServicoObra() {
        this.criadoEm = System.nanoTime();
        this.filhos = new ArrayList<>();
        this.ordem = 0;
    }

    public ServicoObra(ServicoObra servico) {
        this.nome = servico.nome;
        this.superior = servico.superior;
        this.tipoServicoObra = TipoServicoObra.EXECUCAO;
        this.criadoEm = System.nanoTime();
        this.filhos = getNovosFilhos(servico);
    }

    private List<ServicoObra> getNovosFilhos(ServicoObra so) {
        List<ServicoObra> lista = new ArrayList<>();
        for (ServicoObra s : so.getFilhos()) {
            lista.add(new ServicoObra(s));
        }
        return lista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ServicoObra getSuperior() {
        return superior;
    }

    public void setSuperior(ServicoObra superior) {
        this.superior = superior;
    }

    public List<ServicoObra> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<ServicoObra> filhos) {
        this.filhos = filhos;
    }

    public TipoServicoObra getTipoServicoObra() {
        return tipoServicoObra;
    }

    public void setTipoServicoObra(TipoServicoObra tipoServicoObra) {
        this.tipoServicoObra = tipoServicoObra;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int compareTo(ServicoObra servicoObra) {
        if (this.ordem == 0) {
            return this.nome.compareTo(servicoObra.nome);
        }
        return this.ordem.compareTo(servicoObra.ordem);
    }
}
