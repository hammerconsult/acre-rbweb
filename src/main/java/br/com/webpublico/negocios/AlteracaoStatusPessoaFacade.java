package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.DataUtil;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class AlteracaoStatusPessoaFacade extends AbstractFacade<AlteracaoStatusPessoa> {

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AlteracaoStatusPessoaFacade() {
        super(AlteracaoStatusPessoa.class);
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(AlteracaoStatusPessoa alteracao) {
        alteracao = em.merge(alteracao);
        alteracao.getPessoa().setSituacaoCadastralPessoa(retornaSituacaoPorTipoProcessoAlteracao(alteracao.getTipoProcesso()));
        alteracao.getPessoa().adicionarHistoricoSituacaoCadastral();
        em.merge(alteracao.getPessoa());
    }

    public boolean existeNumeroProcesso(String numeroCompleto) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from alteracaostatuspessoa where numerocompleto = :numero ");
        Query q = em.createNativeQuery(sb.toString(), AlteracaoStatusPessoa.class);
        q.setParameter("numero", numeroCompleto);

        return !q.getResultList().isEmpty();

    }

    public String retornaNovoNumero(Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select case");
        sql.append(" when max(numero) is null then 1");
        sql.append(" else");
        sql.append(" max(numero) + 1");
        sql.append(" end");
        sql.append(" from alteracaostatuspessoa where ano = :ano");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ano);


        return q.getSingleResult().toString();
    }

    public String retornaNovoNumeroCompleto(Date data) {
        StringBuilder numero = new StringBuilder();

        Integer ano = Integer.parseInt(DataUtil.getDataFormatada(data, "yyyy"));


        numero.append(DataUtil.getDataFormatada(data, "yyyy"));
        numero.append(StringUtils.leftPad(retornaNovoNumero(ano), 7, "0"));


        return numero.toString();
    }

    public SituacaoCadastralPessoa retornaSituacaoPorTipoProcessoAlteracao(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa tipoProcesso) {
        switch (tipoProcesso) {
            case ATIVACAO:
                return SituacaoCadastralPessoa.ATIVO;
            case INATIVACAO:
                return SituacaoCadastralPessoa.INATIVO;
            case BAIXA:
                return SituacaoCadastralPessoa.BAIXADO;
            case SUSPENSAO:
                return SituacaoCadastralPessoa.SUSPENSO;
            default:
                return SituacaoCadastralPessoa.ATIVO;
        }
    }

    public boolean pessoaPossuiVinculoComCadastroImobiliarioAtivo(Pessoa pessoa) {
        return !cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoa(pessoa).isEmpty();
    }

    public boolean pessoaPossuiVinculoComCadastroEconomicoAtivo(Pessoa pessoa) {
        return !cadastroEconomicoFacade.recuperaCadastrosAtivosDaPessoa(pessoa).isEmpty();
    }

    public boolean pessoaSociaCadastroEconomicoAtivo(Pessoa pessoa) {
        return !cadastroEconomicoFacade.recuperaCadastrosAtivosDaPessoaSocia(pessoa).isEmpty();
    }

    public boolean pessoaPossuiDebitoEmAberto(Pessoa pessoa) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, pessoa.getId());
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.executaConsulta();
        return !consulta.getResultados().isEmpty();
    }

    public boolean pessoaProprietariaCadastroImobiliarioComDebito(Pessoa pessoa) {
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoa(pessoa);
        if (cadastros.isEmpty()) {
            return false;
        } else {
            return cadastroPossuiDebito(cadastros);
        }
    }

    public boolean pessoaCompromissariaCadastroImobiliarioComDebito(Pessoa pessoa) {
        List<CadastroImobiliario> cadastros = cadastroImobiliarioFacade.recuperaCadastrosAtivosDaPessoaCompromissario(pessoa);
        if (cadastros.isEmpty()) {
            return false;
        } else {
            return cadastroPossuiDebito(cadastros);
        }
    }

    public boolean pessoaSociaCadastroEconomicoComDebito(Pessoa pessoa) {
        List<CadastroEconomico> cadastros = cadastroEconomicoFacade.recuperaCadastrosAtivosDaPessoaSocia(pessoa);
        if (cadastros.isEmpty()) {
            return false;
        } else {
            return cadastroPossuiDebito(cadastros);
        }
    }

    private boolean cadastroPossuiDebito(List<? extends Cadastro> cadastros) {
        List<Long> ids = new ArrayList<>();
        for (Cadastro c : cadastros) {
            ids.add(c.getId());
        }

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, ids);
        consulta.executaConsulta();
        return !consulta.getResultados().isEmpty();
    }

    public boolean pessoaTemUsuarioSistemaAtivo(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            return usuarioSistemaFacade.usuarioSistemaDaPessoa((PessoaFisica) pessoa) != null;
        }
        return false;
    }

    public void inativaUsuarioDaPessoa(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            UsuarioSistema usuario = usuarioSistemaFacade.usuarioSistemaDaPessoa((PessoaFisica) pessoa);
            usuarioSistemaFacade.inativaUsuario(usuario);
        }
    }
}
