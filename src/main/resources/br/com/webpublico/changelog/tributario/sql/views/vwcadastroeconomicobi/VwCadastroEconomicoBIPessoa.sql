CREATE or replace VIEW VwCadastroEconomicoBIPessoa AS
    with rec (inscricaoMobiliaria, cpfCnpj, nome, inicio, fim, codigoLogradouro, codigoBairro, numero,
              atividadePrincipal, situacao) as (
        select distinct to_char(coalesce(pf.ID, pj.ID))                             as inscricaoMobiliaria,
                        REGEXP_REPLACE(coalesce(pf.cpf, pj.cnpj, ''), '[^0-9]', '') as cpfCnpj,
                        replace(coalesce(pf.nome, pj.razaosocial), ';', ' ')        as nome,
                        cast(null as date)                                          as inicio,
                        cast(null as date)                                          as fim,
                        (select max(logr.codigo)
                         from logradouro logr
                         where REPLACE(TRIM(upper(logr.nome)), ' ', '') like
                               REPLACE(TRIM(upper(end.logradouro)), ' ', ''))       as codigoLogradouro,
                        (select max(b.codigo)
                         from bairro b
                         where REPLACE(TRIM(upper(b.DESCRICAO)), ' ', '') like
                               REPLACE(TRIM(upper(end.bairro)), ' ', ''))           as codigoBairro,
                        REGEXP_REPLACE(coalesce(end.numero, ''), '[^0-9]', '')      as numero,
                        coalesce(pes.CODIGOCNAEBI, '')                              as atividadePrincipal,
                        pes.situacaoCadastralPessoa                                 as situacao
        from Pessoa pes
                 left join pessoafisica pf on pes.id = pf.id
                 left join pessoajuridica pj on pes.id = pj.id
                 left join enderecocorreio end on end.id = pes.ENDERECOPRINCIPAL_ID
        where coalesce(pf.cpf, pj.cnpj) is not null
    )
    select *
    from rec
