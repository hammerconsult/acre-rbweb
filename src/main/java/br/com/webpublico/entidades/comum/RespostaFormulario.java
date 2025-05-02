package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
public class RespostaFormulario extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Formulario formulario;
    @OneToMany(mappedBy = "respostaFormulario")
    private List<RespostaFormularioCampo> respostaFormularioCampoList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public List<RespostaFormularioCampo> getRespostaFormularioCampoList() {
        return respostaFormularioCampoList;
    }

    public List<RespostaFormularioCampo> getRespostaFormularioCampoListSorted() {
        return respostaFormularioCampoList != null ?
            respostaFormularioCampoList.stream()
                .sorted(Comparator.comparing(RespostaFormularioCampo::getOrdem))
                .collect(Collectors.toList()) : Lists.newArrayList();
    }

    public void setRespostaFormularioCampoList(List<RespostaFormularioCampo> respostaFormularioCampoList) {
        this.respostaFormularioCampoList = respostaFormularioCampoList;
    }

    public List<Arquivo> getArquivosAnexados() {
        List<Arquivo> arquivos = Lists.newArrayList();
        for (RespostaFormularioCampo respostaFormularioCampo : getRespostaFormularioCampoList()) {
            if (FormularioCampo.TipoComponente.ARQUIVO.equals(respostaFormularioCampo.getFormularioCampo().getTipoComponente()) &&
                    respostaFormularioCampo.getArquivo() != null) {
                arquivos.add(respostaFormularioCampo.getArquivo());
            }
        }
        return arquivos;
    }
}
