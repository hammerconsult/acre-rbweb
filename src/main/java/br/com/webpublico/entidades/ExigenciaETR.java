package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Audited
public class ExigenciaETR extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RequerimentoLicenciamentoETR requerimentoLicenciamentoETR;
    @OneToMany(mappedBy = "exigenciaETR", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExigenciaETRFormulario> exigenciaETRFormularioList;

    public ExigenciaETR() {
        super();
        exigenciaETRFormularioList = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequerimentoLicenciamentoETR getRequerimentoLicenciamentoETR() {
        return requerimentoLicenciamentoETR;
    }

    public void setRequerimentoLicenciamentoETR(RequerimentoLicenciamentoETR requerimentoLicenciamentoETR) {
        this.requerimentoLicenciamentoETR = requerimentoLicenciamentoETR;
    }

    public List<ExigenciaETRFormulario> getExigenciaETRFormularioList() {
        if (exigenciaETRFormularioList != null) {
            Collections.sort(exigenciaETRFormularioList);
        }
        return exigenciaETRFormularioList;
    }

    public void setExigenciaETRFormularioList(List<ExigenciaETRFormulario> exigenciaETRFormularioList) {
        this.exigenciaETRFormularioList = exigenciaETRFormularioList;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (exigenciaETRFormularioList == null || exigenciaETRFormularioList.isEmpty()) {
            throw new ValidacaoException("Nenhum Formulário foi adicionado.");
        }
    }

    public boolean hasExigenciaETRFormulario(ExigenciaETRFormulario exigenciaETRFormulario) {
        if (exigenciaETRFormularioList != null) {
            for (ExigenciaETRFormulario etrFormulario : exigenciaETRFormularioList) {
                if (etrFormulario.getFormulario().getId().equals(exigenciaETRFormulario.getFormulario().getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
