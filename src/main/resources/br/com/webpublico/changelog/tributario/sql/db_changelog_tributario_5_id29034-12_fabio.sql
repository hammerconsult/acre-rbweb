insert into CadastroIsencaoIPTU (id, PROCESSO_ID, CADASTRO_ID, ENQUADRA)
select hibernate_sequence.nextval, pro.id, isencao.CADASTROIMOBILIARIO_ID, 1 from processoisencaoiptu pro
inner join ISENCAOCADASTROIMOBILIARIO isencao on isencao.processoisencaoiptu_id = pro.id
