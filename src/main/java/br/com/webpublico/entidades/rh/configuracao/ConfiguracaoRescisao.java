package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Desenvolvimento on 10/03/2016.
 */
@Entity
@Audited
public class ConfiguracaoRescisao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Controle da data final de vigência do vinculoFp ")
    private Boolean controleVigenciaFinalViculoFP;

    public ConfiguracaoRescisao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getControleVigenciaFinalViculoFP() {
        return controleVigenciaFinalViculoFP;
    }

    public void setControleVigenciaFinalViculoFP(Boolean controleVigenciaFinalViculoFP) {
        this.controleVigenciaFinalViculoFP = controleVigenciaFinalViculoFP;
    }

    @Override
    public String toString() {
        if(controleVigenciaFinalViculoFP){
            return "O contrato será finalizado um dia anterior a data de rescisão";
        }
        return "O contrato será resciso na mesma data de rescisão";
    }
}
