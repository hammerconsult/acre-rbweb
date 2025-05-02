package br.com.webpublico.entidadesauxiliares.comum;

import java.io.Serializable;
import java.util.List;

public class GrupoUsuarioVO implements Serializable {
    private Long id;
    private String nome;
    private List<PeriodoVO> periodos;

    public GrupoUsuarioVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<PeriodoVO> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<PeriodoVO> periodos) {
        this.periodos = periodos;
    }
}
