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
@Etiqueta("Encerramento do Arquivo Digital")
public class PgdasRegistroZZZZZ extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Total Registros")
    private String tipoRegistro;
    @Etiqueta("Qtde Linhas")
    private String quantidadeLinhas;

    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistroZZZZZ() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
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

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 18);
        setTipoRegistro(pipes.get(0));
        setQuantidadeLinhas(pipes.get(1));
        setArquivoPgdas(assistente.getArquivoPgdas());
        assistente.getArquivoPgdas().getRegistrosZZZZZ().add(this);
    }
}
