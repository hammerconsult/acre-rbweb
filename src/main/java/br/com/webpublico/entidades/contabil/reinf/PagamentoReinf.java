package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class PagamentoReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pagamento pagamento;
    @ManyToOne
    private RegistroEventoRetencaoReinf registro;

    public PagamentoReinf() {

    }

    public PagamentoReinf(Pagamento pagamento, RegistroEventoRetencaoReinf registro) {
        this.pagamento = pagamento;
        this.registro = registro;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public RegistroEventoRetencaoReinf getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroEventoRetencaoReinf registro) {
        this.registro = registro;
    }
}
