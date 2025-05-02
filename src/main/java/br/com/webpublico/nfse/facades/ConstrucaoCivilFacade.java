package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.ConstrucaoCivil;
import br.com.webpublico.nfse.domain.ConstrucaoCivilLocalizacao;
import br.com.webpublico.nfse.domain.ConstrucaoCivilProfissional;
import br.com.webpublico.nfse.domain.ConstrucaoCivilStatus;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.enums.TipoProfissional;
import br.com.webpublico.nfse.util.GeradorQuery;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ConstrucaoCivilFacade extends AbstractFacade<ConstrucaoCivil> {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConstrucaoCivilFacade() {
        super(ConstrucaoCivil.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConstrucaoCivil recuperar(Object id) {
        ConstrucaoCivil construcaoCivil = super.recuperar(id);
        inicializar(construcaoCivil);
        return construcaoCivil;
    }

    private void inicializar(ConstrucaoCivil construcaoCivil) {
        construcaoCivil.setCadastroEconomico(cadastroEconomicoFacade.recuperar(construcaoCivil.getCadastroEconomico().getId()));
        if (construcaoCivil.getCadastroImobiliario() != null) {
            construcaoCivil.setCadastroImobiliario(cadastroImobiliarioFacade.recuperar(construcaoCivil.getCadastroImobiliario().getId()));
        }
        if (construcaoCivil.getResponsavel() != null) {
            construcaoCivil.setResponsavel(pessoaFacade.recuperar(construcaoCivil.getResponsavel().getId()));
        }
        Hibernate.initialize(construcaoCivil.getProfissionais());
    }

    public Page<ConstrucaoCivilNfseDTO> buscarConstrucoesCivil(Pageable pageable, CadastroEconomico cadastroEconomico, String filtro) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();

        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("ce.id", Operador.IGUAL, cadastroEconomico.getId()))));

        parametros.add(new ParametroQuery(" or ",
            Lists.newArrayList(
                new ParametroQueryCampo("to_char(cc.codigoobra)", Operador.LIKE, "%" + filtro.trim() + "%"),
                new ParametroQueryCampo("cc.art", Operador.LIKE, "%" + filtro.trim() + "%")
            )));

        List<ConstrucaoCivilNfseDTO> toReturn = Lists.newArrayList();
        List<ConstrucaoCivil> construcoes = buscarConstrucaoCivil(parametros, pageable);
        if (construcoes != null && !construcoes.isEmpty()) {
            for (ConstrucaoCivil construcaoCivil : construcoes) {
                toReturn.add(construcaoCivil.toNfseDto());
            }
        }
        return new PageImpl<>(toReturn);
    }

    public List<ConstrucaoCivil> buscarConstrucaoCivil(List<ParametroQuery> parametros, Pageable pageable) throws Exception {
        String sql = " select cc.* " +
            "   from construcaocivil cc " +
            "  inner join cadastroeconomico ce on cc.cadastroeconomico_id = ce.id ";
        Query query = new GeradorQuery(em, ConstrucaoCivil.class, sql).parametros(parametros).build();
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    public ConstrucaoCivilNfseDTO salvarByDTO(ConstrucaoCivilNfseDTO dto) throws ValidacaoException {
        ConstrucaoCivil construcaoCivil = new ConstrucaoCivil();
        construcaoCivil.setId(dto.getId());
        construcaoCivil.setCadastroEconomico(cadastroEconomicoFacade.recuperar(dto.getPrestador().getId()));
        construcaoCivil.setCodigoObra(dto.getCodigoObra());
        gerarCodigoObra(construcaoCivil);
        construcaoCivil.setResponsavel(getPessoaFromDTO(dto.getResponsavel()));
        construcaoCivil.setConstrucaoCivilLocalizacao(getConstrucaoCivilLocalizacaoFromDTO(dto.getLocalizacao()));
        construcaoCivil.setDataAprovacaoProjeto(dto.getDataAprovacaoProjeto());
        construcaoCivil.setDataInicio(dto.getDataInicio());
        construcaoCivil.setDataConclusao(dto.getDataConclusao());
        construcaoCivil.setArt(dto.getArt());
        construcaoCivil.setNumeroAlvara(dto.getNumeroAlvara());
        construcaoCivil.setIncorporacao(dto.getIncorporacao());
        construcaoCivil.setMatriculaImovel(dto.getMatriculaImovel());
        construcaoCivil.setNumeroHabitese(dto.getNumeroHabitese());
        construcaoCivil.setConstrucaoCivilStatus(dto.getStatus() != null ? ConstrucaoCivilStatus.fromDTO(dto.getStatus()) : null);
        if (dto.getImovel() != null && dto.getImovel().getId() != null)
            construcaoCivil.setCadastroImobiliario(cadastroImobiliarioFacade.recuperar(dto.getImovel().getId()));
        atribuirProfissionaisByDTO(construcaoCivil, dto.getProfissionais());
        construcaoCivil = em.merge(construcaoCivil);
        construcaoCivil = recuperar(construcaoCivil.getId());
        return construcaoCivil.toNfseDto();
    }

    private void atribuirProfissionaisByDTO(ConstrucaoCivil construcaoCivil, List<ConstrucaoCivilProfissionalNfseDTO> profissionais) throws ValidacaoException {
        construcaoCivil.setProfissionais(new ArrayList<ConstrucaoCivilProfissional>());

        if (profissionais != null) {
            for (ConstrucaoCivilProfissionalNfseDTO dto : profissionais) {
                ConstrucaoCivilProfissional profissional = new ConstrucaoCivilProfissional();
                profissional.setId(dto.getId());
                profissional.setConstrucaoCivil(construcaoCivil);
                profissional.setProfissional(pessoaFacade.createOrSave(dto.getProfissional()));
                profissional.setTipoProfissional(TipoProfissional.findByDTO(dto.getTipoProfissional()));
                construcaoCivil.getProfissionais().add(profissional);
            }
        }
    }

    private void gerarCodigoObra(ConstrucaoCivil construcaoCivil) {
        if (construcaoCivil.getCodigoObra() != null && construcaoCivil.getCodigoObra() > 0) {
            return;
        }
        construcaoCivil.setCodigoObra(buscarUltimoCodigoObra(construcaoCivil.getCadastroEconomico()) + 1);
    }

    public Long buscarUltimoCodigoObra(CadastroEconomico cadastroEconomico) {
        String sql = " select max(cc.codigoobra) " +
            "   from construcaocivil cc ";
        Query query = em.createNativeQuery(sql);
        if (query.getResultList() != null && !query.getResultList().isEmpty() && query.getResultList().get(0) != null) {
            return ((BigDecimal) query.getResultList().get(0)).longValue();
        }
        return 0l;
    }


    private Pessoa getPessoaFromDTO(PessoaNfseDTO dto) throws ValidacaoException {
        if (dto == null || dto.getDadosPessoais() == null || dto.getDadosPessoais().getCpfCnpj() == null) {
            return null;
        }
        Pessoa pessoa;
        if (dto.getId() != null) {
            pessoa = pessoaFacade.recuperar(dto.getId());
        } else {
            if (dto.getDadosPessoais().getCpfCnpj().length() >= 14) {
                pessoa = new PessoaJuridica();
            } else {
                pessoa = new PessoaFisica();
            }
            pessoa = pessoaFacade.aplicarDadosPessoais(dto.getDadosPessoais(), pessoa);
        }
        return pessoa;
    }

    private ConstrucaoCivilLocalizacao getConstrucaoCivilLocalizacaoFromDTO(ConstrucaoCivilLocalizacaoNfseDTO dto) {
        if (dto == null) {
            return null;
        }
        ConstrucaoCivilLocalizacao construcaoCivilLocalizacao = new ConstrucaoCivilLocalizacao();
        if (dto.getMunicipio() != null) {
            construcaoCivilLocalizacao.setCidade(cidadeFacade.recuperar(dto.getMunicipio().getId()));
        }
        construcaoCivilLocalizacao.setBairro(dto.getBairro());
        construcaoCivilLocalizacao.setLogradouro(dto.getLogradouro());
        construcaoCivilLocalizacao.setNumero(dto.getNumero());
        construcaoCivilLocalizacao.setCep(dto.getCep());
        construcaoCivilLocalizacao.setNomeEmpreendimento(dto.getNomeEmpreendimento());
        return construcaoCivilLocalizacao;
    }

    public ConstrucaoCivil buscarConstrucaoCivilPorArt(String art) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("cc.art", Operador.IGUAL, art))));
        List<ConstrucaoCivil> construcoes = buscarConstrucaoCivil(parametros, null);
        if (!construcoes.isEmpty()) {
            ConstrucaoCivil construcaoCivil = construcoes.get(0);
            inicializar(construcaoCivil);
            return construcaoCivil;
        }
        return null;
    }
}
