package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Etiqueta("Configuração da Tabela de Classe Credor")
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class ConfiguracaoRHClasseCredor extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Etiqueta("Configuração")
    @ManyToOne
    private ConfiguracaoRH configuracaoRH;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe de Pessoa")
    private ClasseCredor classeCredor;

    public ConfiguracaoRHClasseCredor() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        return configuracaoRH;
    }

    public void setConfiguracaoRH(ConfiguracaoRH configuracaoRH) {
        this.configuracaoRH = configuracaoRH;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }
}
