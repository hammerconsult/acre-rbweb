package br.com.webpublico.negocios.comum;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.negocios.tributario.dao.JdbcPessoaFisicaRFBDAO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Stateless
public class ImportacaoPessoaFisicaRFBFacade {

    private JdbcPessoaFisicaRFBDAO jdbcPessoaFisicaRFBDAO;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcPessoaFisicaRFBDAO = (JdbcPessoaFisicaRFBDAO) ap.getBean("pessoaFisicaRFBDAO");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future importarArquivo(AssistenteBarraProgresso assistenteBarraProgresso,
                                  InputStream inputStream) throws IOException {
        assistenteBarraProgresso.setDescricaoProcesso("Lendo dados do arquivo.");
        List<String> lines = IOUtils.readLines(inputStream);
        assistenteBarraProgresso.setDescricaoProcesso("Importando dados do arquivo.");
        assistenteBarraProgresso.setTotal(lines.size());
        jdbcPessoaFisicaRFBDAO.deleteAll();
        Set<String> cpfs = new HashSet<>();
        List<PessoaFisicaRFB> batch = Lists.newArrayList();
        for (String line : lines) {
            PessoaFisicaRFB pessoaFisicaRFB = processarLinhaArquivo(line);
            if (cpfs.contains(pessoaFisicaRFB.getCpf())) continue;
            batch.add(pessoaFisicaRFB);
            if (batch.size() == 1000) {
                jdbcPessoaFisicaRFBDAO.inserir(batch);
                assistenteBarraProgresso.contar(1000);
                batch = Lists.newArrayList();
            }
            cpfs.add(pessoaFisicaRFB.getCpf());
        }
        if (batch.size() > 0) {
            jdbcPessoaFisicaRFBDAO.inserir(batch);
            assistenteBarraProgresso.contar(batch.size());
        }
        return new AsyncResult(null);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public PessoaFisicaRFB processarLinhaArquivo(String line) {
        String[] colunas = line.split(";");

        PessoaFisicaRFB pessoaFisicaRFB = new PessoaFisicaRFB();
        pessoaFisicaRFB.setCpf(colunas[0]);
        pessoaFisicaRFB.setNome(colunas[1]);
        pessoaFisicaRFB.setNomeMae(colunas[2]);
        pessoaFisicaRFB.setDataNascimento(colunas[3] != null ?
            DateUtils.getData(colunas[3], "dd/MM/yyyy") : null);
        pessoaFisicaRFB.setTipoLogradouro(colunas[4]);
        pessoaFisicaRFB.setLogradouro(colunas[5]);
        pessoaFisicaRFB.setComplemento(colunas[6]);
        pessoaFisicaRFB.setNumero(colunas[7]);
        pessoaFisicaRFB.setBairro(colunas[8]);
        pessoaFisicaRFB.setCep(colunas[9]);
        pessoaFisicaRFB.setMunicipio(colunas[10]);
        pessoaFisicaRFB.setDdd(colunas[11]);
        pessoaFisicaRFB.setTelefone(colunas[12]);
        pessoaFisicaRFB.setEmail(colunas[13]);
        return pessoaFisicaRFB;
    }
}
