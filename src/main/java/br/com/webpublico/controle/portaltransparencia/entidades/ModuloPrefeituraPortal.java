package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.controle.portaltransparencia.enums.ModuloSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renato on 30/08/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class ModuloPrefeituraPortal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Prefeitura")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private PrefeituraPortal prefeitura;
    @Etiqueta("Unidade")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private ModuloSistema modulo;
    @Etiqueta("Chave da Pagina de Destino")
    private String chavePaginaDestino;
    @Obrigatorio
    private Integer ordem;
    @Etiqueta("Ícone do Menu")
    private String icone;

    public ModuloPrefeituraPortal() {
    }

    public ModuloPrefeituraPortal(ModuloSistema despesa, PrefeituraPortal prefeituraPortal) {
        this.modulo = despesa;
        this.prefeitura = prefeituraPortal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrefeituraPortal getPrefeitura() {
        return prefeitura;
    }

    public void setPrefeitura(PrefeituraPortal prefeitura) {
        this.prefeitura = prefeitura;
    }

    public ModuloSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModuloSistema modulo) {
        this.modulo = modulo;
    }

    public String getChavePaginaDestino() {
        return chavePaginaDestino;
    }

    public void setChavePaginaDestino(String chavePaginaDestino) {
        this.chavePaginaDestino = chavePaginaDestino;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    @Override
    public String toString() {
        return modulo.toString();
    }
}
