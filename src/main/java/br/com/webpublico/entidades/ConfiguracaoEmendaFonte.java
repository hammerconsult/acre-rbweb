package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class ConfiguracaoEmendaFonte extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fonte de Recursos")
    private Conta contaDeDestinacao;
    @Obrigatorio
    @Etiqueta("Configuração da Emenda")
    @ManyToOne
    private ConfiguracaoEmenda configuracaoEmenda;

    public ConfiguracaoEmendaFonte() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public ContaDeDestinacao getContaAsContaDeDestinacao() {
        return (ContaDeDestinacao) contaDeDestinacao;
    }

    public ConfiguracaoEmenda getConfiguracaoEmenda() {
        return configuracaoEmenda;
    }

    public void setConfiguracaoEmenda(ConfiguracaoEmenda configuracaoEmenda) {
        this.configuracaoEmenda = configuracaoEmenda;
    }
}
