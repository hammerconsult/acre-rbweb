package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 05/04/2016.
 */
@Entity
@Audited
@Etiqueta("Configuração Arquivo Obn600")
public class ConfiguracaoArquivoObn extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Configuração")
    @Tabelavel
    @Pesquisavel
    @OneToMany(mappedBy = "configuracaoArquivoObn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BancoObn> listaBancosObn;


    public ConfiguracaoArquivoObn() {
        this.listaBancosObn = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BancoObn> getListaBancosObn() {
        return listaBancosObn;
    }

    public void setListaBancosObn(List<BancoObn> listaBancosObn) {
        this.listaBancosObn = listaBancosObn;
    }

}
