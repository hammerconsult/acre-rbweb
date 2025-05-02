/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoComissao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@Entity

@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Comissão")
@Audited
public class Comissao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Título")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String titulo;
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Integer codigo;
    @Etiqueta("Inicio da Vigência")
    @Obrigatorio
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    private Date inicioVigencia;
    @Etiqueta("Fim da Vigência")
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Column(name = "FIMVIGENCIA")
    @Pesquisavel
    private Date finalVigencia;
    @Etiqueta("Ato de Comissão")
    @ManyToOne(fetch = FetchType.EAGER)
    @Pesquisavel
    private AtoDeComissao atoDeComissao;
    @Etiqueta("Tipo de Comissão")
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoComissao tipoComissao;
    @Etiqueta("Membro(s) da Comissão")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comissao")
    @Tabelavel(campoSelecionado = false)
    private List<MembroComissao> membroComissao = new ArrayList<>();

    public Comissao() {
    }

    public Comissao(AtoDeComissao atoDeComissao) {
        setAtoDeComissao(atoDeComissao);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public AtoDeComissao getAtoDeComissao() {
        return atoDeComissao;
    }

    public void setAtoDeComissao(AtoDeComissao atoDeComissao) {
        this.atoDeComissao = atoDeComissao;
    }

    public TipoComissao getTipoComissao() {
        return tipoComissao;
    }

    public void setTipoComissao(TipoComissao tipoComissao) {
        this.tipoComissao = tipoComissao;
    }

    public List<MembroComissao> getMembroComissao() {
        return membroComissao;
    }

    public void setMembroComissao(List<MembroComissao> membroComissao) {
        this.membroComissao = membroComissao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return titulo == null ? codigo.toString() : titulo;
    }

    public String getTituloReduzido() {
        if (titulo != null && titulo.length() > 70) {
            return titulo.substring(0, 70);
        }
        return titulo;
    }

    public boolean temMembroAdicionadoPorPessoaFisica(PessoaFisica pessoaFisica) {
        try {
            for (MembroComissao membro : membroComissao) {
                if (membro.getPessoaFisica().equals(pessoaFisica)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean temTitulo() {
        return titulo != null && !titulo.isEmpty();
    }

    public boolean temCodigo() {
        return codigo != null;
    }

    public boolean temInicioVigencia() {
        return inicioVigencia != null;
    }

    public boolean temFinalVigencia() {
        return finalVigencia != null;
    }

    public boolean temMembroAdicionado(MembroComissao membro) {
        for (MembroComissao mc : membroComissao) {
            if (!mc.equals(membro) && mc.getPessoaFisica().equals(membro.getPessoaFisica())) {
                return true;
            }
        }
        return false;
    }

    public void remover(MembroComissao membro) {
        membroComissao.remove(membro);
    }
}
