create or replace view vwenderecobci as(
select
     ci.id cadastroimobiliario_id,
     trim(coalesce(tp.descricao,'')) || ' ' || trim(coalesce(logradouro.nome,'')) logradouro,
     trim(lote.numerocorreio) numero,
     trim(ci.complementoendereco) complemento,
     trim(bairro.descricao) bairro,
     trim(coalesce(loteamento.nome,'')) || ' ' || trim(coalesce(lote.descricaoloteamento,'')) loteamento,
     trim(logbairro.cep) cep,
     trim(setor.codigo) setor,
     trim(quadra.codigo) quadra,
     trim(lote.codigolote) lote,
     trim(lote.complemento) complementolote
    from cadastroimobiliario ci
    inner join lote lote on lote.id = ci.lote_id
    left join quadra quadra on quadra.id = lote.quadra_id
    left join setor setor on setor.id = quadra.setor_id
    left join loteamento loteamento on loteamento.id = quadra.loteamento_id
    inner join testada testadas on testadas.lote_id = lote.id and testadas.principal = 1
    inner join face face on face.id = testadas.face_id
    inner join logradourobairro logbairro on logbairro.id = face.logradourobairro_id
    inner join logradouro logradouro on logradouro.id = logbairro.logradouro_id
    inner join tipologradouro tp on tp.id = logradouro.tipologradouro_id
    inner join bairro bairro on bairro.id = logbairro.bairro_id)