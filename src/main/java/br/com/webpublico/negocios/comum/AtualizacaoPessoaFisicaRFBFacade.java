package br.com.webpublico.negocios.comum;

import br.com.webpublico.StringUtils;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.tributario.dao.*;
import br.com.webpublico.singletons.SingletonPessoaFisicaRFB;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Stateless
public class AtualizacaoPessoaFisicaRFBFacade {

    private static final Logger logger = LoggerFactory.getLogger(AtualizacaoPessoaFisicaRFBFacade.class);
    @EJB
    private SingletonPessoaFisicaRFB singletonPessoaFisicaRFB;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private JdbcRevisaoAuditoriaDAO jdbcRevisaoAuditoriaDAO;
    private JdbcPessoaDAO jdbcPessoaDAO;
    private JdbcPessoaFisicaRFBDAO jdbcPessoaFisicaRFBDAO;
    private JdbcEnderecoCorreioDAO jdbcEnderecoCorreioDAO;
    private JdbcTelefoneDAO jdbcTelefoneDAO;

    @PostConstruct
    private void init() {
        WebApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcPessoaDAO = (JdbcPessoaDAO) ap.getBean("consultaPessoaDAO");
        jdbcPessoaFisicaRFBDAO = (JdbcPessoaFisicaRFBDAO) ap.getBean("pessoaFisicaRFBDAO");
        jdbcRevisaoAuditoriaDAO = (JdbcRevisaoAuditoriaDAO) ap.getBean("revisaoAuditoriaDAO");
        jdbcEnderecoCorreioDAO = (JdbcEnderecoCorreioDAO) ap.getBean("enderecoCorreioDAO");
        jdbcTelefoneDAO = (JdbcTelefoneDAO) ap.getBean("telefoneDAO");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future atualizarDados(AssistenteBarraProgresso assistenteBarraProgresso) {
        List<PessoaFisicaRFB> lista;
        while (!(lista = singletonPessoaFisicaRFB.getProximasPessoasAtualizar()).isEmpty()) {
            List<PessoaFisica> pessoasFisicasParaAtualizar = Lists.newArrayList();
            List<EnderecoCorreio> enderecosCorreioParaAtualizar = Lists.newArrayList();
            List<Telefone> telefonesParaAtualizar = Lists.newArrayList();
            List<PessoaFisica> pessoasFisicasParaInserir = Lists.newArrayList();
            List<EnderecoCorreio> enderecosCorreioParaInserir = Lists.newArrayList();
            List<Telefone> telefonesParaInserir = Lists.newArrayList();
            for (PessoaFisicaRFB pessoaFisicaRFB : lista) {
                try {
                    PessoaFisica pessoaFisica = pessoaFisicaFacade.buscarPessoaPeloCPF(pessoaFisicaRFB.getCpf());
                    if (pessoaFisica != null) {
                        pessoasFisicasParaAtualizar.add(atualizarPessoaFisica(pessoaFisica, pessoaFisicaRFB));
                        EnderecoCorreio enderecoCorreio = atualizarEnderecoCorreio(pessoaFisica, pessoaFisicaRFB);
                        if (enderecoCorreio != null) {
                            enderecosCorreioParaAtualizar.add(enderecoCorreio);
                        }
                        Telefone telefone = atualizarTelefone(pessoaFisica, pessoaFisicaRFB);
                        if (telefone != null) {
                            telefonesParaAtualizar.add(telefone);
                        }
                    } else if (pessoaFisicaFacade.buscarIdDePessoaPorCpf(pessoaFisicaRFB.getCpf()) == null) {
                        PessoaFisica novaPessoaFisica = new PessoaFisica();
                        pessoasFisicasParaInserir.add(novaNovaPessoaFisica(novaPessoaFisica, pessoaFisicaRFB));
                        enderecosCorreioParaInserir.add(criarEnderecoCorreio(novaPessoaFisica, pessoaFisicaRFB, TipoEndereco.DOMICILIO_FISCAL));
                        novaPessoaFisica.setEnderecoPrincipal(null);
                        Telefone telefone = novoTelefone(novaPessoaFisica, pessoaFisicaRFB);
                        if (telefone != null) {
                            telefonesParaInserir.add(telefone);
                        }
                    }
                    pessoaFisicaRFB.setSituacao(PessoaFisicaRFB.Situacao.ATUALIZADO);
                } catch (Exception e) {
                    logger.error("Erro ao atualizar a PF RFB " + pessoaFisicaRFB.getCpf(), e);
                    pessoaFisicaRFB.setSituacao(PessoaFisicaRFB.Situacao.AGUARDANDO);
                }
            }
            try {
                salvarPessoasFisicas(pessoasFisicasParaAtualizar, enderecosCorreioParaAtualizar, telefonesParaAtualizar, false);
                salvarPessoasFisicas(pessoasFisicasParaInserir, enderecosCorreioParaInserir, telefonesParaInserir, true);
                jdbcPessoaFisicaRFBDAO.update(lista);
            } catch (Exception e) {
                logger.error("Erro ao salvar pessoas fisicas", e);
            }
            assistenteBarraProgresso.contar(lista.size());
        }
        return new AsyncResult(null);
    }

    private void salvarPessoasFisicas(List<PessoaFisica> pessoasFisicas,
                                      List<EnderecoCorreio> enderecosCorreio,
                                      List<Telefone> telefones, Boolean novasPessoas) {
        if (!pessoasFisicas.isEmpty()) {
            Long rev = jdbcRevisaoAuditoriaDAO.newRevisaoAuditoria();
            if (novasPessoas) jdbcPessoaDAO.insert(rev, pessoasFisicas);
            jdbcEnderecoCorreioDAO.insert(rev, enderecosCorreio);
            jdbcTelefoneDAO.insert(rev, telefones);
            if (novasPessoas) {
                jdbcPessoaDAO.updateEnderecoAndTelefonePessoa(pessoasFisicas, enderecosCorreio, telefones);
            } else {
                jdbcPessoaDAO.update(rev, pessoasFisicas);
            }
        }
    }

    private void salvarEndereco(EnderecoCorreio enderecoCorreio) {
        if (enderecoCorreio != null) {
            jdbcPessoaDAO.updateEnderecoCorreio(enderecoCorreio);
        }
    }

    private PessoaFisica atualizarPessoaFisica(PessoaFisica pessoaFisica, PessoaFisicaRFB pessoaFisicaRFB) {
        pessoaFisica.setNome(pessoaFisicaRFB.getNome());
        pessoaFisica.setMae(pessoaFisicaRFB.getNomeMae());
        pessoaFisica.setDataNascimento(pessoaFisicaRFB.getDataNascimento());
        pessoaFisica.setEmail(pessoaFisicaRFB.getEmail());
        return pessoaFisica;
    }

    private PessoaFisica novaNovaPessoaFisica(PessoaFisica pessoaFisica, PessoaFisicaRFB pessoaFisicaRFB) {
        pessoaFisica.setNome(pessoaFisicaRFB.getNome());
        pessoaFisica.setMae(pessoaFisicaRFB.getNomeMae());
        pessoaFisica.setCpf(pessoaFisicaRFB.getCpf().replace(".", "").replace("-", ""));
        pessoaFisica.setDataNascimento(pessoaFisicaRFB.getDataNascimento());
        pessoaFisica.setEmail(pessoaFisicaRFB.getEmail());
        pessoaFisica.setDataRegistro(new Date());
        return pessoaFisica;
    }

    private Telefone atualizarTelefone(PessoaFisica pessoaFisica, PessoaFisicaRFB pessoaFisicaRFB) {
        String dddTelefone = pessoaFisicaRFB.getDddTelefone();
        if (Strings.isNullOrEmpty(dddTelefone)) return null;
        for (Telefone tel : pessoaFisica.getTelefones()) {
            String telefoneApenasNumeros = StringUtils.getApenasNumeros(tel.getTelefone());
            if (telefoneApenasNumeros != null && telefoneApenasNumeros.equals(dddTelefone)) {
                return null;
            }
        }
        Telefone telefone = new Telefone();
        telefone.setPessoa(pessoaFisica);
        telefone.setTipoFone(pessoaFisicaRFB.getTelefone().startsWith("9") ? TipoTelefone.CELULAR : TipoTelefone.OUTROS);
        telefone.setTelefone(dddTelefone);
        telefone.setPrincipal(Boolean.FALSE);
        return telefone;
    }

    private Telefone novoTelefone(PessoaFisica pessoaFisica, PessoaFisicaRFB pessoaFisicaRFB) {
        String dddTelefone = pessoaFisicaRFB.getDddTelefone();
        if (Strings.isNullOrEmpty(dddTelefone)) return null;
        Telefone telefone = new Telefone();
        telefone.setPessoa(pessoaFisica);
        telefone.setTipoFone(TipoTelefone.CELULAR);
        telefone.setTelefone(dddTelefone);
        telefone.setPrincipal(Boolean.TRUE);
        return telefone;
    }

    private EnderecoCorreio atualizarEnderecoCorreio(PessoaFisica pessoaFisica, PessoaFisicaRFB pessoaFisicaRFB) {
        for (EnderecoCorreio enderecoCorreio : pessoaFisica.getEnderecos()) {
            if (isMesmoEnderecoRFB(enderecoCorreio, pessoaFisicaRFB)) {
                if (!TipoEndereco.DOMICILIO_FISCAL.equals(enderecoCorreio.getTipoEndereco())) {
                    enderecoCorreio.setTipoEndereco(TipoEndereco.DOMICILIO_FISCAL);
                    salvarEndereco(enderecoCorreio);
                }
                return null;
            } else if (TipoEndereco.DOMICILIO_FISCAL.equals(enderecoCorreio.getTipoEndereco())) {
                enderecoCorreio.setTipoEndereco(TipoEndereco.OUTROS);
                salvarEndereco(enderecoCorreio);
            }
        }

        EnderecoCorreio enderecoDomicilioFiscal = pessoaFisica.getEnderecoDomicilioFiscal();
        if (enderecoDomicilioFiscal != null) {
            if (TipoEndereco.DOMICILIO_FISCAL.equals(enderecoDomicilioFiscal.getTipoEndereco())) {
                enderecoDomicilioFiscal.setTipoEndereco(TipoEndereco.OUTROS);
            }
            enderecoDomicilioFiscal.setPrincipal(false);
            salvarEndereco(enderecoDomicilioFiscal);
        }
        return criarEnderecoCorreio(pessoaFisica, pessoaFisicaRFB, TipoEndereco.DOMICILIO_FISCAL);
    }

    private boolean isMesmoEnderecoRFB(EnderecoCorreio enderecoCorreio, PessoaFisicaRFB pessoaFisicaRFB) {
        return Util.isStringIgual(enderecoCorreio.getBairro(), pessoaFisicaRFB.getBairro()) &&
            Util.isStringIgual(enderecoCorreio.getLogradouro(), pessoaFisicaRFB.getLogradouro()) &&
            Util.isStringIgual(enderecoCorreio.getNumero(), pessoaFisicaRFB.getNumero()) &&
            Util.isStringIgual(enderecoCorreio.getCep(), pessoaFisicaRFB.getCep());
    }

    private EnderecoCorreio criarEnderecoCorreio(PessoaFisica pessoaFisica,
                                                 PessoaFisicaRFB pessoaFisicaRFB,
                                                 TipoEndereco tipoEndereco) {
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setPessoaFisica(pessoaFisica);
        enderecoCorreio.setTipoEndereco(tipoEndereco);
        enderecoCorreio.setBairro(pessoaFisicaRFB.getBairro());
        enderecoCorreio.setTipoLogradouro(Util.traduzirEnum(TipoLogradouroEnderecoCorreio.class,
            pessoaFisicaRFB.getTipoLogradouro()));
        enderecoCorreio.setLogradouro(pessoaFisicaRFB.getLogradouro());
        enderecoCorreio.setNumero(pessoaFisicaRFB.getNumero());
        enderecoCorreio.setComplemento(pessoaFisicaRFB.getComplemento());
        enderecoCorreio.setCep(pessoaFisicaRFB.getCep());
        enderecoCorreio.setLocalidade(pessoaFisicaRFB.getMunicipio());
        enderecoCorreio.setPrincipal(tipoEndereco.equals(TipoEndereco.DOMICILIO_FISCAL));
        if (enderecoCorreio.getPrincipal()) {
            pessoaFisica.setEnderecoPrincipal(enderecoCorreio);
        }
        return enderecoCorreio;
    }
}
