package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta(value = "Assentamento Funcional")
public class AssentamentoFuncional extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Sequencial")
    private Long sequencial;
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Cadastro")
    private Date dataCadastro;
    @Obrigatorio
    private String historico;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Long getSequencial() {
        return sequencial;
    }

    public void setSequencial(Long sequencial) {
        this.sequencial = sequencial;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
