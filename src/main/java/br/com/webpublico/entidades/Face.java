/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
@Etiqueta("Face de Quadra")
public class Face implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @Obrigatorio
    @Etiqueta("Logradouro e Bairro")
    private LogradouroBairro logradouroBairro;
    @Tabelavel
    @Pesquisavel
    @Numerico
    private Double largura;
    @OneToMany(mappedBy = "face", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaceServico> faceServicos;
    @OneToMany(mappedBy = "face", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaceValor> faceValores;
    @Tabelavel
    @NotNull
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código da Face")
    private String codigoFace;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lado")
    private String lado;
    @Tabelavel
    @Etiqueta("CEP")
    private String cep;
    @Invisivel
    private String migracaoChave;
    @Transient
    private Long criadoEm;

    public Face() {
        this.faceServicos = new ArrayList<>();
        this.faceValores = new ArrayList<>();
        this.criadoEm = System.nanoTime();
    }

    public Face(Long id, String migracaoChave) {
        this.id = id;
        this.migracaoChave = migracaoChave;
    }

    public Face(Double largura, LogradouroBairro logradouroBairro) {
        this.largura = largura;
        this.logradouroBairro = logradouroBairro;
        this.faceServicos = new ArrayList<>();
        this.faceValores = new ArrayList<>();
    }

    public Face(Double largura, LogradouroBairro logradouroBairro, List<FaceServico> faceServicos, String codigoFace) {
        this.largura = largura;
        this.logradouroBairro = logradouroBairro;
        this.faceServicos = faceServicos;
        this.codigoFace = codigoFace;
    }

    public Face(Long id, Double largura, LogradouroBairro logradouroBairro, String codigoFace, String lado) {
        this.id = id;
        this.largura = largura;
        this.logradouroBairro = logradouroBairro;
        this.codigoFace = codigoFace;
        this.lado = lado;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCodigoFace() {
        return codigoFace;
    }

    public void setCodigoFace(String codigoFace) {
        this.codigoFace = codigoFace;
    }

    public List<FaceServico> getFaceServicos() {
        return faceServicos;
    }

    public void setFaceServicos(List<FaceServico> faceServicos) {
        this.faceServicos = faceServicos;
    }

    public Double getLargura() {
        return largura;
    }

    public void setLargura(Double largura) {
        this.largura = largura;
    }

    public LogradouroBairro getLogradouroBairro() {
        return logradouroBairro;
    }

    public void setLogradouroBairro(LogradouroBairro logradouroBairro) {
        this.logradouroBairro = logradouroBairro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLado() {
        return lado != null ? lado : "";
    }

    public void setLado(String lado) {
        this.lado = lado;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public List<FaceValor> getFaceValores() {
        return faceValores;
    }

    public FaceValor getFaceValor(Exercicio exercicio, Quadra quadra) {
        for (FaceValor faceValore : faceValores) {
            if (faceValore.getQuadra().equals(quadra) && faceValore.getQuadra().getSetor().equals(quadra.getSetor())
                    && faceValore.getExercicio().equals(exercicio)) {
                return faceValore;
            }
        }
        return null;
    }

    public List<FaceValor> getFacesValoresRepetidos(Exercicio exercicio, Quadra quadra, Setor setor) {
        List<FaceValor> retorno = Lists.newArrayList();
        for (FaceValor faceValore : faceValores) {
            if (faceValore.getQuadra().equals(quadra) && faceValore.getQuadra().getSetor().equals(setor)
                && faceValore.getExercicio().equals(exercicio)) {
                retorno.add(faceValore);
            }
        }
        return retorno;
    }

    public void setFaceValores(List<FaceValor> faceValores) {
        this.faceValores = faceValores;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Face de Quadra";
    }
}
