package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Folha")
public class IntegracaoRHContabilFolha extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @ManyToOne
    private IntegracaoRHContabil integracaoRHContabil;

    public IntegracaoRHContabilFolha() {
    }

    public IntegracaoRHContabilFolha(FolhaDePagamento folhaDePagamento, IntegracaoRHContabil integracaoRHContabil) {
        this.folhaDePagamento = folhaDePagamento;
        this.integracaoRHContabil = integracaoRHContabil;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }
}

