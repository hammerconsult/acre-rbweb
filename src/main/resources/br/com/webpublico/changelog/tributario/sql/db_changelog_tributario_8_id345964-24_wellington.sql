call proc_insere_pontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao');

call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Luxo', 2.5);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Fino', 2);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Médio', 1.25);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Simples', 1);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Econômico', 0.75);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_diario', 'ni_padrao', 'Superior', 1);

call proc_insere_pontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao');

call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Luxo', 2);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Fino', 1.5);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Médio', 1);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Simples', 0.5);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Econômico', 0.5);
call proc_insere_itempontuacao_1_atributo('ni_lixo_residencial_alternado', 'ni_padrao', 'Superior', 0.75);
