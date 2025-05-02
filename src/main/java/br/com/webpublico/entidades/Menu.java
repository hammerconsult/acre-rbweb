/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Segurança")
@Etiqueta("Menu")
public class Menu implements Serializable, Comparable<Menu> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    private String label;
    private String caminho;
    @ManyToOne
    private Menu pai;
    @Obrigatorio
    private Integer ordem;
    @JsonIgnore
    @OneToMany(mappedBy = "pai", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Menu> filhos;
    @Transient
    private Long paiId;
    @Transient
    private String caminhoDoMenu;
    @Transient
    @Invisivel
    private Long criadoEm;
    private String icone;

    public Menu() {
        criadoEm = System.nanoTime();
    }

    public Integer getNumeroDeFilhos() {
        try {
            return this.filhos.size();
        } catch (Exception e) {
            this.filhos = new ArrayList<>();
            return getNumeroDeFilhos();
        }
    }

    public Menu(Long id, String label, String caminho) {
        this.id = id;
        this.label = label;
        this.caminho = caminho;
    }

    public Menu(Long id, String label, String caminho, List<Menu> filhos) {
        this.id = id;
        this.label = label;
        this.caminho = caminho;
        this.filhos = filhos;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Menu getPai() {
        return this.pai;
    }

    public void setPai(Menu pai) {
        this.pai = pai;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getIcone() { return icone; }

    public void setIcone(String icone) { this.icone = icone; }

    public List<Menu> getFilhos() {
        try {
            Collections.sort(filhos);
            return filhos;
        } catch (Exception e) {
            e.printStackTrace();
            return filhos;
        }
    }

    public void setFilhos(List<Menu> filhos) {
        this.filhos = filhos;
    }

    @Override
    public String toString() {
        return label;
    }

    public String toStringArvore() {
        String href = "";
        if (caminho != null) {
            href = caminho.endsWith(".xhtml") ? "/faces" + caminho : caminho;
        }
        href = FacesUtil.getRequestContextPath() + href;
        String aux = caminho == null ? "" : " - <a href='" + href + "' style='color : blue'>" + caminho + "</a>";
        return label + " - " + ordem + aux;
    }

    public Long getPaiId() {
        if (paiId == null) {
            try {
                paiId = pai.getId();
            } catch (NullPointerException npe) {
            }
        }
        return paiId;
    }

    public void setPaiId(Long paiId) {
        this.paiId = paiId;
    }

    public void processarCaminhoDoMenu(Menu m) {
        if (caminhoDoMenu == null) {
            caminhoDoMenu = "INÍCIO";
        }
        if (m != null) {
            processarCaminhoDoMenu(m.getPai());
            caminhoDoMenu += " > " + m.getLabel();
        }
    }

    public String getCaminhoDoMenu() {
        if (caminhoDoMenu == null) {

            processarCaminhoDoMenu(this);
        }
        return caminhoDoMenu;
    }

    public void setCaminhoDoMenu(String caminhoDoMenu) {
        this.caminhoDoMenu = caminhoDoMenu;
    }

    @Override
    public int compareTo(Menu o) {
        try {
            return this.getOrdem().compareTo(o.getOrdem());
        } catch (NullPointerException npe) {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
