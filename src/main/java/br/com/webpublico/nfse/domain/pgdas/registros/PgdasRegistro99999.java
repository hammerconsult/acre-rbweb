package br.com.webpublico.nfse.domain.pgdas.registros;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.RegistroLinhaPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Encerramento do Bloco 00000")
public class PgdasRegistro99999 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("00000")
    private String reg;
    @Etiqueta("Qtde Linhas")
    private String quantidadeLinhas;
    @ManyToOne
    private PgdasRegistroAAAAA pgdasRegistroAAAAA;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro99999() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getQuantidadeLinhas() {
        return quantidadeLinhas;
    }

    public void setQuantidadeLinhas(String quantidadeLinhas) {
        this.quantidadeLinhas = quantidadeLinhas;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistroAAAAA getPgdasRegistroAAAAA() {
        return pgdasRegistroAAAAA;
    }

    public void setPgdasRegistroAAAAA(PgdasRegistroAAAAA pgdasRegistroAAAAA) {
        this.pgdasRegistroAAAAA = pgdasRegistroAAAAA;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 18);
        setReg(pipes.get(0));
        setQuantidadeLinhas(pipes.get(1));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistroAAAAA(assistente.getPgdasRegistroAAAAA());
        assistente.getArquivoPgdas().getRegistros99999().add(this);
    }
}
