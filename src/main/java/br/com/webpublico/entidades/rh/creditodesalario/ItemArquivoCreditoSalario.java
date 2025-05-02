package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Etiqueta(value = "Item Arquivo Crédito de Salário")
public class ItemArquivoCreditoSalario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemCreditoSalario itemCreditoSalario;
    @OneToOne
    private Arquivo arquivo;
    @Enumerated(value = EnumType.STRING)
    private TipoArquivoCreditoSalario tipoArquivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCreditoSalario getItemCreditoSalario() {
        return itemCreditoSalario;
    }

    public void setItemCreditoSalario(ItemCreditoSalario itemCreditoSalario) {
        this.itemCreditoSalario = itemCreditoSalario;
    }

    public TipoArquivoCreditoSalario getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoCreditoSalario tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
