package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class CadastroEconomicoSubvencao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("C.M.C")
    private CadastroEconomico cadastroEconomico;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date vigenciaInicial;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date vigenciaFinal;
    @ManyToOne
    private SubvencaoParametro subvencaoParametro;
    @OneToMany(mappedBy = "empresaCredora", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpresaDevedoraSubvencao> empresaDevedoraSubvencao;

    public CadastroEconomicoSubvencao() {
        empresaDevedoraSubvencao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public SubvencaoParametro getSubvencaoParametro() {
        return subvencaoParametro;
    }

    public void setSubvencaoParametro(SubvencaoParametro subvencaoParametro) {
        this.subvencaoParametro = subvencaoParametro;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public List<EmpresaDevedoraSubvencao> getEmpresaDevedoraSubvencaoOrdenadas() {
        if (empresaDevedoraSubvencao == null) {
            return empresaDevedoraSubvencao;
        }
        Collections.sort(empresaDevedoraSubvencao, new Comparator<EmpresaDevedoraSubvencao>() {
            @Override
            public int compare(EmpresaDevedoraSubvencao o1, EmpresaDevedoraSubvencao o2) {
                return o1.getOrdem().compareTo(o2.getOrdem());
            }
        });
        return empresaDevedoraSubvencao;
    }

    public List<EmpresaDevedoraSubvencao> getEmpresaDevedoraSubvencao() {
        return empresaDevedoraSubvencao;
    }

    public void setEmpresaDevedoraSubvencao(List<EmpresaDevedoraSubvencao> empresaDevedoraSubvencao) {
        this.empresaDevedoraSubvencao = empresaDevedoraSubvencao;
    }

}
