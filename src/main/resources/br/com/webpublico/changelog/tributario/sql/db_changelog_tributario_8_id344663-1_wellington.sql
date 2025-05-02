insert into processocalculo (id, exercicio_id, completo, descricao, datalancamento, usuariosistema_id)
select id, exercicio_id, 1, '', datalancamento, usuariosistema_id  from solicitacaoitbionline;

update calculo set processocalculo_id =
    (select solicitacaoitbionline_id from calculosolicitacaoitbionline s where s.id = calculo.id)
where calculo.id in (select id from calculosolicitacaoitbionline);
