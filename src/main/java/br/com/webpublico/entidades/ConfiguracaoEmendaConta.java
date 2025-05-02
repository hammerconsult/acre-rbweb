package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ConfiguracaoEmendaConta extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;
    @Obrigatorio
    @Etiqueta("Configuração da Emenda")
    @ManyToOne
    private ConfiguracaoEmenda configuracaoEmenda;
    @Enumerated(EnumType.STRING)
    private ModalidadeEmenda modalidadeEmenda;

    public ConfiguracaoEmendaConta() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ConfiguracaoEmenda getConfiguracaoEmenda() {
        return configuracaoEmenda;
    }

    public void setConfiguracaoEmenda(ConfiguracaoEmenda configuracaoEmenda) {
        this.configuracaoEmenda = configuracaoEmenda;
    }

    public ModalidadeEmenda getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(ModalidadeEmenda modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }
}
