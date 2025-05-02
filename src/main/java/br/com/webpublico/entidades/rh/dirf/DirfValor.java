package br.com.webpublico.entidades.rh.dirf;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.dirf.DirfTipoValor;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class DirfValor extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DirfPessoa dirfPessoa;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Valor")
    private DirfTipoValor dirfTipoValor;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Etiqueta("Valor")
    private BigDecimal valor;

    public DirfValor() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DirfPessoa getDirfPessoa() {
        return dirfPessoa;
    }

    public void setDirfPessoa(DirfPessoa dirfPessoa) {
        this.dirfPessoa = dirfPessoa;
    }

    public DirfTipoValor getDirfTipoValor() {
        return dirfTipoValor;
    }

    public void setDirfTipoValor(DirfTipoValor dirfTipoValor) {
        this.dirfTipoValor = dirfTipoValor;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
