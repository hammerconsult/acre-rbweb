CREATE OR REPLACE VIEW VwCadastroEconomicoBI AS
    with rec (inscricaoMobiliaria, cpfCnpj, nome, inicio, fim, codigoLogradouro, codigoBairro, numero,
              atividadePrincipal, situacao) as (
         select distinct ce.inscricaocadastral                                       as inscricaoMobiliaria,
                    REGEXP_REPLACE(coalesce(pf.cpf, pj.cnpj, ''), '[^0-9]', '') as cpfCnpj,
                    replace(coalesce(pf.nome, pj.razaosocial), ';', ' ')        as nome,
                    ce.abertura                                                 as inicio,
                    ce.encerramento                                             as fim,
                    (select max(codigo)
                       from logradouro l
                     where upper(trim(l.nome)) = upper(trim(endereco.logradouro))
                       and situacao = 'ATIVO')  as codigoLogradouro,
                    (select max(codigo)
                       from bairro b
                     where upper(trim(b.descricao)) = upper(trim(endereco.bairro))
                       and ativo = 1) as codigoBairro,
                    REGEXP_REPLACE(coalesce(endereco.numero, ''), '[^0-9]', '')       as numero,
                    (select max(cnae.codigocnae)
                     from cnae
                              left join economicocnae ecocnae on cnae.id = ecocnae.cnae_id
                         and ecocnae.tipo = 'Primaria'
                         and cnae.situacao = 'ATIVO'
                     where ecocnae.cadastroeconomico_id = ce.id)                as atividadePrincipal,
                    sitcmc.situacaocadastral                                    as situacao
    from cadastroeconomico ce
             inner join pessoa pes on ce.pessoa_id = pes.id
             left join EnderecoCadastroEconomico endereco on ce.id = endereco.cadastroEconomico_id
                                                         and endereco.tipoEndereco = 'COMERCIAL'
             left join ce_situacaocadastral sitcad on ce.id = sitcad.cadastroeconomico_id
             inner join situacaocadastroeconomico sitcmc on sitcad.situacaocadastroeconomico_id = sitcmc.id
             left join pessoafisica pf on pes.id = pf.id
             left join pessoajuridica pj on pes.id = pj.id
        and sitcmc.id = (select max(s.id)
                         from situacaocadastroeconomico s
                                  inner join ce_situacaocadastral ces on ces.situacaocadastroeconomico_id = s.id
                         where ces.cadastroeconomico_id = ce.id)
    )
select *
from rec
